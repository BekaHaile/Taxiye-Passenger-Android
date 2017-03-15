package com.sabkuchfresh.fragments;


import android.content.Context;
import android.graphics.Typeface;
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

import com.sabkuchfresh.adapters.FeedOfferingCommentsAdapter;
import com.sabkuchfresh.commoncalls.LikeFeed;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.feeddetail.FeedDetailResponse;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;

import java.util.ArrayList;
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
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FeedOfferingCommentsFragment extends Fragment {

    private static final String FEED_DETAIL = "feed_detail";
    private static final String POSITION_IN_ORIGINAL_LIST = "positionInOriginalList";
    private FeedDetail  feedDetail;
    private FeedOfferingCommentsAdapter feedOfferingCommentsAdapter;
    private FreshActivity activity;
    private ArrayList<Object> dataList;
    private TextView textViewCharCount;
    private Button btnSubmit;
    public String commentAdded;
    private LikeFeed likeFeed;
    private int positionInOriginalList;


    public FeedOfferingCommentsFragment() {
        // Required empty public constructor
    }


    public static FeedOfferingCommentsFragment newInstance(FeedDetail feedDetail, int positionInOriginalList) {
        FeedOfferingCommentsFragment fragment = new FeedOfferingCommentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(FEED_DETAIL, feedDetail);
        args.putSerializable(POSITION_IN_ORIGINAL_LIST, positionInOriginalList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            feedDetail = (FeedDetail) getArguments().getSerializable(FEED_DETAIL);
            positionInOriginalList = getArguments().getInt(POSITION_IN_ORIGINAL_LIST);
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
        activity.fragmentUISetup(this);
        View rootView = inflater.inflate(R.layout.fragment_feed_comments, container, false);
        btnSubmit= (Button) rootView.findViewById(R.id.btnSubmit);
        btnSubmit.setTypeface(btnSubmit.getTypeface(), Typeface.BOLD);
        btnSubmit.setEnabled(false);
        textViewCharCount= (TextView) rootView.findViewById(R.id.tvCharCount);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_feed_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        feedOfferingCommentsAdapter = new FeedOfferingCommentsAdapter(getActivity(), null, recyclerView, new FeedOfferingCommentsAdapter.Callback() {
            @Override
            public void onLikeClick(final int position) {
                if(likeFeed ==null)
                    likeFeed = new LikeFeed(new LikeFeed.LikeUnLikeCallbackResponse() {
                        @Override
                        public void onSuccess(boolean isLikeAPI,int position) {
                            feedOfferingCommentsAdapter.notifyOnLike(position,isLikeAPI);
                            if(activity.getFeedHomeFragment()!=null) {
                                //notifies the feed home fragment that user has liked unliked post so it can refresh accordingly
                                activity.getFeedHomeFragment().notifyOnLikeFromCommentsFragment(positionInOriginalList);
                            }
                        }

                    });
                likeFeed.likeFeed(feedDetail.getPostId(),getActivity(), !feedDetail.isLiked(), position);


            }

            @Override
            public void onCommentClick(Object object) {

            }

            @Override
            public String getEditTextString() {
                return commentAdded;
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



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
        }
    }

    private void fetchFeedDetail() {
        try {
            if(MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(getActivity(), getActivity().getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
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
                                    prepareListAndNotifyAdapter(feedbackResponse);
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

    private void prepareListAndNotifyAdapter(FeedDetailResponse feedbackResponse) {
        if(dataList==null) {
            dataList = new ArrayList<>();
        }

        dataList.clear();
        dataList.add(feedDetail);
        dataList.add(FeedOfferingCommentsAdapter.TYPE_MY_COMMENT);

        if(feedbackResponse.getFeedComments()!=null)
          dataList.addAll(feedbackResponse.getFeedComments());

        feedOfferingCommentsAdapter.setList(dataList);
    }

    private void commentOnFeed(final String comments) {
        try {
            if(MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(getActivity(), getActivity().getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_POST_ID, String.valueOf(feedDetail.getPostId()));
                params.put(Constants.KEY_COMMENT_CONTENT, String.valueOf(comments));

                new HomeUtil().putDefaultParams(params);
                RestClient.getFeedApiService().commentOnFeed(params, new retrofit.Callback<FeedDetailResponse>() {
                    @Override
                    public void success(FeedDetailResponse feedbackResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            String message = feedbackResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, feedbackResponse.getFlag(), feedbackResponse.getError(), feedbackResponse.getMessage())) {
                                if(feedbackResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
                                    Utils.hideKeyboard(getActivity());
                                    commentAdded=null;
                                    feedDetail.setCommentCount(feedDetail.getCommentCount()+1);
                                    prepareListAndNotifyAdapter(feedbackResponse);
                                    if(activity.getFeedHomeFragment()!=null) {
                                        //notifies the feed home fragment that user has liked unliked post so it can refresh accordingly
                                        activity.getFeedHomeFragment().notifyOnLikeFromCommentsFragment(positionInOriginalList);
                                    }

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
            textViewCharCount.setText(String.valueOf(500-s.toString().trim().length()));
            btnSubmit.setEnabled(s.length()>0);


        }
    };
}
