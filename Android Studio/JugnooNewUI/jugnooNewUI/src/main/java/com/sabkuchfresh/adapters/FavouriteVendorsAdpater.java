package com.sabkuchfresh.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 1/20/17.
 */

public class FavouriteVendorsAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemListener {

	public static final int MAX_FAVOURITE_VENDORS_TO_SHOW = 6;
	public static final int FAVOURITE_VENDORS_SEE_ALL = -1;
	private Context context;
	private List<MenusResponse.Vendor> favouriteVendorsList;
	private Callback callback;
	private RecyclerView recyclerView;
	private List<MenusResponse.Vendor> cachedList;
	private boolean isFavouriteVendorCollapsed;

	public boolean isFavouriteVendorCollapsed() {
		return isFavouriteVendorCollapsed;
	}

	public FavouriteVendorsAdpater(Context context, Callback callback, RecyclerView recyclerView) {
		this.context = context;
		this.callback = callback;
		this.recyclerView = recyclerView;
	}

	public synchronized void setList(List<MenusResponse.Vendor> elements) {


		if(elements!=null && elements.size()> MAX_FAVOURITE_VENDORS_TO_SHOW){
			cachedList = new ArrayList<>();
			for(int i = elements.size()-1; i>= MAX_FAVOURITE_VENDORS_TO_SHOW -1; i--){
				cachedList.add(elements.get(i));
				elements.remove(elements.get(i));
			}
			Collections.reverse(cachedList);
			elements.add(new MenusResponse.Vendor(FAVOURITE_VENDORS_SEE_ALL));
			isFavouriteVendorCollapsed = true;

		}
		this.favouriteVendorsList = elements;
		notifyDataSetChanged();
	}

	public class ViewHolderFavouriteVendor extends RecyclerView.ViewHolder {
		public LinearLayout llRoot;
		public ImageView ivFavouriteVendorImage;
		public TextView tvFavouriteVendorName;
		public TextView tvReviewCount;
		public View viewReviewShadow;


		public ViewHolderFavouriteVendor(final View view, final ItemListener itemListener) {
			super(view);
			llRoot = (LinearLayout) view.findViewById(R.id.llRoot);
			ivFavouriteVendorImage = (ImageView) view.findViewById(R.id.ivFavouriteVendorImage);
			tvFavouriteVendorName = (TextView) view.findViewById(R.id.tvFavouriteVendorName);
			tvReviewCount = (TextView) view.findViewById(R.id.tvReviewCount);
			viewReviewShadow = (View) view.findViewById(R.id.view_review_shadow);
			llRoot.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(llRoot, view);
				}
			});
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.list_item_favourite_vendor, parent, false);
		return new ViewHolderFavouriteVendor(view, this);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {


		try {
			MenusResponse.Vendor favouriteVendor = favouriteVendorsList.get(position);
			ViewHolderFavouriteVendor holder = ((ViewHolderFavouriteVendor) mholder);
			if (favouriteVendor.getRestaurantId() == -1) {
                holder.tvFavouriteVendorName.setText(R.string.more_caps);
                holder.ivFavouriteVendorImage.setScaleType(ImageView.ScaleType.CENTER);
                holder.ivFavouriteVendorImage.setImageResource(R.drawable.ic_more);
				holder.tvReviewCount.setVisibility(View.GONE);
				holder.viewReviewShadow.setVisibility(View.GONE);
                return;
            }

			holder.tvReviewCount.setVisibility(View.VISIBLE);
			holder.viewReviewShadow.setVisibility(View.VISIBLE);
            holder.ivFavouriteVendorImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
			holder.tvFavouriteVendorName.setText(favouriteVendor.getName());
			holder.tvReviewCount.setText(Utils.getDecimalFormat1Decimal().format(favouriteVendor.getRating()) + " ");

			try {
                if (!TextUtils.isEmpty(favouriteVendor.getImage())) {
                    Picasso.with(context).load(favouriteVendor.getImage())
                            .placeholder(R.drawable.ic_fresh_item_placeholder)
                            .error(R.drawable.ic_fresh_item_placeholder)
                            .into(holder.ivFavouriteVendorImage);
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                holder.ivFavouriteVendorImage.setImageResource(R.drawable.ic_fresh_item_placeholder);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}



	}

	@Override
	public int getItemCount() {
		return favouriteVendorsList == null ? 0 : favouriteVendorsList.size();
	}


	public interface Callback {
		void onItemClick(MenusResponse.Vendor favouriteVendor);

	}

	@Override
	public void onClickItem(View viewClicked, View parentView) {
		int pos = recyclerView.getChildLayoutPosition(parentView);
		if (pos != RecyclerView.NO_POSITION) {
			switch (viewClicked.getId()) {
				case R.id.llRoot:
					if (callback != null) {
						
						if(favouriteVendorsList.get(pos)!=null){
							if(favouriteVendorsList.get(pos).getRestaurantId()==-1){
								favouriteVendorsList.remove(favouriteVendorsList.size()-1);
								int size = favouriteVendorsList.size();
								favouriteVendorsList.addAll(cachedList);
								isFavouriteVendorCollapsed = false;
								notifyItemRangeChanged(size,cachedList.size());
							}else{
								callback.onItemClick(favouriteVendorsList.get(pos));

							}
						}
					}
					break;


			}
		}
	}


	public List<MenusResponse.Vendor> getFavouriteVendorsList() {
		return favouriteVendorsList;
	}
}