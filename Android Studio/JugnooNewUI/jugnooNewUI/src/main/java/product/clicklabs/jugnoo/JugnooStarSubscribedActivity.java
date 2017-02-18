package product.clicklabs.jugnoo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import product.clicklabs.jugnoo.adapters.StarMembershipAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.StarPurchaseType;
import product.clicklabs.jugnoo.datastructure.SubscriptionData;
import product.clicklabs.jugnoo.fragments.StarSubscriptionCheckoutFragment;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.FetchSubscriptionSavingsResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 27/12/16.
 */

public class JugnooStarSubscribedActivity extends BaseFragmentActivity implements View.OnClickListener {

    private RelativeLayout relative, rlAutoRenewal, rlFragment, rlPlan1, rlPlan2;;
    private TextView textViewTitle, tvAutoRenewal, tvUpgradingText;
    private ImageView imageViewBack, ivAutoRenewalSwitch, ivRadio1, ivRadio2;
    private TextView tvCurrentPlanValue, tvExpiresOnValue, tvSavingsMeterRetry, tvBenefits, tvExpiredTitle,
            tvActualAmount1, tvActualAmount2, tvAmount1, tvAmount2, tvPeriod1, tvPeriod2;;
    private LinearLayout llSavingsValue, llUpgradeContainer, llRenew;
    private ProgressWheel progressWheel;
    private NonScrollListView rvBenefits;
    private StarMembershipAdapter starMembershipAdapter;
    private Button btnUpgradeNow, bConfirm;
    private ArrayList<String> benefits = new ArrayList<>();
    private SubscriptionData.Subscription subscription;
    private String selectedSubId;
    private FetchSubscriptionSavingsResponse subscriptionSavingsResponse = null;
    private int purchaseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugnoo_star_subscribed);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(JugnooStarSubscribedActivity.this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        //textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);

        ((TextView) findViewById(R.id.tvCurrentPlan)).setTypeface(Fonts.mavenMedium(this));
        ((TextView) findViewById(R.id.tvExpiresOn)).setTypeface(Fonts.mavenMedium(this));

        tvCurrentPlanValue = (TextView) findViewById(R.id.tvCurrentPlanValue);
        tvCurrentPlanValue.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);
        tvExpiresOnValue = (TextView) findViewById(R.id.tvExpiresOnValue);
        tvExpiresOnValue.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);
        tvBenefits = (TextView) findViewById(R.id.tvBenefits); tvBenefits.setTypeface(Fonts.mavenMedium(this));
        rvBenefits = (NonScrollListView) findViewById(R.id.rvBenefits);
        /*rvBenefits.setLayoutManager(new LinearLayoutManager(this));
        rvBenefits.setItemAnimator(new DefaultItemAnimator());
        rvBenefits.setHasFixedSize(false);*/

        ((TextView) findViewById(R.id.tvSavingsMeter)).setTypeface(Fonts.mavenMedium(this));
        llSavingsValue = (LinearLayout) findViewById(R.id.llSavingsValue);
        progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
        tvSavingsMeterRetry = (TextView) findViewById(R.id.tvSavingsMeterRetry);
        tvSavingsMeterRetry.setTypeface(Fonts.mavenMedium(this));

        llRenew = (LinearLayout) findViewById(R.id.llRenew);
        tvExpiredTitle = (TextView) findViewById(R.id.tvExpiredTitle); tvExpiredTitle.setTypeface(Fonts.mavenRegular(this));
        rlPlan1 = (RelativeLayout) findViewById(R.id.rlPlan1); rlPlan1.setOnClickListener(this); rlPlan1.setVisibility(View.GONE);
        rlPlan2 = (RelativeLayout) findViewById(R.id.rlPlan2); rlPlan2.setOnClickListener(this); rlPlan2.setVisibility(View.GONE);
        ivRadio1 = (ImageView) findViewById(R.id.ivRadio1); ivRadio1.setOnClickListener(this);
        ivRadio2 = (ImageView) findViewById(R.id.ivRadio2); ivRadio2.setOnClickListener(this);
        tvActualAmount1 = (TextView) findViewById(R.id.tvActualAmount1); tvActualAmount1.setTypeface(Fonts.mavenRegular(this));
        tvActualAmount2 = (TextView) findViewById(R.id.tvActualAmount2); tvActualAmount2.setTypeface(Fonts.mavenRegular(this));
        tvAmount1 = (TextView) findViewById(R.id.tvAmount1); tvAmount1.setTypeface(Fonts.mavenRegular(this));
        tvAmount2 = (TextView) findViewById(R.id.tvAmount2); tvAmount2.setTypeface(Fonts.mavenRegular(this));
        tvPeriod1 = (TextView) findViewById(R.id.tvPeriod1); tvPeriod1.setTypeface(Fonts.mavenMedium(this));
        tvPeriod2 = (TextView) findViewById(R.id.tvPeriod2); tvPeriod2.setTypeface(Fonts.mavenMedium(this));
        bConfirm = (Button) findViewById(R.id.bConfirm); bConfirm.setTypeface(Fonts.mavenMedium(this)); bConfirm.setOnClickListener(this);

        rlFragment = (RelativeLayout) findViewById(R.id.rlFragment);
        llUpgradeContainer = (LinearLayout) findViewById(R.id.llUpgradeContainer);
        rlAutoRenewal = (RelativeLayout) findViewById(R.id.rlAutoRenewal);
        tvAutoRenewal = (TextView) findViewById(R.id.tvAutoRenewal); tvAutoRenewal.setTypeface(Fonts.mavenMedium(this));
        ivAutoRenewalSwitch = (ImageView) findViewById(R.id.ivAutoRenewalSwitch);
        tvUpgradingText = (TextView) findViewById(R.id.tvUpgradingText); tvUpgradingText.setTypeface(Fonts.mavenMedium(this));
        btnUpgradeNow = (Button) findViewById(R.id.btnUpgradeNow); btnUpgradeNow.setTypeface(Fonts.mavenMedium(this));
        apiFetchTotalSavings();

        tvSavingsMeterRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiFetchTotalSavings();
            }
        });

        ivAutoRenewalSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //ivAutoRenewalSwitch.setImageResource(R.drawable.jugnoo_sticky_off);
                    //ivAutoRenewalSwitch.setImageResource(R.drawable.jugnoo_sticky_on);
            }
        });

        btnUpgradeNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStarCheckoutFragment(JugnooStarSubscribedActivity.this, rlFragment, StarPurchaseType.UPGRADE.getOrdinal());
            }
        });

        try {
            tvCurrentPlanValue.setText(Data.userData.getSubscriptionData().getUserSubscriptions().get(0).getPlanString());
            tvExpiresOnValue.setText(DateOperations.convertDateOnlyViaFormat(DateOperations.utcToLocalWithTZFallback(Data.userData.getSubscriptionData().getUserSubscriptions().get(0).getValidTill())));

            String tempStr = Data.userData.getSubscriptionData().getSubTextAutos() + ";;;" + Data.userData.getSubscriptionData().getSubTextFresh() + ";;;" +
                    Data.userData.getSubscriptionData().getSubTextMeals()+";;;"+Data.userData.getSubscriptionData().getSubTextMenus()+";;;"+
                    Data.userData.getSubscriptionData().getSubTextGrocery();
            String[] strArray = tempStr.split(";;;");
            ArrayList<String> benefits = new ArrayList<>(Arrays.asList(strArray));

            ArrayList<String> benefitOffering = new ArrayList<>();
            String[] strArray1 = Data.userData.getSubscriptionData().getSubTextAutos().split(";;;");
            for (int i = 0; i < strArray1.length; i++) {
                benefitOffering.add("Autos");
            }
            String[] strArray2 = Data.userData.getSubscriptionData().getSubTextFresh().split(";;;");
            for (int i = 0; i < strArray2.length; i++) {
                benefitOffering.add("Fresh");
            }
            String[] strArray3 = Data.userData.getSubscriptionData().getSubTextMeals().split(";;;");
            for (int i = 0; i < strArray3.length; i++) {
                benefitOffering.add("Meals");
            }
            String[] strArray4 = Data.userData.getSubscriptionData().getSubTextMenus().split(";;;");
            for (int i = 0; i < strArray4.length; i++) {
                benefitOffering.add("Menus");
            }


            benefits.clear();
            benefitOffering.clear();

            if(!TextUtils.isEmpty(Data.userData.getSubscriptionData().getSubTextAutos())) {
                benefitOffering.add(Config.getAutosClientId());
                benefits.add(Data.userData.getSubscriptionData().getSubTextAutos());
            }
            if(Data.userData.getFreshEnabled() == 1
                    && !TextUtils.isEmpty(Data.userData.getSubscriptionData().getSubTextFresh())){
                benefitOffering.add(Config.getFreshClientId());
                benefits.add(Data.userData.getSubscriptionData().getSubTextFresh());
            }
            if(Data.userData.getMealsEnabled() == 1
                    && !TextUtils.isEmpty(Data.userData.getSubscriptionData().getSubTextMeals())){
                benefitOffering.add(Config.getMealsClientId());
                benefits.add(Data.userData.getSubscriptionData().getSubTextMeals());
            }
            if(Data.userData.getGroceryEnabled() == 1
                    && !TextUtils.isEmpty(Data.userData.getSubscriptionData().getSubTextGrocery())){
                benefitOffering.add(Config.getGroceryClientId());
                benefits.add(Data.userData.getSubscriptionData().getSubTextGrocery());
            }
            if(Data.userData.getMenusEnabled() == 1
                    && !TextUtils.isEmpty(Data.userData.getSubscriptionData().getSubTextMenus())){
                benefitOffering.add(Config.getMenusClientId());
                benefits.add(Data.userData.getSubscriptionData().getSubTextMenus());
            }




            starMembershipAdapter = new StarMembershipAdapter(JugnooStarSubscribedActivity.this, Data.userData.getSubscriptionData().getSubscriptionBenefits()
                    , new StarMembershipAdapter.Callback() {
                @Override
                public void onUnsubscribe() {
                    DialogPopup.alertPopupTwoButtonsWithListeners(JugnooStarSubscribedActivity.this, "", "Are you sure you want to cancel Subscription?"
                            , getResources().getString(R.string.yes), getResources().getString(R.string.no), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    apiCancelSubscription();
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }, false, false);
                }
            });
            rvBenefits.setAdapter(starMembershipAdapter);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((ScrollView)findViewById(R.id.scroll)).scrollTo(0, 0);
                }
            }, 200);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apiCancelSubscription() {
        try {
            if (MyApplication.getInstance().isOnline()) {
				DialogPopup.showLoadingDialog(JugnooStarSubscribedActivity.this, getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_SUB_ID, String.valueOf(Data.userData.getSubscriptionData().getUserSubscriptions().get(0).getId()));
				params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));

				new HomeUtil().putDefaultParams(params);
				RestClient.getApiService().cancelSubscription(params, new retrofit.Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						DialogPopup.dismissLoadingDialog();
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i("cancel Subscription response = ", "" + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
							String message = JSONParser.getServerMessage(jObj);
							if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {

								DialogPopup.alertPopupWithListener(JugnooStarSubscribedActivity.this, "", message, getResources().getString(R.string.ok), new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Data.userData.getSubscriptionData().setUserSubscriptions(new ArrayList<SubscriptionData.UserSubscription>());
										finish();
										overridePendingTransition(R.anim.left_in, R.anim.left_out);
									}
								}, false);
								//DialogPopup.alertPopup(JugnooStarSubscribedActivity.this, "", message);
							}
						} catch (Exception e) {
							e.printStackTrace();
							retryDialog(DialogErrorType.SERVER_ERROR);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("customerFetchUserAddress error=", "" + error.toString());
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
        DialogPopup.dialogNoInternet(JugnooStarSubscribedActivity.this,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        apiCancelSubscription();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {

                    }
                });
    }


    public void performBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                rlFragment.setVisibility(View.GONE);
            }
            super.onBackPressed();
        } else {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                performBackPressed();
                break;
            case R.id.ivRadio1:
                if(subscriptionSavingsResponse.getRenewalData().getRenewPlan() != null) {
                    subscription = subscriptionSavingsResponse.getRenewalData().getRenewPlan();
                }
                purchaseType = StarPurchaseType.RENEW.getOrdinal();
                selectedPlan(ivRadio1, subscription);
                break;
            case R.id.ivRadio2:
                if(subscriptionSavingsResponse.getRenewalData().getUpgradePlan() != null
                        && subscriptionSavingsResponse.getRenewalData().getUpgradePlan().get(0).getUpgradeArray() != null) {
                    subscription = subscriptionSavingsResponse.getRenewalData().getUpgradePlan().get(0).getUpgradeArray().get(0);
                }
                purchaseType = StarPurchaseType.UPGRADE.getOrdinal();
                selectedPlan(ivRadio2, subscription);
                break;
            case R.id.bConfirm:
                openStarCheckoutFragment(JugnooStarSubscribedActivity.this, rlFragment, purchaseType);
                break;
        }
    }

    private TextView getDigitTextView(String digit, boolean rupeeIcon){
        TextView textView = new TextView(this);
        float ratio = Math.min(ASSL.Xscale(), ASSL.Yscale());
        textView.setPadding((int) (ratio * 11f), (int) (ratio * 6f), (int) (ratio * 11f), (int) (ratio * 6f));
        textView.setTextColor(getResources().getColor(R.color.text_color));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ratio * 82f);
        textView.setTypeface(Fonts.avenirNext(this));
        if(!rupeeIcon){
            textView.setBackgroundResource(R.drawable.background_white_rounded_bordered);
        }
        textView.setText(digit);
        return textView;
    }

    private void setTotalSavingsValueText(LinearLayout llSavingsValue, int savingValue){
        llSavingsValue.removeAllViews();
        char[] digits = String.valueOf(savingValue).toCharArray();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int)(ASSL.Xscale()*3f), 0, (int)(ASSL.Xscale()*3f), 0);
        llSavingsValue.addView(getDigitTextView(getString(R.string.rupee), true), params);
        for(char digit : digits){
            llSavingsValue.addView(getDigitTextView(String.valueOf(digit), false), params);
        }
    }


    private void apiFetchTotalSavings() {
        try {
            if (MyApplication.getInstance().isOnline()) {
                DialogPopup.showLoadingDialog(JugnooStarSubscribedActivity.this, "Loading...");
                llSavingsValue.removeAllViews();
                llSavingsValue.addView(progressWheel);
                progressWheel.spin();
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

                new HomeUtil().putDefaultParams(params);
                RestClient.getApiService().getSavingsMeterReading(params, new retrofit.Callback<FetchSubscriptionSavingsResponse>() {
                    @Override
                    public void success(FetchSubscriptionSavingsResponse savingsResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i("fetchSubscriptionSavings response = ", "" + responseStr);
                        try {
                            DialogPopup.dismissLoadingDialog();
                            if (savingsResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                llSavingsValue.removeAllViews();
                                subscriptionSavingsResponse = savingsResponse;
                                setTotalSavingsValueText(llSavingsValue, savingsResponse.getTotalSavings());
                                setUpgradeView(savingsResponse);
                                setRenewView(savingsResponse);
                            } else {
                                DialogPopup.alertPopup(JugnooStarSubscribedActivity.this, "", savingsResponse.getMessage());
                                llSavingsValue.removeAllViews();
                                llSavingsValue.addView(tvSavingsMeterRetry);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            llSavingsValue.removeAllViews();
                            llSavingsValue.addView(tvSavingsMeterRetry);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        Log.e("fetchSubscriptionSavings error=", "" + error.toString());
                        llSavingsValue.removeAllViews();
                        llSavingsValue.addView(tvSavingsMeterRetry);
                    }
                });

            } else {
                llSavingsValue.removeAllViews();
                llSavingsValue.addView(tvSavingsMeterRetry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRenewView(FetchSubscriptionSavingsResponse savingsResponse) {
        llRenew.setVisibility(View.GONE);
        if(savingsResponse.getRenewalData() != null){
            llUpgradeContainer.setVisibility(View.GONE);
            llRenew.setVisibility(View.VISIBLE);
            if(savingsResponse.getRenewalData().getWarning() != null && savingsResponse.getRenewalData().getWarning().getText() != null) {
                tvExpiredTitle.setVisibility(View.VISIBLE);
                tvExpiredTitle.setText(savingsResponse.getRenewalData().getWarning().getText());
            }

            if(savingsResponse.getRenewalData().getUpgradePlan() == null
                    && savingsResponse.getRenewalData().getRenewPlan() != null){
                rlPlan1.setVisibility(View.GONE);
                rlPlan2.setVisibility(View.GONE);
                subscription = savingsResponse.getRenewalData().getRenewPlan();
                purchaseType = StarPurchaseType.RENEW.getOrdinal();
                selectedPlan(ivRadio1, savingsResponse.getRenewalData().getRenewPlan());
                bConfirm.setText(getResources().getString(R.string.renew));
            } else if(savingsResponse.getRenewalData().getRenewPlan() != null){
                rlPlan1.setVisibility(View.VISIBLE);
                tvPeriod1.setText(savingsResponse.getRenewalData().getRenewPlan().getPlanString());
                tvAmount1.setText(String.valueOf(savingsResponse.getRenewalData().getRenewPlan().getAmount()));
                if(savingsResponse.getRenewalData().getRenewPlan().getInitialAmount() != null
                        && savingsResponse.getRenewalData().getRenewPlan().getInitialAmount() > 0){
                    tvActualAmount1.setVisibility(View.VISIBLE);
                    tvActualAmount1.setText(String.valueOf(savingsResponse.getRenewalData().getRenewPlan().getInitialAmount()));
                }
                selectedPlan(ivRadio1, savingsResponse.getRenewalData().getRenewPlan());
                subscription = savingsResponse.getRenewalData().getRenewPlan();
                purchaseType = StarPurchaseType.RENEW.getOrdinal();
                bConfirm.setText(getResources().getString(R.string.confirm));
            }

            if(savingsResponse.getRenewalData().getUpgradePlan() != null && savingsResponse.getRenewalData().getUpgradePlan().size() > 0){
                rlPlan2.setVisibility(View.VISIBLE);
                tvPeriod2.setText(savingsResponse.getRenewalData().getUpgradePlan().get(0).getUpgradeArray().get(0).getPlanString());
                tvAmount2.setText(String.valueOf(savingsResponse.getRenewalData().getUpgradePlan().get(0).getUpgradeArray().get(0).getAmount()));
                if(savingsResponse.getRenewalData().getUpgradePlan().get(0).getUpgradeArray().get(0).getInitialAmount() != null
                        && savingsResponse.getRenewalData().getUpgradePlan().get(0).getUpgradeArray().get(0).getInitialAmount() > 0){
                    tvActualAmount2.setVisibility(View.VISIBLE);
                    tvActualAmount2.setText(String.valueOf(savingsResponse.getRenewalData().getUpgradePlan().get(0).getUpgradeArray().get(0).getInitialAmount()));
                }
                if(rlPlan1.getVisibility() == View.VISIBLE){
                    bConfirm.setText(getResources().getString(R.string.confirm));
                } else{
                    bConfirm.setText(getResources().getString(R.string.renew));
                    subscription = savingsResponse.getRenewalData().getUpgradePlan().get(0).getUpgradeArray().get(0);
                    purchaseType = StarPurchaseType.UPGRADE.getOrdinal();
                    selectedPlan(ivRadio1, savingsResponse.getRenewalData().getUpgradePlan().get(0).getUpgradeArray().get(0));
                }
            }


        }
    }

    private void setUpgradeView(FetchSubscriptionSavingsResponse savingsResponse) {
        try {
            llUpgradeContainer.setVisibility(View.GONE);
            if(savingsResponse.getUpgradeData() != null && savingsResponse.getUpgradeData().size() > 0){
                llUpgradeContainer.setVisibility(View.VISIBLE);
                tvUpgradingText.setText(savingsResponse.getUpgradeData().get(0).getUpgradingText());
                subscription = savingsResponse.getUpgradeData().get(0).getUpgradeArray().get(0);
                selectedSubId = new Gson().toJson(subscription);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectedPlan(ImageView ivRadio, SubscriptionData.Subscription selectedSubscription){
        ivRadio1.setImageResource(R.drawable.ic_radio_button_normal);
        ivRadio2.setImageResource(R.drawable.ic_radio_button_normal);

        ivRadio.setImageResource(R.drawable.ic_order_status_green);
        subscription = selectedSubscription;
        selectedSubId = new Gson().toJson(subscription);
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }

    public void openStarCheckoutFragment(FragmentActivity activity, View container, int purchaseType) {
        rlFragment.setVisibility(View.VISIBLE);
        activity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                .add(container.getId(), StarSubscriptionCheckoutFragment.newInstance(selectedSubId, purchaseType),
                        StarSubscriptionCheckoutFragment.class.getName())
                .addToBackStack(StarSubscriptionCheckoutFragment.class.getName())
                .commitAllowingStateLoss();
    }
}
