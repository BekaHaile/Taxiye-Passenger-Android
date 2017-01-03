package product.clicklabs.jugnoo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import product.clicklabs.jugnoo.adapters.StarBenefitsAdapter;
import product.clicklabs.jugnoo.adapters.StarMembershipAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.SubscriptionData;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.FetchUserAddressResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
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
    private TextView tvCurrentPlanValue, tvExpiresOnValue;
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
}
