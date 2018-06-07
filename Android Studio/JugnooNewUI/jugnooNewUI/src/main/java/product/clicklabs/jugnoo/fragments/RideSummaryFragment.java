package product.clicklabs.jugnoo.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fugu.FuguConfig;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.adapters.EndRideDiscountsAdapter;
import product.clicklabs.jugnoo.apis.ApiGetRideSummary;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.models.VehicleTypeValue;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.WalletCore;


@SuppressLint("ValidFragment")
public class RideSummaryFragment extends Fragment implements Constants {

    private final String TAG = RideSummaryFragment.class.getSimpleName();

    RelativeLayout relative;

    RelativeLayout relativeLayoutMap;
    GoogleMap mapLite;

    RelativeLayout relativeLayoutRideSummary;
    ScrollView scrollViewEndRide;

    ImageView imageViewEndRideAutoIcon, imageViewEndRideDriverIcon;
    TextView textViewEndRideDriverName, textViewEndRideDriverCarNumber;
    RelativeLayout relativeLayoutLuggageCharge, relativeLayoutConvenienceCharge,
            relativeLayoutEndRideDiscount, relativeLayoutPaidUsingJugnooCash, relativeLayoutPaidUsingPaytm,
            relativeLayoutPaidUsingMobikwik, relativeLayoutPaidUsingFreeCharge, rlPaidUsingRazorpay,rlPaidUsingStripeCard,relativeLayoutPaidUsingMpesa;
    LinearLayout linearLayoutEndRideTime, linearLayoutRideDetail;
    RelativeLayout relativeLayoutEndRideWaitTime, relativeLayoutFare, relativeLayoutFinalFare;
    NonScrollListView listViewEndRideDiscounts;
    TextView textViewEndRideFareValue, textViewEndRideLuggageChargeValue, textViewEndRideConvenienceChargeValue,
            textViewEndRideDiscount, textViewEndRideDiscountValue,
            textViewEndRideFinalFareValue, textViewEndRideJugnooCashValue, textViewEndRidePaytmValue,
            textViewEndRideMobikwikValue, textViewEndRideFreeChargeValue,
            textViewEndRideToBePaidValue, textViewEndRideBaseFareValue,
            textViewEndRideDistanceValue, textViewEndRideTime, textViewEndRideTimeValue, textViewEndRideWaitTimeValue, textViewEndRideFareFactorValue,
            tvIncludeToll, tvEndRideRazorpay,tvEndRideStripeCard, tvEndRideRazorpayValue,tvEndRideStripeCardValue,textViewEndRideMpesaValue;
    TextView textViewEndRideStartLocationValue, textViewEndRideEndLocationValue, textViewEndRideStartTimeValue, textViewEndRideEndTimeValue;
    Button buttonEndRideOk;
    EndRideDiscountsAdapter endRideDiscountsAdapter;

    EndRideData endRideData = null;
    ArrayList<ShowPanelResponse.Item> items;
    private int engagementId = 0;

    private View rootView;
    private FragmentActivity activity;
    private boolean rideCancelled;
    private int autosStatus;

    private static final String RIDE_CANCELLED = "rideCancelled", AUTO_STATUS = "autosStatus", END_RIDE_DATA = "endRideData";

    public static RideSummaryFragment newInstance(int engagementId, EndRideData endRideData, boolean rideCancelled, int autosStatus){
        RideSummaryFragment rideSummaryFragment = new RideSummaryFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_ENGAGEMENT_ID, engagementId);
        bundle.putBoolean(RIDE_CANCELLED, rideCancelled);
        bundle.putInt(AUTO_STATUS, autosStatus);
        if(endRideData != null){
            bundle.putString(END_RIDE_DATA, new Gson().toJson(endRideData, EndRideData.class));
        }
        rideSummaryFragment.setArguments(bundle);

