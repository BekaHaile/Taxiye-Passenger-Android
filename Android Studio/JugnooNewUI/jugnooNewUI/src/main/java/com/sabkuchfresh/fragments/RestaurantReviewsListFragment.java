package com.sabkuchfresh.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sabkuchfresh.adapters.RestaurantReviewsAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.commoncalls.ApiRestaurantFetchFeedback;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by Shankar on 15/11/16.
 */
public class RestaurantReviewsListFragment extends Fragment implements GAAction{
    private final String TAG = RestaurantReviewsListFragment.class.getSimpleName();

    private RecyclerView recyclerViewReviews;
    private RelativeLayout rlNoReviews;
    private TextView tvFeedEmpty;
    private RestaurantReviewsAdapter reviewsAdapter;
    private Button bAddReview;
    private LinearLayout llRatingStars;
    private TextView tvRating, tvRatingCount;

    private View rootView;
    private FreshActivity activity;
    private ArrayList<FetchFeedbackResponse.Review> restaurantReviews;
    private MenusResponse.Vendor vendor;
    private FetchFeedbackResponse fetchFeedbackResponse;

    public static RestaurantReviewsListFragment newInstance(MenusResponse.Vendor vendor){
        RestaurantReviewsListFragment fragment = new RestaurantReviewsListFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putString(Constants.KEY_VENDOR, gson.toJson(vendor, MenusResponse.Vendor.class));
        fragment.setArguments(bundle);
        return fragment;
    }


    private void fetchArguments(){
        Bundle bundle = getArguments();
        Gson gson = new Gson();
        vendor = gson.fromJson(bundle.getString(Constants.KEY_VENDOR), MenusResponse.Vendor.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_restaurant_reviews_list, container, false);

        fetchArguments();

        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);

        GAUtils.trackScreenView(activity.getGaCategory()+FEED);

