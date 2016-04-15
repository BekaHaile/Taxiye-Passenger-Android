package product.clicklabs.jugnoo.fresh.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 4/6/16.
 */
public class SubItem {

	@SerializedName("sub_item_id")
	@Expose
	private Integer subItemId;
	@SerializedName("sub_item_name")
	@Expose
	private String subItemName;
	@SerializedName("sub_item_image")
	@Expose
	private String subItemImage;
	@SerializedName("price")
	@Expose
	private Double price;
	@SerializedName("base_unit")
	@Expose
	private String baseUnit;
	@SerializedName("stock")
	@Expose
	private Integer stock;
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

	public String getBaseUnit() {
		if(baseUnit == null){
			return "-";
		} else {
			return baseUnit;
		}
	}

	public void setBaseUnit(String baseUnit) {
		this.baseUnit = baseUnit;
	}

	public Integer getStock() {
		if(stock == null){
			return 50;
		} else {
			return stock;
		}
	}

	public void setStock(Integer stock) {
		this.stock = stock;
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
