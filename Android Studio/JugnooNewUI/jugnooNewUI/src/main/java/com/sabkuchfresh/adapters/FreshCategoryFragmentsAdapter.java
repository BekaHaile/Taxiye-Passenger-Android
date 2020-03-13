package com.sabkuchfresh.adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.fragments.FreshCategoryItemsFragment;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.widgets.PagerSlidingTabStrip;

import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by Ankit on 12/29/15.
 */
public class FreshCategoryFragmentsAdapter extends FragmentStatePagerAdapter
		implements PagerSlidingTabStrip.CustomTabProvider {

	private List<Category> categories;
	private Context context;
	private LayoutInflater inflater;

	public FreshCategoryFragmentsAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setCategories(List<Category> categories){
		this.categories = categories;
		notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int position) {
		return FreshCategoryItemsFragment.newInstance(position, 0);
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
		TextView textView = (TextView) convertView.findViewById(R.id.textView);
		textView.setTypeface(Fonts.mavenRegular(context));
		textView.setText(categories.get(position).getCategoryName());
		convertView.setLayoutParams(new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.MATCH_PARENT));
		ASSL.DoMagic(convertView);
		return convertView;
	}
}
