package product.clicklabs.jugnoo.widgets;

import android.app.Service;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Log;

/**
 * Created by shankar on 14/07/17.
 */

public class PinEditTextLayout implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher{
	private EditText mPinFirstDigitEditText;
	private EditText mPinSecondDigitEditText;
	private EditText mPinThirdDigitEditText;
	private EditText mPinForthDigitEditText;
//	private EditText mPinFifthDigitEditText;
	private EditText mPinHiddenEditText;
	private LinearLayout pinLayout;

	private ViewGroup rootView;
	private Context context;
	private Callback callback;

	public PinEditTextLayout(ViewGroup view, Callback callback){
		rootView = view;
		context = view.getContext();
		this.callback = callback;

		LinearLayout linearLayout = new MainLayout(context, null);
		rootView.addView(linearLayout);

		init(linearLayout);
		setPINListeners();
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	/**
	 * Hides soft keyboard.
	 *
	 * @param editText EditText which has focus
	 */
	public void hideSoftKeyboard(EditText editText) {
		if (editText == null)
			return;

		InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	/**
	 * Initialize EditText fields.
	 */
	private void init(View rootView) {
		mPinFirstDigitEditText = (EditText) rootView.findViewById(R.id.pin_first_edittext);
		mPinSecondDigitEditText = (EditText) rootView.findViewById(R.id.pin_second_edittext);
		mPinThirdDigitEditText = (EditText) rootView.findViewById(R.id.pin_third_edittext);
		mPinForthDigitEditText = (EditText) rootView.findViewById(R.id.pin_forth_edittext);
//		mPinFifthDigitEditText = (EditText) rootView.findViewById(R.id.pin_fifth_edittext);
		mPinHiddenEditText = (EditText) rootView.findViewById(R.id.pin_hidden_edittext);
		pinLayout = (LinearLayout) rootView.findViewById(R.id.pin_layout);
		pinLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setFocus(mPinHiddenEditText);
				showSoftKeyboard(mPinHiddenEditText);
			}
		});
		mPinHiddenEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				callback.onOTPComplete(mPinHiddenEditText.getText().toString(), mPinHiddenEditText);
				return false;
			}
		});
	}

	public void tapOnEditText(){
		pinLayout.performClick();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		final int id = v.getId();
		switch (id) {
			case R.id.pin_first_edittext:
				if (hasFocus) {
					setFocus(mPinHiddenEditText);
					showSoftKeyboard(mPinHiddenEditText);
				}
				break;

			case R.id.pin_second_edittext:
				if (hasFocus) {
					setFocus(mPinHiddenEditText);
					showSoftKeyboard(mPinHiddenEditText);
				}
				break;

			case R.id.pin_third_edittext:
				if (hasFocus) {
					setFocus(mPinHiddenEditText);
					showSoftKeyboard(mPinHiddenEditText);
				}
				break;

			case R.id.pin_forth_edittext:
				if (hasFocus) {
					setFocus(mPinHiddenEditText);
					showSoftKeyboard(mPinHiddenEditText);
				}
				break;

//			case R.id.pin_fifth_edittext:
//				if (hasFocus) {
//					setFocus(mPinHiddenEditText);
//					showSoftKeyboard(mPinHiddenEditText);
//				}
//				break;
			default:
				break;
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			final int id = v.getId();
			switch (id) {
				case R.id.pin_hidden_edittext:
					if (keyCode == KeyEvent.KEYCODE_DEL) {
//						if (mPinHiddenEditText.getText().length() == 5)
//							mPinFifthDigitEditText.setText("");
//						else
						if (mPinHiddenEditText.getText().length() == 4)
							mPinForthDigitEditText.setText("");
						else if (mPinHiddenEditText.getText().length() == 3)
							mPinThirdDigitEditText.setText("");
						else if (mPinHiddenEditText.getText().length() == 2)
							mPinSecondDigitEditText.setText("");
						else if (mPinHiddenEditText.getText().length() == 1)
							mPinFirstDigitEditText.setText("");

						if (mPinHiddenEditText.length() > 0) {
							mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));
							mPinHiddenEditText.post(new Runnable() {
								@Override
								public void run() {
									mPinHiddenEditText.setSelection(mPinHiddenEditText.getText().toString().length());
								}
							});
						}

						return true;
					}

					break;

				default:
					return false;
			}
		}

		return false;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		setDefaultPinBackground(mPinFirstDigitEditText);
		setDefaultPinBackground(mPinSecondDigitEditText);
		setDefaultPinBackground(mPinThirdDigitEditText);
		setDefaultPinBackground(mPinForthDigitEditText);
