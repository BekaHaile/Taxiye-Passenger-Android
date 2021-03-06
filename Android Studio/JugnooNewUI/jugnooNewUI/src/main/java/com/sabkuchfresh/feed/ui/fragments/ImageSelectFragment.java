package com.sabkuchfresh.feed.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.picker.image.model.ImageEntry;
import com.picker.image.util.Picker;
import com.sabkuchfresh.adapters.EditReviewImagesAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.permission.PermissionCommon;


import static android.app.Activity.RESULT_OK;

/**
 * Created by Parminder Singh on 3/20/17.
 */

public abstract class ImageSelectFragment extends Fragment {


    //Images Recycler Variables
    protected String[] permissionsRequest;
    protected Picker picker;
    protected static final int REQUEST_CODE_SELECT_IMAGE = 106;
    private static final int REQ_CODE_PERMISSION_IMAGE = 1004;
    protected EditReviewImagesAdapter editReviewImagesAdapter;
    protected RecyclerView displayImagesRecycler;
    protected ScrollView scrollView;
    public ArrayList<Object> imageSelected;
    protected FreshActivity activity;
    protected int maxNoImages;
    private PermissionCommon mPermissionCommon;


    //This object would only be intialised if we are editing post
    protected FeedDetail feedDetail;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionCommon = new PermissionCommon(this).setCallback(new PermissionCommon.PermissionListener() {
            @Override
            public void permissionGranted(int requestCode) {
                if (picker == null) {
                    picker = new Picker.Builder(activity, R.style.AppThemePicker_NoActionBar).setPickMode(Picker.PickMode.MULTIPLE_IMAGES).build();
                }

                picker.setLimit(imageSelected == null ? maxNoImages : maxNoImages - imageSelected.size());
                picker.startActivity(ImageSelectFragment.this, activity, REQUEST_CODE_SELECT_IMAGE);
            }

            @Override
            public boolean permissionDenied(int requestCode, boolean neverAsk) {
                return true;
            }

            @Override
            public void onRationalRequestIntercepted(int requestCode) {

            }
        });
        if(Data.getFeedData()!=null && Data.getFeedData().getMaxUploadImagesFeed()>0)
            maxNoImages=Data.getFeedData().getMaxUploadImagesFeed();
        else
            maxNoImages=5;

        if(feedDetail!=null && feedDetail.getReviewImages()!=null &&feedDetail.getReviewImages().size()>0){
            if (imageSelected == null)
                imageSelected = new ArrayList<>();

            imageSelected.addAll(feedDetail.getReviewImages());

            if(feedDetail.getReviewImages().size()>maxNoImages)
                maxNoImages=feedDetail.getReviewImages().size();
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (FreshActivity) context;
    }

    public void setUpImagesAdapter() {


        if (imageSelected == null || imageSelected.size() == 0) {
            displayImagesRecycler.setVisibility(View.GONE);
            return;
        }

        if (editReviewImagesAdapter == null) {
            editReviewImagesAdapter = new EditReviewImagesAdapter(activity, imageSelected, new EditReviewImagesAdapter.Callback() {
                @Override
                public void onImageClick(Object object) {
                    //View full Image
                }

                @Override
                public void onDelete(Object object) {

                        imageSelected.remove(object);
                        if (imageSelected.size() == 0)
                            displayImagesRecycler.setVisibility(View.GONE);


                }
            }, displayImagesRecycler);
            displayImagesRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            displayImagesRecycler.setAdapter(editReviewImagesAdapter);
        } else {
            editReviewImagesAdapter.setList(imageSelected);
        }

        if (displayImagesRecycler.getVisibility() != View.VISIBLE)
            displayImagesRecycler.setVisibility(View.VISIBLE);


//        setCameraEnabled(imageSelected.size()<maxNoImages);



        if (imageSelected.size() > 0)
            displayImagesRecycler.smoothScrollToPosition(imageSelected.size());


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        product.clicklabs.jugnoo.utils.Utils.hideKeyboard(getActivity());
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.getSerializableExtra("imagesList") != null) {

                ArrayList<ImageEntry> images = (ArrayList<ImageEntry>) data.getSerializableExtra("imagesList");
                if (images != null && images.size() != 0) {


                    if (imageSelected == null)
                        imageSelected = new ArrayList<>();


                    imageSelected.addAll(images);
                    setUpImagesAdapter();
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            }
        }

    }

    public void onAddImageClick() {

        if(!canUploadImages())
            return;

        if(imageSelected!=null && imageSelected.size()>=maxNoImages) {
            Toast.makeText(activity, getString(R.string.cannot_upload_more_images), Toast.LENGTH_SHORT).show();
            return;
        }

        mPermissionCommon.getPermission(REQ_CODE_PERMISSION_IMAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);


    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionCommon.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public abstract boolean canSubmit();

    public abstract String getText();

    public abstract Integer getRestaurantId();

    private  FeedAddPostFragment.PostReviewAPIData feedAddPostData;


    public  FeedAddPostFragment.PostReviewAPIData getSubmitAPIData(){
            if(feedAddPostData==null){
                feedAddPostData = new FeedAddPostFragment.PostReviewAPIData(getRestaurantId(),imageSelected,getText(),getScore(),isAnonymousPostingEnabled());
                return feedAddPostData;
            } else{
                feedAddPostData.setContent(getText());
                feedAddPostData.setImagesSelected(imageSelected);
                feedAddPostData.setRestaurantId(getRestaurantId());
                feedAddPostData.setScore(getScore());
                feedAddPostData.setAnonymousPostingEnabled(isAnonymousPostingEnabled());
                return feedAddPostData;
            }



    }

    protected abstract Integer getScore();

    public void setSubmitActivated(boolean isEnabled){
        if(  activity.getFeedAddPostFragment()!=null && activity.getFeedAddPostFragment().getView()!=null){

             activity.getFeedAddPostFragment().setSubmitEnabled(isEnabled);
        }


    }

    public void setCameraEnabled(boolean isEnabled){
        if(  activity.getFeedAddPostFragment()!=null && activity.getFeedAddPostFragment().getView()!=null){

             activity.getFeedAddPostFragment().setCameraEnabled(isEnabled);
        }


    }

   public abstract boolean canUploadImages();

    TextWatcher editTextWacherContent = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            setSubmitActivated(s.toString().trim().length()>0);

        }
    };

    public abstract boolean submitEnabledState();

    public abstract boolean isAnonymousPostingEnabled();

    public abstract EditText getFocusEditText();

}
