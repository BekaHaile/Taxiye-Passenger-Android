package com.sabkuchfresh.feed.ui.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.adapters.RestaurantQuerySuggestionsAdapter;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.feed.SuggestRestaurantQueryResp;
import com.sabkuchfresh.retrofit.model.feed.generatefeed.FeedDetail;
import com.sabkuchfresh.utils.RatingBarMenuFeedback;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.sabkuchfresh.feed.ui.fragments.FeedAddPostFragment.FEED_DETAIL;

/**
 * Created by Parminder Singh on 3/20/17.
 */

public class FeedChildReviewFragment extends ImageSelectFragment {


    //Select Restaurant Views
    private LinearLayout llReviewLocation;
    private TextView tvRestaurantLocation;
    private EditText etRestaurantLocation;
    private ProgressWheel pwRestLocQuery;
    private RecyclerView rvRestaurantSuggestions;
    private RestaurantQuerySuggestionsAdapter suggestionsAdapter;
    private ArrayList<SuggestRestaurantQueryResp.Suggestion> suggestions;
    private LinearLayout layoutContent;
//    private View viewContentDisabled;




    //Rating Bar
    private RatingBarMenuFeedback ratingBar;


    //Edit Text views
    private EditText etContent;
    private SuggestRestaurantQueryResp.Suggestion suggestionSelected;

    public static FeedChildReviewFragment newInstance(FeedDetail feedDetail) {
        FeedChildReviewFragment feedChildReviewFragment = new FeedChildReviewFragment();
        if(feedDetail!=null){
            Bundle bundle =new Bundle();
            bundle.putSerializable(FEED_DETAIL,feedDetail);
            feedChildReviewFragment.setArguments(bundle);
        }
        return feedChildReviewFragment;


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(getArguments()!=null && getArguments().containsKey(FEED_DETAIL)){
            feedDetail= (FeedDetail) getArguments().getSerializable(FEED_DETAIL);
        }
        super.onCreate(savedInstanceState);
    }

