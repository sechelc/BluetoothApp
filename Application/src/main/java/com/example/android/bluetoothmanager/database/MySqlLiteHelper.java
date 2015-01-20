package com.example.android.bluetoothmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by SechelC on 1/20/2015.
 */
public class MySqlLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_LOGS = "logs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SPEED = "speed";
    public static final String COLUMN_VISCOSITY = "viscosity";
    public static final String COLUMN_SLUMP = "slump";
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_YIELD = "yield";
    public static final String COLUMN_PRESSURE = "pressure";
    public static final String COLUMN_VOLUME = "volume";
    public static final String COLUMN_TRUCK_NO = "truck_no";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_STATUS = "status";

    private static final String DATABASE_NAME = "ibb.logs.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_LOGS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_SPEED + " text not null"
            + COLUMN_VISCOSITY + " text not null "
            + COLUMN_SLUMP + " text not null "
            + COLUMN_TEMPERATURE + " text not null "
            + COLUMN_YIELD + " text not null "
            + COLUMN_PRESSURE + " text not null "
            + COLUMN_VOLUME + " text not null "
            + COLUMN_TRUCK_NO + " text not null "
            + COLUMN_TIMESTAMP + " text not null "
            + COLUMN_STATUS + " text not null "
            + ");";

    public MySqlLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySqlLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        onCreate(db);
    }
}
