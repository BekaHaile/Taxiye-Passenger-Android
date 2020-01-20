package product.clicklabs.jugnoo.promotion.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.model.Ranklist;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by Ankit on 7/17/15.
 */
public class LeaderboardItemsAdapter extends RecyclerView.Adapter<LeaderboardItemsAdapter.ViewHolder> {

    private Activity activity;
    private int rowLayout;
    Ranklist leaderboardItem;
    private ArrayList<Ranklist> leaderboardItems = new ArrayList<>();

    public LeaderboardItemsAdapter(ArrayList<Ranklist> leaderboardItems, Activity activity, int rowLayout) {
        this.leaderboardItems = leaderboardItems;
        this.activity = activity;
        this.rowLayout = rowLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(680, 110);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(LeaderboardItemsAdapter.ViewHolder holder, int position) {
        leaderboardItem = leaderboardItems.get(position);

        holder.textViewRank.setText(leaderboardItem.getRankStr());
        holder.textViewName.setText(leaderboardItem.getName());
        holder.textViewNoOfDownloads.setText(leaderboardItem.getDownloadsStr());

        /*switch(position){
            case 0:
                holder.relativeLayoutRank.setBackgroundResource(R.color.rank_1);
                break;
            case 1:
                holder.relativeLayoutRank.setBackgroundResource(R.color.rank_2);
                break;
            case 2:
                holder.relativeLayoutRank.setBackgroundResource(R.color.rank_3);
                break;
            case 3:
                holder.relativeLayoutRank.setBackgroundResource(R.color.rank_4);
                break;
            case 4:
                holder.relativeLayoutRank.setBackgroundResource(R.color.rank_5);
                break;
            case 5:
                holder.relativeLayoutRank.setBackgroundResource(R.color.rank_6);
                break;
            default:
                holder.relativeLayoutRank.setBackgroundResource(R.color.rank_6);
                break;
        }*/

        if(leaderboardItem.getIsUser()){
            holder.textViewRank.setTextColor(activity.getResources().getColor(R.color.theme_color));
            holder.textViewName.setTextColor(activity.getResources().getColor(R.color.theme_color));
            holder.textViewNoOfDownloads.setTextColor(activity.getResources().getColor(R.color.theme_color));
        }
        else{
            holder.textViewRank.setTextColor(activity.getResources().getColor(R.color.text_color));
            holder.textViewName.setTextColor(activity.getResources().getColor(R.color.text_color));
            holder.textViewNoOfDownloads.setTextColor(activity.getResources().getColor(R.color.text_color));
        }

	}

    @Override
    public int getItemCount() {
        return leaderboardItems == null ? 0 : leaderboardItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative, relativeLayoutRank;
        public TextView textViewRank, textViewName, textViewNoOfDownloads;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            relativeLayoutRank = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutRank);
            textViewRank = (TextView)itemView.findViewById(R.id.textViewRank);
            textViewRank.setTypeface(Fonts.mavenMedium(activity));
            textViewName = (TextView)itemView.findViewById(R.id.textViewName);
            textViewName.setTypeface(Fonts.mavenMedium(activity));
            textViewNoOfDownloads = (TextView)itemView.findViewById(R.id.textViewNoOfDownloads);
            textViewNoOfDownloads.setTypeface(Fonts.mavenMedium(activity));
        }
    }
}
