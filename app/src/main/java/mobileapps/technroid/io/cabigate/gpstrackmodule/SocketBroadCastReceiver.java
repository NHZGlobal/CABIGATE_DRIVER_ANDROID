package mobileapps.technroid.io.cabigate.gpstrackmodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import mobileapps.technroid.io.cabigate.api.ApiManager;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;

import static mobileapps.technroid.io.cabigate.gpstrackmodule.ConnectivityReceiver.connectivityReceiverListener;

/**
 * Created by Danish on 9/3/2018.
 */

public class SocketBroadCastReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
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
                }
            }
            if (connectivityReceiverListener != null) {
                connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
            }

        } else {

            if (isConnected) {
                if (sharedPrefs.isLogin()) {
                    ApiManager.mSocketManager.connectSocketManager();
                }
            }
            if (connectivityReceiverListener != null) {
                connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
            }

        }
    }
}
