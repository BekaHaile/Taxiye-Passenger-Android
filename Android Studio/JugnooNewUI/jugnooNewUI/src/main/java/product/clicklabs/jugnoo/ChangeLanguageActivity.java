package product.clicklabs.jugnoo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LocaleHelper;
import product.clicklabs.jugnoo.utils.Utils;


public class ChangeLanguageActivity extends BaseActivity {

    FrameLayout relative;
    RelativeLayout rlTransparent;

    TextView textViewTitle, tvCountDownTimer;
    ImageView imageViewBack, ivEnglish, ivArabic, ivFrench, ivGerman;

    RelativeLayout relativeLayoutEnglish, relativeLayoutFrench, relativeLayoutArabic, relativeLayoutGerman;
    TextView textViewEnglish, textViewFrench, textViewArabic, textViewGerman;
    private final String TAG = "Change Language";
    Bundle bundle;
    TextView textViewCounter;
    ImageView imageViewYellowLoadingBar;
    RelativeLayout rlRestartTimer;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        relative = (FrameLayout) findViewById(R.id.relative);
        rlTransparent = (RelativeLayout) findViewById(R.id.rlTransparent);
        rlTransparent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        new ASSL(this, (ViewGroup) relative, 1134, 720, false);

        rlRestartTimer = (RelativeLayout) findViewById(R.id.rlRestartTimer);
        textViewCounter = (TextView) findViewById(R.id.textViewCounter);
        textViewCounter.setTypeface(Fonts.mavenRegular(this));
        imageViewYellowLoadingBar = (ImageView) findViewById(R.id.imageViewYellowLoadingBar);


        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        tvCountDownTimer = (TextView) findViewById(R.id.tvCountDownTimer);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        ivEnglish = (ImageView) findViewById(R.id.ivEnglish);
        ivArabic = (ImageView) findViewById(R.id.ivArabic);
        ivFrench = (ImageView) findViewById(R.id.ivFrench);
        ivGerman = (ImageView) findViewById(R.id.ivGerman);

        relativeLayoutEnglish = (RelativeLayout) findViewById(R.id.relativeLayoutEnglish);
        relativeLayoutFrench = (RelativeLayout) findViewById(R.id.relativeLayoutFrench);
        relativeLayoutArabic = (RelativeLayout) findViewById(R.id.relativeLayoutArabic);
        relativeLayoutGerman = (RelativeLayout) findViewById(R.id.relativeLayoutGerman);

        textViewEnglish = (TextView) findViewById(R.id.textViewEnglish);
        textViewEnglish.setTypeface(Fonts.mavenLight(this));
        textViewFrench = (TextView) findViewById(R.id.textViewFrench);
        textViewFrench.setTypeface(Fonts.mavenLight(this));
        textViewArabic = (TextView) findViewById(R.id.textViewArabic);
        textViewArabic.setTypeface(Fonts.mavenLight(this));
        textViewGerman = (TextView) findViewById(R.id.textViewGerman);
        textViewGerman.setTypeface(Fonts.mavenLight(this));

        textViewTitle.setText(getResources().getString(R.string.change_language_caps));
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

        relativeLayoutEnglish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!LocaleHelper.getLanguage(ChangeLanguageActivity.this).equalsIgnoreCase("en")) {
                    updateViews("en");
                }
            }
        });

        relativeLayoutFrench.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!LocaleHelper.getLanguage(ChangeLanguageActivity.this).equalsIgnoreCase("fr")) {
                    updateViews("fr");
                }
            }
        });

        relativeLayoutArabic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!LocaleHelper.getLanguage(ChangeLanguageActivity.this).equalsIgnoreCase("ar")) {
                    updateViews("ar");
                }
            }
        });

        relativeLayoutGerman.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!LocaleHelper.getLanguage(ChangeLanguageActivity.this).equalsIgnoreCase("de")) {
                    updateViews("de");
                }
            }
        });


        imageViewBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });
        showSelectedLanguage();
    }

    public void showSelectedLanguage() {
        if (LocaleHelper.getLanguage(this).equalsIgnoreCase("en")) {
            ivEnglish.setVisibility(View.VISIBLE);
            ivArabic.setVisibility(View.GONE);
            ivFrench.setVisibility(View.GONE);
            ivGerman.setVisibility(View.GONE);
        } else if (LocaleHelper.getLanguage(this).equalsIgnoreCase("fr")) {
            ivEnglish.setVisibility(View.GONE);
            ivArabic.setVisibility(View.GONE);
            ivFrench.setVisibility(View.VISIBLE);
            ivGerman.setVisibility(View.GONE);
        } else if (LocaleHelper.getLanguage(this).equalsIgnoreCase("ar")) {
            ivEnglish.setVisibility(View.GONE);
            ivArabic.setVisibility(View.VISIBLE);
            ivFrench.setVisibility(View.GONE);
            ivGerman.setVisibility(View.GONE);
        } else if (LocaleHelper.getLanguage(this).equalsIgnoreCase("de")) {
            ivEnglish.setVisibility(View.GONE);
            ivArabic.setVisibility(View.GONE);
            ivFrench.setVisibility(View.GONE);
            ivGerman.setVisibility(View.VISIBLE);
        }
    }

    private void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);
        //  Resources resources = context.getResources();
//        new CountDownTimer(5000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                tvCountDownTimer.setVisibility(View.VISIBLE);
//                tvCountDownTimer.setText("app will restart in " + millisUntilFinished / 1000);
//            }
//
//            public void onFinish() {
//                tvCountDownTimer.setText("done!");
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        finishAffinity();
//                        startActivity(new Intent(ChangeLanguageActivity.this, SplashNewActivity.class));
//                    }
//                }, 1000);
//            }
//
//        }.start();
        rlTransparent.setVisibility(View.VISIBLE);
        startRestartTimer();
    }

    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        if (rlTransparent.getVisibility() == View.GONE) {
            performBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
        super.onDestroy();
    }

    private void startRestartTimer() {
        try {
            long timerDuration = 3000;
            setRlRestartTimerVisibility(View.VISIBLE);
            CustomCountDownTimer customCountDownTimer = new CustomCountDownTimer(timerDuration, 5);
            customCountDownTimer.start();
            Utils.hideKeyboard(this);
        } catch (Exception e) {
            setRlRestartTimerVisibility(View.GONE);
        }
    }

    class CustomCountDownTimer extends CountDownTimer {

        private final long mMillisInFuture;

        public CustomCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            mMillisInFuture = millisInFuture;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            double percent = (((double) millisUntilFinished) * 100.0) / mMillisInFuture;

            double widthToSet = percent * ((double) (ASSL.Xscale() * 530)) / 100.0;

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageViewYellowLoadingBar.getLayoutParams();
            params.width = (int) widthToSet;
            imageViewYellowLoadingBar.setLayoutParams(params);


            long seconds = (long) Math.ceil(((double) millisUntilFinished) / 1000.0d);
            String text = seconds < 10 ? "0:0" + seconds : "0:" + seconds;
            textViewCounter.setText(text);
        }

        @Override
        public void onFinish() {
            finishAffinity();
            startActivity(new Intent(ChangeLanguageActivity.this, SplashNewActivity.class));
        }
    }

    private void setRlRestartTimerVisibility(int visibility) {
        if (visibility == View.GONE) {
            rlRestartTimer.setVisibility(View.GONE);
        } else {
            rlRestartTimer.setVisibility(View.VISIBLE);
        }
    }
}
