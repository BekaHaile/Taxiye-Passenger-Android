package product.clicklabs.jugnoo.fresh.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 4/7/16.
 */
public class Category {

	@SerializedName("categoryId")
	@Expose
	private Integer categoryId;
	@SerializedName("categoryName")
	@Expose
	private String categoryName;
	@SerializedName("categoryImage")
	@Expose
	private String categoryImage;
	@SerializedName("subItems")
	@Expose
	private List<SubItem> subItems = new ArrayList<SubItem>();

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

}