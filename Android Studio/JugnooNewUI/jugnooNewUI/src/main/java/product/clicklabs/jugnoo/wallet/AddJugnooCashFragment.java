package product.clicklabs.jugnoo.wallet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.payu.sdk.Cards;
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
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.SplashNewActivity;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyBoardStateHandler;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;


/**
 * Created by clicklabs on 6/30/15.
 */
public class AddJugnooCashFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, FlurryEventNames {


    public boolean saveDCCard = true, saveCCCard = true;
    public int deletedCardPostion = 0;
    Payment payment;
    Payment.Builder builder;
    String bankCode = "";
    String enterAmount = "1";
    DecimalFormat df = new DecimalFormat("####0.00");
    Button buttonPayNow;

    public boolean selectedFlag = false;

    Handler mHandler;

    LinearLayout relative;

    RelativeLayout relativeLayoutTopBar, layoutBackButton, relativeLayoutDebitCard, relativeLayoutCreditCard, relativeLayoutNetBanking;

    LinearLayout linearLayoutDebitCardDetails, linearLayoutCreditCardDetails, linearLayoutNetbankingDetails, listLayout;

    ImageView imageViewSelectDebitCard, imageViewSelectCreditCard, imageViewSelectNetBanking, imageViewSaveDebitCard, imageViewSaveCreditCard;

    TextView textViewTitle, textViewSelectPaymentMode, textViewDebitCard, textViewCreditCard,
            textViewNetBanking, textViewAddAmount, textViewAddAmountValue, textViewJugnooCashNote, textViewTermsAndConditions;

    TextView textViewDebitCardDetails, textViewCreditCardDetails;
    TextView validTextDebitCard, validTextCreditCard;
    RelativeLayout relativeLayoutSaveDebitCard, relativeLayoutSaveCreditCard;

    EditText editTextDebitCardNumber, editTextCreditCardNumber;

    EditText  editTextCVVDebitCard, editTextNameOnDebitCard;
    EditText editTextCVVCreditCard, editTextNameOnCreditCard;

    public static Button editTextMonthDebitCard, editTextYearDebitCard;
    public static Button editTextMonthCreditCard, editTextYearCreditCard;


    int textLength;
    public static int optionSelect = 0;// 0= DC, 1 = CC, 2 = NB

    View rootView;
    public PaymentActivity homeActivity;

    ScrollView scrollView;
    TextView textViewScroll;
    LinearLayout linearLayoutMain;
    boolean scrolled = false;
    //DecimalFormat df = new DecimalFormat("#");


    String key, tnxId, amount, productInfo, firstName, email, salt, phoneNo;
    int year = 2015, month = 1;
    StoreCardAdapter storeCardAdp;
    ListView saveCardList;
    ArrayList<StoreCard> storeListCards = new ArrayList<StoreCard>();
    boolean[] flag = new boolean[5];
    String[] editTextValue = new String[5];

    String cvvValue = "";
    int positionValue = 0;

    Boolean isCardNumberValid = false;
    Boolean isExpired = true;
    Boolean isCvvValid = false;
    Drawable cardNumberDrawable;
    Drawable issuerDrawable;
    private String cardNumber = "";

    private String issuer = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_jugnoo_cash, container, false);

        homeActivity = (PaymentActivity) getActivity();

        builder = new Payment().new Builder();

        optionSelect = 0;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(homeActivity.enterAmount);

        enterAmount = stringBuilder.toString();
        Log.e("enterAmount", "enterAmount = " + enterAmount);

        mHandler = new Handler();
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;

        scrolled = false;
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);
        linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);

        initComponents();
        new ASSL(homeActivity, relative, 1134, 720, false);

//        setupUI(rootView.findViewById(R.id.relative));

        if (AppStatus.getInstance(homeActivity).isOnline(homeActivity)) {
            getTxtID();
        } else {
            new DialogPopup().alertPopup(homeActivity, "", "" + getResources().getString(R.string.check_internet_connection));
        }

        linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutMain, textViewScroll, new KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {
                if (!scrolled) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            saveCardList.setSelection(saveCardList.getCount() - 1);
//                            saveCardList.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
//                            ScrollViewer.CanContentScrol

//                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            if(editTextCVVDebitCard.hasFocus() || editTextNameOnDebitCard.hasFocus() || editTextDebitCardNumber.hasFocus()) {
                                if(storeListCards.size()>2) {
                                    scrollView.smoothScrollTo(0, relativeLayoutSaveDebitCard.getTop());
                                }
                            } else if(editTextCVVCreditCard.hasFocus() || editTextNameOnCreditCard.hasFocus() || editTextCreditCardNumber.hasFocus()) {
                                if(storeListCards.size()>1)
                                    scrollView.smoothScrollTo(0, relativeLayoutSaveCreditCard.getTop());
                            } else {
                                scrollView.smoothScrollTo(0, buttonPayNow.getTop());
                            }
//
                            //saveCardList.scrollTo(0, buttonPayNow.getTop());
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





