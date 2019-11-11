package Word;

import java.time.Duration;
import java.time.Instant;

public class Homework2 {

	public static void main(String[] args)
	{
		Instant dtStart = Instant.now();

		String stopwordspath = "//people//cs//s//sanda//cs6322//resourcesIR//stopwords";
		String corpuspath = "//people//cs//s//sanda//cs6322//Cranfield";
		String storepath = "//home//011//j//jx//jxz161030//IR6322//homework2";
		Index.BuildIndexUnix(args[0], stopwordspath, corpuspath, false, true);  // Version1.uncompressed
		Index.BuildIndexUnix(args[0], stopwordspath, corpuspath, false, false); // Version2.uncompressed
		Index.BuildIndexUnix(args[0], stopwordspath, corpuspath, true, true); // Version1.compressed
		Index.BuildIndexUnix(args[0], stopwordspath, corpuspath, true, false); // Version2.compressed
		
		Instant dtEnd = Instant.now();
		
		System.out.println("Total Time elapsed: " + Duration.between(dtStart, dtEnd).toMillis() + " milliseconds");

	}
}
