package com.bms.myhelper;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class service extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationChannel c = new NotificationChannel("noti", "noti", NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(c);
        Notification.Builder n = new Notification.Builder(this, "noti").setSmallIcon(Icon.createWithResource(this, R.drawable.ic_launcher_background)).setContentTitle("MyHelper Working!").setAutoCancel(false).setOngoing(true);
        startForeground(1, n.build());
        return START_STICKY;
    }
    protected static boolean fuck(Context context){
        ActivityManager a=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo s:a.getRunningServices(Integer.MAX_VALUE))if(service.class.getName().equals(s.service.getClassName()))return true;
        return false;
    }
}
