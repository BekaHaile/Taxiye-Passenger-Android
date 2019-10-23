package product.clicklabs.jugnoo.room.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "tb_segment")
data class Segment(
        @ColumnInfo(name = "engagementId")
        val engagementId:Int,
        @ColumnInfo(name = "sLat")
        val slat:Double,
        @ColumnInfo(name = "sLng")
        val sLng:Double,
        @ColumnInfo(name = "dLat")
        val dLat:Double,
        @ColumnInfo(name = "dLng")
        val dLng:Double

){
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
}
@Entity(tableName = "tb_path")
data class Path(
        @ColumnInfo(name = "engagementId")
        val engagementId:Int,
        @ColumnInfo(name = "pLat")
        val plat:Double,
        @ColumnInfo(name = "pLng")
        val pLng:Double,
        @ColumnInfo(name = "dLat")
        val dLat:Double,
        @ColumnInfo(name = "dLng")
        val dLng:Double

){
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
}
