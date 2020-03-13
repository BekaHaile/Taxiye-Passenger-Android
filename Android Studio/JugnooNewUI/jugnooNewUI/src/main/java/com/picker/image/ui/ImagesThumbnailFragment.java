package com.picker.image.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import product.clicklabs.jugnoo.R;
import com.picker.image.util.Events;
import com.picker.image.util.Picker;

import de.greenrobot.event.EventBus;



public class ImagesThumbnailFragment extends Fragment {

    public static final String TAG = ImagesThumbnailFragment.class.getSimpleName();
    protected RecyclerView mImagesRecycler;
    protected Picker mPickOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mImagesRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_image_browse, container, false);

        setupRecycler();

//        mPickOptions = EventBus.getDefault().getStickyEvent(Events.OnPublishPickOptionsEvent.class).options;
        mPickOptions=((PickerActivity)getActivity()).mPickOptions;
        return mImagesRecycler;
    }

    @Override
    public void onStart() {
        EventBus.getDefault().registerSticky(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    protected void setupRecycler() {

        mImagesRecycler.setHasFixedSize(true);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.num_columns_images));

        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mImagesRecycler.setLayoutManager(gridLayoutManager);
        mImagesRecycler.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.image_spacing)));


    }


    public void onEvent(final Events.OnClickAlbumEvent event) {
        mImagesRecycler.setAdapter(new ImagesThumbnailAdapter(this, event.albumEntry, mImagesRecycler, mPickOptions));
    }


    public void onEvent(final Events.OnAttachFabEvent fabEvent) {
        fabEvent.fab.attachToRecyclerView(mImagesRecycler);
    }

    public void onEvent(final Events.OnUpdateImagesThumbnailEvent redrawImage) {
        mImagesRecycler.getAdapter().notifyDataSetChanged();





    }

    public void onEvent(final Events.onItemRemovedEventNotify redrawImage) {
        ((ImagesThumbnailAdapter)mImagesRecycler.getAdapter()).notifyItemUnchecked(redrawImage.currentImage);

    }





}
