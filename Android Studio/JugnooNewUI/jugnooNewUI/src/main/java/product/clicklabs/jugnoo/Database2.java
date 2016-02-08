package product.clicklabs.jugnoo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.datastructure.NotificationData;
import product.clicklabs.jugnoo.datastructure.PendingAPICall;
import product.clicklabs.jugnoo.datastructure.RidePath;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Prefs;
import product.clicklabs.jugnoo.utils.Utils;

/**
 * Handles database related work
 */
public class Database2 {                                                                    // class for handling database related activities

    private final String TAG = Database2.class.getSimpleName();

    private static Database2 dbInstance;

    private static final String DATABASE_NAME = "jugnoo_database2";                        // declaring database variables

    private static final int DATABASE_VERSION = 2;

    private DbHelper dbHelper;

    private SQLiteDatabase database;

    // Customer side ride info
    private static final String TABLE_RIDE_INFO = "table_ride_info";
    private static final String POSITION_ID = "position_id";
    private static final String SOURCE_LONGITUDE = "source_longitude";
    private static final String SOURCE_LATITUDE = "source_latitude";
    private static final String DESTINATION_LONGITUDE = "destination_latitude";
    private static final String DESTINATION_LATITUDE = "destination_longitude";



    private static final String TABLE_PENDING_API_CALLS = "table_pending_api_calls";
    private static final String API_ID = "api_id";
    private static final String API_URL = "api_url";
    private static final String API_REQUEST_PARAMS = "api_request_params";

    // Notification center table name and row names...
    private static final String TABLE_NOTIFICATION_CENTER = "table_notification_center";
    private static final String NOTIFICATION_ID = "notification_id";
    private static final String TIME_PUSH_ARRIVED = "time_push_arrived";
    private static final String MESSAGE = "message";
    private static final String DEEP_INDEX = "deep_index";
    private static final String TIME_TO_DISPLAY = "time_to_display";
    private static final String TIME_TILL_DISPLAY = "time_till_display";
    private static final String NOTIFICATION_IMAGE = "notification_image";




    private static final String TABLE_LINKS = "table_links";
    private static final String LINK = "link";
    private static final String LINK_TIME = "link_time";

