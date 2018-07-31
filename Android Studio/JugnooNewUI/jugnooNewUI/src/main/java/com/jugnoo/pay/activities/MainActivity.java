package com.jugnoo.pay.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.google.android.gms.maps.model.LatLng;
import com.jugnoo.pay.adapters.PendingTrnscAdapater;
import com.jugnoo.pay.models.AccountManagementResponse;
import com.jugnoo.pay.models.AccountMgmtCallbackRequest;
import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.models.FetchPayDataResponse;
import com.jugnoo.pay.models.SendMoneyResponse;
import com.jugnoo.pay.models.SetMPINResponse;
import com.jugnoo.pay.models.TransacHistoryResponse;
import com.jugnoo.pay.models.VerifyRegisterResponse;
import com.jugnoo.pay.models.VerifyUserRequest;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.PrefManager;
import com.jugnoo.pay.utils.SharedPreferencesName;
import com.sabkuchfresh.utils.AppConstant;
import com.yesbank.AddAccount;
import com.yesbank.Registration;
import com.yesbank.SetMpin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.BaseAppCompatActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.home.FABViewTest;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.home.MenuBar;
import product.clicklabs.jugnoo.promotion.ReferralActions;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends BaseAppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @OnClick(R.id.back_btn)
    void backBtnClicked() {
        drawer.openDrawer(GravityCompat.START);
    }

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private final int VPA_REGISTER_INTENT_REQUEST_CODE = 121;
    private final int CHANGE_MPIN_INTENT_REQUEST_CODE = 122;
    private final int MANAGE_ACCOUNT_INTENT_REQUEST_CODE = 123;

    private boolean isSendingMoney = true;
    @OnClick(R.id.linearLayoutSendMoney)
    void sendMoneyImgClicked(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            isSendingMoney = true;
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.READ_CONTACTS }, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else {
            startActivity(new Intent(MainActivity.this,SelectContactActivity.class));
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if(isSendingMoney) {
                        startActivity(new Intent(MainActivity.this,SelectContactActivity.class));
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    }
                    else {
                        Intent intent = new Intent(MainActivity.this,SelectContactActivity.class);
                        intent.putExtra(AppConstant.REQUEST_STATUS,true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    DialogPopup.alertPopup(this, "", getString(R.string.please_make_sure_to_grant_contacts_permission));
                }
                return;
            }
        }
    }



    @OnClick(R.id.linearLayoutRequestMoney)
    void requestMoneyImgClicked() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            isSendingMoney = false;
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.READ_CONTACTS }, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            Intent intent = new Intent(MainActivity.this,SelectContactActivity.class);
            intent.putExtra(AppConstant.REQUEST_STATUS,true);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
    }

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;


    void textViewAboutClicked() {
        CommonMethods.openUrl(MainActivity.this, userDetails.getSupport_link());
    }


    private CommonResponse userDetails;
    Intent intent;

    private RecyclerView recyclerViewPendingPayments;
    private PendingTrnscAdapater pendingTrnscAdapater;
    private ArrayList<TransacHistoryResponse.TransactionHistory> transactionHistories;

    private FABViewTest fabViewTest;
    private TextView textViewPaymentIdValue;
    private RelativeLayout relativeLayoutNoPayments;
    private String vpa = "";
    private MenuBar menuBar;
    private ImageButton imageButtonBack;
    private TextView textViewToolbarTitle;
    private ImageView toolbarDivider, ivToolbarSetting, imageViewSharePaymentId;
    private FetchPayDataResponse fetchPayDataResponse;
    private CallbackManager callbackManager;

    private LatLng selectedLatLng;
    private int goBack = 0;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);
            setSupportActionBar(mToolBar);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.setDrawerIndicatorEnabled(false);

            callbackManager = CallbackManager.Factory.create();

            try {
                if(com.sabkuchfresh.utils.Utils.compareDouble(Data.latitude, 0) == 0 && com.sabkuchfresh.utils.Utils.compareDouble(Data.longitude, 0) == 0){
                    Data.latitude = Data.loginLatitude;
                    Data.longitude = Data.loginLongitude;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            toggle.syncState();
            userDetails = Prefs.with(MainActivity.this).getObject(SharedPreferencesName.APP_USER, CommonResponse.class);


            if(getIntent().hasExtra(Constants.KEY_LATITUDE) && getIntent().hasExtra(Constants.KEY_LONGITUDE)){
                setSelectedLatLng(new LatLng(getIntent().getDoubleExtra(Constants.KEY_LATITUDE, Data.latitude),
                        getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, Data.longitude)));
            }

            prefManager = new PrefManager(MainActivity.this);
            int comingFromPayment =  getIntent().getIntExtra("comingFromPayment", 0);
            goBack = getIntent().getIntExtra(Constants.KEY_GO_BACK, 0);
            if(goBack == 1 && comingFromPayment == 0 && prefManager.isFirstTimeLaunch()){
                Intent intent = new Intent(MainActivity.this, PayTutorial.class);
                intent.putExtra(Constants.KEY_GO_BACK, 1);
                intent.putExtra("comingFromPayment", 1);

                startActivity(intent);
                finish();
                return;
            }

            new ASSL(this, 1134, 720, false);
            menuBar = new MenuBar(this, drawer);
//            ASSL.DoMagic(menuBar.menuLayout);

            imageButtonBack = (ImageButton) findViewById(R.id.back_btn);
            imageButtonBack.setImageResource(R.drawable.icon_menu);
            textViewToolbarTitle = (TextView) findViewById(R.id.toolbar_title); textViewToolbarTitle.setTypeface(Fonts.avenirNext(this));
            textViewToolbarTitle.setText(R.string.pay);
            toolbarDivider = (ImageView) findViewById(R.id.toolbarDivider);
            toolbarDivider.setVisibility(View.GONE);
            ivToolbarSetting = (ImageView) findViewById(R.id.ivToolbarSetting);
            ivToolbarSetting.setVisibility(View.VISIBLE);
            imageViewSharePaymentId = (ImageView) findViewById(R.id.imageViewSharePaymentId);


            float marginBottom = 60f;
            fabViewTest = new FABViewTest(this, findViewById(R.id.relativeLayoutFABTest));
            fabViewTest.setMenuLabelsRightTestPadding(marginBottom);

            if(Prefs.with(this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1 &&
                    Data.userData.getIntegratedJugnooEnabled() == 1) {
                    fabViewTest.setRelativeLayoutFABTestVisibility(View.VISIBLE);
                    fabViewTest.setFABButtons(false);
            } else{
                fabViewTest.setRelativeLayoutFABTestVisibility(View.GONE);
            }


            transactionHistories = new ArrayList<>();
            recyclerViewPendingPayments = (RecyclerView) findViewById(R.id.recyclerViewPendingPayments);
            recyclerViewPendingPayments.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewPendingPayments.setItemAnimator(new DefaultItemAnimator());
            recyclerViewPendingPayments.setHasFixedSize(false);
            pendingTrnscAdapater = new PendingTrnscAdapater(this, transactionHistories,
                    new PendingTrnscAdapater.EventHandler() {
                @Override
                public void refreshTransactions() {
                    apiFetchPayData();
                }
            });
            recyclerViewPendingPayments.setAdapter(pendingTrnscAdapater);

            ((TextView) findViewById(R.id.textViewPaymentId)).setTypeface(Fonts.mavenMedium(this));
            textViewPaymentIdValue = (TextView) findViewById(R.id.textViewPaymentIdValue); textViewPaymentIdValue.setTypeface(Fonts.mavenMedium(this));
            ((TextView) findViewById(R.id.textViewSendMoney)).setTypeface(Fonts.mavenMedium(this));
            ((TextView) findViewById(R.id.textViewRequestMoney)).setTypeface(Fonts.mavenMedium(this));
            ((TextView) findViewById(R.id.textViewPendingPayments)).setTypeface(Fonts.mavenMedium(this));
            ((TextView) findViewById(R.id.textViewNoPayments)).setTypeface(Fonts.mavenMedium(this));
            relativeLayoutNoPayments = (RelativeLayout) findViewById(R.id.relativeLayoutNoPayments);
            relativeLayoutNoPayments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apiFetchPayData();
                }
            });

            textViewPaymentIdValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(vpa.equalsIgnoreCase(Constants.KEY_ERROR)){
                        sendToSDKRegister(Data.getPayData().getPay());
                    }
                }
            });

            ivToolbarSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showListMenu(ivToolbarSetting);
                }
            });

            imageViewSharePaymentId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ReferralActions.genericShareDialog(MainActivity.this, callbackManager,
								getString(R.string.jugnoo_pay), fetchPayDataResponse.getShareButtonText(), "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            LocalBroadcastManager.getInstance(this).registerReceiver(localBroadcastReceiver,
                    new IntentFilter(Constants.INTENT_ACTION_PAY_BROADCAST));

        } catch (Exception e) {
            e.printStackTrace();
        }

        firstTimeApi();
    }

    public LatLng getSelectedLatLng() {
        if(selectedLatLng != null){
            return selectedLatLng;
        } else {
            return new LatLng(Data.latitude, Data.longitude);
        }
    }

    public void setSelectedLatLng(LatLng selectedLatLng) {
        this.selectedLatLng = selectedLatLng;
    }

    public LatLng getCurrentPlaceLatLng() {
        return getSelectedLatLng();
    }


    private void firstTimeApi(){
        try {
            if(Data.getPayData().getPay().getHasVpa() == 0){
                sendToSDKRegister(Data.getPayData().getPay());
            } else {
                apiFetchPayData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == VPA_REGISTER_INTENT_REQUEST_CODE
                    && resultCode == Activity.RESULT_OK
                    && data != null) {
                Bundle bundle = data.getExtras();
                String pgMeTrnRefNo = bundle.getString("pgMeTrnRefNo");
                String yblRefId = bundle.getString("yblRefId");
                String virtualAddress = bundle.getString("virtualAddress");
                String status = bundle.getString("status");
                String statusdesc = bundle.getString("statusdesc");
                String registrationDate = bundle.getString("registrationDate");

                String AccountNo = bundle.getString("accountNo");
                String ifsc = bundle.getString("ifsc");
                String accName = bundle.getString("accName");

                System.out.println("virtual address== " + virtualAddress + " date=== " + registrationDate + "  ybl== " + yblRefId + "  pgM== " + pgMeTrnRefNo + " status== " + status + "  dsc ==" + statusdesc + " AccountNo == " + AccountNo + " ifsc == " + ifsc + " accName == " + accName);

                VerifyRegisterResponse verifyRegisterResponse = new VerifyRegisterResponse();
                verifyRegisterResponse.setPgMeTrnRefNo(pgMeTrnRefNo);
                verifyRegisterResponse.setYblRefId(yblRefId);
                verifyRegisterResponse.setVirtualAddress(virtualAddress);
                verifyRegisterResponse.setStatus(status);
                verifyRegisterResponse.setStatusdesc(statusdesc);
                verifyRegisterResponse.setRegistrationDate(registrationDate);

                verifyRegisterResponse.setAccountNo(AccountNo);
                verifyRegisterResponse.setIfsc(ifsc);
                verifyRegisterResponse.setAccName(accName);

                if (virtualAddress.length() > 0) {
                    callVerifyUserApi(verifyRegisterResponse);
                } else {
                    Utils.setTextUnderline(textViewPaymentIdValue, getString(R.string.generate_vpa));
                    vpa = Constants.KEY_ERROR;
                }
            } else if (requestCode == CHANGE_MPIN_INTENT_REQUEST_CODE
                    && resultCode == Activity.RESULT_OK
                    && data != null) {
                Bundle bundle= data.getExtras();

                String pgMeTrnRefNo = bundle.getString("pgMeTrnRefNo");
                String yblRefId = bundle.getString("yblRefId");
                String virtualAddress = bundle.getString("virtualAddress");
                String status= bundle.getString("status");
                String statusdesc = bundle.getString("statusdesc");
                String date = bundle.getString("date");

                // new code - added on 21-11-2016
                String accountNo = bundle.getString("accountNo");
                String ifsc = bundle.getString("ifsc");
                String accName = bundle.getString("accName");
                //----------------------

                String add1 = bundle.getString("add1");
                String add2 = bundle.getString("add2");
                String add3 = bundle.getString("add3");
                String add4 = bundle.getString("add4");
                String add5 = bundle.getString("add5");
                String add6 = bundle.getString("add6");
                String add7 = bundle.getString("add7");
                String add8 = bundle.getString("add8");
                String add9 = bundle.getString("add9");
                String add10 = bundle.getString("add10");

                Log.v("pgMeTrnRefNo== ", pgMeTrnRefNo);
                Log.v("yblRefId== ", yblRefId);
                Log.v("virtualAddress== ", virtualAddress);
                Log.v("status== ", status);
                Log.v("statusdesc== ", statusdesc);
                Log.v("date== ", date);

                SetMPINResponse setMPINResponse = new SetMPINResponse();
                setMPINResponse.setPgMeTrnRefNo(pgMeTrnRefNo);
                setMPINResponse.setYblRefId(yblRefId);
                setMPINResponse.setVirtualAddress(virtualAddress);
                setMPINResponse.setStatus(status);
                setMPINResponse.setStatusdesc(statusdesc);
                setMPINResponse.setDate(date);

                // new code - added on 21-11-2016
                setMPINResponse.setAccountNo(accountNo);
                setMPINResponse.setIfsc(ifsc);
                setMPINResponse.setAccName(accName);
                //--------------------

                changeMPINCallbackApi(setMPINResponse);
            } else if (requestCode == MANAGE_ACCOUNT_INTENT_REQUEST_CODE
                    && resultCode == Activity.RESULT_OK
                    && data != null) {
                Bundle bundle= data.getExtras();
                String pgMeTrnRefNo = bundle.getString("pgMeTrnRefNo");
                String yblRefId = bundle.getString("yblRefId");
                String virtualAddress = bundle.getString("virtualAddress");
                String status= bundle.getString("status");
                String statusdesc = bundle.getString("statusdesc");
                String date = bundle.getString("date");
                String AccountNo = bundle.getString("accountNo");
                String ifsc = bundle.getString("ifsc");
                String accName = bundle.getString("accName");
                AccountMgmtCallbackRequest accountManagementResponse = new AccountMgmtCallbackRequest(pgMeTrnRefNo, yblRefId, virtualAddress, status, statusdesc, date, AccountNo, ifsc, accName);
                apiAccountManagementCallback(accountManagementResponse);
            }
            else{
                if(data == null){
                    Log.e("call failed","call failed");
                }
            }
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            menuBar.setUserData();
            try {
                if(Data.getPayData().getPay().getHasVpa() == 1){
					apiFetchPayData();
				}
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendToSDKRegister(LoginResponse.Pay pay){
        try {
            Bundle bundle = new Bundle();
            bundle.putString("mid", pay.getMid());
            bundle.putString("merchantKey", pay.getMkey());
            bundle.putString("merchantTxnID", pay.getToken());
            Log.i("sendToSDKRegister", "TOKEN IS : " + pay.getToken());
            bundle.putString("appName", "jugnooApp");
            Intent intent = new Intent(getApplicationContext(), Registration.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, VPA_REGISTER_INTENT_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void callVerifyUserApi(final VerifyRegisterResponse verifyRegisterResponse) {
        if(MyApplication.getInstance().isOnline()) {
            CallProgressWheel.showLoadingDialog(this, getString(R.string.please_wait));
            VerifyUserRequest request = new VerifyUserRequest();
            request.setDeviceToken(MyApplication.getInstance().getDeviceToken());
            request.setUniqueDeviceId(CommonMethods.getUniqueDeviceId(this));
            request.setToken(Data.getPayData().getPay().getToken());
            request.setVpa(verifyRegisterResponse.getVirtualAddress());
            request.setAccess_token(Data.userData.accessToken);
            request.setAutos_user_id(Data.userData.getUserId());
            request.setUserEmail(Data.userData.userEmail);
            request.setUser_name(Data.userData.userName);
            request.setPhone_no(Data.userData.phoneNo);
            request.setMessage(verifyRegisterResponse.toString());

            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_DEVICE_TOKEN, MyApplication.getInstance().getDeviceToken());
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put("autos_user_id", Data.userData.getUserId());
            params.put(Constants.KEY_LATITUDE, String.valueOf(Data.latitude));
            params.put(Constants.KEY_LONGITUDE, String.valueOf(Data.longitude));
            params.put(Constants.KEY_MESSAGE, verifyRegisterResponse.toString());
            params.put(Constants.KEY_PHONE_NO, Data.userData.phoneNo);
            params.put("token", Data.getPayData().getPay().getToken());
            params.put(Constants.KEY_UNIQUE_DEVICE_ID, CommonMethods.getUniqueDeviceId(this));
            params.put("user_email", Data.userData.userEmail);
            params.put(Constants.KEY_USER_NAME, Data.userData.userName);
            params.put(Constants.KEY_VPA, verifyRegisterResponse.getVirtualAddress());

            new HomeUtil().putDefaultParams(params);
            RestClient.getPayApiService().verifyUser(params, new Callback<FetchPayDataResponse>() {
                @Override
                public void success(FetchPayDataResponse fetchPayDataResponse, Response response) {
                    CallProgressWheel.dismissLoadingDialog();
                    try {
                        int flag = fetchPayDataResponse.getFlag();
                        if (flag == ApiResponseFlags.AUTH_REGISTRATION_SUCCESSFUL.getOrdinal()) {
                            updateTransactions(fetchPayDataResponse);
                            Data.getPayData().getPay().setHasVpa(1);

                            // set First time launch false in prefManager
                            // prefManager.setFirstTimeLaunch(false);
                            if(goBack == 1){
                                finish();
                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            }

                        } else if (flag == ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal()) {
//                            logoutFunc(MainActivity.this, tokenGeneratedResponse.getMessage());
                        }
                        else if(flag == com.jugnoo.pay.utils.ApiResponseFlags.VPA_NOT_FOUND.getOrdinal())
                        {
                            DialogPopup.alertPopupWithListener(MainActivity.this, getString(R.string.error), fetchPayDataResponse.getMessage(), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MyApplication.getInstance().getAppSwitcher().switchApp(MainActivity.this, Config.getAutosClientId(), new LatLng(Data.latitude, Data.longitude), false);
                                }
                            });
                        }
                        else {
                            retryDialogVerifyUser(DialogErrorType.SERVER_ERROR, verifyRegisterResponse);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        retryDialogVerifyUser(DialogErrorType.SERVER_ERROR, verifyRegisterResponse);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    CallProgressWheel.dismissLoadingDialog();
                    retryDialogVerifyUser(DialogErrorType.CONNECTION_LOST, verifyRegisterResponse);
                }
            });
        } else {
            retryDialogVerifyUser(DialogErrorType.NO_NET, verifyRegisterResponse);
        }

    }



    public void apiFetchPayData() {
        try {
            if (MyApplication.getInstance().isOnline()) {
                CallProgressWheel.showLoadingDialog(this, getString(R.string.loading));
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
                params.put(Constants.KEY_DEVICE_TOKEN, MyApplication.getInstance().getDeviceToken());

                new HomeUtil().putDefaultParams(params);
                RestClient.getPayApiService().fetchPayData(params, new Callback<FetchPayDataResponse>() {
                    @Override
                    public void success(FetchPayDataResponse fetchPayDataResponse, Response response) {
                        CallProgressWheel.dismissLoadingDialog();
                        try{
                            if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == fetchPayDataResponse.getFlag()){
                                updateTransactions(fetchPayDataResponse);

                                // set isFirstTimeAppLaunch to false
                                PrefManager prefManager = new PrefManager(MainActivity.this);
                                prefManager.setFirstTimeLaunch(false);

                            } else {
                                DialogPopup.alertPopup(MainActivity.this, "", fetchPayDataResponse.getMessage());
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            retryDialogFetchPayData(DialogErrorType.SERVER_ERROR);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        CallProgressWheel.dismissLoadingDialog();
                        retryDialogFetchPayData(DialogErrorType.CONNECTION_LOST);
                    }
                });
            } else {
                retryDialogFetchPayData(DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            DialogPopup.dismissLoadingDialog();
            e.printStackTrace();
        }

    }

    private void updateTransactions(FetchPayDataResponse fetchPayDataResponse){
        this.fetchPayDataResponse = fetchPayDataResponse;
        vpa = fetchPayDataResponse.getVpa();
        Utils.setTextUnderline(textViewPaymentIdValue, fetchPayDataResponse.getVpa());
        transactionHistories.clear();
        for(TransacHistoryResponse.TransactionHistory transactionHistory : fetchPayDataResponse.getTransaction()){
            if(transactionHistory.getTxnType() == TransacHistoryResponse.Type.REQUESTED_FROM_PENDING.getOrdinal()
                    || transactionHistory.getTxnType() == TransacHistoryResponse.Type.REQUEST_BY_PENDING.getOrdinal()){
                transactionHistories.add(transactionHistory);
            }
        }
        pendingTrnscAdapater.notifyDataSetChanged();
        relativeLayoutNoPayments.setVisibility((transactionHistories.size() > 0) ? View.GONE : View.VISIBLE);
        imageViewSharePaymentId.setVisibility(TextUtils.isEmpty(fetchPayDataResponse.getShareButtonText()) ? View.GONE : View.VISIBLE);
    }

    private void retryDialogFetchPayData(DialogErrorType dialogErrorType){
        DialogPopup.dialogNoInternet(this,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        apiFetchPayData();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }


    private void retryDialogVerifyUser(DialogErrorType dialogErrorType, final VerifyRegisterResponse verifyRegisterResponse){
        DialogPopup.dialogNoInternet(this,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        callVerifyUserApi(verifyRegisterResponse);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }

    private BroadcastReceiver localBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int flag = intent.getIntExtra(Constants.KEY_FLAG, -1);
                    if(flag == PushFlags.REFRESH_PAY_DATA.getOrdinal()){
                        apiFetchPayData();
                    }
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastReceiver);
        super.onDestroy();
    }

    ListPopupWindow popupWindow;
    boolean popupShowing = false;

    private void showListMenu(final View anchor) {
        if(popupWindow == null) {
            List<HashMap<String, String>> data = new ArrayList<>();
            HashMap<String, String> map = new HashMap<>();
            String TITLE = "title";
            map.put(TITLE, getString(R.string.change_mpin));
            data.add(map);
            map = new HashMap<>();
            map.put(TITLE, getString(R.string.reset_pay_account));
            data.add(map);
            map = new HashMap<>();
            map.put(TITLE, getString(R.string.account_management));
            data.add(map);
            popupWindow = new ListPopupWindow(this);
            ListAdapter adapter = new SimpleAdapter(
                    this,
                    data,
                    R.layout.list_item_popup_menu,
                    new String[]{TITLE}, // These are just the keys that the data uses (constant strings)
                    new int[]{R.id.textView}); // The view ids to map the data to

            popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_white_bordered));
            popupWindow.setAnchorView(anchor);
            popupWindow.setAdapter(adapter);
            popupWindow.setWidth(Utils.dpToPx(this, 200));
            popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            apiChangeMPIN();
                            break;
                        case 1:
                            DialogPopup.alertPopupTwoButtonsWithListeners(MainActivity.this, "",
                                    getString(R.string.reset_account_alert_message),
                                    getString(R.string.ok),
                                    getString(R.string.cancel),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            apiResetAccount();
                                        }
                                    },
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }, false, false);
                            break;
                        case 2:
                            apiAccountManagement();
                            break;
                        default:
                            break;
                    }
                    popupWindow.dismiss();
                    popupShowing = false;
                }
            });
            popupShowing = false;
//            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                @Override
//                public void onDismiss() {
//                    popupShowing = false;
//                }
//            });
        }
        if(popupShowing){
            popupWindow.dismiss();
            popupShowing = false;
        } else {
            popupWindow.show();
            popupShowing = true;
        }
    }


    private void apiChangeMPIN() {
        try {
            if (MyApplication.getInstance().isOnline()) {
				CallProgressWheel.showLoadingDialog(this, getString(R.string.loading));
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

                new HomeUtil().putDefaultParams(params);
				RestClient.getPayApiService().setMPIN(params, new Callback<SendMoneyResponse>() {
					@Override
					public void success(SendMoneyResponse sendMoneyResponse, Response response) {
						CallProgressWheel.dismissLoadingDialog();
						try {
							int flag = sendMoneyResponse.getFlag();
							if (flag == com.jugnoo.pay.utils.ApiResponseFlags.TXN_INITIATED.getOrdinal()) {
								callBankSetMPINApi(sendMoneyResponse.getTxnDetails());
							} else {
								DialogPopup.alertPopup(MainActivity.this, "", sendMoneyResponse.getMessage());
							}
						} catch (Exception e) {
							e.printStackTrace();
							retryDialogChangeMPIN(DialogErrorType.SERVER_ERROR);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						CallProgressWheel.dismissLoadingDialog();
						retryDialogChangeMPIN(DialogErrorType.CONNECTION_LOST);
					}
				});
			} else {
				retryDialogChangeMPIN(DialogErrorType.NO_NET);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialogChangeMPIN(DialogErrorType dialogErrorType){
        DialogPopup.dialogNoInternet(this,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        apiChangeMPIN();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }

    void callBankSetMPINApi(SendMoneyResponse.TxnDetails txnDetails) {
        Bundle bundle = new Bundle();
        bundle.putString("mid", txnDetails.getMid());
        bundle.putString("merchantKey", txnDetails.getMkey());// b0222ce704ebc0c1f4dc24360751f9f6
        bundle.putString("merchantTxnID", Integer.toString(txnDetails.getOrderId())); // 11
        bundle.putString("virtualAddress", txnDetails.getVpa()); // P2P

        bundle.putString("add1", "");
        bundle.putString("add2", "");
        bundle.putString("add3", "");
        bundle.putString("add4", "");
        bundle.putString("add5", "");
        bundle.putString("add6", "");
        bundle.putString("add7", "");
        bundle.putString("add8", "");
        bundle.putString("add9", "NA");
        bundle.putString("add10", "NA");

        Log.v("mid == ", txnDetails.getMid());
        Log.v("merchantKey == ", txnDetails.getMkey());
        Log.v("merchantTxnID== ", Integer.toString(txnDetails.getOrderId()));
        Log.v("virtualAddress== ", txnDetails.getVpa());

        Intent intent = new Intent(getApplicationContext(), SetMpin.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, CHANGE_MPIN_INTENT_REQUEST_CODE);
    }

    private void changeMPINCallbackApi(SetMPINResponse setMPINResponse) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN,  Data.userData.accessToken);
            params.put(Constants.KEY_MESSAGE, setMPINResponse.toString());

            new HomeUtil().putDefaultParams(params);
            RestClient.getPayApiService().setMPINCallback(params, new Callback<CommonResponse>() {
				@Override
				public void success(CommonResponse commonResponse, Response response) {
					try {
						int flag = commonResponse.getFlag();
						if (flag == com.jugnoo.pay.utils.ApiResponseFlags.TXN_INITIATED.getOrdinal()) {
							//callBankSetMPINApi(sendMoneyResponse.getTxnDetails());

						} else {
							DialogPopup.alertPopup(MainActivity.this, "", commonResponse.getMessage());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void failure(RetrofitError error) {
				}
			});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apiResetAccount() {
        try {
            if (MyApplication.getInstance().isOnline()) {
				CallProgressWheel.showLoadingDialog(this, getString(R.string.loading));
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

                new HomeUtil().putDefaultParams(params);
				RestClient.getPayApiService().resetAccount(params, new Callback<SettleUserDebt>() {
					@Override
					public void success(SettleUserDebt settleUserDebt, Response response) {
						CallProgressWheel.dismissLoadingDialog();
						try {
							int flag = settleUserDebt.getFlag();
							if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                new HomeUtil().logoutFunc(MainActivity.this, null);
							} else {
								DialogPopup.alertPopup(MainActivity.this, "", settleUserDebt.getMessage());
							}
						} catch (Exception e) {
							e.printStackTrace();
							retryDialogResetAccount(DialogErrorType.SERVER_ERROR);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						CallProgressWheel.dismissLoadingDialog();
						retryDialogResetAccount(DialogErrorType.CONNECTION_LOST);
					}
				});
			} else {
				retryDialogResetAccount(DialogErrorType.NO_NET);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialogResetAccount(DialogErrorType dialogErrorType){
        DialogPopup.dialogNoInternet(this,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        apiResetAccount();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }





    private void apiAccountManagement(){
        try {
            if (MyApplication.getInstance().isOnline()) {
                CallProgressWheel.showLoadingDialog(MainActivity.this, getString(R.string.loading));
                HashMap<String, String> params = new HashMap<>();

                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);

                new HomeUtil().putDefaultParams(params);
                RestClient.getPayApiService().accountManagement(params, new Callback<AccountManagementResponse>() {
                    @Override
                    public void success(AccountManagementResponse accountManagementResponse, Response response) {
                        CallProgressWheel.dismissLoadingDialog();
                        try {
                            int flag = accountManagementResponse.getFlag();
                            if (flag == 143) {
                                String mid = accountManagementResponse.getMid();
                                String mkey = accountManagementResponse.getMkey();
                                String merchantTxnID = accountManagementResponse.getToken();
                                String vpa = accountManagementResponse.getVpa();

                                Bundle bundle = new Bundle();
                                bundle.putString("mid", mid);
                                bundle.putString("merchantKey", mkey);
                                bundle.putString("merchantTxnID", merchantTxnID);
                                bundle.putString("virtualAddress", vpa);     // vijay27@yesb
                                Intent intent = new Intent(getApplicationContext(), AddAccount.class);
                                intent.putExtras(bundle);
                                startActivityForResult(intent, MANAGE_ACCOUNT_INTENT_REQUEST_CODE);
                            } else {
                                DialogPopup.alertPopup(MainActivity.this, "", accountManagementResponse.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            retryDialogAccountManagement(DialogErrorType.SERVER_ERROR);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        CallProgressWheel.dismissLoadingDialog();
                        retryDialogAccountManagement(DialogErrorType.CONNECTION_LOST);
                    }
                });
            } else {
                retryDialogAccountManagement(DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialogAccountManagement(DialogErrorType dialogErrorType){
        DialogPopup.dialogNoInternet(this,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        apiAccountManagement();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }

    private void apiAccountManagementCallback(AccountMgmtCallbackRequest request) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN,  Data.userData.accessToken);
            params.put(Constants.KEY_MESSAGE, request.toString());

            new HomeUtil().putDefaultParams(params);
            RestClient.getPayApiService().accountManagementCallback(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt commonResponse, Response response) {
                    try {
                        int flag = commonResponse.getFlag();
                        if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                            //callBankSetMPINApi(sendMoneyResponse.getTxnDetails());

                        } else {
                            // DialogPopup.alertPopup(MainActivity.this, "", commonResponse.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
