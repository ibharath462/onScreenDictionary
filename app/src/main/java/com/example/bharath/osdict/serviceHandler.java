package com.example.bharath.osdict;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
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
    public serviceHandler() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(getApplicationContext(),"Started service",Toast.LENGTH_SHORT).show();
        final ClipboardManager clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {
                ClipData clipData = clipBoard.getPrimaryClip();
                ClipData.Item item = clipData.getItemAt(0);
                String word = item.getText().toString();
                dbHelper databaseHelper=new dbHelper(serviceHandler.this);
                String meaning = databaseHelper.getMeaning(word);
                if(meaning != null){
                    Toast.makeText(getApplicationContext(),"" + meaning,Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Word not found",Toast.LENGTH_SHORT).show();
                }

            }
        });
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(serviceHandler.this);
        localBroadcastManager.sendBroadcast(new Intent("com.OSdict.action.close"));
        return START_NOT_STICKY;
    }


}
