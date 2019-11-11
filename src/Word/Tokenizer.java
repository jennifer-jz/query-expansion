package Word;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;

public class Tokenizer {
    
    public static File[] SortFilesByName(File[] filelist)
    {
    	File[] sortedfiles = new File[1400];

    	int length = "cranfield".length();
    	for(int i = 0; i < filelist.length; i++)
    	{
    		String filename = filelist[i].getName();
    		int docid = Integer.parseInt(filename.substring(length));
    		sortedfiles[docid - 1] = filelist[i];
    	}

    	return sortedfiles;
    }
    
	public static File[] GetAllFiles(String path)
	{
		File folder = new File(path);
		File[] allFiles = folder.listFiles();
		File[] filelist = SortFilesByName(allFiles); // Unix system does not read file by name
		
		return filelist;
	}
	
	public static StringBuilder FilterHTML(StringBuilder text)
	{
		StringBuilder newtext = new StringBuilder(text);
		
		int startIndex = newtext.indexOf("<", 0);
		while(startIndex >=0 && startIndex < newtext.length())  // startIndex < newtext.length() should be added to avoid endless loop (is it the case with java?)
		{
		    int endIndex = newtext.indexOf(">", startIndex) + 1;
		    if (endIndex >= 1)  // endIndex - 1 >= 0, to record the offset of after ">"
		    {
		    	newtext.delete(startIndex, endIndex);
		    	newtext.insert(startIndex, ' ');  // to keep at least a space between contents with different html lables.
		    	
		    	startIndex = newtext.indexOf("<", startIndex);
		    	//startIndex = endIndex - endIndex + startIndex;  startIndex should not be incremented because substring between < and > is deleted. 
		    }
		    else
		    {
		    	// To avoid abnormal instances
		        System.out.println("There is a < without > for text: " + newtext.toString());
		        break;
		    }
		}
		
		return newtext;
	}
	
	public static StringBuilder ReadFileText(File file)
	{
		StringBuilder fulltext = new StringBuilder();
		
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
		    		fulltext.append(" ");
		    	}
		    	else
		    	{
		    	    fulltext.append((char)num);
		    	}
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
		
