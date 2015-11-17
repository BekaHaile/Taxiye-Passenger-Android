package product.clicklabs.jugnoo;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.HttpRequester;
import product.clicklabs.jugnoo.utils.KeyBoardStateHandler;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class FareEstimateActivity extends BaseFragmentActivity implements FlurryEventNames, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    LinearLayout relative;

    TextView textViewTitle;
    ImageView imageViewBack;

    RelativeLayout relativeLayoutDropLocationBar;
    EditText editTextDropLocation;
    ProgressWheel progressBarDropLocation;
    ListView listViewDropLocationSearch;
	LinearLayout linearLayoutScroll;
	TextView textViewScroll;
	ScrollView scrollView;


    RelativeLayout relativeLayoutFareEstimateDetails;
    GoogleMap mapLite;
    TextView textViewPickupLocation, textViewDropLocation, textViewEstimateTime, textViewEstimateDistance,
			textViewEstimateFare, textViewConvenienceCharge, textViewEstimateFareNote;
    Button buttonOk;

    public ASSL assl;

	private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_estimate);

		mGoogleApiClient = new GoogleApiClient
				.Builder(this)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

        relative = (LinearLayout) findViewById(R.id.relative);
        assl = new ASSL(this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);


        relativeLayoutDropLocationBar = (RelativeLayout) findViewById(R.id.relativeLayoutDropLocationBar);
        editTextDropLocation = (EditText) findViewById(R.id.editTextDropLocation);
        editTextDropLocation.setTypeface(Fonts.latoRegular(this));
        progressBarDropLocation = (ProgressWheel) findViewById(R.id.progressBarDropLocation);
        progressBarDropLocation.setVisibility(View.GONE);
        listViewDropLocationSearch = (ListView) findViewById(R.id.listViewDropLocationSearch);
		linearLayoutScroll = (LinearLayout) findViewById(R.id.linearLayoutScroll);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);
		scrollView = (ScrollView) findViewById(R.id.scrollView);

		linearLayoutScroll.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutScroll, textViewScroll, new KeyBoardStateHandler() {
			@Override
			public void keyboardOpened() {

			}

			@Override
			public void keyBoardClosed() {

			}
		}));

        SearchListAdapter searchListAdapter = new SearchListAdapter(this, editTextDropLocation, new LatLng(30.75, 76.78), mGoogleApiClient,
				new SearchListAdapter.SearchListActionsHandler() {
			@Override
			public void onTextChange(String text) {

			}

			@Override
            public void onSearchPre() {
                progressBarDropLocation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchPost() {
                progressBarDropLocation.setVisibility(View.GONE);
            }

            @Override
            public void onPlaceClick(AutoCompleteSearchResult autoCompleteSearchResult) {

            }

            @Override
            public void onPlaceSearchPre() {
                DialogPopup.showLoadingDialog(FareEstimateActivity.this, "Loading...");
            }

            @Override
            public void onPlaceSearchPost(SearchResult searchResult) {
                getDirectionsAndComputeFare(Data.pickupLatLng, searchResult.latLng);
                FlurryEventLogger.event(FARE_ESTIMATE_CALCULATED);
            }

            @Override
            public void onPlaceSearchError() {
                DialogPopup.dismissLoadingDialog();
            }
        });
        listViewDropLocationSearch.setAdapter(searchListAdapter);

        relativeLayoutFareEstimateDetails = (RelativeLayout) findViewById(R.id.relativeLayoutFareEstimateDetails);

        mapLite = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLite)).getMap();
        if (mapLite != null) {
            mapLite.getUiSettings().setAllGesturesEnabled(false);
            mapLite.getUiSettings().setZoomGesturesEnabled(false);
            mapLite.getUiSettings().setZoomControlsEnabled(false);
            mapLite.setMyLocationEnabled(true);
            mapLite.getUiSettings().setTiltGesturesEnabled(false);
            mapLite.getUiSettings().setMyLocationButtonEnabled(false);
            mapLite.setMapType(GoogleMap.MAP_TYPE_NORMAL);

			mapLite.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					return true;
				}
			});

        }

        textViewPickupLocation = (TextView) findViewById(R.id.textViewPickupLocation);
        textViewPickupLocation.setTypeface(Fonts.latoRegular(this));
        textViewDropLocation = (TextView) findViewById(R.id.textViewDropLocation);
        textViewDropLocation.setTypeface(Fonts.latoRegular(this));
        textViewEstimateTime = (TextView) findViewById(R.id.textViewEstimateTime);
        textViewEstimateTime.setTypeface(Fonts.latoRegular(this));
        textViewEstimateDistance = (TextView) findViewById(R.id.textViewEstimateDistance);
        textViewEstimateDistance.setTypeface(Fonts.latoRegular(this));
        textViewEstimateFare = (TextView) findViewById(R.id.textViewEstimateFare);
        textViewEstimateFare.setTypeface(Fonts.latoRegular(this));
		textViewConvenienceCharge = (TextView) findViewById(R.id.textViewConvenienceCharge);
		textViewConvenienceCharge.setTypeface(Fonts.latoRegular(this));
		textViewConvenienceCharge.setText("");
        textViewEstimateFareNote = (TextView) findViewById(R.id.textViewEstimateFareNote);
        textViewEstimateFareNote.setTypeface(Fonts.latoRegular(this));
        buttonOk = (Button) findViewById(R.id.buttonOk);
        buttonOk.setTypeface(Fonts.latoRegular(this));


        relativeLayoutDropLocationBar.setVisibility(View.VISIBLE);
		scrollView.setVisibility(View.VISIBLE);
        relativeLayoutFareEstimateDetails.setVisibility(View.GONE);


        imageViewBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        buttonOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackPressed();
                FlurryEventLogger.event(FARE_RECEIPT_CHECKED);
            }
        });

    }


    private void getDirectionsAndComputeFare(final LatLng sourceLatLng, final LatLng destLatLng) {
        try {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (sourceLatLng != null && destLatLng != null) {
                                String url = MapUtils.makeDirectionsURL(sourceLatLng, destLatLng);
                                Log.i("url", "=" + url);
                                HttpRequester.setTimeouts(30000);
                                String result = new HttpRequester().getJSONFromUrl(url);
                                Log.i("result", "=" + result);
                                if (result != null) {
                                    JSONObject jObj = new JSONObject(result);
                                    final List<LatLng> list = MapUtils.getLatLngListFromPath(result);
                                    if (jObj.getString("status").equalsIgnoreCase("OK") && list.size() > 0) {
                                        final String startAddress = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("start_address");
                                        final String endAddress = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("end_address");

                                        final String distanceText = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
                                        final String timeText = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");

                                        final double distanceValue = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getDouble("value");
                                        final double timeValue = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getDouble("value");

                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                try {

                                                    relativeLayoutDropLocationBar.setVisibility(View.GONE);
													scrollView.setVisibility(View.GONE);
													relativeLayoutFareEstimateDetails.setVisibility(View.VISIBLE);


                                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                                    PolylineOptions polylineOptions = new PolylineOptions();
                                                    polylineOptions.width(ASSL.Xscale() * 5).color(Color.BLUE).geodesic(true);
                                                    for (int z = 0; z < list.size(); z++) {
                                                        polylineOptions.add(list.get(z));
                                                        builder.include(list.get(z));
                                                    }

                                                    final LatLngBounds latLngBounds = builder.build();

                                                    mapLite.clear();
                                                    mapLite.addPolyline(polylineOptions);


                                                    MarkerOptions markerOptionsS = new MarkerOptions();
                                                    markerOptionsS.title("Start");
                                                    markerOptionsS.position(sourceLatLng);
                                                    markerOptionsS.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createSmallPinMarkerBitmap(FareEstimateActivity.this,
                                                        assl, R.drawable.ic_small_pin_start)));
                                                    mapLite.addMarker(markerOptionsS);

                                                    MarkerOptions markerOptionsE = new MarkerOptions();
                                                    markerOptionsE.title("Start");
                                                    markerOptionsE.position(destLatLng);
                                                    markerOptionsE.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createSmallPinMarkerBitmap(FareEstimateActivity.this,
                                                        assl, R.drawable.ic_small_pin_end)));
                                                    mapLite.addMarker(markerOptionsE);


                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                                                                mapLite.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, (int)(minRatio*40)));
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }, 500);


                                                    textViewPickupLocation.setText(startAddress);
                                                    String startAdd = textViewPickupLocation.getText().toString();
                                                    if (startAdd.charAt(startAdd.length() - 1) == ',') {
                                                        textViewPickupLocation.setText(startAdd.substring(0, startAdd.length() - 1));
                                                    }

                                                    textViewDropLocation.setText(endAddress);
                                                    String endAdd = textViewDropLocation.getText().toString();
                                                    if (endAdd.charAt(endAdd.length() - 1) == ',') {
                                                        textViewDropLocation.setText(endAdd.substring(0, endAdd.length() - 1));
                                                    }

                                                    textViewEstimateTime.setText(timeText);
                                                    textViewEstimateDistance.setText(distanceText);


                                                    DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#");
                                                    double computedFare = Data.fareStructure.calculateFare(distanceValue / 1000, timeValue / 60, 0);
                                                    double computedFarePlus = computedFare * 110.0 / 100.0;
                                                    double computedFareMinus = computedFare * 90.0 / 100.0;
                                                    textViewEstimateFare.setText(getResources().getString(R.string.rupee) + " " + decimalFormatNoDecimal.format(computedFareMinus) + " - " +
                                                        getResources().getString(R.string.rupee) + " " + decimalFormatNoDecimal.format(computedFarePlus));

													if(Data.fareStructure.convenienceCharge > 0){
														textViewConvenienceCharge.setText("Convenience Charges "
																+getResources().getString(R.string.rupee)+" "+Utils.getMoneyDecimalFormat().format(Data.fareStructure.convenienceCharge));
													}
													else{
														textViewConvenienceCharge.setText("");
													}


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                DialogPopup.alertPopup(FareEstimateActivity.this, "", "Fare could not be estimated between the selected pickup and drop location");
                                            }
                                        });
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                DialogPopup.dismissLoadingDialog();
                            }
                        });
                    }
                }).start();
            } else {
                DialogPopup.dismissLoadingDialog();
                DialogPopup.alertPopup(FareEstimateActivity.this, "", Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }


    @Override
    protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
        super.onDestroy();
    }

	@Override
	public void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		mGoogleApiClient.disconnect();
		super.onStop();
	}

	@Override
	public void onConnected(Bundle bundle) {
	}

	@Override
	public void onConnectionSuspended(int i) {
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
	}
}
