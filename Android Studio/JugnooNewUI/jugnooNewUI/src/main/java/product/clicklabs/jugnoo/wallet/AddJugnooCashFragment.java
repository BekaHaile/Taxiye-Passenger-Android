package product.clicklabs.jugnoo.wallet;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.payu.sdk.Constants;
import com.payu.sdk.Params;
import com.payu.sdk.PayU;
import com.payu.sdk.Payment;
import com.payu.sdk.ProcessPaymentActivity;
import com.payu.sdk.exceptions.HashException;
import com.payu.sdk.exceptions.MissingParameterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Calendar;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.cardformat.CreditCardEditText;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;


/**
 * Created by clicklabs on 6/30/15.
 */
public class AddJugnooCashFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Payment payment;
    Payment.Builder builder;
    String bankCode = "";
    String enterAmount = "1";
    DecimalFormat df = new DecimalFormat("####0.00");
    Button buttonPayNow;


    Handler mHandler;

    LinearLayout relative;

    RelativeLayout relativeLayoutTopBar, layoutBackButton,
            relativeLayoutDebitCard, relativeLayoutCreditCard, relativeLayoutNetBanking;

    LinearLayout linearLayoutDebitCardDetails, linearLayoutCreditCardDetails,
            linearLayoutNetbankingDetails;

    ImageView imageViewSelectDebitCard, imageViewSelectCreditCard, imageViewSelectNetBanking;

    TextView textViewTitle, textViewSelectPaymentMode, textViewDebitCard, textViewCreditCard,
            textViewNetBanking, textViewAddAmount, textViewAddAmountValue, textViewJugnooCashNote,
            textViewTermsAndConditions;

    RelativeLayout relativeLayoutSaveDebitCard, relativeLayoutSaveCreditCard;

    CreditCardEditText editTextDebitCardNumber, editTextCreditCardNumber;

    EditText editTextMonthDebitCard, editTextYearDebitCard, editTextCVVDebitCard, editTextNameOnDebitCard;
    EditText editTextMonthCreditCard, editTextYearCreditCard, editTextCVVCreditCard, editTextNameOnCreditCard;

    int textLength;
    int optionSelect = 0;// 0= DC, 1 = CC, 2 = NB

    View rootView;
    public PaymentActivity paymentActivity;
    //DecimalFormat df = new DecimalFormat("#");


    String key, tnxId, amount, productInfo, firstName, email, salt, phoneNo;
    int year = 2015, month = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_jugnoo_cash, container, false);

        paymentActivity = (PaymentActivity) getActivity();

        builder = new Payment().new Builder();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(paymentActivity.enterAmount);

        enterAmount = stringBuilder.toString();
        Log.e("enterAmount", "enterAmount = " + enterAmount);

        mHandler = new Handler();
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;

        initComponents();
        new ASSL(paymentActivity, relative, 1134, 720, false);

        setupUI(rootView.findViewById(R.id.relative));

        if (AppStatus.getInstance(paymentActivity).isOnline(paymentActivity)) {
            getTxtID();
        } else {
            new DialogPopup().alertPopup(paymentActivity, "", "" + getResources().getString(R.string.check_internet_connection));
        }

        return rootView;
    }


    public void popFrag() {

                try {
                    getActivity().getSupportFragmentManager().popBackStack();
                }catch(Exception e) {
                    e.printStackTrace();
                }

    }

    private void initComponents() {
        relative = (LinearLayout) rootView.findViewById(R.id.relative);
        relativeLayoutTopBar = (RelativeLayout) rootView.findViewById(R.id.topBar);
        layoutBackButton = (RelativeLayout) rootView.findViewById(R.id.layoutBackButton);

        linearLayoutDebitCardDetails = (LinearLayout) rootView.findViewById(R.id.linearLayoutDebitCardDetails);
        linearLayoutCreditCardDetails = (LinearLayout) rootView.findViewById(R.id.linearLayoutCreditCardDetails);
        linearLayoutNetbankingDetails = (LinearLayout) rootView.findViewById(R.id.linearLayoutNetbankingDetails);

        relativeLayoutDebitCard = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDebitCard);
        relativeLayoutCreditCard = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCreditCard);
        relativeLayoutNetBanking = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNetBanking);

        relativeLayoutDebitCard.setOnClickListener(this);
        relativeLayoutCreditCard.setOnClickListener(this);
        relativeLayoutNetBanking.setOnClickListener(this);

        imageViewSelectDebitCard = (ImageView) rootView.findViewById(R.id.imageViewSelectDebitCard);
        imageViewSelectCreditCard = (ImageView) rootView.findViewById(R.id.imageViewSelectCreditCard);
        imageViewSelectNetBanking = (ImageView) rootView.findViewById(R.id.imageViewSelectNetBanking);

        textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.latoRegular(paymentActivity), Typeface.BOLD);
        textViewSelectPaymentMode = (TextView) rootView.findViewById(R.id.textViewSelectPaymentMode);
        textViewSelectPaymentMode.setTypeface(Fonts.latoLight(paymentActivity));
        textViewDebitCard = (TextView) rootView.findViewById(R.id.textViewDebitCard);
        textViewDebitCard.setTypeface(Fonts.latoRegular(paymentActivity));
        textViewCreditCard = (TextView) rootView.findViewById(R.id.textViewCreditCard);
        textViewCreditCard.setTypeface(Fonts.latoRegular(paymentActivity));
        textViewNetBanking = (TextView) rootView.findViewById(R.id.textViewNetBanking);
        textViewNetBanking.setTypeface(Fonts.latoRegular(paymentActivity));
        textViewAddAmount = (TextView) rootView.findViewById(R.id.textViewAddAmount);
        textViewAddAmount.setTypeface(Fonts.latoRegular(paymentActivity));
        textViewAddAmountValue = (TextView) rootView.findViewById(R.id.textViewAddAmountValue);
        textViewAddAmountValue.setTypeface(Fonts.latoLight(paymentActivity));
        textViewJugnooCashNote = (TextView) rootView.findViewById(R.id.textViewJugnooCashNote);
        textViewJugnooCashNote.setTypeface(Fonts.latoLight(paymentActivity));
        textViewTermsAndConditions = (TextView) rootView.findViewById(R.id.textViewTermsAndConditions);
        textViewTermsAndConditions.setTypeface(Fonts.latoLight(paymentActivity));

        editTextDebitCardNumber = (CreditCardEditText) rootView.findViewById(R.id.editTextDebitCardNumber);
        editTextDebitCardNumber.setTypeface(Fonts.latoLight(paymentActivity));
        editTextMonthDebitCard = (EditText) rootView.findViewById(R.id.editTextMonthDebitCard);
        editTextMonthDebitCard.setTypeface(Fonts.latoLight(paymentActivity));
        editTextYearDebitCard = (EditText) rootView.findViewById(R.id.editTextYearDebitCard);
        editTextYearDebitCard.setTypeface(Fonts.latoLight(paymentActivity));
        editTextCVVDebitCard = (EditText) rootView.findViewById(R.id.editTextCVVDebitCard);
        editTextCVVDebitCard.setTypeface(Fonts.latoLight(paymentActivity));
        editTextNameOnDebitCard = (EditText) rootView.findViewById(R.id.editTextNameOnDebitCard);
        editTextNameOnDebitCard.setTypeface(Fonts.latoLight(paymentActivity));

        relativeLayoutSaveDebitCard = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutSaveDebitCard);

        editTextCreditCardNumber = (CreditCardEditText) rootView.findViewById(R.id.editTextCreditCardNumber);
        editTextCreditCardNumber.setTypeface(Fonts.latoLight(paymentActivity));
        editTextMonthCreditCard = (EditText) rootView.findViewById(R.id.editTextMonthCreditCard);
        editTextMonthCreditCard.setTypeface(Fonts.latoLight(paymentActivity));
        editTextYearCreditCard = (EditText) rootView.findViewById(R.id.editTextYearCreditCard);
        editTextYearCreditCard.setTypeface(Fonts.latoLight(paymentActivity));
        editTextCVVCreditCard = (EditText) rootView.findViewById(R.id.editTextCVVCreditCard);
        editTextCVVCreditCard.setTypeface(Fonts.latoLight(paymentActivity));
        editTextNameOnCreditCard = (EditText) rootView.findViewById(R.id.editTextNameOnCreditCard);
        editTextNameOnCreditCard.setTypeface(Fonts.latoLight(paymentActivity));

        relativeLayoutSaveCreditCard = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutSaveCreditCard);

        buttonPayNow = (Button) rootView.findViewById(R.id.nbPayButton);
        buttonPayNow.setTypeface(Fonts.latoRegular(paymentActivity));

        layoutBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popFrag();
            }
        });

        try {
            textViewAddAmountValue.setText(getResources().getString(R.string.rupee) + " " + enterAmount);
        }catch(Exception e) {
            textViewAddAmountValue.setText(getResources().getString(R.string.rupee) + " " + enterAmount);
        }
        editTextMonthDebitCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextMonthDebitCard.getText().length() > 0)
                    if (Integer.parseInt(editTextMonthDebitCard.getText().toString().trim()) > 12) {
                        DialogPopup.dialogBanner(paymentActivity, ""+getResources().getString(R.string.valid_month));
                    }
            }
        });

        editTextMonthCreditCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextMonthCreditCard.getText().length() > 0)
                    if (Integer.parseInt(editTextMonthCreditCard.getText().toString().trim()) > 12) {
                        DialogPopup.dialogBanner(paymentActivity, ""+getResources().getString(R.string.valid_month));
                    }
            }
        });

        editTextYearDebitCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextYearDebitCard.getText().length() == 4) {
                    if (Integer.parseInt(editTextYearDebitCard.getText().toString()) < year) {
                        DialogPopup.dialogBanner(paymentActivity, ""+getResources().getString(R.string.valid_year));
                    }
                }
            }
        });

        editTextYearCreditCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextYearCreditCard.getText().length() == 4) {
                    if (Integer.parseInt(editTextYearCreditCard.getText().toString()) < year) {
                        DialogPopup.dialogBanner(paymentActivity, ""+getResources().getString(R.string.valid_year));
                    }
                }
            }
        });

        buttonPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(paymentActivity.pgName.equalsIgnoreCase("NB")) {
                    paymentActivity.bankCode = bankCode;
                    paymentActivity.ccNum = "";
                    paymentActivity.ccName = "";
                    paymentActivity.ccvv = "";
                    paymentActivity.ccexpmon = "";
                    paymentActivity.ccexpyr = "";
                    makeNetbackingPayment();
                } else {
                    paymentActivity.bankCode = "CC";
                    if(optionSelect == 0) {
                        // DC
                        paymentActivity.ccNum = editTextDebitCardNumber.getText().toString().trim();
                        paymentActivity.ccNum = paymentActivity.ccNum.replace("-", "");
                        paymentActivity.ccName = editTextNameOnDebitCard.getText().toString().trim();
                        paymentActivity.ccvv = editTextCVVDebitCard.getText().toString().trim();
                        paymentActivity.ccexpmon = editTextMonthDebitCard.getText().toString().trim();
                        paymentActivity.ccexpyr = editTextYearDebitCard.getText().toString().trim();
                    } else if(optionSelect == 1) {
                        // CC
                        paymentActivity.ccNum = editTextCreditCardNumber.getText().toString().trim();
                        paymentActivity.ccNum = paymentActivity.ccNum.replace("-", "");
                        paymentActivity.ccName = editTextNameOnCreditCard.getText().toString().trim();
                        paymentActivity.ccvv = editTextCVVCreditCard.getText().toString().trim();
                        paymentActivity.ccexpmon = editTextMonthCreditCard.getText().toString().trim();
                        paymentActivity.ccexpyr = editTextYearCreditCard.getText().toString().trim();
                    }



                    if(paymentActivity.ccNum.length()>10 && paymentActivity.ccName.length()>1 && paymentActivity.ccvv.length()>2 && paymentActivity.ccexpmon.length()>1 && paymentActivity.ccexpyr.length()>3) {
                        if(Integer.parseInt(paymentActivity.ccexpyr) < year || Integer.parseInt(paymentActivity.ccexpyr) >= year+50) {
                            new DialogPopup().dialogBanner(paymentActivity, ""+getResources().getString(R.string.invalid_year));
                        } else if(Integer.parseInt(paymentActivity.ccexpmon) < month && Integer.parseInt(paymentActivity.ccexpyr) == year) {
                            new DialogPopup().dialogBanner(paymentActivity, ""+getResources().getString(R.string.invalid_card_info));
                        } else {
                            makePayment();
                        }
                    } else if(paymentActivity.ccNum.length()==0 || paymentActivity.ccName.length()==0 || paymentActivity.ccvv.length()==0 || paymentActivity.ccexpmon.length()==0 || paymentActivity.ccexpyr.length()==0) {
                        new DialogPopup().dialogBanner(paymentActivity, ""+getResources().getString(R.string.no_field_empty));
                    } else {
                         if(paymentActivity.ccexpmon.length()<2) {
                            new DialogPopup().dialogBanner(paymentActivity, ""+getResources().getString(R.string.invalid_month));
                        } else if(paymentActivity.ccexpyr.length()<4) {
                            new DialogPopup().dialogBanner(paymentActivity, ""+getResources().getString(R.string.invalid_year));
                        } else if(paymentActivity.ccvv.length()<3) {
                            new DialogPopup().dialogBanner(paymentActivity, ""+getResources().getString(R.string.invalid_cvv));
                        } else if(paymentActivity.ccName.length()<3) {
                            new DialogPopup().dialogBanner(paymentActivity, ""+getResources().getString(R.string.invalid_name));
                        } else {
                            new DialogPopup().dialogBanner(paymentActivity, ""+getResources().getString(R.string.invalid_info));
                        }
                    }
                }


