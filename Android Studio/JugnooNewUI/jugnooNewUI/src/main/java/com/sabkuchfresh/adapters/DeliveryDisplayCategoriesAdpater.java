package com.sabkuchfresh.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 1/20/17.
 */

public class DeliveryDisplayCategoriesAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemListener {

	public static final int MAX_CATEGORIES_TO_SHOW = 8;
	public static final int CATEGORY_SEE_ALL = -1;
	private Context context;
	private List<MenusResponse.Category> categoriesList;
	private Callback callback;
	private RecyclerView recyclerView;
	private List<MenusResponse.Category> cachedList;
	private boolean isCategoriesCollapsed;

	public boolean isCategoriesCollapsed() {
		return isCategoriesCollapsed;
	}

	public DeliveryDisplayCategoriesAdpater(Context context, Callback callback, RecyclerView recyclerView) {
		this.context = context;
		this.callback = callback;
		this.recyclerView = recyclerView;
	}

	public synchronized void setList(List<MenusResponse.Category> elements) {


		if(elements!=null && elements.size()> MAX_CATEGORIES_TO_SHOW){
			cachedList = new ArrayList<>();
			for(int i=elements.size()-1;i>=MAX_CATEGORIES_TO_SHOW-1;i--){
				cachedList.add(elements.get(i));
				elements.remove(elements.get(i));
			}
			Collections.reverse(cachedList);
			elements.add(new MenusResponse.Category(CATEGORY_SEE_ALL));
			isCategoriesCollapsed = true;

		}
		this.categoriesList = elements;
		notifyDataSetChanged();
	}

	public class ViewHolderCategory extends RecyclerView.ViewHolder {
		public LinearLayout llRoot;
		public ImageView ivSuperCategoryImage;
		public TextView tvSuperCategoryName;


		public ViewHolderCategory(final View view, final ItemListener itemListener) {
			super(view);
			llRoot = (LinearLayout) view.findViewById(R.id.llRoot);
			ivSuperCategoryImage = (ImageView) view.findViewById(R.id.ivSuperCategoryImage);
			tvSuperCategoryName = (TextView) view.findViewById(R.id.tvSuperCategoryName);
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
		View view = LayoutInflater.from(context).inflate(R.layout.list_item_delivery_category, parent, false);
		return new ViewHolderCategory(view, this);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {


		try {
			MenusResponse.Category prosCatalogueDatum = categoriesList.get(position);
			ViewHolderCategory holder = ((ViewHolderCategory) mholder);
			if (prosCatalogueDatum.getId() == -1) {
                holder.tvSuperCategoryName.setText(R.string.more_caps);
                holder.ivSuperCategoryImage.setScaleType(ImageView.ScaleType.CENTER);
                holder.ivSuperCategoryImage.setImageResource(R.drawable.ic_category_all);
                return;
            }

            holder.ivSuperCategoryImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
			holder.tvSuperCategoryName.setText(prosCatalogueDatum.getCategoryName());

			try {
                if (!TextUtils.isEmpty(prosCatalogueDatum.getImage())) {
                    Picasso.with(context).load(prosCatalogueDatum.getImage())
                            .placeholder(R.drawable.ic_fresh_item_placeholder)
                            .error(R.drawable.ic_fresh_item_placeholder)
                            .into(holder.ivSuperCategoryImage);
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                holder.ivSuperCategoryImage.setImageResource(R.drawable.ic_fresh_item_placeholder);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}



	}

	@Override
	public int getItemCount() {
		return categoriesList == null ? 0 : categoriesList.size();
	}


	public interface Callback {
		void onItemClick(MenusResponse.Category category);

	}

	@Override
	public void onClickItem(View viewClicked, View parentView) {
		int pos = recyclerView.getChildLayoutPosition(parentView);
		if (pos != RecyclerView.NO_POSITION) {
			switch (viewClicked.getId()) {
				case R.id.llRoot:
					if (callback != null) {
						
						if(categoriesList.get(pos)!=null){
							if(categoriesList.get(pos).getId()==-1){
								categoriesList.remove(categoriesList.size()-1);
								int size = categoriesList.size();
								categoriesList.addAll(cachedList);
								isCategoriesCollapsed = false;
								notifyItemRangeChanged(size,cachedList.size());
							}else{
								callback.onItemClick(categoriesList.get(pos));

							}
						}
					}
					break;


			}
		}
	}


	public List<MenusResponse.Category> getCategoriesList() {
		return categoriesList;
	}
}