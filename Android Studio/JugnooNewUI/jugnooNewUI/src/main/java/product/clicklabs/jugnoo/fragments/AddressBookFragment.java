package product.clicklabs.jugnoo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import product.clicklabs.jugnoo.AddPlaceActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.SavedPlacesAdapter;
import product.clicklabs.jugnoo.apis.ApiFetchUserAddress;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;


public class AddressBookFragment extends Fragment {

	private final String TAG = AddressBookFragment.class.getSimpleName();

	private RelativeLayout relativeLayoutRoot;

	private TextView textViewSavedAddresses;
	private CardView cardViewAddresses;
	private RelativeLayout relativeLayoutHome, relativeLayoutWork;
	private TextView textViewHomeValue, textViewAddressUsedHome, textViewWorkValue, textViewAddressUsedWork;
	private View viewHomeSep, viewWorkSep;
	private NonScrollListView listViewSavedLocations;
	private SavedPlacesAdapter savedPlacesAdapter;

	private TextView textViewRecentAddresses;
	private CardView cardViewRecentAddresses;
	private NonScrollListView listViewRecentAddresses;
	private SavedPlacesAdapter savedPlacesAdapterRecent;

	private RelativeLayout relativeLayoutAddHome, relativeLayoutAddWork, relativeLayoutAddNewAddress;

