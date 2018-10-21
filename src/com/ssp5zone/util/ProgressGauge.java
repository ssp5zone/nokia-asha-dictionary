package com.ssp5zone.util;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;

/**
 * 
 * I created but never used it.
 * 
 * @author saurabh
 *
 */
public class ProgressGauge 
{
	
	private static Gauge determinateGauge;
	
	private static int itemNumber;
	
	public static void generateGauge(Form form)
	{
		determinateGauge = new Gauge(
	            "Searching...",
	            false,
	            10000,
	            0);
		
		itemNumber=form.append(determinateGauge);
	}
	
	public static void updateGauge()
	{
		determinateGauge.setValue(determinateGauge.getValue() + 1);
	}
	
	public static void removeGauge(Form form)
	{
		form.delete(itemNumber);
		determinateGauge.setValue(0);
	}

}
