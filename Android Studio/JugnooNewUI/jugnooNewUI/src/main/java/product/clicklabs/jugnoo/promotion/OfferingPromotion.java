package product.clicklabs.jugnoo.promotion;

import java.util.ArrayList;

import product.clicklabs.jugnoo.datastructure.PromoCoupon;

/**
 * Created by shankar on 01/05/17.
 */

public class OfferingPromotion {

	private String name;
	private int leftDrawableResource;
	private ArrayList<PromoCoupon> promoCoupons;

	public OfferingPromotion(String name, int leftDrawableResource, ArrayList<PromoCoupon> promoCoupons) {
		this.name = name;
		this.leftDrawableResource = leftDrawableResource;
		this.promoCoupons = promoCoupons;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLeftDrawableResource() {
		return leftDrawableResource;
	}

	public void setLeftDrawableResource(int leftDrawableResource) {
		this.leftDrawableResource = leftDrawableResource;
	}

	public ArrayList<PromoCoupon> getPromoCoupons() {
		return promoCoupons;
	}

	public void setPromoCoupons(ArrayList<PromoCoupon> promoCoupons) {
		this.promoCoupons = promoCoupons;
	}
}
