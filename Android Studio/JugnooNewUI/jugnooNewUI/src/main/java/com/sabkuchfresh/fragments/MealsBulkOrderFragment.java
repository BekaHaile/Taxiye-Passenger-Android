package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fugu.FuguConfig;
import com.sabkuchfresh.home.FreshActivity;
import com.squareup.picasso.Picasso;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by shankar on 30/06/17.
 */

public class MealsBulkOrderFragment extends Fragment {

	private FreshActivity activity;

	public static MealsBulkOrderFragment newInstance(String url){
		MealsBulkOrderFragment fragment = new MealsBulkOrderFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constants.KEY_URL, url);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_meals_bulk_order, container, false);

		activity = (FreshActivity) getActivity();
		activity.fragmentUISetup(this);

		ImageView iv = (ImageView) rootView.findViewById(R.id.iv);
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					FuguConfig.getInstance().openChat(activity, Data.CHANNEL_ID_FUGU_BULK_MEALS());
				} catch (Exception e) {
					e.printStackTrace();
					Utils.showToast(activity, activity.getString(R.string.something_went_wrong));
				}
			}
		});

		String url = getArguments().getString(Constants.KEY_URL);
		try {
			if(!TextUtils.isEmpty(url)){
				Picasso.with(activity).load(url)
						.placeholder(R.drawable.ic_fresh_new_placeholder)
						.error(R.drawable.ic_fresh_new_placeholder)
						.into(iv);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
			activity.onBackPressed();
		}

		return rootView;
	}
}
