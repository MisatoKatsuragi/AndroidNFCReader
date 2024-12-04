package com.hpe.isodep;

// Imports

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.atosworldline.rd.payment.emv.apdus.Command;
import com.atosworldline.rd.payment.emv.constants.CommandEngine;
import com.atosworldline.rd.payment.emv.datatype.tlv.EmvTLV;
import com.atosworldline.rd.payment.emv.datatype.tlv.EmvTag;
import com.atosworldline.rd.payment.emv.datatype.tlv.TLV;
import com.atosworldline.rd.payment.emv.datatype.DOL;
import com.atosworldline.rd.payment.emv.constants.Tags;
import com.atosworldline.rd.util.HexString;
import com.hpe.emv.Response;

import com.hpe.emv.templates.FCIProprietaryTemplate;
import com.hpe.emv.templates.FCITemplate;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import android.nfc.NfcAdapter.ReaderCallback;
import android.nfc.tech.IsoDep;
import android.nfc.Tag;
import android.os.Vibrator;
import android.util.Log;

public class EMVReaderCallback implements ReaderCallback, Tags,  CommandEngine{

	private  List<EmvTLV> recordList = new ArrayList<EmvTLV>();

	/* The log tag */
	private static final String TAG 				= "HostPosEmulation";
	private static final String LOG_PREFIX 			= "MAIN_ACTIVITY :";

	private static final byte[] CDOL1_TAG = { (byte)0x8C};

	private static final byte[] AID_MasterCard = { (byte)0xA0, 0x00, 0x00, 0x00, 0x04, 0x10, 0x10 };
	private static final byte[] AID_Visa = { (byte)0xA0, 0x00, 0x00, 0x00, 0x03, 0x10, 0x10 };
	//private static final byte[] AID_CB = { (byte)0xA0, 0x00, 0x00, 0x00, 0x42, 0x10, 0x10 };

	public static final byte[] SELECT_CLA_INS_P1_P2 = { (byte)0x00, (byte)0xA4, (byte)0x04, (byte)0x00 };
	public static final byte[] GENAC_CLA_INS_P1_P2  = { (byte)0x80, (byte)0xAE, (byte)0x40, (byte)0x00 };

	public static final byte[] GPO_CLA_INS_P1_P2 	= { (byte)0x80, (byte)0xA8, (byte)0x00, (byte)0x00 };

	EmvTLV cdol1Tlv;

