package com.sabkuchfresh.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sabkuchfresh.adapters.FeedOfferingListAdapter;
import com.sabkuchfresh.commoncalls.LikeFeed;
import com.sabkuchfresh.home.FeedContactsUploadService;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedListResponse;
import com.sabkuchfresh.utils.AppConstant;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FeedHomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private FeedOfferingListAdapter feedOfferingListAdapter;
    private TextView tvAddPost;
    private LikeFeed likeFeed;
    private SwipeRefreshLayout swipeRefreshLayout;


    public FeedHomeFragment() {
        // Required empty public constructor
    }


    public static FeedHomeFragment newInstance(String param1, String param2) {
        FeedHomeFragment fragment = new FeedHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FreshActivity) {
            activity = (FreshActivity) context;
            activity.registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION_CONTACTS_UPLOADED));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (activity != null) {
            activity.unregisterReceiver(broadcastReceiver);
        }
    }

    private FreshActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.fragmentUISetup(this);
        View rootView = inflater.inflate(R.layout.fragment_feed_offering_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchFeedsApi(false);
            }
        });
        feedOfferingListAdapter = new FeedOfferingListAdapter(getActivity(), null, recyclerView, new FeedOfferingListAdapter.Callback() {
            @Override
            public void onLikeClick(FeedDetail feedDetail, final int position) {
                if (likeFeed == null)
                        likeFeed = new LikeFeed(new LikeFeed.LikeUnLikeCallbackResponse() {
                        @Override
                        public void onSuccess(boolean isLiked,int position) {
                            feedOfferingListAdapter.notifyOnLike(position,isLiked);
                        }
                    });

                likeFeed.likeFeed(feedDetail.getPostId(), getActivity(), !feedDetail.isLiked(),position);

            }

            @Override
            public void onCommentClick(final FeedDetail feedDetail,int positionInOriginalList) {

                activity.getTransactionUtils().openFeedCommentsFragment(activity, activity.getRelativeLayoutContainer(), feedDetail,positionInOriginalList);

            }


            @Override
            public void onRestaurantClick(int restaurantId) {
                if(restaurantId > 0){
                    activity.fetchRestaurantMenuAPI(restaurantId, 1, null);
                }
            }
        });
        recyclerView.setAdapter(feedOfferingListAdapter);
        activity.setDeliveryAddressView(rootView);
        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FEED);
        if (activity.getDeliveryAddressView() != null) {
            activity.getDeliveryAddressView().scaleView();
        }
        tvAddPost = (TextView) rootView.findViewById(R.id.tvAddPost);
        tvAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getTransactionUtils().openFeedAddPostFragment(activity, activity.getRelativeLayoutContainer());
            }
        });


        try {
            if (Data.getFeedData() != null
                    && Data.getFeedData().getContactsSynced() != null && Data.getFeedData().getContactsSynced() == 0) {
                Intent syncContactsIntent = new Intent(activity, FeedContactsUploadService.class);
                syncContactsIntent.putExtra(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                activity.startService(syncContactsIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
        }
    }

    public void fetchFeedsApi(boolean loader) {
        try {
            if (MyApplication.getInstance().isOnline()) {

                if(loader) {
                    DialogPopup.showLoadingDialog(getActivity(), getActivity().getResources().getString(R.string.loading));
                }

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, "bc1ff5a34edab8d37c56a977023b8f4d473d22e83facfd534f26341579c94b54");
                params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));

                new HomeUtil().putDefaultParams(params);
                RestClient.getFeedApiService().generateFeed(params, new retrofit.Callback<FeedListResponse>() {
                    @Override
                    public void success(FeedListResponse feedbackResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        DialogPopup.dismissLoadingDialog();
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            String message = feedbackResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, feedbackResponse.getFlag(),
                                    feedbackResponse.getError(), feedbackResponse.getMessage())) {
                                if (feedbackResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                    feedOfferingListAdapter.setList(feedbackResponse.getFeeds());
                                    if(!TextUtils.isEmpty(feedbackResponse.getAddPostText())){
                                        tvAddPost.setText(feedbackResponse.getAddPostText());
                                    }
                                } else {
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(DialogErrorType.SERVER_ERROR);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        retryDialog(DialogErrorType.CONNECTION_LOST);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            } else {
                retryDialog(DialogErrorType.NO_NET);
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void retryDialog(DialogErrorType dialogErrorType) {
        DialogPopup.dialogNoInternet(activity, dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        fetchFeedsApi(true);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {

                    }
                });
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean uploaded = intent.getBooleanExtra(Constants.KEY_UPLOADED, false);
            Log.i("FeedHomeFrag onReceive", "uploaded="+uploaded);
            if(uploaded){
                Utils.showToast(activity, activity.getString(R.string.contacts_synced_successfully));
                fetchFeedsApi(false);
            }
        }
    };


    public void notifyOnLikeFromCommentsFragment(int positionItemLikedUnlikedInCommentsFragment) {
        if(feedOfferingListAdapter!=null)
            feedOfferingListAdapter.notifyItemChanged(positionItemLikedUnlikedInCommentsFragment);
    }
}
