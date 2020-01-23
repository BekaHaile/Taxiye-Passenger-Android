package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetFavouriteDriver {

    @SerializedName("flag")
     @Expose
    private int flag;

    @SerializedName("message")
    @Expose
    private String message;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @SerializedName("error")
    @Expose
    private String error;
    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<GetFetchUserDriverResponse> getData() {
        return data;
    }

    public void setData(ArrayList<GetFetchUserDriverResponse> data) {
        this.data = data;
    }

    @SerializedName("data")
    @Expose
    private ArrayList<GetFetchUserDriverResponse> data;

}
