package com.callduration;
//
// 
//

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
// 
//
// 
//
public class AboutDialog  {
    private Activity activity;

    private TextView textView;
  ;
  
    public AboutDialog(Activity activity){
		this.activity = activity;
	}
  
	public Dialog create() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);			
		builder.setTitle(activity.getString(R.string.menu_about)).setIcon(R.drawable.phone).setCancelable(true);
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.about_dialog, null);

		textView = (TextView) layout.findViewById( R.id.Description );
		String newDescription = activity.getString(R.string.about).replace("\\n", "\n").replace("${VERSION}", MainActivity.getVersion(activity));
		textView.setText(newDescription);
		
		
		
		builder.setView(layout);
		return builder.create();
	}
	

}
