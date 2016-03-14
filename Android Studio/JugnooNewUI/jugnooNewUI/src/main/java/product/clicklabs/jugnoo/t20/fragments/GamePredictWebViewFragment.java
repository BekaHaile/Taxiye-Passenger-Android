package product.clicklabs.jugnoo.t20.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Log;

@SuppressLint("ValidFragment")
public class GamePredictWebViewFragment extends Fragment implements FlurryEventNames, Constants {

	private final String TAG = GamePredictWebViewFragment.class.getSimpleName();

	private RelativeLayout relative;

	private WebView webView;
	private ImageView imageViewProgressBar;

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
        rootView = inflater.inflate(R.layout.fragment_game_predict_webview, container, false);

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

		imageViewProgressBar = (ImageView) rootView.findViewById(R.id.imageViewProgressBar);
		imageViewProgressBar.setBackgroundResource(R.drawable.anim);
		imageViewProgressBar.post(new Runnable() {
			@Override
			public void run() {
				AnimationDrawable frameAnimation =
						(AnimationDrawable) imageViewProgressBar.getBackground();
				frameAnimation.start();
			}
		});
		imageViewProgressBar.setVisibility(View.GONE);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setDatabaseEnabled(true);
		webView.setWebChromeClient(new WebChromeClient() {
		});
		webView.setWebViewClient(new MyAppWebViewClient());

		webView.loadUrl(Data.userData.getGamePredictUrl());

		StringBuilder sb = new StringBuilder();
		sb.append(Data.userData.getGamePredictUrl()).append("?")
				.append(Constants.KEY_ACCESS_TOKEN).append("=").append(Data.userData.getPublicAccessToken());
		Log.i(TAG, "link to hit="+sb.toString());
		webView.loadUrl(sb.toString());

		return rootView;
	}

	class MyAppWebViewClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			imageViewProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (webView.getProgress() > 70) {
				imageViewProgressBar.setVisibility(View.GONE);
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			imageViewProgressBar.setVisibility(View.GONE);
		}
	}
    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}

}
