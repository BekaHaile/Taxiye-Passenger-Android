package com.sabkuchfresh.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
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
import com.sabkuchfresh.utils.DirectionsGestureListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

    @Bind(R.id.llCollapRatingStars)
    LinearLayout llCollapRatingStars;

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
    @Bind(R.id.tvFeedHyperLink)
    public TextView tvFeedHyperLink;

    private FreshActivity activity;
    private BlurImageTask loadBlurredImageTask;
    private Target target;
    private int imageMaxHeight, imageMaxWidth;


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
        View view = inflater.inflate(R.layout.restaurant_collapse_details, container, false);
        ButterKnife.bind(this, view);

        setupDetails(false);

        backgroundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.performBackPressed(true);
            }
        });

        ivRestOriginalImage.setOnTouchListener(new View.OnTouchListener() {
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
            tvCollapRestaurantDeliveryTime.setText("");
            activity.clearRestaurantRatingStars(llCollapRatingStars, tvCollapRestaurantRating);

            shadowView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.opaque_black_strong));


            layoutRestDetails.setVisibility(View.VISIBLE);
            ivRestOriginalImage.setVisibility(View.VISIBLE);


            backgroundImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if (activity.getVendorOpened() != null) {


                if (!TextUtils.isEmpty(activity.getVendorOpened().getImage())) {
                    ViewGroup.LayoutParams layoutParams = ivRestOriginalImage.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    ivRestOriginalImage.setLayoutParams(layoutParams);

                    ivRestOriginalImage.post(new Runnable() {
                        @Override
                        public void run() {
                            if (RestaurantImageFragment.this.getView() != null && !TextUtils.isEmpty(activity.getVendorOpened().getImage())) {
                                target = new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                                        imageMaxWidth = ivRestOriginalImage.getMeasuredWidth();
                                        imageMaxHeight = ivRestOriginalImage.getMeasuredHeight();

                                        int imgHeight = (imageMaxWidth * bitmap.getHeight()) / bitmap.getWidth();
                                        int imgWidth = imageMaxWidth;

                                        if (imgHeight > imageMaxHeight) {
                                            imgHeight = imageMaxHeight;
                                            imgWidth = (imageMaxHeight * bitmap.getWidth()) / bitmap.getHeight();
                                        }


                                        ViewGroup.LayoutParams layoutParams = ivRestOriginalImage.getLayoutParams();
                                        layoutParams.width = imgWidth;
                                        layoutParams.height = imgHeight;
                                        ivRestOriginalImage.setLayoutParams(layoutParams);
                                        ivRestOriginalImage.setImageBitmap(bitmap);
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable drawable) {
                                        ivRestOriginalImage.setImageDrawable(drawable);
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable drawable) {
                                        ivRestOriginalImage.setImageDrawable(drawable);
                                    }
                                };

                                if (!TextUtils.isEmpty(activity.getVendorOpened().getImage())) {
                                    Picasso.with(getActivity()).load(activity.getVendorOpened().getImage()).placeholder(R.drawable.ic_fresh_item_placeholder).into(target);
                                }
                            }
                        }
                    });
                }


                //background blurred Image
                if(!TextUtils.isEmpty(activity.getVendorOpened().getImage())) {
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
                }


                tvRestTitle.setText(activity.getVendorOpened().getName().toUpperCase());

                int visibility = activity.setVendorDeliveryTimeAndDrawableColorToTextView(activity.getVendorOpened(), tvCollapRestaurantDeliveryTime, R.color.white);
                tvCollapRestaurantDeliveryTime.setVisibility(visibility == View.VISIBLE ? View.VISIBLE : View.GONE);

                if (activity.getVendorOpened().getRating() != null && activity.getVendorOpened().getRating() >= 1d) {
                    llCollapRatingStars.setVisibility(View.VISIBLE);
                    activity.setRestaurantRatingStarsToLL(llCollapRatingStars, tvCollapRestaurantRating, activity.getVendorOpened().getRating());
                } else {
                    llCollapRatingStars.setVisibility(View.GONE);
                }


				activity.setFeedArrowToTextView(tvFeedHyperLink);
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
            new DirectionsGestureListener(new DirectionsGestureListener.Callback() {
                @Override
                public void topSwipe() {
                    activity.performBackPressed(true);
                }

                @Override
                public void bottomSwipe() {
                }

                @Override
                public void leftSwipe() {

                }

                @Override
                public void rightSwipe() {

                }
            }));
}
