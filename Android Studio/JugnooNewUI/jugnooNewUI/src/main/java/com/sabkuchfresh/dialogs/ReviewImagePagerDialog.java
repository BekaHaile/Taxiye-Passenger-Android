package com.sabkuchfresh.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.sabkuchfresh.feed.utils.FeedUtils;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.sabkuchfresh.utils.DirectionsGestureListener;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 2/19/17.
 */

public class ReviewImagePagerDialog extends DialogFragment {

	private FreshActivity activity;
	private View rootView;

	private ImageView ivClose;
	private ViewPager vpImages;
	private TextView tvLikeShareCount;
	private RelativeLayout rlRoot;
	private boolean showDynamicImageHeight;
	private static final String SHOW_DYNAMIC_IMAGE_HEIGHT = "show_dynamic_image_height";



	public static ReviewImagePagerDialog newInstance(int positionImageClicked, int likeIsEnabled, int shareIsEnabled){
		ReviewImagePagerDialog dialog = new ReviewImagePagerDialog();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_POSITION, positionImageClicked);
		bundle.putInt(Constants.KEY_LIKE_IS_ENABLED, likeIsEnabled);
		bundle.putInt(Constants.KEY_SHARE_IS_ENABLED, shareIsEnabled);
		dialog.setArguments(bundle);
		return dialog;
	}


	public static ReviewImagePagerDialog newInstance(int positionImageClicked, ArrayList<FetchFeedbackResponse.ReviewImage> imageList){
		ReviewImagePagerDialog dialog = new ReviewImagePagerDialog();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_POSITION, positionImageClicked);
		bundle.putBoolean(Constants.KEY_HIDE_LIKE_SHARE, true);
		bundle.putSerializable(Constants.LIST_IMAGES, imageList);
		bundle.putBoolean(SHOW_DYNAMIC_IMAGE_HEIGHT, true);
		dialog.setArguments(bundle);
		return dialog;
	}


	public static ReviewImagePagerDialog newInstance(int positionImageClicked, String imageUrl){
		ArrayList<FetchFeedbackResponse.ReviewImage> reviewImages = new ArrayList<>();
		reviewImages.add(new FetchFeedbackResponse.ReviewImage(imageUrl,imageUrl));
		return newInstance(positionImageClicked,reviewImages);
	}


	/*public static ReviewImagePagerDialog newInstance(int positionImageClicked, int likeIsEnabled, int shareIsEnabled, boolean showLikeShare){
		ReviewImagePagerDialog dialog = new ReviewImagePagerDialog();
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_POSITION, positionImageClicked);
		bundle.putInt(Constants.KEY_LIKE_IS_ENABLED, likeIsEnabled);
		bundle.putInt(Constants.KEY_SHARE_IS_ENABLED, shareIsEnabled);
		dialog.setArguments(bundle);
		return dialog;
	}
*/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFragmentTrans);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.dialog_fragment_review_images, container, false);
		int positionImageClicked = getArguments().getInt(Constants.KEY_POSITION, 0);
		int likeIsEnabled = getArguments().getInt(Constants.KEY_LIKE_IS_ENABLED, 1);
		int shareIsEnabled = getArguments().getInt(Constants.KEY_SHARE_IS_ENABLED, 1);
		boolean hideLikeShareCount = getArguments().getBoolean(Constants.KEY_HIDE_LIKE_SHARE, false);
		if(getArguments().containsKey(SHOW_DYNAMIC_IMAGE_HEIGHT)) {
			showDynamicImageHeight = getArguments().getBoolean(SHOW_DYNAMIC_IMAGE_HEIGHT, false);
		}
		ArrayList<FetchFeedbackResponse.ReviewImage> imageArrayList =null;
		if(getArguments().containsKey(Constants.LIST_IMAGES)) {
			imageArrayList = (ArrayList<FetchFeedbackResponse.ReviewImage>) getArguments().getSerializable(Constants.LIST_IMAGES);
		}

		activity = (FreshActivity) getActivity();

		ivClose = (ImageView) rootView.findViewById(R.id.ivClose);
		ivClose.setVisibility(View.GONE);
		vpImages = (ViewPager) rootView.findViewById(R.id.vpImages);
		tvLikeShareCount = (TextView) rootView.findViewById(R.id.tvLikeShareCount);

		try {
			RelativeLayout.LayoutParams vpParams = (RelativeLayout.LayoutParams) vpImages.getLayoutParams();
			vpParams.height = FeedUtils.dpToPx(270);

			if(showDynamicImageHeight &&  imageArrayList!=null && imageArrayList.size()==1 && imageArrayList.get(0).getHeight()!=null && imageArrayList.get(0).getHeight()>0){


				if(imageArrayList.get(0).getHeight()>=(FeedUtils.getScreenHeight(activity) -FeedUtils.dpToPx(40))){
					vpParams.height = FeedUtils.getScreenHeight(activity)-FeedUtils.dpToPx(40);
				}
				else {
					float imageSizeinDp =  Utils.pxToDp(activity,imageArrayList.get(0).getHeight());
					if(imageSizeinDp<110)
						vpParams.height = FeedUtils.dpToPx(110);
					else
						vpParams.height = imageArrayList.get(0).getHeight();

				}





			}

			vpParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
			vpImages.setLayoutParams(vpParams);
			vpImages.setAdapter(new ImagePagerAdapter(activity,imageArrayList==null? activity.getCurrentReview().getImages():imageArrayList));



			ivClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getDialog().dismiss();
				}
			});

			if(hideLikeShareCount){
				tvLikeShareCount.setVisibility(View.GONE);
			} else{
				StringBuilder likeCount = new StringBuilder();
				StringBuilder shareCount = new StringBuilder();
				if (activity.getCurrentReview().getLikeCount() > 1) {
					likeCount.append(activity.getCurrentReview().getLikeCount()).append(" ").append(activity.getString(R.string.likes));
				} else {
					likeCount.append(activity.getCurrentReview().getLikeCount()).append(" ").append(activity.getString(R.string.like));
				}
				if (activity.getCurrentReview().getShareCount() > 1) {
					shareCount.append(activity.getCurrentReview().getShareCount()).append(" ").append(activity.getString(R.string.shares));
				} else {
					shareCount.append(activity.getCurrentReview().getShareCount()).append(" ").append(activity.getString(R.string.share));
				}

				if(likeIsEnabled != 1){
					likeCount.delete(0, likeCount.length());
				}
				if(shareIsEnabled != 1){
					shareCount.delete(0, shareCount.length());
				}
				String seperator = (likeCount.length() > 0 && shareCount.length() > 0) ? " | " : "";
				tvLikeShareCount.setText(likeCount.toString() + seperator + shareCount.toString());

				tvLikeShareCount.setVisibility(View.GONE);
			}


			WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			getDialog().setCancelable(false);
			getDialog().setCanceledOnTouchOutside(false);
			vpImages.setCurrentItem(positionImageClicked);
		} catch (Exception e) {
			e.printStackTrace();
		}

		rlRoot = (RelativeLayout) rootView.findViewById(R.id.rlRoot);
		rlRoot.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gesture.onTouchEvent(event);
			}
		});



		return rootView;
	}


	public class ImagePagerAdapter extends PagerAdapter {

		private Context context;
		private List<FetchFeedbackResponse.ReviewImage> reviewImages;
		private LayoutInflater inflater;

		public ImagePagerAdapter(Context context, List<FetchFeedbackResponse.ReviewImage> reviewImages){
			this.context = context;
			this.reviewImages = reviewImages;
			this.inflater = LayoutInflater.from(context);
		}


		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.dialog_item_review_image, container, false);
			ImageView ivReviewImage = (ImageView) root.findViewById(R.id.ivReviewImage);

			root.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getDialog().dismiss();
				}
			});

			ivReviewImage.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return gesture.onTouchEvent(event);
				}
			});



			if(reviewImages!=null && reviewImages.size()==1 && reviewImages.get(0).getHeight()!=null && reviewImages.get(0).getHeight()>0)
			  Glide.with(activity).load(reviewImages.get(position).getUrl()).placeholder(R.drawable.ic_fresh_item_placeholder).error(R.drawable.ic_fresh_item_placeholder).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(ivReviewImage);
			else
				Glide.with(activity).load(reviewImages.get(position).getUrl()).placeholder(R.drawable.ic_fresh_item_placeholder).error(R.drawable.ic_fresh_item_placeholder).into(ivReviewImage);
			container.addView(root);
			return root;
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
				container.removeView((View)object);
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

}
