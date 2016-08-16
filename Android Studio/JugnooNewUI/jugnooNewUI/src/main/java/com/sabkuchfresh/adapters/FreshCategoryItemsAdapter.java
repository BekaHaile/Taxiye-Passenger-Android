package com.sabkuchfresh.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sabkuchfresh.analytics.FlurryEventLogger;
import com.sabkuchfresh.analytics.FlurryEventNames;
import com.sabkuchfresh.dialogs.BannerDetailDialog;
import com.sabkuchfresh.retrofit.model.Category;
import com.sabkuchfresh.retrofit.model.SubItem;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Data;
import com.sabkuchfresh.utils.DialogPopup;
import com.sabkuchfresh.utils.Fonts;
import com.sabkuchfresh.utils.Log;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.R;


/**
 * Created by Shankar on 7/17/15.
 */
public class FreshCategoryItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FlurryEventNames {

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

    public FreshCategoryItemsAdapter(Context context, ArrayList<SubItem> subItems, Category.CategoryBanner categoryBanners, int showCategoryBanner,
                                     OpenMode openMode, Callback callback, int listType, String categoryName, int currentGroupId) {
        this.context = context;
        this.subItems = subItems;
        this.categoryBanners = categoryBanners;
        this.showCategoryBanner = showCategoryBanner;
        this.callback = callback;
        this.openMode = openMode;
        this.listType = listType;
        this.categoryName = categoryName;
        this.currentGroupId = currentGroupId;
    }

    public synchronized void setResults(ArrayList<SubItem> subItems){
        this.subItems = subItems;
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

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 194);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new MainViewHolder(v, context);
        } else if (viewType == BLANK_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 194);
            v.setLayoutParams(layoutParams);

            ASSL.DoMagic(v);
            return new ViewTitleHolder(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }



    @Override
    public int getItemViewType(int position) {
        if(position == 0 && categoryBanners != null && showCategoryBanner == 1) {
            return BANNER_ITEM;
        } else if(position == subItems.size()) {
            return BLANK_ITEM;
        }
        return MAIN_ITEM;
//        return slots.get(position).getSlotViewType().getOrdinal();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MainViewHolder) {
            MainViewHolder mHolder = ((MainViewHolder) holder);
            final SubItem subItem = subItems.get(position);

            mHolder.textViewItemName.setText(subItem.getSubItemName());
            mHolder.textViewItemUnit.setText(subItem.getBaseUnit());
            mHolder.textViewItemPrice.setText(String.format(context.getResources().getString(R.string.rupees_value_format),
                    subItem.getPrice()));

            if(TextUtils.isEmpty(subItem.getOldPrice())) {
                mHolder.textViewItemCost.setVisibility(View.GONE);
            } else {
                mHolder.textViewItemCost.setVisibility(View.VISIBLE);
                mHolder.textViewItemCost.setText(String.format(context.getResources().getString(R.string.rupees_value_format),
                        subItem.getOldPrice()));//subItem.getOldPrice());
            }

            mHolder.textViewItemOff.setText(subItem.getSubItemName());

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
                    int color = Color.parseColor("#4DB831");

                    mHolder.bannerBg.setColorFilter(color);
                    mHolder.offerTag.setText(subItem.getBannerText());
                    mHolder.offerTag.setBackgroundColor(color);
                    int textColor = Color.parseColor("#FFFFFF");
                    mHolder.offerTag.setTextColor(textColor);
                }
            }


            mHolder.textViewQuantity.setText(String.valueOf(subItem.getSubItemQuantitySelected()));
            if (subItem.getSubItemQuantitySelected() == 0) {
                if(subItem.getStock() > 0){
                    mHolder.mAddButton.setVisibility(View.VISIBLE);
                    mHolder.textViewOutOfStock.setVisibility(View.GONE);
                } else{
                    mHolder.mAddButton.setVisibility(View.GONE);
                    mHolder.textViewOutOfStock.setVisibility(View.VISIBLE);
                }
                mHolder.linearLayoutQuantitySelector.setVisibility(View.GONE);
            } else {
                mHolder.mAddButton.setVisibility(View.GONE);
                mHolder.linearLayoutQuantitySelector.setVisibility(View.VISIBLE);
                mHolder.textViewOutOfStock.setVisibility(View.GONE);
            }
