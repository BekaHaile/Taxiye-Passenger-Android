package product.clicklabs.jugnoo.promotion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Events;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.apis.ApiFetchWalletBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.PromCouponResponse;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.models.VehicleTypeValue;
import product.clicklabs.jugnoo.promotion.adapters.PromoAdapter;
import product.clicklabs.jugnoo.promotion.fragments.PromoDescriptionFragment;
import product.clicklabs.jugnoo.promotion.models.Promo;
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
public class PromotionActivity extends BaseFragmentActivity implements Constants,  GAAction, GACategory {

    private final String TAG = PromotionActivity.class.getSimpleName();
    private Button buttonApplyPromo;
    private EditText editTextPromoCode;

    private RelativeLayout relative;
    private LinearLayout linearLayoutNoOffers;
    private RecyclerView recyclerViewOffers;
    private ImageView imageViewBack, imageViewFreeRideAuto;
    private TextView textViewTitle, textViewFreeRides;
    private LinearLayout llContainer;


    private ArrayList<Promo> promosList = new ArrayList<>();
    private PromoAdapter promoAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }


    private boolean codeEntered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotions);
        relative = (RelativeLayout) findViewById(R.id.linearLayoutRoot);

        GAUtils.trackScreenView(PROMOTIONS);
        Log.e("PromotionActivity", "onCreate");

        llContainer = (LinearLayout) findViewById(R.id.llContainer); llContainer.setVisibility(View.GONE);
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
        textViewFreeRides = (TextView) findViewById(R.id.textViewFreeRides); textViewFreeRides.setTypeface(Fonts.mavenMedium(this));
        textViewFreeRides.setText(getString(R.string.want_free_rides, getString(R.string.app_name)));
        imageViewFreeRideAuto = (ImageView) findViewById(R.id.imageViewFreeRideAuto);

        linearLayoutNoOffers = (LinearLayout) findViewById(R.id.linearLayoutNoOffers);
        ((TextView) findViewById(R.id.textViewNoOffers)).setTypeface(Fonts.mavenRegular(this));

        recyclerViewOffers = (RecyclerView) findViewById(R.id.recyclerViewOffers);
        recyclerViewOffers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOffers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOffers.setHasFixedSize(false);

        promoAdapter = new PromoAdapter(this, promosList, recyclerViewOffers, new PromoAdapter.Callback() {
            @Override
            public void onPromoClick(Promo promo) {
                openPromoDescriptionFragment(promo.getName(), promo.getClientId(), promo.getPromoCoupon());
                GAUtils.event(SIDE_MENU, PROMOTIONS + OFFER + TNC + CLICKED, promo.getPromoCoupon().getTitle());
            }
        });
        recyclerViewOffers.setAdapter(promoAdapter);

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
                String promoCode = editTextPromoCode.getText().toString().trim().toUpperCase();
                if (promoCode.length() > 0) {
                    applyPromoCodeAPI(PromotionActivity.this, promoCode);
                    HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
                    profileUpdate.put(Events.PROMO_CODE_USED, promoCode);
                    GAUtils.event(SIDE_MENU, PROMOTIONS, GAAction.PROMO_CODE+APPLIED);
                } else {
                    Utils.hideKeyboard(PromotionActivity.this);
                    buttonApplyPromo.setEnabled(false);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showToast(PromotionActivity.this, getString(R.string.code_cant_be_empty));
                            buttonApplyPromo.setEnabled(true);
                        }
                    }, 200);
                }
            }
        });

        imageViewFreeRideAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewFreeRides.performClick();
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

        llContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        new HomeUtil().forceRTL(this);
    }

    public void performBackPressed(){
        if(getSupportFragmentManager().findFragmentByTag(PromoDescriptionFragment.class.getName()) != null){
            removeFragment(getSupportFragmentManager().findFragmentByTag(PromoDescriptionFragment.class.getName()));
        } else {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
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
        if (promosList.size() == 0) {
            recyclerViewOffers.setVisibility(View.GONE);
            linearLayoutNoOffers.setVisibility(View.VISIBLE);
        } else{
            recyclerViewOffers.setVisibility(View.VISIBLE);
            linearLayoutNoOffers.setVisibility(View.GONE);
            promoAdapter.notifyDataSetChanged();
        }
    }

    public void getCouponsAndPromotions(final Activity activity) {
        try {
            if(!HomeActivity.checkIfUserDataNull(activity)) {
                if (MyApplication.getInstance().isOnline()) {
                    DialogPopup.showLoadingDialog(activity, getString(R.string.loading));

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

                                        parsePromoCoupons(promCouponResponse);
                                        updateListData();

                                    } else {
                                        updateListData();
                                        retryDialogGetPromos(DialogErrorType.OTHER, message);
                                    }
                                } else {
                                    updateListData();
                                }

                            } catch (Exception exception) {
                                exception.printStackTrace();
                                updateListData();
                                retryDialogGetPromos(DialogErrorType.SERVER_ERROR, "");
                            }
                            DialogPopup.dismissLoadingDialog();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, "getCouponsAndPromotions error="+error.toString());
                            DialogPopup.dismissLoadingDialog();
                            updateListData();
                            retryDialogGetPromos(DialogErrorType.CONNECTION_LOST, "");
                        }
                    });
                } else {
                    retryDialogGetPromos(DialogErrorType.NO_NET, "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillMasterPromoCoupons(List promoCouponsMaster,
                                        ArrayList<PromoCoupon> pcAll,
                                        ArrayList<PromoCoupon> pcRides,
                                        ArrayList<PromoCoupon> pcMenus,
                                        ArrayList<PromoCoupon> pcDeliveryCustomer,
                                        ArrayList<PromoCoupon> pcFatafat,
                                        ArrayList<PromoCoupon> pcMeals){
        if(promoCouponsMaster != null) {
            List<PromoCoupon> promoCoupons = promoCouponsMaster;
            for(PromoCoupon promoCoupon : promoCoupons){
                if(pcAll != null
                        && promoCoupon.getAutos() == 1
                        && promoCoupon.getMenus() == 1
                        && promoCoupon.getDeliveryCustomer() == 1
                        && (promoCoupon.getFresh() == 1 || promoCoupon.getGrocery() == 1)
                        && promoCoupon.getMeals() == 1){
                    pcAll.add(promoCoupon);
                } else {
                    if (promoCoupon.getAutos() == 1) {
                        pcRides.add(promoCoupon);
                    }
                    if (promoCoupon.getMenus() == 1) {
                        pcMenus.add(promoCoupon);
                    }
                    if (promoCoupon.getDeliveryCustomer() == 1) {
                        pcDeliveryCustomer.add(promoCoupon);
                    }
                    if (promoCoupon.getFresh() == 1 || promoCoupon.getGrocery() == 1) {
                        pcFatafat.add(promoCoupon);
                    }
                    if (promoCoupon.getMeals() == 1) {
                        pcMeals.add(promoCoupon);
                    }
                }
            }
        }
    }




    private void retryDialogGetPromos(DialogErrorType dialogErrorType, String message){
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
                    DialogPopup.showLoadingDialog(activity, getString(R.string.loading));

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
                                    editTextPromoCode.setText("");
                                } else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
                                    String message = jObj.getString("message");
                                    DialogPopup.dialogBanner(activity, message);
                                    getCouponsAndPromotions(activity);
                                    editTextPromoCode.setText("");

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
                                    DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));

                            }
                            DialogPopup.dismissLoadingDialog();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, "enterCode error="+error.toString());
                            DialogPopup.dismissLoadingDialog();
                            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                        }
                    });
                } else {
                    DialogPopup.dialogNoInternet(activity,
                            activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
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

    public void openPromoDescriptionFragment(String offeringName, String clientId, PromoCoupon promoCoupon){
        removeFragment(getSupportFragmentManager().findFragmentByTag(PromoDescriptionFragment.class.getName()));
        Fragment fragment = PromoDescriptionFragment.newInstance(offeringName, clientId, promoCoupon);
        llContainer.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(llContainer.getId(),
                        fragment,
                        PromoDescriptionFragment.class.getName())
                .commitAllowingStateLoss();
        textViewTitle.setText(getString(R.string.terms_of_use));
    }

    public void removeFragment(Fragment fragment){
        if(fragment != null) {
            llContainer.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
            textViewTitle.setText(getString(R.string.promotions));
        }
    }

    private Handler handler = new Handler();


    private void oldOfferingParse(PromCouponResponse promCouponResponse){
        ArrayList<OfferingPromotion> offeringPromotions = new ArrayList<>();
        offeringPromotions.clear();

        ArrayList<PromoCoupon> pcRides = new ArrayList<PromoCoupon>();
        ArrayList<PromoCoupon> pcMenus = new ArrayList<PromoCoupon>();
        ArrayList<PromoCoupon> pcDeliveryCustomer = new ArrayList<PromoCoupon>();
        ArrayList<PromoCoupon> pcFatafat = new ArrayList<PromoCoupon>();
        ArrayList<PromoCoupon> pcMeals = new ArrayList<PromoCoupon>();

        fillMasterPromoCoupons(promCouponResponse.getCommonPromotions(), null, pcRides, pcMenus, pcDeliveryCustomer, pcFatafat, pcMeals);
        fillMasterPromoCoupons(promCouponResponse.getCommonCoupons(), null, pcRides, pcMenus, pcDeliveryCustomer, pcFatafat, pcMeals);

        if(promCouponResponse.getAutosPromotions() != null)
            pcRides.addAll(promCouponResponse.getAutosPromotions());
        if(promCouponResponse.getAutosCoupons() != null)
            pcRides.addAll(promCouponResponse.getAutosCoupons());

        if(promCouponResponse.getMenusPromotions() != null)
            pcMenus.addAll(promCouponResponse.getMenusPromotions());
        if(promCouponResponse.getMenusCoupons() != null)
            pcMenus.addAll(promCouponResponse.getMenusCoupons());

        if(promCouponResponse.getDeliveryCustomerPromotions() != null)
            pcDeliveryCustomer.addAll(promCouponResponse.getDeliveryCustomerPromotions());
        if(promCouponResponse.getDeliveryCustomerCoupons() != null)
            pcDeliveryCustomer.addAll(promCouponResponse.getDeliveryCustomerCoupons());

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


        if(pcRides.size() > 0) {
            offeringPromotions.add(new OfferingPromotion(getString(R.string.rides), Config.getAutosClientId(),
                    R.drawable.ic_auto_grey, pcRides));
        }
        if(pcMeals.size() > 0) {
            offeringPromotions.add(new OfferingPromotion(getString(R.string.meals), Config.getMealsClientId(),
                    R.drawable.ic_meals_grey, pcMeals));
        }
        if(pcFatafat.size() > 0) {
            offeringPromotions.add(new OfferingPromotion(getString(R.string.fatafat), Config.getFreshClientId(),
                    R.drawable.ic_grocery_grey_vector, pcFatafat));
        }
        if(pcMenus.size() > 0) {
            offeringPromotions.add(new OfferingPromotion(getString(R.string.menus), Config.getMenusClientId(),
                    R.drawable.ic_menus_grey, pcMenus));
        }
        if(pcDeliveryCustomer.size() > 0) {
            offeringPromotions.add(new OfferingPromotion(getString(R.string.delivery_new_name), Config.getDeliveryCustomerClientId(),
                    R.drawable.ic_menus_grey, pcDeliveryCustomer));
        }
    }


    private void parsePromoCoupons(PromCouponResponse promCouponResponse){
        promosList.clear();

        ArrayList<PromoCoupon> pcAll = new ArrayList<PromoCoupon>();
        ArrayList<PromoCoupon> pcRides = new ArrayList<PromoCoupon>();
        ArrayList<PromoCoupon> pcMenus = new ArrayList<PromoCoupon>();
        ArrayList<PromoCoupon> pcDeliveryCustomer = new ArrayList<PromoCoupon>();
        ArrayList<PromoCoupon> pcFatafat = new ArrayList<PromoCoupon>();
        ArrayList<PromoCoupon> pcMeals = new ArrayList<PromoCoupon>();

        fillMasterPromoCoupons(promCouponResponse.getCommonPromotions(), pcAll, pcRides, pcMenus, pcDeliveryCustomer, pcFatafat, pcMeals);
        fillMasterPromoCoupons(promCouponResponse.getCommonCoupons(), pcAll, pcRides, pcMenus, pcDeliveryCustomer, pcFatafat, pcMeals);

        if(promCouponResponse.getAutosPromotions() != null)
            pcRides.addAll(promCouponResponse.getAutosPromotions());
        if(promCouponResponse.getAutosCoupons() != null)
            pcRides.addAll(promCouponResponse.getAutosCoupons());

        if(promCouponResponse.getMenusPromotions() != null)
            pcMenus.addAll(promCouponResponse.getMenusPromotions());
        if(promCouponResponse.getMenusCoupons() != null)
            pcMenus.addAll(promCouponResponse.getMenusCoupons());

        if(promCouponResponse.getDeliveryCustomerPromotions() != null)
            pcDeliveryCustomer.addAll(promCouponResponse.getDeliveryCustomerPromotions());
        if(promCouponResponse.getDeliveryCustomerCoupons() != null)
            pcDeliveryCustomer.addAll(promCouponResponse.getDeliveryCustomerCoupons());

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


        if(pcAll.size() > 0){
            pcAll = countAndRemoveDuplicatePromoCoupons(pcAll);
            for(PromoCoupon pc : pcAll){
                promosList.add(new Promo(getString(R.string.all), "", pc, R.drawable.ic_promo_all, -1));
            }
        }
        if(pcRides.size() > 0) {
            pcRides = countAndRemoveDuplicatePromoCoupons(pcRides);
            for(PromoCoupon pc : pcRides){
                int id  = R.drawable.ic_promo_all;
                if(pc.getAllowedVehicles()!=null && pc.getAllowedVehicles().size()==1){
                    if(pc.getAllowedVehicles().get(0).equals(VehicleTypeValue.TAXI.getOrdinal())){
                        id = R.drawable.ic_taxi_gradient;
                    } else {
                        id = R.drawable.ic_promo_rides;
                    }
                }

                promosList.add(new Promo(getString(R.string.rides), Config.getAutosClientId(), pc, id, R.color.theme_color));
            }
        }
        if(pcMeals.size() > 0) {
            pcMeals = countAndRemoveDuplicatePromoCoupons(pcMeals);
            for(PromoCoupon pc : pcMeals){
                promosList.add(new Promo(getString(R.string.meals), Config.getMealsClientId(), pc, R.drawable.ic_promo_meals, R.color.pink_meals_fab));
            }
        }
        if(pcFatafat.size() > 0) {
            pcFatafat = countAndRemoveDuplicatePromoCoupons(pcFatafat);
            for(PromoCoupon pc : pcFatafat){
                promosList.add(new Promo(getString(R.string.fatafat), Config.getFreshClientId(), pc, R.drawable.ic_promo_fresh, R.color.fresh_promotions_green));
            }
        }
        if(pcMenus.size() > 0) {
            pcMenus = countAndRemoveDuplicatePromoCoupons(pcMenus);
            for(PromoCoupon pc : pcMenus){
                promosList.add(new Promo(getString(R.string.menus), Config.getMenusClientId(), pc, R.drawable.ic_promo_menus, R.color.purple_menus_fab));
            }
        }
        if(pcDeliveryCustomer.size() > 0) {
            pcDeliveryCustomer = countAndRemoveDuplicatePromoCoupons(pcDeliveryCustomer);
            for(PromoCoupon pc : pcDeliveryCustomer){
                promosList.add(new Promo(getString(R.string.delivery_new_name), Config.getDeliveryCustomerClientId(), pc, R.drawable.ic_promo_menus, R.color.purple_menus_fab));
            }
        }


    }


    private ArrayList<PromoCoupon> countAndRemoveDuplicatePromoCoupons(ArrayList<PromoCoupon> promoCoupons){
        for(PromoCoupon promoCoupon : promoCoupons){
            if(promoCoupon instanceof CouponInfo){
                ((CouponInfo)promoCoupon).setCheckWithCouponId(true);
            }
            promoCoupon.setRepeatedCount(Collections.frequency(promoCoupons, promoCoupon));
        }

        Set set = new TreeSet(new Comparator<PromoCoupon>() {
            @Override
            public int compare(PromoCoupon o1, PromoCoupon o2) {
                if (o1.equals(o2)) {
                    return 0;
                }
                return 1;
            }
        });
        set.addAll(promoCoupons);
        promoCoupons = new ArrayList<>(set);
        return promoCoupons;
    }



}
