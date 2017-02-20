package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.Gson;

import com.sabkuchfresh.adapters.RestaurantReviewImagesAdapter1;
import com.sabkuchfresh.commoncalls.SendFeedbackQuery;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.sabkuchfresh.utils.ImageCompression;
import com.sabkuchfresh.utils.RatingBarMenuFeedback;

import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;
import java.io.File;
import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.Events;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.StringAPIService;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;


/**
 * Created by Shankar on 15/11/16.
 */
public class RestaurantAddReviewFragment extends Fragment {
    private final String TAG = RestaurantAddReviewFragment.class.getSimpleName();

    private LinearLayout rlRoot;
    private EditText etFeedback;
    private Button bSubmit;

    private View rootView;
    private FreshActivity activity;
    private int restaurantId;
    private ArrayList<FetchFeedbackResponse.ReviewImage> serverImages;
    private ArrayList<Object> objectList;
    private RestaurantReviewImagesAdapter1 restaurantReviewImagesAdapter1;
    private RecyclerView displayImagesRecycler;
    private Gson gson;
    private ImageCompression imageCompressionTask;
    private RatingBarMenuFeedback customRatingBar;
    private Picker picker;
    private ImageButton ibAccessStar;
    private Animation starOpenAnim;
    private Animation starCloseAnim;
    private Handler handler;
    private Runnable startEnableStateRunnable;
    private View dividerBelowRating;
    // TODO: 19/02/17 Show errror when submitting without entering any char


