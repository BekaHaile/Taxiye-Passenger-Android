package product.clicklabs.jugnoo.datastructure;

public class ReferralMessages{
	
	public String referralSharingMessage;
	public String fbShareCaption;
	public String fbShareDescription;
    public String referralEmailSubject;
    public String referralShortMessage;
    public String referralMoreInfoMessage;
	private String title;


	public ReferralMessages(String referralSharingMessage, String fbShareCaption, String fbShareDescription,
                            String referralEmailSubject, String referralShortMessage, String referralMoreInfoMessage,
							String title){
		this.referralSharingMessage = referralSharingMessage;
		this.fbShareCaption = fbShareCaption;
		this.fbShareDescription = fbShareDescription;
        this.referralEmailSubject = referralEmailSubject;
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
