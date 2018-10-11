package mobileapps.technroid.io.cabigate.ui.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;
import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.api.ApiManager;
import mobileapps.technroid.io.cabigate.api.MyService;
import mobileapps.technroid.io.cabigate.global.Constants;
import mobileapps.technroid.io.cabigate.gpstrackmodule.GPSTracker;
import mobileapps.technroid.io.cabigate.helper.CommonMethods;
import mobileapps.technroid.io.cabigate.helper.Const;
import mobileapps.technroid.io.cabigate.helper.DatabaseHelper;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;
import mobileapps.technroid.io.cabigate.helper.SnackBarDisplay;
import mobileapps.technroid.io.cabigate.helper.WheelView;
import mobileapps.technroid.io.cabigate.localnotification.MyReceiver;
import mobileapps.technroid.io.cabigate.models.Job;
import mobileapps.technroid.io.cabigate.models.List;
import mobileapps.technroid.io.cabigate.models.VehicleModel;
import mobileapps.technroid.io.cabigate.ui.adapter.VeichelSelectionAdapter;
import mobileapps.technroid.io.cabigate.utilities.Utilities;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static mobileapps.technroid.io.cabigate.global.Constants.handler;
import static mobileapps.technroid.io.cabigate.global.Constants.timer;
import static mobileapps.technroid.io.cabigate.global.Constants.timerTask;

public class StartShiftActivity extends BaseActivity implements VeichelSelectionAdapter.OnItemClickListener {

    private ArrayList<List> veichlelist;
    private List selectedList;
    private ArrayList<String> hours;
    private ArrayList<String> minutes;
    private int minute = 0;
    private int hour = 0;
    private View perviousview;
    private int perviospstion = -1;
    GPSTracker gps;

    public static String Job_Tag = "my_job_tag";
    public static FirebaseJobDispatcher jobDispatcher;


