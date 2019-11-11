package Word;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

public class QueryExpansion3 {

	public static String GetLinkStr(String textline)
	{
		String linkstr = "";
		int index = textline.indexOf(",");
		if (index > 0)
		{
		    linkstr = textline.substring(0, index);
		}
		
		return linkstr;
	}
	
	public static String GetText(String textline)
	{
		String text = "";
		int index = textline.indexOf(",");
		if (index > 0)
		{
		    text = textline.substring(index + 1);
		}
		
		return text;
	}
	
	public static List<String> GetWebContents()
	{
		Instant dtStart = Instant.now();
		Instant dtInstant1;
		Instant dtInstant2;
		Instant dtEnd;
		long dtLength1 = 0;
		long dtLength2 = 0;
		long dtLength3 = 0;
		long dtLength4 = 0;
		List<String> webContents = new LinkedList<String>();
		String projectpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//";
		
		String contentLine = "";
		BufferedReader br = null;
	
		String text = "";
		int i = 1;
		
		try
		{
			br = new BufferedReader(new FileReader(projectpath + "content and url"));
			contentLine = br.readLine();
			while (contentLine != null) 
			{
				dtInstant1 = Instant.now();
				text = GetText(contentLine);
				webContents.add(text);
				dtInstant2 = Instant.now();
				dtLength1 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
				
			    if (i % 10 == 0)
			    {
			    	System.out.print(".");
			    }
			    
			    if (i % 1000 == 0)
			    {
			    	System.out.println("\n" + i);
			    }
			    
			    contentLine = br.readLine();
			}
			
			dtEnd = Instant.now();
			
			System.out.println("The time1 elapsed: " + Duration.ofMillis(dtLength1).getSeconds());
			System.out.println("The time elapsed: " + Duration.between(dtStart, dtEnd));
		}
		catch (Exception e)
		{
			System.out.print("\nerror occur when reading file " + "content and url" + "\n" + e.getMessage());
		}
		finally
		{
			if (br != null)
			{
				try
				{
				    br.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + "content and url" + "\n" + e.getMessage());
				}
			}
		}
		
