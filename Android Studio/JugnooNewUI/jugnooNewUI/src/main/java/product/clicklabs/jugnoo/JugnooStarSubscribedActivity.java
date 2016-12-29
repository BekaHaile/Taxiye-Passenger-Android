package product.clicklabs.jugnoo;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.adapters.StarBenefitsAdapter;
import product.clicklabs.jugnoo.adapters.StarMembershipAdapter;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by ankit on 27/12/16.
 */

public class JugnooStarSubscribedActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout relative;
    private TextView textViewTitle;
    private ImageView imageViewBack;
    private TextView tvCurrentPlanValue, tvExpiresOnValue;
    private RecyclerView rvBenefits;
    private StarMembershipAdapter starMembershipAdapter;
    private ArrayList<String> benefits = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugnoo_star_subscribed);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(JugnooStarSubscribedActivity.this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);

        ((TextView)findViewById(R.id.tvCurrentPlan)).setTypeface(Fonts.avenirMedium(this));
        ((TextView)findViewById(R.id.tvExpiresOn)).setTypeface(Fonts.avenirMedium(this));

        tvCurrentPlanValue = (TextView) findViewById(R.id.tvCurrentPlanValue); tvCurrentPlanValue.setTypeface(Fonts.mavenMedium(this));
        tvExpiresOnValue = (TextView) findViewById(R.id.tvExpiresOnValue); tvExpiresOnValue.setTypeface(Fonts.mavenMedium(this));
        rvBenefits = (RecyclerView) findViewById(R.id.rvBenefits);
        rvBenefits.setLayoutManager(new LinearLayoutManager(this));
        rvBenefits.setItemAnimator(new DefaultItemAnimator());
        rvBenefits.setHasFixedSize(false);

        starMembershipAdapter = new StarMembershipAdapter(JugnooStarSubscribedActivity.this, benefits);
        rvBenefits.setAdapter(starMembershipAdapter);

        //textViewDriverCarNumber.setText(DateOperations.convertDateOnlyViaFormat(DateOperations.utcToLocalWithTZFallback(datum.getOrderTime())));
    }


    public void performBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageViewBack:
                performBackPressed();
                break;
        }
    }
}
