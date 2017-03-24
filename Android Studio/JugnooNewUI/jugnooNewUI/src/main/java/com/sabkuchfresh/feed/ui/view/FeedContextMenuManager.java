package com.sabkuchfresh.feed.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.sabkuchfresh.feed.utils.Utils;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;


/**
 * Created by froger_mcs on 16.12.14.
 */
public class FeedContextMenuManager extends RecyclerView.OnScrollListener implements View.OnAttachStateChangeListener {

    private static FeedContextMenuManager instance;

    private FeedContextMenu contextMenuView;


    private boolean isContextMenuDismissing;
    private boolean isContextMenuShowing;
    private View openingView;
    private View viewDisabledEditPostPopUp;
    private FreshActivity freshActivity;
    private FeedDetail feedDetail;

    public static FeedContextMenuManager getInstance() {
        if (instance == null) {
            instance = new FeedContextMenuManager();
        }
        return instance;
    }

    private FeedContextMenuManager() {

    }

public void toggleContextMenuFromView(View openingView, FeedDetail feedItem, FeedContextMenu.OnFeedContextMenuItemClickListener listener, View viewDisabledEditPostPopUp, FreshActivity activity,int position) {
    this.openingView=openingView;
    this.viewDisabledEditPostPopUp = viewDisabledEditPostPopUp;
    this.freshActivity=activity;
    if (contextMenuView == null) {
        showContextMenuFromView(openingView, feedItem, listener,position);
        toggleActivityState(true);

    } else {

        hideContextMenu();
        toggleActivityState(false);
    }
}

    private void showContextMenuFromView(final View openingView, FeedDetail feedItem, FeedContextMenu.OnFeedContextMenuItemClickListener listener,int position) {
        if (!isContextMenuShowing) {
            isContextMenuShowing = true;
            contextMenuView = new FeedContextMenu(openingView.getContext());
            contextMenuView.bindToItem(feedItem,position);
            contextMenuView.addOnAttachStateChangeListener(this);
            contextMenuView.setOnFeedMenuItemClickListener(listener);

            ((ViewGroup) openingView.getRootView().findViewById(android.R.id.content)).addView(contextMenuView);

            contextMenuView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {

                    contextMenuView.getViewTreeObserver().removeOnPreDrawListener(this);

                    setupContextMenuInitialPosition(openingView);
                    performShowAnimation();
                    return false;
                }
            });
        }
    }

    private void setupContextMenuInitialPosition(View openingView) {
        final int[] openingViewLocation = new int[2];
        openingView.getLocationOnScreen(openingViewLocation);
        int additionalBottomMargin = Utils.dpToPx(16);
//        contextMenuView.setTranslationX(openingViewLocation[0] - contextMenuView.getWidth() / 3);
//        contextMenuView.setTranslationY(openingViewLocation[1] - contextMenuView.getHeight() - additionalBottomMargin);
        contextMenuView.setTranslationY(openingViewLocation[1]-additionalBottomMargin);
    }

    private void performShowAnimation() {

        if(openingView!=null){
            contextMenuView.setPivotX(openingView.getX()+ (openingView.getWidth()/2));
            contextMenuView.setPivotY(openingView.getY());
        }
        else{
            contextMenuView.setPivotX(contextMenuView.getWidth() / 2);
            contextMenuView.setPivotY(contextMenuView.getHeight());
        }
      /*  contextMenuView.setPivotX(contextMenuView.getWidth() / 2);
        contextMenuView.setPivotY(contextMenuView.getHeight());*/
        contextMenuView.setScaleX(0.1f);
        contextMenuView.setScaleY(0.1f);
        contextMenuView.animate()
                .scaleX(1f).scaleY(1f)
                .setDuration(150)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isContextMenuShowing = false;
                    }
                });
    }

    public void hideContextMenu() {
        if (!isContextMenuDismissing && contextMenuView!=null) {
            isContextMenuDismissing = true;
            performDismissAnimation();
        }
    }

    private void performDismissAnimation() {

        if(openingView!=null){
            contextMenuView.setPivotX(openingView.getX());
            contextMenuView.setPivotY(openingView.getY());
        }
        else{
            contextMenuView.setPivotX(contextMenuView.getWidth() / 2);
            contextMenuView.setPivotY(contextMenuView.getHeight());
        }
     /*   contextMenuView.setPivotX(contextMenuView.getWidth() / 2);
        contextMenuView.setPivotY(contextMenuView.getHeight());*/

        contextMenuView.animate()
                .scaleX(0.1f).scaleY(0.1f)
                .setDuration(150)
                .setInterpolator(new AccelerateInterpolator())
                .setStartDelay(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (contextMenuView != null) {
                            contextMenuView.dismiss();
                        }

                        toggleActivityState(false);
                        isContextMenuDismissing = false;
                    }
                });
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (contextMenuView != null) {
            hideContextMenu();
            contextMenuView.setTranslationY(contextMenuView.getTranslationY() - dy);
        }


    }

    @Override
    public void onViewAttachedToWindow(View v) {

    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        contextMenuView = null;
    }


    public void toggleActivityState(boolean isPause) {
        if(isPause){
            if(viewDisabledEditPostPopUp!=null) {
                this.viewDisabledEditPostPopUp.setVisibility(View.VISIBLE);
                if(freshActivity.getDrawerLayout().isDrawerOpen(GravityCompat.START)) {
                    freshActivity.getDrawerLayout().closeDrawer(GravityCompat.START);
                }
                freshActivity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,GravityCompat.START);

            }

        }
        else{

            if(viewDisabledEditPostPopUp!=null) {
                this.viewDisabledEditPostPopUp.setVisibility(View.GONE);
               freshActivity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);

            }


        }
    }

}
