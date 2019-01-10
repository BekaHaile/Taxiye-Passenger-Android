package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.retrofit.model.UserCheckoutResponse;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;
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
public class MenusCartItemsAdapter extends BaseAdapter {

	private Activity context;
	private LayoutInflater mInflater;
	private List<Item> items;
	private Callback callback;
	private boolean checkForCouponApplied;
	private int appType;
	private String currencyCode;
	private String currency;
	private UserCheckoutResponse.SubscriptionInfo subscription;

	public MenusCartItemsAdapter(Activity context, ArrayList<Item> items, boolean checkForCouponApplied,
								 Callback callback,  String currencyCode, String currency) {
		this.context = context;
		this.items = items;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.callback = callback;
		this.currencyCode = currencyCode;
		this.currency = currency;
		this.checkForCouponApplied = checkForCouponApplied;
		appType = Prefs.with(context).getInt(Constants.APP_TYPE, Data.AppType);
	}

	public synchronized void setResults(ArrayList<Item> items, UserCheckoutResponse.SubscriptionInfo subscription, String currencyCode, String currency) {
		this.items = items;
		this.subscription = subscription;
		this.currencyCode = currencyCode;
		this.currency = currency;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		int count = 0;
		for(Item item : items){
			for(ItemSelected itemSelected : item.getItemSelectedList()){
				if(itemSelected.getQuantity() > 0){
					count++;
				}
			}
		}
		if(subscription != null){
			count++;
		}
		return count;
	}

	@Override
	public MenuCartViewModel getItem(int position) {
		MenuCartViewModel mcv = new MenuCartViewModel();
		int count = 0;
		for(int i=0; i<items.size(); i++){
			Item item = items.get(i);
			for(int j=0; j<item.getItemSelectedList().size(); j++){
				if(count == position){
					ItemSelected itemSelected = item.getItemSelectedList().get(j);
					item.generateCustomizeText(itemSelected);
//					if(TextUtils.isEmpty(itemSelected.getCustomizeText())
//							&& itemSelected.getCustomizeItemSelectedList().size() > 0){
//						StringBuilder sb = new StringBuilder();
//						for(CustomizeItemSelected customizeItemSelected : itemSelected.getCustomizeItemSelectedList()){
//							CustomizeItem customizeItem = new CustomizeItem();
//							customizeItem.setCustomizeId(customizeItemSelected.getCustomizeId());
//							int index = item.getCustomizeItem().indexOf(customizeItem);
//							if(index > -1){
//								customizeItem = item.getCustomizeItem().get(index);
//								StringBuilder sbOp = new StringBuilder();
//								for(Integer option : customizeItemSelected.getCustomizeOptions()){
//									CustomizeOption customizeOption = new CustomizeOption();
//									customizeOption.setCustomizeOptionId(option);
//									int index1 = customizeItem.getCustomizeOptions().indexOf(customizeOption);
//									if(index1 > -1){
//										customizeOption = customizeItem.getCustomizeOptions().get(index1);
//										if(sbOp.length() > 0){
//											sbOp.append(", ");
//										}
//										sbOp.append(customizeOption.getCustomizeOptionName());
//									}
//								}
//								if(sb.length() > 0){
//									sb.append("\n");
//								}
//								sb.append(customizeItem.getCustomizeItemName()).append(": ").append(sbOp);
//							}
//						}
//						itemSelected.setCustomizeText(sb.toString());
//					}
					mcv.setName(item.getItemName());
					mcv.setIsVeg(item.getIsVeg());
					mcv.setPrice(itemSelected.getTotalPrice());
					mcv.setQuantity(itemSelected.getQuantity());
					mcv.setCustomizeText(itemSelected.getCustomizeText());
					mcv.setItemInstructions(itemSelected.getItemInstructions());
					return mcv;
				}
				count++;
			}
		}
		return mcv;
	}

