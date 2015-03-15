package product.clicklabs.jugnoo.datastructure;

public class CancelOption{
	
	public int cancelId;
	public String name;
	public boolean checked;
	
	public CancelOption(int cancelId, String name){
		this.cancelId = cancelId;
		this.name = name;
		this.checked = false;
	}
	
}
