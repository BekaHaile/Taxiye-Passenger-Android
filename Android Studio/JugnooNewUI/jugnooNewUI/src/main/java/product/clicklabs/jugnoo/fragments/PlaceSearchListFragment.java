package product.clicklabs.jugnoo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import product.clicklabs.jugnoo.AddPlaceActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.KeyBoardStateHandler;
import product.clicklabs.jugnoo.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import rmn.androidscreenlibrary.ASSL;

public class PlaceSearchListFragment extends Fragment implements FlurryEventNames,
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Constants {
	
	private LinearLayout linearLayoutRoot;

	private EditText editTextSearch;
	private ProgressWheel progressBarSearch;
	private ImageView imageViewSearchCross;
	
	private RelativeLayout relativeLayoutAddHome, relativeLayoutAddWork;
	private TextView textViewAddHome, textViewAddWork;
	
	private LinearLayout linearLayoutScrollSearch;
	private NonScrollListView listViewSearch;
	private TextView textViewScrollSearch;

	private View rootView;
    private Activity activity;
	private GoogleApiClient mGoogleApiClient;
	private SearchListAdapter.SearchListActionsHandler searchListActionsHandler;

	private final int ADD_HOME = 2, ADD_WORK = 3;

	public PlaceSearchListFragment(SearchListAdapter.SearchListActionsHandler searchListActionsHandler){
		this.searchListActionsHandler = searchListActionsHandler;
	}

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(PlaceSearchListFragment.class.getSimpleName() + " started");
		mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(activity);
		mGoogleApiClient.disconnect();
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_place_search_list, container, false);

        activity = getActivity();

		mGoogleApiClient = new GoogleApiClient
				.Builder(activity)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		linearLayoutRoot.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
				ViewGroup.LayoutParams.MATCH_PARENT));
		ASSL.DoMagic(linearLayoutRoot);


		editTextSearch = (EditText) rootView.findViewById(R.id.editTextSearch);
		editTextSearch.setTypeface(Fonts.latoRegular(activity));
		progressBarSearch = (ProgressWheel) rootView.findViewById(R.id.progressBarSearch); progressBarSearch.setVisibility(View.GONE);
		imageViewSearchCross = (ImageView) rootView.findViewById(R.id.imageViewSearchCross); imageViewSearchCross.setVisibility(View.GONE);
		listViewSearch = (NonScrollListView) rootView.findViewById(R.id.listViewSearch);
		linearLayoutScrollSearch = (LinearLayout) rootView.findViewById(R.id.linearLayoutScrollSearch);
		textViewScrollSearch = (TextView) rootView.findViewById(R.id.textViewScrollSearch);
		relativeLayoutAddHome = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutAddHome);
		relativeLayoutAddWork = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutAddWork);
		textViewAddHome = (TextView)rootView.findViewById(R.id.textViewAddHome);
		textViewAddWork = (TextView)rootView.findViewById(R.id.textViewAddWork);

		linearLayoutScrollSearch.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutScrollSearch,
				textViewScrollSearch, new KeyBoardStateHandler() {
			@Override
			public void keyboardOpened() {

			}

			@Override
			public void keyBoardClosed() {

			}
		}));

		showSearchLayout();

		SearchListAdapter searchListAdapter = new SearchListAdapter(activity, editTextSearch, new LatLng(30.75, 76.78), mGoogleApiClient,
				new SearchListAdapter.SearchListActionsHandler() {

					@Override
					public void onTextChange(String text) {
						if(text.length() > 0){
							imageViewSearchCross.setVisibility(View.VISIBLE);
							relativeLayoutAddHome.setVisibility(View.GONE);
							relativeLayoutAddWork.setVisibility(View.GONE);
						}
						else{
							imageViewSearchCross.setVisibility(View.GONE);
							showSearchLayout();
						}
						searchListActionsHandler.onTextChange(text);
					}

					@Override
					public void onSearchPre() {
						progressBarSearch.setVisibility(View.VISIBLE);
						searchListActionsHandler.onSearchPre();
					}

					@Override
					public void onSearchPost() {
						progressBarSearch.setVisibility(View.GONE);
						searchListActionsHandler.onSearchPost();
					}

					@Override
					public void onPlaceClick(AutoCompleteSearchResult autoCompleteSearchResult) {
						searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
					}

					@Override
					public void onPlaceSearchPre() {
						searchListActionsHandler.onPlaceSearchPre();
					}

					@Override
					public void onPlaceSearchPost(SearchResult searchResult) {
						searchListActionsHandler.onPlaceSearchPost(searchResult);
					}

					@Override
					public void onPlaceSearchError() {
						searchListActionsHandler.onPlaceSearchError();
					}

					@Override
					public void onPlaceSaved() {
					}
				});

		listViewSearch.setAdapter(searchListAdapter);

		relativeLayoutAddHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(activity, AddPlaceActivity.class);
				intent.putExtra("requestCode", "HOME");
				intent.putExtra("address", Prefs.with(activity).getString(SPLabels.ADD_HOME, ""));
				startActivityForResult(intent, ADD_HOME);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

		relativeLayoutAddWork.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(activity, AddPlaceActivity.class);
				intent.putExtra("requestCode", "WORK");
				intent.putExtra("address", Prefs.with(activity).getString(SPLabels.ADD_WORK, ""));
				startActivityForResult(intent, ADD_WORK);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		

        return rootView;
	}

	private void showSearchLayout(){
		if(Prefs.with(activity).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")){
			relativeLayoutAddHome.setVisibility(View.VISIBLE);
		}else{
			relativeLayoutAddHome.setVisibility(View.GONE);
		}
		if(Prefs.with(activity).getString(SPLabels.ADD_WORK, "").equalsIgnoreCase("")){
			relativeLayoutAddWork.setVisibility(View.VISIBLE);
		}else{
			relativeLayoutAddWork.setVisibility(View.GONE);
		}
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}

	@Override
	public void onConnected(Bundle bundle) {

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if(resultCode == Activity.RESULT_OK) {
				if (requestCode == ADD_HOME) {
					String strResult = data.getStringExtra("PLACE");
					Gson gson = new Gson();
					AutoCompleteSearchResult searchResult = gson.fromJson(strResult, AutoCompleteSearchResult.class);
					if(searchResult != null){
						Prefs.with(activity).save(SPLabels.ADD_HOME, strResult);
						showSearchLayout();
					} else {
						textViewAddHome.setText("Add Home");
					}

				} else if (requestCode == ADD_WORK) {
					String strResult = data.getStringExtra("PLACE");
					Gson gson = new Gson();
					AutoCompleteSearchResult searchResult = gson.fromJson(strResult, AutoCompleteSearchResult.class);
					if(searchResult != null) {
						Prefs.with(activity).save(SPLabels.ADD_WORK, strResult);
						showSearchLayout();
					} else{
						textViewAddWork.setText("Add Work");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		searchListActionsHandler.onPlaceSaved();
	}

}
