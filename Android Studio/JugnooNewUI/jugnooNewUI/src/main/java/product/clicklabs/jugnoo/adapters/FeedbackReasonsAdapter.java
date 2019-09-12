package product.clicklabs.jugnoo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.FeedbackReason;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by socomo20 on 7/27/15.
 */
public class FeedbackReasonsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ViewHolderFeedbackReason holder;
    private Context context;
    private ArrayList<FeedbackReason> feedbackReasons;
    private ArrayList<FeedbackReason> positiveReasons;
    private FeedbackReasonsListEventHandler feedbackReasonsListEventHandler;
    private boolean showPositiveReasons;

    public FeedbackReasonsAdapter(Context context, ArrayList<FeedbackReason> feedbackReasons, FeedbackReasonsListEventHandler feedbackReasonsListEventHandler) {
        this.context = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.feedbackReasons = feedbackReasons;
        this.feedbackReasonsListEventHandler = feedbackReasonsListEventHandler;
    }


    public FeedbackReasonsAdapter(Context context, ArrayList<FeedbackReason> feedbackReasons, ArrayList<FeedbackReason> positiveReasons, FeedbackReasonsListEventHandler feedbackReasonsListEventHandler) {
        this.context = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.feedbackReasons = feedbackReasons;
        this.positiveReasons = positiveReasons;
        this.feedbackReasonsListEventHandler = feedbackReasonsListEventHandler;
    }

    @Override
    public int getCount() {

        return showPositiveReasons ? positiveReasons != null ? positiveReasons.size() : 0 : feedbackReasons != null ? feedbackReasons.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolderFeedbackReason();
            convertView = mInflater.inflate(R.layout.list_item_feedback_reason, null);

            holder.textViewFeedbackReason = (TextView) convertView.findViewById(R.id.textViewFeedbackReason);
            holder.textViewFeedbackReason.setTypeface(Fonts.mavenMedium(context));

            holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);

            holder.textViewFeedbackReason.setTag(holder);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolderFeedbackReason) convertView.getTag();
        }

        holder.id = position;

        final FeedbackReason feedbackReason = showPositiveReasons ? positiveReasons.get(position) : feedbackReasons.get(position);

        holder.textViewFeedbackReason.setText(feedbackReason.name);

        if (feedbackReason.checked) {
            holder.textViewFeedbackReason.setBackgroundResource(R.drawable.capsule_white_theme_stroke);
            holder.textViewFeedbackReason.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            holder.textViewFeedbackReason.setBackgroundResource(R.drawable.capsule_white_stroke);
            holder.textViewFeedbackReason.setTextColor(context.getResources().getColor(R.color.text_color));
        }


        holder.textViewFeedbackReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<FeedbackReason> currentList = showPositiveReasons ? positiveReasons : feedbackReasons;
                try {
                    holder = (ViewHolderFeedbackReason) v.getTag();
                    if (currentList.get(holder.id).checked) {
                        currentList.get(holder.id).checked = false;
                    } else {
                        currentList.get(holder.id).checked = true;
                    }
                    notifyDataSetChanged();

                    feedbackReasonsListEventHandler.onLastItemSelected(isLastSelected(), currentList.get(holder.id).name);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

    public String getSelectedReasons() {
        ArrayList<FeedbackReason> currentList = showPositiveReasons ? positiveReasons : feedbackReasons;
        String reasons = "";
        if (currentList != null && currentList.size() > 0) {
            for (int i = 0; i < currentList.size(); i++) {
                if (currentList.get(i).checked) {
                    reasons = reasons + currentList.get(i).name + ",";
                }
            }
            if (!reasons.equalsIgnoreCase("")) {
                reasons = reasons.substring(0, reasons.length() - 1);
            }
        }
        return reasons;
    }

    public boolean isLastSelected() {
        ArrayList<FeedbackReason> currentList = showPositiveReasons ? positiveReasons : feedbackReasons;
        if (currentList != null && currentList.size() > 0) {
            return currentList.get(currentList.size() - 1).checked;
        } else {
            return false;
        }
    }

    public void resetSelectedStates() {
        if (positiveReasons != null) {
            for (FeedbackReason feedbackReason : positiveReasons)
                feedbackReason.checked = false;
        }
        if (feedbackReasons != null) {
            for (FeedbackReason feedbackReason : feedbackReasons)
                feedbackReason.checked = false;
        }

        notifyDataSetChanged();
    }

    private class ViewHolderFeedbackReason {
        TextView textViewFeedbackReason;
        LinearLayout relative;
        int id;
    }

    public interface FeedbackReasonsListEventHandler {
        void onLastItemSelected(boolean selected, String name);
    }

    public void resetData(boolean showPositiveReasons) {
        this.showPositiveReasons = showPositiveReasons;
        resetSelectedStates();
        //notify Data Set is called in method resetSelectedStates()

    }
}

