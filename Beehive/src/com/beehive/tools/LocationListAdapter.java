package com.beehive.tools;

import java.util.ArrayList;

import com.beehive.R;
import com.beehive.objects.Location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LocationListAdapter extends ArrayAdapter<Location> {

	private final Context context;
	private final ArrayList<Location> locations;

	public LocationListAdapter(Context context,int resourceId, ArrayList<Location> locations) {
		super(context, resourceId, locations);
		this.context = context;
		this.locations = locations;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Location currentLocation = locations.get(position);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = null;
		if(currentLocation.getIsCategory()){
			rowView = inflater.inflate(R.layout.locationslist_row_category, parent, false);
			TextView title = (TextView) rowView.findViewById(R.id.category);
			title.setText(currentLocation.getName());
		}
		else{
			rowView = inflater.inflate(R.layout.locationslist_row, parent, false);
			TextView title = (TextView) rowView.findViewById(R.id.title);
			TextView subtitle = (TextView) rowView.findViewById(R.id.subtitle);
			title.setText(currentLocation.getName());
			subtitle.setText("description soon ...");
		}
		return rowView;
	}


}