package product.clicklabs.jugnoo.utils;

import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.SearchResult;

/**
 * Created by socomo20 on 7/4/15.
 */
public interface SearchListActionsHandler {
    void onSearchPre();
    void onSearchPost();
    void onPlaceClick(AutoCompleteSearchResult autoCompleteSearchResult);
    void onPlaceSearchPre();
    void onPlaceSearchPost(SearchResult searchResult);
}
