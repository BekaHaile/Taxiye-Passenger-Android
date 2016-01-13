package product.clicklabs.jugnoo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.FeedbackReason;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by socomo20 on 7/27/15.
 */
public class FeedbackReasonsAdapter extends BaseAdapter {

    LayoutInflater mInflater;
    ViewHolderFeedbackReason holder;
    Context context;

    ArrayList<FeedbackReason> feedbackReasons;
    FeedbackReasonsListEventHandler feedbackReasonsListEventHandler;

    public FeedbackReasonsAdapter(Context context, ArrayList<FeedbackReason> feedbackReasons, FeedbackReasonsListEventHandler feedbackReasonsListEventHandler) {
        this.context = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.feedbackReasons = feedbackReasons;

        try {
            for(int i=0; i<this.feedbackReasons.size(); i++){
				this.feedbackReasons.get(i).checked = false;
			}
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.feedbackReasonsListEventHandler = feedbackReasonsListEventHandler;
    }

    @Override
    public int getCount() {
        return feedbackReasons.size();
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

            holder.textViewFeedbackReason = (TextView) convertView.findViewById(R.id.textViewFeedbackReason); holder.textViewFeedbackReason.setTypeface(Fonts.latoRegular(context));
            holder.imageViewFeedbackReasonCheck = (ImageView) convertView.findViewById(R.id.imageViewFeedbackReasonCheck);

            holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);

            holder.relative.setTag(holder);

            holder.relative.setLayoutParams(new ListView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
            ASSL.DoMagic(holder.relative);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderFeedbackReason) convertView.getTag();
        }

        holder.id = position;

        FeedbackReason feedbackReason = feedbackReasons.get(position);

        holder.textViewFeedbackReason.setText(feedbackReason.name);

        if(feedbackReason.checked){
            holder.relative.setBackgroundColor(Color.WHITE);
            holder.imageViewFeedbackReasonCheck.setImageResource(R.drawable.check_box_checked);
        }
        else{
            holder.relative.setBackgroundColor(Color.TRANSPARENT);
            holder.imageViewFeedbackReasonCheck.setImageResource(R.drawable.check_box_unchecked);
        }

        holder.relative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    holder = (ViewHolderFeedbackReason) v.getTag();
                    if(feedbackReasons.get(holder.id).checked){
                        feedbackReasons.get(holder.id).checked = false;
                    }
                    else{
                        feedbackReasons.get(holder.id).checked = true;
                    }
                    notifyDataSetChanged();

                    feedbackReasonsListEventHandler.onLastItemSelected(isLastSelected());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

    public String getSelectedReasons(){
        String reasons = "";
        for(int i=0; i<feedbackReasons.size(); i++){
            if(feedbackReasons.get(i).checked) {
                reasons = reasons + feedbackReasons.get(i).name+",";
            }
        }
        if(!reasons.equalsIgnoreCase("")){
            reasons = reasons.substring(0, reasons.length()-1);
        }
        return reasons;
    }

    public boolean isLastSelected(){
        if(feedbackReasons.size() > 0){
            return feedbackReasons.get(feedbackReasons.size()-1).checked;
        }else{
            return false;
        }
    }


    private class ViewHolderFeedbackReason {
        TextView textViewFeedbackReason;
        ImageView imageViewFeedbackReasonCheck;
        LinearLayout relative;
        int id;
    }

	public interface FeedbackReasonsListEventHandler {
		void onLastItemSelected(boolean selected);
	}

}

