package product.clicklabs.jugnoo.promotion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.BaseActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Events;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.PromCouponResponse;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.promotion.adapters.OfferingPromotionsAdapter;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 6/8/16.
 */
public class PromotionActivity extends BaseActivity implements Constants,  GAAction, GACategory {

    private final String TAG = PromotionActivity.class.getSimpleName();
    private Button buttonApplyPromo;
    private EditText editTextPromoCode;

    private RelativeLayout relative;
    private LinearLayout linearLayoutNoOffers;
    private RecyclerView recyclerViewOffers;
    private ImageView imageViewBack, imageViewFreeRideAuto;
    private TextView textViewTitle, textViewFreeRides;



    private ArrayList<OfferingPromotion> offeringPromotions = new ArrayList<>();
    private OfferingPromotionsAdapter offeringPromotionsAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    private boolean codeEntered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotions);
        relative = (RelativeLayout) findViewById(R.id.linearLayoutRoot);

        GAUtils.trackScreenView(PROMOTIONS);

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));
        textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_OFFERS);
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        buttonApplyPromo = (Button) findViewById(R.id.buttonApplyPromo);
        buttonApplyPromo.setTypeface(Fonts.mavenRegular(this));
        editTextPromoCode = (EditText) findViewById(R.id.editTextPromoCode);
        editTextPromoCode.setTypeface(Fonts.mavenRegular(this));
        textViewFreeRides = (TextView) findViewById(R.id.textViewFreeRides); textViewFreeRides.setTypeface(Fonts.mavenMedium(this));
        imageViewFreeRideAuto = (ImageView) findViewById(R.id.imageViewFreeRideAuto);

        linearLayoutNoOffers = (LinearLayout) findViewById(R.id.linearLayoutNoOffers);
        ((TextView) findViewById(R.id.textViewNoOffers)).setTypeface(Fonts.mavenRegular(this));

        recyclerViewOffers = (RecyclerView) findViewById(R.id.recyclerViewOffers);
        recyclerViewOffers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOffers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOffers.setHasFixedSize(false);

        offeringPromotionsAdapter = new OfferingPromotionsAdapter(this, offeringPromotions);
        recyclerViewOffers.setAdapter(offeringPromotionsAdapter);

        textViewFreeRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PromotionActivity.this, ShareActivity.class);
                intent.putExtra(Constants.KEY_SHARE_ACTIVITY_FROM_DEEP_LINK, false);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
                GAUtils.event(SIDE_MENU, PROMOTIONS, GET_FREE_JUGNOO_CASH+CLICKED);
            }
        });


        buttonApplyPromo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String promoCode = editTextPromoCode.getText().toString().trim();
                if (promoCode.length() > 0) {
                    applyPromoCodeAPI(PromotionActivity.this, promoCode);
                    HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                    profileUpdate.put(Events.PROMO_CODE_USED, promoCode);
                    MyApplication.getInstance().getCleverTap().profile.push(profileUpdate);
                    GAUtils.event(SIDE_MENU, PROMOTIONS, GAAction.PROMO_CODE+APPLIED);
                } else {
                    editTextPromoCode.requestFocus();
                    editTextPromoCode.setError("Code can't be empty");
                }
            }
        });

        imageViewFreeRideAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewFreeRides.performClick();
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

        editTextPromoCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length()>0 && !codeEntered){
                    GAUtils.event(SIDE_MENU, PROMOTIONS, GAAction.PROMO_CODE+ENTERED);
                }
                codeEntered = true;
            }
        });


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getCouponsAndPromotions(PromotionActivity.this);
    }

    public void performBackPressed(){
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }


    @Override
    public void onDestroy() {
        try {
            ASSL.closeActivity(relative);
            System.gc();
            super.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateListData() {
        if (offeringPromotions.size() == 0) {
            recyclerViewOffers.setVisibility(View.GONE);
            linearLayoutNoOffers.setVisibility(View.VISIBLE);
        } else{
            recyclerViewOffers.setVisibility(View.VISIBLE);
            linearLayoutNoOffers.setVisibility(View.GONE);
            offeringPromotionsAdapter.notifyDataSetChanged();
            updateUserCoupons();
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
                if (MyApplication.getInstance().isOnline()) {
                    DialogPopup.showLoadingDialog(activity, "Loading...");

                    HashMap<String, String> params = new HashMap<>();
                    params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                    params.put(Constants.KEY_LATITUDE, "" + Data.latitude);
                    params.put(Constants.KEY_LONGITUDE, "" + Data.longitude);

                    new HomeUtil().putDefaultParams(params);
                    RestClient.getApiService().getCouponsAndPromotions(params, new Callback<PromCouponResponse>() {
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
                                        offeringPromotions.clear();

                                        ArrayList<PromoCoupon> pcRides = new ArrayList<PromoCoupon>();
                                        ArrayList<PromoCoupon> pcMenus = new ArrayList<PromoCoupon>();
                                        ArrayList<PromoCoupon> pcFatafat = new ArrayList<PromoCoupon>();
                                        ArrayList<PromoCoupon> pcMeals = new ArrayList<PromoCoupon>();

                                        fillMasterPromoCoupons(promCouponResponse.getCommonPromotions(), pcRides, pcMenus, pcFatafat, pcMeals);
                                        fillMasterPromoCoupons(promCouponResponse.getCommonCoupons(), pcRides, pcMenus, pcFatafat, pcMeals);

                                        if(promCouponResponse.getAutosPromotions() != null)
                                            pcRides.addAll(promCouponResponse.getAutosPromotions());
                                        if(promCouponResponse.getAutosCoupons() != null)
                                            pcRides.addAll(promCouponResponse.getAutosCoupons());

                                        if(promCouponResponse.getMenusPromotions() != null)
                                            pcMenus.addAll(promCouponResponse.getMenusPromotions());
                                        if(promCouponResponse.getMenusCoupons() != null)
                                            pcMenus.addAll(promCouponResponse.getMenusCoupons());

                                        if(promCouponResponse.getFreshPromotions() != null)
                                            pcFatafat.addAll(promCouponResponse.getFreshPromotions());
                                        if(promCouponResponse.getFreshCoupons() != null)
                                            pcFatafat.addAll(promCouponResponse.getFreshCoupons());
                                        if(promCouponResponse.getGroceryPromotions() != null)
                                            pcFatafat.addAll(promCouponResponse.getGroceryPromotions());
                                        if(promCouponResponse.getGroceryCoupons() != null)
                                            pcFatafat.addAll(promCouponResponse.getGroceryCoupons());

                                        if(promCouponResponse.getMealsPromotions() != null)
                                            pcMeals.addAll(promCouponResponse.getMealsPromotions());
                                        if(promCouponResponse.getMealsCoupons() != null)
                                            pcMeals.addAll(promCouponResponse.getMealsCoupons());


                                        offeringPromotions.add(new OfferingPromotion(getString(R.string.rides), R.drawable.ic_auto_grey, pcRides));
                                        offeringPromotions.add(new OfferingPromotion(getString(R.string.menus), R.drawable.ic_menus_grey, pcMenus));
                                        offeringPromotions.add(new OfferingPromotion(getString(R.string.fatafat), R.drawable.ic_fresh_grey, pcFatafat));
                                        offeringPromotions.add(new OfferingPromotion(getString(R.string.meals), R.drawable.ic_meals_grey, pcMeals));

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

    private void fillMasterPromoCoupons(List promoCouponsMaster,
                                        ArrayList<PromoCoupon> pcRides,
                                        ArrayList<PromoCoupon> pcMenus,
                                        ArrayList<PromoCoupon> pcFatafat,
                                        ArrayList<PromoCoupon> pcMeals){
        if(promoCouponsMaster != null) {
            List<PromoCoupon> promoCoupons = promoCouponsMaster;
            for(PromoCoupon promoCoupon : promoCoupons){
                if(promoCoupon.getAutos() == 1){
                    pcRides.add(promoCoupon);
                } else if(promoCoupon.getMenus() == 1){
                    pcMenus.add(promoCoupon);
                } else if(promoCoupon.getFresh() == 1 || promoCoupon.getGrocery() == 1){
                    pcFatafat.add(promoCoupon);
                } else if(promoCoupon.getMeals() == 1){
                    pcMeals.add(promoCoupon);
                }
            }
        }
    }

    private void updateUserCoupons() {
        try{
            ArrayList<String> coupons = new ArrayList<>();
            double maxValue = 0.0;
            if(offeringPromotions != null) {
                for(OfferingPromotion offeringPromotion : offeringPromotions){
                    for(PromoCoupon promoCoupon : offeringPromotion.getPromoCoupons()){
                        coupons.add(promoCoupon.getTitle());
                        String value = MyApplication.getInstance().getCleverTapUtils().getCouponValue(promoCoupon.getTitle());
                        if(value.length()>0) {
                            coupons.add(value);
                            maxValue = MyApplication.getInstance().getCleverTapUtils().getCouponMaxValue(maxValue, value);
                        }
                    }
                }
            }
            MyApplication.getInstance().udpateUserData(Events.COUPONS, coupons);

            DecimalFormat df = new DecimalFormat("#.##");
            HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
            profileUpdate.put(Events.MAX_COUPON_VALUE, df.format(maxValue));
            MyApplication.getInstance().getCleverTap().profile.push(profileUpdate);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }



    private void retryDialog(DialogErrorType dialogErrorType, String message){
        if(dialogErrorType == DialogErrorType.OTHER){
            DialogPopup.alertPopup(PromotionActivity.this, "", message);
        } else{
            DialogPopup.dialogNoInternet(PromotionActivity.this, dialogErrorType, new Utils.AlertCallBackWithButtonsInterface() {
                @Override
                public void positiveClick(View view) {
                    getCouponsAndPromotions(PromotionActivity.this);
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
                if (MyApplication.getInstance().isOnline()) {
                    DialogPopup.showLoadingDialog(activity, "Loading...");

                    HashMap<String, String> params = new HashMap<>();
                    params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                    params.put(Constants.KEY_CODE, promoCode);

                    new HomeUtil().putDefaultParams(params);
                    RestClient.getApiService().enterCode(params, new Callback<SettleUserDebt>() {
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
