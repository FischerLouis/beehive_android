package com.beehive.fragments;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.beehive.activities.StatisticsActivity;
import com.beehive.objects.Zone;
import com.beehive.tools.RealTimeDataUpdater;
import com.beehive.tools.ZonesListAdapter;
import com.beehive.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentList extends ListFragment implements OnItemClickListener, OnClickListener {

	private ArrayList<Zone> zonesList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_list, container, false);
		return rootView;
	}

	@Override 
	public void onActivityCreated(Bundle savedInstanceState) {
		//Retrieving the views
		LinearLayout footer = (LinearLayout)getActivity().findViewById(R.id.footer);
		ListView locationsList = (ListView) getActivity().findViewById(android.R.id.list);
		//Static Data Request
		try {
			requestStaticData();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//RealTime Data Request
		/*RealTimeDataUpdater updater = new RealTimeDataUpdater(getActivity());
		try {
			updater.execute();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		ZonesListAdapter adapter = new ZonesListAdapter(getActivity().getApplicationContext(),R.layout.zoneslist_row_subzone, zonesList);
		setListAdapter(adapter);
		//Setting up the listner
		footer.setOnClickListener(this);
		locationsList.setOnItemClickListener(this);

		super.onActivityCreated(savedInstanceState);
	}

	private void requestStaticData() throws JSONException {
		Bundle bundle = getArguments();
		String arrayZonesString = bundle.getString("arrayZonesString");
		JSONArray arrayZonesJSON = new JSONArray(arrayZonesString);
		//Zone1 Loop
		zonesList = new ArrayList<Zone>();
		for (int i=0;i<arrayZonesJSON.length();i++){
			JSONObject curZoneJSONObject = arrayZonesJSON.getJSONObject(i);
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
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), StatisticsActivity.class);
		TextView titleView = (TextView)view.findViewById(R.id.title);
		TextView subtitleView = (TextView)view.findViewById(R.id.subtitle);
		String title = titleView.getText().toString();
		String subtitle = subtitleView.getText().toString();
		intent.putExtra("TITLE", title);
		intent.putExtra("SUBTITLE", subtitle);
		startActivity(intent);
	}

	@Override
	public void onClick(View view) {
		Toast.makeText(getActivity(), "Coming soon ...", Toast.LENGTH_SHORT).show();
	}

}
