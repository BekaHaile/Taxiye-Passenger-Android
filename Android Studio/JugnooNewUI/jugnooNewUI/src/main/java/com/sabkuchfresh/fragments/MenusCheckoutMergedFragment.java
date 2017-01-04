package com.sabkuchfresh.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sabkuchfresh.adapters.DeliverySlotsAdapter;
import com.sabkuchfresh.adapters.FreshCartItemsAdapter;
import com.sabkuchfresh.adapters.MenusItemChargesAdapter;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.datastructure.ApplicablePaymentMode;
import com.sabkuchfresh.datastructure.CheckoutSaveData;
import com.sabkuchfresh.home.CallbackPaymentOptionSelector;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.home.FreshWalletBalanceLowDialog;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.DeliverySlot;
import com.sabkuchfresh.retrofit.model.MenusResponse;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.ProductsResponse;
import com.sabkuchfresh.retrofit.model.Slot;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.retrofit.model.UserCheckoutResponse;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Events;
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
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.adapters.PromoCouponsAdapter;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.UserDebtDialog;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;
import product.clicklabs.jugnoo.wallet.models.PaymentModeConfigData;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class MenusCheckoutMergedFragment extends Fragment implements FlurryEventNames, DeliverySlotsAdapter.Callback,
        FreshCartItemsAdapter.Callback, PromoCouponsAdapter.Callback {

    private final String TAG = MenusCheckoutMergedFragment.class.getSimpleName();
    private LinearLayout linearLayoutRoot;

    private RelativeLayout relativeLayoutCartTop;
    private TextView textViewCartItems, textViewCartTotalUndiscount, textViewCartTotal;
    private ImageView imageViewCartArrow, imageViewDeleteCart, imageViewCartSep;
    private LinearLayout linearLayoutCartDetails, linearLayoutCartExpansion;
    private NonScrollListView listViewCart;
    private NonScrollListView listViewMenusCharges;
    private MenusItemChargesAdapter menusItemChargesAdapter;
    private FreshCartItemsAdapter freshCartItemsAdapter;

    private RelativeLayout relativeLayoutDeliveryAddress;
    private ImageView imageViewAddressType, imageViewDeliveryAddressForward;
    private TextView textViewAddressName, textViewAddressValue;

    private RelativeLayout relativeLayoutPaytm, relativeLayoutMobikwik, relativeLayoutFreeCharge, relativeLayoutCash;
    private LinearLayout linearLayoutWalletContainer;
    private ImageView imageViewPaytmRadio, imageViewAddPaytm, imageViewRadioMobikwik, imageViewAddMobikwik,
            imageViewRadioFreeCharge, imageViewAddFreeCharge, imageViewCashRadio;
    private TextView textViewPaytmValue, textViewMobikwikValue, textViewFreeChargeValue;

    private LinearLayout linearLayoutOffers;
    private NonScrollListView listViewOffers;
    private PromoCouponsAdapter promoCouponsAdapter;

    private EditText editTextDeliveryInstructions;
    private Button buttonPlaceOrder;

    private ScrollView scrollView;
    private LinearLayout linearLayoutMain;
    private TextView textViewScroll;

    private TextView textViewDeliveryInstructionsText;




    private ArrayList<Slot> slots = new ArrayList<>();

    private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();

    private View rootView;
    private FreshActivity activity;

    // for payment screen
    private double subTotalAmount = 0, totalTaxAmount = 0d;
    private double promoAmount = 0;

    private Bus mBus;
    private int currentGroupId = 1;
    private boolean orderPlaced = false;
    private boolean cartChangedRefreshCheckout = false;

    public MenusCheckoutMergedFragment() {
    }

    private List<Product> productList = new ArrayList<>();
    private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "Don't apply coupon on this ride");

    private ArrayList<SubItem.Tax> taxList = new ArrayList<>();

    private CheckoutSaveData checkoutSaveData;
    private int type;

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
        rootView = inflater.inflate(R.layout.fragment_menus_checkout_merged, container, false);

        cartChangedRefreshCheckout = false;
        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);

        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if (linearLayoutRoot != null) {
                new ASSL(activity, linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        type = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
        mBus = (activity).getBus();
        orderPlaced = false;
        checkoutSaveData = new CheckoutSaveData();

        if(activity.subItemsInCart == null) {
            activity.subItemsInCart = new ArrayList<>();
        }
        activity.subItemsInCart.clear();


        if(activity.getProductsResponse() != null
                && activity.getProductsResponse().getCategories() != null) {
            for (Category category : activity.getProductsResponse().getCategories()) {
                for (SubItem subItem : category.getSubItems()) {
                    if (subItem.getSubItemQuantitySelected() > 0) {
                       /* ArrayList<SubItem.Tax> taxList = new ArrayList<>();
                        taxList.add(subItem.new Tax("pc",5.0));
                        taxList.add(subItem.new Tax("dc",5.0));
                        subItem.setTaxes(taxList);*/
                        activity.subItemsInCart.add(subItem);
                    }
                }
                if(Data.AppType == AppConstant.ApplicationType.MEALS) {
                    currentGroupId = category.getCurrentGroupId();
                }
            }
        }

        try {
            for (int i = 0; i < activity.subItemsInCart.size(); i++) {
                MyApplication.getInstance().getCleverTapUtils().addToCart(activity.subItemsInCart.get(i).getSubItemName(),
                        activity.subItemsInCart.get(i).getSubItemId(), activity.subItemsInCart.get(i).getSubItemQuantitySelected(),
                        activity.subItemsInCart.get(i).getPrice(),
                        Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()),
                        Data.userData.getCity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(Data.getDatumToReOrder() != null){
				activity.setSelectedAddress(Data.getDatumToReOrder().getDeliveryAddress());
				activity.setSelectedLatLng(new LatLng(Data.getDatumToReOrder().getDeliveryLatitude(), Data.getDatumToReOrder().getDeliveryLongitude()));
                ArrayList<SearchResult> searchResults = new HomeUtil().getSavedPlacesWithHomeWork(activity);
                for(SearchResult searchResult : searchResults){
                    if(Data.getDatumToReOrder().getAddressId().equals(searchResult.getId())){
                        activity.setSelectedAddressId(Data.getDatumToReOrder().getAddressId());
                        activity.setSelectedAddressType(Data.getDatumToReOrder().getDeliveryAddressType());
                        break;
                    }
                }
                activity.setRefreshCart(true);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        Data.setDatumToReOrder(null);

        activity.setMenuRefreshLatLng(new LatLng(activity.getSelectedLatLng().latitude, activity.getSelectedLatLng().longitude));

        ((TextView)rootView.findViewById(R.id.textViewDeliveryAddress)).setTypeface(Fonts.mavenMedium(activity));
        ((TextView)rootView.findViewById(R.id.textViewPaymentVia)).setTypeface(Fonts.mavenMedium(activity));
        ((TextView)rootView.findViewById(R.id.textViewOffers)).setTypeface(Fonts.mavenMedium(activity));


        relativeLayoutCartTop = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCartTop);
        textViewCartItems = (TextView) rootView.findViewById(R.id.textViewCartItems); textViewCartItems.setTypeface(Fonts.mavenMedium(activity));
        textViewCartTotalUndiscount = (TextView) rootView.findViewById(R.id.textViewCartTotalUndiscount); textViewCartTotalUndiscount.setTypeface(Fonts.mavenMedium(activity));
        textViewCartTotal = (TextView) rootView.findViewById(R.id.textViewCartTotal); textViewCartTotal.setTypeface(Fonts.mavenMedium(activity));
        imageViewCartArrow = (ImageView) rootView.findViewById(R.id.imageViewCartArrow);
        imageViewDeleteCart = (ImageView) rootView.findViewById(R.id.imageViewDeleteCart);
        imageViewCartSep = (ImageView) rootView.findViewById(R.id.imageViewCartSep);
        linearLayoutCartExpansion = (LinearLayout) rootView.findViewById(R.id.linearLayoutCartExpansion);
        linearLayoutCartDetails = (LinearLayout) rootView.findViewById(R.id.linearLayoutCartDetails);
        listViewCart = (NonScrollListView) rootView.findViewById(R.id.listViewCart);
        listViewMenusCharges = (NonScrollListView) rootView.findViewById(R.id.listViewMenusCharges);

        menusItemChargesAdapter = new MenusItemChargesAdapter(activity, taxList);
        listViewMenusCharges.setAdapter(menusItemChargesAdapter);

        freshCartItemsAdapter = new FreshCartItemsAdapter(activity, activity.subItemsInCart, FlurryEventNames.REVIEW_CART, true, this);
        listViewCart.setAdapter(freshCartItemsAdapter);


        textViewDeliveryInstructionsText = ((TextView)rootView.findViewById(R.id.textViewDeliveryInstructions));
        textViewDeliveryInstructionsText.setTypeface(Fonts.mavenMedium(activity));
        editTextDeliveryInstructions = (EditText) rootView.findViewById(R.id.editTextDeliveryInstructions);
        editTextDeliveryInstructions.setTypeface(Fonts.mavenRegular(activity));

        if(type == AppConstant.ApplicationType.MENUS)
        {
            textViewDeliveryInstructionsText.setText(R.string.delivery_instructions_for_menus);
            editTextDeliveryInstructions.setHint(R.string.add_special_notes_for_menus);
        } else
        {
            textViewDeliveryInstructionsText.setText(R.string.delivery_instructions);
            editTextDeliveryInstructions.setHint(R.string.add_special_notes);
        }


        relativeLayoutDeliveryAddress = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDeliveryAddress);
        imageViewAddressType = (ImageView) rootView.findViewById(R.id.imageViewAddressType);
        imageViewDeliveryAddressForward = (ImageView) rootView.findViewById(R.id.imageViewDeliveryAddressForward);
        textViewAddressName = (TextView) rootView.findViewById(R.id.textViewAddressName); textViewAddressName.setTypeface(Fonts.mavenMedium(activity));
        textViewAddressValue = (TextView) rootView.findViewById(R.id.textViewAddressValue); textViewAddressValue.setTypeface(Fonts.mavenRegular(activity));


        linearLayoutWalletContainer = (LinearLayout) rootView.findViewById(R.id.linearLayoutWalletContainer);
        relativeLayoutPaytm = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPaytm);
        relativeLayoutMobikwik = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutMobikwik);
        relativeLayoutFreeCharge = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutFreeCharge);
        relativeLayoutCash = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCash);
        imageViewPaytmRadio = (ImageView) rootView.findViewById(R.id.imageViewPaytmRadio);
        imageViewAddPaytm = (ImageView) rootView.findViewById(R.id.imageViewAddPaytm);
        imageViewRadioMobikwik = (ImageView)rootView.findViewById(R.id.imageViewRadioMobikwik);
        imageViewAddMobikwik = (ImageView) rootView.findViewById(R.id.imageViewAddMobikwik);
        imageViewRadioFreeCharge = (ImageView)rootView.findViewById(R.id.imageViewRadioFreeCharge);
        imageViewAddFreeCharge = (ImageView) rootView.findViewById(R.id.imageViewAddFreeCharge);
        imageViewCashRadio = (ImageView) rootView.findViewById(R.id.imageViewCashRadio);
        textViewPaytmValue = (TextView)rootView.findViewById(R.id.textViewPaytmValue);textViewPaytmValue.setTypeface(Fonts.mavenMedium(activity));
        textViewMobikwikValue = (TextView)rootView.findViewById(R.id.textViewMobikwikValue);textViewMobikwikValue.setTypeface(Fonts.mavenMedium(activity));
        textViewFreeChargeValue = (TextView)rootView.findViewById(R.id.textViewFreeChargeValue);textViewFreeChargeValue.setTypeface(Fonts.mavenMedium(activity));
        ((TextView)rootView.findViewById(R.id.textViewCash)).setTypeface(Fonts.mavenMedium(activity));

        linearLayoutOffers = (LinearLayout) rootView.findViewById(R.id.linearLayoutOffers);
        listViewOffers = (NonScrollListView) rootView.findViewById(R.id.listViewOffers);
        promoCouponsAdapter = new PromoCouponsAdapter(activity, R.layout.list_item_fresh_promo_coupon, promoCoupons, this);
        listViewOffers.setAdapter(promoCouponsAdapter);

        buttonPlaceOrder = (Button) rootView.findViewById(R.id.buttonPlaceOrder); buttonPlaceOrder.setTypeface(Fonts.mavenRegular(activity));

        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);
        textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);


        relativeLayoutCash.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutPaytm.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutMobikwik.setOnClickListener(onClickListenerPaymentOptionSelector);
        relativeLayoutFreeCharge.setOnClickListener(onClickListenerPaymentOptionSelector);

        activity.setSelectedPromoCoupon(noSelectionCoupon);

        updateCouponsDataView();


        relativeLayoutDeliveryAddress.setBackgroundResource(R.drawable.bg_transparent_menu_item_selector);
        imageViewDeliveryAddressForward.setVisibility(View.VISIBLE);
        textViewAddressName.setTextColor(activity.getResources().getColorStateList(R.color.text_color_selector));
        if(Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType) == AppConstant.ApplicationType.MENUS) {
            relativeLayoutDeliveryAddress.setBackgroundResource(R.drawable.background_transparent);
            imageViewDeliveryAddressForward.setVisibility(View.GONE);
            textViewAddressName.setTextColor(activity.getResources().getColor(R.color.text_color));
        }

        relativeLayoutDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryEventLogger.event(CHECKOUT_SCREEN, SCREEN_TRANSITION, ADDRESS_SCREEN);
                activity.getTransactionUtils().openDeliveryAddressFragment(activity, activity.getRelativeLayoutContainer());
            }
        });

        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((type == AppConstant.ApplicationType.MENUS && subTotalAmount < activity.getVendorOpened().getMinimumOrderAmount())) {
                    Utils.showToast(activity, getResources().getString(R.string.minimum_order_amount_is_format,
                            Utils.getMoneyDecimalFormatWithoutFloat().format(activity.getVendorOpened().getMinimumOrderAmount())));
                }
