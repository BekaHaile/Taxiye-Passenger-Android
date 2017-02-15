package com.sabkuchfresh.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by Parminder Singh on 30/01/2017.
 */

/**
 * regular rating bar. it wraps the stars making its size fit the parent
 */
public class RatingBarMenuFeedback extends LinearLayout {


    private static final int LOW_RATING_RED = Color.parseColor("#FB9758");
    private static final int MEDIUM_RATING_YELLOW = Color.parseColor("#FFD365");
    private static final int GOOD_RATING_GREEN = Color.parseColor("#8DCF61");

    public IRatingBarCallbacks getOnScoreChanged() {
        return onScoreChanged;
    }

    public void setOnScoreChanged(IRatingBarCallbacks onScoreChanged) {
        this.onScoreChanged = onScoreChanged;
    }

    public interface IRatingBarCallbacks {
        void scoreChanged(float score);
    }


    private int mMaxStars = 5;
    private float mCurrentScore = 0f;
    private int mStarOnResource = R.drawable.ic_menu_feedback_star_on;
    private int mStarOffResource = R.drawable.ic_menu_feedback_star_off;
    private int mStarHalfResource = R.drawable.ic_menu_feedback_star_off;
    private TextView[] mStarsViews;
    private float mStarPadding;
    private IRatingBarCallbacks onScoreChanged;
    private int mLastStarId;
    private boolean mOnlyForDisplay;
    private double mLastX;
    private boolean mHalfStars = true;

    public RatingBarMenuFeedback(Context context) {
        super(context);

        init();
    }


    public TextView[] getmStarsViews() {
        return mStarsViews;
    }

    public float getScore() {
        return mCurrentScore;
    }

    public void setScore(float score) {
        score = Math.round(score * 2) / 2.0f;
        if (!mHalfStars)
            score = Math.round(score);
        mCurrentScore = score;
        refreshStars();
    }


    public void setStarResource(int fullIcon, int normalIcon) {
        this.mStarOnResource = fullIcon;
        this.mStarOffResource = normalIcon;
        this.mStarHalfResource = normalIcon;

    }

    public void setScrollToSelect(boolean enabled) {
        mOnlyForDisplay = !enabled;
    }

    public RatingBarMenuFeedback(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeAttributes(attrs, context);
        init();
    }

