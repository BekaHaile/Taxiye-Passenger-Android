package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.fragments.FreshCheckoutMergedFragment;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.retrofit.model.UserCheckoutResponse;
import com.sabkuchfresh.utils.AppConstant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Shankar on 7/17/15.
 */
public class FreshCartItemsAdapter extends BaseAdapter {

	private Activity context;
	private LayoutInflater mInflater;
	private List<SubItem> subItems;
	private Callback callback;
	private boolean checkForCouponApplied;
	private int appType;
	private UserCheckoutResponse.SubscriptionInfo subscription;
	private FreshCheckoutMergedFragment freshCheckoutMergedFragment;

	public FreshCartItemsAdapter(Activity context, ArrayList<SubItem> subItems, boolean checkForCouponApplied,
								 Callback callback, Fragment fragment) {
		this.context = context;
		this.subItems = subItems;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.callback = callback;
		this.checkForCouponApplied = checkForCouponApplied;
		appType = Prefs.with(context).getInt(Constants.APP_TYPE, Data.AppType);
		if(fragment instanceof FreshCheckoutMergedFragment)
			freshCheckoutMergedFragment = (FreshCheckoutMergedFragment) fragment;

	}

	public synchronized void setResults(ArrayList<SubItem> subItems, UserCheckoutResponse.SubscriptionInfo subscription) {
		this.subItems = subItems;
		this.subscription = subscription;
		notifyDataSetChanged();
	}
	public  void resetPrices() {
		if (subItems!=null && subItems.size()>0) {
			for(SubItem item:subItems){
                if(item.getActualPrice()!=null)
                    item.setPrice(item.getActualPrice());

            }
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return subItems == null ? 0 : (subscription != null ? subItems.size()+1 : subItems.size());
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
			convertView = mInflater.inflate(R.layout.list_item_fresh_cart_item, null);
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
		try {
			MainViewHolder mHolder = ((MainViewHolder) holder);
			SubItem subItem;
			if(position == subItems.size()){
				subItem = new SubItem();
				subItem.setSubItemName(subscription.getDescription());
				subItem.setPrice((double)subscription.getPrice());
				subItem.setSubItemQuantitySelected(1);
			} else {
				subItem = subItems.get(position);
			}


			mHolder.textViewItemName.setText(subItem.getSubItemName());
			try {
				mHolder.textViewItemPrice.setText(String.format(context.getResources().getString(R.string.rupees_value_format),
						Utils.getMoneyDecimalFormat().format((subItem.getPrice()*subItem.getSubItemQuantitySelected()))));
			} catch (Exception e) {
				e.printStackTrace();
			}

			if(appType == AppConstant.ApplicationType.FRESH && !TextUtils.isEmpty(subItem.getBaseUnit())) {
				mHolder.textViewItemUnit.setVisibility(View.VISIBLE);
				mHolder.textViewItemUnit.setText(subItem.getBaseUnit()+" x "+subItem.getSubItemQuantitySelected());
			} else {
				mHolder.textViewItemUnit.setVisibility(View.GONE);
			}

			mHolder.textViewQuantity.setText(String.valueOf(subItem.getSubItemQuantitySelected()));
			mHolder.imageViewPlus.setImageResource(R.drawable.ic_plus_dark_selector);
			mHolder.imageViewPlus.setEnabled(!(freshCheckoutMergedFragment!=null && freshCheckoutMergedFragment.isPriceMisMatchDialogShowing()));
			mHolder.imageViewMinus.setEnabled(!(freshCheckoutMergedFragment!=null && freshCheckoutMergedFragment.isPriceMisMatchDialogShowing()));
			if(position == getCount()-1){
				mHolder.imageViewSep.setBackgroundColor(context.getResources().getColor(R.color.transparent));
			} else {
				mHolder.imageViewSep.setBackgroundColor(context.getResources().getColor(R.color.stroke_light_grey_alpha));
			}

			try {
				if(position == subItems.size()){
					Picasso.with(context).load(R.drawable.star)
							.placeholder(R.drawable.ic_fresh_item_placeholder)
							.fit()
							.centerCrop()
							.error(R.drawable.ic_fresh_item_placeholder)
							.into(mHolder.imageViewItemImage);
				} else {
					if (!TextUtils.isEmpty(subItem.getSubItemImage())) {
						mHolder.imageViewItemImage.setVisibility(View.VISIBLE);
						Picasso.with(context).load(subItem.getSubItemImage())
								.placeholder(R.drawable.ic_fresh_item_placeholder)
								.fit()
								.centerCrop()
								.error(R.drawable.ic_fresh_item_placeholder)
								.into(mHolder.imageViewItemImage);
					} else {
						mHolder.imageViewItemImage.setVisibility(View.GONE);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			mHolder.imageViewFoodType.setVisibility(appType == AppConstant.ApplicationType.MENUS ? View.VISIBLE : View.GONE);
			mHolder.imageViewFoodType.setImageResource(subItem.getIsVeg() == 1 ? R.drawable.veg : R.drawable.nonveg);
			RelativeLayout.LayoutParams paramsFT = (RelativeLayout.LayoutParams) mHolder.imageViewFoodType.getLayoutParams();
			RelativeLayout.LayoutParams paramsLLC = (RelativeLayout.LayoutParams) mHolder.linearLayoutContent.getLayoutParams();
			if(mHolder.imageViewFoodType.getVisibility() == View.VISIBLE && mHolder.imageViewItemImage.getVisibility() == View.GONE){
				paramsFT.setMargins(0, (int)(ASSL.Yscale()*25f), 0, 0);
				paramsFT.setMarginStart(0);
				paramsFT.setMarginEnd(0);
				paramsLLC.setMargins((int)(ASSL.Xscale()*20f), 0, 0, 0);
				paramsLLC.setMarginStart((int)(ASSL.Xscale()*20f));
				paramsLLC.setMarginEnd(0);
			} else {
				paramsFT.setMargins((int)(ASSL.Xscale()*2f), (int)(ASSL.Yscale()*2f), 0, 0);
				paramsFT.setMarginStart((int)(ASSL.Xscale()*2f));
				paramsFT.setMarginEnd(0);
				paramsLLC.setMargins((int)(ASSL.Xscale()*30f), 0, 0, 0);
				paramsLLC.setMarginStart((int)(ASSL.Xscale()*30f));
				paramsLLC.setMarginEnd(0);
			}
			mHolder.imageViewFoodType.setLayoutParams(paramsFT);
			mHolder.linearLayoutContent.setLayoutParams(paramsLLC);


			mHolder.imageViewMinus.setTag(position);
			mHolder.imageViewPlus.setTag(position);

			mHolder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					try {

						if(freshCheckoutMergedFragment!=null &&freshCheckoutMergedFragment.isPriceMisMatchDialogShowing())
							return;

						final int pos = (int) v.getTag();
//						if(checkForCouponApplied && callback.getSelectedCoupon() != null && callback.getSelectedCoupon().getId() > 0){
//							DialogPopup.alertPopupTwoButtonsWithListeners(context, "",
//									context.getString(R.string.coupon_remove_reapply_before_checkout),
//									context.getString(R.string.ok),
//									context.getString(R.string.cancel),
//									new View.OnClickListener(){
//										@Override
//										public void onClick(View v) {
//											doMinus(pos);
//											callback.removeCoupon();
//										}
//									},
//									new View.OnClickListener(){
//										@Override
//										public void onClick(View v) {
//
//										}
//									}, true, false);
//						} else{
							doMinus(pos);
//						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			mHolder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						if(freshCheckoutMergedFragment!=null &&freshCheckoutMergedFragment.isPriceMisMatchDialogShowing())
							return;

						final int pos = (int) v.getTag();
//						if(checkForCouponApplied && callback.getSelectedCoupon() != null && callback.getSelectedCoupon().getId() > 0){
//							DialogPopup.alertPopupTwoButtonsWithListeners(context, "",
//									context.getString(R.string.coupon_remove_reapply_before_checkout),
//									context.getString(R.string.ok),
//									context.getString(R.string.cancel),
//									new View.OnClickListener(){
//										@Override
//										public void onClick(View v) {
//											doPlus(pos);
//											callback.removeCoupon();
//										}
//									},
//									new View.OnClickListener(){
//										@Override
//										public void onClick(View v) {
//
//										}
//									}, true, false);
//						} else{
							doPlus(pos);
//						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doMinus(int pos){
		if(subscription != null && pos > subItems.size()-1){
			callback.deleteStarSubscription();
		} else {
			if (callback.checkForMinus(pos, subItems.get(pos))) {
				subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() > 0 ?
						subItems.get(pos).getSubItemQuantitySelected() - 1 : 0);
				callback.onMinusClicked(pos, subItems.get(pos));

				notifyDataSetChanged();
			} else {
				callback.minusNotDone(pos, subItems.get(pos));
			}
		}
	}

	private void doPlus(int pos){
		if(subscription != null && pos > subItems.size()-1){
			Utils.showToast(context, context.getResources().getString(R.string.no_more_than_star, 1));
		} else {
			if (subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
				subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
				callback.onPlusClicked(pos, subItems.get(pos));
				notifyDataSetChanged();
			} else {
				Utils.showToast(context, context.getResources().getString(R.string.no_more_than, subItems.get(pos).getStock()));
			}
		}


	}

	static class MainViewHolder extends RecyclerView.ViewHolder {
		public int id;
		public RelativeLayout relative;
		public LinearLayout linearLayoutContent;
		private ImageView imageViewItemImage, imageViewSep, imageViewMinus, imageViewPlus, imageViewFoodType;
		public TextView textViewItemName, textViewItemPrice, textViewQuantity, textViewItemUnit;

		public MainViewHolder(View itemView, Context context) {
			super(itemView);
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			linearLayoutContent = (LinearLayout) itemView.findViewById(R.id.linearLayoutContent);
			imageViewItemImage = (ImageView) itemView.findViewById(R.id.imageViewItemImage);
			imageViewFoodType = (ImageView) itemView.findViewById(R.id.imageViewFoodType);
			imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);
			imageViewMinus = (ImageView) itemView.findViewById(R.id.imageViewMinus);
			imageViewPlus = (ImageView) itemView.findViewById(R.id.imageViewPlus);

			textViewItemName = (TextView) itemView.findViewById(R.id.textViewItemName); textViewItemName.setTypeface(Fonts.mavenMedium(context));
			textViewItemPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice); textViewItemPrice.setTypeface(Fonts.mavenMedium(context));
			textViewQuantity = (TextView) itemView.findViewById(R.id.textViewQuantity); textViewQuantity.setTypeface(Fonts.mavenMedium(context));
			textViewItemUnit = (TextView)itemView.findViewById(R.id.textViewItemUnit); textViewItemUnit.setTypeface(Fonts.mavenRegular(context));
		}
	}

	public interface Callback{
		void onPlusClicked(int position, SubItem subItem);
		void onMinusClicked(int position, SubItem subItem);
		boolean checkForMinus(int position, SubItem subItem);
		void minusNotDone(int position, SubItem subItem);
		void deleteStarSubscription();
		PromoCoupon getSelectedCoupon();
		void removeCoupon();
	}
}
