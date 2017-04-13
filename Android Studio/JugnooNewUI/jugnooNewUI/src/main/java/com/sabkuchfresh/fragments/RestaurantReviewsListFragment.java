package com.sabkuchfresh.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.RestaurantReviewsAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.commoncalls.ApiRestaurantFetchFeedback;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by Shankar on 15/11/16.
 */
public class RestaurantReviewsListFragment extends Fragment implements GAAction{
    private final String TAG = RestaurantReviewsListFragment.class.getSimpleName();

    private RelativeLayout rlRoot;
    private RecyclerView recyclerViewReviews;
    private RelativeLayout rlNoReviews;
    private TextView tvFeedEmpty;
    private RestaurantReviewsAdapter reviewsAdapter;

    private View rootView;
    private FreshActivity activity;
    private ArrayList<FetchFeedbackResponse.Review> restaurantReviews;
    private int restaurantId;
    private FetchFeedbackResponse fetchFeedbackResponse;

    public static RestaurantReviewsListFragment newInstance(int restaurantId){
        RestaurantReviewsListFragment fragment = new RestaurantReviewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_RESTAURANT_ID, restaurantId);
        fragment.setArguments(bundle);
        return fragment;
    }


    private void fetchArguments(){
        Bundle bundle = getArguments();
        restaurantId = bundle.getInt(Constants.KEY_RESTAURANT_ID, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_restaurant_reviews_list, container, false);

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
        GAUtils.trackScreenView(MENUS+FEED);

        recyclerViewReviews = (RecyclerView) rootView.findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewReviews.setItemAnimator(new DefaultItemAnimator());
        recyclerViewReviews.setHasFixedSize(false);
        restaurantReviews = new ArrayList<>();
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
				/**
				 * if an item's image recyclerView is starting to scroll, main recyclerViews scrolling to be stopped
                 */
                if(newState != RecyclerView.SCROLL_STATE_IDLE){
                    recyclerViewReviews.setEnabled(false);
                } else {
                    recyclerViewReviews.setEnabled(true);
                }
            }

            @Override
            public int getRestaurantId() {
                return restaurantId;
            }

            @Override
            public String getShareTextSelf() {
                return fetchFeedbackResponse == null ? "" : fetchFeedbackResponse.getShareTextSelf();
            }

            @Override
            public String getShareTextOther() {
                return fetchFeedbackResponse == null ? "" : fetchFeedbackResponse.getShareTextOther();
            }

            @Override
            public int getShareIsEnabled() {
                return fetchFeedbackResponse == null ? 1 : fetchFeedbackResponse.getShareIsEnabled();
            }

            @Override
            public int getLikeIsEnabled() {
                return fetchFeedbackResponse == null ? 1 : fetchFeedbackResponse.getLikeIsEnabled();
            }
        }, restaurantReviews);
        recyclerViewReviews.setAdapter(reviewsAdapter);
        recyclerViewReviews.setEnabled(true);


        rlNoReviews = (RelativeLayout) rootView.findViewById(R.id.rlNoReviews);
        tvFeedEmpty = (TextView) rootView.findViewById(R.id.tvFeedEmpty);

        tvFeedEmpty.setText(activity.getString(R.string.feed_is_empty));
        SpannableStringBuilder ssb = new SpannableStringBuilder(activity.getString(R.string.be_first_one_to_add));
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvFeedEmpty.append("\n");
        tvFeedEmpty.append(ssb);

        fetchFeedback();

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            activity.fragmentUISetup(this);
        }
    }

    private ApiRestaurantFetchFeedback apiRestaurantFetchFeedback;
    public void fetchFeedback(){
        if(apiRestaurantFetchFeedback == null){
            apiRestaurantFetchFeedback = new ApiRestaurantFetchFeedback(activity, new ApiRestaurantFetchFeedback.Callback() {
                @Override
                public void onSuccess(FetchFeedbackResponse fetchFeedbackResponse) {
                    restaurantReviews.clear();
                    restaurantReviews.addAll(fetchFeedbackResponse.getReviews());
                    reviewsAdapter.notifyDataSetChanged();
                    rlNoReviews.setVisibility(restaurantReviews.size() == 0 ? View.VISIBLE : View.GONE);
                    RestaurantReviewsListFragment.this.fetchFeedbackResponse = fetchFeedbackResponse;
                    if (fetchFeedbackResponse.getReviewImageLimit() != 0) {
                        activity.setReviewImageCount(fetchFeedbackResponse.getReviewImageLimit());
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
        apiRestaurantFetchFeedback.hit(restaurantId);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ASSL.closeActivity(rlRoot);
        System.gc();
    }

}