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
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.adapters.GridViewAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.NonScrollGridView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.widgets.FAB.FloatingActionMenu;


public class HomeSwitcherActivity extends Activity {

    RelativeLayout relative,relativeLayoutHomeData;

    TextView textViewTitle, textViewHi;
    CardView relativeLayoutRides, relativeLayoutMeals, relativeLayoutFresh, relativeLayoutGrocery, relativeLayoutDelivery, relativeLayoutPay;
    TextView textViewRides, textViewMeals, textViewFresh, textViewGrocery, textViewDelivery, textViewPay;
    FloatingActionMenu fabMenuIns;
    Animation bounceAnim, bounceScaleAnim;
    NonScrollGridView gridView;
    ArrayList<String> gridViewData;
    private ScrollView scrollViewRideSummary;

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

        textViewHi = (TextView) findViewById(R.id.textViewHi);
        textViewHi.setTypeface(Fonts.mavenMedium(this));
        try {
            textViewHi.setText(getString(R.string.hi_username_format, Data.userData.userName));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((TextView) findViewById(R.id.textViewWeHave)).setTypeface(Fonts.mavenMedium(this));
        ((TextView) findViewById(R.id.textViewWeHave2)).setTypeface(Fonts.mavenMedium(this));
        scrollViewRideSummary = (ScrollView) findViewById(R.id.scrollViewRideSummary);

      /*  relativeLayoutHomeData = (RelativeLayout) findViewById(R.id.relativeLayoutHomeData);*/
        /*relativeLayoutRides = (CardView) findViewById(R.id.relativeLayoutRides);
        relativeLayoutMeals = (CardView) findViewById(R.id.relativeLayoutMeals);
        relativeLayoutFresh = (CardView) findViewById(R.id.relativeLayoutFresh);
        relativeLayoutGrocery = (CardView) findViewById(R.id.relativeLayoutGrocery);
        relativeLayoutDelivery = (CardView) findViewById(R.id.relativeLayoutDelivery);
        relativeLayoutPay = (CardView) findViewById(R.id.relativeLayoutPay);*/
        gridView = (NonScrollGridView) findViewById(R.id.gridView);
        gridViewData = new ArrayList<>();


/*        textViewRides = (TextView) findViewById(R.id.textViewRides);

        textViewRides.setTypeface(Fonts.mavenMedium(this));
        textViewMeals = (TextView) findViewById(R.id.textViewMeals);
        textViewMeals.setTypeface(Fonts.mavenMedium(this));
        textViewFresh = (TextView) findViewById(R.id.textViewFresh);
        textViewFresh.setTypeface(Fonts.mavenMedium(this));
        textViewGrocery = (TextView) findViewById(R.id.textViewGrocery);
        textViewGrocery.setTypeface(Fonts.mavenMedium(this));
        textViewDelivery = (TextView) findViewById(R.id.textViewDelivery); *//*textViewDelivery.setTypeface(Fonts.mavenMedium(this));*//*
        textViewPay = (TextView) findViewById(R.id.textViewPay); textViewPay.setTypeface(Fonts.mavenMedium(this));*/

        fabMenuIns = (FloatingActionMenu) findViewById(R.id.fabMenuIns);
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (90f * scale + 0.5f);
        fabMenuIns.setPadding((int) (40f * ASSL.Yscale()), 0, 0, dpAsPixels);
        fabMenuIns.setMenuButtonColorNormal(getResources().getColor(R.color.white));
        fabMenuIns.setMenuButtonColorPressed(getResources().getColor(R.color.grey_light));
        fabMenuIns.setMenuButtonColorRipple(getResources().getColor(R.color.grey_light_alpha));
        fabMenuIns.setEnabled(false);

        bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        bounceScaleAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_scale);
        //button.startAnimation(myAnim);


