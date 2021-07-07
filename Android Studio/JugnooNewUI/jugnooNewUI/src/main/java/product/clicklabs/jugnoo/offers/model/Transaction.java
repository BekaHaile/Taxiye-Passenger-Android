package product.clicklabs.jugnoo.offers.model;

import com.google.gson.annotations.SerializedName;

public class Transaction{
    @SerializedName("type")
    private String type;

    @SerializedName("points")
    private int points;

    @SerializedName("time")
    private String time;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