        recyclerViewReviews = (RecyclerView) rootView.findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(activity));
        ((SimpleItemAnimator) recyclerViewReviews.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerViewReviews.setHasFixedSize(false);
        restaurantReviews = new ArrayList<>();
        reviewsAdapter = new RestaurantReviewsAdapter(activity, new RestaurantReviewsAdapter.Callback() {
            @Override
            public void onEdit(FetchFeedbackResponse.Review review) {
                activity.setCurrentReview(review);
                activity.openRestaurantAddReviewFragment(true, 0.0f);
            }

            @Override
            public void onShare(FetchFeedbackResponse.Review review) {

            }

            @Override
            public void onLike(FetchFeedbackResponse.Review review) {
                GAUtils.event(activity.getGaCategory(), GAAction.FEED , GAAction.FEED + GAAction.LIKED);
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
                return vendor.getRestaurantId();
            }

            @Override
            public MenusResponse.Vendor getVendor() {
                return vendor;
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

            @Override
            public RecyclerView getRecyclerView() {
                return recyclerViewReviews;
            }
        }, restaurantReviews, false);
        recyclerViewReviews.setAdapter(reviewsAdapter);
        recyclerViewReviews.setEnabled(true);


        rlNoReviews = (RelativeLayout) rootView.findViewById(R.id.rlNoReviews);
        tvFeedEmpty = (TextView) rootView.findViewById(R.id.tvFeedEmpty);
        bAddReview = (Button) rootView.findViewById(R.id.bAddReview);
        llRatingStars = (LinearLayout) rootView.findViewById(R.id.llRatingStars);
        tvRating = (TextView) rootView.findViewById(R.id.tvRating);
        tvRatingCount = (TextView) rootView.findViewById(R.id.tvRatingCount);

        tvFeedEmpty.setText(activity.getString(R.string.no_reviews_yet));
        SpannableStringBuilder ssb = new SpannableStringBuilder(activity.getString(R.string.be_the_first_one_to_add));
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvFeedEmpty.append("\n");
        tvFeedEmpty.append(ssb);

        activity.setRestaurantRatingStarsToLL(llRatingStars, tvRating,
                vendor.getRating(), R.drawable.ic_half_star_green_grey, R.drawable.ic_star_grey, tvRatingCount, 0, Color.parseColor(vendor.getColorCode()));
        tvRatingCount.setText("("+vendor.getReviewCount()+")");

        fetchFeedback(false);

        bAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openRestaurantAddReviewFragment(false, 0.0f);
            }
        });

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            activity.fragmentUISetup(this);
            if( activity.getVendorOpened().isHasRated()){
                bAddReview.setVisibility(View.GONE);
            }

        }
    }

    private ApiRestaurantFetchFeedback apiRestaurantFetchFeedback;
    public void fetchFeedback(final boolean scrollToTop){
        if(apiRestaurantFetchFeedback == null){
            apiRestaurantFetchFeedback = new ApiRestaurantFetchFeedback(activity, new ApiRestaurantFetchFeedback.Callback() {
                @Override
                public void onSuccess(FetchFeedbackResponse fetchFeedbackResponse, boolean scrollToTop) {
                    if(RestaurantReviewsListFragment.this.getView() != null) {
                        bAddReview.setVisibility(fetchFeedbackResponse.getHasAlreadyRated()==1?View.GONE:View.VISIBLE);
                        restaurantReviews.clear();
                        restaurantReviews.addAll(fetchFeedbackResponse.getReviews());
                        reviewsAdapter.notifyDataSetChanged();
                        rlNoReviews.setVisibility(restaurantReviews.size() == 0 ? View.VISIBLE : View.GONE);
                        RestaurantReviewsListFragment.this.fetchFeedbackResponse = fetchFeedbackResponse;
                        if (fetchFeedbackResponse.getReviewImageLimit() != 0) {
                            activity.setReviewImageCount(fetchFeedbackResponse.getReviewImageLimit());
                        }
                        scrollToFeedbackIfNeeded(scrollToTop);
                        if(fetchFeedbackResponse.getRestaurantInfo() != null){
                            vendor.setRating(fetchFeedbackResponse.getRestaurantInfo().getRating());
                            vendor.setReviewCount(fetchFeedbackResponse.getRestaurantInfo().getReviewCount());
                            if( activity.getVendorOpened() != null) {
                                activity.getVendorOpened().setRating(fetchFeedbackResponse.getRestaurantInfo().getRating());
                                activity.getVendorOpened().setReviewCount(fetchFeedbackResponse.getRestaurantInfo().getReviewCount());
                            }
                            activity.setRestaurantRatingStarsToLL(llRatingStars, tvRating,
                                    vendor.getRating(), R.drawable.ic_half_star_green_grey, R.drawable.ic_star_grey, tvRatingCount, 0
                            ,Color.parseColor(vendor.getColorCode()));
                            tvRatingCount.setText("("+vendor.getReviewCount()+")");
                        }
                    }
                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onRetry(View view) {
                    fetchFeedback(scrollToTop);
                }

                @Override
                public void onNoRetry(View view) {

                }
            });
        }
        apiRestaurantFetchFeedback.hit(vendor.getRestaurantId(), scrollToTop, null, -1);
    }



    private void scrollToFeedbackIfNeeded(boolean scrollToTop){
        try {
            int feedbackId = Prefs.with(activity).getInt(Constants.SP_RESTAURANT_FEEDBACK_ID_TO_DEEP_LINK, -1);
            if(feedbackId > 0){
                for(int i=0; i<restaurantReviews.size(); i++){
                    if(feedbackId == restaurantReviews.get(i).getFeedbackId()){
                        final int finalI = i;
                        scrollToTop = false;
                        recyclerViewReviews.post(new Runnable() {
                            @Override
                            public void run() {
                                recyclerViewReviews.smoothScrollToPosition(finalI);
                            }
                        });
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Prefs.with(activity).save(Constants.SP_RESTAURANT_FEEDBACK_ID_TO_DEEP_LINK, -1);
        }

        if (scrollToTop) {
            recyclerViewReviews.post(new Runnable() {
                @Override
                public void run() {
                    recyclerViewReviews.scrollToPosition(0);
                }
            });
        }
    }


}
