package mobileapps.technroid.io.cabigate.gpstrackmodule;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import mobileapps.technroid.io.cabigate.helper.SharedPrefs;

public class BootCompletedReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.d("app", "Network connectivity change");
        SharedPrefs sharedPrefs = new SharedPrefs(context);
        sharedPrefs.setJson("latestjob",true);
    }
}