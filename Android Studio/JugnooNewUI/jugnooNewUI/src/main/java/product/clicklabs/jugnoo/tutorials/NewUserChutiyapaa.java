package product.clicklabs.jugnoo.tutorials;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

    private ImageView ivBack;
    private TransactionUtils transactionUtils;
    private RelativeLayout rlContainer, rlRoot;

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

        ivBack = (ImageView) findViewById(R.id.ivBack);
        rlContainer = (RelativeLayout) findViewById(R.id.rlContainer);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        getTransactionUtils().openNewUserReferralFragment(NewUserChutiyapaa.this, rlContainer);
    }

    public TransactionUtils getTransactionUtils() {
        if (transactionUtils == null) {
            transactionUtils = new TransactionUtils();
        }
        return transactionUtils;
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
}
