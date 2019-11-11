package Word;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Test {

	public static void main(String[] args)
	{
		//String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		//StringBuilder temp = Index.ReadDocInfoBinary("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//docInfo2");
		//System.out.print(temp.toString());
		
		//List<Document> docInfoList1 = Index.ReadDocInfo("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//docInfo1");
		//List<Document> docInfoList2 = Index.ReadDocInfo("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//docInfo2");
		
		//Index.PrintDocInfo(docInfoList1);
		//Index.PrintDocInfo(docInfoList2);
		
		//List<Integer> intlist1 = Index.ReadIntegers("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//Index_Version1.uncompressed", docInfoList1);
		//List<Integer> intlist2 = Index.ReadIntegers("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//Index_Version2.uncompressed", docInfoList2);
		
		//System.out.println(Arrays.asList(intlist1).toString());
		//System.out.println(Arrays.asList(intlist2).toString());
		
		//Dictionary myDict1 = Index.GetDictionaryFromIntList2(intlist1);
		//Dictionary myDict2 = Index.GetDictionaryFromIntList2(intlist2);
		
		//Dictionary myDict1 = Index.ReadIndex("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//Index_Version1.uncompress", docInfoList1);
		//Dictionary myDict2 = Index.ReadIndex("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//Index_Version2.uncompress", docInfoList2);

		//byte[] longstring = Index.ReadTermString(folderpath + "TermString_Version1");
		//Index.PrintLongString(longstring);
		//Dictionary myDict1 = Index.ReadCompressedIndex_Block8(folderpath + "Index_Version1.compressed", docInfoList1, longstring);
		
		//Index.PrintDict(myDict1, docInfoList1);
		
		//byte[] longstring= Index.ReadTermString(folderpath + "TermString_Version2");

		//Index.PrintLongString2(longstring);
		
		//Dictionary myDict2 = Index.ReadCompressedIndex_FrontCoding(folderpath + "Index_Version2.compressed", docInfoList2, longstring);
		
		//Index.PrintDict(myDict2, docInfoList2);
		//System.out.println("print complete");
		
		
		/*
       System.out.println("The Gamma Code:");
       
       
       for(int i = 0; i < 350; i ++)
       {
    	   String code = Index.GetGammaCode(i);
    	   byte[] bytes = Index.GetBytesFromBitString(code);
    	   List<Integer> intlist = Index.GetGammaDecode(bytes); //  
    	   System.out.println(i + ": " + code + ", byte value: " + Index.PrintBytesToInt(bytes) + ", binary string: " + Index.PrintBytesToBinary(bytes, true) + ", decoded: " + Arrays.toString(intlist.toArray()));
       }
       
       System.out.println("The Delta Code:");
       
       for(int i = 0; i < 350; i ++)
       {
    	   String code = Index.GetDeltaCode(i);
    	   byte[] bytes = Index.GetBytesFromBitString(code);
    	   List<Integer> intlist = Index.GetDeltaDecode(bytes);
    	   System.out.println(i + ": " + code + ", byte value: " + Index.PrintBytesToInt(bytes) + ", binary string: " + Index.PrintBytesToBinary(bytes, true) + ", decoded: " + Arrays.toString(intlist.toArray()));
       }
       */
       
//       if (args.length > 0)
//       {
//    	   folderpath = args[0];
//       }
//       
//       File[] filelist = Tokenizer.GetAllFiles(folderpath);
//       for(int i = 0; i < filelist.length; i++)
//       {
//    	   System.out.println(filelist[i].getName());
//       }
//       
//       System.out.println("Sorting...");
//       
//       File[] sortedfilelist = Tokenizer.SortFilesByName(filelist);
//       for(int i = 0; i < sortedfilelist.length; i++)
//       {
//    	   System.out.println(sortedfilelist[i].getName());
//       }
		
//		Lemmatizer lemmatizer = new Lemmatizer();
//		List<String> lemmas = lemmatizer.Lemmatize("artistic art attraction attract", false);
//        System.out.println(Arrays.toString(lemmas.toArray()));
		
//		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
//		String folderpath1 = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework3//";
//		String folderpath2 = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//resourcesIR//";
//		String folderpath3 = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//Cranfield//";
//		if (args.length > 0)
//		{
//			folderpath = args[0];
//		}
//		Query query = new Query();
//		query.LoadDictionary1(folderpath);
//		query.LoadQuerySupport(folderpath2);
		//query.LoadDictionaryWithPosition(folderpath3, folderpath2);
		//query.LoadDocTermInfo();
		
//		System.out.println("\nDictionary loaded");
//		Dictionary.PrintDict2(query.dict);
       
//		for(int i = 0; i < query.docInfoList.size(); i++)
//		{
//			System.out.println("doc + " + (i+ 1));
//			
//			Document doc = query.docInfoList.get(i);
//			for(int j = 0; j < doc.dict_termids.size(); j++)
//			{
//				int termid = doc.dict_termids.get(j);
//				Term term = query.dict.GetTerm(termid);
//				int docindex = term.postings.indexOf(doc.ID);
//				List<Integer> positions = QueryExpansion.GetDocTermPositions(term, docindex);
//				System.out.println(term.str + " " + Arrays.toString(positions.toArray()));
//			}
//			
//			System.out.println();
//		}
		
		//QueryExpansion4.BuildIndex3();
		
//		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		String projectpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//project//";
//		String resourcepath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//resourcesIR//";
//
//		Dict_E dict_e = new Dict_E();
//		dict_e.LoadQuerySupport(resourcepath);
//		dict_e.LoadDict(projectpath + "Dict Version 3//");
//		System.out.println("finish");
//		Dictionary.PrintDict(dict_e.dicts.get(1));
		
		//QueryExpansion4.WriteLink();
		
		Dict_E dict_e = new Dict_E();
		dict_e.LoadQuerySupport();
		
		for(int i = 0; i < 200; i++)
		{
		System.out.println(dict_e.links.get(i));
		}
		
		//List<String> webContents = QueryExpansion4.GetWebContents();
	}
}
