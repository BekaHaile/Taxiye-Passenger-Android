package product.clicklabs.jugnoo.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
public class StarMembershipAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private ArrayList<String> benefits;
	private Context context;

	public StarMembershipAdapter(Context context, ArrayList<String> benefits) {
		this.benefits = benefits;
		this.context = context;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_membership, viewGroup, false);

		RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
		itemView.setLayoutParams(layoutParams);
		ASSL.DoMagic(itemView);


		return new ViewHolder(itemView, context);
	}

	@Override
	public int getItemCount() {
		return benefits == null ? 0 : 4;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if(holder instanceof ViewHolder) {
			final ViewHolder viewHolder = ((ViewHolder)holder);

			//chatViewHolder.tvBenefitName.setText(chatHistory.getMessage());
			if(position == 3){
				viewHolder.divider.setVisibility(View.GONE);
			} else{
				viewHolder.divider.setVisibility(View.VISIBLE);
			}
		}
	}



	static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView tvOffering, tvBenefitName;
		public View divider;

		public ViewHolder(View itemView, Context context) {
			super(itemView);
			tvOffering = (TextView) itemView.findViewById(R.id.tvOffering); tvOffering.setTypeface(Fonts.mavenMedium(context));
			tvBenefitName = (TextView) itemView.findViewById(R.id.tvBenefitName);tvBenefitName.setTypeface(Fonts.mavenMedium(context));
			divider = (View) itemView.findViewById(R.id.divider);
		}
	}

}