		return fulltext;
	}
	
	// Tokens should be ordered to make possible statistics
	// Several possible data structures can be considered: hashtable, dictionary, or string list based on binary search
	// 1. Punctuations (including m/n dashes and hyphens), spaces and line breaks are eliminated, for use only in information retrieval
	// 2. Contiguous alphanumeric characters are considered as a token.
	// 3. Pure numbers are discarded.
	// and single characters???
	// 4. Strings are compared ignoring cases. All tokens were lowercased before being added to the token list.
	// 5. The generated token list is sorted alphabetically
	public static List<String> Tokenize(StringBuilder text, boolean IsStemmed, boolean Isalphabeticorder)
	{
		List<String> tokens = new ArrayList<String>();
		
		StringBuilder temptoken = new StringBuilder();
		char tempchar; 
		for(int i = 0; i < text.length(); i++)
		{
			tempchar = text.charAt(i);
			if (IsAlphanumeric(tempchar))
			{
				temptoken.append(tempchar);
			}
			else
			{
				if (temptoken.length() != 0)
				{
					if (!IsPureNumeric(temptoken))
					{
						if (IsStemmed)
						{
							temptoken = StemToken(temptoken);
						}
						
						if (!Isalphabeticorder)
						{
							tokens.add(temptoken.toString().toLowerCase());
						}
						else
						{
						    tokens = InsertToken(tokens, temptoken.toString().toLowerCase());
						}
					}
					//tokens.add(temptoken.toString());
					temptoken.setLength(0);  // will this clear all the content in temptoken?
				}
			}
		}
		
		if (temptoken.length() != 0)
		{
			if (!IsPureNumeric(temptoken))
			{
				if (IsStemmed)
				{
					temptoken = StemToken(temptoken);
				}
				
				if (!Isalphabeticorder)
				{
					tokens.add(temptoken.toString().toLowerCase());
				}
				else
				{
				    tokens = InsertToken(tokens, temptoken.toString().toLowerCase());
				}
			}
			//tokens.add(temptoken.toString());
			temptoken.setLength(0);  // will this clear all the content in temptoken?
		}
		
		
		return tokens;
	}
	
	public static List<String> Tokenize(StringBuilder text, boolean IsStemmed)
	{		
		return Tokenize(text, IsStemmed, false);
	}
	
	public static List<Token> TokenizeWithFormAndPosition(StringBuilder text)
	{
		List<Token> tokens = new ArrayList<Token>();
		
		StringBuilder temptoken = new StringBuilder();
		StringBuilder temptokenstem = new StringBuilder();
		char tempchar; 
		int position = 0;
		for(int i = 0; i < text.length(); i++)
		{
			tempchar = text.charAt(i);
			if (IsAlphanumeric(tempchar))
			{
				temptoken.append(tempchar);
			}
			else
			{
				if (temptoken.length() != 0)
				{
					if (!IsPureNumeric(temptoken))
					{
						temptokenstem = StemToken(temptoken);
					    tokens = InsertToken(tokens, temptokenstem.toString().toLowerCase(), temptoken.toString().toLowerCase(), position);
					    position += 1;
					}
					temptoken.setLength(0); 
				}
			}
		}
		
		if (temptoken.length() != 0)
		{
			if (!IsPureNumeric(temptoken))
			{
				temptokenstem = StemToken(temptoken);
			    tokens = InsertToken(tokens, temptokenstem.toString().toLowerCase(), temptoken.toString().toLowerCase(), position);
			    position += 1;
			}
			temptoken.setLength(0);
		}
		
		return tokens;
	}
	
	public static List<Token> TokenizeWithPosition(StringBuilder text, boolean IsStemmed, boolean Isalphabeticorder)
	{
		List<Token> tokens = new ArrayList<Token>();
		
		StringBuilder temptoken = new StringBuilder();
		char tempchar; 
		int position = 0;
		for(int i = 0; i < text.length(); i++)
		{
			tempchar = text.charAt(i);
			if (IsAlphanumeric(tempchar))
			{
				temptoken.append(tempchar);
			}
			else
			{
				if (temptoken.length() != 0)
				{
					if (!IsPureNumeric(temptoken))
					{
						if (IsStemmed)
						{
							temptoken = StemToken(temptoken);
						}
						
						if (!Isalphabeticorder)
						{
							Token token = new Token();
							token.str = temptoken.toString().toLowerCase();
							token.position = position;
							tokens.add(token);
							position += 1;
						}
						else
						{
						    tokens = InsertToken(tokens, temptoken.toString().toLowerCase(), position);
						    position += 1;
						}
					}
					//tokens.add(temptoken.toString());
					temptoken.setLength(0);  // will this clear all the content in temptoken?
				}
			}
		}
		
		if (temptoken.length() != 0)
		{
			if (!IsPureNumeric(temptoken))
			{
				if (IsStemmed)
				{
					temptoken = StemToken(temptoken);
				}
				
				if (!Isalphabeticorder)
				{
					Token token = new Token();
					token.str = temptoken.toString().toLowerCase();
					token.position = position;
					tokens.add(token);
					position += 1;
				}
				else
				{
				    tokens = InsertToken(tokens, temptoken.toString().toLowerCase(), position);
				    position += 1;
				}
			}
			//tokens.add(temptoken.toString());
			temptoken.setLength(0);  // will this clear all the content in temptoken?
		}
		
		return tokens;
	}
	
	// Porter Stemmer should be faster in my application if it proceeds with StringBuilder instead of char[]
	// But test shows its speed is fast enough for this task.
	public static StringBuilder StemToken(StringBuilder token)
	{
		
		Stemmer s = new Stemmer();
		for (int i = 0; i < token.length(); i++)
		{
		    s.add(token.charAt(i));
		}
		
		s.stem();
		
		StringBuilder stemmedtoken = new StringBuilder(s.toString());
		
		return stemmedtoken;
	}
	
	public static String StemToken(String token)
	{
		
		Stemmer s = new Stemmer();
		for (int i = 0; i < token.length(); i++)
		{
		    s.add(token.charAt(i));
		}
		
		s.stem();

		return s.toString();
	}
	
	public static List<String> StemTokens(List<String> tokens)
	{
		List<String> stemmedtokens = new ArrayList<String>();
		StringBuilder temp = new StringBuilder();
		for(int i = 0; i < tokens.size(); i++)
		{
			temp.setLength(0);
			temp.append(tokens.get(i));
			
			stemmedtokens.add(StemToken(temp).toString());
		}
		
		return stemmedtokens;
	}
	
	//This function is problematic, because stemming processing will result in merging of tokens
	public static List<Token> StemTokensWithForm(List<Token> tokens)
	{
		StringBuilder temp = new StringBuilder();
		String stemmedtoken = "";
		for(int i = 0; i < tokens.size(); i++)
		{
			String tokenstr = tokens.get(i).str;
			tokens.get(i).wordform = tokenstr;
			temp.setLength(0);
			temp.append(tokenstr);
			stemmedtoken = StemToken(temp.toString());

			if (stemmedtoken != temp.toString())
			{
				tokens.get(i).str = stemmedtoken;
			}
		}
		
		return tokens;
	}
	
	// Keep the token list sorted alphabatically
	// Each token is inserted in the an existing tokenlist based on binary search
	public static List<String> InsertToken(List<String> tokenlist, String token)
	{
		List<String> newtokenlist = tokenlist;
		
		if (newtokenlist.size() == 0)
		{
			newtokenlist.add(token);
			return newtokenlist;
		}
		
		int starti = 0;
		int endi = newtokenlist.size() - 1;
		
		while(starti <= endi)
		{
			int midi = (starti + endi) / 2;
			String midstr = newtokenlist.get(midi);
			int isgreater = token.compareToIgnoreCase(midstr);  //ignore cases when comparing two strings
			
			if (midi == starti)  //indicating endi - starti == 1 or endi == starti, which should just be the correct position to insert
			{
				if (isgreater <= 0)
				{
					newtokenlist.add(midi, token);
					break;
				}
				else
				{
					//if (endi < newtokenlist.size()) // It seems to be OK for java to add some object from an offset equaling its size()
					
					if (endi == starti) // insert at the end of string at endi
					{
				        newtokenlist.add(endi + 1, token);  
				        break;
					}
					else
					{
						isgreater = token.compareToIgnoreCase(newtokenlist.get(endi));
						if (isgreater <= 0)
						{
							newtokenlist.add(endi, token);  // token resides between
							break;
						}
						else
						{
							newtokenlist.add(endi + 1, token);  //insert at the end of string at endi
							break;
						}
					}
				}
			}
			else
			{
				if (isgreater == 0)  // the exact match of string was found by chance.
				{
					newtokenlist.add(midi, token);
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
		
		//System.out.print("\n");
		//System.out.print(String.join("|", newtokenlist));
		
		return newtokenlist;
	}
	
	public static List<Token> InsertToken(List<Token> tokenlist, Token token)
	{
		List<Token> newtokenlist = tokenlist;
		
		if (newtokenlist.size() == 0)
		{
			newtokenlist.add(token);
			return newtokenlist;
		}
		
		int starti = 0;
		int endi = newtokenlist.size() - 1;
		
		while(starti <= endi)
		{
			int midi = (starti + endi) / 2;
			String midstr = newtokenlist.get(midi).str;
			int isgreater = token.str.compareToIgnoreCase(midstr);  //ignore cases when comparing two strings
			
			if (midi == starti)  //indicating endi - starti == 1 or endi == starti, which should just be the correct position to insert
			{
				if (isgreater <= 0)
				{
					newtokenlist.add(midi, token);
					break;
				}
				else
				{
					//if (endi < newtokenlist.size()) // It seems to be OK for java to add some object from an offset equaling its size()
					
					if (endi == starti) // insert at the end of string at endi
					{
				        newtokenlist.add(endi + 1, token);  
				        break;
					}
					else
					{
						isgreater = token.str.compareToIgnoreCase(newtokenlist.get(endi).str);
						if (isgreater <= 0)
						{
							newtokenlist.add(endi, token);  // token resides between
							break;
						}
						else
						{
							newtokenlist.add(endi + 1, token);  //insert at the end of string at endi
							break;
						}
					}
				}
			}
			else
			{
				if (isgreater == 0)  // the exact match of string was found by chance.
				{
					newtokenlist.add(midi, token);
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
		
		//System.out.print("\n");
		//System.out.print(String.join("|", newtokenlist));
		
		return newtokenlist;
	}
	
	public static List<Token> InsertToken(List<Token> tokenlist, String tokenstem, String tokenform, int position)
	{
		List<Token> newtokenlist = tokenlist;
		Token token = new Token();
		token.str = tokenstem;
		token.wordform = tokenform;
		token.position = position;
		
		newtokenlist = InsertToken(tokenlist, token);
		
		return newtokenlist;
	}
	
	public static List<Token> InsertToken(List<Token> tokenlist, String tokenstem, String tokenform)
	{
		List<Token> newtokenlist = tokenlist;
		Token token = new Token();
		token.str = tokenstem;
		token.wordform = tokenform;
		
		newtokenlist = InsertToken(tokenlist, token);
		
		return newtokenlist;
	}
	
	public static List<Token> InsertToken(List<Token> tokenlist, String tokenstr, int position)
	{
		List<Token> newtokenlist = tokenlist;
		Token token = new Token();
		token.str = tokenstr;
		token.position = position;
		
		newtokenlist = InsertToken(tokenlist, token);
		
		return newtokenlist;
	}
	
	//To make this function to update the input tokenlist, avoiding unnecessary creations of List<String>
	public static boolean InsertToken2(List<String> tokenlist, String token)
	{
		if (tokenlist.size() == 0)
		{
			tokenlist.add(token);
			return true;
		}
		
		int starti = 0;
		int endi = tokenlist.size() - 1;
		
		boolean succeed = false; 
		while(starti <= endi)
		{
			int midi = (starti + endi) / 2;
			String midstr = tokenlist.get(midi);
			int isgreater = token.compareToIgnoreCase(midstr);  //ignore cases when comparing two strings
			
			if (midi == starti)  //indicating endi - starti == 1 or endi == starti, which should just be the correct position to insert
			{
				if (isgreater <= 0)
				{
					tokenlist.add(midi, token);
					succeed = true;
					break;
				}
				else
				{
					//if (endi < newtokenlist.size()) // It seems to be OK for java to add some object from an offset equaling its size()
					
					if (endi == starti) // insert at the end of string at endi
					{
						tokenlist.add(endi + 1, token);  
						succeed = true;
						break;
					}
					else
					{
						isgreater = token.compareToIgnoreCase(tokenlist.get(endi));
						if (isgreater <= 0)
						{
							tokenlist.add(endi, token);  // token resides between
							succeed = true;
							break;
						}
						else
						{
							tokenlist.add(endi + 1, token);  //insert at the end of string at endi
							succeed = true;
							break;
						}
					}
				}
			}
			else
			{
				if (isgreater == 0)  // the exact match of string was found by chance.
				{
					tokenlist.add(midi, token);
					succeed = true;
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
		
		/*This is only a test for whether there is any token unsuccessfully inserted.
		if (!succeed)
		{
			starti = 1;
		}
		*/
		
		return succeed;
		

		//System.out.print("\n");
		//System.out.print(String.join("|", newtokenlist));
	}
	
	//Insert a token to and update the input tokenlist 
	//Find the insertion location from the given offset on.
	//Return the actual insertion location + 1
	//Java seems to support inserting an item at an offset equaling the total size of a list
	public static int InsertToken3(List<String> tokenlist, String token, int offset)
	{
		if (tokenlist.size() == 0)
		{
			tokenlist.add(token);
			return 1;
		}
		
		if (offset < 0)
		{
			offset = 0;
		}
		
		int starti = offset;
		int endi = tokenlist.size() - 1;
		
		int resultoffset = -1;
		
		// When offset is provided, the starti can sometimes be greater than endi by 1
		// , which explains it difference with InsertToken2()
		while(starti <= endi + 1)  
		{
			int midi = (starti + endi) / 2;
			String midstr = tokenlist.get(midi);
			int isgreater = token.compareToIgnoreCase(midstr);  //ignore cases when comparing two strings
			
			if (midi == starti)  //indicating endi - starti == 1 or endi == starti, which should just be the correct position to insert
			{
				if (isgreater <= 0)
				{
					tokenlist.add(midi, token);
					resultoffset = midi + 1;
					break;
				}
				else
				{
					//if (endi < newtokenlist.size()) // It seems to be OK for java to add some object from an offset equaling its size()
					
					if (endi == starti) // insert at the end of string at endi
					{
						tokenlist.add(endi + 1, token);  
						resultoffset = endi + 2;
						break;
					}
					else
					{
						isgreater = token.compareToIgnoreCase(tokenlist.get(endi));
						if (isgreater <= 0)
						{
							tokenlist.add(endi, token);  // token resides between
							resultoffset = endi + 1;
							break;
						}
						else
						{
							tokenlist.add(endi + 1, token);  //insert at the end of string at endi
							resultoffset = endi + 2;
							break;
						}
					}
				}
			}
			else
			{
				if (isgreater == 0)  // the exact match of string was found by chance.
				{
					tokenlist.add(midi, token);
					resultoffset = midi + 1;
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
		
		return resultoffset;
	}
	
	public static boolean IsAlphanumeric(char mychar)
	{
		boolean Istrue = false;
		if (mychar >= '0' && mychar <= '9')
		{
			Istrue = true;
		}
		else if (mychar >= 'a' && mychar <= 'z')
		{
			Istrue = true;
		}
		else if (mychar >= 'A' && mychar <= 'Z')
		{
			Istrue = true;
		}
		
		return Istrue;
	}
	
	public static boolean IsPureNumeric(String word)
	{
		boolean Istrue = true;
		char mychar;
		for(int i = 0; i < word.length(); i++)
		{
			mychar = word.charAt(i);
			if (mychar < '0')
			{
				Istrue = false;
				break;
			}
			else if (mychar > '9')
			{
				Istrue = false;
				break;
			}
		}
		
		return Istrue;
	}
	
	
	public static boolean IsPureNumeric(StringBuilder word)
	{
		boolean Istrue = true;
		char mychar;
		for(int i = 0; i < word.length(); i++)
		{
			mychar = word.charAt(i);
			if (mychar < '0')
			{
				Istrue = false;
				break;
			}
			else if (mychar > '9')
			{
				Istrue = false;
				break;
			}
		}
		
		return Istrue;
	}
	
	// To increase processing spead by generating token list for each document, and then do merge sort.
	// This function might result in many temporary string lists in memory. Since they are pointers, it doesn't matter?
	public static List<String> MergeTokenLists(List<String> tokenlist1, List<String> tokenlist2)
	{
		List<String> mergedtokenlist = new ArrayList<String>();
		//mergedtokenlist.addAll(tokenlist1);
		int front1 = 0;
		int front2 = 0;
		
		while(front1 < tokenlist1.size() && front2 < tokenlist2.size())
		{
			int result = tokenlist1.get(front1).compareToIgnoreCase(tokenlist2.get(front2));
			if (result == 0)
			{
				mergedtokenlist.add(tokenlist1.get(front1));
				mergedtokenlist.add(tokenlist2.get(front2));
				front1++;
				front2++;
			}
			else if (result < 0)
			{
				mergedtokenlist.add(tokenlist1.get(front1));
				front1++;
			}
			else 
			{
				mergedtokenlist.add(tokenlist2.get(front2));
				front2++;
			}
		}
		
		// Two situations cannot hold simultaneously
		if (front1 < tokenlist1.size())
		{
			for (int i = front1; i < tokenlist1.size(); i++)
			{
				mergedtokenlist.add(tokenlist1.get(i));
			}
		}
		
		if (front2 < tokenlist2.size())
		{
			for (int i = front2; i < tokenlist2.size(); i++)
			{
				mergedtokenlist.add(tokenlist2.get(i));
			}
		}
		
		return mergedtokenlist;
	}
	
	//Merge token list by binary insertion
	//Always place the major token list as the first argument 
	public static void MergeTokenLists2(List<String> mergedtokenlist, List<String> newtokenlist)
	{
		for (int i = 0; i < newtokenlist.size(); i++)
		{
			InsertToken2(mergedtokenlist, newtokenlist.get(i));
		}
	}
	
	//Merge token list by an improved binary insertion, with the starting offset given
	//Always place the major token list as the first argument 
	public static void MergeTokenLists3(List<String> mergedtokenlist, List<String> newtokenlist)
	{
		if (mergedtokenlist.size() == 0)
		{
			mergedtokenlist.addAll(newtokenlist);
			return;
		}
		else
		{
			int offset = 0;
			for (int i = 0; i < newtokenlist.size(); i++)
			{
				offset = InsertToken3(mergedtokenlist, newtokenlist.get(i), offset);
			}
		}
	}
	
	public static int CountMaxTermFreq_Token(List<Token> sortedtokenlist)
	{
		int maxfreq = 0;
		
		int tempcount = 0;
		StringBuilder temptoken = new StringBuilder();
		for (int i = 0; i < sortedtokenlist.size(); i++)
		{
			if (temptoken.length() == 0)
			{
				temptoken.append(sortedtokenlist.get(i).str);
				tempcount = 1;
			}
			else
			{
				// Compare strings ignoring cases
				if (temptoken.toString().compareToIgnoreCase(sortedtokenlist.get(i).str) != 0)
				{
                    if (tempcount > maxfreq)
                    {
                    	maxfreq = tempcount;
                    }
					temptoken.setLength(0);
					temptoken.append(sortedtokenlist.get(i).str);
					tempcount = 1;
				}
				else
				{
					tempcount++;
				}	
			}
		}
		
        if (tempcount > maxfreq)
        {
        	maxfreq = tempcount;
        }
        
        return maxfreq;
	}
	
	public static int CountMaxTermFreq(List<String> sortedtokenlist)
	{
		int maxfreq = 0;
		
		int tempcount = 0;
		StringBuilder temptoken = new StringBuilder();
		for (int i = 0; i < sortedtokenlist.size(); i++)
		{
			if (temptoken.length() == 0)
			{
				temptoken.append(sortedtokenlist.get(i));
				tempcount = 1;
			}
			else
			{
				// Compare strings ignoring cases
				if (temptoken.toString().compareToIgnoreCase(sortedtokenlist.get(i)) != 0)
				{
                    if (tempcount > maxfreq)
                    {
                    	maxfreq = tempcount;
                    }
					temptoken.setLength(0);
					temptoken.append(sortedtokenlist.get(i));
					tempcount = 1;
				}
				else
				{
					tempcount++;
				}	
			}
		}
		
        if (tempcount > maxfreq)
        {
        	maxfreq = tempcount;
        }
        
        return maxfreq;
	}
	
	public static int CountUniqueTokens(List<String> sortedtokenlist)
	{
		int count = 1;  // count can also be initialized to zero and add one after the iteration
		StringBuilder temptoken = new StringBuilder();
		for (int i = 0; i < sortedtokenlist.size(); i++)
		{
			if (temptoken.length() == 0)
			{
				temptoken.append(sortedtokenlist.get(i));
			}
			else
			{
				// Compare strings ignoring cases
				if (temptoken.toString().compareToIgnoreCase(sortedtokenlist.get(i)) != 0)
				{
					count++;
					temptoken.setLength(0);
					temptoken.append(sortedtokenlist.get(i));
				}
			}
		}
		
		return count;
	}
	
	public static int CountTokensApprearingOnce(List<String> sortedtokenlist)
	{
		int count = 0;
		StringBuilder temptoken = new StringBuilder();
		int tempcount = 0;
		for (int i = 0; i < sortedtokenlist.size(); i++)
		{
			if (temptoken.length() == 0)
			{
				temptoken.append(sortedtokenlist.get(i));
				tempcount = 1;
			}
			else
			{
				// Compare strings ignoring cases
				if (temptoken.toString().compareToIgnoreCase(sortedtokenlist.get(i)) != 0)
				{
					if (tempcount == 1)
					{
					    count++;
					}
					temptoken.setLength(0);
					temptoken.append(sortedtokenlist.get(i));
					tempcount = 1;
				}
				else
				{
					tempcount++;
				}	
			}
		}
		
		if (tempcount == 1)
		{
			count++;
		}
		
		return count;
	}
	
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
	
	public static List<Token> FilterStopWordTokens(List<Token> tokens, List<String> stopwords)
	{
		List<Token> filteredtokens = new ArrayList<Token>();
		for(int i = 0; i < tokens.size(); i++)
		{
			if (!stopwords.contains(tokens.get(i).str))
			{
				filteredtokens.add(tokens.get(i));
			}
		}
		
		return filteredtokens;
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
	
	public static void main(String[] args)
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
		
		//File[] filelist = GetAllFiles("/people/cs/s/sanda/cs6322/Cranfield");
		
		File[] filelist = GetAllFiles("Z://ÎÄ¸å/ÁôÑ§/UTDallas/textbooks/6322_spring2017_Information Retrieval/Cranfield");
		//File[] filelist = GetAllFiles("F://corpus");
		
		List<String> mergedlist = new ArrayList<String>();
		List<String> stemmedmergedlist = new ArrayList<String>();
		
		for(int i = 0; i < filelist.length; i++)
		{			
			//System.out.println(filelist[i].getName());
			dtInstant1 = Instant.now();
			StringBuilder originaltext = ReadFileText(filelist[i]);
			dtInstant2 = Instant.now();
			dtLength1 += Duration.between(dtInstant1, dtInstant2).toMillis();
			
			dtInstant1 = dtInstant2;
			StringBuilder filteredtext = FilterHTML(originaltext);
			dtInstant2 = Instant.now();
			dtLength2 += Duration.between(dtInstant1, dtInstant2).toMillis();
			
			//System.out.print(originaltext.toString());
			//System.out.print("\n\n");
			
			//System.out.print(filteredtext.toString());
			//System.out.print("\n\n");
			
			dtInstant1 = dtInstant2;
			List<String> tokens = Tokenize(filteredtext, false);
			dtInstant2 = Instant.now();
			dtLength3 += Duration.between(dtInstant1, dtInstant2).toMillis();
			
			dtInstant1 = dtInstant2;
			List<String> stemmedtokens = Tokenize(filteredtext, true);
			dtInstant2 = Instant.now();
			dtLength4 += Duration.between(dtInstant1, dtInstant2).toMillis();
			
			/*
		    System.out.print("\n");
			System.out.print(String.join("|", tokens));
			System.out.print("\n\n");
			System.out.print(String.join("|", stemmedtokens));
			System.out.print("\n");
			
			System.out.print("\nThere are a total of " + tokens.size() + " tokens in document " + (i+1) + ".");
			System.out.print("\nThere are a total of " + tokens.size() + " stemmed tokens in document " + (i+1) + ".");
			
			int uniquetokens = CountUniqueTokens(tokens);
			int tokensapprearingonce = CountTokensApprearingOnce(tokens);
			int uniquestemmedtokens = CountUniqueTokens(stemmedtokens);
			int stemmedtokensapprearingonce = CountTokensApprearingOnce(stemmedtokens);
			System.out.print("\nThere are a total of " + uniquetokens + " unique tokens in document " + (i+1) + ".");
			System.out.print("\nThere are a total of " + tokensapprearingonce + " tokens apprearing once in document " + (i+1) + ".");
			System.out.print("\nThere are a total of " + uniquestemmedtokens + " unique stemmed tokens in document " + (i+1) + ".");
			System.out.print("\nThere are a total of " + stemmedtokensapprearingonce + " stemmed tokens apprearing once in document " + (i+1) + ".");
            System.out.print("\n\n");
            */
            
			dtInstant1 = dtInstant2;
			// mergedlist = MergeTokenLists(mergedlist, tokens);
			MergeTokenLists2(mergedlist, tokens);
			//MergeTokenLists3(mergedlist, tokens);
			dtInstant2 = Instant.now();
			dtLength5 += Duration.between(dtInstant1, dtInstant2).toMillis();
			
			dtInstant1 = dtInstant2;
			//stemmedmergedlist = MergeTokenLists(stemmedmergedlist, stemmedtokens);
			MergeTokenLists2(stemmedmergedlist, stemmedtokens);
			//MergeTokenLists3(stemmedmergedlist, stemmedtokens);
			dtInstant2 = Instant.now();
			dtLength6 += Duration.between(dtInstant1, dtInstant2).toMillis();
			
			//System.out.print("\nThere are a total of " + mergedlist.size() + " tokens in the mergedlist.\n");
			
			
			if (i%200 == 0)
			{
				System.out.print("\n" + i);
			}
			System.out.print(".");
			
			
			/*
			if (i > 20)
			{
				break;
			}
			*/

		}
		
		
		System.out.print("\n");
		System.out.print("\nThere are a total of " + mergedlist.size() + " tokens in the Cranfield Collection.");
		int uniquetokens = CountUniqueTokens(mergedlist);
		int tokensapprearingonce = CountTokensApprearingOnce(mergedlist);
		System.out.print("\nThere are a total of " + uniquetokens + " unique tokens.");
		System.out.print("\nThere are a total of " + tokensapprearingonce + " tokens apprearing once.");
		System.out.print("\nThe average number of word tokens per document is: " + (float)((float)(mergedlist.size()) / 1400));
        
		dtInstant1 = Instant.now();
        Dictionary myDict = Dictionary.GetDictionary(mergedlist);
        List<Term> top30tokens = Dictionary.GetTopTerms(myDict, 30);
		dtInstant2 = Instant.now();
		dtLength7 += Duration.between(dtInstant1, dtInstant2).toMillis();
        
        System.out.print("\nThe most frequent 30 tokens are:");
        for(int i = top30tokens.size() - 1; i >= 0; i--)
        {
        	System.out.print("\n" + top30tokens.get(i).str + "  freq=" + top30tokens.get(i).tfreq);
        }
        
        
        System.out.print("\n");
		System.out.print("\nThere are a total of " + stemmedmergedlist.size() + " stemmed tokens in the Cranfield Collection.");
		int uniquestemmedtokens = CountUniqueTokens(stemmedmergedlist);
		int stemmedtokensapprearingonce = CountTokensApprearingOnce(stemmedmergedlist);
		System.out.print("\nThere are a total of " + uniquestemmedtokens + " unique stemmed tokens.");
		System.out.print("\nThere are a total of " + stemmedtokensapprearingonce + " stemmed tokens apprearing once.");
		System.out.print("\nThe average number of word stems per document is: " + (float)((float)(stemmedmergedlist.size()) / 1400));
        
		
		dtInstant1 = Instant.now();
        Dictionary myDictStemmed = Dictionary.GetDictionary(stemmedmergedlist);
        List<Term> top30stemmedtokens = Dictionary.GetTopTerms(myDictStemmed, 30);
		dtInstant2 = Instant.now();
		dtLength8 += Duration.between(dtInstant1, dtInstant2).toMillis();
        
        System.out.print("\nThe most frequent 30 stemmed tokens are:");
        for(int i = top30stemmedtokens.size() - 1; i >= 0; i--)
        {
        	System.out.print("\n" + top30stemmedtokens.get(i).str + "  freq=" + top30stemmedtokens.get(i).tfreq);
        }
        
		/*
        int totalfrequency = 0;
        int numfreq1 = 0;
        List<Dictionary.Term> allTerms = myDict.GetAllTerms();
        for (int i = 0; i < allTerms.size(); i++)
        {
        	totalfrequency += allTerms.get(i).freq;
        	if (allTerms.get(i).freq == 1)
        	{
        		numfreq1++;
        	}
        }
        
        System.out.print("\n");
        System.out.print("\nThe frequencies of dictionary entries add up to: " + totalfrequency);
        System.out.print("\nThe number of dictionary entries is: " + allTerms.size());
        System.out.print("\nThe number of dictionary entries with frequency being 1 is: " + numfreq1);
        
        int totalfrequencystemmed = 0;
        int numfreq1stemmed = 0;
        List<Dictionary.Term> allTermsStemmed = myDictStemmed.GetAllTerms();
        for (int i = 0; i < allTermsStemmed.size(); i++)
        {
        	totalfrequencystemmed += allTermsStemmed.get(i).freq;
        	if (allTermsStemmed.get(i).freq == 1)
        	{
        		numfreq1stemmed++;
        	}
        }
        
        System.out.print("\n");
        System.out.print("\nThe frequencies of stemmed dictionary entries add up to: " + totalfrequencystemmed);
        System.out.print("\nThe number of stemmed dictionary entries is: " + allTermsStemmed.size());
        System.out.print("\nThe number of stemmed dictionary entries with frequency being 1 is: " + numfreq1stemmed);
        */
        
        dtEnd = Instant.now();
        
        System.out.print("\n");
        System.out.println("The total time elapsed: " + Duration.between(dtStart, dtEnd).getSeconds());
        System.out.println("The time1 elapsed: " + Duration.ofMillis(dtLength1).getSeconds());
        System.out.println("The time2 elapsed: " + Duration.ofMillis(dtLength2).getSeconds());
        System.out.println("The time3 elapsed: " + Duration.ofMillis(dtLength3).getSeconds());
        System.out.println("The time4 elapsed: " + Duration.ofMillis(dtLength4).getSeconds());
        System.out.println("The time5 elapsed: " + Duration.ofMillis(dtLength5).getSeconds());
        System.out.println("The time6 elapsed: " + Duration.ofMillis(dtLength6).getSeconds());
        System.out.println("The time7 elapsed: " + Duration.ofMillis(dtLength7).getSeconds());
        System.out.println("The time8 elapsed: " + Duration.ofMillis(dtLength8).getSeconds());
        
        /*
         * Results of Performance Test:
         * The total time elapsed: 34 s
         * The time1 elapsed: 1 s
         * The time2 elapsed: 0
         * The time3 elapsed: 0
         * The time4 elapsed: 0
         * The time5 elapsed: 15 s
         * The time6 elapsed: 15 s
         * */
        //Result Analysis:
        //Test shows the rate-limiting steps are merging token lists, i.e., MergeTokenLists()
        //Two merging steps for tokens and stems take up to 30 seconds, while the total processing time is 34 seconds.
        //Tokenize() both with and without stemming shows excellent performance, taking negligible time length
        //This indicates that the binary insertion adopted in the function Tokenize() is quite effective.
        //An improvement should be avoid merging and instead using binary insertion all the way down.
        
        /*
         * Results of Performance Test after the Improvement:
         * The total time elapsed: 11 s
         * The time1 elapsed: 1 s
         * The time2 elapsed: 0
         * The time3 elapsed: 0
         * The time4 elapsed: 0
         * The time5 elapsed: 4 s
         * The time6 elapsed: 4 s
         * */
        //Result Analysis:
        //The improved result shows time elapsed by steps 5 and 6 is exactly a log value of that previously.
        //Since our binary insertion is done with sorted new token list, an even further improvement can be made by returning the insertion offset of the inserted token in the previous iteration.
        //But this assumption makes no significant improvement in time efficiency - the same result is obtained.
        
        //There can still be improvement by forming Dictionary during tokenization, which will siginificantly shrink the size of list to be merged.
	}
}
