package com.jugnoo.pay.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.models.MessageRequest;
import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.models.SendMoneyCallback;
import com.jugnoo.pay.models.SendMoneyRequest;
import com.jugnoo.pay.models.TransacHistoryResponse;
import com.jugnoo.pay.utils.ApiResponseFlags;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Utils;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by cl-macmini-38 on 9/22/16.
 */
public class TranscCompletedActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitleTxt;
    @Bind(R.id.back_btn)
    ImageButton backBtn;

    @OnClick(R.id.back_btn)
    void backBtnClicked() {
        onBackPressed();
    }

    @Bind(R.id.paid_txt)
    TextView textViewPaid;

    @Bind(R.id.message_txt)
    TextView msgTxt;
    @Bind(R.id.message)
    TextView textViewMessage;

    @Bind(R.id.contact_name_txt)
    TextView contactNameTxt;
    @Bind(R.id.mobile_txt)
    TextView mobileTxt;
    @Bind(R.id.contact_image)
    ImageView contactImg;
    @Bind(R.id.ok_btn)
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

            try{
                finish();
            } catch (Exception e) { e.printStackTrace(); }

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

    private TextView tvTransStatusVal, tvTransStatusValMessage, tvTransTimeVal, tvBankRefIdVal, tvNpciTransIdVal,
            textViewAccountNumber, textViewBankName, textViewDebitValue;
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
            textViewAccountNumber = (TextView) findViewById(R.id.textViewAccountNumber); textViewAccountNumber.setTypeface(Fonts.mavenMedium(this));
            textViewBankName = (TextView) findViewById(R.id.textViewBankName); textViewBankName.setTypeface(Fonts.mavenMedium(this));
            textViewDebitValue = (TextView) findViewById(R.id.textViewDebitValue); textViewDebitValue.setTypeface(Fonts.mavenMedium(this));
            ivTransCompleted = (ImageView) findViewById(R.id.ivTransCompleted);
            imageViewBank = (ImageView) findViewById(R.id.imageViewBank);
            cardViewDebitFrom = (CardView) findViewById(R.id.cardViewDebitFrom);
            cardViewMessage = (CardView) findViewById(R.id.cardViewMessage);
            buttonOk.setTypeface(Fonts.mavenRegular(this));
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
            ((TextView)findViewById(R.id.textViewDebitFrom)).setTypeface(Fonts.mavenRegular(this));
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
                textViewPaid.setText(getResources().getString(R.string.paid_string));
            } else if(transactionStatus.equalsIgnoreCase("Failed")){
                tvTransStatusVal.setText(getString(R.string.failed));
                tvTransStatusVal.setTextColor(getResources().getColor(R.color.red_status));
                callingSendMoneyCallbackApi(null, requestObj.getOrderId(), requestObj.getAccess_token());
            }
            else if(getIntent().getIntExtra(Constants.KEY_FETCH_TRANSACTION_SUMMARY, 0) == 1){
                apiGetTransactionSummary(getIntent().getIntExtra(Constants.KEY_ORDER_ID, 0),
                        getIntent().getIntExtra(Constants.KEY_TXN_TYPE, TransacHistoryResponse.Type.REQUEST_BY_PENDING.getOrdinal()));
            }
            else{
                // for Request
                scrollView.setVisibility(View.VISIBLE);
                toolbarTitleTxt.setText("Jugnoo Pay");
                textViewPaid.setText(getResources().getString(R.string.requested_to));
                rvBankRefId.setVisibility(View.GONE);
                rvNpciTransId.setVisibility(View.GONE);
                cardViewDebitFrom.setVisibility(View.GONE);
                tvTransStatusValMessage.setVisibility(View.GONE);
                tvTransStatusVal.setText(getString(R.string.successful));
                tvTransStatusVal.setTextColor(getResources().getColor(R.color.green_rupee));
                ivTransCompleted.setImageResource(R.drawable.ic_tick_copy);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setData() {
        try {
            tvTransTimeVal.setText(DateOperations.convertDateViaFormat(DateOperations.getCurrentTime()));
            textViewDebitValue.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), requestObj.getAmount()));
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
            if (AppStatus.getInstance(this).isOnline(this)) {
                CallProgressWheel.showLoadingDialog(TranscCompletedActivity.this, AppConstant.PLEASE);
                HashMap<String, String> params = new HashMap<>();

                params.put("order_id", orderId);
                params.put("access_token", accessToken);
                if (message != null) {
                    params.put("message", message.toString());
                }
                RestClient.getPayApiService().sendMoneyCallback(params, new Callback<CommonResponse>() {
                    @Override
                    public void success(CommonResponse commonResponse, Response response) {
                        CallProgressWheel.dismissLoadingDialog();
                        try {
                            int flag = commonResponse.getFlag();
                            if (flag == ApiResponseFlags.TXN_COMPLETED.getOrdinal()) {
                                scrollView.setVisibility(View.VISIBLE);
								tvTransStatusValMessage.setVisibility(View.GONE);
								tvTransStatusValMessage.setText(commonResponse.getMessage());
								tvTransStatusVal.setText(getString(R.string.successful));
								tvTransStatusVal.setTextColor(getResources().getColor(R.color.green_rupee));
								ivTransCompleted.setImageResource(R.drawable.ic_tick_copy);
							}
                            else if (flag == ApiResponseFlags.TXN_FAILED.getOrdinal()) {
                                scrollView.setVisibility(View.VISIBLE);
								tvTransStatusValMessage.setVisibility(View.VISIBLE);
								tvTransStatusValMessage.setTextColor(getResources().getColor(R.color.red_status));
								tvTransStatusValMessage.setText(commonResponse.getMessage());
								tvTransStatusVal.setText(getString(R.string.failed));
								tvTransStatusVal.setTextColor(getResources().getColor(R.color.red_status));
								ivTransCompleted.setImageResource(R.drawable.ic_failed);
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


    public void apiGetTransactionSummary(final int orderId, final int txnType) {
        try {
            if (AppStatus.getInstance(this).isOnline(this)) {
                CallProgressWheel.showLoadingDialog(this, "Loading...");
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
                params.put(Constants.KEY_DEVICE_TYPE, Data.DEVICE_TYPE);
                params.put(Constants.KEY_ID, String.valueOf(orderId));
                params.put(Constants.KEY_TXN_TYPE, String.valueOf(txnType));

                RestClient.getPayApiService().getTransactionSummary(params, new Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt settleUserDebt, Response response) {
                        CallProgressWheel.dismissLoadingDialog();
                        try{
                            if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == settleUserDebt.getFlag()){
                                scrollView.setVisibility(View.VISIBLE);
                                textViewPaid.setText("");
                                tvTransTimeVal.setText("");
                                textViewDebitValue.setText("");
                                toolbarTitleTxt.setText("");
                                msgTxt.setText("");
                                mobileTxt.setText("");
                                contactNameTxt.setText("");
                                contactImg.setImageResource(R.drawable.icon_user);
                                tvTransStatusValMessage.setVisibility(View.GONE);
                                tvTransStatusValMessage.setText("");
                                tvTransStatusVal.setText(getString(R.string.successful));
                                tvTransStatusVal.setTextColor(getResources().getColor(R.color.green_rupee));
                                ivTransCompleted.setImageResource(R.drawable.ic_tick_copy);
                            } else {
                                DialogPopup.alertPopupTwoButtonsWithListeners(TranscCompletedActivity.this, "", settleUserDebt.getMessage(),
                                        getString(R.string.retry),
                                        getString(R.string.cancel),
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                apiGetTransactionSummary(orderId, txnType);
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
                            retryDialogGetTransactionSummary(DialogErrorType.SERVER_ERROR, orderId, txnType);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        CallProgressWheel.dismissLoadingDialog();
                        retryDialogGetTransactionSummary(DialogErrorType.CONNECTION_LOST, orderId, txnType);
                    }
                });
            } else {
                retryDialogGetTransactionSummary(DialogErrorType.NO_NET, orderId, txnType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialogGetTransactionSummary(DialogErrorType dialogErrorType, final int orderId, final int txnType){
        DialogPopup.dialogNoInternet(this,
                dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        apiGetTransactionSummary(orderId, txnType);
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
