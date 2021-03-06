package com.picker.image.ui;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.picker.image.model.AlbumEntry;
import com.picker.image.util.Events;
import com.picker.image.util.Picker;
import com.picker.image.util.Util;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import product.clicklabs.jugnoo.R;


public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder> implements Util.OnClickAlbum {

    public final RecyclerView mRecycler;
    protected final ArrayList<AlbumEntry> mAlbumList;
    protected final Picker mPickOptions;
    private final Fragment mFragment;

    public AlbumsAdapter(final Fragment fragment, final ArrayList<AlbumEntry> albums, final RecyclerView recyclerView, Picker mPickOptions) {
        mFragment = fragment;
        this.mAlbumList = albums;
        this.mRecycler = recyclerView;
//        this.mPickOptions = EventBus.getDefault().getStickyEvent(Events.OnPublishPickOptionsEvent.class).options;
        this.mPickOptions=mPickOptions;
    }


    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View layout = LayoutInflater.from(mRecycler.getContext()).inflate(R.layout.element_album, parent, false);

        return new AlbumViewHolder(layout, this);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        final AlbumEntry albumEntry = mAlbumList.get(position);
        setHeight(holder.itemView);
        setupAlbum(holder, albumEntry);

    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }

    @Override
    public void onClickAlbum(View layout) {
        final int position = mRecycler.getChildAdapterPosition(layout);
        final AlbumEntry album = mAlbumList.get(position);

        EventBus.getDefault().postSticky(new Events.OnClickAlbumEvent(album));


    }


    public void setHeight(final View layout) {

        final int height = mRecycler.getMeasuredWidth() / mRecycler.getResources().getInteger(R.integer.num_columns_albums);

        layout.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));


    }

    public void setupAlbum(final AlbumViewHolder holder, final AlbumEntry album) {

        holder.name.setTextColor(mPickOptions.albumNameTextColor);
        holder.count.setTextColor(mPickOptions.albumImagesCountTextColor);


        holder.name.setText(album.name);
        holder.count.setText(album.imageList.size() + "");

        holder.detailsLayout.setBackgroundColor(mPickOptions.albumBackgroundColor);

        RequestOptions options = new RequestOptions()
                .centerCrop();

        Glide.with(mFragment)
                .asBitmap()
                .apply(options)
                .load(album.coverImage.path)
                .into(holder.thumbnail);

    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        protected final ImageView thumbnail;
        protected final TextView count;
        protected final TextView name;
        protected final RelativeLayout detailsLayout;


        public AlbumViewHolder(final View itemView, final Util.OnClickAlbum listener) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.album_thumbnail);
            count = (TextView) itemView.findViewById(R.id.album_count);
            name = (TextView) itemView.findViewById(R.id.album_name);
            detailsLayout = (RelativeLayout) itemView.findViewById(R.id.album_detail_layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickAlbum(itemView);
                }
            });

        }
    }
}
