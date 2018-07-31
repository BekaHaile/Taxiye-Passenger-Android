package com.sabkuchfresh.feed.ui.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.sabkuchfresh.adapters.ItemListener;
import com.sabkuchfresh.feed.models.feedcitiesresponse.FeedCity;
import com.sabkuchfresh.home.FreshActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import product.clicklabs.jugnoo.R;


/**
 * Created by Shankar on 3/31/17.
 */
public class FeedDisplayCitiesAdapter extends RecyclerView.Adapter<FeedDisplayCitiesAdapter.DisplayCitiesViewHolder> implements ItemListener, Filterable {

    private final boolean isTrendingCities;
    private FreshActivity activity;
    private ArrayList<FeedCity> feedCities;
    private ArrayList<FeedCity> feedCitiesFiltered;
    private Callback callback;
    private RecyclerView recyclerView;
    private View disabledViewBehindSearchRecycler;


    public FeedDisplayCitiesAdapter(FreshActivity activity, ArrayList<FeedCity> feedCitiesList, RecyclerView recyclerView, Callback callback, boolean isTrendingCities,View disabledView) {
        this.activity = activity;
        this.isTrendingCities = isTrendingCities;
        if (this.isTrendingCities) {
            feedCitiesFiltered = feedCitiesList;
        } else {
            this.feedCities = feedCitiesList;
            feedCitiesFiltered = new ArrayList<>();

        }
        this.recyclerView = recyclerView;
        this.callback = callback;
        this.disabledViewBehindSearchRecycler=disabledView;
    }

    public void setList(ArrayList<FeedCity> feedCitiesList) {
        if (this.isTrendingCities) {
            feedCitiesFiltered = feedCitiesList;
        } else {
            this.feedCities = feedCitiesList;


        }
        notifyDataSetChanged();
        toggleVisibilty();
    }

    @Override
    public DisplayCitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_trending_cities, parent, false);
        return new DisplayCitiesViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(DisplayCitiesViewHolder holder, int position) {
        holder.tvCityName.setText(feedCitiesFiltered.get(position).getCityName());
        if (position == feedCitiesFiltered.size() - 1) {
            holder.tvCityName.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.background_white_grey_light_feed_selector));
        } else {
            holder.tvCityName.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bottom_border_grey));
        }

    }

    @Override
    public int getItemCount() {
        int size = feedCitiesFiltered == null ? 0 : feedCitiesFiltered.size();
        toggleVisibilty();
        return size;
    }

    private void toggleVisibilty() {
        if (feedCitiesFiltered == null || feedCitiesFiltered.size() == 0) {
            if (recyclerView.getVisibility() == View.VISIBLE){
                recyclerView.setVisibility(View.GONE);
                if(!isTrendingCities){
                    disabledViewBehindSearchRecycler.setVisibility(View.GONE);
                }
            }
        } else {
            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
                if(!isTrendingCities){
                    disabledViewBehindSearchRecycler.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onClickItem(View viewClicked, View parentView) {
        int pos = recyclerView.getChildLayoutPosition(parentView);
        if (pos != RecyclerView.NO_POSITION) {
            switch (viewClicked.getId()) {
                case R.id.tv_city_name:
                    callback.onCityClick(pos, feedCitiesFiltered.get(pos));
                    break;
            }
        }
    }


    public interface Callback {
        void onCityClick(int position, FeedCity notificationDatum);
    }

    static class DisplayCitiesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_city_name)
        TextView tvCityName;

        DisplayCitiesViewHolder(View view, final ItemListener itemListener) {
            super(view);
            ButterKnife.bind(this, view);
            tvCityName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClickItem(tvCityName,v);
                }
            });
        }
    }

    private class FeedCityFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            feedCitiesFiltered.clear();

            if (feedCities != null && feedCities.size() > 0 && constraint.toString().trim().length() != 0) {
                for (FeedCity feedCity : feedCities) {
                    if (feedCity.getCityName().toLowerCase().contains(constraint.toString().trim().toLowerCase())) {
                        feedCitiesFiltered.add(feedCity);
                    }
                }
            }

            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifyDataSetChanged();
            toggleVisibilty();
        }
    }

    private FeedCityFilter feedCityFilter;

    @Override
    public Filter getFilter() {
        if (feedCityFilter == null) {
            feedCityFilter = new FeedCityFilter();
        }
        return feedCityFilter;
    }


}
