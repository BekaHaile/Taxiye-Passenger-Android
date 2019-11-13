package product.clicklabs.jugnoo.directions.room.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.text.DecimalFormat

@Entity(tableName = "tb_point")
data class Point(
        @ColumnInfo(name = "pathId")
        val pathId:Long,
        @ColumnInfo(name = "lat")
        val lat:Double,
        @ColumnInfo(name = "lng")
        val lng:Double

){
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
}
@Entity(tableName = "tb_path")
data class Path(
        @ColumnInfo(name = "pLat")
        val plat:Double,
        @ColumnInfo(name = "pLng")
        val pLng:Double,
        @ColumnInfo(name = "dLat")
        val dLat:Double,
        @ColumnInfo(name = "dLng")
        val dLng:Double,
        @ColumnInfo(name = "distance")
        val distance:Double,
        @ColumnInfo(name = "time")
        val time:Double,
        @ColumnInfo(name = "timeStamp")
        val timeStamp:Long


){
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0


    fun getDistanceText():String{
        val df = DecimalFormat("#.##")
        return df.format(distance/1000.0).plus(" km")
    }

    fun getTimeText():String{
        return (time/60.0).toInt().toString().plus(" min")
    }
}
