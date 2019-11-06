package product.clicklabs.jugnoo.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse;
import com.sabkuchfresh.widgets.LockableBottomSheetBehavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CancellationException;

import kotlinx.coroutines.Job;
import product.clicklabs.jugnoo.AddPlaceActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.LocationFetcher;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.SavedPlacesAdapter;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.apis.ApiAddHomeWorkAddress;
import product.clicklabs.jugnoo.apis.GoogleAPICoroutine;
import product.clicklabs.jugnoo.datastructure.GAPIAddress;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.room.DBObject;
import product.clicklabs.jugnoo.room.apis.DBCoroutine;
import product.clicklabs.jugnoo.room.database.SearchLocationDB;
import product.clicklabs.jugnoo.room.model.SearchLocation;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapStateListener;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.TouchableMapFragment;
import product.clicklabs.jugnoo.utils.Utils;


public class PlaceSearchListFragment extends Fragment implements  Constants {
	
	private LinearLayout linearLayoutRoot;

	private EditText editTextSearch;
	private ProgressWheel progressBarSearch;
	private ImageView imageViewSearchCross, imageViewSearchGPSIcon;

	private LinearLayout linearLayoutAddFav, llFinalAddress;
	private RelativeLayout relativeLayoutAddHome, relativeLayoutAddWork, relativeLayoutSavedPlaces, rlAddress;
	private TextView textViewAddHome, textViewAddWork;
	private ImageView imageViewSep, imageViewSep2, ivDivSavedPlaces, imageViewShadow;

	private ScrollView scrollViewSearch;
	private NonScrollListView listViewSearch;
	private CardView cardViewSearch;

	private NestedScrollView scrollViewSuggestions;
	private TextView textViewSavedPlaces, textViewRecentAddresses, tvFullAddress;
	private NonScrollListView listViewSavedLocations, listViewRecentAddresses;
	private SavedPlacesAdapter savedPlacesAdapter, savedPlacesAdapterRecent;
	private CardView cardViewSavedPlaces;
//	, cvRecentAddresses;

	private View rootView;
    private Activity activity;
	private SearchListAdapter.SearchListActionsHandler searchListActionsHandler;
	private SearchListAdapter searchListAdapter;
	private LockableBottomSheetBehavior<NestedScrollView> bottomSheetBehaviour;
	private CoordinatorLayout coordinatorLayout;
	private int newState;
	private RelativeLayout rootLayout;
	private GoogleMap googleMap;
	private RelativeLayout rlMarkerPin;
	private Button bNext;
	private View mapFragment;
	private ImageView imageViewSearchCrossDest;
	private EditText editTextSearchDest, etPreAddress;
	private ProgressWheel progressBarSearchDest;
	private SearchResult searchResultPickup,searchResultDestination;
	private ImageView ivLocationMarker, ivSearch, ivSearchDest;
	private boolean isMarkerSet = false;
	private List<SearchLocation> searchLocations = new ArrayList<>();

	private LinearLayout llSavedPlaces, llSetLocationOnMap;
	private View vSetLocationOnMapDiv;
	private boolean setLocationOnMapOnTop = true;
	private LatLng lastGeocodeLatLng;
	private GoogleGeocodeResponse lastGeocodeResponse;
	private String lastSingleAddress;
	private boolean isNewUI = false;

