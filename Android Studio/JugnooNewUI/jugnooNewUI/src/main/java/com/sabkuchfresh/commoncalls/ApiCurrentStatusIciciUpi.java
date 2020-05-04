package com.sabkuchfresh.commoncalls;

import android.view.View;

import com.sabkuchfresh.dialogs.OrderCompleteReferralDialog;
import com.sabkuchfresh.enums.IciciPaymentOrderStatus;
import com.sabkuchfresh.fragments.FreshCheckoutMergedFragment;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.home.FreshOrderCompleteDialog;
import com.sabkuchfresh.home.OrderCompletDialog;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.common.IciciPaymentRequestStatus;
import com.sabkuchfresh.utils.AppConstant;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Parminder Saini on 20/06/17.
 */

public class ApiCurrentStatusIciciUpi {


    public interface ApiCurrentStatusListener {
        void onGoToCheckout(IciciPaymentOrderStatus iciciPaymentOrderStatus);


    }



    public static void checkIciciPaymentStatusApi(final FreshActivity activity, final boolean isMenus, final ApiCurrentStatusListener apiCurrentStatusListener, final int apptype) {
        if (MyApplication.getInstance().isOnline()) {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ORDER_ID, String.valueOf(activity.getPlaceOrderResponse().getOrderId()));
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            if(isMenus){
                params.put(Constants.KEY_CLIENT_ID,activity.isDeliveryOpenInBackground()? Config.getDeliveryCustomerClientId(): Config.getMenusClientId());

            }else{
                params.put(Constants.KEY_CLIENT_ID, Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));

            }
            HomeUtil.addDefaultParams(params);
            Callback<IciciPaymentRequestStatus> iciciPaymentStatusCallback = new Callback<IciciPaymentRequestStatus>() {
                @Override
                public void success(IciciPaymentRequestStatus commonResponse, Response response) {
                    if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, commonResponse.getFlag(), commonResponse.getError(), commonResponse.getMessage())) {

                        if (commonResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {

                            if (commonResponse.getStatus() == IciciPaymentOrderStatus.PENDING || commonResponse.getStatus() == IciciPaymentOrderStatus.FAILURE || commonResponse.getStatus() == IciciPaymentOrderStatus.EXPIRED) {

                                if (commonResponse.getStatus() == IciciPaymentOrderStatus.PENDING) {

                                    PlaceOrderResponse placeOrderResponse = Data.getCurrentIciciUpiTransaction(apptype);

                                    if (placeOrderResponse.getIcici() != null) {
                                        placeOrderResponse.getIcici().setIciciPaymentOrderStatus(commonResponse.getStatus());
                                    }
                                } else {

                                    Data.deleteCurrentIciciUpiTransaction(apptype);
                                }
                                apiCurrentStatusListener.onGoToCheckout(commonResponse.getStatus());





                            } else {


                                activity.saveCheckoutData(true);
                                activity.clearAllCartAtOrderComplete(apptype);
                                if(apptype== AppConstant.ApplicationType.MENUS || apptype ==AppConstant.ApplicationType.DELIVERY_CUSTOMER){
                                    activity.getMenusCartSelectedLayout().checkForVisibility();
                                } else {
                                    activity.updateItemListFromSPDB(); // this is necessary
                                    activity.updateCartValuesGetTotalPrice();
                                    if(apptype == AppConstant.ApplicationType.MEALS){
                                        activity.refreshMealsAdapter();
                                    }
                                    activity.llCheckoutBarSetVisibilityDirect(View.GONE);
                                }
                                PlaceOrderResponse placeOrderResponse = Data.getCurrentIciciUpiTransaction(apptype);
                                //Show Order Placed Popup
                                if(placeOrderResponse!=null){
                                    OrderCompletDialog orderCompletDialog;
                                    if(placeOrderResponse.getReferralPopupContent()==null){
                                        orderCompletDialog = new FreshOrderCompleteDialog(activity, new FreshOrderCompleteDialog.Callback() {
                                            @Override
                                            public void onDismiss() {
                                            }
                                        });
                                    }  else{
                                        orderCompletDialog = new OrderCompleteReferralDialog(activity, new OrderCompleteReferralDialog.Callback() {
                                            @Override
                                            public void onDialogDismiss() {
                                            }

                                            @Override
                                            public void onConfirmed() {
                                            }
                                        });
                                    }
                                    try {
                                        FreshCheckoutMergedFragment.showOrderPlacedPopup(placeOrderResponse,apptype,activity,orderCompletDialog);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                Data.deleteCurrentIciciUpiTransaction(apptype);


                            }

                        } else {
                            retryDialog(DialogErrorType.CONNECTION_LOST, activity, isMenus, apiCurrentStatusListener,apptype);
                        }

                    }


                }

                @Override
                public void failure(RetrofitError error) {
                    retryDialog(DialogErrorType.CONNECTION_LOST, activity, isMenus, apiCurrentStatusListener,apptype);
                }

            };


            if (isMenus) {


                RestClient.getMenusApiService().checkPaymentStatus(params, iciciPaymentStatusCallback);
            } else {


                RestClient.getFreshApiService().checkPaymentStatus(params, iciciPaymentStatusCallback);
            }
        } else {
            retryDialog(DialogErrorType.NO_NET, activity, isMenus, apiCurrentStatusListener,apptype);
        }
    }

    public static void retryDialog(DialogErrorType dialogErrorType, final FreshActivity activity, final boolean isMenus, final ApiCurrentStatusListener apiCurrentStatusListener, final int apptype) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        checkIciciPaymentStatusApi(activity, isMenus, apiCurrentStatusListener,apptype);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                }, Data.getCurrentIciciUpiTransaction(apptype)==null);
    }
}
