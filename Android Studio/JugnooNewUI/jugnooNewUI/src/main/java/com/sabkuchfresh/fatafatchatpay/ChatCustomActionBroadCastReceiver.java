package com.sabkuchfresh.fatafatchatpay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.sabkuchfresh.datastructure.FuguCustomActionModel;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;

/**
 * Created by cl-macmini-01 on 12/21/17.
 */
public class ChatCustomActionBroadCastReceiver extends BroadcastReceiver {

    final String ACTION_NATIVE_ACTIVITY = "NATIVE_ACTIVITY";

    @Override
    public void onReceive(final Context context, final Intent intent) {

        // handle incoming fugu broadcast
        String payload = intent.getStringExtra(Constants.FUGU_CUSTOM_ACTION_PAYLOAD);
        FuguCustomActionModel customActionModel = new Gson().fromJson(payload, FuguCustomActionModel.class);

        if (customActionModel != null) {
            if (customActionModel.getActionType().equalsIgnoreCase(ACTION_NATIVE_ACTIVITY)) {

                // get link index
                try {
                    int linkIndex = Integer.parseInt(customActionModel.getReference());
                    if (linkIndex == AppLinkIndex.FATAFAT_PAY_VIA_CHAT.getOrdinal()) {
                        // open fatafat chat pay passing in orderId and amount
                        if (customActionModel.getOrderId() != 0 && customActionModel.getAmount() != 0) {
                            context.startActivity(new Intent(context, FatafatChatPayActivity.class)
                                    .putExtra(Constants.KEY_ORDER_ID, customActionModel.getOrderId())
                                    .putExtra(Constants.KEY_AMOUNT, customActionModel.getAmount()));
                        }

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