    private void initializeAttributes(AttributeSet attrs, Context context) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomRatingBar);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.CustomRatingBar_maxStars)
                mMaxStars = a.getInt(attr, 5);
            else if (attr == R.styleable.CustomRatingBar_stars)
                mCurrentScore = a.getFloat(attr, 2.5f);
            else if (attr == R.styleable.CustomRatingBar_starHalf)
                mStarHalfResource = a.getResourceId(attr, android.R.drawable.star_on);
            else if (attr == R.styleable.CustomRatingBar_starOn)
                mStarOnResource = a.getResourceId(attr, android.R.drawable.star_on);
            else if (attr == R.styleable.CustomRatingBar_starOff)
                mStarOffResource = a.getResourceId(attr, android.R.drawable.star_off);
            else if (attr == R.styleable.CustomRatingBar_starPadding)
                mStarPadding = a.getDimension(attr, 0);
            else if (attr == R.styleable.CustomRatingBar_onlyForDisplay)
                mOnlyForDisplay = a.getBoolean(attr, false);
            else if (attr == R.styleable.CustomRatingBar_halfStars)
                mHalfStars = a.getBoolean(attr, true);
        }
        a.recycle();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RatingBarMenuFeedback(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeAttributes(attrs, context);
        init();
    }

    void init() {
        mStarsViews = new TextView[mMaxStars];
        for (int i = 0; i < mMaxStars; i++) {
            TextView v = createStar();
            addView(v);
            mStarsViews[i] = v;
        }
        refreshStars();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    /**
     * hardcore math over here
     *
     * @param x
     * @return
     */
    private float getScoreForPosition(float x) {
        if (mHalfStars)
            return (float) Math.round(((x / ((float) getWidth() / (mMaxStars * 3f))) / 3f) * 2f) / 2;
        float value = (float) Math.round((x / ((float) getWidth() / (mMaxStars))));
        value = value <= 0 ? 1 : value;
        value = value > mMaxStars ? mMaxStars : value;
        return value;

    }

    private int getImageForScore(float score) {
        if (score > 0)
            return Math.round(score) - 1;
        else return -1;
    }

    private void refreshStars() {
        boolean flagHalf = (mCurrentScore != 0 && (mCurrentScore % 0.5 == 0)) && mHalfStars;
        for (int i = 1; i <= mMaxStars; i++) {

            if (i <= mCurrentScore) {
                mStarsViews[i - 1].setCompoundDrawablesWithIntrinsicBounds(0, mStarOnResource, 0, 0);

                switch (i) {
                    case 1:
                        mStarsViews[i - 1].getCompoundDrawables()[1].setColorFilter(LOW_RATING_RED, PorterDuff.Mode.SRC_ATOP);
                        break;
                    case 2:
                        mStarsViews[i - 1].getCompoundDrawables()[1].setColorFilter(MEDIUM_RATING_YELLOW, PorterDuff.Mode.SRC_ATOP);
                        break;
                    default:
                        mStarsViews[i - 1].getCompoundDrawables()[1].setColorFilter(GOOD_RATING_GREEN, PorterDuff.Mode.SRC_ATOP);
                        break;
                }


            } else {
                if (flagHalf && i - 0.5 <= mCurrentScore)
                    mStarsViews[i - 1].setCompoundDrawablesWithIntrinsicBounds(0, mStarHalfResource, 0, 0);
                else
                    mStarsViews[i - 1].setCompoundDrawablesWithIntrinsicBounds(0, mStarOffResource, 0, 0);


                mStarsViews[i - 1].getCompoundDrawables()[1].setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            }

            if (i == mCurrentScore) {
                switch (i) {
                    case 1:
                        mStarsViews[i - 1].setText("Terrible");
                        break;
                    case 2:
                        mStarsViews[i - 1].setText("Bad");
                        break;
                    case 3:
                        mStarsViews[i - 1].setText("Okay");
                        break;
                    case 4:
                        mStarsViews[i - 1].setText("Good");
                        break;
                    case 5:
                        mStarsViews[i - 1].setText("Great");
                        break;
                }
            } else {
                mStarsViews[i - 1].setText(null);
            }

        }
    }

    private TextView createStar() {
        TextView v = new TextView(getContext());
        v.setCompoundDrawablePadding((int) (ASSL.Yscale() * 20.0f));
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.rightMargin = (int) (ASSL.Xscale() * 15.0f);
        params.bottomMargin = (int) (ASSL.Yscale() * 25.0f);
        params.topMargin = (int) (ASSL.Yscale() * 25.0f);
        params.leftMargin = (int) (ASSL.Xscale() * 15.0f);
        v.setGravity(Gravity.CENTER);
        v.setCompoundDrawablePadding((int) (ASSL.Yscale() * 12.0f));
        v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f);
        v.setTypeface(Fonts.mavenMedium(getContext()), Typeface.BOLD);
        v.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
        v.setLayoutParams(params);
        v.setCompoundDrawablesWithIntrinsicBounds(0, mStarOffResource, 0, 0);
        return v;
    }

    private TextView getImageView(int position) {
        try {
            return mStarsViews[position];
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (mOnlyForDisplay)
            return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                animateStarRelease(getImageView(mLastStarId));
                mLastStarId = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - mLastX) > 80)
                    requestDisallowInterceptTouchEvent(true);
                float lastscore = mCurrentScore;
                mCurrentScore = getScoreForPosition(event.getX());
                if (lastscore != mCurrentScore) {
                    animateStarRelease(getImageView(mLastStarId));
                    animateStarPressed(getImageView(getImageForScore(mCurrentScore)));
                    mLastStarId = getImageForScore(mCurrentScore);
                    refreshStars();
                    if (onScoreChanged != null)
                        onScoreChanged.scoreChanged(mCurrentScore);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                lastscore = mCurrentScore;
                mCurrentScore = getScoreForPosition(event.getX());
                animateStarPressed(getImageView(getImageForScore(mCurrentScore)));
                mLastStarId = getImageForScore(mCurrentScore);
                if (lastscore != mCurrentScore) {
                    refreshStars();
                    if (onScoreChanged != null)
                        onScoreChanged.scoreChanged(mCurrentScore);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                animateStarRelease(getImageView(getImageForScore(mCurrentScore)));
                break;
        }
        return true;
    }

    private void animateStarPressed(TextView star) {
        if (star != null)
//            star.setTextSize(TypedValue.COMPLEX_UNIT_PX, 28);
            ViewCompat.animate(star).scaleX(1.2f).scaleY(1.2f).setDuration(100).start();
    }

    private void animateStarRelease(TextView star) {
        if (star != null) {
//            star.setTextSize(TypedValue.COMPLEX_UNIT_PX, 26);
            ViewCompat.animate(star).scaleX(1f).scaleY(1f).setDuration(100).start();
        }
    }

    public boolean isHalfStars() {
        return mHalfStars;
    }

    public void setHalfStars(boolean halfStars) {
        mHalfStars = halfStars;
    }
}