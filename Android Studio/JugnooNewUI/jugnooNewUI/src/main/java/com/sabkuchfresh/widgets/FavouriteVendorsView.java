package com.sabkuchfresh.widgets;

import android.app.Activity;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sabkuchfresh.adapters.FavouriteVendorsAdpater;
import com.sabkuchfresh.retrofit.model.menus.MenusResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;


public class FavouriteVendorsView extends RecyclerView.ViewHolder {

    private Activity activity;
    public View rootView;
    @BindView(R.id.recycler_favourite_vendors)
    RecyclerView rvFavouriteVendors;
    private Callback callback;
    private FavouriteVendorsAdpater favouriteVendorsAdpater;

    public FavouriteVendorsView(Activity activity, View rootView, Callback callback) {
        super(rootView);
        this.activity = activity;
        this.rootView = rootView;
        this.callback = callback;
        ButterKnife.bind(this, rootView);
    }

    public void setFavouriteVendors(List<MenusResponse.Vendor> favouriteVendorModel) {
        if (favouriteVendorsAdpater == null) {
            rvFavouriteVendors.setLayoutManager(new GridLayoutManager(activity, 3));
            favouriteVendorsAdpater = new FavouriteVendorsAdpater(activity, new FavouriteVendorsAdpater.Callback() {
                @Override
                public void onItemClick(MenusResponse.Vendor favouriteVendor) {
                    if(callback != null){
                        callback.onFavouriteVendorClick(favouriteVendor);
                    }
                }
            }, rvFavouriteVendors);
            rvFavouriteVendors.setAdapter(favouriteVendorsAdpater);
            List<MenusResponse.Vendor> favouriteVendors = new ArrayList<>();
            favouriteVendors.addAll(favouriteVendorModel);
            favouriteVendorsAdpater.setList(favouriteVendors);

        }
        else {
            List<MenusResponse.Vendor> favouriteVendors = new ArrayList<>();
            favouriteVendors.addAll(favouriteVendorModel);
            favouriteVendorsAdpater.setList(favouriteVendors);
        }

    }

    public interface Callback{
        void onFavouriteVendorClick(MenusResponse.Vendor favouriteVendor);
    }



}
