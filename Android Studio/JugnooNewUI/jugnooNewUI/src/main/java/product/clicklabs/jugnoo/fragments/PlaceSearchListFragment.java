package product.clicklabs.jugnoo.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.AddPlaceActivity;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.FlurryEventNames;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LocalGson;
import product.clicklabs.jugnoo.utils.NonScrollListView;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.ProgressWheel;
import product.clicklabs.jugnoo.utils.Utils;


public class PlaceSearchListFragment extends Fragment implements FlurryEventNames,
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Constants {
	
	private LinearLayout linearLayoutRoot;

	private EditText editTextSearch;
	private ProgressWheel progressBarSearch;
	private ImageView imageViewSearchCross, imageViewSearchGPSIcon;

	private LinearLayout linearLayoutAddFav;
	private RelativeLayout relativeLayoutAddHome, relativeLayoutAddWork;
	private TextView textViewAddHome, textViewAddWork;
	private ImageView imageViewSep;

	private LinearLayout linearLayoutScrollSearch;
	private NonScrollListView listViewSearch;
	private TextView textViewScrollSearch;

	private View rootView;
    private Activity activity;
	private GoogleApiClient mGoogleApiClient;
	private SearchListAdapter.SearchListActionsHandler searchListActionsHandler;
	private SearchListAdapter searchListAdapter;

	private final int ADD_HOME = 2, ADD_WORK = 3;

	public PlaceSearchListFragment(){

	}

	@SuppressLint("ValidFragment")
	public PlaceSearchListFragment(SearchListAdapter.SearchListActionsHandler searchListActionsHandler, GoogleApiClient mGoogleApiClient){
		this.searchListActionsHandler = searchListActionsHandler;
		this.mGoogleApiClient = mGoogleApiClient;
	}

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Config.getFlurryKey());
        FlurryAgent.onStartSession(activity, Config.getFlurryKey());
        FlurryAgent.onEvent(PlaceSearchListFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_place_search_list, container, false);


        activity = getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		new ASSL(activity, linearLayoutRoot, 1134, 720, false);


		editTextSearch = (EditText) rootView.findViewById(R.id.editTextSearch);
		editTextSearch.setTypeface(Fonts.latoRegular(activity));
		progressBarSearch = (ProgressWheel) rootView.findViewById(R.id.progressBarSearch); progressBarSearch.setVisibility(View.GONE);
		imageViewSearchCross = (ImageView) rootView.findViewById(R.id.imageViewSearchCross); imageViewSearchCross.setVisibility(View.GONE);
		listViewSearch = (NonScrollListView) rootView.findViewById(R.id.listViewSearch);
		linearLayoutScrollSearch = (LinearLayout) rootView.findViewById(R.id.linearLayoutScrollSearch);
		textViewScrollSearch = (TextView) rootView.findViewById(R.id.textViewScrollSearch);


		linearLayoutAddFav = (LinearLayout) rootView.findViewById(R.id.linearLayoutAddFav);
		relativeLayoutAddHome = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutAddHome);
		relativeLayoutAddWork = (RelativeLayout)rootView.findViewById(R.id.relativeLayoutAddWork);
		textViewAddHome = (TextView)rootView.findViewById(R.id.textViewAddHome);
		textViewAddWork = (TextView)rootView.findViewById(R.id.textViewAddWork);
		imageViewSep = (ImageView) rootView.findViewById(R.id.imageViewSep);
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

		showSearchLayout();

		searchListAdapter = new SearchListAdapter(activity, editTextSearch, new LatLng(30.75, 76.78), mGoogleApiClient,
				new SearchListAdapter.SearchListActionsHandler() {

					@Override
					public void onTextChange(String text) {
						try {
							if(text.length() > 0){
								imageViewSearchCross.setVisibility(View.VISIBLE);
								hideSearchLayout();
							}
							else{
								imageViewSearchCross.setVisibility(View.GONE);
								showSearchLayout();
							}
							searchListActionsHandler.onTextChange(text);
						} catch (Exception e) {
							e.printStackTrace();
						}
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
						progressBarSearch.setVisibility(View.VISIBLE);
						searchListActionsHandler.onPlaceSearchPre();
					}

					@Override
					public void onPlaceSearchPost(SearchResult searchResult) {
						progressBarSearch.setVisibility(View.GONE);
						searchListActionsHandler.onPlaceSearchPost(searchResult);
					}

					@Override
					public void onPlaceSearchError() {
						progressBarSearch.setVisibility(View.GONE);
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


		Bundle bundle = getArguments();
		String text = bundle.getString(KEY_SEARCH_FIELD_TEXT, "");
		String hint = bundle.getString(KEY_SEARCH_FIELD_HINT, "");
		if(hint.equalsIgnoreCase(activity.getResources().getString(R.string.set_pickup_location))){
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

        return rootView;
	}

	private void showSearchLayout(){
		String home = Prefs.with(activity).getString(SPLabels.ADD_HOME, "");
		String work = Prefs.with(activity).getString(SPLabels.ADD_WORK, "");

		if(home.equalsIgnoreCase("") || work.equalsIgnoreCase("")){
			linearLayoutAddFav.setVisibility(View.VISIBLE);
		} else{
			linearLayoutAddFav.setVisibility(View.GONE);
		}

		if(home.equalsIgnoreCase("")){
			relativeLayoutAddHome.setVisibility(View.VISIBLE);
		}else{
			relativeLayoutAddHome.setVisibility(View.GONE);
		}

		if(work.equalsIgnoreCase("")){
			relativeLayoutAddWork.setVisibility(View.VISIBLE);
			if(home.equalsIgnoreCase("")){
				imageViewSep.setVisibility(View.VISIBLE);
			} else{
				imageViewSep.setVisibility(View.GONE);
			}
		}else{
			relativeLayoutAddWork.setVisibility(View.GONE);
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
		try {
			super.onActivityResult(requestCode, resultCode, data);
			if(resultCode == Activity.RESULT_OK) {
				if (requestCode == ADD_HOME) {
					String strResult = data.getStringExtra("PLACE");
					AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(strResult);
					if(searchResult != null){
						Prefs.with(activity).save(SPLabels.ADD_HOME, strResult);
						showSearchLayout();
					} else {
						textViewAddHome.setText("Add Home");
					}

				} else if (requestCode == ADD_WORK) {
					String strResult = data.getStringExtra("PLACE");
					AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(strResult);
					if(searchResult != null) {
						Prefs.with(activity).save(SPLabels.ADD_WORK, strResult);
						showSearchLayout();
					} else{
						textViewAddWork.setText("Add Work");
					}
				}

			}
			searchListActionsHandler.onPlaceSaved();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ProgressWheel getProgressBarSearch(){
		return progressBarSearch;
	}

}
