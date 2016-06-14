package product.clicklabs.jugnoo.promotion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
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

import product.clicklabs.jugnoo.BaseActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.apis.ApiPaytmCheckBalance;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.home.HomeActivity;
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
import product.clicklabs.jugnoo.utils.NudgeClient;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 6/8/16.
 */
public class PromotionActivity extends BaseActivity implements Constants, FlurryEventNames {

    private final String TAG = PromotionActivity.class.getSimpleName();
    private Button buttonAddPromoCode, buttonApplyPromo;
    private RelativeLayout relativeLayoutPromocode;
    private EditText editTextPromoCode;
    private ImageView imageViewClose;

    private RelativeLayout relativeLayoutListTitle, relative;
    private LinearLayout linearLayoutNoOffers;
    private RecyclerView recyclerViewOffers;
    private PromotionsAdapter promotionsAdapter;
    private ImageView imageViewBack, imageViewFreeRideAuto;
    private TextView textViewTitle, textViewFreeRides;


    private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.init(this, Config.getFlurryKey());
        FlurryAgent.onStartSession(this, Config.getFlurryKey());
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_promotions);
        relative = (RelativeLayout) findViewById(R.id.linearLayoutRoot);
        new ASSL(PromotionActivity.this, relative, 1134, 720, false);

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));
        textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_OFFERS);
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryEventLogger.eventGA(Constants.REFERRAL, "Promotions", "Back");
                performBackPressed();
            }
        });

        buttonAddPromoCode = (Button) findViewById(R.id.buttonAddPromoCode);
        buttonAddPromoCode.setTypeface(Fonts.mavenRegular(this));
        buttonApplyPromo = (Button) findViewById(R.id.buttonApplyPromo);
        buttonApplyPromo.setTypeface(Fonts.mavenRegular(this));
        relativeLayoutPromocode = (RelativeLayout) findViewById(R.id.relativeLayoutPromocode);
        editTextPromoCode = (EditText) findViewById(R.id.editTextPromoCode);
        editTextPromoCode.setTypeface(Fonts.mavenRegular(this));
        imageViewClose = (ImageView) findViewById(R.id.imageViewClose);
        textViewFreeRides = (TextView) findViewById(R.id.textViewFreeRides); textViewFreeRides.setTypeface(Fonts.mavenMedium(this));
        imageViewFreeRideAuto = (ImageView) findViewById(R.id.imageViewFreeRideAuto);

        relativeLayoutListTitle = (RelativeLayout) findViewById(R.id.relativeLayoutListTitle);
        ((TextView) findViewById(R.id.textViewOffersAvailable)).setTypeface(Fonts.mavenRegular(this));
        linearLayoutNoOffers = (LinearLayout) findViewById(R.id.linearLayoutNoOffers);
        ((TextView) findViewById(R.id.textViewNoOffers)).setTypeface(Fonts.mavenRegular(this));

        recyclerViewOffers = (RecyclerView) findViewById(R.id.recyclerViewOffers);
        recyclerViewOffers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOffers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOffers.setHasFixedSize(false);

        promotionsAdapter = new PromotionsAdapter(PromotionActivity.this, promoCoupons);
        recyclerViewOffers.setAdapter(promotionsAdapter);

        textViewFreeRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PromotionActivity.this, ShareActivity.class);
                intent.putExtra(Constants.KEY_SHARE_ACTIVITY_FROM_DEEP_LINK, false);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                NudgeClient.trackEventUserId(PromotionActivity.this, FlurryEventNames.NUDGE_FREE_RIDES_CLICKED, null);
            }
        });


        buttonAddPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(PromotionActivity.this, R.anim.scale_in);
                buttonAddPromoCode.setVisibility(View.GONE);
                relativeLayoutPromocode.setVisibility(View.VISIBLE);
                relativeLayoutPromocode.startAnimation(animation);
            }
        });

        buttonApplyPromo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FlurryEventLogger.event(PromotionActivity.this, ENTERED_PROMO_CODE);
                String promoCode = editTextPromoCode.getText().toString().trim();
                if (promoCode.length() > 0) {
                    applyPromoCodeAPI(PromotionActivity.this, promoCode);
                    FlurryEventLogger.event(PromotionActivity.this, CLICKS_ON_APPLY);
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

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(PromotionActivity.this, R.anim.scale_out);
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

                    RestClient.getApiServices().getCouponsAndPromotions(params, new Callback<SettleUserDebt>() {
                        @Override
                        public void success(SettleUserDebt settleUserDebt, Response response) {
                            String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                            Log.i(TAG, "getCouponsAndPromotions response = " + responseStr);
                            try {
                                JSONObject jObj = new JSONObject(responseStr);

                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    int flag = jObj.getInt("flag");
                                    String message = JSONParser.getServerMessage(jObj);
                                    if (ApiResponseFlags.COUPONS.getOrdinal() == flag) {
                                        promoCoupons.clear();
                                        promoCoupons.addAll(JSONParser.parseCouponsArray(jObj));
                                        promoCoupons.addAll(JSONParser.parsePromotionsArray(jObj));
                                        updateListData();
                                        if (Data.userData != null) {
                                            Data.userData.numCouponsAvaliable = promoCoupons.size();
                                        }
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

                                    new ApiPaytmCheckBalance(activity, new ApiPaytmCheckBalance.Callback() {
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

                                        @Override
                                        public void paytmDisabled() {

                                        }
                                    }).getBalance(Data.userData.paytmEnabled, false);

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
