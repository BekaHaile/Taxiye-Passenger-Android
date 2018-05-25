package product.clicklabs.jugnoo.wallet.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.datastructure.PaymentOption;
import product.clicklabs.jugnoo.stripe.model.StripeCardData;

/**
 * Created by shankar on 7/8/16.
 */
public class PaymentModeConfigData {

	private String name;
	private int enabled;
	private int paymentOption;
	private int priority;
	private String offerText, displayName, upiHandle;
	private String jugnooVpaHandle;
	private String upiCashbackValue;
	private ArrayList<StripeCardData> cardsData;
	private static JsonParser jsonParser = new JsonParser();
	private static Gson gson = new Gson();



	public PaymentModeConfigData(String name, int enabled, String offerText, String displayName, String upiHandle,
								 String jugnooVpaHanlde, String upiCashbackValue, JSONArray cardsData){
		this.name = name;
		this.enabled = enabled;
		this.offerText = offerText;
		this.displayName = displayName;
		this.upiHandle = upiHandle;
		this.jugnooVpaHandle = jugnooVpaHanlde;
		if(Constants.KEY_PAYTM.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.PAYTM.getOrdinal();
		}
		else if(Constants.KEY_MOBIKWIK.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.MOBIKWIK.getOrdinal();
		}
		else if(Constants.KEY_FREECHARGE.equalsIgnoreCase(name)) {
			paymentOption = PaymentOption.FREECHARGE.getOrdinal();
		}
		else if(Constants.KEY_JUGNOO_CASH.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.CASH.getOrdinal();
		}
		else if(Constants.KEY_JUGNOO_PAY.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.JUGNOO_PAY.getOrdinal();
		}
		else if(Constants.KEY_RAZORPAY.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.RAZOR_PAY.getOrdinal();
		}
		else if(Constants.KEY_UPI_RAZORPAY.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.UPI_RAZOR_PAY.getOrdinal();
		}else if(Constants.KEY_ICICI_UPI.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.ICICI_UPI.getOrdinal();
		} else if(Constants.KEY_MPESA.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.MPESA.getOrdinal();
		} else if(Constants.KEY_STRIPE_CARDS.equalsIgnoreCase(name)){
			paymentOption = PaymentOption.STRIPE_CARDS.getOrdinal();
			if(cardsData!=null){
				 this.cardsData = new ArrayList<>();


					for (int i=0;i<cardsData.length();i++){

						try {
							JsonElement mJson = jsonParser.parse(cardsData.getJSONObject(i).toString());
							StripeCardData stripeCardData = gson.fromJson(mJson, StripeCardData.class);
							this.cardsData.add(stripeCardData);
						} catch (JSONException e) {
							e.printStackTrace();

						}

					}


			}

		}
		this.priority = 0;
		this.upiCashbackValue = upiCashbackValue;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PaymentModeConfigData && paymentOption == ((PaymentModeConfigData)obj).paymentOption;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public int getPaymentOption() {
		return paymentOption;
	}

	public void setPaymentOption(int paymentOption) {
		this.paymentOption = paymentOption;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void incrementPriority(){
		this.priority = this.priority + 1;
	}

	public String getOfferText() {
		return offerText;
	}

	public void setOfferText(String offerText) {
		this.offerText = offerText;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUpiHandle() {
		return upiHandle;
	}

	public void setUpiHandle(String upiHandle) {
		this.upiHandle = upiHandle;
	}

	public String getJugnooVpaHandle() {
		return jugnooVpaHandle;
	}

	public String getUpiCashbackValue() {
		return upiCashbackValue;
	}

	public void setUpiCashbackValue(String upiCashbackValue) {
		this.upiCashbackValue = upiCashbackValue;
	}

	public ArrayList<StripeCardData> getCardsData() {
		return cardsData;
	}

	public void setCardsData(ArrayList<StripeCardData> cardsData) {
		this.cardsData = cardsData;
	}
}
