package com.example.bharath.osdict;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class MainActivity extends AppCompatActivity {

    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.OSdict.action.close")){
                finish();
            }
        }
    };



    SharedPreferences prefs = null;
    static Resources res;
    static String dbPath,dbName;
    initDictionaryAsynTask dicTask = new initDictionaryAsynTask();
    Intent i;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res = getResources();
        if(Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }
        }

        prefs = getSharedPreferences("com.example.bharath.osdict", MODE_PRIVATE);
        boolean cameFromNotification = false;
        i = new Intent(MainActivity.this,serviceHandler.class);
        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
             cameFromNotification = b.getBoolean("isFromNotification");
        }
        if(cameFromNotification == true){
            Intent myService = new Intent(MainActivity.this, serviceHandler.class);
            stopService(myService);
            finish();
        }
        else if (prefs.getBoolean("firstrun", true)) {
            Toast.makeText(getApplicationContext(),"We are initializing our database, for only this time. Hold back we will notify you once done" ,Toast.LENGTH_LONG).show();
//            prefs.edit().putBoolean("firstrun", false).commit();
//            dicTask.execute();
//            //Creating db initialized notification....
//            Notification noti = new Notification.Builder(MainActivity.this)
//                    .setContentTitle("Initializing")
//                    .setContentText("Adding words").setSmallIcon(R.drawable.icon).build();
//            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            noti.flags |= Notification.FLAG_AUTO_CANCEL;
//
//            notificationManager.notify(0, noti);
            //finish();
            //startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 42);
            prefs.edit().putBoolean("firstrun", false).commit();
//            dbHelper d = new dbHelper(MainActivity.this);
//            try {
//                d.createDataBase();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        else{
            Log.v("Database22", "DOOOO");
            startService(i);
            createNotification();
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction("com.OSdict.action.close");
            mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode != RESULT_OK)
            return;
        else {
            Uri treeUri = resultData.getData();
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
            grantUriPermission(getPackageName(), treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            dbPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
            dbName = "dict";
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void createNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isFromNotification",true);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Stop")
                .setContentText("Subject").setSmallIcon(R.drawable.icon)
                .setContentIntent(pIntent)
                .addAction(R.drawable.icon, "And more", pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

    }

    public class initDictionaryAsynTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            InputStream is = getResources().openRawResource(R.raw.dictionary);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                try {
                    while ((n = reader.read(buffer)) != -1) {
                        writer.write(buffer, 0, n);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String jsonString = writer.toString();
            JSONObject meanings = null;
            try {
                 meanings = new JSONObject(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dbHelper databaseHelper=new dbHelper(MainActivity.this);
            try {
                databaseHelper.initDictionary(meanings);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"Initialised.Read on.",Toast.LENGTH_LONG).show();
                    startService(i);
                    createNotification();
                    mLocalBroadcastManager = LocalBroadcastManager.getInstance(MainActivity.this);
                    IntentFilter mIntentFilter = new IntentFilter();
                    mIntentFilter.addAction("com.OSdict.action.close");
                    mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);

                    Toast.makeText(getApplicationContext(),"Doneeee babyyyy",Toast.LENGTH_SHORT).show();


                    //Creating db initialized notification....
                    Notification noti = new Notification.Builder(MainActivity.this)
                            .setContentTitle("Initialized")
                            .setContentText("Done").setSmallIcon(R.drawable.icon).build();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;

                    notificationManager.notify(0, noti);
                }
            });

        }
    }


    static void copyDataBase() throws IOException{

        //Open your local db as the input stream

        InputStream myInput;
        myInput = res.openRawResource(R.raw.dict);

        // Path to the just created empty db
        String outFileName = dbPath + dbName;
        Log.v("Database22", "CopyingggMAIN.." + outFileName);

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            Log.v("Database22", "Copyingggggg" + outFileName);
            myOutput.write(buffer, 0, length);
            Log.v("Database22", "DOOOO" + outFileName);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

        Log.v("Database22", "Doneee haiii");

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
