package product.clicklabs.jugnoo.datastructure;

public class FeedbackReason {

    public String name;
    public boolean checked;
    public int badgeId;


    public FeedbackReason(String name) {
        this.name = name;
        this.checked = false;
    }
    public FeedbackReason(String name,int badgeId){
        this.name=name;
        this.badgeId=badgeId;
        this.checked = false;
    }

}
