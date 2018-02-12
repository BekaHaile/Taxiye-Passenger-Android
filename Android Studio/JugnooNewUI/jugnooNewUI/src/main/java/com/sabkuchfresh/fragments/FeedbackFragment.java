package com.sabkuchfresh.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Pair;
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
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.commoncalls.SendFeedbackQuery;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.pros.api.ApiProsOrderStatus;
import com.sabkuchfresh.pros.models.ProsOrderStatus;
import com.sabkuchfresh.pros.models.ProsOrderStatusResponse;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.utils.RatingBarMenuFeedback;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
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
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
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
public class FeedbackFragment extends Fragment implements GAAction, View.OnClickListener {


    public static final String FEEDBACK_CLIENT_ID = "feedback_client_id";
    private View rootView;
    private ImageView imageViewThumbsDown, imageViewThumbsUp, imageviewType, imageViewThumbsUpGif, imageViewRideEndWithImage,
            ivOffering;
    private FreshActivity activity;
    private LinearLayout linearLayoutRideSummary, linearLayoutRSViewInvoice, linearLayoutRideSummaryContainer, llBadReason;
    private RelativeLayout mainLayout, relativeLayoutGreat, relativeLayoutRideEndWithImage;
    private TextView textViewThanks, textViewRSTotalFare, textViewRSData, textViewRSCashPaid, textViewRSCashPaidValue, tvItems,
            textViewRSInvoice, textViewRSRateYourRide, textViewThumbsDown, textViewThumbsUp, textViewRideEndWithImage;
    private Button buttonEndRideSkip, buttonEndRideInviteFriends;
    private ScrollView scrollViewRideSummary;
    private TextView textViewRSScroll;

    private int viewType = RideEndGoodFeedbackViewType.RIDE_END_NONE.getOrdinal();
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

    private int jobId = 0;
    private String lastClientId = "";
    private int merchantCategoryId;
    private KeyboardLayoutListener.KeyBoardStateHandler mKeyBoardStateHandler;


