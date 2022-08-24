package com.example.fordecosport.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DB_NAME = "SYNC.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "my_table";
    private static final String COLUME_ID = "_id";
    private static final String COLUME_TIME = "time";
    private static final String COLUME_EVENT ="event";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;


    public DataBaseHelper(@Nullable Context context) {
        super(context,DB_NAME,null,DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " TEXT, "+COLUME_TIME + " TEXT, " +
                COLUME_EVENT +" TEXT);";
        sqLiteDatabase.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);

    }

    public void addDataBase(String event, String time){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUME_TIME, time);
        contentValues.put(COLUME_EVENT, event);
        long res = database.insert(TABLE_NAME,null, contentValues);
        if (res == -1) {
            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readAll(){
        String query = "SELECT * FROM " +TABLE_NAME;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = null;
        if(database!= null){
            cursor = database.rawQuery(query, null);
        } return cursor;
    }
}
