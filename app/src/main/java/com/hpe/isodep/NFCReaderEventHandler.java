package com.hpe.isodep;

// Imports
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

/**
 * Received message from EMVReaderCallback and update activity display.
 * @author FR18632
 *
 */
public class NFCReaderEventHandler extends Handler{
	
	private CaptureTapActivity activity;

	/**
	 * Constructor
	 * @param activity
	 */
	public NFCReaderEventHandler(CaptureTapActivity activity){
		this.activity 	= activity;
	}
	
	/**
	 * Received message.
	 */
	@Override
	public void handleMessage(Message msg) {
		
		TapData data = (TapData)msg.obj;
		activity.updateMessage(data, msg.what);
	
		if(msg.what==2){
			CleanTask myTask = new CleanTask();
			Timer myTimer = new Timer();
			myTimer.schedule(myTask, 2000L);			
		}
		
		if(msg.what==3){
			ResultTask myTask = new ResultTask(data);
			Timer myTimer = new Timer();
			myTimer.schedule(myTask, 1000L);
		}
	}
	
	
	private class CleanTask extends TimerTask {
		  public void run() {			
			  obtainMessage(1,  new TapData(1, CaptureTapActivity.WAITING_MSG)).sendToTarget();
		  }
	}
	
	private class ResultTask extends TimerTask {
		
		  private TapData data;
		  public ResultTask(TapData data){
			  this.data = data;
		  }
		  
		  public void run() {			
			  obtainMessage(5,  data).sendToTarget();
		  }
	}


}
