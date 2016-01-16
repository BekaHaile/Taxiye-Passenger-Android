package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class SupportFAQItemsAdapter extends RecyclerView.Adapter<SupportFAQItemsAdapter.ViewHolder> {

    private Activity activity;
    private int rowLayout;
    private ArrayList<SupportFAQ> supportFAQs = new ArrayList<>();

    public SupportFAQItemsAdapter(ArrayList<SupportFAQ> supportFAQs, Activity activity, int rowLayout) {
        this.supportFAQs = supportFAQs;
        this.activity = activity;
        this.rowLayout = rowLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, 95);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(SupportFAQItemsAdapter.ViewHolder holder, int position) {
        SupportFAQ supportFAQ = supportFAQs.get(position);
        holder.textViewFaqItemName.setText(supportFAQ.getName());
	}

    @Override
    public int getItemCount() {
        return supportFAQs == null ? 0 : supportFAQs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView textViewFaqItemName;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            root = (LinearLayout) itemView.findViewById(R.id.root);
            textViewFaqItemName = (TextView)itemView.findViewById(R.id.textViewFaqItemName);
            textViewFaqItemName.setTypeface(Fonts.mavenLight(activity));
        }
    }
}
