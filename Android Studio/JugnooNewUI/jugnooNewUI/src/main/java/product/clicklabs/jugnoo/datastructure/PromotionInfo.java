package product.clicklabs.jugnoo.datastructure;

public class PromotionInfo extends PromoCoupon{
	
	private String title;
	public String terms;
    public String expiryDate;
	
	public PromotionInfo(int id, String title, String terms, String endOn){
		this.id = id;
		this.title = title;
		this.terms = terms;
        this.expiryDate = endOn;
	}

    public PromotionInfo(int id, String title, String terms){
        this.id = id;
        this.title = title;
        this.terms = terms;
        this.expiryDate = "";
    }

	@Override
	public String getTitle() {
		return title;
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
