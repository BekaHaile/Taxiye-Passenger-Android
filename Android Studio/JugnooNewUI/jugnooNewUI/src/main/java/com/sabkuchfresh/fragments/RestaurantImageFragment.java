package com.sabkuchfresh.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.utils.BlurImageTask;
import com.squareup.picasso.Picasso;

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

    @Bind(R.id.llCollapseRating)
    LinearLayout llCollapseRating;

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
        setupDetails(false);
        backgroundImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });
        return view;
    }

    private void setupDetails(boolean fromHidden) {
        activity.fragmentUISetup(this);
        if (!fromHidden) {
            try {
                if (layoutRestDetails != null) {
                    new ASSL(activity, layoutRestDetails, 1134, 720, true);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        if (activity != null) {
            shadowView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.opaque_black_strong));


            layoutRestDetails.setVisibility(View.VISIBLE);
            ivRestOriginalImage.setVisibility(View.VISIBLE);
            backgroundImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if (activity.getVendorOpened() != null && activity.getVendorOpened().getImage() != null) {


                Picasso.with(getActivity()).load(activity.getVendorOpened().getImage()).placeholder(R.drawable.ic_fresh_item_placeholder).into(ivRestOriginalImage);
                //background blurred Image
                loadBlurredImageTask = new BlurImageTask(getActivity(), new BlurImageTask.OnExecution() {
                    @Override
                    public void onLoad(Bitmap originalbitmap, Bitmap blurredBitmap) {
//                        if (originalbitmap != null) {
//                            ivRestOriginalImage.setImageBitmap(originalbitmap);
//                        }
                        if (blurredBitmap != null) {
                            backgroundImageView.setImageBitmap(blurredBitmap);
                        }
                    }
                });
                loadBlurredImageTask.execute(activity.getVendorOpened().getImage());


                tvRestTitle.setText(activity.getVendorOpened().getName());

                activity.setVendorDeliveryTimeToTextView(activity.getVendorOpened(), tvCollapRestaurantDeliveryTime);
                activity.setTextViewDrawableColor(tvCollapRestaurantDeliveryTime, ContextCompat.getColor(activity, R.color.white));

                if (activity.getVendorOpened().getRating() != null && activity.getVendorOpened().getRating() >= 1d) {
                    tvCollapRestaurantRating.setVisibility(View.VISIBLE);
                    activity.setRatingAndGetColor(tvCollapRestaurantRating, activity.getVendorOpened().getRating(),
                            activity.getVendorOpened().getColorCode());
                } else {
                    tvCollapRestaurantRating.setVisibility(View.GONE);
                }

            }

            llCollapseRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.openRestaurantReviewsListFragment();
                }
            });
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setupDetails(true);
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroyView() {

        if (loadBlurredImageTask != null && !loadBlurredImageTask.isCancelled())
            loadBlurredImageTask.cancel(true);
        ButterKnife.unbind(this);
        activity.resetCollapseToolbar();
        super.onDestroyView();
    }





    final GestureDetector gesture = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    final float SWIPE_MIN_DISTANCE = 125.0f;
                    if (e1.getY() - e2.getY() < SWIPE_MIN_DISTANCE)
                        return false;


                    switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
                        case 1:
                            activity.performBackPressed();
                            return true;
                        case 2:

                            return true;
                        case 3:

                            return true;
                        case 4:
                            return true;
                    }
                    return false;
                }
            });

    private int getSlope(float x1, float y1, float x2, float y2) {
        Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
        if (angle > 45 && angle <= 135)
            // top
            return 1;
        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
            // left
            return 2;
        if (angle < -45 && angle >= -135)
            // down
            return 3;
        if (angle > -45 && angle <= 45)
            // right
            return 4;
        return 0;
    }

}
