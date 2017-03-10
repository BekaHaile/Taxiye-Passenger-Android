package product.clicklabs.jugnoo.datastructure;

public abstract class PromoCoupon {

	public abstract int getId();
	public abstract String getTitle();
	public abstract Double getDiscount();
	public abstract Integer getMasterCoupon();
	public abstract boolean equals(Object o);
	public abstract Integer getIsSelected();
	public abstract Integer getIsValid();

	public abstract Integer getAutos();
	public abstract Integer getFresh();
	public abstract Integer getMeals();
	public abstract Integer getGrocery();
	public abstract Integer getMenus();
	public abstract Integer getPay();
	public abstract String getInvalidMessage();

	public boolean matchPromoCoupon(PromoCoupon c2) {
		try {
			PromoCoupon c1 = this;
			if (c1.getId() == c2.getId()
					&& (c1.getMasterCoupon() != null && c1.getMasterCoupon().equals(c2.getMasterCoupon()))
					&& (c1.getAutos().equals(c2.getAutos()))
					&& (c1.getFresh().equals(c2.getFresh()))
					&& (c1.getMeals().equals(c2.getMeals()))
					&& (c1.getGrocery().equals(c2.getGrocery()))
					&& (c1.getMenus().equals(c2.getMenus()))
					&& (c1.getPay().equals(c2.getPay()))) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
