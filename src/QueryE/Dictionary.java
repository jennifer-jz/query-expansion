package QueryE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Dictionary {
	private List<Term> termlist = new ArrayList<Term>();
	
	public Dictionary()
	{
		termlist = new ArrayList<Term>();
	}
	
	public int Size()
	{
		return termlist.size();
	}
	
	public Term GetTerm(int offset)
	{
		if (offset < 0)
		{
			return null;
		}
		return termlist.get(offset);
	}
	
	public int GetIndex(String term)
	{
		term = term.trim();  // string forms should be normalized before adding.
		int index= -1;
		
		int starti = 0;
		int endi = termlist.size() - 1;
		
		while(starti <= endi)
		{
			int midi = (starti + endi) / 2;
			String midstr = termlist.get(midi).str;
			int isgreater = term.compareToIgnoreCase(midstr);  //ignore cases when comparing with existing terms
			
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
						isgreater = term.compareToIgnoreCase(termlist.get(endi).str);
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
		
		return index;
	}
	
	public int GetInsertionIndex(String term)
	{
		if (termlist.size() == 0)
		{
			return 0;
		}
		
		int starti = 0;
		int endi = termlist.size() - 1;
		int insertionindex = -1;
		
		while(starti <= endi)
		{
			int midi = (starti + endi) / 2;
			String midstr = termlist.get(midi).str;
			int isgreater = term.compareToIgnoreCase(midstr);  //ignore cases when comparing two strings
			
			if (midi == starti)  //indicating endi - starti == 1 or endi == starti, which should just be the correct position to insert
			{
				if (isgreater <= 0)
				{
					insertionindex = midi;
					break;
				}
				else
				{
					//if (endi < newtokenlist.size()) // It seems to be OK for java to add some object from an offset equaling its size()
					
					if (endi == starti) // insert at the end of string at endi
					{
						insertionindex = endi + 1;
						break;
					}
					else
					{
						isgreater = term.compareToIgnoreCase(termlist.get(endi).str);
						if (isgreater <= 0)
						{
							insertionindex = endi;  // token resides between
							break;
						}
						else
						{
							insertionindex = endi + 1; //insert at the end of string at endi
							break;
						}
					}
				}
			}
			else
			{
				if (isgreater == 0)  // the exact match of string was found by chance.
				{
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
		
		return insertionindex;
		

		//System.out.print("\n");
		//System.out.print(String.join("|", newtokenlist));
	}
	
	public void AddTermMatrix(String term, List<String> wordforms, int tfreq, int docid, int docindex, int totaldocnum)
	{
		int index = GetIndex(term);
		if (index >= 0 && index < termlist.size())
		{
			termlist.get(index).dfreq +=1;
			termlist.get(index).tfreq += tfreq;
			termlist.get(index).postings.set(docindex, docid);
			termlist.get(index).tfreqs.set(docindex, tfreq);
			
			termlist.get(index).wordforms = wordforms; // it's not general, but since we are building a matrix from dictionary, no need to worry about consistency
			//termlist.get(index).postings.sort(null);  not necessary to sort, because docs are analyzed sequentially 
		}
		else
		{
			int insertionindex = GetInsertionIndex(term);
			Term myterm = new Term();
			myterm.str = term;
			myterm.dfreq = 1;
			myterm.tfreq = tfreq;
			
			for(int i = 0; i < totaldocnum; i++)
			{
				myterm.postings.add(0);
				myterm.tfreqs.add(0);
			}
			
			myterm.postings.set(docindex, docid);
			myterm.tfreqs.set(docindex, tfreq);
			myterm.wordforms = wordforms;
			termlist.add(insertionindex, myterm);
		}
	}
	
	public static void AddDictTermsWithForm(Dictionary myDict, List<Token> sortedtokenlistofdoc, int docid)
	{
		if (myDict == null)
		{
			return;
		}
		
		Token temptoken = new Token();
		int tfreq = 0;
		List<String> wordforms = new ArrayList<String>();
		for (int i = 0; i < sortedtokenlistofdoc.size(); i++)
		{
			if (i == 0)
			{
				temptoken = sortedtokenlistofdoc.get(i);
				tfreq = 1;
				wordforms = new ArrayList<String>();
				wordforms.add(temptoken.wordform);
			}
			else
			{
				String currenttokenstr = sortedtokenlistofdoc.get(i).wordform;
				// Compare strings ignoring cases
				if (temptoken.str.toString().compareTo(sortedtokenlistofdoc.get(i).str) != 0) // if the comparison value is not zero, then the two strings are not identical
				{
					myDict.InserTerm(temptoken.str.toString(), wordforms, tfreq, docid);
					
					temptoken = sortedtokenlistofdoc.get(i);
					tfreq = 1;
					wordforms = new ArrayList<String>();
					wordforms.add(temptoken.wordform);
				}
				else
				{
					tfreq++;
					if (!wordforms.contains(sortedtokenlistofdoc.get(i).wordform))
					{
						wordforms.add(sortedtokenlistofdoc.get(i).wordform);
					}
				}
			}
		}
		
		if (tfreq > 0)
		{
			myDict.InserTerm(temptoken.str.toString(), wordforms, tfreq, docid);
		}
	}
	
	public static void AddDictTermsWithPositionAndForm(Dictionary myDict, List<Token> sortedtokenlistofdoc, int docid)
	{
		if (myDict == null)
		{
			return;
		}
		
		Token temptoken = new Token();
		int tfreq = 0;
		List<Integer> positions = new ArrayList<Integer>();
		List<String> wordforms = new ArrayList<String>();
		for (int i = 0; i < sortedtokenlistofdoc.size(); i++)
		{
			if (i == 0)
			{
				temptoken = sortedtokenlistofdoc.get(i);
				tfreq = 1;
				positions = new ArrayList<Integer>();
				positions.add(sortedtokenlistofdoc.get(i).position);
				wordforms = new ArrayList<String>();
				wordforms.add(temptoken.wordform);
			}
			else
			{
				String currenttokenstr = sortedtokenlistofdoc.get(i).wordform;
				// Compare strings ignoring cases
				if (temptoken.str.toString().compareTo(sortedtokenlistofdoc.get(i).str) != 0) // if the comparison value is not zero, then the two strings are not identical
				{
					myDict.InserTerm(temptoken.str.toString(), wordforms, tfreq, docid, positions);
					
					temptoken = sortedtokenlistofdoc.get(i);
					tfreq = 1;
					positions = new ArrayList<Integer>();
					positions.add(sortedtokenlistofdoc.get(i).position);
					wordforms = new ArrayList<String>();
					wordforms.add(temptoken.wordform);
				}
				else
				{
					tfreq++;
					positions.add(sortedtokenlistofdoc.get(i).position);
					if (!wordforms.contains(sortedtokenlistofdoc.get(i).wordform))
					{
						wordforms.add(sortedtokenlistofdoc.get(i).wordform);
					}
				}
			}
		}
		
		if (tfreq > 0)
		{
			myDict.InserTerm(temptoken.str.toString(), wordforms, tfreq, docid, positions);
		}
	}
	
	public void InserTerm(String term, List<String> wordforms, int tfreq, int docid, List<Integer> positions)
	{
		if (tfreq <= 0)
		{
			return;
		}
		
		int index = GetIndex(term);
		if (index >= 0 && index < termlist.size())
		{
			termlist.get(index).dfreq +=1;
			termlist.get(index).tfreq += tfreq;
			termlist.get(index).postings.add(docid);
			termlist.get(index).tfreqs.add(tfreq);
			//termlist.get(index).positions.addAll(positions);
			termlist.get(index).positions2.add(positions);
			for(int k = 0; k < wordforms.size(); k++)
			{
				if (!termlist.get(index).wordforms.contains(wordforms.get(k)))
			    {
					termlist.get(index).wordforms.add(wordforms.get(k));
				}
			}
			//termlist.get(index).postings.sort(null);  not necessary to sort, because docs are analyzed sequentially 
		}
		else
		{
			int insertionindex = GetInsertionIndex(term);
			Term myterm = new Term();
			myterm.str = term;
			myterm.dfreq = 1;
			myterm.tfreq = tfreq;
			myterm.postings.add(docid);
			myterm.tfreqs.add(tfreq);
			//myterm.positions.addAll(positions);
			myterm.positions2.add(positions);
			myterm.wordforms.addAll(wordforms);
			termlist.add(insertionindex, myterm);
		}
	}
	
	public void InserTerm(String term, List<String> wordforms, int tfreq, int docid)
	{
		if (tfreq <= 0)
		{
			return;
		}
		
		int index = GetIndex(term);
		if (index >= 0 && index < termlist.size())
		{
			termlist.get(index).dfreq +=1;
			termlist.get(index).tfreq += tfreq;
			termlist.get(index).postings.add(docid);
			termlist.get(index).tfreqs.add(tfreq);
			//termlist.get(index).positions.addAll(positions);
			//termlist.get(index).positions2.add(positions);
			for(int k = 0; k < wordforms.size(); k++)
			{
				if (!termlist.get(index).wordforms.contains(wordforms.get(k)))
			    {
					termlist.get(index).wordforms.add(wordforms.get(k));
				}
			}
			//termlist.get(index).postings.sort(null);  not necessary to sort, because docs are analyzed sequentially 
		}
		else
		{
			int insertionindex = GetInsertionIndex(term);
			Term myterm = new Term();
			myterm.str = term;
			myterm.dfreq = 1;
			myterm.tfreq = tfreq;
			myterm.postings.add(docid);
			myterm.tfreqs.add(tfreq);
			//myterm.positions.addAll(positions);
			//myterm.positions2.add(positions);
			myterm.wordforms.addAll(wordforms);
			termlist.add(insertionindex, myterm);
		}
	}
	
	public void InserTerm(String term, int tfreq, int docid, List<Integer> positions)
	{
		if (tfreq <= 0)
		{
			return;
		}
		
		int index = GetIndex(term);
		if (index >= 0 && index < termlist.size())
		{
			termlist.get(index).dfreq +=1;
			termlist.get(index).tfreq += tfreq;
			termlist.get(index).postings.add(docid);
			termlist.get(index).tfreqs.add(tfreq);
			//termlist.get(index).positions.addAll(positions);
			termlist.get(index).positions2.add(positions);
			//termlist.get(index).postings.sort(null);  not necessary to sort, because docs are analyzed sequentially 
		}
		else
		{
			int insertionindex = GetInsertionIndex(term);
			Term myterm = new Term();
			myterm.str = term;
			myterm.dfreq = 1;
			myterm.tfreq = tfreq;
			myterm.postings.add(docid);
			myterm.tfreqs.add(tfreq);
			//myterm.positions.addAll(positions);
			myterm.positions2.add(positions);
			termlist.add(insertionindex, myterm);
		}
	}
	
	public void InserTerm(String term, int tfreq, int docid)
	{
		if (tfreq <= 0)
		{
			return;
		}
		
		int index = GetIndex(term);
		if (index >= 0 && index < termlist.size())
		{
			termlist.get(index).dfreq +=1;
			termlist.get(index).tfreq += tfreq;
			termlist.get(index).postings.add(docid);
			termlist.get(index).tfreqs.add(tfreq);
			//termlist.get(index).postings.sort(null);  not necessary to sort, because docs are analyzed sequentially 
		}
		else
		{
			int insertionindex = GetInsertionIndex(term);
			Term myterm = new Term();
			myterm.str = term;
			myterm.dfreq = 1;
			myterm.tfreq = tfreq;
			myterm.postings.add(docid);
			myterm.tfreqs.add(tfreq);
			termlist.add(insertionindex, myterm);
		}
	}
	
	public void InserTerm(String term, List<Integer> tfreqs, List<Integer> postings)
	{
		if (tfreqs.size() != postings.size() || tfreqs.size() == 0)
		{
			return;
		}
		
		int tfreq = 0;
		for(int i = 0; i < tfreqs.size(); i++)
		{
			tfreq += tfreqs.get(i);
		}
		
		int index = GetIndex(term);
		if (index >= 0 && index < termlist.size())
		{
			termlist.get(index).dfreq += tfreqs.size();

			termlist.get(index).tfreq += tfreq;
			
			List<Integer> existingpostings = termlist.get(index).postings;
			List<Integer> existingtfreqs = termlist.get(index).tfreqs;
			for(int i = 0; i < postings.size(); i++)
			{
				int postingi = postings.get(i);
				int tfreqi = tfreqs.get(i);
				int j = existingpostings.indexOf(postingi);
				if (j >= 0)
				{
					existingtfreqs.set(j, existingtfreqs.get(j) + tfreqi);
				}
				else
				{
					int insertionk = 0;
					for(int k = 0; k < existingpostings.size(); k++)
					{					
						if (postingi <= existingpostings.get(k))
						{
							if (k == 0)
							{
								break;
							}
						}
						else
						{
							insertionk = k + 1;
							break;
						}
						
					}
					
					existingpostings.add(insertionk, postingi);
					existingtfreqs.add(insertionk, tfreqi);
				}
			}
			
			termlist.get(index).postings = existingpostings;
			termlist.get(index).tfreqs = existingtfreqs;
			//termlist.get(index).postings.sort(null);  not necessary to sort, because docs are analyzed sequentially 
		}
		else
		{
			int insertionindex = GetInsertionIndex(term);
			Term myterm = new Term();
			myterm.str = term;
			myterm.dfreq = tfreqs.size();
			myterm.tfreq = tfreq;
			myterm.postings.addAll(postings);
			myterm.tfreqs.addAll(tfreqs);
			termlist.add(insertionindex, myterm);
		}
	}
	
    public static void PrintDict2(Dictionary myDict)
    {
		for(int i= 0; i < myDict.Size(); i++)
		{
			Term term = myDict.GetTerm(i);
			
			//System.out.println(term.str +  "; " + term.tfreq+ "; " + term.dfreq  + "; *" + Arrays.toString(term.postings.toArray())+ "; *" +  Arrays.toString(term.tfreqs.toArray()) + "; *" +  Arrays.toString(Index.GetMaxTFInDoc(term.postings, docInfoList).toArray())+ "; *" +  Arrays.toString(Index.GetDocLenInDoc(term.postings, docInfoList).toArray()));
			System.out.println(term.str +  "; " + term.tfreq+ "; " + term.dfreq  + "; *" + Arrays.toString(term.postings.toArray())+ "; *" +  Arrays.toString(term.tfreqs.toArray()));
			System.out.println("    positions: ");
			for(int j = 0; j < term.positions2.size(); j++)
			{
				System.out.print(Arrays.toString(term.positions2.get(j).toArray()));
			}
			System.out.println();
			System.out.println("    wordforms: " + Arrays.toString(term.wordforms.toArray()));
		}
		
		System.out.println(" ");
    }
}
