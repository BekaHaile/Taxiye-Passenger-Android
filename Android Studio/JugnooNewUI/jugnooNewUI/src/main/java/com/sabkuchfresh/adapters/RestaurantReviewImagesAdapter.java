package com.sabkuchfresh.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundedCornersTransformation;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;


/**
 * Created by Shankar on 7/17/15.
 */
public class RestaurantReviewImagesAdapter extends RecyclerView.Adapter<RestaurantReviewImagesAdapter.ViewHolderReviewImage> {

    private FreshActivity activity;
    private FetchFeedbackResponse.Review review;
    private Callback callback;

    public RestaurantReviewImagesAdapter(FreshActivity activity, FetchFeedbackResponse.Review review, Callback callback) {
        this.activity = activity;
        this.review = review;
        this.callback = callback;
    }

    public void setList(FetchFeedbackResponse.Review review){
        this.review = review;
        notifyDataSetChanged();
    }

    @Override
    public RestaurantReviewImagesAdapter.ViewHolderReviewImage onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_review_image, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);
        ASSL.DoMagic(v);
        return new ViewHolderReviewImage(v);
    }

    @Override
    public void onBindViewHolder(RestaurantReviewImagesAdapter.ViewHolderReviewImage holder, int position) {
        try {
            FetchFeedbackResponse.ReviewImage reviewImage = review.getImages().get(position);
            Picasso.with(activity).load(reviewImage.getThumbnail())
                    .resize((int) (ASSL.minRatio() * 300f), (int) (ASSL.minRatio() * 300f))
                    .centerCrop()
                    .transform(new RoundedCornersTransformation((int)(ASSL.minRatio()*8), 0))
                    .placeholder(R.drawable.ic_fresh_item_placeholder)
                    .into(holder.ivImage);
            holder.ivImage.setTag(position);
            holder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        callback.onImageClick(pos, review);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    @Override
    public int getItemCount() {
        return review.getImages() == null ? 0 : review.getImages().size();
    }

    class ViewHolderReviewImage extends RecyclerView.ViewHolder {
        public ImageView ivImage;
        public ViewHolderReviewImage(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }


	/**
	 * onImageClick positionImageClicked is for opening that image in ReviewImageDialog
     */
    public interface Callback{
        void onImageClick(int positionImageClicked, FetchFeedbackResponse.Review review);
    }
}
