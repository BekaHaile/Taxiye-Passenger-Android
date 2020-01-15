package com.sabkuchfresh.feed.ui.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sabkuchfresh.feed.models.feedcitiesresponse.FeedCity;
import com.sabkuchfresh.feed.models.feedcitiesresponse.FeedCityResponse;
import com.sabkuchfresh.feed.ui.adapters.FeedDisplayCitiesAdapter;
import com.sabkuchfresh.feed.ui.api.APICommonCallback;
import com.sabkuchfresh.feed.ui.api.ApiCommon;
import com.sabkuchfresh.feed.ui.api.ApiName;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.utils.AppConstant;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import product.clicklabs.jugnoo.R;
import retrofit.RetrofitError;

/**
 * Created by Parminder Singh on 4/10/17.
 */

public class FeedChangeCityFragment extends FeedBaseFragment {


    @BindView(R.id.edt_city_name)
    EditText edtCityName;
    @BindView(R.id.tv_label_or)
    TextView tvLabelOr;
    @BindView(R.id.btn_use_current_location)
    Button btnUseCurrentLocation;
    @BindView(R.id.layout_use_current_location)
    LinearLayout layoutUseCurrentLocation;
    @BindView(R.id.label_trending_cities)
    TextView labelTrendingCities;
    @BindView(R.id.rl_trending_cities)
    RecyclerView rlTrendingCities;
    @BindView(R.id.rl_search_results)
    RecyclerView rlSearchResults;
    @BindView(R.id.blur_view)
    View blurView;
    private FeedDisplayCitiesAdapter searchCitiesAdapter;
    private FeedDisplayCitiesAdapter trendingCitiesAdapter;

    Unbinder unbinder;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_feed_select_city, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        //Search Cities
        searchCitiesAdapter = new FeedDisplayCitiesAdapter((FreshActivity) getActivity(), null, rlSearchResults, new FeedDisplayCitiesAdapter.Callback() {
            @Override
            public void onCityClick(int position, FeedCity feedCity) {
                setCityLatLngAndRefreshFeed(feedCity.getLatitude(),feedCity.getLongitude());
            }
        }, false,blurView);
        edtCityName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchCitiesAdapter.getFilter().filter(s);

            }
        });
        rlSearchResults.setLayoutManager(new LinearLayoutManager(getActivity()));
        rlSearchResults.setAdapter(searchCitiesAdapter);


         //Trending Cities

        trendingCitiesAdapter = new FeedDisplayCitiesAdapter((FreshActivity) getActivity(), null, rlTrendingCities, new FeedDisplayCitiesAdapter.Callback() {
            @Override
            public void onCityClick(int position, FeedCity feedCity) {
                setCityLatLngAndRefreshFeed(feedCity.getLatitude(),feedCity.getLongitude());
            }
        }, true,blurView);

        rlTrendingCities.setLayoutManager(new LinearLayoutManager(getActivity()));
        rlTrendingCities.setAdapter(trendingCitiesAdapter);

        getCityAPI();

        return rootView;

    }

    private void setCityLatLngAndRefreshFeed(Double lat,Double lng) {
        if(lat==null || lng==null){
            activity.setSelectedLatLng(null);

        }else{
            activity.setSelectedLatLng(new LatLng(lat,lng));
        }
        activity.setSelectedAddress("");
        activity.setSelectedAddressId(0);
        activity.setSelectedAddressType("");
        activity.saveOfferingLastAddress(AppConstant.ApplicationType.FEED);

        activity.performBackPressed(false);
        activity.setLocalityAddressFirstTime(AppConstant.ApplicationType.FEED);

    }

    private void getCityAPI() {
        new ApiCommon<FeedCityResponse>(getActivity()).putAccessToken(true).execute(new HashMap<String, String>(), ApiName.CITY_INFO_API, new APICommonCallback<FeedCityResponse>() {
            @Override
            public boolean onNotConnected() {
                return false;
            }

            @Override
            public boolean onException(Exception e) {
                return false;
            }

            @Override
            public void onSuccess(FeedCityResponse feedCityResponse, String message, int flag) {
                searchCitiesAdapter.setList(feedCityResponse.getTotalCities());
                trendingCitiesAdapter.setList(feedCityResponse.getTrendingCities());
                labelTrendingCities.setVisibility(feedCityResponse.getTrendingCities()==null || feedCityResponse.getTrendingCities().size()==0?View.GONE:View.VISIBLE);
            }

            @Override
            public boolean onError(FeedCityResponse feedCityResponse, String message, int flag) {
                return false;
            }

            @Override
            public boolean onFailure(RetrofitError error) {
                return false;
            }

            @Override
            public void onNegativeClick() {
                activity.performBackPressed(false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_use_current_location)
    public void onClick() {
        setCityLatLngAndRefreshFeed(null,null);
    }


}