//                else if((((type == AppConstant.ApplicationType.GROCERY) || (type == AppConstant.ApplicationType.FRESH)) && subTotalAmount < activity.getProductsResponse().getDeliveryInfo().getMinAmount())) {
//                    Utils.showToast(activity, getResources().getString(R.string.minimum_order_amount_is_format,
//                            Utils.getMoneyDecimalFormatWithoutFloat().format(activity.getProductsResponse().getDeliveryInfo().getMinAmount())));
//                }
                else if (buttonPlaceOrder.getText().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.connection_lost_try_again))) {
                    getCheckoutDataAPI();
                } else if (type != AppConstant.ApplicationType.MENUS && activity.getSlotSelected() == null) {
                    product.clicklabs.jugnoo.utils.Utils.showToast(activity, activity.getResources().getString(R.string.please_select_a_delivery_slot));
                } else if (TextUtils.isEmpty(activity.getSelectedAddress())) {
                    product.clicklabs.jugnoo.utils.Utils.showToast(activity, activity.getResources().getString(R.string.please_select_a_delivery_address));
                } else if (MyApplication.getInstance().getWalletCore().displayAlertAndCheckForSelectedWalletCoupon(activity,
                        activity.getPaymentOption().getOrdinal(), activity.getSelectedPromoCoupon())){
                    activity.setSplInstr(editTextDeliveryInstructions.getText().toString().trim());
                    placeOrder();
                    if(type == AppConstant.ApplicationType.MEALS){
                        MyApplication.getInstance().logEvent(FirebaseEvents.M_PAY+"_"+activity.getPaymentOption(), null);
                        MyApplication.getInstance().logEvent(FirebaseEvents.M_PAY+"_"+FirebaseEvents.PLACE_ORDER, null);
                    } else if(type == AppConstant.ApplicationType.GROCERY){
                        MyApplication.getInstance().logEvent(FirebaseEvents.G_PAY+"_"+activity.getPaymentOption(), null);
                        MyApplication.getInstance().logEvent(FirebaseEvents.G_PAY+"_"+FirebaseEvents.PLACE_ORDER, null);
                    } else if(type == AppConstant.ApplicationType.MENUS){
                        MyApplication.getInstance().logEvent(FirebaseEvents.MENUS_PAY+"_"+activity.getPaymentOption(), null);
                        MyApplication.getInstance().logEvent(FirebaseEvents.MENUS_PAY+"_"+FirebaseEvents.PLACE_ORDER, null);
                    } else{
                        MyApplication.getInstance().logEvent(FirebaseEvents.F_PAY+"_"+activity.getPaymentOption(), null);
                        MyApplication.getInstance().logEvent(FirebaseEvents.F_PAY+"_"+FirebaseEvents.PLACE_ORDER, null);
                    }
                    FlurryEventLogger.event(PAYMENT_SCREEN, ORDER_PLACED, ORDER_PLACED);
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
                    imageViewDeleteCart.setVisibility(View.VISIBLE);
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

/*
        Collections.sort(activity.getProductsResponse().getCharges(), new Comparator<ProductsResponse.Charges>() {
            @Override
            public int compare(ProductsResponse.Charges lhs, ProductsResponse.Charges rhs) {
                return lhs.getIncludeValue().size() - rhs.getIncludeValue().size();
            }
        });
*/
        updateAddressView();

        updateCartDataView();

        FlurryEventLogger.checkoutTrackEvent(AppConstant.EventTracker.CHECKOUT, activity.productList);
        getCheckoutDataAPI();

        FlurryEventLogger.checkoutTrackEvent(AppConstant.EventTracker.PAYMENT, activity.productList);
        fetchWalletBalance();

        linearLayoutCartExpansion.setVisibility(View.GONE);
        imageViewDeleteCart.setVisibility(View.GONE);
        imageViewCartArrow.setRotation(180f);

        KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(linearLayoutMain, textViewScroll, new KeyboardLayoutListener.KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {
                scrollView.scrollTo(0, buttonPlaceOrder.getBottom());
            }

            @Override
            public void keyBoardClosed() {

            }
        });
        keyboardLayoutListener.setResizeTextView(false);
        linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        return rootView;
    }


