package product.clicklabs.jugnoo.t20.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shankar on 3/4/16.
 */
public class MatchScheduleResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("schedule")
	@Expose
	private List<Schedule> schedule = new ArrayList<Schedule>();
	@SerializedName("teams")
	@Expose
	private List<Team> teams = new ArrayList<Team>();
	@SerializedName("selections")
	@Expose
	private List<Selection> selections = new ArrayList<Selection>();
	@SerializedName("info")
	@Expose
	private String info;

	/**
	 *
	 * @return
	 * The flag
	 */
	public Integer getFlag() {
		return flag;
	}

	/**
	 *
	 * @param flag
	 * The flag
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	/**
	 *
	 * @return
	 * The schedule
	 */
	public List<Schedule> getSchedule() {
		return schedule;
	}

	/**
	 *
	 * @param schedule
	 * The schedule
	 */
	public void setSchedule(List<Schedule> schedule) {
		this.schedule = schedule;
	}

	/**
	 *
	 * @return
	 * The teams
	 */
	public List<Team> getTeams() {
		return teams;
	}

	/**
	 *
	 * @param teams
	 * The teams
	 */
	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}

	/**
	 *
	 * @return
	 * The selections
	 */
	public List<Selection> getSelections() {
		return selections;
	}

	/**
	 *
	 * @param selections
	 * The selections
	 */
	public void setSelections(List<Selection> selections) {
		this.selections = selections;
	}

	/**
	 *
	 * @return
	 * The info
	 */
	public String getInfo() {
		return info;
	}

	/**
	 *
	 * @param info
	 * The info
	 */
	public void setInfo(String info) {
		this.info = info;
	}

}