//            if (openMode == OpenMode.CART) {
                mHolder.imageViewDelete.setVisibility(View.GONE);
//            } else {
//                mHolder.imageViewDelete.setVisibility(View.GONE);
//            }

            if(!subItem.getSubItemDesc().equalsIgnoreCase("")){
                mHolder.imageViewMoreInfoSeprator.setVisibility(View.VISIBLE);
                mHolder.textViewMoreInfo.setVisibility(View.VISIBLE);
            } else{
                mHolder.imageViewMoreInfoSeprator.setVisibility(View.GONE);
                mHolder.textViewMoreInfo.setVisibility(View.GONE);
            }




            mHolder.imageViewMinus.setTag(position);
            mHolder.imageViewPlus.setTag(position);
            mHolder.imageViewDelete.setTag(position);
            mHolder.mAddButton.setTag(position);

            mHolder.textViewMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogPopup.alertPopupWithCancellable((Activity)context, "", subItem.getSubItemDesc());
                }
            });


            mHolder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        FlurryEventLogger.event(categoryName, FlurryEventNames.DELETE_PRODUCT, subItems.get(pos).getSubItemName());
                        subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() > 0 ?
                                subItems.get(pos).getSubItemQuantitySelected() - 1 : 0);
                        callback.onMinusClicked(pos, subItems.get(pos));

                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mHolder.mAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        int pos = (int) v.getTag();
