package com.sabkuchfresh.home;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.tsengvn.typekit.TypekitContextWrapper;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 26/03/17.
 */

public class TrackOrderActivity extends AppCompatActivity {

	private final String TAG = TrackOrderActivity.class.getSimpleName();

	private String accessToken;
	private int orderId, deliveryId;
	private LatLng pickupLatLng, deliveryLatLng;

	private TextView tvTitle;
	private ImageView ivBack;
	private GoogleMap googleMap;
	private TextView tvTrackingInfo, tvETA;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_order);

		accessToken = getIntent().hasExtra(Constants.KEY_ACCESS_TOKEN) ? getIntent().getStringExtra(Constants.KEY_ACCESS_TOKEN) : "";
		orderId = getIntent().getIntExtra(Constants.KEY_ORDER_ID, 0);
		deliveryId = getIntent().getIntExtra(Constants.KEY_DELIVERY_ID, 0);
		pickupLatLng = new LatLng(getIntent().getDoubleExtra(Constants.KEY_PICKUP_LATITUDE, 0d),
				getIntent().getDoubleExtra(Constants.KEY_PICKUP_LONGITUDE, 0d));
		deliveryLatLng = new LatLng(getIntent().getDoubleExtra(Constants.KEY_DELIVERY_LATITUDE, 0d),
				getIntent().getDoubleExtra(Constants.KEY_DELIVERY_LONGITUDE, 0d));

		tvTitle = (TextView) findViewById(R.id.tvTitle); tvTitle.setTypeface(tvTitle.getTypeface(), Typeface.BOLD);
		ivBack = (ImageView) findViewById(R.id.ivBack);
		tvTrackingInfo = (TextView) findViewById(R.id.tvTrackingInfo); tvTrackingInfo.setVisibility(View.GONE);
		tvETA = (TextView) findViewById(R.id.tvETA); tvETA.setVisibility(View.GONE);

		tvTitle.setText(getString(R.string.order_id_format, String.valueOf(orderId)));


		((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMap) {
				TrackOrderActivity.this.googleMap = googleMap;
				if (googleMap != null) {
					googleMap.setMyLocationEnabled(true);
					googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

					LatLngBounds.Builder llbBuilder = new LatLngBounds.Builder();
					llbBuilder.include(pickupLatLng).include(deliveryLatLng);
					LatLngBounds latLngBounds = llbBuilder.build();

					googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));

					googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
						@Override
						public boolean onMarkerClick(Marker marker) {
							return true;
						}
					});
				}
			}
		});

	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
	}


}
