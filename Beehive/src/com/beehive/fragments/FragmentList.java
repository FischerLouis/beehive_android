package com.beehive.fragments;

import com.beehive.activities.StatisticsActivity;
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
		//Setting up the list
		String[] values = new String[] { "Academy of Medecine", "College of Computing", "Van Leer",
				"Klaus", "Lyman Hall", "Student Center", "Clough", "CRC", "TSRB", "Centergy" };
		LocationListAdapter adapter = new LocationListAdapter(getActivity().getApplicationContext(),R.layout.locationslist_row, values);
		setListAdapter(adapter);
		//Setting up the listner
		footer.setOnClickListener(this);
		locationsList.setOnItemClickListener(this);

		super.onActivityCreated(savedInstanceState);
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
