package product.clicklabs.jugnoo.tutorials;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.sabkuchfresh.home.TransactionUtils;

import product.clicklabs.jugnoo.BaseFragmentActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by ankit on 10/03/17.
 */

public class NewUserFlow extends BaseFragmentActivity {

    private ImageView ivBack, ivTickReferral, ivLineReferral, ivTickProfile, ivLineProfile, ivTickWallet,
            ivLineReferralFill, ivLineProfileFill;
    private TransactionUtils transactionUtils;
    private TextView tvTitle, tvSkip;
    private RelativeLayout rlContainer, rlRoot, rlBar;
    public CallbackManager callbackManager;
    private boolean fromMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_info);
        rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);
        try {
            if (rlRoot != null) {
                new ASSL(NewUserFlow.this, rlRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvSkip = (TextView) findViewById(R.id.tvSkip);
        rlBar = (RelativeLayout) findViewById(R.id.rlBar);
        rlContainer = (RelativeLayout) findViewById(R.id.rlContainer);
        ivTickReferral = (ImageView) findViewById(R.id.ivTickReferral);
        ivLineReferral = (ImageView) findViewById(R.id.ivLineReferral);
        ivTickProfile = (ImageView) findViewById(R.id.ivTickProfile);
        ivLineProfile = (ImageView) findViewById(R.id.ivLineProfile);
        ivTickWallet = (ImageView) findViewById(R.id.ivTickWallet);
        ivLineReferralFill = (ImageView) findViewById(R.id.ivLineReferralFill); ivLineReferralFill.setVisibility(View.GONE);
        ivLineProfileFill = (ImageView) findViewById(R.id.ivLineProfileFill); ivLineProfileFill.setVisibility(View.GONE);

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
                    if(((Data.userData.getSignupTutorial().getDs2() != null
                            && Data.userData.getSignupTutorial().getDs2() == 0)
                            &&(Data.userData.getSignupTutorial().getDs3() != null
                            && Data.userData.getSignupTutorial().getDs3() == 0))){
                        performBackPressed();
                    } else {
                        getTransactionUtils().openNewUserCompleteProfileFragment(NewUserFlow.this, getRlContainer());
                    }
                } else if (getCurrentFragment() instanceof NewUserCompleteProfileFragment) {
                    if(fromMenu){
                        performBackPressed();
                    } else {
                        getTransactionUtils().openNewUserWalletFragment(NewUserFlow.this, getRlContainer());
                    }
                } else if (getCurrentFragment() instanceof NewUserWalletFragment) {
                    performBackPressed();
                }
            }
        });
        callbackManager = CallbackManager.Factory.create();



        if(getIntent().hasExtra(Constants.KEY_MENU_SIGNUP_TUTORIAL)){
            fromMenu = getIntent().getExtras().getBoolean(Constants.KEY_MENU_SIGNUP_TUTORIAL, false);
            if(fromMenu){
                rlBar.setVisibility(View.GONE);
                getTransactionUtils().openNewUserCompleteProfileFragment(NewUserFlow.this, rlContainer);
            }
        } else {
            setTickViewInit();
            if (Data.userData.getSignupTutorial() != null) {
                if (Data.userData.getSignupTutorial().getDs1() != null
                        && Data.userData.getSignupTutorial().getDs1() == 1) {
                    //Data.userData.getSignupTutorial().setDs1(0);
                    getTransactionUtils().openNewUserReferralFragment(NewUserFlow.this, rlContainer);
                } else if (Data.userData.getSignupTutorial().getDs2() != null
                        && Data.userData.getSignupTutorial().getDs2() == 1) {
                    getTransactionUtils().openNewUserCompleteProfileFragment(NewUserFlow.this, rlContainer);
                } else if (Data.userData.getSignupTutorial().getDs3() != null
                        && Data.userData.getSignupTutorial().getDs3() == 1) {
                    getTransactionUtils().openNewUserWalletFragment(NewUserFlow.this, rlContainer);
                }
            }
        }

    }

    public TransactionUtils getTransactionUtils() {
        if (transactionUtils == null) {
            transactionUtils = new TransactionUtils();
        }
        return transactionUtils;
    }

    public boolean isFromMenu() {
        return fromMenu;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public ImageView getIvBack() {
        return ivBack;
    }

    public ImageView getIvTickWallet() {
        return ivTickWallet;
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    private void setTickViewInit(){
        rlBar.setVisibility(View.VISIBLE);
        ivTickReferral.setVisibility(View.GONE);
        ivLineReferral.setVisibility(View.GONE);
        ivTickProfile.setVisibility(View.GONE);
        ivLineProfile.setVisibility(View.GONE);
        ivTickWallet.setVisibility(View.GONE);
        if(Data.userData.getSignupTutorial() != null){
            if((Data.userData.getSignupTutorial().getDs1() != null
            && Data.userData.getSignupTutorial().getDs1() == 1)
                    && ((Data.userData.getSignupTutorial().getDs2() != null
            && Data.userData.getSignupTutorial().getDs2() == 1)
                    || (Data.userData.getSignupTutorial().getDs3() != null
            && Data.userData.getSignupTutorial().getDs3() == 1))){
                ivTickReferral.setVisibility(View.VISIBLE);
                ivLineReferral.setVisibility(View.VISIBLE);
                ivLineReferralFill.setVisibility(View.VISIBLE);
            }
            if(Data.userData.getSignupTutorial().getDs2() != null
                    && Data.userData.getSignupTutorial().getDs2() == 1){
                ivTickProfile.setVisibility(View.VISIBLE);
                ivLineProfile.setVisibility(View.VISIBLE);
                ivLineProfileFill.setVisibility(View.VISIBLE);
            }
            if(Data.userData.getSignupTutorial().getDs3() != null
                    && Data.userData.getSignupTutorial().getDs3() == 1){
                ivTickWallet.setVisibility(View.VISIBLE);
            } else{
                ivLineProfile.setVisibility(View.GONE);
                ivLineProfileFill.setVisibility(View.GONE);
            }
        }
    }

    public void setTickLineView() {
        try {
            if(getCurrentFragment() != null) {
                if (getCurrentFragment() instanceof NewUserReferralFragment) {
                    ivTickReferral.setImageResource(R.drawable.circle_yellow);
                } else if (getCurrentFragment() instanceof NewUserCompleteProfileFragment) {
                    ivTickReferral.setImageResource(R.drawable.ic_bar_check);
                    ivTickProfile.setImageResource(R.drawable.circle_yellow);
                    ivLineReferralFill.setImageResource(R.drawable.bg_green);
                } else if (getCurrentFragment() instanceof NewUserWalletFragment) {
                    ivTickReferral.setImageResource(R.drawable.ic_bar_check);
                    ivTickProfile.setImageResource(R.drawable.ic_bar_check);
                    ivTickWallet.setImageResource(R.drawable.circle_yellow);
                    ivLineReferralFill.setImageResource(R.drawable.bg_green);
                    ivLineProfileFill.setImageResource(R.drawable.bg_green);
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
