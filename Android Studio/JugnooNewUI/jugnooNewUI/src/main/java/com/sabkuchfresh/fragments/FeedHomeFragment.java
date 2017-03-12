package com.sabkuchfresh.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jugnoo.pay.models.CommonResponse;
import com.sabkuchfresh.adapters.FeedOfferingListAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedListResponse;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;

import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.datastructure.FeedData;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FeedHomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private FeedOfferingListAdapter feedOfferingListAdapter;


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
        if(context instanceof FreshActivity)
            activity= (FreshActivity) context;

    }


    private FreshActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed_offering_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        feedOfferingListAdapter = new FeedOfferingListAdapter(getActivity(), null, recyclerView, new FeedOfferingListAdapter.Callback() {
            @Override
            public void onLikeClick(Object object) {

            }

            @Override
            public void onCommentClick(Object object) {

            }
        });
        recyclerView.setAdapter(feedOfferingListAdapter);
        fetchFeedsApi();
        return rootView;
    }

    private void fetchFeedsApi() {
        try {
            if(MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(getActivity(), getActivity().getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, "bc1ff5a34edab8d37c56a977023b8f4d473d22e83facfd534f26341579c94b54");
                params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));

                new HomeUtil().putDefaultParams(params);
                RestClient.getFeedApiService().getAllFeeds(params, new retrofit.Callback<FeedListResponse>() {
                    @Override
                    public void success(FeedListResponse feedbackResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            String message = feedbackResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, feedbackResponse.getFlag(),
                                    feedbackResponse.getMessage(), feedbackResponse.getMessage())) {
                                if(feedbackResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
                                    feedOfferingListAdapter.setList(feedbackResponse.getFeeds());
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

                    }
                });
            }
            else {
                retryDialog(DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    private void retryDialog(DialogErrorType dialogErrorType){
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        fetchFeedsApi();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {
                        fetchFeedsApi();;
                    }
                });
    }
}