	private Pair<Integer, Integer> doItemPlusMinus(int position, boolean plus){
		int count = 0;
		for(int i=0; i<items.size(); i++){
			Item item = items.get(i);
			for(int j=0; j<item.getItemSelectedList().size(); j++){
				if(count == position){
					ItemSelected itemSelected = item.getItemSelectedList().get(j);
					if(plus){
						if (item.getTotalQuantity() < 50) {
							itemSelected.setQuantity(itemSelected.getQuantity() + 1);
							return new Pair<>(i, item.getTotalQuantity());
						} else {
							Utils.showToast(context, context.getString(R.string.order_quantity_limited));
						}
					} else {
						if(item.getTotalQuantity() > 0) {
							itemSelected.setQuantity(itemSelected.getQuantity() - 1);
							if(itemSelected.getQuantity() == 0){
								item.getItemSelectedList().remove(j);
							}
							return new Pair<>(i, item.getTotalQuantity());
						}
					}
					return null;
				}
				count++;
			}
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MainViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_menus_cart_item, null);
			holder = new MainViewHolder(convertView, context);

			holder.relative.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
					AbsListView.LayoutParams.WRAP_CONTENT));
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
		MenuCartViewModel mcv;

		if(isPositionSubscription(position)){
			mcv = new MenuCartViewModel();
			mcv.setName(subscription.getDescription());
			mcv.setPrice((double)subscription.getPrice());
			mcv.setQuantity(1);
			mcv.setIsVeg(-1);
		} else {
			mcv = getItem(position);
		}

		mHolder.textViewItemName.setText(mcv.getName());


		mHolder.textViewItemPrice.setText(com.sabkuchfresh.utils.Utils.formatCurrencyAmount(mcv.getPrice(), currencyCode, currency));

		mHolder.textViewQuantity.setText(String.valueOf(mcv.getQuantity()));
		mHolder.imageViewPlus.setImageResource(R.drawable.ic_plus_dark_selector);
		LinearLayout.LayoutParams paramsName = (LinearLayout.LayoutParams) mHolder.textViewItemName.getLayoutParams();
		if(TextUtils.isEmpty(mcv.getCustomizeText())){
			mHolder.textViewItemCustomizeText.setVisibility(View.GONE);
			paramsName.setMargins(0, 0, 0, (int)(ASSL.Yscale() * 25.0f));
			paramsName.setMarginStart(0);
			paramsName.setMarginEnd(0);
		} else {
			mHolder.textViewItemCustomizeText.setVisibility(View.VISIBLE);
			mHolder.textViewItemCustomizeText.setText(mcv.getCustomizeText());
			paramsName.setMargins(0, 0, 0, (int)(ASSL.Yscale() * 10.0f));
			paramsName.setMarginStart(0);
			paramsName.setMarginEnd(0);
		}
		if (!TextUtils.isEmpty(mcv.getItemInstructions())) {
			String s = context.getString(R.string.special_instructions) + ":\n" +
					mcv.getItemInstructions();
			mHolder.textViewInstructions.setText(s);
			mHolder.textViewInstructions.setVisibility(View.VISIBLE);
		} else {
			mHolder.textViewInstructions.setVisibility(View.GONE);
		}
		mHolder.textViewItemName.setLayoutParams(paramsName);

		if(position == getCount()-1){
			mHolder.imageViewSep.setBackgroundColor(context.getResources().getColor(R.color.transparent));
		} else {
			mHolder.imageViewSep.setBackgroundColor(context.getResources().getColor(R.color.stroke_light_grey_alpha));
		}

		mHolder.imageViewItemImage.setVisibility(View.GONE);
		if(isPositionSubscription(position)){
			mHolder.imageViewItemImage.setVisibility(View.VISIBLE);
			Picasso.with(context).load(R.drawable.star)
					.placeholder(R.drawable.ic_fresh_item_placeholder)
					.fit()
					.centerCrop()
					.error(R.drawable.ic_fresh_item_placeholder)
					.into(mHolder.imageViewItemImage);
		}

		mHolder.imageViewFoodType.setVisibility((mcv.getIsVeg() > -1 && appType == AppConstant.ApplicationType.MENUS) ? View.VISIBLE : View.GONE);
		mHolder.imageViewFoodType.setImageResource(mcv.getIsVeg() == 1 ? R.drawable.veg : R.drawable.nonveg);

		mHolder.imageViewMinus.setTag(position);
		mHolder.imageViewPlus.setTag(position);

		mHolder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					final int pos = (int) v.getTag();
