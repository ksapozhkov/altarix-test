package com.example.altarixtestapp;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GPSTrackingActivity extends FragmentActivity {

	private final static String TAB_USER_COORDINATES_TAG = "user_coordinates_tab";
	private final static String TAB_MAP_TAG = "map_tab";
	private ImageView startTrackLocation;
	private GPSTracker gps;
	private GoogleMap map;
	private List<Marker> markers;
	private ListView userCoordinatesList;
	private GPSAdapter adapter;
	private String currentTabTag;
	private boolean isTrack;
	private Thread thread;
	private double previousLat;
	private double previousLong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		currentTabTag = TAB_USER_COORDINATES_TAG;
		initTabs();
		isTrack = false;
		startTrackLocation = (ImageView) findViewById(R.id.startTrakLocation);

		// show location button click event
		startTrackLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (isTrack) {
					isTrack = false;
					startTrackLocation.setImageResource(R.drawable.btn_play);
					stopUsingGPS();
				} else {
					isTrack = true;
					startTrackLocation.setImageResource(R.drawable.btn_pause);
					// creating GPS Class object

					final Handler mHandler1 = new Handler();

					thread = new Thread(new Runnable() {
						@Override
						public void run() {
							while (isTrack) {
								try {
									Thread.sleep(10000);
									mHandler1.post(new Runnable() {

										@Override
										public void run() {
											gps = new GPSTracker(
													GPSTrackingActivity.this);
											// check if GPS location have
											// some
											// values
											if (gps.canGetLocation()) {

												double currentlat = gps
														.getLatitude();
												double currentlong = gps
														.getLongitude();
												onLocationChanged(currentlat,
														currentlong);

											} else {
												// GPS or Network is not
												// enabled
												// Ask user to enable
												// GPS/network in
												// settings
												gps.showSettingsAlert();
											}
										}
									});

								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						}
					});
					thread.start();
				}
			}
		});

		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		markers = new ArrayList<Marker>();
		userCoordinatesList = (ListView) findViewById(R.id.list_user_coordinates);
		adapter = new GPSAdapter(getApplicationContext(), R.layout.list_row,
				markers);
		userCoordinatesList.setAdapter(adapter);
	}

	private void initTabs() {
		TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
		tabs.setup();
		TabHost.TabSpec spec = tabs.newTabSpec(TAB_USER_COORDINATES_TAG);
		spec.setContent(R.id.tab_user_coordinates);
		spec.setIndicator("Координаты");
		tabs.addTab(spec);
		spec = tabs.newTabSpec(TAB_MAP_TAG);
		spec.setContent(R.id.tab_map);
		spec.setIndicator("Карта");
		tabs.addTab(spec);
		tabs.setCurrentTab(0);
		tabs.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				currentTabTag = tabId;
				refreshMap();
			}
		});

	}

	public void onLocationChanged(double latitude, double longitude) {

		// need to prevent the addition of the same values
		if (markers.isEmpty()
				|| (previousLat != latitude && previousLong != longitude)) {
			previousLat = latitude;
			previousLong = longitude;
			LatLng position = new LatLng(latitude, longitude);
			Marker newMarker = map.addMarker(new MarkerOptions()
					.position(position));
			markers.add(newMarker);
			adapter.notifyDataSetChanged();
			refreshMap();
		}
	}

	private void refreshMap() {
		if (TAB_MAP_TAG.equals(currentTabTag)) {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			if (!markers.isEmpty()) {
				for (Marker marker : markers) {
					builder.include(marker.getPosition());
				}
				LatLngBounds bounds = builder.build();

				try {
					map.animateCamera(CameraUpdateFactory.newLatLngBounds(
							bounds, 20));
				} catch (Exception e) {
					// TODO: proceed it
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		stopUsingGPS();
		super.onBackPressed();
	}

	private void stopUsingGPS() {
		if (gps != null) {
			thread.interrupt();
			gps.stopUsingGPS();
		}
	}

}
