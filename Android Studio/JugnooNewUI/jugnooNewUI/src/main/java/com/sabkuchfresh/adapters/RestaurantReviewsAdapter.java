package com.sabkuchfresh.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.commoncalls.ApiRestLikeShareFeedback;
import com.sabkuchfresh.dialogs.ReviewImagePagerDialog;
import com.sabkuchfresh.feed.ui.adapters.FeedHomeAdapter;
import com.sabkuchfresh.feed.ui.views.animateheartview.LikeButton;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.FetchFeedbackResponse;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundedCornersTransformation;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.AppLinkIndex;
import product.clicklabs.jugnoo.promotion.ReferralActions;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.BranchMetricsUtils;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Shankar on 02/09/17.
 */
public class RestaurantReviewsAdapter extends RecyclerView.Adapter<RestaurantReviewsAdapter.ViewHolderReview> {

	private FreshActivity activity;
	private ArrayList<FetchFeedbackResponse.Review> restaurantReviews;
	private Callback callback;
	private boolean viewOnly;

	public RestaurantReviewsAdapter(FreshActivity activity, Callback callback, ArrayList<FetchFeedbackResponse.Review> restaurantReviews, boolean viewOnly) {
		this.activity = activity;
		this.callback = callback;
		this.restaurantReviews = restaurantReviews;
		this.viewOnly = viewOnly;
	}

