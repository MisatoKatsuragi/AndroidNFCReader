package com.hpe.emv;

// Imports
import java.io.UnsupportedEncodingException;
import java.util.Arrays;


/**
 * Utilities for convert and concatenation methods.
 *
 */
public class Util {


	static String toHex(byte[] hex) {
		StringBuilder stringBuilder = new StringBuilder();
		for (byte b : hex){
			stringBuilder.append(String.format("%02X", b));
		}
		return stringBuilder.toString();
	}
	
	static byte[] getBytes(String string) {
		return string.getBytes();
	}

	static String toASCII(byte[] bytes){
		try {
			return new String(bytes, 0, bytes.length, "ASCII");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	static int toInt(byte[] bytes){
		StringBuilder stringBuilder = new StringBuilder();
		for (byte b : bytes){
			stringBuilder.append(b);
		}
		return Integer.parseInt(stringBuilder.toString());
	}
	
	static byte[] concat(byte... bytes){
		return concat(bytes);
	}
	
	static byte[] concat(byte[] a, byte[]... b) {
		int length = a.length;
		for (byte[] c : b) {
			length += c.length;
		}
		
		byte[] out = Arrays.copyOf(a, length);
		
		int offset = a.length;
		for (byte[] c : b) {
			System.arraycopy(c, 0, out, offset, c.length);
			offset += c.length;
		}
		return out;
	}
	
}