//                        subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock() ?
//                                subItems.get(pos).getSubItemQuantitySelected() + 1 : subItems.get(pos).getStock());
                        if(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
                            subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
                        } else {
                            Toast.makeText(context, "Can't order more than "+subItems.get(pos).getStock() + " units", Toast.LENGTH_SHORT).show();
                        }
                        callback.onPlusClicked(pos, subItems.get(pos));
                        FlurryEventLogger.event(categoryName, FlurryEventNames.ADD_PRODUCT, subItems.get(pos).getSubItemName());
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            mHolder.imageViewPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
//                        subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock() ?
//                                subItems.get(pos).getSubItemQuantitySelected() + 1 : subItems.get(pos).getStock());
                        if(subItems.get(pos).getSubItemQuantitySelected() < subItems.get(pos).getStock()) {
                            subItems.get(pos).setSubItemQuantitySelected(subItems.get(pos).getSubItemQuantitySelected() + 1);
                        } else {
                            Toast.makeText(context, "Can't order more than "+subItems.get(pos).getStock() + " units", Toast.LENGTH_SHORT).show();
                        }

                        callback.onPlusClicked(pos, subItems.get(pos));
                        FlurryEventLogger.event(categoryName, FlurryEventNames.ADD_PRODUCT, subItems.get(pos).getSubItemName());
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mHolder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        FlurryEventLogger.event(categoryName, FlurryEventNames.DELETE_PRODUCT, subItems.get(pos).getSubItemName());
                        subItems.get(pos).setSubItemQuantitySelected(0);
                        callback.onDeleteClicked(pos, subItems.get(pos));

                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            try {
                if (subItem.getSubItemImage() != null && !"".equalsIgnoreCase(subItem.getSubItemImage())) {
                    Picasso.with(context).load(subItem.getSubItemImage())
                            .placeholder(R.drawable.ic_fresh_item_placeholder)
                            .error(R.drawable.ic_fresh_item_placeholder)
                            .fit()
                            .into(mHolder.imageViewItemImage);
                } else {
                    mHolder.imageViewItemImage.setImageResource(R.drawable.ic_fresh_item_placeholder);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(holder instanceof ViewTitleHolder) {
            ViewTitleHolder titleholder = ((ViewTitleHolder) holder);
            titleholder.relative.setVisibility(View.VISIBLE);
        } else if(holder instanceof ViewBannerHolder) {
            ViewBannerHolder bannerHolder = ((ViewBannerHolder) holder);
            bannerHolder.relative.setVisibility(View.VISIBLE);
            bannerHolder.imageViewBanner.setVisibility(View.VISIBLE);
            Picasso.with(context).load(categoryBanners.getSmallImage())
                    .placeholder(R.drawable.img_ice_cream_banner)
                    .error(R.drawable.img_ice_cream_banner)
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
        if(listType == AppConstant.ListType.HOME)
            return subItems == null ? 0 : subItems.size()+1;
        else
            return subItems == null ? 0 : subItems.size();
    }

    static class MainViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        private ImageView imageViewItemImage, imageViewMinus, imageViewPlus, imageViewDelete, bannerBg, imageViewMoreInfoSeprator;
        public TextView textViewItemName, textViewItemUnit, textViewItemPrice, textViewQuantity, textViewItemCost, textViewItemOff, offerTag;
        public TextView unavilableView, textViewOutOfStock, textViewMoreInfo;
        public Button mAddButton;
        public LinearLayout linearLayoutQuantitySelector, offerTagLayout;
        public MainViewHolder(View itemView, Context context) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            linearLayoutQuantitySelector = (LinearLayout) itemView.findViewById(R.id.linearLayoutQuantitySelector);
            offerTagLayout = (LinearLayout) itemView.findViewById(R.id.offer_tag_layout);
            imageViewItemImage = (ImageView) itemView.findViewById(R.id.imageViewItemImage);
            imageViewMinus = (ImageView) itemView.findViewById(R.id.imageViewMinus);
            imageViewPlus = (ImageView) itemView.findViewById(R.id.imageViewPlus);
            imageViewDelete = (ImageView) itemView.findViewById(R.id.imageViewDelete);
            imageViewMoreInfoSeprator = (ImageView)itemView.findViewById(R.id.imageViewMoreInfoSeprator);
            bannerBg = (ImageView) itemView.findViewById(R.id.banner_bg);
            mAddButton = (Button) itemView.findViewById(R.id.add_button);
            textViewMoreInfo = (TextView)itemView.findViewById(R.id.textViewMoreInfo);textViewMoreInfo.setTypeface(Fonts.mavenRegular(context));

            unavilableView = (TextView) itemView.findViewById(R.id.unavilable_view); unavilableView.setTypeface(Fonts.mavenRegular(context));

            textViewItemName = (TextView)itemView.findViewById(R.id.textViewItemName); textViewItemName.setTypeface(Fonts.mavenRegular(context));
            textViewItemUnit = (TextView)itemView.findViewById(R.id.textViewItemUnit); textViewItemUnit.setTypeface(Fonts.mavenRegular(context));
            textViewItemPrice = (TextView)itemView.findViewById(R.id.textViewItemPrice); textViewItemPrice.setTypeface(Fonts.mavenRegular(context));
            textViewQuantity = (TextView)itemView.findViewById(R.id.textViewQuantity); textViewQuantity.setTypeface(Fonts.mavenRegular(context));
            textViewItemCost = (TextView)itemView.findViewById(R.id.textViewItemCost); textViewItemCost.setTypeface(Fonts.mavenRegular(context));
            textViewItemOff = (TextView)itemView.findViewById(R.id.textViewItemOff); textViewItemOff.setTypeface(Fonts.mavenRegular(context));
            textViewOutOfStock = (TextView)itemView.findViewById(R.id.textViewOutOfStock); textViewOutOfStock.setTypeface(Fonts.mavenRegular(context));
            offerTag = (TextView)itemView.findViewById(R.id.offer_tag); offerTag.setTypeface(Fonts.mavenRegular(context));
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
        void onPlusClicked(int position, SubItem subItem);
        void onMinusClicked(int position, SubItem subItem);
        void onDeleteClicked(int position, SubItem subItem);
    }

    public enum OpenMode{
        INVENTORY, CART;
    }

}
