package product.clicklabs.jugnoo.datastructure;

public class GAPIAddress{
	
	public String formattedAddress;

	public GAPIAddress(String formattedAddress){
		this.formattedAddress = formattedAddress;
	}

	public String getSearchableAddress(){
		return formattedAddress;
	}
	
	@Override
	public String toString() {
		return formattedAddress;
	}
}