package product.clicklabs.jugnoo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.ItemListener;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.BidInfo;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by Parminder Saini on 13/06/17.
 */

public class BidsPlacedAdapter extends RecyclerView.Adapter<BidsPlacedAdapter.MyViewHolder> implements ItemListener{

	private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<BidInfo> bidInfos;
    private RecyclerView recyclerView;
    private Callback callback;

    public BidsPlacedAdapter(Context context, RecyclerView recyclerView, Callback callback) {
    	this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.recyclerView = recyclerView;
        this.callback = callback;
    }

    public void setList(ArrayList<BidInfo> bidInfos){
    	this.bidInfos = bidInfos;
    	notifyDataSetChanged();
		recyclerView.setVisibility(getItemCount() == 0 ? View.GONE : View.VISIBLE);
	}

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = layoutInflater.inflate(R.layout.list_item_bid_request,parent,false);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) convertView.getLayoutParams();
        params.setMarginStart(convertView.getContext().getResources().getDimensionPixelSize(R.dimen.dp_2));
        params.setMarginEnd(convertView.getContext().getResources().getDimensionPixelSize(R.dimen.dp_2));
		convertView.setLayoutParams(params);

        return new MyViewHolder(convertView, this);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
    	BidInfo bidInfo = bidInfos.get(position);
        holder.tvBidNumber.setText(context.getString(R.string.hash_format, String.valueOf(position+1)));
        holder.tvBidValue.setText(Utils.formatCurrencyValue(bidInfo.getCurrency(), bidInfo.getBidValue()));
		if(bidInfo.getDistance() >= 1000){
			holder.tvBidDistance.setText(context.getString(R.string.distance_km_away_format, Utils.getDecimalFormat2Decimal().format(bidInfo.getDistance()/1000d)));
		} else {
			holder.tvBidDistance.setText(context.getString(R.string.distance_m_away_format, Utils.getMoneyDecimalFormatWithoutFloat().format(bidInfo.getDistance())));
		}
		holder.tvBidRating.setText(Utils.getDecimalFormat1Decimal().format(bidInfo.getRating()));
    }

    @Override
    public int getItemCount() {
        return bidInfos==null?0:bidInfos.size();
    }

    @Override
    public void onClickItem(View viewClicked, View parentView) {
        if(viewClicked.getId()==R.id.llRoot){
            int position = recyclerView.getChildAdapterPosition(parentView);
            if(position != RecyclerView.NO_POSITION){
				callback.onBidClicked(bidInfos.get(position));
            }
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llRoot;
        private TextView tvBidNumber, tvBidValue, tvBidDistance, tvBidRating;
        public MyViewHolder(final View itemView, final ItemListener itemListener) {
            super(itemView);
			llRoot = (LinearLayout) itemView.findViewById(R.id.llRoot);
			tvBidNumber = (TextView) itemView.findViewById(R.id.tvBidNumber);
			tvBidValue = (TextView) itemView.findViewById(R.id.tvBidValue);
			tvBidDistance = (TextView) itemView.findViewById(R.id.tvBidDistance);
			tvBidRating = (TextView) itemView.findViewById(R.id.tvBidRating);
			llRoot.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(llRoot, itemView);
				}
			});
        }
    }

    public interface Callback{
		void onBidClicked(BidInfo bidInfo);
	}

}
