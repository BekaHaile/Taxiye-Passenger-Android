package com.sabkuchfresh.commoncalls;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.datastructure.VendorDirectSearch;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.Category;
import com.sabkuchfresh.retrofit.model.menus.CustomiseOptionsId;
import com.sabkuchfresh.retrofit.model.menus.CustomizeItemSelected;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.retrofit.model.menus.Subcategory;
import com.sabkuchfresh.retrofit.model.menus.VendorMenuResponse;
import com.sabkuchfresh.utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * For fetching feedback reviews for a particular restaurant
 */
public class ApiFetchRestaurantMenu {

    private final String TAG = ApiFetchRestaurantMenu.class.getSimpleName();

    private FreshActivity activity;
    private Callback callback;

    public ApiFetchRestaurantMenu(FreshActivity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void hit(final int restaurantId, final double latitude, final double longitude, final boolean directCheckout,
                    final JSONArray cartItemToSet, final LatLng reOrderLatlng, final int reorderId,
                    final String reorderAddress, final VendorDirectSearch vendorDirectSearch) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(longitude));
                params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(restaurantId));
                params.put(Constants.KEY_CLIENT_ID, Config.getLastOpenedClientId(activity));
                params.put(Constants.INTERATED, "1");

                // if api hit from home list where MerchantInfoFragment is not opened and direct Checkout page is
                // not to be opened
                if(!directCheckout
                        && activity.isMenusIsOpenMerchantInfo()
                        //&& activity.shouldOpenMerchantInfoFragment()
                        && activity.getMerchantInfoFragment() == null
                        && vendorDirectSearch == null){
                    params.put(Constants.KEY_NOT_SEND_MENU, "1");
                }

                Log.i(TAG, "restaurantMenu params=" + params.toString());

                new HomeUtil().putDefaultParams(params);
                RestClient.getMenusApiService().restaurantMenu(params, new retrofit.Callback<VendorMenuResponse>() {
                    @Override
                    public void success(VendorMenuResponse productsResponse, final Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i(TAG, "getVendorMenu response = " + responseStr);
                        try {
                            final JSONObject jObj = new JSONObject(responseStr);
                            String message = productsResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == productsResponse.getFlag()) {

                                    if (cartItemToSet != null) {

                                        //Restaurant is not available
                                        if(productsResponse.getVendor()!=null && productsResponse.getVendor().getIsAvailable()!=1){
                                            DialogPopup.dismissLoadingDialog();
                                            DialogPopup.alertPopup(activity, "", activity.getString(R.string.reorder_rest_unavailable));

                                            return;
                                        }

                                     /*   if(productsResponse.getRestuarantOutOfRadius()){
                                            DialogPopup.alertPopup(activity, "", activity.getString(R.string.reorder_rest_out_of_radius));
                                            return;
                                        }*/


                                        activity.clearMenusCart(activity.getAppType());
                                        setVendorDataToFreshActivity(productsResponse);

                                        ArrayList<ItemSelected> itemsSelected = prepareMenuItemsArray(cartItemToSet);
                                        int quantityReorderItems = itemsSelected.size();
                                        int count = 0;
                                        for (Category category : activity.getMenuProductsResponse().getCategories()) {
                                            if (category.getSubcategories() != null) {
                                                for (Subcategory subcategory : category.getSubcategories()) {
                                                    count += setIterateItems(subcategory.getItems(), itemsSelected);
                                                }
                                            } else if (category.getItems() != null) {
                                                count += setIterateItems(category.getItems(), itemsSelected);
                                            }
                                        }
                                        if (count == 0) {
                                            //All items in re-oder case have been disabled so do not go to checkout ask user to go to home or VendorMenuFragment
                                            DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",  activity.getString(R.string.reorder_no_items_available),
                                                    activity.getString(R.string.change_order) ,activity.getString(R.string.edit_order)
                                                   ,
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            //stay on menu home fragment

                                                        }
                                                    }, new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            openNextFragment(false, vendorDirectSearch);
                                                            updateCartAndSetJeanie(jObj);

                                                        }
                                                    }, false, false);

                                        }else if( quantityReorderItems!=count){
                                            //Give user a confirmation popup before going to next checkout fragment because some items are missing
                                            DialogPopup.alertPopupWithListener(activity, "", activity.getString(R.string.reorder_items_missing), new View.OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    activity.setReorderLatlngToAdrress(reOrderLatlng,reorderAddress);
                                                    Prefs.with(activity).save(activity.getAppType()== AppConstant.ApplicationType.MENUS?Constants.CART_STATUS_REORDER_ID:Constants.CART_STATUS_REORDER_ID_CUSTOMER_DELIVERY,reorderId);//save reoderId in cart to send in checkout
                                                    openNextFragment(true, vendorDirectSearch);
                                                    updateCartAndSetJeanie(jObj);
                                                }
                                            });

                                        }else{
                                            //Everything is okay go to checkout
                                            Prefs.with(activity).save(activity.getAppType()== AppConstant.ApplicationType.MENUS?Constants.CART_STATUS_REORDER_ID:Constants.CART_STATUS_REORDER_ID_CUSTOMER_DELIVERY,reorderId);//save reoderId in cart to send in checkout
                                            activity.setReorderLatlngToAdrress(reOrderLatlng,reorderAddress);
                                            openNextFragment(true, vendorDirectSearch);
                                            updateCartAndSetJeanie(jObj);
                                        }


                                    } else {
                                        if(activity.getMerchantInfoFragment() == null
                                                || (activity.getMerchantInfoFragment().getRestaurantId() == restaurantId)) {
                                            setVendorDataToFreshActivity(productsResponse);
                                            activity.updateItemListFromSPDB();
                                            if (activity.getVendorOpened().getIsClosed() == 1) {
                                                activity.clearMenusCart(activity.getAppType());
                                            }
                                            updateCartAndSetJeanie(jObj);
                                            openNextFragment(directCheckout, vendorDirectSearch);
                                        }


                                    }




                                    callback.onSuccess();

                                } else {
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "paytmAuthenticateRecharge error" + error.toString());
                        DialogPopup.dismissLoadingDialog();
                        retryDialogVendorData(DialogErrorType.CONNECTION_LOST, restaurantId, directCheckout);
                    }
                });
            } else {
                retryDialogVendorData(DialogErrorType.NO_NET, restaurantId, directCheckout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openNextFragment(boolean goToCheckout, VendorDirectSearch vendorDirectSearch) {

        // if from direct search open vendor
//        if(vendorDirectSearch!=null){
//            Bundle args = new Bundle();
//            args.putInt(Constants.ITEM_CATEGORY_ID,vendorDirectSearch.getCategoryId());
//            args.putInt(Constants.ITEM_SUB_CATEGORY_ID,vendorDirectSearch.getSubcategoryId());
//            args.putInt(Constants.ITEM_RESTAURANT_ITEM_ID,vendorDirectSearch.getItemId());
//            activity.getTransactionUtils().openVendorMenuFragment(activity, activity.getRelativeLayoutContainer(),args);
//            return;
//        }
//
//        if (goToCheckout) {
//            activity.openCart(activity.getAppType(), true);
//        } else {
//            if(activity.isMenusIsOpenMerchantInfo()
//                    && activity.getMerchantInfoFragment() == null){
//                //if(activity.shouldOpenMerchantInfoFragment()
//                       // && activity.getMerchantInfoFragment() == null){
//                activity.getTransactionUtils().openMerchantInfoFragment(activity, activity.getRelativeLayoutContainer());
//            } else {
//                activity.getTransactionUtils().openVendorMenuFragment(activity, activity.getRelativeLayoutContainer());
//            }
//        }

        // if from direct search open vendor
        if(activity.getMenuProductsResponse().getVendor().getActivationStatus() == 1) {
            if (vendorDirectSearch != null) {
                Bundle args = new Bundle();
                args.putInt(Constants.ITEM_CATEGORY_ID, vendorDirectSearch.getCategoryId());
                args.putInt(Constants.ITEM_SUB_CATEGORY_ID, vendorDirectSearch.getSubcategoryId());
                args.putInt(Constants.ITEM_RESTAURANT_ITEM_ID, vendorDirectSearch.getItemId());
                activity.getTransactionUtils().openVendorMenuFragment(activity, activity.getRelativeLayoutContainer(), args);
                return;
            }

            if (goToCheckout) {
                activity.openCart(activity.getAppType(), true);
            } else {
                if (activity.isMenusIsOpenMerchantInfo()
                        && activity.getMerchantInfoFragment() == null) {
                    //if(activity.shouldOpenMerchantInfoFragment()
                    // && activity.getMerchantInfoFragment() == null){
                    activity.getTransactionUtils().openMerchantInfoFragment(activity, activity.getRelativeLayoutContainer());
                } else {
                    activity.getTransactionUtils().openVendorMenuFragment(activity, activity.getRelativeLayoutContainer());
                }
            }
        } else {
            MenusResponse menusResponse = activity.getMenusResponse();
            menusResponse.setVendors(new ArrayList<MenusResponse.Vendor>());
//            activity.getMenusFragment().getDeliveryHomeAdapter().setList(menusResponse, false, true, false);
        }
    }

    private void updateCartAndSetJeanie(JSONObject jObj) {
        if (activity.menusSort == -1) {
            activity.menusSort = jObj.optInt(Constants.SORTED_BY, 0);
        }

        GAUtils.event(GACategory.MENUS, GAAction.HOME + GAAction.RESTAURANT_CLICKED, activity.getVendorOpened().getName());


        activity.updateCartValuesGetTotalPrice();
        activity.getFabViewTest().hideJeanieHelpInSession();
    }

    private void setVendorDataToFreshActivity(VendorMenuResponse productsResponse) {

        activity.setVendorOpened(productsResponse.getVendor(), productsResponse.getCurrencyCode(), productsResponse.getCurrency());
        if (activity.getAppType() == AppConstant.ApplicationType.FEED) {
            Data.AppType = AppConstant.ApplicationType.MENUS;
            Prefs.with(activity).save(Constants.APP_TYPE, AppConstant.ApplicationType.MENUS);
            Prefs.with(activity).save(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getMenusClientId());
        }
        if(productsResponse.getCategories() == null || productsResponse.getCategories().isEmpty()){
            productsResponse.setCategories(null);
        }
        activity.setMenuProductsResponse(productsResponse);
    }

    private void retryDialogVendorData(DialogErrorType dialogErrorType, final int restaurantId, final boolean directCheckout) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        callback.onRetry(view, restaurantId, directCheckout);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                        callback.onNoRetry(view);
                    }
                }, Data.getCurrentIciciUpiTransaction(activity.getAppType()) == null);
    }


    public interface Callback {
        void onSuccess();

        void onFailure();

        void onRetry(View view, int restaurantId, boolean directCheckout);

        void onNoRetry(View view);
    }

    Gson gson = new Gson();
    JsonParser parser = new JsonParser();

    /**
     * @param jsonArray Json array of orderd_items as in order history api
     * @return Hashmap consisting  of ItemSelected corresponding to its itemId;
     */
    private ArrayList<ItemSelected> prepareMenuItemsArray(JSONArray jsonArray) {

        ArrayList<ItemSelected> mapItems = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (jsonArray.getJSONObject(i) != null) {
                    JsonElement mJson = parser.parse(jsonArray.getJSONObject(i).toString());
                    ItemSelected itemSelected = gson.fromJson(mJson, ItemSelected.class);


                    if (itemSelected != null) {
                        //To Assign Value to arrayList<Integer> from ArrayList<CustomiseOptionsId>
                        for (CustomizeItemSelected customizeItemSelected : itemSelected.getCustomizeItemSelectedList()) {
                            ArrayList<Integer> customisedOptionsIdList = new ArrayList<>();
                            for (CustomiseOptionsId customiseOptionsId : customizeItemSelected.getOptions()) {
                                customisedOptionsIdList.add(customiseOptionsId.getId());
                            }
                            customizeItemSelected.setCustomizeOptions(customisedOptionsIdList);
                        }
                    }

                    mapItems.add(itemSelected);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return mapItems;


    }

    private int setIterateItems(List<Item> items,ArrayList<ItemSelected> itemSelected) {
        int count = 0;
//        ArrayList<ItemSelected> itemSelected = prepareMenuItemsArray(jCart);

        for (Item item : items) {


            for (ItemSelected itemSelected1 : itemSelected) {
                if (item.getRestaurantItemId().equals(itemSelected1.getRestaurantItemId())) {

                    item.getItemSelectedList().add(itemSelected1);
                    count++;


                }
            }
            if(item.getItemSelectedList().size() > 0){
                for(ItemSelected itemSaved : item.getItemSelectedList()){
                    if (itemSaved.getQuantity() > 0) {
                        itemSaved.setTotalPrice(item.getCustomizeItemsSelectedTotalPriceForItemSelected(itemSaved));
                    }
                }
            }


        }
        return count;
    }
}
