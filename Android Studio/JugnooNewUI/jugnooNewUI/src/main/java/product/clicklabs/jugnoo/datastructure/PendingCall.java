package product.clicklabs.jugnoo.datastructure;

/**
 * Created by shankar on 2/2/16.
 */
public enum PendingCall {

	SKIP_RATING_BY_CUSTOMER("/skip_rating_by_customer"),
	EMERGENCY_ALERT("/emergency/alert")
	;

	private String path;

	PendingCall(String path){
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}