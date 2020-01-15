package com.sabkuchfresh.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import com.bumptech.glide.request.RequestOptions;
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
import product.clicklabs.jugnoo.RideTransactionsActivity;
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
    public static final String FEEDBACK_ORDER = "feedback_data";
    public static final String FEEDBACK_SHOW_INVOICE_TEXT = "show_invoice_text";

    private View rootView;
    private ImageView imageViewThumbsDown, imageViewThumbsUp, imageviewType, imageViewThumbsUpGif, imageViewRideEndWithImage,
            ivOffering;
    private FragmentActivity activity;
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
    private String orderId = "", feedbackOrderItems = "", feedbackCurrencyCode, feedbackCurrency;
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
    private String feedbackClientId = "";
    private int merchantCategoryId;
    private LoginResponse.FeedbackData feedbackData;
    private KeyboardLayoutListener.KeyBoardStateHandler mKeyBoardStateHandler;

    private Button buttonRSSkipFeedback;
    private boolean showInvoiceText;

    private ParentActivityMethods activityCallbacks;
    private com.sabkuchfresh.home.TransactionUtils transactionUtils;

    public static Fragment newInstance(String clientId, final LoginResponse.FeedbackData feedbackData,
                                       final boolean showInvoiceText) {
        Bundle bundle  = new Bundle();
        bundle.putString(FEEDBACK_CLIENT_ID,clientId);
        bundle.putParcelable(FEEDBACK_ORDER, feedbackData);
        bundle.putBoolean(FEEDBACK_SHOW_INVOICE_TEXT, showInvoiceText);
        FeedbackFragment feedbackFragment = new FeedbackFragment();
        feedbackFragment.setArguments(bundle);
        return feedbackFragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityCallbacks = (ParentActivityMethods) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().containsKey(FEEDBACK_CLIENT_ID)){
            feedbackClientId = getArguments().getString(FEEDBACK_CLIENT_ID);
        }else{
            feedbackClientId = Config.getLastOpenedClientId(activity);
        }

        if (getArguments().containsKey(FEEDBACK_ORDER)) {
            feedbackData = getArguments().getParcelable(FEEDBACK_ORDER);
        }

        showInvoiceText = getArguments().getBoolean(FEEDBACK_SHOW_INVOICE_TEXT, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_feedback, container, false);
        activity = getActivity();
        mainLayout = (RelativeLayout) rootView.findViewById(R.id.mainLayout);
        new ASSL(activity, mainLayout, 1134, 720, false);
        try {
            rateApp = Data.userData.getCustomerRateAppFlag();
            rateAppDialogContent = Data.userData.getRateAppDialogContent();
            negativeReasons.clear();

            if (feedbackClientId.equals(Config.getProsClientId())) {
                jobId = Prefs.with(activity).getInt(Constants.SP_PROS_LAST_COMPLETE_JOB_ID, 0);
                productType = ProductType.PROS;
                getApiProsOrderStatus().getOrderData(activity, jobId);
            } else {
                if (feedbackClientId.equals(Config.getFreshClientId())) {
                    initFeedbackVariables(feedbackData);


                    productType = ProductType.FRESH;
                    setTitle(getResources().getString(R.string.delivery_new_name));

                } else if (feedbackClientId.equals(Config.getMealsClientId())) {
                    initFeedbackVariables(feedbackData);

                    productType = ProductType.MEALS;
                    setTitle(getResources().getString(R.string.meals));

                } else if (feedbackClientId.equals(Config.getGroceryClientId())) {
                    initFeedbackVariables(feedbackData);

                    productType = ProductType.GROCERY;
                    setTitle(getResources().getString(R.string.grocery));


                } else if (feedbackClientId.equals(Config.getMenusClientId())) {

                    productType = ProductType.MENUS;
                    if (!TextUtils.isEmpty(Data.getMenusData().getRestaurantName())) {
                        setTitle(Data.getMenusData().getRestaurantName());
                    } else {
                        setTitle(activity.getString(R.string.menus));
                    }

                    initFeedbackVariables(feedbackData);


                } else if (feedbackClientId.equals(Config.getDeliveryCustomerClientId())) {
                    productType = ProductType.DELIVERY_CUSTOMER;
                    if (!TextUtils.isEmpty(Data.getDeliveryCustomerData().getRestaurantName())) {
                        setTitle(Data.getDeliveryCustomerData().getRestaurantName());
                    } else {
                        setTitle(activity.getString(R.string.delivery_new_name));
                    }
                    merchantCategoryId = Data.getDeliveryCustomerData().getMerchantCategoryId();

                    initFeedbackVariables(feedbackData);


                } else if (feedbackClientId.equals(Config.getFeedClientId())) {
                    productType = ProductType.FEED;
                    if (Data.getFeedData() != null && !TextUtils.isEmpty(Data.getFeedData().getFeedName())) {

                        if(Data.getFeedData().getFeedName().equals("Fatafat")){
                            setTitle(activity.getString(R.string.delivery_new_name));
                        }
                        else {
                            setTitle(Data.getFeedData().getFeedName());
                        }
                    } else {
                        setTitle(activity.getString(R.string.delivery_new_name));
                    }

                    initFeedbackVariables(feedbackData);


                } else if (feedbackClientId.equals(Config.getProsClientId())) {
                    jobId = Prefs.with(activity).getInt(Constants.SP_PROS_LAST_COMPLETE_JOB_ID, 0);
                    productType = ProductType.PROS;
                    getApiProsOrderStatus().getOrderData(activity, jobId);
                } else {
                    activity.getSupportFragmentManager().popBackStack();
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }


        if (TextUtils.isEmpty(orderId) && jobId <= 0)
            activity.finish();


//        GAUtils.trackScreenView(activity.getGaCategory()+FEEDBACK);

        setUp();



        return rootView;
    }

    private void setTitle(String strTitle) {
        if(activity instanceof FreshActivity) {
            ((FreshActivity)activity).getTopBar().title.setText(strTitle);
        } else if(activity instanceof RideTransactionsActivity) {
            ((RideTransactionsActivity)activity).textViewTitle.setText(strTitle);
        }
    }


    public <T extends LoginResponse.FeedbackData> void initFeedbackVariables(T feedbackData){
        viewType = feedbackData.getFeedbackViewType();
        dateValue =feedbackData.getFeedbackDeliveryDate();
        feedbackCurrencyCode = feedbackData.getFeedbackCurrencyCode();
        feedbackCurrency = feedbackData.getFeedbackCurrency();
        orderAmount = feedbackData.getAmount();
        orderId = feedbackData.getOrderId();
        endRideGoodFeedbackText = feedbackData.getRideEndGoodFeedbackText();
        negativeReasons.clear();

        if(feedbackData.getPositiveFeedbackReasons()!=null && feedbackData.getPositiveFeedbackReasons().size()>0){
            positiveReasons = new ArrayList<>();
            for(String feedbackReason: feedbackData.getPositiveFeedbackReasons()){
                positiveReasons.add(new FeedbackReason(feedbackReason));
            }
        }if(feedbackData.getNegativeFeedbackReasons()!=null && feedbackData.getNegativeFeedbackReasons().size()>0){
            negativeReasons = new ArrayList<>();
            for(String feedbackReason: feedbackData.getNegativeFeedbackReasons()){
                negativeReasons.add(new FeedbackReason(feedbackReason));
            }
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

        textViewRSCashPaidValue.setText(Utils.formatCurrencyAmount(orderAmount, feedbackCurrencyCode, feedbackCurrency));
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

        buttonRSSkipFeedback = (Button) rootView.findViewById(R.id.buttonRSSkipFeedback);
        buttonRSSkipFeedback.setTypeface(Fonts.mavenRegular(activity));

        // if from order status hide view invoice and skip feedback options
        linearLayoutRSViewInvoice.setVisibility(showInvoiceText ? View.VISIBLE : View.GONE);
        buttonRSSkipFeedback.setVisibility(showInvoiceText ? View.VISIBLE : View.GONE);


        editTextRSFeedback = (EditText) rootView.findViewById(R.id.editTextRSFeedback);
        ratingBarMenuFeedback = (RatingBarMenuFeedback) rootView.findViewById(R.id.rating_bar);

        if (Config.getFreshClientId().equals(feedbackClientId)) {
            imageviewType.setImageResource(R.drawable.ic_grocery_grey_vector);
            ivOffering.setImageResource(R.drawable.ic_grocery_grey_vector);
        } else if (Config.getMenusClientId().equals(feedbackClientId) || Config.getFeedClientId().equals(feedbackClientId)
                || Config.getDeliveryCustomerClientId().equals(feedbackClientId)
                || Config.getProsClientId().equals(feedbackClientId)) {


            if(Config.getProsClientId().equals(feedbackClientId)){
                imageviewType.setImageResource(R.drawable.ic_pros_grey);
                ivOffering.setImageResource(R.drawable.ic_pros_grey);
            } else if(Config.getFeedClientId().equals(feedbackClientId)){
                imageviewType.setImageResource(R.drawable.ic_fatafat_grey);
                ivOffering.setImageResource(R.drawable.ic_fatafat_grey);
            } else if(Config.getDeliveryCustomerClientId().equals(feedbackClientId) && merchantCategoryId !=Constants.CATEGORY_ID_RESTAURANTS){
                imageviewType.setImageResource(R.drawable.ic_fatafat_grey);
                ivOffering.setImageResource(R.drawable.ic_fatafat_grey);
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
                        activityCallbacks.getHandler().postDelayed(new Runnable() {
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


        } else {
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
                                GAUtils.event(getGaCategory(), FEEDBACK, ISSUE+SELECTED+name);
                            }
                        }
                    });
            gridViewRSFeedbackReasons.setAdapter(feedbackReasonsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        buttonRSSkipFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                skipApiHit();
            }
        });

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
                        GAUtils.event(getGaCategory(), FEEDBACK, COMMENT+ADDED);
                    }
                }

                if (productType != ProductType.MENUS && productType != ProductType.DELIVERY_CUSTOMER && productType!=ProductType.FEED) {
                    comments = comments+((TextUtils.isEmpty(comments) || TextUtils.isEmpty(reviewDescription))?"":", ")+reviewDescription;
                }

                // api call
                if(productType == ProductType.PROS){
                    apiProsFeedback((int) ratingBarMenuFeedback.getScore(), comments);
                } else if (productType != ProductType.MENUS && productType != ProductType.DELIVERY_CUSTOMER && productType!=ProductType.FEED) {
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
                    getTransactionUtils().addProsOrderStatusFragment(activity, activityCallbacks.getFragmentContainer(), jobId, productType.getOrdinal());
                } else {
                    new TransactionUtils().openOrderStatusFragment(activity,
                            activityCallbacks.getFragmentContainer(), Integer.parseInt(orderId), productType.getOrdinal(), 0,
                            true, true);
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
                        activityCallbacks.getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollViewRideSummary.smoothScrollTo(0, (int) buttonRSSubmitFeedback.getY());
                            }
                        }, 300);
                        scrollViewRideSummary.smoothScrollTo(0, (int) buttonRSSubmitFeedback.getY());
                    } else {
                        sendQuery(0, "");
                    }

                    GAUtils.event(getGaCategory(), FEEDBACK, THUMB_DOWN+CLICKED);

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
                GAUtils.event(getGaCategory(), FEEDBACK, THUMB_UP+CLICKED);
            }
        });
        if (activity instanceof FreshActivity) {
            ((FreshActivity) activity).fragmentUISetup(this);
        }

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
        activityCallbacks.registerForKeyBoardEvent(mKeyBoardStateHandler);

    }

    private String getGaCategory() {
        if(activity instanceof FreshActivity) {
            return ((FreshActivity)activity).getGaCategory();
        } else if(activity instanceof RideTransactionsActivity){
            return ((RideTransactionsActivity)activity).getGaCategory();
        } else {
            return "";
        }
    }

    private void skipApiHit() {
        DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        params.put(Constants.INTERATED, "1");
        params.put(Constants.ORDER_ID, "" + orderId);
        params.put(Constants.SKIP, "1");

        Callback<OrderHistoryResponse> callback = new Callback<OrderHistoryResponse>() {

            @Override
            public void success(OrderHistoryResponse orderHistoryResponse, Response response) {
                DialogPopup.dismissLoadingDialog();
                try {
                    if (orderHistoryResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                        backPressed(true);
                    } else {
                        DialogPopup.alertPopup(activity, "", orderHistoryResponse.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                retryDialogSkip();
            }
        };

        if(productType==ProductType.FEED){
            RestClient.getFatafatApiService().orderFeedback(params,callback);
        } else if (productType == ProductType.MENUS || productType == ProductType.DELIVERY_CUSTOMER) {
            RestClient.getMenusApiService().orderFeedback(params, callback);
        } else {
            RestClient.getFreshApiService().orderFeedback(params, callback);
        }
    }

    private void retryDialogSkip() {
        DialogPopup.dialogNoInternet(activity, DialogErrorType.NO_NET,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        skipApiHit();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {

                    }
                });
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
        RequestOptions options = new RequestOptions().placeholder(R.drawable.great_place_holder);
        Glide.with(activity)
                .load(R.drawable.android_thumbs_up)
                .apply(options)
                //.fitCenter()
                .into(imageViewThumbsUpGif);
        activityCallbacks.getHandler().postDelayed(new Runnable() {
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
            feedbackData.setPendingFeedback(0);
            if (MyApplication.getInstance().isOnline()) {
                //DialogPopup.showLoadingDialog(activity, "loading...");

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.ORDER_ID, "" + orderId);
                params.put(Constants.RATING, "" + rating);
                params.put(Constants.RATING_TYPE, "0");
                params.put(Constants.INTERATED, "1");
                params.put(Constants.COMMENT, negativeReasons);
                params.put(Constants.KEY_CLIENT_ID, "" + feedbackClientId);

                Callback<OrderHistoryResponse> callback = new Callback<OrderHistoryResponse>() {
                    @Override
                    public void success(final OrderHistoryResponse notificationInboxResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                if (rating == 1) {
                                    // for Good rating
                                    if (viewType == RideEndGoodFeedbackViewType.RIDE_END_GIF.getOrdinal()) {
                                        activityCallbacks.getHandler().postDelayed(new Runnable() {
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
                        activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
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
            if (activity instanceof FreshActivity) {
                ((FreshActivity) activity).setRefreshCart(true);
            }
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
            if (getActivity() instanceof FreshActivity) {
                ((FreshActivity)activity).fragmentUISetup(this);
            }

            activityCallbacks.registerForKeyBoardEvent(mKeyBoardStateHandler);
            if (feedbackClientId.equals(Config.getFreshClientId())) {
                setTitle(getResources().getString(R.string.delivery_new_name));
            } else if (feedbackClientId.equals(Config.getMealsClientId())) {
                setTitle(getResources().getString(R.string.meals));
            } else if (feedbackClientId.equals(Config.getGroceryClientId())) {
                setTitle(getResources().getString(R.string.grocery));
            } else if(feedbackClientId.equals(Config.getMenusClientId()) ) {
                if(Data.getMenusData()!=null && !TextUtils.isEmpty(Data.getMenusData().getRestaurantName())){
                    setTitle(Data.getMenusData().getRestaurantName());
                } else {
                    setTitle(activity.getString(R.string.menus));
                }
            } else if(feedbackClientId.equals(Config.getDeliveryCustomerClientId()) ) {
                if(Data.getDeliveryCustomerData()!=null && !TextUtils.isEmpty(Data.getDeliveryCustomerData().getRestaurantName())){
                    setTitle(Data.getDeliveryCustomerData().getRestaurantName());
                } else {
                    setTitle(activity.getString(R.string.delivery_new_name));
                }
            } else if(feedbackClientId.equals(Config.getFeedClientId()) ) {
                setTitle(Data.getFeedData()!=null && !TextUtils.isEmpty(Data.getFeedData().getFeedName())?Data.getFeedData().getFeedName():activity.getString(R.string.delivery_new_name));

            } else if (feedbackClientId.equals(Config.getProsClientId())){
                if(orderStatusResponse != null && orderStatusResponse.getData() != null && orderStatusResponse.getData().size() > 0) {
                    ProsOrderStatusResponse.Datum datum = orderStatusResponse.getData().get(0);
                    Pair<String, String> pair = datum.getProductNameAndJobAmount();
                    setTitle(pair.first);
                }
            }
        }
        else {
            activityCallbacks.unRegisterKeyBoardListener();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    SendFeedbackQuery sendFeedbackQuery;

    private void sumbitMenusOrDeliveryFeedback(final String reviewDesc, final String comments, final int score) {
        feedbackData.setPendingFeedback(0);
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
                            if(feedbackClientId.equals(Config.getDeliveryCustomerClientId())){
                                gaCategory=GACategory.DELIVERY_CUSTOMER;
                            }else if(feedbackClientId.equals(Config.getMenusClientId())){
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
                                    activityCallbacks.getHandler().postDelayed(new Runnable() {
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

                    @Override
                    public boolean onRatingFailed(String message, int flag) {
                        // order already reviewed
                        if (flag == 787) {
                            DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    backPressed(false);
                                }
                            });
                            return true;
                        }
                        return false;
                    }
                }, feedbackClientId);
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
            activityCallbacks.setTitle(pair.first);
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
                DialogPopup.showLoadingDialog(activity, getString(R.string.loading));
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_JOB_ID, String.valueOf(orderStatusResponse.getData().get(0).getJobHash()));
                params.put(Constants.KEY_JOB_ID_2, String.valueOf(orderStatusResponse.getData().get(0).getJobId()));
                params.put(Constants.KEY_PRODUCT_TYPE, String.valueOf(ProductType.PROS.getOrdinal()));
                params.put(Constants.KEY_CLIENT_ID, "" + feedbackClientId);
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
                                            activityCallbacks.getHandler().postDelayed(new Runnable() {
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

    private com.sabkuchfresh.home.TransactionUtils getTransactionUtils() {
        if (transactionUtils == null) {
            transactionUtils = new com.sabkuchfresh.home.TransactionUtils();
        }
        return transactionUtils;
    }

    public interface ParentActivityMethods {

        void setTitle(String text);

        Handler getHandler();

        View getFragmentContainer();

        void registerForKeyBoardEvent(final KeyboardLayoutListener.KeyBoardStateHandler keyboardListener);

        void unRegisterKeyBoardListener();
    }

}
