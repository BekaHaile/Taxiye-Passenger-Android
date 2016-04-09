package product.clicklabs.jugnoo.datastructure;

/**
 * Created by shankar on 3/29/16.
 */
public enum LoginVia {
	EMAIL(1),
	FACEBOOK(2),
	GOOGLE(3),
	ACCESS(4),
	EMAIL_OTP(5),
	FACEBOOK_OTP(6),
	GOOGLE_OTP(7)
	;

	private int ordinal;

	LoginVia(int ordinal){
		this.ordinal = ordinal;
	}

	public int getOrdinal(){
		return ordinal;
	}

}
