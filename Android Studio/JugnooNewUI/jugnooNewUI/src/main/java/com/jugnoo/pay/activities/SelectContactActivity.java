package com.jugnoo.pay.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jugnoo.pay.adapters.SendMoneyPagerAdapter;
import com.jugnoo.pay.fragments.ContactsFragment;
import com.jugnoo.pay.fragments.PaymentFragment;
import com.sabkuchfresh.utils.AppConstant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.widgets.PagerSlidingTabStrip;

/**
 * Created by cl-macmini-38 on 9/21/16.
 */
public class SelectContactActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitleTxt;
    @BindView(R.id.back_btn)
    ImageButton backBtn;
    @OnClick(R.id.back_btn)
    void backBtnClicked() {
        onBackPressed();
    }
    @BindView(R.id.phone_et)
    public EditText searchET;
    private ImageView ivToolbarRefreshContacts;

    public boolean requestStatus=false;
    public static SelectContactActivity selectContactActivityObj;
    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private SendMoneyPagerAdapter sendMoneyPagerAdapter;
    private ImageView toolbarDivider, ivToolbarAddVPA, ivAddVPA;

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
       requestStatus = getIntent().getBooleanExtra(AppConstant.REQUEST_STATUS,false);


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        sendMoneyPagerAdapter = new SendMoneyPagerAdapter(this, getSupportFragmentManager(), requestStatus);
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
                    // commented on 29-12-2016
                    // ivToolbarAddVPA.setVisibility(View.VISIBLE);
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
        tabs.setVisibility(requestStatus ? View.GONE : View.VISIBLE);

        searchET.setTypeface(Fonts.mavenRegular(this));
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

    public PaymentFragment getPaymentFragment() {
        return (PaymentFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + 1);
    }

    public ContactsFragment getContactsFragment(){
        return (ContactsFragment) getSupportFragmentManager().findFragmentByTag(ContactsFragment.class.getName());
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

    public ImageView getIvAddVPA()
    {
        return ivAddVPA;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
