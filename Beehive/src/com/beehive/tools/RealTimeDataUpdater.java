package com.beehive.tools;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;



public class RealTimeDataUpdater{
	
	private Context context;
	
	public RealTimeDataUpdater(Context context){
		this.context = context;
	}

	public void execute() throws MalformedURLException, InterruptedException, ExecutionException, JSONException {
		//Request Data
		URL urlRealTimeData = new URL("http://api.letsbeehive.tk/locations/stats");
		RealTimeDataDownloader realTimeDataDownloader = new RealTimeDataDownloader(context);
		realTimeDataDownloader.execute(urlRealTimeData);
		String realTimeDataString = realTimeDataDownloader.get();
		JSONArray dataArray = new JSONArray(realTimeDataString);
		//Update Data
		
	}
}