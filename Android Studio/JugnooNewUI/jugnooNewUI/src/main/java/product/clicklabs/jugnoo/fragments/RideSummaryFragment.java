package product.clicklabs.jugnoo.fragments;

import android.annotation.SuppressLint;
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

import java.text.DecimalFormat;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.adapters.EndRideDiscountsAdapter;
import product.clicklabs.jugnoo.apis.ApiGetRideSummary;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.support.models.GetRideSummaryResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Utils;


@SuppressLint("ValidFragment")
public class RideSummaryFragment extends Fragment implements FlurryEventNames, Constants {

	private final String TAG = RideSummaryFragment.class.getSimpleName();

	RelativeLayout relative;

	RelativeLayout relativeLayoutMap;
	GoogleMap mapLite;

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

	EndRideData endRideData = null;
	GetRideSummaryResponse getRideSummaryResponse;
	private int engagementId = 0;

	private View rootView;
    private FragmentActivity activity;

	@SuppressLint("ValidFragment")
	public RideSummaryFragment(int engagementId){
		this.engagementId = engagementId;
	}

	@SuppressLint("ValidFragment")
	public RideSummaryFragment(EndRideData endRideData){
		this.engagementId = Integer.parseInt(endRideData.engagementId);
		this.endRideData = endRideData;
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

		textViewEndRideDriverName = (TextView) rootView.findViewById(R.id.textViewEndRideDriverName); textViewEndRideDriverName.setTypeface(Fonts.mavenLight(activity));
		textViewEndRideDriverCarNumber = (TextView) rootView.findViewById(R.id.textViewEndRideDriverCarNumber); textViewEndRideDriverCarNumber.setTypeface(Fonts.mavenLight(activity));

		textViewEndRideStartLocationValue = (TextView) rootView.findViewById(R.id.textViewEndRideStartLocationValue); textViewEndRideStartLocationValue.setTypeface(Fonts.mavenLight(activity));
		textViewEndRideEndLocationValue = (TextView) rootView.findViewById(R.id.textViewEndRideEndLocationValue); textViewEndRideEndLocationValue.setTypeface(Fonts.mavenLight(activity));
		textViewEndRideStartTimeValue = (TextView) rootView.findViewById(R.id.textViewEndRideStartTimeValue); textViewEndRideStartTimeValue.setTypeface(Fonts.mavenLight(activity));
		textViewEndRideEndTimeValue = (TextView) rootView.findViewById(R.id.textViewEndRideEndTimeValue); textViewEndRideEndTimeValue.setTypeface(Fonts.mavenLight(activity));

		textViewEndRideFareValue = (TextView) rootView.findViewById(R.id.textViewEndRideFareValue); textViewEndRideFareValue.setTypeface(Fonts.mavenRegular(activity));
		textViewEndRideDiscountValue = (TextView) rootView.findViewById(R.id.textViewEndRideDiscountValue); textViewEndRideDiscountValue.setTypeface(Fonts.mavenRegular(activity));
		textViewEndRideFinalFareValue = (TextView) rootView.findViewById(R.id.textViewEndRideFinalFareValue); textViewEndRideFinalFareValue.setTypeface(Fonts.mavenRegular(activity));
		textViewEndRideJugnooCashValue = (TextView) rootView.findViewById(R.id.textViewEndRideJugnooCashValue); textViewEndRideJugnooCashValue.setTypeface(Fonts.mavenRegular(activity));
		textViewEndRidePaytmValue = (TextView) rootView.findViewById(R.id.textViewEndRidePaytmValue); textViewEndRidePaytmValue.setTypeface(Fonts.mavenRegular(activity));
		textViewEndRideToBePaidValue = (TextView) rootView.findViewById(R.id.textViewEndRideToBePaidValue); textViewEndRideToBePaidValue.setTypeface(Fonts.mavenRegular(activity));
		textViewEndRideBaseFareValue = (TextView) rootView.findViewById(R.id.textViewEndRideBaseFareValue); textViewEndRideBaseFareValue.setTypeface(Fonts.mavenRegular(activity));
		textViewEndRideDistanceValue = (TextView) rootView.findViewById(R.id.textViewEndRideDistanceValue); textViewEndRideDistanceValue.setTypeface(Fonts.mavenRegular(activity));
		textViewEndRideTime = (TextView) rootView.findViewById(R.id.textViewEndRideTime); textViewEndRideTime.setTypeface(Fonts.mavenLight(activity));
		textViewEndRideTimeValue = (TextView) rootView.findViewById(R.id.textViewEndRideTimeValue); textViewEndRideTimeValue.setTypeface(Fonts.mavenRegular(activity));
		textViewEndRideWaitTimeValue = (TextView) rootView.findViewById(R.id.textViewEndRideWaitTimeValue); textViewEndRideWaitTimeValue.setTypeface(Fonts.mavenRegular(activity));
		textViewEndRideFareFactorValue = (TextView) rootView.findViewById(R.id.textViewEndRideFareFactorValue); textViewEndRideFareFactorValue.setTypeface(Fonts.mavenRegular(activity));

		relativeLayoutLuggageCharge = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutLuggageCharge);
		relativeLayoutConvenienceCharge = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutConvenienceCharge);
		relativeLayoutEndRideDiscount = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutEndRideDiscount);
		relativeLayoutPaidUsingJugnooCash = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaidUsingJugnooCash);
		relativeLayoutPaidUsingPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaidUsingPaytm);
		linearLayoutEndRideTime = (LinearLayout) rootView.findViewById(R.id.linearLayoutEndRideTime);
		relativeLayoutEndRideWaitTime = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutEndRideWaitTime);

		textViewEndRideLuggageChargeValue = (TextView) rootView.findViewById(R.id.textViewEndRideLuggageChargeValue); textViewEndRideLuggageChargeValue.setTypeface(Fonts.mavenRegular(activity));
		textViewEndRideConvenienceChargeValue = (TextView) rootView.findViewById(R.id.textViewEndRideConvenienceChargeValue); textViewEndRideConvenienceChargeValue.setTypeface(Fonts.mavenRegular(activity));
		textViewEndRideDiscount = (TextView) rootView.findViewById(R.id.textViewEndRideDiscount); textViewEndRideDiscount.setTypeface(Fonts.mavenLight(activity));

		listViewEndRideDiscounts = (NonScrollListView) rootView.findViewById(R.id.listViewEndRideDiscounts);
		endRideDiscountsAdapter = new EndRideDiscountsAdapter(activity);
		listViewEndRideDiscounts.setAdapter(endRideDiscountsAdapter);

		buttonEndRideOk = (Button) rootView.findViewById(R.id.buttonEndRideOk); buttonEndRideOk.setTypeface(Fonts.mavenRegular(activity));



		((TextView) rootView.findViewById(R.id.textViewEndRideStartLocation)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideEndLocation)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideSummary)).setTypeface(Fonts.mavenRegular(activity));

		((TextView) rootView.findViewById(R.id.textViewEndRideFare)).setTypeface(Fonts.mavenLight(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideLuggageCharge)).setTypeface(Fonts.mavenLight(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideConvenienceCharge)).setTypeface(Fonts.mavenLight(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideFinalFare)).setTypeface(Fonts.mavenLight(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideJugnooCash)).setTypeface(Fonts.mavenLight(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRidePaytm)).setTypeface(Fonts.mavenLight(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideToBePaid)).setTypeface(Fonts.mavenLight(activity));
		((TextView) rootView.findViewById(R.id.textViewEndRideBaseFare)).setTypeface(Fonts.mavenLight(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewEndRideDistance)).setTypeface(Fonts.mavenLight(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewEndRideTime)).setTypeface(Fonts.mavenLight(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewEndRideWaitTime)).setTypeface(Fonts.mavenLight(activity), Typeface.BOLD);


		buttonEndRideOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(activity instanceof RideTransactionsActivity){
					new TransactionUtils().openRideIssuesFragment(activity,
							((RideTransactionsActivity)activity).getContainer(),
							engagementId, endRideData, getRideSummaryResponse);
				} else {
					performBackPressed();
				}
			}
		});

		try {
			if(engagementId == -1 && Data.endRideData != null){
				endRideData = Data.endRideData;
				setRideData();
			} else if(engagementId != -1){
				if(endRideData != null){
					setRideData();
				} else{
					getRideSummaryAPI(activity, ""+engagementId);
				}
			}
			else{
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
			performBackPressed();
		}

		if(activity instanceof RideTransactionsActivity){
			buttonEndRideOk.setText(activity.getResources().getString(R.string.need_help));
		}
		else if(activity instanceof HomeActivity){
			buttonEndRideOk.setText(activity.getResources().getString(R.string.ok));
		}
		else if(activity instanceof SupportActivity){
			buttonEndRideOk.setText(activity.getResources().getString(R.string.ok));
		}

		setActivityTitle();

		return rootView;
	}


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			setActivityTitle();
		}
	}

	private void setActivityTitle(){
		if(activity instanceof RideTransactionsActivity){
			((RideTransactionsActivity)activity).setTitle(activity.getResources().getString(R.string.ride_summary));
		} else if(activity instanceof SupportActivity){
			((SupportActivity)activity).setTitle(activity.getResources().getString(R.string.ride_summary));
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

				if(endRideData.fareFactor > 1 || endRideData.fareFactor < 1){
					textViewEndRideFareFactorValue.setVisibility(View.VISIBLE);
				} else{
					textViewEndRideFareFactorValue.setVisibility(View.GONE);
				}

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
		new ApiGetRideSummary(activity, Data.userData.accessToken, Integer.parseInt(engagementId), Data.fareStructure.fixedFare,
				new ApiGetRideSummary.Callback() {
					@Override
					public void onSuccess(EndRideData endRideData, GetRideSummaryResponse getRideSummaryResponse) {
						RideSummaryFragment.this.endRideData = endRideData;
						RideSummaryFragment.this.getRideSummaryResponse = getRideSummaryResponse;
						setRideData();
					}

					@Override
					public boolean onActionFailed(String message) {
						return true;
					}

					@Override
					public void onFailure() {
					}

					@Override
					public void onRetry(View view) {
						getRideSummaryAPI(activity, engagementId);
					}

					@Override
					public void onNoRetry(View view) {
						performBackPressed();
					}
				}).getRideSummaryAPI();
	}



	public void performBackPressed() {
		if(activity instanceof RideTransactionsActivity){
			((RideTransactionsActivity)activity).performBackPressed();
		} else if(activity instanceof HomeActivity){
			((HomeActivity)activity).onBackPressed();
		} else if(activity instanceof SupportActivity){
			((SupportActivity)activity).performBackPressed();
		}
	}

	@Override
	public void onDestroy() {
		ASSL.closeActivity(relative);
		System.gc();
		super.onDestroy();
	}

}
