package mobileapps.technroid.io.cabigate.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;
import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.api.ApiManager;
import mobileapps.technroid.io.cabigate.api.ConnectSocketService;
import mobileapps.technroid.io.cabigate.api.ConnectionService;
import mobileapps.technroid.io.cabigate.api.SocketManager;
import mobileapps.technroid.io.cabigate.app.MyApplication;
import mobileapps.technroid.io.cabigate.global.Constants;
import mobileapps.technroid.io.cabigate.gpstrackmodule.MyBroadcastReceiver;
import mobileapps.technroid.io.cabigate.helper.Const;
import mobileapps.technroid.io.cabigate.helper.DatabaseHelper;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;
import mobileapps.technroid.io.cabigate.helper.SnackBarDisplay;
import mobileapps.technroid.io.cabigate.localnotification.MyReceiver;
import mobileapps.technroid.io.cabigate.models.ChatMessage;
import mobileapps.technroid.io.cabigate.models.Job;
import mobileapps.technroid.io.cabigate.ui.fragment.FragmentCurrentNew;
import mobileapps.technroid.io.cabigate.ui.widgets.alertbox.SweetAlertDialog;
import mobileapps.technroid.io.cabigate.utilities.ImageUtils;
import mobileapps.technroid.io.cabigate.utilities.Utilities;
import retrofit.client.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static mobileapps.technroid.io.cabigate.global.Constants.handler;
import static mobileapps.technroid.io.cabigate.global.Constants.timer;
import static mobileapps.technroid.io.cabigate.global.Constants.timerTask;
import static mobileapps.technroid.io.cabigate.ui.activity.StartShiftActivity.Job_Tag;
import static mobileapps.technroid.io.cabigate.ui.activity.StartShiftActivity.jobDispatcher;


public class MainActivity extends BaseActivity {



    @Bind(R.id.tvDriverName)
    TextView tvDriverName;
    @Bind(R.id.tvCarNumber)
    TextView tvCarNumber;
    @Bind(R.id.ivDriverProfile)
    ImageView ivDriverProfile;

    @Bind(R.id.tvTime)
    TextView tvTime;
    @Bind(R.id.mainContainer)
    View mainContainer;
    private NotificationManager mNotificationManager;
    private CountDownTimer mTimer;
    private MediaPlayer mediaPlayer;
    private boolean shiftInDilog;
    private Dialog jobdilog;
    private int currentRequest=0;
    private Context context;
    public static final int logoutRequest=0;
    public static final int zooneCheckOutRequest=1;
    public static final int shiftRequest=2;
    public static final int driverStatusRequest=9;
    public static final int lastStatusRequest=3;

    public  static void openActivity(Activity activity) {

        Intent intent=new Intent(activity,MainActivity.class);
        activity.startActivity(intent);
        activity. overridePendingTransition(R.anim.slide_in_right, R.anim.do_nothing);



    }



    @Bind(R.id.btnJObShift)
    Button btnJObShift;



    @OnClick(R.id.btnJObView)
    void jobView(View view) {
        try{
            if (mReceivedReceiver != null)
            this.unregisterReceiver(mReceivedReceiver);
            this.unregisterReceiver(myShiftinreciver);
            this.unregisterReceiver(mDsipactSyncReceiver);
            this.unregisterReceiver(mMessageReceivedReceiver);
            this.unregisterReceiver(driverStatusSyncjobReciver);
        }catch(Exception e){}
        JobViewActivity.openActivity(MainActivity.this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        addBroadcastReceivers();

        //moveTaskToBack(true);

        app.apiManager.mSocketManager.connectSocket();
        checkShifInTime();

        if(!sharedPrefs.isLogin()){
            app.apiManager.mSocketManager.broadCastOffline();
            app.stopService();
            app.mGoogleApiClient.disconnect();
            Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent1);
        }

        if(!sharedPrefs.isJson(Const.SHIFTIN)) {
             cancelAlarmManager();
            btnJObShift.setText("Start Shift");
        }
       /* if(Constants.syncheck)
        {
            this.unregisterReceiver(mReceivedReceiver);
            Constants.syncheck = false;
        }*/
    }