//        final View activityRootView = rootView.findViewById(R.id.linearLayoutMain);
//        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//
//            @Override
//            public void onGlobalLayout() {
//                Rect r = new Rect();
//                // r will be populated with the coordinates of your view
//                // that area still visible.
//                activityRootView.getWindowVisibleDisplayFrame(r);
//
//                int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
//                if (heightDiff > 100) { // if more than 100 pixels, its
//                    // probably a keyboard...
//
//                    /************** Adapter for the parent List *************/
//
//                    ViewGroup.LayoutParams params_12 = textViewScroll.getLayoutParams();
//
//                    params_12.height = (int) (heightDiff);
//
//                    textViewScroll.setLayoutParams(params_12);
//                    textViewScroll.requestLayout();
//
//                } else {
//
//                    ViewGroup.LayoutParams params = textViewScroll.getLayoutParams();
//                    params.height = 0;
//                    textViewScroll.setLayoutParams(params);
//                    textViewScroll.requestLayout();
//
//                }
//            }
//        });

        homeActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return rootView;
    }


    public void popFrag() {

        hideKayboard();
                try {
                    getActivity().getSupportFragmentManager().popBackStack();
                }catch(Exception e) {
                    e.printStackTrace();
                }

    }


    private void valid(EditText editText, Drawable drawable) {
        drawable.setAlpha(255);
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
//        if (findViewById(com.payu.sdk.R.id.expiryCvvLinearLayout).getVisibility() == View.GONE) {
//            isExpired = false;
//            isCvvValid = true;
//        }
//        if (isCardNumberValid && !isExpired && isCvvValid ) {
//            findViewById(com.payu.sdk.R.id.makePayment).setEnabled(true);
//        } else {
//            findViewById(com.payu.sdk.R.id.makePayment).setEnabled(false);
//        }
    }

    private void invalid(EditText editText, Drawable drawable) {
        drawable.setAlpha(100);
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
//        findViewById(com.payu.sdk.R.id.makePayment).setEnabled(false);
//        findViewById(com.payu.sdk.R.id.makePayment).setBackgroundResource(com.payu.sdk.R.drawable.button);
    }

    private void resetHeader() {
//        if (findViewById(com.payu.sdk.R.id.offerMessageTextView) != null && findViewById(com.payu.sdk.R.id.offerMessageTextView).getVisibility() == View.VISIBLE) {
//            findViewById(com.payu.sdk.R.id.offerAmountTextView).setVisibility(View.GONE);
//            ((TextView) findViewById(com.payu.sdk.R.id.offerMessageTextView)).setText("");
//            ((TextView) findViewById(com.payu.sdk.R.id.amountTextView)).setGravity(Gravity.CENTER);
//            ((TextView) findViewById(com.payu.sdk.R.id.amountTextView)).setTextColor(Color.BLACK);
//            ((TextView) findViewById(com.payu.sdk.R.id.amountTextView)).setPaintFlags(0);
//            ((TextView) findViewById(com.payu.sdk.R.id.amountTextView)).setText(getString(com.payu.sdk.R.string.amount, getIntent().getExtras().getDouble(PayU.AMOUNT)));
//        }
    }

    private void makeInvalid() {
//        if (!isCardNumberValid && cardNumber.length() > 0 && !findViewById(com.payu.sdk.R.id.cardNumberEditText).isFocused())
//            ((EditText) findViewById(com.payu.sdk.R.id.cardNumberEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(com.payu.sdk.R.drawable.error_icon), null);
//        if (!isCvvValid && cvv.length() > 0 && !findViewById(com.payu.sdk.R.id.cvvEditText).isFocused())
//            ((EditText) findViewById(com.payu.sdk.R.id.cvvEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(com.payu.sdk.R.drawable.error_icon), null);
    }

    private void initComponents() {
        relative = (LinearLayout) rootView.findViewById(R.id.relative);
        relativeLayoutTopBar = (RelativeLayout) rootView.findViewById(R.id.topBar);
        layoutBackButton = (RelativeLayout) rootView.findViewById(R.id.layoutBackButton);

        linearLayoutDebitCardDetails = (LinearLayout) rootView.findViewById(R.id.linearLayoutDebitCardDetails);
        linearLayoutCreditCardDetails = (LinearLayout) rootView.findViewById(R.id.linearLayoutCreditCardDetails);
        linearLayoutNetbankingDetails = (LinearLayout) rootView.findViewById(R.id.linearLayoutNetbankingDetails);

        listLayout = (LinearLayout) rootView.findViewById(R.id.listLayout);
        listLayout.setVisibility(View.GONE);

        relativeLayoutDebitCard = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDebitCard);
        relativeLayoutCreditCard = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCreditCard);
        relativeLayoutNetBanking = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNetBanking);

        relativeLayoutDebitCard.setOnClickListener(this);
        relativeLayoutCreditCard.setOnClickListener(this);
        relativeLayoutNetBanking.setOnClickListener(this);

        imageViewSelectDebitCard = (ImageView) rootView.findViewById(R.id.imageViewSelectDebitCard);
        imageViewSelectCreditCard = (ImageView) rootView.findViewById(R.id.imageViewSelectCreditCard);
        imageViewSelectNetBanking = (ImageView) rootView.findViewById(R.id.imageViewSelectNetBanking);

        imageViewSaveDebitCard = (ImageView) rootView.findViewById(R.id.imageViewSaveDebitCard);
        imageViewSaveCreditCard = (ImageView) rootView.findViewById(R.id.imageViewSaveCreditCard);

        textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.latoRegular(homeActivity), Typeface.BOLD);
        textViewSelectPaymentMode = (TextView) rootView.findViewById(R.id.textViewSelectPaymentMode);
        textViewSelectPaymentMode.setTypeface(Fonts.latoLight(homeActivity));
        textViewDebitCard = (TextView) rootView.findViewById(R.id.textViewDebitCard);
        textViewDebitCard.setTypeface(Fonts.latoRegular(homeActivity));
        textViewCreditCard = (TextView) rootView.findViewById(R.id.textViewCreditCard);
        textViewCreditCard.setTypeface(Fonts.latoRegular(homeActivity));
        textViewNetBanking = (TextView) rootView.findViewById(R.id.textViewNetBanking);
        textViewNetBanking.setTypeface(Fonts.latoRegular(homeActivity));
        textViewAddAmount = (TextView) rootView.findViewById(R.id.textViewAddAmount);
        textViewAddAmount.setTypeface(Fonts.latoRegular(homeActivity));
        textViewAddAmountValue = (TextView) rootView.findViewById(R.id.textViewAddAmountValue);
        textViewAddAmountValue.setTypeface(Fonts.latoLight(homeActivity));
        textViewJugnooCashNote = (TextView) rootView.findViewById(R.id.textViewJugnooCashNote);
        textViewJugnooCashNote.setTypeface(Fonts.latoLight(homeActivity));
        textViewTermsAndConditions = (TextView) rootView.findViewById(R.id.textViewTermsAndConditions);
        textViewTermsAndConditions.setTypeface(Fonts.latoLight(homeActivity));

        textViewDebitCardDetails = (TextView) rootView.findViewById(R.id.textViewDebitCardDetails);
        textViewDebitCardDetails.setTypeface(Fonts.latoLight(homeActivity));

        textViewCreditCardDetails = (TextView) rootView.findViewById(R.id.textViewCreditCardDetails);
        textViewCreditCardDetails.setTypeface(Fonts.latoLight(homeActivity));

        validTextDebitCard = (TextView) rootView.findViewById(R.id.validTextDebitCard);
        validTextDebitCard.setTypeface(Fonts.latoLight(homeActivity));

        validTextCreditCard = (TextView) rootView.findViewById(R.id.validTextCreditCard);
        validTextCreditCard.setTypeface(Fonts.latoLight(homeActivity));

        editTextDebitCardNumber = (EditText) rootView.findViewById(R.id.editTextDebitCardNumber);
        editTextDebitCardNumber.setTypeface(Fonts.latoLight(homeActivity));
        editTextMonthDebitCard = (Button) rootView.findViewById(R.id.editTextMonthDebitCard);
        editTextMonthDebitCard.setTypeface(Fonts.latoLight(homeActivity));
        editTextYearDebitCard = (Button) rootView.findViewById(R.id.editTextYearDebitCard);
        editTextYearDebitCard.setTypeface(Fonts.latoLight(homeActivity));
        editTextCVVDebitCard = (EditText) rootView.findViewById(R.id.editTextCVVDebitCard);
        editTextCVVDebitCard.setTypeface(Fonts.latoLight(homeActivity));
        editTextNameOnDebitCard = (EditText) rootView.findViewById(R.id.editTextNameOnDebitCard);
        editTextNameOnDebitCard.setTypeface(Fonts.latoLight(homeActivity));

        relativeLayoutSaveDebitCard = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutSaveDebitCard);

        editTextCreditCardNumber = (EditText) rootView.findViewById(R.id.editTextCreditCardNumber);
        editTextCreditCardNumber.setTypeface(Fonts.latoLight(homeActivity));
        editTextMonthCreditCard = (Button) rootView.findViewById(R.id.editTextMonthCreditCard);
        editTextMonthCreditCard.setTypeface(Fonts.latoLight(homeActivity));
        editTextYearCreditCard = (Button) rootView.findViewById(R.id.editTextYearCreditCard);
        editTextYearCreditCard.setTypeface(Fonts.latoLight(homeActivity));
        editTextCVVCreditCard = (EditText) rootView.findViewById(R.id.editTextCVVCreditCard);
        editTextCVVCreditCard.setTypeface(Fonts.latoLight(homeActivity));
        editTextNameOnCreditCard = (EditText) rootView.findViewById(R.id.editTextNameOnCreditCard);
        editTextNameOnCreditCard.setTypeface(Fonts.latoLight(homeActivity));

        relativeLayoutSaveCreditCard = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutSaveCreditCard);

        buttonPayNow = (Button) rootView.findViewById(R.id.nbPayButton);
        buttonPayNow.setTypeface(Fonts.latoRegular(homeActivity));

        flag[0] = true;
        storeCardAdp = new StoreCardAdapter();
        saveCardList = (ListView) rootView.findViewById(R.id.saveCardList);
        saveCardList.setAdapter(storeCardAdp);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.expandListForVariableHeight(saveCardList);
            }
        }, 200);


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

        editTextMonthDebitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDemo();
            }
        });

        editTextYearDebitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDemo();
            }
        });


        editTextMonthCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDemo();
            }
        });

        editTextYearCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDemo();
            }
        });


        relativeLayoutSaveDebitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (saveDCCard) {
                    saveDCCard = false;

                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        imageViewSaveDebitCard.setBackgroundDrawable(getResources().getDrawable(R.drawable.unsave_card_icon));
                    } else {
                        imageViewSaveDebitCard.setBackground(getResources().getDrawable(R.drawable.unsave_card_icon));
                    }
                } else {
                    saveDCCard = true;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        imageViewSaveDebitCard.setBackgroundDrawable(getResources().getDrawable(R.drawable.save_card_icon));
                    } else {
                        imageViewSaveDebitCard.setBackground(getResources().getDrawable(R.drawable.save_card_icon));
                    }
                }
            }
        });

        relativeLayoutSaveCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (saveCCCard) {
                    saveCCCard = false;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        imageViewSaveCreditCard.setBackgroundDrawable(getResources().getDrawable(R.drawable.unsave_card_icon));
                    } else {
                        imageViewSaveCreditCard.setBackground(getResources().getDrawable(R.drawable.unsave_card_icon));
                    }
                } else {
                    saveCCCard = true;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        imageViewSaveCreditCard.setBackgroundDrawable(getResources().getDrawable(R.drawable.save_card_icon));
                    } else {
                        imageViewSaveCreditCard.setBackground(getResources().getDrawable(R.drawable.save_card_icon));
                    }
                }
            }
        });



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
                        DialogPopup.dialogBanner(homeActivity, "" + getResources().getString(R.string.valid_month));
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
                        DialogPopup.dialogBanner(homeActivity, "" + getResources().getString(R.string.valid_month));
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
                        DialogPopup.dialogBanner(homeActivity, "" + getResources().getString(R.string.valid_year));
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
                        DialogPopup.dialogBanner(homeActivity, "" + getResources().getString(R.string.valid_year));
                    }
                }
            }
        });

        //*************/

        Cards.initializeIssuers(getResources());

        cardNumberDrawable = getResources().getDrawable(R.drawable.card);
        cardNumberDrawable.setAlpha(100);
        editTextDebitCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, cardNumberDrawable, null);

        editTextDebitCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                editTextCVVDebitCard.getText().clear();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                cardNumber = charSequence.toString();

                issuer = Cards.getIssuer(cardNumber);

                if (issuer.contentEquals("AMEX")) {
                    editTextCVVDebitCard.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                } else {
                    editTextCVVDebitCard.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                }
                if (issuer != null) {
                    issuerDrawable = Cards.ISSUER_DRAWABLE.get(issuer);
                }

                if (issuer.contentEquals("SMAE")) {
                    // disable cvv and expiry
//                    findViewById(com.payu.sdk.R.id.expiryCvvLinearLayout).setVisibility(View.GONE);
//                    findViewById(com.payu.sdk.R.id.haveCvvExpiryLinearLayout).setVisibility(View.VISIBLE);
//                    findViewById(com.payu.sdk.R.id.dontHaveCvvExpiryLinearLayout).setVisibility(View.GONE);
                    if (Cards.validateCardNumber(cardNumber)) {
                        isCardNumberValid = true;
                        if (PayU.issuingBankDownBin != null && PayU.issuingBankDownBin.has(cardNumber.substring(0, 6))) {// oops bank is down.
                            try {
                                ((TextView) rootView.findViewById(R.id.issuerDownTextView)).setText(PayU.issuingBankDownBin.getString(cardNumber.substring(0, 6)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            rootView.findViewById(R.id.issuerDownTextView).setVisibility(View.VISIBLE);
                        } else {
                            rootView.findViewById(R.id.issuerDownTextView).setVisibility(View.GONE);
                        }
//                        if (getIntent().getExtras().getString(PayU.OFFER_KEY) != null)
//                            checkOffer(cardNumber, getIntent().getExtras().getString(PayU.OFFER_KEY));
                        valid(editTextDebitCardNumber, issuerDrawable);
                    } else {
                        isCardNumberValid = false;
                        invalid(editTextDebitCardNumber, cardNumberDrawable);
                        cardNumberDrawable.setAlpha(100);
                        resetHeader();
                    }
                } else {
                    // enable cvv and expiry
//                    findViewById(com.payu.sdk.R.id.expiryCvvLinearLayout).setVisibility(View.VISIBLE);
//                    findViewById(com.payu.sdk.R.id.haveCvvExpiryLinearLayout).setVisibility(View.GONE);
//                    findViewById(com.payu.sdk.R.id.dontHaveCvvExpiryLinearLayout).setVisibility(View.GONE);
                    if (Cards.validateCardNumber(cardNumber)) {

                        isCardNumberValid = true;

                        if (PayU.issuingBankDownBin != null && PayU.issuingBankDownBin.has(cardNumber.substring(0, 6))) {// oops bank is down.
                            try {
                                ((TextView) rootView.findViewById(R.id.issuerDownTextView)).setText("We are experiencing high failure rate for " + PayU.issuingBankDownBin.getString(cardNumber.substring(0, 6)) + " cards at this time. We recommend you pay using any other means of payment.");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            rootView.findViewById(com.payu.sdk.R.id.issuerDownTextView).setVisibility(View.VISIBLE);
                        } else {
                            rootView.findViewById(com.payu.sdk.R.id.issuerDownTextView).setVisibility(View.GONE);
                        }

//                        if (getIntent().getExtras().getString(PayU.OFFER_KEY) != null)
//                            checkOffer(cardNumber, getIntent().getExtras().getString(PayU.OFFER_KEY));
                        valid(editTextDebitCardNumber, issuerDrawable);
                    } else {
                        isCardNumberValid = false;
                        invalid(editTextDebitCardNumber, cardNumberDrawable);
                        cardNumberDrawable.setAlpha(100);
                        resetHeader();
                    }
                }

                // lets set the issuer drawable.

                if (issuer != null && issuerDrawable != null) {
                    editTextDebitCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, issuerDrawable, null);
                }

                if (editTextDebitCardNumber.length() < 2) {
                    editTextDebitCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, cardNumberDrawable, null);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        editTextCreditCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, cardNumberDrawable, null);
        editTextCreditCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                editTextCreditCardNumber.getText().clear();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                cardNumber = charSequence.toString();

                issuer = Cards.getIssuer(cardNumber);

                if (issuer.contentEquals("AMEX")) {
                    editTextCreditCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                } else {
                    editTextCreditCardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                }
                if (issuer != null) {
                    issuerDrawable = Cards.ISSUER_DRAWABLE.get(issuer);
                }

                if (issuer.contentEquals("SMAE")) {
                    if (Cards.validateCardNumber(cardNumber)) {
                        isCardNumberValid = true;
                        if (PayU.issuingBankDownBin != null && PayU.issuingBankDownBin.has(cardNumber.substring(0, 6))) {// oops bank is down.
                            try {
                                ((TextView) rootView.findViewById(R.id.issuerDownTextView)).setText(PayU.issuingBankDownBin.getString(cardNumber.substring(0, 6)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            rootView.findViewById(R.id.issuerDownTextView).setVisibility(View.VISIBLE);
                        } else {
                            rootView.findViewById(R.id.issuerDownTextView).setVisibility(View.GONE);
                        }
                        valid(editTextCreditCardNumber, issuerDrawable);
                    } else {
                        isCardNumberValid = false;
                        invalid(editTextCreditCardNumber, cardNumberDrawable);
                        cardNumberDrawable.setAlpha(100);
                        resetHeader();
                    }
                } else {
                    if (Cards.validateCardNumber(cardNumber)) {

                        isCardNumberValid = true;

                        if (PayU.issuingBankDownBin != null && PayU.issuingBankDownBin.has(cardNumber.substring(0, 6))) {// oops bank is down.
                            try {
                                ((TextView) rootView.findViewById(R.id.issuerDownTextView)).setText("We are experiencing high failure rate for " + PayU.issuingBankDownBin.getString(cardNumber.substring(0, 6)) + " cards at this time. We recommend you pay using any other means of payment.");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            rootView.findViewById(com.payu.sdk.R.id.issuerDownTextView).setVisibility(View.VISIBLE);
                        } else {
                            rootView.findViewById(com.payu.sdk.R.id.issuerDownTextView).setVisibility(View.GONE);
                        }

                        valid(editTextCreditCardNumber, issuerDrawable);
                    } else {
                        isCardNumberValid = false;
                        invalid(editTextCreditCardNumber, cardNumberDrawable);
                        cardNumberDrawable.setAlpha(100);
                        resetHeader();
                    }
                }

                // lets set the issuer drawable.

                if (issuer != null && issuerDrawable != null) {
                    editTextCreditCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, issuerDrawable, null);
                }

                if (editTextCreditCardNumber.length() < 2) {
                    editTextCreditCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, cardNumberDrawable, null);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /************/


        buttonPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKayboard();
                if (editTextDebitCardNumber.length() >= 19) {
                    isExpired = true;
                } else {
                    isExpired = false;
                }

                if (selectedFlag) {
                    if (homeActivity.pgName.equalsIgnoreCase("NB")) {
                        homeActivity.bankCode = bankCode;
                        homeActivity.ccNum = "";
                        homeActivity.ccName = "";
                        homeActivity.ccvv = "";
                        homeActivity.ccexpmon = "";
                        homeActivity.ccexpyr = "";
                        makeNetbackingPayment();
                    } else if (homeActivity.pgName.equalsIgnoreCase("Stored Cards")) {


                        for (int i = 0; i < storeListCards.size(); i++) {
                            Log.e("value", "save value = " + editTextValue[i]);
                            if (flag[i]) {
                                //((ViewGroup)saveCardList.getItemAtPosition(i)).findViewById(R.id.editTextCVVDebitCard);
                                homeActivity.ccvv = editTextValue[i];
                                String tokenValue = storeListCards.get(i).card_token;

                                if (homeActivity.ccvv != null) {
                                    if (homeActivity.ccvv.length() > 2 && tokenValue.length() > 1) {
                                        saveCardPayment(tokenValue);
                                    } else {
                                        new DialogPopup().dialogBanner(homeActivity, "" + getResources().getString(R.string.no_field_empty));
                                    }
                                } else if ("MAESTRO".equalsIgnoreCase(storeListCards.get(i).card_brand) && tokenValue.length() > 1) {
                                    saveCardPayment(tokenValue);
                                } else {
                                    new DialogPopup().dialogBanner(homeActivity, "" + getResources().getString(R.string.no_field_empty));
                                }
//                            Toast.makeText(homeActivity, "List = " + editTextValue[i], Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        homeActivity.bankCode = "CC";
                        if (optionSelect == 0) {
                            // DC
                            homeActivity.ccNum = editTextDebitCardNumber.getText().toString().trim();
                            homeActivity.ccNum = homeActivity.ccNum.replace("-", "");
                            homeActivity.ccName = editTextNameOnDebitCard.getText().toString().trim();
                            homeActivity.ccvv = editTextCVVDebitCard.getText().toString().trim();
                            homeActivity.ccexpmon = editTextMonthDebitCard.getText().toString().trim();
                            homeActivity.ccexpyr = editTextYearDebitCard.getText().toString().trim();
                        } else if (optionSelect == 1) {
                            // CC
                            homeActivity.ccNum = editTextCreditCardNumber.getText().toString().trim();
                            homeActivity.ccNum = homeActivity.ccNum.replace("-", "");
                            homeActivity.ccName = editTextNameOnCreditCard.getText().toString().trim();
                            homeActivity.ccvv = editTextCVVCreditCard.getText().toString().trim();
                            homeActivity.ccexpmon = editTextMonthCreditCard.getText().toString().trim();
                            homeActivity.ccexpyr = editTextYearCreditCard.getText().toString().trim();
                        }

                        isCvvValid = false;
                        if (isExpired) {
                            isCvvValid = true;
                        } else if (homeActivity.ccvv.length() > 2) {
                            isCvvValid = true;
                        } else if (homeActivity.ccvv.length() == 0) {
                            isCvvValid = false;
                        }


                        if (homeActivity.ccNum.length() > 10 && homeActivity.ccName.length() > 1 && isCvvValid && homeActivity.ccexpmon.length() > 1 && homeActivity.ccexpyr.length() > 3) {
                            if (Integer.parseInt(homeActivity.ccexpyr) < year || Integer.parseInt(homeActivity.ccexpyr) >= year + 50) {
                                new DialogPopup().dialogBanner(homeActivity, "" + getResources().getString(R.string.invalid_year));
                            } else if (Integer.parseInt(homeActivity.ccexpmon) < month && Integer.parseInt(homeActivity.ccexpyr) == year) {
                                new DialogPopup().dialogBanner(homeActivity, "" + getResources().getString(R.string.invalid_card_info));
                            } else {
                                makePayment();
                                if (optionSelect == 0) {
                                    FlurryEventLogger.event(PAYMENT_BY_DEBIT_CARD);
                                }
                                else if (optionSelect == 1) {
                                    FlurryEventLogger.event(PAYMENT_BY_CREDIT_CARD);
                                }
                                FlurryEventLogger.event(PAYMENT_MADE_FOR_JUGNOO_CASH);
                            }
                        } else if (homeActivity.ccNum.length() == 0 || homeActivity.ccName.length() == 0 || !isCvvValid || homeActivity.ccexpmon.length() == 0 || homeActivity.ccexpyr.length() == 0) {
                            new DialogPopup().dialogBanner(homeActivity, "" + getResources().getString(R.string.no_field_empty));
                        } else {
                            if (homeActivity.ccexpmon.length() < 2) {
                                new DialogPopup().dialogBanner(homeActivity, "" + getResources().getString(R.string.invalid_month));
                            } else if (homeActivity.ccexpyr.length() < 4) {
                                new DialogPopup().dialogBanner(homeActivity, "" + getResources().getString(R.string.invalid_year));
                            } else if (homeActivity.ccvv.length() < 3) {
                                new DialogPopup().dialogBanner(homeActivity, "" + getResources().getString(R.string.invalid_cvv));
                            } else if (homeActivity.ccName.length() < 3) {
                                new DialogPopup().dialogBanner(homeActivity, "" + getResources().getString(R.string.invalid_name));
                            } else {
                                new DialogPopup().dialogBanner(homeActivity, "" + getResources().getString(R.string.invalid_info));
                            }
                        }
                    }
                } else {
                    new DialogPopup().dialogBanner(homeActivity, "" + getResources().getString(R.string.select_mode));
                }
            }
        });
    }

    public void clearDCInfo() {
        editTextDebitCardNumber.setText("");
        editTextNameOnDebitCard.setText("");
        editTextCVVDebitCard.setText("");
        editTextMonthDebitCard.setText("");
        editTextYearDebitCard.setText("");
    }


    public void clearCCInfo() {
        editTextCreditCardNumber.setText("");
        editTextNameOnCreditCard.setText("");
        editTextCVVCreditCard.setText("");
        editTextMonthCreditCard.setText("");
        editTextYearCreditCard.setText("");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativeLayoutDebitCard:
                homeActivity.pgName = "DC";
                selectedFlag = true;
                clearCCInfo();
                optionSelect = 0;
                rootView.findViewById(com.payu.sdk.R.id.nbPayButton).setEnabled(true);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (linearLayoutDebitCardDetails.getVisibility() == View.VISIBLE) {
                            homeActivity.pgName = "";
                            selectedFlag = false;
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


                for(int i=0;i<storeListCards.size();i++) {
                    flag[i] = false;
                }
                saveCardList.setAdapter(storeCardAdp);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.expandListForVariableHeight(saveCardList);
                    }
                }, 20);
                break;
            case R.id.relativeLayoutCreditCard:
                homeActivity.pgName = "CC";
                selectedFlag = true;
                clearDCInfo();
                rootView.findViewById(com.payu.sdk.R.id.nbPayButton).setEnabled(true);
                optionSelect = 1;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (linearLayoutCreditCardDetails.getVisibility() == View.VISIBLE) {
                            homeActivity.pgName = "";
                            selectedFlag = false;
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

                for(int i=0;i<storeListCards.size();i++) {
                    flag[i] = false;
                }
                saveCardList.setAdapter(storeCardAdp);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.expandListForVariableHeight(saveCardList);
                    }
                }, 20);

                break;


                    case R.id.relativeLayoutNetBanking:
                        homeActivity.pgName = "NB";
                        rootView.findViewById(com.payu.sdk.R.id.nbPayButton).setEnabled(false);
                        selectedFlag = true;
                        optionSelect = 2;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (linearLayoutNetbankingDetails.getVisibility() == View.VISIBLE) {
                                    homeActivity.pgName = "";
                                    selectedFlag = false;
                                    rootView.findViewById(com.payu.sdk.R.id.nbPayButton).setEnabled(true);
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

                        for(int i=0;i<storeListCards.size();i++) {
                            flag[i] = false;
                        }
                        saveCardList.setAdapter(storeCardAdp);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Utils.expandListForVariableHeight(saveCardList);
                            }
                        }, 20);

                        FlurryEventLogger.event(PAYMENT_BY_NET_BANKING);

                        break;

        }
    }

    /**
     * Method used to hide all layouts
     */
    public void hideLayout() {

        linearLayoutDebitCardDetails.setVisibility(View.GONE);
        imageViewSelectDebitCard.setBackgroundResource(R.drawable.ic_payment_mode_unpressed);

        linearLayoutCreditCardDetails.setVisibility(View.GONE);
        imageViewSelectCreditCard.setBackgroundResource(R.drawable.ic_payment_mode_unpressed);

        linearLayoutNetbankingDetails.setVisibility(View.GONE);
        imageViewSelectNetBanking.setBackgroundResource(R.drawable.ic_payment_mode_unpressed);
    }
