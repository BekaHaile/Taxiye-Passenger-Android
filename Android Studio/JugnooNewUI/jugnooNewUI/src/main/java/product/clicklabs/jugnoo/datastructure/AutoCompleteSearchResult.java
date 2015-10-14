package product.clicklabs.jugnoo.datastructure;


public class AutoCompleteSearchResult{
	
	public String name, address, placeId;
	
	public AutoCompleteSearchResult(String name, String address, String placeId){
		this.name = name;
		this.address = address;
		this.placeId = placeId;
	}
	
	@Override
	public String toString() {
		return name + " "+ address +" " + placeId;
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if(((AutoCompleteSearchResult)o).name.equalsIgnoreCase(this.name)){
				return true;
			}
			else{
				return false;
			}
		} catch(Exception e){
			return false;
		}
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
}