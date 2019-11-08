package product.clicklabs.jugnoo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.AddPlaceActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.SavedPlacesAdapter;
import product.clicklabs.jugnoo.apis.ApiAddHomeWorkAddress;
import product.clicklabs.jugnoo.apis.ApiFetchUserAddress;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.home.HomeUtil;
import product.clicklabs.jugnoo.room.DBObject;
import product.clicklabs.jugnoo.room.apis.DBCoroutine;
import product.clicklabs.jugnoo.room.database.SearchLocationDB;
import product.clicklabs.jugnoo.room.model.SearchLocation;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NonScrollListView;


public class AddressBookFragment extends Fragment {

	private final String TAG = AddressBookFragment.class.getSimpleName();

	private RelativeLayout relativeLayoutRoot;

	private TextView textViewSavedAddresses;
	private CardView cardViewAddresses;
	private NonScrollListView listViewSavedLocations;
	private SavedPlacesAdapter savedPlacesAdapter;

	private TextView textViewRecentAddresses;
	private CardView cardViewRecentAddresses;
	private NonScrollListView listViewRecentAddresses;
	private SavedPlacesAdapter savedPlacesAdapterRecent;

	private Button bAddNewAddress;
	private ScrollView scrollView;

	private View rootView;
    private FragmentActivity activity;

    private ArrayList<SearchLocation> searchLocations;

	public AddressBookFragment(){
	}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_address_book, container, false);


        activity = getActivity();

		relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
		try {
			if(relativeLayoutRoot != null) {
				new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		textViewSavedAddresses = (TextView) rootView.findViewById(R.id.textViewSavedAddresses); textViewSavedAddresses.setTypeface(Fonts.mavenMedium(activity));
		cardViewAddresses = (CardView) rootView.findViewById(R.id.cardViewAddresses);
		bAddNewAddress = (Button) rootView.findViewById(R.id.bAddNewAddress);
		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);

		listViewSavedLocations = (NonScrollListView) rootView.findViewById(R.id.listViewSavedLocations);
		try {
			savedPlacesAdapter = new SavedPlacesAdapter(activity, homeUtil.getSavedPlacesWithHomeWork(activity), new SavedPlacesAdapter.Callback() {
				@Override
				public void onItemClick(SearchResult searchResult) {
					onSavedLocationEdit(searchResult);
				}

				@Override
				public void onDeleteClick(SearchResult searchResult) {
					deleteAddressDialog(searchResult);
				}
			}, false, false, true, false);
			listViewSavedLocations.setAdapter(savedPlacesAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		searchLocations = new ArrayList<>();

		textViewRecentAddresses = (TextView) rootView.findViewById(R.id.textViewRecentAddresses); textViewRecentAddresses.setTypeface(Fonts.mavenMedium(activity));
		cardViewRecentAddresses = (CardView) rootView.findViewById(R.id.cardViewRecentAddresses);
		listViewRecentAddresses = (NonScrollListView) rootView.findViewById(R.id.listViewRecentAddresses);
		textViewRecentAddresses.setVisibility(View.GONE);
		cardViewRecentAddresses.setVisibility(View.GONE);



		bAddNewAddress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				for specific adddress tag intent
//				intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_WORK);
//				intent.putExtra(Constants.KEY_ADDRESS, Prefs.with(activity).getString(SPLabels.ADD_WORK, ""));
//				startActivityForResult(intent, Constants.REQUEST_CODE_ADD_WORK);

				Intent intent=new Intent(activity, AddPlaceActivity.class);
				intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_NEW_LOCATION);
				intent.putExtra(Constants.KEY_ADDRESS, "");
				startActivityForResult(intent, Constants.REQUEST_CODE_ADD_NEW_LOCATION);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});


		getApiFetchUserAddress().hit(true);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		setSavedPlaces();
		scrollView.scrollTo(0, 0);
	}


	private void fetchRecentLocations(){
		SearchLocationDB searchLocationDB = DBObject.INSTANCE.getInstance();
		if(searchLocationDB != null) {
			DBCoroutine.Companion.getAllLocations(searchLocationDB, searchLocation -> {
				if (!searchLocations.isEmpty()) {
					searchLocations.clear();
				}
				searchLocations.addAll(searchLocation);
				setRecentList();
			});
		} else {
			setRecentList();
		}
	}

	private void setRecentList(){
		try {
			ArrayList<SearchResult> searchResults = PlaceSearchListFragment.getSearchResultsRecentAndSaved(activity, searchLocations);
			if(savedPlacesAdapterRecent == null) {
				savedPlacesAdapterRecent = new SavedPlacesAdapter(activity, searchResults, new SavedPlacesAdapter.Callback() {
					@Override
					public void onItemClick(SearchResult searchResult) {
						onSavedLocationEdit(searchResult);
					}

					@Override
					public void onDeleteClick(SearchResult searchResult) {

					}
				}, false, false, false, false);
				listViewRecentAddresses.setAdapter(savedPlacesAdapterRecent);
			} else {
				savedPlacesAdapterRecent.setList(searchResults);
			}
			if (savedPlacesAdapterRecent.getCount() > 0) {
				textViewRecentAddresses.setVisibility(View.VISIBLE);
				cardViewRecentAddresses.setVisibility(View.VISIBLE);
				textViewRecentAddresses.setText(savedPlacesAdapterRecent.getCount() == 1 ? R.string.recent_location : R.string.recent_locations);
			} else {
				textViewRecentAddresses.setVisibility(View.GONE);
				cardViewRecentAddresses.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void setSavedPlaces() {
		try {
			savedPlacesAdapter.setList(homeUtil.getSavedPlacesWithHomeWork(activity));

			if (savedPlacesAdapter.getCount() == 0) {
				textViewSavedAddresses.setVisibility(View.GONE);
				cardViewAddresses.setVisibility(View.GONE);
			} else {
				textViewSavedAddresses.setVisibility(View.VISIBLE);
				cardViewAddresses.setVisibility(View.VISIBLE);
				textViewSavedAddresses.setText(savedPlacesAdapter.getCount() == 1 ? R.string.saved_location : R.string.saved_locations);
			}


			fetchRecentLocations();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	private void onSavedLocationEdit(SearchResult searchResult){
		Intent intent = new Intent(activity, AddPlaceActivity.class);
		intent.putExtra(Constants.KEY_REQUEST_CODE, searchResult.getPlaceRequestCode());
		intent.putExtra(Constants.KEY_ADDRESS, new Gson().toJson(searchResult, SearchResult.class));
		startActivityForResult(intent, searchResult.getPlaceRequestCode());
		activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}


	private ApiFetchUserAddress apiFetchUserAddress;
	private ApiFetchUserAddress getApiFetchUserAddress(){
		if(apiFetchUserAddress == null){
			apiFetchUserAddress = new ApiFetchUserAddress(activity, new ApiFetchUserAddress.Callback() {
				@Override
				public void onSuccess() {

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

				@Override
				public void onFinish() {
					setSavedPlaces();
				}
			});
		}
		return apiFetchUserAddress;
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
					setSavedPlaces();
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

	private HomeUtil homeUtil = new HomeUtil();

}
