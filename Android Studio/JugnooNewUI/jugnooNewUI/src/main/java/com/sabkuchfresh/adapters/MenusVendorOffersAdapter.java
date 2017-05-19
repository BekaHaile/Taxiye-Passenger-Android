package com.sabkuchfresh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundBorderTransform;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 19/05/17.
 */

public class MenusVendorOffersAdapter extends RecyclerView.Adapter<MenusVendorOffersAdapter.ViewHolderVendorOffer> implements ItemListener {

	private Context context;
	private ArrayList<MenusResponse.Vendor> vendorOffers;
	private RecyclerView recyclerView;
	private Callback callback;

	public MenusVendorOffersAdapter(Context context, ArrayList<MenusResponse.Vendor> vendorOffers, RecyclerView recyclerView, Callback callback){
		this.context = context;
		this.vendorOffers = vendorOffers;
		this.recyclerView = recyclerView;
		this.callback = callback;
	}

	public void setList(ArrayList<MenusResponse.Vendor> vendorOffers){
		this.vendorOffers = vendorOffers;
		notifyDataSetChanged();
	}

	@Override
	public MenusVendorOffersAdapter.ViewHolderVendorOffer onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menus_vendor_offer, parent, false);
		return new ViewHolderVendorOffer(v, this);
	}

	@Override
	public void onBindViewHolder(MenusVendorOffersAdapter.ViewHolderVendorOffer holder, int position) {
		MenusResponse.Vendor vendor = vendorOffers.get(position);
		holder.tvRestName.setText(vendor.getName());
		if(!TextUtils.isEmpty(vendor.getImage())) {
			Picasso.with(context).load(vendor.getImage())
					.placeholder(R.drawable.ic_fresh_item_placeholder)
					.resize(context.getResources().getDimensionPixelSize(R.dimen.dp_150),
							context.getResources().getDimensionPixelSize(R.dimen.dp_80))
					.centerCrop()
					.transform(new RoundBorderTransform(context.getResources().getDimensionPixelSize(R.dimen.dp_4), 0))
					.error(R.drawable.ic_fresh_item_placeholder)
					.into(holder.ivRestImage);
		} else {
			Picasso.with(context).load(R.drawable.ic_fresh_item_placeholder)
					.resize(context.getResources().getDimensionPixelSize(R.dimen.dp_150),
							context.getResources().getDimensionPixelSize(R.dimen.dp_80))
					.centerCrop()
					.transform(new RoundBorderTransform(context.getResources().getDimensionPixelSize(R.dimen.dp_4), 0))
					.into(holder.ivRestImage);
		}

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.ivRestImage.getLayoutParams();
		params.rightMargin = (position == getItemCount()-1) ? context.getResources().getDimensionPixelSize(R.dimen.dp_10) : 0;
		holder.ivRestImage.setLayoutParams(params);

	}

	@Override
	public int getItemCount() {
		return vendorOffers == null ? 0 : vendorOffers.size();
	}

	@Override
	public void onClickItem(View viewClicked, View parentView) {
		int pos = recyclerView.getChildLayoutPosition(parentView);
		if(pos != RecyclerView.NO_POSITION){
			switch(viewClicked.getId()){
				case R.id.relative:
					callback.onOfferClick(vendorOffers.get(pos));
					break;
			}
		}
	}

	class ViewHolderVendorOffer extends RecyclerView.ViewHolder{

		@Bind(R.id.ivRestImage)
		ImageView ivRestImage;
		@Bind(R.id.tvRestName)
		TextView tvRestName;
		@Bind(R.id.relative)
		RelativeLayout relative;

		ViewHolderVendorOffer(final View itemView, final ItemListener itemListener) {
			super(itemView);
			ButterKnife.bind(this, itemView);
			relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(relative, itemView);
				}
			});
		}
	}


	public interface Callback{
		void onOfferClick(MenusResponse.Vendor vendor);
	}

}
