package sabkuchfresh.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sabkuchfresh.R;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.home.FreshActivity;
import com.sabkuchfresh.utils.ASSL;
import com.sabkuchfresh.utils.AppConstant;
import com.sabkuchfresh.utils.Fonts;
import com.sabkuchfresh.utils.KeyboardLayoutListener;
import com.sabkuchfresh.utils.Log;
import com.sabkuchfresh.utils.Prefs;
import com.squareup.otto.Bus;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Gurmail S. Kang on 5/4/16.
 */
public class AddToAddressBookFragment extends Fragment {


    private GoogleMap googleMap;
    MarkerOptions marker;

    Button buttonAddToAddressBook;
    RelativeLayout root;

    ImageView imageViewEditHouseNumber, imageViewEditBuilding, imageViewEditArea;
    String homeAddressReceived, buildingStreetReceived, areaReceived, cityValueReceived,
            pinCodeReceived, cityValue, pinCodeValue, selectedAddressPosition;
    Double currentLatitude = 30.215462, currentLongitude = 72.521462;
    EditText houseNumber, buildingStreetName, area, city, pinCode;
    Bundle extras;

    String City, PIN;
    double latitude, longitude;

    View rootView;
    public FreshActivity homeActivity;

    ScrollView scrollView;
    TextView textViewScroll;
    LinearLayout linearLayoutMain;
    boolean scrolled = false;

    protected Bus mBus;

    @Override
    public void onResume() {
        super.onResume();
//        homeActivity.resumeMethod();

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_addto_address, container, false);

        homeActivity = (FreshActivity) getActivity();

        mBus = (homeActivity).getBus();



