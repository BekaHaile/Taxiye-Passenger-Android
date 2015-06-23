package product.clicklabs.jugnoo.datastructure;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

public class PendingAPICall {
	
	public int id;
	public String url;
	public ArrayList<NameValuePair> nameValuePairs;

	public PendingAPICall(int id, String url, ArrayList<NameValuePair> nameValuePairs){
		this.id = id;
		this.url = url;
		this.nameValuePairs = nameValuePairs;
	}

	@Override
	public String toString() {
		return id + " " + url + " " + nameValuePairs.toString();
	}
}
