package com.sabkuchfresh.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.commoncalls.SendFeedbackQuery;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.utils.RatingBarMenuFeedback;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.adapters.FeedbackReasonsAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.FeedbackReason;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.dialogs.RateAppDialog;
import product.clicklabs.jugnoo.home.models.RateAppDialogContent;
import product.clicklabs.jugnoo.home.models.RideEndGoodFeedbackViewType;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollGridView;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by gurmail on 24/05/16.
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener, FlurryEventNames {


    private View rootView;
    private ImageView imageViewThumbsDown, imageViewThumbsUp, imageviewType, imageViewThumbsUpGif, imageViewRideEndWithImage,
            ivOffering;
    private FreshActivity activity;
    private LinearLayout linearLayoutRideSummary, linearLayoutRSViewInvoice, linearLayoutRideSummaryContainer, llBadReason;
    private RelativeLayout mainLayout, relativeLayoutGreat, relativeLayoutRideEndWithImage;
    private TextView textViewThanks, textViewRSTotalFare, textViewRSData, textViewRSCashPaidValue, tvItems,
            textViewRSInvoice, textViewRSRateYourRide, textViewThumbsDown, textViewThumbsUp, textViewRideEndWithImage;
    private Button buttonEndRideSkip, buttonEndRideInviteFriends;
    private ScrollView scrollViewRideSummary;
    private TextView textViewRSScroll;

    private int viewType = -1;
    private String dateValue = "", endRideGoodFeedbackText;
    private double orderAmount = 0;
    private String orderId = "", feedbackOrderItems = "";
    private int rateApp = 0;
    private RateAppDialogContent rateAppDialogContent;

    public boolean isUpbuttonClicked = false;
    private ProductType productType;
    private NonScrollGridView gridViewRSFeedbackReasons;
    private FeedbackReasonsAdapter feedbackReasonsAdapter;
    private TextView textViewRSOtherError;
    private Button buttonRSSubmitFeedback;
    private EditText editTextRSFeedback;
    private LinearLayout llThumbsRating;
    private RatingBarMenuFeedback ratingBarMenuFeedback;
    private ArrayList<FeedbackReason> negativeReasons = new ArrayList<>();
    private TextView textViewRSWhatImprove;
    private ArrayList<FeedbackReason> positiveReasons;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);
        updateUI();
        try {
            rateApp = Data.userData.getCustomerRateAppFlag();
            rateAppDialogContent = Data.userData.getRateAppDialogContent();
            negativeReasons.clear();
            if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId())
                    .equals(Config.getFreshClientId())) {
                viewType = Data.getFreshData().getFeedbackViewType();
                dateValue = Data.getFreshData().getFeedbackDeliveryDate();
                orderAmount = Data.getFreshData().getAmount();
                orderId = Data.getFreshData().getOrderId();
                activity.getTopBar().title.setText(getResources().getString(R.string.fresh));
                endRideGoodFeedbackText = Data.getFreshData().getRideEndGoodFeedbackText();
                productType = ProductType.FRESH;
                for (int i = 0; i < Data.getFreshData().getNegativeFeedbackReasons().length(); i++) {
                    negativeReasons.add(new FeedbackReason(Data.getFreshData().getNegativeFeedbackReasons().get(i).toString()));
                }


            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId())
                    .equals(Config.getMealsClientId())) {
                viewType = Data.getMealsData().getFeedbackViewType();
                dateValue = Data.getMealsData().getFeedbackDeliveryDate();
                orderAmount = Data.getMealsData().getAmount();
                orderId = Data.getMealsData().getOrderId();
                activity.getTopBar().title.setText(getResources().getString(R.string.meals));
                feedbackOrderItems = Data.getMealsData().getFeedbackOrderItems();
                endRideGoodFeedbackText = Data.getMealsData().getRideEndGoodFeedbackText();
                productType = ProductType.MEALS;
                for (int i = 0; i < Data.getMealsData().getNegativeFeedbackReasons().length(); i++) {
                    negativeReasons.add(new FeedbackReason(Data.getMealsData().getNegativeFeedbackReasons().get(i).toString()));
                }
            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId())
                    .equals(Config.getGroceryClientId())) {
                viewType = Data.getGroceryData().getFeedbackViewType();
                dateValue = Data.getGroceryData().getFeedbackDeliveryDate();
                orderAmount = Data.getGroceryData().getAmount();
                orderId = Data.getGroceryData().getOrderId();
                activity.getTopBar().title.setText(getResources().getString(R.string.grocery));
                endRideGoodFeedbackText = Data.getGroceryData().getRideEndGoodFeedbackText();
                productType = ProductType.GROCERY;
                for (int i = 0; i < Data.getGroceryData().getNegativeFeedbackReasons().length(); i++) {
                    negativeReasons.add(new FeedbackReason(Data.getGroceryData().getNegativeFeedbackReasons().get(i).toString()));
                }
            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId())
                    .equals(Config.getMenusClientId())) {

                viewType = Data.getMenusData().getFeedbackViewType();
                dateValue = Data.getMenusData().getFeedbackDeliveryDate();
                orderAmount = Data.getMenusData().getAmount();
                orderId = Data.getMenusData().getOrderId();
                activity.getTopBar().title.setText(getResources().getString(R.string.menus));
                endRideGoodFeedbackText = Data.getMenusData().getRideEndGoodFeedbackText();
                productType = ProductType.MENUS;
                for (int i = 0; i < Data.getMenusData().getNegativeFeedbackReasons().length(); i++) {
                    negativeReasons.add(new FeedbackReason(Data.getMenusData().getNegativeFeedbackReasons().get(i).toString()));
                }
                if (Data.getMenusData().getPositiveFeedbackReasons() != null) {
                    positiveReasons = new ArrayList<>();
                    for (int i = 0; i < Data.getMenusData().getPositiveFeedbackReasons().length(); i++) {
                        positiveReasons.add(new FeedbackReason(Data.getMenusData().getPositiveFeedbackReasons().get(i).toString()));
                    }
                }

            } else {
                activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (TextUtils.isEmpty(orderId))
            activity.finish();
        rootView = inflater.inflate(R.layout.layout_feedback, container, false);

        setUp();

        return rootView;
    }

    private void setUp() {
        mainLayout = (RelativeLayout) rootView.findViewById(R.id.mainLayout);
        new ASSL(activity, mainLayout, 1134, 720, false);

        scrollViewRideSummary = (ScrollView) rootView.findViewById(R.id.scrollViewRideSummary);
        linearLayoutRideSummary = (LinearLayout) rootView.findViewById(R.id.linearLayoutRideSummary);
        textViewRSScroll = (TextView) rootView.findViewById(R.id.textViewRSScroll);

        relativeLayoutGreat = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutGreat);
        relativeLayoutGreat.setVisibility(View.GONE);
        relativeLayoutRideEndWithImage = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRideEndWithImage);
        relativeLayoutRideEndWithImage.setVisibility(View.GONE);

        textViewThanks = (TextView) rootView.findViewById(R.id.textViewThanks);
        textViewRSTotalFare = (TextView) rootView.findViewById(R.id.textViewRSTotalFare);
        textViewRSData = (TextView) rootView.findViewById(R.id.textViewRSData);
        tvItems = (TextView) rootView.findViewById(R.id.tvItems);
        tvItems.setTypeface(Fonts.avenirNext(activity));
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
        textViewRSData.setText("" + dateValue);

        if (feedbackOrderItems != null && !feedbackOrderItems.equalsIgnoreCase("")) {
            textViewRSTotalFare.setVisibility(View.GONE);
            tvItems.setVisibility(View.VISIBLE);
            tvItems.setText(feedbackOrderItems);
        }


        linearLayoutRideSummaryContainer = (LinearLayout) rootView.findViewById(R.id.linearLayoutRideSummaryContainer);
        linearLayoutRSViewInvoice = (LinearLayout) rootView.findViewById(R.id.linearLayoutRSViewInvoice);

        imageViewRideEndWithImage = (ImageView) rootView.findViewById(R.id.imageViewRideEndWithImage);
        textViewRideEndWithImage = (TextView) rootView.findViewById(R.id.textViewRideEndWithImage);
        textViewRideEndWithImage.setTypeface(Fonts.mavenMedium(activity));
        imageViewThumbsUpGif = (ImageView) rootView.findViewById(R.id.imageViewThumbsUpGif);
        imageviewType = (ImageView) rootView.findViewById(R.id.imageview_type);
        ivOffering = (ImageView) rootView.findViewById(R.id.ivOffering);
        llThumbsRating = (LinearLayout) rootView.findViewById(R.id.ll_thumbs_rating);
        imageViewThumbsDown = (ImageView) rootView.findViewById(R.id.imageViewThumbsDown);
        imageViewThumbsUp = (ImageView) rootView.findViewById(R.id.imageViewThumbsUp);
        llBadReason = (LinearLayout) rootView.findViewById(R.id.llBadReason);
        gridViewRSFeedbackReasons = (NonScrollGridView) rootView.findViewById(R.id.gridViewRSFeedbackReasons);
        textViewRSOtherError = (TextView) rootView.findViewById(R.id.textViewRSOtherError);
        textViewRSOtherError.setTypeface(Fonts.mavenRegular(activity));

        buttonEndRideSkip = (Button) rootView.findViewById(R.id.buttonEndRideSkip);
        buttonEndRideInviteFriends = (Button) rootView.findViewById(R.id.buttonEndRideInviteFriends);
        textViewRSWhatImprove = ((TextView) rootView.findViewById(R.id.textViewRSWhatImprove));
        textViewRSWhatImprove.setTypeface(Fonts.mavenMedium(activity));
        buttonRSSubmitFeedback = (Button) rootView.findViewById(R.id.buttonRSSubmitFeedback);
        buttonRSSubmitFeedback.setTypeface(Fonts.mavenRegular(activity));
        editTextRSFeedback = (EditText) rootView.findViewById(R.id.editTextRSFeedback);
        ratingBarMenuFeedback = (RatingBarMenuFeedback) rootView.findViewById(R.id.rating_bar);

        if (Config.getFreshClientId().equals(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()))) {
            imageviewType.setImageResource(R.drawable.feedback_fresh);
            ivOffering.setImageResource(R.drawable.ic_fab_fresh);
        } else if (Config.getGroceryClientId().equals(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()))) {
            imageviewType.setImageResource(R.drawable.feedback_grocery);
            ivOffering.setImageResource(R.drawable.ic_fab_grocery);
        } else if (Config.getMenusClientId().equals(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()))) {


            imageviewType.setImageResource(R.drawable.ic_fab_menus);
            ivOffering.setImageResource(R.drawable.ic_fab_menus);

            /**
             Edited by Parminder Singh on 2/10/17 at 12:46 PM
             **/

            llThumbsRating.setVisibility(View.GONE);
            ratingBarMenuFeedback.setVisibility(View.VISIBLE);
            editTextRSFeedback.setHint("Comments..");
            textViewRSWhatImprove.setTag(null);
            ratingBarMenuFeedback.setOnScoreChanged(new RatingBarMenuFeedback.IRatingBarCallbacks() {
                @Override
                public void scoreChanged(float score) {

                    if (llBadReason.getVisibility() != View.VISIBLE) {
                        llBadReason.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollViewRideSummary.smoothScrollTo(0, (int) buttonRSSubmitFeedback.getY());
                            }
                        }, 300);
                    }

                    if (score > 2) {

                        if (textViewRSWhatImprove.getTag() == null || ((int) textViewRSWhatImprove.getTag()) == 0) {
                            textViewRSWhatImprove.setTag(1);
                            textViewRSWhatImprove.setText(R.string.feedback_menu_what_amazing);
                            if (feedbackReasonsAdapter != null)
                                feedbackReasonsAdapter.resetData(true);
                        } else {
                            if (feedbackReasonsAdapter != null)
                                feedbackReasonsAdapter.resetSelectedStates();
                        }

                    } else {
                        if (textViewRSWhatImprove.getTag() == null || ((int) textViewRSWhatImprove.getTag()) == 1) {
                            textViewRSWhatImprove.setTag(0);
                            textViewRSWhatImprove.setText(R.string.feedback_menu_what_wrong);
                            if (feedbackReasonsAdapter != null)
                                feedbackReasonsAdapter.resetData(false);
                        } else {
                            if (feedbackReasonsAdapter != null)
                                feedbackReasonsAdapter.resetSelectedStates();
                        }

                    }

                }
            });


        } else {
            imageviewType.setImageResource(R.drawable.feedback_meals);
            ivOffering.setImageResource(R.drawable.ic_fab_meals);


        }

        try {
            feedbackReasonsAdapter = new FeedbackReasonsAdapter(activity, negativeReasons, positiveReasons,
                    new FeedbackReasonsAdapter.FeedbackReasonsListEventHandler() {
                        @Override
                        public void onLastItemSelected(boolean selected) {
                            if (!selected) {
                                if (textViewRSOtherError.getText().toString().equalsIgnoreCase(getString(R.string.star_required))) {
                                    textViewRSOtherError.setText("");
                                }
                            }
                        }
                    });
            gridViewRSFeedbackReasons.setAdapter(feedbackReasonsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        buttonRSSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comments = feedbackReasonsAdapter.getSelectedReasons();
                //boolean isLastReasonSelected = feedbackReasonsAdapter.isLastSelected();
                String reviewDescription = editTextRSFeedback.getText().toString().trim();
                if (reviewDescription.length() > 0) {
                    if (reviewDescription.length() > 300) {
                        editTextRSFeedback.requestFocus();
                        editTextRSFeedback.setError(getString(R.string.review_must_be_in));
                    } else {
                        if (productType != ProductType.MENUS) {
                            comments = comments + ", " + reviewDescription;
                        }

                    }
                }

                // api call
                if (productType != ProductType.MENUS) {
                    sendQuery(0, comments);
                } else {
                    sumbitMenuFeedback(reviewDescription, comments, (int) ratingBarMenuFeedback.getScore());
                }


            }
        });

        buttonEndRideSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressed(true);
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
                backPressed(true);
            }
        });

        linearLayoutRSViewInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getOrderData();
                new TransactionUtils().openOrderStatusFragment(activity,
                        activity.getRelativeLayoutContainer(), Integer.parseInt(orderId), productType.getOrdinal());
            }
        });

        imageViewThumbsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendQuery(0, "");
                try {
                    imageViewThumbsDown.setImageResource(R.drawable.ic_thumbs_down_active);
                    if (negativeReasons.size() > 0) {
                        llBadReason.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollViewRideSummary.smoothScrollTo(0, (int) buttonRSSubmitFeedback.getY());
                            }
                        }, 300);
                        scrollViewRideSummary.smoothScrollTo(0, (int) buttonRSSubmitFeedback.getY());
                    } else {
                        sendQuery(0, "");
                    }


                    //openSupportFragment();

                    if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getFreshClientId())) {
                        MyApplication.getInstance().logEvent(FirebaseEvents.FRESH_DOWNVOTE, new Bundle());
                    } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMealsClientId())) {
                        MyApplication.getInstance().logEvent(FirebaseEvents.MEALS_DOWNVOTE, new Bundle());
                    } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getGroceryClientId())) {
                        MyApplication.getInstance().logEvent(FirebaseEvents.GROCERY_DOWNVOTE, new Bundle());
                    } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMenusClientId())) {
                        MyApplication.getInstance().logEvent(FirebaseEvents.MENUS_DOWNVOTE, new Bundle());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imageViewThumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBadReason.setVisibility(View.GONE);
                imageViewThumbsDown.setImageResource(R.drawable.ic_thumbs_down);
                afterGoodRating();
                sendQuery(1, "");
                try {
                    if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getFreshClientId())) {
                        MyApplication.getInstance().logEvent(FirebaseEvents.FRESH_UPVOTE, new Bundle());
                    } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMealsClientId())) {
                        MyApplication.getInstance().logEvent(FirebaseEvents.MEALS_UPVOTE, new Bundle());
                    } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getGroceryClientId())) {
                        MyApplication.getInstance().logEvent(FirebaseEvents.GROCERY_UPVOTE, new Bundle());
                    } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMenusClientId())) {
                        MyApplication.getInstance().logEvent(FirebaseEvents.MENUS_UPVOTE, new Bundle());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        activity.fragmentUISetup(this);

//        KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(linearLayoutRideSummary, textViewRSScroll,
//                new KeyboardLayoutListener.KeyBoardStateHandler() {
//            @Override
//            public void keyboardOpened() {
//
//            }
//
//            @Override
//            public void keyBoardClosed() {
//
//            }
//        });
//        linearLayoutRideSummary.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
    }

    private void afterGoodRating() {
        isUpbuttonClicked = true;

        if (viewType != -1) {
            if (viewType == RideEndGoodFeedbackViewType.RIDE_END_IMAGE_1.getOrdinal()) {
                endRideWithImages(R.drawable.ride_end_image_1);
            } else if (viewType == RideEndGoodFeedbackViewType.RIDE_END_IMAGE_2.getOrdinal()) {
                endRideWithImages(R.drawable.ride_end_image_2);
            } else if (viewType == RideEndGoodFeedbackViewType.RIDE_END_IMAGE_3.getOrdinal()) {
                endRideWithImages(R.drawable.ride_end_image_3);
            } else if (viewType == RideEndGoodFeedbackViewType.RIDE_END_IMAGE_4.getOrdinal()) {
                endRideWithImages(R.drawable.ride_end_image_4);
            } else if (viewType == RideEndGoodFeedbackViewType.RIDE_END_GIF.getOrdinal()) {
                endRideWithGif();
            }
        }
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

    private void endRideWithGif() {
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

    private void endRideWithImages(int drawable) {
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
    private void sendQuery(final int rating, final String negativeReasons) {
        try {
            if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getFreshClientId())) {
                Data.getFreshData().setPendingFeedback(0);
            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMealsClientId())) {
                Data.getMealsData().setPendingFeedback(0);
            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getGroceryClientId())) {
                Data.getGroceryData().setPendingFeedback(0);
            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMenusClientId())) {
                Data.getMenusData().setPendingFeedback(0);
            } else {
                activity.finish();
            }
            if (MyApplication.getInstance().isOnline()) {
                //DialogPopup.showLoadingDialog(activity, "loading...");

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.ORDER_ID, "" + orderId);
                params.put(Constants.RATING, "" + rating);
                params.put(Constants.RATING_TYPE, "0");
                params.put(Constants.INTERATED, "1");
                params.put(Constants.COMMENT, negativeReasons);
                params.put(Constants.KEY_CLIENT_ID, "" + Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));

                Callback<OrderHistoryResponse> callback = new Callback<OrderHistoryResponse>() {
                    @Override
                    public void success(final OrderHistoryResponse notificationInboxResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                if (rating == 1) {
                                    // for Good rating
                                    if (viewType == RideEndGoodFeedbackViewType.RIDE_END_GIF.getOrdinal()) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                backPressed(true);
                                            }
                                        }, 3000);
                                    } else if (viewType == RideEndGoodFeedbackViewType.RIDE_END_NONE.getOrdinal()) {
                                        backPressed(true);
                                    }
                                } else if (rating == 0) {
                                    // for bad rating
                                    backPressed(true);
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
                                        sendQuery(rating, negativeReasons);
                                    }

                                    @Override
                                    public void neutralClick(View view) {

                                    }

                                    @Override
                                    public void negativeClick(View view) {

                                    }
                                });

                    }
                };
                new HomeUtil().putDefaultParams(params);
                if (productType == ProductType.MENUS) {
                    RestClient.getMenusApiService().orderFeedback(params, callback);
                } else {
                    RestClient.getFreshApiService().orderFeedback(params, callback);
                }

            } else {
                DialogPopup.dialogNoInternet(activity,
                        Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
                        new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                            @Override
                            public void positiveClick(View v) {
                                sendQuery(rating, negativeReasons);
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
            if (MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(activity, "Loading...");

                HashMap<String, String> params = new HashMap<>();
                params.put("access_token", Data.userData.accessToken);
                params.put("order_id", "" + orderId);
                params.put(Constants.KEY_CLIENT_ID, "" + Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));
                params.put(Constants.INTERATED, "1");
                Callback<HistoryResponse> callback = new Callback<HistoryResponse>() {
                    @Override
                    public void success(HistoryResponse historyResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i("Server response", "response = " + responseStr);
                        try {

                            JSONObject jObj = new JSONObject(responseStr);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                if (ApiResponseFlags.RECENT_RIDES.getOrdinal() == flag) {
                                    new TransactionUtils().openOrderStatusFragment(activity,
                                            activity.getRelativeLayoutContainer(), historyResponse.getData().get(0).getOrderId(),
                                            historyResponse.getData().get(0).getProductType());
                                } else {
                                    updateListData(message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            updateListData(Data.SERVER_ERROR_MSG);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("TAG", "getRecentRidesAPI error=" + error.toString());
                        DialogPopup.dismissLoadingDialog();
                        updateListData(Data.SERVER_NOT_RESOPNDING_MSG);
                    }
                };

                new HomeUtil().putDefaultParams(params);
                if (productType == ProductType.MENUS) {
                    RestClient.getMenusApiService().orderHistory(params, callback);
                } else {
                    RestClient.getFreshApiService().orderHistory(params, callback);
                }
            } else {
                updateListData(Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateListData(String message) {
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message,
                activity.getString(R.string.retry), activity.getString(R.string.cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getOrderData();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, false, false);
    }


    private void backPressed(boolean goodRating) {
        activity.setRefreshCart(true);
        try {
            activity.getSupportFragmentManager().popBackStack(FeedbackFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
            activity.getSupportFragmentManager().popBackStackImmediate();
        }

        if (goodRating && rateApp == 1) {
            getRateAppDialog().show(rateAppDialogContent);
        }
    }

    private RateAppDialog rateAppDialog;

    private RateAppDialog getRateAppDialog() {
        if (rateAppDialog == null) {
            rateAppDialog = new RateAppDialog(activity);
        }
        return rateAppDialog;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
            if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getFreshClientId())) {
                activity.getTopBar().title.setText(getResources().getString(R.string.fresh));
            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMealsClientId())) {
                activity.getTopBar().title.setText(getResources().getString(R.string.meals));
            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getGroceryClientId())) {
                activity.getTopBar().title.setText(getResources().getString(R.string.grocery));
            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMenusClientId())) {
                activity.getTopBar().title.setText(getResources().getString(R.string.menus));
            }
        }
    }

    SendFeedbackQuery sendFeedbackQuery;

    private void sumbitMenuFeedback(String reviewDesc, String comments, int score) {
        DialogPopup.showLoadingDialog(activity, "");
        if (sendFeedbackQuery == null) {
            sendFeedbackQuery = new SendFeedbackQuery();
        }

        sendFeedbackQuery.sendQuery(Integer.parseInt(orderId), -1, ProductType.MENUS, score, Constants.RATING_TYPE_STAR, comments, reviewDesc, activity,
                new SendFeedbackQuery.FeedbackResultListener() {
                    @Override
                    public void onSendFeedbackResult(boolean isSuccess, int rating) {
                        if (isSuccess) {


                            if (rating > 2) {
                                // for Good rating
                                afterGoodRating();
                                if (viewType == RideEndGoodFeedbackViewType.RIDE_END_GIF.getOrdinal()) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            backPressed(true);
                                        }
                                    }, 3000);
                                } else if (viewType == RideEndGoodFeedbackViewType.RIDE_END_NONE.getOrdinal()) {
                                    backPressed(true);

                                }


                            } else {
                                // for bad rating
                                backPressed(true);
                            }


                          /*  activity.performBackPressed();
                            product.clicklabs.jugnoo.utils.Utils.showToast(activity, activity.getString(R.string.thanks_for_your_valuable_feedback));*/
                        }
                    }
                });
    }

}
