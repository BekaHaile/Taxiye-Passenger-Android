package product.clicklabs.jugnoo.cardformat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Log;

/**
 * Created by kauserali on 05/05/14.
 */
public class CreditCardEditText extends EditText implements CreditCardTextWatcher.TextWatcherListener {

    private static final String SEPARATOR = "-";
    private static int DEFAULT_NO_MATCH_FOUND_DRAWABLE = R.drawable.ic_placeholder_card_payment;

    /**
     * Default minimum and maximum card length.
     */
    private static final int MINIMUM_CREDIT_CARD_LENGTH = 13;
    private static final int MAXIMUM_CREDIT_CARD_LENGTH = 19;

    /**
     * List of CreditCard objects containing the image to display
     * and the regex for pattern matching.
     */
    private List<CreditCard> mListOfCreditCardChecks;

    /**
     * This drawable is shown by default and when no match is found
     */
    private Drawable mNoMatchFoundDrawable;
    private CreditCard mCurrentCreditCardMatch;
    private CreditCartEditTextInterface mCreditCardEditTextInterface;
    private CreditCardTextWatcher mTextWatcher;

    private int mMinimumCreditCardLength, mMaximumCreditCardLength;
    private String mPreviousText;

    public interface CreditCartEditTextInterface {
        public List<CreditCard> mapOfRegexStringAndImageResourceForCreditCardEditText(CreditCardEditText creditCardEditText);
    }

    public CreditCardEditText(Context context) {
        super(context);
        init();
    }

    public CreditCardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CreditCardEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Drawable getNoMatchFoundDrawable() {
        return mNoMatchFoundDrawable;
    }

    public void setNoMatchFoundDrawable(Drawable noMatchFoundDrawable) {
        if (noMatchFoundDrawable != null) {
            mNoMatchFoundDrawable = noMatchFoundDrawable;
            mNoMatchFoundDrawable.setBounds(0, 0, mNoMatchFoundDrawable.getIntrinsicWidth(), mNoMatchFoundDrawable.getIntrinsicHeight());
        }
    }

    public void setCreditCardEditTextListener(CreditCartEditTextInterface creditCartEditTextInterface) {
        mCreditCardEditTextInterface = creditCartEditTextInterface;
        if (mCreditCardEditTextInterface != null) {
            mListOfCreditCardChecks = mCreditCardEditTextInterface.mapOfRegexStringAndImageResourceForCreditCardEditText(this);
        }
    }

    public String getTypeOfSelectedCreditCard() {
        if (mCurrentCreditCardMatch != null) {
            return mCurrentCreditCardMatch.getType();
        }
        return null;
    }

    public int getMaximumCreditCardLength() {
        return mMaximumCreditCardLength;
    }

    public void setMaximumCreditCardLength(int maximumCreditCardLength) {
        mMaximumCreditCardLength = maximumCreditCardLength;
    }

    public int getMinimumCreditCardLength() {
        return mMinimumCreditCardLength;
    }

    public void setMinimumCreditCardLength(int minimumCreditCardLength) {
        mMinimumCreditCardLength = minimumCreditCardLength;
    }

    public String getCreditCardNumber() {
        String creditCardNumber = getText().toString().replace(SEPARATOR, "");
        if (creditCardNumber.length() >= mMinimumCreditCardLength && creditCardNumber.length() <= mMaximumCreditCardLength) {
            return creditCardNumber;
        }
        return null;
    }

    @Override
    public void onTextChanged(EditText view, String text) {
        matchRegexPatternsWithText(text.replace(SEPARATOR, ""));

        String difference = StringUtil.difference(text, mPreviousText);
        if (!difference.equals(SEPARATOR)) {
            addSeparatorToText();
        }
        mPreviousText = text;
    }

    public static class CreditCard {
        private String mRegexPattern;
        private Drawable mDrawable;
        private String mType;

        public CreditCard(String regexPattern, Drawable drawable, String type) {
            if (regexPattern == null || drawable == null || type == null) {
                throw new IllegalArgumentException();
            }
            mRegexPattern = regexPattern;
            mDrawable = drawable;
            mType = type;
        }

        public String getRegexPattern() {
            return mRegexPattern;
        }

        public Drawable getDrawable() {
            return mDrawable;
        }

        public String getType() {
            return mType;
        }
    }

    private void init() {
        mMinimumCreditCardLength = MINIMUM_CREDIT_CARD_LENGTH;
        mMaximumCreditCardLength = MAXIMUM_CREDIT_CARD_LENGTH;
        mNoMatchFoundDrawable = getResources().getDrawable(DEFAULT_NO_MATCH_FOUND_DRAWABLE);
        if (mNoMatchFoundDrawable != null) {
            mNoMatchFoundDrawable.setBounds(0, 0, mNoMatchFoundDrawable.getIntrinsicWidth()*6/10, mNoMatchFoundDrawable.getIntrinsicHeight() * 6/10);


        }
        setPadding(20, 2, 30, 0);
        setInputType(InputType.TYPE_CLASS_NUMBER);

        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], mNoMatchFoundDrawable, getCompoundDrawables()[3]);
        if (mCreditCardEditTextInterface != null) {
            mListOfCreditCardChecks = mCreditCardEditTextInterface.mapOfRegexStringAndImageResourceForCreditCardEditText(this);
        }
        mTextWatcher = new CreditCardTextWatcher(this, this);
        addTextChangedListener(mTextWatcher);
        setCreditCardEditTextListener(new CreditCardPatterns(getContext()));
    }

    private void addSeparatorToText() {
        String text = getText().toString();
        text = text.replace(SEPARATOR, "");
        if (text.length() >= 16) {
            return;
        }
        int interval = 4;
        char separator = SEPARATOR.charAt(0);

        StringBuilder stringBuilder = new StringBuilder(text);
        for (int i = 0; i < text.length() / interval; i++) {
            stringBuilder.insert(((i + 1) * interval) + i, separator);
        }
        removeTextChangedListener(mTextWatcher);
        setText(stringBuilder.toString());
        setSelection(getText().length());
        addTextChangedListener(mTextWatcher);
    }

    private void matchRegexPatternsWithText(String text) {
        if (mListOfCreditCardChecks != null && mListOfCreditCardChecks.size() > 0) {
            Drawable drawable = null;
            for (CreditCard creditCard : mListOfCreditCardChecks) {
                String regex = creditCard.getRegexPattern();
                if (text.matches(regex)) {
                    mCurrentCreditCardMatch = creditCard;
                    drawable = creditCard.getDrawable();
                    break;
                }
            }
            showRightDrawable(drawable);
        }
    }

    private void showRightDrawable(Drawable drawable) {
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2, drawable.getIntrinsicHeight() / 2);
            setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], drawable, getCompoundDrawables()[3]);
        } else {
            mCurrentCreditCardMatch = null;
            setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], mNoMatchFoundDrawable, getCompoundDrawables()[3]);
        }
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new ZanyInputConnection(super.onCreateInputConnection(outAttrs),
                true);
    }


    private class ZanyInputConnection extends InputConnectionWrapper {

        public ZanyInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {

                Log.d("present text", "" + getText().toString());
                /*if(getText().toString().length() == 5){
                    setText("");
                    setText("123");
                }*/


                // Un-comment if you wish to cancel the backspace:
                // return false;
            }
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
            if (beforeLength == 1 && afterLength == 0) {
                // backspace
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }


    }
}
