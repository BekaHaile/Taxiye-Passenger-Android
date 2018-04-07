package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class ChangePhoneBeforeOTPActivity extends BaseActivity implements Constants {

    private final String TAG = ChangePhoneBeforeOTPActivity.class.getSimpleName();

	ImageView imageViewBack;
	TextView textViewTitle;
	
	TextView textViewChangePhoneNoHelp;
	EditText editTextNewPhoneNumber;
	Button buttonChangePhoneNumber;
    private int linkedWallet;
	
	RelativeLayout relative;

	@Override
	protected void onStart() {
		super.onStart();
//		FlurryAgent.init(this, Config.getFlurryKey());
//		FlurryAgent.onStartSession(this, Config.getFlurryKey());
	}

	@Override
	protected void onStop() {
		super.onStop();
//		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_phone_before_verify);

        if(getIntent().hasExtra(LINKED_WALLET)){
            linkedWallet = getIntent().getIntExtra(LINKED_WALLET, 0);
        }
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(ChangePhoneBeforeOTPActivity.this, relative, 1134, 720, false);
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));

        textViewChangePhoneNoHelp = (TextView) findViewById(R.id.textViewChangePhoneNoHelp); textViewChangePhoneNoHelp.setTypeface(Fonts.mavenRegular(this));
        editTextNewPhoneNumber = (EditText) findViewById(R.id.editTextNewPhoneNumber); editTextNewPhoneNumber.setTypeface(Fonts.mavenMedium(this));
        buttonChangePhoneNumber = (Button) findViewById(R.id.buttonChangePhoneNumber); buttonChangePhoneNumber.setTypeface(Fonts.mavenRegular(this));

        ((TextView)findViewById(R.id.textViewPhone91)).setTypeface(Fonts.mavenMedium(this));

        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

		imageViewBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed(false);
			}
		});

        editTextNewPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
                editTextNewPhoneNumber.setError(null);
			}
		});


        buttonChangePhoneNumber.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					String previousPhoneNumber = "", accessToken = "";
					if(SplashNewActivity.RegisterationType.FACEBOOK == SplashNewActivity.registerationType){
						previousPhoneNumber = OTPConfirmScreen.facebookRegisterData.phoneNo;
						accessToken = OTPConfirmScreen.facebookRegisterData.accessToken;
					}
					else if(SplashNewActivity.RegisterationType.GOOGLE == SplashNewActivity.registerationType){
						previousPhoneNumber = OTPConfirmScreen.googleRegisterData.phoneNo;
						accessToken = OTPConfirmScreen.googleRegisterData.accessToken;
					}
					else{
						previousPhoneNumber = OTPConfirmScreen.emailRegisterData.phoneNo;
						accessToken = OTPConfirmScreen.emailRegisterData.accessToken;
					}

					String phoneNoChanged = editTextNewPhoneNumber.getText().toString().trim();
					if("".equalsIgnoreCase(phoneNoChanged)){
						editTextNewPhoneNumber.requestFocus();
						editTextNewPhoneNumber.setError(getString(R.string.phone_number_cant_be_empty));
					}
					else{
						phoneNoChanged = Utils.retrievePhoneNumberTenChars(phoneNoChanged,
								Utils.getCountryCode(ChangePhoneBeforeOTPActivity.this));
						if(Utils.validPhoneNumber(phoneNoChanged)) {
							phoneNoChanged = "+91" + phoneNoChanged;
							if(previousPhoneNumber.equalsIgnoreCase(phoneNoChanged)){
								editTextNewPhoneNumber.requestFocus();
								editTextNewPhoneNumber.setError(getString(R.string.changed_phone_number_same));
							}
							else{
								updateUserProfileAPI(ChangePhoneBeforeOTPActivity.this, phoneNoChanged, accessToken);
							}
						}
						else{
							editTextNewPhoneNumber.requestFocus();
							editTextNewPhoneNumber.setError(getString(R.string.please_enter_valid_phone));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

        editTextNewPhoneNumber.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
                        buttonChangePhoneNumber.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});
		
		

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}



    public void updateUserProfileAPI(final Activity activity, final String updatedField, String accessToken) {
        if(MyApplication.getInstance().isOnline()) {

            DialogPopup.showLoadingDialog(activity, getString(R.string.updating));

            HashMap<String, String> params = new HashMap<>();
            params.put("client_id", Config.getAutosClientId());
            params.put("access_token", accessToken);
            params.put("is_access_token_new", "1");
            params.put("updated_phone_no", updatedField);
            params.put("reg_wallet_type", String.valueOf(linkedWallet));

			new HomeUtil().putDefaultParams(params);
            RestClient.getApiService().updateUserProfile(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
                    Log.i(TAG, "updateUserProfile response = " + responseStr);
                    DialogPopup.dismissLoadingDialog();
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
                            int flag = jObj.getInt("flag");
                            if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
                                String error = jObj.getString("error");
                                DialogPopup.alertPopup(activity, "", error);
                            }
                            else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
                                String message = jObj.getString("message");
                                if(SplashNewActivity.RegisterationType.FACEBOOK == SplashNewActivity.registerationType){
                                    OTPConfirmScreen.facebookRegisterData.phoneNo = updatedField;
                                }
                                else if(SplashNewActivity.RegisterationType.GOOGLE == SplashNewActivity.registerationType){
                                    OTPConfirmScreen.googleRegisterData.phoneNo = updatedField;
                                }
                                else{
                                    OTPConfirmScreen.emailRegisterData.phoneNo = updatedField;
                                }
                                DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        performBackPressed(true);
                                    }
                                });
                            }
                            else{
                                DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                            }
                        }
                    }  catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                        DialogPopup.dismissLoadingDialog();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("request fail", error.toString());
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
                }
            });
        }
        else {
            DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
        }

    }


	
	@Override
	public void onBackPressed() {
		performBackPressed(false);
		super.onBackPressed();
	}
	
	
	public void performBackPressed(boolean showTimer){
        Intent intent = new Intent(ChangePhoneBeforeOTPActivity.this, OTPConfirmScreen.class);
        intent.putExtra(LINKED_WALLET, linkedWallet);
		if(showTimer){
			intent.putExtra("show_timer", 1);
		}
        startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
}