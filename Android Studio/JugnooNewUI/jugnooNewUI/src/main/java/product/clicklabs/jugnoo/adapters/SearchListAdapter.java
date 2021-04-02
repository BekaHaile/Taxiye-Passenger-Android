package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.core.content.ContextCompat;
import product.clicklabs.jugnoo.Constants;
import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.apis.GoogleJungleCaching;
import product.clicklabs.jugnoo.apis.PlaceDetailCallback;
import product.clicklabs.jugnoo.apis.PlacesCallback;
import product.clicklabs.jugnoo.datastructure.SPLabels;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.retrofit.model.PlaceDetailsResponse;
import product.clicklabs.jugnoo.retrofit.model.Prediction;
import product.clicklabs.jugnoo.room.DBObject;
import product.clicklabs.jugnoo.room.apis.DBCoroutine;
import product.clicklabs.jugnoo.room.database.SearchLocationDB;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;


/**
 * Base adapter for google autocomplete search and pressing on a search item fetches LatLng for that place.
 *
 * Created by socomo20 on 7/4/15.
 */
public class SearchListAdapter extends BaseAdapter{


    private SparseArray<TextWatcherEditText> textWatcherMap = new SparseArray<>();

    public TextWatcherEditText getTextWatcherEditText(int editTextId){
        return textWatcherMap.get(editTextId);
    }
     class TextWatcherEditText implements  TextWatcher {
        public CustomRunnable input_finish_checker;

         public TextWatcherEditText(CustomRunnable input_finish_checker) {
             this.input_finish_checker = input_finish_checker;
         }

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
                SearchListAdapter.this.searchListActionsHandler.onTextChange(s.toString().trim());
                if (s.toString().trim().length() > 1 && s.toString().trim().length() <= 4) {

					searchResultsForSearch.clear();
					addFavoriteLocations(s.toString().trim(),input_finish_checker.editText);
					setSearchResultsToList(input_finish_checker.editText);

                }
                else if (s.toString().trim().length() > 4) {

					last_text_edit = System.currentTimeMillis();
					handler.removeCallbacks(input_finish_checker);
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

	long delay = 1000; // 1 seconds after user stops typing
	long last_text_edit = 0;
	Handler handler = new Handler();


    Context context;
    LayoutInflater mInflater;
    ViewHolderSearchItem holder;
    EditText editTextForSearch;
    SearchListActionsHandler searchListActionsHandler;
    LatLng defaultSearchPivotLatLng;

    ArrayList<SearchResult> searchResultsForSearch;
    ArrayList<SearchResult> searchResults;

    private boolean showSavedPlaces;
    private int searchMode;
	private int favLocationsCount = 0;

	private String uuidVal = null;

	private final String SET_LOCATION_ON_MAP = "<set_location_on_map>";
	private SearchResult searchResultSetLocationOnMap;
	boolean setLocationOnMapOnTop;
	private ArrayList<SearchResult> searchResultRecent;
    /**
     * Constructor for initializing search base adapter
     *
     * @param context
     * @param editTextForSearch edittext object whose text change will trigger autocomplete list
     * @param searchPivotLatLng LatLng for searching autocomplete results
     * @param searchListActionsHandler handler for custom actions
     */
    public SearchListAdapter(final Context context,LatLng searchPivotLatLng,
							 int searchMode, SearchListActionsHandler searchListActionsHandler,
                             boolean showSavedPlaces, boolean setLocationOnMapOnTop, EditText... editTextForSearch)
            throws IllegalStateException{
        if(context instanceof Activity) {
            this.context = context;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.searchResultsForSearch = new ArrayList<>();
            this.searchResults = new ArrayList<>();

            this.editTextForSearch = editTextForSearch[0];
            this.defaultSearchPivotLatLng = searchPivotLatLng;
            this.searchListActionsHandler = searchListActionsHandler;
            this.searchMode = searchMode;

            for(final EditText editText: editTextForSearch){
                TextWatcherEditText textWatcherEditText  = new TextWatcherEditText(new CustomRunnable("",editText));
                textWatcherMap.put(editText.getId(),textWatcherEditText);
                editText.addTextChangedListener(textWatcherEditText);
                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                        Utils.hideSoftKeyboard((Activity) context, editText);
                        return true;
                    }
                });
            }

			this.setLocationOnMapOnTop = setLocationOnMapOnTop;
			SearchLocationDB searchLocationDB = DBObject.INSTANCE.getInstance();
            if(searchLocationDB != null) {
				DBCoroutine.Companion.getAllLocations(searchLocationDB, searchLocation -> {
					searchResultRecent = PlaceSearchListFragment.getSearchResultsRecentAndSaved(context, searchLocation);
				});
			} else {
            	searchResultRecent = new ArrayList<>(Data.userData.getSearchResultsRecent());
			}


