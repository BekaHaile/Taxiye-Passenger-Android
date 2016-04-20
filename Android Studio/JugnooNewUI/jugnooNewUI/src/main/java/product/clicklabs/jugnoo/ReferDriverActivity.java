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

import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;


public class ReferDriverActivity extends BaseActivity implements FlurryEventNames {

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
        textViewTitle.setTypeface(Fonts.mavenRegular(this));
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

        buttonRefer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

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

}
