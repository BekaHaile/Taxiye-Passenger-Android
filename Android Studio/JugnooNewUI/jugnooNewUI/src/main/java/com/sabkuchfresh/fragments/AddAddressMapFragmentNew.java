package com.sabkuchfresh.fragments;//package com.sabkuchfresh.fragments;
//
//import android.app.Activity;
//import android.location.Location;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import product.clicklabs.jugnoo.Data;
//import com.sabkuchfresh.utils.LocationFetcher;
//import product.clicklabs.jugnoo.R;
//import com.sabkuchfresh.datastructure.AutoCompleteSearchResult;
//import com.sabkuchfresh.home.FreshActivity;
//import com.sabkuchfresh.utils.ASSL;
//import product.clicklabs.jugnoo.utils.AppConstant;
//import com.sabkuchfresh.utils.DialogPopup;
//import product.clicklabs.jugnoo.utils.Fonts;
//import product.clicklabs.jugnoo.utils.Log;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//
///**
// * Created by Gurmail S. Kang on 5/4/16.
// */
//public class AddAddressMapFragment extends Fragment implements LocationFetcher.LocationUpdate {
//
//    private GoogleMap googleMap;
//    RelativeLayout relative, layoutAddLocation;
//    Button getMyLocation;
//    TextView buttonAddLocation;
//    String PIN;
//    String City;
//    String Address1;
//    LatLng latlng;
//    String addressText;
//    boolean zoomedToMyLoc = false;
//    MarkerOptions marker;
//    ImageView imageViewBack;
//    Location currentLocation;
//    EditText editTextSearch;
//    TextView textVeiwSearch;
//    ProgressBar progressBarSearch;
//    ArrayList<AutoCompleteSearchResult> autoCompleteSearchResults = new ArrayList<AutoCompleteSearchResult>();
//    //    SearchListAdapter searchListAdapter;
//    ListView listViewSearch;
//
//    View rootView;
//    public FreshActivity homeActivity;
//
//    RelativeLayout relativeLayoutSearchBarText;
//    LinearLayout linearLayoutSearch;
//
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        rootView = inflater.inflate(R.layout.fragment_add_address, container, false);
//
//        homeActivity = (FreshActivity) getActivity();
//
//
////        searchListAdapter = new SearchListAdapter();
//
//        zoomedToMyLoc = false;
//
//        relative = (RelativeLayout) rootView.findViewById(R.id.root);
//        new ASSL(homeActivity, relative, 1134, 720, false);
//
//        setupUI(rootView.findViewById(R.id.root));
//
//        layoutAddLocation = (RelativeLayout) rootView.findViewById(R.id.layoutAddLocation);
//
//        buttonAddLocation = (TextView) rootView.findViewById(R.id.buttonAddLocation);
//        buttonAddLocation.setTypeface(Fonts.mavenRegular(homeActivity));
//
//        getMyLocation = (Button) rootView.findViewById(R.id.getMyLocation);
//
//        listViewSearch = (ListView) rootView.findViewById(R.id.listViewSearch);
////        listViewSearch.setAdapter(searchListAdapter);
//
//        linearLayoutSearch = (LinearLayout) rootView.findViewById(R.id.linearLayoutSearch);
//        linearLayoutSearch.setVisibility(View.GONE);
//        relativeLayoutSearchBarText = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutSearchBarText);
////        relativeLayoutSearchBarText.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                linearLayoutSearch.setVisibility(View.VISIBLE);
////                editTextSearch.requestFocus();
////                try {
////                    InputMethodManager imm = (InputMethodManager) homeActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
////                    imm.showSoftInput(editTextSearch, InputMethodManager.SHOW_IMPLICIT);
////                }catch(Exception e) {
////                    e.printStackTrace();
////                }
////                homeActivity.locationSearchShown = true;
////            }
////        });
//
//        progressBarSearch = (ProgressBar) rootView.findViewById(R.id.progressBarSearch);
//        progressBarSearch.setVisibility(View.GONE);
//        textVeiwSearch = (TextView) rootView.findViewById(R.id.textVeiwSearch);
//        textVeiwSearch.setTypeface(Fonts.mavenRegular(homeActivity));
//        editTextSearch = (EditText) rootView.findViewById(R.id.editTextSearch);
////        editTextSearch.addTextChangedListener(new TextWatcher() {
////
////            @Override
////            public void onTextChanged(CharSequence s, int start, int before, int count) {
////            }
////
////            @Override
////            public void beforeTextChanged(CharSequence s, int start, int count,
////                                          int after) {
////            }
////
////
////            @Override
////            public void afterTextChanged(Editable s) {
////                autoCompleteSearchResults.clear();
////                searchListAdapter.notifyDataSetChanged();
////                if(s.length() > 0){
////                    if(googleMap != null){
////                        getSearchResults(s.toString().trim(), googleMap.getCameraPosition().target);
////                    }
////                }
////            }
////        });
//
//
//
//
//        //homeActivity is when map gets initialized :)
//        initializeMap();
//
//        moveCamera();
//
//        getMyLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (currentLocation != null && googleMap != null) {
////                    FlurryEventLogger.event(FlurryText.checkout_add_pin_loc);
//                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
//                            new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).zoom(14).build();
//
//                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                } else {
//                    //Toast.makeText(homeActivity, Data.WAITING_FOR_LOCATION, Toast.LENGTH_SHORT).show();
//
////                    if(Utils.checkIfGooglePlayServicesAvailable(homeActivity)){
////                        LocationInit.showLocationAlertDialog(homeActivity);
////                    }
//
//                }
//            }
//        });
//
//        layoutAddLocation.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        latlng = googleMap.getCameraPosition().target;
//                        new AsyncGetAddress().execute();
//                        //homeActivity.openAddToAddressBook();
//                    }
//                });
//
//        return rootView;
//    }
//
//    public void myLocation() {
//
//        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//            @Override
//            public void onMyLocationChange(Location location) {
//                currentLocation = location;
//                getMyLocation.performClick();
//            }
//        });
//
//
//    }
//
//    class AsyncGetAddress extends AsyncTask<String, String, String> {
//
//        @Override
//        protected void onPreExecute() {
//            DialogPopup.showLoadingDialog(homeActivity, "Loading...");
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            addressText = reverseGeoCoding(latlng.latitude,
//                    latlng.longitude);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//
//
//            homeActivity.current_latitude = latlng.latitude;
//            homeActivity.current_longitude = latlng.longitude;
//
//            homeActivity.current_area = Address1;
//            homeActivity.current_city = City;
//            homeActivity.current_pincode = PIN;
//
//            DialogPopup.dismissLoadingDialog();
//
//            homeActivity.openAddToAddressBook();
//
////            LoadingBox.dismissLoadingDialog();
//
////            try {
////                if (homeActivity.current_action.equals(homeActivity.ADD_ADDRESS)) {
////                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
////                            .add(R.id.fragLayout, new AddToAddressBookFragment(), "AddToAddressBookFragment").addToBackStack("AddToAddressBookFragment")
////                            .hide(getActivity().getSupportFragmentManager().findFragmentByTag(getActivity().getSupportFragmentManager().getBackStackEntryAt(getActivity()
////                                    .getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
////                            .commit();
////                } else if (homeActivity.current_action.equals(homeActivity.EDIT_ADDRESS)) {
////
////
////                    AddToAddressBookFragment frag = (AddToAddressBookFragment) homeActivity.getSupportFragmentManager().findFragmentByTag("AddToAddressBookFragment");
////
////                    if (frag != null) {
////                        frag.updateAddress();
////                    }
////                    performBackPressed();
////
////                } else if (homeActivity.current_action.equals(homeActivity.UPDATE_ADDRESS)) {
////
////                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
////                            .add(R.id.fragLayout, new AddToAddressBookFragment(), "AddToAddressBookFragment").addToBackStack("AddToAddressBookFragment")
////                            .hide(getActivity().getSupportFragmentManager().findFragmentByTag(getActivity().getSupportFragmentManager().getBackStackEntryAt(getActivity()
////                                    .getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
////                            .commit();
////                }
////            }catch(Exception e) {
////                // java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
////                try {
////                    new Handler().post(new Runnable() {
////                        public void run() {
////                            if (homeActivity.current_action.equals(homeActivity.ADD_ADDRESS)) {
////                                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
////                                        .add(R.id.fragLayout, new AddToAddressBookFragment(), "AddToAddressBookFragment").addToBackStack("AddToAddressBookFragment")
////                                        .hide(getActivity().getSupportFragmentManager().findFragmentByTag(getActivity().getSupportFragmentManager().getBackStackEntryAt(getActivity()
////                                                .getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
////                                        .commit();
////                            } else if (homeActivity.current_action.equals(homeActivity.EDIT_ADDRESS)) {
////
////
////                                AddToAddressBookFragment frag = (AddToAddressBookFragment) homeActivity.getSupportFragmentManager().findFragmentByTag("AddToAddressBookFragment");
////
////                                if (frag != null) {
////                                    frag.updateAddress();
////                                }
////                                performBackPressed();
////
////                            } else if (homeActivity.current_action.equals(homeActivity.UPDATE_ADDRESS)) {
////
////                                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
////                                        .add(R.id.fragLayout, new AddToAddressBookFragment(), "AddToAddressBookFragment").addToBackStack("AddToAddressBookFragment")
////                                        .hide(getActivity().getSupportFragmentManager().findFragmentByTag(getActivity().getSupportFragmentManager().getBackStackEntryAt(getActivity()
////                                                .getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
////                                        .commit();
////                            }
////                        }
////                    });
////                } catch(Exception e1){
////                    //fragment not exist anymore
////                }
////            }
//
//        }
//    }
//
//    protected JSONObject getLocationInfo(double lat, double lng) {
//
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//
//            HttpGet httpGet = new HttpGet(AppConstant.REVERSE_GEO_CODING_URL + lat + "," + lng + "&sensor=true");
//            HttpClient client = new DefaultHttpClient();
//            HttpResponse response;
//            StringBuilder stringBuilder = new StringBuilder();
//
//            try {
//                response = client.execute(httpGet);
//                HttpEntity entity = response.getEntity();
//                InputStream stream = entity.getContent();
//                int b;
//                while ((b = stream.read()) != -1) {
//                    stringBuilder.append((char) b);
//                }
//            } catch (ClientProtocolException e) {
//
//            } catch (IOException e) {
//
//            }
//
//            JSONObject jsonObject = new JSONObject();
//            try {
//
//                Log.d("stringBuilder.toString()", "stringBuilder.toString() = " + stringBuilder.toString());
//                jsonObject = new JSONObject(stringBuilder.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            return jsonObject;
//        }
//        return null;
//    }
//
//    protected String reverseGeoCoding(double lat, double lng) {
//
//        //Here the JSON Object is retrieved via 'getLocationInfo' method
//        //The JSONObject is retrieved in 'jsonObj'
//        JSONObject jsonObj = getLocationInfo(lat, lng);
//        //Log.i("JSON string =>", jsonObj.toString());
//
//        Address1 = "";
//        String Address2 = "";
//        City = "";
//        String State = "";
//        String Country = "";
//        String County = "";
//        PIN = "";
//
//        String currentLocation = "";
//
//        try {
//            String status = jsonObj.getString("status").toString();
//            Log.i("status", status);
//
//            if (status.equalsIgnoreCase("OK")) {
//                JSONArray Results = jsonObj.getJSONArray("results");
//                JSONObject zero = Results.getJSONObject(0);
//                JSONArray address_components = zero.getJSONArray("address_components");
//
//                for (int i = 0; i < address_components.length(); i++) {
//                    JSONObject zero2 = address_components.getJSONObject(i);
//                    String long_name = zero2.getString("long_name");
//                    JSONArray mtypes = zero2.getJSONArray("types");
//                    String Type = mtypes.getString(0);
//
//                    if (Type.equalsIgnoreCase("street_number")) {
//                        //Address1 = long_name + " ";
//                    } else if (Type.equalsIgnoreCase("route")) {
//                        //Address1 = Address1 + long_name + " ";
//                    }  else if (Type.equalsIgnoreCase("sublocality_level_1")) {
//                        Address1 = long_name;
//                        //Address2 = long_name;
//                    }
//                    else if (Type.equalsIgnoreCase("sublocality_level_2")) {
//                        if(Address1.equalsIgnoreCase("")) {
//                            Address1 = long_name;
//                        }
//                        //Address2 = long_name;
//                    } else if (Type.equalsIgnoreCase("locality")) {
//                        // Address2 = Address2 + long_name + ", ";
//                        City = long_name;
//                    } else if (Type
//                            .equalsIgnoreCase("administrative_area_level_2")) {
//                        County = long_name;
//                    } else if (Type
//                            .equalsIgnoreCase("administrative_area_level_1")) {
//                        State = long_name;
//                    } else if (Type.equalsIgnoreCase("country")) {
//                        Country = long_name;
//                    } else if (Type.equalsIgnoreCase("postal_code")) {
//                        PIN = long_name;
//                    }
//
//                }
//
//                currentLocation = Address1 + "," + Address2 + "," + City + ","
//                        + State + "," + Country + "," + PIN;
//
//            }
//        } catch (Exception e) {
//
//        }
//        return currentLocation;
//
//    }
//
//
//    private void moveCamera() {
//        CameraPosition cameraPosition;
//        try {
//            cameraPosition = new CameraPosition.Builder().target(
//                    new LatLng(Data.locationFetcher.getLatitude(), Data.locationFetcher.getLongitude())).zoom(14).build();
//            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void initializeMap() {
//
//
//
//        if (googleMap == null) {
//
//            //googleMap = ((MapFragment) homeActivity.getFragmentManager().findFragmentById(R.id.addressmapView)).getMap();
//            //googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
//            try {
//                googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView)).getMap();
//
//                googleMap.setMyLocationEnabled(true);
//                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
//                googleMap.getUiSettings().setZoomControlsEnabled(false);
//                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//                    @Override
//                    public void onMyLocationChange(Location location) {
//                        currentLocation = location;
//                    }
//                });
//            }catch(Exception e) {
//                try {
//                    googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView)).getMap();
//                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
//                    googleMap.getUiSettings().setZoomControlsEnabled(false);
//                    googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//                        @Override
//                        public void onMyLocationChange(Location location) {
//                            currentLocation = location;
//                        }
//                    });
//
//                }catch(Exception e1) {
//                    e.printStackTrace();
//
//                }
//            }
//
//            // check if map is created successfully or not
//            if (googleMap == null) {
//                Toast.makeText(homeActivity,
//                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
//                        .show();
//            }
//
//        }
//    }
//
//
//
//
//    @Override
//    public void onDestroyView() {
//
//
//        super.onDestroyView();
//        destroyMap();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
////        try {
////            FlurryAgent.onStartSession(homeActivity, Data.flurryKey);
////        }catch(Exception e) {
////
////        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
////        try {
////            FlurryAgent.onEndSession(homeActivity);
////        }catch(Exception e) {
////
////        }
//    }
//
//    public void destroyMap(){
//
//        try {
//            SupportMapFragment suMapFrag = (SupportMapFragment) homeActivity.getSupportFragmentManager().findFragmentById(R.id.mapView);
//            if (suMapFrag != null)
//                homeActivity.getSupportFragmentManager().beginTransaction().remove(suMapFrag ).commit();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
////        homeActivity.resumeMethod();
//
//    }
//
//    public Thread autoCompleteThread;
//    public boolean refreshingAutoComplete = false;
//
//    public synchronized void getSearchResults(final String searchText, final LatLng latLng){
////        try {
////            if(!refreshingAutoComplete) {
////                progressBarSearch.setVisibility(View.VISIBLE);
////
////                if (autoCompleteThread != null) {
////                    autoCompleteThread.interrupt();
////                }
////
////                autoCompleteThread = new Thread(new Runnable() {
////                    @Override
////                    public void run() {
////                        refreshingAutoComplete = true;
////                        autoCompleteSearchResults.clear();
////                        autoCompleteSearchResults.addAll(MapUtils.getAutoCompleteSearchResultsFromGooglePlaces(searchText, latLng));
////                        setSearchResultsToList();
////                        refreshingAutoComplete = false;
////                        autoCompleteThread = null;
////                    }
////                });
////                autoCompleteThread.start();
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }
//
////    public synchronized void setSearchResultsToList(){
////        homeActivity.runOnUiThread(new Runnable() {
////            @Override
////            public void run() {
////                progressBarSearch.setVisibility(View.GONE);
////
////                if (autoCompleteSearchResults.size() == 0) {
////                    autoCompleteSearchResults.add(new AutoCompleteSearchResult("No results found", ""));
////                }
////                searchListAdapter.notifyDataSetChanged();
////            }
////        });
////    }
//
//
////    public synchronized void getSearchResultFromPlaceId(final String placeId){
////        progressBarSearch.setVisibility(View.VISIBLE);
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                SearchResult searchResult = MapUtils.getSearchResultsFromPlaceIdGooglePlaces(placeId);
////                setSearchResultToMapAndText(searchResult);
////            }
////        }).start();
////    }
////
////    public synchronized void setSearchResultToMapAndText(final SearchResult searchResult){
////        homeActivity.runOnUiThread(new Runnable() {
////            @Override
////            public void run() {
////                progressBarSearch.setVisibility(View.GONE);
////                autoCompleteSearchResults.clear();
////                searchListAdapter.notifyDataSetChanged();
////
////                closeLayout();
////                if (googleMap != null && searchResult != null) {
////                    textVeiwSearch.setText(searchResult.name);
////                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchResult.latLng, 14), 1000, null);
////                }
////            }
////        });
////    }
//
//
//
//    //    class ViewHolderSearchItem {
////        TextView textViewSearchName, textViewSearchAddress;
////        LinearLayout relative;
////        int id;
////    }
////
////    class ViewHolderAddress {
////        int pos;
////        RelativeLayout relative;
////        TextView name;
////    }
////
////    class SearchListAdapter extends BaseAdapter {
////
////        Context context;
////        LayoutInflater inflater = null;
////        ViewHolderAddress holder;
////
////        public SearchListAdapter() {
////
////            inflater = (LayoutInflater) homeActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////        }
////
////        public int getCount() {
////            return autoCompleteSearchResults.size();
////        }
////
////        public synchronized void refreshAdapter(ArrayList<AutoCompleteSearchResult> searchResult) {
////
////            autoCompleteSearchResults = searchResult;
////            notifyDataSetChanged();
////        }
////
////        public Object getItem(int position) {
////            return position;
////        }
////
////        public long getItemId(int position) {
////            return position;
////        }
////
////        public View getView(final int position, View convertView, ViewGroup parent) {
////            if (convertView == null) {
////                holder = new ViewHolderAddress();
////
////                convertView = inflater.inflate(R.layout.list_item_search, null);
////
////                holder.name = (TextView) convertView.findViewById(R.id.itemname);
////                holder.name.setTypeface(Fonts.latoRegular(homeActivity));
////
////                holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);
////
////                holder.relative.setLayoutParams(new ListView.LayoutParams(720, 80));
////                ASSL.DoMagic(holder.relative);
////
////                convertView.setTag(holder);
////            } else {
////                holder = (ViewHolderAddress) convertView.getTag();
////            }
////
////            holder.pos = position;
////            holder.name.setText(autoCompleteSearchResults.get(position).name);
////
////
////            holder.relative.setOnClickListener(new View.OnClickListener() {
////
////                @Override
////                public void onClick(View v) {
////                    holder = (ViewHolderAddress) v.getTag();
////                    hideSoftKeyboard();
////                    AutoCompleteSearchResult autoCompleteSearchResult = autoCompleteSearchResults.get(holder.pos);
////                    if (!"".equalsIgnoreCase(autoCompleteSearchResult.placeId)) {
////                        textVeiwSearch.setText(autoCompleteSearchResult.name);
////
////                        getSearchResultFromPlaceId(autoCompleteSearchResult.placeId);
////
////                    }
////                }
////            });
////
////
////            return convertView;
////        }
////
////        @Override
////        public void notifyDataSetChanged() {
////            if (autoCompleteSearchResults.size() > 1) {
////                if (autoCompleteSearchResults.contains(new AutoCompleteSearchResult("No results found", ""))) {
////                    autoCompleteSearchResults.remove(autoCompleteSearchResults.indexOf(new AutoCompleteSearchResult("No results found", "")));
////                }
////            }
////            super.notifyDataSetChanged();
////        }
////    }
//    public void hideSoftKeyboard() {
//        try {
//            if (homeActivity.getCurrentFocus() != null) {
//                InputMethodManager inputMethodManager = (InputMethodManager) homeActivity.getSystemService(homeActivity.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(homeActivity.getCurrentFocus().getWindowToken(), 0);
//            }
//        } catch (Exception e) {
//            // Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onLocationChanged(Location location, int priority) {
//        Data.latitude = location.getLatitude();
//        Data.longitude = location.getLongitude();
//    }
//
//
//    /**
//     * Method used to hide keyboard if outside touched.
//     *
//     * @param view
//     */
//    public void setupUI(View view) {
//        // Set up touch listener for non-text box views to hide keyboard.
//        if (!(view instanceof EditText)) {
//            view.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    try {
//                        InputMethodManager inputMethodManager = (InputMethodManager) homeActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//                        inputMethodManager.hideSoftInputFromWindow(homeActivity.getCurrentFocus().getWindowToken(), 0);
//                    } catch(Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    return false;
//                }
//
//            });
//        }
//        // If a layout container, iterate over children and seed recursion.
//        if (view instanceof ViewGroup) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                View innerView = ((ViewGroup) view).getChildAt(i);
//                setupUI(innerView);
//            }
//        }
//    }
//}
