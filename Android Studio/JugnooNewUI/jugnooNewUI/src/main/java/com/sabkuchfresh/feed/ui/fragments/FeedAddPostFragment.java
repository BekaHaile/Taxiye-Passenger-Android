package com.sabkuchfresh.feed.ui.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.picker.image.model.ImageEntry;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.feed.ui.adapters.FeedAddPostPagerAdapter;
import com.sabkuchfresh.feed.utils.FeedUtils;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.sabkuchfresh.utils.ImageCompression;
import com.sabkuchfresh.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.DialogPopup;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by Shankar on 15/11/16.
 */
public class FeedAddPostFragment extends Fragment implements View.OnClickListener, GAAction {
    private FreshActivity activity;
    private TextView tvReview, tvAsk;
    private View vReviewSelected, vAskSelected;
    private ImageView ivReview, ivAsk;
    private ViewPager viewPager;
    private FeedAddPostPagerAdapter feedAddPostPagerAdapter;
    public static final int NOT_APPLICABLE = -1;
    private Button btnSubmit;
    private ImageView ivAccessCamera;
    public static final String FEED_DETAIL = "feed_detail";
    private FeedDetail feedDetail;
    private boolean isEditingPost;
    private SwitchCompat switchAnonymousPost;
    private TextView labelAnonymousSwitch;
    private String handleName = "";


    public FeedAddPostFragment() {

    }


