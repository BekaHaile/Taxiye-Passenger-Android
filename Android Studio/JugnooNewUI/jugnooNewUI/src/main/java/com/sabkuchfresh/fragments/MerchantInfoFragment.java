package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.RestaurantReviewsAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.commoncalls.ApiRestaurantFetchFeedback;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.sabkuchfresh.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import retrofit.RetrofitError;


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
	@Bind(R.id.progressWheel)
	ProgressWheel progressWheel;
	@Bind(R.id.tvReviewsHeader)
	TextView tvReviewsHeader;
	@Bind(R.id.scrollView)
	NestedScrollView scrollView;

	private View rootView;
	private FreshActivity activity;
	private RestaurantReviewsAdapter reviewsAdapter;

	public MerchantInfoFragment() {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_merchant_info, container, false);
		ButterKnife.bind(this, rootView);
		activity = (FreshActivity) getActivity();

		activity.fragmentUISetup(this);
		activity.appBarLayout.setExpanded(true);

		rvTopReviews.setLayoutManager(new LinearLayoutManager(activity));
		reviewsAdapter = new RestaurantReviewsAdapter(activity, new RestaurantReviewsAdapter.Callback() {
			@Override
			public void onEdit(FetchFeedbackResponse.Review review) {
				activity.setCurrentReview(review);
				activity.openRestaurantAddReviewFragment(false);
			}

			@Override
			public void onShare(FetchFeedbackResponse.Review review) {

			}

			@Override
			public void onLike(FetchFeedbackResponse.Review review) {
				GAUtils.event(GACategory.MENUS, GAAction.FEED , GAAction.FEED + GAAction.LIKED);
			}

			@Override
			public void onScrollStateChanged(int newState) {
			}

			@Override
			public int getRestaurantId() {
				return activity.getVendorOpened().getRestaurantId();
			}

			@Override
			public MenusResponse.Vendor getVendor() {
				return activity.getVendorOpened();
			}

			@Override
			public String getShareTextSelf() {
				return "";
			}

			@Override
			public String getShareTextOther() {
				return "";
			}

			@Override
			public int getShareIsEnabled() {
				return 0;
			}

			@Override
			public int getLikeIsEnabled() {
				return 0;
			}

			@Override
			public RecyclerView getRecyclerView() {
				return rvTopReviews;
			}
		}, restaurantReviews, true);
		rvTopReviews.setAdapter(reviewsAdapter);

		activity.tvCollapRestaurantDeliveryTime.setText("");
		activity.clearRestaurantRatingStars(activity.llCollapRatingStars, activity.tvCollapRestaurantRating, null);
		progressWheel.stopSpinning();
		progressWheel.setVisibility(View.GONE);

		setMerchantInfoToUI();

		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		try {
			if (!hidden) {
				activity.fragmentUISetup(this);
				activity.tvCollapRestaurantDeliveryTime.setVisibility(View.GONE);
				if(getView() != null) {
					scrollView.scrollTo(0, 0);
				}
			}
		} catch (Exception e) {
		}
	}


	void setMerchantInfoToUI() {
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
				setRatingViews();
				tvMerchantDisplayAddress.setText(activity.getVendorOpened().getDisplayAddress());
				activity.setTextViewDrawableColor(tvMerchantDisplayAddress, ContextCompat.getColor(activity, R.color.text_color));
				tvMerchantDisplayAddress.setVisibility(!TextUtils.isEmpty(activity.getVendorOpened().getDisplayAddress()) ? View.VISIBLE : View.GONE);
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
				tvMerchantMail.setText(activity.getVendorOpened().getEmail());
				tvMerchantMail.setVisibility(!TextUtils.isEmpty(activity.getVendorOpened().getEmail()) ? View.VISIBLE : View.GONE);
				tvMerchantContact.setText(activity.getVendorOpened().getCallingNumber());
				tvMerchantContact.setVisibility(!TextUtils.isEmpty(activity.getVendorOpened().getCallingNumber()) ? View.VISIBLE : View.GONE);
				tvMerchantAddress.setText(activity.getVendorOpened().getAddress());
				tvMerchantAddress.setVisibility(!TextUtils.isEmpty(activity.getVendorOpened().getAddress()) ? View.VISIBLE : View.GONE);
				rvTopReviews.setVisibility(View.GONE);
				llSeeAll.setVisibility(View.GONE);
				fetchFeedback();
				llChatNow.setVisibility(activity.getVendorOpened().isChatModeEnabled() ? View.VISIBLE : View.GONE);
				bOrderOnline.setBackgroundResource(activity.getVendorOpened().getOrderMode() == 1 ? R.drawable.capsule_theme_color_selector : R.drawable.capsule_grey_dark_bg);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void setRatingViews() {
		llRatingStars.removeAllViews();
		activity.addStarsToLayout(llRatingStars, activity.getVendorOpened().getRating(),
				R.drawable.ic_half_star_green_grey, R.drawable.ic_star_grey);
		llRatingStars.addView(tvReviewCount);
		tvReviewCount.setText(activity.getVendorOpened().getReviewCount()+" Ratings");
	}

	private void setUpCollapseToolbarData() {
		if (activity.getVendorOpened() != null) {
			if (!TextUtils.isEmpty(activity.getVendorOpened().getImage())) {
				Picasso.with(activity).load(activity.getVendorOpened().getImage())
						.placeholder(R.drawable.ic_fresh_item_placeholder)
						.into(activity.ivCollapseRestImage);
			} else {
				activity.ivCollapseRestImage.setImageDrawable(null);
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

				sendUserClickEvent(Constants.KEY_CHAT_MODE);
				break;
			case R.id.llCall:
				Utils.openCallIntent(activity, activity.getVendorOpened().getPhoneNo());
				sendUserClickEvent(Constants.KEY_CALL_MODE);
				break;
			case R.id.llAddReview:
				activity.openRestaurantAddReviewFragment(true);
				break;
			case R.id.bOrderOnline:
				if(activity.getVendorOpened().getOrderMode() == 0){
					Utils.showToast(activity, activity.getString(R.string.order_online_not_available));
				} else if (activity.getVendorOpened().getOrderMode() == 2){
					Utils.showToast(activity, activity.getString(R.string.order_online_closed));
				} else {
					if (activity.getMenuProductsResponse().getCategories() != null) {
						activity.getTransactionUtils().openVendorMenuFragment(activity, activity.getRelativeLayoutContainer());
					} else {
						activity.fetchRestaurantMenuAPI(activity.getVendorOpened().getRestaurantId(), false, null, null, -1, null);
					}
				}
				sendUserClickEvent(Constants.KEY_ORDER_MODE);
				break;
			case R.id.llSeeAll:
				activity.openRestaurantReviewsListFragment();
				break;
		}
	}


	private ArrayList<FetchFeedbackResponse.Review> restaurantReviews = new ArrayList<>();
	private ApiRestaurantFetchFeedback apiRestaurantFetchFeedback;
	public void fetchFeedback(){
		if(apiRestaurantFetchFeedback == null){
			apiRestaurantFetchFeedback = new ApiRestaurantFetchFeedback(activity, new ApiRestaurantFetchFeedback.Callback() {
				@Override
				public void onSuccess(FetchFeedbackResponse fetchFeedbackResponse, boolean scrollToTop) {
					if(getView() != null) {
						restaurantReviews.clear();
						restaurantReviews.addAll(fetchFeedbackResponse.getReviews());
						reviewsAdapter.notifyDataSetChanged();
						if(restaurantReviews.size() == 0){
							tvReviewsHeader.setText(R.string.no_reviews_yet_be_first);
							rvTopReviews.setVisibility(View.GONE);
							llSeeAll.setVisibility(View.GONE);
						} else {
							tvReviewsHeader.setText(R.string.reviews);
							rvTopReviews.setVisibility(View.VISIBLE);
							llSeeAll.setVisibility(View.VISIBLE);
						}
						if (fetchFeedbackResponse.getReviewImageLimit() != 0) {
							activity.setReviewImageCount(fetchFeedbackResponse.getReviewImageLimit());
						}
						if(fetchFeedbackResponse.getRestaurantInfo() != null){
							if( activity.getVendorOpened() != null) {
								activity.getVendorOpened().setRating(fetchFeedbackResponse.getRestaurantInfo().getRating());
								activity.getVendorOpened().setReviewCount(fetchFeedbackResponse.getRestaurantInfo().getReviewCount());
								setRatingViews();
							}
						}
					}
				}

				@Override
				public void onFailure() {
				}

				@Override
				public void onRetry(View view) {
					fetchFeedback();
				}

				@Override
				public void onNoRetry(View view) {

				}
			});
		}
		apiRestaurantFetchFeedback.hit(activity.getVendorOpened().getRestaurantId(), false, progressWheel, 1);
	}


	public void sendUserClickEvent(String eventName){
		HashMap<String, String> params = new HashMap<>();
		params.put(Constants.KEY_RESTAURANT_ID, String.valueOf(activity.getVendorOpened().getRestaurantId()));
		params.put(Constants.KEY_EVENT_TYPE, Constants.KEY_USER_CLICK);
		params.put(Constants.KEY_EVENT_STATUS, eventName);

		new ApiCommon<>(activity).showLoader(false).execute(params, ApiName.USER_CLICK_EVENTS,
				new APICommonCallback<FeedCommonResponse>() {
			@Override
			public boolean onNotConnected() {
				return true;
			}

			@Override
			public boolean onException(Exception e) {
				return true;
			}

			@Override
			public void onSuccess(FeedCommonResponse feedCommonResponse, String message, int flag) {

			}

			@Override
			public boolean onError(FeedCommonResponse feedCommonResponse, String message, int flag) {
				return true;
			}

			@Override
			public boolean onFailure(RetrofitError error) {
				return true;
			}

			@Override
			public void onNegativeClick() {

			}
		});
	}
}
