package QueryE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QueryExpansion {
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
							if (term.postings.get(k).equals(currentterm.postings.get(h)))  // document ids match, two terms co-occur in the same document
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
		    System.out.println(term.str + " --> (scalar)" + relatedterm + "(" + maxscore + ")");
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
	
	
	
	public static List<String> GetMetricTerm(String stem, Dictionary querytermsDict, Dictionary metricDict, boolean isnormalized)
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
				if (currentterm.str.length() == 1 || querytermsDict.GetIndex(currentterm.str) >= 0)
				{
					continue;
				}
				
				double matchscore = 0;
				
				if (stemindex == i)  // the associated term should not be itself
				{
					
				}
				else
				{
					//System.out.println(term.str + " " + Arrays.toString(term.postings.toArray()));
					//System.out.println(currentterm.str + " " + Arrays.toString(currentterm.postings.toArray()));
					for(int j = 0; j < term.postings.size(); j++)
					{						
						int postingj = term.postings.get(j);
						for(int k = 0; k < currentterm.postings.size(); k++)
						{
							int postingk = currentterm.postings.get(k);
							// so strange if using  term.postings.get(j) == currentterm.postings.get(k), there will be problem
							if (postingj == postingk)  // document ids match, two terms co-occur in the same document
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
		else
		{
			for(int i = 0; i < metricDict.Size(); i++)
			{
				Term currentterm = metricDict.GetTerm(i);
				
				if (currentterm.str.length() == 1 || querytermsDict.GetIndex(currentterm.str) >= 0)
				{
					continue;
				}
				
				double matchscore = 0;
				
				if (stemindex == i)  // the associated term should not be itself
				{
					
				}
				else
				{
					for(int j = 0; j < term.postings.size(); j++)
					{
						int postingj = term.postings.get(j);
						for(int k = 0; k < currentterm.postings.size(); k++)
						{
							int postingk = currentterm.postings.get(k);
							if (postingj == postingk)  // document ids match, two terms co-occur in the same document
							{
								List<Integer> stempositions = GetDocTermPositions(term, j);
								List<Integer> currenttermpositions = GetDocTermPositions(currentterm, k);
								matchscore += GetCorrelationMetric(stempositions, currenttermpositions); // contribution of correlation of two terms in one document
								
								if (matchscore > 0)
								{
								    //System.out.println("In document " + term.postings.get(j) + ", " +  term.str + "'s positions: " + Arrays.toString(stempositions.toArray()) + ", " + currentterm.str +  "'s positions: " + Arrays.toString(currenttermpositions.toArray()) + " Score: " + matchscore);
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
				    //System.out.println("The metric score between '" + stem + "' and '" + currentterm.str + "' in document " + " is " + matchscore);
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
			//relatedterm.addAll(metricDict.GetTerm(minstemindex).wordforms);
			relatedterm.add(metricDict.GetTerm(minstemindex).wordforms.get(0));
		}
		if (maxstemindex >= 0)
		{
			//relatedterm.addAll(metricDict.GetTerm(maxstemindex).wordforms);
			relatedterm.add(metricDict.GetTerm(maxstemindex).wordforms.get(0));
		}
		
		if (maxscore != 0.0)
		{
		    System.out.println(term.str + " --> (metric)" + metricDict.GetTerm(maxstemindex).wordforms.get(0) + "(" + maxscore + ")");
		}
		if (minscore != 0.0)
		{
			System.out.println(term.str + " --> (metric)" + metricDict.GetTerm(minstemindex).wordforms.get(0) + "(" + minscore + ")");
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
	public static List<String> GetAssociatedTerm(String stem, Dictionary querytermsDict, Dictionary associationDict, boolean isnormalized)
	{
		List<String> relatedterm = new ArrayList<String>();
		
		double maxscore = 0.0;
		double minscore = 0.0;  // to make possible two metric stems are included
		//double normalizedmaxscore = 0.0;
		int maxstemindex = -1;
		int minstemindex = -1;
		int tempindex= -1;
		
//		int maxscore = 0;
//		double normalizedmaxscore = 0.0;
//		int maxstemindex = -1;
		
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
				if (currentterm.str.length() == 1 || querytermsDict.GetIndex(currentterm.str) >= 0)
				{
					continue;
				}
				
				double matchscore = 0;
				for(int j = 0; j < currentterm.tfreqs.size(); j++)
				{
					matchscore += stemtfreqs.get(j) * currentterm.tfreqs.get(j);
				}

				if (matchscore > 0)
				{
				    System.out.println("The unnormalized association score between '" + stem + "' and '" + currentterm.str + "' " + Arrays.toString(currentterm.wordforms.toArray()) + " is " + matchscore);
				}
				
				if (stemindex == i) // the associated term should not be itself
				{

				}
				else
				{
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
							maxstemindex = tempindex;
						}
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
				if (currentterm.str.length() == 1 || querytermsDict.GetIndex(currentterm.str) >= 0)
				{
					continue;
				}
				
				double matchscore = 0;
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
				    System.out.println("The normalized association score between '" + stem + "' and '" + currentterm.str + "' " + Arrays.toString(currentterm.wordforms.toArray()) + " is " + normalizedscore);
				}
				
				if (stemindex == i) // the associated term should not be itself
				{
					
				}
				else
				{

					if (normalizedscore > minscore)
					{
						minscore = normalizedscore;
						minstemindex = i;
						if (minscore > maxscore)
						{
							normalizedscore = minscore;
							minscore = maxscore;
							maxscore = normalizedscore;
							tempindex= minstemindex;
							minstemindex = maxstemindex;
							maxstemindex =tempindex;
						}

					}
				}
			}
		}
		
		if (maxstemindex >= 0)
		{
			System.out.println(term.str + " -->(association) " + associationDict.GetTerm(maxstemindex).wordforms.get(0) + "(" + maxscore + ")");
			relatedterm.addAll(associationDict.GetTerm(maxstemindex).wordforms);
			//relatedterm.add(associationDict.GetTerm(maxstemindex).wordforms.get(0));
		}
		
		if (minstemindex >= 0)
		{
			System.out.println(term.str + " -->(association) " + associationDict.GetTerm(minstemindex).wordforms.get(0) + "(" + minscore + ")");
			relatedterm.addAll(associationDict.GetTerm(minstemindex).wordforms);
			//relatedterm.add(associationDict.GetTerm(minstemindex).wordforms.get(0));
		}
		
		return relatedterm;
	}
	
	public static List<String> GetExpandedTerms(Dictionary querytermsDict, Dictionary correlationDict, int type, boolean isnormalized)
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
				associatedterm.addAll(GetAssociatedTerm(term.str, querytermsDict, correlationDict, isnormalized));
			}
			else if (type == 2)
			{
				associatedterm.addAll(GetMetricTerm(term.str, querytermsDict, correlationDict, isnormalized));
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
		
		//int querytermno = querytermsDict.Size();

		for(int j = 0; j < querytermsDict.Size(); j++)
		{
			Term term = querytermsDict.GetTerm(j);
			List<String> associatedterm = new ArrayList<String>();
			associatedterm.addAll(GetMetricTerm(term.str, querytermsDict, metricDict, true));
			
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
			associatedterm.addAll(GetAssociatedTerm(term.str, querytermsDict, associationDict, true));
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
			
//			if (expandedterms.size() >= querytermno)
//			{
//				break;
//			}
		}
		
		Dictionary scalarDict = GetMetricMatrix(metricDict, true);
		for(int j = 0; j < querytermsDict.Size(); j++)
		{
			Term term = querytermsDict.GetTerm(j);
			String relatedterm = GetScalarTerm(term.str, scalarDict);
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
	
		
//		for(int i = 0; i < metricDict.Size(); i++)
//		{
//			System.out.println(metricDict.GetTerm(i).wordforms.get(0));
//		}
//		
//		System.out.println();
//		
//		for(int i = 0; i < metricDict.Size(); i++)
//		{
//			System.out.println(metricDict.GetTerm(i).str);
//		}
//		
//		System.out.println();
		
//		Dictionary.PrintDict2(queryDict);
//		System.out.println();
//		System.out.println();
//		Dictionary.PrintDict2(associationDict);
//		System.out.println();
//		System.out.println();
//		Dictionary.PrintDict2(metricDict);
		
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
	
	public static List<Sentence> GetQueryTest()
	{
		List<Sentence> queries = new ArrayList<Sentence>();
		Sentence sent = new Sentence();
		//sent.str = "serious questions";
		//sent.str = "divide and conquer";
		sent.str = "Discrete Structure";
		//sent.str = "Computer Algorithm";
		//sent.str = "main page";
		//sent.str = "background";
		//sent.str = "study";
		sent.id = 0;
		queries.add(sent);
		
		return queries;
	}
	
	public static List<String> GetLinksTest2(Dict_E dict_e)
	{
		List<String> testlinks = new ArrayList<String>();
		
		String[] docstrs = "72,142,320,347,457,499,596,602,603,604,608,619,628,810,949,1110,1144,1254,1259,1275,1366".split(",");
		
		for(int i = 0; i < docstrs.length; i++)
		{
			String link = dict_e.links.get(Integer.valueOf(docstrs[i]) - 1);
			testlinks.add(link);
		}
		
		return testlinks;
	}
	
	public static List<String> GetLinksTest()
	{
		List<String> testlinks = new ArrayList<String>();
//		testlinks.add("http://05command.wikidot.com/alexandra-glossary");
//		testlinks.add("http://05command.wikidot.com/ambassador-main");
//		testlinks.add("http://05command.wikidot.com/archived-pages");
//		testlinks.add("http://05command.wikidot.com/beautilog");
//		testlinks.add("http://05command.wikidot.com/chat");
//		testlinks.add("http://05command.wikidot.com/chatop-guide");
//		testlinks.add("http://05command.wikidot.com/community-outreach-main");
//		testlinks.add("http://05command.wikidot.com/contests");
//		testlinks.add("http://05command.wikidot.com/deletions-guide");
//		testlinks.add("http://05command.wikidot.com/duty-statement-letter");
//		testlinks.add("http://05command.wikidot.com/form-letters");
//		testlinks.add("http://05command.wikidot.com/forum-crit-main");
//		testlinks.add("http://05command.wikidot.com/forum/c-798754/non-disciplinary-record-keeping");
//		testlinks.add("http://05command.wikidot.com/forum/start");
//		testlinks.add("http://05command.wikidot.com/forum/t-1056456/scantron");
//		testlinks.add("http://05command.wikidot.com/forum/t-1686551/nicholastan");
//		testlinks.add("http://05command.wikidot.com/forum/t-2158991/disciplinary-morphogenetic-field");
//		testlinks.add("http://05command.wikidot.com/forum/t-2165916/tag-proposal:reality-bending");
//		testlinks.add("http://05command.wikidot.com/forum/t-2200117/canon-creator-autonomy");
//		testlinks.add("http://05command.wikidot.com/forum/t-2203314/navigation-update-new-pages-proposal");
		
//		testlinks.add("http://stackoverflow.com/questions/tagged/divide-and-conquer");
//		testlinks.add("http://stackoverflow.com/questions/43032425/algorithms-find-recursive-equation-of-divide-and-conquer-algorithm");
//		testlinks.add("http://stackoverflow.com/questions/21541549/line-segment-intersection-algorithm-using-divide-and-conquer");
//		testlinks.add("http://www.geeksforgeeks.org/divide-and-conquer-set-1-find-closest-pair-of-points/");
//		testlinks.add("https://www.tutorialspoint.com/data_structures_algorithms/divide_and_conquer.htm");
//		testlinks.add("http://www.geeksforgeeks.org/category/algorithm/divide-and-conquer/");
//		testlinks.add("http://stackoverflow.com/tags/divide-and-conquer/info");
//		testlinks.add("http://stackoverflow.com/tags/divide-and-conquer/hot");
//		testlinks.add("http://stackoverflow.com/questions/24554549/divide-and-conquer-algorithm-find-the-minimum-of-a-matrix");
//		testlinks.add("http://stackoverflow.com/questions/8850447/why-is-binary-search-a-divide-and-conquer-algorithm");
//		testlinks.add("http://stackoverflow.com/questions/8850447/why-is-binary-search-a-divide-and-conquer-algorithm/8850561");
//		testlinks.add("http://stackoverflow.com/questions/8850447/why-is-binary-search-a-divide-and-conquer-algorithm/8851907");
//		testlinks.add("http://stackoverflow.com/questions/42475395/finding-the-local-maxima-in-a-2d-array-of-nxn");
//		testlinks.add("https://en.wikipedia.org/wiki/Divide_and_conquer_(algorithm)");
//		testlinks.add("http://stackoverflow.com/tags/divide-and-conquer/topusers");
//		testlinks.add("https://en.wikipedia.org/wiki/Divide_and_conquer_algorithm");
//		testlinks.add("http://www.ics.uci.edu/~goodrich/teach/cs161/notes/");
//		testlinks.add("http://www.ics.uci.edu/~goodrich/teach/cs263/notes/");
//		testlinks.add("http://stackoverflow.com/tags/divide-and-conquer/synonyms");
//		testlinks.add("https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-046j-design-and-analysis-of-algorithms-spring-2015/lecture-notes/");
		
		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Discrete_space");
		testlinks.add("http://www.allisons.org/ll/MML/Structured/");
		testlinks.add("https://en.wikipedia.org/wiki/Discrete_space");
		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Discrete_subgroup");
		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Discrete_group");
		testlinks.add("https://en.wikipedia.org/wiki/Discrete");
		testlinks.add("https://en.wikipedia.org/wiki/Discrete_transform");
		testlinks.add("http://www.siam.org/activity/dm/");
		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Structure");
		testlinks.add("https://en.wikipedia.org/wiki/Discrete_mathematics");
		testlinks.add("http://www.allisons.org/ll/MML/Discrete/");
		testlinks.add("https://en.wikipedia.org/wiki/Discrete_optimization");
		testlinks.add("https://www.wikidata.org/wiki/Q7390256");
		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Discrete_logarithm");
		testlinks.add("https://en.wikibooks.org/wiki/Discrete_Mathematics");
		testlinks.add("http://www.exampleproblems.com/wiki/index.php/List_of_transforms");
		testlinks.add("https://en.wikipedia.org/wiki/Structure_(mathematics)");
		testlinks.add("https://en.wikipedia.org/wiki/Lattice_(discrete_subgroup)");
		testlinks.add("http://www.siam.org/meetings/da02/");
		testlinks.add("https://en.wikipedia.org/wiki/Fourier-related_transforms");
		
//		testlinks.add("https://www.wikidata.org/wiki/Q8366");
//		testlinks.add("https://en.wikipedia.org/wiki/Knuth%27s_Simpath_algorithm");
//		testlinks.add("http://quiz.geeksforgeeks.org/gate-gate-cs-2013-question-10/");
//		testlinks.add("https://en.wikipedia.org/wiki/Concurrent_algorithm");
//		testlinks.add("https://en.wikipedia.org/wiki/Template:Donald_Knuth_navbox");
//		testlinks.add("https://en.wikipedia.org/wiki/Category:Distributed_algorithms");
//		testlinks.add("http://computer.howstuffworks.com/question717.htm");
//		testlinks.add("https://en.wikipedia.org/wiki/Sequential_algorithm");
//		testlinks.add("https://en.wikipedia.org/wiki/Category:Unassessed_Computer_science_articles");
//		testlinks.add("https://en.wikipedia.org/wiki/Category:Mid-importance_Computer_science_articles");
//		testlinks.add("https://en.wikipedia.org/wiki/Template:Edsger_Dijkstra");
//		testlinks.add("https://en.wikipedia.org/wiki/Algorithm_engineering");
//		testlinks.add("https://en.wikipedia.org/wiki/Category:Articles_with_example_C_code");
//		testlinks.add("https://en.wikipedia.org/wiki/Category:Algorithms");
//		testlinks.add("https://en.wikipedia.org/wiki/List_of_algorithms");
//		testlinks.add("http://www.ics.uci.edu/~eppstein/161/people.html");
//		testlinks.add("https://en.wikipedia.org/wiki/Talk:K_shortest_path_routing");
//		testlinks.add("https://en.wikipedia.org/wiki/Category:Articles_with_example_pseudocode");
//		testlinks.add("https://en.wikipedia.org/wiki/Category:Computer_arithmetic_algorithms");
//		testlinks.add("http://www.interactivepython.org/runestone/static/thinkcspy/GeneralIntro/Algorithms.html");
		
//		testlinks.add("https://en.wikibooks.org/wiki/Category:Main_page");
//		testlinks.add("http://opende.sourceforge.net/docs/");
//		testlinks.add("http://www.geolib.co.uk/doxygen/index.html");
//		testlinks.add("https://www.wikidata.org/wiki/Property:P301");
//		testlinks.add("http://atlantica-online.wikidot.com/forum/t-2067114/start");
//		testlinks.add("https://en.wikipedia.org/wiki/Talk:Main_Page");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Main_diagonal");
//		testlinks.add("http://inter-irc.wikidot.com/nav:side");
//		testlinks.add("https://www.wikidata.org/wiki/Property:P1423");
//		testlinks.add("https://en.wikibooks.org/wiki/Help:Development_stages");
//		testlinks.add("https://doc.wikimedia.org/mediawiki-core/master/php/");
//		testlinks.add("https://doc.wikimedia.org/mediawiki-core/master/php/index.html");
//		testlinks.add("http://www.mridulkhan.com/");
//		testlinks.add("http://inter-irc.wikidot.com/system:page-tags-list");
//		testlinks.add("https://login.wikimedia.org/wiki/Main_Page");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/LA1");
//		testlinks.add("https://en.wikipedia.org/wiki/Wikipedia:Main_Page/Errors");
//		testlinks.add("https://en.wikipedia.org/wiki/File:P_medicine.svg");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg1.1.10");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg1.1.13");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg1.1.9");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg1.2.1");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg1.2.2");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg1.2.6");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg1.2.7");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg1.2.8");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg10.1");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg10.13");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg10.19");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg2.1.1");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg2.1.2");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg2.1.22");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg2.1.3");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg2.1.4");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg2.1.6");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg3.1.2.1");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg3.1.8");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg3.2.1");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg3.6.6");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg9.4.28");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg9.4.31");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg9.4.47");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Alg9.4.49");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CV1");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CV11");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CV12");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CV13");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CV14");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CV2");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CV3");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CV4");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CV5");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CV6");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CVCI3");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CVD2");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CVD3");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CVD4");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CVEL10");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CVEL4");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CVEL6");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CVEL9");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CVS2");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CVT2");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CVT3");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CVT4");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CVT5");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CVXI5");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Calc2.1063");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Calc2.140");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Calc2.19");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Calc2.21");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Calc2.29");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Calc2.32");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Calc2.57");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Classical_Mechanics");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CoV13");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CoV14");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CoV15");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CoV27");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/CoV28");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/DO1");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/DO5");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/DO6");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/DO7");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/Dream_art");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/FS2");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/IE1");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/IE18");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/IE3");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/IE4");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/VC1.3");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/VC1.7");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/VC1.8");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/VC2.10");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/VC2.2");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/VC2.3");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/VC2.4");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/VC2.5");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/VC2.6");
//		testlinks.add("http://www.exampleproblems.com/wiki/index.php/VC2.7");
		
		return testlinks;
	}
	
	public static void main(String[] args)
	{
		Dict_E dict_e = new Dict_E();
		dict_e.LoadQuerySupport();
		
		System.out.println("\nDictionary loaded");
		//Dictionary.PrintDict2(query.dict);
		
		//Sentence is a class built to record the id of a query string.
		//List<Sentence> queries = GetQueries(dict_e.resourcepath + "hw3.queries");
		List<Sentence> queries = GetQueryTest();
		
		System.out.println("queries:");
		System.out.println(" ");
		
		for(int i = 0; i < queries.size(); i++)
		{
			System.out.println("Query " + queries.get(i).id + ":");
			System.out.println(" ");
			System.out.println("Query string: " + queries.get(i).str);

			//Random link results are selected for test
			//List<String> links = GetRandomLinks(dict_e);
			List<String> links = GetLinksTest();
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
