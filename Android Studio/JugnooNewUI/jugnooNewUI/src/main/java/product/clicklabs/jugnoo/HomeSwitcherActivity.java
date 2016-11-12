package product.clicklabs.jugnoo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.widgets.FAB.FloatingActionMenu;


public class HomeSwitcherActivity extends Activity {

    RelativeLayout relative;

    TextView textViewTitle, textViewHi;
    CardView relativeLayoutRides, relativeLayoutMeals, relativeLayoutFresh, relativeLayoutGrocery;
    TextView textViewRides, textViewMeals, textViewFresh, textViewGrocery;
    FloatingActionMenu fabMenuIns;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_switcher);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));

        textViewTitle.setText(getString(R.string.app_name));
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));

        textViewHi = (TextView) findViewById(R.id.textViewHi); textViewHi.setTypeface(Fonts.mavenMedium(this));
        try {
            textViewHi.setText(getString(R.string.hi_username_format, Data.userData.userName));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((TextView)findViewById(R.id.textViewWeHave)).setTypeface(Fonts.mavenMedium(this));
        ((TextView)findViewById(R.id.textViewWeHave2)).setTypeface(Fonts.mavenMedium(this));

        relativeLayoutRides = (CardView) findViewById(R.id.relativeLayoutRides);
        relativeLayoutMeals = (CardView) findViewById(R.id.relativeLayoutMeals);
        relativeLayoutFresh = (CardView) findViewById(R.id.relativeLayoutFresh);
        relativeLayoutGrocery = (CardView) findViewById(R.id.relativeLayoutGrocery);
        textViewRides = (TextView) findViewById(R.id.textViewRides); textViewRides.setTypeface(Fonts.mavenMedium(this));
        textViewMeals = (TextView) findViewById(R.id.textViewMeals); textViewMeals.setTypeface(Fonts.mavenMedium(this));
        textViewFresh = (TextView) findViewById(R.id.textViewFresh); textViewFresh.setTypeface(Fonts.mavenMedium(this));
        textViewGrocery = (TextView) findViewById(R.id.textViewGrocery); textViewGrocery.setTypeface(Fonts.mavenMedium(this));

        fabMenuIns = (FloatingActionMenu) findViewById(R.id.fabMenuIns);
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (90f*scale + 0.5f);
        fabMenuIns.setPadding((int) (40f * ASSL.Yscale()), 0, 0, dpAsPixels);
        fabMenuIns.setMenuButtonColorNormal(getResources().getColor(R.color.white));
        fabMenuIns.setMenuButtonColorPressed(getResources().getColor(R.color.grey_light));
        fabMenuIns.setMenuButtonColorRipple(getResources().getColor(R.color.grey_light_alpha));
        fabMenuIns.setEnabled(false);

        relativeLayoutRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!animStarted) {
                    startInnerAnim(Config.getAutosClientId());
                }
            }
        });

        relativeLayoutMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!animStarted) {
                    startInnerAnim(Config.getMealsClientId());
                }
            }
        });

        relativeLayoutFresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!animStarted) {
                    startInnerAnim(Config.getFreshClientId());
                }
            }
        });

        relativeLayoutGrocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!animStarted) {
                    startInnerAnim(Config.getGroceryClientId());
                }
            }
        });


    }




    long duration = 500;
    boolean animStarted = false;
    private void startInnerAnim(final String clientId){
        animStarted = true;
        relativeLayoutRides.clearAnimation();
        relativeLayoutMeals.clearAnimation();
        relativeLayoutFresh.clearAnimation();
        relativeLayoutGrocery.clearAnimation();

        AnimationSet asr = new AnimationSet(true);
        Animation tr = new TranslateAnimation(0, -(ASSL.Xscale() * 60f), 0, ASSL.Yscale() * 520f);
        tr.setDuration(duration);
        tr.setFillAfter(false);
        tr.setInterpolator(new AccelerateDecelerateInterpolator());
        addScaleAlphaAnimListener(asr, tr, relativeLayoutRides);

        AnimationSet asf = new AnimationSet(true);
        Animation tf = new TranslateAnimation(0, -(ASSL.Xscale() * 60f), 0, ASSL.Yscale() * 240f);
        tf.setDuration(duration);
        tf.setFillAfter(false);
        tf.setInterpolator(new AccelerateDecelerateInterpolator());
        addScaleAlphaAnimListener(asf, tf, relativeLayoutFresh);

        AnimationSet asm = new AnimationSet(true);
        Animation tm = new TranslateAnimation(0, -(ASSL.Xscale() * 360f), 0, ASSL.Yscale() * 520f);
        tm.setDuration(duration);
        tm.setFillAfter(false);
        tm.setInterpolator(new AccelerateDecelerateInterpolator());
        addScaleAlphaAnimListener(asm, tm, relativeLayoutMeals);

        AnimationSet asg = new AnimationSet(true);
        Animation tg = new TranslateAnimation(0, -(ASSL.Xscale() * 360f), 0, ASSL.Yscale() * 240f);
        tg.setDuration(duration);
        tg.setFillAfter(false);
        tg.setInterpolator(new AccelerateDecelerateInterpolator());
        addScaleAlphaAnimListener(asg, tg, relativeLayoutGrocery);

        relativeLayoutRides.startAnimation(asr);
        relativeLayoutMeals.startAnimation(asm);
        relativeLayoutFresh.startAnimation(asf);
        relativeLayoutGrocery.startAnimation(asg);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                double latitude = getIntent().getDoubleExtra(Constants.KEY_LATITUDE, LocationFetcher.getSavedLatFromSP(HomeSwitcherActivity.this));
                double longitude = getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, LocationFetcher.getSavedLngFromSP(HomeSwitcherActivity.this));
                Bundle bundle = getIntent().getBundleExtra(Constants.KEY_APP_SWITCH_BUNDLE);
                MyApplication.getInstance().getAppSwitcher().switchApp(HomeSwitcherActivity.this, clientId, getIntent().getData(),
                        new LatLng(latitude, longitude), bundle, false, false, true);
            }
        }, duration);

    }


    private void addScaleAlphaAnimListener(AnimationSet asr, Animation tr, final View view){
        Animation a = new AlphaAnimation(1, 0);
        a.setDuration(duration);
        a.setFillAfter(false);
        a.setInterpolator(new AccelerateDecelerateInterpolator());

        Animation s = new ScaleAnimation(1f, 0.4f, 1f, 0.4f, ScaleAnimation.RELATIVE_TO_SELF, 0.0f, ScaleAnimation.RELATIVE_TO_SELF, 1.0f);
        s.setDuration(duration);
        s.setFillAfter(false);
        s.setInterpolator(new AccelerateDecelerateInterpolator());

        asr.addAnimation(s);
        asr.addAnimation(tr);
        asr.addAnimation(a);
        asr.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

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
