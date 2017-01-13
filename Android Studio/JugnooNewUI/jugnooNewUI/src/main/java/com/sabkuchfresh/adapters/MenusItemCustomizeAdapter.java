package com.sabkuchfresh.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.CustomizeItem;
import com.sabkuchfresh.retrofit.model.menus.CustomizeItemSelected;
import com.sabkuchfresh.retrofit.model.menus.CustomizeOption;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Shankar on 7/17/15.
 */
public class MenusItemCustomizeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FlurryEventNames {

    private Context context;
    private Item item;
    private ItemSelected itemSelected;
    private ArrayList<CustomizeOption> customizeOptions;
    private Callback callback;

    private static final int ITEM = 0;
    private static final int CUSTOMIZE_ITEM = 1;
    private static final int CUSTOMIZE_OPTION = 2;

    public MenusItemCustomizeAdapter(Context context, Item item, Callback callback) {
        this.context = context;
        this.callback = callback;
        customizeOptions = new ArrayList<>();
        setList(item);
    }


    public void setList(Item item){
        this.item = item;
        itemSelected = new ItemSelected();
        itemSelected.setRestaurantItemId(item.getRestaurantItemId());
        itemSelected.setQuantity(1);

        customizeOptions.clear();
        CustomizeOption coItem = new CustomizeOption();
        coItem.setIsItem(1);
        customizeOptions.add(coItem);
        double totalPrice = item.getPrice();
        for(int i=0; i<item.getCustomizeItem().size(); i++){
            CustomizeItem customizeItem = item.getCustomizeItem().get(i);
            CustomizeOption coCustomizeItem = new CustomizeOption();
            coCustomizeItem.setIsCustomizeItem(1);
            coCustomizeItem.setCustomizeItemPos(i);
            customizeOptions.add(coCustomizeItem);

            CustomizeItemSelected customizeItemSelected = null;
            if(customizeItem.getIsCheckBox() == 0){
                customizeItemSelected = new CustomizeItemSelected(customizeItem.getCustomizeId());
            }

            for(CustomizeOption customizeOption : customizeItem.getCustomizeOptions()){
                customizeOption.setIsMultiSelect(customizeItem.getIsCheckBox());
                customizeOption.setCustomizeItemPos(i);
                customizeOptions.add(customizeOption);
                if(customizeItemSelected != null){
                    double optionPrice = 0d;
                    if(customizeItem.getIsCheckBox() == 0 && customizeOption.getIsDefault() == 1
                            && customizeItemSelected.getCustomizeOptions().size() == 0){
                        customizeItemSelected.getCustomizeOptions().add(customizeOption.getCustomizeOptionId());
                        optionPrice = customizeOption.getCustomizePrice();
                    } else if(customizeItem.getIsCheckBox() == 1 && customizeOption.getIsDefault() == 1){
                        customizeItemSelected.getCustomizeOptions().add(customizeOption.getCustomizeOptionId());
                        optionPrice = customizeOption.getCustomizePrice();
                    }
                    totalPrice = totalPrice + optionPrice;
                }
            }

            if(customizeItemSelected != null){
                itemSelected.getCustomizeItemSelectedList().add(customizeItemSelected);
            }
        }
        itemSelected.setTotalPrice(totalPrice);
        callback.updateItemTotalPrice(itemSelected);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menus_item, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderItem(v, context);
        } else if (viewType == CUSTOMIZE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menus_subcategory, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderCustomizeItem(v, context);
        } else if (viewType == CUSTOMIZE_OPTION) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_customize_option_item, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderCustomizeOption(v, context);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }



    @Override
    public int getItemViewType(int position) {
        if(customizeOptions.get(position).getIsItem() == 1){
            return ITEM;
        } else if(customizeOptions.get(position).getIsCustomizeItem() == 1){
            return CUSTOMIZE_ITEM;
        } else {
            return CUSTOMIZE_OPTION;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CustomizeOption customizeOption = customizeOptions.get(position);
        if(holder instanceof ViewHolderItem) {
            ViewHolderItem mHolder = (ViewHolderItem) holder;
            mHolder.imageViewFoodType.setImageResource(item.getIsVeg() == 1 ? R.drawable.veg : R.drawable.nonveg);
            mHolder.textViewItemCategoryName.setText(item.getItemName());

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mHolder.cardViewRecycler.getLayoutParams();
            layoutParams.setMargins((int) (25.0f*ASSL.Xscale()), (int) (25.0f*ASSL.Yscale()), (int)(25.0f*ASSL.Xscale()), (int) (6.0f*ASSL.Yscale()));
            mHolder.cardViewRecycler.setLayoutParams(layoutParams);

            if(itemSelected.getQuantity() > 0){
                mHolder.linearLayoutQuantitySelector.setVisibility(View.VISIBLE);
                mHolder.addButton.setVisibility(View.GONE);
                mHolder.textViewQuantity.setText(String.valueOf(itemSelected.getQuantity()));
            } else {
                mHolder.linearLayoutQuantitySelector.setVisibility(View.GONE);
                mHolder.addButton.setVisibility(View.VISIBLE);
            }

            mHolder.textViewAboutItemDescription.setVisibility(item.getItemDetails() != null ? View.VISIBLE : View.GONE);
            mHolder.textViewAboutItemDescription.setText(item.getItemDetails());
            mHolder.textViewAboutItemDescription.setMaxLines(2);
            mHolder.textViewAboutItemDescription.setEllipsize(TextUtils.TruncateAt.END);


            mHolder.relativeLayoutQuantitySel.setVisibility(View.VISIBLE);
            if(context instanceof FreshActivity
                    && ((FreshActivity)context).getVendorOpened() != null
                    && (1 == ((FreshActivity)context).getVendorOpened().getIsClosed() || 0 == ((FreshActivity)context).getVendorOpened().getIsAvailable())){
                mHolder.relativeLayoutQuantitySel.setVisibility(View.GONE);
            }


            mHolder.textViewMinus.setTag(position);
            mHolder.textViewPlus.setTag(position);
            mHolder.addButton.setTag(position);

            View.OnClickListener plusClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        if(item.getTotalQuantity() + itemSelected.getQuantity() < 50) {
                            itemSelected.setQuantity(itemSelected.getQuantity() + 1);
                            notifyDataSetChanged();
                            callback.updateItemTotalPrice(itemSelected);
                        } else {
                            Utils.showToast(context, context.getString(R.string.cannot_add_more_than_50));
                        }
                    } catch (Exception e){}
                }
            };

            mHolder.addButton.setOnClickListener(plusClick);
            mHolder.textViewPlus.setOnClickListener(plusClick);
            mHolder.textViewMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        if(itemSelected.getQuantity() > 1) {
                            itemSelected.setQuantity(itemSelected.getQuantity() - 1);
                            notifyDataSetChanged();
                            callback.updateItemTotalPrice(itemSelected);
                        }
                    } catch (Exception e){}
                }
            });


        } else if(holder instanceof ViewHolderCustomizeItem) {
            ViewHolderCustomizeItem mHolder = ((ViewHolderCustomizeItem) holder);
            CustomizeItem customizeItem = getCustomizeItem(customizeOption);
            mHolder.textViewSubCategoryName.setText("");
            final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
            final SpannableStringBuilder sb = new SpannableStringBuilder(customizeItem.getCustomizeItemName().toUpperCase());
            sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mHolder.textViewSubCategoryName.append(sb);
            mHolder.textViewSubCategoryName.append(" ");
            mHolder.textViewSubCategoryName.append(customizeItem.getIsCheckBox() == 1 ? context.getString(R.string.optional_bracket) : context.getString(R.string.required_bracket));

        } else if(holder instanceof ViewHolderCustomizeOption) {
            ViewHolderCustomizeOption mHolder = ((ViewHolderCustomizeOption) holder);

            CustomizeOption itemUp = customizeOptions.get(position - 1);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mHolder.cvRoot.getLayoutParams();
            int topMargin, bottomMargin;
            if(itemUp.getIsCustomizeItem() == 1){
                topMargin = (int)(6.0f*ASSL.Yscale());
            } else{
                topMargin = (int)(-6.0f*ASSL.Yscale());
            }

            CustomizeOption itemDown = (position < (customizeOptions.size()-1)) ? customizeOptions.get(position + 1) : null;
            if(itemDown == null){
                mHolder.vSep.setVisibility(View.GONE);
                bottomMargin = (int)(25.0f*ASSL.Yscale());
            } else if(itemDown.getIsCustomizeItem() == 1){
                mHolder.vSep.setVisibility(View.GONE);
                bottomMargin = (int)(6.0f*ASSL.Yscale());
            } else{
                mHolder.vSep.setVisibility(View.VISIBLE);
                bottomMargin = (int)(-6.0f*ASSL.Yscale());
            }
            layoutParams.setMargins((int) (25.0f*ASSL.Xscale()), topMargin, (int)(25.0f*ASSL.Xscale()), bottomMargin);
            mHolder.cvRoot.setLayoutParams(layoutParams);

            mHolder.tvCustomizeOptionItemName.setText(customizeOption.getCustomizeOptionName());

            mHolder.tvCustomizeOptionItemPrice.setText(context.getString(R.string.rupees_value_format,
                    Utils.getMoneyDecimalFormat().format(customizeOption.getCustomizePrice())));

            if(getCustomizeItemSelected(getCustomizeItem(customizeOption), false).getCustomizeOptions().contains(customizeOption.getCustomizeOptionId())){
                mHolder.ivCustomizeOptionItem.setImageResource(customizeOption.getIsMultiSelect() == 1 ? R.drawable.checkbox_signup_checked : R.drawable.ic_radio_button_selected);
            } else {
                mHolder.ivCustomizeOptionItem.setImageResource(customizeOption.getIsMultiSelect() == 1 ? R.drawable.check_box_unchecked : R.drawable.ic_radio_button_normal);
            }

            mHolder.cvRoot.setTag(position);
            mHolder.cvRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        int pos = (int) v.getTag();
                        CustomizeOption customizeOption = customizeOptions.get(pos);
                        CustomizeItem customizeItem = getCustomizeItem(customizeOption);
                        CustomizeItemSelected customizeItemSelected = getCustomizeItemSelected(customizeItem, true);
                        if(customizeOption.getIsMultiSelect() == 1){
                            if(customizeItemSelected.getCustomizeOptions().contains(customizeOption.getCustomizeOptionId())){
                                customizeItemSelected.getCustomizeOptions().remove(customizeOption.getCustomizeOptionId());
                            } else{
                                customizeItemSelected.getCustomizeOptions().add(customizeOption.getCustomizeOptionId());
                            }
                        } else {
                            if(customizeItemSelected.getCustomizeOptions().contains(customizeOption.getCustomizeOptionId())){
//                                customizeItemSelected.getCustomizeOptions().remove(customizeOption.getCustomizeOptionId());
                            } else{
                                customizeItemSelected.getCustomizeOptions().clear();
                                customizeItemSelected.getCustomizeOptions().add(customizeOption.getCustomizeOptionId());
                            }
                        }
                        updateItemSelectedTotalPrice();
                        notifyDataSetChanged();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }
	}

    @Override
    public int getItemCount() {
        return customizeOptions == null ? 0 : customizeOptions.size();
    }

    private CustomizeItemSelected getCustomizeItemSelected(CustomizeItem customizeItem, boolean addSelected){
        CustomizeItemSelected customizeItemSelected = new CustomizeItemSelected(customizeItem.getCustomizeId());
        int index = itemSelected.getCustomizeItemSelectedList().indexOf(customizeItemSelected);
        if(index > -1){
            customizeItemSelected = itemSelected.getCustomizeItemSelectedList().get(index);
        } else if(addSelected) {
            itemSelected.getCustomizeItemSelectedList().add(customizeItemSelected);
        }
        return customizeItemSelected;
    }

    private CustomizeItem getCustomizeItem(CustomizeOption customizeOption){
        return item.getCustomizeItem().get(customizeOption.getCustomizeItemPos());
    }

    private void updateItemSelectedTotalPrice(){
        double totalPrice = item.getPrice();
        for(CustomizeItem customizeItem : item.getCustomizeItem()) {
            CustomizeItemSelected customizeItemSelected = getCustomizeItemSelected(customizeItem, false);
            for(CustomizeOption customizeOption : customizeItem.getCustomizeOptions()){
                if(customizeItemSelected.getCustomizeOptions().contains(customizeOption.getCustomizeOptionId())){
                    totalPrice = totalPrice + customizeOption.getCustomizePrice();
                }
            }
        }
        itemSelected.setTotalPrice(totalPrice);
        callback.updateItemTotalPrice(itemSelected);
    }


    class ViewHolderItem extends RecyclerView.ViewHolder {

        public CardView cardViewRecycler;
        public RelativeLayout relativeLayoutItem, relativeLayoutQuantitySel ;
        public LinearLayout linearLayoutQuantitySelector;
        private ImageView imageViewFoodType, saperatorImage;
        public TextView textViewItemCategoryName, textViewAboutItemDescription, textViewQuantity, textViewMinus, textViewPlus;
        public Button addButton;

        public ViewHolderItem(View itemView, Context context) {
            super(itemView);
            cardViewRecycler = (CardView) itemView.findViewById(R.id.cvRoot);
            relativeLayoutItem = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutItem);
            relativeLayoutQuantitySel = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutQuantitySel);
            linearLayoutQuantitySelector = (LinearLayout) itemView.findViewById(R.id.linearLayoutQuantitySelector);
            imageViewFoodType = (ImageView) itemView.findViewById(R.id.imageViewFoodType);
            saperatorImage = (ImageView) itemView.findViewById(R.id.saperatorImage);
            saperatorImage.setVisibility(View.GONE);
            textViewMinus = (TextView) itemView.findViewById(R.id.textViewMinus);
            textViewPlus = (TextView) itemView.findViewById(R.id.textViewPlus);

            textViewQuantity = (TextView)itemView.findViewById(R.id.textViewQuantity); textViewQuantity.setTypeface(Fonts.mavenRegular(context));
            textViewItemCategoryName = (TextView)itemView.findViewById(R.id.textViewItemCategoryName); textViewItemCategoryName.setTypeface(Fonts.mavenRegular(context));
            textViewAboutItemDescription = (TextView)itemView.findViewById(R.id.textViewAboutItemDescription); textViewAboutItemDescription.setTypeface(Fonts.mavenRegular(context));

            addButton = (Button) itemView.findViewById(R.id.add_button);
        }
    }

    class ViewHolderCustomizeItem extends RecyclerView.ViewHolder {

        public TextView textViewSubCategoryName;
        public ViewHolderCustomizeItem(View itemView, Context context) {
            super(itemView);
            textViewSubCategoryName = (TextView) itemView.findViewById(R.id.tvSubCategoryName);textViewSubCategoryName.setTypeface(Fonts.mavenRegular(context));
        }
    }

    class ViewHolderCustomizeOption extends RecyclerView.ViewHolder {

        public CardView cvRoot;
        private ImageView ivCustomizeOptionItem;
        private View vSep;
        public TextView tvCustomizeOptionItemName, tvCustomizeOptionItemPrice;

        public ViewHolderCustomizeOption(View itemView, Context context) {
            super(itemView);
            cvRoot = (CardView) itemView.findViewById(R.id.cvRoot);
            ivCustomizeOptionItem = (ImageView) itemView.findViewById(R.id.ivCustomizeOptionItem);
            vSep = itemView.findViewById(R.id.vSep);
            tvCustomizeOptionItemName = (TextView) itemView.findViewById(R.id.tvCustomizeOptionItemName);
            tvCustomizeOptionItemName.setTypeface(Fonts.mavenMedium(context));
            tvCustomizeOptionItemPrice = (TextView) itemView.findViewById(R.id.tvCustomizeOptionItemPrice);
            tvCustomizeOptionItemPrice.setTypeface(Fonts.mavenMedium(context));
        }
    }

    public ItemSelected getItemSelected() {
        return itemSelected;
    }

    public Item getItem() {
        return item;
    }

    public interface Callback{
        void updateItemTotalPrice(ItemSelected itemSelected);
    }

}
