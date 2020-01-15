package product.clicklabs.jugnoo.promotion.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sabkuchfresh.analytics.GAAction;
import com.sabkuchfresh.analytics.GACategory;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.promotion.OfferingPromotion;


/**
 * Created by Shankar on 5/1/17.
 */
public class OfferingPromotionsAdapter extends RecyclerView.Adapter<OfferingPromotionsAdapter.ViewHolder> implements GAAction, GACategory {

	private Activity activity;
	private ArrayList<OfferingPromotion> offeringPromotions = new ArrayList<>();

	public OfferingPromotionsAdapter(Activity activity, ArrayList<OfferingPromotion> offeringPromotions) {
		this.activity = activity;
		this.offeringPromotions = offeringPromotions;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_offering_promotions, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		OfferingPromotion offeringPromotion = offeringPromotions.get(position);
		holder.tvOfferingName.setText(offeringPromotion.getName());

		if(holder.promotionsAdapter == null){
			holder.promotionsAdapter = new PromotionsAdapter(activity, offeringPromotion.getPromoCoupons(),
					holder.rvPromotions, offeringPromotion.getName(), offeringPromotion.getClientId());
		} else {
			holder.promotionsAdapter.setList(offeringPromotion.getPromoCoupons(), offeringPromotion.getName(),
					offeringPromotion.getClientId());
		}
		holder.rvPromotions.setAdapter(holder.promotionsAdapter);

	}

	@Override
	public int getItemCount() {
		return offeringPromotions == null ? 0 : offeringPromotions.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder{
		@BindView(R.id.tvOfferingName)
		TextView tvOfferingName;
		@BindView(R.id.rvPromotions)
		RecyclerView rvPromotions;
		PromotionsAdapter promotionsAdapter;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
			rvPromotions.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false));
		}
	}
}
