package com.picker.image.ui;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.picker.image.model.ImageEntry;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundedCornersTransformation;

import java.io.File;
import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;


/**
 * Created by Shankar on 7/17/15.
 */
public class DisplaySelectedImagesAdapter extends RecyclerView.Adapter<DisplaySelectedImagesAdapter.ViewHolderReviewImage>{

    private Activity activity;
    private ArrayList<Object> reviewImages;
    private Callback callback;

    public DisplaySelectedImagesAdapter(Activity activity, ArrayList<Object> reviewImages, Callback callback) {
        this.activity = activity;
        this.reviewImages = reviewImages;
        this.callback = callback;
    }

    public void setList(ArrayList<Object> reviewImages){
        this.reviewImages = reviewImages;
        notifyDataSetChanged();
    }

    @Override
    public DisplaySelectedImagesAdapter.ViewHolderReviewImage onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_review_image, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);
        ASSL.DoMagic(v);
        return new ViewHolderReviewImage(v);
    }

    @Override
    public void onBindViewHolder(DisplaySelectedImagesAdapter.ViewHolderReviewImage holder, final int position) {
        try {

            String path ;
            if (reviewImages.get(position) instanceof FetchFeedbackResponse.ReviewImage) {
                FetchFeedbackResponse.ReviewImage reviewImage = (FetchFeedbackResponse.ReviewImage) reviewImages.get(position);
                path=reviewImage.getUrl();
                Picasso.with(activity).load(path).resize((int) (ASSL.minRatio() * 300f), (int) (ASSL.minRatio() * 300f))
                        .centerCrop()
                        .transform(new RoundedCornersTransformation((int)(ASSL.minRatio()*8), 0))
                        .placeholder(R.drawable.ic_fresh_item_placeholder)
                        .into(holder.ivImage);
            }
            else if(reviewImages.get(position) instanceof ImageEntry) {
                path=((ImageEntry)reviewImages.get(position)).path;
                Picasso.with(activity).load(new File(path))
                        .resize((int) (ASSL.minRatio() * 300f), (int) (ASSL.minRatio() * 300f))
                        .centerCrop()
                        .transform(new RoundedCornersTransformation((int)(ASSL.minRatio()*8), 0))
                        .placeholder(R.drawable.ic_fresh_item_placeholder)
                        .into(holder.ivImage);
            }






            holder.ivImage.setTag(position);
            holder.btnRemove.setTag(position);
            holder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    callback.onImageClick(reviewImages.get(pos));
                }
            });

            holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    callback.onDelete(reviewImages.get(pos));
                    notifyDataSetChanged();

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    @Override
    public int getItemCount() {
        return reviewImages == null ? 0 : reviewImages.size();
    }


    class ViewHolderReviewImage extends RecyclerView.ViewHolder {
        public ImageView ivImage;
        public ImageView btnRemove;
        public ViewHolderReviewImage(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            btnRemove=(ImageView)itemView.findViewById(R.id.btn_remove);
            btnRemove.setVisibility(View.VISIBLE);


        }

    }

    public interface Callback{
        void onImageClick(Object object);

        void onDelete(Object object);
    }


}
