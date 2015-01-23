package com.example.android.bluetoothmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.bluetoothmanager.model.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SechelC on 1/20/2015.
 */
public class LogsDAO {
    // Database fields
    private SQLiteDatabase database;
    private MySqlLiteHelper dbHelper;
    private String[] allColumns = {MySqlLiteHelper.COLUMN_ID,
            MySqlLiteHelper.COLUMN_PRESSURE};

    public LogsDAO(Context context) {
        dbHelper = new MySqlLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Entry createEntry(Entry entry) {
        ContentValues values = new ContentValues();
        values.put(MySqlLiteHelper.COLUMN_PRESSURE, entry.getPressure());
        values.put(MySqlLiteHelper.COLUMN_SLUMP, entry.getSlump());
        values.put(MySqlLiteHelper.COLUMN_SPEED, entry.getSpeed());
        values.put(MySqlLiteHelper.COLUMN_TEMPERATURE, entry.getTempProbe());
        values.put(MySqlLiteHelper.COLUMN_TRUCK_NO, entry.getTruckNo());
        values.put(MySqlLiteHelper.COLUMN_VISCOSITY, entry.getViscosity());
        values.put(MySqlLiteHelper.COLUMN_VOLUME, entry.getVolume());
        values.put(MySqlLiteHelper.COLUMN_YIELD, entry.getYield());
        values.put(MySqlLiteHelper.COLUMN_TIMESTAMP, System.currentTimeMillis());
        values.put(MySqlLiteHelper.COLUMN_STATUS, "false");
        values.put(MySqlLiteHelper.MEAS_VOLUME, entry.getMeasVolume());
        values.put(MySqlLiteHelper.CALC_VOLUME, entry.getCalcVolume());
        values.put(MySqlLiteHelper.ANGLE, entry.getAngle());
        values.put(MySqlLiteHelper.RATIO, entry.getRatio());
        values.put(MySqlLiteHelper.TURN_NUMBER, entry.getTurnNumber());
        values.put(MySqlLiteHelper.PAIR_LINK_QUALITY, entry.getPairLinkQuality());
        values.put(MySqlLiteHelper.TURN_COUNT_POS, entry.getTurnCountPos());
        values.put(MySqlLiteHelper.TURN_COUNT_NEG, entry.getTurnCountNeg());
        values.put(MySqlLiteHelper.TEMP_AIR, entry.getTempAir());
        values.put(MySqlLiteHelper.WATER_TEMPERATURE, entry.getWaterTemperature());
        values.put(MySqlLiteHelper.BATTERY_VOLTAGE, entry.getBatteryVoltage());
        values.put(MySqlLiteHelper.SUPPLY_VOLTAGE, entry.getSupplyVoltage());
        values.put(MySqlLiteHelper.CHARGER_VOLTAGE, entry.getChargerVoltage());
        values.put(MySqlLiteHelper.Z_AXIS, entry.getzAxis());
        values.put(MySqlLiteHelper.DRUM_STATE, entry.getDrumState());
        values.put(MySqlLiteHelper.TRUCK_ACTIVITY, entry.getTruckActivity());
        values.put(MySqlLiteHelper.MEASUREMENT_INDEX, entry.getMeasurementIndex());
        values.put(MySqlLiteHelper.LOG_QTY, entry.getLogQty());
        values.put(MySqlLiteHelper.ADDED_WATER, entry.getAddedWater());
        values.put(MySqlLiteHelper.RAW_DATA, entry.getRawData());
        long insertId = database.insert(MySqlLiteHelper.TABLE_LOGS, null,
                values);
        Cursor cursor = database.query(MySqlLiteHelper.TABLE_LOGS,
                null, MySqlLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Entry newEntry = cursorToComment(cursor);
        cursor.close();
        return newEntry;
    }

    public void updateStatus(int id){
        ContentValues values = new ContentValues();
        values.put(MySqlLiteHelper.COLUMN_STATUS, "true");
        database.update(MySqlLiteHelper.TABLE_LOGS, values, "_id=", new String[]{String.valueOf(id)});
    }

    public void deleteEntry(Entry comment) {
        long id = comment.getId();
        System.out.println("Entry deleted with id: " + id);
        database.delete(MySqlLiteHelper.TABLE_LOGS, MySqlLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Entry> getAllEntries() {
        List<Entry> comments = new ArrayList<>();

        Cursor cursor = database.query(MySqlLiteHelper.TABLE_LOGS,
                null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Entry comment = cursorToComment(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    public List<Entry> getAllUnsent() {
        List<Entry> comments = new ArrayList<>();

        Cursor cursor = database.query(MySqlLiteHelper.TABLE_LOGS,
                null, "status=?", new String[]{"false"}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Entry comment = cursorToComment(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    private Entry cursorToComment(Cursor cursor) {
        Entry entry = new Entry();
        entry.setId(cursor.getLong(0));
        entry.setSpeed(cursor.getString(1));
        entry.setViscosity(cursor.getString(2));
        entry.setSlump(cursor.getString(3));
        entry.setTempProbe(cursor.getString(4));
        entry.setYield(cursor.getString(5));
        entry.setPressure(cursor.getString(6));
        entry.setVolume(cursor.getString(7));
        entry.setTruckNo(cursor.getString(8));
        entry.setTimestamp(cursor.getString(9));
        entry.setStatus(cursor.getString(10));
        entry.setMeasVolume(cursor.getString(11));
        entry.setCalcVolume(cursor.getString(12));
        entry.setAngle(cursor.getString(13));
        entry.setRatio(cursor.getString(14));
        entry.setTurnNumber(cursor.getString(15));
        entry.setPairLinkQuality(cursor.getString(16));
        entry.setTurnCountPos(cursor.getString(17));
        entry.setTempAir(cursor.getString(18));
        entry.setTurnCountNeg(cursor.getString(19));
        entry.setWaterTemperature(cursor.getString(20));
        entry.setBatteryVoltage(cursor.getString(21));
        entry.setSupplyVoltage(cursor.getString(22));
        entry.setChargerVoltage(cursor.getString(23));
        entry.setzAxis(cursor.getString(24));
        entry.setDrumState(cursor.getString(25));
        entry.setTruckActivity(cursor.getString(26));
        entry.setMeasurementIndex(cursor.getString(27));
        entry.setLogQty(cursor.getString(28));
        entry.setAddedWater(cursor.getString(29));
        entry.setRawData(cursor.getString(30));
        return entry;
    }
}
