package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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


public class RequestDuplicateRegistrationActivity extends BaseActivity {

	RelativeLayout relative;

	TextView textViewTitle;
	ImageView imageViewBack;

    TextView textViewRegisterNameValue, textViewRegisterEmailValue, textViewRegisterPhoneValue, textViewRegisterHelp;
    EditText editTextMessage;
    Button buttonSubmitRequest;

	ScrollView scrollView;
	TextView textViewScroll;


	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_duplicate_registration);

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);

		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.avenirNext(this));
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        textViewRegisterNameValue = (TextView) findViewById(R.id.textViewRegisterNameValue); textViewRegisterNameValue.setTypeface(Fonts.mavenRegular(this));
        textViewRegisterEmailValue = (TextView) findViewById(R.id.textViewRegisterEmailValue); textViewRegisterEmailValue.setTypeface(Fonts.mavenRegular(this));
        textViewRegisterPhoneValue = (TextView) findViewById(R.id.textViewRegisterPhoneValue); textViewRegisterPhoneValue.setTypeface(Fonts.mavenRegular(this));
        textViewRegisterHelp = (TextView) findViewById(R.id.textViewRegisterHelp); textViewRegisterHelp.setTypeface(Fonts.mavenLight(this));

        ((TextView) findViewById(R.id.textViewRegistration)).setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewRegisterName)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewRegisterEmail)).setTypeface(Fonts.mavenLight(this));
        ((TextView) findViewById(R.id.textViewRegisterPhone)).setTypeface(Fonts.mavenLight(this));

        editTextMessage = (EditText) findViewById(R.id.editTextMessage); editTextMessage.setTypeface(Fonts.mavenMedium(this));

        buttonSubmitRequest = (Button) findViewById(R.id.buttonSubmitRequest); buttonSubmitRequest.setTypeface(Fonts.mavenRegular(this));

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);

		textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


        buttonSubmitRequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String messageStr = editTextMessage.getText().toString().trim();
                String name = "";
                String email = "";
                String phone = "";

                if(SplashNewActivity.RegisterationType.FACEBOOK == SplashNewActivity.registerationType) {
                    name = OTPConfirmScreen.facebookRegisterData.fbName;
                    email = OTPConfirmScreen.facebookRegisterData.fbUserEmail;
                    phone = OTPConfirmScreen.facebookRegisterData.phoneNo;
                }
				else if(SplashNewActivity.RegisterationType.GOOGLE == SplashNewActivity.registerationType){
					name = OTPConfirmScreen.googleRegisterData.name;
					email = OTPConfirmScreen.googleRegisterData.email;
					phone = OTPConfirmScreen.googleRegisterData.phoneNo;
				}
                else{
                    name = OTPConfirmScreen.emailRegisterData.name;
                    email = OTPConfirmScreen.emailRegisterData.emailId;
                    phone = textViewRegisterPhoneValue.getText().toString();
                }

                submitDuplicateRegistrationRequestAPI(RequestDuplicateRegistrationActivity.this, messageStr, name, email, phone);

			}
		});


        try{
			if(SplashNewActivity.RegisterationType.FACEBOOK == SplashNewActivity.registerationType) {
                textViewRegisterNameValue.setText(OTPConfirmScreen.facebookRegisterData.fbName);
                textViewRegisterEmailValue.setText(OTPConfirmScreen.facebookRegisterData.fbUserEmail);
                textViewRegisterPhoneValue.setText(OTPConfirmScreen.facebookRegisterData.phoneNo);
            }
			else if(SplashNewActivity.RegisterationType.GOOGLE == SplashNewActivity.registerationType){
				textViewRegisterNameValue.setText(OTPConfirmScreen.googleRegisterData.name);
				textViewRegisterEmailValue.setText(OTPConfirmScreen.googleRegisterData.email);
				textViewRegisterPhoneValue.setText(OTPConfirmScreen.googleRegisterData.phoneNo);
			}
            else{
                textViewRegisterNameValue.setText(OTPConfirmScreen.emailRegisterData.name);
                textViewRegisterEmailValue.setText(OTPConfirmScreen.emailRegisterData.emailId);
                //textViewRegisterPhoneValue.setText(OTPConfirmScreen.emailRegisterData.phoneNo);
				textViewRegisterPhoneValue.setText(Data.kitPhoneNumber);
            }
            editTextMessage.setHint(getString(R.string.you_have_already_created_multiple_accounts_format, String.valueOf(Data.previousAccountInfoList.size())));
        } catch(Exception e){
            e.printStackTrace();
            performBackPressed();
        }

		
		final View activityRootView = findViewById(R.id.linearLayoutMain);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						Rect r = new Rect();
						// r will be populated with the coordinates of your view
						// that area still visible.
						activityRootView.getWindowVisibleDisplayFrame(r);

						int heightDiff = activityRootView.getRootView()
								.getHeight() - (r.bottom - r.top);
						if (heightDiff > 100) { // if more than 100 pixels, its
												// probably a keyboard...

							/************** Adapter for the parent List *************/

							ViewGroup.LayoutParams params_12 = textViewScroll
									.getLayoutParams();

							params_12.height = (int)(heightDiff);

							textViewScroll.setLayoutParams(params_12);
							textViewScroll.requestLayout();

						} else {

							ViewGroup.LayoutParams params = textViewScroll
									.getLayoutParams();
							params.height = 0;
							textViewScroll.setLayoutParams(params);
							textViewScroll.requestLayout();

						}
					}
				});
		
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	}

	
	public void performBackPressed(){
        startActivity(new Intent(RequestDuplicateRegistrationActivity.this, MultipleAccountsActivity.class));
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
        Log.e("test", "test");
	}
	



	
	
	public void submitDuplicateRegistrationRequestAPI(final Activity activity, String messageStr, String name, String email, String phone) {
		if (MyApplication.getInstance().isOnline()) {
			
			DialogPopup.showLoadingDialog(activity, getString(R.string.loading));
			
			HashMap<String, String> params = new HashMap<>();

            params.put("user_name", name);
            params.put("user_email", email);
            params.put("phone_no", phone);
			params.put("user_message", ""+messageStr);
			params.put("client_id", Config.getAutosClientId());

            try {
                if (SplashNewActivity.multipleCaseJSON != null) {
                    params.put("users", ""+SplashNewActivity.multipleCaseJSON.getJSONArray("users"));
                }
            } catch(Exception e){
                e.printStackTrace();
            }


			Log.i("params request_dup_registration", "=" + params);

		
//			AsyncHttpClient client = Data.getClient();
//			client.post(Config.getServerUrl() + "/request_dup_registration", params,
//					new CustomAsyncHttpResponseHandler() {
//					private JSONObject jObj;
//
//						@Override
//						public void onFailure(Throwable arg3) {
//							Log.e("request fail", arg3.toString());
//							DialogPopup.dismissLoadingDialog();
//							DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
//						}
//
//						@Override
//						public void onSuccess(String response) {
//							Log.i("Server response request_dup_registration", "response = " + response);
//							try {
//								jObj = new JSONObject(response);
//								int flag = jObj.getInt("flag");
//								String message = JSONParser.getServerMessage(jObj);
//								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
//                                    if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
//										DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
//											@Override
//											public void onClick(View v) {
//												activity.startActivity(new Intent(activity, SplashNewActivity.class));
//												activity.finish();
//												activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
//											}
//										});
//                                    }
//									else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
//                                        DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener(){
//                                            @Override
//                                            public void onClick(View v) {
//                                                activity.startActivity(new Intent(activity, SplashNewActivity.class));
//                                                activity.finish();
//                                                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
//                                            }
//                                        });
//									}
//									else{
//										DialogPopup.alertPopup(activity, "", message);
//									}
//								}
//							}  catch (Exception exception) {
//								exception.printStackTrace();
//								DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
//							}
//							DialogPopup.dismissLoadingDialog();
//						}
//					});


			new HomeUtil().putDefaultParams(params);
			RestClient.getApiService().requestDupRegistration(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt settleUserDebt, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.i("Server response request_dup_registration", "response = " + response);
					try {
						JSONObject jObj = new JSONObject(responseStr);
						int flag = jObj.getInt("flag");
						String message = JSONParser.getServerMessage(jObj);
						if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
							if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
								DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										activity.startActivity(new Intent(activity, SplashNewActivity.class));
										activity.finish();
										activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
									}
								});
							}
							else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
								DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener(){
									@Override
									public void onClick(View v) {
										activity.startActivity(new Intent(activity, SplashNewActivity.class));
										activity.finish();
										activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
									}
								});
							}
							else{
								DialogPopup.alertPopup(activity, "", message);
							}
						}
					}  catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_please_try_again));
					}
					DialogPopup.dismissLoadingDialog();
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

	
}
