package com.hpe.isodep;

// Imports
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hpe.R;

public class CaptureTapActivity extends Activity  {
	
	
	public static String WAITING_MSG = "WAITING TAP";
	
	
	private String amount, phone;
	
	/* The log tag */
	private static final String TAG 				= "HostPosEmulation";
	private static final String LOG_PREFIX 			= "MAIN_ACTIVITY :";

	
	
	private Button cancelButton;
	private NfcAdapter nfcAdapter;
	private EditText finalMessageTextView;
	private TextView messageField;
	private ImageView logo, finalLogo;
	
	private TextView resumeStatus, resumeAmount, resumeCard, resumeExp;
	private EditText customerTel;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, LOG_PREFIX + "onCreate()");
		
		setContentView(R.layout.activity_capture_tap);
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);	
		
		// Pay button
		cancelButton = (Button) findViewById(R.id.tap_cancel);  		
		cancelButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				finish();
			}
		 });
		
		finalMessageTextView = (EditText) findViewById(R.id.final_msg); 
		logo = (ImageView) findViewById(R.id.logo); 
		finalLogo = (ImageView) findViewById(R.id.final_logo); 
	}
	

	/**
	 * 
	 */
	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, LOG_PREFIX + "onPause()");
		nfcAdapter.disableReaderMode(this);
	}
	

	/**
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, LOG_PREFIX + "onResume()");
		
		NfcManager manager = (NfcManager) this.getSystemService(Context.NFC_SERVICE);
		NfcAdapter adapter = manager.getDefaultAdapter();
		if (adapter != null && adapter.isEnabled()) {
		    // adapter exists and is enabled.
			Log.i(TAG, LOG_PREFIX + "adapter exists and is enabled.");
		}else if(adapter == null){
			Log.i(TAG, LOG_PREFIX + "adapter doesn't exist.");
		}else{
			Log.i(TAG, LOG_PREFIX + "adapter is not enabled.");
			Toast.makeText(getApplicationContext(), "Please activate NFC and press Back to return to the application!", Toast.LENGTH_LONG).show();
	        startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS ));
		}
		
	       // Check for available NFC Adapter
        PackageManager pm = getPackageManager();
        if(pm.hasSystemFeature(PackageManager.FEATURE_NFC) && NfcAdapter.getDefaultAdapter(this) != null) {
                Log.i(TAG, "NFC feature found");
        } else {
                Log.i(TAG, "NFC feature not found");
        }
        
		
		Bundle extras = getIntent().getExtras();
		Log.i(TAG, "onCreate:Extra : " + extras );
		amount = "0";
		if (extras != null) {
			amount = extras.getString("amount");
			Log.i(TAG, "onResume(): Amount:" + amount);
			amount = prepareAmount(amount);
		}
		
		phone = "";
		if (extras != null) {
			phone = extras.getString("phone");
			Log.i(TAG, "onResume(): Phone:" + phone);
		}
		
		EMVReaderCallback callback = new EMVReaderCallback(this, new NFCReaderEventHandler(this));
		nfcAdapter.enableReaderMode(this, callback, NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK | 
				NfcAdapter.FLAG_READER_NFC_A | 
				NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS, null);
		
		messageField = (TextView) findViewById(R.id.tap_status);
		messageField.setText(WAITING_MSG);
		
	}
	
	/**
	 * Return the amount in cents
	 * @return
	 */
	protected String getAmount(){
		
		Log.i(TAG, LOG_PREFIX + "GenerateAC AMOUNT 0  : " +  amount);
		
		String formatedAmount = amount.replace(".", "");
		Log.i(TAG, LOG_PREFIX + "GenerateAC AMOUNT 0  : " +  formatedAmount);
		return formatedAmount;
	}


	/**
	 * Update the message. 
	 * @param message
	 */
	public void updateMessage(TapData data, int what){
		
		
		messageField.setText(data.message);
		
		
		if(what==2){
			messageField.setTextColor(0xffa81212);
						
		} else if(what==3){
				messageField.setTextColor(0xff11700c);
			    
		} else if(what==5){			
			displayFinalMessage("PAYMENT ACCEPTED", amount, protectPAN(data.card), data.exp);

		}else{
			messageField.setTextColor(0xff000000);
		}

	}

	
	/**
	 * 
	 * @param amount
	 * @return
	 */
	private String prepareAmount(String amount){
		
		String result = new String(amount);
		result = result.replace(",", ".");
		if(!result.contains(".")){
			result = result + ".00";
		}
		
		if(result.indexOf(".") == result.length()-2){
			result = result + "0";
		}
		
		return result;
		
	}
	
	
	/**
	 * 
	 * @param title
	 * @param amount
	 * @param card
	 * @param exp
	 */
	private void displayFinalMessage(final String title, final String amount, final String card, final String exp){

		Log.i(TAG, LOG_PREFIX + "GenerateAC AMOUNT 0  : " +  amount);
		setContentView(R.layout.activity_resume);

		resumeStatus = (TextView) findViewById(R.id.resume_statusField);  		
		resumeAmount = (TextView) findViewById(R.id.resume_amountField);
		resumeCard = (TextView) findViewById(R.id.resume_cardField);
		resumeExp = (TextView) findViewById(R.id.resume_expField);
		
		resumeStatus.setText("PAYMENT ACCEPTED") ; 
		resumeAmount.setText(amount) ; 
		resumeCard.setText(card) ; 
		resumeExp.setText(exp) ; 
		
		customerTel =  (EditText) findViewById(R.id.customerTelField);
		customerTel.setText(phone) ; 
		
		// Exit button
		ImageButton exitButton = (ImageButton) findViewById(R.id.exit_But);  		
		exitButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {finish();}
		 });
		
		
		// Exit button
		ImageButton sendButton = (ImageButton) findViewById(R.id.send_but);  		
		sendButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				String smsMessage = "HPE POS Emulation\nPAYMENT ACCEPTED\n\n" +  
				 "  Amount : " + amount + " Eur\n" +
				 "  Card   : " + card + "\n" +
				 "  Expiry : " + exp + "\n";
				
				SmsManager smsManager = SmsManager.getDefault();
				String phone_ = customerTel.getText().toString();
				try{
					if(phone_!=null && phone_.length()>0){				
						smsManager.sendTextMessage(phone_, null, smsMessage, null, null);
					}
					displayToast("Message envoye au " + phone_, Toast.LENGTH_SHORT);
					
				}catch(Exception ex){
					Log.i(TAG, LOG_PREFIX + "Cannot send SMS [" +  ex + "]");
				}
			}
		 });

		
