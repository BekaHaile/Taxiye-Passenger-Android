package product.clicklabs.jugnoo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
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
import java.util.HashMap;
import java.util.regex.Pattern;

import product.clicklabs.jugnoo.adapters.StarMembershipAdapter;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.StarPurchaseType;
import product.clicklabs.jugnoo.datastructure.SubscriptionData;
import product.clicklabs.jugnoo.fragments.StarSubscriptionCheckoutFragment;
import product.clicklabs.jugnoo.fragments.ViewJugnooStarBenefitsFragment;
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
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 27/12/16.
 */

public class JugnooStarSubscribedActivity extends RazorpayBaseActivity implements View.OnClickListener {

    private RelativeLayout relative, rlAutoRenewal, rlFragment, rlPlan1, rlPlan2, rlExpire, rlWarning;
    private TextView textViewTitle, tvAutoRenewal, tvUpgradingText,tvViewBenefits;
    private ImageView imageViewBack, ivAutoRenewalSwitch, ivRadio1, ivRadio2, ivStarInfo;
    private TextView tvCurrentPlanValue, tvExpiresOnValue, tvSavingsMeterRetry, tvBenefits, tvExpiredTitle,
            tvActualAmount1, tvActualAmount2, tvAmount1, tvAmount2, tvPeriod1, tvPeriod2, tvCurrentPlan;
    private LinearLayout llSavingsValue, llUpgradeContainer, llRenew;
    private NonScrollListView rvBenefits;
    private StarMembershipAdapter starMembershipAdapter;
    private Button btnUpgradeNow, bConfirm;
    private ArrayList<String> benefits = new ArrayList<>();
    private SubscriptionData.Subscription subscription;
    private String selectedSubId;
    private FetchSubscriptionSavingsResponse subscriptionSavingsResponse = null;
    private int purchaseType;
    private String crossTextFormatter = "{{{cross_text}}}";
    private static StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();
    private CardView cvContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugnoo_star_subscribed);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(JugnooStarSubscribedActivity.this, relative, 1134, 720, false);

        cvContainer = (CardView) findViewById(R.id.cvContainer);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_JUGNOO_STAR);
        //textViewTitle.getPaint().setShader(FeedUtils.textColorGradient(this, textViewTitle));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);

        tvCurrentPlan = (TextView) findViewById(R.id.tvCurrentPlan); tvCurrentPlan.setTypeface(Fonts.mavenMedium(this));
        ((TextView) findViewById(R.id.tvExpiresOn)).setTypeface(Fonts.mavenMedium(this));

        rlExpire = (RelativeLayout) findViewById(R.id.rlExpire);
        tvCurrentPlanValue = (TextView) findViewById(R.id.tvCurrentPlanValue);
        tvCurrentPlanValue.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);
        tvExpiresOnValue = (TextView) findViewById(R.id.tvExpiresOnValue);
        tvExpiresOnValue.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);
        tvBenefits = (TextView) findViewById(R.id.tvBenefits); tvBenefits.setTypeface(Fonts.mavenMedium(this));
        rvBenefits = (NonScrollListView) findViewById(R.id.rvBenefits);

        ((TextView) findViewById(R.id.tvSavingsMeter)).setTypeface(Fonts.mavenMedium(this));
        llSavingsValue = (LinearLayout) findViewById(R.id.llSavingsValue);
        tvSavingsMeterRetry = (TextView) findViewById(R.id.tvSavingsMeterRetry);
        tvSavingsMeterRetry.setTypeface(Fonts.mavenMedium(this));

        llRenew = (LinearLayout) findViewById(R.id.llRenew);
        rlWarning = (RelativeLayout) findViewById(R.id.rlWarning);
        tvExpiredTitle = (TextView) findViewById(R.id.tvExpiredTitle); tvExpiredTitle.setTypeface(Fonts.mavenRegular(this));
        rlPlan1 = (RelativeLayout) findViewById(R.id.rlPlan1); rlPlan1.setOnClickListener(this); rlPlan1.setVisibility(View.GONE);
        rlPlan2 = (RelativeLayout) findViewById(R.id.rlPlan2); rlPlan2.setOnClickListener(this); rlPlan2.setVisibility(View.GONE);
        ivRadio1 = (ImageView) findViewById(R.id.ivRadio1); rlPlan1.setOnClickListener(this);
        ivRadio2 = (ImageView) findViewById(R.id.ivRadio2); rlPlan2.setOnClickListener(this);
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
        tvViewBenefits = (TextView) findViewById(R.id.tv_view_benefits); tvViewBenefits.setTypeface(Fonts.mavenMedium(this));
        tvViewBenefits.setOnClickListener(this);
        btnUpgradeNow = (Button) findViewById(R.id.btnUpgradeNow); btnUpgradeNow.setTypeface(Fonts.mavenMedium(this));
        ivStarInfo = (ImageView) findViewById(R.id.ivStarInfo); ivStarInfo.setOnClickListener(this);
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
            if(Data.userData.getSubscriptionData() != null
                    && Data.userData.getSubscriptionData().getUserSubscriptions() != null
                    && Data.userData.getSubscriptionData().getUserSubscriptions().size() > 0) {
                tvCurrentPlanValue.setText(Data.userData.getSubscriptionData().getUserSubscriptions().get(0).getPlanString());
                tvExpiresOnValue.setText(DateOperations.convertDateOnlyViaFormat(DateOperations.utcToLocalWithTZFallback(Data.userData.getSubscriptionData().getUserSubscriptions().get(0).getValidTill())));
            }

