package com.example.coordinates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "location_info.db";

    static final String TABLE_NAME_1 = "original_coordinates_table";
    static final String TABLE_NAME_2 = "final_coordinates_table";

    static final String TABLE_NAME_3 = "original_coordinates_table_copied";

    static final String COL_1_1 = "ID";
    static final String COL_2_1 = "LATITUDE";
    static final String COL_3_1 = "LONGITUDE";
    static final String COL_4_1 = "SPEED";
    static final String COL_5_1 = "ALTITUDE";
    static final String COL_6_1 = "DATETIME";

    static final String COL_1_2 = "ID";
    static final String COL_2_2 = "LATITUDE";
    static final String COL_3_2 = "LONGITUDE";
    static final String COL_4_2 = "SPEED";
    static final String COL_5_2 = "ALTITUDE";
    static final String COL_6_2 = "DATETIME";
    static final String COL_7_2 = "TIME_SPENT";
    static final String COL_8_2 = "LISTVIEW_LOCATION";
    static final String COL_9_2 = "LOCATION_NAME";

    private static final String COL_1_3 = "ID";
    private static final String COL_2_3 = "LATITUDE";
    private static final String COL_3_3 = "LONGITUDE";
    private static final String COL_4_3 = "SPEED";
    private static final String COL_5_3 = "ALTITUDE";
    private static final String COL_6_3 = "DATETIME";


    private String databasePath;

    DatabaseHelper(@Nullable Context context) {

        super(context, DATABASE_NAME, null, 1);

        databasePath = context.getDatabasePath(DATABASE_NAME).toString();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME_1 + " (" + COL_1_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2_1 + " TEXT, " + COL_3_1 + " TEXT, " + COL_4_1 + " TEXT, " + COL_5_1 + " TEXT, " + COL_6_1 + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_NAME_2 + " (" + COL_1_2 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2_2 + " TEXT, " + COL_3_2 + " TEXT, " + COL_4_2 + " TEXT, " + COL_5_2 + " TEXT, " + COL_6_2 + " TEXT, " + COL_7_2 + " TEXT, " + COL_8_2 + " INTEGER, " + COL_9_2 + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_NAME_3 + " (" + COL_1_3 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2_3 + " TEXT, " + COL_3_3 + " TEXT, " + COL_4_3 + " TEXT, " + COL_5_3 + " TEXT, " + COL_6_3 + " TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_3);

        onCreate(db);

    }

    boolean insertData(String tableName, String coordinates_latitude, String coordinates_longitude, String coordinates_speed, String coordinates_altitude, String coordinates_dateTime, String coordinates_timeSpent, int listeviewLocation, String locationName) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(tableName.equals(TABLE_NAME_1)) {

            contentValues.put(COL_2_1, coordinates_latitude);
            contentValues.put(COL_3_1, coordinates_longitude);
            contentValues.put(COL_4_1, coordinates_speed);
            contentValues.put(COL_5_1, coordinates_altitude);
            contentValues.put(COL_6_1, coordinates_dateTime);

        }
        else {

            contentValues.put(COL_2_2, coordinates_latitude);
            contentValues.put(COL_3_2, coordinates_longitude);
            contentValues.put(COL_4_2, coordinates_speed);
            contentValues.put(COL_5_2, coordinates_altitude);
            contentValues.put(COL_6_2, coordinates_dateTime);
            contentValues.put(COL_7_2, coordinates_timeSpent);
            contentValues.put(COL_8_2, listeviewLocation);
            contentValues.put(COL_9_2, locationName);

        }

        long result = db.insert(tableName, null, contentValues);

        return result != -1; //If result is -1 return false, else return true

    }

    public int updateLocationName(int id, String locationName) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_9_2, locationName);

        return db.update(TABLE_NAME_2, contentValues, COL_8_2 + "=" + id, null);

    }

    public int updateLocationPosition(int locationPosition, int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_8_2, locationPosition);

        return db.update(TABLE_NAME_2, contentValues, COL_1_2 + "=" + id, null);

    }

    Integer deleteDataByRowID(String tableName, int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        if(tableName.equals(TABLE_NAME_1))
            return db.delete(tableName, COL_1_1 + "=" + id, null);
        else if(tableName.equals(TABLE_NAME_2))
            return db.delete(tableName, COL_1_2 + "=" + id, null);
        else
            return db.delete(tableName, COL_1_3 + "=" + id, null);

    }

    Integer deleteDataByRowLocationID(int locationID) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME_2, COL_8_2 + "=" + locationID, null);

    }

    Integer deleteData(String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(tableName, "1", null);

    }

    Integer deleteLastRow(String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();

        if(tableName.equals(TABLE_NAME_1))
            return db.delete(tableName, COL_1_1 + " = (SELECT MAX(" + COL_1_1 + ") FROM " + tableName + ")", null);
        else if(tableName.equals(TABLE_NAME_2))
            return db.delete(tableName, COL_1_2 + " = (SELECT MAX(" + COL_1_2 + ") FROM " + tableName + ")", null);
        else
            return db.delete(tableName, COL_1_3 + " = (SELECT MAX(" + COL_1_2 + ") FROM " + tableName + ")", null);


    }

    Cursor getAllData(String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("SELECT * FROM " + tableName, null);

    }

    Cursor getDataByLocation(int listViewLocation) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("SELECT * FROM " + TABLE_NAME_2 + " WHERE " + COL_8_2 + "=" + listViewLocation, null);

    }

    Cursor getLastRecord(String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();

        if(tableName.equals(TABLE_NAME_1))
            return db.rawQuery("SELECT * FROM " + tableName + " ORDER BY " + COL_1_1 + " DESC LIMIT 1", null);
        else
            return db.rawQuery("SELECT * FROM " + tableName + " ORDER BY " + COL_1_2 + " DESC LIMIT 1", null);

    }

    Cursor getSecondToLastRecord(String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();

        if(tableName.equals(TABLE_NAME_1))
            return db.rawQuery("SELECT * FROM " + tableName + " ORDER BY " + COL_1_1 + " DESC LIMIT 2", null);
        else
            return db.rawQuery("SELECT * FROM " + tableName + " ORDER BY " + COL_1_2 + " DESC LIMIT 2", null);

    }

    void duplicateDatabase(String destinationTable, String sourceTable) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(destinationTable, "1", null);

        db.execSQL("INSERT INTO " + destinationTable + " SELECT * FROM " + sourceTable);

    }

    void saveDatabaseToSDCard() {

        try {

            Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            Boolean isSDSupportedDevice = Environment.isExternalStorageRemovable();

            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if(isSDPresent && isSDSupportedDevice) {

                if (sd.canWrite()) {
                    String currentDBPath = databasePath;
                    String backupDBPath = DATABASE_NAME;
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();

                        Log.d("Database", "Saved to SD Card");

                    }
                }
            }

        } catch (Exception e) {
        }

    }

}