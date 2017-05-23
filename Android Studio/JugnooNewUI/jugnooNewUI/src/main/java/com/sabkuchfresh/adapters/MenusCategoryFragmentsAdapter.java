package com.sabkuchfresh.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.fragments.MenusCategoryItemsFragment;
import com.sabkuchfresh.retrofit.model.menus.Category;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.Subcategory;
import com.sabkuchfresh.widgets.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by Ankit on 12/29/15.
 */
public class MenusCategoryFragmentsAdapter extends FragmentStatePagerAdapter
		implements PagerSlidingTabStrip.CustomTabProvider {

	private List<Category> categories;
	private Context context;
	private LayoutInflater inflater;
	public MenusCategoryFragmentsAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setCategories(List<Category> categories){
		if(this.categories == null){
			this.categories = new ArrayList<>();
		}
		this.categories.clear();
		this.categories.addAll(categories);
		notifyDataSetChanged();
	}

	public void filterCategoriesAccIsVeg(List<Category> categories){
		int isVegToggle = Prefs.with(context).getInt(Constants.KEY_SP_IS_VEG_TOGGLE, 0);
		if(this.categories == null){
			this.categories = new ArrayList<>();
		}
		this.categories.clear();
		for(Category category : categories){
			int vegItemsCount = 0;
			if(category.getSubcategories() != null){
				for(Subcategory subcategory : category.getSubcategories()){
					for(Item item : subcategory.getItems()){
						if(item.getIsVeg() == 1){
							vegItemsCount++;
						}
					}
				}
			} else if(category.getItems() != null){
				for(Item item : category.getItems()){
					if(item.getIsVeg() == 1){
						vegItemsCount++;
					}
				}
			}
			if(isVegToggle == 0 || vegItemsCount > 0){
				this.categories.add(category);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int position) {
		return MenusCategoryItemsFragment.newInstance(position, 1);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return categories == null ? 0 : categories.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return categories.get(position).getCategoryName();
	}

	@Override
	public View getCustomTabView(int position) {
		View convertView = inflater.inflate(R.layout.tab_item_fresh_category, null);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
		TextView textView = (TextView) convertView.findViewById(R.id.textView);
		textView.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
		textView.setText(categories.get(position).getCategoryName());
//		try{
//			Picasso.with(context).load(categories.get(position).getCategoryImage())
//					.into(imageView);
//		} catch(Exception e){
//			e.printStackTrace();
//		}
		convertView.setLayoutParams(new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.MATCH_PARENT));
		ASSL.DoMagic(convertView);
		return convertView;
	}
}
