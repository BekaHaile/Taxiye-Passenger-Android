package com.sabkuchfresh.feed.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.feed.models.FeedCommonResponse;
import com.sabkuchfresh.feed.ui.adapters.FeedHomeAdapter;
import com.sabkuchfresh.feed.ui.adapters.FeedOfferingCommentsAdapter;
import com.sabkuchfresh.feed.ui.api.DeleteFeed;
import com.sabkuchfresh.feed.ui.api.LikeFeed;
import com.sabkuchfresh.feed.ui.dialogs.DeletePostDialog;
import com.sabkuchfresh.feed.ui.dialogs.DialogPopupTwoButtonCapsule;
import com.sabkuchfresh.feed.ui.dialogs.EditPostPopup;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.feeddetail.FeedComment;
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
import retrofit.mime.TypedByteArray;


public class FeedOfferingCommentsFragment extends Fragment implements DeletePostDialog.DeleteDialogCallback,EditPostPopup.EditPostDialogCallback, GAAction {

    private static final String FEED_DETAIL = "feed_detail";
    private static final String POSITION_IN_ORIGINAL_LIST = "positionInOriginalList";
    private static final String OPEN_KEYBOARD_ON_LOAD = "openKeyboardOnLoad";
    private FeedDetail feedDetail;
    private FeedOfferingCommentsAdapter feedOfferingCommentsAdapter;
    private FreshActivity activity;
    private ArrayList<Object> dataList;
    private TextView textViewCharCount;
    private TextView btnSubmit;
    public String commentAdded;
    private LikeFeed likeFeed;
    private int positionInOriginalList;
    private EditText edtMyComment;
    private RecyclerView recyclerView;
    private DeleteFeed deleteFeed;
    private boolean openKeyboardOnLoad;


    public FeedOfferingCommentsFragment() {
        // Required empty public constructor
    }


