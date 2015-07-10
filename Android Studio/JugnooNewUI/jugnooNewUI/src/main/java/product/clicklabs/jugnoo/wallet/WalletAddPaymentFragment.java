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
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyBoardStateHandler;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import rmn.androidscreenlibrary.ASSL;

public class WalletAddPaymentFragment extends Fragment {
	
	LinearLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;
	
	TextView textViewHelp;//, rupee;
	EditText editTextAmount;
	Button button599, button999, button1999, buttonMakePayment;
	TextView textViewCurrentBalance, textViewCurrentBalanceValue;

    ScrollView scrollView;
    LinearLayout linearLayoutMain;
    TextView textViewScroll;

    View rootView;
    public PaymentActivity paymentActivity;
    boolean scrolled = false;
	
	//public static AddPaymentPath addPaymentPath = AddPaymentPath.FROM_WALLET;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(paymentActivity, Config.getFlurryKey());
        FlurryAgent.onStartSession(paymentActivity, Config.getFlurryKey());
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(paymentActivity);
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.checkForAccessTokenChange(paymentActivity);
        editTextAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cash_balance, container, false);

        paymentActivity = (PaymentActivity) getActivity();

        scrolled = false;
 
		
		relative = (LinearLayout) rootView.findViewById(R.id.relative);
		new ASSL(paymentActivity, relative, 1134, 720, false);

        setupUI(rootView.findViewById(R.id.relative));

		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(paymentActivity), Typeface.BOLD);
		
		textViewHelp = (TextView) rootView.findViewById(R.id.textViewHelp); textViewHelp.setTypeface(Fonts.latoLight(paymentActivity));
		
		editTextAmount = (EditText) rootView.findViewById(R.id.editTextAmount); editTextAmount.setTypeface(Fonts.latoRegular(paymentActivity));
        editTextAmount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

//        rupee = (TextView) rootView.findViewById(R.id.rupee);
//        rupee.setTypeface(Fonts.latoLight(paymentActivity));
//        rupee.setVisibility(View.GONE);
		
		button599 = (Button) rootView.findViewById(R.id.button100); button599.setTypeface(Fonts.latoRegular(paymentActivity));
		button999 = (Button) rootView.findViewById(R.id.button200); button999.setTypeface(Fonts.latoRegular(paymentActivity));
		button1999 = (Button) rootView.findViewById(R.id.button500); button1999.setTypeface(Fonts.latoRegular(paymentActivity));
		buttonMakePayment = (Button) rootView.findViewById(R.id.buttonMakePayment); buttonMakePayment.setTypeface(Fonts.latoRegular(paymentActivity));
		
		textViewCurrentBalance = (TextView) rootView.findViewById(R.id.textViewCurrentBalance); textViewCurrentBalance.setTypeface(Fonts.latoRegular(paymentActivity));
		textViewCurrentBalanceValue = (TextView) rootView.findViewById(R.id.textViewCurrentBalanceValue); textViewCurrentBalanceValue.setTypeface(Fonts.latoRegular(paymentActivity));

        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);
        textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);



		imageViewBack.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		button599.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                editTextAmount.setText("599");
                editTextAmount.setSelection(editTextAmount.getText().length());
            }
        });
		
		button999.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextAmount.setText("999");
                editTextAmount.setSelection(editTextAmount.getText().length());
            }
        });

		button1999.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextAmount.setText("1999");
                editTextAmount.setSelection(editTextAmount.getText().length());
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
try{
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
        }
    }
            } catch(Exception e) {
            }

            }
        });
		
		buttonMakePayment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					String amountStr = editTextAmount.getText().toString().trim();
					if("".equalsIgnoreCase(amountStr)){
                        new DialogPopup().dialogBanner(paymentActivity, "" + getResources().getString(R.string.amount_range));
					}
					else{
//						double amount = Double.parseDouble(editTextAmount.getText().toString().trim());
                        int amountNew = Integer.parseInt(editTextAmount.getText().toString().trim());
                        if (AppStatus.getInstance(paymentActivity).isOnline(paymentActivity)) {
                            if(amountNew<Data.MIN_AMOUNT || amountNew>Data.MAX_AMOUNT) {
                                new DialogPopup().dialogBanner(paymentActivity, ""+getResources().getString(R.string.amount_range));
                            } else {
//                                paymentActivity.enterAmount = Double.toString(amount);
                                DecimalFormat decimalFormat = new DecimalFormat("#");
                                paymentActivity.enterAmount = ""+decimalFormat.format(Double.parseDouble(editTextAmount.getText().toString()));

                                paymentActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
                                    .add(R.id.fragLayout, new AddJugnooCashFragment(), "AddJugnooCashFragment").addToBackStack("AddJugnooCashFragment")
                                    .hide(paymentActivity.getSupportFragmentManager().findFragmentByTag(paymentActivity.getSupportFragmentManager()
                                        .getBackStackEntryAt(paymentActivity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
                                    .commit();

                            }
						}
						else{
                            new DialogPopup().alertPopup(paymentActivity, "", Data.CHECK_INTERNET_MSG);
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
						//buttonMakePayment.performClick();
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
                if(!scrolled) {
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
                new DialogPopup().dialogBanner(paymentActivity, ""+getResources().getString(R.string.trans_failed));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void performBackPressed() {
        getActivity().getSupportFragmentManager().popBackStack ("WalletAddPaymentFragment", getFragmentManager().POP_BACK_STACK_INCLUSIVE);
//        startActivity(new Intent(paymentActivity, WalletActivity.class));
//        finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
	

	
	@Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}



	
}
