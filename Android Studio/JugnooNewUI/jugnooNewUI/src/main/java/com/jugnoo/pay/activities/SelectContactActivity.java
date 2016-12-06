package com.jugnoo.pay.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jugnoo.pay.adapters.ContactsListAdapter;
import com.jugnoo.pay.adapters.PaymentAddressAdapter;
import com.jugnoo.pay.adapters.SendMoneyPagerAdapter;
import com.jugnoo.pay.fragments.ContactsFragment;
import com.jugnoo.pay.fragments.PaymentFragment;
import com.jugnoo.pay.models.SelectUser;
import com.jugnoo.pay.utils.CallProgressWheel;
import com.jugnoo.pay.utils.CommonMethods;
import com.jugnoo.pay.utils.RecyclerViewClickListener;
import com.jugnoo.pay.utils.SharedPreferencesName;
import com.jugnoo.pay.utils.Validator;
import com.sabkuchfresh.utils.AppConstant;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.promotion.adapters.PromotionsFragmentAdapter;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.widgets.PagerSlidingTabStrip;

/**
 * Created by cl-macmini-38 on 9/21/16.
 */
public class SelectContactActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitleTxt;
    @Bind(R.id.back_btn)
    ImageButton backBtn;
    @OnClick(R.id.back_btn)
    void backBtnClicked() {
        onBackPressed();
    }
    @Bind(R.id.phone_et)
    public EditText searchET;
    private String accessToken;
//    / ArrayList
   private ArrayList<SelectUser> selectUsers;
   private List<SelectUser> temp;
    // Cursor to load contacts list
    private Cursor phones, email;
    private ImageView ivToolbarRefreshContacts;

    // Pop up
   private ContentResolver resolver;
    public boolean requestStatus=false;
    public static SelectContactActivity selectContactActivityObj;
    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private SendMoneyPagerAdapter sendMoneyPagerAdapter;
    private ImageView toolbarDivider, ivToolbarAddVPA;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        ButterKnife.bind(this);
        toolbarTitleTxt.setText(R.string.select_contact_screen); toolbarTitleTxt.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);

        ivToolbarRefreshContacts = (ImageView) findViewById(R.id.ivToolbarRefreshContacts); ivToolbarRefreshContacts.setVisibility(View.VISIBLE);
        ivToolbarAddVPA = (ImageView) findViewById(R.id.ivToolbarAddVPA); ivToolbarAddVPA.setVisibility(View.GONE);
        toolbarDivider = (ImageView) findViewById(R.id.toolbarDivider); toolbarDivider.setVisibility(View.GONE);

        selectContactActivityObj = this;
        accessToken = Data.userData.accessToken;          //Prefs.with(SelectContactActivity.this).getString(SharedPreferencesName.ACCESS_TOKEN, "");
       requestStatus = getIntent().getBooleanExtra(AppConstant.REQUEST_STATUS,false);

        selectUsers = new ArrayList<SelectUser>();
        resolver = this.getContentResolver();


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        sendMoneyPagerAdapter = new SendMoneyPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(sendMoneyPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    ivToolbarRefreshContacts.setVisibility(View.VISIBLE);
                    ivToolbarAddVPA.setVisibility(View.GONE);
                } else{
                    ivToolbarAddVPA.setVisibility(View.VISIBLE);
                    ivToolbarRefreshContacts.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextSize((int) (ASSL.Xscale() * 32f));
        tabs.setTextColorResource(R.color.text_color, R.color.text_color_light);
        tabs.setTypeface(Fonts.mavenMedium(this), Typeface.NORMAL);
        tabs.setViewPager(viewPager);

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + viewPager.getCurrentItem());
                if (page != null) {
                    if(page instanceof ContactsFragment){
                        ((ContactsFragment)page).getAdapter().filter(editable.toString());
                    } else if(page instanceof PaymentFragment){
                        ((PaymentFragment)page).getPaymentAddressAdapter().filter(editable.toString());
                    }
                }
            }
        });


    }

    public EditText getSearchET() {
        return searchET;
    }

    public boolean isRequestStatus() {
        return requestStatus;
    }

    public ImageView getIvToolbarRefreshContacts() {
        return ivToolbarRefreshContacts;
    }

    public ImageView getIvToolbarAddVPA() {
        return ivToolbarAddVPA;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(phones != null && !phones.isClosed()){
            phones.close();
        }

    }

}
