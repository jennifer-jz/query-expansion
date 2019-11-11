package QueryE;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Term {
	public String str = "";
	public int tfreq = 0;
	public int dfreq = 0;
	public List<Integer> postings = new ArrayList<Integer>(); 
	public List<Integer> gaps = new ArrayList<Integer>(); 
	public List<Integer> tfreqs = new ArrayList<Integer>(); 
	public List<Integer> positions = new ArrayList<Integer>(); // counted by number of tokens, not offsets
	public List<List<Integer>> positions2 = new LinkedList<List<Integer>>();
	public List<Double> weights = new ArrayList<Double>();
	public List<String> wordforms = new ArrayList<String>();
	
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