    public static Fragment newInstance(String clientId) {
        Bundle bundle  = new Bundle();
        bundle.putString(FEEDBACK_CLIENT_ID,clientId);
        FeedbackFragment feedbackFragment = new FeedbackFragment();
        feedbackFragment.setArguments(bundle);
        return feedbackFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().containsKey(FEEDBACK_CLIENT_ID)){
            lastClientId = getArguments().getString(FEEDBACK_CLIENT_ID);
        }else{
            lastClientId = Config.getLastOpenedClientId(activity);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_feedback, container, false);
        activity = (FreshActivity) getActivity();
        mainLayout = (RelativeLayout) rootView.findViewById(R.id.mainLayout);
        new ASSL(activity, mainLayout, 1134, 720, false);
        try {
            rateApp = Data.userData.getCustomerRateAppFlag();
            rateAppDialogContent = Data.userData.getRateAppDialogContent();
            negativeReasons.clear();
            if (lastClientId.equals(Config.getFreshClientId())) {
                viewType = Data.getFreshData().getFeedbackViewType();
                dateValue = Data.getFreshData().getFeedbackDeliveryDate();
                orderAmount = Data.getFreshData().getAmount();
                orderId = Data.getFreshData().getOrderId();
                activity.getTopBar().title.setText(getResources().getString(R.string.fatafat));
                endRideGoodFeedbackText = Data.getFreshData().getRideEndGoodFeedbackText();
                productType = ProductType.FRESH;
                negativeReasons = Data.getFreshData().getNegativeFeedbackReasons();

            } else if (lastClientId.equals(Config.getMealsClientId())) {
                viewType = Data.getMealsData().getFeedbackViewType();
                dateValue = Data.getMealsData().getFeedbackDeliveryDate();
                orderAmount = Data.getMealsData().getAmount();
                orderId = Data.getMealsData().getOrderId();
                activity.getTopBar().title.setText(getResources().getString(R.string.meals));
                feedbackOrderItems = Data.getMealsData().getFeedbackOrderItems();
                endRideGoodFeedbackText = Data.getMealsData().getRideEndGoodFeedbackText();
                productType = ProductType.MEALS;
                negativeReasons = Data.getMealsData().getNegativeFeedbackReasons();
            } else if (lastClientId.equals(Config.getGroceryClientId())) {
                viewType = Data.getGroceryData().getFeedbackViewType();
                dateValue = Data.getGroceryData().getFeedbackDeliveryDate();
                orderAmount = Data.getGroceryData().getAmount();
                orderId = Data.getGroceryData().getOrderId();
                activity.getTopBar().title.setText(getResources().getString(R.string.grocery));
                endRideGoodFeedbackText = Data.getGroceryData().getRideEndGoodFeedbackText();
                productType = ProductType.GROCERY;
                negativeReasons = Data.getGroceryData().getNegativeFeedbackReasons();

            } else if (lastClientId.equals(Config.getMenusClientId()) || lastClientId.equals(Config.getDeliveryCustomerClientId()) ||
                    lastClientId.equals(Config.getFeedClientId())) {


                if (lastClientId.equals(Config.getFeedClientId())) {
                    productType = ProductType.FEED;
                } else if (lastClientId.equals(Config.getDeliveryCustomerClientId())) {
                    productType = ProductType.DELIVERY_CUSTOMER;
                } else {
                    productType = ProductType.MENUS;
                }

                setTitleForMenusDeliveryOrFeed();
                viewType = getMenusOrDeliveryData().getFeedbackViewType();
                dateValue = getMenusOrDeliveryData().getFeedbackDeliveryDate();
                orderAmount = getMenusOrDeliveryData().getAmount();
                orderId = getMenusOrDeliveryData().getOrderId();
                if(getMenusOrDeliveryData() instanceof LoginResponse.DeliveryCustomer){
                    merchantCategoryId = ((LoginResponse.DeliveryCustomer)getMenusOrDeliveryData()).getMerchantCategoryId();
                }

                endRideGoodFeedbackText = getMenusOrDeliveryData().getRideEndGoodFeedbackText();
                negativeReasons = getMenusOrDeliveryData().getNegativeFeedbackReasons();
                positiveReasons = getMenusOrDeliveryData().getPositiveFeedbackReasons();


            } else if(lastClientId.equals(Config.getProsClientId())){
                jobId = Prefs.with(activity).getInt(Constants.SP_PROS_LAST_COMPLETE_JOB_ID, 0);
                productType = ProductType.PROS;
                getApiProsOrderStatus().getOrderData(activity, jobId);
            } else {
                activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (TextUtils.isEmpty(orderId) && jobId <= 0)
            activity.finish();


        GAUtils.trackScreenView(activity.getGaCategory()+FEEDBACK);

        setUp();



        return rootView;
    }

    private void setTitleForMenusDeliveryOrFeed() {
        if(!TextUtils.isEmpty(getMenusOrDeliveryData().getRestaurantName())){
            activity.getTopBar().title.setText(getMenusOrDeliveryData().getRestaurantName());
        } else {
            activity.getTopBar().title.setText(activity.getString(R.string.menus));
        }
    }

    private LoginResponse.Menus getMenusOrDeliveryData() {
        if(lastClientId.equals(Config.getDeliveryCustomerClientId()) || activity.isDeliveryOpenInBackground()){
            return Data.getDeliveryCustomerData();
        } else {
            return Data.getMenusData();
        }
    }

    private void setUp() {

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
        textViewRSCashPaid = (TextView) rootView.findViewById(R.id.textViewRSCashPaid);
        textViewRSCashPaidValue = (TextView) rootView.findViewById(R.id.textViewRSCashPaidValue);
        textViewRSInvoice = (TextView) rootView.findViewById(R.id.textViewRSInvoice);
        textViewRSRateYourRide = (TextView) rootView.findViewById(R.id.textViewRSRateYourRide);
        textViewThumbsDown = (TextView) rootView.findViewById(R.id.textViewThumbsDown);
        textViewThumbsUp = (TextView) rootView.findViewById(R.id.textViewThumbsUp);

        textViewThanks.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewRSTotalFare.setTypeface(Fonts.avenirNext(activity));
        textViewRSData.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewRSCashPaid.setTypeface(Fonts.mavenMedium(activity)); textViewRSCashPaid.setVisibility(View.GONE);
        textViewRSCashPaidValue.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewRSInvoice.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewRSRateYourRide.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewThumbsDown.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);
        textViewThumbsUp.setTypeface(Fonts.avenirNext(activity), Typeface.BOLD);

        textViewRSCashPaidValue.setText(getResources().getString(R.string.rupee)
                + "" + Utils.getMoneyDecimalFormat().format(orderAmount));
        textViewRSData.setText("" + dateValue);

        if (!TextUtils.isEmpty(feedbackOrderItems)) {
            textViewRSTotalFare.setVisibility(View.GONE);
            tvItems.setVisibility(View.VISIBLE);
            tvItems.setText(feedbackOrderItems);
        }

        if(productType == ProductType.PROS){
            textViewRSTotalFare.setText("");
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

        if (Config.getFreshClientId().equals(lastClientId)) {
            imageviewType.setImageResource(R.drawable.ic_grocery_grey_vector);
            ivOffering.setImageResource(R.drawable.ic_grocery_grey_vector);
        } else if (Config.getMenusClientId().equals(lastClientId) || Config.getFeedClientId().equals(lastClientId)
                || Config.getDeliveryCustomerClientId().equals(lastClientId)
                || Config.getProsClientId().equals(lastClientId)) {


            if(Config.getProsClientId().equals(lastClientId)){
                imageviewType.setImageResource(R.drawable.ic_pros_grey);
                ivOffering.setImageResource(R.drawable.ic_pros_grey);
            } else if(Config.getFeedClientId().equals(lastClientId)){
                imageviewType.setImageResource(R.drawable.ic_menus_delivery_customer_grey);
                ivOffering.setImageResource(R.drawable.ic_menus_delivery_customer_grey);
            } else if(Config.getDeliveryCustomerClientId().equals(lastClientId) && merchantCategoryId !=Constants.CATEGORY_ID_RESTAURANTS){
                imageviewType.setImageResource(R.drawable.ic_menus_delivery_customer_grey);
                ivOffering.setImageResource(R.drawable.ic_menus_delivery_customer_grey);
            }else {
                imageviewType.setImageResource(R.drawable.ic_menus_grey);
                ivOffering.setImageResource(R.drawable.ic_menus_grey);
            }


            llThumbsRating.setVisibility(View.GONE);
            ratingBarMenuFeedback.setVisibility(View.VISIBLE);
            editTextRSFeedback.setHint(R.string.comments);
            textViewRSWhatImprove.setTag(null);
            ratingBarMenuFeedback.setOnScoreChanged(new RatingBarMenuFeedback.IRatingBarCallbacks() {
                @Override
                public void scoreChanged(float score) {

                    if (llBadReason.getVisibility() != View.VISIBLE) {
                        llBadReason.setVisibility(View.VISIBLE);
                        activity.getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollViewRideSummary.smoothScrollTo(0, (int) buttonRSSubmitFeedback.getY());
                            }
                        }, 300);
                    }

                    if (score > 2) {

                        if (textViewRSWhatImprove.getTag() == null || ((int) textViewRSWhatImprove.getTag()) == 0) {
                            textViewRSWhatImprove.setTag(1);
                            if (feedbackReasonsAdapter != null)
                                feedbackReasonsAdapter.resetData(true);
                        } else {
                            if (feedbackReasonsAdapter != null)
                                feedbackReasonsAdapter.resetSelectedStates();
                        }
                        if(score > 2 && score < 5){
                            textViewRSWhatImprove.setText(R.string.what_could_we_improve);
                        } else {
                            textViewRSWhatImprove.setText(R.string.feedback_menu_what_amazing);
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


        }
        else {
            imageviewType.setImageResource(R.drawable.ic_meals_grey);
            ivOffering.setImageResource(R.drawable.ic_meals_grey);


        }

        try {
            feedbackReasonsAdapter = new FeedbackReasonsAdapter(activity, negativeReasons, positiveReasons,
                    new FeedbackReasonsAdapter.FeedbackReasonsListEventHandler() {
                        @Override
                        public void onLastItemSelected(boolean selected, String name) {
                            if (!selected) {
                                if (textViewRSOtherError.getText().toString().equalsIgnoreCase(getString(R.string.star_required))) {
                                    textViewRSOtherError.setText("");
                                }
                            } else {
                                GAUtils.event(activity.getGaCategory(), FEEDBACK, ISSUE+SELECTED+name);
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
                        return;
                    } else {
                        GAUtils.event(activity.getGaCategory(), FEEDBACK, COMMENT+ADDED);
                    }
                }

                if (productType != ProductType.MENUS && productType != ProductType.DELIVERY_CUSTOMER) {
                    comments = comments+((TextUtils.isEmpty(comments) || TextUtils.isEmpty(reviewDescription))?"":", ")+reviewDescription;
                }

                // api call
                if(productType == ProductType.PROS){
                    apiProsFeedback((int) ratingBarMenuFeedback.getScore(), comments);
                } else if (productType != ProductType.MENUS && productType != ProductType.DELIVERY_CUSTOMER) {
                    sendQuery(0, comments);
                } else {
                    sumbitMenusOrDeliveryFeedback(reviewDescription, comments, (int) ratingBarMenuFeedback.getScore());
                }
                Utils.hideSoftKeyboard(activity, editTextRSFeedback);


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
                if(productType == ProductType.PROS){
                    activity.getTransactionUtils().addProsOrderStatusFragment(activity, activity.getRelativeLayoutContainer(), jobId, productType.getOrdinal());
                } else {
                    new TransactionUtils().openOrderStatusFragment(activity,
                            activity.getRelativeLayoutContainer(), Integer.parseInt(orderId), productType.getOrdinal(), 0);
                }
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
                        activity.getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollViewRideSummary.smoothScrollTo(0, (int) buttonRSSubmitFeedback.getY());
                            }
                        }, 300);
                        scrollViewRideSummary.smoothScrollTo(0, (int) buttonRSSubmitFeedback.getY());
                    } else {
                        sendQuery(0, "");
                    }

                    GAUtils.event(activity.getGaCategory(), FEEDBACK, THUMB_DOWN+CLICKED);

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
                GAUtils.event(activity.getGaCategory(), FEEDBACK, THUMB_UP+CLICKED);
            }
        });
        activity.fragmentUISetup(this);

