package product.clicklabs.jugnoo.wallet;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.payu.sdk.Constants;
import com.payu.sdk.Params;

import org.json.JSONObject;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyBoardStateHandler;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class DemoFragment extends Fragment {

    LinearLayout relative;

    ImageView imageViewBack, rupeeLogoView;
    TextView textViewTitle;

	ProgressWheel progress;
	TextView dropTextView;

    TextView textViewHelp;//, rupee;
    EditText editTextAmount;
    Button button599, button999, buttonAddMoney, buttonMakePayment, buttonMakePaymentOTP, buttonWithdrawMoney;
    TextView textViewCurrentBalance, textViewCurrentBalanceValue;

    View rootView;
    PaymentActivity paymentActivity;

    ScrollView scrollView;
    TextView textViewScroll;
    LinearLayout linearLayoutMain;
    boolean scrolled = false;

    //public static AddPaymentPath addPaymentPath = AddPaymentPath.FROM_WALLET;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(getActivity(), Config.getFlurryKey());
        FlurryAgent.onStartSession(getActivity(), Config.getFlurryKey());
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(getActivity());
        //editTextAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

		startBalanceUpdater();
    }

	@Override
	public void onPause() {
		super.onPause();

		stopBalanceUpdater();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_spare, container, false);

		paymentActivity = (PaymentActivity) getActivity();




        relative = (LinearLayout) rootView.findViewById(R.id.relative);
        new ASSL(paymentActivity, relative, 1134, 720, false);

        setupUI(rootView.findViewById(R.id.relative));

        imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
        rupeeLogoView = (ImageView) rootView.findViewById(R.id.rupeeLogoView);
        textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(paymentActivity), Typeface.BOLD);

        textViewHelp = (TextView) rootView.findViewById(R.id.textViewHelp); textViewHelp.setTypeface(Fonts.latoLight(paymentActivity));

        editTextAmount = (EditText) rootView.findViewById(R.id.editTextAmount); editTextAmount.setTypeface(Fonts.latoRegular(paymentActivity));
