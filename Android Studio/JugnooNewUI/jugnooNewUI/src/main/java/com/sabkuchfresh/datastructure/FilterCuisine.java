package com.sabkuchfresh.datastructure;

/**
 * Created by shankar on 4/9/16.
 */
public class FilterCuisine {

	private String name;
	private int selected;

	public FilterCuisine(String name, int selected) {
		this.name = name;
		this.selected = selected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}
}