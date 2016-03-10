package product.clicklabs.jugnoo.datastructure;

/**
 * Created by shankar on 3/10/16.
 */
public class PaytmRechargeInfo {

	private String transferId, transferPhone, transferAmount, transferSenderName;

	public PaytmRechargeInfo(String transferId, String transferPhone, String transferAmount, String transferSenderName){
		this.transferId = transferId;
		this.transferPhone = transferPhone;
		this.transferAmount = transferAmount;
		this.transferSenderName = transferSenderName;
	}

	public String getTransferId() {
		return transferId;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}

	public String getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(String transferAmount) {
		this.transferAmount = transferAmount;
	}

	public String getTransferPhone() {
		return transferPhone;
	}

	public void setTransferPhone(String transferPhone) {
		this.transferPhone = transferPhone;
	}

	public String getTransferSenderName() {
		return transferSenderName;
	}

	public void setTransferSenderName(String transferSenderName) {
		this.transferSenderName = transferSenderName;
	}
}
