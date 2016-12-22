package com.jugnoo.pay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 5/7/16.
 */
public class Campaigns {

	@SerializedName("map_left_button")
	@Expose
	private MapLeftButton mapLeftButton;

	/**
	 *
	 * @return
	 * The mapLeftButton
	 */
	public MapLeftButton getMapLeftButton() {
		return mapLeftButton;
	}

	/**
	 *
	 * @param mapLeftButton
	 * The map_left_button
	 */
	public void setMapLeftButton(MapLeftButton mapLeftButton) {
		this.mapLeftButton = mapLeftButton;
	}

	public class MapLeftButton {

		@SerializedName("campaign_id")
		@Expose
		private Integer campaignId;
		@SerializedName("ab")
		@Expose
		private Integer ab;
		@SerializedName("images")
		@Expose
		private List<String> images = new ArrayList<String>();
		@SerializedName("text")
		@Expose
		private String text;
		@SerializedName("show_campaign_after_avail")
		@Expose
		private Integer showCampaignAfterAvail;

		/**
		 *
		 * @return
		 * The campaignId
		 */
		public Integer getCampaignId() {
			return campaignId;
		}

		/**
		 *
		 * @param campaignId
		 * The campaign_id
		 */
		public void setCampaignId(Integer campaignId) {
			this.campaignId = campaignId;
		}

		/**
		 *
		 * @return
		 * The ab
		 */
		public Integer getAb() {
			return ab;
		}

		/**
		 *
		 * @param ab
		 * The ab
		 */
		public void setAb(Integer ab) {
			this.ab = ab;
		}

		/**
		 *
		 * @return
		 * The images
		 */
		public List<String> getImages() {
			return images;
		}

		/**
		 *
		 * @param images
		 * The images
		 */
		public void setImages(List<String> images) {
			this.images = images;
		}

		/**
		 *
		 * @return
		 * The text
		 */
		public String getText() {
			return text;
		}

		/**
		 *
		 * @param text
		 * The text
		 */
		public void setText(String text) {
			this.text = text;
		}

		public Integer getShowCampaignAfterAvail() {
			return showCampaignAfterAvail;
		}

		public void setShowCampaignAfterAvail(Integer showCampaignAfterAvail) {
			this.showCampaignAfterAvail = showCampaignAfterAvail;
		}

	}

}