        root = (RelativeLayout) rootView.findViewById(R.id.root);
        new ASSL(homeActivity, root, 1134, 720, false);
        scrolled = false;

//        if (homeActivity.current_action.equals(homeActivity.ADD_ADDRESS)) {
            currentLatitude = homeActivity.current_latitude;
            currentLongitude = homeActivity.current_longitude;
            cityValue = homeActivity.current_city;
            pinCodeValue = homeActivity.current_pincode;
            areaReceived = homeActivity.current_area;
//
//
//
//        } else if (homeActivity.current_action.equals(homeActivity.UPDATE_ADDRESS)) {
//
//            try{
//
//                currentLatitude = homeActivity.current_latitude;
//                currentLongitude = homeActivity.current_longitude;
//
//
//                JSONObject object = new JSONObject(""+homeActivity.addressBookList.get(homeActivity.addressPos).address);
//                homeAddressReceived = object.getString("house_number");
//                buildingStreetReceived = object.getString("building_name");
//                areaReceived = object.getString("area");
//
//                cityValueReceived = homeActivity.current_city;
//                pinCodeReceived = homeActivity.current_pincode;
//
//
//
//            } catch(Exception e) {
//                e.printStackTrace();
//                currentLatitude = homeActivity.current_latitude;
//                currentLongitude = homeActivity.current_longitude;
//                homeAddressReceived = ""+homeActivity.addressBookList.get(homeActivity.addressPos).address;
//            }
//
//        }
//        else if (homeActivity.current_action.equals(homeActivity.EDIT_ADDRESS)) {
//
//            if(homeActivity.addressBookList.get(homeActivity.addressPos).address_version == 0) {
//                try{
//
//                    latitude = homeActivity.addressBookList.get(homeActivity.addressPos).latitude;
//                    longitude = homeActivity.addressBookList.get(homeActivity.addressPos).longitude;
//
//
//
//                    JSONObject object = new JSONObject(""+homeActivity.addressBookList.get(homeActivity.addressPos).address);
//                    homeAddressReceived = object.getString("house_number");
//                    buildingStreetReceived = object.getString("building_name");
//                    areaReceived = object.getString("area");
//
//
//                    cityValueReceived = object.getString("city");
//                    pinCodeReceived = object.getString("pin");
//
//                    if(cityValueReceived.length()==0 || pinCodeReceived.length() == 0){
//                        new AsyncGetAddress().execute();
//                    }
//
//
//                } catch(Exception e) {
//                    e.printStackTrace();
//                    homeAddressReceived = ""+homeActivity.addressBookList.get(homeActivity.addressPos).address;
//                    new AsyncGetAddress().execute();
//                }
//
//            } else if(homeActivity.addressBookList.get(homeActivity.addressPos).address_version == 1) {
//                try{
//                    JSONObject object = new JSONObject(""+homeActivity.addressBookList.get(homeActivity.addressPos).address);
//                    homeAddressReceived = object.getString("house_number");
//                    buildingStreetReceived = object.getString("building_name");
//                    areaReceived = object.getString("area");
//                    cityValueReceived = object.getString("city");
//                    pinCodeReceived = object.getString("pin");
//
//
//
//                } catch(Exception e) {
//
//                }
//
//            }
//            homeActivity.current_latitude = homeActivity.addressBookList.get(homeActivity.addressPos).latitude;
//            homeActivity.current_longitude = homeActivity.addressBookList.get(homeActivity.addressPos).longitude;
//
//        }
        initializeMap();
        initComponents();

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mBus.unregister(this);
    }


    private void initializeMap() {



        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.addressmapView)).getMap();

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setAllGesturesEnabled(false);

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    goToExpandedMap();
                }
            });

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    goToExpandedMap();
                    return true;
                }
            });

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(homeActivity,
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void goToExpandedMap(){
//        if (homeActivity.current_action.equals(homeActivity.EDIT_ADDRESS)) {
//            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.hold, R.anim.hold, R.anim.fade_out)
//                    .add(R.id.fragLayout, new AddAddressFragment(), "AddAddressFragment").addToBackStack("AddAddressFragment")
//                    .hide(getActivity().getSupportFragmentManager().findFragmentByTag(getActivity().getSupportFragmentManager().getBackStackEntryAt(getActivity()
//                            .getSupportFragmentManager().getBackStackEntryCount() - 1).getName())).commit();
//        }
    }

    private void initComponents() {

        houseNumber = (EditText) rootView.findViewById(R.id.edt_houseFlatNo);
        houseNumber.setTypeface(Fonts.latoRegular(homeActivity));
        buildingStreetName = (EditText) rootView.findViewById(R.id.edt_buildingStreetName);
        buildingStreetName.setTypeface(Fonts.latoRegular(homeActivity));
        area = (EditText) rootView.findViewById(R.id.edt_area);
        area.setTypeface(Fonts.latoRegular(homeActivity));
        city = (EditText) rootView.findViewById(R.id.edt_city);
        city.setTypeface(Fonts.latoRegular(homeActivity));
        pinCode = (EditText) rootView.findViewById(R.id.edt_pinCode);
        pinCode.setTypeface(Fonts.latoRegular(homeActivity));

        buttonAddToAddressBook = (Button) rootView.findViewById(R.id.buttonAddToAddressBook);
        buttonAddToAddressBook.setTypeface(Fonts.latoRegular(homeActivity));


        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);
        linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);

        linearLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutMain, textViewScroll, new KeyboardLayoutListener.KeyBoardStateHandler() {
            @Override
            public void keyboardOpened() {
                if (!scrolled) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (houseNumber.hasFocus()) {

                            } else if (buildingStreetName.hasFocus()) {

                            } else if (area.hasFocus()) {
                                scrollView.smoothScrollTo(0, city.getTop());
                            } else {
                                scrollView.smoothScrollTo(0, pinCode.getTop());
                            }
                        }
                    }, 100);
                    scrolled = true;
                }
            }

            @Override
            public void keyBoardClosed() {
                scrolled = false;
            }
        }));




//        if (homeActivity.current_action.equals(homeActivity.ADD_ADDRESS)) {
//            activityTitle.setText("ADD ADDRESS");
            buttonAddToAddressBook.setText("ADD ADDRESS");

            extractMapSnapShot(currentLatitude, currentLongitude);
