package com.ssp5zone.model;

/**
 * 
 * A simple model that contains a word and where it is located in the dictionary.
 * 
 * @author saurabh
 *
 */
public class WordMapModel 
{

	private String word;
	
	private int fileName;
	private int defIndex;
	private int defLength;
	
	public WordMapModel()
	{
		
	}
	
	public WordMapModel(String word, int[] posInfo)
	{
		this.word = word;
		this.fileName = posInfo[0];
		this.defIndex = posInfo[1];
		this.defLength= posInfo[2];
	}
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getFileName() {
		return fileName;
	}
	public void setFileName(int fileName) {
		this.fileName = fileName;
	}
	public int getDefIndex() {
		return defIndex;
	}
	public void setDefIndex(int defIndex) {
		this.defIndex = defIndex;
	}
	public int getDefLength() {
		return defLength;
	}
	public void setDefLength(int defLength) {
		this.defLength = defLength;
	}
	
}
