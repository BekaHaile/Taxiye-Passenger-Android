package product.clicklabs.jugnoo.fresh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.fresh.models.SubItem;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by Shankar on 7/17/15.
 */
public class FreshCategoryItemsAdapter extends RecyclerView.Adapter<FreshCategoryItemsAdapter.ViewHolder> {

    private Context context;
    private List<SubItem> subItems;

    public FreshCategoryItemsAdapter(Context context, ArrayList<SubItem> subItems) {
        this.context = context;
        if(subItems != null){
            this.subItems = subItems;
        }
    }

    public void setResults(ArrayList<SubItem> subItems){
        this.subItems = subItems;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_category_item, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 200);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, context);
    }

    @Override
    public void onBindViewHolder(FreshCategoryItemsAdapter.ViewHolder holder, int position) {
        SubItem subItem = subItems.get(position);

        holder.textViewItemName.setText(subItem.getSubItemName());
        holder.textViewItemUnit.setText(subItem.getSubItemUnit());
        holder.textViewItemPrice.setText(String.format(context.getResources().getString(R.string.rupees_value_format),
                subItem.getPrice()));
        holder.textViewQuantity.setText(String.valueOf(subItem.getSubItemQuantitySelected()));

        holder.imageViewMinus.setTag(position);
        holder.imageViewPlus.setTag(position);

        holder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int pos = (int) v.getTag();
                    subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() > 0 ?
                            subItems.get(pos).getSubItemQuantitySelected() - 1 : 0);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int pos = (int) v.getTag();
                    subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getSubItemTotalQuantity() ?
                            subItems.get(pos).getSubItemQuantitySelected() + 1 : subItems.get(pos).getSubItemTotalQuantity());
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try{
            Picasso.with(context).load(subItem.getSubItemImage()).into(holder.imageViewItemImage);
        } catch(Exception e){
            e.printStackTrace();
        }

	}

    @Override
    public int getItemCount() {
        return subItems == null ? 0 : subItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        private ImageView imageViewItemImage, imageViewMinus, imageViewPlus;
        public TextView textViewItemName, textViewItemUnit, textViewItemPrice, textViewQuantity;
        public ViewHolder(View itemView, Context context) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            imageViewItemImage = (ImageView) itemView.findViewById(R.id.imageViewItemImage);
            imageViewMinus = (ImageView) itemView.findViewById(R.id.imageViewMinus);
            imageViewPlus = (ImageView) itemView.findViewById(R.id.imageViewPlus);

            textViewItemName = (TextView)itemView.findViewById(R.id.textViewItemName); textViewItemName.setTypeface(Fonts.mavenRegular(context));
            textViewItemUnit = (TextView)itemView.findViewById(R.id.textViewItemUnit); textViewItemUnit.setTypeface(Fonts.mavenLight(context));
            textViewItemPrice = (TextView)itemView.findViewById(R.id.textViewItemPrice); textViewItemPrice.setTypeface(Fonts.mavenRegular(context));
            textViewQuantity = (TextView)itemView.findViewById(R.id.textViewQuantity); textViewQuantity.setTypeface(Fonts.mavenLight(context));
        }
    }

}
