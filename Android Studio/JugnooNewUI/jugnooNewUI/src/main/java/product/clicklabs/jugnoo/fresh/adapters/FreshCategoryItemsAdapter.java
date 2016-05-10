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
    private Callback callback;
    private OpenMode openMode;

    public FreshCategoryItemsAdapter(Context context, ArrayList<SubItem> subItems, OpenMode openMode, Callback callback) {
        this.context = context;
        this.subItems = subItems;
        this.callback = callback;
        this.openMode = openMode;
    }

    public synchronized void setResults(ArrayList<SubItem> subItems){
        this.subItems = subItems;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_category_item, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 194);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, context);
    }

    @Override
    public void onBindViewHolder(FreshCategoryItemsAdapter.ViewHolder holder, int position) {
        SubItem subItem = subItems.get(position);

        holder.textViewItemName.setText(subItem.getSubItemName());
        holder.textViewItemUnit.setText(subItem.getBaseUnit());
        holder.textViewItemPrice.setText(String.format(context.getResources().getString(R.string.rupees_value_format),
                subItem.getPrice()));
        holder.textViewQuantity.setText(String.valueOf(subItem.getSubItemQuantitySelected()));

        if(openMode == OpenMode.CART){
            holder.imageViewDelete.setVisibility(View.GONE);
        } else{
            holder.imageViewDelete.setVisibility(View.GONE);
        }

        holder.imageViewMinus.setTag(position);
        holder.imageViewPlus.setTag(position);
        holder.imageViewDelete.setTag(position);

        holder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int pos = (int) v.getTag();
                    subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() > 0 ?
                            subItems.get(pos).getSubItemQuantitySelected() - 1 : 0);
                    callback.onMinusClicked(pos, subItems.get(pos));
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
                    subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock() ?
                            subItems.get(pos).getSubItemQuantitySelected() + 1 : subItems.get(pos).getStock());
                    callback.onPlusClicked(pos, subItems.get(pos));
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int pos = (int) v.getTag();
                    subItems.get(pos).setSubItemQuantitySelected(0);
                    callback.onDeleteClicked(pos, subItems.get(pos));
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try{
            if(subItem.getSubItemImage() != null && !"".equalsIgnoreCase(subItem.getSubItemImage())) {
                Picasso.with(context).load(subItem.getSubItemImage())
                        .placeholder(R.drawable.ic_fresh_item_placeholder)
                        .error(R.drawable.ic_fresh_item_placeholder)
                        .into(holder.imageViewItemImage);
            } else{
                holder.imageViewItemImage.setImageResource(R.drawable.ic_fresh_item_placeholder);
            }
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
        private ImageView imageViewItemImage, imageViewMinus, imageViewPlus, imageViewDelete;
        public TextView textViewItemName, textViewItemUnit, textViewItemPrice, textViewQuantity;
        public ViewHolder(View itemView, Context context) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            imageViewItemImage = (ImageView) itemView.findViewById(R.id.imageViewItemImage);
            imageViewMinus = (ImageView) itemView.findViewById(R.id.imageViewMinus);
            imageViewPlus = (ImageView) itemView.findViewById(R.id.imageViewPlus);
            imageViewDelete = (ImageView) itemView.findViewById(R.id.imageViewDelete);

            textViewItemName = (TextView)itemView.findViewById(R.id.textViewItemName); textViewItemName.setTypeface(Fonts.mavenRegular(context));
            textViewItemUnit = (TextView)itemView.findViewById(R.id.textViewItemUnit); textViewItemUnit.setTypeface(Fonts.mavenRegular(context));
            textViewItemPrice = (TextView)itemView.findViewById(R.id.textViewItemPrice); textViewItemPrice.setTypeface(Fonts.mavenRegular(context));
            textViewQuantity = (TextView)itemView.findViewById(R.id.textViewQuantity); textViewQuantity.setTypeface(Fonts.mavenRegular(context));
        }
    }

    public interface Callback{
        void onPlusClicked(int position, SubItem subItem);
        void onMinusClicked(int position, SubItem subItem);
        void onDeleteClicked(int position, SubItem subItem);
    }

    public enum OpenMode{
        INVENTORY, CART;
    }

}