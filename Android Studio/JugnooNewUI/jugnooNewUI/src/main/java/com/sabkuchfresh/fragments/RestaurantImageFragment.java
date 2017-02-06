package com.sabkuchfresh.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.utils.BlurImageTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;


public class RestaurantImageFragment extends Fragment {

    @Bind(R.id.iv_rest_collapse_image)
    ImageView backgroundImageView;
    @Bind(R.id.tv_rest_title)
    TextView tvRestTitle;
    @Bind(R.id.tv_rest_reviews)
    TextView tvRestReviews;
    @Bind(R.id.layout_rest_details)
    RelativeLayout layoutRestDetails;
    @Bind(R.id.iv_rest_original_image)
    ImageView ivRestOriginalImage;
    @Bind(R.id.shadow_view)
    View shadowView;
    private FreshActivity freshActivity;
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
            freshActivity = (FreshActivity) context;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.restaurant_collapse_details, container, false);
        ButterKnife.bind(this, view);
        freshActivity.fragmentUISetup(this);
        try {
            if (layoutRestDetails != null) {
                new ASSL(freshActivity, layoutRestDetails, 1134, 720, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (freshActivity != null) {
            shadowView.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.opaque_black_strong));
            layoutRestDetails.setVisibility(View.VISIBLE);
            ivRestOriginalImage.setVisibility(View.VISIBLE);
            backgroundImageView.setScaleType(ImageView.ScaleType.FIT_XY);

            if (freshActivity.getVendorOpened() != null && freshActivity.getVendorOpened().getImage() != null) {


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
                loadBlurredImageTask.execute(freshActivity.getVendorOpened().getImage());


                tvRestTitle.setText(freshActivity.getVendorOpened().getName());

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


}
