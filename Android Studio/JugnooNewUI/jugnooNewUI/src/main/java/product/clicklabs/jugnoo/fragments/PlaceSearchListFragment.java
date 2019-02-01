package product.clicklabs.jugnoo.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import product.clicklabs.jugnoo.AddPlaceActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.FareEstimateActivity;
import product.clicklabs.jugnoo.GeocodeCallback;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.SavedPlacesAdapter;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.apis.ApiAddHomeWorkAddress;
import product.clicklabs.jugnoo.datastructure.GAPIAddress;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.GoogleRestApis;
import product.clicklabs.jugnoo.utils.LocaleHelper;
import product.clicklabs.jugnoo.utils.MapStateListener;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.TouchableMapFragment;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class PlaceSearchListFragment extends Fragment implements  Constants {
	
	private LinearLayout linearLayoutRoot;

	private EditText editTextSearch;
	private ProgressWheel progressBarSearch;
	private ImageView imageViewSearchCross, imageViewSearchGPSIcon;

	private LinearLayout linearLayoutAddFav;
	private RelativeLayout relativeLayoutAddHome, relativeLayoutAddWork, relativeLayoutSavedPlaces;
	private TextView textViewAddHome, textViewAddWork;
	private ImageView imageViewSep, imageViewSep2, ivDivSavedPlaces;

	private ScrollView scrollViewSearch;
	private NonScrollListView listViewSearch;
	private CardView cardViewSearch;

	private NestedScrollView scrollViewSuggestions;
	private TextView textViewSavedPlaces, textViewRecentAddresses;
	private NonScrollListView listViewSavedLocations, listViewRecentAddresses;
	private SavedPlacesAdapter savedPlacesAdapter, savedPlacesAdapterRecent;
	private CardView cardViewSavedPlaces;
//	, cvRecentAddresses;

	private View rootView;
    private Activity activity;
	private SearchListAdapter.SearchListActionsHandler searchListActionsHandler;
	private SearchListAdapter searchListAdapter;
	private LockableBottomSheetBehavior<NestedScrollView> bottomSheetBehaviour;
	private int newState;
	private RelativeLayout rootLayout;
	private GoogleMap googleMap;
	private RelativeLayout rlMarkerPin;
	private Button bNext;
	private View mapFragment;
	private ImageView imageViewSearchCrossDest;
	private EditText editTextSearchDest;
	private ProgressWheel progressBarSearchDest;
	private SearchResult searchResultPickup,searchResultDestination;
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
	};;

	public static PlaceSearchListFragment newInstance(Bundle bundle){
		PlaceSearchListFragment fragment = new PlaceSearchListFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
	private GeoDataClient geoDataClient;
	private int searchMode,searchModeClicked;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try{
			this.searchListActionsHandler = (SearchListAdapter.SearchListActionsHandler) context;
			if(context instanceof FareEstimateActivity){
				geoDataClient = ((FareEstimateActivity)context).getGeoDataClient();
			} else {
				geoDataClient = ((HomeActivity)context).getmGeoDataClient();
			}
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

		new ASSL(activity, rootLayout, 1134, 720, false);


		rlMarkerPin = (RelativeLayout) rootView.findViewById(R.id.rlMarkerPin);
		bNext = (Button) rootView.findViewById(R.id.bNext);
		progressBarSearch = (ProgressWheel) rootView.findViewById(R.id.progressBarSearch); progressBarSearch.setVisibility(View.GONE);
		imageViewSearchCross = (ImageView) rootView.findViewById(R.id.ivDeliveryAddressCross); imageViewSearchCross.setVisibility(View.GONE);
		progressBarSearchDest = (ProgressWheel) rootView.findViewById(R.id.progressBarSearchDest); progressBarSearchDest.setVisibility(View.GONE);
		imageViewSearchCrossDest = (ImageView) rootView.findViewById(R.id.ivDeliveryAddressCrossDest); imageViewSearchCrossDest.setVisibility(View.GONE);
		listViewSearch = (NonScrollListView) rootView.findViewById(R.id.listViewSearch);
		scrollViewSearch = (ScrollView) rootView.findViewById(R.id.scrollViewSearch);
		scrollViewSearch.setVisibility(View.GONE);
		cardViewSearch = (CardView) rootView.findViewById(R.id.cardViewSearch);


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

		searchListAdapter = new SearchListAdapter(activity, new LatLng(30.75, 76.78), geoDataClient, searchMode,
				searchAdapterListener, true,editTextsForAdapter);


		ViewGroup header = (ViewGroup)activity.getLayoutInflater().inflate(R.layout.header_place_search_list, listViewSearch, false);
		header.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
		ASSL.DoMagic(header);
		listViewSavedLocations.addFooterView(header, null, false);

		linearLayoutAddFav = (LinearLayout) header.findViewById(R.id.linearLayoutAddFav);
		relativeLayoutAddHome = (RelativeLayout)header.findViewById(R.id.relativeLayoutAddHome);
		relativeLayoutAddWork = (RelativeLayout)header.findViewById(R.id.relativeLayoutAddWork);
		relativeLayoutSavedPlaces = (RelativeLayout)header.findViewById(R.id.relativeLayoutSavedPlaces);
		textViewAddHome = (TextView)header.findViewById(R.id.textViewAddHome); textViewAddHome.setTypeface(Fonts.mavenMedium(activity));
		textViewAddWork = (TextView)header.findViewById(R.id.textViewAddWork); textViewAddWork.setTypeface(Fonts.mavenMedium(activity));
		((TextView)header.findViewById(R.id.textViewSavedPlaces)).setTypeface(Fonts.mavenMedium(activity));
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





		ImageView imageViewShadow = (ImageView) rootView.findViewById(R.id.imageViewShadow);
		if(activity instanceof HomeActivity){
			imageViewShadow.setVisibility(View.VISIBLE);
		} else {
			imageViewShadow.setVisibility(View.GONE);
		}

		bottomSheetBehaviour = (LockableBottomSheetBehavior)BottomSheetBehavior.from(scrollViewSuggestions);

		bottomSheetBehaviour.setPeekHeight(0);
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
		rootView.findViewById(R.id.ll_set_location_on_map).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
			}
		});
		rootView.findViewById(R.id.bNext).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mapSettledCanForward){
					Utils.hideSoftKeyboard(activity, editTextSearch);
					SearchResult autoCompleteSearchResult = new SearchResult("",getFocusedEditText().getText().toString(),"", lastLatFetched, lastLngFetched,0,1,0 );
					searchAdapterListener.onPlaceClick(autoCompleteSearchResult);
					searchAdapterListener.onPlaceSearchPre();
					searchAdapterListener.onPlaceSearchPost(autoCompleteSearchResult, null);
				}else{
					Utils.showToast(activity,activity.getString(R.string.please_wait));
				}


			}
		});
		setMap();

		searchListAdapter.addSavedLocationsToList();
		updateSavedPlacesLists();
		showSearchLayout();

        return rootView;
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
				editTextSearch.requestFocus();
				Utils.showSoftKeyboard(activity, editTextSearch);
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
                    editTextSearch.requestFocus();
                    editTextSearch.setSelection(editTextSearch.getText().length());
                    Utils.showSoftKeyboard(activity, editTextSearch);
                }
            }, 200);

		}

		if(searchMode == PlaceSearchMode.DROP.getOrdinal()){
			imageViewSearchGPSIcon.setImageResource(R.drawable.circle_red);
		} else{
			imageViewSearchGPSIcon.setImageResource(R.drawable.circle_green);
		}




	}

	private void clearExistingAddress(EditText editTextSearch) {
		editTextSearch.setText("");
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
			ArrayList<SearchResult> searchResults = homeUtil.getSavedPlacesWithHomeWork(activity);
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

			if(savedPlacesAdapterRecent == null) {
				savedPlacesAdapterRecent = new SavedPlacesAdapter(activity, Data.userData.getSearchResultsRecent(), new SavedPlacesAdapter.Callback() {
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
				}, false, false, false, true);
				listViewRecentAddresses.setAdapter(savedPlacesAdapterRecent);
			} else {
				savedPlacesAdapterRecent.setList(Data.userData.getSearchResultsRecent());
			}
			if(Data.userData.getSearchResultsRecent().size() > 0){
//				cvRecentAddresses.setVisibility(View.VISIBLE);
				textViewRecentAddresses.setVisibility(View.VISIBLE);
				listViewRecentAddresses.setVisibility(View.VISIBLE);
			} else{
//				cvRecentAddresses.setVisibility(View.GONE);
				textViewRecentAddresses.setVisibility(View.GONE);
				listViewRecentAddresses.setVisibility(View.GONE);
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
							setFetchedAddressToTextView("", true);
							/*mapSettledCanForward = false;
							searchResultNearPin = null;*/
						}

						@Override
						public void onMapSettled() {
						if(bottomSheetBehaviour.getState()==BottomSheetBehavior.STATE_COLLAPSED)
						  fillAddressDetails(PlaceSearchListFragment.this.googleMap.getCameraPosition().target,false);
//							autoCompleteResultClicked = false;
						}

						@Override
						public void onCameraPositionChanged(CameraPosition cameraPosition) {

						}
					};

					if (PlaceSearchListFragment.this.searchMode == PlaceSearchMode.PICKUP_AND_DROP.getOrdinal()) {
						fillAddressDetails(googleMap.getCameraPosition().target,true);

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


	private Double lastLatFetched ;
	private Double lastLngFetched ;
	private void fillAddressDetails(final LatLng latLng, final boolean setSearchResult) {
		try {
		/*
			// we need to check if the autoCompleteResult clicked latLng is near some saved place,
			// if yes this case will also behave like map pan near saved location case
			if(autoCompleteResultClicked) {
				mapSettledCanForward = true;
				return;
			}
*/
			/*if(isVisible() && !isRemoving()) {
				progressWheelDeliveryAddressPin.setVisibility(View.VISIBLE);
			}*/
			getFocussedProgressBar().setVisibility(View.VISIBLE);
			final Map<String, String> params = new HashMap<String, String>(6);

			params.put(Data.LATLNG, latLng.latitude + "," + latLng.longitude);
			params.put("language", Locale.getDefault().getCountry());
			params.put("sensor", "false");

			GoogleRestApis.INSTANCE.geocode(latLng.latitude + "," + latLng.longitude, LocaleHelper.getLanguage(activity), new GeocodeCallback(geoDataClient) {
				@Override
				public void onSuccess(GoogleGeocodeResponse geocodeResponse, Response response) {
					try {
						if (geocodeResponse.results != null && geocodeResponse.results.size() > 0) {
							lastLatFetched = latLng.latitude;
							lastLngFetched = latLng.longitude;
							GAPIAddress gapiAddress = MapUtils.parseGAPIIAddress(geocodeResponse);

							if(setSearchResult){
								getFocussedProgressBar().setVisibility(View.GONE);
								SearchResult searchResult  = new SearchResult("",gapiAddress.getSearchableAddress(),
										"", lastLatFetched, lastLngFetched,0,1,0 );
								setFocusedSearchResult(searchResult, true);

							}else{
								setFetchedAddressToTextView(gapiAddress.getSearchableAddress(), false);

							}
							mapSettledCanForward = true;
						} else {
							Utils.showToast(activity, activity.getString(R.string.unable_to_fetch_address));
							setFetchedAddressToTextView("", false);
						}

					} catch (Exception e) {
						e.printStackTrace();
						Utils.showToast(activity, activity.getString(R.string.unable_to_fetch_address));
						setFetchedAddressToTextView("", false);
					}
					getFocussedProgressBar().setVisibility(View.GONE);
				}

				@Override
				public void failure(RetrofitError error) {
					product.clicklabs.jugnoo.utils.Log.e("DeliveryAddressFragment", "error=" + error.toString());
					Utils.showToast(activity, activity.getString(R.string.unable_to_fetch_address));
					getFocussedProgressBar().setVisibility(View.GONE);
					setFetchedAddressToTextView("", false);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void setFetchedAddressToTextView(String address, boolean isAddressConfirmed){
		EditText editText = getFocusedEditText();
		if(searchListAdapter!=null){
			editText.removeTextChangedListener(searchListAdapter.getTextWatcherEditText(editText.getId()));
			editText.setText(address);
			editText.setSelection(address.length());
			getFocusedCross().setVisibility(View.VISIBLE);
			editText.addTextChangedListener(searchListAdapter.getTextWatcherEditText(editText.getId()));
		}else{
			editText.setText(address);
			editText.setSelection(address.length());
			getFocusedCross().setVisibility(View.VISIBLE);
		}
		if(!isAddressConfirmed){
			if (getFocusedEditText().getId() == editTextSearch.getId()) {
				searchResultPickup = null;
			} else {
				searchResultDestination = null;

			}
		}
	}

	private EditText getFocusedEditText() {
		return searchMode== PlaceSearchMode.PICKUP_AND_DROP.getOrdinal()
				&& editTextSearchDest.hasFocus()?editTextSearchDest:editTextSearch;
	}

	private View getFocusedCross() {
		return searchMode== PlaceSearchMode.PICKUP_AND_DROP.getOrdinal()
				&& editTextSearchDest.hasFocus()?imageViewSearchCrossDest:imageViewSearchCross;
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
			setFetchedAddressToTextView(searchResult.getAddress(), true);

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
			if(bottomSheetBehaviour!=null && bottomSheetBehaviour.getState()!=BottomSheetBehavior.STATE_COLLAPSED){
				bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
			}
			rlMarkerPin.setVisibility(View.VISIBLE);
			if(googleMap != null) {
				fillAddressDetails(googleMap.getCameraPosition().target, false);
			}
			Utils.hideSoftKeyboard(activity,editTextSearch);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openBottomSheetMode(){
		bNext.setVisibility(View.GONE);
		if(bottomSheetBehaviour!=null && bottomSheetBehaviour.getState()!=BottomSheetBehavior.STATE_EXPANDED){
			bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
		}
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
				public void onSuccess(SearchResult searchResult, String strResult, boolean addressDeleted) {
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

}
