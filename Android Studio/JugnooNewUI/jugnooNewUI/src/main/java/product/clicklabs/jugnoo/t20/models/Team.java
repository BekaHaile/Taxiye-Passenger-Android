package product.clicklabs.jugnoo.t20.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 3/4/16.
 */
public class Team {

	@SerializedName("team_id")
	@Expose
	private Integer teamId;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("short_name")
	@Expose
	private String shortName;
	@SerializedName("flag_image_url")
	@Expose
	private String flagImageUrl;

	public Team(Integer teamId, String name, String shortName, String flagImageUrl){
		this.teamId = teamId;
		this.name = name;
		this.shortName = shortName;
		this.flagImageUrl = flagImageUrl;
	}

	@Override
	public boolean equals(Object o) {
		try{
			return ((Team)o).getTeamId().equals(getTeamId());
		} catch(Exception e){
			return false;
		}
	}

	/**
	 *
	 * @return
	 * The teamId
	 */
	public Integer getTeamId() {
		return teamId;
	}

	/**
	 *
	 * @param teamId
	 * The team_id
	 */
	public void setTeamId(Integer teamId) {
		this.teamId = teamId;
	}

	/**
	 *
	 * @return
	 * The name
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @param name
	 * The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * @return
	 * The shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 *
	 * @param shortName
	 * The short_name
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 *
	 * @return
	 * The flagImageUrl
	 */
	public String getFlagImageUrl() {
		return flagImageUrl;
	}

	/**
	 *
	 * @param flagImageUrl
	 * The flag_image_url
	 */
	public void setFlagImageUrl(String flagImageUrl) {
		this.flagImageUrl = flagImageUrl;
	}

}