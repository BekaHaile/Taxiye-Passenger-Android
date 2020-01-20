package product.clicklabs.jugnoo.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_location")
data class SearchLocation(
        @ColumnInfo(name = "latitude")
        val slat:Double,
        @ColumnInfo(name = "longitude")
        val sLng:Double,
        @ColumnInfo(name = "name")
        val name: String,
        @ColumnInfo(name = "address")
        val address: String,
        @ColumnInfo(name = "placeId")
        val placeId: String,
        @ColumnInfo(name = "date")
        val date: Long,
        @ColumnInfo(name = "type") // type:- 0 = pickup, 1 = drop location
        val type: Int

){
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
}
