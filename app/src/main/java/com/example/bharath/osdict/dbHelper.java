package com.example.bharath.osdict;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private  String DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

    private  String DB_NAME = "dict";

    private final Context myContext;


    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
            Log.v("Database22", "Alreadyyy haiii");
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                MainActivity.copyDataBase();

            } catch (IOException e) {

                //throw new Error("Error copying database");

            }
        }

    }

    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.
            Log.v("Database22", "Virign haiiiii");

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }



    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + CATEGORY_COLUMN_ID + " INTEGER PRIMARY KEY, "
                + WORD + " TEXT , " + MEANING + " TEXT)");

        Log.i("Database22: ", "Created");
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
        String myPath = DB_PATH + DB_NAME;
        database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM dict where word='" + word + "'";
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
