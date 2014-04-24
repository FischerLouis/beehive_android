package com.beehive.application;


import java.util.HashMap;

import com.beehive.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;
import android.content.Context;

public class BeehiveApplication extends Application {

	private static BeehiveApplication mInstance;
	private static Context mAppContext;

	// The following line should be changed to include the correct property id.
	private static final String PROPERTY_ID = "UA-50255979-1";

	//Logging TAG
	private static final String TAG = "BeehiveApp";

	public static int GENERAL_TRACKER = 0;

	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	public BeehiveApplication() {
		super();
	}

	public synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = (Tracker) ((trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker) : (trackerId == TrackerName.GLOBAL_TRACKER));
					mTrackers.put(trackerId, t);
		}
		return mTrackers.get(trackerId);
	}


	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;

		this.setAppContext(getApplicationContext());
	}

	public static BeehiveApplication getInstance(){
		return mInstance;
	}
	public static Context getAppContext() {
		return mAppContext;
	}
	public void setAppContext(Context mAppContext) {
		this.mAppContext = mAppContext;
	}
}