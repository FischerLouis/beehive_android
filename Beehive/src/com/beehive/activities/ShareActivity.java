package com.beehive.activities;

import java.util.Calendar;
import java.util.Date;

import com.beehive.R;
import com.beehive.tools.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShareActivity extends Activity implements OnTouchListener {

	private int zoneId;
	private String title;
	private String subtitle;
	private String occupancy;
	private String timeToGo;
	private Bitmap bm;
	private TextView hourView;
	private TextView minuteView;
	private TextView ampmView;
	private Activity activity = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		//Retrieve data from intent
		Intent intent = getIntent();
		zoneId = intent.getIntExtra("ID", 0);
		title = intent.getStringExtra("TITLE");
		subtitle = intent.getStringExtra("SUBTITLE");
		occupancy = intent.getStringExtra("OCCUPANCY");
		timeToGo = intent.getStringExtra("TIMETOGO");

		Bundle extras = intent.getExtras();
		bm = extras.getParcelable(Constants.KEY_PIC);
		//Retrieve views
		TextView titleView = (TextView)findViewById(R.id.title);
		TextView subtitleView = (TextView)findViewById(R.id.subtitle);
		TextView occupancyView = (TextView)findViewById(R.id.occupancy);
		TextView timeToGoView = (TextView)findViewById(R.id.time_to_go);
		ImageView pic = (ImageView)findViewById(R.id.pics);
		LinearLayout sendLayout = (LinearLayout)findViewById(R.id.footer_queue);
		hourView = (TextView)findViewById(R.id.time_hour);
		minuteView = (TextView)findViewById(R.id.time_minutes);
		ampmView = (TextView)findViewById(R.id.time_ampm);
		pic.setImageBitmap(bm);
		//Setting up data
		titleView.setText(title);
		subtitleView.setText(subtitle);
		if(occupancy != null){
			occupancyView.setText(occupancy);
			setColorTitle(titleView, occupancy);
		}
		if(timeToGo != null)
			timeToGoView.setText(timeToGo);
		//LISTENER
		sendLayout.setOnTouchListener(this);
		hourView.setOnTouchListener(this);
		minuteView.setOnTouchListener(this);
		ampmView.setOnTouchListener(this);
		//TIME
		setTime();
	}

	@Override
	public void onBackPressed() {
		setIntent();
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			setIntent(); 
			finish();
		}
		return true;
	}

	private void setIntent(){
		Intent intent = new Intent();
		//PUT VALUES
		intent.putExtra("ID", zoneId);
		intent.putExtra("TITLE", title);
		intent.putExtra("SUBTITLE", subtitle);
		intent.putExtra("OCCUPANCY", occupancy);
		intent.putExtra("TIMETOGO", timeToGo);
		Bundle picExtra = new Bundle();
		picExtra.putParcelable(Constants.KEY_PIC, bm);
		intent.putExtras(picExtra);	
	}

	private void setTime(){
		//INIT CALENDAR
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		//GET TIME
		int hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);
		int ampm = calendar.get(Calendar.AM_PM);
		//SET TIME
		hourView.setText(""+hour);
		minuteView.setText(""+minute);
		if(ampm == 0)
			ampmView.setText("AM");
		else
			ampmView.setText("PM");
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()){
		case R.id.footer_queue:
			if(event.getAction() == MotionEvent.ACTION_UP){
				Toast.makeText(this, "Thank you!", Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
		case R.id.time_hour:
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				hourView.setBackgroundColor(getResources().getColor(R.color.orange));
				hourView.setTextColor(Color.WHITE);
				break;
			case MotionEvent.ACTION_UP:
				hourView.setBackgroundColor(Color.TRANSPARENT);
				hourView.setTextColor(getResources().getColor(R.color.orange));
				updateHour();
				break;
			}
			break;
		case R.id.time_minutes:
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				minuteView.setBackgroundColor(getResources().getColor(R.color.orange));
				minuteView.setTextColor(Color.WHITE);
				break;
			case MotionEvent.ACTION_UP:
				minuteView.setBackgroundColor(Color.TRANSPARENT);
				minuteView.setTextColor(getResources().getColor(R.color.orange));
				updateMinute();
				break;
			}
			break;
		case R.id.time_ampm:
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				ampmView.setBackgroundColor(getResources().getColor(R.color.orange));
				ampmView.setTextColor(Color.WHITE);
				break;
			case MotionEvent.ACTION_UP:
				ampmView.setBackgroundColor(Color.TRANSPARENT);
				ampmView.setTextColor(getResources().getColor(R.color.orange));
				updateAmpm();
				break;
			}
			break;
		}
		return true;
	}

	private void updateHour(){
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);

		new AlertDialog.Builder(this)
		.setTitle("Change hours")
		.setView(input)
		.setPositiveButton("Set", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				int result = 0;
				try{
					result = Integer.parseInt(input.getText().toString().replaceAll("\\s+",""));
				}catch(NumberFormatException e){
				}
				if(result >=1 && result <=12)
					hourView.setText(""+result);
				else
					Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Do nothing.
			}
		}).show();
	}

	private void updateMinute(){
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);

		new AlertDialog.Builder(this)
		.setTitle("Change minutes")
		.setView(input)
		.setPositiveButton("Set", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				int result = 0;
				try{
					result = Integer.parseInt(input.getText().toString().replaceAll("\\s+",""));
				}catch(NumberFormatException e){
				}
				if(result >=0 && result <=59)
					minuteView.setText(""+result);
				else
					Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Do nothing.
			}
		}).show();
	}

	private void updateAmpm(){
		if(ampmView.getText().equals("PM"))
			ampmView.setText("AM");
		else
			ampmView.setText("PM");
	}

	private void setColorTitle(TextView title, String occupancy){
		int percentageChar = occupancy.indexOf("%");
		if(percentageChar>0){
			int valueOcc = Integer.parseInt(occupancy.substring(0, percentageChar));
			if(valueOcc < Constants.OCCUPANCY_THRESHOLD_LOW){
				title.setTextColor(getResources().getColor(R.color.green));
			}
			else if(valueOcc < Constants.OCCUPANCY_THRESHOLD_HIGH){
				title.setTextColor(getResources().getColor(R.color.orange));
			}
			else{
				title.setTextColor(getResources().getColor(R.color.red));
			}
		}
	}
}
