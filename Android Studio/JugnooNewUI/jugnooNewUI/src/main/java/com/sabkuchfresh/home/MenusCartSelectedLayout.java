package com.sabkuchfresh.home;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.feed.ui.dialogs.DialogPopupTwoButtonCapsule;

import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 08/06/17.
 */

public class MenusCartSelectedLayout {

	private FreshActivity activity;
	private RelativeLayout rlMenusCartSelected, rlMenusCartSelectedInner;
	private TextView tvRestName;
	private LinearLayout llDeleteCart;
	private int vendorId;

	public RelativeLayout getRlMenusCartSelectedInner() {
		return rlMenusCartSelectedInner;
	}

	public MenusCartSelectedLayout(FreshActivity activity, View root){
		this.activity = activity;
		init(root);
	}

	private void init(View root){
		rlMenusCartSelected = (RelativeLayout) root;
		rlMenusCartSelected.setVisibility(View.GONE);
		rlMenusCartSelectedInner = (RelativeLayout) root.findViewById(R.id.rlMenusCartSelectedInner);
		tvRestName = (TextView) root.findViewById(R.id.tvRestName);
		llDeleteCart = (LinearLayout) root.findViewById(R.id.llDeleteCart);

		rlMenusCartSelectedInner.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(vendorId > 0) {
					activity.fetchRestaurantMenuAPI(vendorId, true, null, null, -1, null);
				}
			}
		});

		llDeleteCart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getDialogPopupTwoButtonCapsule().show();
			}
		});

	}


	public void checkForVisibility(){
		try {
			RestaurantCart restaurantCart = activity.getMenusCart().getRestaurantCartFilled();
			if(restaurantCart != null){
				vendorId = restaurantCart.getRestaurant().getRestaurantId();
				String oldRestaurantName = restaurantCart.getRestaurant().getName();
				rlMenusCartSelected.setVisibility(View.VISIBLE);
				tvRestName.setText(oldRestaurantName);
			} else {
				rlMenusCartSelected.setVisibility(View.GONE);
			}

//			JSONObject jsonSavedCart = new JSONObject(Prefs.with(activity).getString(Constants.SP_MENUS_CART, Constants.EMPTY_JSON_OBJECT));
//			vendorId = jsonSavedCart.optInt(Constants.KEY_RESTAURANT_ID, -1);
//			if(vendorId > 0){
//				String oldRestaurantName = jsonSavedCart.optString(Constants.KEY_RESTAURANT_NAME, "");
//				rlMenusCartSelected.setVisibility(View.VISIBLE);
//				tvRestName.setText(oldRestaurantName);
//			} else {
//				rlMenusCartSelected.setVisibility(View.GONE);
//			}
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

	private DialogPopupTwoButtonCapsule dialogPopupTwoButtonCapsule;
	private DialogPopupTwoButtonCapsule getDialogPopupTwoButtonCapsule(){
		if(dialogPopupTwoButtonCapsule == null){
			dialogPopupTwoButtonCapsule = new DialogPopupTwoButtonCapsule(new DialogPopupTwoButtonCapsule.DialogCallback() {
				@Override
				public void onLeftClick() {
					activity.clearMenusCart();
					activity.getMenusCartSelectedLayout().checkForVisibility();
				}

				@Override
				public void onRightClick() {
				}
			}, R.style.Feed_Popup_Theme, activity, activity.getString(R.string.discard_all_items_in_cart));
		}
		return dialogPopupTwoButtonCapsule;
	}
}
