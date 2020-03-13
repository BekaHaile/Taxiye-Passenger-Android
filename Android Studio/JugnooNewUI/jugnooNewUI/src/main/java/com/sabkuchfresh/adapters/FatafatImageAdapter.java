package com.sabkuchfresh.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.picker.image.model.ImageEntry;
import com.sabkuchfresh.home.FreshActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import product.clicklabs.jugnoo.R;

/**
 * Created by cl-macmini-01 on 1/15/18.
 */

public class FatafatImageAdapter extends RecyclerView.Adapter<FatafatImageAdapter.ViewHolderImage> implements ItemListener {

    private FreshActivity activity;
    private ArrayList<?> images;
    private FatafatImageAdapter.Callback callback;
    private RecyclerView recyclerView;


    public FatafatImageAdapter(FreshActivity activity, ArrayList<?> images, FatafatImageAdapter.Callback callback, RecyclerView recyclerView) {
        this.activity = activity;
        this.images = images;
        this.callback = callback;
        this.recyclerView = recyclerView;
    }


    public void setList(ArrayList<?> reviewImages) {
        this.images = reviewImages;
        notifyDataSetChanged();
    }


    @Override
    public FatafatImageAdapter.ViewHolderImage onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fatafat_image, parent, false);
        return new ViewHolderImage(v, this);
    }

    @Override
    public void onBindViewHolder(ViewHolderImage holder, int position) {

        String path = ((ImageEntry) images.get(position)).path;
        Picasso.with(activity).load(new File(path))
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_fresh_item_placeholder)
                .into(holder.ivImage);

    }

    @Override
    public int getItemCount() {
        return images == null ? 0 : images.size();
    }

    @Override
    public void onClickItem(View viewClicked, View itemView) {
        int position = recyclerView.getChildLayoutPosition(itemView);
        if (position != RecyclerView.NO_POSITION) {
            switch (viewClicked.getId()) {

                case R.id.ivImage:

                    callback.onImageClick(images.get(position));
                    break;

                case R.id.btnDelete:

                    callback.onDelete(images.get(position));
                    notifyItemRemoved(position);

                    break;
                default:
                    break;
            }
        }
    }


    public interface Callback {
        void onImageClick(Object object);

        void onDelete(Object object);
    }

    class ViewHolderImage extends RecyclerView.ViewHolder {
        public ImageView ivImage;
        ImageView btnRemove;

        ViewHolderImage(final View itemView, final ItemListener itemListener) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            btnRemove = (ImageView) itemView.findViewById(R.id.btnDelete);
            btnRemove.setVisibility(View.VISIBLE);
            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(ivImage, itemView);
                }
            });
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(btnRemove, itemView);
                }
            });
        }

    }

}
