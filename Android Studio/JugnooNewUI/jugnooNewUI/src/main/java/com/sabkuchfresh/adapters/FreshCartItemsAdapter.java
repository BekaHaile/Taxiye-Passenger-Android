package com.sabkuchfresh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Shankar on 7/17/15.
 */
public class FreshCartItemsAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private List<SubItem> subItems;
	private String categoryName;
	private FreshCategoryItemsAdapter.Callback callback;

	public FreshCartItemsAdapter(Context context, ArrayList<SubItem> subItems, String categoryName, FreshCategoryItemsAdapter.Callback callback) {
		this.context = context;
		this.subItems = subItems;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.callback = callback;
		this.categoryName = categoryName;
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
		MainViewHolder mHolder = ((MainViewHolder) holder);
		final SubItem subItem = subItems.get(position);

		mHolder.textViewItemName.setText(subItem.getSubItemName());
		mHolder.textViewItemPrice.setText(String.format(context.getResources().getString(R.string.rupees_value_format),
				Utils.getMoneyDecimalFormat().format(subItem.getPrice())));
		mHolder.textViewQuantity.setText(String.valueOf(subItem.getSubItemQuantitySelected()));

		if(position == getCount()-1){
			mHolder.imageViewSep.setBackgroundColor(context.getResources().getColor(R.color.transparent));
		} else {
			mHolder.imageViewSep.setBackgroundColor(context.getResources().getColor(R.color.stroke_light_grey_alpha));
		}

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


		mHolder.imageViewMinus.setTag(position);
		mHolder.imageViewPlus.setTag(position);

		mHolder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int pos = (int) v.getTag();
					if(callback.checkForMinus(pos, subItems.get(pos))) {
						FlurryEventLogger.event(categoryName, FlurryEventNames.DELETE_PRODUCT, subItems.get(pos).getSubItemName());
						subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() > 0 ?
								subItems.get(pos).getSubItemQuantitySelected() - 1 : 0);
						callback.onMinusClicked(pos, subItems.get(pos));

						notifyDataSetChanged();
					} else{
						callback.minusNotDone(pos, subItems.get(pos));
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
					if(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
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


	}

	static class MainViewHolder extends RecyclerView.ViewHolder {
		public int id;
		public RelativeLayout relative;
		private ImageView imageViewItemImage, imageViewSep, imageViewMinus, imageViewPlus;
		public TextView textViewItemName, textViewItemPrice, textViewQuantity;

		public MainViewHolder(View itemView, Context context) {
			super(itemView);
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			imageViewItemImage = (ImageView) itemView.findViewById(R.id.imageViewItemImage);
			imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);
			imageViewMinus = (ImageView) itemView.findViewById(R.id.imageViewMinus);
			imageViewPlus = (ImageView) itemView.findViewById(R.id.imageViewPlus);

			textViewItemName = (TextView) itemView.findViewById(R.id.textViewItemName); textViewItemName.setTypeface(Fonts.mavenMedium(context));
			textViewItemPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice); textViewItemPrice.setTypeface(Fonts.mavenMedium(context));
			textViewQuantity = (TextView) itemView.findViewById(R.id.textViewQuantity); textViewQuantity.setTypeface(Fonts.mavenMedium(context));
		}
	}
}
