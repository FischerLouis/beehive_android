package com.beehive.activities;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.beehive.R;
import com.beehive.application.BeehiveApplication;
import com.beehive.tools.Constants;
import com.beehive.tools.VolleySingleton;
import com.google.android.gms.analytics.GoogleAnalytics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ShareActivity extends Activity implements OnTouchListener, OnCheckedChangeListener {

	private int zoneId;
	private String title;
	private String occupancy;
	private String timeToGo;
	private Bitmap bm;
	private TextView totalContributionView;
	private TextView rankView;
	private RadioGroup radioGroup;
	private int contribution = 0;
	private int totalContribution = 0;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		//Get a Tracker (should auto-report)
		((BeehiveApplication) getApplication()).getTracker(BeehiveApplication.TrackerName.APP_TRACKER);
		// DATA FROM INTENT
		Intent intent = getIntent();
		zoneId = intent.getIntExtra("ID", 0);
		title = intent.getStringExtra("TITLE");
		occupancy = intent.getStringExtra("OCCUPANCY");
		timeToGo = intent.getStringExtra("TIMETOGO");
		String queue = intent.getStringExtra("QUEUE");
		int colorTitle = intent.getIntExtra("TITLECOLOR",R.color.black);

		Bundle extras = intent.getExtras();
		bm = extras.getParcelable(Constants.KEY_PIC);
		//Retrieve views
		TextView titleView = (TextView)findViewById(R.id.title);
		TextView occupancyView = (TextView)findViewById(R.id.occupancy);
		TextView timeToGoView = (TextView)findViewById(R.id.time_to_go);
		TextView queueView = (TextView)findViewById(R.id.queue);
		ImageView pic = (ImageView)findViewById(R.id.pics);
		LinearLayout sendLayout = (LinearLayout)findViewById(R.id.footer_queue);
		totalContributionView = (TextView)findViewById(R.id.total_contribution);
		rankView = (TextView)findViewById(R.id.contribution_rank);
		radioGroup = (RadioGroup) findViewById(R.id.radio_queue);
		pic.setImageBitmap(bm);
		//Setting up data
		titleView.setText(title);
		titleView.setTextColor(colorTitle);
		if(occupancy != null)
			occupancyView.setText(occupancy);
		if(timeToGo != null)
			timeToGoView.setText(timeToGo);
		if(queue != null)
			queueView.setText(queue);
		//LISTENER
		sendLayout.setOnTouchListener(this);
		radioGroup.setOnCheckedChangeListener(this);
		//PREFERENCES
		prefs = this.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
		//SET TIME VIEW
		setContribution();
	}

	@Override
	public void onStart() {
		super.onStart();
		//Get an Analytics tracker to report app starts & uncaught exceptions etc.
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		//Stop the analytics tracking
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: 
			finish();
		}
		return true;
	}

	private void setContribution(){
		//GET TOTAL CONTRIBUTION (if exist)
		if(prefs.contains("totalContribution")){
			totalContribution = prefs.getInt("totalContribution", 0);
			totalContributionView.setText(""+totalContribution);

			if(totalContribution < Constants.RANK_2_THRESHOLD){
				rankView.setText(Constants.RANK_1_LABEL);
			}
			else if(totalContribution < Constants.RANK_3_THRESHOLD){
				rankView.setText(Constants.RANK_2_LABEL);
			}
			else if(totalContribution < Constants.RANK_4_THRESHOLD){
				rankView.setText(Constants.RANK_3_LABEL);
			}
			else if(totalContribution < Constants.RANK_5_THRESHOLD){
				rankView.setText(Constants.RANK_4_LABEL);
			}
			else{
				rankView.setText(Constants.RANK_5_LABEL);
			}
		}
		else{
			prefs.edit().putInt("totalContribution", 0).commit();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(v.getId()){
		case R.id.footer_queue:
			if(event.getAction() == MotionEvent.ACTION_UP){
				prefs.edit().putInt("totalContribution", totalContribution+10).commit();
				sendData();
				Toast.makeText(this, "BeeHive thanks you! +10pts", Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
		}
		return true;
	}

	private void sendData(){
		RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
		StringRequest req = new StringRequest(Request.Method.POST,Constants.URL_QUEUE, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				//TODO
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				//TODO
			}
		}){
			@Override
			protected Map<String,String> getParams(){
				Map<String, String> params = new HashMap<String, String>();
				params.put("id_location", ""+zoneId);
				params.put("reported", ""+contribution);

				return params;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String,String> params = new HashMap<String, String>();
				params.put("Content-Type","application/x-www-form-urlencoded");
				return params;
			}
		};
		queue.add(req);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId){
		case R.id.queue1:
			contribution = 0;
			break;
		case R.id.queue2:
			contribution = 1;
			break;
		case R.id.queue3:
			contribution = 2;
			break;
		case R.id.queue4:
			contribution = 3;
			break;
		}
	}
}
