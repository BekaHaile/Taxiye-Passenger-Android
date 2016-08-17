package com.sabkuchfresh.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.sabkuchfresh.fragments.AboutUsFragment;
import com.sabkuchfresh.fragments.FeedbackFragment;
import com.sabkuchfresh.fragments.FreshOrderHistoryFragment;
import com.sabkuchfresh.fragments.FreshOrderSummaryFragment;
import com.sabkuchfresh.fragments.FreshSupportFragment;
import com.sabkuchfresh.fragments.PromotionsFragment;
import com.sabkuchfresh.fragments.ReferralsFragment;
import com.sabkuchfresh.retrofit.model.OrderHistory;
import com.sabkuchfresh.retrofit.model.OrderItem;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Constants;
import com.sabkuchfresh.utils.DialogPopup;
import com.sabkuchfresh.utils.Log;

import org.json.JSONObject;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by Gurmail S. Kang on 5/3/16.
 */
public class SupportActivity extends BaseFragmentActivity implements View.OnClickListener {
    private final String TAG = SupportActivity.class.getSimpleName();
    private RelativeLayout drawerLayout;

    private LinearLayout relativeLayoutContainer;
    private ImageView backImage;
    private TextView textViewTitle;
    private TransactionUtils transactionUtils;
    private int fragmentValue = 0;

    private OrderHistory orderHistoryOpened;
    private int orderHistoryOpenedPosition;
    private CallbackManager callbackManager;

    public String mContactNum = "";
    public String orderId = "";
    public String questionType = "";
    public String question = "";
    public boolean skip = false;

    public CallbackManager getCallbackManager(){
        return callbackManager;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        drawerLayout = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, drawerLayout, 1134, 720, false);

        callbackManager = CallbackManager.Factory.create();

        relativeLayoutContainer = (LinearLayout) findViewById(R.id.linearLayoutContainer);
        backImage = (ImageView) findViewById(R.id.imageViewBack);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);

        backImage.setOnClickListener(this);
        fragmentValue = getIntent().getIntExtra(Constants.FRAGMENT_SELECTED, 0);

        if (fragmentValue == AppConstant.SupportType.SUPPORT) {
            mContactNum = getIntent().getStringExtra(Constants.ORDER_CONTACT);
            openSupport();
        } else if (fragmentValue == AppConstant.SupportType.HISTORY) {
            openOrderHistory();
        } else if (fragmentValue == AppConstant.SupportType.ABOUT) {
            openAboutFragment();
        } else if (fragmentValue == AppConstant.SupportType.NOTIFICATION) {
        } else if (fragmentValue == AppConstant.SupportType.SHARE) {
            openShareFragment();
        } else if (fragmentValue == AppConstant.SupportType.FEED_BACK) {
            orderId = getIntent().getStringExtra(Constants.ORDER_ID);
            questionType = getIntent().getStringExtra(Constants.QUESTION_TYPE);
            question = getIntent().getStringExtra(Constants.QUESTION);
            if (getIntent().hasExtra(Constants.SKIP))
                skip = getIntent().getBooleanExtra(Constants.SKIP, false);
            openFeedback();
        } else if (fragmentValue == AppConstant.SupportType.PROMO) {
            openPromoFragment();
        }
    }

    public LinearLayout getRelativeLayoutContainer() {
        return relativeLayoutContainer;
    }

    public OrderHistory getOrderHistoryOpened() {
        return orderHistoryOpened;
    }

    public void setOrderHistoryOpened(int position, OrderHistory orderHistoryOpened) {
        this.orderHistoryOpenedPosition = position;
        this.orderHistoryOpened = orderHistoryOpened;
    }

    public int getOrderHistoryOpenedPosition() {
        return orderHistoryOpenedPosition;
    }

    public void setOrderHistoryOpenedPosition(int orderHistoryOpenedPosition) {
        this.orderHistoryOpenedPosition = orderHistoryOpenedPosition;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(drawerLayout);
        System.gc();
    }

    public void openOrderHistory() {
        textViewTitle.setText(getResources().getString(R.string.order_fragment));
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new FreshOrderHistoryFragment(),
                        FreshOrderHistoryFragment.class.getName())
                .addToBackStack(FreshOrderHistoryFragment.class.getName())
                .commitAllowingStateLoss();
    }

    public void openSupport() {
        textViewTitle.setText(getResources().getString(R.string.support_fragment));
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new FreshSupportFragment(),
                        FreshSupportFragment.class.getName())
                .addToBackStack(FreshSupportFragment.class.getName())
                .commitAllowingStateLoss();
    }

    public void openFeedback() {
        textViewTitle.setText(getResources().getString(R.string.feedback_fragment));
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new FeedbackFragment(),
                        FeedbackFragment.class.getName())
                .addToBackStack(FeedbackFragment.class.getName())
                .commitAllowingStateLoss();
    }

    public void openAboutFragment() {
        textViewTitle.setText(getResources().getString(R.string.about_fragment));
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new AboutUsFragment(),
                        AboutUsFragment.class.getName())
                .addToBackStack(AboutUsFragment.class.getName())
                .commitAllowingStateLoss();
    }

    public void openShareFragment() {
        textViewTitle.setText(getResources().getString(R.string.share_fragment));
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new ReferralsFragment(),
                        ReferralsFragment.class.getName())
                .addToBackStack(ReferralsFragment.class.getName())
                .commitAllowingStateLoss();
    }

    public void openPromoFragment() {
        textViewTitle.setText(getResources().getString(R.string.promo_fragment));
        getSupportFragmentManager().beginTransaction()
                .add(relativeLayoutContainer.getId(), new PromotionsFragment(),
                        PromotionsFragment.class.getName())
                .addToBackStack(PromotionsFragment.class.getName())
                .commitAllowingStateLoss();
    }


    public void openOrderFeedback() {
        textViewTitle.setText(getResources().getString(R.string.feedback_fragment));
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                .add(relativeLayoutContainer.getId(), new FeedbackFragment(),
                        FeedbackFragment.class.getName())
                .addToBackStack(FeedbackFragment.class.getName())
                .hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
                        .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                .commitAllowingStateLoss();

//        getSupportFragmentManager().beginTransaction()
//                .add(relativeLayoutContainer.getId(), new FeedbackFragment(),
//                        FeedbackFragment.class.getName())
//                .addToBackStack(FeedbackFragment.class.getName())
//                .commitAllowingStateLoss();
    }


