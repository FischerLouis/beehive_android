package com.beehive.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.beehive.R;
import com.beehive.activities.StatisticsActivity;
import com.beehive.objects.AddInfoZone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentMap extends Fragment implements OnInfoWindowClickListener, InfoWindowAdapter, OnCameraChangeListener {

	private static View view;
	// Google Map
	private GoogleMap googleMap;
	//Initialize the zoom value
	private float previousZoomLevel = 14.5f;
	private boolean isZooming = false;
	//Data
	private HashMap<String,AddInfoZone> addInfoZoneHm;
	private HashMap<String,ArrayList<String>> zonesList;
	private ArrayList<Marker> markersZoneList;
	private ArrayList<Marker> markersSubZoneList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//Map set up
		initMap(inflater, container);
		//Data Request And Set Up
		try {
			retrieveData();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Marker building
		setMarkers();
		return view;
	}

	private void initMap(LayoutInflater inflater, ViewGroup container){
		//Google Map set up
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.fragment_map, container, false);
			googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			//Enable Self Location
			googleMap.setMyLocationEnabled(true);
			//Default position and zoom (Georgia Tech)
			final LatLng GeorgiaTech = new LatLng(33.775771, -84.396302);
			googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(GeorgiaTech, previousZoomLevel) );
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}
		googleMap.setInfoWindowAdapter(this);
		googleMap.setOnInfoWindowClickListener(this);
		googleMap.setOnCameraChangeListener(this);
	}

	private void retrieveData() throws JSONException{
		// Bulk Data Retrieve
		Bundle bundle = getArguments();
		String arrayZonesString = bundle.getString("arrayZonesString");
		JSONArray arrayZonesJSON = new JSONArray(arrayZonesString);
		/*
		 * 3 Data Objects Created
		 * AddInfoZones
		 * ListOfZones
		 * ListOfMarkers
		 */
		addInfoZoneHm = new HashMap<String,AddInfoZone>();
		zonesList = new HashMap<String,ArrayList<String>>();
		markersZoneList = new ArrayList<Marker>();
		markersSubZoneList = new ArrayList<Marker>();

		//Zone1 Loop
		for (int i=0;i<arrayZonesJSON.length();i++){
			JSONObject curZoneJSONObject = arrayZonesJSON.getJSONObject(i);
			//Get Attributs
			int curZoneId = curZoneJSONObject.getInt("id");
			String curZoneName = curZoneJSONObject.getString("name");
			Double curZoneLatitude = curZoneJSONObject.getDouble("latitude");
			Double curZoneLontitude = curZoneJSONObject.getDouble("longitude");
			String curZoneDescription = curZoneJSONObject.getString("description");
			String curZoneUrlPic = curZoneJSONObject.getString("url_photo");
			//MarkerList
			//options
			MarkerOptions curZoneMarkerOptions = new MarkerOptions()
			.position(new LatLng(curZoneLatitude, curZoneLontitude))
			.title(curZoneName)
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
			//marker
			Marker currentZoneMarker = googleMap.addMarker(curZoneMarkerOptions);
			markersZoneList.add(currentZoneMarker);
			//ZoneList
			ArrayList<String> subZoneList = new ArrayList<String>();
			//PicList
			ArrayList<String> picList = new ArrayList<String>();

			//Zone2 Loop
			for(int j=0;j<curZoneJSONObject.getJSONArray("locations").length();j++){
				JSONObject curSubZoneJSONObject = curZoneJSONObject.getJSONArray("locations").getJSONObject(j);
				//Get Attributs
				int curSubZoneId = curSubZoneJSONObject.getInt("id");
				String curSubZoneName = curSubZoneJSONObject.getString("name");
				Double curSubZoneLatitude = curSubZoneJSONObject.getDouble("latitude");
				Double curSubZoneLontitude = curSubZoneJSONObject.getDouble("longitude");
				String curSubZoneDescription = curSubZoneJSONObject.getString("description");
				String curSubZoneUrlPic = curSubZoneJSONObject.getString("url_photo");
				//int curSubZoneZoneId = curSubZoneJSONObject.getInt("id_zone");
				//MarkerList
				//options
				MarkerOptions curSubZoneMarkerOptions = new MarkerOptions()
				.position(new LatLng(curSubZoneLatitude, curSubZoneLontitude))
				.title(curSubZoneName)
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
				//marker
				Marker currentSubZoneMarker = googleMap.addMarker(curSubZoneMarkerOptions);
				markersSubZoneList.add(currentSubZoneMarker);
				//ZoneList
				subZoneList.add(curSubZoneName);
				//AddInfoZone
				AddInfoZone curSubZoneInfo = new AddInfoZone(curSubZoneId, curSubZoneName, curSubZoneDescription, curSubZoneUrlPic);
				addInfoZoneHm.put(curSubZoneName, curSubZoneInfo);
				//picList
				picList.add(curSubZoneUrlPic);

			}
			// ZoneList
			zonesList.put(curZoneName, subZoneList);
			//AddInfoZone
			AddInfoZone curZoneInfo = new AddInfoZone(curZoneId, curZoneName, curZoneDescription, curZoneUrlPic, picList);
			addInfoZoneHm.put(curZoneName, curZoneInfo);
		}
		//Log.v("addInfoZoneHm",addInfoZoneHm.toString());
	}

	private void setMarkers(){		
		for(int i=0;i<markersZoneList.size();i++){
			markersZoneList.get(i).setVisible(true);
		}
		for(int j=0;j<markersSubZoneList.size();j++){
			markersSubZoneList.get(j).setVisible(false);
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {

		if(zonesList.containsKey(marker.getTitle())){
			googleMap.animateCamera( CameraUpdateFactory.newLatLngZoom(marker.getPosition() , 17f) );
			hideZoneMarker();
		}
		else{
			Intent intent = new Intent(getActivity(), StatisticsActivity.class);
			intent.putExtra("TITLE", marker.getTitle());
			intent.putExtra("SUBTITLE", addInfoZoneHm.get(marker.getTitle()).getDescription());
			startActivity(intent);
		}
	}

	@Override
	public View getInfoContents(Marker marker) {
		View view;
		// Getting view from the layout file info_window_layout
		if(zonesList.containsKey(marker.getTitle())){
			view = getActivity().getLayoutInflater().inflate(R.layout.map_infowindow_zone, null);
			TextView zoneName = (TextView)view.findViewById(R.id.zone_name);
			TextView zoneDescription = (TextView)view.findViewById(R.id.zone_description);
			final ImageView zonePic = (ImageView)view.findViewById(R.id.zone_pic);
			AddInfoZone info = addInfoZoneHm.get(marker.getTitle());
			zoneName.setText(marker.getTitle());
			zoneDescription.setText(info.getDescription());

			String urlZonePic = info.getUrlPic();
			Target target = new Target() {
				@Override
				public void onBitmapFailed(Drawable arg0) {
				}
				@Override
				public void onBitmapLoaded(Bitmap bmp, LoadedFrom from) {
					zonePic.setImageBitmap(bmp);	
				}
				@Override
				public void onPrepareLoad(Drawable arg0) {
				}
			};
			Picasso.with(getActivity()).load(urlZonePic).into(target);

		}
		else{
			view = getActivity().getLayoutInflater().inflate(R.layout.map_infowindow_subzone, null);
			TextView subZoneName = (TextView)view.findViewById(R.id.subzone_name);
			TextView subZoneDescription = (TextView)view.findViewById(R.id.subzone_description);
			final ImageView subZonePic = (ImageView)view.findViewById(R.id.subzone_pic);
			AddInfoZone info = addInfoZoneHm.get(marker.getTitle());
			subZoneName.setText(marker.getTitle());
			subZoneDescription.setText(info.getDescription());
			
			String urlSubZonePic = info.getUrlPic();
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
			Picasso.with(getActivity()).load(urlSubZonePic).into(target);
		}
		return view;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		if(previousZoomLevel != position.zoom)
		{
			isZooming = true;
			if(position.zoom >= 17f){
				hideZoneMarker();
			}
			else if(position.zoom < 17f){
				hideSubZoneMarker();
			}          
		}
		else{
			isZooming = false;
		}
		previousZoomLevel = position.zoom;		
	}

	private void hideZoneMarker(){
		for(int i=0;i<markersZoneList.size();i++){
			markersZoneList.get(i).setVisible(false);
		}
		for(int j=0;j<markersSubZoneList.size();j++){
			markersSubZoneList.get(j).setVisible(true);
		}
	}

	private void hideSubZoneMarker(){
		for(int i=0;i<markersZoneList.size();i++){
			markersZoneList.get(i).setVisible(true);
		}
		for(int j=0;j<markersSubZoneList.size();j++){
			markersSubZoneList.get(j).setVisible(false);
		}
	}
}