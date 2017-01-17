package com.sabkuchfresh.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.Category;
import com.sabkuchfresh.retrofit.model.menus.Item;
import com.sabkuchfresh.retrofit.model.menus.ItemSelected;
import com.sabkuchfresh.retrofit.model.menus.Subcategory;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Shankar on 7/17/15.
 */
public class MenusCategoryItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FlurryEventNames {

    private Context context;
    private List<Item> subItems;
    private int categoryPos;
    private Callback callback;

    private static final int MAIN_ITEM = 0;
    private static final int BLANK_ITEM = 1;
    private static final int SUB_CATEGORY_ITEM = 2;

    public MenusCategoryItemsAdapter(Context context, int categoryPos, Category category, Callback callback) {
        this.context = context;
        this.callback = callback;
        this.categoryPos = categoryPos;
        setSubItems(category);
    }

    private void setSubItems(Category category){
        subItems = new ArrayList<>();
        if(category.getSubcategories() != null){
            List<Subcategory> subcategories = category.getSubcategories();
            for(int i=0; i<subcategories.size(); i++){
                Subcategory subcategory = subcategories.get(i);
                Item item = new Item();
                item.setItemName(subcategory.getSubcategoryName());
                item.setIsSubCategory(1);
                subItems.add(item);
                for(int j=0; j<subcategory.getItems().size(); j++){
                    Item item1 = subcategory.getItems().get(j);
                    item1.setSubCategoryPos(i);
                    item1.setItemPos(j);
                    subItems.add(item1);
                }
            }
        } else if(category.getItems() != null){
            List<Item> items = category.getItems();
            for(int j=0; j<items.size(); j++){
                Item item1 = items.get(j);
                item1.setSubCategoryPos(-1);
                item1.setItemPos(j);
                subItems.add(item1);
            }
        }
    }


    public MenusCategoryItemsAdapter(Context context, ArrayList<Item> items, Callback callback) {
        this.context = context;
        this.callback = callback;
        this.categoryPos = -1;
        subItems = items;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SUB_CATEGORY_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menus_subcategory, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new SubCategoryViewHolder(v, context);
        } else if (viewType == MAIN_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menus_item, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new MainViewHolder(v, context);
        } else if (viewType == BLANK_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 194);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewHolderBlank(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }



