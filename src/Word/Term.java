package Word;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

public class Term {
	//int id = 0;   // The id reflecting the temporal sequence of term creation. Is it necessary when terms are already sorted when being added?
	//int sortedid = 0;
	String str = "";
	int tfreq = 0;
	int dfreq = 0;
	List<Integer> postings = new ArrayList<Integer>(); 
	List<Integer> gaps = new ArrayList<Integer>(); 
	List<Integer> tfreqs = new ArrayList<Integer>(); 
	List<Integer> positions = new ArrayList<Integer>(); // counted by number of tokens, not offsets
	List<List<Integer>> positions2 = new LinkedList<List<Integer>>();
	List<Double> weights = new ArrayList<Double>();
	List<String> wordforms = new ArrayList<String>();
	
	public Term()
	{
		str = "";
		tfreq = 0;
		dfreq = 0;
		postings = new ArrayList<Integer>(); 
		gaps = new ArrayList<Integer>(); 
		tfreqs = new ArrayList<Integer>(); 
		positions = new ArrayList<Integer>(); // counted by number of tokens, not offsets
		positions2 = new LinkedList<List<Integer>>();
		weights = new ArrayList<Double>();
		wordforms = new ArrayList<String>();
	}
}
