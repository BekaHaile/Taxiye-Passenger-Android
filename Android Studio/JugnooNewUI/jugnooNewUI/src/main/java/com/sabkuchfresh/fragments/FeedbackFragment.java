package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.SupportActivity;
import com.sabkuchfresh.retrofit.RestClient;
import com.sabkuchfresh.retrofit.model.OrderHistoryResponse;
import com.sabkuchfresh.utils.Constants;
import com.sabkuchfresh.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import com.sabkuchfresh.utils.Utils;

import java.util.HashMap;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DialogErrorType;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by gurmail on 24/05/16.
 */
public class FeedbackFragment extends BaseFragment implements View.OnClickListener, FlurryEventNames, Constants {


    private View rootView;
    private SupportActivity activity;
    private LinearLayout mainLayout;
    private TextView titleText, skip;
    private Button submit;
    private EditText commentBox;
    private LikeButton dislikeImage, likeImage;
    private int ratingType = -1;
    private static final String RATING_TYPE = "0";
    private String skipValue = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_feedback, container, false);

        activity = (SupportActivity) getActivity();

        mainLayout = (LinearLayout) rootView.findViewById(R.id.main_layout);
        new ASSL(activity, mainLayout, 1134, 720, false);

        titleText = (TextView) rootView.findViewById(R.id.question_view);
        skip = (TextView) rootView.findViewById(R.id.textView_skip);

        submit = (Button) rootView.findViewById(R.id.buttonSubmit);
        commentBox = (EditText) rootView.findViewById(R.id.edittext_view);
        commentBox.setTypeface(Fonts.mavenRegular(activity));

        titleText.setTypeface(Fonts.mavenRegular(activity));
        skip.setTypeface(Fonts.mavenRegular(activity));
        submit.setTypeface(Fonts.mavenRegular(activity));

        likeImage = (LikeButton) rootView.findViewById(R.id.like_view);
        dislikeImage = (LikeButton) rootView.findViewById(R.id.dislike_view);


        titleText.setText(activity.question);

        dislikeImage.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                likeImage.setLiked(false);
                ratingType = 0;
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                likeImage.setLiked(true);
                ratingType = 1;
            }
        });

        likeImage.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                dislikeImage.setLiked(false);
                ratingType = 1;
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                dislikeImage.setLiked(true);
                ratingType = 0;
            }
        });

        if (activity.skip) {
            skip.setVisibility(View.GONE);
        }


        skip.setOnClickListener(this);
        submit.setOnClickListener(this);
//        FlurryEventLogger.checkoutTrackEvent(AppConstant.EventTracker.CHECKOUT, activity.productList);
        FlurryEventLogger.event(FEEDBACK_SCREEN);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSubmit:
                if (ratingType == 0)
                    FlurryEventLogger.event(PAYMENT_SCREEN, "Dislike", SUBMIT_FEEDBACK);
                else
                    FlurryEventLogger.event(PAYMENT_SCREEN, "Like", SUBMIT_FEEDBACK);

                if (ratingType == -1) {
                    Toast.makeText(activity, "Please rate us", Toast.LENGTH_SHORT).show();
                } else {
                    sendQuery(commentBox.getText().toString().trim(), ratingType);
                }
                break;
            case R.id.textView_skip:
                FlurryEventLogger.event(PAYMENT_SCREEN, SKIP_FEEDBACK, SKIP_FEEDBACK);
                skipValue = "1";
                sendQuery("", ratingType);
                activity.onBackPressed();
                //activity.finish();
                break;
            default:

                break;


        }

    }

    /**
     * Method used to send order feedback value
     *
     * @param text
     * @param rating
     */
    private void sendQuery(String text, final int rating) {
        try {
            if (AppStatus.getInstance(MyApplication.getInstance()).isOnline(MyApplication.getInstance())) {

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                params.put(Constants.ORDER_ID, activity.orderId);
                if (TextUtils.isEmpty(skipValue)) {
                    DialogPopup.showLoadingDialog(activity, "loading...");
                    params.put(Constants.RATING, "" + rating);
                    params.put(Constants.QUESTION_TYPE, "" + activity.questionType);
                    params.put(Constants.RATING_TYPE, RATING_TYPE);
                    params.put(Constants.COMMENT, text);
//                    params.put(Constants.ORDER_ID, ORDER_ID);

                } else {
                    params.put(Constants.SKIP, skipValue);
                }


                RestClient.getFreshApiService().orderFeedback(params, new Callback<OrderHistoryResponse>() {
                    @Override
                    public void success(final OrderHistoryResponse notificationInboxResponse, Response response) {
                        DialogPopup.dismissLoadingDialog();

                        try {

                            if (notificationInboxResponse.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                if (TextUtils.isEmpty(skipValue)) {
                                    commentBox.setText("");
                                    if (activity.getOrderHistoryOpened() != null)
                                        activity.getOrderHistoryOpened().setPendingFeedback(0);

                                    if (Prefs.with(getActivity()).getBoolean(KEY_STORE_RATE, true) && rating == 1) {
                                        Prefs.with(getActivity()).save(KEY_STORE_RATE, true);
                                        DialogPopup.rateusPopup(activity, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Utils.openPlayStore(activity);
                                                activity.onBackPressed();
                                            }
                                        }, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                activity.onBackPressed();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(activity, "" + notificationInboxResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                        activity.onBackPressed();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        if (TextUtils.isEmpty(skipValue)) {
                            DialogPopup.dialogNoInternet(activity, DialogErrorType.CONNECTION_LOST,
                                    new Utils.AlertCallBackWithButtonsInterface() {
                                        @Override
                                        public void positiveClick(View view) {
                                            sendQuery(commentBox.getText().toString().trim(), ratingType);
                                        }

                                        @Override
                                        public void neutralClick(View view) {

                                        }

                                        @Override
                                        public void negativeClick(View view) {

                                        }
                                    });
                        }
                    }
                });
            } else {
                if (TextUtils.isEmpty(skipValue)) {
                    DialogPopup.dialogNoInternet(activity,
                            Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,

                            new Utils.AlertCallBackWithButtonsInterface() {
                                @Override
                                public void positiveClick(View v) {
                                    sendQuery(commentBox.getText().toString().trim(), ratingType);
                                }

                                @Override
                                public void neutralClick(View v) {

                                }

                                @Override
                                public void negativeClick(View v) {

                                }
                            });
                }
            }
        } catch (Exception e) {
            DialogPopup.dismissLoadingDialog();
            e.printStackTrace();
        }
    }


}
