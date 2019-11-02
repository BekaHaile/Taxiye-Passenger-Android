package product.clicklabs.jugnoo.room.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import product.clicklabs.jugnoo.room.dao.SearchLocationDao
import product.clicklabs.jugnoo.room.model.SearchLocation



@Database(entities = [
    SearchLocation::class
],     //******* version should be increase if any change done with schema ***** //
        version = 1)
abstract class SearchLocationDB : RoomDatabase(){

    companion object {
        @Volatile
        private var instance:SearchLocationDB? = null

        fun getInstance(context : Context) : SearchLocationDB?{
            if(instance == null){
                synchronized(SearchLocationDB::class.java){
                    if(instance == null){
                        instance = Room.databaseBuilder(context.applicationContext, SearchLocationDB::class.java, "search_location.db")
                                .addMigrations(MIGRATION_1_2)
                                .fallbackToDestructiveMigration()
                                .addCallback(callback).build()
                    }
                }
            }
            return instance
        }

        private val callback: RoomDatabase.Callback = object : RoomDatabase.Callback(){
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
            }
        }

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Since we didn't alter the table, there's nothing else to do here.
            }
        }


    }
    abstract fun getSearchLocation(): SearchLocationDao

}