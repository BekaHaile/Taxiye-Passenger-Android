package product.clicklabs.jugnoo.fresh.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import product.clicklabs.jugnoo.fresh.fragments.FreshCategoryItemsFragment;
import product.clicklabs.jugnoo.fresh.models.Category;

/**
 * Created by Ankit on 12/29/15.
 */
public class FreshCategoryFragmentsAdapter extends FragmentStatePagerAdapter {

	private List<Category> categories;

	public FreshCategoryFragmentsAdapter(FragmentManager fm) {
		super(fm);
	}

	public void setCategories(List<Category> categories){
		this.categories = categories;
		notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int position) {
		return new FreshCategoryItemsFragment(categories.get(position));
	}

	@Override
	public int getCount() {
		return categories == null ? 0 : categories.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return categories.get(position).getCategoryName();
	}

}
