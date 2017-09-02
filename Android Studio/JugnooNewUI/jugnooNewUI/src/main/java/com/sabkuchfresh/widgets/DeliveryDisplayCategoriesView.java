package com.sabkuchfresh.widgets;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sabkuchfresh.adapters.DeliveryDisplayCategoriesAdpater;
import com.sabkuchfresh.retrofit.model.delivery.DeliveryCategoryModel;

import java.util.ArrayList;
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

    public DeliveryDisplayCategoriesView(Activity activity, View rootView) {
        this.activity = activity;
        this.rootView = rootView;
        ButterKnife.bind(this, rootView);
        categoryHideAnim = AnimationUtils.loadAnimation(activity, R.anim.rating_review_close_anim);
        categoryShowAnim = AnimationUtils.loadAnimation(activity, R.anim.rating_review_open_anim);
        ivArrow.setRotation(0);
        ivArrow.setRotation(rvCategories.getVisibility()==View.VISIBLE?90:-90);


    }

    public void setCategory(String categoryName) {
        tvCategoryName.setText(categoryName);
    }

    public void setCategories(List<DeliveryCategoryModel> deliveryCategoryModel) {
        if (rvCategories.getAdapter() == null) {
            rvCategories.setLayoutManager(new GridLayoutManager(activity, 4));
            deliveryDisplayCategoriesAdpater = new DeliveryDisplayCategoriesAdpater(activity, new DeliveryDisplayCategoriesAdpater.Callback() {
                @Override
                public void onItemClick(DeliveryCategoryModel deliveryCategoryModel) {

                }
            }, rvCategories);
            rvCategories.setAdapter(deliveryDisplayCategoriesAdpater);

        }

        List<DeliveryCategoryModel> deliveryCategoryModell  = new ArrayList<>();
        deliveryCategoryModell.add(new DeliveryCategoryModel("Bakery",null));
        deliveryCategoryModell.add(new DeliveryCategoryModel("Beauty",null));
        deliveryCategoryModell.add(new DeliveryCategoryModel("Grocery",null));
        deliveryCategoryModell.add(new DeliveryCategoryModel("Ask local",null));
        deliveryCategoryModell.add(new DeliveryCategoryModel("Fresh",null));
        deliveryDisplayCategoriesAdpater.setList(deliveryCategoryModell);

    }

    @OnClick({R.id.layout_choose_category, R.id.view_bottom_blank})
    public void OnCategoryClick(View view) {
        switch (view.getId()) {
            case R.id.layout_choose_category:
                layoutChooseCategory.setEnabled(false);
                if(rvCategories.getVisibility()==View.VISIBLE){
                    viewBottomblank.setVisibility(View.GONE);
                    rvCategories.startAnimation(categoryHideAnim);
                }else{
                    rvCategories.startAnimation(categoryShowAnim);
                }
                handler.postDelayed(hideViewsRunnable,activity.getResources().getInteger(R.integer.time_category_anim));


                break;

            case R.id.view_bottom_blank:

                break;
        }

    }


}
