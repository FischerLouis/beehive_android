package com.beehive.fragments;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.beehive.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentMap extends Fragment {

	private static View view;
	// Google Map
    private GoogleMap googleMap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    if (view != null) {
	        ViewGroup parent = (ViewGroup) view.getParent();
	        if (parent != null)
	            parent.removeView(view);
	    }
	    try {
	        view = inflater.inflate(R.layout.fragment_map, container, false);
	        initMap(googleMap);
	    } catch (InflateException e) {
	        /* map is already there, just return view as it is */
	    }
	    return view;
	}
	
	private void initMap(GoogleMap googleMap){
		googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		//Enable Self Location
        googleMap.setMyLocationEnabled(true);
        //Default position and zoom (Georgia Tech)
        final LatLng GeorgiaTech = new LatLng(33.775771, -84.396302);
        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(GeorgiaTech , 14.5f) );
        //Default marker on Clough
        final LatLng Clough = new LatLng(33.775771, -84.396302);
        MarkerOptions marker = new MarkerOptions().position(Clough).title("Clough");
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        googleMap.addMarker(marker);
        WindowAdapter windowAdapter = new WindowAdapter();
        googleMap.setInfoWindowAdapter(windowAdapter);
        //MapClickListener mapClickListener = new MapClickListener();
        //googleMap.setOnMapClickListener(mapClickListener);
	}
	
	public class WindowAdapter implements InfoWindowAdapter{

		@Override
		public View getInfoContents(Marker marker) {
			// Getting view from the layout file info_window_layout
            View view = getActivity().getLayoutInflater().inflate(R.layout.map_info_window, null);
			return view;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			return null;
		}
	}
	
	/*public class MapClickListener implements OnMapClickListener{

		@Override
		public void onMapClick(LatLng latlng) {
			// Clears any existing markers from the GoogleMap
            googleMap.clear();
         // Showing InfoWindow on the GoogleMap
            marker.showInfoWindow();
		}

	}*/
}