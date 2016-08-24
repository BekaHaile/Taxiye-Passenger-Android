package product.clicklabs.jugnoo.datastructure;

import java.util.ArrayList;

/**
 * Created by shankar on 8/17/16.
 */
public class MealsData {

	private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();
	public MealsData() {

	}

	public ArrayList<PromoCoupon> getPromoCoupons() {
		return promoCoupons;
	}

	public void setPromoCoupons(ArrayList<PromoCoupon> promoCoupons) {
		this.promoCoupons = promoCoupons;
	}
}
