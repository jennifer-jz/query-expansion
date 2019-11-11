package Word;

import java.util.Arrays;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.*;

public class Query {

	Dictionary dict;
	List<Document> docInfoList;
	List<String> stopwords;
	Lemmatizer lemmatizer;
	
	public Query()
	{
		dict = new Dictionary();
		docInfoList = new ArrayList<Document>();
		stopwords = new ArrayList<String>();
	}
	
	void LoadDictionary1(String folderpath)
	{
		//String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		String docinfofilepath = folderpath + "docInfo1";
		String indexfilepath = folderpath + "Index_Version1.uncompressed";
		docInfoList = new ArrayList<Document>();
		docInfoList = Index.ReadDocInfo(docinfofilepath);
		dict = new Dictionary();
	    dict = Index.ReadIndex(indexfilepath, docInfoList);
	    
		System.out.println("dictionary version 1 loaded!");
	}
	
	void LoadDictionaryWithPosition(String corpuspath, String resourcepath)
	{
	    File[] filelist = Tokenizer.GetAllFiles(corpuspath);
	    File stopwordsfile = new File(resourcepath + "stopwords");
	    if (stopwordsfile.exists())
	    {
	    	stopwords = Tokenizer.GetStopWords(resourcepath + "stopwords");
	    }
	    lemmatizer = new Lemmatizer();
	    docInfoList = new ArrayList<Document>();
	    dict = new Dictionary();
	    int doclen1 = 0;
	    int max_tf1 = 0;
	    for(int i = 0; i < filelist.length; i++)
		{
			StringBuilder originaltext = Tokenizer.ReadFileText(filelist[i]);
			StringBuilder filteredtext = Tokenizer.FilterHTML(originaltext);
			List<Token> rawtokens = Tokenizer.TokenizeWithPosition(filteredtext, false, true);  // without stemmatization

            List<Token> tokens1 = lemmatizer.LemmatizeToken(rawtokens, false);
            doclen1 = tokens1.size();  // doclen includes occurrences of stop words
			tokens1 = Tokenizer.FilterStopWordTokens(tokens1, stopwords);  // lemmatize raw tokens before filtering stop words
			max_tf1 = Tokenizer.CountMaxTermFreq_Token(tokens1);  // count maximum term frequency in a document after removing stop words
			Dictionary.AddDictTermsWithPosition(dict, tokens1, (i+1));
			
			//PrintDict(myDict1);
			
			Document newdoc1 = new Document();
			newdoc1.ID = i+1;
			newdoc1.max_tf = max_tf1;
			newdoc1.doclen = doclen1;
			docInfoList.add(newdoc1);
			
			if (i%200 == 0)
			{
				System.out.print("\n" + i);
			}
			System.out.print(".");

		}
	}
	
	void LoadDictionary2(String folderpath)
	{
		//String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		String docinfofilepath = folderpath + "docInfo2";
		String indexfilepath = folderpath + "Index_Version2.uncompressed";
		docInfoList = new ArrayList<Document>();
		docInfoList = Index.ReadDocInfo(docinfofilepath);
		dict = new Dictionary();
	    dict = Index.ReadIndex(indexfilepath, docInfoList);
	    
	    System.out.println("dictionary version 2 loaded!");
	}
	
	void LoadQuerySupport(String folderpath)
	{
	    File stopwordsfile = new File(folderpath + "stopwords");
	    if (stopwordsfile.exists())
	    {
	    	stopwords = Tokenizer.GetStopWords(folderpath + "stopwords");
	    }
	    lemmatizer = new Lemmatizer();
	    
	    System.out.println("query support database loaded!");
	}
	
	void LoadDocTermInfo()
	{
		for (int i = 0; i < dict.Size(); i++)
		{
			Term term = dict.GetTerm(i);
			for(int j = 0; j < term.postings.size(); j++)
			{
				int docindex = term.postings.get(j) - 1;
				int freq = term.tfreqs.get(j);
				docInfoList.get(docindex).dict_termids.add(i);
				docInfoList.get(docindex).dict_tfs.add(freq);
				
				List<Integer> positions = QueryExpansion.GetDocTermPositions(term, j);
				docInfoList.get(docindex).dict_tpositions.addAll(positions);
//				int totalfreqs = 0;
//				for(int k = 0; k < term.tfreqs.size(); k++)
//				{
//					if (k < j)
//					{
//						totalfreqs += term.tfreqs.get(k);
//					}
//				}
//				
//				for(int k = totalfreqs; k < totalfreqs + freq; k++)
//				{
//					docInfoList.get(docindex).dict_tpositions.add(term.positions.get(k));
//				}
			}
		}
	}
	
