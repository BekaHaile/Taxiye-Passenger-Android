package com.sabkuchfresh.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import com.sabkuchfresh.dialogs.MenusCustomizeItemDialog;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.menus.Item;
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
    private Callback callback;

    private static final int MAIN_ITEM = 0;
    private static final int BLANK_ITEM = 1;
    private static final int SUB_CATEGORY_ITEM = 2;

    public MenusCategoryItemsAdapter(Context context, ArrayList<Subcategory> subcategories, Callback callback) {
        this.context = context;
        setSubItems(subcategories);
        this.callback = callback;
    }

    public synchronized void setResults(ArrayList<Subcategory> subcategories){
        setSubItems(subcategories);
        notifyDataSetChanged();
    }

    private void setSubItems(ArrayList<Subcategory> subcategories){
        subItems = new ArrayList<>();
        for(Subcategory subcategory : subcategories){
            Item item = new Item();
            item.setItemName(subcategory.getSubcategoryName());
            item.setIsSubCategory(1);
            subItems.add(item);
            subItems.addAll(subcategory.getItems());
        }
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menus_category_desc, parent, false);

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

            Item itemUp = subItems.get(position - 1);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mHolder.cardViewRecycler.getLayoutParams();
            int topMargin, bottomMargin;
            if(itemUp.getIsSubCategory() == 1){
                topMargin = (int)(6.0f*ASSL.Yscale());
            } else{
                topMargin = (int)(-6.0f*ASSL.Yscale());
            }

            Item itemDown = (position < (subItems.size()-1)) ? subItems.get(position + 1) : null;
            if(itemDown == null || itemDown.getIsSubCategory() == 1){
                mHolder.saperatorImage.setVisibility(View.GONE);
                bottomMargin = (int)(6.0f*ASSL.Yscale());
            } else{
                mHolder.saperatorImage.setVisibility(View.VISIBLE);
                bottomMargin = (int)(-6.0f*ASSL.Yscale());
            }
            layoutParams.setMargins((int) (25.0f*ASSL.Xscale()), topMargin, (int)(25.0f*ASSL.Xscale()), bottomMargin);
            mHolder.cardViewRecycler.setLayoutParams(layoutParams);

            mHolder.imageViewFoodType.setImageResource(item.getIsVeg() == 1 ? R.drawable.veg : R.drawable.nonveg);

            StringBuilder sb = new StringBuilder();
            sb.append(item.getItemName())
            .append("\n")
            .append(context.getString(R.string.rupees_value_format, Utils.getMoneyDecimalFormat().format(item.getPrice())))
            .append(" ").append(context.getString(R.string.onwards));
            mHolder.textViewItemCategoryName.setText(sb);

            if(item.getItemSelectedList().size() > 0){
                mHolder.linearLayoutQuantitySelector.setVisibility(View.VISIBLE);
                mHolder.addButton.setVisibility(View.GONE);
                mHolder.textViewQuantity.setText(String.valueOf(item.getItemSelectedList().size()));
            } else {
                mHolder.linearLayoutQuantitySelector.setVisibility(View.GONE);
                mHolder.addButton.setVisibility(View.VISIBLE);
            }

            mHolder.textViewAboutItemDescription.setVisibility(item.getItemDetails() != null ? View.VISIBLE : View.GONE);
            mHolder.textViewAboutItemDescription.setText(item.getItemDetails());

            makeTextViewResizable(mHolder.textViewAboutItemDescription, 2, "View More", true);


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
                    try {
                        int pos = (int) v.getTag();
                        Item item1 = subItems.get(pos);
                        if (item1.getCustomizeItem().size() > 0) {

                            new MenusCustomizeItemDialog((FreshActivity) context, new MenusCustomizeItemDialog.Callback() {
                                @Override
                                public void onDismiss() {
                                }
                            }, MenusCategoryItemsAdapter.this).show(item1);
                            notifyDataSetChanged();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            mHolder.addButton.setOnClickListener(plusClick);
            mHolder.textViewPlus.setOnClickListener(plusClick);


           /* mHolder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        if(callback.checkForAdd(pos, subItems.get(pos))) {
                            if (subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
                                subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
                            } else {
//                                Utils.showToast(context, context.getResources().getString(R.string.no_more_than, subItems.get(pos).getStock()));
                            }
                            callback.onPlusClicked(pos, subItems.get(pos));
                            notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });*/


          /*  mHolder.textViewMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        if(callback.checkForMinus(pos, subItems.get(pos))) {

                            subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() > 0 ?
                                    subItems.get(pos).getSubItemQuantitySelected() - 1 : 0);
                            callback.onMinusClicked(pos, subItems.get(pos));

                            notifyDataSetChanged();
                        } else{
                            callback.minusNotDone(pos, subItems.get(pos));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            mHolder.textViewPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
//                        subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock() ?
//                                subItems.get(pos).getSubItemQuantitySelected() + 1 : subItems.get(pos).getStock());
                        if(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
                            subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
                        } else {
                            Utils.showToast(context, context.getResources().getString(R.string.no_more_than, subItems.get(pos).getStock()));
                        }

                        callback.onPlusClicked(pos, subItems.get(pos));
                        FlurryEventLogger.event(categoryName, FlurryEventNames.ADD_PRODUCT, subItems.get(pos).getSubItemName());
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });*/


         /*   mHolder.textViewMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        if(callback.checkForMinus(pos, subItems.get(pos))) {

                            subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() > 0 ?
                                    subItems.get(pos).getSubItemQuantitySelected() - 1 : 0);
                            callback.onMinusClicked(pos, subItems.get(pos));

                            notifyDataSetChanged();
                            int appType = Prefs.with(context).getInt(Constants.APP_TYPE, Data.AppType);
                            if(appType == AppConstant.ApplicationType.FRESH){
                                FlurryEventLogger.event(FRESH_FRAGMENT, FlurryEventNames.DELETE_PRODUCT, subItems.get(pos).getSubItemName());
                            } else if(appType == AppConstant.ApplicationType.GROCERY){
                                FlurryEventLogger.event(FlurryEventNames.GROCERY_FRAGMENT, FlurryEventNames.DELETE_PRODUCT, subItems.get(pos).getSubItemName());
                            }
                        } else{
                            callback.minusNotDone(pos, subItems.get(pos));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            mHolder.textViewPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
//                        subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock() ?
//                                subItems.get(pos).getSubItemQuantitySelected() + 1 : subItems.get(pos).getStock());
                        if(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
                            subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
                        } else {
                            Utils.showToast(context, context.getResources().getString(R.string.no_more_than, subItems.get(pos).getStock()));
                        }

                        callback.onPlusClicked(pos, subItems.get(pos));
                        FlurryEventLogger.event(categoryName, FlurryEventNames.ADD_PRODUCT, subItems.get(pos).getSubItemName());
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });*/


        } else if(holder instanceof ViewHolderBlank) {
            ViewHolderBlank titleholder = ((ViewHolderBlank) holder);
            titleholder.relative.setVisibility(View.VISIBLE);
        } else if(holder instanceof SubCategoryViewHolder) {
            SubCategoryViewHolder subCategoryHolder = ((SubCategoryViewHolder) holder);
            subCategoryHolder.textViewSubCategoryName.setText(subItems.get(position).getItemName());
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
        private ImageView imageViewFoodType, saperatorImage;
        public TextView textViewItemCategoryName, textViewOutOfStock, textViewAboutItemDescription, textViewQuantity, textViewMinus, textViewPlus;
        public Button addButton;

        public MainViewHolder(View itemView, Context context) {
            super(itemView);
            cardViewRecycler = (CardView) itemView.findViewById(R.id.cardViewRecycler);
            relativeLayoutItem = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutItem);
            relativeLayoutQuantitySel = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutQuantitySel);
            linearLayoutQuantitySelector = (LinearLayout) itemView.findViewById(R.id.linearLayoutQuantitySelector);
            imageViewFoodType = (ImageView) itemView.findViewById(R.id.imageViewFoodType);
            saperatorImage = (ImageView) itemView.findViewById(R.id.saperatorImage);
            textViewMinus = (TextView) itemView.findViewById(R.id.textViewMinus);
            textViewPlus = (TextView) itemView.findViewById(R.id.textViewPlus);

            textViewQuantity = (TextView)itemView.findViewById(R.id.textViewQuantity); textViewQuantity.setTypeface(Fonts.mavenRegular(context));
            textViewOutOfStock = (TextView)itemView.findViewById(R.id.textViewOutOfStock); textViewOutOfStock.setTypeface(Fonts.mavenRegular(context));
            textViewItemCategoryName = (TextView)itemView.findViewById(R.id.textViewItemCategoryName); textViewItemCategoryName.setTypeface(Fonts.mavenRegular(context));
            textViewAboutItemDescription = (TextView)itemView.findViewById(R.id.textViewAboutItemDescription); textViewAboutItemDescription.setTypeface(Fonts.mavenRegular(context));

            addButton = (Button) itemView.findViewById(R.id.add_button);
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
            textViewSubCategoryName = (TextView) itemView.findViewById(R.id.textViewSubCategoryName);textViewSubCategoryName.setTypeface(Fonts.mavenRegular(context));

        }
    }

    public interface Callback{
        boolean checkForAdd(int position, Item subItem);
        void onPlusClicked(int position, Item subItem);
        void onMinusClicked(int position, Item subItem);
        boolean checkForMinus(int position, Item subItem);
        void minusNotDone(int position, Item subItem);
    }


    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

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

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
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
                        makeTextViewResizable(tv, -1, "View Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 2, "View More", true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
        }
        return ssb;
    }



}
