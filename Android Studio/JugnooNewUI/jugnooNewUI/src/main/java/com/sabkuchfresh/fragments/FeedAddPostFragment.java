package com.sabkuchfresh.fragments;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.picker.image.model.ImageEntry;
import com.picker.image.util.Picker;
import com.sabkuchfresh.adapters.EditReviewImagesAdapter;
import com.sabkuchfresh.adapters.RestaurantQuerySuggestionsAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.SuggestRestaurantQueryResp;
import com.sabkuchfresh.utils.ImageCompression;
import com.sabkuchfresh.utils.RatingBarMenuFeedback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
import product.clicklabs.jugnoo.utils.ProgressWheel;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Shankar on 15/11/16.
 */
public class FeedAddPostFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout rlReview, rlAsk;
    private TextView tvReview, tvAsk;
    private View vReviewSelected, vAskSelected;
    private ImageView ivReview, ivAsk;
    private LinearLayout llReviewLocation;
    private TextView tvRestaurantLocation;
    private EditText etRestaurantLocation;
    private ProgressWheel pwRestLocQuery;
    private RecyclerView rvRestaurantSuggestions;
    private RestaurantQuerySuggestionsAdapter suggestionsAdapter;
    private EditText etContent;
    private ImageView ivAccessCamera;
    private Button btnSubmit;
    private TextView tvCharCount;
    private RelativeLayout rlSelectRestaurant;

    private View rootView;
    private FreshActivity activity;
    private AddPostType addPostType = AddPostType.ASK;
    private ArrayList<SuggestRestaurantQueryResp.Suggestion> suggestions;
    private SuggestRestaurantQueryResp.Suggestion suggestionSelected;

    private RatingBarMenuFeedback ratingBar;
    private String[] permissionsRequest;
    private Picker picker;
    private static final int REQUEST_CODE_SELECT_IMAGE = 106;

    private ImageCompression imageCompressionTask;
    private EditReviewImagesAdapter editReviewImagesAdapter;
    private RecyclerView displayImagesRecycler;
    private ScrollView scrollView;
    private ArrayList<ImageEntry> imageSelected;

    public FeedAddPostFragment() {
    }


    public static FeedAddPostFragment newInstance() {
        FeedAddPostFragment fragment = new FeedAddPostFragment();
        Bundle bundle = new Bundle();

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // fetch arguments here
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_feed_add_post, container, false);

        activity = (FreshActivity) getActivity();
        activity.fragmentUISetup(this);

        displayImagesRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_view_photos);
        scrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);
        rlReview = (RelativeLayout) rootView.findViewById(R.id.rlReview);
        rlAsk = (RelativeLayout) rootView.findViewById(R.id.rlAsk);
        tvReview = (TextView) rootView.findViewById(R.id.tvReview);
        tvReview.setTypeface(tvReview.getTypeface(), Typeface.BOLD);
        tvAsk = (TextView) rootView.findViewById(R.id.tvAsk);
        tvAsk.setTypeface(tvAsk.getTypeface(), Typeface.BOLD);
        vReviewSelected = rootView.findViewById(R.id.vReviewSelected);
        vAskSelected = rootView.findViewById(R.id.vAskSelected);
        ivReview = (ImageView) rootView.findViewById(R.id.ivReview);
        ivAsk = (ImageView) rootView.findViewById(R.id.ivAsk);
        llReviewLocation = (LinearLayout) rootView.findViewById(R.id.llReviewLocation);
        tvRestaurantLocation = (TextView) rootView.findViewById(R.id.tvRestaurantLocation);
        etRestaurantLocation = (EditText) rootView.findViewById(R.id.etRestaurantLocation);
        pwRestLocQuery = (ProgressWheel) rootView.findViewById(R.id.pwRestLocQuery);
        rvRestaurantSuggestions = (RecyclerView) rootView.findViewById(R.id.rvRestaurantSuggestions);
        rvRestaurantSuggestions.setLayoutManager(new LinearLayoutManager(activity));
        rvRestaurantSuggestions.setItemAnimator(new DefaultItemAnimator());
        rvRestaurantSuggestions.setHasFixedSize(false);
        etContent = (EditText) rootView.findViewById(R.id.etContent);
        rlSelectRestaurant = (RelativeLayout) rootView.findViewById(R.id.layout_select_restaurant);
        ivAccessCamera = (ImageView) rootView.findViewById(R.id.ivAccessCamera);
        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        tvCharCount = (TextView) rootView.findViewById(R.id.tvCharCount);
        ratingBar = (RatingBarMenuFeedback) rootView.findViewById(R.id.rating_bar_add_post);
        rlReview.setOnClickListener(this);
        rlAsk.setOnClickListener(this);
        tvRestaurantLocation.setOnClickListener(this);

        suggestions = new ArrayList<>();
        suggestionsAdapter = new RestaurantQuerySuggestionsAdapter(suggestions, new RestaurantQuerySuggestionsAdapter.Callback() {
            @Override
            public void onSuggestionClick(int position, SuggestRestaurantQueryResp.Suggestion suggestion) {
                suggestionSelected = suggestion;
                tvRestaurantLocation.setText(suggestion.getName());
                openSuggestionView(false);


            }
        });
        rvRestaurantSuggestions.setAdapter(suggestionsAdapter);


        etRestaurantLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 2) {
                    suggestRestaurantApi(s.toString().trim());
                } else if (s.toString().trim().length() == 0) {
                    suggestions.clear();
                    suggestionsAdapter.notifyDataSetChanged();
                    rvRestaurantSuggestions.setVisibility(suggestionsAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
                }
            }
        });

        /*etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCharCount.setText(String.valueOf((500 - s.toString().trim().length())));

            }
        });*/

        ivAccessCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


                    (FeedAddPostFragment.this).requestPermissions(permissionsRequest, 20);
                    return;
                }


                if (picker == null) {
                    picker = new Picker.Builder(activity, R.style.AppThemePicker_NoActionBar).setPickMode(Picker.PickMode.MULTIPLE_IMAGES).build();
                }


                picker.setLimit(imageSelected == null ? 5 : 5 - imageSelected.size());
                picker.startActivity(FeedAddPostFragment.this, activity, REQUEST_CODE_SELECT_IMAGE);

            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postText = etContent.getText().toString().trim();

                if (addPostType == AddPostType.REVIEW) {

                    if (suggestionSelected == null) {
                        Toast.makeText(activity, "You need to select a restaurant to review", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(postText)) {
                        Toast.makeText(activity, R.string.please_enter_something, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    postFeedAPI(postText, suggestionSelected.getId());

                } else {
                    if (TextUtils.isEmpty(postText)) {
                        Toast.makeText(activity, R.string.please_enter_something, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    postFeedAPI(postText, -1);
                }

            }
        });


        switchAddFeed(addPostType);

        return rootView;
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
                    setUpAdapter();
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            }
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            activity.fragmentUISetup(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlReview:
                product.clicklabs.jugnoo.utils.Utils.hideKeyboard(getActivity());
                switchAddFeed(AddPostType.REVIEW);
                break;

            case R.id.rlAsk:
                product.clicklabs.jugnoo.utils.Utils.hideKeyboard(getActivity());
                switchAddFeed(AddPostType.ASK);
                break;

            case R.id.tvRestaurantLocation:
                openSuggestionView(true);
                break;
        }
    }

    private void switchAddFeed(AddPostType addPostType) {
        this.addPostType = addPostType;
        switch (addPostType) {
            case REVIEW:
                tvReview.setTextColor(ContextCompat.getColor(activity, R.color.text_color));
                tvAsk.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
                vReviewSelected.setVisibility(View.VISIBLE);
                vAskSelected.setVisibility(View.GONE);
                ivReview.setImageResource(R.drawable.ic_feed_star);
                ivAsk.setImageResource(R.drawable.ic_feed_ask_disabled);
                etContent.setHint(R.string.share_your_experience);
                ratingBar.setVisibility(View.VISIBLE);
                rlSelectRestaurant.setVisibility(View.VISIBLE);
                imageSelected = null;
                setUpAdapter();
                if (ratingBar != null)
                    ratingBar.setScore(0);
                break;

            case ASK:
                tvReview.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
                tvAsk.setTextColor(ContextCompat.getColor(activity, R.color.text_color));
                vReviewSelected.setVisibility(View.GONE);
                vAskSelected.setVisibility(View.VISIBLE);
                ivReview.setImageResource(R.drawable.ic_feed_star_disable);
                ivAsk.setImageResource(R.drawable.ic_feed_ask);
                ratingBar.setVisibility(View.GONE);
                etContent.setHint(R.string.looking_for_something);
                rlSelectRestaurant.setVisibility(View.GONE);
                imageSelected = null;
                setUpAdapter();
                break;
        }
        openSuggestionView(false);
        LinearLayout.LayoutParams paramsETContent = (LinearLayout.LayoutParams) etContent.getLayoutParams();
        paramsETContent.height = 0;
        paramsETContent.weight = 1;
        etContent.setLayoutParams(paramsETContent);

        etContent.requestFocus();

        etContent.setText("");
        suggestionSelected = null;
        tvRestaurantLocation.setText("");
        etRestaurantLocation.setText("");


    }

    private void openSuggestionView(boolean open) {
        if (open) {
            tvRestaurantLocation.setVisibility(View.GONE);
            etRestaurantLocation.setVisibility(View.VISIBLE);
            etRestaurantLocation.setText(tvRestaurantLocation.getText());
            etRestaurantLocation.setSelection(etRestaurantLocation.getText().length());
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llReviewLocation.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            llReviewLocation.setLayoutParams(params);
            rvRestaurantSuggestions.setVisibility(View.GONE);
            etRestaurantLocation.requestFocus();
        } else {
            tvRestaurantLocation.setVisibility(View.VISIBLE);
            etRestaurantLocation.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llReviewLocation.getLayoutParams();
            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            llReviewLocation.setLayoutParams(params);
            pwRestLocQuery.setVisibility(View.GONE);
            rvRestaurantSuggestions.setVisibility(View.GONE);
        }
    }

    private void suggestRestaurantApi(String query) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                pwRestLocQuery.setVisibility(View.VISIBLE);

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_INPUT, query);
                params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));

                RestClient.getFeedApiService().suggestRestaurant(params, new retrofit.Callback<SuggestRestaurantQueryResp>() {
                    @Override
                    public void success(SuggestRestaurantQueryResp queryResp, Response response) {
                        try {
//                            String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                            String message = queryResp.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, queryResp.getFlag(),
                                    queryResp.getError(), queryResp.getMessage())) {
                                if (queryResp.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                    suggestions.clear();
                                    suggestions.addAll(queryResp.getSuggestions());
                                    suggestionsAdapter.notifyDataSetChanged();
                                    rvRestaurantSuggestions.setVisibility(suggestionsAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
                                } else {
                                    suggestions.clear();
                                    SuggestRestaurantQueryResp.Suggestion suggestion = new SuggestRestaurantQueryResp.Suggestion();
                                    suggestion.setId(-1);
                                    suggestion.setName("No results found");
                                    suggestions.add(suggestion);
                                    suggestionsAdapter.notifyDataSetChanged();
                                    rvRestaurantSuggestions.setVisibility(suggestionsAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        pwRestLocQuery.setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pwRestLocQuery.setVisibility(View.GONE);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public enum AddPostType {
        REVIEW(0), ASK(1);

        private int ordinal;

        AddPostType(int ordinal) {
            this.ordinal = ordinal;
        }

        public int getOrdinal() {
            return ordinal;
        }
    }


    public void postFeedAPI(final String postText, final int restId) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                final MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
                DialogPopup.showLoadingDialog(getActivity(), getActivity().getResources().getString(R.string.loading));

                if (imageSelected != null && imageSelected.size() != 0) {
                    //upload feedback with new Images
                    imageCompressionTask = new ImageCompression(new ImageCompression.AsyncResponse() {
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
                            uploadParamsAndPost(multipartTypedOutput, postText, restId);
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
                    uploadParamsAndPost(multipartTypedOutput, postText, restId);
                }
            } else {
                retryDialog(DialogErrorType.NO_NET, postText, restId);

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void uploadParamsAndPost(MultipartTypedOutput multipartTypedOutput, final String postText, final int restId) {

        multipartTypedOutput.addPart(Constants.KEY_ACCESS_TOKEN, new TypedString(Data.userData.accessToken));
        multipartTypedOutput.addPart(Constants.KEY_LATITUDE, new TypedString(String.valueOf(activity.getSelectedLatLng().latitude)));
        multipartTypedOutput.addPart(Constants.KEY_LONGITUDE, new TypedString(String.valueOf(activity.getSelectedLatLng().longitude)));
        multipartTypedOutput.addPart(Constants.KEY_POST_TEXT, new TypedString(postText));

        if (addPostType == AddPostType.REVIEW) {
            multipartTypedOutput.addPart(Constants.KEY_RESTAURANT_ID, new TypedString(String.valueOf(restId)));
            multipartTypedOutput.addPart(Constants.KEY_STAR_COUNT, new TypedString(String.valueOf(Math.round(ratingBar.getScore()))));
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
                            if (activity.getFeedHomeFragment() != null && activity.getFeedHomeFragment().getView()!=null) {
                                activity.getFeedHomeFragment().fetchFeedsApi(true);
                            }
                        } else {
                            DialogPopup.alertPopup(activity, "", message);
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                    retryDialog(DialogErrorType.SERVER_ERROR, postText, restId);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                DialogPopup.dismissLoadingDialog();
                retryDialog(DialogErrorType.CONNECTION_LOST, postText, restId);

            }
        });
    }

    private void retryDialog(DialogErrorType dialogErrorType, final String postText, final int restId) {
        DialogPopup.dialogNoInternet(activity, dialogErrorType,
                new product.clicklabs.jugnoo.utils.Utils.AlertCallBackWithButtonsInterface() {
                    @Override
                    public void positiveClick(View view) {
                        postFeedAPI(postText, restId);
                    }

                    @Override
                    public void neutralClick(View view) {

                    }

                    @Override
                    public void negativeClick(View view) {

                    }
                });
    }

    private void setUpAdapter() {


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
                    if (object instanceof ImageEntry) {
                        imageSelected.remove(object);
                        if (imageSelected.size() == 0)
                            displayImagesRecycler.setVisibility(View.GONE);
                        ivAccessCamera.setEnabled(imageSelected.size() < 5);
                    }


                }
            }, displayImagesRecycler);
            displayImagesRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            displayImagesRecycler.setAdapter(editReviewImagesAdapter);
        } else {
            editReviewImagesAdapter.setList(imageSelected);
        }

        if (displayImagesRecycler.getVisibility() != View.VISIBLE)
            displayImagesRecycler.setVisibility(View.VISIBLE);


        ivAccessCamera.setEnabled(imageSelected.size() < 5);


        if (imageSelected.size() > 0)
            displayImagesRecycler.smoothScrollToPosition(imageSelected.size());


    }
}
