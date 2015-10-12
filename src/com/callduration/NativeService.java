package com.callduration;



import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;



public class NativeService extends Service 
{



	public static boolean isServiceActive;
	public static long startHour;
	public static long startMin;
	public static long startSec;
	public static long hourInterval;
	public static long minInterval;
	public static long secInterval;
	public static long startTotalTime;
	public static long intervalTotalTime;

	
	private static SharedPreferences proflieSharedPreference;
	public static boolean isTaskRunning;
	public static boolean isVibratorSupported;
	public static Vibrator mVibrator;
	private final static String TAG = NativeService.class.getCanonicalName();
	
	
	private BroadcastReceiver mBroadcastReceiver;
	private Timer intervalTimer = null;
	Time time;
	
	
	public enum CallStatus {
		  IDLE , RINGING, OFFHook  ;
	}
	
	private CallStatus callStatus = CallStatus.IDLE;
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(TAG, "onBind()");
		return null;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind()");
		return super.onUnbind(intent);
	}
	
	//init time value here
	public void initTime()
	{
		proflieSharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		startHour = proflieSharedPreference.getLong("startHour", 0);
		startMin = proflieSharedPreference.getLong("startMin", 0);
		startSec = proflieSharedPreference.getLong("startSec", 0);
		hourInterval = proflieSharedPreference.getLong("hourInterval", 0);
		minInterval = proflieSharedPreference.getLong("minInterval", 0);
		secInterval = proflieSharedPreference.getLong("secInterval", 50);
		isServiceActive = proflieSharedPreference.getBoolean("isServiceActive", false);
		isVibratorSupported = proflieSharedPreference.getBoolean("isVibratorSupported", true);
		time = new Time(Time.getCurrentTimezone());
		isTaskRunning = false;
		mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		startTotalTime = (3600*startHour+60*startMin+startSec)*1000;
		intervalTotalTime = (3600*hourInterval+60*minInterval+secInterval)*1000;
	}
	
	
	private TimerTask intervalTimerTask = null;
	private Handler mHandler = null;
	private Runnable r = null;
	
	
	
	public static void Virbrate()
	{
	
		 if(mVibrator!= null)
		 {
			 Log.d(TAG,"mVibrator not null");
			 
			 if (android.os.Build.VERSION.SDK_INT < 11) 
			 {
				//no need to check hasVirator , I am not sure if this is OK
				 Log.d(TAG,"android version is "+android.os.Build.VERSION.SDK_INT+" below 11");
				 mVibrator.vibrate(100);
				 
				 if(isVibratorSupported == false)
				 {
					 isVibratorSupported = true;
					 proflieSharedPreference.edit().putBoolean("isVibratorSupported",NativeService.isVibratorSupported).commit();
				 }
			 }
			 else
			 {
				 Log.d(TAG,"android version is "+android.os.Build.VERSION.SDK_INT+" above 11");
				 
				 if(mVibrator.hasVibrator())
				 {
					 mVibrator.vibrate(100);
					 
					 if(isVibratorSupported == false)
					 {
						 isVibratorSupported = true;
						 proflieSharedPreference.edit().putBoolean("isVibratorSupported",true).commit();
					 }
				 }
				 else
				 {
					 if(isVibratorSupported == true)
					 {
						 isVibratorSupported = false;
						 proflieSharedPreference.edit().putBoolean("isVibratorSupported",NativeService.isVibratorSupported).commit();
					 }
						
				 }
				 
			 }
			
		 
		 
		 }
		 else
		 {
			 if(isVibratorSupported == true)
			 {	
				 isVibratorSupported = false;
				 proflieSharedPreference.edit().putBoolean("isVibratorSupported",NativeService.isVibratorSupported).commit();
			 }
			
		 }
		
		
	}
	
	
	@Override
	public void onStart(Intent intent, int startId) 
	{
		super.onStart(intent, startId);
		Log.d(TAG, "onStart()");
		initTime();
		
		
		
		mBroadcastReceiver = new BroadcastReceiver()
		{
			
			@Override
		    public void onReceive(Context arg0, Intent intent) 
			{
		        String action = intent.getAction();
		        Log.d(TAG,"onReceive action is "+action);
		        if(action.equalsIgnoreCase("android.intent.action.PHONE_STATE"))
		        {
		            //pick up the call
		        	
		            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals("OFFHOOK"))
		            {
		            
		            	
		            		Log.d(TAG,"incoming Call");
		            		if(intervalTimer == null)
		            		{
		            			intervalTimer = new Timer();
		            		}
		            	
		            		mHandler = new Handler();
		            	
		            		if((startHour == 0) &&(startMin == 0)&&(startSec == 0))
		            		{
		            			Log.i(TAG,"startTime is 0");
		                	
		            			if (intervalTimerTask == null) {  
		                		 intervalTimerTask = new TimerTask() {  
			                         @Override  
			                         public void run() {  
			                        	 
			                        	 if(isServiceActive == true)
			                        	 {
			                        		 time.setToNow();
			                        		 Log.d(TAG,"interval timer activate time is "+time.hour+"minute is "+time.minute+"second is "+time.second); 
			                        		 Virbrate();
			                        	 }
			                        	 else
			                        	 {
			                        		 releaseTimer();
			                        	 }
			                         }  
			                     };  
			                 }  
			           
		                	if(isTaskRunning == false)
		                	{
		                		intervalTimer.schedule(intervalTimerTask,(3600*hourInterval+60*minInterval+secInterval)*1000, (3600*hourInterval+60*minInterval+secInterval)*1000);
		                		isTaskRunning = true;
		                	}
		                }
		                else
		                {
		                	 if (intervalTimerTask == null) {  
		                		 intervalTimerTask = new TimerTask() {  
			                         @Override  
			                         public void run() {  
			                         	 if(isServiceActive == true)
			                        	 {
			                         		time.setToNow();
			                         		Log.d(TAG,"interval timer activate2 time is "+time.hour+"minute is "+time.minute+"second is "+time.second); 
			                         		 Virbrate();
			                        	 }
			                        	 else
			                        	 {
			                        		 releaseTimer();
			                        	 }
			                         }  
			                     };  
			                 }  
		                	
		                	 if(callStatus == CallStatus.RINGING)
				             {
				              
		                		 startTotalTime = (3600*startHour+60*startMin+startSec)*1000;
		                	
				             }
		                	 else if(callStatus == CallStatus.IDLE)
		                	 {
		                		 // for outgoing call delay 5 second
		                		 startTotalTime = (3600*startHour+60*startMin+startSec)*1000 + 5000;
		                		 
		                	 }
		                	 
		                	 mHandler.postDelayed(r = new Runnable() {

		            	   
		            	            public void run() {
		            	            	 
		            	            	Log.i(TAG,"mHandler runner enters");
				                 		if(intervalTimer != null)
				                 		{
				                 			time.setToNow();
				                 			Log.d(TAG,"isTaskRunning is "+isTaskRunning);
				                 			if(isTaskRunning == false)
				                 			{
				                 				Log.d(TAG,"mHandler run time is "+time.hour+"minute is "+time.minute+"second is "+time.second);
				                 				intervalTimer.schedule(intervalTimerTask, NativeService.intervalTotalTime, NativeService.intervalTotalTime);
				                 				isTaskRunning = true;
				                 			}
				                 		}
				                 			
		            	            }
		            	        }, startTotalTime); // one second
		                	 
		                	
		               
		                	
		                	
		                	}  //end of else
		 
		            	
		            	
		            
		            }//end of OFFHOOK
		            
		            //End of a call
		            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals("IDLE")) 
		            {
		            	Log.d(TAG,"Call ended");
		            	callStatus = CallStatus.IDLE;
		            	releaseTimer();
		            	if(mHandler != null)
		            	{
		            		mHandler.removeCallbacks(r);
		            	}
		            	r = null;
		            	mHandler = null;
		            	
		            }    
		            
		            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) 
		            {
		            	Log.d(TAG,"Call Ringing");
		            	callStatus = CallStatus.RINGING;
		            	
		            }
		            
		            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals("RINGING")) 
		            {
		            	Log.d(TAG,"Call Ringing2");
		            	callStatus = CallStatus.RINGING;
		            	
		            }
		            
		            
		        }   
		    }//end of onRecvie
		};//end of mBroadcastReceiver
		
			final IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("android.intent.action.PHONE_STATE");
			registerReceiver(mBroadcastReceiver, intentFilter);

	} //end of onstart
	


	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy()");
		super.onDestroy();
		releaseTimer();
		if(mBroadcastReceiver != null)
		{
			unregisterReceiver(mBroadcastReceiver);
		}
	
	}
	
	public void releaseTimer()
	{
		if(intervalTimer != null)
    	{
    		intervalTimer.cancel();
    		intervalTimer = null;
    		
    	}
    	if(intervalTimerTask != null)
    	{
    		intervalTimerTask.cancel();
    		intervalTimerTask = null;
    		
    		
    	}
    	
    	isTaskRunning = false;
		
	}

	
}//end of class
