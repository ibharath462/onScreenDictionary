package com.example.bharath.osdict;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class serviceHandler extends Service {

    private static final String TAG = "BackgroundSoundService";
    ClipboardManager clipBoard ;
    static boolean bHasClipChangedListener = false;
    private WindowManager wm;
    WindowManager.LayoutParams p;
    View myView;


    ClipboardManager.OnPrimaryClipChangedListener mPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            ClipData clipData = clipBoard.getPrimaryClip();
                ClipData.Item item = clipData.getItemAt(0);
                String word = item.getText().toString();
                dbHelper databaseHelper=new dbHelper(serviceHandler.this);
                String meaning = databaseHelper.getMeaning(word);
                if(meaning != null){
                    //Toast.makeText(getApplicationContext(),"" + meaning,Toast.LENGTH_LONG).show();
                    wm=(WindowManager)getSystemService(WINDOW_SERVICE);
                    p=new WindowManager.LayoutParams(1000,1000, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.OPAQUE);
                    LayoutInflater fac=LayoutInflater.from(serviceHandler.this);
                    myView = fac.inflate(R.layout.popup, null);
                    TextView w = (TextView)myView.findViewById(R.id.word);
                    TextView m = (TextView)myView.findViewById(R.id.meaning);
                    Button b = (Button)myView.findViewById(R.id.c);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            wm.removeView(myView);
                        }
                    });
                    w.setText(word);
                    m.setText(meaning);
                    wm.addView(myView, p);


                }
                else{
                    Toast.makeText(getApplicationContext(),"Word not found",Toast.LENGTH_SHORT).show();
                }
        }
    };

    private void RegPrimaryClipChanged(){
        if(!bHasClipChangedListener){
            clipBoard.addPrimaryClipChangedListener(mPrimaryClipChangedListener);
            bHasClipChangedListener = true;
        }
    }

    @Override
    public void onCreate() {
        clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        RegPrimaryClipChanged();
        super.onCreate();
    }

    private void UnRegPrimaryClipChanged(){
        if(bHasClipChangedListener){
            clipBoard.removePrimaryClipChangedListener(mPrimaryClipChangedListener);
            bHasClipChangedListener = false;
        }
    }

    public serviceHandler() {
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i(TAG, "onBind()" );
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(getApplicationContext(),"Started service",Toast.LENGTH_SHORT).show();
//        clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
//        clipBoard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
//
//            @Override
//            public void onPrimaryClipChanged() {
//                ClipData clipData = clipBoard.getPrimaryClip();
//                ClipData.Item item = clipData.getItemAt(0);
//                String word = item.getText().toString();
//                dbHelper databaseHelper=new dbHelper(serviceHandler.this);
//                String meaning = databaseHelper.getMeaning(word);
//                if(meaning != null){
//                    Toast.makeText(getApplicationContext(),"" + meaning,Toast.LENGTH_LONG).show();
//                }
//                else{
//                    Toast.makeText(getApplicationContext(),"Word not found",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(serviceHandler.this);
        localBroadcastManager.sendBroadcast(new Intent("com.OSdict.action.close"));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        UnRegPrimaryClipChanged();
        super.onDestroy();
    }


}
