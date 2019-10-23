package product.clicklabs.jugnoo.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import product.clicklabs.jugnoo.room.model.Path
import product.clicklabs.jugnoo.room.model.Segment

@Dao
interface DirectionsPathDao {

    //Segment queries------
    @Query("SELECT * FROM tb_segment WHERE engagementId = :engagementId")
    fun getSegments(engagementId:Int) : List<Segment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSegments(segments:List<Segment>)

    @Query("DELETE FROM tb_segment WHERE engagementId = :engagementId")
    fun deleteSegments(engagementId:Int)

    @Query("SELECT count(*) FROM tb_segment WHERE engagementId = :engagementId")
    fun getSegmentsCount(engagementId:Int) : Int


    //Path queries------
    @Query("SELECT * FROM tb_path WHERE engagementId = :engagementId AND pLat = :pLat AND pLng = :pLng AND dLat = :dLat AND dLng = :dLng")
    fun getPath(engagementId:Int, pLat:Double, pLng:Double, dLat:Double, dLng:Double) : List<Path>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPath(path:List<Path>)

    @Query("DELETE FROM tb_path WHERE engagementId = :engagementId")
    fun deletePath(engagementId:Int)


}