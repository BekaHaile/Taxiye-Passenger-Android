package com.sabkuchfresh.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.dialogs.MenusCustomizeOptionDialog;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.CustomizeItem;
import com.sabkuchfresh.retrofit.model.menus.CustomizeItemSelected;
import com.sabkuchfresh.retrofit.model.menus.CustomizeOption;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;
import com.sabkuchfresh.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;


/**
 * Created by Shankar on 7/17/15.
 */
public class MenusCustomizeItemAdapter extends RecyclerView.Adapter<MenusCustomizeItemAdapter.MainViewHolder> implements FlurryEventNames {

    private Context context;
    private List<CustomizeItem> customizeItems;
    private Callback callback;
    public MenusCustomizeItemAdapter(Context context, ArrayList<CustomizeItem> customizeItems, Callback callback) {
        this.context = context;
        this.customizeItems = customizeItems;
        this.callback = callback;
    }


    @Override
    public MenusCustomizeItemAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menus_customize_item, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);
        ASSL.DoMagic(v);
        return new MainViewHolder(v, context);
    }

    @Override
    public void onBindViewHolder(MenusCustomizeItemAdapter.MainViewHolder holder, int position) {
        CustomizeItem customizeItem = customizeItems.get(position);

        holder.textViewCustomizeItemCategoryName.setText(customizeItem.getCustomizeItemName());

        StringBuilder sb = new StringBuilder();
        Double sumItemPrice = 0d;
        CustomizeItemSelected customizeItemSelected = new CustomizeItemSelected(customizeItem.getCustomizeId());
        int index = callback.getItemSelected().getCustomizeItemSelectedList().indexOf(customizeItemSelected);
        if(index > -1){
            customizeItemSelected = callback.getItemSelected().getCustomizeItemSelectedList().get(index);
            if(customizeItemSelected.getCustomizeOptions().size() > 0) {
                holder.relativeLayoutCustomizeItemAddItem.setVisibility(View.VISIBLE);
                for (CustomizeOption customizeOption : customizeItem.getCustomizeOptions()) {
                    if (customizeItemSelected.getCustomizeOptions().contains(customizeOption.getCustomizeOptionId())) {
                        sb.append(customizeOption.getCustomizeOptionName());
                        sb.append(",");
                        sumItemPrice = sumItemPrice + customizeOption.getCustomizePrice();
                    }
                }
                String selected = sb.toString();
                if(selected.length() > 0){
                    selected = selected.substring(0, selected.length()-1);
                }
                holder.textViewCustomizeItemDesc.setText(selected);
                holder.textViewItemAdditionalCostValue.setText(context.getString(R.string.rupees_value_format,
                        Utils.getMoneyDecimalFormat().format(sumItemPrice)));
            } else {
                holder.relativeLayoutCustomizeItemAddItem.setVisibility(View.GONE);
            }
        } else {
            holder.relativeLayoutCustomizeItemAddItem.setVisibility(View.GONE);
        }


        holder.cardViewCustomizeItem.setTag(position);
        holder.cardViewCustomizeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int pos = (int) v.getTag();
                    CustomizeItemSelected customizeItemSelected = new CustomizeItemSelected(customizeItems.get(pos).getCustomizeId());
                    int index = callback.getItemSelected().getCustomizeItemSelectedList().indexOf(customizeItemSelected);
                    if(index > -1){
                        customizeItemSelected = callback.getItemSelected().getCustomizeItemSelectedList().get(index);
                    } else {
                        callback.getItemSelected().getCustomizeItemSelectedList().add(customizeItemSelected);
                    }

                    new MenusCustomizeOptionDialog((FreshActivity)context, new MenusCustomizeOptionDialog.Callback() {
                        @Override
                        public void onDismiss() {

                        }
                    }, MenusCustomizeItemAdapter.this).show(customizeItems.get(pos), customizeItemSelected);

                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return customizeItems == null ? 0 : customizeItems.size();
    }


    static class MainViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout relative, relativeLayoutCustomizeItem, relativeLayoutCustomizeItemAddItem;
        public CardView cardViewCustomizeItem;

        private ImageView imageViewArrow;
        public TextView textViewCustomizeItemCategoryName, textViewCustomizeItemDesc, textViewItemAdditionalCost, textViewItemAdditionalCostValue;

        public MainViewHolder(View itemView, Context context) {
            super(itemView);

            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            relativeLayoutCustomizeItem = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutCustomizeItem);
            relativeLayoutCustomizeItemAddItem = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutCustomizeItemAddItem);
            cardViewCustomizeItem = (CardView) itemView.findViewById(R.id.cardViewCustomizeItem);
            imageViewArrow = (ImageView) itemView.findViewById(R.id.imageViewArrow);

            textViewCustomizeItemCategoryName = (TextView) itemView.findViewById(R.id.textViewCustomizeItemCategoryName);textViewCustomizeItemCategoryName.setTypeface(Fonts.mavenRegular(context));
            textViewCustomizeItemDesc = (TextView) itemView.findViewById(R.id.textViewCustomizeItemDesc);textViewCustomizeItemDesc.setTypeface(Fonts.mavenRegular(context));

            textViewItemAdditionalCost = (TextView) itemView.findViewById(R.id.textViewItemAdditionalCost);textViewItemAdditionalCost.setTypeface(Fonts.mavenRegular(context));
            textViewItemAdditionalCostValue = (TextView) itemView.findViewById(R.id.textViewItemAdditionalCostValue);textViewItemAdditionalCostValue.setTypeface(Fonts.mavenRegular(context));
        }
    }


    public interface Callback{
        ItemSelected getItemSelected();
    }

}
