package com.sabkuchfresh.adapters;

import android.content.Context;
import android.graphics.Typeface;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.retrofit.model.SuperCategoriesData;
import com.squareup.picasso.Picasso;

import java.util.List;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;

/**
 * Created by shankar on 1/20/17.
 */

public class FreshSuperCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<SuperCategoriesData.SuperCategory> superCategories;
    private Callback callback;
    private boolean isSingleItem;
    private List<SuperCategoriesData.SuperCategory> offerList;
    public static final int MAIN_ITEM = 1;
    public static final int SINGLE_ITEM = 0;
    public static final int PAGER = 2;

    public FreshSuperCategoriesAdapter(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    public synchronized void clearList() {
        if (superCategories != null) {
            superCategories.clear();
        }
        notifyDataSetChanged();
    }

    public synchronized void setList(List<SuperCategoriesData.SuperCategory> elements, List<SuperCategoriesData.SuperCategory> offerList) {
        this.offerList = offerList;
        this.superCategories = elements;
        int enabledItem = 0;
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).getIsEnabled() == 1) {
                enabledItem++;
            }
        }
        if (enabledItem == 1) {
            isSingleItem = true;
        } else {
            isSingleItem = false;
        }
        notifyDataSetChanged();
    }

    public class ViewHolderCategory extends RecyclerView.ViewHolder {
        public LinearLayout llRoot;
        public ImageView ivSuperCategoryImage;
        public TextView tvSuperCategoryName, tvBanner;
        public View viewBG;

        public ViewHolderCategory(View view, Context context) {
            super(view);
            llRoot = (LinearLayout) view.findViewById(R.id.llRoot);
            ivSuperCategoryImage = (ImageView) view.findViewById(R.id.ivSuperCategoryImage);
            tvSuperCategoryName = (TextView) view.findViewById(R.id.tvSuperCategoryName);
            tvSuperCategoryName.setTypeface(Fonts.mavenMedium(context));
            tvBanner = (TextView) view.findViewById(R.id.tvBanner);
            viewBG = (View) view.findViewById(R.id.viewBG);
        }
    }

    public class ViewHolderCategorySingle extends RecyclerView.ViewHolder {
        public LinearLayout llRoot;
        public ImageView ivSuperCategoryImage;
        public TextView tvSuperCategoryName, tvComingSoon;
        public View viewBG;

        public ViewHolderCategorySingle(View view, Context context) {
            super(view);
            llRoot = (LinearLayout) view.findViewById(R.id.llRoot);
            ivSuperCategoryImage = (ImageView) view.findViewById(R.id.ivSuperCategoryImage);
            tvSuperCategoryName = (TextView) view.findViewById(R.id.tvSuperCategoryName);
            tvSuperCategoryName.setTypeface(Fonts.mavenMedium(context));
            tvComingSoon = (TextView) view.findViewById(R.id.tvComingSoon);
            tvComingSoon.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
            viewBG = (View) view.findViewById(R.id.viewBG);
        }
    }

    public class ViewHolderPager extends RecyclerView.ViewHolder {
        public RelativeLayout rlPager;
        public ViewPager pager;
        public TabLayout tabDots;
        private CustomPagerAdapter mCustomPagerAdapter;

        public ViewHolderPager(View view, Context context) {
            super(view);
            rlPager = (RelativeLayout) view.findViewById(R.id.rlPager);
            pager = (ViewPager) view.findViewById(R.id.pager);
            tabDots = (TabLayout) view.findViewById(R.id.tabDots);
            mCustomPagerAdapter = new CustomPagerAdapter(context, offerList, new CustomPagerAdapter.Callback() {
                @Override
                public void onOfferClick(int pos, SuperCategoriesData.SuperCategory superCategory) {
                    callback.onItemClick(pos, superCategory);
                }
            });
            pager.setAdapter(mCustomPagerAdapter);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SINGLE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_fresh_super_category_single, parent, false);
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            ASSL.DoMagic(view);
            return new ViewHolderCategorySingle(view, context);
        } else if (viewType == PAGER) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_pager, parent, false);
            view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 220));
            ASSL.DoMagic(view);
            return new ViewHolderPager(view, context);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_fresh_super_category, parent, false);
            view.setLayoutParams(new RecyclerView.LayoutParams(240, 240));
            ASSL.DoMagic(view);
            return new ViewHolderCategory(view, context);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {
        if (mholder instanceof ViewHolderCategory) {
            if (offerList.size() > 0) {
                position--;
            }
            SuperCategoriesData.SuperCategory superCategory = superCategories.get(position);
            ViewHolderCategory holder = ((ViewHolderCategory) mholder);
            holder.tvSuperCategoryName.setText(superCategory.getSuperCategoryName());
            if(superCategory.getSuperCategoryBanner() != null
                    && !TextUtils.isEmpty(superCategory.getSuperCategoryBanner().getBannerText())){
                holder.tvBanner.setVisibility(View.VISIBLE);
                holder.tvBanner.setText(superCategory.getSuperCategoryBanner().getBannerText());
                if(!TextUtils.isEmpty(superCategory.getSuperCategoryBanner().getBannerColor())) {
                    holder.tvBanner.setBackgroundColor(((FreshActivity) context).getParsedColor(superCategory.getSuperCategoryBanner().getBannerColor(), R.color.theme_color));
                }
                if(!TextUtils.isEmpty(superCategory.getSuperCategoryBanner().getBannerTextColor())){
                    holder.tvBanner.setTextColor(((FreshActivity) context).getParsedColor(superCategory.getSuperCategoryBanner().getBannerTextColor(), R.color.white));
                }
            } else{
                holder.tvBanner.setVisibility(View.GONE);
            }
            holder.llRoot.setTag(position);
            holder.llRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        callback.onItemClick(pos, superCategories.get(pos));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            try {
                if (!TextUtils.isEmpty(superCategory.getSuperCategoryImage())) {
                    Picasso.with(context).load(superCategory.getSuperCategoryImage())
                            .placeholder(R.drawable.ic_fresh_new_placeholder)
                            .error(R.drawable.ic_fresh_new_placeholder)
                            .into(holder.ivSuperCategoryImage);
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                Picasso.with(context).load(R.drawable.ic_fresh_new_placeholder)
                        .into(holder.ivSuperCategoryImage);
            }

            if (superCategory.getIsEnabled() == 0) {
                holder.viewBG.setBackgroundResource(R.drawable.bg_white_60_selector_color);
            } else {
                holder.viewBG.setBackgroundResource(R.drawable.bg_transparent_white_60_selector);
            }
        } else if (mholder instanceof ViewHolderCategorySingle) {
            SuperCategoriesData.SuperCategory superCategory = superCategories.get(position);
            ViewHolderCategorySingle singleHolder = ((ViewHolderCategorySingle) mholder);
            singleHolder.tvSuperCategoryName.setText(superCategory.getSuperCategoryName());
            singleHolder.llRoot.setTag(position);
            singleHolder.llRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int pos = (int) v.getTag();
                        callback.onItemClick(pos, superCategories.get(pos));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            try {
                if (!TextUtils.isEmpty(superCategory.getSuperCategoryImage())) {
                    Picasso.with(context).load(superCategory.getSuperCategoryImage())
                            .placeholder(R.drawable.ic_fresh_new_placeholder)
                            .error(R.drawable.ic_fresh_new_placeholder)
                            .into(singleHolder.ivSuperCategoryImage);
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Picasso.with(context).load(R.drawable.ic_fresh_new_placeholder)
                        .into(singleHolder.ivSuperCategoryImage);
            }
        } else if (mholder instanceof ViewHolderPager) {
            ViewHolderPager pagerHolder = ((ViewHolderPager) mholder);
            pagerHolder.tabDots.setupWithViewPager(pagerHolder.pager, true);
            pagerHolder.mCustomPagerAdapter.setList(offerList);

            for (int i = 0; i < pagerHolder.tabDots.getTabCount(); i++) {
                View tab = ((ViewGroup) pagerHolder.tabDots.getChildAt(0)).getChildAt(i);
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                p.setMargins(20, 0, 0, 0);
                p.setMarginStart(20);
                p.setMarginEnd(0);
                tab.requestLayout();
            }
            if(offerList.size() == 1){
                pagerHolder.tabDots.setVisibility(View.GONE);
            } else{
                pagerHolder.tabDots.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    public int getItemCount() {
        if (superCategories == null) {
            return 0;
        } else if (offerList.size() > 0) {
            return superCategories.size() + 1;
        } else {
            return superCategories.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && offerList.size() > 0) {
            return PAGER;
        } else {
            return MAIN_ITEM;
        }
    }

    public interface Callback {
        void onItemClick(int pos, SuperCategoriesData.SuperCategory superCategory);
    }
}