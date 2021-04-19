package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.utils.ASSL;
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
    private ProgressBar imageViewJugnooAnimation;
    //private AnimationDrawable jugnooAnimation;
    TextView textViewInfo;
    WebView webview;

    public static HelpSection helpSection = HelpSection.ABOUT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_particular);

        relative = (LinearLayout) findViewById(R.id.relative);
        new ASSL(HelpParticularActivity.this, relative, 1134, 720, false);

        imageViewJugnooAnimation = (ProgressBar)findViewById(R.id.imageViewJugnooAnimation);
        //jugnooAnimation = (AnimationDrawable) imageViewJugnooAnimation.getBackground();
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.avenirNext(this));

        textViewInfo = (TextView) findViewById(R.id.textViewInfo);
        textViewInfo.setTypeface(Fonts.mavenMedium(this));

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);

        textViewTitle.getPaint().setShader(Utils.textColorGradient(this, textViewTitle));


        //override the web client to open all links in the same webview
        webview.setWebViewClient(new MyWebViewClient1());


        if (helpSection != null) {
            textViewTitle.setText(getString(helpSection.getName()));
            if(helpSection.getOrdinal() == HelpSection.FAQ.getOrdinal()){
                textViewTitle.setAllCaps(false);
                textViewTitle.setText(R.string.faqs);
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
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            loadingFinished = false;
            //SHOW LOADING IF IT ISNT ALREADY VISIBLE
            imageViewJugnooAnimation.setVisibility(View.VISIBLE);
            //jugnooAnimation.start();
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
                //jugnooAnimation.stop();
            } else{
                redirect = false;
            }
            Log.e("onPageFinished", "url="+url);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!loadingFinished) {
                redirect = true;
            }
            loadingFinished = false;
            if (url == null) {
                return false;
            }
            if (url.startsWith("market://")) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            if (url.startsWith("mailto:")) {

                try {
                    List<String> to = new ArrayList<String>();
                    List<String> cc = new ArrayList<String>();
                    List<String> bcc = new ArrayList<String>();
                    String subject = null;
                    String body = null;

                    url = url.replaceFirst("mailto:", "");

                    String[] urlSections = url.split("&");
                    if (urlSections.length >= 2) {

                        to.addAll(Arrays.asList(urlSections[0].split(",")));

                        for (int i = 1; i < urlSections.length; i++) {
                            String urlSection = urlSections[i];
                            String[] keyValue = urlSection.split("=");

                            if (keyValue.length == 2) {
                                String key = keyValue[0];
                                String value = keyValue[1];

                                value = URLDecoder.decode(value, "UTF-8");

                                if (key.equals("cc")) {
                                    cc.addAll(Arrays.asList(url.split(",")));
                                }
                                else if (key.equals("bcc")) {
                                    bcc.addAll(Arrays.asList(url.split(",")));
                                }
                                else if (key.equals("subject")) {
                                    subject = value;
                                }
                                else if (key.equals("body")) {
                                    body = value;
                                }
                            }
                        }
                    }
                    else {
                        String[] toArr = url.split(",");
                        for(String toI : toArr){
                            toI = URLDecoder.decode(toI, "UTF-8");
                            to.add(toI);
                        }
//                        to.addAll(Arrays.asList(url.split(",")));
                    }

                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("message/rfc822");

                    String[] dummyStringArray = new String[0]; // For list to array conversion
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, to.toArray(dummyStringArray));
                    if (cc.size() > 0) {
                        emailIntent.putExtra(android.content.Intent.EXTRA_CC, cc.toArray(dummyStringArray));
                    }
                    if (bcc.size() > 0) {
                        emailIntent.putExtra(android.content.Intent.EXTRA_BCC, bcc.toArray(dummyStringArray));
                    }
                    if (subject != null) {
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                    }
                    if (body != null) {
                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
                    }
                    view.getContext().startActivity(emailIntent);

                    return true;
                }
                catch (Exception e) {
      /* Won't happen*/
                }

            }
            return false;
        }
    }




    public void openHelpData(String data, boolean errorOccured) {
        if (errorOccured) {
            textViewInfo.setVisibility(View.VISIBLE);
            textViewInfo.setText(data);
            webview.setVisibility(View.GONE);
            imageViewJugnooAnimation.setVisibility(View.GONE);
            //jugnooAnimation.stop();
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
            if (MyApplication.getInstance().isOnline()) {
                if (helpSection != null) {
                    apiCalling = true;
                    //                    DialogPopup.showLoadingDialog(activity, "Loading...");
                    imageViewJugnooAnimation.setVisibility(View.VISIBLE);
                    //jugnooAnimation.start();
                    textViewInfo.setVisibility(View.GONE);
                    webview.setVisibility(View.GONE);
                    loadHTMLContent("");

                    Log.e("helpSection", "=" + helpSection.getOrdinal() + " " + helpSection.getName());

                    HashMap<String, String> params = new HashMap<>();
//                    params.put("access_token", Data.userData.accessToken);
                    params.put("section", "" + helpSection.getOrdinal());

                    if (HelpSection.FARE_DETAILS.getOrdinal() == helpSection.getOrdinal()) {
                        if (Data.autoData.getLastRefreshLatLng() != null) {
                            params.put(KEY_LATITUDE, "" + Data.autoData.getLastRefreshLatLng().latitude);
                            params.put(KEY_LONGITUDE, "" + Data.autoData.getLastRefreshLatLng().longitude);
                        } else if (HomeActivity.myLocation != null) {
                            params.put(KEY_LATITUDE, "" + HomeActivity.myLocation.getLatitude());
                            params.put(KEY_LONGITUDE, "" + HomeActivity.myLocation.getLongitude());
                        }
                    } else {
                        if (HomeActivity.myLocation != null) {
                            params.put(KEY_LATITUDE, "" + HomeActivity.myLocation.getLatitude());
                            params.put(KEY_LONGITUDE, "" + HomeActivity.myLocation.getLongitude());
                        } else if (Data.autoData.getLastRefreshLatLng() != null) {
                            params.put(KEY_LATITUDE, "" + Data.autoData.getLastRefreshLatLng().latitude);
                            params.put(KEY_LONGITUDE, "" + Data.autoData.getLastRefreshLatLng().longitude);
                        }
                    }

                    new HomeUtil().putDefaultParams(params);
                    RestClient.getApiService().getInformation(params, new Callback<SettleUserDebt>() {
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
                                    openHelpData(getString(R.string.some_error_occured_try_again), true);
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                openHelpData(getString(R.string.some_error_occured_try_again), true);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            try {
                                Log.e(TAG, "getInformation error="+error.toString());
                                apiCalling = false;
                                imageViewJugnooAnimation.setVisibility(View.GONE);
                                //jugnooAnimation.stop();
                                openHelpData(getString(R.string.some_error_occured_try_again), true);
                                //                                DialogPopup.dismissLoadingDialog();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } else {
                DialogPopup.dialogNoInternet(HelpParticularActivity.this,
                        activity.getString(R.string.connection_lost_title), activity.getString(R.string.connection_lost_desc),
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