//    /**
//     * Method used to hide keyboard if outside touched.
//     *
//     * @param view
//     */
//    private void setupUI(View view) {
//        // Set up touch listener for non-text box views to hide keyboard.
//        if (!(view instanceof EditText)) {
//            view.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    try {
//                        InputMethodManager inputMethodManager = (InputMethodManager) homeActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//                        inputMethodManager.hideSoftInputFromWindow(homeActivity.getCurrentFocus().getWindowToken(), 0);
//                    }catch(Exception e) {
//
//                    }
//                    return false;
//                }
//
//            });
//        }
//        // If a layout container, iterate over children and seed recursion.
//        if (view instanceof ViewGroup) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                View innerView = ((ViewGroup) view).getChildAt(i);
//                setupUI(innerView);
//            }
//        }
//    }

    public void hideKayboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) homeActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(homeActivity.getCurrentFocus().getWindowToken(), 0);
        }catch(Exception e) {

        }
    }

    /**
     * Method used to set bank value into list
     */
    public void setupAdapter() {

        com.payu.sdk.adapters.NetBankingAdapter adapter = new com.payu.sdk.adapters.NetBankingAdapter(homeActivity, PayU.availableBanks);

        Spinner netBankingSpinner = (Spinner) rootView.findViewById(com.payu.sdk.R.id.netBankingSpinner);
        netBankingSpinner.setAdapter(adapter);

        netBankingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.rgb(69, 79, 79));
                    float size = ASSL.Xscale() * 31;
                    ((TextView) adapterView.getChildAt(0)).setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

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
            DialogPopup.showLoadingDialog(homeActivity, "Loading...");
            RequestParams params = new RequestParams();

            params.put("client_id", Config.getClientId());
            params.put("access_token", Data.userData.accessToken);
            params.put("is_access_token_new", "1");
            params.put("amount", ""+enterAmount);
            Log.e("url", "url = "+ Config.getTXN_URL()+params);
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
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(homeActivity, res)) {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

                                    homeActivity.txnId = "";
                                    homeActivity.uName = "";
                                    homeActivity.uEmail = "";
                                    homeActivity.hash = "";
                                    homeActivity.hashSdk = "";
                                    homeActivity.uProdInfo = "";


                                    homeActivity.txnId = res.getString("txn_id");
                                    homeActivity.uName = res.getString("first_name");
                                    homeActivity.uEmail = res.getString("email");
                                    homeActivity.hash = res.getString("hash_txn");
                                    homeActivity.hashSdk = res.getString("hash_sdk");
                                    homeActivity.uProdInfo = res.getString("product_info");

                                    // for store cards
                                    PayU.saveUserCardHash = res.getString("hash_cards_save");
                                    PayU.deleteCardHash = res.getString("hash_cards_edit");
                                    PayU.getUserCardHash = res.getString("hash_cards_get");
                                    PayU.deleteCardHash = res.getString("hash_cards_delete");
                                    PayU.userCredentials = res.getString("user_credentials");

//
                                    PayU.paymentHash = homeActivity.hash;
                                    PayU.ibiboCodeHash = homeActivity.hashSdk;

                                    getBankList();

                                } else if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                    String error = res.getString("error");
                                    DialogPopup.alertPopup(homeActivity, "", error);
                                } else {
                                    new DialogPopup().alertPopup(homeActivity, "", Data.SERVER_ERROR_MSG);
                                }

                            }
                            //LoadingBox.dismissLoadingDialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                            DialogPopup.dismissLoadingDialog();
                            new DialogPopup().alertPopup(homeActivity, "", Data.SERVER_ERROR_MSG);
                        }

                    }

                    @Override
                    public void onFailure(Throwable arg0) {
                        android.util.Log.e("request fail", arg0.toString());
                        DialogPopup.dismissLoadingDialog();
                    }
                });
        }
        else{
            new DialogPopup().alertPopup(homeActivity, "", Data.CHECK_INTERNET_MSG);
        }
    }


    /**
     * Make payment for CC/DC
     */
    public void makePayment() {
        Params requiredParams = new Params();
        builder.set(PayU.MODE, homeActivity.pgName);
        builder.set(PayU.PRODUCT_INFO, ""+homeActivity.uProdInfo);
        builder.set(PayU.AMOUNT, enterAmount);


        try {
            homeActivity.uEmail = URLEncoder.encode(homeActivity.uEmail, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        builder.set(PayU.EMAIL, homeActivity.uEmail);
        builder.set(PayU.FIRSTNAME, homeActivity.uName);

        builder.set(PayU.TXNID, homeActivity.txnId);
        builder.set(PayU.SURL, Config.getSURL());
        builder.set(PayU.FURL, Config.getFURL());

//        builder.set(PayU.STORE_CARD, "1");

        String nameOnCard = homeActivity.ccName;
        if (nameOnCard.length() < 3) {
            nameOnCard = "Jugnoo " + nameOnCard;
        }


        requiredParams.put(PayU.CARD_NUMBER, homeActivity.ccNum);
        requiredParams.put(PayU.EXPIRY_MONTH, homeActivity.ccexpmon);
        requiredParams.put(PayU.EXPIRY_YEAR, homeActivity.ccexpyr);
        requiredParams.put(PayU.CARDHOLDER_NAME, nameOnCard);
        requiredParams.put(PayU.CVV, homeActivity.ccvv);

        requiredParams.put(PayU.EMAIL, homeActivity.uEmail);
        requiredParams.put(PayU.FIRSTNAME, homeActivity.uName);

        requiredParams.put(PayU.BANKCODE, homeActivity.bankCode);
        requiredParams.put(PayU.AMOUNT, enterAmount);
        requiredParams.put(PayU.PRODUCT_INFO, ""+homeActivity.uProdInfo);
        requiredParams.put(PayU.TXNID, homeActivity.txnId);
        requiredParams.put(PayU.SURL, Config.getSURL());
        requiredParams.put(PayU.FURL, Config.getFURL());

        if(optionSelect == 0 && saveDCCard) {
            // DC
            requiredParams.put(PayU.STORE_CARD, "1");
        }
        else if(optionSelect == 1 && saveCCCard) {
            // CC
            requiredParams.put(PayU.STORE_CARD, "1");
        }

        requiredParams.put(PayU.USER_CREDENTIALS, PayU.userCredentials);



        try {
            Bundle bundle = homeActivity.getPackageManager().getApplicationInfo(homeActivity.getPackageName(), PackageManager.GET_META_DATA).metaData;

            requiredParams.put(PayU.MERCHANT_KEY, bundle.getString("payu_merchant_id"));//kayValue
            payment = builder.create();

            String postData = PayU.getInstance(homeActivity).createPayment(payment, requiredParams);
            Log.e("postData", "postData = " + postData);



            Intent intent = new Intent(homeActivity, ProcessPaymentActivity.class);
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


    /**
     * Payment from stored card.
     * @param tokenValue Card token value
     */
    public void saveCardPayment(String tokenValue) {
        Params requiredParams = new Params();
        builder.set(PayU.MODE, String.valueOf(PayU.PaymentMode.CC));
        builder.set(PayU.PRODUCT_INFO, ""+homeActivity.uProdInfo);
        builder.set(PayU.AMOUNT, enterAmount);


        try {
            homeActivity.uEmail = URLEncoder.encode(homeActivity.uEmail, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        builder.set(PayU.EMAIL, homeActivity.uEmail);
        builder.set(PayU.FIRSTNAME, homeActivity.uName);

        builder.set(PayU.TXNID, homeActivity.txnId);
        builder.set(PayU.SURL, Config.getSURL());
        builder.set(PayU.FURL, Config.getFURL());


        requiredParams.put("store_card_token", tokenValue);
        requiredParams.put(PayU.CVV, homeActivity.ccvv);


        requiredParams.put(PayU.EMAIL, homeActivity.uEmail);
        requiredParams.put(PayU.FIRSTNAME, homeActivity.uName);
        requiredParams.put(PayU.AMOUNT, enterAmount);
        requiredParams.put(PayU.PRODUCT_INFO, ""+homeActivity.uProdInfo);
        requiredParams.put(PayU.TXNID, homeActivity.txnId);
        requiredParams.put(PayU.SURL, Config.getSURL());
        requiredParams.put(PayU.FURL, Config.getFURL());

        //requiredParams.put(PayU.STORE_CARD, "1");

        requiredParams.put(PayU.USER_CREDENTIALS, PayU.userCredentials);



        try {
            Bundle bundle = homeActivity.getPackageManager().getApplicationInfo(homeActivity.getPackageName(), PackageManager.GET_META_DATA).metaData;

            requiredParams.put(PayU.MERCHANT_KEY, bundle.getString("payu_merchant_id"));//kayValue
            payment = builder.create();

            String postData = PayU.getInstance(homeActivity).createPayment(payment, requiredParams);
            Log.e("postData", "postData = " + postData);



            Intent intent = new Intent(homeActivity, ProcessPaymentActivity.class);
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

            builder.set(PayU.MODE, homeActivity.pgName);
            builder.set(PayU.PRODUCT_INFO, ""+homeActivity.uProdInfo);
            builder.set(PayU.AMOUNT, enterAmount);//homeActivity.enterAmount


            try {
                homeActivity.uEmail = URLEncoder.encode(homeActivity.uEmail, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            builder.set(PayU.EMAIL, homeActivity.uEmail);
            builder.set(PayU.FIRSTNAME, homeActivity.uName);

            builder.set(PayU.TXNID, homeActivity.txnId);
            builder.set(PayU.SURL, Config.getSURL());
            builder.set(PayU.FURL, Config.getFURL());


            String nameOnCard = homeActivity.ccName;
            if (nameOnCard.length() < 3) {
                nameOnCard = "PayU " + nameOnCard;
            }


            requiredParams.put(PayU.EMAIL, homeActivity.uEmail);
            requiredParams.put(PayU.FIRSTNAME, homeActivity.uName);

            requiredParams.put(PayU.BANKCODE, homeActivity.bankCode);
            requiredParams.put(PayU.AMOUNT, enterAmount);
            requiredParams.put(PayU.PRODUCT_INFO, ""+homeActivity.uProdInfo);
            requiredParams.put(PayU.TXNID, homeActivity.txnId);
            requiredParams.put(PayU.SURL, Config.getSURL());
            requiredParams.put(PayU.FURL, Config.getFURL());



            try {
                Bundle bundle = homeActivity.getPackageManager().getApplicationInfo(homeActivity.getPackageName(), PackageManager.GET_META_DATA).metaData;

                requiredParams.put(PayU.MERCHANT_KEY, bundle.getString("payu_merchant_id"));//kayValue
                payment = builder.create();

                String postData = PayU.getInstance(homeActivity).createPayment(payment, requiredParams);
                Log.e("postData", "postData = " + postData);

                Intent intent = new Intent(homeActivity, ProcessPaymentActivity.class);
                intent.putExtra(Constants.POST_DATA, postData);

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
        homeActivity.getListBanks();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        ((TextView) parent.getChildAt(0)).setTextColor(0x000000FF);
        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void stopLoading() {
        DialogPopup.dismissLoadingDialog();
    }

    public void notifyStoreCard() {
        storeListCards.clear();
        Gson gson = new Gson();
        try {
            Type listType = new TypeToken<List<StoreCard>>() {
            }.getType();
            storeListCards = gson.fromJson(PayU.storedCards.toString(), listType);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(storeListCards.size()>0) {
            flag = new boolean[storeListCards.size()];
            editTextValue = new String[storeListCards.size()];
            flag[0] = true;
            selectedFlag = true;
            listLayout.setVisibility(View.VISIBLE);
        } else {
            selectedFlag = false;
            listLayout.setVisibility(View.GONE);
        }

        storeCardAdp.notifyDataSetChanged();

        DialogPopup.dismissLoadingDialog();
    }

    public void removeCard() {
        storeListCards.remove(deletedCardPostion);
        if(storeListCards.size()>0) {
            storeCardAdp.notifyDataSetChanged();
        } else {
            listLayout.setVisibility(View.GONE);
        }
        DialogPopup.dismissLoadingDialog();
    }

    class ViewHolderMeal {
        LinearLayout relative, nameLayout, noLayout;
        RelativeLayout upperLayout;
        ImageView imageViewSelectCard, cardImageView;
        EditText editTextCVVDebitCard;
        TextView textViewCard, deleteText, textViewCardDetail;
        int pos;
    }

    public ViewHolderMeal holderTemp = new ViewHolderMeal();
    class StoreCardAdapter extends BaseAdapter {

        LayoutInflater inflater = null;
        ViewHolderMeal holder;

        public StoreCardAdapter() {
            inflater = (LayoutInflater) homeActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return storeListCards.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolderMeal();


                convertView = inflater.inflate(R.layout.list_item_save_card, null);

                holder.textViewCard = (TextView) convertView.findViewById(R.id.textViewCard);
                holder.textViewCard.setTypeface(Fonts.latoRegular(homeActivity));

                holder.deleteText = (TextView) convertView.findViewById(R.id.deleteText);
                holder.deleteText.setTypeface(Fonts.latoRegular(homeActivity));

                holder.textViewCardDetail = (TextView) convertView.findViewById(R.id.textViewCardDetail);
                holder.textViewCardDetail.setTypeface(Fonts.latoLight(homeActivity));

                holder.imageViewSelectCard = (ImageView) convertView.findViewById(R.id.imageViewSelectCard);
                holder.cardImageView = (ImageView) convertView.findViewById(R.id.cardImageView);

                holder.editTextCVVDebitCard = (EditText) convertView.findViewById(R.id.editTextCVVDebitCard);
                holder.editTextCVVDebitCard.setTag(holder);

                holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);

                holder.nameLayout = (LinearLayout) convertView.findViewById(R.id.nameLayout);
                holder.noLayout = (LinearLayout) convertView.findViewById(R.id.noLayout);

                holder.upperLayout = (RelativeLayout) convertView.findViewById(R.id.upperLayout);

                holder.relative.setLayoutParams(new ListView.LayoutParams(720, ListView.LayoutParams.WRAP_CONTENT));
                holder.upperLayout.setTag(holder);
                holder.deleteText.setTag(holder);
                holder.relative.setTag(holder);
                ASSL.DoMagic(holder.relative);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolderMeal) convertView.getTag();
            }

            holder.pos = position;


            holder.textViewCard.setText(storeListCards.get(position).card_no);
            holder.textViewCardDetail.setText(storeListCards.get(position).card_brand+ " | "+ storeListCards.get(position).name_on_card);

            int sdk = android.os.Build.VERSION.SDK_INT;
            if(storeListCards.get(position).card_brand.equalsIgnoreCase("MASTERCARD")) {
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.cardImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.mastercard));
                }else {
                    holder.cardImageView.setBackground(getResources().getDrawable(R.drawable.mastercard));
                }
            } else if(storeListCards.get(position).card_brand.equalsIgnoreCase("VISA")) {
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.cardImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.visa));
                }else {
                    holder.cardImageView.setBackground(getResources().getDrawable(R.drawable.visa));
                }
            } else if(storeListCards.get(position).card_brand.equalsIgnoreCase("AMERICANEXPRESS")) {
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.cardImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.amex));
                }else {
                    holder.cardImageView.setBackground(getResources().getDrawable(R.drawable.amex));
                }
            } else{
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.cardImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_placeholder_card_payment));
                }else {
                    holder.cardImageView.setBackground(getResources().getDrawable(R.drawable.ic_placeholder_card_payment));
                }
            }



            if(flag[holder.pos]) {
                Log.e("flag","==="+position);
                homeActivity.pgName = "Stored Cards";
                optionSelect = 3;
                holder.nameLayout.setVisibility(View.VISIBLE);
                holder.noLayout.setVisibility(View.VISIBLE);

                holder.deleteText.setVisibility(View.VISIBLE);

                holder.upperLayout.setBackgroundColor(homeActivity.getResources().getColor(R.color.grey_lightest));
                holder.nameLayout.setBackgroundColor(homeActivity.getResources().getColor(R.color.grey_lightest));

                holder.imageViewSelectCard.setBackgroundResource(R.drawable.ic_payment_mode_pressed);
            } else {

                holder.upperLayout.setBackgroundColor(homeActivity.getResources().getColor(R.color.white_light_grey));

                holder.nameLayout.setVisibility(View.GONE);
                holder.noLayout.setVisibility(View.GONE);
                holder.deleteText.setVisibility(View.GONE);
                holder.imageViewSelectCard.setBackgroundResource(R.drawable.ic_payment_mode_unpressed);
            }

            holder.deleteText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder = (ViewHolderMeal) v.getTag();
                    int id = holder.pos;
                    deletedCardPostion = id;
                    new DialogPopup().alertPopupTwoButtonsWithListeners(homeActivity, "Delete", "Are you sure you want to delete the card", "OK", "Cancel", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DialogPopup.showLoadingDialog(homeActivity, "Deleting card...");
                            homeActivity.deleteCard(storeListCards.get(deletedCardPostion).card_token);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, false, false);


                }
            });

            holder.editTextCVVDebitCard.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                    editTextValue[positionValue] = arg0.toString();
                }
            });


            holder.upperLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    holderTemp = new ViewHolderMeal();
                    holderTemp = holder;
                    holder = (ViewHolderMeal) v.getTag();

                    int id = holder.pos;
                    positionValue = id;
                    if (flag[id]) {
                        flag[id] = false;
                    } else {
                        for (int i = 0; i < storeListCards.size(); i++) {
                            flag[i] = false;
                        }
                        flag[id] = true;
                    }
                    holder.editTextCVVDebitCard.setText("");
                    cvvValue = "";
                    notifyDataSetChanged();

                }
            });

            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            hideLayout();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Utils.expandListForVariableHeight(saveCardList);
                }
            }, 20);
            //Utils.expandListForVariableHeight(saveCardList);


        }

    }


    public void showDialogDemo() {

        DialogFragment newFragment = new SelectDateFragment();
        newFragment.show(getFragmentManager(), "DatePicker");

    }

    public static DatePickerDialog dialog;
    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, DatePicker.OnDateChangedListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();

            dialog = new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

