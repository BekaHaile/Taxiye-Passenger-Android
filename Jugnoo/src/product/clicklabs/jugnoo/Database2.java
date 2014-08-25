package product.clicklabs.jugnoo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Handles database related work
 */
public class Database2 {																	// class for handling database related activities

	private static final String DATABASE_NAME = "jugnoo_database2";						// declaring database variables

	private static final int DATABASE_VERSION = 2;

	private DbHelper dbHelper;

	SQLiteDatabase database;

	
	private static final String TABLE_DRIVER_SERVICE_FAST = "table_driver_service_fast";
	private static final String FAST = "fast";
	
	
	/**
	 * Creates and opens database for the application use 
	 * @author shankar
	 *
	 */
	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			/****************************************** CREATING ALL THE TABLES *****************************************************/
			database.execSQL(" CREATE TABLE " + TABLE_DRIVER_SERVICE_FAST + " ("
					+ FAST + " TEXT" + ");");
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			onCreate(database);
		}

	}

	public Database2(Context context) {
		dbHelper = new DbHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
		System.gc();
	}
	
	
	public String getDriverServiceFast() {
		String[] columns = new String[] { Database2.FAST };
		Cursor cursor = database.query(Database2.TABLE_DRIVER_SERVICE_FAST, columns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String choice = cursor.getString(cursor.getColumnIndex(Database2.FAST));
			return choice;
		} else {
			return "no";
		}
	}
	
	public void updateDriverServiceFast(String choice) {
		String[] columns = new String[] { Database2.FAST };
		Cursor cursor = database.query(Database2.TABLE_DRIVER_SERVICE_FAST, columns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			deleteDriverServiceFast();
			insertDriverServiceFast(choice);
		} else {
			insertDriverServiceFast(choice);
		}
	}
	
	public void insertDriverServiceFast(String choice){
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.FAST, choice);
			database.insert(Database2.TABLE_DRIVER_SERVICE_FAST, null, contentValues);
		} catch(Exception e){
			Log.e("e","="+e);
		}
	}
	
	public void deleteDriverServiceFast(){
		try{
			database.delete(Database2.TABLE_DRIVER_SERVICE_FAST, null, null);
		} catch(Exception e){
			Log.e("e","="+e);
		}
	}
	
	
	
	
	
}