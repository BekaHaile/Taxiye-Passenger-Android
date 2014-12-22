package product.clicklabs.jugnoo.datastructure;

public class ItemInfo {
	
	public int id, countSelected;
	public String name;
	public String price;
	public String description;
	public String image;
	
	public ItemInfo(int id, String name, String price, String description, String image){
		this.id = id;
		this.name = name;
		this.price = price;
		this.description = description;
		this.image = image;
		this.countSelected = 0;
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if(((ItemInfo)o).id == this.id){
				return true;
			}
			else{
				return false;
			}
		} catch(Exception e){
			return false;
		}
	}
	
}
