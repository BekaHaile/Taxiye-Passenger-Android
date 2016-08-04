package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import product.clicklabs.jugnoo.home.models.PokestopInfo;

/**
 * Created by shankar on 1/5/16.
 */
public class FindPokestopResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
    @SerializedName("pokestops")
    @Expose
    private List<PokestopInfo> pokestops;

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    public List<PokestopInfo> getPokestops() {
        return pokestops;
    }

    public void setPokestops(List<PokestopInfo> pokestops) {
        this.pokestops = pokestops;
    }

}
