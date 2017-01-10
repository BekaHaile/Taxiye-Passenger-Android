package product.clicklabs.jugnoo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import product.clicklabs.jugnoo.adapters.StarMembershipAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.SubscriptionData;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.FetchSubscriptionSavingsResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 27/12/16.
 */

public class JugnooStarSubscribedActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout relative;
    private TextView textViewTitle;
    private ImageView imageViewBack;
    private TextView tvCurrentPlanValue, tvExpiresOnValue, tvSavingsMeterRetry;
    private LinearLayout llSavingsValue;
    private ProgressWheel progressWheel;
    private RecyclerView rvBenefits;
    private StarMembershipAdapter starMembershipAdapter;
    private ArrayList<String> benefits = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugnoo_star_subscribed);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(JugnooStarSubscribedActivity.this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);

        ((TextView) findViewById(R.id.tvCurrentPlan)).setTypeface(Fonts.mavenMedium(this));
        ((TextView) findViewById(R.id.tvExpiresOn)).setTypeface(Fonts.mavenMedium(this));

        tvCurrentPlanValue = (TextView) findViewById(R.id.tvCurrentPlanValue);
        tvCurrentPlanValue.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);
        tvExpiresOnValue = (TextView) findViewById(R.id.tvExpiresOnValue);
        tvExpiresOnValue.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);
        rvBenefits = (RecyclerView) findViewById(R.id.rvBenefits);
        rvBenefits.setLayoutManager(new LinearLayoutManager(this));
        rvBenefits.setItemAnimator(new DefaultItemAnimator());
        rvBenefits.setHasFixedSize(false);

        ((TextView) findViewById(R.id.tvSavingsMeter)).setTypeface(Fonts.mavenMedium(this));
        llSavingsValue = (LinearLayout) findViewById(R.id.llSavingsValue);
        progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
        tvSavingsMeterRetry = (TextView) findViewById(R.id.tvSavingsMeterRetry);
        tvSavingsMeterRetry.setTypeface(Fonts.mavenMedium(this));
        apiFetchTotalSavings();

        tvSavingsMeterRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiFetchTotalSavings();
            }
        });

        try {
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


            starMembershipAdapter = new StarMembershipAdapter(JugnooStarSubscribedActivity.this, benefits, benefitOffering, new StarMembershipAdapter.Callback() {
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

            tvCurrentPlanValue.setText(Data.userData.getSubscriptionData().getUserSubscriptions().get(0).getPlanString());
            tvExpiresOnValue.setText(DateOperations.convertDateOnlyViaFormat(DateOperations.utcToLocalWithTZFallback(Data.userData.getSubscriptionData().getUserSubscriptions().get(0).getValidTill())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apiCancelSubscription() {
        try {
            if (AppStatus.getInstance(JugnooStarSubscribedActivity.this).isOnline(JugnooStarSubscribedActivity.this)) {
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
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                performBackPressed();
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
            if (AppStatus.getInstance(this).isOnline(this)) {
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
                            if (savingsResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                llSavingsValue.removeAllViews();
                                setTotalSavingsValueText(llSavingsValue, savingsResponse.getTotalSavings());
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

    @Override
    public void onBackPressed() {
        performBackPressed();
    }
}
