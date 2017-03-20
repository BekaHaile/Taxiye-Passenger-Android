package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.dialogs.ReviewImagePagerDialog;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.DateParser;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundedCornersTransformation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Utils;

import static com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail.FeedType.REVIEW;


/**
 * Created by Shankar on 7/17/15.
 */
public class FeedHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemListener {


    private FreshActivity activity;
    private FeedPostCallback feedPostCallback;
    private List<FeedDetail> feedDetailArrayList;
    private RecyclerView recyclerView;
    private static final StyleSpan BOLD_SPAN = new StyleSpan(android.graphics.Typeface.BOLD);
    private static final StyleSpan BOLD_SPAN_2 = new StyleSpan(android.graphics.Typeface.BOLD);//since we cant reuse same style span again in a spannable
    private boolean showAddPostView;
    private boolean showChangeLocation = true;
    private String addPostText;
    private static final int LAYOUT_CHANGE_LOCATION = 99;
    private static final int LAYOUT_WHATS_ON_MIND = 100;
    private static final int LAYOUT_FEED = 101;
    private int headerViewHolderCount;

    public FeedHomeAdapter(Activity activity, List<FeedDetail> reviewImages, RecyclerView recyclerView, FeedPostCallback feedPostCallback, boolean showAddPostView) {
        this.activity = (FreshActivity) activity;
        this.feedDetailArrayList = reviewImages;
        this.feedPostCallback = feedPostCallback;
        this.recyclerView = recyclerView;
        this.showAddPostView = showAddPostView;

    }

