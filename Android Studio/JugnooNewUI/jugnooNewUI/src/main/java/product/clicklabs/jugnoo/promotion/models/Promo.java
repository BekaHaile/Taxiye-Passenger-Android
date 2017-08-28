package product.clicklabs.jugnoo.promotion.models;

import product.clicklabs.jugnoo.datastructure.PromoCoupon;

/**
 * Created by shankar on 01/05/17.
 */

public class Promo {

	private String name;
	private PromoCoupon promoCoupon;
	private int iconRes, lineColorRes;

	public Promo(String name, PromoCoupon promoCoupon, int iconRes, int lineColorRes) {
		this.name = name;
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
}
