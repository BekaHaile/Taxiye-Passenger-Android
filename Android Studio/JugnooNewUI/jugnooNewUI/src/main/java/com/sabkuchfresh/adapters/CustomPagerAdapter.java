package com.sabkuchfresh.adapters;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sabkuchfresh.retrofit.model.SuperCategoriesData;
import com.squareup.picasso.Picasso;

import java.util.List;

import product.clicklabs.jugnoo.R;

/**
 * Created by ankit on 22/03/17.
 */

public class CustomPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    private List<SuperCategoriesData.SuperCategory> offerList;
    private Callback callback;

    public CustomPagerAdapter(Context context, List<SuperCategoriesData.SuperCategory> offerList, Callback callback) {
        mContext = context;
        this.offerList = offerList;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return offerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    public synchronized void setList(List<SuperCategoriesData.SuperCategory> mResources){
        this.offerList = mResources;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.item_pager_promo, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.ivPromo);
        Picasso.with(mContext).load(offerList.get(position).getImageUrl())
                .placeholder(R.drawable.ic_fresh_new_placeholder)
                .error(R.drawable.ic_fresh_new_placeholder)
                .fit()
                .into(imageView);

        container.addView(itemView);

        imageView.setTag(position);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int pos = (int) v.getTag();
                    callback.onOfferClick(pos, offerList.get(pos));
                } catch (Exception e){}
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    public interface Callback{
        void onOfferClick(int pos, SuperCategoriesData.SuperCategory superCategory);
    }
}
