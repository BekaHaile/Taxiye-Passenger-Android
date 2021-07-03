package product.clicklabs.jugnoo.offers.historyadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.offers.model.AirtimeHistory;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder>{

    public interface Actions {
        void onCall(AirtimeHistory item);
    }

    private ArrayList<AirtimeHistory> airtimeHistories;
    private boolean isSummary;
    private Actions actions;

    // Pass in the deliveryItems array into the constructor
    public HistoryRecyclerViewAdapter(ArrayList<AirtimeHistory> airtimeHistories) {
        this.airtimeHistories = airtimeHistories;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView voucherNumber;
        public TextView airtimeAmount;
        public TextView date;
        public ImageView callButton;

        public ViewHolder(View itemView) {
            super(itemView);

            voucherNumber = (TextView) itemView.findViewById(R.id.voucher_number);
            airtimeAmount = (TextView) itemView.findViewById(R.id.airtime_amount);
            date = (TextView) itemView.findViewById(R.id.date);
            callButton = (ImageView) itemView.findViewById(R.id.call_button);
        }
    }

    @NonNull
    @Override
    public HistoryRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View deliveryView = inflater.inflate(R.layout.airtime_hist_list, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(deliveryView);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull HistoryRecyclerViewAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        AirtimeHistory airtimeHistory = airtimeHistories.get(position);

        TextView textView = holder.voucherNumber;
        textView.setText(airtimeHistory.getVoucherNumber());
        TextView textViewPhone = holder.airtimeAmount;
        textViewPhone.setText(String.valueOf(airtimeHistory.getAmount()));
        TextView textViewSecondaryPhone = holder.date;
        textViewSecondaryPhone.setText(airtimeHistory.getDate());

        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actions != null) actions.onCall(airtimeHistory);
            }
        });

    }

    @Override
    public int getItemCount() {
        return airtimeHistories.size();
    }
}