    private void suggestRestaurantApi(String query) {
        try {
            if (MyApplication.getInstance().isOnline()) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_INPUT, query);
                params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getSelectedLatLng().latitude));
                params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getSelectedLatLng().longitude));

                new HomeUtil().putDefaultParams(params);
                RestClient.getFeedApiService().suggestRestaurant(params, new retrofit.Callback<SuggestRestaurantQueryResp>() {
                    @Override
                    public void success(SuggestRestaurantQueryResp queryResp, Response response) {
                        try {
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, queryResp.getFlag(),
                                    queryResp.getError(), queryResp.getMessage())) {
                                if (queryResp.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                    suggestions.clear();
                                    suggestions.addAll(queryResp.getSuggestions());
                                    suggestionsAdapter.notifyDataSetChanged();
                                    rvRestaurantSuggestions.setVisibility(suggestionsAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
                                    if(suggestionsAdapter.getItemCount()>3){
                                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rvRestaurantSuggestions.getLayoutParams();
                                        layoutParams.height= product.clicklabs.jugnoo.utils.Utils.convertDpToPx(activity,200);
                                        rvRestaurantSuggestions.setLayoutParams(layoutParams);
                                    }
                                    else{
                                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rvRestaurantSuggestions.getLayoutParams();
                                        layoutParams.height= LinearLayout.LayoutParams.WRAP_CONTENT;
                                        rvRestaurantSuggestions.setLayoutParams(layoutParams);
                                    }
                                } else {
                                    suggestions.clear();
                                    SuggestRestaurantQueryResp.Suggestion suggestion = new SuggestRestaurantQueryResp.Suggestion();
                                    suggestion.setId(-1);
                                    suggestion.setName(getActivity().getString(R.string.no_results_found));
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
                pwRestLocQuery.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_child_feed_review, container, false);
        rootView.findViewById(R.id.layout_select_restaurant).setVisibility(View.VISIBLE);
        layoutContent= (LinearLayout) rootView.findViewById(R.id.layout_share_experience);
//        viewContentDisabled  = rootView.findViewById(R.id.view_content_disabled);
        activity = ((FreshActivity) getActivity());
        etContent = (EditText) rootView.findViewById(R.id.etContent);
        displayImagesRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_view_photos);
        scrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);
        llReviewLocation = (LinearLayout) rootView.findViewById(R.id.llReviewLocation);
        tvRestaurantLocation = (TextView) rootView.findViewById(R.id.tvRestaurantLocation);
        etRestaurantLocation = (EditText) rootView.findViewById(R.id.etRestaurantLocation);
        pwRestLocQuery = (ProgressWheel) rootView.findViewById(R.id.pwRestLocQuery); pwRestLocQuery.setVisibility(View.GONE);
        rvRestaurantSuggestions = (RecyclerView) rootView.findViewById(R.id.rvRestaurantSuggestions);
        rvRestaurantSuggestions.setLayoutManager(new LinearLayoutManager(activity));
        rvRestaurantSuggestions.setItemAnimator(new DefaultItemAnimator());
        rvRestaurantSuggestions.setHasFixedSize(false);
        ratingBar = (RatingBarMenuFeedback) rootView.findViewById(R.id.rating_bar_add_post);
        ratingBar.setVisibility(View.VISIBLE);
        tvRestaurantLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSuggestionView(true);
            }
        });

        suggestions = new ArrayList<>();
        suggestionsAdapter = new RestaurantQuerySuggestionsAdapter(suggestions, new RestaurantQuerySuggestionsAdapter.Callback() {
            @Override
            public void onSuggestionClick(int position, SuggestRestaurantQueryResp.Suggestion suggestion) {
                suggestionSelected =suggestion;
                tvRestaurantLocation.setText(suggestion.getName());
                openSuggestionView(false);
                onRestaurantChanged(true);


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
                    tvRestaurantLocation.setText("");
                    suggestionSelected = null;
                }
            }
        });


        etRestaurantLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    onRestaurantChanged(false);
                }
            }
        });




        etContent.addTextChangedListener(editTextWacherContent);
        if(Data.getFeedData()!=null && !TextUtils.isEmpty(Data.getFeedData().getFeedAddReviewHint())) {
            etContent.setHint(Data.getFeedData().getFeedAddReviewHint());
        }


        if(feedDetail != null){
            //If editing
            etContent.setText(feedDetail.getContent());
            etContent.setSelection(etContent.getText().length());
            etContent.requestFocus();
            setUpImagesAdapter();
            suggestionSelected = new SuggestRestaurantQueryResp.Suggestion();
            suggestionSelected.setId(feedDetail.getRestaurantId());
            ratingBar.setScore(feedDetail.getStarCount().floatValue(), true);
            tvRestaurantLocation.setText(feedDetail.getRestaurantName());
            tvRestaurantLocation.setOnClickListener(null);

        }else{
            // if fresh review then Restaurant Enabled
            layoutContent.setEnabled(false);
            ratingBar.setEnabled(false);
            etContent.setEnabled(false);
            ratingBar.setRatingDisabled(true);
        }


        return rootView;


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
            pwRestLocQuery.setVisibility(View.GONE);
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








    @Override
    public boolean canSubmit() {
        if (feedDetail == null && (suggestionSelected == null || etRestaurantLocation.hasFocus())) {
            Toast.makeText(activity, R.string.error_feed_review_restaurant_not_selected, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
            Toast.makeText(activity, R.string.please_enter_something, Toast.LENGTH_SHORT).show();
            return false;
        }

        return   layoutContent.isEnabled();


    }



    public void onRestaurantChanged(boolean isSelected){
        if(isSelected || feedDetail != null){
            layoutContent.setEnabled(true);
            ratingBar.setEnabled(true);
            etContent.setEnabled(true);
//            setCameraEnabled(true);
            ratingBar.setRatingDisabled(false);
        }
        else {
            layoutContent.setEnabled(false);
            ratingBar.setEnabled(false);
            etContent.setEnabled(false);
//            setCameraEnabled(false);
            if(Math.round(ratingBar.getScore())<1)
                ratingBar.setRatingDisabled(true);
        }


    }

    @Override
    public String getText() {
        return etContent.getText().toString().trim();
    }

    @Override
    public Integer getRestaurantId() {
        return   suggestionSelected.getId();
    }

    @Override
    protected Integer getScore() {
        return Math.round(ratingBar.getScore());
    }

    @Override
    public boolean canUploadImages() {

        if(!layoutContent.isEnabled())
            Toast.makeText(activity, R.string.error_feed_review_restaurant_not_selected, Toast.LENGTH_SHORT).show();

        return  layoutContent.isEnabled() ;

    }

    @Override
    public boolean submitEnabledState() {
        return etContent.getText().toString().trim().length()>0;
    }

    @Override
    public boolean isAnonymousPostingEnabled() {
        return false;
    }

    @Override
    public EditText getFocusEditText() {
        return etRestaurantLocation;
    }
}
