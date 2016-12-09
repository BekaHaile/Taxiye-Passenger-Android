package com.jugnoo.pay.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jugnoo.pay.fragments.ContactsFragment;
import com.jugnoo.pay.fragments.PaymentFragment;

import product.clicklabs.jugnoo.R;

/**
 * Created by Ankit on 12/29/15.
 */
public class SendMoneyPagerAdapter extends FragmentPagerAdapter{

	private Context context;
	private boolean requestStatus;

	public SendMoneyPagerAdapter(Context context, FragmentManager fm, boolean requestStatus) {
		super(fm);
		this.context = context;
		this.requestStatus = requestStatus;
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
		if(requestStatus){
			return 1;
		} else {
			return 2;
		}
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
