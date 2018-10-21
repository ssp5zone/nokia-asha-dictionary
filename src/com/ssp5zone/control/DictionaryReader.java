package com.ssp5zone.control;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.StringItem;

import com.ssp5zone.model.WordMapModel;
import com.ssp5zone.util.ProgressGauge;

/**
 * 
 * The class that is responsible for actually reading the data from dictionary. 
 * 
 * It does 2 jobs,
 * 
 * 1. Read from the map files to provide a list (map-vectors) of a given word.
 * 2. When asked use those vectors to actually fetch the meaning.
 * 
 * @author saurabh
 *
 */
public class DictionaryReader 
{
	public FileConnection mapFileCon = null;
	public FileConnection DBFileCon = null;
	
	/**
	 * Older stub. No longer used. Refer the buffered version of the same below.
	 * 
	 * @param word
	 * @return
	 */
	public Vector getWordVectors(String word)
	{
		Vector wordVector = new Vector();
		
		int[] posInfo = null;
		word = word.trim();
        word = word.toUpperCase();
        
        if(word.charAt(0)<65 || word.charAt(0)>90)
        {
        	return null;
        }
        
        String SEARCHFILE = new String("/DictionaryDB/WordMap/"+word.charAt(0)+"/"+word.charAt(0)+".txt");
        
        InputStreamReader reader = openFile_internal(SEARCHFILE);
        
        if(reader==null)
        {
        	return null;
        }
        
        String line = null;
        
        boolean found=false;
        
        while ((line = readLine(reader)) != null) 
        {
        	if(line.indexOf(word)>-1) 
            { 
                posInfo = new int[3];
                String temp = line.substring(line.indexOf(">>")+3,line.length());
                found=true;
                String match = line.substring(0,line.indexOf(">>"));
                posInfo[0] = Integer.parseInt(temp.substring(0,temp.indexOf(",")));
                posInfo[1] = Integer.parseInt(temp.substring(temp.indexOf(",")+1,temp.lastIndexOf((int)',')));
                posInfo[2] = Integer.parseInt(temp.substring(temp.lastIndexOf((int)',')+1,temp.length()));
                
                WordMapModel wmp = new WordMapModel(match, posInfo);
                wordVector.addElement(wmp);
            }
        }   
		if(found==false)
		{
			return null;
		}
		
		destroyCon(reader);
		
		return wordVector;
	}
	
