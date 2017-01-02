package product.clicklabs.jugnoo;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sabkuchfresh.fragments.FreshCheckoutMergedFragment;

import java.util.ArrayList;
import java.util.Arrays;

import product.clicklabs.jugnoo.adapters.StarBenefitsAdapter;
import product.clicklabs.jugnoo.datastructure.SubscriptionData;
import product.clicklabs.jugnoo.fragments.StarSubscriptionCheckoutFragment;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by ankit on 27/12/16.
 */

public class JugnooStarActivity extends BaseFragmentActivity implements View.OnClickListener{

    private RelativeLayout relative, rlPlan1, rlPlan2;
    private TextView textViewTitle, tvSubTitle;
    private ImageView imageViewBack, ivRadio1, ivRadio2;
    private TextView tvActualAmount1, tvActualAmount2, tvAmount1, tvAmount2, tvPeriod1, tvPeriod2;
    private RecyclerView rvBenefits;
    private StarBenefitsAdapter starBenefitsAdapter;
    private String selectedSubId;
    private RelativeLayout rlFragment;
    private Button bJoinNow;
    private SubscriptionData.Subscription subscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugnoo_star);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(JugnooStarActivity.this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);

        textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_JUGNOO_STAR);
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));
        bJoinNow = (Button) findViewById(R.id.bJoinNow); bJoinNow.setTypeface(Fonts.avenirNext(this)); bJoinNow.setOnClickListener(this);

        rlFragment = (RelativeLayout) findViewById(R.id.rlFragment);
        tvSubTitle = (TextView) findViewById(R.id.tvSubTitle); tvSubTitle.setTypeface(Fonts.avenirMedium(this));
        rlPlan1 = (RelativeLayout) findViewById(R.id.rlPlan1); rlPlan1.setOnClickListener(this); rlPlan1.setVisibility(View.GONE);
        rlPlan2 = (RelativeLayout) findViewById(R.id.rlPlan2); rlPlan2.setOnClickListener(this); rlPlan2.setVisibility(View.GONE);
        ivRadio1 = (ImageView) findViewById(R.id.ivRadio1);
        ivRadio2 = (ImageView) findViewById(R.id.ivRadio2);
        tvActualAmount1 = (TextView) findViewById(R.id.tvActualAmount1); tvActualAmount1.setTypeface(Fonts.mavenMedium(this));
        tvActualAmount2 = (TextView) findViewById(R.id.tvActualAmount2); tvActualAmount2.setTypeface(Fonts.mavenMedium(this));
        tvAmount1 = (TextView) findViewById(R.id.tvAmount1); tvAmount1.setTypeface(Fonts.avenirNext(this));
        tvAmount2 = (TextView) findViewById(R.id.tvAmount2); tvAmount2.setTypeface(Fonts.avenirNext(this));
        tvPeriod1 = (TextView) findViewById(R.id.tvPeriod1); tvPeriod1.setTypeface(Fonts.avenirMedium(this));
        tvPeriod2 = (TextView) findViewById(R.id.tvPeriod2); tvPeriod2.setTypeface(Fonts.avenirMedium(this));
        rvBenefits = (RecyclerView) findViewById(R.id.rvBenefits);
        rvBenefits.setLayoutManager(new LinearLayoutManager(this));
        rvBenefits.setItemAnimator(new DefaultItemAnimator());
        rvBenefits.setHasFixedSize(false);


        try {
            tvSubTitle.setText(Data.userData.getSubscriptionData().getSubscriptionTitle());
            if(Data.userData.getSubscriptionData().getSubscriptions() != null){
                for(int i=0; i<Data.userData.getSubscriptionData().getSubscriptions().size(); i++) {
                    if (i == 0) {
                        rlPlan1.setVisibility(View.VISIBLE);
                        if(Data.userData.getSubscriptionData().getSubscriptions().get(i).getInitialAmount() != null && Data.userData.getSubscriptionData().getSubscriptions().get(i).getInitialAmount() !=0){
                            tvActualAmount1.setVisibility(View.VISIBLE);
                            tvActualAmount1.setText(String.valueOf(Data.userData.getSubscriptionData().getSubscriptions().get(i).getInitialAmount()));
                            tvActualAmount1.setPaintFlags(tvActualAmount1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }
                        tvAmount1.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space),
                                String.valueOf(Data.userData.getSubscriptionData().getSubscriptions().get(i).getAmount())));
                        tvPeriod1.setText(String.valueOf(Data.userData.getSubscriptionData().getSubscriptions().get(i).getPlanString()));
                    } else if (i == 1) {
                        rlPlan2.setVisibility(View.VISIBLE);
                        if(Data.userData.getSubscriptionData().getSubscriptions().get(i).getInitialAmount() != null && Data.userData.getSubscriptionData().getSubscriptions().get(i).getInitialAmount() !=0){
                            tvActualAmount2.setVisibility(View.VISIBLE);
                            tvActualAmount2.setText(String.valueOf(Data.userData.getSubscriptionData().getSubscriptions().get(i).getInitialAmount()));
                            tvActualAmount2.setPaintFlags(tvActualAmount2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }
                        tvAmount2.setText(String.format(getResources().getString(R.string.rupees_value_format_without_space),
                                String.valueOf(Data.userData.getSubscriptionData().getSubscriptions().get(i).getAmount())));
                        tvPeriod2.setText(String.valueOf(Data.userData.getSubscriptionData().getSubscriptions().get(i).getPlanString()));
                    }
                }
            }

            String tempStr = Data.userData.getSubscriptionData().getSubTextAutos()+";;;"+Data.userData.getSubscriptionData().getSubTextFresh()+";;;"+Data.userData.getSubscriptionData().getSubTextMeals();
            String[] strArray = tempStr.split(";;;");
            ArrayList<String> benefits = new ArrayList<>(Arrays.asList(strArray));

            starBenefitsAdapter = new StarBenefitsAdapter(JugnooStarActivity.this, benefits);
            rvBenefits.setAdapter(starBenefitsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        selectedPlan(rlPlan1, ivRadio1, 0);
    }

    private void selectedPlan(RelativeLayout rlPlan, ImageView ivRadio, int subId){
        rlPlan1.setBackgroundResource(R.drawable.capsule_white_stroke);
        rlPlan2.setBackgroundResource(R.drawable.capsule_white_stroke);
        ivRadio1.setImageResource(R.drawable.ic_radio_button_normal);
        ivRadio2.setImageResource(R.drawable.ic_radio_button_normal);

        rlPlan.setBackgroundResource(R.drawable.capsule_white_theme_stroke);
        ivRadio.setImageResource(R.drawable.ic_radio_button_selected);
        subscription = Data.userData.getSubscriptionData().getSubscriptions().get(subId);
        selectedSubId = new Gson().toJson(subscription);
    }

    public void performBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            if(getSupportFragmentManager().getBackStackEntryCount() == 1){
                rlFragment.setVisibility(View.GONE);
            }
            super.onBackPressed();
        } else {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
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
                break;
            case R.id.rlPlan2:
                selectedPlan(rlPlan2, ivRadio2, 1);
                break;
            case R.id.bJoinNow:
                openStarCheckoutFragment(JugnooStarActivity.this, rlFragment);
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
                    .add(container.getId(), StarSubscriptionCheckoutFragment.newInstance(selectedSubId),
                            StarSubscriptionCheckoutFragment.class.getName())
                    .addToBackStack(StarSubscriptionCheckoutFragment.class.getName())
                    .commitAllowingStateLoss();
        }
}
