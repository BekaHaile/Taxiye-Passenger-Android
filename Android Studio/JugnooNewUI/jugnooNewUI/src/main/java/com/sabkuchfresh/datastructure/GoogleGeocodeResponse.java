package com.sabkuchfresh.datastructure;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;

/**
 * Created by anshul1235 on 29/07/14.
 */
public class GoogleGeocodeResponse {


    @SerializedName("results")
    public List<Results> results;
    @SerializedName("error_message")
    private String errorMessage;
    @SerializedName("status")
    private String status;

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class Results {

        @SerializedName("formatted_address")
        public String formatted_address;

        @SerializedName("address_components")
        public List<AddressComponent> addressComponents = new ArrayList<AddressComponent>();
        @SerializedName("place_id")
        private String placeId;
        @SerializedName("types")
        @Expose
        private List<String> types = new ArrayList<String>();
        private String placeName;

        /**
         * Index
         * 0=locality
         * 1=city
         * 2=state
         * 3=country
         */

        public String[] getAddress() {
            String[] address = TextUtils.split(this.formatted_address, ",");
            String[] addressSplit = new String[4];
            int length = address.length;
            //gives country
            addressSplit[3] = address[length - 1];
            //gives state
            if (length - 2 >= 0)
                addressSplit[2] = address[length - 2];
            else {
                addressSplit[2] = address[length - 1];
            }
            //gives city
            if (length - 3 >= 0)
                addressSplit[1] = address[length - 3];
            else if (length - 2 >= 0) {
                addressSplit[1] = address[length - 2];
            } else {
                addressSplit[1] = address[length - 1];
            }
            //gives locality
            if (length - 4 >= 0)
                addressSplit[0] = address[length - 4];
            else if (length - 3 >= 0) {
                addressSplit[0] = address[length - 3];
            } else if (length - 2 >= 0) {
                addressSplit[0] = address[length - 2];
            } else {
                addressSplit[0] = address[length - 1];
            }

            return addressSplit;
        }

        public String getAddAddress() {
            String neighborhood = "", city = "", locality = "",locality1 = "",sublocality = "", state = "";
            if(addressComponents.size()>0) {
                for(int i=0;i<addressComponents.size(); i++) {
                    ArrayList<String> addressTypes = new ArrayList<String>();
                    for (int j = 0; j < addressComponents.get(i).types.size(); j++) {
                        addressTypes.add(addressComponents.get(i).types.get(j));
                    }
//                    if (addressTypes.contains("sublocality_level_2")) {
//                        city = addressComponents.get(i).longName;
//                    }
                    if (addressTypes.contains("sublocality_level_1")) {
                        locality = addressComponents.get(i).longName;
                    }
                    if (addressTypes.contains("locality")) {
                        state = addressComponents.get(i).longName;
                    }
                    if(addressTypes.contains("administrative_area_level_2")) {
                        locality1 = addressComponents.get(i).longName;
                    }
                    if (addressTypes.contains("administrative_area_level_1")) {
                        sublocality = addressComponents.get(i).longName;
                    }

                }
                if(!TextUtils.isEmpty(city)) {
                    return city;
                } else if(!TextUtils.isEmpty(locality)) {
                    return locality;
                } else if(!TextUtils.isEmpty(state)) {
                    return state;
                } else if(!TextUtils.isEmpty(locality1)) {
                    return locality1;
                } else if(!TextUtils.isEmpty(sublocality)) {
                    return sublocality;
                } else {
                    return MyApplication.getInstance().getString(R.string.country_name);
                }
            } else {
                String[] address = getAddress();
                city = address[address.length - 3].trim();
            }
            return city;
        }

        public String getLocality() {
            String neighborhood = "", city = "", locality = "",sublocality = "", state = "";
            if(addressComponents.size()>0) {
                for(int i=0;i<addressComponents.size(); i++) {
                    ArrayList<String> addressTypes = new ArrayList<String>();
                    for (int j = 0; j < addressComponents.get(i).types.size(); j++) {
                        addressTypes.add(addressComponents.get(i).types.get(j));
                    }
//                    if (addressTypes.contains("neighborhood")) {
//                        neighborhood = addressComponents.get(i).longName;
//                    }
                    if (addressTypes.contains("locality")) {
                        city = addressComponents.get(i).longName;
                    }
                    if (addressTypes.contains("sublocality")) {
                        locality = addressComponents.get(i).longName;
                    }
                    if (addressTypes.contains("administrative_area_level_2")) {
                        state = addressComponents.get(i).longName;
                    }
                    if (addressTypes.contains("administrative_area_level_1")) {
                        sublocality = addressComponents.get(i).longName;
                    }

                }
                if(!TextUtils.isEmpty(neighborhood)) {
                    return neighborhood;
                } else if(!TextUtils.isEmpty(locality)) {
                    return locality;
                } else if(!TextUtils.isEmpty(city)) {
                    return city;
                }else if(!TextUtils.isEmpty(state)) {
                    return state;
                } else if(!TextUtils.isEmpty(sublocality)) {
                    return sublocality;
                } else {
                    return MyApplication.getInstance().getString(R.string.country_name);
                }
            } else {
                String[] address = getAddress();
                city = address[address.length - 3].trim();
            }
            return city;
        }

        public String getPin() {
            String pin = "";
            if(addressComponents.size()>0) {
                for(int i=0;i<addressComponents.size(); i++) {
                    ArrayList<String> addressTypes = new ArrayList<String>();
                    for (int j = 0; j < addressComponents.get(i).types.size(); j++) {
                        addressTypes.add(addressComponents.get(i).types.get(j));
                    }
                    if (addressTypes.contains("postal_code")) {
                        pin = addressComponents.get(i).longName;
                    }
                }

            }

            return pin;
        }

        public String getCity() {
            String city = "", locality = "", state = "";
            if(addressComponents.size()>0) {
                for(int i=0;i<addressComponents.size(); i++) {
                    ArrayList<String> addressTypes = new ArrayList<String>();
                    for (int j = 0; j < addressComponents.get(i).types.size(); j++) {
                        addressTypes.add(addressComponents.get(i).types.get(j));
                    }
                    if (addressTypes.contains("locality")) {
                        city = addressComponents.get(i).longName;
                    }
                    if (addressTypes.contains("sublocality")) {
                        locality = addressComponents.get(i).longName;
                    }
                    if (addressTypes.contains("administrative_area_level_2")) {
                        state = addressComponents.get(i).longName;
                    }

                }
                if(!TextUtils.isEmpty(city)) {
                    return city;
                } else if(!TextUtils.isEmpty(state)) {
                    return state;
                } else if(!TextUtils.isEmpty(locality)) {
                    return locality;
                } else {
                    return "";
                }
            } else {
                String[] address = getAddress();
                city = address[address.length - 3].trim();
            }
            return city;
        }

        public String getStreetNumber() {
            String streetNumber = "";
            if(addressComponents.size()>0) {
                for(int i=0;i<addressComponents.size(); i++) {
                    ArrayList<String> addressTypes = new ArrayList<String>();
                    for (int j = 0; j < addressComponents.get(i).types.size(); j++) {
                        addressTypes.add(addressComponents.get(i).types.get(j));
                    }
                    if (addressTypes.contains("street_number")) {
                        streetNumber = addressComponents.get(i).longName;
                    }


                }
                if(!TextUtils.isEmpty(streetNumber)) {
                    return streetNumber+"";
                } else {
                    return "";
                }
            } else {
                String[] address = TextUtils.split(this.formatted_address, ",");
                streetNumber = address[0].trim()+"";
            }
            return streetNumber;
        }

        public String getRoute() {
            String route = "";
            if(addressComponents.size()>0) {
                for(int i=0;i<addressComponents.size(); i++) {
                    ArrayList<String> addressTypes = new ArrayList<String>();
                    for (int j = 0; j < addressComponents.get(i).types.size(); j++) {
                        addressTypes.add(addressComponents.get(i).types.get(j));
                    }
                    if (addressTypes.contains("route")) {
                        route = addressComponents.get(i).longName;
                    }


                }
                if(!TextUtils.isEmpty(route)) {
                    return route+"";
                } else {
                    return "";
                }
            } else {
                String[] address = TextUtils.split(this.formatted_address, ",");
                route = address[1].trim()+"";
            }
            return route;
        }

        public String getState() {
            String city = "", locality = "",sublocality = "", state = "";
            if(addressComponents.size()>0) {
                for(int i=0;i<addressComponents.size(); i++) {
                    ArrayList<String> addressTypes = new ArrayList<String>();
                    for (int j = 0; j < addressComponents.get(i).types.size(); j++) {
                        addressTypes.add(addressComponents.get(i).types.get(j));
                    }
                    if (addressTypes.contains("locality")) {
                        city = addressComponents.get(i).longName;
                    }
                    if (addressTypes.contains("sublocality")) {
                        locality = addressComponents.get(i).longName;
                    }
                    if (addressTypes.contains("administrative_area_level_2")) {
                        state = addressComponents.get(i).longName;
                    }
                    if (addressTypes.contains("administrative_area_level_1")) {
                        sublocality = addressComponents.get(i).longName;
                    }

                }
                if(!TextUtils.isEmpty(city)) {
                    return city;
                } else if(!TextUtils.isEmpty(locality)) {
                    return locality;
                } else if(!TextUtils.isEmpty(state)) {
                    return state;
                } else if(!TextUtils.isEmpty(sublocality)) {
                    return sublocality;
                } else {
                    return MyApplication.getInstance().getString(R.string.country_name);
                }
            } else {
                String[] address = getAddress();
                city = address[address.length - 3].trim();
            }
            return city;
        }

        public String getCountry() {
            String country = "", political = "";
            if(addressComponents.size()>0) {
                for(int i=0;i<addressComponents.size(); i++) {
                    ArrayList<String> addressTypes = new ArrayList<String>();
                    for (int j = 0; j < addressComponents.get(i).types.size(); j++) {
                        addressTypes.add(addressComponents.get(i).types.get(j));
                    }
                    if (addressTypes.contains("country")) {
                        country = addressComponents.get(i).longName;
                    }
                    if (addressTypes.contains("political")) {
                        political = addressComponents.get(i).longName;
                    }
                }
                if(!TextUtils.isEmpty(country)) {
                    return country;
                } else if(!TextUtils.isEmpty(political)) {
                    return political;
                } else {
                    return MyApplication.getInstance().getString(R.string.country_name);
                }
            } else {
                String[] address = getAddress();
                country = address[address.length - 1].trim();
            }
            return country;
        }

        public String getPlaceId() {
            return placeId;
        }

        public List<String> getTypes() {
            return types;
        }

        public String getPlaceName() {
            return placeName;
        }

        public void setPlaceName(String placeName) {
            this.placeName = placeName;
        }
    }

    public class AddressComponent {

        @SerializedName("long_name")
        @Expose
        public String longName;
        @SerializedName("types")
        @Expose
        public List<String> types = new ArrayList<String>();
        public boolean redundant;


    }

}
