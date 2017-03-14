package com.sabkuchfresh.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.adapters.FeedOfferingCommentsAdapter;
import com.sabkuchfresh.commoncalls.LikeFeed;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.feeddetail.FeedDetailResponse;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FeedCommentsFragment extends Fragment {

    private static final String FEED_DETAIL = "feed_detail";
    private static final String FEED_POST = "feedpost";
    private FeedDetail  feedDetail;
    private FeedOfferingCommentsAdapter feedOfferingCommentsAdapter;
    private FreshActivity activity;
    private ArrayList<Object> dataList;
    private TextView textViewCharCount;
    private Button btnSubmit;
    private String commentAdded;
    private LikeFeed likeFeed;


    public FeedCommentsFragment() {
        // Required empty public constructor
    }


    public static FeedCommentsFragment newInstance(FeedDetail feedDetail) {
        FeedCommentsFragment fragment = new FeedCommentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(FEED_DETAIL, feedDetail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            feedDetail = (FeedDetail) getArguments().getSerializable(FEED_DETAIL);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FreshActivity)
            activity= (FreshActivity) context;

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed_comments, container, false);
        btnSubmit= (Button) rootView.findViewById(R.id.btnSubmit);
        textViewCharCount= (TextView) rootView.findViewById(R.id.tvCharCount);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_feed_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        feedOfferingCommentsAdapter = new FeedOfferingCommentsAdapter(getActivity(), null, recyclerView, new FeedOfferingCommentsAdapter.Callback() {
            @Override
            public void onLikeClick(Object object) {
                if(likeFeed ==null)
                    likeFeed = new LikeFeed(new LikeFeed.LikeCallbackResponse() {
                        @Override
                        public void onSuccess() {

                        }
                    });
                likeFeed.likeFeed(feedDetail.getPostId(),getActivity());
            }

            @Override
            public void onCommentClick(Object object) {

            }
        },submitTextWatcher);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(commentAdded))
                    commentOnFeed(commentAdded.trim());
            }
        });
        recyclerView.setAdapter(feedOfferingCommentsAdapter);
        fetchFeedDetail();
        return rootView;
    }



    private void fetchFeedDetail() {
        try {
            if(MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(getActivity(), getActivity().getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, "bc1ff5a34edab8d37c56a977023b8f4d473d22e83facfd534f26341579c94b54");
                params.put(Constants.KEY_POST_ID, String.valueOf(feedDetail.getPostId()));

                new HomeUtil().putDefaultParams(params);
                RestClient.getFeedApiService().fetchFeedDetails(params, new retrofit.Callback<FeedDetailResponse>() {
                    @Override
                    public void success(FeedDetailResponse feedbackResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            String message = feedbackResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, feedbackResponse.getFlag(),
                                    feedbackResponse.getError(), feedbackResponse.getMessage())) {
                                if(feedbackResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
                                        if(dataList==null) {
                                            dataList = new ArrayList<>();
                                        }

                                    dataList.clear();
                                    dataList.add(feedDetail);
                                    dataList.add(FeedOfferingCommentsAdapter.TYPE_MY_COMMENT);

                                    if(feedbackResponse.getFeedComments()!=null)
                                      dataList.addAll(feedbackResponse.getFeedComments());

                                    feedOfferingCommentsAdapter.setList(dataList);
//                                    feedOfferingCommentsAdapter.setList(feedbackResponse.getFeeds());
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

    private void commentOnFeed(final String comments) {
        try {
            if(MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(getActivity(), getActivity().getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, "bc1ff5a34edab8d37c56a977023b8f4d473d22e83facfd534f26341579c94b54");
                params.put(Constants.KEY_POST_ID, String.valueOf(feedDetail.getPostId()));
                params.put(Constants.KEY_COMMENT_CONTENT, String.valueOf(comments));

                new HomeUtil().putDefaultParams(params);
                RestClient.getFeedApiService().commentOnFeed(params, new retrofit.Callback<SettleUserDebt>() {
                    @Override
                    public void success(SettleUserDebt feedbackResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            String message = feedbackResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, feedbackResponse.getFlag(), feedbackResponse.getError(), feedbackResponse.getMessage())) {
                                if(feedbackResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
                                    Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();
                                    Utils.hideKeyboard(activity);

                                } else {
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryCommentAPIDialog(DialogErrorType.SERVER_ERROR,comments);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        retryCommentAPIDialog(DialogErrorType.CONNECTION_LOST,comments);

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

    private void retryCommentAPIDialog(DialogErrorType dialogErrorType, final String commentAdded){
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        commentOnFeed(commentAdded);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {

                    }
                });
    }

    private void retryDialog(DialogErrorType dialogErrorType){
        DialogPopup.dialogNoInternet(activity,
                dialogErrorType,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        fetchFeedDetail();
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {

                    }
                });
    }

    TextWatcher submitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {


        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            commentAdded=s.toString();
            textViewCharCount.setText(String.valueOf(500-s.length()));
            if(s.length()==0||s.length()==1)
                btnSubmit.setEnabled(s.length()>0);


        }
    };
}