//        } else if (homeActivity.current_action.equals(homeActivity.EDIT_ADDRESS)) {
//            activityTitle.setText("UPDATE ADDRESS");
//            buttonAddToAddressBook.setText("UPDATE ADDRESS");
//            double lat = homeActivity.addressBookList.get(homeActivity.addressPos).latitude;
//            double longi = homeActivity.addressBookList.get(homeActivity.addressPos).longitude;
//
//
//            extractMapSnapShot(lat, longi);
//        }  else if (homeActivity.current_action.equals(homeActivity.UPDATE_ADDRESS)) {
//            activityTitle.setText("UPDATE ADDRESS");
//            buttonAddToAddressBook.setText("UPDATE ADDRESS");
//
//            extractMapSnapShot(currentLatitude, currentLongitude);
//        }

        buttonAddToAddressBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fieldsAreFilled()){
                    hideSoftKeyboard();
//                    if (homeActivity.current_action.equals(homeActivity.ADD_ADDRESS)) {


                        String hno = "";
                        String building = "";
                        String areaVal = "";
                        String cityVal = "";
                        String pin = "";

                    if(!TextUtils.isEmpty(houseNumber.getText().toString().trim())) {
                        hno = houseNumber.getText().toString().trim() + ", ";
                    }
                    if(!TextUtils.isEmpty(buildingStreetName.getText().toString().trim())) {
                        building = buildingStreetName.getText().toString().trim() + ", ";
                    }
                    if(!TextUtils.isEmpty(area.getText().toString().trim())) {
                        areaVal = area.getText().toString().trim() + ", ";
                    }
                    if(!TextUtils.isEmpty(city.getText().toString().trim())) {
                        cityVal = city.getText().toString().trim() + ", ";
                    }
                    if(!TextUtils.isEmpty(pinCode.getText().toString().trim())) {
                        pin = pinCode.getText().toString().trim();
                    }


                    String localAddress = hno + building + areaVal + cityVal + pin;

                        if(checkFields()) {
                            Prefs.with(homeActivity).save(homeActivity.getResources().getString(R.string.pref_local_address), localAddress);
                            Prefs.with(homeActivity).save(homeActivity.getResources().getString(R.string.pref_loc_lati), ""+homeActivity.current_latitude);
                            Prefs.with(homeActivity).save(homeActivity.getResources().getString(R.string.pref_loc_longi), ""+homeActivity.current_longitude);

                            mBus.post(new AddressAdded(true));

                            homeActivity.setSelectedAddress(localAddress);
                            Prefs.with(homeActivity).save(homeActivity.getResources().getString(R.string.pref_address_selected), 3);

                            FreshAddressFragment freshAddressFragment = homeActivity.getFreshAddressFragment();
                            if(freshAddressFragment != null) {
                                getActivity().getSupportFragmentManager().popBackStack(FreshAddressFragment.class.getName(), getFragmentManager().POP_BACK_STACK_INCLUSIVE);
                            } else {
                                getActivity().getSupportFragmentManager().popBackStack(AddAddressMapFragment.class.getName(), getFragmentManager().POP_BACK_STACK_INCLUSIVE);
                            }

                        }

                } else {
                    //new DialogPopup().alertPopup(homeActivity, "", "Please fill the address");
                }
            }
        });

            area.setText(areaReceived);
            city.setText(cityValue);
            pinCode.setText(pinCodeValue);

    }

    public void extractMapSnapShot(double latitude, double longitude) {

        googleMap.clear();
        CameraPosition cameraPosition;


        cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(14).build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        marker = new MarkerOptions().position(new LatLng(latitude, longitude));
        marker.icon(BitmapDescriptorFactory.fromBitmap(createPinMarkerBitmap()));



        googleMap.addMarker(marker);

    }




    public Bitmap createPinMarkerBitmap() {
        float scale = Math.min(ASSL.Xscale(), ASSL.Yscale());
        int width = (int) (45.0f * scale);
        int height = (int) (74.97f * scale);
        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = homeActivity.getResources().getDrawable(R.drawable.pin_ball);
        shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
        shape.draw(canvas);
        return mDotMarkerBitmap;
    }



    private boolean fieldsAreFilled() {
        if (houseNumber.getText().toString().trim().length() == 0 && buildingStreetName.getText().toString().trim().length() == 0 &&
                area.getText().toString().trim().length() == 0){// && city.getText().toString().trim().length() == 0 && pinCode.getText().toString().trim().length() == 0) {
            houseNumber.requestFocus();
            houseNumber.setError("required field");
            return false;
        }
        else if(city.getText().toString().trim().length() == 0) {
            city.requestFocus();
            city.setError("required field");
            return false;
        }
        else if(pinCode.getText().toString().trim().length() == 0) {
            pinCode.requestFocus();
            pinCode.setError("required field");
            return false;
        }
        return true;
    }

    public void hideSoftKeyboard() {
        try {
            if (homeActivity.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) homeActivity.getSystemService(homeActivity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(homeActivity.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

//    public void updateAddress() {
//
//
//        homeAddressReceived = "";
//        buildingStreetReceived = "";
//        areaReceived = "";
//
//        city.setText(homeActivity.current_city);
//        pinCode.setText(homeActivity.current_pincode);
//
//        houseNumber.setText(homeAddressReceived);
//        buildingStreetName.setText(buildingStreetReceived);
//        area.setText(areaReceived);
//
//        houseNumber.setEnabled(true);
//        buildingStreetName.setEnabled(true);
//        area.setEnabled(true);
//
//        extractMapSnapShot(homeActivity.current_latitude, homeActivity.current_longitude);
//    }

    @Override
    public void onDestroyView() {

        Log.e("onDestroyView", "====onDestroyView");
        destroyMap();
        super.onDestroyView();

    }


    public void destroyMap(){

        try {
            SupportMapFragment suMapFrag = (SupportMapFragment) homeActivity.getSupportFragmentManager()
                    .findFragmentById(R.id.addressmapView);
            if (suMapFrag != null)
                homeActivity.getSupportFragmentManager().beginTransaction().remove(suMapFrag ).commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void editNewAddress(String data) {

    }


    public void addNewAddress(String data) {

    }



    class AsyncGetAddress extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
//            LoadingBox.showLoadingDialog(homeActivity, "Loading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String addressText = reverseGeoCoding(latitude,
                    longitude);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {


            cityValueReceived = City;
            pinCodeReceived = PIN;
            city.setText(cityValueReceived);
            pinCode.setText(pinCodeReceived);

//            LoadingBox.dismissLoadingDialog();



        }
    }

    protected JSONObject getLocationInfo(double lat, double lng) {

        if (android.os.Build.VERSION.SDK_INT > 9) {

            HttpGet httpGet = new HttpGet(
                    AppConstant.REVERSE_GEO_CODING_URL
                            + lat + "," + lng + "&sensor=true");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(stringBuilder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }
        return null;
    }

    protected String reverseGeoCoding(double lat, double lng) {

        //Here the JSON Object is retrieved via 'getLocationInfo' method
        //The JSONObject is retrieved in 'jsonObj'
        JSONObject jsonObj = getLocationInfo(lat, lng);
        Log.i("JSON string =>", jsonObj.toString());

        String Address1 = "";
        String Address2 = "";
        City = "";
        String State = "";
        String Country = "";
        String County = "";
        PIN = "";

        String currentLocation = "";

        try {
            String status = jsonObj.getString("status").toString();
            Log.i("status", status);

            if (status.equalsIgnoreCase("OK")) {
                JSONArray Results = jsonObj.getJSONArray("results");
                JSONObject zero = Results.getJSONObject(0);
                JSONArray address_components = zero
                        .getJSONArray("address_components");

                for (int i = 0; i < address_components.length(); i++) {
                    JSONObject zero2 = address_components.getJSONObject(i);
                    String long_name = zero2.getString("long_name");
                    JSONArray mtypes = zero2.getJSONArray("types");
                    String Type = mtypes.getString(0);

                    if (Type.equalsIgnoreCase("street_number")) {
                        Address1 = long_name + " ";
                    } else if (Type.equalsIgnoreCase("route")) {
                        Address1 = Address1 + long_name;
                    } else if (Type.equalsIgnoreCase("sublocality")) {
                        Address2 = long_name;
                    } else if (Type.equalsIgnoreCase("locality")) {
                        // Address2 = Address2 + long_name + ", ";
                        City = long_name;
                    } else if (Type
                            .equalsIgnoreCase("administrative_area_level_2")) {
                        County = long_name;
                    } else if (Type
                            .equalsIgnoreCase("administrative_area_level_1")) {
                        State = long_name;
                    } else if (Type.equalsIgnoreCase("country")) {
                        Country = long_name;
                    } else if (Type.equalsIgnoreCase("postal_code")) {
                        PIN = long_name;
                    }

                }

                currentLocation = Address1 + "," + Address2 + "," + City + ","
                        + State + "," + Country + "," + PIN;

            }
        } catch (Exception e) {

        }
        return currentLocation;

    }


    public boolean check(){
        return false;
    }

    public boolean checkFields() {
        try {
            String hno = houseNumber.getText().toString().trim();
            String building = buildingStreetName.getText().toString().trim();
            String areaVal = area.getText().toString().trim();
            String cityVal = city.getText().toString().trim();
            String pin = pinCode.getText().toString().trim();

            if(hno.length() == 0 && building.length() == 0) {
                Toast.makeText(homeActivity, "Please fill address", Toast.LENGTH_SHORT).show();
                return false;
            } else if(cityVal.length() == 0 && areaVal.length() == 0) {
                Toast.makeText(homeActivity, "Please fill area/city ", Toast.LENGTH_SHORT).show();
                return false;
            } else if(pin.length() == 0) {
                Toast.makeText(homeActivity, "Please fill pin", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
