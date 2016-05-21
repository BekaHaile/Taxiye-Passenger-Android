package product.clicklabs.jugnoo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class HelpParticularActivity extends BaseActivity implements Constants {

    private final String TAG = HelpParticularActivity.class.getSimpleName();

    LinearLayout relative;

    ImageView imageViewBack;
    TextView textViewTitle;
    private ImageView imageViewJugnooAnimation;
    private AnimationDrawable jugnooAnimation;
    TextView textViewInfo;
    WebView webview;

    public static HelpSection helpSection = HelpSection.FARE_DETAILS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_particular);

        relative = (LinearLayout) findViewById(R.id.relative);
        new ASSL(HelpParticularActivity.this, relative, 1134, 720, false);

        imageViewJugnooAnimation = (ImageView)findViewById(R.id.imageViewJugnooAnimation);
        jugnooAnimation = (AnimationDrawable) imageViewJugnooAnimation.getBackground();
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));

        textViewInfo = (TextView) findViewById(R.id.textViewInfo);
        textViewInfo.setTypeface(Fonts.mavenMedium(this));

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);

        textViewTitle.measure(0, 0);
        int mWidth = textViewTitle.getMeasuredWidth();
        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, mWidth));


        //override the web client to open all links in the same webview
        webview.setWebViewClient(new MyWebViewClient1());


        if (helpSection != null) {
            textViewTitle.setText(helpSection.getName().toUpperCase());
            if(helpSection.getOrdinal() == HelpSection.FAQ.getOrdinal()){
                textViewTitle.setAllCaps(false);
                textViewTitle.setText("FAQs");
            }
        }


        imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });


        textViewInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getFareDetailsAsync(HelpParticularActivity.this);
            }
        });

        getFareDetailsAsync(HelpParticularActivity.this);

    }

    boolean loadingFinished = true;
    boolean redirect = false;
    boolean apiCalling = true;

    private class MyWebViewClient1 extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
            if (!loadingFinished) {
                redirect = true;
            }
            loadingFinished = false;
            view.loadUrl(urlNewString);
            Log.e("shouldOverrideUrlLoading", "urlNewString=" + urlNewString);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            loadingFinished = false;
            //SHOW LOADING IF IT ISNT ALREADY VISIBLE
            imageViewJugnooAnimation.setVisibility(View.VISIBLE);
            jugnooAnimation.start();
            Log.e("onPageStarted", "url="+url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(!redirect){
                loadingFinished = true;
            }

            if(loadingFinished && !redirect && !apiCalling){
                //HIDE LOADING IT HAS FINISHED
                imageViewJugnooAnimation.setVisibility(View.GONE);
                jugnooAnimation.stop();
            } else{
                redirect = false;
            }
            Log.e("onPageFinished", "url="+url);

        }
    }




    public void openHelpData(String data, boolean errorOccured) {
        if (errorOccured) {
            textViewInfo.setVisibility(View.VISIBLE);
            textViewInfo.setText(data);
            webview.setVisibility(View.GONE);
            imageViewJugnooAnimation.setVisibility(View.GONE);
            jugnooAnimation.stop();
        } else {
            textViewInfo.setVisibility(View.GONE);
            webview.setVisibility(View.VISIBLE);
            loadHTMLContent(data);
        }
    }

    public void loadHTMLContent(String data) {
        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        webview.loadDataWithBaseURL("", data, mimeType, encoding, "");
    }


    /**
     * ASync for fetching information for supplied section
     */
    public void getFareDetailsAsync(final Activity activity) {
		try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                if (helpSection != null) {
                    apiCalling = true;
                    //                    DialogPopup.showLoadingDialog(activity, "Loading...");
                    imageViewJugnooAnimation.setVisibility(View.VISIBLE);
                    jugnooAnimation.start();
                    textViewInfo.setVisibility(View.GONE);
                    webview.setVisibility(View.GONE);
                    loadHTMLContent("");

                    Log.e("helpSection", "=" + helpSection.getOrdinal() + " " + helpSection.getName());

                    HashMap<String, String> params = new HashMap<>();
                    params.put("access_token", Data.userData.accessToken);
                    params.put("section", "" + helpSection.getOrdinal());

                    if (HelpSection.FARE_DETAILS.getOrdinal() == helpSection.getOrdinal()) {
                        if (Data.lastRefreshLatLng != null) {
                            params.put(KEY_LATITUDE, "" + Data.lastRefreshLatLng.latitude);
                            params.put(KEY_LONGITUDE, "" + Data.lastRefreshLatLng.longitude);
                        } else if (HomeActivity.myLocation != null) {
                            params.put(KEY_LATITUDE, "" + HomeActivity.myLocation.getLatitude());
                            params.put(KEY_LONGITUDE, "" + HomeActivity.myLocation.getLongitude());
                        }
                    } else {
                        if (HomeActivity.myLocation != null) {
                            params.put(KEY_LATITUDE, "" + HomeActivity.myLocation.getLatitude());
                            params.put(KEY_LONGITUDE, "" + HomeActivity.myLocation.getLongitude());
                        } else if (Data.lastRefreshLatLng != null) {
                            params.put(KEY_LATITUDE, "" + Data.lastRefreshLatLng.latitude);
                            params.put(KEY_LONGITUDE, "" + Data.lastRefreshLatLng.longitude);
                        }
                    }

                    RestClient.getApiServices().getInformation(params, new Callback<SettleUserDebt>() {
                        @Override
                        public void success(SettleUserDebt settleUserDebt, Response response) {
                            String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
                            apiCalling = false;
                            Log.i(TAG, "getInformation response = " + responseStr);
                            try {
                                JSONObject jObj = new JSONObject(responseStr);
                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    String data = jObj.getString("data");
                                    openHelpData(data, false);
                                } else {
                                    openHelpData("Some error occured. Tap to retry.", true);
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                openHelpData("Some error occured. Tap to retry.", true);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            try {
                                Log.e(TAG, "getInformation error="+error.toString());
                                apiCalling = false;
                                imageViewJugnooAnimation.setVisibility(View.GONE);
                                jugnooAnimation.stop();
                                openHelpData("Some error occured. Tap to retry.", true);
                                //                                DialogPopup.dismissLoadingDialog();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } else {
                DialogPopup.dialogNoInternet(HelpParticularActivity.this,
                        Data.CHECK_INTERNET_TITLE, Data.CHECK_INTERNET_MSG,
                        new Utils.AlertCallBackWithButtonsInterface() {
                            @Override
                            public void positiveClick(View v) {
                                getFareDetailsAsync(activity);
                            }

                            @Override
                            public void neutralClick(View v) {

                            }

                            @Override
                            public void negativeClick(View v) {

                            }
                        });
            }
        } catch (Exception e) {
			e.printStackTrace();
		}
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
        super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
    }

}

