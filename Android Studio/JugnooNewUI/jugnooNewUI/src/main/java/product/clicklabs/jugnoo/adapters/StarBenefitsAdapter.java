package product.clicklabs.jugnoo.adapters;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by aneesh on 10/4/15.
 */
public class StarBenefitsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private ArrayList<String> benefits;
	private Context context;

	public StarBenefitsAdapter(Context context, ArrayList<String> benefits) {
		this.benefits = benefits;
		this.context = context;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_benefits, viewGroup, false);

		RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
		itemView.setLayoutParams(layoutParams);
		ASSL.DoMagic(itemView);


		return new ViewHolder(itemView, context);
	}

	@Override
	public int getItemCount() {
		return benefits == null ? 0 : benefits.size();
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if(holder instanceof ViewHolder) {
			final ViewHolder chatViewHolder = ((ViewHolder)holder);

			chatViewHolder.tvBenefitName.setText(benefits.get(position));
		}
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView tvBenefitName;

		public ViewHolder(View itemView, Context context) {
			super(itemView);
			tvBenefitName = (TextView) itemView.findViewById(R.id.tvBenefitName);tvBenefitName.setTypeface(Fonts.mavenMedium(context));

		}
	}

}