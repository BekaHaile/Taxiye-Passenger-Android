package product.clicklabs.jugnoo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.NearbyDriver;
import rmn.androidscreenlibrary.ASSL;

/**
 * Created by socomo20 on 10/4/15.
 */
public class NearbyDriversAdapter extends RecyclerView.Adapter<NearbyDriversAdapter.NearbyDriverViewHolder> {

	private ArrayList<NearbyDriver> nearbyDrivers;
	private Context context;
	private NearbyDriversAdapterHandler adapterHandler;

	public NearbyDriversAdapter(Context context, ArrayList<NearbyDriver> nearbyDrivers, NearbyDriversAdapterHandler adapterHandler) {
		this.nearbyDrivers = nearbyDrivers;
		this.context = context;
		this.adapterHandler = adapterHandler;
	}

	@Override
	public int getItemCount() {
		return nearbyDrivers.size();
	}

	public void resetALl(){
		for(int i=0; i<nearbyDrivers.size(); i++){
			nearbyDrivers.get(i).ticked = false;
		}
		notifyDataSetChanged();
	}

	@Override
	public void onBindViewHolder(NearbyDriverViewHolder nearbyDriverViewHolder, int i) {
		NearbyDriver nd = nearbyDrivers.get(i);
		if(nd.ticked){
			nearbyDriverViewHolder.linearLayoutNearbyInner.setBackgroundResource(R.drawable.background_yellow_less_rounded);
			nearbyDriverViewHolder.textViewDriverId.setTextColor(context.getResources().getColor(R.color.white));
		}
		else{
			nearbyDriverViewHolder.linearLayoutNearbyInner.setBackgroundResource(R.drawable.background_white_rounded_bordered);
			nearbyDriverViewHolder.textViewDriverId.setTextColor(context.getResources().getColor(R.color.black));
		}
		nearbyDriverViewHolder.textViewDriverId.setText(nd.autoId);
		nearbyDriverViewHolder.linearLayoutNearby.setTag(i);
		nearbyDriverViewHolder.linearLayoutNearby.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for(int i=0; i<nearbyDrivers.size(); i++){
					nearbyDrivers.get(i).ticked = false;
				}
				nearbyDrivers.get((int) v.getTag()).ticked = true;
				notifyDataSetChanged();
				adapterHandler.itemClicked(nearbyDrivers.get((int) v.getTag()));
			}
		});

	}

	@Override
	public NearbyDriverViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.
				from(viewGroup.getContext()).
				inflate(R.layout.list_item_nearby_driver, viewGroup, false);

		return new NearbyDriverViewHolder(itemView);
	}

	public class NearbyDriverViewHolder extends RecyclerView.ViewHolder {
		protected LinearLayout linearLayoutNearby, linearLayoutNearbyInner;
		protected TextView textViewDriverId;

		public NearbyDriverViewHolder(View v) {
			super(v);
			linearLayoutNearby =  (LinearLayout) v.findViewById(R.id.linearLayoutNearby);
			linearLayoutNearbyInner = (LinearLayout) v.findViewById(R.id.linearLayoutNearbyInner);
			textViewDriverId = (TextView)  v.findViewById(R.id.textViewDriverId);
			linearLayoutNearby.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(linearLayoutNearby);
		}
	}


	public interface NearbyDriversAdapterHandler {
		void itemClicked(NearbyDriver nearbyDriver);
	}

}