package com.sabkuchfresh.feed.ui.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.sabkuchfresh.adapters.ItemListener;
import com.sabkuchfresh.feed.ui.views.animateheartview.LikeButton;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.feeddetail.FeedComment;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Utils;

import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;


/**
 * Created by Shankar on 7/17/15.
 */
public class FeedPostDetailAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> implements ItemListener, DisplayFeedHomeImagesAdapter.Callback {

    private FreshActivity activity;
    private FeedHomeAdapter.FeedPostCallback callback;
    private List<Object> feedDetailData;
    private RecyclerView recyclerView;
    public static final int TYPE_POST_LAYOUT = 1;
    public static final int TYPE_USERS_COMMENTS = 2;
    public static final int TYPE_MY_COMMENT = 3;
    private TextWatcher textWatcherMyComment;
    private Drawable userDrawable;


    private static final StyleSpan BOLD_SPAN = new StyleSpan(Typeface.BOLD);
    private static final StyleSpan BOLD_SPAN_2 = new StyleSpan(Typeface.BOLD);//since we cant reuse same style span again in a spannable

    public FeedPostDetailAdapter(Activity activity, List<Object> reviewImages, RecyclerView recyclerView, FeedHomeAdapter.FeedPostCallback callback, TextWatcher textWatcherMyComment) {
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
                return new FeedHomeAdapter.ViewHolderReviewImage(v,this);
            case TYPE_USERS_COMMENTS:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_comment, parent, false);
                return new UserCommentViewHolder(v,this);
            case TYPE_MY_COMMENT:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_my_comment, parent, false);
                return new MyCommentViewHolder(v);
            default:
                throw new IllegalArgumentException();

        }


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        try {
            if(holder instanceof FeedHomeAdapter.ViewHolderReviewImage)
            {

                FeedHomeAdapter.setData((FeedHomeAdapter.ViewHolderReviewImage)holder,(FeedDetail) feedDetailData.get(position),activity, callback, this, true);
                ((FeedHomeAdapter.ViewHolderReviewImage) holder).shadow.setVisibility(View.GONE);

            }
            else if(holder instanceof MyCommentViewHolder){


                if (Data.userData!=null) {
                    ((MyCommentViewHolder) holder).tvMyUserName.setText(Data.userData.userName);
                    if (!TextUtils.isEmpty(Data.userData.userImage) && !Constants.DEFAULT_IMAGE_URL.equalsIgnoreCase(Data.userData.userImage))
                        Picasso.with(activity).load(Data.userData.userImage).resize(Utils.convertDpToPx(activity,25), Utils.convertDpToPx(activity,25)).centerCrop().transform(new CircleTransform()).into(((MyCommentViewHolder) holder).ivMyProfilePic);
                    else {
                        if (userDrawable == null) {
                            String firstLetter =  Data.userData.userName.toUpperCase().substring(0,1);
                            TextDrawable drawable = TextDrawable.builder()
                                    .beginConfig().bold().endConfig()
                                    .buildRound(firstLetter, activity.getParsedColor("", null));
                            userDrawable = drawable;
                        }
                        ((MyCommentViewHolder) holder).ivMyProfilePic.setImageDrawable(userDrawable);
                    }
                }

                ((MyCommentViewHolder) holder).edtComment.setText(callback.getEditTextString());

            }
            else if(holder instanceof UserCommentViewHolder){



                final FeedComment feedComment = (FeedComment) feedDetailData.get(position);
                final UserCommentViewHolder userCommentViewHolder =  ((UserCommentViewHolder) holder);


            //Show Comment if X Comment on Y Post Done with only one textVIew


                String keyComment =  ": " + feedComment.getCommentContent();
                SpannableString spannableString = new SpannableString(feedComment.getUserName() + keyComment);
                spannableString.setSpan(BOLD_SPAN,0,feedComment.getUserName().length(),SPAN_INCLUSIVE_EXCLUSIVE);
                userCommentViewHolder.tvUserNameAndComment.setText(spannableString);


                if(position==feedDetailData.size()-1)
                    userCommentViewHolder.lineBottom.setVisibility(View.INVISIBLE);
                else
                    userCommentViewHolder.lineBottom.setVisibility(View.VISIBLE);

                if(((FeedComment) feedDetailData.get(position)).getTimeCreated()==null)
                    userCommentViewHolder.tvUserTimePosted.setVisibility(View.GONE);
                else {
                    userCommentViewHolder.tvUserTimePosted.setVisibility(View.VISIBLE);
                    userCommentViewHolder.tvUserTimePosted.setText(FeedHomeAdapter.getTimeToDisplay(((FeedComment) feedDetailData.get(position)).getTimeCreated(), activity.isTimeAutomatic));
                }

                if (!TextUtils.isEmpty(feedComment.getUserImage()) && !Constants.DEFAULT_IMAGE_URL.equalsIgnoreCase(feedComment.getUserImage()))
                    Picasso.with(activity).load(feedComment.getUserImage()).resize(Utils.convertDpToPx(activity,50), Utils.convertDpToPx(activity,50)).centerCrop().transform(new CircleTransform()).into(userCommentViewHolder.ivUserCommentPic);
                else {
                    if (feedComment.getDrawable() == null) {
                        String firstLetter =  feedComment.getUserName().toUpperCase().substring(0,1);
                        TextDrawable drawable = TextDrawable.builder()
                                .beginConfig().bold().endConfig()
                                .buildRound(firstLetter, activity.getParsedColor(feedComment.getColor(), null));
                        feedComment.setDrawable(drawable);
                    }
                    userCommentViewHolder.ivUserCommentPic.setImageDrawable(feedComment.getDrawable());
                }



                //Swipe Listener

                userCommentViewHolder.swipeLayout.setSwipeEnabled(feedComment.canEdit());
                userCommentViewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
                userCommentViewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                    @Override
                    public void onOpen(SwipeLayout layout) {

                    }

                    @Override
                    public void onStartOpen(SwipeLayout layout) {
                        super.onStartOpen(layout);


                        userCommentViewHolder.rlComment.setBackgroundColor(ContextCompat.getColor(activity,R.color.grey_e7));

                    }

                    @Override
                    public void onClose(SwipeLayout layout) {
                        super.onClose(layout);
                        userCommentViewHolder.rlComment.setBackgroundColor(ContextCompat.getColor(activity,R.color.feed_comment_background));

                    }
                });
                mItemManger.bindView(holder.itemView,position);


                ((UserCommentViewHolder) holder).ivDeleteComment.setVisibility(feedComment.canEdit()?View.VISIBLE:View.GONE);
                if(feedComment.canEdit()&& mItemManger.isOpen(position) ){
                    userCommentViewHolder.rlComment.setBackgroundColor(ContextCompat.getColor(activity,R.color.grey_e7));

                }else{
                    userCommentViewHolder.rlComment.setBackgroundColor(ContextCompat.getColor(activity,R.color.feed_comment_background));

                }

                FeedHomeAdapter.linkifyTextView(userCommentViewHolder.tvUserNameAndComment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onRestaurantImageClick(Integer restaurantId) {
        callback.onRestaurantClick(restaurantId);

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
    public void onClickItem(View viewClicked, View parentView) {

        int position = recyclerView.getChildLayoutPosition(parentView);

        if (position!=RecyclerView.NO_POSITION) {
            switch (viewClicked.getId()) {
                case R.id.view_action_like:
                    if(feedDetailData.get(position) instanceof  FeedDetail){
                        FeedDetail feedDetail = (FeedDetail) feedDetailData.get(position);
                        if(!feedDetail.isLikeAPIInProgress()){
                            feedDetail.setIsLikeAPIInProgress(true);
                            callback.onLikeClick(null,position);
                        }
                    }

                    break;
                case R.id.view_action_comment:
                        callback.onCommentClick(null,position);
                    break;
                case R.id.iv_place_image:
                    if(feedDetailData.get(position) instanceof  FeedDetail)
                    {
                        FeedDetail feedDetail = (FeedDetail) feedDetailData.get(position);

                        if (feedDetail.getReviewImages() != null && feedDetail.getReviewImages().size() > 0){
                            //This means userimages are being displayed
                            ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages = feedDetail.getReviewImages();
                            FeedHomeAdapter.showZoomedPagerDialog(0, reviewImages, activity);
                        }

                        else if (!TextUtils.isEmpty(feedDetail.getRestaurantImage())) {
                            //Open the restaurant in menus
                            callback.onRestaurantClick(feedDetail.getRestaurantId());
                            //THis means only one image is being displayed which is restaurant Image

                        }
                    }


                    break;
                case R.id.ib_arrow_more:
                    callback.onMoreClick((FeedDetail) feedDetailData.get(position),0,viewClicked);
                    break;
                case R.id.ll_delete_comment:
                    UserCommentViewHolder userCommentViewHolder = ((UserCommentViewHolder)recyclerView.findViewHolderForAdapterPosition(position));
                    if(userCommentViewHolder!=null) {
                        userCommentViewHolder.swipeLayout.close(false,true);
                    }
                    callback.onDeleteComment((FeedComment) feedDetailData.get(position),position,viewClicked);
                    break;
                default:
                    break;
            }
        }
    }

    public void notifyOnLike(int position,boolean isLikeAPI) {
        if(feedDetailData!=null && position<feedDetailData.size() && feedDetailData.get(position) instanceof FeedDetail)
        {
            FeedDetail feedDetail = (FeedDetail) feedDetailData.get(position);
            if (feedDetail.isLikeAPIInProgress()) {
                feedDetail.setIsLikeAPIInProgress(false);
                if(isLikeAPI) {

                    FeedHomeAdapter.ViewHolderReviewImage viewHolderReviewImage = ((FeedHomeAdapter.ViewHolderReviewImage)recyclerView.findViewHolderForAdapterPosition(position));
                    if(viewHolderReviewImage!=null) {
                        LikeButton likeButton = viewHolderReviewImage.likeButtonAnimate;
                        likeButton.onClick(likeButton);
                    }

                    feedDetail.setLikeCount(feedDetail.getLikeCount() + 1);
                } else if(feedDetail.getLikeCount()!=0)
                    feedDetail.setLikeCount(feedDetail.getLikeCount()-1);

                ((FeedDetail) feedDetailData.get(position)).setLiked(isLikeAPI);
                notifyItemChanged(position);
            }
        }
    }

    public void notifyOnComment(){
        if(feedDetailData!=null && feedDetailData.size()!=0 && feedDetailData.get(0) instanceof FeedDetail)
        {
            FeedDetail feedDetail = (FeedDetail) feedDetailData.get(0);
            feedDetail.setCommentCount(feedDetail.getCommentCount()+1);
            notifyItemChanged(1);
        }
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    /*public interface Callback {
        void onLikeClick(int positionOfLayout);

        void onCommentClick(Object object);

        String getEditTextString();
    }*/



     static class UserCommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_profile_pic)
        ImageView ivUserCommentPic;
        @BindView(R.id.tv_user_name_and_comment)
        TextView tvUserNameAndComment;

        @BindView(R.id.tv_time_posted)
        TextView tvUserTimePosted;
         @BindView(R.id.line_bottom)
         View lineBottom;
         @BindView(R.id.ll_delete_comment)
         LinearLayout ivDeleteComment;
         @BindView(R.id.swipe)
         SwipeLayout swipeLayout;
         @BindView(R.id.rl_comment)
         RelativeLayout rlComment;

         UserCommentViewHolder(final View view, final ItemListener itemListener) {
            super(view);
            ButterKnife.bind(this, view);


             ivDeleteComment.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     itemListener.onClickItem(ivDeleteComment,view);
                 }
             });
        }
    }

    public class MyCommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_profile_pic)
        ImageView ivMyProfilePic;
        @BindView(R.id.tv_user_name)
        TextView tvMyUserName;
        @BindView(R.id.edt_comment)
        EditText edtComment;


        MyCommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            edtComment.addTextChangedListener(textWatcherMyComment);
            tvMyUserName.setTypeface(tvMyUserName.getTypeface(), Typeface.BOLD);
        }
    }
}
