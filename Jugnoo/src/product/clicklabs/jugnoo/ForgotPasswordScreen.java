package product.clicklabs.jugnoo;

import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ForgotPasswordScreen extends Activity{
	
	EditText emailEt;
	Button sendEmailBtn;
	
	LinearLayout relative;
	
	static String emailAlready = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password_screen);
		
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(ForgotPasswordScreen.this, relative, 1134, 720, false);
		
		emailEt = (EditText) findViewById(R.id.emailEt); emailEt.setTypeface(Data.regularFont(getApplicationContext()));
		
		sendEmailBtn = (Button) findViewById(R.id.sendEmailBtn); sendEmailBtn.setTypeface(Data.regularFont(getApplicationContext()));
		


		emailEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				emailEt.setError(null);
			}
		});
		
		
		
		sendEmailBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String email = emailEt.getText().toString().trim();
				
				if("".equalsIgnoreCase(email)){
					emailEt.requestFocus();
					emailEt.setError("Please enter email");
				}
				else{
					if(isEmailValid(email)){
						forgotPasswordAsync(ForgotPasswordScreen.this, email);
					}
					else{
						emailEt.requestFocus();
						emailEt.setError("Please enter valid email");
					}
				}
				
			}
		});
		
		
		emailEt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						sendEmailBtn.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});
		
		emailEt.setText(emailAlready);
		emailEt.setSelection(emailEt.getText().toString().length());
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	}

	
	
	/**
	 * ASync for register from server
	 */
	public void forgotPasswordAsync(final Activity activity, final String email) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			params.put("email", email);

			Log.i("email", "=" + email);
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/forgot_password", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@SuppressWarnings("unused")
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									String errorMessage = jObj.getString("error");
									
									new DialogPopup().alertPopup(activity, "", errorMessage);
									
								}
								else{
									new DialogPopup().alertPopup(activity, "", jObj.getString("log"));
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	
	boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(ForgotPasswordScreen.this, SplashLogin.class));
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		finish();
		super.onBackPressed();
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        
        ASSL.closeActivity(relative);
        
        System.gc();
	}
	
}
