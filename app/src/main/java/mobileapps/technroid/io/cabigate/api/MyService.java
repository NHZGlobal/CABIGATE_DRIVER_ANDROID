package mobileapps.technroid.io.cabigate.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import mobileapps.technroid.io.cabigate.helper.SharedPrefs;

/**
 * Created by TMC on 10/09/2018.
 */

public class MyService  extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        SharedPrefs sharedPrefs = new SharedPrefs(this);
        ConnectivityManager cm = (ConnectivityManager)this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            if (sharedPrefs.isLogin()) {
                ApiManager.mSocketManager.connectSocketManager();
                //Toast.makeText(getApplicationContext(), "job created", Toast.LENGTH_SHORT).show();
            }
        }
        //Toast.makeText(getApplicationContext(), "job created", Toast.LENGTH_SHORT).show();
        // Do some work here
        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }
}
