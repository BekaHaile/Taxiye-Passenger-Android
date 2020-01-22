package com.sabkuchfresh.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.jugnoo.pay.activities.MainActivity;
import com.jugnoo.pay.models.MessageRequest;
import com.jugnoo.pay.models.SendMoneyCallbackResponse;
import com.sabkuchfresh.adapters.BecomeStarAdapter;
import com.sabkuchfresh.adapters.CheckoutChargesAdapter;
import com.sabkuchfresh.adapters.DeliverySlotsAdapter;
import com.sabkuchfresh.adapters.FreshCartItemsAdapter;
import com.sabkuchfresh.adapters.MenusCartItemsAdapter;
import com.sabkuchfresh.adapters.VehicleTypeAdapterMenus;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.commoncalls.ApiCancelOrder;
import com.sabkuchfresh.datastructure.ApplicablePaymentMode;
import com.sabkuchfresh.datastructure.CheckoutSaveData;
import com.sabkuchfresh.dialogs.CheckoutPriceMismatchDialog;
import com.sabkuchfresh.dialogs.CheckoutRequestPaymentDialog;
import com.sabkuchfresh.dialogs.OrderCompleteReferralDialog;
import com.sabkuchfresh.enums.IciciPaymentOrderStatus;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.home.CallbackPaymentOptionSelector;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.home.FreshWalletBalanceLowDialog;
import com.sabkuchfresh.home.OrderCheckoutFailureDialog;
import com.sabkuchfresh.home.OrderCompletDialog;
import com.sabkuchfresh.retrofit.model.DeliverySlot;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.Slot;
import com.sabkuchfresh.retrofit.model.SlotViewType;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.retrofit.model.UserCheckoutResponse;
import com.sabkuchfresh.retrofit.model.common.IciciPaymentRequestStatus;
import com.sabkuchfresh.retrofit.model.feed.NearbyDriversResponse;
import com.sabkuchfresh.retrofit.model.menus.Category;
import com.sabkuchfresh.retrofit.model.menus.Charges;
import com.sabkuchfresh.retrofit.model.menus.CustomizeItemSelected;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.retrofit.model.menus.Subcategory;
import com.sabkuchfresh.retrofit.model.menus.Tax;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.datastructure.SubscriptionData;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.adapters.PromoCouponsRecyclerAdapter;
import product.clicklabs.jugnoo.home.dialogs.PromoCouponDialog;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.UserDebtDialog;
import product.clicklabs.jugnoo.wallet.WalletCore;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;
import product.clicklabs.jugnoo.widgets.MySpinner;
import product.clicklabs.jugnoo.widgets.SwipeButton.SwipeButton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static android.view.View.GONE;


