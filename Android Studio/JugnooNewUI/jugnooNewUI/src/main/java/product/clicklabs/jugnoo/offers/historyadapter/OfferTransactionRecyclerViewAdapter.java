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
import product.clicklabs.jugnoo.offers.model.OfferTransaction;

public class OfferTransactionRecyclerViewAdapter extends RecyclerView.Adapter<OfferTransactionRecyclerViewAdapter.ViewHolder>{

    public interface Actions {
        void onCall(AirtimeHistory item);
    }

    private ArrayList<OfferTransaction> offerTransactions;
    private boolean isSummary;
    private Actions actions;

    // Pass in the deliveryItems array into the constructor
    public OfferTransactionRecyclerViewAdapter(ArrayList<OfferTransaction> offerTransactions) {
        this.offerTransactions = offerTransactions;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView voucherNumber;
        public TextView airtimeAmount;
        public TextView date;

        public ViewHolder(View itemView) {
            super(itemView);

            voucherNumber = (TextView) itemView.findViewById(R.id.voucher_number);
            airtimeAmount = (TextView) itemView.findViewById(R.id.airtime_amount);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }

    @NonNull
    @Override
    public OfferTransactionRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View deliveryView = inflater.inflate(R.layout.offer_transaction_list, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(deliveryView);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull OfferTransactionRecyclerViewAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        OfferTransaction offerTransaction = offerTransactions.get(position);

        TextView textView = holder.voucherNumber;
        textView.setText(offerTransaction.getVoucherNumber());
        TextView textViewPhone = holder.airtimeAmount;
        textViewPhone.setText(String.valueOf(offerTransaction.getAmount()));
        TextView textViewSecondaryPhone = holder.date;
        textViewSecondaryPhone.setText(offerTransaction.getDate());
    }

    @Override
    public int getItemCount() {
        return offerTransactions.size();
    }
}