        return rideSummaryFragment;
    }


    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void parseFromArguments(){
        this.engagementId = getArguments().getInt(Constants.KEY_ENGAGEMENT_ID, -1);
        this.rideCancelled = getArguments().getBoolean(RIDE_CANCELLED, false);
        this.autosStatus = getArguments().getInt(AUTO_STATUS, EngagementStatus.ENDED.getOrdinal());
        String endRideDataStr = getArguments().getString(END_RIDE_DATA, Constants.EMPTY_JSON_OBJECT);
        if(!Constants.EMPTY_JSON_OBJECT.equalsIgnoreCase(endRideDataStr)){
            endRideData = new Gson().fromJson(endRideDataStr, EndRideData.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.fragment_ride_summary, container, false);

            activity = getActivity();
            relative = (RelativeLayout) rootView.findViewById(R.id.relative);
            new ASSL(activity, relative, 1134, 720, false);
            parseFromArguments();

            relativeLayoutMap = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMap);
            relativeLayoutMap.setVisibility(View.GONE);
            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapLite)).getMapAsync(new OnMapReadyCallback() {
				@Override
				public void onMapReady(GoogleMap googleMap) {
					mapLite = googleMap;
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
				}
			});

            relativeLayoutFare = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutFare);
            relativeLayoutRideSummary = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRideSummary);
            relativeLayoutRideSummary.setVisibility(View.GONE);
            scrollViewEndRide = (ScrollView) rootView.findViewById(R.id.scrollViewEndRide);

            imageViewEndRideAutoIcon = (ImageView) rootView.findViewById(R.id.imageViewEndRideAutoIcon);
            textViewEndRideDriverName = (TextView) rootView.findViewById(R.id.textViewEndRideDriverName);
            textViewEndRideDriverName.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideDriverCarNumber = (TextView) rootView.findViewById(R.id.textViewEndRideDriverCarNumber);
            textViewEndRideDriverCarNumber.setTypeface(Fonts.mavenRegular(activity));

            textViewEndRideStartLocationValue = (TextView) rootView.findViewById(R.id.textViewEndRideStartLocationValue);
            textViewEndRideStartLocationValue.setTypeface(Fonts.mavenLight(activity));
            textViewEndRideEndLocationValue = (TextView) rootView.findViewById(R.id.textViewEndRideEndLocationValue);
            textViewEndRideEndLocationValue.setTypeface(Fonts.mavenLight(activity));
            textViewEndRideStartTimeValue = (TextView) rootView.findViewById(R.id.textViewEndRideStartTimeValue);
            textViewEndRideStartTimeValue.setTypeface(Fonts.mavenLight(activity));
            textViewEndRideEndTimeValue = (TextView) rootView.findViewById(R.id.textViewEndRideEndTimeValue);
            textViewEndRideEndTimeValue.setTypeface(Fonts.mavenLight(activity));

            textViewEndRideFareValue = (TextView) rootView.findViewById(R.id.textViewEndRideFareValue);
            textViewEndRideFareValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideDiscountValue = (TextView) rootView.findViewById(R.id.textViewEndRideDiscountValue);
            textViewEndRideDiscountValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideFinalFareValue = (TextView) rootView.findViewById(R.id.textViewEndRideFinalFareValue);
            textViewEndRideFinalFareValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideJugnooCashValue = (TextView) rootView.findViewById(R.id.textViewEndRideJugnooCashValue);
            textViewEndRideJugnooCashValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRidePaytmValue = (TextView) rootView.findViewById(R.id.textViewEndRidePaytmValue);
            textViewEndRidePaytmValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideMobikwikValue = (TextView) rootView.findViewById(R.id.textViewEndRideMobikwikValue);
            textViewEndRideMobikwikValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideFreeChargeValue = (TextView) rootView.findViewById(R.id.textViewEndRideFreeChargeValue);
            textViewEndRideFreeChargeValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideMpesaValue = (TextView) rootView.findViewById(R.id.textViewEndRideMpesaValue);
            textViewEndRideMpesaValue.setTypeface(Fonts.mavenRegular(activity));
            tvEndRideRazorpay = (TextView) rootView.findViewById(R.id.tvEndRideRazorpay); tvEndRideRazorpay.setTypeface(Fonts.mavenLight(activity));
            tvEndRideStripeCard = (TextView) rootView.findViewById(R.id.tvEndRideStripeCard); tvEndRideStripeCard.setTypeface(Fonts.mavenLight(activity));
            tvEndRideRazorpayValue = (TextView) rootView.findViewById(R.id.tvEndRideRazorpayValue); tvEndRideRazorpayValue.setTypeface(Fonts.mavenRegular(activity));
            tvEndRideStripeCardValue = (TextView) rootView.findViewById(R.id.tvEndRideStripeCardValue); tvEndRideStripeCardValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideToBePaidValue = (TextView) rootView.findViewById(R.id.textViewEndRideToBePaidValue);
            textViewEndRideToBePaidValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideBaseFareValue = (TextView) rootView.findViewById(R.id.textViewEndRideBaseFareValue);
            textViewEndRideBaseFareValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideDistanceValue = (TextView) rootView.findViewById(R.id.textViewEndRideDistanceValue);
            textViewEndRideDistanceValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideTime = (TextView) rootView.findViewById(R.id.textViewEndRideTime);
            textViewEndRideTime.setTypeface(Fonts.mavenLight(activity));
            textViewEndRideTimeValue = (TextView) rootView.findViewById(R.id.textViewEndRideTimeValue);
            textViewEndRideTimeValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideWaitTimeValue = (TextView) rootView.findViewById(R.id.textViewEndRideWaitTimeValue);
            textViewEndRideWaitTimeValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideFareFactorValue = (TextView) rootView.findViewById(R.id.textViewEndRideFareFactorValue);
            textViewEndRideFareFactorValue.setTypeface(Fonts.mavenRegular(activity));
            tvIncludeToll = (TextView) rootView.findViewById(R.id.tvIncludeToll);

            relativeLayoutLuggageCharge = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutLuggageCharge);
            relativeLayoutConvenienceCharge = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutConvenienceCharge);
            relativeLayoutEndRideDiscount = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutEndRideDiscount);
            relativeLayoutPaidUsingJugnooCash = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaidUsingJugnooCash);
            relativeLayoutPaidUsingPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaidUsingPaytm);
            relativeLayoutPaidUsingMobikwik = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaidUsingMobikwik);
            relativeLayoutPaidUsingFreeCharge = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaidUsingFreeCharge);
            relativeLayoutPaidUsingMpesa = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaidUsingMpesa);
            rlPaidUsingRazorpay = (RelativeLayout) rootView.findViewById(R.id.rlPaidUsingRazorpay);
            rlPaidUsingStripeCard = (RelativeLayout) rootView.findViewById(R.id.rlPaidUsingStripeCard);
            linearLayoutEndRideTime = (LinearLayout) rootView.findViewById(R.id.linearLayoutEndRideTime);
            relativeLayoutEndRideWaitTime = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutEndRideWaitTime);
            imageViewEndRideDriverIcon = (ImageView) rootView.findViewById(R.id.imageViewEndRideDriverIcon);
            linearLayoutRideDetail = (LinearLayout) rootView.findViewById(R.id.linearLayoutRideDetail);
            relativeLayoutFinalFare = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutFinalFare);

            textViewEndRideLuggageChargeValue = (TextView) rootView.findViewById(R.id.textViewEndRideLuggageChargeValue);
            textViewEndRideLuggageChargeValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideConvenienceChargeValue = (TextView) rootView.findViewById(R.id.textViewEndRideConvenienceChargeValue);
            textViewEndRideConvenienceChargeValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideDiscount = (TextView) rootView.findViewById(R.id.textViewEndRideDiscount);
            textViewEndRideDiscount.setTypeface(Fonts.mavenLight(activity));

            listViewEndRideDiscounts = (NonScrollListView) rootView.findViewById(R.id.listViewEndRideDiscounts);
            endRideDiscountsAdapter = new EndRideDiscountsAdapter(activity);
            listViewEndRideDiscounts.setAdapter(endRideDiscountsAdapter);

            buttonEndRideOk = (Button) rootView.findViewById(R.id.buttonEndRideOk);
            buttonEndRideOk.setTypeface(Fonts.mavenRegular(activity));


            ((TextView) rootView.findViewById(R.id.textViewEndRideStartLocation)).setTypeface(Fonts.mavenRegular(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideEndLocation)).setTypeface(Fonts.mavenRegular(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideSummary)).setTypeface(Fonts.mavenMedium(activity));

            ((TextView) rootView.findViewById(R.id.textViewEndRideFare)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideLuggageCharge)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideConvenienceCharge)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideFinalFare)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideJugnooCash)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRidePaytm)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideMobikwik)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideFreeCharge)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideToBePaid)).setTypeface(Fonts.mavenLight(activity), Typeface.BOLD);
            ((TextView) rootView.findViewById(R.id.textViewEndRideBaseFare)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideDistance)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideTime)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideWaitTime)).setTypeface(Fonts.mavenLight(activity));


            buttonEndRideOk.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (activity instanceof RideTransactionsActivity) {
                        if (Data.isFuguChatEnabled()) {
                            try {
                                if(!TextUtils.isEmpty(endRideData.getFuguChannelId())){
                                    FuguConfig.getInstance().openChatByTransactionId(endRideData.getFuguChannelId(),String.valueOf(Data.getFuguUserData().getUserId()),
                                            endRideData.getFuguChannelName(), endRideData.getFuguTags());
                                }else {
                                    FuguConfig.getInstance().openChat(activity, Data.CHANNEL_ID_FUGU_ISSUE_RIDE());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.showToast(activity, activity.getString(R.string.something_went_wrong));
                            }
                        } else {
                            new TransactionUtils().openRideIssuesFragment(activity,
                                    ((RideTransactionsActivity) activity).getContainer(),
                                    engagementId, -1, endRideData, items, 0, false, autosStatus, null, -1, -1, "");
                        }
					} else {
						performBackPressed();
					}
				}
			});

            try {
				if (engagementId == -1 && Data.autoData.getEndRideData() != null) {
					endRideData = Data.autoData.getEndRideData();
					setRideData();
				} else if (engagementId != -1) {
					if (endRideData != null) {
						setRideData();
					} else {
						getRideSummaryAPI(activity, "" + engagementId);
					}
				} else {
					throw new Exception();
				}
			} catch (Exception e) {
				e.printStackTrace();
				performBackPressed();
			}

            if (activity instanceof RideTransactionsActivity) {
				buttonEndRideOk.setText(activity.getResources().getString(R.string.need_help));
			} else if (activity instanceof HomeActivity) {
				buttonEndRideOk.setText(activity.getResources().getString(R.string.ok));
			} else if (activity instanceof SupportActivity) {
				buttonEndRideOk.setText(activity.getResources().getString(R.string.ok));
			}

            setActivityTitle();
        } catch (Exception e) {
            e.printStackTrace();
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        return rootView;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setActivityTitle();
        }
    }

    private void setActivityTitle() {
        if (activity instanceof RideTransactionsActivity) {
            ((RideTransactionsActivity) activity).setTitle(activity.getResources().getString(R.string.ride_summary));
        } else if (activity instanceof SupportActivity) {
            ((SupportActivity) activity).setTitle(activity.getResources().getString(R.string.ride_summary));
        }
    }


    public void setRideData() {
        try {
            if (endRideData != null) {
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#");
                scrollViewEndRide.scrollTo(0, 0);

                relativeLayoutMap.setVisibility(View.VISIBLE);
                relativeLayoutRideSummary.setVisibility(View.VISIBLE);

                imageViewEndRideAutoIcon.setImageResource(endRideData.getVehicleIconSet().getIconInvoice());

                textViewEndRideDriverName.setText(endRideData.driverName);
                textViewEndRideDriverCarNumber.setText(endRideData.driverCarNumber);

                textViewEndRideStartLocationValue.setText(endRideData.pickupAddress);
                textViewEndRideEndLocationValue.setText(endRideData.dropAddress);

                textViewEndRideStartTimeValue.setText(endRideData.pickupTime);
                textViewEndRideEndTimeValue.setText(endRideData.dropTime);

                if (endRideData.fare > 0) {
                    relativeLayoutFare.setVisibility(View.VISIBLE);
                    linearLayoutRideDetail.setVisibility(View.VISIBLE);
                    relativeLayoutFinalFare.setVisibility(View.VISIBLE);
                    textViewEndRideFareValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.fare));
                } else {
                    relativeLayoutFare.setVisibility(View.GONE);
                    linearLayoutRideDetail.setVisibility(View.GONE);
                    relativeLayoutFinalFare.setVisibility(View.GONE);
                }

                if (Utils.compareDouble(endRideData.luggageCharge, 0) > 0) {
                    relativeLayoutLuggageCharge.setVisibility(View.VISIBLE);
                    textViewEndRideLuggageChargeValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.luggageCharge));
                } else {
                    relativeLayoutLuggageCharge.setVisibility(View.GONE);
                }

                if (Utils.compareDouble(endRideData.convenienceCharge, 0) > 0) {
                    relativeLayoutConvenienceCharge.setVisibility(View.VISIBLE);
                    textViewEndRideConvenienceChargeValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.convenienceCharge));
                } else {
                    relativeLayoutConvenienceCharge.setVisibility(View.GONE);
                }

                if (endRideData.getIsPooled() == 1) {
                    if(endRideData.getVehicleType() == VehicleTypeValue.TAXI.getOrdinal()){
                        imageViewEndRideAutoIcon.setImageResource(R.drawable.ic_history_carpool);
                        tvIncludeToll.setVisibility(View.VISIBLE);
                    } else {
                        imageViewEndRideAutoIcon.setImageResource(R.drawable.ic_history_pool);
                    }
                }

                if(endRideData.getInvoiceAdditionalTextCabs() != null
                        && !endRideData.getInvoiceAdditionalTextCabs().equalsIgnoreCase("")){
                    tvIncludeToll.setVisibility(View.VISIBLE);
                    tvIncludeToll.setText(endRideData.getInvoiceAdditionalTextCabs());
                }

                if (endRideData.discountTypes.size() > 0) {
                    listViewEndRideDiscounts.setVisibility(View.VISIBLE);
                    endRideDiscountsAdapter.setList(endRideData.discountTypes, endRideData.getCurrency());
                    textViewEndRideDiscountValue.setVisibility(View.GONE);
                    relativeLayoutEndRideDiscount.setVisibility(View.VISIBLE);
                    textViewEndRideDiscount.setVisibility(View.GONE);

                    for (int i = 0; i < endRideData.discountTypes.size(); i++) {
                        if (endRideData.discountTypes.get(i).getReferenceId() == 0) {
                            textViewEndRideDiscount.setVisibility(View.VISIBLE);
                            textViewEndRideDiscount.setText(R.string.discounts);
                            break;
                        }
                    }
                } else {
                    relativeLayoutEndRideDiscount.setVisibility(View.GONE);
                }


				/*if(endRideData.discountTypes.size() > 1){
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
					textViewEndRideDiscountValue.setText(String.format(getResources().getString(R.string.rupees_value_format), FeedUtils.getMoneyDecimalFormat().format(endRideData.discount)));
					relativeLayoutEndRideDiscount.setVisibility(View.VISIBLE);
				}
				else{
					listViewEndRideDiscounts.setVisibility(View.GONE);
					textViewEndRideDiscount.setText("Discounts");
					textViewEndRideDiscountValue.setVisibility(View.VISIBLE);
					textViewEndRideDiscountValue.setText(String.format(getResources().getString(R.string.rupees_value_format), FeedUtils.getMoneyDecimalFormat().format(endRideData.discount)));
					if(endRideData.discount > 0){
						relativeLayoutEndRideDiscount.setVisibility(View.VISIBLE);
					} else{
						relativeLayoutEndRideDiscount.setVisibility(View.GONE);
					}
				}*/


                textViewEndRideFinalFareValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.finalFare));
                if(Utils.compareDouble(endRideData.fare, endRideData.finalFare) == 0){
                    relativeLayoutFinalFare.setVisibility(View.GONE);
                }

                if (Utils.compareDouble(endRideData.paidUsingWallet, 0) > 0) {
                    relativeLayoutPaidUsingJugnooCash.setVisibility(View.VISIBLE);
                    textViewEndRideJugnooCashValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.paidUsingWallet));
                } else {
                    relativeLayoutPaidUsingJugnooCash.setVisibility(View.GONE);
                }
                if (Utils.compareDouble(endRideData.paidUsingPaytm, 0) > 0) {
                    relativeLayoutPaidUsingPaytm.setVisibility(View.VISIBLE);
                    textViewEndRidePaytmValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.paidUsingPaytm));
                } else {
                    relativeLayoutPaidUsingPaytm.setVisibility(View.GONE);
                }
                if(Utils.compareDouble(endRideData.paidUsingMobikwik, 0) > 0){
                    relativeLayoutPaidUsingMobikwik.setVisibility(View.VISIBLE);
                    textViewEndRideMobikwikValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.paidUsingMobikwik));
                } else{
                    relativeLayoutPaidUsingMobikwik.setVisibility(View.GONE);
                }
                if(Utils.compareDouble(endRideData.paidUsingFreeCharge, 0) > 0){
                    relativeLayoutPaidUsingFreeCharge.setVisibility(View.VISIBLE);
                    textViewEndRideFreeChargeValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.paidUsingFreeCharge));
                } else{
                    relativeLayoutPaidUsingFreeCharge.setVisibility(View.GONE);
                }
                if(Utils.compareDouble(endRideData.paidUsingMpesa, 0) > 0){
                    relativeLayoutPaidUsingMpesa.setVisibility(View.VISIBLE);
                    textViewEndRideMpesaValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.paidUsingMpesa));
                } else{
                    relativeLayoutPaidUsingMpesa.setVisibility(View.GONE);
                }
                if(Utils.compareDouble(endRideData.paidUsingRazorpay, 0) > 0){
                    rlPaidUsingRazorpay.setVisibility(View.VISIBLE);
                    tvEndRideRazorpay.setText(MyApplication.getInstance().getWalletCore().getRazorpayName(activity));
                    tvEndRideRazorpayValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.paidUsingRazorpay));
                } else{
                    rlPaidUsingRazorpay.setVisibility(View.GONE);
                }
                if(Utils.compareDouble(endRideData.getPaidUsingStripe(), 0) > 0){
                    rlPaidUsingStripeCard.setVisibility(View.VISIBLE);
                    if(!TextUtils.isEmpty(endRideData.getLast_4())){
                        tvEndRideStripeCard.setText(WalletCore.getStripeCardDisplayString(activity,endRideData.getLast_4()));

                    }else{
                        tvEndRideStripeCard.setText(MyApplication.getInstance().getWalletCore().getStripeCardName(activity));

                    }
                    tvEndRideStripeCardValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.getPaidUsingStripe()));
                } else{
                    rlPaidUsingStripeCard.setVisibility(View.GONE);
                }

                textViewEndRideToBePaidValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.toPay));

                if (!rideCancelled && (endRideData.fareFactor > 1 || endRideData.fareFactor < 1)) {
                    textViewEndRideFareFactorValue.setVisibility(View.VISIBLE);
                } else {
                    textViewEndRideFareFactorValue.setVisibility(View.GONE);
                }

                textViewEndRideFareFactorValue.setText(String.format(getResources().getString(R.string.priority_tip_format), decimalFormat.format(endRideData.fareFactor)));
                textViewEndRideBaseFareValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.baseFare));
                double totalDistanceInKm = endRideData.distance;
                String kmsStr = "";
                if (totalDistanceInKm > 1) {
                    kmsStr = getString(R.string.kms);
                } else {
                    kmsStr = getString(R.string.km);
                }
                textViewEndRideDistanceValue.setText("" + decimalFormat.format(totalDistanceInKm) + " " + kmsStr);
                if (endRideData.rideTime > -1) {
                    linearLayoutEndRideTime.setVisibility(View.VISIBLE);
                    textViewEndRideTimeValue.setText(decimalFormatNoDecimal.format(endRideData.rideTime) + " "+getString(R.string.min));
                } else {
                    linearLayoutEndRideTime.setVisibility(View.GONE);
                }
                if (endRideData.waitingChargesApplicable == 1 || endRideData.waitTime > 0) {
                    relativeLayoutEndRideWaitTime.setVisibility(View.VISIBLE);
                    textViewEndRideWaitTimeValue.setText(decimalFormatNoDecimal.format(endRideData.waitTime) + " "+getString(R.string.min));
                    textViewEndRideTime.setText(R.string.total);
                } else {
                    relativeLayoutEndRideWaitTime.setVisibility(View.GONE);
                    textViewEndRideTime.setText(R.string.time);
                }

                if(rideCancelled){
                    relativeLayoutConvenienceCharge.setVisibility(View.GONE);
                    relativeLayoutLuggageCharge.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getRideSummaryAPI(final Activity activity, final String engagementId) {
        new ApiGetRideSummary(activity, Data.userData.accessToken, Integer.parseInt(engagementId), -1, Data.autoData.getFareStructure().getFixedFare(),
                new ApiGetRideSummary.Callback() {
                    @Override
                    public void onSuccess(EndRideData endRideData, HistoryResponse.Datum datum, ArrayList<ShowPanelResponse.Item> items) {
                        RideSummaryFragment.this.endRideData = endRideData;
                        RideSummaryFragment.this.items = items;
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
                }).getRideSummaryAPI(autosStatus, ProductType.AUTO, false);
    }


    public void performBackPressed() {

        if (activity instanceof RideTransactionsActivity) {
            ((RideTransactionsActivity) activity).performBackPressed();
        } else if (activity instanceof HomeActivity) {
            ((HomeActivity) activity).onBackPressed();
        } else if (activity instanceof SupportActivity) {
            ((SupportActivity) activity).performBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
        super.onDestroy();
    }

}
