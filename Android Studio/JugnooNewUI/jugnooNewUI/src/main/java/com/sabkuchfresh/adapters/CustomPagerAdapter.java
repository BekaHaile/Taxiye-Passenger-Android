package com.sabkuchfresh.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;

/**
 * Created by ankit on 22/03/17.
 */

public class CustomPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    private ArrayList<String> mResources;

    public CustomPagerAdapter(Context context, ArrayList<String>mResources) {
        mContext = context;
        this.mResources = mResources;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.item_pager_promo, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.ivPromo);
        Picasso.with(mContext).load(mResources.get(position))
                .placeholder(R.drawable.ic_fresh_new_placeholder)
                .error(R.drawable.ic_fresh_new_placeholder)
                .fit()
                .into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
