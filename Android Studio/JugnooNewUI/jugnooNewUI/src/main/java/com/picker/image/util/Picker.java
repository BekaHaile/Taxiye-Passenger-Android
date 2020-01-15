package com.picker.image.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import android.util.TypedValue;

import com.picker.image.model.ImageEntry;
import com.picker.image.ui.PickerActivity;

import java.io.Serializable;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import product.clicklabs.jugnoo.R;


public final class Picker implements Serializable{

    public int limit;
    public final int fabBackgroundColor;
    public final int fabBackgroundColorWhenPressed;
    public final int imageBackgroundColorWhenChecked;
    public final int imageBackgroundColor;
    public final int imageCheckColor;
    public final int checkedImageOverlayColor;
//    public final PickListener pickListener;
    public final int albumImagesCountTextColor;
    public final int albumBackgroundColor;
    public final int albumNameTextColor;
    public final PickMode pickMode;
    public final int themeResId;
    public final int popupThemeResId;
    public final int captureItemIconTintColor;
    public final int doneFabIconTintColor;
    public final boolean shouldShowCaptureMenuItem;
    public final int checkIconTintColor;
    public final boolean videosEnabled;
    public final int videoLengthLimit;
    public final int videoThumbnailOverlayColor;
    public final int videoIconTintColor;
    public final boolean backBtnInMainActivity;

    private Picker(final Builder builder) {

        limit = builder.mLimit;
        fabBackgroundColor = builder.mFabBackgroundColor;
        fabBackgroundColorWhenPressed = builder.mFabBackgroundColorWhenPressed;
        imageBackgroundColorWhenChecked = builder.mImageBackgroundColorWhenChecked;
        imageBackgroundColor = builder.mImageBackgroundColor;
        imageCheckColor = builder.mImageCheckColor;
        checkedImageOverlayColor = builder.mCheckedImageOverlayColor;
//        pickListener = builder.mPickListener;
        albumBackgroundColor = builder.mAlbumBackgroundColor;
        albumImagesCountTextColor = builder.mAlbumImagesCountTextColor;
        albumNameTextColor = builder.mAlbumNameTextColor;
        pickMode = builder.mPickMode;
        themeResId = builder.mThemeResId;
        popupThemeResId = builder.mPopupThemeResId;
        captureItemIconTintColor = builder.mCaptureItemIconTintColor;
        doneFabIconTintColor = builder.mDoneFabIconTintColor;
        shouldShowCaptureMenuItem = builder.mShouldShowCaptureMenuItem;
        checkIconTintColor = builder.mCheckIconTintColor;
        videosEnabled = builder.mVideosEnabled;
        videoLengthLimit = builder.mVideoLengthLimit;
        videoThumbnailOverlayColor = builder.mVideoThumbnailOverlayColor;
        videoIconTintColor = builder.mVideoIconTintColor;
        backBtnInMainActivity = builder.mBackBtnInMainActivity;

    }

    public void startActivity(Fragment fragment,Activity activity,int reqCode) {

        EventBus.getDefault().postSticky(new Events.OnPublishPickOptionsEvent(this));

        final Intent intent = new Intent(activity, PickerActivity.class);

        fragment.startActivityForResult(intent,reqCode);

    }
    public void startActivity(Activity activity,int reqCode) {

        EventBus.getDefault().postSticky(new Events.OnPublishPickOptionsEvent(this));

        final Intent intent = new Intent(activity, PickerActivity.class);

        activity.startActivityForResult(intent,reqCode);

    }

    public void setLimit(int limit){
        this.limit=limit;
    }

    public enum PickMode {

        SINGLE_IMAGE, MULTIPLE_IMAGES
    }

    public interface PickListener {
        void onPickedSuccessfully(final ArrayList<ImageEntry> images);

        void onCancel();

    }

    public static class Builder {

        private final Context mContext;

        private final int mThemeResId;
        private int mLimit = PickerActivity.NO_LIMIT;
        private int mFabBackgroundColor;
        private int mFabBackgroundColorWhenPressed;
        private int mImageBackgroundColorWhenChecked;
        private int mImageBackgroundColor;
        private int mImageCheckColor;
        private int mCheckedImageOverlayColor;
        private int mAlbumImagesCountTextColor;
        private int mAlbumBackgroundColor;
        private int mAlbumNameTextColor;
        private PickMode mPickMode;
        private int mPopupThemeResId;
        private int mDoneFabIconTintColor;
        private int mCaptureItemIconTintColor;
        private boolean mShouldShowCaptureMenuItem;
        private int mCheckIconTintColor;
        private boolean mVideosEnabled;
        private int mVideoLengthLimit;
        private int mVideoThumbnailOverlayColor;
        private int mVideoIconTintColor;
        private boolean mBackBtnInMainActivity;

        //Use (Context,PickListener,themeResId) instead
        @Deprecated
        public Builder(final Context context, final PickListener listener) {


            mThemeResId = R.style.Theme_AppCompat_Light_NoActionBar;
            mContext = context;
            mContext.setTheme(mThemeResId);
            init();


        }

