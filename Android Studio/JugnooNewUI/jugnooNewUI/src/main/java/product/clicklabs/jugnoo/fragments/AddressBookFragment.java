package product.clicklabs.jugnoo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import product.clicklabs.jugnoo.AddPlaceActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.SavedPlacesAdapter;
import product.clicklabs.jugnoo.apis.ApiFetchUserAddress;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FirebaseEvents;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;


public class AddressBookFragment extends Fragment {

	private final String TAG = AddressBookFragment.class.getSimpleName();

	private RelativeLayout relativeLayoutRoot;
	private ImageView imageViewEditHome, imageViewEditWork;
	private RelativeLayout relativeLayoutAddHome, relativeLayoutAddWork;
	private TextView textViewAddHome, textViewAddHomeValue, textViewAddWork, textViewAddWorkValue;

	private NonScrollListView listViewSavedLocations;
	private RelativeLayout relativeLayoutAddNewAddress;
	private SavedPlacesAdapter savedPlacesAdapter;

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


		relativeLayoutAddHome = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutAddHome);
		imageViewEditHome = (ImageView)rootView.findViewById(R.id.imageViewEditHome);
		textViewAddHome = (TextView) rootView.findViewById(R.id.textViewAddHome); textViewAddHome.setTypeface(Fonts.mavenMedium(activity));
		textViewAddHomeValue = (TextView) rootView.findViewById(R.id.textViewAddHomeValue); textViewAddHomeValue.setTypeface(Fonts.mavenMedium(activity));
		relativeLayoutAddWork = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutAddWork);
		imageViewEditWork = (ImageView)rootView.findViewById(R.id.imageViewEditWork);
		textViewAddWork = (TextView) rootView.findViewById(R.id.textViewAddWork); textViewAddWork.setTypeface(Fonts.mavenMedium(activity));
		textViewAddWorkValue = (TextView) rootView.findViewById(R.id.textViewAddWorkValue); textViewAddWorkValue.setTypeface(Fonts.mavenMedium(activity));

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
			}, true, false);
			listViewSavedLocations.setAdapter(savedPlacesAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		relativeLayoutAddNewAddress = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutAddNewAddress);
		((TextView) rootView.findViewById(R.id.textViewAddNewAddress)).setTypeface(Fonts.mavenMedium(activity));

		relativeLayoutAddHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(activity, AddPlaceActivity.class);
				intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_HOME);
				intent.putExtra(Constants.KEY_ADDRESS, Prefs.with(activity).getString(SPLabels.ADD_HOME, ""));
				startActivityForResult(intent, Constants.REQUEST_CODE_ADD_HOME);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.event(activity, FlurryEventNames.HOW_MANY_USERS_ADDED_ADD_HOME);
				MyApplication.getInstance().logEvent(FirebaseEvents.INFORMATIVE+"_"+FirebaseEvents.VIEW_ACCOUNT+"_"+ FirebaseEvents.ADD_HOME, new Bundle());
				FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "Add Home");
			}
		});

		relativeLayoutAddWork.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(activity, AddPlaceActivity.class);
				intent.putExtra(Constants.KEY_REQUEST_CODE, Constants.REQUEST_CODE_ADD_WORK);
				intent.putExtra(Constants.KEY_ADDRESS, Prefs.with(activity).getString(SPLabels.ADD_WORK, ""));
				startActivityForResult(intent, Constants.REQUEST_CODE_ADD_WORK);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.event(activity, FlurryEventNames.HOW_MANY_USERS_ADDED_ADD_WORK);
				MyApplication.getInstance().logEvent(FirebaseEvents.INFORMATIVE+"_"+FirebaseEvents.VIEW_ACCOUNT+"_"+FirebaseEvents.ADD_WORK, new Bundle());
				FlurryEventLogger.eventGA(Constants.INFORMATIVE, TAG, "Add Work");
			}
		});

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

		setSavePlaces();

		getApiFetchUserAddress().hit();

		return rootView;
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
			setSavePlaces();
		} else if (resultCode == FragmentActivity.RESULT_CANCELED) {
			setSavePlaces();
		}
	}


	private void setSavePlaces() {
		if (!Prefs.with(activity).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")) {
			String homeString = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
			SearchResult searchResult = new Gson().fromJson(homeString, SearchResult.class);
			textViewAddHome.setText(getResources().getString(R.string.home));
			textViewAddHomeValue.setVisibility(View.VISIBLE);
			textViewAddHomeValue.setText(searchResult.getAddress());
			imageViewEditHome.setVisibility(View.VISIBLE);
		} else{
			textViewAddHome.setText(getResources().getString(R.string.add_home));
			textViewAddHomeValue.setVisibility(View.GONE);
			imageViewEditHome.setVisibility(View.GONE);
		}

		if (!Prefs.with(activity).getString(SPLabels.ADD_WORK, "").equalsIgnoreCase("")) {
			String workString = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");
			SearchResult searchResult = new Gson().fromJson(workString, SearchResult.class);
			textViewAddWork.setText(getResources().getString(R.string.work));
			textViewAddWorkValue.setVisibility(View.VISIBLE);
			textViewAddWorkValue.setText(searchResult.getAddress());
			imageViewEditWork.setVisibility(View.VISIBLE);
		} else{
			textViewAddWork.setText(getResources().getString(R.string.add_work));
			textViewAddWorkValue.setVisibility(View.GONE);
			imageViewEditWork.setVisibility(View.GONE);
		}

		if(savedPlacesAdapter != null){
			savedPlacesAdapter.notifyDataSetChanged();
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
					Utils.showToast(activity, "addresses fetched");
					setSavePlaces();
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
