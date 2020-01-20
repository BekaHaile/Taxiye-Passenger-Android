package com.sabkuchfresh.widgets;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by shankar on 29/05/17.
 */

public class LockableBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

	private boolean mLocked = false;

	public LockableBottomSheetBehavior() {
	}

	public LockableBottomSheetBehavior(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setLocked(boolean locked) {
		mLocked = locked;
	}

	@Override
	public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
		boolean handled = false;

		if (!mLocked) {
			handled = super.onInterceptTouchEvent(parent, child, event);
		}

		return handled;
	}

	@Override
	public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
		boolean handled = false;

		if (!mLocked) {
			handled = super.onTouchEvent(parent, child, event);
		}

		return handled;
	}

	@Override
	public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
		boolean handled = false;

		if (!mLocked) {
			handled = super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
		}

		return handled;
	}

	@Override
	public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed, int type) {
		if (!mLocked) {
			super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
		}
	}

	@Override
	public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target, int type) {
		if (!mLocked) {
			super.onStopNestedScroll(coordinatorLayout, child, target, type);
		}
	}

	@Override
	public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY) {
		boolean handled = false;

		if (!mLocked) {
			handled = super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
		}

		return handled;

	}
}