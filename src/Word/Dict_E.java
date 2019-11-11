package Word;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

public class Dict_E {

	Dictionary dict = new Dictionary();
	List<Dictionary> dicts = new LinkedList<Dictionary>();
	List<String> webtexts = new LinkedList<String>();
	List<String> stopwords;
	Lemmatizer lemmatizer;
	List<Document> docInfoList;
	List<String> links = new ArrayList<String>();
	String docfolder = "F://text//Docs//";
	String projectpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//";
	String resourcepath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//Dict_sub//";
	
	public Dict_E()
	{
		webtexts = new LinkedList<String>();
		stopwords = new ArrayList<String>();
		dicts = new LinkedList<Dictionary>();
		docInfoList = new LinkedList<Document>();
		links = new ArrayList<String>();
		
	}
	
	void LoadQuerySupport()
	{
		lemmatizer = new Lemmatizer();
		stopwords = Tokenizer.GetStopWords(resourcepath + "stopwords");
	    
	    LoadLink();
	    
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
	
	int GetDocIDFromLink(String linkstr)
	{
		linkstr = linkstr.trim();  // string forms should be normalized before adding.
		int index= -1;
		
		int starti = 0;
		int endi = links.size() - 1;
		
		while(starti <= endi)
		{
			int midi = (starti + endi) / 2;
			String midstr = links.get(midi);
			int isgreater = linkstr.compareTo(midstr);  //ignore cases when comparing with existing terms
			
			if (midi == starti)  //indicating endi - starti == 1 or endi == starti, which should just be the correct position to insert
			{
				if (isgreater == 0)  // term == termlist.get(starti).str;
				{
					index = starti;
					break;
				}
				else if (isgreater < 0)
				{
                    break;
				}
				else
				{
					if (endi == starti) // insert at the end of string at endi
					{
                        break;
					}
					else
					{
						isgreater = linkstr.compareTo(links.get(endi));
						if (isgreater == 0)
						{
							index = endi;
							break;
						}
						else
						{
							break;
						}
					}
				}
			}
			else
			{
				if (isgreater == 0)  // the exact match of string was found by chance.
				{
					index = midi;
					break;
				}
				else if (isgreater > 0)
				{
					starti = midi ;
				}
				else
				{
					endi = midi;
				}
			}
		}
		
		return index + 1;
	}
}
