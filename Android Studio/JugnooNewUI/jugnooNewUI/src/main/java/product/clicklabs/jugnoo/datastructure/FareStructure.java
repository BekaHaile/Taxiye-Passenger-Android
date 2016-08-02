package product.clicklabs.jugnoo.datastructure;

import android.content.Context;

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


    public FareStructure(double fixedFare, double thresholdDistance, double farePerKm, double farePerMin, double freeMinutes, double farePerWaitingMin, double fareThresholdWaitingTime,
						 double convenienceCharge, boolean fromServer, String displayBaseFare){
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
    }


    public boolean getIsFromServer() {
        return fromServer;
    }

    public void setFromServer(boolean fromServer) {
        this.fromServer = fromServer;
    }

    public String getDisplayBaseFare(Context context) {
        if(displayBaseFare == null){
            return context.getResources().getString(R.string.rupees_value_format_without_space,
                    Utils.getMoneyDecimalFormatWithoutFloat().format(fixedFare));
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
}