//		setDefaultPinBackground(mPinFifthDigitEditText);

		if (s.length() == 0) {
			setFocusedPinBackground(mPinFirstDigitEditText);
			mPinFirstDigitEditText.setText("");
		} else if (s.length() == 1) {
			setFocusedPinBackground(mPinSecondDigitEditText);
			mPinFirstDigitEditText.setText(s.charAt(0) + "");
			mPinSecondDigitEditText.setText("");
			mPinThirdDigitEditText.setText("");
			mPinForthDigitEditText.setText("");
//			mPinFifthDigitEditText.setText("");
		} else if (s.length() == 2) {
			setFocusedPinBackground(mPinThirdDigitEditText);
			mPinSecondDigitEditText.setText(s.charAt(1) + "");
			mPinThirdDigitEditText.setText("");
			mPinForthDigitEditText.setText("");
//			mPinFifthDigitEditText.setText("");
		} else if (s.length() == 3) {
			setFocusedPinBackground(mPinForthDigitEditText);
			mPinThirdDigitEditText.setText(s.charAt(2) + "");
			mPinForthDigitEditText.setText("");
//			mPinFifthDigitEditText.setText("");
		} else if (s.length() == 4) {
			setDefaultPinBackground(mPinForthDigitEditText);
			mPinForthDigitEditText.setText(s.charAt(3) + "");
//			mPinFifthDigitEditText.setText("");
			callback.onOTPComplete(s.toString(), mPinHiddenEditText);
		}
//		else if (s.length() == 5) {
//			setDefaultPinBackground(mPinFifthDigitEditText);
//			mPinFifthDigitEditText.setText(s.charAt(4) + "");
//
////			hideSoftKeyboard(mPinFifthDigitEditText);
//			callback.onOTPComplete(s.toString(), mPinHiddenEditText);
//		}
	}

	/**
	 * Sets default PIN background.
	 *
	 * @param editText edit text to change
	 */
	private void setDefaultPinBackground(EditText editText) {
		setViewBackground(editText, ContextCompat.getDrawable(context, R.drawable.bg_white_layer_shadow));
	}

	/**
	 * Sets focus on a specific EditText field.
	 *
	 * @param editText EditText to set focus on
	 */
	public static void setFocus(EditText editText) {
		if (editText == null)
			return;

		editText.setFocusable(true);
		editText.setFocusableInTouchMode(true);
		editText.requestFocus();
		editText.setSelection(editText.getText().length());
	}

	/**
	 * Sets focused PIN field background.
	 *
	 * @param editText edit text to change
	 */
	private void setFocusedPinBackground(EditText editText) {
		setViewBackground(editText, ContextCompat.getDrawable(context, R.drawable.bg_white_layer_shadow_mid_cursor));
	}

	/**
	 * Sets listeners for EditText fields.
	 */
	private void setPINListeners() {
		mPinHiddenEditText.addTextChangedListener(this);

		mPinFirstDigitEditText.setOnFocusChangeListener(this);
		mPinSecondDigitEditText.setOnFocusChangeListener(this);
		mPinThirdDigitEditText.setOnFocusChangeListener(this);
		mPinForthDigitEditText.setOnFocusChangeListener(this);
//		mPinFifthDigitEditText.setOnFocusChangeListener(this);

		mPinFirstDigitEditText.setOnKeyListener(this);
		mPinSecondDigitEditText.setOnKeyListener(this);
		mPinThirdDigitEditText.setOnKeyListener(this);
		mPinForthDigitEditText.setOnKeyListener(this);
//		mPinFifthDigitEditText.setOnKeyListener(this);
		mPinHiddenEditText.setOnKeyListener(this);
	}

	/**
	 * Sets background of the view.
	 * This method varies in implementation depending on Android SDK version.
	 *
	 * @param view       View to which set background
	 * @param background Background to set to view
	 */
	@SuppressWarnings("deprecation")
	public void setViewBackground(View view, Drawable background) {
		if (view == null || background == null)
			return;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackground(background);
		} else {
			view.setBackgroundDrawable(background);
		}
	}

	/**
	 * Shows soft keyboard.
	 *
	 * @param editText EditText which has focus
	 */
	public void showSoftKeyboard(EditText editText) {
		if (editText == null)
			return;

		InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, 0);
	}

	/**
	 * Custom LinearLayout with overridden onMeasure() method
	 * for handling software keyboard show and hide events.
	 */
	public class MainLayout extends LinearLayout {

		public MainLayout(Context context, AttributeSet attributeSet) {
			super(context, attributeSet);
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.layout_otp_edittexts, this);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			final int proposedHeight = MeasureSpec.getSize(heightMeasureSpec);
			final int actualHeight = getHeight();

			Log.d("TAG", "proposed: " + proposedHeight + ", actual: " + actualHeight);

			if (actualHeight >= proposedHeight) {
				// Keyboard is shown
				if (mPinHiddenEditText.length() == 0)
					setFocusedPinBackground(mPinFirstDigitEditText);
				else
					setDefaultPinBackground(mPinFirstDigitEditText);
			}

			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}


	public interface Callback{
		void onOTPComplete(String otp, EditText editText);
	}

	public void setOTPDirectly(String otp){
		mPinHiddenEditText.setText(String.valueOf(otp.charAt(0)));
		mPinHiddenEditText.append(String.valueOf(otp.charAt(1)));
		mPinHiddenEditText.append(String.valueOf(otp.charAt(2)));
		mPinHiddenEditText.append(String.valueOf(otp.charAt(3)));
	}
}