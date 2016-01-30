package product.clicklabs.jugnoo.utils;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

public class CustomHttpRequesterFinal extends HttpRequesterFinal{

	
	@Override
	public String getJSONFromUrlFinal(String url) {
		return super.getJSONFromUrlFinal(url);
	}
	
	@Override
	public String getJSONFromUrlParamsFinal(String url, ArrayList<NameValuePair> nameValuePairs) {
		return super.getJSONFromUrlParamsFinal(url, nameValuePairs);
	}
	
}