//            datePicker.init(c.get(Calendar.YEAR),
//                c.get(Calendar.MONTH),
//                c.get(Calendar.DAY_OF_MONTH),
//                new DatePicker.OnDateChangedListener() {
//                    @Override
//                    public void onDateChanged(DatePicker view, int year,
//                                              int monthOfYear, int dayOfMonth) {
//                        String myDate = String.valueOf(dayOfMonth);
//                        String myMonth =String.valueOf(monthOfYear);
//
//                        if (myDate.length() == 1) {
//                            myDate = "0" + dayOfMonth;
//                        }
//                        if (myMonth.length() == 1 && monthOfYear < 9) {
//                            myMonth = "0" + (monthOfYear + 1);
//                        } else {
//                            myMonth = String.valueOf((monthOfYear + 1));
//                        }
//                        Toast.makeText(homeActivity, "asd", Toast.LENGTH_SHORT).show();
//                        //textview.setText(year + "-" + myMonth + "-" + myDate);
//                    }
//                });

            dialog.getDatePicker().setSpinnersShown(true );

            // hiding calendarview and daySpinner in datePicker
            dialog.getDatePicker().setCalendarViewShown(false);

            try {
                String string_date = "31-December-"+(c.get(Calendar.YEAR)+50);
                SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
                Date d = f.parse(string_date);
                long maxTime = d.getTime();

                long minTime = c.getTimeInMillis();

                dialog.getDatePicker().setMinDate(minTime);
                dialog.getDatePicker().setMaxDate(maxTime);
            } catch(Exception e) {

            }

