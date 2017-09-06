package com.sabkuchfresh.widgets;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.DeliveryDisplayCategoriesAdpater;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import product.clicklabs.jugnoo.R;

/**
 * Created by Parminder Saini on 02/09/17.
 */

public class DeliveryDisplayCategoriesView {

    private Activity activity;
    public View rootView;
    @Bind(R.id.iv_switch_category)
    ImageView ivSwitchCategory;
    @Bind(R.id.recycler_categories)
    RecyclerView rvCategories;
    @Bind(R.id.iv_category_arrow)
    ImageView ivArrow;
    @Bind(R.id.tv_category_name)
    TextView tvCategoryName;
    @Bind(R.id. view_bottom_blank)
    View viewBottomblank;
    @Bind(R.id.layout_choose_category)
    RelativeLayout layoutChooseCategory;
    private Callback callback;
    private DeliveryDisplayCategoriesAdpater deliveryDisplayCategoriesAdpater;
    private Animation categoryHideAnim;
    private Animation categoryShowAnim;
    private Handler handler = new Handler();
    private Runnable hideViewsRunnable = new Runnable() {
        @Override
        public void run() {
            layoutChooseCategory.setEnabled(true);
            if(rvCategories.getVisibility()==View.VISIBLE){
                rvCategories.setVisibility(View.GONE);
                viewBottomblank.setVisibility(View.GONE);
                ivArrow.setRotation(0);
                ivArrow.setRotation(90);
            }else{
                rvCategories.setVisibility(View.VISIBLE);
                viewBottomblank.setVisibility(View.VISIBLE);
                ivArrow.setRotation(0);
                ivArrow.setRotation(-90);
            }

        }
    };;

    @SuppressWarnings("Unused")
    private DeliveryDisplayCategoriesView() {

    }

    public DeliveryDisplayCategoriesView(Activity activity, View rootView, Callback callback) {
        this.activity = activity;
        this.rootView = rootView;
        this.callback = callback;
        ButterKnife.bind(this, rootView);
        categoryHideAnim = AnimationUtils.loadAnimation(activity, R.anim.rating_review_close_anim);
        categoryShowAnim = AnimationUtils.loadAnimation(activity, R.anim.rating_review_open_anim);
        ivArrow.setRotation(0);
        ivArrow.setRotation(rvCategories.getVisibility()==View.VISIBLE?90:-90);


    }

    public void setCategory(String categoryName) {
        tvCategoryName.setText(categoryName);
    }

    public void setCategories(List<MenusResponse.Category> deliveryCategoryModel) {
        if (rvCategories.getAdapter() == null) {
            rvCategories.setLayoutManager(new GridLayoutManager(activity, 4));
            deliveryDisplayCategoriesAdpater = new DeliveryDisplayCategoriesAdpater(activity, new DeliveryDisplayCategoriesAdpater.Callback() {
                @Override
                public void onItemClick(MenusResponse.Category category) {
                    OnCategoryClick(layoutChooseCategory);
                    setCategoryLabelIcon(category);
                    if(callback != null){
                        callback.onCategoryClick(category);
                    }
                }
            }, rvCategories);
            rvCategories.setAdapter(deliveryDisplayCategoriesAdpater);
        }

        deliveryDisplayCategoriesAdpater.setList(deliveryCategoryModel);
    }

    @OnClick({R.id.layout_choose_category, R.id.view_bottom_blank})
    public void OnCategoryClick(View view) {
        switch (view.getId()) {
            case R.id.layout_choose_category:
                layoutChooseCategory.setEnabled(false);
                if(rvCategories.getVisibility()==View.VISIBLE){
                    viewBottomblank.setVisibility(View.GONE);
                    rvCategories.startAnimation(categoryHideAnim);
                    handler.postDelayed(hideViewsRunnable,activity.getResources().getInteger(R.integer.time_category_anim_close));
                }else{
                    viewBottomblank.setAlpha(0);
                    viewBottomblank.setVisibility(View.VISIBLE);
                    viewBottomblank.animate().alpha(1).setDuration(activity.getResources().getInteger(R.integer.time_category_anim_open)).start();
                    rvCategories.startAnimation(categoryShowAnim);
                    handler.postDelayed(hideViewsRunnable,activity.getResources().getInteger(R.integer.time_category_anim_open));
                }
                break;

            case R.id.view_bottom_blank:
                OnCategoryClick(layoutChooseCategory);
                break;
        }

    }

    public interface Callback{
        void onCategoryClick(MenusResponse.Category category);
    }

    public void setCategoryLabelIcon(int categoryId){
        if(categoryId > 0) {
            if (deliveryDisplayCategoriesAdpater != null && deliveryDisplayCategoriesAdpater.getCategoriesList() != null) {
                int index = deliveryDisplayCategoriesAdpater.getCategoriesList().indexOf(new MenusResponse.Category(categoryId));
                if(index > -1){
                    setCategoryLabelIcon(deliveryDisplayCategoriesAdpater.getCategoriesList().get(index));
                }
            }
        } else {
            tvCategoryName.setText(R.string.all);
            ivSwitchCategory.setImageResource(R.drawable.ic_nav_select_category);
        }
    }

    private void setCategoryLabelIcon(MenusResponse.Category category){
        tvCategoryName.setText(category.getCategoryName());
        try {
            if (!TextUtils.isEmpty(category.getImage())) {
                Picasso.with(activity).load(category.getImage())
                        .placeholder(R.drawable.ic_nav_select_category)
                        .error(R.drawable.ic_nav_select_category)
                        .into(ivSwitchCategory);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            ivSwitchCategory.setImageResource(R.drawable.ic_nav_select_category);
        }
    }
}
