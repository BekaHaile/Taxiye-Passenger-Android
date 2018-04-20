package product.clicklabs.jugnoo.datastructure;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class PromoCoupon implements Serializable {

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
	public abstract Integer getDeliveryCustomer();
	public abstract Integer getPay();
	public abstract String getInvalidMessage();
	public abstract String getExpiryDate();
	public abstract ArrayList<Integer> getAllowedVehicles();
	public abstract int getOperatorId();
	public abstract boolean showPromoBox();
	public abstract String messageToDisplay();
	public abstract boolean isPromoApplied();
	public  abstract void setIsPromoApplied(boolean isPromoApplied);
	public  abstract void setMessageToDisplay(String messageToDisplay);
	public abstract String getPromoName();
	private int repeatedCount;

	public boolean matchPromoCoupon(PromoCoupon c2) {
		try {
			PromoCoupon c1 = this;
			if (((c1 instanceof CouponInfo && c2 instanceof CouponInfo) || (c1 instanceof PromotionInfo && c2 instanceof PromotionInfo))
					&& c1.getId() == c2.getId()
					&& (c1.getMasterCoupon() != null && c1.getMasterCoupon().equals(c2.getMasterCoupon()))
					&& (c1.getAutos().equals(c2.getAutos()))
					&& (c1.getFresh().equals(c2.getFresh()))
					&& (c1.getMeals().equals(c2.getMeals()))
					&& (c1.getGrocery().equals(c2.getGrocery()))
					&& (c1.getMenus().equals(c2.getMenus()))
					&& (c1.getDeliveryCustomer().equals(c2.getDeliveryCustomer()))
					&& (c1.getPay().equals(c2.getPay()))) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isVehicleTypeExists(int vehicleType, int operatorId){
		if(operatorId != getOperatorId()){
			return false;
		}
		if(getAllowedVehicles()==null || getAllowedVehicles().size()<=0) {
			return true;
		}

		for(Integer vehicleTypeAllowed:getAllowedVehicles()){
			if(vehicleType==vehicleTypeAllowed){
				return true;
			}
		}
		return false;
	}

	public int getRepeatedCount() {
		return repeatedCount;
	}

	public void setRepeatedCount(int repeatedCount) {
		this.repeatedCount = repeatedCount;
	}


}
