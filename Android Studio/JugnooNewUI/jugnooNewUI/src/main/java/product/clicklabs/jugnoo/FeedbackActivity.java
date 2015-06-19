package product.clicklabs.jugnoo;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.FeedbackMode;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class FeedbackActivity extends Activity {

    RelativeLayout relative;

    RelativeLayout topBar;
    TextView textViewTitle;
    ImageView imageViewBack;

    RatingBar ratingBarFeedback;
    TextView textViewRateText;
    EditText editTextFeedback;
    Button buttonSubmitFeedback;

    RelativeLayout relativeLayoutSkip;
    TextView textViewSkip;

    ScrollView scrollView;
    LinearLayout linearLayoutMain;
    TextView textViewScroll;

    FeedbackMode feedbackMode = FeedbackMode.SUPPORT;
    int pastDriverId = 0, pastEngagementId = 0, position = 0;

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

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(this, (ViewGroup) relative, 1134, 720, false);

        topBar = (RelativeLayout) findViewById(R.id.topBar);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        ratingBarFeedback = (RatingBar) findViewById(R.id.ratingBarFeedback);
        ratingBarFeedback.setRating(0);
        textViewRateText = (TextView) findViewById(R.id.textViewRateText);
        textViewRateText.setTypeface(Fonts.latoRegular(this));
        textViewRateText.setText("");
        editTextFeedback = (EditText) findViewById(R.id.editTextFeedback);
        editTextFeedback.setTypeface(Fonts.latoRegular(this));
        buttonSubmitFeedback = (Button) findViewById(R.id.buttonSubmitFeedback);
        buttonSubmitFeedback.setTypeface(Fonts.latoRegular(this));

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


        topBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resetEditText();
            }
        });
        linearLayoutMain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resetEditText();
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
                resetEditText();
            }
        });



        buttonSubmitFeedback.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String feedbackStr = editTextFeedback.getText().toString().trim();
                int rating = (int) ratingBarFeedback.getRating();
                rating = Math.abs(rating);
                Log.e("rating screen =", "= feedbackStr = " + feedbackStr + " , rating = " + rating);


                //We take your feedback seriously. Please give us a rating

                if (0 == rating) {
                    DialogPopup.alertPopup(FeedbackActivity.this, "", "We take your feedback seriously. Please give us a rating");
                } else {
                    if (feedbackStr.length() > 300) {
                        editTextFeedback.requestFocus();
                        editTextFeedback.setError("Review must be in 300 letters.");
                    } else {
                        if (FeedbackMode.AFTER_RIDE == feedbackMode) {
                            submitFeedbackToDriverAsync(FeedbackActivity.this, Data.cEngagementId, Data.cDriverId, rating, feedbackStr);
                            FlurryEventLogger.reviewSubmitted(Data.userData.accessToken, Data.cEngagementId);
                        } else if (FeedbackMode.PAST_RIDE == feedbackMode) {
                            submitFeedbackToDriverAsync(FeedbackActivity.this, "" + pastEngagementId, "" + pastDriverId, rating, feedbackStr);
                            FlurryEventLogger.reviewSubmitted(Data.userData.accessToken, "" + pastEngagementId);
                        } else {
                            submitFeedbackSupportAsync(FeedbackActivity.this, rating, feedbackStr);
                        }
                    }
                }
                resetEditText();
            }
        });

        relativeLayoutSkip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                skipFeedbackForCustomerAsync(FeedbackActivity.this, Data.cEngagementId);
                resetEditText();
            }
        });



        editTextFeedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextFeedback.setHint("");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, buttonSubmitFeedback.getTop());
                    }
                }, 200);
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

                        textViewScroll.setLayoutParams(params_12);
                        textViewScroll.requestLayout();

                    } else {

                        ViewGroup.LayoutParams params = textViewScroll
                            .getLayoutParams();
                        params.height = 0;
                        textViewScroll.setLayoutParams(params);
                        textViewScroll.requestLayout();

                    }
                }
            });


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void resetEditText(){
        editTextFeedback.setHint("How did you like your experience with Jugnoo? Please share your valuable feedback");
        Utils.hideSoftKeyboard(FeedbackActivity.this, editTextFeedback);
    }


    public void performBackPressed() {
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


    public void submitFeedbackToDriverAsync(final Activity activity, String engagementId, String ratingReceiverId, final int givenRating, String feedbackText) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

            RequestParams params = new RequestParams();

            params.put("access_token", Data.userData.accessToken);
            params.put("given_rating", "" + givenRating);
            params.put("engagement_id", engagementId);
            params.put("driver_id", ratingReceiverId);
            params.put("feedback", feedbackText);

            Log.i("params", "=" + params);

            AsyncHttpClient client = Data.getClient();
            client.post(Config.getServerUrl() + "/rate_the_driver", params,
                new CustomAsyncHttpResponseHandler() {
                    private JSONObject jObj;

                    @Override
                    public void onFailure(Throwable arg3) {
                        Log.e("request fail", arg3.toString());
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.i("Server response", "response = " + response);
                        try {
                            jObj = new JSONObject(response);
                            int flag = jObj.getInt("flag");
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    Toast.makeText(activity, "Thank you for your valuable feedback", Toast.LENGTH_SHORT).show();
                                    if (FeedbackMode.AFTER_RIDE == feedbackMode && HomeActivity.appInterruptHandler != null) {
                                        HomeActivity.appInterruptHandler.onAfterRideFeedbackSubmitted(givenRating, false);
                                    }
                                    else if(FeedbackMode.PAST_RIDE == feedbackMode && RideTransactionsActivity.updateRideTransaction != null){
                                        RideTransactionsActivity.updateRideTransaction.updateRideTransaction(position);
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
                });
        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }
    }


    public void skipFeedbackForCustomerAsync(final Activity activity, String engagementId) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

            RequestParams params = new RequestParams();

            params.put("access_token", Data.userData.accessToken);
            params.put("engagement_id", engagementId);

            Log.i("access_token", "=" + Data.userData.accessToken);
            Log.i("engagement_id", engagementId);

            AsyncHttpClient client = Data.getClient();
            client.post(Config.getServerUrl() + "/skip_rating_by_customer", params,
                new CustomAsyncHttpResponseHandler() {
                    private JSONObject jObj;

                    @Override
                    public void onFailure(Throwable arg3) {
                        Log.e("request fail", arg3.toString());
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.i("Server response", "response = " + response);
                        try {
                            jObj = new JSONObject(response);
                            int flag = jObj.getInt("flag");
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                    String error = jObj.getString("error");
                                    DialogPopup.alertPopup(activity, "", error);
                                } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    HomeActivity.appInterruptHandler.onAfterRideFeedbackSubmitted(0, true);
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
                });
        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }
    }


    public void submitFeedbackSupportAsync(final Activity activity, int givenRating, String feedbackText) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

            RequestParams params = new RequestParams();

            params.put("access_token", Data.userData.accessToken);
            params.put("given_rating", "" + givenRating);
            params.put("feedback", feedbackText);

            Log.i("params", "=" + params);

            AsyncHttpClient client = Data.getClient();
            client.post(Config.getServerUrl() + "/submit_feedback", params,
                new CustomAsyncHttpResponseHandler() {
                    private JSONObject jObj;

                    @Override
                    public void onFailure(Throwable arg3) {
                        Log.e("request fail", arg3.toString());
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.i("Server response", "response = " + response);
                        try {
                            jObj = new JSONObject(response);
                            int flag = jObj.getInt("flag");
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                    String error = jObj.getString("error");
                                    DialogPopup.alertPopup(activity, "", error);
                                } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    Toast.makeText(activity, "Thank you for your valuable feedback", Toast.LENGTH_SHORT).show();
                                    performBackPressed();
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
                });
        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }
    }


}
