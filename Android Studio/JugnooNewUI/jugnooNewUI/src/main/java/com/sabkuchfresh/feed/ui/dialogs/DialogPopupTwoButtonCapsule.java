package com.sabkuchfresh.feed.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.StyleRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;

/**
 * Created by Parminder Singh on 3/24/17.
 */

public class DialogPopupTwoButtonCapsule extends Dialog {

    private DialogCallback callback;
    private String message, leftText, rightText;

    public DialogPopupTwoButtonCapsule(@StyleRes int themeResId, Activity context) {
        this(null, themeResId, context, "");
    }

    public <T extends DialogCallback> DialogPopupTwoButtonCapsule(T callback, @StyleRes int themeResId, Activity context, String message) {
        super(context, themeResId);
        this.callback = callback;
        setContentView(R.layout.dialog_two_buttons_capsule);
        findViewById(R.id.bPositive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPopupTwoButtonCapsule.super.dismiss();
                if(DialogPopupTwoButtonCapsule.this.callback != null) {
                    DialogPopupTwoButtonCapsule.this.callback.onLeftClick();
                }
            }
        });
        findViewById(R.id.bNegative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPopupTwoButtonCapsule.super.dismiss();
                if(DialogPopupTwoButtonCapsule.this.callback != null) {
                    DialogPopupTwoButtonCapsule.this.callback.onRightClick();
                }
            }
        });

        this.message = message;

        Window window = getWindow();
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.windowAnimations = R.style.Animations_LoadingDialogFade;
        wlp.gravity=Gravity.START|Gravity.CENTER;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//to avoid black backgorund during animation
        window.setAttributes(wlp);
/*
        getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
        getWindow().getAttributes().dimAmount = 0.6f;
        getWindow().getAttributes().gravity = Gravity.START|Gravity.CENTER;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));*/

    }

    @Override
    public void show() {
        ((TextView)findViewById(R.id.tvMessage)).setText(message);

        if(!TextUtils.isEmpty(leftText)) {
            ((Button)findViewById(R.id.bPositive)).setText(leftText);
        }

        if(!TextUtils.isEmpty(rightText)) {
            ((Button)findViewById(R.id.bNegative)).setText(rightText);
        }

        super.show();
    }

    public void show(DialogCallback callback, String message, String leftText, String rightText){
        this.callback = callback;
        this.message = message;
        this.leftText = leftText;
        this.rightText = rightText;
        show();
    }

    public interface DialogCallback {
        void onLeftClick();
        void onRightClick();
    }

}
