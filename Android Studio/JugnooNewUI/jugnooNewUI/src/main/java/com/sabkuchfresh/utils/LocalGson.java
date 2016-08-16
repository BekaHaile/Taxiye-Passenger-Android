package com.sabkuchfresh.utils;

import com.sabkuchfresh.datastructure.AutoCompleteSearchResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shankar on 2/12/16.
 */
public class LocalGson {

	public AutoCompleteSearchResult getAutoCompleteSearchResultFromJSON(String jsonStr){
		try {
			JSONObject json = new JSONObject(jsonStr);
			return new AutoCompleteSearchResult(json.optString("name", ""), json.optString("address", ""),
					json.optString("placeId", ""));
		} catch (JSONException e) {
			e.printStackTrace();
			return new AutoCompleteSearchResult("", "", "");
		}
	}

	public String getJSONFromAutoCompleteSearchResult(AutoCompleteSearchResult result){
		try {
			JSONObject json = new JSONObject();
			json.put("address", result.address);
			json.put("name", result.name);
			json.put("placeId", result.placeId);
			return json.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

}
