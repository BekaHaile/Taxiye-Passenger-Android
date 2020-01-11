package com.sabkuchfresh.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundedCornersTransformation;

import com.picker.image.model.ImageEntry;

import java.io.File;
import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;


/**
 * Created by Shankar on 7/17/15.
 */
public class EditReviewImagesAdapter extends RecyclerView.Adapter<EditReviewImagesAdapter.ViewHolderReviewImage> implements ItemListener{

    private FreshActivity activity;
    private ArrayList<?> reviewImages;
    private Callback callback;
    private RecyclerView recyclerView;


    public EditReviewImagesAdapter(FreshActivity activity, ArrayList<?> reviewImages, Callback callback, RecyclerView recyclerView) {
        this.activity = activity;
        this.reviewImages = reviewImages;
        this.callback = callback;
        this.recyclerView=recyclerView;
    }


   public void setList(ArrayList<?> reviewImages){
        this.reviewImages = reviewImages;
        notifyDataSetChanged();
    }



    @Override
    public EditReviewImagesAdapter.ViewHolderReviewImage onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_review_image, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);
        ASSL.DoMagic(v);
        return new ViewHolderReviewImage(v,this);
    }

    @Override
    public void onBindViewHolder(EditReviewImagesAdapter.ViewHolderReviewImage holder, int position) {
        try {

            String path ;
            if (reviewImages.get(position) instanceof FetchFeedbackResponse.ReviewImage) {
                FetchFeedbackResponse.ReviewImage reviewImage = (FetchFeedbackResponse.ReviewImage) reviewImages.get(position);
                path=reviewImage.getUrl();
                Picasso.with(activity).load(path)
                        .resize((int) (ASSL.minRatio() * 300f), (int) (ASSL.minRatio() * 300f))
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




        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    @Override
    public int getItemCount() {
        return reviewImages == null ? 0 : reviewImages.size();
    }

    @Override
    public void onClickItem(View viewClicked, View itemView) {
        int position = recyclerView.getChildLayoutPosition(itemView);
        if (position!=RecyclerView.NO_POSITION) {
            switch (viewClicked.getId()){
                case R.id.ivImage:

                        callback.onImageClick(reviewImages.get(position));


                    break;
                case R.id.btn_remove:

                        callback.onDelete(reviewImages.get(position));
                        notifyItemRemoved(position);


                    break;
                default:
                    break;
            }
        }
    }


    class ViewHolderReviewImage extends RecyclerView.ViewHolder {
        public ImageView ivImage;
        public ImageView btnRemove;
        public ViewHolderReviewImage(final View itemView, final ItemListener itemListener) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            btnRemove=(ImageView)itemView.findViewById(R.id.btn_remove);
            btnRemove.setVisibility(View.VISIBLE);
            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(ivImage,itemView);
                }
            });
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(btnRemove,itemView);
                }
            });
        }

    }

    public interface Callback{
        void onImageClick(Object object);

        void onDelete(Object object);
    }


}
