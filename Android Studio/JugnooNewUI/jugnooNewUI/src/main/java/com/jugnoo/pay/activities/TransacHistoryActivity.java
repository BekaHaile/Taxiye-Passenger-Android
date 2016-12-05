package com.jugnoo.pay.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jugnoo.pay.adapters.PendingTrnscAdapater;
import com.jugnoo.pay.adapters.TranscRecyslerAdapter;
import com.jugnoo.pay.models.AccessTokenRequest;
import com.jugnoo.pay.models.TransacHistoryResponse;
import com.jugnoo.pay.utils.AppConstants;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.SingleButtonAlert;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by cl-macmini-38 on 9/22/16.
 */
public class TransacHistoryActivity extends BaseActivity {
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

    @Bind(R.id.requests_recycler)
    RecyclerView recyclerView;
    private boolean pendingTranscStatus = false;
    private String accessToken;
    private LinearLayout linearLayoutNoTxn;
    private TextView textViewNoTxn;
    private ImageView imageViewNoTxn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            setContentView(R.layout.activity_transc_history);
            ButterKnife.bind(this);
            linearLayoutNoTxn = (LinearLayout)findViewById(R.id.linearLayoutNoTxn);
            textViewNoTxn = (TextView)findViewById(R.id.textViewNoTxn);
            imageViewNoTxn = (ImageView)findViewById(R.id.imageViewNoTxn);
            pendingTranscStatus = getIntent().getBooleanExtra(AppConstants.PENDING_TRANSACTION_STATUS, false);
            if(pendingTranscStatus) {
                toolbarTitleTxt.setText(R.string.pending_trnsc_screen);
                textViewNoTxn.setText(getResources().getString(R.string.no_pending_txn));
                imageViewNoTxn.setBackgroundResource(R.drawable.no_pending_txn);
            } else {
                toolbarTitleTxt.setText(R.string.trnsc_history_screen);
                textViewNoTxn.setText(getResources().getString(R.string.no_txn_currently));
                imageViewNoTxn.setBackgroundResource(R.drawable.no_txn_history);
            }
            mToolBar.setTitle("");
            setSupportActionBar(mToolBar);
            accessToken =  Data.userData.accessToken;        //Prefs.with(TransacHistoryActivity.this).getString(SharedPreferencesName.ACCESS_TOKEN, "");
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(TransacHistoryActivity.this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try
        {
            callingAllTranscHistoryApi();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public LinearLayout getLinearLayoutNoTxn() {
        return linearLayoutNoTxn;
    }

    // getting history of all transactions or all pending transactions
    private void callingAllTranscHistoryApi()
    {
        CallProgressWheel.showLoadingDialog(TransacHistoryActivity.this, "Please wait..");
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setAccess_token(accessToken);
        if(pendingTranscStatus)
            accessTokenRequest.setIs_pending(1);
        else
            accessTokenRequest.setIs_pending(0);

        RestClient.getPayApiService().getTranscHistory(accessTokenRequest,new Callback<TransacHistoryResponse>() {
            @Override
            public void success(TransacHistoryResponse transacHistoryResponse, Response response) {
                CallProgressWheel.dismissLoadingDialog();
                System.out.println("transacHistoryResponse.success");

                if (transacHistoryResponse != null) {
//                    Prefs.with(SignUpActivity.this).save(SharedPreferencesName.ACCESS_TOKEN, tokenGeneratedResponse.getToken());
//
                    int flag = transacHistoryResponse.getFlag();
                    if (flag == 143) {
                        if(pendingTranscStatus)
                        {
                            if(transacHistoryResponse.getTransactionHistory().size() > 0) {
                                linearLayoutNoTxn.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                PendingTrnscAdapater adapter = new PendingTrnscAdapater(TransacHistoryActivity.this, transacHistoryResponse.getTransactionHistory());
                                recyclerView.setAdapter(adapter);
                            } else{
                                linearLayoutNoTxn.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }
                        else {
                            if(transacHistoryResponse.getTransactionHistory().size() > 0) {
                                linearLayoutNoTxn.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                TranscRecyslerAdapter adapter = new TranscRecyslerAdapter(TransacHistoryActivity.this, transacHistoryResponse.getTransactionHistory());
                                recyclerView.setAdapter(adapter);
                            } else{
                                linearLayoutNoTxn.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }
                    }
                    else
                    {
                        CommonMethods.callingBadToken(TransacHistoryActivity.this,flag,transacHistoryResponse.getMessage());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                CallProgressWheel.dismissLoadingDialog();

                System.out.println("transacHistoryResponse.failure");
                try {

                    if (error.getKind().equals(RetrofitError.Kind.NETWORK))
                    {
//                        SingleButtonAlert.showAlert(HomeActivity.this,"No Internet Connection", "Ok");
                        showAlertNoInternet(TransacHistoryActivity.this);
                    }
                    else {

                        String json = new String(((TypedByteArray) error.getResponse()
                                .getBody()).getBytes());

                        JSONObject jsonObject = new JSONObject(json);

                        SingleButtonAlert.showAlert(TransacHistoryActivity.this, jsonObject.getString("message"), AppConstants.OK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }



}
