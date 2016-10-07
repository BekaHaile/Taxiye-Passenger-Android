package com.sabkuchfresh.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.home.FreshActivity;
import com.squareup.otto.Bus;

import product.clicklabs.jugnoo.AddPlaceActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Created by Gurmail S. Kang on 5/4/16.
 */
public class AddToAddressBookFragment extends Fragment {


    private GoogleMap googleMap;
    MarkerOptions marker;

    Button buttonAddToAddressBook;
    RelativeLayout root;

    EditText editTextLabel, houseNumber, buildingStreetName, area, city, pinCode;

    View rootView;
    public FragmentActivity activity;

    protected Bus mBus;

    public double current_latitude = 0.0;
    public double current_longitude = 0.0;
    public String current_street = "";
    public String current_route = "";
    public String current_area = "";
    public String current_city = "";
    public String current_pincode = "";
    public String placeId = "";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_addto_address, container, false);

        activity = getActivity();
        fetchAddressBundle();

        root = (RelativeLayout) rootView.findViewById(R.id.root);
        new ASSL(activity, root, 1134, 720, false);

        mBus = MyApplication.getInstance().getBus();
        if(activity instanceof FreshActivity) {
            ((FreshActivity) activity).fragmentUISetup(this);
        } else if(activity instanceof AddPlaceActivity){
            AddPlaceActivity addPlaceActivity = (AddPlaceActivity) activity;
            addPlaceActivity.getTextViewTitle().setVisibility(View.VISIBLE);
            addPlaceActivity.getTextViewTitle().setText(activity.getString(R.string.confirm_address));
            addPlaceActivity.getRelativeLayoutSearch().setVisibility(View.GONE);
        }


        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.addressmapView)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;
                    if(googleMap != null) {
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        googleMap.getUiSettings().setZoomControlsEnabled(false);
                        googleMap.getUiSettings().setAllGesturesEnabled(false);

                    }

                    // check if map is created successfully or not
                    if (googleMap == null) {
                        Toast.makeText(activity,
                                "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                                .show();
                    }
                    extractMapSnapShot(current_latitude, current_longitude);
                }
            });


        }
    }


    private void initComponents() {
        editTextLabel = (EditText) rootView.findViewById(R.id.editTextLabel); editTextLabel.setTypeface(Fonts.mavenRegular(activity));
        houseNumber = (EditText) rootView.findViewById(R.id.edt_houseFlatNo); houseNumber.setTypeface(Fonts.mavenRegular(activity));
        buildingStreetName = (EditText) rootView.findViewById(R.id.edt_buildingStreetName); buildingStreetName.setTypeface(Fonts.mavenRegular(activity));
        area = (EditText) rootView.findViewById(R.id.edt_area); area.setTypeface(Fonts.mavenRegular(activity));
        city = (EditText) rootView.findViewById(R.id.edt_city); city.setTypeface(Fonts.mavenRegular(activity));
        pinCode = (EditText) rootView.findViewById(R.id.edt_pinCode); pinCode.setTypeface(Fonts.mavenRegular(activity));

        buttonAddToAddressBook = (Button) rootView.findViewById(R.id.buttonAddToAddressBook); buttonAddToAddressBook.setTypeface(Fonts.mavenRegular(activity));

        buttonAddToAddressBook.setText(activity.getResources().getString(R.string.confirm));

        buttonAddToAddressBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fieldsAreFilled()){
                    Utils.hideSoftKeyboard(activity, editTextLabel);

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
                            DeliveryAddressesFragment deliveryAddressesFragment = null;

                            int placeRequestCode = 0, searchResultId = 0, otherId = 0;
                            boolean editThisAddress = false;
                            String savedWorkStr = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");
                            String savedHomeStr = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");

                            if(activity instanceof FreshActivity) {
                                ((FreshActivity) activity).setSelectedAddress(localAddress);
                                ((FreshActivity)activity).setSelectedLatLng(new LatLng(current_latitude, current_longitude));

                                FreshActivity freshActivity = (FreshActivity) activity;
                                deliveryAddressesFragment = freshActivity.getDeliveryAddressesFragment();
                                placeRequestCode = freshActivity.getPlaceRequestCode();
                                editThisAddress = freshActivity.isEditThisAddress();
                                if (freshActivity.isEditThisAddress() && freshActivity.getSearchResult() != null) {
                                    searchResultId = freshActivity.getSearchResult().getId();
                                    ((FreshActivity)activity).setSelectedAddressId(searchResultId);
                                    placeId = freshActivity.getSearchResult().getPlaceId();
                                }

                            }
                            else if(activity instanceof AddPlaceActivity){
                                AddPlaceActivity addPlaceActivity = (AddPlaceActivity) activity;
                                deliveryAddressesFragment = addPlaceActivity.getDeliveryAddressesFragment();
                                placeRequestCode = addPlaceActivity.getPlaceRequestCode();
                                editThisAddress = addPlaceActivity.isEditThisAddress();
                                if (addPlaceActivity.isEditThisAddress() && addPlaceActivity.getSearchResult() != null) {
                                    searchResultId = addPlaceActivity.getSearchResult().getId();
                                    placeId = addPlaceActivity.getSearchResult().getPlaceId();
                                }
                            }


                            if (placeRequestCode == Constants.REQUEST_CODE_ADD_HOME) {
                                otherId = 0;
                                if (!"".equalsIgnoreCase(savedWorkStr)) {
                                    SearchResult savedWork = new Gson().fromJson(savedWorkStr, SearchResult.class);
                                    if (savedWork.getAddress().equalsIgnoreCase(localAddress)) {
                                        otherId = savedWork.getId();
                                    }
                                }
                            } else if (placeRequestCode == Constants.REQUEST_CODE_ADD_WORK) {
                                otherId = 0;
                                if (!"".equalsIgnoreCase(savedHomeStr)) {
                                    SearchResult savedHome = new Gson().fromJson(savedHomeStr, SearchResult.class);
                                    if (savedHome.getAddress().equalsIgnoreCase(localAddress)) {
                                        otherId = savedHome.getId();
                                    }
                                }
                            } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION) {
                                otherId = 0;
                            }

                            String label = "";
                            if(placeRequestCode == Constants.REQUEST_CODE_ADD_HOME){
                                label = Constants.TYPE_HOME;
                            } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
                                label = Constants.TYPE_WORK;
                            } else {
                                label = editTextLabel.getText().toString().trim();
                            }

                            if(activity instanceof FreshActivity){
                                FreshActivity freshActivity = (FreshActivity) activity;
                                freshActivity.setSelectedAddressId(searchResultId);
                                freshActivity.setSelectedAddressType(label);

                                if(freshActivity.getDeliveryAddressToEdit() != null){
                                    if(label.length() > 0){
                                        freshActivity.getUserCheckoutResponse().getCheckoutData().getDeliveryAddresses().remove(freshActivity.getDeliveryAddressToEdit());
                                    } else {
                                        freshActivity.getDeliveryAddressToEdit().setLastAddress(localAddress);
                                        freshActivity.getDeliveryAddressToEdit().setDeliveryLatitude(String.valueOf(current_latitude));
                                        freshActivity.getDeliveryAddressToEdit().setDeliveryLongitude(String.valueOf(current_longitude));
                                    }
                                }

                                mBus.post(new AddressAdded(true));
                            }

                            SearchResult searchResult = new SearchResult(label, localAddress, placeId, current_latitude, current_longitude);
                            searchResult.setId(searchResultId);
                            if(activity instanceof AddPlaceActivity) {
                                ((AddPlaceActivity)activity).hitApiAddHomeWorkAddress(searchResult, false, otherId, editThisAddress, placeRequestCode);
                            } else if(activity instanceof FreshActivity && label.length() > 0) {
                                ((FreshActivity)activity).hitApiAddHomeWorkAddress(searchResult, false, otherId, editThisAddress, placeRequestCode);
                            }

                            if(deliveryAddressesFragment != null) {
                                getActivity().getSupportFragmentManager().popBackStack(DeliveryAddressesFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            } else {
                                getActivity().getSupportFragmentManager().popBackStack(AddAddressMapFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                        }

                } else {
                    //new DialogPopup().alertPopup(homeActivity, "", "Please fill the address");
                }
            }
        });


        houseNumber.setText(current_street);
        buildingStreetName.setText(current_route);
        area.setText(current_area);
        city.setText(current_city);
        pinCode.setText(current_pincode);

        int placeRequestCode = 0;
        String label = "";
        boolean editAddress = false;
        if(activity instanceof AddPlaceActivity){
            AddPlaceActivity addPlaceActivity = ((AddPlaceActivity) activity);
            placeRequestCode = addPlaceActivity.getPlaceRequestCode();
            editAddress = addPlaceActivity.isEditThisAddress();
            if(addPlaceActivity.getSearchResult() != null && addPlaceActivity.getSearchResult().getName() != null) {
                label = addPlaceActivity.getSearchResult().getName();
            }
            addPlaceActivity.getImageViewDelete().setVisibility(editAddress ? View.VISIBLE : View.GONE);
            addPlaceActivity.getTextViewTitle().setText(editAddress ? activity.getString(R.string.edit_address) : activity.getString(R.string.confirm_address));

        } else if(activity instanceof FreshActivity){
            final FreshActivity freshActivity = ((FreshActivity) activity);
            placeRequestCode = freshActivity.getPlaceRequestCode();
            editAddress = freshActivity.isEditThisAddress();
            if(editAddress && freshActivity.getSearchResult() != null && freshActivity.getSearchResult().getName() != null) {
                label = freshActivity.getSearchResult().getName();
            }
            freshActivity.getTopBar().title.setText(editAddress ? activity.getString(R.string.edit_address) : activity.getString(R.string.confirm_address));
            freshActivity.getTopBar().imageViewDelete.setVisibility(editAddress ? View.VISIBLE : View.GONE);
            freshActivity.getTopBar().imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogPopup.alertPopupTwoButtonsWithListeners(freshActivity, "",
                            getString(R.string.address_delete_confirm_message),
                            getString(R.string.delete), getString(R.string.cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    freshActivity.hitApiAddHomeWorkAddress(freshActivity.getSearchResult(), true, 0,
                                            freshActivity.isEditThisAddress(), freshActivity.getPlaceRequestCode());
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }, false, false);
                }
            });
        }

        if(placeRequestCode == Constants.REQUEST_CODE_ADD_HOME){
            editTextLabel.setText(getString(R.string.home));
            editTextLabel.setEnabled(false);
            houseNumber.requestFocus();
            houseNumber.setSelection(houseNumber.getText().length());
        } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
            editTextLabel.setText(getString(R.string.work));
            editTextLabel.setEnabled(false);
            houseNumber.requestFocus();
            houseNumber.setSelection(houseNumber.getText().length());
        } else {
            editTextLabel.setText(label);
            editTextLabel.setEnabled(true);
            editTextLabel.requestFocus();
            editTextLabel.setSelection(editTextLabel.getText().length());
        }

        buttonAddToAddressBook.setText(editAddress ? R.string.update_address : R.string.confirm);

        editTextLabel.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                houseNumber.requestFocus();
                houseNumber.setSelection(houseNumber.getText().length());
                return true;
            }
        });
        houseNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                buildingStreetName.requestFocus();
                buildingStreetName.setSelection(buildingStreetName.getText().length());
                return true;
            }
        });
        buildingStreetName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                area.requestFocus();
                area.setSelection(area.getText().length());
                return true;
            }
        });
        area.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                city.requestFocus();
                city.setSelection(city.getText().length());
                return true;
            }
        });
        city.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                pinCode.requestFocus();
                pinCode.setSelection(pinCode.getText().length());
                return true;
            }
        });
        pinCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                buttonAddToAddressBook.performClick();
                return true;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.hideSoftKeyboard(activity, houseNumber);
            }
        }, 100);


    }

    @Override
    public void onResume() {
        super.onResume();
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
        int height = (int) (85.0f * scale);
        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = activity.getResources().getDrawable(R.drawable.ic_delivery_address_map);
        shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
        shape.draw(canvas);
        return mDotMarkerBitmap;
    }

    private boolean fieldsAreFilled() {
        if(activity instanceof AddPlaceActivity && editTextLabel.getText().toString().trim().length() == 0){
            editTextLabel.requestFocus();
            editTextLabel.setError("required field");
            return false;
        }
        else if (houseNumber.getText().toString().trim().length() == 0 && buildingStreetName.getText().toString().trim().length() == 0 &&
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


    @Override
    public void onDestroyView() {
        destroyMap();
        super.onDestroyView();

    }

    public void destroyMap(){
        try {
            SupportMapFragment suMapFrag = (SupportMapFragment) activity.getSupportFragmentManager()
                    .findFragmentById(R.id.addressmapView);
            if (suMapFrag != null)
                activity.getSupportFragmentManager().beginTransaction().remove(suMapFrag ).commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean checkFields() {
        try {
            String hno = houseNumber.getText().toString().trim();
            String building = buildingStreetName.getText().toString().trim();
            String areaVal = area.getText().toString().trim();
            String cityVal = city.getText().toString().trim();
            String pin = pinCode.getText().toString().trim();

            if(hno.length() == 0 && building.length() == 0) {
                Toast.makeText(activity, "Please fill address", Toast.LENGTH_SHORT).show();
                return false;
            } else if(cityVal.length() == 0 && areaVal.length() == 0) {
                Toast.makeText(activity, "Please fill area/city ", Toast.LENGTH_SHORT).show();
                return false;
            } else if(pin.length() == 0) {
                Toast.makeText(activity, "Please fill pin", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void fetchAddressBundle(){
        Bundle bundle = getArguments();
        current_street = bundle.getString("current_street", current_street);
        current_route = bundle.getString("current_route", current_route);
        current_area = bundle.getString("current_area", current_area);
        current_city = bundle.getString("current_city", current_city);
        current_pincode = bundle.getString("current_pincode", current_pincode);
        current_latitude = bundle.getDouble("current_latitude", current_latitude);
        current_longitude = bundle.getDouble("current_longitude", current_longitude);
        placeId = bundle.getString(Constants.KEY_PLACEID, placeId);
    }

}
