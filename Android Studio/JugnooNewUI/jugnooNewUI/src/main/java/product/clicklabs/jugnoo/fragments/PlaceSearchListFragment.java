package product.clicklabs.jugnoo.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.text.TextWatcher;
import android.util.Log;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sabkuchfresh.datastructure.GoogleGeocodeResponse;
import com.sabkuchfresh.fragments.DeliveryAddressesFragment;
import com.sabkuchfresh.home.FreshActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import product.clicklabs.jugnoo.AddPlaceActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.FareEstimateActivity;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.SavedPlacesAdapter;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.retrofit.RestClient;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.MapStateListener;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.TouchableMapFragment;
import product.clicklabs.jugnoo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class PlaceSearchListFragment extends Fragment implements  Constants {
	
	private LinearLayout linearLayoutRoot;

	private EditText editTextSearch;
	private ProgressWheel progressBarSearch;
	private ImageView imageViewSearchCross, imageViewSearchGPSIcon;

	private LinearLayout linearLayoutAddFav;
	private RelativeLayout relativeLayoutAddHome, relativeLayoutAddWork;
	private TextView textViewAddHome, textViewAddWork;
	private ImageView imageViewSep, imageViewSep2;

	private ScrollView scrollViewSearch;
	private NonScrollListView listViewSearch;
	private CardView cardViewSearch;

	private NestedScrollView scrollViewSuggestions;
	private TextView textViewSavedPlaces, textViewRecentAddresses;
	private NonScrollListView listViewSavedLocations, listViewRecentAddresses;
	private SavedPlacesAdapter savedPlacesAdapter, savedPlacesAdapterRecent;
	private CardView cardViewSavedPlaces, cvRecentAddresses;

	private View rootView;
    private Activity activity;
	private GoogleApiClient mGoogleApiClient;
	private SearchListAdapter.SearchListActionsHandler searchListActionsHandler;
	private SearchListAdapter searchListAdapter;
	private BottomSheetBehavior<NestedScrollView> bottomSheetBehaviour;
	private int newState;
	private RelativeLayout rootLayout;
	private GoogleMap googleMap;
	private RelativeLayout rlMarkerPin;
	private Button bNext;
	public static PlaceSearchListFragment newInstance(Bundle bundle){
		PlaceSearchListFragment fragment = new PlaceSearchListFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try{
			this.searchListActionsHandler = (SearchListAdapter.SearchListActionsHandler) context;
			if(context instanceof FareEstimateActivity){
				this.mGoogleApiClient = ((FareEstimateActivity)context).getmGoogleApiClient();
			} else {
				this.mGoogleApiClient = ((HomeActivity)context).getmGoogleApiClient();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_place_search_list, container, false);


        activity = getActivity();
		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		rootLayout = (RelativeLayout) rootView.findViewById(R.id.rootLayout);

		new ASSL(activity, rootLayout, 1134, 720, false);


		rlMarkerPin = (RelativeLayout) rootView.findViewById(R.id.rlMarkerPin);
		bNext = (Button) rootView.findViewById(R.id.bNext);
		editTextSearch = (EditText) rootView.findViewById(R.id.editTextSearch);
		editTextSearch.setTypeface(Fonts.mavenMedium(activity));
		progressBarSearch = (ProgressWheel) rootView.findViewById(R.id.progressBarSearch); progressBarSearch.setVisibility(View.GONE);
		imageViewSearchCross = (ImageView) rootView.findViewById(R.id.ivDeliveryAddressCross); imageViewSearchCross.setVisibility(View.GONE);
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
		cvRecentAddresses = (CardView) rootView.findViewById(R.id.cardViewRecentAddresses);

		imageViewSearchGPSIcon = (ImageView) rootView.findViewById(R.id.imageViewSearchGPSIcon);

		editTextSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editTextSearch.requestFocus();
				Utils.showSoftKeyboard(activity, editTextSearch);
			}
		});

		imageViewSearchCross.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextSearch.setText("");
			}
		});

		Bundle bundle = getArguments();
		String text = bundle.getString(KEY_SEARCH_FIELD_TEXT, "");
		String hint = bundle.getString(KEY_SEARCH_FIELD_HINT, "");
		int searchMode = bundle.getInt(KEY_SEARCH_MODE, PlaceSearchMode.PICKUP.getOrdinal());

		searchListAdapter = new SearchListAdapter(activity, editTextSearch, new LatLng(30.75, 76.78), mGoogleApiClient, searchMode,
				new SearchListAdapter.SearchListActionsHandler() {

					@Override
					public void onTextChange(String text) {
						try {
							if(text.length() > 0){
								scrollViewSearch.setVisibility(View.VISIBLE);
								imageViewSearchCross.setVisibility(View.VISIBLE);
								hideSearchLayout();
							}
							else{
								scrollViewSearch.setVisibility(View.GONE);
								imageViewSearchCross.setVisibility(View.GONE);
								showSearchLayout();
							}
							if(searchListActionsHandler != null){searchListActionsHandler.onTextChange(text);}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onSearchPre() {
						progressBarSearch.setVisibility(View.VISIBLE);
						if(searchListActionsHandler != null){searchListActionsHandler.onSearchPre();}
					}

					@Override
					public void onSearchPost() {
						progressBarSearch.setVisibility(View.GONE);
						if(searchListActionsHandler != null){searchListActionsHandler.onSearchPost();}
					}

					@Override
					public void onPlaceClick(SearchResult autoCompleteSearchResult) {
						if(searchListActionsHandler != null){searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);}
					}

					@Override
					public void onPlaceSearchPre() {
						progressBarSearch.setVisibility(View.VISIBLE);
						if(searchListActionsHandler != null){searchListActionsHandler.onPlaceSearchPre();}
					}

					@Override
					public void onPlaceSearchPost(SearchResult searchResult) {
						scrollViewSearch.setVisibility(View.GONE);
						progressBarSearch.setVisibility(View.GONE);
						if(searchListActionsHandler != null){searchListActionsHandler.onPlaceSearchPost(searchResult);}
					}

					@Override
					public void onPlaceSearchError() {
						progressBarSearch.setVisibility(View.GONE);
						if(searchListActionsHandler != null){searchListActionsHandler.onPlaceSearchError();}
					}

					@Override
					public void onPlaceSaved() {
					}

					@Override
					public void onNotifyDataSetChanged(int count) {
						if(count > 0){
							cardViewSearch.setVisibility(View.VISIBLE);
						} else {
							cardViewSearch.setVisibility(View.GONE);
						}
						if(searchListActionsHandler != null){searchListActionsHandler.onNotifyDataSetChanged(count);}
					}
				}, true);

		ViewGroup header = (ViewGroup)activity.getLayoutInflater().inflate(R.layout.header_place_search_list, listViewSearch, false);
		header.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
		ASSL.DoMagic(header);
		listViewSavedLocations.addFooterView(header, null, false);

		linearLayoutAddFav = (LinearLayout) header.findViewById(R.id.linearLayoutAddFav);
		relativeLayoutAddHome = (RelativeLayout)header.findViewById(R.id.relativeLayoutAddHome);
		relativeLayoutAddWork = (RelativeLayout)header.findViewById(R.id.relativeLayoutAddWork);
		textViewAddHome = (TextView)header.findViewById(R.id.textViewAddHome); textViewAddHome.setTypeface(Fonts.mavenMedium(activity));
		textViewAddWork = (TextView)header.findViewById(R.id.textViewAddWork); textViewAddWork.setTypeface(Fonts.mavenMedium(activity));
		imageViewSep = (ImageView) header.findViewById(R.id.imageViewSep);
		imageViewSep2 = (ImageView) header.findViewById(R.id.imageViewSep2);

		listViewSearch.setAdapter(searchListAdapter);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(editTextSearch.getText().length() == 0 || editTextSearch.getText().toString().equalsIgnoreCase(" ")) {
					editTextSearch.setText("");
					editTextSearch.setText(" ");
					editTextSearch.setText("");
				}
			}
		},500);


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




		if(searchMode == PlaceSearchMode.DROP.getOrdinal()){
			imageViewSearchGPSIcon.setImageResource(R.drawable.circle_red);
		} else{
			imageViewSearchGPSIcon.setImageResource(R.drawable.circle_green);
		}
		editTextSearch.setText(text);
		editTextSearch.setHint(hint);
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				editTextSearch.requestFocus();
				editTextSearch.setSelection(editTextSearch.getText().length());
				Utils.showSoftKeyboard(activity, editTextSearch);
			}
		});

		ImageView imageViewShadow = (ImageView) rootView.findViewById(R.id.imageViewShadow);
		if(activity instanceof HomeActivity){
			imageViewShadow.setVisibility(View.VISIBLE);
		} else {
			imageViewShadow.setVisibility(View.GONE);
		}

			bottomSheetBehaviour = BottomSheetBehavior.from(scrollViewSuggestions);

		Log.i("TAG", "peekHeight: "+bottomSheetBehaviour.getPeekHeight());
		bottomSheetBehaviour.setPeekHeight(0);
		bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
	/*	bottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
				Log.e("BottomSheetAutos", "onStateChanged: "+ newState);
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {
				Log.i("BottomSheetAutos", "onSlide: "+slideOffset);
				;
			}
		});*/
		setMap();
        return rootView;
	}


	private void showSearchLayout(){
		String home = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
		String work = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");

		if(home.equalsIgnoreCase("") || work.equalsIgnoreCase("")){
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

		if(savedPlacesAdapter != null && savedPlacesAdapter.getCount() == 0){
			imageViewSep.setVisibility(View.GONE);
		}

	}

	private void hideSearchLayout(){
		linearLayoutAddFav.setVisibility(View.GONE);
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
		searchListAdapter.addSavedLocationsToList();
		updateSavedPlacesLists();
		showSearchLayout();
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
						textViewAddHome.setText("Add Home");
					}

				} else if (requestCode == Constants.REQUEST_CODE_ADD_WORK) {
					String strResult = data.getStringExtra("PLACE");
					SearchResult searchResult = new Gson().fromJson(strResult, SearchResult.class);
					if(searchResult != null) {
						updateSavedPlacesLists();
						showSearchLayout();
					} else{
						textViewAddWork.setText("Add Work");
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
		return progressBarSearch;
	}

	public enum PlaceSearchMode {
		PICKUP(1),
		DROP(2)
		;

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
		if(searchListActionsHandler != null) {
			searchListActionsHandler.onPlaceClick(searchResult);
			searchListActionsHandler.onPlaceSearchPre();
			searchListActionsHandler.onPlaceSearchPost(searchResult);
		}
		Utils.hideSoftKeyboard(activity, editTextSearch);
	}

	private HomeUtil homeUtil = new HomeUtil();

	private void updateSavedPlacesLists(){
		try {
			ArrayList<SearchResult> searchResults = homeUtil.getSavedPlacesWithHomeWork(activity);
			int savedPlaces = searchResults.size();

			savedPlacesAdapter = new SavedPlacesAdapter(activity, searchResults, new SavedPlacesAdapter.Callback() {
				@Override
				public void onItemClick(SearchResult searchResult) {
					clickOnSavedItem(searchResult);
				}

				@Override
				public void onDeleteClick(SearchResult searchResult) {
				}
			}, false, false, false);
			listViewSavedLocations.setAdapter(savedPlacesAdapter);
			if(searchResults.size() > 0){
				cardViewSavedPlaces.setVisibility(View.VISIBLE);
				textViewSavedPlaces.setText(savedPlacesAdapter.getCount() == 1 ? R.string.saved_location : R.string.saved_locations);
			} else {
				cardViewSavedPlaces.setVisibility(View.GONE);
			}

			savedPlacesAdapterRecent = new SavedPlacesAdapter(activity, Data.userData.getSearchResultsRecent(), new SavedPlacesAdapter.Callback() {
				@Override
				public void onItemClick(SearchResult searchResult) {
					clickOnSavedItem(searchResult);
				}

				@Override
				public void onDeleteClick(SearchResult searchResult) {
				}
			}, false, false, false);
			listViewRecentAddresses.setAdapter(savedPlacesAdapterRecent);
			if(Data.userData.getSearchResultsRecent().size() > 0){
				cvRecentAddresses.setVisibility(View.VISIBLE);
			} else{
				cvRecentAddresses.setVisibility(View.GONE);
			}

			if(savedPlaces > 0) {
				textViewSavedPlaces.setVisibility(View.VISIBLE);
			} else {
				textViewSavedPlaces.setVisibility(View.GONE);
			}

			if (savedPlacesAdapterRecent.getCount() > 0) {
				textViewRecentAddresses.setVisibility(View.VISIBLE);
				textViewRecentAddresses.setText(savedPlacesAdapterRecent.getCount() == 1 ? R.string.recent_location : R.string.recent_locations);
			} else {
				textViewRecentAddresses.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setMap() {
		((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMap) {
				PlaceSearchListFragment.this.googleMap = googleMap;
				if (googleMap != null) {
					googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					googleMap.setMyLocationEnabled(true);
					googleMap.getUiSettings().setMyLocationButtonEnabled(false);
					setupMapAndButtonMargins();
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
							/*mapSettledCanForward = false;
							searchResultNearPin = null;*/
						}

						@Override
						public void onMapSettled() {
							fillAddressDetails(PlaceSearchListFragment.this.googleMap.getCameraPosition().target);
//							autoCompleteResultClicked = false;
						}

						@Override
						public void onCameraPositionChanged(CameraPosition cameraPosition) {
						}
					};

				}
			}
		});
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
		return new LatLng(Data.latitude, Data.longitude);
	}

	private void setupMapAndButtonMargins() {
		if(getView() != null) {
			RelativeLayout.LayoutParams paramsRL = (RelativeLayout.LayoutParams) rlMarkerPin.getLayoutParams();
			RelativeLayout.LayoutParams paramsB = (RelativeLayout.LayoutParams) bNext.getLayoutParams();
			int height = activity.getResources().getDimensionPixelSize(R.dimen.dp_162);
//        if (savedPlacesAdapter.getCount() + savedPlacesAdapterRecent.getCount() <= 5) {
//            ViewGroup.LayoutParams layoutParams = scrollViewSuggestions.getLayoutParams();
//            layoutParams.height = llLocationsContainer.getMeasuredHeight() + activity.getResources().getDimensionPixelSize(R.dimen.dp_8);
//            scrollViewSuggestions.setLayoutParams(layoutParams);
//            height = layoutParams.height;
//        }
			if (scrollViewSuggestions.getVisibility() == View.VISIBLE) {
				if (savedPlacesAdapter.getCount() >= 3) {
					height = activity.getResources().getDimensionPixelSize(R.dimen.dp_280);
					paramsRL.setMargins(0, 0, 0, height);
					paramsB.setMargins(0, 0, 0, activity.getResources().getDimensionPixelSize(R.dimen.dp_290));
				} else {
					paramsRL.setMargins(0, 0, 0, height);
					paramsB.setMargins(0, 0, 0, activity.getResources().getDimensionPixelSize(R.dimen.dp_176));
				}
			} else {
				paramsRL.setMargins(0, 0, 0, 0);
				paramsB.setMargins(0, 0, 0, activity.getResources().getDimensionPixelSize(R.dimen.dp_20));
			}
			rlMarkerPin.setLayoutParams(paramsRL);
//			bNext.setLayoutParams(paramsB);
			if (googleMap != null) {
				googleMap.setPadding(0, 0, 0, scrollViewSuggestions.getVisibility() == View.VISIBLE ?
						height : 0);
			}
			/*if (bottomSheetBehavior != null) {
				bottomSheetBehavior.setPeekHeight(height);
			}*/
		}
	}


	@Override
	public void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
		mGoogleApiClient.disconnect();
	}
	private void fillAddressDetails(final LatLng latLng) {
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
			final Map<String, String> params = new HashMap<String, String>(6);

			params.put(Data.LATLNG, latLng.latitude + "," + latLng.longitude);
			params.put("language", Locale.getDefault().getCountry());
			params.put("sensor", "false");

			RestClient.getGoogleApiService().getMyAddress(params, new Callback<GoogleGeocodeResponse>() {
				@Override
				public void success(GoogleGeocodeResponse geocodeResponse, Response response) {
					try {
						if(geocodeResponse.results != null && geocodeResponse.results.size() > 0){
							double current_latitude = latLng.latitude;
							double current_longitude = latLng.longitude;

							String current_street = geocodeResponse.results.get(0).getStreetNumber();
							String current_route = geocodeResponse.results.get(0).getRoute();
							String current_area = geocodeResponse.results.get(0).getLocality();
							String current_city = geocodeResponse.results.get(0).getCity();
							String current_pincode = geocodeResponse.results.get(0).getCountry();

							setFetchedAddressToTextView(current_street + (current_street.length()>0?", ":"")
									+ current_route + (current_route.length()>0?", ":"")
									+ geocodeResponse.results.get(0).getAddAddress()
									+ ", " + current_city);
						} else {
							Utils.showToast(activity, activity.getString(R.string.unable_to_fetch_address));
							setFetchedAddressToTextView("");
						}

					} catch (Exception e) {
						e.printStackTrace();
						Utils.showToast(activity, activity.getString(R.string.unable_to_fetch_address));
						setFetchedAddressToTextView("");
					}
//					progressWheelDeliveryAddressPin.setVisibility(View.GONE);
				}

				@Override
				public void failure(RetrofitError error) {
					product.clicklabs.jugnoo.utils.Log.e("DeliveryAddressFragment", "error=" + error.toString());
					Utils.showToast(activity, activity.getString(R.string.unable_to_fetch_address));
//					progressWheelDeliveryAddressPin.setVisibility(View.GONE);
					setFetchedAddressToTextView("");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void setFetchedAddressToTextView(String address){
		if(searchListAdapter!=null){
			editTextSearch.removeTextChangedListener(null);
			editTextSearch.setText(address);
			editTextSearch.addTextChangedListener(searchListAdapter.getTextWatcherEditText());
		}else{
			editTextSearch.setText(address);
		}


	}

}
