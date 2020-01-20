package product.clicklabs.jugnoo.home.trackinglog;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 5/10/16.
 */
public class TrackingLogDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private Activity activity;
	private ArrayList<TrackingLogReponse.Datum> data = new ArrayList<>();
	private Callback callback;

	public TrackingLogDataAdapter(Activity activity, ArrayList<TrackingLogReponse.Datum> data, Callback callback) {
		this.activity = activity;
		this.data = data;
		this.callback = callback;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if(viewType == TrackingLogReponse.ViewType.LABEL.getOrdinal()){
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_tracking_log_data_type, parent, false);

			RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
			v.setLayoutParams(layoutParams);
			ASSL.DoMagic(v);

			return new ViewHolderLogType(v, activity);
		} else{
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_tracking_log_data, parent, false);

			RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
			v.setLayoutParams(layoutParams);
			ASSL.DoMagic(v);

			return new ViewHolder(v, activity);
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		TrackingLogReponse.Datum datum = data.get(position);
		if(holder instanceof ViewHolderLogType){
			ViewHolderLogType viewHolder = (ViewHolderLogType) holder;
			viewHolder.textViewLogType.setText(datum.getLabel());

		} else if(holder instanceof ViewHolder){
			ViewHolder viewHolder = (ViewHolder) holder;
			viewHolder.textViewDate.setText(DateOperations.utcToLocalWithTZFallback(datum.getTimestamp()));
			viewHolder.relative.setTag(position);
			viewHolder.relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						int pos = (int) v.getTag();
						callback.onItemClicked(data.get(pos));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public int getItemViewType(int position) {
		return data.get(position).getViewType().getOrdinal();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		public RelativeLayout relative;
		public TextView textViewDate;
		public ViewHolder(View itemView, Activity activity) {
			super(itemView);
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			textViewDate = (TextView)itemView.findViewById(R.id.textViewDate);
			textViewDate.setTypeface(Fonts.mavenLight(activity));
		}
	}

	class ViewHolderLogType extends RecyclerView.ViewHolder {
		public RelativeLayout relative;
		public TextView textViewLogType;
		public ViewHolderLogType(View itemView, Activity activity) {
			super(itemView);
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			textViewLogType = (TextView) itemView.findViewById(R.id.textViewLogType);
			textViewLogType.setTypeface(Fonts.mavenMedium(activity));
		}
	}

	public interface Callback{
		void onItemClicked(TrackingLogReponse.Datum datum);
	}

}
