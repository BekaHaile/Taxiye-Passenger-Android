package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.GoogleRestApis;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Base adapter for google autocomplete search and pressing on a search item fetches LatLng for that place.
 *
 * Created by socomo20 on 7/4/15.
 */
public class SearchListAdapter extends BaseAdapter{

    public TextWatcher getTextWatcherEditText() {
        return textWatcherEditText;
    }

    private final TextWatcher textWatcherEditText = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
			//You need to remove this to run only once
			handler.removeCallbacks(input_finish_checker);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                SearchListAdapter.this.searchListActionsHandler.onTextChange(s.toString());
                if (s.length() > 0) {
					last_text_edit = System.currentTimeMillis();
					handler.postDelayed(input_finish_checker.setTextToSearch(s.toString().trim()), delay);
                } else {
                    searchResultsForSearch.clear();
                    setResults(searchResultsForSearch);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }; ;

    class ViewHolderSearchItem {
        TextView textViewSearchName, textViewSearchAddress, textViewAddressUsed;
        ImageView imageViewType, imageViewSep, ivDeleteAddress, ivAddAddress;
        RelativeLayout relative;
        int id;
    }

	long delay = 700; // 1 seconds after user stops typing
	long last_text_edit = 0;
	Handler handler = new Handler();

	private CustomRunnable input_finish_checker = new CustomRunnable("");

    Context context;
    LayoutInflater mInflater;
    ViewHolderSearchItem holder;
    EditText editTextForSearch;
    SearchListActionsHandler searchListActionsHandler;
    LatLng defaultSearchPivotLatLng;

    ArrayList<SearchResult> searchResultsForSearch;
    ArrayList<SearchResult> searchResults;

	private GeoDataClient geoDataClient;
    private boolean showSavedPlaces;
    private int searchMode;
	private int favLocationsCount = 0;

