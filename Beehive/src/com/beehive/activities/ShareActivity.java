package com.beehive.activities;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.beehive.R;
import com.beehive.tools.Constants;
import com.beehive.tools.VolleySingleton;

import android.app.Activity;
import android.content.Intent;
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
	private String subtitle;
	private String occupancy;
	private String timeToGo;
	private Bitmap bm;
	private TextView hourView;
	private TextView minuteView;
	private TextView ampmView;
	private RadioGroup radioGroup;
	private int contribution = 0;
	private Calendar calendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		// DATA FROM INTENT
		Intent intent = getIntent();
		zoneId = intent.getIntExtra("ID", 0);
		title = intent.getStringExtra("TITLE");
		subtitle = intent.getStringExtra("SUBTITLE");
		occupancy = intent.getStringExtra("OCCUPANCY");
		timeToGo = intent.getStringExtra("TIMETOGO");
		String queue = intent.getStringExtra("QUEUE");
		int colorTitle = intent.getIntExtra("TITLECOLOR",R.color.black);

		Bundle extras = intent.getExtras();
		bm = extras.getParcelable(Constants.KEY_PIC);
		//Retrieve views
		TextView titleView = (TextView)findViewById(R.id.title);
		TextView subtitleView = (TextView)findViewById(R.id.subtitle);
		TextView occupancyView = (TextView)findViewById(R.id.occupancy);
		TextView timeToGoView = (TextView)findViewById(R.id.time_to_go);
		TextView queueView = (TextView)findViewById(R.id.queue);
		ImageView pic = (ImageView)findViewById(R.id.pics);
		LinearLayout sendLayout = (LinearLayout)findViewById(R.id.footer_queue);
		hourView = (TextView)findViewById(R.id.time_hour);
		minuteView = (TextView)findViewById(R.id.time_minutes);
		ampmView = (TextView)findViewById(R.id.time_ampm);
		radioGroup = (RadioGroup) findViewById(R.id.radio_queue);
		pic.setImageBitmap(bm);
		//Setting up data
		titleView.setText(title);
		titleView.setTextColor(colorTitle);
		subtitleView.setText(subtitle);
		if(occupancy != null)
			occupancyView.setText(occupancy);
		if(timeToGo != null)
			timeToGoView.setText(timeToGo);
		if(queue != null)
			queueView.setText(queue);
		//LISTENER
		sendLayout.setOnTouchListener(this);
		radioGroup.setOnCheckedChangeListener(this);
		//INIT TIME
		Date now = new Date();
		calendar = Calendar.getInstance();
		calendar.setTime(now);
		//SET TIME VIEW
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
				sendData();
				Toast.makeText(this, "BeeHive thanks you! +1", Toast.LENGTH_SHORT).show();
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
