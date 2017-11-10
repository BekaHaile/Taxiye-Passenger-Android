package com.sabkuchfresh.home;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.feed.ui.dialogs.DialogPopupTwoButtonCapsule;
import com.sabkuchfresh.fragments.MenusFragment;

import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 08/06/17.
 */

public class MenusCartSelectedLayout {

	private FreshActivity activity;
	private RelativeLayout rlMenusCartSelected, rlMenusCartSelectedInner;
	private TextView tvRestName;
	private LinearLayout llDeleteCart;
	private View disableView;
	private int vendorId;
	private boolean isViewDisabled;

	public RelativeLayout getRlMenusCartSelected() {
		return rlMenusCartSelected;
	}

	public RelativeLayout getRlMenusCartSelectedInner() {
		return rlMenusCartSelectedInner;
	}

	public MenusCartSelectedLayout(FreshActivity activity, View root){
		this.activity = activity;
		init(root);
	}

	private void init(View root){
		rlMenusCartSelected = (RelativeLayout) root;
		rlMenusCartSelectedInner = (RelativeLayout) root.findViewById(R.id.rlMenusCartSelectedInner);
		tvRestName = (TextView) root.findViewById(R.id.tvRestName);
		llDeleteCart = (LinearLayout) root.findViewById(R.id.llDeleteCart);
		disableView = root.findViewById(R.id.disable_view);

		rlMenusCartSelectedInner.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isViewDisabled && vendorId > 0) {
					activity.fetchRestaurantMenuAPI(vendorId, true, null, null, -1, null);
				}
			}
		});

		llDeleteCart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isViewDisabled) {
					getDialogPopupTwoButtonCapsule().show();
				}
			}
		});

	}


	public void checkForVisibility(){
		try {
			RestaurantCart restaurantCart = activity.getMenusCart().getRestaurantCartFilled();
			if(restaurantCart != null && activity.getTopFragment() instanceof MenusFragment){
				vendorId = restaurantCart.getRestaurant().getRestaurantId();
				String oldRestaurantName = restaurantCart.getRestaurant().getName();
				rlMenusCartSelected.setVisibility(View.VISIBLE);
				tvRestName.setText(oldRestaurantName);
			} else {
				rlMenusCartSelected.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		activity.adjustFabMarginsForDeliveryOffering();


	}



	public void setVisibility(int visibility){
		if(visibility == View.VISIBLE && activity.getTopFragment() instanceof MenusFragment){
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
					activity.clearMenusCart(activity.getAppType());
					activity.getMenusCartSelectedLayout().checkForVisibility();
				}

				@Override
				public void onRightClick() {
				}
			}, R.style.Feed_Popup_Theme, activity, activity.getString(R.string.discard_all_items_in_cart));
		}
		return dialogPopupTwoButtonCapsule;
	}

	public void disableView(boolean isDisable){
		this.isViewDisabled = isDisable;
		if(disableView!=null){
			disableView.setVisibility(isDisable?View.VISIBLE:View.GONE);

		}
	}
}
