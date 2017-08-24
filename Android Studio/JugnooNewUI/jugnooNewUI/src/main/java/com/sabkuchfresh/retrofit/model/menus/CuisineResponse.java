package com.sabkuchfresh.retrofit.model.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sabkuchfresh.datastructure.FilterCuisine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by shankar on 11/15/16.
 */
public class CuisineResponse  {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;



	public Integer getFlag() {
		return flag;
	}

	public String getMessage() {
		return message;
	}


	@SerializedName("data")
	@Expose
	public Map<String, String> ranges;

	public Map<String, String> getRangesMap() {
		return ranges;
	}

	public ArrayList<FilterCuisine> getRanges() {
		if(ranges==null)
			return null;

		ArrayList<FilterCuisine> result = new ArrayList<>(ranges.size());
		for(String id : ranges.keySet()) {
			result.add(new FilterCuisine(ranges.get(id),Integer.parseInt(id),0));
		}
		return result;
	}




}
