package com.sabkuchfresh.retrofit.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shankar on 18/03/17.
 */

public class DeliveryStoreCart {

	private DeliveryStore deliveryStore;
	private HashMap<Integer, SubItem> subItemHashMap;

	public DeliveryStoreCart(DeliveryStore deliveryStore){
		this.deliveryStore = deliveryStore;
		this.subItemHashMap = new HashMap<>();
	}

	public DeliveryStore getDeliveryStore() {
		return deliveryStore;
	}

	public void setDeliveryStore(DeliveryStore deliveryStore) {
		this.deliveryStore = deliveryStore;
	}

	public HashMap<Integer, SubItem> getSubItemHashMap() {
		return subItemHashMap;
	}

	public void setSubItemHashMap(HashMap<Integer, SubItem> subItemHashMap) {
		this.subItemHashMap = subItemHashMap;
	}

	public double getCartTotal(){
		double total = 0d;
		for(SubItem subItem : subItemHashMap.values()){
			total = total + (((double) subItem.getSubItemQuantitySelected()) * subItem.getPrice());
		}
		return total;
	}

	public ArrayList<SubItem> getCartItems(){
		return (ArrayList<SubItem>) subItemHashMap.values();
	}
}
