package product.clicklabs.jugnoo.home.models;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;

import product.clicklabs.jugnoo.R;

/**
 * Created by shankar on 4/2/16.
 */
public enum VehicleIconSet {
	ORANGE_AUTO("ORANGE AUTO", R.drawable.ic_auto_tab_orange,
			R.drawable.ic_auto_marker,
			R.drawable.ic_auto_request_normal,
			R.drawable.ic_auto_request_pressed,
			R.drawable.ic_auto_invoice),

	BIKE("bike", R.drawable.ic_bike_tab,
			R.drawable.ic_bike_marker,
			R.drawable.ic_bike_request_normal,
			R.drawable.ic_bike_request_pressed,
			R.drawable.ic_bike_invoice),

	YELLOW_AUTO("YELLOW AUTO", R.drawable.ic_auto_tab_yellow,
			R.drawable.ic_auto_marker,
			R.drawable.ic_auto_request_normal,
			R.drawable.ic_auto_request_pressed,
			R.drawable.ic_auto_invoice),

	RED_AUTO("RED AUTO", R.drawable.ic_auto_tab_red,
			R.drawable.ic_auto_marker,
			R.drawable.ic_auto_request_normal,
			R.drawable.ic_auto_request_pressed,
			R.drawable.ic_auto_invoice)
	;


	private String name;
	private int iconTab;
	private int iconMarker;
	private int iconRequestNormal;
	private int iconRequestPressed;
	private int iconInvoice;

	VehicleIconSet(String name, int iconTab, int iconMarker, int iconRequestNormal, int iconRequestPressed, int iconInvoice){
		this.name = name;
		this.iconTab = iconTab;
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