    public static void openActivity(Activity activity) {

        Intent intent = new Intent(activity, StartShiftActivity.class);
        activity.startActivityForResult(intent, Const.SHIFTIN_CODE);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.do_nothing);

    }

    @OnClick(R.id.ivtimer)
    void timeDilaog() {
        hours = new ArrayList<String>();
        minutes = new ArrayList<String>();
        for (int i = 0; i <= 60; i++) {
            minutes.add(i + "");
        }
        for (int i = 0; i <= 24; i++) {
            hours.add(i + "");
        }


        View outerView = LayoutInflater.from(this).inflate(R.layout.time_selection_dilaog, null);
        final WheelView wh = (WheelView) outerView.findViewById(R.id.wheel_hours);
        Button btnSave = (Button) outerView.findViewById(R.id.btnSave);
        final WheelView wmin = (WheelView) outerView.findViewById(R.id.wheel_minute);
        wh.setOffset(2);
        wh.setItems(hours);
        wh.setSeletion(12);


        wh.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.e(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);


            }
        });

        wmin.setOffset(2);
        wmin.setItems(minutes);
        wmin.setSeletion(0);
        wmin.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                //Log.d(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);

                minute = Integer.parseInt(minutes.get(selectedIndex - 2));
            }

        });

        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setTitle("Select Shift Time")
                .setView(outerView)
        ;


        ;
        final AlertDialog ad = alBuilder.show();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minute = Integer.parseInt(minutes.get(wmin.getSeletedIndex()));
                hour = Integer.parseInt(hours.get(wh.getSeletedIndex())) * 60;

                sharedPrefs.setJson(Const.TIME, (hour + minute) + "");
                sharedPrefs.setJson(Const.TIME, true);
                tvShiftTime.setText("Shift end: " + hours.get(wh.getSeletedIndex()) + "h:" + minute + "min");
                int newminutes = hour + minute;
                if (newminutes == 0) {
                    SnackBarDisplay.ShowSnackBar(StartShiftActivity.this, "Please select ShiftIn Time");
                    return;
                }
                ad.dismiss();


            }
        });


    }

    @Bind(R.id.rv)
    RecyclerView rvFeed;

    @OnClick(R.id.ivBack)
    public void finish(View view) {
        finish();
    }

    @Bind(R.id.tvSelectedVeihlhle)
    TextView tvSelectedVeihlhle;
    @Bind(R.id.tvShiftTime)
    TextView tvShiftTime;

    @OnClick(R.id.btnCancel)
    public void confirm(View view) {

        finish();
    }

    @OnClick(R.id.btnConfirm)
    public void logout(View view) {

        if (selectedList == null) {
            SnackBarDisplay.ShowSnackBar(StartShiftActivity.this, "Please Select Vehicle First");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(Const.COMPANYID, app.myDriver.companyID);
            jsonObject.put(Const.USERID, app.myDriver.userid);
            jsonObject.put(Const.TOKEN, app.myDriver.token);
            jsonObject.put(Const.VEHICLEID, selectedList.vehicleid);
            jsonObject.put(Const.LAT, gps.getLatitude());
            jsonObject.put(Const.LNG, gps.getLongitude());
            jsonObject.put(Const.BEARING, app.getBearing());
            if (!sharedPrefs.isJson(Const.TIME)) {
                jsonObject.put(Const.DURATION, "720");
            } else {
                jsonObject.put(Const.DURATION, sharedPrefs.getJson(Const.TIME));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int newminutes1 = hour + minute;
        if (newminutes1 <= 0) {
            SnackBarDisplay.ShowSnackBar(StartShiftActivity.this, "Please select ShiftIn Time");
            return;
        }
        sweetIndicator.show();
        //schedulerTask();
        dispatchjob();
        //startService(new Intent(this, ConnectSocketService.class));
        ApiManager.getInstance().mUrlManager.shiftIN(jsonObject.toString()).cache().
                subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observerShifIn);

    }
    public void dispatchjob()
    {
        com.firebase.jobdispatcher.Job job = jobDispatcher.newJobBuilder().
                setService(MyService.class).
                setLifetime(Lifetime.FOREVER).
                setRecurring(true).
                setTag(Job_Tag).
                setTrigger(Trigger.executionWindow(10, 20)).
                setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL).
                setReplaceCurrent(true).
                setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        jobDispatcher.mustSchedule(job);
    }
   public void schedulerTask()
    {
        //Constants.syncheck = true;
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
                        SharedPrefs sharedPrefs = new SharedPrefs(StartShiftActivity.this);
                        ConnectivityManager cm = (ConnectivityManager) StartShiftActivity.this
                                .getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null
                                && activeNetwork.isConnectedOrConnecting();
                        if (isConnected) {
                            if (sharedPrefs.isLogin()) {
                                ApiManager.mSocketManager.connectSocketManager();
                                //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                                 Toast.makeText(StartShiftActivity.this.getApplicationContext(),"Job Scheduled", Toast.LENGTH_SHORT).show();               }

                        }

                    }
                });
            }
        };
    }


    private VeichelSelectionAdapter veichelSelectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addBroadcastReceivers();
        setContentView(R.layout.activity_jobshift);
        jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        JSONObject jsonObject = new JSONObject();
        gps = new GPSTracker(this, StartShiftActivity.this);

        try {

            jsonObject.put(Const.COMPANYID, app.myDriver.companyID);
            jsonObject.put(Const.USERID, app.myDriver.userid);
            jsonObject.put(Const.TOKEN, app.myDriver.token);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiManager.getInstance().mUrlManager.getVehicle(jsonObject.toString()).cache().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observertest);

        rvFeed.setHasFixedSize(true);
        rvFeed.setLayoutManager(new LinearLayoutManager(StartShiftActivity.this, LinearLayoutManager.VERTICAL, false));


        veichlelist = new ArrayList<List>();
        veichelSelectionAdapter = new VeichelSelectionAdapter(StartShiftActivity.this, veichlelist);
        rvFeed.setAdapter(veichelSelectionAdapter);
        veichelSelectionAdapter.setOnItemClickListener(this);
        if (!sharedPrefs.isJson(Const.SHIFTIN)) {
            if (sharedPrefs.getJson(Const.SHIFTINTIME) != null) {
                tvShiftTime.setText("Shift end: " + sharedPrefs.getJson(Const.SHIFTINTIME));
                minute = Integer.parseInt(sharedPrefs.getJson(Const.TIME));
            } else {

                hour = 720;
                sharedPrefs.setJson(Const.SHIFTINTIME, (720 / 60) + "h:" + minute + "min");
                tvShiftTime.setText("Shift end: " + sharedPrefs.getJson(Const.SHIFTINTIME));

                sharedPrefs.setJson(Const.TIME, true);
                sharedPrefs.setJson(Const.TIME, "720");
            }
        }

    }


    Observer<Response> observerShifIn = new Observer<Response>() {
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
            sweetIndicator.dismiss();
            String jsonStr = Utilities.convertTypedInputToString(response.getBody());
            try {
                Log.e("error", jsonStr);
                JSONObject obj = new JSONObject(jsonStr);

                if (obj.getInt(Const.STATUS) == 1) {

                    sharedPrefs.setJson(Const.SHIFTINTIME, (hour / 60) + "h:" + minute + "min");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.MINUTE, Integer.parseInt(sharedPrefs.getJson(Const.TIME)));
                    sharedPrefs.setJson(Const.SHIFT_END_TIME, calendar.getTimeInMillis() + "");
                    genrateNotification(Integer.parseInt(sharedPrefs.getJson(Const.TIME)));
                    sharedPrefs.setJson(Const.SHIFTIN, true);
                    setResult(RESULT_OK);
                    JSONObject response1 = obj.getJSONObject("response");
                    sharedPrefs.setJson(Const.OFFER, obj.getJSONObject("response").getInt("offerscount"));

                    if (response1.getString("next_active_job").equals("yes")) {
                        sharedPrefs.setJson(Const.CURREMT_WAY_POINT, 0);
                        Job job = new Gson().fromJson(response1.getString("jobdetails"), Job.class);
                        app.apiManager.mReservedBooking = job;
                        app.apiManager.mReservedBooking.setCurrentstatus("callout");
                        app.apiManager.mReservedBooking.status1 = 0;


                        Intent myIntent = new Intent(StartShiftActivity.this, JobViewActivity.class);

                        sharedPrefs.setJson(Const.ISJOB, true);
                        myIntent.putExtra(Const.FLAG, true);
                        myIntent.putExtra(Const.JOB_CODE, job);
                        startActivity(myIntent);
                    } else {
                        finish();
                    }
                    //

                } else {
                    SnackBarDisplay.ShowSnackBar(StartShiftActivity.this, obj.getString(Const.ERROR_MESSAGE));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };


    private Observer<Response> observertest = new Observer<Response>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable error) {
            RetrofitError e = (RetrofitError) error;
            if (e.getKind() == RetrofitError.Kind.NETWORK) {
                SnackBarDisplay.ShowSnackBar(StartShiftActivity.this, getResources().getString(R.string.error_network_subtitle));

            } else if (e.getKind() == RetrofitError.Kind.HTTP) {
                SnackBarDisplay.ShowSnackBar(StartShiftActivity.this, getResources().getString(R.string.error_server_subtitle));


            } else {
                SnackBarDisplay.ShowSnackBar(StartShiftActivity.this, getResources().getString(R.string.error_uncommon_subtitle));

            }
        }

        @Override
        public void onNext(Response response) {
            String jsonStr = Utilities.convertTypedInputToString(response.getBody());

            Log.e("error", jsonStr);
            try {
                Gson gson = new Gson();
                JSONObject obj = new JSONObject(jsonStr);
                VehicleModel vehiclemodel = gson.fromJson(obj.toString(), VehicleModel.class);

                if (vehiclemodel.status == 1) {

                    veichlelist.addAll(vehiclemodel.response.list);
                    veichelSelectionAdapter.notifyDataSetChanged();

                } else {
                    // SnackBarDisplay.ShowSnackBar(LoginActivity.this,driverModel.error_msg);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    @Override
    public void onItemClick(View view, int position) {

        selectedList = veichlelist.get(position);
        if (!selectedList.status.equals("booked")) {


            if (perviousview != null && perviospstion != -1) {

                perviousview.setBackgroundColor(Color.TRANSPARENT);
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));


            } else {
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));

            }

            perviousview = view;
            perviospstion = position;


            CommonMethods.setTextSpanColor(tvSelectedVeihlhle, "Selected:" + selectedList.name, selectedList.name, getResources().getColor(R.color.colorAccent));
        } else {
            SnackBarDisplay.ShowSnackBar(StartShiftActivity.this, selectedList.name + " already booked");
        }
    }


    void genrateNotification(int minute) {

        int _id = (int) System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        Intent myIntentbeborestrat = new Intent(StartShiftActivity.this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(StartShiftActivity.this, 1, myIntentbeborestrat, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) StartShiftActivity.this.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

    }


    private final BroadcastReceiver forcelogoutreciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("data")) {


                String type = intent.getStringExtra("data");
                sharedPrefs.setJson(Const.SHIFTIN, false);

                if (type.equals("shift")) {
                    sharedPrefs.setJson(Const.SHIFTIN, false);
                    finish();

                } else if (type.equals("logout")) {
                    if (app.isAppOnline) {
                        DatabaseHelper databaseHelper = new DatabaseHelper(StartShiftActivity.this);
                        databaseHelper.deleteAllHIatorY();
                        sharedPrefs.clearSharedprefs();
                        app.apiManager.mSocketManager.broadCastOffline();
                        app.stopService();
                        app.mGoogleApiClient.disconnect();
                        Intent intent1 = new Intent(StartShiftActivity.this, LoginActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                    } else {
                        DatabaseHelper databaseHelper = new DatabaseHelper(StartShiftActivity.this);
                        databaseHelper.deleteAllHIatorY();
                        sharedPrefs.clearSharedprefs();
                        app.apiManager.mSocketManager.broadCastOffline();
                        app.stopService();
                        app.mGoogleApiClient.disconnect();
                    }
                } else if (type.equals("zone")) {

                    sharedPrefs.setJson("myqueue", false);

                }
            }
        }


    };

    private void addBroadcastReceivers() {

        IntentFilter forcelogoutreciverfilter = new IntentFilter(Constants.ACTION_FORCE);
        registerReceiver(forcelogoutreciver, forcelogoutreciverfilter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(forcelogoutreciver);
    }
}
