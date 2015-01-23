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
    public static final String MEAS_VOLUME = "MeasVolume";
    public static final String CALC_VOLUME = "CalcVolume";
    public static final String ANGLE = "Angle";
    public static final String RATIO = "Ratio";
    public static final String TURN_NUMBER = "TurnNumber";
    public static final String PAIR_LINK_QUALITY = "PairLinkQuality";
    public static final String TURN_COUNT_POS = "TurnCountPos";
    public static final String TURN_COUNT_NEG = "TurnCountNeg";
    public static final String TEMP_AIR = "TempAir";
    public static final String WATER_TEMPERATURE = "WaterTemperature";
    public static final String BATTERY_VOLTAGE = "BatteryVoltage";
    public static final String SUPPLY_VOLTAGE = "SupplyVoltage";
    public static final String CHARGER_VOLTAGE = "ChargerVoltage";
    public static final String Z_AXIS = "ZAxis";
    public static final String DRUM_STATE = "DrumState";
    public static final String TRUCK_ACTIVITY = "TruckActivity";
    public static final String MEASUREMENT_INDEX = "MeasurementIndex";
    public static final String LOG_QTY = "LogQty";
    public static final String ADDED_WATER = "AddedWater";
    public static final String RAW_DATA = "rawData";

    private static final String DATABASE_NAME = "ibb.logs.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_LOGS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_SPEED + " text not null, "
            + COLUMN_VISCOSITY + " text not null, "
            + COLUMN_SLUMP + " text not null, "
            + COLUMN_TEMPERATURE + " text not null, "
            + COLUMN_YIELD + " text not null, "
            + COLUMN_PRESSURE + " text not null, "
            + COLUMN_VOLUME + " text not null, "
            + COLUMN_TRUCK_NO + " text not null, "
            + COLUMN_TIMESTAMP + " text not null, "
            + COLUMN_STATUS + " text not null, "
            + MEAS_VOLUME + " text not null, "
            + CALC_VOLUME + " text not null, "
            + ANGLE + " text not null, "
            + RATIO + " text not null, "
            + TURN_NUMBER + " text not null, "
            + PAIR_LINK_QUALITY + " text not null, "
            + TURN_COUNT_POS + " text not null, "
            + TEMP_AIR + " text not null, "
            + TURN_COUNT_NEG + " text not null, "
            + WATER_TEMPERATURE + " text not null, "
            + BATTERY_VOLTAGE + " text not null, "
            + SUPPLY_VOLTAGE + " text not null, "
            + CHARGER_VOLTAGE + " text not null, "
            + Z_AXIS + " text not null, "
            + DRUM_STATE + " text not null, "
            + TRUCK_ACTIVITY + " text not null, "
            + MEASUREMENT_INDEX + " text not null, "
            + LOG_QTY + " text not null, "
            + ADDED_WATER + " text not null, "
            + RAW_DATA + " text not null "
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
