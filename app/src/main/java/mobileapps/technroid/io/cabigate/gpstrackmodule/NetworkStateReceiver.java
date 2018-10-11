package mobileapps.technroid.io.cabigate.gpstrackmodule;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import mobileapps.technroid.io.cabigate.api.ApiManager;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;

import static android.content.Context.ACTIVITY_SERVICE;

public class NetworkStateReceiver extends BroadcastReceiver {
    public void onReceive(final Context context, Intent intent) {
        Log.d("app", "Network connectivity change");
        if (intent.getExtras() != null) {
            SharedPrefs sharedPrefs = new SharedPrefs(context);
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                if (sharedPrefs.isLogin()) {
                    ApiManager.mSocketManager.connectSocketManager();
                }
                ///Log.i("app","Network "+ni.getTypeName()+" connected");
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                if (sharedPrefs.isLogin()) {

                    ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                    ComponentName componentInfo = taskInfo.get(0).topActivity;
                    String packageName = componentInfo.getPackageName();
                    String name = "mobileapps.technroid.io.cabigate";
                    if ((name.toString().trim()).equals(packageName.toString().trim())) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(context.getApplicationContext(), " You are not Connected to Internet", Toast.LENGTH_SHORT).show();


                            }
                        });

                    } else {
                        //sendPushNotificationBackground(json);
                    }

                    // Log.d("app","There's no network connectivity
                    // ");
                }
            }
        }
    }
}