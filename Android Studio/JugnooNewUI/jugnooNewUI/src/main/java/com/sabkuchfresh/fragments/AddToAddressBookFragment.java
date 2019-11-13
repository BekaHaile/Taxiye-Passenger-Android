package com.sabkuchfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.sabkuchfresh.bus.AddressAdded;
import com.sabkuchfresh.home.FreshActivity;
import com.squareup.otto.Bus;

import java.util.ArrayList;

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


    RelativeLayout root;

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

    private TextView textViewAddress;
    private EditText editTextLabel, editTextFlatNumber, editTextLandmark;
    private Button bConfirm;
    private RelativeLayout rlAddressLabels;
    private boolean showAddressLabels;

    private GoogleMap mapLite;

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

        showAddressLabels = !(activity instanceof FreshActivity && ((FreshActivity) activity).getSuggestAStoreFragment()!=null);
        initComponents();
        if(!showAddressLabels){
            rlAddressLabels.setVisibility(View.GONE);
            editTextLabel.setVisibility(View.GONE);
        }

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapLite)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapLite = googleMap;
                if (mapLite != null) {
                    mapLite.getUiSettings().setAllGesturesEnabled(false);
                    mapLite.getUiSettings().setZoomGesturesEnabled(false);
                    mapLite.getUiSettings().setZoomControlsEnabled(false);
                    mapLite.getUiSettings().setTiltGesturesEnabled(false);
                    mapLite.getUiSettings().setMyLocationButtonEnabled(false);
                    addMarker();

                    mapLite.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            openDeliveryAddressFragment();
                            return true;
                        }
                    });
					mapLite.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
						@Override
						public void onMapClick(LatLng latLng) {
                            openDeliveryAddressFragment();
                        }
					});
                }
            }
        });

        return rootView;
    }

    public void openDeliveryAddressFragment() {
        if (activity instanceof AddPlaceActivity
                && activity.getSupportFragmentManager().findFragmentByTag(DeliveryAddressesFragment.class.getName()) == null) {
            ((AddPlaceActivity) activity).getSearchResult().setLatitude(current_latitude);
            ((AddPlaceActivity) activity).getSearchResult().setLongitude(current_longitude);
            ((AddPlaceActivity) activity).openDeliveryAddressFragment();
        }
    }

    public void addMarker() {
        if(mapLite != null) {
            mapLite.clear();
            LatLng latLng = new LatLng(current_latitude, current_longitude);
            mapLite.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            mapLite.addMarker(new MarkerOptions().position(latLng));
        }
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


    private void initComponents() {
        editTextLabel = (EditText) rootView.findViewById(R.id.editTextLabel);
        rlAddressLabels = (RelativeLayout) rootView.findViewById(R.id.rlAddressLabels);
        textViewAddress = (TextView) rootView.findViewById(R.id.textViewAddress);
        editTextFlatNumber = (EditText) rootView.findViewById(R.id.editTextFlatNumber);
        editTextLandmark = (EditText) rootView.findViewById(R.id.editTextLandmark);
        bConfirm = (Button) rootView.findViewById(R.id.bConfirm);

        relativeLayoutTypeHome = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutTypeHome);
        relativeLayoutTypeWork = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutTypeWork);
        relativeLayoutTypeOther = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutTypeOther);
        imageViewRadioTypeHome = (ImageView) rootView.findViewById(R.id.imageViewRadioTypeHome);
        imageViewRadioTypeWork = (ImageView) rootView.findViewById(R.id.imageViewRadioTypeWork);
        imageViewRadioTypeOther = (ImageView) rootView.findViewById(R.id.imageViewRadioTypeOther);
        textViewTypeHome = (TextView) rootView.findViewById(R.id.textViewTypeHome); textViewTypeHome.setTypeface(Fonts.mavenMedium(activity));
        textViewTypeWork = (TextView) rootView.findViewById(R.id.textViewTypeWork); textViewTypeWork.setTypeface(Fonts.mavenMedium(activity));
        textViewTypeOther = (TextView) rootView.findViewById(R.id.textViewTypeOther); textViewTypeOther.setTypeface(Fonts.mavenMedium(activity));


        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fieldsAreFilled()){
                    Utils.hideSoftKeyboard(activity, editTextLabel);
                    String localAddress = getAddressFromForm();
                    if (checkFields()) {
                        saveAddressFromForm(localAddress, -1, -1);
                    }
                }
            }
        });

        editTextLabel.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                editTextFlatNumber.requestFocus();
                editTextFlatNumber.setSelection(editTextFlatNumber.getText().length());
                return true;
            }
        });
        editTextFlatNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                editTextLandmark.requestFocus();
                editTextLandmark.setSelection(editTextLandmark.getText().length());
                return true;
            }
        });
        editTextLandmark.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                bConfirm.performClick();
                return true;
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

        setDataToUI();
    }

    private void setAddressTypeUI(String label){
        imageViewRadioTypeHome.setImageResource(R.drawable.ic_home);
        imageViewRadioTypeWork.setImageResource(R.drawable.ic_work);
        imageViewRadioTypeOther.setImageResource(R.drawable.ic_loc_other);
        textViewTypeHome.setTextColor(ContextCompat.getColorStateList(activity, R.color.text_color_selector));
        textViewTypeWork.setTextColor(ContextCompat.getColorStateList(activity, R.color.text_color_selector));
        textViewTypeOther.setTextColor(ContextCompat.getColorStateList(activity, R.color.text_color_selector));
        relativeLayoutTypeHome.setBackgroundResource(R.drawable.background_transparent);
        relativeLayoutTypeWork.setBackgroundResource(R.drawable.background_transparent);
        relativeLayoutTypeOther.setBackgroundResource(R.drawable.background_transparent);
        if(label.equalsIgnoreCase(activity.getString(R.string.home))){
            imageViewRadioTypeHome.setImageResource(R.drawable.ic_home_highlighted);
            textViewTypeHome.setTextColor(ContextCompat.getColor(activity, R.color.theme_color));
            relativeLayoutTypeHome.setBackgroundResource(R.drawable.capsule_white_br_layer_shadow);
        } else if(label.equalsIgnoreCase(activity.getString(R.string.work))){
            imageViewRadioTypeWork.setImageResource(R.drawable.ic_work_highlighted);
            textViewTypeWork.setTextColor(ContextCompat.getColor(activity, R.color.theme_color));
            relativeLayoutTypeWork.setBackgroundResource(R.drawable.capsule_white_br_layer_shadow);
        } else {
            imageViewRadioTypeOther.setImageResource(R.drawable.ic_loc_other_highlighted);
            textViewTypeOther.setTextColor(ContextCompat.getColor(activity, R.color.theme_color));
            relativeLayoutTypeOther.setBackgroundResource(R.drawable.capsule_white_br_layer_shadow);
            editTextLabel.requestFocus();
        }
        editTextLabel.setText(label);
        editTextLabel.setSelection(editTextLabel.getText().length());
    }

    private void setDataToUI(){

        textViewAddress.setText(current_area+(TextUtils.isEmpty(current_area)?"":", ")
                +current_city+(TextUtils.isEmpty(current_city)?"":", ")
                +current_pincode);

        editTextFlatNumber.setText(current_street+(TextUtils.isEmpty(current_street)?"":", ")
                +current_route);

        int placeRequestCode = 0;
        String label = "";
        boolean editAddress = false;
        SearchResult searchResultToEdit = null;
        if(activity instanceof AddPlaceActivity){
            AddPlaceActivity addPlaceActivity = ((AddPlaceActivity) activity);
            placeRequestCode = addPlaceActivity.getPlaceRequestCode();
            editAddress = addPlaceActivity.isEditThisAddress();
            addPlaceActivity.getImageViewDelete().setVisibility(editAddress ? View.VISIBLE : View.GONE);
            addPlaceActivity.getTextViewTitle().setText(activity.getString(R.string.confirm_address));
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
            if(!editAddress){
                placeRequestCode = getUpdatedPlaceRequestCode(placeRequestCode);
            }

        } else if(activity instanceof FreshActivity){
            final FreshActivity freshActivity = ((FreshActivity) activity);
            placeRequestCode = freshActivity.getPlaceRequestCode();
            editAddress = freshActivity.isEditThisAddress();
            freshActivity.getTopBar().title.setText(activity.getString(R.string.confirm_address));
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
            if(!editAddress){
                placeRequestCode = getUpdatedPlaceRequestCode(placeRequestCode);
            }
        }

        editTextLabel.setEnabled(true);
        if(placeRequestCode == Constants.REQUEST_CODE_ADD_HOME){
            editTextLabel.setText(getString(R.string.home));
        } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
            editTextLabel.setText(getString(R.string.work));
        } else {
            editTextLabel.setText(label);
        }
        if(TextUtils.isEmpty(label)){
            label = editTextLabel.getText().toString();
        }
        editTextFlatNumber.requestFocus();
        editTextFlatNumber.setSelection(editTextFlatNumber.getText().length());

        editThisAddress = editAddress;

        relativeLayoutTypeHome.setVisibility(View.VISIBLE);
        relativeLayoutTypeWork.setVisibility(View.VISIBLE);
        relativeLayoutTypeOther.setVisibility(View.VISIBLE);
        editTextLabel.setVisibility(View.GONE);
        if (showAddressLabels) {
            if(placeRequestCode == Constants.REQUEST_CODE_ADD_HOME){
                label = activity.getString(R.string.home);
            } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
                label = activity.getString(R.string.work);
            } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION){
                editTextLabel.setVisibility(View.VISIBLE);
            }
        }
        lastLabel = label;
        setAddressTypeUI(label);
        addMarker();
    }

    public int getUpdatedPlaceRequestCode(int placeRequestCode) {
		if(placeRequestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION) {
			ArrayList<SearchResult> searchResults = MyApplication.getInstance().getHomeUtil().getSavedPlacesWithHomeWork(activity);
			boolean homeSaved = false, workSaved = false;
			for (SearchResult searchResult : searchResults) {
				if (!TextUtils.isEmpty(searchResult.getName())) {
					if (searchResult.getName().equalsIgnoreCase(Constants.TYPE_HOME)) {
						homeSaved = true;
					}
					if (searchResult.getName().equalsIgnoreCase(Constants.TYPE_WORK)) {
						workSaved = true;
					}
					if (homeSaved && workSaved) {
						placeRequestCode = Constants.REQUEST_CODE_ADD_NEW_LOCATION;
						return placeRequestCode;
					}
				}
			}
			if (!homeSaved) {
				placeRequestCode = Constants.REQUEST_CODE_ADD_HOME;
			} else {
				placeRequestCode = Constants.REQUEST_CODE_ADD_WORK;
			}
		}
        return placeRequestCode;
    }

    public void setNewArgumentsToUI(Bundle bundle){
        double oldLatitude = current_latitude;
        double oldLongitude = current_longitude;

        fetchAddressBundle(bundle);
        setDataToUI();

        locationEdited = (Utils.compareDouble(oldLatitude, current_latitude) != 0 && Utils.compareDouble(oldLongitude, current_longitude) != 0);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private boolean fieldsAreFilled() {

        if (showAddressLabels) {
            if(editTextLabel.getText().toString().trim().length() == 0){
                if(activity instanceof FreshActivity && ((FreshActivity)activity).getAnywhereHomeFragment() != null && ((FreshActivity)activity).getAnywhereHomeFragment().isPickUpAddressRequested()){
                    return true;
                }

                editTextLabel.requestFocus();
                if(editTextLabel.getVisibility() == View.GONE){
                    editTextLabel.setVisibility(View.VISIBLE);
                }
                editTextLabel.setError(getString(R.string.required_field));

                return false;
            }
        }
        if(editTextFlatNumber.getText().toString().trim().length() == 0){
            editTextFlatNumber.requestFocus();
            editTextFlatNumber.setError(getString(R.string.required_field));
            return false;
        }
        return true;
    }




    public boolean checkFields() {
        try {
            String label = editTextLabel.getText().toString().trim();
            boolean editThisAddress;
            int selfId = -1;
            if (activity instanceof AddPlaceActivity) {
                editThisAddress = ((AddPlaceActivity) activity).isEditThisAddress();
                if (editThisAddress) {
                    selfId = ((AddPlaceActivity) activity).getSearchResult().getId();
                }
            } else if (activity instanceof FreshActivity) {
                editThisAddress = ((FreshActivity) activity).isEditThisAddress();
                if (editThisAddress) {
                    selfId = ((FreshActivity) activity).getSearchResult().getId();
                }
            }

            boolean labelMatched = false;
            int newId = -1;
            String workString = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");
            if (!TextUtils.isEmpty(workString)) {
                SearchResult searchResult = new Gson().fromJson(workString, SearchResult.class);
                if (searchResult.getName().equalsIgnoreCase(label) && !searchResult.getId().equals(selfId)) {
                    labelMatched = true;
                    newId = searchResult.getId();
                }
            }

            String homeString = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
            if (!TextUtils.isEmpty(homeString)) {
                SearchResult searchResult = new Gson().fromJson(homeString, SearchResult.class);
                if (searchResult.getName().equalsIgnoreCase(label) && !searchResult.getId().equals(selfId)) {
                    labelMatched = true;
                    newId = searchResult.getId();
                }
            }

            if (Data.userData != null) {
                for (SearchResult searchResult : Data.userData.getSearchResults()) {
                    if (searchResult.getName().equalsIgnoreCase(label) && !searchResult.getId().equals(selfId)) {
                        labelMatched = true;
                        newId = searchResult.getId();
                        break;
                    }
                }
            }

            if (labelMatched) {
                final int finalSelfId = selfId;
                final int finalNewId = newId;
                DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
                        activity.getString(R.string.address_label_matched_message_format, label),
                        activity.getString(R.string.yes), activity.getString(R.string.no),
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


            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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


                FreshActivity freshActivity = (FreshActivity) activity;
                deliveryAddressesFragment = freshActivity.getDeliveryAddressesFragment();


                if (showAddressLabels) {
                    ((FreshActivity) activity).setSelectedAddress(localAddress);
                    ((FreshActivity)activity).setSelectedLatLng(new LatLng(current_latitude, current_longitude));
                    ((FreshActivity)activity).setSelectedAddressId(0);
                    ((FreshActivity)activity).setSelectedAddressType("");


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

            }
            else if(activity instanceof AddPlaceActivity){
                AddPlaceActivity addPlaceActivity = (AddPlaceActivity) activity;
                deliveryAddressesFragment = addPlaceActivity.getDeliveryAddressesFragment();
                if (showAddressLabels) {
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
            }


            if (showAddressLabels) {
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
                    label = Utils.firstCharCapital(Constants.TYPE_HOME);
                } else if(placeRequestCode == Constants.REQUEST_CODE_ADD_WORK){
                    label = Utils.firstCharCapital(Constants.TYPE_WORK);
                } else {
                    label = editTextLabel.getText().toString().trim();
                }
            }

            SearchResult searchResult = new SearchResult(label, localAddress, placeId, current_latitude, current_longitude);
            searchResult.setId(searchResultId);

            if(activity instanceof FreshActivity){
                FreshActivity freshActivity = (FreshActivity) activity;
                if(freshActivity.getAnywhereHomeFragment() != null){
                    freshActivity.getAnywhereHomeFragment().setRequestedAddress(searchResult);
                }else if(freshActivity.getSuggestAStoreFragment() != null){
                    freshActivity.getSuggestAStoreFragment().setAddress(searchResult);
                }else{
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
            }


            if (showAddressLabels) {
                if(activity instanceof AddPlaceActivity) {
                    ((AddPlaceActivity)activity).hitApiAddHomeWorkAddress(searchResult, false, otherId, editThisAddress, placeRequestCode);
                } else if(activity instanceof FreshActivity) {
                    if(label.length() > 0) {
                        ((FreshActivity) activity).hitApiAddHomeWorkAddress(searchResult, false, otherId, editThisAddress, placeRequestCode);
                    } else{
                        if(deliveryAddressesFragment != null) {
                            activity.getSupportFragmentManager().popBackStack(DeliveryAddressesFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                    }
                }
            }else{
                if(deliveryAddressesFragment != null) {
                    activity.getSupportFragmentManager().popBackStack(DeliveryAddressesFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
        String flatNo = editTextFlatNumber.getText().toString().trim();
        String landmark = editTextLandmark.getText().toString().trim();

        if (!TextUtils.isEmpty(flatNo)) {
            flatNo = flatNo + ", ";
        }

        if (!TextUtils.isEmpty(landmark)) {
            landmark = ", " + landmark;
        }
        return flatNo+textViewAddress.getText().toString()+landmark;
    }

}
