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

    private static final String DATABASE_NAME = "Coordinates.db";
    private static final String TABLE_NAME = "coordinates_table";

    static final String COL_1 = "ID";
    static final String COL_2 = "LATITUDE";
    static final String COL_3 = "LONGITUDE";

    public String databasePath;


    DatabaseHelper(@Nullable Context context) {

        super(context, DATABASE_NAME, null, 1);

        databasePath = context.getDatabasePath(DATABASE_NAME).toString();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2 + " TEXT, " + COL_3 + " TEXT) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);

    }

    boolean insertData(String coordinates_latitude, String coordinates_longitude) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_2, coordinates_latitude);
        contentValues.put(COL_3, coordinates_longitude);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1; //If result is -1 return false, else return true

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

    Integer deleteData() {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, "1", null);

    }

    Integer deleteLastRow() {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, COL_1 + " = (SELECT MAX(" + COL_1 + ") FROM " + TABLE_NAME + ")", null);

    }

    Cursor getAllData() {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

    }

    Cursor getLastRecord() {

        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_1 + " DESC LIMIT 1", null);

    }

    Cursor getSecondToLastRecord() {

        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_1 + " DESC LIMIT 2", null);

    }

}