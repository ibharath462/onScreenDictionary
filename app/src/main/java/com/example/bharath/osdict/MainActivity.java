package com.example.bharath.osdict;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean cameFromNotification = false;
        Intent i = new Intent(MainActivity.this,serviceHandler.class);
        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
             cameFromNotification = b.getBoolean("isFromNotification");
        }
        if(cameFromNotification == true){
            //this.stopService(new Intent(this, serviceHandler.class));
            Toast.makeText(getApplicationContext(),"Service stopped" ,Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            startService(i);
            createNotification();
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction("com.OSdict.action.close");
            mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void createNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isFromNotification",true);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Stop")
                .setContentText("Subject").setSmallIcon(R.drawable.icon)
                .setContentIntent(pIntent)
                .addAction(R.drawable.icon, "And more", pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

    }
}
