package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.utils.Utils;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Prefs;


public class MerchantInfoFragment extends Fragment implements GAAction {

	@Bind(R.id.tvMerchantName)
	TextView tvMerchantName;
	@Bind(R.id.tvReviewCount)
	TextView tvReviewCount;
	@Bind(R.id.llRatingStars)
	LinearLayout llRatingStars;
	@Bind(R.id.tvMerchantDisplayAddress)
	TextView tvMerchantDisplayAddress;
	@Bind(R.id.tvOpensAt)
	TextView tvOpensAt;
	@Bind(R.id.llChatNow)
	LinearLayout llChatNow;
	@Bind(R.id.llCall)
	LinearLayout llCall;
	@Bind(R.id.llAddReview)
	LinearLayout llAddReview;
	@Bind(R.id.bOrderOnline)
	Button bOrderOnline;
	@Bind(R.id.tvMerchantMail)
	TextView tvMerchantMail;
	@Bind(R.id.tvMerchantContact)
	TextView tvMerchantContact;
	@Bind(R.id.tvMerchantAddress)
	TextView tvMerchantAddress;
	@Bind(R.id.rvTopReviews)
	RecyclerView rvTopReviews;
	@Bind(R.id.llSeeAll)
	LinearLayout llSeeAll;

	private View rootView;
	private FreshActivity activity;

	public MerchantInfoFragment() {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_merchant_info, container, false);
		ButterKnife.bind(this, rootView);
		activity = (FreshActivity) getActivity();

		activity.fragmentUISetup(this);
		activity.appBarLayout.setExpanded(true);


		activity.tvCollapRestaurantDeliveryTime.setText("");
		activity.clearRestaurantRatingStars(activity.llCollapRatingStars, activity.tvCollapRestaurantRating, null);

		success();

		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		try {
			if (!hidden) {
				activity.fragmentUISetup(this);
			}
		} catch (Exception e) {
		}
	}


	void success() {
		try {
			if (activity.getMenuProductsResponse() != null) {
				if (activity.updateCart) {
					activity.updateCart = false;
					activity.openCart();
				}

				setUpCollapseToolbarData();

				activity.getHandler().post(new Runnable() {
					@Override
					public void run() {
						if (Prefs.with(activity).getInt(Constants.SP_RESTAURANT_FEEDBACK_ID_TO_DEEP_LINK, -1) > 0) {
							activity.openRestaurantReviewsListFragment();
						}
					}
				});

				//merchant info set to views
				tvMerchantName.setText(activity.getVendorOpened().getName());
				llRatingStars.removeAllViews();
				activity.addStarsToLayout(llRatingStars, activity.getVendorOpened().getRating(),
						R.drawable.ic_half_star_green_grey, R.drawable.ic_star_grey);
				llRatingStars.addView(tvReviewCount);
				tvReviewCount.setText(activity.getVendorOpened().getReviewCount()+" Ratings");
				tvMerchantDisplayAddress.setText(activity.getVendorOpened().getAddress());
				activity.setTextViewDrawableColor(tvMerchantDisplayAddress, ContextCompat.getColor(activity, R.color.text_color));
				tvOpensAt.setText("");
				if(activity.getVendorOpened().getIsClosed() == 1 || activity.getVendorOpened().getIsAvailable() == 0){
					tvOpensAt.append(activity.getString(R.string.closed));
					tvOpensAt.setTextColor(ContextCompat.getColor(activity, R.color.red_dark_more));
				} else {
					tvOpensAt.append(activity.getString(R.string.open_now_colon));
					tvOpensAt.append(" ");
					tvOpensAt.append(DateOperations.convertDayTimeAPViaFormat(activity.getVendorOpened().getOpensAt())
							+" - "+DateOperations.convertDayTimeAPViaFormat(activity.getVendorOpened().getCloseIn()));
					tvOpensAt.setTextColor(ContextCompat.getColor(activity, R.color.green_fresh_fab_pressed));
				}
				tvMerchantMail.setText(activity.getVendorOpened().getMailAddress());
				tvMerchantContact.setText(activity.getVendorOpened().getContact());
				tvMerchantAddress.setText(activity.getVendorOpened().getAddress());
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void setUpCollapseToolbarData() {
		if (activity.getVendorOpened() != null) {
			activity.tvCollapRestaurantName.setText(activity.getVendorOpened().getName().toUpperCase());

			if (!TextUtils.isEmpty(activity.getVendorOpened().getImage())) {
				Picasso.with(activity).load(activity.getVendorOpened().getImage())
						.placeholder(R.drawable.ic_fresh_item_placeholder)
						.into(activity.ivCollapseRestImage);
			} else {
				activity.ivCollapseRestImage.setImageDrawable(null);
			}

			int visibility = activity.setVendorDeliveryTimeAndDrawableColorToTextView(activity.getVendorOpened(), activity.tvCollapRestaurantDeliveryTime, R.color.white);
			activity.tvCollapRestaurantDeliveryTime.setVisibility(visibility == View.VISIBLE ? View.VISIBLE : View.GONE);

			if (activity.getVendorOpened().getRating() != null && activity.getVendorOpened().getRating() >= 1d) {
				activity.llCollapRatingStars.setVisibility(View.VISIBLE);
				activity.setRestaurantRatingStarsToLL(activity.llCollapRatingStars, activity.tvCollapRestaurantRating,
						activity.getVendorOpened().getRating(),
						R.drawable.ic_half_star_green_white, R.drawable.ic_star_white, null, 0);
			} else {
				activity.llCollapRatingStars.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@OnClick({R.id.llChatNow, R.id.llCall, R.id.llAddReview, R.id.bOrderOnline, R.id.llSeeAll})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.llChatNow:
				break;
			case R.id.llCall:
				Utils.openCallIntent(activity, activity.getVendorOpened().getPhoneNo());
				break;
			case R.id.llAddReview:
				activity.openRestaurantAddReviewFragment(true);
				break;
			case R.id.bOrderOnline:
				activity.getTransactionUtils().openVendorMenuFragment(activity, activity.getRelativeLayoutContainer());
				break;
			case R.id.llSeeAll:
				activity.openRestaurantReviewsListFragment();
				break;
		}
	}
}