	// Get processing Option
	private static final byte[] DOL = {
			(byte)0x80, (byte)0xA8, (byte)0x00, (byte)0x00,
			(byte)0x3A, (byte)0x83, (byte)0x38,

			(byte)0x20, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x50, (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x09, (byte)0x78, (byte)0x14, (byte)0x10, (byte)0x06, (byte)0x00,
			(byte)0x06, (byte)0x5F, (byte)0x32, (byte)0xC4, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x00,

	};


	private CaptureTapActivity activity;
	private NFCReaderEventHandler handler;
	private Iterator<Command> readrecordIterator;


	/**
	 *
	 * @param activity
	 * @param handler
	 */
	public EMVReaderCallback(CaptureTapActivity activity, NFCReaderEventHandler handler){
		this.activity 	= activity;
		this.handler	= handler;
	}


	/**
	 *
	 */
	@Override
	public void onTagDiscovered(Tag tag) {

		Log.i(TAG, LOG_PREFIX + "onTagDiscovered(): HCEfound");


		String card 	= null;
		String expiry 	= null;

		// 1 second vibration
		Vibrator vib=(Vibrator)activity.getSystemService(Context.VIBRATOR_SERVICE);
		vib.vibrate(1000);

		handler.obtainMessage(1, new TapData(2, "READING CARD")).sendToTarget();

		IsoDep isoDep = IsoDep.get(tag);
		Log.i(TAG, LOG_PREFIX + "onTagDiscovered(): Connecting HCE");
		try {
			isoDep.connect();
		} catch (IOException e) {
			Log.w(TAG, LOG_PREFIX + "onTagDiscovered(): Cannot connect HCE");
			handler.obtainMessage(2, new TapData(2, "Cannot connect HCE")).sendToTarget();
			return;
		}

		Log.i(TAG, LOG_PREFIX + "onTagDiscovered(): HCE connected");

		// -------------------------------------------------------------
		// SELECT
		// -------------------------------------------------------------
		byte [] command = createSelectAidApdu(AID_MasterCard);
		//Log.i(TAG, LOG_PREFIX + "Select command  : " +  HexString.bytes2String(command));
		byte [] responseData = tranceive(isoDep, 3,  command);
		Log.i(TAG, LOG_PREFIX + "Processing MC card : Select response  : " +  HexString.bytes2String(responseData));
		if(responseData==null){
			return;
		}

		int cartType; // MC
		if(responseData[responseData.length-2]==(byte)0x90) { // Test MC
			cartType = 1; // MC
		}else {

			command = createSelectAidApdu(AID_Visa);
			responseData = tranceive(isoDep, 3, command);
			Log.i(TAG, LOG_PREFIX + "Processing Visa  : Select response  : " + HexString.bytes2String(responseData));
			if (responseData == null) {
				return;
			}

			if(responseData[responseData.length-2]==(byte)0x90) { // Test Visa
				cartType = 2; // Visa
			} else {
				Log.i(TAG, LOG_PREFIX + "No file found : Select response  : " + HexString.bytes2String(responseData));
				return;
			}
		}

		process(isoDep, responseData , cartType);

	}

	/**
	 *
	 * @param isoDep
	 * @param responseData The response to a SELECT command
	 */
	private void process(IsoDep isoDep, byte [] responseData, int cardType) {

		String card 	= null;
		String expiry 	= null;

		byte [] command;

		Response response  = new Response(responseData);
		FCITemplate fciTemplate = new FCITemplate(response);
		Log.i(TAG, LOG_PREFIX + "Select response : \n" + fciTemplate.toString());
		FCIProprietaryTemplate fciProprietaryTemplate = fciTemplate.getFCIProprietaryTemplate();


		// GET PROCESSING OPTION
		Response pdol = fciProprietaryTemplate.getPdol();
		Log.i(TAG, LOG_PREFIX + "Response PDOL : " + pdol.toHex());

		command = prepareGetProcessingOptionCommand(constructDOL(pdol.getBytes()));
		Log.i(TAG, LOG_PREFIX + "Get Processing Option command  : " +  HexString.bytes2String(command));
		responseData = tranceive(isoDep, 3,  command);
		if(responseData==null){
			return;
		}
		response  = new Response(responseData);
		Log.i(TAG, LOG_PREFIX + "Get Processing Option response : " + response.toHex());

		if(response.toHex().length()<5){
			handler.obtainMessage(2, new TapData(2, "ERROR READING CARD")).sendToTarget();
			return;
		}

		// READ RECORD 
		readrecordIterator =   getEmvAPDUReadRecord(response.toHex()).iterator();

		while(readrecordIterator.hasNext()){

			command = readrecordIterator.next().toBytes();
			byte[] b = new byte[command.length-1];
			for(int i=0; i<command.length-1;i++){
				b[i] = command[i];
			}
			command = b;
			responseData = tranceive(isoDep, 3,  command);
			response  = new Response(responseData);

			//byte sfi = (byte) ((command[2] >>>3) & 	(byte)0x1F);
			//byte record = (byte) (command[3] & 	(byte)0x1F);	
			byte sfi = (byte) ((command[3] >>>3) & 	(byte)0x1F);
			byte record = (byte)(command[2]);

			Log.i(TAG, LOG_PREFIX + "Read record  : " +  HexString.bytes2String(command));
			Log.i(TAG, LOG_PREFIX + "Read record : sfi : " + sfi + " - record : " + record);
			Log.i(TAG, LOG_PREFIX + "Read record : " + response.toHex());

			EmvTLV tlv = new TLV(responseData);
			recordList.add(tlv);

			/*
			appli.parseResponse(response.toHex());
			Log.i(TAG, LOG_PREFIX + "appli : " + appli.getTagValueList());
			for(TLValues tag:appli.getTagValueList()){
				System.out.println(tag.toString());
			}
			*/

			try {

				EmvTag cdol1Tag = new com.atosworldline.rd.payment.emv.datatype.tlv.Tag(HexString.parseHexString(Tags.CDOL1_TAG));
				cdol1Tlv = tlv.findSingleTag(cdol1Tag, null);
				if(cdol1Tlv != null){
					Log.i(TAG, LOG_PREFIX + "CDOL1  : " + HexString.bytes2String(cdol1Tlv.valueAsByteArray()));
				}
			} catch (Exception e) {
				Log.w(TAG, LOG_PREFIX + "Error gettin CDOL 1 (" + e +"]");
			}

			try {

				EmvTag panTag = new com.atosworldline.rd.payment.emv.datatype.tlv.Tag(HexString.parseHexString(Tags.APP_PAN_TAG));
				EmvTLV panTlv = tlv.findSingleTag(panTag, null);
				if(panTlv != null){
					card = HexString.bytes2String(panTlv.valueAsByteArray());
					Log.i(TAG, LOG_PREFIX + "PAN    : " + card);
				}
			} catch (Exception e) {
				Log.w(TAG, LOG_PREFIX + "Error getting panTag (" + e +"]");
			}
			try {
				EmvTag expTag = new com.atosworldline.rd.payment.emv.datatype.tlv.Tag(HexString.parseHexString(Tags.APP_EXPIRATION_DATE));
				EmvTLV expTlv = tlv.findSingleTag(expTag, null);
				if(expTlv != null){
					expiry =  formatExpDate( HexString.bytes2String(expTlv.valueAsByteArray()));
					Log.i(TAG, LOG_PREFIX + "EXP    : " + expiry);
				}
			} catch (Exception e) {
				Log.w(TAG, LOG_PREFIX + "Error getting expTag (" + e +"]");
			}
			try {
				EmvTag cardholderTag = new com.atosworldline.rd.payment.emv.datatype.tlv.Tag(HexString.parseHexString(Tags.CARDHOLDER_NAME));
				EmvTLV cardholderTlv = tlv.findSingleTag(cardholderTag, null);
				if(cardholderTlv != null){

					Log.i(TAG, LOG_PREFIX + "NAME   : " + HexString.bytes2String(cardholderTlv.valueAsByteArray()));
				}
			} catch (Exception e) {
				Log.w(TAG, LOG_PREFIX + "Error getting cardholderTag (" + e +"]");
			}

		}

		// GENERATEAC
		String amount = activity.getAmount();
		Log.i(TAG, LOG_PREFIX + "GenerateAC AMOUNT 1  : " +  amount);
		while (amount.length() < 12){
			amount = "0" + amount;
		}

		Log.i(TAG, LOG_PREFIX + "GenerateAC AMOUNT 2  : " +  amount);
		//Log.i(TAG, LOG_PREFIX + "GenerateAC command Date : " +  HexString.bytes2String(getDate()));
		//Log.i(TAG, LOG_PREFIX + "GenerateAC command Time : " +  HexString.bytes2String(getTime()));

		// Carte MC
		byte [] generateAC = null;
		if(cardType==1){

			String generateACData = amount + "000000000000025000800000000978131213001D832F600000000000000032323232323232323232323232323232323232320000000000001F030200";
			generateAC = createGenerateACApdu(generateACData);

		} else if(cardType==2){

			String generateACData = amount + "000000000000025000800000000978131213001D832F6000000000000000000000001F030200";
			generateAC = createGenerateACApdu(generateACData);
		}

		Log.i(TAG, LOG_PREFIX + "GenerateAC command : " + HexString.bytes2String(generateAC));

		responseData = tranceive(isoDep, 3,  generateAC);
		if(responseData==null){
			handler.obtainMessage(2, new TapData(2, "TRANSACTION REFUSED")).sendToTarget();
			return;
		}
		response  = new Response(responseData);
		Log.i(TAG, LOG_PREFIX + "GenerateAC response : " + response.toHex());

		if(response.getData()[0]==(byte)0x77){

			EmvTLV tlv = new TLV(responseData);

			EmvTag iad = new com.atosworldline.rd.payment.emv.datatype.tlv.Tag(HexString.parseHexString(Tags.ISSUER_APPLICATION_DATA));
			EmvTag sdad = new com.atosworldline.rd.payment.emv.datatype.tlv.Tag(HexString.parseHexString(Tags.SIGNED_DYNAMIC_APP_DATA));
			EmvTLV signedDynAppData = null;
			try {
				signedDynAppData = tlv.findSingleTag(sdad, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(signedDynAppData != null){
				Log.i(TAG, LOG_PREFIX + "signedDynAppData  : " + HexString.bytes2String(signedDynAppData.valueAsByteArray()));
			}

			/*
			Signed Data Format
1
Hex value '05'
b
Hash Algorithm Indicator
1
Identifies the hash algorithm used to produce the Hash Result 20
b
ICC Dynamic Data Length
1
Identifies the length LDD of the ICC Dynamic Data in bytes
b
ICC Dynamic Data
LDD
Dynamic data generated by and/or stored in the ICC
—
Pad Pattern
NIC – LDD – 25
(NIC – LDD – 25) padding bytes of value 'BB' 21
b
Terminal Dynamic Data
var.
Concatenation of the data elements specified by the DDOL

			 */
			String SHA256ALGO = "01";
			String dynDataToBeSigned = "05" + SHA256ALGO;



			TapData data = new TapData(3, "TRANSACTION ACCEPTED");
			data.card		= card;
			data.exp		= expiry;
			handler.obtainMessage(3, data).sendToTarget();
		}else{
			handler.obtainMessage(2, new TapData(2, "TRANSACTION REFUSED")).sendToTarget();
		}

		try {
			isoDep.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(activity.getApplicationContext(), notification);
			r.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Performs several call to the HCE
	 * @param isoDep
	 * @param attempt number of attempt
	 * @param command The command apdu
	 * @return
	 */
	private byte[] tranceive(IsoDep isoDep, int attempt, byte [] command){

		byte[] response 	= null;
		boolean notSended 	= true;
		int essai 			= 0;

		while(notSended && (essai <attempt)){
			try {
				essai++;
				response = isoDep.transceive(command);
				notSended = false;
			} catch (IOException e) {
				Log.w(TAG, LOG_PREFIX + "Transmission error [" + e + "]");
			}

		}

		if(response==null){
			handler.obtainMessage(2, new TapData(2, "ERROR READING CARD")).sendToTarget();
		}

		return response;

	}


	/**
	 * Creates the SELECT command APDU
	 * @param aid
	 * @return
	 */
	private byte[] createSelectAidApdu(byte[] aid) {

		byte[] apdu = new byte[6 + aid.length];

		System.arraycopy(SELECT_CLA_INS_P1_P2, 0, apdu, 0, SELECT_CLA_INS_P1_P2.length);
		apdu[4] = (byte)aid.length;
		System.arraycopy(aid, 0, apdu, 5, aid.length);
		apdu[apdu.length - 1] = 0;

		return apdu;
	}


	private byte[] createGenerateACApdu(String data){

		return  createGenerateACApdu(HexString.parseHexString(data));
	}


	private byte[] createGenerateACApdu(byte[] data) {

		byte[] apdu = new byte[6 + data.length];
		System.arraycopy(GENAC_CLA_INS_P1_P2, 0, apdu, 0, GENAC_CLA_INS_P1_P2.length);
		apdu[4] = (byte)data.length;
		System.arraycopy(data, 0, apdu, 5, data.length);

		apdu[apdu.length - 1] = 0;
		return apdu;
	}

	/**
	 * 10.2 Read Application Data : Return the APDU list to send to read the
	 * different files on the card the position of these files are given by the
	 * GPO CommandAPDU
	 */
	private  List<Command> getEmvAPDUReadRecord(String gpoResponseAPDU) {

		String stringAfl = annalyseGPO(gpoResponseAPDU);

		ArrayList<Command> commandAPDUList = new ArrayList<Command>();
		ArrayList<byte[]> sfiList = new ArrayList<byte[]>();
		String applicationFile;

		int index;
		int aflRecordNumber = stringAfl.length() / 8;
		for (int i = 0; i < aflRecordNumber; i++) {
			applicationFile = stringAfl.substring(i * 8, 8 + (i * 8));
			sfiList.add(HexString.parseHexString(applicationFile));
			int firstRecord = sfiList.get(i)[1];
			int lastRecord = sfiList.get(i)[2];

			// int numberOfRecordInvolvedInODAuth = sfiList.get(i)[3];
			for (index = firstRecord; index <= lastRecord; index++) {
				commandAPDUList.add(new Command(new byte[] { CLA_ISO7816,
						INS_READ_RECORD_0xB2, (byte) (index),
						(byte) (sfiList.get(i)[0] + 4), LE_0x00 }));
			}
		}
		return commandAPDUList;
	}


	/**
	 * Construct the DOL from extracted PDOL
	 * @param pdol
	 * @return
	 */
	private byte [] constructDOL(byte [] pdol){

		DOL pDol = new DOL(HexString.bytes2String(pdol));
		byte[] result = null;
		if(pdol.length==0){ // No PDOL
			result = new byte[] {(byte)0x83, (byte)0x00};

		} else{

			// Visa 9F6604 9F0206 9F0306 9F1A02 9505 5F2A02 9A03 9C01 9F3704 9F4E 14 9F2103
			String ttq = "33C04000";
			result = HexString.parseHexString("8338" + ttq +
					"00000000123200000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");

		}
		return result;
	}


	/**
	 *
	 * @param pdol
	 * @return
	 */
	private byte [] prepareGetProcessingOptionCommand( byte [] pdol){

		Log.i(TAG, "pdol.length : " + pdol.length);
		byte[] result = new byte[6 + pdol.length];

		System.arraycopy(GPO_CLA_INS_P1_P2, 0, result, 0, GPO_CLA_INS_P1_P2.length);
		result[4] = (byte)pdol.length;
		System.arraycopy(pdol, 0, result, 5, pdol.length);
		result[result.length - 1] = 0;

		return result;
	}


	/**
	 *
	 * @param responseAPDU
	 * @return
	 */
	private static String annalyseGPO(String responseAPDU) {

		Log.i(TAG, "responseAPDU : " + responseAPDU);
		//		    AIP aip;

		Integer template = Integer.parseInt(responseAPDU.substring(0, 2));
		Log.i(TAG,"Template : " + template);

		String stringAip = "";
		EmvTLV aipTlv;

		String stringAfl = "";
		EmvTLV aflTlv;
		EmvTLV responseTlv = new TLV(HexString.parseHexString(responseAPDU));

		switch (template) {
			case RESPONSE_MESSAGE_TEMPLATE_2:
				Log.i(TAG,"case 77: Response Message Template Format 2");

				aipTlv = responseTlv.findTag(
						new com.atosworldline.rd.payment.emv.datatype.tlv.Tag(HexString
								.parseHexString(APPLICATION_INTERCHANGE_PROFILE)),
						null);
				stringAip = HexString.bytes2String(aipTlv.valueAsByteArray());
				//		      aip = new AIP(stringAip);
				Log.i(TAG,"AIP : " + stringAip);

				aflTlv = responseTlv.findTag(
						new com.atosworldline.rd.payment.emv.datatype.tlv.Tag(HexString.parseHexString(APP_FILE_LOCATOR)), null);
				stringAfl = HexString.bytes2String(aflTlv.valueAsByteArray());
				Log.i(TAG,"aflTlv" + aflTlv.toString());
				break;

			case RESPONSE_MESSAGE_TEMPLATE_1:
				Log.i(TAG,"case 80: Response Message Template Format 1");
				String responseValue = HexString.bytes2String(responseTlv
						.valueAsByteArray());

				stringAip = responseValue.substring(0, 4);
				//		      aip = new AIP(stringAip);
				Log.i(TAG,"AIP : " + stringAip);

				stringAfl = responseValue.substring(4);
				Log.i(TAG,"AFL : " + stringAfl);
				break;
			default:
				break;
		}

		return stringAfl;
	}

	private byte [] getDate(){

		Calendar calendar = Calendar.getInstance();

		String val =
				formatDateByte(calendar.get(Calendar.YEAR)) +
						formatDateByte(calendar.get(Calendar.MONTH)+1) +
						formatDateByte(calendar.get(Calendar.DAY_OF_MONTH));
		return HexString.parseHexString(val);
	}

	private byte [] getTime(){

		Calendar calendar = Calendar.getInstance();
		String val =
				formatDateByte(calendar.get(Calendar.HOUR_OF_DAY)) +
						formatDateByte(calendar.get(Calendar.MINUTE)) +
						formatDateByte(calendar.get(Calendar.SECOND));

		return HexString.parseHexString(val);
	}


	private String formatDateByte(int val){

		String result = String.valueOf(val);
		if(result.length()==1){
			result = "0" + result;
		}

		else if(result.length()==4){
			result = result.substring(2);
		}

		return result;
	}

	/**
	 * Format exp date from YYmmDD to mm/YY
	 * @param date
	 * @return
	 */
	private String formatExpDate(String date){

		StringBuffer buf = new StringBuffer();
		buf.append(date.substring(2, 4));
		buf.append("/");
		buf.append(date.substring(0, 2));

		return buf.toString();

	}




}
