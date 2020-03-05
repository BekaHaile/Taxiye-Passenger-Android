package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hippo.ChatByUniqueIdAttributes;
import com.hippo.HippoConfig;
import com.sabkuchfresh.adapters.OrderItemsAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.commoncalls.ApiCancelOrder;
import com.sabkuchfresh.dialogs.ReviewImagePagerDialog;
import com.sabkuchfresh.fragments.OrderCancelReasonsFragment;
import com.sabkuchfresh.fragments.TrackOrderFragment;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.OrderStatus;
import com.sabkuchfresh.retrofit.model.menus.Charges;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.sabkuchfresh.utils.TextViewStrikeThrough;
import com.sabkuchfresh.widgets.LockableBottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.adapters.EndRideDiscountsAdapter;
import product.clicklabs.jugnoo.adapters.ImageWithTextAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.DiscountType;
import product.clicklabs.jugnoo.datastructure.MenuInfoTags;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.BillSummaryModel;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.support.SupportMailActivity;
import product.clicklabs.jugnoo.support.TransactionUtils;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 27/10/16.
 */

public class OrderStatusFragment extends Fragment implements GAAction, View.OnClickListener {

    private static final int REQ_CODE_REORDER = 5011;
    private boolean hideRateOrder, hideRepeatOrder;
    private CoordinatorLayout relative;
    private RelativeLayout rlOrderStatus;
    private TextView tvOrderStatus, tvOrderStatusVal, tvOrderTime, tvOrderTimeVal, tvDeliveryTime, tvDeliveryTimeVal, tvDeliveryTo,
            tvDelveryPlace, tvDeliveryToVal,
            tvTotalAmountVal, tvAmountPayableVal;
    private ImageView ivDeliveryPlace, ivOrderCompleted, imageViewRestaurant, imageViewCallRestaurant;
    private Button bNeedHelp, buttonCancelOrder, reorderBtn, feedbackBtn, cancelfeedbackBtn, btRateOrder;
    private int orderId, productType;
    private NonScrollListView listViewOrder;
    private OrderItemsAdapter orderItemsAdapter;
    private LinearLayout llFinalAmount, llDeliveryPlace, orderComplete, orderCancel;
    private ArrayList<HistoryResponse.OrderItem> subItemsOrders = new ArrayList<>();
    private HistoryResponse.Datum datum1;
    private FragmentActivity activity;
    private CardView cvOrderStatus, cvPaymentMethod;
    public TextView tvStatus0, tvStatus1, tvStatus2, tvStatus3;
    public ImageView ivStatus0, ivStatus1, ivStatus2, ivStatus3;
    public View lineStatus1, lineStatus2, lineStatus3;
    private View rootView, vDividerPayment;
    private ImageView ivTopShadow;
    private LinearLayout llExtraCharges, llMain;

    private TextView tvItemsQuantityVal;
    private TextViewStrikeThrough tvTotalAmountValOld;
    private TextView tvTotalAmountMessage;
    private TextView tvPaymentMethodVal;
    private RelativeLayout rlWalletDeducted;
    private LinearLayout llPaymentSummary;

    private NestedScrollView scrollView;
    private RelativeLayout rlContainer;
    private LockableBottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout rlOrderStatusMapPeek;
    private LinearLayout llShadowPeek;
    private int llShadowPeekHeight, openLiveTracking;
    private JSONObject responseOrderDataApi;
    private boolean isFeedOrder;
    private NonScrollListView listViewStripeTxns;
    private EndRideDiscountsAdapter stripeTxnsAdapter;
    private HomeUtil homeUtil = new HomeUtil();
    private String currencyCode, currency;
    private String vehicleImage;

    private com.sabkuchfresh.home.TransactionUtils transactionUtils;

