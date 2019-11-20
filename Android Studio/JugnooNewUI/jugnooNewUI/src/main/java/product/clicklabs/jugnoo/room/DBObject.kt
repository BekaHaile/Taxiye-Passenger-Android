package product.clicklabs.jugnoo.room

import product.clicklabs.jugnoo.MyApplication
import product.clicklabs.jugnoo.room.database.SearchLocationDB


object DBObject {

    private var searchLocationDB: SearchLocationDB? = null

    @Synchronized
    private fun createInstance() {
        if (searchLocationDB == null) {
            searchLocationDB = SearchLocationDB.getInstance(MyApplication.getInstance())
        }
    }

    fun getInstance(): SearchLocationDB? {
        if (searchLocationDB == null) createInstance()
        return searchLocationDB
    }

    fun clearInstance(){
        searchLocationDB = null
    }

}
