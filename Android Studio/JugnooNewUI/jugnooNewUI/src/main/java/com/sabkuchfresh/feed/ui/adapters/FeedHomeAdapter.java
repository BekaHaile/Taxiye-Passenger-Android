package com.sabkuchfresh.feed.ui.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.sabkuchfresh.adapters.ItemListener;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.dialogs.ReviewImagePagerDialog;
import com.sabkuchfresh.feed.utils.FeedUtils;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.feeddetail.FeedComment;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.CustomTypeFaceSpan;
import com.sabkuchfresh.utils.DateParser;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Utils;

import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;


/**
 * Created by Shankar on 7/17/15.
 */
public class FeedHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemListener, DisplayFeedHomeImagesAdapter.Callback, GAAction {


    private FreshActivity activity;
    private FeedPostCallback feedPostCallback;
    private List<?> adapterList;
    private RecyclerView recyclerView;
    private static final StyleSpan BOLD_SPAN = new StyleSpan(android.graphics.Typeface.BOLD);
    private static final StyleSpan BOLD_SPAN_2 = new StyleSpan(android.graphics.Typeface.BOLD);//since we cant reuse same style span again in a spannable
    private static final int ITEM_LOCATION_SELECTED = 99;
    private static final int ITEM_ADD_POST = 100;
    private static final int ITEM_FEED = 101;
    public static final int ITEM_FOOTER_BLANK = 122;
    private static Typeface FONT_STAR;




    public FeedHomeAdapter(Activity activity, List<?> reviewImages, RecyclerView recyclerView, FeedPostCallback feedPostCallback) {
        this.activity = (FreshActivity) activity;
        this.adapterList = reviewImages;
        this.feedPostCallback = feedPostCallback;
        this.recyclerView = recyclerView;
        FONT_STAR = Typeface.createFromAsset(activity.getAssets(), "fonts/font_feed_star.ttf");

    }

