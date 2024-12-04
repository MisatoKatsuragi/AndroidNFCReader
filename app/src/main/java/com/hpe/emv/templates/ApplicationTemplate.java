package com.hpe.emv.templates;

// Imports
import com.hpe.emv.Response;
import com.hpe.emv.AdditionalTags;



/**
 * The EMV Application template
 *
 */
public class ApplicationTemplate {
	
	private static String SPC= "            ";

	Response data;
	Response aid;
	Response apl;
	Response api;
	
	ApplicationTemplate(Response data) {
		this.data = data;
		this.aid = data.getTag(AdditionalTags.AID);
		this.apl = data.getTag(AdditionalTags.APPLICATION_LABEL);
		this.api = data.getTag(AdditionalTags.APPLICATION_PRIORITY_INDICATOR);
	}
	
	public String toString(){
		
		String result = 
				SPC + "aid : " + aid.toHex() + "\n" +
				SPC + "apl : " + apl.toHex() + "\n" +
				SPC + "api : " + api.toHex() + "\n" ;
		return result;
	}

}
