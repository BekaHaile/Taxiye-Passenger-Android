package product.clicklabs.jugnoo.t20.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;

import org.apache.http.util.EncodingUtils;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;

@SuppressLint("ValidFragment")
public class T20WebViewFragment extends Fragment implements FlurryEventNames, Constants {

	private final String TAG = T20WebViewFragment.class.getSimpleName();

	private RelativeLayout relative;

	private WebView webView;
	private ProgressBar progressBar;

	private View rootView;
    private FragmentActivity activity;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
		FlurryAgent.onEvent(TAG + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_t20_webview, container, false);

        activity = getActivity();

		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		try {
			if(relative != null) {
				new ASSL(activity, relative, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		webView = (WebView) rootView.findViewById(R.id.webView);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		progressBar.setVisibility(View.GONE);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setDatabaseEnabled(true);
		webView.setWebChromeClient(new WebChromeClient() {});
		webView.setWebViewClient(new MyAppWebViewClient());

		webView.loadUrl(Data.userData.getGameUrl());

		String postData = Constants.KEY_PUBLIC_ACCESS_TOKEN+"="+Data.userData.getPublicAccessToken();
		webView.postUrl(Data.userData.getGameUrl(), EncodingUtils.getBytes(postData, "BASE64"));

		return rootView;
	}

	class MyAppWebViewClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (webView.getProgress() > 70) {
				progressBar.setVisibility(View.GONE);
			}
		}

	}
    @Override
	public void onDestroy() {
		super.onDestroy();
		webView.destroy();
        ASSL.closeActivity(relative);
        System.gc();
	}

}
