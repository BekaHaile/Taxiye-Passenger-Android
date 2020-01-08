package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.dialogs.BannerDetailDialog;
import com.sabkuchfresh.dialogs.ReviewImagePagerDialog;
import com.sabkuchfresh.fragments.FreshCategoryItemsFragment;
import com.sabkuchfresh.fragments.FreshSearchFragment;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.AppConstant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Created by Shankar on 7/17/15.
 */
public class FreshCategoryItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private List<SubItem> subItems;
    private Category.CategoryBanner categoryBanners;
    private Callback callback;
    private OpenMode openMode;
    private int showCategoryBanner = 0;

    private static final int MAIN_ITEM = 0;
    private static final int BLANK_ITEM = 1;
    private static final int BANNER_ITEM = 2;

    private int listType = 0;
    private int currentGroupId;
    private String categoryName;
    private int appType;

    public<T extends FreshCategoryItemsFragment> FreshCategoryItemsAdapter(Context context, ArrayList<SubItem> subItems, Category.CategoryBanner categoryBanners, int showCategoryBanner,
                                                                           OpenMode openMode, Callback callback, int listType, String categoryName, int currentGroupId,T fragment) {
        this.context = context;
        this.subItems = subItems;
        this.categoryBanners = categoryBanners;
        this.showCategoryBanner = showCategoryBanner;
        this.callback = callback;
        this.openMode = openMode;
        this.listType = listType;
        this.categoryName = categoryName;
        this.currentGroupId = currentGroupId;
        appType = Prefs.with(context).getInt(Constants.APP_TYPE, Data.AppType);
    }
    public<T extends FreshSearchFragment> FreshCategoryItemsAdapter(Context context, ArrayList<SubItem> subItems, Category.CategoryBanner categoryBanners, int showCategoryBanner,
                                                                    OpenMode openMode, Callback callback, int listType, String categoryName, int currentGroupId, T fragment) {
        this.context = context;
        this.subItems = new ArrayList<>();
        if(subItems!=null){
            this.subItems.addAll(subItems);

        }
        this.categoryBanners = categoryBanners;
        this.showCategoryBanner = showCategoryBanner;
        this.callback = callback;
        this.openMode = openMode;
        this.listType = listType;
        this.categoryName = categoryName;
        this.currentGroupId = currentGroupId;
        appType = Prefs.with(context).getInt(Constants.APP_TYPE, Data.AppType);
    }

   public synchronized   <T extends FreshSearchFragment> void setResults(ArrayList<SubItem> subItems,T fragment){
        this.subItems.clear();
        if(subItems!=null){
            this.subItems.addAll(subItems);

        }
        notifyDataSetChanged();

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BANNER_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_banner_category, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 194);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewBannerHolder(v, context);
        } else if (viewType == MAIN_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fresh_category_item, parent, false);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new MainViewHolder(v, context);
        } else if (viewType == BLANK_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer, parent, false);
            return new ViewTitleHolder(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }



    @Override
    public int getItemViewType(int position) {
//        if(position == 0 && categoryBanners != null && showCategoryBanner == 1) {
//            return BANNER_ITEM;
//        } else
        if(position == subItems.size()) {
            return BLANK_ITEM;
        }
        return MAIN_ITEM;
//        return slots.get(position).getSlotViewType().getOrdinal();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MainViewHolder) {
            final MainViewHolder mHolder = ((MainViewHolder) holder);
            final SubItem subItem = subItems.get(position);

            mHolder.textViewItemName.setText(subItem.getSubItemName());
            if(!TextUtils.isEmpty(subItem.getBaseUnit())) {
                mHolder.textViewItemUnit.setVisibility(View.VISIBLE);
                mHolder.textViewItemUnit.setText(subItem.getBaseUnit());
            } else {
                mHolder.textViewItemUnit.setVisibility(View.GONE);
            }
            //todo api: /get_super_categories
            mHolder.textViewItemPrice.setText(String.format(context.getResources().getString(R.string.rupees_value_format),
                    Utils.getMoneyDecimalFormat().format(subItem.getPrice())));

            if(TextUtils.isEmpty(subItem.getOldPrice())) {
                mHolder.textViewItemCost.setVisibility(View.GONE);
            } else {
                mHolder.textViewItemCost.setVisibility(View.VISIBLE);
                mHolder.textViewItemCost.setText(String.format(context.getResources().getString(R.string.rupees_value_format),
                        subItem.getOldPrice()));
            }

            if (openMode == OpenMode.CART) {
                Log.d("TAG", "currentGroupId = " + currentGroupId);
                if (Data.AppType == AppConstant.ApplicationType.MEALS) {
                    if (currentGroupId == subItem.getGroupId()) {
                        mHolder.unavilableView.setVisibility(View.GONE);
                    } else {
                        mHolder.unavilableView.setVisibility(View.VISIBLE);
                    }
                } else {
                    mHolder.unavilableView.setVisibility(View.GONE);
                }
            } else {
                mHolder.unavilableView.setVisibility(View.GONE);
            }


            mHolder.textViewItemCost.setPaintFlags(mHolder.textViewItemCost.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


//            mHolder.textViewItemCost.setVisibility(View.GONE);
            if(TextUtils.isEmpty(subItem.getOfferText())) {
                mHolder.textViewItemOff.setVisibility(View.GONE);
            } else {
                mHolder.textViewItemOff.setVisibility(View.VISIBLE);
                mHolder.textViewItemOff.setText(subItem.getOfferText());
            }

            if(TextUtils.isEmpty(subItem.getBannerText())) {
                mHolder.offerTagLayout.setVisibility(View.GONE);
            } else {
                try {
                    mHolder.offerTagLayout.setVisibility(View.VISIBLE);
                    int color = Color.parseColor(subItem.getBannerColor());

                    mHolder.bannerBg.setColorFilter(color);
                    mHolder.offerTag.setText(subItem.getBannerText());
                    mHolder.offerTag.setBackgroundColor(color);
                    int textColor = Color.parseColor(subItem.getBannerTextColor());//getBannertextColor
                    mHolder.offerTag.setTextColor(textColor);
                } catch(Exception e) {

                    e.printStackTrace();
                    Log.d("asdasd", "subItem.getBannerColor() = "+subItem.getBannerColor());
                    Log.d("asdasd", "subItem.getBannertextColor() = "+subItem.getBannerTextColor());

                    mHolder.offerTagLayout.setVisibility(View.VISIBLE);
                    int color = Color.parseColor("#FD7945");

                    mHolder.bannerBg.setColorFilter(color);
                    mHolder.offerTag.setText(subItem.getBannerText());
                    mHolder.offerTag.setBackgroundColor(color);
                    int textColor = Color.parseColor("#FFFFFF");
                    mHolder.offerTag.setTextColor(textColor);
                }
            }


            mHolder.textViewQuantity.setText(String.valueOf(subItem.getSubItemQuantitySelected()));
            mHolder.imageViewPlus.setImageResource(R.drawable.ic_plus_dark_selector);
            mHolder.linearLayoutQuantitySelector.setVisibility(View.VISIBLE);
            if (subItem.getSubItemQuantitySelected() == 0) {
                if(subItem.getStock() > 0){
                    mHolder.imageViewPlus.setImageResource(R.drawable.ic_plus_theme_selector);
                    mHolder.imageViewMinus.setVisibility(View.GONE);
                    mHolder.textViewQuantity.setVisibility(View.GONE);
                    mHolder.textViewOutOfStock.setVisibility(View.GONE);
                } else{
                    mHolder.linearLayoutQuantitySelector.setVisibility(View.GONE);
                    mHolder.textViewOutOfStock.setVisibility(View.VISIBLE);
                }
            } else {
                mHolder.imageViewMinus.setVisibility(View.VISIBLE);
                mHolder.textViewQuantity.setVisibility(View.VISIBLE);
                mHolder.textViewOutOfStock.setVisibility(View.GONE);
            }

            if(appType == AppConstant.ApplicationType.MENUS
                    && context instanceof FreshActivity
                    && ((FreshActivity)context).getVendorOpened() != null
                    && (1 == ((FreshActivity)context).getVendorOpened().getIsClosed() || 0 == ((FreshActivity)context).getVendorOpened().getIsAvailable())){
                mHolder.linearLayoutQuantitySelector.setVisibility(View.GONE);
                mHolder.textViewOutOfStock.setVisibility(View.GONE);
            }

            if(!subItem.getSubItemDesc().equalsIgnoreCase("")){
                mHolder.textViewMoreInfo.setVisibility(View.VISIBLE);
                if(appType == AppConstant.ApplicationType.MENUS){
                    mHolder.textViewMoreInfo.setTextColor(context.getResources().getColor(R.color.text_color_light));
                    mHolder.textViewMoreInfo.setText(subItem.getSubItemDesc());
                } else {
                    mHolder.textViewMoreInfo.setTextColor(context.getResources().getColor(R.color.theme_color));
                    mHolder.textViewMoreInfo.setText(context.getString(R.string.more_info));
                }
            } else{
                mHolder.textViewMoreInfo.setVisibility(View.GONE);
            }



            mHolder.relativeLayoutItemImage.setTag(position);
            mHolder.imageViewMinus.setTag(position);
            mHolder.imageViewPlus.setTag(position);
            mHolder.linearLayoutQuantitySelector.setTag(position);

            mHolder.textViewMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(appType != AppConstant.ApplicationType.MENUS) {
                        DialogPopup.alertPopup((Activity) context, "", subItem.getSubItemDesc());
                    }
                }
            });

            mHolder.linearLayoutQuantitySelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.e("onClick", "v=="+v);
                }
            });

            mHolder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
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

            mHolder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long time = System.currentTimeMillis();
                    try {
                        int pos = (int) v.getTag();
                        mHolder.linearLayoutQuantitySelector.performClick();
                        if(callback.checkForAdd(pos, subItems.get(pos))) {
                            if (subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
                                subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
                            } else {
                                Utils.showToast(context, context.getResources().getString(R.string.no_more_than, subItems.get(pos).getStock()));
                            }
                            callback.onPlusClicked(pos, subItems.get(pos));
                            notifyDataSetChanged();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("FreshCategoryItemsAdapter", "time="+(System.currentTimeMillis() - time));
                }
            });

            try {
                if (!TextUtils.isEmpty(subItem.getSubItemImage())) {
                    Picasso.with(context).load(subItem.getSubItemImage())
                            .placeholder(R.drawable.ic_fresh_item_placeholder)
                            .fit()
                            .centerCrop()
                            .error(R.drawable.ic_fresh_item_placeholder)
                            .into(mHolder.imageViewItemImage);

                    mHolder.relativeLayoutItemImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                int pos = (int) v.getTag();
                                if(subItems.get(pos)!=null && !TextUtils.isEmpty(subItems.get(pos).getSubItemImage())){
                                    ReviewImagePagerDialog dialog = ReviewImagePagerDialog.newInstance(0, subItems.get(pos).getSubItemImage());
                                    dialog.show(((Activity)context).getFragmentManager(), ReviewImagePagerDialog.class.getSimpleName());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } else {
                    mHolder.imageViewItemImage.setImageResource(R.drawable.ic_fresh_item_placeholder);
                    mHolder.imageViewItemImage.setVisibility((appType == AppConstant.ApplicationType.MENUS) ? View.GONE : View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mHolder.imageViewFoodType.setVisibility(appType == AppConstant.ApplicationType.MENUS ? View.VISIBLE : View.GONE);
            mHolder.imageViewFoodType.setImageResource(subItem.getIsVeg() == 1 ? R.drawable.veg : R.drawable.nonveg);
            RelativeLayout.LayoutParams paramsFT = (RelativeLayout.LayoutParams) mHolder.imageViewFoodType.getLayoutParams();
            RelativeLayout.LayoutParams paramsLLC = (RelativeLayout.LayoutParams) mHolder.linearLayoutContent.getLayoutParams();
            if(mHolder.imageViewFoodType.getVisibility() == View.VISIBLE && mHolder.imageViewItemImage.getVisibility() == View.GONE){
                if(mHolder.textViewMoreInfo.getVisibility() == View.VISIBLE){
                    paramsFT.setMargins((int)(ASSL.Xscale()*2f), (int)(ASSL.Yscale()*2f), 0, 0);
                    paramsFT.setMarginStart((int)(ASSL.Xscale()*2f));
                    paramsFT.setMarginEnd(0);
                } else {
                    paramsFT.setMargins(0, (int)(ASSL.Yscale()*25f), 0, 0);
                    paramsFT.setMarginStart(0);
                    paramsFT.setMarginEnd(0);
                }
                paramsLLC.setMargins((int)(ASSL.Xscale()*20f), 0, 0, 0);
                paramsLLC.setMarginStart((int)(ASSL.Xscale()*20f));
                paramsLLC.setMarginEnd(0);
            } else {
                paramsFT.setMargins((int)(ASSL.Xscale()*2f), (int)(ASSL.Yscale()*2f), 0, 0);
                paramsFT.setMarginStart((int)(ASSL.Xscale()*2f));
                paramsFT.setMarginEnd(0);
                paramsLLC.setMargins((int)(ASSL.Xscale()*30f), 0, 0, 0);
                paramsLLC.setMarginStart((int)(ASSL.Xscale()*30f));
                paramsLLC.setMarginEnd(0);
            }
            mHolder.imageViewFoodType.setLayoutParams(paramsFT);
            mHolder.linearLayoutContent.setLayoutParams(paramsLLC);
            if(TextUtils.isEmpty(subItem.getOfferText()) && TextUtils.isEmpty(subItem.getSubItemDesc())) {
                mHolder.llMoreInfoOff.setVisibility(View.GONE);
            } else {
                mHolder.llMoreInfoOff.setVisibility(View.VISIBLE);
            }

        } else if(holder instanceof ViewTitleHolder) {
            ViewTitleHolder titleholder = ((ViewTitleHolder) holder);
            titleholder.relative.setVisibility(View.VISIBLE);
        } else if(holder instanceof ViewBannerHolder) {
            ViewBannerHolder bannerHolder = ((ViewBannerHolder) holder);
            bannerHolder.relative.setVisibility(View.VISIBLE);
            bannerHolder.imageViewBanner.setVisibility(View.VISIBLE);
            Picasso.with(context).load(categoryBanners.getSmallImage())
                    .fit()
                    .into(bannerHolder.imageViewBanner);

            bannerHolder.relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BannerDetailDialog((Activity) context, new BannerDetailDialog.Callback() {
                        @Override
                        public void onDialogDismiss() {

                        }

                        @Override
                        public void onConfirmed() {

                        }
                    }).showBannerDetailDialog(categoryBanners.getLargeImage(), categoryBanners.getDescription());
                }
            });
        }

	}

    @Override
    public int getItemCount() {
        return subItems == null ? 0 : subItems.size()+1;
    }


    static class MainViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        private ImageView imageViewItemImage, imageViewFoodType, imageViewMinus, imageViewPlus, bannerBg;
        public TextView textViewItemName, textViewItemUnit, textViewItemPrice, textViewQuantity, textViewItemCost, textViewItemOff, offerTag;
        public TextView textViewOutOfStock, textViewMoreInfo, unavilableView;
        public LinearLayout linearLayoutContent, linearLayoutQuantitySelector, offerTagLayout, llMoreInfoOff;
        private RelativeLayout relativeLayoutItemImage;
        public MainViewHolder(View itemView, Context context) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            linearLayoutContent = (LinearLayout) itemView.findViewById(R.id.linearLayoutContent);
            linearLayoutQuantitySelector = (LinearLayout) itemView.findViewById(R.id.linearLayoutQuantitySelector);
            offerTagLayout = (LinearLayout) itemView.findViewById(R.id.offer_tag_layout);
            imageViewItemImage = (ImageView) itemView.findViewById(R.id.imageViewItemImage);
            relativeLayoutItemImage = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutItemImage);
            imageViewFoodType = (ImageView) itemView.findViewById(R.id.imageViewFoodType);
            imageViewMinus = (ImageView) itemView.findViewById(R.id.imageViewMinus);
            imageViewPlus = (ImageView) itemView.findViewById(R.id.imageViewPlus);
            bannerBg = (ImageView) itemView.findViewById(R.id.banner_bg);
            textViewMoreInfo = (TextView)itemView.findViewById(R.id.textViewMoreInfo);textViewMoreInfo.setTypeface(Fonts.mavenRegular(context));

            unavilableView = (TextView) itemView.findViewById(R.id.unavilable_view); unavilableView.setTypeface(Fonts.mavenRegular(context));
            textViewItemName = (TextView)itemView.findViewById(R.id.textViewItemName); textViewItemName.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
            textViewItemUnit = (TextView)itemView.findViewById(R.id.textViewItemUnit); textViewItemUnit.setTypeface(Fonts.mavenRegular(context));
            textViewItemPrice = (TextView)itemView.findViewById(R.id.textViewItemPrice); textViewItemPrice.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
            textViewQuantity = (TextView)itemView.findViewById(R.id.textViewQuantity); textViewQuantity.setTypeface(Fonts.avenirMedium(context));
            textViewItemCost = (TextView)itemView.findViewById(R.id.textViewItemCost); textViewItemCost.setTypeface(Fonts.mavenRegular(context));
            textViewItemOff = (TextView)itemView.findViewById(R.id.textViewItemOff); textViewItemOff.setTypeface(Fonts.mavenRegular(context));
            textViewOutOfStock = (TextView)itemView.findViewById(R.id.textViewOutOfStock); textViewOutOfStock.setTypeface(Fonts.mavenRegular(context));
            offerTag = (TextView)itemView.findViewById(R.id.offer_tag); offerTag.setTypeface(Fonts.mavenRegular(context));
            llMoreInfoOff = (LinearLayout) itemView.findViewById(R.id.llMoreInfoOff);
        }
    }

    static class ViewTitleHolder extends RecyclerView.ViewHolder {

        public RelativeLayout relative;
        public ViewTitleHolder(View itemView) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
        }
    }

    static class ViewBannerHolder extends RecyclerView.ViewHolder {

        public RelativeLayout relative;
        public ImageView imageViewBanner;
        public ViewBannerHolder(View itemView, Context context) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            imageViewBanner = (ImageView) itemView.findViewById(R.id.imageViewBanner);
        }
    }

    public interface Callback{
        boolean checkForAdd(int position, SubItem subItem);
        void onPlusClicked(int position, SubItem subItem);
        void onMinusClicked(int position, SubItem subItem);
        void onDeleteClicked(int position, SubItem subItem);
        boolean checkForMinus(int position, SubItem subItem);
        void minusNotDone(int position, SubItem subItem);
    }

    public enum OpenMode{
        INVENTORY, CART;
    }

}
