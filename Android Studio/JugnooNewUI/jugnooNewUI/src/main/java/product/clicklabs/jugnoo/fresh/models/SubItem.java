package product.clicklabs.jugnoo.fresh.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 4/6/16.
 */
public class SubItem {

	@SerializedName("subItemId")
	@Expose
	private Integer subItemId;
	@SerializedName("subItemName")
	@Expose
	private String subItemName;
	@SerializedName("subItemImage")
	@Expose
	private String subItemImage;
	@SerializedName("price")
	@Expose
	private Double price;
	@SerializedName("subItemUnit")
	@Expose
	private String subItemUnit;
	@SerializedName("subItemTotalQuantity")
	@Expose
	private Integer subItemTotalQuantity;
	private Integer subItemQuantitySelected;

	/**
	 *
	 * @return
	 * The subItemId
	 */
	public Integer getSubItemId() {
		return subItemId;
	}

	/**
	 *
	 * @param subItemId
	 * The subItemId
	 */
	public void setSubItemId(Integer subItemId) {
		this.subItemId = subItemId;
	}

	/**
	 *
	 * @return
	 * The subItemName
	 */
	public String getSubItemName() {
		return subItemName;
	}

	/**
	 *
	 * @param subItemName
	 * The subItemName
	 */
	public void setSubItemName(String subItemName) {
		this.subItemName = subItemName;
	}

	/**
	 *
	 * @return
	 * The subItemImage
	 */
	public String getSubItemImage() {
		return subItemImage;
	}

	/**
	 *
	 * @param subItemImage
	 * The subItemImage
	 */
	public void setSubItemImage(String subItemImage) {
		this.subItemImage = subItemImage;
	}

	/**
	 *
	 * @return
	 * The price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 *
	 * @param price
	 * The price
	 */
	public void setPrice(Double price) {
		this.price = price;
	}

	public String getSubItemUnit() {
		if(subItemUnit == null){
			return "-";
		} else {
			return subItemUnit;
		}
	}

	public void setSubItemUnit(String subItemUnit) {
		this.subItemUnit = subItemUnit;
	}

	public Integer getSubItemTotalQuantity() {
		if(subItemTotalQuantity == null){
			return 20;
		} else {
			return subItemTotalQuantity;
		}
	}

	public void setSubItemTotalQuantity(Integer subItemTotalQuantity) {
		this.subItemTotalQuantity = subItemTotalQuantity;
	}

	public Integer getSubItemQuantitySelected() {
		if(subItemQuantitySelected == null){
			return 0;
		} else {
			return subItemQuantitySelected;
		}
	}

	public void setSubItemQuantitySelected(Integer subItemQuantitySelected) {
		this.subItemQuantitySelected = subItemQuantitySelected;
	}
}
