package mobileapps.technroid.io.cabigate.api;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import mobileapps.technroid.io.cabigate.gpstrackmodule.SocketBroadCastReceiver;

/**
 * Created by Danish on 9/3/2018.
 */

public class ConnectSocketService extends Service
{

    PendingIntent pIntent;
    AlarmManager scheduler;

    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        scheduler = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, SocketBroadCastReceiver.class);
        pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        setSchedule();
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        cancelSchedule();
    }
    public void setSchedule() {

        scheduler.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),  2*1000 , pIntent); // Millisec * Second * Minute
    }

    public void cancelSchedule() {
        scheduler.cancel(pIntent);
    }




    }


