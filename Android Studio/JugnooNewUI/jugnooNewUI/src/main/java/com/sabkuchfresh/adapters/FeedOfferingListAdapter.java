package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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
import com.sabkuchfresh.utils.DateParser;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundedCornersTransformation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Utils;

import static com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail.FeedType.REVIEW;


/**
 * Created by Shankar on 7/17/15.
 */
public class FeedOfferingListAdapter extends RecyclerView.Adapter<FeedOfferingListAdapter.ViewHolderReviewImage> implements ItemListener {


    private FreshActivity activity;
    private FeedPostCallback feedPostCallback;
    private List<FeedDetail> feedDetailArrayList;
    private RecyclerView recyclerView;
    private static final StyleSpan BOLD_SPAN = new StyleSpan(android.graphics.Typeface.BOLD);
    private static final StyleSpan BOLD_SPAN_2 = new StyleSpan(android.graphics.Typeface.BOLD);//since we cant reuse same style span again in a spannable

    public FeedOfferingListAdapter(Activity activity, List<FeedDetail> reviewImages, RecyclerView recyclerView, FeedPostCallback feedPostCallback) {
        this.activity = (FreshActivity) activity;
        this.feedDetailArrayList = reviewImages;
        this.feedPostCallback = feedPostCallback;
        this.recyclerView = recyclerView;
    }


    public void setList(List<FeedDetail> reviewImages) {
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
        setData(holder, feedDetail, activity, feedPostCallback);


    }

