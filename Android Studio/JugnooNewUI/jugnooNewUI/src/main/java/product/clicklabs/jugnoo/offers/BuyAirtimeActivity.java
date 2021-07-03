package product.clicklabs.jugnoo.offers;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashMap;

import product.clicklabs.jugnoo.BaseActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.offers.model.BuyAirtime;
import product.clicklabs.jugnoo.offers.model.OfferMenu;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BuyAirtimeActivity extends BaseActivity {

    private ImageView imageViewBack;
    private CardView buyAirtime1, buyAirtime2;
    private TextView balance;
    private double balanceValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_buy_airtime);

        balanceValue = getIntent().getDoubleExtra("wallet_balance",0);
        balance = (TextView) findViewById(R.id.balance);
        balance.setText(String.valueOf(balanceValue));

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        buyAirtime1 = (CardView) findViewById(R.id.buyAirtime1);
        buyAirtime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(balanceValue < 50.00 )
                    Toast.makeText(BuyAirtimeActivity.this, getString(R.string.low_balance), Toast.LENGTH_SHORT).show();
                else
                    buyAirtime("50");
            }
        });
        buyAirtime2 = (CardView) findViewById(R.id.buyAirtime2);
        buyAirtime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(balanceValue < 100.00 )
                    Toast.makeText(BuyAirtimeActivity.this, getString(R.string.low_balance), Toast.LENGTH_SHORT).show();
                else
                buyAirtime("100");
            }
        });

    }

    public void buyAirtime(String amount){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(BuyAirtimeActivity.this);
        builder1.setMessage(getString(R.string.are_you_sure_you_want_to_buy_airtime, amount));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HashMap<String, String> params = new HashMap<>();params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                        params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
                        params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
                        params.put("integrated", "1");
                        params.put("airtime_amount", amount);

                        new HomeUtil().putDefaultParams(params);


                        RestClient.getApiService().buyAirtime(params, new Callback<BuyAirtime>() {
                            @Override
                            public void success(BuyAirtime buyAirtime, Response response) {
                                //call ussd *805*voucher#
                                runUssd("*805*" + buyAirtime.getVoucherNumber());
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.e("Error", error.toString());
                            }
                        });
                    }
                });

        builder1.setNegativeButton(
                R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
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
