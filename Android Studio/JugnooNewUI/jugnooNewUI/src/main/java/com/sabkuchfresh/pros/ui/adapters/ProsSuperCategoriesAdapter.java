package com.sabkuchfresh.pros.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.ItemListener;
import com.sabkuchfresh.retrofit.model.SuperCategoriesData;
import com.squareup.picasso.Picasso;

import java.util.List;

import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 1/20/17.
 */

public class ProsSuperCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemListener{

	private Context context;
	private List<SuperCategoriesData.SuperCategory> superCategories;
	private Callback callback;
	private final int MAIN_ITEM = 1;
	private RecyclerView recyclerView;

	public ProsSuperCategoriesAdapter(Context context, Callback callback, RecyclerView recyclerView) {
		this.context = context;
		this.callback = callback;
		this.recyclerView = recyclerView;
	}

	public synchronized void setList(List<SuperCategoriesData.SuperCategory> elements) {
		this.superCategories = elements;
		notifyDataSetChanged();
	}

	public class ViewHolderCategory extends RecyclerView.ViewHolder {
		public LinearLayout llRoot;
		public ImageView ivSuperCategoryImage;
		public TextView tvSuperCategoryName;
		public View viewBG;

		public ViewHolderCategory(final View view, final ItemListener itemListener) {
			super(view);
			llRoot = (LinearLayout) view.findViewById(R.id.llRoot);
			ivSuperCategoryImage = (ImageView) view.findViewById(R.id.ivSuperCategoryImage);
			tvSuperCategoryName = (TextView) view.findViewById(R.id.tvSuperCategoryName);
			viewBG = view.findViewById(R.id.viewBG);
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
		View view = LayoutInflater.from(context).inflate(R.layout.list_item_pros_super_category, parent, false);
		return new ViewHolderCategory(view, this);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {
		SuperCategoriesData.SuperCategory superCategory = superCategories.get(position);
		ViewHolderCategory holder = ((ViewHolderCategory) mholder);
		holder.tvSuperCategoryName.setText(superCategory.getSuperCategoryName());

		try {
			if (!TextUtils.isEmpty(superCategory.getSuperCategoryImage())) {
				Picasso.with(context).load(superCategory.getSuperCategoryImage())
						.placeholder(R.drawable.ic_fresh_new_placeholder)
						.error(R.drawable.ic_fresh_new_placeholder)
						.into(holder.ivSuperCategoryImage);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			Picasso.with(context).load(R.drawable.ic_fresh_new_placeholder)
					.into(holder.ivSuperCategoryImage);
		}

		if (superCategory.getIsEnabled() == 0) {
			holder.viewBG.setBackgroundResource(R.drawable.bg_white_60_selector_color);
		} else {
			holder.viewBG.setBackgroundResource(R.drawable.bg_transparent_white_60_selector);
		}
	}

	@Override
	public int getItemCount() {
		return superCategories == null ? 0 : superCategories.size();
	}

	@Override
	public int getItemViewType(int position) {
		return MAIN_ITEM;
	}

	public interface Callback {
		void onItemClick(int pos, SuperCategoriesData.SuperCategory superCategory);
	}

	@Override
	public void onClickItem(View viewClicked, View parentView) {
		int pos = recyclerView.getChildLayoutPosition(parentView);
		if(pos != RecyclerView.NO_POSITION){
			switch(viewClicked.getId()){
				case R.id.llRoot:
					callback.onItemClick(pos, superCategories.get(pos));
					break;
			}
		}
	}
}