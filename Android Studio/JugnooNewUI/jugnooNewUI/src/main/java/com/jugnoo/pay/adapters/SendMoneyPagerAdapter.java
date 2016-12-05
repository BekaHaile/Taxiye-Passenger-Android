package com.jugnoo.pay.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jugnoo.pay.fragments.ContactsFragment;
import com.jugnoo.pay.fragments.PaymentFragment;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.promotion.fragments.PromotionsFragment;
import product.clicklabs.jugnoo.promotion.fragments.ReferralActivityFragment;
import product.clicklabs.jugnoo.promotion.fragments.ReferralsFragment;

/**
 * Created by Ankit on 12/29/15.
 */
public class SendMoneyPagerAdapter extends FragmentPagerAdapter{
//		implements PagerSlidingTabStrip.CustomTabProvider {

	private Context context;
//	private LayoutInflater inflater;

	public SendMoneyPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
//		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Fragment getItem(int position) {
		switch(position){
			case 0:
				return new ContactsFragment();
			case 1:
				return new PaymentFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return context.getResources().getString(R.string.contact);
			case 1:
				return context.getResources().getString(R.string.payment_address);
		}
		return "";
	}

}
