package product.clicklabs.jugnoo.wallet;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;

import java.text.DecimalFormat;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.HomeActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyBoardStateHandler;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class WalletAddPaymentFragment extends Fragment implements FlurryEventNames {
	
	LinearLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;
	
	TextView textViewHelp;//, rupee;
	EditText editTextAmount;
	Button button599, button999, button1999, buttonMakePayment;
	TextView textViewCurrentBalance, textViewCurrentBalanceValue;

    View rootView;
    public PaymentActivity homeActivity;

    ScrollView scrollView;
    TextView textViewScroll;
    LinearLayout linearLayoutMain;
    boolean scrolled = false;
	

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
        editTextAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
	}





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cash_balance, container, false);

        homeActivity = (PaymentActivity) getActivity();


 
		
		relative = (LinearLayout) rootView.findViewById(R.id.relative);
		new ASSL(homeActivity, relative, 1134, 720, false);

        setupUI(rootView.findViewById(R.id.relative));

		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack); 
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(homeActivity), Typeface.BOLD);
		
		textViewHelp = (TextView) rootView.findViewById(R.id.textViewAddCash); textViewHelp.setTypeface(Fonts.latoLight(homeActivity));
		
		editTextAmount = (EditText) rootView.findViewById(R.id.editTextAmount); editTextAmount.setTypeface(Fonts.latoRegular(homeActivity));
        editTextAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        scrolled = false;
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);
        linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);

