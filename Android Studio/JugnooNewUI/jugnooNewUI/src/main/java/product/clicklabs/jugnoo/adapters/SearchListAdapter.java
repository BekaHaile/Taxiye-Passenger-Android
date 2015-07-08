package product.clicklabs.jugnoo.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

/**
 * Base adapter for google autocomplete search and pressing on a search item fetches LatLng for that place.
 *
 * Created by socomo20 on 7/4/15.
 */
public class SearchListAdapter extends BaseAdapter {

    class ViewHolderSearchItem {
        TextView textViewSearchName, textViewSearchAddress;
        LinearLayout relative;
        int id;
    }

    Context context;
    LayoutInflater mInflater;
    ViewHolderSearchItem holder;
    EditText editTextForSearch;
    SearchListActionsHandler searchListActionsHandler;
    LatLng searchPivotLatLng;

    ArrayList<AutoCompleteSearchResult> autoCompleteSearchResultsForSearch;
    ArrayList<AutoCompleteSearchResult> autoCompleteSearchResults;

    /**
     * Constructor for initializing search base adapter
     *
     * @param context
     * @param editTextForSearch edittext object whose text change will trigger autocomplete list
     * @param searchPivotLatLng LatLng for searching autocomplete results
     * @param searchListActionsHandler handler for custom actions
     * @throws IllegalStateException
     */
    public SearchListAdapter(Context context, EditText editTextForSearch, LatLng searchPivotLatLng, SearchListActionsHandler searchListActionsHandler) throws IllegalStateException{
        if(context instanceof Activity) {
            this.context = context;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.autoCompleteSearchResultsForSearch = new ArrayList<>();
            this.autoCompleteSearchResults = new ArrayList<>();
            this.editTextForSearch = editTextForSearch;
            this.searchPivotLatLng = searchPivotLatLng;
            this.searchListActionsHandler = searchListActionsHandler;
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
                    if (s.length() > 0) {
                        getSearchResults(s.toString().trim(), SearchListAdapter.this.searchPivotLatLng);
                    }
                    else{
                        autoCompleteSearchResultsForSearch.clear();
                        setResults(autoCompleteSearchResultsForSearch);
                    }
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
            holder.textViewSearchName.setTypeface(Fonts.latoRegular(context));
            holder.textViewSearchAddress = (TextView) convertView.findViewById(R.id.textViewSearchAddress);
            holder.textViewSearchAddress.setTypeface(Fonts.latoRegular(context));
            holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);

            holder.relative.setTag(holder);

            holder.relative.setLayoutParams(new ListView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
            ASSL.DoMagic(holder.relative);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderSearchItem) convertView.getTag();
        }


        try {
            holder.id = position;

            holder.textViewSearchName.setText(autoCompleteSearchResults.get(position).name);
            holder.textViewSearchAddress.setText(autoCompleteSearchResults.get(position).address);

            holder.relative.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder = (ViewHolderSearchItem) v.getTag();
                    Utils.hideSoftKeyboard((Activity) context, editTextForSearch);
                    AutoCompleteSearchResult autoCompleteSearchResult = autoCompleteSearchResults.get(holder.id);
                    if (!"".equalsIgnoreCase(autoCompleteSearchResult.placeId)) {
                        searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
                        getSearchResultFromPlaceId(autoCompleteSearchResult.placeId);
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


    private Thread autoCompleteThread;
    private boolean refreshingAutoComplete = false;

    private synchronized void getSearchResults(final String searchText, final LatLng latLng) {
        try {
            if (!refreshingAutoComplete) {
                searchListActionsHandler.onSearchPre();

                if (autoCompleteThread != null) {
                    autoCompleteThread.interrupt();
                }

                autoCompleteThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        refreshingAutoComplete = true;
                        autoCompleteSearchResultsForSearch.clear();
                        autoCompleteSearchResultsForSearch.addAll(MapUtils.getAutoCompleteSearchResultsFromGooglePlaces(searchText, latLng));

                        setSearchResultsToList();
                        refreshingAutoComplete = false;
                        autoCompleteThread = null;
                    }
                });
                autoCompleteThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private synchronized void setSearchResultsToList() {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (autoCompleteSearchResultsForSearch.size() == 0) {
                    autoCompleteSearchResultsForSearch.add(new AutoCompleteSearchResult("No results found", "", ""));
                }
                SearchListAdapter.this.setResults(autoCompleteSearchResultsForSearch);
                searchListActionsHandler.onSearchPost();
            }
        });
    }


    private synchronized void getSearchResultFromPlaceId(final String placeId) {
        searchListActionsHandler.onPlaceSearchPre();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchResult searchResult = MapUtils.getSearchResultsFromPlaceIdGooglePlaces(placeId);
                setSearchResult(searchResult);
            }
        }).start();
    }

    private synchronized void setSearchResult(final SearchResult searchResult) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(searchResult != null) {
                    searchListActionsHandler.onPlaceSearchPost(searchResult);
                }
                else{
                    searchListActionsHandler.onPlaceSearchError();
                }
            }
        });
    }


}
