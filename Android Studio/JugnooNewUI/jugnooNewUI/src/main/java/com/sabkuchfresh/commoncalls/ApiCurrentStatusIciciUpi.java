package com.sabkuchfresh.commoncalls;

import android.view.View;

import com.sabkuchfresh.enums.IciciPaymentOrderStatus;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.PlaceOrderResponse;
import com.sabkuchfresh.retrofit.model.common.IciciPaymentRequestStatus;

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


    public static void checkIciciPaymentStatusApi(final FreshActivity activity, final boolean isMenus, final ApiCurrentStatusListener apiCurrentStatusListener) {
        if (MyApplication.getInstance().isOnline()) {
            HashMap<String, String> params = new HashMap<>();
            HomeUtil.addDefaultParams(params);
            params.put(Constants.KEY_ORDER_ID, String.valueOf(activity.getPlaceOrderResponse().getOrderId()));
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_CLIENT_ID, Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()));
            Callback<IciciPaymentRequestStatus> iciciPaymentStatusCallback = new Callback<IciciPaymentRequestStatus>() {
                @Override
                public void success(IciciPaymentRequestStatus commonResponse, Response response) {
                    if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, commonResponse.getFlag(), commonResponse.getError(), commonResponse.getMessage())) {

                        if (commonResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {

                            if (commonResponse.getStatus() == IciciPaymentOrderStatus.PENDING) {
                                PlaceOrderResponse placeOrderResponse = Data.getCurrentIciciUpiTransaction();
                                if(placeOrderResponse.getIcici()!=null) {
                                    placeOrderResponse.getIcici().setIciciPaymentOrderStatus(commonResponse.getStatus());
                                }
                                apiCurrentStatusListener.onGoToCheckout(commonResponse.getStatus());



                            } else {
                                Data.deleteCurrentIciciUpiTransaction();
                            }

                        }else{
                            retryDialog(DialogErrorType.CONNECTION_LOST,activity,isMenus,apiCurrentStatusListener);
                        }

                    }


                }

                @Override
                public void failure(RetrofitError error) {
                 retryDialog(DialogErrorType.CONNECTION_LOST,activity,isMenus,apiCurrentStatusListener);
                }

            };


            if (isMenus) {


                RestClient.getMenusApiService().checkPaymentStatus(params, iciciPaymentStatusCallback);
            } else {


                RestClient.getFreshApiService().checkPaymentStatus(params, iciciPaymentStatusCallback);
            }
        } else {
            retryDialog(DialogErrorType.NO_NET,activity,isMenus,apiCurrentStatusListener);
        }
    }

    public static void retryDialog(DialogErrorType dialogErrorType, final FreshActivity activity, final boolean isMenus, final ApiCurrentStatusListener apiCurrentStatusListener) {
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        checkIciciPaymentStatusApi(activity,isMenus,apiCurrentStatusListener);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }
}
