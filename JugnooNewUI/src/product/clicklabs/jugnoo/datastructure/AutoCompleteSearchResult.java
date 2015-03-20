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
	
	
}