    /**
     * Creates and opens database for the application use
     *
     * @author shankar
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


    private static void createAllTables(SQLiteDatabase database) {
        /****************************************** CREATING ALL THE TABLES *****************************************************/
        database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_RIDE_INFO + " ("
                + POSITION_ID + " INTEGER, "
                + SOURCE_LATITUDE + " REAL, "
                + SOURCE_LONGITUDE + " REAL, "
                + DESTINATION_LATITUDE + " REAL, "
                + DESTINATION_LONGITUDE + " REAL "
                + ");");


        database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_PENDING_API_CALLS + " ("
            + API_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + API_URL + " TEXT, "
            + API_REQUEST_PARAMS + " TEXT"
            + ");");


        database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION_CENTER + " ("
                + NOTIFICATION_ID + " INTEGER, "
                + TIME_PUSH_ARRIVED + " TEXT, "
                + MESSAGE + " TEXT, "
                + DEEP_INDEX + " TEXT, "
                + TIME_TO_DISPLAY + " TEXT, "
                + TIME_TILL_DISPLAY + " TEXT, "
                + NOTIFICATION_IMAGE + " TEXT"
                + ");");


        database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_LINKS + " ("
            + LINK + " TEXT, "
            + LINK_TIME + " TEXT"
            + ");");



    }

    private void dropAndCreateNotificationTable(SQLiteDatabase database, Context context){
        if(Prefs.with(context).getInt(Constants.FIRST_TIME_DB, 1) == 1) {
            ArrayList<NotificationData> notifications = getAllNotification();
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION_CENTER);
            database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION_CENTER + " ("
                    + NOTIFICATION_ID + " INTEGER, "
                    + TIME_PUSH_ARRIVED + " TEXT, "
                    + MESSAGE + " TEXT, "
                    + DEEP_INDEX + " TEXT, "
                    + TIME_TO_DISPLAY + " TEXT, "
                    + TIME_TILL_DISPLAY + " TEXT, "
                    + NOTIFICATION_IMAGE + " TEXT"
                    + ");");

            for(NotificationData data : notifications){
                insertNotification(context, data.getNotificationId(),
                        data.getTimePushArrived(),
                        data.getMessage(),
                        data.getDeepIndex(),
                        data.getTimeToDisplay(),
                        data.getTimeTillDisplay(),
                        data.getNotificationImage());
            }
            Prefs.with(context).save(Constants.FIRST_TIME_DB, 0);
        }
    }


    public static Database2 getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new Database2(context);
        } else if (!dbInstance.database.isOpen()) {
            dbInstance = null;
            dbInstance = new Database2(context);
        }
        dbInstance.dropAndCreateNotificationTable(dbInstance.database, context);
        return dbInstance;
    }

    private Database2(Context context) {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        createAllTables(database);
    }

    public void close() {
        database.close();
        dbHelper.close();
        System.gc();
    }

    public int getLastRowIdInRideInfo() {
        String[] columns = new String[]{POSITION_ID};
        Cursor cursor = database.query(TABLE_RIDE_INFO, columns, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            Log.d("ride_path_db_current_size", String.valueOf(cursor.getCount()));
            return cursor.getInt(cursor.getColumnIndex(POSITION_ID));
        } else {
            return 0;
        }
    }

    public void createRideInfoRecords(ArrayList<RidePath> ridePathArrayList) {
        for (int i = 0; i < ridePathArrayList.size(); i++){
            createRideInfoEntry(ridePathArrayList.get(i));
        }
    }




    public long createRideInfoEntry(RidePath ridePath) {

        ContentValues fields = new ContentValues();
        fields.put(POSITION_ID, ridePath.ridePathId);
        fields.put(SOURCE_LATITUDE, ridePath.sourceLatitude);
        fields.put(SOURCE_LONGITUDE, ridePath.sourceLongitude);
        fields.put(DESTINATION_LATITUDE, ridePath.destinationLatitude);
        fields.put(DESTINATION_LONGITUDE, ridePath.destinationLongitude);
        try {
            return database.insert(TABLE_RIDE_INFO, null, fields);
        } catch (Exception e) {
            return -1;
        }
    }

    public ArrayList<RidePath> getRidePathInfo(){
        ArrayList<RidePath> ridePaths = new ArrayList<>();
        try {
            String[] columns = new String[]{POSITION_ID, SOURCE_LATITUDE,SOURCE_LONGITUDE,
                    DESTINATION_LATITUDE, DESTINATION_LONGITUDE};
            Cursor cursor = database.query(TABLE_RIDE_INFO, columns, null, null, null, null, null);

            int iId = cursor.getColumnIndex(POSITION_ID);
            int iSrcLat = cursor.getColumnIndex(SOURCE_LATITUDE);
            int iSrcLong = cursor.getColumnIndex(SOURCE_LONGITUDE);
            int iDestLat = cursor.getColumnIndex(DESTINATION_LATITUDE);
            int iDestLong = cursor.getColumnIndex(DESTINATION_LONGITUDE);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ridePaths.add(new RidePath(cursor.getInt(iId),
                        cursor.getDouble(iSrcLat),
                        cursor.getDouble(iSrcLong),
                        cursor.getDouble(iDestLat),
                        cursor.getDouble(iDestLong)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ridePaths;
    }

    public void deleteRidePathTable(){
        try {
            database.delete(TABLE_RIDE_INFO, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public ArrayList<PendingAPICall> getAllPendingAPICalls() {
        ArrayList<PendingAPICall> pendingAPICalls = new ArrayList<PendingAPICall>();
        try {
            String[] columns = new String[] { API_ID, API_URL, API_REQUEST_PARAMS };
            Cursor cursor = database.query(TABLE_PENDING_API_CALLS, columns, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                int in0 = cursor.getColumnIndex(API_ID);
                int in1 = cursor.getColumnIndex(API_URL);
                int in2 = cursor.getColumnIndex(API_REQUEST_PARAMS);

                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                    try {
                        pendingAPICalls.add(new PendingAPICall(cursor.getInt(in0), cursor.getString(in1),
                            Utils.convertQueryToNameValuePairArr(cursor.getString(in2))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pendingAPICalls;
    }

    public int getAllPendingAPICallsCount() {
        try {
            String[] columns = new String[] { API_ID };
            Cursor cursor = database.query(TABLE_PENDING_API_CALLS, columns, null, null, null, null, null);
            return cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void insertPendingAPICall(Context context, String url, HashMap<String, String> requestParams) {
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(API_URL, url);
            contentValues.put(API_REQUEST_PARAMS, requestParams.toString());
            database.insert(TABLE_PENDING_API_CALLS, null, contentValues);
            checkStartPendingApisService(context);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public int deletePendingAPICall(int apiId){
        try{
            return database.delete(TABLE_PENDING_API_CALLS, API_ID + "=" + apiId, null);
        } catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public void checkStartPendingApisService(Context context){
        if(!Utils.isServiceRunning(context, PushPendingCallsService.class.getName())){
            context.startService(new Intent(context, PushPendingCallsService.class));
        }
    }







    public ArrayList<NotificationData> getAllNotification() {
        ArrayList<NotificationData> allNotification = new ArrayList<NotificationData>();
        try {
            String[] columns = new String[] { NOTIFICATION_ID, TIME_PUSH_ARRIVED, MESSAGE, DEEP_INDEX, TIME_TO_DISPLAY, TIME_TILL_DISPLAY, NOTIFICATION_IMAGE };
            Cursor cursor = database.query(TABLE_NOTIFICATION_CENTER, columns, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                int in0 = cursor.getColumnIndex(NOTIFICATION_ID);
                int in1 = cursor.getColumnIndex(TIME_PUSH_ARRIVED);
                int in2 = cursor.getColumnIndex(MESSAGE);
                int in3 = cursor.getColumnIndex(DEEP_INDEX);
                int in4 = cursor.getColumnIndex(TIME_TO_DISPLAY);
                int in5 = cursor.getColumnIndex(TIME_TILL_DISPLAY);
                int in6 = cursor.getColumnIndex(NOTIFICATION_IMAGE);

				long currentTimeLong = DateOperations.getMilliseconds(DateOperations.getCurrentTimeInUTC());

                for(cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()){
                    try {
                        long savedIn4 = 600000;
                        try{
                            savedIn4 = Long.parseLong(cursor.getString(in4));
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                        long pushArrAndTimeToDisVal = (savedIn4 + DateOperations.getMilliseconds(cursor.getString(in1)));

						boolean added = false;
                        if((!"0".equalsIgnoreCase(cursor.getString(in4))) && (!"".equalsIgnoreCase(cursor.getString(in5)))) { //if both values
                            if ((currentTimeLong < pushArrAndTimeToDisVal) &&
									(currentTimeLong < DateOperations.getMilliseconds(cursor.getString(in5)))) {
                                allNotification.add(new NotificationData(cursor.getInt(in0), cursor.getString(in1), cursor.getString(in2),
                                        cursor.getString(in3), cursor.getString(in4), cursor.getString(in5), cursor.getString(in6)));
								added = true;
                            }
                        }else if((!"0".equalsIgnoreCase(cursor.getString(in4))) && ("".equalsIgnoreCase(cursor.getString(in5)))){ // only timeToDisplay
                            if ((currentTimeLong < pushArrAndTimeToDisVal)) {
                                allNotification.add(new NotificationData(cursor.getInt(in0), cursor.getString(in1), cursor.getString(in2),
                                        cursor.getString(in3), cursor.getString(in4), cursor.getString(in5), cursor.getString(in6)));
								added = true;
                            }
                        }else if((!"".equalsIgnoreCase(cursor.getString(in5))) && ("0".equalsIgnoreCase(cursor.getString(in4)))){ //only timeTillDisplay
                            if (   (currentTimeLong < DateOperations.getMilliseconds(cursor.getString(in5)))) {
                                allNotification.add(new NotificationData(cursor.getInt(in0), cursor.getString(in1), cursor.getString(in2),
                                        cursor.getString(in3), cursor.getString(in4), cursor.getString(in5), cursor.getString(in6)));
								added = true;
                            }
                        }
						if(!added){
							deleteNotification(cursor.getInt(in0));
						}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allNotification;
    }

    public int getAllNotificationCount() {
        try {
            String[] columns = new String[] { NOTIFICATION_ID };
            Cursor cursor = database.query(TABLE_NOTIFICATION_CENTER, columns, null, null, null, null, null);
            return cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void insertNotification(Context context, int id, String timePushArrived, String message, String deepIndex, String timeToDisplay,
                                   String timeTillDisplay, String notificationImage) {
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(NOTIFICATION_ID, id);
            contentValues.put(TIME_PUSH_ARRIVED, timePushArrived);
            contentValues.put(MESSAGE, message);
            contentValues.put(DEEP_INDEX, deepIndex);
            contentValues.put(TIME_TO_DISPLAY, timeToDisplay);
            contentValues.put(TIME_TILL_DISPLAY, timeTillDisplay);
            contentValues.put(NOTIFICATION_IMAGE, notificationImage);
            database.insert(TABLE_NOTIFICATION_CENTER, null, contentValues);
            int rowCount = getAllNotificationCount();
            Log.i(TAG, "insertNotification rowCount=" + rowCount);
        } catch(Exception e){
            e.printStackTrace();
            dropAndCreateNotificationTable(database, context);
            insertNotification(context, id, timePushArrived, message, deepIndex, timeToDisplay, timeTillDisplay, notificationImage);
        }
    }

    public int deleteNotification(int notificationId){
        try{
            return database.delete(TABLE_NOTIFICATION_CENTER, NOTIFICATION_ID + "=" + notificationId, null);
        } catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteNotificationTable(){
        try{
            database.execSQL("delete from "+ TABLE_NOTIFICATION_CENTER);
        } catch(Exception e){
            e.printStackTrace();
        }

    }








    public void insertLink(String link) {
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(LINK, link);
            contentValues.put(LINK_TIME, ""+System.currentTimeMillis());
            database.insert(TABLE_LINKS, null, contentValues);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void deleteLinks(){
        try{
            database.execSQL("delete from " + TABLE_LINKS);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getSavedLinksUpToTime(long timeDiff){
        long currTime = System.currentTimeMillis();
        JSONArray linksArr = new JSONArray();
        try {
            String[] columns = new String[] { LINK, LINK_TIME };
            Cursor cursor = database.query(TABLE_LINKS, columns, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                int in0 = cursor.getColumnIndex(LINK);
                int in1 = cursor.getColumnIndex(LINK_TIME);

                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                    try {
                        long savedTime = Long.parseLong(cursor.getString(in1));
                        if(Math.abs(currTime - savedTime) <= timeDiff){
                            JSONObject jObj = new JSONObject();
                            jObj.put("link", cursor.getString(in0));
                            jObj.put("timestamp", cursor.getString(in1));
                            linksArr.put(jObj);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return linksArr.toString();
    }


}