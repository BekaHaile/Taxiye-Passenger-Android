package com.sabkuchfresh.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.utils.BlurImageTask;
import com.sabkuchfresh.utils.CustomTypeFaceSpan;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;


public class RestaurantImageFragment extends Fragment {

    @Bind(R.id.iv_rest_collapse_image)
    ImageView backgroundImageView;
    @Bind(R.id.tv_rest_title)
    TextView tvRestTitle;
    @Bind(R.id.tvCollapRestaurantRating)
    TextView tvCollapRestaurantRating;
    @Bind(R.id.tvCollapRestaurantDeliveryTime)
    TextView tvCollapRestaurantDeliveryTime;
    @Bind(R.id.layout_rest_details)
    RelativeLayout layoutRestDetails;
    @Bind(R.id.iv_rest_original_image)
    ImageView ivRestOriginalImage;
    @Bind(R.id.shadow_view)
    View shadowView;
    private FreshActivity activity;
    private BlurImageTask loadBlurredImageTask;


    public RestaurantImageFragment() {
        // Required empty public constructor
    }


    public static RestaurantImageFragment newInstance() {
        return new RestaurantImageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FreshActivity)
            activity = (FreshActivity) context;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.restaurant_collapse_details, container, false);
        ButterKnife.bind(this, view);
        activity.fragmentUISetup(this);
        try {
            if (layoutRestDetails != null) {
                new ASSL(activity, layoutRestDetails, 1134, 720, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (activity != null) {
            shadowView.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.opaque_black_strong));
            layoutRestDetails.setVisibility(View.VISIBLE);
            ivRestOriginalImage.setVisibility(View.VISIBLE);
            backgroundImageView.setScaleType(ImageView.ScaleType.FIT_XY);

            if (activity.getVendorOpened() != null && activity.getVendorOpened().getImage() != null) {

                //background blurred Image
                loadBlurredImageTask = new BlurImageTask(getActivity(), new BlurImageTask.OnExecution() {
                    @Override
                    public void onLoad(Bitmap originalbitmap, Bitmap blurredBitmap) {
                        if (originalbitmap != null) {
                            ivRestOriginalImage.setImageBitmap(originalbitmap);
                        }
                        if (blurredBitmap != null) {
                            backgroundImageView.setImageBitmap(blurredBitmap);
                        }
                    }
                });
                loadBlurredImageTask.execute(activity.getVendorOpened().getImage());


                tvRestTitle.setText(activity.getVendorOpened().getName());

                activity.setVendorDeliveryTimeToTextView(activity.getVendorOpened(), tvCollapRestaurantDeliveryTime);
                setTextViewDrawableColor(tvCollapRestaurantDeliveryTime, ContextCompat.getColor(activity, R.color.white));

                if (activity.getVendorOpened().getRating() != null) {
                    tvCollapRestaurantRating.setVisibility(View.VISIBLE);

                    Spannable spannable = new SpannableString(activity.getString(R.string.star_icon) + " " + activity.getVendorOpened().getRating() + "  " + "(" + activity.getVendorOpened().getReviewCount() + ")");
                    Typeface star = Typeface.createFromAsset(activity.getAssets(), "fonts/icomoon.ttf");
                    spannable.setSpan(new CustomTypeFaceSpan("", star), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    tvCollapRestaurantRating.setText(spannable);
                    int ratingColor;
                    if (activity.getVendorOpened().getColorCode() != null
                            && activity.getVendorOpened().getColorCode().startsWith("#")
                            && activity.getVendorOpened().getColorCode().length() == 7)
                        ratingColor = Color.parseColor(activity.getVendorOpened().getColorCode());
                    else
                        ratingColor = Color.parseColor("#8dd061"); //default Green Color

                    setTextViewBackgroundDrawableColor(tvCollapRestaurantRating, ratingColor);
                } else {
                    tvCollapRestaurantRating.setVisibility(View.GONE);
                }

            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (loadBlurredImageTask != null && !loadBlurredImageTask.isCancelled())
            loadBlurredImageTask.cancel(true);
        ButterKnife.unbind(this);
    }


    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private void setTextViewBackgroundDrawableColor(TextView textView, int color) {
        if(textView.getBackground() != null){
            textView.getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
    }

}