//                paymentActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
//                    .add(R.id.fragLayout, new WalletWebviewFragment(), "WalletWebviewFragment").addToBackStack("WalletWebviewFragment")
//                    .hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
//                        .getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
//                    .commit();

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativeLayoutDebitCard:
                paymentActivity.pgName = "DC";
                optionSelect = 0;
                rootView.findViewById(com.payu.sdk.R.id.nbPayButton).setEnabled(true);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (linearLayoutDebitCardDetails.getVisibility() == View.VISIBLE) {
                            linearLayoutDebitCardDetails.setVisibility(View.GONE);
                            imageViewSelectDebitCard.setBackgroundResource(R.drawable.ic_payment_mode_unpressed);
                        } else {
                            linearLayoutDebitCardDetails.setVisibility(View.VISIBLE);
                            imageViewSelectDebitCard.setBackgroundResource(R.drawable.ic_payment_mode_pressed);
                        }

                    }
                }, 200);

                // if credit card layout is visible, set invisible
                if (linearLayoutCreditCardDetails.getVisibility() == View.VISIBLE) {
                    linearLayoutCreditCardDetails.setVisibility(View.GONE);
                    imageViewSelectCreditCard.setBackgroundResource(R.drawable.ic_payment_mode_unpressed);
                }

                // if net banking layout is visible, set it invisible
                if (linearLayoutNetbankingDetails.getVisibility() == View.VISIBLE) {
                    linearLayoutNetbankingDetails.setVisibility(View.GONE);
                    imageViewSelectNetBanking.setBackgroundResource(R.drawable.ic_payment_mode_unpressed);
                }


                break;
            case R.id.relativeLayoutCreditCard:
                paymentActivity.pgName = "CC";
                rootView.findViewById(com.payu.sdk.R.id.nbPayButton).setEnabled(true);
                optionSelect = 1;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (linearLayoutCreditCardDetails.getVisibility() == View.VISIBLE) {
                            linearLayoutCreditCardDetails.setVisibility(View.GONE);
                            imageViewSelectCreditCard.setBackgroundResource(R.drawable.ic_payment_mode_unpressed);
                        } else {
                            linearLayoutCreditCardDetails.setVisibility(View.VISIBLE);
                            imageViewSelectCreditCard.setBackgroundResource(R.drawable.ic_payment_mode_pressed);
                        }
                    }
                }, 200);

                // if debit card layout is visible, set it invisible
                if (linearLayoutDebitCardDetails.getVisibility() == View.VISIBLE) {
                    linearLayoutDebitCardDetails.setVisibility(View.GONE);
                    imageViewSelectDebitCard.setBackgroundResource(R.drawable.ic_payment_mode_unpressed);
                }

                // if net banking layout is visible, set it invisible
                if (linearLayoutNetbankingDetails.getVisibility() == View.VISIBLE) {
                    linearLayoutNetbankingDetails.setVisibility(View.GONE);
                    imageViewSelectNetBanking.setBackgroundResource(R.drawable.ic_payment_mode_unpressed);
                }

                break;


                    case R.id.relativeLayoutNetBanking:
                        paymentActivity.pgName = "NB";
                        optionSelect = 2;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (linearLayoutNetbankingDetails.getVisibility() == View.VISIBLE) {
                                    linearLayoutNetbankingDetails.setVisibility(View.GONE);
                                    imageViewSelectNetBanking.setBackgroundResource(R.drawable.ic_payment_mode_unpressed);
                                } else {
                                    linearLayoutNetbankingDetails.setVisibility(View.VISIBLE);
                                    imageViewSelectNetBanking.setBackgroundResource(R.drawable.ic_payment_mode_pressed);
                                }
                            }
                        }, 200);

                        // if debit card layout is visible, set it invisible
                        if (linearLayoutDebitCardDetails.getVisibility() == View.VISIBLE) {
                            linearLayoutDebitCardDetails.setVisibility(View.GONE);
                            imageViewSelectDebitCard.setBackgroundResource(R.drawable.ic_payment_mode_unpressed);
                        }

                        // if credit card layout is visible, set invisible
                        if (linearLayoutCreditCardDetails.getVisibility() == View.VISIBLE) {
                            linearLayoutCreditCardDetails.setVisibility(View.GONE);
                            imageViewSelectCreditCard.setBackgroundResource(R.drawable.ic_payment_mode_unpressed);
                        }

                        break;

        }
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
                        InputMethodManager inputMethodManager = (InputMethodManager) paymentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(paymentActivity.getCurrentFocus().getWindowToken(), 0);
                    }catch(Exception e) {

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
     * Method used to set bank value into list
     */
    public void setupAdapter() {

        com.payu.sdk.adapters.NetBankingAdapter adapter = new com.payu.sdk.adapters.NetBankingAdapter(paymentActivity, PayU.availableBanks);

        Spinner netBankingSpinner = (Spinner) rootView.findViewById(com.payu.sdk.R.id.netBankingSpinner);
        netBankingSpinner.setAdapter(adapter);

        netBankingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.rgb(69, 79, 79));
                    float size = ASSL.Xscale() * 31;
                    ((TextView) adapterView.getChildAt(0)).setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

                    ((TextView) adapterView.getChildAt(0)).setSingleLine(true);
                    bankCode = ((JSONObject) adapterView.getAdapter().getItem(i)).getString("code");

                    if (bankCode.contentEquals("default")) {
                        rootView.findViewById(com.payu.sdk.R.id.nbPayButton).setEnabled(false);
                    } else {
                        if (PayU.netBankingStatus != null && PayU.netBankingStatus.get(bankCode) == 0) {
                            ((TextView) rootView.findViewById(com.payu.sdk.R.id.netBankingErrorText)).setText("Oops! " + ((JSONObject) adapterView.getAdapter().getItem(i)).getString("title") + " seems to be down. We recommend you pay using any other means of payment.");
                            rootView.findViewById(com.payu.sdk.R.id.netBankingErrorText).setVisibility(View.VISIBLE);
                        } else {
                            rootView.findViewById(com.payu.sdk.R.id.netBankingErrorText).setVisibility(View.GONE);
                        }
                        rootView.findViewById(com.payu.sdk.R.id.nbPayButton).setEnabled(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


    /**
     * Get HASH and other information from server for payment.
     */
    public void getTxtID() {
        if (AppStatus.getInstance(getActivity()).isOnline(getActivity())) {

            DialogPopup.showLoadingDialog(paymentActivity, "Loading...");
            RequestParams params = new RequestParams();

            params.put("client_id", Config.getClientId());
            params.put("access_token", Data.userData.accessToken);
            params.put("is_access_token_new", "1");
            params.put("amount", ""+enterAmount);

            AsyncHttpClient client = Data.getClient();
            client.post(Config.getTXN_URL(), params,
                new CustomAsyncHttpResponseHandler() {
                    private JSONObject jObj;
                    @Override
                    public void onSuccess(String response) {
                        android.util.Log.e("Server response", "response = " + response);
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            Log.e("res", "res = " + res.toString());
                            int flag = res.getInt("flag");
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(paymentActivity, res)) {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

                                    paymentActivity.txnId = "";
                                    paymentActivity.uName = "";
                                    paymentActivity.uEmail = "";
                                    paymentActivity.hash = "";
                                    paymentActivity.hashSdk = "";
                                    paymentActivity.uProdInfo = "";

                                    paymentActivity.txnId = res.getString("txn_id");
                                    paymentActivity.uName = res.getString("first_name");
                                    paymentActivity.uEmail = res.getString("email");
                                    paymentActivity.hash = res.getString("hash_txn");
                                    paymentActivity.hashSdk = res.getString("hash_sdk");
                                    paymentActivity.uProdInfo = res.getString("product_info");

                                    PayU.paymentHash = paymentActivity.hash;
                                    PayU.ibiboCodeHash = paymentActivity.hashSdk;

                                    getBankList();

                                } else if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                    String error = res.getString("error");
                                    DialogPopup.alertPopup(paymentActivity, "", error);
                                } else {
                                    new DialogPopup().alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
                                }

                            }
                            DialogPopup.dismissLoadingDialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                            DialogPopup.dismissLoadingDialog();
                            new DialogPopup().alertPopup(paymentActivity, "", Data.SERVER_ERROR_MSG);
                        }

                    }

                    @Override
                    public void onFailure(Throwable arg0) {
                        Log.e("request fail", arg0.toString());
                        DialogPopup.dismissLoadingDialog();
                    }
                });
        }
        else{
            new DialogPopup().alertPopup(paymentActivity, "", Data.CHECK_INTERNET_MSG);
        }
    }


    /**
     * Make payment for CC/DC
     */
    public void makePayment() {
        Params requiredParams = new Params();
        builder.set(PayU.MODE, paymentActivity.pgName);
        builder.set(PayU.PRODUCT_INFO, ""+paymentActivity.uProdInfo);
        builder.set(PayU.AMOUNT, enterAmount);


        try {
            paymentActivity.uEmail = URLEncoder.encode(paymentActivity.uEmail, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        builder.set(PayU.EMAIL, paymentActivity.uEmail);
        builder.set(PayU.FIRSTNAME, paymentActivity.uName);

        builder.set(PayU.TXNID, paymentActivity.txnId);
        builder.set(PayU.SURL, Config.getSURL());
        builder.set(PayU.FURL, Config.getFURL());


        String nameOnCard = paymentActivity.ccName;
        if (nameOnCard.length() < 3) {
            nameOnCard = "PayU " + nameOnCard;
        }

        requiredParams.put(PayU.CARD_NUMBER, paymentActivity.ccNum);
        requiredParams.put(PayU.EXPIRY_MONTH, paymentActivity.ccexpmon);
        requiredParams.put(PayU.EXPIRY_YEAR, paymentActivity.ccexpyr);
        requiredParams.put(PayU.CARDHOLDER_NAME, nameOnCard);
        requiredParams.put(PayU.CVV, paymentActivity.ccvv);

        requiredParams.put(PayU.EMAIL, paymentActivity.uEmail);
        requiredParams.put(PayU.FIRSTNAME, paymentActivity.uName);

        requiredParams.put(PayU.BANKCODE, paymentActivity.bankCode);
        requiredParams.put(PayU.AMOUNT, enterAmount);
        requiredParams.put(PayU.PRODUCT_INFO, ""+paymentActivity.uProdInfo);
        requiredParams.put(PayU.TXNID, paymentActivity.txnId);
        requiredParams.put(PayU.SURL, Config.getSURL());
        requiredParams.put(PayU.FURL, Config.getFURL());



        try {
            Bundle bundle = paymentActivity.getPackageManager().getApplicationInfo(paymentActivity.getPackageName(), PackageManager.GET_META_DATA).metaData;

            requiredParams.put(PayU.MERCHANT_KEY, bundle.getString("payu_merchant_id"));//kayValue
            payment = builder.create();

            String postData = PayU.getInstance(paymentActivity).createPayment(payment, requiredParams);
            Log.e("postData", "postData = " + postData);

            Intent intent = new Intent(paymentActivity, ProcessPaymentActivity.class);
            intent.putExtra(Constants.POST_DATA, postData);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            popFrag();
            startActivity(intent);
//            startActivityForResult(intent, PayU.RESULT);
        } catch (MissingParameterException e) {
            e.printStackTrace();
        } catch (HashException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void makeNetbackingPayment() {


            Params requiredParams = new Params();

            builder.set(PayU.MODE, paymentActivity.pgName);
            builder.set(PayU.PRODUCT_INFO, ""+paymentActivity.uProdInfo);
            builder.set(PayU.AMOUNT, enterAmount);//paymentActivity.enterAmount


            try {
                paymentActivity.uEmail = URLEncoder.encode(paymentActivity.uEmail, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            builder.set(PayU.EMAIL, paymentActivity.uEmail);
            builder.set(PayU.FIRSTNAME, paymentActivity.uName);

            builder.set(PayU.TXNID, paymentActivity.txnId);
            builder.set(PayU.SURL, Config.getSURL());
            builder.set(PayU.FURL, Config.getFURL());


            String nameOnCard = paymentActivity.ccName;
            if (nameOnCard.length() < 3) {
                nameOnCard = "PayU " + nameOnCard;
            }


            requiredParams.put(PayU.EMAIL, paymentActivity.uEmail);
            requiredParams.put(PayU.FIRSTNAME, paymentActivity.uName);

            requiredParams.put(PayU.BANKCODE, paymentActivity.bankCode);
            requiredParams.put(PayU.AMOUNT, enterAmount);
            requiredParams.put(PayU.PRODUCT_INFO, ""+paymentActivity.uProdInfo);
            requiredParams.put(PayU.TXNID, paymentActivity.txnId);
            requiredParams.put(PayU.SURL, Config.getSURL());
            requiredParams.put(PayU.FURL, Config.getFURL());



            try {
                Bundle bundle = paymentActivity.getPackageManager().getApplicationInfo(paymentActivity.getPackageName(), PackageManager.GET_META_DATA).metaData;

                requiredParams.put(PayU.MERCHANT_KEY, bundle.getString("payu_merchant_id"));//kayValue
                payment = builder.create();

                String postData = PayU.getInstance(paymentActivity).createPayment(payment, requiredParams);
                Log.e("postData", "postData = " + postData);

                Intent intent = new Intent(paymentActivity, ProcessPaymentActivity.class);
                intent.putExtra(com.payu.sdk.Constants.POST_DATA, postData);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                popFrag();
                startActivity(intent);
//                startActivityForResult(intent, PayU.RESULT);
        } catch (MissingParameterException e) {
            e.printStackTrace();
        } catch (HashException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getBankList() {
//        if(com.payu.sdk.Constants.SDK_HASH_GENERATION) {
//            String command = com.payu.sdk.Constants.PAYMENT_RELATED_DETAILS;
//            String defaultval = "default";
//            Bundle bundle = null;
//            try {
//                bundle = paymentActivity.getPackageManager().getApplicationInfo(paymentActivity.getPackageName(), PackageManager.GET_META_DATA).metaData;
//                key = bundle.getString("payu_merchant_id");
//                salt = bundle.getString("payu_merchant_salt");
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//
//            }
//
//            String hashValue = key + "|" + command + "|" + defaultval + "|" + salt;
//
//            try {
//                PayU.ibiboCodeHash = sha512(hashValue);
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            Log.e("PayU.paymentHash", "PayU.ibiboCodeHash = " + PayU.ibiboCodeHash);
//        }

        paymentActivity.getListBanks();


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        ((TextView) parent.getChildAt(0)).setTextColor(0x000000FF);
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
