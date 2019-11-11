package Word;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;

public class QueryExpansion4 {
    
    public static List<Integer> GetDocIDs(List<String> links, Dict_E dict_e)
    {
    	List<Integer> docids = new ArrayList<Integer>();
    	for(int i = 0; i < links.size(); i++)
    	{
    		docids.add(dict_e.GetDocIDFromLink2(links.get(i)));
    	}
    	return docids;
    } 
    
    public static List<String> GetDocTexts(List<Integer> docids, Dict_E dict_e)
    {
    	List<String> doctexts = new ArrayList<String>();
    	for(int i = 0; i < docids.size(); i++)
    	{
    		doctexts.add(ReadDocText(docids.get(i), dict_e.docfolder));
    	}
    	
    	return doctexts;
    }

	public static List<String> GetTermsFromFile_Stemming(StringBuilder text, Dict_E dict_e)
	{
		List<String> tokens = Tokenizer.Tokenize(text, false, true);  // without stemmatization
		tokens = Tokenizer.FilterStopWords(tokens, dict_e.stopwords);  // lemmatize raw tokens before filtering stop words
		tokens = Tokenizer.StemTokens(tokens);
		return tokens;
	}
	
	public static List<Token> GetTermsFromFileWithForm(StringBuilder text, Dict_E dict_e)
	{
		List<Token> tokens = Tokenizer.TokenizeWithFormAndPosition(text); 
		// This guarantees the positions of stopwords are maintained in token attribute, but stopwords are still filtered
		tokens = Tokenizer.FilterStopWordTokens(tokens, dict_e.stopwords);  // lemmatize raw tokens before filtering stop words
		return tokens;
	}
	
	public static List<Sentence> GetQueries(String filepath)
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
			if (temp.startsWith("Q") && temp.endsWith(":"))
			{
				if (tempsent.length() > 0)
				{
					query.str = tempsent.toString();
					querylist.add(query);
					tempsent.setLength(0);
				}
				
				query = new Sentence();
				String temp2 = temp.substring(1, temp.length() - 1);
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

	
	public static List<Integer> GetDocTermPositions(Term term, int postingindex)
	{
		List<Integer> positions = term.positions2.get(postingindex);		
		return positions;
	}
	
	public static String ReadDocText(int docid, String folderpath)
	{
		String subpath = Common.GetFolder(docid);
		String filepath = folderpath + subpath + docid;
		
		return new String(Common.ReadFile(filepath));
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
				relatedterm = currentterm.wordforms.get(0);
			}
		}
		
		if (maxscore != 0.0)
		{
		    //System.out.println(term.str + " --> " + relatedterm + "(" + maxscore + ")");
		}
		
