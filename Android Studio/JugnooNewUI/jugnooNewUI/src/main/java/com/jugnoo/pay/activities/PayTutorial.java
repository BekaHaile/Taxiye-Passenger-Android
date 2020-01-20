package com.jugnoo.pay.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jugnoo.pay.utils.PrefManager;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

public class PayTutorial extends AppCompatActivity {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager vpPayTutorial;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter vpPayTutorialAdapter;

    private ViewPager viewPager;
    private PayTutorialViewPagerAdapter tutorialVpAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private PrefManager prefManager;

    private double latitude;
    private double longitude;

    RelativeLayout ActivityPayTutorialLayout;
    private int goBack;
    private int dontShowTurorialAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().hasExtra(Constants.KEY_LATITUDE) && getIntent().hasExtra(Constants.KEY_LONGITUDE))
        {
            this.latitude = getIntent().getDoubleExtra(Constants.KEY_LATITUDE, Data.latitude);
            this.longitude = getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, Data.longitude);
        }

        if(getIntent().hasExtra(Constants.KEY_GO_BACK))
        {
            goBack = 1;
        }

        if(getIntent().hasExtra("comingFromPayment"))
        {
            dontShowTurorialAgain = 1;
        }

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch() || Data.getPayData().getPay().getHasVpa() == 1) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_pay_tutorial);

        ActivityPayTutorialLayout = (RelativeLayout) findViewById(R.id.activity_pay_tutorial);
        new ASSL(PayTutorial.this, ActivityPayTutorialLayout, 1134, 720, false);

        viewPager = (ViewPager) findViewById(R.id.vpPayTutorial);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);

        btnNext.setTypeface(Fonts.mavenMedium(PayTutorial.this));
        btnSkip.setTypeface(Fonts.mavenMedium(PayTutorial.this));

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.fragment_pay_tutorial_screen1,
                R.layout.fragment_pay_tutorial_screen2};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        vpPayTutorialAdapter = new PayTutorialViewPagerAdapter();
        viewPager.setAdapter(vpPayTutorialAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });

    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                // btnNext.setText(getString(R.string.start));
                btnNext.setText(getString(R.string.GotIt));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

//        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
//        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            // dots[i].setTextColor(colorsInactive[currentPage]);
            dots[i].setTextColor(getResources().getColor(R.color.color_inactive_dot));
            // dots[i].setTextColor(ContextCompat.getColor(context, R.color.color_inactive_dot));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.color_active_dot));
            // dots[currentPage].setTextColor(ContextCompat.getColor(context, R.color.color_active_dot));
            // dots[currentPage].setTextColor(colorsActive[currentPage]);

    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void launchHomeScreen() {
        // prefManager.setFirstTimeLaunch(false);

        Intent intent = new Intent(PayTutorial.this, MainActivity.class);
        intent.putExtra(Constants.KEY_LATITUDE, this.latitude);
        intent.putExtra(Constants.KEY_LONGITUDE, this.longitude);

        if(goBack == 1)
        {
            intent.putExtra(Constants.KEY_GO_BACK, 1);
            intent.putExtra("comingFromPayment", 1);
        }


        startActivity(intent);



        finish();
    }


    /**
     * View pager adapter
     */
    public class PayTutorialViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public PayTutorialViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);

            TextView tv1, tv2, tv3, tv4;
            if (position == 0)
            {
                tv1 = (TextView) view.findViewById(R.id.tv1);
                tv2 = (TextView) view.findViewById(R.id.tv2);
                tv3 = (TextView) view.findViewById(R.id.tv3);
                tv4 = (TextView) view.findViewById(R.id.tv4);

                tv1.setTypeface(Fonts.mavenMedium(PayTutorial.this));
                tv2.setTypeface(Fonts.mavenMedium(PayTutorial.this));
                tv3.setTypeface(Fonts.mavenMedium(PayTutorial.this));
                tv4.setTypeface(Fonts.mavenMedium(PayTutorial.this));
            }
            else if(position == 1)
            {
                tv1 = (TextView) view.findViewById(R.id.tv1);
                tv2 = (TextView) view.findViewById(R.id.tv2);

                tv1.setTypeface(Fonts.mavenMedium(PayTutorial.this));
                tv2.setTypeface(Fonts.mavenMedium(PayTutorial.this));
            }

            view.setLayoutParams(new LinearLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT));
            ASSL.DoMagic(view);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }


    @Override
    public void onBackPressed() {
        try {
            btnSkip.performClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
