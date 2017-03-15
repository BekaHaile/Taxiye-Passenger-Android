package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.feeddetail.FeedComment;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;

import product.clicklabs.jugnoo.utils.Utils;



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
    private TextWatcher textWatcherMyComment;


    private static final StyleSpan BOLD_SPAN = new StyleSpan(Typeface.BOLD);
    private static final StyleSpan BOLD_SPAN_2 = new StyleSpan(Typeface.BOLD);//since we cant reuse same style span again in a spannable

    public FeedOfferingCommentsAdapter(Activity activity, List<Object> reviewImages, RecyclerView recyclerView, Callback callback, TextWatcher textWatcherMyComment) {
        this.activity = (FreshActivity) activity;
        this.feedDetailData = reviewImages;
        this.callback = callback;
        this.recyclerView = recyclerView;
        this.textWatcherMyComment = textWatcherMyComment;
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
            ((FeedOfferingListAdapter.ViewHolderReviewImage) holder).shadow.setVisibility(View.GONE);

        }
        else if(holder instanceof MyCommentViewHolder){
            if (Data.userData!=null) {
                ((MyCommentViewHolder) holder).tvMyUserName.setText(Data.userData.userName);
                if (!TextUtils.isEmpty(Data.userData.userImage))
                    Picasso.with(activity).load(Data.userData.userImage).resize(Utils.convertDpToPx(activity,25), Utils.convertDpToPx(activity,25)).centerCrop().transform(new CircleTransform()).into(((MyCommentViewHolder) holder).ivMyProfilePic);
                else
                    ((MyCommentViewHolder) holder).ivMyProfilePic.setImageResource(R.drawable.placeholder_img);
            }

            ((MyCommentViewHolder) holder).edtComment.setText(callback.getEditTextString());

        }
        else if(holder instanceof UserCommentViewHolder){
            FeedComment feedComment = (FeedComment) feedDetailData.get(position);
            UserCommentViewHolder userCommentViewHolder =     ((UserCommentViewHolder) holder);
            userCommentViewHolder.tvUserCommentName.setText(feedComment.getUserName());
            userCommentViewHolder.tvUserCommentDescription.setText(feedComment.getCommentContent());
            if(position==feedDetailData.size()-1)
                userCommentViewHolder.lineBottom.setVisibility(View.INVISIBLE);
            else
                userCommentViewHolder.lineBottom.setVisibility(View.VISIBLE);

            if(((FeedComment) feedDetailData.get(position)).getTimeCreated()==null)
                userCommentViewHolder.tvUserTimePosted.setVisibility(View.GONE);
            else {
                userCommentViewHolder.tvUserTimePosted.setVisibility(View.VISIBLE);
                userCommentViewHolder.tvUserTimePosted.setText(FeedOfferingListAdapter.getTimeToDisplay(((FeedComment) feedDetailData.get(position)).getTimeCreated(), activity.isTimeAutomatic));
            }

            if (!TextUtils.isEmpty(feedComment.getUserImage()))
                Picasso.with(activity).load(feedComment.getUserImage()).resize(Utils.convertDpToPx(activity,50), Utils.convertDpToPx(activity,50)).centerCrop().transform(new CircleTransform()).into(userCommentViewHolder.ivUserCommentPic);

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
                    callback.onLikeClick(position);
                break;
            case R.id.view_action_comment:
                break;
            default:
                break;
        }
    }

    public void notifyOnLike(int position,boolean isLikeAPI) {
        if(feedDetailData!=null && position<feedDetailData.size() && feedDetailData.get(position) instanceof FeedDetail)
        {
            FeedDetail feedDetail = (FeedDetail) feedDetailData.get(position);
            if(isLikeAPI)
                feedDetail.setLikeCount(feedDetail.getLikeCount()+1);
            else if(feedDetail.getLikeCount()!=0)
                feedDetail.setLikeCount(feedDetail.getLikeCount()-1);

            ((FeedDetail) feedDetailData.get(position)).setLiked(isLikeAPI);
            notifyItemChanged(position);
        }
    }


    public interface Callback {
        void onLikeClick(int positionOfLayout);

        void onCommentClick(Object object);

        String getEditTextString();
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
         @Bind(R.id.line_bottom)
         View lineBottom;

         UserCommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
             tvUserCommentName.setTypeface(tvUserCommentName.getTypeface(), Typeface.BOLD);
        }
    }

    public class MyCommentViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_profile_pic)
        ImageView ivMyProfilePic;
        @Bind(R.id.tv_user_name)
        TextView tvMyUserName;
        @Bind(R.id.edt_comment)
        EditText edtComment;


        MyCommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            edtComment.addTextChangedListener(textWatcherMyComment);
        }
    }
}
