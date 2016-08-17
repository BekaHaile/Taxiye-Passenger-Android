package com.sabkuchfresh.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.HomeUtil;
import com.sabkuchfresh.home.SupportActivity;
import com.sabkuchfresh.retrofit.RestClient;
import com.sabkuchfresh.retrofit.model.SettleUserDebt;
import com.sabkuchfresh.utils.Constants;
import com.sabkuchfresh.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class PromotionsFragment extends Fragment {

	private final String TAG = PromotionsFragment.class.getSimpleName();
	private LinearLayout linearLayoutRoot;

	private Button buttonApplyPromo;
	private RelativeLayout relativeLayoutPromocode;
	private EditText editTextPromoCode;


	private View rootView;
    private SupportActivity activity;


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
		super.onStop();
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_promotions, container, false);

        activity = (SupportActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
//				FlurryEventLogger.eventGA(Constants.REFERRAL, "Promotions", "Offers");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		buttonApplyPromo = (Button) rootView.findViewById(R.id.buttonApplyPromo);
		buttonApplyPromo.setTypeface(Fonts.mavenRegular(activity));
		relativeLayoutPromocode = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPromocode);
		editTextPromoCode = (EditText) rootView.findViewById(R.id.editTextPromoCode);
		editTextPromoCode.setTypeface(Fonts.mavenRegular(activity));

		buttonApplyPromo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				FlurryEventLogger.event(activity, ENTERED_PROMO_CODE);
				String promoCode = editTextPromoCode.getText().toString().trim();
				if (promoCode.length() > 0) {
					applyPromoCodeAPI(activity, promoCode);
//					FlurryEventLogger.event(activity, CLICKS_ON_APPLY);
				} else {
					editTextPromoCode.requestFocus();
					editTextPromoCode.setError("Code can't be empty");
				}
			}
		});

		editTextPromoCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				editTextPromoCode.setError(null);
			}
		});

		editTextPromoCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						buttonApplyPromo.performClick();
						return false;

					case EditorInfo.IME_ACTION_NEXT:
						return false;

					default:
						return false;
				}
			}
		});

		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		return rootView;
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}




	/**
	 * API call for applying promo code to server
	 */
	public void applyPromoCodeAPI(final Activity activity, final String promoCode) {
		try {
			if(!HomeUtil.checkIfUserDataNull(activity)) {
				if (AppStatus.getInstance(activity).isOnline(activity)) {
					DialogPopup.showLoadingDialog(activity, "Loading...");

					HashMap<String, String> params = new HashMap<>();
					params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
					params.put(Constants.KEY_CODE, promoCode);
                    params.put("app_name", "fatafat");

					RestClient.getApiServices().enterCode(params, new Callback<SettleUserDebt>() {
						@Override
						public void success(SettleUserDebt settleUserDebt, Response response) {
							String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.i(TAG, "enterCode response = " + responseStr);
							try {
								JSONObject jObj = new JSONObject(responseStr);
								int flag = jObj.getInt("flag");
								if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
                                    HomeUtil.logoutUser(activity);
								} else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
									String errorMessage = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", errorMessage);
								} else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
									String message = jObj.getString("message");
									DialogPopup.dialogBanner(activity, message);
                                    activity.finish();
//									FlurryEventLogger.event(PROMO_CODE_APPLIED);

//									new ApiPaytmCheckBalance(activity, new ApiPaytmCheckBalance.Callback() {
//										@Override
//										public void onSuccess() {
//
//										}
//
//										@Override
//										public void onFinish() {
//
//										}
//
//										@Override
//										public void onFailure() {
//
//										}
//
//										@Override
//										public void onRetry(View view) {
//
//										}
//
//										@Override
//										public void onNoRetry(View view) {
//
//										}
//									}).getBalance(Data.userData.paytmEnabled, false);

								} else {
									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}

								Data.userData.setJugnooBalance(jObj.optDouble(Constants.KEY_JUGNOO_BALANCE,
										Data.userData.getJugnooBalance()));
								Data.userData.setPaytmBalance(jObj.optDouble(Constants.KEY_PAYTM_BALANCE,
										Data.userData.getPaytmBalance()));
							} catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);

							}
							DialogPopup.dismissLoadingDialog();
						}

						@Override
						public void failure(RetrofitError error) {
							Log.e(TAG, "enterCode error="+error.toString());
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
					});
				} else {
					DialogPopup.dialogNoInternet(activity,
							Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
							new Utils.AlertCallBackWithButtonsInterface() {
								@Override
								public void positiveClick(View v) {
									applyPromoCodeAPI(activity, promoCode);
								}

								@Override
								public void neutralClick(View v) {

								}

								@Override
								public void negativeClick(View v) {

								}
							});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
