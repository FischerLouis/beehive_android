package com.beehive.fragments;

import java.util.ArrayList;

import com.beehive.activities.StatisticsActivity;
import com.beehive.objects.Building;
import com.beehive.objects.Location;
import com.beehive.tools.LocationListAdapter;
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
	
	private ArrayList<Location> locations;

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
		//Data Request
		requestData();
		LocationListAdapter adapter = new LocationListAdapter(getActivity().getApplicationContext(),R.layout.locationslist_row, locations);
		setListAdapter(adapter);
		//Setting up the listner
		footer.setOnClickListener(this);
		locationsList.setOnItemClickListener(this);

		super.onActivityCreated(savedInstanceState);
	}

	private void requestData(){
		//Creation des objects Locations
		Location build = new Location(0, "Clough", "N/A", 0, 0, 0, "N/A");
		build.setIsCategory(true);
		Location loc1 = new Location(1, "Starbucks", "http://lala/", 33.774258, -84.396348, 0.80, "now");
		Location loc2 = new Location(2, "Conference Room", "http://lala/", 33.774930, -84.396269, 0.20, "in 5mn");
		Location loc3 = new Location(3, "Big Hall", "http://lala/", 33.774635, -84.396465, 0.65, "tommorow");
		Location loc4 = new Location(4, "Meeting Room", "http://lala/", 33.775038, -84.396449, 0.10, "now");
		locations = new ArrayList<Location>();
		locations.add(build);
		locations.add(loc1);
		locations.add(loc2);
		locations.add(loc3);
		locations.add(loc4);
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
