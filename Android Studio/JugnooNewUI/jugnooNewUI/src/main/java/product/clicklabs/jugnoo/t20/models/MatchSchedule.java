package product.clicklabs.jugnoo.t20.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shankar on 3/4/16.
 */
public class MatchSchedule {

	private int scheduleId;
	private String timeStamp;
	private int team1Id, team2Id;
	private String team1Flag, team2Flag, team1Name, team2Name, team1NameShort, team2NameShort;
	private int guessTeamId;

	public MatchSchedule(int scheduleId, String timeStamp,
						 int team1Id, int team2Id,
						 String team1Flag, String team2Flag,
						 String team1Name, String team2Name,
						 String team1NameShort, String team2NameShort,
						 int guessTeamId){
		this.scheduleId = scheduleId;
		this.timeStamp = timeStamp;
		this.team1Id = team1Id;
		this.team2Id = team2Id;
		this.team1Flag = team1Flag;
		this.team2Flag = team2Flag;
		this.team1Name = team1Name;

		this.team2Name = team2Name;
		this.team1NameShort = team1NameShort;
		this.team2NameShort = team2NameShort;
		this.guessTeamId = guessTeamId;
	}

	@Override
	public boolean equals(Object o) {
		try{
			return ((MatchSchedule) o).getScheduleId() == getScheduleId();
		} catch(Exception e){
			return false;
		}
	}

	public int getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	private Calendar getCalendar(){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date myDate = sdf.parse(getTimeStamp());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(myDate);
			return calendar;
		} catch (Exception e) {
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
				+ " " +
				getCalendar().getDisplayName(Calendar.MINUTE, Calendar.SHORT, Locale.getDefault());
	}


	public int getTeam1Id() {
		return team1Id;
	}

	public void setTeam1Id(int team1Id) {
		this.team1Id = team1Id;
	}

	public int getTeam2Id() {
		return team2Id;
	}

	public void setTeam2Id(int team2Id) {
		this.team2Id = team2Id;
	}

	public String getTeam1Flag() {
		return team1Flag;
	}

	public void setTeam1Flag(String team1Flag) {
		this.team1Flag = team1Flag;
	}

	public String getTeam2Flag() {
		return team2Flag;
	}

	public void setTeam2Flag(String team2Flag) {
		this.team2Flag = team2Flag;
	}

	public String getTeam1Name() {
		return team1Name;
	}

	public void setTeam1Name(String team1Name) {
		this.team1Name = team1Name;
	}

	public String getTeam2Name() {
		return team2Name;
	}

	public void setTeam2Name(String team2Name) {
		this.team2Name = team2Name;
	}

	public String getTeam1NameShort() {
		return team1NameShort;
	}

	public void setTeam1NameShort(String team1NameShort) {
		this.team1NameShort = team1NameShort;
	}

	public String getTeam2NameShort() {
		return team2NameShort;
	}

	public void setTeam2NameShort(String team2NameShort) {
		this.team2NameShort = team2NameShort;
	}

	public int getGuessTeamId() {
		return guessTeamId;
	}

	public void setGuessTeamId(int guessTeamId) {
		this.guessTeamId = guessTeamId;
	}
}
