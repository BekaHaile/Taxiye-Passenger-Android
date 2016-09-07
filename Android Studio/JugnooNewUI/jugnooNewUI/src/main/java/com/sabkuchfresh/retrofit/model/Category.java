package com.sabkuchfresh.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 4/7/16.
 */
public class Category {

	@SerializedName("category_id")
	@Expose
	private Integer categoryId;
	@SerializedName("category_name")
	@Expose
	private String categoryName;
	@SerializedName("category_image")
	@Expose
	private String categoryImage;
	@SerializedName("show_category_banner")
	@Expose
	private Integer showCategoryBanner = 0;
	@SerializedName("category_banner")
	@Expose
	private CategoryBanner categoryBanner = null;
    @SerializedName("current_group_id")
    @Expose
    private Integer currentGroupId;
	@SerializedName("sub_items")
	@Expose
	private List<SubItem> subItems = new ArrayList<SubItem>();

    public Integer getCurrentGroupId() {
        return currentGroupId;
    }

    public void setCurrentGroupId(Integer currentGroupId) {
        this.currentGroupId = currentGroupId;
    }

	/**
	 *
	 * @return
	 * The categoryId
	 */
	public Integer getCategoryId() {
		return categoryId;
	}

	/**
	 *
	 * @param categoryId
	 * The categoryId
	 */
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 *
	 * @return
	 * The categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 *
	 * @param categoryName
	 * The categoryName
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 *
	 * @return
	 * The categoryImage
	 */
	public String getCategoryImage() {
		return categoryImage;
	}

	/**
	 *
	 * @param categoryImage
	 * The categoryImage
	 */
	public void setCategoryImage(String categoryImage) {
		this.categoryImage = categoryImage;
	}

	/**
	 *
	 * @return
	 * The subItems
	 */
	public List<SubItem> getSubItems() {
		return subItems;
	}

	/**
	 *
	 * @param subItems
	 * The subItems
	 */
	public void setSubItems(List<SubItem> subItems) {
		this.subItems = subItems;
	}

	public Integer getShowCategoryBanner() {
		return showCategoryBanner;
	}

	public void setShowCategoryBanner(Integer showCategoryBanner) {
		this.showCategoryBanner = showCategoryBanner;
	}

	public CategoryBanner getCategoryBanner() {
		return categoryBanner;
	}

	public void setCategoryBanner(CategoryBanner categoryBanner) {
		this.categoryBanner = categoryBanner;
	}

	public class CategoryBanner {

		@SerializedName("small_image")
		@Expose
		private String smallImage;
		@SerializedName("large_image")
		@Expose
		private String largeImage;
		@SerializedName("description")
		@Expose
		private String description;

		/**
		 *
		 * @return
		 * The smallImage
		 */
		public String getSmallImage() {
			return smallImage;
		}

		/**
		 *
		 * @param smallImage
		 * The small_image
		 */
		public void setSmallImage(String smallImage) {
			this.smallImage = smallImage;
		}

		/**
		 *
		 * @return
		 * The largeImage
		 */
		public String getLargeImage() {
			return largeImage;
		}

		/**
		 *
		 * @param largeImage
		 * The large_image
		 */
		public void setLargeImage(String largeImage) {
			this.largeImage = largeImage;
		}

		/**
		 *
		 * @return
		 * The description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 *
		 * @param description
		 * The description
		 */
		public void setDescription(String description) {
			this.description = description;
		}

	}
}