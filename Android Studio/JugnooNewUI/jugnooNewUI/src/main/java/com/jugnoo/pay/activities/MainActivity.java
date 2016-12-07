package com.jugnoo.pay.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jugnoo.pay.adapters.PendingTrnscAdapater;
import com.jugnoo.pay.models.AccountManagementResponse;
import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.models.FetchPayDataResponse;
import com.jugnoo.pay.models.TransacHistoryResponse;
import com.jugnoo.pay.models.VerifyRegisterResponse;
import com.jugnoo.pay.models.VerifyUserRequest;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.SharedPreferencesName;
import com.jugnoo.pay.utils.SingleButtonAlert;
import com.sabkuchfresh.utils.AppConstant;
import com.yesbank.AddAccount;
import com.yesbank.Registration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.FABViewTest;
import product.clicklabs.jugnoo.home.MenuBar;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class MainActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @OnClick(R.id.back_btn)
    void backBtnClicked() {
        drawer.openDrawer(GravityCompat.START);
    }

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private final int VPA_REGISTER_INTENT_REQUEST_CODE = 121;

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
                    SingleButtonAlert.showAlertGps(MainActivity.this, "Please make sure you've granted the permission to read Contacts", "OK", new SingleButtonAlert.OnAlertOkClickListener() {
                        @Override
                        public void onOkButtonClicked() {
                        }
                    });
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

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    void profileLayoutClicked() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

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

            toggle.syncState();
            userDetails = Prefs.with(MainActivity.this).getObject(SharedPreferencesName.APP_USER, CommonResponse.class);

            menuBar = new MenuBar(this, drawer);

            float marginBottom = 77f;
            float scale = getResources().getDisplayMetrics().density;
            fabViewTest = new FABViewTest(this, findViewById(R.id.relativeLayoutFABTest));
            int dpAsPixels = (int) (marginBottom*scale + 0.5f);
            fabViewTest.menuLabelsRightTest.setPadding((int) (40f * ASSL.Yscale()), 0, 0, dpAsPixels);

            if(Prefs.with(this).getInt(Constants.FAB_ENABLED_BY_USER, 1) == 1 &&
                    Data.userData.getIntegratedJugnooEnabled() == 1) {
                    fabViewTest.relativeLayoutFABTest.setVisibility(View.VISIBLE);
                    fabViewTest.setFABButtons();
            } else{
                fabViewTest.relativeLayoutFABTest.setVisibility(View.GONE);
            }


            transactionHistories = new ArrayList<>();
            recyclerViewPendingPayments = (RecyclerView) findViewById(R.id.recyclerViewPendingPayments);
            recyclerViewPendingPayments.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewPendingPayments.setItemAnimator(new DefaultItemAnimator());
            recyclerViewPendingPayments.setHasFixedSize(false);
            pendingTrnscAdapater = new PendingTrnscAdapater(this, transactionHistories);
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


        } catch (Exception e) {
            e.printStackTrace();
        }

        firstTimeApi();
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


    private void accountManagement(){
        CallProgressWheel.showLoadingDialog(MainActivity.this, "Please wait..");
        HashMap<String, String> params = new HashMap<>();

        params.put("access_token",  Data.userData.accessToken);

        RestClient.getPayApiService().accountManagement(params, new Callback<AccountManagementResponse>() {
            @Override
            public void success(AccountManagementResponse accountManagementResponse, Response response) {
                CallProgressWheel.dismissLoadingDialog();
                if(accountManagementResponse != null) {
                    int flag = accountManagementResponse.getFlag();
                    if(flag == 143) {
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
                        startActivityForResult(intent, 1);
                    }
                    else {
                        System.out.println(flag);
                    }
                } else {
                    System.out.println("response object is null");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("failure..");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == VPA_REGISTER_INTENT_REQUEST_CODE
                    && resultCode == Activity.RESULT_OK && data != null) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            menuBar.setUserData();
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


    private void callVerifyUserApi(VerifyRegisterResponse verifyRegisterResponse) {
        if(AppStatus.getInstance(this).isOnline(this)) {
            CallProgressWheel.showLoadingDialog(this, AppConstant.PLEASE);
            VerifyUserRequest request = new VerifyUserRequest();
            request.setDeviceToken(MyApplication.getInstance().getDeviceToken());
            request.setUniqueDeviceId(CommonMethods.getUniqueDeviceId(this));
            request.setToken(Data.getPayData().getPay().getToken());
            request.setVpa(verifyRegisterResponse.getVirtualAddress());
            request.setDeviceType(Data.DEVICE_TYPE);
            request.setAccess_token(Data.userData.accessToken);
            request.setAutos_user_id(Data.userData.getUserId());
            request.setUserEmail(Data.userData.userEmail);
            request.setUser_name(Data.userData.userName);
            request.setPhone_no(Data.userData.phoneNo);
            request.setMessage(verifyRegisterResponse.toString());

            RestClient.getPayApiService().verifyUser(request, new Callback<FetchPayDataResponse>() {
                @Override
                public void success(FetchPayDataResponse fetchPayDataResponse, Response response) {
                    CallProgressWheel.dismissLoadingDialog();
                    if (fetchPayDataResponse != null) {
                        int flag = fetchPayDataResponse.getFlag();
                        if (flag == 401) {
                            updateTransactions(fetchPayDataResponse);
                        } else if (flag == 403) {
//                        logoutFunc(MainActivity.this, tokenGeneratedResponse.getMessage());

                        } else {
                            CommonMethods.callingBadToken(MainActivity.this, flag, fetchPayDataResponse.getMessage());
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    try {
                        CallProgressWheel.dismissLoadingDialog();
                        if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                            showAlertNoInternet(MainActivity.this);
                        } else {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            JSONObject jsonObject = new JSONObject(json);
                            SingleButtonAlert.showAlert(MainActivity.this, jsonObject.getString("message"), AppConstant.OK);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        CallProgressWheel.dismissLoadingDialog();
                    }
                }
            });
        } else {
            DialogPopup.alertPopup(this, "", getString(R.string.no_net_text));
        }

    }



    public void apiFetchPayData() {
        try {
            if (AppStatus.getInstance(this).isOnline(this)) {
                CallProgressWheel.showLoadingDialog(this, "Loading...");
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
                params.put(Constants.KEY_DEVICE_TYPE, Data.DEVICE_TYPE);

                RestClient.getPayApiService().fetchPayData(params, new Callback<FetchPayDataResponse>() {
                    @Override
                    public void success(FetchPayDataResponse fetchPayDataResponse, Response response) {
                        CallProgressWheel.dismissLoadingDialog();
                        try{
                            if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == fetchPayDataResponse.getFlag()){
                                updateTransactions(fetchPayDataResponse);
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
        vpa = fetchPayDataResponse.getVpa();
        Utils.setTextUnderline(textViewPaymentIdValue, fetchPayDataResponse.getVpa());
        transactionHistories.clear();
        transactionHistories.addAll(fetchPayDataResponse.getTransaction());
        pendingTrnscAdapater.notifyDataSetChanged();
        relativeLayoutNoPayments.setVisibility((transactionHistories.size() > 0) ? View.GONE : View.VISIBLE);
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

}