    public static FeedAddPostFragment newInstance(FeedDetail feedDetail) {
        FeedAddPostFragment fragment = new FeedAddPostFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FEED_DETAIL, feedDetail);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(FEED_DETAIL)) {
                this.feedDetail = (FeedDetail) getArguments().getSerializable(FEED_DETAIL);
                isEditingPost = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed_add_post, container, false);
        GAUtils.trackScreenView(GAAction.FEED + GAAction.ADD_POST);
        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);
        RelativeLayout rlReview = (RelativeLayout) rootView.findViewById(R.id.rlReview);
        RelativeLayout rlAsk = (RelativeLayout) rootView.findViewById(R.id.rlAsk);
        switchAnonymousPost = (SwitchCompat) rootView.findViewById(R.id.switch_handle);
        labelAnonymousSwitch = (TextView) rootView.findViewById(R.id.tv_switch_label);
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
        feedAddPostPagerAdapter = new FeedAddPostPagerAdapter(getChildFragmentManager(), feedDetail);
        viewPager.setAdapter(feedAddPostPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchAddFeed(position);
                toggleAnonymousPosting(getVisibleFragment().isAnonymousPostingEnabled());
                btnSubmit.setActivated(getVisibleFragment().submitEnabledState());

                activity.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getVisibleFragment().getFocusEditText().requestFocus();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 100);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        ivAccessCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Utils.hideSoftKeyboard(activity, getVisibleFragment().getFocusEditText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getVisibleFragment().onAddImageClick();


            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getVisibleFragment().canSubmit()) {
                    product.clicklabs.jugnoo.utils.Utils.hideKeyboard(activity);
                    PostReviewAPIData postReviewAPIData = getVisibleFragment().getSubmitAPIData();
                    postFeedAPI(postReviewAPIData.getContent(), postReviewAPIData.getImagesSelected(),
                            postReviewAPIData.getRestaurantId(), postReviewAPIData.getScore(),
                            postReviewAPIData.isAnonymousPostingEnabled());
                    GAUtils.event(FEED, ADD+POST, SUBMIT+CLICKED);
                }

            }
        });

        if (isEditingPost) {
            rootView.findViewById(R.id.llTabs).setVisibility(View.GONE);
            rootView.findViewById(R.id.id_top_line).setVisibility(View.GONE);
            switchAnonymousPost.setVisibility(View.GONE);
            labelAnonymousSwitch.setVisibility(View.GONE);
            if (feedDetail != null) {


                if (feedDetail.getFeedType() == FeedDetail.FeedType.POST)
                    activity.getTopBar().title.setText(R.string.edit_post);
                else if (feedDetail.getFeedType() == FeedDetail.FeedType.REVIEW)
                    activity.getTopBar().title.setText(R.string.edit_review);

            }

        } else {
            if (Data.getFeedData() != null) {
                int visibility = Data.getFeedData().getAnonymousFunctionalityEnabled() == 1 ? View.VISIBLE : View.GONE;
                labelAnonymousSwitch.setVisibility(visibility);
                switchAnonymousPost.setVisibility(visibility);
                if (!TextUtils.isEmpty(Data.getFeedData().getHandleName())) {
                    handleName = Data.getFeedData().getHandleName();
                }
            }
            switchAddFeed(0);
        }

        labelAnonymousSwitch.setTypeface(labelAnonymousSwitch.getTypeface(), Typeface.BOLD);
        switchAnonymousPost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                labelAnonymousSwitch.setText(isChecked ? handleName : activity.getString(R.string.label_anonymous));
            }
        });
        switchAnonymousPost.setChecked(true);

        activity.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    getVisibleFragment().getFocusEditText().requestFocus();
                    product.clicklabs.jugnoo.utils.Utils.showKeyboard(activity, getVisibleFragment().getFocusEditText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 200);

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
                GAUtils.event(FEED, ADD+POST, REVIEW+CLICKED);
                break;

            case R.id.rlAsk:
                viewPager.setCurrentItem(0);
                GAUtils.event(FEED, ADD+POST, ASK+CLICKED);
                break;

        }
    }

    private void switchAddFeed(int pos) {

        switch (pos) {
            case 1:
                tvReview.setTextColor(ContextCompat.getColor(activity,R.color.text_color));
                tvAsk.getPaint().setShader(null);
                tvAsk.setTextColor(ContextCompat.getColor(activity,R.color.text_color_30alpha));
              /*  tvReview.setTextColor(ContextCompat.getColor(activity, R.color.text_color));
                tvAsk.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));*/
                vReviewSelected.setVisibility(View.VISIBLE);
                vAskSelected.setVisibility(View.GONE);
                ivReview.setImageResource(R.drawable.ic_feed_star);
                ivAsk.setImageResource(R.drawable.ic_feed_ask_disabled);


                break;

            case 0:
                tvAsk.setTextColor(ContextCompat.getColor(activity,R.color.text_color));
                tvReview.getPaint().setShader(null);
                tvReview.setTextColor(ContextCompat.getColor(activity,R.color.text_color_30alpha));
               /* tvReview.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
                tvAsk.setTextColor(ContextCompat.getColor(activity, R.color.text_color));*/
                vReviewSelected.setVisibility(View.GONE);
                vAskSelected.setVisibility(View.VISIBLE);
                ivReview.setImageResource(R.drawable.ic_feed_star_disable);
                ivAsk.setImageResource(R.drawable.ic_feed_ask);


                break;
        }

    }


    public void postFeedAPI(final String postText, final ArrayList<Object> images, final int restId,
                            final int ratingScore, final boolean anonymousPostingEnabled) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                final MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
                DialogPopup.showLoadingDialog(getActivity(), getActivity().getResources().getString(R.string.loading));

                if (images == null || images.size() == 0) {
                    uploadParamsAndPost(multipartTypedOutput, postText, restId, ratingScore, anonymousPostingEnabled, 0);
                    return;
                }


                ArrayList<ImageEntry> imageSelected = new ArrayList<>();//New images added these will be first compressed and then sent
                JSONArray reviewImages = new JSONArray();//Server images will be sent back as it is in array

                for (Object object : images) {
                    if (object instanceof ImageEntry) {
                        imageSelected.add((ImageEntry) object);
                    } else if (object instanceof FetchFeedbackResponse.ReviewImage) {

                        reviewImages.put(object);
                    }
                }

                if (reviewImages.length() > 0) {
                    //send back old images if any exist else send empty array
                    multipartTypedOutput.addPart(Constants.KEY_IMAGES, new TypedString(FeedUtils.getGson().toJson((FeedUtils.getGson().toJsonTree(reviewImages).getAsJsonObject().get("values")))));
                }

                if (imageSelected.size() == 0) {
                    uploadParamsAndPost(multipartTypedOutput, postText, restId, ratingScore, anonymousPostingEnabled, 0);
                    return;
                }

                ImageCompression imageCompressionTask = new ImageCompression(new ImageCompression.AsyncResponse() {
                    @Override
                    public void processFinish(ImageCompression.CompressedImageModel[] output) {

                        if (output != null) {
                            JSONArray imageSize = new JSONArray();
                            for (ImageCompression.CompressedImageModel file : output) {

                                if (file != null) {
                                    multipartTypedOutput.addPart(Constants.KEY_REVIEW_IMAGES, new TypedFile("image/*", file.getFile()));
                                    imageSize.put(new ImageSize(file.getWidth(),file.getSize()));

                                }
                            }
                            if(imageSize.length()>0){
                                multipartTypedOutput.addPart("image_size", new TypedString(FeedUtils.getGson().toJson((FeedUtils.getGson().toJsonTree(imageSize).getAsJsonObject().get("values")))));

                            }

                        }
                        //upload feedback with new Images
                        uploadParamsAndPost(multipartTypedOutput, postText, restId, ratingScore, anonymousPostingEnabled, output != null ? output.length : 0);
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

                DialogPopup.dialogNoInternet(activity, DialogErrorType.NO_NET,
                        new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                            @Override
                            public void positiveClick(View view) {
                                postFeedAPI(postText, images, restId, ratingScore, anonymousPostingEnabled);
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
            DialogPopup.dismissLoadingDialog();
        }
    }


    public class ImageSize{
        @SerializedName("width")
        int width;
        @SerializedName("height")
        int height;

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
    private void uploadParamsAndPost(final MultipartTypedOutput multipartTypedOutput, final String postText,
                                     final int restId, final int rating, final boolean anonymousPostingEnabled, final int imagesCount) {

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


        retrofit.Callback<SettleUserDebt> APICallBack = new retrofit.Callback<SettleUserDebt>() {
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
                                activity.getFeedHomeFragment().fetchFeedsApi(true, true, false);
                            }
                            if (activity.getTopFragment() instanceof FeedPostDetailFragment) {
                                activity.getOfferingsCommentFragment().fetchDetailAPI();
                            }

                            if (imagesCount > 0) {
                                GAUtils.event(FEED, ADD + POST, IMAGE + UPLOADED);
                            }
                        } else {
                            DialogPopup.alertPopup(activity, "", message);
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                    retryDialog(DialogErrorType.SERVER_ERROR, multipartTypedOutput, postText, restId, rating, anonymousPostingEnabled, imagesCount);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                DialogPopup.dismissLoadingDialog();
                retryDialog(DialogErrorType.CONNECTION_LOST, multipartTypedOutput, postText, restId, rating, anonymousPostingEnabled, imagesCount);

            }
        };
        new HomeUtil().putDefaultParamsMultipart(multipartTypedOutput);
        if (isEditingPost) {
            multipartTypedOutput.addPart(Constants.KEY_POST_ID, new TypedString(String.valueOf(feedDetail.getPostId())));
            RestClient.getFeedApiService().editFeed(multipartTypedOutput, APICallBack);

        } else {
            if (anonymousPostingEnabled) {
                multipartTypedOutput.addPart(Constants.IS_ANONYMOUS, new TypedString(String.valueOf(switchAnonymousPost.isChecked() ? "0" : "1")));
            }

            RestClient.getFeedApiService().postFeed(multipartTypedOutput, APICallBack);

        }
    }

    private void retryDialog(DialogErrorType dialogErrorType, final MultipartTypedOutput multipartTypedOutput, final String postText, final int restId, final int ratingScore, final boolean anonymousPostingEnabled, final int imagesCount) {
        DialogPopup.dialogNoInternet(activity, dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        uploadParamsAndPost(multipartTypedOutput, postText, restId, ratingScore, anonymousPostingEnabled, imagesCount);
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
        private ArrayList<Object> imagesSelected;
        private String content;
        private Integer score;
        private boolean AnonymousPostingEnabled;

        public boolean isAnonymousPostingEnabled() {
            return AnonymousPostingEnabled;
        }

        public void setAnonymousPostingEnabled(boolean anonymousPostingEnabled) {
            AnonymousPostingEnabled = anonymousPostingEnabled;
        }

        public PostReviewAPIData(Integer restaurantId, ArrayList<Object> imagesSelected, String content, Integer score, boolean anonymousPostingEnabled) {
            this.restaurantId = restaurantId;
            this.imagesSelected = imagesSelected;
            this.content = content;
            this.score = score;
            this.AnonymousPostingEnabled = anonymousPostingEnabled;
        }

        public Integer getRestaurantId() {
            return restaurantId;
        }

        public void setRestaurantId(Integer restaurantId) {
            this.restaurantId = restaurantId;
        }

        public ArrayList<Object> getImagesSelected() {
            return imagesSelected;
        }

        public void setImagesSelected(ArrayList<Object> imagesSelected) {
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

    public void toggleAnonymousPosting(boolean isEnable) {
        if (isEnable && !isEditingPost
                && Data.getFeedData() != null
                && Data.getFeedData().getAnonymousFunctionalityEnabled() == 1) {
            labelAnonymousSwitch.setVisibility(View.VISIBLE);
            switchAnonymousPost.setVisibility(View.VISIBLE);
        } else {

            labelAnonymousSwitch.setVisibility(View.GONE);
            switchAnonymousPost.setVisibility(View.GONE);
        }
    }
}
