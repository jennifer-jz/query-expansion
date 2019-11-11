package QueryE;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Dict_E {
	Dictionary dict = new Dictionary();
	List<Dictionary> dicts = new LinkedList<Dictionary>();
	List<String> webtexts = new LinkedList<String>();
	List<String> stopwords;
	List<String> links = new ArrayList<String>();
	String docfolder = "F://text//Docs//";
	String resourcepath = "Z://Œƒ∏Â//¡Ù—ß//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//source//Dict_sub//";
	
	public Dict_E()
	{
		webtexts = new LinkedList<String>();
		stopwords = new ArrayList<String>();
		dicts = new LinkedList<Dictionary>();
		links = new ArrayList<String>();
		
	}
	
	void LoadQuerySupport()
	{
		stopwords = Common.ReadFileLine(resourcepath + "stopwords", true);
		List<String> stemmedstopwords = new ArrayList<String>();
		for(int i = 0; i < stopwords.size(); i++)
		{
			String stopword = Tokenizer.StemToken(new StringBuilder(stopwords.get(i))).toString();
			if (stopword != stopwords.get(i))
			{
				stemmedstopwords.add(stopword);
			}
		}
		stopwords.addAll(stemmedstopwords);
		//stopwords = Tokenizer.GetStopWords(resourcepath + "stopwords");
	    
	    LoadLink();
	    
	    System.out.println("query support database loaded!");
	}
	
	public void LoadQuerySupportExam()
	{
		stopwords = Common.ReadFileLine(resourcepath + "stopwords", true);
		List<String> stemmedstopwords = new ArrayList<String>();
		for(int i = 0; i < stopwords.size(); i++)
		{
			String stopword = Tokenizer.StemToken(new StringBuilder(stopwords.get(i))).toString();
			if (stopword != stopwords.get(i))
			{
				stemmedstopwords.add(stopword);
			}
		}
		stopwords.addAll(stemmedstopwords);
		//stopwords = Tokenizer.GetStopWords(resourcepath + "stopwords");

	    System.out.println("query support database loaded!");
	}
	
	void LoadLink()
	{
		links = Common.ReadFileLine(resourcepath + "links", true);
	}
	
	int GetDocIDFromLink2(String linkstr)
	{
		int docid = 0;
		for(int i = 0; i < links.size(); i++)
		{
			if (links.get(i).compareTo(linkstr) == 0)
			{
				docid  = i + 1;
				break;
			}
		}
		return docid;
	}
}
