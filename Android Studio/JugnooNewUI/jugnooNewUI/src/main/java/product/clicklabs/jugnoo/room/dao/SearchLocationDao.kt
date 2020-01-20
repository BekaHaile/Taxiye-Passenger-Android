package product.clicklabs.jugnoo.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import product.clicklabs.jugnoo.room.model.SearchLocation

@Dao
interface SearchLocationDao {
    // 0 = pickup location
    @Query("SELECT * FROM search_location WHERE type = 0")
    fun getPickupLocations() : List<SearchLocation>

    //1 = drop location
    @Query("SELECT * FROM search_location WHERE type = 1")
    fun getDropLocations() : List<SearchLocation>

    @Query("SELECT * FROM search_location")
    fun getLocation() : List<SearchLocation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchedLocation(searchLocation : SearchLocation)

    @Query("DELETE FROM search_location WHERE id = :id")
    fun deleteLocation(id:Int)
    @Query("DELETE FROM search_location")
    fun deleteAll()

}