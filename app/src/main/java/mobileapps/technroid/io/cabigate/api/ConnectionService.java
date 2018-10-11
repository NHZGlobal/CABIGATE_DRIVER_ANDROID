package mobileapps.technroid.io.cabigate.api;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.app.MyApplication;


public class ConnectionService extends Service
{
	private final IBinder mBinder = new ConnectionServiceBinder();
	private MyApplication app;
	private PowerManager.WakeLock wl;
	/* Not using binder anywhere right now */
	public class ConnectionServiceBinder extends Binder
	{
		public ConnectionService getService()
		{
			return ConnectionService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if (Build.VERSION.SDK_INT >= 26) {
////			Intent notificationIntent = new Intent(this, MyApplication.class);
////			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//					notificationIntent, 0);
			Notification notification = new Notification.Builder(this)
					.setSmallIcon(R.mipmap.ic_launcher)
					.setContentTitle("Cabigate")
					.setContentText("Tracking  Location...").build();

			startForeground(1, notification);
		}
//        else {
//
////            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
////                    .setContentTitle(getString(R.string.app_name))
////                    .setContentText("Tracking  Location...")
////                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
////                    .setAutoCancel(true);
////
////            Notification notification = builder.build();
////            startForeground(1, notification);
//        }
		//connect();
		return START_STICKY;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		app = (MyApplication) getApplication();
		app.mService = this;
		PowerManager pm = (PowerManager)getApplicationContext().getSystemService(
				Context.POWER_SERVICE);
		this.wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK,

				"TAGXYS");
		wl.acquire();
	}

	public void updateLocationOfDriver()
	{
		try

		{

			SocketManager.getInstance().broadCastOnline();
		}catch (Exception e)
		{}
	}


	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (Build.VERSION.SDK_INT >= 26) {
			stopForeground(true);
		}
		wl.release();
	}
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		return mBinder;
	}

}