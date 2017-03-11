package product.clicklabs.jugnoo.tutorials;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.sabkuchfresh.home.TransactionUtils;

import product.clicklabs.jugnoo.BaseActivity;
import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by ankit on 10/03/17.
 */

public class NewUserChutiyapaa extends BaseFragmentActivity {

    private ImageView ivBack, ivTickReferral, ivLineReferral, ivTickProfile, ivLineProfile, ivTickWallet;
    private TransactionUtils transactionUtils;
    private TextView tvTitle, tvSkip;
    private RelativeLayout rlContainer, rlRoot;
    public CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_info);
        rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);
        try {
            if (rlRoot != null) {
                new ASSL(NewUserChutiyapaa.this, rlRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvSkip = (TextView) findViewById(R.id.tvSkip);
        rlContainer = (RelativeLayout) findViewById(R.id.rlContainer);
        ivTickReferral = (ImageView) findViewById(R.id.ivTickReferral);
        ivLineReferral = (ImageView) findViewById(R.id.ivLineReferral);
        ivTickProfile = (ImageView) findViewById(R.id.ivTickProfile);
        ivLineProfile = (ImageView) findViewById(R.id.ivLineProfile);
        ivTickWallet = (ImageView) findViewById(R.id.ivTickWallet);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentFragment() instanceof NewUserReferralFragment) {
                    getTransactionUtils().openNewUserCompleteProfileFragment(NewUserChutiyapaa.this, getRlContainer());
                } else if (getCurrentFragment() instanceof NewUserCompleteProfileFragment) {
                    getTransactionUtils().openNewUserWalletFragment(NewUserChutiyapaa.this, getRlContainer());
                } else if (getCurrentFragment() instanceof NewUserWalletFragment) {
                    performBackPressed();
                }
            }
        });
        callbackManager = CallbackManager.Factory.create();
        getTransactionUtils().openNewUserReferralFragment(NewUserChutiyapaa.this, rlContainer);
    }

    public TransactionUtils getTransactionUtils() {
        if (transactionUtils == null) {
            transactionUtils = new TransactionUtils();
        }
        return transactionUtils;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    public void setTickLineView() {
        try {
            if(getCurrentFragment() != null) {
                if (getCurrentFragment() instanceof NewUserReferralFragment) {
                    ivTickReferral.setImageResource(R.drawable.circle_red);
                } else if (getCurrentFragment() instanceof NewUserCompleteProfileFragment) {
                    ivTickReferral.setImageResource(R.drawable.ic_bar_check);
                    ivTickProfile.setImageResource(R.drawable.circle_red);
                    ivLineReferral.setBackgroundColor(ContextCompat.getColor(NewUserChutiyapaa.this, R.color.green));
                } else if (getCurrentFragment() instanceof NewUserWalletFragment) {
                    ivTickReferral.setImageResource(R.drawable.ic_bar_check);
                    ivTickProfile.setImageResource(R.drawable.ic_bar_check);
                    ivTickWallet.setImageResource(R.drawable.circle_red);
                    ivLineReferral.setBackgroundColor(ContextCompat.getColor(NewUserChutiyapaa.this, R.color.green));
                    ivLineProfile.setBackgroundColor(ContextCompat.getColor(NewUserChutiyapaa.this, R.color.green));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Fragment getCurrentFragment(){
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.rlContainer);
            return currentFragment;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public RelativeLayout getRlContainer() {
        return rlContainer;
    }

    public void performBackPressed(){
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
