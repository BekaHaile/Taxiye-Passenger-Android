package product.clicklabs.jugnoo.emergency.models;

import android.net.Uri;

/**
 * Created by shankar on 2/23/16.
 */
public class ContactBean {

	private int id;
	private String name;
	private String phoneNo;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	private String countryCode;
	private String type;
	private boolean selected;
	private ContactBeanViewType contactBeanViewType;
	private Uri imageUri;
	private String imageUrl;


	public ContactBean(String name, String phoneNo, String countryCode, String type, ContactBeanViewType contactBeanViewType, Uri imageUri, String imageUrl) {
		this.name = name;
		this.phoneNo = phoneNo;
		this.countryCode = countryCode;
		this.type = type;
		this.contactBeanViewType = contactBeanViewType;
		this.imageUri = imageUri;
		this.imageUrl = imageUrl;
		this.selected = false;
	}

	@Override
	public String toString() {
		return getName();
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

	public ContactBeanViewType getContactBeanViewType() {
		return contactBeanViewType;
	}

	public void setContactBeanViewType(ContactBeanViewType contactBeanViewType) {
		this.contactBeanViewType = contactBeanViewType;
	}

	public Uri getImageUri() {
		return imageUri;
	}

	public void setImageUri(Uri imageUri) {
		this.imageUri = imageUri;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	public enum ContactBeanViewType{
		CONTACT(0),
		EMERGENCY_CONTACTS(1),
		PHONE_CONTACTS(2)
		;


		private int ordinal;

		ContactBeanViewType(int ordinal){
			this.ordinal = ordinal;
		}

		public int getOrdinal() {
			return ordinal;
		}

		public void setOrdinal(int ordinal) {
			this.ordinal = ordinal;
		}
	}

}
