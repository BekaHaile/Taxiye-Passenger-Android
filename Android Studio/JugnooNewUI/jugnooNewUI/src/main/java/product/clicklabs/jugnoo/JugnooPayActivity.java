package product.clicklabs.jugnoo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import rmn.androidscreenlibrary.ASSL;

public class JugnooPayActivity extends BaseActivity implements FlurryEventNames {

    LinearLayout relative;

    TextView textViewTitle;
    ImageView imageViewBack;

	TextView textViewAmount, textViewAutoId;
	Button button10, button15, button20, buttonMakePayment;
	EditText editTextPayAmount, editTextAutoId;


    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        relative = (LinearLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

		textViewAmount = (TextView) findViewById(R.id.textViewAmount); textViewAmount.setTypeface(Fonts.latoRegular(this));
		textViewAutoId = (TextView) findViewById(R.id.textViewAutoId); textViewAutoId.setTypeface(Fonts.latoRegular(this));
		button10 = (Button) findViewById(R.id.button10); button10.setTypeface(Fonts.latoRegular(this));
		button15 = (Button) findViewById(R.id.button15); button15.setTypeface(Fonts.latoRegular(this));
		button20 = (Button) findViewById(R.id.button20); button20.setTypeface(Fonts.latoRegular(this));
		editTextPayAmount = (EditText) findViewById(R.id.editTextPayAmount); editTextPayAmount.setTypeface(Fonts.latoRegular(this));
		editTextAutoId = (EditText) findViewById(R.id.editTextAutoId); editTextAutoId.setTypeface(Fonts.latoRegular(this));
		buttonMakePayment = (Button) findViewById(R.id.buttonMakePayment); buttonMakePayment.setTypeface(Fonts.latoRegular(this));

		button10.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextPayAmount.setText("10");
			}
		});

		button15.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextPayAmount.setText("15");
			}
		});

		button20.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextPayAmount.setText("20");
			}
		});


        imageViewBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });
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

}
