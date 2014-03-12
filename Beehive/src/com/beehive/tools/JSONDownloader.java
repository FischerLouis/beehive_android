package com.beehive.tools;

import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import android.content.Context;
import android.os.AsyncTask;

public class JSONDownloader extends AsyncTask<URL, Integer, String> {
	
	Context context;
	
	public JSONDownloader(Context context){
		this.context = context;
	}

	@Override
	protected String doInBackground(URL... urls) {
		JSONArray jsonRequest = null;
		String urlZones = urls[0].toString();
		// Creating JSON Parser instance
		JSONParser jParser = new JSONParser();
		//Getting JSON informations
		try {
			jsonRequest = new JSONArray(jParser.getJSONParsed(urlZones));
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