//    private Double getCalculatedChargesSubItem(SubItem subItem, ProductsResponse.Charges charges, List<ProductsResponse.Charges> chargesList, double value){
//        if (charges.getIsPercent() == 1) {
//            double percentVal = (subItem.getPrice() * (double) subItem.getSubItemQuantitySelected());
//            for (Integer pos : charges.getIncludeValue()) {
//                try {
//                    ProductsResponse.Charges chargesPos = chargesList.get(chargesList.indexOf(activity.getProductsResponse().new Charges(pos)));
//                    double valuePos = 0d;
//                    for (SubItem.Tax tax : subItem.getTaxes()) {
//                        if (tax.getKey().equalsIgnoreCase(chargesPos.getValue())) {
//                            valuePos = tax.getValue();
//                            break;
//                        }
//                    }
//                    percentVal = percentVal + getCalculatedChargesSubItem(subItem, chargesPos, chargesList, valuePos);
//                } catch (Exception e) {
//                }
//            }
//            return (percentVal * (value / 100d));
//        } else {
//            return ((double) subItem.getSubItemQuantitySelected() * value);
//        }
//    }

    private Double getCalculatedCharges(double amount, ProductsResponse.Charges charges, List<ProductsResponse.Charges> chargesList){
        double includedTaxValue = 0d;
        for (Integer pos : charges.getIncludeValue()) {
            try {
                ProductsResponse.Charges chargesPos = chargesList.get(chargesList.indexOf(activity.getProductsResponse().new Charges(pos)));
                includedTaxValue = includedTaxValue + getCalculatedCharges(amount, chargesPos, chargesList);
            } catch (Exception e) {
            }
        }
        if(charges.getType() == ProductsResponse.ChargeType.SUBTOTAL_LEVEL.getOrdinal()) {
            if (charges.getIsPercent() == 1) {
                return ((amount + includedTaxValue) * (Double.parseDouble(charges.getValue()) / 100d));
            } else {
                return (Double.parseDouble(charges.getValue()) + includedTaxValue);
            }
        } else if(charges.getType() == ProductsResponse.ChargeType.ITEM_LEVEL.getOrdinal()) {
            double totalCharge = 0d;
            for (SubItem subItem : activity.subItemsInCart) {
                SubItem.Tax taxMatched = null;
                for (SubItem.Tax tax : subItem.getTaxes()) {
                    if (tax.getKey().equalsIgnoreCase(charges.getValue())) {
                        taxMatched = tax;
                        break;
                    }
                }
                if (taxMatched != null) {
                    if (charges.getIsPercent() == 1) {
                        totalCharge = totalCharge +
                                ((subItem.getPrice() * (double) subItem.getSubItemQuantitySelected()) * (taxMatched.getValue() / 100d));
                    } else {
                        totalCharge = totalCharge + ((double) subItem.getSubItemQuantitySelected() * taxMatched.getValue());
                    }
                }
            }
            if (charges.getIsPercent() == 1) {
                totalCharge = totalCharge + (includedTaxValue * (charges.getDefaultVal() / 100d));
            } else {
                totalCharge = totalCharge + includedTaxValue;
            }
            return totalCharge;
        } else {
            return 0d;
        }
    }


    private void updateCartUI() {
        taxList.clear();

        editTextDeliveryInstructions.setText(activity.getSpecialInst());

        SubItem subItemTemp = new SubItem();
        if (promoAmount > 0) {
            taxList.add(subItemTemp.new Tax(getString(R.string.discount), promoAmount));
        }
        /*activity.getProductsResponse().getCharges().clear();
        ProductsResponse.Charges charges = activity.getProductsResponse().new Charges();
        charges.setId(0);
        charges.setIsPercent(0);
        charges.setText("Packing Charges");
        charges.setValue("pc");
        charges.setType(1);
        List<Integer> includedValues = new ArrayList<>();
        charges.setIncludeValue(includedValues);
        activity.getProductsResponse().getCharges().add(charges);


        ProductsResponse.Charges charges2 = activity.getProductsResponse().new Charges();
        charges2.setId(1);
        charges2.setIsPercent(1);
        charges2.setText("Delivery Charges");
        charges2.setType(1);
        charges2.setValue("dc");
        List<Integer> includedValues2 = new ArrayList<>();
        includedValues2.add(charges.getId());
        charges2.setIncludeValue(includedValues2);
        activity.getProductsResponse().getCharges().add(charges2);

*/
        totalTaxAmount = 0d;
        for(ProductsResponse.Charges charges1 : activity.getProductsResponse().getCharges()){
            SubItem.Tax tax = subItemTemp.new Tax(charges1.getText(), getCalculatedCharges(subTotalAmount, charges1, activity.getProductsResponse().getCharges()));
            if(tax.getValue() > 0 || charges1.getForceShow() == 1) {
                taxList.add(tax);
            }
            totalTaxAmount = totalTaxAmount + tax.getValue();
        }

        if (totalAmount() > 0 && jcUsed() > 0) {
            taxList.add(subItemTemp.new Tax(getString(R.string.jugnoo_cash), jcUsed()));
        }
        menusItemChargesAdapter.notifyDataSetChanged();

        if(taxList.size() > 0){
            linearLayoutCartDetails.setVisibility(View.VISIBLE);
            imageViewCartSep.setVisibility(View.GONE);
        } else {
            linearLayoutCartDetails.setVisibility(View.GONE);
            imageViewCartSep.setVisibility(View.VISIBLE);
        }
        textViewCartTotal.setText(activity.getString(R.string.rupees_value_format, String.valueOf(Math.round(payableAmount()))));
        if(promoAmount > 0){
            textViewCartTotalUndiscount.setVisibility(View.VISIBLE);
            textViewCartTotalUndiscount.setText(activity.getString(R.string.rupees_value_format,
                    Utils.getMoneyDecimalFormat().format(totalUndiscounted())));
            textViewCartTotalUndiscount.setPaintFlags(textViewCartTotalUndiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else{
            textViewCartTotalUndiscount.setVisibility(View.GONE);
        }
    }


    private View.OnClickListener onClickListenerPaymentOptionSelector = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                String offeringPrefix = Config.getMealsClientId()
                        .equalsIgnoreCase(Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()))?
                        FirebaseEvents.MEALS_PAYMENT_MODE : FirebaseEvents.FRESH_PAYMENT_MODE;

                Bundle bundle = new Bundle();
                switch (v.getId()){
                    case R.id.relativeLayoutPaytm:
                        MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+offeringPrefix +"_"
                                +FirebaseEvents.PAYTM, bundle);
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.PAYTM,
                                callbackPaymentOptionSelector);
                        break;

                    case R.id.relativeLayoutMobikwik:
                        MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+offeringPrefix +"_"
                                +FirebaseEvents.MOBIKWIK, bundle);
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.MOBIKWIK,
                                callbackPaymentOptionSelector);
                        break;

                    case R.id.relativeLayoutFreeCharge:
                        MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+offeringPrefix+"_"
                                +FirebaseEvents.FREECHARGE, bundle);
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.FREECHARGE,
                                callbackPaymentOptionSelector);
                        break;

                    case R.id.relativeLayoutCash:
                        MyApplication.getInstance().logEvent(FirebaseEvents.TRANSACTION+"_"+offeringPrefix+"_"
                                +FirebaseEvents.CASH, bundle);
                        MyApplication.getInstance().getWalletCore().paymentOptionSelectionAtFreshCheckout(activity, PaymentOption.CASH,
                                callbackPaymentOptionSelector);
                        break;
                }
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
    };


    @Override
    public void onResume() {
        super.onResume();
        orderPaymentModes();
        setPaymentOptionUI();
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
				}
            } catch (Exception e) {
                e.printStackTrace();
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

            if (activity.getPaymentOption() == PaymentOption.PAYTM) {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_selected);
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewCashRadio.setImageResource(R.drawable.ic_radio_button_normal);
            }
            else if (activity.getPaymentOption() == PaymentOption.MOBIKWIK) {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_selected);
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewCashRadio.setImageResource(R.drawable.ic_radio_button_normal);
            }
            else if (activity.getPaymentOption() == PaymentOption.FREECHARGE) {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_selected);
                imageViewCashRadio.setImageResource(R.drawable.ic_radio_button_normal);
            }
            else {
                imageViewPaytmRadio.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioMobikwik.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewRadioFreeCharge.setImageResource(R.drawable.ic_radio_button_normal);
                imageViewCashRadio.setImageResource(R.drawable.ic_radio_button_selected);
            }
            updateCartDataView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void placeOrder() {
        try {
            final int appType = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
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
            if (goAhead) {
                buttonPlaceOrder.setEnabled(false);
                DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
                        activity.getResources().getString(R.string.place_order_confirmation),
                        activity.getResources().getString(R.string.ok),
                        activity.getResources().getString(R.string.cancel),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (activity.getPaymentOption().getOrdinal() == 1) {
                                    FlurryEventLogger.event(PAYMENT_SCREEN, PAYMENT_METHOD, CASH);
                                } else {
                                    FlurryEventLogger.event(PAYMENT_SCREEN, PAYMENT_METHOD, PAYTM);
                                }

                                if(appType == AppConstant.ApplicationType.MEALS){
                                    MyApplication.getInstance().logEvent(FirebaseEvents.M_PAY+"_"+FirebaseEvents.PLACE_ORDER+"_"+FirebaseEvents.OK, null);
                                } else if(appType == AppConstant.ApplicationType.GROCERY){
                                    MyApplication.getInstance().logEvent(FirebaseEvents.G_PAY+"_"+FirebaseEvents.PLACE_ORDER+"_"+FirebaseEvents.OK, null);
                                } else if(appType == AppConstant.ApplicationType.MENUS){
                                    MyApplication.getInstance().logEvent(FirebaseEvents.MENUS_PAY+"_"+FirebaseEvents.PLACE_ORDER+"_"+FirebaseEvents.OK, null);
                                } else{
                                    MyApplication.getInstance().logEvent(FirebaseEvents.F_PAY+"_"+FirebaseEvents.PLACE_ORDER+"_"+FirebaseEvents.OK, null);
                                }
                                placeOrderApi();
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buttonPlaceOrder.setEnabled(true);
                                if(appType == AppConstant.ApplicationType.MEALS){
                                    MyApplication.getInstance().logEvent(FirebaseEvents.M_PAY+"_"+FirebaseEvents.PLACE_ORDER+"_"+FirebaseEvents.CANCEL, null);
                                } else if(appType == AppConstant.ApplicationType.GROCERY){
                                    MyApplication.getInstance().logEvent(FirebaseEvents.G_PAY+"_"+FirebaseEvents.PLACE_ORDER+"_"+FirebaseEvents.CANCEL, null);
                                } else if(appType == AppConstant.ApplicationType.MENUS){
                                    MyApplication.getInstance().logEvent(FirebaseEvents.MENUS_PAY+"_"+FirebaseEvents.PLACE_ORDER+"_"+FirebaseEvents.CANCEL, null);
                                } else{
                                    MyApplication.getInstance().logEvent(FirebaseEvents.F_PAY+"_"+FirebaseEvents.PLACE_ORDER+"_"+FirebaseEvents.CANCEL, null);
                                }
                            }
                        }, false, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String cartItems(int type){
        String idKey = Constants.KEY_SUB_ITEM_ID;
        if(type == AppConstant.ApplicationType.MENUS){
            idKey = Constants.KEY_ITEM_ID;
        }
        JSONArray jCart = new JSONArray();
        if (activity.getProductsResponse() != null
                && activity.getProductsResponse().getCategories() != null) {
            for (Category category : activity.getProductsResponse().getCategories()) {
                Log.d(TAG, "" + category.getCategoryName());
                for (SubItem subItem : category.getSubItems()) {
                    if (subItem.getSubItemQuantitySelected() > 0) {
                        try {
                            JSONObject jItem = new JSONObject();
                            //subItem.getSubItemName();
                            jItem.put(idKey, subItem.getSubItemId());
                            jItem.put(Constants.KEY_QUANTITY, subItem.getSubItemQuantitySelected());
                            jCart.put(jItem);

                        } catch (Exception e) {
                            e.printStackTrace();
                            return "";
                        }

                        try {
                            String categoryName = "", itemName = "";
                            double price = 0.0;
                            int qty = 0, itemId = 0;

                            qty = subItem.getSubItemQuantitySelected();
                            categoryName = category.getCategoryName();
                            itemId = subItem.getSubItemId();
                            itemName = subItem.getSubItemName();
                            price = subItem.getPrice();

                            Product product = new Product()
                                    .setCategory(categoryName)
                                    .setId("" + itemId)
                                    .setName(itemName)
                                    .setPrice(price)
                                    .setQuantity(qty);
//                                                                .setPosition(4);
                            productList.add(product);

                            HashMap<String, Object> item = new HashMap<>();
                            item.put(Events.PRODUCT_NAME, itemName);
                            item.put(Events.PRODUCT_ID, itemId);
                            item.put(Events.QUANTITY, qty);
                            items.add(item);

                        } catch (Exception e) {
                            e.printStackTrace();
                            return "";
                        }
                    }
                }
            }
        }
        return jCart.toString();
    }

    HashMap<String, Object> chargeDetails = new HashMap<String, Object>();
    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

    public void placeOrderApi() {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                productList.clear();
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

                chargeDetails.clear();
                items.clear();
                int type = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);


                chargeDetails.put("Payment mode", ""+activity.getPaymentOption());
                chargeDetails.put(Events.TOTAL_AMOUNT, ""+subTotalAmount);
                chargeDetails.put(Events.DISCOUNT_AMOUNT, "" + promoAmount);
                if(type != AppConstant.ApplicationType.MENUS) {
                    chargeDetails.put(Events.START_TIME, "" + String.valueOf(activity.getSlotSelected().getStartTime()));
                    chargeDetails.put(Events.END_TIME, "" + String.valueOf(activity.getSlotSelected().getEndTime()));
                }
                chargeDetails.put(Events.CITY, Data.userData.getCity());

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));

                params.put(Constants.KEY_MENU_LATITUDE, String.valueOf(activity.getMenuRefreshLatLng().latitude));
                params.put(Constants.KEY_MENU_LONGITUDE, String.valueOf(activity.getMenuRefreshLatLng().longitude));

                params.put(Constants.DELIVERY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                params.put(Constants.DELIVERY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));

                params.put(Constants.KEY_PAYMENT_MODE, String.valueOf(activity.getPaymentOption().getOrdinal()));
                if(type != AppConstant.ApplicationType.MENUS) {
                    params.put(Constants.KEY_DELIVERY_SLOT_ID, String.valueOf(activity.getSlotSelected().getDeliverySlotId()));
                }
                params.put(Constants.KEY_DELIVERY_ADDRESS, String.valueOf(activity.getSelectedAddress()));
                if(activity.getSelectedAddressId() > 0){
                    params.put(Constants.KEY_DELIVERY_ADDRESS_ID, String.valueOf(activity.getSelectedAddressId()));
                    params.put(Constants.KEY_DELIVERY_ADDRESS_TYPE, String.valueOf(activity.getSelectedAddressType()));
                }
                params.put(Constants.KEY_DELIVERY_NOTES, String.valueOf(activity.getSpecialInst()));
                params.put(Constants.KEY_CLIENT_ID, ""+ Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));
                params.put(Constants.KEY_CART, cartItems(type));
                if(activity.getSelectedPromoCoupon() != null && activity.getSelectedPromoCoupon().getId() > -1){
                    if(activity.getSelectedPromoCoupon() instanceof CouponInfo){
                        params.put(Constants.KEY_ACCOUNT_ID, String.valueOf(activity.getSelectedPromoCoupon().getId()));
                    } else if(activity.getSelectedPromoCoupon() instanceof PromotionInfo){
                        params.put(Constants.KEY_ORDER_OFFER_ID, String.valueOf(activity.getSelectedPromoCoupon().getId()));
                    }
                    params.put(Constants.KEY_MASTER_COUPON, String.valueOf(activity.getSelectedPromoCoupon().getMasterCoupon()));
                }
                try {
                    chargeDetails.put(Events.COUPONS_USED, activity.getSelectedPromoCoupon().getTitle());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if(type == AppConstant.ApplicationType.MEALS) {
                    params.put("store_id", "2");
                    params.put("group_id", ""+activity.getProductsResponse().getCategories().get(0).getSubItems().get(0).getGroupId());
                    chargeDetails.put(Events.TYPE, "Meals");
                } else if(type == AppConstant.ApplicationType.GROCERY) {
                    chargeDetails.put(Events.TYPE, "Grocery");
                } else if(type == AppConstant.ApplicationType.MENUS) {
                    chargeDetails.put(Events.TYPE, "Menus");
                } else {
                    chargeDetails.put(Events.TYPE, "Fresh");
                }
                params.put(Constants.INTERATED, "1");

                if(type == AppConstant.ApplicationType.MENUS){
                    params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(activity.getVendorOpened().getRestaurantId()));
                }


                Log.i(TAG, "getAllProducts params=" + params.toString());

                Callback<PlaceOrderResponse> callback = new Callback<PlaceOrderResponse>() {
                    @Override
                    public void success(PlaceOrderResponse placeOrderResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
//						DialogPopup.dismissLoadingDialog();
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                final int flag = jObj.getInt(Constants.KEY_FLAG);
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    orderPlaced = true;
                                    activity.saveCheckoutData(true);
                                    long time = 0L;
                                    Prefs.with(activity).save(SPLabels.CHECK_BALANCE_LAST_TIME, time);
                                    activity.resumeMethod();

                                    chargeDetails.put("Charged ID", placeOrderResponse.getOrderId());
                                    MyApplication.getInstance().charged(chargeDetails, items);


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

                                    FlurryEventLogger.checkoutTrackEvent(AppConstant.EventTracker.ORDER_PLACED, productList);
                                    FlurryEventLogger.orderedProduct(productList, productAction);

                                    int type = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
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
                                    new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
                                        @Override
                                        public void onDismiss() {
                                            activity.orderComplete();
                                        }
                                    }).show(String.valueOf(placeOrderResponse.getOrderId()),
                                            deliverySlot, deliveryDay, showDeliverySlot, restaurantName, placeOrderResponse);
                                    activity.setSelectedPromoCoupon(noSelectionCoupon);


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
                                            getCheckoutDataAPI();
                                        }
                                    });
                                }
                                else {
                                    final int validStockCount = jObj.optInt(Constants.KEY_VALID_STOCK_COUNT, -1);
                                    if(validStockCount > -1){
                                        DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    int appType = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
                                                    String fragName = MenusCheckoutMergedFragment.class.getName();
                                                    activity.setRefreshCart(true);
                                                    activity.getSupportFragmentManager().popBackStack(fragName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    } else {
                                        final int redirect = jObj.optInt(Constants.KEY_REDIRECT, 0);
                                        final int appType = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
                                        final int isEmpty = jObj.optInt(Constants.KEY_IS_EMPTY, 0);
                                        DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Log.v("redirect value","redirect value"+redirect);
                                                if(appType == AppConstant.ApplicationType.MENUS && ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag && isEmpty == 1) {
                                                    activity.clearMenusCart();
                                                    activity.setRefreshCart(true);
                                                    activity.performBackPressed();
                                                    activity.setRefreshCart(true);
                                                    activity.performBackPressed();
                                                }
                                                else if(redirect == 1) {
                                                    activity.setRefreshCart(true);
                                                    activity.performBackPressed();
                                                    activity.setRefreshCart(true);
                                                    activity.performBackPressed();
                                                   // activity.performBackPressed();
                                                }/*activity.performBackPressed();*/
                                            }
                                        });
                                    }
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(DialogErrorType.SERVER_ERROR, 1);
                        }
