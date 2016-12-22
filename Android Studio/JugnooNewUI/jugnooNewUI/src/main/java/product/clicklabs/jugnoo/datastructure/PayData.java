package product.clicklabs.jugnoo.datastructure;

import java.util.ArrayList;

import product.clicklabs.jugnoo.retrofit.model.LoginResponse;

/**
 * Created by shankar on 8/17/16.
 */
public class PayData {
	private ArrayList<PromoCoupon> promoCoupons = new ArrayList<>();

	private LoginResponse.Pay pay;

	public PayData(LoginResponse.Pay pay) {
		this.pay = pay;

	}

	public ArrayList<PromoCoupon> getPromoCoupons() {
		return promoCoupons;
	}

	public void setPromoCoupons(ArrayList<PromoCoupon> promoCoupons) {
		this.promoCoupons = promoCoupons;
	}

	public LoginResponse.Pay getPay() {
		return pay;
	}

	public void setPay(LoginResponse.Pay pay) {
		this.pay = pay;
	}
}
