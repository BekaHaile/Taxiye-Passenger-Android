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

import com.sabkuchfresh.commoncalls.ApiRestLikeShareFeedback;
import com.sabkuchfresh.dialogs.ReviewImagePagerDialog;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundedCornersTransformation;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.promotion.ReferralActions;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Shankar on 02/09/17.
 */
public class RestaurantReviewsAdapter extends RecyclerView.Adapter<RestaurantReviewsAdapter.ViewHolderReview> {

	private FreshActivity activity;
	private ArrayList<FetchFeedbackResponse.Review> restaurantReviews;
	private Callback callback;

	public RestaurantReviewsAdapter(FreshActivity activity, Callback callback, ArrayList<FetchFeedbackResponse.Review> restaurantReviews) {
		this.activity = activity;
		this.callback = callback;
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
	public void onBindViewHolder(ViewHolderReview holder, int position) {
		try {
			FetchFeedbackResponse.Review review = restaurantReviews.get(position);

			holder.tvNameCap.setText(!TextUtils.isEmpty(review.getUserName()) ? review.getUserName().substring(0, 1) : "");
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


			RelativeLayout.LayoutParams paramsSep = (RelativeLayout.LayoutParams) holder.vSepBelowMessage.getLayoutParams();
			if (review.getImages() != null && review.getImages().size() > 0) {
				if(review.getImages().size() > 1) {
					holder.rvFeedImages.setVisibility(View.VISIBLE);
					holder.ivFeedImageSingle.setVisibility(View.GONE);
					paramsSep.addRule(RelativeLayout.BELOW, holder.rvFeedImages.getId());
					if (holder.imagesAdapter == null) {
						holder.imagesAdapter = new RestaurantReviewImagesAdapter(activity, review,
								new RestaurantReviewImagesAdapter.Callback() {
									@Override
									public void onImageClick(int positionImageClicked, FetchFeedbackResponse.Review review) {
										try {
											activity.setCurrentReview(review);
											ReviewImagePagerDialog dialog = ReviewImagePagerDialog.newInstance(positionImageClicked);
											dialog.show(activity.getFragmentManager(), ReviewImagePagerDialog.class.getSimpleName());
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
						holder.rvFeedImages.setAdapter(holder.imagesAdapter);
					} else {
						holder.imagesAdapter.setList(review);
						holder.rvFeedImages.setAdapter(holder.imagesAdapter);
					}
				} else {
					holder.rvFeedImages.setVisibility(View.GONE);
					holder.ivFeedImageSingle.setVisibility(View.VISIBLE);
					paramsSep.addRule(RelativeLayout.BELOW, holder.ivFeedImageSingle.getId());
					holder.imagesAdapter = null;
					Picasso.with(activity).load(review.getImages().get(0).getUrl())
							.resize((int) (ASSL.minRatio() * 644f), (int) (ASSL.minRatio() * 310f))
							.centerCrop()
							.transform(new RoundedCornersTransformation((int)(ASSL.minRatio()*8), 0))
							.placeholder(R.drawable.ic_fresh_item_placeholder)
							.into(holder.ivFeedImageSingle);
					holder.ivFeedImageSingle.setTag(position);
					holder.ivFeedImageSingle.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							try {
								int pos = (int) v.getTag();
								activity.setCurrentReview(restaurantReviews.get(pos));
								ReviewImagePagerDialog dialog = ReviewImagePagerDialog.newInstance(0);
								dialog.show(activity.getFragmentManager(), ReviewImagePagerDialog.class.getSimpleName());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			} else {
				holder.rvFeedImages.setVisibility(View.GONE);
				holder.ivFeedImageSingle.setVisibility(View.GONE);
				paramsSep.addRule(RelativeLayout.BELOW, holder.rvFeedImages.getId());
				holder.imagesAdapter = null;
			}
			holder.vSepBelowMessage.setLayoutParams(paramsSep);

			if(review.getLiked() >= 1){
				holder.ivFeedLike.setImageResource(R.drawable.ic_feed_like_active);
			} else {
				holder.ivFeedLike.setImageDrawable(Utils.getSelector(activity, R.drawable.ic_feed_like_normal, R.drawable.ic_feed_like_active));
			}

			if(review.getShared() >= 1){
				holder.ivFeedShare.setImageResource(R.drawable.ic_feed_share_active);
			} else {
				holder.ivFeedShare.setImageDrawable(Utils.getSelector(activity, R.drawable.ic_feed_share_normal, R.drawable.ic_feed_share_active));
			}

			holder.ivFeedEdit.setTag(position);
			holder.ivFeedLike.setTag(position);
			holder.ivFeedShare.setTag(position);
            holder.ivFeedEdit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						int pos = (int) v.getTag();
						callback.onEdit(restaurantReviews.get(pos));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			holder.ivFeedLike.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						int pos = (int) v.getTag();
						callback.onLike(restaurantReviews.get(pos));
						likeShareReview(pos, restaurantReviews.get(pos).getFeedbackId(), ACTION_LIKE);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			holder.ivFeedShare.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						final int pos = (int) v.getTag();
						callback.onShare(restaurantReviews.get(pos));

						StringBuilder sb = new StringBuilder();
						sb.append("I have ordered food from ")
								.append(activity.getVendorOpened().getName())
								.append(", ")
								.append(activity.getVendorOpened().getRestaurantAddress())
								.append(" using Jugnoo!\n");
						if(!TextUtils.isEmpty(restaurantReviews.get(pos).getReviewDesc())){
							sb.append("My experience: ");
							sb.append(restaurantReviews.get(pos).getReviewDesc()).append("\n");
						}

								sb.append("https://jugnoo.in/review/")
								.append(activity.getVendorOpened().getRestaurantId());


						ReferralActions.genericShareDialog(activity, null,
								"Sharing my experience on Jugnoo!",
								sb.toString(), "", true,
								new ReferralActions.ShareDialogCallback() {
							@Override
							public void onShareClicked(String appName) {
								likeShareReview(pos, restaurantReviews.get(pos).getFeedbackId(), ACTION_SHARE);
							}
						}, false);

					} catch (Exception e) {
						e.printStackTrace();
					}
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
		public ImageView ivFeedImageSingle, ivFeedEdit, ivFeedShare, ivFeedLike;
		public View vSepBelowMessage;
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
			rvFeedImages.addOnScrollListener(onScrollListener);
			tvLikeShareCount = (TextView) itemView.findViewById(R.id.tvLikeShareCount);
			ivFeedEdit = (ImageView) itemView.findViewById(R.id.ivFeedEdit);
			ivFeedShare = (ImageView) itemView.findViewById(R.id.ivFeedShare);
			ivFeedLike = (ImageView) itemView.findViewById(R.id.ivFeedLike);
			ivFeedImageSingle = (ImageView) itemView.findViewById(R.id.ivFeedImageSingle);
			vSepBelowMessage = itemView.findViewById(R.id.vSepBelowMessage);
		}
	}

	private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
			callback.onScrollStateChanged(newState);
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
		}
	};


	public interface Callback {
		void onEdit(FetchFeedbackResponse.Review review);
		void onShare(FetchFeedbackResponse.Review review);
		void onLike(FetchFeedbackResponse.Review review);
		void onScrollStateChanged(int newState);
		int getRestaurantId();
	}


	public final String ACTION_LIKE = "LIKE";
	public final String ACTION_SHARE = "SHARE";

	private ApiRestLikeShareFeedback apiRestLikeShareFeedback;
	private void likeShareReview(final int position, int feedback, String action){
		if(apiRestLikeShareFeedback == null){
			apiRestLikeShareFeedback = new ApiRestLikeShareFeedback(activity);
		}
		apiRestLikeShareFeedback.hit(callback.getRestaurantId(), feedback, action, new ApiRestLikeShareFeedback.Callback() {
			@Override
			public void onSuccess(FetchFeedbackResponse.Review review) {
				if(review != null){
					restaurantReviews.set(position, review);
					notifyDataSetChanged();
				}
			}

			@Override
			public void onFailure() {

			}
		});
	}

}
