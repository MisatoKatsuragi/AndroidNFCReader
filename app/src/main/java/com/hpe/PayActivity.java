package com.hpe;

// Imports
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hpe.isodep.CaptureTapActivity;

/**
 *
 *
 */
public class PayActivity extends Activity  implements Callback, TextWatcher {
	
	
	/* The log tag */
	private static final String TAG 				= "HostPosEmulation";
	private static final String LOG_PREFIX 			= "HPE_PAY:";
	
	
	
	public static final int TPM_TASK	= 123;
	
	// Graphical components
	private TextView amountField, labelField, mobileField, emailField;
	private ImageButton payButton;

	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	Log.i(TAG,  LOG_PREFIX + "onCreate()");
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        	

  		amountField = (TextView) findViewById(R.id.amountField);
  		labelField 	= (TextView) findViewById(R.id.merchantRefField);
  		mobileField = (TextView) findViewById(R.id.customerTelField);
  		emailField 	= (TextView) findViewById(R.id.customerEmailField);
  		
  		amountField.addTextChangedListener(this);
  		labelField.addTextChangedListener(this);
  		mobileField.addTextChangedListener(this);
  		emailField.addTextChangedListener(this);
  		
  		
  		// Pay button
  		payButton = (ImageButton) findViewById(R.id.pay_sendBut);  		
  		payButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {payButtonClicked(view);}
		 });
  		
  		

       
	}

    @Override
    public void onResume() {
    	
    	Log.v(TAG, LOG_PREFIX + "onResume()");
		super.onResume();
    	 //managePayButtonStatus();
    }

    
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	
    /**
     * Hide the keyboard when screen is touch
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
 
    	InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        mgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    	return super.onTouchEvent(event);
    }
	
    
	/**
	 * Manage the click on the pay button
	 * @param v the View attribute
	 */
	private void payButtonClicked(View v) {
		
		Log.i(TAG, LOG_PREFIX + "payButtonClicked() : " +  amountField.getText().toString());
		
		String amount =  amountField.getText().toString();
		if(amount==null || amount.length()<1){
			displayToast("Montant invalide !", Toast.LENGTH_SHORT);
		}else{
			Intent captureItent = new Intent(getApplicationContext(), CaptureTapActivity.class);
			captureItent.putExtra("amount", amountField.getText().toString());
			captureItent.putExtra("phone", mobileField.getText().toString());
			startActivityForResult(captureItent, 1);
		}	

	}
	
	
	/**
	 *  
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		
		Log.i(TAG, "Back from activity " + requestCode + "  " + resultCode);
		switch(requestCode){
						
			case 1: // Return for tap
				Log.i(TAG, "Back from payment");
		 		amountField.setText("");
		  		labelField.setText("");
		  		mobileField.setText("");
		  		emailField.setText("");
		  		amountField.requestFocus();
		 
				break;
			    
		}
	}	
	
	
	/**
	 * Manage the Pay button state
	 */
	private void managePayButtonStatus(){
		
		String amount = amountField.getText().toString();		
		//Log.v(TAG, LOG_PREFIX + "managePayButtonStatus(): Amount = " + amount);	
		
		if(amount.equals("")){
			payButton.setEnabled(false);			
			payButton.setBackgroundColor(0x77c0c0c0);
		} else {
			payButton.setEnabled(true);
			payButton.setBackgroundColor(0xff33b5e5);			
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
