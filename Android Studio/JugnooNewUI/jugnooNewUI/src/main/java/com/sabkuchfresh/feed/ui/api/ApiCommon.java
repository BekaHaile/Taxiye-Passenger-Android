package com.sabkuchfresh.feed.ui.api;

import android.app.Activity;
import android.view.View;

import com.sabkuchfresh.feed.models.FeedCommonResponse;

import java.util.HashMap;

import androidx.annotation.NonNull;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.RestClient2;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedString;
import retrofit2.Call;

/**
 * Created by Parminder Singh on 3/27/17.
 */

/**
 * @param <T> Expected Response Type Class
 */
public class ApiCommon<T extends FeedCommonResponse> {
    private Callback callback;
    private retrofit2.Callback callback2;
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
    private TypedFile typedFile;
    private int successFlag = ApiResponseFlags.ACTION_COMPLETE.getOrdinal();

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

    public ApiCommon<T> successFlag(int successFlag) {
        this.successFlag = successFlag;
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

    public void execute(TypedFile typedFile,HashMap<String,String> params, @NonNull ApiName apiName, APICommonCallback<T> apiCommonCallback) {
        this.apiCommonCallback = apiCommonCallback;
        if (multipartTypedOutput == null) {
            multipartTypedOutput = new MultipartTypedOutput();
        }
        this.params = params;
        this.apiName = apiName;
        this.typedFile = typedFile;
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
					handleSuccess(feedCommonResponse);

					if(apiCommonCallback.isRawResponseNeeded()) {
						try {
							String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
							apiCommonCallback.rawResponse(responseStr);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

                @Override
                public void failure(RetrofitError error) {
					handleFailure(error);
				}
            };
        }
		if (callback2 == null) {
			callback2 = new retrofit2.Callback<T>(){

				@Override
				public void onResponse(Call<T> call, retrofit2.Response<T> response) {
					handleSuccess(response.body());


					if(apiCommonCallback.isRawResponseNeeded()) {
						try {
							okhttp3.Response responseRaw = response.raw();
							String responseStr = new String(responseRaw.body().bytes());
							apiCommonCallback.rawResponse(responseStr);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				@Override
				public void onFailure(Call<T> call, Throwable t) {
					handleFailure(new Exception(t));
				}
			};
		}

        if (isMultiPartRequest) {
            new HomeUtil().putDefaultParamsMultipart(multipartTypedOutput);
        } else {
            HomeUtil.addDefaultParams(params);
        }


        if (putAccessToken && Data.userData != null) {
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
            case UPLOAD_VERICATION_DOCUMENTS:
                RestClient.getApiService().uploadVerificationDocuments(multipartTypedOutput,callback);
                break;
            case FETCH_DOCUMENTS:
                RestClient.getApiService().fetchDocuments(params,callback);
                break;
            case DELETE_DOCUMENT:
                RestClient.getApiService().deleteDocument(params,callback);
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
            case NEARBY_AGENTS:
                RestClient.getFatafatApiService().nearbyAgents(params,callback);
                break;
            case NEARBY_AGENTS_MENUS:
                RestClient.getMenusApiService().nearbyAgents(params,callback);
                break;

            case SCRATCH_CARD:
                RestClient.getApiService().scratchCard(params, callback);
                break;
			case FARE_ESTIMATE_WITH_NEW_DROP:
                RestClient.getApiService().fareEstimateWithNewDrop(params, callback);
                break;
			case REFERRAL_INFO:
                RestClient.getApiService().fetchCustomerReferralInfo(params, callback);
                break;
			case FILTER_ACTIVE_USERS:
                RestClient2.getApiService().filterActiveUsers(params).enqueue(callback2);
                break;
			case REINVITE_USERS:
                RestClient2.getApiService().reinviteUsers(params).enqueue(callback2);
                break;
			case ADD_DROP_LOCATION:
                RestClient.getApiService().addDropLocation(params, callback);
                break;
			case UPDATE_PAYMENT_TO_UPI:
                RestClient2.getApiService().updatePaymentModeToUpi(params).enqueue(callback2);
                break;
			case RATE_THE_DRIVER:
                RestClient.getApiService().rateTheDriver(params, callback);
                break;
			case CANCEL_RIDE_BY_CUSTOMER:
                RestClient.getApiService().cancelRideByCustomer(params, callback);
                break;
			case SEND_OTP_VIA_CALL:
                RestClient.getApiService().sendOtpViaCall(params, callback);
                break;
			case CLAIM_GIFT:
                RestClient.getApiService().claimGift(params, callback);
                break;
            case REQUEST_DELETE_ACCOUNT:
                RestClient.getApiService().requestDeleteAccount(params, callback);
                break;
            case CANCEL_DELETE_ACCOUNT_REQUEST:
                RestClient.getApiService().cancelDeleteAccountRequest(params, callback);
                break;
            case SETTLE_NEGATIVE_WALLET_BALANCE:
                RestClient.getApiService().settleNegativeWalletBalance(params, callback);
                break;
            default:
                throw new IllegalArgumentException("API Type not declared");

        }


    }

	private void handleFailure(Exception error) {
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

	private void handleSuccess(T feedCommonResponse) {
		setInProgress(false);

		if (showLoader) {
			DialogPopup.dismissLoadingDialog();
		}
		if (isCancelled() || activity.isFinishing())
			return;

		try {
			if (feedCommonResponse.getFlag() == successFlag) {
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
