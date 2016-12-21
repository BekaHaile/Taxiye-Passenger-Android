package com.sabkuchfresh.adapters;

import com.sabkuchfresh.retrofit.model.DeliveryAddress;

/**
 * Created by shankar on 12/14/16.
 */

public interface FreshAddressAdapterCallback {
	void onSlotSelected(int position, DeliveryAddress slot);
	void onEditClick(int position, DeliveryAddress slot);
}