package product.clicklabs.jugnoo;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.BlurTransform;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

public class AccountActivity extends Activity {

	RelativeLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack, imageViewLogout;
	
	ImageView imageViewUserImageBlur, imageViewProfileImage;
	EditText editTextUserName, editTextEmail, editTextPhone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_user);
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		
		imageViewUserImageBlur = (ImageView) findViewById(R.id.imageViewUserImageBlur);
		imageViewProfileImage = (ImageView) findViewById(R.id.imageViewProfileImage);
		
		editTextUserName = (EditText) findViewById(R.id.editTextUserName); editTextUserName.setTypeface(Data.latoRegular(this));
		editTextEmail = (EditText) findViewById(R.id.editTextEmail); editTextEmail.setTypeface(Data.latoRegular(this));
		editTextPhone = (EditText) findViewById(R.id.editTextPhone); editTextPhone.setTypeface(Data.latoRegular(this));
		
		editTextUserName.setEnabled(false);
		editTextEmail.setEnabled(false);
		editTextPhone.setEnabled(false);
		
		
		editTextUserName.setText(Data.userData.userName);
		editTextEmail.setText(Data.userData.userEmail);
		editTextPhone.setText(Data.userData.phoneNo);
		
		try{
			if(!"".equalsIgnoreCase(Data.userData.userImage)){
				Picasso.with(this).load(Data.userData.userImage).transform(new CircleTransform()).into(imageViewProfileImage);
				Picasso.with(this).load(Data.userData.userImage).transform(new BlurTransform()).into(imageViewUserImageBlur);
			}
		} catch(Exception e){
			e.printStackTrace();
		}	
		
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
	}

	
	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
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
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}

}
