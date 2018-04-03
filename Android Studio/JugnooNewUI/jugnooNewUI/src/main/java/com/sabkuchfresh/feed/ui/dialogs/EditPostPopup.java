package com.sabkuchfresh.feed.ui.dialogs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;

import com.sabkuchfresh.feed.utils.FeedUtils;
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
    private View arrowUp,arrowDown;


    public <T extends EditPostDialogCallback> EditPostPopup(@NonNull T deleteDialogCall, @StyleRes int themeResId, Activity context) {
        super(context, themeResId);
        this.editPostDialogCallback = deleteDialogCall;
        setContentView(R.layout.dialog_feed_edit_popup);
        arrowDown=findViewById(R.id.arrow_down);
        arrowUp=findViewById(R.id.arrow_up);
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
                EditPostPopup.super.dismiss();
                editPostDialogCallback.onMoreDelete(feedDetail,positionInList);
            }
        });

        Window window = getWindow();
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.gravity=Gravity.START|Gravity.TOP;
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
            getWindow().getDecorView().setPivotX(openingViewLocation[0]+ viewClicked.getMeasuredWidth()/2);
            getWindow().getDecorView().setPivotY(arrowDown.getVisibility()==View.VISIBLE?getContext().getResources().getDimensionPixelSize(R.dimen.height_popup_edit_feed):0);
            getWindow().getDecorView().animate()
                    .scaleX(0.3f)
                    .scaleY(0.3f)
                    .setDuration(200)
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
            int xViewClicked = openingViewLocation[0];
            int yViewClicked = openingViewLocation[1];
            wlp.x = xViewClicked;
            if(openingViewLocation[1] > FeedUtils.getScreenHeight(getContext())/2){
                wlp.y= yViewClicked- getContext().getResources().getDimensionPixelSize(R.dimen.height_popup_edit_feed) + FeedUtils.dpToPx(10);
                arrowDown.setVisibility(View.VISIBLE);
                arrowUp.setVisibility(View.GONE);
                getWindow().getDecorView().setPivotY(getContext().getResources().getDimensionPixelSize(R.dimen.height_popup_edit_feed));
            }else{
                wlp.y = openingViewLocation[1]+ viewClicked.getHeight() - FeedUtils.dpToPx(10);
                arrowUp.setVisibility(View.VISIBLE);
                arrowDown.setVisibility(View.GONE);
                getWindow().getDecorView().setPivotY(0);
            }

            getWindow().getDecorView().setPivotX(openingViewLocation[0]+viewClicked.getMeasuredWidth()/2);
            getWindow().getDecorView().setScaleX(0.3f);
            getWindow().getDecorView().setScaleY(0.3f);
            getWindow().setAttributes(wlp);
            getWindow().getDecorView().animate().scaleX(1f).scaleY(1f).setDuration(250);
//                   .setInterpolator(new AccelerateInterpolator());

        }
    }


}
