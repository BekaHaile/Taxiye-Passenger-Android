package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.adapters.FeedbackReasonsAdapter;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.FeedbackMode;
import product.clicklabs.jugnoo.support.SupportActivity;
import product.clicklabs.jugnoo.datastructure.PendingCall;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class FeedbackActivity extends BaseActivity implements FlurryEventNames{

    private final String TAG = FeedbackActivity.class.getSimpleName();

    RelativeLayout relative;

    RelativeLayout topBar;
    TextView textViewTitle;
    ImageView imageViewBack;

    RatingBar ratingBarFeedback;
    TextView textViewRateText, textViewWhatImprove;
    ListView listViewFeedbackReasons;
    EditText editTextFeedback;
    Button buttonSubmitFeedback;
    RelativeLayout relativeLayoutOtherError;

    FeedbackReasonsAdapter feedbackReasonsAdapter;

    RelativeLayout relativeLayoutSkip;
    TextView textViewSkip;

    ScrollView scrollView;
    LinearLayout linearLayoutMain;
    TextView textViewScroll;

    FeedbackMode feedbackMode = FeedbackMode.SUPPORT;
    int pastDriverId = 0, pastEngagementId = 0, position = 0;
    boolean scrolled = false;

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);



        feedbackMode = FeedbackMode.SUPPORT;

        scrolled = false;

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, relative, 1134, 720, false);

        topBar = (RelativeLayout) findViewById(R.id.topBar);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.mavenRegular(this));
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        ratingBarFeedback = (RatingBar) findViewById(R.id.ratingBarFeedback);
        ratingBarFeedback.setRating(0);
        textViewRateText = (TextView) findViewById(R.id.textViewRateText);
        textViewRateText.setTypeface(Fonts.latoRegular(this));
        textViewRateText.setText("");

        textViewWhatImprove = (TextView) findViewById(R.id.textViewWhatImprove); textViewWhatImprove.setTypeface(Fonts.latoRegular(this));
        listViewFeedbackReasons = (ListView) findViewById(R.id.listViewFeedbackReasons);

        feedbackReasonsAdapter = new FeedbackReasonsAdapter(this, Data.feedbackReasons, new FeedbackReasonsAdapter.FeedbackReasonsListEventHandler() {
            @Override
            public void onLastItemSelected(boolean selected) {
                if(!selected){
                    if(relativeLayoutOtherError.getVisibility() == View.VISIBLE){
                        relativeLayoutOtherError.setVisibility(View.GONE);
                    }
                }
            }
        });
        listViewFeedbackReasons.setAdapter(feedbackReasonsAdapter);

        editTextFeedback = (EditText) findViewById(R.id.editTextFeedback);
        editTextFeedback.setTypeface(Fonts.latoRegular(this));
        buttonSubmitFeedback = (Button) findViewById(R.id.buttonSubmitFeedback);
        buttonSubmitFeedback.setTypeface(Fonts.mavenRegular(this));

        relativeLayoutOtherError = (RelativeLayout) findViewById(R.id.relativeLayoutOtherError);
        ((TextView)findViewById(R.id.textViewOtherError)).setTypeface(Fonts.latoRegular(this));
        relativeLayoutOtherError.setVisibility(View.GONE);

        relativeLayoutSkip = (RelativeLayout) findViewById(R.id.relativeLayoutSkip);
        textViewSkip = (TextView) findViewById(R.id.textViewSkip);
        textViewSkip.setTypeface(Fonts.latoRegular(this));

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
        textViewScroll = (TextView) findViewById(R.id.textViewScroll);


        imageViewBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });


        editTextFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    if(relativeLayoutOtherError.getVisibility() == View.VISIBLE){
                        relativeLayoutOtherError.setVisibility(View.GONE);
                    }
                }
            }
        });


        ratingBarFeedback.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating <= 0) {
                    textViewRateText.setText("");
                } else if (rating > 0 && rating <= 1) {
                    textViewRateText.setText("Hated it");
                } else if (rating > 1 && rating <= 2) {
                    textViewRateText.setText("Disliked it");
                } else if (rating > 2 && rating <= 3) {
                    textViewRateText.setText("It's OK");
                } else if (rating > 3 && rating <= 4) {
                    textViewRateText.setText("Liked it");
                } else if (rating > 4 && rating <= 5) {
                    textViewRateText.setText("Loved it");
                }

                if(FeedbackMode.SUPPORT != feedbackMode && Data.feedbackReasons.size() > 0) {
                    if (rating > 0 && rating <= 3) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                textViewWhatImprove.setVisibility(View.VISIBLE);
                                listViewFeedbackReasons.setVisibility(View.VISIBLE);

                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editTextFeedback.getLayoutParams();
                                layoutParams.height = (int) (ASSL.Yscale() * 150);
                                editTextFeedback.setLayoutParams(layoutParams);
                            }
                        }, 205);
                    } else {
                        textViewWhatImprove.setVisibility(View.GONE);
                        listViewFeedbackReasons.setVisibility(View.GONE);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editTextFeedback.getLayoutParams();
                        layoutParams.height = (int) (ASSL.Yscale() * 200);
                        editTextFeedback.setLayoutParams(layoutParams);
                    }
                }

            }
        });



        buttonSubmitFeedback.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String feedbackStr = editTextFeedback.getText().toString().trim();
                int rating = (int) ratingBarFeedback.getRating();
                rating = Math.abs(rating);
                Log.e("rating screen =", "= feedbackStr = " + feedbackStr + " , rating = " + rating);

                String feedbackReasons = feedbackReasonsAdapter.getSelectedReasons();
                boolean isLastReasonSelected = feedbackReasonsAdapter.isLastSelected();

                if (0 == rating) {
                    DialogPopup.alertPopup(FeedbackActivity.this, "", "We take your feedback seriously. Please give us a rating");
                    FlurryEventLogger.event(FEEDBACK_WITH_COMMENTS);
                } else {
                    if(FeedbackMode.SUPPORT != feedbackMode  && Data.feedbackReasons.size() > 0 && rating <= 3){
                        if(feedbackReasons.length() > 0){
                            if(isLastReasonSelected && feedbackStr.length() == 0){
                                relativeLayoutOtherError.setVisibility(View.VISIBLE);
                                return;
                            }
                        }
                        else{
                            DialogPopup.alertPopup(FeedbackActivity.this, "", "Please provide your reason for the rating");
                            return;
                        }
                    }

                    if (feedbackStr.length() > 300) {
                        editTextFeedback.requestFocus();
                        editTextFeedback.setError("Review must be in 300 letters.");
                    } else {
                        if (FeedbackMode.AFTER_RIDE == feedbackMode) {
                            submitFeedbackToDriverAsync(FeedbackActivity.this, Data.cEngagementId, Data.cDriverId, rating, feedbackStr, feedbackReasons);
                            FlurryEventLogger.event(FEEDBACK_AFTER_RIDE_YES);
                            if(feedbackStr.length() > 0){
                                FlurryEventLogger.event(FEEDBACK_WITH_COMMENTS);
                            }
                        } else if (FeedbackMode.PAST_RIDE == feedbackMode) {
                            submitFeedbackToDriverAsync(FeedbackActivity.this, "" + pastEngagementId, "" + pastDriverId, rating, feedbackStr, feedbackReasons);
                            if(feedbackStr.length() > 0){
                                FlurryEventLogger.event(FEEDBACK_WITH_COMMENTS);
                            }
                        } else {
                            submitFeedbackSupportAsync(FeedbackActivity.this, rating, feedbackStr);
                            if(feedbackStr.length() > 0){
                                FlurryEventLogger.event(FEEDBACK_COMMENTS_PROVIDED);
                            }
                            FlurryEventLogger.event(FEEDBACK_SUBMITTED);
                        }
                    }
                }
            }
        });

        relativeLayoutSkip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                skipFeedbackForCustomerAsync(FeedbackActivity.this, Data.cEngagementId);
                FlurryEventLogger.event(FEEDBACK_AFTER_RIDE_NO);
            }
        });




        try {
            if (getIntent().hasExtra(FeedbackMode.class.getName())) {
                int mode = getIntent().getIntExtra(FeedbackMode.class.getName(), feedbackMode.getOrdinal());
                if (FeedbackMode.AFTER_RIDE.getOrdinal() == mode) {
                    feedbackMode = FeedbackMode.AFTER_RIDE;
                } else if (FeedbackMode.PAST_RIDE.getOrdinal() == mode) {
                    feedbackMode = FeedbackMode.PAST_RIDE;
                    position = getIntent().getIntExtra("position", 0);
                    pastDriverId = getIntent().getIntExtra("driver_id", 0);
                    pastEngagementId = getIntent().getIntExtra("engagement_id", 0);
                } else {
                    feedbackMode = FeedbackMode.SUPPORT;
                }
            } else {
                performBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            performBackPressed();
        }


        if (FeedbackMode.AFTER_RIDE == feedbackMode) {
            textViewTitle.setText("RATE YOUR EXPERIENCE");
            relativeLayoutSkip.setVisibility(View.VISIBLE);
        } else if (FeedbackMode.PAST_RIDE == feedbackMode) {
            textViewTitle.setText("RATE YOUR EXPERIENCE");
            relativeLayoutSkip.setVisibility(View.GONE);
        } else {
            textViewTitle.setText("FEEDBACK");
            relativeLayoutSkip.setVisibility(View.GONE);
        }

        textViewWhatImprove.setVisibility(View.GONE);
        listViewFeedbackReasons.setVisibility(View.GONE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editTextFeedback.getLayoutParams();
        layoutParams.height = (int)(ASSL.Yscale() * 200);
        editTextFeedback.setLayoutParams(layoutParams);



        linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(
            new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    // r will be populated with the coordinates of your view
                    // that area still visible.
                    linearLayoutMain.getWindowVisibleDisplayFrame(r);

                    int heightDiff = linearLayoutMain.getRootView()
                        .getHeight() - (r.bottom - r.top);
                    if (heightDiff > 100) { // if more than 100 pixels, its
                        // probably a keyboard...

                        /************** Adapter for the parent List *************/

                        ViewGroup.LayoutParams params_12 = textViewScroll
                            .getLayoutParams();

                        params_12.height = (int) (heightDiff);

//                        textViewScroll.setLayoutParams(params_12);
                        textViewScroll.requestLayout();
                        editTextFeedback.setHint("");
                        if(!scrolled) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.smoothScrollTo(0, editTextFeedback.getTop() - ((int) (ASSL.Yscale() * 15)));
                                }
                            }, 200);
                            scrolled = true;
                        }

                    } else {

                        ViewGroup.LayoutParams params = textViewScroll
                            .getLayoutParams();
                        params.height = 0;
//                        textViewScroll.setLayoutParams(params);
                        textViewScroll.requestLayout();
                        editTextFeedback.setHint("Please share your valuable feedback");
                        scrolled = false;

                    }
                }
            });


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


    public void performBackPressed() {
		if (FeedbackMode.SUPPORT == feedbackMode) {
			startActivity(new Intent(this, SupportActivity.class));
		}
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
    }


    @Override
    protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
        super.onDestroy();
    }


    public void submitFeedbackToDriverAsync(final Activity activity, String engagementId, String ratingReceiverId, final int givenRating, String feedbackText, String feedbackReasons) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            DialogPopup.showLoadingDialogDownwards(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();

            params.put("access_token", Data.userData.accessToken);
            params.put("given_rating", "" + givenRating);
            params.put("engagement_id", engagementId);
            params.put("driver_id", ratingReceiverId);
            params.put("feedback", feedbackText);
            params.put("feedback_reasons", feedbackReasons);

            Log.i("params", "=" + params);

            RestClient.getApiServices().rateTheDriver(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "rateTheDriver response = " + responseStr);
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        int flag = jObj.getInt("flag");
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                Toast.makeText(activity, "Thank you for your valuable feedback", Toast.LENGTH_SHORT).show();
                                if (FeedbackMode.AFTER_RIDE == feedbackMode && HomeActivity.appInterruptHandler != null) {
                                    HomeActivity.appInterruptHandler.onAfterRideFeedbackSubmitted(givenRating, false);
                                } else if (FeedbackMode.PAST_RIDE == feedbackMode && RideTransactionsActivity.updateRideTransaction != null) {
                                    RideTransactionsActivity.updateRideTransaction.updateRideTransaction(position);
                                }
                                try {
                                    Data.driverInfos.clear();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                finish();
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            } else {
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                    }
                    DialogPopup.dismissLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "rateTheDriver error="+error.toString());
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                }
            });
        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }
    }


    public void skipFeedbackForCustomerAsync(final Activity activity, String engagementId) {

		try {
			final HashMap<String, String> params = new HashMap<>();
			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", engagementId);

			try { Data.driverInfos.clear(); } catch (Exception e) { e.printStackTrace(); }

			HomeActivity.feedbackSkipped = true;
			HomeActivity.appInterruptHandler.onAfterRideFeedbackSubmitted(0, true);
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            RestClient.getApiServices().skipRatingByCustomer(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.e(TAG, "skipRatingByCustomer response="+responseStr);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "skipRatingByCustomer error="+error.toString());
                }
            });

			Database2.getInstance(activity).insertPendingAPICall(activity,
                    PendingCall.SKIP_RATING_BY_CUSTOMER.getPath(), params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    public void submitFeedbackSupportAsync(final Activity activity, final int givenRating, final String feedbackText) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            DialogPopup.showLoadingDialogDownwards(activity, "Loading...");

            HashMap<String, String> params = new HashMap<>();

            params.put("access_token", Data.userData.accessToken);
            params.put("given_rating", "" + givenRating);
            params.put("feedback", feedbackText);

            Log.i("params", "=" + params);

            RestClient.getApiServices().submitFeedback(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt settleUserDebt, Response response) {
                    String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                    Log.i(TAG, "submitFeedback response = " + responseStr);
                    try {
                        JSONObject jObj = new JSONObject(responseStr);
                        int flag = jObj.getInt("flag");
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                            if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                String error = jObj.getString("error");
                                DialogPopup.alertPopup(activity, "", error);
                            } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                Data.supportFeedbackSubmitted = true;
                                finish();
                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            } else {
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                    }
                    DialogPopup.dismissLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "submitFeedback error="+error.toString());
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                }
            });
        } else {
            DialogPopup.dialogNoInternet(FeedbackActivity.this,
                    Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
                    new Utils.AlertCallBackWithButtonsInterface() {
                        @Override
                        public void positiveClick(View v) {
                            submitFeedbackSupportAsync(activity, givenRating, feedbackText);
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


}
