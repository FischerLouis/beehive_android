package com.beehive.tools;

import org.json.JSONArray;

public interface FragmentListCommunicator {
	public void passStaticData(JSONArray json);
	public void passRealTimeData(JSONArray json);
}
