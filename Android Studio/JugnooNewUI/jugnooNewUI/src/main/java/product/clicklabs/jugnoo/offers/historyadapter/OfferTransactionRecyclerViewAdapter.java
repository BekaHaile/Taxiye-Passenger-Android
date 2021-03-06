package product.clicklabs.jugnoo.offers.historyadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.offers.model.AirtimeHistory;
import product.clicklabs.jugnoo.offers.model.OfferTransaction;
import product.clicklabs.jugnoo.offers.model.Transaction;
import product.clicklabs.jugnoo.utils.DateOperations;

public class OfferTransactionRecyclerViewAdapter extends RecyclerView.Adapter<OfferTransactionRecyclerViewAdapter.ViewHolder>{

    public interface Actions {
        void onCall(AirtimeHistory item);
    }

    private ArrayList<Transaction> offerTransactions;
    private boolean isSummary;
    private Actions actions;
    private Context context;

    // Pass in the deliveryItems array into the constructor
    public OfferTransactionRecyclerViewAdapter(ArrayList<Transaction> offerTransactions) {
        this.offerTransactions = offerTransactions;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView type;
        public TextView points;
        public TextView time;

        public ViewHolder(View itemView) {
            super(itemView);

            type = (TextView) itemView.findViewById(R.id.type);
            points = (TextView) itemView.findViewById(R.id.points);
            time = (TextView) itemView.findViewById(R.id.time);
        }
    }

    @NonNull
    @Override
    public OfferTransactionRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
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
        Transaction offerTransaction = offerTransactions.get(position);

        TextView typeView = holder.type;
        typeView.setText(offerTransaction.getType());
        TextView textViewPoints = holder.points;
        textViewPoints.setText(String.valueOf(offerTransaction.getPoints()));
        TextView textViewTime = holder.time;

        String dateString = offerTransaction.getTime();

        String temp = dateString.replace("Z", " UTC");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        Date date = null;
        try {
            date = sdf.parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat newDate = new SimpleDateFormat("MMM d, h:mm a");
        String finalDate = newDate.format(date);

        textViewTime.setText(finalDate);
    }

    @Override
    public int getItemCount() {
        return offerTransactions.size();
    }
}
