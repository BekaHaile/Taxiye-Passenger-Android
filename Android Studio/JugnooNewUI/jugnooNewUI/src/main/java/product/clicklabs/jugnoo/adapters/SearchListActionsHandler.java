package product.clicklabs.jugnoo.adapters;

import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.SearchResult;

/**
 * Created by socomo20 on 7/4/15.
 */
public interface SearchListActionsHandler {
	void onTextChange(String text);
    void onSearchPre();
    void onSearchPost();
    void onPlaceClick(AutoCompleteSearchResult autoCompleteSearchResult);
    void onPlaceSearchPre();
    void onPlaceSearchPost(SearchResult searchResult);
    void onPlaceSearchError();
}
