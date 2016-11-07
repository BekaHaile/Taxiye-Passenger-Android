package product.clicklabs.jugnoo.home.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.retrofit.model.SubItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Ankit on 7/17/15.
 */
public class SpecialPickupItemsAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private List<String> subItems;
	private Callback callback;

	public SpecialPickupItemsAdapter(Context context, ArrayList<String> subItems, Callback callback) {
		this.context = context;
		this.subItems = subItems;
		this.callback = callback;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public synchronized void setResults(ArrayList<String> subItems) {
		this.subItems = subItems;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return subItems == null ? 5 : 5;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MainViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_special_pickup, null);
			holder = new MainViewHolder(convertView, context);

			holder.relative.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, 80));
			ASSL.DoMagic(holder.relative);

			convertView.setTag(holder);
		} else {
			holder = (MainViewHolder) convertView.getTag();
		}
		onBindViewHolder(holder, position);

		return convertView;
	}



	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		final MainViewHolder mHolder = ((MainViewHolder) holder);

		try {
			mHolder.tvPickupName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					callback.onPickedSelected(mHolder.tvPickupName.getText().toString());
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static class MainViewHolder extends RecyclerView.ViewHolder {
		public RelativeLayout relative;
		public TextView tvPickupName;

		public MainViewHolder(View itemView, Context context) {
			super(itemView);
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);
			tvPickupName = (TextView) itemView.findViewById(R.id.tvPickupName); tvPickupName.setTypeface(Fonts.mavenMedium(context));
		}
	}

	public interface Callback{
		void onPickedSelected(String pickedName);
	}
}
