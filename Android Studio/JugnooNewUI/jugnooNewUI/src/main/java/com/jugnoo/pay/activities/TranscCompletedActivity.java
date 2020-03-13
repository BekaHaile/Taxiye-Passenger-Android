package com.jugnoo.pay.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jugnoo.pay.models.MessageRequest;
import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.models.SendMoneyCallback;
import com.jugnoo.pay.models.SendMoneyCallbackResponse;
import com.jugnoo.pay.models.SendMoneyRequest;
import com.jugnoo.pay.models.TransacHistoryResponse;
import com.jugnoo.pay.models.TransactionSummaryResponse;
import com.jugnoo.pay.utils.ApiResponseFlags;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Utils;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.wallet.models.TransactionInfo;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by cl-macmini-38 on 9/22/16.
 */
public class TranscCompletedActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitleTxt;
    @BindView(R.id.back_btn)
    ImageButton backBtn;

    @OnClick(R.id.back_btn)
    void backBtnClicked() {
        onBackPressed();
    }

    @BindView(R.id.paid_txt)
    TextView textViewPaid;

    @BindView(R.id.message_txt)
    TextView msgTxt;
    @BindView(R.id.message)
    TextView textViewMessage;

    @BindView(R.id.contact_name_txt)
    TextView contactNameTxt;
    @BindView(R.id.mobile_txt)
    TextView mobileTxt;
    @BindView(R.id.contact_image)
    ImageView contactImg;
    @BindView(R.id.ok_btn)
    Button buttonOk;

    @OnClick(R.id.ok_btn)
    void okBtnClicked()
    {
        try
        {
            try {
                SelectContactActivity.selectContactActivityObj.finish();
            } catch (Exception e) { e.printStackTrace(); }

            try{
                SendMoneyActivity.sendMoneyActivityObj.finish();
            } catch (Exception e) { e.printStackTrace(); }

            startActivity(new Intent(this, SupportActivity.class));
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    private SelectUser contactDetails;
    private SendMoneyRequest requestObj;
    private String orderId;
    String transactionStatus = "";

    private TextView tvTransStatusVal, tvTransStatusValMessage, tvTransTimeVal, tvBankRefIdVal, tvNpciTransIdVal, tvAmountVal,
            textViewAccountNumber, textViewBankName, textViewDebitValue, textViewDebitFrom;
    private ImageView ivTransCompleted, imageViewBank, imageViewCall;
    private CardView cardViewDebitFrom, cardViewMessage;
    private RelativeLayout rvBankRefId, rvNpciTransId;
    private ScrollView scrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_transaction_completed);
            ButterKnife.bind(this);
            toolbarTitleTxt.setText(R.string.transc_completed_screen);
            toolbarTitleTxt.setTypeface(Fonts.avenirNext(this));
            mToolBar.setTitle("");
            setSupportActionBar(mToolBar);
            contactDetails = (SelectUser) getIntent().getExtras().getParcelable(AppConstant.CONTACT_DATA);
            requestObj = (SendMoneyRequest)getIntent().getSerializableExtra(AppConstant.TRANSACTION_DATA);
            orderId = getIntent().getStringExtra(AppConstant.ORDER_ID);
            if(getIntent().hasExtra(AppConstant.TRANSACTION_STATUS)){
                transactionStatus = getIntent().getStringExtra(AppConstant.TRANSACTION_STATUS);
            }

            scrollView = (ScrollView) findViewById(R.id.scrollView);
            tvTransStatusVal = (TextView) findViewById(R.id.tvTransStatusVal); tvTransStatusVal.setTypeface(Fonts.mavenMedium(this));
            tvTransStatusValMessage = (TextView) findViewById(R.id.tvTransStatusValMessage); tvTransStatusValMessage.setTypeface(Fonts.mavenRegular(this));
            tvTransStatusValMessage.setVisibility(View.GONE);
            tvTransTimeVal = (TextView) findViewById(R.id.tvTransTimeVal); tvTransTimeVal.setTypeface(Fonts.mavenMedium(this));
            tvBankRefIdVal = (TextView) findViewById(R.id.tvBankRefIdVal); tvBankRefIdVal.setTypeface(Fonts.mavenMedium(this));
            tvNpciTransIdVal = (TextView) findViewById(R.id.tvNpciTransIdVal); tvNpciTransIdVal.setTypeface(Fonts.mavenMedium(this));
            tvAmountVal = (TextView) findViewById(R.id.tvAmountVal); tvAmountVal.setTypeface(Fonts.mavenMedium(this), Typeface.BOLD);
            textViewAccountNumber = (TextView) findViewById(R.id.textViewAccountNumber); textViewAccountNumber.setTypeface(Fonts.mavenMedium(this));
            textViewBankName = (TextView) findViewById(R.id.textViewBankName); textViewBankName.setTypeface(Fonts.mavenMedium(this));
            textViewDebitValue = (TextView) findViewById(R.id.textViewDebitValue); textViewDebitValue.setTypeface(Fonts.mavenMedium(this));
            ivTransCompleted = (ImageView) findViewById(R.id.ivTransCompleted);
            imageViewBank = (ImageView) findViewById(R.id.imageViewBank);
            cardViewDebitFrom = (CardView) findViewById(R.id.cardViewDebitFrom);
            cardViewMessage = (CardView) findViewById(R.id.cardViewMessage);
            buttonOk.setTypeface(Fonts.mavenRegular(this));
            buttonOk.setText(getString(R.string.need_help));
            mobileTxt.setTypeface(Fonts.mavenRegular(this));
            contactNameTxt.setTypeface(Fonts.mavenMedium(this));
            textViewMessage.setTypeface(Fonts.mavenMedium(this));
            msgTxt.setTypeface(Fonts.mavenRegular(this));
            textViewPaid.setTypeface(Fonts.mavenRegular(this));
            imageViewCall = (ImageView) findViewById(R.id.imageViewCall);
            ((TextView)findViewById(R.id.tvTransStatus)).setTypeface(Fonts.mavenRegular(this));
            ((TextView)findViewById(R.id.tvTransTime)).setTypeface(Fonts.mavenRegular(this));
            ((TextView)findViewById(R.id.tvBankRefId)).setTypeface(Fonts.mavenRegular(this));
            ((TextView)findViewById(R.id.tvNpciTransId)).setTypeface(Fonts.mavenRegular(this));
            ((TextView)findViewById(R.id.tvAmount)).setTypeface(Fonts.mavenRegular(this));
            textViewDebitFrom = (TextView)findViewById(R.id.textViewDebitFrom); textViewDebitFrom.setTypeface(Fonts.mavenRegular(this));
            rvNpciTransId = (RelativeLayout) findViewById(R.id.rvNpciTransId);
            rvBankRefId = (RelativeLayout) findViewById(R.id.rvBankRefId);

            imageViewCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.openCallIntent(TranscCompletedActivity.this, mobileTxt.getText().toString());
                }
            });

            SendMoneyCallback callback = (SendMoneyCallback) getIntent().getExtras().getSerializable(AppConstant.SEND_TRANSACTION_DATA);
            setData();

            scrollView.setVisibility(View.GONE);
            if(callback!=null) {
                // for send
                callingSendMoneyCallbackApi(callback.getMessage(), "", callback.getAccess_token());
                textViewPaid.setText(getResources().getString(R.string.to));
            } else if(transactionStatus.equalsIgnoreCase("Failed")){
                tvTransStatusVal.setText(getString(R.string.failed));
                tvTransStatusVal.setTextColor(getResources().getColor(R.color.red_status));
                callingSendMoneyCallbackApi(null, requestObj.getOrderId(), requestObj.getAccess_token());
            }
            else if(getIntent().getIntExtra(Constants.KEY_FETCH_TRANSACTION_SUMMARY, 0) == 1){
                try {
                    Gson gson = new Gson();
                    TransactionInfo transactionInfo = gson.fromJson(getIntent().getStringExtra(Constants.KEY_TXN_OBJECT), TransactionInfo.class);
                    orderId = getIntent().getStringExtra(Constants.KEY_ORDER_ID);
                    apiGetTransactionSummary(Integer.parseInt(orderId),
							getIntent().getIntExtra(Constants.KEY_TXN_TYPE, TransacHistoryResponse.Type.REQUEST_BY_PENDING.getOrdinal()),
                            transactionInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                // for Request
                scrollView.setVisibility(View.VISIBLE);
                toolbarTitleTxt.setText(getString(R.string.jugnoo_pay));
                textViewPaid.setText(getResources().getString(R.string.from));
                rvBankRefId.setVisibility(View.GONE);
                rvNpciTransId.setVisibility(View.GONE);
                cardViewDebitFrom.setVisibility(View.GONE);
                tvTransStatusVal.setText(getString(R.string.requested_in_caps));
                tvTransStatusVal.setTextColor(getResources().getColor(R.color.green_rupee));
                ivTransCompleted.setImageResource(R.drawable.ic_tick_copy);
                tvTransStatusValMessage.setText(contactDetails.getStatusMessage());
                tvTransStatusValMessage.setVisibility(TextUtils.isEmpty(contactDetails.getStatusMessage()) ? View.GONE : View.VISIBLE);
                tvTransStatusValMessage.setTextColor(getResources().getColor(R.color.green_rupee));
                tvAmountVal.setText(getString(R.string.rupees_value_format,
                        Utils.getMoneyDecimalFormat().format(Double.parseDouble(contactDetails.getAmount()))));
                toolbarTitleTxt.setText(getResources().getString(R.string.transaction_id_number_format, contactDetails.getOrderId()));
                tvTransTimeVal.setText(DateOperations.convertDateViaFormat(DateOperations.utcToLocalWithTZFallback(contactDetails.getDate())));
            }
            cardViewDebitFrom.setVisibility(View.GONE);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setData() {
        try {
            tvTransTimeVal.setText(DateOperations.convertDateViaFormat(DateOperations.getCurrentTime()));
            textViewDebitValue.setText(String.format(getResources().getString(R.string.rupees_value_format), requestObj.getAmount()));
            toolbarTitleTxt.setText(getResources().getString(R.string.transaction_id_number_format, orderId));
            if((requestObj != null) && !requestObj.getMessage().equalsIgnoreCase("")) {
				msgTxt.setText(requestObj.getMessage());
			} else{
				cardViewMessage.setVisibility(View.GONE);
			}
            mobileTxt.setText(contactDetails.getPhone());
            contactNameTxt.setText(contactDetails.getName());
            try {
				if (contactDetails.getThumb() != null) {
					Picasso.with(TranscCompletedActivity.this).load(contactDetails.getThumb()).transform(new CircleTransform()).into(contactImg);
				} else {
					contactImg.setImageResource(R.drawable.icon_user);
				}
			} catch (Exception e) {
				contactImg.setImageResource(R.drawable.icon_user);
				e.printStackTrace();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // used to send the  money
    private void callingSendMoneyCallbackApi(final MessageRequest message, final String orderId, final String accessToken) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                CallProgressWheel.showLoadingDialog(TranscCompletedActivity.this, getString(R.string.please_wait));
                HashMap<String, String> params = new HashMap<>();

                params.put("order_id", orderId);
                params.put("access_token", accessToken);
                if (message != null) {
                    params.put("message", message.toString());
                }
                new HomeUtil().putDefaultParams(params);
                RestClient.getPayApiService().sendMoneyCallback(params, new Callback<SendMoneyCallbackResponse>() {
                    @Override
                    public void success(SendMoneyCallbackResponse commonResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        CallProgressWheel.dismissLoadingDialog();
                        try {
                            int flag = commonResponse.getFlag();
                            if (flag == ApiResponseFlags.TXN_COMPLETED.getOrdinal()) {
                                TransactionSummaryResponse.TxnDetail txnDetail = new TransactionSummaryResponse()
                                        .new TxnDetail(Double.parseDouble(requestObj.getAmount()), commonResponse.getDate(),
                                        getString(R.string.successful),
                                        contactDetails.getName(), contactDetails.getPhone(), contactDetails.getPhone(),
                                        getString(R.string.payment_to),
                                        commonResponse.getNpciTxnId(), commonResponse.getBankRefId(),
                                        commonResponse.getTxnMessage(), commonResponse.getMessage());
                                updateUI(txnDetail, true, true, false);
							}
                            else if (flag == ApiResponseFlags.TXN_FAILED.getOrdinal()) {
                                TransactionSummaryResponse.TxnDetail txnDetail = new TransactionSummaryResponse()
                                        .new TxnDetail(Double.parseDouble(requestObj.getAmount()), commonResponse.getDate(),
                                        getString(R.string.failed),
                                        contactDetails.getName(), contactDetails.getPhone(), contactDetails.getPhone(),
                                        getString(R.string.payment_to),
                                        commonResponse.getNpciTxnId(), commonResponse.getBankRefId(),
                                        commonResponse.getTxnMessage(), commonResponse.getMessage());
                                updateUI(txnDetail, false, true, false);
							}
                            else if(flag == ApiResponseFlags.TXN_NOT_EXISTS.getOrdinal())
                            {
                                // DialogPopup.alertPopup(TranscCompletedActivity.this, "", commonResponse.getMessage(), false);
                                DialogPopup.alertPopupWithListener(TranscCompletedActivity.this, getString(R.string.error), commonResponse.getMessage(), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        onBackPressed();
                                    }
                                });
                            }
                            else {
                                retryDialogSendMoneyCallbackApi(DialogErrorType.SERVER_ERROR, message, orderId, accessToken);
							}
                        } catch (Exception e) {
                            e.printStackTrace();
                            retryDialogSendMoneyCallbackApi(DialogErrorType.SERVER_ERROR, message, orderId, accessToken);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        CallProgressWheel.dismissLoadingDialog();
                        retryDialogSendMoneyCallbackApi(DialogErrorType.CONNECTION_LOST, message, orderId, accessToken);
                    }
                });
            } else {
                retryDialogSendMoneyCallbackApi(DialogErrorType.NO_NET, message, orderId, accessToken);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialogSendMoneyCallbackApi(DialogErrorType dialogErrorType, final MessageRequest message, final String orderId, final String accessToken){
        DialogPopup.dialogNoInternet(this,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        callingSendMoneyCallbackApi(message, orderId, accessToken);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }

    private void updateUI(TransactionSummaryResponse.TxnDetail txnDetail, boolean success, boolean debit, boolean credit){
        try {
            scrollView.setVisibility(View.VISIBLE);
            toolbarTitleTxt.setText(getString(R.string.transaction_id_number_format, String.valueOf(orderId)));
            tvTransStatusVal.setText(txnDetail.getStatus());
            if(success){  //transactionInfo.getStatus() == 1
                tvTransStatusVal.setTextColor(getResources().getColor(R.color.green_rupee));
                ivTransCompleted.setImageResource(R.drawable.ic_tick_copy);
                tvTransStatusValMessage.setVisibility(View.GONE);
            } else {
                tvTransStatusVal.setTextColor(getResources().getColor(R.color.red_status));
                ivTransCompleted.setImageResource(R.drawable.ic_failed);
                cardViewDebitFrom.setVisibility(View.GONE);
                tvTransStatusValMessage.setVisibility(TextUtils.isEmpty(txnDetail.getStatusMessage()) ? View.GONE : View.VISIBLE);
                tvTransStatusValMessage.setText(txnDetail.getStatusMessage());
                tvTransStatusValMessage.setTextColor(getResources().getColor(R.color.red_status));
            }

            tvTransTimeVal.setText(DateOperations.convertDateViaFormat(DateOperations.utcToLocalWithTZFallback(txnDetail.getDate())));
            rvBankRefId.setVisibility(TextUtils.isEmpty(txnDetail.getBankRefId()) ? View.GONE : View.VISIBLE);
            rvNpciTransId.setVisibility(TextUtils.isEmpty(txnDetail.getNpciTxnId()) ? View.GONE : View.VISIBLE);
            tvBankRefIdVal.setText(txnDetail.getBankRefId());
            tvNpciTransIdVal.setText(txnDetail.getNpciTxnId());
            tvAmountVal.setText(getString(R.string.rupees_value_format,
                    Utils.getMoneyDecimalFormat().format(txnDetail.getAmount())));
            textViewDebitValue.setText(getString(R.string.rupees_value_format,
					Utils.getMoneyDecimalFormat().format(txnDetail.getAmount())));


            textViewPaid.setText(txnDetail.getTxnString());
            contactImg.setImageResource(R.drawable.icon_user);
            // mobileTxt.setVisibility(View.GONE);
            imageViewCall.setVisibility(View.GONE);
            if(TextUtils.isEmpty(txnDetail.getName())){
				if(!TextUtils.isEmpty(txnDetail.getPhoneNo()) && Utils.isPhoneValid(txnDetail.getPhoneNo())){
					contactNameTxt.setText(txnDetail.getPhoneNo());
                    imageViewCall.setVisibility(View.VISIBLE);
				} else if(!TextUtils.isEmpty(txnDetail.getVpa())){
					contactNameTxt.setText(txnDetail.getVpa());
				}
			} else {
				contactNameTxt.setText(txnDetail.getName());
				if(!TextUtils.isEmpty(txnDetail.getPhoneNo()) && Utils.isPhoneValid(txnDetail.getPhoneNo())){
					mobileTxt.setText(txnDetail.getPhoneNo());
					imageViewCall.setVisibility(View.VISIBLE);
				} else if(!TextUtils.isEmpty(txnDetail.getVpa())){
					mobileTxt.setText(txnDetail.getVpa());
				}
			}

            msgTxt.setText(txnDetail.getMessage());
            cardViewMessage.setVisibility(TextUtils.isEmpty(txnDetail.getMessage()) ? View.GONE : View.VISIBLE);

            if(debit){ //(transactionInfo.transactionType == 1 || transactionInfo.transactionType == 3)
                textViewDebitFrom.setText(R.string.debit_from);
            } else if(credit){ //(transactionInfo.transactionType == 2 || transactionInfo.transactionType == 4)
                textViewDebitFrom.setText(R.string.credit_to);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void apiGetTransactionSummary(final int orderId, final int txnType, final TransactionInfo transactionInfo) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                CallProgressWheel.showLoadingDialog(this, getString(R.string.loading));
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
                params.put(Constants.KEY_TXN_ID, String.valueOf(orderId));
                params.put(Constants.KEY_TXN_TYPE, String.valueOf(txnType));

                new HomeUtil().putDefaultParams(params);
                RestClient.getPayApiService().getTransactionSummary(params, new Callback<TransactionSummaryResponse>() {
                    @Override
                    public void success(TransactionSummaryResponse summaryResponse, Response response) {
                        CallProgressWheel.dismissLoadingDialog();
                        try{
                            if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == summaryResponse.getFlag()){
                                TransactionSummaryResponse.TxnDetail txnDetail = summaryResponse.getTxnDetails().get(0);
                                updateUI(txnDetail, (transactionInfo.getStatus() == 1),
                                        (transactionInfo.transactionType == 1 || transactionInfo.transactionType == 3),
                                        (transactionInfo.transactionType == 2 || transactionInfo.transactionType == 4));
                            } else {
                                DialogPopup.alertPopupTwoButtonsWithListeners(TranscCompletedActivity.this, "", summaryResponse.getMessage(),
                                        getString(R.string.retry),
                                        getString(R.string.cancel),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                apiGetTransactionSummary(orderId, txnType, transactionInfo);
                                            }
                                        },
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                okBtnClicked();
                                            }
                                        }
                                        , false, false);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            retryDialogGetTransactionSummary(DialogErrorType.SERVER_ERROR, orderId, txnType, transactionInfo);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        CallProgressWheel.dismissLoadingDialog();
                        retryDialogGetTransactionSummary(DialogErrorType.CONNECTION_LOST, orderId, txnType, transactionInfo);
                    }
                });
            } else {
                retryDialogGetTransactionSummary(DialogErrorType.NO_NET, orderId, txnType, transactionInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialogGetTransactionSummary(DialogErrorType dialogErrorType, final int orderId, final int txnType,
                                                  final TransactionInfo transactionInfo){
        DialogPopup.dialogNoInternet(this,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        apiGetTransactionSummary(orderId, txnType, transactionInfo);
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