public class FreshCheckoutMergedFragment extends Fragment implements GAAction, DeliverySlotsAdapter.Callback,
        FreshCartItemsAdapter.Callback, PromoCouponDialog.Callback, MenusCartItemsAdapter.Callback {


    private final String TAG = FreshCheckoutMergedFragment.class.getSimpleName();
    private RelativeLayout linearLayoutRoot;

    private RelativeLayout relativeLayoutCartTop;
    private CardView cvNoAvailableOffers, cvOffersAvailable;
    private NonScrollListView listViewCharges;
    private CheckoutChargesAdapter chargesAdapter;
    private TextView textViewCartItems, textViewCartTotalUndiscount;
    private ImageView imageViewCartArrow, imageViewDeleteCart, imageViewCartSep;
    private LinearLayout linearLayoutCartExpansion;
    private TextView tvBecomeStar, tvStarOffer;
    private NonScrollListView listViewCart;
    private FreshCartItemsAdapter freshCartItemsAdapter;

    private LinearLayout linearLayoutDeliverySlot;
    private RecyclerView recyclerViewDeliverySlots;
    private TextView textViewNoDeliverySlot;
    private DeliverySlotsAdapter deliverySlotsAdapter;

    private RelativeLayout relativeLayoutDeliveryAddress;
    private ImageView imageViewAddressType, imageViewDeliveryAddressForward;
    private TextView textViewAddressName, textViewAddressValue, tvNoAddressAlert;

    private RelativeLayout relativeLayoutPaytm,relativeLayoutStripeCard, relativeLayoutMobikwik, relativeLayoutFreeCharge, relativeLayoutJugnooPay, relativeLayoutCash;
    private LinearLayout linearLayoutWalletContainer;
    private ImageView imageViewPaytmRadio, imageViewAddPaytm, imageViewRadioMobikwik, imageViewAddMobikwik,imageViewStripeRadio,imageViewAddStripeCard,
            imageViewRadioFreeCharge, imageViewAddFreeCharge, imageViewRadioJugnooPay, imageViewAddJugnooPay, imageViewCashRadio,ivStripeCardIcon;
    private TextView textViewPaytmValue, tvStripeCardNumber, textViewMobikwikValue, textViewFreeChargeValue;
    private RelativeLayout rlOtherModesToPay, rlUPI;
    private ImageView ivOtherModesToPay, ivUPI;
    private TextView tvOtherModesToPay, tvUPI;

    private LinearLayout linearLayoutOffers;
    private RecyclerView listViewOffers;
    private PromoCouponsRecyclerAdapter promoCouponsAdapter;

    private EditText editTextDeliveryInstructions;

    private ScrollView scrollView;
    private LinearLayout linearLayoutMain;
    private TextView textViewScroll;

    private TextView textViewDeliveryInstructionsText, tvNoAvailableOffers;

    private CardView cvStarSavings, cvBecomeStar;
    private TextView tvStarSavingsValue;
    private View shadowMinOrder;


    private ArrayList<Slot> slots = new ArrayList<>();

    private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();

    private View rootView;
    private FreshActivity activity;

    // for payment screen
    private double subTotalAmount = 0;
    private double promoAmount = 0;

    private Bus mBus;
    private int currentGroupId = 1;
    private boolean orderPlaced = false;
    private boolean cartChangedRefreshCheckout = false;
    private MySpinner spin;
    boolean flag = false;
    private OrderCompletDialog dialogOrderComplete;
    private RelativeLayout relativeLayoutIcici;
    private ImageView imageViewIcici;
    private final static IntentFilter ICICI_STATUS_BROADCAST_FILTER = new IntentFilter(Constants.INTENT_ICICI_PAYMENT_STATUS_UPDATE);
    private TextView tvLabelIciciUpi;
    private TextView tvMinOrderLabelDisplay;
    private LinearLayout layoutMinOrder;
    private TextView tvOrderViaFatafat;
    private TextView labelOrMinOrder;


    public FreshCheckoutMergedFragment() {
    }

    private List<Product> productList = new ArrayList<>();
    private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "");

    private CheckoutSaveData checkoutSaveData;
    private int type, selectedSlot = -1;
    private BecomeStarAdapter becomeStarAdapter;
    private Button btnAddStar;
    private String selectedSubId;
    private SwipeButton mSwipeButton;
    private float xDown = 0f;
    private DisplayMetrics displayMetrics;
    private RelativeLayout.LayoutParams paramsF;
    private SubscriptionData.Subscription selectedSubscription = null;

    public ArrayList<SubItem> subItemsInCart;
    private ArrayList<Tax> chargesList, chargesListForFatafat;
    private Tax taxSubTotal, taxTotal;
    private ArrayList<Item> itemsInCart = new ArrayList<>();
    private MenusCartItemsAdapter menusCartItemsAdapter;
    private double totalTaxAmount = 0d;
    private LinearLayout llDeliveryFrom;
    private RelativeLayout rlDeliveryFrom;
    private TextView tvRestName, tvRestAddress;
    private boolean couponManuallySelected;
    private boolean isRazorUPI;
    private EditText edtIciciVpa;
    private String jugnooVpaHandle;
    private TextView tvUPICashback;
    private String currencyCode = "", currency = "";

    private LinearLayout linearLayoutDeliveryVehicles;
    private TextView textViewChooseVehicle;
    private CardView cvVehicles;
    private RecyclerView rvVehicles;
    private SearchResult pickUpAddress;
    private int currentVehicleTypePos = -1;
    private VehicleTypeAdapterMenus vehicleTypeAdapterMenus;
    private List<UserCheckoutResponse.VehiclesList> vehicleInfoList;
    private int vehicleType ;
    private boolean isPickUpSet = false;
    private int checkCount = 0;




    @Override
    public void onStart() {
        LocalBroadcastManager.getInstance(activity).registerReceiver(broadcastReceiverWalletUpdate,
                new IntentFilter(Constants.INTENT_ACTION_WALLET_UPDATE));
        mBus.register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        if (!orderPlaced) {
            activity.saveCheckoutData(false);
        }
        super.onStop();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_checkout_merged, container, false);
        noSelectionCoupon = new CouponInfo(-1, getString(R.string.dont_apply_coupon_on_this_ride));

        cartChangedRefreshCheckout = false;
        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);
        activity.setCartChangedAtCheckout(false);
        type = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);

        GAUtils.trackScreenView(activity.getGaCategory() + CHECKOUT);
        GAUtils.trackScreenView(activity.getGaCategory() + CHECKOUT + V2);
        labelOrMinOrder = (TextView)rootView.findViewById(R.id.labelOrMinOrder);
        labelOrMinOrder.setTypeface(labelOrMinOrder.getTypeface(),Typeface.BOLD);
        linearLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.linearLayoutRoot);
        tvMinOrderLabelDisplay=(TextView)rootView.findViewById(R.id.tv_min_order_label);
        layoutMinOrder =(LinearLayout)rootView.findViewById(R.id.layout_min_order);
        tvOrderViaFatafat =(TextView)rootView.findViewById(R.id.tv_order_via_fatafaat);
        tvOrderViaFatafat.setTypeface(tvOrderViaFatafat.getTypeface(),Typeface.BOLD);
        tvOrderViaFatafat.setText(getString(R.string.action_order_via_fatafat, getString(R.string.fatafat_text)));
        tvOrderViaFatafat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GAUtils.event(GACategory.FATAFAT3, GAAction.MIN_ORDER, GAAction.LABEL_ORDER_VIA_FATAFAT);

                    orderViaFatafat(activity, itemsInCart,subItemsInCart,activity,subTotalAmount, currencyCode, currency);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            if (linearLayoutRoot != null) {
                new ASSL(activity, linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        deliveryAddressUpdated = false;


        mBus = (activity).getBus();
        orderPlaced = false;
        checkoutSaveData = new CheckoutSaveData();


        if (isMenusOrDeliveryOpen()) {
            try {
                if (itemsInCart == null) {
                    itemsInCart = new ArrayList<>();
                }
                itemsInCart.clear();
                vehicleTypeAdapterMenus = null;
                vehicleType = 2217;
                currentVehicleTypePos = -1;
                vehicleInfoList = null;
                isPickUpSet = false;
                checkCount = 0;
                setCurrentSelectedAddressToMenus();
                itemsInCart = prepareItemsInCartForMenus(activity,itemsInCart);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            subItemsInCart = activity.fetchCartList();
            try {
                if (type == AppConstant.ApplicationType.MEALS
                        && activity.getProductsResponse() != null
                        && activity.getProductsResponse().getCategories() != null) {
                    currentGroupId = activity.getProductsResponse().getCategories().get(0).getCurrentGroupId();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }



        }


        try {
            if (Data.getDatumToReOrder() != null) {
//				activity.setSelectedAddress(Data.getDatumToReOrder().getDeliveryAddress());
//				activity.setSelectedLatLng(new LatLng(Data.getDatumToReOrder().getDeliveryLatitude(), Data.getDatumToReOrder().getDeliveryLongitude()));
                ArrayList<SearchResult> searchResults = new HomeUtil().getSavedPlacesWithHomeWork(activity);
                for (SearchResult searchResult : searchResults) {
                    if (Data.getDatumToReOrder().getAddressId().equals(searchResult.getId())) {
                        activity.setSelectedAddressId(Data.getDatumToReOrder().getAddressId());
                        activity.setSelectedAddressType(Data.getDatumToReOrder().getDeliveryAddressType());
                        break;
                    }
                }
                activity.setRefreshCart(true);
                deliveryAddressUpdated = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Data.setDatumToReOrder(null);

        activity.setMenuRefreshLatLng(new LatLng(activity.getSelectedLatLng().latitude, activity.getSelectedLatLng().longitude));

        ((TextView) rootView.findViewById(R.id.textViewDeliverySlot)).setTypeface(Fonts.mavenMedium(activity));
        ((TextView) rootView.findViewById(R.id.textViewDeliveryAddress)).setTypeface(Fonts.mavenMedium(activity));
        ((TextView) rootView.findViewById(R.id.textViewPaymentVia)).setTypeface(Fonts.mavenMedium(activity));
        ((TextView) rootView.findViewById(R.id.textViewOffers)).setTypeface(Fonts.mavenMedium(activity));

        relativeLayoutCartTop = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCartTop);
        textViewCartItems = (TextView) rootView.findViewById(R.id.textViewCartItems);
        textViewCartItems.setTypeface(Fonts.mavenMedium(activity));
        textViewCartTotalUndiscount = (TextView) rootView.findViewById(R.id.textViewCartTotalUndiscount);
        textViewCartTotalUndiscount.setTypeface(Fonts.mavenMedium(activity));
        imageViewCartArrow = (ImageView) rootView.findViewById(R.id.imageViewCartArrow);
        imageViewDeleteCart = (ImageView) rootView.findViewById(R.id.imageViewDeleteCart);
        imageViewCartSep = (ImageView) rootView.findViewById(R.id.imageViewCartSep);
        linearLayoutCartExpansion = (LinearLayout) rootView.findViewById(R.id.linearLayoutCartExpansion);
        tvBecomeStar = (TextView) rootView.findViewById(R.id.tvBecomeStar);
        tvBecomeStar.setTypeface(Fonts.mavenMedium(activity));
        tvBecomeStar.setText(getString(R.string.become_a_jugnoo_star, getString(R.string.app_name)));
        tvStarOffer = (TextView) rootView.findViewById(R.id.tvStarOffer);
        tvStarOffer.setTypeface(Fonts.mavenMedium(activity));
        listViewCart = (NonScrollListView) rootView.findViewById(R.id.listViewCart);


        listViewCharges = (NonScrollListView) rootView.findViewById(R.id.listViewCharges);
        chargesList = new ArrayList<>(); chargesListForFatafat = new ArrayList<>();
        taxSubTotal = new Tax(activity.getString(R.string.sub_total), activity.getTotalPrice());
        taxTotal = new Tax(activity.getString(R.string.total).toUpperCase(), activity.getTotalPrice());
        chargesList.add(taxSubTotal);
        chargesList.add(taxTotal);

        linearLayoutDeliverySlot = (LinearLayout) rootView.findViewById(R.id.linearLayoutDeliverySlot);
        recyclerViewDeliverySlots = (RecyclerView) rootView.findViewById(R.id.recyclerViewDeliverySlots);
        recyclerViewDeliverySlots.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDeliverySlots.setItemAnimator(new DefaultItemAnimator());
        recyclerViewDeliverySlots.setHasFixedSize(false);
        deliverySlotsAdapter = new DeliverySlotsAdapter(activity, slots, this);
        recyclerViewDeliverySlots.setAdapter(deliverySlotsAdapter);
        textViewNoDeliverySlot = (TextView) rootView.findViewById(R.id.textViewNoDeliverySlot);
        textViewNoDeliverySlot.setTypeface(Fonts.mavenMedium(activity));
        textViewNoDeliverySlot.setVisibility(View.GONE);

        if (isMenusOrDeliveryOpen()) {
            linearLayoutDeliverySlot.setVisibility(View.GONE);
        }

        textViewDeliveryInstructionsText = ((TextView) rootView.findViewById(R.id.textViewDeliveryInstructions));
        textViewDeliveryInstructionsText.setTypeface(Fonts.mavenMedium(activity));
        editTextDeliveryInstructions = (EditText) rootView.findViewById(R.id.editTextDeliveryInstructions);
        editTextDeliveryInstructions.setTypeface(Fonts.mavenRegular(activity));

        if (isMenusOrDeliveryOpen()) {
            textViewDeliveryInstructionsText.setText(R.string.delivery_instructions_for_menus);
            editTextDeliveryInstructions.setHint(R.string.add_special_notes_for_menus);
        } else {
            textViewDeliveryInstructionsText.setText(R.string.delivery_instructions);
            editTextDeliveryInstructions.setHint(R.string.add_special_notes);
        }


        relativeLayoutDeliveryAddress = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDeliveryAddress);
        imageViewAddressType = (ImageView) rootView.findViewById(R.id.imageViewAddressType);
        imageViewDeliveryAddressForward = (ImageView) rootView.findViewById(R.id.imageViewDeliveryAddressForward);
        textViewAddressName = (TextView) rootView.findViewById(R.id.textViewAddressName);
        textViewAddressName.setTypeface(Fonts.mavenMedium(activity));
        textViewAddressValue = (TextView) rootView.findViewById(R.id.textViewAddressValue);
        textViewAddressValue.setTypeface(Fonts.mavenRegular(activity));
        tvNoAddressAlert = (TextView) rootView.findViewById(R.id.tv_no_address_alert);

        llDeliveryFrom = (LinearLayout) rootView.findViewById(R.id.llDeliveryFrom);
        rlDeliveryFrom = (RelativeLayout) rootView.findViewById(R.id.rlDeliveryFrom);
        tvRestName = (TextView) rootView.findViewById(R.id.tvRestName);
        tvRestAddress = (TextView) rootView.findViewById(R.id.tvRestAddress);
        shadowMinOrder = (View)rootView.findViewById(R.id.shadow_top_min_order);

        linearLayoutWalletContainer = (LinearLayout) rootView.findViewById(R.id.linearLayoutWalletContainer);
        linearLayoutWalletContainer.setVisibility(View.GONE);
        relativeLayoutPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaytm);
        relativeLayoutStripeCard = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutStripeCard);
        relativeLayoutIcici = (RelativeLayout) rootView.findViewById(R.id.rlIciciUpi);
        relativeLayoutMobikwik = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutMobikwik);
        relativeLayoutFreeCharge = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutFreeCharge);
        relativeLayoutJugnooPay = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutJugnooPay);
        relativeLayoutCash = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCash);
        edtIciciVpa = (EditText) rootView.findViewById(R.id.edtIciciVpa);
        tvLabelIciciUpi = (TextView) rootView.findViewById(R.id.tv_label_below_edt_icici);
        imageViewPaytmRadio = (ImageView) rootView.findViewById(R.id.imageViewPaytmRadio);
        imageViewStripeRadio = (ImageView) rootView.findViewById(R.id.imageViewStripeRadio);
        imageViewAddStripeCard = (ImageView) rootView.findViewById(R.id.imageViewAddStripeCard);
        imageViewAddPaytm = (ImageView) rootView.findViewById(R.id.imageViewAddPaytm);
        imageViewRadioMobikwik = (ImageView) rootView.findViewById(R.id.imageViewRadioMobikwik);
        imageViewIcici = (ImageView) rootView.findViewById(R.id.ivRadioIciciUpi);
        imageViewAddMobikwik = (ImageView) rootView.findViewById(R.id.imageViewAddMobikwik);
        imageViewRadioFreeCharge = (ImageView) rootView.findViewById(R.id.imageViewRadioFreeCharge);
        imageViewAddFreeCharge = (ImageView) rootView.findViewById(R.id.imageViewAddFreeCharge);
        imageViewRadioJugnooPay = (ImageView) rootView.findViewById(R.id.imageViewRadioJugnooPay);
        imageViewAddJugnooPay = (ImageView) rootView.findViewById(R.id.imageViewAddJugnooPay);
        imageViewCashRadio = (ImageView) rootView.findViewById(R.id.imageViewCashRadio);
        textViewPaytmValue = (TextView) rootView.findViewById(R.id.textViewPaytmValue);
        tvStripeCardNumber = (TextView) rootView.findViewById(R.id.tvStripeCardNumber);
        ivStripeCardIcon = (ImageView) rootView.findViewById(R.id.ivStripeCardIcon);
        textViewPaytmValue.setTypeface(Fonts.mavenMedium(activity));
        tvStripeCardNumber.setTypeface(Fonts.mavenMedium(activity));
        textViewMobikwikValue = (TextView) rootView.findViewById(R.id.textViewMobikwikValue);
        textViewMobikwikValue.setTypeface(Fonts.mavenMedium(activity));
        textViewFreeChargeValue = (TextView) rootView.findViewById(R.id.textViewFreeChargeValue);
        textViewFreeChargeValue.setTypeface(Fonts.mavenMedium(activity));
        ((TextView) rootView.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenMedium(activity));
        ((TextView) rootView.findViewById(R.id.textViewJugnooPay)).setTypeface(Fonts.mavenMedium(activity));
        rlOtherModesToPay = (RelativeLayout) rootView.findViewById(R.id.rlOtherModesToPay);
        ivOtherModesToPay = (ImageView) rootView.findViewById(R.id.ivOtherModesToPay);
        tvOtherModesToPay = (TextView) rootView.findViewById(R.id.tvOtherModesToPay);
        rlUPI = (RelativeLayout) rootView.findViewById(R.id.rlUPI);
        ivUPI = (ImageView) rootView.findViewById(R.id.ivUPI);
        tvUPI = (TextView) rootView.findViewById(R.id.tvUPI);
        tvNoAvailableOffers = rootView.findViewById(R.id.tvNoAvailableOffers);
        tvNoAvailableOffers.setTypeface(Fonts.mavenMedium(activity));
        cvOffersAvailable = rootView.findViewById(R.id.cvOffersAvailable);
        linearLayoutOffers = (LinearLayout) rootView.findViewById(R.id.linearLayoutOffers);
        listViewOffers = (RecyclerView) rootView.findViewById(R.id.listViewOffers);
        listViewOffers.setNestedScrollingEnabled(false);
        listViewOffers.setLayoutManager(new LinearLayoutManager(activity));

        promoCouponsAdapter = new PromoCouponsRecyclerAdapter(activity, R.layout.list_item_fresh_promo_coupon, promoCoupons, this,listViewOffers);
        listViewOffers.setAdapter(promoCouponsAdapter);


        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);
        textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);

        cvStarSavings = (CardView) rootView.findViewById(R.id.cvStarSavings);
        cvStarSavings.setVisibility(View.GONE);
        ((TextView) rootView.findViewById(R.id.tvStarSavings)).setTypeface(Fonts.mavenMedium(activity));
        tvStarSavingsValue = (TextView) rootView.findViewById(R.id.tvStarSavingsValue);
        tvStarSavingsValue.setTypeface(Fonts.mavenMedium(activity));

        cvBecomeStar = (CardView) rootView.findViewById(R.id.cvBecomeStar);
        cvBecomeStar.setVisibility(View.GONE);
        spin = (MySpinner) rootView.findViewById(R.id.simpleSpinner);
        btnAddStar = (Button) rootView.findViewById(R.id.btnAddStar);

        linearLayoutDeliveryVehicles = (LinearLayout) rootView.findViewById(R.id.linearLayoutDeliveryVehicles);
        textViewChooseVehicle = (TextView) rootView.findViewById(R.id.textViewChooseVehicle);
        cvVehicles = (CardView) rootView.findViewById(R.id.cvVehicles);
        rvVehicles = (RecyclerView) rootView.findViewById(R.id.rvVehicles);
        rvVehicles.setVisibility(View.GONE);
        rvVehicles.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(activity,
                LinearLayoutManager.HORIZONTAL, false));
        rvVehicles.setItemAnimator(new DefaultItemAnimator());



        try {
            activity.sliderText.setText(getString(R.string.swipe_to_confirm));
        } catch (Exception e) {
        }

        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        paramsF = (RelativeLayout.LayoutParams) activity.tvSlide.getLayoutParams();

        tvUPICashback = (TextView) rootView.findViewById(R.id.tvUPICashback);
        tvUPICashback.setTypeface(tvUPICashback.getTypeface(), Typeface.ITALIC);


        btnAddStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSubscription = new Gson().fromJson(selectedSubId, SubscriptionData.Subscription.class);
                getCheckoutDataAPI(selectedSubscription, false);
                GAUtils.event(activity.getGaCategory(), CHECKOUT + SUBSCRIPTION + ADDED, selectedSubscription.getPlanString());
            }
        });

        activity.rlSliderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rlDeliveryFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof FreshActivity && isMenusOrDeliveryOpen())
                    GAUtils.event(activity.getGaCategory(), GAAction.CHECKOUT, GAAction.DELIVERY_FROM + GAAction.CLICKED);
                if(activity.getVendorMenuFragment() == null && activity.getMerchantInfoFragment() == null) {
                    activity.openVendorMenuFragmentOnBack = true;
                }
                activity.performBackPressed(false);
            }
        });
        rlDeliveryFrom.setMinimumHeight((int) (ASSL.Yscale() * 116f));
        relativeLayoutCash.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutPaytm.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutStripeCard.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutMobikwik.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutFreeCharge.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutJugnooPay.setOnClickListener(onClickListenerPaymentOptionSelector);
        rlUPI.setOnClickListener(onClickListenerPaymentOptionSelector);
        rlOtherModesToPay.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutIcici.setOnClickListener(onClickListenerPaymentOptionSelector);

        activity.setSelectedPromoCoupon(noSelectionCoupon);


        relativeLayoutDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getTransactionUtils().openDeliveryAddressFragment(activity, activity.getRelativeLayoutContainer(), false);
            }
        });

        activity.buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((isMenusOrDeliveryOpen() && getSubTotalAmount(false) < activity.getVendorOpened().getMinimumOrderAmount())) {
                    Utils.showToast(activity, getResources().getString(R.string.minimum_order_amount_is_format,
                            Utils.getMoneyDecimalFormatWithoutFloat().format(activity.getVendorOpened().getMinimumOrderAmount())));
                    setSlideInitial();
                } else if (activity.buttonPlaceOrder.getText().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.connection_lost_try_again))) {
                    getCheckoutDataAPI(selectedSubscription, false);
                } else if (!isMenusOrDeliveryOpen() && activity.getSlotSelected() == null) {
                    product.clicklabs.jugnoo.utils.Utils.showToast(activity, activity.getResources().getString(R.string.please_select_a_delivery_slot));
                    setSlideInitial();
                } else if (addressSelectedNotValid() || TextUtils.isEmpty(activity.getSelectedAddress())) {
                    product.clicklabs.jugnoo.utils.Utils.showToast(activity, activity.getResources().getString(R.string.please_select_a_delivery_address));
                    setSlideInitial();
                } else if (MyApplication.getInstance().getWalletCore().displayAlertAndCheckForSelectedWalletCoupon(activity,
                        activity.getPaymentOption().getOrdinal(), activity.getSelectedPromoCoupon())) {
                    activity.setSplInstr(editTextDeliveryInstructions.getText().toString().trim());
                    placeOrder();
                    placeOrderAnalytics();
                } else {
                    setSlideInitial();
                }
            }
        });

        imageViewDeleteCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.deleteCart();
            }
        });

        relativeLayoutCartTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listViewCart.getVisibility() == View.VISIBLE) {
//                    linearLayoutCartExpansion.setVisibility(View.GONE);
                    imageViewCartSep.setVisibility(View.GONE);
                    listViewCart.setVisibility(View.GONE);
                    imageViewDeleteCart.setVisibility(View.GONE);
                    imageViewCartArrow.setRotation(180f);
                } else {
//                    linearLayoutCartExpansion.setVisibility(View.VISIBLE);
                    imageViewCartSep.setVisibility(View.VISIBLE);
                    listViewCart.setVisibility(View.VISIBLE);
                    imageViewDeleteCart.setVisibility(View.GONE);
                    imageViewCartArrow.setRotation(0f);
                }
            }
        });

        editTextDeliveryInstructions.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.setSplInstr("" + s);
            }
        });

        checkoutSaveData = activity.getCheckoutSaveData();
        activity.setSplInstr(checkoutSaveData.getSpecialInstructions());


        linearLayoutCartExpansion.setVisibility(View.VISIBLE);
        imageViewDeleteCart.setVisibility(View.GONE);

        checkoutApiDoneOnce = false;
        activity.showPaySliderEnabled(true);
        activity.tvSlide.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xDown = event.getRawX();
                        GAUtils.event(activity.getGaCategory(), CHECKOUT, PAY_SLIDER + STARTED);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if ((event.getRawX() - getRelativeSliderLeftMargin()) > (activity.tvSlide.getWidth() / 2)
                                && (event.getRawX() - getRelativeSliderLeftMargin()) < activity.relativeLayoutSlider.getWidth() - (activity.tvSlide.getWidth() / 2)) {
                            paramsF.leftMargin = (int) layoutX(event.getRawX() - getRelativeSliderLeftMargin());
                            paramsF.setMarginStart((int) layoutX(event.getRawX() - getRelativeSliderLeftMargin()));
                            activity.relativeLayoutSlider.updateViewLayout(activity.tvSlide, paramsF);
                            activity.sliderText.setVisibility(View.VISIBLE);
                            float percent = (event.getRawX() - getRelativeSliderLeftMargin()) / (activity.relativeLayoutSlider.getWidth() - activity.tvSlide.getWidth());
                            activity.viewAlpha.setAlpha(percent);
                            if (percent > 0.6f) {
                                activity.sliderText.setVisibility(View.GONE);
                            } else {
                                activity.sliderText.setVisibility(View.VISIBLE);
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if ((event.getRawX() - getRelativeSliderLeftMargin()) < (activity.relativeLayoutSlider.getWidth() - (activity.tvSlide.getWidth() / 2)) * 0.6f) {
                            setSlideInitial();
                        } else {
                            animateSliderButton(paramsF.getMarginStart(), activity.relativeLayoutSlider.getWidth() - activity.tvSlide.getWidth());
                            setSliderCompleteState();
                            activity.buttonPlaceOrder.performClick();
                            GAUtils.event(activity.getGaCategory(), CHECKOUT, PAY_SLIDER + ENDED);
                        }
                        break;
                }

                return true;
            }


        });

        try {
            if (Data.userData.getSlideCheckoutPayEnabled() == 1) {
                activity.rlSliderContainer.setVisibility(View.VISIBLE);
                activity.buttonPlaceOrder.setVisibility(View.GONE);
            } else {
                activity.buttonPlaceOrder.setVisibility(View.VISIBLE);
                activity.rlSliderContainer.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setSlideInitial();
        toggleChatAvailability(false);
        return rootView;
    }


    private void fetchDrivers() {
        HashMap<String, String> params = new HashMap<>();

        if (pickUpAddress != null) {
            params.put(Constants.KEY_LATITUDE, String.valueOf(pickUpAddress.getLatitude()));
            params.put(Constants.KEY_LONGITUDE, String.valueOf(pickUpAddress.getLongitude()));
        } else {
            params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
            params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));
        }

        new HomeUtil().putDefaultParams(params);

        new ApiCommon<NearbyDriversResponse>(activity).showLoader(false).execute(params, ApiName.NEARBY_AGENTS_MENUS, new APICommonCallback<NearbyDriversResponse>() {
            @Override
            public void onSuccess(final NearbyDriversResponse dynamicDeliveryResponse, final String message, final int flag) {

            }

            @Override
            public boolean onFailure(final RetrofitError error) {
                return true;
            }

            @Override
            public boolean onError(final NearbyDriversResponse dynamicDeliveryResponse, final String message, final int flag) {
                return true;
            }
        });
    }

    public void handleVehiclesList(List<UserCheckoutResponse.VehiclesList> vehiclesListFromResponse) {

        vehicleInfoList = vehiclesListFromResponse;
        for (int i = 0; i < vehiclesListFromResponse.size(); i++) {
            if (vehiclesListFromResponse.get(i).getIsSelected() != null && vehiclesListFromResponse.get(i).getIsSelected() == 1) {
                currentVehicleTypePos = i;
                break;
            }
            else {
                currentVehicleTypePos = 0;
            }
        }
        vehicleType = vehicleInfoList.get(currentVehicleTypePos).getType();
        if(vehicleInfoList != null && !vehicleInfoList.isEmpty()){
            currentVehicleTypePos = 0;
            vehicleType = vehicleInfoList.get(currentVehicleTypePos).getType();
            if (vehicleTypeAdapterMenus == null) {

                vehicleTypeAdapterMenus = new VehicleTypeAdapterMenus((FreshActivity) activity, vehicleInfoList, currentVehicleTypePos, new VehicleTypeAdapterMenus.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(UserCheckoutResponse.VehiclesList item, int pos) {
                        currentVehicleTypePos = pos;
                        vehicleType = vehicleInfoList.get(currentVehicleTypePos).getType();
                        getCheckoutDataAPI(selectedSubscription, false);
                    }
                });
            }
            if(isPickUpSet) {
                if(checkCount == 0) {
                    linearLayoutDeliveryVehicles.setVisibility(View.VISIBLE);
                    rvVehicles.setVisibility(View.VISIBLE);
                    rvVehicles.setAdapter(vehicleTypeAdapterMenus);
                    checkCount++;
                }
                else {
                    rvVehicles.setVisibility(View.VISIBLE);
                    vehicleTypeAdapterMenus.updateList(vehicleInfoList);
                }
            }

            if(vehicleInfoList.size()>1 && getResources().getBoolean(R.bool.vehicle_list_menus)) {
                linearLayoutDeliveryVehicles.setVisibility(View.VISIBLE);
                rvVehicles.setVisibility(View.VISIBLE);
                textViewChooseVehicle.setVisibility(View.VISIBLE);
            }
            else {
                linearLayoutDeliveryVehicles.setVisibility(View.GONE);
                rvVehicles.setVisibility(GONE);
                textViewChooseVehicle.setVisibility(View.GONE);
            }

        }
        else {
            textViewChooseVehicle.setVisibility(View.GONE);
            linearLayoutDeliveryVehicles.setVisibility(View.GONE);
            rvVehicles.setVisibility(View.GONE);
        }

        rvVehicles.post(new Runnable() {
            @Override
            public void run() {
                rvVehicles.smoothScrollToPosition(currentVehicleTypePos);
            }
        });
    }

    private void setSliderCompleteState() {
        activity.relativeLayoutSlider.setBackgroundResource(R.drawable.capsule_slider_confirm_color_bg);
        activity.rlSliderContainer.setBackgroundResource(R.color.slider_green);
        activity.sliderText.setVisibility(View.GONE);
        activity.viewAlpha.setAlpha(1.0f);
    }
    private void toggleChatAvailability(boolean isChatAvailable) {
        if(activity.isDeliveryOpenInBackground() && isChatAvailable && activity.getAppType()== AppConstant.ApplicationType.DELIVERY_CUSTOMER){
            tvOrderViaFatafat.setVisibility(View.VISIBLE);
            (rootView.findViewById(R.id.labelOrMinOrder)).setVisibility(View.VISIBLE);
        }else{
            tvOrderViaFatafat.setVisibility(View.GONE);
            (rootView.findViewById(R.id.labelOrMinOrder)).setVisibility(View.GONE);
        }
    }

    public static  ArrayList<Item> prepareItemsInCartForMenus(FreshActivity activity,ArrayList<Item> items) {
        if(items==null){
            items = new ArrayList<>();
        }

        if (activity.getMenuProductsResponse().getCategories() != null) {
            for (Category category : activity.getMenuProductsResponse().getCategories()) {
                if (category.getSubcategories() != null) {
                    for (Subcategory subcategory : category.getSubcategories()) {
                        for (Item item : subcategory.getItems()) {
                            if (item.getTotalQuantity() > 0) {
                                items.add(item);
                            }
                        }
                    }
                } else if (category.getItems() != null) {
                    for (Item item : category.getItems()) {
                        if (item.getTotalQuantity() > 0) {
                            items.add(item);
                        }
                    }
                }
            }
        }
        return items;
    }

    TextWatcher selectIciciPaymentTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(activity.getPaymentOption()==null || activity.getPaymentOption()!= PaymentOption.ICICI_UPI)
                callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.ICICI_UPI);
        }
    };


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateAddressView();
                updateDeliveryFromView("");
                updateCartDataView();
                fetchWalletBalance();
                linearLayoutRoot.setPaddingRelative(0, 0, 0, activity.llPayViewContainer.getMeasuredHeight());
            }
        }, 50);
    }

    private void setSlideInitial() {
        animateSliderButton(paramsF.getMarginStart(), 0);
        activity.rlSliderContainer.setBackgroundResource(R.drawable.bg_rectangle_gradient_normal);
        activity.relativeLayoutSlider.setBackgroundResource(R.drawable.capsule_slider_color_bg);
        activity.sliderText.setVisibility(View.VISIBLE);

        activity.viewAlpha.setAlpha(activity.viewAlpha.getTag()!=null && activity.viewAlpha.getTag().equals("Disabled")?1.0f:0.0f);
    }

    private float getRelativeSliderLeftMargin() {
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) activity.relativeLayoutSlider.getLayoutParams();
        return relativeParams.getMarginStart();
    }

    private long animDuration = 150;


    private void animateSliderButton(final int currMargin, final float newMargin) {
        float diff = newMargin - (float) currMargin;
        Animation translateAnim = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, diff,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0);
        translateAnim.setDuration(animDuration);
        translateAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnim.setFillAfter(false);
        translateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                activity.tvSlide.clearAnimation();
                paramsF.leftMargin = (int) newMargin;
                paramsF.setMarginStart((int) newMargin);
                activity.relativeLayoutSlider.updateViewLayout(activity.tvSlide, paramsF);
                activity.tvSlide.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        activity.tvSlide.clearAnimation();
        activity.tvSlide.setEnabled(false);
        activity.tvSlide.startAnimation(translateAnim);
    }

    private float layoutX(float rawX) {
        return rawX - sliderButtonWidth() / 2f;
    }

    private float sliderButtonWidth() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) activity.tvSlide.getLayoutParams();
        return (float) params.width;
    }

    double chargesNotConsideredForFatafat;
    private void updateCartUI() {
        editTextDeliveryInstructions.setText(activity.getSpecialInst());

        chargesList.clear();
        chargesList.add(taxSubTotal);

        chargesListForFatafat.clear();
        chargesListForFatafat.add(taxSubTotal);
        chargesNotConsideredForFatafat = 0;

        double promoAmount = getTotalPromoAmount();
        if (promoAmount > 0) {
            chargesNotConsideredForFatafat+=promoAmount;
            chargesList.add(new Tax(activity.getString(R.string.discount), promoAmount));
        }

        if(activity.getUserCheckoutResponse() != null && activity.getUserCheckoutResponse().getTaxes() != null){
            for(Tax tax : activity.getUserCheckoutResponse().getTaxes()){
                Tax taxForDisplay = new Tax(tax.getKey(), tax.getCalculatedValue(subTotalAmount, getTotalPromoAmount()));
                chargesList.add(taxForDisplay);
                if(!taxForDisplay.getKey().toLowerCase().contains("delivery charges")) {
                    chargesNotConsideredForFatafat -= taxForDisplay.getValue();
                    chargesListForFatafat.add(taxForDisplay);
                }
            }
        }
        if (isMenusOrDeliveryOpen()) {
            totalTaxAmount = 0d;
            for (Charges charges1 : activity.getMenuProductsResponse().getCharges()) {
                Tax tax = new Tax(charges1.getText(), getCalculatedCharges(charges1, activity.getMenuProductsResponse().getCharges()));
                if (tax.getValue() > 0 || charges1.getForceShow() == 1) {
                    chargesList.add(tax);
                    if(!tax.getKey().toLowerCase().contains("delivery charges")) {
                        chargesNotConsideredForFatafat -= tax.getValue();
                        chargesListForFatafat.add(tax);
                    }
                }
                totalTaxAmount = totalTaxAmount + tax.getValue();
            }
        } else {
            chargesNotConsideredForFatafat -=deliveryCharges();
            chargesList.add(new Tax(activity.getString(R.string.delivery_charges), deliveryCharges()));
        }

        if (totalAmount() > 0 && jcUsed() > 0) {
            chargesList.add(new Tax(activity.getString(R.string.jugnoo_cash), jcUsed()));
        }



        if (isFreshOpen()) {
            double totalSavings = getTotalSavings();
            if (totalSavings > 0) {
                chargesList.add(new Tax(activity.getString(R.string.total_savings), totalSavings));
            }
        }

        taxTotal.setValue((double) Math.round(payableAmount()));
        chargesList.add(taxTotal);
        chargesListForFatafat.add(taxTotal);
        if (chargesAdapter != null) {
            chargesAdapter.notifyDataSetChanged();
        }
        if (dialogOrderComplete == null || dialogOrderComplete.getDialog()==null ||!dialogOrderComplete.getDialog().isShowing()) {
            if (payableAmount() > 0) {
//                Utils.getDoubleTwoDigits((double)Math.round(payableAmount()));

                activity.buttonPlaceOrder.setText(getString(R.string.pay_format, Utils.formatCurrencyAmount(payableAmount(), activity.getUserCheckoutResponse().getCurrencyCode(), activity.getUserCheckoutResponse().getCurrency())));
                activity.tvSlide.setText(getString(R.string.pay_format, Utils.formatCurrencyAmount(payableAmount(), activity.getUserCheckoutResponse().getCurrencyCode(), activity.getUserCheckoutResponse().getCurrency())));

            } else {
                activity.buttonPlaceOrder.setText(activity.getResources().getString(R.string.place_order));
                activity.tvSlide.setText(activity.getResources().getString(R.string.place_order));
            }
        }

        updateStarLayout();

    }

    private void updateStarLayout() {
        if ((type == AppConstant.ApplicationType.MEALS || isMenusOrDeliveryOpen())
                && Data.userData != null && Data.userData.isSubscriptionActive()
                && activity.getUserCheckoutResponse() != null
                && activity.getUserCheckoutResponse().getSubscription() != null) {
            double totalUndiscounted = isMenusOrDeliveryOpen() ? getSubTotalAmount(false) : totalUndiscounted();
            double cashbackValue = activity.getUserCheckoutResponse().getSubscription().getCashback(totalUndiscounted);
            cashbackValue = isMenusOrDeliveryOpen() ? Math.round(cashbackValue) : Math.round(totalUndiscounted - Math.round(totalUndiscounted - cashbackValue));
            if (cashbackValue > 0d) {
                cvStarSavings.setVisibility(View.VISIBLE);
                String cashbackText = TextUtils.isEmpty(activity.getUserCheckoutResponse().getSubscription().getCashbackText())
                        ?
                        activity.getString(R.string.you_will_receive_cashback_on_order, Utils.getMoneyDecimalFormatWithoutFloat().format(cashbackValue))
                        :
                        activity.getUserCheckoutResponse().getSubscription().getCashbackText()
                                .replace("{{{cashback_value}}}", activity.getString(R.string.rupees_value_format,
                                        Utils.getMoneyDecimalFormatWithoutFloat().format(cashbackValue)));
                tvStarSavingsValue.setText(cashbackText);
            } else {
                cvStarSavings.setVisibility(View.GONE);
            }
        } else {
            cvStarSavings.setVisibility(View.GONE);
        }
    }


    private View.OnClickListener onClickListenerPaymentOptionSelector = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isRazorUPI = false;
            try {
                switch (v.getId()) {
                    case R.id.relativeLayoutPaytm:
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.PAYTM,
                                callbackPaymentOptionSelector);
                        break;

                    case R.id.relativeLayoutMobikwik:
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.MOBIKWIK,
                                callbackPaymentOptionSelector);
                        break;

                    case R.id.relativeLayoutFreeCharge:
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.FREECHARGE,
                                callbackPaymentOptionSelector);
                        break;

                    case R.id.relativeLayoutJugnooPay:
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.JUGNOO_PAY,
                                callbackPaymentOptionSelector);
                        break;

                    case R.id.relativeLayoutCash:
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.CASH,
                                callbackPaymentOptionSelector);
                        break;

                    case R.id.rlUPI:
                        isRazorUPI = true;
                        callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.RAZOR_PAY);

                    case R.id.rlOtherModesToPay:
                        callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.RAZOR_PAY);
                        break;
                    case R.id.rlIciciUpi:
                        callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.ICICI_UPI);
                        break;
                    case R.id.relativeLayoutStripeCard:
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.STRIPE_CARDS,
                                callbackPaymentOptionSelector);
