package product.clicklabs.jugnoo.utils;

import org.json.JSONException;
import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.SearchResult;

/**
 * Created by shankar on 2/12/16.
 */
public class LocalGson {

	public SearchResult getAutoCompleteSearchResultFromJSON(String jsonStr){
		try {
			JSONObject json = new JSONObject(jsonStr);
			return new SearchResult(json.optString("name", ""), json.optString("address", ""),
					json.optString("placeId", ""));
		} catch (JSONException e) {
			e.printStackTrace();
			return new SearchResult("", "", "");
		}
	}

	public String getJSONFromAutoCompleteSearchResult(SearchResult result){
		try {
			JSONObject json = new JSONObject();
			json.put("address", result.getAddress());
			json.put("name", result.getName());
			json.put("placeId", result.getPlaceId());
			return json.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}


}