        public Builder(@NonNull final Context context, @StyleRes final int themeResId) {
            mContext = context;
            mContext.setTheme(themeResId);
            mThemeResId = themeResId;
            init();

        }

        private void init() {
            final TypedValue typedValue = new TypedValue();
            initUsingColorAccent(typedValue);

            mImageBackgroundColor = getColor(R.color.alter_unchecked_image_background);
            mImageCheckColor = getColor(R.color.alter_image_check_color);
            mCheckedImageOverlayColor = getColor(R.color.alter_checked_photo_overlay);
            mAlbumBackgroundColor = getColor(R.color.alter_album_background);
            mAlbumNameTextColor = getColor(R.color.alter_album_name_text_color);
            mAlbumImagesCountTextColor = getColor(R.color.alter_album_images_count_text_color);
            mFabBackgroundColorWhenPressed = ColorUtils.setAlphaComponent(mFabBackgroundColor, (int) (android.graphics.Color.alpha(mFabBackgroundColor) * 0.8f));
            mPickMode = PickMode.MULTIPLE_IMAGES;

            mPopupThemeResId = Util.getDefaultPopupTheme(mContext);
            mCaptureItemIconTintColor = mDoneFabIconTintColor = Util.getDefaultIconTintColor(mContext);

            mShouldShowCaptureMenuItem = true;

            mCheckIconTintColor = Color.WHITE;
            mVideosEnabled = false;
            mVideoLengthLimit = 0; // No limit

            mVideoThumbnailOverlayColor = getColor(R.color.alter_video_thumbnail_overlay);
            mVideoIconTintColor = Color.WHITE;
        }

        private int getColor(@ColorRes final int colorRes) {
            return ContextCompat.getColor(mContext, colorRes);
        }


        private void initUsingColorAccent(final TypedValue typedValue) {
            mContext.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
            mImageBackgroundColorWhenChecked = mFabBackgroundColor = typedValue.data;
        }


        /**
         * @param limit limit for the count of image user can pick , By default it's infinite
         */
        public Picker.Builder setLimit(final int limit) {
            mLimit = limit;
            return this;
        }

        public Picker.Builder setFabBackgroundColor(@ColorInt final int color) {
            mFabBackgroundColor = color;
            return this;
        }

        public Picker.Builder setFabBackgroundColorWhenPressed(@ColorInt final int color) {
            mFabBackgroundColorWhenPressed = color;
            return this;
        }

        public Picker.Builder setImageBackgroundColorWhenChecked(@ColorInt final int color) {
            mImageBackgroundColorWhenChecked = color;
            return this;
        }

        public Picker.Builder setImageBackgroundColor(@ColorInt final int color) {
            mImageBackgroundColor = color;
            return this;
        }

        public Picker.Builder setImageCheckColor(@ColorInt final int color) {
            mImageCheckColor = color;
            return this;
        }

        public Picker.Builder setCheckedImageOverlayColor(@ColorInt final int color) {
            mCheckedImageOverlayColor = color;
            return this;
        }


        public Picker.Builder setAlbumBackgroundColor(@ColorInt final int color) {
            mAlbumBackgroundColor = color;
            return this;
        }

        public Picker.Builder setAlbumNameTextColor(@ColorInt final int color) {
            mAlbumNameTextColor = color;
            return this;
        }

        public Picker.Builder setAlbumImagesCountTextColor(@ColorInt final int color) {
            mAlbumImagesCountTextColor = color;
            return this;
        }

        public Picker.Builder setPickMode(final PickMode pickMode) {
            mPickMode = pickMode;
            return this;
        }

        public Picker.Builder setToolbarPopupTheme(@StyleRes final int themeRes) {
            mPopupThemeResId = themeRes;
            return this;
        }

        public Picker.Builder setDoneFabIconTintColor(@ColorInt final int color) {
            mDoneFabIconTintColor = color;
            return this;
        }

        public Picker.Builder setCaptureItemIconTintColor(@ColorInt final int color) {
            mCaptureItemIconTintColor = color;
            return this;
        }

        public Picker.Builder disableCaptureImageFromCamera() {
            mShouldShowCaptureMenuItem = false;
            return this;
        }

        public Picker.Builder setCheckIconTintColor(@ColorInt final int color) {
            mCheckIconTintColor = color;
            return this;
        }

        public Picker.Builder setBackBtnInMainActivity(final boolean backBtn) {
            mBackBtnInMainActivity = backBtn;
            return this;
        }

        public Picker.Builder setVideosEnabled(final boolean enabled) {
            mVideosEnabled = enabled;
            return this;
        }

        public Picker.Builder setVideoLengthLimitInMilliSeconds(final int limit) {
            mVideoLengthLimit = limit;
            return this;
        }

        public Picker.Builder setVideoThumbnailOverlayColor(@ColorInt final int color) {
            mVideoThumbnailOverlayColor = color;
            return this;
        }

        public Picker.Builder setVideoIconTintColor(@ColorInt final int color) {
            mVideoIconTintColor = color;
            return this;
        }



        public Picker build() {
            return new Picker(this);
        }


    }
}
