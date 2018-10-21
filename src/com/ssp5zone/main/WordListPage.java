package com.ssp5zone.main;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

import com.ssp5zone.model.WordMapModel;

/**
 * 
 * The 2nd View in the App. This one displays a list of all 
 * entries that were found associated to the entered word.
 * 
 * The user can select one word from the list and the meaning for that would be displayed 
 * in the next page.
 * 
 * @author saurabh
 *
 */
public class WordListPage extends List
implements CommandListener 
{
	private final Command BACK;
	private final Command MEANING;
	
	private Page1 midlet;
	
	private Vector wordVector;
	
	private Displayable parentDisplay;
	private CommandListener parentCommandListener;
	
	public WordListPage(Page1 mainMidlet, Displayable parentDisplay) 
	{
		super("Words Found..", List.EXCLUSIVE);
		
		BACK = new Command("BACK", Command.BACK,2);
        this.addCommand(BACK);
                
        MEANING = new Command("Meaning", Command.OK, 1);
        this.addCommand(MEANING);
        
        this.midlet = mainMidlet;
        this.parentDisplay=parentDisplay;
		
        this.setCommandListener(this);
        
	}

	public void populateWordList(Vector wordVector)
	{
		this.wordVector = wordVector;
		
		if(wordVector!=null && !wordVector.isEmpty())
		{
			for(int index=0; index<wordVector.size();index++)
			{
				WordMapModel wmp = (WordMapModel)wordVector.elementAt(index);
				
				this.append(wmp.getWord(),null);
			}
			
		}
	}

	public void commandAction(Command c, Displayable d) 
	{
		if(c == BACK)
		{
			midlet.passControl(parentDisplay);
		}
		
		// When the user selects meaning button
		if (c == MEANING)
		{
			// Get the word that was selected and go to next page
			WordMapModel wmp = (WordMapModel)wordVector.elementAt(this.getSelectedIndex());
			MeaningPage mp = new MeaningPage(midlet, this);
			mp.getMeaning(wmp);
		}
	}
}