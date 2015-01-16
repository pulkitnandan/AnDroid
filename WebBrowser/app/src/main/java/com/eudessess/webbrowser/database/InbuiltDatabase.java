package com.eudessess.webbrowser.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by eudessess on 16-01-2015.
 */
public class InbuiltDatabase {

    // the Activity or Application that is creating an object from this class.
    Context context;
    // Database default properties
    private String DB_NAME = "DB_NAME";
    private int DB_VERSION = 1;
    private String TABLE_NAME = "DATA";
    private String TABLE_ROW_ONE = "NAME";
    private String TABLE_ROW_TWO = "VALUE";
    // a reference to the database used by this application/object
    private SQLiteDatabase database;

    public InbuiltDatabase(Context context, String databaseName) {
        this.context = context;
        this.DB_NAME = databaseName;

        // create or open the database
        CustomSQLiteOpenHelper helper;
        helper = new CustomSQLiteOpenHelper(context);
        this.database = helper.getWritableDatabase();
    }

    public void insertRow(String rowStringOne, String rowStringTwo)
            throws SQLiteException {

        // A key value pair holder used by android's SQLite functions
        ContentValues values = new ContentValues();
        values.put(TABLE_ROW_ONE, rowStringOne);
        values.put(TABLE_ROW_TWO, rowStringTwo);

        // Insert data
        try {
            database.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            Toast.makeText(this.context, "Failed to insert data - Retry : )",
                    Toast.LENGTH_SHORT).show();
        } finally {
            database.close();
        }
    }

    public Cursor fetchData() {
        Cursor c = database.rawQuery("SELECT title,url FROM " + TABLE_NAME, null);
        return c;
    }

    public void deleteData(String url) {
        String whereClause = TABLE_ROW_TWO + " = " + "'" + url + "'";
        database.delete(TABLE_NAME, whereClause, null);
    }

    public void deleteTable() {
        database.delete(TABLE_NAME, null, null);
    }

    public void close() {
        database.close();
    }


    private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
        public CustomSQLiteOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Create Table
            String newTableQueryString = "create table if not exists "
                    + TABLE_NAME + " (" + TABLE_ROW_ONE + " text,"
                    + TABLE_ROW_TWO + " text" + ");";
            db.execSQL(newTableQueryString);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

}
