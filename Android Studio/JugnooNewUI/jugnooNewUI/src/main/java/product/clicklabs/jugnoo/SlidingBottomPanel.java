package product.clicklabs.jugnoo;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import product.clicklabs.jugnoo.adapters.ShareFragmentAdapter;
import product.clicklabs.jugnoo.adapters.SlidingBottomFragmentAdapter;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.fragments.SlidingBottomOffersFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import product.clicklabs.jugnoo.widgets.PagerSlidingTabStrip;

/**
 * Created by Ankit on 1/7/16.
 */
public class SlidingBottomPanel {

    private FragmentActivity activity;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private PagerSlidingTabStrip tabs;
    private String TAG = "slidingPanel";
    private ViewPager viewPager;
    private SlidingBottomFragmentAdapter slidingBottomFragmentAdapter;
    private TextView textViewMinFareValue, textViewOffersValue, textViewCashValue;

    private PromoCoupon selectedCoupon;
    private PromoCoupon noSelectionCoupon = new CouponInfo(-1, "Don't apply coupon on this ride");

    public SlidingBottomPanel(FragmentActivity activity, View view) {
        this.activity = activity;
        initComponents(view);
    }

    private void initComponents(View view){
        //SlidingUp Layout
        ((TextView)view.findViewById(R.id.textViewMinFare)).setTypeface(Fonts.mavenLight(activity));
        textViewMinFareValue = (TextView)view.findViewById(R.id.textViewMinFareValue);textViewMinFareValue.setTypeface(Fonts.mavenRegular(activity));
        ((TextView)view.findViewById(R.id.textViewOffers)).setTypeface(Fonts.mavenLight(activity));
        textViewOffersValue = (TextView)view.findViewById(R.id.textViewOffersValue);textViewOffersValue.setTypeface(Fonts.mavenRegular(activity));
        textViewCashValue = (TextView)view.findViewById(R.id.textViewCashValue);textViewCashValue.setTypeface(Fonts.mavenRegular(activity));


        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidingLayout);
        slidingUpPanelLayout.setParallaxOffset((int) (260 * ASSL.Yscale()));
        slidingUpPanelLayout.setPanelHeight((int) (110 * ASSL.Yscale()));

        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        slidingBottomFragmentAdapter = new SlidingBottomFragmentAdapter(activity.getSupportFragmentManager());
        viewPager.setAdapter(slidingBottomFragmentAdapter);

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabs.setTextColorResource(R.color.theme_color, R.color.grey_dark);
        tabs.setViewPager(viewPager);

        update(null);
    }

    public void slideOnClick(View view){
        if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        } else {
            switch (view.getId()) {
                case R.id.linearLayoutCash:
                        Log.v("on click", "linearLayoutCash");
                    viewPager.setCurrentItem(0, true);
                    break;

                case R.id.linearLayoutFare:
                        Log.v("on click", "linearLayoutFare");
                    viewPager.setCurrentItem(1, true);
                    break;

                case R.id.linearLayoutOffers:
                        Log.v("on click", "linearLayoutOffers");
                    viewPager.setCurrentItem(2, true);
                    break;
            }
        }
    }

    public void update(ArrayList<PromoCoupon> promoCoupons){
        textViewCashValue.setText(String.format(activity.getResources().getString(R.string.ruppes_value_format_without_space)
                , Utils.getMoneyDecimalFormat().format(Data.userData.getTotalWalletBalance())));
        textViewMinFareValue.setText(String.format(activity.getResources().getString(R.string.ruppes_value_format_without_space)
                , Utils.getMoneyDecimalFormat().format(Data.fareStructure.fixedFare)));

        if(promoCoupons != null) {
            if (promoCoupons.size() > 0) {
                selectedCoupon = noSelectionCoupon;
            } else {
                selectedCoupon = new CouponInfo(0, "");
            }
            textViewOffersValue.setText(""+promoCoupons.size());
        } else{
            textViewOffersValue.setText("0");
        }

        Fragment frag = slidingBottomFragmentAdapter.getItem(2);
        if(frag != null && frag instanceof SlidingBottomOffersFragment){
            ((SlidingBottomOffersFragment)frag).update(promoCoupons);
        }


    }

    public SlidingUpPanelLayout getSlidingUpPanelLayout(){
        return slidingUpPanelLayout;
    }

}
