package Word;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.*;

public class Midterm {
	
	public static List<String> GetStopWords(String path)
	{
		List<String> stopwordlist = new ArrayList<String>();
		File file = new File(path);
		StringBuilder tempword = new StringBuilder();
		
		BufferedReader reader = null;
		FileReader filereader = null;
		String filename = file.getName();
		try
		{
			filereader = new FileReader(file);
		    reader = new BufferedReader(filereader);
		    
		    /*
		    ////This readLine() results in ineffectiveness in recognizing "\n"
		    String line = reader.readLine();   		
		    while(line != null)
		    {
		    	fulltext.append(line);
		    	line = reader.readLine();
		    }
		    */
		    int num = 0;
		    
		    while((num = reader.read()) != -1)  // num != -1 ???
		    {
		    	if (num == 10)  // line break "\n"
		    	{
		    		if (tempword.length() > 0)
		    		{
		    			stopwordlist.add(tempword.toString().trim());
		    		}
		    		tempword.setLength(0);
		    	}
		    	
		    	tempword.append((char)num);
		    }
		    
    		if (tempword.length() > 0)
    		{
    			stopwordlist.add(tempword.toString().trim());
    		}
		}
		catch (Exception e)
		{
			System.out.print("\nerror occur when reading file " + filename + "\n" + e.getMessage());
		}
		finally
		{
			if (reader != null)
			{
				try
				{
				    reader.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + filename + "\n" + e.getMessage());
				}
			}
			if (filereader != null)
			{
				try
				{
				    filereader.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + filename + "\n" + e.getMessage());
				}
			}
		}
		
		return stopwordlist;
	}
	
	public static List<String> FilterStopWords(List<String> tokens, List<String> stopwords)
	{
		List<String> filteredtokens = new ArrayList<String>();
		for(int i = 0; i < tokens.size(); i++)
		{
			if (!stopwords.contains(tokens.get(i)))
			{
				filteredtokens.add(tokens.get(i));
			}
		}
		
		return filteredtokens;
	}
	
	public static int[] GetDocV_Binary(Dictionary myDict, int mydocid)
	{
		int size = myDict.Size();
		int[] docvec = new int[size];
		
		for(int i = 0; i < size; i++)
		{
			if (myDict.GetTerm(i).postings.contains(mydocid))
			{
				docvec[i] = 1;
			}
			else
			{
				docvec[i] = 0;
			}
		}
		
		
		return docvec;
		
	}
	
	public static int[] GetDocV_RawTf(Dictionary myDict, int mydocid)
	{
		int size = myDict.Size();
		int[] docvec = new int[size];
		
		for(int i = 0; i < size; i++)
		{
			int docidindex =  myDict.GetTerm(i).postings.indexOf(mydocid);
			if (docidindex < 0)
			{
				docvec[i] = 0;
			}
			else
			{
				docvec[i] = myDict.GetTerm(i).tfreqs.get(docidindex);
			}
		}
		
		
		return docvec;
		
	}
	
	public static float[] GetDocV_TFIDF(Dictionary myDict, int mydocid, int totaldocn)
	{
		int size = myDict.Size();
		float[] docvec = new float[size];
		
		for(int i = 0; i < size; i++)
		{
			int docidindex =  myDict.GetTerm(i).postings.indexOf(mydocid);
			if (docidindex < 0)
			{
				docvec[i] = 0;
			}
			else
			{
				int rawtf = myDict.GetTerm(i).tfreqs.get(docidindex);
				double logtf = java.lang.Math.log10((double)rawtf) + 1;
				double idf = java.lang.Math.log10((double)totaldocn/(double)myDict.GetTerm(i).dfreq);
				docvec[i] = (float)(round(logtf * idf, 3));
				
			}
		}
		
		
		return docvec;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	public static int GetDocTermsNum(Dictionary myDict, int docid)
	{
		int num = 0;
		for(int i = 0; i <myDict.Size(); i++)
		{
			if(myDict.GetTerm(i).postings.contains(docid))
			{
				num += 1;
			}
		}
		
		return num;
	}
	
	public static int GetCommonTermsNum(Dictionary myDict, int docid, String[] queryterms)
	{
		int commonnum = 0;
		
		for (int i = 0; i < queryterms.length; i++)
		{
			int offset = myDict.GetIndex(queryterms[i]);
			if (offset >= 0)
			{
				if (myDict.GetTerm(offset).postings.contains(docid))
				{
					commonnum += 1;
				}
			}
		}
		
		return commonnum;
	}
	
	public static float GetJaccardSimilarity(Dictionary myDict, int docid, String[] queryterms)
	{
		int num = 0;
		for(int i = 0; i <myDict.Size(); i++)
		{
			if(myDict.GetTerm(i).postings.contains(docid))
			{
				num += 1;
			}
		}
		
		int commonnum = 0;
		
		for (int i = 0; i < queryterms.length; i++)
		{
			int offset = myDict.GetIndex(queryterms[i]);
			if (offset >= 0)
			{
				if (myDict.GetTerm(offset).postings.contains(docid))
				{
					commonnum += 1;
				}
				else
				{
					num += 1;
				}
			}
			else
			{
				num += 1;
			}
		}
		
		float jvalue = (float) round((double)commonnum / (double)num, 3);
		
		return jvalue;
	}
	
	public static Dictionary GetDictForProb2()
	{
		List<int[]> doctermfreqs = new LinkedList<int[]>();
		doctermfreqs.add(new int[]{0, 3, 2, 4, 0, 5, 0, 0, 4, 2});
		doctermfreqs.add(new int[]{3, 0, 1, 4, 3, 0, 0, 5, 1, 6});
		doctermfreqs.add(new int[]{6, 0, 5, 1, 2, 0, 2, 5, 0, 7});
		doctermfreqs.add(new int[]{1, 8, 0, 2, 0, 1, 6, 0, 2, 1});
		doctermfreqs.add(new int[]{2, 7, 0, 0, 0, 3, 0, 2, 3, 0});
		
		Dictionary myDictNew = new Dictionary();
		for (int i = 0; i < doctermfreqs.size(); i++)
		{
			for(int j = 0; j < 10; j++)
			{
				int n = doctermfreqs.get(i)[j];
				myDictNew.InserTerm("Term_" + (char)(j+65) + "_" + (j+1), n, i+1);
			}
		}
		
		return myDictNew;
	}
	
	public static String GetUnaryCode(int gap)
	{
		StringBuilder code = new StringBuilder();
		for(int i = 0; i < gap; i++)
		{
			code.append("1");
		}
		code.append("0");
		
		return code.toString();
	}
	
	public static String GetGammaCode(int gap)
	{
		StringBuilder code = new StringBuilder();
		
		String binary = Integer.toBinaryString(gap);
		code.append(binary);
		code.deleteCharAt(0);
		String length = GetUnaryCode(code.length());
		code.insert(0, length);
		
		return code.toString();
	}
	
	public static String GetDeltaCode(int gap)
	{
		StringBuilder code = new StringBuilder();
		
		String binary = Integer.toBinaryString(gap);
		code.append(binary);
		code.deleteCharAt(0);
		String length = GetGammaCode(code.length());
		code.insert(0, length);
		
		return code.toString();
	}
	
	public static void main(String[] args)
	{
		String doc1 = "Probiotics help digest the milk sugar lactose, making yogurt often tolerable by those who can't eat dairy foods.";
		String doc2 = "In addition to supporting digestion, research suggests that probiotics in yogurt may boost the functioning of the immune system.";
		String doc3 = "If you want to give your yogurt a nutritional upgrade, go for Greek yogurt, which can pack about double the protein of regular yogurt thanks to its straining process, which removes the liquid whey along with some sugars.";
		
		List<String> tokens1 = Tokenizer.Tokenize(new StringBuilder(doc1), false);
		List<String> tokens2 = Tokenizer.Tokenize(new StringBuilder(doc2), false);
		List<String> tokens3 = Tokenizer.Tokenize(new StringBuilder(doc3), false);
		
		String[] discardedwords = {"to", "those", "t", "if"};
		List<String> stopwords = GetStopWords("Z://Œƒ∏Â//¡Ù—ß//UTDallas//textbooks//6322_spring2017_Information Retrieval//resourcesIR//stopwords");
		stopwords.addAll(Arrays.asList(discardedwords));
		List<String> filteredtokens1 = FilterStopWords(tokens1, stopwords);
		List<String> filteredtokens2 = FilterStopWords(tokens2, stopwords);
		List<String> filteredtokens3 = FilterStopWords(tokens3, stopwords);
		
		Lemmatizer lemmatizer = new Lemmatizer();
		List<String> lemmatizedtokens1 = lemmatizer.Lemmatize(filteredtokens1, true);
		List<String> tags1 = lemmatizer.GetTags();
		List<String> lemmatizedtokens2 = lemmatizer.Lemmatize(filteredtokens2, true);
		List<String> tags2 = lemmatizer.GetTags();
		List<String> lemmatizedtokens3 = lemmatizer.Lemmatize(filteredtokens3, true);
		List<String> tags3 = lemmatizer.GetTags();
		
		List<String> mergedtokens = new ArrayList<String>();
		Tokenizer.MergeTokenLists2(mergedtokens, lemmatizedtokens1);
		Tokenizer.MergeTokenLists2(mergedtokens, lemmatizedtokens2);
		Tokenizer.MergeTokenLists2(mergedtokens, lemmatizedtokens3);
		
		Dictionary myDict = new Dictionary();
		Dictionary.AddDictTerms(myDict, lemmatizedtokens1, 1);
		Dictionary.AddDictTerms(myDict, lemmatizedtokens2, 2);
		Dictionary.AddDictTerms(myDict, lemmatizedtokens3, 3);

		System.out.println(doc1);
		System.out.println("tokens:  " + String.join("|", tokens1));
		System.out.println("without stopwords: " + String.join("|", filteredtokens1));
		System.out.println("lemmatized tokens:  " + String.join("|", lemmatizedtokens1));
		System.out.println("tags:  " + String.join("|", tags1));
		
		
		System.out.println(" ");
		System.out.println(doc2);
		System.out.println("tokens:  " + String.join("|", tokens2));
		System.out.println("without stopwords: " + String.join("|", filteredtokens2));
		System.out.println("lemmatized tokens:  " + String.join("|", lemmatizedtokens2));
		System.out.println("tags:  " + String.join("|", tags2));
		System.out.println(" ");
		System.out.println(doc3);
		System.out.println("tokens:  " + String.join("|", tokens3));
		System.out.println("without stopwords: " + String.join("|", filteredtokens3));
		System.out.println("lemmatized tokens:  " + String.join("|", lemmatizedtokens3));
		System.out.println("tags:  " + String.join("|", tags3));
		System.out.println(" ");
		System.out.println("merged lemmatized tokens:  " + String.join("|", mergedtokens));
		System.out.println(" ");
		System.out.println("Dict: ");
		
		//int totalterms = 0;
		for(int i= 0; i < myDict.Size(); i++)
		{
			Term term = myDict.GetTerm(i);
			System.out.println(term.str +  "; " + term.tfreq+ "; " + term.dfreq  + "; *" + Arrays.toString(term.postings.toArray())+ "; *" +  Arrays.toString(term.tfreqs.toArray()));
			//totalterms+= term.tfreq;
		}
		
		System.out.println(" ");
		System.out.println("Dict size: " + myDict.Size());
		
		int[] docvector_binary1 = GetDocV_Binary(myDict, 1);
		int[] docvector_binary2 = GetDocV_Binary(myDict, 2);
		int[] docvector_binary3 = GetDocV_Binary(myDict, 3);
		int[] docvector_rawtf1 = GetDocV_RawTf(myDict, 1);
		int[] docvector_rawtf2 = GetDocV_RawTf(myDict, 2);
		int[] docvector_rawtf3 = GetDocV_RawTf(myDict, 3);
		float[] docvector_tfidf1 = GetDocV_TFIDF(myDict, 1, 3);
		float[] docvector_tfidf2 = GetDocV_TFIDF(myDict, 2, 3);
		float[] docvector_tfidf3 = GetDocV_TFIDF(myDict, 3, 3);
		
		System.out.println(" ");
		System.out.println("terms: ");
		for(int i= 0; i < myDict.Size(); i++)
		{
			System.out.print(myDict.GetTerm(i).str);
			if (i < myDict.Size())
			{
				System.out.print(", ");
			}
		}
		System.out.println(" ");
		System.out.println(" ");
		System.out.println("document vector binary:");
		System.out.println(Arrays.toString(docvector_binary1));
		System.out.println(Arrays.toString(docvector_binary2));
		System.out.println(Arrays.toString(docvector_binary3));
		System.out.println(" ");
		System.out.println("document vector raw tf:");
		System.out.println(Arrays.toString(docvector_rawtf1));
		System.out.println(Arrays.toString(docvector_rawtf2));
		System.out.println(Arrays.toString(docvector_rawtf3));
		System.out.println(" ");
		System.out.println("document vector tf idf:");
		System.out.println(Arrays.toString(docvector_tfidf1));
		System.out.println(Arrays.toString(docvector_tfidf2));
		System.out.println(Arrays.toString(docvector_tfidf3));
		
		String[] query1 = new String[]{"yogurt", "immune", "system"};
		String[] query2 = new String[]{"yogurt", "digest", "probiotic", "nutrition"};
		String[] query3 = new String[]{"sugar", "yogurt", "nutrition", "milk"};
		String[] query4 = new String[]{"research", "milk", "yogurt", "nutrition"};
		
		float jvalue1_1 = GetJaccardSimilarity(myDict, 1, query1);
		float jvalue1_2 = GetJaccardSimilarity(myDict, 2, query1);
		float jvalue1_3 = GetJaccardSimilarity(myDict, 3, query1);
		float jvalue2_1 = GetJaccardSimilarity(myDict, 1, query2);
		float jvalue2_2 = GetJaccardSimilarity(myDict, 2, query2);
		float jvalue2_3 = GetJaccardSimilarity(myDict, 3, query2);
		float jvalue3_1 = GetJaccardSimilarity(myDict, 1, query3);
		float jvalue3_2 = GetJaccardSimilarity(myDict, 2, query3);
		float jvalue3_3 = GetJaccardSimilarity(myDict, 3, query3);
		float jvalue4_1 = GetJaccardSimilarity(myDict, 1, query4);
		float jvalue4_2 = GetJaccardSimilarity(myDict, 2, query4);
		float jvalue4_3 = GetJaccardSimilarity(myDict, 3, query4);
		
		int termsnumdoc1 = GetDocTermsNum(myDict, 1);
		int termsnumdoc2 = GetDocTermsNum(myDict, 2);
		int termsnumdoc3 = GetDocTermsNum(myDict, 3);
		
		System.out.println(" ");
		System.out.println("There are " + termsnumdoc1 + " terms in doc1");
		System.out.println("There are " + termsnumdoc2 + " terms in doc2");
		System.out.println("There are " + termsnumdoc3 + " terms in doc3");
		
		System.out.println(" ");
		System.out.println("Jaccard similarity Q1 vs D1: " + jvalue1_1 + "  Common terms: " + GetCommonTermsNum(myDict, 1, query1));
		System.out.println("Jaccard similarity Q1 vs D2: " + jvalue1_2 + "  Common terms: " + GetCommonTermsNum(myDict, 2, query1));
		System.out.println("Jaccard similarity Q1 vs D3: " + jvalue1_3 + "  Common terms: " + GetCommonTermsNum(myDict, 3, query1));
		System.out.println("Jaccard similarity Q2 vs D1: " + jvalue2_1 + "  Common terms: " + GetCommonTermsNum(myDict, 1, query2));
		System.out.println("Jaccard similarity Q2 vs D2: " + jvalue2_2 + "  Common terms: " + GetCommonTermsNum(myDict, 2, query2));
		System.out.println("Jaccard similarity Q2 vs D3: " + jvalue2_3 + "  Common terms: " + GetCommonTermsNum(myDict, 3, query2));
		System.out.println("Jaccard similarity Q3 vs D1: " + jvalue3_1 + "  Common terms: " + GetCommonTermsNum(myDict, 1, query3));
		System.out.println("Jaccard similarity Q3 vs D2: " + jvalue3_2 + "  Common terms: " + GetCommonTermsNum(myDict, 2, query3));
		System.out.println("Jaccard similarity Q3 vs D3: " + jvalue3_3 + "  Common terms: " + GetCommonTermsNum(myDict, 3, query3));
		System.out.println("Jaccard similarity Q4 vs D1: " + jvalue4_1 + "  Common terms: " + GetCommonTermsNum(myDict, 1, query4));
		System.out.println("Jaccard similarity Q4 vs D2: " + jvalue4_2 + "  Common terms: " + GetCommonTermsNum(myDict, 2, query4));
		System.out.println("Jaccard similarity Q4 vs D3: " + jvalue4_3 + "  Common terms: " + GetCommonTermsNum(myDict, 3, query4));

		System.out.println(" ");
		
        Dictionary myDictNew = GetDictForProb2();
        
		for(int i= 0; i < myDictNew.Size(); i++)
		{
			Term term = myDictNew.GetTerm(i);
			List<Integer> gaps = new ArrayList<Integer>();
			List<String> unarycodes = new ArrayList<String>();
			List<String> gammacodes = new ArrayList<String>();
			List<String> deltacodes = new ArrayList<String>();
			int lastdocid = 0;
			for (int j = 0; j < term.postings.size(); j++)
			{
				int gap = term.postings.get(j) - lastdocid;
				gaps.add(gap);
				unarycodes.add(GetUnaryCode(gap));
				gammacodes.add(GetGammaCode(gap));
				deltacodes.add(GetDeltaCode(gap));
				
				lastdocid = term.postings.get(j);
			}
			
			System.out.println(term.str +  "; tfreq: " + term.tfreq+ "; dfreq: " + term.dfreq  + "; tfreqs: *" +  Arrays.toString(term.tfreqs.toArray()));
			System.out.println("    " + "postings: *" + Arrays.toString(term.postings.toArray()));
			System.out.println("    " + "gaps: *" + Arrays.toString(gaps.toArray()));
			System.out.println("    " + "unary codes: *" + Arrays.toString(unarycodes.toArray()));
			System.out.println("    " + "gamma codes: *" + Arrays.toString(gammacodes.toArray()));
			System.out.println("    " + "delta codes: *" + Arrays.toString(deltacodes.toArray()));
		}

	}
}
