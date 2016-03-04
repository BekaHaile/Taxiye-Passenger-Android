package product.clicklabs.jugnoo.t20.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shankar on 3/4/16.
 */
public class Schedule {

	@SerializedName("schedule_id")
	@Expose
	private Integer scheduleId;
	@SerializedName("team_1_id")
	@Expose
	private Integer team1Id;
	@SerializedName("team_2_id")
	@Expose
	private Integer team2Id;
	@SerializedName("match_time")
	@Expose
	private String matchTime;

	private Team team1 = null, team2 = null;
	private Integer selectedTeamId = null;

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
	 * The team1Id
	 */
	public Integer getTeam1Id() {
		return team1Id;
	}

	/**
	 *
	 * @param team1Id
	 * The team_1_id
	 */
	public void setTeam1Id(Integer team1Id) {
		this.team1Id = team1Id;
	}

	/**
	 *
	 * @return
	 * The team2Id
	 */
	public Integer getTeam2Id() {
		return team2Id;
	}

	/**
	 *
	 * @param team2Id
	 * The team_2_id
	 */
	public void setTeam2Id(Integer team2Id) {
		this.team2Id = team2Id;
	}

	/**
	 *
	 * @return
	 * The matchTime
	 */
	public String getMatchTime() {
		return matchTime;
	}

	/**
	 *
	 * @param matchTime
	 * The match_time
	 */
	public void setMatchTime(String matchTime) {
		this.matchTime = matchTime;
	}


	public Team getTeam1() {
		return team1;
	}

	public void setTeam1(Team team1) {
		this.team1 = team1;
	}

	public Team getTeam2() {
		return team2;
	}

	public void setTeam2(Team team2) {
		this.team2 = team2;
	}


	private Calendar getCalendar(){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			setMatchTime(getMatchTime().replace("T", " "));
			setMatchTime(getMatchTime().split("\\.")[0]);
			Date myDate = sdf.parse(getMatchTime());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(myDate);
			return calendar;
		} catch(Exception e){
			e.printStackTrace();
		}
		return Calendar.getInstance();
	}

	public String getDate(){
		return getCalendar().getDisplayName(Calendar.DATE, Calendar.SHORT, Locale.getDefault());
	}

	public String getMonth(){
		return getCalendar().getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
	}

	public String getTime(){
		return getCalendar().getDisplayName(Calendar.HOUR_OF_DAY, Calendar.SHORT, Locale.getDefault())
				+" "+
				getCalendar().getDisplayName(Calendar.MINUTE, Calendar.SHORT, Locale.getDefault());
	}

	public Integer getSelectedTeamId() {
		return selectedTeamId;
	}

	public void setSelectedTeamId(Integer selectedTeamId) {
		this.selectedTeamId = selectedTeamId;
	}
}