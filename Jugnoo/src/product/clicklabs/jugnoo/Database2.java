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

	private static final String TABLE_DRIVER_SERVICE_RESTART_ON_REBOOT = "table_driver_service_restart_on_reboot";
	private static final String DRIVER_SERVICE_RESTART_ON_REBOOT = "driver_service_restart_on_reboot";
	
	private static final String TABLE_DRIVER_SERVICE_FAST = "table_driver_service_fast";
	private static final String FAST = "fast";
	
	private static final String TABLE_JUGNOO_ON = "table_jugnoo_on";
	private static final String JUGNOO_ON = "jugnoo_on";
	
	private static final String TABLE_DRIVER_LOC_DATA = "table_driver_loc_data";
	private static final String DLD_ACCESS_TOKEN = "dld_access_token";
	private static final String DLD_DEVICE_TOKEN = "dld_device_token";
	private static final String DLD_SERVER_URL = "dld_server_url";
	
	
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
			Database2.createAllTables(database);
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			onCreate(database);
		}
		

	}

	
	public static void createAllTables(SQLiteDatabase database){
		/****************************************** CREATING ALL THE TABLES *****************************************************/
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_SERVICE_RESTART_ON_REBOOT + " ("
				+ DRIVER_SERVICE_RESTART_ON_REBOOT + " TEXT" + ");");
		
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_SERVICE_FAST + " ("
				+ FAST + " TEXT" + ");");
		
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_JUGNOO_ON + " ("
				+ JUGNOO_ON + " TEXT" + ");");
		
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_LOC_DATA + " ("
				+ DLD_ACCESS_TOKEN + " TEXT, " 
				+ DLD_DEVICE_TOKEN + " TEXT, " 
				+ DLD_SERVER_URL + " TEXT" 
				+ ");");
	}
	
	
	
	public Database2(Context context) {
		dbHelper = new DbHelper(context);
		database = dbHelper.getWritableDatabase();
		createAllTables(database);
	}

	public void close() {
		dbHelper.close();
		System.gc();
	}
	
	
	public String getDriverServiceRestartOnReboot() {
		try {
			String[] columns = new String[] { Database2.DRIVER_SERVICE_RESTART_ON_REBOOT };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_SERVICE_RESTART_ON_REBOOT, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String choice = cursor.getString(cursor.getColumnIndex(Database2.DRIVER_SERVICE_RESTART_ON_REBOOT));
				return choice;
			} else {
				return "yes";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "yes";
		}
	}
	
	public void updateDriverServiceRestartOnReboot(String choice) {
		String[] columns = new String[] { Database2.DRIVER_SERVICE_RESTART_ON_REBOOT };
		Cursor cursor = database.query(Database2.TABLE_DRIVER_SERVICE_RESTART_ON_REBOOT, columns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			deleteDriverServiceRestartOnReboot();
			insertDriverServiceRestartOnReboot(choice);
		} else {
			insertDriverServiceRestartOnReboot(choice);
		}
	}
	
	public void insertDriverServiceRestartOnReboot(String choice){
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DRIVER_SERVICE_RESTART_ON_REBOOT, choice);
			database.insert(Database2.TABLE_DRIVER_SERVICE_RESTART_ON_REBOOT, null, contentValues);
		} catch(Exception e){
			Log.e("e","="+e);
		}
	}
	
	public void deleteDriverServiceRestartOnReboot(){
		try{
			database.delete(Database2.TABLE_DRIVER_SERVICE_RESTART_ON_REBOOT, null, null);
		} catch(Exception e){
			Log.e("e","="+e);
		}
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
	
	
	
	public String getJugnooOn() {
		try {
			String[] columns = new String[] { Database2.JUGNOO_ON };
			Cursor cursor = database.query(Database2.TABLE_JUGNOO_ON, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String choice = cursor.getString(cursor.getColumnIndex(Database2.JUGNOO_ON));
				return choice;
			} else {
				return "on";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "e="+e.toString();
		}
	}
	
	
	
	public void updateJugnooOn(String choice) {
		try {
			String[] columns = new String[] { Database2.JUGNOO_ON };
			Cursor cursor = database.query(Database2.TABLE_JUGNOO_ON, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				deleteJugnooOn();
				insertJugnooOn(choice);
			} else {
				insertJugnooOn(choice);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insertJugnooOn(String choice){
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.JUGNOO_ON, choice);
			database.insert(Database2.TABLE_JUGNOO_ON, null, contentValues);
		} catch(Exception e){
			Log.e("e","="+e);
		}
	}
	
	public void deleteJugnooOn(){
		try{
			database.delete(Database2.TABLE_JUGNOO_ON, null, null);
		} catch(Exception e){
			Log.e("e","="+e);
		}
	}
	
	
	
	
	public void insertDriverLocData(String accessToken, String deviceToken, String serverUrl){
		try{
			deleteDriverLocData();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DLD_ACCESS_TOKEN, accessToken);
			contentValues.put(Database2.DLD_DEVICE_TOKEN, deviceToken);
			contentValues.put(Database2.DLD_SERVER_URL, serverUrl);
			database.insert(Database2.TABLE_DRIVER_LOC_DATA, null, contentValues);
		} catch(Exception e){
			Log.e("e","="+e);
		}
	}
	
	public void deleteDriverLocData(){
		try{
			database.delete(Database2.TABLE_DRIVER_LOC_DATA, null, null);
		} catch(Exception e){
			Log.e("e","="+e);
		}
	}
	
	
	public String getDLDAccessToken() {
		String[] columns = new String[] { Database2.DLD_ACCESS_TOKEN };
		Cursor cursor = database.query(Database2.TABLE_DRIVER_LOC_DATA, columns, null, null, null, null, null);
		cursor.moveToFirst();
		String choice = cursor.getString(cursor.getColumnIndex(Database2.DLD_ACCESS_TOKEN));
		return choice;
	}
	
	public String getDLDDeviceToken() {
		String[] columns = new String[] { Database2.DLD_DEVICE_TOKEN };
		Cursor cursor = database.query(Database2.TABLE_DRIVER_LOC_DATA, columns, null, null, null, null, null);
		cursor.moveToFirst();
		String choice = cursor.getString(cursor.getColumnIndex(Database2.DLD_DEVICE_TOKEN));
		return choice;
	}
	
	public String getDLDServerUrl() {
		String[] columns = new String[] { Database2.DLD_SERVER_URL };
		Cursor cursor = database.query(Database2.TABLE_DRIVER_LOC_DATA, columns, null, null, null, null, null);
		cursor.moveToFirst();
		String choice = cursor.getString(cursor.getColumnIndex(Database2.DLD_SERVER_URL));
		return choice;
	}
	
}