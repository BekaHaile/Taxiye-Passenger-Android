package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;
import com.squareup.picasso.RoundedCornersGlideTransform;

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
    private ArrayList<FeedDetail> feedDetailArrayList;
    private RecyclerView recyclerView;
    private static final StyleSpan BOLD_SPAN = new StyleSpan(android.graphics.Typeface.BOLD);

    public FeedOfferingListAdapter(Activity activity, ArrayList<FeedDetail> reviewImages, RecyclerView recyclerView, Callback callback) {
        this.activity = (FreshActivity) activity;
        this.feedDetailArrayList = reviewImages;
        this.callback = callback;
        this.recyclerView = recyclerView;
    }


    public void setList(ArrayList<FeedDetail> reviewImages) {
        this.feedDetailArrayList = reviewImages;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderReviewImage onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_list, parent, false);
        return new ViewHolderReviewImage(v, this);
    }

    @Override
    public void onBindViewHolder(ViewHolderReviewImage holder, int position) {


        FeedDetail feedDetail = feedDetailArrayList.get(position);
        String imageUrl = null;
        Spannable title = null;
        String restaurantAddress = null, userImage = null;
        Double rating = null;
        if (feedDetail != null && feedDetail.getFeedType() != null) {


            switch (feedDetail.getFeedType()) {
                case COMMENT_ON_POST:
                    break;
                case LIKE_ON_POST:
                    break;
                case COMMENT_ON_REVIEW:
                    break;
                case LIKE_ON_REVIEW:
                    break;
                case REVIEW:
                    //Choose which picture to display
                    if (!TextUtils.isEmpty(feedDetail.getImageUrl())) {
                        imageUrl = feedDetail.getImageUrl();
                    } else if (!TextUtils.isEmpty(feedDetail.getRestaurantImage())) {
                        imageUrl = feedDetail.getRestaurantImage();
                        if (!TextUtils.isEmpty(feedDetail.getRestaurantAddress()))
                            restaurantAddress = feedDetail.getRestaurantAddress();
                    }

                    //Form Title
                    if (feedDetail.getStarCount() != null && feedDetail.getStarCount() > 0)
                        rating = feedDetail.getStarCount();
                    String actualTitle = feedDetail.getUserName() + feedDetail.getFeedType().getValue() + feedDetail.getRestaurantName();
                    title = new SpannableString(actualTitle);
                    title.setSpan(BOLD_SPAN, 0, feedDetail.getUserName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title.setSpan(BOLD_SPAN, actualTitle.length() - feedDetail.getRestaurantName().length(), actualTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    //Chooser UsersImage
                    if (feedDetail.getUserImage() != null && !TextUtils.isEmpty(feedDetail.getImageUrl()))
                        userImage = feedDetail.getUserImage();

                    break;
                case POST:
                default:
                    //Choose which picture to display
                    if (feedDetail.getImageUrl() != null && !TextUtils.isEmpty(feedDetail.getImageUrl()))
                        imageUrl = feedDetail.getImageUrl();

                    //Form Title
                    title = new SpannableString(feedDetail.getUserName());


                    //Chooser UsersImage
                    if (feedDetail.getUserImage() != null && !TextUtils.isEmpty(feedDetail.getImageUrl()))
                        userImage = feedDetail.getUserImage();

                    break;


            }


        }


        //SetAddress
        holder.tvAddress.setVisibility(restaurantAddress == null ? View.GONE : View.VISIBLE);
        holder.tvAddress.setText(restaurantAddress);

        //SetImageUrl
        holder.ivPlaceImage.setVisibility(imageUrl == null ? View.GONE : View.VISIBLE);
        if (imageUrl != null)
            Glide.with(activity).load(imageUrl).bitmapTransform(new RoundedCornersGlideTransform(activity, 3, 0)).into(holder.ivPlaceImage);


        //Set Profile Pic
        if (userImage != null)
            Glide.with(activity).load(userImage).bitmapTransform(new RoundedCornersGlideTransform(activity, 25, 0)).into(holder.ivPlaceImage);

        //set Heading
        holder.tvFeedHeading.setText(title);

        //Set Rating
        holder.tvFeedRating.setVisibility(rating == null ? View.GONE : View.VISIBLE);
        if (rating != null)
            activity.setRatingAndGetColor(holder.tvFeedRating, rating, "#8dd061", true);

        //Set Likes and Comments
        holder.tvLikeCommentStatus.setText(formLikesComment(feedDetail.getLikeCount(), feedDetail.getCommentCount()));

        //Set Content
        holder.tvFeedDescription.setText(feedDetail.getContent());


    }

    private String formLikesComment(int likeCount, int commentCount) {

        return likeCount + " likes " + activity.getString(R.string.bullet) + commentCount + " comments";
    }

    @Override
    public int getItemCount() {
        return feedDetailArrayList == null ? 0 : feedDetailArrayList.size();
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


    static class ViewHolderReviewImage extends RecyclerView.ViewHolder {
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
        @Bind(R.id.tv_restaurant_feed_address)
        TextView tvAddress;

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
