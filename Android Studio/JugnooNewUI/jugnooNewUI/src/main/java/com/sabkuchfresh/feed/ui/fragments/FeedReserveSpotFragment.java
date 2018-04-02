package com.sabkuchfresh.feed.ui.fragments;

import android.graphics.Typeface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;
import com.sabkuchfresh.analytics.GAUtils;
import com.sabkuchfresh.feed.models.RegisterForFeedResponse;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.feed.utils.FeedUtils;
import com.sabkuchfresh.home.FreshActivity;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.utils.Fonts;
import retrofit.RetrofitError;

/**
 * Created by Parminder Singh on 3/28/17.
 */

public class FeedReserveSpotFragment extends Fragment implements GACategory, GAAction {


    @Bind(R.id.layout_meter_feed_users)
    LinearLayout layoutMeterFeedUsers;
    @Bind(R.id.tv_placeholder_feed_introudction)
    TextView tvPlaceholderFeedIntroudction;
    @Bind(R.id.btn_reserve_spot)
    Button btnReserveSpot;
    @Bind(R.id.tv_rank_description)
    TextView tvRankDescription;
    FreshActivity activity;
    @Bind(R.id.ivBg)
    ImageView ivBg;
    @Bind(R.id.vSpacing)
    View vSpacing;

	@Bind(R.id.rlCreateHandle)
	RelativeLayout rlCreateHandle;
	@Bind(R.id.etCreateHandle)
	EditText etCreateHandle;
	@Bind(R.id.bSubmit)
	Button bSubmit;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed_reserve_my_spot, container, false);
        ButterKnife.bind(this, rootView);
        activity= (FreshActivity) getActivity();
        activity.fragmentUISetup(this);
        if(Data.getFeedData() != null) {
            tvPlaceholderFeedIntroudction.setText(Data.getFeedData().getFeedIntroString());
        }
        setFeedUsersData();

		bSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onReserveSpotClick();
			}
		});

        return rootView;
    }

    private void setFeedUsersData() {
        if(Data.getFeedData().getFeedRank() == null){
            //IF not registered
            setMeter(Data.getFeedData().getUsersCount());
            vSpacing.setVisibility(View.GONE);

			if(Data.getFeedData().showCreateHandle()){
				btnReserveSpot.setVisibility(View.GONE);
				rlCreateHandle.setVisibility(View.VISIBLE);
                GAUtils.trackScreenView(FEED+HOME+WAITLIST+WITH_HANDLE);
			} else {
				btnReserveSpot.setVisibility(View.VISIBLE);
				rlCreateHandle.setVisibility(View.GONE);
                GAUtils.trackScreenView(FEED+HOME+WAITLIST);
			}

        }else{
//            btnReserveSpot.setVisibility(View.GONE);
//            setMeter(Data.getFeedData().getFeedRank()-1);
//            tvRankDescription.setText("people ahead of you in the waitlist.");
//            vSpacing.setVisibility(View.VISIBLE);
//            GAUtils.trackScreenView(FEED+HOME+WAITLIST+SHARING);

            activity.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.getTransactionUtils().openFeedSpotReservedSharingFragment(activity, activity.getRelativeLayoutContainer());
                }
            }, 200);

        }
    }

    private void setMeter(long number) {
        if((layoutMeterFeedUsers.getChildCount() > 0))
            layoutMeterFeedUsers.removeAllViews();

        if (number <= 0) {
            layoutMeterFeedUsers.addView(createTextView(String.valueOf(0)));
        } else {

//            while (number != 0) {
//
//                layoutMeterFeedUsers.addView(createTextView(String.valueOf(number % 10)));
//
//                number = number / 10;
//            }

            char[] digits = String.valueOf(number).toCharArray();
            for(char digit : digits){
                layoutMeterFeedUsers.addView(createTextView(String.valueOf(digit)));
            }

        }
    }

    private TextView createTextView(String textToSet) {
        TextView v = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = FeedUtils.dpToPx(4.0f);
        v.setPadding(FeedUtils.dpToPx(6), FeedUtils.dpToPx(8f), FeedUtils.dpToPx(6), FeedUtils.dpToPx(8f));
        v.setGravity(Gravity.CENTER);
        v.setBackgroundResource(R.drawable.background_white_rounded_bordered);
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
    public void onReserveSpotClick() {

        if(Data.getFeedData().getFeedRank()==null)
        {
            GAUtils.event(FEED, WAITLIST, RESERVE_MY_SPOT+CLICKED);
            if(Data.longitude==0||Data.latitude==0){
                Toast.makeText(activity, getString(R.string.please_turn_on_location), Toast.LENGTH_SHORT).show();
                return;
            }

            String handleText = etCreateHandle.getText().toString().trim();
            if(Data.getFeedData().showCreateHandle() && handleText.length() == 0){
				Toast.makeText(activity, getString(R.string.please_enter_handle_of_your_choice), Toast.LENGTH_SHORT).show();
				return;
			}

            HashMap<String,String> params = new HashMap<>();
            params.put(Constants.KEY_LATITUDE,String.valueOf(activity.getSelectedLatLng().latitude));
            params.put(Constants.KEY_LONGITUDE,String.valueOf(activity.getSelectedLatLng().longitude));
            params.put(Constants.KEY_ACCESS_TOKEN,String.valueOf(Data.userData.accessToken));
			if(Data.getFeedData().showCreateHandle()){
				params.put(Constants.KEY_HANDLE, handleText);
                product.clicklabs.jugnoo.utils.Utils.hideSoftKeyboard(activity, etCreateHandle);
			}


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
                   Data.getFeedData().setFeedRank(registerForFeedResponse.getFeedRank());
                   setFeedUsersData();
                   if(flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
                       product.clicklabs.jugnoo.utils.Utils.showToast(activity, message);
                   }

               }

               @Override
               public boolean onError(RegisterForFeedResponse registerForFeedResponse, String message, int flag) {
                   return false;
               }

               @Override
               public boolean onFailure(RetrofitError error) {
                   return false;
               }

               @Override
               public void onNegativeClick() {

               }
           });
        }

    }
}
