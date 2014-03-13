package com.beehive.tools;

import java.util.ArrayList;

import com.beehive.R;
import com.beehive.objects.Zone;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Picasso.LoadedFrom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ZonesListAdapter extends ArrayAdapter<Zone> {

	private final Context context;
	private final ArrayList<Zone> zones;

	public ZonesListAdapter(Context context,int resourceId, ArrayList<Zone> zones) {
		super(context, resourceId, zones);
		this.context = context;
		this.zones = zones;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Zone curZone = zones.get(position);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = null;
		if(!curZone.isSubZone()){
			rowView = inflater.inflate(R.layout.zoneslist_row_zone, parent, false);
			TextView title = (TextView) rowView.findViewById(R.id.zone_title);
			title.setText(curZone.getName());
		}
		else{
			rowView = inflater.inflate(R.layout.zoneslist_row_subzone, parent, false);
			TextView title = (TextView) rowView.findViewById(R.id.title);
			TextView description = (TextView) rowView.findViewById(R.id.subtitle);
			final ImageView subZonePic = (ImageView) rowView.findViewById(R.id.pics);
			
			title.setText(curZone.getName());
			description.setText(curZone.getDescription());
			Target target = new Target() {
				@Override
				public void onBitmapFailed(Drawable arg0) {
				}
				@Override
				public void onBitmapLoaded(Bitmap bmp, LoadedFrom from) {
					subZonePic.setImageBitmap(bmp);	
				}
				@Override
				public void onPrepareLoad(Drawable arg0) {
				}
			};
			Picasso.with(context).load(curZone.getUrlPic()).into(target);
		}
		return rowView;
	}


}