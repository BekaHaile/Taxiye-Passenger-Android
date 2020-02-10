package product.clicklabs.jugnoo.datastructure;

public class FeedbackReason {

    public String name;
    public boolean checked;
    public int badgeId;
    public boolean canComment=false;


    public FeedbackReason(String name) {
        this.name = name;
        this.checked = false;
    }
    public FeedbackReason(String name,int badgeId, boolean canComment){
        this.name=name;
        this.badgeId=badgeId;
        this.checked = false;
        this.canComment=canComment;
    }

}
