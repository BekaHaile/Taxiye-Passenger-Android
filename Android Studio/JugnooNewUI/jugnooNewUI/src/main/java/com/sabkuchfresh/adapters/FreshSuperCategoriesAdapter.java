package com.sabkuchfresh.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.retrofit.model.SuperCategoriesData;
import com.squareup.picasso.Picasso;

import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 1/20/17.
 */

public class FreshSuperCategoriesAdapter extends RecyclerView.Adapter<FreshSuperCategoriesAdapter.ViewHolderCategory>{

	private Context context;
	private List<SuperCategoriesData.SuperCategory> superCategories;
	private Callback callback;

	public FreshSuperCategoriesAdapter(Context context, Callback callback){
		this.context = context;
		this.callback = callback;
	}

	public synchronized void setList(List<SuperCategoriesData.SuperCategory> elements){
		this.superCategories = elements;
		notifyDataSetChanged();
	}

	public class ViewHolderCategory extends RecyclerView.ViewHolder {
		public LinearLayout llRoot;
		public ImageView ivSuperCategoryImage;
		public TextView tvSuperCategoryName;

		public ViewHolderCategory(View view, Context context) {
			super(view);
			llRoot = (LinearLayout) view.findViewById(R.id.llRoot);
			ivSuperCategoryImage = (ImageView) view.findViewById(R.id.ivSuperCategoryImage);
			tvSuperCategoryName = (TextView) view.findViewById(R.id.tvSuperCategoryName);
			tvSuperCategoryName.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);
		}
	}

	@Override
	public ViewHolderCategory onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(context).inflate(R.layout.list_item_fresh_super_category, parent, false);
		view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));
		ASSL.DoMagic(view);
		return new ViewHolderCategory(view, context);
	}

	@Override
	public void onBindViewHolder(ViewHolderCategory holder, int position) {
		SuperCategoriesData.SuperCategory superCategory = superCategories.get(position);
		holder.tvSuperCategoryName.setText(superCategory.getSuperCategoryName());
		holder.llRoot.setTag(position);
		holder.llRoot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int pos = (int) v.getTag();
					callback.onItemClick(pos, superCategories.get(pos));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


		try {
			if(!TextUtils.isEmpty(superCategory.getSuperCategoryImage())) {
				Picasso.with(context).load(superCategory.getSuperCategoryImage())
						.placeholder(R.drawable.ic_fresh_new_placeholder)
						.error(R.drawable.ic_fresh_new_placeholder)
						.into(holder.ivSuperCategoryImage);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Picasso.with(context).load(R.drawable.ic_fresh_new_placeholder)
//					.resize((int) (384f * ASSL.Xscale()), (int) (210f * ASSL.Yscale()))
//					.transform(new RoundBorderTransform((int) (Math.min(ASSL.Xscale(), ASSL.Yscale()) * 6), 0))
					.into(holder.ivSuperCategoryImage);
		}

	}

	@Override
	public int getItemCount() {
		return superCategories == null ? 0 : superCategories.size();
	}

	public interface Callback{
		void onItemClick(int pos, SuperCategoriesData.SuperCategory superCategory);
	}
}