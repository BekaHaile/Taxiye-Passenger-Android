package product.clicklabs.jugnoo.datastructure;

/**
 * Created by ankit on 14/03/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignupTutorial {

    @SerializedName("ts1")
    @Expose
    private Integer ts1;
    @SerializedName("ts2")
    @Expose
    private Integer ts2;
    @SerializedName("ds1")
    @Expose
    private Integer ds1;
    @SerializedName("ds2")
    @Expose
    private Integer ds2;
    @SerializedName("ds3")
    @Expose
    private Integer ds3;

    public Integer getTs1() {
        return ts1;
    }

    public void setTs1(Integer ts1) {
        this.ts1 = ts1;
    }

    public Integer getTs2() {
        return ts2;
    }

    public void setTs2(Integer ts2) {
        this.ts2 = ts2;
    }

    public Integer getDs1() {
        return ds1;
    }

    public void setDs1(Integer ds1) {
        this.ds1 = ds1;
    }

    public Integer getDs2() {
        return ds2;
    }

    public void setDs2(Integer ds2) {
        this.ds2 = ds2;
    }

    public Integer getDs3() {
        return ds3;
    }

    public void setDs3(Integer ds3) {
        this.ds3 = ds3;
    }

}
