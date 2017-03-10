package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;


/**
 * Created by Shankar on 7/17/15.
 */
public class FeedOfferingListAdapter extends RecyclerView.Adapter<FeedOfferingListAdapter.ViewHolderReviewImage> implements ItemListener {


    private Activity activity;
    private Callback callback;
    private ArrayList<FeedData> reviewImages;
    private RecyclerView recyclerView;

    public FeedOfferingListAdapter(Activity activity, ArrayList<FeedData> reviewImages, RecyclerView recyclerView, Callback callback) {
        this.activity = activity;
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

    }

    @Override
    public int getItemCount() {
        return reviewImages == null ? 5 : reviewImages.size();
    }

    @Override
    public void onClickItem(View tvComment, View view) {

        int position  = recyclerView.getChildLayoutPosition(view);
        switch (view.getId()){
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


    private class FeedData {

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
