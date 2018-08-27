package product.clicklabs.jugnoo.datastructure;

import java.util.ArrayList;

public class GAPIAddress{
	
	public ArrayList<String> addressComponents;
	public String formattedAddress;
	public String street;
	public String subLocality;
	public String locality;
	public String administrativeArea;
	public String country;
	public String postalCode;
	
	public GAPIAddress(ArrayList<String> addressComponents, String formattedAddress,
					   String street, String subLocality, String locality, String administrativeArea, String country, String postalCode){
		this.addressComponents = addressComponents;
		this.formattedAddress = formattedAddress;
		this.street = street;
		this.subLocality = subLocality;
		this.locality = locality;
		this.administrativeArea = administrativeArea;
		this.country = country;
		this.postalCode = postalCode;
	}

	public String getSearchableAddress(){
		String address = "";
		if(!"".equalsIgnoreCase(street)){
			address = address + street;
		}
		if(!"".equalsIgnoreCase(subLocality)
				&& !locality.toLowerCase().contains(subLocality.toLowerCase())
				&& !administrativeArea.toLowerCase().contains(subLocality.toLowerCase())){
			if("".equalsIgnoreCase(address)){
				address = address + subLocality;
			}
			else{
				address = address + ", " + subLocality;
			}
		}
		if(!"".equalsIgnoreCase(locality)
				&& !administrativeArea.toLowerCase().contains(locality.toLowerCase())){
			if("".equalsIgnoreCase(address)){
				address = address + locality;
			}
			else{
				address = address + ", " + locality;
			}
		}
		if(!"".equalsIgnoreCase(administrativeArea)){
			if("".equalsIgnoreCase(address)){
				address = address + administrativeArea;
			}
			else{
				address = address + ", " + administrativeArea;
			}
		}
		if(!"".equalsIgnoreCase(country)){
			if("".equalsIgnoreCase(address)){
				address = address + country;
			}
			else{
				address = address + ", " + country;
			}
		}

//		address = formattedAddress.replace(",", "");
		address = address.replace("-", " ");

		return address;
//		return address;
	}
	
	@Override
	public String toString() {
		return addressComponents.toString();
	}
}