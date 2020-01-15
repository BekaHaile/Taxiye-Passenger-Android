package com.sabkuchfresh.widgets;

import android.content.Context;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by vitaliiobideiko on 10/5/16.
 */

public class UserLockBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {




    private boolean isDragEnabled = false;
    public UserLockBottomSheetBehavior() {
        super();
    }

    public void setDragEnabled(boolean isDragEnabled){
        this.isDragEnabled = isDragEnabled;
    }

    public UserLockBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        return isDragEnabled;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        return isDragEnabled;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child, View directTargetChild, View target, int nestedScrollAxes) {
        return isDragEnabled;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed) {}

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {}

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY) {
        return isDragEnabled;
    }


}