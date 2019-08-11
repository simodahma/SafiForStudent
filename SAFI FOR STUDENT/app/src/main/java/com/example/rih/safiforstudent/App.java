package com.example.rih.safiforstudent;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class App extends Application {

    public static final String CHANNEL="channel";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate()
    {
        super.onCreate();
        createNotification();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void  createNotification()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O);
        {
            NotificationChannel chanel = new NotificationChannel(CHANNEL,"Channel",NotificationManager.IMPORTANCE_HIGH);
            chanel.setDescription("New place added by");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(chanel);
        }
    }
}
