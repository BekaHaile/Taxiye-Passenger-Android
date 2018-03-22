package com.sabkuchfresh.home;

import com.sabkuchfresh.retrofit.model.SubItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import product.clicklabs.jugnoo.utils.Log;

/**
 * Created by shankar on 18/03/17.
 */

public class AppCart {

	private HashMap<Integer, HashMap<Integer, SubItem>> vendorCartHashMap;
	private int cityId;

	public AppCart(){
		vendorCartHashMap = new HashMap<>();
	}

	public HashMap<Integer, HashMap<Integer, SubItem>> getVendorCartHashMap() {
		return vendorCartHashMap;
	}

	public void saveSubItemToStore(Integer vendorId, SubItem subItem){
		if(subItem.getSubItemQuantitySelected() == 0){
			getSubItemHashMap(vendorId).remove(subItem.getSubItemId());
			if(getSubItemHashMap(vendorId).size() == 0){
				vendorCartHashMap.remove(vendorId);
			}
		} else {
			if(!getSubItemHashMap(vendorId).containsKey(subItem.getSubItemId())){
				getSubItemHashMap(vendorId).put(subItem.getSubItemId(), subItem);
			}
		}
		Log.i(AppCart.class.getSimpleName(), "save to cart");
	}


	public double getSubTotal(){
		double total = 0d;
		for(Integer vendorId : vendorCartHashMap.keySet()){
			total = total + getCartTotal(vendorId);
		}
		return total;
	}

	public double getCartTotal(Integer vendorId){
		double total = 0d;
		for(SubItem subItem : getSubItemHashMap(vendorId).values()){
			total = total + (((double) subItem.getSubItemQuantitySelected()) * subItem.getPrice());
		}
		return total;
	}

	public Collection<SubItem> getCartItems(Integer vendorId){
		return getSubItemHashMap(vendorId).values();
	}

	public HashMap<Integer, SubItem> getSubItemHashMap(Integer vendorId){
		if(vendorCartHashMap.containsKey(vendorId)) {
			return vendorCartHashMap.get(vendorId);
		} else {
			HashMap<Integer, SubItem> subItemHashMap = new HashMap<>();
			vendorCartHashMap.put(vendorId, subItemHashMap);
			return subItemHashMap;
		}
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public void removeEmptyItems(){
		ArrayList<Integer> removeIds = new ArrayList<>();
		for(Integer id : vendorCartHashMap.keySet()){
			if(vendorCartHashMap.get(id).values().size() == 0){
				removeIds.add(id);
			}
		}
		for(Integer id : removeIds){
			vendorCartHashMap.remove(id);
		}
		Log.e("AppCart", "emtry restaurants cleared");
	}

}
