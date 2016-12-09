package com.jugnoo.pay.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.models.MessageRequest;
import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.models.SendMoneyCallback;
import com.jugnoo.pay.models.SendMoneyRequest;
import com.jugnoo.pay.models.SendMoneyResponse;
import com.jugnoo.pay.utils.ApiResponseFlags;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.Validator;
import com.sabkuchfresh.utils.AppConstant;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.yesbank.PayActivity;
import com.yesbank.TransactionStatus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;
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
        if(!requestStatusConfirmation) {
            Intent intent = new Intent(this, SelectContactActivity.class);
            intent.putExtra(AppConstant.REQUEST_STATUS, requestStatus);
            startActivity(intent);
        }
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
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

    private LinearLayout linearLayoutContact, linearLayoutDebitFrom;
    private ImageView imageViewBank, imageViewSendMoney;
    private TextView textViewAccountNumber, textViewBankName;
    private EditText editTextLocation;
    private LinearLayout relativeLayoutSendMoney;
    private TextView textViewSendMoney;

    @OnClick(R.id.relativeLayoutSendMoney)
    void sendBtnClicked() {
        if (amountET.getText().toString().length() > 0) {
            int amount = Integer.parseInt(amountET.getText().toString());
            if(amount > 0 && amount <= 100000) {
                if (requestStatus) {
                    callingRequestMoneyApi();
                } else {
                    callingSendMoneyApi();
                }
            } else{
                amountET.requestFocus();
                amountET.setHovered(true);
                amountET.setError(getString(R.string.amount_range_error));
            }
        } else {
            amountET.requestFocus();
            amountET.setHovered(true);
            amountET.setError(getString(R.string.fill_your_amount));
        }
    }

    private SelectUser contactDetails;
    private boolean requestStatus = false, requestStatusConfirmation = false;
    private String accessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
        ButterKnife.bind(this);
        requestStatus = getIntent().getBooleanExtra(AppConstant.REQUEST_STATUS, false);
        requestStatusConfirmation = getIntent().getBooleanExtra(AppConstant.REQUEST_STATUS_CONFIRMATION, false);

        ((TextView)findViewById(R.id.textViewDebitFrom)).setTypeface(Fonts.mavenMedium(this));
        linearLayoutContact = (LinearLayout) findViewById(R.id.linearLayoutContact);
        linearLayoutDebitFrom = (LinearLayout) findViewById(R.id.linearLayoutDebitFrom);
        imageViewBank = (ImageView) findViewById(R.id.imageViewBank);
        textViewAccountNumber = (TextView) findViewById(R.id.textViewAccountNumber); textViewAccountNumber.setTypeface(Fonts.mavenMedium(this));
        textViewBankName = (TextView) findViewById(R.id.textViewBankName); textViewBankName.setTypeface(Fonts.mavenRegular(this));
        editTextLocation = (EditText) findViewById(R.id.editTextLocation); editTextLocation.setTypeface(Fonts.mavenRegular(this));
        imageViewSendMoney = (ImageView) findViewById(R.id.imageViewSendMoney);
        messageET.setTypeface(Fonts.mavenRegular(this));
        amountET.setTypeface(Fonts.mavenRegular(this));
        contactNameTxt.setTypeface(Fonts.mavenMedium(this));
        contactMobileTxt.setTypeface(Fonts.mavenRegular(this));

        relativeLayoutSendMoney = (LinearLayout) findViewById(R.id.relativeLayoutSendMoney);
        textViewSendMoney = (TextView) findViewById(R.id.textViewSendMoney); textViewSendMoney.setTypeface(Fonts.mavenMedium(this));
        toolbarTitleTxt.setTypeface(Fonts.avenirNext(this));
        if (requestStatus) {
            toolbarTitleTxt.setText(R.string.request_money);
            textViewSendMoney.setText(getResources().getString(R.string.request_money));
            linearLayoutDebitFrom.setVisibility(View.GONE);
            imageViewSendMoney.setImageResource(R.drawable.ic_request_money_white);
        }
        else {
            toolbarTitleTxt.setText(requestStatusConfirmation ? R.string.confirmation : R.string.send_money);
            textViewSendMoney.setText(getResources().getString(R.string.send_money));
            linearLayoutDebitFrom.setVisibility(View.VISIBLE);
            imageViewSendMoney.setImageResource(R.drawable.ic_send_money_white);
        }
        linearLayoutDebitFrom.setVisibility(View.GONE);

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



        linearLayoutContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestStatus || !requestStatusConfirmation) {
                    backBtnClicked();
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Utils.showSoftKeyboard(SendMoneyActivity.this, amountET);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 200);

    }

    void setData() {
        contactNameTxt.setText(contactDetails.getName());
        if (new Validator().validateEmail(contactDetails.getPhone())) {
            contactDetails.setPhone("+91"+Utils.retrievePhoneNumberTenChars(contactDetails.getPhone()));
            contactMobileTxt.setText(contactDetails.getPhone());
        } else {
            contactMobileTxt.setText(contactDetails.getPhone());
        }

        if((contactDetails.getAmount() != null) && (!contactDetails.getAmount().equalsIgnoreCase(""))){
            amountET.setText(contactDetails.getAmount());
            amountET.setEnabled(false);
            amountET.setTextColor(getResources().getColor(R.color.green_rupee));
            messageET.setVisibility(TextUtils.isEmpty(contactDetails.getMessage()) ? View.GONE : View.VISIBLE);
            messageET.setText(contactDetails.getMessage());
            messageET.setEnabled(false);
        } else {
            amountET.setText("");
            amountET.setEnabled(true);
            amountET.setTextColor(getResources().getColor(R.color.text_color));
            messageET.setText("");
            messageET.setEnabled(true);
        }

        try {
            if (contactDetails.getThumb() != null) {
                Picasso.with(SendMoneyActivity.this).load(contactDetails.getThumb()).transform(new CircleTransform()).into(contactImage);
            } else {
                contactImage.setImageResource(R.drawable.icon_user);
            }
        } catch (Exception e) {
            contactImage.setImageResource(R.drawable.icon_user);
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        backBtnClicked();
    }

    private void callingSendMoneyApi() {
        try {
            if (AppStatus.getInstance(this).isOnline(this)) {
				CallProgressWheel.showLoadingDialog(SendMoneyActivity.this, AppConstant.PLEASE);
				SendMoneyRequest request = new SendMoneyRequest();

				if (Utils.isPhoneValid(contactDetails.getPhone())) {
					request.setPhone_no(Utils.removeExtraCharsPhoneNumber(contactDetails.getPhone()));
                    request.setHas_vpa(0);
				} else if (Utils.isVPAValid(contactDetails.getPhone())) {
					request.setVpa(contactDetails.getPhone());
                    request.setHas_vpa(1);
				}

				request.setAccess_token(accessToken);
				request.setAmount(amountET.getText().toString());
				request.setMessage(messageET.getText().toString());
				request.setOrderId(contactDetails.getOrderId());


				RestClient.getPayApiService().sendMoney(request, new Callback<SendMoneyResponse>() {
					@Override
					public void success(SendMoneyResponse sendMoneyResponse, Response response) {
                        CallProgressWheel.dismissLoadingDialog();
                        try {
                            int flag = sendMoneyResponse.getFlag();
                            if (flag == ApiResponseFlags.TXN_INITIATED.getOrdinal()) {
                                contactDetails.setOrderId(String.valueOf(sendMoneyResponse.getTxnDetails().getOrderId()));
								callBankTransactionApi(sendMoneyResponse.getTxnDetails());
							} else {
                                retryDialogSendMoneyApi(sendMoneyResponse.getMessage());
							}
                        } catch (Exception e) {
                            e.printStackTrace();
                            retryDialogSendMoneyApi(Data.SERVER_ERROR_MSG);
                        }
                    }

					@Override
					public void failure(RetrofitError error) {
                        CallProgressWheel.dismissLoadingDialog();
                        retryDialogSendMoneyApi(Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});
			} else {
				retryDialogSendMoneyApi(Data.CHECK_INTERNET_MSG);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialogSendMoneyApi(String message){
        DialogPopup.alertPopupTwoButtonsWithListeners(SendMoneyActivity.this, "", message,
                getString(R.string.retry),
                getString(R.string.cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callingSendMoneyApi();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }
                , false, false);
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
        }
    }


    private void callingRequestMoneyApi() {
        try {
            if (AppStatus.getInstance(this).isOnline(this)) {
				CallProgressWheel.showLoadingDialog(SendMoneyActivity.this, AppConstant.PLEASE);
				final SendMoneyRequest request = new SendMoneyRequest();

				if (Utils.isPhoneValid(contactDetails.getPhone())) {
					request.setPhone_no(Utils.removeExtraCharsPhoneNumber(contactDetails.getPhone()));
				} else if (Utils.isVPAValid(contactDetails.getPhone())) {
					request.setVpa(contactDetails.getPhone());
				}
                contactDetails.setAmount(amountET.getText().toString());
                contactDetails.setMessage(messageET.getText().toString());
				request.setAccess_token(accessToken);
				request.setAmount(amountET.getText().toString());
				request.setMessage(messageET.getText().toString());


				RestClient.getPayApiService().requestMoney(request, new Callback<CommonResponse>() {
					@Override
					public void success(CommonResponse commonResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						CallProgressWheel.dismissLoadingDialog();
						try {
							int flag = commonResponse.getFlag();
							if (flag == ApiResponseFlags.TXN_INITIATED.getOrdinal()) {
                                contactDetails.setStatusMessage(commonResponse.getMessage());
                                contactDetails.setOrderId(commonResponse.getOrder_id());
                                contactDetails.setDate(commonResponse.getDate());
								Intent intent = new Intent(SendMoneyActivity.this, TranscCompletedActivity.class);
								intent.putExtra(AppConstant.TRANSACTION_DATA, request);
								Bundle bun = new Bundle();
								bun.putParcelable(AppConstant.CONTACT_DATA, contactDetails);
								intent.putExtras(bun);
								intent.putExtra(AppConstant.ORDER_ID, commonResponse.getOrder_id());
								startActivity(intent);
								finish();
							} else if (flag == ApiResponseFlags.NO_RECIEVER.getOrdinal()
									|| flag == ApiResponseFlags.INVALID_RECIEVER.getOrdinal()
									|| flag == ApiResponseFlags.INVALID_PHONE_NUMBER.getOrdinal()) {
								retryDialogRequestMoneyApi(commonResponse.getMessage());
							} else{
								retryDialogRequestMoneyApi(commonResponse.getMessage());
							}
						} catch (Exception e) {
							e.printStackTrace();
							retryDialogRequestMoneyApi(Data.SERVER_ERROR_MSG);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						CallProgressWheel.dismissLoadingDialog();
						retryDialogRequestMoneyApi(Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});
			} else {
				retryDialogRequestMoneyApi(Data.CHECK_INTERNET_MSG);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialogRequestMoneyApi(String message){
        DialogPopup.alertPopupTwoButtonsWithListeners(SendMoneyActivity.this, "", message,
                getString(R.string.retry),
                getString(R.string.cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callingRequestMoneyApi();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }
                , false, false);
    }
}
