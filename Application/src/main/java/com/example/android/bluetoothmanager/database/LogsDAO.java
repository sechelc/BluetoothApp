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
    private String[] allColumns = { MySqlLiteHelper.COLUMN_ID,
            MySqlLiteHelper.COLUMN_PRESSURE };

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
        //todo add all columns
        long insertId = database.insert(MySqlLiteHelper.TABLE_LOGS, null,
                values);
        Cursor cursor = database.query(MySqlLiteHelper.TABLE_LOGS,
                allColumns, MySqlLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Entry newEntry = cursorToComment(cursor);
        cursor.close();
        return newEntry;
    }

    public void deleteEntry(Entry comment) {
        long id = comment.getId();
        System.out.println("Entry deleted with id: " + id);
        database.delete(MySqlLiteHelper.TABLE_LOGS, MySqlLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Entry> getAllComments() {
        List<Entry> comments = new ArrayList<>();

        Cursor cursor = database.query(MySqlLiteHelper.TABLE_LOGS,
                allColumns, null, null, null, null, null);

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
        entry.setPressure(cursor.getString(1));
        return entry;
    }
}
