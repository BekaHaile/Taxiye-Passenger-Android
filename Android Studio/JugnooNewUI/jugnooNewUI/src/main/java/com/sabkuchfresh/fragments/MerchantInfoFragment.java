package com.sabkuchfresh.fragments;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
	@Bind(R.id.ivChatNow)
	ImageView ivChatNow;
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
					scrollView.postDelayed(scrollToTopRunnable, 100);
				}
			}
		} catch (Exception e) {
		}
	}

	private Runnable scrollToTopRunnable = new Runnable() {
		@Override
		public void run() {
			if(getView() != null) {
				scrollView.scrollTo(0, 0);
			}
		}
	};

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
				tvOpensAt.setText(activity.getVendorOpened().getRestaurantTimingsStr());
				tvMerchantMail.setText(activity.getVendorOpened().getEmail());
				tvMerchantMail.setVisibility(!TextUtils.isEmpty(activity.getVendorOpened().getEmail()) ? View.VISIBLE : View.GONE);
				tvMerchantContact.setText(activity.getVendorOpened().getContactList());
				tvMerchantContact.setVisibility(!TextUtils.isEmpty(activity.getVendorOpened().getContactList()) ? View.VISIBLE : View.GONE);
				stripUnderlines(tvMerchantContact);
				tvMerchantAddress.setText(activity.getVendorOpened().getAddress());
				tvMerchantAddress.setVisibility(!TextUtils.isEmpty(activity.getVendorOpened().getAddress()) ? View.VISIBLE : View.GONE);
				rvTopReviews.setVisibility(View.GONE);
				llSeeAll.setVisibility(View.GONE);
				fetchFeedback();
				if(!activity.getVendorOpened().isChatModeEnabled()) {
					ivChatNow.getDrawable().setColorFilter(BW_FILTER);
				} else {
					ivChatNow.getDrawable().setColorFilter(null);
				}
				bOrderOnline.setBackgroundResource((activity.getVendorOpened().getIsClosed() == 1 || activity.getVendorOpened().getIsAvailable() == 0) ?
						R.drawable.capsule_grey_dark_bg : R.drawable.capsule_theme_color_selector);
				bOrderOnline.setVisibility(activity.getVendorOpened().getOrderMode() == 0 ? View.GONE : View.VISIBLE);
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
				activity.ivCollapseRestImage.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_fresh_item_placeholder));
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
				if(activity.getVendorOpened().isChatModeEnabled()) {

					sendUserClickEvent(Constants.KEY_CHAT_MODE);
				} else {
					Utils.showToast(activity, activity.getString(R.string.chat_is_not_enabled_format, activity.getVendorOpened().getName()));
				}
				break;
			case R.id.llCall:
				Utils.openCallIntent(activity, activity.getVendorOpened().getCallingNumber());
				sendUserClickEvent(Constants.KEY_CALL_MODE);
				break;
			case R.id.llAddReview:
				if(userHasReviewed == 0) {
					activity.openRestaurantAddReviewFragment(true);
				} else {
					Utils.showToast(activity, activity.getString(R.string.you_have_already_reviewed_format, activity.getVendorOpened().getName()));
				}
				break;
			case R.id.bOrderOnline:
				if (activity.getMenuProductsResponse().getCategories() != null) {
					activity.getTransactionUtils().openVendorMenuFragment(activity, activity.getRelativeLayoutContainer());
				} else {
					activity.fetchRestaurantMenuAPI(activity.getVendorOpened().getRestaurantId(), false, null, null, -1, null);
				}
				sendUserClickEvent(Constants.KEY_ORDER_MODE);
				break;
			case R.id.llSeeAll:
				activity.openRestaurantReviewsListFragment();
				break;
		}
	}


	private int userHasReviewed = 0;
	private ArrayList<FetchFeedbackResponse.Review> restaurantReviews = new ArrayList<>();
	private ApiRestaurantFetchFeedback apiRestaurantFetchFeedback;
	public void fetchFeedback(){
		if(apiRestaurantFetchFeedback == null){
			apiRestaurantFetchFeedback = new ApiRestaurantFetchFeedback(activity, new ApiRestaurantFetchFeedback.Callback() {
				@Override
				public void onSuccess(FetchFeedbackResponse fetchFeedbackResponse, boolean scrollToTop) {
					if(getView() != null) {
						userHasReviewed = fetchFeedbackResponse.getHasAlreadyRated();
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

	private void stripUnderlines(TextView textView) {
		Spannable s = new SpannableString(textView.getText());
		URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
		for (URLSpan span: spans) {
			int start = s.getSpanStart(span);
			int end = s.getSpanEnd(span);
			s.removeSpan(span);
			span = new URLSpanNoUnderline(span.getURL());
			s.setSpan(span, start, end, 0);
		}
		textView.setText(s);
	}

	private class URLSpanNoUnderline extends URLSpan {
		public URLSpanNoUnderline(String url) {
			super(url);
		}
		@Override public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
		}
	}

	private final static ColorMatrix BW_MATRIX = new ColorMatrix();
	private final static ColorMatrixColorFilter BW_FILTER;
	static {
		BW_MATRIX.setSaturation(0);
		BW_FILTER = new ColorMatrixColorFilter(BW_MATRIX);
	}
}
