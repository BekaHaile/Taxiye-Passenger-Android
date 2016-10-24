package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.AppConstant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Shankar on 7/17/15.
 */
public class FreshCartItemsAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private List<SubItem> subItems;
	private FreshCategoryItemsAdapter.Callback callback;

	private int currentGroupId;
	private String categoryName;

	public FreshCartItemsAdapter(Context context, ArrayList<SubItem> subItems, FreshCategoryItemsAdapter.Callback callback,
								 String categoryName, int currentGroupId) {
		this.context = context;
		this.subItems = subItems;
		this.callback = callback;
		this.categoryName = categoryName;
		this.currentGroupId = currentGroupId;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public synchronized void setResults(ArrayList<SubItem> subItems) {
		this.subItems = subItems;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return subItems == null ? 0 : subItems.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MainViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_fresh_category_item, null);
			holder = new MainViewHolder(convertView, context);

			holder.relative.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(holder.relative);

			convertView.setTag(holder);
		} else {
			holder = (MainViewHolder) convertView.getTag();
		}
		holder.id = position;
		onBindViewHolder(holder, position);

		return convertView;
	}



	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		MainViewHolder mHolder = ((MainViewHolder) holder);
		final SubItem subItem = subItems.get(position);

		mHolder.textViewItemName.setText(subItem.getSubItemName());
		mHolder.textViewItemUnit.setText(subItem.getBaseUnit());
		mHolder.textViewItemPrice.setText(String.format(context.getResources().getString(R.string.rupees_value_format),
				Utils.getMoneyDecimalFormat().format(subItem.getPrice())));

		if (TextUtils.isEmpty(subItem.getOldPrice())) {
			mHolder.textViewItemCost.setVisibility(View.GONE);
		} else {
			mHolder.textViewItemCost.setVisibility(View.VISIBLE);
			mHolder.textViewItemCost.setText(String.format(context.getResources().getString(R.string.rupees_value_format),
					subItem.getOldPrice()));
		}

		mHolder.textViewItemOff.setText(subItem.getSubItemName());

		Log.d("TAG", "currentGroupId = " + currentGroupId);
		if (Data.AppType == AppConstant.ApplicationType.MEALS) {
			if (currentGroupId == subItem.getGroupId()) {
				mHolder.unavilableView.setVisibility(View.GONE);
			} else {
				mHolder.unavilableView.setVisibility(View.VISIBLE);
			}
		} else {
			mHolder.unavilableView.setVisibility(View.GONE);
		}


		mHolder.textViewItemCost.setPaintFlags(mHolder.textViewItemCost.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


		if (TextUtils.isEmpty(subItem.getOfferText())) {
			mHolder.textViewItemOff.setVisibility(View.GONE);
		} else {
			mHolder.textViewItemOff.setVisibility(View.VISIBLE);
			mHolder.textViewItemOff.setText(subItem.getOfferText());
		}

		if (TextUtils.isEmpty(subItem.getBannerText())) {
			mHolder.offerTagLayout.setVisibility(View.GONE);
		} else {
			try {
				mHolder.offerTagLayout.setVisibility(View.VISIBLE);
				int color = Color.parseColor(subItem.getBannerColor());

				mHolder.bannerBg.setColorFilter(color);
				mHolder.offerTag.setText(subItem.getBannerText());
				mHolder.offerTag.setBackgroundColor(color);
				int textColor = Color.parseColor(subItem.getBannerTextColor());//getBannertextColor
				mHolder.offerTag.setTextColor(textColor);
			} catch (Exception e) {

				e.printStackTrace();
				Log.d("asdasd", "subItem.getBannerColor() = " + subItem.getBannerColor());
				Log.d("asdasd", "subItem.getBannertextColor() = " + subItem.getBannerTextColor());

				mHolder.offerTagLayout.setVisibility(View.VISIBLE);
				int color = Color.parseColor("#FD7945");

				mHolder.bannerBg.setColorFilter(color);
				mHolder.offerTag.setText(subItem.getBannerText());
				mHolder.offerTag.setBackgroundColor(color);
				int textColor = Color.parseColor("#FFFFFF");
				mHolder.offerTag.setTextColor(textColor);
			}
		}


		mHolder.textViewQuantity.setText(String.valueOf(subItem.getSubItemQuantitySelected()));
		if (subItem.getSubItemQuantitySelected() == 0) {
			if (subItem.getStock() > 0) {
				mHolder.mAddButton.setVisibility(View.VISIBLE);
				mHolder.textViewOutOfStock.setVisibility(View.GONE);
			} else {
				mHolder.mAddButton.setVisibility(View.GONE);
				mHolder.textViewOutOfStock.setVisibility(View.VISIBLE);
			}
			mHolder.linearLayoutQuantitySelector.setVisibility(View.GONE);
		} else {
			mHolder.mAddButton.setVisibility(View.GONE);
			mHolder.linearLayoutQuantitySelector.setVisibility(View.VISIBLE);
			mHolder.textViewOutOfStock.setVisibility(View.GONE);
		}
//            if (openMode == OpenMode.CART) {
		mHolder.imageViewDelete.setVisibility(View.GONE);
//            } else {
//                mHolder.imageViewDelete.setVisibility(View.GONE);
//            }

		if (!subItem.getSubItemDesc().equalsIgnoreCase("")) {
			mHolder.imageViewMoreInfoSeprator.setVisibility(View.VISIBLE);
			mHolder.textViewMoreInfo.setVisibility(View.VISIBLE);
		} else {
			mHolder.imageViewMoreInfoSeprator.setVisibility(View.GONE);
			mHolder.textViewMoreInfo.setVisibility(View.GONE);
		}
		mHolder.imageViewSep.setBackgroundColor(context.getResources().getColor(R.color.transparent));


		mHolder.imageViewMinus.setTag(position);
		mHolder.imageViewPlus.setTag(position);
		mHolder.imageViewDelete.setTag(position);
		mHolder.mAddButton.setTag(position);

		mHolder.textViewMoreInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogPopup.alertPopupWithCancellable((Activity) context, "", subItem.getSubItemDesc());
			}
		});


		mHolder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int pos = (int) v.getTag();
					if (callback.checkForMinus(pos, subItems.get(pos))) {
						FlurryEventLogger.event(categoryName, FlurryEventNames.DELETE_PRODUCT, subItems.get(pos).getSubItemName());
						subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() > 0 ?
								subItems.get(pos).getSubItemQuantitySelected() - 1 : 0);
						callback.onMinusClicked(pos, subItems.get(pos));

						notifyDataSetChanged();
					} else {
						callback.minusNotDone(pos, subItems.get(pos));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mHolder.mAddButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {

					int pos = (int) v.getTag();
//                        subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock() ?
//                                subItems.get(pos).getSubItemQuantitySelected() + 1 : subItems.get(pos).getStock());
					if (subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
						subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
					} else {
						Utils.showToast(context, context.getResources().getString(R.string.no_more_than, subItems.get(pos).getStock()));
					}
					callback.onPlusClicked(pos, subItems.get(pos));
					FlurryEventLogger.event(categoryName, FlurryEventNames.ADD_PRODUCT, subItems.get(pos).getSubItemName());
					notifyDataSetChanged();
					int appType = Prefs.with(context).getInt(Constants.APP_TYPE, Data.AppType);
					if (appType == AppConstant.ApplicationType.FRESH) {
						MyApplication.getInstance().logEvent(FirebaseEvents.F_ADD, null);
					} else if (appType == AppConstant.ApplicationType.GROCERY) {
						MyApplication.getInstance().logEvent(FirebaseEvents.G_ADD, null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		mHolder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int pos = (int) v.getTag();
//                        subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock() ?
//                                subItems.get(pos).getSubItemQuantitySelected() + 1 : subItems.get(pos).getStock());
					if (subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
						subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
					} else {
						Utils.showToast(context, context.getResources().getString(R.string.no_more_than, subItems.get(pos).getStock()));
					}

					callback.onPlusClicked(pos, subItems.get(pos));
					FlurryEventLogger.event(categoryName, FlurryEventNames.ADD_PRODUCT, subItems.get(pos).getSubItemName());
					notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mHolder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int pos = (int) v.getTag();
					FlurryEventLogger.event(categoryName, FlurryEventNames.DELETE_PRODUCT, subItems.get(pos).getSubItemName());
					subItems.get(pos).setSubItemQuantitySelected(0);
					callback.onDeleteClicked(pos, subItems.get(pos));

					notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		try {
			if (subItem.getSubItemImage() != null && !"".equalsIgnoreCase(subItem.getSubItemImage())) {
				Picasso.with(context).load(subItem.getSubItemImage())
						.placeholder(R.drawable.ic_fresh_item_placeholder)
						.fit()
						.centerCrop()
						.error(R.drawable.ic_fresh_item_placeholder)
						.into(mHolder.imageViewItemImage);
			} else {
				mHolder.imageViewItemImage.setImageResource(R.drawable.ic_fresh_item_placeholder);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	static class MainViewHolder extends RecyclerView.ViewHolder {
		public int id;
		public RelativeLayout relative;
		private ImageView imageViewItemImage, imageViewMinus, imageViewPlus, imageViewDelete, bannerBg, imageViewMoreInfoSeprator, imageViewSep;
		public TextView textViewItemName, textViewItemUnit, textViewItemPrice, textViewQuantity, textViewItemCost, textViewItemOff, offerTag;
		public TextView unavilableView, textViewOutOfStock, textViewMoreInfo;
		public Button mAddButton;
		public LinearLayout linearLayoutQuantitySelector, offerTagLayout;

		public MainViewHolder(View itemView, Context context) {
			super(itemView);
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			linearLayoutQuantitySelector = (LinearLayout) itemView.findViewById(R.id.linearLayoutQuantitySelector);
			offerTagLayout = (LinearLayout) itemView.findViewById(R.id.offer_tag_layout);
			imageViewItemImage = (ImageView) itemView.findViewById(R.id.imageViewItemImage);
			imageViewMinus = (ImageView) itemView.findViewById(R.id.imageViewMinus);
			imageViewPlus = (ImageView) itemView.findViewById(R.id.imageViewPlus);
			imageViewDelete = (ImageView) itemView.findViewById(R.id.imageViewDelete);
			imageViewMoreInfoSeprator = (ImageView) itemView.findViewById(R.id.imageViewMoreInfoSeprator);
			bannerBg = (ImageView) itemView.findViewById(R.id.banner_bg);
			mAddButton = (Button) itemView.findViewById(R.id.add_button);
			textViewMoreInfo = (TextView) itemView.findViewById(R.id.textViewMoreInfo);
			textViewMoreInfo.setTypeface(Fonts.mavenRegular(context));

			unavilableView = (TextView) itemView.findViewById(R.id.unavilable_view);
			unavilableView.setTypeface(Fonts.mavenRegular(context));

			textViewItemName = (TextView) itemView.findViewById(R.id.textViewItemName);
			textViewItemName.setTypeface(Fonts.mavenRegular(context));
			textViewItemUnit = (TextView) itemView.findViewById(R.id.textViewItemUnit);
			textViewItemUnit.setTypeface(Fonts.mavenRegular(context));
			textViewItemPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice);
			textViewItemPrice.setTypeface(Fonts.mavenRegular(context));
			textViewQuantity = (TextView) itemView.findViewById(R.id.textViewQuantity);
			textViewQuantity.setTypeface(Fonts.mavenRegular(context));
			textViewItemCost = (TextView) itemView.findViewById(R.id.textViewItemCost);
			textViewItemCost.setTypeface(Fonts.mavenRegular(context));
			textViewItemOff = (TextView) itemView.findViewById(R.id.textViewItemOff);
			textViewItemOff.setTypeface(Fonts.mavenRegular(context));
			textViewOutOfStock = (TextView) itemView.findViewById(R.id.textViewOutOfStock);
			textViewOutOfStock.setTypeface(Fonts.mavenRegular(context));
			offerTag = (TextView) itemView.findViewById(R.id.offer_tag);
			offerTag.setTypeface(Fonts.mavenRegular(context));
			imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);
		}
	}
}
