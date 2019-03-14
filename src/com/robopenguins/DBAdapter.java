package com.robopenguins;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class DBAdapter {

	int id = 0;
	public static final String KEY_ROWID = "_id";
	public static final String KEY_WATERING_PERIOD = "Period";
	public static final String KEY_NEXT_WATERING = "Next";
	public static final String KEY_LAST_CHECK = "Last";
	public static final String KEY_NAME = "Name";
	public static final String KEY_MOOD = "Mood";
	private static final String TAG = "DBAdapter";
	private static final String KEY_HOUR = "hour";
	private static final String KEY_MINUTE = "minute";
	private static final String KEY_NOTIFY = "notify";
	private static final String KEY_VIBRATE = "vibrate";
	private static final String KEY_SOUND = "sound";
	private static final String KEY_RING = "ring";
	
	
	private static final String DB_NAME = "Plants";
	private static final String ALARM_TABLE = "Alarms";
	private static final String PLANT_TABLE = "plantStatus";
	private static final int DATABASE_VERSION = 2;

	private static final String PLANT_TABLE_CREATE = "create table " + PLANT_TABLE
			+ " (" + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_WATERING_PERIOD + " integer, " + KEY_LAST_CHECK + " BIGINT, "
			+ KEY_NEXT_WATERING + " BIGINT, " + KEY_MOOD + " VARCHAR(32), "
			+ KEY_NAME + " VARCHAR(100) not null );" ;
			
	private static final String ALARM_TABLE_CREATE = "create table "
			+ ALARM_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_HOUR + " integer, "
			+ KEY_MINUTE + " integer, "+ KEY_NOTIFY + " bool, "+ KEY_VIBRATE 
			+ " bool, "+ KEY_SOUND + " bool, "+KEY_RING+  " VARCHAR(256) );";

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(PLANT_TABLE_CREATE);
			db.execSQL(ALARM_TABLE_CREATE);
		/*	
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_WATERING_PERIOD, 3);
			Date now = new Date(System.currentTimeMillis());
			initialValues.put(KEY_NEXT_WATERING,
					Utilities.SkipAheadNDays(now, -1).getTime());
			initialValues.put(KEY_MOOD, Plant.Mood.CONTENT.toString());
			initialValues.put(KEY_NAME, "test");
			initialValues.put(KEY_LAST_CHECK, 0);
			db.insert(PLANT_TABLE, null, initialValues);
		 */
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion );
			if(oldVersion<2)
			{
				db.execSQL("ALTER TABLE "+ALARM_TABLE+" ADD "+KEY_RING+" VARCHAR(256)");
			}
		}
	}

	// ---opens the database---
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	public long setAlarm(AlarmParams params) {
		
		Cursor cursor = db.rawQuery("SELECT * FROM " + ALARM_TABLE, null);

		ContentValues values = new ContentValues();
		values.put(KEY_HOUR, params.hour);
		values.put(KEY_MINUTE, params.min);
		values.put(KEY_NOTIFY, (params.notification)?1:0);
		values.put(KEY_VIBRATE, (params.vibrate)?1:0);
		values.put(KEY_SOUND, (params.sound)?1:0);
		if( params.ringtone!=null)
		{
			values.put(KEY_RING, params.ringtone.toString());
		}
		else
		{
			values.put(KEY_RING, "NONE");
		}

		if (cursor.moveToFirst()) {
			cursor.close();
			return db.update(ALARM_TABLE, values, "'1'", null);

		} else {
			cursor.close();
			return db.insert(ALARM_TABLE, null, values);
		}
	}

	public AlarmParams getAlarm() {
		Cursor cursor = db.rawQuery("SELECT * FROM " + ALARM_TABLE, null);

		if (cursor.moveToFirst()) {

			int hour = cursor.getInt(cursor.getColumnIndex(KEY_HOUR));

			int min = cursor.getInt(cursor.getColumnIndex(KEY_MINUTE));
			
			boolean vibrate = 0!=cursor.getInt(cursor.getColumnIndex(KEY_VIBRATE));
			
			boolean sound = 0!=cursor.getInt(cursor.getColumnIndex(KEY_SOUND));
			
			boolean notify = 0!=cursor.getInt(cursor.getColumnIndex(KEY_NOTIFY));
			
			String ring= cursor.getString(cursor.getColumnIndex(KEY_RING));
			
			Uri ringtone=null;
			if(ring!=null&&!ring.equals("NONE"))
			{
				ringtone=Uri.parse(ring );
			}
			
			cursor.close();
			return new AlarmParams(hour, min,notify,sound,vibrate,ringtone);
		}
		cursor.close();
		return null;
	}

	public long clearAlarm() {
		return db.delete(ALARM_TABLE, "'1'", null);
	}

	// ---insert a title into the database---
	public long insertPlant(Plant plant) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_WATERING_PERIOD, plant.getWateringPeriod());
		initialValues.put(KEY_NEXT_WATERING, plant.getNextWatering().getTime());
		initialValues.put(KEY_MOOD, plant.getMood().toString());
		initialValues.put(KEY_NAME, plant.getName());
		initialValues.put(KEY_LAST_CHECK, plant.getLastCheck().getTime());
		return db.insert(PLANT_TABLE, null, initialValues);
	}

	public long updatePlant(String name, Plant plant) {
		name = "'" + name + "'";
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_WATERING_PERIOD, plant.getWateringPeriod());
		initialValues.put(KEY_NEXT_WATERING, plant.getNextWatering().getTime());
		initialValues.put(KEY_LAST_CHECK, plant.getLastCheck().getTime());
		initialValues.put(KEY_MOOD, plant.getMood().toString());
		initialValues.put(KEY_NAME, plant.getName());
		return db.update(PLANT_TABLE, initialValues, KEY_NAME + "=" + name,
				null);
	}

	public long deletePlant(String name) {
		name = "'" + name + "'";
		return db.delete(PLANT_TABLE, KEY_NAME + "=" + name, null);
	}

	public boolean nameTaken(String name) {
		name = "'" + name + "'";
		Cursor cursor = db.rawQuery("SELECT " + KEY_ROWID + " FROM "
				+ PLANT_TABLE + " WHERE " + KEY_NAME + "=" + name, null);
		boolean taken=cursor.moveToFirst();
		cursor.close();
		return taken;

	}

	public Plant getEntry(String name) {
		name = "'" + name + "'";
		Plant plant = null;

		Cursor cursor = db.rawQuery("SELECT * FROM " + PLANT_TABLE + " WHERE "
				+ KEY_NAME + "=" + name, null);

		if (cursor.moveToFirst()) {
			plant = new Plant(cursor.getInt(cursor
					.getColumnIndex(KEY_WATERING_PERIOD)),
					cursor.getString(cursor.getColumnIndex(KEY_NAME)));
			plant.mood = Enum.valueOf(Plant.Mood.class,
					cursor.getString(cursor.getColumnIndex(KEY_MOOD)));
			plant.nextWatering = new Date(cursor.getLong(cursor
					.getColumnIndex(KEY_NEXT_WATERING)));
			plant.lastCheck = new Date(cursor.getLong(cursor
					.getColumnIndex(KEY_LAST_CHECK)));

		}
		cursor.close();
		return plant;

	}

	public ArrayList<Plant> getAllEntries() {
		Cursor cursor = db.rawQuery("SELECT * FROM " + PLANT_TABLE, null);
		int len = cursor.getCount();
		ArrayList<Plant> plants = new ArrayList<Plant>(len);

		for (int i = 0; i < len; i++) {
			cursor.moveToNext();

			Plant p = new Plant(cursor.getInt(cursor
					.getColumnIndex(KEY_WATERING_PERIOD)),
					cursor.getString(cursor.getColumnIndex(KEY_NAME)));
			p.mood = Enum.valueOf(Plant.Mood.class,
					cursor.getString(cursor.getColumnIndex(KEY_MOOD)));
			p.nextWatering = new Date(cursor.getLong(cursor
					.getColumnIndex(KEY_NEXT_WATERING)));
			p.lastCheck = new Date(cursor.getLong(cursor
					.getColumnIndex(KEY_LAST_CHECK)));
			plants.add(p);
		}
		cursor.close();
		return plants;

	}

}
