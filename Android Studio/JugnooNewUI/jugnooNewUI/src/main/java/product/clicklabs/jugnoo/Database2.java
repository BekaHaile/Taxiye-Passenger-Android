package product.clicklabs.jugnoo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.datastructure.NotificationData;
import product.clicklabs.jugnoo.datastructure.PendingAPICall;
import product.clicklabs.jugnoo.datastructure.RidePath;
import product.clicklabs.jugnoo.support.models.ShowPanelResponse;
import product.clicklabs.jugnoo.t20.models.Schedule;
import product.clicklabs.jugnoo.t20.models.Selection;
import product.clicklabs.jugnoo.t20.models.T20DataType;
import product.clicklabs.jugnoo.t20.models.Team;
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
    private static final String PUSH_TITLE = "push_title";
    private static final String MESSAGE = "message";
    private static final String DEEP_INDEX = "deep_index";
    private static final String TIME_TO_DISPLAY = "time_to_display";
    private static final String TIME_TILL_DISPLAY = "time_till_display";
    private static final String NOTIFICATION_IMAGE = "notification_image";




    private static final String TABLE_LINKS = "table_links";
    private static final String LINK = "link";
    private static final String LINK_TIME = "link_time";



    private static final String TABLE_SUPPORT_DATA = "table_support_data";
    private static final String SUPPORT_CATEGORY = "support_category";
    private static final String SUPPORT_DATA = "support_data";


    private static final String TABLE_T20_DATA = "table_t20_data";
    private static final String T20_CATEGORY = "t20_category";
    private static final String T20_DATA = "t20_data";


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
                + NOTIFICATION_IMAGE + " TEXT, "
                + PUSH_TITLE + " TEXT"
                + ");");


        database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_LINKS + " ("
            + LINK + " TEXT, "
            + LINK_TIME + " TEXT"
            + ");");



        database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_SUPPORT_DATA + " ("
                + SUPPORT_CATEGORY + " INTEGER, "
                + SUPPORT_DATA + " TEXT"
                + ");");

        database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_T20_DATA + " ("
                + T20_CATEGORY + " INTEGER, "
                + T20_DATA + " TEXT"
                + ");");

    }

    private void dropAndCreateNotificationTable(SQLiteDatabase database, Context context) {
        try {
            if(!Prefs.with(context).contains(Constants.SECOND_TIME_DB)) {
				ArrayList<NotificationData> notifications = getAllNotificationOld();
				database.execSQL("DROP TABLE " + TABLE_NOTIFICATION_CENTER);
				database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION_CENTER + " ("
						+ NOTIFICATION_ID + " INTEGER, "
						+ TIME_PUSH_ARRIVED + " TEXT, "
						+ MESSAGE + " TEXT, "
						+ DEEP_INDEX + " TEXT, "
						+ TIME_TO_DISPLAY + " TEXT, "
						+ TIME_TILL_DISPLAY + " TEXT, "
						+ NOTIFICATION_IMAGE + " TEXT, "
						+ PUSH_TITLE + " TEXT"
						+ ");");

				for (int i = notifications.size()-1; i >= 0; i--) {
					NotificationData data = notifications.get(i);
					insertNotification(context, data.getNotificationId(),
							data.getTimePushArrived(),
							data.getTitle(),
							data.getMessage(),
							data.getDeepIndex(),
							data.getTimeToDisplay(),
							data.getTimeTillDisplay(),
							data.getNotificationImage());
				}
				Prefs.with(context).save(Constants.SECOND_TIME_DB, 1);
			}
        } catch (Exception e) {
            e.printStackTrace();
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
            String[] columns = new String[] { NOTIFICATION_ID, TIME_PUSH_ARRIVED, MESSAGE, DEEP_INDEX, TIME_TO_DISPLAY, TIME_TILL_DISPLAY, NOTIFICATION_IMAGE, PUSH_TITLE };
            Cursor cursor = database.query(TABLE_NOTIFICATION_CENTER, columns, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                int in0 = cursor.getColumnIndex(NOTIFICATION_ID);
                int in1 = cursor.getColumnIndex(TIME_PUSH_ARRIVED);
                int in2 = cursor.getColumnIndex(MESSAGE);
                int in3 = cursor.getColumnIndex(DEEP_INDEX);
                int in4 = cursor.getColumnIndex(TIME_TO_DISPLAY);
                int in5 = cursor.getColumnIndex(TIME_TILL_DISPLAY);
                int in6 = cursor.getColumnIndex(NOTIFICATION_IMAGE);
                int in7 = cursor.getColumnIndex(PUSH_TITLE);

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
                                allNotification.add(new NotificationData(cursor.getInt(in0), cursor.getString(in1), cursor.getString(in7),
                                        cursor.getString(in2),
                                        cursor.getString(in3), cursor.getString(in4), cursor.getString(in5), cursor.getString(in6)));
								added = true;
                            }
                        }else if((!"0".equalsIgnoreCase(cursor.getString(in4))) && ("".equalsIgnoreCase(cursor.getString(in5)))){ // only timeToDisplay
                            if ((currentTimeLong < pushArrAndTimeToDisVal)) {
                                allNotification.add(new NotificationData(cursor.getInt(in0), cursor.getString(in1), cursor.getString(in7),
                                        cursor.getString(in2),
                                        cursor.getString(in3), cursor.getString(in4), cursor.getString(in5), cursor.getString(in6)));
								added = true;
                            }
                        }else if((!"".equalsIgnoreCase(cursor.getString(in5))) && ("0".equalsIgnoreCase(cursor.getString(in4)))){ //only timeTillDisplay
                            if (   (currentTimeLong < DateOperations.getMilliseconds(cursor.getString(in5)))) {
                                allNotification.add(new NotificationData(cursor.getInt(in0), cursor.getString(in1), cursor.getString(in7),
                                        cursor.getString(in2),
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

    private ArrayList<NotificationData> getAllNotificationOld() {
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

                        String titleMessage = cursor.getString(in2);
                        Log.e(TAG, "titleMessage="+titleMessage);
                        String[] arr = titleMessage.split("\\\n");
                        String title = arr[0];
                        String message = titleMessage.substring((title+"\n").length());

                        boolean added = false;
                        if((!"0".equalsIgnoreCase(cursor.getString(in4))) && (!"".equalsIgnoreCase(cursor.getString(in5)))) { //if both values
                            if ((currentTimeLong < pushArrAndTimeToDisVal) &&
                                    (currentTimeLong < DateOperations.getMilliseconds(cursor.getString(in5)))) {
                                allNotification.add(new NotificationData(cursor.getInt(in0), cursor.getString(in1), title, message,
                                        cursor.getString(in3), cursor.getString(in4), cursor.getString(in5), cursor.getString(in6)));
                                added = true;
                            }
                        }else if((!"0".equalsIgnoreCase(cursor.getString(in4))) && ("".equalsIgnoreCase(cursor.getString(in5)))){ // only timeToDisplay
                            if ((currentTimeLong < pushArrAndTimeToDisVal)) {
                                allNotification.add(new NotificationData(cursor.getInt(in0), cursor.getString(in1), title, message,
                                        cursor.getString(in3), cursor.getString(in4), cursor.getString(in5), cursor.getString(in6)));
                                added = true;
                            }
                        }else if((!"".equalsIgnoreCase(cursor.getString(in5))) && ("0".equalsIgnoreCase(cursor.getString(in4)))){ //only timeTillDisplay
                            if (   (currentTimeLong < DateOperations.getMilliseconds(cursor.getString(in5)))) {
                                allNotification.add(new NotificationData(cursor.getInt(in0), cursor.getString(in1), title, message,
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

    public void insertNotification(Context context, int id, String timePushArrived, String title, String message, String deepIndex, String timeToDisplay,
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
            contentValues.put(PUSH_TITLE, title);
            database.insert(TABLE_NOTIFICATION_CENTER, null, contentValues);
        } catch (Exception e){
            e.printStackTrace();
            dropAndCreateNotificationTable(database, context);
            insertNotification(context, id, timePushArrived, title, message, deepIndex, timeToDisplay, timeTillDisplay, notificationImage);
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
            contentValues.put(LINK_TIME, "" + System.currentTimeMillis());
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








    private void insertSupportData(int category, String supportData) {
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(SUPPORT_CATEGORY, category);
            contentValues.put(SUPPORT_DATA, supportData);
            database.insert(TABLE_SUPPORT_DATA, null, contentValues);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void insertUpdateSupportData(int category, List<ShowPanelResponse.Item> supportData){
        try{
            Gson gson = new Gson();
            JsonElement element = gson.toJsonTree(supportData, new TypeToken<List<ShowPanelResponse.Item>>() {}.getType());

            if (!element.isJsonArray()) {
                throw new Exception();
            }

            JsonArray jsonArray = element.getAsJsonArray();

            ContentValues contentValues = new ContentValues();
            contentValues.put(SUPPORT_DATA, jsonArray.toString());
            int rowsAffected = database.update(TABLE_SUPPORT_DATA, contentValues, SUPPORT_CATEGORY + "=" + category, null);
            if(rowsAffected == 0){
                insertSupportData(category, jsonArray.toString());
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<ShowPanelResponse.Item> getSupportDataItems(int category){
        ArrayList<ShowPanelResponse.Item> menu = new ArrayList<>();
        try{
            String[] columns = new String[] { SUPPORT_DATA };
            Cursor cursor = database.query(TABLE_SUPPORT_DATA, columns, SUPPORT_CATEGORY + "=" + category,
                    null, null, null, null);

            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                String data = cursor.getString(cursor.getColumnIndex(SUPPORT_DATA));

                Gson gson = new Gson();
                menu = gson.fromJson(data, new TypeToken<List<ShowPanelResponse.Item>>(){}.getType());
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        return menu;
    }











    private void insertT20Data(int category, String t20Data) {
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(T20_CATEGORY, category);
            contentValues.put(T20_DATA, t20Data);
            database.insert(TABLE_T20_DATA, null, contentValues);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void insertUpdateT20Data(int category, List t20Data){
        try{
            Gson gson = new Gson();
            JsonElement element = null;
            if(T20DataType.SCHEDULE.getOrdinal() == category) {
                element = gson.toJsonTree(t20Data, new TypeToken<List<Schedule>>() {}.getType());
            } else if(T20DataType.SELECTION.getOrdinal() == category){
                element = gson.toJsonTree(t20Data, new TypeToken<List<Selection>>() {}.getType());
            } else if(T20DataType.TEAM.getOrdinal() == category){
                element = gson.toJsonTree(t20Data, new TypeToken<List<Team>>() {}.getType());
            } else{
                return;
            }

            if (element == null || !element.isJsonArray()) {
                throw new Exception();
            }

            JsonArray jsonArray = element.getAsJsonArray();

            ContentValues contentValues = new ContentValues();
            contentValues.put(T20_DATA, jsonArray.toString());
            int rowsAffected = database.update(TABLE_T20_DATA, contentValues, T20_CATEGORY + "=" + category, null);
            if(rowsAffected == 0){
                insertT20Data(category, jsonArray.toString());
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList getT20DataItems(int category){
        ArrayList dataList = new ArrayList();
        try{
            String[] columns = new String[] { T20_DATA };
            Cursor cursor = database.query(TABLE_T20_DATA, columns, T20_CATEGORY + "=" + category,
                    null, null, null, null);

            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                String data = cursor.getString(cursor.getColumnIndex(T20_DATA));

                Gson gson = new Gson();

                if(T20DataType.SCHEDULE.getOrdinal() == category) {
                    dataList = gson.fromJson(data, new TypeToken<List<Schedule>>() {}.getType());
                } else if(T20DataType.SELECTION.getOrdinal() == category){
                    dataList = gson.fromJson(data, new TypeToken<List<Selection>>() {}.getType());
                } else if(T20DataType.TEAM.getOrdinal() == category){
                    dataList = gson.fromJson(data, new TypeToken<List<Team>>() {}.getType());
                } else{
                    throw new Exception();
                }
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        return dataList;
    }


}