package com.sabkuchfresh.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by shankar on 08/06/17.
 */

public class MenusCartSelectedLayout {

	private FreshActivity activity;
	private RelativeLayout rlMenusCartSelected;
	private TextView tvRestName;
	private ImageView ivDeleteCart;
	private int vendorId;

	public MenusCartSelectedLayout(FreshActivity activity){
		this.activity = activity;
	}

	public void init(View root){
		rlMenusCartSelected = (RelativeLayout) root;
		tvRestName = (TextView) root.findViewById(R.id.tvRestName);
		ivDeleteCart = (ImageView) root.findViewById(R.id.ivDeleteCart);

		rlMenusCartSelected.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(vendorId > 0) {
					activity.fetchRestaurantMenuAPI(vendorId);
				}
			}
		});

		ivDeleteCart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.deleteCart();
			}
		});

	}


	public void checkForVisibility(){
		try {
			JSONObject jsonSavedCart = new JSONObject(Prefs.with(activity).getString(Constants.SP_MENUS_CART, Constants.EMPTY_JSON_OBJECT));
			vendorId = jsonSavedCart.optInt(Constants.KEY_RESTAURANT_ID, -1);
			if(vendorId > 0){
				String oldRestaurantName = jsonSavedCart.optString(Constants.KEY_RESTAURANT_NAME, "");
				rlMenusCartSelected.setVisibility(View.VISIBLE);
				tvRestName.setText(oldRestaurantName);
			} else {
				rlMenusCartSelected.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setVisibility(int visibility){
		if(visibility == View.VISIBLE){
			rlMenusCartSelected.setVisibility(View.VISIBLE);
		} else {
			rlMenusCartSelected.setVisibility(View.GONE);
		}
	}

}
