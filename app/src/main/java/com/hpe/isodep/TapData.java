package com.hpe.isodep;

public class TapData {

	public String message;
	public int code;
	public String card;
	public String exp;
	
	/**
	 * Constructor
	 * @param code
	 * @param message
	 */
	public TapData(int code, String message){
		this.code = code;
		this.message = message;
	}

}
