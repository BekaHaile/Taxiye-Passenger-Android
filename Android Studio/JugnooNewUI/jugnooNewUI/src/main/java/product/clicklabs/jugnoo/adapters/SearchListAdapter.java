package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.home.HomeActivity;
import product.clicklabs.jugnoo.utils.ASSL;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
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
                    getSearchResults(s.toString().trim(), SearchListAdapter.this.getPivotLatLng());
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

    ArrayList<SearchResult> searchResultsForSearch;
    ArrayList<SearchResult> searchResults;

	private GoogleApiClient mGoogleApiClient;
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
							 GoogleApiClient mGoogleApiClient, int searchMode, SearchListActionsHandler searchListActionsHandler,
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
			this.mGoogleApiClient = mGoogleApiClient;
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

    public synchronized void setResults(ArrayList<SearchResult> autoCompleteSearchResults) {
        this.searchResults.clear();
        this.searchResults.addAll(autoCompleteSearchResults);
        try {
            if(context instanceof HomeActivity && editTextForSearch.getText().length() == 0){
                String json;
                if(SearchListAdapter.this.searchMode == PlaceSearchListFragment.PlaceSearchMode.DROP.getOrdinal()){
                    json = Prefs.with(context).getString(SPLabels.LAST_DESTINATION, "");
                } else{
                    json = Prefs.with(context).getString(SPLabels.LAST_PICK_UP, "");
                }
                Type type = new TypeToken<ArrayList<SearchResult>>() {}.getType();
                ArrayList<SearchResult> lastPickUp = new Gson().fromJson(json, type);

				if(favLocationsCount < 5){
					int locationsToAdd = 5 - favLocationsCount;
					locationsToAdd = locationsToAdd > lastPickUp.size() ? lastPickUp.size() : locationsToAdd;
					for(int i=0; i<locationsToAdd; i++){
						lastPickUp.get(i).setType(SearchResult.Type.LAST_SAVED);
						searchResults.add(lastPickUp.get(i));
					}
				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            convertView = mInflater.inflate(R.layout.list_item_search_item, null);

            holder.textViewSearchName = (TextView) convertView.findViewById(R.id.textViewSearchName);
            holder.textViewSearchName.setTypeface(Fonts.mavenMedium(context));
            holder.textViewSearchAddress = (TextView) convertView.findViewById(R.id.textViewSearchAddress);
            holder.textViewSearchAddress.setTypeface(Fonts.mavenMedium(context));
			holder.textViewAddressUsed = (TextView) convertView.findViewById(R.id.textViewAddressUsed);
			holder.textViewAddressUsed.setTypeface(Fonts.mavenRegular(context));
            holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);
            holder.imageViewType = (ImageView)convertView.findViewById(R.id.imageViewType);
            holder.imageViewSep = (ImageView) convertView.findViewById(R.id.imageViewSep);

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
    public synchronized void notifyDataSetChanged() {
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
							searchResultsForSearch.clear();
							for (AutocompletePrediction autocompletePrediction : autocompletePredictions) {
                                String name = autocompletePrediction.getFullText(null).toString().split(",")[0];
								searchResultsForSearch.add(new SearchResult(name,
                                        autocompletePrediction.getFullText(null).toString(),
										autocompletePrediction.getPlaceId(), 0, 0));
							}
							autocompletePredictions.release();

                            addFavoriteLocations(searchText);

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


	private synchronized void addFavoriteLocations(String searchText){
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



    private synchronized void getSearchResultFromPlaceId(final String placeName, final String placeAddress, final String placeId) {
		try {
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
									SearchResult searchResult = new SearchResult(placeName, placeAddress, placeId,
											myPlace.getLatLng().latitude, myPlace.getLatLng().longitude);
									searchResult.setThirdPartyAttributions(thirdPartyAttributions);
									sendSearchResult(searchResult);
								}
								places.release();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			Log.v("after call back", "after call back");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private synchronized void sendSearchResult(final SearchResult searchResult) {
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



}
