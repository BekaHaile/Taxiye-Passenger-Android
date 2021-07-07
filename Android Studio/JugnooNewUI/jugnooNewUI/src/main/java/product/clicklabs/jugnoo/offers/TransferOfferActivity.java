package product.clicklabs.jugnoo.offers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import product.clicklabs.jugnoo.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TransferOfferActivity extends BaseActivity {

    private EditText etCredits, etPhoneNumber;
    private Button btSendCredits;
    private ImageView ivBackButton;
    private ProgressDialog progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_offer_transfer);

        etCredits = (EditText) findViewById(R.id.etCredits);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if (s.startsWith("0")) {
                    if (s.length() > 1) {
                        etPhoneNumber.setText(s.toString().substring(1));
                    } else {
                        etPhoneNumber.setText("");
                    }
                    Toast.makeText(TransferOfferActivity.this, getApplication().getString(R.string.number_should_not_start_with_zero), Toast.LENGTH_SHORT).show();
                }
            }
        });


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
                showLoadingDialog();
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_CLIENT_ID, Config.getAutosClientId());
                params.put(Constants.KEY_IS_ACCESS_TOKEN_NEW, "1");
                params.put("integrated", "1");
                new HomeUtil().putDefaultParams(params);

                params.put("amount", String.valueOf(etCredits.getText()));
                params.put("phone_number", String.valueOf("+251" + etPhoneNumber.getText()));

                RestClient.getApiService().offerTransfer(params, new Callback<OfferTransfer>() {
                    @Override
                    public void success(OfferTransfer offerTransfer, Response response) {
                        dismissLoadingDialog();
                        if(offerTransfer.getFlag() == 1) {
                            successMessage();
                        }
                        else Toast.makeText(TransferOfferActivity.this, offerTransfer.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("Error: ", error.toString());
                        dismissLoadingDialog();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dismissLoadingDialog();
    }

    public void showLoadingDialog() {
        if (progress == null) {
            progress = new ProgressDialog(this);
            progress.setMessage(getString(R.string.loading));
        }
        progress.show();
    }

    public void dismissLoadingDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public void successMessage() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(TransferOfferActivity.this);
        builder1.setTitle(getString(R.string.successful));
        builder1.setMessage(getString(R.string.points_transferred));
        builder1.setCancelable(true);

        builder1.setNegativeButton(
                R.string.close,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        etCredits.setText("");
                        etPhoneNumber.setText("");
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