    @BindView(R.id.tv2r)
    TextView tv2r;
    @BindView(R.id.tv3r)
    TextView tv3r;
    @BindView(R.id.ivDeliveryPlaceFeed)
    ImageView ivDeliveryPlaceFeed;
    @BindView(R.id.tvDeliveryPlace)
    TextView tvDeliveryPlace;
    @BindView(R.id.llDeliveryPlaceFeed)
    LinearLayout llDeliveryPlaceFeed;
    @BindView(R.id.tvDeliveryToValFeed)
    TextView tvDeliveryToValFeed;
    @BindView(R.id.tvAmountValue)
    TextView tvAmountValue;
    @BindView(R.id.ivPaidVia)
    ImageView ivPaidVia;
    @BindView(R.id.tvPaidViaValue)
    TextView tvPaidViaValue;
    @BindView(R.id.bNeedHelpFeed)
    Button bNeedHelpFeed;
    @BindView(R.id.bCancelOrder)
    Button bCancelOrder;
    @BindView(R.id.tv1r)
    TextView tv1r;
    @BindView(R.id.tv1l)
    TextView tv1l;
    @BindView(R.id.tv2l)
    TextView tv2l;
    @BindView(R.id.tv3l)
    TextView tv3l;
    @BindView(R.id.tv4l)
    TextView tv4l;
    @BindView(R.id.ivFromPlace)
    ImageView ivFromPlace;
    @BindView(R.id.tvFromPlace)
    TextView tvFromPlace;
    @BindView(R.id.llFromPlace)
    LinearLayout llFromPlace;
    @BindView(R.id.tvFromToVal)
    TextView tvFromToVal;
    @BindView(R.id.llFromAddress)
    LinearLayout llFromAddress;
    @BindView(R.id.tv5l)
    TextView tv5l;
    @BindView(R.id.tvTaskDetails)
    TextView tvTaskDetails;
    @BindView(R.id.llAmount)
    LinearLayout llAmount;
    @BindView(R.id.llPaidVia)
    LinearLayout llPaidVia;
    @BindView(R.id.rlOrderStatusFeed)
     LinearLayout rlOrderStatusFeed;
    @BindView(R.id.divider_below_rlOrderStatusFeed)
     View dividerBelowRlOrderStatusFeed;
    @BindView(R.id.feed_fragment_shadow_top)
     View feedFragmentShadowTop;
    @BindView(R.id.cardFeedBillSummary)
     CardView cardFeedBillSummary;
    @BindView(R.id.llFeedExtraCharges)
     LinearLayout llFeedExtraCharges;
    @BindView(R.id.btRepeatOrderFeed)
    Button btRepeatOrderFeed;
    @BindView(R.id.btRateOrder) Button btRateOrderFeed;
    @BindView(R.id.rvFeedPickupImages)
	RecyclerView rvFeedPickupImages;
    @BindView(R.id.rvFeedDeliveriesImages) RecyclerView rvFeedDeliveriesImages;
    @BindView(R.id.cardDeliveriesFeedPhotos) CardView cardDeliveriesFeedPhotos;
    @BindView(R.id.cardPickupFeedPhotos) CardView cardPickupFeedPhotos;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order_status, container, false);
        relative = (CoordinatorLayout) rootView.findViewById(R.id.relative);

        activity = getActivity();


        if (activity instanceof FreshActivity) {
            ((FreshActivity) activity).fragmentUISetup(this);
            GAUtils.trackScreenView(((FreshActivity) activity).getGaCategory() + ORDER_STATUS);
        }

        try {
            new ASSL(activity, relative, 1134, 720, false);
        } catch (Exception e) {
        }


        try {
            orderId = getArguments().getInt(Constants.KEY_ORDER_ID, 0);
            productType = getArguments().getInt(Constants.KEY_PRODUCT_TYPE, ProductType.MEALS.getOrdinal());
            openLiveTracking = getArguments().getInt(Constants.KEY_OPEN_LIVE_TRACKING, 0);
            hideRateOrder = getArguments().getBoolean(Constants.KEY_SHOW_RATE_ORDER_BUTTON, false);
            setFragTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        scrollView = (NestedScrollView) rootView.findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);
        bottomSheetBehavior = (LockableBottomSheetBehavior)BottomSheetBehavior.from(scrollView);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setLocked(true);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    TrackOrderFragment fragment = getTrackOrderFragment();
                    if(fragment != null){
                        fragment.setPaddingZero();
                    }
                } else if(newState == BottomSheetBehavior.STATE_EXPANDED) {
                    TrackOrderFragment fragment = getTrackOrderFragment();
                    if(fragment != null){
                        fragment.setPaddingSome(false);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        rlOrderStatusMapPeek = (RelativeLayout) rootView.findViewById(R.id.rlOrderStatusMapPeek);
        rlOrderStatusMapPeek.setBackgroundColor(ContextCompat.getColor(activity, R.color.offer_popup_item_color));
        llShadowPeek = (LinearLayout) rootView.findViewById(R.id.llShadowPeek);
        llShadowPeek.setVisibility(View.GONE);
        llShadowPeekHeight = activity.getResources().getDimensionPixelSize(R.dimen.dp_130);
        rlContainer = (RelativeLayout) rootView.findViewById(R.id.rlContainer);
        rlContainer.setVisibility(View.GONE);
        llShadowPeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });


        llMain = (LinearLayout) rootView.findViewById(R.id.llMain);
        llMain.setVisibility(View.GONE);
        tvOrderStatus = (TextView) rootView.findViewById(R.id.tvOrderStatusLabel);
        tvOrderStatus.setTypeface(Fonts.mavenMedium(activity));
        tvOrderStatusVal = (TextView) rootView.findViewById(R.id.tvOrderStatusVal);
        tvOrderStatusVal.setTypeface(Fonts.mavenMedium(activity));
        tvOrderTime = (TextView) rootView.findViewById(R.id.tvOrderTime);
        tvOrderTime.setTypeface(Fonts.mavenMedium(activity));
        tvOrderTimeVal = (TextView) rootView.findViewById(R.id.tvOrderTimeVal);
        tvOrderTimeVal.setTypeface(Fonts.mavenMedium(activity));
        tvDeliveryTime = (TextView) rootView.findViewById(R.id.tvDeliveryTime);
        tvDeliveryTime.setTypeface(Fonts.mavenMedium(activity));
        tvDeliveryTimeVal = (TextView) rootView.findViewById(R.id.tvDeliveryTimeVal);
        tvDeliveryTimeVal.setTypeface(Fonts.mavenMedium(activity));
        tvDeliveryTo = (TextView) rootView.findViewById(R.id.tvDeliveryTo);
        tvDeliveryTo.setTypeface(Fonts.mavenMedium(activity));
        tvDelveryPlace = (TextView) rootView.findViewById(R.id.tvDelveryPlace);
        tvDelveryPlace.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        tvDeliveryToVal = (TextView) rootView.findViewById(R.id.tvDeliveryToVal);
        tvDeliveryToVal.setTypeface(Fonts.mavenMedium(activity));


        bNeedHelp = (Button) rootView.findViewById(R.id.bNeedHelp);
        llDeliveryPlace = (LinearLayout) rootView.findViewById(R.id.llDeliveryPlace);
        ivDeliveryPlace = (ImageView) rootView.findViewById(R.id.ivDeliveryPlace);
        orderComplete = (LinearLayout) rootView.findViewById(R.id.order_complete);
        orderCancel = (LinearLayout) rootView.findViewById(R.id.order_cancel);
        ivOrderCompleted = (ImageView) rootView.findViewById(R.id.ivOrderCompleted);
        rlOrderStatus = (RelativeLayout) rootView.findViewById(R.id.rlOrderStatus);
        imageViewRestaurant = (ImageView) rootView.findViewById(R.id.imageViewRestaurant);
        imageViewRestaurant.setVisibility(View.GONE);
        imageViewCallRestaurant = (ImageView) rootView.findViewById(R.id.imageViewCallRestaurant);
        imageViewCallRestaurant.setVisibility(View.GONE);
        llExtraCharges = (LinearLayout) rootView.findViewById(R.id.llExtraCharges);
        llExtraCharges.setVisibility(View.GONE);

        // Order Status
        cvOrderStatus = (CardView) rootView.findViewById(R.id.cvOrderStatus);
        tvStatus0 = (TextView) rootView.findViewById(R.id.tvStatus0);
        tvStatus0.setTypeface(Fonts.mavenRegular(activity));
        tvStatus1 = (TextView) rootView.findViewById(R.id.tvStatus1);
        tvStatus1.setTypeface(Fonts.mavenRegular(activity));
        tvStatus2 = (TextView) rootView.findViewById(R.id.tvStatus2);
        tvStatus2.setTypeface(Fonts.mavenRegular(activity));
        tvStatus3 = (TextView) rootView.findViewById(R.id.tvStatus3);
        tvStatus3.setTypeface(Fonts.mavenRegular(activity));
        ivStatus0 = (ImageView) rootView.findViewById(R.id.ivStatus0);
        ivStatus1 = (ImageView) rootView.findViewById(R.id.ivStatus1);
        ivStatus2 = (ImageView) rootView.findViewById(R.id.ivStatus2);
        ivStatus3 = (ImageView) rootView.findViewById(R.id.ivStatus3);
        lineStatus1 = (View) rootView.findViewById(R.id.lineStatus1);
        lineStatus2 = (View) rootView.findViewById(R.id.lineStatus2);
        lineStatus3 = (View) rootView.findViewById(R.id.lineStatus3);
        ivTopShadow = (ImageView) rootView.findViewById(R.id.ivTopShadow);
        cvOrderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (activity instanceof FreshActivity) {
            ivTopShadow.setVisibility(View.VISIBLE);
        }


        buttonCancelOrder = (Button) rootView.findViewById(R.id.buttonCancelOrder);
        buttonCancelOrder.setTypeface(Fonts.mavenRegular(activity));
        reorderBtn = (Button) rootView.findViewById(R.id.reorderBtn);
        reorderBtn.setTypeface(Fonts.mavenRegular(activity));
        feedbackBtn = (Button) rootView.findViewById(R.id.feedbackBtn);
        feedbackBtn.setTypeface(Fonts.mavenRegular(activity));
        btRateOrder = (Button) rootView.findViewById(R.id.btRateOrder);
        btRateOrder.setTypeface(Fonts.mavenRegular(activity));
        cancelfeedbackBtn = (Button) rootView.findViewById(R.id.cancelfeedbackBtn);
        cancelfeedbackBtn.setTypeface(Fonts.mavenRegular(activity));

        tvItemsQuantityVal = (TextView) rootView.findViewById(R.id.tvItemsQuantityVal);
        tvItemsQuantityVal.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        tvTotalAmountValOld = (TextViewStrikeThrough) rootView.findViewById(R.id.tvTotalAmountValOld);
        tvTotalAmountMessage = (TextView) rootView.findViewById(R.id.tvTotalAmountMessage);
        ((TextView) rootView.findViewById(R.id.tvItemSummary)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        ((TextView) rootView.findViewById(R.id.tvBillSummary)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        tvTotalAmountVal = (TextView) rootView.findViewById(R.id.tvTotalAmountVal);





        buttonCancelOrder.setOnClickListener(this);
        btRateOrder.setOnClickListener(this);
        reorderBtn.setOnClickListener(this);
        feedbackBtn.setOnClickListener(this);
        cancelfeedbackBtn.setOnClickListener(this);


        listViewOrder = (NonScrollListView) rootView.findViewById(R.id.listViewCart);
        orderItemsAdapter = new OrderItemsAdapter(activity, subItemsOrders, currencyCode, currency);
        listViewOrder.setAdapter(orderItemsAdapter);




        if(productType==ProductType.FEED.getOrdinal()){
            ButterKnife.bind(this,  rootView.findViewById(R.id.layout_feed_order));
            cvPaymentMethod = (CardView) rootView.findViewById(R.id.cvPaymentMethodFeed);
            initPaymentMethodViews();
            bNeedHelpFeed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity instanceof RideTransactionsActivity) {
                        if (datum1 != null && !TextUtils.isEmpty(datum1.getFuguChannelId())) {
                            ChatByUniqueIdAttributes chatAttr = new ChatByUniqueIdAttributes.Builder()
                                    .setTransactionId(datum1.getFuguChannelId())
                                    .setUserUniqueKey(String.valueOf(Data.getFuguUserData().getUserId()))
                                    .setChannelName(datum1.getFuguChannelName())
                                    .setTags(datum1.getFuguTags())
                                    .build();
                            HippoConfig.getInstance().openChatByUniqueId(chatAttr);
                        } else if(Data.isMenuTagEnabled(MenuInfoTags.EMAIL_SUPPORT)){
                            activity.startActivity(new Intent(activity, SupportMailActivity.class));
                        }
                    }else if(activity instanceof FreshActivity){
                        activity.onBackPressed();
                    }
                }
            });
            bCancelOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelOrder(true);
                }
            });
            feedFragmentShadowTop.setVisibility(View.GONE);
            rootView.findViewById(R.id.layout_menus_order).setVisibility(View.GONE);
            rootView.findViewById(R.id.layout_feed_order).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.tvFeedBillSummary)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
            tv1l.setText(R.string.status_colon);
            tv2l.setText(R.string.order_time_colon);
            tv3l.setText(R.string.delivery_time_colon);
            llFromAddress.setVisibility(View.VISIBLE);
            tv5l.setText(R.string.to_colon);
            tvTaskDetails.setVisibility(View.VISIBLE);
            llAmount.setVisibility(View.GONE);
            llPaidVia.setVisibility(View.GONE);
            bNeedHelpFeed.setText(activity instanceof RideTransactionsActivity?R.string.chat:R.string.ok);
            getFeedOrderData(activity);
            isFeedOrder = true;
        }else{
            cvPaymentMethod = (CardView) rootView.findViewById(R.id.cvPaymentMethod);
            initPaymentMethodViews();

            rootView.findViewById(R.id.layout_menus_order).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.layout_feed_order).setVisibility(View.GONE);
            getOrderData(activity);

        }


        bNeedHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needHelpClick();
            }
        });

        imageViewCallRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(datum1.getRestaurantPhoneNo())) {
                    Utils.openCallIntent(activity, datum1.getRestaurantPhoneNo());
                }
            }
        });
        LocalBroadcastManager.getInstance(activity).registerReceiver(orderUpdateBroadcast, new IntentFilter(Constants.INTENT_ACTION_ORDER_STATUS_UPDATE));

        return relative;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initPaymentMethodViews() {
        llPaymentSummary = (LinearLayout) cvPaymentMethod.findViewById(R.id.llPaymentSummary);
        ((TextView) cvPaymentMethod.findViewById(R.id.tvPaymentSummary)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
        rlWalletDeducted = (RelativeLayout) cvPaymentMethod.findViewById(R.id.rlWalletDeducted);
        tvPaymentMethodVal = (TextView) cvPaymentMethod.findViewById(R.id.tvPaymentMethodVal);
        tvAmountPayableVal = (TextView) cvPaymentMethod.findViewById(R.id.tvAmountPayableVal);
        vDividerPayment = cvPaymentMethod.findViewById(R.id.vDividerPayment);
        llFinalAmount = (LinearLayout) cvPaymentMethod.findViewById(R.id.llFinalAmount);
        listViewStripeTxns = (NonScrollListView) cvPaymentMethod.findViewById(R.id.listViewStripeTxns);
        stripeTxnsAdapter = new EndRideDiscountsAdapter(activity, false);
        listViewStripeTxns.setAdapter(stripeTxnsAdapter);
        llFinalAmount.setVisibility(View.GONE);
    }

    public void getFeedOrderData(final Activity activity) {
        try {
            if (MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(activity, getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_ORDER_ID, "" + orderId);
                params.put(Constants.KEY_PRODUCT_TYPE, "" + productType);
                params.put(Constants.KEY_CLIENT_ID,  getClientIdByProductType(productType));
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
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    datum1 = historyResponse.getData().get(0);
                                    currencyCode = datum1.getCurrencyCode();
                                    currency = datum1.getCurrency();
                                    setOrderItemAdapter();
                                    llMain.setVisibility(View.VISIBLE);

                                    if (historyResponse.getRecentOrdersPossibleFatafatStatus().size() > 0) {
                                        cvOrderStatus.setVisibility(View.VISIBLE);
                                        rlOrderStatusFeed.setVisibility(View.GONE);
                                        dividerBelowRlOrderStatusFeed.setVisibility(View.GONE);


                                        showPossibleStatus(historyResponse.getRecentOrdersPossibleFatafatStatus(), datum1.getOrderStatusIndex(),true);

                                        TrackOrderFragment fragment = getTrackOrderFragment();
//                                        if (fragment != null) {
//                                            // for multiple deliveries, update the currently delivery point being delivered
//                                            fragment.updateOrderDetails(datum1.getDeliveries(), datum1.getLiveTracking().isPickupCompleted());
//                                        }

                                        if (datum1.getCancellable() == 1) {
                                            bCancelOrder.setVisibility(View.VISIBLE);
                                        } else {
                                            bCancelOrder.setVisibility(View.GONE);
                                        }

                                    } else {
                                        cvOrderStatus.setVisibility(View.GONE);
                                        rlOrderStatusFeed.setVisibility(View.VISIBLE);
                                        dividerBelowRlOrderStatusFeed.setVisibility(View.VISIBLE);

                                        bCancelOrder.setVisibility(View.GONE);
                                    }

                                    setFeedOrderData(datum1, activity);
                                    if(datum1.getIsPaid()==1){
                                        setPaymentModes(datum1);
                                    } else {
                                        tvAmountPayableVal.setText(com.sabkuchfresh.utils.Utils.formatCurrencyAmount(0, currencyCode, currency));
                                    }
                                    openTrackOrderFragment();
                                } else {
                                    retryDialogCancelOrderOrOrderStatusFeed(message, DialogErrorType.SERVER_ERROR);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialogCancelOrderOrOrderStatusFeed("", DialogErrorType.SERVER_ERROR);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("TAG", "getRecentRidesAPI error=" + error.toString());
                        DialogPopup.dismissLoadingDialog();
                        retryDialogCancelOrderOrOrderStatusFeed("", DialogErrorType.CONNECTION_LOST);
                    }
                };
                new HomeUtil().putDefaultParams(params);
                RestClient.getFatafatApiService().getCustomOrderHistory(params, callback);
            } else {
                retryDialogCancelOrderOrOrderStatusFeed("", DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelOrder(boolean isFromFatafat) {
        if (datum1.getCancellable() == 1) {
            int storeId = datum1.getStoreId() == null ? 0 : datum1.getStoreId();
            if (datum1.getShowCancellationReasons() == 1) {
                int containerId = -1;
                if (activity instanceof FreshActivity) {
                    containerId = ((FreshActivity) activity).getRelativeLayoutContainer().getId();
                } else if (activity instanceof RideTransactionsActivity) {
                    containerId = ((RideTransactionsActivity) activity).getContainer().getId();
                } else if (activity instanceof SupportActivity) {
                    containerId = ((SupportActivity) activity).getContainer().getId();
                }
                if (containerId > -1) {
                    activity.getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .add(containerId, OrderCancelReasonsFragment.newInstance(datum1.getOrderId(),
                                    productType, storeId, getClientIdByProductType(productType)),
                                    OrderCancelReasonsFragment.class.getName())
                            .addToBackStack(OrderCancelReasonsFragment.class.getName())
                            .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                                    .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                            .commitAllowingStateLoss();
                }
            } else if (!isFromFatafat) {
                DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", getString(R.string.are_you_sure_cancel_order), getResources().getString(R.string.ok),
                        getResources().getString(R.string.cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelOrderApi();
                                if (activity instanceof FreshActivity) {
                                    GAUtils.event(((FreshActivity) activity).getGaCategory(), ORDER_STATUS, ORDER + CANCELLED);
                                }
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }, false, false);
            }
        } else if (!isFromFatafat) {
            feedbackBtn.performClick();
        }
    }

    private void setOrderItemAdapter() {
        if(orderItemsAdapter == null) {
            orderItemsAdapter = new OrderItemsAdapter(activity, subItemsOrders, currencyCode, currency);
            listViewOrder.setAdapter(orderItemsAdapter);
        }
    }

    private void setFeedOrderData(HistoryResponse.Datum datum, Activity activity) {
        tv1r.setText(datum.getOrderStatus());
        btRateOrder.setVisibility(datum.isPendingFeedback() ? View.VISIBLE : View.GONE);

        try{
            tv1r.setTextColor(Color.parseColor(datum.getOrderStatusColor()));
        } catch (Exception e){
            tv1r.setTextColor(ContextCompat.getColor(activity, R.color.green_status));
        }
        tv2r.setText(DateOperations.convertDateViaFormat(DateOperations.utcToLocalWithTZFallback(datum.getCreatedAt())));
        if(!TextUtils.isEmpty(datum.getDeliveryTime())){
            tv3r.setText(DateOperations.convertDateViaFormat(DateOperations.utcToLocalWithTZFallback(datum.getDeliveryTime())));
        } else {
            tv3r.setText(R.string.asap);
        }

        SearchResult searchResultFrom = homeUtil.getNearBySavedAddress(activity,
                new LatLng(datum.getFromLatitude(), datum.getFromLongitude()),
				false);
        if (searchResultFrom != null && !TextUtils.isEmpty(searchResultFrom.getName()) && datum.getCategory()==0) {
            llFromPlace.setVisibility(View.VISIBLE);
            ivFromPlace.setImageResource(homeUtil.getSavedLocationIcon(searchResultFrom.getName()));
            tvFromPlace.setText(searchResultFrom.getName());
            tvFromToVal.setText(searchResultFrom.getAddress());
        } else {
            llFromPlace.setVisibility(View.GONE);
            tvFromToVal.setText(datum.getFromAddress());
        }

        SearchResult searchResultTo = homeUtil.getNearBySavedAddress(activity,
                new LatLng(datum.getToLatitude(), datum.getToLongitude()),
				false);
        if (searchResultTo != null && !TextUtils.isEmpty(searchResultTo.getName())) {
            llDeliveryPlaceFeed.setVisibility(View.VISIBLE);
            ivDeliveryPlaceFeed.setImageResource(homeUtil.getSavedLocationIcon(searchResultTo.getName()));
            tvDeliveryPlace.setText(searchResultTo.getName());
            tvDeliveryToValFeed.setText(searchResultTo.getAddress());
        } else {
            llDeliveryPlaceFeed.setVisibility(View.GONE);
            tvDeliveryToValFeed.setText(datum.getToAddress());
        }
        tvTaskDetails.setText(datum.getDetails());


        // set delivery charges if available
        if (datum.getBillSummary() != null && datum.getBillSummary().size() != 0) {

            cardFeedBillSummary.setVisibility(View.VISIBLE);
            llFeedExtraCharges.removeAllViews();

            LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int i=0;
            for (BillSummaryModel bill : datum.getBillSummary()) {

                View view = layoutInflater.inflate(R.layout.layout_order_amount_new, null);
                RelativeLayout relative = (RelativeLayout) view.findViewById(R.id.relative);
                TextView tvDelCharges = (TextView) view.findViewById(R.id.tvDelCharges);
                TextView tvDelChargesVal = (TextView) view.findViewById(R.id.tvDelChargesVal);
                tvDelCharges.setText(bill.getKey());
                tvDelCharges.setTextColor(Color.parseColor(bill.getFormat().getColor()));
                tvDelChargesVal.setText(bill.getValue());
                tvDelChargesVal.setTextColor(Color.parseColor(bill.getFormat().getColor()));
                if(bill.getFormat().getIsBold()==1){
                    tvDelChargesVal.setTypeface(tvDelChargesVal.getTypeface(), Typeface.BOLD);
                    tvDelCharges.setTypeface(tvDelChargesVal.getTypeface(), Typeface.BOLD);
                }

                View vDivider = view.findViewById(R.id.vDivider);

                if (i==datum.getBillSummary().size()-1) {
                    vDivider.setVisibility(View.INVISIBLE);
                }

                llFeedExtraCharges.addView(view);
                ASSL.DoMagic(relative);
                i++;

            }

        }
        else {
            cardFeedBillSummary.setVisibility(View.GONE);
        }

//todo delivery

        if (datum1.getImages() != null) {
            final HistoryResponse.OrderImageTypes images = datum1.getImages();

            // pickup images
            if(images.getPickupImages() != null && images.getPickupImages().size() > 0) {

                ArrayList<FetchFeedbackResponse.ReviewImage> imageList = new ArrayList<>();

                for (HistoryResponse.OrderImages image :images.getPickupImages()) {
                    imageList.add(new FetchFeedbackResponse.ReviewImage(image.getImage(), image.getImage()));
                }

                setOrderImages(imageList, rvFeedPickupImages, images.getPickupImages());
                cardPickupFeedPhotos.setVisibility(View.VISIBLE);
            } else {
                cardPickupFeedPhotos.setVisibility(View.GONE);
            }

            // delivery images

            if(images.getDeliveryImages() != null && images.getDeliveryImages().size() > 0) {


                ArrayList<FetchFeedbackResponse.ReviewImage> imageList = new ArrayList<>();

                int deliveryId = images.getDeliveryImages().get(0).getDelivery_id();
                int count = 1;

                for (HistoryResponse.OrderImages image :images.getDeliveryImages()) {
                    imageList.add(new FetchFeedbackResponse.ReviewImage(image.getImage(), image.getImage()));
                    if (deliveryId != image.getDelivery_id()) {
                        deliveryId = image.getDelivery_id();
                        count++;
                    }
                    image.setDeliveryNo(count);
                }

                setOrderImages(imageList, rvFeedDeliveriesImages,images.getDeliveryImages());
                cardDeliveriesFeedPhotos.setVisibility(View.VISIBLE);
            } else {
                cardDeliveriesFeedPhotos.setVisibility(View.GONE);
            }
        }
    }

    private void setStripeData(HistoryResponse.Datum datum, Activity activity) {
        ArrayList<DiscountType> stripeCardEntries = datum.getStripeCardsAmount();
        if(stripeCardEntries.size() > 0){
            listViewStripeTxns.setVisibility(View.VISIBLE);
            stripeTxnsAdapter.setList(stripeCardEntries, datum.getCurrencyCode(), true);
        }
//        else if(Utils.compareDouble(datum.getPaidUsingStripe(), 0) > 0){
//            listViewStripeTxns.setVisibility(View.VISIBLE);
//            ArrayList<DiscountType> discountTypes = new ArrayList<>();
//
//            if(!TextUtils.isEmpty(endRideData.getLast_4())){
//                discountTypes.add(new DiscountType(WalletCore.getStripeCardDisplayString(activity,endRideData.getLast_4()),
//                        endRideData.getPaidUsingStripe(), 0));
//            }else{
//                String card = MyApplication.getInstance().getWalletCore().getConfigDisplayNameCards(activity, PaymentOption.STRIPE_CARDS.getOrdinal());
//                if(TextUtils.isDigitsOnly(card)){
//                    card = WalletCore.getStripeCardDisplayString(activity, card);
//                }
//                discountTypes.add(new DiscountType(card,
//                        endRideData.getPaidUsingStripe(), 0));
//
//            }
//            stripeTxnsAdapter.setList(discountTypes, endRideData.getCurrency());
////                    tvEndRideStripeCardValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), endRideData.getPaidUsingStripe()));
//        }
        else{
            listViewStripeTxns.setVisibility(View.GONE);
        }
    }

    private void setOrderImages(final ArrayList<FetchFeedbackResponse.ReviewImage> adapterImageList,
                                final RecyclerView recyclerView, final ArrayList<HistoryResponse.OrderImages> imageList) {

        if(recyclerView == null) return;

        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new ImageWithTextAdapter(imageList, new ImageWithTextAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final HistoryResponse.OrderImages image, final int pos) {
                ReviewImagePagerDialog dialog = ReviewImagePagerDialog.newInstance(pos,adapterImageList);
                dialog.show(activity.getFragmentManager(), ReviewImagePagerDialog.class.getSimpleName());
            }
        }));

    }


    private void retryDialogCancelOrderOrOrderStatusFeed(String message, DialogErrorType dialogErrorType) {
        if (TextUtils.isEmpty(message)) {
            DialogPopup.dialogNoInternet(activity,
                    dialogErrorType,
                    new Utils.AlertCallBackWithButtonsInterface() {
                        @Override
                        public void positiveClick(View view) {
                            if(productType == ProductType.PROS.getOrdinal()) {
                            } else {
                                getFeedOrderData(activity);
                            }
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
                            if(productType == ProductType.PROS.getOrdinal()) {
                            } else {
                                getFeedOrderData(activity);
                            }
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.onBackPressed();
                        }
                    }, false, false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(orderUpdateBroadcast);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (activity instanceof FreshActivity) {
                ((FreshActivity) activity).fragmentUISetup(this);
            }

            if (isFeedOrder){
                getFeedOrderData(activity);
            } else{
                getOrderData(activity);
            }
            setFragTitle();
            if (datum1.getCancellable() == 1) {
                orderCancel.setVisibility(View.VISIBLE);
                orderComplete.setVisibility(View.GONE);
                if (activity instanceof SupportActivity) {
                    cancelfeedbackBtn.setVisibility(View.GONE);
                }

            } else {
                orderComplete.setVisibility(View.VISIBLE);
                orderCancel.setVisibility(View.GONE);
                cancelfeedbackBtn.setVisibility(View.GONE);
                if (activity instanceof RideTransactionsActivity) {
                    feedbackBtn.setText(R.string.need_help);
                    if (datum1.getCanReorder() == 1) {
                        reorderBtn.setVisibility(View.VISIBLE);
                    } else {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                        buttonCancelOrder.setBackgroundResource(R.drawable.capsule_theme_color_selector);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    }
                } else {
                    if (datum1.getCanReorder() == 1 && !(activity instanceof FreshActivity)) {
                        reorderBtn.setVisibility(View.VISIBLE);
                        feedbackBtn.setVisibility(View.GONE);
                    } else if (datum1.getCanReorder() == 1 && (activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                        buttonCancelOrder.setBackgroundResource(R.drawable.capsule_theme_color_selector);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    } else if (datum1.getCanReorder() == 0 && !(activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                        buttonCancelOrder.setBackgroundResource(R.drawable.capsule_theme_color_selector);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    } else {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                        buttonCancelOrder.setBackgroundResource(R.drawable.capsule_theme_color_selector);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    }

                }
            }
        }
    }

    private void setFragTitle() {
        if (activity instanceof RideTransactionsActivity) {
            ((RideTransactionsActivity) activity).setTitle(getString(R.string.order_hash_format, String.valueOf(orderId)));
        } else if (activity instanceof SupportActivity) {
            ((SupportActivity) activity).setTitle(getString(R.string.order_hash_format, String.valueOf(orderId)));
        } else if (activity instanceof FreshActivity) {
            ((FreshActivity) activity).getTopBar().title.setText(getString(R.string.order_hash_format, String.valueOf(orderId)));
        }
    }

    /**
     * Method used to get order information
     */
    public void getOrderData(final Activity activity) {
        try {
            if (MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(activity, getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_ORDER_ID, "" + orderId);
                params.put(Constants.KEY_PRODUCT_TYPE, "" + productType);
                params.put(Constants.KEY_CLIENT_ID,getClientIdByProductType(productType));
                params.put(Constants.INTERATED, "1");

                Callback<HistoryResponse> callback = new Callback<HistoryResponse>() {
                    @Override
                    public void success(HistoryResponse historyResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i("Server response", "response = " + responseStr);
                        try {
                            responseOrderDataApi = new JSONObject(responseStr);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, responseOrderDataApi)) {
                                int flag = responseOrderDataApi.getInt("flag");
                                String message = JSONParser.getServerMessage(responseOrderDataApi);
                                if (ApiResponseFlags.RECENT_RIDES.getOrdinal() == flag) {
                                    llMain.setVisibility(View.VISIBLE);
                                    datum1 = historyResponse.getData().get(0);
                                    currencyCode = datum1.getCurrencyCode();
                                    currency = datum1.getCurrency();
                                    setOrderItemAdapter();
                                    subItemsOrders.clear();
                                    subItemsOrders.addAll(historyResponse.getData().get(0).getOrderItems());
                                    orderItemsAdapter.notifyDataSetChanged();
                                    tvItemsQuantityVal.setText(String.valueOf(subItemsOrders.size()));
                                    setStatusResponse(historyResponse);
                                    openTrackOrderFragment();
                                } else {
                                    retryDialogOrderData(message, DialogErrorType.SERVER_ERROR);
                                }


                            }else{

                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialogOrderData("", DialogErrorType.SERVER_ERROR);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("TAG", "getRecentRidesAPI error=" + error.toString());
                        DialogPopup.dismissLoadingDialog();
                        retryDialogOrderData("", DialogErrorType.CONNECTION_LOST);
                    }
                };

                new HomeUtil().putDefaultParams(params);
                if (productType == ProductType.MENUS.getOrdinal()
                    || productType == ProductType.DELIVERY_CUSTOMER.getOrdinal()) {
                    RestClient.getMenusApiService().orderHistory(params, callback);
                } else {
                    RestClient.getFreshApiService().orderHistory(params, callback);
                }
            } else {
                retryDialogOrderData("", DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialogOrderData(String message, DialogErrorType dialogErrorType) {
        if (TextUtils.isEmpty(message)) {
            DialogPopup.dialogNoInternet(activity,
                    dialogErrorType,
                    new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                        @Override
                        public void positiveClick(View view) {
                            getOrderData(activity);
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
                            getOrderData(activity);
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.onBackPressed();
                        }
                    }, false, false);
        }
    }


    private void showPossibleStatus(ArrayList<String> possibleStatus, int status,boolean isFeedOrder) {
        setDefaultState();
        int selectedSize = (int) (35 * ASSL.Xscale());
        if(isFeedOrder){
            tvStatus3.setVisibility(View.GONE);
            ivStatus3.setVisibility(View.GONE);
            lineStatus3.setVisibility(View.GONE);
        }else{
            tvStatus3.setVisibility(View.VISIBLE);
            ivStatus3.setVisibility(View.VISIBLE);
            lineStatus3.setVisibility(View.VISIBLE);
        }
        switch (possibleStatus.size()) {
            case 4:
                tvStatus3.setVisibility(View.VISIBLE);
                ivStatus3.setVisibility(View.VISIBLE);
                lineStatus3.setVisibility(View.VISIBLE);
                tvStatus3.setText(possibleStatus.get(3));
                if (status == 3) {
                    ivStatus3.setBackgroundResource(R.drawable.circle_order_status_green);
                    lineStatus3.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                } else if (status > 3) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    ivStatus3.setLayoutParams(layoutParams);
                    ivStatus3.setBackgroundResource(R.drawable.ic_order_status_green);
                    lineStatus3.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                }
            case 3:
                tvStatus2.setVisibility(View.VISIBLE);
                ivStatus2.setVisibility(View.VISIBLE);
                lineStatus2.setVisibility(View.VISIBLE);
                tvStatus2.setText(possibleStatus.get(2));
                if (status == 2) {
                    ivStatus2.setBackgroundResource(R.drawable.circle_order_status_green);
                    lineStatus2.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                } else if (status > 2) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    ivStatus2.setLayoutParams(layoutParams);
                    ivStatus2.setBackgroundResource(R.drawable.ic_order_status_green);
                    lineStatus2.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                }
            case 2:
                tvStatus1.setVisibility(View.VISIBLE);
                ivStatus1.setVisibility(View.VISIBLE);
                lineStatus1.setVisibility(View.VISIBLE);
                tvStatus1.setText(possibleStatus.get(1));
                if (status == 1) {
                    ivStatus1.setBackgroundResource(R.drawable.circle_order_status_green);
                    lineStatus1.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                } else if (status > 1) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    ivStatus1.setLayoutParams(layoutParams);
                    ivStatus1.setBackgroundResource(R.drawable.ic_order_status_green);
                    lineStatus1.setBackgroundColor(activity.getResources().getColor(R.color.order_status_green));
                }
            case 1:
                tvStatus0.setVisibility(View.VISIBLE);
                ivStatus0.setVisibility(View.VISIBLE);
                tvStatus0.setText(possibleStatus.get(0));
                if (status == 0) {
                    ivStatus0.setBackgroundResource(R.drawable.circle_order_status_green);
                } else if (status > 0) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
                    ivStatus0.setLayoutParams(layoutParams);
                    ivStatus0.setBackgroundResource(R.drawable.ic_order_status_green);
                }
                break;
        }
    }

    private void setDefaultState() {
        int selectedSize = (int) (25 * ASSL.Xscale());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(selectedSize, selectedSize);
        ivStatus3.setBackgroundResource(R.drawable.circle_order_status);
        ivStatus3.setLayoutParams(layoutParams);
        lineStatus3.setBackgroundColor(activity.getResources().getColor(R.color.rank_5));
        ivStatus2.setBackgroundResource(R.drawable.circle_order_status);
        ivStatus2.setLayoutParams(layoutParams);
        lineStatus2.setBackgroundColor(activity.getResources().getColor(R.color.rank_5));
        ivStatus1.setBackgroundResource(R.drawable.circle_order_status);
        ivStatus1.setLayoutParams(layoutParams);
        lineStatus1.setBackgroundColor(activity.getResources().getColor(R.color.rank_5));
        ivStatus0.setBackgroundResource(R.drawable.circle_order_status);
        ivStatus0.setLayoutParams(layoutParams);
    }

    /**
     * to show positive/negative fields
     *
     * @param llFinalAmount
     * @param fieldText
     * @param fieldTextVal
     * @param showNegative
     * @return
     */
    private View addFinalAmountView(LinearLayout llFinalAmount, String fieldText, Double fieldTextVal, boolean showNegative) {
        return addFinalAmountView(llFinalAmount, fieldText, fieldTextVal, showNegative, false, true, false);
    }

    private View addFinalAmountView(LinearLayout llFinalAmount, String fieldText, Double fieldTextVal,
                                    boolean showNegative, boolean showFree, boolean showDivider, boolean showAsterisk) {
        try {
            llFinalAmount.setVisibility(View.VISIBLE);
            LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.layout_order_amount_new, null);
            RelativeLayout relative = (RelativeLayout) view.findViewById(R.id.relative);
            TextView tvDelCharges = (TextView) view.findViewById(R.id.tvDelCharges);
            TextView tvDelChargesVal = (TextView) view.findViewById(R.id.tvDelChargesVal);

            tvDelCharges.setText(fieldText);

            if (showNegative) {
                tvDelChargesVal.setText("- " + com.sabkuchfresh.utils.Utils.formatCurrencyAmount(fieldTextVal, currencyCode, currency));
                tvDelChargesVal.setTextColor(ContextCompat.getColor(activity, R.color.order_status_green));
            } else {
                if (fieldTextVal > 0) {
                    tvDelChargesVal.setText(com.sabkuchfresh.utils.Utils.formatCurrencyAmount(fieldTextVal, currencyCode, currency));
                    tvDelChargesVal.setTextColor(ContextCompat.getColor(activity, R.color.text_color));
                } else {
                    if (showFree) {
                        tvDelChargesVal.setText(activity.getResources().getString(R.string.free));
                        tvDelChargesVal.setTextColor(ContextCompat.getColor(activity, R.color.order_status_green));
                    } else {
                        if (Utils.compareDouble(fieldTextVal, 0) == -1) {
                            tvDelChargesVal.setText("- " + com.sabkuchfresh.utils.Utils.formatCurrencyAmount(Math.abs(fieldTextVal), currencyCode, currency));
                        } else {
                            tvDelChargesVal.setText(com.sabkuchfresh.utils.Utils.formatCurrencyAmount(fieldTextVal, currencyCode, currency));
                        }
                        tvDelChargesVal.setTextColor(ContextCompat.getColor(activity, R.color.text_color));
                    }
                }
            }
            if (showAsterisk) {
                tvDelChargesVal.append("*");
            }
            View vDivider = view.findViewById(R.id.vDivider);
            if (!showDivider) {
                vDivider.setVisibility(View.INVISIBLE);
            }
            llFinalAmount.addView(view);
            ASSL.DoMagic(relative);
            return vDivider;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private double getSubTotalAmount(HistoryResponse historyResponse) {
        Double subTotal = 0d;
        for (HistoryResponse.OrderItem orderItem : historyResponse.getData().get(0).getOrderItems()) {
            subTotal = subTotal + orderItem.getItemAmount();
        }
        return subTotal;
    }

    private void setStatusResponse(HistoryResponse historyResponse) {
        try {
            HistoryResponse.Datum datum1 = historyResponse.getData().get(0);

            tvOrderStatusVal.setText(datum1.getOrderStatus());
            if ((datum1.getOrderStatusInt() == OrderStatus.ORDER_COMPLETED.getOrdinal())
                    || datum1.getOrderStatusInt() == OrderStatus.CASH_RECEIVED.getOrdinal()) {
                ivOrderCompleted.setVisibility(View.VISIBLE);
            } else {
                ivOrderCompleted.setVisibility(View.GONE);
            }

            if (historyResponse.getRecentOrdersPossibleStatus().size() > 0) {
                cvOrderStatus.setVisibility(View.VISIBLE);
                rlOrderStatus.setVisibility(View.GONE);
                setDefaultState();
                showPossibleStatus(historyResponse.getRecentOrdersPossibleStatus(), datum1.getOrderTrackingIndex(),false);
            } else {
                cvOrderStatus.setVisibility(View.GONE);
                rlOrderStatus.setVisibility(View.VISIBLE);
            }

            btRateOrder.setVisibility(datum1.isPendingFeedback() ? View.VISIBLE : View.GONE);

            tvOrderTimeVal.setText(DateOperations.convertDateViaFormat(DateOperations.utcToLocalWithTZFallback(datum1.getOrderTime())));

            if (datum1.getProductType() == ProductType.MENUS.getOrdinal()
                || datum1.getProductType() == ProductType.DELIVERY_CUSTOMER.getOrdinal()) {
                tvDeliveryTime.setText(activity.getString(R.string.delivered_from_colon));

                tvDeliveryTimeVal.setText("");
                final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                final SpannableStringBuilder sb = new SpannableStringBuilder("      " + datum1.getRestaurantName());
                sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvDeliveryTimeVal.append(sb);
                tvDeliveryTimeVal.append("\n" + datum1.getRestaurantAddress());
                imageViewRestaurant.setVisibility(View.VISIBLE);
                imageViewCallRestaurant.setVisibility(TextUtils.isEmpty(datum1.getRestaurantPhoneNo()) ? View.GONE : View.VISIBLE);
            } else {
                if (datum1.getStartTime() != null && datum1.getEndTime() != null) {
                    tvDeliveryTimeVal.setText(datum1.getExpectedDeliveryDate() + " " +
                            DateOperations.convertDayTimeAPViaFormat(datum1.getStartTime(), false).replace("AM", "").replace("PM", "") + " - " + DateOperations.convertDayTimeAPViaFormat(datum1.getEndTime(), false));
                } else {
                    tvDeliveryTimeVal.setText(datum1.getExpectedDeliveryDate());
                }
                imageViewRestaurant.setVisibility(View.GONE);
                imageViewCallRestaurant.setVisibility(View.GONE);
            }
            tvDeliveryToVal.setText(datum1.getDeliveryAddress());


            tvOrderStatusVal.setTextColor(Color.parseColor(datum1.getOrderStatusColor()));

            if (!datum1.getDeliveryAddressType().equalsIgnoreCase("")) {
                llDeliveryPlace.setVisibility(View.VISIBLE);
                tvDelveryPlace.setText(datum1.getDeliveryAddressType());
                if (datum1.getDeliveryAddressType().equalsIgnoreCase(activity.getString(R.string.home))) {
                    ivDeliveryPlace.setImageResource(R.drawable.home);
                } else if (datum1.getDeliveryAddressType().equalsIgnoreCase(activity.getString(R.string.work))) {
                    ivDeliveryPlace.setImageResource(R.drawable.work);
                } else {
                    ivDeliveryPlace.setImageResource(R.drawable.ic_loc_other);
                }
            } else {
                llDeliveryPlace.setVisibility(View.GONE);
            }

            tvTotalAmountValOld.setVisibility(View.GONE);
            if (Double.compare(datum1.getDiscountedAmount(), datum1.getOrderAmount()) != 0) {
                tvTotalAmountValOld.setVisibility(View.VISIBLE);
                tvTotalAmountValOld.setText(com.sabkuchfresh.utils.Utils.formatCurrencyAmount(datum1.getOrderAmount(), currencyCode, currency));
            }

            tvTotalAmountVal.setText(com.sabkuchfresh.utils.Utils.formatCurrencyAmount(datum1.getDiscountedAmount(), currencyCode, currency));

            tvTotalAmountMessage.setVisibility(TextUtils.isEmpty(datum1.getNote()) ? View.GONE : View.VISIBLE);
            tvTotalAmountMessage.setText(datum1.getNote());
            boolean showAsterisk = !TextUtils.isEmpty(datum1.getNote());


            llExtraCharges.removeAllViews();
            addFinalAmountView(llExtraCharges, activity.getString(R.string.sub_total), datum1.getSubTotal(), false, false, true, showAsterisk);

            if ((datum1.getDiscount() != null) && (datum1.getDiscount() > 0)) {
                addFinalAmountView(llExtraCharges, activity.getString(R.string.discount), datum1.getDiscount(), true);
            }

            if (datum1.getCharges() != null) {
                for (Charges charges : datum1.getCharges()) {
                    try {
                        if (Double.parseDouble(charges.getValue()) > 0) {
                            addFinalAmountView(llExtraCharges, charges.getText(), Double.parseDouble(charges.getValue()), false);
                        }
                        else if (Double.parseDouble(charges.getValue()) < 0) {
                            addFinalAmountView(llExtraCharges, charges.getText(), Double.parseDouble(charges.getValue()), true, false, true, false);
                        }
                        else if (charges.getForceShow() == 1) {
                            addFinalAmountView(llExtraCharges, charges.getText(), Double.parseDouble(charges.getValue()), false, true, true, false);
                        }
                    } catch (Exception e) {
                    }
                }
            }


            setPaymentModes(datum1);

            if (datum1.getCancellable() == 1) {
                orderCancel.setVisibility(View.VISIBLE);
                orderComplete.setVisibility(View.GONE);
                if (activity instanceof SupportActivity) {
                    cancelfeedbackBtn.setVisibility(View.GONE);
                }

            } else {
                orderComplete.setVisibility(View.VISIBLE);
                orderCancel.setVisibility(View.GONE);
                cancelfeedbackBtn.setVisibility(View.GONE);
                if (activity instanceof RideTransactionsActivity) {
                    feedbackBtn.setText(R.string.need_help);
                    if (datum1.getCanReorder() == 1) {
                        reorderBtn.setVisibility(View.VISIBLE);
                    } else {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                        buttonCancelOrder.setBackgroundResource(R.drawable.capsule_theme_color_selector);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    }
                } else {
                    if (datum1.getCanReorder() == 1 && !(activity instanceof FreshActivity)) {
                        reorderBtn.setVisibility(View.VISIBLE);
                        feedbackBtn.setVisibility(View.GONE);
                    } else if (datum1.getCanReorder() == 1 && (activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                        buttonCancelOrder.setBackgroundResource(R.drawable.capsule_theme_color_selector);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    } else if (datum1.getCanReorder() == 0 && !(activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                        buttonCancelOrder.setBackgroundResource(R.drawable.capsule_theme_color_selector);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    } else if (datum1.getCanReorder() == 0 && (activity instanceof FreshActivity)) {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.ok);
                        buttonCancelOrder.setBackgroundResource(R.drawable.capsule_theme_color_selector);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    } else {
                        orderComplete.setVisibility(View.GONE);
                        orderCancel.setVisibility(View.VISIBLE);
                        buttonCancelOrder.setText(R.string.need_help);
                        buttonCancelOrder.setBackgroundResource(R.drawable.capsule_theme_color_selector);
                        buttonCancelOrder.setTextColor(activity.getResources().getColor(R.color.white));
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPaymentModes(HistoryResponse.Datum datum1) {
        llFinalAmount.removeAllViews();
        vDividerPayment = cvPaymentMethod.findViewById(R.id.vDividerPayment);
        if (datum1.getJugnooDeducted() > 0) {
            vDividerPayment = addFinalAmountView(llFinalAmount, activity.getString(R.string.jugnoo_cash, activity.getString(R.string.app_name)), datum1.getJugnooDeducted(), false);
        }
        if (datum1.getWalletDeducted() > 0) {
            vDividerPayment = addFinalAmountView(llFinalAmount,getString(R.string.card),datum1.getWalletDeducted(),false);
        }

        if(datum1.getStripeCardsAmount().size() > 0) {
            setStripeData(datum1, activity);
            rlWalletDeducted.setVisibility(View.GONE);
            llFinalAmount.setVisibility(View.GONE);
        } else if (datum1.getWalletDeducted() > 0) {
            listViewStripeTxns.setVisibility(View.GONE);
            rlWalletDeducted.setVisibility(View.VISIBLE);
            llPaymentSummary.removeView(rlWalletDeducted);
            llFinalAmount.addView(rlWalletDeducted);
//            tvAmountPayableVal.setText(activity.getString(R.string.rupees_value_format,
//                    Utils.getMoneyDecimalFormat().format(datum1.getWalletDeducted())));
            tvAmountPayableVal.setText(Data.currencyConfirm+Utils.getMoneyDecimalFormat().format(datum1.getAmount()));
            llFinalAmount.setVisibility(View.VISIBLE);
            vDividerPayment = cvPaymentMethod.findViewById(R.id.vDividerPayment);
        } else {
            listViewStripeTxns.setVisibility(View.GONE);
            rlWalletDeducted.setVisibility(View.GONE);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvPaymentMethodVal.getLayoutParams();
        tvPaymentMethodVal.setPaddingRelative(0, 0, 0, 0);
        tvPaymentMethodVal.setText(getString(R.string.total));
        tvPaymentMethodVal.setBackgroundResource(R.drawable.background_transparent);
        if (datum1.getPaymentMode() == PaymentOption.PAYTM.getOrdinal()) {
            params.setMargins((int) (ASSL.Xscale() * 10f), 0, 0, 0);
            params.setMarginStart((int) (ASSL.Xscale() * 10f));
            params.setMarginEnd(0);
            tvPaymentMethodVal.setBackgroundResource(R.drawable.ic_paytm_small);
        } else if (datum1.getPaymentMode() == PaymentOption.MOBIKWIK.getOrdinal()) {
            params.setMargins((int) (ASSL.Xscale() * 25f), 0, 0, 0);
            params.setMarginStart((int) (ASSL.Xscale() * 25f));
            params.setMarginEnd(0);
            tvPaymentMethodVal.setBackgroundResource(R.drawable.ic_mobikwik_small);
        } else if (datum1.getPaymentMode() == PaymentOption.FREECHARGE.getOrdinal()) {
            params.setMargins((int) (ASSL.Xscale() * 30f), 0, 0, 0);
            params.setMarginStart((int) (ASSL.Xscale() * 30f));
            params.setMarginEnd(0);
            tvPaymentMethodVal.setBackgroundResource(R.drawable.ic_freecharge_small);
        } else if (datum1.getPaymentMode() == PaymentOption.JUGNOO_PAY.getOrdinal()) {
            tvPaymentMethodVal.setText(R.string.jugnoo_pay);
            params.setMargins((int) (ASSL.Xscale() * 35f), 0, 0, 0);
            params.setMarginStart((int) (ASSL.Xscale() * 35f));
            params.setMarginEnd(0);
        } else if (datum1.getPaymentMode() == PaymentOption.RAZOR_PAY.getOrdinal()) {
            if (!TextUtils.isEmpty(datum1.getOtherPaymentModeText())) {
                tvPaymentMethodVal.setText(datum1.getOtherPaymentModeText());
            } else {
                tvPaymentMethodVal.setText(R.string.other_payment_mode);
            }
            params.setMargins((int) (ASSL.Xscale() * 35f), 0, 0, 0);
            params.setMarginStart((int) (ASSL.Xscale() * 35f));
            params.setMarginEnd(0);
        } else if(datum1.getPaymentMode()==PaymentOption.ICICI_UPI.getOrdinal()){
            params.setMargins((int) (ASSL.Xscale() * 34f), 0, 0, 0);
            params.setMarginStart((int) (ASSL.Xscale() * 34f));
            params.setMarginEnd(0);
            params.height= (int) (ASSL.minRatio() * 35f);
            params.width= (int) (ASSL.minRatio() * 90f);
            tvPaymentMethodVal.setBackgroundResource(R.drawable.upi_logo);
        } else if(datum1.getPaymentMode()==PaymentOption.STRIPE_CARDS.getOrdinal()){
            params.setMargins((int) (ASSL.Xscale() * 34f), 0, 0, 0);
            params.setMarginStart((int) (ASSL.Xscale() * 34f));
            params.setMarginEnd(0);
            params.height= (int) (ASSL.minRatio() * 35f);
            params.width= (int) (ASSL.minRatio() * 90f);
            tvPaymentMethodVal.setText(activity.getString(R.string.card));
        }
        tvPaymentMethodVal.setLayoutParams(params);


        if (datum1.getPayableAmount() > 0) {
            vDividerPayment = addFinalAmountView(llFinalAmount, activity.getString(R.string.cash), datum1.getPayableAmount(), false);
        }
        if (Utils.compareDouble(datum1.getOrderAdjustment(), 0) != 0) {
            vDividerPayment = addFinalAmountView(llFinalAmount, activity.getString(R.string.order_adjustment), datum1.getOrderAdjustment(), false);
        }
        if (datum1.getRefundAmount() > 0) {
            vDividerPayment = addFinalAmountView(llFinalAmount, activity.getString(R.string.refund), datum1.getRefundAmount(), false);
        }

        if (vDividerPayment != null) {
            vDividerPayment.setVisibility(View.INVISIBLE);
        }

        if (llFinalAmount.getChildCount() > 0 || datum1.getWalletDeducted() > 0) {
            cvPaymentMethod.setVisibility(View.VISIBLE);
        } else {
            cvPaymentMethod.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int tag = v.getId();
        switch (tag) {
            case R.id.buttonCancelOrder:
                if (datum1.getCancellable() == 1) {
                    int storeId = datum1.getStoreId() == null ? 0 : datum1.getStoreId();
                    if (datum1.getShowCancellationReasons() == 1) {
                        int containerId = -1;
                        if (activity instanceof FreshActivity) {
                            containerId = ((FreshActivity) activity).getRelativeLayoutContainer().getId();
                        } else if (activity instanceof RideTransactionsActivity) {
                            containerId = ((RideTransactionsActivity) activity).getContainer().getId();
                        } else if (activity instanceof SupportActivity) {
                            containerId = ((SupportActivity) activity).getContainer().getId();
                        }
                        if (containerId > -1) {
                            activity.getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                    .add(containerId, OrderCancelReasonsFragment.newInstance(datum1.getOrderId(),
                                            productType, storeId, getClientIdByProductType(productType)),
                                            OrderCancelReasonsFragment.class.getName())
                                    .addToBackStack(OrderCancelReasonsFragment.class.getName())
                                    .hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
                                            .getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                                    .commitAllowingStateLoss();
                        }
                    } else {
                        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", getString(R.string.are_you_sure_cancel_order), getResources().getString(R.string.ok),
                                getResources().getString(R.string.cancel), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancelOrderApi();
                                        if (activity instanceof FreshActivity) {
                                            GAUtils.event(((FreshActivity) activity).getGaCategory(), ORDER_STATUS, ORDER + CANCELLED);
                                        }
                                    }
                                }, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }, false, false);
                    }
                } else {
                    feedbackBtn.performClick();
                }
                break;
            case R.id.feedbackBtn:
                needHelpClick();
                break;
            case R.id.reorderBtn:
                try {
                    if(productType==ProductType.MENUS.getOrdinal() || productType==ProductType.DELIVERY_CUSTOMER.getOrdinal()){
                        if(responseOrderDataApi!=null){
                            JSONArray jsonArray = responseOrderDataApi.getJSONArray("data");
                            if(jsonArray!=null && jsonArray.length()>0){
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                Integer restaurantId = jsonObject.getInt(Constants.KEY_RESTAURANT_ID);
                                JSONArray cartItems = jsonObject.getJSONArray("order_items");
                                LatLng delLatlng =null;
                                String delAddress  = null;
                                try {
                                    if(jsonObject.has("delivery_latitude") && jsonObject.has("delivery_longitude")){
                                        Double delivery_latitude = jsonObject.getDouble("delivery_latitude");
                                        Double delivery_longitude = jsonObject.getDouble("delivery_longitude");
                                        delLatlng = new LatLng(delivery_latitude,delivery_longitude);
                                    }
                                    if(jsonObject.has("delivery_address")){
                                        delAddress  = jsonObject.getString("delivery_address");
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                reorderMenus(cartItems,restaurantId,delLatlng,delAddress);

                            }
                        }

                    }else{
                        saveHistoryCardToSP(datum1);
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }
                break;
            case R.id.cancelfeedbackBtn:
                needHelpClick();
                if (activity instanceof FreshActivity) {
                    GAUtils.event(((FreshActivity) activity).getGaCategory(), ORDER_STATUS, NEED_HELP);
                } else {
                    GAUtils.event(GACategory.SIDE_MENU, ORDER + DETAILS, NEED_HELP + CLICKED);
                }
                break;
            case R.id.btRateOrder:
                openFeedBackFragment();
                break;
        }
    }

    private void needHelpClick() {
        if (activity instanceof RideTransactionsActivity) {
            if (Data.isFuguChatEnabled()) {
                try {

                    if(!TextUtils.isEmpty(datum1.getFuguChannelId())){
                        ChatByUniqueIdAttributes chatAttr = new ChatByUniqueIdAttributes.Builder()
                                .setTransactionId(datum1.getFuguChannelId())
                                .setUserUniqueKey(String.valueOf(Data.getFuguUserData().getUserId()))
                                .setChannelName(datum1.getFuguChannelName())
                                .setTags(datum1.getFuguTags())
                                .build();
                        HippoConfig.getInstance().openChatByUniqueId(chatAttr);
                    }else{
                        HippoConfig.getInstance().openChat(getActivity(), Data.CHANNEL_ID_FUGU_ISSUE_ORDER());
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
                        -1, -1, null, null, 0, false, 0, datum1, -1, -1, "");
            }
        } else {
            activity.onBackPressed();
        }
    }

    public void saveHistoryCardToSP(HistoryResponse.Datum orderHistory) {
        try {
            DialogPopup.showLoadingDialog(activity, getString(R.string.please_wait));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.finish();
                }
            }, 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver orderUpdateBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int flag = intent.getIntExtra(Constants.KEY_FLAG, -1);
                        int orderId = intent.getIntExtra(Constants.KEY_ORDER_ID, 0);
                        if (PushFlags.STATUS_CHANGED.getOrdinal() == flag) {
                            if(isFeedOrder){
                                getFeedOrderData(activity);
                            }else{
                                getOrderData(activity);

                            }
                        } else if (PushFlags.MENUS_STATUS.getOrdinal() == flag || PushFlags.MENUS_STATUS_SILENT.getOrdinal() == flag) {
                            if(orderId == OrderStatusFragment.this.orderId) {
                                if(isFeedOrder){
                                    getFeedOrderData(activity);
                                }else{
                                    getOrderData(activity);

                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    };

    private ApiCancelOrder apiCancelOrder;

    private void cancelOrderApi() {
        if (apiCancelOrder == null) {
            apiCancelOrder = new ApiCancelOrder(activity, new ApiCancelOrder.Callback() {
                @Override
                public void onSuccess(String message) {
                    DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Data.isOrderCancelled = true;
                            datum1.setCancellable(0);

                            Intent intent = new Intent(Data.LOCAL_BROADCAST);
                            intent.putExtra("message", getString(R.string.order_cancelled_refresh_inventory));
                            intent.putExtra("open_type", 10);
                            LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);

                            activity.onBackPressed();
                        }
                    });
                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onRetry(View view) {
                    cancelOrderApi();
                }

                @Override
                public void onNoRetry(View view) {

                }
            });
        }
        int storeId = datum1.getStoreId() == null ? 0 : datum1.getStoreId();
        apiCancelOrder.hit(datum1.getOrderId(), getClientIdByProductType(productType),
                storeId,
                productType);
    }


    private void openTrackOrderFragment(){
        if(datum1.getLiveTracking() != null
                && datum1.getLiveTracking().getShowLiveTracking() == 1 && datum1.getLiveTracking().getDeliveryId() > 0) {
            if(getChildFragmentManager().findFragmentByTag(TrackOrderFragment.class.getName()) == null) {
                rlOrderStatusMapPeek.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                llShadowPeek.setVisibility(View.VISIBLE);
                rlContainer.setVisibility(View.VISIBLE);
                getChildFragmentManager().beginTransaction()
                        .replace(rlContainer.getId(), TrackOrderFragment.newInstance(Data.userData.accessToken,
                                datum1.getOrderId(), datum1.getLiveTracking().getDeliveryId(),
                                datum1.getLiveTracking().getPickupLatitude(), datum1.getLiveTracking().getPickupLongitude(),
                                datum1.getLiveTracking().getDeliveryLatitude(), datum1.getLiveTracking().getDeliveryLongitude(),
                                datum1.getLiveTracking().getShowDeliveryRoute(), datum1.getLiveTracking().getDriverPhoneNo(), llShadowPeekHeight,
                                openLiveTracking != 1),
                                TrackOrderFragment.class.getName())
                        .commitAllowingStateLoss();

                if (openLiveTracking == 1) {
                    scrollView.setVisibility(View.GONE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setLocked(true);
                } else {
                    scrollView.setVisibility(View.VISIBLE);
                    bottomSheetBehavior.setLocked(false);
                }
            }
        } else {
            bottomSheetBehavior.setLocked(true);
            rlOrderStatusMapPeek.setBackgroundColor(ContextCompat.getColor(activity, R.color.offer_popup_item_color));
            llShadowPeek.setVisibility(View.GONE);
            rlContainer.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            if(!performBack()){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    public boolean performBack(){
        if(openLiveTracking == 0 && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            return true;
        }
        return false;
    }

    private TrackOrderFragment getTrackOrderFragment(){
        Fragment fragment = getChildFragmentManager().findFragmentByTag(TrackOrderFragment.class.getName());
        if(fragment != null){
            return (TrackOrderFragment) fragment;
        }
        return null;
    }



    public void reorderMenus(JSONArray jsonArray, int restaurantId, LatLng latLng, String delAddress){


        if(activity instanceof FreshActivity &&
                ((productType == ProductType.MENUS.getOrdinal() && Config.getLastOpenedClientId(activity).equals(Config.getMenusClientId()))
                || (productType == ProductType.DELIVERY_CUSTOMER.getOrdinal() && Config.getLastOpenedClientId(activity).equals(Config.getDeliveryCustomerClientId()))
                )){

            ((FreshActivity)activity).onBackPressed();

            ((FreshActivity)activity).fetchRestaurantMenuAPI(restaurantId,true,jsonArray, latLng, orderId, delAddress);
        }else {
            Prefs.with(activity).save(Constants.ORDER_STATUS_PENDING_ID,restaurantId);
            Prefs.with(activity).save(Constants.ORDER_STATUS_JSON_ARRAY,jsonArray.toString());
            Prefs.with(activity).save(Constants.ORDER_STATUS_LAT_LNG,latLng, LatLng.class);
            Prefs.with(activity).save(Constants.ORDER_STATUS_ORDER_ID,orderId);
            Prefs.with(activity).save(Constants.ORDER_STATUS_ADDRESS,delAddress);
            MyApplication.getInstance().getAppSwitcher().switchApp(activity,
                    productType == ProductType.MENUS.getOrdinal() ? Config.getMenusClientId() : Config.getDeliveryCustomerClientId(),
                    new LatLng(Data.latitude,Data.longitude), false);


        }
    }

    public String getClientIdByProductType(int productTypeOrdinal){


        if(productTypeOrdinal==ProductType.AUTO.getOrdinal()){
            return Config.getAutosClientId();
        }else if(productTypeOrdinal==ProductType.DELIVERY_CUSTOMER.getOrdinal()){
            return Config.getDeliveryCustomerClientId();
        }else if(productType == ProductType.MENUS.getOrdinal()){
            return Config.getMenusClientId();
        }else if(productType==ProductType.FRESH.getOrdinal()){
            return Config.getFreshClientId();
        }else if(productType==ProductType.FEED.getOrdinal()){
            return Config.getFeedClientId();
        }else if(productType==ProductType.MEALS.getOrdinal()){
            return Config.getMealsClientId();
        }else if(productType==ProductType.PAY.getOrdinal()){
            return Config.getPayClientId();
        }else  if(productType==ProductType.GROCERY.getOrdinal()){
            return Config.getGroceryClientId();
        }
       return Config.getAutosClientId();
    }

    private com.sabkuchfresh.home.TransactionUtils getTransactionUtils() {
        if (transactionUtils == null) {
            transactionUtils = new com.sabkuchfresh.home.TransactionUtils();
        }
        return transactionUtils;
    }

    private void openFeedBackFragment() {
       View container = null;
        if (getActivity() instanceof RideTransactionsActivity) {
            container = ((RideTransactionsActivity)getActivity()).getContainer();
        } else if (activity instanceof FreshActivity) {
            container = ((FreshActivity)getActivity()).getRelativeLayoutContainer();
        }
        getTransactionUtils().openFeedback(getActivity(), container, getClientIdByProductType(productType),
                getFeedBackData(), false);
    }

    private LoginResponse.FeedbackData getFeedBackData() {
        LoginResponse.FeedbackData feedbackData = datum1.getFeedBackData();
        if (feedbackData == null) return null;

        feedbackData.setRestaurantName(datum1.getRestaurantName());
        feedbackData.setDriverName(datum1.getDriveName());
        feedbackData.setOrderId(String.valueOf(datum1.getOrderId()));
        feedbackData.setAmount(datum1.getDiscountedAmount() == 0 ? datum1.getAmount() : datum1.getDiscountedAmount());
        feedbackData.setFeedbackCurrencyCode(currencyCode);
        feedbackData.setFeedbackCurrency(currency);
        return feedbackData;
    }

}
