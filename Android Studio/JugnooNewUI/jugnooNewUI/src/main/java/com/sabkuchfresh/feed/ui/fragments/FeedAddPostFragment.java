package com.sabkuchfresh.feed.ui.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.picker.image.model.ImageEntry;
import com.sabkuchfresh.feed.ui.adapters.FeedAddPostPagerAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.utils.ImageCompression;

import java.io.File;
import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by Shankar on 15/11/16.
 */
public class FeedAddPostFragment extends Fragment implements View.OnClickListener {
    private FreshActivity activity;
    private TextView tvReview, tvAsk;
    private View vReviewSelected, vAskSelected;
    private ImageView ivReview, ivAsk;
    private ViewPager viewPager;
    private FeedAddPostPagerAdapter feedAddPostPagerAdapter;
    public static final int NOT_APPLICABLE = -1;
    private Button btnSubmit;
    private ImageView ivAccessCamera;


    public FeedAddPostFragment() {

    }


    public static FeedAddPostFragment newInstance() {
        FeedAddPostFragment fragment = new FeedAddPostFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed_add_post, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);
        RelativeLayout rlReview = (RelativeLayout) rootView.findViewById(R.id.rlReview);
        RelativeLayout rlAsk = (RelativeLayout) rootView.findViewById(R.id.rlAsk);
        tvReview = (TextView) rootView.findViewById(R.id.tvReview);
        tvReview.setTypeface(tvReview.getTypeface(), Typeface.BOLD);
        tvAsk = (TextView) rootView.findViewById(R.id.tvAsk);
        tvAsk.setTypeface(tvAsk.getTypeface(), Typeface.BOLD);
        vReviewSelected = rootView.findViewById(R.id.vReviewSelected);
        vAskSelected = rootView.findViewById(R.id.vAskSelected);
        ivReview = (ImageView) rootView.findViewById(R.id.ivReview);
        ivAsk = (ImageView) rootView.findViewById(R.id.ivAsk);
        ivAccessCamera = (ImageView) rootView.findViewById(R.id.ivAccessCamera);
        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        rlReview.setOnClickListener(this);
        rlAsk.setOnClickListener(this);
        feedAddPostPagerAdapter = new FeedAddPostPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(feedAddPostPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                switchAddFeed(position);
//                ivAccessCamera.setEnabled(getVisibleFragment().canUploadImages());
                btnSubmit.setActivated(getVisibleFragment().submitEnabledState());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        ivAccessCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getVisibleFragment().onAddImageClick();


            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getVisibleFragment().canSubmit()) {
                    Utils.hideKeyboard(activity);
                    PostReviewAPIData postReviewAPIData = getVisibleFragment().getSubmitAPIData();
                    postFeedAPI(postReviewAPIData.getContent(), postReviewAPIData.getImagesSelected(), postReviewAPIData.getRestaurantId(), postReviewAPIData.getScore());

                }

            }
        });



        return rootView;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlReview:
                viewPager.setCurrentItem(1);

                break;

            case R.id.rlAsk:
                viewPager.setCurrentItem(0);
                break;

        }
    }

    private void switchAddFeed(int pos) {

        switch (pos) {
            case 1:
                tvReview.setTextColor(ContextCompat.getColor(activity, R.color.text_color));
                tvAsk.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
                vReviewSelected.setVisibility(View.VISIBLE);
                vAskSelected.setVisibility(View.GONE);
                ivReview.setImageResource(R.drawable.ic_feed_star);
                ivAsk.setImageResource(R.drawable.ic_feed_ask_disabled);


                break;

            case 0:
                tvReview.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
                tvAsk.setTextColor(ContextCompat.getColor(activity, R.color.text_color));
                vReviewSelected.setVisibility(View.GONE);
                vAskSelected.setVisibility(View.VISIBLE);
                ivReview.setImageResource(R.drawable.ic_feed_star_disable);
                ivAsk.setImageResource(R.drawable.ic_feed_ask);


                break;
        }

    }


    public void postFeedAPI(final String postText, final ArrayList<ImageEntry> imageSelected, final int restId, final int ratingScore) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                final MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
                DialogPopup.showLoadingDialog(getActivity(), getActivity().getResources().getString(R.string.loading));

                if (imageSelected != null && imageSelected.size() != 0) {
                    //upload feedback with new Images
                    ImageCompression imageCompressionTask = new ImageCompression(new ImageCompression.AsyncResponse() {
                        @Override
                        public void processFinish(File[] output) {

                            if (output != null) {
                                for (File file : output) {
                                    if (file != null) {
                                        multipartTypedOutput.addPart(Constants.KEY_REVIEW_IMAGES, new TypedFile("image/*", file));
                                    }
                                }

                            }
                            //upload feedback with new Images
                            uploadParamsAndPost(multipartTypedOutput, postText, restId, ratingScore);
                        }

                        @Override
                        public void onError() {
                            DialogPopup.dismissLoadingDialog();

                        }
                    }, activity);


                    String[] imagesToCompress = new String[imageSelected.size()];
                    for (int i = 0; i < imageSelected.size(); i++) {

                        imagesToCompress[i] = imageSelected.get(i).path;
                    }
                    imageCompressionTask.execute(imagesToCompress);
                } else {
                    uploadParamsAndPost(multipartTypedOutput, postText, restId, ratingScore);
                }
            } else {

                DialogPopup.dialogNoInternet(activity, DialogErrorType.NO_NET,
                        new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                            @Override
                            public void positiveClick(View view) {
                                postFeedAPI(postText, imageSelected, restId, ratingScore);
                            }

                            @Override
                            public void neutralClick(View view) {

                            }

                            @Override
                            public void negativeClick(View view) {

                            }
                        });


            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void postFeedAPI(final String postText, ArrayList<ImageEntry> imageSelected) {
        postFeedAPI(postText, imageSelected, NOT_APPLICABLE, NOT_APPLICABLE);
    }

    private void uploadParamsAndPost(final MultipartTypedOutput multipartTypedOutput, final String postText, final int restId, final int rating) {

        multipartTypedOutput.addPart(Constants.KEY_ACCESS_TOKEN, new TypedString(Data.userData.accessToken));
        multipartTypedOutput.addPart(Constants.KEY_LATITUDE, new TypedString(String.valueOf(activity.getSelectedLatLng().latitude)));
        multipartTypedOutput.addPart(Constants.KEY_LONGITUDE, new TypedString(String.valueOf(activity.getSelectedLatLng().longitude)));
        multipartTypedOutput.addPart(Constants.KEY_POST_TEXT, new TypedString(postText));


        if (restId != NOT_APPLICABLE) {
            multipartTypedOutput.addPart(Constants.KEY_RESTAURANT_ID, new TypedString(String.valueOf(restId)));
            if (rating > 0)
                multipartTypedOutput.addPart(Constants.KEY_STAR_COUNT, new TypedString(String.valueOf(rating)));
        }


        multipartTypedOutput.addPart(Constants.KEY_APP_VERSION, new TypedString(String.valueOf(MyApplication.getInstance().appVersion())));
        multipartTypedOutput.addPart(Constants.KEY_DEVICE_TYPE, new TypedString(Data.DEVICE_TYPE));

        RestClient.getFeedApiService().postFeed(multipartTypedOutput, new retrofit.Callback<SettleUserDebt>() {
            @Override
            public void success(SettleUserDebt feedbackResponse, Response response) {
                String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                DialogPopup.dismissLoadingDialog();

                try {
                    String message = feedbackResponse.getMessage();
                    if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, feedbackResponse.getFlag(),
                            feedbackResponse.getError(), feedbackResponse.getMessage())) {
                        if (feedbackResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                            activity.performBackPressed(false);
                            if (activity.getFeedHomeFragment() != null && activity.getFeedHomeFragment().getView() != null) {
                                activity.getFeedHomeFragment().fetchFeedsApi(true);
                            }
                        } else {
                            DialogPopup.alertPopup(activity, "", message);
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                    retryDialog(DialogErrorType.SERVER_ERROR, multipartTypedOutput, postText, restId, rating);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                DialogPopup.dismissLoadingDialog();
                retryDialog(DialogErrorType.CONNECTION_LOST, multipartTypedOutput, postText, restId, rating);

            }
        });
    }

    private void retryDialog(DialogErrorType dialogErrorType, final MultipartTypedOutput multipartTypedOutput, final String postText, final int restId, final int ratingScore) {
        DialogPopup.dialogNoInternet(activity, dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        uploadParamsAndPost(multipartTypedOutput, postText, restId, ratingScore);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {

                    }
                });
    }

    public ImageSelectFragment getVisibleFragment() {
        return ((ImageSelectFragment) feedAddPostPagerAdapter.getItem(viewPager.getCurrentItem()));
    }


    public static class PostReviewAPIData {
        private Integer restaurantId;
        private ArrayList<ImageEntry> imagesSelected;
        private String content;
        private Integer score;


        public PostReviewAPIData(Integer restaurantId, ArrayList<ImageEntry> imagesSelected, String content, Integer score) {
            this.restaurantId = restaurantId;
            this.imagesSelected = imagesSelected;
            this.content = content;
            this.score = score;
        }

        public Integer getRestaurantId() {
            return restaurantId;
        }

        public void setRestaurantId(Integer restaurantId) {
            this.restaurantId = restaurantId;
        }

        public ArrayList<ImageEntry> getImagesSelected() {
            return imagesSelected;
        }

        public void setImagesSelected(ArrayList<ImageEntry> imagesSelected) {
            this.imagesSelected = imagesSelected;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }
    }

    public void setSubmitEnabled(boolean isEnable) {
        if (getView() != null && btnSubmit != null) {
            btnSubmit.setActivated(isEnable);
        }
    }

    public void setCameraEnabled(boolean isEnable) {
        if (getView() != null && ivAccessCamera != null) {
            ivAccessCamera.setEnabled(isEnable);
        }
    }
}
