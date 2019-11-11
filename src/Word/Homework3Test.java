package Word;

import java.util.*;

import Word.Dictionary;

public class Homework3Test {

	public static List<Sentence> GetQueries2(String filepath)
	{
		List<String> lines = Common.ReadFile_Unix(filepath);
		String temp = "";
		StringBuilder tempsent = new StringBuilder();
		List<Sentence> querylist = new ArrayList<Sentence>();
		Sentence query = new Sentence();
		for(int i = 0; i < lines.size(); i++)
		{
			temp = lines.get(i);
			if (temp.startsWith(".I"))
			{
				if (tempsent.length() > 0)
				{
					query.str = tempsent.toString();
					querylist.add(query);
					tempsent.setLength(0);
				}
				
				query = new Sentence();
				String temp2 = temp.substring(3, temp.length());
				query.id = Integer.parseInt(temp2);
			}
			else if (temp.startsWith(".W"))
			{

			}
			else
			{
				if (tempsent.length() > 0)
				{
					tempsent.append(" ");
				}
				tempsent.append(temp);
			}
		}
		
		if (tempsent.length() > 0)
		{
			query.str = tempsent.toString();
			querylist.add(query);
			tempsent.setLength(0);
		}
		
		return querylist;
	}
	
	public static double GetAvgDocLen(List<Document> docList)
	{
		int totaldoclen = 0;
		for(int i = 0; i < docList.size(); i++)
		{
			totaldoclen += docList.get(i).doclen;
		}
		
		return (double)totaldoclen / (double)docList.size();
	}
	
	public static double GetVectorLength(int[] vector)
	{
		double squaresum = 0;
		for(int i = 0; i < vector.length; i++)
		{
			squaresum += vector[i] * vector[i];
		}
		
		return Math.sqrt(squaresum);
	}
	
	public static double GetVectorLength(float[] vector)
	{
		double squaresum = 0;
		for(int i = 0; i < vector.length; i++)
		{
			squaresum += vector[i] * vector[i];
		}
		
		return Math.sqrt(squaresum);
	}
	
	public static double GetWeight(int tf, int maxtf, int collectionsize, int df)
	{
		double weight = 0;
		double logcollection = Math.log10(collectionsize);
		weight = Math.log10(tf + 0.5) / Math.log10(maxtf + 1.0);
		weight = 0.4 + 0.6 * weight;
		weight = weight * (logcollection - Math.log10(df)) / logcollection;
		
		return weight;
	}
	
	public static double GetWeight2(int tf, int doclen, int collectionsize, int df, double avgdoclen)
	{
		double weight2 = 0;
		double logcollection = Math.log10(collectionsize);
		weight2 = doclen/avgdoclen;
		weight2 = 1.5 * weight2 + 0.5 + tf;
		weight2 = tf/weight2;
		weight2 = 0.6 * weight2;
		weight2 = 0.4 + weight2 * (logcollection - Math.log10(df)) / logcollection;
		
		return weight2;
	}
	
	public static double GetWeight3(int tf, int doclen, int collectionsize, int df, double avgdoclen)
	{
		double weight2 = 0;
		double logcollection = Math.log10(collectionsize);
		weight2 = doclen/avgdoclen;
		weight2 = 1.5 * weight2 + 0.5 + tf;
		weight2 = tf/weight2;
		weight2 = 0.4 + 0.6 * weight2;
		weight2 = weight2 * (logcollection - Math.log10(df)) / logcollection;
		
		return weight2;
	}
	
	public static List<Document> GetTopScoredDocuments(List<Document> doclist, int topno, boolean isscore2)
	{
		if (topno <= 0)
		{
			topno = 10;
		}
		
		List<Document> topdocs = new ArrayList<Document>();
		double minscore = 0.0;
		Document thisdoc = null;
		for (int i = 0; i < doclist.size(); i++)
		{
			thisdoc = doclist.get(i);
			double currentscore = thisdoc.score;
			if (isscore2)
			{
				currentscore = thisdoc.score2;
			}
			if (i < topno)
			{
				if (minscore == 0.0)
				{
					minscore = currentscore;
					topdocs.add(thisdoc);
				}
				else
				{
					if (currentscore <= minscore)
					{
						minscore = currentscore;
						topdocs.add(0, thisdoc);  // add the document with lowest score at the front of list, waiting for being popped out
					}
					else
					{
						// To keep topdocs sorted incrementally by score
						int foundj = topdocs.size();
						for(int j = 0; j < topdocs.size(); j++)
						{
							double topscore = topdocs.get(j).score;
							if (isscore2)
							{
								topscore = topdocs.get(j).score2;
							}
						    if (topscore >= currentscore)
						    {
						    	foundj = j;
						    	break;
						    }
						}
						topdocs.add(foundj, thisdoc);
					}
				}
			}
			else
			{
				if (currentscore > minscore)
				{
					topdocs.remove(0);
					
					// To keep topdocs sorted incrementally by score
					int foundj = topdocs.size();
					for(int j = 0; j < topdocs.size(); j++)
					{
						double topscore = topdocs.get(j).score;
						if (isscore2)
						{
							topscore = topdocs.get(j).score2;
						}
					    if (topscore >= currentscore)
					    {
					    	foundj = j;
					    	break;
					    }
					}
					topdocs.add(foundj, thisdoc);
					
					minscore = topdocs.get(0).score;
					if (isscore2)
					{
						minscore = topdocs.get(0).score2;
					}
				}
			}
		}
		
		return topdocs;	

	}
	
