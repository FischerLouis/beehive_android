package com.beehive.tools;

import org.json.JSONArray;

public interface FragmentMapCommunicator {
	public void passStaticData(JSONArray json);
	public void passRealTimeData(JSONArray json);
	public void passQueryTextChange(String query);
}
