package product.clicklabs.jugnoo.emergency.models;

/**
 * Created by shankar on 2/23/16.
 */
public class ContactBean {

	private String name;
	private String phoneNo;
	private String type;
	private String image;
	private boolean selected;


	public ContactBean(String name, String phoneNo, String type, String image) {
		this.name = name;
		this.phoneNo = phoneNo;
		this.type = type;
		this.image = image;
		this.selected = false;
	}

	@Override
	public String toString() {
		return getName()+" "+getPhoneNo()+" "+getType()+" "+getImage();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
