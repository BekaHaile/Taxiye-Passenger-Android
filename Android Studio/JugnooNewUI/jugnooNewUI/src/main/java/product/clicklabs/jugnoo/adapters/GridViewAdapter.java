package product.clicklabs.jugnoo.adapters;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.HomeSwitcherActivity;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;

/**
 * Created by socomo on 12/7/16.
 */

public class GridViewAdapter extends BaseAdapter
{
    private Context context;
    LayoutInflater gridViewLayout;
    ArrayList<String> gridList;
    private Callback callback;

    public GridViewAdapter(Context context, ArrayList<String> gridList, Callback callback)
    {
        this.context = context;
        this.gridList = gridList;
        this.gridViewLayout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.callback = callback;
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
            holder.cardViewGridScreen = (LinearLayout) convertView.findViewById(R.id.cardViewGridViewScreen);
            holder.imageViewGridScreen = (ImageView) convertView.findViewById(R.id.imageViewGridViewScreen);
            holder.textViewGridScreen = (TextView) convertView.findViewById(R.id.textViewGridViewScreen);
            holder.linearLayoutGridViewScreen.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
            ASSL.DoMagic(holder.linearLayoutGridViewScreen);
        }
        else
            holder = (GridViewAdapter.ViewHolder) convertView.getTag();
        try
        {
            holder.id = position;

            Log.v("gridListSize ","gridListSize "+gridList.size());

          /*  if((Data.userData.getFreshEnabled() == 0) && (Data.userData.getMealsEnabled() == 0)
                    && (Data.userData.getGroceryEnabled() == 0)
                    && (Data.userData.getMenusEnabled() == 0) && (Data.userData.getPayEnabled() == 0))
            {
                holder.linearLayoutGridViewScreen.setVisibility(View.GONE);
            }
            else*/
            {

                    holder.linearLayoutGridViewScreen.setVisibility(View.VISIBLE);
                    holder.cardViewGridScreen.setBackgroundResource(R.drawable.circle_border_auto_selector);
                    holder.imageViewGridScreen.setImageResource(R.drawable.ic_auto_grey);
                    holder.textViewGridScreen.setText(R.string.rides);

                    if (gridList.get(position).equalsIgnoreCase(Config.getMealsClientId())) {
                        holder.cardViewGridScreen.setBackgroundResource(R.drawable.circle_border_meals_selector);
                        holder.imageViewGridScreen.setImageResource(R.drawable.ic_meals_grey);
                        holder.textViewGridScreen.setText(R.string.meals);
                    }
                    if (gridList.get(position).equalsIgnoreCase(Config.getFreshClientId())) {
                        holder.cardViewGridScreen.setBackgroundResource(R.drawable.circle_border_fresh_selector);
                        holder.imageViewGridScreen.setImageResource(R.drawable.ic_fresh_grey);
                        holder.textViewGridScreen.setText(R.string.fresh);
                    }
                    if (gridList.get(position).equalsIgnoreCase(Config.getGroceryClientId())) {
                        holder.cardViewGridScreen.setBackgroundResource(R.drawable.circle_border_grocery_selector);
                        holder.imageViewGridScreen.setImageResource(R.drawable.ic_fresh_grey);
                        holder.textViewGridScreen.setText(R.string.grocery);
                    }
                    if (gridList.get(position).equalsIgnoreCase(Config.getMenusClientId())) {
                        holder.cardViewGridScreen.setBackgroundResource(R.drawable.circle_border_menus_selector);
                        holder.imageViewGridScreen.setImageResource(R.drawable.ic_menus_grey);
                        holder.textViewGridScreen.setText(R.string.menus);
                    }
                    if (gridList.get(position).equalsIgnoreCase(Config.getPayClientId())) {
                        holder.cardViewGridScreen.setBackgroundResource(R.drawable.circle_border_pay_selector);
                        holder.imageViewGridScreen.setImageResource(R.drawable.ic_pay_grey);
                        holder.textViewGridScreen.setText(R.string.pay);
                    }

/*
                if(gridList.get(position).equalsIgnoreCase(Config.getDeliveryClientId()))
                {
                    holder.cardViewGridScreen.setBackgroundResource(R.drawable.circle_border_delievery_selector);
                    holder.imageViewGridScreen.setImageResource(R.drawable.ic_fab_delivery);
                    holder.textViewGridScreen.setText(R.string.delivery);
                }
*/

                holder.linearLayoutGridViewScreen.setTag(holder);
                holder.linearLayoutGridViewScreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int pos = ((ViewHolder)v.getTag()).id;

                            double latitude = (((HomeSwitcherActivity)context).getIntent().getDoubleExtra(Constants.KEY_LATITUDE, callback.getLocation().getLatitude()));
                            double longitude = (((HomeSwitcherActivity)context).getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, callback.getLocation().getLongitude()));
                            Bundle bundle = ((HomeSwitcherActivity)context).getIntent().getBundleExtra(Constants.KEY_APP_SWITCH_BUNDLE);
                            MyApplication.getInstance().getAppSwitcher().switchApp(((HomeSwitcherActivity)context),  gridList.get(pos),
                                    ((HomeSwitcherActivity)context).getIntent().getData(),
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
                try {
                    Prefs.with(context).save("home_switcher_client_id", clientId);

                    double latitude = (((HomeSwitcherActivity)context).getIntent().getDoubleExtra(Constants.KEY_LATITUDE, callback.getLocation().getLatitude()));
                    double longitude = (((HomeSwitcherActivity)context).getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, callback.getLocation().getLongitude()));
                    Bundle bundle = ((HomeSwitcherActivity)context).getIntent().getBundleExtra(Constants.KEY_APP_SWITCH_BUNDLE);
                    MyApplication.getInstance().getAppSwitcher().switchApp(((HomeSwitcherActivity)context), clientId, ((HomeSwitcherActivity)context).getIntent().getData(),
							new LatLng(latitude, longitude), bundle, false, false, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, duration+bounceDuration+75);

    }


    static class ViewHolder {
        public int id;
        public LinearLayout linearLayoutGridViewScreen;
        public LinearLayout cardViewGridScreen;

        public ImageView imageViewGridScreen;
        public TextView textViewGridScreen;

    }

    public interface Callback{
        Location getLocation();
    }
}
