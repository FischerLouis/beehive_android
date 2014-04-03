package com.beehive.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.beehive.activities.MainActivity;
import com.beehive.activities.StatisticsActivity;
import com.beehive.objects.Zone;
import com.beehive.tools.Constants;
import com.beehive.tools.FragmentListCommunicator;
import com.beehive.tools.ZonesListAdapter;
import com.beehive.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentList extends ListFragment implements FragmentListCommunicator, OnItemClickListener {

	private Context context;
	private Activity activity;

	private ArrayList<Zone> zonesList;
	private ZonesListAdapter adapter;

	public FragmentList(){
	}
	public static FragmentList newInstance(){
		return new FragmentList();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = getActivity();
		this.activity = activity;
		((MainActivity)context).fragmentListCommunicator = this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_list, container, false);
		return rootView;
	}

	@Override 
	public void onActivityCreated(Bundle savedInstanceState) {
		// VIEWS
		ListView locationsList = (ListView) getActivity().findViewById(android.R.id.list);
		// LISTENER
		locationsList.setOnItemClickListener(this);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume(){
		super.onResume();
		boolean loadFromCache =((MainActivity)context).loadListFromCache;
		if(loadFromCache){
			try {
				updateStaticData(((MainActivity)context).jsonCached);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			((MainActivity)context).loadListFromCache = false;
		}
	}

	public void updateStaticData(JSONArray json) throws JSONException{
		zonesList = new ArrayList<Zone>();

		for (int i=0;i<json.length();i++){
			JSONObject curZoneJSONObject = json.getJSONObject(i);
			//Get Attributs
			int curZoneId = curZoneJSONObject.getInt("id");
			String curZoneName = curZoneJSONObject.getString("name");
			Double curZoneLatitude = curZoneJSONObject.getDouble("latitude");
			Double curZoneLontitude = curZoneJSONObject.getDouble("longitude");
			String curZoneDescription = curZoneJSONObject.getString("description");
			String curZoneUrlPic = curZoneJSONObject.getString("url_photo");
			//Creat Object
			Zone curZone = new Zone(curZoneId, curZoneName, curZoneLatitude, curZoneLontitude, curZoneDescription, curZoneUrlPic, false);
			zonesList.add(curZone);
			for(int j=0;j<curZoneJSONObject.getJSONArray("locations").length();j++){
				JSONObject curSubZoneJSONObject = curZoneJSONObject.getJSONArray("locations").getJSONObject(j);
				//Get Attributs
				int curSubZoneId = curSubZoneJSONObject.getInt("id");
				String curSubZoneName = curSubZoneJSONObject.getString("name");
				Double curSubZoneLatitude = curSubZoneJSONObject.getDouble("latitude");
				Double curSubZoneLontitude = curSubZoneJSONObject.getDouble("longitude");
				String curSubZoneDescription = curSubZoneJSONObject.getString("description");
				String curSubZoneUrlPic = curSubZoneJSONObject.getString("url_photo");
				//Creat object
				Zone curSubZone = new Zone(curSubZoneId, curSubZoneName, curSubZoneLatitude, curSubZoneLontitude, curSubZoneDescription, curSubZoneUrlPic, true);
				zonesList.add(curSubZone);
			}
		}
		// DISPLAY DATA
		adapter = new ZonesListAdapter(activity.getApplicationContext(),R.layout.zoneslist_row_subzone, zonesList);
		setListAdapter(adapter);
	}

	public void updateRealTimeData(JSONArray json) throws JSONException{

		//UPDATING DATA
		for(int i=0;i<json.length();i++){
			JSONObject curObj = json.getJSONObject(i);
			int curId = curObj.getInt("id");
			for(int j=0;j<zonesList.size();j++){
				if(zonesList.get(j).getId()==curId && zonesList.get(j).isSubZone()){
					zonesList.get(j).setOccupancy(curObj.getString("occupancy"));
					zonesList.get(j).setTimeToGo(curObj.getString("best_time"));
				}
			}
		}
		//UPDATING ADAPTER
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(view.getTag(R.string.subzone_tag_key).equals("subZone")){
			Intent intent = new Intent(getActivity(), StatisticsActivity.class);
			// VIEW RETRIEVING
			TextView titleView = (TextView)view.findViewById(R.id.title);
			TextView subtitleView = (TextView)view.findViewById(R.id.subtitle);
			TextView occupancyView = (TextView)view.findViewById(R.id.occupancy);
			TextView timeToGoView = (TextView)view.findViewById(R.id.time_to_go);
			//IMAGE PARCE
			ImageView pic = (ImageView)view.findViewById(R.id.pics);
			BitmapDrawable bmDrawable = ((BitmapDrawable) pic.getDrawable());
			Bitmap picBm = bmDrawable .getBitmap();
			Bundle picExtra = new Bundle();
			picExtra.putParcelable(Constants.KEY_PIC, picBm);
			//SET VAR			
			String title = titleView.getText().toString();
			String subtitle = subtitleView.getText().toString();
			String occupancy = occupancyView.getText().toString();
			String timeToGo = timeToGoView.getText().toString();
			int idView = (Integer) view.getTag(R.string.id_tag_key);
			//PUT VALUES
			intent.putExtra("ID", idView);
			intent.putExtra("TITLE", title);
			intent.putExtra("SUBTITLE", subtitle);
			intent.putExtra("OCCUPANCY", occupancy);
			intent.putExtra("TIMETOGO", timeToGo);
			intent.putExtras(picExtra);
			startActivity(intent);
		}
	}
	
	@Override
	public void passStaticData(JSONArray json) {
		try {
			updateStaticData(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}
	@Override
	public void passRealTimeData(JSONArray json) {
		try {
			updateRealTimeData(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}

}
