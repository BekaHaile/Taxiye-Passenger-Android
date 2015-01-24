package com.example.androidgpsmetering;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AndroidGPSMeteringActivity extends Activity implements GpsDistanceUpdater{

	TextView distanceDisplayTextView;
	Button startTrackingButton, stopTrackingButton, forward;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_android_gpsmetering);
		
		distanceDisplayTextView = (TextView) findViewById(R.id.distanceDisplayTextView);
		
		startTrackingButton = (Button) findViewById(R.id.startTrackingButton);
		stopTrackingButton = (Button) findViewById(R.id.stopTrackingButton);
		forward = (Button) findViewById(R.id.forward);
		
		Log.e("startm ", ";asdml;ikju;");
		gpsInstance();
		
		startTrackingButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gpsInstance().start();
			}
		});
		
		stopTrackingButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gpsInstance().stop();
			}
		});
		
		forward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(AndroidGPSMeteringActivity.this, SecondActivity.class));
				finish();
				overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
			}
		});
		
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		gpsInstance().resume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		gpsInstance().pause();
	}
	
	public GpsDistanceCalculator gpsInstance(){
		return GpsDistanceCalculator.getInstance(AndroidGPSMeteringActivity.this, AndroidGPSMeteringActivity.this, GpsDistanceCalculator.getInstance(AndroidGPSMeteringActivity.this, AndroidGPSMeteringActivity.this, -1).getSavedTotalDistanceFromSP(AndroidGPSMeteringActivity.this));
	}

	@Override
	public void updateDistance(double distance) {
		distanceDisplayTextView.setText("Total distance = "+distance);
	}



}
