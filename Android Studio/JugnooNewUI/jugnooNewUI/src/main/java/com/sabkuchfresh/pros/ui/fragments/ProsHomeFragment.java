package com.sabkuchfresh.pros.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 15/06/17.
 */

public class ProsHomeFragment extends Fragment {

	private View rootView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_feed_claim_handle, container, false);


		return rootView;
	}

	public void getAllProducts(boolean showLoader, LatLng selectedLatLng) {

	}
}
