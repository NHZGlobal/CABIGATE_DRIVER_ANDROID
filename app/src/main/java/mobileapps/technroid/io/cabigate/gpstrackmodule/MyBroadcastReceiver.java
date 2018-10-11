package mobileapps.technroid.io.cabigate.gpstrackmodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import mobileapps.technroid.io.cabigate.api.ApiManager;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;

import static mobileapps.technroid.io.cabigate.gpstrackmodule.ConnectivityReceiver.connectivityReceiverListener;

/**
 * Created by TMC on 10/09/2018.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
this.mContext = context;
        SharedPrefs sharedPrefs = new SharedPrefs(context);
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            ////// reset scheduler

            if (isConnected) {
                if (sharedPrefs.isLogin()) {
                    ApiManager.mSocketManager.connectSocketManager();
                    Toast.makeText(mContext.getApplicationContext(),"Job Scheduled", Toast.LENGTH_SHORT).show();
                }
            }
//            if (connectivityReceiverListener != null) {
//                connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
//            }

        } else {

            if (isConnected) {
                if (sharedPrefs.isLogin()) {
                    ApiManager.mSocketManager.connectSocketManager();
                   // Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                    Toast.makeText(mContext.getApplicationContext(),"Job Scheduled", Toast.LENGTH_SHORT).show();               }
            }
//            if (connectivityReceiverListener != null) {
//                connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
//            }

        }
    }


}

