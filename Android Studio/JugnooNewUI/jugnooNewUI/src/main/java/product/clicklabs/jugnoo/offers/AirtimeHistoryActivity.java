package product.clicklabs.jugnoo.offers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import product.clicklabs.jugnoo.offers.model.AirtimeHistory;
import product.clicklabs.jugnoo.retrofit.ApiService;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AirtimeHistoryActivity  extends BaseActivity {

    private ImageView imageViewBack;
    HistoryRecyclerViewAdapter historyRecyclerViewAdapter;
    RecyclerView historyRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_airtime_history);

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

        RestClient.getApiService().airtimeHistory(params, new Callback<ArrayList<AirtimeHistory>>() {
            @Override
            public void success(ArrayList<AirtimeHistory> deliveryItems, Response response) {

                historyRecyclerViewAdapter = new HistoryRecyclerViewAdapter(deliveryItems);
                historyRecyclerViewAdapter.setActions(new HistoryRecyclerViewAdapter.Actions() {
                    @Override
                    public void onCall(AirtimeHistory airtimeHistory) {
                        //call the ussd
                        openDialer("*805*" + airtimeHistory.getVoucherNumber() + "#");
                    }
                });
                historyRecyclerView.setAdapter(historyRecyclerViewAdapter);
                historyRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Error", error.toString());
            }
        });
    }

    public void openDialer(String ussd){
        String ussdCode = "tel:" + ussd;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(ussdCode));
        startActivity(intent);
    }
}
