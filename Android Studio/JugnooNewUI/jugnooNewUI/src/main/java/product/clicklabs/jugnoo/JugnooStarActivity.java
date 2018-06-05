package product.clicklabs.jugnoo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;

import product.clicklabs.jugnoo.adapters.StarMembershipAdapter;
import product.clicklabs.jugnoo.datastructure.StarPurchaseType;
import product.clicklabs.jugnoo.datastructure.SubscriptionData;
import product.clicklabs.jugnoo.fragments.StarSubscriptionCheckoutFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NonScrollListView;

/**
 * Created by ankit on 27/12/16.
 */

public class JugnooStarActivity extends RazorpayBaseActivity implements View.OnClickListener, GAAction, GACategory{

    private RelativeLayout relative, rlPlan1, rlPlan2;
    private TextView textViewTitle, tvSubTitle;
    private ImageView imageViewBack, ivRadio1, ivRadio2;
    private TextView tvActualAmount1, tvActualAmount2, tvAmount1, tvAmount2, tvPeriod1, tvPeriod2,
            tvSubPlanAmount, tvSubPlanDesc;
    private NonScrollListView rvBenefits;
    private StarMembershipAdapter starMembershipAdapter;
    private String selectedSubId;
    private RelativeLayout rlFragment;
    private Button bJoinNow;
    private SubscriptionData.Subscription subscription;
    private boolean fromFreshCheckout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugnoo_star);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(JugnooStarActivity.this, relative, 1134, 720, false);

        GAUtils.trackScreenView(JUGNOO+STAR+HOME);

        rlFragment = (RelativeLayout) findViewById(R.id.rlFragment);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);

        textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_JUGNOO_STAR);
        bJoinNow = (Button) findViewById(R.id.bJoinNow); bJoinNow.setTypeface(Fonts.mavenMedium(this)); bJoinNow.setOnClickListener(this);


        tvSubTitle = (TextView) findViewById(R.id.tvSubTitle); tvSubTitle.setTypeface(Fonts.mavenRegular(this));
        tvSubTitle.setText(getString(R.string.jugnoo_Star_title, getString(R.string.app_name)));
        rlPlan1 = (RelativeLayout) findViewById(R.id.rlPlan1); rlPlan1.setOnClickListener(this); rlPlan1.setVisibility(View.GONE);
        rlPlan2 = (RelativeLayout) findViewById(R.id.rlPlan2); rlPlan2.setOnClickListener(this); rlPlan2.setVisibility(View.GONE);
        ivRadio1 = (ImageView) findViewById(R.id.ivRadio1);
        ivRadio2 = (ImageView) findViewById(R.id.ivRadio2);
        tvActualAmount1 = (TextView) findViewById(R.id.tvActualAmount1); tvActualAmount1.setTypeface(Fonts.mavenRegular(this));
        tvActualAmount2 = (TextView) findViewById(R.id.tvActualAmount2); tvActualAmount2.setTypeface(Fonts.mavenRegular(this));
        tvAmount1 = (TextView) findViewById(R.id.tvAmount1); tvAmount1.setTypeface(Fonts.mavenRegular(this));
        tvAmount2 = (TextView) findViewById(R.id.tvAmount2); tvAmount2.setTypeface(Fonts.mavenRegular(this));
        tvPeriod1 = (TextView) findViewById(R.id.tvPeriod1); tvPeriod1.setTypeface(Fonts.mavenMedium(this));
        tvPeriod2 = (TextView) findViewById(R.id.tvPeriod2); tvPeriod2.setTypeface(Fonts.mavenMedium(this));
        rvBenefits = (NonScrollListView) findViewById(R.id.rvBenefits);
        tvSubPlanAmount = (TextView) findViewById(R.id.tvSubPlanAmount);
        tvSubPlanDesc = (TextView) findViewById(R.id.tvSubPlanDesc);

        if(getIntent().hasExtra("checkout_fragment")){
            fromFreshCheckout = true;
            selectedSubId = getIntent().getStringExtra("checkout_fragment");
            subscription = new Gson().fromJson(selectedSubId, SubscriptionData.Subscription.class);
            bJoinNow.performClick();
        }

        try {
            tvSubTitle.setText(Data.userData.getSubscriptionData().getSubscriptionTitleNew());
            tvSubPlanAmount.setText(Data.userData.getSubscriptionData().getSubscriptionScreenTitle());
            tvSubPlanDesc.setText(Data.userData.getSubscriptionData().getSubscriptionScreenSubtitle());
            if(Data.userData.getSubscriptionData().getSubscriptions() != null){
                for(int i=0; i<Data.userData.getSubscriptionData().getSubscriptions().size(); i++) {
                    if (i == 0) {
                        rlPlan1.setVisibility(View.VISIBLE);
                        if(Data.userData.getSubscriptionData().getSubscriptions().get(i).getInitialAmountText() != null && !TextUtils.isEmpty(Data.userData.getSubscriptionData().getSubscriptions().get(i).getInitialAmountText())){
                            tvActualAmount1.setVisibility(View.VISIBLE);
                            tvActualAmount1.setText(Data.userData.getSubscriptionData().getSubscriptions().get(i).getInitialAmountText()+" ");
                            tvActualAmount1.setTextColor(ContextCompat.getColor(this, R.color.green));
                            tvAmount1.setTextColor(ContextCompat.getColor(this, R.color.green));
                            //tvActualAmount1.setPaintFlags(tvActualAmount1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }
                        tvAmount1.setText(Data.userData.getSubscriptionData().getSubscriptions().get(i).getFinalAmountText());
                        tvPeriod1.setText(String.valueOf(Data.userData.getSubscriptionData().getSubscriptions().get(i).getPlanStringNew()));
                    } else if (i == 1) {
                        rlPlan2.setVisibility(View.VISIBLE);
                        if(Data.userData.getSubscriptionData().getSubscriptions().get(i).getInitialAmountText() != null && !TextUtils.isEmpty(Data.userData.getSubscriptionData().getSubscriptions().get(i).getInitialAmountText())){
                            tvActualAmount2.setVisibility(View.VISIBLE);
                            tvActualAmount2.setText(Data.userData.getSubscriptionData().getSubscriptions().get(i).getInitialAmountText()+" ");
                            tvActualAmount2.setTextColor(ContextCompat.getColor(this, R.color.green));
                            tvAmount2.setTextColor(ContextCompat.getColor(this, R.color.green));
                            //tvActualAmount2.setPaintFlags(tvActualAmount2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }
                        tvAmount2.setText(Data.userData.getSubscriptionData().getSubscriptions().get(i).getFinalAmountText());
                        tvPeriod2.setText(String.valueOf(Data.userData.getSubscriptionData().getSubscriptions().get(i).getPlanStringNew()));
                    }
                }
            }


            starMembershipAdapter = new StarMembershipAdapter(JugnooStarActivity.this, Data.userData.getSubscriptionData().getSubscriptionBenefits()
                    , new StarMembershipAdapter.Callback() {
                @Override
                public void onUnsubscribe() {
                }
            });

            rvBenefits.setAdapter(starMembershipAdapter);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((ScrollView)findViewById(R.id.scroll)).scrollTo(0, 0);
                }
            }, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }

        selectedPlan(rlPlan1, ivRadio1, 0);
    }

    private void selectedPlan(RelativeLayout rlPlan, ImageView ivRadio, int subId){
        try {
            //rlPlan1.setBackgroundResource(R.drawable.capsule_white_stroke);
            //rlPlan2.setBackgroundResource(R.drawable.capsule_white_stroke);
            ivRadio1.setImageResource(R.drawable.ic_radio_button_normal);
            ivRadio2.setImageResource(R.drawable.ic_radio_button_normal);

            //rlPlan.setBackgroundResource(R.drawable.capsule_white_theme_stroke);
            ivRadio.setImageResource(R.drawable.ic_order_status_green);
            subscription = Data.userData.getSubscriptionData().getSubscriptions().get(subId);
            selectedSubId = new Gson().toJson(subscription);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performBackPressed() {
        if(fromFreshCheckout){
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    //rlFragment.setVisibility(View.GONE);
                }
                super.onBackPressed();
            } else {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageViewBack:
                performBackPressed();
                break;
            case R.id.rlPlan1:
                selectedPlan(rlPlan1, ivRadio1, 0);
                try{GAUtils.event(SIDE_MENU, JUGNOO+STAR+PLAN+CLICKED, subscription.getPlanString());}catch(Exception e){}
                break;
            case R.id.rlPlan2:
                selectedPlan(rlPlan2, ivRadio2, 1);
                try{GAUtils.event(SIDE_MENU, JUGNOO+STAR+PLAN+CLICKED, subscription.getPlanString());}catch(Exception e){}
                break;
            case R.id.bJoinNow:
                openStarCheckoutFragment(JugnooStarActivity.this, rlFragment);
                GAUtils.event(SIDE_MENU, JUGNOO+STAR, JOIN_NOW+CLICKED);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }

    public void openStarCheckoutFragment(FragmentActivity activity, View container) {
        rlFragment.setVisibility(View.VISIBLE);
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                    .add(container.getId(), StarSubscriptionCheckoutFragment.newInstance(selectedSubId, StarPurchaseType.PURCHARE.getOrdinal()),
                            StarSubscriptionCheckoutFragment.class.getName())
                    .addToBackStack(StarSubscriptionCheckoutFragment.class.getName())
                    .commitAllowingStateLoss();
        }



}
