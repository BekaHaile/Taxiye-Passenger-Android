package com.sabkuchfresh.feed.ui.dialogs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by Parminder Singh on 3/24/17.
 */

public class EditPostPopup extends Dialog {

    private EditPostDialogCallback editPostDialogCallback;
    private FeedDetail feedDetail;
    private View viewClicked;
    private int positionInList;


    public <T extends EditPostDialogCallback> EditPostPopup(@NonNull T deleteDialogCall, @StyleRes int themeResId, Activity context) {
        super(context, themeResId);
        this.editPostDialogCallback = deleteDialogCall;
        setContentView(R.layout.dialog_feed_edit_popup);
        findViewById(R.id.tv_edit_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditPostPopup.super.dismiss();
                editPostDialogCallback.onMoreEdit(feedDetail,positionInList);
            }
        });
        findViewById(R.id.tv_delete_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                editPostDialogCallback.onMoreDelete(feedDetail,positionInList);
            }
        });

        Window window = getWindow();
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.gravity=Gravity.LEFT|Gravity.TOP;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//to avoid black backgorund during animation
        window.setAttributes(wlp);

    }


    public interface EditPostDialogCallback {
        void onMoreDelete(FeedDetail feedDetail, int positionInList);

        void onMoreEdit(FeedDetail feedDetail, int positionInList);
    }


    public void show(FeedDetail feedDetail, View viewClicked,int positionInList) {
        this.feedDetail = feedDetail;
        this.positionInList = positionInList;
        this.viewClicked=viewClicked;
        animateEnterDialog();
        super.show();
    }





    @Override
    public void show() {

    }

    @Override
    public void dismiss() {
        if (viewClicked != null) {
            int[] openingViewLocation = new int[2];
            viewClicked.getLocationOnScreen(openingViewLocation);
            getWindow().getDecorView().setPivotX(openingViewLocation[0]- viewClicked.getMeasuredWidth()/2);
            getWindow().getDecorView().setPivotY(viewClicked.getY());
            getWindow().getDecorView().animate()
                    .scaleX(0.1f)
                    .scaleY(0.1f)
                    .setDuration(150)
                    .setInterpolator(new AccelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (getWindow().getDecorView() != null) {
                                EditPostPopup.super.dismiss();


                            }
                            viewClicked = null;
                            feedDetail = null;


                        }
                    });

        } else {
            super.dismiss();
        }
    }

    private void animateEnterDialog() {
        if (viewClicked != null) {
            int[] openingViewLocation = new int[2];
            viewClicked.getLocationOnScreen(openingViewLocation);
            WindowManager.LayoutParams wlp = getWindow().getAttributes();
            wlp.x = openingViewLocation[0];
            wlp.y = openingViewLocation[1]+ viewClicked.getHeight()- com.sabkuchfresh.feed.utils.Utils.dpToPx(10);
            getWindow().getDecorView().setPivotX(openingViewLocation[0]-viewClicked.getMeasuredWidth()/2);
            getWindow().getDecorView().setPivotY(viewClicked.getY());
            getWindow().getDecorView().setScaleX(0.1f);
            getWindow().getDecorView().setScaleY(0.1f);
            getWindow().setAttributes(wlp);
            getWindow().getDecorView().
                    animate().
                    scaleX(1f).
                    scaleY(1f).
                    setDuration(150).
                    setInterpolator(new OvershootInterpolator());
        }
    }


}
