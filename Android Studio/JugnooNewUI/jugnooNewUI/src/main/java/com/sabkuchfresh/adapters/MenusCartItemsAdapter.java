package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;
import com.sabkuchfresh.utils.AppConstant;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
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

	public MenusCartItemsAdapter(Activity context, ArrayList<Item> items, boolean checkForCouponApplied,
								 Callback callback) {
		this.context = context;
		this.items = items;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.callback = callback;
		this.checkForCouponApplied = checkForCouponApplied;
		appType = Prefs.with(context).getInt(Constants.APP_TYPE, Data.AppType);
	}

	@Override
	public int getCount() {
		int count = 0;
		for(Item item : items){
			count  = count + item.getItemSelectedList().size();
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
					mcv.setName(item.getItemName());
					mcv.setIsVeg(item.getIsVeg());
					mcv.setPrice(itemSelected.getTotalPrice());
					mcv.setQuantity(itemSelected.getQuantity());
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
						} else {
							Utils.showToast(context, context.getString(R.string.cannot_add_more_than_50));
						}
					} else {
						if(item.getTotalQuantity() > 0) {
							itemSelected.setQuantity(itemSelected.getQuantity() - 1);
						}
					}
					return new Pair<>(i, item.getTotalQuantity());
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
		MenuCartViewModel mcv = getItem(position);

		mHolder.textViewItemName.setText(mcv.getName());
		mHolder.textViewItemPrice.setText(String.format(context.getResources().getString(R.string.rupees_value_format),
				Utils.getMoneyDecimalFormat().format(mcv.getPrice())));
		mHolder.textViewQuantity.setText(String.valueOf(mcv.getQuantity()));

		if(position == getCount()-1){
			mHolder.imageViewSep.setBackgroundColor(context.getResources().getColor(R.color.transparent));
		} else {
			mHolder.imageViewSep.setBackgroundColor(context.getResources().getColor(R.color.stroke_light_grey_alpha));
		}

		mHolder.imageViewItemImage.setVisibility(View.GONE);

		mHolder.imageViewFoodType.setVisibility(appType == AppConstant.ApplicationType.MENUS ? View.VISIBLE : View.GONE);
		mHolder.imageViewFoodType.setImageResource(mcv.getIsVeg() == 1 ? R.drawable.veg : R.drawable.nonveg);
		RelativeLayout.LayoutParams paramsFT = (RelativeLayout.LayoutParams) mHolder.imageViewFoodType.getLayoutParams();
		RelativeLayout.LayoutParams paramsLLC = (RelativeLayout.LayoutParams) mHolder.linearLayoutContent.getLayoutParams();
		if(mHolder.imageViewFoodType.getVisibility() == View.VISIBLE && mHolder.imageViewItemImage.getVisibility() == View.GONE){
			paramsFT.setMargins(0, (int)(ASSL.Yscale()*25f), 0, 0);
			paramsLLC.setMargins((int)(ASSL.Xscale()*20f), 0, 0, 0);
		} else {
			paramsFT.setMargins((int)(ASSL.Xscale()*2f), (int)(ASSL.Yscale()*2f), 0, 0);
			paramsLLC.setMargins((int)(ASSL.Xscale()*30f), 0, 0, 0);
		}
		mHolder.imageViewFoodType.setLayoutParams(paramsFT);
		mHolder.linearLayoutContent.setLayoutParams(paramsLLC);


		mHolder.textViewMinus.setTag(position);
		mHolder.textViewPlus.setTag(position);

		mHolder.textViewMinus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					final int pos = (int) v.getTag();
					if(checkForCouponApplied && callback.getSelectedCoupon() != null && callback.getSelectedCoupon().getId() > 0){
						DialogPopup.alertPopupTwoButtonsWithListeners(context, "",
								context.getString(R.string.coupon_remove_reapply_before_checkout),
								context.getString(R.string.ok),
								context.getString(R.string.cancel),
								new View.OnClickListener(){
									@Override
									public void onClick(View v) {
										doMinus(pos);
										callback.removeCoupon();
									}
								},
								new View.OnClickListener(){
									@Override
									public void onClick(View v) {

									}
								}, true, false);
					} else{
						doMinus(pos);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		mHolder.textViewPlus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					final int pos = (int) v.getTag();
					if(checkForCouponApplied && callback.getSelectedCoupon() != null && callback.getSelectedCoupon().getId() > 0){
						DialogPopup.alertPopupTwoButtonsWithListeners(context, "",
								context.getString(R.string.coupon_remove_reapply_before_checkout),
								context.getString(R.string.ok),
								context.getString(R.string.cancel),
								new View.OnClickListener(){
									@Override
									public void onClick(View v) {
										doPlus(pos);
										callback.removeCoupon();
									}
								},
								new View.OnClickListener(){
									@Override
									public void onClick(View v) {

									}
								}, true, false);
					} else{
						doPlus(pos);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


	}

	private void doMinus(int pos) {
		Pair<Integer, Integer> pair = doItemPlusMinus(pos, false);
		if(pair != null) {
			callback.onMinusClicked(pair.first, pair.second);
		}
		notifyDataSetChanged();
	}

	private void doPlus(int pos){
		Pair<Integer, Integer> pair = doItemPlusMinus(pos, true);
		if(pair != null) {
			callback.onPlusClicked(pair.first, pair.second);
		}
		notifyDataSetChanged();
	}

	static class MainViewHolder extends RecyclerView.ViewHolder {
		public int id;
		public RelativeLayout relative;
		public LinearLayout linearLayoutContent;
		private ImageView imageViewItemImage, imageViewSep, imageViewFoodType;
		public TextView textViewItemName, textViewItemPrice, textViewQuantity, textViewItemCustomizeText,
				textViewMinus, textViewPlus;

		public MainViewHolder(View itemView, Context context) {
			super(itemView);
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			linearLayoutContent = (LinearLayout) itemView.findViewById(R.id.linearLayoutContent);
			imageViewItemImage = (ImageView) itemView.findViewById(R.id.imageViewItemImage);
			imageViewFoodType = (ImageView) itemView.findViewById(R.id.imageViewFoodType);
			imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);
			textViewMinus = (TextView) itemView.findViewById(R.id.textViewMinus);
			textViewPlus = (TextView) itemView.findViewById(R.id.textViewPlus);

			textViewItemName = (TextView) itemView.findViewById(R.id.textViewItemName); textViewItemName.setTypeface(Fonts.mavenMedium(context));
			textViewItemPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice); textViewItemPrice.setTypeface(Fonts.mavenMedium(context));
			textViewQuantity = (TextView) itemView.findViewById(R.id.textViewQuantity); textViewQuantity.setTypeface(Fonts.mavenMedium(context));
			textViewItemCustomizeText = (TextView) itemView.findViewById(R.id.textViewItemCustomizeText); textViewItemCustomizeText.setTypeface(Fonts.mavenRegular(context));
		}
	}

	public interface Callback{
		void onPlusClicked(int position, int itemTotalQuantity);
		void onMinusClicked(int position, int itemTotalQuantity);
		PromoCoupon getSelectedCoupon();
		void removeCoupon();
	}


	private class MenuCartViewModel{
		private String name;
		private Integer isVeg;
		private Double price;
		private Integer quantity;

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

	}

}
