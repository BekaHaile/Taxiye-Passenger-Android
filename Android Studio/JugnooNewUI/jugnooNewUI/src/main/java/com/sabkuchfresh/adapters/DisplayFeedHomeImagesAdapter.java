package com.sabkuchfresh.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundedCornersTransformation;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;


/**
 * Created by Shankar on 7/17/15.
 */
public class DisplayFeedHomeImagesAdapter extends RecyclerView.Adapter<DisplayFeedHomeImagesAdapter.ViewHolderReviewImage> implements ItemListener{

    private FreshActivity activity;
    private ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages;
    private Callback callback;
    private RecyclerView recyclerView;


    public DisplayFeedHomeImagesAdapter(FreshActivity activity, ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages, RecyclerView recyclerView) {
        this.activity = activity;
        this.reviewImages = reviewImages;
        this.recyclerView=recyclerView;
    }


   public void setList(ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages){
        this.reviewImages = reviewImages;
        notifyDataSetChanged();
    }



    @Override
    public DisplayFeedHomeImagesAdapter.ViewHolderReviewImage onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_review_image, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);
        ASSL.DoMagic(v);
        return new ViewHolderReviewImage(v,this);
    }

    @Override
    public void onBindViewHolder(DisplayFeedHomeImagesAdapter.ViewHolderReviewImage holder, int position) {
        try {

            String path ;
            FetchFeedbackResponse.ReviewImage reviewImage = reviewImages.get(position+1);
                path=reviewImage.getThumbnail();
                Picasso.with(activity).load(path)
                        .resize((int) (ASSL.minRatio() * 90f), (int) (ASSL.minRatio() * 90f))
                        .centerCrop()
                        .transform(new RoundedCornersTransformation((int)(ASSL.minRatio()*8), 0))
                        .placeholder(R.drawable.ic_fresh_item_placeholder)
                        .into(holder.ivImage);



        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    @Override
    public int getItemCount() {
        return reviewImages != null && reviewImages.size()>1 ? reviewImages.size()-1 : 0;
    }

    @Override
    public void onClickItem(View viewClicked, View itemView) {
        int position = recyclerView.getChildLayoutPosition(itemView);
        switch (viewClicked.getId()){
            case R.id.ivImage:
                  FeedHomeAdapter.showZoomedPagerDialog(position+1, reviewImages,activity);
                break;
            case R.id.btn_remove:




                break;
            default:
                break;
        }
    }


    class ViewHolderReviewImage extends RecyclerView.ViewHolder {
        public ImageView ivImage;
        public ImageView btnRemove;
        public ViewHolderReviewImage(final View itemView, final ItemListener itemListener) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            btnRemove=(ImageView)itemView.findViewById(R.id.btn_remove);
            btnRemove.setVisibility(View.GONE);
            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(ivImage,itemView);
                }
            });
           /* btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(btnRemove,itemView);
                }
            });*/
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivImage.getLayoutParams();
            layoutParams.width= (int) (ASSL.minRatio()*90f);
            layoutParams.height= (int) (ASSL.minRatio()*90f);
            layoutParams.height= (int) (ASSL.minRatio()*90f);
            ivImage.setLayoutParams(layoutParams);
        }

    }

    public interface Callback{
        void onImageClick(Object object);

        void onDelete(Object object);
    }


}
