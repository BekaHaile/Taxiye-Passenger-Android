package product.clicklabs.jugnoo.datastructure;

public abstract class PromoCoupon {

	public abstract int getId();
	public abstract String getTitle();
	public abstract Double getDiscount();
	public abstract Integer getMasterCoupon();
	public abstract boolean equals(Object o);

	public boolean matchPromoCoupon(PromoCoupon pc2) {
		PromoCoupon pc1 = this;
		if (pc1 instanceof CouponInfo && pc2 instanceof CouponInfo) {
			CouponInfo c1 = (CouponInfo) pc1;
			CouponInfo c2 = (CouponInfo) pc2;
			if (c1.getId() == c2.getId()
					&& (c1.getMasterCoupon() != null && c1.getMasterCoupon().equals(c2.getMasterCoupon()))
					&& (c1.getAutos().equals(c2.getAutos()))
					&& (c1.getFresh().equals(c2.getFresh()))
					&& (c1.getMeals().equals(c2.getMeals()))
					&& (c1.getGrocery().equals(c2.getGrocery()))) {
				return true;
			}
		} else if (pc1 instanceof PromotionInfo && pc2 instanceof PromotionInfo) {
			PromotionInfo c1 = (PromotionInfo) pc1;
			PromotionInfo c2 = (PromotionInfo) pc2;
			if (c1.getId() == c2.getId()
					&& (c1.getMasterCoupon() != null && c1.getMasterCoupon().equals(c2.getMasterCoupon()))
					&& (c1.getAutos().equals(c2.getAutos()))
					&& (c1.getFresh().equals(c2.getFresh()))
					&& (c1.getMeals().equals(c2.getMeals()))
					&& (c1.getGrocery().equals(c2.getGrocery()))) {
				return true;
			}
		}
		return false;
	}

}
