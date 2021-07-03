package product.clicklabs.jugnoo.offers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.BaseActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.credits.SendCreditsToCustomer;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.offers.historyadapter.HistoryRecyclerViewAdapter;
import product.clicklabs.jugnoo.offers.model.AirtimeHistory;
import product.clicklabs.jugnoo.offers.model.OfferMenu;
import product.clicklabs.jugnoo.promotion.PromotionActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OffersActivity extends BaseActivity {

    private TextView balance;
    private ImageView imageViewBack;
    private CardView buyAirtime, promotions, airtimeHistory, transfer;
    private double balanceValue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_offers);

        balance = (TextView) findViewById(R.id.balance);

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        buyAirtime = (CardView) findViewById(R.id.buyAirtime);
        buyAirtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OffersActivity.this, BuyAirtimeActivity.class);
                intent.putExtra("wallet_balance", balanceValue);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        promotions = (CardView) findViewById(R.id.promotions);
        promotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OffersActivity.this, PromotionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        transfer = (CardView) findViewById(R.id.transfer);
        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OffersActivity.this, SendCreditsToCustomer.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        airtimeHistory = (CardView) findViewById(R.id.airtimeHistory);
        airtimeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OffersActivity.this, AirtimeHistoryActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        setOptions();
    }

    public void setOptions(){
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
        params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
        params.put("integrated", "1");
        new HomeUtil().putDefaultParams(params);


        RestClient.getApiService().offers(params, new Callback<OfferMenu>() {
            @Override
            public void success(OfferMenu offerMenu, Response response) {
                if(!offerMenu.getAirtime()){
                    buyAirtime.setVisibility(View.GONE);
                    airtimeHistory.setVisibility(View.GONE);
                }
                if(!offerMenu.getTransfer()) transfer.setVisibility(View.GONE);
                if(!offerMenu.getPromotions()) promotions.setVisibility(View.GONE);

                balanceValue = offerMenu.getWalletBalance();
                balance.setText(String.valueOf(offerMenu.getWalletBalance()));
            }

            @Override
            public void failure(RetrofitError error){
                Log.e("Error", error.toString());
            }
        });
    }
}
