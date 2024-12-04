package com.hpe.emv;

/**
 * EMV Tags
 *
 */
public class AdditionalTags {

	//Dedicated File Name
	public static final byte DF_NAME = (byte) 0x84;
	
	//File Control Information Template
	public static final byte FCI_TEMPLATE = 0x6F;
	
	//File Control Information Proprietary Template
	public static final byte FCI_PROPRIETARY_TEMPLATE = (byte) 0xA5;
	
	//Application Label
	public static final byte APPLICATION_LABEL = 0x50;
	
	//Application Priority Indicator
	public static final byte APPLICATION_PRIORITY_INDICATOR = (byte) 0x87;
	
	//Application Identifier
	public static final byte AID = 0x4F;
	
	//Short File Identifier
	public static final byte SFI = (byte) 0x88;
	
	//File Control Information Issuer Discretionary Data Template
	public static final byte FCI_ISSUER_DISCRETIONARY_DATA_TEMPLATE = (byte) 0xBF0C;
	
	public static final byte APPLICATION_TEMPLATE = 0x61;
	
	//Processing Options Data Object List
	public static final byte PDOL = (byte) 0x9F38;

	//Application Interchange Profile
	public static final byte AIP = (byte) 0x80;
	
	//Application File Locator
	public static final byte AFL = (byte) 0x94;

	public static final byte LANGUAGE_PREFERENCE = (byte) 0x5F2D;
	
	public static final byte ISSUER_CODE_TABLE_INDEX = (byte) 0x9F11;

	public static final byte APPLICATION_PREFERRED_NAME = (byte) 0x9F12;

	public static final byte LOG_ENTRY = (byte) 0x9F4D;
	
	public static final byte EMV_PROPRIETARY_TEMPLATE = 0x70;

	public static final byte CARDHOLDER_NAME = (byte) 0x5F20;

	public static final byte TRACK_2_DATA = 0x57;

	public static final byte TRACK_1_DATA = (byte) 0x9F1F;

}
