package com.beehive.tools;

import com.beehive.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LocationListAdapter extends ArrayAdapter<String> {

	private final Context context;
	  private final String[] values;

	public LocationListAdapter(Context context,int resourceId, String[] values) {
		super(context, resourceId, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.locationslist_row, parent, false);
		
		TextView title = (TextView) rowView.findViewById(R.id.title);
		TextView subtitle = (TextView) rowView.findViewById(R.id.subtitle);
		title.setText(values[position]);
		subtitle.setText("description soon ...");
		return rowView;
	}


}