package product.clicklabs.jugnoo.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Element;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.adapters.OrderItemsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.GridViewData;
import product.clicklabs.jugnoo.HomeSwitcherActivity;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.DiscountType;
import product.clicklabs.jugnoo.retrofit.model.HistoryResponse;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by socomo on 12/7/16.
 */

public class GridViewAdapter extends BaseAdapter
{
    private Context context;
    LayoutInflater gridViewLayout;
    ArrayList<String> gridList;
    public GridViewAdapter(Context context)
    {
        this.context = context;
    }

    public GridViewAdapter(Context context, ArrayList<String> gridList)
    {
        this.context = context;
        this.gridList = gridList;
        this.gridViewLayout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount()
    {
        return gridList.size();
    }
    @Override
    public Object getItem(int position)
    {
        return null;
    }
    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null)
        {
            convertView = gridViewLayout.inflate(R.layout.list_item_home_switcher_grid_view, null);
            holder.linearLayoutGridViewScreen = (LinearLayout) convertView.findViewById(R.id.linearLayoutGridViewScreen);
            holder.cardViewGridScreen = (CardView) convertView.findViewById(R.id.cardViewGridViewScreen);
            holder.imageViewGridScreen = (ImageView) convertView.findViewById(R.id.imageViewGridViewScreen);
            holder.textViewGridScreen = (TextView) convertView.findViewById(R.id.textViewGridViewScreen);
            holder.linearLayoutGridViewScreen.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ASSL.DoMagic(holder.linearLayoutGridViewScreen);
        }
        else
            holder = (GridViewAdapter.ViewHolder) convertView.getTag();
        try
        {
            holder.id = position;
            if((Data.userData.getFreshEnabled() == 0) && (Data.userData.getMealsEnabled() == 0)
                    && (Data.userData.getDeliveryEnabled() == 0) && (Data.userData.getGroceryEnabled() == 0) && (Data.userData.getDeliveryEnabled() == 0))
            {
                holder.linearLayoutGridViewScreen.setVisibility(View.GONE);
            }
            else
            {
                holder.linearLayoutGridViewScreen.setVisibility(View.VISIBLE);

                holder.imageViewGridScreen.setImageResource(R.drawable.home_switcher_auto);
                holder.textViewGridScreen.setText(R.string.rides);


                if(gridList.get(position).equalsIgnoreCase(Config.getFreshClientId()))
                {
                    holder.imageViewGridScreen.setImageResource(R.drawable.ic_fab_fresh);
                    holder.textViewGridScreen.setText(R.string.fresh);
                }
                if(gridList.get(position).equalsIgnoreCase(Config.getMealsClientId()))
                {
                    holder.imageViewGridScreen.setImageResource(R.drawable.ic_fab_meals);
                    holder.textViewGridScreen.setText(R.string.meals);
                }
                if(gridList.get(position).equalsIgnoreCase(Config.getGroceryClientId()))
                {
                    holder.imageViewGridScreen.setImageResource(R.drawable.ic_fab_grocery);
                    holder.textViewGridScreen.setText(R.string.grocery);
                }
                if(gridList.get(position).equalsIgnoreCase(Config.getDeliveryClientId()))
                {
                    holder.imageViewGridScreen.setImageResource(R.drawable.ic_fab_grocery);
                    holder.textViewGridScreen.setText(R.string.delivery);
                }
             /*   if(gridViewData.getClientId()== Config.getPayNowClientId())
                {
                    holder.imageViewGridScreen.setImageResource(R.drawable.ic_fab_pay_now);
                    holder.textViewGridScreen.setText(R.string.paynow);
                }*/


                holder.linearLayoutGridViewScreen.setTag(holder);
                holder.linearLayoutGridViewScreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = ((ViewHolder)v.getTag()).id;

                            double latitude = (((HomeSwitcherActivity)context).getIntent().getDoubleExtra(Constants.KEY_LATITUDE, LocationFetcher.getSavedLatFromSP((context))));
                            double longitude = (((HomeSwitcherActivity)context).getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, LocationFetcher.getSavedLngFromSP((context))));
                            Bundle bundle = ((HomeSwitcherActivity)context).getIntent().getBundleExtra(Constants.KEY_APP_SWITCH_BUNDLE);
                            MyApplication.getInstance().getAppSwitcher().switchApp(((HomeSwitcherActivity)context),  gridList.get(pos), ((HomeSwitcherActivity)context).getIntent().getData(),
                                    new LatLng(latitude, longitude), bundle, false, false, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }


    long duration = 500;
    long bounceDuration = 200;
    boolean animStarted = false;
    private void startInnerAnim(final String clientId){
        animStarted = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Prefs.with(context).save("home_switcher_client_id", clientId);

                double latitude = (((HomeSwitcherActivity)context).getIntent().getDoubleExtra(Constants.KEY_LATITUDE, LocationFetcher.getSavedLatFromSP((context))));
                double longitude = (((HomeSwitcherActivity)context).getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, LocationFetcher.getSavedLngFromSP((context))));
                Bundle bundle = ((HomeSwitcherActivity)context).getIntent().getBundleExtra(Constants.KEY_APP_SWITCH_BUNDLE);
                MyApplication.getInstance().getAppSwitcher().switchApp(((HomeSwitcherActivity)context), clientId, ((HomeSwitcherActivity)context).getIntent().getData(),
                        new LatLng(latitude, longitude), bundle, false, false, true);
            }
        }, duration+bounceDuration+75);

    }


    static class ViewHolder {
        public int id;
        public LinearLayout linearLayoutGridViewScreen;
        public CardView cardViewGridScreen;

        public ImageView imageViewGridScreen;
        public TextView textViewGridScreen;

    }
}
