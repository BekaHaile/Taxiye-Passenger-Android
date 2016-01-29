package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.text.DecimalFormat;

import product.clicklabs.jugnoo.adapters.EndRideDiscountsAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Utils;


public class RideSummaryActivity extends BaseFragmentActivity implements FlurryEventNames, Constants {

    RelativeLayout relative;

	RelativeLayout relativeLayoutMap;
	GoogleMap mapLite;

    TextView textViewTitle;
    ImageView imageViewBack;

	//ProgressWheel progressWheel;

	RelativeLayout relativeLayoutRideSummary;
	ScrollView scrollViewEndRide;

	TextView textViewEndRideDriverName, textViewEndRideDriverCarNumber;
	RelativeLayout relativeLayoutLuggageCharge, relativeLayoutConvenienceCharge,
		relativeLayoutEndRideDiscount, relativeLayoutPaidUsingJugnooCash, relativeLayoutPaidUsingPaytm;
	LinearLayout linearLayoutEndRideTime;
	RelativeLayout relativeLayoutEndRideWaitTime;
	NonScrollListView listViewEndRideDiscounts;
	TextView textViewEndRideFareValue, textViewEndRideLuggageChargeValue, textViewEndRideConvenienceChargeValue,
			textViewEndRideDiscount, textViewEndRideDiscountValue,
			textViewEndRideFinalFareValue, textViewEndRideJugnooCashValue, textViewEndRidePaytmValue, textViewEndRideToBePaidValue, textViewEndRideBaseFareValue,
			textViewEndRideDistanceValue, textViewEndRideTime, textViewEndRideTimeValue, textViewEndRideWaitTimeValue, textViewEndRideFareFactorValue;
	TextView textViewEndRideStartLocationValue, textViewEndRideEndLocationValue, textViewEndRideStartTimeValue, textViewEndRideEndTimeValue;
	Button buttonEndRideOk;
	EndRideDiscountsAdapter endRideDiscountsAdapter;
	private ImageView imageViewJugnooAnimation;
	private AnimationDrawable jugnooAnimation;

