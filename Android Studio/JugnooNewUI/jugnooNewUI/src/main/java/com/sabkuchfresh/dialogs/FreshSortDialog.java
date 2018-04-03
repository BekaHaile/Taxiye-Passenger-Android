package com.sabkuchfresh.dialogs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import com.sabkuchfresh.adapters.FreshSortingAdapter;
import com.sabkuchfresh.feed.utils.FeedUtils;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.SortResponseModel;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 23/04/17.
 */

public class FreshSortDialog extends Dialog {

	private View viewClicked;
	private FreshSortingAdapter freshSortingAdapter;

	public <T extends FreshSortingAdapter.Callback> FreshSortDialog(@NonNull final T dialogCallback1, @StyleRes int themeResId, FreshActivity context, ArrayList<SortResponseModel> sortList) {
		super(context, themeResId);
		setContentView(R.layout.dialog_fresh_sort);
		RecyclerView rvSortOptions = (RecyclerView) findViewById(R.id.rvSortOptions);
		rvSortOptions.setLayoutManager(new LinearLayoutManager(context));
		rvSortOptions.setItemAnimator(new DefaultItemAnimator());
		freshSortingAdapter = new FreshSortingAdapter(context, sortList, rvSortOptions, dialogCallback1);
		rvSortOptions.setAdapter(freshSortingAdapter);

		Window window = getWindow();
		WindowManager.LayoutParams wlp = getWindow().getAttributes();
		wlp.gravity = Gravity.START | Gravity.TOP;
		wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//to avoid black backgorund during animation
		window.setAttributes(wlp);
	}

	public void show(View viewClicked) {
		this.viewClicked = viewClicked;
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
			getWindow().getDecorView().setPivotX(openingViewLocation[0] + viewClicked.getMeasuredWidth() / 2);
			getWindow().getDecorView().setPivotY(viewClicked.getY());
			getWindow().getDecorView().animate()
					.scaleX(0.0f)
					.scaleY(0.0f)
					.setDuration(150)
					.setInterpolator(new AccelerateInterpolator())
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							if (getWindow().getDecorView() != null) {
								FreshSortDialog.super.dismiss();


							}
							viewClicked = null;
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
			wlp.y = openingViewLocation[1] + viewClicked.getHeight();
			getWindow().getDecorView().setPivotX(openingViewLocation[0] + viewClicked.getMeasuredWidth() / 2);
			getWindow().getDecorView().setPivotY(viewClicked.getY());
			getWindow().getDecorView().setScaleX(0.0f);
			getWindow().getDecorView().setScaleY(0.0f);
			getWindow().setAttributes(wlp);
			getWindow().getDecorView().
					animate().
					scaleX(1f).
					scaleY(1f).
					setDuration(150);
		}
	}


}