    private void addBroadcastReceivers() {
        IntentFilter filter = new IntentFilter(Constants.ACTION_REQUEST_ORDER);
        MainActivity.this.registerReceiver(mReceivedReceiver, filter);
        IntentFilter myShiftfilter = new IntentFilter(Constants.ACTION_MY_SHIFT_FILTER);
        MainActivity.this.registerReceiver(myShiftinreciver, myShiftfilter);
        IntentFilter mDsipactSyncfilter = new IntentFilter(Constants.ACTION_REQUEST_SYNC_JOB);
        MainActivity.this.registerReceiver(mDsipactSyncReceiver, mDsipactSyncfilter);
        IntentFilter mMessageReceivedReceiverfilter = new IntentFilter(Constants.ACTION_RECIVE_MESSAGE);
registerReceiver(mMessageReceivedReceiver, mMessageReceivedReceiverfilter);
        IntentFilter forcelogoutreciverfilter = new IntentFilter(Constants.ACTION_FORCE);
        MainActivity.this. registerReceiver(forcelogoutreciver, forcelogoutreciverfilter);
        IntentFilter driverstatussyncjob = new IntentFilter(Constants.ACTION_SYNC_DRVIER_STATUS);
        MainActivity.this.registerReceiver(driverStatusSyncjobReciver, driverstatussyncjob);




    }


