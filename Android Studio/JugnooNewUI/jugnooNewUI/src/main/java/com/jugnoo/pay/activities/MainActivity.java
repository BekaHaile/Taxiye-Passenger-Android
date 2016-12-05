package com.jugnoo.pay.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jugnoo.pay.adapters.CustomDrawerAdapter;
import com.jugnoo.pay.models.AccountManagementResponse;
import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.models.VerifyRegisterResponse;
import com.jugnoo.pay.models.VerifyUserRequest;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.SharedPreferencesName;
import com.jugnoo.pay.utils.SingleButtonAlert;
import com.sabkuchfresh.utils.AppConstant;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.yesbank.AddAccount;
import com.yesbank.Registration;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.FABViewTest;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.LoginResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Prefs;
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
    @OnClick(R.id.send_money_image)
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



    @OnClick(R.id.request_money_image)
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

    @OnClick(R.id.profile_layout)
    void profileLayoutClicked() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @OnClick(R.id.textViewAbout)
    void textViewAboutClicked() {
        CommonMethods.openUrl(MainActivity.this, userDetails.getSupport_link());
    }

    @Bind(R.id.name_char)
    TextView nameChar;
    @Bind(R.id.name_txt)
    TextView nameTxt;
    @Bind(R.id.phone_txt)
    TextView phnTxt;

    private ListView mDrawerList;
    private ImageView imageViewProfile;
    private CustomDrawerAdapter drawerAdapter;
    private CommonResponse userDetails;
    private String accessToken;
    private String drawerItems[] = {"Home", "Pending Transactions", "Transactions History", "FAQs", "Account Management"};
    Intent intent;

    private FABViewTest fabViewTest;

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
            accessToken = Prefs.with(MainActivity.this).getString(SharedPreferencesName.ACCESS_TOKEN, "");
            userDetails = Prefs.with(MainActivity.this).getObject(SharedPreferencesName.APP_USER, CommonResponse.class);
            mDrawerList = (ListView) findViewById(R.id.lst_menu_items);
            drawerAdapter = new CustomDrawerAdapter(this, android.R.layout.simple_list_item_1, drawerItems);
            imageViewProfile = (ImageView)findViewById(R.id.profile_image);

            mDrawerList.setAdapter(drawerAdapter);

            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

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

            mToolBar.setTitle(R.string.pay);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(Data.getPayData().getPay().getHasVpa() == 0){
				sendToSDKRegister(Data.getPayData().getPay());
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            drawerAdapter.setSelectedPosition(position);
            selectItem(position);
        }
    }

    public void selectItem(int possition) {
        switch (possition) {
            case 0:
                break;
            case 1:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            intent = new Intent(MainActivity.this, TransacHistoryActivity.class);
                            intent.putExtra(AppConstant.PENDING_TRANSACTION_STATUS, true);
                            startActivity(intent);
                            overridePendingTransition(R.anim.right_in, R.anim.right_out);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 200);

                break;
            case 2:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            startActivity(new Intent(MainActivity.this, TransacHistoryActivity.class));
                            overridePendingTransition(R.anim.right_in, R.anim.right_out);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 200);
                break;
            case 3:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            intent = new Intent(MainActivity.this, WebActivity.class);
                            intent.putExtra(AppConstant.URL, Data.getPayData().getPay().getFaqLink());
                            startActivity(intent);
                            overridePendingTransition(R.anim.right_in, R.anim.right_out);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 200);
                break;
            case 4:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            accountManagement();
                        } catch (Exception e) {
                            System.out.println("exception");
                        }
                    }
                }, 200);
                break;
        }

        mDrawerList.setItemChecked(possition, true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        drawerAdapter.notifyDataSetChanged();
        mDrawerList.invalidateViews();
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
            nameTxt.setText(Data.userData.userName);
            phnTxt.setText(Data.userData.phoneNo);
            nameChar.setText(Data.userData.userName.substring(0, 1).toUpperCase());
            if (!"".equalsIgnoreCase(Data.userData.userImage)) {
                Picasso.with(MainActivity.this).load(Data.userData.userImage).transform(new CircleTransform())
                        .into(imageViewProfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        drawerAdapter.setSelectedPosition(-1);
        drawerAdapter.notifyDataSetChanged();
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
        CallProgressWheel.showLoadingDialog(this, AppConstant.PLEASE);
        VerifyUserRequest request = new VerifyUserRequest();
        request.setDeviceToken(MyApplication.getInstance().getDeviceToken());
        request.setUniqueDeviceId(CommonMethods.getUniqueDeviceId(this));
        request.setToken(accessToken);
        request.setVpa(verifyRegisterResponse.getVirtualAddress());
        request.setDeviceType("0");
        request.setPhone_no(Data.userData.phoneNo);
        request.setMessage(verifyRegisterResponse.toString());

        RestClient.getPayApiService().verifyUser(request, new Callback<CommonResponse>() {
            @Override
            public void success(CommonResponse tokenGeneratedResponse, Response response) {
                CallProgressWheel.dismissLoadingDialog();
                if (tokenGeneratedResponse != null) {
                    int flag = tokenGeneratedResponse.getFlag();
                    if (flag == 401) {
//                        accessTokenLogin(MainActivity.this);
                    } else if(flag == 403) {
//                        logoutFunc(MainActivity.this, tokenGeneratedResponse.getMessage());
                    } else {
                        CommonMethods.callingBadToken(MainActivity.this, flag, tokenGeneratedResponse.getMessage());
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


    }

}
