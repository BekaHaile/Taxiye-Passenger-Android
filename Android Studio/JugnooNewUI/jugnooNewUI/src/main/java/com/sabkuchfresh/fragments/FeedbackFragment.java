package com.sabkuchfresh.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.models.RideEndGoodFeedbackViewType;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by gurmail on 24/05/16.
 */
public class FeedbackFragment extends BaseFragment implements View.OnClickListener, FlurryEventNames {


    private View rootView;
    private ImageView imageViewThumbsDown, imageViewThumbsUp, imageviewType, imageViewThumbsUpGif, imageViewRideEndWithImage;
    private FreshActivity activity;
    private LinearLayout linearLayoutRSViewInvoice, linearLayoutRideSummaryContainer;
    private RelativeLayout mainLayout, relativeLayoutGreat, relativeLayoutRideEndWithImage;
    private TextView textViewThanks, textViewRSTotalFare, textViewRSData, textViewRSCashPaidValue,
            textViewRSInvoice, textViewRSRateYourRide, textViewThumbsDown, textViewThumbsUp, textViewRideEndWithImage;
    private Button buttonEndRideSkip, buttonEndRideInviteFriends;
    private ScrollView scrollViewRideSummary;

    private int viewType = -1;
    private String dateValue = "", endRideGoodFeedbackText;
    private double orderAmount = 0;
    private String orderId = "";

