package com.sabkuchfresh.feed.ui.api;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.sabkuchfresh.feed.models.FeedCommonResponse;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedString;

/**
 * Created by Parminder Singh on 3/27/17.
 */

/**
 * @param <T> Expected Response Type Class
 */
public class ApiCommon<T extends FeedCommonResponse> {
    private Callback callback;
    private Activity activity;
    private boolean showLoader = true;
    private boolean putDefaultParams = true;
    private APICommonCallback<T> apiCommonCallback;
    private HashMap<String, String> params;
    private MultipartTypedOutput multipartTypedOutput;
    private ApiName apiName;
    private boolean putAccessToken = true;
    private boolean isCancelled;
    private boolean isErrorCancellable = true;

    public boolean isInProgress() {
        return isInProgress;
    }

    public void setInProgress(boolean inProgress) {
        isInProgress = inProgress;
    }

    private boolean isInProgress;


    /**
     * Generates a new constructor with type parameter and context
     *
     * @param activity Context Of The Calling Activity
     */
    public ApiCommon(Activity activity) {
        this.activity = activity;
    }

    public ApiCommon<T> showLoader(boolean showLoader) {
        this.showLoader = showLoader;
        return this;
    }

    public ApiCommon<T> isErrorCancellable(boolean isErrorCancellable) {
        this.isErrorCancellable = isErrorCancellable;
        return this;
    }

    public ApiCommon<T> putDefaultParams(boolean putDefaultParams) {
        this.putDefaultParams = putDefaultParams;
        return this;
    }


    public ApiCommon<T> putAccessToken(boolean putAccessToken) {
        this.putAccessToken = putAccessToken;
        return this;
    }

    public void execute(HashMap<String, String> params, @NonNull ApiName apiName, APICommonCallback<T> apiCommonCallback) {
        this.apiCommonCallback = apiCommonCallback;
        this.params = params;
        this.apiName = apiName;
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        hitAPI(false);
    }

    public void execute(MultipartTypedOutput params, @NonNull ApiName apiName, APICommonCallback<T> apiCommonCallback) {
        this.apiCommonCallback = apiCommonCallback;
        if (multipartTypedOutput == null) {
            multipartTypedOutput = new MultipartTypedOutput();
        }
        this.multipartTypedOutput = params;
        this.apiName = apiName;
        hitAPI(true);
    }


