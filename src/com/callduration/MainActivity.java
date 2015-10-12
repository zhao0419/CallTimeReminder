package com.callduration;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	CheckBox checkbox;
	static Boolean isChecked = false;
	private static final String TAG = MainActivity.class.getSimpleName();
	ProgressDialog updatingContactsDialog = null;
	public  Handler handler;
	private View view;
	public static final int ABOUT_DIALOG = 0;
	SharedPreferences proflieSharedPreference;


	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ABOUT_DIALOG:
			return new AboutDialog(this).create();
		}
		return null;
	}

	
	
	public static String getVersion(Context context) {
		String unknown = "Unknown";

		if (context == null) {
			return unknown;
		}

		try {
			unknown = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
			unknown = unknown
					+ "(r"
					+ context.getPackageManager().getPackageInfo(
							context.getPackageName(), 0).versionCode + ")";
		} catch (NameNotFoundException ex) {
		}

		return unknown;
	}
	
	
	
	void initData()
	{
		proflieSharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());		
		checkbox = (CheckBox)this.findViewById(R.id.switch_checkbox);
		Boolean isServiceActive = proflieSharedPreference.getBoolean("isServiceActive", false);
		if(isServiceActive)
		{
			checkbox.setChecked(true);	
		}
		else
		{
			checkbox.setChecked(false);
		}
		
		checkbox.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v)
			{
				CheckBox checkBox  = (CheckBox)v;
				
				NativeService.isServiceActive = checkBox.isChecked();
				proflieSharedPreference.edit().putBoolean("isServiceActive",NativeService.isServiceActive).commit();
			}
		});
		
		
		
		EditText startHourET = (EditText)this.findViewById(R.id.start_hour);
		
		Long startHour = proflieSharedPreference.getLong("startHour", 0);
		
		startHourET.setText(startHour.toString());
			
		startHourET.addTextChangedListener(new TextWatcher() {
			
			   public void afterTextChanged(Editable s) {
			    
				   String  text = s.toString();
				   
				   if(!text.isEmpty())
				   {
					   NativeService.startHour = Integer.parseInt(text);
					 
					   proflieSharedPreference.edit().putLong("startHour",Integer.parseInt(text)).commit();
				   }
				   Log.d(TAG,"afterTextChanged startHour text is"+text);
			   }

			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
				   
				   	
			   }

			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
				 
			   }

		});
			 	
		EditText startMinET = (EditText)this.findViewById(R.id.start_min);
		Long startMin = proflieSharedPreference.getLong("startMin", 0);
		startMinET.setText(startMin.toString());
	
		startMinET.addTextChangedListener(new TextWatcher() {
			
			   public void afterTextChanged(Editable s) {
			    
				   
				   
				   String  text = s.toString();
				   
				   if(!text.isEmpty())
				   {
					   NativeService.startMin = Integer.parseInt(text);
					   
					   
					   
					   
					   
					   proflieSharedPreference.edit().putLong("startMin",Integer.parseInt(text)).commit();
				   }
				   Log.d(TAG,"afterTextChanged startMin text is"+text);
			   }

			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
				   
				   	
			   }

			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
				 
			   }

		});
			 
	
		
		
		EditText startSecET = (EditText)this.findViewById(R.id.start_sec);
		Long startSec = proflieSharedPreference.getLong("startSec", 0);
		startSecET.setText(startSec.toString());
		
		startSecET.addTextChangedListener(new TextWatcher() {

			 

			   public void afterTextChanged(Editable s) {
			    
				   String  text = s.toString();
				   
				   if(!text.isEmpty())
				   {
					   NativeService.startSec = Integer.parseInt(text);
				
					   proflieSharedPreference.edit().putLong("startSec",Integer.parseInt(text)).commit();
				   }
				   Log.d(TAG,"afterTextChanged startSec text is"+text);
			   }

			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
				   
				   	
			   }

			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
				 
			   }

		});
		
		EditText intervalHourET = (EditText)this.findViewById(R.id.interval_hour);
		Long hourInterval = proflieSharedPreference.getLong("hourInterval", 0);
		intervalHourET.setText(hourInterval.toString());
		
		intervalHourET.addTextChangedListener(new TextWatcher() {

			 

			   public void afterTextChanged(Editable s) {
			    
				   String  text = s.toString();
				   
				   if(!text.isEmpty())
				   {
					   NativeService.hourInterval = Integer.parseInt(text);
					   NativeService.intervalTotalTime = (3600*NativeService.hourInterval+60*NativeService.minInterval+NativeService.secInterval)*1000;
					   proflieSharedPreference.edit().putLong("hourInterval",Integer.parseInt(text)).commit();
					   if(NativeService.intervalTotalTime == 0)
					   {
						   NativeService.intervalTotalTime = 1;
						   NativeService.secInterval = 1;
						   proflieSharedPreference.edit().putLong("secInterval",Integer.parseInt(text)).commit();
						   Toast.makeText(getApplicationContext(), "间隔时间不能为零",Toast.LENGTH_LONG).show();
						   
					   }
				   }
				   Log.d(TAG,"afterTextChanged hourInterval text is"+text);
			   }

			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
				   
				   	
			   }

			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
				 
			   }

		});
	
		
		EditText intervalMinET = (EditText)this.findViewById(R.id.interval_min);
		Long minInterval = proflieSharedPreference.getLong("minInterval", 0);
		intervalMinET.setText(minInterval.toString());
		
		
		intervalMinET.addTextChangedListener(new TextWatcher() {

			 

			   public void afterTextChanged(Editable s) {
			    
				   String  text = s.toString();
				   
				   if(!text.isEmpty())
				   {
					   NativeService.minInterval = Integer.parseInt(text);
					   NativeService.intervalTotalTime = (3600*NativeService.hourInterval+60*NativeService.minInterval+NativeService.secInterval)*1000;
					   proflieSharedPreference.edit().putLong("minInterval",Integer.parseInt(text)).commit();
					   if(NativeService.intervalTotalTime == 0)
					   {
						   NativeService.intervalTotalTime = 1;
						   NativeService.secInterval = 1;
						   proflieSharedPreference.edit().putLong("secInterval",Integer.parseInt(text)).commit();
						   Toast.makeText(getApplicationContext(), "间隔时间不能为零",Toast.LENGTH_LONG).show();
						   
					   }
				   }
				   Log.d(TAG,"afterTextChanged minInterval text is"+text);
			   }

			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
				   
				   	
			   }

			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
				 
			   }

		});
	
		
		EditText intervalSecET = (EditText)this.findViewById(R.id.interval_sec);
		Long secInterval = proflieSharedPreference.getLong("secInterval", 50);
		intervalSecET.setText(secInterval.toString());
		
	
		
		intervalSecET.addTextChangedListener(new TextWatcher() {

			 

			   public void afterTextChanged(Editable s) {
			    
				   String  text = s.toString();
				   
				   if(!text.isEmpty())
				   {
					   NativeService.secInterval = Integer.parseInt(text);
					   NativeService.intervalTotalTime = (3600*NativeService.hourInterval+60*NativeService.minInterval+NativeService.secInterval)*1000;
					   
					   proflieSharedPreference.edit().putLong("secInterval",Integer.parseInt(text)).commit();
					   
					   
					   if(NativeService.intervalTotalTime == 0)
					   {
						   NativeService.intervalTotalTime = 1;
						   NativeService.secInterval = 1;
						   proflieSharedPreference.edit().putLong("secInterval",Integer.parseInt(text)).commit();
						   Toast.makeText(getApplicationContext(), "间隔时间不能为零",Toast.LENGTH_LONG).show();
						   
					   }
					   
				   }
				   Log.d(TAG,"afterTextChanged secInterval text is"+text);
			   }

			   public void beforeTextChanged(CharSequence s, int start, int count,
			     int after) {
				   
				   	
			   }

			   public void onTextChanged(CharSequence s, int start, int before,
			     int count) {
				 
			   }

		});
	
		
		
		
		
		
	}// end of initdata
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		
		startService(new Intent(this, NativeService.class));
		initData();
		
		ImageButton imageButton = (ImageButton)this.findViewById(R.id.sync_tab_button);
		imageButton.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				MainActivity.this.showDialog(ABOUT_DIALOG);	
			}
		});
		
		
	}// end of OnCreate
	
	protected void onResume() {
		super.onResume();
		
		TextView alertTextView = (TextView)this.findViewById(R.id.virbrator_reminder);
		
		Boolean isVibratorSupported = proflieSharedPreference.getBoolean("isVibratorSupported", true);
		
		Log.e(TAG,"isVibratorSupported is "+isVibratorSupported);
		
		if(isVibratorSupported == false)
		{
			alertTextView.setVisibility(View.VISIBLE);
		}
	}// end of onResume
   

}
