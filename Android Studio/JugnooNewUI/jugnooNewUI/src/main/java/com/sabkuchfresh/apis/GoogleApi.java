package com.sabkuchfresh.apis;

import com.sabkuchfresh.datastructure.GoogleGeocodeResponse;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Class for Google Location APIs
 * Created by vinaysshenoy on 23/10/14.
 */
public interface GoogleApi {

    //String address = String.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=false&language=" + Locale.getDefault().getCountry(), lat, lng);

    //get location from the google geocode api
    @GET("/geocode/json")
    void getMyAddress(@QueryMap Map<String, String> params, Callback<GoogleGeocodeResponse> cb);

//    //get location from the google geocode api
//    @GET("/place/autocomplete/json")
//    void getAddressList(@QueryMap Map<String, String> params, Callback<GetPlacesModel> cb);
//
//    //get location from the google geocode api
//    @GET("/place/autocomplete/json")
//    GetPlacesModel getAddressListModel(@QueryMap Map<String, String> params);
//
//    //get  full location from the google geocode api
//    @GET("/place/details/json")
//    void getFullAddress(@QueryMap Map<String, String> params, Callback<GetClickedPlaceModel> cb);

}
