package Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MidTermSubmit {

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
