package product.clicklabs.jugnoo.home.models;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;

import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 4/2/16.
 */
public enum VehicleIconSet {
	ORANGE_AUTO("ORANGE AUTO", R.drawable.ic_auto_tab_normal, R.drawable.ic_auto_tab_selected,
			R.drawable.ic_auto_marker,
			R.drawable.ic_auto_request_normal,
			R.drawable.ic_auto_request_pressed,
			R.drawable.ic_auto_grey),

	YELLOW_AUTO("YELLOW AUTO", R.drawable.ic_auto_tab_normal, R.drawable.ic_auto_tab_selected,
			R.drawable.ic_auto_marker,
			R.drawable.ic_auto_request_normal,
			R.drawable.ic_auto_request_pressed,
			R.drawable.ic_auto_grey),

	RED_AUTO("RED AUTO", R.drawable.ic_auto_tab_normal, R.drawable.ic_auto_tab_selected,
			R.drawable.ic_auto_marker,
			R.drawable.ic_auto_request_normal,
			R.drawable.ic_auto_request_pressed,
			R.drawable.ic_auto_grey),






	ORANGE_BIKE("ORANGE BIKE", R.drawable.ic_bike_tab_normal, R.drawable.ic_bike_tab_selected,
			R.drawable.ic_bike_marker,
			R.drawable.ic_bike_request_normal,
			R.drawable.ic_bike_request_pressed,
			R.drawable.ic_history_bike),

	YELLOW_BIKE("YELLOW BIKE", R.drawable.ic_bike_tab_normal, R.drawable.ic_bike_tab_selected,
			R.drawable.ic_bike_marker,
			R.drawable.ic_bike_request_normal,
			R.drawable.ic_bike_request_pressed,
			R.drawable.ic_history_bike),

	RED_BIKE("RED BIKE", R.drawable.ic_bike_tab_normal, R.drawable.ic_bike_tab_selected,
			R.drawable.ic_bike_marker,
			R.drawable.ic_bike_request_normal,
			R.drawable.ic_bike_request_pressed,
			R.drawable.ic_history_bike),



	ORANGE_CAR("ORANGE CAR", R.drawable.ic_car_tab_normal, R.drawable.ic_car_tab_selected,
			R.drawable.ic_car_marker,
			R.drawable.ic_car_request_normal,
			R.drawable.ic_car_request_pressed,
			R.drawable.ic_history_car),

	YELLOW_CAR("YELLOW CAR", R.drawable.ic_car_tab_normal, R.drawable.ic_car_tab_selected,
			R.drawable.ic_car_marker,
			R.drawable.ic_car_request_normal,
			R.drawable.ic_car_request_pressed,
			R.drawable.ic_history_car),

	RED_CAR("RED CAR", R.drawable.ic_car_tab_normal, R.drawable.ic_car_tab_selected,
			R.drawable.ic_car_marker,
			R.drawable.ic_car_request_normal,
			R.drawable.ic_car_request_pressed,
			R.drawable.ic_history_car),


	HELICOPTER("HELICOPTER", R.drawable.ic_helicopter_tab, R.drawable.ic_helicopter_tab,
			R.drawable.ic_helicopter_marker,
			R.drawable.ic_helicopter_request_normal,
			R.drawable.ic_helicopter_request_pressed,
			R.drawable.ic_helicopter_invoice),


	ERICKSHAW("E-RICK", R.drawable.ic_erickshaw_tab_normal, R.drawable.ic_erickshaw_tab_selected,
			R.drawable.ic_erickshaw_marker,
			R.drawable.ic_erickshaw_request_normal,
			R.drawable.ic_erickshaw_request_pressed,
			R.drawable.ic_erickshaw_grey),


	TRANSPORT("TRANSPORT", R.drawable.ic_transport_tab_normal, R.drawable.ic_transport_tab_selected,
			R.drawable.ic_transport_marker,
			R.drawable.ic_transport_request_normal,
			R.drawable.ic_transport_request_pressed,
			R.drawable.ic_transport_tab_normal),

	;


	private String name;
	private int iconTab;
	private int iconTabSelected;
	private int iconMarker;
	private int iconRequestNormal;
	private int iconRequestPressed;
	private int iconInvoice;

	VehicleIconSet(String name, int iconTab, int iconTabSelected,
				   int iconMarker, int iconRequestNormal, int iconRequestPressed, int iconInvoice){
		this.name = name;
		this.iconTab = iconTab;
		this.iconTabSelected = iconTabSelected;
		this.iconMarker = iconMarker;
		this.iconRequestNormal = iconRequestNormal;
		this.iconRequestPressed = iconRequestPressed;
		this.iconInvoice = iconInvoice;
	}

	public String getName() {
		return name;
	}

	public int getIconTab() {
		return iconTab;
	}

	public int getIconTabSelected() {
		return iconTabSelected;
	}

	public int getIconMarker() {
		return iconMarker;
	}

	private int getIconRequestNormal() {
		return iconRequestNormal;
	}

	private int getIconRequestPressed() {
		return iconRequestPressed;
	}

	public int getIconInvoice() {
		return iconInvoice;
	}

	public StateListDrawable getRequestSelector(Context context){
		StateListDrawable stateListDrawable = new StateListDrawable();
		stateListDrawable.addState(new int[]{android.R.attr.state_pressed},
				context.getResources().getDrawable(getIconRequestPressed()));
		stateListDrawable.addState(new int[]{},
				context.getResources().getDrawable(getIconRequestNormal()));
		return stateListDrawable;
	}
}
