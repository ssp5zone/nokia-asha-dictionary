package com.ssp5zone.main;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.TextField;


import com.nokia.mid.ui.orientation.Orientation;
import com.nokia.mid.ui.orientation.OrientationListener;
import com.ssp5zone.control.DictionaryReader;
import com.ssp5zone.model.WordMapModel;
import com.ssp5zone.util.ProgressGauge;

/**
 * 
 * A very simple initial view that contains a Form. It has,
 * an input to type the word, a Search button and an Exit button.
 * 
 * 
 * @author saurabh
 *
 */
public class Page1View extends Form
implements CommandListener, OrientationListener 
{
	
	private final Page1 mainMidlet;
	
	private final Command exitCommand;
	private final Command SEARCH;
	
	private TextField textField;
    private String textFieldText;
	
    // Support for at max 30 char word.
    private int MAX_CHARS = 30;
    
	public Page1View(Page1 midlet) 
	{
		super("Dictionary");
		mainMidlet = midlet;
	
		Orientation.addOrientationListener(this);
		
		exitCommand = new Command("Exit", Command.EXIT, 1);
        this.addCommand(exitCommand);
                
        SEARCH = new Command("Search", Command.OK, 1);
        this.addCommand(SEARCH);
        
        // Add listener to the buttons we just added
        this.setCommandListener(this);
        
        constructForm();
	}
	
	public void constructForm()
	{
		textField = new TextField("Type the word", "", MAX_CHARS, TextField.ANY);
        textFieldText = textField.getString();
        this.append(textField);
	}

	public void displayOrientationChanged(int newDisplayOrientation) 
	{
		//don't bother about orientation
	}
	
	public void commandAction(Command c, Displayable d) 
	{
		if (c == exitCommand) 
		{
            mainMidlet.notifyDestroyed();
            System.out.println("Exiting Application.");
        }
		if (c == SEARCH) 
		{
			// If user clicked the search button
			textFieldText = textField.getString();
			searchWord(textFieldText);
        }
	}
	
	private void searchWord(String word)
	{
		// If nothing was entered and user clicks search
		if(word==null||word.equals(""))
		{
			super.append("Are you kidding me!!");
			return;
		}
		
		// Look into dictionary map for our word 
		DictionaryReader wordObj = new DictionaryReader();
		Vector wordVector = wordObj.getWordVectors_buffered(word);
		
		// If at-least 1 reference was found to it 
		if(wordVector!=null && !wordVector.isEmpty())
		{
			// Go to next page, where you list all the references.
			WordListPage wlp = new WordListPage(mainMidlet,this);
			wlp.populateWordList(wordVector);
			mainMidlet.passControl(wlp);
		}
		else
			super.append("Run, Run, Run for your life....");
	}


}