    public boolean isUpbuttonClicked = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_feedback, container, false);

        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);
        updateUI();
        try {
            if(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getFreshClientId())) {
				viewType = Data.getFreshData().getFeedbackViewType();
				dateValue = Data.getFreshData().getFeedbackDeliveryDate();
				orderAmount = Data.getFreshData().getAmount();
				orderId = Data.getFreshData().getOrderId();
				activity.getTopBar().title.setText(getResources().getString(R.string.fresh));
                endRideGoodFeedbackText = Data.getFreshData().getRideEndGoodFeedbackText();
			} else if(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMealsClientId())){
				viewType = Data.getMealsData().getFeedbackViewType();
				dateValue = Data.getMealsData().getFeedbackDeliveryDate();
				orderAmount = Data.getMealsData().getAmount();
				orderId = Data.getMealsData().getOrderId();
				activity.getTopBar().title.setText(getResources().getString(R.string.meals));
                endRideGoodFeedbackText = Data.getMealsData().getRideEndGoodFeedbackText();
			} else {
				activity.finish();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(TextUtils.isEmpty(orderId))
            activity.finish();


        mainLayout = (RelativeLayout) rootView.findViewById(R.id.mainLayout);
        new ASSL(activity, mainLayout, 1134, 720, false);

        scrollViewRideSummary = (ScrollView) rootView.findViewById(R.id.scrollViewRideSummary);

        relativeLayoutGreat = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutGreat);
        relativeLayoutGreat.setVisibility(View.GONE);
        relativeLayoutRideEndWithImage = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRideEndWithImage);
        relativeLayoutRideEndWithImage.setVisibility(View.GONE);

        textViewThanks = (TextView) rootView.findViewById(R.id.textViewThanks);
        textViewRSTotalFare = (TextView) rootView.findViewById(R.id.textViewRSTotalFare);
        textViewRSData = (TextView) rootView.findViewById(R.id.textViewRSData);
        textViewRSCashPaidValue = (TextView) rootView.findViewById(R.id.textViewRSCashPaidValue);
        textViewRSInvoice = (TextView) rootView.findViewById(R.id.textViewRSInvoice);
        textViewRSRateYourRide = (TextView) rootView.findViewById(R.id.textViewRSRateYourRide);
        textViewThumbsDown = (TextView) rootView.findViewById(R.id.textViewThumbsDown);
        textViewThumbsUp = (TextView) rootView.findViewById(R.id.textViewThumbsUp);

        textViewThanks.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewRSTotalFare.setTypeface(Fonts.avenirNext(activity));
        textViewRSData.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewRSCashPaidValue.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewRSInvoice.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewRSRateYourRide.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewThumbsDown.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewThumbsUp.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);

        textViewRSCashPaidValue.setText(getResources().getString(R.string.rupee)
                + "" + Utils.getMoneyDecimalFormat().format(orderAmount));
        textViewRSData.setText(""+dateValue);

        linearLayoutRideSummaryContainer = (LinearLayout) rootView.findViewById(R.id.linearLayoutRideSummaryContainer);
        linearLayoutRSViewInvoice = (LinearLayout) rootView.findViewById(R.id.linearLayoutRSViewInvoice);

        imageViewRideEndWithImage = (ImageView) rootView.findViewById(R.id.imageViewRideEndWithImage);
        textViewRideEndWithImage = (TextView) rootView.findViewById(R.id.textViewRideEndWithImage); textViewRideEndWithImage.setTypeface(Fonts.mavenMedium(activity));
        imageViewThumbsUpGif = (ImageView) rootView.findViewById(R.id.imageViewThumbsUpGif);
        imageviewType = (ImageView) rootView.findViewById(R.id.imageview_type);
        imageViewThumbsDown = (ImageView) rootView.findViewById(R.id.imageViewThumbsDown);
        imageViewThumbsUp = (ImageView) rootView.findViewById(R.id.imageViewThumbsUp);

        buttonEndRideSkip = (Button) rootView.findViewById(R.id.buttonEndRideSkip);
        buttonEndRideInviteFriends = (Button) rootView.findViewById(R.id.buttonEndRideInviteFriends);


        if(Config.getFreshClientId().equals(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()))) {
            imageviewType.setImageResource(R.drawable.feedback_fresh);
        } else {
            imageviewType.setImageResource(R.drawable.feedback_meals);
        }
        buttonEndRideSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressed();
            }
        });

        buttonEndRideInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Data.LOCAL_BROADCAST);
                // You can also include some extra data.
                intent.putExtra("message", "This is my message!");
                intent.putExtra("open_type", 1);
                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
                backPressed();
            }
        });

        linearLayoutRSViewInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOrderData();
            }
        });

        imageViewThumbsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendQuery(0);
                openSupportFragment();

            }
        });

        imageViewThumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpbuttonClicked = true;
                sendQuery(1);
                if(viewType != -1){
                    if(viewType == RideEndGoodFeedbackViewType.RIDE_END_IMAGE_1.getOrdinal()){
                        endRideWithImages(R.drawable.ride_end_image_1);
                    } else if(viewType == RideEndGoodFeedbackViewType.RIDE_END_IMAGE_2.getOrdinal()){
                        endRideWithImages(R.drawable.ride_end_image_2);
                    } else if(viewType == RideEndGoodFeedbackViewType.RIDE_END_IMAGE_3.getOrdinal()){
                        endRideWithImages(R.drawable.ride_end_image_3);
                    } else if(viewType == RideEndGoodFeedbackViewType.RIDE_END_IMAGE_4.getOrdinal()){
                        endRideWithImages(R.drawable.ride_end_image_4);
                    } else if(viewType == RideEndGoodFeedbackViewType.RIDE_END_GIF.getOrdinal()){
                        endRideWithGif();
                    }
                }
            }
        });
        activity.fragmentUISetup(this);
        return rootView;
    }

    private void updateUI() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                update();
            }
        }, 500);
    }

    private void update() {
        try {
            activity.fragmentUISetup(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void endRideWithGif(){
        relativeLayoutGreat.setVisibility(View.VISIBLE);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageViewThumbsUpGif);
        Glide.with(activity)
                .load(R.drawable.android_thumbs_up)
                .placeholder(R.drawable.great_place_holder)
                //.fitCenter()
                .into(imageViewTarget);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageViewThumbsUpGif.setImageDrawable(null);
            }
        }, 3000);
    }

    private void endRideWithImages(int drawable){
        try {
            relativeLayoutRideEndWithImage.setVisibility(View.VISIBLE);
            imageViewRideEndWithImage.setImageResource(drawable);
            textViewRideEndWithImage.setText(endRideGoodFeedbackText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.buttonSubmit:
//                if (ratingType == 0)
//                    FlurryEventLogger.event(PAYMENT_SCREEN, "Dislike", SUBMIT_FEEDBACK);
//                else
//                    FlurryEventLogger.event(PAYMENT_SCREEN, "Like", SUBMIT_FEEDBACK);
//
//                if (ratingType == -1) {
//                    Toast.makeText(activity, "Please rate us", Toast.LENGTH_SHORT).show();
//                } else {
//                    sendQuery(commentBox.getText().toString().trim(), ratingType);
//                }
//                break;
//            case R.id.textView_skip:
//                FlurryEventLogger.event(PAYMENT_SCREEN, SKIP_FEEDBACK, SKIP_FEEDBACK);
//                skipValue = "1";
//                sendQuery("", ratingType);
//                activity.onBackPressed();
//                //activity.finish();
//                break;
//            default:
//
//                break;
//
//
//        }

    }

    /**
     * Method used to send order feedback value
     *
     * @param rating
     */
    private void sendQuery(final int rating) {
        try {
            if(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getFreshClientId())) {
                Data.getFreshData().setPendingFeedback(0);
            } else if(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMealsClientId())){
                Data.getMealsData().setPendingFeedback(0);
            } else {
                activity.finish();
            }
            if (AppStatus.getInstance(MyApplication.getInstance()).isOnline(MyApplication.getInstance())) {
                //DialogPopup.showLoadingDialog(activity, "loading...");

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.ORDER_ID, ""+orderId);
                params.put(Constants.RATING, "" + rating);
                params.put(Constants.RATING_TYPE, "0");
                params.put(Constants.INTERATED, "1");
                params.put(Constants.KEY_CLIENT_ID, ""+ Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));

                RestClient.getFreshApiService().orderFeedback(params, new Callback<OrderHistoryResponse>() {
                    @Override
                    public void success(final OrderHistoryResponse notificationInboxResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();

                        try {

                            if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                if(rating == 1) {
                                    // for Good rating
                                    if(viewType == RideEndGoodFeedbackViewType.RIDE_END_GIF.getOrdinal()){
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                backPressed();
                                            }
                                        }, 3000);
                                    } else if(viewType == RideEndGoodFeedbackViewType.RIDE_END_NONE.getOrdinal()
                                            ) {
                                        backPressed();
                                    }
                                } else if(rating == 0) {
                                    // for bad rating

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();

                            DialogPopup.dialogNoInternet(activity, DialogErrorType.CONNECTION_LOST,
                                    new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                                        @Override
                                        public void positiveClick(View view) {
                                            sendQuery(rating);
                                        }

                                        @Override
                                        public void neutralClick(View view) {

                                        }

                                        @Override
                                        public void negativeClick(View view) {

                                        }
                                    });

                    }
                });
            } else {

                    DialogPopup.dialogNoInternet(activity,
                            Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,

                            new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                                @Override
                                public void positiveClick(View v) {
                                    sendQuery(rating);
                                }

                                @Override
                                public void neutralClick(View v) {

                                }

                                @Override
                                public void negativeClick(View v) {

                                }
                            });

            }
        } catch (Exception e) {
            DialogPopup.dismissLoadingDialog();
            e.printStackTrace();
        }
    }

    /**
     * Method used to get order information
     */
    private void getOrderData() {
        try {
            if(AppStatus.getInstance(activity).isOnline(activity)) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                HashMap<String, String> params = new HashMap<>();
                params.put("access_token", Data.userData.accessToken);
                params.put("order_id", "" + orderId);
                params.put(Constants.KEY_CLIENT_ID, ""+ Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));
                params.put(Constants.INTERATED, "1");
                RestClient.getFreshApiService().orderHistory(params, new Callback<HistoryResponse>() {
                    @Override
                    public void success(HistoryResponse historyResponse, Response response) {
                        String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
                        Log.i("Server response", "response = " + responseStr);
                        try {

                            JSONObject jObj = new JSONObject(responseStr);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt("flag");
                                if (ApiResponseFlags.RECENT_RIDES.getOrdinal() == flag) {
                                    //activity.openOrderInvoice(historyResponse.getData().get(0));
                                    activity.getTransactionUtils().openOrderSummaryFragment(activity,
                                            activity.getRelativeLayoutContainer(), historyResponse.getData().get(0));
                                } else {
                                    updateListData("Some error occurred, tap to retry", true);
                                }
                            } else {
                                updateListData("Some error occurred, tap to retry", true);
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            updateListData("Some error occurred, tap to retry", true);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("TAG", "getRecentRidesAPI error="+error.toString());
                        DialogPopup.dismissLoadingDialog();

                        updateListData("Some error occurred, tap to retry", true);
                    }
                });
            }
            else {
                updateListData("No internet connection, tap to retry", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateListData(String message, boolean errorOccurred){
        if(errorOccurred){

            DialogPopup.dialogNoInternet(activity,
                    "", message,

                    new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                        @Override
                        public void positiveClick(View v) {
                            getOrderData();
                        }

                        @Override
                        public void neutralClick(View v) {

                        }

                        @Override
                        public void negativeClick(View v) {

                        }
                    });
        }

    }



    /**
     * Method used to open
     */
    private void openSupportFragment() {

        Intent intent = new Intent(activity, SupportActivity.class);
        intent.putExtra(Constants.INTENT_KEY_FROM_BAD, 1);
        intent.putExtra(Constants.KEY_ORDER_ID, Integer.parseInt(orderId));
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        backPressed();
    }

    private void backPressed() {
        try {
            activity.getSupportFragmentManager().popBackStack(FeedbackFragment.class.getName(), getFragmentManager().POP_BACK_STACK_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
            activity.getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
            if(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getFreshClientId())) {
                activity.getTopBar().title.setText(getResources().getString(R.string.fresh));
            } else if(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMealsClientId())){
                activity.getTopBar().title.setText(getResources().getString(R.string.meals));
            }
        }
    }
}
