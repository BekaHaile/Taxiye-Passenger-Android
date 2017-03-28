package com.sabkuchfresh.feed.ui.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.feed.models.RegisterForFeedResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.feed.utils.Utils;
import com.sabkuchfresh.home.FreshActivity;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Fonts;
import retrofit.RetrofitError;

/**
 * Created by Parminder Singh on 3/28/17.
 */

public class FeedReserveSpotFragment extends Fragment {


    @Bind(R.id.layout_meter_feed_users)
    LinearLayout layoutMeterFeedUsers;
    @Bind(R.id.tv_placeholder_feed_introudction)
    TextView tvPlaceholderFeedIntroudction;
    @Bind(R.id.btn_reserve_spot)
    Button btnReserveSpot;
    @Bind(R.id.tv_rank_description)
    TextView tvRankDescription;
    private FreshActivity activity;
    @Bind(R.id.ivBg)
     ImageView ivBg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed_reserve_my_spot, container, false);
        ButterKnife.bind(this, rootView);
        activity= (FreshActivity) getActivity();
        activity.fragmentUISetup(this);
        tvPlaceholderFeedIntroudction.setText(Data.getFeedData().getFeedIntroString());
        setFeedUsersData();
        return rootView;
    }

    private void setFeedUsersData() {
        if(Data.getFeedData().getUserRank()==null){
            //IF not registered
            setMeter(Data.getFeedData().getFeedUsersCount());
        }else{
            btnReserveSpot.setVisibility(View.GONE);

            //if(user_ahead_count added by backend)
              setMeter(Data.getFeedData().getUserAheadCount());
            //else
//            setMeter(Data.getFeedData().getFeedUsersCount()-Data.getFeedData().getUserRank());

            tvRankDescription.setText("Already ahead of you in queue.");

        }
    }

    private void setMeter(long number) {
        if((layoutMeterFeedUsers.getChildCount() > 0))
            layoutMeterFeedUsers.removeAllViews();

        if (number <= 0) {
            layoutMeterFeedUsers.addView(createTextView(String.valueOf(0)));
        } else {

            while (number != 0) {

                layoutMeterFeedUsers.addView(createTextView(String.valueOf(number % 10)));

                number = number / 10;
            }

        }
    }

    private TextView createTextView(String textToSet) {
        TextView v = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = Utils.dpToPx(4.0f);
        v.setPadding(Utils.dpToPx(6), Utils.dpToPx(8f), Utils.dpToPx(6), Utils.dpToPx(8f));
        v.setGravity(Gravity.CENTER);
        v.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        v.setTypeface(Fonts.mavenRegular(getContext()), Typeface.BOLD);
        v.setTextColor(ContextCompat.getColor(getContext(), R.color.feed_grey_text));
        v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
        v.setLayoutParams(params);
        v.setText(textToSet);
        return v;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_reserve_spot)
    public void onClick() {

        if(Data.getFeedData().getUserRank()==null)
        {
            if(Data.longitude==0||Data.latitude==0){
                Toast.makeText(activity, "Please turn on your location to register.", Toast.LENGTH_SHORT).show();
                return;
            }
            HashMap<String,String> params = new HashMap<>();
            params.put(Constants.KEY_LATITUDE,String.valueOf(activity.getSelectedLatLng().latitude));
            params.put(Constants.KEY_LONGITUDE,String.valueOf(activity.getSelectedLatLng().longitude));
            params.put(Constants.KEY_ACCESS_TOKEN,String.valueOf(Data.userData.accessToken));


           new ApiCommon<RegisterForFeedResponse>(activity).putDefaultParams(true).execute(params, ApiName.REGISTER_FOR_FEED, new APICommonCallback<RegisterForFeedResponse>() {
               @Override
               public boolean onNotConnected() {
                   return false;//return true to not show dialog error
               }

               @Override
               public boolean onException(Exception e) {
                   return false;
               }

               @Override
               public void onSuccess(RegisterForFeedResponse registerForFeedResponse, String message, int flag) {
                   Data.getFeedData().setUserRank(registerForFeedResponse.getUserRank());


                   //iff(user_ahead_count added by backend)
                     Data.getFeedData().setUserAheadCount(registerForFeedResponse.getUserAheadCount());
                   //else
                   // Data.getFeedData().incrementUserCount();


                   setFeedUsersData();

               }

               @Override
               public boolean onError(RegisterForFeedResponse registerForFeedResponse, String message, int flag) {
                   return false;
               }

               @Override
               public boolean onFailure(RetrofitError error) {
                   return false;
               }
           });
        }

    }
}
