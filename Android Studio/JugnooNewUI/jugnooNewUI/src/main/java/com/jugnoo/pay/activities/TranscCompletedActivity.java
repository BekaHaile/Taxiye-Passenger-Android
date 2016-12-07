package com.jugnoo.pay.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.models.MessageRequest;
import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.models.SendMoneyCallback;
import com.jugnoo.pay.models.SendMoneyRequest;
import com.jugnoo.pay.utils.ApiResponseFlags;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.SingleButtonAlert;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Utils;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.Fonts;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

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
    private CardView cardViewDebitFrom;

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
            buttonOk.setTypeface(Fonts.mavenRegular(this));
            mobileTxt.setTypeface(Fonts.mavenRegular(this));
            contactNameTxt.setTypeface(Fonts.mavenMedium(this));
            textViewMessage.setTypeface(Fonts.mavenMedium(this));
            msgTxt.setTypeface(Fonts.mavenRegular(this));
            textViewPaid.setTypeface(Fonts.mavenRegular(this));
            imageViewCall = (ImageView) findViewById(R.id.imageViewCall);

            SendMoneyCallback callback = (SendMoneyCallback) getIntent().getExtras().getSerializable(AppConstant.SEND_TRANSACTION_DATA);
            setData();

            if(callback!=null) {
                // for send
                callingSendMoneyCallbackApi(callback.getMessage(), "", callback.getAccess_token());
                textViewPaid.setText(getResources().getString(R.string.paid_string));
            } else if(transactionStatus.equalsIgnoreCase("Failed")){
                tvTransStatusVal.setText(getString(R.string.failed));
                tvTransStatusVal.setTextColor(getResources().getColor(R.color.red_status));
                callingSendMoneyCallbackApi(null, requestObj.getOrderId(), requestObj.getAccess_token());
            } else{
                // for Request
                toolbarTitleTxt.setText("Jugnoo Pay");
                textViewPaid.setText(getResources().getString(R.string.requested_to));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setData()
    {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        tvTransTimeVal.setText(formattedDate);
        textViewDebitValue.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), requestObj.getAmount()));
        toolbarTitleTxt.setText(getResources().getString(R.string.transaction_id_number_format, orderId));
        if((requestObj != null) && !requestObj.getMessage().equalsIgnoreCase("")) {
            msgTxt.setVisibility(View.VISIBLE);
            textViewMessage.setVisibility(View.VISIBLE);
            msgTxt.setText(requestObj.getMessage());
        } else{
            msgTxt.setVisibility(View.GONE);
            textViewMessage.setVisibility(View.GONE);
        }
        mobileTxt.setText(contactDetails.getPhone());
        contactNameTxt.setText(contactDetails.getName());
        imageViewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openCallIntent(TranscCompletedActivity.this, contactDetails.getPhone());
            }
        });
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

    }


    // used to send the  money
    private void callingSendMoneyCallbackApi(MessageRequest message, String orderId, String accessToken) {

        try
        {
            CallProgressWheel.showLoadingDialog(TranscCompletedActivity.this, AppConstant.PLEASE);
            HashMap<String, String> params = new HashMap<>();

            params.put("order_id", orderId);
            params.put("access_token",  accessToken);
            if(message != null) {
                params.put("message", message.toString());
            }
            RestClient.getPayApiService().sendMoneyCallback(params, new Callback<CommonResponse>() {
                @Override
                public void success(CommonResponse commonResponse, Response response) {
                    System.out.println("SendMoneyActivity.success22222222");
                    CallProgressWheel.dismissLoadingDialog();
                    if (commonResponse != null) {
//                    Prefs.with(SignUpActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getToken());
//
                        int flag = commonResponse.getFlag();
                        if(flag == ApiResponseFlags.TXN_COMPLETED.getOrdinal()) {
                            tvTransStatusValMessage.setVisibility(View.GONE);
                            tvTransStatusValMessage.setText(commonResponse.getMessage());
                            tvTransStatusVal.setText(getString(R.string.successful));
                            tvTransStatusVal.setTextColor(getResources().getColor(R.color.green_rupee));
                            ivTransCompleted.setImageResource(R.drawable.ic_tick_copy);
                        }
                        else if(flag == ApiResponseFlags.TXN_FAILED.getOrdinal()) {
                            tvTransStatusValMessage.setVisibility(View.VISIBLE);
                            tvTransStatusValMessage.setTextColor(getResources().getColor(R.color.red_status));
                            tvTransStatusValMessage.setText(commonResponse.getMessage());
                            tvTransStatusVal.setText(getString(R.string.failed));
                            tvTransStatusVal.setTextColor(getResources().getColor(R.color.red_status));
                            ivTransCompleted.setImageResource(R.drawable.ic_failed);
                        }
                        else {
                            CommonMethods.callingBadToken(TranscCompletedActivity.this,flag,commonResponse.getMessage());
                            tvTransStatusValMessage.setVisibility(View.VISIBLE);
                            tvTransStatusValMessage.setTextColor(getResources().getColor(R.color.red_status));
                            tvTransStatusValMessage.setText(commonResponse.getMessage());
                            tvTransStatusVal.setText(getString(R.string.failed));
                            tvTransStatusVal.setTextColor(getResources().getColor(R.color.red_status));
                            ivTransCompleted.setImageResource(R.drawable.ic_failed);

                            // below methods can be used INSTEAD of .getColor method above
                            // getResources().getColor(R.color.booking_failed_color, null);
                            // ContextCompat.getColor(TranscCompletedActivity.this, R.color.booking_failed_color);
                        }

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    try {
                        System.out.println("SendMoneyActivity.failure2222222");
                        CallProgressWheel.dismissLoadingDialog();
                        if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                            showAlertNoInternet(TranscCompletedActivity.this);
                        } else {
                            String json = new String(((TypedByteArray) error.getResponse()
                                    .getBody()).getBytes());
                            JSONObject jsonObject = new JSONObject(json);
                            SingleButtonAlert.showAlert(TranscCompletedActivity.this, jsonObject.getString("message"), AppConstant.OK);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        CallProgressWheel.dismissLoadingDialog();
                    }

                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
