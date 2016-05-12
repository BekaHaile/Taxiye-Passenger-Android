package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.LocalGson;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Base adapter for google autocomplete search and pressing on a search item fetches LatLng for that place.
 *
 * Created by socomo20 on 7/4/15.
 */
public class SearchListAdapter extends BaseAdapter{

    class ViewHolderSearchItem {
        TextView textViewSearchName, textViewSearchAddress;
        ImageView imageViewType, imageViewSep;
        RelativeLayout relative;
        int id;
    }

    Context context;
    LayoutInflater mInflater;
    ViewHolderSearchItem holder;
    EditText editTextForSearch;
    SearchListActionsHandler searchListActionsHandler;
    LatLng defaultSearchPivotLatLng;

    ArrayList<AutoCompleteSearchResult> autoCompleteSearchResultsForSearch;
    ArrayList<AutoCompleteSearchResult> autoCompleteSearchResults;

	private GoogleApiClient mGoogleApiClient;
    private boolean showSavedPlaces;

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
							 GoogleApiClient mGoogleApiClient, SearchListActionsHandler searchListActionsHandler)
            throws IllegalStateException{
        if(context instanceof Activity) {
            this.context = context;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.autoCompleteSearchResultsForSearch = new ArrayList<>();
            this.autoCompleteSearchResults = new ArrayList<>();
            this.editTextForSearch = editTextForSearch;
            this.defaultSearchPivotLatLng = searchPivotLatLng;
            this.searchListActionsHandler = searchListActionsHandler;
			this.mGoogleApiClient = mGoogleApiClient;
            this.showSavedPlaces = true;
            this.editTextForSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
					SearchListAdapter.this.searchListActionsHandler.onTextChange(s.toString());
                    if (s.length() > 0) {
                        getSearchResults(s.toString().trim(), SearchListAdapter.this.getPivotLatLng());
                    }
                    else{
                        autoCompleteSearchResultsForSearch.clear();
                        addFavoriteLocations("");
                        setResults(autoCompleteSearchResultsForSearch);
                    }
                }
            });

            this.editTextForSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    Utils.hideSoftKeyboard((Activity) context, SearchListAdapter.this.editTextForSearch);
                    return true;
                }
            });

        }
        else{
            throw new IllegalStateException("context passed is not of Activity type");
        }
    }

    public synchronized void setResults(ArrayList<AutoCompleteSearchResult> autoCompleteSearchResults) {
        this.autoCompleteSearchResults.clear();
        this.autoCompleteSearchResults.addAll(autoCompleteSearchResults);
        this.notifyDataSetChanged();
    }

    public void addSavedLocationsToList(){
        autoCompleteSearchResultsForSearch.clear();
        addFavoriteLocations("");
        setResults(autoCompleteSearchResultsForSearch);
    }

    @Override
    public int getCount() {
        return autoCompleteSearchResults.size();
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
            convertView = mInflater.inflate(R.layout.list_item_search_item, null);

            holder.textViewSearchName = (TextView) convertView.findViewById(R.id.textViewSearchName);
            holder.textViewSearchName.setTypeface(Fonts.mavenMedium(context));
            holder.textViewSearchAddress = (TextView) convertView.findViewById(R.id.textViewSearchAddress);
            holder.textViewSearchAddress.setTypeface(Fonts.mavenMedium(context));
            holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);
            holder.imageViewType = (ImageView)convertView.findViewById(R.id.imageViewType);
            holder.imageViewSep = (ImageView) convertView.findViewById(R.id.imageViewSep);

            holder.relative.setTag(holder);

            holder.relative.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 110));
            ASSL.DoMagic(holder.relative);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderSearchItem) convertView.getTag();
        }


        try {
            holder.id = position;

            holder.textViewSearchName.setText(autoCompleteSearchResults.get(position).name);
            holder.textViewSearchAddress.setText(autoCompleteSearchResults.get(position).address);

            if(autoCompleteSearchResults.get(position).name == SPLabels.ADD_HOME){
                holder.imageViewType.setVisibility(View.VISIBLE);
                holder.imageViewType.setImageResource(R.drawable.home);
            } else if(autoCompleteSearchResults.get(position).name == SPLabels.ADD_WORK){
                holder.imageViewType.setVisibility(View.VISIBLE);
                holder.imageViewType.setImageResource(R.drawable.work);
            } else{
                holder.imageViewType.setVisibility(View.GONE);
            }

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
						Utils.hideSoftKeyboard((Activity) context, editTextForSearch);
						AutoCompleteSearchResult autoCompleteSearchResult = autoCompleteSearchResults.get(holder.id);
                        Log.e("SearchListAdapter", "on click="+autoCompleteSearchResult);
						if (!"".equalsIgnoreCase(autoCompleteSearchResult.placeId)) {
							searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
							getSearchResultFromPlaceId(autoCompleteSearchResult.getName(), autoCompleteSearchResult.placeId);
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
    public synchronized void notifyDataSetChanged() {
        if (autoCompleteSearchResults.size() > 1) {
            if (autoCompleteSearchResults.contains(new AutoCompleteSearchResult("No results found", "", ""))) {
                autoCompleteSearchResults.remove(autoCompleteSearchResults.indexOf(new AutoCompleteSearchResult("No results found", "", "")));
            }
        }

        super.notifyDataSetChanged();
    }

	private LatLng getPivotLatLng(){
		if(Data.lastRefreshLatLng != null){
			return Data.lastRefreshLatLng;
		} else{
			return defaultSearchPivotLatLng;
		}
	}

    public void setShowSavedPlaces(boolean showSavedPlaces) {
        this.showSavedPlaces = showSavedPlaces;
    }

    private boolean refreshingAutoComplete = false;

    private synchronized void getSearchResults(final String searchText, final LatLng latLng) {
        try {
			if (!refreshingAutoComplete) {
				searchListActionsHandler.onSearchPre();
				Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, searchText,
						new LatLngBounds.Builder().include(latLng).build(),
						null).setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
					@Override
					public void onResult(AutocompletePredictionBuffer autocompletePredictions) {
						try {
							refreshingAutoComplete = true;
							autoCompleteSearchResultsForSearch.clear();
							for (AutocompletePrediction autocompletePrediction : autocompletePredictions) {
								String name = autocompletePrediction.getDescription().split(",")[0];
								autoCompleteSearchResultsForSearch.add(new AutoCompleteSearchResult(name,
										autocompletePrediction.getDescription(), autocompletePrediction.getPlaceId()));
							}
							autocompletePredictions.release();

                            if(showSavedPlaces) {
                                addFavoriteLocations(searchText);
                            }
							setSearchResultsToList();
							refreshingAutoComplete = false;

							if (!editTextForSearch.getText().toString().trim().equalsIgnoreCase(searchText)) {
								recallSearch(editTextForSearch.getText().toString().trim());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private synchronized void recallSearch(final String searchText){
		((Activity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getSearchResults(searchText, SearchListAdapter.this.getPivotLatLng());
			}
		});
	}

	private synchronized void setSearchResultsToList() {
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (autoCompleteSearchResultsForSearch.size() == 0) {
                    if(AppStatus.getInstance(context).isOnline(context)) {
                        autoCompleteSearchResultsForSearch.add(new AutoCompleteSearchResult(context.getResources()
                                .getString(R.string.no_results_found), "", ""));
                    } else{
                        autoCompleteSearchResultsForSearch.add(new AutoCompleteSearchResult(context.getResources()
                                .getString(R.string.no_internet_connection), "", ""));
                    }
                }
                SearchListAdapter.this.setResults(autoCompleteSearchResultsForSearch);
                searchListActionsHandler.onSearchPost();
            }
        });
    }


	private synchronized void addFavoriteLocations(String searchText){
		try {
			if(!Prefs.with(context).getString(SPLabels.ADD_GYM, "").equalsIgnoreCase("")) {
				if (SPLabels.ADD_GYM.toLowerCase().contains(searchText.toLowerCase()) ||
						Prefs.with(context).getString(SPLabels.ADD_GYM, "").toLowerCase().contains(searchText.toLowerCase())) {
					AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(Prefs.with(context).getString(SPLabels.ADD_GYM, ""));
					searchResult.address = searchResult.name+", "+searchResult.address;
					searchResult.name = SPLabels.ADD_GYM;
					autoCompleteSearchResultsForSearch.add(0, searchResult);
				}
			}

			if(!Prefs.with(context).getString(SPLabels.ADD_FRIEND, "").equalsIgnoreCase("")) {
				if (SPLabels.ADD_FRIEND.toLowerCase().contains(searchText.toLowerCase()) ||
						Prefs.with(context).getString(SPLabels.ADD_FRIEND, "").toLowerCase().contains(searchText.toLowerCase())) {
					AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(Prefs.with(context).getString(SPLabels.ADD_FRIEND, ""));
					searchResult.address = searchResult.name+", "+searchResult.address;
					searchResult.name = SPLabels.ADD_FRIEND;
					autoCompleteSearchResultsForSearch.add(0, searchResult);
				}
			}

			if(!Prefs.with(context).getString(SPLabels.ADD_WORK, "").equalsIgnoreCase("")) {
				if (SPLabels.ADD_WORK.toLowerCase().contains(searchText.toLowerCase()) ||
						Prefs.with(context).getString(SPLabels.ADD_WORK, "").toLowerCase().contains(searchText.toLowerCase())
                        || searchText.equalsIgnoreCase("")) {
					AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(Prefs.with(context).getString(SPLabels.ADD_WORK, ""));
					//searchResult.address = searchResult.name+", "+searchResult.address;
					searchResult.name = SPLabels.ADD_WORK;
					autoCompleteSearchResultsForSearch.add(0, searchResult);
				}
			}

			if(!Prefs.with(context).getString(SPLabels.ADD_HOME, "").equalsIgnoreCase("")) {
				if(SPLabels.ADD_HOME.toLowerCase().contains(searchText.toLowerCase()) ||
						Prefs.with(context).getString(SPLabels.ADD_HOME, "").toLowerCase().contains(searchText.toLowerCase())
                        || searchText.equalsIgnoreCase("")) {
					AutoCompleteSearchResult searchResult = new LocalGson().getAutoCompleteSearchResultFromJSON(Prefs.with(context).getString(SPLabels.ADD_HOME, ""));
					//searchResult.address = searchResult.name+", "+searchResult.address;
					searchResult.name = SPLabels.ADD_HOME;
					autoCompleteSearchResultsForSearch.add(0, searchResult);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



    private synchronized void getSearchResultFromPlaceId(final String placeName, final String placeId) {
        searchListActionsHandler.onPlaceSearchPre();
        Log.e("SearchListAdapter", "getPlaceById placeId=" + placeId);
		Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
				.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        try {
                            Log.e("SearchListAdapter", "getPlaceById response=" + places);
                            if (places.getStatus().isSuccess()) {
                                final Place myPlace = places.get(0);
                                final CharSequence thirdPartyAttributions = places.getAttributions();
                                SearchResult searchResult = new SearchResult(placeName, myPlace.getAddress().toString(), myPlace.getLatLng());
                                searchResult.setThirdPartyAttributions(thirdPartyAttributions);
                                setSearchResult(searchResult);
                            }
                            places.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        Log.v("after call back", "after call back");
    }

    private synchronized void setSearchResult(final SearchResult searchResult) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(searchResult != null) {
                    searchListActionsHandler.onPlaceSearchPost(searchResult);
                }
                else{
                    DialogPopup.alertPopup((Activity) context, "", Data.CHECK_INTERNET_MSG);
                    searchListActionsHandler.onPlaceSearchError();
                }
            }
        });
    }



	public interface SearchListActionsHandler {
		void onTextChange(String text);
		void onSearchPre();
		void onSearchPost();
		void onPlaceClick(AutoCompleteSearchResult autoCompleteSearchResult);
		void onPlaceSearchPre();
		void onPlaceSearchPost(SearchResult searchResult);
		void onPlaceSearchError();
        void onPlaceSaved();
	}



}
