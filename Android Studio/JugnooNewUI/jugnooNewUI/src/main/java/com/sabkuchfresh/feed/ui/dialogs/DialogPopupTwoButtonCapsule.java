package com.sabkuchfresh.feed.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;

/**
 * Created by Parminder Singh on 3/24/17.
 */

public class DialogPopupTwoButtonCapsule extends Dialog {

    private DialogCallback callback;


    public <T extends DialogCallback> DialogPopupTwoButtonCapsule(@NonNull T callback, @StyleRes int themeResId, Activity context, String message) {
        super(context, themeResId);
        this.callback = callback;
        setContentView(R.layout.dialog_two_buttons_capsule);
        findViewById(R.id.bPositive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPopupTwoButtonCapsule.super.dismiss();
                DialogPopupTwoButtonCapsule.this.callback.onPositiveClick();
            }
        });
        findViewById(R.id.bNegative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPopupTwoButtonCapsule.super.dismiss();
                DialogPopupTwoButtonCapsule.this.callback.onNegativeClick();
            }
        });
        ((TextView)findViewById(R.id.tvMessage)).setText(message);

        getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
        getWindow().getAttributes().dimAmount = 0.6f;
        getWindow().getAttributes().gravity = Gravity.CENTER;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        setCancelable(true);
//        setCanceledOnTouchOutside(true);

    }

    public interface DialogCallback {
        void onPositiveClick();
        void onNegativeClick();
    }

}
