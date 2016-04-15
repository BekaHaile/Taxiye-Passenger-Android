package product.clicklabs.jugnoo.fresh.adapters;

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

import com.squareup.picasso.Picasso;

import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.fresh.fragments.FreshCategoryItemsFragment;
import product.clicklabs.jugnoo.fresh.models.Category;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.widgets.PagerSlidingTabStrip;

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
		return new FreshCategoryItemsFragment(categories.get(position));
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
		try{
			Picasso.with(context).load(categories.get(position).getCategoryImage())
					.into(imageView);
		} catch(Exception e){
			e.printStackTrace();
		}
		convertView.setLayoutParams(new LinearLayout.LayoutParams(220, LinearLayout.LayoutParams.MATCH_PARENT));
		ASSL.DoMagic(convertView);
		return convertView;
	}
}
