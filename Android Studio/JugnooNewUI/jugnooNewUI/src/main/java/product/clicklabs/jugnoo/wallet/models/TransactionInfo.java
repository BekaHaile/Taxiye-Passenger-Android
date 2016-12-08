package product.clicklabs.jugnoo.wallet.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionInfo {

	@SerializedName("transactionId")
	@Expose
	public int transactionId;
	@SerializedName("transactionType")
	@Expose
	public int transactionType;
	@SerializedName("time")
	@Expose
	public String time;
	@SerializedName("date")
	@Expose
	public String date;
	@SerializedName("transactionText")
	@Expose
	public String transactionText;
	@SerializedName("amount")
	@Expose
	public double amount;
	@SerializedName("paytm")
	@Expose
	public int paytm;
	@SerializedName("mobikwik")
	@Expose
	private int mobikwik;
	@SerializedName("freecharge")
	@Expose
	private int freecharge;
	@SerializedName("pay")
	@Expose
	private int pay;
	@SerializedName("status")
	@Expose
	private int status;
	@SerializedName("name")
	@Expose
	private String name;
	
	public TransactionInfo(int transactionId, int transactionType, String time, String date, String transactionText,
						   double amount, int paytm, int mobikwik, int freecharge, int pay, int status, String name){
		this.transactionId = transactionId;
		this.transactionType = transactionType;
		this.time = time;
		this.date = date;
		this.transactionText = transactionText;
		this.amount = amount;
		this.paytm = paytm;
		this.mobikwik = mobikwik;
		this.freecharge = freecharge;
		this.pay = pay;
		this.status = status;
		this.name = name;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
