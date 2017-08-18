package com.sabkuchfresh.home;

import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.utils.Log;

/**
 * Created by shankar on 09/06/17.
 */

public class MenusCart {

	private HashMap<Integer, RestaurantCart> restaurantCarts;


	public HashMap<Integer, RestaurantCart> getRestaurantCarts() {
		if(restaurantCarts == null){
			restaurantCarts = new HashMap<>();
		}
		return restaurantCarts;
	}

	public void putItemForRestaurant(MenusResponse.Vendor vendor, Item item){
		if(vendor != null) {
			if (item.getTotalQuantity() == 0) {
				getRestaurantCart(vendor).getItemHashMap().remove(item.getRestaurantItemId());
			} else {
				getRestaurantCart(vendor).getItemHashMap().put(item.getRestaurantItemId(), item);
			}
		}
	}

	public void updateItemForRestaurant(MenusResponse.Vendor vendor, Item item){
		if(vendor != null) {
			List<ItemSelected> itemSelecteds = new ArrayList<>();
			if (getRestaurantCart(vendor).getItemHashMap().containsKey(item.getRestaurantItemId())) {
				Item itemSaved = getRestaurantCart(vendor).getItemHashMap().get(item.getRestaurantItemId());
				if(itemSaved.getItemSelectedList().size() > 0){
					for(ItemSelected itemSelected : itemSaved.getItemSelectedList()){
						if (itemSelected.getQuantity() > 0) {
							itemSelected.setTotalPrice(item.getCustomizeItemsSelectedTotalPriceForItemSelected(itemSelected));
							itemSelecteds.add(itemSelected);
						}
					}
				}
				item.getItemSelectedList().clear();
				item.getItemSelectedList().addAll(itemSelecteds);
				getRestaurantCart(vendor).getItemHashMap().put(item.getRestaurantItemId(), item);
			}
		}
	}

	public void updateRestaurant(MenusResponse.Vendor vendor){
		if(getRestaurantCarts().containsKey(vendor.getRestaurantId())){
			RestaurantCart restaurantCart = getRestaurantCarts().get(vendor.getRestaurantId());
			restaurantCart.setRestaurant(vendor);
			getRestaurantCarts().put(vendor.getRestaurantId(), restaurantCart);
		}
	}



	public RestaurantCart getRestaurantCart(MenusResponse.Vendor vendor){
		if(getRestaurantCarts().containsKey(vendor.getRestaurantId())){
			return getRestaurantCarts().get(vendor.getRestaurantId());
		} else {
			HashMap<Integer, Item> itemHashMap = new HashMap<>();
			RestaurantCart restaurantCart = new RestaurantCart(vendor, itemHashMap);
			getRestaurantCarts().put(vendor.getRestaurantId(), restaurantCart);
			return restaurantCart;
		}
	}

	public RestaurantCart getRestaurantCartFilled(){
		for(RestaurantCart restaurantCart : getRestaurantCarts().values()){
			for(Item item : restaurantCart.getItemHashMap().values()){
				if(item.getTotalQuantity() > 0){
					return restaurantCart;
				}
			}
		}
		return null;
	}

	public void clearEmptyRestaurantCarts(){
		List<Integer> idsToRemove = new ArrayList<>();
		for(RestaurantCart restaurantCart : getRestaurantCarts().values()){
			if(restaurantCart.getItemHashMap().size() == 0){
				idsToRemove.add(restaurantCart.getRestaurant().getRestaurantId());
			}
		}
		for(Integer id : idsToRemove){
			getRestaurantCarts().remove(id);
		}
		Log.e("MenuCart", "emtry restaurants cleared");
	}


}
