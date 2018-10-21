package com.ssp5zone.main;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.ssp5zone.control.DictionaryReader;
import com.ssp5zone.model.WordMapModel;

/**
 * 
 * The final page that contains the meaning for the selected word.
 * 
 * @author saurabh
 *
 */
public class MeaningPage extends Form
implements CommandListener 
{

	private final Command BACK;
	
	private Page1 midlet;
		
	private Displayable parentDisplay;
	
	private WordMapModel wmp;
	
	public MeaningPage(Page1 mainMidlet, Displayable parentDisplay,String word) 
	{
		super(word);
		BACK = new Command("BACK", Command.BACK,2);
        this.addCommand(BACK);
        
        this.midlet = mainMidlet;
        this.parentDisplay=parentDisplay;
		
        this.setCommandListener(this);
		
	}
	
	public MeaningPage(Page1 mainMidlet, Displayable parentDisplay) 
	{
		super("Meaning");
		BACK = new Command("BACK", Command.BACK,2);
        this.addCommand(BACK);
        
        this.midlet = mainMidlet;
        this.parentDisplay=parentDisplay;
		
        this.setCommandListener(this);
		
	}
	
	
	public void getMeaning(WordMapModel wmp)
	{
		this.wmp = wmp;
		
		super.append(wmp.getWord());
				
		DictionaryReader fileObj = new DictionaryReader();
		String meaning = fileObj.getMeaning(wmp);
		
		super.append(meaning);
		
		midlet.passControl(this);
	}

	public void commandAction(Command c, Displayable d) 
	{
		if(c == BACK)
		{
			midlet.passControl(parentDisplay);
		}
	}

}