    public void setList(List<FeedDetail> reviewImages, String addPostText, boolean showAddPostView) {
        this.feedDetailArrayList = reviewImages;
        if (!TextUtils.isEmpty(addPostText)) this.addPostText = addPostText;
        this.showAddPostView = showAddPostView;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        switch (viewType) {
            case LAYOUT_CHANGE_LOCATION:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_delivery_address_bar, parent, false);
                return new ChangeLocationViewHolder(v);
            case LAYOUT_WHATS_ON_MIND:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_list_add_new_post, parent, false);
                return new AddNewPostViewHolder(v);
            case LAYOUT_FEED:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_list, parent, false);
                return new ViewHolderReviewImage(v, this);
            default:
                throw new IllegalArgumentException();

        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof ViewHolderReviewImage) {


            FeedDetail feedDetail = feedDetailArrayList.get(position - headerViewHolderCount);
            setData((ViewHolderReviewImage) holder, feedDetail, activity, feedPostCallback);
            if ((position - headerViewHolderCount) == feedDetailArrayList.size()-1) {
                ((ViewHolderReviewImage) holder).shadow.setVisibility(View.GONE);
            } else {
                ((ViewHolderReviewImage) holder).shadow.setVisibility(View.VISIBLE);

            }

        } else if (holder instanceof AddNewPostViewHolder) {

            if (!TextUtils.isEmpty(addPostText))
                ((AddNewPostViewHolder) holder).tvAddPost.setText(addPostText);
            if (Data.userData!=null) {
                if (!TextUtils.isEmpty(Data.userData.userImage))
                    Picasso.with(activity).load(Data.userData.userImage).resize(Utils.convertDpToPx(activity,40), Utils.convertDpToPx(activity,40)).centerCrop().transform(new CircleTransform()).into(((AddNewPostViewHolder) holder).ivMyProfilePic);
                else
                    ((AddNewPostViewHolder) holder).ivMyProfilePic.setImageResource(R.drawable.placeholder_img);
            }

        }


    }

    public static void setData(ViewHolderReviewImage holder, final FeedDetail feedDetail, FreshActivity activity, final FeedPostCallback feedPostCallback) {
        String imageUrl = null, restaurantAddress = null, ownerImage = null, userImage = null;
        Spannable title = null, userActivityTitle = null;
        Double rating = null;
        boolean showUserActivity = false, setMovementMethod = false;
        if (feedDetail != null && feedDetail.getFeedType() != null) {


            switch (feedDetail.getFeedType()) {
                case COMMENT_ON_REVIEW:
                case LIKE_ON_REVIEW:
                case REVIEW:
                    if (feedDetail.getFeedType() != REVIEW) {
                        showUserActivity = true;
                        userActivityTitle = new SpannableString(feedDetail.getUserName() + feedDetail.getFeedType().getValue() + feedDetail.getOwnerName() + "'s review.");
                        userActivityTitle.setSpan(BOLD_SPAN, 0, feedDetail.getUserName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        userActivityTitle.setSpan(BOLD_SPAN_2, feedDetail.getUserName().length() + feedDetail.getFeedType().getValue().length(), userActivityTitle.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        if (!TextUtils.isEmpty(feedDetail.getUserImage()))
                            userImage = feedDetail.getUserImage();
                        holder.tvUserActivityTime.setText(getTimeToDisplay(feedDetail.getActivityDoneOn(), activity.isTimeAutomatic));

                    }


                    //Choose which picture to display
                    if (feedDetail.getReviewImages() != null && feedDetail.getReviewImages().size() > 0 && !TextUtils.isEmpty(feedDetail.getReviewImages().get(0).getUrl())) {
                        imageUrl = feedDetail.getReviewImages().get(0).getUrl();
                    } else if (!TextUtils.isEmpty(feedDetail.getRestaurantImage())) {
                        imageUrl = feedDetail.getRestaurantImage();
                        if (!TextUtils.isEmpty(feedDetail.getRestaurantAddress()))
                            restaurantAddress = feedDetail.getRestaurantAddress();
                    }

                    //Form Title
                    if (feedDetail.getStarCount() != null && feedDetail.getStarCount() > 0)
                        rating = feedDetail.getStarCount();


                    if (!TextUtils.isEmpty(feedDetail.getOwnerName())) {
                        String actualTitle = feedDetail.getOwnerName() + REVIEW.getValue() + feedDetail.getRestaurantName() + ".";
                        title = new SpannableString(actualTitle);
                        title.setSpan(new MyClickableSpan(feedDetail.getRestaurantId(), feedPostCallback), feedDetail.getOwnerName().length() + REVIEW.getValue().length(), actualTitle.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        title.setSpan(BOLD_SPAN, feedDetail.getOwnerName().length() + REVIEW.getValue().length(), actualTitle.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        title.setSpan(BOLD_SPAN_2, 0, feedDetail.getOwnerName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        setMovementMethod = true;

                    }

                    //Chooser UsersImage
                    if (!TextUtils.isEmpty(feedDetail.getOwnerImage()))
                        ownerImage = feedDetail.getOwnerImage();


                    break;
                case POST:
                case COMMENT_ON_POST:
                case LIKE_ON_POST:

                    if (feedDetail.getFeedType() != FeedDetail.FeedType.POST) {
                        showUserActivity = true;
                        userActivityTitle = new SpannableString(feedDetail.getUserName() + feedDetail.getFeedType().getValue() + feedDetail.getOwnerName() + "'s post.");
                        userActivityTitle.setSpan(BOLD_SPAN, 0, feedDetail.getUserName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        userActivityTitle.setSpan(BOLD_SPAN_2, feedDetail.getUserName().length() + feedDetail.getFeedType().getValue().length(), userActivityTitle.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        if (!TextUtils.isEmpty(feedDetail.getUserImage()))
                            userImage = feedDetail.getUserImage();
                        holder.tvUserActivityTime.setText(getTimeToDisplay(feedDetail.getActivityDoneOn(), activity.isTimeAutomatic));

                    }

                    //Choose which picture to display
                    if (feedDetail.getReviewImages() != null && feedDetail.getReviewImages().size() > 0 && !TextUtils.isEmpty(feedDetail.getReviewImages().get(0).getUrl()))
                        imageUrl = feedDetail.getReviewImages().get(0).getUrl();

                    //Form Title
                    if (!TextUtils.isEmpty(feedDetail.getOwnerName())) {
                        title = new SpannableString(feedDetail.getOwnerName());
                        title.setSpan(BOLD_SPAN, 0, feedDetail.getOwnerName().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }

                    //Chooser UsersImage
                    if (!TextUtils.isEmpty(feedDetail.getOwnerImage()))
                        ownerImage = feedDetail.getOwnerImage();

                    break;
                default:
                    break;


            }


        }

        //SetAddress
        holder.tvFeedAddress.setVisibility(restaurantAddress == null ? View.GONE : View.VISIBLE);
        holder.tvFeedAddress.setText(restaurantAddress);

        //SetImageUrl
        holder.ivPlaceImage.setVisibility(imageUrl == null ? View.GONE : View.VISIBLE);
        if (imageUrl != null)
            Picasso.with(activity).load(imageUrl).resize(Utils.convertDpToPx(activity, 310), Utils.convertDpToPx(activity, 110)).centerCrop().transform(new RoundedCornersTransformation(Utils.convertDpToPx(activity, 6), 0)).into(holder.ivPlaceImage);
        else
            holder.ivPlaceImage.setImageResource(R.drawable.placeholder_img);


        //Set Profile Pic
        if (ownerImage != null)
            Picasso.with(activity).load(ownerImage).resize(Utils.convertDpToPx(activity, 50), Utils.convertDpToPx(activity, 50)).centerCrop().transform(new CircleTransform()).into(holder.ivFeedOwnerPic);
        else
            holder.ivFeedOwnerPic.setImageResource(R.drawable.placeholder_img);

        //set Heading
        holder.tvFeedOwnerTitle.setText(title);
        holder.tvFeedOwnerTitle.setMovementMethod(setMovementMethod ? LinkMovementMethod.getInstance() : null);


        //Set Rating
        holder.tvFeedRating.setVisibility(rating == null ? View.GONE : View.VISIBLE);
        if (rating != null)
            activity.setRatingAndGetColor(holder.tvFeedRating, rating, feedDetail.getRatingColor(), true);

        //Set Likes and Comments
        holder.tvLikeCommentStatus.setText(formLikesComment(feedDetail.getLikeCount(), feedDetail.getCommentCount(), activity));

        //Set Content
        holder.tvFeedDescription.setText(feedDetail.getContent());

        //Show User Acitivty Layout such as Person A commented on Person B's post
        if (showUserActivity) {
            holder.layoutUserActivity.setVisibility(View.VISIBLE);
            holder.dividerUserActivity.setVisibility(View.VISIBLE);
            holder.tvUserActivityTitle.setText(userActivityTitle);
            if (userImage != null)
                Picasso.with(activity).load(userImage).resize(Utils.convertDpToPx(activity, 50), Utils.convertDpToPx(activity, 50)).centerCrop().transform(new CircleTransform()).into(holder.ivUserProfilePic);

        } else {
            holder.layoutUserActivity.setVisibility(View.GONE);
            holder.dividerUserActivity.setVisibility(View.GONE);

        }

        //show posted Time
        holder.tvOwnerTime.setText(getTimeToDisplay(feedDetail.getCreatedAt(), activity.isTimeAutomatic));


        //Set Like Icons and like text color
        Drawable drawableToSet = feedDetail.isLiked() ? ContextCompat.getDrawable(activity, R.drawable.ic_like_active) : ContextCompat.getDrawable(activity, R.drawable.ic_like);
        holder.tvLike.setCompoundDrawablesWithIntrinsicBounds(drawableToSet, null, null, null);
        if (feedDetail.isLiked())
            holder.tvLike.setTextColor(ContextCompat.getColor(activity, R.color.feed_color_like_active));
        else
            holder.tvLike.setTextColor(ContextCompat.getColor(activity, R.color.feed_grey_text));

        //Show user Images if greater than 1 in a recycler view
        if (feedDetail.getReviewImages() != null && feedDetail.getReviewImages().size() > 1) {
            holder.recyclerViewUserImages.setVisibility(View.VISIBLE);
            if (holder.displayFeedHomeImagesAdapter == null) {
                holder.displayFeedHomeImagesAdapter = new DisplayFeedHomeImagesAdapter(activity, feedDetail.getReviewImages(), holder.recyclerViewUserImages);
                holder.recyclerViewUserImages.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                holder.recyclerViewUserImages.setAdapter(holder.displayFeedHomeImagesAdapter);
            } else {
                holder.displayFeedHomeImagesAdapter.setList(feedDetail.getReviewImages());
            }
        } else {
            holder.recyclerViewUserImages.setVisibility(View.GONE);
        }


    }

    private static String formLikesComment(int likeCount, int commentCount, FreshActivity activity) {

        String likeSuffix = likeCount > 1 ? " Likes " : " Like ";
        String commentSuffix = commentCount > 1 ? " Comments " : " Comment ";
        return likeCount + likeSuffix + activity.getString(R.string.bullet) + " " + commentCount + commentSuffix;
    }


    public void notifyOnLike(int position, boolean isLiked) {

        if (feedDetailArrayList != null && position < feedDetailArrayList.size()) {

            FeedDetail feedDetail = feedDetailArrayList.get(position);
            if (isLiked)
                feedDetail.setLikeCount(feedDetail.getLikeCount() + 1);
            else if (feedDetail.getLikeCount() > 0)
                feedDetail.setLikeCount(feedDetail.getLikeCount() - 1);
            feedDetail.setLiked(isLiked);
            notifyFeedListItem(position);
        }
    }


    private static Calendar feedPostedCal = Calendar.getInstance();
    private static Calendar currentDateCal = Calendar.getInstance();

    public static String getTimeToDisplay(String createdAtTime, boolean isTimeAutomatic) {
        if (isTimeAutomatic) {
            feedPostedCal.setTime(DateParser.getUtcDateWithTimeZone(createdAtTime));
            currentDateCal.setTimeInMillis(System.currentTimeMillis());
            int yearPosted, yearCurrent, currentMonth, postedMonth, currentDate, postedDate, postedHour, currentHour, postedMin, currentMin;
            yearPosted = feedPostedCal.get(Calendar.YEAR);
            yearCurrent = currentDateCal.get(Calendar.YEAR);
            currentMonth = currentDateCal.get(Calendar.MONTH);
            postedMonth = feedPostedCal.get(Calendar.MONTH);
            currentDate = currentDateCal.get(Calendar.DATE);
            postedDate = feedPostedCal.get(Calendar.DATE);
        /*    postedHour=feedPostedCal.get(Calendar.HOUR_OF_DAY);
            currentHour=currentDateCal.get(Calendar.HOUR_OF_DAY);
            postedMin=feedPostedCal.get(Calendar.MINUTE);
            currentMin=currentDateCal.get(Calendar.MINUTE);*/


            int diff = yearCurrent - yearPosted;
            if (postedMonth > currentMonth || (postedMonth == currentMonth && postedDate > currentDate)) {
                diff--;
            }

            if (diff > 0) return diff + " years ago";

            if (yearPosted != yearCurrent) {
                diff = currentMonth + (11 - postedMonth);
                if (postedDate > currentDate) diff++;
            } else {
                diff = currentMonth - postedMonth;
                if (currentMonth != postedMonth && postedDate > currentDate)
                    diff++;

            }

            if (diff > 0) return diff + " months ago";


            long diffT = currentDateCal.getTimeInMillis() - feedPostedCal.getTimeInMillis();
            long diffC = diffT / (24 * 60 * 60 * 1000 * 7);
            if (diffC >= 1) return diffC + "w";
            diffC = diffT / (24 * 60 * 60 * 1000);
            if (diffC >= 1) return diffC + "d";
            diffC = diffT / (60 * 60 * 1000) % 24;
            if (diffC >= 1) return diffC + "h";
            diffC = diffT / (60 * 1000) % 60;
            if (diffC >= 1) return diffC + "m";
            diffC = diffT / 1000 % 60;
            if (diffC >= 0)
                return diffC + "s";
            else
                return 0 + "s";

        } else {
            return " " + DateParser.getLocalDateString(createdAtTime);
        }


    }

    @Override
    public int getItemCount() {
        headerViewHolderCount = 0;
        if (showChangeLocation) headerViewHolderCount++;
        if (showAddPostView) headerViewHolderCount++;


        return feedDetailArrayList == null ? headerViewHolderCount : feedDetailArrayList.size() + headerViewHolderCount;
    }


    @Override
    public void onClickItem(View viewClicked, View itemView) {

        int position = recyclerView.getChildLayoutPosition(itemView);
        if (position!=RecyclerView.NO_POSITION) {
            position = position - headerViewHolderCount;//position in feed ArrayList

            switch (viewClicked.getId()) {
                case R.id.view_action_like:
                    feedPostCallback.onLikeClick(feedDetailArrayList.get(position), position);
                    break;
                case R.id.view_action_comment:
                case R.id.root_layout_item:
                    feedPostCallback.onCommentClick(feedDetailArrayList.get(position), position);
                    break;

                case R.id.tv_feed_owner_title:

                    break;
                case R.id.iv_place_image:
                    final FeedDetail feedDetail = feedDetailArrayList.get(position);



                    if (feedDetail.getReviewImages() != null && feedDetail.getReviewImages().size() > 0){
                        //This means userimages are being displayed
                        ArrayList<FetchFeedbackResponse.ReviewImage>   reviewImages = feedDetail.getReviewImages();
                        showZoomedPagerDialog(0, reviewImages, activity);
                    }

                    else if (!TextUtils.isEmpty(feedDetail.getRestaurantImage())) {
                        //Open the restaurant in menus
                        feedPostCallback.onRestaurantClick(feedDetail.getRestaurantId());
                        //THis means only one image is being displayed which is restaurant Image

                    }


               //If null means no image is being showed Actually

                    break;

                default:
                    break;
            }
        }
    }

    /**
     * @param pos          position at which view should open when pager opens
     * @param reviewImages images List to be shown
     */
    public static void showZoomedPagerDialog(int pos, ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages, Activity activity) {
        ReviewImagePagerDialog dialog = ReviewImagePagerDialog.newInstance(pos, reviewImages);
        dialog.show(activity.getFragmentManager(), ReviewImagePagerDialog.class.getSimpleName());
    }


    public interface FeedPostCallback {
        void onLikeClick(FeedDetail object, int position);

        void onCommentClick(FeedDetail postId, int position);

        void onRestaurantClick(int restaurantId);

        String getEditTextString();//required Only For Comments Adapter
    }


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
        @Bind(R.id.vShadowDown)
        View shadow;
        @Bind(R.id.root_layout_item)
        RelativeLayout layoutItem;
        @Bind(R.id.recycler_user_images)
        RecyclerView recyclerViewUserImages;//feeddetail.getReviewImages field is greater than 1 than we  have to show this recycler view
        DisplayFeedHomeImagesAdapter displayFeedHomeImagesAdapter;

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

            tvFeedOwnerTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickView.onClickItem(tvFeedOwnerTitle, view);
                }

            });
            layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickView.onClickItem(layoutItem, view);

                }
            });
            ivPlaceImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickView.onClickItem(ivPlaceImage, view);
                }
            });

        }
    }

    private class ChangeLocationViewHolder extends RecyclerView.ViewHolder {

        public ChangeLocationViewHolder(View itemView) {
            super(itemView);
            activity.setDeliveryAddressView(itemView);
            activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FEED);
            if (activity.getDeliveryAddressView() != null) {
                activity.getDeliveryAddressView().scaleView();
                activity.getDeliveryAddressView().tvDeliveryAddress.setText(R.string.label_location_feed);
            }
        }
    }

    private class AddNewPostViewHolder extends RecyclerView.ViewHolder {

        public TextView tvAddPost;
        public ImageView ivMyProfilePic;

        public AddNewPostViewHolder(View itemView) {
            super(itemView);
            tvAddPost = (TextView) itemView.findViewById(R.id.tvAddPost);
            tvAddPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    activity.openFeedAddPostFragment();
                }
            });
            ivMyProfilePic=(ImageView)itemView.findViewById(R.id.iv_profile_pic);


        }
    }


    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            if (showChangeLocation)
                return LAYOUT_CHANGE_LOCATION;
            else if (showAddPostView)
                return LAYOUT_WHATS_ON_MIND;
            else
                return LAYOUT_FEED;
        }
        if (position == 1) {
            if (showChangeLocation && showAddPostView)
                return LAYOUT_WHATS_ON_MIND;
            else
                return LAYOUT_FEED;

        }

        return LAYOUT_FEED;

    }

    public int getPositionOfChild(final View child, final int childParentId, final RecyclerView recyclerView) {

        if (child.getId() == childParentId) {
            return recyclerView.getChildAdapterPosition(child);
        }


        View parent = (View) child.getParent();
        while (parent.getId() != childParentId) {
            parent = (View) parent.getParent();
        }
        return recyclerView.getChildAdapterPosition(parent);
    }

    private static class MyClickableSpan extends ClickableSpan {// extend ClickableSpan

        private int restaurantId;
        private FeedPostCallback feedPostCallback;

        private MyClickableSpan() {

        }

        MyClickableSpan(int restaurantId, FeedPostCallback feedPostCallback) {
            super();
            this.restaurantId = restaurantId;
            this.feedPostCallback = feedPostCallback;
        }


        public void onClick(View tv) {
            feedPostCallback.onRestaurantClick(restaurantId);
        }

        public void updateDrawState(TextPaint ds) {// override updateDrawState
            ds.setUnderlineText(false); // set to false to remove underline

        }
    }

    public void notifyFeedListItem(int postitioninFeedList) {
        notifyItemChanged(postitioninFeedList + headerViewHolderCount);
    }
}
