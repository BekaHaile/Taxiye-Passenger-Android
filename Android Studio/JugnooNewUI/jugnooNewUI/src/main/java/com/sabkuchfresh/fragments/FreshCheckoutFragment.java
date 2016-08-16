package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.ecommerce.Product;
import product.clicklabs.jugnoo.R;
import com.sabkuchfresh.SplashNewActivity;
import com.sabkuchfresh.adapters.FreshCheckoutAdapter;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.analytics.NudgeClient;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.datastructure.ApiResponseFlags;
import com.sabkuchfresh.datastructure.DialogErrorType;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.RestClient;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.DeliverySlot;
import com.sabkuchfresh.retrofit.model.Slot;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.retrofit.model.UserCheckoutResponse;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.AppStatus;
import com.sabkuchfresh.utils.Constants;
import com.sabkuchfresh.utils.Data;
import com.sabkuchfresh.utils.DateOperations;
import com.sabkuchfresh.utils.DialogPopup;
import com.sabkuchfresh.utils.Fonts;
import com.sabkuchfresh.utils.JSONParser;
import com.sabkuchfresh.utils.Log;
import com.sabkuchfresh.utils.Prefs;
import com.sabkuchfresh.utils.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FreshCheckoutFragment extends Fragment implements View.OnClickListener, FlurryEventNames, FreshCheckoutAdapter.Callback {

    private final String TAG = FreshCheckoutFragment.class.getSimpleName();
    private LinearLayout linearLayoutRoot;
    //
//	private TextView textViewTotalAmountValue, textViewDeliveryChargesValue, textViewAmountPayableValue;
//	private RelativeLayout relativeLayoutAddress;
//	private TextView textViewAddAddress, textViewAddressValue;
//	private ImageView imageViewEditAddress, imageViewEditSlot;
//	private RelativeLayout relativeLayoutSlot;
//	private TextView textViewDay, textViewSlotValue;
    private Button buttonProceedToPayment;

    private View rootView;
    private FreshActivity activity;
    private ArrayList<Slot> checkout = new ArrayList<>();
    private ArrayList<Slot> slots = new ArrayList<>();
    Bus mBus;

    FreshCheckoutAdapter checkoutAdapter;

    public FreshCheckoutFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mBus.unregister(this);
    }


    private void setCheckoutScreen() {

        checkout.clear();

        String address = "";
        String camountPayable = "";
        String ctotalAmount = "";
        String deliveryCharge = "";
        String isDeliveryCharger = "";

        try {
            if (activity.getSelectedAddress().equalsIgnoreCase("")) {
                address = activity.getUserCheckoutResponse().getCheckoutData().getLastAddress();
                activity.setSelectedAddress(activity.getUserCheckoutResponse().getCheckoutData().getLastAddress());
            } else {
                address = activity.getSelectedAddress();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (activity.getProductsResponse() != null
                    && activity.getProductsResponse().getDeliveryInfo() != null) {
                double totalAmount = activity.updateCartValuesGetTotalPrice().first;
                double amountPayable = totalAmount;
                if (activity.getProductsResponse().getDeliveryInfo().getMinAmount() > totalAmount) {
                    deliveryCharge = String.format(activity.getResources().getString(R.string.rupees_value_format),
                            Utils.getMoneyDecimalFormat().format(activity.getProductsResponse().getDeliveryInfo().getDeliveryCharges()));
                    isDeliveryCharger = "yes";
                    amountPayable = amountPayable + activity.getProductsResponse().getDeliveryInfo().getDeliveryCharges();

                } else {
                    isDeliveryCharger = "";
                }

                ctotalAmount = String.format(activity.getResources().getString(R.string.rupees_value_format),
                        Utils.getMoneyDecimalFormat().format(amountPayable));
                camountPayable = String.format(activity.getResources().getString(R.string.rupees_value_format),
                        Utils.getMoneyDecimalFormat().format(amountPayable));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Slot slotDay = new Slot();
        slotDay.setCaddress(address);
        slotDay.setCamount(ctotalAmount);
        slotDay.setCdelivery(deliveryCharge);
        slotDay.setIsdelivery(isDeliveryCharger);
        slotDay.setCtotal(camountPayable);
        slotDay.setSlotViewType(FreshCheckoutAdapter.SlotViewType.HEADER);

        checkout.add(slotDay);
        checkoutAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fresh_checkout, container, false);


        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);

        activity.setSlotToSelect(null);
        activity.setSlotSelected(null);

        mBus = (activity).getBus();

        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        try {
            if (linearLayoutRoot != null) {
                new ASSL(activity, linearLayoutRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        RecyclerView recyclerViewDeliverySlots = (RecyclerView) rootView.findViewById(R.id.recyclerViewDeliverySlots);
        recyclerViewDeliverySlots.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewDeliverySlots.setItemAnimator(new DefaultItemAnimator());
        recyclerViewDeliverySlots.setHasFixedSize(false);

        checkout.clear();
        activity.setSplInstr("");
        checkoutAdapter = new FreshCheckoutAdapter(activity, checkout, this);

        recyclerViewDeliverySlots.setAdapter(checkoutAdapter);

        setCheckoutScreen();

        buttonProceedToPayment = (Button) rootView.findViewById(R.id.buttonProceedToPayment);
        buttonProceedToPayment.setTypeface(Fonts.mavenRegular(activity));
        buttonProceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryEventLogger.event(CHECKOUT_SCREEN, SCREEN_TRANSITION, PAYMENT_SCREEN);
                Utils.hideSoftKeyboard(getActivity(), linearLayoutRoot);
                if (buttonProceedToPayment.getText().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.connection_lost_try_again))) {
                    getCheckoutData();
                } else if (activity.getSlotSelected() == null) {
                    Toast.makeText(activity, activity.getResources().getString(R.string.please_select_a_delivery_slot),
                            Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(activity.getSelectedAddress()) || "".equalsIgnoreCase(activity.getSelectedAddress())) {
                    Toast.makeText(activity, activity.getResources().getString(R.string.please_select_a_delivery_address),
                            Toast.LENGTH_LONG).show();
                } else {
                    activity.getTransactionUtils().openPaymentFragment(activity, activity.getRelativeLayoutContainer());
                    NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FRESH_PROCEED_TO_PAYMENT_CLICKED, null);
                }
            }
        });
        FlurryEventLogger.checkoutTrackEvent(AppConstant.EventTracker.CHECKOUT, activity.productList);
        getCheckoutData();
        NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FRESH_CHECKOUT_CLICKED, null);

        return rootView;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
