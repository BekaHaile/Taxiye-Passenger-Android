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
public class Database {																	// class for handling database related activities

	private static final String DATABASE_NAME = "jugnoo_database";						// declaring database variables

	private static final int DATABASE_VERSION = 2;

	private DbHelper dbHelper;

	SQLiteDatabase database;

	// ***************** table_email_suggestions table columns
	// **********************//
	private static final String TABLE_EMAILS = "table_email_suggestions";
	private static final String EMAIL = "email";
	
	// ***************** table_previous_latlng table columns
		// **********************//
	private static final String TABLE_PREVIOUS_LATLNG = "table_previous_latlng";
	private static final String LAT = "lat";
	private static final String LNG = "lng";

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

			// table_email_suggestions
			database.execSQL(" CREATE TABLE " + TABLE_EMAILS + " ("
					+ EMAIL + " TEXT NOT NULL" + ");");

			// table_previous_latlng
			database.execSQL(" CREATE TABLE " + TABLE_PREVIOUS_LATLNG + " (" 
					+ LAT + " REAL NOT NULL" + ","
					+ LNG + " REAL NOT NULL" + ""
					+ ");");

		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			onCreate(database);
		}

	}

	public Database(Context context) {
		dbHelper = new DbHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
		System.gc();
	}


	/**
	 * Inserting login Email
	 * @param email String 
	 * @return
	 */
	public long insertEmail(String email) {																// insert login Email
		String[] columns = new String[] { Database.EMAIL };
		int count = 0;
		
		Cursor cursor1 = database
				.query(Database.TABLE_EMAILS, columns,
						Database.EMAIL + "=?", new String[] { email }, null,
						null, null);																	// checks if email is already added or not
		count = cursor1.getCount();
		if (count <= 0) {
		
		Cursor cursor = database.query(Database.TABLE_EMAILS, columns, null,
				null, null, null, null);
		count = cursor.getCount();
		int columnIndex = cursor.getColumnIndex(Database.EMAIL);
		if (count >= 20) {																							// else first deleting an email entry
			String emailToDelete = "";
			cursor.moveToFirst();
			emailToDelete = cursor.getString(columnIndex);
			deleteEmail(emailToDelete);
		}
		cursor.close();
		cursor1.close();
		
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database.EMAIL, email);

			return database.insert(Database.TABLE_EMAILS, null, contentValues);
		} else {																						// if already added email will not be added
			return 1;
		}

	}

	/**
	 * Function to get login email array
	 * @return string array containing email
	 */
	public String[] getEmails() {																	// get login Email array
		String[] columns = new String[] { Database.EMAIL };

		Cursor cursor = database.query(Database.TABLE_EMAILS, columns, null,
				null, null, null, null);

		String result = "";
		
		if (cursor.getCount() > 0) {																		// if there are more than one emails
			
			int emailColumnIndex = cursor.getColumnIndex(Database.EMAIL);
	
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				result = result + cursor.getString(emailColumnIndex) + "\n";
			}
	
			String[] emails = result.split("\n");

			return emails;
		} else {
			return null;
		}

	}

	/**
	 * delete an email entry 
	 * @param email email string to delete
	 */
	public void deleteEmail(String email) {															// delete an email entry
		database.delete(Database.TABLE_EMAILS, Database.EMAIL + "=?",
				new String[] { email });
	}

	
	
	
	public void insertLatLngArr(ArrayList<LatLng> latLngs){
		
		if(latLngs.size() > 0){
			
			ContentValues contentValues = new ContentValues();
			
			for(LatLng latLng : latLngs){
				contentValues.put(Database.LAT, latLng.latitude);
				contentValues.put(Database.LNG, latLng.longitude);
				database.insert(Database.TABLE_PREVIOUS_LATLNG, null, contentValues);
				contentValues.clear();
			}
			
		}
		
	}
	
	
	public ArrayList<LatLng> getSavedLatLngs(){
		
		ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
		
		String[] columns = new String[] { Database.LAT, Database.LNG };

		Cursor cursor = database.query(Database.TABLE_PREVIOUS_LATLNG, columns, null, null, null, null, null);
		
		int in0 = cursor.getColumnIndex(Database.LAT);
		int in1 = cursor.getColumnIndex(Database.LNG);
		
		if(latLngs.size() > 0){
			
			ContentValues contentValues = new ContentValues();
			
			for(LatLng latLng : latLngs){
				contentValues.put(Database.LAT, latLng.latitude);
				contentValues.put(Database.LNG, latLng.longitude);
				database.insert(Database.TABLE_PREVIOUS_LATLNG, null, contentValues);
				contentValues.clear();
			}
			
		}
		
	}
	
	
	
}