package com.example.paabooking;

import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAppointmentsAdapter {
	static final String KEY_APPOINTMENTID = "_id";
	static final String KEY_DATE_DAY = "dateDay";
	static final String KEY_DATE_MONTH = "dateMonth";
	static final String KEY_DATE_YEAR = "dateYear";
	static final String KEY_TIME = "time";
	static final String KEY_PAAID = "paaId";
	static final String KEY_STUDENT_USERNAME = "studentUsername";
	static final String KEY_STUDENT_ID = "studentId";
	static final String KEY_STUDENT_FULLNAME = "studentFullName";
	static final String KEY_CANCELLED = "cancelled";
	static final String KEY_CANCELLEDBY = "cancelledBy";
	static final String TAG = "DBAppointmentsAdapter";
	
	static final String DATABASE_NAME = "AppointmentsDB";
	static final String DATABASE_TABLE = "appointments";
	
	static final int DATABASE_VERSION = 7;
	
	/*static final String DATABASE_CREATE ="create table appointments (_id integer primary key autoincrement, "
										 + "date text not null, time text not null, paaId text not null, studentId, not null, studentFullName not null);";*/
	static final String DATABASE_CREATE = "create table " 
										  + DATABASE_TABLE 
										  + "("
										  + KEY_APPOINTMENTID
										  + " integer primary key autoincrement, "
										  + KEY_DATE_DAY
										  + " text not null, "
										  + KEY_DATE_MONTH
										  + " text not null, "
										  + KEY_DATE_YEAR
										  + " text not null, "
										  + KEY_TIME
										  + " text not null, "
										  + KEY_PAAID
										  + " text not null, "
										  + KEY_STUDENT_USERNAME
										  + " text not null, "
										  + KEY_STUDENT_ID
										  + " text not null, "
										  + KEY_STUDENT_FULLNAME
										  + " text not null, "
										  + KEY_CANCELLED
										  + " text not null, "
										  + KEY_CANCELLEDBY 
										  + " text not null);";
	
	final Context context;
	
	DatabaseHelper DBHelper;
	SQLiteDatabase db;
	
	public DBAppointmentsAdapter(Context context) {
		this.context = context;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d("Database", "Creating database.");
			db.execSQL(DATABASE_CREATE);
			
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d("Database", "Upgrading the database version from: " 
		         + oldVersion 
		         + " to new version: "
		         + newVersion);
			db.execSQL("DROP TABLE IF EXISTS " 
		              + DATABASE_TABLE);
			onCreate(db);
		}
	} // End of DatabaseHelper class
	
	// Open the database connection
	public DBAppointmentsAdapter open() throws SQLException {
		Log.d("Database", "Openning connection.");
		db = DBHelper.getWritableDatabase();
		Log.d("Database", "obtained writeable database");
		return this;
	}
	
	// Close the database connection
	public void close() {
		DBHelper.close();
	}
	
	// Insert an appointment into the database
	public long insertAppointment(String dateDay, String dateMonth, String dateYear, String time, String paaId, String studentUsername, String studentId, String studentFullName, String cancelled, String cancelledBy) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DATE_DAY, dateDay);
		initialValues.put(KEY_DATE_MONTH, dateMonth);
		initialValues.put(KEY_DATE_YEAR, dateYear);
		initialValues.put(KEY_TIME, time);
		initialValues.put(KEY_PAAID, paaId);
		initialValues.put(KEY_STUDENT_USERNAME, studentUsername);
		initialValues.put(KEY_STUDENT_ID, studentId);
		initialValues.put(KEY_STUDENT_FULLNAME, studentFullName);
		initialValues.put(KEY_CANCELLED, cancelled);
		initialValues.put(KEY_CANCELLEDBY, cancelledBy);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}
	
	// Delete an appointment
	public boolean deleteAppointment(long appointmentId) {
		return db.delete(DATABASE_TABLE, KEY_APPOINTMENTID + "=" + appointmentId, null) > 0;
	}
	
	public Cursor getAllAppointments() {
		return db.query(DATABASE_TABLE, new String[] {KEY_APPOINTMENTID, 
				                                      KEY_DATE_DAY,
				                                      KEY_DATE_MONTH,
				                                      KEY_DATE_YEAR,
				                                      KEY_TIME, 
				                                      KEY_PAAID, 
				                                      KEY_STUDENT_USERNAME, 
				                                      KEY_STUDENT_ID, 
				                                      KEY_STUDENT_FULLNAME, 
				                                      KEY_CANCELLED, 
				                                      KEY_CANCELLEDBY}, null, null, null, null, null);
	}
	
	// Get a particular appointment
	public Cursor getAppointment(long appointmentId) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_APPOINTMENTID, 
													  KEY_DATE_DAY,
													  KEY_DATE_MONTH,
													  KEY_DATE_YEAR,
									                  KEY_TIME, 
									                  KEY_PAAID, 
									                  KEY_STUDENT_USERNAME, 
									                  KEY_STUDENT_ID, 
									                  KEY_STUDENT_FULLNAME, 
									                  KEY_CANCELLED, 
				                                      KEY_CANCELLEDBY}, 
				                                      KEY_APPOINTMENTID + "=" + appointmentId,
				                                      null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	// Get an appointment based on date and time
	public Cursor getAppointmentByDateAndTime(String dateDay, String dateMonth, String dateYear, String time) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_APPOINTMENTID, 
													  KEY_DATE_DAY,
													  KEY_DATE_MONTH,
													  KEY_DATE_YEAR,
									                  KEY_TIME, 
									                  KEY_PAAID, 
									                  KEY_STUDENT_USERNAME, 
									                  KEY_STUDENT_ID, 
									                  KEY_STUDENT_FULLNAME, 
									                  KEY_CANCELLED,  
				                                      KEY_CANCELLEDBY}, 
				                                      KEY_DATE_DAY + "=" + dateDay + " AND " + KEY_DATE_MONTH + "=" + dateMonth + " AND " + KEY_DATE_YEAR + "=" + dateYear + " AND " + KEY_TIME + "=" + time,
				                                      null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	// Get an appointment based on the student username
	public Cursor getAppointmentByStudentUsername(String studentUsername) {
		Cursor mCursor = null;
		try {
		mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_APPOINTMENTID, 
				KEY_DATE_DAY,
				KEY_DATE_MONTH,
				KEY_DATE_YEAR,
                KEY_TIME, 
                KEY_PAAID, 
                KEY_STUDENT_USERNAME, 
                KEY_STUDENT_ID, 
                KEY_STUDENT_FULLNAME, 
                KEY_CANCELLED,  
                KEY_CANCELLEDBY}, 
                KEY_STUDENT_USERNAME + "=" + studentUsername,
                null, null, null, null, null);
		} catch (SQLiteException e) {
			Log.d("Database", "Catched SQLite exception: " + e.getMessage());
		}
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	// Update appointment
	public boolean updateAppointment(long appointmentId, String dateDay, String dateMonth, String dateYear, String time, String paaId, String studentUsername, String studentId, String studentFullName, String cancelled, String cancelledBy) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(KEY_DATE_DAY, dateDay);
		updateValues.put(KEY_DATE_MONTH, dateMonth);
		updateValues.put(KEY_DATE_YEAR, dateYear);
		updateValues.put(KEY_TIME, time);
		updateValues.put(KEY_PAAID, paaId);
		updateValues.put(KEY_STUDENT_USERNAME, studentUsername);
		updateValues.put(KEY_STUDENT_ID, studentId);
		updateValues.put(KEY_STUDENT_FULLNAME, studentFullName);
		updateValues.put(KEY_CANCELLED, cancelled);
		updateValues.put(KEY_CANCELLEDBY, cancelledBy);
		return db.update(DATABASE_TABLE, updateValues, KEY_APPOINTMENTID + "=" + appointmentId, null) > 0;
		
	}
 }