    @Override
    public int getItemViewType(int position) {
        if(position == subItems.size()) {
            return BLANK_ITEM;
        } else if(subItems.get(position).getIsSubCategory() == 1){
            return SUB_CATEGORY_ITEM;
        } else {
            return MAIN_ITEM;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MainViewHolder) {
            MainViewHolder mHolder = ((MainViewHolder) holder);
            Item item = subItems.get(position);

            Item itemUp = position == 0 ? null : subItems.get(position - 1);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mHolder.cardViewRecycler.getLayoutParams();
            int topMargin, bottomMargin;
            if(itemUp == null){
                topMargin = (int)(25.0f*ASSL.Yscale());
            } else if(itemUp.getIsSubCategory() == 1){
                topMargin = (int)(6.0f*ASSL.Yscale());
            } else{
                topMargin = (int)(-6.0f*ASSL.Yscale());
            }

            Item itemDown = (position < (subItems.size()-1)) ? subItems.get(position + 1) : null;
            if(itemDown == null){
                mHolder.saperatorImage.setVisibility(View.GONE);
                bottomMargin = (int)(25.0f*ASSL.Yscale());
            } else if(itemDown.getIsSubCategory() == 1){
                mHolder.saperatorImage.setVisibility(View.GONE);
                bottomMargin = (int)(6.0f*ASSL.Yscale());
            } else{
                mHolder.saperatorImage.setVisibility(View.VISIBLE);
                bottomMargin = (int)(-8.0f*ASSL.Yscale());
            }
            layoutParams.setMargins((int) (25.0f*ASSL.Xscale()), topMargin, (int)(25.0f*ASSL.Xscale()), bottomMargin);
            mHolder.cardViewRecycler.setLayoutParams(layoutParams);

            mHolder.imageViewFoodType.setImageResource(item.getIsVeg() == 1 ? R.drawable.veg : R.drawable.nonveg);

            StringBuilder sb = new StringBuilder();
            sb.append(item.getItemName())
            .append("\n");
            if(!TextUtils.isEmpty(item.getDisplayPrice())){
                sb.append(item.getDisplayPrice());
            } else {
                sb.append(context.getString(R.string.rupees_value_format, Utils.getMoneyDecimalFormat().format(item.getPrice())));
            }

            mHolder.textViewItemCategoryName.setText(sb);

            int total = item.getTotalQuantity();
            if(total > 0){
                mHolder.linearLayoutQuantitySelector.setVisibility(View.VISIBLE);
                mHolder.addButton.setVisibility(View.GONE);
                mHolder.textViewQuantity.setText(String.valueOf(total));
            } else {
                mHolder.linearLayoutQuantitySelector.setVisibility(View.GONE);
                mHolder.addButton.setVisibility(View.VISIBLE);
            }

            mHolder.textViewAboutItemDescription.setVisibility(item.getItemDetails() != null ? View.VISIBLE : View.GONE);
            mHolder.textViewAboutItemDescription.setText(item.getItemDetails());
            mHolder.textViewAboutItemDescription.setTag(position);

            if(!TextUtils.isEmpty(item.getItemDetails())){
                if(item.getItemDetails().length() > 80){
                    SpannableStringBuilder ssb;
                    int end;
                    if(item.getExpanded()){
                        ssb = new SpannableStringBuilder(context.getString(R.string.less));
                        end = item.getItemDetails().length();
                    } else {
                        ssb = new SpannableStringBuilder(context.getString(R.string.more));
                        end = 80;
                    }
                    mHolder.textViewAboutItemDescription.setText(item.getItemDetails().substring(0, end));
                    ssb.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.theme_color)),
                            0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mHolder.textViewAboutItemDescription.append(" ");
                    mHolder.textViewAboutItemDescription.append(ssb);
                }
            }
            mHolder.textViewAboutItemDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        Item item1 = subItems.get(pos);
                        TextView tv = (TextView) v;
                        if(item1.getItemDetails().length() > 80) {
                            if (tv.getText().toString().length() > 85) {
                                item1.setExpanded(false);
                            } else {
                                item1.setExpanded(true);
                            }
                            notifyItemChanged(pos);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


//            makeTextViewResizable(mHolder.textViewAboutItemDescription, 2, context.getString(R.string.more), true);



            mHolder.relativeLayoutQuantitySel.setVisibility(View.VISIBLE);

            if(context instanceof FreshActivity
                    && ((FreshActivity)context).getVendorOpened() != null
                    && (1 == ((FreshActivity)context).getVendorOpened().getIsClosed() || 0 == ((FreshActivity)context).getVendorOpened().getIsAvailable())){
                mHolder.relativeLayoutQuantitySel.setVisibility(View.GONE);
            }



            mHolder.imageViewMinus.setTag(position);
            mHolder.imageViewPlus.setTag(position);
            mHolder.addButton.setTag(position);

            View.OnClickListener plusClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        Item item1 = subItems.get(pos);
                        if(callback.checkForAdd(pos, item1)) {
                            if (item1.getTotalQuantity() < 50) {
                                if (item1.getCustomizeItem().size() > 0) {
                                    if(categoryPos > -1){
                                        ((FreshActivity) context).openMenusItemCustomizeFragment(categoryPos, item1.getSubCategoryPos(), item1.getItemPos());
                                    } else {
                                        ((FreshActivity) context).openMenusItemCustomizeFragment(item1.getCategoryPos(), item1.getSubCategoryPos(), item1.getItemPos());
                                    }
                                } else {
                                    if (item1.getItemSelectedList().size() > 0) {
                                        item1.getItemSelectedList().get(0).setQuantity(item1.getItemSelectedList().get(0).getQuantity() + 1);
                                    } else {
                                        ItemSelected itemSelected = new ItemSelected();
                                        itemSelected.setRestaurantItemId(item1.getRestaurantItemId());
                                        itemSelected.setQuantity(1);
                                        itemSelected.setTotalPrice(item1.getPrice());
                                        item1.getItemSelectedList().add(itemSelected);
                                    }
                                    notifyItemChanged(pos);
                                    callback.onPlusClicked(pos, item1);
                                }
                            } else {
                                Utils.showToast(context, context.getString(R.string.cannot_add_more_than_50));
                            }
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            mHolder.addButton.setOnClickListener(plusClick);
            mHolder.imageViewPlus.setOnClickListener(plusClick);

            mHolder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        int pos = (int) v.getTag();
                        Item item1 = subItems.get(pos);
                        if (item1.getCustomizeItem().size() > 0) {
                            if(item1.getItemSelectedList().size() == 1){
                                item1.getItemSelectedList().get(0).setQuantity(item1.getItemSelectedList().get(0).getQuantity() - 1);
                                notifyItemChanged(pos);
                                callback.onMinusClicked(pos, item1);
                            } else {
                                callback.onMinusFailed(pos, item1);
                            }
                        } else {
                            if(item1.getItemSelectedList().size() > 0){
                                item1.getItemSelectedList().get(0).setQuantity(item1.getItemSelectedList().get(0).getQuantity() - 1);
                                notifyItemChanged(pos);
                                callback.onMinusClicked(pos, item1);
                            }
                        }
                    } catch (Exception e){}
                }
            });

        } else if(holder instanceof ViewHolderBlank) {
            ViewHolderBlank titleholder = ((ViewHolderBlank) holder);
            titleholder.relative.setVisibility(View.VISIBLE);
        } else if(holder instanceof SubCategoryViewHolder) {
            SubCategoryViewHolder subCategoryHolder = ((SubCategoryViewHolder) holder);
            subCategoryHolder.textViewSubCategoryName.setText(subItems.get(position).getItemName().toUpperCase());
        }

	}

    @Override
    public int getItemCount() {
        return subItems == null ? 0 : subItems.size()+1;
    }


    class MainViewHolder extends RecyclerView.ViewHolder {

        public CardView cardViewRecycler;
        public RelativeLayout relativeLayoutItem, relativeLayoutQuantitySel ;
        public LinearLayout linearLayoutQuantitySelector;
        private ImageView imageViewFoodType, saperatorImage, imageViewMinus, imageViewPlus;
        public TextView textViewItemCategoryName, textViewAboutItemDescription, textViewQuantity;
        public Button addButton;

        public MainViewHolder(View itemView, Context context) {
            super(itemView);
            cardViewRecycler = (CardView) itemView.findViewById(R.id.cvRoot);
            relativeLayoutItem = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutItem);
            relativeLayoutQuantitySel = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutQuantitySel);
            linearLayoutQuantitySelector = (LinearLayout) itemView.findViewById(R.id.linearLayoutQuantitySelector);
            imageViewFoodType = (ImageView) itemView.findViewById(R.id.imageViewFoodType);
            saperatorImage = (ImageView) itemView.findViewById(R.id.saperatorImage);
            imageViewMinus = (ImageView) itemView.findViewById(R.id.imageViewMinus);
            imageViewPlus = (ImageView) itemView.findViewById(R.id.imageViewPlus);

            textViewQuantity = (TextView)itemView.findViewById(R.id.textViewQuantity); textViewQuantity.setTypeface(Fonts.mavenRegular(context));
            textViewItemCategoryName = (TextView)itemView.findViewById(R.id.textViewItemCategoryName); textViewItemCategoryName.setTypeface(Fonts.mavenRegular(context));
            textViewAboutItemDescription = (TextView)itemView.findViewById(R.id.textViewAboutItemDescription); textViewAboutItemDescription.setTypeface(Fonts.mavenRegular(context));

            addButton = (Button) itemView.findViewById(R.id.add_button); addButton.setTypeface(Fonts.mavenRegular(context));
        }
    }

    class ViewHolderBlank extends RecyclerView.ViewHolder {

        public RelativeLayout relative;
        public ViewHolderBlank(View itemView) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
        }
    }

    class SubCategoryViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewSubCategoryName;
        public SubCategoryViewHolder(View itemView, Context context) {
            super(itemView);
            textViewSubCategoryName = (TextView) itemView.findViewById(R.id.tvSubCategoryName);textViewSubCategoryName.setTypeface(Fonts.mavenRegular(context));

        }
    }

    public interface Callback{
        boolean checkForAdd(int position, Item item);
        void onPlusClicked(int position, Item item);
        void onMinusClicked(int position, Item item);
        void onMinusFailed(int position, Item item);
    }


    public void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);
        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, context.getString(R.string.less), false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 2, context.getString(R.string.more), true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
            ssb.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.theme_color)),
                    str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ssb;
    }

}
