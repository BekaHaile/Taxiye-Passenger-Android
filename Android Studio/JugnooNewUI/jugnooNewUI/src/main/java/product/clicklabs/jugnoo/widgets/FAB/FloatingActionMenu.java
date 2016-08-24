package product.clicklabs.jugnoo.widgets.FAB;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Iterator;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by ankit on 8/19/16.
 */
public class FloatingActionMenu extends ViewGroup {
    private static final int ANIMATION_DURATION = 300;
    private static final float CLOSED_PLUS_ROTATION = 0.0F;
    private static final float OPENED_PLUS_ROTATION_LEFT = -135.0F;
    private static final float OPENED_PLUS_ROTATION_RIGHT = 135.0F;
    private static final int OPEN_UP = 0;
    private static final int OPEN_DOWN = 1;
    private static final int LABELS_POSITION_LEFT = 0;
    private static final int LABELS_POSITION_RIGHT = 1;
    private AnimatorSet mOpenAnimatorSet;
    private AnimatorSet mCloseAnimatorSet;
    private AnimatorSet mIconToggleSet;
    private int mButtonSpacing;
    private FloatingActionButton mMenuButton;
    private int mMaxButtonWidth;
    private int mLabelsMargin;
    private int mLabelsVerticalOffset;
    private int mButtonsCount;
    private boolean mMenuOpened;
    private boolean mIsMenuOpening;
    private Handler mUiHandler;
    private int mLabelsShowAnimation;
    private int mLabelsHideAnimation;
    private int mLabelsPaddingTop;
    private int mLabelsPaddingRight;
    private int mLabelsPaddingBottom;
    private int mLabelsPaddingLeft;
    private ColorStateList mLabelsTextColor;
    private float mLabelsTextSize;
    private int mLabelsCornerRadius;
    private boolean mLabelsShowShadow;
    private int mLabelsColorNormal;
    private int mLabelsColorPressed;
    private int mLabelsColorRipple;
    private boolean mMenuShowShadow;
    private int mMenuShadowColor;
    private float mMenuShadowRadius;
    private float mMenuShadowXOffset;
    private float mMenuShadowYOffset;
    private int mMenuColorNormal;
    private int mMenuColorPressed;
    private int mMenuColorRipple;
    private Drawable mIcon;
    private int mAnimationDelayPerItem;
    private Interpolator mOpenInterpolator;
    private Interpolator mCloseInterpolator;
    private boolean mIsAnimated;
    private boolean mLabelsSingleLine;
    private int mLabelsEllipsize;
    private int mLabelsMaxLines;
    private int mMenuFabSize;
    private int mLabelsStyle;
    private Typeface mCustomTypefaceFromFont;
    private boolean mIconAnimated;
    private ImageView mImageToggle;
    private Animation mMenuButtonShowAnimation;
    private Animation mMenuButtonHideAnimation;
    private Animation mImageToggleShowAnimation;
    private Animation mImageToggleHideAnimation;
    private boolean mIsMenuButtonAnimationRunning;
    private boolean mIsSetClosedOnTouchOutside;
    private int mOpenDirection;
    private FloatingActionMenu.OnMenuToggleListener mToggleListener;
    private ValueAnimator mShowBackgroundAnimator;
    private ValueAnimator mHideBackgroundAnimator;
    private int mBackgroundColor;
    private int mLabelsPosition;
    private Context mLabelsContext;
    private String mMenuLabelText;
    private boolean mUsingMenuLabel;

    public OnMenuToggleListener getmToggleListener() {
        return mToggleListener;
    }