	/**
	 * 
	 * Due to the low RAM of device, the above caused the device to hang.
	 * Hence, a buffered version of the above.
	 * 
	 * This method takes in word and searches in the word maps files for all the references to that 
	 * word. 
	 * 
	 * A map file does not contain the entire meaning, rather just a reference to where the meaning is.
	 * Right now, we don't care about the meaning, just get all possible meaning-maps of that word 
	 * along with a list of any similar words found to provide as a suggestion.
	 * 
	 * @param word
	 * @return
	 */
	public Vector getWordVectors_buffered(String word)
	{
		// Contains a list of all the words and similar words found along with their maps.
		Vector wordVector = new Vector();
		
		int[] posInfo = null;
		word = word.trim();
        word = word.toUpperCase();
        
        if(word.charAt(0)<65 || word.charAt(0)>90)
        {
        	return null;
        }
        
        // This is where the maps are stores internally. Based on the first character of the input, 
        // look for in that particular alphabet's map file.
        String SEARCHFILE = new String("/DictionaryDB/WordMap/"+word.charAt(0)+"/"+word.charAt(0)+".txt");
        
        InputStreamReader reader = openFile_internal(SEARCHFILE);
        
        if(reader==null)
        {
        	return null;
        }
        
        // Here is the difference when compared to above. Instead of reading the whole file
        // just read chunks of 1kB at a time
        char[] buffer = new char[1024];
        int read;
        String line = null;
        boolean found=false;
        int currPos;
        
        try {
			while ((read=reader.read(buffer, 0, buffer.length)) != -1) 
			{
				currPos = 0;
				
				while(currPos<read)
				{
					StringBuffer string = new StringBuffer("");
			    	while (buffer[currPos] != '\n' && buffer[currPos]!='\r') 
				        {  				        	
				            string.append(buffer[currPos++]);
				            if(currPos==read)
				            {
				            	read=reader.read(buffer, 0, buffer.length);
				            	currPos=0;
				            }
				        }
					line=string.toString();
					currPos+=1;
					if(line.indexOf(word)>-1) 
			        { 
			            posInfo = new int[3];
			            String temp = line.substring(line.indexOf(">>")+3,line.length());
			            found=true;
			            String match = line.substring(0,line.indexOf(">>"));
			            posInfo[0] = Integer.parseInt(temp.substring(0,temp.indexOf(",")));
			            posInfo[1] = Integer.parseInt(temp.substring(temp.indexOf(",")+1,temp.lastIndexOf((int)',')));
			            posInfo[2] = Integer.parseInt(temp.substring(temp.lastIndexOf((int)',')+1,temp.length()));
			            
			            WordMapModel wmp = new WordMapModel(match, posInfo);
			            wordVector.addElement(wmp);
			        }
					
				}
			  
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if(found==false)
		{
			return null;
		}
		
		destroyCon(reader);
		
		return wordVector;
	}
	
	
	
	/**
	 * 
	 * The function that actually fetches meaning given the word map model generated via above function. 
	 * 
	 * @param wmp
	 * @return
	 */
	public String getMeaning(WordMapModel wmp)
	{
		String meaning=null;
		
		byte[] b = new byte[wmp.getDefLength()];
		
		// The meaning are stored in data chunks at this path 
		String DBFile = new String("/DictionaryDB/FileDB/"+wmp.getFileName());
		
		DataInputStream ds = openFile_internal_ds(DBFile);

		// If something went worng opening the file.
		if(ds==null)
		{
			return "Run boy Run...";
		}
		
		try 
		{
			boolean success = false;
			
			long skippedLen =0 ;
			long remaningBytes = wmp.getDefIndex();
			
			int noOfTry = 0 ;
			
			/**
			 * What the hell is this??
			 * 
			 * Ok, the skip() in older versions of Java (like this one) was
			 * like a moody girlfriend. If you ask it to skip a certain amount of bytes,
			 * it would skip only some - depending upon its mood. Aaaand it's random.
			 * 
			 * So you have to keep a track of how much amount of data it has skipped and 
			 * ask it to skip the remainder again!! Well you have to keep doing this until it has skipped
			 * the needed amount.
			 * 
			 */
			while(success==false)
			{
				skippedLen = ds.skip(remaningBytes);
				remaningBytes-=skippedLen;
				if(remaningBytes==0)
				{
					success = true;
				}
				
				System.out.println(++noOfTry+"Skipped Bytes:"+skippedLen);
				
				if(noOfTry==100)
				{
					return "Unable to Skip in 100 tries";
				}
					
			}
			
			ds.read(b);
			
			meaning = new String(b);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		destroyCon(ds);
		
		return meaning;
	}
	
	
	
	/**
	 * 
	 * An older stub used with getWordVectors(). No longer used now.
	 * 
	 * @param reader
	 * @return
	 */
	private String readLine(InputStreamReader reader) 
	{
		StringBuffer string = new StringBuffer("");
    	try 
		{
			int readChar;
			readChar = reader.read();
		
	        if (readChar == -1) 
	        {
	            return null;
	        }
	        // Read until end of file or new line
	        while (readChar != -1 && readChar != '\n') {
	            // Append the read character to the string. Some operating systems
	            // such as Microsoft Windows prepend newline character ('\n') with
	            // carriage return ('\r'). This is part of the newline character
	            // and therefore an exception that should not be appended to the
	            // string.
	            if (readChar != '\r') {
	                string.append((char)readChar);
	            }
	            // Read the next character
	            readChar = reader.read();
	        }
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return string.toString();
    }
	
	private InputStreamReader openFile_internal(String path) 
	{
		System.out.println("Stream : "+getClass().getResourceAsStream(path));
        InputStreamReader reader = new InputStreamReader(
            getClass().getResourceAsStream(path));
        return reader;
    }
	
	private DataInputStream openFile_internal_ds(String path) 
	{
		System.out.println("Stream : "+getClass().getResourceAsStream(path));
		DataInputStream reader = new DataInputStream(
            getClass().getResourceAsStream(path));
        return reader;
    }
	
	private void destroyCon(InputStreamReader reader)
	{
		try
		{
			if(reader!=null)
			{
				reader.close();
			}
			if(this.mapFileCon!=null)
			{
				this.mapFileCon.close();
			}
			if(DBFileCon!=null)
			{
				DBFileCon.close();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void destroyCon(DataInputStream reader)
	{
		try
		{
			if(reader!=null)
			{
				reader.close();
			}
			if(this.mapFileCon!=null)
			{
				this.mapFileCon.close();
			}
			if(DBFileCon!=null)
			{
				DBFileCon.close();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}
