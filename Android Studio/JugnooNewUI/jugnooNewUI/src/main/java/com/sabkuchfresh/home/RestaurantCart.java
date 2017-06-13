package com.sabkuchfresh.home;

import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;

import java.util.HashMap;

/**
 * Created by shankar on 09/06/17.
 */

public class RestaurantCart {

	private MenusResponse.Vendor restaurant;
	private HashMap<Integer, Item> itemHashMap;

	public RestaurantCart(MenusResponse.Vendor restaurant, HashMap<Integer, Item> itemHashMap){
		this.restaurant = restaurant;
		this.itemHashMap = itemHashMap;
	}

	public MenusResponse.Vendor getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(MenusResponse.Vendor restaurant) {
		this.restaurant = restaurant;
	}

	public HashMap<Integer, Item> getItemHashMap() {
		return itemHashMap;
	}

}
