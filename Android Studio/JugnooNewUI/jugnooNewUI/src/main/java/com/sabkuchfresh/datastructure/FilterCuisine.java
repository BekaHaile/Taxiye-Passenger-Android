package com.sabkuchfresh.datastructure;

/**
 * Created by shankar on 4/9/16.
 */
public class FilterCuisine {

	private int id;
	private String name;
	private int selected;

	public FilterCuisine(String name,int id, int selected) {
		this.name = name;
		this.id = id;
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

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof FilterCuisine && ((FilterCuisine)obj).id == id;
	}
}
