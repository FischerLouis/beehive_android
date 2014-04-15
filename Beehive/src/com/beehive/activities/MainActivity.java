package com.beehive.activities;

import java.util.ArrayList;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.beehive.fragments.FragmentList;
import com.beehive.fragments.FragmentMap;
import com.beehive.tools.Constants;
import com.beehive.tools.FragmentListCommunicator;
import com.beehive.tools.FragmentMapCommunicator;
import com.beehive.tools.VolleySingleton;
import com.beehive.R;

import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener, OnQueryTextListener {

	private Menu optionsMenu;
	private RequestQueue queue;
	private boolean isRefreshing = false;
	private SharedPreferences prefs;

	public static ArrayList<Fragment> listFragments;
	private SectionsPagerAdapter sectionsPagerAdapter;
	private ViewPager viewPager;
	public FragmentMapCommunicator fragmentMapCommunicator;
	public FragmentListCommunicator fragmentListCommunicator;

	// on Activity
	public boolean loadMapFromCache = false;
	public boolean loadListFromCache = false;
	public JSONArray jsonCached;

	private int curTabId = 0;
	
	private SearchView searchView;

	TabListener tabListener = this;
	Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//INIT QUEUE
		queue = VolleySingleton.getInstance().getRequestQueue();
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager with the sections adapter.
		viewPager = (ViewPager) findViewById(R.id.pager);
		listFragments = new ArrayList<Fragment>();
		FragmentMap fragmentMap = FragmentMap.newInstance();
		FragmentList fragmentList = FragmentList.newInstance();
		listFragments.add(fragmentMap);
		listFragments.add(fragmentList);
		sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),listFragments);
		viewPager.setAdapter(sectionsPagerAdapter);

		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});
		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab().setText(sectionsPagerAdapter.getPageTitle(i)).setTabListener(tabListener));
		}
	}

	@Override
	public void onPause() {
		super.onPause();  // Always call the superclass method first
		prefs.edit().putInt("curTabId", curTabId).commit();
	}

	@Override
	public void onResume() {
		super.onResume();  // Always call the superclass method first
		// PREFERENCES
		prefs = this.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);

		//SELECT PREVIOUS TAB (IF EXIST)
		if(prefs.contains("curTabId")){
			viewPager.setCurrentItem(prefs.getInt("curTabId", 0));
		}
		// Init Requests Queue
		// queue = Volley.newRequestQueue(this);
		// REQUESTS DATA (STATIC & REALTIME)
		dataRequest();
	}

	private void setRefreshActionButtonState(final boolean refreshing) {
		if (optionsMenu != null) {
			final MenuItem refreshItem = optionsMenu.findItem(R.id.action_refresh);
			if (refreshItem != null) {
				if (refreshing) {
					refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
					refreshItem.expandActionView();
				} else {
					refreshItem.collapseActionView();
					refreshItem.setActionView(null);
				}
			}
		}
	}

	// STATIC DATA REQUEST
	private void dataRequest(){

		// CACHE
		if(queue.getCache().get(Constants.URL_STATIC)!=null){
			Log.v("REQUEST","Cache");
			//Static Data from Cache
			try {				
				String cachedResponse = new String(queue.getCache().get(Constants.URL_STATIC).data);
				jsonCached = new JSONArray(cachedResponse);
				loadMapFromCache = true;
				loadListFromCache = true;
				realTimeDataRequest();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// NO CACHE
		else{
			Log.v("REQUEST","No Cache");			
			//Loading Windows
			final ProgressDialog progress = new ProgressDialog(this);
			progress.setTitle("Loading");
			progress.setMessage("Wait while loading...");
			progress.show();
			//Static Data from Server
			JsonArrayRequest staticDataReq = new JsonArrayRequest(Constants.URL_STATIC, new Response.Listener<JSONArray>(){
				@Override
				public void onResponse(JSONArray response) {
					if(fragmentMapCommunicator != null)
						fragmentMapCommunicator.passStaticData(response);
					if(fragmentListCommunicator != null)
						fragmentListCommunicator.passStaticData(response);
					realTimeDataRequest();	
					progress.dismiss();
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Toast.makeText(context, "Request error ... Check your data connection and try again", Toast.LENGTH_SHORT).show();
					VolleyLog.e("Error: ", error.getMessage());
				}
			});
			queue.add(staticDataReq);
		}
	}

	// RealTime Request
	private void realTimeDataRequest(){
		JsonArrayRequest realTimeDataReq = new JsonArrayRequest(Constants.URL_DYNAMIC_NOW, new Response.Listener<JSONArray>(){
			@Override
			public void onResponse(JSONArray response) {
				if(fragmentMapCommunicator != null)
					fragmentMapCommunicator.passRealTimeData(response);	
				if(fragmentListCommunicator != null)
					fragmentListCommunicator.passRealTimeData(response);	
				if(isRefreshing){
					setRefreshActionButtonState(false);
					isRefreshing = false;
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(context, "Request error ... Check your data connection and try again", Toast.LENGTH_SHORT).show();
				VolleyLog.e("Error: ", error.getMessage());
			}
		});
		queue.add(realTimeDataReq);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.optionsMenu = menu;
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_actions, menu);
				
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextListener(this);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		viewPager.setCurrentItem(tab.getPosition());
		curTabId = tab.getPosition();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch(itemId){
		case R.id.action_search:
			//TODO
			break;
		case R.id.action_refresh:
			isRefreshing = true;
			setRefreshActionButtonState(true);
			item.expandActionView();
			realTimeDataRequest();
			break;
		default:
			break;
		}
		return true;
	}

	public class SectionsPagerAdapter  extends FragmentStatePagerAdapter  {
		private ArrayList<Fragment> list;
		public SectionsPagerAdapter (FragmentManager fm, ArrayList<Fragment> list) {
			super(fm);
			this.list = list;
		}

		@Override
		public Fragment getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	@Override
	public boolean onQueryTextSubmit(String textSearched) {
		if(fragmentListCommunicator != null)
			fragmentListCommunicator.passQueryText(textSearched, true);
		searchView.clearFocus();
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if(fragmentListCommunicator != null)
			fragmentListCommunicator.passQueryText(newText, false);
		if(fragmentMapCommunicator != null)
			fragmentMapCommunicator.passQueryTextChange(newText);
		return false;
	}
}
