package product.clicklabs.jugnoo.datastructure;

import java.util.ArrayList;

import product.clicklabs.jugnoo.retrofit.model.MediaInfo;

public class ReferralMessages{
	
	public String referralSharingMessage;
	public String fbShareCaption;
	public String fbShareDescription;
    public String referralEmailSubject;
    public String referralShortMessage;
    public String referralMoreInfoMessage;
	private String title;
	private ArrayList<MediaInfo> referralImages;
	private int multiLevelReferralEnabled;

	private double referralsCount;
	private double referralEarnedTotal;
	private double referralEarnedToday;



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

	public ArrayList<MediaInfo> getReferralImages() {
		return referralImages;
	}

	public boolean getMultiLevelReferralEnabled() {
		return multiLevelReferralEnabled == 1;
	}

	public void setReferralImages(ArrayList<MediaInfo> referralImages) {
		this.referralImages = referralImages;
	}

	public void setMultiLevelReferralEnabled(int multiLevelReferralEnabled) {
		this.multiLevelReferralEnabled = multiLevelReferralEnabled;
	}

	public double getReferralsCount() {
		return referralsCount;
	}

	public void setReferralsCount(double referralsCount) {
		this.referralsCount = referralsCount;
	}

	public double getReferralEarnedTotal() {
		return referralEarnedTotal;
	}

	public void setReferralEarnedTotal(double referralEarnedTotal) {
		this.referralEarnedTotal = referralEarnedTotal;
	}

	public double getReferralEarnedToday() {
		return referralEarnedToday;
	}

	public void setReferralEarnedToday(double referralEarnedToday) {
		this.referralEarnedToday = referralEarnedToday;
	}
}
