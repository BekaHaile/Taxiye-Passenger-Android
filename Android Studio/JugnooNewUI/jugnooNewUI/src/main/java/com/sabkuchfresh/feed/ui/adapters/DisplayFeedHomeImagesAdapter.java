package com.sabkuchfresh.feed.ui.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;


/**
 * Created by Shankar on 7/17/15.
 */
public class DisplayFeedHomeImagesAdapter extends PagerAdapter {

	private FreshActivity activity;
	LayoutInflater mLayoutInflater;
	private ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages;
	private Integer restaurantId;
	private Callback callback;


	public DisplayFeedHomeImagesAdapter(FreshActivity activity, ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages, Integer restaurantId, Callback callback) {
		this.activity = activity;
		this.reviewImages = reviewImages;
		this.callback = callback;
		this.mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.restaurantId = restaurantId;
	}


	public void setList(ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages, Integer restaurantId) {
		this.reviewImages = reviewImages;
		this.restaurantId = restaurantId;
		notifyDataSetChanged();
	}


	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View itemView = mLayoutInflater.inflate(R.layout.list_item_review_image, container, false);

		ImageView ivImage = (ImageView) itemView.findViewById(R.id.ivImage);

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivImage.getLayoutParams();
		params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
		params.height = (int) (ASSL.minRatio() * 244f);
		ivImage.setLayoutParams(params);

		ivImage.setTag(position);
		ivImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int pos = (int) v.getTag();
					if (reviewImages.get(pos).isRestaurantImage()) {
						callback.onRestaurantImageClick(restaurantId);
					} else {
						FeedHomeAdapter.showZoomedPagerDialog(pos, reviewImages, activity);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		FetchFeedbackResponse.ReviewImage reviewImage = reviewImages.get(position);
		Picasso.with(activity).load(reviewImage.getThumbnail())
				.resize((int) (ASSL.minRatio() * 720f), (int) (ASSL.minRatio() * 244f))
				.centerCrop()
//                        .transform(new RoundedCornersTransformation((int)(ASSL.minRatio()*8), 0))
				.placeholder(R.drawable.ic_fresh_item_placeholder)
				.into(ivImage);

		container.addView(itemView);

		return itemView;
	}

	@Override
	public int getCount() {
		return reviewImages != null ? reviewImages.size() : 0;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (object);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((LinearLayout) object);
	}

	public interface Callback {
		void onRestaurantImageClick(Integer restaurantId);
	}


}
