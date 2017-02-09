package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sabkuchfresh.adapters.RestaurantReviewsAdapter;
import com.sabkuchfresh.commoncalls.ApiRestaurantFetchFeedback;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;

/**
 * Created by Shankar on 15/11/16.
 */
public class RestaurantReviewsListFragment extends Fragment{
    private final String TAG = RestaurantReviewsListFragment.class.getSimpleName();

    private RelativeLayout rlRoot;
    private RecyclerView recyclerViewReviews;
    private RestaurantReviewsAdapter reviewsAdapter;

    private View rootView;
    private FreshActivity activity;
    private ArrayList<FetchFeedbackResponse.Review> restaurantReviews;
    private int restaurantId;

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

        recyclerViewReviews = (RecyclerView) rootView.findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewReviews.setItemAnimator(new DefaultItemAnimator());
        recyclerViewReviews.setHasFixedSize(false);

        restaurantReviews = new ArrayList<>();
        reviewsAdapter = new RestaurantReviewsAdapter(activity, restaurantReviews);
        recyclerViewReviews.setAdapter(reviewsAdapter);

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
    private void fetchFeedback(){
        if(apiRestaurantFetchFeedback == null){
            apiRestaurantFetchFeedback = new ApiRestaurantFetchFeedback(activity, new ApiRestaurantFetchFeedback.Callback() {
                @Override
                public void onSuccess(List<FetchFeedbackResponse.Review> reviews) {
                    restaurantReviews.clear();
                    restaurantReviews.addAll(reviews);
                    reviewsAdapter.notifyDataSetChanged();
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
