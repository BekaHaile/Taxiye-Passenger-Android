package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.feeddetail.FeedComment;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.FeedData;


/**
 * Created by Shankar on 7/17/15.
 */
public class FeedOfferingCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemListener {

    private FreshActivity activity;
    private Callback callback;
    private List<Object> feedDetailData;
    private RecyclerView recyclerView;
    public static final int TYPE_POST_LAYOUT = 1;
    public static final int TYPE_USERS_COMMENTS = 2;
    public static final int TYPE_MY_COMMENT = 3;


    private static final StyleSpan BOLD_SPAN = new StyleSpan(Typeface.BOLD);
    private static final StyleSpan BOLD_SPAN_2 = new StyleSpan(Typeface.BOLD);//since we cant reuse same style span again in a spannable

    public FeedOfferingCommentsAdapter(Activity activity, List<Object> reviewImages, RecyclerView recyclerView, Callback callback) {
        this.activity = (FreshActivity) activity;
        this.feedDetailData = reviewImages;
        this.callback = callback;
        this.recyclerView = recyclerView;
    }


    public void setList(List<Object> reviewImages) {
        this.feedDetailData = reviewImages;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case TYPE_POST_LAYOUT:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_list, parent, false);
                return new FeedOfferingListAdapter.ViewHolderReviewImage(v,this);
            case TYPE_USERS_COMMENTS:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_comment, parent, false);
                return new UserCommentViewHolder(v);
            case TYPE_MY_COMMENT:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_my_comment, parent, false);
                return new MyCommentViewHolder(v);
            default:
                throw new IllegalArgumentException();

        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof FeedOfferingListAdapter.ViewHolderReviewImage)
        {

            FeedOfferingListAdapter.setData((FeedOfferingListAdapter.ViewHolderReviewImage)holder,(FeedDetail) feedDetailData.get(position),activity);

        }
        else if(holder instanceof MyCommentViewHolder){
            ((MyCommentViewHolder) holder).tvMyUserName.setText("Parminder Singh");
        }
        else if(holder instanceof UserCommentViewHolder){
            FeedComment feedComment = (FeedComment) feedDetailData.get(position);
            ((UserCommentViewHolder) holder).tvUserCommentName.setText(feedComment.getUserName());
            ((UserCommentViewHolder) holder).tvUserCommentDescription.setText(feedComment.getCommentContent());
            // TODO: 13/03/17  Pic missing

        }


    }


    @Override
    public int getItemCount() {
        return feedDetailData == null ? 0 : feedDetailData.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (feedDetailData.get(position) instanceof FeedDetail)
            return TYPE_POST_LAYOUT;
        else if (feedDetailData.get(position) instanceof FeedComment)
            return TYPE_USERS_COMMENTS;
        else return TYPE_MY_COMMENT;


    }


    @Override
    public void onClickItem(View viewClicked, View itemView) {

        int position = recyclerView.getChildLayoutPosition(itemView);

        switch (viewClicked.getId()) {
            case R.id.view_action_like:

                break;
            case R.id.view_action_comment:
                break;
            default:
                break;
        }
    }


    public interface Callback {
        void onLikeClick(Object object);

        void onCommentClick(Object object);
    }



     static class UserCommentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_profile_pic)
        ImageView ivUserCommentPic;
        @Bind(R.id.tv_user_name)
        TextView tvUserCommentName;
        @Bind(R.id.tv_comment_description)
        TextView tvUserCommentDescription;
        @Bind(R.id.tv_time_posted)
        TextView tvUserTimePosted;
        @Bind(R.id.line_top)
        View lineTop;

        UserCommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
/*
     static class ViewHolderReviewImage extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_owner_profile_pic)
        ImageView ivFeedOwnerPic;
        @Bind(R.id.tv_feed_owner_title)
        TextView tvFeedOwnerTitle;
        @Bind(R.id.tv_feed_description)
        TextView tvFeedDescription;
        @Bind(R.id.iv_place_image)
        ImageView ivPlaceImage;
        @Bind(R.id.tv_like_comment_status)
        TextView tvLikeCommentStatus;
        @Bind(R.id.tv_action_comment)
        TextView tvComment;
        @Bind(R.id.tv_action_like)
        TextView tvLike;
        @Bind(R.id.view_action_like)
        LinearLayout viewActionLike;
        @Bind(R.id.view_action_comment)
        LinearLayout viewActionComment;
        @Bind(R.id.tv_feed_rating)
        TextView tvFeedRating;
        @Bind(R.id.tv_restaurant_feed_address)
        TextView tvFeedAddress;
        @Bind(R.id.layout_user_activity_heading)
        RelativeLayout layoutUserActivity;
        @Bind(R.id.divider_user_activity)
        View dividerUserActivity;
        @Bind(R.id.iv_user_profile_pic)
        ImageView ivUserProfilePic;
        @Bind(R.id.tv_user_activity_time)
        TextView tvUserActivityTime;
        @Bind(R.id.tv_user_activity_title)
        TextView tvUserActivityTitle;
        @Bind(R.id.tv_owner_feed_time)
        TextView tvOwnerTime;

        ViewHolderReviewImage(final View view, final ItemListener onClickView) {
            super(view);
            ButterKnife.bind(this, view);
            viewActionLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickView.onClickItem(viewActionLike, view);
                }
            });
            viewActionComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickView.onClickItem(viewActionComment, view);
                }
            });


        }
    }*/


    static class MyCommentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_profile_pic)
        ImageView ivMyProfilePic;
        @Bind(R.id.tv_user_name)
        TextView tvMyUserName;

        MyCommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