//        rupee = (TextView) rootView.findViewById(R.id.rupee);
//        rupee.setTypeface(Fonts.latoLight(homeActivity));
//        rupee.setVisibility(View.GONE);
		
		button599 = (Button) rootView.findViewById(R.id.buttonAmount1); button599.setTypeface(Fonts.latoRegular(homeActivity));
		button999 = (Button) rootView.findViewById(R.id.buttonAmount2); button999.setTypeface(Fonts.latoRegular(homeActivity));
		button1999 = (Button) rootView.findViewById(R.id.buttonAmount3); button1999.setTypeface(Fonts.latoRegular(homeActivity));
		buttonMakePayment = (Button) rootView.findViewById(R.id.buttonMakePayment); buttonMakePayment.setTypeface(Fonts.latoRegular(homeActivity));
		
		textViewCurrentBalance = (TextView) rootView.findViewById(R.id.textViewCurrentBalance); textViewCurrentBalance.setTypeface(Fonts.latoRegular(homeActivity));
		textViewCurrentBalanceValue = (TextView) rootView.findViewById(R.id.textViewCurrentBalanceValue); textViewCurrentBalanceValue.setTypeface(Fonts.latoRegular(homeActivity));
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(AddPaymentPath.FROM_WALLET == PaymentActivity.addPaymentPath) {
                    performBackPressed();

                } else {
                    homeActivity.finish();
                    homeActivity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }
            }
        });
		
		
		button599.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                editTextAmount.setText("599");
                editTextAmount.setSelection(editTextAmount.getText().length());
                FlurryEventLogger.event(ADD_JUGNOO_CASH_WITH_GIVEN_AMOUNT);
            }
        });
		
		button999.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextAmount.setText("999");
                editTextAmount.setSelection(editTextAmount.getText().length());
                FlurryEventLogger.event(ADD_JUGNOO_CASH_WITH_GIVEN_AMOUNT);
            }
        });

		button1999.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextAmount.setText("1999");
                editTextAmount.setSelection(editTextAmount.getText().length());
                FlurryEventLogger.event(ADD_JUGNOO_CASH_WITH_GIVEN_AMOUNT);
            }
        });

        editTextAmount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = editTextAmount.getText().toString();
                try {
                    int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        if (text.equalsIgnoreCase("599")) {
                            button599.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_wallet_yellow_border));
                            button999.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_wallet_border));
                            button1999.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_wallet_border));

                        } else if (text.equalsIgnoreCase("999")) {
                            button599.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_wallet_border));
                            button999.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_wallet_yellow_border));
                            button1999.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_wallet_border));
                        } else if (text.equalsIgnoreCase("1999")) {
                            button599.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_wallet_border));
                            button999.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_wallet_border));
                            button1999.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_wallet_yellow_border));
                        } else {
                            button599.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_wallet_border));
                            button999.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_wallet_border));
                            button1999.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_wallet_border));
                            FlurryEventLogger.event(ADD_JUGNOO_CASH_MANUALLY);
                        }
                    } else {
                        if (text.equalsIgnoreCase("599")) {
                            button599.setBackground(getResources().getDrawable(R.drawable.background_wallet_yellow_border));
                            button999.setBackground(getResources().getDrawable(R.drawable.background_wallet_border));
                            button1999.setBackground(getResources().getDrawable(R.drawable.background_wallet_border));

                        } else if (text.equalsIgnoreCase("999")) {
                            button599.setBackground(getResources().getDrawable(R.drawable.background_wallet_border));
                            button999.setBackground(getResources().getDrawable(R.drawable.background_wallet_yellow_border));
                            button1999.setBackground(getResources().getDrawable(R.drawable.background_wallet_border));
                        } else if (text.equalsIgnoreCase("1999")) {
                            button599.setBackground(getResources().getDrawable(R.drawable.background_wallet_border));
                            button999.setBackground(getResources().getDrawable(R.drawable.background_wallet_border));
                            button1999.setBackground(getResources().getDrawable(R.drawable.background_wallet_yellow_border));
                        } else {
                            button599.setBackground(getResources().getDrawable(R.drawable.background_wallet_border));
                            button999.setBackground(getResources().getDrawable(R.drawable.background_wallet_border));
                            button1999.setBackground(getResources().getDrawable(R.drawable.background_wallet_border));
                            FlurryEventLogger.event(ADD_JUGNOO_CASH_MANUALLY);
                        }
                    }
                } catch (Exception e) {
                }

            }
        });
		
		buttonMakePayment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
                    Utils.hideSoftKeyboard(homeActivity, editTextAmount);
                    String amountStr = editTextAmount.getText().toString().trim();
					if("".equalsIgnoreCase(amountStr)){
                        new DialogPopup().dialogBanner(homeActivity, "" + getResources().getString(R.string.amount_range));
					}
					else{
//						double amount = Double.parseDouble(editTextAmount.getText().toString().trim());
                        int amountNew = Integer.parseInt(editTextAmount.getText().toString().trim());
                        if (AppStatus.getInstance(homeActivity).isOnline(homeActivity)) {
                            if(amountNew < Config.getMinAmount() || amountNew > Config.getMaxAmount()) {

                                new DialogPopup().dialogBanner(homeActivity, ""+getResources().getString(R.string.amount_range));
                            } else {
//                                homeActivity.enterAmount = Double.toString(amount);
                                DecimalFormat decimalFormat = new DecimalFormat("#");
                                homeActivity.enterAmount = ""+decimalFormat.format(Double.parseDouble(editTextAmount.getText().toString()));

                                homeActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                                    .add(R.id.fragLayout, new AddJugnooCashFragment(), "AddJugnooCashFragment").addToBackStack("AddJugnooCashFragment")
                                    .hide(homeActivity.getSupportFragmentManager().findFragmentByTag(homeActivity.getSupportFragmentManager()
                                        .getBackStackEntryAt(homeActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                                    .commit();

                                FlurryEventLogger.event(ADDING_JUGNOO_CASH_FOR_FINAL_PAYMENT);
                            }
						}
						else{
                            new DialogPopup().alertPopup(homeActivity, "", Data.CHECK_INTERNET_MSG);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					
					editTextAmount.requestFocus();
					editTextAmount.setError("Please enter valid amount");
				}
			}
		});
		
		editTextAmount.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        buttonMakePayment.performClick();
                        break;

                    case EditorInfo.IME_ACTION_NEXT:
                        break;

                    default:
                }
                return true;
            }
        });
		
		
		try{
			try {
			    textViewCurrentBalanceValue.setText(getResources().getString(R.string.rupee)+" "+(int)Data.userData.jugnooBalance);
            }catch(Exception e) {
                textViewCurrentBalanceValue.setText(getResources().getString(R.string.rupee) + " "
                        + (int) Data.userData.jugnooBalance);
            }
		} catch(Exception e){
			e.printStackTrace();
		}

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

        homeActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
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
                        if (homeActivity.getCurrentFocus() != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) homeActivity.getSystemService(homeActivity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(homeActivity.getCurrentFocus().getWindowToken(), 0);
                        }
                    } catch (Exception e) {
                        //
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
     * Method used to hide soft keyboard
     */
//    public void hideKayboard() {
//        try {
//            if (homeActivity.getCurrentFocus() != null) {
//                InputMethodManager inputMethodManager = (InputMethodManager) homeActivity.getSystemService(homeActivity.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(homeActivity.getCurrentFocus().getWindowToken(), 0);
//            }
//        } catch (Exception e) {
//            //
//            e.printStackTrace();
//        }
//    }

    public void updateStatus(String status) {
        try{
            try {
                editTextAmount.setText("");

                textViewCurrentBalanceValue.setText(getResources().getString(R.string.rupee)+" "+Data.userData.jugnooBalance);
            }catch(Exception e) {
                textViewCurrentBalanceValue.setText(getResources().getString(R.string.rupee) + " "
                        + (int) Data.userData.jugnooBalance);
            }
            if("failure".equalsIgnoreCase(status)){
                new DialogPopup().dialogBanner(homeActivity, ""+getResources().getString(R.string.trans_failed));
            }
        } catch(Exception e){
            e.printStackTrace();
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



	
}