	private SearchListAdapter.SearchListActionsHandler searchAdapterListener = new SearchListAdapter.SearchListActionsHandler() {

		@Override
		public void onTextChange(String text) {
			setFocusedSearchResult(null, false);
			ImageView imageViewSearchCross;

			if (searchMode == PlaceSearchMode.PICKUP_AND_DROP.getOrdinal() && editTextSearch.hasFocus()) {

				imageViewSearchCross = PlaceSearchListFragment.this.imageViewSearchCross;
			} else {
				imageViewSearchCross = PlaceSearchListFragment.this.imageViewSearchCrossDest;
			}

			try {
				if (text.length() > 0) {
					scrollViewSearch.setVisibility(View.VISIBLE);
					imageViewSearchCross.setVisibility(View.VISIBLE);
					hideSearchLayout();
				} else {
					scrollViewSearch.setVisibility(View.GONE);
					imageViewSearchCross.setVisibility(View.GONE);
					showSearchLayout();
				}
				if (searchListActionsHandler != null) {
					searchListActionsHandler.onTextChange(text);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onSearchPre() {
			setFocusedSearchResult(null, false);
			getFocussedProgressBar().setVisibility(View.VISIBLE);
			if (searchListActionsHandler != null) {
				searchListActionsHandler.onSearchPre();
			}
		}

		@Override
		public void onSearchPost() {
			setFocusedSearchResult(null, false);
			getFocussedProgressBar().setVisibility(View.GONE);
			if (searchListActionsHandler != null) {
				searchListActionsHandler.onSearchPost();
			}
		}

		@Override
		public void onPlaceClick(SearchResult autoCompleteSearchResult) {
			setFocusedSearchResult(null, false);
			if (searchListActionsHandler != null) {
				searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
			}
		}

		@Override
		public void onPlaceSearchPre() {
			setFocusedSearchResult(null, false);
			getFocussedProgressBar().setVisibility(View.VISIBLE);
			if (searchListActionsHandler != null) {
				searchListActionsHandler.onPlaceSearchPre();
			}
		}

		@Override
		public void onPlaceSearchPost(SearchResult searchResult, PlaceSearchMode placeSearchMode) {

			getFocussedProgressBar().setVisibility(View.GONE);
			scrollViewSearch.setVisibility(View.GONE);

			if (searchMode == PlaceSearchMode.PICKUP_AND_DROP.getOrdinal()) {
				setFocusedSearchResult(searchResult, false);

				if (searchResultPickup != null && searchResultDestination != null) {
					if (searchListActionsHandler != null) {
						searchListActionsHandler.onPlaceSearchPost(searchResultPickup, PlaceSearchMode.PICKUP);
						searchListActionsHandler.onPlaceSearchPost(searchResultDestination, PlaceSearchMode.DROP);
					}

				}
			} else {

				if (searchListActionsHandler != null) {
					searchListActionsHandler.onPlaceSearchPost(searchResult, null);
				}

			}

		}

		@Override
		public void onPlaceSearchError() {
			setFocusedSearchResult(null, false);
			getFocussedProgressBar().setVisibility(View.GONE);
			if (searchListActionsHandler != null) {
				searchListActionsHandler.onPlaceSearchError();
			}
		}

		@Override
		public void onPlaceSaved() {
		}

		@Override
		public void onNotifyDataSetChanged(int count) {
			if (count > 0) {
				cardViewSearch.setVisibility(View.VISIBLE);
			} else {
				cardViewSearch.setVisibility(View.GONE);
			}
			if (searchListActionsHandler != null) {
				searchListActionsHandler.onNotifyDataSetChanged(count);
			}
		}

		@Override
		public void onSetLocationOnMapClicked() {
			llSetLocationOnMap.performClick();
			scrollViewSearch.setVisibility(View.GONE);
		}
	};;

	public static PlaceSearchListFragment newInstance(Bundle bundle){
		PlaceSearchListFragment fragment = new PlaceSearchListFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
	private int searchMode,searchModeClicked;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try{
			this.searchListActionsHandler = (SearchListAdapter.SearchListActionsHandler) context;
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_place_search_list, container, false);

		mapFragment = rootView.findViewById(R.id.googleMap);
		repositionMyLocationButton();
        activity = getActivity();
		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		rootLayout = (RelativeLayout) rootView.findViewById(R.id.rootLayout);

		setLocationOnMapOnTop = Prefs.with(activity).getInt(KEY_CUSTOMER_LOCATION_ON_MAP_ON_TOP, 0) == 1;

		new ASSL(activity, rootLayout, 1134, 720, false);


		rlMarkerPin = (RelativeLayout) rootView.findViewById(R.id.rlMarkerPin);
		bNext = (Button) rootView.findViewById(R.id.bNext);
		progressBarSearch = (ProgressWheel) rootView.findViewById(R.id.progressBarSearch); progressBarSearch.setVisibility(View.GONE);
		imageViewSearchCross = (ImageView) rootView.findViewById(R.id.ivDeliveryAddressCross); imageViewSearchCross.setVisibility(View.GONE);
		progressBarSearchDest = (ProgressWheel) rootView.findViewById(R.id.progressBarSearchDest); progressBarSearchDest.setVisibility(View.GONE);
		imageViewSearchCrossDest = (ImageView) rootView.findViewById(R.id.ivDeliveryAddressCrossDest); imageViewSearchCrossDest.setVisibility(View.GONE);
		listViewSearch = (NonScrollListView) rootView.findViewById(R.id.listViewSearch);
		scrollViewSearch = (ScrollView) rootView.findViewById(R.id.scrollViewSearch);
		rlAddress = rootView.findViewById(R.id.rlAddress);
		etPreAddress = rootView.findViewById(R.id.etPreAddress);
		tvFullAddress = rootView.findViewById(R.id.tvFullAddress);
		llFinalAddress = rootView.findViewById(R.id.llFinalAddress);
		ivSearch = rootView.findViewById(R.id.ivSearch);
		ivSearchDest = rootView.findViewById(R.id.ivSearchDest);
		scrollViewSearch.setVisibility(View.GONE);
		cardViewSearch = (CardView) rootView.findViewById(R.id.cardViewSearch);


		if(activity instanceof HomeActivity ) {
			isNewUI = ((HomeActivity)activity).isNewUI() && Prefs.with(activity).getInt(KEY_CUSTOMER_REMOVE_PICKUP_ADDRESS_HIT, 0) == 1;
		}


		ivLocationMarker = rootView.findViewById(R.id.ivLocationMarker);

		if(showBouncingMarker()) {
			ivLocationMarker.setImageResource(R.drawable.ic_bounce_pin);
		} else {
			ivLocationMarker.setImageResource(R.drawable.ic_delivery_address_map);
		}

		ivLocationMarker.setOnClickListener(view -> {
			if(showBouncingMarker()) {
				if (bottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_COLLAPSED && !isMarkerSet)
					fillAddressDetails(PlaceSearchListFragment.this.googleMap.getCameraPosition().target, false, false);
				stopAnimation();
			}
		});

		scrollViewSuggestions = (NestedScrollView) rootView.findViewById(R.id.scrollViewSuggestions);
		textViewSavedPlaces = (TextView) rootView.findViewById(R.id.textViewSavedPlaces); textViewSavedPlaces.setTypeface(Fonts.mavenMedium(activity));
		textViewRecentAddresses = (TextView) rootView.findViewById(R.id.textViewRecentAddresses); textViewRecentAddresses.setTypeface(Fonts.mavenMedium(activity));
		listViewSavedLocations = (NonScrollListView) rootView.findViewById(R.id.listViewSavedLocations);
		listViewRecentAddresses = (NonScrollListView) rootView.findViewById(R.id.listViewRecentAddresses);
		cardViewSavedPlaces = (CardView) rootView.findViewById(R.id.cardViewSavedPlaces);
//		cvRecentAddresses = (CardView) rootView.findViewById(R.id.cardViewRecentAddresses);

		imageViewSearchGPSIcon = (ImageView) rootView.findViewById(R.id.imageViewSearchGPSIcon);


		editTextSearch = (EditText) rootView.findViewById(R.id.editTextSearch);
		Bundle bundle = getArguments();

		searchMode = bundle.getInt(KEY_SEARCH_MODE, PlaceSearchMode.PICKUP.getOrdinal());
		searchModeClicked = bundle.getInt(KEY_SEARCH_MODE_CLICKED, PlaceSearchMode.DROP.getOrdinal());
//		searchMode = PlaceSearchMode.PICKUP_AND_DROP.getOrdinal();


		EditText[] editTextsForAdapter ;
		if(searchMode==PlaceSearchMode.PICKUP_AND_DROP.getOrdinal()){
			rootView.findViewById(R.id.rlAddressDest).setVisibility(View.VISIBLE);
			String hintPickup = getString(R.string.enter_pickup);
			String hintDestination = getString(R.string.assigning_state_edit_text_hint);


			initaliseAddressEditText(editTextSearch,imageViewSearchCross,imageViewSearchGPSIcon, hintPickup, hintPickup, PlaceSearchMode.PICKUP.getOrdinal());//For Pickup

			editTextSearchDest = rootView.findViewById(R.id.editTextSearchDest);
			ImageView imageViewSearchGPSIconDest = rootView.findViewById(R.id.imageViewSearchGPSIconDest);
			initaliseAddressEditText(editTextSearchDest, imageViewSearchCrossDest,imageViewSearchGPSIconDest,hintDestination, hintDestination, PlaceSearchMode.DROP.getOrdinal());//For Drop
			editTextsForAdapter = new EditText[]{editTextSearch,editTextSearchDest};
		}else{

			String text = bundle.getString(KEY_SEARCH_FIELD_TEXT, "");
			String hint = bundle.getString(KEY_SEARCH_FIELD_HINT, "");
			editTextsForAdapter = new EditText[]{editTextSearch};
			rootView.findViewById(R.id.rlAddressDest).setVisibility(View.GONE);
			initaliseAddressEditText(editTextSearch,imageViewSearchCross,imageViewSearchGPSIcon, text, hint, searchMode);

		}

		searchListAdapter = new SearchListAdapter(activity, getPivotLatLng(activity), searchMode,
				searchAdapterListener, true, setLocationOnMapOnTop, editTextsForAdapter);


		ViewGroup header = (ViewGroup)activity.getLayoutInflater().inflate(R.layout.header_place_search_list, listViewSearch, false);
		header.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
		listViewSavedLocations.addFooterView(header, null, false);

		linearLayoutAddFav = (LinearLayout) header.findViewById(R.id.linearLayoutAddFav);
		relativeLayoutAddHome = (RelativeLayout)header.findViewById(R.id.relativeLayoutAddHome);
		relativeLayoutAddWork = (RelativeLayout)header.findViewById(R.id.relativeLayoutAddWork);
		relativeLayoutSavedPlaces = (RelativeLayout)header.findViewById(R.id.relativeLayoutSavedPlaces);
		textViewAddHome = (TextView)header.findViewById(R.id.textViewAddHome); textViewAddHome.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
		textViewAddWork = (TextView)header.findViewById(R.id.textViewAddWork); textViewAddWork.setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
		((TextView)header.findViewById(R.id.textViewSavedPlaces)).setTypeface(Fonts.mavenMedium(activity), Typeface.BOLD);
		imageViewSep = (ImageView) header.findViewById(R.id.imageViewSep);
		imageViewSep2 = (ImageView) header.findViewById(R.id.imageViewSep2);
		ivDivSavedPlaces = (ImageView) header.findViewById(R.id.ivDivSavedPlaces);

		listViewSearch.setAdapter(searchListAdapter);



		relativeLayoutAddHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(activity, AddPlaceActivity.class);
				intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_HOME);
				intent.putExtra(Constants.KEY_ADDRESS, Prefs.with(activity).getString(SPLabels.ADD_HOME, ""));
				startActivityForResult(intent, Constants.REQUEST_CODE_ADD_HOME);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

		relativeLayoutAddWork.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(activity, AddPlaceActivity.class);
				intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_WORK);
				intent.putExtra(Constants.KEY_ADDRESS, Prefs.with(activity).getString(SPLabels.ADD_WORK, ""));
				startActivityForResult(intent, Constants.REQUEST_CODE_ADD_WORK);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		relativeLayoutSavedPlaces.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(activity, AddPlaceActivity.class);
				intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_NEW_LOCATION);
				intent.putExtra(Constants.KEY_ADDRESS, "");
				startActivityForResult(intent, Constants.REQUEST_CODE_ADD_NEW_LOCATION);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});





		imageViewShadow = (ImageView) rootView.findViewById(R.id.imageViewShadow);
		if(activity instanceof HomeActivity){
			imageViewShadow.setVisibility(View.VISIBLE);
		} else {
			imageViewShadow.setVisibility(View.GONE);
		}

		coordinatorLayout = rootView.findViewById(R.id.coordinatorLayout);
		bottomSheetBehaviour = (LockableBottomSheetBehavior)BottomSheetBehavior.from(scrollViewSuggestions);

		bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
		bottomSheetBehaviour.setLocked(true);
		bottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
				if(newState== BottomSheetBehavior.STATE_COLLAPSED){
					openSetLocationOnMapMode();
					if(googleMap!=null){
						enableMapMyLocation(googleMap, true);
					}
				}else{
					if(googleMap!=null){
						enableMapMyLocation(googleMap, false);

					}
				}
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {

			}
		});
		llSavedPlaces = rootView.findViewById(R.id.llSavedPlaces);
		llSetLocationOnMap = rootView.findViewById(R.id.ll_set_location_on_map);
		vSetLocationOnMapDiv = rootView.findViewById(R.id.vSetLocationOnMapDiv);
		llSetLocationOnMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bottomSheetBehaviour.setPeekHeight(0);
				bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
			}
		});
		bNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mapSettledCanForward){
					Utils.hideSoftKeyboard(activity, editTextSearch);
					String address =  isNewUI ? (!TextUtils.isEmpty(etPreAddress.getText().toString()) ? etPreAddress.getText().toString().concat(", ") : "").concat(tvFullAddress.getText().toString()) : getFocusedEditText().getText().toString();
					if(address.equalsIgnoreCase(Constants.UNNAMED)){
						Utils.showToast(activity, getString(R.string.unable_to_fetch_address));
						return;
					}
					SearchResult autoCompleteSearchResult = new SearchResult("",address,"", lastLatFetched, lastLngFetched,0,1,0 );
					searchAdapterListener.onPlaceClick(autoCompleteSearchResult);
					searchAdapterListener.onPlaceSearchPre();
					searchAdapterListener.onPlaceSearchPost(autoCompleteSearchResult, null);
				}else{
					if(showBouncingMarker()) {
						if (bottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_COLLAPSED && !isMarkerSet)
							fillAddressDetails(PlaceSearchListFragment.this.googleMap.getCameraPosition().target, false, true);
						stopAnimation();
					}
					Utils.showToast(activity, activity.getString(R.string.please_wait));
				}


			}
		});
		setMap();

		searchListAdapter.addSavedLocationsToList();
		updateSavedPlacesLists();
		showSearchLayout();

		if(setLocationOnMapOnTop){
			LinearLayout.LayoutParams paramsLL = (LinearLayout.LayoutParams) llSetLocationOnMap.getLayoutParams();
			LinearLayout.LayoutParams paramsV = (LinearLayout.LayoutParams) vSetLocationOnMapDiv.getLayoutParams();
			llSavedPlaces.removeView(llSetLocationOnMap);
			llSavedPlaces.removeView(vSetLocationOnMapDiv);
			llSavedPlaces.addView(llSetLocationOnMap, 0, paramsLL);
			llSavedPlaces.addView(vSetLocationOnMapDiv, 1, paramsV);
		}

		setNewUIChanges();


        return rootView;
	}

	public void setNewUIChanges() {
		if(isNewUI){
			if(bottomSheetBehaviour.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
				coordinatorLayout.getMeasuredHeight();
				coordinatorLayout.post(new Runnable() {
					@Override
					public void run() {
						bottomSheetBehaviour.setPeekHeight(coordinatorLayout.getMeasuredHeight());
					}
				});
			} else {
				bottomSheetBehaviour.setPeekHeight(0);
			}

			rlAddress.setVisibility(View.GONE);

			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cardViewSavedPlaces.getLayoutParams();
			params.setMarginStart(0);
			params.setMarginEnd(0);
			params.bottomMargin = 0;
			cardViewSavedPlaces.setLayoutParams(params);
			cardViewSavedPlaces.setRadius(0);

			llSavedPlaces.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
			imageViewShadow.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
			scrollViewSuggestions.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));

			imageViewSep.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
			imageViewSep2.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
			ivDivSavedPlaces.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
			vSetLocationOnMapDiv.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
		} else {
			bottomSheetBehaviour.setPeekHeight(0);

			rlAddress.setVisibility(View.VISIBLE);

			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cardViewSavedPlaces.getLayoutParams();
			params.setMarginStart((int)(ASSL.Xscale()*16F));
			params.setMarginEnd((int)(ASSL.Xscale()*16F));
			params.bottomMargin = (int)(ASSL.Xscale()*16F);
			cardViewSavedPlaces.setLayoutParams(params);
			cardViewSavedPlaces.setRadius(ASSL.minRatio()*4F);

			llSavedPlaces.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
			imageViewShadow.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
			scrollViewSuggestions.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));

			imageViewSep.setBackgroundColor(ContextCompat.getColor(activity, R.color.fatafat_divider_color));
			imageViewSep2.setBackgroundColor(ContextCompat.getColor(activity, R.color.fatafat_divider_color));
			ivDivSavedPlaces.setBackgroundColor(ContextCompat.getColor(activity, R.color.fatafat_divider_color));
			vSetLocationOnMapDiv.setBackgroundColor(ContextCompat.getColor(activity, R.color.fatafat_divider_color));
		}
	}

	private ProgressWheel getFocussedProgressBar() {
		return searchMode== PlaceSearchMode.PICKUP_AND_DROP.getOrdinal()
                                    && editTextSearchDest.hasFocus()?progressBarSearchDest:PlaceSearchListFragment.this.progressBarSearch;
	}

	private void initaliseAddressEditText(final EditText editTextSearch, final ImageView imageViewSearchCross, ImageView imageViewSearchGPSIcon, String text, String hint, final int searchMode) {
		editTextSearch.setTypeface(Fonts.mavenMedium(activity));
		editTextSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!isNewUI) {
					editTextSearch.requestFocus();
					Utils.showSoftKeyboard(activity, editTextSearch);
				}
			}
		});

		editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){

					if (PlaceSearchListFragment.this.searchMode == PlaceSearchMode.PICKUP_AND_DROP.getOrdinal()) {
						if(searchResultPickup==null){
                            clearExistingAddress(PlaceSearchListFragment.this.editTextSearch);
                        }
						if(searchResultDestination==null){
                            clearExistingAddress(editTextSearchDest);
                        }

						if((v.getId()==PlaceSearchListFragment.this.editTextSearch.getId() && searchResultPickup==null)
                            || (v.getId()==PlaceSearchListFragment.this.editTextSearchDest.getId() && searchResultDestination==null)){
                            openBottomSheetMode();

                        }
					}

				}

			  }
			}
		);
		imageViewSearchCross.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					clearExistingAddress(editTextSearch);
					startAnimation();
					clearBottomAddress();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


		editTextSearch.setText(text);
		editTextSearch.setHint(hint);


		if (editTextSearch.getId()==PlaceSearchListFragment.this.editTextSearch.getId()) {
			new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                	if(!isNewUI) {
						editTextSearch.requestFocus();
						editTextSearch.setSelection(editTextSearch.getText().length());
						Utils.showSoftKeyboard(activity, editTextSearch);
					}
                }
            }, 200);

		}





	}

	private void clearExistingAddress(EditText editTextSearch) {
		editTextSearch.setText("");
		getFocusedSearchIcon().setVisibility(View.VISIBLE);
		getFocusedCross().setVisibility(View.GONE);
		if(editTextSearch.getId()==PlaceSearchListFragment.this.editTextSearch.getId()){
            searchResultPickup = null;
        }else{
            searchResultDestination=null;
        }
	}


	private void showSearchLayout(){
		String home = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
		String work = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");

		if(home.equalsIgnoreCase("") || work.equalsIgnoreCase("")
				|| Prefs.with(activity).getInt(Constants.KEY_CUSTOMER_SHOW_ADD_SAVED_PLACE, 0) == 1){
			linearLayoutAddFav.setVisibility(View.VISIBLE);
			cardViewSavedPlaces.setVisibility(View.VISIBLE);
		} else{
			linearLayoutAddFav.setVisibility(View.GONE);
		}

		if(home.equalsIgnoreCase("")){
			relativeLayoutAddHome.setVisibility(View.VISIBLE);
			imageViewSep.setVisibility(View.VISIBLE);
		}else{
			relativeLayoutAddHome.setVisibility(View.GONE);
			imageViewSep.setVisibility(View.GONE);
		}

		if(work.equalsIgnoreCase("")){
			relativeLayoutAddWork.setVisibility(View.VISIBLE);
			imageViewSep2.setVisibility(View.VISIBLE);
		}else{
			relativeLayoutAddWork.setVisibility(View.GONE);
			imageViewSep2.setVisibility(View.GONE);
		}

		if(Prefs.with(activity).getInt(Constants.KEY_CUSTOMER_SHOW_ADD_SAVED_PLACE, 0) == 1){
			relativeLayoutSavedPlaces.setVisibility(View.VISIBLE);
			ivDivSavedPlaces.setVisibility(View.VISIBLE);
		} else {
			relativeLayoutSavedPlaces.setVisibility(View.GONE);
			ivDivSavedPlaces.setVisibility(View.GONE);
		}

		if(savedPlacesAdapter != null && savedPlacesAdapter.getCount() == 0){
			imageViewSep.setVisibility(View.GONE);
		}

	}

	private void hideSearchLayout(){
		relativeLayoutAddHome.setVisibility(View.GONE);
		relativeLayoutAddWork.setVisibility(View.GONE);
		imageViewSep.setVisibility(View.GONE);
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
		if(jobGeocode != null){
			jobGeocode.cancel(new CancellationException());
		}
        System.gc();
	}

	@Override
	public void onResume() {
		super.onResume();
		enableMapMyLocation(googleMap, true);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			if(resultCode == Activity.RESULT_OK) {
				if (requestCode == Constants.REQUEST_CODE_ADD_HOME) {
					String strResult = data.getStringExtra("PLACE");
					SearchResult searchResult = new Gson().fromJson(strResult, SearchResult.class);
					if(searchResult != null){
						updateSavedPlacesLists();
						showSearchLayout();
					} else {
						textViewAddHome.setText(R.string.add_home);
					}

				} else if (requestCode == Constants.REQUEST_CODE_ADD_WORK) {
					String strResult = data.getStringExtra("PLACE");
					SearchResult searchResult = new Gson().fromJson(strResult, SearchResult.class);
					if(searchResult != null) {
						updateSavedPlacesLists();
						showSearchLayout();
					} else{
						textViewAddWork.setText(R.string.add_work);
					}
				} else if (requestCode == Constants.REQUEST_CODE_ADD_NEW_LOCATION) {
					String strResult = data.getStringExtra("PLACE");
					SearchResult searchResult = new Gson().fromJson(strResult, SearchResult.class);
					if(searchResult != null) {
						updateSavedPlacesLists();
						showSearchLayout();
					}
				}

			}
			if(searchListActionsHandler != null){
				searchListActionsHandler.onPlaceSaved();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ProgressWheel getProgressBarSearch(){
		return getFocussedProgressBar();
	}

	public enum PlaceSearchMode {
		PICKUP(1),
		DROP(2),
		PICKUP_AND_DROP(3);

		private int ordinal;
		PlaceSearchMode(int ordinal){
			this.ordinal = ordinal;
		}


		public int getOrdinal() {
			return ordinal;
		}

		public void setOrdinal(int ordinal) {
			this.ordinal = ordinal;
		}
	}

	private void clickOnSavedItem(SearchResult searchResult){
		if(searchAdapterListener != null) {
			searchAdapterListener.onPlaceClick(searchResult);
			searchAdapterListener.onPlaceSearchPre();
			searchAdapterListener.onPlaceSearchPost(searchResult, null);
		}
		Utils.hideSoftKeyboard(activity, editTextSearch);
	}

	private HomeUtil homeUtil = new HomeUtil();

	private void updateSavedPlacesLists(){
		try {
			ArrayList<SearchResult> searchResults = sortSearchResults(homeUtil.getSavedPlacesWithHomeWork(activity), getPivotLatLng(activity));
			int savedPlaces = searchResults.size();

			if(savedPlacesAdapter == null) {
				savedPlacesAdapter = new SavedPlacesAdapter(activity, searchResults, new SavedPlacesAdapter.Callback() {
					@Override
					public void onItemClick(SearchResult searchResult) {
						clickOnSavedItem(searchResult);
					}

					@Override
					public void onDeleteClick(SearchResult searchResult) {
					}

					@Override
					public void onItemLongClick(SearchResult searchResult) {
						deleteAddressDialog(searchResult);
					}
				}, false, false, false, false);
				listViewSavedLocations.setAdapter(savedPlacesAdapter);
			} else {
				savedPlacesAdapter.setList(searchResults);
			}
			if(searchResults.size() > 0){
				cardViewSavedPlaces.setVisibility(View.VISIBLE);
				textViewSavedPlaces.setText(savedPlacesAdapter.getCount() == 1 ? R.string.saved_location : R.string.saved_locations);
			} else {
				cardViewSavedPlaces.setVisibility(View.GONE);
			}

			SearchLocationDB searchLocationDB = DBObject.INSTANCE.getInstance();

			if(searchLocationDB != null) {
				if (PlaceSearchMode.PICKUP.getOrdinal() == PlaceSearchListFragment.this.searchMode) {
					DBCoroutine.Companion.getAllLocations(searchLocationDB, searchLocation -> {
						if (!searchLocations.isEmpty()) {
							searchLocations.clear();
						}
						searchLocations.addAll(searchLocation);
						setRecentList();
					});
				} else {
					DBCoroutine.Companion.getAllLocations(searchLocationDB, searchLocation -> {
						if (!searchLocations.isEmpty()) {
							searchLocations.clear();
						}
						searchLocations.addAll(searchLocation);
						setRecentList();
					});
				}
			}

			if(savedPlaces > 0) {
				textViewSavedPlaces.setVisibility(View.VISIBLE);
			} else {
				textViewSavedPlaces.setVisibility(View.GONE);
			}

			if (savedPlacesAdapterRecent.getCount() > 0) {
				textViewRecentAddresses.setVisibility(View.VISIBLE);
				listViewRecentAddresses.setVisibility(View.VISIBLE);
				textViewRecentAddresses.setText(savedPlacesAdapterRecent.getCount() == 1 ? R.string.recent_location : R.string.recent_locations);
			} else {
				textViewRecentAddresses.setVisibility(View.GONE);
				listViewRecentAddresses.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setRecentList() {
		ArrayList<SearchResult> searchResultList = getSearchResultsRecentAndSaved(activity, searchLocations);
		if(savedPlacesAdapterRecent == null) {

			savedPlacesAdapterRecent = new SavedPlacesAdapter(activity, searchResultList, new SavedPlacesAdapter.Callback() {
				@Override
				public void onItemClick(SearchResult searchResult) {
					clickOnSavedItem(searchResult);
				}

				@Override
				public void onDeleteClick(SearchResult searchResult) {
				}

				@Override
				public void onAddClick(SearchResult searchResult) {
					onSavedLocationEdit(searchResult);
				}
			}, false, false, false, false);
			listViewRecentAddresses.setAdapter(savedPlacesAdapterRecent);
		} else {
			savedPlacesAdapterRecent.setList(searchResultList);
		}
		if(searchResultList.size() > 0){
//				cvRecentAddresses.setVisibility(View.VISIBLE);
			textViewRecentAddresses.setVisibility(View.VISIBLE);
			listViewRecentAddresses.setVisibility(View.VISIBLE);
		} else{
//				cvRecentAddresses.setVisibility(View.GONE);
			textViewRecentAddresses.setVisibility(View.GONE);
			listViewRecentAddresses.setVisibility(View.GONE);
		}
	}

	private boolean showBouncingMarker(){
		return Prefs.with(activity).getInt(Constants.KEY_CUSTOMER_SHOW_BOUNCING_MARKER, 0) == 1;
	}

	@NonNull
	public static ArrayList<SearchResult> getSearchResultsRecentAndSaved(Context context, List<SearchLocation> searchLocations) {
		ArrayList<SearchResult> searchResultList = new ArrayList<>(Data.userData.getSearchResultsRecent());
		if(searchLocations != null) {
			for (int i = 0; i < searchLocations.size(); i++) {
				SearchResult searchResult = new SearchResult(searchLocations.get(i).getName(), searchLocations.get(i).getAddress(), searchLocations.get(i).getPlaceId(),
						searchLocations.get(i).getSlat(), searchLocations.get(i).getSLng());
				searchResult.setType(SearchResult.Type.RECENT);
				searchResultList.add(0, searchResult);
			}
		}
		return sortSearchResults(searchResultList, getPivotLatLng(context));
	}

	public static ArrayList<SearchResult> sortSearchResults(ArrayList<SearchResult> searchResults, LatLng latLng){

		Collections.sort(searchResults, new Comparator<SearchResult>() {
			@Override
			public int compare(SearchResult lhs, SearchResult rhs) {
				double lhsDist = MapUtils.distance(latLng, lhs.getLatLng());
				double rhsDist = MapUtils.distance(latLng, rhs.getLatLng());
				return (int)(lhsDist - rhsDist);
			}
		});
		return searchResults;
	}

	private boolean mapSettledCanForward;
	private void setMap() {
		((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(final GoogleMap googleMap) {
				PlaceSearchListFragment.this.googleMap = googleMap;
				if (googleMap != null) {
					googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					enableMapMyLocation(googleMap, true);
					googleMap.getUiSettings().setMyLocationButtonEnabled(false);
//					setupMapAndButtonMargins();
					moveCameraToCurrent();



					TouchableMapFragment mapFragment = ((TouchableMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap));
					new MapStateListener(googleMap, mapFragment, activity) {

						@Override
						public void onMapTouched() {
						}

						@Override
						public void onMapReleased() {
						}

						@Override
						public void onMapUnsettled() {
							mapSettledCanForward=false;
							if(!showBouncingMarker()) {
								setFetchedAddressToTextView("Loading...", true, true, true);
							}
							/*mapSettledCanForward = false;
							searchResultNearPin = null;*/
						}

						@Override
						public void moveMap() {
							startAnimation();
						}

						@Override
						public void onMapSettled() {
							if(getContext() != null) {
								if (!showBouncingMarker()) {
									if (bottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
										fillAddressDetails(PlaceSearchListFragment.this.googleMap.getCameraPosition().target, false, false);
									}
//								autoCompleteResultClicked = false;
								}
							}
						}

						@Override
						public void onCameraPositionChanged(CameraPosition cameraPosition) {

						}
					};

					if (PlaceSearchListFragment.this.searchMode == PlaceSearchMode.PICKUP_AND_DROP.getOrdinal()) {
						fillAddressDetails(googleMap.getCameraPosition().target,true, false);

					}

				}
			}
		});
	}

	private void enableMapMyLocation(GoogleMap googleMap, boolean enabled) {
		if(googleMap != null && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			googleMap.setMyLocationEnabled(enabled);
			googleMap.getUiSettings().setMyLocationButtonEnabled(enabled);
		}
	}

	private void moveCameraToCurrent(){
		if(getView() != null && googleMap != null) {
			if (activity instanceof AddPlaceActivity
					&& ((AddPlaceActivity) activity).isEditThisAddress()
					&& ((AddPlaceActivity) activity).getSearchResult() != null) {
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(((AddPlaceActivity) activity).getSearchResult().getLatLng(), 14));
			} else {
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLatLng(), 14));
				zoomToCurrentLocation();
			}
		}
	}
	void zoomToCurrentLocation(){
		try {
			if(googleMap != null
					&& MapUtils.distance(googleMap.getCameraPosition().target,
					getCurrentLatLng()) > 10){
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLatLng(), 14), 300, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private LatLng getCurrentLatLng(){
		if(Data.autoData != null && Data.autoData.getPickupLatLng()!=null){
			return Data.autoData.getPickupLatLng();
		} else {
			return new LatLng(Data.latitude, Data.longitude);
		}
	}

	private Job jobGeocode = null;
	private Double lastLatFetched ;
	private Double lastLngFetched ;
	private void fillAddressDetails(LatLng latLng, final boolean setSearchResult, final boolean isFromConfirm) {
		try {
			if(lastGeocodeLatLng != null && MapUtils.distance(latLng, lastGeocodeLatLng) <= 100 && (lastGeocodeResponse != null || lastSingleAddress != null)){
				setAddressToUI(lastGeocodeLatLng, lastGeocodeResponse, lastSingleAddress, setSearchResult, isFromConfirm);
				return;
			}
			getFocussedProgressBar().setVisibility(View.VISIBLE);

			lastLatFetched = latLng.latitude;
			lastLngFetched = latLng.longitude;

			if(jobGeocode != null){
				jobGeocode.cancel(new CancellationException());
			}
			jobGeocode = GoogleAPICoroutine.INSTANCE.hitGeocode(latLng, (googleGeocodeResponse, singleAddress) -> PlaceSearchListFragment.this.setAddressToUI(latLng, googleGeocodeResponse, singleAddress, setSearchResult, isFromConfirm));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void setFetchedAddressToTextView(String address, final boolean isHint, boolean isAddressConfirmed, boolean fromMapUnsettle){
		EditText editText = getFocusedEditText();
		if(!fromMapUnsettle && TextUtils.isEmpty(address)){
			address = Constants.UNNAMED;
			mapSettledCanForward = true;
		}
		if(searchListAdapter!=null){
			editText.removeTextChangedListener(searchListAdapter.getTextWatcherEditText(editText.getId()));
			if(isHint) {
				editText.setHint(address);
				editText.setText("");
				getFocusedSearchIcon().setVisibility(View.VISIBLE);
				getFocusedCross().setVisibility(View.GONE);
			} else {
				editText.setText(address);
				getFocusedSearchIcon().setVisibility(View.GONE);
				getFocusedCross().setVisibility(View.VISIBLE);
				editText.setSelection(address.length());
			}
			editText.addTextChangedListener(searchListAdapter.getTextWatcherEditText(editText.getId()));
		}else{
			if(isHint) {
				editText.setHint(address);
				editText.setText("");
				getFocusedSearchIcon().setVisibility(View.VISIBLE);
				getFocusedCross().setVisibility(View.GONE);
			} else {
				editText.setText(address);
				getFocusedSearchIcon().setVisibility(View.GONE);
				getFocusedCross().setVisibility(View.VISIBLE);
				editText.setSelection(address.length());
			}
		}
		setBottomAddressLayout(address, isHint, isAddressConfirmed);
		if(!isAddressConfirmed){
			if (getFocusedEditText().getId() == editTextSearch.getId()) {
				searchResultPickup = null;
			} else {
				searchResultDestination = null;

			}
		}
	}

	private void setBottomAddressLayout(String address, boolean isHint, boolean isAddressConfirmed) {
		if(isHint || !isNewUI) {
			llFinalAddress.setVisibility(View.GONE);
		} else {
			if (bNext.getVisibility() == View.VISIBLE) {
				int index = address.indexOf(",");
				if(index > 0) {
					etPreAddress.setText(address.substring(0, index));
					tvFullAddress.setText(address.substring(index + 1));
				} else {
					etPreAddress.setText("");
					tvFullAddress.setText(address);
				}
				llFinalAddress.setVisibility(View.VISIBLE);
			} else {
				clearBottomAddress();
			}
		}
	}

	private void clearBottomAddress() {
		etPreAddress.setText("");
		tvFullAddress.setText("");
		llFinalAddress.setVisibility(View.GONE);
	}

	private EditText getFocusedEditText() {
		return searchMode== PlaceSearchMode.PICKUP_AND_DROP.getOrdinal()
				&& editTextSearchDest.hasFocus()?editTextSearchDest:editTextSearch;
	}

	private View getFocusedCross() {
		return searchMode== PlaceSearchMode.PICKUP_AND_DROP.getOrdinal()
				&& editTextSearchDest.hasFocus()?imageViewSearchCrossDest:imageViewSearchCross;
	}

	private View getFocusedSearchIcon() {
		return searchMode== PlaceSearchMode.PICKUP_AND_DROP.getOrdinal()
				&& editTextSearchDest.hasFocus()?ivSearchDest:ivSearch;
	}


	private SearchResult getFocusedSearchResult(){
		return getFocusedEditText().getId() ==editTextSearch.getId()?searchResultPickup:searchResultDestination;
	}

	private void setFocusedSearchResult(SearchResult searchResult, boolean isFirstTime){
		if (getFocusedEditText().getId() == editTextSearch.getId()) {
			searchResultPickup = searchResult;
		} else {
			searchResultDestination = searchResult;

		}
		if(searchResult!=null){
			setFetchedAddressToTextView(searchResult.getAddress(), false, true, false);

			if(searchMode==PlaceSearchMode.PICKUP_AND_DROP.getOrdinal()){
				if(getFocusedEditText().getId()==editTextSearch.getId()){
					if(!(isFirstTime && searchModeClicked == PlaceSearchMode.PICKUP.getOrdinal())){
						editTextSearchDest.requestFocus();

					}
				}else{
					editTextSearch.requestFocus();
				}
			}

		}

	}
	public void openSetLocationOnMapMode(){
		try {
			bNext.setVisibility(View.VISIBLE);
			rlAddress.setVisibility(View.VISIBLE);
			if(bottomSheetBehaviour!=null && bottomSheetBehaviour.getState()!=BottomSheetBehavior.STATE_COLLAPSED){
				bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
			}
			rlMarkerPin.setVisibility(View.VISIBLE);
			if(showBouncingMarker()) {
				startAnimation();
			} else {
				if(googleMap != null) {
					fillAddressDetails(googleMap.getCameraPosition().target, false, false);
				}
			}
			Utils.hideSoftKeyboard(activity,editTextSearch);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startAnimation() {
		if(showBouncingMarker()) {
			setFetchedAddressToTextView(getString(R.string.tap_on_pin), true,true, true);
			if(ivLocationMarker.getAnimation() == null) {
				ivLocationMarker.clearAnimation();
				final Animation anim = AnimationUtils.loadAnimation(activity, R.anim.bounce_view);
				ivLocationMarker.startAnimation(anim);
			}
			isMarkerSet = false;
		}
	}

	private void stopAnimation() {
		ivLocationMarker.clearAnimation();
		isMarkerSet = true;
	}

	public void openBottomSheetMode(){
		bNext.setVisibility(View.GONE);
		llFinalAddress.setVisibility(View.GONE);
		if(bottomSheetBehaviour!=null && bottomSheetBehaviour.getState()!=BottomSheetBehavior.STATE_EXPANDED){
			bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
		}
		setNewUIChanges();
		rlMarkerPin.setVisibility(View.GONE);
	}

	public void repositionMyLocationButton(){
		try {
			View locationButton = ((View) mapFragment.findViewById(Integer.parseInt("1")).
                    getParent()).findViewById(Integer.parseInt("2"));

			// and next place it, for example, on bottom right (as Google Maps app)
			RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
			// position on right bottom
			rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
			rlp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			rlp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
			rlp.setMargins(0, 0, 40, 0);
			rlp.setMarginStart(0);
			rlp.setMarginEnd(40);
			locationButton.setLayoutParams(rlp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteAddressDialog(final SearchResult searchResult){
		DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
				getString(R.string.address_delete_confirm_message),
				getString(R.string.delete), getString(R.string.cancel),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						hitApiAddHomeWorkAddress(searchResult, true, 0, true, searchResult.getPlaceRequestCode());
					}
				},
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}, false, false);
	}

	private ApiAddHomeWorkAddress apiAddHomeWorkAddress;
	public void hitApiAddHomeWorkAddress(final SearchResult searchResult, final boolean deleteAddress, final int matchedWithOtherId,
										 final boolean editThisAddress, final int placeRequestCode){
		if(apiAddHomeWorkAddress == null){
			apiAddHomeWorkAddress = new ApiAddHomeWorkAddress(activity, new ApiAddHomeWorkAddress.Callback() {
				@Override
				public void onSuccess(SearchResult searchResult, String strResult, boolean addressDeleted, final String serverMsg) {
					updateSavedPlacesLists();
					showSearchLayout();
				}

				@Override
				public void onFailure() {

				}

				@Override
				public void onRetry(View view) {

				}

				@Override
				public void onNoRetry(View view) {

				}
			});
		}
		apiAddHomeWorkAddress.addHomeAndWorkAddress(searchResult, deleteAddress, matchedWithOtherId, editThisAddress, placeRequestCode);
	}

	private void onSavedLocationEdit(SearchResult searchResult){
		Intent intent = new Intent(activity, AddPlaceActivity.class);
		intent.putExtra(Constants.KEY_REQUEST_CODE, searchResult.getPlaceRequestCode());
		intent.putExtra(Constants.KEY_ADDRESS, new Gson().toJson(searchResult, SearchResult.class));
		intent.putExtra(Constants.KEY_DIRECT_CONFIRM, true);
		startActivityForResult(intent, searchResult.getPlaceRequestCode());
		activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}


	private void setAddressToUI(LatLng latLng, GoogleGeocodeResponse address, String singleAddress, boolean setSearchResult, final boolean isFromConfirm) {
		Log.i("PlaceSearchListFragment", "setAddressToUI address=" + address);
		GAPIAddress gapiAddress = null;
		if (address != null) {
			gapiAddress = MapUtils.parseGAPIIAddress(address);
		} else if(singleAddress != null){
			gapiAddress = new GAPIAddress(singleAddress);
		}
		if (gapiAddress != null && !TextUtils.isEmpty(gapiAddress.formattedAddress)) {
			if (setSearchResult) {
				SearchResult searchResult = new SearchResult("", gapiAddress.formattedAddress,
						"", lastLatFetched, lastLngFetched, 0, 1, 0);
				setFocusedSearchResult(searchResult, true);

			} else {
				setFetchedAddressToTextView(gapiAddress.formattedAddress, false, false, false);

			}
			lastGeocodeLatLng = latLng;
			lastGeocodeResponse = address;
			lastSingleAddress  = singleAddress;
			mapSettledCanForward = true;
		} else {
			Utils.showToast(activity, activity.getString(R.string.unable_to_fetch_address));
			setFetchedAddressToTextView("", false,false, false);
		}
		getFocussedProgressBar().setVisibility(View.GONE);

		if (isFromConfirm && !isNewUI) {
			bNext.performClick();
		}
	}

	public static LatLng getPivotLatLng(Context context){
		if(Data.autoData != null){
			if(Data.autoData.getLastRefreshLatLng() != null) {
				return Data.autoData.getLastRefreshLatLng();
			}
			else if(Data.autoData.getPickupLatLng() != null) {
				return Data.autoData.getPickupLatLng();
			}
			else if(HomeActivity.myLocation != null){
				return new LatLng(HomeActivity.myLocation.getLatitude(), HomeActivity.myLocation.getLongitude());
			}
			else if(Math.abs(Data.latitude) > 0 &&  Math.abs(Data.longitude) > 0){
				return new LatLng(Data.latitude, Data.longitude);
			}
			else {
				return new LatLng(LocationFetcher.getSavedLatFromSP(context), LocationFetcher.getSavedLngFromSP(context));
			}
		} else{
			return new LatLng(30.75, 76.78);
		}
	}
}
