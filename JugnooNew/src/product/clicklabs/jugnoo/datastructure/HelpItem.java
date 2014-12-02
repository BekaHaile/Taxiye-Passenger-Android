package product.clicklabs.jugnoo.datastructure;

public class HelpItem {
	public HelpSection id;
	public String name;
	public HelpItem(HelpSection id, String name){
		this.id = id;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name + " " + id + " " + id.getOrdinal();
	}
}
