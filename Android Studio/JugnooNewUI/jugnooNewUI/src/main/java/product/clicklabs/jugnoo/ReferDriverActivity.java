package product.clicklabs.jugnoo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashMap;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class ReferDriverActivity extends BaseActivity  {

    RelativeLayout relative;

    TextView textViewTitle;
    ImageView imageViewBack;

    ScrollView scrollView;
    LinearLayout linearLayoutMain;
    TextView textViewScroll;
    EditText editTextName, editTextPhone;
    Button buttonRefer;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_driver);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);

        ((TextView) findViewById(R.id.textViewCameAcross)).setTypeface(Fonts.mavenRegular(this));
        ((TextView) findViewById(R.id.textViewKindlyRecommend)).setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewIfTheyAreInterested)).setTypeface(Fonts.mavenRegular(this));
        textViewScroll = (TextView) findViewById(R.id.textViewScroll);

        editTextName = (EditText) findViewById(R.id.editTextName); editTextName.setTypeface(Fonts.mavenLight(this));
        editTextPhone = (EditText) findViewById(R.id.editTextPhone); editTextPhone.setTypeface(Fonts.mavenLight(this));
        buttonRefer = (Button) findViewById(R.id.buttonRefer); buttonRefer.setTypeface(Fonts.mavenRegular(this));

        textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_REFER_A_DRIVER);
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

        buttonRefer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!editTextName.getText().toString().isEmpty()) && (!editTextPhone.getText().toString().isEmpty())){
                    if(Utils.validPhoneNumber(editTextPhone.getText().toString())){
                        referDriver();
                    }else {
                        editTextPhone.requestFocus();
                        editTextPhone.setError(getString(R.string.invalid_number));
                        editTextName.setError(null);
                    }
                }else{
                    if(editTextName.getText().toString().isEmpty()){
                        editTextName.requestFocus();
                        editTextName.setError(getString(R.string.name_is_required));
                        editTextPhone.setError(null);
                    }else if(editTextPhone.getText().toString().isEmpty()){
                        editTextPhone.requestFocus();
                        editTextPhone.setError(getString(R.string.enter_contact_number));
                        editTextName.setError(null);
                    }else{
                        editTextName.setError(getString(R.string.name_is_required));
                        editTextPhone.setError(null);
                    }

                }
            }
        });

        imageViewBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        KeyboardLayoutListener keyboardLayoutListener = new KeyboardLayoutListener(linearLayoutMain, textViewScroll,
                new KeyboardLayoutListener.KeyBoardStateHandler() {
                    @Override
                    public void keyboardOpened() {
                        scrollView.scrollTo(0, buttonRefer.getBottom());
                    }

                    @Override
                    public void keyBoardClosed() {

                    }
                });
        keyboardLayoutListener.setResizeTextView(false);
        linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }


    @Override
    protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
        super.onDestroy();
    }

    private void referDriver(){
        try {
            if(MyApplication.getInstance().isOnline()) {
                DialogPopup.showLoadingDialog(ReferDriverActivity.this, getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_REFER_DRIVER_NAME, editTextName.getText().toString());
                params.put(Constants.KEY_REFER_DRIVER_PHONE_NO, editTextPhone.getText().toString());
                Log.i("Refer Driver params=", "" + params.toString());

                new HomeUtil().putDefaultParams(params);
                RestClient.getApiService().referDriver(params, new Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt settleUserDebt, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.i("Refer Driver Response", "" + responseStr);
                        if(settleUserDebt.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
                            DialogPopup.alertPopupWithListener(ReferDriverActivity.this, "", settleUserDebt.getMessage(), new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    performBackPressed();
                                }
                            });
                        }else {
                            DialogPopup.alertPopup(ReferDriverActivity.this, "", settleUserDebt.getMessage());
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("Refer Driver error", ""+ error.toString());
                        DialogPopup.dismissLoadingDialog();
                        retryDialog(DialogErrorType.CONNECTION_LOST);
                    }
                });
            }
            else {
                retryDialog(DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType){
        DialogPopup.dialogNoInternet(ReferDriverActivity.this,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        referDriver();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                    }
                });
    }

}
