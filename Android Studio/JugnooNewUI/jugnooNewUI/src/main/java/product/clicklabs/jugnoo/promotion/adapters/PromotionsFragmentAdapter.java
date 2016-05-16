package product.clicklabs.jugnoo.promotion.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.promotion.fragments.PromotionsFragment;
import product.clicklabs.jugnoo.promotion.fragments.ReferralActivityFragment;
import product.clicklabs.jugnoo.promotion.fragments.ReferralLeaderboardFragment;
import product.clicklabs.jugnoo.promotion.fragments.ReferralsFragment;

/**
 * Created by shankar on 12/29/15.
 */
public class PromotionsFragmentAdapter extends FragmentPagerAdapter{
//		implements PagerSlidingTabStrip.CustomTabProvider {

	private Context context;
	private LayoutInflater inflater;

	public PromotionsFragmentAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Fragment getItem(int position) {
		switch(position){
			case 0:
				return new ReferralsFragment();

			case 1:
				return new PromotionsFragment();

			case 2:
				if(Data.userData != null && Data.userData.getReferralLeaderboardEnabled() == 1) {
					return new ReferralLeaderboardFragment();
				}

			case 3:
				if(Data.userData != null && Data.userData.getReferralActivityEnabled() == 1) {
					return new ReferralActivityFragment();
				}
		}

		return null;
	}

	@Override
	public int getCount() {
		int count = 2;
		if(Data.userData != null && Data.userData.getReferralLeaderboardEnabled() == 1){
			count = count + 1;
		}
		if(Data.userData != null && Data.userData.getReferralActivityEnabled() == 1){
			count = count + 1;
		}
		return count;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return context.getResources().getString(R.string.referrals).toUpperCase();
			case 1:
				return context.getResources().getString(R.string.nl_offers).toUpperCase();
			case 2:
				if(Data.userData != null && Data.userData.getReferralLeaderboardEnabled() == 1) {
					return context.getResources().getString(R.string.leaderboard).toUpperCase();
				}
			case 3:
				if(Data.userData != null && Data.userData.getReferralActivityEnabled() == 1) {
					return context.getResources().getString(R.string.activity).toUpperCase();
				}
		}
		return "";
	}

//	@Override
//	public View getCustomTabView(int position, boolean selected) {
//		View convertView = inflater.inflate(R.layout.tab_item_promotion, null);
//		TextView textView = (TextView) convertView.findViewById(R.id.textView);
//		textView.setTypeface(Fonts.mavenRegular(context), selected ? Typeface.BOLD : Typeface.NORMAL);
//		textView.setText(getPageTitle(position));
//		convertView.setLayoutParams(new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.MATCH_PARENT));
//		ASSL.DoMagic(convertView);
//		return convertView;
//	}

}
