package Word;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

public class Document {

	int ID = 0;
	int max_tf = 0;
	int doclen = 0;
	int[] tfs = null;
	int[] dfs = null;
	float[] vector = null;
	float[] vector2 = null;
	float[] vector3 = null;
	double score = 0;
	double score2 = 0;
	double score3 = 0;
	List<Integer> dict_tfs = new LinkedList<Integer>();
	List<Integer> dict_termids = new LinkedList<Integer>();
	List<Integer> dict_tpositions = new LinkedList<Integer>();
	List<List<Integer>> dict_tpositions2 = new LinkedList<List<Integer>>();
	String linkstr = "";
	
	public Document()
	{
		ID = 0;
		max_tf = 0;
		doclen = 0;
		tfs = null;
		dfs = null;
		vector = null;
		vector2 = null;
		vector3 = null;
		score = 0;
		score2 = 0;
		score3 = 0;
		dict_tfs = new LinkedList<Integer>();
		dict_termids = new LinkedList<Integer>();
		dict_tpositions = new LinkedList<Integer>();
		dict_tpositions2 = new LinkedList<List<Integer>>();
		linkstr = "";
	}
	
    public static void PrintDocInfo(List<Document> docInfoList)
    {
    	for(int i = 0; i < docInfoList.size(); i++)
    	{
    		Document doc = docInfoList.get(i);
    		System.out.println(doc.ID + ", " + doc.max_tf + ", " + doc.doclen);
    	}
    	
    	System.out.println(" ");
    }
}