    private void hitAPI(boolean isMultiPartRequest) {


        if (!MyApplication.getInstance().isOnline()) {
            if (!apiCommonCallback.onNotConnected()) {
                retryDialog(DialogErrorType.NO_NET);
            }
            apiCommonCallback.onFinish();

            return;

        }


        if (callback == null) {
            callback = new Callback<T>() {
                @Override
                public void success(T feedCommonResponse, Response response) {
                    setInProgress(false);

                    if (showLoader) {
                        DialogPopup.dismissLoadingDialog();
                    }
                    if (isCancelled() || activity.isFinishing())
                        return;

                    try {
                        if (feedCommonResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                            apiCommonCallback.onSuccess(feedCommonResponse, feedCommonResponse.getMessage(), feedCommonResponse.getFlag());
                            apiCommonCallback.onFinish();


                        } else {
                            if (!apiCommonCallback.onError(feedCommonResponse, feedCommonResponse.getMessage() == null ? feedCommonResponse.getError() : feedCommonResponse.getMessage(),
                                    feedCommonResponse.getFlag())) {
                                DialogPopup.alertPopup(activity, "", feedCommonResponse.getMessage() == null ? feedCommonResponse.getError() : feedCommonResponse.getMessage());
                            }
                            apiCommonCallback.onFinish();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (!apiCommonCallback.onException(e)) {
                            retryDialog(DialogErrorType.CONNECTION_LOST);
                        }
                        apiCommonCallback.onFinish();

                    }


                }

                @Override
                public void failure(RetrofitError error) {
                    setInProgress(false);
                    if (showLoader) {
                        DialogPopup.dismissLoadingDialog();
                    }
                    if (isCancelled() || activity.isFinishing())
                        return;
                    error.printStackTrace();
                    if (!apiCommonCallback.onFailure(error)) {

                        retryDialog(DialogErrorType.CONNECTION_LOST);
                    }
                    apiCommonCallback.onFinish();



                }
            };
        }

        if (isMultiPartRequest) {
            new HomeUtil().putDefaultParamsMultipart(multipartTypedOutput);
        } else {
            HomeUtil.addDefaultParams(params);
        }


        if (putAccessToken) {
            if (isMultiPartRequest) {
                multipartTypedOutput.addPart(Constants.KEY_ACCESS_TOKEN, new TypedString(Data.userData.accessToken));
            } else {
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            }
        }


        if (showLoader) {
            DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
        }
        setInProgress(true);
        switch (apiName) {
            case GENERATE_FEED_API:
                RestClient.getFeedApiService().generateFeed(params, callback);
                break;
            case REGISTER_FOR_FEED:
                RestClient.getFeedApiService().registerForFeed(params, callback);
                break;
            case COUNT_NOTIFICATION:
                RestClient.getFeedApiService().countNotification(params, callback);
                break;
            case CITY_INFO_API:
                RestClient.getFeedApiService().getCityInfo(params, callback);
                break;
            case SET_HANDLE_API:
                RestClient.getFeedApiService().setUserHandle(params, callback);
                break;
            case FEED_UPDATE_NOTIFICATION:
                RestClient.getFeedApiService().updateNotification(params, callback);
                break;
            case GET_HANLDE_SUGGESTIONS:
                RestClient.getFeedApiService().getHandleSuggestions(params, callback);
                break;
            case USER_CLICK_EVENTS:
                RestClient.getMenusApiService().userClickEvents(params, callback);
                break;
            case USER_CLICK_EVENTS_CATEGORY:
                RestClient.getMenusApiService().userCategoryLogs(params, callback);
                break;
            case ANYWHERE_PLACE_ORDER:
                RestClient.getFatafatApiService().anywherePlaceOrder(multipartTypedOutput, callback);
                break;
            case ANYWHERE_DYNAMIC_DELIVERY:
                RestClient.getFatafatApiService().dynamicDeliveryCharges(params, callback);
                break;
            case INITIATE_RIDE_END_PAYMENT:
                RestClient.getApiService().initiateRideEndPayment(params, callback);
                break;
            case FEED_FETCH_ORDER_STATUS:
                RestClient.getFatafatApiService().fetchOrderStatus(params, callback);
                break;
            case FEED_PAY_FOR_ORDER:
                RestClient.getFatafatApiService().payForOrder(params, callback);
                break;
            case OFFERING_VISBILITY_API:
                RestClient.getApiService().fetchOfferingsVisibility(params, callback);
                break;
            case SUGGEST_A_STORE:
                RestClient.getMenusApiService().suggestStore(multipartTypedOutput, callback);
                break;
            case CREATE_CHAT:
                RestClient.getFatafatApiService().createChat(params, callback);
                break;
            case FETCH_CONTACTS:
                RestClient.getFatafatApiService().fetchContacts(params, callback);
                break;
            case SELECT_BID:
                RestClient.getApiService().selectTheBid(params, callback);
                break;
            case EDIT_TIP:
                RestClient.getApiService().editTip(params, callback);
                break;
            case SCHEDULE_RIDE:
                RestClient.getApiService().scheduleRide(params, callback);
                break;
            case ADD_CARD_API:
                RestClient.getApiService().addCardToCustomer(params, callback);
                break;
            case FETCH_CHAT:
                RestClient.getChatApiService().fetchChat(params, callback);
                break;
            case POST_CHAT:
                RestClient.getChatApiService().postChat(params, callback);
                break;
            case UPDATE_USER_PROFILE:
                RestClient.getApiService().updateUserProfile(params, callback);
                break;
            case UPDATE_USER_PROFILE_MULTIPART:
                RestClient.getApiService().updateUserProfile(multipartTypedOutput, callback);
                break;
            case FETCH_ACTIVE_LOCALES:
                RestClient.getApiService().fetchActiveLocales(params, callback);
                break;
            case ADD_CARD_PAYSTACK:
                RestClient.getApiService().addCardPayStack(params, callback);
                break;
            case DELETE_CARD_PAYSTACK:
                RestClient.getApiService().deleteCardPayStack(params, callback);
                break;
            case FARE_DETAILS:
                RestClient.getApiService().fareDetails(params, callback);
                break;
            case GET_UPCOMING_RIDES:
                RestClient.getApiService().upcomingRides(params, callback);
                break;
            case DELETE_SCHEDULE_RIDE:
                RestClient.getApiService().deleteScheduleRide(params, callback);
                break;
            case FETCH_CORPORATES:
                RestClient.getApiService().fetchUserCorporates(params, callback);
                break;
            case SEND_EMAIL_INVOICE:
                RestClient.getApiService().sendEmailInvoice(params, callback);
                break;
            case RENTALS_UPDATE_LOCK_STATUS:
                RestClient.getApiService().updateLockStatus(params, callback);
                break;
            case RENTALS_GET_LOCK_STATUS:
                RestClient.getApiService().getLockStatus(params, callback);
                break;
            case RENTALS_INSERT_DAMAGE_REPORT:
                RestClient.getApiService().insertDamageReport(multipartTypedOutput, callback);
                break;
			case CANCEL_BID:
                RestClient.getApiService().cancelBid(params, callback);
                break;
            default:
                throw new IllegalArgumentException("API Type not declared");

        }


    }

    private void retryDialog(DialogErrorType dialogErrorType) {
        DialogPopup.dialogNoInternet(activity, dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        if (multipartTypedOutput != null) {
                            hitAPI(true);
                        } else {
                            hitAPI(false);
                        }
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                        apiCommonCallback.onNegativeClick();
                    }
                }, isErrorCancellable);

    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

}
