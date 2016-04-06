package product.clicklabs.jugnoo.fresh.models;

/**
 * Created by shankar on 4/6/16.
 */
public class SubItem {

	private String name;
	private String unit;
	private double price;
	private int totalQuantity;
	private int quantitySelected;
	private String image;


	public SubItem(String name, String unit, double price, int totalQuantity, String image) {
		this.name = name;
		this.unit = unit;
		this.price = price;
		this.totalQuantity = totalQuantity;
		this.quantitySelected = 0;
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public int getQuantitySelected() {
		return quantitySelected;
	}

	public void setQuantitySelected(int quantitySelected) {
		this.quantitySelected = quantitySelected;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
