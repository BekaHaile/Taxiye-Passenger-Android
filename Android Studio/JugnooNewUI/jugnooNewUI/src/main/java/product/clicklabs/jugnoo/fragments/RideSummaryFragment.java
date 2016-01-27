package product.clicklabs.jugnoo.fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.adapters.EndRideDiscountsAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.support.models.GetRideSummaryResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class RideSummaryFragment extends Fragment implements FlurryEventNames, Constants {

	RelativeLayout relative;

	RelativeLayout relativeLayoutMap;
	GoogleMap mapLite;

	RelativeLayout relativeLayoutRideSummary;
	ScrollView scrollViewEndRide;

	TextView textViewEndRideDriverName, textViewEndRideDriverCarNumber;
	RelativeLayout relativeLayoutLuggageCharge, relativeLayoutConvenienceCharge,
			relativeLayoutEndRideDiscount, relativeLayoutPaidUsingJugnooCash, relativeLayoutPaidUsingPaytm;
	LinearLayout linearLayoutEndRideTime, linearLayoutEndRideWaitTime;
	NonScrollListView listViewEndRideDiscounts;
	TextView textViewEndRideFareValue, textViewEndRideLuggageChargeValue, textViewEndRideConvenienceChargeValue,
			textViewEndRideDiscount, textViewEndRideDiscountRupee, textViewEndRideDiscountValue,
			textViewEndRideFinalFareValue, textViewEndRideJugnooCashValue, textViewEndRidePaytmValue, textViewEndRideToBePaidValue, textViewEndRideBaseFareValue,
			textViewEndRideDistanceValue, textViewEndRideTime, textViewEndRideTimeValue, textViewEndRideWaitTimeValue, textViewEndRideFareFactorValue;
	TextView textViewEndRideStartLocationValue, textViewEndRideEndLocationValue, textViewEndRideStartTimeValue, textViewEndRideEndTimeValue;
	Button buttonEndRideOk;
	EndRideDiscountsAdapter endRideDiscountsAdapter;

	EndRideData endRideData = null;
	private int engagementId = 0;
	private OpenMode openMode;

	private View rootView;
    private FragmentActivity activity;

	public RideSummaryFragment(int engagementId, OpenMode openMode){
		this.engagementId = engagementId;
		this.openMode = openMode;
	}


	@Override
	public void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(activity);
	}

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
		FlurryAgent.onEvent(RideSummaryFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ride_summary, container, false);

        activity = getActivity();
		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		new ASSL(activity, relative, 1134, 720, false);

		relativeLayoutMap = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMap); relativeLayoutMap.setVisibility(View.GONE);
		mapLite = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapLite)).getMap();
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

		relativeLayoutRideSummary = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRideSummary); relativeLayoutRideSummary.setVisibility(View.GONE);
		scrollViewEndRide = (ScrollView) rootView.findViewById(R.id.scrollViewEndRide);

		textViewEndRideDriverName = (TextView) rootView.findViewById(R.id.textViewEndRideDriverName); textViewEndRideDriverName.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideDriverCarNumber = (TextView) rootView.findViewById(R.id.textViewEndRideDriverCarNumber); textViewEndRideDriverCarNumber.setTypeface(Fonts.latoRegular(activity));

		textViewEndRideStartLocationValue = (TextView) rootView.findViewById(R.id.textViewEndRideStartLocationValue); textViewEndRideStartLocationValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideEndLocationValue = (TextView) rootView.findViewById(R.id.textViewEndRideEndLocationValue); textViewEndRideEndLocationValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideStartTimeValue = (TextView) rootView.findViewById(R.id.textViewEndRideStartTimeValue); textViewEndRideStartTimeValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideEndTimeValue = (TextView) rootView.findViewById(R.id.textViewEndRideEndTimeValue); textViewEndRideEndTimeValue.setTypeface(Fonts.latoRegular(activity));

		textViewEndRideFareValue = (TextView) rootView.findViewById(R.id.textViewEndRideFareValue); textViewEndRideFareValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideDiscountValue = (TextView) rootView.findViewById(R.id.textViewEndRideDiscountValue); textViewEndRideDiscountValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideFinalFareValue = (TextView) rootView.findViewById(R.id.textViewEndRideFinalFareValue); textViewEndRideFinalFareValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideJugnooCashValue = (TextView) rootView.findViewById(R.id.textViewEndRideJugnooCashValue); textViewEndRideJugnooCashValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRidePaytmValue = (TextView) rootView.findViewById(R.id.textViewEndRidePaytmValue); textViewEndRidePaytmValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideToBePaidValue = (TextView) rootView.findViewById(R.id.textViewEndRideToBePaidValue); textViewEndRideToBePaidValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideBaseFareValue = (TextView) rootView.findViewById(R.id.textViewEndRideBaseFareValue); textViewEndRideBaseFareValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideDistanceValue = (TextView) rootView.findViewById(R.id.textViewEndRideDistanceValue); textViewEndRideDistanceValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideTime = (TextView) rootView.findViewById(R.id.textViewEndRideTime); textViewEndRideTime.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
		textViewEndRideTimeValue = (TextView) rootView.findViewById(R.id.textViewEndRideTimeValue); textViewEndRideTimeValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideWaitTimeValue = (TextView) rootView.findViewById(R.id.textViewEndRideWaitTimeValue); textViewEndRideWaitTimeValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideFareFactorValue = (TextView) rootView.findViewById(R.id.textViewEndRideFareFactorValue); textViewEndRideFareFactorValue.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);

		relativeLayoutLuggageCharge = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutLuggageCharge);
		relativeLayoutConvenienceCharge = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutConvenienceCharge);
		relativeLayoutEndRideDiscount = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutEndRideDiscount);
		relativeLayoutPaidUsingJugnooCash = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaidUsingJugnooCash);
		relativeLayoutPaidUsingPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaidUsingPaytm);
		linearLayoutEndRideTime = (LinearLayout) rootView.findViewById(R.id.linearLayoutEndRideTime);
		linearLayoutEndRideWaitTime = (LinearLayout) rootView.findViewById(R.id.linearLayoutEndRideWaitTime);

		textViewEndRideLuggageChargeValue = (TextView) rootView.findViewById(R.id.textViewEndRideLuggageChargeValue); textViewEndRideLuggageChargeValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideConvenienceChargeValue = (TextView) rootView.findViewById(R.id.textViewEndRideConvenienceChargeValue); textViewEndRideConvenienceChargeValue.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideDiscount = (TextView) rootView.findViewById(R.id.textViewEndRideDiscount); textViewEndRideDiscount.setTypeface(Fonts.latoRegular(activity));
		textViewEndRideDiscountRupee = (TextView) rootView.findViewById(R.id.textViewEndRideDiscountRupee); textViewEndRideDiscountRupee.setTypeface(Fonts.latoRegular(activity));

		listViewEndRideDiscounts = (NonScrollListView) rootView.findViewById(R.id.listViewEndRideDiscounts);
		endRideDiscountsAdapter = new EndRideDiscountsAdapter(activity);
		listViewEndRideDiscounts.setAdapter(endRideDiscountsAdapter);

		buttonEndRideOk = (Button) rootView.findViewById(R.id.buttonEndRideOk); buttonEndRideOk.setTypeface(Fonts.mavenRegular(activity));



		((TextView) rootView.findViewById(R.id.textViewEndRideStartLocation)).setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewEndRideEndLocation)).setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewEndRideSummary)).setTypeface(Fonts.latoRegular(activity));

		((TextView) rootView.findViewById(R.id.textViewEndRideFare)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideFareRupee)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideLuggageCharge)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideLuggageChargeRupee)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideConvenienceCharge)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideConvenienceChargeRupee)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideFinalFare)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideFinalFareRupee)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideJugnooCash)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideJugnooCashRupee)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRidePaytm)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRidePaytmRupee)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideToBePaid)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideToBePaidRupee)).setTypeface(Fonts.latoRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideBaseFare)).setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewEndRideDistance)).setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewEndRideTime)).setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewEndRideWaitTime)).setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewEndRideFareFactor)).setTypeface(Fonts.latoRegular(activity));


		buttonEndRideOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		try {
			if(engagementId == -1 && Data.endRideData != null){
				endRideData = Data.endRideData;
				setRideData();
			} else if(engagementId != -1){
				getRideSummaryAPI(activity, ""+engagementId);
			}
			else{
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
			performBackPressed();
		}

		if(OpenMode.FROM_MENU == openMode && activity instanceof RideTransactionsActivity){
			((RideTransactionsActivity)activity).setTitle(((RideTransactionsActivity)activity)
					.getResources().getString(R.string.ride_summary));
		} else if(OpenMode.FROM_SUPPORT == openMode){

		} else if(OpenMode.FROM_RIDE_END == openMode){

		}

		return rootView;
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

				textViewEndRideFareValue.setText(Utils.getMoneyDecimalFormat().format(endRideData.fare));

				if(Utils.compareDouble(endRideData.luggageCharge, 0) > 0){
					relativeLayoutLuggageCharge.setVisibility(View.VISIBLE);
					textViewEndRideLuggageChargeValue.setText(Utils.getMoneyDecimalFormat().format(endRideData.luggageCharge));
				} else{
					relativeLayoutLuggageCharge.setVisibility(View.GONE);
				}

				if(Utils.compareDouble(endRideData.convenienceCharge, 0) > 0){
					relativeLayoutConvenienceCharge.setVisibility(View.VISIBLE);
					textViewEndRideConvenienceChargeValue.setText(Utils.getMoneyDecimalFormat().format(endRideData.convenienceCharge));
				} else{
					relativeLayoutConvenienceCharge.setVisibility(View.GONE);
				}

				if(endRideData.discountTypes.size() > 1){
					listViewEndRideDiscounts.setVisibility(View.VISIBLE);
					endRideDiscountsAdapter.setList(endRideData.discountTypes);
					textViewEndRideDiscount.setText("Discounts");
					textViewEndRideDiscountRupee.setVisibility(View.GONE);
					textViewEndRideDiscountValue.setVisibility(View.GONE);
					relativeLayoutEndRideDiscount.setVisibility(View.VISIBLE);
				}
				else if(endRideData.discountTypes.size() > 0){
					listViewEndRideDiscounts.setVisibility(View.GONE);
					textViewEndRideDiscount.setText(endRideData.discountTypes.get(0).name);
					textViewEndRideDiscountRupee.setVisibility(View.VISIBLE);
					textViewEndRideDiscountValue.setVisibility(View.VISIBLE);
					textViewEndRideDiscountValue.setText(Utils.getMoneyDecimalFormat().format(endRideData.discount));
					relativeLayoutEndRideDiscount.setVisibility(View.VISIBLE);
				}
				else{
					listViewEndRideDiscounts.setVisibility(View.GONE);
					textViewEndRideDiscount.setText("Discounts");
					textViewEndRideDiscountRupee.setVisibility(View.VISIBLE);
					textViewEndRideDiscountValue.setVisibility(View.VISIBLE);
					textViewEndRideDiscountValue.setText(Utils.getMoneyDecimalFormat().format(endRideData.discount));
					if(endRideData.discount > 0){
						relativeLayoutEndRideDiscount.setVisibility(View.VISIBLE);
					} else{
						relativeLayoutEndRideDiscount.setVisibility(View.GONE);
					}
				}

				textViewEndRideFinalFareValue.setText(Utils.getMoneyDecimalFormat().format(endRideData.finalFare));

				if(Utils.compareDouble(endRideData.paidUsingWallet, 0) > 0){
					relativeLayoutPaidUsingJugnooCash.setVisibility(View.VISIBLE);
					textViewEndRideJugnooCashValue.setText(Utils.getMoneyDecimalFormat().format(endRideData.paidUsingWallet));
				} else{
					relativeLayoutPaidUsingJugnooCash.setVisibility(View.GONE);
				}
				if(Utils.compareDouble(endRideData.paidUsingPaytm, 0) > 0){
					relativeLayoutPaidUsingPaytm.setVisibility(View.VISIBLE);
					textViewEndRidePaytmValue.setText(Utils.getMoneyDecimalFormat().format(endRideData.paidUsingPaytm));
				} else{
					relativeLayoutPaidUsingPaytm.setVisibility(View.GONE);
				}

				textViewEndRideToBePaidValue.setText(Utils.getMoneyDecimalFormat().format(endRideData.toPay));


				textViewEndRideFareFactorValue.setText(decimalFormat.format(endRideData.fareFactor) + "x");
				textViewEndRideBaseFareValue.setText(getResources().getString(R.string.rupee) + " " + Utils.getMoneyDecimalFormat().format(endRideData.baseFare));
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
					linearLayoutEndRideWaitTime.setVisibility(View.VISIBLE);
					textViewEndRideWaitTimeValue.setText(decimalFormatNoDecimal.format(endRideData.waitTime) + " min");
					textViewEndRideTime.setText("Total");
				}
				else{
					linearLayoutEndRideWaitTime.setVisibility(View.GONE);
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
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put("access_token", Data.userData.accessToken);
				params.put("engagement_id", engagementId);

				RestClient.getApiServices().getRideSummary(params, new Callback<GetRideSummaryResponse>() {
					@Override
					public void success(GetRideSummaryResponse getRideSummaryResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i("Server response get_ride_summary", "response = " + response);
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
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

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						endRideRetryDialog(activity, engagementId, Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});

			} else {
				endRideRetryDialog(activity, engagementId, Data.CHECK_INTERNET_MSG);
			}
		}
	}

	public void endRideRetryDialog(final Activity activity, final String engagementId, String errorMessage) {
		DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", errorMessage, "Retry", "Cancel", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getRideSummaryAPI(activity, engagementId);
			}
		}, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		}, false, false);
	}


	public void performBackPressed() {
		if(OpenMode.FROM_MENU == openMode && activity instanceof RideTransactionsActivity){
			((RideTransactionsActivity)activity).onBackPressed();
		} else if(OpenMode.FROM_SUPPORT == openMode){

		} else if(OpenMode.FROM_RIDE_END == openMode && activity instanceof HomeActivity){
			((HomeActivity)activity).onBackPressed();
		}
	}

	@Override
	public void onDestroy() {
		ASSL.closeActivity(relative);
		System.gc();
		super.onDestroy();
	}

	public enum OpenMode{

		FROM_MENU(0), FROM_SUPPORT(1), FROM_RIDE_END(2);

		private int ordinal;

		OpenMode(int ordinal){
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}
	}

}
