package product.clicklabs.jugnoo.datastructure;

import java.util.ArrayList;

/**
 * Created by gurmail on 24/08/16.
 */
public class DeliveryData {
    private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();
    public DeliveryData() {

    }

    public ArrayList<PromoCoupon> getPromoCoupons() {
        return promoCoupons;
    }

    public void setPromoCoupons(ArrayList<PromoCoupon> promoCoupons) {
        this.promoCoupons = promoCoupons;
    }
}
