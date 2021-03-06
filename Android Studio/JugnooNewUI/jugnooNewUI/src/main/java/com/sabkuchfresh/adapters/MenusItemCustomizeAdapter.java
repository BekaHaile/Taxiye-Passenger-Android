package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.dialogs.ReviewImagePagerDialog;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.CustomizeItem;
import com.sabkuchfresh.retrofit.model.menus.CustomizeItemSelected;
import com.sabkuchfresh.retrofit.model.menus.CustomizeOption;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Shankar on 7/17/15.
 */
public class MenusItemCustomizeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Activity context;
    private Item item;
    private ItemSelected itemSelected;
    private ArrayList<CustomizeOption> customizeOptions;
    private Callback callback;
    private boolean showSpecialInstructions;
    private String instructions;
    private String currencyCode, currency;
    private SparseIntArray mapItemOptionsAdded = new SparseIntArray();

    private static final int ITEM = 0;
    private static final int CUSTOMIZE_ITEM = 1;
    private static final int CUSTOMIZE_OPTION = 2;
    private static final int SPECIAL_INSTRUCTIONS = 3;

    public MenusItemCustomizeAdapter(Activity context, Item item, Callback callback, String currencyCode, String currency,boolean showSpecialInstructions) {
        this.context = context;
        this.callback = callback;
        customizeOptions = new ArrayList<>();
        this.showSpecialInstructions = showSpecialInstructions;
        this.currencyCode = currencyCode;
        this.currency = currency;
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
            mapItemOptionsAdded.put(customizeItem.getCustomizeId(), 0);

            CustomizeItemSelected customizeItemSelected = null;
            if(customizeItem.getIsCheckBox() == 0 || customizeItem.getCustomizeItemLowerLimit() > 0){
                customizeItemSelected = new CustomizeItemSelected(customizeItem.getCustomizeId(),
                        customizeItem.getCustomizeItemName(),
                        customizeItem.getCustomizeItemLowerLimit(), customizeItem.getCustomizeItemUpperLimit());
            }

            for(CustomizeOption customizeOption : customizeItem.getCustomizeOptions()){
                customizeOption.setIsMultiSelect(customizeItem.getIsCheckBox());
                customizeOption.setCustomizeItemPos(i);
                customizeOptions.add(customizeOption);
                if(customizeItemSelected != null){
                    double optionPrice = 0d;
                    if(customizeItem.getIsCheckBox() == 0 && customizeItemSelected.getCustomizeOptions().size() == 0){
                        customizeItemSelected.getCustomizeOptions().add(customizeOption.getCustomizeOptionId());
                        optionPrice = customizeOption.getCustomizePrice();
                    }
                    totalPrice = totalPrice + optionPrice;
                }
            }

            if(customizeItemSelected != null){
                itemSelected.getCustomizeItemSelectedList().add(customizeItemSelected);
                mapItemOptionsAdded.put(customizeItem.getCustomizeId(), 0);
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
        } else if (viewType == SPECIAL_INSTRUCTIONS) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_customize_special_instructions, parent, false);
            return new SpecialInstructionsViewHolder(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }



    @Override
    public int getItemViewType(int position) {
        if (position == customizeOptions.size()) {
            return SPECIAL_INSTRUCTIONS;
        } else if(customizeOptions.get(position).getIsItem() == 1){
            return ITEM;
        } else if(customizeOptions.get(position).getIsCustomizeItem() == 1){
            return CUSTOMIZE_ITEM;
        } else {
            return CUSTOMIZE_OPTION;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SpecialInstructionsViewHolder) {
            ((SpecialInstructionsViewHolder) (holder)).etInstructions.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    instructions = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }

            });
        } else {
            CustomizeOption customizeOption = customizeOptions.get(position);
            if (holder instanceof ViewHolderItem) {
                ViewHolderItem mHolder = (ViewHolderItem) holder;
                mHolder.imageViewFoodType.setImageResource(item.getIsVeg() == 1 ? R.drawable.veg : R.drawable.nonveg);
                mHolder.imageViewFoodType.setVisibility(item.showFoodType() ? View.VISIBLE : View.GONE);
                mHolder.textViewItemCategoryName.setText(item.getItemName());
                mHolder.textViewItemCategoryName.setMinimumHeight(((int) (ASSL.Yscale() * 70f)));

            int total = itemSelected.getQuantity();
            mHolder.textViewQuantity.setText(String.valueOf(total));
            mHolder.imageViewPlus.setImageResource(R.drawable.ic_plus_dark_selector);
            mHolder.linearLayoutQuantitySelector.setVisibility(View.VISIBLE);
            if (total == 0) {
                mHolder.imageViewPlus.setImageResource(R.drawable.ic_plus_theme_selector);
                mHolder.imageViewMinus.setVisibility(View.GONE);
                mHolder.textViewQuantity.setVisibility(View.GONE);
            } else {
                mHolder.imageViewMinus.setVisibility(View.VISIBLE);
                mHolder.textViewQuantity.setVisibility(View.VISIBLE);
            }

            mHolder.textViewAboutItemDescription.setText(item.getItemDetails());
            int gravity, visibilityDesc;
//            RelativeLayout.LayoutParams paramsFT = (RelativeLayout.LayoutParams) mHolder.imageViewFoodType.getLayoutParams();
            if(!TextUtils.isEmpty(item.getItemDetails())){
//                gravity = Gravity.START;
                visibilityDesc = View.VISIBLE;
//                paramsFT.setMargins(paramsFT.leftMargin, (int)(ASSL.Yscale() * 30f), paramsFT.rightMargin, paramsFT.bottomMargin);
            } else {
//                gravity = Gravity.CENTER_VERTICAL;
                visibilityDesc = View.GONE;
//                paramsFT.setMargins(paramsFT.leftMargin, (int)(ASSL.Yscale() * 45f), paramsFT.rightMargin, paramsFT.bottomMargin);
            }
            mHolder.textViewAboutItemDescription.setVisibility(visibilityDesc);
//            mHolder.textViewItemCategoryName.setGravity(gravity);
//            mHolder.imageViewFoodType.setLayoutParams(paramsFT);

            if(context instanceof FreshActivity
                    && ((FreshActivity)context).getVendorOpened() != null
                    && (1 == ((FreshActivity)context).getVendorOpened().getIsClosed() || 0 == ((FreshActivity)context).getVendorOpened().getIsAvailable())){
                mHolder.linearLayoutQuantitySelector.setVisibility(View.GONE);
            }
            try {
                if(!TextUtils.isEmpty(item.getItemImage()) || !TextUtils.isEmpty(item.getItemImageCompressed())){
                    mHolder.ivItemImage.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(!TextUtils.isEmpty(item.getItemImageCompressed())?item.getItemImageCompressed():item.getItemImage())
                            .placeholder(R.drawable.ic_fresh_item_placeholder)
                            .fit()
                            .centerCrop()
                            .error(R.drawable.ic_fresh_item_placeholder)
                            .into(mHolder.ivItemImage);
                } else {
                    mHolder.ivItemImage.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mHolder.ivItemImage.setVisibility(View.GONE);
            }
           /* RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mHolder.imageViewFoodType.getLayoutParams();
            params.setMargins((mHolder.ivItemImage.getVisibility() == View.VISIBLE ?
                            context.getResources().getDimensionPixelSize(R.dimen.dp_6) : context.getResources().getDimensionPixelSize(R.dimen.dp_14)),
                    params.topMargin, params.rightMargin, params.bottomMargin);
            mHolder.imageViewFoodType.setLayoutParams(params);*/


            mHolder.imageViewMinus.setTag(position);
            mHolder.imageViewPlus.setTag(position);

            View.OnClickListener plusClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        if(item.getTotalQuantity() + itemSelected.getQuantity() < 50) {
                            itemSelected.setQuantity(itemSelected.getQuantity() + 1);
                            notifyDataSetChanged();
                            callback.updateItemTotalPrice(itemSelected);
                            callback.onItemPlusClick();
                        } else {
                            Utils.showToast(context, context.getString(R.string.order_quantity_limited));
                        }

                    } catch (Exception e){}
                }
            };

                mHolder.imageViewPlus.setOnClickListener(plusClick);
                mHolder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (itemSelected.getQuantity() > 1) {
                                itemSelected.setQuantity(itemSelected.getQuantity() - 1);
                                notifyDataSetChanged();
                                callback.updateItemTotalPrice(itemSelected);
                                callback.onItemMinusClick(false);
                            } else {
                                callback.onItemMinusClick(true);
                            }
                        } catch (Exception e) {
                        }
                    }
            });
            mHolder.ivItemImage.setTag(position);
            mHolder.ivItemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {



                        if(item!=null && !TextUtils.isEmpty(item.getItemImage())){

                            ReviewImagePagerDialog dialog = ReviewImagePagerDialog.newInstance(0, item.getItemImage());
                            dialog.show(context.getFragmentManager(), ReviewImagePagerDialog.class.getSimpleName());

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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
            String toAppend = "";
            int lower = customizeItem.getCustomizeItemLowerLimit();
            int upper = customizeItem.getCustomizeItemUpperLimit();

            if (lower > 0 && upper < Integer.MAX_VALUE) toAppend = context.getString(R.string.select_between_lower_upper, lower, upper);
            else if (lower == 0 && upper < Integer.MAX_VALUE) toAppend = context.getString(R.string.select_atmost_upper, upper);
            else if (lower > 0 && upper == Integer.MAX_VALUE) toAppend = context.getString(R.string.select_atleast_lower, lower);
            else if (customizeItem.getIsCheckBox() == 1) toAppend = context.getString(R.string.optional_bracket);
            else toAppend = context.getString(R.string.required_bracket);
//            mHolder.textViewSubCategoryName.append(toAppend);
            mHolder.textViewSubCategoryName.append(customizeItem.getIsCheckBox() == 1 ? toAppend : context.getString(R.string.required_bracket));


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
                bottomMargin = (int)(-8.0f*ASSL.Yscale());
            }
            layoutParams.setMargins((int) (25.0f*ASSL.Xscale()), topMargin, (int)(25.0f*ASSL.Xscale()), bottomMargin);
            layoutParams.setMarginStart((int) (25.0f*ASSL.Xscale()));
            layoutParams.setMarginEnd((int)(25.0f*ASSL.Xscale()));
            mHolder.cvRoot.setLayoutParams(layoutParams);

            mHolder.tvCustomizeOptionItemName.setText(customizeOption.getCustomizeOptionName());

            if(customizeOption.getCustomizePrice() > 0){

                mHolder.tvCustomizeOptionItemPrice.setText(com.sabkuchfresh.utils.Utils.formatCurrencyAmount(customizeOption.getCustomizePrice(), currencyCode, currency));
            } else {
                mHolder.tvCustomizeOptionItemPrice.setText("");
            }


            if(item.getCustomizeItemSelected(getCustomizeItem(customizeOption), false, itemSelected).getCustomizeOptions().contains(customizeOption.getCustomizeOptionId())){
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
                        CustomizeItemSelected customizeItemSelected = item.getCustomizeItemSelected(customizeItem, true, itemSelected);
                        if(customizeOption.getIsMultiSelect() == 1){

                            int itemAdded = mapItemOptionsAdded.get(customizeItem.getCustomizeId());
                            Integer customizeUpperItemLimit = customizeItem.getCustomizeItemUpperLimit();
                            Integer customizeLowerItemLimit = 0;

                            if (customizeItem.getCustomizeItemLowerLimit() < customizeUpperItemLimit) {
                                customizeLowerItemLimit = customizeItem.getCustomizeItemLowerLimit();
                            }

                            if(customizeItemSelected.getCustomizeOptions().contains(customizeOption.getCustomizeOptionId())){
                                // being removed

                                // check for lower limit
                                if (customizeLowerItemLimit == 0 || itemAdded - 1 >= customizeLowerItemLimit) {
                                    customizeItemSelected.getCustomizeOptions().remove(customizeOption.getCustomizeOptionId());
                                    mapItemOptionsAdded.put(customizeItem.getCustomizeId(), --itemAdded);
                                } else {
                                    Utils.showToast(context,
                                            context.getString(R.string.error_customization_lower_limit,
                                                    customizeItem.getCustomizeItemLowerLimit(),
                                                    customizeItem.getCustomizeItemName()));
                                    return;
                                }

                            } else{
                                // being added
                                if (customizeUpperItemLimit != 0) {
                                    if(itemAdded >= customizeUpperItemLimit) {
                                        Utils.showToast(context,
                                                context.getString(R.string.error_customization_limit,
                                                        customizeItem.getCustomizeItemUpperLimit(),
                                                        customizeItem.getCustomizeItemName()));
                                        return;
                                    } else {
                                        mapItemOptionsAdded.put(customizeItem.getCustomizeId(), ++itemAdded);
                                    }
                                }

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
	}

    @Override
    public int getItemCount() {
        if (customizeOptions == null) return 0;
        else if (showSpecialInstructions) return customizeOptions.size() + 1;
        else return customizeOptions.size();
    }

//    private CustomizeItemSelected getCustomizeItemSelected(CustomizeItem customizeItem, boolean addSelected){
//        CustomizeItemSelected customizeItemSelected = new CustomizeItemSelected(customizeItem.getCustomizeId());
//        int index = itemSelected.getCustomizeItemSelectedList().indexOf(customizeItemSelected);
//        if(index > -1){
//            customizeItemSelected = itemSelected.getCustomizeItemSelectedList().get(index);
//        } else if(addSelected) {
//            itemSelected.getCustomizeItemSelectedList().add(customizeItemSelected);
//        }
//        return customizeItemSelected;
//    }

    private CustomizeItem getCustomizeItem(CustomizeOption customizeOption){
        return item.getCustomizeItem().get(customizeOption.getCustomizeItemPos());
    }

    private void updateItemSelectedTotalPrice(){
//        double totalPrice = item.getPrice();
//        for(CustomizeItem customizeItem : item.getCustomizeItem()) {
//            CustomizeItemSelected customizeItemSelected = getCustomizeItemSelected(customizeItem, false);
//            for(CustomizeOption customizeOption : customizeItem.getCustomizeOptions()){
//                if(customizeItemSelected.getCustomizeOptions().contains(customizeOption.getCustomizeOptionId())){
//                    totalPrice = totalPrice + customizeOption.getCustomizePrice();
//                }
//            }
//        }
//        itemSelected.setTotalPrice(totalPrice);
        itemSelected.setTotalPrice(item.getCustomizeItemsSelectedTotalPriceForItemSelected(itemSelected));
        callback.updateItemTotalPrice(itemSelected);
    }


    class ViewHolderItem extends RecyclerView.ViewHolder {

        public RelativeLayout relativeLayoutItem ;
        public LinearLayout linearLayoutQuantitySelector;
        private ImageView ivItemImage, imageViewFoodType, saperatorImage, imageViewMinus, imageViewPlus;
        public TextView textViewItemCategoryName, textViewAboutItemDescription, textViewQuantity;

        public ViewHolderItem(View itemView, Context context) {
            super(itemView);
            relativeLayoutItem = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutItem);
            linearLayoutQuantitySelector = (LinearLayout) itemView.findViewById(R.id.linearLayoutQuantitySelector);
            ivItemImage = (ImageView) itemView.findViewById(R.id.ivItemImage);
            imageViewFoodType = (ImageView) itemView.findViewById(R.id.imageViewFoodType);
            saperatorImage = (ImageView) itemView.findViewById(R.id.saperatorImage);
            saperatorImage.setVisibility(View.GONE);
            imageViewMinus = (ImageView) itemView.findViewById(R.id.imageViewMinus);
            imageViewPlus = (ImageView) itemView.findViewById(R.id.imageViewPlus);

            textViewQuantity = (TextView)itemView.findViewById(R.id.textViewQuantity); textViewQuantity.setTypeface(Fonts.mavenMedium(context));
            textViewItemCategoryName = (TextView)itemView.findViewById(R.id.textViewItemCategoryName); textViewItemCategoryName.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);
            textViewAboutItemDescription = (TextView)itemView.findViewById(R.id.textViewAboutItemDescription); textViewAboutItemDescription.setTypeface(Fonts.mavenMedium(context));
            textViewItemCategoryName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
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

    class SpecialInstructionsViewHolder extends RecyclerView.ViewHolder {
        private AppCompatEditText etInstructions;
        private AppCompatEditText tvLabelInstructions;

        SpecialInstructionsViewHolder(View itemView) {
            super(itemView);
            etInstructions = itemView.findViewById(R.id.etInstructions);
            tvLabelInstructions = itemView.findViewById(R.id.tvLabelInstructions);

            etInstructions.setTypeface(Fonts.mavenRegular(context));
            tvLabelInstructions.setTypeface(Fonts.mavenRegular(context));
        }
    }

    public ItemSelected getItemSelected() {
        itemSelected.setItemInstructions(instructions);
        return itemSelected;
    }

    public Item getItem() {
        return item;
    }

    public interface Callback{
        void updateItemTotalPrice(ItemSelected itemSelected);
        void onItemMinusClick(boolean allItemsFinished);
        void onItemPlusClick();
    }

    public boolean validateItemQuantityLimit() {
        boolean isValid = true;

        for (CustomizeItemSelected item: itemSelected.getCustomizeItemSelectedList()) {
            if(item.getCustomizeOptions().size()<1) {
                int quantityAdded = mapItemOptionsAdded.get(item.getCustomizeId());

                if (item.getLowerLimit() != 0 && quantityAdded < item.getLowerLimit()) {
                    Utils.showToast(context,
                            context.getString(R.string.error_customization_lower_limit,
                                    item.getLowerLimit(),
                                    item.getName()));
                    isValid = false;
                    break;
                } else if (quantityAdded > item.getUpperLimit()) {
                    Utils.showToast(context,
                            context.getString(R.string.error_customization_limit,
                                    item.getUpperLimit(),
                                    item.getName()));
                    isValid = false;

                }
            }
        }

        return isValid;
    }

}