	private View rootView;
    private FragmentActivity activity;

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
		relativeLayoutHome = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutHome);
		((TextView) rootView.findViewById(R.id.textViewHome)).setTypeface(Fonts.mavenMedium(activity));
		textViewHomeValue = (TextView) rootView.findViewById(R.id.textViewHomeValue); textViewHomeValue.setTypeface(Fonts.mavenMedium(activity));
		textViewAddressUsedHome = (TextView) rootView.findViewById(R.id.textViewAddressUsedHome); textViewAddressUsedHome.setTypeface(Fonts.mavenRegular(activity));
		relativeLayoutWork = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutWork);
		((TextView) rootView.findViewById(R.id.textViewWork)).setTypeface(Fonts.mavenMedium(activity));
		textViewWorkValue = (TextView) rootView.findViewById(R.id.textViewWorkValue); textViewWorkValue.setTypeface(Fonts.mavenMedium(activity));
		textViewAddressUsedWork = (TextView) rootView.findViewById(R.id.textViewAddressUsedWork); textViewAddressUsedWork.setTypeface(Fonts.mavenRegular(activity));
		viewHomeSep = rootView.findViewById(R.id.viewHomeSep);
		viewWorkSep = rootView.findViewById(R.id.viewWorkSep);


		listViewSavedLocations = (NonScrollListView) rootView.findViewById(R.id.listViewSavedLocations);
		try {
			savedPlacesAdapter = new SavedPlacesAdapter(activity, Data.userData.getSearchResults(), new SavedPlacesAdapter.Callback() {
				@Override
				public void onItemClick(SearchResult searchResult) {
					onSavedLocationEdit(searchResult);
				}

				@Override
				public void onEditClick(SearchResult searchResult) {
					onSavedLocationEdit(searchResult);
				}
			}, false, false, false);
			listViewSavedLocations.setAdapter(savedPlacesAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}


		textViewRecentAddresses = (TextView) rootView.findViewById(R.id.textViewRecentAddresses); textViewRecentAddresses.setTypeface(Fonts.mavenMedium(activity));
		cardViewRecentAddresses = (CardView) rootView.findViewById(R.id.cardViewRecentAddresses);
		listViewRecentAddresses = (NonScrollListView) rootView.findViewById(R.id.listViewRecentAddresses);
		textViewRecentAddresses.setVisibility(View.GONE);
		cardViewRecentAddresses.setVisibility(View.GONE);
		try {
			savedPlacesAdapterRecent = new SavedPlacesAdapter(activity, Data.userData.getSearchResultsRecent(), new SavedPlacesAdapter.Callback() {
				@Override
				public void onItemClick(SearchResult searchResult) {
					onSavedLocationEdit(searchResult);
				}

				@Override
				public void onEditClick(SearchResult searchResult) {
					onSavedLocationEdit(searchResult);
				}
			}, false, false, false);
			listViewRecentAddresses.setAdapter(savedPlacesAdapterRecent);
		} catch (Exception e) {
			e.printStackTrace();
		}


		relativeLayoutAddHome = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutAddHome);
		relativeLayoutAddWork = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutAddWork);
		relativeLayoutAddNewAddress = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutAddNewAddress);
		((TextView) rootView.findViewById(R.id.textViewAddHome)).setTypeface(Fonts.mavenMedium(activity));
		((TextView) rootView.findViewById(R.id.textViewAddWork)).setTypeface(Fonts.mavenMedium(activity));
		((TextView) rootView.findViewById(R.id.textViewAddNewAddress)).setTypeface(Fonts.mavenMedium(activity));

		View.OnClickListener homeClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(activity, AddPlaceActivity.class);
				intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_HOME);
				intent.putExtra(Constants.KEY_ADDRESS, Prefs.with(activity).getString(SPLabels.ADD_HOME, ""));
				startActivityForResult(intent, Constants.REQUEST_CODE_ADD_HOME);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		};
		relativeLayoutHome.setOnClickListener(homeClickListener);
		relativeLayoutAddHome.setOnClickListener(homeClickListener);

		View.OnClickListener workClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(activity, AddPlaceActivity.class);
				intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_WORK);
				intent.putExtra(Constants.KEY_ADDRESS, Prefs.with(activity).getString(SPLabels.ADD_WORK, ""));
				startActivityForResult(intent, Constants.REQUEST_CODE_ADD_WORK);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		};
		relativeLayoutWork.setOnClickListener(workClickListener);
		relativeLayoutAddWork.setOnClickListener(workClickListener);

		relativeLayoutAddNewAddress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(activity, AddPlaceActivity.class);
				intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_NEW_LOCATION);
				intent.putExtra(Constants.KEY_ADDRESS, "");
				startActivityForResult(intent, Constants.REQUEST_CODE_ADD_NEW_LOCATION);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});


		setSavedPlaces();

		getApiFetchUserAddress().hit(true);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		setSavedPlaces();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relativeLayoutRoot);
		System.gc();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == FragmentActivity.RESULT_OK) {
//			setSavedPlaces();
		} else if (resultCode == FragmentActivity.RESULT_CANCELED) {
//			setSavedPlaces();
		}
	}


	private void setSavedPlaces() {
		try {
			String homeString = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
			if (!homeString.equalsIgnoreCase("")) {
				SearchResult searchResult = new Gson().fromJson(homeString, SearchResult.class);
				relativeLayoutHome.setVisibility(View.VISIBLE);
				relativeLayoutAddHome.setVisibility(View.GONE);
				textViewHomeValue.setText(searchResult.getAddress());
				textViewAddressUsedHome.setVisibility(View.GONE);
				if (searchResult.getFreq() > 0) {
					textViewAddressUsedHome.setVisibility(View.VISIBLE);
					if(searchResult.getFreq() <= 1){
						textViewAddressUsedHome.setText(activity.getString(R.string.address_used_one_time_format,
								String.valueOf(searchResult.getFreq())));
					} else {
						textViewAddressUsedHome.setText(activity.getString(R.string.address_used_multiple_time_format,
								String.valueOf(searchResult.getFreq())));
					}
				}
			} else {
				relativeLayoutHome.setVisibility(View.GONE);
				relativeLayoutAddHome.setVisibility(View.VISIBLE);
			}

			String workString = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");
			if (!workString.equalsIgnoreCase("")) {
				SearchResult searchResult = new Gson().fromJson(workString, SearchResult.class);
				relativeLayoutWork.setVisibility(View.VISIBLE);
				relativeLayoutAddWork.setVisibility(View.GONE);
				textViewWorkValue.setText(searchResult.getAddress());
				textViewAddressUsedWork.setVisibility(View.GONE);
				if (searchResult.getFreq() > 0) {
					textViewAddressUsedWork.setVisibility(View.VISIBLE);
					if(searchResult.getFreq() <= 1){
						textViewAddressUsedWork.setText(activity.getString(R.string.address_used_one_time_format,
								String.valueOf(searchResult.getFreq())));
					} else {
						textViewAddressUsedWork.setText(activity.getString(R.string.address_used_multiple_time_format,
								String.valueOf(searchResult.getFreq())));
					}
				}
			} else {
				relativeLayoutWork.setVisibility(View.GONE);
				relativeLayoutAddWork.setVisibility(View.VISIBLE);
			}

			savedPlacesAdapter.notifyDataSetChanged();

			if (relativeLayoutHome.getVisibility() == View.GONE
					&& relativeLayoutWork.getVisibility() == View.GONE
					&& savedPlacesAdapter.getCount() == 0) {
				textViewSavedAddresses.setVisibility(View.GONE);
				cardViewAddresses.setVisibility(View.GONE);
			} else {
				textViewSavedAddresses.setVisibility(View.VISIBLE);
				cardViewAddresses.setVisibility(View.VISIBLE);

				viewHomeSep.setVisibility((relativeLayoutWork.getVisibility() == View.GONE && savedPlacesAdapter.getCount() == 0) ? View.GONE : View.VISIBLE);
				viewWorkSep.setVisibility(savedPlacesAdapter.getCount() == 0 ? View.GONE : View.VISIBLE);
			}


			savedPlacesAdapterRecent.notifyDataSetChanged();
			if (savedPlacesAdapterRecent.getCount() > 0) {
				textViewRecentAddresses.setVisibility(View.VISIBLE);
				cardViewRecentAddresses.setVisibility(View.VISIBLE);
			} else {
				textViewRecentAddresses.setVisibility(View.GONE);
				cardViewRecentAddresses.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	private void onSavedLocationEdit(SearchResult searchResult){
		Intent intent = new Intent(activity, AddPlaceActivity.class);
		intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_NEW_LOCATION);
		intent.putExtra(Constants.KEY_ADDRESS, new Gson().toJson(searchResult, SearchResult.class));
		startActivityForResult(intent, Constants.REQUEST_CODE_ADD_NEW_LOCATION);
		activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}


	private ApiFetchUserAddress apiFetchUserAddress;
	private ApiFetchUserAddress getApiFetchUserAddress(){
		if(apiFetchUserAddress == null){
			apiFetchUserAddress = new ApiFetchUserAddress(activity, new ApiFetchUserAddress.Callback() {
				@Override
				public void onSuccess() {
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
		return apiFetchUserAddress;
	}

}
