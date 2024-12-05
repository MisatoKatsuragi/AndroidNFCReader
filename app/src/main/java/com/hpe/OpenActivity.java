package com.hpe;

//Imports
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * 
 *
 */
@SuppressLint("HandlerLeak")
public class OpenActivity extends Activity {
	
	public static final String TAG			= "HostPosEmulation";
	
	private static final int STOPSPLASH = 0;
	private static final long SPLASHTIME = 4500;	
	private final Handler splashHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			Log.i(TAG, "handleMessage(Message msg)");

            if (msg.what == STOPSPLASH) {//Switch from SplashScreen to Activity
                Intent intent = new Intent(OpenActivity.this, PayActivity.class);
                startActivity(intent);
                OpenActivity.this.finish();
            }
			super.handleMessage(msg);
	
		}
	
	};

	 
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Log.i(TAG, "SplashActivity:onCreate()");
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 
		setContentView(R.layout.activity_open);

		View view = findViewById(android.R.id.content);
		Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
		view.startAnimation(animationFadeIn);
		
       	Message msg = new Message();
    	msg.what = STOPSPLASH;
    	Log.i(TAG, "Waiting for message");
    	splashHandler.sendMessageDelayed(msg, SPLASHTIME);

	
	}
	
}