        // set up keyboard listener
        mKeyBoardStateHandler = new KeyboardLayoutListener.KeyBoardStateHandler() {

            @Override
            public void keyboardOpened() {
                scrollViewRideSummary.scrollTo(0, editTextRSFeedback.getBottom());
            }

            @Override
            public void keyBoardClosed() {

            }
        };
        // register for keyboard event
        activity.registerForKeyBoardEvent(mKeyBoardStateHandler);

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

    private void endRideWithGif() {
        relativeLayoutGreat.setVisibility(View.VISIBLE);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageViewThumbsUpGif);
        Glide.with(activity)
                .load(R.drawable.android_thumbs_up)
                .placeholder(R.drawable.great_place_holder)
                //.fitCenter()
                .into(imageViewTarget);
        activity.getHandler().postDelayed(new Runnable() {
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

    }

    /**
     * Method used to send order feedback value
     *
     * @param rating
     */
    private void sendQuery(final int rating, final String negativeReasons) {
        try {
            if (lastClientId.equals(Config.getFreshClientId())) {
                Data.getFreshData().setPendingFeedback(0);
            } else if (lastClientId.equals(Config.getMealsClientId())) {
                Data.getMealsData().setPendingFeedback(0);
            } else if (lastClientId.equals(Config.getGroceryClientId())) {
                Data.getGroceryData().setPendingFeedback(0);
            }else if (lastClientId.equals(Config.getMenusClientId()) || lastClientId.equals(Config.getDeliveryCustomerClientId())) {
                getMenusOrDeliveryData().setPendingFeedback(0);
            }else if (lastClientId.equals(Config.getFeedClientId())) {
                Data.getFeedData().setPendingFeedback(0);
            }else if(activity.isDeliveryOpenInBackground()) {
                getMenusOrDeliveryData().setPendingFeedback(0);
            } else {
                activity.finish();
                return;
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
                params.put(Constants.KEY_CLIENT_ID, "" + lastClientId);

                Callback<OrderHistoryResponse> callback = new Callback<OrderHistoryResponse>() {
                    @Override
                    public void success(final OrderHistoryResponse notificationInboxResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                if (rating == 1) {
                                    // for Good rating
                                    if (viewType == RideEndGoodFeedbackViewType.RIDE_END_GIF.getOrdinal()) {
                                        activity.getHandler().postDelayed(new Runnable() {
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
                                    backPressed(false);
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
                if (productType == ProductType.MENUS || productType == ProductType.DELIVERY_CUSTOMER || productType==ProductType.FEED) {
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


    private void backPressed(boolean goodRating) {
        this.goodRating = goodRating;
        if(fragResumed) {
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
        } else {
            backPressCalled = true;
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
            activity.registerForKeyBoardEvent(mKeyBoardStateHandler);
            if (lastClientId.equals(Config.getFreshClientId())) {
                activity.getTopBar().title.setText(getResources().getString(R.string.fatafat));
            } else if (lastClientId.equals(Config.getMealsClientId())) {
                activity.getTopBar().title.setText(getResources().getString(R.string.meals));
            } else if (lastClientId.equals(Config.getGroceryClientId())) {
                activity.getTopBar().title.setText(getResources().getString(R.string.grocery));
            } else if(lastClientId.equals(Config.getMenusClientId()) || lastClientId.equals(Config.getDeliveryCustomerClientId())
                    || lastClientId.equals(Config.getFeedClientId())) {
                setTitleForMenusDeliveryOrFeed();
            } else if (lastClientId.equals(Config.getProsClientId())){
                if(orderStatusResponse != null && orderStatusResponse.getData() != null && orderStatusResponse.getData().size() > 0) {
                    ProsOrderStatusResponse.Datum datum = orderStatusResponse.getData().get(0);
                    Pair<String, String> pair = datum.getProductNameAndJobAmount();
                    activity.getTopBar().title.setText(pair.first);
                }
            }
        }
        else {
            activity.unRegisterKeyBoardListener();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    SendFeedbackQuery sendFeedbackQuery;

    private void sumbitMenusOrDeliveryFeedback(final String reviewDesc, final String comments, final int score) {
        if (lastClientId.equals(Config.getFreshClientId())) {
            Data.getFreshData().setPendingFeedback(0);
        } else if (lastClientId.equals(Config.getMealsClientId())) {
            Data.getMealsData().setPendingFeedback(0);
        } else if (lastClientId.equals(Config.getGroceryClientId())) {
            Data.getGroceryData().setPendingFeedback(0);
        } else if (lastClientId.equals(Config.getFeedClientId())) {
            Data.getFeedData().setPendingFeedback(0);
        } else if (lastClientId.equals(Config.getMenusClientId())
                || lastClientId.equals(Config.getDeliveryCustomerClientId())) {
            getMenusOrDeliveryData().setPendingFeedback(0);
        } else {
            activity.finish();
            return;
        }
        DialogPopup.showLoadingDialog(activity, "");
        if (sendFeedbackQuery == null) {
            sendFeedbackQuery = new SendFeedbackQuery();
        }

        sendFeedbackQuery.sendQuery(Integer.parseInt(orderId), -1, productType, score, Constants.RATING_TYPE_STAR, comments, reviewDesc, activity,
                new SendFeedbackQuery.FeedbackResultListener() {
                    @Override
                    public void onSendFeedbackResult(boolean isSuccess, int rating) {
                        if (isSuccess) {


                            String gaCategory = GACategory.FATAFAT3;
                            if(lastClientId.equals(Config.getDeliveryCustomerClientId())){
                                gaCategory=GACategory.DELIVERY_CUSTOMER;
                            }else if(lastClientId.equals(Config.getMenusClientId())){
                                gaCategory = GACategory.MENUS;
                            }
                            GAUtils.event(gaCategory, GAAction.FEEDBACK , GAAction.SUBMIT_BUTTON + GAAction.CLICKED);
                            if(!TextUtils.isEmpty(comments))  GAUtils.event(gaCategory, GAAction.FEEDBACK , GAAction.COMMENT + GAAction.ADDED );
                            if(!TextUtils.isEmpty(reviewDesc)) GAUtils.event(gaCategory, GAAction.FEEDBACK , GAAction.TAG + GAAction.ADDED );
                            if(score>0) GAUtils.event(gaCategory, GAAction.FEEDBACK , GAAction.RATING+ GAAction.ADDED );;

                            if (rating > 2) {
                                // for Good rating
                                afterGoodRating();
                                if (viewType == RideEndGoodFeedbackViewType.RIDE_END_GIF.getOrdinal()) {
                                    activity.getHandler().postDelayed(new Runnable() {
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
                                backPressed(false);
                            }

                        }
                    }
                }, false);
    }


    boolean fragResumed, backPressCalled, goodRating;
    @Override
    public void onResume() {
        super.onResume();
        fragResumed = true;
        if(backPressCalled){
            backPressed(goodRating);
            backPressCalled = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        fragResumed = false;
    }

    private ApiProsOrderStatus apiProsOrderStatus;
    private ApiProsOrderStatus getApiProsOrderStatus(){
        if(apiProsOrderStatus == null){
            apiProsOrderStatus = new ApiProsOrderStatus(new ApiProsOrderStatus.Callback() {
                @Override
                public void onNoRetry() {
                    activity.onBackPressed();
                }

                @Override
                public void onSuccess(ProsOrderStatusResponse orderStatusResponse) {
                    setProsDataToUI(orderStatusResponse);

                }
            });
        }
        return apiProsOrderStatus;
    }

    private ProsOrderStatusResponse orderStatusResponse;
    private void setProsDataToUI(ProsOrderStatusResponse orderStatusResponse){
        textViewRSCashPaid.setVisibility(View.VISIBLE);
        this.orderStatusResponse = orderStatusResponse;
        if(orderStatusResponse != null && orderStatusResponse.getData() != null && orderStatusResponse.getData().size() > 0) {
            ProsOrderStatusResponse.Datum datum = orderStatusResponse.getData().get(0);
            Pair<String, String> pair = datum.getProductNameAndJobAmount();
            activity.getTopBar().title.setText(pair.first);
            if(datum.getJobStatus() == ProsOrderStatus.ENDED.getOrdinal()
                    || datum.getJobStatus() == ProsOrderStatus.FAILED.getOrdinal()) {
                if (!TextUtils.isEmpty(pair.second)) {
                    textViewRSCashPaidValue.setText(activity.getString(R.string.rupees_value_format, pair.second));
                } else {
                    textViewRSCashPaidValue.setText(activity.getString(R.string.rupees_value_format, "-"));
                }
            }
            textViewRSTotalFare.setText(TextUtils.isEmpty(datum.getFleetName()) ? activity.getString(R.string.service_date) : datum.getFleetName());
            textViewRSData.setText(DateOperations.convertDateTimeUSToInd(datum.getJobPickupDatetime().replace("\\", "")));
        }
    }


    public void apiProsFeedback(final int rating, final String comments) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                DialogPopup.showLoadingDialog(activity, "Loading...");
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_JOB_ID, String.valueOf(orderStatusResponse.getData().get(0).getJobHash()));
                params.put(Constants.KEY_JOB_ID_2, String.valueOf(orderStatusResponse.getData().get(0).getJobId()));
                params.put(Constants.KEY_PRODUCT_TYPE, String.valueOf(ProductType.PROS.getOrdinal()));
                params.put(Constants.KEY_CLIENT_ID, "" + lastClientId);
                params.put(Constants.RATING, String.valueOf(rating));
                params.put(Constants.KEY_CUSTOMER_COMMENT, comments);

                new HomeUtil().putDefaultParams(params);
                RestClient.getProsApiService().taskRating(params, new Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt settleUserDebt, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i("Server response", "response = " + responseStr);
                        try {
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, settleUserDebt.getFlag(), settleUserDebt.getError(), settleUserDebt.getMessage())) {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == settleUserDebt.getFlag()) {
                                    Prefs.with(activity).save(Constants.SP_PROS_LAST_COMPLETE_JOB_ID, 0);
                                    Utils.showToast(activity, activity.getString(R.string.thanks_for_your_valuable_feedback));
                                    if (rating > 2) {
                                        // for Good rating
                                        afterGoodRating();
                                        if (viewType == RideEndGoodFeedbackViewType.RIDE_END_GIF.getOrdinal()) {
                                            activity.getHandler().postDelayed(new Runnable() {
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
                                        backPressed(false);
                                    }
                                } else {
                                    retryDialogProsFeedback(rating, comments, settleUserDebt.getMessage(), DialogErrorType.SERVER_ERROR);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialogProsFeedback(rating, comments, "", DialogErrorType.SERVER_ERROR);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("TAG", "getRecentRidesAPI error=" + error.toString());
                        DialogPopup.dismissLoadingDialog();
                        retryDialogProsFeedback(rating, comments, "", DialogErrorType.CONNECTION_LOST);
                    }
                });
            } else {
                retryDialogProsFeedback(rating, comments, "", DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialogProsFeedback(final int rating, final String comments, String message, DialogErrorType dialogErrorType) {
        if (TextUtils.isEmpty(message)) {
            DialogPopup.dialogNoInternet(activity,
                    dialogErrorType,
                    new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                        @Override
                        public void positiveClick(View view) {
                            apiProsFeedback(rating, comments);
                        }

                        @Override
                        public void neutralClick(View view) {

                        }

                        @Override
                        public void negativeClick(View view) {
                        }
                    });
        } else {
            DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message,
                    activity.getString(R.string.retry), activity.getString(R.string.cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            apiProsFeedback(rating, comments);
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.finish();
                        }
                    }, false, false);
        }
    }


}
