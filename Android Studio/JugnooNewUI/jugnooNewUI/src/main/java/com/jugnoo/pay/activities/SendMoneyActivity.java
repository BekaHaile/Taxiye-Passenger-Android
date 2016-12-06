package com.jugnoo.pay.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.models.MessageRequest;
import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.models.SendMoneyCallback;
import com.jugnoo.pay.models.SendMoneyRequest;
import com.jugnoo.pay.models.SendMoneyResponse;
import com.jugnoo.pay.utils.ApiResponseFlags;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.SingleButtonAlert;
import com.jugnoo.pay.utils.Validator;
import com.sabkuchfresh.utils.AppConstant;
import com.squareup.picasso.Picasso;
import com.yesbank.PayActivity;
import com.yesbank.TransactionStatus;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.Fonts;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by cl-macmini-38 on 9/21/16.
 */
public class SendMoneyActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitleTxt;
    @Bind(R.id.back_btn)
    ImageButton backBtn;

    @OnClick(R.id.back_btn)
    void backBtnClicked() {
        onBackPressed();
        startActivity(new Intent(SendMoneyActivity.this,SelectContactActivity.class));
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    @Bind(R.id.contact_name_txt)
    TextView contactNameTxt;
    @Bind(R.id.mobile_txt)
    TextView contactMobileTxt;
    @Bind(R.id.contact_image)
    ImageView contactImage;
    @Bind(R.id.message_et)
    EditText messageET;
    @Bind(R.id.amount_et)
    EditText amountET;
    public static SendMoneyActivity sendMoneyActivityObj;

    private LinearLayout linearLayoutContact;
    private ImageView imageViewBank;
    private TextView textViewAccountNumber, textViewBankName;
    private EditText editTextLocation;
    private RelativeLayout relativeLayoutSendMoney;
    private TextView textViewSendMoney;

    @OnClick(R.id.relativeLayoutSendMoney)
    void sendBtnClicked() {
        if (amountET.getText().toString().length() > 0) {
            int amount = Integer.parseInt(amountET.getText().toString());
            if(amount > 0 && amount <= 100000) {
                if (requestStatus) {
                    callingRequestMoneyApi();
                } else
                    callingSendMoneyApi();
            } else{
                amountET.requestFocus();
                amountET.setHovered(true);
                amountET.setError("Amount should be Rs. 1 to 1,00,000");
            }
        } else {
            amountET.requestFocus();
            amountET.setHovered(true);
            amountET.setError("Please fill your amount");

        }
    }

    private SelectUser contactDetails;
    private boolean requestStatus = false;
    private String accessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
        ButterKnife.bind(this);
        requestStatus = getIntent().getBooleanExtra(AppConstant.REQUEST_STATUS, false);

        relativeLayoutSendMoney = (RelativeLayout) findViewById(R.id.relativeLayoutSendMoney);
        textViewSendMoney = (TextView) findViewById(R.id.textViewSendMoney); textViewSendMoney.setTypeface(Fonts.mavenMedium(this));
        if (requestStatus) {
            toolbarTitleTxt.setText(R.string.request_money_screen);
            textViewSendMoney.setText(getResources().getString(R.string.request));
        }
        else {
            toolbarTitleTxt.setText(R.string.send_money_screen);
            textViewSendMoney.setText(getResources().getString(R.string.send_string));
        }
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        sendMoneyActivityObj = this;
        accessToken = Data.userData.accessToken;              //Prefs.with(SendMoneyActivity.this).getString(SharedPreferencesName.ACCESS_TOKEN, "");
        contactDetails = (SelectUser) getIntent().getExtras().getParcelable(AppConstant.CONTACT_DATA);

        amountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() <= 0){
                    relativeLayoutSendMoney.setAlpha(0.5f);
                    relativeLayoutSendMoney.setEnabled(false);
                } else{
                    relativeLayoutSendMoney.setAlpha(1.0f);
                    relativeLayoutSendMoney.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setData();

        ((TextView)findViewById(R.id.textViewDebitFrom)).setTypeface(Fonts.mavenMedium(this));
        linearLayoutContact = (LinearLayout) findViewById(R.id.linearLayoutContact);
        imageViewBank = (ImageView) findViewById(R.id.imageViewBank);
        textViewAccountNumber = (TextView) findViewById(R.id.textViewAccountNumber); textViewAccountNumber.setTypeface(Fonts.mavenMedium(this));
        textViewBankName = (TextView) findViewById(R.id.textViewBankName); textViewBankName.setTypeface(Fonts.mavenRegular(this));
        editTextLocation = (EditText) findViewById(R.id.editTextLocation); editTextLocation.setTypeface(Fonts.mavenRegular(this));

    }

    void setData() {
        contactNameTxt.setText(contactDetails.getName());
        if (new Validator().validateEmail(contactDetails.getPhone())) {
            contactMobileTxt.setText(contactDetails.getPhone());
        } else
            contactMobileTxt.setText(contactDetails.getPhone() + "  Mobile");

        if((contactDetails.getAmount() != null) && (!contactDetails.getAmount().equalsIgnoreCase(""))){
            amountET.setText(contactDetails.getAmount());
            amountET.setEnabled(false);
        } else{
            amountET.setText("");
            amountET.setEnabled(true);
        }

        // Set image if exists
        try {
            if (contactDetails.getThumb() != null) {
                //contactImage.setImageBitmap(contactDetails.getThumb());
                Picasso.with(SendMoneyActivity.this).load(contactDetails.getThumb()).into(contactImage);
            } else {
                contactImage.setImageResource(R.drawable.icon_user);
            }
            // Seting round image
//            Bitmap bm = BitmapFactory.decodeResource(holder.contactImage.getResources(), R.drawable.icon_logo); // Load default image
//            roundedImage = new RoundImage(bm);
//            v.imageView.setImageDrawable(roundedImage);
        } catch (Exception e) {
            // Add default picture
            contactImage.setImageResource(R.drawable.icon_user);
            e.printStackTrace();
        }
    }


    // used to send the  money
    private void callingSendMoneyApi() {
        CallProgressWheel.showLoadingDialog(SendMoneyActivity.this, AppConstant.PLEASE);
        SendMoneyRequest request = new SendMoneyRequest();

        // logic to replace extra character in phone number
        String PhoneNumber = contactDetails.getPhone();
        char[] number = PhoneNumber.toCharArray();
        Character[] arrayNumerics = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        List<Character> listNumerics = Arrays.asList(arrayNumerics);
        for (int i=0; i<number.length; i++)
        {
            if(number[i] != '+' || !listNumerics.contains(number[i]))
            {
                PhoneNumber.replace(number[i], ' ');
            }
        }

        // request.setPhone_no(contactDetails.getPhone());
        request.setPhone_no(PhoneNumber);

        request.setAccess_token(accessToken);
        request.setAmount(amountET.getText().toString());
        request.setMessage(messageET.getText().toString());
        request.setOrderId(contactDetails.getOrderId());


        RestClient.getPayApiService().sendMoney(request, new Callback<SendMoneyResponse>() {
            @Override
            public void success(SendMoneyResponse sendMoneyResponse, Response response) {
                System.out.println("SendMoneyActivity.success22222222");
                CallProgressWheel.dismissLoadingDialog();
                if (sendMoneyResponse != null) {
//                    Prefs.with(SignUpActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getToken());
//
                    int flag = sendMoneyResponse.getFlag();
                    if (flag == ApiResponseFlags.TXN_INITIATED.getOrdinal()) {
                        callBankTransactionApi(sendMoneyResponse.getTxnDetails());
                        contactDetails.setOrderId(String.valueOf(sendMoneyResponse.getTxnDetails().getOrderId()));
                        //callBankTransactionStatusApi(sendMoneyResponse.getTxnDetails());
                    } else {
                        CommonMethods.callingBadToken(SendMoneyActivity.this, flag, sendMoneyResponse.getMessage());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    System.out.println("SendMoneyActivity.failure2222222");
                    CallProgressWheel.dismissLoadingDialog();
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                        showAlertNoInternet(SendMoneyActivity.this);
                    } else {
                        String json = new String(((TypedByteArray) error.getResponse()
                                .getBody()).getBytes());

                        JSONObject jsonObject = new JSONObject(json);
                        SingleButtonAlert.showAlert(SendMoneyActivity.this, jsonObject.getString("message"), AppConstant.OK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CallProgressWheel.dismissLoadingDialog();
                }
            }
        });


    }


    void callBankTransactionStatusApi(SendMoneyResponse.TxnDetails txnDetails) {
        Bundle bundle = new Bundle();
        bundle.putString("mid", txnDetails.getMid()); // YBL0000000000123
        bundle.putString("merchantKey", txnDetails.getMkey());// b0222ce704ebc0c1f4dc24360751f9f6
        bundle.putString("merchantTxnID", Integer.toString(txnDetails.getOrderId())); // 11
        bundle.putString("yblTxnId", "");
        bundle.putString("refranceId", "");
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
//        bundle.putString("payeeMMID", "0000000");


        System.out.println("mid== " + txnDetails.getMid());
        System.out.println("merchantKey== " + txnDetails.getMkey());
        System.out.println("merchantTxnID== " + Integer.toString(txnDetails.getOrderId()));
        System.out.println("transactionDesc== " + txnDetails.getTransactionDescription());
        System.out.println("currency== " + txnDetails.getCurrency());
        System.out.println("paymentType== " + txnDetails.getPaymentType());
        System.out.println("transactionType== " + txnDetails.getTransactionType());
        System.out.println("merchantCatCode== " + txnDetails.getMcc());
        System.out.println("amount== " + Integer.toString(txnDetails.getAmount()));
        System.out.println("payeeMobileNo== " + "8179145931");


        Intent intent = new Intent(getApplicationContext(), TransactionStatus.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 3);
    }

    void callBankTransactionApi(SendMoneyResponse.TxnDetails txnDetails) {
        Bundle bundle = new Bundle();
        bundle.putString("mid", txnDetails.getMid()); // YBL0000000000123
        bundle.putString("merchantKey", txnDetails.getMkey());// b0222ce704ebc0c1f4dc24360751f9f6
        bundle.putString("merchantTxnID", Integer.toString(txnDetails.getOrderId())); // 11
        bundle.putString("transactionDesc", txnDetails.getTransactionDescription()); // SEND MONEY REQUEST
        bundle.putString("currency", txnDetails.getCurrency()); //INR
        bundle.putString("appName", "com.jugnoo.pay"); // JUGNOO_PAY
        bundle.putString("paymentType", txnDetails.getPaymentType()); // P2P
        bundle.putString("transactionType", txnDetails.getTransactionType()); // PAY
        bundle.putString("merchantCatCode", txnDetails.getMcc());  // 4814
        bundle.putString("amount", Integer.toString(txnDetails.getAmount())); // 20.00
        bundle.putString("payeeMobileNo", txnDetails.getPayee_phone_no());

        bundle.putString("payeePayAddress", txnDetails.getPayee_vpa());

        bundle.putString("payerMobileNo", txnDetails.getPayer_phone_no());
        bundle.putString("payerPaymentAddress", txnDetails.getPayer_vpa());

        // new keys for new yes bank SDK - added on 21-11-2016
        bundle.putString("payeeAccntNo", txnDetails.getPayeeAccntNo());
        bundle.putString("payeeIFSC", txnDetails.getPayeeIFSC());
        bundle.putString("payeeAadhaarNo", txnDetails.getPayeeAadhaarNo());
        bundle.putString("expiryTime", txnDetails.getExpiryTime());
        bundle.putString("payerAccntNo", txnDetails.getPayerAccntNo());
        bundle.putString("payerIFSC", txnDetails.getPayerIFSC());
        bundle.putString("payerAadhaarNo", txnDetails.getPayerAadhaarNo());

        bundle.putString("subMerchantID", txnDetails.getSubMerchantID());
        bundle.putString("whitelistedAccnts", txnDetails.getWhitelistedAccnts());
        bundle.putString("payerMMID", txnDetails.getPayerMMID());
        bundle.putString("payeeMMID", txnDetails.getPayeeMMID());
        bundle.putString("refurl", txnDetails.getRefurl());
        //-----------------------

        System.out.println("mid== " + txnDetails.getMid());
        System.out.println("merchantKey== " + txnDetails.getMkey());
        System.out.println("merchantTxnID== " + Integer.toString(txnDetails.getOrderId()));
        System.out.println("transactionDesc== " + txnDetails.getTransactionDescription());
        System.out.println("currency== " + txnDetails.getCurrency());
        System.out.println("paymentType== " + txnDetails.getPaymentType());
        System.out.println("transactionType== " + txnDetails.getTransactionType());
        System.out.println("merchantCatCode== " + txnDetails.getMcc());
        System.out.println("amount== " + Integer.toString(txnDetails.getAmount()));
        System.out.println("payeeMobileNo== " + "8179145931");


        Intent intent = new Intent(getApplicationContext(), PayActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK
                && data != null) {
            Bundle bundle = data.getExtras();
            String pgMeTrnRefNo = bundle.getString("pgMeTrnRefNo");
            String orderNo = bundle.getString("orderNo");
            String txnAmount = bundle.getString("txnAmount");
            String tranAuthdate = bundle.getString("tranAuthdate");
            String statusCode = bundle.getString("status");
            String statusDesc = bundle.getString("statusDesc");
            String responsecode = bundle.getString("responsecode");
            String approvalCode = bundle.getString("approvalCode");
            String payerVA = bundle.getString("payerVA");
            String npciTxnId = bundle.getString("npciTxnId");
            String refId = bundle.getString("refId");

            // new parameters for new yes bank SDK
            String payerAccountNo = bundle.getString("payerAccountNo");
            String payerIfsc = bundle.getString("payerIfsc");
            String payerAccName = bundle.getString("payerAccName");
            //------------------------

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

            System.out.println("pgMeTrnRefNo== " + pgMeTrnRefNo);
            System.out.println("tranAuthdate== " + tranAuthdate);
            System.out.println("status== " + statusCode);
            System.out.println("statusDesc== " + statusDesc);
            System.out.println("responsecode== " + responsecode);
            System.out.println("approvalCode== " + approvalCode);
            System.out.println("payerVA== " + payerVA);
            System.out.println("npciTxnId== " + npciTxnId);
            System.out.println("refId== " + refId);
            System.out.println("orderNo== " + orderNo);

            SendMoneyCallback sendMoneyCallback = new SendMoneyCallback();
            sendMoneyCallback.setAmount(txnAmount);
            sendMoneyCallback.setAccess_token(accessToken);
            sendMoneyCallback.setPhone_no(contactDetails.getPhone());
            MessageRequest messageRequest = new MessageRequest();
            messageRequest.setApprovalCode(approvalCode);
            messageRequest.setNpciTxnId(npciTxnId);
            messageRequest.setOrderNo(orderNo);
            messageRequest.setPayerVA(payerVA);
            messageRequest.setRefId(refId);
            messageRequest.setResponsecode(responsecode);
            messageRequest.setTranAuthdate(tranAuthdate);
            messageRequest.setPgMeTrnRefNo(pgMeTrnRefNo);
            messageRequest.setStatus(statusCode);
            messageRequest.setStatusDesc(statusDesc);

            // new code - added at 21-11-2016
            messageRequest.setPayerAccountNo(payerAccountNo);
            messageRequest.setPayerIfsc(payerIfsc);
            messageRequest.setPayerAccName(payerAccName);
            //--------------

            sendMoneyCallback.setMessage(messageRequest);
            final SendMoneyRequest request = new SendMoneyRequest();
            request.setPhone_no(contactDetails.getPhone());
            request.setAccess_token(accessToken);
            request.setAmount(amountET.getText().toString());
            request.setMessage(messageET.getText().toString());
            Intent intent = new Intent(SendMoneyActivity.this, TranscCompletedActivity.class);
            Bundle bun = new Bundle();
            bun.putSerializable(AppConstant.SEND_TRANSACTION_DATA, sendMoneyCallback);

            intent.putExtra(AppConstant.TRANSACTION_DATA, request);
            bun.putParcelable(AppConstant.CONTACT_DATA, contactDetails);
            intent.putExtras(bun);
            intent.putExtra(AppConstant.ORDER_ID, orderNo);
            startActivity(intent);
            finish();
//            callingSendMoneyCallbackApi(sendMoneyCallback);
        }
        else if (requestCode == 3 && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle= data.getExtras();
            String yblRegID = bundle.getString("yblRegID");
            String merchantTxnID = bundle.getString("merchantTxnID");
            String amount = bundle.getString("amount");
            String txnAuthDate= bundle.getString("txnAuthDate");
            String status = bundle.getString("status");
            String statusdesc = bundle.getString("statusdesc");
            String respCode = bundle.getString("respCode");
            String approvalNo = bundle.getString("approvalNo");
            String payerVA = bundle.getString("payerVA");
            String npciTxnId = bundle.getString("npciTxnId");
            String refranceId = bundle.getString("refranceId");
        }
        else{
            if(data == null){
                Log.e("call failed","call failed");
                SendMoneyRequest request = new SendMoneyRequest();
                request.setPhone_no(contactDetails.getPhone());
                request.setAccess_token(accessToken);
                request.setAmount(amountET.getText().toString());
                request.setMessage(messageET.getText().toString());
                request.setOrderId(contactDetails.getOrderId());

                Intent intent = new Intent(SendMoneyActivity.this, TranscCompletedActivity.class);
                Bundle bun = new Bundle();
                intent.putExtra(AppConstant.TRANSACTION_DATA, request);
                bun.putParcelable(AppConstant.CONTACT_DATA, contactDetails);
                intent.putExtras(bun);
                intent.putExtra(AppConstant.ORDER_ID, contactDetails.getOrderId());
                intent.putExtra(AppConstant.TRANSACTION_STATUS, "Failed");
                startActivity(intent);
                finish();
            }
            //Bundle bundle = data.getExtras();
        }
    }


    // used to request the  money from a specific friend
    private void callingRequestMoneyApi() {
        CallProgressWheel.showLoadingDialog(SendMoneyActivity.this, AppConstant.PLEASE);
        final SendMoneyRequest request = new SendMoneyRequest();

        // logic to replace extra character in phone number
        String PhoneNumber = contactDetails.getPhone();
        char[] number = PhoneNumber.toCharArray();
        Character[] arrayNumerics = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        List<Character> listNumerics = Arrays.asList(arrayNumerics);
        for (int i=0; i<number.length; i++)
        {
            if(number[i] != '+' || !listNumerics.contains(number[i]))
            {
                PhoneNumber.replace(number[i], ' ');
            }
        }

        // request.setPhone_no(contactDetails.getPhone());
        request.setPhone_no(PhoneNumber);

        request.setAccess_token(accessToken);
        request.setAmount(amountET.getText().toString());
        request.setMessage(messageET.getText().toString());


        RestClient.getPayApiService().requestMoney(request, new Callback<CommonResponse>() {
            @Override
            public void success(CommonResponse commonResponse, Response response) {
                System.out.println("SendMoneyActivity.success22222222");
                CallProgressWheel.dismissLoadingDialog();
                if (commonResponse != null) {
//                    Prefs.with(SignUpActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getToken());
//
                    int flag = commonResponse.getFlag();
                    if (flag == ApiResponseFlags.TXN_INITIATED.getOrdinal()) {
                        Intent intent = new Intent(SendMoneyActivity.this, TranscCompletedActivity.class);
                        intent.putExtra(AppConstant.TRANSACTION_DATA, request);
                        Bundle bun = new Bundle();
                        bun.putParcelable(AppConstant.CONTACT_DATA, contactDetails);
                        intent.putExtras(bun);
                        intent.putExtra(AppConstant.ORDER_ID, commonResponse.getOrder_id());
                        startActivity(intent);
                        finish();
//                        SingleButtonAlert.showAlert(SendMoneyActivity.this, "Request Submitted successfully.", AppConstant.OK);

                    } else if (flag == ApiResponseFlags.NO_RECIEVER.getOrdinal()
                            || flag == ApiResponseFlags.INVALID_RECIEVER.getOrdinal()
                            || flag == ApiResponseFlags.INVALID_PHONE_NUMBER.getOrdinal()) {
                                SingleButtonAlert.showAlert(SendMoneyActivity.this, commonResponse.getMessage(), AppConstant.OK);
                    } else if (flag == ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal()) {
                        // startActivity(new Intent(SendMoneyActivity.this, SignInActivity.class));
                        startActivity(new Intent(SendMoneyActivity.this, SplashNewActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    System.out.println("SendMoneyActivity.failure2222222");
                    CallProgressWheel.dismissLoadingDialog();
                    if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                        showAlertNoInternet(SendMoneyActivity.this);
                    } else {
                        String json = new String(((TypedByteArray) error.getResponse()
                                .getBody()).getBytes());
                        JSONObject jsonObject = new JSONObject(json);
                        SingleButtonAlert.showAlert(SendMoneyActivity.this, jsonObject.getString("message"), AppConstant.OK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CallProgressWheel.dismissLoadingDialog();
                }
            }
        });
    }
}