//    public void openFeedback() {
//        textViewTitle.setText(getResources().getString(R.string.support_fragment));
//        getSupportFragmentManager().beginTransaction()
//                .add(relativeLayoutContainer.getId(), new FeedbackFragment(),
//                        FeedbackFragment.class.getName())
//                .addToBackStack(FeedbackFragment.class.getName())
//                .commitAllowingStateLoss();
//    }

    public TransactionUtils getTransactionUtils() {
        if (transactionUtils == null) {
            transactionUtils = new TransactionUtils();
        }
        return transactionUtils;
    }

    public void fragmentUISetup(Fragment fragment) {
        if (fragment instanceof FreshOrderSummaryFragment) {
            textViewTitle.setText(getResources().getString(R.string.order_fragment));
        } else if (fragment instanceof FreshOrderHistoryFragment) {
            textViewTitle.setText(getResources().getString(R.string.order_fragment));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                onBackPressed();
                break;
        }
    }


    public void saveHistoryCardToSP(OrderHistory orderHistory) {
        try {
            Prefs.with(this).save(Constants.SP_FRESH_CART, Constants.EMPTY_JSON_OBJECT);

            JSONObject jCart = new JSONObject();
            if (orderHistory != null && orderHistory.getOrderItems() != null) {

                    for (OrderItem subItem : orderHistory.getOrderItems()) {
                        if (subItem.getItemQuantity() > 0) {
                            try {
                                jCart.put(String.valueOf(subItem.getSubItemId()), (int) subItem.getItemQuantity());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

            }
            Prefs.with(this).save(Constants.SP_FRESH_CART, jCart.toString());
            sendMessage();
            DialogPopup.showLoadingDialog(SupportActivity.this, "Please wait...");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Data.LOCAL_BROADCAST);
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    public FreshOrderHistoryFragment getFreshOrderHistoryFragment() {
        return (FreshOrderHistoryFragment) getSupportFragmentManager().findFragmentByTag(FreshOrderHistoryFragment.class.getName());
    }
}
