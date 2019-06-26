package product.clicklabs.jugnoo.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.RideTransactionsActivity;
import product.clicklabs.jugnoo.adapters.EndRideDiscountsAdapter;
import product.clicklabs.jugnoo.apis.ApiGetRideSummary;
import product.clicklabs.jugnoo.datastructure.DiscountType;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.VehicleTypeValue;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.SupportMailActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
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
    TextView textViewEndRideSummary, textViewEndRideDriverName, textViewEndRideDriverCarNumber;
    RelativeLayout relativeLayoutTollCharge, relativeLayoutLuggageCharge, relativeLayoutConvenienceCharge,
            relativeLayoutPaidUsingJugnooCash, relativeLayoutPaidUsingPaytm,
            relativeLayoutPaidUsingMobikwik, relativeLayoutPaidUsingFreeCharge, rlPaidUsingRazorpay, rlTaxNet,relativeLayoutPaidUsingMpesa,
            rlPaidUsingCorporate, rlPaidUsingPOS, rlToBePaid;
    LinearLayout linearLayoutEndRideTime, linearLayoutRideDetail;
    RelativeLayout relativeLayoutEndRideWaitTime, relativeLayoutFare,relativeLayoutDriverTip, relativeLayoutFinalFare;
    NonScrollListView listViewEndRideDiscounts, listViewStripeTxns;
    TextView textViewEndRideFareValue,textViewDriverTipValue, textViewEndTollChargeValue, textViewEndRideLuggageChargeValue, textViewEndRideConvenienceChargeValue,
            textViewEndRideFinalFareValue, textViewEndRideJugnooCashValue, textViewEndRidePaytmValue,
            textViewEndRideMobikwikValue, textViewEndRideFreeChargeValue,
            textViewEndRideToBePaidValue, textViewEndRideBaseFare, textViewEndRideBaseFareValue,
            textViewEndRideDistanceValue, textViewEndRideTime, textViewEndRideTimeValue, textViewEndRideWaitTimeValue, textViewEndRideFareFactorValue,
            tvIncludeToll, tvEndRideRazorpay, tvEndRideTaxNet, tvEndRideRazorpayValue, tvEndRideTaxNetValue,textViewEndRideMpesaValue;
    TextView textViewEndRideStartLocationValue, textViewEndRideEndLocationValue, textViewEndRideStartTimeValue, textViewEndRideEndTimeValue,
            tvEndRideCorporate, tvEndRideCorporateValue, tvEndRidePOS, tvEndRidePOSValue;
    Button buttonEndRideOk, btnSendInvoice;
    EndRideDiscountsAdapter endRideDiscountsAdapter, stripeTxnsAdapter;

    RelativeLayout rlLuggageChargesNew;
    TextView tvLuggageChargesNewValue;

    EndRideData endRideData = null;
    ArrayList<ShowPanelResponse.Item> items;
    private int engagementId = 0;

    private View rootView;
    private FragmentActivity activity;
    private boolean rideCancelled;
    private int autosStatus;

    private static final String RIDE_CANCELLED = "rideCancelled", AUTO_STATUS = "autosStatus", END_RIDE_DATA = "endRideData";

    public static RideSummaryFragment newInstance(int engagementId, EndRideData endRideData, boolean rideCancelled, int autosStatus) {
        RideSummaryFragment rideSummaryFragment = new RideSummaryFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_ENGAGEMENT_ID, engagementId);
        bundle.putBoolean(RIDE_CANCELLED, rideCancelled);
        bundle.putInt(AUTO_STATUS, autosStatus);
        if (endRideData != null) {
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

    private void parseFromArguments() {
        this.engagementId = getArguments().getInt(Constants.KEY_ENGAGEMENT_ID, -1);
        this.rideCancelled = getArguments().getBoolean(RIDE_CANCELLED, false);
        this.autosStatus = getArguments().getInt(AUTO_STATUS, EngagementStatus.ENDED.getOrdinal());
        String endRideDataStr = getArguments().getString(END_RIDE_DATA, Constants.EMPTY_JSON_OBJECT);
        if (!Constants.EMPTY_JSON_OBJECT.equalsIgnoreCase(endRideDataStr)) {
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
            relativeLayoutDriverTip = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDriverTip);
            relativeLayoutTollCharge = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutTollCharge);
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
            textViewDriverTipValue = (TextView) rootView.findViewById(R.id.textViewDriverTipValue);
            textViewDriverTipValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideFareValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndTollChargeValue = (TextView) rootView.findViewById(R.id.textViewEndTollChargeValue);
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
            tvEndRideCorporate = (TextView) rootView.findViewById(R.id.tvEndRideCorporate); tvEndRideCorporate.setTypeface(Fonts.mavenLight(activity));
            tvEndRideCorporateValue = (TextView) rootView.findViewById(R.id.tvEndRideCorporateValue); tvEndRideCorporateValue.setTypeface(Fonts.mavenRegular(activity));
            tvEndRidePOS = (TextView) rootView.findViewById(R.id.tvEndRidePOS); tvEndRidePOS.setTypeface(Fonts.mavenLight(activity));
            tvEndRidePOSValue = (TextView) rootView.findViewById(R.id.tvEndRidePOSValue); tvEndRidePOSValue.setTypeface(Fonts.mavenRegular(activity));
            tvEndRideTaxNet = (TextView) rootView.findViewById(R.id.tvEndRideTaxNet); tvEndRideTaxNet.setTypeface(Fonts.mavenLight(activity));
            tvEndRideRazorpayValue = (TextView) rootView.findViewById(R.id.tvEndRideRazorpayValue); tvEndRideRazorpayValue.setTypeface(Fonts.mavenRegular(activity));
            tvEndRideTaxNetValue = (TextView) rootView.findViewById(R.id.tvEndRideTaxNetValue); tvEndRideTaxNetValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideToBePaidValue = (TextView) rootView.findViewById(R.id.textViewEndRideToBePaidValue);
            textViewEndRideToBePaidValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideBaseFare = (TextView) rootView.findViewById(R.id.textViewEndRideBaseFare);
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
            relativeLayoutPaidUsingJugnooCash = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaidUsingJugnooCash);
            relativeLayoutPaidUsingPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaidUsingPaytm);
            relativeLayoutPaidUsingMobikwik = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaidUsingMobikwik);
            relativeLayoutPaidUsingFreeCharge = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaidUsingFreeCharge);
            relativeLayoutPaidUsingMpesa = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaidUsingMpesa);
            rlPaidUsingRazorpay = (RelativeLayout) rootView.findViewById(R.id.rlPaidUsingRazorpay);
            rlPaidUsingCorporate = (RelativeLayout) rootView.findViewById(R.id.rlPaidUsingCorporate);
            rlPaidUsingPOS = (RelativeLayout) rootView.findViewById(R.id.rlPaidUsingPOS);
            rlTaxNet = (RelativeLayout) rootView.findViewById(R.id.rlTaxNet);
            linearLayoutEndRideTime = (LinearLayout) rootView.findViewById(R.id.linearLayoutEndRideTime);
            relativeLayoutEndRideWaitTime = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutEndRideWaitTime);
            imageViewEndRideDriverIcon = (ImageView) rootView.findViewById(R.id.imageViewEndRideDriverIcon);
            linearLayoutRideDetail = (LinearLayout) rootView.findViewById(R.id.linearLayoutRideDetail);
            relativeLayoutFinalFare = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutFinalFare);
			rlToBePaid = (RelativeLayout) rootView.findViewById(R.id.rlToBePaid);

            textViewEndRideLuggageChargeValue = (TextView) rootView.findViewById(R.id.textViewEndRideLuggageChargeValue);
            textViewEndRideLuggageChargeValue.setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideConvenienceChargeValue = (TextView) rootView.findViewById(R.id.textViewEndRideConvenienceChargeValue);
            textViewEndRideConvenienceChargeValue.setTypeface(Fonts.mavenRegular(activity));

            listViewEndRideDiscounts = (NonScrollListView) rootView.findViewById(R.id.listViewEndRideDiscounts);
            endRideDiscountsAdapter = new EndRideDiscountsAdapter(activity, true);
            listViewEndRideDiscounts.setAdapter(endRideDiscountsAdapter);

            listViewStripeTxns = (NonScrollListView) rootView.findViewById(R.id.listViewStripeTxns);
            stripeTxnsAdapter = new EndRideDiscountsAdapter(activity, false);
            listViewStripeTxns.setAdapter(stripeTxnsAdapter);

            buttonEndRideOk = (Button) rootView.findViewById(R.id.buttonEndRideOk);
            buttonEndRideOk.setTypeface(Fonts.mavenRegular(activity));
            btnSendInvoice = (Button) rootView.findViewById(R.id.btnSendInvoice);
            btnSendInvoice.setTypeface(Fonts.mavenRegular(activity));

            rlLuggageChargesNew = (RelativeLayout) rootView.findViewById(R.id.rlLuggageChargesNew);
            tvLuggageChargesNewValue = (TextView) rootView.findViewById(R.id.tvLuggageChargesNewValue);


            ((TextView) rootView.findViewById(R.id.textViewEndRideStartLocation)).setTypeface(Fonts.mavenRegular(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideEndLocation)).setTypeface(Fonts.mavenRegular(activity));
            textViewEndRideSummary = rootView.findViewById(R.id.textViewEndRideSummary);
            textViewEndRideSummary.setTypeface(Fonts.mavenMedium(activity));

            ((TextView) rootView.findViewById(R.id.textViewEndRideFare)).setTypeface(Fonts.mavenLight(activity));
            ((TextView)rootView.findViewById(R.id.textViewEndRideDriverTip)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideLuggageCharge)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideConvenienceCharge)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideFinalFare)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideJugnooCash)).setTypeface(Fonts.mavenLight(activity));
            ((TextView) rootView.findViewById(R.id.textViewEndRideJugnooCash)).setText(getString(R.string.jugnoo_cash, getString(R.string.app_name)));
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
                        } else if(Data.isMenuTagEnabled(MenuInfoTags.EMAIL_SUPPORT)){
                            activity.startActivity(new Intent(activity, SupportMailActivity.class));
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
            if(Data.autoData.getResendEmailInvoiceEnabled() == 1) {
                btnSendInvoice.setVisibility(View.VISIBLE);
            } else {
                btnSendInvoice.setVisibility(View.GONE);
            }
            btnSendInvoice.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
				    sendEmailInvoice();
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

    private void sendEmailInvoice() {
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(engagementId));

        new ApiCommon<>(activity).showLoader(true).execute(params, ApiName.SEND_EMAIL_INVOICE,
                new APICommonCallback<FeedCommonResponse>() {

                    @Override
                    public void onSuccess(final FeedCommonResponse response, String message, int flag) {

                        DialogPopup.alertPopupWithListener(activity,"",
                                message, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                });
                    }

                    @Override
                    public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
                        return false;
                    }

                });


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

                int fallbackResourceId = endRideData.getVehicleIconSet().getIconInvoice();
                HomeUtil.setVehicleIcon(activity, imageViewEndRideAutoIcon, endRideData.getIconUrl(), fallbackResourceId, true, null);

                textViewEndRideSummary.setText(getString(R.string.ride_summary)+" #"+endRideData.engagementId);
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
                    rootView.findViewById(R.id.ivSepRideDetails).setVisibility(View.VISIBLE);
                } else {
                    relativeLayoutFare.setVisibility(View.GONE);
                    linearLayoutRideDetail.setVisibility(View.GONE);
                    relativeLayoutFinalFare.setVisibility(View.GONE);
                    rootView.findViewById(R.id.ivSepRideDetails).setVisibility(View.GONE);
                }

                if (endRideData.getDriverTipAmount() > 0) {
                    relativeLayoutDriverTip.setVisibility(View.VISIBLE);
                    textViewDriverTipValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(),endRideData.getDriverTipAmount()));
                } else {
                    relativeLayoutDriverTip.setVisibility(View.GONE);
                }


                if(endRideData.tollCharge > 0.0){
                    relativeLayoutTollCharge.setVisibility(View.VISIBLE);
                    textViewEndTollChargeValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.tollCharge));
                } else {
                    relativeLayoutTollCharge.setVisibility(View.GONE);
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
                    } else {
                        imageViewEndRideAutoIcon.setImageResource(R.drawable.ic_history_pool);
                    }
                }
                if(Prefs.with(activity).getInt(Constants.KEY_CUSTOMER_SHOW_INCLUDE_TOLL_IN_SUMMARY, 0) == 1
                        && endRideData.getVehicleType() == VehicleTypeValue.TAXI.getOrdinal()){
                    tvIncludeToll.setVisibility(View.VISIBLE);
                } else {
                    tvIncludeToll.setVisibility(View.GONE);
                }

                if(endRideData.getInvoiceAdditionalTextCabs() != null
                        && !endRideData.getInvoiceAdditionalTextCabs().equalsIgnoreCase("")){
                    tvIncludeToll.setVisibility(View.VISIBLE);
                    tvIncludeToll.setText(endRideData.getInvoiceAdditionalTextCabs());
                }

                if (endRideData.discountTypes.size() > 0) {
                    listViewEndRideDiscounts.setVisibility(View.VISIBLE);
                    endRideDiscountsAdapter.setList(endRideData.discountTypes, endRideData.getCurrency());
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


                rlLuggageChargesNew.setVisibility(endRideData.getLuggageChargesNew() > 0.0 ? View.VISIBLE : View.GONE);
                tvLuggageChargesNewValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.getLuggageChargesNew()));

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

                if(endRideData.getStripeCardsAmount().size() > 0){
                    listViewStripeTxns.setVisibility(View.VISIBLE);
                    stripeTxnsAdapter.setList(endRideData.getStripeCardsAmount(), endRideData.getCurrency());
                }
                else if(Utils.compareDouble(endRideData.getPaidUsingStripe(), 0) > 0){
                    listViewStripeTxns.setVisibility(View.VISIBLE);
                    ArrayList<DiscountType> discountTypes = new ArrayList<>();

                    if(!TextUtils.isEmpty(endRideData.getLast_4())){
                        discountTypes.add(new DiscountType(WalletCore.getStripeCardDisplayString(activity,endRideData.getLast_4()),
                                endRideData.getPaidUsingStripe(), 0));
                    }else{
                        discountTypes.add(new DiscountType(MyApplication.getInstance().getWalletCore().getConfigDisplayNameCards(activity, PaymentOption.STRIPE_CARDS.getOrdinal()),
                                endRideData.getPaidUsingStripe(), 0));

                    }
                    stripeTxnsAdapter.setList(discountTypes, endRideData.getCurrency());
//                    tvEndRideStripeCardValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.getPaidUsingStripe()));
                }
                else{
                    listViewStripeTxns.setVisibility(View.GONE);
                }
                if(endRideData.getIsCorporateRide() == 1){
                    rlPaidUsingCorporate.setVisibility(View.VISIBLE);
                    tvEndRideCorporate.setText(endRideData.getPartnerName());
                    tvEndRideCorporateValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.finalFare));
                } else {
                    rlPaidUsingCorporate.setVisibility(View.GONE);
                }
                if(endRideData.getPaymentOption() == PaymentOption.POS.getOrdinal()){
                    rlPaidUsingPOS.setVisibility(View.VISIBLE);
                    tvEndRidePOSValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.finalFare));
                } else {
                    rlPaidUsingPOS.setVisibility(View.GONE);
                }

                if(Utils.compareDouble(endRideData.getNetCustomerTax(), 0) > 0){
                    rlTaxNet.setVisibility(View.VISIBLE);
                    String textLabel = getString(R.string.net_tax);
                    if(Utils.compareDouble(endRideData.getTaxPercentage(), 0) > 0){
                        textLabel+="("+ endRideData.getTaxPercentage()+ "%)";

                    }
                    tvEndRideTaxNet.setText(textLabel);

                    tvEndRideTaxNetValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.getNetCustomerTax()));
                } else{
                    rlTaxNet.setVisibility(View.GONE);
                }

                textViewEndRideToBePaidValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.toPay));

                if (!rideCancelled && (endRideData.fareFactor > 1 || endRideData.fareFactor < 1)) {
                    textViewEndRideFareFactorValue.setVisibility(View.VISIBLE);
                } else {
                    textViewEndRideFareFactorValue.setVisibility(View.GONE);
                }

                textViewEndRideFareFactorValue.setText(Prefs.with(activity).getString(Constants.KEY_CUSTOMER_PRIORITY_TIP_TITLE,
                        activity.getString(R.string.customer_priority_tip_title)) + " " + decimalFormat.format(endRideData.fareFactor)+"x");
                textViewEndRideBaseFareValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.baseFare));
                if(Prefs.with(activity).getInt(KEY_SHOW_BASE_FARE_IN_RIDE_SUMMARY, 1) != 1){
					linearLayoutRideDetail.setVisibility(View.VISIBLE);
					rootView.findViewById(R.id.ivSepRideDetails).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.llBaseFare).setVisibility(View.GONE);
                    rootView.findViewById(R.id.ivSepBaseFare).setVisibility(View.GONE);
					rlToBePaid.setVisibility(endRideData.toPay > 0D ? View.VISIBLE : View.GONE);
                }
                textViewEndRideBaseFare.setText(endRideData.getReverseBid() == 1 ? R.string.bid_fare : R.string.base_fare);

                double totalDistanceInKm = endRideData.distance;
                String kmsStr = Utils.getDistanceUnit(endRideData.getDistanceUnit());
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