    public static void PrintDict(Dictionary myDict)
    {
		for(int i= 0; i < myDict.Size(); i++)
		{
			Term term = myDict.GetTerm(i);
			
			System.out.println(term.str +  "; " + term.tfreq+ "; " + term.dfreq  + "; *" + Arrays.toString(term.postings.toArray())+ "; *" +  Arrays.toString(term.tfreqs.toArray()));
			//totalterms+= term.tfreq;
		}
		
		System.out.println(" ");
    }
    
    public static StringBuilder PrintDictTerms(Dictionary myDict)
    {
    	StringBuilder printcontent = new StringBuilder();
    	printcontent.append("(");
		for(int i= 0; i < myDict.Size(); i++)
		{
			Term term = myDict.GetTerm(i);
			if (printcontent.length() > 1)
			{
				printcontent.append(", ");
			}
			printcontent.append(term.str);
		}
		printcontent.append(")");
		
		return printcontent;
    }
    
    public static void PrintDictStr(Dictionary myDict)
    {
    	StringBuilder printcontent = new StringBuilder();
		for(int i= 0; i < myDict.Size(); i++)
		{
			Term term = myDict.GetTerm(i);
			if (printcontent.length() > 0)
			{
				printcontent.append(", ");
			}
			printcontent.append(term.str);
			if (term.tfreq > 1)
			{
				printcontent.append(" (" + term.tfreq + ")");
			}
			
		}
		
		System.out.println(printcontent.toString());
    }
    
    public static void PrintDictStr(Dictionary myDict, float[] queryvector, int[] dfs)
    {
    	StringBuilder printcontent = new StringBuilder();
		for(int i= 0; i < myDict.Size(); i++)
		{
			Term term = myDict.GetTerm(i);
			if (printcontent.length() > 0)
			{
				printcontent.append("; ");
			}
			printcontent.append(term.str);
			printcontent.append(" (" + queryvector[i] + ", " + term.tfreq + ", " + dfs[i] + ")");
		}
		
		System.out.println(printcontent.toString());
    }
    
    public static StringBuilder GetMatchedWord(Dictionary queryDict, float[] weights, int[] tfs, int[] dfs)
    {
    	StringBuilder content = new StringBuilder();
    	for(int i = 0; i < weights.length; i++)
    	{
    		if (weights[i] != 0.0)
    		{
    			if (content.length() > 0)
    			{
    				content.append("; ");
    			}
    			Term term = queryDict.GetTerm(i);
    			content.append(term.str + " (" + weights[i] + ", " + tfs[i] + ", " + dfs[i] + ")");
    		}
    	}
    	
    	return content;
    }
    
