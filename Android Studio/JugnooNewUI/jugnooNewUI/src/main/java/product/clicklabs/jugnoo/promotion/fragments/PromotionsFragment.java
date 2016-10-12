package product.clicklabs.jugnoo.promotion.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.PromCouponResponse;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.promotion.adapters.PromotionsAdapter;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class PromotionsFragment extends Fragment implements FlurryEventNames, Constants {

	private final String TAG = PromotionsFragment.class.getSimpleName();
	private LinearLayout linearLayoutRoot;

	private Button buttonAddPromoCode, buttonApplyPromo;
	private RelativeLayout relativeLayoutPromocode;
	private EditText editTextPromoCode;
	private ImageView imageViewClose;

	private RelativeLayout relativeLayoutListTitle;
	private LinearLayout linearLayoutNoOffers;
	private RecyclerView recyclerViewOffers;
	private PromotionsAdapter promotionsAdapter;

	private View rootView;
    private ShareActivity activity;

	private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(PromotionsFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_promotions, container, false);

        activity = (ShareActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
				FlurryEventLogger.eventGA(Constants.REFERRAL, "Promotions", "Offers");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		buttonAddPromoCode = (Button) rootView.findViewById(R.id.buttonAddPromoCode);
		buttonAddPromoCode.setTypeface(Fonts.mavenRegular(activity));
		buttonApplyPromo = (Button) rootView.findViewById(R.id.buttonApplyPromo);
		buttonApplyPromo.setTypeface(Fonts.mavenRegular(activity));
		relativeLayoutPromocode = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPromocode);
		editTextPromoCode = (EditText) rootView.findViewById(R.id.editTextPromoCode);
		editTextPromoCode.setTypeface(Fonts.mavenRegular(activity));
		imageViewClose = (ImageView) rootView.findViewById(R.id.imageViewClose);

		relativeLayoutListTitle = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutListTitle);
		((TextView) rootView.findViewById(R.id.textViewOffersAvailable)).setTypeface(Fonts.mavenRegular(activity));
		linearLayoutNoOffers = (LinearLayout) rootView.findViewById(R.id.linearLayoutNoOffers);
		((TextView) rootView.findViewById(R.id.textViewNoOffers)).setTypeface(Fonts.mavenRegular(activity));

		recyclerViewOffers = (RecyclerView) rootView.findViewById(R.id.recyclerViewOffers);
		recyclerViewOffers.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewOffers.setItemAnimator(new DefaultItemAnimator());
		recyclerViewOffers.setHasFixedSize(false);

		promotionsAdapter = new PromotionsAdapter(activity, promoCoupons);
		recyclerViewOffers.setAdapter(promotionsAdapter);


		buttonAddPromoCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale_in);
				buttonAddPromoCode.setVisibility(View.GONE);
				relativeLayoutPromocode.setVisibility(View.VISIBLE);
				relativeLayoutPromocode.startAnimation(animation);
			}
		});

		buttonApplyPromo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(activity, ENTERED_PROMO_CODE);
				String promoCode = editTextPromoCode.getText().toString().trim();
				if (promoCode.length() > 0) {
					applyPromoCodeAPI(activity, promoCode);
					FlurryEventLogger.event(activity, CLICKS_ON_APPLY);
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

		imageViewClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale_out);
				relativeLayoutPromocode.clearAnimation();
				animation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						buttonAddPromoCode.setVisibility(View.VISIBLE);
						relativeLayoutPromocode.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});
				relativeLayoutPromocode.startAnimation(animation);
			}
		});

		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		getCouponsAndPromotions(activity);

		return rootView;
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}

	public void updateListData() {
		if (promoCoupons.size() == 0) {
			relativeLayoutListTitle.setVisibility(View.GONE);
			recyclerViewOffers.setVisibility(View.GONE);
			linearLayoutNoOffers.setVisibility(View.VISIBLE);
		} else{
			relativeLayoutListTitle.setVisibility(View.VISIBLE);
			recyclerViewOffers.setVisibility(View.VISIBLE);
			linearLayoutNoOffers.setVisibility(View.GONE);
			promotionsAdapter.notifyDataSetChanged();
		}
	}

	public void clearErrorForEditText(){
		try {
			editTextPromoCode.setError(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getCouponsAndPromotions(final Activity activity) {
		try {
			if(!HomeActivity.checkIfUserDataNull(activity)) {
				if (AppStatus.getInstance(activity).isOnline(activity)) {
					DialogPopup.showLoadingDialog(activity, "Loading...");

					HashMap<String, String> params = new HashMap<>();
					params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
					params.put(Constants.KEY_LATITUDE, "" + Data.latitude);
					params.put(Constants.KEY_LONGITUDE, "" + Data.longitude);

					RestClient.getApiServices().getCouponsAndPromotions(params, new Callback<PromCouponResponse>() {
						@Override
						public void success(PromCouponResponse promCouponResponse, Response response) {
							String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.i(TAG, "getCouponsAndPromotions response = " + responseStr);
							try {
								JSONObject jObj = new JSONObject(responseStr);

								if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
									int flag = jObj.getInt("flag");
									String message = JSONParser.getServerMessage(jObj);
									if (ApiResponseFlags.COUPONS.getOrdinal() == flag) {
										promoCoupons.clear();
										if(promCouponResponse.getCommonPromotions() != null)
											promoCoupons.addAll(promCouponResponse.getCommonPromotions());
										if(promCouponResponse.getCommonCoupons() != null)
											promoCoupons.addAll(promCouponResponse.getCommonCoupons());

										if(promCouponResponse.getAutosPromotions() != null)
											promoCoupons.addAll(promCouponResponse.getAutosPromotions());
										if(promCouponResponse.getAutosCoupons() != null)
											promoCoupons.addAll(promCouponResponse.getAutosCoupons());

										if(promCouponResponse.getFreshPromotions() != null)
											promoCoupons.addAll(promCouponResponse.getFreshPromotions());
										if(promCouponResponse.getFreshCoupons() != null)
											promoCoupons.addAll(promCouponResponse.getFreshCoupons());

										if(promCouponResponse.getMealsPromotions() != null)
											promoCoupons.addAll(promCouponResponse.getMealsPromotions());
										if(promCouponResponse.getMealsCoupons() != null)
											promoCoupons.addAll(promCouponResponse.getMealsCoupons());

										if(promCouponResponse.getDeliveryPromotions() != null)
											promoCoupons.addAll(promCouponResponse.getDeliveryPromotions());
										if(promCouponResponse.getDeliveryCoupons() != null)
											promoCoupons.addAll(promCouponResponse.getDeliveryCoupons());

										if(promCouponResponse.getGroceryPromotions() != null)
											promoCoupons.addAll(promCouponResponse.getGroceryPromotions());
										if(promCouponResponse.getGroceryCoupons() != null)
											promoCoupons.addAll(promCouponResponse.getGroceryCoupons());

										updateListData();
									} else {
										updateListData();
										retryDialog(DialogErrorType.OTHER, message);
									}
								} else {
									updateListData();
								}

							} catch (Exception exception) {
								exception.printStackTrace();
								updateListData();
								retryDialog(DialogErrorType.SERVER_ERROR, "");
							}
							DialogPopup.dismissLoadingDialog();
						}

						@Override
						public void failure(RetrofitError error) {
							Log.e(TAG, "getCouponsAndPromotions error="+error.toString());
							DialogPopup.dismissLoadingDialog();
							updateListData();
							retryDialog(DialogErrorType.CONNECTION_LOST, "");
						}
					});
				} else {
					retryDialog(DialogErrorType.NO_NET, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retryDialog(DialogErrorType dialogErrorType, String message){
		if(dialogErrorType == DialogErrorType.OTHER){
			DialogPopup.alertPopup(activity, "", message);
		} else{
			DialogPopup.dialogNoInternet(activity, dialogErrorType, new Utils.AlertCallBackWithButtonsInterface() {
				@Override
				public void positiveClick(View view) {
					getCouponsAndPromotions(activity);
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


	/**
	 * API call for applying promo code to server
	 */
	public void applyPromoCodeAPI(final Activity activity, final String promoCode) {
		try {
			if(!HomeActivity.checkIfUserDataNull(activity)) {
				if (AppStatus.getInstance(activity).isOnline(activity)) {
					DialogPopup.showLoadingDialog(activity, "Loading...");

					HashMap<String, String> params = new HashMap<>();
					params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
					params.put(Constants.KEY_CODE, promoCode);

					RestClient.getApiServices().enterCode(params, new Callback<SettleUserDebt>() {
						@Override
						public void success(SettleUserDebt settleUserDebt, Response response) {
							String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.i(TAG, "enterCode response = " + responseStr);
							try {
								JSONObject jObj = new JSONObject(responseStr);
								int flag = jObj.getInt("flag");
								if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
									HomeActivity.logoutUser(activity);
								} else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
									String errorMessage = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", errorMessage);
								} else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
									String message = jObj.getString("message");
									DialogPopup.dialogBanner(activity, message);
									getCouponsAndPromotions(activity);
									FlurryEventLogger.event(PROMO_CODE_APPLIED);

									new ApiFetchWalletBalance(activity, new ApiFetchWalletBalance.Callback() {
										@Override
										public void onSuccess() {

										}

										@Override
										public void onFinish() {

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

									}).getBalance(false);
								} else {
									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
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