		System.out.println("Write complete!");
		
		
		return webContents;
	}
	
	public static List<String> GetTermsFromFile(StringBuilder text, Dict_E dict_e)
	{
		List<String> tokens = Tokenizer.Tokenize(text, false, true);  // without stemmatization
		tokens = Tokenizer.FilterStopWords(tokens, dict_e.stopwords);  // lemmatize raw tokens before filtering stop words

		return tokens;
	}
	
	
	public static List<Document> LoadDocTermInfo(Dictionary dict, List<Document> docInfoList)
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
				docInfoList.get(docindex).dict_tpositions2.add(term.positions2.get(j));
				//List<Integer> positions = QueryExpansion.GetDocTermPositions(term, j);
				//docInfoList.get(docindex).dict_tpositions.addAll(positions);
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
		
		return docInfoList;
	}
	
    public static void BuildIndex()
    {
		Instant dtStart = Instant.now();
		Instant dtInstant1;
		Instant dtInstant2;
		Instant dtEnd;
		long dtLength1 = 0;
		long dtLength2 = 0;
		long dtLength3 = 0;
		long dtLength4 = 0;
		long dtLength5 = 0;
		long dtLength6 = 0;
		long dtLength7 = 0;
		long dtLength8 = 0;
    	
		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		String projectpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//";
		//List<String> stopwords = Tokenizer.GetStopWords("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//resourcesIR//stopwords");
		//File sourcefile = new File(sourcefilepath);

		Dictionary myDict1 = new Dictionary();
		List<Document> docInfoList1 = new ArrayList<Document>();
		int max_tf1 = 0;

		List<Token> tokens = new LinkedList<Token>();
		String contentLine = "";
		BufferedReader br = null;
	
		int i = 1;
		
		try
		{
			br = new BufferedReader(new FileReader(projectpath + "content and url"));
			contentLine = br.readLine();
			while (contentLine != null) 
			{
				dtInstant1 = Instant.now();
				String text = GetText(contentLine);
				dtInstant2 = Instant.now();
				dtLength1 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
				
				tokens = new LinkedList<Token>();
				tokens = GetTermsFromFileWithPosition_Stem(new StringBuilder(text)); //without stemmatization, with forms kept

	            //List<String> tokens1 = lemmatizer.Lemmatize(rawtokens, false); //This step is extremely slow
				//tokens1 = Tokenizer.FilterStopWords(tokens1, stopwords);  // lemmatize raw tokens before filtering stop words
				dtInstant2 = Instant.now();
				dtLength2 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
				max_tf1 = Tokenizer.CountMaxTermFreq_Token(tokens);  // count maximum term frequency in a document after removing stop words
				Dictionary.AddDictTermsWithPositionAndForm(myDict1, tokens, i);
				dtInstant2 = Instant.now();
				dtLength3 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
				Document newdoc1 = new Document();
				newdoc1.ID = i;
				newdoc1.max_tf = max_tf1;
				newdoc1.linkstr = GetLinkStr(contentLine);
				docInfoList1.add(newdoc1);
				
			    contentLine = br.readLine();
			    i++;
				dtInstant2 = Instant.now();
				dtLength4 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
			    if (i % 10 == 0)
			    {
			    	System.out.print(".");
			    }
			    
			    if (i % 1000 == 0)
			    {
			    	System.out.println("\n" + i);
			        System.out.println("The time1 elapsed: " + Duration.ofMillis(dtLength1).getSeconds());
			        System.out.println("The time2 elapsed: " + Duration.ofMillis(dtLength2).getSeconds());
			        System.out.println("The time3 elapsed: " + Duration.ofMillis(dtLength3).getSeconds());
			        System.out.println("The time4 elapsed: " + Duration.ofMillis(dtLength4).getSeconds());
			    }
			    
			    if (i % 10000 == 0)
			    {
			    	docInfoList1 = LoadDocTermInfo(myDict1, docInfoList1);
			    	WriteIndex(projectpath + "DictIndex_" + (i / 10000), myDict1);
			    	WriteDocInfo(projectpath + "docInfo_" + (i / 10000), docInfoList1);
			    	myDict1 = null;
			    	myDict1 = new Dictionary();
			    	docInfoList1 = null;
			    	docInfoList1 = new ArrayList<Document>();
			    }
			}
			
			if (i % 10000 > 0)
			{
				docInfoList1 = LoadDocTermInfo(myDict1, docInfoList1);
			    WriteIndex(projectpath + "DictIndex_" + (i / 10000 + 1), myDict1);
			    WriteDocInfo(projectpath + "docInfo_" + (i / 10000 + 1), docInfoList1);
		    	myDict1 = null;
		    	myDict1 = new Dictionary();
		    	docInfoList1 = null;
		    	docInfoList1 = new ArrayList<Document>();
			}
		}
		catch (Exception e)
		{
			System.out.print("\nerror occur when reading file " + "content and url" + "\n" + e.getMessage());
		}
		finally
		{
			if (br != null)
			{
				try
				{
				    br.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + "content and url" + "\n" + e.getMessage());
				}
			}
		}
		
		System.out.println("Write complete!");
    }
    
    public static void BuildIndex_2()
    {
		Instant dtStart = Instant.now();
		Instant dtInstant1;
		Instant dtInstant2;
		Instant dtEnd;
		long dtLength1 = 0;
		long dtLength2 = 0;
		long dtLength3 = 0;
		long dtLength4 = 0;
		long dtLength5 = 0;
		long dtLength6 = 0;
		long dtLength7 = 0;
		long dtLength8 = 0;
    	
		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		String projectpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//";
		//List<String> stopwords = Tokenizer.GetStopWords("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//resourcesIR//stopwords");
		//File sourcefile = new File(sourcefilepath);

		Dictionary myDict1 = new Dictionary();
		List<Document> docInfoList1 = new ArrayList<Document>();
		int max_tf1 = 0;

		List<Token> tokens = new LinkedList<Token>();
		String contentLine = "";
		BufferedReader br = null;
	
		int i = 1;
		
		try
		{
			br = new BufferedReader(new FileReader(projectpath + "content and url"));
			contentLine = br.readLine();
			while (contentLine != null) 
			{
				dtInstant1 = Instant.now();
				String text = GetText(contentLine);
				dtInstant2 = Instant.now();
				dtLength1 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
				
				tokens = new LinkedList<Token>();
				//tokens = GetTermsFromFileWithForm(new StringBuilder(text)); //without stemmatization, with forms kept

	            //List<String> tokens1 = lemmatizer.Lemmatize(rawtokens, false); //This step is extremely slow
				//tokens1 = Tokenizer.FilterStopWords(tokens1, stopwords);  // lemmatize raw tokens before filtering stop words
				dtInstant2 = Instant.now();
				dtLength2 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
				max_tf1 = Tokenizer.CountMaxTermFreq_Token(tokens);  // count maximum term frequency in a document after removing stop words
				Dictionary.AddDictTermsWithPositionAndForm(myDict1, tokens, i);
				dtInstant2 = Instant.now();
				dtLength3 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
				Document newdoc1 = new Document();
				newdoc1.ID = i;
				newdoc1.max_tf = max_tf1;
				newdoc1.linkstr = GetLinkStr(contentLine);
				docInfoList1.add(newdoc1);
				
			    contentLine = br.readLine();
			    i++;
				dtInstant2 = Instant.now();
				dtLength4 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
			    if (i % 10 == 0)
			    {
			    	System.out.print(".");
			    }
			    
			    if (i % 1000 == 0)
			    {
			    	System.out.println("\n" + i);
			        System.out.println("The time1 elapsed: " + Duration.ofMillis(dtLength1).getSeconds());
			        System.out.println("The time2 elapsed: " + Duration.ofMillis(dtLength2).getSeconds());
			        System.out.println("The time3 elapsed: " + Duration.ofMillis(dtLength3).getSeconds());
			        System.out.println("The time4 elapsed: " + Duration.ofMillis(dtLength4).getSeconds());
			    }
			    
			    if (i % 10000 == 0)
			    {
			    	docInfoList1 = LoadDocTermInfo(myDict1, docInfoList1);
			    	WriteIndex(projectpath + "DictIndex_" + (i / 10000), myDict1);
			    	WriteDocInfo(projectpath + "docInfo_" + (i / 10000), docInfoList1);
			    	myDict1 = null;
			    	myDict1 = new Dictionary();
			    	docInfoList1 = null;
			    	docInfoList1 = new ArrayList<Document>();
			    }
			}
			
			if (i % 10000 > 0)
			{
				docInfoList1 = LoadDocTermInfo(myDict1, docInfoList1);
			    WriteIndex(projectpath + "DictIndex_" + (i / 10000 + 1), myDict1);
			    WriteDocInfo(projectpath + "docInfo_" + (i / 10000 + 1), docInfoList1);
		    	myDict1 = null;
		    	myDict1 = new Dictionary();
		    	docInfoList1 = null;
		    	docInfoList1 = new ArrayList<Document>();
			}
		}
		catch (Exception e)
		{
			System.out.print("\nerror occur when reading file " + "content and url" + "\n" + e.getMessage());
		}
		finally
		{
			if (br != null)
			{
				try
				{
				    br.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + "content and url" + "\n" + e.getMessage());
				}
			}
		}
		
		System.out.println("Write complete!");
    }
	
    public static void BuildIndex2()
    {
		Instant dtStart = Instant.now();
		Instant dtInstant1;
		Instant dtInstant2;
		Instant dtEnd;
		long dtLength1 = 0;
		long dtLength2 = 0;
		long dtLength3 = 0;
		long dtLength4 = 0;
		long dtLength5 = 0;
		long dtLength6 = 0;
		long dtLength7 = 0;
		long dtLength8 = 0;
    	
		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		String projectpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//";
		//List<String> stopwords = Tokenizer.GetStopWords("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//resourcesIR//stopwords");
		//File sourcefile = new File(sourcefilepath);

		Dictionary myDict1 = new Dictionary();
		List<Document> docInfoList1 = new ArrayList<Document>();
		int max_tf1 = 0;

		List<Token> tokens = new LinkedList<Token>();
		String contentLine = "";
		BufferedReader br = null;
	
		int i = 1;
		
		try
		{
			br = new BufferedReader(new FileReader(projectpath + "content and url"));
			contentLine = br.readLine();
			while (contentLine != null) 
			{
				dtInstant1 = Instant.now();
				String text = GetText(contentLine);
				dtInstant2 = Instant.now();
				dtLength1 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
				
				tokens = new LinkedList<Token>();
				tokens = GetTermsFromFileWithPosition_Stem(new StringBuilder(text)); //without stemmatization, with forms kept

	            //List<String> tokens1 = lemmatizer.Lemmatize(rawtokens, false); //This step is extremely slow
				//tokens1 = Tokenizer.FilterStopWords(tokens1, stopwords);  // lemmatize raw tokens before filtering stop words
				dtInstant2 = Instant.now();
				dtLength2 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
				max_tf1 = Tokenizer.CountMaxTermFreq_Token(tokens);  // count maximum term frequency in a document after removing stop words
				Dictionary.AddDictTermsWithPositionAndForm(myDict1, tokens, i);
				dtInstant2 = Instant.now();
				dtLength3 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
				Document newdoc1 = new Document();
				newdoc1.ID = i;
				newdoc1.max_tf = max_tf1;
				newdoc1.linkstr = GetLinkStr(contentLine);
				docInfoList1.add(newdoc1);
				
			    contentLine = br.readLine();
			    i++;
				dtInstant2 = Instant.now();
				dtLength4 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
			    if (i % 10 == 0)
			    {
			    	System.out.print(".");
			    }
			    
			    if (i % 1000 == 0)
			    {
			    	System.out.println("\n" + i);
			        System.out.println("The time1 elapsed: " + Duration.ofMillis(dtLength1).getSeconds());
			        System.out.println("The time2 elapsed: " + Duration.ofMillis(dtLength2).getSeconds());
			        System.out.println("The time3 elapsed: " + Duration.ofMillis(dtLength3).getSeconds());
			        System.out.println("The time4 elapsed: " + Duration.ofMillis(dtLength4).getSeconds());
			    }
			    
			    if (i % 10000 == 0)
			    {
			    	docInfoList1 = LoadDocTermInfo(myDict1, docInfoList1);
			    	WriteIndex(projectpath + "DictIndex_" + (i / 10000), myDict1);
			    	WriteDocInfo(projectpath + "docInfo_" + (i / 10000), docInfoList1);
			    	myDict1 = null;
			    	myDict1 = new Dictionary();
			    	docInfoList1 = null;
			    	docInfoList1 = new ArrayList<Document>();
			    }
			}
			
			if (i % 10000 > 0)
			{
				docInfoList1 = LoadDocTermInfo(myDict1, docInfoList1);
			    WriteIndex(projectpath + "DictIndex_" + (i / 10000 + 1), myDict1);
			    WriteDocInfo(projectpath + "docInfo_" + (i / 10000 + 1), docInfoList1);
		    	myDict1 = null;
		    	myDict1 = new Dictionary();
		    	docInfoList1 = null;
		    	docInfoList1 = new ArrayList<Document>();
			}
		}
		catch (Exception e)
		{
			System.out.print("\nerror occur when reading file " + "content and url" + "\n" + e.getMessage());
		}
		finally
		{
			if (br != null)
			{
				try
				{
				    br.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + "content and url" + "\n" + e.getMessage());
				}
			}
		}
		
		System.out.println("Write complete!");
    }
    
    public static void BuildIndex2_2()
    {
		Instant dtStart = Instant.now();
		Instant dtInstant1;
		Instant dtInstant2;
		Instant dtEnd;
		long dtLength1 = 0;
		long dtLength2 = 0;
		long dtLength3 = 0;
		long dtLength4 = 0;
		long dtLength5 = 0;
		long dtLength6 = 0;
		long dtLength7 = 0;
		long dtLength8 = 0;
    	
		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		String projectpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//";
		//List<String> stopwords = Tokenizer.GetStopWords("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//resourcesIR//stopwords");
		//File sourcefile = new File(sourcefilepath);

		Dict_E dict_e = new Dict_E();
		
		
		Dictionary myDict1 = new Dictionary();
		List<Document> docInfoList1 = new ArrayList<Document>();
		int max_tf1 = 0;

		List<String> tokens = new LinkedList<String>();
		String text = "";
		BufferedReader br = null;
	
		int i = 1;
		
		try
		{
			br = new BufferedReader(new FileReader(projectpath + "webtext"));
			text = br.readLine();
			while (text != null) 
			{
				dtInstant1 = Instant.now();
				
				tokens = new LinkedList<String>();
				tokens = GetTermsFromFile(new StringBuilder(text), dict_e); //without stemmatization, with forms kept

	            //List<String> tokens1 = lemmatizer.Lemmatize(rawtokens, false); //This step is extremely slow
				//tokens1 = Tokenizer.FilterStopWords(tokens1, stopwords);  // lemmatize raw tokens before filtering stop words
				dtInstant2 = Instant.now();
				dtLength2 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
				max_tf1 = Tokenizer.CountMaxTermFreq(tokens);  // count maximum term frequency in a document after removing stop words
				Dictionary.AddDictTerms(myDict1, tokens, i);
				dtInstant2 = Instant.now();
				dtLength3 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
//				Document newdoc1 = new Document();
//				newdoc1.ID = i;
//				newdoc1.max_tf = max_tf1;
//				newdoc1.linkstr = GetLinkStr(contentLine);
//				docInfoList1.add(newdoc1);
				
				text = br.readLine();
			    i++;
				dtInstant2 = Instant.now();
				dtLength4 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
			    if (i % 10 == 0)
			    {
			    	System.out.print(".");
			    }
			    
			    if (i % 1000 == 0)
			    {
			    	System.out.println("\n" + i);
			        System.out.println("The time1 elapsed: " + Duration.ofMillis(dtLength1).getSeconds());
			        System.out.println("The time2 elapsed: " + Duration.ofMillis(dtLength2).getSeconds());
			        System.out.println("The time3 elapsed: " + Duration.ofMillis(dtLength3).getSeconds());
			        System.out.println("The time4 elapsed: " + Duration.ofMillis(dtLength4).getSeconds());
			    }
			    
			    if (i % 10000 == 0)
			    {
					dtInstant2 = Instant.now();
					dtLength5 += Duration.between(dtInstant1, dtInstant2).toMillis();
					dtInstant1 = Instant.now();
			    	//docInfoList1 = LoadDocTermInfo(myDict1, docInfoList1);
					dtInstant2 = Instant.now();
					dtLength6 += Duration.between(dtInstant1, dtInstant2).toMillis();
					dtInstant1 = Instant.now();
					WriteIndexSimple(projectpath + "DictIndex_" + (i / 10000), myDict1);
					dtInstant2 = Instant.now();
					dtLength7 += Duration.between(dtInstant1, dtInstant2).toMillis();
			    	//WriteDocInfo(projectpath + "docInfo_" + (i / 10000), docInfoList1);
			    	myDict1 = null;
			    	myDict1 = new Dictionary();
			    	//docInfoList1 = null;
			    	//docInfoList1 = new ArrayList<Document>();
			    	
			        System.out.println("The time5 elapsed: " + Duration.ofMillis(dtLength5).getSeconds());
			        System.out.println("The time6 elapsed: " + Duration.ofMillis(dtLength6).getSeconds());
			        System.out.println("The time7 elapsed: " + Duration.ofMillis(dtLength7).getSeconds());
			        dtLength5 = 0;
			        dtLength6 = 0;
			        dtLength7 = 0;
			    }
			}
			
			if (i % 10000 > 0)
			{
				//docInfoList1 = LoadDocTermInfo(myDict1, docInfoList1);
				WriteIndexSimple(projectpath + "DictIndex_" + (i / 10000 + 1), myDict1);
			    //WriteDocInfo(projectpath + "docInfo_" + (i / 10000 + 1), docInfoList1);
		    	myDict1 = null;
		    	myDict1 = new Dictionary();
		    	//docInfoList1 = null;
		    	//docInfoList1 = new ArrayList<Document>();
			}
		}
		catch (Exception e)
		{
			System.out.print("\nerror occur when reading file " + "content and url" + "\n" + e.getMessage());
		}
		finally
		{
			if (br != null)
			{
				try
				{
				    br.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + "content and url" + "\n" + e.getMessage());
				}
			}
		}
		
		System.out.println("Write complete!");
    }
    
    public static void BuildIndex3()
    {
		Instant dtStart = Instant.now();
		Instant dtInstant1;
		Instant dtInstant2;
		Instant dtEnd;
		long dtLength1 = 0;
		long dtLength2 = 0;
		long dtLength3 = 0;
		long dtLength4 = 0;
		long dtLength5 = 0;
		long dtLength6 = 0;
		long dtLength7 = 0;
		long dtLength8 = 0;
    	
		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		String projectpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//";
		String resourcepath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//resourcesIR//";
		//List<String> stopwords = Tokenizer.GetStopWords("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//resourcesIR//stopwords");
		//File sourcefile = new File(sourcefilepath);

		Dict_E dict_e = new Dict_E();
		dict_e.LoadQuerySupport();
		
		Dictionary myDict1 = new Dictionary();
		List<Document> docInfoList1 = new ArrayList<Document>();
		int max_tf1 = 0;

		List<String> tokens = new LinkedList<String>();
		String text = "";
		BufferedReader br = null;
	
		int i = 1;
		
		try
		{
			br = new BufferedReader(new FileReader(projectpath + "webtext"));
			text = br.readLine();
			while (text != null) 
			{
				dtInstant1 = Instant.now();
				
				tokens = new LinkedList<String>();
				tokens = GetTermsFromFile_Stemming(new StringBuilder(text), dict_e); //without stemmatization, with forms kept

	            //List<String> tokens1 = lemmatizer.Lemmatize(rawtokens, false); //This step is extremely slow
				//tokens1 = Tokenizer.FilterStopWords(tokens1, stopwords);  // lemmatize raw tokens before filtering stop words
				dtInstant2 = Instant.now();
				dtLength2 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
				max_tf1 = Tokenizer.CountMaxTermFreq(tokens);  // count maximum term frequency in a document after removing stop words
				Dictionary.AddDictTerms(myDict1, tokens, i);
				dtInstant2 = Instant.now();
				dtLength3 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
//				Document newdoc1 = new Document();
//				newdoc1.ID = i;
//				newdoc1.max_tf = max_tf1;
//				newdoc1.linkstr = GetLinkStr(contentLine);
//				docInfoList1.add(newdoc1);
				
				text = br.readLine();
			    i++;
				dtInstant2 = Instant.now();
				dtLength4 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
			    if (i % 10 == 0)
			    {
			    	System.out.print(".");
			    }
			    
			    if (i % 1000 == 0)
			    {
			    	System.out.println("\n" + i);
			        System.out.println("The time1 elapsed: " + Duration.ofMillis(dtLength1).getSeconds());
			        System.out.println("The time2 elapsed: " + Duration.ofMillis(dtLength2).getSeconds());
			        System.out.println("The time3 elapsed: " + Duration.ofMillis(dtLength3).getSeconds());
			        System.out.println("The time4 elapsed: " + Duration.ofMillis(dtLength4).getSeconds());
			    }
			    
			    if (i % 10000 == 0)
			    {
					dtInstant2 = Instant.now();
					dtLength5 += Duration.between(dtInstant1, dtInstant2).toMillis();
					dtInstant1 = Instant.now();
			    	//docInfoList1 = LoadDocTermInfo(myDict1, docInfoList1);
					dtInstant2 = Instant.now();
					dtLength6 += Duration.between(dtInstant1, dtInstant2).toMillis();
					dtInstant1 = Instant.now();
					WriteIndexSimple(projectpath + "DictIndex_" + (i / 10000), myDict1);
					dtInstant2 = Instant.now();
					dtLength7 += Duration.between(dtInstant1, dtInstant2).toMillis();
			    	//WriteDocInfo(projectpath + "docInfo_" + (i / 10000), docInfoList1);
			    	myDict1 = null;
			    	myDict1 = new Dictionary();
			    	//docInfoList1 = null;
			    	//docInfoList1 = new ArrayList<Document>();
			    	
			        System.out.println("The time5 elapsed: " + Duration.ofMillis(dtLength5).getSeconds());
			        System.out.println("The time6 elapsed: " + Duration.ofMillis(dtLength6).getSeconds());
			        System.out.println("The time7 elapsed: " + Duration.ofMillis(dtLength7).getSeconds());
			        dtLength5 = 0;
			        dtLength6 = 0;
			        dtLength7 = 0;
			    }
			}
			
			if (i % 10000 > 0)
			{
				//docInfoList1 = LoadDocTermInfo(myDict1, docInfoList1);
				WriteIndexSimple(projectpath + "DictIndex_" + (i / 10000 + 1), myDict1);
			    //WriteDocInfo(projectpath + "docInfo_" + (i / 10000 + 1), docInfoList1);
		    	myDict1 = null;
		    	myDict1 = new Dictionary();
		    	//docInfoList1 = null;
		    	//docInfoList1 = new ArrayList<Document>();
			}
		}
		catch (Exception e)
		{
			System.out.print("\nerror occur when reading file " + "content and url" + "\n" + e.getMessage());
		}
		finally
		{
			if (br != null)
			{
				try
				{
				    br.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + "content and url" + "\n" + e.getMessage());
				}
			}
		}
		
		System.out.println("Write complete!");
    }
    
    public static void BuildIndex4()
    {
		Instant dtStart = Instant.now();
		Instant dtInstant1;
		Instant dtInstant2;
		Instant dtEnd;
		long dtLength1 = 0;
		long dtLength2 = 0;
		long dtLength3 = 0;
		long dtLength4 = 0;
		long dtLength5 = 0;
		long dtLength6 = 0;
		long dtLength7 = 0;
		long dtLength8 = 0;
    	
		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		String projectpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//";
		String resourcepath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//resourcesIR//";
		//List<String> stopwords = Tokenizer.GetStopWords("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//resourcesIR//stopwords");
		//File sourcefile = new File(sourcefilepath);

		Dict_E dict_e = new Dict_E();
		dict_e.LoadQuerySupport();
		
		Dictionary myDict1 = new Dictionary();
		List<Document> docInfoList1 = new ArrayList<Document>();
		int max_tf1 = 0;

		List<String> tokens = new LinkedList<String>();
		String text = "";
		BufferedReader br = null;
	
		int i = 1;
		
		try
		{
			br = new BufferedReader(new FileReader(projectpath + "webtext"));
			text = br.readLine();
			while (text != null) 
			{
				dtInstant1 = Instant.now();
				
				tokens = new LinkedList<String>();
				tokens = GetTermsFromFile_Stemming(new StringBuilder(text), dict_e); //without stemmatization, with forms kept

	            //List<String> tokens1 = lemmatizer.Lemmatize(rawtokens, false); //This step is extremely slow
				//tokens1 = Tokenizer.FilterStopWords(tokens1, stopwords);  // lemmatize raw tokens before filtering stop words
				dtInstant2 = Instant.now();
				dtLength2 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
				max_tf1 = Tokenizer.CountMaxTermFreq(tokens);  // count maximum term frequency in a document after removing stop words
				Dictionary.AddDictTerms(myDict1, tokens, i);
				dtInstant2 = Instant.now();
				dtLength3 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
//				Document newdoc1 = new Document();
//				newdoc1.ID = i;
//				newdoc1.max_tf = max_tf1;
//				newdoc1.linkstr = GetLinkStr(contentLine);
//				docInfoList1.add(newdoc1);
				
				text = br.readLine();
			    i++;
				dtInstant2 = Instant.now();
				dtLength4 += Duration.between(dtInstant1, dtInstant2).toMillis();
				dtInstant1 = Instant.now();
			    if (i % 10 == 0)
			    {
			    	System.out.print(".");
			    }
			    
			    if (i % 1000 == 0)
			    {
			    	System.out.println("\n" + i);
			        System.out.println("The time1 elapsed: " + Duration.ofMillis(dtLength1).getSeconds());
			        System.out.println("The time2 elapsed: " + Duration.ofMillis(dtLength2).getSeconds());
			        System.out.println("The time3 elapsed: " + Duration.ofMillis(dtLength3).getSeconds());
			        System.out.println("The time4 elapsed: " + Duration.ofMillis(dtLength4).getSeconds());
			    }
			    
			    if (i % 10000 == 0)
			    {
					dtInstant2 = Instant.now();
					dtLength5 += Duration.between(dtInstant1, dtInstant2).toMillis();
					dtInstant1 = Instant.now();
			    	//docInfoList1 = LoadDocTermInfo(myDict1, docInfoList1);
					dtInstant2 = Instant.now();
					dtLength6 += Duration.between(dtInstant1, dtInstant2).toMillis();
					dtInstant1 = Instant.now();
					WriteIndexSimple(projectpath + "DictIndex_" + (i / 10000), myDict1);
					dtInstant2 = Instant.now();
					dtLength7 += Duration.between(dtInstant1, dtInstant2).toMillis();
			    	//WriteDocInfo(projectpath + "docInfo_" + (i / 10000), docInfoList1);
			    	myDict1 = null;
			    	myDict1 = new Dictionary();
			    	//docInfoList1 = null;
			    	//docInfoList1 = new ArrayList<Document>();
			    	
			        System.out.println("The time5 elapsed: " + Duration.ofMillis(dtLength5).getSeconds());
			        System.out.println("The time6 elapsed: " + Duration.ofMillis(dtLength6).getSeconds());
			        System.out.println("The time7 elapsed: " + Duration.ofMillis(dtLength7).getSeconds());
			        dtLength5 = 0;
			        dtLength6 = 0;
			        dtLength7 = 0;
			    }
			}
			
			if (i % 10000 > 0)
			{
				//docInfoList1 = LoadDocTermInfo(myDict1, docInfoList1);
				WriteIndexSimple(projectpath + "DictIndex_" + (i / 10000 + 1), myDict1);
			    //WriteDocInfo(projectpath + "docInfo_" + (i / 10000 + 1), docInfoList1);
		    	myDict1 = null;
		    	myDict1 = new Dictionary();
		    	//docInfoList1 = null;
		    	//docInfoList1 = new ArrayList<Document>();
			}
		}
		catch (Exception e)
		{
			System.out.print("\nerror occur when reading file " + "content and url" + "\n" + e.getMessage());
		}
		finally
		{
			if (br != null)
			{
				try
				{
				    br.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + "content and url" + "\n" + e.getMessage());
				}
			}
		}
		
		System.out.println("Write complete!");
    }
    
    public static List<String> LoadWebTexts()
    {  	
		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		String projectpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//";
		String contentLine = "";
		BufferedReader br = null;
	
		List<String> texts = new LinkedList<String>();
		int i = 1;
		
		try
		{
			br = new BufferedReader(new FileReader(projectpath + "webtext"));
			contentLine = br.readLine();
			while (contentLine != null)
			{
				texts.add(contentLine);
				
			    contentLine = br.readLine();
			    i++;
			    if (i % 10 == 0)
			    {
			    	System.out.print(i + " ");
			    }
			    
			    if (i % 1000 == 0)
			    {
			    	System.out.println("\n" + i);
			    }
			    
			    if (i > 80000)
			    {
			    	break;
			    }
			}
		}
		catch (Exception e)
		{
			System.out.print("\nerror occur when reading file " + "content and url" + "\n" + e.getMessage());
		}
		finally
		{
			if (br != null)
			{
				try
				{
				    br.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + "content and url" + "\n" + e.getMessage());
				}
			}
		}
		
		System.out.println("Load complete!");
		
		return texts;
    }
    
    public static void WriteWebText_Separate()
    {
		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		String projectpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//";
		String docpath = "F://text//Docs//";

		String contentLine = "";
		BufferedReader br = null;
		BufferedWriter wr = null;

		String text = "";
		String path = "";
		int i = 1;
		//File file;
		try
		{
			br = new BufferedReader(new FileReader(projectpath + "content and url"));
			//wr = new BufferedWriter(new FileWriter(projectpath + "DocIndex//" + i));
			contentLine = br.readLine();
			while (contentLine != null)
			{
				text = GetText(contentLine);
				path = docpath + Common.GetFolder(i);
				//System.out.println(path);
				Common.MkDir(path);
//				file = new File(path);
//				if (!file.exists())
//				{
//					//file.mkdirs();
//	        		file.getParentFile().mkdirs();
//	        		file.createNewFile();
//				}
				
				wr = new BufferedWriter(new FileWriter(path + i));
				wr.write(text);
				wr.flush();
				wr.close();

			    contentLine = br.readLine();
			    i++;

			    if (i % 10 == 0)
			    {
			    	System.out.print(i + " ");
			    }
			    
			    if (i % 1000 == 0)
			    {
			    	System.out.println("\n" + i);
			    }
			}
		}
		catch (Exception e)
		{
			System.out.print("\nerror occur when reading file " + "content and url" + "\n" + e.getMessage());
		}
		finally
		{
			if (br != null)
			{
				try
				{
				    br.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + "content and url" + "\n" + e.getMessage());
				}
			}
			
			if (wr != null)
			{
				try
				{
				    wr.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + "webtext" + "\n" + e.getMessage());
				}
			}
		}
		
		System.out.println("Write complete!");
    }
    
    public static void WriteWebText()
    {
		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		String projectpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//";

		String contentLine = "";
		BufferedReader br = null;
		BufferedWriter wr = null;

		String text = "";
		int i = 1;
		
		try
		{
			br = new BufferedReader(new FileReader(projectpath + "content and url"));
			wr = new BufferedWriter(new FileWriter(projectpath + "webtext"));
			contentLine = br.readLine();
			while (contentLine != null)
			{
				text = GetText(contentLine);
				wr.write(text);
				wr.newLine();
				wr.flush();

			    contentLine = br.readLine();
			    i++;

			    if (i % 10 == 0)
			    {
			    	System.out.print(i + " ");
			    }
			    
			    if (i % 1000 == 0)
			    {
			    	System.out.println("\n" + i);
			    }
			}
		}
		catch (Exception e)
		{
			System.out.print("\nerror occur when reading file " + "content and url" + "\n" + e.getMessage());
		}
		finally
		{
			if (br != null)
			{
				try
				{
				    br.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + "content and url" + "\n" + e.getMessage());
				}
			}
			
			if (wr != null)
			{
				try
				{
				    wr.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + "webtext" + "\n" + e.getMessage());
				}
			}
		}
		
		System.out.println("Write complete!");
    }
    
    public static void WriteLink()
    {
		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		String projectpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//";

		String contentLine = "";
		BufferedReader br = null;
		BufferedWriter wr = null;

		String text = "";
		int i = 1;
		
		try
		{
			br = new BufferedReader(new FileReader(projectpath + "content and url"));
			wr = new BufferedWriter(new FileWriter(projectpath + "links"));
			contentLine = br.readLine();
			while (contentLine != null)
			{
				text = GetLinkStr(contentLine);
				wr.write(text);
				wr.newLine();
				wr.flush();

			    contentLine = br.readLine();
			    i++;

			    if (i % 10 == 0)
			    {
			    	System.out.print(i + " ");
			    }
			    
			    if (i % 1000 == 0)
			    {
			    	System.out.println("\n" + i);
			    }
			}
		}
		catch (Exception e)
		{
			System.out.print("\nerror occur when reading file " + "content and url" + "\n" + e.getMessage());
		}
		finally
		{
			if (br != null)
			{
				try
				{
				    br.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + "content and url" + "\n" + e.getMessage());
				}
			}
			
			if (wr != null)
			{
				try
				{
				    wr.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + "webtext" + "\n" + e.getMessage());
				}
			}
		}
		
		System.out.println("Write complete!");
    }
    
	public static void WriteIndex(String path, Dictionary myDict)
	{
		ByteArrayOutputStream bytestream;
        try 
        {
        	bytestream = new ByteArrayOutputStream();
            Term currentTerm;
    		for(int i= 0; i < myDict.Size(); i++)
    		{
    			currentTerm = myDict.GetTerm(i);

    			bytestream.write(currentTerm.str.getBytes());
    			bytestream.write(" ".getBytes());
    			bytestream.write(Arrays.toString(currentTerm.wordforms.toArray()).replace("[", "").replace("]", "").replace(" ", "").getBytes());
    			bytestream.write(" ".getBytes());
    			bytestream.write(String.valueOf(currentTerm.dfreq).getBytes()); //write df
    			bytestream.write(" ".getBytes());
    			bytestream.write(Arrays.toString(currentTerm.postings.toArray()).replace("[", "").replace("]", "").replace(" ", "").getBytes());
    			bytestream.write(" ".getBytes());
    			bytestream.write(Arrays.toString(currentTerm.tfreqs.toArray()).replace("[", "").replace("]", "").replace(" ", "").getBytes());
    			bytestream.write(" ".getBytes());
    			for(int j = 0; j < currentTerm.positions2.size(); j++)
    			{
    				if (j > 0)
    				{
    					bytestream.write(";".getBytes());
    				}
    				bytestream.write(Arrays.toString(currentTerm.positions2.get(j).toArray()).replace("[", "").replace("]", "").replace(" ", "").getBytes());
    			}
    			//bytestream.write(Arrays.toString(currentTerm.positions.toArray()).replace("[", "").replace("]", "").replace(" ", "").getBytes());
    			bytestream.write("\n".getBytes());
    		}
    		
    		byte[] indexbytes = bytestream.toByteArray();
    		bytestream.close();
    		
    		System.out.println("size of index: " + indexbytes.length);
    		System.out.println("Number of inverted lists: " + myDict.Size());
    		
    		WriteFile(path, indexbytes);
    		
            }
            catch(IOException e)
            {
            	e.printStackTrace();
            }
	}
	
	public static void WriteIndexSimple(String path, Dictionary myDict)
	{
		ByteArrayOutputStream bytestream;
        try 
        {
        	bytestream = new ByteArrayOutputStream();
            Term currentTerm;
    		for(int i= 0; i < myDict.Size(); i++)
    		{
    			currentTerm = myDict.GetTerm(i);

    			bytestream.write(currentTerm.str.getBytes());
    			bytestream.write(" ".getBytes());
    			bytestream.write(String.valueOf(currentTerm.dfreq).getBytes()); //write df
    			bytestream.write(" ".getBytes());
    			bytestream.write(Arrays.toString(currentTerm.postings.toArray()).replace("[", "").replace("]", "").replace(" ", "").getBytes());
    			bytestream.write(" ".getBytes());
    			bytestream.write(Arrays.toString(currentTerm.tfreqs.toArray()).replace("[", "").replace("]", "").replace(" ", "").getBytes());
    			bytestream.write("\n".getBytes());
    		}
    		
    		byte[] indexbytes = bytestream.toByteArray();
    		bytestream.close();
    		
    		System.out.println("size of index: " + indexbytes.length);
    		System.out.println("Number of inverted lists: " + myDict.Size());
    		
    		WriteFile(path, indexbytes);
    		
            }
            catch(IOException e)
            {
            	e.printStackTrace();
            }
	}
    
	public static void WriteDocInfo(String path, List<Document> docList)
	{
		ByteArrayOutputStream bytestream;
        try 
        {
        	bytestream = new ByteArrayOutputStream();

            Document currentDoc;
    		for(int i= 0; i < docList.size(); i++)
    		{
    			currentDoc = docList.get(i);
    			//byte[] strbytes = currentDoc.ID.toString()

    			bytestream.write(String.valueOf(currentDoc.ID).getBytes()); //write document ID
    			bytestream.write(" ".getBytes());
    			bytestream.write(String.valueOf(currentDoc.max_tf).getBytes()); //write max_ft of the current docid
    			bytestream.write(" ".getBytes());
    			bytestream.write(String.valueOf(currentDoc.linkstr).getBytes());
    			bytestream.write(" ".getBytes());
    			bytestream.write(Arrays.toString(currentDoc.dict_termids.toArray()).replace("[", "").replace("]", "").replace(" ", "").getBytes());
    			bytestream.write(" ".getBytes());
    			bytestream.write(Arrays.toString(currentDoc.dict_tfs.toArray()).replace("[", "").replace("]", "").replace(" ", "").getBytes());
    			bytestream.write(" ".getBytes());
    			for(int j = 0; j < currentDoc.dict_tpositions2.size(); j++)
    			{
    				if (j > 0)
    				{
    					bytestream.write(";".getBytes());
    				}
    				bytestream.write(Arrays.toString(currentDoc.dict_tpositions2.get(j).toArray()).replace("[", "").replace("]", "").replace(" ", "").getBytes());
    			}
    			
     			bytestream.write("\n".getBytes());
    		}
    		
    		byte[] indexbytes = bytestream.toByteArray();
    		bytestream.close();
    		
    		System.out.println("size of document information index: " + indexbytes.length);
    		
    		WriteFile(path, indexbytes);
    		
    		bytestream.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
	}
	
	public static void WriteFile(String filepath, byte[] data)
	{
        try
        {
        	File file = new File(filepath);
        	if (!file.exists())
        	{
        		file.getParentFile().mkdirs();
        		file.createNewFile();
        	}
        	
        	Path path = Paths.get(filepath);
            // truncate and overwrite an existing file, or create the file if
            // it doesn't initially exist
            DataOutputStream stream = new DataOutputStream(
              new BufferedOutputStream(
                    Files.newOutputStream(path)
                )
            );
            for(int i = 0; i<data.length; i++){
                stream.writeByte(data[i]);
            }
            
            stream.close();
        } 
        catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
	}
	
	public static Dictionary ReadIndexSimple(String folderpath)
	{
		Instant dtStart = Instant.now();
		Instant dtInstant0;
		Instant dtInstant1;
		Instant dtInstant2;
		Instant dtEnd;
		long dtLength1 = 0;
		long dtLength2 = 0;
		long dtLength3 = 0;
		long dtLength4 = 0;
		long dtLength5 = 0;
		long dtLength6 = 0;
		long dtLength7 = 0;
		long dtLength8 = 0;
		
		Dictionary dict = new Dictionary();
		File folder = new File(folderpath);
		File[] allFiles = folder.listFiles();
		String[] items;
		List<String> filelines = new LinkedList<String>();
		for(int i = 0; i <= allFiles.length; i++)
		{
			dtInstant1 = Instant.now();
			Dictionary tempdict = new Dictionary();
			filelines = Common.ReadFileLine(allFiles[i].getPath(), true);
			dtInstant2 = Instant.now();
			dtLength1 += Duration.between(dtInstant1, dtInstant2).toMillis();
			dtInstant0 = Instant.now();
			for(int j = 0; j < filelines.size(); j++)
			{
				dtInstant1 = Instant.now();
				items = filelines.get(j).split(" ");
				dtInstant2 = Instant.now();
				dtLength3 += Duration.between(dtInstant1, dtInstant2).toMillis();
				if (items.length == 4)
				{
					dtInstant1 = Instant.now();
					Term term = new Term();
					term.str = items[0];
					term.dfreq = Integer.valueOf(items[1]);
					dtInstant2 = Instant.now();
					dtLength4 += Duration.between(dtInstant1, dtInstant2).toMillis();
					dtInstant1 = Instant.now();
					term.postings = Common.StrToListInt(items[2]);
					term.tfreqs = Common.StrToListInt(items[3]);
					dtInstant2 = Instant.now();
					dtLength5 += Duration.between(dtInstant1, dtInstant2).toMillis();
					tempdict.Add(term);
				}
				else
				{
					//int test = 0;
				}
			}
			dtInstant2 = Instant.now();
			dtLength6 += Duration.between(dtInstant0, dtInstant2).toMillis();
			dtInstant1 = Instant.now();
		    dict.AddDict(tempdict);
			dtInstant2 = Instant.now();
			dtLength7 += Duration.between(dtInstant1, dtInstant2).toMillis();
		    System.out.println("dict " + i + " merged");
	        System.out.println("The time1 elapsed: " + Duration.ofMillis(dtLength1).getSeconds());
	        System.out.println("The time2 elapsed: " + Duration.ofMillis(dtLength2).getSeconds());
	        System.out.println("The time3 elapsed: " + Duration.ofMillis(dtLength3).getSeconds());
	        System.out.println("The time4 elapsed: " + Duration.ofMillis(dtLength4).getSeconds());
	        System.out.println("The time5 elapsed: " + Duration.ofMillis(dtLength5).getSeconds());
	        System.out.println("The time6 elapsed: " + Duration.ofMillis(dtLength6).getSeconds());
	        System.out.println("The time7 elapsed: " + Duration.ofMillis(dtLength7).getSeconds());
		    
		}
		return dict;
	}
	
	public static List<Dictionary> ReadIndexSimple_Split(String folderpath)
	{
		Instant dtStart = Instant.now();
		Instant dtInstant0;
		Instant dtInstant1;
		Instant dtInstant2;
		Instant dtEnd;
		long dtLength1 = 0;
		long dtLength2 = 0;
		long dtLength3 = 0;
		long dtLength4 = 0;
		long dtLength5 = 0;
		long dtLength6 = 0;
		long dtLength7 = 0;
		long dtLength8 = 0;
		
		List<Dictionary> dictlist = new LinkedList<Dictionary>();
		//Dictionary dict = new Dictionary();
		File folder = new File(folderpath);
		File[] allFiles = folder.listFiles();
		String[] items;
		List<String> filelines = new LinkedList<String>();
		for(int i = 0; i <= allFiles.length; i++)
		{
			dtInstant1 = Instant.now();
			Dictionary tempdict = new Dictionary();
			filelines = Common.ReadFileLine(allFiles[i].getPath(), true);
			dtInstant2 = Instant.now();
			dtLength1 += Duration.between(dtInstant1, dtInstant2).toMillis();
			dtInstant0 = Instant.now();
			for(int j = 0; j < filelines.size(); j++)
			{
				dtInstant1 = Instant.now();
				items = filelines.get(j).split(" ");
				dtInstant2 = Instant.now();
				dtLength3 += Duration.between(dtInstant1, dtInstant2).toMillis();
				if (items.length == 4)
				{
					dtInstant1 = Instant.now();
					Term term = new Term();
					term.str = items[0];
					term.dfreq = Integer.valueOf(items[1]);
					dtInstant2 = Instant.now();
					dtLength4 += Duration.between(dtInstant1, dtInstant2).toMillis();
					dtInstant1 = Instant.now();
					term.postings = Common.StrToListInt(items[2]);
					term.tfreqs = Common.StrToListInt(items[3]);
					dtInstant2 = Instant.now();
					dtLength5 += Duration.between(dtInstant1, dtInstant2).toMillis();
					tempdict.Add(term);
				}
				else
				{
					//int test = 0;
				}
			}
			dtInstant2 = Instant.now();
			dtLength6 += Duration.between(dtInstant0, dtInstant2).toMillis();
			dtInstant1 = Instant.now();
		    //dict.AddDict(tempdict);
			dictlist.add(tempdict);
			dtInstant2 = Instant.now();
			dtLength7 += Duration.between(dtInstant1, dtInstant2).toMillis();
		    System.out.println("dict " + i + " merged");
	        System.out.println("The time1 elapsed: " + Duration.ofMillis(dtLength1).getSeconds());
	        System.out.println("The time2 elapsed: " + Duration.ofMillis(dtLength2).getSeconds());
	        System.out.println("The time3 elapsed: " + Duration.ofMillis(dtLength3).getSeconds());
	        System.out.println("The time4 elapsed: " + Duration.ofMillis(dtLength4).getSeconds());
	        System.out.println("The time5 elapsed: " + Duration.ofMillis(dtLength5).getSeconds());
	        System.out.println("The time6 elapsed: " + Duration.ofMillis(dtLength6).getSeconds());
	        System.out.println("The time7 elapsed: " + Duration.ofMillis(dtLength7).getSeconds());
	        
	        if (i > 4)
	        {
	        	break;
	        }
		    
		}
		return dictlist;
	}
	
	public static List<Document> ReadDocInfo(String path)
	{
		List<Document> docInfoList = new ArrayList<Document>();
		File file = new File(path);
		DataInputStream reader = null;
		FileInputStream fileinput = null;
		String filename = file.getName();
		
		try
		{
			fileinput = new FileInputStream(file);
		    reader = new DataInputStream(fileinput);
		    
		    int num = 0;
		    
		    Document doc = new Document();
		    int readcount = 0;
		    while(true)
		    {
		    
		    	num = reader.readInt();
		    	readcount += 1;
		    	if (num == -1)  // line break -1 set when writing a file
		    	{
		    		
		    		docInfoList.add(doc);
		    		doc = new Document();
		    		readcount = 0;
		    		//doc.ID = reader.readInt();
		    		//doc.max_tf = reader.readInt();
		    		//doc.doclen = reader.readInt();    		
		    	}
		    	else
		    	{
		    		if (readcount == 1)
		    		{
		    			doc.ID = num;
		    		}
		    		else if (readcount == 2)
		    		{
		    			doc.max_tf = num;
		    		}
		    		else if (readcount == 3)
		    		{
		    			doc.doclen = num;
		    		}
		    	}
		    }
		}
		catch (Exception e)
		{
			System.out.print("\n docInfoList read of size " + docInfoList.size() + "\n");
			// System.out.print("\nerror occur when reading file " + filename + "\n" + e.getMessage());
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
			if (fileinput != null)
			{
				try
				{
					fileinput.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + filename + "\n" + e.getMessage());
				}
			}
		}
		
		return docInfoList;
	}
	
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
	
	public static List<Token> GetTermsFromFileWithPosition_Stem(StringBuilder text)
	{
		List<Token> tokens = Tokenizer.TokenizeWithPosition(text, false, true);  // without stemmatization

        //List<Token> tokens = query.lemmatizer.LemmatizeToken(rawtokens, false);
		//tokens = Tokenizer.FilterStopWordTokens(tokens, query.stopwords);  // lemmatize raw tokens before filtering stop words
        tokens = Tokenizer.StemTokensWithForm(tokens);
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
				if (minscore == 0.0 && i == 0)  // i==0 should be added, because the actual score can be zero
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
	
	public static double GetAvgDocLen(List<Document> docList)
	{
		int totaldoclen = 0;
		for(int i = 0; i < docList.size(); i++)
		{
			totaldoclen += docList.get(i).doclen;
		}
		
		return (double)totaldoclen / (double)docList.size();
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
	
	public static String GetDocTerms(int docid, Query query)
	{
		List<Integer> termids = query.docInfoList.get(docid - 1).dict_termids;
		List<Integer> termfreqs = query.docInfoList.get(docid - 1).dict_tfs;
		
		StringBuilder info = new StringBuilder();
		for(int i = 0; i < termids.size(); i++)
		{
			if (info.length() > 0)
			{
				info.append("; ");
			}
			
			Term term = query.dict.GetTerm(termids.get(i));
			info.append(term.str + " (" + termfreqs.get(i) + ")");
		}
		
		return info.toString();
	}
	
	public static List<Integer> GetDocTermPositions(Term term, int postingindex)
	{
		List<Integer> positions = term.positions2.get(postingindex);
//		int totaloccurrence = 0;
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
		
//		int totaloccurrence = 0;
//		for(int i = 0; i < freqs.size(); i++)
//		{
//			if (i == postingindex)
//			{
//				for(int j = totaloccurrence; j < totaloccurrence + freqs.get(i); j++)
//				{
//					mypositions.add(positions.get(j));
//				}
//			}
//			
//			totaloccurrence += freqs.get(i);
//		}
		
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
				relatedterm = currentterm.wordforms.get(0);
			}
		}
		
		if (maxscore != 0.0)
		{
		    //System.out.println(term.str + " --> " + relatedterm + "(" + maxscore + ")");
		}
		
		return relatedterm;
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
	
	public static Dictionary GetMetricDict(List<Document> localdoclist, Query query)
	{
		Dictionary metrictermdict = new Dictionary();
		for(int i = 0; i < localdoclist.size(); i++)
		{
			int docid = localdoclist.get(i).ID;
			List<Integer> termids = query.docInfoList.get(docid - 1).dict_termids;
			List<Integer> termfreqs = query.docInfoList.get(docid - 1).dict_tfs;
			//List<Integer> termpositions = query.docInfoList.get(docid - 1).dict_tpositions;
			List<List<Integer>> termpositions2 = query.docInfoList.get(docid - 1).dict_tpositions2;
			for(int j = 0; j < termids.size(); j++)
			{
				Term term = query.dict.GetTerm(termids.get(j));
				
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
	
	public static Dictionary GetAssociationDict(List<Document> localdoclist, Query query)
	{
		Dictionary associatedtermdict = new Dictionary();
		
		for(int i = 0; i < localdoclist.size(); i++)
		{
			int docid = localdoclist.get(i).ID;
			List<Integer> termids = query.docInfoList.get(docid - 1).dict_termids;
			List<Integer> termfreqs = query.docInfoList.get(docid - 1).dict_tfs;
			
			for(int j = 0; j < termids.size(); j++)
			{
				//Term dictterm = dict.GetTerm(termids.get(j));
				associatedtermdict.AddTermMatrix(query.dict.GetTerm(termids.get(j)).str, termfreqs.get(j), docid, i, localdoclist.size());
			}
		}

		return associatedtermdict;
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
		//System.out.println(" ");
		

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
	
	public static Dictionary GetQueryDict(String querystr, int queryid, Query query)
	{
		Dictionary queryDict = new Dictionary();
		List<String> termlist = GetQueryTermList(querystr, query);
		Dictionary.AddDictTerms(queryDict, termlist, queryid);
		
		return queryDict;
	}
	
	public static Dictionary GetQueryDict_Stem(String querystr, int queryid, Query query)
	{
		Dictionary queryDict = new Dictionary();
		List<String> termlist = GetQueryTermList(querystr, query);
		List<String> stemlist = Tokenizer.StemTokens(termlist);
		Dictionary.AddDictTerms(queryDict, stemlist, queryid);
		
		return queryDict;
	}
	
	public static List<String> GetQueryTermList(String querysent, Query query)
	{
		StringBuilder querysentbuilder = new StringBuilder(querysent);
		List<String> termlist = Tokenizer.Tokenize(querysentbuilder, false);
		termlist = Tokenizer.FilterStopWords(termlist, query.stopwords);
		termlist = query.lemmatizer.Lemmatize(termlist, false);

		return termlist;
	}
	
	public static List<Document> GetQueryResult(Dictionary currentqueryDict, Query query)
	{
		int tf;
		int maxtf;
		int collectionsize = query.docInfoList.size();
		int df;
		double weight;
		double veclength_query = 0;
		double veclength_doc = 0;
		float[] queryvector = new float[currentqueryDict.Size()];
		float[] queryvector2 = new float[currentqueryDict.Size()];
		int[] dfs = new int[currentqueryDict.Size()];
		List<Document> doclist = new ArrayList<Document>();
		doclist.addAll(query.docInfoList);
		for(int j = 0; j < doclist.size(); j++)
		{
			doclist.get(j).vector = new float[currentqueryDict.Size()];
			doclist.get(j).tfs = new int[currentqueryDict.Size()];
			doclist.get(j).dfs = new int[currentqueryDict.Size()];
			doclist.get(j).score = 0;
		}
		maxtf = currentqueryDict.GetMaxTermFreq();
		for(int j = 0; j < currentqueryDict.Size(); j++)
		{
			String querytermstr = currentqueryDict.GetTerm(j).str;
			int tfreq = currentqueryDict.GetTerm(j).tfreq;
			queryvector[j] = 0;
			queryvector2[j] = 0;
			
			Term term = query.dict.GetTerm(querytermstr);
			
			if (term == null)
			{
				queryvector[j] = (float)Common.Round(GetWeight(tfreq, maxtf, collectionsize, 1), 4);  // at least one document is there as the query
				dfs[j] = 1;
			}
			else
			{
				List<Integer> postings = term.postings;
				List<Integer> tfreqs = term.tfreqs;
				df = term.dfreq;
				
				queryvector[j] = (float)Common.Round(GetWeight(tfreq, maxtf, collectionsize, df + 1), 4);  // consider a query as a document, "+ 1" to differentiate with the situation when no documents in the collection contain this term
				dfs[j] = df + 1;
				
				for(int k = 0; k < postings.size(); k++)
				{
					tf = tfreqs.get(k);
					maxtf = doclist.get(postings.get(k) - 1).max_tf;
					weight = GetWeight(tf, maxtf, collectionsize, df);
					
					doclist.get(postings.get(k) - 1).tfs[j] = tf;
					doclist.get(postings.get(k) - 1).dfs[j] = df;
					doclist.get(postings.get(k) - 1).vector[j] = (float)Common.Round(weight, 4);
					doclist.get(postings.get(k) - 1).score += queryvector[j] * weight; // add unnormailized cosine similarity
				}
			}
		}
		
		veclength_query = GetVectorLength(queryvector);
		
		for(int j = 0; j < doclist.size(); j++)
		{
			if (doclist.get(j).score != 0.0)
			{
				veclength_doc = GetVectorLength(doclist.get(j).vector);
				double weightedscore = doclist.get(j).score / (veclength_query * veclength_doc);
				doclist.get(j).score = Common.Round(weightedscore, 4);
			}
		}
		

		//PrintDictStr(currentqueryDict, queryvector, dfs);
		//PrintDictStr(currentqueryDict, queryvector2, dfs);
		
		List<Document> sorteddoclist = GetTopScoredDocuments(doclist, 20, false);
		
		return sorteddoclist;
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
		//Dictionary.PrintDict2(query.dict);
		
		List<Sentence> queries = GetQueries(folderpath1 + "hw3.queries");
		
		System.out.println("queries:");
		System.out.println(" ");
		
		for(int i = 0; i < queries.size(); i++)
		{
			Dictionary currentqueryDict = GetQueryDict(queries.get(i).str, queries.get(i).id, query);
			Dictionary currentqueryDict_Stem = GetQueryDict_Stem(queries.get(i).str, queries.get(i).id, query);
			//for each term in a query sentence, let its frequency be the vector value in its dimension
			System.out.println("Query " + queries.get(i).id + ":");
			System.out.println(" ");
			System.out.println("Query string: " + queries.get(i).str);

			List<Document> sorteddoclist = GetQueryResult(currentqueryDict, query);
			
			//Dictionary associationDict = GetAssociationDict(sorteddoclist, query);  // To get local clusters from Dictionary loaded initially
			Dictionary associationDict = GetAssociationDict(sorteddoclist, folderpath3, query); // To get local clusters by reading query result files
            List<String> expandedterms1 = GetExpandedTerms(currentqueryDict, associationDict, 1);
            
            //Dictionary metricDict = GetMetricDict(sorteddoclist, query);  // To get local clusters from Dictionary loaded initially
            Dictionary metricDict = GetMetricDict_Stem(sorteddoclist, folderpath3, query);  // To get local clusters by reading query result files
            List<String> expandedterms2 = GetExpandedTerms(currentqueryDict_Stem, metricDict, 2);
            
            Dictionary scalarDict = GetMetricMatrix(metricDict, false);
            List<String> expandedterms3 = GetExpandedTerms(currentqueryDict_Stem, scalarDict, 3);
            
            System.out.println();
            System.out.println("Query terms: " + Homework3.PrintDictTerms(currentqueryDict).toString());
			System.out.println("Association Expanded Terms: " + Arrays.toString(expandedterms1.toArray()));
			System.out.println("Metric Expanded Terms: " + Arrays.toString(expandedterms2.toArray()));
			System.out.println("Scalar Expanded Terms: " + Arrays.toString(expandedterms3.toArray()));
			System.out.println();

		}
		
	}
}
