package product.clicklabs.jugnoo.t20.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.t20.models.MatchScheduleResponse;
import product.clicklabs.jugnoo.t20.models.Schedule;
import product.clicklabs.jugnoo.t20.models.Selection;
import product.clicklabs.jugnoo.t20.models.Team;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by Shankar on 7/17/15.
 */
public class MatchScheduleAdapter extends RecyclerView.Adapter<MatchScheduleAdapter.ViewHolder> {

    private Activity activity;
    private MatchScheduleResponse matchScheduleResponse = null;

    public MatchScheduleAdapter(MatchScheduleResponse matchScheduleResponse, Activity activity) {
        if(matchScheduleResponse != null){
            this.matchScheduleResponse = matchScheduleResponse;
        }
        this.activity = activity;
    }

    public void setResults(MatchScheduleResponse matchScheduleResponse){
        this.matchScheduleResponse = matchScheduleResponse;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_t20_schedule, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(MatchScheduleAdapter.ViewHolder holder, int position) {
        Schedule schedule = matchScheduleResponse.getSchedule().get(position);

        if(schedule.getTeam1() == null){
            int index1 = matchScheduleResponse.getTeams().indexOf(new Team(schedule.getTeam1Id(), "", "", ""));
            if (-1 != index1) {
                matchScheduleResponse.getSchedule().get(position).setTeam1(matchScheduleResponse.getTeams().get(index1));
            }
        }
        if(schedule.getTeam2() == null){
            int index2 = matchScheduleResponse.getTeams().indexOf(new Team(schedule.getTeam2Id(), "", "", ""));
            if (-1 != index2) {
                matchScheduleResponse.getSchedule().get(position).setTeam2(matchScheduleResponse.getTeams().get(index2));
            }
        }
        if(schedule.getSelectedTeamId() == null){
            int index = matchScheduleResponse.getSelections().indexOf(new Selection(schedule.getScheduleId(), -1));
            if(-1 != index){
                matchScheduleResponse.getSchedule().get(position)
                        .setSelectedTeamId(matchScheduleResponse.getSelections().get(index).getTeamId());
            } else{
                matchScheduleResponse.getSchedule().get(position).setSelectedTeamId(-1);
            }
        }
        schedule = matchScheduleResponse.getSchedule().get(position);


        holder.textViewDate.setText(schedule.getDate());
        holder.textViewMonth.setText(schedule.getMonth());
        holder.textViewTime.setText(schedule.getTime());
        holder.textViewTeam1.setText(schedule.getTeam1().getShortName());
        holder.textViewTeam2.setText(schedule.getTeam2().getShortName());
        if(!schedule.getSelectedTeamId().equals(-1)) {
            holder.relativeLayoutGuess.setVisibility(View.VISIBLE);
            if (schedule.getSelectedTeamId().equals(schedule.getTeam1Id())) {
                holder.textViewYourGuessValue.setText(schedule.getTeam1().getName());
            } else if (schedule.getSelectedTeamId().equals(schedule.getTeam2Id())){
                holder.textViewYourGuessValue.setText(schedule.getTeam2().getName());
            } else{
                holder.textViewYourGuessValue.setText("-");
            }
        } else{
            if(schedule.getCalendar().getTimeInMillis() > System.currentTimeMillis()){
                holder.relativeLayoutGuess.setVisibility(View.GONE);
            } else{
                holder.relativeLayoutGuess.setVisibility(View.VISIBLE);
                holder.textViewYourGuessValue.setText("-");
            }
        }

        try{
            Picasso.with(activity).load(schedule.getTeam1().getFlagImageUrl())
                    .placeholder(R.drawable.ic_t20_flag).error(R.drawable.ic_t20_flag)
                    .into(holder.imageViewTeam1Flag);
        } catch(Exception e){
            e.printStackTrace();
        }
        try{
            Picasso.with(activity).load(schedule.getTeam2().getFlagImageUrl())
                    .placeholder(R.drawable.ic_t20_flag).error(R.drawable.ic_t20_flag)
                    .into(holder.imageViewTeam2Flag);
        } catch(Exception e){
            e.printStackTrace();
        }

	}

    @Override
    public int getItemCount() {
        return matchScheduleResponse == null ? 0 : matchScheduleResponse.getSchedule().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout relative;
        public TextView textViewDate, textViewMonth, textViewTime, textViewTeam1, textViewVS, textViewTeam2;
        public ImageView imageViewTeam1Flag, imageViewTeam2Flag;
        public RelativeLayout relativeLayoutGuess;
        public TextView textViewYourGuess, textViewYourGuessValue;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            relative = (LinearLayout) itemView.findViewById(R.id.relative);
            textViewDate = (TextView)itemView.findViewById(R.id.textViewDate); textViewDate.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
            textViewMonth = (TextView) itemView.findViewById(R.id.textViewMonth); textViewMonth.setTypeface(Fonts.mavenRegular(activity));
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime); textViewTime.setTypeface(Fonts.mavenRegular(activity));
            textViewTeam1 = (TextView) itemView.findViewById(R.id.textViewTeam1); textViewTeam1.setTypeface(Fonts.mavenRegular(activity));
            textViewVS = (TextView) itemView.findViewById(R.id.textViewVS); textViewVS.setTypeface(Fonts.mavenRegular(activity));
            textViewTeam2 = (TextView) itemView.findViewById(R.id.textViewTeam2); textViewTeam2.setTypeface(Fonts.mavenRegular(activity));
            textViewYourGuess = (TextView) itemView.findViewById(R.id.textViewYourGuess); textViewYourGuess.setTypeface(Fonts.mavenRegular(activity));
            textViewYourGuessValue = (TextView) itemView.findViewById(R.id.textViewYourGuessValue); textViewYourGuessValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
            imageViewTeam1Flag = (ImageView) itemView.findViewById(R.id.imageViewTeam1Flag);
            imageViewTeam2Flag = (ImageView) itemView.findViewById(R.id.imageViewTeam2Flag);
            relativeLayoutGuess = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutGuess);
        }
    }

}
