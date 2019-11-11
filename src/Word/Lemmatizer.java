package Word;
import java.util.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;

public class Lemmatizer {
	
	StanfordCoreNLP pipeline = null;
	Properties props = null;
	private List<String> tags = new LinkedList<String>();
	public Lemmatizer()
	{
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
		tags = new LinkedList<String>();
	}
	
	public List<String> GetTags()
	{
		return tags;
	}
	
	public List<Token> LemmatizeToken(List<Token> tokens, boolean istag)
	{
		StringBuilder text = new StringBuilder();
		for(int i = 0; i < tokens.size(); i++)
		{
			if (text.length() > 0)
			{
				text.append(" ");
			}
			text.append(tokens.get(i).str);
		}
		List<String> lemmatizedtokens = Lemmatize(text.toString(), istag);
		List<Token> newtokens = new ArrayList<Token>();
		if (lemmatizedtokens.size() == tokens.size())
		{
			for(int i = 0; i < tokens.size(); i++)
			{
				Token token = new Token();
				token.str = lemmatizedtokens.get(i);
				token.position = tokens.get(i).position;
				newtokens.add(token);
			}
		}
		else
		{
			System.out.print("#");
			//System.out.println("lemmatizedtokens.size() != tokens.size()");
			for(int i = 0; i < tokens.size(); i++)
			{
				Token token = Lemmatize(tokens.get(i));
				newtokens.add(token);
			}
		}
		return newtokens;
	}
	
	public List<String> Lemmatize(List<String> tokens, boolean istag)
	{
		String paragraph = String.join(" ", tokens);
		return Lemmatize(paragraph, istag);
	}
	
	public Token Lemmatize(Token token)
	{
		//StanfordCoreNLP pipeline;
		//Properties props = new Properties();
		//props.put("annotators", "tokenize, ssplit, pos, lemma");
		//pipeline = new StanfordCoreNLP(props);
		
		Annotation document = new Annotation(token.str);
		pipeline.annotate(document);
				
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<String> lemmas = new LinkedList<>();
		Token newtoken = new Token();
		newtoken.position = token.position;
		
		for (CoreMap sentence : sentences) {
			for (CoreLabel word : sentence.get(TokensAnnotation.class)) {
				newtoken.str = word.get(LemmaAnnotation.class);
				break;
			}
			}
		
		
		return newtoken;
	}
	
	public List<String> Lemmatize(String paragraph, boolean istag)
	{
		//StanfordCoreNLP pipeline;
		//Properties props = new Properties();
		//props.put("annotators", "tokenize, ssplit, pos, lemma");
		//pipeline = new StanfordCoreNLP(props);
		
		Annotation document = new Annotation(paragraph);
		pipeline.annotate(document);
				
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<String> lemmas = new LinkedList<>();
		
		if (istag)
		{
			tags = new LinkedList<String>();
		}
		
		for (CoreMap sentence : sentences) {
			for (CoreLabel word : sentence.get(TokensAnnotation.class)) {
			lemmas.add(word.get(LemmaAnnotation.class));
			if (istag)
			{
				tags.add(word.tag());
			}
			}
			}
		
		return lemmas;
	}
	
	public static void main(String[] args)
	{
		String paragraph = "Similar to stemming is Lemmatization. "
				+"This is the process of finding its lemma, its form " +
				"as found in a dictionary.";
		Lemmatizer lemmatizer = new Lemmatizer();
		List<String> lemmas = lemmatizer.Lemmatize(paragraph, false);
		
		System.out.print("[");
		for (String element : lemmas) {
		System.out.print(element + " ");
		}
		System.out.println("]");
	}
}
