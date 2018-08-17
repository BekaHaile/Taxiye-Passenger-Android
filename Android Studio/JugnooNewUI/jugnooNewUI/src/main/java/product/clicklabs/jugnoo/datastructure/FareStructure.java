package product.clicklabs.jugnoo.datastructure;

import android.content.Context;
import android.text.TextUtils;

import java.text.DecimalFormat;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.Utils;

public class FareStructure {
	private double fixedFare;
	public double thresholdDistance;
	public double farePerKm;
	public double farePerMin;
	public double freeMinutes;
    public double farePerWaitingMin;
    public double fareThresholdWaitingTime;

	public double convenienceCharge;

    private boolean fromServer;
    private String displayBaseFare;
    private String displayFareText;
    private int operatorId;
    private String currency;
    private String distanceUnit;


    public FareStructure(double fixedFare, double thresholdDistance, double farePerKm, double farePerMin, double freeMinutes, double farePerWaitingMin, double fareThresholdWaitingTime,
						 double convenienceCharge, boolean fromServer, String displayBaseFare, String displayFareText, int operatorId, String currency, String distanceUnit){
        this.fixedFare = fixedFare;
        this.thresholdDistance = thresholdDistance;
        this.farePerKm = farePerKm;
        this.farePerMin = farePerMin;
        this.freeMinutes = freeMinutes;
        this.farePerWaitingMin = farePerWaitingMin;
        this.fareThresholdWaitingTime = fareThresholdWaitingTime;
		this.convenienceCharge = convenienceCharge;

        this.fromServer = fromServer;
        this.displayBaseFare = displayBaseFare;
        this.displayFareText = displayFareText;
        this.operatorId = operatorId;
        this.currency = currency;
        this.distanceUnit = distanceUnit;
    }


    public boolean getIsFromServer() {
        return fromServer;
    }

    public void setFromServer(boolean fromServer) {
        this.fromServer = fromServer;
    }

    public String getDisplayBaseFare(Context context) {
        if(displayBaseFare == null){
            return Utils.formatCurrencyValue(currency, fixedFare, false);
        } else{
            return displayBaseFare;
        }
    }

    public void setDisplayBaseFare(String displayBaseFare) {
        this.displayBaseFare = displayBaseFare;
    }

    public double getFixedFare() {
        return fixedFare;
    }

    public void setFixedFare(double fixedFare) {
        this.fixedFare = fixedFare;
    }

    public String getDisplayFareText(Context context) {
        String convenienceThresholdText = "";
        try {
            if(convenienceCharge > 0){
                convenienceThresholdText = context.getResources().getString(R.string.convenience_charge_colon) + " " +
                        Utils.formatCurrencyValue(currency, convenienceCharge);
            }

            if(thresholdDistance > 1.0){
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                if("".equalsIgnoreCase(convenienceThresholdText)){
                    convenienceThresholdText = String.format(context.getResources()
                                    .getString(R.string.fare_threshold_distance_message_format),
                            decimalFormat.format(thresholdDistance)+" "+Utils.getDistanceUnit(getDistanceUnit()));
                } else{
                    convenienceThresholdText = convenienceThresholdText + "\n" + String.format(context.getResources()
                                    .getString(R.string.fare_threshold_distance_message_format),
                            decimalFormat.format(thresholdDistance)+" "+Utils.getDistanceUnit(getDistanceUnit()));
                }
            }

            try {
                if(displayFareText != null && !TextUtils.isEmpty(displayFareText)) {
                    convenienceThresholdText = displayFareText;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convenienceThresholdText;
    }

    public void setDisplayFareText(String displayFareText) {
        this.displayFareText = displayFareText;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }
}
