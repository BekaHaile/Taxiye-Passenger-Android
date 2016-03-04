package product.clicklabs.jugnoo.t20.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.t20.models.MatchSchedule;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by Ankit on 7/17/15.
 */
public class MatchScheduleAdapter extends RecyclerView.Adapter<MatchScheduleAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<MatchSchedule> matchSchedules = new ArrayList<>();

    public MatchScheduleAdapter(ArrayList<MatchSchedule> matchSchedules, Activity activity) {
        if(matchSchedules != null){
            this.matchSchedules = matchSchedules;
        }
        this.activity = activity;
    }

    public void setResults(ArrayList<MatchSchedule> items){
        this.matchSchedules = items;
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
        MatchSchedule matchSchedule = matchSchedules.get(position);
        holder.textViewDate.setText(matchSchedule.getDate());
        holder.textViewMonth.setText(matchSchedule.getMonth());
        holder.textViewTime.setText(String.format(activity.getResources().getString(R.string.time_ist_format),
                matchSchedule.getTime()));
        holder.textViewTeam1.setText(matchSchedule.getTeam1NameShort());
        holder.textViewTeam2.setText(matchSchedule.getTeam2NameShort());
        if(matchSchedule.getGuessTeamId() == matchSchedule.getTeam1Id()){
            holder.textViewYourGuessValue.setText(matchSchedule.getTeam1NameShort());
        } else{
            holder.textViewYourGuessValue.setText(matchSchedule.getTeam2NameShort());
        }

        try{
            Picasso.with(activity).load(matchSchedule.getTeam1Flag()).into(holder.imageViewTeam1Flag);
        } catch(Exception e){
            e.printStackTrace();
        }
        try{
            Picasso.with(activity).load(matchSchedule.getTeam2Flag()).into(holder.imageViewTeam2Flag);
        } catch(Exception e){
            e.printStackTrace();
        }

        if(matchSchedule.getGuessTeamId() == -1){
            holder.relativeLayoutGuess.setVisibility(View.GONE);
        } else{
            holder.relativeLayoutGuess.setVisibility(View.VISIBLE);
        }
	}

    @Override
    public int getItemCount() {
        return matchSchedules == null ? 0 : matchSchedules.size();
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
            textViewMonth = (TextView) itemView.findViewById(R.id.textViewMonth); textViewMonth.setTypeface(Fonts.mavenLight(activity));
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime); textViewTime.setTypeface(Fonts.mavenLight(activity));
            textViewTeam1 = (TextView) itemView.findViewById(R.id.textViewTeam1); textViewTeam1.setTypeface(Fonts.mavenRegular(activity));
            textViewVS = (TextView) itemView.findViewById(R.id.textViewVS); textViewVS.setTypeface(Fonts.mavenRegular(activity));
            textViewTeam2 = (TextView) itemView.findViewById(R.id.textViewTeam2); textViewTeam2.setTypeface(Fonts.mavenRegular(activity));
            textViewYourGuess = (TextView) itemView.findViewById(R.id.textViewYourGuess); textViewYourGuess.setTypeface(Fonts.mavenLight(activity));
            textViewYourGuessValue = (TextView) itemView.findViewById(R.id.textViewYourGuessValue); textViewYourGuessValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
            imageViewTeam1Flag = (ImageView) itemView.findViewById(R.id.imageViewTeam1Flag);
            imageViewTeam2Flag = (ImageView) itemView.findViewById(R.id.imageViewTeam2Flag);
            relativeLayoutGuess = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutGuess);
        }
    }

}
