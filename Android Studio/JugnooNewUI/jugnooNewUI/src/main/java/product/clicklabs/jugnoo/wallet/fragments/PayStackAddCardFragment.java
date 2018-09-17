package product.clicklabs.jugnoo.wallet.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.stripe.StripeCardsStateListener;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;

/**
 * Created by Parminder Saini on 07/05/18.
 */
public class PayStackAddCardFragment extends Fragment {


    private static final String TAG = PayStackAddCardFragment.class.getName();
    private PayStackWebViewClient payStackWebViewClient;
    private WebView webView;

    public static final String ARGS_URL_TO_OPEN = "url_to_open";
    private String urlToLoad;
    public static final String ADD_CARD_SUCCESS_URL = "/static/success";
    public static final String ADD_CARD_FAILURE_URL = "/static/failure";
    private StripeCardsStateListener stripeCardsStateListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof StripeCardsStateListener){
            stripeCardsStateListener = (StripeCardsStateListener) context;
        }


    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments()==null || !getArguments().containsKey(ARGS_URL_TO_OPEN)) {
            throw new IllegalArgumentException("No url passed for add card");
        }

        urlToLoad = getArguments().getString(ARGS_URL_TO_OPEN);
        removeAllCookies(requireActivity());


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_add_card_pay_stack,container,false);
        webView = rootView.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setPadding(0, 0, 0, 0);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        payStackWebViewClient = new PayStackWebViewClient();
        webView.setWebViewClient(payStackWebViewClient);
        webView.loadUrl(urlToLoad);
        TextView tvTitle =  ((TextView) rootView.findViewById(R.id.textViewTitle));
        tvTitle.setTypeface(Fonts.avenirNext(getActivity()));
        rootView.findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });
        return rootView;

    }

    private class PayStackWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            handleUrlLoading(view, url);
            return false;
        }

        private void handleUrlLoading(WebView view, String url) {

            if (view == null || getActivity() == null) {
                return;
            }


            if (url.contains(ADD_CARD_SUCCESS_URL)) {

                final String message = getActivity().getString(R.string.paystack_add_card_success);
                if(stripeCardsStateListener!=null){
                    stripeCardsStateListener.onCardsUpdated(null,message,true, PaymentOption.PAY_STACK_CARD);
                }

            } else if (url.contains(ADD_CARD_FAILURE_URL)) {

                String message = getString(R.string.paystack_add_card_failure);

                if(stripeCardsStateListener!=null){
                    stripeCardsStateListener.onCardsUpdated(null,message,false, PaymentOption.PAY_STACK_CARD);
                }

            }


        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (view == null || getActivity().isFinishing()) {
                return;
            }

            if (url.contains(urlToLoad)) {
                DialogPopup.showLoadingDialog(getActivity(), getActivity().getString(R.string.loading));
            } else {
                handleUrlLoading(view, url);
            }
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            if (DialogPopup.isDialogShowing()) {
                DialogPopup.dismissLoadingDialog();

            }
        }


        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (DialogPopup.isDialogShowing()) {
                DialogPopup.dismissLoadingDialog();

            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            if (DialogPopup.isDialogShowing()) {
                DialogPopup.dismissLoadingDialog();

            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (DialogPopup.isDialogShowing()) {
                DialogPopup.dismissLoadingDialog();

            }
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            handleUrlLoading(view, request.getUrl().toString());
            return false;
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            Log.i(TAG, host);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            if (webView != null) {
                webView.destroy();
            }
        }

    }



    private static void removeAllCookies(final Context context) {
        //On Lollipop and beyond, the CookieManager singleton works fine by itself.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }





}
