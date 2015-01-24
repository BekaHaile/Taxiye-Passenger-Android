package com.example.androidgpsmetering;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends Activity implements GpsDistanceUpdater{

	TextView distanceDisplayTextView;
	Button startTrackingButton, stopTrackingButton, back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		
		distanceDisplayTextView = (TextView) findViewById(R.id.distanceDisplayTextView);
		
		startTrackingButton = (Button) findViewById(R.id.startTrackingButton);
		stopTrackingButton = (Button) findViewById(R.id.stopTrackingButton);
		back = (Button) findViewById(R.id.back);
		
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
		
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SecondActivity.this, AndroidGPSMeteringActivity.class));
				finish();
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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
		return GpsDistanceCalculator.getInstance(SecondActivity.this, SecondActivity.this, GpsDistanceCalculator.getInstance(SecondActivity.this, SecondActivity.this, -1).getSavedTotalDistanceFromSP(SecondActivity.this));
	}

	@Override
	public void updateDistance(double distance) {
		distanceDisplayTextView.setText("Total distance = "+distance);
	}


}
