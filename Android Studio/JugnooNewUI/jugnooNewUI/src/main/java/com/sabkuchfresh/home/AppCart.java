package com.sabkuchfresh.home;

import com.sabkuchfresh.retrofit.model.DeliveryStore;
import com.sabkuchfresh.retrofit.model.DeliveryStoreCart;
import com.sabkuchfresh.retrofit.model.SubItem;

import java.util.HashMap;

import product.clicklabs.jugnoo.utils.Log;

/**
 * Created by shankar on 18/03/17.
 */

public class AppCart {

	private HashMap<Integer, DeliveryStoreCart> deliveryStoreCartHashMap;
	private int cityId;

	public AppCart(){
		deliveryStoreCartHashMap = new HashMap<>();
	}

	public HashMap<Integer, DeliveryStoreCart> getDeliveryStoreCartHashMap() {
		return deliveryStoreCartHashMap;
	}

	public void setDeliveryStoreCartHashMap(HashMap<Integer, DeliveryStoreCart> deliveryStoreCartHashMap) {
		this.deliveryStoreCartHashMap = deliveryStoreCartHashMap;
	}

	public void saveSubItemToStore(DeliveryStore deliveryStore, SubItem subItem){
		DeliveryStoreCart deliveryStoreCart = getDeliveryStoreCart(deliveryStore);
		if(subItem.getSubItemQuantitySelected() == 0){
			deliveryStoreCart.getSubItemHashMap().remove(subItem.getSubItemId());
		} else {
			if(!deliveryStoreCart.getSubItemHashMap().containsKey(subItem.getSubItemId())){
				deliveryStoreCart.getSubItemHashMap().put(subItem.getSubItemId(), subItem);
			}
		}
		Log.i(AppCart.class.getSimpleName(), "save to cart");
	}


	public double getSubTotal(){
		double total = 0d;
		for(DeliveryStoreCart deliveryStoreCart : deliveryStoreCartHashMap.values()){
			total = total + deliveryStoreCart.getCartTotal();
		}
		return total;
	}

	public DeliveryStoreCart getDeliveryStoreCart(DeliveryStore deliveryStore){
		DeliveryStoreCart deliveryStoreCart;
		if(deliveryStoreCartHashMap.containsKey(deliveryStore.getVendorId())){
			deliveryStoreCart = deliveryStoreCartHashMap.get(deliveryStore.getVendorId());
			deliveryStoreCart.setDeliveryStore(deliveryStore);
		} else {
			deliveryStoreCart = new DeliveryStoreCart(deliveryStore);
			deliveryStoreCartHashMap.put(deliveryStore.getVendorId(), deliveryStoreCart);
		}
		return deliveryStoreCart;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
}