//		cancelButton.setVisibility(View.INVISIBLE);
//		messageField.setVisibility(View.INVISIBLE);
//		logo.setVisibility(View.INVISIBLE);
//		
//		String message = "<b>PAYMENT ACCEPTED</b><br>\n" +  
//		 "  Amount : " + amount + " Eur<br>\n" +
//		 "  Card   : " + card + "<br>\n" +
//		 "  Expiry : " + exp + "<br>\n";
//		
//		
//		String smsMessage = "HPE POS Emulation\nPAYMENT ACCEPTED\n\n" +  
//				 "  Amount : " + amount + " Eur\n" +
//				 "  Card   : " + card + "\n" +
//				 "  Expiry : " + exp + "\n";
//
//
//		finalMessageTextView.setText(Html.fromHtml(message));
//		finalMessageTextView.setVisibility(View.VISIBLE);
//		finalLogo.setVisibility(View.VISIBLE);
//		
//		GoBack myTask = new GoBack();
//		Timer myTimer = new Timer();
//		
//		SmsManager smsManager = SmsManager.getDefault();
//		
//		try{
//			if(phone!=null && phone.length()>0){				
//				smsManager.sendTextMessage(phone, null, smsMessage, null, null);
//			}
//		}catch(Exception ex){
//			Log.i(TAG, LOG_PREFIX + "Cannot send SMS [" +  ex + "]");
//		}
//		
//		myTimer.schedule(myTask, 5000L);
	}
	
	
	/**
	 * 
	 * 
	 * @param pan
	 * @return
	 */
	private String protectPAN(String pan){
		
		StringBuffer protectedPan = new StringBuffer();

		protectedPan.append(pan.substring(0, 4));
		protectedPan.append(" XX..XX ");
		protectedPan.append(pan.substring(pan.length()-4));

		return protectedPan.toString();
	}
	
	
	private class GoBack extends TimerTask{
	
		public void run() {
			finish();
	
		 }
	}


	/**
	 * 
	 * @param message
	 */
	private void displayToast(String message, int duration){
		
		Context context = getApplicationContext();
		
		Toast toast = Toast.makeText(context, message, duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

}
