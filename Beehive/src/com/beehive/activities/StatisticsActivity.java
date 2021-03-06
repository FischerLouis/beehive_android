package com.beehive.activities;

import java.util.Calendar;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.beehive.R;
import com.beehive.application.BeehiveApplication;
import com.beehive.tools.Constants;
import com.beehive.tools.VolleySingleton;
import com.google.android.gms.analytics.GoogleAnalytics;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class StatisticsActivity extends Activity {

	private HorizontalScrollView hsv;
	private RequestQueue queue;
	private int zoneId;
	private int dayOfWeek;
	private GraphicalView weekChart;
	private ImageLoader mImageLoader;
	private ProgressBar weeklyLoading;
	private ProgressBar dailyLoading;
	private boolean showSpinners;
	private int width = 600; // DEFAULT VALUE

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		//Get a Tracker (should auto-report)
		((BeehiveApplication) getApplication()).getTracker(BeehiveApplication.TrackerName.APP_TRACKER);
		//SET IMAGE LOADER
		mImageLoader = VolleySingleton.getInstance().getImageLoader();
		//DATA FROM INTENT
		Intent intent = getIntent();
		zoneId = intent.getIntExtra("ID", 0);
		String title = intent.getStringExtra("TITLE");
		String occupancy = intent.getStringExtra("OCCUPANCY");
		String timeToGo = intent.getStringExtra("TIMETOGO");
		String queue = intent.getStringExtra("QUEUE");
		int colorTitle = intent.getIntExtra("TITLECOLOR",R.color.black);
		String urlPic = intent.getStringExtra("URLPIC");
		// VIEWS
		TextView titleView = (TextView)findViewById(R.id.title);
		TextView occupancyView = (TextView)findViewById(R.id.occupancy);
		TextView timeToGoView = (TextView)findViewById(R.id.time_to_go);
		TextView queueView = (TextView)findViewById(R.id.queue);
		ImageView pic = (ImageView)findViewById(R.id.pics);
		weeklyLoading = (ProgressBar)findViewById(R.id.weekly_progress);
		dailyLoading = (ProgressBar)findViewById(R.id.daily_progress);
		hsv = (HorizontalScrollView) findViewById(R.id.hsw);
		//SCREEN WIDTH
		width = getScreenWidth();
		//Setting up data
		titleView.setText(title);
		titleView.setTextColor(colorTitle);
		setImage(urlPic, pic);
		occupancyView.setText(occupancy);
		if(timeToGo != null)
			timeToGoView.setText(timeToGo);
		queueView.setText(queue);
		showSpinners = true;
		//CALENDAR
		dayOfWeek = getDay();
	}

	protected void onResume() {
		super.onResume();
		//REQUEST
		requestChartData();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_statistics_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch(itemId){
		case R.id.action_share:
			Intent intent = new Intent(this, ShareActivity.class);
			// VIEW RETRIEVING
			TextView titleView = (TextView)findViewById(R.id.title);
			TextView occupancyView = (TextView)findViewById(R.id.occupancy);
			TextView timeToGoView = (TextView)findViewById(R.id.time_to_go);
			TextView queueView = (TextView)findViewById(R.id.queue);
			//IMAGE PARCE
			ImageView pic = (ImageView)findViewById(R.id.pics);
			BitmapDrawable bmDrawable = ((BitmapDrawable) pic.getDrawable());
			Bitmap picBm = bmDrawable .getBitmap();
			Bundle picExtra = new Bundle();
			picExtra.putParcelable(Constants.KEY_PIC, picBm);
			//SET VAR			
			String title = titleView.getText().toString();
			String occupancy = occupancyView.getText().toString();
			String timeToGo = timeToGoView.getText().toString();
			String queue = queueView.getText().toString();
			//PUT VALUES
			intent.putExtra("ID", zoneId);
			intent.putExtra("TITLE", title);
			intent.putExtra("OCCUPANCY", occupancy);
			intent.putExtra("TIMETOGO", timeToGo);
			intent.putExtra("QUEUE", queue);
			intent.putExtra("TITLECOLOR", titleView.getCurrentTextColor());
			intent.putExtras(picExtra);	
			startActivity(intent);
			break;
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return true;
	}

	private int getDay(){
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		if (day == 1)
			return 6;
		else
			return (day-2); // (JSON LOOP AND WEEK OFFSET)
	}
	
	private int getHour(){
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	private void requestChartData(){
		// Init Requests Queue
		queue = Volley.newRequestQueue(this);
		String urlChartData = Constants.URL_DYNAMIC_DAILY+zoneId;
		if(showSpinners){
			weeklyLoading.setVisibility(View.VISIBLE);
			dailyLoading.setVisibility(View.VISIBLE);
		}

		JsonArrayRequest chartDataReq = new JsonArrayRequest(urlChartData, new Response.Listener<JSONArray>(){
			@Override
			public void onResponse(JSONArray response) {
				try {
					weeklyLoading.setVisibility(View.GONE);
					dailyLoading.setVisibility(View.GONE);
					showSpinners = false;
					buildCharts(response);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "Request error ... Check your data connection and try again", Toast.LENGTH_SHORT).show();
				VolleyLog.e("Error: ", error.getMessage());
			}
		});
		queue.add(chartDataReq);
	}

	private void buildCharts(JSONArray json) throws JSONException{
		//INIT WEEK DATA
		XYMultipleSeriesDataset weekDataSet = new XYMultipleSeriesDataset();
		CategorySeries weekSerieOff = new CategorySeries("Average occupancy");
		CategorySeries weekSerieOn = new CategorySeries("Average occupancy");
		int maxWeek = 0;
		// INIT X VALUES
		int X[] = new int[96];
		for(int k=0;k<96;k++){
			X[k] = k;
		}
		// DAY CHARTS
		for(int i=0;i<json.length();i++){
			XYSeries curSerie=new XYSeries("Daily Occupancy Serie");
			XYSeries pointerSerie=new XYSeries("");
			XYSeries dotSerie=new XYSeries("");
			JSONObject day = json.getJSONObject(i);
			LinearLayout curLayout = getLayoutFromId(i);
			curLayout.setLayoutParams(new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT));
			JSONArray hours = day.getJSONArray("hours");
			int dayAverage = 0;
			int maxDay = 0;
			int minDay = 0;
			int nbSamples = hours.length();
			int pointedClientCount = 0;
			int pointedHour = 0;
			if(nbSamples == 0){
				for(int l=0;l<95;l++){
					int curClientCount = 0;
					curSerie.add(X[l], curClientCount);
				}
				dayAverage = 0;
			}
			else{
				for(int j=0;j<nbSamples;j++){
					int curClientCount = hours.getJSONObject(j).getInt("clients");
					curSerie.add(X[j], curClientCount);
					//CROSS LINE CHARTS
					if(i == dayOfWeek){
						if(j == (getHour()*4)){
							pointedClientCount = curClientCount;
							pointedHour = j;
						}
					}
					// STATS
					if(curClientCount > maxDay)
						maxDay = curClientCount;
					else if(curClientCount < minDay)
						minDay = curClientCount;
					dayAverage = dayAverage + curClientCount;
				}
				dayAverage = dayAverage/nbSamples;
			}
			//DOT CHART
			dotSerie.add(pointedHour, pointedClientCount);
			//POINTER CHART
			for(int k=0;k<=pointedClientCount;k++){
				pointerSerie.add(pointedHour, k);
			}
			// LINE CHARTS RENDERER
			XYMultipleSeriesRenderer curLineRendere = getLineRenderer();
			lineChartSettings(curLineRendere, day.getString("day"), nbSamples, minDay, maxDay);
			//SET DAY CHART
			if(i == dayOfWeek){
				weekSerieOff.add(0);
				weekSerieOn.add(dayOfWeek);
			}
			else{
				weekSerieOff.add(dayAverage);
				weekSerieOn.add(0);
			}
			if(maxWeek < dayAverage)
				maxWeek = dayAverage;
			XYMultipleSeriesDataset curDataSet = new XYMultipleSeriesDataset();
			curDataSet.addSeries(curSerie);
			curDataSet.addSeries(pointerSerie);
			curDataSet.addSeries(dotSerie);
			GraphicalView curChart = ChartFactory.getLineChartView(this, curDataSet, curLineRendere);
			curLayout.addView(curChart);
		}
		weekDataSet.addSeries(weekSerieOff.toXYSeries());
		weekDataSet.addSeries(weekSerieOn.toXYSeries());
		// WEEK CHART
		XYMultipleSeriesRenderer barRenderer = getBarRenderer();
		barChartSettings(barRenderer, maxWeek);
		weekChart = ChartFactory.getBarChartView(this, weekDataSet, barRenderer, Type.STACKED);
		//weekChart.setOnClickListener(this);
		LinearLayout weekLayout = (LinearLayout) findViewById(R.id.average_week_chart);
		weekLayout.addView(weekChart);	
		//SET FOCUS
		setChartFocus();
	}

	private void setChartFocus(){
		final int dayOfWeekRunnable = getDay();
		hsv.post(new Runnable() {
			@Override
			public void run () {
				hsv.scrollTo(width*(dayOfWeekRunnable), 0);
			}
		});
	}

	private int getScreenWidth(){
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.x;
	}

	private LinearLayout getLayoutFromId(int id){
		switch (id){
		case 0:
			return (LinearLayout) findViewById(R.id.mon_chart);
		case 1:
			return (LinearLayout) findViewById(R.id.tue_chart);
		case 2:
			return (LinearLayout) findViewById(R.id.wed_chart);
		case 3:
			return (LinearLayout) findViewById(R.id.thu_chart);
		case 4:
			return (LinearLayout) findViewById(R.id.fri_chart);
		case 5:
			return (LinearLayout) findViewById(R.id.sat_chart);
		case 6:
			return (LinearLayout) findViewById(R.id.sun_chart);
		}
		return null;
	}

	private XYMultipleSeriesRenderer getBarRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		
		SimpleSeriesRenderer r1 = new SimpleSeriesRenderer();
		r1.setColor(getResources().getColor(R.color.gray));
		renderer.addSeriesRenderer(r1);
		
		SimpleSeriesRenderer r2 = new SimpleSeriesRenderer();
		r2.setColor(getResources().getColor(R.color.orange));
		renderer.addSeriesRenderer(r2);
		
		renderer.setClickEnabled(true);
		return renderer;
	}
	
	private XYMultipleSeriesRenderer getLineRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		
		XYSeriesRenderer  r1 = new XYSeriesRenderer ();
		r1.setPointStyle(PointStyle.POINT);
		r1.setColor(getResources().getColor(R.color.gray));
		r1.setFillPoints(true);
		r1.setLineWidth(2);
		renderer.addSeriesRenderer(r1);
		
		XYSeriesRenderer  r2 = new XYSeriesRenderer ();
		r2.setPointStyle(PointStyle.POINT);
		r2.setColor(getResources().getColor(R.color.orange));
		r2.setFillPoints(true);
		r2.setLineWidth(3);
		renderer.addSeriesRenderer(r2);
		
		XYSeriesRenderer  r3 = new XYSeriesRenderer ();
		r3.setPointStyle(PointStyle.CIRCLE);
		r3.setColor(getResources().getColor(R.color.orange));
		r3.setFillPoints(false);
		r3.setPointStrokeWidth(5);
		renderer.addSeriesRenderer(r3);
		
		return renderer;
	}
	// BAR CHART SETTINGS
	private void barChartSettings(XYMultipleSeriesRenderer renderer, int maxWeek){
		// SIZE
		renderer.setAxisTitleTextSize(20);
		renderer.setChartTitleTextSize(30);
		renderer.setLabelsTextSize(20);
		// INTERACTION
		renderer.setZoomEnabled(false, false);
		renderer.setShowLegend(false);
		renderer.setPanEnabled(false, false);
		// COLOR
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setAxesColor(Color.BLACK);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setGridColor(Color.GRAY);
		//MIN MAX
		renderer.setXAxisMin(0.5);
		renderer.setXAxisMax(7.5);
		renderer.setYAxisMin(0);
		formatYTextLabel(renderer, maxWeek);
		if(maxWeek<100)
			renderer.setYAxisMax(100);
		else
			renderer.setYAxisMax(maxWeek);
		// TEXT LABEL X AND Y
		renderer.setChartTitle("Average occupancy");
		renderer.addXTextLabel(1, "Mon.");
		renderer.addXTextLabel(2, "Tue.");
		renderer.addXTextLabel(3, "Wes.");
		renderer.addXTextLabel(4, "Thu.");
		renderer.addXTextLabel(5, "Fri.");
		renderer.addXTextLabel(6, "Sat.");
		renderer.addXTextLabel(7, "Sun.");

		// OTHER
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setBarSpacing(0.5);
		renderer.setShowGrid(true);
		renderer.setMargins(new int[] { 40, 50, 0, 0 });
		renderer.setXLabels(0); // sets the number of integer labels to appear
	}
	// LINE CHART SETTINGS
	private void lineChartSettings(XYMultipleSeriesRenderer renderer, String day, int nbSamples, int minDay, int maxDay){
		// SIZE
		renderer.setAxisTitleTextSize(20);
		renderer.setChartTitleTextSize(30);
		renderer.setLabelsTextSize(20);
		// INTERACTION
		renderer.setInScroll(true);
		renderer.setZoomEnabled(false, false);
		renderer.setPanEnabled(false, false);
		renderer.setShowLegend(false);
		// COLOR
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setAxesColor(Color.BLACK);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setGridColor(Color.GRAY);
		// MIN MAX
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(nbSamples);
		renderer.setYAxisMin(minDay);
		if(maxDay<100)
			renderer.setYAxisMax(100);
		else
			renderer.setYAxisMax(maxDay);
		// TEXT
		renderer.setChartTitle(day);
		formatXTextLabel(renderer);
		formatYTextLabel(renderer, maxDay);
		//OTHER
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setShowGrid(true);
		renderer.setXLabels(0);
		renderer.setMargins(new int[] { 40, 50, 0, 0 });
	}

	private void formatXTextLabel(XYMultipleSeriesRenderer renderer){
		for(int i=0; i<=95;i++){
			switch(i){
			case 0:
				renderer.addXTextLabel(i, "12am");
				break;
			case 11:
				renderer.addXTextLabel(i, "3am");
				break;
			case 23:
				renderer.addXTextLabel(i, "6am");
				break;
			case 35:
				renderer.addXTextLabel(i, "9am");
				break;
			case 47:
				renderer.addXTextLabel(i, "12pm");
				break;
			case 59:
				renderer.addXTextLabel(i, "3pm");
				break;
			case 71:
				renderer.addXTextLabel(i, "6pm");
				break;
			case 83:
				renderer.addXTextLabel(i, "9pm");
				break;
			default:
				break;
			}
		}
	}

	private void formatYTextLabel(XYMultipleSeriesRenderer renderer, int maxWeek){
		renderer.setYLabels(0);
		if(maxWeek <= 100){
			for(int i=0; i<=100;i++){
				if(i%25 == 0){
					renderer.addYTextLabel(i, i+"%", 0);
				}
			}			
		}
		else if(maxWeek > 100){
			for(int i=0; i<=maxWeek;i++){
				if(i%50 == 0){
					renderer.addYTextLabel(i, i+"%", 0);
				}
			}				
		}
	}

	private void setImage(String url, ImageView imageView){

		final ImageView view = imageView;

		if(VolleySingleton.getInstance().getRequestQueue().getCache().get(url)!=null){
			byte[]data = VolleySingleton.getInstance().getRequestQueue().getCache().get(url).data;
			Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
			view.setImageBitmap(bitmap);
		}
		else{
			mImageLoader.get(url, new ImageListener() {

				public void onErrorResponse(VolleyError error) {
					//TODO
				}

				public void onResponse(ImageContainer response, boolean arg1) {
					if (response.getBitmap() != null) {
						view.setImageBitmap(response.getBitmap());                	
					}
				}
			});
		}
	}
}
