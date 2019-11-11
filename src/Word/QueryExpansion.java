package Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryExpansion {

	public static String GetHighlightedStr(String sent, Dictionary queryDict, Lemmatizer lemmatizer)
	{
		StringBuilder querysentbuilder = new StringBuilder(sent);
		List<String> termlist = Tokenizer.Tokenize(querysentbuilder, false, false);
		querysentbuilder.setLength(0);
		for(int i = 0; i < termlist.size(); i++)
		{
			String lemma = lemmatizer.Lemmatize(termlist.get(i), false).get(0);
			int index = queryDict.GetIndex(lemma);
			
			if (querysentbuilder.length() > 0)
			{
				querysentbuilder.append(" ");
			}
			
			if (index >= 0)
			{
				querysentbuilder.append(termlist.get(i).toUpperCase());
			}
			else
			{
				querysentbuilder.append(termlist.get(i));
			}
		}
		
		return querysentbuilder.toString();
	}
	
	public static String GetDocumentVectorInfo(float[] vector, Dictionary queryDict)
	{
		StringBuilder info = new StringBuilder();
		for (int i = 0; i < vector.length; i++)
		{
			if (info.length() > 0)
			{
				info.append(", ");
			}
			info.append(queryDict.GetTerm(i).str + "(" + vector[i] + ")");
		}
		
		info.insert(0, "[");
		info.append("]");
		
		return info.toString();
	}
	
	public static List<Sentence> GetQuery(String str)
	{
		List<Sentence> queries = new ArrayList<Sentence>();
		Sentence sent = new Sentence();
		sent.str = str;
		sent.id = 0;
		queries.add(sent);
		return queries;
	}
	
	public static String GetDocTerms(int docid, List<Document> docInfoList, Dictionary dict)
	{
		List<Integer> termids = docInfoList.get(docid - 1).dict_termids;
		List<Integer> termfreqs =docInfoList.get(docid - 1).dict_tfs;
		
		StringBuilder info = new StringBuilder();
		for(int i = 0; i < termids.size(); i++)
		{
			if (info.length() > 0)
			{
				info.append("; ");
			}
			
			Term term = dict.GetTerm(termids.get(i));
			info.append(term.str + " (" + termfreqs.get(i) + ")");
		}
		
		return info.toString();
	}
	
	public static List<Integer> GetDocTermPositions(Term term, int postingindex)
	{
		List<Integer> positions = term.positions2.get(postingindex);
//		int totaloccurrence = 0;
//		
//		for(int i = 0; i < term.postings.size(); i++)
//		{
//			if (i < postingindex)
//			{
//				totaloccurrence += term.tfreqs.get(i);  // since there are thousands of addition, it is rather slow
//			}
//		}
//		
//		for(int j = totaloccurrence; j < totaloccurrence + term.tfreqs.get(postingindex); j++)
//		{
//			positions.add(term.positions.get(j));
//		}
		
		
//		for(int i = 0; i < term.postings.size(); i++)
//		{
//			if (i == postingindex)
//			{
//				for(int j = totaloccurrence; j < totaloccurrence + term.tfreqs.get(i); j++)
//				{
//					positions.add(term.positions.get(j));
//				}
//			}
//			
//			totaloccurrence += term.tfreqs.get(i);
//		}
		
		return positions;
	}
	
	public static List<Integer> GetDocTermPositions(List<List<Integer>> positions, int postingindex)
	{
		List<Integer> mypositions = positions.get(postingindex);
		
		return mypositions;
	}
	
	public static List<Integer> GetDocTermPositions(List<Integer> freqs, List<Integer> positions, int postingindex)
	{
		List<Integer> mypositions = new ArrayList<Integer>();
		int totaloccurrence = 0;
		for(int i = 0; i < freqs.size(); i++)
		{
			if (i == postingindex)
			{
				for(int j = totaloccurrence; j < totaloccurrence + freqs.get(i); j++)
				{
					mypositions.add(positions.get(j));
				}
			}
			
			totaloccurrence += freqs.get(i);
		}
		
		return mypositions;
	}
	
	public static Dictionary GetMetricMatrix(Dictionary metricDict, boolean isnormalized)
	{
		List<Double> initialweights = new ArrayList<Double>();
		for(int i = 0; i < metricDict.Size(); i++)
		{
			initialweights.add(0.0);
		}
		
		for(int i = 0; i < metricDict.Size(); i++)
		{
			Term term = metricDict.GetTerm(i);
			metricDict.GetTerm(i).weights = new ArrayList<Double>();
			metricDict.GetTerm(i).weights.addAll(initialweights);
			
			for(int j = 0; j < metricDict.Size(); j++)
			{				
				if (i != j)
				{
					Term currentterm = metricDict.GetTerm(j);
					double matchscore = 0.0;
					for(int k = 0; k < term.postings.size(); k++)
					{
						for(int h = 0; h < currentterm.postings.size(); h++)
						{
							if (term.postings.get(k) == currentterm.postings.get(h))  // document ids match, two terms co-occur in the same document
							{
								List<Integer> stempositions = GetDocTermPositions(term, k);
								List<Integer> currenttermpositions = GetDocTermPositions(currentterm, h);
								matchscore += GetCorrelationMetric(stempositions, currenttermpositions); // contribution of correlation of two terms in one document
								
								if (matchscore > 0)
								{
								    //System.out.println("In document " + term.postings.get(k) + ", " +  term.str + "'s positions: " + Arrays.toString(stempositions.toArray()) + ", " + currentterm.str +  "'s positions: " + Arrays.toString(currenttermpositions.toArray()) + " Score: " + matchscore);
								}
							}
						}
					}
					
					if (isnormalized)
					{
						matchscore = matchscore / (currentterm.wordforms.size() * term.wordforms.size());
					}
					
					metricDict.GetTerm(i).weights.set(j, matchscore);
				}
			}
		}

		return metricDict;
	}
	
	public static String GetScalarTerm(String stem, Dictionary scalarMatrix)
	{
		String relatedterm = "";
		double maxscore = 0.0;
		
		int stemindex = scalarMatrix.GetIndex(stem);
		
		if (stemindex < 0) // if a given stem in a query is not matched in any returned documents
		{
			return "";
		}
		
		Term term = scalarMatrix.GetTerm(stemindex);
		
		for(int i = 0; i < scalarMatrix.Size(); i++)
		{
			Term currentterm = scalarMatrix.GetTerm(i);
			double matchscore = 0;
			double squaresum1 = 0;
			double squaresum2 = 0;
			
			if (stemindex == i)  // the associated term should not be itself
			{
				
			}
			else
			{
				for(int j = 0; j < term.weights.size(); j++)
				{
					matchscore += term.weights.get(j) * currentterm.weights.get(j);
					squaresum1 += term.weights.get(j) * term.weights.get(j);
					squaresum2 += currentterm.weights.get(j) * currentterm.weights.get(j);
				}
			}

			if (matchscore > 0)
			{
				matchscore = matchscore / (Math.sqrt(squaresum1) * Math.sqrt(squaresum2));
			    //System.out.println("The scalar score between '" + stem + "' and '" + currentterm.str + "'" + " is " + matchscore);
			}
			
			if (matchscore > maxscore)
			{
				maxscore = matchscore;
				//maxstemindex = i;
				relatedterm = currentterm.str;
			}
		}
		
		if (maxscore != 0.0)
		{
		    //System.out.println(term.str + " --> " + relatedterm + "(" + maxscore + ")");
		}
		
		return relatedterm;
	}
	
	public static Dictionary GetMetricDict(List<Document> localdoclist, List<Document> docInfoList, Dictionary dict)
	{
		Dictionary metrictermdict = new Dictionary();
		for(int i = 0; i < localdoclist.size(); i++)
		{
			int docid = localdoclist.get(i).ID;
			List<Integer> termids = docInfoList.get(docid - 1).dict_termids;
			List<Integer> termfreqs = docInfoList.get(docid - 1).dict_tfs;
			List<List<Integer>> termpositions2 = docInfoList.get(docid - 1).dict_tpositions2;
			
			for(int j = 0; j < termids.size(); j++)
			{
				Term term = dict.GetTerm(termids.get(j));
				
				List<Integer> mypositions = GetDocTermPositions(termpositions2, j);
				metrictermdict.InserTerm(term.str, termfreqs.get(j), docid, mypositions);
			}
		}
		return metrictermdict;
	}
	
	public static String GetMetricTerm(String stem, Dictionary metricDict, boolean isnormalized)
	{
		String relatedterm = "";
		double maxscore = 0.0;
		double normalizedmaxscore = 0.0;
		int maxstemindex = -1;
		
		int stemindex = metricDict.GetIndex(stem);
		
		if (stemindex < 0) // if a given stem in a query is not matched in any returned documents
		{
			return "";
		}
		
		Term term = metricDict.GetTerm(stemindex);		
		
		if (!isnormalized)
		{
			for(int i = 0; i < metricDict.Size(); i++)
			{
				Term currentterm = metricDict.GetTerm(i);
				double matchscore = 0;
				
				if (stemindex == i)  // the associated term should not be itself
				{
					
				}
				else
				{
					for(int j = 0; j < term.postings.size(); j++)
					{
						for(int k = 0; k < currentterm.postings.size(); k++)
						{
							if (term.postings.get(j) == currentterm.postings.get(k))  // document ids match, two terms co-occur in the same document
							{
								List<Integer> stempositions = GetDocTermPositions(term, j);
								List<Integer> currenttermpositions = GetDocTermPositions(currentterm, k);
								matchscore += GetCorrelationMetric(stempositions, currenttermpositions); // contribution of correlation of two terms in one document
								
//								if (matchscore > 0)
//								{
//								    //System.out.println("In document " + term.postings.get(j) + ", " +  term.str + "'s positions: " + Arrays.toString(stempositions.toArray()) + ", " + currentterm.str +  "'s positions: " + Arrays.toString(currenttermpositions.toArray()) + " Score: " + matchscore);
//								}
								
//								if (matchscore > 10)
//								{
//									String ma = "";
//								}
							}
						}
					}
				}

//				if (matchscore > 0)
//				{
//				    //System.out.println("The metric score between '" + stem + "' and '" + currentterm.str + "' in document " + " is " + matchscore);
//				}
				
				if (matchscore > maxscore)
				{
					maxscore = matchscore;
					maxstemindex = i;
					relatedterm = currentterm.wordforms.get(0);
				}
			}
		}
		else
		{
			for(int i = 0; i < metricDict.Size(); i++)
			{
				Term currentterm = metricDict.GetTerm(i);
				double matchscore = 0;
				
				if (stemindex == i)  // the associated term should not be itself
				{
					
				}
				else
				{
					for(int j = 0; j < term.postings.size(); j++)
					{
						for(int k = 0; k < currentterm.postings.size(); k++)
						{
							if (term.postings.get(j) == currentterm.postings.get(k))  // document ids match, two terms co-occur in the same document
							{
								List<Integer> stempositions = GetDocTermPositions(term, j);
								List<Integer> currenttermpositions = GetDocTermPositions(currentterm, k);
								matchscore += GetCorrelationMetric(stempositions, currenttermpositions); // contribution of correlation of two terms in one document
								
//								if (matchscore > 0)
//								{
//								    //System.out.println("In document " + term.postings.get(j) + ", " +  term.str + "'s positions: " + Arrays.toString(stempositions.toArray()) + ", " + currentterm.str +  "'s positions: " + Arrays.toString(currenttermpositions.toArray()) + " Score: " + matchscore);
//								}
								
//								if (matchscore > 10)
//								{
//									String ma = "";
//								}
							}
						}
					}
				}
				
				matchscore = matchscore / (currentterm.wordforms.size() * term.wordforms.size());

//				if (matchscore > 0)
//				{
//				    //System.out.println("The metric score between '" + stem + "' and '" + currentterm.str + "' in document " + " is " + matchscore);
//				}
				
				if (matchscore > maxscore)
				{
					maxscore = matchscore;
					maxstemindex = i;
					relatedterm = currentterm.wordforms.get(0);
				}
			}
		}
		
		if (maxscore != 0.0)
		{
		    //System.out.println(term.str + " --> " + relatedterm + "(" + maxscore + ")");
		}
		
		return relatedterm;
	}
	
	public static double GetCorrelationMetric(List<Integer> positions1, List<Integer> positions2)
	{
		double metric = 0;
		
		//theoretically, there should not be identical positions in two lists, because they represent positions of different stems.
		for(int i = 0; i < positions1.size(); i++)
		{
			int position1 = positions1.get(i);
			for(int j = 0; j < positions2.size(); j++)
			{
				metric += Math.abs(1 / (double)(position1 - positions2.get(j)));
			}
		}
		
		return metric;
	}
	
	public static Dictionary GetAssociationDict(List<Document> localdoclist, List<Document> docInfoList, Dictionary dict)
	{
		Dictionary associatedtermdict = new Dictionary();
		
		for(int i = 0; i < localdoclist.size(); i++)
		{
			int docid = localdoclist.get(i).ID;
			List<Integer> termids = docInfoList.get(docid - 1).dict_termids;
			List<Integer> termfreqs =docInfoList.get(docid - 1).dict_tfs;
			
			for(int j = 0; j < termids.size(); j++)
			{
				//Term dictterm = dict.GetTerm(termids.get(j));
				associatedtermdict.AddTermMatrix(dict.GetTerm(termids.get(j)).str, termfreqs.get(j), docid, i, localdoclist.size());
			}
		}

		return associatedtermdict;
	}
	
	// only one associated term is obtained in this function.
	// more terms can be obtained by legalizing the maximum n associated terms.
	// but since each term in a query attracts an associated term, it seems enough
	public static String GetAssociatedTerm(String stem, Dictionary associationDict, boolean isnormalized)
	{
		String relatedterm = "";
		int maxscore = 0;
		double normalizedmaxscore = 0.0;
		int maxstemindex = -1;
		
		int stemindex = associationDict.GetIndex(stem);
		
		if (stemindex < 0) // if a given stem in a query is not matched in any returned documents
		{
			return "";
		}
		
		Term term = associationDict.GetTerm(stemindex);
		List<Integer> stemtfreqs = term.tfreqs;
		
		if (!isnormalized)
		{
			for(int i = 0; i < associationDict.Size(); i++)
			{
				Term currentterm = associationDict.GetTerm(i);
				int matchscore = 0;
				for(int j = 0; j < currentterm.tfreqs.size(); j++)
				{
					matchscore += stemtfreqs.get(j) * currentterm.tfreqs.get(j);
				}

				if (matchscore > 0)
				{
				    //System.out.println("The association score between '" + stem + "' and '" + currentterm.str + "' is " + matchscore);
				}
				
				if (stemindex == i) // the associated term should not be itself
				{

				}
				else
				{
					if (matchscore > maxscore)
					{
						maxscore = matchscore;
						maxstemindex = i;
						relatedterm = currentterm.str;
					}
				}
			}
		}
		else
		{
			int selfmatchscore = 0; 
			for(int j = 0; j < term.tfreqs.size(); j++)
			{
				selfmatchscore += term.tfreqs.get(j) * term.tfreqs.get(j);
			}

			for(int i = 0; i < associationDict.Size(); i++)
			{
				Term currentterm = associationDict.GetTerm(i);
				int matchscore = 0;
				for(int j = 0; j < currentterm.tfreqs.size(); j++)
				{
					matchscore += stemtfreqs.get(j) * currentterm.tfreqs.get(j);
				}
				int othermatchscore = 0;
				for(int j = 0; j < currentterm.tfreqs.size(); j++)
				{
					othermatchscore += currentterm.tfreqs.get(j) * currentterm.tfreqs.get(j);
				}
				
				double normalizedscore = selfmatchscore + matchscore + othermatchscore;
				if (normalizedscore != 0.0)
				{
					normalizedscore = matchscore / normalizedscore;
				}

				if (normalizedscore > 0)
				{
				    //System.out.println("The normalized association score between '" + stem + "' and '" + currentterm.str + "' is " + normalizedscore);
				}
				
				if (stemindex == i) // the associated term should not be itself
				{
					
				}
				else
				{

					if (normalizedscore > normalizedmaxscore)
					{
						normalizedmaxscore = normalizedscore;
						maxstemindex = i;
						relatedterm = currentterm.str;
					}
				}
			}
		}
		
		return relatedterm;
	}
	
	public static List<String> GetExpandedTerms(Dictionary querytermsDict, Dictionary correlationDict, int type)
	{
		List<String> existingterms = new ArrayList<String>();
		for(int j = 0; j < querytermsDict.Size(); j++)
		{
			existingterms.add(querytermsDict.GetTerm(j).str);
		}
		List<String> expandedterms = new ArrayList<String>();
		
		//Dictionary associationMatrix = GetAssociationDict(relevantdocList, docInfoList, dict);
		//Dictionary.PrintDict(associatedterms);
		System.out.println(" ");
		

		for(int j = 0; j < querytermsDict.Size(); j++)
		{
			Term term = querytermsDict.GetTerm(j);
			String associatedterm = "";
			if (type == 1)
			{
				associatedterm = GetAssociatedTerm(term.str, correlationDict, true);
			}
			else if (type == 2)
			{
				associatedterm = GetMetricTerm(term.str, correlationDict, false);
			}
			else if (type == 3)
			{
				associatedterm = GetScalarTerm(term.str, correlationDict);
			}
			if (associatedterm != "")
			{
				if (!expandedterms.contains(associatedterm) && !existingterms.contains(associatedterm))
				{
					expandedterms.add(associatedterm);
				}
			    //System.out.println(term.str + " --> " + associatedterm);
			}
		}
		
		return expandedterms;
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
		//query.LoadDictionary1(folderpath);
		//query.LoadQuerySupport(folderpath2);
		query.LoadDictionaryWithPosition(folderpath3, folderpath2);
		query.LoadDocTermInfo();
		
		System.out.println("\nDictionary loaded");
		Dictionary.PrintDict2(query.dict);
		
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
			
			List<Document> sorteddoclist = Homework3.GetTopScoredDocuments(doclist, 20, false);
			//List<Document> sorteddoclist2 = Homework3.GetTopScoredDocuments(doclist, 20, true);
			
			String headline = "";
			String headlineformatted = "";
			
			for(int j = sorteddoclist.size() - 1; j >= 0; j--)
			{
				int docid = sorteddoclist.get(j).ID;
				headline = Homework3.ReadHeadLine(folderpath3 + "cranfield" + String.format("%04d", docid));
				headlineformatted = GetHighlightedStr(headline, currentqueryDict, query.lemmatizer);
				
				//System.out.println("top " + (sorteddoclist.size() - j) + " doc, docid: " + sorteddoclist.get(j).ID + ", score1: " + sorteddoclist.get(j).score);
				System.out.println("Rank: " + (sorteddoclist.size() - j) + ", Document ID: " + docid + ", Score: " + sorteddoclist.get(j).score);
				//System.out.println("Headline: " + headline);
				System.out.println("Headline Formatted: " + headlineformatted);
				//System.out.println("matched: " + GetMatchedWord(queryDict, sorteddoclist.get(j).vector, sorteddoclist.get(j).tfs, sorteddoclist.get(j).dfs).toString());
			    //System.out.println("Document Vector: " + Arrays.toString(sorteddoclist.get(j).vector).replace("[", "(").replace("]", ")"));
			    System.out.println("Document Vector: " + GetDocumentVectorInfo(sorteddoclist.get(j).vector, currentqueryDict));
				System.out.println("Document Terms: " + GetDocTerms(docid, query.docInfoList, query.dict));
			    
			    System.out.println(" ");
			}
			
			System.out.println("Query terms: " + Homework3.PrintDictTerms(currentqueryDict).toString());
			//System.out.println("Association Matrix: ");
			Dictionary associationDict = GetAssociationDict(sorteddoclist, query.docInfoList, query.dict);
            List<String> expandedterms1 = GetExpandedTerms(currentqueryDict, associationDict, 1);
            //System.out.println("Metric Matrix:");
            Dictionary metricDict = GetMetricDict(sorteddoclist, query.docInfoList, query.dict);

            //Dictionary.PrintDict2(metricDict);
            
            List<String> expandedterms2 = GetExpandedTerms(currentqueryDict, metricDict, 2);
            
            Dictionary scalarDict = GetMetricMatrix(metricDict, false);
            
            List<String> expandedterms3 = GetExpandedTerms(currentqueryDict, scalarDict, 3);
            
			System.out.println("Association Expanded Terms: " + Arrays.toString(expandedterms1.toArray()));
			System.out.println("Metric Expanded Terms: " + Arrays.toString(expandedterms2.toArray()));
			System.out.println("Scalar Expanded Terms: " + Arrays.toString(expandedterms3.toArray()));
			System.out.println(" ");
			
//			System.out.println("Top 20 documents for Query " + queries.get(i).id + " - W2:");
//			System.out.println(" ");
//			for(int j = sorteddoclist2.size() - 1; j >= 0; j--)
//			{
//				int docid = sorteddoclist2.get(j).ID;
//				headline = Homework3.ReadHeadLine(folderpath3 + "cranfield" + String.format("%04d", docid));
//				headlineformatted = GetHighlightedStr(headline, currentqueryDict, query.lemmatizer);
//				//System.out.println("top " + (sorteddoclist2.size() - j) + " doc, docid: " + sorteddoclist2.get(j).ID + ", score2: " + sorteddoclist2.get(j).score2);
//				System.out.println("Rank: " + (sorteddoclist2.size() - j) + ", Document ID: " + docid + ", Score: " + sorteddoclist2.get(j).score2);
//				//System.out.println("Headline: " + headline);
//				System.out.println("Headline Formatted: " + headlineformatted);
//				//System.out.println( "matched: " + GetMatchedWord(queryDict, sorteddoclist2.get(j).vector2, sorteddoclist2.get(j).tfs, sorteddoclist2.get(j).dfs).toString());
//				//System.out.println("Document Vector: " + Arrays.toString(sorteddoclist2.get(j).vector2).replace("[", "(").replace("]", ")"));
//				System.out.println("Document Vector: " + GetDocumentVectorInfo(sorteddoclist2.get(j).vector2, currentqueryDict));
//				System.out.println("Document Terms: " + GetDocTerms(docid, query.docInfoList, query.dict));
//				System.out.println(" ");
//			}
//			
//			System.out.println(" ");
			
			//System.out.println("Association Matrix: ");
            //List<String> expandedterms2 = GetExpandedTerms(currentqueryDict, sorteddoclist2, query.dict, query.docInfoList);

//			Dictionary associatedterms2 = GetAssociationMatrix(sorteddoclist2, query.docInfoList, query.dict);
//			//Dictionary.PrintDict(associatedterms2);
//			System.out.println(" ");
//			
//			List<String> relatedterms2 = new ArrayList<String>();
//			for(int j = 0; j < currentqueryDict.Size(); j++)
//			{
//				relatedterms2.add(currentqueryDict.GetTerm(j).str);
//			}
//			for(int j = 0; j < currentqueryDict.Size(); j++)
//			{
//				Term term = currentqueryDict.GetTerm(j);
//				String associatedterm = GetAssociatedTerm(term.str, associatedterms2, true);
//				if (associatedterm != "")
//				{
//					if (!relatedterms2.contains(associatedterm))
//					{
//						relatedterms2.add(associatedterm);
//					}
//					//System.out.println(term.str + " --> " + associatedterm);
//				}
//			}
//			System.out.println("Query terms: " + Homework3.PrintDictTerms(currentqueryDict).toString());
//			System.out.println("Expanded Terms: " + Arrays.toString(expandedterms2.toArray()));
//			System.out.println(" ");
		}
		
	}
}
