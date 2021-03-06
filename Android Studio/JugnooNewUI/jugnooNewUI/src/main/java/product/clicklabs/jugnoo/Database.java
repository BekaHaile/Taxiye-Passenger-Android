package product.clicklabs.jugnoo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Handles database related work
 */
public class Database {																	// class for handling database related activities

	private static Database dbInstance;

	private static String DATABASE_NAME;

	private static final int DATABASE_VERSION = 2;

	private DbHelper dbHelper;

	private SQLiteDatabase database;

	// ***************** table_email_suggestions table columns
	// **********************//
	private static final String TABLE_EMAILS = "table_email_suggestions";
	
	// ***************** table_previous_latlng table columns
		// **********************//
	private static final String TABLE_PREVIOUS_PATH = "table_previous_path";
	private static final String SLAT = "slat";
	private static final String SLNG = "slng";
	private static final String DLAT = "dlat";
	private static final String DLNG = "dlng";

	
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
			createAllTables(database);
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			onCreate(database);
		}

	}
	
	private static void createAllTables(SQLiteDatabase database){
		// table_email_suggestions
					database.execSQL(" DROP TABLE IF EXISTS " + TABLE_EMAILS + ";");

					// table_previous_latlng
					database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_PREVIOUS_PATH + " (" 
							+ SLAT + " REAL NOT NULL" + ","
							+ SLNG + " REAL NOT NULL" + ","
							+ DLAT + " REAL NOT NULL" + ","
							+ DLNG + " REAL NOT NULL" + ""
							+ ");");
	}
	

	public static Database getInstance(Context context) {
		if (dbInstance == null) {
			dbInstance = new Database(context);
		} 
		else if (!dbInstance.database.isOpen()) {
			dbInstance = null;
			dbInstance = new Database(context);
		}
		return dbInstance;
	}
	
	private Database(Context context) {
		DATABASE_NAME = context.getString(R.string.db_name);
		dbHelper = new DbHelper(context);
		database = dbHelper.getWritableDatabase();
		createAllTables(database);
	}

	public void close() {
		database.close();
		dbHelper.close();
		System.gc();
	}

	
	
	public void insertPolyLine(LatLng slatLng, LatLng dlatLng){
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database.SLAT, slatLng.latitude);
			contentValues.put(Database.SLNG, slatLng.longitude);
			contentValues.put(Database.DLAT, dlatLng.latitude);
			contentValues.put(Database.DLNG, dlatLng.longitude);
			database.insert(Database.TABLE_PREVIOUS_PATH, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<Pair<LatLng, LatLng>> getSavedPath(){
		
		ArrayList<Pair<LatLng, LatLng>> pathList = new ArrayList<Pair<LatLng, LatLng>>();
		
		String[] columns = new String[] { Database.SLAT, Database.SLNG, Database.DLAT, Database.DLNG };

		Cursor cursor = database.query(Database.TABLE_PREVIOUS_PATH, columns, null, null, null, null, null);
		
		int in0 = cursor.getColumnIndex(Database.SLAT);
		int in1 = cursor.getColumnIndex(Database.SLNG);
		int in2 = cursor.getColumnIndex(Database.DLAT);
		int in3 = cursor.getColumnIndex(Database.DLNG);
		
		
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
			pathList.add(new Pair<LatLng, LatLng>(new LatLng(cursor.getDouble(in0), cursor.getDouble(in1)),
					new LatLng(cursor.getDouble(in2), cursor.getDouble(in3))));
		}
		
		return pathList;
	}
	
	
	
	public void deleteSavedPath(){
		database.delete(Database.TABLE_PREVIOUS_PATH, null, null);
	}
	
	
}