//                        callbackPaymentOptionSelector.onPaymentOptionSelected(PaymentOption.STRIPE_CARDS);
                        break;
                }
                GAUtils.event(activity.getGaCategory(), CHECKOUT + WALLET + MODIFIED, String.valueOf(activity.getPaymentOption()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private CallbackPaymentOptionSelector callbackPaymentOptionSelector = new CallbackPaymentOptionSelector() {
        @Override
        public void onPaymentOptionSelected(PaymentOption paymentOption) {
            activity.setPaymentOption(paymentOption);
            setPaymentOptionUI();
        }

        @Override
        public void onWalletAdd(PaymentOption paymentOption) {
            activity.setPaymentOption(paymentOption);
        }

        @Override
        public String getAmountToPrefill() {
            return dfNoDecimal.format(Math.ceil(payableAmount()));
        }

        @Override
        public void onWalletOptionClick() {

        }

        @Override
        public int getSelectedPaymentOption() {
            return activity.getPaymentOption().getOrdinal();
        }

        @Override
        public void setSelectedPaymentOption(int paymentOption) {
            activity.setPaymentOption(MyApplication.getInstance().getWalletCore().getPaymentOptionFromInt(paymentOption));
        }

        @Override
        public boolean isRazorpayEnabled() {
            return true;
        }
    };




    private void setSubscriptionView() {
        if (Data.userData.getShowSubscriptionData() == 1 && !Data.userData.isSubscriptionActive() && activity.getUserCheckoutResponse().getShowStarSubscriptions() == 1) {
            if (activity.getUserCheckoutResponse().getStarSubscriptionText() != null && !activity.getUserCheckoutResponse().getStarSubscriptionText().equalsIgnoreCase("")) {
                tvStarOffer.setText(activity.getUserCheckoutResponse().getStarSubscriptionText());
            } else {
                tvStarOffer.setText(activity.getString(R.string.become_a_jugnoo_star, activity.getString(R.string.app_name)));
            }

            if (activity.getUserCheckoutResponse().getStarSubscriptionTitle() != null && !activity.getUserCheckoutResponse().getStarSubscriptionTitle().equalsIgnoreCase("")) {
                tvBecomeStar.setText(activity.getUserCheckoutResponse().getStarSubscriptionTitle());
            } else {
                tvBecomeStar.setText(activity.getResources().getString(R.string.add_to_avail_unlimited_free_deliveries));
            }

            cvBecomeStar.setVisibility(View.VISIBLE);
            becomeStarAdapter = new BecomeStarAdapter(getActivity(), Data.userData.getSubscriptionData().getSubscriptions());
            spin.setAdapter(becomeStarAdapter);
            selectedSubId = new Gson().toJson(Data.userData.getSubscriptionData().getSubscriptions().get(0));

            spin.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SubscriptionData.Subscription sd = Data.userData.getSubscriptionData().getSubscriptions().get(position);
                    selectedSubId = new Gson().toJson(sd);
                    GAUtils.event(activity.getGaCategory(), CHECKOUT + SUBSCRIPTION + MODIFIED, sd.getPlanString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            cvBecomeStar.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver broadcastReceiverWalletUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            activity.setPaymentOption(getSavedPaymentOption());
            onResume();
        }
    };


    private ApiFetchWalletBalance apiFetchWalletBalance = null;

    private void fetchWalletBalance() {
        try {
            if (apiFetchWalletBalance == null) {
                apiFetchWalletBalance = new ApiFetchWalletBalance(activity, new ApiFetchWalletBalance.Callback() {
                    @Override
                    public void onSuccess() {
                        try {
                            activity.setPaymentOption(getSavedPaymentOption());
                            orderPaymentModes();
                            setPaymentOptionUI();
                            activity.updateMenu();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure() {
                        try {
                            activity.setPaymentOption(getSavedPaymentOption());
                            orderPaymentModes();
                            setPaymentOptionUI();
                            activity.updateMenu();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onRetry(View view) {
                    }

                    @Override
                    public void onNoRetry(View view) {
                    }
                });
            }
            apiFetchWalletBalance.getBalance(true, false, activity.getSelectedLatLng());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setPaymentOptionUI() {
        try {
            activity.setPaymentOption(MyApplication.getInstance().getWalletCore().getPaymentOptionFromInt(
                    MyApplication.getInstance().getWalletCore().getPaymentOptionAccAvailability(activity.getPaymentOption().getOrdinal())));

            try {
                if (isMenusOrDeliveryOpen()) {
                    if (activity.getVendorOpened().getApplicablePaymentMode() == ApplicablePaymentMode.CASH.getOrdinal()) {
                        activity.setPaymentOption(PaymentOption.CASH);
                    } else if (activity.getVendorOpened().getApplicablePaymentMode() == ApplicablePaymentMode.ONLINE.getOrdinal()
                            && activity.getPaymentOption() == PaymentOption.CASH) {
                        activity.setPaymentOption(PaymentOption.PAYTM);
                    }
                } else {
                    if (getPaymentInfoMode() == ApplicablePaymentMode.CASH.getOrdinal()) {
                        activity.setPaymentOption(PaymentOption.CASH);
                    } else if (getPaymentInfoMode() == ApplicablePaymentMode.ONLINE.getOrdinal()
                            && activity.getPaymentOption() == PaymentOption.CASH) {
                        activity.setPaymentOption(PaymentOption.PAYTM);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            if (promoCouponsAdapter != null && promoCoupons != null && promoCoupons.size() > 0) {
                if (selectAutoSelectedCouponAtCheckout()) {
                    promoCouponsAdapter.notifyDataSetChanged();
                }
            }

            textViewPaytmValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format), Data.userData.getPaytmBalanceStr()));
            textViewPaytmValue.setTextColor(Data.userData.getPaytmBalanceColor(activity));
            textViewMobikwikValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format), Data.userData.getMobikwikBalanceStr()));
            textViewMobikwikValue.setTextColor(Data.userData.getMobikwikBalanceColor(activity));
            textViewFreeChargeValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format), Data.userData.getFreeChargeBalanceStr()));
            textViewFreeChargeValue.setTextColor(Data.userData.getFreeChargeBalanceColor(activity));

            if (Data.userData.getPaytmEnabled() == 1) {
                textViewPaytmValue.setVisibility(View.VISIBLE);
                imageViewAddPaytm.setVisibility(View.GONE);
            } else {
                textViewPaytmValue.setVisibility(View.GONE);
                imageViewAddPaytm.setVisibility(View.VISIBLE);
            }

            if (Data.userData.getMobikwikEnabled() == 1) {
                textViewMobikwikValue.setVisibility(View.VISIBLE);
                imageViewAddMobikwik.setVisibility(View.GONE);
            } else {
                textViewMobikwikValue.setVisibility(View.GONE);
                imageViewAddMobikwik.setVisibility(View.VISIBLE);
            }
            if (Data.userData.getFreeChargeEnabled() == 1) {
                textViewFreeChargeValue.setVisibility(View.VISIBLE);
                imageViewAddFreeCharge.setVisibility(View.GONE);
            } else {
                textViewFreeChargeValue.setVisibility(View.GONE);
                imageViewAddFreeCharge.setVisibility(View.VISIBLE);
            }
            if (Data.getPayData() != null && Data.getPayData().getPay().getHasVpa() == 1) {
                imageViewAddJugnooPay.setVisibility(View.GONE);
            } else {
                imageViewAddJugnooPay.setVisibility(View.VISIBLE);
            }


            imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_normal);
            imageViewStripeRadio.setImageResource(R.drawable.ic_radio_button_normal);
            imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_normal);
            imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_normal);
            imageViewRadioJugnooPay.setImageResource(R.drawable.ic_radio_button_normal);
            imageViewCashRadio.setImageResource(R.drawable.ic_radio_button_normal);
            ivOtherModesToPay.setImageResource(R.drawable.ic_radio_button_normal);
            ivUPI.setImageResource(R.drawable.ic_radio_button_normal);
            imageViewIcici.setImageResource(R.drawable.ic_radio_button_normal);

            if (activity.getPaymentOption() == PaymentOption.PAYTM) {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_selected);
            } else if(activity.getPaymentOption()==PaymentOption.STRIPE_CARDS){
                imageViewStripeRadio.setImageResource(R.drawable.ic_radio_button_selected);

            }else if (activity.getPaymentOption() == PaymentOption.MOBIKWIK) {
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_selected);
            } else if (activity.getPaymentOption() == PaymentOption.FREECHARGE) {
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_selected);
            } else if (activity.getPaymentOption() == PaymentOption.JUGNOO_PAY) {
                imageViewRadioJugnooPay.setImageResource(R.drawable.ic_radio_button_selected);
            } else if (activity.getPaymentOption() == PaymentOption.RAZOR_PAY) {
                if (isRazorUPI) {
                    ivUPI.setImageResource(R.drawable.ic_radio_button_selected);
                } else {
                    ivOtherModesToPay.setImageResource(R.drawable.ic_radio_button_selected);
                }
            } else if (activity.getPaymentOption() == PaymentOption.UPI_RAZOR_PAY) {
                isRazorUPI = true;
                ivUPI.setImageResource(R.drawable.ic_radio_button_selected);
            } else if (activity.getPaymentOption() == PaymentOption.ICICI_UPI) {
                imageViewIcici.setImageResource(R.drawable.ic_radio_button_selected);

            } else {
                imageViewCashRadio.setImageResource(R.drawable.ic_radio_button_selected);
            }
            edtIciciVpa.setVisibility(activity.getPaymentOption() == PaymentOption.ICICI_UPI?View.VISIBLE:View.GONE);
            tvLabelIciciUpi.setVisibility(activity.getPaymentOption() == PaymentOption.ICICI_UPI?View.VISIBLE:View.GONE);
            updateCartDataView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void placeOrder() {
        try {
            boolean goAhead = true;
            if (activity.getPaymentOption() == PaymentOption.PAYTM) {
                if (Data.userData.getPaytmBalance() < Math.round(payableAmount())) {
                    if (Data.userData.getPaytmEnabled() == 0) {
                        relativeLayoutPaytm.performClick();
                    } else if (Data.userData.getPaytmBalance() < 0) {
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.paytm_error_cash_select_cash));
                    } else {
                        showWalletBalanceLowDialog(PaymentOption.PAYTM);
                    }
                    goAhead = false;
                }
            } else if (activity.getPaymentOption() == PaymentOption.MOBIKWIK) {
                if (Data.userData.getMobikwikBalance() < Math.round(payableAmount())) {
                    if (Data.userData.getMobikwikEnabled() == 0) {
                        relativeLayoutMobikwik.performClick();
                    } else if (Data.userData.getMobikwikBalance() < 0) {
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.mobikwik_error_select_cash));
                    } else {
                        showWalletBalanceLowDialog(PaymentOption.MOBIKWIK);
                    }
                    goAhead = false;
                }
            } else if (activity.getPaymentOption() == PaymentOption.FREECHARGE) {
                if (Data.userData.getFreeChargeBalance() < Math.round(payableAmount())) {
                    if (Data.userData.getFreeChargeEnabled() == 0) {
                        relativeLayoutFreeCharge.performClick();
                    } else if (Data.userData.getFreeChargeBalance() < 0) {
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.freecharge_error_case_select_cash));
                    } else {
                        showWalletBalanceLowDialog(PaymentOption.FREECHARGE);
                    }
                    goAhead = false;
                }
            } else if (activity.getPaymentOption() == PaymentOption.JUGNOO_PAY) {
                if (Data.getPayData() == null || Data.getPayData().getPay().getHasVpa() != 1) {
                    relativeLayoutJugnooPay.performClick();
                    goAhead = false;
                }
            }
            if (goAhead) {
                if (activity.getPaymentOption() == PaymentOption.ICICI_UPI && TextUtils.isEmpty(edtIciciVpa.getText().toString().trim())) {
                    Utils.showToast(activity, activity.getString(R.string.error_enter_virtual_payment_address));
                    setSlideInitial();
                    return;
                }

                activity.buttonPlaceOrder.setEnabled(false);
                if (activity.rlSliderContainer.getVisibility() == View.VISIBLE) {
                    placeOrderConfirmation();

                } else {
                    DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
                            activity.getResources().getString(R.string.place_order_confirmation),
                            activity.getResources().getString(R.string.ok),
                            activity.getResources().getString(R.string.cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    placeOrderConfirmation();
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    activity.buttonPlaceOrder.setEnabled(true);
                                    setSlideInitial();
                                }
                            }, false, false);
                }
            } else {
                setSlideInitial();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void placeOrderConfirmation() {
        placeOrderApi();
    }

    private String cartItemsFM() {
        String idKey = Constants.KEY_SUB_ITEM_ID;
        JSONArray jCart = new JSONArray();
        for (SubItem subItem : subItemsInCart) {
            if (subItem.getSubItemQuantitySelected() > 0) {
                try {
                    JSONObject jItem = new JSONObject();
                    jItem.put(idKey, subItem.getSubItemId());
                    jItem.put(Constants.KEY_QUANTITY, subItem.getSubItemQuantitySelected());
                    jCart.put(jItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String categoryName = "", itemName = "";
                    double price = 0.0;
                    int qty = 0, itemId = 0;
                    qty = subItem.getSubItemQuantitySelected();
                    categoryName = subItem.getSubItemName();
                    itemId = subItem.getSubItemId();
                    itemName = subItem.getSubItemName();
                    price = subItem.getPrice();
                    Product product = new Product()
                            .setCategory(categoryName)
                            .setId("" + itemId)
                            .setName(itemName)
                            .setPrice(price)
                            .setQuantity(qty);
                    productList.add(product);
                    HashMap<String, Object> item = new HashMap<>();
                    item.put(PRODUCT_NAME, itemName);
                    item.put(PRODUCT_ID, itemId);
                    item.put(QUANTITY, qty);
                    items.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return jCart.toString();
    }

    private void iterateItems(List<Item> items, JSONArray jCart) {
        for (Item item : items) {
            if (item.getItemSelectedList().size() > 0) {
                for (ItemSelected itemSelected : item.getItemSelectedList()) {
                    try {
                        JSONObject jItem = new JSONObject();
                        jItem.put(Constants.KEY_ITEM_ID, itemSelected.getRestaurantItemId());
                        jItem.put(Constants.KEY_QUANTITY, itemSelected.getQuantity());
                        JSONArray jCustomisations = new JSONArray();
                        for (CustomizeItemSelected customizeItemSelected : itemSelected.getCustomizeItemSelectedList()) {
                            JSONObject jCustomisation = new JSONObject();
                            jCustomisation.put(Constants.KEY_ID, customizeItemSelected.getCustomizeId());
                            JSONArray jOptions = new JSONArray();
                            for (Integer option : customizeItemSelected.getCustomizeOptions()) {
                                JSONObject jOption = new JSONObject();
                                jOption.put(Constants.KEY_ID, option);
                                jOptions.put(jOption);
                            }
                            jCustomisation.put(Constants.KEY_OPTIONS, jOptions);
                            jCustomisations.put(jCustomisation);
                        }
                        if (jCustomisations.length() > 0) {
                            jItem.put(Constants.KEY_CUSTOMISATIONS, jCustomisations);
                        }
                        jCart.put(jItem);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String cartItemsMenus() {
        JSONArray jCart = new JSONArray();
        if (activity.getMenuProductsResponse() != null
                && activity.getMenuProductsResponse().getCategories() != null) {
            for (Category category : activity.getMenuProductsResponse().getCategories()) {
                if (category.getSubcategories() != null) {
                    for (Subcategory subcategory : category.getSubcategories()) {
                        iterateItems(subcategory.getItems(), jCart);
                    }
                } else if (category.getItems() != null) {
                    iterateItems(category.getItems(), jCart);
                }
            }
        }
        return jCart.toString();
    }





    HashMap<String, Object> chargeDetails = new HashMap<String, Object>();
    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

    public void placeOrderApi() {
        try {
            if (MyApplication.getInstance().isOnline()) {
                productList.clear();
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

                chargeDetails.clear();
                items.clear();

                chargeDetails.put("Payment mode", "" + activity.getPaymentOption());
                chargeDetails.put(TOTAL_AMOUNT, "" + getSubTotalAmount(false));
                chargeDetails.put(DISCOUNT_AMOUNT, "" + getTotalPromoAmount());
                if (!isMenusOrDeliveryOpen()) {
                    chargeDetails.put(START_TIME, "" + String.valueOf(activity.getSlotSelected().getStartTime()));
                    chargeDetails.put(END_TIME, "" + String.valueOf(activity.getSlotSelected().getEndTime()));
                }
                chargeDetails.put(CITY, Data.userData.getCity());

                final HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));

                params.put(Constants.KEY_MENU_LATITUDE, String.valueOf(activity.getMenuRefreshLatLng().latitude));
                params.put(Constants.KEY_MENU_LONGITUDE, String.valueOf(activity.getMenuRefreshLatLng().longitude));

                params.put(Constants.DELIVERY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                params.put(Constants.DELIVERY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
                if(vehicleInfoList != null && !vehicleInfoList.isEmpty() && currentVehicleTypePos != -1 && vehicleType != 2217) {
                    params.put(Constants.KEY_VEHICLE_TYPE, ""+vehicleType);
                }

                if (activity.getPaymentOption().getOrdinal() == PaymentOption.UPI_RAZOR_PAY.getOrdinal()) {
                    params.put(Constants.KEY_PAYMENT_MODE, String.valueOf(PaymentOption.RAZOR_PAY.getOrdinal()));
                } else {
                    params.put(Constants.KEY_PAYMENT_MODE, String.valueOf(activity.getPaymentOption().getOrdinal()));
                    if (activity.getPaymentOption().getOrdinal() == PaymentOption.ICICI_UPI.getOrdinal()) {
                        params.put(Constants.KEY_VPA, edtIciciVpa.getText().toString().trim());
                    }

                }
                if (!isMenusOrDeliveryOpen()) {
                    params.put(Constants.KEY_DELIVERY_SLOT_ID, String.valueOf(activity.getSlotSelected().getDeliverySlotId()));
                }
                params.put(Constants.KEY_DELIVERY_ADDRESS, String.valueOf(activity.getSelectedAddress()));
                if (activity.getSelectedAddressId() > 0) {
                    params.put(Constants.KEY_DELIVERY_ADDRESS_ID, String.valueOf(activity.getSelectedAddressId()));
                    params.put(Constants.KEY_DELIVERY_ADDRESS_TYPE, String.valueOf(activity.getSelectedAddressType()));
                }
                params.put(Constants.KEY_DELIVERY_NOTES, String.valueOf(activity.getSpecialInst()));
                params.put(Constants.KEY_CLIENT_ID, Config.getLastOpenedClientId(activity));
                if (isMenusOrDeliveryOpen()) {
                    params.put(Constants.KEY_CART, cartItemsMenus());
                } else {
                    params.put(Constants.KEY_CART, cartItemsFM());
                }
                if (activity.getSelectedPromoCoupon() != null && activity.getSelectedPromoCoupon().getId() > -1) {
                    if (activity.getSelectedPromoCoupon() instanceof CouponInfo) {
                        params.put(Constants.KEY_ACCOUNT_ID, String.valueOf(activity.getSelectedPromoCoupon().getId()));
                    } else if (activity.getSelectedPromoCoupon() instanceof PromotionInfo) {
                        params.put(Constants.KEY_ORDER_OFFER_ID, String.valueOf(activity.getSelectedPromoCoupon().getId()));
                    }
                    if(activity.getSelectedPromoCoupon().showPromoBox()){
                        params.put(Constants.KEY_REFFERAL_CODE,activity.getSelectedPromoCoupon().getPromoName());
                    }
                    params.put(Constants.KEY_MASTER_COUPON, String.valueOf(activity.getSelectedPromoCoupon().getMasterCoupon()));
                    GAUtils.event(activity.getGaCategory(), CHECKOUT + OFFER + SELECTED, activity.getSelectedPromoCoupon().getTitle());
                }
                try {
                    chargeDetails.put(COUPONS_USED, activity.getSelectedPromoCoupon().getTitle());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (type == AppConstant.ApplicationType.MEALS) {

                    params.put("is_early_bird_discount",activity.isShowingEarlyBirdDiscount()?"1":"0");
                    params.put("store_id", "2");
                    params.put("group_id", "" + activity.getProductsResponse().getCategories().get(0).getSubItems().get(0).getGroupId());
                    chargeDetails.put(TYPE, "Meals");
                } else if (type == AppConstant.ApplicationType.GROCERY) {
                    chargeDetails.put(TYPE, "Grocery");
                } else if (type == AppConstant.ApplicationType.MENUS) {
                    chargeDetails.put(TYPE, "Menus");
                } else if (type == AppConstant.ApplicationType.DELIVERY_CUSTOMER) {
                    chargeDetails.put(TYPE, "Delivery Customer");
                } else {
                    chargeDetails.put(TYPE, "Fresh");
                }
                params.put(Constants.INTERATED, "1");

                if (isMenusOrDeliveryOpen()) {
                    params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(activity.getVendorOpened().getRestaurantId()));
                }

                if (selectedSubscription != null) {
                    JSONObject jItem = new JSONObject();
                    jItem.put(Constants.KEY_SUBSCRIPTION_ID, selectedSubscription.getId());
                    params.put(Constants.KEY_SUBSCRIPTION_INFO, jItem.toString());
                }


                Log.i(TAG, "getAllProducts params=" + params.toString());
                if (activity.getSlotSelected() != null) {
                    GAUtils.event(activity.getGaCategory(), CHECKOUT + DELIVERY_SLOT + SELECTED, activity.getSlotSelected().getDayName() + " " + activity.getSlotSelected().getTimeSlotDisplay());
                }
                GAUtils.event(activity.getGaCategory(), CHECKOUT + WALLET + SELECTED, String.valueOf(activity.getPaymentOption()));
                if (!TextUtils.isEmpty(activity.getSpecialInst())) {
                    GAUtils.event(activity.getGaCategory(), CHECKOUT, NOTES + ADDED);
                }
                if (type == AppConstant.ApplicationType.FRESH) {
                    params.put(Constants.KEY_VENDOR_ID, String.valueOf(activity.getOpenedVendorId()));
                }

                //for menus or delivery reorder case

                if (isMenusOrDeliveryOpen()) {
                    //send order ID of old order if order has been reorder,this value is saved when cart is made from reorder and cleared everytime the cart is cleared
                    int cartReorderId = Prefs.with(activity).getInt(activity.getAppType()== AppConstant.ApplicationType.MENUS?Constants.CART_STATUS_REORDER_ID:Constants.CART_STATUS_REORDER_ID_CUSTOMER_DELIVERY,-1);
                    if(cartReorderId!=-1){
                        params.put(Constants.KEY_REODER_ID, String.valueOf(cartReorderId));

                    }
                }

                Callback<PlaceOrderResponse> callback = new Callback<PlaceOrderResponse>() {
                    @Override
                    public void success(PlaceOrderResponse placeOrderResponse, Response response) {
                        boolean doSlideInitial = true;
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
//						DialogPopup.dismissLoadingDialog();
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                final int flag = jObj.getInt(Constants.KEY_FLAG);
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    if (jObj.has(Constants.KEY_PAYMENT_OBJECT)) {
                                        activity.setPlaceOrderResponse(placeOrderResponse);
                                        final ProgressDialog progressDialog = DialogPopup.showLoadingDialogNewInstance(activity,
                                                activity.getString(R.string.loading));
                                        activity.getHandler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (progressDialog != null) {
                                                    progressDialog.dismiss();
                                                }
                                                activity.getPaySDKUtils().openSendMoneyPage(activity,
                                                        activity.getPlaceOrderResponse().getPaymentObject());
                                            }
                                        }, 3000);
                                    } else if (jObj.has(Constants.KEY_RAZORPAY_PAYMENT_OBJECT)) {
                                        // razor pay case send data to RazorPay Checkout page
                                        activity.setPlaceOrderResponse(placeOrderResponse);
                                        activity.startRazorPayPayment(jObj.getJSONObject(Constants.KEY_RAZORPAY_PAYMENT_OBJECT), isRazorUPI);
                                        doSlideInitial = false;
                                    } else {
                                        if(Integer.parseInt(placeOrderResponse.getPaymentMode())==PaymentOption.ICICI_UPI.getOrdinal() &&
                                                placeOrderResponse.getIcici()!=null ){
                                            //Icici Upi Payment Initiated

                                            activity.setPlaceOrderResponse(placeOrderResponse);
                                            onIciciUpiPaymentInitiated(placeOrderResponse.getIcici(),String.valueOf(placeOrderResponse.getAmount()));

                                        }else{
                                            paramsPlaceOrder = params;
                                            orderPlacedSuccess(placeOrderResponse);
                                            doSlideInitial = false;
                                        }
                                    }
                                }
                                else if(ApiResponseFlags.MEALS_PRICE_MISMATCH.getOrdinal() == flag){
                                    getCheckoutDataAPI(selectedSubscription, true);
/*
                                    DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });*/
                                }else if (ApiResponseFlags.USER_IN_DEBT.getOrdinal() == flag) {
                                    final String message1 = jObj.optString(Constants.KEY_MESSAGE, "");
                                    final double userDebt = jObj.optDouble(Constants.KEY_USER_DEBT, 0);
                                    Log.e("USER_IN_DEBT message", "=" + message1);
                                    new UserDebtDialog(activity, Data.userData,
                                            new UserDebtDialog.Callback() {
                                                @Override
                                                public void successFullyDeducted(double userDebt) {
                                                    activity.setPaymentOption(getSavedPaymentOption());
                                                    setPaymentOptionUI();
                                                    activity.updateMenu();
                                                }

                                            }).showUserDebtDialog(userDebt, message1);
                                } else if (ApiResponseFlags.INSUFFICIENT_BALANCE.getOrdinal() == flag) {
                                    DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            fetchWalletBalance();
                                        }
                                    });
                                } else if (ApiResponseFlags.INVALID_DELIVERY_SLOT.getOrdinal() == flag) {
                                    DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getCheckoutDataAPI(selectedSubscription, false);
                                        }
                                    });
                                } else {
                                    final int validStockCount = jObj.optInt(Constants.KEY_VALID_STOCK_COUNT, -1);
                                    if (validStockCount > -1) {
                                        DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    String fragName;
                                                    if (validStockCount == 0) {
                                                        if (type == AppConstant.ApplicationType.MEALS && activity.isMealAddonItemsAvailable()) {
                                                            fragName = MealAddonItemsFragment.class.getName();
                                                        } else {
                                                            fragName = FreshCheckoutMergedFragment.class.getName();
                                                        }
                                                    } else {
                                                        if (type == AppConstant.ApplicationType.MEALS && activity.isMealAddonItemsAvailable()) {
                                                            fragName = MealAddonItemsFragment.class.getName();
                                                        } else {
                                                            fragName = FreshCheckoutMergedFragment.class.getName();
                                                        }
                                                    }
                                                    activity.setRefreshCart(true);
                                                    activity.getSupportFragmentManager().popBackStack(fragName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    } else {
                                        final int redirect = jObj.optInt(Constants.KEY_REDIRECT, 0);
                                        final int isEmpty = jObj.optInt(Constants.KEY_IS_EMPTY, 0);
                                        final int emptyCart = jObj.optInt(Constants.KEY_EMPTY_CART, 0);
                                        final int outOfRange = jObj.optInt(Constants.KEY_OUT_OF_RANGE, 0);
                                        if (isMenusOrDeliveryOpen() && outOfRange == 1) {
                                            outOfRangeDialog(message);
                                        } else {
                                            DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (emptyCart == 1) {
                                                        clearMenusCartAndGoToMenusFragment(true);
                                                    } else if (isMenusOrDeliveryOpen()
                                                            && ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag
                                                            && isEmpty == 1) {
                                                        activity.clearMenusCart(activity.getAppType());
                                                        if(activity.getVendorMenuFragment() != null) {
                                                            activity.setRefreshCart(true);
                                                            activity.performBackPressed(false);
                                                        }
                                                        activity.setRefreshCart(true);
                                                        activity.performBackPressed(false);
                                                    } else if (redirect == 1) {
                                                        activity.setRefreshCart(true);
                                                        activity.performBackPressed(false);
                                                        activity.setRefreshCart(true);
                                                        activity.performBackPressed(false);
                                                        // activity.performBackPressed();
                                                    }/*activity.performBackPressed();*/
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(DialogErrorType.SERVER_ERROR, 1);
                        }
//                        123
                        DialogPopup.dismissLoadingDialog();
                        if (doSlideInitial) {
                            activity.getHandler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setSlideInitial();
                                }
                            }, 200);
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        setSlideInitial();
                        Log.e(TAG, "paytmAuthenticateRecharge error " + error.toString());
                        DialogPopup.dismissLoadingDialog();
//                        123
                        retryDialog(DialogErrorType.CONNECTION_LOST, 1);
                    }
                };

                new HomeUtil().putDefaultParams(params);
                if (isMenusOrDeliveryOpen()) {
                    RestClient.getMenusApiService().placeOrder(params, callback);
                } else {
                    RestClient.getFreshApiService().placeOrder(params, callback);
                }
            } else {
                setSlideInitial();
                retryDialog(DialogErrorType.NO_NET, 1);
            }
        } catch (Exception e) {
            setSlideInitial();
            e.printStackTrace();
        }
        activity.buttonPlaceOrder.setEnabled(true);
    }

    private void fbPurchasedEvent(HashMap<String, String> params, PlaceOrderResponse placeOrderResponse) {
        try {
            Bundle bundle = new Bundle();
            if (params != null) {
                for (String key : params.keySet()) {
                    bundle.putString(key, params.get(key));
                }
            }
            bundle.putString("order_id", String.valueOf(placeOrderResponse.getOrderId()));
            bundle.putString("amount", String.valueOf(placeOrderResponse.getAmount()));

            if (type == AppConstant.ApplicationType.MENUS) {
                bundle.putString("product_type", "Menus");
            } else if (type == AppConstant.ApplicationType.MEALS) {
                bundle.putString("product_type", "Meals");
            } else if (type == AppConstant.ApplicationType.DELIVERY_CUSTOMER) {
                bundle.putString("product_type", "Delivery Customer");
            } else if (type == AppConstant.ApplicationType.FRESH) {
                bundle.putString("product_type", "Fresh");
            }

            MyApplication.getInstance().getAppEventsLogger().logPurchase(
                    BigDecimal.valueOf(placeOrderResponse.getAmount()),
                    Currency.getInstance(getString(R.string.currency_code)),
                    bundle
            );
            Log.e("FreshCheckout>>>>>>>>", "fb logPurchase bundle>"+bundle.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType, final int apiHit) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        if (apiHit == 1)
                            placeOrderApi();
                        else if (apiHit == 0) {
                            // commented api applyPromo();
                        }
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }

    private void orderPlacedSuccess(PlaceOrderResponse placeOrderResponse) {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiverWalletUpdate);
        orderPlaced = true;
        activity.saveCheckoutData(true);
        activity.setCategoryIdOpened(null);
        long time = 0L;
        Prefs.with(activity).save(SPLabels.CHECK_BALANCE_LAST_TIME, time);
        activity.resumeMethod();



        if (placeOrderResponse.getSubscriptionDataPlaceOrder() != null) {
            if (placeOrderResponse.getSubscriptionDataPlaceOrder().getUserSubscriptions() != null && placeOrderResponse.getSubscriptionDataPlaceOrder().getUserSubscriptions().size() > 0) {
                Data.userData.getSubscriptionData().setUserSubscriptions(placeOrderResponse.getSubscriptionDataPlaceOrder().getUserSubscriptions());
                Data.autoData.setCancellationChargesPopupTextLine1(placeOrderResponse.getSubscriptionDataPlaceOrder().getCancellationChargesPopupTextLine1());
                Data.autoData.setCancellationChargesPopupTextLine2(placeOrderResponse.getSubscriptionDataPlaceOrder().getCancellationChargesPopupTextLine2());
            }
        }

        String restaurantName = "";
        if (isMenusOrDeliveryOpen() && activity.getVendorOpened() != null) {
            restaurantName = activity.getVendorOpened().getName();
        }
        placeOrderResponse.setSlot(activity.getSlotSelected());
        placeOrderResponse.setRestaurantName(restaurantName);


         if(placeOrderResponse.getReferralPopupContent()==null){
             dialogOrderComplete = new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
                 @Override
                 public void onDismiss() {
                     activity.orderComplete();
                 }
             });
         }  else{
             dialogOrderComplete = new OrderCompleteReferralDialog(activity, new OrderCompleteReferralDialog.Callback() {
                 @Override
                 public void onDialogDismiss() {
                     activity.orderComplete();
                 }

                 @Override
                 public void onConfirmed() {
                     activity.orderComplete();
                 }
             });
         }
        showOrderPlacedPopup(placeOrderResponse,type,activity,dialogOrderComplete);
        activity.clearAllCartAtOrderComplete(activity.getAppType());
        activity.setSelectedPromoCoupon(noSelectionCoupon);
        flurryEventPlaceOrder(placeOrderResponse);
        fbPurchasedEvent(paramsPlaceOrder, placeOrderResponse);
    }

    public   static void showOrderPlacedPopup(PlaceOrderResponse placeOrderResponse,Integer type, final FreshActivity activity,OrderCompletDialog orderCompletDialog) {
        Slot slot = placeOrderResponse.getSlot();
        String restaurantName = placeOrderResponse.getRestaurantName();

        int productType;
        if (type == AppConstant.ApplicationType.MEALS) {
            productType = ProductType.MEALS.getOrdinal();
        } else if (type == AppConstant.ApplicationType.MENUS) {
            productType = ProductType.MENUS.getOrdinal();
        } else if (type == AppConstant.ApplicationType.DELIVERY_CUSTOMER) {
            productType = ProductType.DELIVERY_CUSTOMER.getOrdinal();
        } else {
            productType = ProductType.FRESH.getOrdinal();
        }

        String deliverySlot = "", deliveryDay = "";
        boolean showDeliverySlot = true;
        if (type == AppConstant.ApplicationType.MENUS || type == AppConstant.ApplicationType.DELIVERY_CUSTOMER) {
            showDeliverySlot = false;
        } else {
            deliverySlot = DateOperations.convertDayTimeAPViaFormat(slot.getStartTime(), false)
                    + " - " + DateOperations.convertDayTimeAPViaFormat(slot.getEndTime(), false);
            deliveryDay = slot.getDayName();
        }


        if (placeOrderResponse.getReferralPopupContent() == null) {
            if (orderCompletDialog instanceof FreshOrderCompleteDialog) {
                ((FreshOrderCompleteDialog)orderCompletDialog).show(String.valueOf(placeOrderResponse.getOrderId()),
                        deliverySlot, deliveryDay, showDeliverySlot, restaurantName,
                        placeOrderResponse, type, "");
            }
            GAUtils.trackScreenView(productType+ORDER_PLACED);
        } else {
            if(orderCompletDialog instanceof OrderCompleteReferralDialog){
                ((OrderCompleteReferralDialog)orderCompletDialog).show(true, deliverySlot, deliveryDay,
                        activity.getResources().getString(R.string.thank_you_for_placing_order_menus_format, restaurantName),
                        placeOrderResponse.getReferralPopupContent(),
                        -1, placeOrderResponse.getOrderId(), productType);
            }

            GAUtils.trackScreenView(productType + ORDER_PLACED + REFERRAL);
        }
    }



    private void flurryEventPlaceOrder(PlaceOrderResponse placeOrderResponse) {
        try {
            chargeDetails.put("Charged ID", placeOrderResponse.getOrderId());
//            if(activity.getVendorOpened() != null) {
//                MyApplication.getInstance().updateUserDataAddInMultiValue(Events.RESTAURANT_NAMES, activity.getVendorOpened().getName());
//            }

            ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
                    .setTransactionId(String.valueOf(placeOrderResponse.getOrderId()))
                    .setTransactionAffiliation("Fresh Store")
                    .setTransactionRevenue(placeOrderResponse.getAmount())
                    .setTransactionTax(0)
                    //.setCheckoutStep(4)
                    .setTransactionShipping(0);


            try {
                AppEventsLogger logger = AppEventsLogger.newLogger(getActivity());

                Bundle parameters = new Bundle();
                parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, getString(R.string.currency_code));
                parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "product");
                parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, String.valueOf(placeOrderResponse.getOrderId()));

                logger.logEvent(AppEventsConstants.EVENT_NAME_PURCHASED,
                        placeOrderResponse.getAmount(),
                        parameters);

            } catch (Exception e) {
            }

            GAUtils.checkoutTrackEvent(AppConstant.EventTracker.ORDER_PLACED, productList);
            GAUtils.transactionCompleteEvent(productList, productAction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWalletBalanceLowDialog(final PaymentOption paymentOption) {
        try {
            FreshWalletBalanceLowDialog.Callback callback = new FreshWalletBalanceLowDialog.Callback() {
                @Override
                public void onRechargeNowClicked() {
                    intentToWallet(paymentOption);
                }

                @Override
                public void onPayByCashClicked() {
                }
            };
            if (paymentOption == PaymentOption.PAYTM && Data.userData.getPaytmEnabled() == 1) {
                String amount = Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getPaytmBalance() - Math.ceil(payableAmount())));
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_paytm_balance, amount, currencyCode, currency, R.drawable.ic_paytm_big);
            } else if (paymentOption == PaymentOption.MOBIKWIK && Data.userData.getMobikwikEnabled() == 1) {
                String amount = Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getMobikwikBalance() - Math.ceil(payableAmount())));
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_mobikwik_balance, amount, currencyCode, currency, R.drawable.ic_mobikwik_big);
            } else if (paymentOption == PaymentOption.FREECHARGE && Data.userData.getFreeChargeEnabled() == 1) {
                String amount = Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getFreeChargeBalance() - Math.ceil(payableAmount())));
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_freecharge_balance, amount, currencyCode, currency, R.drawable.ic_freecharge_big);
            } else {
                intentToWallet(paymentOption);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DecimalFormat dfNoDecimal = new DecimalFormat("#");

    private void intentToWallet(PaymentOption paymentOption) {
        try {

            Intent intent = new Intent(activity, PaymentActivity.class);
            intent.putExtra(Constants.KEY_WALLET_TYPE, paymentOption.getOrdinal());
            if (paymentOption == PaymentOption.PAYTM) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getPaytmEnabled() == 1) ? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        dfNoDecimal.format(Math.ceil(payableAmount()
                                - Data.userData.getPaytmBalance())));
            } else if (paymentOption == PaymentOption.MOBIKWIK) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getMobikwikEnabled() == 1) ? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        dfNoDecimal.format(Math.ceil(payableAmount()
                                - Data.userData.getMobikwikBalance())));
            } else if (paymentOption == PaymentOption.FREECHARGE) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getFreeChargeEnabled() == 1) ? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        dfNoDecimal.format(Math.ceil(payableAmount()
                                - Data.userData.getFreeChargeBalance())));
            } else if (paymentOption == PaymentOption.JUGNOO_PAY) {
                intent.setClass(activity, MainActivity.class);
                intent.putExtra(Constants.KEY_GO_BACK, 1);
            } else {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
            }
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isPayBarEnabled;


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
            onResume();
            activity.showPaySliderEnabled(isPayBarEnabled);
        }else{
            isPayBarEnabled = activity.isPaySliderEnabled();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiverWalletUpdate);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
    }


    private void orderPaymentModes() {
        try {
            ArrayList<PaymentModeConfigData> paymentModeConfigDatas = MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas();
            if (paymentModeConfigDatas != null && paymentModeConfigDatas.size() > 0) {
                linearLayoutWalletContainer.setVisibility(View.VISIBLE);
                linearLayoutWalletContainer.removeAllViews();
                for (PaymentModeConfigData paymentModeConfigData : paymentModeConfigDatas) {
                    if (paymentModeConfigData.getEnabled() == 1) {
                        if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutPaytm);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutMobikwik);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.FREECHARGE.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutFreeCharge);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.CASH.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutCash);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.RAZOR_PAY.getOrdinal()) {
                            linearLayoutWalletContainer.addView(rlOtherModesToPay);
                            tvOtherModesToPay.setText(paymentModeConfigData.getDisplayName());
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.UPI_RAZOR_PAY.getOrdinal()) {
                            linearLayoutWalletContainer.addView(rlUPI);
                            tvUPI.setText(paymentModeConfigData.getDisplayName());
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.ICICI_UPI.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutIcici);
                            edtIciciVpa.removeTextChangedListener(selectIciciPaymentTextWatcher);
                            edtIciciVpa.setText(paymentModeConfigData.getUpiHandle());
                            if(paymentModeConfigData.getUpiHandle()!=null && paymentModeConfigData.getUpiHandle().length()>0){
                                edtIciciVpa.setSelection(paymentModeConfigData.getUpiHandle().length()-1);

                            }
                            edtIciciVpa.addTextChangedListener(selectIciciPaymentTextWatcher);
                            jugnooVpaHandle =  paymentModeConfigData.getJugnooVpaHandle();
                            tvLabelIciciUpi.setText(activity.getString(R.string.label_below_icici_payment_edt, jugnooVpaHandle));
                            tvUPICashback.setText(!TextUtils.isEmpty(paymentModeConfigData.getUpiCashbackValue())?paymentModeConfigData.getUpiCashbackValue():"");
                        }else if (paymentModeConfigData.getPaymentOption() == PaymentOption.STRIPE_CARDS.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutStripeCard);
                            PaymentModeConfigData stripeConfigData = MyApplication.getInstance().getWalletCore().getConfigData(PaymentOption.STRIPE_CARDS.getOrdinal());
                            if(stripeConfigData!=null && stripeConfigData.getCardsData()!=null && stripeConfigData.getCardsData().size()>0 ){
                                WalletCore.getStripeCardDisplayString(activity,stripeConfigData.getCardsData().get(0),tvStripeCardNumber,ivStripeCardIcon);
                                imageViewAddStripeCard.setVisibility(View.GONE);
                            }else{
                                tvStripeCardNumber.setText(getString(R.string.add_card_payments));
                                ivStripeCardIcon.setImageResource(R.drawable.ic_card_default);
                                imageViewAddStripeCard.setVisibility(View.VISIBLE);
                            }


                        }

                    }
                }


                // for pay only in fresh and meals
                if (!isMenusOrDeliveryOpen()
                        && Data.getPayData() != null && Data.userData.getPayEnabled() == 1) {
                    linearLayoutWalletContainer.addView(relativeLayoutJugnooPay);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        setPaymentOptionVisibility();
    }

    private void setPaymentOptionVisibility() {
        try {
            if (isMenusOrDeliveryOpen()) {
                setPaymentOptionVisibility(activity.getVendorOpened().getApplicablePaymentMode());
            } else {
                setPaymentOptionVisibility(getPaymentInfoMode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getPaymentInfoMode() {

        if (type == AppConstant.ApplicationType.FRESH && activity.getUserCheckoutResponse() != null
                && activity.getUserCheckoutResponse().getPaymentInfo().getApplicablePaymentMode() != null) {
            return activity.getUserCheckoutResponse().getPaymentInfo().getApplicablePaymentMode().intValue();
        } else if (type == AppConstant.ApplicationType.MEALS && activity.getUserCheckoutResponse() != null
                && activity.getUserCheckoutResponse().getVendorInfo().getApplicablePaymentMode()!= -1){
            return activity.getUserCheckoutResponse().getVendorInfo().getApplicablePaymentMode();
        } else {
            return ApplicablePaymentMode.BOTH.getOrdinal();
        }
    }

    private void setPaymentOptionVisibility(int applicablePaymentMode) {
        relativeLayoutCash.setVisibility(View.VISIBLE);
        relativeLayoutPaytm.setVisibility(View.VISIBLE);
        relativeLayoutStripeCard.setVisibility(View.VISIBLE);
        relativeLayoutMobikwik.setVisibility(View.VISIBLE);
        relativeLayoutFreeCharge.setVisibility(View.VISIBLE);
        relativeLayoutJugnooPay.setVisibility(View.VISIBLE);
        rlOtherModesToPay.setVisibility(View.VISIBLE);
        rlUPI.setVisibility(View.VISIBLE);
        relativeLayoutIcici.setVisibility(View.VISIBLE);
        if (applicablePaymentMode == ApplicablePaymentMode.CASH.getOrdinal()) {
            relativeLayoutPaytm.setVisibility(View.GONE);
            relativeLayoutMobikwik.setVisibility(View.GONE);
            relativeLayoutFreeCharge.setVisibility(View.GONE);
            relativeLayoutJugnooPay.setVisibility(View.GONE);
            rlOtherModesToPay.setVisibility(View.GONE);
            rlUPI.setVisibility(View.GONE);
            relativeLayoutIcici.setVisibility(View.GONE);
            relativeLayoutStripeCard.setVisibility(View.GONE);
        } else if (applicablePaymentMode == ApplicablePaymentMode.ONLINE.getOrdinal()) {
            relativeLayoutCash.setVisibility(View.GONE);
        }
    }

    private void filterCouponsByApplicationPaymentMode(int applicablePaymentMode, ProductType productType) {
        if (promoCoupons == null) {
            promoCoupons = new ArrayList<>();
        }
        promoCoupons.clear();
        ArrayList<PromoCoupon> promoCouponsList = Data.userData.getCoupons(productType, activity, false);
        if(promoCouponsList!=null){
            for(PromoCoupon promo:promoCouponsList){
                if(promo.showPromoBox()){
                    promoCouponsList.remove(promo);
//                    promoCouponsList.add(promo);
                    break;
                }
            }
        }

        if (applicablePaymentMode == ApplicablePaymentMode.CASH.getOrdinal()) {
            for (PromoCoupon promoCoupon : promoCouponsList) {
                if (MyApplication.getInstance().getWalletCore().couponOfWhichWallet(promoCoupon) == PaymentOption.CASH.getOrdinal()) {
                    promoCoupons.add(promoCoupon);
                }
            }
        } else {
            promoCoupons.addAll(promoCouponsList);
        }


    }

    private void updateCouponsDataView() {
        try {
            if (type == AppConstant.ApplicationType.MEALS) {
                promoCoupons = Data.userData.getCoupons(ProductType.MEALS, activity, false);
            } else if (type == AppConstant.ApplicationType.GROCERY) {
                promoCoupons = Data.userData.getCoupons(ProductType.GROCERY, activity, false);
            } else if (type == AppConstant.ApplicationType.MENUS) {
                filterCouponsByApplicationPaymentMode(activity.getVendorOpened().getApplicablePaymentMode(), ProductType.MENUS);
            } else if (type == AppConstant.ApplicationType.DELIVERY_CUSTOMER) {
                filterCouponsByApplicationPaymentMode(activity.getVendorOpened().getApplicablePaymentMode(), ProductType.DELIVERY_CUSTOMER);
            } else {
                filterCouponsByApplicationPaymentMode(getPaymentInfoMode(), ProductType.FRESH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (promoCoupons != null) {

            if (promoCoupons.size() > 0) {
//                linearLayoutOffers.setVisibility(View.VISIBLE);
                listViewOffers.setVisibility(View.VISIBLE);
                tvNoAvailableOffers.setVisibility(View.GONE);

            } else {
//                linearLayoutOffers.setVisibility(View.GONE);
                listViewOffers.setVisibility(View.GONE);
                tvNoAvailableOffers.setVisibility(View.VISIBLE);
            }
            PromoCoupon pcOld = selectServerMarkedCouponAndReturnOld();

            // to handle if coupon manually selected and refresh on cart change is 1
            // but server is not sending any pre selected coupon
            // so revert back to old coupon
            if (couponManuallySelected
                    && activity.getUserCheckoutResponse() != null
                    && activity.getUserCheckoutResponse().getRefreshOnCartChange() == 1
                    && pcOld != null && pcOld.getId() > 0
                    && noSelectionCoupon.matchPromoCoupon(activity.getSelectedPromoCoupon())
                    && pcOld.getIsValid() == 1) {
                activity.setSelectedPromoCoupon(pcOld);
            }

            setPromoAmount();
            promoCouponsAdapter.setList(promoCoupons);

            // to check if user has selected some promo coupon from promotions screen
            if (!selectAutoSelectedCouponAtCheckout()) {
                //checks only different coupons
                if (pcOld != null && activity.getSelectedPromoCoupon() != null
                        && !pcOld.matchPromoCoupon(activity.getSelectedPromoCoupon())) {
                    if (pcOld.matchPromoCoupon(noSelectionCoupon)
                            && !activity.getSelectedPromoCoupon().matchPromoCoupon(noSelectionCoupon)) {  // coupon just applied
                        Utils.showToast(activity, activity.getString(R.string.offer_applied) + ": " + activity.getSelectedPromoCoupon().getTitle());
                    } else if (!pcOld.matchPromoCoupon(noSelectionCoupon)
                            && activity.getSelectedPromoCoupon().matchPromoCoupon(noSelectionCoupon)) {   // coupon just removed
                        Utils.showToast(activity, activity.getString(R.string.offer_removed_alert));
                    } else if (!pcOld.matchPromoCoupon(noSelectionCoupon)
                            && !activity.getSelectedPromoCoupon().matchPromoCoupon(noSelectionCoupon)) {   // coupon changed
                        Utils.showToast(activity, activity.getString(R.string.offer_applied) + ": " + activity.getSelectedPromoCoupon().getTitle());
                    }
                }
            }
        } else {
            linearLayoutOffers.setVisibility(View.GONE);
//            listViewOffers.setVisibility(View.GONE);
//            tvNoAvailableOffers.setVisibility(View.VISIBLE);
        }
    }

    /**
     * To auto apply a selected coupon from Promotions screen
     *
     * @return returns true if some coupon is selected or can't be selected else false
     */
    private boolean selectAutoSelectedCouponAtCheckout() {
        String clientId = Config.getLastOpenedClientId(activity);
        boolean couponSelected = false;
        try {
            int promoCouponId = Prefs.with(activity).getInt(Constants.SP_USE_COUPON_ + clientId, -1);
            boolean isCouponInfo = Prefs.with(activity).getBoolean(Constants.SP_USE_COUPON_IS_COUPON_ + clientId, false);
            if (promoCouponId > 0) {
                for (int i = 0; i < promoCoupons.size(); i++) {
                    PromoCoupon pc = promoCoupons.get(i);
                    if (((isCouponInfo && pc instanceof CouponInfo) || (!isCouponInfo && pc instanceof PromotionInfo))
                            && pc.getId() == promoCouponId) {
                        if (pc.getIsValid() == 1 && setSelectedCoupon(i)) {
                            Utils.showToast(activity, activity.getString(R.string.offer_applied), Toast.LENGTH_LONG);
                        }
                        couponSelected = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Prefs.with(activity).save(Constants.SP_USE_COUPON_ + clientId, -1);
        Prefs.with(activity).save(Constants.SP_USE_COUPON_IS_COUPON_ + clientId, false);
        return couponSelected;
    }

    private PromoCoupon selectServerMarkedCouponAndReturnOld() {
        if (activity.getUserCheckoutResponse() != null
                && activity.getUserCheckoutResponse().getRefreshOnCartChange() == 1) {
            PromoCoupon pcOld = activity.getSelectedPromoCoupon();
            activity.setSelectedPromoCoupon(noSelectionCoupon);

            boolean pcOldReturned = false;
            for (PromoCoupon promoCoupon : promoCoupons) {
                if (pcOld.getId() == promoCoupon.getId()) {
                    pcOld = promoCoupon;
                    pcOldReturned = true;
                    break;
                }
            }
            if(!pcOldReturned){
                pcOld = null;
            }

                for (PromoCoupon promoCoupon : promoCoupons) {
                    if (promoCoupon.getIsSelected() == 1) {
                        activity.setSelectedPromoCoupon(promoCoupon);
                        break;
                    }
                }
            return pcOld;
        } else if (activity.getUserCheckoutResponse() != null) {
            PromoCoupon pcOld = activity.getSelectedPromoCoupon();
            if (promoCoupons.size() == 0) {
                activity.setSelectedPromoCoupon(noSelectionCoupon);
            }
            return pcOld;
        }
        return null;
    }

    private void setPromoAmount() {
        try {
            if (activity.getSelectedPromoCoupon() != null && activity.getSelectedPromoCoupon().getId() > -1) {
                promoAmount = activity.getSelectedPromoCoupon().getDiscount() != null
                        && activity.getSelectedPromoCoupon().getDiscount() > 0.0 ? activity.getSelectedPromoCoupon().getDiscount() : 0;
            } else {
                promoAmount = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            promoAmount = 0;
        }
    }

    private double getTotalPromoAmount() {
        if (activity.getUserCheckoutResponse() != null && activity.getUserCheckoutResponse().getSubscription() != null) {
            return promoAmount + activity.getUserCheckoutResponse().getSubscription().getDiscount(subTotalAmount);
        } else {
            return promoAmount;
        }
    }


    @Override
    public void onSlotSelected(int position, Slot slot) {
        if (type == AppConstant.ApplicationType.MEALS
                && activity.getProductsResponse().getDeliveryInfo().getDynamicDeliveryCharges() == 1) {
            selectedSlot = slot.getDeliverySlotId();
            getCheckoutDataAPI(selectedSubscription, false);
        } else {
            activity.setSlotSelected(slot);
            recyclerViewDeliverySlots.scrollToPosition(position);
        }
        GAUtils.event(activity.getGaCategory(), CHECKOUT + DELIVERY_SLOT + MODIFIED, slot.getDayName() + " " + slot.getTimeSlotDisplay());
    }


    public void getCheckoutDataAPI(SubscriptionData.Subscription subscription, final boolean showMealsMismatchPopup,String promoText) {
        try {
            if (MyApplication.getInstance().isOnline()) {

                boolean loaderShown = false;
                if (!DialogPopup.isDialogShowing()) {
                    DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
                    loaderShown = true;
                }
                final boolean finalLoaderShown = loaderShown;

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                if (deliveryAddressUpdated) {
                    params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                    params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
                } else {
                    params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getMenuRefreshLatLng().latitude));
                    params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getMenuRefreshLatLng().longitude));
                }

                params.put(Constants.KEY_CURRENT_LATITUDE, String.valueOf(Data.latitude));
                params.put(Constants.KEY_CURRENT_LONGITUDE, String.valueOf(Data.longitude));
                if(promoText==null && getSelectedCoupon()!=null && getSelectedCoupon().showPromoBox()){
                    promoText = getSelectedCoupon().getPromoName();
                }

                if(promoText!=null){
                    params.put(Constants.KEY_REFFERAL_CODE,promoText);

                }

                if(vehicleInfoList != null && !vehicleInfoList.isEmpty() && currentVehicleTypePos != -1 && vehicleType != 2217) {
                    params.put(Constants.KEY_VEHICLE_TYPE, ""+vehicleType);
                }

                if (isMenusOrDeliveryOpen()) {
                    params.put(Constants.KEY_CART, cartItemsMenus());
                } else {
                    params.put(Constants.KEY_CART, cartItemsFM());
                }
                params.put(Constants.ORDER_AMOUNT, Utils.getMoneyDecimalFormat().format(getSubTotalAmount(false)));


                if (type == AppConstant.ApplicationType.MEALS) {
                    params.put(Constants.STORE_ID, "" + AppConstant.ApplicationType.MEALS);
                    params.put(Constants.GROUP_ID, "" + activity.getProductsResponse().getCategories().get(0).getCurrentGroupId());
                } else if (isMenusOrDeliveryOpen()) {
                    params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(activity.getVendorOpened().getRestaurantId()));
                    String data = new Gson().toJson(activity.getVendorOpened(), MenusResponse.Vendor.class);
                    params.put(Constants.KEY_RESTAURANT_DATA, data);
                }

                params.put(Constants.INTERATED, "1");
                params.put(Constants.KEY_CLIENT_ID, Config.getLastOpenedClientId(activity));

                if (selectedSlot != -1) {
                    params.put(Constants.KEY_USER_SELECTED_SLOT, String.valueOf(selectedSlot));
                }

                if (subscription != null) {
                    JSONObject jItem = new JSONObject();
                    jItem.put(Constants.KEY_SUBSCRIPTION_ID, selectedSubscription.getId());
                    params.put(Constants.KEY_SUBSCRIPTION_INFO, jItem.toString());
                }

                if (type == AppConstant.ApplicationType.FRESH) {
                    params.put(Constants.KEY_VENDOR_ID, String.valueOf(activity.getOpenedVendorId()));
                }

                Log.i(TAG, "getAllProducts params=" + params.toString());

                final String finalPromoText = promoText;
                Callback<UserCheckoutResponse> callback = new Callback<UserCheckoutResponse>() {
                    @Override
                    public void success(UserCheckoutResponse userCheckoutResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt(Constants.KEY_FLAG);
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

                                    currencyCode = userCheckoutResponse.getCurrencyCode();
                                    currency = userCheckoutResponse.getCurrency();
                                    setAdapterMenuOrFresh(userCheckoutResponse);

                                    setChargesAdapter(userCheckoutResponse.getCurrencyCode(), userCheckoutResponse.getCurrency());

                                    if (FreshCheckoutMergedFragment.this.type == AppConstant.ApplicationType.FRESH
                                            && userCheckoutResponse.getCityId() != null
                                            && activity.checkForCityChange(userCheckoutResponse.getCityId(),
                                            new FreshActivity.CityChangeCallback() {
                                                @Override
                                                public void onYesClick() {
                                                    activity.refreshCart2 = true;
                                                    if (activity.getFreshFragment() != null) {
                                                        activity.getSupportFragmentManager().popBackStack(FreshFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                                    } else {
                                                        activity.getSupportFragmentManager().popBackStack(FreshCheckoutMergedFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                                    }
                                                }

                                                @Override
                                                public void onNoClick() {
                                                    getCheckoutDataAPI(selectedSubscription, false);
                                                }
                                            })) {


                                    } else {



                                        activity.setUserCheckoutResponse(userCheckoutResponse);
                                        if (activity.getUserCheckoutResponse() != null
                                                && activity.getUserCheckoutResponse().getSubscriptionInfo() != null
                                                && activity.getUserCheckoutResponse().getSubscriptionInfo().getSubscriptionId() == null) {
                                            Utils.showToast(activity, activity.getString(R.string.star_could_not_be_added, getString(R.string.app_name)));
                                        }
                                        setSubscriptionView();
                                        if (isMenusOrDeliveryOpen()) {
                                            getSubscriptionFromCheckout(userCheckoutResponse);
                                            if (userCheckoutResponse.getCharges() != null) {
                                                activity.getMenuProductsResponse().setCharges(userCheckoutResponse.getCharges());
                                            }
                                        } else {
                                            updateCartFromCheckoutData(userCheckoutResponse);
                                        }

                                        activity.buttonPlaceOrder.setText(getActivity().getResources().getString(R.string.place_order));

                                        Log.v(TAG, "" + userCheckoutResponse.getCheckoutData().getLastAddress());

                                        setActivityLastAddressFromResponse(userCheckoutResponse);
                                        updateDeliverySlot(userCheckoutResponse.getDeliveryInfo());
                                        setDeliverySlotsDataUI();

                                        updateAddressView();
                                        toggleChatAvailability(userCheckoutResponse.getChatAvailable()==1);
                                        if (isMenusOrDeliveryOpen()
                                                && userCheckoutResponse.getRestaurantInfo() != null
                                                && !TextUtils.isEmpty(userCheckoutResponse.getRestaurantInfo().getAddress())) {
                                            updateDeliveryFromView(userCheckoutResponse.getRestaurantInfo().getAddress());
                                        }




                                        if (type == AppConstant.ApplicationType.MEALS) {
                                            if (Data.getMealsData().getPromoCoupons() == null) {
                                                Data.getMealsData().setPromoCoupons(new ArrayList<PromoCoupon>());
                                            }
                                            Data.getMealsData().getPromoCoupons().clear();
                                            if (userCheckoutResponse.getPromotions() != null) {
                                                Data.getMealsData().getPromoCoupons().addAll(userCheckoutResponse.getPromotions());
                                            }
                                            if (userCheckoutResponse.getCoupons() != null) {
                                                Data.getMealsData().getPromoCoupons().addAll(userCheckoutResponse.getCoupons());
                                            }

                                            boolean isDiscountValidCheckout =  userCheckoutResponse.getDiscountInfo()!=null && userCheckoutResponse.getDiscountInfo().getIsActive();
                                            if(isDiscountValidCheckout!=activity.isShowingEarlyBirdDiscount()){
                                                if(!showMealsMismatchPopup)
                                                   DialogPopup.alertPopup(activity,"", userCheckoutResponse.getDiscountSwitchMessage(isDiscountValidCheckout));
                                                activity.setShowingEarlyBirdDiscount(isDiscountValidCheckout);
                                                activity.setRefreshCart(true);

                                            }
                                            if(showMealsMismatchPopup){
                                                showMealsPriceMismatchDialog();
                                            }
                                        } else if (type == AppConstant.ApplicationType.GROCERY) {
                                            if (Data.getGroceryData().getPromoCoupons() == null) {
                                                Data.getGroceryData().setPromoCoupons(new ArrayList<PromoCoupon>());
                                            }
                                            Data.getGroceryData().getPromoCoupons().clear();
                                            if (userCheckoutResponse.getPromotions() != null) {
                                                Data.getGroceryData().getPromoCoupons().addAll(userCheckoutResponse.getPromotions());
                                            }
                                            if (userCheckoutResponse.getCoupons() != null) {
                                                Data.getGroceryData().getPromoCoupons().addAll(userCheckoutResponse.getCoupons());
                                            }
                                        } else if (isMenusOpen()) {
                                            if (Data.getMenusData().getPromoCoupons() == null) {
                                                Data.getMenusData().setPromoCoupons(new ArrayList<PromoCoupon>());
                                            }
                                            Data.getMenusData().getPromoCoupons().clear();
                                            if (userCheckoutResponse.getPromotions() != null) {
                                                Data.getMenusData().getPromoCoupons().addAll(userCheckoutResponse.getPromotions());
                                            }
                                            if (userCheckoutResponse.getCoupons() != null) {
                                                Data.getMenusData().getPromoCoupons().addAll(userCheckoutResponse.getCoupons());
                                            }
                                        } else if (type == AppConstant.ApplicationType.DELIVERY_CUSTOMER) {
                                            if (Data.getDeliveryCustomerData().getPromoCoupons() == null) {
                                                Data.getDeliveryCustomerData().setPromoCoupons(new ArrayList<PromoCoupon>());
                                            }
                                            Data.getDeliveryCustomerData().getPromoCoupons().clear();
                                            if (userCheckoutResponse.getPromotions() != null) {
                                                Data.getDeliveryCustomerData().getPromoCoupons().addAll(userCheckoutResponse.getPromotions());
                                            }
                                            if (userCheckoutResponse.getCoupons() != null) {
                                                Data.getDeliveryCustomerData().getPromoCoupons().addAll(userCheckoutResponse.getCoupons());
                                            }
                                        } else {
                                            if (Data.getFreshData().getPromoCoupons() == null) {
                                                Data.getFreshData().setPromoCoupons(new ArrayList<PromoCoupon>());
                                            }
                                            Data.getFreshData().getPromoCoupons().clear();
                                            if (userCheckoutResponse.getPromotions() != null) {
                                                Data.getFreshData().getPromoCoupons().addAll(userCheckoutResponse.getPromotions());
                                            }
                                            if (userCheckoutResponse.getCoupons() != null) {
                                                Data.getFreshData().getPromoCoupons().addAll(userCheckoutResponse.getCoupons());
                                            }
                                        }
                                        updateCouponsDataView();
                                        updateCartDataView();
                                        setPaymentOptionUI();
                                        setPaymentOptionVisibility();

                                        try {
                                            if (cartChangedRefreshCheckout) {
                                                setSelectedCoupon(promoCoupons.indexOf(activity.getSelectedPromoCoupon()));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        cartChangedRefreshCheckout = false;
                                    }

                                    if(Data.getCurrentIciciUpiTransaction(activity.getAppType())!=null){
                                        activity.setPlaceOrderResponse(Data.getCurrentIciciUpiTransaction(activity.getAppType()));
                                        onIciciUpiPaymentInitiated(Data.getCurrentIciciUpiTransaction(activity.getAppType()).getIcici(),String.valueOf(Data.getCurrentIciciUpiTransaction(activity.getAppType()).getAmount()));


                                    }

                                } else {
                                    setSlideInitial();
                                    final int redirect = jObj.optInt(Constants.KEY_REDIRECT, 0);
                                    final int emptyCart = jObj.optInt(Constants.KEY_EMPTY_CART, 0);
                                    final int outOfRange = jObj.optInt(Constants.KEY_OUT_OF_RANGE, 0);
                                    if (isMenusOrDeliveryOpen() && outOfRange == 1) {
                                        outOfRangeDialog(message);
                                    } else {
                                        DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (emptyCart == 1) {
                                                    clearMenusCartAndGoToMenusFragment(true);
                                                } else if (redirect == 1) {
                                                    if (activity.getFreshSearchFragment() != null) {
                                                        activity.performBackPressed(false);
                                                    }
                                                    activity.performBackPressed(false);
                                                }
                                            }
                                        });
                                    }
                                    activity.buttonPlaceOrder.setText(activity.getString(R.string.connection_lost_try_again));
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(DialogErrorType.SERVER_ERROR, finalPromoText);
                        }
                        if (finalLoaderShown) {
                            DialogPopup.dismissLoadingDialog();
                        }
                        linearLayoutMain.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                        if(userCheckoutResponse.getVehiclesList()!=null){
                            //TODO handle vehicles here.
                            handleVehiclesList(userCheckoutResponse.getVehiclesList());
                        }
                        else {
                            textViewChooseVehicle.setVisibility(View.GONE);
                            linearLayoutDeliveryVehicles.setVisibility(View.GONE);
                            rvVehicles.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
                        if (finalLoaderShown) {
                            DialogPopup.dismissLoadingDialog();
                        }
                        retryDialog(DialogErrorType.CONNECTION_LOST, finalPromoText);
                    }
                };
                new HomeUtil().putDefaultParams(params);
                if (isMenusOrDeliveryOpen()) {
                    RestClient.getMenusApiService().userCheckoutData(params, callback);
                } else {
                    RestClient.getFreshApiService().userCheckoutData(params, callback);
                }
            } else {
                retryDialog(DialogErrorType.NO_NET, promoText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setChargesAdapter(String currencyCode, String currency) {
        if(chargesAdapter == null) {
            chargesAdapter = new CheckoutChargesAdapter(activity, chargesList, currencyCode, currency);
            listViewCharges.setAdapter(chargesAdapter);
        }
    }

    private void setAdapterMenuOrFresh(UserCheckoutResponse userCheckoutResponse) {
        if (isMenusOrDeliveryOpen()) {
            if(menusCartItemsAdapter == null) {
                menusCartItemsAdapter = new MenusCartItemsAdapter(activity, itemsInCart, true, this, userCheckoutResponse.getCurrencyCode(), userCheckoutResponse.getCurrency());
                listViewCart.setAdapter(menusCartItemsAdapter);
            }
        } else {
            if(freshCartItemsAdapter == null) {
                freshCartItemsAdapter = new FreshCartItemsAdapter(activity, subItemsInCart, true, this, this, userCheckoutResponse.getCurrencyCode(), userCheckoutResponse.getCurrency());
                listViewCart.setAdapter(freshCartItemsAdapter);
            }
        }
    }

    public void getCheckoutDataAPI(SubscriptionData.Subscription subscription, final boolean showMealsMismatchPopup){
        getCheckoutDataAPI(subscription,showMealsMismatchPopup,null);
    }

    private void updateDeliverySlot(UserCheckoutResponse.DeliveryInfo deliveryInfo) {
        if (deliveryInfo != null) {
            for (DeliverySlot slot : activity.getUserCheckoutResponse().getCheckoutData().getDeliverySlots()) {
                for (Slot slot1 : slot.getSlots()) {
                    if (slot1.getDeliverySlotId().equals(deliveryInfo.getSelectedSlot())) {
                        activity.setSlotSelected(slot1);
                        return;
                    }
                }
            }
        }
    }

    private boolean checkoutApiDoneOnce = false;
    private void setActivityLastAddressFromResponse(UserCheckoutResponse userCheckoutResponse){
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType, final String promoText) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getCheckoutDataAPI(selectedSubscription, false,promoText);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                },Data.getCurrentIciciUpiTransaction(activity.getAppType())==null);
    }


    private void updateCartFromCheckoutData(UserCheckoutResponse userCheckoutResponse) {
        if (userCheckoutResponse.getCartItems() != null) {
            boolean cartChanged = false;
            for (int i = 0; i < subItemsInCart.size(); i++) {
                SubItem si = subItemsInCart.get(i);
                int index = userCheckoutResponse.getCartItems().indexOf(userCheckoutResponse.new CartItem(si.getSubItemId()));
                if (index > -1) {
                    UserCheckoutResponse.CartItem cartItem = userCheckoutResponse.getCartItems().remove(index);
                    if (cartItem.getStatus() < 0 || cartItem.getQuantity() <= 0) {
                        subItemsInCart.remove(i);
                        i--;
                        cartChanged = true;
                    } else {
                        cartChanged = cartChanged
                                || (!si.getPrice().equals(cartItem.getPrice())
                                || !si.getSubItemQuantitySelected().equals(cartItem.getQuantity())
                                || !si.getSubItemName().equalsIgnoreCase(cartItem.getSubItemName())
                                || !si.getSubItemImage().equalsIgnoreCase(cartItem.getSubItemImage()));
                        si.setSubItemQuantitySelected(cartItem.getQuantity());
                        si.setSubItemName(cartItem.getSubItemName());
                        si.setSubItemImage(cartItem.getSubItemImage());
                        si.setPrice(cartItem.getPrice());
                    }
                } else {
                    subItemsInCart.remove(i);
                    i--;
                    cartChanged = true;
                }
            }
            if (cartChanged) {
                activity.saveCartList(subItemsInCart);
                freshCartItemsAdapter.notifyDataSetChanged();
                activity.setCartChangedAtCheckout(true);
            }
        }

        getSubscriptionFromCheckout(userCheckoutResponse);
    }

    private void getSubscriptionFromCheckout(UserCheckoutResponse userCheckoutResponse) {
        if (Data.userData.getShowSubscriptionData() == 1 && !Data.userData.isSubscriptionActive() && userCheckoutResponse.getShowStarSubscriptions() == 1) {
            if (userCheckoutResponse.getSubscriptionInfo() != null && userCheckoutResponse.getSubscriptionInfo().getSubscriptionId() != null) {
                if (isMenusOrDeliveryOpen()) {
                    menusCartItemsAdapter.setResults(itemsInCart, userCheckoutResponse.getSubscriptionInfo(), userCheckoutResponse.getCurrencyCode(), userCheckoutResponse.getCurrency());
                } else {
                    freshCartItemsAdapter.setResults(subItemsInCart, userCheckoutResponse.getSubscriptionInfo(), userCheckoutResponse.getCurrencyCode(), userCheckoutResponse.getCurrency());
                }
                cvBecomeStar.setVisibility(View.GONE);
            } else {
                if (isMenusOrDeliveryOpen()) {
                    menusCartItemsAdapter.setResults(itemsInCart, userCheckoutResponse.getSubscriptionInfo(), userCheckoutResponse.getCurrencyCode(), userCheckoutResponse.getCurrency());
                } else {
                    freshCartItemsAdapter.setResults(subItemsInCart, null, userCheckoutResponse.getCurrencyCode(), userCheckoutResponse.getCurrency());
                }
                cvBecomeStar.setVisibility(View.VISIBLE);
            }
        } else {
            cvBecomeStar.setVisibility(View.GONE);
        }
    }

    private void setDeliverySlotsDataUI() {
        if (activity.getUserCheckoutResponse() != null
                && activity.getUserCheckoutResponse().getCheckoutData() != null
                && activity.getUserCheckoutResponse().getCheckoutData().getDeliverySlots() != null) {
            slots.clear();

            if (activity.getSlotSelected() != null) {
                if (activity.getSlotSelected().getIsActiveSlot() != 1) {
                    activity.setSlotSelected(null);
                }
            }

            if (activity.getSlotSelected() != null) {
                boolean slotContains = false;
                for (DeliverySlot deliverySlot : activity.getUserCheckoutResponse().getCheckoutData().getDeliverySlots()) {
                    if (deliverySlot.getSlots().contains(activity.getSlotSelected())) {
                        slotContains = true;
                        break;
                    }
                }
                if (!slotContains) {
                    activity.setSlotSelected(null);
                }
            }

            for (DeliverySlot deliverySlot : activity.getUserCheckoutResponse().getCheckoutData().getDeliverySlots()) {
                int slotsEnabled = 0;
                for (Slot slot : deliverySlot.getSlots()) {
                    slot.setSlotViewType(SlotViewType.SLOT_TIME);
                    slot.setDayName(deliverySlot.getDayName());
                    slotsEnabled = slot.getIsActiveSlot() == 1 ? slotsEnabled + 1 : slotsEnabled;
                    slots.add(slot);
                    if ((activity.getSlotSelected() == null || activity.getSlotSelected().getIsActiveSlot() != 1)
                            && slot.getIsActiveSlot() == 1) {
                        activity.setSlotSelected(slot);
                    }
                }
            }

            if (slots.size() == 0) {
                textViewNoDeliverySlot.setVisibility(View.VISIBLE);
            } else {
                textViewNoDeliverySlot.setVisibility(View.GONE);
            }
            deliverySlotsAdapter.notifyDataSetChanged();
        }
    }

    private double getTotalSavings() {
        double totalSavings = 0d;
        try {
            for (SubItem subItems : subItemsInCart) {
                if (!TextUtils.isEmpty(subItems.getOldPrice())) {
                    totalSavings = totalSavings + ((Double.parseDouble(subItems.getOldPrice()) - subItems.getPrice()) * subItems.getSubItemQuantitySelected());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalSavings;
    }


    @Subscribe
    public void onUpdateListEvent(AddressAdded event) {
        if (event.flag) {
            updateAddressView();
        }
    }

    public boolean addressSelectedNotValid() {
        return TextUtils.isEmpty(activity.getSelectedAddressType()) || activity.getSelectedAddressId()<=0;
    }

    public void updateAddressView() {
        try {
            if (TextUtils.isEmpty(activity.getSelectedAddress())) {
                setActivityLastAddressFromResponse(activity.getUserCheckoutResponse());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageViewAddressType.setImageResource(R.drawable.ic_loc_other);
        imageViewAddressType.setPaddingRelative(0, 0, 0, 0);
        textViewAddressName.setVisibility(View.GONE);
        textViewAddressValue.setTextColor(activity.getResources().getColor(R.color.text_color));
        if (!addressSelectedNotValid() && !TextUtils.isEmpty(activity.getSelectedAddress())) {
            textViewAddressValue.setVisibility(View.VISIBLE);
            tvNoAddressAlert.setVisibility(View.GONE);
            imageViewDeliveryAddressForward.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_back_pay_selector));
            imageViewDeliveryAddressForward.setVisibility(View.VISIBLE);
            textViewAddressValue.setText(activity.getSelectedAddress());
            setCurrentSelectedAddressToMenus();
            imageViewAddressType.setImageResource(R.drawable.ic_loc_other);
            if (!TextUtils.isEmpty(activity.getSelectedAddressType())) {
                textViewAddressName.setVisibility(View.VISIBLE);
                textViewAddressValue.setTextColor(activity.getResources().getColor(R.color.text_color_light));
                if (activity.getSelectedAddressType().equalsIgnoreCase(activity.getString(R.string.home))) {
                    imageViewAddressType.setImageResource(R.drawable.ic_home);
                    textViewAddressName.setText(activity.getString(R.string.home));
                } else if (activity.getSelectedAddressType().equalsIgnoreCase(activity.getString(R.string.work))) {
                    imageViewAddressType.setImageResource(R.drawable.ic_work);
                    textViewAddressName.setText(activity.getString(R.string.work));
                } else {
                    imageViewAddressType.setImageResource(R.drawable.ic_loc_other);
                    textViewAddressName.setText(activity.getSelectedAddressType());
                }
            }
        } else {
            textViewAddressValue.setText(activity.getResources().getString(R.string.add_address));
            imageViewAddressType.setImageResource(R.drawable.ic_exclamation_address);
            imageViewDeliveryAddressForward.getDrawable().mutate().setColorFilter(ContextCompat.getColor(activity, R.color.red_alert_no_address), PorterDuff.Mode.SRC_ATOP);
            int padding = activity.getResources().getDimensionPixelSize(R.dimen.dp_2);
            imageViewAddressType.setPaddingRelative(padding, padding, padding, padding);
            textViewAddressValue.setVisibility(View.GONE);
            tvNoAddressAlert.setVisibility(View.VISIBLE);
//            imageViewDeliveryAddressForward.setVisibility(View.GONE);


        }
    }


    @Override
    public void onPlusClicked(int position, SubItem subItem) {
        if (!cartChangedRefreshCheckout) {
            GAUtils.event(activity.getGaCategory(), CHECKOUT, CART + ITEM + MODIFIED);
        }
        activity.saveSubItemToDeliveryStoreCart(subItem);
        activity.setCartChangedAtCheckout(true);
        editTextDeliveryInstructions.clearFocus();
        cartChangedRefreshCheckout = true;
        updateCartDataView();
        if (type == AppConstant.ApplicationType.MEALS
                && activity.getProductsResponse().getDeliveryInfo().getDynamicDeliveryCharges() == 1) {
            getCheckoutDataAPI(selectedSubscription, false);
        } else {
            if (!rehitCheckoutApi()) {
                removeCouponWithCheck();
            }
        }
        GAUtils.event(activity.getGaCategory(), CHECKOUT, CART + ITEM + INCREASED);
    }

    @Override
    public void onMinusClicked(int position, SubItem subItem) {
        if (!cartChangedRefreshCheckout) {
            GAUtils.event(activity.getGaCategory(), CHECKOUT, CART + ITEM + MODIFIED);
        }
        editTextDeliveryInstructions.clearFocus();
        activity.setCartChangedAtCheckout(true);
        cartChangedRefreshCheckout = true;
        if (subItem.getSubItemQuantitySelected() == 0) {
            subItemsInCart.remove(position);
        }
        activity.saveSubItemToDeliveryStoreCart(subItem);

        checkIfEmpty();
        updateCartDataView();
        if (subItemsInCart.size() > 0
                && type == AppConstant.ApplicationType.MEALS
                && activity.getProductsResponse().getDeliveryInfo().getDynamicDeliveryCharges() == 1) {
            getCheckoutDataAPI(selectedSubscription, false);
        } else if (subItemsInCart.size() > 0) {
            if (!rehitCheckoutApi()) {
                removeCouponWithCheck();
            }
        }
        GAUtils.event(activity.getGaCategory(), CHECKOUT, CART + ITEM + DECREASED);
    }

    @Override
    public void deleteStarSubscription() {
        selectedSubscription = null;
        getCheckoutDataAPI(selectedSubscription, false);
    }


    @Override
    public boolean checkForMinus(int position, SubItem subItem) {
        return activity.checkForMinus(position, subItem);
    }

    @Override
    public void minusNotDone(int position, SubItem subItem) {
        activity.clearMealsCartIfNoMainMeal();
    }


    @Override
    public void onPlusClicked(int position, int itemTotalQuantity) {
        editTextDeliveryInstructions.clearFocus();
        cartChangedRefreshCheckout = true;
        updateCartDataView();
        if (!rehitCheckoutApi()) {
            removeCouponWithCheck();
        }
    }

    @Override
    public void onMinusClicked(int position, int itemTotalQuantity) {
        editTextDeliveryInstructions.clearFocus();
        cartChangedRefreshCheckout = true;
        updateCartDataView();
        if (itemTotalQuantity == 0) {
            itemsInCart.remove(position);
            checkIfEmpty();
        }
        if (itemsInCart.size() > 0) {
            if (!rehitCheckoutApi()) {
                removeCouponWithCheck();
            }
        }
    }

    private void setCurrentSelectedAddressToMenus() {
        SearchResult searchResult = HomeUtil.getNearBySavedAddress(activity, activity.getSelectedLatLng(), false);
        pickUpAddress = searchResult;
        if(searchResult != null) {
//            fetchDrivers();
            isPickUpSet = true;
        }
    }

    private void removeCouponWithCheck() {
        if (getSelectedCoupon() != null && getSelectedCoupon().getId() > 0) {
            removeCoupon();
        }
    }

    private boolean rehitCheckoutApi() {
        if (activity.getUserCheckoutResponse() != null
                && activity.getUserCheckoutResponse().getRefreshOnCartChange() == 1) {
            getCheckoutDataAPI(selectedSubscription, false);
            return true;
        }
        return false;
    }


    public void deleteCart() {
        if (isMenusOrDeliveryOpen()) {
            for (Item item : itemsInCart) {
                item.getItemSelectedList().clear();
            }
            updateCartDataView();
            itemsInCart.clear();
            menusCartItemsAdapter.notifyDataSetChanged();
            checkIfEmpty();
        } else {
            for (SubItem subItem : subItemsInCart) {
                subItem.setSubItemQuantitySelected(0);
                activity.saveSubItemToDeliveryStoreCart(subItem);
            }
            updateCartDataView();
            subItemsInCart.clear();
            freshCartItemsAdapter.notifyDataSetChanged();
            checkIfEmpty();
        }
    }

    private void checkIfEmpty() {
        if (isMenusOrDeliveryOpen()) {
            if (itemsInCart.size() == 0) {
                activity.performBackPressed(false);
            }
        } else {
            if (subItemsInCart.size() == 0) {
                activity.updateTotalAmountPrice(0d, 0);
                if (activity.isMealAddonItemsAvailable()) {
                    activity.performBackPressed(false);
                }
                activity.performBackPressed(false);
            }
        }
    }


    @Override
    public void onCouponSelected() {
        if (cartChangedRefreshCheckout) {
            getCheckoutDataAPI(selectedSubscription, false);
        }
        couponManuallySelected = true;
    }

    @Override
    public PromoCoupon getSelectedCoupon() {
        return activity.getSelectedPromoCoupon();
    }

    @Override
    public void removeCoupon() {
        activity.setSelectedPromoCoupon(noSelectionCoupon);
        setPromoAmount();
        updateCartUI();
        promoCouponsAdapter.notifyDataSetChanged();
        Utils.showToast(activity, activity.getString(R.string.offer_removed_alert));
    }

    @Override
    public boolean setSelectedCoupon(int position) {
        PromoCoupon promoCoupon;
        if (promoCoupons != null && position > -1 && position < promoCoupons.size()) {
            promoCoupon = promoCoupons.get(position);
        } else {
            promoCoupon = noSelectionCoupon;
            activity.setSelectedPromoCoupon(promoCoupon);
        }
        boolean offerApplied = false;
        if (promoCoupon.getIsValid() == 0) {
            String message = activity.getString(R.string.please_check_tnc);
            if (!TextUtils.isEmpty(promoCoupon.getInvalidMessage())) {
                message = promoCoupon.getInvalidMessage();
            }
            DialogPopup.alertPopup(activity, "", message);
        } else {
           if (MyApplication.getInstance().getWalletCore().displayAlertAndCheckForSelectedWalletCoupon(activity, activity.getPaymentOption().getOrdinal(), promoCoupon)) {
                activity.setSelectedPromoCoupon(promoCoupon);
                offerApplied = true;
            }
            setPromoAmount();
            updateCartUI();
            if (activity.getSelectedPromoCoupon() != null) {
                GAUtils.event(activity.getGaCategory(), CHECKOUT + OFFER + MODIFIED, activity.getSelectedPromoCoupon().getTitle());
            }
        }
        return offerApplied;
    }

    @Override
    public void applyPromoCoupon(String text) {
        getCheckoutDataAPI(selectedSubscription,false,text);
    }




    private int noOfItemsInCart;
    private void updateCartTopBarView(Pair<Double, Integer> pair) {
        taxSubTotal.setValue(getSubTotalAmount(true));
        noOfItemsInCart =pair.second;
        textViewCartItems.setText(activity.getString(R.string.cart_items_format, String.valueOf(pair.second)));
        taxTotal.setValue(pair.first);
        if (chargesAdapter != null) {
            chargesAdapter.notifyDataSetChanged();
        }
    }

    private void updateCartDataView() {
        try {
            Pair<Double, Integer> pair;
            if (isMenusOrDeliveryOpen()) {
                pair = activity.updateCartValuesGetTotalPrice();
            } else {
                pair = activity.getSubItemInCartTotalPrice();
            }
            subTotalAmount = pair.first;
            updateCartTopBarView(pair);
            updateCartUI();

            if(isMenusOrDeliveryOpen() && activity.getVendorOpened()!=null){

                double diffDouble = activity.getVendorOpened().getMinimumOrderAmount()-subTotalAmount;
                if(diffDouble>0){
                    String textToSet = activity.getString(R.string.min_order_checkout, Utils.getMoneyDecimalFormat().format( activity.getVendorOpened().getMinimumOrderAmount()));
                    tvMinOrderLabelDisplay.setText(textToSet);
                    layoutMinOrder.setVisibility(View.VISIBLE);
                    tvMinOrderLabelDisplay.setVisibility(View.VISIBLE);
                    shadowMinOrder.setVisibility(View.VISIBLE);
                    activity.showPaySliderEnabled(false);

                }else{

                    activity.showPaySliderEnabled(true);
                    tvMinOrderLabelDisplay.setVisibility(View.GONE);
                    layoutMinOrder.setVisibility(View.GONE);
                    shadowMinOrder.setVisibility(View.GONE);
                }

            }else if(activity.getAppType()== AppConstant.ApplicationType.FRESH && activity.getVendorOpened()!=null){

                double diffDouble = activity.getOpenedDeliveryStore().getMinOrderAmount()-subTotalAmount;
                if(diffDouble>0){
                    String textToSet = activity.getString(R.string.min_order_checkout, Utils.getMoneyDecimalFormat().format( activity.getVendorOpened().getMinimumOrderAmount()));
                    tvMinOrderLabelDisplay.setText(textToSet);
                    tvMinOrderLabelDisplay.setVisibility(View.VISIBLE);
                    layoutMinOrder.setVisibility(View.VISIBLE);
                    shadowMinOrder.setVisibility(View.VISIBLE);
                    activity.showPaySliderEnabled(false);

                }else{

                    activity.showPaySliderEnabled(true);
                    tvMinOrderLabelDisplay.setVisibility(View.GONE);
                    layoutMinOrder.setVisibility(View.GONE);
                    shadowMinOrder.setVisibility(View.GONE);
                }

            }else{
                activity.showPaySliderEnabled(true);
                tvMinOrderLabelDisplay.setVisibility(View.GONE);
                layoutMinOrder.setVisibility(View.GONE);
                shadowMinOrder.setVisibility(View.GONE);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double getSubTotalAmount(boolean includeSubscription) {
        if (includeSubscription) {
            double subTotalAmountIncSubs = subTotalAmount;
            if (!Data.userData.isSubscriptionActive()
                    && activity.getUserCheckoutResponse() != null
                    && activity.getUserCheckoutResponse().getSubscriptionInfo() != null
                    && activity.getUserCheckoutResponse().getSubscriptionInfo().getSubscriptionId() != null) {
                subTotalAmountIncSubs = subTotalAmountIncSubs + activity.getUserCheckoutResponse().getSubscriptionInfo().getPrice();
            }
            return subTotalAmountIncSubs;
        } else {
            return subTotalAmount;
        }
    }

    private PaymentOption getSavedPaymentOption() {
        return MyApplication.getInstance().getWalletCore().getDefaultPaymentOption();
//        if(checkoutSaveData.isDefault()){
//            return MyApplication.getInstance().getWalletCore().getDefaultPaymentOption();
//        } else{
//            return MyApplication.getInstance().getWalletCore().getPaymentOptionFromInt(checkoutSaveData.getPaymentMode());
//        }
    }

    private double getTotalTaxValue(){
        if(activity.getUserCheckoutResponse() != null){
            return activity.getUserCheckoutResponse().getTotalTaxValue(subTotalAmount, getTotalPromoAmount());
        } else {
            return 0d;
        }
    }


    private double totalUndiscounted() {
        if (isMenusOrDeliveryOpen()) {
            return getSubTotalAmount(true) + totalTaxAmount + getTotalTaxValue();
        } else {
            return getSubTotalAmount(true) + deliveryCharges() + getTotalTaxValue();
        }
    }

    private double totalAmount() {
        double totalAmount = totalUndiscounted() - getTotalPromoAmount();
        if (totalAmount < 0) {
            totalAmount = 0;
        }
        return totalAmount;
    }

    private double payableAmount() {
        double payableAmount = totalAmount() - jcUsed();
        if (payableAmount < 0) {
            payableAmount = 0;
        }
        return payableAmount;
    }

    private double jcUsed() {
        if (isMenusOrDeliveryOpen() && activity.getPaymentOption() == PaymentOption.CASH) {
            return 0d;
        } else {
            return Math.min(totalAmount(), Data.userData.getJugnooBalance());
        }
    }


    private double deliveryCharges() {
        double deliveryCharges = 0d;
        if (type == AppConstant.ApplicationType.MEALS) {
            if (activity.getUserCheckoutResponse() != null
                    && activity.getUserCheckoutResponse().getSubscription().getDeliveryCharges() != null
                    /*&& activity.getUserCheckoutResponse().getSubscription().getDeliveryCharges() > 0*/){
                deliveryCharges = activity.getUserCheckoutResponse().getSubscription().getDeliveryCharges();
            } else if (activity.getUserCheckoutResponse() != null
                    && activity.getUserCheckoutResponse().getDeliveryInfo() != null
                    && activity.getUserCheckoutResponse().getDeliveryInfo().getDeliveryCharges() != null
                    /*&& activity.getUserCheckoutResponse().getDeliveryInfo().getDeliveryCharges() > 0*/){
                deliveryCharges = activity.getUserCheckoutResponse().getDeliveryInfo().getDeliveryCharges();
            } else {
                deliveryCharges = activity.getProductsResponse().getDeliveryInfo().getApplicableDeliveryCharges(type, getSubTotalAmount(false));
            }
        } else {
            if (activity.getProductsResponse() != null && activity.getProductsResponse().getDeliveryInfo() != null) {
                deliveryCharges = activity.getProductsResponse().getDeliveryInfo().getApplicableDeliveryCharges(type, getSubTotalAmount(false));
            } else {
                deliveryCharges = activity.getSuperCategoriesData().getDeliveryInfo().getApplicableDeliveryCharges(type, getSubTotalAmount(false));
                if (activity.getUserCheckoutResponse() != null
                        && activity.getUserCheckoutResponse().getSubscription().getDeliveryCharges() != null) {
                    deliveryCharges = activity.getUserCheckoutResponse().getSubscription().getDeliveryCharges();
                }
            }
        }
        if (!Data.userData.isSubscriptionActive()
                && activity.getUserCheckoutResponse() != null
                && activity.getUserCheckoutResponse().getSubscriptionInfo() != null
                && activity.getUserCheckoutResponse().getSubscriptionInfo().getSubscriptionId() != null
                && activity.getUserCheckoutResponse().getSubscriptionInfo().getDeliveryCharges() != null) {
            deliveryCharges = activity.getUserCheckoutResponse().getSubscriptionInfo().getDeliveryCharges();
        }

        return deliveryCharges;
    }


    public void apiPlaceOrderPayCallback(final MessageRequest message) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                DialogPopup.showLoadingDialog(activity, "");
                HashMap<String, String> params = new HashMap<>();

                params.put(Constants.KEY_ORDER_ID, String.valueOf(activity.getPlaceOrderResponse().getOrderId()));
                params.put(Constants.KEY_PAY_ORDER_ID, String.valueOf(activity.getPlaceOrderResponse().getPaymentObject().getOrderId()));
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_CLIENT_ID,
                        Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));
                if (message != null) {
                    params.put(Constants.KEY_MESSAGE, message.toString());
                }
                Callback<SendMoneyCallbackResponse> callback = new Callback<SendMoneyCallbackResponse>() {
                    @Override
                    public void success(SendMoneyCallbackResponse commonResponse, Response response) {
//                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        DialogPopup.dismissLoadingDialog();
                        try {
                            int flag = commonResponse.getFlag();
                            if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                orderPlacedSuccess(activity.getPlaceOrderResponse());
                            } else if (flag == ApiResponseFlags.ACTION_FAILED.getOrdinal()) {
                                // DialogPopup.alertPopup(activity, "", commonResponse.getMessage());

                                new OrderCheckoutFailureDialog(getActivity(), new OrderCheckoutFailureDialog.Callback() {
                                    @Override
                                    public void onRetryClicked() {
                                        placeOrderApi();
                                    }

                                    @Override
                                    public void onChangePaymentClicked() {

                                    }
                                }).show(commonResponse.getMessage());

                            } else {
                                retryDialogPlaceOrderPayCallbackApi(DialogErrorType.SERVER_ERROR, message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            retryDialogPlaceOrderPayCallbackApi(DialogErrorType.SERVER_ERROR, message);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        retryDialogPlaceOrderPayCallbackApi(DialogErrorType.CONNECTION_LOST, message);
                    }
                };
                new HomeUtil().putDefaultParams(params);
                if (isMenusOrDeliveryOpen()) {
                    RestClient.getMenusApiService().placeOrderCallback(params, callback);
                } else {
                    RestClient.getFreshApiService().placeOrderCallback(params, callback);
                }
            } else {
                retryDialogPlaceOrderPayCallbackApi(DialogErrorType.NO_NET, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialogPlaceOrderPayCallbackApi(DialogErrorType dialogErrorType, final MessageRequest message) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        apiPlaceOrderPayCallback(message);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }

    private boolean deliveryAddressUpdated = false;

    public void setDeliveryAddressUpdated(boolean deliveryAddressUpdated) {
        this.deliveryAddressUpdated = deliveryAddressUpdated;
    }

    private void placeOrderAnalytics() {
    }


    private boolean isMenusOpen() {
        return type == AppConstant.ApplicationType.MENUS ;
    }

    public boolean isMenusOrDeliveryOpen(){
        return type== AppConstant.ApplicationType.MENUS || type==AppConstant.ApplicationType.DELIVERY_CUSTOMER;
    }

    private boolean isFreshOpen() {
        return type == AppConstant.ApplicationType.FRESH;
    }

    private void clearMenusCartAndGoToMenusFragment(boolean clearCart) {
       if(clearCart) activity.clearMenusCart(activity.getAppType());
        if(activity.getVendorMenuFragment() != null) {
            if(clearCart)activity.setRefreshCart(true);
            activity.performBackPressed(false);
        }
        if(activity.getMerchantInfoFragment() != null) {
            if(clearCart) activity.setRefreshCart(true);
            activity.performBackPressed(false);
        }
        if(clearCart)activity.setRefreshCart(true);
        activity.performBackPressed(false);
    }

    private Double getCalculatedCharges(Charges charges, List<Charges> chargesList) {
        double amount = subTotalAmount - (charges.getSubtractDiscount() == 1 ? getTotalPromoAmount() : 0);
        double includedTaxValue = 0d;
        for (Integer pos : charges.getIncludedValues()) {
            try {
                Charges chargesPos = chargesList.get(chargesList.indexOf(new Charges(pos)));
                includedTaxValue = includedTaxValue + getCalculatedCharges(chargesPos, chargesList);
            } catch (Exception e) {
            }
        }
        if (charges.getType() == Charges.ChargeType.SUBTOTAL_LEVEL.getOrdinal()) {
            if (charges.getIsPercent() == 1) {
                return ((amount + includedTaxValue) * (Double.parseDouble(charges.getValue()) / 100d));
            } else {
                return (Double.parseDouble(charges.getValue()) + includedTaxValue);
            }
        } else if (charges.getType() == Charges.ChargeType.ITEM_LEVEL.getOrdinal()) {
            double totalCharge = 0d;
            for (Item item : itemsInCart) {
                Tax taxMatched = null;
                for (Tax tax : item.getTaxes()) {
                    if (tax.getKey().equalsIgnoreCase(charges.getValue())) {
                        taxMatched = tax;
                        break;
                    }
                }
                if (taxMatched != null) {
                    if (charges.getIsPercent() == 1) {
                        totalCharge = totalCharge +
                                (item.getSuperTotalPrice() * (taxMatched.getValue() / 100d));
                    } else {
                        totalCharge = totalCharge + ((double) item.getTotalQuantity() * taxMatched.getValue());
                    }
                }
            }
            if (charges.getIsPercent() == 1) {
                totalCharge = totalCharge + (includedTaxValue * (charges.getDefault() / 100d));
            } else {
                totalCharge = totalCharge + includedTaxValue;
            }
            return totalCharge;
        } else {
            return 0d;
        }
    }

    private void updateDeliveryFromView(String address) {
        if (isMenusOrDeliveryOpen() && activity.getVendorOpened() != null) {
            llDeliveryFrom.setVisibility(View.VISIBLE);
            tvRestName.setText(activity.getVendorOpened().getName());
            if (TextUtils.isEmpty(address)) {
                tvRestAddress.setText(activity.getVendorOpened().getAddress());
            } else {
                tvRestAddress.setText(address);
            }
        } else {
            llDeliveryFrom.setVisibility(View.GONE);
        }
    }


    // razor pay back from service callback
    public void razorpayServiceCallback(JSONObject jsonObject) {
        try {
            int flag = jsonObject.getInt(Constants.KEY_FLAG);
            String message = JSONParser.getServerMessage(jsonObject);
            if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
//                if(jsonObject.has(Constants.KEY_REFERRAL_POPUP_CONTENT)){
//                    PlaceOrderResponse.ReferralPopupContent referralPopupContent = activity.getGson()
//                            .fromJson(jsonObject.getJSONObject(Constants.KEY_REFERRAL_POPUP_CONTENT).toString(),
//                                    PlaceOrderResponse.ReferralPopupContent.class);
//                    activity.getPlaceOrderResponse().setReferralPopupContent(referralPopupContent);
//                }
                orderPlacedSuccess(activity.getPlaceOrderResponse());
                setSliderCompleteState();

            } else if (flag == ApiResponseFlags.ACTION_FAILED.getOrdinal()) {
                DialogPopup.alertPopup(activity, "", message);
                setSlideInitial();
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
        }
    }


    private HashMap<String, String> paramsPlaceOrder;

    private void outOfRangeDialog(String message) {
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message,
                activity.getString(R.string.change),
                activity.getString(R.string.back),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearMenusCartAndGoToMenusFragment(true);
                        Utils.showToast(activity, activity.getString(R.string.your_cart_has_been_cleared));
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean addressChanged = activity.setDeliveryAddressModelToSelectedAddress(true,true);
                        if(addressChanged){
                            deliveryAddressUpdated = true;
                            getCheckoutDataAPI(selectedSubscription, false);
                        }else {
                            clearMenusCartAndGoToMenusFragment(false);
                        }

                    }
                }, false, false);
    }


    private CheckoutRequestPaymentDialog checkoutRequestPaymentDialog;
    private ApiCancelOrder apiCancelOrder;

    private void showRequestPaymentDialog(String amount, long expiryTimeLeft, ArrayList<String> reasonList,Long timerStartedAt) {

        if (checkoutRequestPaymentDialog == null) {
            checkoutRequestPaymentDialog = CheckoutRequestPaymentDialog.init(activity);
        }
        checkoutRequestPaymentDialog.setData(amount, timerStartedAt, expiryTimeLeft, reasonList, new CheckoutRequestPaymentDialog.CheckoutRequestPaymentListener() {
            @Override
            public void onCancelClick(String reason) {
                if(activity.getPlaceOrderResponse()!=null){
                    if(apiCancelOrder ==null){
                        apiCancelOrder = new ApiCancelOrder(getActivity(), new ApiCancelOrder.Callback() {
                            @Override
                            public void onSuccess(String message) {
                                onIciciStatusResponse(IciciPaymentOrderStatus.CANCELLED,message);
                            }

                            @Override
                            public void onFailure() {

                            }

                            @Override
                            public void onRetry(View view) {

                            }

                            @Override
                            public void onNoRetry(View view) {

                            }
                        });
                    }
                    apiCancelOrder.hit(activity.getPlaceOrderResponse().getOrderId(),Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()),
                            -1,isMenusOrDeliveryOpen()?ProductType.MENUS.getOrdinal():ProductType.FRESH.getOrdinal(),reason,"");
                }
            }

            @Override
            public void onTimerComplete() {
                isIciciPaymentRunnableInProgress = false;
                activity.getHandler().removeCallbacks(checkIciciUpiPaymentStatusRunnable);
                checkIciciPaymentStatusApi();

            }

            @Override
            public void onRetryClick() {
                checkIciciPaymentStatusApi();
            }
        }, jugnooVpaHandle).showDialog();

    }

    private void onIciciUpiPaymentInitiated(PlaceOrderResponse.IciciUpi icici,String amount) {
        currentStatus=null;
        isIciciPaymentRunnableInProgress = true;
        TOTAL_EXPIRY_TIME_ICICI_UPI = icici.getExpirationTimeMillis();
        DELAY_ICICI_UPI_STATUS_CHECK = icici.getPollingTimeMillis();
        Long timerStartedAt = icici.getTimerStartedAt()==null?System.currentTimeMillis():icici.getTimerStartedAt();
        icici.setTimerStartedAt(timerStartedAt);
        if(activity.getPlaceOrderResponse()!=null){
            activity.getPlaceOrderResponse().setSlot(activity.getSlotSelected());
            String restaurantName = "";
            if (isMenusOrDeliveryOpen() && activity.getVendorOpened() != null) {
                restaurantName = activity.getVendorOpened().getName();
            }
            activity.getPlaceOrderResponse().setRestaurantName(restaurantName);
        }
        Data.saveCurrentIciciUpiTransaction(activity.getPlaceOrderResponse(), activity.getAppType());
        showRequestPaymentDialog(amount, TOTAL_EXPIRY_TIME_ICICI_UPI,icici.getReasonList(),icici.getTimerStartedAt()==null?System.currentTimeMillis():icici.getTimerStartedAt());
        activity.getHandler().postDelayed(checkIciciUpiPaymentStatusRunnable, DELAY_ICICI_UPI_STATUS_CHECK);

    }


    private  boolean isIciciPaymentRunnableInProgress;
    private IciciPaymentOrderStatus currentStatus ;
    private void onIciciStatusResponse(IciciPaymentOrderStatus status,String toastMessage) {
        if (currentStatus==null || status!=currentStatus) {
            switch (status) {
                case FAILURE:
                case EXPIRED:
                case CANCELLED:
                    isIciciPaymentRunnableInProgress = false;
                    activity.getHandler().removeCallbacks(checkIciciUpiPaymentStatusRunnable);
                    if (checkoutRequestPaymentDialog != null && checkoutRequestPaymentDialog.isShowing()) {
                        checkoutRequestPaymentDialog.dismiss();
                    }
                    Toast.makeText(activity, toastMessage, Toast.LENGTH_SHORT).show();
                    Data.deleteCurrentIciciUpiTransaction(activity.getAppType());
                    break;
                case SUCCESSFUL:
                case PROCESSED:
                case COMPLETED:
                    isIciciPaymentRunnableInProgress = false;
                    activity.getHandler().removeCallbacks(checkIciciUpiPaymentStatusRunnable);

                    if (checkoutRequestPaymentDialog != null && checkoutRequestPaymentDialog.isShowing()) {
                        checkoutRequestPaymentDialog.dismiss();
                    }
    //                Toast.makeText(activity,toastMessage, Toast.LENGTH_SHORT).show();
                    orderPlacedSuccess(activity.getPlaceOrderResponse());
                    Data.deleteCurrentIciciUpiTransaction(activity.getAppType());

                    break;
                case PENDING:
                    //Keep waiting for next status
                    break;



            }
        }
        currentStatus=status;


    }



    private long DELAY_ICICI_UPI_STATUS_CHECK = 30 * 1000;
    private long TOTAL_EXPIRY_TIME_ICICI_UPI = 4 * 60 * 1000;
    private   Callback<IciciPaymentRequestStatus> iciciPaymentStatusCallback;

    private Runnable checkIciciUpiPaymentStatusRunnable = new Runnable() {
        @Override
        public void run() {

            checkIciciPaymentStatusApi();
            activity.getHandler().postDelayed(this, DELAY_ICICI_UPI_STATUS_CHECK);


        }
    };



    private void checkIciciPaymentStatusApi() {
        if (MyApplication.getInstance().isOnline()) {
            HashMap<String, String> params = new HashMap<>();
            HomeUtil.addDefaultParams(params);
            params.put(Constants.KEY_ORDER_ID, String.valueOf(activity.getPlaceOrderResponse().getOrderId()));
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_CLIENT_ID, Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));
            if (iciciPaymentStatusCallback == null) {

                iciciPaymentStatusCallback = new Callback<IciciPaymentRequestStatus>() {
                    @Override
                    public void success(IciciPaymentRequestStatus commonResponse, Response response) {
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, commonResponse.getFlag(), commonResponse.getError(), commonResponse.getMessage())) {

                            if (commonResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                onIciciStatusResponse(commonResponse.getStatus(),commonResponse.getToastMessage());

                            }

                        }
                        if(checkoutRequestPaymentDialog!=null&&checkoutRequestPaymentDialog.isTimerExpired()){
                            checkoutRequestPaymentDialog.enableRetryButton();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        if(checkoutRequestPaymentDialog!=null&&checkoutRequestPaymentDialog.isTimerExpired()){
                            checkoutRequestPaymentDialog.enableRetryButton();
                            checkoutRequestPaymentDialog.toggleConnectionState(false);

                        }
                    }

                };
            }

            if (isMenusOrDeliveryOpen()) {


                RestClient.getMenusApiService().checkPaymentStatus(params, iciciPaymentStatusCallback);
            } else {


                RestClient.getFreshApiService().checkPaymentStatus(params, iciciPaymentStatusCallback);
            }
        }else{
            if(checkoutRequestPaymentDialog!=null&&checkoutRequestPaymentDialog.isTimerExpired()){
                checkoutRequestPaymentDialog.enableRetryButton();
                checkoutRequestPaymentDialog.toggleConnectionState(false);

            }
            Log.e("TAG", "No net tried to hit get status icici api");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.getHandler().removeCallbacks(checkIciciUpiPaymentStatusRunnable);
        if(checkoutRequestPaymentDialog!=null){
            checkoutRequestPaymentDialog.stopTimer();
        }
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(iciciStatusBroadcast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity.getTopFragment() instanceof FreshCheckoutMergedFragment) {
            try {
                activity.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        orderPaymentModes();
                        setPaymentOptionUI();

                        if (dialogOrderComplete == null || dialogOrderComplete.getDialog()==null || !dialogOrderComplete.getDialog().isShowing()) {
                            if(checkoutRequestPaymentDialog==null||!checkoutRequestPaymentDialog.isShowing())
                                 getCheckoutDataAPI(selectedSubscription, false);
                        }
                    }
                }, 150);
                if (Data.userData != null) {
                    if (Data.userData.isSubscriptionActive()) {
                        cvBecomeStar.setVisibility(View.GONE);
                    }
                }

                if(isIciciPaymentRunnableInProgress){
                    activity.getHandler().postDelayed(checkIciciUpiPaymentStatusRunnable,1 * 1000);
                }

                if(checkoutRequestPaymentDialog!=null && checkoutRequestPaymentDialog.isShowing()){
                    checkoutRequestPaymentDialog.resumeTimer();
                }

                LocalBroadcastManager.getInstance(getActivity()).registerReceiver(iciciStatusBroadcast, ICICI_STATUS_BROADCAST_FILTER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private BroadcastReceiver iciciStatusBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Integer orderStatus  = intent.getIntExtra(Constants.ICICI_ORDER_STATUS,Constants.NO_VALID_STATUS);
            if(orderStatus!=Constants.NO_VALID_STATUS){
                    //Only if the payment is processing corresponding to that order ID
                if(activity.getPlaceOrderResponse()!=null && activity.getPlaceOrderResponse().getOrderId()==intent.getIntExtra(Constants.KEY_ORDER_ID,0)) {
                    onIciciStatusResponse(IciciPaymentRequestStatus.parseStatus(intent.getBooleanExtra(Constants.IS_MENUS_OR_DELIVERY, false), orderStatus, false),
                            intent.hasExtra(Constants.KEY_MESSAGE) ? intent.getStringExtra(Constants.KEY_MESSAGE) : "");
                }


            }

        }
    };

    public boolean isPriceMisMatchDialogShowing() {
        return checkoutPriceMismatchDialog!=null && checkoutPriceMismatchDialog.isShowing();
    }

    private CheckoutPriceMismatchDialog checkoutPriceMismatchDialog;
    public void showMealsPriceMismatchDialog(){
        if(checkoutPriceMismatchDialog==null){
            checkoutPriceMismatchDialog = CheckoutPriceMismatchDialog.init(activity);
        }
        checkoutPriceMismatchDialog.setData(new CheckoutPriceMismatchDialog.CheckoutPriceMismatchDialogListener() {
            @Override
            public void onCancelClick() {

            }

            @Override
            public void onSubmitClick() {
                placeOrder();

            }
        },freshCartItemsAdapter,chargesAdapter,noOfItemsInCart).showDialog();

    }




    public static void orderViaFatafat(Context context, ArrayList<Item> itemsInCart, ArrayList<SubItem> subItemsInCart, FreshActivity activity, Double subTotalAmount, String mCurrencyCode, String mCurrency){
        activity.lastAppTypeOpen = activity.getAppType();
        StringBuilder sb = new StringBuilder();
        String newLine = "\n", colon = ": ", xSpace = " X ";
        if(activity.isMenusOrDeliveryOpen()){
            int count = 0;
            for(Item item : itemsInCart){
                for(ItemSelected itemSelected : item.getItemSelectedList()){
                    if(itemSelected.getQuantity() > 0){
                        sb.append(itemSelected.getQuantity()).append(xSpace).append(item.getItemName()+"\n("+item.getItemDetails()+")");
                        item.generateCustomizeText(itemSelected);

                        if(!TextUtils.isEmpty(itemSelected.getCustomizeText())){
                            sb.append(newLine).append(itemSelected.getCustomizeText());
                        }
                        sb.append(newLine);
                        sb.append(newLine);
                        count++;
                    }

                }
            }

           if(count>0){
               //remove new line
               sb.setLength(sb.length()-1);
           }
        } else {
            for(SubItem subItem : subItemsInCart){

                sb.append(subItem.getSubItemQuantitySelected()).append(xSpace).append(subItem.getSubItemName());
                sb.append(newLine);


             }
        }


        sb.append(newLine);
        sb.append(context.getString(R.string.approx_order_amount)).append(colon).append(Utils.formatCurrencyAmount(subTotalAmount, mCurrencyCode, mCurrency));



        String cartString;
        if(sb.length()>0){
            cartString = sb.toString().trim();
        }else{
            cartString=null;
        }



        activity.setOrderViaChatData(new FreshActivity.OrderViaChatData(activity.getVendorOpened().getLatLng(), activity.getVendorOpened().getAddress(), activity.getVendorOpened().getName(),cartString,activity.getVendorOpened().getRestaurantId()));
        activity.switchOffering(Config.getFeedClientId());
    }
}
