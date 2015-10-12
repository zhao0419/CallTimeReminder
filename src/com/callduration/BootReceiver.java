package com.callduration;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	/* the intent source */
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	
	private static final String TAG = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) 
		{
				Intent serviceIntent = new Intent(context, NativeService.class);
				ComponentName startService = context.startService(serviceIntent);
				Log.w(TAG, "Component " + startService);			
		}

	}

}