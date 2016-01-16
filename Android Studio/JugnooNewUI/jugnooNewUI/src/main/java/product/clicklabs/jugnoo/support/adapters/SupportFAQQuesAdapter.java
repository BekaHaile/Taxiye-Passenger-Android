package product.clicklabs.jugnoo.support.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.retrofit.model.SupportFAQ;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by Ankit on 7/17/15.
 */
public class SupportFAQQuesAdapter extends RecyclerView.Adapter<SupportFAQQuesAdapter.ViewHolder> {

    private Activity activity;
    private int rowLayout;
    private ArrayList<SupportFAQ.QuestionAnswer> questionAnswers = new ArrayList<>();
    private Callback callback;

    public SupportFAQQuesAdapter(ArrayList<SupportFAQ.QuestionAnswer> questionAnswers, Activity activity, int rowLayout, Callback callback) {
        this.questionAnswers = questionAnswers;
        this.activity = activity;
        this.rowLayout = rowLayout;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(SupportFAQQuesAdapter.ViewHolder holder, int position) {
        SupportFAQ.QuestionAnswer questionAnswer = questionAnswers.get(position);
        holder.textViewFaqItemName.setText(questionAnswer.getQuestion());
        holder.root.setTag(position);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPos = (int) v.getTag();
                callback.onClick(clickedPos, questionAnswers.get(clickedPos));
            }
        });
        if(position < getItemCount()-1){
            holder.imageViewSep.setVisibility(View.VISIBLE);
        } else{
            holder.imageViewSep.setVisibility(View.GONE);
        }
	}

    @Override
    public int getItemCount() {
        return questionAnswers == null ? 0 : questionAnswers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView textViewFaqItemName;
        public ImageView imageViewSep;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            root = (LinearLayout) itemView.findViewById(R.id.root);
            textViewFaqItemName = (TextView)itemView.findViewById(R.id.textViewFaqItemName);
            textViewFaqItemName.setTypeface(Fonts.mavenLight(activity));
            imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);
        }
    }


    public interface Callback{
        void onClick(int position, SupportFAQ.QuestionAnswer questionAnswer);
    }

}
