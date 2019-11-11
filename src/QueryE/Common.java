package QueryE;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Common {
	
    public static String GetFolder(int i)
    {
    	StringBuilder folder = new StringBuilder();
    	int firstlevel = i % 100;
    	
    	int secondlevel = (i / 100) % 100;
    	
    	int thirdlevel = (i / 100) % 100;
    	
    	folder.append(String.valueOf(firstlevel) + "//" + String.valueOf(secondlevel) + "//" + thirdlevel + "//");
    	
    	return folder.toString();
    }  
	
	public static List<String> ReadFileLine(String path, boolean isunix)
	{
		List<String> contentlines = new ArrayList<String>();
		if (isunix)
		{
			byte[] filebytes = ReadFile(path);
			StringBuilder tempword = new StringBuilder();
			for(byte b: filebytes)
			{
				if (b == 10)
				{
		    		if (tempword.length() > 0)
		    		{
		    			contentlines.add(tempword.toString().trim());
		    		}
		    		tempword.setLength(0);
				}
				
				tempword.append((char)b);
			}
			
			if (tempword.length() > 0)
			{
				contentlines.add(tempword.toString().trim());
			}
		}
		else
		{
			 String contentLine = "";
			BufferedReader br = null;
		
			int i = 0;
			
			try
			{
				br = new BufferedReader(new FileReader(path));
				   contentLine = br.readLine();
				   while (contentLine != null) {
					   contentlines.add(contentLine);
				      contentLine = br.readLine();
				   }

			}
			catch (Exception e)
			{
				System.out.print("\n byte[] read of size " + i + "\n");
				//System.out.print("\nerror occur when reading file " + filename + "\n" + e.getMessage());
			}
			finally
			{
				if (br != null)
				{
					try
					{
					    br.close();
					}
					catch(Exception e)
					{
						System.out.print("\nerror occur when closing file " + "sourcefile" + "\n" + e.getMessage());
					}
				}
			}
		}
		
		return contentlines;
	}
	
	public static byte[] ReadFile(String path)
	{
		File file = new File(path);
		DataInputStream reader = null;
		FileInputStream fileinput = null;
		String filename = file.getName();
		
		byte[] termbytes = null;
		int i = 0;
		
		try
		{
			fileinput = new FileInputStream(file);
		    reader = new DataInputStream(fileinput);
		    
		    int buffer = 1024;
		    
		    byte[] temp = new byte[buffer];
		    
		    
		    termbytes = new byte[reader.available()];
		    
		    int count = 0;
		    
		    
		    int readbytes = reader.read(temp);
		    while(readbytes > 0)
		    {
		    	while(count < readbytes)
		    	{
		    		termbytes[i++] = temp[count++];
		    	}
		    	
		    	temp = new byte[buffer];
		    	readbytes = reader.read(temp);
		    	count = 0;
		    }
		}
		catch (Exception e)
		{
			System.out.print("\n byte[] read of size " + i + "\n");
			//System.out.print("\nerror occur when reading file " + filename + "\n" + e.getMessage());
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
			if (fileinput != null)
			{
				try
				{
					fileinput.close();
				}
				catch(Exception e)
				{
					System.out.print("\nerror occur when closing file " + filename + "\n" + e.getMessage());
				}
			}
		}
		
		return termbytes;
	}
	
	public static List<String> ReadFile_Unix(String path)
	{
		List<String> contentlines = new ArrayList<String>();
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
		    			contentlines.add(tempword.toString().trim());
		    		}
		    		tempword.setLength(0);
		    	}
		    	
		    	tempword.append((char)num);
		    }
		    
    		if (tempword.length() > 0)
    		{
    			contentlines.add(tempword.toString().trim());
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
		
		return contentlines;
	}
}
