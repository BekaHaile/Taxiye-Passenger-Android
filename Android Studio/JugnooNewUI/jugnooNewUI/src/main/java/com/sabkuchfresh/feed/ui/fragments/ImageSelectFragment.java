package com.sabkuchfresh.feed.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import static android.app.Activity.RESULT_OK;

/**
 * Created by Parminder Singh on 3/20/17.
 */

public abstract class ImageSelectFragment extends Fragment {


    //Images Recycler Variables
    protected String[] permissionsRequest;
    protected Picker picker;
    protected static final int REQUEST_CODE_SELECT_IMAGE = 106;
    protected EditReviewImagesAdapter editReviewImagesAdapter;
    protected RecyclerView displayImagesRecycler;
    protected ScrollView scrollView;
    public ArrayList<Object> imageSelected;
    protected FreshActivity activity;
    protected int maxNoImages;


    //This object would only be intialised if we are editing post
    protected FeedDetail feedDetail;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            Toast.makeText(activity, "Cannot upload more images", Toast.LENGTH_SHORT).show();
            return;
        }



        if (PermissionChecker.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            if (permissionsRequest == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    permissionsRequest = new String[2];
                    permissionsRequest[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                    permissionsRequest[1] = Manifest.permission.READ_EXTERNAL_STORAGE;
                }
                {
                    permissionsRequest = new String[1];
                    permissionsRequest[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                }
            }


            (ImageSelectFragment.this).requestPermissions(permissionsRequest, 20);
            return;
        }


        if (picker == null) {
            picker = new Picker.Builder(activity, R.style.AppThemePicker_NoActionBar).setPickMode(Picker.PickMode.MULTIPLE_IMAGES).build();
        }


        picker.setLimit(imageSelected == null ? maxNoImages : maxNoImages - imageSelected.size());
        picker.startActivity(ImageSelectFragment.this, activity, REQUEST_CODE_SELECT_IMAGE);
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
