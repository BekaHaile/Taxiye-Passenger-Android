package product.clicklabs.jugnoo.datastructure;

public class ReferralMessages{
	
	public String referralMessage;
	public String referralSharingMessage;
	public String fbShareCaption;
	public String fbShareDescription;
    public String referralCaption;
    public int referralCaptionEnabled;


	public ReferralMessages(String referralMessage, String referralSharingMessage, String fbShareCaption, String fbShareDescription, String referralCaption, int referralCaptionEnabled){
		this.referralMessage = referralMessage;
		this.referralSharingMessage = referralSharingMessage;
		this.fbShareCaption = fbShareCaption;
		this.fbShareDescription = fbShareDescription;
        this.referralCaption = referralCaption;
        this.referralCaptionEnabled = referralCaptionEnabled;
	}
	
}
