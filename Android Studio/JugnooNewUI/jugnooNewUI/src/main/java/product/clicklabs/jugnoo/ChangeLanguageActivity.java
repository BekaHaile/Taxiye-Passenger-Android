package product.clicklabs.jugnoo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.adapters.LanguagesAdapter;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.model.FetchActiveLocaleResponse;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LocaleHelper;
import product.clicklabs.jugnoo.utils.Utils;


public class ChangeLanguageActivity extends BaseActivity {

    FrameLayout relative;
    RelativeLayout rlTransparent;

    TextView textViewTitle;
    ImageView imageViewBack;

    RecyclerView rvLanguages;
    LanguagesAdapter adapter;

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
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        rvLanguages = (RecyclerView) findViewById(R.id.rvLanguages);
        rvLanguages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new LanguagesAdapter(this, rvLanguages, new LanguagesAdapter.Callback() {
            @Override
            public void onLanguageClick(FetchActiveLocaleResponse.Locale locale) {
                if (!LocaleHelper.getLanguage(ChangeLanguageActivity.this).equalsIgnoreCase(locale.getLocale())) {
                    updateLocaleApi(locale.getLocale());
                }
            }
        });
        rvLanguages.setAdapter(adapter);

        textViewTitle.setText(getResources().getString(R.string.change_language_caps));
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));


        imageViewBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });
		fetchActiveLocalesApi();
    }

    private void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);
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
            startActivity(new Intent(ChangeLanguageActivity.this, SplashNewActivity.class));
            finish();
        }
    }

    private void setRlRestartTimerVisibility(int visibility) {
        if (visibility == View.GONE) {
            rlRestartTimer.setVisibility(View.GONE);
        } else {
            rlRestartTimer.setVisibility(View.VISIBLE);
        }
    }


    private void updateLocaleApi(final String locale){
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_UPDATED_LOCALE, locale);

        new ApiCommon<SettleUserDebt>(this).execute(params, ApiName.UPDATE_USER_PROFILE,
                new APICommonCallback<SettleUserDebt>() {
            @Override
            public void onSuccess(SettleUserDebt settleUserDebt, String message, int flag) {
                updateViews(locale);
            }

            @Override
            public boolean onError(SettleUserDebt settleUserDebt, String message, int flag) {
                return false;
            }
        });

    }
    private void fetchActiveLocalesApi(){
        HashMap<String, String> params = new HashMap<>();

        new ApiCommon<FetchActiveLocaleResponse>(this).execute(params, ApiName.FETCH_ACTIVE_LOCALES,
                new APICommonCallback<FetchActiveLocaleResponse>() {
            @Override
            public void onSuccess(FetchActiveLocaleResponse settleUserDebt, String message, int flag) {
                ArrayList<FetchActiveLocaleResponse.Locale> locales = new ArrayList<>();
                for(FetchActiveLocaleResponse.Locale locale : settleUserDebt.getLocaleSet()){
                    if(BaseActivity.isLocaleSupported(ChangeLanguageActivity.this, locale.getName())){
                        locales.add(locale);
                    }
                }
                adapter.setList(locales);
            }

            @Override
            public boolean onError(FetchActiveLocaleResponse settleUserDebt, String message, int flag) {
                return false;
            }
        });

    }
}
