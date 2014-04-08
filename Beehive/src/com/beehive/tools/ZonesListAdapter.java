package com.beehive.tools;

import java.util.ArrayList;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.beehive.R;
import com.beehive.objects.Zone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class ZonesListAdapter extends ArrayAdapter<Zone> implements Filterable {

	private final Context context;
	private ArrayList<Zone> zones;
	private ImageLoader mImageLoader;
	private ZoneFilter zoneFilter;

	public ZonesListAdapter(Context context,int resourceId, ArrayList<Zone> zones) {
		super(context, resourceId, zones);
		this.context = context;
		this.zones = zones;
		mImageLoader = VolleySingleton.getInstance().getImageLoader();
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
			rowView.setTag(R.string.subzone_tag_key, "zone");
		}
		else{
			rowView = inflater.inflate(R.layout.zoneslist_row_subzone, parent, false);
			TextView title = (TextView) rowView.findViewById(R.id.title);
			TextView description = (TextView) rowView.findViewById(R.id.subtitle);
			TextView occupancy = (TextView) rowView.findViewById(R.id.occupancy);
			TextView timeToGo = (TextView) rowView.findViewById(R.id.time_to_go);
			int viewId = curZone.getId();
			rowView.setTag(R.string.subzone_tag_key, "subZone");
			final ImageView subZonePic = (ImageView) rowView.findViewById(R.id.pics);

			title.setText(curZone.getName());
			description.setText(curZone.getDescription());
			occupancy.setText(curZone.getOccupancy());
			timeToGo.setText(curZone.getTimeToGo());
			// TAG ID ZONE
			rowView.setTag(R.string.id_tag_key, viewId);
			// TAG URL PIC
			rowView.setTag(R.string.urlpic_tag_key, curZone.getUrlPic());
			// IMAGE FROM CACHE IF EXISTING
			setImage(curZone.getUrlPic(), subZonePic);
			// TITLE ACCORDING TO THE OCCUPANCY
			setColorTitle(title, curZone.getOccupancy());
		}
		return rowView;
	}

	@Override
	public Filter getFilter() {
		if (zoneFilter == null) {
			zoneFilter = new ZoneFilter();
		}
		return zoneFilter;
	}
	
	@Override
	public int getCount()
	{
	   return zones.size();
	}

	private void setColorTitle(TextView title, String occupancy){
		int percentageChar = occupancy.indexOf("%");
		if(percentageChar>0){
			int valueOcc = Integer.parseInt(occupancy.substring(0, percentageChar));
			if(valueOcc < Constants.OCCUPANCY_THRESHOLD_LOW){
				title.setTextColor(context.getResources().getColor(R.color.green));
			}
			else if(valueOcc < Constants.OCCUPANCY_THRESHOLD_HIGH){
				title.setTextColor(context.getResources().getColor(R.color.orange));
			}
			else{
				title.setTextColor(context.getResources().getColor(R.color.red));
			}
		}
	}

	private void setImage(String url, ImageView imageView){

		final ImageView view = imageView;

		if(VolleySingleton.getInstance().getRequestQueue().getCache().get(url)!=null){
			byte[]data = VolleySingleton.getInstance().getRequestQueue().getCache().get(url).data;
			Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
			view.setImageBitmap(bitmap);
		}
		else{
			mImageLoader.get(url, new ImageListener() {

				public void onErrorResponse(VolleyError error) {
					//TODO
				}

				public void onResponse(ImageContainer response, boolean arg1) {
					if (response.getBitmap() != null) {
						view.setImageBitmap(response.getBitmap());                	
					}
				}
			});
		}
	}

	private class ZoneFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			if (constraint == null || constraint.length() == 0) {
				// No filter implemented we return all the list
				results.values = zones;
				results.count = zones.size();
			}
			else{
				// VARS
				ArrayList<Zone> zoneRes = new ArrayList<Zone>();
				boolean addSubZone = false;
				boolean zoneAdded = false;
				int indexZoneSaved = -1;
				// MATCHING ZONE
				for(int i=0;i<zones.size();i++){
					// ZONE
					if(!zones.get(i).isSubZone()){
						Zone curZone = zones.get(i);
						addSubZone = false;
						zoneAdded = false;
						if(curZone.getName().contains(constraint)){
							zoneRes.add(curZone);
							addSubZone = true;
							Log.v("ZONE MATCH",curZone.getName());
						}
						else{
							indexZoneSaved = i;
						}
					}
					// SUB ZONE
					else{
						Zone curSubZone = zones.get(i);
						if(addSubZone){
							zoneRes.add(curSubZone);
						}
						else{
							if(curSubZone.getName().contains(constraint.toString())){
								Log.v("SUBZONE MATCH",curSubZone.getName());
								if(zoneAdded){
									zoneRes.add(curSubZone);
								}
								else{
									zoneRes.add(zones.get(indexZoneSaved));
									zoneRes.add(curSubZone);
									zoneAdded = true;
								}
							}
						}
					}
				}
				results.values = zoneRes;
				results.count = zoneRes.size();
				Log.v("COUNT",""+results.count);
			}
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,FilterResults results) {
			if (results.count == 0)
				notifyDataSetInvalidated();
			else {
				zones = (ArrayList<Zone>) results.values;
				notifyDataSetChanged();
			}
		}
	}

}