package com.sabkuchfresh.fatafatchatpay;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.sabkuchfresh.datastructure.FuguCustomActionModel;
import com.sabkuchfresh.home.FreshActivity;

import product.clicklabs.jugnoo.AboutActivity;
import product.clicklabs.jugnoo.AccountActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JugnooStarActivity;
import product.clicklabs.jugnoo.JugnooStarSubscribedActivity;
import product.clicklabs.jugnoo.NotificationCenterActivity;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.promotion.PromotionActivity;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by cl-macmini-01 on 12/21/17.
 */
public class ChatCustomActionBroadCastReceiver extends BroadcastReceiver {

    final String ACTION_NATIVE_ACTIVITY = "NATIVE_ACTIVITY";
    private Gson  gson = new Gson();

    @Override
    public void onReceive(final Context context, final Intent intent) {

        // handle incoming fugu broadcast
        String payload = intent.getStringExtra(Constants.FUGU_CUSTOM_ACTION_PAYLOAD);
        FuguCustomActionModel customActionModel = gson.fromJson(payload, FuguCustomActionModel.class);

        if (customActionModel != null) {
            int linkIndex = Integer.parseInt(customActionModel.getReference());

            if (customActionModel.getActionType().equalsIgnoreCase(ACTION_NATIVE_ACTIVITY)) {

                // get link index
                try {
                    if (linkIndex == AppLinkIndex.FATAFAT_PAY_VIA_CHAT.getOrdinal()) {
                        // open fatafat chat pay passing in orderId and amount
                        if (customActionModel.getOrderId() != 0 && customActionModel.getAmount() != 0) {
                            Intent payIntent = new Intent(context,FatafatChatPayActivity.class);
                            payIntent.putExtra(Constants.KEY_ORDER_ID, customActionModel.getOrderId());
                            payIntent.putExtra(Constants.KEY_AMOUNT, customActionModel.getAmount());
                            payIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(payIntent);
                        }else{
                            handleDeepLink(linkIndex,context);
                        }

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void handleDeepLink(int deeplink, Context activity) {

        if (AppLinkIndex.WALLET_TRANSACTIONS.getOrdinal() == deeplink) {
            Intent intent = new Intent();
            intent.setClass(activity, PaymentActivity.class);
            intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
            intent.putExtra(Constants.KEY_WALLET_TRANSACTIONS, 1);
            activity.startActivity(intent);
        } else if (AppLinkIndex.JUGNOO_STAR.getOrdinal() == deeplink) {
            activity.startActivity(new Intent(activity, JugnooStarSubscribedActivity.class));
        } else if (AppLinkIndex.SUBSCRIPTION_PLAN_OPTION_SCREEN.getOrdinal() == deeplink) {
            if ((Data.userData.getSubscriptionData().getSubscribedUser() != null && Data.userData.getSubscriptionData().getSubscribedUser() == 1)
                    || Data.userData.isSubscriptionActive()) {
                activity.startActivity(new Intent(activity, JugnooStarSubscribedActivity.class));
            } else {
                activity.startActivity(new Intent(activity, JugnooStarActivity.class));
            }
        } else if (AppLinkIndex.NOTIFICATION_CENTER.getOrdinal() == deeplink) {
            activity.startActivity(new Intent(activity, NotificationCenterActivity.class));
        } else if (AppLinkIndex.ACCOUNT.getOrdinal() == deeplink) {
            activity.startActivity(new Intent(activity, AccountActivity.class));

        } else if (AppLinkIndex.ABOUT.getOrdinal() == deeplink) {
            activity.startActivity(new Intent(activity, AboutActivity.class));

        } else if (AppLinkIndex.PROMOTIONS.getOrdinal() == deeplink) {
            activity.startActivity(new Intent(activity, PromotionActivity.class));

        } else if (AppLinkIndex.JUGNOO_CASH.getOrdinal() == deeplink) {
            Intent intent = new Intent(activity, PaymentActivity.class);
            intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
            activity.startActivity(intent);
        } else if(deeplink!=-1){

        }
    }


}
