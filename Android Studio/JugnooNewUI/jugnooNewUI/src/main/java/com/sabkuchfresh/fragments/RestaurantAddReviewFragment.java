package com.sabkuchfresh.fragments;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.picker.image.model.ImageEntry;
import com.picker.image.util.Picker;
import com.sabkuchfresh.adapters.EditReviewImagesAdapter;
import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.commoncalls.SendFeedbackQuery;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.sabkuchfresh.utils.ImageCompression;
import com.sabkuchfresh.utils.RatingBarMenuFeedback;

import org.json.JSONArray;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.PermissionCommon;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Shankar on 15/11/16.
 */
public class RestaurantAddReviewFragment extends Fragment implements GAAction, PermissionCommon.PermissionListener {

    private final String TAG = RestaurantAddReviewFragment.class.getSimpleName();

    private RelativeLayout rlRoot;
    private EditText etFeedback;
    private Button bSubmit;

    private View rootView;
    private FreshActivity activity;
    private int restaurantId;
    private ArrayList<FetchFeedbackResponse.ReviewImage> serverImages;
    private ArrayList<Object> objectList;
    private EditReviewImagesAdapter editReviewImagesAdapter;
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
    private TextView tvCharCount;
    private  int etReviewMaxLength;
    private View ibAccessCamera;
    private ScrollView scrollView;
    private String[] permissionsRequest;
    private int maxNoImages = 5;
    private static final int REQUEST_CODE_SELECT_IMAGES=99;
    private boolean isKeyboardOpen = true;
    private Float prefilledRating;
    private KeyboardLayoutListener.KeyBoardStateHandler mKeyBoardStateHandler;
    private final static int REQ_CODE_PERMISSION_IMAGE = 1002;
    private PermissionCommon mPermissionCommon;

    public static RestaurantAddReviewFragment newInstance(int restaurantId,float rating) {
        RestaurantAddReviewFragment fragment = new RestaurantAddReviewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_RESTAURANT_ID, restaurantId);
        bundle.putFloat(Constants.KEY_RATING, rating);
        fragment.setArguments(bundle);
        return fragment;
    }


    private void fetchArguments() {
        Bundle bundle = getArguments();
        restaurantId = bundle.getInt(Constants.KEY_RESTAURANT_ID, 0);
        prefilledRating = bundle.getFloat(Constants.KEY_RATING,0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_restaurant_add_review, container, false);
        mPermissionCommon = new PermissionCommon(this);
        fetchArguments();

        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);
        maxNoImages = activity.getReviewImageCount();
        scrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);
        rlRoot = (RelativeLayout) rootView.findViewById(R.id.rlRoot);
        try {
            if (rlRoot != null) {
                new ASSL(activity, rlRoot, 1134, 720, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        GAUtils.trackScreenView(activity.getGaCategory()+ADD_FEED);

        etReviewMaxLength = getResources().getInteger(R.integer.edt_add_review_max_length);
        etFeedback = (EditText) rootView.findViewById(R.id.etFeedback);
        etFeedback.setHint(Config.getLastOpenedClientId(activity).equals(Config.getMenusClientId())?R.string.what_you_love_about_restaurant:R.string.tell_us_about_exp);
        bSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reviewDesc = etFeedback.getText().toString().trim();
              /*  if (reviewDesc.length() == 0) {
                    etFeedback.requestFocus();
                    etFeedback.setError(activity.getString(R.string.please_enter_some_feedback));
                } else*/
              if (reviewDesc.length() > 500) {
                    etFeedback.requestFocus();
                    etFeedback.setError(activity.getString(R.string.feedback_must_be_in_500));
                }
                else if(customRatingBar.getScore() < 1){
                    Utils.showToast(activity,getString(R.string.error_no_rating));
//                    DialogPopup.alertPopup(activity, "", activity.getString(R.string.please_give_some_rating));
                }
                else {
                    submitFeedback(reviewDesc);
                    Utils.hideSoftKeyboard(activity, etFeedback);
                }
            }
        });


        mKeyBoardStateHandler = new KeyboardLayoutListener.KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {
                isKeyboardOpen= true;
            }

            @Override
            public void keyBoardClosed() {
                isKeyboardOpen= false;
            }
        };
        // register for keyboard event
        activity.registerForKeyBoardEvent(mKeyBoardStateHandler);