    /**
     * Constructor for initializing search base adapter
     *
     * @param context
     * @param editTextForSearch edittext object whose text change will trigger autocomplete list
     * @param searchPivotLatLng LatLng for searching autocomplete results
     * @param searchListActionsHandler handler for custom actions
     * @throws IllegalStateException
     */
    public SearchListAdapter(final Context context, EditText editTextForSearch, LatLng searchPivotLatLng,
							 GeoDataClient geoDataClient, int searchMode, SearchListActionsHandler searchListActionsHandler,
                             boolean showSavedPlaces)
            throws IllegalStateException{
        if(context instanceof Activity) {
            this.context = context;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.searchResultsForSearch = new ArrayList<>();
            this.searchResults = new ArrayList<>();
            this.editTextForSearch = editTextForSearch;
            this.defaultSearchPivotLatLng = searchPivotLatLng;
            this.searchListActionsHandler = searchListActionsHandler;
			this.geoDataClient = geoDataClient;
            this.searchMode = searchMode;
            this.editTextForSearch.addTextChangedListener(textWatcherEditText);

            this.editTextForSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    Utils.hideSoftKeyboard((Activity) context, SearchListAdapter.this.editTextForSearch);
                    return true;
                }
            });

            this.showSavedPlaces = showSavedPlaces;
        }
        else{
            throw new IllegalStateException("context passed is not of Activity type");
        }
    }

    public void setResults(ArrayList<SearchResult> autoCompleteSearchResults) {
        this.searchResults.clear();
        this.searchResults.addAll(autoCompleteSearchResults);
        this.notifyDataSetChanged();
    }

    public void addSavedLocationsToList(){
        searchResultsForSearch.clear();
        setResults(searchResultsForSearch);
    }

    @Override
    public int getCount() {
        return searchResults.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolderSearchItem();
            convertView = mInflater.inflate(R.layout.list_item_saved_place, null);

            holder.textViewSearchName = (TextView) convertView.findViewById(R.id.textViewSearchName);
            holder.textViewSearchName.setTypeface(Fonts.mavenMedium(context));
            holder.textViewSearchAddress = (TextView) convertView.findViewById(R.id.textViewSearchAddress);
            holder.textViewSearchAddress.setTypeface(Fonts.mavenMedium(context));
			holder.textViewAddressUsed = (TextView) convertView.findViewById(R.id.textViewAddressUsed);
			holder.textViewAddressUsed.setTypeface(Fonts.mavenRegular(context));
            holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);
            holder.imageViewType = (ImageView)convertView.findViewById(R.id.imageViewType);
            holder.imageViewSep = (ImageView) convertView.findViewById(R.id.imageViewSep);
            holder.ivDeleteAddress = (ImageView) convertView.findViewById(R.id.ivDeleteAddress);
            holder.ivAddAddress = (ImageView) convertView.findViewById(R.id.ivAddAddress);

            holder.relative.setTag(holder);

            holder.relative.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ASSL.DoMagic(holder.relative);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderSearchItem) convertView.getTag();
        }


        try {
            holder.id = position;

			SearchResult searchResult = searchResults.get(position);

            holder.textViewSearchName.setText(searchResult.getName());
            holder.textViewSearchAddress.setText(searchResult.getAddress());
			if(searchResult.getAddress().equalsIgnoreCase("")){
				holder.textViewSearchAddress.setVisibility(View.GONE);
			}else {
				holder.textViewSearchAddress.setVisibility(View.VISIBLE);
			}

			if(TextUtils.isEmpty(searchResult.getName())){
				holder.textViewSearchName.setText(searchResult.getAddress());
				holder.textViewSearchAddress.setVisibility(View.GONE);
			}

            if(searchResult.getType() == SearchResult.Type.HOME){
                holder.imageViewType.setVisibility(View.VISIBLE);
                holder.imageViewType.setImageResource(R.drawable.ic_home);
            } else if(searchResult.getType() == SearchResult.Type.WORK){
                holder.imageViewType.setVisibility(View.VISIBLE);
                holder.imageViewType.setImageResource(R.drawable.ic_work);
            } else{
				holder.imageViewType.setVisibility(View.VISIBLE);
				if(searchResult.getType() == SearchResult.Type.LAST_SAVED
						|| searchResult.getType() == SearchResult.Type.RECENT) {
					holder.imageViewType.setImageResource(R.drawable.ic_recent_loc);
				} else{
					holder.imageViewType.setImageResource(R.drawable.ic_loc_other);
				}
            }



			holder.textViewAddressUsed.setVisibility(View.GONE);
			if(searchResult.getFreq() > 0) {
                if(searchResults.get(position).getFreq() <= 1){
                    holder.textViewAddressUsed.setText(context.getString(R.string.address_used_one_time_format,
                            String.valueOf(searchResults.get(position).getFreq())));
                } else {
                    holder.textViewAddressUsed.setText(context.getString(R.string.address_used_multiple_time_format,
                            String.valueOf(searchResults.get(position).getFreq())));
                }
				holder.textViewAddressUsed.setVisibility(View.VISIBLE);
			}

			holder.ivDeleteAddress.setVisibility(View.GONE);
			holder.ivAddAddress.setVisibility(View.GONE);
            if(position == getCount()-1){
                holder.imageViewSep.setVisibility(View.GONE);
            } else{
                holder.imageViewSep.setVisibility(View.VISIBLE);
            }

            holder.relative.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
					try {
						holder = (ViewHolderSearchItem) v.getTag();
                        final SearchResult autoCompleteSearchResult = searchResults.get(holder.id);
                        if(!context.getResources().getString(R.string.no_results_found).equalsIgnoreCase(autoCompleteSearchResult.getName())
                                && !context.getResources().getString(R.string.no_internet_connection).equalsIgnoreCase(autoCompleteSearchResult.getName())){
                            Utils.hideSoftKeyboard((Activity) context, editTextForSearch);
                            Log.e("SearchListAdapter", "on click="+autoCompleteSearchResult.getAddress());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (autoCompleteSearchResult.getPlaceId() != null
                                            && !"".equalsIgnoreCase(autoCompleteSearchResult.getPlaceId())) {
                                        searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
                                        getSearchResultFromPlaceId(autoCompleteSearchResult.getName(),autoCompleteSearchResult.getAddress(), autoCompleteSearchResult.getPlaceId());
                                    } else{
                                        searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
                                        searchListActionsHandler.onPlaceSearchPre();
                                        searchListActionsHandler.onPlaceSearchPost(autoCompleteSearchResult);
                                    }
                                }
                            }, 200);

                        }
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        if (searchResults.size() > 1) {
            if (searchResults.contains(new SearchResult(context.getResources()
                    .getString(R.string.no_results_found), "", "", 0, 0))) {
                searchResults.remove(searchResults.indexOf(new SearchResult(context.getResources()
                        .getString(R.string.no_results_found), "", "", 0, 0)));
            }
        }
        super.notifyDataSetChanged();
		searchListActionsHandler.onNotifyDataSetChanged(getCount());
    }

	private LatLng getPivotLatLng(){
		if(Data.autoData != null && Data.autoData.getLastRefreshLatLng() != null){
			return Data.autoData.getLastRefreshLatLng();
		} else{
			return defaultSearchPivotLatLng;
		}
	}

    private boolean refreshingAutoComplete = false;

    private void getSearchResults(final String searchText, final LatLng latLng) {
        try {
        	Log.e(SearchListAdapter.class.getSimpleName(), "getSearchResults running for: "+searchText);
			if (!refreshingAutoComplete) {
				refreshingAutoComplete = true;
				searchListActionsHandler.onSearchPre();
				String specifiedCountry = Prefs.with(context).getString(Constants.KEY_SPECIFIED_COUNTRY_PLACES_SEARCH, "");
                AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
						.setCountry(specifiedCountry)
						.build();
                geoDataClient.getAutocompletePredictions(searchText,
						new LatLngBounds.Builder().include(latLng).build(),
						!TextUtils.isEmpty(specifiedCountry) ? autocompleteFilter : null)
						.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
					@Override
					public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
						try {
							searchResultsForSearch.clear();
							for (AutocompletePrediction autocompletePrediction : task.getResult()) {
								String name = autocompletePrediction.getFullText(null).toString().split(",")[0];
								searchResultsForSearch.add(new SearchResult(name,
										autocompletePrediction.getFullText(null).toString(),
										autocompletePrediction.getPlaceId(), 0, 0));
							}
							task.getResult().release();

							addFavoriteLocations(searchText);

							setSearchResultsToList();
							refreshingAutoComplete = false;

							if (!editTextForSearch.getText().toString().trim().equalsIgnoreCase(searchText)) {
								recallSearch(editTextForSearch.getText().toString().trim());
							}
							GoogleRestApis.INSTANCE.logGoogleRestAPIC("0", "0", GoogleRestApis.API_NAME_AUTOCOMPLETE);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						refreshingAutoComplete = false;

						if (!editTextForSearch.getText().toString().trim().equalsIgnoreCase(searchText)) {
							recallSearch(editTextForSearch.getText().toString().trim());
						}
					}
				});
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void recallSearch(final String searchText){
		((Activity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getSearchResults(searchText, SearchListAdapter.this.getPivotLatLng());
			}
		});
	}

	private void setSearchResultsToList() {
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if ((searchResultsForSearch.size()) == 0 && (editTextForSearch.getText().toString().trim().length() > 0)) {
                    if(MyApplication.getInstance().isOnline()) {
                        searchResultsForSearch.add(new SearchResult(context.getResources()
                                .getString(R.string.no_results_found), "", "", 0, 0));
                    } else{
                        searchResultsForSearch.add(new SearchResult(context.getResources()
                                .getString(R.string.no_internet_connection), "", "", 0, 0));
                    }
                }
                SearchListAdapter.this.setResults(searchResultsForSearch);
                searchListActionsHandler.onSearchPost();
            }
        });
    }


	private void addFavoriteLocations(String searchText){
		try {
            if(showSavedPlaces && editTextForSearch.getText().length() > 0) {
				favLocationsCount = 0;
				try {
					for(int i = Data.userData.getSearchResultsRecent().size()-1; i >= 0; i--){
						SearchResult searchResult = Data.userData.getSearchResultsRecent().get(i);
						if(searchResult.getName().toLowerCase().contains(searchText.toLowerCase())
								|| searchResult.getAddress().toLowerCase().contains(searchText.toLowerCase())
								|| searchText.equalsIgnoreCase("")){
							searchResult.setType(SearchResult.Type.RECENT);
							searchResultsForSearch.add(0, searchResult);
							favLocationsCount++;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					for(int i = Data.userData.getSearchResults().size()-1; i >= 0; i--){
						SearchResult searchResult = Data.userData.getSearchResults().get(i);
						if(searchResult.getName().toLowerCase().contains(searchText.toLowerCase())
								|| searchResult.getAddress().toLowerCase().contains(searchText.toLowerCase())
								|| searchText.equalsIgnoreCase("")){
							searchResultsForSearch.add(0, searchResult);
							favLocationsCount++;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (!Prefs.with(context).getString(SPLabels.ADD_WORK, "").equalsIgnoreCase("")) {
                    if (SPLabels.ADD_WORK.toLowerCase().contains(searchText.toLowerCase()) ||
                            Prefs.with(context).getString(SPLabels.ADD_WORK, "").toLowerCase().contains(searchText.toLowerCase())
                            || searchText.equalsIgnoreCase("")) {
                        SearchResult searchResult = new Gson().fromJson(Prefs.with(context).getString(SPLabels.ADD_WORK, ""), SearchResult.class);
                        searchResult.setName(SPLabels.ADD_WORK);
						searchResult.setType(SearchResult.Type.WORK);
                        searchResultsForSearch.add(0, searchResult);
						favLocationsCount++;
                    }
                }

                if (!Prefs.with(context).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")) {
                    if (SPLabels.ADD_HOME.toLowerCase().contains(searchText.toLowerCase()) ||
                            Prefs.with(context).getString(SPLabels.ADD_HOME, "").toLowerCase().contains(searchText.toLowerCase())
                            || searchText.equalsIgnoreCase("")) {
                        SearchResult searchResult = new Gson().fromJson(Prefs.with(context).getString(SPLabels.ADD_HOME, ""), SearchResult.class);
                        searchResult.setName(SPLabels.ADD_HOME);
						searchResult.setType(SearchResult.Type.HOME);
                        searchResultsForSearch.add(0, searchResult);
						favLocationsCount++;
                    }
                }
            } else{
				favLocationsCount = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



    private void getSearchResultFromPlaceId(final String placeName, final String placeAddress, final String placeId) {
		try {
			searchListActionsHandler.onPlaceSearchPre();
			DialogPopup.showLoadingDialog(context, context.getString(R.string.loading));
			Log.e("SearchListAdapter", "getPlaceById placeId=" + placeId);
			geoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
				@Override
				public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
					try {
						Log.e("SearchListAdapter", "getPlaceById response=" + task.getResult());
						if (task.isSuccessful()) {
							final Place myPlace = task.getResult().get(0);
							final CharSequence thirdPartyAttributions = task.getResult().getAttributions();
							SearchResult searchResult = new SearchResult(placeName, placeAddress, placeId,
									myPlace.getLatLng().latitude, myPlace.getLatLng().longitude);
							searchResult.setThirdPartyAttributions(thirdPartyAttributions);
							sendSearchResult(searchResult);
							if(myPlace != null && myPlace.getLatLng() != null) {
								GoogleRestApis.INSTANCE.logGoogleRestAPIC(String.valueOf(myPlace.getLatLng().latitude), String.valueOf(myPlace.getLatLng().longitude), GoogleRestApis.API_NAME_PLACES);
							}
						}
						task.getResult().release();
					} catch (Exception e) {
						e.printStackTrace();
					}
					DialogPopup.dismissLoadingDialog();
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					DialogPopup.dismissLoadingDialog();
				}
			});
			Log.v("after call back", "after call back");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void sendSearchResult(final SearchResult searchResult) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(searchResult != null) {
                    searchListActionsHandler.onPlaceSearchPost(searchResult);
                }
                else{
                    DialogPopup.alertPopup((Activity) context, "", context.getString(R.string.connection_lost_desc));
                    searchListActionsHandler.onPlaceSearchError();
                }
            }
        });
    }



	public interface SearchListActionsHandler {
		void onTextChange(String text);
		void onSearchPre();
		void onSearchPost();
		void onPlaceClick(SearchResult autoCompleteSearchResult);
		void onPlaceSearchPre();
		void onPlaceSearchPost(SearchResult searchResult);
		void onPlaceSearchError();
        void onPlaceSaved();
		void onNotifyDataSetChanged(int count);
	}

	private class CustomRunnable implements Runnable {
		private String textToSearch;
		public CustomRunnable(String textToSearch){
			this.textToSearch = textToSearch;
		}
    	public CustomRunnable setTextToSearch(String textToSearch){
			this.textToSearch = textToSearch;
			return this;
		}

		@Override
		public void run() {
			if (System.currentTimeMillis() > (last_text_edit + delay - 200)) {
				getSearchResults(textToSearch, SearchListAdapter.this.getPivotLatLng());
			}
		}
	}



}
