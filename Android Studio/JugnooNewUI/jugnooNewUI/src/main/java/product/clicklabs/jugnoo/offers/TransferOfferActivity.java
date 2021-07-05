package product.clicklabs.jugnoo.offers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.BaseActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.offers.model.AirtimeHistory;
import product.clicklabs.jugnoo.offers.model.OfferTransfer;
import product.clicklabs.jugnoo.retrofit.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TransferOfferActivity extends BaseActivity {

    EditText etCredits, etPhoneNumber;
    Button btSendCredits;
    ImageView ivBackButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_offer_transfer);

        etCredits = (EditText) findViewById(R.id.etCredits);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);

        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        btSendCredits = (Button) findViewById(R.id.btSendCredits);
        btSendCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
                params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
                params.put("integrated", "1");
                new HomeUtil().putDefaultParams(params);

                params.put("amount", String.valueOf(etCredits.getText()));
                params.put("phone_number", String.valueOf(etPhoneNumber.getText()));

                RestClient.getApiService().offerTransfer(params, new Callback<OfferTransfer>() {
                    @Override
                    public void success(OfferTransfer offerTransfer, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        });
    }
}
