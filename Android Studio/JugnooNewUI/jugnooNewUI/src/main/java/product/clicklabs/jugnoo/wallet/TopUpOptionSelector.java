package product.clicklabs.jugnoo.wallet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import product.clicklabs.jugnoo.BaseActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.wallet.models.PaymentConfig;
import product.clicklabs.jugnoo.wallet.models.WalletEnabledPayments;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopUpOptionSelector extends BaseActivity {

    TextView helloCashOption;
    ImageView backBtn;
    boolean isTopUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wallet_top_up);

        TextView title = (TextView) findViewById(R.id.textViewTitle);
        title.setTypeface(Fonts.avenirNext(TopUpOptionSelector.this));

        isTopUp = Prefs.with(this).getBoolean("isTopUp", false);
        if(isTopUp)
            title.setText(R.string.top_up);
        else title.setText(R.string.cash_out);

//        cbeBirrOption = (TextView) findViewById(R.id.cbeBirrOption);
//        cbeBirrOption.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openCbeTopUpActivity();
//            }
//        });
//
//        mpesaOption = (TextView) findViewById(R.id.mpesaOption);
//        mpesaOption.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openMpesaTopUpActivity();
//            }
//        });

        helloCashOption = (TextView) findViewById(R.id.helloCashOption);
        helloCashOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHelloCashTopUpActivity();
            }
        });

        backBtn = (ImageView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });
    }

//    public void openCbeTopUpActivity(){
//        startActivity(new Intent(product.clicklabs.jugnoo.driver.wallet.TopUpOptionSelector.this, CbeTopUp.class));
//        overridePendingTransition(R.anim.right_in, R.anim.right_out);
//    }
//
//    public void openMpesaTopUpActivity(){
//        startActivity(new Intent(product.clicklabs.jugnoo.driver.wallet.TopUpOptionSelector.this, MpesaTopUp.class));
//        overridePendingTransition(R.anim.right_in, R.anim.right_out);
//    }

    public void openHelloCashTopUpActivity(){
        startActivity(new Intent(TopUpOptionSelector.this, HelloCash.class));
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean helloCashEnabled = Prefs.with(this).getBoolean("helloCashEnabled", true);
        if(helloCashEnabled){
            helloCashOption.setVisibility(View.VISIBLE);
        }
        else{
            helloCashOption.setVisibility(View.GONE);
        }
    }
}


