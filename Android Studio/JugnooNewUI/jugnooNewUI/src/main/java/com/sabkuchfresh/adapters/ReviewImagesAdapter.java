/*
package com.sabkuchfresh.adapters;


import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;


import java.util.ArrayList;

import product.clicklabs.jugnoo.R;


public final class ReviewImagesAdapter extends RecyclerView.Adapter<ReviewImagesAdapter.myViewHolder> {

    public static final int ACTION_IMAGE_DELETE_SERVER = 9;
    private final Activity context;
    private final LayoutInflater inflater;
    private final OnItemClickListener onItemClickListener;
    private int maxNoImages;
    private boolean isEditable;
    private final ArrayList<Object> actualList = new ArrayList<>();



    */
/**
     * Interface for receiving click events from cells.
     *//*

    public interface OnItemClickListener {
        void onClickItem(int ID, int position, Object serverImage);
    }

    public ReviewImagesAdapter(Activity context, ArrayList<Object> data, int maxNoImages) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        isEditable = true;
        actualList.clear();
        actualList.addAll(data);
        this.maxNoImages = maxNoImages;
        onItemClickListener = (OnItemClickListener) context;

    }


    */
/*
        For viewing images thumbnails from server
     *//*

    public ReviewImagesAdapter(Activity context, ArrayList<Image> stringImageList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        if (stringImageList != null)
            actualList.addAll(stringImageList);
        onItemClickListener = (OnItemClickListener) context;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_thumbnail_image, parent, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final myViewHolder holder, final int position) {


        Object imageData = actualList.get(position);

        if (imageData instanceof ChosenImage) {
            final ChosenImage image = (ChosenImage) imageData;
            holder.btnClearImage.setVisibility(View.VISIBLE);
            holder.imageThumbnail.setOnClickListener(null);
            holder.btnClearImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClickItem(ACTION_IMAGE_DELETE, position, image);

                }
            });
            setImage(holder, image.getThumbnailPath());

        }
        if (imageData instanceof Image) {
            final Image serverImage = (Image) imageData;
            setImage(holder, serverImage.getThumbnail());
            if (isEditable) {
                holder.btnClearImage.setVisibility(View.VISIBLE);
                holder.imageThumbnail.setOnClickListener(null);
                holder.btnClearImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onClickItem(ACTION_IMAGE_DELETE_SERVER, position, serverImage);

                    }
                });
            } else {
                holder.btnClearImage.setVisibility(View.GONE);
                holder.imageThumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onClickItem(ACTION_IMAGE_CHOOSE, position, serverImage);
                    }
                });
            }

        }


    }

    private void setImage(final myViewHolder holder, String imageLink) {
        Glide.with(context).load(imageLink).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.imageThumbnail) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCornerRadius(10);
                holder.imageThumbnail.setImageDrawable(circularBitmapDrawable);
            }
        });
    }


    public void resetData(ArrayList<Object> newData) {

        if (isEditable) {
            actualList.clear();
            actualList.addAll(newData);

             notifyDataSetChanged();
        }


    }


    @Override
    public int getItemCount() {
        return actualList != null ? actualList.size() : 0;

    }


    public class myViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageThumbnail;
        final ImageButton btnClearImage;


        public myViewHolder(View itemView) {
            super(itemView);
            imageThumbnail = (ImageView) itemView.findViewById(R.id.item_thumb_image);
            btnClearImage = (ImageButton) itemView.findViewById(R.id.btn_clear_image);


        }


    }
    private class Image{
        private String original;
        private String thumbnail;

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getOrigninal() {
            return original;
        }

        public void setOrigninal(String origninal) {
            this.original = origninal;
        }
    }

}
*/