    public void schedulerTask()
    {

        timer = new Timer();

        initializeTimerTask();
        timer.schedule(timerTask, 5000, 3000); //

//        Intent intent = new Intent(StartShiftActivity.this, MyBroadcastReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                StartShiftActivity.this, 1, intent, 0);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
////        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
////                SystemClock.elapsedRealtime(),
////                3*1000, // 60000 = 1 minute
////                pendingIntent);
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
//                ,(3 * 1000), pendingIntent);
    }
    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        SharedPrefs sharedPrefs = new SharedPrefs(MainActivity.this);
                        ConnectivityManager cm = (ConnectivityManager) MainActivity.this
                                .getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null
                                && activeNetwork.isConnectedOrConnecting();
                        if (isConnected) {
                            if (sharedPrefs.isLogin()) {
                                ApiManager.mSocketManager.connectSocketManager();
                                // Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this.getApplicationContext(),"Job Scheduled", Toast.LENGTH_SHORT).show();               }
                        }

                    }
                });
            }
        };
    }

    private final BroadcastReceiver driverStatusSyncjobReciver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getBooleanExtra("error", false)) {


                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.app_name))
                        .setContentText(intent.getStringExtra(Constants.RESPONSE_KEY_MESSAGE))
                        .show();
                return;
            }

            if (!intent.hasExtra("data")) {
                return;
            }
            String data = intent.getStringExtra("data");

            if(sharedPrefs.isJson(Const.SHIFTIN)){
                Job job = new Gson().fromJson(data, Job.class);
              {
                    currentRequest=MainActivity.driverStatusRequest;
                    if(job.status.equals("free")){
                        FragmentCurrentNew.status = 6;
                        updateDriverStatus("free", "0");
                        sharedPrefs.setJson(Const.AWAYTIME, false);

                    }else if(job.status.equals("busy")){
                        updateDriverStatus("busy", "0");
                        sharedPrefs.setJson(Const.AWAYTIME, false);
                    }else if(job.status.equals("away")){
                        awayStatussync(job.awaytime);
                    }


                }
            }
        }
    };



    public  void awayStatussync(int time){
        currentRequest=MainActivity.driverStatusRequest;
        //sweetIndicator.show();
        updateDriverStatus("away", "" + time);

        sharedPrefs.setJson(Const.AWAYSTARTTIME, new Date().getTime() + "");
        sharedPrefs.setJson(Const.AWAYTIME, (60 * time) + "");
        sharedPrefs.setJson(Const.AWAYTIME, true);

    }


    public void updateDriverStatus(String status,String time){

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put(Const.COMPANYID,MyApplication.getInstance().myDriver.companyID);
            jsonObject.put(Const.USERID,MyApplication.getInstance().myDriver.userid);
            jsonObject.put(Const.TOKEN,MyApplication.getInstance().myDriver.token);
            jsonObject.put("eta",time);
            jsonObject.put(Const.STATUS, status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiManager.getInstance().mUrlManager.statusDriverUpdate(jsonObject.toString()).cache().
                subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }


    private final BroadcastReceiver mDsipactSyncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("error", false)) {


                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.app_name))
                        .setContentText(intent.getStringExtra(Constants.RESPONSE_KEY_MESSAGE))
                        .show();
                return;
            }

            if (!intent.hasExtra("data")) {
                return;
            }

                String data = intent.getStringExtra("data");
                ApiManager.getInstance().mBooking = new Gson().fromJson(data, Job.class);
                ApiManager.getInstance().mBooking.issync=true;
                if (ApiManager.getInstance().mBooking.status.equals("pob")) {
                    ApiManager.getInstance().mBooking.setCurrentstatus("pob");
                    ApiManager.getInstance().mBooking.status1 = 2;
                } else if (ApiManager.getInstance().mBooking.status.equals("callout")) {
                    ApiManager.getInstance().mBooking.setCurrentstatus("callout");
                    ApiManager.getInstance().mBooking.status1 = 0;
                } else if (ApiManager.getInstance().mBooking.status.equals("wait")) {
                    ApiManager.getInstance().mBooking.status1 = 1;
                    ApiManager.getInstance().mBooking.setCurrentstatus("wait");
                } else {
                    ApiManager.getInstance().mBooking = null;
                }


                Intent myIntent = new Intent(MainActivity.this, JobViewActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                myIntent.putExtra("data", data.toString());
                sharedPrefs.setJson(Const.CURREMT_WAY_POINT, 0);

                if (sharedPrefs.isJson(Const.SHIFTIN)){
                    if( jobdilog!=null&&jobdilog.isShowing())
                        return;
                if (((Activity) MainActivity.this).isFinishing()) {
                        return;
                }
                 if(ApiManager.getInstance().mBooking==null) {
                            return;
                 }
                    try {
                         if (mReceivedReceiver != null)
                                   MainActivity.this.unregisterReceiver(mReceivedReceiver);
                        MainActivity.this.unregisterReceiver(myShiftinreciver);
                        MainActivity.this.unregisterReceiver(forcelogoutreciver);
                        MainActivity.this. unregisterReceiver(mDsipactSyncReceiver);
                        MainActivity.this. unregisterReceiver(mMessageReceivedReceiver);
                        MainActivity.this.unregisterReceiver(driverStatusSyncjobReciver);
                         } catch (Exception e) {
                        }
                            app.apiManager.mReservedBooking = ApiManager.getInstance().mBooking;
                            Intent yIntent = new Intent(MainActivity.this, JobViewActivity.class);
                            sharedPrefs.setJson(Const.ISJOB, true);
                            myIntent.putExtra(Const.FLAG, true);
                            myIntent.putExtra(Const.JOB_CODE, app.apiManager.mReservedBooking);
                            startActivity(yIntent);
                }

            }



    };




    private final BroadcastReceiver mReceivedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("error", false)) {


                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.app_name))
                        .setContentText(intent.getStringExtra(Constants.RESPONSE_KEY_MESSAGE))
                        .show();
                return;
            }

            if (intent.hasExtra("data")) {


                String data = intent.getStringExtra("data");
                ApiManager.getInstance().mBooking = new Gson().fromJson(data, Job.class);

                Intent myIntent = new Intent(MainActivity.this, JobViewActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                myIntent.putExtra("data", data.toString());
                sharedPrefs.setJson(Const.CURREMT_WAY_POINT, 0);



                if (sharedPrefs.isJson(Const.SHIFTIN)){
                    if( jobdilog!=null&&jobdilog.isShowing())
                        return;
                    /*if(!app.isAppOnline){
                        Intent intent1 = new Intent(context, MainActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent1);
                    }*/

                    if (! ((Activity) MainActivity.this).isFinishing()) {
                        jobDilaog(ApiManager.getInstance().mBooking, data);
                    }

                    }

            }


        }
    };



    private final BroadcastReceiver myShiftinreciver= new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            if (! ((Activity) MainActivity.this).isFinishing()) {
                shiftInDilog();
            }


        }
    };
    private final BroadcastReceiver forcelogoutreciver= new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.hasExtra("data")) {


                String type=intent.getStringExtra("data"); sharedPrefs.setJson(Const.SHIFTIN, false);

                if(type.equals("shift")) {

                    currentRequest=MainActivity.shiftRequest;
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Const.COMPANYID, app.myDriver.companyID);
                        jsonObject.put(Const.USERID, app.myDriver.userid);
                        jsonObject.put(Const.TOKEN, app.myDriver.token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApiManager.getInstance().mUrlManager.shiftOut(jsonObject.toString()).cache().
                            subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

                    sharedPrefs.setJson(Const.SHIFTIN, false);

               /*     tvTime.setText("");
                    btnJObShift.setText("Start Shift");*/
                    cancelAlarmManager();
                }else if(type.equals("logout")){

                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put(Const.COMPANYID,app.myDriver.companyID);
                        jsonObject.put(Const.USERID,app.myDriver.userid);
                        jsonObject.put(Const.TOKEN,app.myDriver.token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    currentRequest=logoutRequest;
                    //sweetIndicator.show();
                    MyApplication.getInstance().apiManager.getInstance().mUrlManager.logout(jsonObject.toString()).cache().
                            subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

                }else if(type.equals("zone")){

                    sharedPrefs.setJson("myqueue", false);
                    JSONObject jsonObject=new JSONObject();
                    try {

                        jsonObject.put(Const.USERID, MyApplication.getInstance().myDriver.userid);
                        jsonObject.put(Const.COMPANYID,MyApplication.getInstance().myDriver.companyID);
                        jsonObject.put(Const.TOKEN,MyApplication.getInstance().myDriver.token);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    currentRequest=zooneCheckOutRequest;
                    ApiManager.getInstance().mUrlManager.zoneCheckout(jsonObject.toString()).cache().subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(observer);

                }

            }
        }
    };















    @OnClick(R.id.btnJObShift)
     void startShift(View view){
         if(!sharedPrefs.isJson(Const.SHIFTIN)){
         StartShiftActivity.openActivity(MainActivity.this);
         }else{
             JSONObject jsonObject=new JSONObject();
             try {
                 jsonObject.put(Const.COMPANYID,app.myDriver.companyID);
                 jsonObject.put(Const.USERID,app.myDriver.userid);
                 jsonObject.put(Const.TOKEN,app.myDriver.token);
             } catch (JSONException e) {
                 e.printStackTrace();
             }
             currentRequest=shiftRequest;
            sweetIndicator.show();
             ApiManager.getInstance().mUrlManager.shiftOut(jsonObject.toString()).cache().
                     subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

         }
     }

    @OnClick(R.id.btnLogOut)
    public void logout(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage("Are you sure you want to Logout?");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put(Const.COMPANYID,app.myDriver.companyID);
                            jsonObject.put(Const.USERID,app.myDriver.userid);
                            jsonObject.put(Const.TOKEN,app.myDriver.token);
                            Constants.logoutcheck = true;
                            Intent serviceIntent = new Intent(MainActivity.this, ConnectionService.class);
                            stopService(serviceIntent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        sweetIndicator.show();
                        currentRequest=logoutRequest;
                        MyApplication.getInstance().apiManager.getInstance().mUrlManager.logout(jsonObject.toString()).cache().
                                subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

                    }
                });


        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        alertDialogBuilder.show();





    }


    void checkShifInTime(){
        if(sharedPrefs.isJson(Const.SHIFTIN)){

            if(shiftInDilog){
                return;
            }
            if(sharedPrefs.isJson(Const.SHIFTIN)){
            if(new DateTime().isAfter(new DateTime(Long.parseLong(sharedPrefs.getJson(Const.SHIFT_END_TIME)))))
            {


                if (! ((Activity) MainActivity.this).isFinishing()) {
                    shiftInDilog();
                }
            }

        }}
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        /*null the job status*/
        MyApplication.getInstance().apiManager.mReservedBooking=null;
        sharedPrefs.setJson(Const.ISJOB, false);

        String versionRelease = Build.VERSION.RELEASE;
        tvCarNumber.setText(app.versionName);
//        if(Constants.syncheck)
//        {
//            schedulerTask();
//        }
//        app.apiManager.mSocketManager.connectSocket();
        ImageUtils.setRoundedBitmap(app.myDriver.driverImage, ivDriverProfile, this, R.drawable.user_ic);
        tvDriverName.setText("Welcome," + app.myDriver.username);

        if(sharedPrefs.isJson(Const.SHIFTIN)){

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            String date2 = format.format(Long.parseLong(sharedPrefs.getJson(Const.SHIFT_END_TIME)));
            tvTime.setText("Shift end at:"+date2);
        }else{



        }/**/


        if(getIntent().getBooleanExtra(Const.FLAG,false)){

            if(sharedPrefs.isJson(Const.SHIFTIN)) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(Const.COMPANYID, app.myDriver.companyID);
                    jsonObject.put(Const.USERID, app.myDriver.userid);
                    jsonObject.put(Const.TOKEN, app.myDriver.token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sweetIndicator.show();
                ApiManager.getInstance().mUrlManager.shiftOut(jsonObject.toString()).cache().
                        subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
            }
        }


        if(sharedPrefs.isJson(Const.SHIFTIN)){

          if(sharedPrefs.isJson("latestjob")) {
              currentRequest = MainActivity.lastStatusRequest;
              JSONObject jsonObject = new JSONObject();
              try {
                  jsonObject.put(Const.COMPANYID, app.myDriver.companyID);
                  jsonObject.put(Const.USERID, app.myDriver.userid);
                  jsonObject.put(Const.TOKEN, app.myDriver.token);
              } catch (JSONException e) {
                  e.printStackTrace();
              }
              sweetIndicator.show();
              ApiManager.getInstance().mUrlManager.myLastState(jsonObject.toString()).cache().
                      subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
          }else
            if(!new DateTime().isAfter(new DateTime(Long.parseLong(sharedPrefs.getJson(Const.SHIFT_END_TIME)))))
            {
             sweetIndicator.show();
                ApiManager.getInstance().mUrlManager.latestJob(app.myDriver.companyID, app.myDriver.userid).cache().
                        subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observerisnewJob);

            }

        }

        SocketManager.getInstance().connectSocketManager();

        if(sharedPrefs.isJson(Const.SHIFTIN)) {
            btnJObShift.setText("End Shift");
        }else{
            btnJObShift.setText("Start Shift");
        }


    }



    Observer<Response> observer=new Observer<Response>() {
        @Override
        public void onCompleted() {
         sweetIndicator.dismiss();
        }

        @Override

        public void onError(Throwable e) {
            sweetIndicator.dismiss();

         displayError(e);

        }

        @Override
        public void onNext(Response response) {



            String jsonStr = Utilities.convertTypedInputToString(response.getBody());
            Log.e("error", jsonStr);

            try {
                JSONObject obj = new JSONObject(jsonStr);
                if(obj.getInt(Const.STATUS)==1){
                switch (currentRequest){
                    case lastStatusRequest:
                        sharedPrefs.setJson(Const.OFFER,obj.getJSONObject("response").getInt("offerscount"));
                       if(!obj.getJSONObject("response").getString("job_active").equals("yes"))
                           return;
                        sharedPrefs.setJson("latestjob",false);
                        String data = obj.getJSONObject("response").getJSONObject("details").toString();
                        ApiManager.getInstance().mBooking = new Gson().fromJson(data, Job.class);
                        ApiManager.getInstance().mBooking.issync=true;
                        if (ApiManager.getInstance().mBooking.status.equals("pob")) {
                            ApiManager.getInstance().mBooking.setCurrentstatus("pob");
                            ApiManager.getInstance().mBooking.status1 = 2;
                        } else if (ApiManager.getInstance().mBooking.status.equals("callout")) {
                            ApiManager.getInstance().mBooking.setCurrentstatus("callout");
                            ApiManager.getInstance().mBooking.status1 = 0;
                        } else if (ApiManager.getInstance().mBooking.status.equals("wait")) {
                            ApiManager.getInstance().mBooking.status1 = 1;
                            ApiManager.getInstance().mBooking.setCurrentstatus("wait");
                        } else {
                            ApiManager.getInstance().mBooking = null;
                        }


                        Intent myIntent = new Intent(MainActivity.this, JobViewActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        myIntent.putExtra("data", data.toString());
                        sharedPrefs.setJson(Const.CURREMT_WAY_POINT, 0);

                        if (sharedPrefs.isJson(Const.SHIFTIN)) {
                            if (jobdilog != null && jobdilog.isShowing())
                                return;
                            if (((Activity) MainActivity.this).isFinishing()) {
                                return;
                            }
                            if (ApiManager.getInstance().mBooking == null) {
                                return;
                            }
                            try {
                                if (mReceivedReceiver != null)
                                    MainActivity.this.unregisterReceiver(mReceivedReceiver);
                                MainActivity.this.unregisterReceiver(myShiftinreciver);
                                MainActivity.this.unregisterReceiver(forcelogoutreciver);
                                MainActivity.this.unregisterReceiver(mDsipactSyncReceiver);
                                MainActivity.this.unregisterReceiver(mMessageReceivedReceiver);
                                MainActivity.this.unregisterReceiver(driverStatusSyncjobReciver);
                            } catch (Exception e) {
                            }
                            app.apiManager.mReservedBooking = ApiManager.getInstance().mBooking;
                            Intent yIntent = new Intent(MainActivity.this, JobViewActivity.class);
                            sharedPrefs.setJson(Const.ISJOB, true);
                            myIntent.putExtra(Const.FLAG, true);
                            myIntent.putExtra(Const.JOB_CODE, app.apiManager.mReservedBooking);
                            startActivity(yIntent);
                        }
                        break;
                    case logoutRequest:
                        DatabaseHelper databaseHelper=new DatabaseHelper(MainActivity.this);
                        databaseHelper.deleteAllHIatorY();
                        sharedPrefs.clearSharedprefs();
                        app.apiManager.mSocketManager.broadCastOffline();
                        app.stopService();
                        app.mGoogleApiClient.disconnect();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        break;
                    case zooneCheckOutRequest:
                        sharedPrefs.setJson("myqueue", false);

                        break;
                    case shiftRequest:
                        sharedPrefs.setJson(Const.SHIFTIN, false);
                        tvTime.setText("");
                        btnJObShift.setText("Start Shift");
                        cancelAlarmManager();
                        //canceltask();
                        canceljob();
                        //stopService(new Intent(MainActivity.this, ConnectSocketService.class));
                        if(shiftInDilog){
                            StartShiftActivity.openActivity(MainActivity.this);
                        }

                        if (isShiftContine) {

                            isShiftContine=false;
                        }


                        break;

                }

                }else{
                    SnackBarDisplay.ShowSnackBar(MainActivity.this, obj.getString(Const.ERROR_MESSAGE));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    private boolean isShiftContine;
    Observer<Response> observerSiftout = new Observer<Response>() {
        @Override
        public void onCompleted() {
            sweetIndicator.dismiss();
        }

        @Override
        public void onError(Throwable e) {
           sweetIndicator.dismiss();
            displayError(e);

        }

        @Override
        public void onNext(Response response) {


            String jsonStr = Utilities.convertTypedInputToString(response.getBody());
            try {
                Log.e("error", jsonStr);
                JSONObject obj = new JSONObject(jsonStr);

                if (obj.getInt(Const.STATUS) == 1) {
                    sharedPrefs.setJson(Const.SHIFTIN, false);
                    tvTime.setText("");
                    btnJObShift.setText("Start Shift");
                    cancelAlarmManager();
                    if(shiftInDilog){
                        StartShiftActivity.openActivity(MainActivity.this);
                    }

                    if (isShiftContine) {

                      isShiftContine=false;
                    }

                } else {
                    SnackBarDisplay.ShowSnackBar(MainActivity.this, obj.getString("error_msg"));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

private void canceljob()
{
    jobDispatcher.cancel(Job_Tag);
}

private void canceltask()
{
    //Constants.syncheck = false;
    //stop the timer, if it's not already null
    if (timer != null) {
        timer.cancel();
        timer = null;
    }
//    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//    Intent myIntent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
//    PendingIntent pendingIntent = PendingIntent.getBroadcast(
//            getApplicationContext(), 1, myIntent, 0);
//
//    alarmManager.cancel(pendingIntent);
}

    private void cancelAlarmManager() {
        //Log.d(TAG, "cancelAlarmManager");

        Context context = getBaseContext();
        Intent gpsTrackerIntent = new Intent(context, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, gpsTrackerIntent, PendingIntent.FLAG_ONE_SHOT );
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Const.SHIFTIN_CODE) {
            if(resultCode == Activity.RESULT_OK){
               btnJObShift.setText("End Shift");
                if(sharedPrefs.isJson(Const.SHIFTIN)){

                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    String date2 = format.format(Long.parseLong(sharedPrefs.getJson(Const.SHIFT_END_TIME)));
                    tvTime.setText("Shift end at:"+date2);
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }





    public void shiftInDilog(){
        shiftInDilog=true;
        View outerView = LayoutInflater.from(MainActivity.this).inflate(R.layout.shift_in_dialog, null);
        Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);
        Button btnCancel = (Button) outerView.findViewById(R.id.btnCancel);

        final AlertDialog.Builder alBuilder=new AlertDialog.Builder(MainActivity.this);

        alBuilder.setView(outerView)
        ;
        final AlertDialog ad = alBuilder.show();

        ad.setCancelable(false);
        ad.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        ad. getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        ad.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(Const.COMPANYID, app.myDriver.companyID);
                    jsonObject.put(Const.USERID, app.myDriver.userid);
                    jsonObject.put(Const.TOKEN, app.myDriver.token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sweetIndicator.show();
                currentRequest=shiftRequest;
                ApiManager.getInstance().mUrlManager.shiftOut(jsonObject.toString()).cache().
                        subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

            }
        });
           btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(Const.COMPANYID, app.myDriver.companyID);
                    jsonObject.put(Const.USERID, app.myDriver.userid);
                    jsonObject.put(Const.TOKEN, app.myDriver.token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sweetIndicator.show();
                currentRequest=shiftRequest;
                ApiManager.getInstance().mUrlManager.shiftOut(jsonObject.toString()).cache().
                        subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

            }
        });

    }







    Observer<Response> observerisnewJob=new Observer<Response>() {
        @Override
        public void onCompleted() {
            sweetIndicator.dismiss();
        }

        @Override

        public void onError(Throwable e) {

            displayError(e);

        }

        @Override
        public void onNext(Response response) {



            String jsonStr = Utilities.convertTypedInputToString(response.getBody());
            try {
                Log.e("error", jsonStr);
                JSONObject obj = new JSONObject(jsonStr);

                if(obj.getInt(Const.STATUS)==1){
                    JSONObject response1=obj.getJSONObject("response");
                    if(response1.getString("next_active_job").equals("yes")) {
                        sharedPrefs.setJson(Const.CURREMT_WAY_POINT, 0);
                        Job job = new Gson().fromJson(response1.getString("jobdetails"), Job.class);
                        app.apiManager.mReservedBooking = job;
                        app.apiManager.mReservedBooking.setCurrentstatus("callout");
                        app.apiManager.mReservedBooking.status1 = 0;


                        Intent myIntent = new Intent(MainActivity.this, JobViewActivity.class);

                        sharedPrefs.setJson(Const.ISJOB, true);
                        myIntent.putExtra(Const.FLAG, true);
                        myIntent.putExtra(Const.JOB_CODE, job);
                        startActivity(myIntent);


                    }



                }else{
                    SnackBarDisplay.ShowSnackBar(MainActivity.this, obj.getString(Const.ERROR_MESSAGE));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    @Override
    public void onBackPressed() {

    }

    public void jobDilaog(final Job job,String data){


        sharedPrefs.setJson(Const.JOURNYTYPE, false);
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.tick_muscic);

        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if(!MyApplication.getInstance().isAppOnline){
          moveToFront();
        }


        View outerView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_job, null);
        Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);
        Button btnCancel = (Button) outerView.findViewById(R.id.btnCancel);
        //TextView tvFair = (TextView) outerView.findViewById(R.id.tvFair);
        TextView tvTime = (TextView) outerView.findViewById(R.id.tvTime);
        TextView tvName = (TextView) outerView.findViewById(R.id.tvName);
        final TextView tvTimer = (TextView) outerView.findViewById(R.id.tvTimer);
        final TextView tvBag = (TextView) outerView.findViewById(R.id.tvBag);
        final TextView tvPass = (TextView) outerView.findViewById(R.id.tvPass);
        TextView tvPickUpLocation = (TextView) outerView.findViewById(R.id.tvPickUpLocation);
        TextView tvDropOffTitle = (TextView) outerView.findViewById(R.id.tvDropOffTitle);
        TextView tvDropOff = (TextView) outerView.findViewById(R.id.tvDropOff);
        TextView tvPickUp = (TextView) outerView.findViewById(R.id.tvPickUp);
        tvPickUp.setText(job.getPickuptime());
        tvPickUpLocation.setText(job.getPickup());
        if(!job.getDropoff().equals("")) {
            tvDropOff.setText(job.getDropoff());
            tvDropOff.setVisibility(View.VISIBLE);
            tvDropOffTitle.setVisibility(View.VISIBLE);

        }

        tvName.setText(job.getPaxname());
        tvTime.setText(job.getDuration());
        tvBag.setText(job.getBags());
        tvPass.setText(job.getPassengers());
/*
        if(!job.isprice){
            tvFair.setVisibility(View.INVISIBLE);
        }*/

        jobdilog= new Dialog(MainActivity.this,R.style.MyDialog);
        jobdilog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        jobdilog.setContentView(outerView);
        jobdilog.show();
        jobdilog.setCanceledOnTouchOutside(false);
        jobdilog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        jobdilog. getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        jobdilog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jobdilog.dismiss();
                mediaPlayer.stop();
                mediaPlayer.release();
                mTimer.cancel();
                Constants.check = true;

                try {
                    if (mReceivedReceiver != null)
                        MainActivity.this. unregisterReceiver(mReceivedReceiver);
                    MainActivity.this. unregisterReceiver(myShiftinreciver);
                    MainActivity.this. unregisterReceiver(forcelogoutreciver);
                    MainActivity.this.  unregisterReceiver(mDsipactSyncReceiver);
                    MainActivity.this. unregisterReceiver(mMessageReceivedReceiver);
                    MainActivity.this. unregisterReceiver(driverStatusSyncjobReciver);

                } catch (Exception e) {
                }

                app.apiManager.mReservedBooking = job;
                app.apiManager.mReservedBooking.setCurrentstatus("callout");
                app.apiManager.mReservedBooking.status1 = 0;
                if(job.getWhen().equals("ASAP")){

                    MyApplication.getInstance().apiManager.mSocketManager.acceptRejectBooking(1, job);

                }else {
                    app.apiManager.mReservedBooking =null;
                    MyApplication.getInstance().apiManager.mSocketManager.acceptRejectBooking(2, job);
                }


                Intent myIntent = new Intent(MainActivity.this, JobViewActivity.class);

                sharedPrefs.setJson(Const.ISJOB, true);
                myIntent.putExtra(Const.FLAG, true);
                myIntent.putExtra(Const.JOB_CODE, job);

                startActivity(myIntent);


            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jobdilog.dismiss();

                app.apiManager.mSocketManager.acceptRejectBooking(0, job);
                mediaPlayer.stop();
                mediaPlayer.release();
                mTimer.cancel();
                app.apiManager.mReservedBooking=null;

            }
        });







        mTimer=new CountDownTimer(1000 * Integer.parseInt(job.getTimer()),1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("" + millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                mediaPlayer.stop();
                mediaPlayer.release();
                mTimer.cancel();
                ApiManager.getInstance().mBooking = null;
                ApiManager.getInstance().mReservedBooking = null;
                app.apiManager.mSocketManager.acceptRejectBooking(0,job);
                jobdilog.dismiss();
            }
        };
        mTimer.cancel();
        mTimer.start();
    }


    private final BroadcastReceiver mMessageReceivedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("error", false)) {


                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.app_name))
                        .setContentText(intent.getStringExtra(Constants.RESPONSE_KEY_MESSAGE))
                        .show();
                return;
            }

            if (intent.hasExtra("data")) {
                String data = intent.getStringExtra("data");
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    ChatMessage chatMessage = new ChatMessage(false, false, "",
                            jsonObject.getString("message"), jsonObject.getString("senderid"),
                            databaseHelper.getDateTime());

                        databaseHelper.createCatagory(chatMessage);
                        Intent intent1=new Intent();

                    sendNotification(chatMessage.getContent(),intent1,"New Message");


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }
    };





    private void sendNotification(String msg, Intent i, String title)
    {
        mNotificationManager = (NotificationManager)
              getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0,
                i, PendingIntent.FLAG_ONE_SHOT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                .setSound(alarmSound);
        mBuilder.setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.cancel(Constants.PUSH_ID);
        mNotificationManager.notify(Constants.PUSH_ID, mBuilder.build());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceivedReceiver != null)
            this.unregisterReceiver(mReceivedReceiver);
        this.unregisterReceiver(myShiftinreciver);
        this.unregisterReceiver(mDsipactSyncReceiver);
        this.unregisterReceiver(mMessageReceivedReceiver);
        this.unregisterReceiver(driverStatusSyncjobReciver);
        //Toast.makeText(app, "Need to logout for closing this Application", Toast.LENGTH_SHORT).show();
        if(Constants.logoutcheck==false)
        {
           // Constants.syncheck = true;
            Intent i = new Intent(MainActivity.this,MainActivity.class);
            startActivity(i);
        }


    }







    @TargetApi(11)
    protected void moveToFront() {
        if (Build.VERSION.SDK_INT >= 11) { // honeycomb
            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

            for (int i = 0; i < recentTasks.size(); i++)
            {
                Log.d("Executed app", "Application executed : "
                        +recentTasks.get(i).baseActivity.toShortString()
                        + "\t\t ID: "+recentTasks.get(i).id+"");
                // bring to front
                if (recentTasks.get(i).baseActivity.toShortString().indexOf("mobileapps.technroid.io.cabigate") > -1) {
                    activityManager.moveTaskToFront(recentTasks.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
                }
            }
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }



}