    public static String ReadHeadLine(String filepath)
    {
    	List<String> lines = Common.ReadFileLine(filepath, true);
    	List<String> headlines = new ArrayList<String>();
    	boolean isstart = false;
    	for(int i = 0; i < lines.size(); i++)
    	{
    		String line = lines.get(i);
    		if (line.equals("</TITLE>"))
    		{
    			isstart = false;
    			break;
    		}
    		
    		if (isstart)
    		{
    			headlines.add(line);
    		}
    		
    		if (line.equals("<TITLE>"))  // "equals" compare the value equality, while "==" compares the reference equality
    		{
    			isstart = true;
    		}
    	}
    	
    	StringBuilder headline = new StringBuilder();
    	for(int i = 0; i < headlines.size(); i++)
    	{
    		headline.append(headlines.get(i));
    		headline.append(" ");
    	}
    	
    	return headline.toString().trim();
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
		
		List<Sentence> queries = Homework3.GetQueries(folderpath1 + "hw3.queries");
		
		System.out.println("queries:");
		System.out.println(" ");
		
		int tf;
		int maxtf;
		int doclen;
		double avgdoclen = GetAvgDocLen(query.docInfoList);
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
		for(int i = 0; i < queries.size(); i++)
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
					queryvector[j] = (float)Common.Round(GetWeight(tfreq, maxtf, collectionsize, 1), 4);  // at least one document is there as the query
					queryvector2[j] = (float)Common.Round(GetWeight2(tfreq, doclen, collectionsize, 1, avgdoclen), 4);
					dfs[j] = 1;
				}
				else
				{
					List<Integer> postings = term.postings;
					List<Integer> tfreqs = term.tfreqs;
					df = term.dfreq;
					
					queryvector[j] = (float)Common.Round(GetWeight(tfreq, maxtf, collectionsize, df + 1), 4);  // consider a query as a document, "+ 1" to differentiate with the situation when no documents in the collection contain this term
					queryvector2[j] = (float)Common.Round(GetWeight2(tfreq, doclen, collectionsize, df + 1, avgdoclen), 4);
					dfs[j] = df + 1;
					
					for(int k = 0; k < postings.size(); k++)
					{
						tf = tfreqs.get(k);
						maxtf = doclist.get(postings.get(k) - 1).max_tf;
						weight = GetWeight(tf, maxtf, collectionsize, df);
						
						doclen = doclist.get(postings.get(k) - 1).doclen;
						weight2 = GetWeight2(tf, doclen, collectionsize, df, avgdoclen);
						
						doclist.get(postings.get(k) - 1).tfs[j] = tf;
						doclist.get(postings.get(k) - 1).dfs[j] = df;
						doclist.get(postings.get(k) - 1).vector[j] = (float)Common.Round(weight, 4);
						doclist.get(postings.get(k) - 1).vector2[j] = (float)Common.Round(weight2, 4);
						doclist.get(postings.get(k) - 1).score += queryvector[j] * weight; // add unnormailized cosine similarity
						doclist.get(postings.get(k) - 1).score2 += queryvector2[j] * weight2; // add unnormailized cosine similarity
					}
				}
			}
			
			veclength_query = GetVectorLength(queryvector);
			veclength_query2 = GetVectorLength(queryvector2);
			
			for(int j = 0; j < doclist.size(); j++)
			{
				if (doclist.get(j).score != 0.0)
				{
					veclength_doc = GetVectorLength(doclist.get(j).vector);
					double weightedscore = doclist.get(j).score / (veclength_query * veclength_doc);
					doclist.get(j).score = Common.Round(weightedscore, 4);
				}
				
				if (doclist.get(j).score2 != 0.0)
				{
					veclength_doc2 = GetVectorLength(doclist.get(j).vector2);
					double weightedscore2 = doclist.get(j).score2 / (veclength_query2 * veclength_doc2);
					doclist.get(j).score2 = Common.Round(weightedscore2, 4);
				}
			}
			
			System.out.println("Query " + queries.get(i).id + ":");
			System.out.println(" ");
			System.out.println("Query string: " + queries.get(i).str);
			System.out.println("Query terms: " + PrintDictTerms(queryDict).toString());
			//PrintDictStr(queryDict);
			System.out.println("Query terms info: term (weight, tf, df)");
			PrintDictStr(queryDict, queryvector, dfs);
			PrintDictStr(queryDict, queryvector2, dfs);
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
			
			List<Document> sorteddoclist = GetTopScoredDocuments(doclist, 5, false);
			List<Document> sorteddoclist2 = GetTopScoredDocuments(doclist, 5, true);
			
			for(int j = sorteddoclist.size() - 1; j >= 0; j--)
			{
				//System.out.println("top " + (sorteddoclist.size() - j) + " doc, docid: " + sorteddoclist.get(j).ID + ", score1: " + sorteddoclist.get(j).score);
				System.out.println("Rank: " + (sorteddoclist.size() - j) + ", Document ID: " + sorteddoclist.get(j).ID + ", Score: " + sorteddoclist.get(j).score);
				System.out.println("Headline: " + ReadHeadLine(folderpath3 + "cranfield" + String.format("%04d", sorteddoclist.get(j).ID)));
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
				System.out.println("Headline: " + ReadHeadLine(folderpath3 + "cranfield" + String.format("%04d", sorteddoclist2.get(j).ID)));
				//System.out.println( "matched: " + GetMatchedWord(queryDict, sorteddoclist2.get(j).vector2, sorteddoclist2.get(j).tfs, sorteddoclist2.get(j).dfs).toString());
				System.out.println("Document Vector: " + Arrays.toString(sorteddoclist2.get(j).vector2).replace("[", "(").replace("]", ")"));
				System.out.println(" ");
			}
			
			System.out.println(" ");
		}
		
		//PrintDict(query.dict);
		
	}
}
