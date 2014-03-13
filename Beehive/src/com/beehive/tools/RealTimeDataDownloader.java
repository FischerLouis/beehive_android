package com.beehive.tools;

import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

public class RealTimeDataDownloader extends AsyncTask<URL, Integer, String> {
	
	Context context;
	
	public RealTimeDataDownloader(Context context){
		this.context = context;
	}

	@Override
	protected String doInBackground(URL... urls) {
		JSONObject jsonRequest = null;
		String urlRealTimeStat = urls[0].toString();
		// Creating JSON Parser instance
		JSONParser jParser = new JSONParser();
		//Getting JSON informations
		try {
			jsonRequest = new JSONObject(jParser.getJSONParsed(urlRealTimeStat));
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		return jsonRequest.toString();
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {}

	@Override
	protected void onPostExecute(String result) {
		//
	}

	@Override
	protected void onPreExecute() {}

}