    public void setList(List<?> reviewImages) {
        this.adapterList = reviewImages;
        notifyDataSetChanged();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        switch (viewType) {
            case ITEM_LOCATION_SELECTED:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_delivery_address_bar, parent, false);
                return new ChangeLocationViewHolder(v);
            case ITEM_ADD_POST:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_list_add_new_post, parent, false);
                return new AddNewPostViewHolder(v);
            case ITEM_FEED:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_list, parent, false);
                return new ViewHolderReviewImage(v, this);
            case ITEM_FOOTER_BLANK:
                 v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer, parent, false);
                 RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, Utils.convertDpToPx(activity,180));
                 v.setLayoutParams(layoutParams);
                 return new ViewBlankHolder(v);
            default:
                throw new IllegalArgumentException();

        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof ViewHolderReviewImage) {


            FeedDetail feedDetail = (FeedDetail) adapterList.get(position);
            setData((ViewHolderReviewImage) holder, feedDetail, activity, feedPostCallback, this, false);
            if (position == adapterList.size() - 2) {
                ((ViewHolderReviewImage) holder).shadow.setVisibility(View.GONE);
            } else {
                ((ViewHolderReviewImage) holder).shadow.setVisibility(View.VISIBLE);

            }

        } else if (holder instanceof AddNewPostViewHolder) {

            AddPostData addPostData = (AddPostData) adapterList.get(position);
            if(addPostData.getAddPostText()!=null)
            {
                ((AddNewPostViewHolder) holder).tvAddPost.setText(addPostData.getAddPostText());
            }
            if(addPostData.getImageUrl()!=null){
             Picasso.with(activity).load(addPostData.getImageUrl()).resize(Utils.convertDpToPx(activity, 40), Utils.convertDpToPx(activity, 40)).centerCrop().transform(new CircleTransform()).into(((AddNewPostViewHolder) holder).ivMyProfilePic);

            }


        }
        else if(holder instanceof ChangeLocationViewHolder){

            SelectedLocation selectedLocation = (SelectedLocation) adapterList.get(position);
            ((ChangeLocationViewHolder) holder).textViewLocation.setText(selectedLocation.getCityName());
            ((ChangeLocationViewHolder) holder).tvLabel.setText(selectedLocation.isCity()?"City":"Location");

        }


    }

    public static void setData(ViewHolderReviewImage holder, final FeedDetail feedDetail, FreshActivity activity, final FeedPostCallback feedPostCallback, final DisplayFeedHomeImagesAdapter.Callback callback, boolean isViewingDetail) {
        String imageUrl = null, restaurantAddress = null, ownerImage = null, userImage = null;
        Spannable title = null, userActivityTitle = null;
        Double rating = null;
        boolean showUserActivity = false, setMovementMethod = false;
        if (feedDetail != null && feedDetail.getFeedType() != null) {


            switch (feedDetail.getFeedType()) {
                case COMMENT_ON_REVIEW:
                case LIKE_ON_REVIEW:
                case REVIEW:
                    //Form Title
                    if (feedDetail.getStarCount() != null && feedDetail.getStarCount() > 0)
                        rating = feedDetail.getStarCount();






                    if (feedDetail.getFeedType() != FeedDetail.FeedType.REVIEW && !TextUtils.isEmpty(feedDetail.getUserName()) &&!TextUtils.isEmpty(feedDetail.getOwnerName())) {
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


                    if (!TextUtils.isEmpty(feedDetail.getOwnerName()) && !TextUtils.isEmpty(feedDetail.getRestaurantName())) {
                        String actualTitle = feedDetail.getOwnerName() + FeedDetail.FeedType.REVIEW.getValue() + feedDetail.getRestaurantName() + ".";

                        if(rating!=null){
                            String  titleWithReview = actualTitle + " ";

                            int starCount = 0;
                            while(starCount!=5){
                                if(starCount<rating.intValue()){
                                    titleWithReview+= activity.getString(R.string.feed_star_selected);
                                } else{
                                    titleWithReview+= activity.getString(R.string.feed_star_deselected);
                                }

                                starCount++;
                            }


                            title = new SpannableString(titleWithReview);
                            title.setSpan(new MyClickableSpan(feedDetail.getRestaurantId(), feedPostCallback), feedDetail.getOwnerName().length() + FeedDetail.FeedType.REVIEW.getValue().length(), actualTitle.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            title.setSpan(BOLD_SPAN, feedDetail.getOwnerName().length() + FeedDetail.FeedType.REVIEW.getValue().length(), actualTitle.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            title.setSpan(BOLD_SPAN_2, 0, feedDetail.getOwnerName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);


                            title.setSpan(new CustomTypeFaceSpan("", FONT_STAR), actualTitle.length(), title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            int ratingStartLength = actualTitle.length()+1;//adding 1 for " " i.e space
                            title.setSpan(new ForegroundColorSpan(Color.parseColor(feedDetail.getRatingColor())), ratingStartLength, actualTitle.length() + 1 +  rating.intValue(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            title.setSpan(new ForegroundColorSpan(Color.GRAY), ratingStartLength + rating.intValue(), title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            title.setSpan(new RelativeSizeSpan(0.8f), actualTitle.length(), title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            setMovementMethod = true;
                        }else{
                            title = new SpannableString(actualTitle);
                            title.setSpan(new MyClickableSpan(feedDetail.getRestaurantId(), feedPostCallback), feedDetail.getOwnerName().length() + FeedDetail.FeedType.REVIEW.getValue().length(), actualTitle.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            title.setSpan(BOLD_SPAN, feedDetail.getOwnerName().length() + FeedDetail.FeedType.REVIEW.getValue().length(), actualTitle.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            title.setSpan(BOLD_SPAN_2, 0, feedDetail.getOwnerName().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            setMovementMethod = true;
                        }



                    }

                    //Chooser UsersImage
                    if (!TextUtils.isEmpty(feedDetail.getOwnerImage()))
                        ownerImage = feedDetail.getOwnerImage();


                    break;
                case POST:
                case COMMENT_ON_POST:
                case LIKE_ON_POST:

                    if (feedDetail.getFeedType() != FeedDetail.FeedType.POST && !TextUtils.isEmpty(feedDetail.getUserName()) &&!TextUtils.isEmpty(feedDetail.getOwnerName())) {
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
                        title.setSpan(BOLD_SPAN, 0, feedDetail.getOwnerName().length(), SPAN_INCLUSIVE_EXCLUSIVE);
                    }

                    //Chooser UsersImage
                    if (!TextUtils.isEmpty(feedDetail.getOwnerImage()))
                        ownerImage = feedDetail.getOwnerImage();

                    break;
                default:
                    break;


            }

         //SetAddress
            holder.tvFeedAddress.setVisibility(restaurantAddress == null ? View.GONE : View.VISIBLE);
            holder.shadowAddress.setVisibility(restaurantAddress == null ? View.GONE : View.VISIBLE);
            holder.tvFeedAddress.setText(restaurantAddress);


            if (!TextUtils.isEmpty(ownerImage) && !Constants.DEFAULT_IMAGE_URL.equalsIgnoreCase(ownerImage)) {
                Picasso.with(activity).load(ownerImage).placeholder(R.drawable.placeholder_img).transform(new CircleTransform()).into(holder.ivFeedOwnerPic);

            } else if(!TextUtils.isEmpty(feedDetail.getOwnerName())) {

                if (feedDetail.getOwnerImageDrawable() == null) {
                    String firstLetter =  feedDetail.getOwnerName().toUpperCase().substring(0,1);
                    TextDrawable drawable = TextDrawable.builder()
                            .beginConfig().bold().endConfig()
                            .buildRound(firstLetter, activity.getParsedColor(feedDetail.getColor()));
                    feedDetail.setOwnerImageDrawable(drawable);
                }

                holder.ivFeedOwnerPic.setImageDrawable(feedDetail.getOwnerImageDrawable());
            }
            else{
                holder.ivFeedOwnerPic.setImageDrawable(feedDetail.getOwnerImageDrawable());
            }

            //set Heading
            holder.tvFeedOwnerTitle.setText(title);
            holder.tvFeedOwnerTitle.setMovementMethod(setMovementMethod ? LinkMovementMethod.getInstance() : null);



        /*    //Set Rating
            if (rating != null) {

                holder.tvFeedRating.setVisibility(View.VISIBLE)
                activity.setRatingAndGetColor(holder.tvFeedRating, rating, feedDetail.getRatingColor(), true);
            }
            else{
                holder.tvFeedRating.setVisibility(View.GONE)
            }*/


            //set Like Count and comment count
            String likeSuffix = feedDetail.getLikeCount() > 1 ? " Likes" : " Like";
            SpannableString likeText;
            if(feedDetail.getLikeCount()>0){
                //bold like Count
                likeText = new SpannableString(String.valueOf(feedDetail.getLikeCount()) + likeSuffix);
                likeText.setSpan(BOLD_SPAN,0,String.valueOf(feedDetail.getLikeCount()).length(),SPAN_INCLUSIVE_EXCLUSIVE);
            }
            else{
                likeText = new SpannableString(likeSuffix);
            }
            holder.tvLike.setText(likeText);

            String commentSuffix = feedDetail.getCommentCount() > 1 ? " Replies" : " Reply";
            SpannableString commentText;
            if(feedDetail.getCommentCount()>0){
                //bold comment Count
                commentText = new SpannableString(String.valueOf(feedDetail.getCommentCount()) + commentSuffix);
                commentText.setSpan(BOLD_SPAN,0,String.valueOf(feedDetail.getCommentCount()).length(),SPAN_INCLUSIVE_EXCLUSIVE);
            }
            else{
                commentText = new SpannableString(commentSuffix);
            }

            holder.tvComment.setText(commentText);
            holder.tvComment.setCompoundDrawablesWithIntrinsicBounds(feedDetail.getIsCommentedByUser() == 1
                    ? R.drawable.ic_comment_active : R.drawable.ic_comment, 0, 0, 0);





            //Set Content
            holder.tvFeedDescription.setText(feedDetail.getContent());

            //Show User Activity Layout such as Person A commented on Person B's post
            if (showUserActivity && !isViewingDetail) {
                holder.layoutUserActivity.setVisibility(View.VISIBLE);

                holder.tvUserActivityTitle.setText(userActivityTitle);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.layoutActualPost.getLayoutParams();
                layoutParams.setMargins(FeedUtils.dpToPx(12),0,FeedUtils.dpToPx(20),FeedUtils.dpToPx(12));
                holder.layoutActualPost.setPadding(0,FeedUtils.dpToPx(20),0,0);
                holder.layoutActualPost.setLayoutParams(layoutParams);
           /* if (userImage != null)
                Picasso.with(activity).load(userImage).resize(FeedUtils.convertDpToPx(activity, 50), FeedUtils.convertDpToPx(activity, 50)).centerCrop().transform(new CircleTransform()).into(holder.ivUserProfilePic);*/

            } else {
                holder.layoutUserActivity.setVisibility(View.GONE);


                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.layoutActualPost.getLayoutParams();
                layoutParams.setMargins(0,0,0,0);
                holder.layoutActualPost.setPadding(0,FeedUtils.dpToPx(20),0,0);
                holder.layoutActualPost.setLayoutParams(layoutParams);
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



            // for adding restaurant image in reviewImages list if no images added
            ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages;
            if((feedDetail.getReviewImages() == null || feedDetail.getReviewImages().size() == 0)
                    && !TextUtils.isEmpty(imageUrl)){
                reviewImages = new ArrayList<>();
                FetchFeedbackResponse.ReviewImage reviewImage = new FetchFeedbackResponse.ReviewImage(imageUrl, imageUrl);
                reviewImage.setIsRestaurantImage(true);
                reviewImages.add(reviewImage);
            } else {
                reviewImages = feedDetail.getReviewImages();
            }

            //Show user Images if greater than 1 in a recycler view

            RelativeLayout.LayoutParams vpParams = (RelativeLayout.LayoutParams) holder.vpReviewImages.getLayoutParams();
            if (reviewImages != null && reviewImages.size() > 0) {
                holder.vpReviewImages.setVisibility(View.VISIBLE);
                if (holder.displayFeedHomeImagesAdapter == null) {
                    holder.displayFeedHomeImagesAdapter = new DisplayFeedHomeImagesAdapter(activity, reviewImages,
                            feedDetail.getRestaurantId(),
                            callback);
                } else {
                    holder.displayFeedHomeImagesAdapter.setList(reviewImages, feedDetail.getRestaurantId());
                }
                holder.vpReviewImages.setAdapter(holder.displayFeedHomeImagesAdapter);
                for (int i = 0; i < holder.tabDots.getTabCount(); i++) {
                    View tab = ((ViewGroup) holder.tabDots.getChildAt(0)).getChildAt(i);
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                    p.setMargins(activity.getResources().getDimensionPixelSize(R.dimen.dp_4), 0, 0, 0);
                    tab.requestLayout();
                }
                holder.tabDots.setVisibility(reviewImages.size() > 1 ? View.VISIBLE : View.GONE);
                holder.lineBelowImagesPager.setVisibility(View.INVISIBLE);

                vpParams.bottomMargin=0;

            } else {
                holder.lineBelowImagesPager.setVisibility(View.VISIBLE);
                holder.vpReviewImages.setVisibility(View.GONE);
                holder.tabDots.setVisibility(View.GONE);
                vpParams.bottomMargin=FeedUtils.dpToPx(10);
            }
            holder.vpReviewImages.setLayoutParams(vpParams);


            //Show Edit post option if available
            holder.tvMore.setVisibility(feedDetail.isPostEditable()?View.VISIBLE:View.GONE);




        if(!isViewingDetail && (feedDetail.getFeedType()== FeedDetail.FeedType.COMMENT_ON_POST || feedDetail.getFeedType()== FeedDetail.FeedType.COMMENT_ON_REVIEW ) && feedDetail.getCommentContent()!=null ){
       /*

        //Show Comment if X Comment on Y Post Done with only one textVIew
            String userCommentName = feedDetail.getUserName() + "\n";
            String newline = "\n"  ;//margin between name and comment
            String keyComment =  feedDetail.getCommentContent();



            SpannableString spannableString = new SpannableString(userCommentName + newline+  keyComment );
            spannableString.setSpan(BOLD_SPAN,0,userCommentName.length(),SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity,R.color.feed_grey_text_heading)),0,userCommentName.length(),SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(0.9f),spannableString.length()-keyComment.length(),spannableString.length(),SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(0.2f),userCommentName.length(),spannableString.length()-keyComment.length(),SPAN_INCLUSIVE_EXCLUSIVE);//margin between name and comment reducing size
            holder.tvUserCommentedNameAndComment.setText(spannableString);
*/

            holder.tvUserCommentedName.setText(feedDetail.getUserName());
            holder.tvUserCommentedComment.setText(feedDetail.getCommentContent() );


            //User Image Comment
            if (!TextUtils.isEmpty(userImage) && !Constants.DEFAULT_IMAGE_URL.equalsIgnoreCase(userImage)) {
                Picasso.with(activity).load(userImage).placeholder(R.drawable.placeholder_img).transform(new CircleTransform()).into(holder.ivUserCommentedPic);

            } else if(!TextUtils.isEmpty(feedDetail.getUserName())) {

                if (feedDetail.getUserImageDrawable() == null) {
                    String firstLetter =  feedDetail.getUserName().toUpperCase().substring(0,1);
                    TextDrawable drawable = TextDrawable.builder()
                            .beginConfig().bold().endConfig()
                            .buildRound(firstLetter, activity.getParsedColor(feedDetail.getUserImageColor()));
                    feedDetail.setUserImageDrawable(drawable);
                }

                holder.ivUserCommentedPic.setImageDrawable(feedDetail.getUserImageDrawable());
            }else{
                holder.ivUserCommentedPic.setImageDrawable(feedDetail.getUserImageDrawable());
            }

            holder.layoutComment.setVisibility(View.VISIBLE);
        }else{
            holder.layoutComment.setVisibility(View.GONE);

        }


        }



    }

    @Override
    public void onRestaurantImageClick(Integer restaurantId) {
        feedPostCallback.onRestaurantClick(restaurantId);
    }

    private static String formLikesComment(int likeCount, int commentCount, FreshActivity activity) {
        if(likeCount==0 && commentCount==0)
            return null;


        String likeSuffix = likeCount > 1 ? " Likes " : " Like ";
        likeSuffix=likeCount>0? likeCount+likeSuffix:"";
        String commentSuffix = commentCount > 1 ? " Replies " : " Reply ";
        commentSuffix=commentCount>0?commentCount+commentSuffix:"";
        String divider = likeCount!=0 && commentCount!=0 ?activity.getString(R.string.bullet) + " ":"";

        return  likeSuffix + divider +commentSuffix;
    }


    public void notifyOnLike(int position, boolean isLiked) {

        if (adapterList != null && position < adapterList.size()) {

            FeedDetail feedDetail = (FeedDetail) adapterList.get(position);
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

            int diff = yearCurrent - yearPosted;
            if (postedMonth > currentMonth || (postedMonth == currentMonth && postedDate > currentDate)) {
                diff--;
            }

            if (diff > 0){
                if(diff==1)
                    return diff + " year ago";
                else
                    return diff + " years ago";
            }

            if (yearPosted != yearCurrent) {
                diff = currentMonth + (11 - postedMonth);
                if (postedDate > currentDate) diff--;
            } else {

                diff = currentMonth - postedMonth;
                if (currentMonth != postedMonth && postedDate > currentDate)
                    diff--;

            }

            if (diff > 0) {
                if(diff==1)
                    return diff + " month ago";
                else
                    return diff + " months ago";
            }


            long diffT = currentDateCal.getTimeInMillis() - feedPostedCal.getTimeInMillis();
            long diffC = diffT / (24 * 60 * 60 * 1000 * 7);
            if (diffC >= 1) return diffC + (diffC == 1?" week ago":" weeks ago");
            diffC = diffT / (24 * 60 * 60 * 1000);
            if (diffC >= 1) return diffC + (diffC == 1?" day ago":" days ago");
            diffC = diffT / (60 * 60 * 1000) % 24;
            if (diffC >= 1) return diffC + (diffC == 1?" hour ago":" hours ago");
            diffC = diffT / (60 * 1000) % 60;
            if (diffC >= 1) return diffC + (diffC == 1?" minute ago":" minutes ago");
            diffC = diffT / 1000 % 60;

            return " Just now";
           /* if (diffC >= 0)
                return diffC + "s";
            else
                return 0 + "s";*/

        } else {
            return " " + DateParser.getLocalDateString(createdAtTime);
        }


    }

    @Override
    public int getItemCount() {


        return adapterList == null ? 0 : adapterList.size();
    }


    @Override
    public void onClickItem(View viewClicked, View itemView) {

        int position = recyclerView.getChildLayoutPosition(itemView);
        if (position != RecyclerView.NO_POSITION && adapterList.get(position) instanceof FeedDetail) {
            final FeedDetail feedDetail = (FeedDetail) adapterList.get(position);
            switch (viewClicked.getId()) {
                case R.id.view_action_like:
                    if(!feedDetail.isLiked()){
                        Animation animation = AnimationUtils.loadAnimation(viewClicked.getContext(), R.anim.bounce_scale_out_in);
                        viewClicked.findViewById(R.id.tv_action_like).clearAnimation();
                        viewClicked.findViewById(R.id.tv_action_like).startAnimation(animation);
                    }
                    feedPostCallback.onLikeClick(feedDetail, position);
                    break;
                case R.id.view_action_comment:
                    feedPostCallback.onCommentClick(feedDetail, position);
                    break;
                case R.id.root_layout_item:
                    feedPostCallback.onFeedLayoutClick(feedDetail, position);

                    break;

                case R.id.tv_feed_owner_title:

                    break;
                case R.id.iv_place_image:

                    if (feedDetail.getReviewImages() != null && feedDetail.getReviewImages().size() > 0) {
                        //This means userimages are being displayed
                        ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages = feedDetail.getReviewImages();
                        showZoomedPagerDialog(0, reviewImages, activity);
                    } else if (!TextUtils.isEmpty(feedDetail.getRestaurantImage())) {
                        //Open the restaurant in menus
                        feedPostCallback.onRestaurantClick(feedDetail.getRestaurantId());
                        GAUtils.event(FEED, HOME, RESTAURANT_IMAGE+CLICKED);
                        //THis means only one image is being displayed which is restaurant Image

                    }


                    //If null means no image is being showed Actually

                    break;
                case R.id.ib_arrow_more:
                    feedPostCallback.onMoreClick(feedDetail,position,viewClicked);
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


        void onMoreClick(FeedDetail feedDetail, int positionInOriginalList, View moreItemView);

        void onDeleteComment(FeedComment feedComment, int position, View viewClicked);

        void onFeedLayoutClick(FeedDetail feedDetail, int position);
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
        @Bind(R.id.line_below_images_pager)
        View lineBelowImagesPager;
        @Bind(R.id.tv_user_commented_name)
        TextView tvUserCommentedName;
        @Bind(R.id.tv_user_commented_comment)
        TextView tvUserCommentedComment;
        @Bind(R.id.tv_action_comment)
        TextView tvComment;
        @Bind(R.id.tv_action_like)
        TextView tvLike;
        @Bind(R.id.view_action_like)
        LinearLayout viewActionLike;
        @Bind(R.id.view_action_comment)
        LinearLayout viewActionComment;
        @Bind(R.id.iv_user_commented_pic)
        ImageView ivUserCommentedPic;
    /*    @Bind(R.id.tv_feed_rating)
        TextView tvFeedRating;*/
        @Bind(R.id.tv_restaurant_feed_address)
        TextView tvFeedAddress;
        @Bind(R.id.layout_user_activity_heading)
        RelativeLayout layoutUserActivity;

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
        @Bind(R.id.ib_arrow_more)
        TextView tvMore;
        @Bind(R.id.vpReviewImages)
        ViewPager vpReviewImages;
        DisplayFeedHomeImagesAdapter displayFeedHomeImagesAdapter;
        @Bind(R.id.tabDots)
        TabLayout tabDots;
        @Bind(R.id.shadow_address)
         View shadowAddress  ;
        @Bind(R.id.layout_actual_post)
        RelativeLayout layoutActualPost;
        @Bind(R.id.layout_comment)
        LinearLayout layoutComment;

        ViewHolderReviewImage(final View view, final ItemListener onClickView) {
            super(view);
            ButterKnife.bind(this, view);
            tvFeedAddress.setTypeface(tvFeedAddress.getTypeface(), Typeface.BOLD);
            tvUserCommentedName.setTypeface(tvUserCommentedName.getTypeface(), Typeface.BOLD);
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

            tvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickView.onClickItem(tvMore, view);
                }
            });
            tabDots.setupWithViewPager(vpReviewImages, true);
        }
    }

    private class ChangeLocationViewHolder extends RecyclerView.ViewHolder {


        private TextView textViewLocation;
        private TextView tvLabel;
        public ChangeLocationViewHolder(View itemView) {
            super(itemView);
            activity.setDeliveryAddressView(itemView);
            activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FEED);
            activity.getDeliveryAddressView().getShadowTop().setVisibility(View.GONE);
            if (activity.getDeliveryAddressView() != null) {
                activity.getDeliveryAddressView().scaleView();
                activity.getDeliveryAddressView().tvDeliveryAddress.setText(R.string.label_city);
                textViewLocation=activity.getDeliveryAddressView().getTvLocation();
                tvLabel=activity.getDeliveryAddressView().tvDeliveryAddress;
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

                    activity.openFeedAddPostFragment(null);
                }
            });
            ivMyProfilePic = (ImageView) itemView.findViewById(R.id.iv_profile_pic);


        }
    }

    private static class ViewBlankHolder extends RecyclerView.ViewHolder {

//        public RelativeLayout relative;

        public ViewBlankHolder(View itemView) {
            super(itemView);
//            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
        }
    }

    @Override
    public int getItemViewType(int position) {


        if (adapterList.get(position) instanceof FeedDetail) {
            return ITEM_FEED;
        } else if (adapterList.get(position) instanceof SelectedLocation)
            return ITEM_LOCATION_SELECTED;
        else if (adapterList.get(position) instanceof AddPostData)
            return ITEM_ADD_POST;
        else if (adapterList.get(position) instanceof Integer && ((Integer) adapterList.get(position)) == ITEM_FOOTER_BLANK) {
            return ITEM_FOOTER_BLANK;
        }


        throw new IllegalArgumentException("No View Type found");
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



        MyClickableSpan(int restaurantId, FeedPostCallback feedPostCallback) {
            super();
            this.restaurantId = restaurantId;
            this.feedPostCallback = feedPostCallback;
        }


        public void onClick(View tv) {
            feedPostCallback.onRestaurantClick(restaurantId);
            GAUtils.event(FEED, HOME, RESTAURANT_NAME+CLICKED);
        }

        public void updateDrawState(TextPaint ds) {// override updateDrawState
            ds.setUnderlineText(false); // set to false to remove underline

        }
    }

    public void notifyFeedListItem(int postitioninFeedList) {
        notifyItemChanged(postitioninFeedList);
    }

    public static class SelectedLocation {
        private String cityName;
        private boolean isCity;

        public SelectedLocation(String cityName,boolean isCity) {
            this.cityName = cityName;
            this.isCity=isCity;
        }

        public String getCityName() {
            return cityName;
        }

        public boolean isCity() {
            return isCity;
        }
    }

    public static class AddPostData {
        private String addPostText;
        private String imageUrl;

        public AddPostData(String addPostText, String imageUrl) {
            this.addPostText = addPostText;
            this.imageUrl = imageUrl;
        }

        public String getAddPostText() {
            return addPostText;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}