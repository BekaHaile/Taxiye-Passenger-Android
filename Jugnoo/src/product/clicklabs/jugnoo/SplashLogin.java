package product.clicklabs.jugnoo;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class SplashLogin extends Activity{
	
	EditText emailEt, passwordEt;
	Button signInBtn, forgotPasswordBtn, signupBtn, facebookSignInBtn;
	
	LinearLayout relative;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_login);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(SplashLogin.this, relative, 1134, 720, false);
		
		emailEt = (EditText) findViewById(R.id.emailEt);
		passwordEt = (EditText) findViewById(R.id.passwordEt);
		
		signInBtn = (Button) findViewById(R.id.signInBtn);
		forgotPasswordBtn = (Button) findViewById(R.id.forgotPasswordBtn);
		signupBtn = (Button) findViewById(R.id.signupBtn);
		facebookSignInBtn = (Button) findViewById(R.id.facebookSignInBtn);
		
		signInBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SplashLogin.this, HomeActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				finish();
			}
		});
		
		
	}

	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        
        ASSL.closeActivity(relative);
        
        System.gc();
	}
	
}
