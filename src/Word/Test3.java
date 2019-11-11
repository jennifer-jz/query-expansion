package Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test3 {

	public static void main(String[] args)
	{
		String folderpath = "//home//011//j//jx//jxz161030//IR6322//homework3//";
		String folderpath2 = "//people//cs//s//sanda//cs6322//resourcesIR//";
		String folderpath3 = "//people//cs//s//sanda//cs6322//Cranfield//";
		if (args.length > 0)
		{
			folderpath = args[0];
		}
		Query query = new Query();
		query.LoadDictionary2(folderpath);
		query.LoadQuerySupport(folderpath2);
		
		List<Sentence> queries = Homework3.GetQueries(folderpath2 + "hw3.queries");
		
		System.out.println("queries:");
		System.out.println(" ");
		
		int tf;
		int maxtf;
		int doclen;
		double avgdoclen = Homework3.GetAvgDocLen(query.docInfoList);
		int collectionsize = query.docInfoList.size();
		//double logcollection = Math.log10(collectionsize);
		int df;
		double weight;
		double weight2;
		
		double veclength_query = 0;
		double veclength_query2 = 0;
		double veclength_doc = 0;
		double veclength_doc2 = 0;
		
		Dictionary queryDict = new Dictionary();
		for(int i = 0; i < 10; i++)
		{
			queryDict = new Dictionary();
			List<String> termlist = query.GetQueryTermList(queries.get(i).str);
			Dictionary.AddDictTerms(queryDict, termlist, queries.get(i).id);
			
			//for each term in a query sentence, let its frequency be the vector value in its dimension
			float[] queryvector = new float[queryDict.Size()];
			float[] queryvector2 = new float[queryDict.Size()];
			int[] dfs = new int[queryDict.Size()];
			List<Document> doclist = new ArrayList<Document>();
			doclist.addAll(query.docInfoList);
			for(int j = 0; j < doclist.size(); j++)
			{
				doclist.get(j).vector = new float[queryDict.Size()];
				doclist.get(j).vector2 = new float[queryDict.Size()];
				doclist.get(j).tfs = new int[queryDict.Size()];
				doclist.get(j).dfs = new int[queryDict.Size()];
				doclist.get(j).score = 0;
				doclist.get(j).score2 = 0;
			}
			maxtf = queryDict.GetMaxTermFreq();
			doclen = queryDict.GetDocLen();
			for(int j = 0; j < queryDict.Size(); j++)
			{
				String querytermstr = queryDict.GetTerm(j).str;
				int tfreq = queryDict.GetTerm(j).tfreq;
				queryvector[j] = 0;
				queryvector2[j] = 0;
				
				Term term = query.dict.GetTerm(querytermstr);
				
				if (term == null)
				{
					queryvector[j] = (float)Common.Round(Homework3.GetWeight(tfreq, maxtf, collectionsize, 1), 4);  // at least one document is there as the query
					queryvector2[j] = (float)Common.Round(Homework3.GetWeight2(tfreq, doclen, collectionsize, 1, avgdoclen), 4);
					dfs[j] = 1;
				}
				else
				{
					List<Integer> postings = term.postings;
					List<Integer> tfreqs = term.tfreqs;
					df = term.dfreq;
					
					queryvector[j] = (float)Common.Round(Homework3.GetWeight(tfreq, maxtf, collectionsize, df + 1), 4);  // consider a query as a document, "+ 1" to differentiate with the situation when no documents in the collection contain this term
					queryvector2[j] = (float)Common.Round(Homework3.GetWeight2(tfreq, doclen, collectionsize, df + 1, avgdoclen), 4);
					dfs[j] = df + 1;
					
					for(int k = 0; k < postings.size(); k++)
					{
						tf = tfreqs.get(k);
						maxtf = doclist.get(postings.get(k) - 1).max_tf;
						weight = Homework3.GetWeight(tf, maxtf, collectionsize, df);
						
						doclen = doclist.get(postings.get(k) - 1).doclen;
						weight2 = Homework3.GetWeight2(tf, doclen, collectionsize, df, avgdoclen);
						
						doclist.get(postings.get(k) - 1).tfs[j] = tf;
						doclist.get(postings.get(k) - 1).dfs[j] = df;
						doclist.get(postings.get(k) - 1).vector[j] = (float)Common.Round(weight, 4);
						doclist.get(postings.get(k) - 1).vector2[j] = (float)Common.Round(weight2, 4);
						doclist.get(postings.get(k) - 1).score += queryvector[j] * weight; // add unnormailized cosine similarity
						doclist.get(postings.get(k) - 1).score2 += queryvector2[j] * weight2; // add unnormailized cosine similarity
					}
				}
			}
			
			veclength_query = Homework3.GetVectorLength(queryvector);
			veclength_query2 = Homework3.GetVectorLength(queryvector2);
			
			for(int j = 0; j < doclist.size(); j++)
			{
				if (doclist.get(j).score != 0.0)
				{
					veclength_doc = Homework3.GetVectorLength(doclist.get(j).vector);
					double weightedscore = doclist.get(j).score / (veclength_query * veclength_doc);
					doclist.get(j).score = Common.Round(weightedscore, 4);
				}
				
				if (doclist.get(j).score2 != 0.0)
				{
					veclength_doc2 = Homework3.GetVectorLength(doclist.get(j).vector2);
					double weightedscore2 = doclist.get(j).score2 / (veclength_query2 * veclength_doc2);
					doclist.get(j).score2 = Common.Round(weightedscore2, 4);
				}
			}
			
			System.out.println("Query " + queries.get(i).id + ":");
			System.out.println(" ");
			System.out.println("Query string: " + queries.get(i).str);
			System.out.println("Query terms: " + Homework3.PrintDictTerms(queryDict).toString());
			//PrintDictStr(queryDict);
			System.out.println("Query terms info: term (weight, tf, df)");
			Homework3.PrintDictStr(queryDict, queryvector, dfs);
			Homework3.PrintDictStr(queryDict, queryvector2, dfs);
			System.out.println("Query vector W1: " + Arrays.toString(queryvector).replace("[", "(").replace("]", ")"));
			System.out.println("Query vector W2: " + Arrays.toString(queryvector2).replace("[", "(").replace("]", ")"));
			
			/*
			System.out.println("doc vectors:");
			for(int j = 0; j < doclist.size(); j++)
			{
				System.out.println("doc " + doclist.get(j).ID + ": " + Arrays.toString(doclist.get(j).rawvector));
				System.out.println("doc " + doclist.get(j).ID + ": " + Arrays.toString(doclist.get(j).vector));
				System.out.println("doc " + doclist.get(j).ID + ": " + Arrays.toString(doclist.get(j).vector2));
				System.out.println("doc score: " + doclist.get(j).score);
				System.out.println("doc score2: " + doclist.get(j).score2);
			}
			*/
			
			System.out.println(" ");
			System.out.println("Top 5 documents for Query " + queries.get(i).id + " - W1:");
			System.out.println(" ");
			
			List<Document> sorteddoclist = Homework3.GetTopScoredDocuments(doclist, 5, false);
			List<Document> sorteddoclist2 = Homework3.GetTopScoredDocuments(doclist, 5, true);
			
			for(int j = sorteddoclist.size() - 1; j >= 0; j--)
			{
				//System.out.println("top " + (sorteddoclist.size() - j) + " doc, docid: " + sorteddoclist.get(j).ID + ", score1: " + sorteddoclist.get(j).score);
				System.out.println("Rank: " + (sorteddoclist.size() - j) + ", Document ID: " + sorteddoclist.get(j).ID + ", Score: " + sorteddoclist.get(j).score);
				System.out.println("Headline: " + Homework3.ReadHeadLine(folderpath3 + "cranfield" + String.format("%04d", sorteddoclist.get(j).ID)));
				//System.out.println("matched: " + GetMatchedWord(queryDict, sorteddoclist.get(j).vector, sorteddoclist.get(j).tfs, sorteddoclist.get(j).dfs).toString());
			    System.out.println("Document Vector: " + Arrays.toString(sorteddoclist.get(j).vector).replace("[", "(").replace("]", ")"));
			    System.out.println(" ");
			}
			
			System.out.println("Top 5 documents for Query " + queries.get(i).id + " - W2:");
			System.out.println(" ");
			for(int j = sorteddoclist2.size() - 1; j >= 0; j--)
			{
				//System.out.println("top " + (sorteddoclist2.size() - j) + " doc, docid: " + sorteddoclist2.get(j).ID + ", score2: " + sorteddoclist2.get(j).score2);
				System.out.println("Rank: " + (sorteddoclist2.size() - j) + ", Document ID: " + sorteddoclist2.get(j).ID + ", Score: " + sorteddoclist2.get(j).score2);
				System.out.println("Headline: " + Homework3.ReadHeadLine(folderpath3 + "cranfield" + String.format("%04d", sorteddoclist2.get(j).ID)));
				//System.out.println( "matched: " + GetMatchedWord(queryDict, sorteddoclist2.get(j).vector2, sorteddoclist2.get(j).tfs, sorteddoclist2.get(j).dfs).toString());
				System.out.println("Document Vector: " + Arrays.toString(sorteddoclist2.get(j).vector2).replace("[", "(").replace("]", ")"));
				System.out.println(" ");
			}
			
			System.out.println(" ");
		}
		
		//Homework3.PrintDict(query.dict);
	}
}