	public static String TermTrim(String term)
	{
		String newterm = term.trim();
		char tempchar;
		for(int i = 0; i < term.length(); i++)
		{
			tempchar = term.charAt(i);
			if (!Tokenizer.IsAlphanumeric(tempchar))
			{
				newterm = "";
				break;
			}
		}
		if (Tokenizer.IsPureNumeric(newterm))
		{
			newterm = "";
		}
		
		
		String stemmedtoken = Tokenizer.StemToken(newterm.toLowerCase());
		
		
		return stemmedtoken;
	}
	
	public List<String> GetQueryTermList(String querysent)
	{
		StringBuilder querysentbuilder = new StringBuilder(querysent);
		List<String> termlist = Tokenizer.Tokenize(querysentbuilder, false);
		termlist = Tokenizer.FilterStopWords(termlist, stopwords);
		termlist = lemmatizer.Lemmatize(termlist, false);
		
		
		return termlist;
	}
	
	public Term GetTermInfo(String query)
	{
		int index = dict.GetIndex(TermTrim(query));
		Term term = dict.GetTerm(index);
		
		return term;
	}
	
	public List<Integer> GetMaxTFInDoc(List<Integer> docids)
	{
		return Index.GetMaxTFInDoc(docids, docInfoList);
	}
	
	public List<Integer> GetDocLenInDoc(List<Integer> docids)
	{
		return Index.GetDocLenInDoc(docids, docInfoList);
	}
	
	//Since we load uncompressed dictionary, postings are not gaps
	public byte[] GetInvertedListBytes(Term term, boolean iscompress)
	{
		byte[] codebytes = null;
		StringBuilder tempcodes = new StringBuilder();
		ByteArrayOutputStream bytestream;
		try
		{
			int lastdocid = 0;
			bytestream = new ByteArrayOutputStream();
			for(int j = 0; j < term.postings.size(); j++)
			{
				Document currentDoc = docInfoList.get(term.postings.get(j) - 1);
				if (!iscompress)
				{
					bytestream.write(Index.IntToBytes4(term.postings.get(j))); // write a docid
					bytestream.write(Index.IntToBytes4(term.tfreqs.get(j)));  // write tfreq of the current docid
					bytestream.write(Index.IntToBytes4(currentDoc.max_tf)); // write max_ft of the current docid
					bytestream.write(Index.IntToBytes4(currentDoc.doclen)); // write doclen of the current docid

				}
				else
				{
					tempcodes.append(Index.GetDeltaCode(term.postings.get(j) - lastdocid));// write a docid
					tempcodes.append(Index.GetDeltaCode(term.tfreqs.get(j)));// write tfreq of the current docid  
					tempcodes.append(Index.GetDeltaCode(currentDoc.max_tf));// write max_ft of the current docid
					tempcodes.append(Index.GetDeltaCode(currentDoc.doclen));// write doclen of the current docid

				}
				lastdocid = term.postings.get(j);
			}
			
			if (iscompress)
			{
			    codebytes = Index.GetBytesFromBitString(tempcodes.toString());
			}
			else
			{
				codebytes = bytestream.toByteArray();
	    		bytestream.close();
			}
		
		}
		catch(IOException ee)
		{
			ee.printStackTrace();
		}

		return codebytes;
	}
	
	public List<Term> GetTermWithMaximumDF()
	{
		int maxdf = 0;
		List<Term> terms = new ArrayList<Term>();
		Term term;
		for(int i = 0; i < dict.Size(); i++)
		{
			term = dict.GetTerm(i);
			if (term.dfreq > maxdf)
			{
				maxdf = term.dfreq;
			}
		}
		
		for(int i = 0; i < dict.Size(); i++)
		{
			term = dict.GetTerm(i);
			if (term.dfreq == maxdf)
			{
				terms.add(term);
			}
		}
		
		return terms;
	}
	
	public List<Term> GetTermWithMinimumDF()
	{
		int mindf = 0;
		List<Term> terms = new ArrayList<Term>();
		Term term;
		for(int i = 0; i < dict.Size(); i++)
		{
			term = dict.GetTerm(i);
			if (term.dfreq < mindf || mindf == 0)
			{
				mindf = term.dfreq;
			}
		}
		
		for(int i = 0; i < dict.Size(); i++)
		{
			term = dict.GetTerm(i);
			if (term.dfreq == mindf)
			{
				terms.add(term);
			}
		}
		
		return terms;
	}
	
	public List<Document> GetDocWithLargestMaxTF()
	{
		List<Document> docs = new ArrayList<Document>();
		int maxtf = 0;
		Document doc;
		for(int i = 0; i < docInfoList.size(); i++)
		{
			doc = docInfoList.get(i);
			if (doc.max_tf > maxtf)
			{
				maxtf = doc.max_tf;
			}
		}
		
		for(int i = 0; i < docInfoList.size(); i++)
		{
			doc = docInfoList.get(i);
			if (doc.max_tf == maxtf)
			{
				docs.add(doc);
			}
		}
		
		return docs;
	}
	
