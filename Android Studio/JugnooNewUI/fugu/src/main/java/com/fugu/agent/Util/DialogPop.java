package com.fugu.agent.Util;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.fugu.R;

/**
 * Created by gurmail on 19/06/18.
 *
 * @author gurmail
 */

public class DialogPop {

    public void alertPopupWithTwoButton(Context context, String title, String description, String positiveClickName,
                                        String negativeClickName, final Callback callback){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.FuguAppCompatAlertDialogStyle);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        callback.onPositiveClick();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        callback.onNegativeClick();
                        break;
                    default:
                        break;
                }
            }
        };

        if(!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(description);
        builder.setPositiveButton(positiveClickName, listener);
        builder.setNegativeButton(negativeClickName, listener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.ttf");
        TextView textView = alertDialog.getWindow().findViewById(android.R.id.message);
        Button button1 = alertDialog.getWindow().findViewById(android.R.id.button1);
        Button button2 = alertDialog.getWindow().findViewById(android.R.id.button2);
//        textView.setTypeface(typeface);
//        button1.setTypeface(typeface);
//        button2.setTypeface(typeface);
    }

    public interface Callback{
        void onPositiveClick();
        void onNegativeClick();
    }
}
