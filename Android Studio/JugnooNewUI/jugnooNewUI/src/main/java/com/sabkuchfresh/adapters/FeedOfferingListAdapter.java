package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.home.FreshActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;


/**
 * Created by Shankar on 7/17/15.
 */
public class FeedOfferingListAdapter extends RecyclerView.Adapter<FeedOfferingListAdapter.ViewHolderReviewImage> implements ItemListener {


    private FreshActivity activity;
    private Callback callback;
    private ArrayList<FeedData> reviewImages;
    private RecyclerView recyclerView;

    public FeedOfferingListAdapter(Activity activity, ArrayList<FeedData> reviewImages, RecyclerView recyclerView, Callback callback) {
        this.activity = (FreshActivity) activity;
        this.reviewImages = reviewImages;
        this.callback = callback;
        this.recyclerView = recyclerView;
    }


    public void setList(ArrayList<FeedData> reviewImages) {
        this.reviewImages = reviewImages;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderReviewImage onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_list, parent, false);
        return new ViewHolderReviewImage(v,this);
    }

    @Override
    public void onBindViewHolder(ViewHolderReviewImage holder, final int position) {
        holder.tvFeedHeading.setTypeface(holder.tvFeedHeading.getTypeface(), Typeface.BOLD);
        activity.setRatingAndGetColor(holder.tvFeedRating, 3.5,"#8dd061", true);
    }

    @Override
    public int getItemCount() {
        return reviewImages == null ? 25 : reviewImages.size();
    }

    @Override
    public void onClickItem(View viewClicked, View itemView) {

        int position  = recyclerView.getChildLayoutPosition(itemView);

        switch (viewClicked.getId()){
            case R.id.view_action_like:
                Log.i("TAG", "onClickItem: " + position);
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


    private static class FeedData {


        @SerializedName("comment_count")
        private long commentCount;
        private long likeCount;
        private String imageUrl;
        private String userImage;
        private String userName;
        private Double starCount;
        private FeedType feedType;
        private String restaurantName;
        private String content;

        public long getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(long commentCount) {
            this.commentCount = commentCount;
        }

        public long getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(long likeCount) {
            this.likeCount = likeCount;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getUserImage() {
            return userImage;
        }

        public void setUserImage(String userImage) {
            this.userImage = userImage;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Double getStarCount() {
            return starCount;
        }

        public void setStarCount(Double starCount) {
            this.starCount = starCount;
        }

        public FeedType getFeedType() {
            return feedType;
        }

        public void setFeedType(FeedType feedType) {
            this.feedType = feedType;
        }

        public String getRestaurantName() {
            return restaurantName;
        }

        public void setRestaurantName(String restaurantName) {
            this.restaurantName = restaurantName;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public enum FeedType{
            POST(1),
            REVIEW(2),
            LIKE_ON_POST(3),
            LIKE_ON_REVIEW (4),
            COMMENT_ON_POST(5),
            COMMENT_ON_REVIEW(6);

            private int value;
            FeedType(int value) {
                this.value=value;
            }
            public int getValue(){
                return value;
            }


        }





         /*   POST : 1,
            REVIEW : 2,
            LIKE_ON_POST : 3,
            LIKE_ON_REVIEW : 4,
            COMMENT_ON_POST : 5,
            COMMENT_ON_REVIEW : 6

         \"post_id\": 3,
                 \"city\": 1,
                 \"like_count\": 20,
                 \"comment_count\": 1,
                 \"content\": \"Hi,
        thisismythirdpost.\",
                \"image_url\": \"\",
                \"created_at\": \"2017-03-09T10: 48: 50.000Z\",
                \"star_count\": 0,
                \"feed_type\": 1,
                \"feed_type_string\": \"Post\",
                \"user_name\
*/
    }

    static class ViewHolderReviewImage extends RecyclerView.ViewHolder{
        @Bind(R.id.iv_profile_pic)
        ImageView ivProfilePic;
        @Bind(R.id.tv_feed_heading)
        TextView tvFeedHeading;
        @Bind(R.id.tv_feed_description)
        TextView tvFeedDescription;
        @Bind(R.id.iv_place_image)
        ImageView ivPlaceImage;
        @Bind(R.id.tv_like_comment_status)
        TextView tvLikeCommentStatus;
        @Bind(R.id.line_below_like_status)
        View lineBelowLikeStatus;
        @Bind(R.id.line_divider_like_comment)
        View lineDividerLikeComment;
        @Bind(R.id.line_bottom)
        View lineBottom;
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

        ViewHolderReviewImage(final View view, final ItemListener onClickView) {
            super(view);
            ButterKnife.bind(this, view);
            viewActionLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickView.onClickItem(viewActionLike,view);
                }
            });
            viewActionComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickView.onClickItem(viewActionComment,view);
                }
            });


        }
    }



    public static int getPositionOfChild(final View child, final int childParentId, final RecyclerView recyclerView) {

        if (child.getId() == childParentId) {
            return recyclerView.getChildAdapterPosition(child);
        }


        View parent = (View) child.getParent();
        while (parent.getId() != childParentId) {
            parent = (View) parent.getParent();
        }
        return recyclerView.getChildAdapterPosition(parent);
    }
}