		return relatedterm;
	}
	
	public static Dictionary GetMetricDict(List<String> resultlinks, List<String> doctexts, Dict_E dict_e)
	{
		List<Integer> localdoclist = GetDocIDs(resultlinks, dict_e);
		
		return GetMetricDict_ByID(localdoclist, doctexts, dict_e);
	}
	
	public static Dictionary GetMetricDict_ByID(List<Integer> localdoclist, List<String> doctexts, Dict_E dict_e)
	{
		Dictionary resultDict = new Dictionary();
		for(int i = 0; i < localdoclist.size(); i++)
		{
			int docid = localdoclist.get(i); // start from zero
			String text = doctexts.get(i);
			List<Token> tokens = GetTermsFromFileWithForm(new StringBuilder(text), dict_e);
			Dictionary.AddDictTermsWithPositionAndForm(resultDict, tokens, docid);
		}
		return resultDict;
	}
	
	public static List<String> GetMetricTerm(String stem, Dictionary metricDict, boolean isnormalized)
	{
		List<String> relatedterm = new ArrayList<String>();
		double maxscore = 0.0;
		double minscore = 0.0;  // to make possible two metric stems are included
		//double normalizedmaxscore = 0.0;
		int maxstemindex = -1;
		int minstemindex = -1;
		int tempindex= -1;
		
		int stemindex = metricDict.GetIndex(stem);
		
		if (stemindex < 0) // if a given stem in a query is not matched in any returned documents
		{
			return relatedterm;
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
				
				if (matchscore > minscore)
				{
					minscore = matchscore;
					minstemindex = i;
					if (minscore > maxscore)
					{
						matchscore = minscore;
						minscore = maxscore;
						maxscore = matchscore;
						tempindex= minstemindex;
						minstemindex = maxstemindex;
						maxstemindex =tempindex;
					}
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
								
								if (matchscore > 0)
								{
								  //  System.out.println("In document " + term.postings.get(j) + ", " +  term.str + "'s positions: " + Arrays.toString(stempositions.toArray()) + ", " + currentterm.str +  "'s positions: " + Arrays.toString(currenttermpositions.toArray()) + " Score: " + matchscore);
								}
								
//								if (matchscore > 10)
//								{
//									String ma = "";
//								}
							}
						}
					}
				}
				
				matchscore = matchscore / (currentterm.wordforms.size() * term.wordforms.size());

				if (matchscore > 0)
				{
				   // System.out.println("The metric score between '" + stem + "' and '" + currentterm.str + "' in document " + " is " + matchscore);
				}
				
				if (matchscore > minscore)
				{
					minscore = matchscore;
					minstemindex = i;
					if (minscore > maxscore)
					{
						matchscore = minscore;
						minscore = maxscore;
						maxscore = matchscore;
						tempindex= minstemindex;
						minstemindex = maxstemindex;
						maxstemindex =tempindex;
					}

				}
			}
		}
		
		if (minstemindex >= 0)
		{
			relatedterm.addAll(metricDict.GetTerm(minstemindex).wordforms);
		}
		if (maxstemindex >= 0)
		{
			relatedterm.addAll(metricDict.GetTerm(maxstemindex).wordforms);
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
	
	public static Dictionary GetAssociationDict(List<String> resultlinks, List<String> doctexts, Dict_E dict_e)
	{
		List<Integer> localdoclist = GetDocIDs(resultlinks, dict_e);
		return GetAssociationDict_ByID(localdoclist, doctexts, dict_e);
	}
	
	public static Dictionary GetAssociationDict_ByID(List<Integer> localdoclist, List<String> doctexts, Dict_E dict_e)
	{
		Dictionary associatedtermdict = new Dictionary();
		Dictionary resultDict = new Dictionary();
		for(int i = 0; i < localdoclist.size(); i++)
		{
			int docid = localdoclist.get(i);
			String text = doctexts.get(i);
			List<Token> tokens = GetTermsFromFileWithForm(new StringBuilder(text), dict_e);
			resultDict = new Dictionary();
			Dictionary.AddDictTermsWithPositionAndForm(resultDict, tokens, docid);

			for(int j = 0; j < resultDict.Size(); j++)
			{
				Term dictterm = resultDict.GetTerm(j);
				
				for(int k = 0; k < dictterm.tfreqs.size(); k++)
				{
				    associatedtermdict.AddTermMatrix(dictterm.str, dictterm.wordforms, dictterm.tfreqs.get(k), docid, i, localdoclist.size());
				}
			}
		}

		return associatedtermdict;
	}
	
	// only one associated term is obtained in this function.
	// more terms can be obtained by legalizing the maximum n associated terms.
	// but since each term in a query attracts an associated term, it seems enough
	public static List<String> GetAssociatedTerm(String stem, Dictionary associationDict, boolean isnormalized)
	{
		List<String> relatedterm = new ArrayList<String>();
		int maxscore = 0;
		double normalizedmaxscore = 0.0;
		int maxstemindex = -1;
		
		int stemindex = associationDict.GetIndex(stem);
		
		if (stemindex < 0) // if a given stem in a query is not matched in any returned documents
		{
			return relatedterm;
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
					}
				}
			}
		}
		
		if (maxstemindex >= 0)
		{
			relatedterm.addAll(associationDict.GetTerm(maxstemindex).wordforms);
		}
		
		return relatedterm;
	}
	
	public static List<String> GetExpandedTerms(Dictionary querytermsDict, Dictionary correlationDict, int type)
	{
		List<String> existingterms = new ArrayList<String>();
		for(int j = 0; j < querytermsDict.Size(); j++)
		{
			existingterms.addAll(querytermsDict.GetTerm(j).wordforms);
		}
		List<String> expandedterms = new ArrayList<String>();
		
		//Dictionary associationMatrix = GetAssociationDict(relevantdocList, docInfoList, dict);
		//Dictionary.PrintDict(associatedterms);
		//System.out.println(" ");
		

		for(int j = 0; j < querytermsDict.Size(); j++)
		{
			Term term = querytermsDict.GetTerm(j);
			List<String> associatedterm = new ArrayList<String>();
			String termstr = "";
			if (type == 1)
			{
				associatedterm.addAll(GetAssociatedTerm(term.str, correlationDict, true));
			}
			else if (type == 2)
			{
				associatedterm.addAll(GetMetricTerm(term.str, correlationDict, true));
			}
			else if (type == 3)
			{
				termstr = GetScalarTerm(term.str, correlationDict);
				if (termstr != "")
				{
					associatedterm.add(termstr);
				}
			}
			if (associatedterm.size() != 0)
			{
				for(int k = 0; k < associatedterm.size(); k++)
				{
					if (!expandedterms.contains(associatedterm.get(k)) && !existingterms.contains(associatedterm.get(k)))
					{
						expandedterms.add(associatedterm.get(k));
					}
				}

			    //System.out.println(term.str + " --> " + associatedterm);
			}
		}
		
		return expandedterms;
	}
	
	public static List<String> GetExpandedTerms(Dictionary querytermsDict, Dictionary metricDict, Dictionary associationDict)
	{
		List<String> existingterms = new ArrayList<String>();
		for(int j = 0; j < querytermsDict.Size(); j++)
		{
			existingterms.addAll(querytermsDict.GetTerm(j).wordforms);
		}
		List<String> expandedterms = new ArrayList<String>();
		
		int querytermno = querytermsDict.Size();

		for(int j = 0; j < querytermsDict.Size(); j++)
		{
			Term term = querytermsDict.GetTerm(j);
			List<String> associatedterm = new ArrayList<String>();
			associatedterm.addAll(GetMetricTerm(term.str, metricDict, true));
			
			if (associatedterm.size() != 0)
			{
				for(int k = 0; k < associatedterm.size(); k++)
				{
					if (!expandedterms.contains(associatedterm.get(k)) && !existingterms.contains(associatedterm.get(k)))
					{
						expandedterms.add(associatedterm.get(k));
					}
				}

			    //System.out.println(term.str + " --> " + associatedterm);
			}
		}
		
		for(int j = 0; j < querytermsDict.Size(); j++)
		{
			Term term = querytermsDict.GetTerm(j);
			List<String> associatedterm = new ArrayList<String>();
			associatedterm.addAll(GetAssociatedTerm(term.str, associationDict, true));
			if (associatedterm.size() != 0)
			{
				for(int k = 0; k < associatedterm.size(); k++)
				{
					if (!expandedterms.contains(associatedterm.get(k)) && !existingterms.contains(associatedterm.get(k)))
					{
						expandedterms.add(associatedterm.get(k));
					}
				}

			    //System.out.println(term.str + " --> " + associatedterm);
			}
			
			if (expandedterms.size() >= querytermno)
			{
				break;
			}
		}
		
		return expandedterms;
	}
	
	public static Dictionary GetQueryDict(String querystr, int queryid, Dict_E dict_e)
	{
		Dictionary queryDict = new Dictionary();
		List<Token> tokenlist = GetTermsFromFileWithForm(new StringBuilder(querystr), dict_e);

		Dictionary.AddDictTermsWithForm(queryDict, tokenlist, queryid);
		
		return queryDict;
	}
	
	public static List<String> GetQueryTermList(String querysent, Dict_E dict_e)
	{
		List<Token> tokenlist = GetTermsFromFileWithForm(new StringBuilder(querysent), dict_e);

		List<String> termlist = new ArrayList<String>();
		for(int i = 0; i < tokenlist.size(); i++)
		{
			if (!termlist.contains(tokenlist.get(i).wordform))
			{
			    termlist.add(tokenlist.get(i).wordform);
			}
		}

		return termlist;
	}
	
	public static List<String> GetExpandedTerms(String query, List<String> resultlinks, Dict_E dict_e)
	{
		//Dictionary currentqueryDict = GetQueryDict(query, 0, dict_e);
		Dictionary queryDict = GetQueryDict(query, 0, dict_e);
		List<Integer> localdoclist = GetDocIDs(resultlinks, dict_e);
		
		List<String> doctexts = GetDocTexts(localdoclist, dict_e);
		List<String> expandedterms = new ArrayList<String>();
		
		Dictionary associationDict = GetAssociationDict_ByID(localdoclist, doctexts, dict_e);
		Dictionary metricDict = GetMetricDict_ByID(localdoclist, doctexts, dict_e);
		
		expandedterms = GetExpandedTerms(queryDict, metricDict, associationDict);
		
		return expandedterms;
	}

	public static List<String> GetRandomLinks(Dict_E dict_e)
	{
		List<String> links = new ArrayList<String>();
		Random r = new Random();
		for(int i = 0; i < 20; i++)
		{
			int j = r.nextInt(130000);
			links.add(dict_e.links.get(j));
		}
		
		return links;
	}
	
	public static void main(String[] args)
	{
		Dict_E dict_e = new Dict_E();
		dict_e.LoadQuerySupport();
		
		System.out.println("\nDictionary loaded");
		//Dictionary.PrintDict2(query.dict);
		
		//Sentence is a class built to record the id of a query string.
		List<Sentence> queries = GetQueries(dict_e.resourcepath + "hw3.queries");
		
		System.out.println("queries:");
		System.out.println(" ");
		
		for(int i = 0; i < queries.size(); i++)
		{
			System.out.println("Query " + queries.get(i).id + ":");
			System.out.println(" ");
			System.out.println("Query string: " + queries.get(i).str);

			//Random link results are selected for test
			List<String> links = GetRandomLinks(dict_e);
			//The corresponding document ids of the links.
			List<Integer> docids = GetDocIDs(links, dict_e);
			
			System.out.println(Arrays.toString(links.toArray()));
			System.out.println(Arrays.toString(docids.toArray()));
			
			//The major function to call for expanded terms
			//The expanded terms are combined results of metric cluster and association cluster, the former being more accurate but less.
			//A maximum number of expanded terms equaling that of the original query is set.
			List<String> expandedterms = GetExpandedTerms(queries.get(i).str, links, dict_e);
			
            System.out.println();
            System.out.println("Query terms: " + Arrays.toString(GetQueryTermList(queries.get(i).str, dict_e).toArray()));
			System.out.println("Expanded Terms: " + Arrays.toString(expandedterms.toArray()));
			//System.out.println("Metric Expanded Terms: " + Arrays.toString(expandedterms2.toArray()));
			//System.out.println("Scalar Expanded Terms: " + Arrays.toString(expandedterms3.toArray()));
			System.out.println();

		}
		
	}
}