//					if(checkForCouponApplied && callback.getSelectedCoupon() != null && callback.getSelectedCoupon().getId() > 0){
//						DialogPopup.alertPopupTwoButtonsWithListeners(context, "",
//								context.getString(R.string.coupon_remove_reapply_before_checkout),
//								context.getString(R.string.ok),
//								context.getString(R.string.cancel),
//								new View.OnClickListener(){
//									@Override
//									public void onClick(View v) {
//										doMinus(pos);
//										callback.removeCoupon();
//									}
//								},
//								new View.OnClickListener(){
//									@Override
//									public void onClick(View v) {
//
//									}
//								}, true, false);
//					} else{
						doMinus(pos);
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		mHolder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					final int pos = (int) v.getTag();
//					if(checkForCouponApplied && callback.getSelectedCoupon() != null && callback.getSelectedCoupon().getId() > 0){
//						DialogPopup.alertPopupTwoButtonsWithListeners(context, "",
//								context.getString(R.string.coupon_remove_reapply_before_checkout),
//								context.getString(R.string.ok),
//								context.getString(R.string.cancel),
//								new View.OnClickListener(){
//									@Override
//									public void onClick(View v) {
//										doPlus(pos);
//										callback.removeCoupon();
//									}
//								},
//								new View.OnClickListener(){
//									@Override
//									public void onClick(View v) {
//
//									}
//								}, true, false);
//					} else{
						doPlus(pos);
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


	}

	private void doMinus(int pos) {
		if(isPositionSubscription(pos)){
			callback.deleteStarSubscription();
		} else {
			Pair<Integer, Integer> pair = doItemPlusMinus(pos, false);
			if (pair != null) {
				callback.onMinusClicked(pair.first, pair.second);
				notifyDataSetChanged();

			}
		}
	}

	private void doPlus(int pos){
		if(isPositionSubscription(pos)){
			Utils.showToast(context, context.getResources().getString(R.string.no_more_than_star, 1));
		} else {
			Pair<Integer, Integer> pair = doItemPlusMinus(pos, true);
			if (pair != null) {
				callback.onPlusClicked(pair.first, pair.second);
				notifyDataSetChanged();
			}

		}
	}

	private boolean isPositionSubscription(int pos){
		return (subscription != null && pos == getCount()-1);
	}

	static class MainViewHolder extends RecyclerView.ViewHolder {
		public int id;
		public RelativeLayout relative;
		public LinearLayout linearLayoutContent;
		private ImageView imageViewItemImage, imageViewSep, imageViewFoodType, imageViewMinus, imageViewPlus;
		public TextView textViewItemName, textViewItemPrice, textViewQuantity, textViewItemCustomizeText, textViewInstructions;

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
			textViewItemCustomizeText = (TextView) itemView.findViewById(R.id.textViewItemCustomizeText); textViewItemCustomizeText.setTypeface(Fonts.mavenRegular(context));
			textViewInstructions = (TextView) itemView.findViewById(R.id.textViewInstructions); textViewInstructions.setTypeface(Fonts.mavenRegular(context));
		}
	}

	public interface Callback{
		void onPlusClicked(int position, int itemTotalQuantity);
		void onMinusClicked(int position, int itemTotalQuantity);
		PromoCoupon getSelectedCoupon();
		void removeCoupon();
		void deleteStarSubscription();
	}


	private class MenuCartViewModel{
		private String name;
		private Integer isVeg;
		private Double price;
		private Integer quantity;
		private String customizeText;
		private String itemInstructions = "";

		public String getName() {
			return name;
		}

		public Integer getIsVeg() {
			return isVeg;
		}

		public Double getPrice() {
			return price;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setIsVeg(Integer isVeg) {
			this.isVeg = isVeg;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public String getCustomizeText() {
			return customizeText;
		}

		public void setCustomizeText(String customizeText) {
			this.customizeText = customizeText;
		}

		public String getItemInstructions() {
			return itemInstructions;
		}

		public void setItemInstructions(String itemInstructions) {
			if (itemInstructions == null) itemInstructions = "";
			this.itemInstructions = itemInstructions;
		}
	}

}