	EndRideData endRideData = null;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_summary);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

		relativeLayoutMap = (RelativeLayout) findViewById(R.id.relativeLayoutMap); relativeLayoutMap.setVisibility(View.GONE);
		mapLite = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLite)).getMap();
		if (mapLite != null) {
			mapLite.getUiSettings().setAllGesturesEnabled(false);
			mapLite.getUiSettings().setZoomControlsEnabled(false);
			mapLite.setMyLocationEnabled(false);
			mapLite.getUiSettings().setTiltGesturesEnabled(false);
			mapLite.getUiSettings().setMyLocationButtonEnabled(false);
			mapLite.setMapType(GoogleMap.MAP_TYPE_NORMAL);

			mapLite.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					return true;
				}
			});

			if (Utils.compareDouble(Data.latitude, 0) == 0 && Utils.compareDouble(Data.longitude, 0) == 0) {
				mapLite.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.7500, 76.7800), 15));
			} else {
				mapLite.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Data.latitude, Data.longitude), 15));
			}
		}

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.mavenRegular(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

		imageViewJugnooAnimation = (ImageView)findViewById(R.id.imageViewJugnooAnimation);
		jugnooAnimation = (AnimationDrawable) imageViewJugnooAnimation.getBackground();

		relativeLayoutRideSummary = (RelativeLayout) findViewById(R.id.relativeLayoutRideSummary); relativeLayoutRideSummary.setVisibility(View.GONE);
		scrollViewEndRide = (ScrollView) findViewById(R.id.scrollViewEndRide);

		textViewEndRideDriverName = (TextView) findViewById(R.id.textViewEndRideDriverName); textViewEndRideDriverName.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideDriverCarNumber = (TextView) findViewById(R.id.textViewEndRideDriverCarNumber); textViewEndRideDriverCarNumber.setTypeface(Fonts.mavenRegular(this));

		textViewEndRideStartLocationValue = (TextView) findViewById(R.id.textViewEndRideStartLocationValue); textViewEndRideStartLocationValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideEndLocationValue = (TextView) findViewById(R.id.textViewEndRideEndLocationValue); textViewEndRideEndLocationValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideStartTimeValue = (TextView) findViewById(R.id.textViewEndRideStartTimeValue); textViewEndRideStartTimeValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideEndTimeValue = (TextView) findViewById(R.id.textViewEndRideEndTimeValue); textViewEndRideEndTimeValue.setTypeface(Fonts.mavenRegular(this));

		textViewEndRideFareValue = (TextView) findViewById(R.id.textViewEndRideFareValue); textViewEndRideFareValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideDiscountValue = (TextView) findViewById(R.id.textViewEndRideDiscountValue); textViewEndRideDiscountValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideFinalFareValue = (TextView) findViewById(R.id.textViewEndRideFinalFareValue); textViewEndRideFinalFareValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideJugnooCashValue = (TextView) findViewById(R.id.textViewEndRideJugnooCashValue); textViewEndRideJugnooCashValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRidePaytmValue = (TextView) findViewById(R.id.textViewEndRidePaytmValue); textViewEndRidePaytmValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideToBePaidValue = (TextView) findViewById(R.id.textViewEndRideToBePaidValue); textViewEndRideToBePaidValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideBaseFareValue = (TextView) findViewById(R.id.textViewEndRideBaseFareValue); textViewEndRideBaseFareValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideDistanceValue = (TextView) findViewById(R.id.textViewEndRideDistanceValue); textViewEndRideDistanceValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideTime = (TextView) findViewById(R.id.textViewEndRideTime); textViewEndRideTime.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewEndRideTimeValue = (TextView) findViewById(R.id.textViewEndRideTimeValue); textViewEndRideTimeValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideWaitTimeValue = (TextView) findViewById(R.id.textViewEndRideWaitTimeValue); textViewEndRideWaitTimeValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideFareFactorValue = (TextView) findViewById(R.id.textViewEndRideFareFactorValue); textViewEndRideFareFactorValue.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

		relativeLayoutLuggageCharge = (RelativeLayout) findViewById(R.id.relativeLayoutLuggageCharge);
		relativeLayoutConvenienceCharge = (RelativeLayout) findViewById(R.id.relativeLayoutConvenienceCharge);
		relativeLayoutEndRideDiscount = (RelativeLayout) findViewById(R.id.relativeLayoutEndRideDiscount);
		relativeLayoutPaidUsingJugnooCash = (RelativeLayout) findViewById(R.id.relativeLayoutPaidUsingJugnooCash);
		relativeLayoutPaidUsingPaytm = (RelativeLayout) findViewById(R.id.relativeLayoutPaidUsingPaytm);
		linearLayoutEndRideTime = (LinearLayout) findViewById(R.id.linearLayoutEndRideTime);
		relativeLayoutEndRideWaitTime = (RelativeLayout) findViewById(R.id.relativeLayoutEndRideWaitTime);

		textViewEndRideLuggageChargeValue = (TextView) findViewById(R.id.textViewEndRideLuggageChargeValue); textViewEndRideLuggageChargeValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideConvenienceChargeValue = (TextView) findViewById(R.id.textViewEndRideConvenienceChargeValue); textViewEndRideConvenienceChargeValue.setTypeface(Fonts.mavenRegular(this));
		textViewEndRideDiscount = (TextView) findViewById(R.id.textViewEndRideDiscount); textViewEndRideDiscount.setTypeface(Fonts.mavenRegular(this));

		listViewEndRideDiscounts = (NonScrollListView) findViewById(R.id.listViewEndRideDiscounts);
		endRideDiscountsAdapter = new EndRideDiscountsAdapter(this);
		listViewEndRideDiscounts.setAdapter(endRideDiscountsAdapter);

		buttonEndRideOk = (Button) findViewById(R.id.buttonEndRideOk); buttonEndRideOk.setTypeface(Fonts.mavenRegular(this));



		((TextView) findViewById(R.id.textViewEndRideStartLocation)).setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		((TextView) findViewById(R.id.textViewEndRideEndLocation)).setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		((TextView) findViewById(R.id.textViewEndRideSummary)).setTypeface(Fonts.mavenRegular(this));

		((TextView) findViewById(R.id.textViewEndRideFare)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewEndRideLuggageCharge)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewEndRideConvenienceCharge)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewEndRideFinalFare)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewEndRideJugnooCash)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewEndRidePaytm)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewEndRideToBePaid)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewEndRideBaseFare)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewEndRideDistance)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewEndRideTime)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewEndRideWaitTime)).setTypeface(Fonts.mavenRegular(this));


        imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		buttonEndRideOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		try {
			Intent intent = getIntent();
			if(intent.hasExtra("engagement_id")){
				int engagementId = intent.getIntExtra("engagement_id", 0);
				if(0 == engagementId){
					throw new Exception();
				}
				else{
					getRideSummaryAPI(this, ""+engagementId);
				}
			}
			else if(intent.hasExtra(KEY_END_RIDE_DATA)){
				endRideData = Data.endRideData;
				setRideData();
			}
			else{
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
			performBackPressed();
		}

	}




	public void setRideData(){
		try{
			if(endRideData != null){
				DecimalFormat decimalFormat = new DecimalFormat("#.##");
				DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#");
				scrollViewEndRide.scrollTo(0, 0);

				relativeLayoutMap.setVisibility(View.VISIBLE);
				relativeLayoutRideSummary.setVisibility(View.VISIBLE);

				textViewEndRideDriverName.setText(endRideData.driverName);
				textViewEndRideDriverCarNumber.setText(endRideData.driverCarNumber);

				textViewEndRideStartLocationValue.setText(endRideData.pickupAddress);
				textViewEndRideEndLocationValue.setText(endRideData.dropAddress);

				textViewEndRideStartTimeValue.setText(endRideData.pickupTime);
				textViewEndRideEndTimeValue.setText(endRideData.dropTime);

				textViewEndRideFareValue.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(endRideData.fare)));

				if(Utils.compareDouble(endRideData.luggageCharge, 0) > 0){
					relativeLayoutLuggageCharge.setVisibility(View.VISIBLE);
					textViewEndRideLuggageChargeValue.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(endRideData.luggageCharge)));
				} else{
					relativeLayoutLuggageCharge.setVisibility(View.GONE);
				}

				if(Utils.compareDouble(endRideData.convenienceCharge, 0) > 0){
					relativeLayoutConvenienceCharge.setVisibility(View.VISIBLE);
					textViewEndRideConvenienceChargeValue.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(endRideData.convenienceCharge)));
				} else{
					relativeLayoutConvenienceCharge.setVisibility(View.GONE);
				}

				if(endRideData.discountTypes.size() > 1){
					listViewEndRideDiscounts.setVisibility(View.VISIBLE);
					endRideDiscountsAdapter.setList(endRideData.discountTypes);
					textViewEndRideDiscount.setText("Discounts");
					textViewEndRideDiscountValue.setVisibility(View.GONE);
					relativeLayoutEndRideDiscount.setVisibility(View.VISIBLE);
				}
				else if(endRideData.discountTypes.size() > 0){
					listViewEndRideDiscounts.setVisibility(View.GONE);
					textViewEndRideDiscount.setText(endRideData.discountTypes.get(0).name);
					textViewEndRideDiscountValue.setVisibility(View.VISIBLE);
					textViewEndRideDiscountValue.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(endRideData.discount)));
					relativeLayoutEndRideDiscount.setVisibility(View.VISIBLE);
				}
				else{
					listViewEndRideDiscounts.setVisibility(View.GONE);
					textViewEndRideDiscount.setText("Discounts");
					textViewEndRideDiscountValue.setVisibility(View.VISIBLE);
					textViewEndRideDiscountValue.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(endRideData.discount)));
					if(endRideData.discount > 0){
						relativeLayoutEndRideDiscount.setVisibility(View.VISIBLE);
					} else{
						relativeLayoutEndRideDiscount.setVisibility(View.GONE);
					}
				}

				textViewEndRideFinalFareValue.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(endRideData.finalFare)));

				if(Utils.compareDouble(endRideData.paidUsingWallet, 0) > 0){
					relativeLayoutPaidUsingJugnooCash.setVisibility(View.VISIBLE);
					textViewEndRideJugnooCashValue.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(endRideData.paidUsingWallet)));
				} else{
					relativeLayoutPaidUsingJugnooCash.setVisibility(View.GONE);
				}
				if(Utils.compareDouble(endRideData.paidUsingPaytm, 0) > 0){
					relativeLayoutPaidUsingPaytm.setVisibility(View.VISIBLE);
					textViewEndRidePaytmValue.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(endRideData.paidUsingPaytm)));
				} else{
					relativeLayoutPaidUsingPaytm.setVisibility(View.GONE);
				}

				textViewEndRideToBePaidValue.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), Utils.getMoneyDecimalFormat().format(endRideData.toPay)));


				textViewEndRideFareFactorValue.setText(String.format(getResources().getString(R.string.priority_tip_format), decimalFormat.format(endRideData.fareFactor)));
				textViewEndRideBaseFareValue.setText(String.format(getResources().getString(R.string.rupees_value_format), Utils.getMoneyDecimalFormat().format(endRideData.baseFare)));
				double totalDistanceInKm = endRideData.distance;
				String kmsStr = "";
				if (totalDistanceInKm > 1) {
					kmsStr = "kms";
				} else {
					kmsStr = "km";
				}
				textViewEndRideDistanceValue.setText("" + decimalFormat.format(totalDistanceInKm) + " " + kmsStr);
				if(endRideData.rideTime > -1){
					linearLayoutEndRideTime.setVisibility(View.VISIBLE);
					textViewEndRideTimeValue.setText(decimalFormatNoDecimal.format(endRideData.rideTime) + " min");
				} else{
					linearLayoutEndRideTime.setVisibility(View.GONE);
				}
				if(endRideData.waitingChargesApplicable == 1 || endRideData.waitTime > 0){
					relativeLayoutEndRideWaitTime.setVisibility(View.VISIBLE);
					textViewEndRideWaitTimeValue.setText(decimalFormatNoDecimal.format(endRideData.waitTime) + " min");
					textViewEndRideTime.setText("Total");
				}
				else{
					relativeLayoutEndRideWaitTime.setVisibility(View.GONE);
					textViewEndRideTime.setText("Time");
				}

			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}



	public void getRideSummaryAPI(final Activity activity, final String engagementId) {
		if (!HomeActivity.checkIfUserDataNull(activity)) {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				imageViewJugnooAnimation.setVisibility(View.VISIBLE);
				jugnooAnimation.start();
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				params.put("engagement_id", engagementId);
				AsyncHttpClient client = Data.getClient();
				client.post(Config.getServerUrl() + "/get_ride_summary", params,
						new CustomAsyncHttpResponseHandler() {
							private JSONObject jObj;

							@Override
							public void onFailure(Throwable arg3) {
								imageViewJugnooAnimation.setVisibility(View.GONE);
								jugnooAnimation.stop();
								endRideRetryDialog(activity, engagementId, Data.SERVER_NOT_RESOPNDING_MSG);
							}

							@Override
							public void onSuccess(String response) {
								Log.i("Server response get_ride_summary", "response = " + response);
								imageViewJugnooAnimation.setVisibility(View.GONE);
								jugnooAnimation.stop();
								try {
									jObj = new JSONObject(response);
									if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
										int flag = jObj.getInt("flag");
										if (ApiResponseFlags.RIDE_ENDED.getOrdinal() == flag) {
											endRideData = JSONParser.parseEndRideData(jObj, engagementId, Data.fareStructure.fixedFare);
											setRideData();
										} else {
											endRideRetryDialog(activity, engagementId, Data.SERVER_ERROR_MSG);
										}
									}
								} catch (Exception exception) {
									exception.printStackTrace();
									endRideRetryDialog(activity, engagementId, Data.SERVER_ERROR_MSG);
								}
							}
						});
			} else {
				endRideRetryDialog(activity, engagementId, Data.CHECK_INTERNET_MSG);
			}
		}
	}

	public void endRideRetryDialog(final Activity activity, final String engagementId, String errorMessage) {
		DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", errorMessage, "Retry", "Cancel", new OnClickListener() {
			@Override
			public void onClick(View v) {
				getRideSummaryAPI(activity, engagementId);
			}
		}, new OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		}, false, false);
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

}
