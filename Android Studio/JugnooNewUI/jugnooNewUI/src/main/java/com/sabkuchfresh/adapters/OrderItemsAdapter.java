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

import com.sabkuchfresh.retrofit.model.SubItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Shankar on 7/17/15.
 */
public class OrderItemsAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private List<HistoryResponse.OrderItem> subItems;

	public OrderItemsAdapter(Context context, ArrayList<HistoryResponse.OrderItem> subItems) {
		this.context = context;
		this.subItems = subItems;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public synchronized void setResults(ArrayList<HistoryResponse.OrderItem> subItems) {
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
		final HistoryResponse.OrderItem subItem = subItems.get(position);

		mHolder.textViewItemName.setText(subItem.getItemName());
		mHolder.textViewItemPrice.setText(String.format(context.getResources().getString(R.string.rupees_value_format),
				Utils.getMoneyDecimalFormat().format(subItem.getItemAmount())));
		mHolder.textViewItemQuantity.setText("X "+subItem.getItemQuantity());
		mHolder.textViewItemTotalPrice.setText(context.getString(R.string.rupees_value_format,
				Utils.getMoneyDecimalFormat().format(subItem.getItemAmount() * subItem.getItemQuantity())));

		if(position == getCount()-1){
			mHolder.imageViewSep.setVisibility(View.GONE);
		} else {
			mHolder.imageViewSep.setVisibility(View.VISIBLE);
		}

		mHolder.imageViewItemImage.setVisibility(View.GONE);

	}

	static class MainViewHolder extends RecyclerView.ViewHolder {
		public int id;
		public RelativeLayout relative;
		private ImageView imageViewItemImage, imageViewSep;
		public TextView textViewItemName, textViewItemPrice, textViewItemQuantity, textViewItemTotalPrice;

		public MainViewHolder(View itemView, Context context) {
			super(itemView);
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			imageViewItemImage = (ImageView) itemView.findViewById(R.id.imageViewItemImage);
			imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);

			textViewItemName = (TextView) itemView.findViewById(R.id.textViewItemName); textViewItemName.setTypeface(Fonts.mavenMedium(context));
			textViewItemPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice); textViewItemPrice.setTypeface(Fonts.mavenMedium(context));
			textViewItemQuantity = (TextView) itemView.findViewById(R.id.textViewItemQuantity); textViewItemQuantity.setTypeface(Fonts.mavenMedium(context));
			textViewItemTotalPrice = (TextView) itemView.findViewById(R.id.textViewItemTotalPrice); textViewItemTotalPrice.setTypeface(Fonts.mavenMedium(context));

		}
	}
}
