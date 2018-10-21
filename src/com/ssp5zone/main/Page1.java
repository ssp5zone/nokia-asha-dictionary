package com.ssp5zone.main;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * The starting point of the app.
 * 
 * @author saurabh
 *
 */
public class Page1 extends MIDlet 
{

	private Page1View page1= null;
	
	public Page1() 
	{
		// TODO Auto-generated constructor stub
	}


	protected void startApp() throws MIDletStateChangeException 
	{
		Displayable current = Display.getDisplay(this).getCurrent();
        if (current == null) 
        {
        	// Page1View is the 1st thing that is displayed when the app starts.
            page1 = new Page1View(this);
            Display.getDisplay(this).setCurrent(page1);
        }
       
        
	}
	
	// Used to pass the user from one display to another
	public void passControl(Displayable displayable)
	{
        Display.getDisplay(this).setCurrent(displayable);
	}

	protected void destroyApp(boolean unconditional)
			throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

}
