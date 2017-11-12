package com.example.bharath.osdict;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

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
                String text = item.getText().toString();
                Toast.makeText(getApplicationContext(),"" + text,Toast.LENGTH_SHORT).show();
            }
        });
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(serviceHandler.this);
        localBroadcastManager.sendBroadcast(new Intent("com.OSdict.action.close"));
        return START_NOT_STICKY;
    }
}
