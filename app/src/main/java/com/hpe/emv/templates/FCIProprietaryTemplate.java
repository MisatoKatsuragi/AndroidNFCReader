package com.hpe.emv.templates;

//Imports

import com.hpe.emv.Response;
import com.hpe.emv.AdditionalTags;

/**
 * The EMV FCI Proprietary Template
 *
 */
public class FCIProprietaryTemplate {
	
	private static String SPC= "        ";
	
	/* The log tag */
	private static final String TAG 				= "HostPosEmulation";
	private static final String LOG_PREFIX 			= "FCIProprietaryTemplate :";


	Response data;
	Response applicationLabel;
	Response applicationPriorityIndicator;
	Response pdol;
	Response languagePreference;
	Response issuerCodeTableIndex;
	Response applicationPreferredName;
	
	FCIIssuerDiscretionaryData issuerDiscretionaryData;
	
	
	
	
	
	/**
	 * Constructor
	 * @param data
	 */
	FCIProprietaryTemplate (Response data){
		
		//Log.i(TAG, LOG_PREFIX + "Data : " + data.toHex());
		
		try{
			this.data = data;
			this.applicationLabel = data.getTag(AdditionalTags.APPLICATION_LABEL);
			this.applicationPriorityIndicator = data.getTag(AdditionalTags.APPLICATION_PRIORITY_INDICATOR);
			this.languagePreference = data.getTag(AdditionalTags.LANGUAGE_PREFERENCE);
			this.issuerCodeTableIndex = data.getTag(AdditionalTags.ISSUER_CODE_TABLE_INDEX);
			this.pdol = data.getTag(AdditionalTags.PDOL);
			this.applicationPreferredName = data.getTag(AdditionalTags.APPLICATION_PREFERRED_NAME);
			this.issuerDiscretionaryData = new FCIIssuerDiscretionaryData(data.getTag(AdditionalTags.FCI_ISSUER_DISCRETIONARY_DATA_TEMPLATE));
			
		}catch(Exception e){
			// Optional element might be null
		}
	}

	/**
	 * @return the languagePreference
	 */
	public Response getLanguagePreference() {
		return languagePreference;
	}

	/**
	 * @return the issuerCodeTableIndex
	 */
	public Response getIssuerCodeTableIndex() {
		return issuerCodeTableIndex;
	}

	/**
	 * @return the issuerDiscretionaryData
	 */
	public FCIIssuerDiscretionaryData getIssuerDiscretionaryData() {
		return issuerDiscretionaryData;
	}

	/**
	 * @return the pdol
	 */
	public Response getPdol() {
		return pdol;
	}

	/**
	 * @return the applicationPreferredName
	 */
	public Response getApplicationPreferredName() {
		return applicationPreferredName;
	}

	/**
	 * @return the applicationPriorityIndicator
	 */
	public Response getApplicationPriorityIndicator() {
		return applicationPriorityIndicator;
	}

	/**
	 * @return the applicationLabel
	 */
	public Response getApplicationLabel() {
		return applicationLabel;
	}
	
	
	public String toString(){
		
		StringBuffer result = new StringBuffer();
		constructLine(result, applicationLabel,             "applicationLabel            ", 2);
		constructLine(result, applicationPriorityIndicator, "applicationPriorityIndicator", 1);
		constructLine(result, pdol,                         "pdol                        ", 1);
		constructLine(result, languagePreference,           "languagePreference          ", 2);
		constructLine(result, issuerCodeTableIndex,         "issuerCodeTableIndex        ", 1);
		
		constructLine(result, applicationPreferredName,     "applicationPreferredName    ", 2);
		
		

		return result.toString();
	}
	
	
	private void constructLine(StringBuffer buffer, Response obj, String label, int format){
		
		if(obj!=null){
			buffer.append(SPC);
			buffer.append(label);
			buffer.append(" : ");
			if(format==1){
				buffer.append(obj.toHex());
			}else {
				buffer.append(obj.toASCII());
			}
			buffer.append("\n");
		}
	}

}
