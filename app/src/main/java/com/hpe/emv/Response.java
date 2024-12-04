package com.hpe.emv;

// Imports
import java.util.Arrays;

import android.util.Log;


/**
 * An EMV command response object 
 */
public class Response {
	
	/* The log tag */
	private static final String TAG 				= "HostPosEmulation";
	private static final String LOG_PREFIX 			= "MAIN_ACTIVITY :";

	
	private static byte [] EMPTY = {};
	private byte[] data;
	
	/**
	 * Constructor
	 * @param data
	 */
	public Response(byte[] data){
		this.data = data;
	}
	
	/**
	 * Constructor from a String
	 * @param data
	 */
	public Response(String data){
		this.data = Util.getBytes(data);
	}
	
	
	public Response getTag(byte tag){
		int typeLength;
		for (int i = 0; i < data.length && i >= 0; i++){
			typeLength = (data[i] & 0x1F) == 0x1F ? 2 : 1;
			if (data[i] == tag || ((data[i] & 0x1F) == 0x1F && (byte) (data[i] << 8 | data[i + 1]) == tag)) {
				return new Response(Arrays.copyOfRange(data, i + typeLength + 1, data[i + typeLength] + i + typeLength + 1));
			}
			else{
				i += data[i + typeLength] + typeLength;
			}
		}
		//return new Response(Util.getBytes("null"));
		return new Response(EMPTY);
	}
	
	/**
	 * Response data as a byte array
	 * @return
	 */
	public byte[] getBytes(){
		return data;
	}
	
	/**
	 * Response data as an hexa String
	 * @return
	 */
	public String toHex() {
		return Util.toHex(data);
	}
	
	/**
	 * Response data as an ascii String
	 * @return
	 */
	public String toASCII(){
		return Util.toASCII(data);
	}
	
	/**
	 * Concat the given byte to object data
	 * @return
	 */
	public byte[] concat(byte[] b){
		return Util.concat(data, b);
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}
	
	
	public Response parse(){
		
		int typeLength;
		for (int i = 2; i < data.length && i >= 0; i++){
			typeLength = (data[i] & 0x1F) == 0x1F ? 2 : 1;
			Log.i(TAG, LOG_PREFIX + "type : " + typeLength);
			//Response tag = new Response(Arrays.copyOfRange(data, i + typeLength + 1, data[i + typeLength] + i + typeLength + 1));
			//Log.i(TAG, LOG_PREFIX + "tag : " + tag.toHex());
			i += data[i + typeLength] + typeLength;

		}
		//return new Response(Util.getBytes("null"));
		return new Response(EMPTY);
	}

	
	
}