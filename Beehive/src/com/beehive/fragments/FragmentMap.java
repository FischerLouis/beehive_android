package com.beehive.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.beehive.R;
import com.beehive.activities.StatisticsActivity;
import com.beehive.objects.Building;
import com.beehive.objects.Location;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentMap extends Fragment implements OnInfoWindowClickListener, InfoWindowAdapter,  OnMapClickListener {

	private static View view;
	// Google Map
	private GoogleMap googleMap;
	//Data
	private ArrayList<Building> buildings;
	private ArrayList<String> buildingList;
	private HashMap<String,ArrayList<MarkerOptions>> buildingMarkers;
	private ArrayList<Marker> activeSubMarkers;
	private Marker activeMarker;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//Data Request
		requestData();
		//Map set up
		initMap(inflater, container);
		//Marker building
		setMarkers();
		return view;
	}

	private void requestData(){
		//Creation des objects Locations
		Location loc1 = new Location(1, "Starbucks", "http://lala/", 33.774258, -84.396348, 0.80, "now");
		Location loc2 = new Location(2, "Conference Room", "http://lala/", 33.774930, -84.396269, 0.20, "in 5mn");
		Location loc3 = new Location(3, "Big Hall", "http://lala/", 33.774635, -84.396465, 0.65, "tommorow");
		Location loc4 = new Location(4, "Meeting Room", "http://lala/", 33.775038, -84.396449, 0.10, "now");
		ArrayList<Location> locations = new ArrayList<Location>();
		locations.add(loc1);
		locations.add(loc2);
		locations.add(loc3);
		locations.add(loc4);
		//Creation des objects Buildings
		Building building1 = new Building(1, "Clough", 33.774778, -84.396395, locations);
		buildings = new ArrayList<Building>();
		buildings.add(building1);
		//Creation de la list buildings
		buildingList = new ArrayList<String>();
		buildingList.add(building1.getName());
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
			googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(GeorgiaTech , 14.5f) );
			//Default marker
			for(int i=0;i<buildings.size();i++){
				Building currentBuilding = buildings.get(i);
				googleMap.addMarker(new MarkerOptions()
				.position(new LatLng(currentBuilding.getLatitude(),currentBuilding.getLongitude()))
				.title(currentBuilding.getName()))
				.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
			}
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}
		googleMap.setInfoWindowAdapter(this);
		googleMap.setOnInfoWindowClickListener(this);
		googleMap.setOnMapClickListener(this);
	}

	private void setMarkers(){
		//Creation Hashmap: Building=>markers
		buildingMarkers = new HashMap<String,ArrayList<MarkerOptions>>();
		ArrayList<MarkerOptions> arrayMarkers = new ArrayList<MarkerOptions>();
		for(int i=0;i<buildings.get(0).getLocations().size();i++){
			Location location = buildings.get(0).getLocations().get(i);
			MarkerOptions markerOptions = new MarkerOptions()
			.position(new LatLng(location.getLatitude(), location.getLongitude()))
			.title(location.getName())
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
			arrayMarkers.add(markerOptions);
		}
		buildingMarkers.put(buildings.get(0).getName(), arrayMarkers);
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		if(activeSubMarkers == null){
			activeSubMarkers = new ArrayList<Marker>();
		}
		else{
			activeSubMarkers.clear();
		}
		String markerClickedName = marker.getTitle();
		if (buildingList.contains(markerClickedName)){	
			marker.setVisible(false);
			ArrayList<MarkerOptions> markers = buildingMarkers.get(markerClickedName);
			for(int i=0;i<markers.size();i++){
				Marker currentMarker = googleMap.addMarker(markers.get(i));
				activeSubMarkers.add(currentMarker);
			}
			googleMap.animateCamera( CameraUpdateFactory.newLatLngZoom(marker.getPosition() , 19.1f) );
		}
		else{
			Intent intent = new Intent(getActivity(), StatisticsActivity.class);
			intent.putExtra("TITLE", marker.getTitle());
			intent.putExtra("SUBTITLE", "description to do ...");
			startActivity(intent);
		}
	}

	@Override
	public View getInfoContents(Marker marker) {
		View view;
		String markerClickedName = marker.getTitle();
		// Getting view from the layout file info_window_layout
		if (buildingList.contains(markerClickedName)){
			activeMarker = marker;
			view = getActivity().getLayoutInflater().inflate(R.layout.map_infowindow_building, null);
			TextView buildingName = (TextView)view.findViewById(R.id.building_name);
			buildingName.setText(marker.getTitle());
		}
		else{
			view = getActivity().getLayoutInflater().inflate(R.layout.map_infowindow_location, null);
			TextView locationName = (TextView)view.findViewById(R.id.location_name);
			locationName.setText(marker.getTitle());
		}
		return view;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

	@Override
	public void onMapClick(LatLng latLng) {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("Reset position?");
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Log.v("XXXX",activeSubMarkers.toString());
				for(int i=0;i< activeSubMarkers.size();i++){
					activeSubMarkers.get(i).setVisible(false);
				}
				activeMarker.setVisible(true);
				//final LatLng GeorgiaTech = new LatLng(33.775771, -84.396302);
				googleMap.animateCamera( CameraUpdateFactory.newLatLngZoom(activeMarker.getPosition() , 14.5f) );
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		alert.show();
	}
}