//                        123
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
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
                retryDialog(DialogErrorType.NO_NET, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        buttonPlaceOrder.setEnabled(true);
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


    private void showWalletBalanceLowDialog(final PaymentOption paymentOption) {
        try {
            FreshWalletBalanceLowDialog.Callback callback = new FreshWalletBalanceLowDialog.Callback() {
                @Override
                public void onRechargeNowClicked() {
                    FlurryEventLogger.event(PAYMENT_SCREEN, PAYMENT_SCREEN, RECHARGE);
                    intentToWallet(paymentOption);
                }

                @Override
                public void onPayByCashClicked() {
                    FlurryEventLogger.event(PAYMENT_SCREEN, PAYMENT_SCREEN, PAY_VIA_CASH);
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


    private void intentToWallet(PaymentOption paymentOption) {
        try {
            DecimalFormat df = new DecimalFormat("#");
            Intent intent = new Intent(activity, PaymentActivity.class);
            intent.putExtra(Constants.KEY_WALLET_TYPE, paymentOption.getOrdinal());
            if (paymentOption == PaymentOption.PAYTM) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getPaytmEnabled() == 1)? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        df.format(Math.ceil(payableAmount()
                                - Data.userData.getPaytmBalance())));
            }
            else if (paymentOption == PaymentOption.MOBIKWIK) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getMobikwikEnabled() == 1)? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        df.format(Math.ceil(payableAmount()
                                - Data.userData.getMobikwikBalance())));
            }
            else if (paymentOption == PaymentOption.FREECHARGE) {
                intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH,
                        (Data.userData.getFreeChargeEnabled() == 1)? PaymentActivityPath.WALLET_ADD_MONEY.getOrdinal()
                                : PaymentActivityPath.ADD_WALLET.getOrdinal());
                intent.putExtra(Constants.KEY_PAYMENT_RECHARGE_VALUE,
                        df.format(Math.ceil(payableAmount()
                                - Data.userData.getFreeChargeBalance())));
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
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            int type = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
            if(type == AppConstant.ApplicationType.MENUS){
				if(activity.getVendorOpened().getApplicablePaymentMode() == ApplicablePaymentMode.CASH.getOrdinal()){
					relativeLayoutPaytm.setVisibility(View.GONE);
					relativeLayoutMobikwik.setVisibility(View.GONE);
					relativeLayoutFreeCharge.setVisibility(View.GONE);
				} else if(activity.getVendorOpened().getApplicablePaymentMode() == ApplicablePaymentMode.ONLINE.getOrdinal()){
					relativeLayoutCash.setVisibility(View.GONE);
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateCouponsDataView(){
        try {
            String lastClientId = Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId());
            if(lastClientId.equalsIgnoreCase(Config.getMealsClientId())){
                promoCoupons = Data.userData.getCoupons(ProductType.MEALS);
            } else if(lastClientId.equalsIgnoreCase(Config.getGroceryClientId())) {
                promoCoupons = Data.userData.getCoupons(ProductType.GROCERY);
            } else if(lastClientId.equalsIgnoreCase(Config.getMenusClientId())) {
                if(promoCoupons == null){
                    promoCoupons = new ArrayList<>();
                }
                promoCoupons.clear();
                ArrayList<PromoCoupon> promoCouponsList = Data.userData.getCoupons(ProductType.MENUS);
                if(activity.getVendorOpened().getApplicablePaymentMode() == ApplicablePaymentMode.CASH.getOrdinal())
                {
                    for(PromoCoupon promoCoupon : promoCouponsList)
                    {
                        if(MyApplication.getInstance().getWalletCore().couponOfWhichWallet(promoCoupon) == PaymentOption.CASH.getOrdinal())
                        {
                            promoCoupons.add(promoCoupon);
                        }
                    }
                }
                else
                {
                    promoCoupons.addAll(promoCouponsList);
                }
            } else {
                promoCoupons = Data.userData.getCoupons(ProductType.FRESH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(promoCoupons != null) {
            if(promoCoupons.size() > 0){
                linearLayoutOffers.setVisibility(View.VISIBLE);
                setPromoAmount();
            } else {
                linearLayoutOffers.setVisibility(View.GONE);
            }
            promoCouponsAdapter.setList(promoCoupons);
        }
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


    @Override
    public void onSlotSelected(int position, Slot slot) {
        FlurryEventLogger.event(CHECKOUT_SCREEN, TIMESLOT_CHANGED, "" + (position + 1));
        activity.setSlotSelected(slot);
    }


    public void getCheckoutDataAPI() {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {

                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getMenuRefreshLatLng().latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getMenuRefreshLatLng().longitude));
                params.put(Constants.KEY_CURRENT_LATITUDE, String.valueOf(Data.latitude));
                params.put(Constants.KEY_CURRENT_LONGITUDE, String.valueOf(Data.longitude));

                final int type = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
                String idKey = Constants.KEY_SUB_ITEM_ID;
                if(type == AppConstant.ApplicationType.MENUS){
                    idKey = Constants.KEY_ITEM_ID;
                }

                JSONArray jCart = new JSONArray();
                if (activity.getProductsResponse() != null
                        && activity.getProductsResponse().getCategories() != null) {
                    for (Category category : activity.getProductsResponse().getCategories()) {
                        Log.d(TAG, "" + category.getCategoryName());
                        for (SubItem subItem : category.getSubItems()) {
                            if (subItem.getSubItemQuantitySelected() > 0) {
                                try {
                                    JSONObject jItem = new JSONObject();
                                    //subItem.getSubItemName();
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
                                    categoryName = category.getCategoryName();
                                    itemId = subItem.getSubItemId();
                                    itemName = subItem.getSubItemName();
                                    price = subItem.getPrice();

                                    Product product = new Product()
                                            .setCategory(categoryName)
                                            .setId("" + itemId)
                                            .setName(itemName)
                                            .setPrice(price)
                                            .setQuantity(qty);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                params.put(Constants.KEY_CART, jCart.toString());
                params.put(Constants.ORDER_AMOUNT, Utils.getMoneyDecimalFormat().format(subTotalAmount));


                if(type == AppConstant.ApplicationType.MEALS) {
                    params.put(Constants.STORE_ID, ""+ Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType));
                    params.put(Constants.GROUP_ID, ""+activity.getProductsResponse().getCategories().get(0).getCurrentGroupId());
                } else if(type == AppConstant.ApplicationType.MENUS){
                    params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(activity.getVendorOpened().getRestaurantId()));
                    String data = new Gson().toJson(activity.getVendorOpened(), MenusResponse.Vendor.class);
                    params.put(Constants.KEY_RESTAURANT_DATA, data);
                }
                params.put(Constants.INTERATED, "1");
                params.put(Constants.KEY_CLIENT_ID, ""+ Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));
                Log.i(TAG, "getAllProducts params=" + params.toString());

                Callback<UserCheckoutResponse> callback = new Callback<UserCheckoutResponse>() {
                    @Override
                    public void success(UserCheckoutResponse userCheckoutResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt(Constants.KEY_FLAG);
                                if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    buttonPlaceOrder.setText(getActivity().getResources().getString(R.string.place_order));
                                    activity.setUserCheckoutResponse(userCheckoutResponse);
                                    Log.v(TAG, "" + userCheckoutResponse.getCheckoutData().getLastAddress());

                                    setActivityLastAddressFromResponse(userCheckoutResponse);
                                    updateCartDataView();

                                    setDeliverySlotsDataUI();

                                    updateAddressView();

                                    String lastClientId = Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId());
                                    if(lastClientId.equalsIgnoreCase(Config.getMealsClientId())){
                                        if(Data.getMealsData().getPromoCoupons() == null){
                                            Data.getMealsData().setPromoCoupons(new ArrayList<PromoCoupon>());
                                        }
                                        Data.getMealsData().getPromoCoupons().clear();
                                        if(userCheckoutResponse.getPromotions() != null){
                                            Data.getMealsData().getPromoCoupons().addAll(userCheckoutResponse.getPromotions());
                                        }
                                        if(userCheckoutResponse.getCoupons() != null){
                                            Data.getMealsData().getPromoCoupons().addAll(userCheckoutResponse.getCoupons());
                                        }
                                    } else if(lastClientId.equalsIgnoreCase(Config.getGroceryClientId())) {
                                        if(Data.getGroceryData().getPromoCoupons() == null){
                                            Data.getGroceryData().setPromoCoupons(new ArrayList<PromoCoupon>());
                                        }
                                        Data.getGroceryData().getPromoCoupons().clear();
                                        if(userCheckoutResponse.getPromotions() != null){
                                            Data.getGroceryData().getPromoCoupons().addAll(userCheckoutResponse.getPromotions());
                                        }
                                        if(userCheckoutResponse.getCoupons() != null){
                                            Data.getGroceryData().getPromoCoupons().addAll(userCheckoutResponse.getCoupons());
                                        }
                                    } else if(lastClientId.equalsIgnoreCase(Config.getMenusClientId())) {
                                        if(Data.getMenusData().getPromoCoupons() == null){
                                            Data.getMenusData().setPromoCoupons(new ArrayList<PromoCoupon>());
                                        }
                                        Data.getMenusData().getPromoCoupons().clear();
                                        if(userCheckoutResponse.getPromotions() != null){
                                            Data.getMenusData().getPromoCoupons().addAll(userCheckoutResponse.getPromotions());
                                        }
                                        if(userCheckoutResponse.getCoupons() != null){
                                            Data.getMenusData().getPromoCoupons().addAll(userCheckoutResponse.getCoupons());
                                        }
                                    } else {
                                        if(Data.getFreshData().getPromoCoupons() == null){
                                            Data.getFreshData().setPromoCoupons(new ArrayList<PromoCoupon>());
                                        }
                                        Data.getFreshData().getPromoCoupons().clear();
                                        if(userCheckoutResponse.getPromotions() != null){
                                            Data.getFreshData().getPromoCoupons().addAll(userCheckoutResponse.getPromotions());
                                        }
                                        if(userCheckoutResponse.getCoupons() != null){
                                            Data.getFreshData().getPromoCoupons().addAll(userCheckoutResponse.getCoupons());
                                        }
                                    }
                                    updateCouponsDataView();
                                    try {
                                        if(cartChangedRefreshCheckout){
											setSelectedCoupon(promoCoupons.indexOf(activity.getSelectedPromoCoupon()));
										}
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    cartChangedRefreshCheckout = false;

                                } else{
                                    final int redirect = jObj.optInt(Constants.KEY_REDIRECT, 0);
                                    DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(redirect == 1) {
                                                activity.performBackPressed();
                                            }
                                        }
                                    });
                                    buttonPlaceOrder.setText(getActivity().getResources().getString(R.string.connection_lost_try_again));
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(DialogErrorType.SERVER_ERROR);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
                        DialogPopup.dismissLoadingDialog();
                        retryDialog(DialogErrorType.CONNECTION_LOST);
                    }
                };
                new HomeUtil().putDefaultParams(params);
                if(type == AppConstant.ApplicationType.MENUS){
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

    private void setActivityLastAddressFromResponse(UserCheckoutResponse userCheckoutResponse){
        try {
            if(!activity.isAddressConfirmed() && TextUtils.isEmpty(activity.getSelectedAddressType())) {
                if (userCheckoutResponse.getCheckoutData().getLastAddress() != null) {
                    activity.setSelectedAddress(userCheckoutResponse.getCheckoutData().getLastAddress());
                    activity.setSelectedAddressType(userCheckoutResponse.getCheckoutData().getLastAddressType());
                    activity.setSelectedAddressId(userCheckoutResponse.getCheckoutData().getLastAddressId());
                    try {
                        activity.setSelectedLatLng(new LatLng(Double.parseDouble(userCheckoutResponse.getCheckoutData().getLastAddressLatitude()),
                                Double.parseDouble(userCheckoutResponse.getCheckoutData().getLastAddressLongitude())));
                        activity.setRefreshCart(true);
                    } catch (Exception e) {
                    }
                } else {
                    activity.setSelectedAddress("");
                    activity.setSelectedLatLng(null);
                    activity.setSelectedAddressId(0);
                    activity.setSelectedAddressType("");
                }
                relativeLayoutDeliveryAddress.setEnabled(true);
            }
            else {
                if(type == AppConstant.ApplicationType.MENUS) {
                    relativeLayoutDeliveryAddress.setEnabled(false);
                }
            }

//            if(type != AppConstant.ApplicationType.MENUS) {
//                if (!checkoutSaveData.isDefault()) {
//                    activity.setSelectedAddress(checkoutSaveData.getAddress());
//                    activity.setSelectedAddressType(checkoutSaveData.getAddressType());
//                    activity.setSelectedAddressId(checkoutSaveData.getAddressId());
//                    activity.setSelectedLatLng(new LatLng(checkoutSaveData.getLatitude(), checkoutSaveData.getLongitude()));
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getCheckoutDataAPI();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
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
        }
    }



    @Subscribe
    public void onUpdateListEvent(AddressAdded event) {
        if (event.flag) {
            updateAddressView();
        }
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
        textViewAddressName.setVisibility(View.GONE);
        textViewAddressValue.setTextColor(activity.getResources().getColor(R.color.text_color));
        if(!TextUtils.isEmpty(activity.getSelectedAddress())) {
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
        }
    }


    @Override
    public void onPlusClicked(int position, SubItem subItem) {
        cartChangedRefreshCheckout = true;
        updateCartDataView();
    }

    @Override
    public void onMinusClicked(int position, SubItem subItem) {
        cartChangedRefreshCheckout = true;
        updateCartDataView();
        if(subItem.getSubItemQuantitySelected() == 0){
            activity.subItemsInCart.remove(position);
            checkIfEmpty();
        }
    }


    @Override
    public boolean checkForMinus(int position, SubItem subItem) {
        return activity.checkForMinus(position, subItem);
    }

    @Override
    public void minusNotDone(int position, SubItem subItem) {
        activity.clearMealsCartIfNoMainMeal();
    }



    public void deleteCart() {
        for(SubItem subItem : activity.subItemsInCart){
            subItem.setSubItemQuantitySelected(0);
        }
        updateCartDataView();
        activity.subItemsInCart.clear();
        freshCartItemsAdapter.notifyDataSetChanged();
        checkIfEmpty();

    }

    private void checkIfEmpty(){
        if(activity.subItemsInCart.size() == 0){
            if(activity.isMealAddonItemsAvailable()){
                activity.performBackPressed();
            }
            activity.performBackPressed();
        }
    }


    @Override
    public void onCouponSelected() {
        if(cartChangedRefreshCheckout){
            getCheckoutDataAPI();
        }
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
    }

    @Override
    public void setSelectedCoupon(int position) {
        PromoCoupon promoCoupon;
        if (promoCoupons != null && position > -1 && position < promoCoupons.size()) {
            promoCoupon = promoCoupons.get(position);
        } else {
            promoCoupon = noSelectionCoupon;
        }
        if (MyApplication.getInstance().getWalletCore().displayAlertAndCheckForSelectedWalletCoupon(activity, activity.getPaymentOption().getOrdinal(), promoCoupon)) {
            activity.setSelectedPromoCoupon(promoCoupon);
        }
        setPromoAmount();
        updateCartUI();
    }


    private void updateCartTopBarView(Pair<Double, Integer> pair){
        textViewCartItems.setText(activity.getString(R.string.cart_items_format, String.valueOf(pair.second)));
//        textViewCartTotal.setText(activity.getString(R.string.rupees_value_format_without_space,
//                Utils.getMoneyDecimalFormatWithoutFloat().format(pair.first)));
        /*textViewCartTotal.setText(activity.getString(R.string.rupees_value_format,
                Utils.getMoneyDecimalFormatWithoutFloat().format(payableAmount())));*/
    }

    private void updateCartDataView(){
        try {
            Pair<Double, Integer> pair = activity.updateCartValuesGetTotalPrice();
            subTotalAmount = pair.first;
            updateCartTopBarView(pair);
            updateCartUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PaymentOption getSavedPaymentOption(){
        if(checkoutSaveData.isDefault()){
            return MyApplication.getInstance().getWalletCore().getDefaultPaymentOption();
        } else{
            return MyApplication.getInstance().getWalletCore().getPaymentOptionFromInt(checkoutSaveData.getPaymentMode());
        }
    }


    private double totalUndiscounted(){
        return totalAmount() + promoAmount;
    }

    private double totalAmount(){
        double totalAmount = subTotalAmount + totalTaxAmount - promoAmount;
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
        if(type == AppConstant.ApplicationType.MENUS && activity.getPaymentOption() == PaymentOption.CASH){
            return 0d;
        } else {
            return Math.min(totalAmount(), Data.userData.getJugnooBalance());
        }
    }

}