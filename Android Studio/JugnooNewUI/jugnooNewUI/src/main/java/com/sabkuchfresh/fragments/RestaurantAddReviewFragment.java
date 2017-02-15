package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.commoncalls.SendFeedbackQuery;
import com.sabkuchfresh.home.FreshActivity;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Events;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ProductType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by Shankar on 15/11/16.
 */
public class RestaurantAddReviewFragment extends Fragment {
	private final String TAG = RestaurantAddReviewFragment.class.getSimpleName();

	private RelativeLayout rlRoot;
	private EditText etFeedback;
	private Button bSubmit;

	private View rootView;
	private FreshActivity activity;
	private int restaurantId;

	public static RestaurantAddReviewFragment newInstance(int restaurantId) {
		RestaurantAddReviewFragment fragment = new RestaurantAddReviewFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_RESTAURANT_ID, restaurantId);
		fragment.setArguments(bundle);
		return fragment;
	}


	private void fetchArguments() {
		Bundle bundle = getArguments();
		restaurantId = bundle.getInt(Constants.KEY_RESTAURANT_ID, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_restaurant_add_review, container, false);

		fetchArguments();

		activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		rlRoot = (RelativeLayout) rootView.findViewById(R.id.rlRoot);
		try {
			if (rlRoot != null) {
				new ASSL(activity, rlRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		etFeedback = (EditText) rootView.findViewById(R.id.etFeedback);
		bSubmit = (Button) rootView.findViewById(R.id.bSubmit);

		bSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String reviewDesc = etFeedback.getText().toString().trim();
				if(reviewDesc.length() == 0){
					etFeedback.requestFocus();
					etFeedback.setError(activity.getString(R.string.please_enter_some_feedback));
				} else if(reviewDesc.length() > 500){
					etFeedback.requestFocus();
					etFeedback.setError(activity.getString(R.string.feedback_must_be_in_500));
				} else {
					submitFeedback(reviewDesc);
				}
			}
		});

		rlRoot.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(rlRoot, null, new KeyboardLayoutListener.KeyBoardStateHandler() {
			@Override
			public void keyboardOpened() {
			}

			@Override
			public void keyBoardClosed() {
			}
		}));

		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		etFeedback.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				bSubmit.setEnabled(s.length() > 0);
			}
		});
		bSubmit.setEnabled(false);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				etFeedback.requestFocus();
				Utils.showSoftKeyboard(activity, etFeedback);
			}
		}, 200);

		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			activity.fragmentUISetup(this);
		}
	}


	SendFeedbackQuery sendFeedbackQuery;
	private void submitFeedback(String reviewDesc){
		DialogPopup.showLoadingDialog(activity, "");
		if(sendFeedbackQuery == null) {
			sendFeedbackQuery = new SendFeedbackQuery();
		}
		sendFeedbackQuery.sendQuery(-1, restaurantId, ProductType.MENUS, -1,null, "", reviewDesc, activity,
				new SendFeedbackQuery.FeedbackResultListener() {
					@Override
					public void onSendFeedbackResult(boolean isSuccess, int rating) {
						if (isSuccess) {
							FlurryEventLogger.eventGA(Events.MENU,Events.REVIEW,Events.SUBMITTED);
							activity.performBackPressed();
							Utils.showToast(activity, activity.getString(R.string.thanks_for_your_valuable_feedback));
							RestaurantReviewsListFragment frag = activity.getRestaurantReviewsListFragment();
							if(frag != null) {
								frag.fetchFeedback();
							}
						}
					}
				});
	}

	@Override
	public void onDestroyView() {
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onDestroyView();
		ASSL.closeActivity(rlRoot);
		System.gc();
	}

}
