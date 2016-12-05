package com.jugnoo.pay.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jugnoo.pay.models.CommonResponse;
import com.jugnoo.pay.models.MessageRequest;
import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.models.SendMoneyCallback;
import com.jugnoo.pay.models.SendMoneyRequest;
import com.jugnoo.pay.retrofit.RetrofitClient;
import com.jugnoo.pay.retrofit.WebApi;
import com.jugnoo.pay.utils.ApiResponseFlags;
import com.jugnoo.pay.utils.AppConstants;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.SingleButtonAlert;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;
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

    @Bind(R.id.date_txt)
    TextView dateTxt;

    @Bind(R.id.paid_txt)
    TextView textViewPaid;

    @Bind(R.id.amount_txt)
    TextView amountTxt;
    @Bind(R.id.transaction_id_txt)
    TextView trnscIdTxt;
    @Bind(R.id.trnsc_status_txt)
    TextView trnscStatusTxt;
    @Bind(R.id.message_txt)
    TextView msgTxt;
    @Bind(R.id.message)
    TextView textViewMessage;

    @Bind(R.id.status_layout)
    RelativeLayout statusLayout;

    @Bind(R.id.contact_name_txt)
    TextView contactNameTxt;
    @Bind(R.id.mobile_txt)
    TextView mobileTxt;
    @Bind(R.id.contact_image)
    ImageView contactImg;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            setContentView(R.layout.activity_transaction_completed);
            ButterKnife.bind(this);
            toolbarTitleTxt.setText(R.string.transc_completed_screen);
            mToolBar.setTitle("");
            setSupportActionBar(mToolBar);
            contactDetails = (SelectUser) getIntent().getExtras().getParcelable(AppConstants.CONTACT_DATA);
            requestObj = (SendMoneyRequest)getIntent().getSerializableExtra(AppConstants.TRANSACTION_DATA);
            orderId = getIntent().getStringExtra(AppConstants.ORDER_ID);
            if(getIntent().hasExtra(AppConstants.TRANSACTION_STATUS)){
                transactionStatus = getIntent().getStringExtra(AppConstants.TRANSACTION_STATUS);
            }


            SendMoneyCallback callback = (SendMoneyCallback) getIntent().getExtras().getSerializable(AppConstants.SEND_TRANSACTION_DATA);
            setData();

            if(callback!=null) {
                // for send
                callingSendMoneyCallbackApi(callback.getMessage(), "", callback.getAccess_token());
                textViewPaid.setText(getResources().getString(R.string.paid_string));
            } else if(transactionStatus.equalsIgnoreCase("Failed")){
                trnscStatusTxt.setText(getResources().getString(R.string.transaction_failed_string));
                statusLayout.setBackgroundColor(getResources().getColor(R.color.booking_failed_color));
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

        dateTxt.setText(formattedDate);
        amountTxt.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space), requestObj.getAmount()));
        trnscIdTxt.setText(orderId);
        if((requestObj != null) && !requestObj.getMessage().equalsIgnoreCase("")) {
            msgTxt.setVisibility(View.VISIBLE);
            textViewMessage.setVisibility(View.VISIBLE);
            msgTxt.setText(requestObj.getMessage());
        } else{
            msgTxt.setVisibility(View.GONE);
            textViewMessage.setVisibility(View.GONE);
        }
        mobileTxt.setText(contactDetails.getPhone()+" Mobile");
        contactNameTxt.setText(contactDetails.getName());
        // Set image if exists
        try {

            if (contactDetails.getThumb() != null) {
                //contactImg.setImageBitmap(contactDetails.getThumb());
                Picasso.with(TranscCompletedActivity.this).load(contactDetails.getThumb()).into(contactImg);
            } else {
                contactImg.setImageResource(R.drawable.icon_user);
            }
            // Seting round image
//            Bitmap bm = BitmapFactory.decodeResource(holder.contactImage.getResources(), R.drawable.icon_logo); // Load default image
//            roundedImage = new RoundImage(bm);
//            v.imageView.setImageDrawable(roundedImage);
        } catch (Exception e) {
            // Add default picture
            contactImg.setImageResource(R.drawable.icon_user);
            e.printStackTrace();
        }

    }


    // used to send the  money
    private void callingSendMoneyCallbackApi(MessageRequest message, String orderId, String accessToken) {

        try
        {
            CallProgressWheel.showLoadingDialog(TranscCompletedActivity.this, AppConstants.PLEASE);
            HashMap<String, String> params = new HashMap<>();

            params.put("order_id", orderId);
            params.put("access_token",  accessToken);
            if(message != null) {
                params.put("message", message.toString());
            }
            WebApi mWebApi = RetrofitClient.createService(WebApi.class);
            mWebApi.sendMoneyCallback(params, new Callback<CommonResponse>() {
                @Override
                public void success(CommonResponse commonResponse, Response response) {
                    System.out.println("SendMoneyActivity.success22222222");
                    CallProgressWheel.dismissLoadingDialog();
                    if (commonResponse != null) {
//                    Prefs.with(SignUpActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getToken());
//
                        int flag = commonResponse.getFlag();
                        if(flag == ApiResponseFlags.TXN_COMPLETED.getOrdinal()) {
                            trnscStatusTxt.setText(commonResponse.getMessage());
                            statusLayout.setBackgroundColor(getResources().getColor(R.color.booking_completed_color));
                        }
                        else if(flag == ApiResponseFlags.TXN_FAILED.getOrdinal()) {
                            trnscStatusTxt.setText(commonResponse.getMessage());
                            statusLayout.setBackgroundColor(getResources().getColor(R.color.booking_failed_color));
                        }
                        else {
                            CommonMethods.callingBadToken(TranscCompletedActivity.this,flag,commonResponse.getMessage());
                            trnscStatusTxt.setText(commonResponse.getMessage());
                            statusLayout.setBackgroundColor(getResources().getColor(R.color.booking_failed_color));

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
                            SingleButtonAlert.showAlert(TranscCompletedActivity.this, jsonObject.getString("message"), AppConstants.OK);
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