	@Override
	public RestaurantReviewsAdapter.ViewHolderReview onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_restaurant_review, parent, false);
		return new ViewHolderReview(v);
	}

	@Override
	public void onBindViewHolder(ViewHolderReview holder, int position) {
		try {
			FetchFeedbackResponse.Review review = restaurantReviews.get(position);

			// decide whether to show reviews footer ( when reviews < ratings )
			// and show only on all reviews screen ( viewOnly = false )
			if((position == getItemCount()-1) && !viewOnly &&
					getItemCount() < callback.getVendor().getReviewCount() ){
				int missingReviews = (int) callback.getVendor().getReviewCount() - getItemCount();
				holder.tvReviewFooter.setVisibility(View.VISIBLE);
				holder.tvReviewFooter.setText(String.format(activity.getString(R.string.more_reviews_not_shown_text)
				,String.valueOf(missingReviews)));
			}
			else {
				holder.tvReviewFooter.setVisibility(View.GONE);
			}

			holder.tvNameCap.setText(!TextUtils.isEmpty(review.getUserName()) ? review.getUserName().substring(0, 1).toUpperCase() : "");
			if (!TextUtils.isEmpty(review.getUserImage())) {
				holder.ivImage.setVisibility(View.VISIBLE);
				Picasso.with(activity).load(review.getUserImage())
						.resize(activity.getResources().getDimensionPixelSize(R.dimen.dp_50),
								activity.getResources().getDimensionPixelSize(R.dimen.dp_50))
						.centerCrop()
						.transform(new CircleTransform())
						.into(holder.ivImage);
			} else {
				holder.ivImage.setVisibility(View.GONE);
			}


			holder.tvName.setText(review.getUserName());
			try {
				holder.tvDateTime.setText(FeedHomeAdapter.getTimeToDisplay(review.getPostTime(), activity.isTimeAutomatic));
			} catch (Exception e) {
				holder.tvDateTime.setText(review.getDate());
			}

			holder.tvReviewMessage.setText(review.getReviewDesc());
			holder.tvReviewTag.setText(review.getTags());
			holder.tvReviewTag.setVisibility(TextUtils.isEmpty(review.getTags()) ? View.GONE : View.VISIBLE);
			holder.tvRating.setVisibility(review.getRating() == null ? View.GONE : View.VISIBLE);
			holder.llRatingMain.setVisibility(review.getRating() == null ? View.GONE : View.VISIBLE);
			holder.tvReviewMessage.setVisibility(TextUtils.isEmpty(review.getReviewDesc()) ? View.GONE : View.VISIBLE);

			if (review.getRating() != null) {
				int color = activity.setRatingAndGetColor(holder.tvRating, review.getRating(), review.getColor(), true);
				activity.setTextViewBackgroundDrawableColor(holder.tvNameCap, color);
				if (review.getRatingFlag() == 1) {
					holder.tvReviewTag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumbsup_small, 0, 0, 0);
				} else {
					holder.tvReviewTag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumbsdown_small, 0, 0, 0);
				}
				activity.setRestaurantRatingStarsToLL(holder.llRating, null,
						review.getRating(), R.drawable.ic_half_star_green_grey, R.drawable.ic_star_grey
						, null, 0, Color.parseColor(review.getColor()));

			} else {
				activity.setTextViewBackgroundDrawableColor(holder.tvNameCap, ContextCompat.getColor(activity, R.color.text_color_light));
			}


			RelativeLayout.LayoutParams paramsRL = (RelativeLayout.LayoutParams) holder.rlRestaurantReply.getLayoutParams();
			if (review.getImages() != null && review.getImages().size() > 0) {
				if(review.getImages().size() > 1) {
					holder.rvFeedImages.setVisibility(View.VISIBLE);
					holder.ivFeedImageSingle.setVisibility(View.GONE);
					paramsRL.addRule(RelativeLayout.BELOW, holder.rvFeedImages.getId());
					if (holder.imagesAdapter == null) {
						holder.imagesAdapter = new RestaurantReviewImagesAdapter(activity, review,review.getImages(),false,
								new RestaurantReviewImagesAdapter.Callback() {
									@Override
									public void onImageClick(int positionImageClicked, FetchFeedbackResponse.Review review) {
										try {
											activity.setCurrentReview(review);
											ReviewImagePagerDialog dialog = ReviewImagePagerDialog.newInstance(positionImageClicked, callback.getLikeIsEnabled(), callback.getShareIsEnabled());
											dialog.show(activity.getFragmentManager(), ReviewImagePagerDialog.class.getSimpleName());
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
						holder.rvFeedImages.setAdapter(holder.imagesAdapter);
					} else {
						holder.imagesAdapter.setList(review,review.getImages());
						holder.rvFeedImages.setAdapter(holder.imagesAdapter);
					}
				} else {
					holder.rvFeedImages.setVisibility(View.GONE);
					holder.ivFeedImageSingle.setVisibility(View.VISIBLE);
					paramsRL.addRule(RelativeLayout.BELOW, holder.ivFeedImageSingle.getId());
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
								ReviewImagePagerDialog dialog = ReviewImagePagerDialog.newInstance(0, callback.getLikeIsEnabled(), callback.getShareIsEnabled());
								dialog.show(activity.getFragmentManager(), ReviewImagePagerDialog.class.getSimpleName());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
				if(review.getReviewDesc().length() > 80) {
					SpannableStringBuilder ssb;
					int end;
					if(review.isExpanded()){
						ssb = new SpannableStringBuilder(activity.getString(R.string.view_less));
						end = review.getReviewDesc().length();
					} else {
						ssb = new SpannableStringBuilder(activity.getString(R.string.view_more));
						end = 80;
					}
					holder.tvReviewMessage.setText(review.getReviewDesc().substring(0, end));
					ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.theme_color)), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					holder.tvReviewMessage.append(" ");
					holder.tvReviewMessage.append(ssb);
				}

			} else {
				holder.rvFeedImages.setVisibility(View.GONE);
				holder.ivFeedImageSingle.setVisibility(View.GONE);
				paramsRL.addRule(RelativeLayout.BELOW, holder.rvFeedImages.getId());
				holder.imagesAdapter = null;
			}
			holder.rlRestaurantReply.setLayoutParams(paramsRL);
			holder.tvReviewMessage.setTag(position);
			holder.tvReviewMessage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						int pos = (int) v.getTag();
						FetchFeedbackResponse.Review item1 = restaurantReviews.get(pos);
						TextView tv = (TextView) v;
						if(item1.getReviewDesc().length() > 80) {
							if (tv.getText().toString().length() > 90) {
								item1.setExpanded(false);
							} else {
								item1.setExpanded(true);
							}
							notifyItemChanged(pos);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			if(!viewOnly) {
				if (!TextUtils.isEmpty(review.getReply())) {
					holder.rlRestaurantReply.setVisibility(View.VISIBLE);
					holder.tvRestName.setText(callback.getVendor().getName());
					holder.tvRestReply.setText(review.getReply());
					holder.tvReplyDateTime.setText(FeedHomeAdapter.getTimeToDisplay(review.getReplyTime(), activity.isTimeAutomatic));
					Picasso.with(activity).load(callback.getVendor().getImage())
							.resize(activity.getResources().getDimensionPixelSize(R.dimen.dp_50),
									activity.getResources().getDimensionPixelSize(R.dimen.dp_50))
							.centerCrop()
							.transform(new CircleTransform())
							.into(holder.ivRestImage);
				} else {
					holder.rlRestaurantReply.setVisibility(View.GONE);
				}
				holder.tvLikeShareCount.setVisibility(View.VISIBLE);


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
				holder.ivFeedLike.setVisibility(callback.getLikeIsEnabled() == 1 ? View.VISIBLE : View.GONE);
				holder.ivFeedShare.setVisibility(callback.getShareIsEnabled() == 1 ? View.VISIBLE : View.GONE);
				if (callback.getLikeIsEnabled() != 1) {
					likeCount.delete(0, likeCount.length());
				}
				if (callback.getShareIsEnabled() != 1) {
					shareCount.delete(0, shareCount.length());
				}
				String seperator = (likeCount.length() > 0 && shareCount.length() > 0) ? " | " : "";
				holder.tvLikeShareCount.setText(likeCount.toString() + seperator + shareCount.toString());
//			holder.tvLikeShareCount.setVisibility((likeCount.length() == 0 && shareCount.length() == 0) ? View.INVISIBLE : View.VISIBLE);

				holder.ivFeedLike.setLiked(review.getIsLiked() >= 1);
//			if(review.getIsLiked() >= 1){
//				holder.ivFeedLike.setImageResource(R.drawable.ic_feed_like_active);
//			} else {
//				holder.ivFeedLike.setImageResource(R.drawable.ic_feed_like_normal);
//			}
				if (review.getIsShared() >= 1) {
					holder.ivFeedShare.setImageResource(R.drawable.ic_feed_share_active);
				} else {
					holder.ivFeedShare.setImageResource(R.drawable.ic_feed_share_normal);
				}

				holder.ivFeedEdit.setVisibility(review.getIsEditable() == 1 ? View.VISIBLE : View.GONE);
				holder.ivFeedEdit.setImageDrawable(Utils.getSelector(activity, R.drawable.ic_feed_edit, R.drawable.ic_feed_edit_pressed));

				RelativeLayout.LayoutParams paramsVShadowDown = (RelativeLayout.LayoutParams) holder.vShadowDown.getLayoutParams();
				if (review.getIsEditable() == 1) {
					paramsVShadowDown.addRule(RelativeLayout.BELOW, holder.ivFeedEdit.getId());
				} else {
					paramsVShadowDown.addRule(RelativeLayout.BELOW, holder.tvLikeShareCount.getId());
				}
				holder.vShadowDown.setLayoutParams(paramsVShadowDown);
				holder.vShadowDown.setVisibility(View.VISIBLE);
			} else {
				holder.rlRestaurantReply.setVisibility(View.GONE);
				holder.tvLikeShareCount.setVisibility(View.GONE);
				holder.ivFeedLike.setVisibility(View.GONE);
				holder.ivFeedShare.setVisibility(View.GONE);
				holder.ivFeedEdit.setVisibility(View.GONE);
				holder.vShadowDown.setVisibility(View.GONE);
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
						final FetchFeedbackResponse.Review review1 = restaurantReviews.get(pos);
						callback.onShare(review1);

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(Constants.KEY_DEEPINDEX, String.valueOf(AppLinkIndex.MENUS_PAGE.getOrdinal()));
						map.put(Constants.KEY_RESTAURANT_ID, String.valueOf(activity.getVendorOpened().getRestaurantId()));

						BranchMetricsUtils.getBranchLink(activity, new BranchMetricsUtils.BranchMetricsEventHandler() {
							@Override
							public void onBranchLinkCreated(String link) {
								String content = "";
								if(review1.getIsEditable() == 1 && !TextUtils.isEmpty(callback.getShareTextSelf())){
										content = callback.getShareTextSelf()
												.replace("{{{restaurant_name}}", activity.getVendorOpened().getName())
												.replace("{{{restaurant_address}}", activity.getVendorOpened().getAddress())
												.replace("{{{review_desc}}}", "\n"+review1.getReviewDesc())
												.replace("{{{link}}}", "\n"+link);
								} else if(review1.getIsEditable() == 0 && !TextUtils.isEmpty(callback.getShareTextOther())) {
									content = callback.getShareTextOther()
											.replace("{{{restaurant_name}}", activity.getVendorOpened().getName())
											.replace("{{{restaurant_address}}", activity.getVendorOpened().getAddress())
											.replace("{{{review_desc}}}", "\n"+review1.getReviewDesc())
											.replace("{{{link}}}", "\n"+link);
								}

								if(TextUtils.isEmpty(content)){
									StringBuilder sb = new StringBuilder();
									if(review1.getIsEditable() == 1){
										sb.append("Here's my experience of ");
									} else {
										sb.append("Have a look at this experience of ");
									}
									sb.append(activity.getVendorOpened().getName())
											.append(", ")
											.append(activity.getVendorOpened().getAddress())
											.append(" @ Jugnoo!\n\n");
									if(!TextUtils.isEmpty(review1.getReviewDesc())){
										sb.append(review1.getReviewDesc()).append("\n");
									}
									sb.append(link);
									content = sb.toString();
								}

								ReferralActions.genericShareDialog(activity, null,
										"Sharing experience of "+activity.getVendorOpened().getName()+" @ Jugnoo!",
										content, link, activity.getVendorOpened().getImage(), true,
										new ReferralActions.ShareDialogCallback() {
											@Override
											public void onShareClicked(String appName) {
												likeShareReview(pos, restaurantReviews.get(pos).getFeedbackId(), ACTION_SHARE);
											}
										}, false);
							}

							@Override
							public void onBranchError(String error) {
								DialogPopup.alertPopup(activity, "", activity.getString(R.string.connection_lost_desc));
							}
						}, activity.getVendorOpened().getName(),
								"https://jugnoo.in",
								activity.getVendorOpened().getImage(),
								BranchMetricsUtils.BRANCH_CHANNEL_MENUS_REVIEW_SHARE,
								map);



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
		public ImageView ivFeedImageSingle, ivFeedEdit, ivFeedShare;
		public LikeButton ivFeedLike;
		public View vSepBelowMessage, vShadowDown;
		public RestaurantReviewImagesAdapter imagesAdapter = null;
		public RelativeLayout rlRestaurantReply;
		public TextView tvRestName, tvRestReply, tvReplyDateTime;
		public ImageView ivRestImage;
		TextView tvReviewFooter;
		LinearLayout llRating,llRatingMain;

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
			ivFeedLike = (LikeButton) itemView.findViewById(R.id.ivFeedLike);
			ivFeedImageSingle = (ImageView) itemView.findViewById(R.id.ivFeedImageSingle);
			vSepBelowMessage = itemView.findViewById(R.id.vSepBelowMessage);
			vShadowDown = itemView.findViewById(R.id.vShadowDown);
			rlRestaurantReply = (RelativeLayout) itemView.findViewById(R.id.rlRestaurantReply);
			tvRestName = (TextView) itemView.findViewById(R.id.tvRestName); tvRestName.setTypeface(tvRestName.getTypeface(), Typeface.BOLD);
			tvRestReply = (TextView) itemView.findViewById(R.id.tvRestReply);
			tvReplyDateTime = (TextView) itemView.findViewById(R.id.tvReplyDateTime);
			ivRestImage = (ImageView) itemView.findViewById(R.id.ivRestImage);
			tvReviewFooter = (TextView) itemView.findViewById(R.id.tvReviewFooter);
			llRating = (LinearLayout)itemView.findViewById(R.id.llRating);
			llRatingMain = (LinearLayout)itemView.findViewById(R.id.llRatingMain);
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
		String getShareTextSelf();
		String getShareTextOther();
		int getShareIsEnabled();
		int getLikeIsEnabled();
		RecyclerView getRecyclerView();
		MenusResponse.Vendor getVendor();
	}


	public final String ACTION_LIKE = "LIKE";
	public final String ACTION_SHARE = "SHARE";

	private ApiRestLikeShareFeedback apiRestLikeShareFeedback;
	private void likeShareReview(int position, int feedback, final String action){
		if(apiRestLikeShareFeedback == null){
			apiRestLikeShareFeedback = new ApiRestLikeShareFeedback(activity);
		}
		apiRestLikeShareFeedback.hit(callback.getRestaurantId(), feedback, action, new ApiRestLikeShareFeedback.Callback() {
			@Override
			public void onSuccess(FetchFeedbackResponse.Review review, int position) {
				if(review != null){
					restaurantReviews.set(position, review);
					if(action.equalsIgnoreCase(ACTION_LIKE)
							&& callback != null && callback.getRecyclerView() != null) {
						ViewHolderReview holder = ((ViewHolderReview) callback.getRecyclerView().findViewHolderForAdapterPosition(position));
						if (holder != null) {
							LikeButton likeButton = holder.ivFeedLike;
							likeButton.onClick(likeButton);
						}
					}
					notifyItemChanged(position);
				}
			}

			@Override
			public void onFailure() {

			}
		}, position);
	}

}
