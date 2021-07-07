package product.clicklabs.jugnoo.offers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.BaseActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.offers.historyadapter.HistoryRecyclerViewAdapter;
import product.clicklabs.jugnoo.offers.historyadapter.OfferTransactionRecyclerViewAdapter;
import product.clicklabs.jugnoo.offers.model.AirtimeHistory;
import product.clicklabs.jugnoo.offers.model.OfferTransaction;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OfferTransactionActivity extends BaseActivity {

    private ImageView imageViewBack;
    OfferTransactionRecyclerViewAdapter offerTransactionRecyclerViewAdapter;
    RecyclerView historyRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_offer_transaction);

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        historyRecyclerView = (RecyclerView) findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        //will be used to retrieve delivery items from backend
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
        params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
        params.put("integrated", "1");
        new HomeUtil().putDefaultParams(params);

        RestClient.getApiService().offerTransaction(params, new Callback<OfferTransaction>() {
            @Override
            public void success(OfferTransaction offerTransaction, Response response) {
                offerTransactionRecyclerViewAdapter = new OfferTransactionRecyclerViewAdapter(offerTransaction.getMessage());
                historyRecyclerView.setAdapter(offerTransactionRecyclerViewAdapter);
                offerTransactionRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Error", error.toString());
            }
        });
    }

    public void runUssd(String ussd){
        String ussdCode = "tel:" + ussd + Uri.encode("#");
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(ussdCode));
        try{
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        Integer.parseInt("123"));
            } else {
                startActivity(intent);
            }
        } catch (SecurityException e){
            e.printStackTrace();
        }
    }
}
