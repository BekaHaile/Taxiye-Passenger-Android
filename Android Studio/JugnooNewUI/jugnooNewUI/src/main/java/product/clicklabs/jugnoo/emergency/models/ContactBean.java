package product.clicklabs.jugnoo.emergency.models;

/**
 * Created by shankar on 2/23/16.
 */
public class ContactBean {

	private int id;
	private String name;
	private String phoneNo;
	private String type;
	private boolean selected;


	public ContactBean(String name, String phoneNo, String type) {
		this.name = name;
		this.phoneNo = phoneNo;
		this.type = type;
		this.selected = false;
	}

	@Override
	public String toString() {
		return getName()+" "+getPhoneNo()+" "+getType();
	}

	@Override
	public boolean equals(Object o) {
		try{
			return (o instanceof ContactBean && ((ContactBean)o).getPhoneNo().equalsIgnoreCase(this.getPhoneNo()));
		} catch(Exception e){
			e.printStackTrace();
		}
		return false;
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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
