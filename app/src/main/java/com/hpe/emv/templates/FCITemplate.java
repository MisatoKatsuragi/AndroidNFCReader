package com.hpe.emv.templates;

//Imports

import com.hpe.emv.Response;
import com.hpe.emv.AdditionalTags;

/**
 * The EMV FCI Proprietary Template
 */
public class FCITemplate {
	
	private static String SPC= "    ";
	
	/* The log tag */
	private static final String TAG 				= "HostPosEmulation";
	private static final String LOG_PREFIX 			= "FCITemplate :";


	Response data;
	Response fci;
	Response dfName;
	FCIProprietaryTemplate fciProprietaryTemplate;
	
	/**
	 * Constructor
	 * @param data
	 */
	public FCITemplate(Response data) {
		
		//Log.i(TAG, LOG_PREFIX + "Data : " + data.toHex());
		
		this.data = data;
		this.fci = data.getTag(AdditionalTags.FCI_TEMPLATE);
		this.dfName = fci.getTag(AdditionalTags.DF_NAME);
			
		this.fciProprietaryTemplate = new FCIProprietaryTemplate(fci.getTag(AdditionalTags.FCI_PROPRIETARY_TEMPLATE));
	}

	
	/**
	 * @return the FCIProprietaryTemplate
	 */
	public FCIProprietaryTemplate getFCIProprietaryTemplate() {
		return fciProprietaryTemplate;
	}
	
	public String toString(){
		
		StringBuffer result = new StringBuffer();
		constructLine(result, dfName, "dfName");

		result.append(fciProprietaryTemplate.toString());
		result.append("\n");

		return result.toString();
	}
	
	private void constructLine(StringBuffer buffer, Response obj, String label){
		
		if(obj!=null){
			buffer.append(SPC);
			buffer.append(label);
			buffer.append(" : ");
			buffer.append(obj.toHex());
			buffer.append("\n");
		}
	}

	
}
