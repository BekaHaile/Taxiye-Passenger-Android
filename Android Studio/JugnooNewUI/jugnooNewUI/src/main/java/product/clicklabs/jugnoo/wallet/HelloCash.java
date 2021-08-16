package product.clicklabs.jugnoo.wallet;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.country.picker.Country;
import com.country.picker.CountryPicker;
import com.country.picker.OnCountryPickerListener;

import java.util.HashMap;

import product.clicklabs.jugnoo.BaseActivity;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.wallet.models.HelloCashCashoutResponse;
import product.clicklabs.jugnoo.wallet.models.ResponseModel;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HelloCash extends BaseActivity implements OnCountryPickerListener {

    Button buttonDone, fiftyButton, hundredButton, hundredFiftyButton;
    ImageView backBtn;
    EditText editAmount, edtPhoneNo;
    String currency = Data.autoData.getCurrency() + " %s";

    private TextView tvCountryCode;
    private CountryPicker countryPicker;
    boolean isTopUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hellocash_top_up);

        TextView title = (TextView) findViewById(R.id.textViewTitle);
        title.setTypeface(Fonts.mavenRegular(this));

        editAmount = (EditText) findViewById(R.id.editAmount);

        edtPhoneNo = (EditText) findViewById(R.id.edtPhoneNo);
        edtPhoneNo.setText(Data.userData.phoneNo.substring(4));
        edtPhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().startsWith("0")) {
                    if (editable.toString().length() > 1) {
                        edtPhoneNo.setText(editable.toString().substring(1));
                    } else {
                        edtPhoneNo.setText("");
                    }
                    Toast.makeText(HelloCash.this, HelloCash.this.getString(R.string.number_should_not_start_with_zero), Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvCountryCode = (TextView) findViewById(R.id.tvCountryCode);
        tvCountryCode.setText(Utils.getCountryCode(this));
        tvCountryCode.setOnClickListener(countryCodeClickListener);
        countryPicker =
                new CountryPicker.Builder().with(this)
                        .listener(this)
                        .build();

        fiftyButton = (Button) findViewById(R.id.fiftyButton);
        fiftyButton.setText(String.format(currency, "50"));
        fiftyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAmount.setText("50");
            }
        });

        hundredButton = (Button) findViewById(R.id.hundredButton);
        hundredButton.setText(String.format(currency, "100"));
        hundredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAmount.setText("100");
            }
        });

        hundredFiftyButton = (Button) findViewById(R.id.hundredFiftyButton);
        hundredFiftyButton.setText(String.format(currency, "300"));
        hundredFiftyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAmount.setText("300");
            }
        });

        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"Lucy", "Walya", "Axum"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        buttonDone = (Button) findViewById(R.id.btn_done);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTopUp = Prefs.with(getApplicationContext()).getBoolean("isTopUp", false);
                if(isTopUp){
                    HashMap<String, String> params = new HashMap<>();
                    params.put("payment_method", "HELLO-CASH");
                    params.put("user_id", Data.userData.getUserId());
                    params.put("amount", editAmount.getText().toString());
                    params.put("access_token", Data.userData.accessToken);
                    params.put("user_type", "CUSTOMER");

                    params.put("phone_no", edtPhoneNo.getText().toString());
                    params.put("system", dropdown.getSelectedItem().toString());

                    RestClient.getApiService().helloCashTopUp(params, new Callback<ResponseModel<HelloCashCashoutResponse>>() {
                        @Override
                        public void success(ResponseModel<HelloCashCashoutResponse> helloCashCashoutResponse, Response response) {
                            buildDialog(isTopUp, helloCashCashoutResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("Error", "Top up error");

                            Toast.makeText(HelloCash.this, HelloCash.this.getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    HashMap<String, String> params = new HashMap<>();
                    params.put("payment_method", "HELLO-CASH");
                    params.put("access_token", Data.userData.accessToken);
                    params.put("user_id", Data.userData.getUserId());
                    params.put("amount", editAmount.getText().toString());
                        params.put("user_type", "CUSTOMER");

                    params.put("phone_no", edtPhoneNo.getText().toString());
                    params.put("system", dropdown.getSelectedItem().toString());

                    RestClient.getApiService().helloCashCashout(params, new Callback<ResponseModel<HelloCashCashoutResponse>>() {
                        @Override
                        public void success(ResponseModel<HelloCashCashoutResponse> helloCashCashoutResponse, Response response) {
                            Toast.makeText(HelloCash.this, HelloCash.this.getString(R.string.successful), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("Error", "Cash out error");

                            Toast.makeText(HelloCash.this, HelloCash.this.getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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

    View.OnClickListener countryCodeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            countryPicker.showDialog(getSupportFragmentManager());
        }
    };

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

    public void buildDialog(boolean isTopUp, ResponseModel<HelloCashCashoutResponse> helloCashCashoutResponse){
        Dialog dialog = new Dialog(HelloCash.this);

        LayoutInflater layoutInflater = LayoutInflater.from(HelloCash.this);
        View dialogView = layoutInflater.inflate(R.layout.cbe_dialogue, null);

        dialog.setContentView(dialogView);


        String id = String.valueOf(helloCashCashoutResponse.getData().getId());
        String code = String.valueOf(helloCashCashoutResponse.getData().getCode());
        String amount = helloCashCashoutResponse.getData().getAmount();
        String description = helloCashCashoutResponse.getData().getDescription();

        TextView stepsDialogue = (TextView) dialogView.findViewById(R.id.stepsDialogue);
        stepsDialogue.setText(getString(R.string.hello_cash_instruction, id, code, amount, description));

        Button btnDone = (Button) dialogView.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runUssd("*912#");
            }
        });

        Button btn_cancel = (Button)  dialogView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
//                CbeTopUp.super.onBackPressed();
            }
        });
        dialog.show();
    }

    @Override
    public void onSelectCountry(Country country) {
        tvCountryCode.setText(country.getDialCode());
    }
}
