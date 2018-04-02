package com.jugnoo.pay.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jugnoo.pay.models.AccountManagementResponse;
import com.jugnoo.pay.utils.CallProgressWheel;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JSONParser;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ankit on 06/12/16.
 */

public class AddPaymentAddressActivity extends BaseActivity {

    private Toolbar mToolBar;
    private TextView toolbarTitleTxt, tvTitle;
    private ImageButton ibBack;
    private ImageView ivTBDivider, ivContacts;
    private final int RQS_PICK_CONTACT = 1;
    private EditText etName, etPaymentAddress;
    private Button bAddPaymentAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_address);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitleTxt = (TextView) findViewById(R.id.toolbar_title); toolbarTitleTxt.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
        ibBack = (ImageButton) findViewById(R.id.back_btn);
        ibBack.setImageResource(R.drawable.ic_close_pay);
        ivTBDivider = (ImageView) findViewById(R.id.toolbarDivider);
        ivTBDivider.setVisibility(View.GONE);
        toolbarTitleTxt.setText(R.string.add_payment_address);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        ivContacts = (ImageView) findViewById(R.id.ivContacts);
        etName = (EditText) findViewById(R.id.etName); etName.setTypeface(Fonts.mavenRegular(this));
        etPaymentAddress = (EditText) findViewById(R.id.etPaymentAddress); etPaymentAddress.setTypeface(Fonts.mavenRegular(this));
        bAddPaymentAddress = (Button) findViewById(R.id.bAddPaymentAddress); bAddPaymentAddress.setTypeface(Fonts.avenirNext(this));
        tvTitle = (TextView) findViewById(R.id.tvTitle); tvTitle.setTypeface(Fonts.mavenRegular(this));

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                //intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, RQS_PICK_CONTACT);
            }
        });

        bAddPaymentAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String vpa = etPaymentAddress.getText().toString().trim();
                if(name.length() == 0){
                    Utils.showToast(AddPaymentAddressActivity.this, getString(R.string.please_enter_name));
                } else if(vpa.length() == 0){
                    Utils.showToast(AddPaymentAddressActivity.this, getString(R.string.please_enter_vpa));
                } else if(!Utils.isVPAValid(vpa)){
                    Utils.showToast(AddPaymentAddressActivity.this, getString(R.string.please_enter_valid_vpa));
                } else {
                    apiAddPaymentAddress();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (RQS_PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            //cNumber = phones.getString(phones.getColumnIndex("data1"));
                            //System.out.println("number is:"+cNumber);
                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        etName.setText(name);

                    }
                }
                break;
        }
    }

    public void apiAddPaymentAddress() {
        try {
            if (MyApplication.getInstance().isOnline()) {
                CallProgressWheel.showLoadingDialog(this, getString(R.string.loading));
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_USER_NAME, etName.getText().toString());
                params.put(Constants.KEY_VPA, etPaymentAddress.getText().toString());

                new HomeUtil().putDefaultParams(params);
                RestClient.getPayApiService().addPaymentAddress(params, new Callback<AccountManagementResponse>() {
                    @Override
                    public void success(AccountManagementResponse accountManagementResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        CallProgressWheel.dismissLoadingDialog();
                        try{
                            JSONObject jObj = new JSONObject(responseStr);
                            int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
                            String message = JSONParser.getServerMessage(jObj);
                            if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
                                onBackPressed();
                            } else {
                                DialogPopup.alertPopup(AddPaymentAddressActivity.this, "", message);
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

    private void retryDialogFetchPayData(DialogErrorType dialogErrorType){
        DialogPopup.dialogNoInternet(this,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        apiAddPaymentAddress();
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

