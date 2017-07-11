package com.sabkuchfresh.pros.models;

/**
 * Created by shankar on 04/07/17.
 */

public class TimeDisplay{
	private String display;
	private String value;
	private boolean selected;

	public TimeDisplay(String display, String value) {
		this.display = display;
		this.value = value;
		this.selected = false;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
