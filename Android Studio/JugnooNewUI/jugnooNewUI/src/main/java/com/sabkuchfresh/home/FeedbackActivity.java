package com.sabkuchfresh.home;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.fragments.FeedbackFragment;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by gurmail on 25/08/16.
 */
public class FeedbackActivity extends BaseFragmentActivity implements View.OnClickListener {

    LinearLayout mainLayout, linearLayoutContainer;
    ImageView imageViewBack;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_new);

        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        new ASSL(this, mainLayout, 1134, 720, false);

        linearLayoutContainer = (LinearLayout) findViewById(R.id.linearLayoutContainer);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        title = (TextView) findViewById(R.id.title);
        title.getPaint().setShader(Utils.textColorGradient(this, title));
        imageViewBack.setOnClickListener(this);

        openFeedback();
    }

    /**
     * Method used to open feedback screen
     */
    private void openFeedback() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                .add(linearLayoutContainer.getId(), new FeedbackFragment(),
                        FeedbackFragment.class.getName())
                .addToBackStack(FeedbackFragment.class.getName())
                .commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        int tag = v.getId();
        switch (tag) {
            case R.id.imageViewBack:
                performBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }

    public void performBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