//        editTextAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});


		progress = (ProgressWheel) rootView.findViewById(R.id.progress);
		dropTextView = (TextView) rootView.findViewById(R.id.dropTextView);

        scrolled = false;
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);
        linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);


        button599 = (Button) rootView.findViewById(R.id.button100); button599.setTypeface(Fonts.latoRegular(paymentActivity));
        buttonAddMoney = (Button) rootView.findViewById(R.id.buttonAddMoney); buttonAddMoney.setTypeface(Fonts.latoRegular(paymentActivity));
        buttonMakePaymentOTP = (Button) rootView.findViewById(R.id.buttonMakePaymentOTP); buttonMakePaymentOTP.setTypeface(Fonts.latoRegular(paymentActivity));
        buttonMakePayment = (Button) rootView.findViewById(R.id.buttonMakePayment); buttonMakePayment.setTypeface(Fonts.latoRegular(paymentActivity));
		buttonWithdrawMoney = (Button) rootView.findViewById(R.id.buttonWithdrawMoney); buttonWithdrawMoney.setTypeface(Fonts.latoRegular(paymentActivity));

        textViewCurrentBalance = (TextView) rootView.findViewById(R.id.textViewCurrentBalance); textViewCurrentBalance.setTypeface(Fonts.latoRegular(paymentActivity));
        textViewCurrentBalanceValue = (TextView) rootView.findViewById(R.id.textViewCurrentBalanceValue); textViewCurrentBalanceValue.setTypeface(Fonts.latoRegular(paymentActivity));

        imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


        buttonMakePayment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
				genrateOTP();
            }
        });

        buttonMakePaymentOTP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
				sendOTP(editTextAmount.getText().toString().trim());
            }
        });

		buttonWithdrawMoney.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				withdrawMoney(editTextAmount.getText().toString().trim());
			}
		});

        rupeeLogoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
				getBalance();
            }
        });

        buttonAddMoney.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addBalance(editTextAmount.getText().toString().trim());
			}
        });


        linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutMain, textViewScroll, new KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {
                if (!scrolled) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0, buttonMakePayment.getTop());
                        }
                    }, 100);
                    scrolled = true;
                }
            }

            @Override
            public void keyBoardClosed() {
                scrolled = false;
            }
        }));

		paymentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        return rootView;
    }


    /**
     * Method used to hide keyboard if outside touched.
     *
     * @param view
     */
    private void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    try {
                        if (paymentActivity.getCurrentFocus() != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) paymentActivity.getSystemService(paymentActivity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(paymentActivity.getCurrentFocus().getWindowToken(), 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }

            });
        }
        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }


    /**
     * Method used to remove fragment from stack
     */
    public void performBackPressed() {
        getActivity().getSupportFragmentManager().popBackStack ("WalletAddPaymentFragment", getFragmentManager().POP_BACK_STACK_INCLUSIVE);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
    }


    public void genrateOTP() {
		DialogPopup.showLoadingDialog(paymentActivity, "Generating OTP...");
        RequestParams params = new RequestParams();
        params.put("access_token", Data.userData.accessToken);
        params.put("client_id", Config.getClientId());
        params.put("is_access_token_new", "1");

        AsyncHttpClient client = Data.getClient();

        client.post(Config.getTXN_URL() + "paytm/login/request_otp", params, new CustomAsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {
				Log.i("request succesfull", "response = " + response);
				try {
					Toast.makeText(paymentActivity, "res = " + response, Toast.LENGTH_SHORT).show();

					JSONObject res = new JSONObject(response.toString());
//                    int flag = res.getInt("flag");
//
//
//                    if (!SplashScreen.checkIfTrivialAPIErrors(paymentActivity, res)) {
//                        Log.d("res", "result res = " + res.toString());
//
//
//                        Log.d("Data.storedataList", "Data.storedataList = " + Data.storedataList.size());
//                        LoadingBox.dismissLoadingDialog();
//
//                    }
				} catch (Exception e) {
					DialogPopup.dismissLoadingDialog();
					e.printStackTrace();
					DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
				}

				DialogPopup.dismissLoadingDialog();

			}

			@Override
			public void onFailure(Throwable arg0) {
				Log.e("request fail", arg0.toString());
				DialogPopup.dismissLoadingDialog();
				DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
			}
		});

    }

    private void sendOTP(String otp) {
		DialogPopup.showLoadingDialog(paymentActivity, "Sending OTP...");
        RequestParams params = new RequestParams();
        params.put("access_token", Data.userData.accessToken);
        params.put("client_id", Config.getClientId());
        params.put("is_access_token_new", "1");

        params.put("otp", "" + otp);

        AsyncHttpClient client = Data.getClient();

        client.post(Config.getTXN_URL() + "paytm/wallet/login_with_otp", params, new CustomAsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                Log.i("request succesfull", "response = " + response);
                try {
                    Toast.makeText(paymentActivity, "res = " + response, Toast.LENGTH_SHORT).show();

                    JSONObject res = new JSONObject(response.toString());
//                    int flag = res.getInt("flag");
//
//
//                    if (!SplashScreen.checkIfTrivialAPIErrors(paymentActivity, res)) {
//                        Log.d("res", "result res = " + res.toString());
//
//
//                        Log.d("Data.storedataList", "Data.storedataList = " + Data.storedataList.size());
//                        LoadingBox.dismissLoadingDialog();
//
//
//
//                    }
                } catch (Exception e) {
					DialogPopup.dismissLoadingDialog();
                    e.printStackTrace();
                    DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
                }

				DialogPopup.dismissLoadingDialog();

            }

            @Override
            public void onFailure(Throwable arg0) {
                Log.e("request fail", arg0.toString());
				DialogPopup.dismissLoadingDialog();
                DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
            }
        });
    }

    private void getBalance() {
		progress.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams();
        params.put("access_token", Data.userData.accessToken);
        params.put("client_id", Config.getClientId());
        params.put("is_access_token_new", "1");


        AsyncHttpClient client = Data.getClient();

        client.post(Config.getTXN_URL() + "paytm/wallet/check_balance", params, new CustomAsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                Log.i("request succesfull", "response = " + response);
                try {
                    JSONObject res = new JSONObject(response.toString());
					dropTextView.setText(res.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }

				progress.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Throwable arg0) {
                Log.e("request fail", arg0.toString());
				progress.setVisibility(View.GONE);
            }
        });
    }

    private void addBalance(String amount) {
		DialogPopup.showLoadingDialog(paymentActivity, "Adding Balance...");
        RequestParams params = new RequestParams();
        params.put("access_token", Data.userData.accessToken);
        params.put("client_id", Config.getClientId());
        params.put("is_access_token_new", "1");

        params.put("amount", ""+amount);
        AsyncHttpClient client = Data.getClient();

		String url = Config.getTXN_URL() + "paytm/wallet/add_money_request";

        client.post(Config.getTXN_URL() + "paytm/wallet/add_money_request", params, new CustomAsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                Log.i("request succesfull", "response = " + response);
                DialogPopup.dismissLoadingDialog();
                try {
                    Toast.makeText(paymentActivity, "res = "+response, Toast.LENGTH_SHORT).show();

                    JSONObject res1 = new JSONObject(response.toString());
                    JSONObject res = res1.getJSONObject("data");
//                    JSONObject res = new JSONObject("data");

                    Log.e("datavalue", "dataVal1 = "+res.toString());

                    String jData = res.toString();


                    Params requiredParams = new Params();
                    requiredParams.put("MID", ""+res.getString("MID"));
                    requiredParams.put("REQUEST_TYPE", ""+res.getString("REQUEST_TYPE"));
                    requiredParams.put("ORDER_ID", ""+res.getString("ORDER_ID"));
                    requiredParams.put("TXN_AMOUNT", ""+res.getString("TXN_AMOUNT"));
                    requiredParams.put("CHANNEL_ID", ""+res.getString("CHANNEL_ID"));
                    requiredParams.put("INDUSTRY_TYPE_ID", ""+res.getString("INDUSTRY_TYPE_ID"));
                    requiredParams.put("WEBSITE", ""+res.getString("WEBSITE"));
                    requiredParams.put("SSO_TOKEN", ""+res.getString("SSO_TOKEN"));
                    requiredParams.put("CHECKSUMHASH", ""+res.getString("CHECKSUMHASH"));
                    requiredParams.put("CUST_ID", ""+res.getString("CUST_ID"));


                    String postData = "";

                    for (String key : requiredParams.keySet()) {
//                        if(key.contentEquals(PayU.SURL) || key.contentEquals(PayU.FURL)){ // encode only ulrs
//                            postData += key + "=" + URLEncoder.encode(requiredParams.get(key), "UTF-8") + "&";
//                        }else{
                            postData += key + "=" + requiredParams.get(key) + "&";
//                        }
                    }


                    Log.e("datavalue", "dataVal1 = "+postData);

//
//
//                    String dataValue = requiredParams.toString();


//                    HashMap<String, Integer> companyDetails = new HashMap<String, Integer>();
//
//                    // create hashmap with keys and values (CompanyName, #Employees)
//                    companyDetails.put("eBay", 4444);
//                    companyDetails.put("Paypal", 5555);
//                    companyDetails.put("IBM", 6666);
//                    companyDetails.put("Google", 7777);
//                    companyDetails.put("Yahoo", 8888);

                    Log.e("datavalue", "dataVal1 = "+jData);

                    openWebView(postData);


                } catch (Exception e) {
					DialogPopup.dismissLoadingDialog();
                    e.printStackTrace();
                    DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
                }

				DialogPopup.dismissLoadingDialog();

            }

            @Override
            public void onFailure(Throwable arg0) {
                Log.e("request fail", arg0.toString());
				DialogPopup.dismissLoadingDialog();
                DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
            }
        });
    }

	private void withdrawMoney(String amount) {
		DialogPopup.showLoadingDialog(paymentActivity, "Withdrawing money...");
		RequestParams params = new RequestParams();
		params.put("access_token", Data.userData.accessToken);
		params.put("client_id", Config.getClientId());
		params.put("is_access_token_new", "1");

		params.put("amount", "" + amount);
		params.put("appIP", "" + Utils.getLocalIpAddress());

		AsyncHttpClient client = Data.getClient();

		client.post(Config.getTXN_URL() +"paytm/wallet/withdraw", params, new CustomAsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {
				Log.i("request succesfull", "response = " + response);
				DialogPopup.dismissLoadingDialog();
				try {
					Toast.makeText(paymentActivity, "res = "+response, Toast.LENGTH_SHORT).show();

					JSONObject res1 = new JSONObject(response.toString());
					JSONObject res = res1.getJSONObject("data");
//                    JSONObject res = new JSONObject("data");

					Log.e("datavalue", "dataVal1 = "+res.toString());

					String jData = res.toString();


					Params requiredParams = new Params();
					requiredParams.put("MID", ""+res.getString("MID"));
					requiredParams.put("REQUEST_TYPE", ""+res.getString("REQUEST_TYPE"));
					requiredParams.put("ORDER_ID", ""+res.getString("ORDER_ID"));
					requiredParams.put("TXN_AMOUNT", ""+res.getString("TXN_AMOUNT"));
					requiredParams.put("CHANNEL_ID", ""+res.getString("CHANNEL_ID"));
					requiredParams.put("INDUSTRY_TYPE_ID", ""+res.getString("INDUSTRY_TYPE_ID"));
					requiredParams.put("WEBSITE", ""+res.getString("WEBSITE"));
					requiredParams.put("SSO_TOKEN", ""+res.getString("SSO_TOKEN"));
					requiredParams.put("CHECKSUMHASH", ""+res.getString("CHECKSUMHASH"));
					requiredParams.put("CUST_ID", ""+res.getString("CUST_ID"));


					String postData = "";

					for (String key : requiredParams.keySet()) {
//                        if(key.contentEquals(PayU.SURL) || key.contentEquals(PayU.FURL)){ // encode only ulrs
//                            postData += key + "=" + URLEncoder.encode(requiredParams.get(key), "UTF-8") + "&";
//                        }else{
						postData += key + "=" + requiredParams.get(key) + "&";
//                        }
					}


					Log.e("datavalue", "dataVal1 = "+postData);

//
//
//                    String dataValue = requiredParams.toString();


//                    HashMap<String, Integer> companyDetails = new HashMap<String, Integer>();
//
//                    // create hashmap with keys and values (CompanyName, #Employees)
//                    companyDetails.put("eBay", 4444);
//                    companyDetails.put("Paypal", 5555);
//                    companyDetails.put("IBM", 6666);
//                    companyDetails.put("Google", 7777);
//                    companyDetails.put("Yahoo", 8888);

					Log.e("datavalue", "dataVal1 = "+jData);

					openWebView(postData);


				} catch (Exception e) {
					DialogPopup.dismissLoadingDialog();
					e.printStackTrace();
					DialogPopup.alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
				}

				DialogPopup.dismissLoadingDialog();

			}

			@Override
			public void onFailure(Throwable arg0) {
				Log.e("request fail", arg0.toString());
				DialogPopup.dismissLoadingDialog();
				new DialogPopup().alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
			}
		});
	}

    private void openWebView(String jsonData) {
//        String url = Config.getTXN_URL() + "paytm/wallet/add_money?JsonData="+jsonData+"&access_token="+ Data.userData.accessToken +"&client_id="+ Data.CLIENT_ID+"&is_access_token_new=1";
//        String postData = "<iframe src="+url+" ></iframe>";
//
//
//        Params requiredParams = new Params();
//        requiredParams.put("MID", "Socomo56207867201243");
//        requiredParams.put("REQUEST_TYPE", "ADD_MONEY");
//        requiredParams.put("ORDER_ID", "73911552");
//        requiredParams.put("TXN_AMOUNT", "dhgajs");
//        requiredParams.put("CHANNEL_ID", "dhgajs");
//        requiredParams.put("INDUSTRY_TYPE_ID", "dhgajs");
//        requiredParams.put("WEBSITE", "dhgajs");
//        requiredParams.put("SSO_TOKEN", "dhgajs");
//        requiredParams.put("CHECKSUMHASH", "dhgajs");


//        String postData = jsonData.replaceAll("\"", "\\\\\"");

//        Log.e("datavalue", "dataVal1 = "+postData);

        String jData = "JsonData=\""+jsonData.toString()+"\"";

        Log.e("jData", "jData = "+jData);


        Intent intent = new Intent(paymentActivity, PaymentWebViewActivity.class);
        intent.putExtra(Constants.POST_DATA, jsonData);


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


	private int balanceUpdaterRunning = 0;
	private Handler handlerBalanceUpdater = new Handler();
	private Runnable runnableBalanceUpdater = new Runnable() {
		@Override
		public void run() {
			try {
				if(balanceUpdaterRunning == 1){
					getBalance();
					handlerBalanceUpdater.postDelayed(runnableBalanceUpdater, 20000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void startBalanceUpdater(){
		try {
			balanceUpdaterRunning = 1;
			handlerBalanceUpdater.postDelayed(runnableBalanceUpdater, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stopBalanceUpdater(){
		try {
			balanceUpdaterRunning = 0;
			handlerBalanceUpdater.removeCallbacks(runnableBalanceUpdater);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}






}
