package com.jugnoo.pay.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;


/**
 * Created by cl_macmini_86 on 4/8/16.
 */
public class SingleButtonAlert {


//  public static   Dialog dialog,dialogNotification;

    private static OnAlertOkClickListener onAlertOkClickListener;

    static TextView tvSingleHead;
    static TextView tvSingleOk;

    public static void showAlert(Context context, String head, String msg) {
        Dialog  dialogNotification = null;

        try {
              dialogNotification= new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);

            dialogNotification.setContentView(R.layout.single_buton_dialog);


            WindowManager.LayoutParams lp = dialogNotification.getWindow().getAttributes();
            lp.dimAmount = 0.5f;// Dim level. 0.0 - no dim, 1.0 - completely opaque
            dialogNotification.getWindow().setAttributes(lp);
            dialogNotification.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            //lp.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            //   dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHI‌​ND);


            tvSingleHead = (TextView) dialogNotification.findViewById(R.id.tv_single_head);
            tvSingleOk = (TextView)dialogNotification.findViewById(R.id.tv_single_ok);


            dialogNotification.setCancelable(false);
            dialogNotification.setCanceledOnTouchOutside(false);

            tvSingleHead.setText(head);
            if (msg != null)
                tvSingleOk.setText(msg);
            else
                tvSingleOk.setText("Ok");


            final Dialog finalDialogNotification = dialogNotification;
            tvSingleOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finalDialogNotification.dismiss();
                }
            });


            dialogNotification.show();
        } catch (Exception e) {
            dialogNotification.dismiss();
            e.printStackTrace();
        }
    }

    public static void showAlertGps(Context context, String head, String msg,OnAlertOkClickListener onAlertOkClickListener1) {
            Dialog dialog =  null;

        try {
            onAlertOkClickListener = onAlertOkClickListener1;
            dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);

            dialog.setContentView(R.layout.single_buton_dialog);


            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.5f;// Dim level. 0.0 - no dim, 1.0 - completely opaque
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            //lp.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            //   dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHI‌​ND);


            tvSingleHead = (TextView) dialog.findViewById(R.id.tv_single_head);
            tvSingleOk = (TextView) dialog.findViewById(R.id.tv_single_ok);


            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            tvSingleHead.setText(head);


            final Dialog finalDialog = dialog;
            tvSingleOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finalDialog.dismiss();
                    onAlertOkClickListener.onOkButtonClicked();

                }
            });


            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void showNotification(Context context, String serviceName, String url,String time,
//                                        String address,String creatorName,OnAlertOkClickListener onAlertOkClickListener1) {
//
//
//        try {
//            ImageView ivServiceIcon;
//            TextView tvServiceName,tvServiceTime,tvServiceCreatorName,tvServiceAddress;
//            TextView btnNotificationAck;
//
//
//            onAlertOkClickListener = onAlertOkClickListener1;
//            dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
//
//            dialog.setContentView(R.layout.layout_notification);
//
//
//            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//            lp.dimAmount = 0.5f;// Dim level. 0.0 - no dim, 1.0 - completely opaque
//            dialog.getWindow().setAttributes(lp);
//            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//
//            dialog.setCancelable(false);
//            dialog.setCanceledOnTouchOutside(false);
//            //lp.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//            //   dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHI‌​ND);
//
//
//            btnNotificationAck=(TextView)dialog.findViewById(R.id.btn_notification_ack);
//            tvServiceName = (TextView) dialog.findViewById(R.id.tv_notification_service_name);
//            tvServiceTime = (TextView) dialog.findViewById(R.id.tv_notification_service_time);
//            tvServiceCreatorName = (TextView) dialog.findViewById(R.id.tv_notification_creator_name);
//            tvServiceAddress = (TextView) dialog.findViewById(R.id.tv_notification_address);
//            ivServiceIcon=(ImageView)dialog.findViewById(R.id.iv_notification_service_icon);
//
//
//            //  tvServiceName.setText(serviceName);
//            tvServiceName.setText(capEachWord(serviceName));
//            tvServiceAddress.setText(capEachWord(address));
//            tvServiceTime.setText(time);
//            tvServiceCreatorName.setText(capEachWord(creatorName));
//
//
//            btnNotificationAck.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    onAlertOkClickListener.onOkButtonClicked();
//                    dialog.dismiss();
//                }
//            });
//
//
//            dialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void showAlertNoInternet(Context context, String head, String msg,OnAlertOkClickListener onAlertOkClickListener1) {

        Dialog dialog =  null;
        onAlertOkClickListener = onAlertOkClickListener1;
        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);

//        dialog.setContentView(R.layout.layout_no_internet_connection);
        dialog.setContentView(R.layout.single_buton_dialog);


        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.5f;// Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //lp.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //   dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHI‌​ND);


        tvSingleHead = (TextView) dialog.findViewById(R.id.tv_single_head);
        tvSingleOk = (TextView) dialog.findViewById(R.id.tv_single_ok);



        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        tvSingleHead.setText(head);
        if (msg != null)
            tvSingleOk.setText(msg);
        else
            tvSingleOk.setText("Ok");


        final Dialog finalDialog = dialog;
        tvSingleOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
                onAlertOkClickListener.onOkButtonClicked();

            }
        });


        dialog.show();
    }

    public interface OnAlertOkClickListener {
        public void onOkButtonClicked();

    }

    public static String capEachWord(String source) {
        String result = "";
        Log.d("Tag", "dsf");
        String[] splitString = source.split(" ");
        for (String target : splitString) {
            result += Character.toUpperCase(target.charAt(0))
                    + target.substring(1).toLowerCase() + " ";
        }
        return result.trim();
    }
}
