package com.example.bharath.osdict;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by bharath on 14/11/17.
 */

public class dbHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "dict.db";
    private static final String TABLE_NAME = "dictTable";
    public static final String CATEGORY_COLUMN_ID = "_id";
    public static final String WORD = "word";
    public static final String MEANING = "meaning";
    private SQLiteDatabase database;


    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + CATEGORY_COLUMN_ID + " INTEGER PRIMARY KEY, "
                + WORD + " TEXT , " + MEANING + " TEXT)");

        Log.i("Database : ", "Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    public void initDictionary(JSONObject list) throws JSONException {

        Iterator<?> keys = list.keys();
        database = this.getWritableDatabase();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            ContentValues contentValues = new ContentValues();
            contentValues.put(WORD, key);
            contentValues.put(MEANING, list.getString(key));
            database.insert(TABLE_NAME, null, contentValues);
        }

    }

    public String getMeaning(String word){
        String meaning = "";
        String query = "SELECT * FROM dictTable where word='" + word.toUpperCase() + "'";
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query,null);
        Log.i("Database : ", "" + cursor.toString());
        if((cursor.moveToFirst()) || cursor.getCount() != 0){
            cursor.moveToFirst();
            meaning = cursor.getString(cursor.getColumnIndex(MEANING));
            cursor.close();
            return meaning;
        }
        return null;
        //Log.i("Database : ", "" + word.toUpperCase());
        //Log.i("Database : ", "" + cursor.getString(2));
    }
}
