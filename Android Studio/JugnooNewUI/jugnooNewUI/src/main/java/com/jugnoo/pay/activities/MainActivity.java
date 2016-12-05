package com.jugnoo.pay.activities;

// <<<<<<< 741d47f103067de25b678aea943c1dfd0feb0a38
// import android.Manifest;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jugnoo.pay.adapters.CustomDrawerAdapter;
import com.jugnoo.pay.models.AccountManagementResponse;
import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.retrofit.RetrofitClient;
import com.jugnoo.pay.retrofit.WebApi;
import com.jugnoo.pay.utils.AppConstants;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.Data;
import com.jugnoo.pay.utils.Prefs;
import com.jugnoo.pay.utils.SharedPreferencesName;
import com.jugnoo.pay.utils.SingleButtonAlert;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.yesbank.AddAccount;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

// =======
// >>>>>>> working on account management API. Account management option added in menu-drawer.
// <<<<<<< 741d47f103067de25b678aea943c1dfd0feb0a38
// =======
// >>>>>>> working on account management API. Account management option added in menu-drawer.

public class MainActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @OnClick(R.id.back_btn)
    void backBtnClicked() {
        drawer.openDrawer(GravityCompat.START);
    }

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private boolean isSendingMoney = true;
    @OnClick(R.id.send_money_image)
// <<<<<<< 741d47f103067de25b678aea943c1dfd0feb0a38
    void sendMoneyImgClicked(){

        if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            isSendingMoney = true;
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.READ_CONTACTS }, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else
        {
            startActivity(new Intent(MainActivity.this,SelectContactActivity.class));
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
    }

    // added on 30-11-2016
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    if(isSendingMoney)
                    {
                        startActivity(new Intent(MainActivity.this,SelectContactActivity.class));
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    }
                    else
                    {
                        Intent intent = new Intent(MainActivity.this,SelectContactActivity.class);
                        intent.putExtra(AppConstants.REQUEST_STATUS,true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    // SingleButtonAlert.showAlert(MainActivity.this, "Permissions", "Allow contact permission to proceed");

                    SingleButtonAlert.showAlertGps(MainActivity.this, "Please make sure you've granted the permission to read Contacts", "OK", new SingleButtonAlert.OnAlertOkClickListener() {
                        @Override
                        public void onOkButtonClicked() {

                        }
                    });

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    @OnClick(R.id.request_money_image)
    void requestMoneyImgClicked()
    {
        if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            isSendingMoney = false;
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { android.Manifest.permission.READ_CONTACTS }, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else
        {
            Intent intent = new Intent(MainActivity.this,SelectContactActivity.class);
            intent.putExtra(AppConstants.REQUEST_STATUS,true);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }

// =======
//    void sendMoneyImgClicked() {
//        startActivity(new Intent(MainActivity.this, SelectContactActivity.class));
//        overridePendingTransition(R.anim.right_in, R.anim.right_out);
//    }

//    @OnClick(R.id.request_money_image)
//    void requestMoneyImgClicked() {
//        Intent intent = new Intent(MainActivity.this, SelectContactActivity.class);
//        intent.putExtra(AppConstants.REQUEST_STATUS, true);
//        startActivity(intent);
//        overridePendingTransition(R.anim.right_in, R.anim.right_out);
//// >>>>>>> working on account management API. Account management option added in menu-drawer.

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

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
        imageViewProfile = (ImageView) findViewById(R.id.profile_image);

        mDrawerList.setAdapter(drawerAdapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

// >>>>>>> working on account management API. Account management option added in menu-drawer.
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
//             TextView itemTxt = (TextView) view
//                    .findViewById(R.id.item_name);
//             itemTxt.setTextColor(getResources().getColor(R.color.signUpBtnBackground));
//            ImageView icon = (ImageView) view.findViewById(R.id.item_left_icon);
            drawerAdapter.setSelectedPosition(position);
            SelectItem(position);

        }
    }

    public void SelectItem(int possition) {

        switch (possition) {
            case 0:
                break;
            case 1:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            intent = new Intent(MainActivity.this, TransacHistoryActivity.class);
                            intent.putExtra(AppConstants.PENDING_TRANSACTION_STATUS, true);
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

                            // edited on 24-11-2016
                            // intent.putExtra(AppConstants.URL, userDetails.getFaqLink().trim());
                            intent.putExtra(AppConstants.URL, Data.userData.getFaq_link());

                            startActivity(intent);
                            overridePendingTransition(R.anim.right_in, R.anim.right_out);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 200);

                //CommonMethods.openUrl(MainActivity.this, userDetails.getFaqLink().trim());
//                startActivity(new Intent(MainActivity.this, TokensActivity.class));
                break;
            case 4:
// <<<<<<< 741d47f103067de25b678aea943c1dfd0feb0a38
// =======

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            accountManagement();
                        }
                        catch (Exception e)
                        {
                            System.out.println("exception");
                        }
                    }
                }, 200);




//                intent = new Intent(MainActivity.this, WebActivity.class);
//                intent.putExtra(AppConstants.URL, userDetails.getSupport_link());
//                startActivity(intent);
//                overridePendingTransition(R.anim.right_in, R.anim.right_out);

                //CommonMethods.openUrl(MainActivity.this, userDetails.getSupport_link());
//                startActivity(new Intent(MainActivity.this, ReferUserActivity.class));
                break;
// >>>>>>> working on account management API. Account management option added in menu-drawer.

//                try
//                {
//                    intent = new Intent(MainActivity.this, WebActivity.class);
//                    intent.putExtra(AppConstants.URL, userDetails.getSupport_link());
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
//                    //CommonMethods.openUrl(MainActivity.this, userDetails.getSupport_link());
////                startActivity(new Intent(MainActivity.this, ReferUserActivity.class));
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }

                // break;
        }

        mDrawerList.setItemChecked(possition, true);
//        setTitle(dataList.get(possition).getItemName());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        drawerAdapter.notifyDataSetChanged();
        mDrawerList.invalidateViews();
    }

    private void accountManagement(){
        CallProgressWheel.showLoadingDialog(MainActivity.this, "Please wait..");
        WebApi mWebApi = RetrofitClient.createService(WebApi.class);
        HashMap<String, String> params = new HashMap<>();

        params.put("access_token",  Data.userData.accessToken);

        mWebApi.accountManagement(params, new Callback<AccountManagementResponse>() {
            @Override
            public void success(AccountManagementResponse accountManagementResponse, Response response) {
                CallProgressWheel.dismissLoadingDialog();
                // System.out.println("accountManagementAPI.success");

                if(accountManagementResponse != null)
                {
                    int flag = accountManagementResponse.getFlag();
                    if(flag == 143)
                    {
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
                    else
                    {
                        System.out.println(flag);
                        // CommonMethods.callingBadToken(MainActivity.this,flag,accountManagementResponse.getMessage());
                    }
                }
                else
                {
                    System.out.println("response object is null");
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                System.out.println("failure..");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try
        {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
                Bundle bundle = data.getExtras();
                String pgMeTrnRefNo = bundle.getString("pgMeTrnRefNo");
                String yblRefId = bundle.getString("yblRefId");
                String virtualAddress = bundle.getString("virtualAddress");
                String status = bundle.getString("status");
                String statusdesc = bundle.getString("statusdesc");
                String date = bundle.getString("date");
                String AccountNo = bundle.getString("accountNo");
                String ifsc = bundle.getString("ifsc");
                String accName = bundle.getString("accName");
            }
        }
        catch (Exception e)
        {
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


}
