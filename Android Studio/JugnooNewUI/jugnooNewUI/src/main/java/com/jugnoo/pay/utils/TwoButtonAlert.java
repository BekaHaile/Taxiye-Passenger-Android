package com.jugnoo.pay.utils;


import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;


/**
 * Created by cl_macmini_86 on 4/8/16.
 */
public class TwoButtonAlert {

    private static Dialog dialog;
    private static OnAlertOkClickListener onAlertOkClickListener;
    private static OnAlertOkCancelClickListener mOnAlertOkClickListener;

    static RelativeLayout relativeLayoutParent;
    static TextView tvHead;
    static TextView LeftButton;
    static TextView rightButton;

//    public static void showAlert(Context context, String head, String left, String right) {
//
//        try {
//            final Dialog  dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
//            dialog.setContentView(R.layout.two_button_custom_alert);
//
//            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//            lp.dimAmount = 0.5f;// Dim level. 0.0 - no dim, 1.0 - completely opaque
//            dialog.getWindow().setAttributes(lp);
//
//            tvHead = (TextView) dialog.findViewById(R.id.tvInfo);
//            LeftButton = (TextView) dialog.findViewById(R.id.tvYes);
//            rightButton = (TextView) dialog.findViewById(R.id.tvNo);
//
//
//            dialog.getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//            dialog.setCancelable(false);
//            dialog.setCanceledOnTouchOutside(false);
//
//            tvHead.setText(head);
//            LeftButton.setText(left);
//            rightButton.setText(right);
//
//            rightButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    dialog.dismiss();
//                }
//            });
//
//            LeftButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                }
//            });
//            dialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }


//    public static void showAlert(Context sourceContext, String head, String left, String right, OnAlertOkClickListener onAlertOkClickListener1) {
//        try {
//            onAlertOkClickListener = onAlertOkClickListener1;
//
//
//            final Dialog dialog = new Dialog(sourceContext, android.R.style.Theme_Translucent_NoTitleBar);
//            dialog.setContentView(R.layout.two_button_custom_alert);
//            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//            lp.dimAmount = 0.5f;// Dim level. 0.0 - no dim, 1.0 - completely opaque
//            dialog.getWindow().setAttributes(lp);
//
//            tvHead = (TextView) dialog.findViewById(R.id.tvInfo);
//            LeftButton = (TextView) dialog.findViewById(R.id.tvYes);
//            rightButton = (TextView) dialog.findViewById(R.id.tvNo);
//
//
//            dialog.getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//            dialog.setCancelable(false);
//            dialog.setCanceledOnTouchOutside(false);
//
//            tvHead.setText(head);
//            LeftButton.setText(left);
//            rightButton.setText(right);
//
//            rightButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    onAlertOkClickListener.onOkButtonClicked();
//                    dialog.dismiss();
//                }
//            });
//
//            LeftButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                    dialog.dismiss();
//                }
//            });
//            dialog.show();
//
//
//            dialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//keep it
    public static void showAlert(Context sourceContext, String head, String left, String right, OnAlertOkCancelClickListener onAlertOkClickListener1) {


        try {
                 mOnAlertOkClickListener = onAlertOkClickListener1;


                dialog = new Dialog(sourceContext, android.R.style.Theme_Translucent_NoTitleBar);
                dialog.setContentView(R.layout.two_button_custom_alert);
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.dimAmount = 0.5f;// Dim level. 0.0 - no dim, 1.0 - completely opaque
                dialog.getWindow().setAttributes(lp);
                if (dialog.isShowing())
                    dialog.dismiss();
                relativeLayoutParent = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutParent);
                tvHead = (TextView) dialog.findViewById(R.id.tvInfo);
                LeftButton = (TextView) dialog.findViewById(R.id.tvYes);
                rightButton = (TextView) dialog.findViewById(R.id.tvNo);


                dialog.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);


                tvHead.setText(head);
                LeftButton.setText(left);
                rightButton.setText(right);

            relativeLayoutParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LeftButton.performClick();
                }
            });

                rightButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mOnAlertOkClickListener.onOkButtonClicked();
                        dialog.dismiss();
                    }
                });

                LeftButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mOnAlertOkClickListener.onCancelButtonClicked();

                        dialog.dismiss();
                    }
                });
                dialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnAlertOkClickListener {
        public void onOkButtonClicked();

    }

    public interface OnAlertOkCancelClickListener {
        public void onOkButtonClicked();
        public void onCancelButtonClicked();

    }

    public static void dismissLoadingDialog() {
        try{
            if(dialog != null){
                dialog.dismiss();
                dialog = null;
            }} catch(Exception e){
            Log.e("e", "=" + e);
        }
    }
}