    public static RestaurantAddReviewFragment newInstance(int restaurantId) {
        RestaurantAddReviewFragment fragment = new RestaurantAddReviewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_RESTAURANT_ID, restaurantId);
        fragment.setArguments(bundle);
        return fragment;
    }


    private void fetchArguments() {
        Bundle bundle = getArguments();
        restaurantId = bundle.getInt(Constants.KEY_RESTAURANT_ID, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_restaurant_add_review, container, false);

        fetchArguments();

        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);

        rlRoot = (LinearLayout) rootView.findViewById(R.id.rlRoot);
        try {
            if (rlRoot != null) {
                new ASSL(activity, rlRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        etFeedback = (EditText) rootView.findViewById(R.id.etFeedback);
        bSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reviewDesc = etFeedback.getText().toString().trim();
                if (reviewDesc.length() == 0) {
                    etFeedback.requestFocus();
                    etFeedback.setError(activity.getString(R.string.please_enter_some_feedback));
                } else if (reviewDesc.length() > 500) {
                    etFeedback.requestFocus();
                    etFeedback.setError(activity.getString(R.string.feedback_must_be_in_500));
                } else {
                    submitFeedback(reviewDesc);
                }
            }
        });

        rlRoot.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(rlRoot, null, new KeyboardLayoutListener.KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {
            }

            @Override
            public void keyBoardClosed() {
            }
        }));

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        etFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                bSubmit.setEnabled(s.length() > 0);
            }
        });
        bSubmit.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                etFeedback.requestFocus();
                Utils.showSoftKeyboard(activity, etFeedback);
            }
        }, 10);

		/*
            Edited By Parminder
		 */

        dividerBelowRating = (View)rootView.findViewById(R.id.line_below_rating);
        customRatingBar=(RatingBarMenuFeedback)rootView.findViewById(R.id.rating_bar);
        displayImagesRecycler = (RecyclerView) rootView.findViewById(R.id.rvFeedImages);
        ibAccessStar = (ImageButton) rootView.findViewById(R.id.ib_access_star);
        rootView.findViewById(R.id.ib_access_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int alreadyPresent = objectList == null ? 0 : objectList.size();

                if(picker==null){
                    picker = new Picker.Builder(activity, new Picker.PickListener() {
                        @Override
                        public void onPickedSuccessfully(ArrayList<ImageEntry> images) {
                            if (images != null && images.size() != 0) {
                                objectList.addAll(images);
                                setUpAdapter(objectList);
                            }
                        }

                        @Override
                        public void onCancel() {

                        }
                    }, R.style.AppThemePicker_NoActionBar)
                            .setPickMode(Picker.PickMode.MULTIPLE_IMAGES)
                            .build();
                }

                picker.setLimit(5-alreadyPresent);
                picker.startActivity();

            }
        });


        ibAccessStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(customRatingBar.getVisibility()==View.VISIBLE)
                {
                    if( starCloseAnim ==null) {
                        starCloseAnim = AnimationUtils.loadAnimation(activity, R.anim.rating_review_close_anim);
                    }
                    ibAccessStar.setSelected(false);
                    customRatingBar.setVisibility(View.GONE);
                    dividerBelowRating.setVisibility(View.GONE);
//                    etFeedback.animate().translationYBy(-(ibAccessStar.getHeight())).setDuration(400).start();
                    customRatingBar.startAnimation(starCloseAnim);

                }
                else{

                    if(starOpenAnim ==null) {
                        starOpenAnim = AnimationUtils.loadAnimation(activity, R.anim.rating_review_open_anim);
                    }
                   customRatingBar.setVisibility(View.VISIBLE);
                    ibAccessStar.setSelected(true);
                    dividerBelowRating.setVisibility(View.VISIBLE);
//                    etFeedback.animate().translationYBy(ibAccessStar.getHeight()).setDuration(400).start();
                    customRatingBar.startAnimation(starOpenAnim);

                }

                if(handler==null){
                    handler = new Handler();

                }
                if(startEnableStateRunnable==null){
                    startEnableStateRunnable = new Runnable() {
                        @Override
                        public void run() {
                            ibAccessStar.setEnabled(true);
                        }
                    };
                }


                ibAccessStar.setEnabled(false);
                handler.postDelayed(startEnableStateRunnable,400);
            }
        });


        if (objectList == null) objectList = new ArrayList<>();


        if (activity.getCurrentReview() != null
                && activity.getCurrentReview().getImages() != null
                && activity.getCurrentReview().getImages().size() != 0) {
            serverImages = (ArrayList<FetchFeedbackResponse.ReviewImage>) activity.getCurrentReview().getImages();
        }
        if (serverImages != null) {
            objectList.addAll(serverImages);
        }

        setUpAdapter(objectList);
        return rootView;
    }

    private void setUpAdapter(final ArrayList<Object> objectList) {


        if (objectList == null || objectList.size() == 0) {
            displayImagesRecycler.setVisibility(View.GONE);
            return;
        }

        if (restaurantReviewImagesAdapter1 == null) {
            restaurantReviewImagesAdapter1 = new RestaurantReviewImagesAdapter1(activity, objectList, new RestaurantReviewImagesAdapter1.Callback() {
                @Override
                public void onImageClick(Object object) {
                    //View full Image
                }

                @Override
                public void onDelete(Object object) {
                    objectList.remove(object);
                    if(objectList.size()==0)
                        displayImagesRecycler.setVisibility(View.GONE);

                }
            });
            displayImagesRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            displayImagesRecycler.setAdapter(restaurantReviewImagesAdapter1);
        } else {
            restaurantReviewImagesAdapter1.setList(objectList);
        }

        if (displayImagesRecycler.getVisibility() != View.VISIBLE)
            displayImagesRecycler.setVisibility(View.VISIBLE);


    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
        }
    }


    private SendFeedbackQuery sendFeedbackQuery;

    private void submitFeedback(final String reviewDesc) {

    if(!MyApplication.getInstance().isOnline()){
            DialogPopup.dialogNoInternet(activity, DialogErrorType.NO_NET,
                    new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                        @Override
                        public void positiveClick(View view) {
                        }

                        @Override
                        public void neutralClick(View view) {

                        }

                        @Override
                        public void negativeClick(View view) {

                        }
                    });

            return;
        }



        DialogPopup.showLoadingDialog(activity, "");
        if (sendFeedbackQuery == null) {
            sendFeedbackQuery = new SendFeedbackQuery();
        }

        final MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();

        if(objectList!=null && objectList.size()>0){

      //Compress Images if any new added
            ArrayList<String> imageEntries =null;
            for(Object image:objectList){
                if(image instanceof ImageEntry){
                    if(imageEntries==null)
                        imageEntries= new ArrayList<>();

                    imageEntries.add(((ImageEntry) image).path);
                }
            }

            if(imageEntries!=null){
                //upload feedback with new Images
                imageCompressionTask = new ImageCompression(new ImageCompression.AsyncResponse() {
                    @Override
                    public void processFinish(File[] output) {

                        if(output!=null){
                            for(File file:output)
                            {
                                if(file!=null){
                                    multipartTypedOutput.addPart(Constants.KEY_REVIEW_IMAGES,new TypedFile("image/*",file));
                                }
                            }

                        }
                        //upload feedback with new Images
                        uploadFeedback(reviewDesc,multipartTypedOutput);
                    }

                    @Override
                    public  void onError(){
                        DialogPopup.dismissLoadingDialog();
                        // TODO: 19/02/17 Show Error for not compressing

                    }
                },activity);
                imageCompressionTask.execute(imageEntries.toArray(new String[imageEntries.size()]));
            }
            else{
                //No new Images added upload feedback with old Images if exist
                uploadFeedback(reviewDesc,multipartTypedOutput);
            }

        }
        else{
            uploadFeedback(reviewDesc,multipartTypedOutput);
            //upload feedback with old Images if exist
        }



    }

    private void uploadFeedback(String reviewDesc,MultipartTypedOutput params) {

        try {
            if (!MyApplication.getInstance().isOnline())
                 return;

            if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getFreshClientId())) {
                Data.getFreshData().setPendingFeedback(0);
            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMealsClientId())) {
                Data.getMealsData().setPendingFeedback(0);
            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getGroceryClientId())) {
                Data.getGroceryData().setPendingFeedback(0);
            } else if (Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId()).equals(Config.getMenusClientId())) {
                Data.getMenusData().setPendingFeedback(0);
            }




            params.addPart(Constants.KEY_ACCESS_TOKEN, new TypedString(Data.userData.accessToken));
            params.addPart(Constants.RATING_TYPE, new TypedString(Constants.RATING_TYPE_STAR));
            params.addPart(Constants.INTERATED, new TypedString("1"));


            int score = Math.round(customRatingBar.getScore());
            if(score>=1)
                params.addPart(Constants.RATING, new TypedString(String.valueOf(score)));



          if (!TextUtils.isEmpty(reviewDesc)) {
                    params.addPart(Constants.KEY_REVIEW_DESC, new TypedString(reviewDesc));
          }


         if (restaurantId > 0) {
                    params.addPart(Constants.KEY_RESTAURANT_ID, new TypedString(String.valueOf(restaurantId)));
           }

         params.addPart(Constants.KEY_CLIENT_ID, new TypedString("" + Prefs.with(activity).getString(Constants.KEY_SP_LAST_OPENED_CLIENT_ID, Config.getFreshClientId())));


                //send back old images if any exist
                if (objectList != null && objectList.size() > 0) {


                    for (Object image : objectList) {


                        if (image instanceof FetchFeedbackResponse.ReviewImage) {

                            if(gson==null)
                                 gson = new Gson();

                            params.addPart(Constants.KEY_IMAGES, new TypedString(gson.toJson(image)));

                        }


                    }

                }

                Callback<OrderHistoryResponse> callback = new Callback<OrderHistoryResponse>() {
                    @Override
                    public void success(final OrderHistoryResponse notificationInboxResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                FlurryEventLogger.eventGA(Events.MENUS, Events.REVIEW, Events.SUBMITTED);
                                activity.performBackPressed();
                                Utils.showToast(activity, activity.getString(R.string.thanks_for_your_valuable_feedback));
                                RestaurantReviewsListFragment frag = activity.getRestaurantReviewsListFragment();
                                if (frag != null) {
                                    frag.fetchFeedback();
                                }
                            } else {
                                DialogPopup.alertPopup(activity, "", notificationInboxResponse.getMessage());
                            }
                        } catch (Exception e) {
                            DialogPopup.dismissLoadingDialog();
                             e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.dialogNoInternet(activity, DialogErrorType.NO_NET,
                                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                                    @Override
                                    public void positiveClick(View view) {
                                    }

                                    @Override
                                    public void neutralClick(View view) {

                                    }

                                    @Override
                                    public void negativeClick(View view) {

                                    }
                                });
//                        retryDialog(DialogErrorType.CONNECTION_LOST, productType, activity, orderId, restaurantId, rating, ratingType, comments, reviewDesc, feedbackResultListener);
                    }
                };

                //hit finally
                RestClient.getMenusApiService().orderFeedback(params, callback);

        } catch (Exception e) {
            DialogPopup.dismissLoadingDialog();
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onDestroyView();
        if(imageCompressionTask!=null && !imageCompressionTask.isCancelled()) {
            imageCompressionTask.cancel(true);
        }

        ASSL.closeActivity(rlRoot);
        System.gc();
    }

}
