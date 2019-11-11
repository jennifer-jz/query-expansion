package Word;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryExpansion2 {

	public static String GetCorpusFileName(String corpuspath, int docid)
	{
		return (corpuspath + "cranfield" + String.format("%04d", docid));
	}
	
	public static List<String> GetTermsFromFile(String filepath, Query query)
	{
		File file = new File(filepath);
		StringBuilder originaltext = Tokenizer.ReadFileText(file);
		StringBuilder filteredtext = Tokenizer.FilterHTML(originaltext);
		List<String> rawtokens = Tokenizer.Tokenize(filteredtext, false, true);  // without stemmatization

        List<String> tokens = query.lemmatizer.Lemmatize(rawtokens, false);
		tokens = Tokenizer.FilterStopWords(tokens, query.stopwords);  // lemmatize raw tokens before filtering stop words

		return tokens;
	}
	
	public static List<Token> GetTermsFromFileWithPosition(String filepath, Query query)
	{
		File file = new File(filepath);
		StringBuilder originaltext = Tokenizer.ReadFileText(file);
		StringBuilder filteredtext = Tokenizer.FilterHTML(originaltext);
		List<Token> rawtokens = Tokenizer.TokenizeWithPosition(filteredtext, false, true);  // without stemmatization

        List<Token> tokens = query.lemmatizer.LemmatizeToken(rawtokens, false);
		tokens = Tokenizer.FilterStopWordTokens(tokens, query.stopwords);  // lemmatize raw tokens before filtering stop words

		return tokens;
	}
	
	public static List<Token> GetTermsFromFileWithPosition_Stem(String filepath, Query query)
	{
		File file = new File(filepath);
		StringBuilder originaltext = Tokenizer.ReadFileText(file);
		StringBuilder filteredtext = Tokenizer.FilterHTML(originaltext);
		List<Token> rawtokens = Tokenizer.TokenizeWithPosition(filteredtext, false, true);  // without stemmatization

        List<Token> tokens = query.lemmatizer.LemmatizeToken(rawtokens, false);
		tokens = Tokenizer.FilterStopWordTokens(tokens, query.stopwords);  // lemmatize raw tokens before filtering stop words
        tokens = Tokenizer.StemTokensWithForm(tokens);
		return tokens;
	}
	
	public static Dictionary GetAssociationDict(List<Document> localdoclist, String corpuspath, Query query)
	{
		Dictionary associatedtermdict = new Dictionary();
		Dictionary resultDict = new Dictionary();
		for(int i = 0; i < localdoclist.size(); i++)
		{
			int docid = localdoclist.get(i).ID;
			String filepath = GetCorpusFileName(corpuspath, docid);
			List<String> tokens = GetTermsFromFile(filepath, query);
			resultDict = new Dictionary();
			Dictionary.AddDictTerms(resultDict, tokens, docid);

			for(int j = 0; j < resultDict.Size(); j++)
			{
				Term dictterm = resultDict.GetTerm(j);
				
				for(int k = 0; k < dictterm.tfreqs.size(); k++)
				{
				    associatedtermdict.AddTermMatrix(dictterm.str, dictterm.tfreqs.get(k), docid, i, localdoclist.size());
				}
			}
		}

		return associatedtermdict;
	}
	
	public static Dictionary GetMetricDict(List<Document> localdoclist, String corpuspath, Query query)
	{
		Dictionary resultDict = new Dictionary();
		for(int i = 0; i < localdoclist.size(); i++)
		{
			int docid = localdoclist.get(i).ID;
			String filepath = GetCorpusFileName(corpuspath, docid);
			List<Token> tokens = GetTermsFromFileWithPosition(filepath, query);
			Dictionary.AddDictTermsWithPosition(resultDict, tokens, docid);
		}
		return resultDict;
	}
	
	public static Dictionary GetMetricDict_Stem(List<Document> localdoclist, String corpuspath, Query query)
	{
		Dictionary resultDict = new Dictionary();
		for(int i = 0; i < localdoclist.size(); i++)
		{
			int docid = localdoclist.get(i).ID;
			String filepath = GetCorpusFileName(corpuspath, docid);
			List<Token> tokens = GetTermsFromFileWithPosition_Stem(filepath, query);
			Dictionary.AddDictTermsWithPositionAndForm(resultDict, tokens, docid);
		}
		return resultDict;
	}
	
	public static void main(String[] args)
	{
		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		String folderpath1 = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework3//";
		String folderpath2 = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//resourcesIR//";
		String folderpath3 = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//Cranfield//";
		if (args.length > 0)
		{
			folderpath = args[0];
		}
		Query query = new Query();
		query.LoadDictionary1(folderpath);
		query.LoadQuerySupport(folderpath2);
		
		System.out.println("\nDictionary loaded");
		//Dictionary.PrintDict2(query.dict);
		
		List<Sentence> queries = Homework3.GetQueries(folderpath1 + "hw3.queries");
		
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
		
		Dictionary currentqueryDict = new Dictionary();
		for(int i = 0; i < queries.size(); i++)
		{
			currentqueryDict = new Dictionary();
			List<String> termlist = query.GetQueryTermList(queries.get(i).str);
			Dictionary.AddDictTerms(currentqueryDict, termlist, queries.get(i).id);
			
			//for each term in a query sentence, let its frequency be the vector value in its dimension
			float[] queryvector = new float[currentqueryDict.Size()];
			float[] queryvector2 = new float[currentqueryDict.Size()];
			int[] dfs = new int[currentqueryDict.Size()];
			List<Document> doclist = new ArrayList<Document>();
			doclist.addAll(query.docInfoList);
			for(int j = 0; j < doclist.size(); j++)
			{
				doclist.get(j).vector = new float[currentqueryDict.Size()];
				doclist.get(j).vector2 = new float[currentqueryDict.Size()];
				doclist.get(j).tfs = new int[currentqueryDict.Size()];
				doclist.get(j).dfs = new int[currentqueryDict.Size()];
				doclist.get(j).score = 0;
				doclist.get(j).score2 = 0;
			}
			maxtf = currentqueryDict.GetMaxTermFreq();
			doclen = currentqueryDict.GetDocLen();
			for(int j = 0; j < currentqueryDict.Size(); j++)
			{
				String querytermstr = currentqueryDict.GetTerm(j).str;
				int tfreq = currentqueryDict.GetTerm(j).tfreq;
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
			//System.out.println("Query terms: " + Homework3.PrintDictTerms(queryDict).toString());
			//PrintDictStr(queryDict);
			//System.out.println("Query vector W1: " + Arrays.toString(queryvector).replace("[", "(").replace("]", ")"));
			//System.out.println("Query vector W2: " + Arrays.toString(queryvector2).replace("[", "(").replace("]", ")"));
			System.out.println("Query terms info: term (weight, tf, df)");
			Homework3.PrintDictStr(currentqueryDict, queryvector, dfs);
			Homework3.PrintDictStr(currentqueryDict, queryvector2, dfs);

			System.out.println(" ");
			System.out.println("Top 20 documents for Query " + queries.get(i).id + " - W1:");
			System.out.println(" ");
			
			List<Document> sorteddoclist = Homework3.GetTopScoredDocuments(doclist, 10, false);
			//List<Document> sorteddoclist2 = Homework3.GetTopScoredDocuments(doclist, 20, true);
			
			String headline = "";
			String headlineformatted = "";
			
			for(int j = sorteddoclist.size() - 1; j >= 0; j--)
			{
				int docid = sorteddoclist.get(j).ID;
				headline = Homework3.ReadHeadLine(folderpath3 + "cranfield" + String.format("%04d", docid));
				headlineformatted = QueryExpansion.GetHighlightedStr(headline, currentqueryDict, query.lemmatizer);
				
				//System.out.println("top " + (sorteddoclist.size() - j) + " doc, docid: " + sorteddoclist.get(j).ID + ", score1: " + sorteddoclist.get(j).score);
				System.out.println("Rank: " + (sorteddoclist.size() - j) + ", Document ID: " + docid + ", Score: " + sorteddoclist.get(j).score);
				//System.out.println("Headline: " + headline);
				System.out.println("Headline Formatted: " + headlineformatted);
				//System.out.println("matched: " + GetMatchedWord(queryDict, sorteddoclist.get(j).vector, sorteddoclist.get(j).tfs, sorteddoclist.get(j).dfs).toString());
			    //System.out.println("Document Vector: " + Arrays.toString(sorteddoclist.get(j).vector).replace("[", "(").replace("]", ")"));
			    System.out.println("Document Vector: " + QueryExpansion.GetDocumentVectorInfo(sorteddoclist.get(j).vector, currentqueryDict));
				System.out.println("Document Terms: " + QueryExpansion.GetDocTerms(docid, query.docInfoList, query.dict));
			    
			    System.out.println(" ");
			}
			
			
			//System.out.println("Association Matrix: ");
			Dictionary associationDict = GetAssociationDict(sorteddoclist, folderpath3, query);
			
			//Dictionary.PrintDict2(associationDict);
			
            List<String> expandedterms1 = QueryExpansion.GetExpandedTerms(currentqueryDict, associationDict, 1);
            //System.out.println("Metric Matrix:");
            Dictionary metricDict = GetMetricDict_Stem(sorteddoclist, folderpath3, query);

            System.out.println("metric Dict\n");
            Dictionary.PrintDict2(metricDict);
            
            //Dictionary metricDict2 = GetMetricDict_Stem(sorteddoclist, folderpath3, query);
            //System.out.println("metric Dict stemmed\n");
            //Dictionary.PrintDict2(metricDict2);
            
            List<String> expandedterms2 = QueryExpansion.GetExpandedTerms(currentqueryDict, metricDict, 2);
            
            Dictionary scalarDict = QueryExpansion.GetMetricMatrix(metricDict, false);
            
            List<String> expandedterms3 = QueryExpansion.GetExpandedTerms(currentqueryDict, scalarDict, 3);
			
            System.out.println("Query terms: " + Homework3.PrintDictTerms(currentqueryDict).toString());
			System.out.println("Association Expanded Terms: " + Arrays.toString(expandedterms1.toArray()));
			System.out.println("Metric Expanded Terms: " + Arrays.toString(expandedterms2.toArray()));
			System.out.println("Scalar Expanded Terms: " + Arrays.toString(expandedterms3.toArray()));
			System.out.println(" ");
		}
	}
}
