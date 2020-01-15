package product.clicklabs.jugnoo.stripe;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.stripe.model.StripeCardData;
import product.clicklabs.jugnoo.wallet.WalletCore;

class StripeCardsAdapter extends RecyclerView.Adapter<StripeCardsAdapter.ViewHolder> {

    private ArrayList<StripeCardData> stripeCardData;
    private Activity activity;
    callback pCallback;

    public StripeCardsAdapter(Activity activity, ArrayList<StripeCardData> listdata,callback  pCallback) {
        this.stripeCardData = listdata;
        this.activity = activity;
        this.pCallback = pCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pCallback.onDelete(position);
            }
        });
        viewHolder.textView.setText(WalletCore.getStripeCardDisplayString(activity, stripeCardData.get(position).getLast4()));
        viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(WalletCore.getBrandImage(stripeCardData.get(position).getBrand()), 0, 0, 0);
    }


    @Override
    public int getItemCount() {
        return stripeCardData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView delete;
        public RelativeLayout viewForeground;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.tv_card);
            this.delete = itemView.findViewById(R.id.delete);
            this.viewForeground = (RelativeLayout) itemView.findViewById(R.id.cards_view_foreground);

        }
    }

}