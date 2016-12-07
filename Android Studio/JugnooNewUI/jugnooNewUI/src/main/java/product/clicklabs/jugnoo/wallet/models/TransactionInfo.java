package product.clicklabs.jugnoo.wallet.models;

public class TransactionInfo {
	
	public int transactionId, transactionType;
	public String time, date;
	public String transactionText;
	public double amount;
	public int paytm;
	private int mobikwik;
	private int freecharge;
	private int pay, payTxnType;
	private String payType;
	
	public TransactionInfo(int transactionId, int transactionType, String time, String date, String transactionText,
						   double amount,String payType, int paytm, int mobikwik, int freecharge, int pay, int payTxnType){
		this.transactionId = transactionId;
		this.transactionType = transactionType;
		this.time = time;
		this.date = date;
		this.transactionText = transactionText;
		this.amount = amount;
		this.payType = payType;
		this.paytm = paytm;
		this.mobikwik = mobikwik;
		this.freecharge = freecharge;
		this.pay = pay;
		this.payTxnType = payTxnType;
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if(((TransactionInfo)o).transactionId == this.transactionId){
				return true;
			}
			else{
				return false;
			}
		} catch(Exception e){
			return false;
		}
	}

	public int getMobikwik() {
		return mobikwik;
	}

	public int getFreecharge() {
		return freecharge;
	}

	public int getPay() {
		return pay;
	}

	public String getPayType() {
		return payType;
	}

	public int getPayTxnType() {
		return payTxnType;
	}

	public void setPayTxnType(int payTxnType) {
		this.payTxnType = payTxnType;
	}
}