    public static FeedOfferingCommentsFragment newInstance(FeedDetail feedDetail, int positionInOriginalList, boolean openKeyboardOnLoad) {
        FeedOfferingCommentsFragment fragment = new FeedOfferingCommentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(FEED_DETAIL, feedDetail);
        args.putSerializable(POSITION_IN_ORIGINAL_LIST, positionInOriginalList);
        args.putBoolean(OPEN_KEYBOARD_ON_LOAD, openKeyboardOnLoad);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            feedDetail = (FeedDetail) getArguments().getSerializable(FEED_DETAIL);
            positionInOriginalList = getArguments().getInt(POSITION_IN_ORIGINAL_LIST);
            openKeyboardOnLoad = getArguments().getBoolean(OPEN_KEYBOARD_ON_LOAD);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FreshActivity)
            activity = (FreshActivity) context;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.fragmentUISetup(this);
        View rootView = inflater.inflate(R.layout.fragment_feed_comments, container, false);
        btnSubmit = (TextView) rootView.findViewById(R.id.btnSubmit);
        btnSubmit.setEnabled(false);
        textViewCharCount = (TextView) rootView.findViewById(R.id.tvCharCount);
        edtMyComment = (EditText) rootView.findViewById(R.id.edt_my_comment);
        edtMyComment.addTextChangedListener(submitTextWatcher);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_feed_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        feedOfferingCommentsAdapter = new FeedOfferingCommentsAdapter(getActivity(), null, recyclerView, new FeedHomeAdapter.FeedPostCallback() {
            @Override
            public void onLikeClick(FeedDetail object, int position) {
                if (likeFeed == null)
                    likeFeed = new LikeFeed(new LikeFeed.LikeUnLikeCallbackResponse() {
                        @Override
                        public void onSuccess(boolean isLikeAPI, int position) {
                            feedOfferingCommentsAdapter.notifyOnLike(position, isLikeAPI);
                            if (activity.getFeedHomeFragment() != null) {
                                //notifies the feed home fragment that user has liked unliked post so it can refresh accordingly
                                activity.getFeedHomeFragment().refreshFeedInHomeFragment(positionInOriginalList);
                            }
                        }

                    });
                likeFeed.likeFeed(feedDetail.getPostId(), getActivity(), !feedDetail.isLiked(), position);
                GAUtils.event(FEED, COMMENT, LIKE+CLICKED);
            }

            @Override
            public void onCommentClick(FeedDetail postId, int position) {
                edtMyComment.requestFocus();
                Utils.showKeyboard(activity, edtMyComment);
                GAUtils.event(FEED, COMMENT, COMMENT+CLICKED);
            }

            @Override
            public void onRestaurantClick(int restaurantId) {
                if (restaurantId > 0) {
                    activity.fetchRestaurantMenuAPI(restaurantId);
                }

            }

            @Override
            public String getEditTextString() {
                return commentAdded;
            }

            @Override
            public void onMoreClick(FeedDetail feedDetail, int positionInOriginalList, View moreItemView) {
                getEditPostDialog().show(feedDetail,moreItemView,positionInOriginalList);

            }

            @Override
            public void onDeleteComment(final FeedComment feedComment, final int positionInList, View viewClicked) {
             DialogPopup.alertPopupTwoButtonsWithListeners(activity, "Delete Reply", "Are you sure you want to delete?", "Yes", "No", new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     deleteCommentAPI(feedComment.getActivityId(),positionInList,feedDetail.getPostId());
                 }
             }, new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                 }
             },true,false);

            }

            @Override
            public void onFeedLayoutClick(FeedDetail feedDetail, int position) {

            }
        }, submitTextWatcher);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(commentAdded)) {
                    commentOnFeed(commentAdded.trim());
                    GAUtils.event(FEED, COMMENT, ADD+COMMENT+BAR+CLICKED);
                }
            }
        });
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        ;
        recyclerView.setAdapter(feedOfferingCommentsAdapter);

        fetchFeedDetail();
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
            if (MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(getActivity(), getActivity().getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_POST_ID, String.valueOf(feedDetail.getPostId()));

                new HomeUtil().putDefaultParams(params);
                RestClient.getFeedApiService().fetchFeedDetails(params, new retrofit.Callback<FeedDetailResponse>() {
                    @Override
                    public void success(FeedDetailResponse feedbackResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        DialogPopup.dismissLoadingDialog();
                        try {
                            String message = feedbackResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, feedbackResponse.getFlag(), feedbackResponse.getError(), feedbackResponse.getMessage())) {
                                if (feedbackResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                    if(setFeedObjectAndRefresh(feedbackResponse)) {
                                        prepareListAndNotifyAdapter(feedbackResponse);
                                    }
                                    if((feedbackResponse.getFeedComments()==null || feedbackResponse.getFeedComments().size()==0) && openKeyboardOnLoad){

                                        activity.getHandler().postDelayed(new Runnable() {
                                          @Override
                                          public void run() {
                                              edtMyComment.requestFocus();
                                              Utils.showKeyboard(activity,edtMyComment);
                                          }
                                      },200);
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

                    }
                });
            } else {
                retryDialog(DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * returns true if data all set, else false if null
     * @param feedbackResponse
     * @return
     */
    private boolean setFeedObjectAndRefresh(FeedDetailResponse feedbackResponse) {
        if (feedbackResponse.getPostDetails() == null) {
            DialogPopup.alertPopupWithListener(activity, "", "We could not find this post right now", "Back",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.performBackPressed(false);
                        }
                    }, false);
            return false;
        }
        if (positionInOriginalList == -1) {
            feedDetail = feedbackResponse.getPostDetails();
        } else {
            feedDetail.setLikeCount(feedbackResponse.getPostDetails().getLikeCount());
            feedDetail.setCommentCount(feedbackResponse.getPostDetails().getCommentCount());
            feedDetail.setContent(feedbackResponse.getPostDetails().getContent());
            feedDetail.setPostEditable(feedbackResponse.getPostDetails().isPostEditable());
            feedDetail.setStarCount(feedbackResponse.getPostDetails().getStarCount());
            feedDetail.setRestaurantImage(feedbackResponse.getPostDetails().getRestaurantImage());
//            feedDetail.setOwnerImage(feedbackResponse.getPostDetails().getOwnerImage());
            feedDetail.setReviewImages(feedbackResponse.getPostDetails().getReviewImages());
        }


        if (activity.getFeedHomeFragment() != null) {


            //notifies the feed home fragment that user has commented on post so it can refresh accordingly i.e increase comment count
            activity.getFeedHomeFragment().refreshFeedInHomeFragment(positionInOriginalList);
        }
        return true;
    }

    private void prepareListAndNotifyAdapter(FeedDetailResponse feedbackResponse) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }

        dataList.clear();
        dataList.add(feedDetail);
//        dataList.add(FeedOfferingCommentsAdapter.TYPE_MY_COMMENT);

        if (feedbackResponse.getFeedComments() != null)
            dataList.addAll(feedbackResponse.getFeedComments());

        feedOfferingCommentsAdapter.setList(dataList);
    }

    private void commentOnFeed(final String comments) {
        try {
            if (MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(getActivity(), getActivity().getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_POST_ID, String.valueOf(feedDetail.getPostId()));
                params.put(Constants.KEY_COMMENT_CONTENT, String.valueOf(comments));

                new HomeUtil().putDefaultParams(params);
                RestClient.getFeedApiService().commentOnFeed(params, new retrofit.Callback<FeedDetailResponse>() {
                    @Override
                    public void success(FeedDetailResponse feedbackResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        DialogPopup.dismissLoadingDialog();
                        try {
                            String message = feedbackResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, feedbackResponse.getFlag(), feedbackResponse.getError(), feedbackResponse.getMessage())) {
                                if (feedbackResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                    Utils.hideKeyboard(getActivity());
                                    commentAdded = null;
                                    edtMyComment.setText(null);
                                    if(setFeedObjectAndRefresh(feedbackResponse)) {
                                        prepareListAndNotifyAdapter(feedbackResponse);
                                    }
                                    recyclerView.smoothScrollToPosition(feedOfferingCommentsAdapter.getItemCount() - 1);
                                } else {
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryCommentAPIDialog(DialogErrorType.SERVER_ERROR, comments);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        retryCommentAPIDialog(DialogErrorType.CONNECTION_LOST, comments);

                    }
                });
            } else {
                retryDialog(DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void retryCommentAPIDialog(DialogErrorType dialogErrorType, final String commentAdded) {
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

    private void retryDialog(DialogErrorType dialogErrorType) {
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
            commentAdded = s.toString();
//            textViewCharCount.setText(String.valueOf(500-s.toString().trim().length()));
            btnSubmit.setEnabled(s.toString().trim().length() > 0);


        }
    };

    public DeletePostDialog deletePostDialog;

    public DeletePostDialog getDeletePostDialog() {
        if (deletePostDialog == null)
            deletePostDialog = new DeletePostDialog(this, R.style.Feed_Popup_Theme, activity);

        return deletePostDialog;
    }

    @Override
    public void onDelete(FeedDetail feedDetail, int position) {
        if (deleteFeed == null)
            deleteFeed = new DeleteFeed(new DeleteFeed.DeleteApiCallback() {
                @Override
                public void onSuccess(int posInOriginalList) {
                    if (activity.getFeedHomeFragment() != null) {
                        //notifies the feed home fragment that user has deleted the post
                        activity.onBackPressed();
                        activity.getFeedHomeFragment().notifyOnDelete(positionInOriginalList);

                    }
                }
            });
        deleteFeed.delete(feedDetail.getPostId(), activity, positionInOriginalList);
    }

    @Override
    public void onEdit(FeedDetail feedDetail) {
        activity.openFeedAddPostFragment(feedDetail);
    }

    @Override
    public void onDismiss(FeedDetail feedDetail) {

    }

    private EditPostPopup editPostDialog;

    public EditPostPopup getEditPostDialog(){
//        if(editPostDialog==null)
            editPostDialog=new EditPostPopup(this,R.style.Feed_Popup_Theme,activity);

        return editPostDialog;
    }



    public void fetchDetailAPI() {
                fetchFeedDetail();
    }

    public void deleteCommentAPI(final long activityId, final int positionOfCommentInList,final long postId){

        try {
            if (MyApplication.getInstance().isOnline()) {

                DialogPopup.showLoadingDialog(getActivity(), getActivity().getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.KEY_ACTIVITY_ID, String.valueOf(activityId));
                params.put(Constants.KEY_POST_ID, String.valueOf(postId));
                new HomeUtil().putDefaultParams(params);

            RestClient.getFeedApiService().deleteComment(params, new retrofit.Callback<FeedCommonResponse>() {
                    @Override
                    public void success(FeedCommonResponse feedbackResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            String message = feedbackResponse.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, feedbackResponse.getFlag(), feedbackResponse.getError(), feedbackResponse.getMessage())) {
                                if (feedbackResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                    Utils.hideKeyboard(getActivity());
                                    feedDetail.setCommentCount(feedDetail.getCommentCount() - 1);
                                    dataList.remove(positionOfCommentInList);
                                    feedOfferingCommentsAdapter.notifyItemRemoved(positionOfCommentInList);
                                    feedOfferingCommentsAdapter.notifyItemChanged(0);
                                    if(dataList!=null && dataList.size()==2){
                                        feedOfferingCommentsAdapter.notifyItemChanged(1);//If only one comment is left remove horizontal line below it
                                    }
                                    if (activity.getFeedHomeFragment() != null) {
                                        //notifies the feed home fragment
                                        activity.getFeedHomeFragment().refreshFeedInHomeFragment(positionInOriginalList);

                                    }

                                } else {
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDeleteCommentAPIDialog(DialogErrorType.SERVER_ERROR, activityId,positionOfCommentInList,postId);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        retryDeleteCommentAPIDialog(DialogErrorType.CONNECTION_LOST, activityId,positionOfCommentInList,postId);

                    }
                });
            } else {
                retryDialog(DialogErrorType.NO_NET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void retryDeleteCommentAPIDialog(DialogErrorType serverError, final long activityId, final int positionOfCommentInList, final long postId) {
        DialogPopup.dialogNoInternet(activity,
                serverError,
                new Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        deleteCommentAPI(activityId,positionOfCommentInList,postId);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {

                    }
                });
    }


    @Override
    public void onMoreDelete(FeedDetail feedDetail, int positionInList) {
//        getDeletePostDialog().show(feedDetail,positionInList);
        showDialogPopupTwoButtonCapsule(activity.getString(R.string.delete_post_alert_message), feedDetail, positionInList);
    }

    @Override
    public void onMoreEdit(FeedDetail feedDetail, int positionInList) {
            onEdit(feedDetail);
    }

    public void showDialogPopupTwoButtonCapsule(String message, final FeedDetail feedDetail, final int pos){
        new DialogPopupTwoButtonCapsule(new DialogPopupTwoButtonCapsule.DialogCallback() {
            @Override
            public void onPositiveClick() {
                onDelete(feedDetail, pos);
            }

            @Override
            public void onNegativeClick() {
            }
        }, android.R.style.Theme_Translucent_NoTitleBar, activity, message).show();
    }

}
