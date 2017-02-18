package com.sabkuchfresh.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;


/**
 * Created by Shankar on 02/09/17.
 */
public class RestaurantReviewsAdapter extends RecyclerView.Adapter<RestaurantReviewsAdapter.ViewHolderReview> {

	private FreshActivity activity;
	private ArrayList<FetchFeedbackResponse.Review> restaurantReviews;
	private onReviewClick onReviewClick;

	public RestaurantReviewsAdapter(FreshActivity activity,onReviewClick onReviewClick, ArrayList<FetchFeedbackResponse.Review> restaurantReviews) {
		this.activity = activity;
		this.onReviewClick=onReviewClick;
		this.restaurantReviews = restaurantReviews;
	}

	@Override
	public RestaurantReviewsAdapter.ViewHolderReview onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_restaurant_review, parent, false);
		RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
				RecyclerView.LayoutParams.WRAP_CONTENT);
		v.setLayoutParams(layoutParams);
		ASSL.DoMagic(v);
		return new ViewHolderReview(v);
	}

	@Override
	public void onBindViewHolder(final RestaurantReviewsAdapter.ViewHolderReview holder, final int position) {
		try {


			final FetchFeedbackResponse.Review review = restaurantReviews.get(position);

			holder.tvNameCap.setText(review.getUserName().substring(0, 1));
			if (!TextUtils.isEmpty(review.getUserImage())) {
				holder.ivImage.setVisibility(View.VISIBLE);
				Picasso.with(activity).load(review.getUserImage())
						.resize((int) (ASSL.minRatio() * 100f), (int) (ASSL.minRatio() * 100f))
						.centerCrop()
						.transform(new CircleTransform())
						.into(holder.ivImage);
			} else {
				holder.ivImage.setVisibility(View.GONE);
			}


			holder.tvName.setText(review.getUserName());
			holder.tvDateTime.setText(review.getDate());
			holder.tvReviewMessage.setText(review.getReviewDesc());
			holder.tvReviewTag.setText(review.getTags());


			RelativeLayout.LayoutParams paramsRating = (RelativeLayout.LayoutParams) holder.tvRating.getLayoutParams();
			RelativeLayout.LayoutParams paramsRatingTag = (RelativeLayout.LayoutParams) holder.tvReviewTag.getLayoutParams();
			int ratingVis = View.VISIBLE, messageVis = View.VISIBLE;
			if (review.getRating() != null && !TextUtils.isEmpty(review.getReviewDesc())) {
				paramsRating.setMargins(paramsRating.leftMargin, paramsRating.topMargin, paramsRating.rightMargin, 0);
				paramsRatingTag.setMargins(paramsRatingTag.leftMargin, paramsRatingTag.topMargin, paramsRatingTag.rightMargin, 0);
				holder.tvReviewTag.setVisibility(TextUtils.isEmpty(review.getTags()) ? View.INVISIBLE : View.VISIBLE);
			} else if (review.getRating() != null && TextUtils.isEmpty(review.getReviewDesc())) {
				paramsRating.setMargins(paramsRating.leftMargin, paramsRating.topMargin, paramsRating.rightMargin, (int) (ASSL.Yscale() * 40f));
				paramsRatingTag.setMargins(paramsRatingTag.leftMargin, paramsRatingTag.topMargin, paramsRatingTag.rightMargin, (int) (ASSL.Yscale() * 40f));
				messageVis = View.GONE;
				holder.tvReviewTag.setVisibility(TextUtils.isEmpty(review.getTags()) ? View.INVISIBLE : View.VISIBLE);
			} else if (review.getRating() == null && !TextUtils.isEmpty(review.getReviewDesc())) {
				ratingVis = View.GONE;
				holder.tvReviewTag.setVisibility(View.GONE);
			}
			holder.tvRating.setLayoutParams(paramsRating);
			holder.tvReviewTag.setLayoutParams(paramsRatingTag);
			holder.tvRating.setVisibility(ratingVis);
			holder.tvReviewMessage.setVisibility(messageVis);

			if (review.getRating() != null) {
				int color = activity.setRatingAndGetColor(holder.tvRating, review.getRating(), review.getColor());
//				int colorAlpha = (color & 0x00FFFFFF) | 0x99000000;
//				holder.tvNameCap.setTextColor(color);
				activity.setTextViewBackgroundDrawableColor(holder.tvNameCap, color);
				if (review.getRatingFlag() == 1) {
					holder.tvReviewTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_uparrow, 0);
				} else {
					holder.tvReviewTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_downarrow, 0);
				}
			} else {
				activity.setTextViewBackgroundDrawableColor(holder.tvNameCap, ContextCompat.getColor(activity, R.color.text_color_light));
			}

			StringBuilder likeCount = new StringBuilder();
			StringBuilder shareCount = new StringBuilder();
			if (review.getLikeCount() > 1) {
				likeCount.append(review.getLikeCount()).append(" ").append(activity.getString(R.string.likes));
			} else {
				likeCount.append(review.getLikeCount()).append(" ").append(activity.getString(R.string.like));
			}
			if (review.getShareCount() > 1) {
				shareCount.append(review.getShareCount()).append(" ").append(activity.getString(R.string.shares));
			} else {
				shareCount.append(review.getShareCount()).append(" ").append(activity.getString(R.string.share));
			}
			holder.tvLikeShareCount.setText(likeCount.toString() + " | " + shareCount.toString());

			holder.ivFeedEdit.setVisibility(review.getEditable() == 1 ? View.VISIBLE : View.GONE);


		/*	List<FetchFeedbackResponse.ReviewImage> reviewImages = new ArrayList<>();
			FetchFeedbackResponse.ReviewImage reviewImage = new FetchFeedbackResponse.ReviewImage();
			reviewImage.setUrl("http://eskipaper.com/images/serene-landscape-1.jpg");
			reviewImage.setThumbnail("http://eskipaper.com/images/serene-landscape-1.jpg");
			reviewImages.add(reviewImage);
			reviewImage = new FetchFeedbackResponse.ReviewImage();
			reviewImage.setUrl("http://orig04.deviantart.net/2ff9/f/2010/202/6/9/serene_landscape_by_futuramajsp.jpg");
			reviewImage.setThumbnail("http://orig04.deviantart.net/2ff9/f/2010/202/6/9/serene_landscape_by_futuramajsp.jpg");
			reviewImages.add(reviewImage);
			reviewImage = new FetchFeedbackResponse.ReviewImage();
			reviewImage.setUrl("http://wallpaperswide.com/download/serene_autumn_landscape-wallpaper-1280x1024.jpg");
			reviewImage.setThumbnail("http://wallpaperswide.com/download/serene_autumn_landscape-wallpaper-1280x1024.jpg");
			reviewImages.add(reviewImage);
			reviewImage = new FetchFeedbackResponse.ReviewImage();
			reviewImage.setUrl("http://img15.deviantart.net/05d7/i/2012/307/c/5/serene_landscape_by_rambled-d5ju302.jpg");
			reviewImage.setThumbnail("http://img15.deviantart.net/05d7/i/2012/307/c/5/serene_landscape_by_rambled-d5ju302.jpg");
			reviewImages.add(reviewImage);
			review.setImages(reviewImages);
*/
			if (review.getImages() != null && review.getImages().size() > 0) {
				holder.rvFeedImages.setVisibility(View.VISIBLE);
				if(holder.imagesAdapter == null){
					holder.imagesAdapter = new RestaurantReviewImagesAdapter(activity,
							(ArrayList<FetchFeedbackResponse.ReviewImage>) review.getImages(),
							new RestaurantReviewImagesAdapter.Callback() {
						@Override
						public void onImageClick(FetchFeedbackResponse.ReviewImage reviewImage) {

						}
					});
					holder.rvFeedImages.setAdapter(holder.imagesAdapter);
				} else {
					holder.imagesAdapter.setList((ArrayList<FetchFeedbackResponse.ReviewImage>) review.getImages());
					holder.rvFeedImages.setAdapter(holder.imagesAdapter);
				}
				holder.rvFeedImages.startNestedScroll(RecyclerView.SCROLL_AXIS_HORIZONTAL);
			} else {
				holder.rvFeedImages.setVisibility(View.GONE);
				holder.imagesAdapter = null;
			}

			holder.ivFeedLike.setImageResource(review.getLiked() == 1 ?
					R.drawable.ic_feed_like_active : R.drawable.ic_feed_like_normal);

			holder.ivFeedShare.setImageResource(review.getShared() == 1 ?
					R.drawable.ic_feed_share_active : R.drawable.ic_feed_share_normal);

			holder.ivFeedEdit.setTag(position);
			holder.ivFeedLike.setTag(position);
			holder.ivFeedShare.setTag(position);
            holder.ivFeedEdit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onReviewClick.onEdit(restaurantReviews.get((int)v.getTag()));
				}
			});

			holder.ivFeedLike.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onReviewClick.onLike(restaurantReviews.get((int)v.getTag()));


				}
			});

			holder.ivFeedShare.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onReviewClick.onShare(restaurantReviews.get((int)v.getTag()));


				}
			});


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public int getItemCount() {
		return restaurantReviews == null ? 0 : restaurantReviews.size();
	}

	class ViewHolderReview extends RecyclerView.ViewHolder {
		public TextView tvNameCap, tvName, tvDateTime, tvRating, tvReviewTag, tvReviewMessage;
		public ImageView ivImage;
		public RecyclerView rvFeedImages;
		public TextView tvLikeShareCount;
		public ImageView ivFeedEdit, ivFeedShare, ivFeedLike;
		public RestaurantReviewImagesAdapter imagesAdapter = null;

		public ViewHolderReview(View itemView) {
			super(itemView);
			ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
			tvNameCap = (TextView) itemView.findViewById(R.id.tvNameCap);
			tvName = (TextView) itemView.findViewById(R.id.tvName);
			tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
			tvRating = (TextView) itemView.findViewById(R.id.tvRating);
			tvReviewTag = (TextView) itemView.findViewById(R.id.tvReviewTag);
			tvReviewMessage = (TextView) itemView.findViewById(R.id.tvReviewMessage);
			rvFeedImages = (RecyclerView) itemView.findViewById(R.id.rvFeedImages);
			rvFeedImages.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
			rvFeedImages.setItemAnimator(new DefaultItemAnimator());
			rvFeedImages.setHasFixedSize(true);
			tvLikeShareCount = (TextView) itemView.findViewById(R.id.tvLikeShareCount);
			ivFeedEdit = (ImageView) itemView.findViewById(R.id.ivFeedEdit);
			ivFeedShare = (ImageView) itemView.findViewById(R.id.ivFeedShare);
			ivFeedLike = (ImageView) itemView.findViewById(R.id.ivFeedLike);

		}
	}

	private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);

		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
		}
	};


	public interface  onReviewClick{

		void onEdit(FetchFeedbackResponse.Review review);
		void onShare(FetchFeedbackResponse.Review review);
		void onLike(FetchFeedbackResponse.Review review);
	}


}
