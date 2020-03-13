package product.clicklabs.jugnoo.t20.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.t20.T20Activity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;

@SuppressLint("ValidFragment")
public class GamePredictWebViewFragment extends Fragment implements  Constants {

	private final String TAG = GamePredictWebViewFragment.class.getSimpleName();

	private RelativeLayout relative;

	private WebView webView;
	private ImageView imageViewProgressBar, imageViewBack;

	private View rootView;
    private T20Activity activity;
	private TextView textViewTitle;

    @Override
    public void onStart() {
        super.onStart();
//        FlurryAgent.init(activity, Config.getFlurryKey());
//        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
//		FlurryAgent.onEvent(TAG + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
//        FlurryAgent.onEndSession(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game_predict_webview, container, false);

		try {
			activity = (T20Activity) getActivity();

			relative = (RelativeLayout) rootView.findViewById(R.id.relative);
			try {
				if(relative != null) {
					new ASSL(activity, relative, 1134, 720, false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
			textViewTitle.setTypeface(Fonts.avenirNext(getActivity()));
			textViewTitle.setText(MyApplication.getInstance().ACTIVITY_NAME_PLAY);
			textViewTitle.getPaint().setShader(Utils.textColorGradient(getActivity(), textViewTitle));
			imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);

			webView = (WebView) rootView.findViewById(R.id.webView);
			webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

			imageViewProgressBar = (ImageView) rootView.findViewById(R.id.imageViewProgressBar);
			imageViewProgressBar.setBackgroundResource(R.drawable.loader_new_frame_anim);
			imageViewProgressBar.post(new Runnable() {
				@Override
				public void run() {
					AnimationDrawable frameAnimation =
							(AnimationDrawable) imageViewProgressBar.getBackground();
					frameAnimation.start();
				}
			});
			imageViewProgressBar.setVisibility(View.GONE);

			webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setDomStorageEnabled(true);
			webView.getSettings().setDatabaseEnabled(true);
			webView.setWebChromeClient(new WebChromeClient() {
			});
			webView.setWebViewClient(new MyAppWebViewClient());

//			webView.loadUrl(Data.userData.getGamePredictUrl());

			imageViewBack.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					activity.performBackPressed();
				}
			});

			if(!HomeActivity.checkIfUserDataNull(activity)) {
				StringBuilder sb = new StringBuilder();
				sb.append(Data.userData.getGamePredictUrl()).append("?")
						.append(Constants.KEY_ACCESS_TOKEN).append("=").append(Data.userData.getPublicAccessToken());
				Log.i(TAG, "link to hit=" + sb.toString());
				webView.loadUrl(sb.toString());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}


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
			Log.e(TAG, "onReceivedError description="+description);
			imageViewProgressBar.setVisibility(View.GONE);
			DialogPopup.alertPopup(activity, "", description);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Uri uri;
			try {
				uri = Uri.parse(url);
			} catch (NullPointerException e) {
				return true;
			}

			String host = uri.getHost(); //Host is null when user clicked on email, phone number, ...
			if (host != null && host.equals(getString(R.string.app_domain))) {
				// This is my web site, so do not override; let my WebView load the page
				return false;
			}
			else {
				Log.i(TAG, "url="+url);
				// Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs or anything else (email, phone number, ...)
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(intent);
				return true;
			}
		}
	}
    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}

}