	public List<Document> GetDocWithMaxDocLen()
	{
		List<Document> docs = new ArrayList<Document>();
		int maxdoclen = 0;
		Document doc;
		for(int i = 0; i < docInfoList.size(); i++)
		{
			doc = docInfoList.get(i);
			if (doc.doclen > maxdoclen)
			{
				maxdoclen = doc.doclen;
			}
		}
		
		for(int i = 0; i < docInfoList.size(); i++)
		{
			doc = docInfoList.get(i);
			if (doc.doclen == maxdoclen)
			{
				docs.add(doc);
			}
		}
		
		return docs;
	}
	
	public static void main(String[] args)
	{
		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		if (args.length > 0)
		{
			folderpath = args[0];
		}
		Query query = new Query();
		query.LoadDictionary2(folderpath);
		
		System.out.println(" ");
		//String work = "NASA";
		String[] querystrings = {"Reynolds", "NASA", "Prandtl", "flow", "pressure", "boundary", "shock"};
		for(int i = 0; i < querystrings.length; i++)
		{
			System.out.println("Stems of " + querystrings[i] + ": " + TermTrim(querystrings[i]));
			Term term = query.GetTermInfo(querystrings[i]);
			System.out.println("Query result: " + term.str +  "; " + term.tfreq+ "; " + term.dfreq  + "; *" + Arrays.toString(term.postings.toArray())+ "; *" +  Arrays.toString(term.tfreqs.toArray()) + "; *" +  Arrays.toString(query.GetMaxTFInDoc(term.postings).toArray())+ "; *" +  Arrays.toString(query.GetDocLenInDoc(term.postings).toArray()));
			System.out.println("number of bytes for posting (uncompressed): " + query.GetInvertedListBytes(term, false).length);
			System.out.println("number of bytes for posting (compressed): " + query.GetInvertedListBytes(term, true).length);
			System.out.println(" ");
		}
		
		List<Term> maxdfterms;
		List<Term> mindfterms;
		
		query.LoadDictionary1(folderpath);
		mindfterms = query.GetTermWithMinimumDF();
		maxdfterms = query.GetTermWithMaximumDF();
		System.out.println(" ");
		System.out.println("Terms with maximum df in Dictionary 1: ");
		for(int i = 0; i < maxdfterms.size(); i++)
		{
			System.out.print(maxdfterms.get(i).str + "; ");
		}
		System.out.println(" ");
		System.out.println("Terms with minimum df in Dictionary 1: ");
		for(int i = 0; i < mindfterms.size(); i++)
		{
			System.out.print(mindfterms.get(i).str + "; ");
		}
		
		System.out.println(" ");
		List<Document> docsmaxtf = query.GetDocWithLargestMaxTF();
		System.out.println("Document with largest max_tf: ");
		for(int i = 0; i < docsmaxtf.size(); i++)
		{
			System.out.print(docsmaxtf.get(i).ID + "(" + docsmaxtf.get(i).max_tf + "); ");
		}
		System.out.println(" ");
		List<Document> docsmaxdoclen = query.GetDocWithMaxDocLen();
		System.out.println("Document with largest doclen: ");
		for(int i = 0; i < docsmaxdoclen.size(); i++)
		{
			System.out.print(docsmaxdoclen.get(i).ID + "(" + docsmaxdoclen.get(i).doclen + "); ");
		}
		System.out.println(" ");
		
		query.LoadDictionary2(folderpath);
		System.out.println(" ");
		System.out.println("Terms with maximum df in Dictionary 2: ");
		mindfterms = query.GetTermWithMinimumDF();
		maxdfterms = query.GetTermWithMaximumDF();
		for(int i = 0; i < maxdfterms.size(); i++)
		{
			System.out.print(maxdfterms.get(i).str + "; ");
		}
		System.out.println(" ");
		System.out.println("Terms with minimum df in Dictionary 2: ");
		for(int i = 0; i < mindfterms.size(); i++)
		{
			System.out.print(mindfterms.get(i).str + "; ");
		}
		
		System.out.println(" ");
		List<Document> docsmaxtf2 = query.GetDocWithLargestMaxTF();
		System.out.println("Document with largest max_tf: ");
		for(int i = 0; i < docsmaxtf2.size(); i++)
		{
			System.out.print(docsmaxtf2.get(i).ID + "(" + docsmaxtf2.get(i).max_tf + "); ");
		}
		System.out.println(" ");
		List<Document> docsmaxdoclen2 = query.GetDocWithMaxDocLen();
		System.out.println("Document with largest doclen: ");
		for(int i = 0; i < docsmaxdoclen2.size(); i++)
		{
			System.out.print(docsmaxdoclen2.get(i).ID + "(" + docsmaxdoclen2.get(i).doclen + "); ");
		}
		System.out.println(" ");
		
	}
}
