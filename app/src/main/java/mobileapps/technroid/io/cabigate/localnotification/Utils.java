package mobileapps.technroid.io.cabigate.localnotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.helper.Const;
import mobileapps.technroid.io.cabigate.ui.activity.MainActivity;


public class Utils {

	public static NotificationManager mManager;
	public static final int notifyID = 9001;
	@SuppressWarnings("static-access")
	public static void generateNotification(Context context,Intent intent){

		int millis =(int) System.currentTimeMillis() / 1000;

		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		notificationIntent.putExtra(Const.FLAG,true);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, millis,
				notificationIntent, PendingIntent.FLAG_ONE_SHOT);





		NotificationCompat.Builder mNotifyBuilder;
		NotificationManager mNotificationManager;

		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);




		mNotifyBuilder = new NotificationCompat.Builder(context)
				.setContentTitle(context.getResources().getString(R.string.app_name))
				.setContentText("Shift is near to comming finish")
				.setSmallIcon(R.mipmap.ic_launcher);
		// Set pending intent
		mNotifyBuilder.setContentIntent(resultPendingIntent);

		// Set Vibrate, Sound and Light
		int defaults = 0;
		defaults = defaults | Notification.DEFAULT_LIGHTS;
		defaults = defaults | Notification.DEFAULT_VIBRATE;


		///   mNotifyBuilder.setDefaults(defaults);

		mNotifyBuilder.setDefaults(Notification.DEFAULT_SOUND);
		mNotifyBuilder.setAutoCancel(true);



		mNotificationManager.notify(323, mNotifyBuilder.build());





	}























}
