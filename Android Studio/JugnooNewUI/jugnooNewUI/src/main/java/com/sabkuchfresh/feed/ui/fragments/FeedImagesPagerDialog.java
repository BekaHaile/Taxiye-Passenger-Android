package com.sabkuchfresh.feed.ui.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.sabkuchfresh.feed.utils.FeedUtils;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;

/**
 * Created by Parminder Saini on 15/05/17.
 */

public class FeedImagesPagerDialog extends DialogFragment {

    public static final String IMAGES_TO_SHOW = "imagesToShow";
    public static final String POSITION_TO_OPEN = "positionToOpen";
    private ViewPager viewPager;


    public static FeedImagesPagerDialog newInstance(ArrayList<FetchFeedbackResponse.ReviewImage> imagesToShow) {
        return newInstance(imagesToShow, 0);

    }

    public static FeedImagesPagerDialog newInstance(ArrayList<FetchFeedbackResponse.ReviewImage> imagesToShow, Integer positionToOpen) {
        FeedImagesPagerDialog feedImagesPagerDialog = new FeedImagesPagerDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(IMAGES_TO_SHOW, imagesToShow);
        if (positionToOpen != null && positionToOpen < imagesToShow.size())
            bundle.putInt(POSITION_TO_OPEN, positionToOpen);
        else
            bundle.putInt(POSITION_TO_OPEN, 0);

        feedImagesPagerDialog.setArguments(bundle);
        return feedImagesPagerDialog;

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFragmentTrans);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_feed_images_pager, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.vpImages);
        ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages = (ArrayList<FetchFeedbackResponse.ReviewImage>) getArguments().getSerializable(IMAGES_TO_SHOW);
        viewPager.setPadding(0, 20, 0, 20);
        viewPager.setAdapter(new DisplayImagesPager(getActivity(), reviewImages));
        viewPager.setCurrentItem(getArguments().getInt(POSITION_TO_OPEN));
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.dimAmount = 0.6f;
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        return rootView;

    }

    public class DisplayImagesPager extends PagerAdapter {

        private Context context;
        private List<FetchFeedbackResponse.ReviewImage> reviewImages;
        private LayoutInflater inflater;

        public DisplayImagesPager(Context context, List<FetchFeedbackResponse.ReviewImage> reviewImages) {
            this.context = context;
            this.reviewImages = reviewImages;
            this.inflater = LayoutInflater.from(context);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView ivReviewImage = (ImageView) inflater.inflate(R.layout.dialog_item_review_image, container, false);
            ViewPager.LayoutParams layoutParams = (ViewPager.LayoutParams) ivReviewImage.getLayoutParams();
            if (reviewImages.get(position).getHeight() != null && reviewImages.get(position).getHeight() > 0)
                layoutParams.height = reviewImages.get(position).getHeight();
            else
                layoutParams.height = FeedUtils.dpToPx(270);
            ivReviewImage.setLayoutParams(layoutParams);
            ivReviewImage.setClickable(true);
            Glide.with(context).load(reviewImages.get(position).getUrl()).override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL).into(ivReviewImage);
            container.addView(ivReviewImage);
            return ivReviewImage;
        }


        @Override
        public int getCount() {
            return reviewImages == null ? 0 : reviewImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            try {
                container.removeView((View) object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