    public FloatingActionMenu(Context context) {
        this(context, (AttributeSet)null);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mOpenAnimatorSet = new AnimatorSet();
        this.mCloseAnimatorSet = new AnimatorSet();
        this.mButtonSpacing = Utils.dpToPx(this.getContext(), 0.0F);
        this.mLabelsMargin = Utils.dpToPx(this.getContext(), 0.0F);
        this.mLabelsVerticalOffset = Utils.dpToPx(this.getContext(), 0.0F);
        this.mUiHandler = new Handler();
        this.mLabelsPaddingTop = Utils.dpToPx(this.getContext(), 4.0F);
        this.mLabelsPaddingRight = Utils.dpToPx(this.getContext(), 8.0F);
        this.mLabelsPaddingBottom = Utils.dpToPx(this.getContext(), 4.0F);
        this.mLabelsPaddingLeft = Utils.dpToPx(this.getContext(), 8.0F);
        this.mLabelsCornerRadius = Utils.dpToPx(this.getContext(), 3.0F);
        this.mMenuShadowRadius = 4.0F;
        this.mMenuShadowXOffset = 1.0F;
        this.mMenuShadowYOffset = 3.0F;
        this.mIsAnimated = true;
        this.mIconAnimated = true;
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionMenu, 0, 0);
        this.mButtonSpacing = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_buttonSpacing, this.mButtonSpacing);
        this.mLabelsMargin = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_labels_margin, this.mLabelsMargin);
        this.mLabelsPosition = attr.getInt(R.styleable.FloatingActionMenu_menu_labels_position, 0);
        this.mLabelsShowAnimation = attr.getResourceId(R.styleable.FloatingActionMenu_menu_labels_showAnimation, this.mLabelsPosition == 0? R.anim.fab_slide_in_from_right: R.anim.fab_slide_in_from_left);
        this.mLabelsHideAnimation = attr.getResourceId(R.styleable.FloatingActionMenu_menu_labels_hideAnimation, this.mLabelsPosition == 0? R.anim.fab_slide_out_to_right: R.anim.fab_slide_out_to_left);
        this.mLabelsPaddingTop = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_labels_paddingTop, this.mLabelsPaddingTop);
        this.mLabelsPaddingRight = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_labels_paddingRight, this.mLabelsPaddingRight);
        this.mLabelsPaddingBottom = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_labels_paddingBottom, this.mLabelsPaddingBottom);
        this.mLabelsPaddingLeft = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_labels_paddingLeft, this.mLabelsPaddingLeft);
        this.mLabelsTextColor = attr.getColorStateList(R.styleable.FloatingActionMenu_menu_labels_textColor);
        if(this.mLabelsTextColor == null) {
            this.mLabelsTextColor = ColorStateList.valueOf(-1);
        }

        this.mLabelsTextSize = attr.getDimension(R.styleable.FloatingActionMenu_menu_labels_textSize, this.getResources().getDimension(R.dimen.labels_text_size));
        this.mLabelsCornerRadius = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_labels_cornerRadius, this.mLabelsCornerRadius);
        this.mLabelsShowShadow = attr.getBoolean(R.styleable.FloatingActionMenu_menu_labels_showShadow, true);
        this.mLabelsColorNormal = attr.getColor(R.styleable.FloatingActionMenu_menu_labels_colorNormal, -13421773);
        this.mLabelsColorPressed = attr.getColor(R.styleable.FloatingActionMenu_menu_labels_colorPressed, -12303292);
        this.mLabelsColorRipple = attr.getColor(R.styleable.FloatingActionMenu_menu_labels_colorRipple, 1728053247);
        this.mMenuShowShadow = attr.getBoolean(R.styleable.FloatingActionMenu_menu_showShadow, true);
        this.mMenuShadowColor = attr.getColor(R.styleable.FloatingActionMenu_menu_shadowColor, 1711276032);
        this.mMenuShadowRadius = attr.getDimension(R.styleable.FloatingActionMenu_menu_shadowRadius, this.mMenuShadowRadius);
        this.mMenuShadowXOffset = attr.getDimension(R.styleable.FloatingActionMenu_menu_shadowXOffset, this.mMenuShadowXOffset);
        this.mMenuShadowYOffset = attr.getDimension(R.styleable.FloatingActionMenu_menu_shadowYOffset, this.mMenuShadowYOffset);
        this.mMenuColorNormal = attr.getColor(R.styleable.FloatingActionMenu_menu_colorNormal, -2473162);
        this.mMenuColorPressed = attr.getColor(R.styleable.FloatingActionMenu_menu_colorPressed, -1617853);
        this.mMenuColorRipple = attr.getColor(R.styleable.FloatingActionMenu_menu_colorRipple, -1711276033);
        this.mAnimationDelayPerItem = attr.getInt(R.styleable.FloatingActionMenu_menu_animationDelayPerItem, 50);
        this.mIcon = attr.getDrawable(R.styleable.FloatingActionMenu_menu_icon);
        if(this.mIcon == null) {
            this.mIcon = this.getResources().getDrawable(R.drawable.fab_add);
        }

        this.mLabelsSingleLine = attr.getBoolean(R.styleable.FloatingActionMenu_menu_labels_singleLine, false);
        this.mLabelsEllipsize = attr.getInt(R.styleable.FloatingActionMenu_menu_labels_ellipsize, 0);
        this.mLabelsMaxLines = attr.getInt(R.styleable.FloatingActionMenu_menu_labels_maxLines, -1);
        this.mMenuFabSize = attr.getInt(R.styleable.FloatingActionMenu_menu_fab_size, 0);
        this.mLabelsStyle = attr.getResourceId(R.styleable.FloatingActionMenu_menu_labels_style, 0);
        String customFont = attr.getString(R.styleable.FloatingActionMenu_menu_labels_customFont);

        try {
            if(!TextUtils.isEmpty(customFont)) {
                this.mCustomTypefaceFromFont = Typeface.createFromAsset(this.getContext().getAssets(), customFont);
            }
        } catch (RuntimeException var6) {
            throw new IllegalArgumentException("Unable to load specified custom font: " + customFont, var6);
        }

        this.mOpenDirection = attr.getInt(R.styleable.FloatingActionMenu_menu_openDirection, 0);
        this.mBackgroundColor = attr.getColor(R.styleable.FloatingActionMenu_menu_backgroundColor, 0);
        if(attr.hasValue(R.styleable.FloatingActionMenu_menu_fab_label)) {
            this.mUsingMenuLabel = true;
            this.mMenuLabelText = attr.getString(R.styleable.FloatingActionMenu_menu_fab_label);
        }

        if(attr.hasValue(R.styleable.FloatingActionMenu_menu_labels_padding)) {
            int padding = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_menu_labels_padding, 0);
            this.initPadding(padding);
        }

        this.mOpenInterpolator = new OvershootInterpolator();
        this.mCloseInterpolator = new AnticipateInterpolator();
        this.mLabelsContext = new ContextThemeWrapper(this.getContext(), this.mLabelsStyle);
        this.initBackgroundDimAnimation();
        this.createMenuButton();
        this.initMenuButtonAnimations(attr);
        attr.recycle();
    }

    private void initMenuButtonAnimations(TypedArray attr) {
        int showResId = attr.getResourceId(R.styleable.FloatingActionMenu_menu_fab_show_animation, R.anim.fab_scale_up);
        this.setMenuButtonShowAnimation(AnimationUtils.loadAnimation(this.getContext(), showResId));
        this.mImageToggleShowAnimation = AnimationUtils.loadAnimation(this.getContext(), showResId);
        mImageToggleShowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("start animation", "start Animation");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //mImageToggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_fab_down));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        int hideResId = attr.getResourceId(R.styleable.FloatingActionMenu_menu_fab_hide_animation, R.anim.fab_scale_down);
        this.setMenuButtonHideAnimation(AnimationUtils.loadAnimation(this.getContext(), hideResId));
        this.mImageToggleHideAnimation = AnimationUtils.loadAnimation(this.getContext(), hideResId);
    }

    private void initBackgroundDimAnimation() {
        int maxAlpha = Color.alpha(this.mBackgroundColor);
        final int red = Color.red(this.mBackgroundColor);
        final int green = Color.green(this.mBackgroundColor);
        final int blue = Color.blue(this.mBackgroundColor);
        this.mShowBackgroundAnimator = ValueAnimator.ofInt(new int[]{0, maxAlpha});
        this.mShowBackgroundAnimator.setDuration(300L);
        this.mShowBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer alpha = (Integer)animation.getAnimatedValue();
                FloatingActionMenu.this.setBackgroundColor(Color.argb(alpha.intValue(), red, green, blue));
            }
        });
        this.mHideBackgroundAnimator = ValueAnimator.ofInt(new int[]{maxAlpha, 0});
        this.mHideBackgroundAnimator.setDuration(300L);
        this.mHideBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer alpha = (Integer)animation.getAnimatedValue();
                FloatingActionMenu.this.setBackgroundColor(Color.argb(alpha.intValue(), red, green, blue));
            }
        });
    }

    private boolean isBackgroundEnabled() {
        return this.mBackgroundColor != 0;
    }

    private void initPadding(int padding) {
        this.mLabelsPaddingTop = padding;
        this.mLabelsPaddingRight = padding;
        this.mLabelsPaddingBottom = padding;
        this.mLabelsPaddingLeft = padding;
    }

    private void createMenuButton() {
        this.mMenuButton = new FloatingActionButton(this.getContext());
        this.mMenuButton.mShowShadow = this.mMenuShowShadow;
        if(this.mMenuShowShadow) {
            this.mMenuButton.mShadowRadius = Utils.dpToPx(this.getContext(), this.mMenuShadowRadius);
            this.mMenuButton.mShadowXOffset = Utils.dpToPx(this.getContext(), this.mMenuShadowXOffset);
            this.mMenuButton.mShadowYOffset = Utils.dpToPx(this.getContext(), this.mMenuShadowYOffset);
        }

        this.mMenuButton.setColors(this.mMenuColorNormal, this.mMenuColorPressed, this.mMenuColorRipple);
        this.mMenuButton.mShadowColor = this.mMenuShadowColor;
        this.mMenuButton.mFabSize = this.mMenuFabSize;
        this.mMenuButton.updateBackground();
        this.mMenuButton.setLabelText(this.mMenuLabelText);
        this.mImageToggle = new ImageView(this.getContext());
        this.mImageToggle.setImageDrawable(this.mIcon);
        this.addView(this.mMenuButton, super.generateDefaultLayoutParams());
        this.addView(this.mImageToggle);
        this.createDefaultIconAnimation();
    }

    private void createDefaultIconAnimation() {
        float collapseAngle;
        float expandAngle;
        if(this.mOpenDirection == 0) {
            collapseAngle = this.mLabelsPosition == 0?-135.0F:135.0F;
            expandAngle = this.mLabelsPosition == 0?-135.0F:135.0F;
        } else {
            collapseAngle = this.mLabelsPosition == 0?135.0F:-135.0F;
            expandAngle = this.mLabelsPosition == 0?135.0F:-135.0F;
        }

        ObjectAnimator collapseAnimator = ObjectAnimator.ofFloat(this.mImageToggle, "rotation", new float[]{collapseAngle, 0.0F});
        ObjectAnimator expandAnimator = ObjectAnimator.ofFloat(this.mImageToggle, "rotation", new float[]{0.0F, expandAngle});
        this.mOpenAnimatorSet.play(expandAnimator);
        this.mCloseAnimatorSet.play(collapseAnimator);
        this.mOpenAnimatorSet.setInterpolator(this.mOpenInterpolator);
        this.mCloseAnimatorSet.setInterpolator(this.mCloseInterpolator);
        this.mOpenAnimatorSet.setDuration(300L);
        this.mCloseAnimatorSet.setDuration(300L);

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean width = false;
        int height = 0;
        this.mMaxButtonWidth = 0;
        int maxLabelWidth = 0;
        this.measureChildWithMargins(this.mImageToggle, widthMeasureSpec, 0, heightMeasureSpec, 0);

        int i;
        for(i = 0; i < this.mButtonsCount; ++i) {
            View usedWidth = this.getChildAt(i);
            if(usedWidth.getVisibility() != 8 && usedWidth != this.mImageToggle) {
                this.measureChildWithMargins(usedWidth, widthMeasureSpec, 0, heightMeasureSpec, 0);
                this.mMaxButtonWidth = Math.max(this.mMaxButtonWidth, usedWidth.getMeasuredWidth());
            }
        }

        for(i = 0; i < this.mButtonsCount; ++i) {
            byte var13 = 0;
            View child = this.getChildAt(i);
            if(child.getVisibility() != 8 && child != this.mImageToggle) {
                int var14 = var13 + child.getMeasuredWidth();
                height += child.getMeasuredHeight();
                Label label = (Label)child.getTag(R.id.fab_label);
                if(label != null) {
                    int labelOffset = (this.mMaxButtonWidth - child.getMeasuredWidth()) / (this.mUsingMenuLabel?1:2);
                    int labelUsedWidth = child.getMeasuredWidth() + label.calculateShadowWidth() + this.mLabelsMargin + labelOffset;
                    this.measureChildWithMargins(label, widthMeasureSpec, labelUsedWidth, heightMeasureSpec, 0);
                    var14 += label.getMeasuredWidth();
                    maxLabelWidth = Math.max(maxLabelWidth, var14 + labelOffset);
                }
            }
        }

        int var12 = Math.max(this.mMaxButtonWidth, maxLabelWidth + this.mLabelsMargin) + this.getPaddingLeft() + this.getPaddingRight();
        height += this.mButtonSpacing * (this.mButtonsCount - 1) + this.getPaddingTop() + this.getPaddingBottom();
        height = this.adjustForOvershoot(height);
        if(this.getLayoutParams().width == -1) {
            var12 = getDefaultSize(this.getSuggestedMinimumWidth(), widthMeasureSpec);
        }

        if(this.getLayoutParams().height == -1) {
            height = getDefaultSize(this.getSuggestedMinimumHeight(), heightMeasureSpec);
        }

        this.setMeasuredDimension(var12, height);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int buttonsHorizontalCenter = this.mLabelsPosition == 0?r - l - this.mMaxButtonWidth / 2 - this.getPaddingRight():this.mMaxButtonWidth / 2 + this.getPaddingLeft();
        boolean openUp = this.mOpenDirection == 0;
        int menuButtonTop = openUp?b - t - this.mMenuButton.getMeasuredHeight() - this.getPaddingBottom():this.getPaddingTop();
        int menuButtonLeft = buttonsHorizontalCenter - this.mMenuButton.getMeasuredWidth() / 2;
        this.mMenuButton.layout(menuButtonLeft, menuButtonTop, menuButtonLeft + this.mMenuButton.getMeasuredWidth(), menuButtonTop + this.mMenuButton.getMeasuredHeight());
        int imageLeft = buttonsHorizontalCenter - this.mImageToggle.getMeasuredWidth() / 2;
        int imageTop = menuButtonTop + this.mMenuButton.getMeasuredHeight() / 2 - this.mImageToggle.getMeasuredHeight() / 2;
        this.mImageToggle.layout(imageLeft, imageTop, imageLeft + this.mImageToggle.getMeasuredWidth(), imageTop + this.mImageToggle.getMeasuredHeight());
        int nextY = openUp?menuButtonTop + this.mMenuButton.getMeasuredHeight() + this.mButtonSpacing:menuButtonTop;

        for(int i = this.mButtonsCount - 1; i >= 0; --i) {
            View child = this.getChildAt(i);
            if(child != this.mImageToggle) {
                FloatingActionButton fab = (FloatingActionButton)child;
                if(fab.getVisibility() != 8) {
                    int childX = buttonsHorizontalCenter - fab.getMeasuredWidth() / 2;
                    int childY = openUp?nextY - fab.getMeasuredHeight() - this.mButtonSpacing:nextY;
                    if(fab != this.mMenuButton) {
                        fab.layout(childX, childY, childX + fab.getMeasuredWidth(), childY + fab.getMeasuredHeight());
                        if(!this.mIsMenuOpening) {
                            fab.hide(false);
                        }
                    }

                    View label = (View)fab.getTag(R.id.fab_label);
                    if(label != null) {
                        int labelsOffset = (this.mUsingMenuLabel?this.mMaxButtonWidth / 2:fab.getMeasuredWidth() / 2) + this.mLabelsMargin;
                        int labelXNearButton = this.mLabelsPosition == 0?buttonsHorizontalCenter - labelsOffset:buttonsHorizontalCenter + labelsOffset;
                        int labelXAwayFromButton = this.mLabelsPosition == 0?labelXNearButton - label.getMeasuredWidth():labelXNearButton + label.getMeasuredWidth();
                        int labelLeft = this.mLabelsPosition == 0?labelXAwayFromButton:labelXNearButton;
                        int labelRight = this.mLabelsPosition == 0?labelXNearButton:labelXAwayFromButton;
                        int labelTop = childY - this.mLabelsVerticalOffset + (fab.getMeasuredHeight() - label.getMeasuredHeight()) / 2;
                        label.layout(labelLeft, labelTop, labelRight, labelTop + label.getMeasuredHeight());
                        if(!this.mIsMenuOpening) {
                            label.setVisibility(4);
                        }
                    }

                    nextY = openUp?childY - this.mButtonSpacing:childY + child.getMeasuredHeight() + this.mButtonSpacing;
                }
            }
        }

    }

    private int adjustForOvershoot(int dimension) {
        return (int)((double)dimension * 0.03D + (double)dimension);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.bringChildToFront(this.mMenuButton);
        this.bringChildToFront(this.mImageToggle);
        this.mButtonsCount = this.getChildCount();
        this.createLabels();
    }

    private void createLabels() {
        for(int i = 0; i < this.mButtonsCount; ++i) {
            if(this.getChildAt(i) != this.mImageToggle) {
                FloatingActionButton fab = (FloatingActionButton)this.getChildAt(i);
                if(fab.getTag(R.id.fab_label) == null) {
                    this.addLabel(fab);
                    if(fab == this.mMenuButton) {
                        this.mMenuButton.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                FloatingActionMenu.this.toggle(FloatingActionMenu.this.mIsAnimated);
                            }
                        });
                    }
                }
            }
        }

    }

    private void addLabel(FloatingActionButton fab) {
        String text = fab.getLabelText();
        if(!TextUtils.isEmpty(text)) {
            Label label = new Label(this.mLabelsContext);
            label.setClickable(true);
            label.setFab(fab);
            label.setShowAnimation(AnimationUtils.loadAnimation(this.getContext(), this.mLabelsShowAnimation));
            label.setHideAnimation(AnimationUtils.loadAnimation(this.getContext(), this.mLabelsHideAnimation));
            if(this.mLabelsStyle > 0) {
                label.setTextAppearance(this.getContext(), this.mLabelsStyle);
                label.setShowShadow(false);
                label.setUsingStyle(true);
            } else {
                label.setColors(this.mLabelsColorNormal, this.mLabelsColorPressed, this.mLabelsColorRipple);
                label.setShowShadow(this.mLabelsShowShadow);
                label.setCornerRadius(this.mLabelsCornerRadius);
                if(this.mLabelsEllipsize > 0) {
                    this.setLabelEllipsize(label);
                }

                label.setMaxLines(this.mLabelsMaxLines);
                label.updateBackground();
                label.setTextSize(0, this.mLabelsTextSize);
                label.setTextColor(this.mLabelsTextColor);
                int left = this.mLabelsPaddingLeft;
                int top = this.mLabelsPaddingTop;
                if(this.mLabelsShowShadow) {
                    left += fab.getShadowRadius() + Math.abs(fab.getShadowXOffset());
                    top += fab.getShadowRadius() + Math.abs(fab.getShadowYOffset());
                }

                label.setPadding(left, top, this.mLabelsPaddingLeft, this.mLabelsPaddingTop);
                if(this.mLabelsMaxLines < 0 || this.mLabelsSingleLine) {
                    label.setSingleLine(this.mLabelsSingleLine);
                }
            }

            if(this.mCustomTypefaceFromFont != null) {
                label.setTypeface(this.mCustomTypefaceFromFont);
            }

            label.setText(text);
            label.setOnClickListener(fab.getOnClickListener());
            this.addView(label);
            fab.setTag(R.id.fab_label, label);
        }
    }

    private void setLabelEllipsize(Label label) {
        switch(this.mLabelsEllipsize) {
            case 1:
                label.setEllipsize(TextUtils.TruncateAt.START);
                break;
            case 2:
                label.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                break;
            case 3:
                label.setEllipsize(TextUtils.TruncateAt.END);
                break;
            case 4:
                label.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        }

    }

    public MarginLayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(this.getContext(), attrs);
    }

    protected MarginLayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    protected MarginLayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(-2, -2);
    }

    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    private void hideMenuButtonWithImage(boolean animate) {
        if(!this.isMenuButtonHidden()) {
            this.mMenuButton.hide(animate);
            if(animate) {
                this.mImageToggle.startAnimation(this.mImageToggleHideAnimation);
            }

            this.mImageToggle.setVisibility(4);
            this.mIsMenuButtonAnimationRunning = false;
        }

    }

    private void showMenuButtonWithImage(boolean animate) {
        if(this.isMenuButtonHidden()) {
            this.mMenuButton.show(animate);
            if(animate) {
                this.mImageToggle.startAnimation(this.mImageToggleShowAnimation);
            }

            this.mImageToggle.setVisibility(0);
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        if(this.mIsSetClosedOnTouchOutside) {
            boolean handled = false;
            switch(event.getAction()) {
                case 0:
                    handled = this.isOpened();
                    break;
                case 1:
                    this.close(this.mIsAnimated);
                    handled = true;
            }

            return handled;
        } else {
            return super.onTouchEvent(event);
        }
    }

    public boolean isOpened() {
        return this.mMenuOpened;
    }

    public void toggle(boolean animate) {
        if(this.isOpened()) {
            this.close(animate);
        } else {
            this.open(animate);
        }

    }

    public void open(final boolean animate) {
        if(!this.isOpened()) {
            if(this.isBackgroundEnabled()) {
                this.mShowBackgroundAnimator.start();
            }

            if(this.mIconAnimated) {
                if(this.mIconToggleSet != null) {
                    this.mIconToggleSet.start();
                } else {
                    this.mCloseAnimatorSet.cancel();
                    this.mOpenAnimatorSet.start();
                }
            }

            int delay = 0;
            int counter = 0;
            this.mIsMenuOpening = true;

            for(int i = this.getChildCount() - 1; i >= 0; --i) {
                View child = this.getChildAt(i);
                if(child instanceof FloatingActionButton && child.getVisibility() != 8) {
                    ++counter;
                    final FloatingActionButton fab = (FloatingActionButton)child;
                    this.mUiHandler.postDelayed(new Runnable() {
                        public void run() {
                            if(!FloatingActionMenu.this.isOpened()) {
                                if(fab != FloatingActionMenu.this.mMenuButton) {
                                    fab.show(animate);
                                }

                                Label label = (Label)fab.getTag(R.id.fab_label);
                                if(label != null && label.isHandleVisibilityChanges()) {
                                    label.show(animate);
                                }
                            }
                        }
                    }, (long)delay);
                    delay += this.mAnimationDelayPerItem;
                }
            }

            Handler var10000 = this.mUiHandler;
            Runnable var10001 = new Runnable() {
                public void run() {
                    FloatingActionMenu.this.mMenuOpened = true;
                    if(FloatingActionMenu.this.mToggleListener != null) {
                        FloatingActionMenu.this.mToggleListener.onMenuToggle(true);
                        //mImageToggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_fab_down));
                    }

                }
            };
            ++counter;
            var10000.postDelayed(var10001, (long)(counter * this.mAnimationDelayPerItem));
        }

    }

    public void close(final boolean animate) {
        if(this.isOpened()) {
            if(this.isBackgroundEnabled()) {
                this.mHideBackgroundAnimator.start();
            }

            if(this.mIconAnimated) {
                if(this.mIconToggleSet != null) {
                    this.mIconToggleSet.start();
                } else {
                    this.mCloseAnimatorSet.start();
                    this.mOpenAnimatorSet.cancel();
                }
            }

            int delay = 0;
            int counter = 0;
            this.mIsMenuOpening = false;

            for(int i = 0; i < this.getChildCount(); ++i) {
                View child = this.getChildAt(i);
                if(child instanceof FloatingActionButton && child.getVisibility() != 8) {
                    ++counter;
                    final FloatingActionButton fab = (FloatingActionButton)child;
                    this.mUiHandler.postDelayed(new Runnable() {
                        public void run() {
                            if(FloatingActionMenu.this.isOpened()) {
                                if(fab != FloatingActionMenu.this.mMenuButton) {
                                    fab.hide(animate);
                                }

                                Label label = (Label)fab.getTag(R.id.fab_label);
                                if(label != null && label.isHandleVisibilityChanges()) {
                                    label.hide(animate);
                                }

                            }
                        }
                    }, (long)delay);
                    delay += this.mAnimationDelayPerItem;
                }
            }

            Handler var10000 = this.mUiHandler;
            Runnable var10001 = new Runnable() {
                public void run() {
                    FloatingActionMenu.this.mMenuOpened = false;
                    if(FloatingActionMenu.this.mToggleListener != null) {
                        FloatingActionMenu.this.mToggleListener.onMenuToggle(false);
                        //mImageToggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_fab_menu));
                    }

                }
            };
            ++counter;
            var10000.postDelayed(var10001, (long)(counter * this.mAnimationDelayPerItem));
        }

    }

    public void setIconAnimationInterpolator(Interpolator interpolator) {
        this.mOpenAnimatorSet.setInterpolator(interpolator);
        this.mCloseAnimatorSet.setInterpolator(interpolator);
    }

    public void setIconAnimationOpenInterpolator(Interpolator openInterpolator) {
        this.mOpenAnimatorSet.setInterpolator(openInterpolator);
    }

    public void setIconAnimationCloseInterpolator(Interpolator closeInterpolator) {
        this.mCloseAnimatorSet.setInterpolator(closeInterpolator);
    }

    public void setAnimated(boolean animated) {
        this.mIsAnimated = animated;
        this.mOpenAnimatorSet.setDuration(animated?300L:0L);
        this.mCloseAnimatorSet.setDuration(animated?300L:0L);
    }

    public boolean isAnimated() {
        return this.mIsAnimated;
    }

    public void setAnimationDelayPerItem(int animationDelayPerItem) {
        this.mAnimationDelayPerItem = animationDelayPerItem;
    }

    public int getAnimationDelayPerItem() {
        return this.mAnimationDelayPerItem;
    }

    public void setOnMenuToggleListener(FloatingActionMenu.OnMenuToggleListener listener) {
        this.mToggleListener = listener;
    }

    public void setIconAnimated(boolean animated) {
        this.mIconAnimated = animated;
    }

    public boolean isIconAnimated() {
        return this.mIconAnimated;
    }

    public ImageView getMenuIconView() {
        return this.mImageToggle;
    }

    public void setIconToggleAnimatorSet(AnimatorSet toggleAnimatorSet) {
        this.mIconToggleSet = toggleAnimatorSet;
    }

    public AnimatorSet getIconToggleAnimatorSet() {
        return this.mIconToggleSet;
    }

    public void setMenuButtonShowAnimation(Animation showAnimation) {
        this.mMenuButtonShowAnimation = showAnimation;
        this.mMenuButton.setShowAnimation(showAnimation);
    }

    public void setMenuButtonHideAnimation(Animation hideAnimation) {
        this.mMenuButtonHideAnimation = hideAnimation;
        this.mMenuButton.setHideAnimation(hideAnimation);
    }

    public boolean isMenuHidden() {
        return this.getVisibility() == 4;
    }

    public boolean isMenuButtonHidden() {
        return this.mMenuButton.isHidden();
    }

    public void showMenu(boolean animate) {
        if(this.isMenuHidden()) {
            if(animate) {
                this.startAnimation(this.mMenuButtonShowAnimation);
            }

            this.setVisibility(0);
        }

    }

    public void hideMenu(final boolean animate) {
        if(!this.isMenuHidden() && !this.mIsMenuButtonAnimationRunning) {
            this.mIsMenuButtonAnimationRunning = true;
            if(this.isOpened()) {
                this.close(animate);
                this.mUiHandler.postDelayed(new Runnable() {
                    public void run() {
                        if(animate) {
                            FloatingActionMenu.this.startAnimation(FloatingActionMenu.this.mMenuButtonHideAnimation);
                        }

                        FloatingActionMenu.this.setVisibility(4);
                        FloatingActionMenu.this.mIsMenuButtonAnimationRunning = false;
                    }
                }, (long)(this.mAnimationDelayPerItem * this.mButtonsCount));
            } else {
                if(animate) {
                    this.startAnimation(this.mMenuButtonHideAnimation);
                }

                this.setVisibility(4);
                this.mIsMenuButtonAnimationRunning = false;
            }
        }

    }

    public void toggleMenu(boolean animate) {
        if(this.isMenuHidden()) {
            this.showMenu(animate);
        } else {
            this.hideMenu(animate);
        }

    }

    public void showMenuButton(boolean animate) {
        if(this.isMenuButtonHidden()) {
            this.showMenuButtonWithImage(animate);
        }

    }

    public void hideMenuButton(final boolean animate) {
        if(!this.isMenuButtonHidden() && !this.mIsMenuButtonAnimationRunning) {
            this.mIsMenuButtonAnimationRunning = true;
            if(this.isOpened()) {
                this.close(animate);
                this.mUiHandler.postDelayed(new Runnable() {
                    public void run() {
                        FloatingActionMenu.this.hideMenuButtonWithImage(animate);
                    }
                }, (long)(this.mAnimationDelayPerItem * this.mButtonsCount));
            } else {
                this.hideMenuButtonWithImage(animate);
            }
        }

    }

    public void toggleMenuButton(boolean animate) {
        if(this.isMenuButtonHidden()) {
            this.showMenuButton(animate);
        } else {
            this.hideMenuButton(animate);
        }

    }

    public void setClosedOnTouchOutside(boolean close) {
        this.mIsSetClosedOnTouchOutside = close;
    }

    public void setMenuButtonColorNormal(int color) {
        this.mMenuColorNormal = color;
        this.mMenuButton.setColorNormal(color);
    }

    public void setMenuButtonColorNormalResId(int colorResId) {
        this.mMenuColorNormal = this.getResources().getColor(colorResId);
        this.mMenuButton.setColorNormalResId(colorResId);
    }

    public int getMenuButtonColorNormal() {
        return this.mMenuColorNormal;
    }

    public void setMenuButtonColorPressed(int color) {
        this.mMenuColorPressed = color;
        this.mMenuButton.setColorPressed(color);
    }

    public void setMenuButtonColorPressedResId(int colorResId) {
        this.mMenuColorPressed = this.getResources().getColor(colorResId);
        this.mMenuButton.setColorPressedResId(colorResId);
    }

    public int getMenuButtonColorPressed() {
        return this.mMenuColorPressed;
    }

    public void setMenuButtonColorRipple(int color) {
        this.mMenuColorRipple = color;
        this.mMenuButton.setColorRipple(color);
    }

    public void setMenuButtonColorRippleResId(int colorResId) {
        this.mMenuColorRipple = this.getResources().getColor(colorResId);
        this.mMenuButton.setColorRippleResId(colorResId);
    }

    public int getMenuButtonColorRipple() {
        return this.mMenuColorRipple;
    }

    public void addMenuButton(FloatingActionButton fab) {
        this.addView(fab, this.mButtonsCount - 2);
        ++this.mButtonsCount;
        this.addLabel(fab);
    }

    public void removeMenuButton(FloatingActionButton fab) {
        this.removeView(fab.getLabelView());
        this.removeView(fab);
        --this.mButtonsCount;
    }

    public void addMenuButton(FloatingActionButton fab, int index) {
        int size = this.mButtonsCount - 2;
        if(index < 0) {
            index = 0;
        } else if(index > size) {
            index = size;
        }

        this.addView(fab, index);
        ++this.mButtonsCount;
        this.addLabel(fab);
    }

    public void removeAllMenuButtons() {
        this.close(true);
        ArrayList viewsToRemove = new ArrayList();

        for(int i = 0; i < this.getChildCount(); ++i) {
            View v = this.getChildAt(i);
            if(v != this.mMenuButton && v != this.mImageToggle && v instanceof FloatingActionButton) {
                viewsToRemove.add((FloatingActionButton)v);
            }
        }

        Iterator var4 = viewsToRemove.iterator();

        while(var4.hasNext()) {
            FloatingActionButton var5 = (FloatingActionButton)var4.next();
            this.removeMenuButton(var5);
        }

    }

    public void setMenuButtonLabelText(String text) {
        this.mMenuButton.setLabelText(text);
    }

    public String getMenuButtonLabelText() {
        return this.mMenuLabelText;
    }

    public void setOnMenuButtonClickListener(OnClickListener clickListener) {
        this.mMenuButton.setOnClickListener(clickListener);
    }

    public void setOnMenuButtonLongClickListener(OnLongClickListener longClickListener) {
        this.mMenuButton.setOnLongClickListener(longClickListener);
    }

    public interface OnMenuToggleListener {
        void onMenuToggle(boolean var1);
    }
}