//            LinearLayout pickerParentLayout = (LinearLayout) dialog.getDatePicker().getChildAt(0);
//
//            LinearLayout pickerSpinnersHolder = (LinearLayout) pickerParentLayout.getChildAt(0);
//
//            pickerSpinnersHolder.getChildAt(0).setVisibility(View.GONE);


//            try{
//                Field[] datePickerDialogFields = dialog.getClass().getDeclaredFields();
//                for (Field datePickerDialogField : datePickerDialogFields) {
//                    if (datePickerDialogField.getName().equals("mDatePicker")) {
//                        datePickerDialogField.setAccessible(true);
//                        DatePicker datePicker = (DatePicker) datePickerDialogField.get(dialog);
//                        Field datePickerFields[] = datePickerDialogField.getType().getDeclaredFields();
//                        for (Field datePickerField : datePickerFields) {
//                            if ("mDelegate".equals(datePickerField.getName())) {
//                                datePickerField.setAccessible(true);
//                                Object dayPicker = datePickerField.get(datePicker);
//                                ((View) dayPicker).setVisibility(View.GONE);
//                            }
//                        }
//                    }
//
//                }
//            }catch(Exception ex){
//                ex.printStackTrace();
//            }

            dialog.setTitle("Pick date");
            return dialog;
        }



        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            //dialog.setTitle("Pick date");
            populateSetDate(yy, mm+1, dd);
        }
        public void populateSetDate(int year, int month, int day) {
//            Toast.makeText(homeActivity, month+"/"+day+"/"+year, Toast.LENGTH_SHORT).show();
            //dob.setText(month+"/"+day+"/"+year);
            if(optionSelect == 0) {
                if (month < 10)
                    editTextMonthDebitCard.setText("0" + month);
                else
                    editTextMonthDebitCard.setText("" + month);

                editTextYearDebitCard.setText("" + year);

                editTextMonthCreditCard.setText("");
                editTextYearCreditCard.setText("");
            } else if(optionSelect == 1) {
                if (month < 10)
                    editTextMonthCreditCard.setText("0" + month);
                else
                    editTextMonthCreditCard.setText("" + month);

                editTextYearCreditCard.setText("" + year);


                editTextMonthDebitCard.setText("");
                editTextYearDebitCard.setText("");
            }
        }

        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dialog.setTitle("Pick date");
        }
    }

}
