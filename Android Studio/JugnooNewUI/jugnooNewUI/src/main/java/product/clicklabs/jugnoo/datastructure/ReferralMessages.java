package product.clicklabs.jugnoo.datastructure;

public class ReferralMessages{
	
	public String referralMessage;
	public String referralSharingMessage;
	public String fbShareCaption;
	public String fbShareDescription;
    public String referralCaption;
    public int referralCaptionEnabled;
    public String referralEmailSubject, referralPopupText;
    public String referralShortMessage;
    public String referralMoreInfoMessage;
	private String title;


	public ReferralMessages(String referralMessage, String referralSharingMessage, String fbShareCaption, String fbShareDescription, String referralCaption, int referralCaptionEnabled,
                            String referralEmailSubject, String referralPopupText, String referralShortMessage, String referralMoreInfoMessage,
							String title){
		this.referralMessage = referralMessage;
		this.referralSharingMessage = referralSharingMessage;
		this.fbShareCaption = fbShareCaption;
		this.fbShareDescription = fbShareDescription;
        this.referralCaption = referralCaption;
        this.referralCaptionEnabled = referralCaptionEnabled;
        this.referralEmailSubject = referralEmailSubject;
		this.referralPopupText = referralPopupText;
		this.referralShortMessage = referralShortMessage;
		this.referralMoreInfoMessage = referralMoreInfoMessage;
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