            this.showSavedPlaces = showSavedPlaces;
			searchResultSetLocationOnMap = new SearchResult(SET_LOCATION_ON_MAP, context.getString(R.string.set_location_on_map), "", 0, 0);

        }
        else{
            throw new IllegalStateException("context passed is not of Activity type");
        }
    }

    private String getUUID(){
    	if(uuidVal == null){
			uuidVal = UUID.randomUUID().toString();
    	}
    	return uuidVal;
	}

    public void setResults(ArrayList<SearchResult> autoCompleteSearchResults) {
        this.searchResults.clear();
        this.searchResults.addAll(autoCompleteSearchResults);
        if(setLocationOnMapOnTop) {
			this.searchResults.add(0, searchResultSetLocationOnMap);
		}
        this.notifyDataSetChanged();
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
            holder.textViewSearchName.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);
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


            convertView.setTag(holder);
        } else {
            holder = (ViewHolderSearchItem) convertView.getTag();
        }


        try {
            holder.id = position;

			SearchResult searchResult = searchResults.get(position);
			if(SET_LOCATION_ON_MAP.equalsIgnoreCase(searchResult.getName())){
				holder.textViewSearchName.setText(searchResult.getAddress());
				holder.textViewSearchAddress.setVisibility(View.GONE);
			} else {
				holder.textViewSearchAddress.setVisibility(View.VISIBLE);
				if(!TextUtils.isEmpty(searchResult.getName())
						&& !searchResult.getAddress().contains(searchResult.getName())){
					holder.textViewSearchName.setVisibility(View.VISIBLE);
					holder.textViewSearchName.setText(searchResult.getName());
					holder.textViewSearchAddress.setText(searchResult.getAddress());
				} else {
					String nameForDisp = (searchResult.getAddress().contains(",")) ?
							searchResult.getAddress().substring(0, searchResult.getAddress().indexOf(","))
							: searchResult.getAddress();

					holder.textViewSearchName.setVisibility(TextUtils.isEmpty(nameForDisp) ? View.GONE : View.VISIBLE);
					holder.textViewSearchName.setText(nameForDisp);

					holder.textViewSearchAddress.setText(TextUtils.isEmpty(nameForDisp) ? searchResult.getAddress()
							: searchResult.getAddress().replace(nameForDisp+", ", ""));
				}
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

			//seperator hidden by setting color transparent
			holder.imageViewSep.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));


            holder.relative.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
					try {
						holder = (ViewHolderSearchItem) v.getTag();
                        final SearchResult autoCompleteSearchResult = searchResults.get(holder.id);
						if(SET_LOCATION_ON_MAP.equalsIgnoreCase(autoCompleteSearchResult.getName())){
							Utils.hideSoftKeyboard((Activity) context, editTextForSearch);
							handler.postDelayed(() -> searchListActionsHandler.onSetLocationOnMapClicked(), 200);
						}
                        else if(!context.getResources().getString(R.string.no_results_found).equalsIgnoreCase(autoCompleteSearchResult.getName())
                                && !context.getResources().getString(R.string.no_internet_connection).equalsIgnoreCase(autoCompleteSearchResult.getName())){
							Utils.hideSoftKeyboard((Activity) context, editTextForSearch);
                            Log.e("SearchListAdapter", "on click="+autoCompleteSearchResult.getAddress());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (autoCompleteSearchResult.getPlaceId() != null
                                            && !"".equalsIgnoreCase(autoCompleteSearchResult.getPlaceId())
                                            && MapUtils.distance(autoCompleteSearchResult.getLatLng(), new LatLng(0,0)) <= 10) {
                                        searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
                                        getSearchResultFromPlaceId(autoCompleteSearchResult.getName(),autoCompleteSearchResult.getAddress(), autoCompleteSearchResult.getPlaceId());
                                    } else{
                                        searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
                                        searchListActionsHandler.onPlaceSearchPre();
                                        searchListActionsHandler.onPlaceSearchPost(autoCompleteSearchResult, null);
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



    private boolean refreshingAutoComplete = false;

    private void getSearchResults(final String searchText, final LatLng latLng,final EditText editText) {
        try {
        	Log.e(SearchListAdapter.class.getSimpleName(), "getSearchResults running for: "+searchText);
			if (!refreshingAutoComplete) {
				refreshingAutoComplete = true;
				searchListActionsHandler.onSearchPre();
				String specifiedCountry = Prefs.with(context).getString(Constants.KEY_SPECIFIED_COUNTRY_PLACES_SEARCH, "");

                String components = !TextUtils.isEmpty(specifiedCountry)? "country:"+specifiedCountry:"";
                String location = latLng.latitude+","+latLng.longitude;
                String radius = searchText.length() <= 3 ? "50" : (searchText.length() <= 5 ? "100": (searchText.length() <= 8 ? "1000" : "10000"));

				searchResultsForSearch.clear();
				addFavoriteLocations(searchText,editText);

				GoogleJungleCaching.INSTANCE.getAutoCompletePredictions(searchText, getUUID(), components, location, radius, new PlacesCallback() {
					@Override
					public void onAutocompletePredictionsReceived(List<Prediction> predictions) {
						try {
							if(predictions != null) {
								for (Prediction autocompletePrediction : predictions) {
									String name = autocompletePrediction.getDescription().split(",")[0];
									searchResultsForSearch.add(new SearchResult(name,
											autocompletePrediction.getDescription(),
											autocompletePrediction.getPlaceId(),
											autocompletePrediction.getLat() != null ? autocompletePrediction.getLat() : 0,
											autocompletePrediction.getLng() != null ? autocompletePrediction.getLng() : 0));
								}
							}

							setSearchResultsToList(editText);
							refreshingAutoComplete = false;

							if (!editText.getText().toString().trim().equalsIgnoreCase(searchText)) {
								handler.removeCallbacks(getTextWatcherEditText(editText.getId()).input_finish_checker);
								handler.postDelayed(getTextWatcherEditText(editText.getId()).input_finish_checker.setTextToSearch(editText.getText().toString().trim()), delay);
							}
						} catch (Exception e) {
							e.printStackTrace();
							searchListActionsHandler.onSearchPost();
						}
					}

					@Override
					public void onAutocompleteError() {
						refreshingAutoComplete = false;

						if (!editText.getText().toString().trim().equalsIgnoreCase(searchText)) {
							handler.removeCallbacks(getTextWatcherEditText(editText.getId()).input_finish_checker);
							handler.postDelayed(getTextWatcherEditText(editText.getId()).input_finish_checker.setTextToSearch(editText.getText().toString().trim()), delay);
						}
						searchListActionsHandler.onSearchPost();
					}
				});
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


	private void setSearchResultsToList(final EditText editTextForSearch) {
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


	private void addFavoriteLocations(String searchText,EditText editTextForSearch){
		try {
            if(showSavedPlaces && editTextForSearch.getText().length() > 0) {
				favLocationsCount = 0;
				try {
					if(searchResultRecent != null) {
						for (int i = searchResultRecent.size() - 1; i >= 0; i--) {
							SearchResult searchResult = searchResultRecent.get(i);
							if (searchResult.getName().toLowerCase().contains(searchText.toLowerCase())
									|| searchResult.getAddress().toLowerCase().contains(searchText.toLowerCase())
									|| searchText.equalsIgnoreCase("")) {
								searchResult.setType(SearchResult.Type.RECENT);
								searchResultsForSearch.add(0, searchResult);
								favLocationsCount++;
							}
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
			Log.v("after call back", "after call back");

			GoogleJungleCaching.INSTANCE.getPlaceById(placeId, placeAddress, defaultSearchPivotLatLng, getUUID(), new PlaceDetailCallback() {
				@Override
				public void onPlaceDetailReceived(@NotNull PlaceDetailsResponse placeDetailsResponse) {
					try {
						Log.e("SearchListAdapter", "getPlaceById response=" + placeDetailsResponse);
						SearchResult searchResult = new SearchResult(placeName, placeAddress, placeId,
								placeDetailsResponse.getResults().get(0).getGeometry().getLocation().getLat(),
								placeDetailsResponse.getResults().get(0).getGeometry().getLocation().getLng());
						sendSearchResult(searchResult);
					} catch (Exception e) {
						e.printStackTrace();
						searchListActionsHandler.onPlaceSearchError();
					}
					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void onPlaceDetailError() {
					DialogPopup.dismissLoadingDialog();
					searchListActionsHandler.onPlaceSearchError();
				}
			});


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void sendSearchResult(final SearchResult searchResult) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(searchResult != null) {
                    searchListActionsHandler.onPlaceSearchPost(searchResult, null);
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
		void onPlaceSearchPost(SearchResult searchResult, PlaceSearchListFragment.PlaceSearchMode placeSearchMode);
		void onPlaceSearchError();
        void onPlaceSaved();
		void onNotifyDataSetChanged(int count);
		void onSetLocationOnMapClicked();
		default void onPlaceSearchPostForStop(SearchResult searchResult, int position){}
	}

	private class CustomRunnable implements Runnable {
		private String textToSearch;
		private EditText editText;
		public CustomRunnable(String textToSearch,EditText editText){
			this.textToSearch = textToSearch;
			this.editText = editText;
		}
    	public CustomRunnable setTextToSearch(String textToSearch){
			this.textToSearch = textToSearch;
			return this;
		}

		@Override
		public void run() {
			if (System.currentTimeMillis() > (last_text_edit + delay - 200) && textToSearch.length() > 2) {
				getSearchResults(textToSearch, SearchListAdapter.this.defaultSearchPivotLatLng,editText);
			}
		}
	}



}
