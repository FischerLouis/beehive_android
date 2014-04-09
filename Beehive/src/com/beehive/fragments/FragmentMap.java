package com.beehive.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.beehive.R;
import com.beehive.activities.MainActivity;
import com.beehive.activities.StatisticsActivity;
import com.beehive.objects.AddInfoZone;
import com.beehive.objects.RealTimeInfoZone;
import com.beehive.tools.Constants;
import com.beehive.tools.FragmentMapCommunicator;
import com.beehive.tools.VolleySingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class FragmentMap extends Fragment implements FragmentMapCommunicator, OnInfoWindowClickListener, InfoWindowAdapter, OnCameraChangeListener {

	private Context context;
	private Activity activity;
	private static View view;

	private ImageLoader mImageLoader;
	// Google Map
	private GoogleMap googleMap;
	//Initialize the zoom value
	private float previousZoomLevel = Constants.MAP_ZOOM_INIT;
	//private boolean isZooming = false;
	//Data
	private HashMap<String,AddInfoZone> addInfoZoneHm;
	private HashMap<Integer,RealTimeInfoZone> realTimeInfoZoneHm;
	private HashMap<String,Integer> zonesList;
	private HashMap<String,Integer> subZonesList;
	private ArrayList<Marker> markersZoneList;
	private ArrayList<Marker> markersSubZoneList;

	public FragmentMap(){
	}
	public static FragmentMap newInstance(){
		return new FragmentMap();
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		context = getActivity();
		this.activity = activity;
		((MainActivity)context).fragmentMapCommunicator = this;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//mImageLoader = ImageLoaderProvider.getImageLoader(context, VolleyProvider.getQueue(context));
		mImageLoader = VolleySingleton.getInstance().getImageLoader();
		Log.v("ONCREATE","ONCREATE");
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try{
			view = inflater.inflate(R.layout.fragment_map, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}
		//setRetainInstance(true);
		initMap();
		Log.v("ONCREATEVIEW","ONCREATEVIEW");
		return view;
	}

	@Override
	public void onResume(){
		super.onResume();
		boolean loadFromCache =((MainActivity)context).loadMapFromCache;
		if(loadFromCache){
			try {
				updateStaticData(((MainActivity)context).jsonCached);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			((MainActivity)context).loadMapFromCache = false;
		}
		Log.v("ONRESUME","ONRESUME");
	}
	
	@Override
	public void onStop(){
		super.onStop();
		showMarkers(false, false);
	}

	private void initMap(){
		//Google Map set up
		googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		//Enable Self Location
		googleMap.setMyLocationEnabled(true);
		//Default position and zoom (Georgia Tech)
		final LatLng GeorgiaTech = new LatLng(33.775771, -84.396302);
		googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(GeorgiaTech, previousZoomLevel) );

		googleMap.setInfoWindowAdapter(this);
		googleMap.setOnInfoWindowClickListener(this);
		googleMap.setOnCameraChangeListener(this);
	}

	public void updateStaticData(JSONArray json) throws JSONException{
		//DATA
		addInfoZoneHm = new HashMap<String,AddInfoZone>();
		zonesList = new HashMap<String,Integer>();
		subZonesList = new HashMap<String,Integer>();
		markersZoneList = new ArrayList<Marker>();
		markersSubZoneList = new ArrayList<Marker>();
		//Zone1 Loop
		for (int i=0;i<json.length();i++){
			JSONObject curZoneJSONObject = json.getJSONObject(i);
			//Get Attributs
			int curZoneId = curZoneJSONObject.getInt("id");
			final String curZoneName = curZoneJSONObject.getString("name");
			Double curZoneLatitude = curZoneJSONObject.getDouble("latitude");
			Double curZoneLontitude = curZoneJSONObject.getDouble("longitude");
			String curZoneDescription = curZoneJSONObject.getString("description");
			String curZoneUrlPic = curZoneJSONObject.getString("url_photo");
			//CACHE IMAGE
			cacheImage(curZoneUrlPic);
			//MarkerList
			MapsInitializer.initialize(activity);
			//options
			MarkerOptions curZoneMarkerOptions = new MarkerOptions()
			.position(new LatLng(curZoneLatitude, curZoneLontitude))
			.title(curZoneName)
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_orange));
			//.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
			//marker
			Marker currentZoneMarker = googleMap.addMarker(curZoneMarkerOptions);
			markersZoneList.add(currentZoneMarker);
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
				//CACHE IMAGE
				cacheImage(curSubZoneUrlPic);
				//MarkerList
				//options
				MarkerOptions curSubZoneMarkerOptions = new MarkerOptions()
				.position(new LatLng(curSubZoneLatitude, curSubZoneLontitude))
				.title(curSubZoneName)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_orange));
				//marker
				Marker currentSubZoneMarker = googleMap.addMarker(curSubZoneMarkerOptions);
				markersSubZoneList.add(currentSubZoneMarker);
				//ZoneList
				subZonesList.put(curSubZoneName, curSubZoneId);
				//AddInfoZone
				AddInfoZone curSubZoneInfo = new AddInfoZone(curSubZoneId, curSubZoneName, curSubZoneDescription, curSubZoneUrlPic);
				addInfoZoneHm.put(curSubZoneName, curSubZoneInfo);
				//picList
				picList.add(curSubZoneUrlPic);
			}
			// ZoneList
			zonesList.put(curZoneName, curZoneId);
			//AddInfoZone
			AddInfoZone curZoneInfo = new AddInfoZone(curZoneId, curZoneName, curZoneDescription, curZoneUrlPic, picList);
			addInfoZoneHm.put(curZoneName, curZoneInfo);
		}
		showMarkers(false, false);
		//DISPLAYING MARKERS
		if(previousZoomLevel >= Constants.MAP_ZOOM_THRESHOLD){
			showMarkers(false, true);
		}
		else {
			showMarkers(true, false);
		}
	}

	public void updateRealTimeData(JSONArray json) throws JSONException{
		//init or reset of real time data
		if(realTimeInfoZoneHm == null){
			realTimeInfoZoneHm = new HashMap<Integer,RealTimeInfoZone>();
		}
		else{
			realTimeInfoZoneHm.clear();
		}

		for (int i=0;i<json.length();i++){
			JSONObject curObj = json.getJSONObject(i);
			RealTimeInfoZone curRealTimeInfoZone = new RealTimeInfoZone(curObj.getInt("id"), curObj.getInt("occupancy_percent"), curObj.getString("best_time"), curObj.getString("queue"), curObj.getInt("threshold_min"), curObj.getInt("threshold_max"));
			realTimeInfoZoneHm.put(curObj.getInt("id"), curRealTimeInfoZone);
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {

		String markerTitle = marker.getTitle();

		if(zonesList.containsKey(markerTitle)){
			googleMap.animateCamera( CameraUpdateFactory.newLatLngZoom(marker.getPosition() , Constants.MAP_ZOOM_THRESHOLD) );
			showMarkers(false, true);
		}
		else{
			Intent intent = new Intent(getActivity(), StatisticsActivity.class);
			int id = subZonesList.get(markerTitle);	
			intent.putExtra("ID", id);
			intent.putExtra("TITLE", markerTitle);
			intent.putExtra("SUBTITLE", addInfoZoneHm.get(markerTitle).getDescription());
			intent.putExtra("URLPIC", addInfoZoneHm.get(markerTitle).getUrlPic());
			if(realTimeInfoZoneHm != null){
				intent.putExtra("OCCUPANCY",realTimeInfoZoneHm.get(id).getOccupancy());
				intent.putExtra("TIMETOGO",realTimeInfoZoneHm.get(id).getBestTime());
				intent.putExtra("QUEUE",realTimeInfoZoneHm.get(id).getQueue());
				intent.putExtra("TITLECOLOR",getColorTitle(realTimeInfoZoneHm.get(id).getOccupancy(), realTimeInfoZoneHm.get(id).getThresholdMin(), realTimeInfoZoneHm.get(id).getThresholdMax()));
			}
			startActivity(intent);
		}
	}

	@Override
	public View getInfoContents(Marker marker) {
		View view;
		final String markerTitle = marker.getTitle();
		// Getting view from the layout file info_window_layout
		//InfoWindow for Zone
		if(zonesList.containsKey(markerTitle)){
			view = getActivity().getLayoutInflater().inflate(R.layout.map_infowindow_zone, null);
			TextView zoneName = (TextView)view.findViewById(R.id.zone_name);
			TextView zoneDescription = (TextView)view.findViewById(R.id.zone_description);
			ImageView zonePic = (ImageView)view.findViewById(R.id.zone_pic);
			AddInfoZone info = addInfoZoneHm.get(markerTitle);
			zoneName.setText(markerTitle);
			zoneDescription.setText(info.getDescription());
			//IMAGE FROM CACHE
			setImageFromCache(info.getUrlPic(), zonePic);
		}
		//InfoWindow for SubZone
		else{
			view = getActivity().getLayoutInflater().inflate(R.layout.map_infowindow_subzone, null);
			TextView subZoneName = (TextView)view.findViewById(R.id.subzone_name);
			TextView subZoneDescription = (TextView)view.findViewById(R.id.subzone_description);
			TextView subZoneOccupancy = (TextView)view.findViewById(R.id.subzone_occupancy);
			TextView subZoneTimeToGo = (TextView)view.findViewById(R.id.subzone_timetogo);
			TextView subZoneQueue = (TextView)view.findViewById(R.id.subzone_queue);
			ImageView subZonePic = (ImageView)view.findViewById(R.id.subzone_pic);
			AddInfoZone info = addInfoZoneHm.get(markerTitle);
			subZoneName.setText(markerTitle);
			subZoneDescription.setText(info.getDescription());
			// SET FROM CACHE
			setImageFromCache(info.getUrlPic(), subZonePic);
			// REAL TIME INFO
			if(realTimeInfoZoneHm != null){
				RealTimeInfoZone realTimeInfo = realTimeInfoZoneHm.get(info.getId());
				subZoneOccupancy.setText(realTimeInfo.getOccupancy()+"%");
				subZoneTimeToGo.setText(realTimeInfo.getBestTime());
				subZoneQueue.setText(realTimeInfo.getQueue());
				setColorTitle(subZoneName, realTimeInfo.getOccupancy(), realTimeInfo.getThresholdMin(), realTimeInfo.getThresholdMax());
			}			
		}
		return view;
	}

	private void setColorTitle(TextView title, int occupancy, int thresholdMin, int thresholdMax){
		if(occupancy < thresholdMin){
			title.setTextColor(context.getResources().getColor(R.color.green));
		}
		else if(occupancy < thresholdMax){
			title.setTextColor(context.getResources().getColor(R.color.orange));
		}
		else{
			title.setTextColor(context.getResources().getColor(R.color.red));
		}
	}

	private int getColorTitle(int occupancy, int thresholdMin, int thresholdMax){
		if(occupancy < thresholdMin){
			return context.getResources().getColor(R.color.green);
		}
		else if(occupancy < thresholdMax){
			return context.getResources().getColor(R.color.orange);
		}
		else{
			return context.getResources().getColor(R.color.red);
		}
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		if(previousZoomLevel != position.zoom)
		{
			if(position.zoom >= Constants.MAP_ZOOM_THRESHOLD){
				showMarkers(false, true);
			}
			else {
				showMarkers(true, false);
			}          
		}
		previousZoomLevel = position.zoom;		
	}

	private void showMarkers(boolean showZone, boolean showSubZone){
		for(int i=0;i<markersZoneList.size();i++){
			markersZoneList.get(i).setVisible(showZone);
		}
		for(int j=0;j<markersSubZoneList.size();j++){
			markersSubZoneList.get(j).setVisible(showSubZone);
		}
	}

	private void clearMarkers(){
		for(int i=0;i<markersZoneList.size();i++){
			markersZoneList.get(i).setVisible(false);
		}
		for(int j=0;j<markersSubZoneList.size();j++){
			markersSubZoneList.get(j).setVisible(false);
		}
	}

	private void cacheImage(String url){	
		mImageLoader.get(url, new ImageListener() {

			public void onErrorResponse(VolleyError error) {
				//TODO
			}

			public void onResponse(ImageContainer response, boolean arg1) {
				if (response.getBitmap() != null) {
					//TODO	                	
				}
			}
		});
	}

	private void setImageFromCache(String url, ImageView view){
		if(VolleySingleton.getInstance().getRequestQueue().getCache().get(url)!=null){
			byte[]data = VolleySingleton.getInstance().getRequestQueue().getCache().get(url).data;
			Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
			view.setImageBitmap(bitmap);
		}
		else
			//TODO ImageView FAIL
			Log.v("XX","FAIL");
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