    public static void setData(ViewHolderReviewImage holder, final FeedDetail feedDetail, FreshActivity activity, final FeedPostCallback feedPostCallback) {
        String imageUrl = null, restaurantAddress = null, ownerImage = null, userImage = null;
        Spannable title = null, userActivityTitle = null;
        Double rating = null;
        boolean showUserActivity = false,setMovementMethod=false;
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
                    if (feedDetail.getReviewImages()!=null && feedDetail.getReviewImages().size()>0 && !TextUtils.isEmpty(feedDetail.getReviewImages().get(0).getUrl())) {
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
                        title.setSpan(new MyClickableSpan(feedDetail.getRestaurantId(),feedPostCallback),feedDetail.getOwnerName().length() + REVIEW.getValue().length(),actualTitle.length(),Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        title.setSpan(BOLD_SPAN, feedDetail.getOwnerName().length() + REVIEW.getValue().length(), actualTitle.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        title.setSpan(BOLD_SPAN_2, 0, feedDetail.getOwnerName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        setMovementMethod=true;

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
                    if (feedDetail.getReviewImages()!=null && feedDetail.getReviewImages().size()>0 && !TextUtils.isEmpty(feedDetail.getReviewImages().get(0).getUrl()))
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
        holder.tvFeedOwnerTitle.setMovementMethod(setMovementMethod? LinkMovementMethod.getInstance():null);



        //Set Rating
        holder.tvFeedRating.setVisibility(rating == null ? View.GONE : View.VISIBLE);
        if (rating != null)
            activity.setRatingAndGetColor(holder.tvFeedRating, rating, "#8dd061", true);

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
            holder.tvLike.setTextColor(ContextCompat.getColor(activity, R.color.feed_color_orange));
        else
            holder.tvLike.setTextColor(ContextCompat.getColor(activity, R.color.feed_grey_text));

        //Show user Images if greater than 1 in a recycler view
        if(feedDetail.getReviewImages()!=null && feedDetail.getReviewImages().size()>1)
        {
            holder.recyclerViewUserImages.setVisibility(View.VISIBLE);
            if(holder.editReviewImagesAdapter==null)
            {


//                holder.editReviewImagesAdapter = new EditReviewImagesAdapter(activity,feedDetail.getReviewImages(),holder.recyclerViewUserImages);
            }
        }
        else{
            holder.recyclerViewUserImages.setVisibility(View.GONE);
        }




    }

    private static String formLikesComment(int likeCount, int commentCount, FreshActivity activity) {

        String likeSuffix = likeCount > 1 ? " Likes " : " Like ";
        String commentSuffix = commentCount > 1 ? " Comments " : " Comment ";
        return likeCount + likeSuffix + activity.getString(R.string.bullet) + " " + commentCount + commentSuffix;
    }

    private String calculateTimePosted(String createdOnDate) {
        String timeValue;
        long time = DateParser.getUtcDateWithTimeZone(createdOnDate).getTime();
        long timeSince = System.currentTimeMillis() - time;
        timeSince = timeSince / 1000;
        if (timeSince / 60 < 1)
            timeValue = timeSince + "s";
        else if (timeSince / 3600 < 1)
            timeValue = timeSince / 60 + "m";
        else if (timeSince / 86400 < 1)
            timeValue = timeSince / 3600 + "h";
        else if (timeSince / 604800 < 1)
            timeValue = timeSince / 86400 + "d";
        else if (timeSince / 2419200 < 1)
            timeValue = timeSince / 604800 + "w";
        else timeValue = "must be years";
        return timeValue;


    }


    public void notifyOnLike(int position, boolean isLiked) {

        if (feedDetailArrayList != null && position < feedDetailArrayList.size()) {

            FeedDetail feedDetail = feedDetailArrayList.get(position);
            if (isLiked)
                feedDetail.setLikeCount(feedDetail.getLikeCount() + 1);
            else if (feedDetail.getLikeCount() > 0)
                feedDetail.setLikeCount(feedDetail.getLikeCount() - 1);
            feedDetail.setLiked(isLiked);
            notifyItemChanged(position);
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


     /*       int noOfDaysInPostedMonth=feedPostedCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            if(postedMonth!=currentMonth){
                if((noOfDaysInPostedMonth-postedDate)+currentDate>=7)
                    diff=(noOfDaysInPostedMonth-postedDate)/7;
                else
                    diff=-1;


            }


            else{
                if(currentDate-postedDate>=7)
                    diff=(currentDate-postedDate)/7;
                else
                    diff=-1;
            }
            if(diff>0) return diff+"w";
*/










/*
*//*        diff= currentDateCal.get(Calendar.WEEK_OF_MONTH)- feedPostedCal.get(Calendar.WEEK_OF_MONTH);
        if(diff>0) return diff+"w";*//*


            if(postedMonth!=currentMonth) {
                diff =  currentDate + (noOfDaysInPostedMonth- postedDate);
            }
            else{
                diff=currentDate-postedDate;

            }
            if(currentDateCal.get(Calendar.HOUR_OF_DAY)<feedPostedCal.get(Calendar.HOUR_OF_DAY))
                diff--;
            if(diff>0)return diff+"d";

            if(postedDate!=currentDate){
                diff = (24- postedHour) + currentHour;
            }
            else{
                diff=currentHour-postedHour;
            }

            if(currentDateCal.get(Calendar.MINUTE)<feedPostedCal.get(Calendar.MINUTE))
                diff--;
            if(diff>0)return diff+"h";

            if(postedHour!=currentHour){
                diff= (60-feedPostedCal.get(Calendar.MINUTE))+currentDateCal.get(Calendar.MINUTE);
            }
            else{
                diff = currentDateCal.get(Calendar.MINUTE) - feedPostedCal.get(Calendar.MINUTE);
            }

            if(currentDateCal.get(Calendar.SECOND)<feedPostedCal.get(Calendar.SECOND))
                diff--;


            if(diff>0)return diff+"m";

            if(postedMin!=currentMin)
                diff= (60-feedPostedCal.get(Calendar.SECOND))+currentDateCal.get(Calendar.SECOND);
            else
                diff=currentDateCal.get(Calendar.SECOND)- feedPostedCal.get(Calendar.SECOND);
            return diff<0?0+"s":diff+"s";*/

        } else {
            return " " + DateParser.getLocalDateString(createdAtTime);
        }


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
                feedPostCallback.onLikeClick(feedDetailArrayList.get(position), position);
                break;
            case R.id.view_action_comment:
            case R.id.root_layout_item:
                feedPostCallback.onCommentClick(feedDetailArrayList.get(position), position);
                break;

            case R.id.tv_feed_owner_title:

                break;
            case R.id.iv_place_image:


                String imageUrl=null;
             /*   if(!TextUtils.isEmpty(feedDetailArrayList.get(position).getReviewImages()))
                        imageUrl=feedDetailArrayList.get(position).getReviewImages();
                else if(!TextUtils.isEmpty(feedDetailArrayList.get(position).getRestaurantImage()))
                    imageUrl=feedDetailArrayList.get(position).getRestaurantImage();*/

                if (imageUrl!=null) {
                    final FetchFeedbackResponse.ReviewImage reviewImage = new FetchFeedbackResponse.ReviewImage();
                    reviewImage.setUrl(imageUrl);
                    ReviewImagePagerDialog dialog = ReviewImagePagerDialog.newInstance(0, new ArrayList<FetchFeedbackResponse.ReviewImage>(){{add(reviewImage);}});
                    dialog.show(activity.getFragmentManager(), ReviewImagePagerDialog.class.getSimpleName());
                }
                break;

            default:
                break;
        }
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
        EditReviewImagesAdapter editReviewImagesAdapter;

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
                    onClickView.onClickItem(ivPlaceImage,view);
                }
            });

        }
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

   public static class MyClickableSpan extends ClickableSpan {// extend ClickableSpan

       private int restaurantId;
       private FeedPostCallback feedPostCallback;

       private MyClickableSpan(){

       }

       public MyClickableSpan(int restaurantId, FeedPostCallback feedPostCallback) {
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
}
