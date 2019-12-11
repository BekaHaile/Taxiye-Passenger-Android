package com.sabkuchfresh.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;


public class RestaurantImageFragment extends Fragment {

    @BindView(R.id.iv_rest_collapse_image)
    ImageView backgroundImageView;

    @BindView(R.id.tv_rest_title)
    TextView tvRestTitle;

    @BindView(R.id.tvCollapRestaurantRating)
    TextView tvCollapRestaurantRating;

    @BindView(R.id.llCollapRatingStars)
    LinearLayout llCollapRatingStars;

    @BindView(R.id.tvCollapRestaurantDeliveryTime)
    TextView tvCollapRestaurantDeliveryTime;

    @BindView(R.id.layout_rest_details)
    RelativeLayout layoutRestDetails;

    @BindView(R.id.iv_rest_original_image)
    ImageView ivRestOriginalImage;

    @BindView(R.id.llCollapseRating)
    LinearLayout llCollapseRating;
    @BindView(R.id.tvFeedHyperLink)
    public TextView tvFeedHyperLink;
    @BindView(R.id.shadow_view)
    View shadowView;

    private FreshActivity activity;
    private BlurImageTask loadBlurredImageTask;
    private Target target;



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
    Unbinder unbinder;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restaurant_collapse_details, container, false);
        unbinder = ButterKnife.bind(this, view);

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

        tvCollapRestaurantDeliveryTime.setVisibility(View.VISIBLE);
        tvRestTitle.setVisibility(View.VISIBLE);
        llCollapseRating.setVisibility(View.VISIBLE);


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
            activity.clearRestaurantRatingStars(llCollapRatingStars, tvCollapRestaurantRating, null);

            shadowView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.opaque_black_strong));


            layoutRestDetails.setVisibility(View.VISIBLE);
            ivRestOriginalImage.setVisibility(View.VISIBLE);


            backgroundImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if (activity.getVendorOpened() != null) {


                if (!TextUtils.isEmpty(activity.getVendorOpened().getImage())) {
                    ViewGroup.LayoutParams layoutParams = ivRestOriginalImage.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    ivRestOriginalImage.setLayoutParams(layoutParams);

                    if (!TextUtils.isEmpty(activity.getVendorOpened().getImage())) {
                        Picasso.with(getActivity()).load(activity.getVendorOpened().getImage()).fit().centerCrop().
                                placeholder(R.drawable.ic_fresh_item_placeholder).into(ivRestOriginalImage);
                    }
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

                int visibility = activity.setVendorDeliveryTimeAndDrawableColorToTextView(activity.getVendorOpened(), tvCollapRestaurantDeliveryTime, R.color.white, true);
                tvCollapRestaurantDeliveryTime.setVisibility(visibility == View.VISIBLE ? View.VISIBLE : View.GONE);

                if (activity.getVendorOpened().getRating() != null && activity.getVendorOpened().getRating() >= 1d) {
                    llCollapRatingStars.setVisibility(View.VISIBLE);
                    activity.setRestaurantRatingStarsToLL(llCollapRatingStars, tvCollapRestaurantRating,
                            activity.getVendorOpened().getRating(),
                            R.drawable.ic_half_star_green_white, R.drawable.ic_star_white, null, 0);
                } else {
                    llCollapRatingStars.setVisibility(View.INVISIBLE);
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
        unbinder.unbind();
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