//			setAddressAndTimeSlot();
        }
    }

    public void getCheckoutData() {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {

                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));

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
                                    jItem.put(Constants.KEY_SUB_ITEM_ID, subItem.getSubItemId());
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
//                                                                .setPosition(4);
                                    //productList.add(product);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                params.put(Constants.KEY_CART, jCart.toString());

                int type = Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType);
                if(type == AppConstant.ApplicationType.MEALS) {
                    params.put(Constants.STORE_ID, ""+ Prefs.with(activity).getInt(Constants.APP_TYPE, Data.AppType));
                    params.put(Constants.GROUP_ID, ""+activity.getProductsResponse().getCategories().get(0).getCurrentGroupId());
                }
                Log.i(TAG, "getAllProducts params=" + params.toString());

                RestClient.getFreshApiService().userCheckoutData(params, new Callback<UserCheckoutResponse>() {
                    @Override
                    public void success(UserCheckoutResponse userCheckoutResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getAllProducts response = " + responseStr);
                        DialogPopup.dismissLoadingDialog();
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                int flag = jObj.getInt(Constants.KEY_FLAG);
                                if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    buttonProceedToPayment.setText(getActivity().getResources().getString(R.string.proceed_to_payment));
                                    activity.setUserCheckoutResponse(userCheckoutResponse);
                                    Log.v(TAG, "" + userCheckoutResponse.getCheckoutData().getLastAddress());
                                    try {
                                        Prefs.with(activity).save(activity.getResources().getString(R.string.pref_loc_lati), userCheckoutResponse.getCheckoutData().getLastAddressLatitude());
                                        Prefs.with(activity).save(activity.getResources().getString(R.string.pref_loc_longi), userCheckoutResponse.getCheckoutData().getLastAddressLongitude());
                                    } catch (Exception e) {
                                        // if sometimes data not found or some other error occures
                                    }
                                    checkout.get(0).setCaddress(userCheckoutResponse.getCheckoutData().getLastAddress());
                                    activity.setSelectedAddress(userCheckoutResponse.getCheckoutData().getLastAddress());
                                    generateSlots();
//								setAddressAndTimeSlot();
                                } else{
                                    DialogPopup.alertPopup(activity, "", message);
                                    buttonProceedToPayment.setText(getActivity().getResources().getString(R.string.connection_lost_try_again));
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
                });
            } else {
                retryDialog(DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void retryDialog(DialogErrorType dialogErrorType) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        getCheckoutData();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }


    private void generateSlots() {
        if (activity.getUserCheckoutResponse() != null
                && activity.getUserCheckoutResponse().getCheckoutData() != null
                && activity.getUserCheckoutResponse().getCheckoutData().getDeliverySlots() != null) {
            slots.clear();

            if (activity.getSlotSelected() != null) {
                verifySlotTiming(activity.getSlotSelected());
                if (!activity.getSlotSelected().isEnabled()) {
                    activity.setSlotSelected(null);
                }
            }


            for (DeliverySlot deliverySlot : activity.getUserCheckoutResponse().getCheckoutData().getDeliverySlots()) {
                int slotsEnabled = 0;
                for (Slot slot : deliverySlot.getSlots()) {
                    slot.setSlotViewType(FreshCheckoutAdapter.SlotViewType.SLOT_TIME);
                    slot.setDayName(deliverySlot.getDayName());
                    verifySlotTiming(slot);
                    slotsEnabled = slot.isEnabled() ? slotsEnabled + 1 : slotsEnabled;
                    slots.add(slot);
                    if (activity.getSlotSelected() == null && slot.isEnabled()) {
                        activity.setSlotSelected(slot);
                    }
                    activity.setSlotToSelect(activity.getSlotSelected());
                }
            }

//            Slot slotDayHeader = new Slot();
//            slotDayHeader.setSlotViewType(FreshCheckoutAdapter.SlotViewType.SLOT_STATUS);
//            checkout.add(slotDayHeader);
//
            checkout.addAll(slots);
//            Slot slotLine = new Slot();
//            slotLine.setSlotViewType(FreshCheckoutAdapter.SlotViewType.DIVIDER);
//            checkout.add(slotLine);

            Slot slotDay = new Slot();
            slotDay.setSlotViewType(FreshCheckoutAdapter.SlotViewType.FEED);
            checkout.add(slotDay);

            checkoutAdapter.notifyDataSetChanged();
        }
    }

    private void verifySlotTiming(Slot slot) {
        if (slot != null) {
            if (slot.getDayId() == DateOperations.getCurrentDayInt()) {
                if (slot.getThresholdTimeSeconds() < DateOperations.getCurrentDayTimeSeconds()) {
                    slot.setEnabled(false);
                } else {
                    slot.setEnabled(true);
                }
            } else {
                slot.setEnabled(true);
            }
            slot.setEnabled(!(slot.getDayId() == DateOperations.getCurrentDayInt()
                    && slot.getThresholdTimeSeconds() < DateOperations.getCurrentDayTimeSeconds()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//		generateSlots();
//		getFreshDeliverySlotsDialog().notifySlots();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewEditAddress:
//                relativeLayoutAddress.performClick();
                break;
            case R.id.imageViewEditSlot:
//                relativeLayoutSlot.performClick();
                break;
        }
    }

    @Subscribe
    public void onUpdateListEvent(AddressAdded event) {
        if (event.flag) {
            // New Address added
            activity.setSelectedAddress(Prefs.with(activity).getString(activity.getResources().getString(R.string.pref_local_address), ""));
            checkout.get(0).setCaddress(Prefs.with(activity).getString(activity.getResources().getString(R.string.pref_local_address), ""));
            checkoutAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSlotSelected(int position, Slot slot) {
        FlurryEventLogger.event(CHECKOUT_SCREEN, TIMESLOT_CHANGED, "" + (position + 1));
        activity.setSlotToSelect(slot);
        activity.setSlotSelected(slot);
    }

    @Override
    public void onAddressClick() {
        FlurryEventLogger.event(CHECKOUT_SCREEN, SCREEN_TRANSITION, ADDRESS_SCREEN);
        if(activity.getUserCheckoutResponse().getCheckoutData().getDeliveryAddresses().size()>0) {
            activity.getTransactionUtils().openAddressFragment(activity, activity.getRelativeLayoutContainer());
        } else {
            activity.openMapAddress();
        }
        NudgeClient.trackEventUserId(activity, FlurryEventNames.NUDGE_FRESH_ADDRESS_CLICKED, null);
    }
}