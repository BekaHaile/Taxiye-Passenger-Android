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
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.picker.image.model.ImageEntry;
import com.picker.image.util.Picker;
import com.sabkuchfresh.adapters.RestaurantQuerySuggestionsAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.SuggestRestaurantQueryResp;
import com.sabkuchfresh.utils.RatingBarMenuFeedback;
import com.sabkuchfresh.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundedCornersTransformation;

import java.io.File;
import java.util.ArrayList;

import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

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

    private View rootView;
    private FreshActivity activity;
    private AddPostType addPostType = AddPostType.ASK;
    private ArrayList<SuggestRestaurantQueryResp.Suggestion> suggestions;
    private SuggestRestaurantQueryResp.Suggestion suggestionSelected;
    private ImageView ivImageUploaded;
    private RatingBarMenuFeedback ratingBar;
    private String[] permissionsRequest;
    private Picker picker;
    private static  final int REQUEST_CODE_SELECT_IMAGE=106;
    private ImageView btnRemoveImage;

    public FeedAddPostFragment() {
    }


    public static FeedAddPostFragment newInstance(){
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

        rlReview = (RelativeLayout) rootView.findViewById(R.id.rlReview);
        rlAsk = (RelativeLayout) rootView.findViewById(R.id.rlAsk);
        tvReview = (TextView) rootView.findViewById(R.id.tvReview); tvReview.setTypeface(tvReview.getTypeface(), Typeface.BOLD);
        tvAsk = (TextView) rootView.findViewById(R.id.tvAsk); tvAsk.setTypeface(tvAsk.getTypeface(), Typeface.BOLD);
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
        ivAccessCamera = (ImageView) rootView.findViewById(R.id.ivAccessCamera);
        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        tvCharCount = (TextView) rootView.findViewById(R.id.tvCharCount);
        ivImageUploaded =(ImageView)rootView.findViewById(R.id.ivImageUploaded);
        ratingBar =(RatingBarMenuFeedback)rootView.findViewById(R.id.rating_bar_add_post);
        rlReview.setOnClickListener(this);
        rlAsk.setOnClickListener(this);
        tvRestaurantLocation.setOnClickListener(this);
        btnRemoveImage = (ImageView)rootView.findViewById(R.id.btn_remove);
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
                if(s.toString().trim().length() > 0){
                    suggestRestaurantApi(s.toString().trim());
                } else {
                    suggestions.clear();
                    suggestionsAdapter.notifyDataSetChanged();
                }
            }
        });

        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCharCount.setText(String.valueOf((400 - s.toString().trim().length())));
            }
        });

        switchAddFeed(addPostType);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        ivAccessCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(PermissionChecker.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED ||
                        PermissionChecker.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED)
                {
                    if (permissionsRequest ==null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            permissionsRequest = new String[2];
                            permissionsRequest[0]=Manifest.permission.WRITE_EXTERNAL_STORAGE;
                            permissionsRequest[1]=Manifest.permission.READ_EXTERNAL_STORAGE;
                        }
                        {
                            permissionsRequest = new String[1];
                            permissionsRequest[0]=Manifest.permission.WRITE_EXTERNAL_STORAGE;
                        }
                    }


                    (FeedAddPostFragment.this).requestPermissions(permissionsRequest, 20);
                    return;
                }


                if(picker ==null){
                    picker = new Picker.Builder(activity, R.style.AppThemePicker_NoActionBar).setPickMode(Picker.PickMode.MULTIPLE_IMAGES).build();
                }


                picker.setLimit(1);
                picker.startActivity(FeedAddPostFragment.this,activity, REQUEST_CODE_SELECT_IMAGE);

            }
        });
        btnRemoveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelected =null;
                ivImageUploaded.setVisibility(View.INVISIBLE);
                btnRemoveImage.setVisibility(View.INVISIBLE);
                ivAccessCamera.setEnabled(true);
            }
        });

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return rootView;
    }

    private ImageEntry imageSelected;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        product.clicklabs.jugnoo.utils.Utils.hideKeyboard(getActivity());
        if(requestCode== REQUEST_CODE_SELECT_IMAGE && resultCode==RESULT_OK){
            if(data!=null && data.getSerializableExtra("imagesList")!=null)
            {

                ArrayList<ImageEntry> images = (ArrayList<ImageEntry>) data.getSerializableExtra("imagesList");
                if (images != null && images.size() != 0) {
                    imageSelected =images.get(0);
                    Picasso.with(getActivity()).load(new File(images.get(0).path)).resize((int) Utils.convertDpToPixel(75.0f,getActivity()),(int) Utils.convertDpToPixel(75.0f,getActivity())).
                            centerCrop().transform(new RoundedCornersTransformation(8, 0)).into(ivImageUploaded);

                    ivImageUploaded.setVisibility(View.VISIBLE);
                    btnRemoveImage.setVisibility(View.VISIBLE);
                    ivAccessCamera.setEnabled(false);
                }
            }
        }

    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            activity.fragmentUISetup(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rlReview:
                switchAddFeed(AddPostType.REVIEW);
                break;

            case R.id.rlAsk:
                switchAddFeed(AddPostType.ASK);
                break;

            case R.id.tvRestaurantLocation:
                openSuggestionView(true);
                break;
        }
    }

    private void switchAddFeed(AddPostType addPostType){
        this.addPostType = addPostType;
        switch(addPostType){
            case REVIEW:
                tvReview.setTextColor(ContextCompat.getColor(activity, R.color.text_color));
                tvAsk.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
                vReviewSelected.setVisibility(View.VISIBLE);
                vAskSelected.setVisibility(View.GONE);
                ivReview.setImageResource(R.drawable.ic_feed_star);
                ivAsk.setImageResource(R.drawable.ic_feed_ask_disabled);
                llReviewLocation.setVisibility(View.VISIBLE);
                etContent.setHint(R.string.share_your_experience);
                ratingBar.setVisibility(View.VISIBLE);
                break;

            case ASK:
                tvReview.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
                tvAsk.setTextColor(ContextCompat.getColor(activity, R.color.text_color));
                vReviewSelected.setVisibility(View.GONE);
                vAskSelected.setVisibility(View.VISIBLE);
                ivReview.setImageResource(R.drawable.ic_feed_star_disable);
                ivAsk.setImageResource(R.drawable.ic_feed_ask);
                llReviewLocation.setVisibility(View.GONE);
                ratingBar.setVisibility(View.GONE);
                etContent.setHint(R.string.looking_for_something);
                break;
        }
        openSuggestionView(false);

        LinearLayout.LayoutParams paramsETContent = (LinearLayout.LayoutParams) etContent.getLayoutParams();
        paramsETContent.height = 0;
        paramsETContent.weight = 1;
        etContent.setLayoutParams(paramsETContent);

        etContent.requestFocus();
        Utils.showSoftKeyboard(activity, etContent);
    }

    private void openSuggestionView(boolean open){
        if(open){
            tvRestaurantLocation.setVisibility(View.GONE);
            etRestaurantLocation.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llReviewLocation.getLayoutParams();
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
            llReviewLocation.setLayoutParams(params);
            rvRestaurantSuggestions.setVisibility(View.VISIBLE);
            etRestaurantLocation.requestFocus();
            Utils.showSoftKeyboard(activity, etRestaurantLocation);
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
            if(MyApplication.getInstance().isOnline()) {
                pwRestLocQuery.setVisibility(View.VISIBLE);

                RestClient.getFeedApiService().suggestRestaurant(query, new retrofit.Callback<SuggestRestaurantQueryResp>() {
                    @Override
                    public void success(SuggestRestaurantQueryResp queryResp, Response response) {
                        try {
                            String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                            String message = queryResp.getMessage();
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, queryResp.getFlag(),
                                    queryResp.getError(), queryResp.getMessage())) {
                                if(queryResp.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
                                    suggestions.clear();
                                    suggestions.addAll(queryResp.getSuggestions());
                                    suggestionsAdapter.notifyDataSetChanged();
                                } else {
                                    suggestions.clear();
                                    SuggestRestaurantQueryResp.Suggestion suggestion = new SuggestRestaurantQueryResp.Suggestion();
                                    suggestion.setId(-1);
                                    suggestion.setName("No results found");
                                    suggestions.add(suggestion);
                                    suggestionsAdapter.notifyDataSetChanged();
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


    public enum AddPostType{
        REVIEW(0), ASK(1);

        private int ordinal;
        AddPostType(int ordinal){
            this.ordinal = ordinal;
        }

        public int getOrdinal() {
            return ordinal;
        }
    }

}
