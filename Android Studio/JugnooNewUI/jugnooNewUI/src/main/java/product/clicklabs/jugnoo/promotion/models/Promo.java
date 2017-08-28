package product.clicklabs.jugnoo.promotion.models;

import product.clicklabs.jugnoo.datastructure.PromoCoupon;

/**
 * Created by shankar on 01/05/17.
 */

public class Promo {

	private String name, clientId;
	private PromoCoupon promoCoupon;
	private int iconRes, lineColorRes;

	public Promo(String name, String clientId, PromoCoupon promoCoupon, int iconRes, int lineColorRes) {
		this.name = name;
		this.clientId = clientId;
		this.promoCoupon = promoCoupon;
		this.iconRes = iconRes;
		this.lineColorRes = lineColorRes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIconRes() {
		return iconRes;
	}

	public void setIconRes(int iconRes) {
		this.iconRes = iconRes;
	}

	public int getLineColorRes() {
		return lineColorRes;
	}

	public void setLineColorRes(int lineColorRes) {
		this.lineColorRes = lineColorRes;
	}

	public PromoCoupon getPromoCoupon() {
		return promoCoupon;
	}

	public void setPromoCoupon(PromoCoupon promoCoupon) {
		this.promoCoupon = promoCoupon;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
