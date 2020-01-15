package product.clicklabs.jugnoo.support.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by Ankit on 7/17/15.
 */
public class SupportFAQItemsAdapter extends RecyclerView.Adapter<SupportFAQItemsAdapter.ViewHolder> {

    private Activity activity;
    private int rowLayout;
    private ArrayList<ShowPanelResponse.Item> items = new ArrayList<>();
    private Callback callback;

    public SupportFAQItemsAdapter(ArrayList<ShowPanelResponse.Item> items, Activity activity, int rowLayout, Callback callback) {
        if(items != null){
            this.items = items;
        }
        this.activity = activity;
        this.rowLayout = rowLayout;
        this.callback = callback;
    }

    public void setResults(ArrayList<ShowPanelResponse.Item> items){
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(SupportFAQItemsAdapter.ViewHolder holder, int position) {
        ShowPanelResponse.Item supportFAq = items.get(position);
        holder.textViewFaqItemName.setText(supportFAq.getText());
        holder.root.setTag(position);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = (int) v.getTag();
                callback.onClick(clickedPosition, items.get(clickedPosition));
            }
        });

        if(position == getItemCount()-1){
            holder.imageViewSep.setVisibility(View.GONE);
        }
        else{
            holder.imageViewSep.setVisibility(View.VISIBLE);
        }

	}

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView textViewFaqItemName;
        public ImageView imageViewSep;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            root = (LinearLayout) itemView.findViewById(R.id.root);
            textViewFaqItemName = (TextView)itemView.findViewById(R.id.textViewFaqItemName);
            textViewFaqItemName.setTypeface(Fonts.mavenRegular(activity));
            imageViewSep = (ImageView) itemView.findViewById(R.id.imageViewSep);
        }
    }

    public interface Callback{
        void onClick(int position, ShowPanelResponse.Item supportFAq);
    }

}
