package product.clicklabs.jugnoo;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

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
	
	private static final String TABLE_USER_MODE = "table_user_mode";
	private static final String USER_MODE = "user_mode";
	public static final String UM_DRIVER = "driver";
	public static final String UM_PASSENGER = "passenger";
	
	
	private static final String TABLE_DRIVER_REQUESTS = "table_driver_requests";
	private static final String TABLE_DRIVER_REQUESTS_ENGAGEMENT_ID = "engagement_id";
	public static final String TABLE_DRIVER_REQUESTS_USER_ID = "user_id";
	public static final String TABLE_DRIVER_REQUESTS_LATITUDE = "latitude";
	public static final String TABLE_DRIVER_REQUESTS_LONGITUDE = "longitude";
	public static final String TABLE_DRIVER_REQUESTS_START_TIME = "start_time";
	public static final String TABLE_DRIVER_REQUESTS_END_TIME = "end_time";
	public static final String TABLE_DRIVER_REQUESTS_ADDRESS = "address";
	
	
	
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
		
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_USER_MODE + " ("
				+ USER_MODE + " TEXT" + ");");
		
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_REQUESTS + " ("
				+ TABLE_DRIVER_REQUESTS_ENGAGEMENT_ID + " TEXT, " 
				+ TABLE_DRIVER_REQUESTS_USER_ID + " TEXT, " 
				+ TABLE_DRIVER_REQUESTS_LATITUDE + " TEXT, " 
				+ TABLE_DRIVER_REQUESTS_LONGITUDE + " TEXT, " 
				+ TABLE_DRIVER_REQUESTS_START_TIME + " TEXT, " 
				+ TABLE_DRIVER_REQUESTS_END_TIME + " TEXT, " 
				+ TABLE_DRIVER_REQUESTS_ADDRESS + " TEXT" 
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
		deleteDriverServiceRestartOnReboot();
		insertDriverServiceRestartOnReboot(choice);
	}
	
	public void insertDriverServiceRestartOnReboot(String choice){
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DRIVER_SERVICE_RESTART_ON_REBOOT, choice);
			database.insert(Database2.TABLE_DRIVER_SERVICE_RESTART_ON_REBOOT, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void deleteDriverServiceRestartOnReboot(){
		try{
			database.delete(Database2.TABLE_DRIVER_SERVICE_RESTART_ON_REBOOT, null, null);
		} catch(Exception e){
			e.printStackTrace();
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
		deleteDriverServiceFast();
		insertDriverServiceFast(choice);
	}
	
	public void insertDriverServiceFast(String choice){
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.FAST, choice);
			database.insert(Database2.TABLE_DRIVER_SERVICE_FAST, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void deleteDriverServiceFast(){
		try{
			database.delete(Database2.TABLE_DRIVER_SERVICE_FAST, null, null);
		} catch(Exception e){
			e.printStackTrace();
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
			deleteJugnooOn();
			insertJugnooOn(choice);
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
			e.printStackTrace();
		}
	}
	
	public void deleteJugnooOn(){
		try{
			database.delete(Database2.TABLE_JUGNOO_ON, null, null);
		} catch(Exception e){
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}
	
	public void deleteDriverLocData(){
		try{
			database.delete(Database2.TABLE_DRIVER_LOC_DATA, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public String getDLDAccessToken() {
		try {
			String[] columns = new String[] { Database2.DLD_ACCESS_TOKEN };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_LOC_DATA, columns, null, null, null, null, null);
			cursor.moveToFirst();
			String choice = cursor.getString(cursor.getColumnIndex(Database2.DLD_ACCESS_TOKEN));
			return choice;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public String getDLDDeviceToken() {
		try {
			String[] columns = new String[] { Database2.DLD_DEVICE_TOKEN };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_LOC_DATA, columns, null, null, null, null, null);
			cursor.moveToFirst();
			String choice = cursor.getString(cursor.getColumnIndex(Database2.DLD_DEVICE_TOKEN));
			return choice;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public String getDLDServerUrl() {
		try {
			String[] columns = new String[] { Database2.DLD_SERVER_URL };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_LOC_DATA, columns, null, null, null, null, null);
			cursor.moveToFirst();
			String choice = cursor.getString(cursor.getColumnIndex(Database2.DLD_SERVER_URL));
			return choice;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
	
	
	
	
	
	public String getUserMode() {
		try {
			String[] columns = new String[] { Database2.USER_MODE };
			Cursor cursor = database.query(Database2.TABLE_USER_MODE, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String userMode = cursor.getString(cursor.getColumnIndex(Database2.USER_MODE));
				return userMode;
			} else {
				return Database2.UM_PASSENGER;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Database2.UM_PASSENGER;
		}
	}
	
	
	
	public void updateUserMode(String userMode) {
		try {
			deleteUserMode();
			insertUserMode(userMode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insertUserMode(String userMode){
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.USER_MODE, userMode);
			database.insert(Database2.TABLE_USER_MODE, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void deleteUserMode(){
		try{
			database.delete(Database2.TABLE_USER_MODE, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	public ArrayList<DriverRideRequest> getAllDriverRequests() {
		ArrayList<DriverRideRequest> arrayList = new ArrayList<DriverRideRequest>();
		try {
			String[] columns = new String[] { Database2.TABLE_DRIVER_REQUESTS_ENGAGEMENT_ID, Database2.TABLE_DRIVER_REQUESTS_USER_ID, 
					Database2.TABLE_DRIVER_REQUESTS_LATITUDE, Database2.TABLE_DRIVER_REQUESTS_LONGITUDE, 
					Database2.TABLE_DRIVER_REQUESTS_START_TIME, Database2.TABLE_DRIVER_REQUESTS_END_TIME, 
					Database2.TABLE_DRIVER_REQUESTS_ADDRESS };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_REQUESTS, columns, null, null, null, null, null);
			
			int in0 = cursor.getColumnIndex(Database2.TABLE_DRIVER_REQUESTS_ENGAGEMENT_ID);
			int in1 = cursor.getColumnIndex(Database2.TABLE_DRIVER_REQUESTS_USER_ID);
			int in2 = cursor.getColumnIndex(Database2.TABLE_DRIVER_REQUESTS_LATITUDE);
			int in3 = cursor.getColumnIndex(Database2.TABLE_DRIVER_REQUESTS_LONGITUDE);
			int in4 = cursor.getColumnIndex(Database2.TABLE_DRIVER_REQUESTS_START_TIME);
			int in5 = cursor.getColumnIndex(Database2.TABLE_DRIVER_REQUESTS_END_TIME);
			int in6 = cursor.getColumnIndex(Database2.TABLE_DRIVER_REQUESTS_ADDRESS);
			
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
				arrayList.add(new DriverRideRequest(cursor.getString(in0), 
						cursor.getString(in1), 
						new LatLng(Double.parseDouble(cursor.getString(in2)), Double.parseDouble(cursor.getString(in3))), 
						Long.parseLong(cursor.getString(in4)), 
						Long.parseLong(cursor.getString(in5)),
						cursor.getString(in6)));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrayList;
	}
	
	
	public void insertDriverRequest(String engagementId, String userId, String latitude, String longitude, String startTime, 
			String endTime, String address){
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.TABLE_DRIVER_REQUESTS_ENGAGEMENT_ID, engagementId);
			contentValues.put(Database2.TABLE_DRIVER_REQUESTS_USER_ID, userId);
			contentValues.put(Database2.TABLE_DRIVER_REQUESTS_LATITUDE, latitude);
			contentValues.put(Database2.TABLE_DRIVER_REQUESTS_LONGITUDE, longitude);
			contentValues.put(Database2.TABLE_DRIVER_REQUESTS_START_TIME, startTime);
			contentValues.put(Database2.TABLE_DRIVER_REQUESTS_END_TIME, endTime);
			contentValues.put(Database2.TABLE_DRIVER_REQUESTS_ADDRESS, address);
			database.insert(Database2.TABLE_DRIVER_REQUESTS, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public int deleteDriverRequest(String engagementId){
		try{
			return database.delete(Database2.TABLE_DRIVER_REQUESTS, Database2.TABLE_DRIVER_REQUESTS_ENGAGEMENT_ID + "=?", new String[]{engagementId});
		} catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	public void deleteAllDriverRequests(){
		try{
			database.delete(Database2.TABLE_DRIVER_REQUESTS, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
}