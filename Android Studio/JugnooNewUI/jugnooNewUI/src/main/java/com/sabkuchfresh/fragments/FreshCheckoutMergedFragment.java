package com.sabkuchfresh.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.datastructure.ApplicablePaymentMode;
import com.sabkuchfresh.datastructure.CheckoutSaveData;
import com.sabkuchfresh.dialogs.OrderCompleteReferralDialog;
import com.sabkuchfresh.home.CallbackPaymentOptionSelector;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.home.FreshWalletBalanceLowDialog;
import com.sabkuchfresh.home.OrderCheckoutFailureDialog;
import com.sabkuchfresh.retrofit.model.DeliverySlot;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.Slot;
import com.sabkuchfresh.retrofit.model.SlotViewType;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.retrofit.model.UserCheckoutResponse;
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
import product.clicklabs.jugnoo.home.adapters.PromoCouponsAdapter;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.UserDebtDialog;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;
import product.clicklabs.jugnoo.widgets.MySpinner;
import product.clicklabs.jugnoo.widgets.SwipeButton.SwipeButton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FreshCheckoutMergedFragment extends Fragment implements GAAction, DeliverySlotsAdapter.Callback,
        FreshCartItemsAdapter.Callback, PromoCouponsAdapter.Callback, MenusCartItemsAdapter.Callback {

    private final String TAG = FreshCheckoutMergedFragment.class.getSimpleName();
    private RelativeLayout linearLayoutRoot;

    private RelativeLayout relativeLayoutCartTop;
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
    private TextView textViewAddressName, textViewAddressValue,tvNoAddressAlert;

    private RelativeLayout relativeLayoutPaytm, relativeLayoutMobikwik, relativeLayoutFreeCharge, relativeLayoutJugnooPay, relativeLayoutCash;
    private LinearLayout linearLayoutWalletContainer;
    private ImageView imageViewPaytmRadio, imageViewAddPaytm, imageViewRadioMobikwik, imageViewAddMobikwik,
            imageViewRadioFreeCharge, imageViewAddFreeCharge, imageViewRadioJugnooPay, imageViewAddJugnooPay, imageViewCashRadio;
    private TextView textViewPaytmValue, textViewMobikwikValue, textViewFreeChargeValue;
	private RelativeLayout rlOtherModesToPay, rlUPI;
	private ImageView ivOtherModesToPay, ivUPI;
	private TextView tvOtherModesToPay, tvUPI;

    private LinearLayout linearLayoutOffers;
    private NonScrollListView listViewOffers;
    private PromoCouponsAdapter promoCouponsAdapter;

    private EditText editTextDeliveryInstructions;

    private ScrollView scrollView;
    private LinearLayout linearLayoutMain;
    private TextView textViewScroll;

    private TextView textViewDeliveryInstructionsText;

    private CardView cvStarSavings, cvBecomeStar;
    private TextView tvStarSavingsValue;



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
    private Dialog dialogOrderComplete;

    public FreshCheckoutMergedFragment() {
    }

    private List<Product> productList = new ArrayList<>();
    private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "Don't apply coupon on this ride");

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
    private ArrayList<Tax> chargesList;
    private Tax taxSubTotal, taxTotal;
    private ArrayList<Item> itemsInCart = new ArrayList<>();
    private MenusCartItemsAdapter menusCartItemsAdapter;
    private double totalTaxAmount = 0d;
    private LinearLayout llDeliveryFrom;
    private RelativeLayout rlDeliveryFrom;
    private TextView tvRestName, tvRestAddress;
    private boolean couponManuallySelected;
    private boolean isRazorUPI;

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
        if(!orderPlaced){
            activity.saveCheckoutData(false);
        }
        super.onStop();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_checkout_merged, container, false);

        cartChangedRefreshCheckout = false;
        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);
        activity.setCartChangedAtCheckout(false);
        type = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);

        GAUtils.trackScreenView(activity.getGaCategory()+CHECKOUT);

        linearLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.linearLayoutRoot);
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


        if(isMenusOpen()){
            try {
                if(itemsInCart == null) {
					itemsInCart = new ArrayList<>();
				}
                itemsInCart.clear();


                if(activity.getMenuProductsResponse().getCategories() != null) {
					for (Category category : activity.getMenuProductsResponse().getCategories()) {
						if(category.getSubcategories() != null){
							for(Subcategory subcategory : category.getSubcategories()){
								for(Item item : subcategory.getItems()){
									if(item.getTotalQuantity() > 0){
										itemsInCart.add(item);
									}
								}
							}
						} else if(category.getItems() != null){
							for(Item item : category.getItems()){
								if(item.getTotalQuantity() > 0){
									itemsInCart.add(item);
								}
							}
						}
					}
				}

                try {
					for (int i = 0; i < itemsInCart.size(); i++) {
						MyApplication.getInstance().getCleverTapUtils().addToCart(itemsInCart.get(i).getItemName(),
								itemsInCart.get(i).getRestaurantItemId(), itemsInCart.get(i).getTotalQuantity(),
								itemsInCart.get(i).getPrice(),
								Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()),
								Data.userData.getCity());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            subItemsInCart = activity.fetchCartList();
            try {
                if(type == AppConstant.ApplicationType.MEALS
                        && activity.getProductsResponse() != null
                        && activity.getProductsResponse().getCategories() != null) {
                    currentGroupId = activity.getProductsResponse().getCategories().get(0).getCurrentGroupId();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                for (int i = 0; i < subItemsInCart.size(); i++) {
                    MyApplication.getInstance().getCleverTapUtils().addToCart(subItemsInCart.get(i).getSubItemName(),
                            subItemsInCart.get(i).getSubItemId(), subItemsInCart.get(i).getSubItemQuantitySelected(),
                            subItemsInCart.get(i).getPrice(),
                            Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()),
                            Data.userData.getCity());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        try {
            if(Data.getDatumToReOrder() != null){
//				activity.setSelectedAddress(Data.getDatumToReOrder().getDeliveryAddress());
//				activity.setSelectedLatLng(new LatLng(Data.getDatumToReOrder().getDeliveryLatitude(), Data.getDatumToReOrder().getDeliveryLongitude()));
                ArrayList<SearchResult> searchResults = new HomeUtil().getSavedPlacesWithHomeWork(activity);
                for(SearchResult searchResult : searchResults){
                    if(Data.getDatumToReOrder().getAddressId().equals(searchResult.getId())){
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

        ((TextView)rootView.findViewById(R.id.textViewDeliverySlot)).setTypeface(Fonts.mavenMedium(activity));
        ((TextView)rootView.findViewById(R.id.textViewDeliveryAddress)).setTypeface(Fonts.mavenMedium(activity));
        ((TextView)rootView.findViewById(R.id.textViewPaymentVia)).setTypeface(Fonts.mavenMedium(activity));
        ((TextView)rootView.findViewById(R.id.textViewOffers)).setTypeface(Fonts.mavenMedium(activity));


        relativeLayoutCartTop = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCartTop);
        textViewCartItems = (TextView) rootView.findViewById(R.id.textViewCartItems); textViewCartItems.setTypeface(Fonts.mavenMedium(activity));
        textViewCartTotalUndiscount = (TextView) rootView.findViewById(R.id.textViewCartTotalUndiscount); textViewCartTotalUndiscount.setTypeface(Fonts.mavenMedium(activity));
        imageViewCartArrow = (ImageView) rootView.findViewById(R.id.imageViewCartArrow);
        imageViewDeleteCart = (ImageView) rootView.findViewById(R.id.imageViewDeleteCart);
        imageViewCartSep = (ImageView) rootView.findViewById(R.id.imageViewCartSep);
        linearLayoutCartExpansion = (LinearLayout) rootView.findViewById(R.id.linearLayoutCartExpansion);
        tvBecomeStar = (TextView)rootView.findViewById(R.id.tvBecomeStar); tvBecomeStar.setTypeface(Fonts.mavenMedium(activity));
        tvStarOffer = (TextView)rootView.findViewById(R.id.tvStarOffer); tvStarOffer.setTypeface(Fonts.mavenMedium(activity));
        listViewCart = (NonScrollListView) rootView.findViewById(R.id.listViewCart);

        if(isMenusOpen()){
            menusCartItemsAdapter = new MenusCartItemsAdapter(activity, itemsInCart, true, this);
            listViewCart.setAdapter(menusCartItemsAdapter);
        } else {
            freshCartItemsAdapter = new FreshCartItemsAdapter(activity, subItemsInCart, "Review Cart", true, this);
            listViewCart.setAdapter(freshCartItemsAdapter);
        }


        listViewCharges = (NonScrollListView) rootView.findViewById(R.id.listViewCharges);
        chargesList = new ArrayList<>();
        taxSubTotal = new Tax(activity.getString(R.string.sub_total), activity.getTotalPrice());
        taxTotal = new Tax(activity.getString(R.string.total).toUpperCase(), activity.getTotalPrice());
        chargesList.add(taxSubTotal);
        chargesList.add(taxTotal);

        chargesAdapter = new CheckoutChargesAdapter(activity, chargesList);
        listViewCharges.setAdapter(chargesAdapter);
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

        if(type == AppConstant.ApplicationType.MENUS) {
            linearLayoutDeliverySlot.setVisibility(View.GONE);
        }

        textViewDeliveryInstructionsText = ((TextView)rootView.findViewById(R.id.textViewDeliveryInstructions));
        textViewDeliveryInstructionsText.setTypeface(Fonts.mavenMedium(activity));
        editTextDeliveryInstructions = (EditText) rootView.findViewById(R.id.editTextDeliveryInstructions);
        editTextDeliveryInstructions.setTypeface(Fonts.mavenRegular(activity));

        if(type == AppConstant.ApplicationType.MENUS) {
            textViewDeliveryInstructionsText.setText(R.string.delivery_instructions_for_menus);
            editTextDeliveryInstructions.setHint(R.string.add_special_notes_for_menus);
        } else {
            textViewDeliveryInstructionsText.setText(R.string.delivery_instructions);
            editTextDeliveryInstructions.setHint(R.string.add_special_notes);
        }


        relativeLayoutDeliveryAddress = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDeliveryAddress);
        imageViewAddressType = (ImageView) rootView.findViewById(R.id.imageViewAddressType);
        imageViewDeliveryAddressForward = (ImageView) rootView.findViewById(R.id.imageViewDeliveryAddressForward);
        textViewAddressName = (TextView) rootView.findViewById(R.id.textViewAddressName); textViewAddressName.setTypeface(Fonts.mavenMedium(activity));
        textViewAddressValue = (TextView) rootView.findViewById(R.id.textViewAddressValue); textViewAddressValue.setTypeface(Fonts.mavenRegular(activity));
        tvNoAddressAlert = (TextView) rootView.findViewById(R.id.tv_no_address_alert);

        llDeliveryFrom = (LinearLayout) rootView.findViewById(R.id.llDeliveryFrom);
        rlDeliveryFrom = (RelativeLayout) rootView.findViewById(R.id.rlDeliveryFrom);
        tvRestName = (TextView) rootView.findViewById(R.id.tvRestName);
        tvRestAddress = (TextView) rootView.findViewById(R.id.tvRestAddress);


        linearLayoutWalletContainer = (LinearLayout) rootView.findViewById(R.id.linearLayoutWalletContainer);
        relativeLayoutPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaytm);
        relativeLayoutMobikwik = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutMobikwik);
        relativeLayoutFreeCharge = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutFreeCharge);
        relativeLayoutJugnooPay = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutJugnooPay);
        relativeLayoutCash = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCash);
        imageViewPaytmRadio = (ImageView) rootView.findViewById(R.id.imageViewPaytmRadio);
        imageViewAddPaytm = (ImageView) rootView.findViewById(R.id.imageViewAddPaytm);
        imageViewRadioMobikwik = (ImageView)rootView.findViewById(R.id.imageViewRadioMobikwik);
        imageViewAddMobikwik = (ImageView) rootView.findViewById(R.id.imageViewAddMobikwik);
        imageViewRadioFreeCharge = (ImageView)rootView.findViewById(R.id.imageViewRadioFreeCharge);
        imageViewAddFreeCharge = (ImageView) rootView.findViewById(R.id.imageViewAddFreeCharge);
        imageViewRadioJugnooPay = (ImageView)rootView.findViewById(R.id.imageViewRadioJugnooPay);
        imageViewAddJugnooPay = (ImageView) rootView.findViewById(R.id.imageViewAddJugnooPay);
        imageViewCashRadio = (ImageView) rootView.findViewById(R.id.imageViewCashRadio);
        textViewPaytmValue = (TextView)rootView.findViewById(R.id.textViewPaytmValue);textViewPaytmValue.setTypeface(Fonts.mavenMedium(activity));
        textViewMobikwikValue = (TextView)rootView.findViewById(R.id.textViewMobikwikValue);textViewMobikwikValue.setTypeface(Fonts.mavenMedium(activity));
        textViewFreeChargeValue = (TextView)rootView.findViewById(R.id.textViewFreeChargeValue);textViewFreeChargeValue.setTypeface(Fonts.mavenMedium(activity));
        ((TextView)rootView.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenMedium(activity));
        ((TextView)rootView.findViewById(R.id.textViewJugnooPay)).setTypeface(Fonts.mavenMedium(activity));
		rlOtherModesToPay = (RelativeLayout) rootView.findViewById(R.id.rlOtherModesToPay);
		ivOtherModesToPay = (ImageView) rootView.findViewById(R.id.ivOtherModesToPay);
		tvOtherModesToPay = (TextView) rootView.findViewById(R.id.tvOtherModesToPay);
        rlUPI = (RelativeLayout) rootView.findViewById(R.id.rlUPI);
        ivUPI = (ImageView) rootView.findViewById(R.id.ivUPI);
        tvUPI = (TextView) rootView.findViewById(R.id.tvUPI);

        linearLayoutOffers = (LinearLayout) rootView.findViewById(R.id.linearLayoutOffers);
        listViewOffers = (NonScrollListView) rootView.findViewById(R.id.listViewOffers);
        promoCouponsAdapter = new PromoCouponsAdapter(activity, R.layout.list_item_fresh_promo_coupon, promoCoupons, this);
        listViewOffers.setAdapter(promoCouponsAdapter);


        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);
        textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);

        cvStarSavings = (CardView) rootView.findViewById(R.id.cvStarSavings); cvStarSavings.setVisibility(View.GONE);
        ((TextView)rootView.findViewById(R.id.tvStarSavings)).setTypeface(Fonts.mavenMedium(activity));
        tvStarSavingsValue = (TextView)rootView.findViewById(R.id.tvStarSavingsValue); tvStarSavingsValue.setTypeface(Fonts.mavenMedium(activity));

        cvBecomeStar = (CardView) rootView.findViewById(R.id.cvBecomeStar); cvBecomeStar.setVisibility(View.GONE);
        spin = (MySpinner) rootView.findViewById(R.id.simpleSpinner);
        btnAddStar = (Button) rootView.findViewById(R.id.btnAddStar);


        try {
            activity.sliderText.setText("Swipe to confirm >>");
        } catch (Exception e) {
        }

        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        paramsF = (RelativeLayout.LayoutParams) activity.tvSlide.getLayoutParams();



        btnAddStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSubscription = new Gson().fromJson(selectedSubId, SubscriptionData.Subscription.class);
                getCheckoutDataAPI(selectedSubscription);
                GAUtils.event(activity.getGaCategory(), CHECKOUT+SUBSCRIPTION+ADDED, selectedSubscription.getPlanString());
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
                if(getActivity() instanceof FreshActivity && ((FreshActivity)getActivity()).getAppType()==AppConstant.ApplicationType.MENUS)
                     GAUtils.event(GACategory.MENUS, GAAction.CHECKOUT, GAAction.DELIVERY_FROM + GAAction.CLICKED);
                activity.performBackPressed(false);
            }
        });
        rlDeliveryFrom.setMinimumHeight((int)(ASSL.Yscale() * 116f));
        relativeLayoutCash.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutPaytm.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutMobikwik.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutFreeCharge.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutJugnooPay.setOnClickListener(onClickListenerPaymentOptionSelector);
        rlUPI.setOnClickListener(onClickListenerPaymentOptionSelector);
		rlOtherModesToPay.setOnClickListener(onClickListenerPaymentOptionSelector);

        activity.setSelectedPromoCoupon(noSelectionCoupon);




        relativeLayoutDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getTransactionUtils().openDeliveryAddressFragment(activity, activity.getRelativeLayoutContainer());
            }
        });

        activity.buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((type == AppConstant.ApplicationType.MENUS && getSubTotalAmount(false) < activity.getVendorOpened().getMinimumOrderAmount())) {
                    Utils.showToast(activity, getResources().getString(R.string.minimum_order_amount_is_format,
                            Utils.getMoneyDecimalFormatWithoutFloat().format(activity.getVendorOpened().getMinimumOrderAmount())));
                    setSlideInitial();
                } else if (activity.buttonPlaceOrder.getText().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.connection_lost_try_again))) {
                    getCheckoutDataAPI(selectedSubscription);
                } else if (type != AppConstant.ApplicationType.MENUS && activity.getSlotSelected() == null) {
                    product.clicklabs.jugnoo.utils.Utils.showToast(activity, activity.getResources().getString(R.string.please_select_a_delivery_slot));
                    setSlideInitial();
                } else if (addressSelectedNotValid() || TextUtils.isEmpty(activity.getSelectedAddress())) {
                    product.clicklabs.jugnoo.utils.Utils.showToast(activity, activity.getResources().getString(R.string.please_select_a_delivery_address));
                    setSlideInitial();
                } else if (MyApplication.getInstance().getWalletCore().displayAlertAndCheckForSelectedWalletCoupon(activity,
                        activity.getPaymentOption().getOrdinal(), activity.getSelectedPromoCoupon())){
                    activity.setSplInstr(editTextDeliveryInstructions.getText().toString().trim());
                    placeOrder();
                    placeOrderAnalytics();
                } else{
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
                if(linearLayoutCartExpansion.getVisibility() == View.VISIBLE){
                    linearLayoutCartExpansion.setVisibility(View.GONE);
                    imageViewDeleteCart.setVisibility(View.GONE);
                    imageViewCartArrow.setRotation(180f);
                } else {
                    linearLayoutCartExpansion.setVisibility(View.VISIBLE);
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
                activity.setSplInstr(""+s);
            }
        });

        checkoutSaveData = activity.getCheckoutSaveData();
        activity.setSplInstr(checkoutSaveData.getSpecialInstructions());



        linearLayoutCartExpansion.setVisibility(View.VISIBLE);
        imageViewDeleteCart.setVisibility(View.GONE);

        KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(linearLayoutMain, textViewScroll, new KeyboardLayoutListener.KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {
                activity.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            scrollView.scrollTo(0, rootView.findViewById(R.id.linearLayoutDeliveryInstructions).getBottom());
                        } catch (Exception e) {
                        }
                    }
                }, 100);

            }

            @Override
            public void keyBoardClosed() {
                editTextDeliveryInstructions.clearFocus();
            }
        });
        keyboardLayoutListener.setResizeTextView(false);
        linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        checkoutApiDoneOnce = false;

        activity.tvSlide.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xDown = event.getRawX();
                        GAUtils.event(activity.getGaCategory(), CHECKOUT, PAY_SLIDER+STARTED);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if((event.getRawX()-getRelativeSliderLeftMargin()) > (activity.tvSlide.getWidth()/2)
                                && (event.getRawX()-getRelativeSliderLeftMargin()) < activity.relativeLayoutSlider.getWidth()-(activity.tvSlide.getWidth()/2)){
                            paramsF.leftMargin = (int) layoutX(event.getRawX()-getRelativeSliderLeftMargin());
                            activity.relativeLayoutSlider.updateViewLayout(activity.tvSlide, paramsF);
                            activity.sliderText.setVisibility(View.VISIBLE);
                            float percent = (event.getRawX()-getRelativeSliderLeftMargin()) / (activity.relativeLayoutSlider.getWidth()-activity.tvSlide.getWidth());
                            activity.viewAlpha.setAlpha(percent);
                            if(percent > 0.6f){
                                activity.sliderText.setVisibility(View.GONE);
                            } else{
                                activity.sliderText.setVisibility(View.VISIBLE);
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if ((event.getRawX()-getRelativeSliderLeftMargin()) < (activity.relativeLayoutSlider.getWidth()-(activity.tvSlide.getWidth()/2))*0.6f) {
                            setSlideInitial();
                        } else{
                            animateSliderButton(paramsF.leftMargin, activity.relativeLayoutSlider.getWidth()-activity.tvSlide.getWidth());
                            activity.relativeLayoutSlider.setBackgroundResource(R.drawable.capsule_slider_confirm_color_bg);
                            activity.rlSliderContainer.setBackgroundResource(R.color.slider_green);
                            activity.sliderText.setVisibility(View.GONE);
                            activity.viewAlpha.setAlpha(1.0f);
                            activity.buttonPlaceOrder.performClick();
                            GAUtils.event(activity.getGaCategory(), CHECKOUT, PAY_SLIDER+ENDED);
                        }
                        break;
                }

                return true;
            }
        });

        try {
            if(Data.userData.getSlideCheckoutPayEnabled() == 1){
                activity.rlSliderContainer.setVisibility(View.VISIBLE);
                activity.buttonPlaceOrder.setVisibility(View.GONE);
			} else{
                activity.buttonPlaceOrder.setVisibility(View.VISIBLE);
                activity.rlSliderContainer.setVisibility(View.GONE);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        setSlideInitial();


        return rootView;
    }

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
                linearLayoutRoot.setPadding(0, 0, 0, activity.llPayViewContainer.getMeasuredHeight());
            }
        }, 50);
    }

    private void setSlideInitial(){
        animateSliderButton(paramsF.leftMargin, 0);
        activity.rlSliderContainer.setBackgroundResource(R.drawable.bg_rectangle_gradient_normal);
        activity.relativeLayoutSlider.setBackgroundResource(R.drawable.capsule_slider_color_bg);
        activity.sliderText.setVisibility(View.VISIBLE);
        activity.viewAlpha.setAlpha(0.0f);
    }

    private float getRelativeSliderLeftMargin(){
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)activity.relativeLayoutSlider.getLayoutParams();
        return relativeParams.leftMargin;
    }

    private long animDuration = 150;
    private void animateSliderButton(final int currMargin, final float newMargin){
        float diff = newMargin - (float)currMargin;
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

    private float layoutX(float rawX){
        return rawX - sliderButtonWidth()/2f;
    }

    private float sliderButtonWidth(){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) activity.tvSlide.getLayoutParams();
        return (float)params.width;
    }

    private void updateCartUI() {
        editTextDeliveryInstructions.setText(activity.getSpecialInst());

        chargesList.clear();
        chargesList.add(taxSubTotal);

        if(isMenusOpen()){
            totalTaxAmount = 0d;
            for(Charges charges1 : activity.getMenuProductsResponse().getCharges()){
                Tax tax = new Tax(charges1.getText(), getCalculatedCharges(subTotalAmount, charges1, activity.getMenuProductsResponse().getCharges()));
                if(tax.getValue() > 0 || charges1.getForceShow() == 1) {
                    chargesList.add(tax);
                }
                totalTaxAmount = totalTaxAmount + tax.getValue();
            }
        } else {
            chargesList.add(new Tax(activity.getString(R.string.delivery_charges), deliveryCharges()));
        }

        if (totalAmount() > 0 && jcUsed() > 0) {
            chargesList.add(new Tax(activity.getString(R.string.jugnoo_cash), jcUsed()));
        }

        if (getTotalPromoAmount() > 0) {
            chargesList.add(new Tax(activity.getString(R.string.discount), getTotalPromoAmount()));
        }

        if(isFreshOpen()){
            double totalSavings = getTotalSavings();
            if(totalSavings > 0) {
                chargesList.add(new Tax(activity.getString(R.string.total_savings), totalSavings));
            }
        }

        taxTotal.setValue((double)Math.round(payableAmount()));
        chargesList.add(taxTotal);
        chargesAdapter.notifyDataSetChanged();

        if(linearLayoutCartExpansion.getVisibility() == View.VISIBLE){
            textViewCartTotalUndiscount.setVisibility(View.GONE);
        } else {
            textViewCartTotalUndiscount.setVisibility(View.VISIBLE);
            textViewCartTotalUndiscount.setText(activity.getString(R.string.rupees_value_format,
                    Utils.getMoneyDecimalFormat().format(taxTotal.getValue())));
        }

        if(dialogOrderComplete == null || !dialogOrderComplete.isShowing()) {
            if (payableAmount() > 0) {
//                Utils.getDoubleTwoDigits((double)Math.round(payableAmount()));
                activity.buttonPlaceOrder.setText("PAY " + activity.getString(R.string.rupees_value_format,
                        Utils.getDoubleTwoDigits((double)Math.round(payableAmount()))));
                activity.tvSlide.setText("PAY " + activity.getString(R.string.rupees_value_format,
                        Utils.getDoubleTwoDigits((double)Math.round(payableAmount()))));
            } else {
                activity.buttonPlaceOrder.setText(activity.getResources().getString(R.string.place_order));
                activity.tvSlide.setText(activity.getResources().getString(R.string.place_order));
            }
        }

        updateStarLayout();

    }

    private void updateStarLayout(){
        if((type == AppConstant.ApplicationType.MEALS || isMenusOpen())
                && Data.userData != null && Data.userData.isSubscriptionActive()
                && activity.getUserCheckoutResponse() != null
                && activity.getUserCheckoutResponse().getSubscription() != null){
            double totalUndiscounted = isMenusOpen() ? getSubTotalAmount(false) : totalUndiscounted();
            double cashbackValue = activity.getUserCheckoutResponse().getSubscription().getCashback(totalUndiscounted);
            cashbackValue = isMenusOpen() ? Math.round(cashbackValue) : Math.round(totalUndiscounted - Math.round(totalUndiscounted - cashbackValue));
            if(cashbackValue > 0d) {
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
                switch (v.getId()){
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
                }
                GAUtils.event(activity.getGaCategory(), CHECKOUT+WALLET+MODIFIED, String.valueOf(activity.getPaymentOption()));
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
    };



    @Override
    public void onResume() {
        super.onResume();
        try {
            activity.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    orderPaymentModes();
                    setPaymentOptionUI();

                    if(dialogOrderComplete == null || !dialogOrderComplete.isShowing()) {
                        getCheckoutDataAPI(selectedSubscription);
                    }
                }
            }, 150);
            if(Data.userData != null) {
				if (Data.userData.isSubscriptionActive()) {
					cvBecomeStar.setVisibility(View.GONE);
				}
			}


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSubscriptionView() {
        if(Data.userData.getShowSubscriptionData() == 1 && !Data.userData.isSubscriptionActive() && activity.getUserCheckoutResponse().getShowStarSubscriptions() == 1) {
            if(activity.getUserCheckoutResponse().getStarSubscriptionText() != null && !activity.getUserCheckoutResponse().getStarSubscriptionText().equalsIgnoreCase("")){
                tvStarOffer.setText(activity.getUserCheckoutResponse().getStarSubscriptionText());
            } else {
                tvStarOffer.setText(activity.getResources().getString(R.string.become_a_jugnoo_star));
            }

            if(activity.getUserCheckoutResponse().getStarSubscriptionTitle() != null && !activity.getUserCheckoutResponse().getStarSubscriptionTitle().equalsIgnoreCase("")){
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
                    GAUtils.event(activity.getGaCategory(), CHECKOUT+SUBSCRIPTION+MODIFIED, sd.getPlanString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else{
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
            if(apiFetchWalletBalance == null){
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
            apiFetchWalletBalance.getBalance(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void setPaymentOptionUI() {
        try {
            activity.setPaymentOption(MyApplication.getInstance().getWalletCore().getPaymentOptionFromInt(
                    MyApplication.getInstance().getWalletCore().getPaymentOptionAccAvailability(activity.getPaymentOption().getOrdinal())));

            try {
                if(type == AppConstant.ApplicationType.MENUS){
					if(activity.getVendorOpened().getApplicablePaymentMode() == ApplicablePaymentMode.CASH.getOrdinal()){
						activity.setPaymentOption(PaymentOption.CASH);
					} else if(activity.getVendorOpened().getApplicablePaymentMode() == ApplicablePaymentMode.ONLINE.getOrdinal()
							&& activity.getPaymentOption() == PaymentOption.CASH){
						activity.setPaymentOption(PaymentOption.PAYTM);
					}
				} else if(type == AppConstant.ApplicationType.FRESH){
                    if(getPaymentInfoMode() == ApplicablePaymentMode.CASH.getOrdinal()){
                        activity.setPaymentOption(PaymentOption.CASH);
                    } else if(getPaymentInfoMode() == ApplicablePaymentMode.ONLINE.getOrdinal()
                            && activity.getPaymentOption() == PaymentOption.CASH){
                        activity.setPaymentOption(PaymentOption.PAYTM);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            if(promoCouponsAdapter != null && promoCoupons != null && promoCoupons.size() > 0) {
                if(selectAutoSelectedCouponAtCheckout()) {
                    promoCouponsAdapter.notifyDataSetChanged();
                }
            }

            textViewPaytmValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format_without_space), Data.userData.getPaytmBalanceStr()));
            textViewPaytmValue.setTextColor(Data.userData.getPaytmBalanceColor(activity));
            textViewMobikwikValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format_without_space), Data.userData.getMobikwikBalanceStr()));
            textViewMobikwikValue.setTextColor(Data.userData.getMobikwikBalanceColor(activity));
            textViewFreeChargeValue.setText(String.format(activity.getResources()
                    .getString(R.string.rupees_value_format_without_space), Data.userData.getFreeChargeBalanceStr()));
            textViewFreeChargeValue.setTextColor(Data.userData.getFreeChargeBalanceColor(activity));

            if(Data.userData.getPaytmEnabled() == 1){
                textViewPaytmValue.setVisibility(View.VISIBLE);
                imageViewAddPaytm.setVisibility(View.GONE);
            } else{
                textViewPaytmValue.setVisibility(View.GONE);
                imageViewAddPaytm.setVisibility(View.VISIBLE);
            }
            if(Data.userData.getMobikwikEnabled() == 1){
                textViewMobikwikValue.setVisibility(View.VISIBLE);
                imageViewAddMobikwik.setVisibility(View.GONE);
            } else{
                textViewMobikwikValue.setVisibility(View.GONE);
                imageViewAddMobikwik.setVisibility(View.VISIBLE);
            }
            if(Data.userData.getFreeChargeEnabled() == 1){
                textViewFreeChargeValue.setVisibility(View.VISIBLE);
                imageViewAddFreeCharge.setVisibility(View.GONE);
            } else{
                textViewFreeChargeValue.setVisibility(View.GONE);
                imageViewAddFreeCharge.setVisibility(View.VISIBLE);
            }
            if(Data.getPayData() != null && Data.getPayData().getPay().getHasVpa() == 1){
                imageViewAddJugnooPay.setVisibility(View.GONE);
            } else{
                imageViewAddJugnooPay.setVisibility(View.VISIBLE);
            }

			imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_normal);
			imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_normal);
			imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_normal);
			imageViewRadioJugnooPay.setImageResource(R.drawable.ic_radio_button_normal);
			imageViewCashRadio.setImageResource(R.drawable.ic_radio_button_normal);
			ivOtherModesToPay.setImageResource(R.drawable.ic_radio_button_normal);
            ivUPI.setImageResource(R.drawable.ic_radio_button_normal);
            if (activity.getPaymentOption() == PaymentOption.PAYTM) {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_selected);
            } else if (activity.getPaymentOption() == PaymentOption.MOBIKWIK) {
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_selected);
            } else if (activity.getPaymentOption() == PaymentOption.FREECHARGE) {
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_selected);
            } else if (activity.getPaymentOption() == PaymentOption.JUGNOO_PAY) {
                imageViewRadioJugnooPay.setImageResource(R.drawable.ic_radio_button_selected);
            } else if(activity.getPaymentOption() == PaymentOption.RAZOR_PAY){
                if(isRazorUPI){
                    ivUPI.setImageResource(R.drawable.ic_radio_button_selected);
                } else {
                    ivOtherModesToPay.setImageResource(R.drawable.ic_radio_button_selected);
                }
			} else if(activity.getPaymentOption() == PaymentOption.UPI_RAZOR_PAY){
                isRazorUPI = true;
                ivUPI.setImageResource(R.drawable.ic_radio_button_selected);
            } else{
                imageViewCashRadio.setImageResource(R.drawable.ic_radio_button_selected);
            }
            updateCartDataView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void placeOrder() {
        try {
            boolean goAhead = true;
            if (activity.getPaymentOption() == PaymentOption.PAYTM) {
                if (Data.userData.getPaytmBalance() < payableAmount()) {
                    if(Data.userData.getPaytmEnabled() == 0){
                        relativeLayoutPaytm.performClick();
                    } else if (Data.userData.getPaytmBalance() < 0) {
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.paytm_error_cash_select_cash));
                    } else {
                        showWalletBalanceLowDialog(PaymentOption.PAYTM);
                    }
                    goAhead = false;
                }
            }
            else if (activity.getPaymentOption() == PaymentOption.MOBIKWIK) {
                if (Data.userData.getMobikwikBalance() < payableAmount()) {
                    if(Data.userData.getMobikwikEnabled() == 0){
                        relativeLayoutMobikwik.performClick();
                    } else if (Data.userData.getMobikwikBalance() < 0) {
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.mobikwik_error_select_cash));
                    } else {
                        showWalletBalanceLowDialog(PaymentOption.MOBIKWIK);
                    }
                    goAhead = false;
                }
            }
            else if (activity.getPaymentOption() == PaymentOption.FREECHARGE) {
                if (Data.userData.getFreeChargeBalance() < payableAmount()) {
                    if(Data.userData.getFreeChargeEnabled() == 0){
                        relativeLayoutFreeCharge.performClick();
                    } else if (Data.userData.getFreeChargeBalance() < 0) {
                        DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.freecharge_error_case_select_cash));
                    } else {
                        showWalletBalanceLowDialog(PaymentOption.FREECHARGE);
                    }
                    goAhead = false;
                }
            }
            else if (activity.getPaymentOption() == PaymentOption.JUGNOO_PAY) {
                if (Data.getPayData() == null || Data.getPayData().getPay().getHasVpa() != 1) {
                    relativeLayoutJugnooPay.performClick();
                    goAhead = false;
                }
            }
            if (goAhead) {
                activity.buttonPlaceOrder.setEnabled(false);
                if(activity.rlSliderContainer.getVisibility() == View.VISIBLE){
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


    private void placeOrderConfirmation(){
        placeOrderApi();
    }

    private String cartItemsFM(){
        String idKey = Constants.KEY_SUB_ITEM_ID;
        JSONArray jCart = new JSONArray();
        for(SubItem subItem : subItemsInCart){
            if(subItem.getSubItemQuantitySelected() > 0){
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

    private void iterateItems(List<Item> items, JSONArray jCart){
        for(Item item : items){
            if(item.getItemSelectedList().size() > 0){
                for(ItemSelected itemSelected : item.getItemSelectedList()){
                    try {
                        JSONObject jItem = new JSONObject();
                        jItem.put(Constants.KEY_ITEM_ID, itemSelected.getRestaurantItemId());
                        jItem.put(Constants.KEY_QUANTITY, itemSelected.getQuantity());
                        JSONArray jCustomisations = new JSONArray();
                        for(CustomizeItemSelected customizeItemSelected : itemSelected.getCustomizeItemSelectedList()){
                            JSONObject jCustomisation = new JSONObject();
                            jCustomisation.put(Constants.KEY_ID, customizeItemSelected.getCustomizeId());
                            JSONArray jOptions = new JSONArray();
                            for(Integer option : customizeItemSelected.getCustomizeOptions()){
                                JSONObject jOption = new JSONObject();
                                jOption.put(Constants.KEY_ID, option);
                                jOptions.put(jOption);
                            }
                            jCustomisation.put(Constants.KEY_OPTIONS, jOptions);
                            jCustomisations.put(jCustomisation);
                        }
                        if(jCustomisations.length() > 0){
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

    private String cartItemsMenus(){
        JSONArray jCart = new JSONArray();
        if (activity.getMenuProductsResponse() != null
                && activity.getMenuProductsResponse().getCategories() != null) {
            for (Category category : activity.getMenuProductsResponse().getCategories()) {
                if(category.getSubcategories() != null){
                    for(Subcategory subcategory : category.getSubcategories()){
                        iterateItems(subcategory.getItems(), jCart);
                    }
                } else if(category.getItems() != null){
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

                chargeDetails.put("Payment mode", ""+activity.getPaymentOption());
                chargeDetails.put(TOTAL_AMOUNT, ""+getSubTotalAmount(false));
                chargeDetails.put(DISCOUNT_AMOUNT, "" + getTotalPromoAmount());
                if(type != AppConstant.ApplicationType.MENUS) {
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

                if(activity.getPaymentOption().getOrdinal() == PaymentOption.UPI_RAZOR_PAY.getOrdinal()){
                    params.put(Constants.KEY_PAYMENT_MODE, String.valueOf(PaymentOption.RAZOR_PAY.getOrdinal()));
                } else {
                    params.put(Constants.KEY_PAYMENT_MODE, String.valueOf(activity.getPaymentOption().getOrdinal()));
                }
                if(!isMenusOpen()) {
                    params.put(Constants.KEY_DELIVERY_SLOT_ID, String.valueOf(activity.getSlotSelected().getDeliverySlotId()));
                }
                params.put(Constants.KEY_DELIVERY_ADDRESS, String.valueOf(activity.getSelectedAddress()));
                if(activity.getSelectedAddressId() > 0){
                    params.put(Constants.KEY_DELIVERY_ADDRESS_ID, String.valueOf(activity.getSelectedAddressId()));
                    params.put(Constants.KEY_DELIVERY_ADDRESS_TYPE, String.valueOf(activity.getSelectedAddressType()));
                }
                params.put(Constants.KEY_DELIVERY_NOTES, String.valueOf(activity.getSpecialInst()));
                if(activity.getAppType() == AppConstant.ApplicationType.MEALS){
                    params.put(Constants.KEY_CLIENT_ID, Config.getMealsClientId());
                } else if(activity.getAppType() == AppConstant.ApplicationType.MENUS){
                    params.put(Constants.KEY_CLIENT_ID, Config.getMenusClientId());
                } else {
                    params.put(Constants.KEY_CLIENT_ID, Config.getFreshClientId());
                }
                if(type == AppConstant.ApplicationType.MENUS){
                    params.put(Constants.KEY_CART, cartItemsMenus());
                } else {
                    params.put(Constants.KEY_CART, cartItemsFM());
                }
                if(activity.getSelectedPromoCoupon() != null && activity.getSelectedPromoCoupon().getId() > -1){
                    if(activity.getSelectedPromoCoupon() instanceof CouponInfo){
                        params.put(Constants.KEY_ACCOUNT_ID, String.valueOf(activity.getSelectedPromoCoupon().getId()));
                    } else if(activity.getSelectedPromoCoupon() instanceof PromotionInfo){
                        params.put(Constants.KEY_ORDER_OFFER_ID, String.valueOf(activity.getSelectedPromoCoupon().getId()));
                    }
                    params.put(Constants.KEY_MASTER_COUPON, String.valueOf(activity.getSelectedPromoCoupon().getMasterCoupon()));
                    GAUtils.event(activity.getGaCategory(), CHECKOUT+OFFER+SELECTED, activity.getSelectedPromoCoupon().getTitle());
                }
                try {
                    chargeDetails.put(COUPONS_USED, activity.getSelectedPromoCoupon().getTitle());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if(type == AppConstant.ApplicationType.MEALS) {
                    params.put("store_id", "2");
                    params.put("group_id", ""+activity.getProductsResponse().getCategories().get(0).getSubItems().get(0).getGroupId());
                    chargeDetails.put(TYPE, "Meals");
                } else if(type == AppConstant.ApplicationType.GROCERY) {
                    chargeDetails.put(TYPE, "Grocery");
                } else if(type == AppConstant.ApplicationType.MENUS) {
                    chargeDetails.put(TYPE, "Menus");
                } else {
                    chargeDetails.put(TYPE, "Fresh");
                }
                params.put(Constants.INTERATED, "1");

                if(type == AppConstant.ApplicationType.MENUS){
                    params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(activity.getVendorOpened().getRestaurantId()));
                }

                if(selectedSubscription != null) {
                    JSONObject jItem = new JSONObject();
                    jItem.put(Constants.KEY_SUBSCRIPTION_ID, selectedSubscription.getId());
                    params.put(Constants.KEY_SUBSCRIPTION_INFO, jItem.toString());
                }


                Log.i(TAG, "getAllProducts params=" + params.toString());
                if(activity.getSlotSelected() != null) {
                    GAUtils.event(activity.getGaCategory(), CHECKOUT + DELIVERY_SLOT + SELECTED, activity.getSlotSelected().getDayName() + " " + activity.getSlotSelected().getTimeSlotDisplay());
                }
                GAUtils.event(activity.getGaCategory(), CHECKOUT+WALLET+SELECTED, String.valueOf(activity.getPaymentOption()));
                if(!TextUtils.isEmpty(activity.getSpecialInst())){
                    GAUtils.event(activity.getGaCategory(), CHECKOUT, NOTES+ADDED);
                }
                if(type == AppConstant.ApplicationType.FRESH){
                    params.put(Constants.KEY_VENDOR_ID, String.valueOf(activity.getOpenedVendorId()));
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
                                    if(jObj.has(Constants.KEY_PAYMENT_OBJECT)){
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
                                    } else if(jObj.has(Constants.KEY_RAZORPAY_PAYMENT_OBJECT)){
                                        // razor pay case send data to RazorPay Checkout page
                                        activity.setPlaceOrderResponse(placeOrderResponse);
                                        activity.startRazorPayPayment(jObj.getJSONObject(Constants.KEY_RAZORPAY_PAYMENT_OBJECT), isRazorUPI);
                                        doSlideInitial = false;
                                    } else {
                                        paramsPlaceOrder = params;
                                        orderPlacedSuccess(placeOrderResponse);
                                        doSlideInitial = false;
                                    }
                                } else if (ApiResponseFlags.USER_IN_DEBT.getOrdinal() == flag) {
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
                                            getCheckoutDataAPI(selectedSubscription);
                                        }
                                    });
                                } else {
                                    final int validStockCount = jObj.optInt(Constants.KEY_VALID_STOCK_COUNT, -1);
                                    if(validStockCount > -1){
                                        DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    String fragName;
                                                    if(validStockCount == 0){
                                                        if(type == AppConstant.ApplicationType.MEALS){
                                                            if(activity.isMealAddonItemsAvailable()){
                                                                fragName = MealAddonItemsFragment.class.getName();
                                                            } else {
                                                                fragName = FreshCheckoutMergedFragment.class.getName();
                                                            }
                                                        } else if(type == AppConstant.ApplicationType.GROCERY){
                                                            fragName = FreshCheckoutMergedFragment.class.getName();
                                                        } else if(type == AppConstant.ApplicationType.MENUS){
                                                            fragName = FreshCheckoutMergedFragment.class.getName();
                                                        }else {
                                                            fragName = FreshCheckoutMergedFragment.class.getName();
                                                        }
                                                    } else {
                                                        if(type == AppConstant.ApplicationType.MEALS && activity.isMealAddonItemsAvailable()){
                                                            fragName = MealAddonItemsFragment.class.getName();
                                                        } else if(type == AppConstant.ApplicationType.MENUS){
                                                            fragName = FreshCheckoutMergedFragment.class.getName();
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
                                        if(type == AppConstant.ApplicationType.MENUS && outOfRange == 1){
                                            outOfRangeDialog(message);
                                        } else {
                                            DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (emptyCart == 1) {
                                                        clearMenusCartAndGoToMenusFragment();
                                                    } else if (type == AppConstant.ApplicationType.MENUS
                                                            && ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag
                                                            && isEmpty == 1) {
                                                        activity.clearMenusCart();
                                                        activity.setRefreshCart(true);
                                                        activity.performBackPressed(false);
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
                        if(doSlideInitial){
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
                if(type == AppConstant.ApplicationType.MENUS){
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
            if(params != null) {
                for (String key : params.keySet()) {
                    bundle.putString(key, params.get(key));
                }
            }
            bundle.putString("order_id", String.valueOf(placeOrderResponse.getOrderId()));
            bundle.putString("amount", String.valueOf(placeOrderResponse.getAmount()));

            if (type == AppConstant.ApplicationType.MENUS){
				bundle.putString("product_type", "Menus");
			} else if (type ==AppConstant.ApplicationType.MEALS){
				bundle.putString("product_type", "Meals");
			} else if (type == AppConstant.ApplicationType.FRESH) {
				bundle.putString("product_type", "Fresh");
			}

            MyApplication.getInstance().getAppEventsLogger().logPurchase(
					BigDecimal.valueOf(placeOrderResponse.getAmount()),
					Currency.getInstance("INR"),
					bundle
			);
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
                        if(apiHit == 1)
                            placeOrderApi();
                        else if(apiHit == 0) {
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

    private void orderPlacedSuccess(PlaceOrderResponse placeOrderResponse){
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiverWalletUpdate);
        orderPlaced = true;
        activity.saveCheckoutData(true);
        long time = 0L;
        Prefs.with(activity).save(SPLabels.CHECK_BALANCE_LAST_TIME, time);
        activity.resumeMethod();

        String deliverySlot = "", deliveryDay = "";
        boolean showDeliverySlot = true;
        if(type == AppConstant.ApplicationType.MENUS){
            showDeliverySlot = false;
        } else {
            deliverySlot = DateOperations.convertDayTimeAPViaFormat(activity.getSlotSelected().getStartTime())
                    + " - " + DateOperations.convertDayTimeAPViaFormat(activity.getSlotSelected().getEndTime());
            deliveryDay = activity.getSlotSelected().getDayName();
        }
        String restaurantName = "";
        if(type == AppConstant.ApplicationType.MENUS && activity.getVendorOpened() != null){
            restaurantName = activity.getVendorOpened().getName();
        }

        if(placeOrderResponse.getSubscriptionDataPlaceOrder() != null) {
            if (placeOrderResponse.getSubscriptionDataPlaceOrder().getUserSubscriptions() != null && placeOrderResponse.getSubscriptionDataPlaceOrder().getUserSubscriptions().size() > 0) {
                Data.userData.getSubscriptionData().setUserSubscriptions(placeOrderResponse.getSubscriptionDataPlaceOrder().getUserSubscriptions());
                Data.autoData.setCancellationChargesPopupTextLine1(placeOrderResponse.getSubscriptionDataPlaceOrder().getCancellationChargesPopupTextLine1());
                Data.autoData.setCancellationChargesPopupTextLine2(placeOrderResponse.getSubscriptionDataPlaceOrder().getCancellationChargesPopupTextLine2());
            }
        }
        int productType;
        if(type == AppConstant.ApplicationType.MEALS){
            productType = ProductType.MEALS.getOrdinal();
        } else if(type == AppConstant.ApplicationType.MENUS){
            productType = ProductType.MENUS.getOrdinal();
        } else {
            productType = ProductType.FRESH.getOrdinal();
        }
        if(placeOrderResponse.getReferralPopupContent() == null){
            dialogOrderComplete = new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
                @Override
                public void onDismiss() {
                    activity.orderComplete();
                }
            }).show(String.valueOf(placeOrderResponse.getOrderId()),
                    deliverySlot, deliveryDay, showDeliverySlot, restaurantName,
                    placeOrderResponse);
            GAUtils.trackScreenView(productType+ORDER_PLACED);
        } else {
            dialogOrderComplete = new OrderCompleteReferralDialog(activity, new OrderCompleteReferralDialog.Callback() {
                @Override
                public void onDialogDismiss() {
                    activity.orderComplete();
                }

                @Override
                public void onConfirmed() {
                    activity.orderComplete();
                }
            }).show(true, deliverySlot, deliveryDay,
                    activity.getResources().getString(R.string.thank_you_for_placing_order_menus_format, restaurantName),
                    placeOrderResponse.getReferralPopupContent(),
                    -1, placeOrderResponse.getOrderId(), productType);
            GAUtils.trackScreenView(productType+ORDER_PLACED+REFERRAL);
        }
        activity.clearAllCartAtOrderComplete();
        activity.setSelectedPromoCoupon(noSelectionCoupon);
        flurryEventPlaceOrder(placeOrderResponse);
        fbPurchasedEvent(paramsPlaceOrder, placeOrderResponse);
    }

    private void flurryEventPlaceOrder(PlaceOrderResponse placeOrderResponse){
        try {
            chargeDetails.put("Charged ID", placeOrderResponse.getOrderId());
            MyApplication.getInstance().charged(chargeDetails, items);
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
				parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "INR");
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
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_paytm_balance, amount, R.drawable.ic_paytm_big);
            }
            else if (paymentOption == PaymentOption.MOBIKWIK && Data.userData.getMobikwikEnabled() == 1) {
                String amount = Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getMobikwikBalance() - Math.ceil(payableAmount())));
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_mobikwik_balance, amount, R.drawable.ic_mobikwik_big);
            }
            else if (paymentOption == PaymentOption.FREECHARGE && Data.userData.getFreeChargeEnabled() == 1) {
                String amount = Utils.getMoneyDecimalFormat().format(Math.ceil(Data.userData.getFreeChargeBalance() - Math.ceil(payableAmount())));
                new FreshWalletBalanceLowDialog(activity, callback).show(R.string.dont_have_enough_freecharge_balance, amount, R.drawable.ic_freecharge_big);
            }
            else {
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
                        (Data.userData.getPaytmEnabled() == 1)? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        dfNoDecimal.format(Math.ceil(payableAmount()
                                - Data.userData.getPaytmBalance())));
            }
            else if (paymentOption == PaymentOption.MOBIKWIK) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getMobikwikEnabled() == 1)? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        dfNoDecimal.format(Math.ceil(payableAmount()
                                - Data.userData.getMobikwikBalance())));
            }
            else if (paymentOption == PaymentOption.FREECHARGE) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getFreeChargeEnabled() == 1)? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        dfNoDecimal.format(Math.ceil(payableAmount()
                                - Data.userData.getFreeChargeBalance())));
            }
            else if (paymentOption == PaymentOption.JUGNOO_PAY) {
                intent.setClass(activity, MainActivity.class);
                intent.putExtra(Constants.KEY_GO_BACK, 1);
            }
            else {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
            }
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
            onResume();
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



    private void orderPaymentModes(){
        try{
            ArrayList<PaymentModeConfigData> paymentModeConfigDatas = MyApplication.getInstance().getWalletCore().getPaymentModeConfigDatas(Data.userData);
            if(paymentModeConfigDatas != null && paymentModeConfigDatas.size() > 0){
                linearLayoutWalletContainer.removeAllViews();
                for(PaymentModeConfigData paymentModeConfigData : paymentModeConfigDatas){
                    if(paymentModeConfigData.getEnabled() == 1) {
                        if (paymentModeConfigData.getPaymentOption() == PaymentOption.PAYTM.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutPaytm);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.MOBIKWIK.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutMobikwik);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.FREECHARGE.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutFreeCharge);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.CASH.getOrdinal()) {
                            linearLayoutWalletContainer.addView(relativeLayoutCash);
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.RAZOR_PAY.getOrdinal()){
                            linearLayoutWalletContainer.addView(rlOtherModesToPay);
                            tvOtherModesToPay.setText(paymentModeConfigData.getDisplayName());
                        } else if (paymentModeConfigData.getPaymentOption() == PaymentOption.UPI_RAZOR_PAY.getOrdinal()){
                            linearLayoutWalletContainer.addView(rlUPI);
                            tvUPI.setText(paymentModeConfigData.getDisplayName());
                        }
                    }
                }

                // for pay only in fresh and meals
                if (type != AppConstant.ApplicationType.MENUS
                        && Data.getPayData() != null && Data.userData.getPayEnabled() == 1) {
                    linearLayoutWalletContainer.addView(relativeLayoutJugnooPay);
                }

            }


        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            if(type == AppConstant.ApplicationType.MENUS){
				setPaymentOptionVisibility(activity.getVendorOpened().getApplicablePaymentMode());
			} else if(type == AppConstant.ApplicationType.FRESH){
                setPaymentOptionVisibility(getPaymentInfoMode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getPaymentInfoMode(){
        if(activity.getUserCheckoutResponse() != null && activity.getUserCheckoutResponse().getPaymentInfo().getApplicablePaymentMode() != null){
            return activity.getUserCheckoutResponse().getPaymentInfo().getApplicablePaymentMode().intValue();
        } else{
            return ApplicablePaymentMode.BOTH.getOrdinal();
        }
    }

    private void setPaymentOptionVisibility(int applicablePaymentMode){
        if(applicablePaymentMode == ApplicablePaymentMode.CASH.getOrdinal()){
            relativeLayoutPaytm.setVisibility(View.GONE);
            relativeLayoutMobikwik.setVisibility(View.GONE);
            relativeLayoutFreeCharge.setVisibility(View.GONE);
            relativeLayoutJugnooPay.setVisibility(View.GONE);
            rlOtherModesToPay.setVisibility(View.GONE);
            rlUPI.setVisibility(View.GONE);
        } else if(applicablePaymentMode == ApplicablePaymentMode.ONLINE.getOrdinal()){
            relativeLayoutCash.setVisibility(View.GONE);
        }
    }

    private void filterCouponsByApplicationPaymentMode(int applicablePaymentMode, ProductType productType){
        if (promoCoupons == null) {
            promoCoupons = new ArrayList<>();
        }
        promoCoupons.clear();
        ArrayList<PromoCoupon> promoCouponsList = Data.userData.getCoupons(productType);
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

    private void updateCouponsDataView(){
        try {
            if (type == AppConstant.ApplicationType.MEALS) {
                promoCoupons = Data.userData.getCoupons(ProductType.MEALS);
            } else if (type == AppConstant.ApplicationType.GROCERY) {
                promoCoupons = Data.userData.getCoupons(ProductType.GROCERY);
            } else if (type == AppConstant.ApplicationType.MENUS) {
                filterCouponsByApplicationPaymentMode(activity.getVendorOpened().getApplicablePaymentMode(), ProductType.MENUS);
            } else {
                filterCouponsByApplicationPaymentMode(getPaymentInfoMode(), ProductType.FRESH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(promoCoupons != null) {

            if(promoCoupons.size() > 0){
                linearLayoutOffers.setVisibility(View.VISIBLE);
            } else {
                linearLayoutOffers.setVisibility(View.GONE);
            }
            PromoCoupon pcOld = selectServerMarkedCouponAndReturnOld();

            // to handle if coupon manually selected and refresh on cart change is 1
            // but server is not sending any pre selected coupon
            // so revert back to old coupon
            if(couponManuallySelected
                    && activity.getUserCheckoutResponse() != null
                    && activity.getUserCheckoutResponse().getRefreshOnCartChange() == 1
                    && pcOld != null && pcOld.getId() > 0
                    && noSelectionCoupon.matchPromoCoupon(activity.getSelectedPromoCoupon())
                    && pcOld.getIsValid() == 1){
                activity.setSelectedPromoCoupon(pcOld);
            }

            setPromoAmount();
            promoCouponsAdapter.setList(promoCoupons);

            // to check if user has selected some promo coupon from promotions screen
            if(!selectAutoSelectedCouponAtCheckout()) {
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
        }
    }

    /**
     * To auto apply a selected coupon from Promotions screen
     * @return returns true if some coupon is selected or can't be selected else false
     */
    private boolean selectAutoSelectedCouponAtCheckout(){
        String clientId = Config.getFreshClientId();
        if(type == AppConstant.ApplicationType.MEALS){
            clientId = Config.getMealsClientId();
        } else if(type == AppConstant.ApplicationType.MENUS){
            clientId = Config.getMenusClientId();
        }

        boolean couponSelected = false;
        try {
            int promoCouponId = Prefs.with(activity).getInt(Constants.SP_USE_COUPON_+clientId, -1);
            boolean isCouponInfo = Prefs.with(activity).getBoolean(Constants.SP_USE_COUPON_IS_COUPON_ + clientId, false);
            if(promoCouponId > 0){
				for(int i=0; i<promoCoupons.size(); i++){
                    PromoCoupon pc = promoCoupons.get(i);
                    if(((isCouponInfo && pc instanceof CouponInfo) || (!isCouponInfo && pc instanceof PromotionInfo))
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

    private PromoCoupon selectServerMarkedCouponAndReturnOld(){
        if(activity.getUserCheckoutResponse() != null
                && activity.getUserCheckoutResponse().getRefreshOnCartChange() == 1) {
            PromoCoupon pcOld = activity.getSelectedPromoCoupon();
            activity.setSelectedPromoCoupon(noSelectionCoupon);

            for (PromoCoupon promoCoupon : promoCoupons) {
                if (pcOld.getId() == promoCoupon.getId()) {
                    pcOld = promoCoupon;
                    break;
                }
            }

            for (PromoCoupon promoCoupon : promoCoupons) {
                if (promoCoupon.getIsSelected() == 1) {
                    activity.setSelectedPromoCoupon(promoCoupon);
                    break;
                }
            }
            return pcOld;
        } else if(activity.getUserCheckoutResponse() != null){
            PromoCoupon pcOld = activity.getSelectedPromoCoupon();
            if(promoCoupons.size() == 0){
                activity.setSelectedPromoCoupon(noSelectionCoupon);
            }
            return pcOld;
        }
        return null;
    }

    private void setPromoAmount(){
        try {
            if(activity.getSelectedPromoCoupon() != null && activity.getSelectedPromoCoupon().getId() > -1){
                promoAmount = activity.getSelectedPromoCoupon().getDiscount() != null
                        && activity.getSelectedPromoCoupon().getDiscount() > 0.0 ? activity.getSelectedPromoCoupon().getDiscount() : 0;
            } else{
                promoAmount = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            promoAmount = 0;
        }
    }

    private double getTotalPromoAmount(){
        if(activity.getUserCheckoutResponse() != null && activity.getUserCheckoutResponse().getSubscription() != null) {
            return promoAmount + activity.getUserCheckoutResponse().getSubscription().getDiscount(totalUndiscounted());
        } else {
            return promoAmount;
        }
    }


    @Override
    public void onSlotSelected(int position, Slot slot) {
        if(type == AppConstant.ApplicationType.MEALS
                && activity.getProductsResponse().getDeliveryInfo().getDynamicDeliveryCharges() == 1){
            selectedSlot = slot.getDeliverySlotId();
            getCheckoutDataAPI(selectedSubscription);
        } else{
            activity.setSlotSelected(slot);
            recyclerViewDeliverySlots.scrollToPosition(position);
        }
        GAUtils.event(activity.getGaCategory(), CHECKOUT+DELIVERY_SLOT+MODIFIED, slot.getDayName()+" "+slot.getTimeSlotDisplay());
    }


    public void getCheckoutDataAPI(SubscriptionData.Subscription subscription) {
        try {
            if (MyApplication.getInstance().isOnline()) {

                boolean loaderShown = false;
                if(!DialogPopup.isDialogShowing()) {
                    DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
                    loaderShown = true;
                }
                final boolean finalLoaderShown = loaderShown;

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                if(deliveryAddressUpdated){
                    params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                    params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));
                } else {
                    params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getMenuRefreshLatLng().latitude));
                    params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getMenuRefreshLatLng().longitude));
                }

                params.put(Constants.KEY_CURRENT_LATITUDE, String.valueOf(Data.latitude));
                params.put(Constants.KEY_CURRENT_LONGITUDE, String.valueOf(Data.longitude));


                if(isMenusOpen()){
                    params.put(Constants.KEY_CART, cartItemsMenus());
                } else {
                    params.put(Constants.KEY_CART, cartItemsFM());
                }
                params.put(Constants.ORDER_AMOUNT, Utils.getMoneyDecimalFormat().format(getSubTotalAmount(false)));


                if(type == AppConstant.ApplicationType.MEALS) {
                    params.put(Constants.STORE_ID, ""+ Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType));
                    params.put(Constants.GROUP_ID, ""+activity.getProductsResponse().getCategories().get(0).getCurrentGroupId());
                } else if(isMenusOpen()){
                    params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(activity.getVendorOpened().getRestaurantId()));
                    String data = new Gson().toJson(activity.getVendorOpened(), MenusResponse.Vendor.class);
                    params.put(Constants.KEY_RESTAURANT_DATA, data);
                }

                params.put(Constants.INTERATED, "1");
                if(type == AppConstant.ApplicationType.MEALS){
                    params.put(Constants.KEY_CLIENT_ID, Config.getMealsClientId());
                } else if(isMenusOpen()){
                    params.put(Constants.KEY_CLIENT_ID, Config.getMenusClientId());
                } else {
                    params.put(Constants.KEY_CLIENT_ID, Config.getFreshClientId());
                }

                if(selectedSlot != -1) {
                    params.put(Constants.KEY_USER_SELECTED_SLOT, String.valueOf(selectedSlot));
                }

                if(subscription != null) {
                    JSONObject jItem = new JSONObject();
                    jItem.put(Constants.KEY_SUBSCRIPTION_ID, selectedSubscription.getId());
                    params.put(Constants.KEY_SUBSCRIPTION_INFO, jItem.toString());
                }

                if(type == AppConstant.ApplicationType.FRESH){
                    params.put(Constants.KEY_VENDOR_ID, String.valueOf(activity.getOpenedVendorId()));
                }

                Log.i(TAG, "getAllProducts params=" + params.toString());

                Callback<UserCheckoutResponse> callback = new Callback<UserCheckoutResponse>() {
                    @Override
                    public void success(UserCheckoutResponse userCheckoutResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt(Constants.KEY_FLAG);
                                if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

                                    if(FreshCheckoutMergedFragment.this.type == AppConstant.ApplicationType.FRESH
                                            && userCheckoutResponse.getCityId() != null
                                            && activity.checkForCityChange(userCheckoutResponse.getCityId(),
                                            new FreshActivity.CityChangeCallback() {
                                                @Override
                                                public void onYesClick() {
                                                    activity.refreshCart2 = true;
                                                    if(activity.getFreshFragment() != null) {
                                                        activity.getSupportFragmentManager().popBackStack(FreshFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                                    } else {
                                                        activity.getSupportFragmentManager().popBackStack(FreshCheckoutMergedFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                                    }
                                                }

                                                @Override
                                                public void onNoClick() {
                                                    getCheckoutDataAPI(selectedSubscription);
                                                }
                                            })) {
                                    } else {
                                        activity.setUserCheckoutResponse(userCheckoutResponse);
                                        if(activity.getUserCheckoutResponse() != null
                                                && activity.getUserCheckoutResponse().getSubscriptionInfo() != null
                                                && activity.getUserCheckoutResponse().getSubscriptionInfo().getSubscriptionId() == null) {
                                            Utils.showToast(activity, activity.getResources().getString(R.string.star_could_not_be_added));
                                        }
                                        setSubscriptionView();
                                        if(isMenusOpen()) {
                                            getSubscriptionFromCheckout(userCheckoutResponse);
                                            if(userCheckoutResponse.getCharges() != null){
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

                                        if(type == AppConstant.ApplicationType.MENUS
                                                && userCheckoutResponse.getRestaurantInfo() != null
                                                && !TextUtils.isEmpty(userCheckoutResponse.getRestaurantInfo().getAddress())){
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
                                        if(type == AppConstant.ApplicationType.FRESH){
                                            setPaymentOptionVisibility(getPaymentInfoMode());
                                        }

                                        try {
                                            if (cartChangedRefreshCheckout) {
                                                setSelectedCoupon(promoCoupons.indexOf(activity.getSelectedPromoCoupon()));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        cartChangedRefreshCheckout = false;
                                    }
                                } else{
                                    setSlideInitial();
                                    final int redirect = jObj.optInt(Constants.KEY_REDIRECT, 0);
                                    final int emptyCart = jObj.optInt(Constants.KEY_EMPTY_CART, 0);
                                    final int outOfRange = jObj.optInt(Constants.KEY_OUT_OF_RANGE, 0);
                                    if(type == AppConstant.ApplicationType.MENUS && outOfRange == 1){
                                        outOfRangeDialog(message);
                                    } else {
                                        DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(emptyCart == 1){
                                                    clearMenusCartAndGoToMenusFragment();
                                                } else if(redirect == 1) {
                                                    if(activity.getFreshSearchFragment() != null){
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
                            retryDialog(DialogErrorType.SERVER_ERROR);
                        }
                        if(finalLoaderShown) {
                            DialogPopup.dismissLoadingDialog();
                        }
                        linearLayoutMain.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
                        if(finalLoaderShown) {
                            DialogPopup.dismissLoadingDialog();
                        }
                        retryDialog(DialogErrorType.CONNECTION_LOST);
                    }
                };
                new HomeUtil().putDefaultParams(params);
                if(isMenusOpen()){
                    RestClient.getMenusApiService().userCheckoutData(params, callback);
                } else {
                    RestClient.getFreshApiService().userCheckoutData(params, callback);
                }
            } else {
                retryDialog(DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void updateDeliverySlot(UserCheckoutResponse.DeliveryInfo deliveryInfo){
        if(deliveryInfo != null) {
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
//        try {
//            if(!activity.isAddressConfirmed() && TextUtils.isEmpty(activity.getSelectedAddressType())) {
//                if (userCheckoutResponse.getCheckoutData().getLastAddress() != null) {
//                    activity.setSelectedAddress(userCheckoutResponse.getCheckoutData().getLastAddress());
//                    activity.setSelectedAddressType(userCheckoutResponse.getCheckoutData().getLastAddressType());
//                    activity.setSelectedAddressId(userCheckoutResponse.getCheckoutData().getLastAddressId());
//                    try {
//                        activity.setSelectedLatLng(new LatLng(Double.parseDouble(userCheckoutResponse.getCheckoutData().getLastAddressLatitude()),
//                                Double.parseDouble(userCheckoutResponse.getCheckoutData().getLastAddressLongitude())));
//                        activity.setRefreshCart(true);
//                        deliveryAddressUpdated = true;
//                        if(!checkoutApiDoneOnce) {
//                            activity.getHandler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    getCheckoutDataAPI(selectedSubscription);
//                                }
//                            }, 500);
//                        }
//                        checkoutApiDoneOnce = true;
//                    } catch (Exception e) {
//                    }
//                } else {
//                    activity.setSelectedAddress("");
//                    activity.setSelectedLatLng(null);
//                    activity.setSelectedAddressId(0);
//                    activity.setSelectedAddressType("");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void retryDialog(DialogErrorType dialogErrorType) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getCheckoutDataAPI(selectedSubscription);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }


    private void updateCartFromCheckoutData(UserCheckoutResponse userCheckoutResponse){
        if(userCheckoutResponse.getCartItems() != null){
            boolean cartChanged = false;
            for(int i=0; i<subItemsInCart.size(); i++){
                SubItem si = subItemsInCart.get(i);
                int index = userCheckoutResponse.getCartItems().indexOf(userCheckoutResponse.new CartItem(si.getSubItemId()));
                if(index > -1){
                    UserCheckoutResponse.CartItem cartItem = userCheckoutResponse.getCartItems().remove(index);
                    if(cartItem.getStatus() < 0 || cartItem.getQuantity() <= 0){
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
            if(cartChanged) {
                activity.saveCartList(subItemsInCart);
                freshCartItemsAdapter.notifyDataSetChanged();
                activity.setCartChangedAtCheckout(true);
            }
        }

        getSubscriptionFromCheckout(userCheckoutResponse);
    }

    private void getSubscriptionFromCheckout(UserCheckoutResponse userCheckoutResponse){
        if(Data.userData.getShowSubscriptionData() == 1 && !Data.userData.isSubscriptionActive() && userCheckoutResponse.getShowStarSubscriptions() == 1) {
            if (userCheckoutResponse.getSubscriptionInfo() != null && userCheckoutResponse.getSubscriptionInfo().getSubscriptionId() != null) {
                if(isMenusOpen()){
                    menusCartItemsAdapter.setResults(itemsInCart, userCheckoutResponse.getSubscriptionInfo());
                } else {
                    freshCartItemsAdapter.setResults(subItemsInCart, userCheckoutResponse.getSubscriptionInfo());
                }
                cvBecomeStar.setVisibility(View.GONE);
            } else {
                if(isMenusOpen()){
                    menusCartItemsAdapter.setResults(itemsInCart, userCheckoutResponse.getSubscriptionInfo());
                } else {
                    freshCartItemsAdapter.setResults(subItemsInCart, null);
                }
                cvBecomeStar.setVisibility(View.VISIBLE);
            }
        } else{
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

            if(activity.getSlotSelected() != null) {
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

            if(slots.size() == 0){
                textViewNoDeliverySlot.setVisibility(View.VISIBLE);
            } else {
                textViewNoDeliverySlot.setVisibility(View.GONE);
            }
            deliverySlotsAdapter.notifyDataSetChanged();
        }
    }

    private double getTotalSavings(){
        double totalSavings = 0d;
        try {
            for(SubItem subItems : subItemsInCart){
                if(!TextUtils.isEmpty(subItems.getOldPrice())) {
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

    public boolean addressSelectedNotValid(){
        return TextUtils.isEmpty(activity.getSelectedAddressType());
    }

    public void updateAddressView(){
        try {
            if (TextUtils.isEmpty(activity.getSelectedAddress())) {
                setActivityLastAddressFromResponse(activity.getUserCheckoutResponse());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageViewAddressType.setImageResource(R.drawable.ic_loc_other);
        imageViewAddressType.setPadding(0,0,0,0);
        textViewAddressName.setVisibility(View.GONE);
        textViewAddressValue.setTextColor(activity.getResources().getColor(R.color.text_color));
        if(!addressSelectedNotValid() && !TextUtils.isEmpty(activity.getSelectedAddress())) {
            textViewAddressValue.setVisibility(View.VISIBLE);          tvNoAddressAlert.setVisibility(View.GONE);
            imageViewDeliveryAddressForward.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_back_pay_selector));
            imageViewDeliveryAddressForward.setVisibility(View.VISIBLE);
            textViewAddressValue.setText(activity.getSelectedAddress());
            imageViewAddressType.setImageResource(R.drawable.ic_loc_other);
            if(!TextUtils.isEmpty(activity.getSelectedAddressType())){
                textViewAddressName.setVisibility(View.VISIBLE);
                textViewAddressValue.setTextColor(activity.getResources().getColor(R.color.text_color_light));
                if(activity.getSelectedAddressType().equalsIgnoreCase(activity.getString(R.string.home))){
                    imageViewAddressType.setImageResource(R.drawable.ic_home);
                    textViewAddressName.setText(activity.getString(R.string.home));
                }
                else if(activity.getSelectedAddressType().equalsIgnoreCase(activity.getString(R.string.work))){
                    imageViewAddressType.setImageResource(R.drawable.ic_work);
                    textViewAddressName.setText(activity.getString(R.string.work));
                }
                else {
                    imageViewAddressType.setImageResource(R.drawable.ic_loc_other);
                    textViewAddressName.setText(activity.getSelectedAddressType());
                }
            }
        } else {
            textViewAddressValue.setText(activity.getResources().getString(R.string.add_address));
            imageViewAddressType.setImageResource(R.drawable.ic_exclamation_address);
            imageViewDeliveryAddressForward.getDrawable().mutate().setColorFilter(ContextCompat.getColor(activity,R.color.red_alert_no_address), PorterDuff.Mode.SRC_ATOP);
            int padding = activity.getResources().getDimensionPixelSize(R.dimen.dp_2);
            imageViewAddressType.setPadding(padding,padding,padding,padding);
            textViewAddressValue.setVisibility(View.GONE);
            tvNoAddressAlert.setVisibility(View.VISIBLE);
//            imageViewDeliveryAddressForward.setVisibility(View.GONE);


        }
    }


    @Override
    public void onPlusClicked(int position, SubItem subItem) {
        if(!cartChangedRefreshCheckout){
            GAUtils.event(activity.getGaCategory(), CHECKOUT, CART+ITEM+MODIFIED);
        }
        activity.saveSubItemToDeliveryStoreCart(subItem);
        activity.setCartChangedAtCheckout(true);
        editTextDeliveryInstructions.clearFocus();
        cartChangedRefreshCheckout = true;
        updateCartDataView();
        if(type == AppConstant.ApplicationType.MEALS
                && activity.getProductsResponse().getDeliveryInfo().getDynamicDeliveryCharges() == 1){
            getCheckoutDataAPI(selectedSubscription);
        } else {
            if(!rehitCheckoutApi()){
                removeCouponWithCheck();
            }
        }
        GAUtils.event(activity.getGaCategory(), CHECKOUT, CART+ITEM+INCREASED);
    }

    @Override
    public void onMinusClicked(int position, SubItem subItem) {
        if(!cartChangedRefreshCheckout){
            GAUtils.event(activity.getGaCategory(), CHECKOUT, CART+ITEM+MODIFIED);
        }
        editTextDeliveryInstructions.clearFocus();
        activity.setCartChangedAtCheckout(true);
        cartChangedRefreshCheckout = true;
        if(subItem.getSubItemQuantitySelected() == 0){
            subItemsInCart.remove(position);
        }
        activity.saveSubItemToDeliveryStoreCart(subItem);

        checkIfEmpty();
        updateCartDataView();
        if(subItemsInCart.size() > 0
                && type == AppConstant.ApplicationType.MEALS
                && activity.getProductsResponse().getDeliveryInfo().getDynamicDeliveryCharges() == 1){
            getCheckoutDataAPI(selectedSubscription);
        } else if(subItemsInCart.size() > 0) {
            if(!rehitCheckoutApi()){
                removeCouponWithCheck();
            }
        }
        GAUtils.event(activity.getGaCategory(), CHECKOUT, CART+ITEM+DECREASED);
    }

    @Override
    public void deleteStarSubscription() {
        selectedSubscription = null;
        getCheckoutDataAPI(selectedSubscription);
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
        if(!rehitCheckoutApi()){
            removeCouponWithCheck();
        }
    }

    @Override
    public void onMinusClicked(int position, int itemTotalQuantity) {
        editTextDeliveryInstructions.clearFocus();
        cartChangedRefreshCheckout = true;
        updateCartDataView();
        if(itemTotalQuantity == 0){
            itemsInCart.remove(position);
            checkIfEmpty();
        }
        if(itemsInCart.size() > 0) {
            if(!rehitCheckoutApi()){
                removeCouponWithCheck();
            }
        }
    }

    private void removeCouponWithCheck(){
        if(getSelectedCoupon() != null && getSelectedCoupon().getId() > 0){
            removeCoupon();
        }
    }

    private boolean rehitCheckoutApi(){
        if(activity.getUserCheckoutResponse() != null
                && activity.getUserCheckoutResponse().getRefreshOnCartChange() == 1){
            getCheckoutDataAPI(selectedSubscription);
            return true;
        }
        return false;
    }


    public void deleteCart() {
        if(isMenusOpen()){
            for(Item item : itemsInCart){
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

    private void checkIfEmpty(){
        if(isMenusOpen()){
            if(itemsInCart.size() == 0){
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
        if(cartChangedRefreshCheckout){
            getCheckoutDataAPI(selectedSubscription);
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
        }
        boolean offerApplied = false;
        if(promoCoupon.getIsValid() == 0){
            String message = activity.getString(R.string.please_check_tnc);
            if(!TextUtils.isEmpty(promoCoupon.getInvalidMessage())){
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
            if(activity.getSelectedPromoCoupon() != null) {
                GAUtils.event(activity.getGaCategory(), CHECKOUT+OFFER+MODIFIED, activity.getSelectedPromoCoupon().getTitle());
            }
        }
        return offerApplied;
    }


    private void updateCartTopBarView(Pair<Double, Integer> pair){
        taxSubTotal.setValue(getSubTotalAmount(true));
        textViewCartItems.setText(activity.getString(R.string.cart_items_format, String.valueOf(pair.second)));
        taxTotal.setValue(pair.first);
        chargesAdapter.notifyDataSetChanged();
    }

    private void updateCartDataView(){
        try {
            Pair<Double, Integer> pair;
            if(isMenusOpen()){
                pair = activity.updateCartValuesGetTotalPrice();
            } else {
                pair = activity.getSubItemInCartTotalPrice();
            }
            subTotalAmount = pair.first;
            updateCartTopBarView(pair);
            updateCartUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double getSubTotalAmount(boolean includeSubscription){
        if(includeSubscription){
            double subTotalAmountIncSubs = subTotalAmount;
            if(!Data.userData.isSubscriptionActive()
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

    private PaymentOption getSavedPaymentOption(){
        return MyApplication.getInstance().getWalletCore().getDefaultPaymentOption();
//        if(checkoutSaveData.isDefault()){
//            return MyApplication.getInstance().getWalletCore().getDefaultPaymentOption();
//        } else{
//            return MyApplication.getInstance().getWalletCore().getPaymentOptionFromInt(checkoutSaveData.getPaymentMode());
//        }
    }


    private double totalUndiscounted(){
        if(isMenusOpen()){
            return getSubTotalAmount(true) + totalTaxAmount;
        } else {
            return getSubTotalAmount(true) + deliveryCharges();
        }
    }

    private double totalAmount(){
        double totalAmount = totalUndiscounted() - getTotalPromoAmount();
        if(totalAmount < 0) {
            totalAmount = 0;
        }
        return totalAmount;
    }

    private double payableAmount(){
        double payableAmount = totalAmount() - jcUsed();
        if(payableAmount < 0) {
            payableAmount = 0;
        }
        return payableAmount;
    }

    private double jcUsed(){
        if(isMenusOpen() && activity.getPaymentOption() == PaymentOption.CASH){
            return 0d;
        } else {
            return Math.min(totalAmount(), Data.userData.getJugnooBalance());
        }
    }


    private double deliveryCharges(){
        double deliveryCharges = 0d;
        if(type == AppConstant.ApplicationType.MEALS) {
            if(activity.getUserCheckoutResponse() != null
                    && activity.getUserCheckoutResponse().getSubscription().getDeliveryCharges() != null
                    && activity.getUserCheckoutResponse().getSubscription().getDeliveryCharges() > 0){
                deliveryCharges = activity.getUserCheckoutResponse().getSubscription().getDeliveryCharges();
            } else if(activity.getUserCheckoutResponse() != null
                    && activity.getUserCheckoutResponse().getDeliveryInfo() != null
                    && activity.getUserCheckoutResponse().getDeliveryInfo().getDeliveryCharges() != null
                    && activity.getUserCheckoutResponse().getDeliveryInfo().getDeliveryCharges() > 0){
                deliveryCharges = activity.getUserCheckoutResponse().getDeliveryInfo().getDeliveryCharges();
            } else {
                deliveryCharges = activity.getProductsResponse().getDeliveryInfo().getApplicableDeliveryCharges(type, getSubTotalAmount(false));
            }
        } else{
            if(activity.getProductsResponse() != null && activity.getProductsResponse().getDeliveryInfo() != null){
                deliveryCharges = activity.getProductsResponse().getDeliveryInfo().getApplicableDeliveryCharges(type, getSubTotalAmount(false));
            } else {
                deliveryCharges = activity.getSuperCategoriesData().getDeliveryInfo().getApplicableDeliveryCharges(type, getSubTotalAmount(false));
                if(activity.getUserCheckoutResponse() != null
                        && activity.getUserCheckoutResponse().getSubscription().getDeliveryCharges() != null){
                    deliveryCharges = activity.getUserCheckoutResponse().getSubscription().getDeliveryCharges();
                }
            }
        }
        if(!Data.userData.isSubscriptionActive()
                && activity.getUserCheckoutResponse() != null
                && activity.getUserCheckoutResponse().getSubscriptionInfo() != null
                && activity.getUserCheckoutResponse().getSubscriptionInfo().getSubscriptionId() != null
                && activity.getUserCheckoutResponse().getSubscriptionInfo().getDeliveryCharges() != null) {
            deliveryCharges = activity.getUserCheckoutResponse().getSubscriptionInfo().getDeliveryCharges();
        }

        return deliveryCharges;
    }



    public void apiPlaceOrderPayCallback(final MessageRequest message){
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
                            }
                            else if (flag == ApiResponseFlags.ACTION_FAILED.getOrdinal()) {
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

                            }
                            else {
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
                if(isMenusOpen()){
                    RestClient.getMenusApiService().placeOrderCallback(params, callback);
                } else {
                    RestClient.getFreshApiService().placeOrderCallback(params, callback);
                }
            } else {
                retryDialogPlaceOrderPayCallbackApi(DialogErrorType.NO_NET, message);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialogPlaceOrderPayCallbackApi(DialogErrorType dialogErrorType, final MessageRequest message){
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
    public void setDeliveryAddressUpdated(boolean deliveryAddressUpdated){
        this.deliveryAddressUpdated = deliveryAddressUpdated;
    }

    private void placeOrderAnalytics(){
    }


    private boolean isMenusOpen(){
        return type == AppConstant.ApplicationType.MENUS;
    }

    private boolean isFreshOpen(){
        return type == AppConstant.ApplicationType.FRESH;
    }

    private void clearMenusCartAndGoToMenusFragment(){
        activity.clearMenusCart();
        activity.setRefreshCart(true);
        activity.performBackPressed(false);
        activity.setRefreshCart(true);
        activity.performBackPressed(false);
    }

    private Double getCalculatedCharges(double amount, Charges charges, List<Charges> chargesList){
        double includedTaxValue = 0d;
        for (Integer pos : charges.getIncludedValues()) {
            try {
                Charges chargesPos = chargesList.get(chargesList.indexOf(new Charges(pos)));
                includedTaxValue = includedTaxValue + getCalculatedCharges(amount, chargesPos, chargesList);
            } catch (Exception e) {
            }
        }
        if(charges.getType() == Charges.ChargeType.SUBTOTAL_LEVEL.getOrdinal()) {
            if (charges.getIsPercent() == 1) {
                return ((amount + includedTaxValue) * (Double.parseDouble(charges.getValue()) / 100d));
            } else {
                return (Double.parseDouble(charges.getValue()) + includedTaxValue);
            }
        } else if(charges.getType() == Charges.ChargeType.ITEM_LEVEL.getOrdinal()) {
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

    private void updateDeliveryFromView(String address){
        if(isMenusOpen() && activity.getVendorOpened() != null){
            llDeliveryFrom.setVisibility(View.VISIBLE);
            tvRestName.setText(activity.getVendorOpened().getName());
            if(TextUtils.isEmpty(address)) {
                tvRestAddress.setText(activity.getVendorOpened().getRestaurantAddress());
            } else {
                tvRestAddress.setText(address);
            }
        } else {
            llDeliveryFrom.setVisibility(View.GONE);
        }
    }



    // razor pay back from service callback
    public void razorpayServiceCallback(JSONObject jsonObject){
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
			}
			else if (flag == ApiResponseFlags.ACTION_FAILED.getOrdinal()) {
				 DialogPopup.alertPopup(activity, "", message);
                setSlideInitial();
			}
        } catch (Exception e) {
            e.printStackTrace();
            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
        }
    }


    private HashMap<String, String> paramsPlaceOrder;

    private void outOfRangeDialog(String message){
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message,
                activity.getString(R.string.change),
                activity.getString(R.string.back),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearMenusCartAndGoToMenusFragment();
                        Utils.showToast(activity, activity.getString(R.string.your_cart_has_been_cleared));
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.setDeliveryAddressModelToSelectedAddress(true);
                        getCheckoutDataAPI(selectedSubscription);
                    }
                }, false, false);
    }

}
