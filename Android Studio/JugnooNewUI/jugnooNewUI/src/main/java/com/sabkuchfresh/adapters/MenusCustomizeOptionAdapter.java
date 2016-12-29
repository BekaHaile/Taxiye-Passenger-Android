package com.sabkuchfresh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.retrofit.model.menus.CustomizeItemSelected;
import com.sabkuchfresh.retrofit.model.menus.CustomizeOption;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Shankar on 7/17/15.
 */
public class MenusCustomizeOptionAdapter extends RecyclerView.Adapter<MenusCustomizeOptionAdapter.MainViewHolder> implements FlurryEventNames {

    private Context context;
    private List<CustomizeOption> customizeOptions;
    int optionSelector;
    private Callback callback;

    public MenusCustomizeOptionAdapter(Context context, ArrayList<CustomizeOption> customizeOptions, int optionSelector, Callback callback) {
        this.context = context;
        this.customizeOptions = customizeOptions;
        this.optionSelector = optionSelector;
        this.callback = callback;
    }


    @Override
    public MenusCustomizeOptionAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_customize_option_item, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);
        ASSL.DoMagic(v);
        return new MainViewHolder(v, context);
    }

    @Override
    public void onBindViewHolder(MenusCustomizeOptionAdapter.MainViewHolder holder, int position) {
        CustomizeOption customizeOption = customizeOptions.get(position);
        holder.textViewCustomizeOptionItemName.setText(customizeOption.getCustomizeOptionName());

        if(position == getItemCount()-1){
            holder.imageViewSaperator.setVisibility(View.GONE);
        } else{
            holder.imageViewSaperator.setVisibility(View.VISIBLE);
        }

        holder.textViewimageViewCustomizeOptionItemPrice.setText(context.getString(R.string.rupees_value_format,
                Utils.getMoneyDecimalFormat().format(customizeOption.getCustomizePrice())));

        if(callback.getCustomizeItemSelected().getCustomizeOptions().contains(customizeOption.getCustomizeOptionId())){
            holder.imageViewCustomizeOptionItem.setImageResource(optionSelector == 1 ? R.drawable.checkbox_signup_checked : R.drawable.ic_radio_button_selected);
        } else {
            holder.imageViewCustomizeOptionItem.setImageResource(optionSelector == 1 ? R.drawable.check_box_unchecked : R.drawable.ic_radio_button_normal);
        }

        holder.relativeLayoutCustomizeOptionItem.setTag(position);
        holder.relativeLayoutCustomizeOptionItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    int pos = (int) v.getTag();
                    CustomizeOption customizeOption = customizeOptions.get(pos);
                    if(optionSelector == 1){
                        if(callback.getCustomizeItemSelected().getCustomizeOptions().contains(customizeOption.getCustomizeOptionId())){
                            callback.getCustomizeItemSelected().getCustomizeOptions().remove(customizeOption.getCustomizeOptionId());
                        } else{
                            callback.getCustomizeItemSelected().getCustomizeOptions().add(customizeOption.getCustomizeOptionId());
                        }
                    } else {
                        if(callback.getCustomizeItemSelected().getCustomizeOptions().contains(customizeOption.getCustomizeOptionId())){
                            callback.getCustomizeItemSelected().getCustomizeOptions().remove(customizeOption.getCustomizeOptionId());
                        } else{
                            callback.getCustomizeItemSelected().getCustomizeOptions().clear();
                            callback.getCustomizeItemSelected().getCustomizeOptions().add(customizeOption.getCustomizeOptionId());
                        }
                    }
                    notifyDataSetChanged();
                } catch (Exception e){}
            }
        });

    }

    @Override
    public int getItemCount() {
        return customizeOptions == null ? 0 : customizeOptions.size();
    }


    static class MainViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout relativeLayoutCustomizeOptionItem;
        private ImageView imageViewCustomizeOptionItem;
        private View imageViewSaperator;
        public TextView textViewCustomizeOptionItemName, textViewimageViewCustomizeOptionItemPrice;

        public MainViewHolder(View itemView, Context context) {
            super(itemView);

            relativeLayoutCustomizeOptionItem = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutCustomizeOptionItem);
            imageViewCustomizeOptionItem = (ImageView) itemView.findViewById(R.id.imageViewCustomizeOptionItem);
            imageViewSaperator = (View) itemView.findViewById(R.id.viewSaperator);
            textViewCustomizeOptionItemName = (TextView) itemView.findViewById(R.id.textViewCustomizeOptionItemName);textViewCustomizeOptionItemName.setTypeface(Fonts.mavenRegular(context));
            textViewimageViewCustomizeOptionItemPrice = (TextView) itemView.findViewById(R.id.textViewimageViewCustomizeOptionItemPrice);textViewimageViewCustomizeOptionItemPrice.setTypeface(Fonts.mavenRegular(context));
        }
    }


    public interface Callback{
        CustomizeItemSelected getCustomizeItemSelected();
    }

}