//            setBenfitsView();

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

    private void setBenfitsView() {
        tvBenefits.setVisibility(View.VISIBLE);
        starMembershipAdapter = new StarMembershipAdapter(JugnooStarSubscribedActivity.this, Data.userData.getSubscriptionData().getSubscriptionBenefits()
                , new StarMembershipAdapter.Callback() {
            @Override
            public void onUnsubscribe() {
             /*   DialogPopup.alertPopupTwoButtonsWithListeners(JugnooStarSubscribedActivity.this, "", "Are you sure you want to cancel Subscription?"
                        , getResources().getString(R.string.yes), getResources().getString(R.string.no), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                apiCancelSubscription();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }, false, false);*/
            }
        });
        rvBenefits.setAdapter(starMembershipAdapter);
        rvBenefits.setVisibility(View.VISIBLE);
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
                setScreenTitle(MyApplication.getInstance().ACTIVITY_NAME_JUGNOO_STAR);
                //rlFragment.setVisibility(View.GONE);
                btnUpgradeNow.setVisibility(View.VISIBLE);
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
            case R.id.tv_view_benefits:
                openBenefitsFragment(this,rlFragment);
                break;
            case R.id.imageViewBack:
                performBackPressed();
                break;
            case R.id.rlPlan1:
                if(subscriptionSavingsResponse.getRenewalData() != null){
                    if(subscriptionSavingsResponse.getRenewalData().getRenewPlan() != null) {
                        subscription = subscriptionSavingsResponse.getRenewalData().getRenewPlan();
                    }
                    purchaseType = StarPurchaseType.RENEW.getOrdinal();
                } else if(subscriptionSavingsResponse.getExpiredData() != null){
                    if(subscriptionSavingsResponse.getExpiredData().getSubscriptions() != null){
                        subscription = subscriptionSavingsResponse.getExpiredData().getSubscriptions().get(0);
                        purchaseType = StarPurchaseType.PURCHARE.getOrdinal();
                    }
                }
                selectedPlan(ivRadio1, subscription);
                break;
            case R.id.rlPlan2:
                if(subscriptionSavingsResponse.getRenewalData() != null) {
                    if (subscriptionSavingsResponse.getRenewalData().getUpgradePlan() != null
                            && subscriptionSavingsResponse.getRenewalData().getUpgradePlan().get(0).getUpgradeArray() != null) {
                        subscription = subscriptionSavingsResponse.getRenewalData().getUpgradePlan().get(0).getUpgradeArray().get(0);
                        purchaseType = StarPurchaseType.UPGRADE.getOrdinal();
                    }
                } else if(subscriptionSavingsResponse.getExpiredData() != null){
                    if(subscriptionSavingsResponse.getExpiredData().getSubscriptions() != null){
                        subscription = subscriptionSavingsResponse.getExpiredData().getSubscriptions().get(1);
                        purchaseType = StarPurchaseType.PURCHARE.getOrdinal();
                    }
                }
                selectedPlan(ivRadio2, subscription);
                break;
            case R.id.bConfirm:
                openStarCheckoutFragment(JugnooStarSubscribedActivity.this, rlFragment, purchaseType);
                break;
            case R.id.ivStarInfo:
                DialogPopup.alertPopup(JugnooStarSubscribedActivity.this, "", getResources().getString(R.string.star_info_text));
                break;
        }
    }

    private TextView getDigitTextView(String digit, boolean rupeeIcon){
        TextView textView = new TextView(this);
        float ratio = Math.min(ASSL.Xscale(), ASSL.Yscale());
        textView.setPaddingRelative((int) (ratio * 11f), (int) (ratio * 6f), (int) (ratio * 11f), (int) (ratio * 6f));
        textView.setTextColor(getResources().getColor(R.color.text_color));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ratio * 82f);
        textView.setTypeface(Fonts.avenirNext(this));
        if(!rupeeIcon){
            textView.setBackgroundResource(R.drawable.background_white_rounded_bordered);
        }
        textView.setText(digit);
        return textView;
    }

    private void setTotalSavingsValueText(LinearLayout llSavingsValue, int savingValue, String currency){
        llSavingsValue.removeAllViews();
        char[] digits = String.valueOf(savingValue).toCharArray();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins((int)(ASSL.Xscale()*3f), 0, (int)(ASSL.Xscale()*3f), 0);
        params.setMarginStart((int)(ASSL.Xscale()*3f));
        params.setMarginEnd((int)(ASSL.Xscale()*3f));
        llSavingsValue.addView(getDigitTextView(currency, false), params);
        for(char digit : digits){
            llSavingsValue.addView(getDigitTextView(String.valueOf(digit), false), params);
        }
    }


    private void apiFetchTotalSavings() {
        try {
            if (MyApplication.getInstance().isOnline()) {
                DialogPopup.showLoadingDialog(JugnooStarSubscribedActivity.this, getString(R.string.loading));
                llSavingsValue.removeAllViews();
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));

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
                                setTotalSavingsValueText(llSavingsValue, savingsResponse.getTotalSavings(), savingsResponse.getCurrency());
                                setUpgradeView(savingsResponse);
                                if(savingsResponse.getRenewalData() != null) {
                                    setRenewView(savingsResponse);
                                }
                                if(savingsResponse.getExpiredData() != null) {
                                    setExpiredView(savingsResponse);
                                }
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
//        llRenew.setVisibility(View.GONE);
        rlWarning.setVisibility(View.GONE);

        if(savingsResponse.getRenewalData() != null){
            rlExpire.setVisibility(View.GONE);
            llUpgradeContainer.setVisibility(View.GONE);
            tvViewBenefits.setVisibility(View.GONE);
            btnUpgradeNow.setVisibility(View.VISIBLE);
//            llRenew.setVisibility(View.VISIBLE);
//            cvContainer.setVisibility(View.GONE);
              setBenfitsView();



            if(savingsResponse.getRenewalData().getWarning() != null && savingsResponse.getRenewalData().getWarning().getText() != null) {
                rlWarning.setVisibility(View.VISIBLE);
                tvExpiredTitle.setText(savingsResponse.getRenewalData().getWarning().getText());
            }

            // For Renew View
            if(savingsResponse.getRenewalData().getUpgradePlan() == null && savingsResponse.getRenewalData().getRenewPlan() != null){
                rlPlan1.setVisibility(View.GONE);
                rlPlan2.setVisibility(View.GONE);
                subscription = savingsResponse.getRenewalData().getRenewPlan();
                purchaseType = StarPurchaseType.RENEW.getOrdinal();
                selectedPlan(ivRadio1, savingsResponse.getRenewalData().getRenewPlan());
                bConfirm.setText(getResources().getString(R.string.renew));

                btnUpgradeNow.setText(savingsResponse.getRenewalData().getRenewPlan().getFinalAmountText());
            } else if(savingsResponse.getRenewalData().getRenewPlan() != null){
                rlPlan1.setVisibility(View.VISIBLE);
                tvPeriod1.setText(savingsResponse.getRenewalData().getRenewPlan().getPlanString());

                if(savingsResponse.getRenewalData().getRenewPlan().getCrossText() != null && !savingsResponse.getRenewalData().getRenewPlan().getCrossText().equalsIgnoreCase("")){
                    String temp = savingsResponse.getRenewalData().getRenewPlan().getFinalAmountText().replace(crossTextFormatter, savingsResponse.getRenewalData().getRenewPlan().getCrossText());
                    String split[] = savingsResponse.getRenewalData().getRenewPlan().getFinalAmountText().split(Pattern.quote(crossTextFormatter));
                    if(split.length == 1){
                        int first = split[0].length();
                        tvAmount1.setText(temp, TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) tvAmount1.getText();
                        if(savingsResponse.getRenewalData().getRenewPlan().getFinalAmountText().startsWith(crossTextFormatter)){
                            spannable.setSpan(STRIKE_THROUGH_SPAN, 0, tvAmount1.length()-first, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else{
                            spannable.setSpan(STRIKE_THROUGH_SPAN, first, tvAmount1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    } else if(split.length > 1){
                        int first = split[0].length();
                        int last = savingsResponse.getRenewalData().getRenewPlan().getCrossText().length();
                        tvAmount1.setText(temp, TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) tvAmount1.getText();
                        spannable.setSpan(STRIKE_THROUGH_SPAN, first, first+last, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    //tvAmount2.setText(temp);
                    tvAmount1.setTextColor(ContextCompat.getColor(this, R.color.green));
                } else{
                    tvAmount1.setText(savingsResponse.getRenewalData().getRenewPlan().getFinalAmountText());
                }

                selectedPlan(ivRadio1, savingsResponse.getRenewalData().getRenewPlan());
                subscription = savingsResponse.getRenewalData().getRenewPlan();
                purchaseType = StarPurchaseType.RENEW.getOrdinal();
                btnUpgradeNow.setText(savingsResponse.getRenewalData().getRenewPlan().getFinalAmountText());
            }

            // For Upgrade View
            if(savingsResponse.getRenewalData().getUpgradePlan() != null && savingsResponse.getRenewalData().getUpgradePlan().size() > 0){
                rlPlan2.setVisibility(View.VISIBLE);
                SubscriptionData.Subscription upgradeSubscription = savingsResponse.getRenewalData().getUpgradePlan().get(0).getUpgradeArray().get(0);
                tvPeriod2.setText(upgradeSubscription.getPlanString());
                if(upgradeSubscription.getCrossText() != null && !upgradeSubscription.getCrossText().equalsIgnoreCase("")){
                    String temp = upgradeSubscription.getFinalAmountText().replace(crossTextFormatter, upgradeSubscription.getCrossText());
                    String split[] = upgradeSubscription.getFinalAmountText().split(Pattern.quote(crossTextFormatter));
                    if(split.length == 1){
                        int first = split[0].length();
                        tvAmount2.setText(temp, TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) tvAmount2.getText();
                        if(upgradeSubscription.getFinalAmountText().startsWith(crossTextFormatter)){
                            spannable.setSpan(STRIKE_THROUGH_SPAN, 0, tvAmount2.length()-first, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else{
                            //STRIKE_THROUGH_SPAN.updateDrawState(paint);
                            spannable.setSpan(STRIKE_THROUGH_SPAN, first, tvAmount2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    } else if(split.length > 1){
                        int first = split[0].length();
                        int last = upgradeSubscription.getCrossText().length();
                        tvAmount2.setText(temp, TextView.BufferType.SPANNABLE);
                        Spannable spannable = (Spannable) tvAmount2.getText();
                        spannable.setSpan(STRIKE_THROUGH_SPAN, first, first+last, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    //tvAmount2.setText(temp);
                    tvAmount2.setTextColor(ContextCompat.getColor(this, R.color.green));
                } else{
                    tvAmount2.setText(upgradeSubscription.getFinalAmountText());
                }


                btnUpgradeNow.setText(upgradeSubscription.getFinalAmountText());


                if(rlPlan1.getVisibility() == View.VISIBLE){
                    bConfirm.setText(getResources().getString(R.string.confirm));
                } else{
                    bConfirm.setText(getResources().getString(R.string.renew));
                    subscription = upgradeSubscription;
                    purchaseType = StarPurchaseType.UPGRADE.getOrdinal();
                    selectedPlan(ivRadio1, upgradeSubscription);
                }
            }


        }
    }

    private void setUpgradeView(FetchSubscriptionSavingsResponse savingsResponse) {
        try {
            llUpgradeContainer.setVisibility(View.GONE);
            tvViewBenefits.setVisibility(View.GONE);
            if(savingsResponse.getUpgradeData() != null && savingsResponse.getUpgradeData().size() > 0){
                llUpgradeContainer.setVisibility(View.VISIBLE);
                tvViewBenefits.setVisibility(View.VISIBLE);
                tvUpgradingText.setText(savingsResponse.getUpgradeData().get(0).getUpgradingText());
                subscription = savingsResponse.getUpgradeData().get(0).getUpgradeArray().get(0);
                selectedSubId = new Gson().toJson(subscription);
                btnUpgradeNow.setText(subscription.getFinalAmountText());
                btnUpgradeNow.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setExpiredView(FetchSubscriptionSavingsResponse savingsResponse) {
        if(savingsResponse.getExpiredData() != null) {
//            llRenew.setVisibility(View.VISIBLE);
           cvContainer.setVisibility(View.GONE);
            setBenfitsView();
            btnUpgradeNow.setText(R.string.renew);
            btnUpgradeNow.setVisibility(View.VISIBLE);
            tvCurrentPlan.setText(getResources().getString(R.string.previous_plan));
            if (savingsResponse.getExpiredData().getLastSubscription() != null
                    && savingsResponse.getExpiredData().getLastSubscription().getPlanString() != null) {
                tvCurrentPlanValue.setText(savingsResponse.getExpiredData().getLastSubscription().getPlanString());
            }
            rlExpire.setVisibility(View.GONE);

            if (savingsResponse.getExpiredData().getWarning() != null
                    && savingsResponse.getExpiredData().getWarning().getText() != null) {
                rlWarning.setVisibility(View.VISIBLE);
                tvExpiredTitle.setText(savingsResponse.getExpiredData().getWarning().getText());
            }

            if (savingsResponse.getExpiredData().getSubscriptions() != null
                    && savingsResponse.getExpiredData().getSubscriptions().size() > 0) {
                for (int i = 0; i < savingsResponse.getExpiredData().getSubscriptions().size(); i++) {
                    SubscriptionData.Subscription subscriptionTemp = savingsResponse.getExpiredData().getSubscriptions().get(i);
                    if (i == 0) {
                        rlPlan1.setVisibility(View.VISIBLE);
                        if (subscriptionTemp.getInitialAmountText() != null && !TextUtils.isEmpty(subscriptionTemp.getInitialAmountText())) {
                            tvActualAmount1.setVisibility(View.VISIBLE);
                            tvActualAmount1.setText(subscriptionTemp.getInitialAmountText() + " ");
                            tvActualAmount1.setTextColor(ContextCompat.getColor(this, R.color.green));
                            tvAmount1.setTextColor(ContextCompat.getColor(this, R.color.green));
                            //tvActualAmount1.setPaintFlags(tvActualAmount1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }
                        tvAmount1.setText(subscriptionTemp.getFinalAmountText());
                        tvPeriod1.setText(String.valueOf(subscriptionTemp.getPlanStringNew()));
                        subscription = subscriptionTemp;
                        purchaseType = StarPurchaseType.PURCHARE.getOrdinal();
                        selectedPlan(ivRadio1, subscriptionTemp);
                        btnUpgradeNow.setText(subscriptionTemp.getFinalAmountText());
                    } else if (i == 1) {
                        rlPlan2.setVisibility(View.VISIBLE);
                        if (subscriptionTemp.getInitialAmountText() != null && !TextUtils.isEmpty(subscriptionTemp.getInitialAmountText())) {
                            tvActualAmount2.setVisibility(View.VISIBLE);
                            tvActualAmount2.setText(subscriptionTemp.getInitialAmountText() + " ");
                            tvActualAmount2.setTextColor(ContextCompat.getColor(this, R.color.green));
                            tvAmount2.setTextColor(ContextCompat.getColor(this, R.color.green));
                            //tvActualAmount2.setPaintFlags(tvActualAmount2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }
                        tvAmount2.setText(subscriptionTemp.getFinalAmountText());
                        tvPeriod2.setText(String.valueOf(subscriptionTemp.getPlanStringNew()));
                        btnUpgradeNow.setText(subscriptionTemp.getFinalAmountText());
                    }
                }
                bConfirm.setText(getResources().getString(R.string.renew));
            }
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

        if(getListToShowInCheckout()==null) {
            return;
        }

        rlFragment.setVisibility(View.VISIBLE);
        textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_JUGNOO_STAR);
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                .add(container.getId(), StarSubscriptionCheckoutFragment.newInstance(selectedSubId, purchaseType),
                        StarSubscriptionCheckoutFragment.class.getName())
                .addToBackStack(StarSubscriptionCheckoutFragment.class.getName());
        Fragment benefitsFragment  = activity.getSupportFragmentManager().findFragmentByTag(ViewJugnooStarBenefitsFragment.class.getName());
        if(benefitsFragment!=null) {fragmentTransaction.hide(benefitsFragment);}
        fragmentTransaction.commitAllowingStateLoss();
        btnUpgradeNow.setVisibility(View.GONE);
    }



    public void openBenefitsFragment(FragmentActivity activity, View container) {
        rlFragment.setVisibility(View.VISIBLE);
        textViewTitle.setText(getString(R.string.benefits));
        activity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                .add(container.getId(), new ViewJugnooStarBenefitsFragment(),
                        ViewJugnooStarBenefitsFragment.class.getName())
                .addToBackStack(ViewJugnooStarBenefitsFragment.class.getName())
                .commitAllowingStateLoss();
    }

    public void openStarCheckoutFragment() {
        openStarCheckoutFragment(JugnooStarSubscribedActivity.this, rlFragment, StarPurchaseType.UPGRADE.getOrdinal());


    }

    public void setScreenTitle(String string) {
        textViewTitle.setText(string);
    }


    public FetchSubscriptionSavingsResponse getStarSubData(){
        return subscriptionSavingsResponse;
    }

    private ArrayList<SubscriptionData.Subscription> subListCheckout = new ArrayList<>();
    public ArrayList<SubscriptionData.Subscription> getListToShowInCheckout(){
        subListCheckout.clear();




        if(subscriptionSavingsResponse.getRenewalData() != null){
            if(subscriptionSavingsResponse.getRenewalData().getRenewPlan() != null) {
                subscription = subscriptionSavingsResponse.getRenewalData().getRenewPlan();
                subListCheckout.add(subscription);
                subListCheckout.get(0).setStarPurchaseType(StarPurchaseType.RENEW);

            }
            purchaseType = StarPurchaseType.RENEW.getOrdinal();
        } else if(subscriptionSavingsResponse.getExpiredData() != null){
            if(subscriptionSavingsResponse.getExpiredData().getSubscriptions() != null){
                subscription = subscriptionSavingsResponse.getExpiredData().getSubscriptions().get(0);

                purchaseType = StarPurchaseType.PURCHARE.getOrdinal();
                subListCheckout.add(subscription);
                subListCheckout.get(0).setStarPurchaseType(StarPurchaseType.PURCHARE);

            }
        }

        if(subscriptionSavingsResponse.getRenewalData() != null) {
            if (subscriptionSavingsResponse.getRenewalData().getUpgradePlan() != null && subscriptionSavingsResponse.getRenewalData().getUpgradePlan().get(0).getUpgradeArray() != null) {
                subscription = subscriptionSavingsResponse.getRenewalData().getUpgradePlan().get(0).getUpgradeArray().get(0);
                purchaseType = StarPurchaseType.UPGRADE.getOrdinal();
                    subListCheckout.add(subscription);
                    subListCheckout.get(subListCheckout.size()-1).setStarPurchaseType(StarPurchaseType.UPGRADE);

            }
        } else if(subscriptionSavingsResponse.getExpiredData() != null){
            if(subscriptionSavingsResponse.getExpiredData().getSubscriptions() != null){
                subscription = subscriptionSavingsResponse.getExpiredData().getSubscriptions().get(1);
                purchaseType = StarPurchaseType.PURCHARE.getOrdinal();
                     subListCheckout.add(subscription);
                    subListCheckout.get(subListCheckout.size()-1).setStarPurchaseType(StarPurchaseType.PURCHARE);


            }
        }

        //IF only upgrade
        if (subListCheckout.size()==0) {
            if(subscriptionSavingsResponse.getUpgradeData() != null && subscriptionSavingsResponse.getUpgradeData().size() > 0){
                subListCheckout.add(subscriptionSavingsResponse.getUpgradeData().get(0).getUpgradeArray().get(0));
                subListCheckout.get(0).setStarPurchaseType(StarPurchaseType.UPGRADE);
                return subListCheckout;
            }
        }


        return subListCheckout.size()==0?null:subListCheckout;
    }
}
