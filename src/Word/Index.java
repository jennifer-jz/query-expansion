package Word;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Index {

	public static int Sum(List<Integer> intlist)
	{
		int sum = 0;
		for(int inti: intlist)
		{
			sum += inti;
		}
		
		return sum;
	}
	public static byte[] IntToBytes4(int data) 
	{
		byte[] result = new byte[]{ 
		        (byte) ((data & 0xFF000000) >> 24),
		        (byte) ((data & 0x00FF0000) >> 16),
				(byte) ((data & 0x0000FF00) >> 8),
				(byte) ((data & 0x000000FF) >> 0)
		    };
		
		    return result;
    }
	
	public static byte[] IntToBytes3(int data) 
	{
		byte[] result = new byte[]{ 
		        (byte) ((data & 0x00FF0000) >> 16),
				(byte) ((data & 0x0000FF00) >> 8),
				(byte) ((data & 0x000000FF) >> 0)
		    };
		
		    return result;
    }
	
	public static void PrintBytes(byte[] bytes)
	{
		System.out.println(" ");
		for(int i = 0; i < bytes.length; i++)
		{
			System.out.print((int)bytes[i]);
			System.out.print(" ");
		}
		System.out.println(" ");
	}
	
	public static StringBuilder PrintBytesToInt(byte[] bytes)
	{
		StringBuilder str = new StringBuilder();
		for(int i = 0; i < bytes.length; i++)
		{
			str.append((int)bytes[i]);
			str.append(" ");
		}
		
		return str;
	}
	
	public static String GetBinaryString(byte onebyte)
	{
		StringBuilder str = new StringBuilder();
		String temp = Integer.toBinaryString(onebyte & 0xFF);
		if (temp.length() < 8)
		{
			str.append(String.format("%1$0"+(8-temp.length())+"d",0)); 
		}
		str.append(temp);
		
		return str.toString();
	}
	
	public static StringBuilder PrintBytesToBinary(byte[] bytes, boolean isspace)
	{
		StringBuilder str = new StringBuilder();
		String temp = "";
		for(int i = 0; i < bytes.length; i++)
		{
			temp = Integer.toBinaryString(bytes[i] & 0xFF); // There are difference between Integer.toBinaryString(int), Integer.toString(int, 2) and Byte.toString(byte). The latter two obtain signed result while the first one gets unsigned result
			if (temp.length() < 8)
			{
			    str.append(String.format("%1$0"+(8-temp.length())+"d",0)); 
			}
			str.append(temp);
			if (isspace)
			{
			    str.append(" ");
			    
			    if (i % 10 == 0)
			    {
			    	str.append("\r\n");
			    }
			}
			
			
		}
		
		return str;
	}
	
	
	public static int Byte4ToInt(byte[] bytes)
	{
	    int i = ((0xFF & bytes[0]) << 24) | ((0xFF & bytes[1]) << 16) |
	            ((0xFF & bytes[2]) << 8) | (0xFF & bytes[3]);
	    
	    return i;
	}
	
	public static int Byte3ToInt(byte[] bytes)
	{
	    int i = ((0xFF & bytes[0]) << 16) | ((0xFF & bytes[1]) << 8) |
	            (0xFF & bytes[2]);
	    
	    return i;
	}
	
	public static String GetStrFromIntegerList(List<Integer> intlist)
	{
		//List<Byte> strbytelist = new ArrayList<Byte>();
		byte[] strbytes = new byte[intlist.size() * 4];
		int count = 0;
		for(int i = 0; i < intlist.size(); i++)
		{
			byte[] intbyte = IntToBytes4(intlist.get(i));
			
			
			for(int j = 0; j < intbyte.length; j++)
			{
				if (intbyte[j] != 0)
				{
					strbytes[count++] = intbyte[j];
					//strbytelist.add(intbyte[j]);
				}
			}
			
			
			/*
			if (i == 0 && intbyte[0] == 0 && intbyte[1] == 0)
			{
				strbytes[count++] = intbyte[2];
				strbytes[count++] = intbyte[3]; 
			}
			else
			{
				strbytes[count++] = intbyte[0];
				strbytes[count++] = intbyte[1];
				strbytes[count++] = intbyte[2];
				strbytes[count++] = intbyte[3];
			}
			*/
			
		}
		
		String str = new String(strbytes);  // Is it necessary to remove the first two zero-value bytes?
		str = str.trim();
		return str;
	}
	
	public static String GetStrFromBytes(List<Byte> bytelist)
	{
		//List<Byte> strbytelist = new ArrayList<Byte>();
		byte[] strbytes = new byte[bytelist.size()];
		int count = 0;
		for(int j = 0; j < bytelist.size(); j++)
		{
			if (bytelist.get(j) != 0)
			{
				strbytes[count++] = bytelist.get(j);
			}
		}
		
		String str = new String(strbytes);
		str = str.trim();
		return str;
	}
	
	public static String GetFormmatedBitString(String bitstring)
	{
		StringBuilder formattedstr = new StringBuilder(bitstring);
		for(int i = bitstring.length(); i < 8; i++)
		{
			formattedstr.insert(0, '0');
		}
		
		return formattedstr.toString();
	}
	
	// To shrink concatenated bytes of gamma codes, it will be errorneous to cut the leading zeros in each byte!!
	public static byte[] ShrinkBytes(byte[] inputbytes)
	{
		StringBuilder temp = new StringBuilder();
		for(int i = 0; i < inputbytes.length; i++)
		{
			temp.append(Integer.toBinaryString((int)(inputbytes[i]) & 0xFF));
		}
		
		return GetBytesFromBitString(temp.toString());
	}
	
	public static byte[] GetBytesFromBitString(String bitstring)
	{
		int i = 0;
		int module = bitstring.length() % 8;
		int bytecount = bitstring.length() / 8;
		if (module > 0)
		{
			bytecount += 1;
		}
		byte[] bytes = new byte[bytecount];
		if (module > 0)
		{
			String temp =GetFormmatedBitString(bitstring.substring(0, module));
			bytes[i++] = Byte.parseByte(temp, 2);
		}
		int start = module;
		int end = module + 8;
		while(i < bytecount)
		{
			bytes[i++] = (byte)Integer.parseInt(bitstring.substring(start, end), 2);
			start += 8;
			end += 8;
		}
		
		return bytes;
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
	
	public static byte[] GetGammaCodeBytes(int gap)
	{
		return GetBytesFromBitString(GetGammaCode(gap));
	}
	
	public static String GetGammaCode(int gap)
	{
		StringBuilder code = new StringBuilder();
	
		String binary = Integer.toBinaryString(gap);
		//String binary = Integer.toBinaryString(gap & 0xFF);  when gap < 256
		code.append(binary);
		code.deleteCharAt(0);
		String length = GetUnaryCode(code.length());
		code.insert(0, length);
		
		return code.toString();
	}
	
	public static List<Integer> GetGammaDecode_Strong(byte[] gammacode)
	{
		StringBuilder longstr = PrintBytesToBinary(gammacode, false);
		
		List<Integer> intlist = new ArrayList<Integer>();
		int length = 0;
		char currentchar = '0';
		boolean ishead = false;
		boolean ispointer = true;
		boolean isleadingzero = true;
		StringBuilder tempstr = new StringBuilder();
		int wordindex = 0;
		byte[] pointer;
		
		int lineno = 0;
		
		for(int i = 0; i < longstr.length(); i++)
		{
			currentchar = longstr.charAt(i);
			
			if (ispointer)
			{
				pointer = new byte[3];
				for(int j = 0; j < 3; j++)
				{
					tempstr.setLength(0);
					for(int k = 0; k < 8; k++)
					{
						currentchar = longstr.charAt(i++);
						tempstr.append(currentchar);
					}
					pointer[j] = (byte)Integer.parseInt(tempstr.toString(), 2);
				}
				
				intlist.add(Byte3ToInt(pointer));
				ishead = true;
				ispointer = false;
				i--;

			}
			else if (ishead)
			{
				if (currentchar == '1')
				{
					length++;
					isleadingzero = false;
				}
				else
				{
					if (length == 0) // zero
					{
						boolean islinebreak = true;
						int j = 0;
						for(j = 0; j < 16; j++)
						{
							if (i + j >= longstr.length())
							{
								islinebreak = false;
								break;
							}
							
							currentchar = longstr.charAt(i + j);
							if (currentchar != '0')
							{
								islinebreak = false;
								break;
							}
						}
						if (islinebreak)
						{
							intlist.add(-1);  // as a line break
							isleadingzero = true;  // start the next line
							
							lineno++;
							
							if (lineno % 8 == 0)
							{
								ispointer = true;
							}
							else
							{
								ispointer = false;
							}
						
						}
						else if (isleadingzero)
						{
							if (j >= 8)
							{
							    intlist.add(1);   // special case, the gamma code for 1 is 0
							    isleadingzero = false;
							}
						}
						else if (j == 1)  // not leading zero
						{
							intlist.add(1);  // special case, the gamma code for 1 is 0
						}
						i = i + j - 1;
						
					}
					else
					{
					//length++; // the last one increment to make length the actual length of the int string
					ishead = false;
					//wordindex = 0;
					}
				}
				
			}
			else
			{
				wordindex = 0;
				tempstr.setLength(0);
				while(wordindex < length)  // length cannot be zero
				{
					currentchar = longstr.charAt(i + wordindex);
				    tempstr.append(currentchar);
				    wordindex++;
				}
				
				i = i + length - 1;
				
				tempstr.insert(0, '1');
				intlist.add(Integer.valueOf(tempstr.toString(), 2));
				ishead = true;
				length = 0;
			}
		}
		
		return intlist;
	}
	
	public static List<Integer> GetGammaDecode(StringBuilder gammacode)
	{
		List<Integer> intlist = new ArrayList<Integer>();
		int length = 0;
		char currentchar = '0';
		boolean ishead = true;
		boolean isleadingzero = true;
		StringBuilder tempstr = new StringBuilder();
		int wordindex = 0;
		
		for(int i = 0; i < gammacode.length(); i++)
		{
			currentchar = gammacode.charAt(i);
			
			if (ishead)
			{
				if (currentchar == '1')
				{
					length++;
					isleadingzero = false;
				}
				else
				{
					if (length == 0) // zero
					{
						boolean islinebreak = true;
						int j = 0;
						for(j = 0; j < 16; j++)
						{
							if (i + j >= gammacode.length())
							{
								islinebreak = false;
								break;
							}
							
							currentchar = gammacode.charAt(i + j);
							if (currentchar != '0')
							{
								islinebreak = false;
								break;
							}
						}
						if (islinebreak)
						{
							intlist.add(-1);  // as a line break
							isleadingzero = true;  // start the next line
						}
						else if (isleadingzero)
						{
							if (j >= 8)
							{
							    intlist.add(1);   // special case, the gamma code for 1 is 0
							    isleadingzero = false;
							}
						}
						else if (j == 1)  // not leading zero
						{
							intlist.add(1);  // special case, the gamma code for 1 is 0
						}
						
						i = i + j - 1;
					}
					else
					{
					//length++; // the last one increment to make length the actual length of the int string
					ishead = false;
					//wordindex = 0;
					}
				}
				
			}
			else
			{
				wordindex = 0;
				tempstr.setLength(0);
				while(wordindex < length)  // length cannot be zero
				{
					currentchar = gammacode.charAt(i + wordindex);
				    tempstr.append(currentchar);
				    wordindex++;
				}
				
				i = i + length - 1;
				
				tempstr.insert(0, '1');
				intlist.add(Integer.valueOf(tempstr.toString(), 2));
				ishead = true;
				length = 0;
			}
		}
		
		return intlist;
	}
	
	public static List<Integer> GetGammaDecode(byte[] gammacode)
	{
		StringBuilder longstr = PrintBytesToBinary(gammacode, false);

		return GetGammaDecode(longstr);

	}
	
	public static byte[] GetDeltaCodeBytes(int gap)
	{
		return GetBytesFromBitString(GetDeltaCode(gap));
	}
	
	public static String GetDeltaCode(int gap)
	{
		StringBuilder code = new StringBuilder();
		
		String binary = Integer.toBinaryString(gap);
		// String binary = Integer.toBinaryString(gap & 0xFF); // when gap < 256
		code.append(binary);
		String length = GetGammaCode(code.length());
		code.deleteCharAt(0);
		code.insert(0, length);
		
		return code.toString();
	}
	
	public static List<Integer> GetDeltaDecode_Strong(byte[] gammacode)
	{
		StringBuilder longstr = PrintBytesToBinary(gammacode, false);
		
		List<Integer> intlist = new ArrayList<Integer>();
		int length = 0;
		char currentchar = '0';
		boolean ishead = false;
		boolean ispointer = true;
		boolean isleadingzero = true;
		StringBuilder tempstr = new StringBuilder();
		
		boolean isheadofhead = true;
		int bodylength = 0;
		
		int wordindex = 0;
		byte[] pointer;
		
		int lineno = 0;
		
		for(int i = 0; i < longstr.length(); i++)
		{
			currentchar = longstr.charAt(i);
			
			if (ispointer)
			{
				pointer = new byte[3];
				for(int j = 0; j < 3; j++)
				{
					tempstr.setLength(0);
					for(int k = 0; k < 8; k++)
					{
						currentchar = longstr.charAt(i++);
						tempstr.append(currentchar);
					}
					pointer[j] = (byte)Integer.parseInt(tempstr.toString(), 2);
				}
				
				intlist.add(Byte3ToInt(pointer));
				ishead = true;
				isheadofhead = true;
				ispointer = false;
				i--;

			}
			else if (ishead)
			{
				if (isheadofhead)
				{
					if (currentchar == '1')
					{
						length++;
						isleadingzero = false;
					}
					else
					{
						if (length == 0) // zero
						{
							boolean islinebreak = true;
							int j = 0;
							for(j = 0; j < 16; j++)
							{
								if (i + j >= longstr.length())
								{
									islinebreak = false;
									break;
								}
								
								currentchar = longstr.charAt(i + j);
								if (currentchar != '0')
								{
									islinebreak = false;
									break;
								}
							}
							if (islinebreak)
							{
								intlist.add(-1);  // as a line break
								isleadingzero = true;  // start the next line
								
								lineno++;
								
								if (lineno % 4 == 0)
								{
									ispointer = true;
								}
								else
								{
									ispointer = false;
								}
								
								//System.out.println(Arrays.toString(intlist.toArray()));
							
							}
							else if (isleadingzero)
							{
								if (j >= 8)
								{
								    intlist.add(1);   // special case, the gamma code for 1 is 0
								    isleadingzero = false;
								}
							}
							else if (j == 1)  // not leading zero
							{
								intlist.add(1);  // special case, the gamma code for 1 is 0
							}
							i = i + j - 1;
							
						}
						else
						{
						//length++; // the last one increment to make length the actual length of the int string
							isheadofhead = false;
						//wordindex = 0;
						}
					}

				}
				else
				{
					wordindex = 0;
					tempstr.setLength(0);
					while(wordindex < length)  // length cannot be zero
					{
						currentchar = longstr.charAt(i + wordindex);
					    tempstr.append(currentchar);
					    wordindex++;
					}
					
					i = i + length - 1;
					
					tempstr.insert(0, '1');
					bodylength = Integer.valueOf(tempstr.toString(), 2);
					
					isheadofhead = false;
					ishead = false;
					length = 0;
				}
				
			}
			else
			{
				wordindex = 0;
				tempstr.setLength(0);
				while(wordindex < bodylength - 1)  // length cannot be zero
				{
					currentchar = longstr.charAt(i + wordindex);
				    tempstr.append(currentchar);
				    wordindex++;
				}
				
				i = i + bodylength - 2;
				
				tempstr.insert(0, '1');
				intlist.add(Integer.valueOf(tempstr.toString(), 2));
				
				isheadofhead = true;
				ishead = true;
				bodylength = 0;
			}
		}
		
		return intlist;
	}
	
	public static List<Integer> GetDeltaDecode(StringBuilder deltacodestring)
	{
		List<Integer> intlist = new ArrayList<Integer>();
		int length = 0;
		char currentchar = '0';
		boolean ishead = true;
		boolean isheadofhead = true;
		StringBuilder tempstr = new StringBuilder();
		int wordindex = 0;
		int bodylength = 0;
		
		for(int i = 0; i < deltacodestring.length(); i++)
		{
			currentchar = deltacodestring.charAt(i);
			
			if (ishead)
			{
				if (isheadofhead)
				{
					if (currentchar == '1')
					{
						length++;
					}
					else
					{
						if (length == 0)
						{
							boolean islinebreak = true;
							int j = 0;
							for(j = 0; j < 8; j++)
							{
								if (i + j >= deltacodestring.length())
								{
									islinebreak = false;
									break;
								}
								
								currentchar = deltacodestring.charAt(i + j);
								if (currentchar != '0')
								{
									islinebreak = false;
									break;
								}
							}
							if (islinebreak)
							{
								intlist.add(-1);  // as a line break
							}
							
							i = i + j - 1;
						}
						else
						{
						//length++; // the last one increment to make length the actual length of the int string
							isheadofhead = false;
						//wordindex = 0;
						}
					}
					
				}
				else
				{
					wordindex = 0;
					tempstr.setLength(0);
					while(wordindex < length)  // length cannot be zero
					{
						currentchar = deltacodestring.charAt(i + wordindex);
					    tempstr.append(currentchar);
					    wordindex++;
					}
					
					i = i + length - 1;
					
					tempstr.insert(0, '1');
					
					bodylength = Integer.valueOf(tempstr.toString(), 2);
					
					isheadofhead = false;
					ishead = false;
					length = 0;
				}
			}
			else
			{
					wordindex = 0;
					tempstr.setLength(0);
					
					while(wordindex < bodylength - 1)  // length cannot be zero
					{
						currentchar = deltacodestring.charAt(i + wordindex);
					    tempstr.append(currentchar);
					    wordindex++;
					}
					
					i = i + bodylength - 2;
					
					tempstr.insert(0, '1');
					
					intlist.add(Integer.valueOf(tempstr.toString(), 2));
					
					isheadofhead = true;
					ishead = true;
					//length = 0;
					bodylength = 0;
			}
		}
		
		return intlist;
	}
	
	public static List<Integer> GetDeltaDecode(byte[] deltacode)
	{
		StringBuilder longstr = PrintBytesToBinary(deltacode, false);
		
		return GetDeltaDecode(longstr);


	}
	
	public static void WriteTermString(String path, Dictionary myDict)
	{
		DataOutputStream output = null;
		FileOutputStream filestream = null;
		
        try 
        {
        	File file = new File(path);
        	if (!file.exists())
        	{
        		file.getParentFile().mkdirs();
        		file.createNewFile();
        	}
        	//filestream = new FileOutputStream(path);
        	filestream = new FileOutputStream(path, false);
        	filestream.write(("").getBytes());  //clear existing content
        	
        	output = new DataOutputStream(filestream);
            
            Term currentTerm;
    		for(int i= 0; i < myDict.Size(); i++)
    		{
    			currentTerm = myDict.GetTerm(i);
    			output.write(currentTerm.str.length());  // write a one-byte integer as the length of term string
    			output.writeChars(currentTerm.str);
    		}
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            if (output != null) 
            {
                try {
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            if (filestream != null)
            {
            	try {
					filestream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
	}

	public static byte[] ReadTermString(String path)
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
	
	public static void PrintLongString2(byte[] longstrbytes)
	{
		StringBuilder longStr = new StringBuilder();
		int pointer = 0;
		int length = (int)longstrbytes[pointer];
		//List<Byte> termbytes = new ArrayList<Byte>();
		
		while(pointer < longstrbytes.length)
		{
			length = (int)longstrbytes[pointer++];
			longStr.append(length);
			//longStr.append(" ");
			for(int i = 0; i <= length; i++)
			{
				//int tempint = (int)longstrbytes[pointer++];
				//Character tempchar = (char)tempint;
				//char[] tempchars = new char[]{tempint};
				if (pointer < longstrbytes.length)
				{
				longStr.append((char)longstrbytes[pointer++]);
				//longStr.append(" ");
				}
			}
			
			longStr.append("\r\n");
		}
		
		System.out.println(" ");
		System.out.print(longStr.toString());
		System.out.println(" ");
	}
	
	public static void PrintLongString(byte[] longstrbytes)
	{
		StringBuilder longStr = new StringBuilder();
		int pointer = 0;
		int length = (int)longstrbytes[pointer];
		//List<Byte> termbytes = new ArrayList<Byte>();
		
		while(pointer < longstrbytes.length)
		{
			length = (int)longstrbytes[pointer++];
			longStr.append(length);
			//longStr.append(" ");
			for(int i = 0; i < length; i++)
			{
				//int tempint = (int)longstrbytes[pointer++];
				//Character tempchar = (char)tempint;
				//char[] tempchars = new char[]{tempint};
				if (pointer < longstrbytes.length)
				{
				longStr.append((char)longstrbytes[pointer++]);
				//longStr.append(" ");
				}
			}
			
			longStr.append("\r\n");
		}
		
		System.out.println(" ");
		System.out.print(longStr.toString());
		System.out.println(" ");
	}
	
	public static void PrintLongString3(byte[] longstrbytes)
	{
		StringBuilder longStr = new StringBuilder();
		int pointer = 0;
		int length = (int)longstrbytes[pointer];
		List<Byte> termbytes = new ArrayList<Byte>();
		
		while(pointer < longstrbytes.length)
		{
			length = (int)longstrbytes[pointer];
			longStr.append(length);
			termbytes = new ArrayList<Byte>();
			int i = 1;
			while(i <= length)
			{
				termbytes.add(longstrbytes[pointer + i]);
				i++;
			}
			
			longStr.append(GetStrFromBytes(termbytes));
			longStr.append("\r\n");
			pointer += 1;
			pointer += length;
		}
		
		System.out.println(" ");
		System.out.print(longStr.toString());
		System.out.println(" ");
	}
	
	public static String GetTermFromLongString(byte[] termstring, int pointer, int offset)
	{
		String term = "";
		
		int length = (int)termstring[pointer];
		int offseti = 0;
		while(offseti < offset)
		{
			pointer += 1;
			pointer += length;
			
			if (pointer >= termstring.length)
			{
				break;
			}
			
			length = (int)termstring[pointer];
			offseti += 1;
		}
		
		List<Byte> termbytes = new ArrayList<Byte>();
		int i = 0;
		while(i < length)
		{
			termbytes.add(termstring[++pointer]);
			i++;
		}
		
		term = GetStrFromBytes(termbytes);
		
		return term;
	}
	
	public static String GetTermFromLongString_FrontCoding(byte[] termstring, int pointer, int offset)
	{
		StringBuilder term = new StringBuilder();
		
		int length = (int)termstring[pointer];
		int offseti = 0;
		
		char tempchar;
		
		int internalpointer = pointer + 1;
		for(int i = 1; i < length + 2; i++)
		{
			tempchar = (char)termstring[internalpointer++];
			if (tempchar != '*')
			{
				term.append(tempchar);
			}
			else
			{
				break;
			}
		}
		
		int headlength = term.length();
		
		if (offset == 0)
		{
			for(int i = 0; i < length - headlength; i++)
			{
				tempchar = (char)termstring[internalpointer++];
				term.append(tempchar);
			}
		}
		else
		{
			while(offseti < offset)
			{
				pointer += 2;
				pointer += length;
				
				if (pointer >= termstring.length)
				{
					break;
				}
				
				length = (int)termstring[pointer];
				offseti += 1;
			}
			
			length = (int)termstring[pointer];
			pointer += 2;
			for(int i = 0; i < length; i++)
			{
				tempchar = (char)termstring[pointer++];
				term.append(tempchar);
			}
		}
		
		return term.toString();
	}
	
	public static void WriteFile(String filepath, byte[] data)
	{
        try
        {
        	File file = new File(filepath);
        	if (!file.exists())
        	{
        		file.getParentFile().mkdirs();
        		file.createNewFile();
        	}
        	
        	Path path = Paths.get(filepath);
            // truncate and overwrite an existing file, or create the file if
            // it doesn't initially exist
            DataOutputStream stream = new DataOutputStream(
              new BufferedOutputStream(
                    Files.newOutputStream(path)
                )
            );
            for(int i = 0; i<data.length; i++){
                stream.writeByte(data[i]);
            }
            
            stream.close();
        } 
        catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
	}
	
	public static StringBuilder GetCommonChars(List<String> terms)
	{
		StringBuilder commonchar = new StringBuilder();
		if (terms.size() > 0 && terms.get(0).length() > 0)
		{
			int totallength = terms.get(0).length();
			char currentchar = 0;
			for(int i = 0; i < totallength; i++)
			{
				boolean iscommon = true;
				currentchar = terms.get(0).charAt(i);
				for(int j = 1; j < terms.size(); j++)
				{
					if (i >= terms.get(j).length())
					{
						iscommon = false;
						break;
					}
					
					if (currentchar != terms.get(j).charAt(i))
					{
						iscommon = false;
						break;
					}
				}
				
				if (iscommon)
				{
					commonchar.append(currentchar);  //StringBuilder appends an integer as a string, not as a character
				}
				else
				{
					break;
				}
			}
		}
		
		return commonchar;
	}
	
	// block = 4
	public static void WriteCompressedIndex_FrontCoding(String indexpath, String stringpath, Dictionary myDict, List<Document> docList)
	{
        List<Integer> postings = new ArrayList<Integer>();
        List<Integer> tfreqs = new ArrayList<Integer>();
        Document currentDoc;
        Term currentTerm;
        
        int termpointer = 0;  // offset in the byte array
        
        ByteArrayOutputStream indexbytestream = new ByteArrayOutputStream();
        ByteArrayOutputStream stringbytestream = new ByteArrayOutputStream();
        StringBuilder tempgammacodes = new StringBuilder();
        
        List<String> tempterms = new ArrayList<String>();
        
        byte[] shinkedlinebytes;
        
        try
        {
        
		for(int i= 0; i < myDict.Size(); i++)
		{
			currentTerm = myDict.GetTerm(i);
			postings = currentTerm.postings;
			tfreqs = currentTerm.tfreqs;

			if (i % 4 == 0)  //a blocked compression with k=8
			{
				if (tempterms.size() > 0)
				{
					int firsttermlength = tempterms.get(0).length();
					StringBuilder commonchars = GetCommonChars(tempterms);
					int headlength = commonchars.length();
					stringbytestream.write(firsttermlength);
					termpointer += 1;
					
					for(int j = 0; j < commonchars.length(); j++)
					{
						stringbytestream.write((byte)(commonchars.charAt(j)));
						termpointer += 1;
					}
					
					// This operation leads to uncontrolled length of bytes taken for a string
					//tempbytes = commonchars.toString().getBytes();
					//stringbytestream.write(tempbytes);
					//termpointer += tempbytes.length;
					
					stringbytestream.write((byte)'*');
					termpointer += 1;
					
					for(int j = commonchars.length(); j < firsttermlength; j++)
					{
						stringbytestream.write((byte)(tempterms.get(0).charAt(j)));
						termpointer += 1;
					}
					
					for(int j = 1; j < tempterms.size(); j++)
					{
						stringbytestream.write(tempterms.get(j).length() - headlength);
						stringbytestream.write((byte)'&');
						termpointer += 2;
						for(int k = headlength; k < tempterms.get(j).length(); k++)
						{
							stringbytestream.write((byte)(tempterms.get(j).charAt(k)));
							termpointer += 1;
						}
					}
				}
				
				indexbytestream.write(IntToBytes3(termpointer)); // write a term pointer as a 3-byte integer
				
				tempterms = new ArrayList<String>();
			}
			
			tempgammacodes.setLength(0);
			if (currentTerm.dfreq == 1)
			{
				tempgammacodes.append("0000000");  // there should be an additional zero in the subsequent append operation. In this way, the first byte being zero can be used to recognize tfreq = 1;
			}
			tempgammacodes.append(GetDeltaCode(currentTerm.dfreq));//write df
			//indexbytestream.write(IntToBytes4(currentTerm.dfreq)); 
			
			int lastdocid = 0;
			for(int j = 0; j < postings.size(); j++)
			{
				tempgammacodes.append(GetDeltaCode(postings.get(j) - lastdocid));// write a docid
				tempgammacodes.append(GetDeltaCode(tfreqs.get(j)));// write tfreq of the current docid
				//indexbytestream.write(IntToBytes4(postings.get(j) - lastdocid)); 
				//indexbytestream.write(IntToBytes4(tfreqs.get(j)));  
				currentDoc = docList.get(postings.get(j) - 1);
				
				tempgammacodes.append(GetDeltaCode(currentDoc.max_tf));// write max_ft of the current docid
				tempgammacodes.append(GetDeltaCode(currentDoc.doclen));// write doclen of the current docid
				//indexbytestream.write(IntToBytes4(currentDoc.max_tf)); 
				//indexbytestream.write(IntToBytes4(currentDoc.doclen)); 
				
				lastdocid = postings.get(j);
			}
			// It is better not to write mixed data types, because it will be difficult to find a line break when reading the file.
			// The int value for a '\n' when reading file is 10, which is easily confused with an existing document id 10 or any higher byte out of every 4 bytes of an integer equaling 10.
			// output.writeChar('\n');  
			// output.write(0);  // since there should be no zeros in the index, 0 can be used as a line break
			
			shinkedlinebytes = GetBytesFromBitString(tempgammacodes.toString());
			indexbytestream.write(shinkedlinebytes);
			
			
			indexbytestream.write((byte)0);  //line breaker
			indexbytestream.write((byte)0);
			//indexbytestream.write(IntToBytes4(-1));
			
			tempterms.add(currentTerm.str);

		}
		
		if (tempterms.size() > 0)
		{
			int firsttermlength = tempterms.get(0).length();
			StringBuilder commonchars = GetCommonChars(tempterms);
			int headlength = commonchars.length();
			stringbytestream.write(firsttermlength);
			termpointer += 1;
			
			for(int j = 0; j < commonchars.length(); j++)
			{
				stringbytestream.write((byte)(commonchars.charAt(j)));
				termpointer += 1;
			}
			//tempbytes = commonchars.toString().getBytes();
			//stringbytestream.write(tempbytes);
			//termpointer += tempbytes.length;
			
			stringbytestream.write((byte)'*');
			termpointer += 1;
			
			for(int j = commonchars.length(); j < firsttermlength; j++)
			{
				stringbytestream.write((byte)(tempterms.get(0).charAt(j)));
				termpointer += 1;
			}
			
			for(int j = 1; j < tempterms.size(); j++)
			{
				stringbytestream.write(tempterms.get(j).length() - headlength);
				stringbytestream.write((byte)'&');
				termpointer += 2;
				for(int k = headlength; k < tempterms.get(j).length(); k++)
				{
					stringbytestream.write((byte)(tempterms.get(j).charAt(k)));
					termpointer += 1;
				}
			}
		}
	
		
		
		byte[] indexbytes = indexbytestream.toByteArray();
		byte[] stringbytes = stringbytestream.toByteArray();
		indexbytestream.close();
		stringbytestream.close();
		
		System.out.println("size of compressed index with delta encoding: " + indexbytes.length);
		System.out.println("size of long string for front-coding version: " + stringbytes.length);
		System.out.println("number of inverted lists: " + myDict.Size());
		
		WriteFile(indexpath, indexbytes);
		WriteFile(stringpath, stringbytes);
		
        }
        catch(IOException e)
        {
        	e.printStackTrace();
        }
	}
	
	public static Dictionary ReadCompressedIndex_FrontCoding(String path, List<Document> docList, byte[] longstr)
	{
		byte[] data = ReadBytes(path);
		
		//System.out.println(PrintBytesToBinary(data, true).toString());
		//System.out.println(" ");
		
		List<Integer> intlist = GetDeltaDecode_Strong(data);
		

		//System.out.println(Arrays.toString(intlist.toArray()));
		
		Dictionary myDict = GetDictionaryFromIntList_DeltaPointer(intlist, longstr);
		
		return myDict;
	}
	
	// block = 4
	public static void WriteCompressedIndex_FrontCoding_Dict(String indexpath, String stringpath, Dictionary myDict, List<Document> docList)
	{
        List<Integer> postings = new ArrayList<Integer>();
        List<Integer> tfreqs = new ArrayList<Integer>();
        Document currentDoc;
        Term currentTerm;
        
        int termpointer = 0;  // offset in the byte array
        
        ByteArrayOutputStream indexbytestream = new ByteArrayOutputStream();
        ByteArrayOutputStream stringbytestream = new ByteArrayOutputStream();        
        
        List<String> tempterms = new ArrayList<String>();
        
        byte[] tempbytes;
        
        try
        {
        
		for(int i= 0; i < myDict.Size(); i++)
		{
			currentTerm = myDict.GetTerm(i);
			postings = currentTerm.postings;
			tfreqs = currentTerm.tfreqs;

			if (i % 4 == 0)  //a blocked compression with k=8
			{
				if (tempterms.size() > 0)
				{
					int firsttermlength = tempterms.get(0).length();
					StringBuilder commonchars = GetCommonChars(tempterms);
					int headlength = commonchars.length();
					stringbytestream.write(firsttermlength);
					termpointer += 1;
					
					for(int j = 0; j < commonchars.length(); j++)
					{
						stringbytestream.write((byte)(commonchars.charAt(j)));
						termpointer += 1;
					}
					
					// This operation leads to uncontrolled length of bytes taken for a string
					//tempbytes = commonchars.toString().getBytes();
					//stringbytestream.write(tempbytes);
					//termpointer += tempbytes.length;
					
					stringbytestream.write((byte)'*');
					termpointer += 1;
					
					for(int j = commonchars.length(); j < firsttermlength; j++)
					{
						stringbytestream.write((byte)(tempterms.get(0).charAt(j)));
						termpointer += 1;
					}
					
					for(int j = 1; j < tempterms.size(); j++)
					{
						stringbytestream.write(tempterms.get(j).length() - headlength);
						stringbytestream.write((byte)'&');
						termpointer += 2;
						for(int k = headlength; k < tempterms.get(j).length(); k++)
						{
							stringbytestream.write((byte)(tempterms.get(j).charAt(k)));
							termpointer += 1;
						}
					}
				}
				
				indexbytestream.write(IntToBytes3(termpointer)); // write a term pointer as a 3-byte integer
				
				tempterms = new ArrayList<String>();
			}
			
			indexbytestream.write(IntToBytes4(currentTerm.dfreq)); //write df
			
			int lastdocid = 0;
			for(int j = 0; j < postings.size(); j++)
			{
				indexbytestream.write(IntToBytes4(postings.get(j) - lastdocid)); // write a docid
				indexbytestream.write(IntToBytes4(tfreqs.get(j)));  // write tfreq of the current docid
				currentDoc = docList.get(postings.get(j) - 1);
				indexbytestream.write(IntToBytes4(currentDoc.max_tf)); // write max_ft of the current docid
				indexbytestream.write(IntToBytes4(currentDoc.doclen)); // write doclen of the current docid
				
				lastdocid = postings.get(j);
			}
			// It is better not to write mixed data types, because it will be difficult to find a line break when reading the file.
			// The int value for a '\n' when reading file is 10, which is easily confused with an existing document id 10 or any higher byte out of every 4 bytes of an integer equaling 10.
			// output.writeChar('\n');  
			// output.write(0);  // since there should be no zeros in the index, 0 can be used as a line break
			indexbytestream.write(IntToBytes4(-1));
			
			tempterms.add(currentTerm.str);

		}
		
		if (tempterms.size() > 0)
		{
			int firsttermlength = tempterms.get(0).length();
			StringBuilder commonchars = GetCommonChars(tempterms);
			int headlength = commonchars.length();
			stringbytestream.write(firsttermlength);
			termpointer += 1;
			
			for(int j = 0; j < commonchars.length(); j++)
			{
				stringbytestream.write((byte)(commonchars.charAt(j)));
				termpointer += 1;
			}
			//tempbytes = commonchars.toString().getBytes();
			//stringbytestream.write(tempbytes);
			//termpointer += tempbytes.length;
			
			stringbytestream.write((byte)'*');
			termpointer += 1;
			
			for(int j = commonchars.length(); j < firsttermlength; j++)
			{
				stringbytestream.write((byte)(tempterms.get(0).charAt(j)));
				termpointer += 1;
			}
			
			for(int j = 1; j < tempterms.size(); j++)
			{
				stringbytestream.write(tempterms.get(j).length() - headlength);
				stringbytestream.write((byte)'&');
				termpointer += 2;
				for(int k = headlength; k < tempterms.get(j).length(); k++)
				{
					stringbytestream.write((byte)(tempterms.get(j).charAt(k)));
					termpointer += 1;
				}
			}
		}
	
		
		
		byte[] indexbytes = indexbytestream.toByteArray();
		byte[] stringbytes = stringbytestream.toByteArray();
		indexbytestream.close();
		stringbytestream.close();
		
		WriteFile(indexpath, indexbytes);
		WriteFile(stringpath, stringbytes);
		
        }
        catch(IOException e)
        {
        	e.printStackTrace();
        }
	}
	
	public static Dictionary ReadCompressedIndex_FrontCoding_Dict(String path, List<Document> docList, byte[] longstr)
	{
		Dictionary myDict = new Dictionary();
		byte[] data = ReadBytes(path);
	    byte[] intbytes = new byte[4];
	    byte[] ptrbytes = new byte[3];
	    
	    boolean istermptr = true;
	    
	    int num = 0;
	    
	    int module = 0;
	    
	    Term term = new Term();
	    
	    int linecount = 0;
	    
	    //PrintBytes(data);
	    
	    for(int i = 0; i < data.length; i++)
	    {
	    	if (istermptr)
	    	{
	    		if (linecount % 4 == 0)
	    		{
		    		ptrbytes = new byte[3];
		    		ptrbytes[0] = data[i++];
		    		ptrbytes[1] = data[i++];
		    		ptrbytes[2] = data[i];
		    		
		    		int pointer = Byte3ToInt(ptrbytes);
		    		
		    		term.str = GetTermFromLongString_FrontCoding(longstr, pointer, 0);

	    		}
	    		else
	    		{
		    		int pointer = Byte3ToInt(ptrbytes);
		    		
		    		term.str = GetTermFromLongString_FrontCoding(longstr, pointer, linecount % 4);
	    			i--; // reread intbytes
	    		}
	    		
	    		module = 0;
	    		istermptr = false;
	    	}
	    	else
	    	{
	    		intbytes = new byte[4];
	    		intbytes[0] = data[i++];
	    		intbytes[1] = data[i++];
	    		intbytes[2] = data[i++];
	    		intbytes[3] = data[i];
	    		
	    		num = Byte4ToInt(intbytes);
	    		if (num == -1)
	    		{
    				myDict.Add(term);
    				term = new Term();
    				
    				istermptr = true;
    				module = 0;
    				linecount++;
    				
	    		}
	    		else
	    		{
	    			if (module == 0)
	    			{
	    				term.dfreq = num;
	    			}
	    			else if (module == 1)
	    			{
	    				term.postings.add(num);
	    			}
	    			else if (module == 2)
	    			{
	    				term.tfreqs.add(num);
	    			}
	    			
	    			module +=1;
	    			
	    			if (module > 4)
	    			{
	    				module = module - 4;
	    			}
	    		}
	    	}
	    }
		
		
		return myDict;
	}
	
	// block size = 8, for every 8 pointers, write only the first
	public static void WriteCompressedIndex_Block8(String indexpath, String stringpath, Dictionary myDict, List<Document> docList)
	{
        ByteArrayOutputStream indexbytestream = new ByteArrayOutputStream();
        ByteArrayOutputStream stringbytestream = new ByteArrayOutputStream();
        StringBuilder tempgammacodes = new StringBuilder();
        
        try 
        {
        	int blocksize = 8;
            List<Integer> postings = new ArrayList<Integer>();
            List<Integer> tfreqs = new ArrayList<Integer>();
            Document currentDoc;
            Term currentTerm;
            
            byte[] shinkedlinebytes;
            
            int termpointer = 0;  // offset in the byte array
            
    		for(int i= 0; i < myDict.Size(); i++)
    		{
    			currentTerm = myDict.GetTerm(i);
    			postings = currentTerm.postings;
    			tfreqs = currentTerm.tfreqs;

    			if (i % 8 == 0)  //a blocked compression with k=8
    			{
    				indexbytestream.write(IntToBytes3(termpointer));  // write a term pointer as a 3-byte integer
    			}
    			
    			tempgammacodes.setLength(0);
    			
    			// when freq == 1, the gamma code is zero. Since it's the head zero, it should be differentiated with position occupying zeros
    			if (currentTerm.dfreq == 1)
    			{
    				tempgammacodes.append("0000000");  // there should be an additional zero in the subsequent append operation. In this way, the first byte being zero can be used to recognize tfreq = 1;
    			}
    			tempgammacodes.append(GetGammaCode(currentTerm.dfreq));//write df, gamma-encoded
    			
    			//tempbytestream.write(GetGammaCodeBytes(currentTerm.dfreq)); 
    			//indexbytestream.write(IntToBytes4(currentTerm.dfreq)); 
    			
    			int lastdocid = 0;
    			for(int j = 0; j < postings.size(); j++)
    			{
    				tempgammacodes.append(GetGammaCode(postings.get(j) - lastdocid));// write a docid
    				tempgammacodes.append(GetGammaCode(tfreqs.get(j)));// write tfreq of the current docid
    				//tempbytestream.write(GetGammaCodeBytes(postings.get(j) - lastdocid)); 
    				//tempbytestream.write(GetGammaCodeBytes(tfreqs.get(j))); 
    				//indexbytestream.write(IntToBytes4(postings.get(j) - lastdocid)); // write a docid
    				//indexbytestream.write(IntToBytes4(tfreqs.get(j)));  // write tfreq of the current docid
    				currentDoc = docList.get(postings.get(j) - 1);
    				
    				tempgammacodes.append(GetGammaCode(currentDoc.max_tf));// write max_ft of the current docid
    				tempgammacodes.append(GetGammaCode(currentDoc.doclen));// write doclen of the current docid
    				//tempbytestream.write(GetGammaCodeBytes(currentDoc.max_tf));
    				//tempbytestream.write(GetGammaCodeBytes(currentDoc.doclen));
    				//indexbytestream.write(IntToBytes4(currentDoc.max_tf)); 
    				//indexbytestream.write(IntToBytes4(currentDoc.doclen)); 
    				
    				lastdocid = postings.get(j);
    			}
    			
    			//shinkedlinebytes = ShrinkBytes(tempbytestream.toByteArray());
    			
    			//shinkedlinebytes = tempbytestream.toByteArray();
    			shinkedlinebytes = GetBytesFromBitString(tempgammacodes.toString());
    			
    			//System.out.println(PrintBytesToBinary(shinkedlinebytes, true));
    			
    			indexbytestream.write(shinkedlinebytes);
    			//tempbytestream.close();
    			
    			// It is better not to write mixed data types, because it will be difficult to find a line break when reading the file.
    			// The int value for a '\n' when reading file is 10, which is easily confused with an existing document id 10 or any higher byte out of every 4 bytes of an integer equaling 10.
    			// output.writeChar('\n');  
    			// output.write(0);  // since there should be no zeros in the index, 0 can be used as a line break
    			
    			indexbytestream.write((byte)0);  //line splitter two zero bytes because there can be one zero byte for gamma code of 1
    			indexbytestream.write((byte)0);
    			//indexbytestream.write(IntToBytes4(-1));
    			
    			stringbytestream.write(currentTerm.str.length());  // write a one-byte integer as the length of term string
    			
    			for(int j = 0; j < currentTerm.str.length(); j++)
    			{
    				stringbytestream.write((byte)currentTerm.str.charAt(j));
    			}
    			//output2.writeChars(currentTerm.str);
    			
    			termpointer += 1;
    			termpointer += currentTerm.str.length();
    		}
    		
    		byte[] indexbytes = indexbytestream.toByteArray();
    		byte[] stringbytes = stringbytestream.toByteArray();
    		indexbytestream.close();
    		stringbytestream.close();
    		
    		System.out.println("size of compressed index with gamma encoding: " + indexbytes.length);
    		System.out.println("size of long string for Block 8 version: " + stringbytes.length);
    		System.out.println("number of inverted lists: " + myDict.Size());
    		
    		WriteFile(indexpath, indexbytes);
    		WriteFile(stringpath, stringbytes);
    		
        }
        catch(IOException e)
        {
         	e.printStackTrace();
        }
	}
	
	public static Dictionary ReadCompressedIndex_Block8(String path, List<Document> docList, byte[] longstr)
	{
		byte[] data = ReadBytes(path);
		
		List<Integer> intlist = GetGammaDecode_Strong(data);
		
		//System.out.println(PrintBytesToBinary(data, true).toString());
		//System.out.println(" ");
		//System.out.println(Arrays.toString(intlist.toArray()));
		
		Dictionary myDict = GetDictionaryFromIntList_GammaPointer(intlist, longstr);
		
		return myDict;
	}
	
	public static Dictionary ReadCompressedIndex_Block8_Old2(String path, List<Document> docList, byte[] longstr)
	{
		Dictionary myDict = new Dictionary();
		byte[] data = ReadBytes(path);
		
	    byte[] intbytes = new byte[4];
	    byte[] ptrbytes = new byte[3];
	    
	    boolean istermptr = true;
	    
	    int num = 0;
	    
	    Term term = new Term();
	    
	    int linecount = 0;
	    
	    StringBuilder tempgammacodes = new StringBuilder();
	    for(int i = 0; i < data.length; i++)
	    {
	    	if (istermptr)
	    	{
	    		if (linecount % 8 == 0)
	    		{
		    		ptrbytes = new byte[3];
		    		ptrbytes[0] = data[i++];
		    		ptrbytes[1] = data[i++];
		    		ptrbytes[2] = data[i];
		    		
		    		int pointer = Byte3ToInt(ptrbytes);
		    		
		    		term.str = GetTermFromLongString(longstr, pointer, 0);
	    		}
	    		else
	    		{
		    		int pointer = Byte3ToInt(ptrbytes);
		    		
		    		term.str = GetTermFromLongString(longstr, pointer, linecount % 8);
	    			i--; // reread intbytes
	    		}
	    		istermptr = false;
	    	}
	    	else
	    	{
	    		if ((int)data[i] == 0 && i < data.length - 1 && (int)data[i + 1] == 0)
	    		{	    			
	    			List<Integer> linesinfo = GetGammaDecode(tempgammacodes);
	    			if (linesinfo.size() == 0)
	    			{
	    				int k = 0;  //cannot work, when there are trailing 8-zero byte, the line break will be confused.
	    			}
	    			term.dfreq = linesinfo.get(0);
	    			for(int j = 1; j < linesinfo.size(); j++)
	    			{
	    				if (j % 4 == 1)
	    				{
	    					term.postings.add(num);
	    				}
	    				else if (j % 4 == 2)
	    				{
	    					term.tfreqs.add(num);
	    				}
	    			}
	    			
    				myDict.Add(term);
    				term = new Term();
    				tempgammacodes.setLength(0);
    				
    				istermptr = true;
    				linecount++;
	    			i++;
	    		}
	    		else
	    		{
	    			tempgammacodes.append(GetBinaryString(data[i]));
	    		}
	    	}
	    }
		
		return myDict;
	}
	
	// block size = 8, for every 8 pointers, write only the first
	public static void WriteCompressedIndex_Block8_Dict(String indexpath, String stringpath, Dictionary myDict, List<Document> docList)
	{
        ByteArrayOutputStream indexbytestream = new ByteArrayOutputStream();
        ByteArrayOutputStream stringbytestream = new ByteArrayOutputStream();
        try 
        {
        	int blocksize = 8;
            List<Integer> postings = new ArrayList<Integer>();
            List<Integer> tfreqs = new ArrayList<Integer>();
            Document currentDoc;
            Term currentTerm;
            
            int termpointer = 0;  // offset in the byte array
            
    		for(int i= 0; i < myDict.Size(); i++)
    		{
    			currentTerm = myDict.GetTerm(i);
    			postings = currentTerm.postings;
    			tfreqs = currentTerm.tfreqs;

    			if (i % 8 == 0)  //a blocked compression with k=8
    			{
    				indexbytestream.write(IntToBytes3(termpointer));  // write a term pointer as a 3-byte integer
    			}
    			indexbytestream.write(IntToBytes4(currentTerm.dfreq)); //write df
    			
    			int lastdocid = 0;
    			for(int j = 0; j < postings.size(); j++)
    			{
    				indexbytestream.write(IntToBytes4(postings.get(j) - lastdocid)); // write a docid
    				indexbytestream.write(IntToBytes4(tfreqs.get(j)));  // write tfreq of the current docid
    				currentDoc = docList.get(postings.get(j) - 1);
    				indexbytestream.write(IntToBytes4(currentDoc.max_tf)); // write max_ft of the current docid
    				indexbytestream.write(IntToBytes4(currentDoc.doclen)); // write doclen of the current docid
    				
    				lastdocid = postings.get(j);
    			}
    			// It is better not to write mixed data types, because it will be difficult to find a line break when reading the file.
    			// The int value for a '\n' when reading file is 10, which is easily confused with an existing document id 10 or any higher byte out of every 4 bytes of an integer equaling 10.
    			// output.writeChar('\n');  
    			// output.write(0);  // since there should be no zeros in the index, 0 can be used as a line break
    			indexbytestream.write(IntToBytes4(-1));
    			
    			stringbytestream.write(currentTerm.str.length());  // write a one-byte integer as the length of term string
    			
    			for(int j = 0; j < currentTerm.str.length(); j++)
    			{
    				stringbytestream.write((byte)currentTerm.str.charAt(j));
    			}
    			//output2.writeChars(currentTerm.str);
    			
    			termpointer += 1;
    			termpointer += currentTerm.str.length();
    		}
    		
    		byte[] indexbytes = indexbytestream.toByteArray();
    		byte[] stringbytes = stringbytestream.toByteArray();
    		indexbytestream.close();
    		stringbytestream.close();
    		
    		WriteFile(indexpath, indexbytes);
    		WriteFile(stringpath, stringbytes);
    		
        }
        catch(IOException e)
        {
         	e.printStackTrace();
        }
	}
	
	public static Dictionary ReadCompressedIndex_Block8_Dict(String path, List<Document> docList, byte[] longstr)
	{
		Dictionary myDict = new Dictionary();
		byte[] data = ReadBytes(path);
	    byte[] intbytes = new byte[4];
	    byte[] ptrbytes = new byte[3];
	    
	    boolean istermptr = true;
	    
	    int num = 0;
	    
	    int module = 0;
	    
	    Term term = new Term();
	    
	    int linecount = 0;
	    
	    //PrintBytes(data);
	    
	    for(int i = 0; i < data.length; i++)
	    {
	    	if (istermptr)
	    	{
	    		if (linecount % 8 == 0)
	    		{
		    		ptrbytes = new byte[3];
		    		ptrbytes[0] = data[i++];
		    		ptrbytes[1] = data[i++];
		    		ptrbytes[2] = data[i];
		    		
		    		int pointer = Byte3ToInt(ptrbytes);
		    		
		    		term.str = GetTermFromLongString(longstr, pointer, 0);

	    		}
	    		else
	    		{
		    		int pointer = Byte3ToInt(ptrbytes);
		    		
		    		term.str = GetTermFromLongString(longstr, pointer, linecount % 8);
	    			i--; // reread intbytes
	    		}
	    		
	    		module = 0;
	    		istermptr = false;
	    	}
	    	else
	    	{
	    		intbytes = new byte[4];
	    		intbytes[0] = data[i++];
	    		intbytes[1] = data[i++];
	    		intbytes[2] = data[i++];
	    		intbytes[3] = data[i];
	    		
	    		num = Byte4ToInt(intbytes);
	    		if (num == -1)
	    		{
    				myDict.Add(term);
    				term = new Term();
    				
    				istermptr = true;
    				module = 0;
    				linecount++;
    				
	    		}
	    		else
	    		{
	    			if (module == 0)
	    			{
	    				term.dfreq = num;
	    			}
	    			else if (module == 1)
	    			{
	    				term.postings.add(num);
	    			}
	    			else if (module == 2)
	    			{
	    				term.tfreqs.add(num);
	    			}
	    			
	    			module +=1;
	    			
	    			if (module > 4)
	    			{
	    				module = module - 4;
	    			}
	    		}
	    	}
	    }
		
		
		return myDict;
	}
	
	// block size = 8, for every 8 pointers, write only the first
	public static void WriteCompressedIndex_Block8_Old(String indexpath, String stringpath, Dictionary myDict, List<Document> docList)
	{
        // BufferedWriter output = null;  BufferedWriter is used to write strings or characters
		DataOutputStream output = null;
		FileOutputStream filestream = null;
		DataOutputStream output2 = null;
		FileOutputStream filestream2 = null;
        try 
        {
        	File file = new File(indexpath);
        	if (!file.exists())
        	{
        		file.getParentFile().mkdirs();
        		file.createNewFile();
        	}
        	
        	File file2 = new File(stringpath);
        	if (!file2.exists())
        	{
        		file2.getParentFile().mkdirs();
        		file2.createNewFile();
        	}
        	
        	filestream = new FileOutputStream(indexpath, false);
        	filestream.write(("").getBytes());  //clear existing content
        	
        	filestream2 = new FileOutputStream(stringpath, false);
        	filestream2.write(("").getBytes());  //clear existing content
        	
        	output = new DataOutputStream(filestream);
        	
        	output2 = new DataOutputStream(filestream2);
            
        	int blocksize = 8;
            List<Integer> postings = new ArrayList<Integer>();
            List<Integer> tfreqs = new ArrayList<Integer>();
            Document currentDoc;
            Term currentTerm;
            
            int termpointer = 0;  // offset in the byte array
            
    		for(int i= 0; i < myDict.Size(); i++)
    		{
    			currentTerm = myDict.GetTerm(i);
    			postings = currentTerm.postings;
    			tfreqs = currentTerm.tfreqs;

    			if (i % 8 == 0)  //a blocked compression with k=8
    			{
    			    output.write(IntToBytes3(termpointer));  // write a term pointer as a 3-byte integer
    			}
    			output.writeInt(currentTerm.dfreq); //write df
    			
    			int lastdocid = 0;
    			for(int j = 0; j < postings.size(); j++)
    			{
    				output.writeInt(postings.get(j) - lastdocid); // write a docid
    				output.writeInt(tfreqs.get(j));  // write tfreq of the current docid
    				currentDoc = docList.get(postings.get(j) - 1);
    				output.writeInt(currentDoc.max_tf); // write max_ft of the current docid
    				output.writeInt(currentDoc.doclen); // write doclen of the current docid
    				
    				lastdocid = postings.get(j);
    			}
    			// It is better not to write mixed data types, because it will be difficult to find a line break when reading the file.
    			// The int value for a '\n' when reading file is 10, which is easily confused with an existing document id 10 or any higher byte out of every 4 bytes of an integer equaling 10.
    			// output.writeChar('\n');  
    			// output.write(0);  // since there should be no zeros in the index, 0 can be used as a line break
    			output.writeInt(-1);
    			
    			
    			output2.write(currentTerm.str.length());  // write a one-byte integer as the length of term string
    			output2.writeChars(currentTerm.str);
    			
    			termpointer += 1;
    			termpointer += currentTerm.str.length() * 2;
    		}
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            if (output != null) 
            {
                try {
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            if (output2 != null) 
            {
                try {
					output2.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            if (filestream != null)
            {
            	try {
					filestream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            if (filestream2 != null)
            {
            	try {
					filestream2.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
	}
	
	public static Dictionary ReadCompressedIndexOld(String path, List<Document> docList, byte[] longstr)
	{
		File file = new File(path);
		DataInputStream reader = null;
		FileInputStream fileinput = null;
		String filename = file.getName();
		
		Dictionary myDict = new Dictionary();
		
		try
		{
			fileinput = new FileInputStream(file);
		    reader = new DataInputStream(fileinput);
		    
		    int module = 0;
		    int bytemodule = 0;
		    
		    int buffer = 1024;
		    
		    byte[] temp = new byte[buffer];
		    byte[] intbytes = new byte[4];
		    byte[] ptrbytes = new byte[3];
		    
		    //int length = reader.available();
		    
		    int count = 0;
		    
		    int num = 0;
		    Term term = new Term();
		    //term.dfreq =  reader.readInt();	
		    boolean istermptr = true;
		    boolean isdf = false;
		    
		    int zerocount = 0;
		    
		    int readbytes = reader.read(temp);
		    while(readbytes > 0)
		    {
		    	while(count < readbytes)
		    	{
		    		if (istermptr)
		    		{
		    			ptrbytes = new byte[3];
		    			ptrbytes[0] = temp[count++];
		    			ptrbytes[1] = temp[count++];
		    			ptrbytes[2] = temp[count++];
		    			
		    			term.str = GetTermFromLongString(longstr, Byte3ToInt(ptrbytes), 0);
		    			istermptr = false;
		    			isdf = true;
		    			bytemodule = 0;
		    		}
		    		else
		    		{
		    			intbytes[bytemodule] = temp[count];
		    			
		    			if (bytemodule == 3)
		    			{
		    				num = Byte4ToInt(intbytes);
		    				
			    			if (num == -1)
			    			{
			    				myDict.Add(term);
			    				term = new Term();
			    				
			    				
			    				istermptr = true;
			    				module = 0;
			    			}
			    			else
			    			{
					    		if (isdf)
					    		{
					    			term.dfreq = num;
					    			isdf = false;
					    			module = 1;
					    		}
					    		else
					    		{
					    			if (module == 1)
					    			{
					    				term.postings.add(num);
					    			}
					    			else if (module == 2)
					    			{
					    				term.tfreqs.add(num);
					    			}
					    			
					    			module +=1;
					    			
					    			if (module > 4)
					    			{
					    				module = module - 4;
					    			}
					    		}
			    			}


				    		intbytes = new byte[4];
				    		bytemodule -= 4;
		    			}
		    			
		    			bytemodule++;
		    			count++;
		    			
		    		}
		    		

		    		

	    			
	    			//intlist.add(num);
		    	}
		    	
		    	temp = new byte[buffer];
		    	readbytes = reader.read(temp);
		    	count = 0;
		    	
		    	PrintBytes(temp);
		    }
		}
		catch (Exception e)
		{
			System.out.print("\n compressed index read of size " + myDict.Size() + "\n");
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
		
		return myDict;
	}

	//For each line of the file, the following information is stored sequentially:
	//Term string, df, (docid, tf, max_tf, doclen)
	//There is an integer -1 residing between the term string and df, also an integer -1 as line break.
	//All information excluding the term strings is stored as integers, while term strings are stored as two-byte sequence.
	//For convenience of Index Reading, a two-byte sequence of term string with odd length is padded with two zero-byte in the head end.
	//The purpose is to make possible reading the index file into an Integer list before further interpreting it into Dictionary.
	public static void WriteIndex(String path, Dictionary myDict, List<Document> docList)
	{
		ByteArrayOutputStream bytestream;
        try 
        {
        	bytestream = new ByteArrayOutputStream();

            List<Integer> postings = new ArrayList<Integer>();
            List<Integer> tfreqs = new ArrayList<Integer>();
            Document currentDoc;
            Term currentTerm;
    		for(int i= 0; i < myDict.Size(); i++)
    		{
    			currentTerm = myDict.GetTerm(i);
    			postings = currentTerm.postings;
    			tfreqs = currentTerm.tfreqs;
    			
    			int module = 4 - currentTerm.str.length() % 4;
    			module = module % 4;

    			for(int j = 0; j < module; j++)
    			{
    				bytestream.write(0);// supplement zero-value bytes at the front to make the number of str bytes divisible by 4
    			}

    			bytestream.write(currentTerm.str.getBytes());
    			//output.writeChars(currentTerm.str);
    			bytestream.write(IntToBytes4(-1));
    			bytestream.write(IntToBytes4(currentTerm.dfreq)); //write df
    			
    			for(int j = 0; j < postings.size(); j++)
    			{
    				bytestream.write(IntToBytes4(postings.get(j))); // write a docid
    				bytestream.write(IntToBytes4(tfreqs.get(j)));  // write tfreq of the current docid
    				currentDoc = docList.get(postings.get(j) - 1);
    				bytestream.write(IntToBytes4(currentDoc.max_tf)); // write max_ft of the current docid
    				bytestream.write(IntToBytes4(currentDoc.doclen)); // write doclen of the current docid
    			}
    			// It is better not to write mixed data types, because it will be difficult to find a line break when reading the file.
    			// The int value for a '\n' when reading file is 10, which is easily confused with an existing document id 10 or any higher byte out of every 4 bytes of an integer equaling 10.
    			// output.writeChar('\n');  
    			// output.write(0);  // since there should be no zeros in the index, 0 can be used as a line break
    			bytestream.write(IntToBytes4(-1));
    		}
    		
    		
    		byte[] indexbytes = bytestream.toByteArray();
    		bytestream.close();
    		
    		System.out.println("size of index: " + indexbytes.length);
    		System.out.println("Number of inverted lists: " + myDict.Size());
    		
    		WriteFile(path, indexbytes);
    		
            }
            catch(IOException e)
            {
            	e.printStackTrace();
            }
	}
	
	public static Dictionary ReadIndex(String path, List<Document> docInfoList)
	{
		List<Integer> intlist = ReadIntegers(path);
		
		//System.out.println(Arrays.asList(intlist).toString());
		
		Dictionary myDict = GetDictionaryFromIntList(intlist);
		return myDict;
	}
	
	public static void WriteIndexOld(String path, Dictionary myDict, List<Document> docList)
	{
        // BufferedWriter output = null;  BufferedWriter is used to write strings or characters
		DataOutputStream output = null;
		FileOutputStream filestream = null;
        try 
        {
        	File file = new File(path);
        	if (!file.exists())
        	{
        		file.getParentFile().mkdirs();
        		file.createNewFile();
        	}
        	//filestream = new FileOutputStream(path);
        	filestream = new FileOutputStream(path, false);
        	filestream.write(("").getBytes());  //clear existing content
        	
        	output = new DataOutputStream(filestream);
            
            //output = new BufferedWriter(new FileWriter(file));  
            
            List<Integer> postings = new ArrayList<Integer>();
            List<Integer> tfreqs = new ArrayList<Integer>();
            Document currentDoc;
            Term currentTerm;
    		for(int i= 0; i < myDict.Size(); i++)
    		{
    			currentTerm = myDict.GetTerm(i);
    			postings = currentTerm.postings;
    			tfreqs = currentTerm.tfreqs;
    			if (currentTerm.str.length() % 2 == 1)
    			{
    				output.writeChar(0);  // supplement 2 zero-value bytes at the front to make the number of str bytes divisible by 4
    			}
    			output.writeChars(currentTerm.str);
    			output.writeInt(-1);
    			output.writeInt(currentTerm.dfreq); //write df
    			
    			for(int j = 0; j < postings.size(); j++)
    			{
    				output.writeInt(postings.get(j)); // write a docid
    				output.writeInt(tfreqs.get(j));  // write tfreq of the current docid
    				currentDoc = docList.get(postings.get(j) - 1);
    				output.writeInt(currentDoc.max_tf); // write max_ft of the current docid
    				output.writeInt(currentDoc.doclen); // write doclen of the current docid
    			}
    			// It is better not to write mixed data types, because it will be difficult to find a line break when reading the file.
    			// The int value for a '\n' when reading file is 10, which is easily confused with an existing document id 10 or any higher byte out of every 4 bytes of an integer equaling 10.
    			// output.writeChar('\n');  
    			// output.write(0);  // since there should be no zeros in the index, 0 can be used as a line break
    			output.writeInt(-1);
    		}
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            if (output != null) 
            {
                try {
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            if (filestream != null)
            {
            	try {
					filestream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
	}
	
	public static byte[] ReadBytes(String path)
	{
		File file = new File(path);
		DataInputStream reader = null;
		FileInputStream fileinput = null;
		String filename = file.getName();
		
		byte[] data = null;
		int i = 0;
		
		try
		{
			fileinput = new FileInputStream(file);
		    reader = new DataInputStream(fileinput);
		    
		    int buffer = 1024;
		    
		    byte[] temp = new byte[buffer];
		    data = new byte[reader.available()];
		    
		    int count = 0;
		    
		    int readbytes = reader.read(temp);
		    while(readbytes > 0)
		    {
		    	while(count < readbytes)
		    	{
		    		data[i++] = temp[count++];
		    	}
		    	
		    	temp = new byte[buffer];
		    	readbytes = reader.read(temp);
		    	count = 0;
		    }
		}
		catch (Exception e)
		{
			System.out.print("\n bytes read of size " + i + "\n");
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
		
		return data;
	}
	
	// block read should be much faster than byte read or int read
	public static List<Integer> ReadIntegers(String path)
	{
		File file = new File(path);
		DataInputStream reader = null;
		FileInputStream fileinput = null;
		String filename = file.getName();
		
		List<Integer> intlist = new ArrayList<Integer>();
		
		try
		{
			fileinput = new FileInputStream(file);
		    reader = new DataInputStream(fileinput);
		    
		    int module = 0;
		    
		    int buffer = 1024;
		    
		    byte[] temp = new byte[buffer];
		    byte[] intbytes = new byte[4];
		    
		    
		    //int length = reader.available();
		    
		    int count = 0;
		    int integerid = 0;
		    
		    int num = 0;
		    Term term = new Term();
		    //term.dfreq =  reader.readInt();	
		    
		    int readbytes = reader.read(temp);
		    while(readbytes > 0)
		    {
		    	while(count < readbytes)
		    	{
	    			intbytes = new byte[4];
	    			intbytes[0] = temp[count++];
	    			intbytes[1] = temp[count++];
	    			intbytes[2] = temp[count++];
	    			intbytes[3] = temp[count++];
	    			
	    			num = Byte4ToInt(intbytes);
	    			
	    			intlist.add(num);
		    	}
		    	
		    	temp = new byte[buffer];
		    	readbytes = reader.read(temp);
		    	count = 0;
		    }
		}
		catch (Exception e)
		{
			System.out.print("\n intlist read of size " + intlist.size() + "\n");
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
		
		return intlist;
	}
	
	public static Dictionary GetDictionaryFromIntList_GammaPointer(List<Integer> intlist, byte[] longstr)
	{
		Dictionary myDict = new Dictionary();
		
		int module = 0;
		int tempint = 0;
		boolean isterm = true;
		Term term = new Term();
		int pointer = 0;
		int linecount = 0;
		for(int i = 0; i < intlist.size(); i++)
		{
			tempint = intlist.get(i);
			if (tempint == -1)
			{
	    		if (linecount % 8 == 0)
	    		{
		    		term.str = GetTermFromLongString(longstr, pointer, 0);

	    		}
	    		else
	    		{
		    		term.str = GetTermFromLongString(longstr, pointer, linecount % 8);
	    		}
				
	    		term.tfreq = Sum(term.tfreqs);
				myDict.Add(term);
				term = new Term();
				
				module = 0;
				
				linecount++;
				
				if (linecount % 8 == 0)
				{
				    isterm = true;
				}
				else
				{
					isterm = false;
				}
			}
			else
			{
				if (isterm)
				{
					pointer = tempint;
					isterm = false;
				}
				else
				{
					if (module == 0) // i == 0
					{
						term.dfreq = tempint;
					}
					else
					{
						if (module > 4)
						{
							module = module - 4;
						}
					}
					
					if (module == 1)
					{
						term.postings.add(tempint);
					}
					else if (module == 2)
					{
						term.tfreqs.add(tempint);
					}
					
					module += 1;
				}
			}
			
			
		}
		
		return myDict;
	}
	
	public static Dictionary GetDictionaryFromIntList_DeltaPointer(List<Integer> intlist, byte[] longstr)
	{
		Dictionary myDict = new Dictionary();
		
		int module = 0;
		int tempint = 0;
		boolean isterm = true;
		Term term = new Term();
		int pointer = 0;
		int linecount = 0;
		for(int i = 0; i < intlist.size(); i++)
		{
			tempint = intlist.get(i);
			if (tempint == -1)
			{
	    		if (linecount % 4 == 0)
	    		{
		    		term.str = GetTermFromLongString_FrontCoding(longstr, pointer, 0);

	    		}
	    		else
	    		{
		    		term.str = GetTermFromLongString_FrontCoding(longstr, pointer, linecount % 4);
	    		}
				
	    		term.tfreq = Sum(term.tfreqs);
				myDict.Add(term);
				term = new Term();
				
				module = 0;
				
				linecount++;
				
				if (linecount % 4 == 0)
				{
				    isterm = true;
				}
				else
				{
					isterm = false;
				}
			}
			else
			{
				if (isterm)
				{
					pointer = tempint;
					isterm = false;
				}
				else
				{
					if (module == 0) // i == 0
					{
						term.dfreq = tempint;
					}
					else
					{
						if (module > 4)
						{
							module = module - 4;
						}
					}
					
					if (module == 1)
					{
						term.postings.add(tempint);
					}
					else if (module == 2)
					{
						term.tfreqs.add(tempint);
					}
					
					module += 1;
				}
			}
			
			
		}
		
		return myDict;
	}
	
	public static Dictionary GetDictionaryFromIntList(List<Integer> intlist)
	{
		Dictionary myDict = new Dictionary();
		
		List<Integer> tempintlist = new ArrayList<Integer>();
		
		int module = 0;
		int tempint = 0;
		boolean isterm = true;
		Term term = new Term();
		for(int i = 0; i < intlist.size(); i++)
		{
			tempint = intlist.get(i);
			if (tempint == -1)
			{
				if (isterm)
				{
					term.str = GetStrFromIntegerList(tempintlist);
					tempintlist = new ArrayList<Integer>();
					isterm = false;
					module = 0;
				}
				else
				{
					term.tfreq = Sum(term.tfreqs);
					
					myDict.Add(term);
					term = new Term();
					
					/*// This segment leads to bug: the first two chars of term are discarded.
					if (i < intlist.size() - 1)
					{
					    term.dfreq = intlist.get(++i);
					}
					*/
					
					module = 0;
					isterm = true;
				}
			}
			else
			{
				if (isterm)
				{
				    tempintlist.add(tempint);
				}
				else
				{
					if (module == 0) // i == 0
					{
						term.dfreq = tempint;
					}
					else
					{
						if (module > 4)
						{
							module = module - 4;
						}
					}
					
					if (module == 1)
					{
						term.postings.add(tempint);
					}
					else if (module == 2)
					{
						term.tfreqs.add(tempint);
					}
					
					module += 1;
				}
			}
			
			
		}
		
		return myDict;
	}
	
	public static Dictionary GetDictionaryFromIntList_Old(List<Integer> intlist)
	{
		Dictionary myDict = new Dictionary();
		
		int module = 0;
		
		Term term = new Term();
		for(int i = 0; i < intlist.size(); i++)
		{
			if (intlist.get(i) == -1)
			{
				myDict.Add(term);
				term = new Term();
				if (i < intlist.size() - 1)
				{
				    term.dfreq = intlist.get(++i);
				    module = 0;
				}
			}
			else
			{
				if (module == 0) // i == 0
				{
					term.dfreq = intlist.get(i);
				}
				else
				{
					if (module > 4)
					{
						module = module - 4;
					}
				}
				
				if (module == 1)
				{
					term.postings.add(intlist.get(i));
				}
				else if (module == 2)
				{
					term.tfreqs.add(intlist.get(i));
				}
			}
			
			module += 1;
		}
		
		return myDict;
	}
	
	public static Dictionary ReadIndexOld2(String path, List<Document> docList)
	{
		Dictionary myDict = new Dictionary();
		
		File file = new File(path);
		DataInputStream reader = null;
		FileInputStream fileinput = null;
		String filename = file.getName();
		try
		{
			fileinput = new FileInputStream(file);
		    reader = new DataInputStream(fileinput);
		    
		    int module = 0;
		    
		    int buffer = 1024;
		    
		    byte[] temp = new byte[buffer];
		    byte[] intbytes = new byte[4];
		    
		    
		    //int length = reader.available();
		    
		    int count = 0;
		    int integerid = 0;
		    
		    int num = 0;
		    Term term = new Term();
		    //term.dfreq =  reader.readInt();	
		    
		    int readbytes = reader.read(temp);
		    while(readbytes > 0)
		    {
		    	while(count < readbytes)
		    	{
	    			intbytes = new byte[4];
	    			intbytes[0] = temp[count++];
	    			intbytes[1] = temp[count++];
	    			intbytes[2] = temp[count++];
	    			intbytes[3] = temp[count++];
	    			
	    			num = Byte4ToInt(intbytes);
	    			if (num == -1)
	    			{
	    				integerid = 0;
	    				module = 0;
	    			}
		    		
		    		module = count % 4;
		    		integerid += 1;
		    	}
		    	for(int j = 0; j < temp.length; j++)
		    	{
		    		
		    	}
		    	num = reader.readInt();

		    	if (num == -1)  // line break 0 set when writing a file
		    	{
		    		myDict.Add(term);
		    		term = new Term();
		    		term.dfreq = reader.readInt();		    		
		    	}
		    	else
		    	{
		    		term.postings.add(num);
		    		term.tfreqs.add(reader.readInt());
		    		reader.readInt();
		    		reader.readInt();
		    	}
		    	
		    	temp = new byte[buffer];
		    	readbytes = reader.read(temp);
		    }
		}
		catch (Exception e)
		{
			System.out.print("\n dictionary read of size " + myDict.Size() + "\n");
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
		
		return myDict;
	}
	
	public static Dictionary ReadIndexOld(String path, List<Document> docList)
	{
		Dictionary myDict = new Dictionary();
		
		File file = new File(path);
		DataInputStream reader = null;
		FileInputStream fileinput = null;
		String filename = file.getName();
		try
		{
			fileinput = new FileInputStream(file);
		    reader = new DataInputStream(fileinput);
		    
		    int num = 0;
		    
		    Term term = new Term();
		    term.dfreq =  reader.readInt();	
		    while(true)
		    {
		    	num = reader.readInt();

		    	if (num == -1)  // line break 0 set when writing a file
		    	{
		    		myDict.Add(term);
		    		term = new Term();
		    		term.dfreq = reader.readInt();		    		
		    	}
		    	else
		    	{
		    		term.postings.add(num);
		    		term.tfreqs.add(reader.readInt());
		    		reader.readInt();
		    		reader.readInt();
		    	}
		    }
		}
		catch (Exception e)
		{
			System.out.print("\n dictionary read of size " + myDict.Size() + "\n");
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
		
		return myDict;
	}
	
	// Document information is stored as a separate file.
	// It can be either indexed for each query for document information or loaded into the Dictionary.
	public static void WriteDocInfo(String path, List<Document> docList)
	{
		ByteArrayOutputStream bytestream;
        try 
        {
        	bytestream = new ByteArrayOutputStream();

            Document currentDoc;
    		for(int i= 0; i < docList.size(); i++)
    		{
    			currentDoc = docList.get(i);
    			
    			bytestream.write(IntToBytes4(currentDoc.ID)); //write document ID
    			bytestream.write(IntToBytes4(currentDoc.max_tf)); //write max_ft of the current docid
    			bytestream.write(IntToBytes4(currentDoc.doclen)); //write doclen of the current docid
    			
    			// It is better not to write mixed data types, because it will be difficult to find a line break when reading the file.
    			// The int value for a '\n' when reading file is 10, which is easily confused with an existing document id 10 or any higher byte out of every 4 bytes of an integer equaling 10.
    			// output.writeChar('\n');  
    			// output.write(0);  // since there should be no zeros in the index, 0 can be used as a line break
    			bytestream.write(IntToBytes4(-1));
    		}
    		
    		byte[] indexbytes = bytestream.toByteArray();
    		bytestream.close();
    		
    		System.out.println("size of document information index: " + indexbytes.length);
    		
    		WriteFile(path, indexbytes);
    		
    		bytestream.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
	}
	
	public static StringBuilder ReadDocInfoBinary(String path)
	{
		File file = new File(path);
		DataInputStream reader = null;
		FileInputStream fileinput = null;
		String filename = file.getName();
		
		StringBuilder temp = new StringBuilder();
		try
		{
			fileinput = new FileInputStream(file);
		    reader = new DataInputStream(fileinput);
		    
		    int num = 0;
		    
		    int length = reader.available();
		    Document doc = new Document();
		    int readcount = 0;
		    int count = 0;
		    while(true)
		    {
		    	count += 1;
		    	temp.append(Byte.toUnsignedInt(reader.readByte()));
		    	
		    	if (count % 4 == 0)
		    	{
		    		temp.append("\r\n");
		    	}
		    }
		}
		catch (Exception e)
		{
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
		
		return temp;
	}
	
	public static List<Document> ReadDocInfo(String path)
	{
		List<Document> docInfoList = new ArrayList<Document>();
		File file = new File(path);
		DataInputStream reader = null;
		FileInputStream fileinput = null;
		String filename = file.getName();
		
		try
		{
			fileinput = new FileInputStream(file);
		    reader = new DataInputStream(fileinput);
		    
		    int num = 0;
		    
		    Document doc = new Document();
		    int readcount = 0;
		    while(true)
		    {
		    
		    	num = reader.readInt();
		    	readcount += 1;
		    	if (num == -1)  // line break -1 set when writing a file
		    	{
		    		
		    		docInfoList.add(doc);
		    		doc = new Document();
		    		readcount = 0;
		    		//doc.ID = reader.readInt();
		    		//doc.max_tf = reader.readInt();
		    		//doc.doclen = reader.readInt();    		
		    	}
		    	else
		    	{
		    		if (readcount == 1)
		    		{
		    			doc.ID = num;
		    		}
		    		else if (readcount == 2)
		    		{
		    			doc.max_tf = num;
		    		}
		    		else if (readcount == 3)
		    		{
		    			doc.doclen = num;
		    		}
		    	}
		    }
		}
		catch (Exception e)
		{
			System.out.print("\n docInfoList read of size " + docInfoList.size() + "\n");
			// System.out.print("\nerror occur when reading file " + filename + "\n" + e.getMessage());
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
		
		return docInfoList;
	}
	
	public static List<Integer> GetMaxTFInDoc(List<Integer> postings, List<Document> docInfoList)
	{
		List<Integer> max_tfs = new ArrayList<Integer>();
		for(int docid: postings)
		{
			max_tfs.add(docInfoList.get(docid - 1).max_tf);
		}
		
		return max_tfs;
	}
	
	public static List<Integer> GetDocLenInDoc(List<Integer> postings, List<Document> docInfoList)
	{
		List<Integer> doclens = new ArrayList<Integer>();
		for(int docid: postings)
		{
			doclens.add(docInfoList.get(docid - 1).doclen);
		}
		
		return doclens;
	}

    public static void BuildIndexUnix(String folderpath, String stopwordspath, String corpuspath, boolean iscompress, boolean islemmatize)
    {
    	Instant dtStart = Instant.now();
		//String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		
		//List<String> stopwords = Tokenizer.GetStopWords("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//resourcesIR//stopwords");
		//File[] filelist = Tokenizer.GetAllFiles("Z://文稿/留学/UTDallas/textbooks/6322_spring2017_Information Retrieval/Cranfield");
		
		List<String> stopwords = Tokenizer.GetStopWords(stopwordspath);
		File[] filelist = Tokenizer.GetAllFiles(corpuspath);

		Dictionary myDict1 = new Dictionary();
		Dictionary myDict2 = new Dictionary();
		List<Document> docInfoList1 = new ArrayList<Document>();
		List<Document> docInfoList2 = new ArrayList<Document>();
		int max_tf1 = 0;
		int max_tf2 = 0;
		int doclen1 = 0;
		int doclen2 = 0;
		
		if (!islemmatize)  // version 2
		{
			for(int i = 0; i < filelist.length; i++)
			{
				StringBuilder originaltext = Tokenizer.ReadFileText(filelist[i]);
				StringBuilder filteredtext = Tokenizer.FilterHTML(originaltext);
				List<String> rawtokens = Tokenizer.Tokenize(filteredtext, false);  // without stemmatization
				
				// There seems to be potential problems for stemmatization before removing stop words, since words other than stop words might be stemmed into stop words.
				// But if stop words are removed before stemmatization, doclen2 cannot be calculated correctly.
				List<String> tokens2 = Tokenizer.StemTokens(rawtokens);  // with stemmatization
				doclen2 = tokens2.size();  // doclen includes occurrences of stop words
				tokens2 = Tokenizer.FilterStopWords(tokens2, stopwords);  // filter stop words after stemmatization
				max_tf2 = Tokenizer.CountMaxTermFreq(tokens2);
				Dictionary.AddDictTerms(myDict2, tokens2, (i+1));
				
				Document newdoc2 = new Document();
				newdoc2.ID = i+1;
				newdoc2.max_tf = max_tf2;
				newdoc2.doclen = doclen2;
				docInfoList2.add(newdoc2);
				
				if (i%200 == 0)
				{
					System.out.print("\n" + i);
				}
				System.out.print(".");

			}
			
			System.out.println(" ");
			System.out.println("Start to write index...");
			
			WriteDocInfo(folderpath + "docInfo2", docInfoList2);
			
			if (!iscompress)
			{
				WriteIndex(folderpath + "Index_Version2.uncompressed", myDict2, docInfoList2);
			}
			else
			{
				WriteCompressedIndex_FrontCoding(folderpath + "Index_Version2.compressed",  folderpath + "TermString_Version2", myDict2, docInfoList2);
			}
		}
		else
		{
			Lemmatizer lemmatizer = new Lemmatizer();  //version 1
			for(int i = 0; i < filelist.length; i++)
			{
				StringBuilder originaltext = Tokenizer.ReadFileText(filelist[i]);
				StringBuilder filteredtext = Tokenizer.FilterHTML(originaltext);
				List<String> rawtokens = Tokenizer.Tokenize(filteredtext, false);  // without stemmatization

	            List<String> tokens1 = lemmatizer.Lemmatize(rawtokens, false);
	            doclen1 = tokens1.size();  // doclen includes occurrences of stop words
				tokens1 = Tokenizer.FilterStopWords(tokens1, stopwords);  // lemmatize raw tokens before filtering stop words
				max_tf1 = Tokenizer.CountMaxTermFreq(tokens1);  // count maximum term frequency in a document after removing stop words
				Dictionary.AddDictTerms(myDict1, tokens1, (i+1));
				
				//PrintDict(myDict1);
				
				Document newdoc1 = new Document();
				newdoc1.ID = i+1;
				newdoc1.max_tf = max_tf1;
				newdoc1.doclen = doclen1;
				docInfoList1.add(newdoc1);
				
				if (i%200 == 0)
				{
					System.out.print("\n" + i);
				}
				System.out.print(".");

			}
			
			System.out.println(" ");
			System.out.println("Start to write index...");
			
			WriteDocInfo(folderpath + "docInfo1", docInfoList1);
			
			if(!iscompress)
			{
				WriteIndex(folderpath + "Index_Version1.uncompressed", myDict1, docInfoList1);
			}
			else
			{
				WriteCompressedIndex_Block8(folderpath + "Index_Version1.compressed",  folderpath + "TermString_Version1", myDict1, docInfoList1);
			}
		}
		
		//PrintDict(myDict1);
		//PrintDict(myDict2);
		//PrintDocInfo(docInfoList1);
		//PrintDocInfo(docInfoList2);

		System.out.println(" ");
		System.out.println("Write complete!");
		Instant dtEnd = Instant.now();
		System.out.println("Time elapsed: " + Duration.between(dtStart, dtEnd).toMillis() + " milliseconds");
    }
    
    public static void BuildIndex(boolean iscompress, boolean islemmatize)
    {
		String folderpath = "Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//homework//homework2//";
		
		List<String> stopwords = Tokenizer.GetStopWords("Z://文稿//留学//UTDallas//textbooks//6322_spring2017_Information Retrieval//resourcesIR//stopwords");
		File[] filelist = Tokenizer.GetAllFiles("Z://文稿/留学/UTDallas/textbooks/6322_spring2017_Information Retrieval/Cranfield");

		Dictionary myDict1 = new Dictionary();
		Dictionary myDict2 = new Dictionary();
		List<Document> docInfoList1 = new ArrayList<Document>();
		List<Document> docInfoList2 = new ArrayList<Document>();
		int max_tf1 = 0;
		int max_tf2 = 0;
		int doclen1 = 0;
		int doclen2 = 0;
		
		if (!islemmatize)  // version 2
		{
			for(int i = 0; i < filelist.length; i++)
			{
				StringBuilder originaltext = Tokenizer.ReadFileText(filelist[i]);
				StringBuilder filteredtext = Tokenizer.FilterHTML(originaltext);
				List<String> rawtokens = Tokenizer.Tokenize(filteredtext, false);  // without stemmatization
				
				// There seems to be potential problems for stemmatization before removing stop words, since words other than stop words might be stemmed into stop words.
				// But if stop words are removed before stemmatization, doclen2 cannot be calculated correctly.
				List<String> tokens2 = Tokenizer.StemTokens(rawtokens);  // with stemmatization
				doclen2 = tokens2.size();  // doclen includes occurrences of stop words
				tokens2 = Tokenizer.FilterStopWords(tokens2, stopwords);  // filter stop words after stemmatization
				max_tf2 = Tokenizer.CountMaxTermFreq(tokens2);
				Dictionary.AddDictTerms(myDict2, tokens2, (i+1));
				
				Document newdoc2 = new Document();
				newdoc2.ID = i+1;
				newdoc2.max_tf = max_tf2;
				newdoc2.doclen = doclen2;
				docInfoList2.add(newdoc2);
				
				if (i%200 == 0)
				{
					System.out.print("\n" + i);
				}
				System.out.print(".");

			}
			
			System.out.println(" ");
			System.out.println("Start to write index...");
			
			WriteDocInfo(folderpath + "docInfo2", docInfoList2);
			
			if (!iscompress)
			{
				WriteIndex(folderpath + "Index_Version2.uncompressed", myDict2, docInfoList2);
			}
			else
			{
				WriteCompressedIndex_FrontCoding(folderpath + "Index_Version2.compressed",  folderpath + "TermString_Version2", myDict2, docInfoList2);
			}
		}
		else
		{
			Lemmatizer lemmatizer = new Lemmatizer();  //version 1
			for(int i = 0; i < filelist.length; i++)
			{
				StringBuilder originaltext = Tokenizer.ReadFileText(filelist[i]);
				StringBuilder filteredtext = Tokenizer.FilterHTML(originaltext);
				List<String> rawtokens = Tokenizer.Tokenize(filteredtext, false);  // without stemmatization

	            List<String> tokens1 = lemmatizer.Lemmatize(rawtokens, false);
	            doclen1 = tokens1.size();  // doclen includes occurrences of stop words
				tokens1 = Tokenizer.FilterStopWords(tokens1, stopwords);  // lemmatize raw tokens before filtering stop words
				max_tf1 = Tokenizer.CountMaxTermFreq(tokens1);  // count maximum term frequency in a document after removing stop words
				Dictionary.AddDictTerms(myDict1, tokens1, (i+1));
				
				//PrintDict(myDict1);
				
				Document newdoc1 = new Document();
				newdoc1.ID = i+1;
				newdoc1.max_tf = max_tf1;
				newdoc1.doclen = doclen1;
				docInfoList1.add(newdoc1);
				
				if (i%200 == 0)
				{
					System.out.print("\n" + i);
				}
				System.out.print(".");

			}
			
			System.out.println(" ");
			System.out.println("Start to write index...");
			
			WriteDocInfo(folderpath + "docInfo1", docInfoList1);
			
			if(!iscompress)
			{
				WriteIndex(folderpath + "Index_Version1.uncompressed", myDict1, docInfoList1);
			}
			else
			{
				WriteCompressedIndex_Block8(folderpath + "Index_Version1.compressed",  folderpath + "TermString_Version1", myDict1, docInfoList1);
			}
		}
		
		//PrintDict(myDict1);
		//PrintDict(myDict2);
		//PrintDocInfo(docInfoList1);
		//PrintDocInfo(docInfoList2);

		System.out.println(" ");
		System.out.println("Write complete!");
    }

	public static void main(String[] args)
	{
		Instant dtStart = Instant.now();

		BuildIndex(false, true);  // Version1.uncompressed
		BuildIndex(false, false); // Version2.uncompressed
		BuildIndex(true, true); // Version1.compressed
		BuildIndex(true, false); // Version2.compressed
		
		Instant dtEnd = Instant.now();
		
		System.out.println("Time elapsed: " + Duration.between(dtStart, dtEnd).toMillis() + "milliseconds");
		
	}
}
