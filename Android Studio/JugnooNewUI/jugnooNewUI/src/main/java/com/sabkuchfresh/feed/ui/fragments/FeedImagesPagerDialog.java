package com.sabkuchfresh.feed.ui.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sabkuchfresh.feed.utils.FeedUtils;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.sabkuchfresh.utils.DirectionsGestureListener;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;

/**
 * Created by Parminder Saini on 15/05/17.
 */

public class FeedImagesPagerDialog extends DialogFragment {

    public static final String IMAGES_TO_SHOW = "imagesToShow";
    public static final String POSITION_TO_OPEN = "positionToOpen";


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
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.vpImages);
        ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages = (ArrayList<FetchFeedbackResponse.ReviewImage>) getArguments().getSerializable(IMAGES_TO_SHOW);
        viewPager.setPaddingRelative(0, 20, 0, 20);
        viewPager.setAdapter(new DisplayImagesPager(getActivity(), reviewImages));
        viewPager.setCurrentItem(getArguments().getInt(POSITION_TO_OPEN));
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.dimAmount = 0.6f;
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        return rootView;

    }

    private class DisplayImagesPager extends PagerAdapter {

        private Context context;
        private List<FetchFeedbackResponse.ReviewImage> reviewImages;
        private LayoutInflater inflater;

        private DisplayImagesPager(Context context, List<FetchFeedbackResponse.ReviewImage> reviewImages) {
            this.context = context;
            this.reviewImages = reviewImages;
            this.inflater = LayoutInflater.from(context);
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View inflaterView =  inflater.inflate(R.layout.item_feed_pager, container, false);
            final ImageView ivReviewImage = (ImageView) inflaterView.findViewById(R.id.iv_picture);
            View swipeView = inflaterView.findViewById(R.id.swipe_view);
            final ImageView pbar = (ImageView) inflaterView.findViewById(R.id.pbar);
            pbar.post(new Runnable() {
                @Override
                public void run() {
                    AnimationDrawable animationDrawable = (AnimationDrawable) pbar.getDrawable();
                    animationDrawable.start();

                }
            });
            swipeView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gesture.onTouchEvent(event);
                    return false;
                }
            });
            inflaterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });
            ivReviewImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gesture.onTouchEvent(event);
                    return true;
                }
            });


            if(!TextUtils.isEmpty(reviewImages.get(position).getUrl())){
                Glide.with(context).load(reviewImages.get(position).getUrl()).error(R.drawable.ic_fresh_new_placeholder).listener(new MyRequestListener<String, GlideDrawable>(pbar)).into(ivReviewImage);

            }
            else{
                ivReviewImage.setImageResource(R.drawable.ic_fresh_new_placeholder);
            }

            container.addView(inflaterView);
            return inflaterView;
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

    final GestureDetector gesture = new GestureDetector(getActivity(),
            new DirectionsGestureListener(new DirectionsGestureListener.Callback() {
                @Override
                public void topSwipe() {
                    getDialog().dismiss();
                }

                @Override
                public void bottomSwipe() {
                    getDialog().dismiss();
                }

                @Override
                public void leftSwipe() {

                }

                @Override
                public void rightSwipe() {

                }
            }));

    /*
        To hide progress bar when loading is done
     */
    private class MyRequestListener<T,R> implements RequestListener<T,R>{
        private ImageView progressView;

        public MyRequestListener(ImageView imageView) {
            this.progressView = imageView;
        }

        @Override
        public boolean onException(Exception e, T model, Target<R> target, boolean isFirstResource) {
            if(progressView!=null) {
                progressView.setVisibility(View.GONE);
            }
            return false;
        }

        @Override
        public boolean onResourceReady(R resource, T model, Target<R> target, boolean isFromMemoryCache, boolean isFirstResource) {
            if(progressView!=null) {
                ((AnimationDrawable)   progressView.getDrawable()).stop();
                progressView.setVisibility(View.GONE);
            }
            return false;
        }
    }
}
