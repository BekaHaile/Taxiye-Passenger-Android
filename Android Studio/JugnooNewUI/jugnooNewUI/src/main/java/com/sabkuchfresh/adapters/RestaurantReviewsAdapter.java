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

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;


/**
 * Created by Shankar on 02/09/17.
 */
public class RestaurantReviewsAdapter extends RecyclerView.Adapter<RestaurantReviewsAdapter.ViewHolderReview> {

	private FreshActivity activity;
	private ArrayList<FetchFeedbackResponse.Review> restaurantReviews;

	public RestaurantReviewsAdapter(FreshActivity activity, ArrayList<FetchFeedbackResponse.Review> restaurantReviews) {
		this.activity = activity;
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
	public void onBindViewHolder(RestaurantReviewsAdapter.ViewHolderReview holder, int position) {
		try {
			FetchFeedbackResponse.Review review = restaurantReviews.get(position);

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
			holder.vShadowUp.setVisibility(position == 0 ? View.GONE : View.VISIBLE);


			RelativeLayout.LayoutParams paramsRating = (RelativeLayout.LayoutParams) holder.tvRating.getLayoutParams();
			RelativeLayout.LayoutParams paramsRatingTag = (RelativeLayout.LayoutParams) holder.tvReviewTag.getLayoutParams();
			int ratingVis = View.VISIBLE, messageVis = View.VISIBLE;
			if (review.getRating() != null && !TextUtils.isEmpty(review.getReviewDesc())) {
				paramsRating.setMargins(paramsRating.leftMargin, paramsRating.topMargin, paramsRating.rightMargin, 0);
				paramsRatingTag.setMargins(paramsRatingTag.leftMargin, paramsRatingTag.topMargin, paramsRatingTag.rightMargin, 0);
				holder.tvReviewTag.setVisibility(TextUtils.isEmpty(review.getTags())? View.INVISIBLE : View.VISIBLE);
			}
			else if (review.getRating() != null && TextUtils.isEmpty(review.getReviewDesc())) {
				paramsRating.setMargins(paramsRating.leftMargin, paramsRating.topMargin, paramsRating.rightMargin, (int)(ASSL.Yscale()*40f));
				paramsRatingTag.setMargins(paramsRatingTag.leftMargin, paramsRatingTag.topMargin, paramsRatingTag.rightMargin, (int)(ASSL.Yscale()*40f));
				messageVis = View.GONE;
				holder.tvReviewTag.setVisibility(TextUtils.isEmpty(review.getTags())? View.INVISIBLE : View.VISIBLE);
			}
			else if (review.getRating() == null && !TextUtils.isEmpty(review.getReviewDesc())){
				ratingVis = View.GONE;
				holder.tvReviewTag.setVisibility(View.GONE);
			}
			holder.tvRating.setLayoutParams(paramsRating);
			holder.tvReviewTag.setLayoutParams(paramsRatingTag);
			holder.tvRating.setVisibility(ratingVis);
			holder.tvReviewMessage.setVisibility(messageVis);

			if(review.getRating() != null){
				int color = activity.setRatingAndGetColor(holder.tvRating, review.getRating(), review.getColor());
//				int colorAlpha = (color & 0x00FFFFFF) | 0x99000000;
//				holder.tvNameCap.setTextColor(color);
				activity.setTextViewBackgroundDrawableColor(holder.tvNameCap, color);
				if(review.getRatingFlag() == 1){
					holder.tvReviewTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_uparrow, 0);
				} else {
					holder.tvReviewTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_downarrow, 0);
				}
			} else {
				activity.setTextViewBackgroundDrawableColor(holder.tvNameCap, ContextCompat.getColor(activity, R.color.text_color_light));
			}


			holder.ivFeedEdit.setTag(position);
			holder.ivFeedEdit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

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
		public View vShadowUp;
		public RecyclerView rvFeedImages;
		public TextView tvLikeShareCount;
		public ImageView ivFeedEdit, ivFeedShare, ivFeedLike;

		public ViewHolderReview(View itemView) {
			super(itemView);
			vShadowUp = itemView.findViewById(R.id.vShadowUp);
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
			rvFeedImages.setHasFixedSize(false);
			tvLikeShareCount = (TextView) itemView.findViewById(R.id.tvLikeShareCount);
			ivFeedEdit = (ImageView) itemView.findViewById(R.id.ivFeedEdit);
			ivFeedShare = (ImageView) itemView.findViewById(R.id.ivFeedShare);
			ivFeedLike = (ImageView) itemView.findViewById(R.id.ivFeedLike);
		}
	}

}
