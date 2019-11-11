package QueryE;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
	
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
}
