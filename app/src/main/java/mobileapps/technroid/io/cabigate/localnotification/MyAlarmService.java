package mobileapps.technroid.io.cabigate.localnotification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyAlarmService extends Service

{

	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() 
	{
		// TODO Auto-generated method stub  
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}