//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);




        activity.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                etFeedback.requestFocus();
                Utils.showSoftKeyboard(activity, etFeedback);
            }
        }, 10);

		/*
            Edited By Parminder
		 */
        //init Views
        tvCharCount= (TextView) rootView.findViewById(R.id.tv_char_count);
        dividerBelowRating = (View)rootView.findViewById(R.id.line_below_rating);
        customRatingBar=(RatingBarMenuFeedback)rootView.findViewById(R.id.rating_bar);
        displayImagesRecycler = (RecyclerView) rootView.findViewById(R.id.rvFeedImages);
        ibAccessStar = (ImageButton) rootView.findViewById(R.id.ib_access_star);
        ibAccessCamera = rootView.findViewById(R.id.ib_access_camera);
        bSubmit.setEnabled(false);
        if (objectList == null) objectList = new ArrayList<>();

        setUpImagePicker();

        setUpRatingBar();

        setupReviewEditText();

        if(prefilledRating!=null && prefilledRating>0){
            customRatingBar.setScore(prefilledRating, true);
        }
        loadDataIfEditingFeedback();


        setUpAdapter(objectList);

        updateTextCount();

        etFeedback.setSelection(etFeedback.length());

        updateSubmitButtonStatus();


        customRatingBar.setOnScoreChanged(new RatingBarMenuFeedback.IRatingBarCallbacks() {
            @Override
            public void scoreChanged(float score) {
                updateSubmitButtonStatus();
            }
        });


        return rootView;
    }

    private void setupReviewEditText() {
        etFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateSubmitButtonStatus();

                updateTextCount();
            }
        });
    }

    private void updateSubmitButtonStatus() {
        if(/*customRatingBar.getScore() > 0 && */(etFeedback.getText().toString().trim().length()>0||(objectList!=null && objectList.size()>0)))
            bSubmit.setEnabled(true);
        else
            bSubmit.setEnabled(false);
    }

    private void updateTextCount() {
        tvCharCount.setText(String.valueOf(etReviewMaxLength-etFeedback.getText().toString().length()));
    }

    private void setUpImagePicker() {

        ibAccessCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();

            }
        });
    }

    private void pickImage(){

        if(mPermissionCommon.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            etFeedback.setText(etFeedback.getText().toString().trim());
            int alreadyPresent = objectList == null ? 0 : objectList.size();
            if(picker==null){
                picker = new Picker.Builder(activity, R.style.AppThemePicker_NoActionBar).setPickMode(Picker.PickMode.MULTIPLE_IMAGES).build();
            }

            picker.setLimit(maxNoImages -alreadyPresent);
            picker.startActivity(RestaurantAddReviewFragment.this,activity,REQUEST_CODE_SELECT_IMAGES);

        }
        else {
            mPermissionCommon.getPermission(REQ_CODE_PERMISSION_IMAGE, false,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== REQUEST_CODE_SELECT_IMAGES && resultCode==RESULT_OK){
            if(data!=null && data.getSerializableExtra("imagesList")!=null)
            {

                ArrayList<ImageEntry> images = (ArrayList<ImageEntry>) data.getSerializableExtra("imagesList");
                if (images != null && images.size() != 0) {
                    objectList.addAll(images);
                    setUpAdapter(objectList);
                    scrollView.fullScroll(View.FOCUS_DOWN);


                }
            }
        }

    }

    private void loadDataIfEditingFeedback() {
        if (activity.getCurrentReview() != null){
            if(!TextUtils.isEmpty(activity.getCurrentReview().getReviewDesc()))
               etFeedback.setText(activity.getCurrentReview().getReviewDesc());

              serverImages=null;
              if( activity.getCurrentReview().getImages() != null && activity.getCurrentReview().getImages().size() != 0) {
                  serverImages = (ArrayList<FetchFeedbackResponse.ReviewImage>) activity.getCurrentReview().getImages();
        }

            if(activity.getCurrentReview().getRating()!=null && activity.getCurrentReview().getRating()>=1)
            {
                customRatingBar.setScore(activity.getCurrentReview().getRating().floatValue(), true);
            }

        }
        if (serverImages != null) {
            objectList.addAll(serverImages);
        }
    }

    private void setUpRatingBar() {
        ibAccessStar.setSelected(true);
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
                    customRatingBar.startAnimation(starCloseAnim);

                }
                else{

                    if(starOpenAnim ==null) {
                        starOpenAnim = AnimationUtils.loadAnimation(activity, R.anim.rating_review_open_anim);
                    }
                   customRatingBar.setVisibility(View.VISIBLE);
                    ibAccessStar.setSelected(true);
                    dividerBelowRating.setVisibility(View.VISIBLE);
                    customRatingBar.startAnimation(starOpenAnim);

                }

                if(handler==null){
                    handler = activity.getHandler();

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
    }

    private void setUpAdapter(final ArrayList<Object> objectList) {


        if (objectList == null || objectList.size() == 0) {
            displayImagesRecycler.setVisibility(View.GONE);
            return;
        }

        if (editReviewImagesAdapter == null) {
            editReviewImagesAdapter = new EditReviewImagesAdapter(activity, objectList, new EditReviewImagesAdapter.Callback() {
                @Override
                public void onImageClick(Object object) {
                    //View full Image
                }

                @Override
                public void onDelete(Object object) {
                    objectList.remove(object);
                    if(objectList.size()==0)
                        displayImagesRecycler.setVisibility(View.GONE);
                    ibAccessCamera.setEnabled(objectList.size()<5);
                    updateSubmitButtonStatus();

                }
            }, displayImagesRecycler);
            displayImagesRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            displayImagesRecycler.setAdapter(editReviewImagesAdapter);
        } else {
            editReviewImagesAdapter.setList(objectList);
        }

        if (displayImagesRecycler.getVisibility() != View.VISIBLE)
            displayImagesRecycler.setVisibility(View.VISIBLE);


      ibAccessCamera.setEnabled(objectList.size()<5);
       updateSubmitButtonStatus();

    if(objectList.size()>0)
        displayImagesRecycler.smoothScrollToPosition(objectList.size());


    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.registerForKeyBoardEvent(mKeyBoardStateHandler);
            activity.fragmentUISetup(this);
        }
        else {
            activity.unRegisterKeyBoardListener();
        }
    }


    private SendFeedbackQuery sendFeedbackQuery;

    private void submitFeedback(final String reviewDesc) {

    if(!MyApplication.getInstance().isOnline()){
            DialogPopup.dialogNoInternet(activity, DialogErrorType.NO_NET,
                    new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                        @Override
                        public void positiveClick(View view) {
                            submitFeedback(reviewDesc);
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
                    public void processFinish(ImageCompression.CompressedImageModel[] output) {

                        if(output!=null){
                            for(ImageCompression.CompressedImageModel file:output)
                            {
                                if(file!=null){
                                    multipartTypedOutput.addPart(Constants.KEY_REVIEW_IMAGES,new TypedFile("image/*",file.getFile()));
                                }
                            }

                        }
                        //upload feedback with new Images
                        uploadFeedback(reviewDesc,multipartTypedOutput);
                    }

                    @Override
                    public  void onError(){
                        DialogPopup.dismissLoadingDialog();

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

    private void uploadFeedback(final String reviewDesc, final MultipartTypedOutput params) {

        try {
            if (!MyApplication.getInstance().isOnline())
                 return;

            if (Config.getLastOpenedClientId(activity).equals(Config.getFreshClientId())) {
                Data.getFreshData().setPendingFeedback(0);
            } else if (Config.getLastOpenedClientId(activity).equals(Config.getMealsClientId())) {
                Data.getMealsData().setPendingFeedback(0);
            } else if (Config.getLastOpenedClientId(activity).equals(Config.getGroceryClientId())) {
                Data.getGroceryData().setPendingFeedback(0);
            } else if (Config.getLastOpenedClientId(activity).equals(Config.getMenusClientId())) {
                Data.getMenusData().setPendingFeedback(0);
            } else if (Config.getLastOpenedClientId(activity).equals(Config.getDeliveryCustomerClientId())) {
                Data.getDeliveryCustomerData().setPendingFeedback(0);
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

            //send back old images if any exist else send empty array
            JSONArray myArray = new JSONArray();
            if(gson==null)
                gson = new Gson();

                if (objectList != null && objectList.size() > 0) {



                    for (Object image : objectList) {

                        if (image instanceof FetchFeedbackResponse.ReviewImage) {
                                  myArray.put(image);
                        }


                    }

                }
            params.addPart(Constants.KEY_IMAGES, new TypedString(gson.toJson((gson.toJsonTree(myArray).getAsJsonObject().get("values")))));

             Callback<OrderHistoryResponse> callback = new Callback<OrderHistoryResponse>() {
                    @Override
                    public void success(final OrderHistoryResponse notificationInboxResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();
                        try {
                            if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {



                                if(activity.getCurrentReview()==null){
                                    if (!TextUtils.isEmpty(reviewDesc)) {
                                        GAUtils.event(activity.getGaCategory(), GAAction.ADD_FEED , GAAction.TEXT + GAAction.ADDED);
                                    }

                                    if(objectList!=null && objectList.size()>0){
                                        GAUtils.event(activity.getGaCategory(), GAAction.ADD_FEED , GAAction.PHOTO + GAAction.ADDED);
                                    }

                                    int score = Math.round(customRatingBar.getScore());
                                    if(score>=1)
                                    {
                                        GAUtils.event(activity.getGaCategory(), GAAction.ADD_FEED  + GAAction.RATING_ADDED, String.valueOf(score));

                                    }


                                        GAUtils.event(activity.getGaCategory(), GAAction.ADD_FEED , GAAction.FEED + GAAction.ADDED);

                                } else{
                                    GAUtils.event(activity.getGaCategory(), GAAction.ADD_FEED , GAAction.FEED + GAAction.EDITED);

                                }

                                activity.getVendorOpened().setHasRated(true);

                                // if we come from editing the review, set the editing flag in vendor
                                if(activity.getCurrentReview() != null){
                                    activity.getVendorOpened().setUserJustEditedReview(true);
                                }

                                activity.performBackPressed(false);
                                Utils.showToast(activity, activity.getString(R.string.thanks_for_your_valuable_feedback));
                                RestaurantReviewsListFragment frag = activity.getRestaurantReviewsListFragment();
                                if (frag != null) {
                                    frag.fetchFeedback(true);
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
                        DialogPopup.dialogNoInternet(activity, DialogErrorType.CONNECTION_LOST,
                                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                                    @Override
                                    public void positiveClick(View view) {
                                        uploadFeedback(reviewDesc, params);
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


            new HomeUtil().putDefaultParamsMultipart(params);
            if(activity.getCurrentReview()==null) {
                //Adding A new Review
                RestClient.getMenusApiService().orderFeedback(params, callback);
            }
            else
            {
                //Editing old review
                params.addPart(Constants.KEY_FEEDBACK_ID,new TypedString(activity.getCurrentReview().getFeedbackId()+""));
                RestClient.getMenusApiService().editFeedback(params, callback);
            }

        } catch (Exception e) {
            DialogPopup.dismissLoadingDialog();
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onDestroyView();
        if(imageCompressionTask!=null && !imageCompressionTask.isCancelled()) {
            imageCompressionTask.cancel(true);
        }

        ASSL.closeActivity(rlRoot);
        System.gc();
    }


    public boolean isKeyboardOpen() {
        return isKeyboardOpen;

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionCommon.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    public View getFocusEditText() {
        return etFeedback;
    }

    @Override
    public void permissionGranted(final int requestCode) {
        if(requestCode == REQ_CODE_PERMISSION_IMAGE){
            pickImage();
        }
    }

    @Override
    public void permissionDenied(final int requestCode) {

    }
}
