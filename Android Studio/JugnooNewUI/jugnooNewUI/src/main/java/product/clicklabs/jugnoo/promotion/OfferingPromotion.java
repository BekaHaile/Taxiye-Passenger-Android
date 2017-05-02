package product.clicklabs.jugnoo.promotion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;

/**
 * Created by shankar on 01/05/17.
 */

public class OfferingPromotion {

	private String name, clientId;
	private int leftDrawableResource;
	private ArrayList<PromoCoupon> promoCoupons;

	public OfferingPromotion(String name, String clientId, int leftDrawableResource, ArrayList<PromoCoupon> promoCoupons) {
		this.name = name;
		this.clientId = clientId;
		this.leftDrawableResource = leftDrawableResource;

		for(PromoCoupon promoCoupon : promoCoupons){
			if(promoCoupon instanceof CouponInfo){
				((CouponInfo)promoCoupon).setCheckWithCouponId(true);
			}
			promoCoupon.setRepeatedCount(Collections.frequency(promoCoupons, promoCoupon));
		}

		Set set = new TreeSet(new Comparator<PromoCoupon>() {
			@Override
			public int compare(PromoCoupon o1, PromoCoupon o2) {
				if (o1.equals(o2)) {
					return 0;
				}
				return 1;
			}
		});
		set.addAll(promoCoupons);
		this.promoCoupons = new ArrayList<>(set);
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

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
