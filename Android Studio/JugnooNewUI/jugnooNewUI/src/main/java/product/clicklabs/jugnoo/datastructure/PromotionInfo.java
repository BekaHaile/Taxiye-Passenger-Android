package product.clicklabs.jugnoo.datastructure;

public class PromotionInfo extends PromoCoupon{
	
	public String title;
	public String terms;
    public String expiryDate;
	
	public PromotionInfo(int id, String title, String terms){
		this.id = id;
		this.title = title;
		this.terms = terms;
        this.expiryDate = "2015-08-01 00:00:00";
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if((((PromotionInfo)o).id == this.id)){
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
