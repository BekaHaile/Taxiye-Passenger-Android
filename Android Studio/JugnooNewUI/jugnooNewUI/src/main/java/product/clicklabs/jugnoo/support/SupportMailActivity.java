package product.clicklabs.jugnoo.support;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import product.clicklabs.jugnoo.BaseAppCompatActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;

public class SupportMailActivity extends BaseAppCompatActivity implements View.OnClickListener{

	private EditText etMessage;
	private Button bSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_support_mail);

		ImageView backBtn = (ImageView) findViewById(R.id.imageViewBack);
		backBtn.setOnClickListener(this);
		((TextView) findViewById(R.id.textViewTitle)).setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		((TextView) findViewById(R.id.tvSendUsMail)).setTypeface(Fonts.mavenRegular(this));

		etMessage = (EditText) findViewById(R.id.etMessage);
		etMessage.setTypeface(Fonts.mavenRegular(this));

		bSubmit = (Button) findViewById(R.id.bSubmit);
		bSubmit.setTypeface(Fonts.mavenRegular(this));
		bSubmit.setOnClickListener(this);

		etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				bSubmit.performClick();
				return true;
			}
		});


	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.imageViewBack:
				onBackPressed();
				break;

			case R.id.bSubmit:
				if(etMessage.getText().toString().trim().length() == 0){
					Utils.showToast(this, getString(R.string.please_enter_something));
					return;
				}
				if(Data.userData != null) {
					Utils.openMailIntent(this,
							new String[]{Prefs.with(this).getString(Constants.KEY_CUSTOMER_SUPPORT_EMAIL,
									getString(R.string.default_support_email))},
							Prefs.with(this).getString(Constants.KEY_CUSTOMER_SUPPORT_EMAIL_SUBJECT,
									getString(R.string.support_mail_subject, getString(R.string.app_name))),
							etMessage.getText().toString().trim());
					etMessage.setText("");
				}
				break;
		}
	}

}
