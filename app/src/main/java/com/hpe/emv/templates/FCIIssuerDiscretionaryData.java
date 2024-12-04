package com.hpe.emv.templates;

// Imports

import com.hpe.emv.Response;
import com.hpe.emv.AdditionalTags;


/**
 * The EMV FCI Issuer Discretionary Data
 *
 */
public class FCIIssuerDiscretionaryData {
	
	private static String SPC= "          ";
	
	/* The log tag */
	private static final String TAG 				= "HostPosEmulation";
	private static final String LOG_PREFIX 			= "FCIIssuerDiscretionaryData :";


	Response data;
	ApplicationTemplate applicationTemplate;
	Response logEntry;
	
	FCIIssuerDiscretionaryData(Response data){
		
		//Log.i(TAG, LOG_PREFIX + "Data : " + data.toHex());
		
		this.data = data;
		this.applicationTemplate = new ApplicationTemplate(data.getTag(AdditionalTags.APPLICATION_TEMPLATE));
		this.logEntry = data.getTag(AdditionalTags.LOG_ENTRY);
	}

	public String toString(){
		
		StringBuffer result = new StringBuffer();

		return result.toString();
	}

}
