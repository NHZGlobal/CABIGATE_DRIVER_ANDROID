package mobileapps.technroid.io.cabigate.localnotification;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import mobileapps.technroid.io.cabigate.global.Constants;


public class MyReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> services = activityManager
				.getRunningTasks(Integer.MAX_VALUE);
		boolean isActivityFound = false;

		if (services.get(0).topActivity.getPackageName().toString()
				.equalsIgnoreCase(context.getPackageName().toString())) {
			isActivityFound = true;
		}

		if (isActivityFound) {
			Intent myIntent = new Intent();
			myIntent.setAction(Constants.ACTION_MY_SHIFT_FILTER);

			context.sendBroadcast(myIntent);

		} else {
			Utils.generateNotification(context,intent);
		}
		Log.i("App", "called receiver method");

	}

}
