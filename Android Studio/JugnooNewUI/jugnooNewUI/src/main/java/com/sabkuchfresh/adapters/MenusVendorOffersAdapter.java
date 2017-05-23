package com.sabkuchfresh.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 19/05/17.
 */

public class MenusVendorOffersAdapter extends PagerAdapter {

	private Context context;
	private List<MenusResponse.BannerInfo> bannerInfos;
	private Callback callback;
	private LayoutInflater layoutInflater;

	public MenusVendorOffersAdapter(Context context, List<MenusResponse.BannerInfo> bannerInfos, Callback callback){
		this.context = context;
		this.bannerInfos = bannerInfos;
		this.callback = callback;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setList(List<MenusResponse.BannerInfo> bannerInfos){
		this.bannerInfos = bannerInfos;
		notifyDataSetChanged();
	}


	public void onBindViewHolder(MenusVendorOffersAdapter.ViewHolderVendorOffer holder, int position) {
		MenusResponse.BannerInfo bannerInfo = bannerInfos.get(position);
		if(!TextUtils.isEmpty(bannerInfo.getImageLink())) {
			Picasso.with(context).load(bannerInfo.getImageLink())
					.placeholder(R.drawable.ic_fresh_item_placeholder)
					.error(R.drawable.ic_fresh_item_placeholder)
					.into(holder.ivRestImage);
		} else {
			Picasso.with(context).load(R.drawable.ic_fresh_item_placeholder).into(holder.ivRestImage);
		}

		holder.relative.setTag(position);
		holder.relative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int pos = (int) v.getTag();
					callback.onBannerInfoClick(bannerInfos.get(pos));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public int getCount() {
		return bannerInfos == null ? 0 : bannerInfos.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View v = layoutInflater.inflate(R.layout.item_menus_vendor_offer, container, false);
		ViewHolderVendorOffer holder = new ViewHolderVendorOffer(v);
		onBindViewHolder(holder, position);
		container.addView(v);
		return v;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((RelativeLayout) object);
	}

	class ViewHolderVendorOffer{
		@Bind(R.id.ivRestImage)
		ImageView ivRestImage;
		@Bind(R.id.relative)
		RelativeLayout relative;

		ViewHolderVendorOffer(View itemView) {
			ButterKnife.bind(this, itemView);
		}
	}


	public interface Callback{
		void onBannerInfoClick(MenusResponse.BannerInfo bannerInfo);
	}

}
