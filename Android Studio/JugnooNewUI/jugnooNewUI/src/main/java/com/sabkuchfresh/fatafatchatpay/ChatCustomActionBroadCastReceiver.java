package com.sabkuchfresh.fatafatchatpay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.sabkuchfresh.datastructure.FuguCustomActionModel;

import product.clicklabs.jugnoo.AboutActivity;
import product.clicklabs.jugnoo.AccountActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.JugnooStarActivity;
import product.clicklabs.jugnoo.JugnooStarSubscribedActivity;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.promotion.PromotionActivity;
import product.clicklabs.jugnoo.promotion.ShareActivity;
import product.clicklabs.jugnoo.wallet.PaymentActivity;
import product.clicklabs.jugnoo.wallet.models.PaymentActivityPath;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by cl-macmini-01 on 12/21/17.
 */
public class ChatCustomActionBroadCastReceiver extends BroadcastReceiver {

    final String ACTION_NATIVE_ACTIVITY = "NATIVE_ACTIVITY";
    final String ACTION_OPEN_NEW_CHAT= "OPEN_NEW_CHAT";
    private Gson  gson = new Gson();

    @Override
    public void onReceive(final Context context, final Intent intent) {

        // handle incoming fugu broadcast
        String payload = intent.getStringExtra(Constants.FUGU_CUSTOM_ACTION_PAYLOAD);
        FuguCustomActionModel customActionModel = gson.fromJson(payload, FuguCustomActionModel.class);

        if (customActionModel != null) {

            int linkIndex = 0;
            if(customActionModel.getReference()!=null) {
                linkIndex = Integer.parseInt(customActionModel.getReference());
            }
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
                        }

                    }else if(customActionModel.getDeepIndex()!=null && customActionModel.getDeepIndex()!=-1){
                        handleDeepLink(customActionModel.getDeepIndex(),context,intent.getExtras(),customActionModel);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
            else if(customActionModel.getActionType().equalsIgnoreCase(ACTION_OPEN_NEW_CHAT)){
                // start new conversation
                Intent newChatIntent = new Intent(context,NewConversationActivity.class);
                newChatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newChatIntent);
            }
        }
    }

    public void handleDeepLink(int deeplink, Context context,Bundle extras,FuguCustomActionModel customActionModel) {



        if(AppLinkIndex.INVITE_AND_EARN.getOrdinal()==deeplink){
            Intent intent = new Intent(context, ShareActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.KEY_SHARE_ACTIVITY_FROM_DEEP_LINK, false);
            context.startActivity(intent);
        }
        else if (AppLinkIndex.WALLET_TRANSACTIONS.getOrdinal() == deeplink) {
            Intent intent = new Intent();
            intent.setClass(context, PaymentActivity.class);
            intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
            intent.putExtra(Constants.KEY_WALLET_TRANSACTIONS, 1);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (AppLinkIndex.JUGNOO_STAR.getOrdinal() == deeplink) {
            context.startActivity(new Intent(context, JugnooStarSubscribedActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (AppLinkIndex.SUBSCRIPTION_PLAN_OPTION_SCREEN.getOrdinal() == deeplink) {
            if ((Data.userData.getSubscriptionData().getSubscribedUser() != null && Data.userData.getSubscriptionData().getSubscribedUser() == 1)
                    || Data.userData.isSubscriptionActive()) {
                context.startActivity(new Intent(context, JugnooStarSubscribedActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } else {
                context.startActivity(new Intent(context, JugnooStarActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        } else if (AppLinkIndex.ACCOUNT.getOrdinal() == deeplink) {
            context.startActivity(new Intent(context, AccountActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        } else if (AppLinkIndex.ABOUT.getOrdinal() == deeplink) {
            context.startActivity(new Intent(context, AboutActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        } else if (AppLinkIndex.PROMOTIONS.getOrdinal() == deeplink) {
            context.startActivity(new Intent(context, PromotionActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        } else if (AppLinkIndex.JUGNOO_CASH.getOrdinal() == deeplink) {
            Intent intent = new Intent(context, PaymentActivity.class);
            intent.putExtra(Constants.KEY_PAYMENT_ACTIVITY_PATH, PaymentActivityPath.WALLET.getOrdinal());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } /*
        else if (AppLinkIndex.NOTIFICATION_CENTER.getOrdinal() == deeplink) {
            context.startActivity(new Intent(context, NotificationCenterActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }else if (AppLinkIndex.RIDE_HISTORY.getOrdinal() == deeplink) {
            Intent intent = new Intent(context, RideTransactionsActivity.class);
            if(customActionModel.getOrderId()!=0) {
                intent.putExtra(Constants.KEY_ORDER_ID, customActionModel.getOrderId());
                intent.putExtra(Constants.KEY_PRODUCT_TYPE, ProductType.FEED);
            }
            context.startActivity(intent);

        } else */
        else  if(deeplink!=-1){
            try {
                Class lastActivityOpenForeground = Data.getLastActivityOnForeground(context);
                if(!lastActivityOpenForeground.getName().equals(MyApplication.class.getName())){
                    Intent intent = new Intent(context,lastActivityOpenForeground);
                    intent.putExtras(extras);
                    intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP|FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }




        }
    }


}