        try {
            gridViewData.add(Config.getAutosClientId());
            if ((Data.userData.getFreshEnabled() == 1)) {
                gridViewData.add(Config.getFreshClientId());
            }
            if ((Data.userData.getMealsEnabled() == 1)) {
                gridViewData.add(Config.getMealsClientId());
            }
            if ((Data.userData.getGroceryEnabled() == 1)) {
                gridViewData.add(Config.getGroceryClientId());
            }


/*
            if ((Data.userData.getMenusEnabled() == 1)) {
                gridViewData.add(Config.getMenusClientId());
            }
            if ((Data.userData.getPayEnabled == 1))
            {
               gridViewData.add(Config.getPayClientId());
    //            gridViewData.add(Data.userData.getPAyNowEnabled,Config.getPayNowClientId());
            }
*/
            /*if ((Data.userData.getDeliveryEnabled() == 1)) {
                gridViewData.add(Config.getDeliveryClientId());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.v("length of data","length of data"+gridViewData.size());
        GridViewAdapter gridViewAdapter = new GridViewAdapter(this, gridViewData);
        gridView.setAdapter(gridViewAdapter);
        gridView.setNumColumns((gridViewData.size()>2)?2:1);

       /* try {
               if((Data.userData.getFreshEnabled() == 0) && (Data.userData.getMealsEnabled() == 0)
                    && (Data.userData.getDeliveryEnabled() == 0) && (Data.userData.getGroceryEnabled() == 0) && (Data.userData.getDeliveryEnabled() == 0)
                      *//* && ((Data.userData.getPayNowEnabled() == 0))*//* )
               {
                relativeLayoutHomeData.setVisibility(View.GONE);
                }
                else
                {
                    relativeLayoutHomeData.setVisibility(View.VISIBLE);
                    if (Data.userData.getFreshEnabled() != 1)
                    {
                        relativeLayoutFresh.setVisibility(View.GONE);
                    }
                    else
                    {
                        relativeLayoutFresh.setVisibility(View.VISIBLE);
                    }
                    if (Data.userData.getMealsEnabled() != 1)
                    {
                        relativeLayoutMeals.setVisibility(View.GONE);
                    }
                    else
                    {
                        relativeLayoutMeals.setVisibility(View.VISIBLE);
                    }
                    if(Data.userData.getGroceryEnabled() != 1)
                    {
                        relativeLayoutGrocery.setVisibility(View.GONE);
                    }
                    else
                    {
                        relativeLayoutGrocery.setVisibility(View.VISIBLE);
                    }
                    if (Data.userData.getDeliveryEnabled() != 1)
                    {
                        relativeLayoutDelivery.setVisibility(View.GONE);
                    }
                    else
                    {
                        relativeLayoutDelivery.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
            e.printStackTrace();
        }








        relativeLayoutRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!animStarted) {
//                    relativeLayoutRides.startAnimation(bounceAnim);
                    startInnerAnim(Config.getAutosClientId());
                }
            }
        });

        relativeLayoutMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!animStarted) {
//                    relativeLayoutMeals.startAnimation(bounceAnim);
                    startInnerAnim(Config.getMealsClientId());
                }
            }
        });

        relativeLayoutFresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!animStarted) {
//                    relativeLayoutFresh.startAnimation(bounceAnim);
                    startInnerAnim(Config.getFreshClientId());
                }
            }
        });

        relativeLayoutGrocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!animStarted) {
//                    relativeLayoutGrocery.startAnimation(bounceAnim);
                    startInnerAnim(Config.getGroceryClientId());
                }
            }
        });

        relativeLayoutDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!animStarted) {
//                    relativeLayoutGrocery.startAnimation(bounceAnim);
                    startInnerAnim(Config.getDeliveryClientId());
                }
            }
        });


        *//*relativeLayoutPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!animStarted) {
//                    relativeLayoutGrocery.startAnimation(bounceAnim);
                    startInnerAnim(Config.getPayClientId());
                }
            }
        });*//*

    }




    long duration = 500;
    long bounceDuration = 200;
    boolean animStarted = false;
    private void startInnerAnim(final String clientId){
        animStarted = true;
        relativeLayoutRides.clearAnimation();
        relativeLayoutMeals.clearAnimation();
        relativeLayoutFresh.clearAnimation();
        relativeLayoutGrocery.clearAnimation();
        relativeLayoutDelivery.clearAnimation();
        relativeLayoutPay.clearAnimation();

        *//*relativeLayoutRides.startAnimation(bounceAnim);
        relativeLayoutFresh.startAnimation(bounceAnim);
        relativeLayoutMeals.startAnimation(bounceAnim);
        relativeLayoutGrocery.startAnimation(bounceAnim);*//*

        *//*relativeLayoutRides.startAnimation(bounceScaleAnim);
        relativeLayoutFresh.startAnimation(bounceScaleAnim);
        relativeLayoutMeals.startAnimation(bounceScaleAnim);
        relativeLayoutGrocery.startAnimation(bounceScaleAnim);*//*
        if(clientId.equalsIgnoreCase(Config.getAutosClientId())){
            relativeLayoutRides.startAnimation(bounceAnim);
        } else if(clientId.equalsIgnoreCase(Config.getFreshClientId())){
            relativeLayoutFresh.startAnimation(bounceAnim);
        } else if(clientId.equalsIgnoreCase(Config.getMealsClientId())){
            relativeLayoutMeals.startAnimation(bounceAnim);
        } else if(clientId.equalsIgnoreCase(Config.getGroceryClientId())){
            relativeLayoutGrocery.startAnimation(bounceAnim);
        }

        final AnimationSet asr = new AnimationSet(true);
        Animation tr = new TranslateAnimation(0, -(ASSL.Xscale() * 60f), 0, ASSL.Yscale() * 470f);
        tr.setDuration(duration);
        tr.setFillAfter(false);
        tr.setInterpolator(new AccelerateDecelerateInterpolator());
        addScaleAlphaAnimListener(asr, tr, relativeLayoutRides);

        final AnimationSet asf = new AnimationSet(true);
        Animation tf = new TranslateAnimation(0, -(ASSL.Xscale() * 60f), 0, ASSL.Yscale() * 130f);
        tf.setDuration(duration);
        tf.setFillAfter(false);
        tf.setInterpolator(new AccelerateDecelerateInterpolator());
        addScaleAlphaAnimListener(asf, tf, relativeLayoutFresh);

        final AnimationSet asm = new AnimationSet(true);
        Animation tm = new TranslateAnimation(0, -(ASSL.Xscale() * 360f), 0, ASSL.Yscale() * 470f);
        tm.setDuration(duration);
        tm.setFillAfter(false);
        tm.setInterpolator(new AccelerateDecelerateInterpolator());
        addScaleAlphaAnimListener(asm, tm, relativeLayoutMeals);

        final AnimationSet asg = new AnimationSet(true);
        Animation tg = new TranslateAnimation(0, -(ASSL.Xscale() * 360f), 0, ASSL.Yscale() * 130f);
        tg.setDuration(duration);
        tg.setFillAfter(false);
        tg.setInterpolator(new AccelerateDecelerateInterpolator());
        addScaleAlphaAnimListener(asg, tg, relativeLayoutGrocery);

        *//*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                relativeLayoutRides.startAnimation(asr);
                relativeLayoutMeals.startAnimation(asm);
                relativeLayoutFresh.startAnimation(asf);
                relativeLayoutGrocery.startAnimation(asg);
            }
        }, bounceDuration);*//*

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                relativeLayoutFresh.startAnimation(asf);
            }
        }, bounceDuration);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                relativeLayoutGrocery.startAnimation(asg);
            }
        }, bounceDuration+75);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                relativeLayoutRides.startAnimation(asr);
            }
        }, bounceDuration+150);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fabMenuIns.setVisibility(View.VISIBLE);
                relativeLayoutMeals.startAnimation(asm);
            }
        }, bounceDuration+225);





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Prefs.with(HomeSwitcherActivity.this).save("home_switcher_client_id", clientId);

                double latitude = getIntent().getDoubleExtra(Constants.KEY_LATITUDE, LocationFetcher.getSavedLatFromSP(HomeSwitcherActivity.this));
                double longitude = getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, LocationFetcher.getSavedLngFromSP(HomeSwitcherActivity.this));
                Bundle bundle = getIntent().getBundleExtra(Constants.KEY_APP_SWITCH_BUNDLE);
                overridePendingTransition(getInAnim(true), getOutAnim(true));
                finish();
//                MyApplication.getInstance().getAppSwitcher().switchApp(HomeSwitcherActivity.this, clientId, getIntent().getData(),
//                        new LatLng(latitude, longitude), bundle, false, false, true);
            }
        }, duration+bounceDuration+75);

    }

    private int getInAnim(boolean slowTransition){
        if(slowTransition){
            return R.anim.fade_in_slow;
        } else{
            return R.anim.fade_in;
        }
    }

    private int getOutAnim(boolean slowTransition){
        if(slowTransition){
            return R.anim.fade_out_slow;
        } else{
            return R.anim.fade_out;
        }
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
    */
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
