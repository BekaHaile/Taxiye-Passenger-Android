package product.clicklabs.jugnoo.t20.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 3/4/16.
 */
public class Selection {

	@SerializedName("schedule_id")
	@Expose
	private Integer scheduleId;
	@SerializedName("team_id")
	@Expose
	private Integer teamId;

	public Selection(Integer scheduleId, Integer teamId){
		this.scheduleId = scheduleId;
		this.teamId = teamId;
	}

	@Override
	public boolean equals(Object o) {
		try{
			return ((Selection)o).getScheduleId().equals(getScheduleId());
		} catch (Exception e){
			return false;
		}
	}

	/**
	 *
	 * @return
	 * The scheduleId
	 */
	public Integer getScheduleId() {
		return scheduleId;
	}

	/**
	 *
	 * @param scheduleId
	 * The schedule_id
	 */
	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
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

}
