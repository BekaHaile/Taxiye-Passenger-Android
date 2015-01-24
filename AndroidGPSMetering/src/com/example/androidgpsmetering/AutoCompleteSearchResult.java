package com.example.androidgpsmetering;


public class AutoCompleteSearchResult{
	
	public String name, placeId;
	
	public AutoCompleteSearchResult(String name, String placeId){
		this.name = name;
		this.placeId = placeId;
	}
	
	@Override
	public String toString() {
		return name + " " + placeId;
	}
}