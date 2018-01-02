package com.sabkuchfresh.fatafatchatpay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.sabkuchfresh.datastructure.FuguCustomActionModel;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.home.DeepLinkAction;
import product.clicklabs.jugnoo.home.adapters.MenuAdapter;

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
                        }

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
