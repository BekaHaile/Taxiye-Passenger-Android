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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.home.FreshActivity;
import com.squareup.otto.Bus;

import product.clicklabs.jugnoo.AddPlaceActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
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
    RelativeLayout root, relativeLayoutEdit;

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

    public boolean locationEdited = false;
    public boolean editThisAddress;
    public String lastLabel = "";


    RelativeLayout relativeLayoutTypeHome, relativeLayoutTypeWork, relativeLayoutTypeOther;
    ImageView imageViewRadioTypeHome, imageViewRadioTypeWork, imageViewRadioTypeOther;
    TextView textViewTypeHome, textViewTypeWork, textViewTypeOther;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_addto_address, container, false);

        locationEdited = false;
        activity = getActivity();
        fetchAddressBundle(getArguments());

        root = (RelativeLayout) rootView.findViewById(R.id.root);
        new ASSL(activity, root, 1134, 720, false);

        mBus = MyApplication.getInstance().getBus();
        fragmentUiSetup();


        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initializeMap();
        initComponents();

        return rootView;
    }

    private void fragmentUiSetup(){
        if(activity instanceof FreshActivity) {
            ((FreshActivity) activity).fragmentUISetup(this);
        } else if(activity instanceof AddPlaceActivity) {
            AddPlaceActivity addPlaceActivity = (AddPlaceActivity) activity;
            addPlaceActivity.getTextViewTitle().setVisibility(View.VISIBLE);
            addPlaceActivity.getRelativeLayoutSearch().setVisibility(View.GONE);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            fragmentUiSetup();
            setDataToUI();
        }
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

                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                return true;
                            }
                        });

                    }

                    // check if map is created successfully or not
                    if (googleMap == null) {
                        Utils.showToast(activity, "Sorry! unable to create maps");
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
        relativeLayoutEdit = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutEdit);

        buttonAddToAddressBook = (Button) rootView.findViewById(R.id.buttonAddToAddressBook); buttonAddToAddressBook.setTypeface(Fonts.mavenRegular(activity));

        relativeLayoutTypeHome = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutTypeHome);
        relativeLayoutTypeWork = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutTypeWork);
        relativeLayoutTypeOther = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutTypeOther);
        imageViewRadioTypeHome = (ImageView) rootView.findViewById(R.id.imageViewRadioTypeHome);
        imageViewRadioTypeWork = (ImageView) rootView.findViewById(R.id.imageViewRadioTypeWork);
        imageViewRadioTypeOther = (ImageView) rootView.findViewById(R.id.imageViewRadioTypeOther);
        textViewTypeHome = (TextView) rootView.findViewById(R.id.textViewTypeHome); textViewTypeHome.setTypeface(Fonts.mavenMedium(activity));
        textViewTypeWork = (TextView) rootView.findViewById(R.id.textViewTypeWork); textViewTypeWork.setTypeface(Fonts.mavenMedium(activity));
        textViewTypeOther = (TextView) rootView.findViewById(R.id.textViewTypeOther); textViewTypeOther.setTypeface(Fonts.mavenMedium(activity));


        buttonAddToAddressBook.setText(activity.getResources().getString(R.string.confirm));

        buttonAddToAddressBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fieldsAreFilled()){
                    Utils.hideSoftKeyboard(activity, editTextLabel);
                    String localAddress = getAddressFromForm();
                    if (checkFields()) {
                        saveAddressFromForm(localAddress, -1, -1);
                    }
                } else {
                    //new DialogPopup().alertPopup(homeActivity, "", "Please fill the address");
                }
            }
        });

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

        relativeLayoutEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean editAddress = false;
                    if(activity instanceof AddPlaceActivity){
                        editAddress = ((AddPlaceActivity)activity).isEditThisAddress();
                        if(editAddress){
                            ((AddPlaceActivity)activity).openMapAddress(createAddressBundle(placeId));
                        }
                    } else if(activity instanceof FreshActivity){
                        editAddress = ((FreshActivity)activity).isEditThisAddress();
                        if(editAddress){
                            ((FreshActivity)activity).openMapAddress(createAddressBundle(placeId));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        relativeLayoutTypeHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextLabel.setVisibility(View.GONE);
                setAddressTypeUI(activity.getString(R.string.home));
            }
        });

        relativeLayoutTypeWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextLabel.setVisibility(View.GONE);
                setAddressTypeUI(activity.getString(R.string.work));
            }
        });

        relativeLayoutTypeOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextLabel.setVisibility(View.VISIBLE);
                editTextLabel.setSelection(editTextLabel.getText().length());
                if(lastLabel.equalsIgnoreCase(activity.getString(R.string.home))
                        || lastLabel.equalsIgnoreCase(activity.getString(R.string.work))){
                    setAddressTypeUI("");
                } else {
                    setAddressTypeUI(lastLabel);
                }
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.hideSoftKeyboard(activity, houseNumber);
            }
        }, 100);


        setDataToUI();

    }

    private void setAddressTypeUI(String label){
        imageViewRadioTypeHome.setImageResource(R.drawable.ic_home);
        imageViewRadioTypeWork.setImageResource(R.drawable.ic_work);
        imageViewRadioTypeOther.setImageResource(R.drawable.ic_loc_other);
        textViewTypeHome.setTextColor(activity.getResources().getColorStateList(R.color.text_color_selector));
        textViewTypeWork.setTextColor(activity.getResources().getColorStateList(R.color.text_color_selector));
        textViewTypeOther.setTextColor(activity.getResources().getColorStateList(R.color.text_color_selector));
        if(label.equalsIgnoreCase(activity.getString(R.string.home))){
            imageViewRadioTypeHome.setImageResource(R.drawable.ic_home_highlighted);
            textViewTypeHome.setTextColor(activity.getResources().getColor(R.color.theme_color));
        } else if(label.equalsIgnoreCase(activity.getString(R.string.work))){
            imageViewRadioTypeWork.setImageResource(R.drawable.ic_work_highlighted);
            textViewTypeWork.setTextColor(activity.getResources().getColor(R.color.theme_color));
        } else {
            imageViewRadioTypeOther.setImageResource(R.drawable.ic_loc_other_highlighted);
            textViewTypeOther.setTextColor(activity.getResources().getColor(R.color.theme_color));
            editTextLabel.requestFocus();
        }
        editTextLabel.setText(label);
        editTextLabel.setSelection(editTextLabel.getText().length());
    }

    private void setDataToUI(){

        houseNumber.setText(current_street);
        buildingStreetName.setText(current_route);
        area.setText(current_area);
        city.setText(current_city);
        pinCode.setText(current_pincode);

        int placeRequestCode = 0;
        String label = "";
        boolean editAddress = false;
        SearchResult searchResultToEdit = null;
        if(activity instanceof AddPlaceActivity){
            AddPlaceActivity addPlaceActivity = ((AddPlaceActivity) activity);
            placeRequestCode = addPlaceActivity.getPlaceRequestCode();
            editAddress = addPlaceActivity.isEditThisAddress();
            addPlaceActivity.getImageViewDelete().setVisibility(editAddress ? View.VISIBLE : View.GONE);
            addPlaceActivity.getTextViewTitle().setText(editAddress ? activity.getString(R.string.edit_address) : activity.getString(R.string.confirm_address));
            searchResultToEdit = addPlaceActivity.getSearchResult();
            if(searchResultToEdit != null) {
                if(searchResultToEdit.getName() != null) {
                    label = searchResultToEdit.getName();
                }
                if(searchResultToEdit.isRecentAddress()){
                    addPlaceActivity.getImageViewDelete().setVisibility(View.GONE);
                    addPlaceActivity.getTextViewTitle().setText(activity.getString(R.string.confirm_address));
                }
            }

        } else if(activity instanceof FreshActivity){
            final FreshActivity freshActivity = ((FreshActivity) activity);
            placeRequestCode = freshActivity.getPlaceRequestCode();
            editAddress = freshActivity.isEditThisAddress();
            freshActivity.getTopBar().title.setText(editAddress ? activity.getString(R.string.edit_address) : activity.getString(R.string.confirm_address));
            freshActivity.getTopBar().imageViewDelete.setVisibility(editAddress ? View.VISIBLE : View.GONE);
            searchResultToEdit = freshActivity.getSearchResult();
            if(editAddress && freshActivity.getSearchResult() != null && freshActivity.getSearchResult().getName() != null) {
                label = freshActivity.getSearchResult().getName();
            }
            if(searchResultToEdit != null && searchResultToEdit.isRecentAddress()){
                freshActivity.getTopBar().title.setText(activity.getString(R.string.confirm_address));
                freshActivity.getTopBar().imageViewDelete.setVisibility(View.GONE);
            }
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

        editTextLabel.setEnabled(true);
        if(placeRequestCode == Constants.REQUEST_CODE_ADD_HOME){
            editTextLabel.setText(getString(R.string.home));
            houseNumber.requestFocus();
            houseNumber.setSelection(houseNumber.getText().length());
        } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
            editTextLabel.setText(getString(R.string.work));
            houseNumber.requestFocus();
            houseNumber.setSelection(houseNumber.getText().length());
        } else {
            editTextLabel.setText(label);
            editTextLabel.requestFocus();
            editTextLabel.setSelection(editTextLabel.getText().length());
        }

        if(searchResultToEdit != null && searchResultToEdit.isRecentAddress()){
            buttonAddToAddressBook.setText(R.string.confirm);
            relativeLayoutEdit.setVisibility(View.GONE);
        } else {
            buttonAddToAddressBook.setText(editAddress ? R.string.update_address : R.string.confirm);
            relativeLayoutEdit.setVisibility(editAddress ? View.VISIBLE : View.GONE);
        }
        editThisAddress = editAddress;

        relativeLayoutTypeHome.setVisibility(View.VISIBLE);
        relativeLayoutTypeWork.setVisibility(View.VISIBLE);
        relativeLayoutTypeOther.setVisibility(View.VISIBLE);
        editTextLabel.setVisibility(View.GONE);
        if(placeRequestCode == Constants.REQUEST_CODE_ADD_HOME){
            label = activity.getString(R.string.home);
        } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
            label = activity.getString(R.string.work);
        } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION){
            if(!editAddress) {
                label = activity.getString(R.string.home);
            } else {
                editTextLabel.setVisibility(View.VISIBLE);
            }
        }
        lastLabel = label;
        setAddressTypeUI(label);
    }


    public void setNewArgumentsToUI(Bundle bundle){
        double oldLatitude = current_latitude;
        double oldLongitude = current_longitude;

        fetchAddressBundle(bundle);
        setDataToUI();

        locationEdited = (Utils.compareDouble(oldLatitude, current_latitude) != 0 && Utils.compareDouble(oldLongitude, current_longitude) != 0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                extractMapSnapShot(current_latitude, current_longitude);
            }
        }, 500);
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
            if(editTextLabel.getVisibility() == View.GONE){
                editTextLabel.setVisibility(View.VISIBLE);
            }
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

            String label = editTextLabel.getText().toString().trim();

            if(hno.length() == 0 && building.length() == 0) {
                Utils.showToast(activity, "Please fill address");
                return false;
            } else if(cityVal.length() == 0 && areaVal.length() == 0) {
                Utils.showToast(activity, "Please fill area/city ");
                return false;
            } else if(pin.length() == 0) {
                Utils.showToast(activity, "Please fill pin");
                return false;
            } else {
                boolean editThisAddress = false;
                int selfId = -1;
                if(activity instanceof AddPlaceActivity){
                    editThisAddress = ((AddPlaceActivity)activity).isEditThisAddress();
                    if(editThisAddress){
                        selfId = ((AddPlaceActivity)activity).getSearchResult().getId();
                    }
                } else if(activity instanceof FreshActivity){
                    editThisAddress = ((FreshActivity)activity).isEditThisAddress();
                    if(editThisAddress){
                        selfId = ((FreshActivity)activity).getSearchResult().getId();
                    }
                }

                boolean labelMatched = false;
                int newId = -1;
                String workString = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");
                if(!TextUtils.isEmpty(workString)){
                    SearchResult searchResult = new Gson().fromJson(workString, SearchResult.class);
                    if(searchResult.getName().equalsIgnoreCase(label) && !searchResult.getId().equals(selfId)){
                        labelMatched = true;
                        newId = searchResult.getId();
                    }
                }

                String homeString = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
                if(!TextUtils.isEmpty(homeString)){
                    SearchResult searchResult = new Gson().fromJson(homeString, SearchResult.class);
                    if(searchResult.getName().equalsIgnoreCase(label) && !searchResult.getId().equals(selfId)){
                        labelMatched = true;
                        newId = searchResult.getId();
                    }
                }

                if(Data.userData != null) {
                    for (SearchResult searchResult : Data.userData.getSearchResults()) {
                        if(searchResult.getName().equalsIgnoreCase(label) && !searchResult.getId().equals(selfId)){
                            labelMatched = true;
                            newId = searchResult.getId();
                            break;
                        }
                    }
                }

                if(labelMatched){
                    final int finalSelfId = selfId;
                    final int finalNewId = newId;
                    DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
                            activity.getString(R.string.address_label_matched_message_format, label),
                            activity.getString(R.string.yes), activity.getString(R.string.cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String localAddress = getAddressFromForm();
                                    saveAddressFromForm(localAddress, finalSelfId, finalNewId);
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }, false, false);
                    return false;
                }

            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private Bundle createAddressBundle(String placeId){
        Bundle bundle = new Bundle();
        bundle.putString("current_street", current_street);
        bundle.putString("current_route", current_route);
        bundle.putString("current_area", current_area);
        bundle.putString("current_city", current_city);
        bundle.putString("current_pincode", current_pincode);
        bundle.putDouble("current_latitude", current_latitude);
        bundle.putDouble("current_longitude", current_longitude);
        bundle.putString(Constants.KEY_PLACEID, placeId);
        return bundle;
    }

    private void fetchAddressBundle(Bundle bundle){
        current_street = bundle.getString("current_street", current_street);
        current_route = bundle.getString("current_route", current_route);
        current_area = bundle.getString("current_area", current_area);
        current_city = bundle.getString("current_city", current_city);
        current_pincode = bundle.getString("current_pincode", current_pincode);
        current_latitude = bundle.getDouble("current_latitude", current_latitude);
        current_longitude = bundle.getDouble("current_longitude", current_longitude);
        placeId = bundle.getString(Constants.KEY_PLACEID, placeId);
    }


    private void saveAddressFromForm(String localAddress, int deleteOtherId, int newId){
        {
            DeliveryAddressesFragment deliveryAddressesFragment = null;

            int placeRequestCode = 0, searchResultId = 0, otherId = 0;
            boolean editThisAddress = false;
            String label = "";

            if(activity instanceof FreshActivity) {
                ((FreshActivity) activity).setSelectedAddress(localAddress);
                ((FreshActivity)activity).setSelectedLatLng(new LatLng(current_latitude, current_longitude));
                ((FreshActivity)activity).setSelectedAddressId(0);
                ((FreshActivity)activity).setSelectedAddressType("");

                FreshActivity freshActivity = (FreshActivity) activity;
                deliveryAddressesFragment = freshActivity.getDeliveryAddressesFragment();
                placeRequestCode = freshActivity.getPlaceRequestCode();
                editThisAddress = freshActivity.isEditThisAddress();

                if(editTextLabel.getText().toString().equalsIgnoreCase(activity.getString(R.string.home))){
                    placeRequestCode = Constants.REQUEST_CODE_ADD_HOME;
                } else if(editTextLabel.getText().toString().equalsIgnoreCase(activity.getString(R.string.work))){
                    placeRequestCode = Constants.REQUEST_CODE_ADD_WORK;
                } else{
                    placeRequestCode = Constants.REQUEST_CODE_ADD_NEW_LOCATION;
                }
                freshActivity.setPlaceRequestCode(placeRequestCode);

                if(placeRequestCode == Constants.REQUEST_CODE_ADD_HOME){
                    label = Constants.TYPE_HOME;
                } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
                    label = Constants.TYPE_WORK;
                } else {
                    label = editTextLabel.getText().toString().trim();
                }
                if (freshActivity.isEditThisAddress() && freshActivity.getSearchResult() != null) {
                    searchResultId = freshActivity.getSearchResult().getId();
                    ((FreshActivity)activity).setSelectedAddressId(searchResultId);
                    ((FreshActivity)activity).setSelectedAddressType(label);
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
                if(editTextLabel.getText().toString().equalsIgnoreCase(activity.getString(R.string.home))){
                    placeRequestCode = Constants.REQUEST_CODE_ADD_HOME;
                } else if(editTextLabel.getText().toString().equalsIgnoreCase(activity.getString(R.string.work))){
                    placeRequestCode = Constants.REQUEST_CODE_ADD_WORK;
                } else{
                    placeRequestCode = Constants.REQUEST_CODE_ADD_NEW_LOCATION;
                }
                addPlaceActivity.setPlaceRequestCode(placeRequestCode);
            }


            if(deleteOtherId > 0 || newId > 0){
                otherId = deleteOtherId;
                searchResultId = newId;
                if(activity instanceof FreshActivity) {
                    if(((FreshActivity)activity).getSearchResult() != null) {
                        ((FreshActivity) activity).getSearchResult().setId(searchResultId);
                    }
                    ((FreshActivity)activity).setSelectedAddressId(searchResultId);
                } else if(activity instanceof AddPlaceActivity){
                    if(((AddPlaceActivity)activity).getSearchResult() != null) {
                        ((AddPlaceActivity) activity).getSearchResult().setId(searchResultId);
                    }
                }

            } else {
                otherId = getOtherMatchedId(placeRequestCode, localAddress, searchResultId);
            }

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
            } else if(activity instanceof FreshActivity) {
                if(label.length() > 0) {
                    ((FreshActivity) activity).hitApiAddHomeWorkAddress(searchResult, false, otherId, editThisAddress, placeRequestCode);
                } else{
                    if(deliveryAddressesFragment != null) {
                        activity.getSupportFragmentManager().popBackStack(DeliveryAddressesFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    } else {
                        activity.getSupportFragmentManager().popBackStack(AddAddressMapFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                }
            }

        }
    }

    private int getOtherMatchedId(int placeRequestCode, String localAddress, int searchResultId){
        int otherId = 0;
        String workString = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");
        if((placeRequestCode == Constants.REQUEST_CODE_ADD_HOME || placeRequestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION)
                && !TextUtils.isEmpty(workString)){
            SearchResult searchResult = new Gson().fromJson(workString, SearchResult.class);
            if(searchResult.getAddress().equalsIgnoreCase(localAddress)){
                otherId = searchResult.getId();
            }
        }

        String homeString = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
        if((placeRequestCode == Constants.REQUEST_CODE_ADD_WORK || placeRequestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION)
                && !TextUtils.isEmpty(homeString)){
            SearchResult searchResult = new Gson().fromJson(homeString, SearchResult.class);
            if(searchResult.getAddress().equalsIgnoreCase(localAddress)){
                otherId = searchResult.getId();
            }
        }

        if(Data.userData != null) {
            for (SearchResult searchResult : Data.userData.getSearchResults()) {
                if(searchResult.getAddress().equalsIgnoreCase(localAddress)){
                    otherId = searchResult.getId();
                    break;
                }
            }
        }

        if(otherId == searchResultId){
            otherId = 0;
        }

        return otherId;
    }

    private String getAddressFromForm(){
        String hno = "";
        String building = "";
        String areaVal = "";
        String cityVal = "";
        String pin = "";

        if (!TextUtils.isEmpty(houseNumber.getText().toString().trim())) {
            hno = houseNumber.getText().toString().trim() + ", ";
        }
        if (!TextUtils.isEmpty(buildingStreetName.getText().toString().trim())) {
            building = buildingStreetName.getText().toString().trim() + ", ";
        }
        if (!TextUtils.isEmpty(area.getText().toString().trim())) {
            areaVal = area.getText().toString().trim() + ", ";
        }
        if (!TextUtils.isEmpty(city.getText().toString().trim())) {
            cityVal = city.getText().toString().trim() + ", ";
        }
        if (!TextUtils.isEmpty(pinCode.getText().toString().trim())) {
            pin = pinCode.getText().toString().trim();
        }

        return hno + building + areaVal + cityVal + pin;
    }

}
