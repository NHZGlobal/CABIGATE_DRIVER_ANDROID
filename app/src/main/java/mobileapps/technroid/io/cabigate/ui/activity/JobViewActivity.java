package mobileapps.technroid.io.cabigate.ui.activity;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.nekocode.badge.BadgeDrawable;
import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.api.ApiManager;
import mobileapps.technroid.io.cabigate.app.MyApplication;
import mobileapps.technroid.io.cabigate.global.Constants;
import mobileapps.technroid.io.cabigate.gpstrackmodule.ConnectivityReceiver;
import mobileapps.technroid.io.cabigate.helper.Const;
import mobileapps.technroid.io.cabigate.helper.DatabaseHelper;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;
import mobileapps.technroid.io.cabigate.helper.SnackBarDisplay;
import mobileapps.technroid.io.cabigate.localnotification.MyReceiver;
import mobileapps.technroid.io.cabigate.models.ChatMessage;
import mobileapps.technroid.io.cabigate.models.Job;
import mobileapps.technroid.io.cabigate.ui.fragment.FragmentChat;
import mobileapps.technroid.io.cabigate.ui.fragment.FragmentCurrentNew;
import mobileapps.technroid.io.cabigate.ui.fragment.FragmentDispatch;
import mobileapps.technroid.io.cabigate.ui.fragment.FragmentOffers;
import mobileapps.technroid.io.cabigate.ui.fragment.FragmentQueue;
import mobileapps.technroid.io.cabigate.ui.widgets.alertbox.SweetAlertDialog;
import mobileapps.technroid.io.cabigate.utilities.Utilities;
import retrofit.client.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static mobileapps.technroid.io.cabigate.global.Constants.backcheck;


public final class JobViewActivity extends BaseActivity implements OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {


    private static boolean isBottomBarVissible;
    @Bind(R.id.btmBtnCurrent)
    Button btmBtnCurrent;
    @Bind(R.id.btmBtnQueue)
    Button btmBtnQueue;
    @Bind(R.id.btmBtnOffer)
    Button btmBtnOffer;
    @Bind(R.id.btmBtnMap)
    Button btmBtnMap;
    @Bind(R.id.btmBtnMessaging)
    Button btmBtnMessaging;
    @Bind(R.id.btmBtnDispatch)
    Button btmBtnDispatch;
    private Context mContext;

    public static ImageView ivLoctonBtn;
    @Bind(R.id.ivBadge)
    TextView ivBadge;
    private DatabaseHelper databaseHelper;
    RelativeLayout rellayout;

    @OnClick(R.id.ivBack)
    void goBack() {
        backcheck = true;
        if (!sharedPrefs.isJson(Const.ISJOB)) {
            super.onBackPressed();

        } else {
            backcheck = false;
            Utilities.showToast(JobViewActivity.this, "Please complete active job before move to main screen");
        }

    }


    private Fragment fragment;
    private CountDownTimer mTimer;


    public static boolean isBottomBarVissible() {
        return isBottomBarVissible;
    }


    public static FragmentManager fragmentManager;


    private SharedPrefs sharedPrefs;


    public static TextView tv_title;
    ///public static ImageButton ib_map_option;

    public static View top_layout;
    private Drawable drawaselectedplaceorder;
    private boolean mVisible;


    public static void openActivity(Activity activity) {

        Intent intent = new Intent(activity, JobViewActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.do_nothing);


    }


    private void addBroadcastReceivers() {
        IntentFilter filter = new IntentFilter(Constants.ACTION_REQUEST_ORDER);
        IntentFilter filtermessagerevice = new IntentFilter(Constants.ACTION_RECIVE_MESSAGE);
        IntentFilter forceFilter = new IntentFilter(Constants.ACTION_FORCE);
        JobViewActivity.this.registerReceiver(forcelogoutreciver, forceFilter);
        IntentFilter offercountFilter = new IntentFilter(Constants.ACTION_OFFER_COUNT);
        JobViewActivity.this.registerReceiver(offercount, offercountFilter);
        IntentFilter dispatchsyncjob = new IntentFilter(Constants.ACTION_REQUEST_SYNC_JOB);
        IntentFilter driverstatussyncjob = new IntentFilter(Constants.ACTION_SYNC_DRVIER_STATUS);
       JobViewActivity.this.registerReceiver(mReceivedReceiver, filter);
        //moveTaskToBack(true);
     registerReceiver(mMessageReceivedReceiver, filtermessagerevice);
        JobViewActivity.this. registerReceiver(mDsipactSyncReceiver, dispatchsyncjob);
        JobViewActivity.this. registerReceiver(driverStatusSyncjobReciver, driverstatussyncjob);
    }


    private final BroadcastReceiver mReceivedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("error", false)) {


                new SweetAlertDialog(JobViewActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.app_name))
                        .setContentText(intent.getStringExtra(Constants.RESPONSE_KEY_MESSAGE))
                        .show();
                return;
            }

            if (intent.hasExtra("data")) {


                String data = intent.getStringExtra("data");
                ApiManager.getInstance().mBooking = new Gson().fromJson(data, Job.class);


                if (fragment instanceof FragmentCurrentNew) {
                    FragmentCurrentNew my = (FragmentCurrentNew) fragment;
                    //pass intent or its data to the fragment's method
                    if (sharedPrefs.isJson(Const.SHIFTIN))

                        my.jobDilaog(ApiManager.getInstance().mBooking);
                } else {

                    if (sharedPrefs.isJson(Const.SHIFTIN)) {
                        fragment = FragmentCurrentNew.newInstance(true, true, ApiManager.getInstance().mBooking);
                        dashBordButtonEnabel(btmBtnCurrent);
                        drawaselectedplaceorder = getResources().getDrawable(
                                R.drawable.current_nav_sel);

                        btmBtnCurrent.setCompoundDrawablesWithIntrinsicBounds(null, drawaselectedplaceorder,
                                null, null);

                        fragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment_container, fragment
                                        , "FragmentDashBoard")
                                .commitAllowingStateLoss();
                    }
                }

            }


        }
    };


    private final BroadcastReceiver driverStatusSyncjobReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("error", false)) {


                new SweetAlertDialog(JobViewActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.app_name))
                        .setContentText(intent.getStringExtra(Constants.RESPONSE_KEY_MESSAGE))
                        .show();
                return;
            }

            if (!intent.hasExtra("data")) {
                return;
            }
            String data = intent.getStringExtra("data");

            if (sharedPrefs.isJson(Const.SHIFTIN)) {
                Job job = new Gson().fromJson(data, Job.class);
                if (fragment instanceof FragmentCurrentNew) {
                    FragmentCurrentNew my = (FragmentCurrentNew) fragment;

                    if (job.status.equals("free")) {
                        FragmentCurrentNew.status = 6;
                        my.updateDriverStatus("free", "0");
                        sharedPrefs.setJson(Const.AWAYTIME, false);

                    } else if (job.status.equals("busy")) {
                        FragmentCurrentNew.status = -1;
                        my.updateDriverStatus("busy", "0");
                        sharedPrefs.setJson(Const.AWAYTIME, false);
                    } else if (job.status.equals("away")) {
                        my.awayStatussync(job.awaytime);
                    }
                } else {
                    currentRequest = MainActivity.driverStatusRequest;
                    if (job.status.equals("free")) {
                        FragmentCurrentNew.status = 6;
                        updateDriverStatus("free", "0");
                        sharedPrefs.setJson(Const.AWAYTIME, false);

                    } else if (job.status.equals("busy")) {
                        updateDriverStatus("busy", "0");
                        sharedPrefs.setJson(Const.AWAYTIME, false);
                    } else if (job.status.equals("away")) {
                        awayStatussync(job.awaytime);
                    }


                }
            }
        }
    };


    public void awayStatussync(int time) {
        currentRequest = MainActivity.driverStatusRequest;
        //sweetIndicator.show();
        updateDriverStatus("away", "" + time);

        sharedPrefs.setJson(Const.AWAYSTARTTIME, new Date().getTime() + "");
        sharedPrefs.setJson(Const.AWAYTIME, (60 * time) + "");
        sharedPrefs.setJson(Const.AWAYTIME, true);

    }


    public void updateDriverStatus(String status, String time) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Const.COMPANYID, MyApplication.getInstance().myDriver.companyID);
            jsonObject.put(Const.USERID, MyApplication.getInstance().myDriver.userid);
            jsonObject.put(Const.TOKEN, MyApplication.getInstance().myDriver.token);
            jsonObject.put("eta", time);
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


                new SweetAlertDialog(JobViewActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.app_name))
                        .setContentText(intent.getStringExtra(Constants.RESPONSE_KEY_MESSAGE))
                        .show();
                return;
            }

            if (!intent.hasExtra("data")) {
                return;
            }
            String data = intent.getStringExtra("data");
            Job testJOb = MyApplication.getInstance().apiManager.mReservedBooking;

            Job job = new Gson().fromJson(data, Job.class);
                /*This COde Woluld be used further*/
            Intent myIntent = new Intent(JobViewActivity.this, JobViewActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            myIntent.putExtra("data", data.toString());
                /*This COde Woluld be used further*/
            job.issync = true;

            Log.e("TestJob", testJOb + "");
            if (job.status.equals("pob")) {
                job.setCurrentstatus("pob");
                job.status1 = 2;
            } else if (job.status.equals("callout")) {
                job.setCurrentstatus("callout");
                job.status1 = 0;
            } else if (job.status.equals("wait")) {
                job.status1 = 1;
                job.setCurrentstatus("wait");
            } else {
                job.status1 = 4;
            }


            if (sharedPrefs.isJson(Const.SHIFTIN)) {

                if (fragment instanceof FragmentCurrentNew) {
                    FragmentCurrentNew my = (FragmentCurrentNew) fragment;
                    //pass intent or its data to the fragment's method

                    if (MyApplication.getInstance().apiManager.mReservedBooking != null) {

								/*When Both Jobs ARE SAME*/
                        if (job.getJobid().equals(MyApplication.getInstance().apiManager.mReservedBooking.getJobid())) {
                            if (job.status1 == 4) {
                                job = null;
                            }
                            MyApplication.getInstance().apiManager.mReservedBooking = job;
                            my.syncJob(MyApplication.getInstance().apiManager.mReservedBooking);
                        }
									/*END  Both Jobs ARE SAME*/
                    } else {
                        if (job.status1 == 4) {
                            job = null;
                        }
                        if (job != null) {
                            MyApplication.getInstance().apiManager.mReservedBooking = job;
                            my.syncJob(MyApplication.getInstance().apiManager.mReservedBooking);
                        }
                    }

                } else {
                    if (MyApplication.getInstance().apiManager.mReservedBooking != null) {
                        if (job.getJobid().equals(MyApplication.getInstance().apiManager.mReservedBooking.getJobid())) {
                            if (job.status1 == 4) {
                                //job=null;
                            }
                            MyApplication.getInstance().apiManager.mReservedBooking = job;
                            moveToCurrentSection(MyApplication.getInstance().apiManager.mReservedBooking);
                        }
                    } else {
                        if (job.status1 == 4) {
                            job = null;
                        }
                        if (job != null) {
                            MyApplication.getInstance().apiManager.mReservedBooking = job;
                            moveToCurrentSection(job);
                        }
                    }
                }

            }


        }
    };


    private final BroadcastReceiver mMessageReceivedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("error", false)) {


                new SweetAlertDialog(JobViewActivity.this, SweetAlertDialog.ERROR_TYPE)
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


                    if (fragment instanceof FragmentChat) {
                        FragmentChat my = (FragmentChat) fragment;
                        my.mimicOtherMessage(chatMessage);

                    } else {

                        if (sharedPrefs.isJson(Const.SHIFTIN)) {
                            ivLoctonBtn.setImageResource(R.drawable.top_sos_btn);


                            dashBordButtonEnabel(btmBtnMessaging);
                            fragment = FragmentChat.newInstance(chatMessage);

                            fragmentManager
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, fragment
                                            , "FragmentChat")
                                    .commitAllowingStateLoss();
                            drawaselectedplaceorder = getResources().getDrawable(
                                    R.drawable.chat_nav_sel);

                            btmBtnMessaging.setCompoundDrawablesWithIntrinsicBounds(null, drawaselectedplaceorder,
                                    null, null);


                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                            mp.start();


                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        addBroadcastReceivers();
        MyApplication.getInstance().setConnectivityListener(this);
        ApiManager.mSocketManager.connectSocket();
        if (!sharedPrefs.isLogin()) {
            app.apiManager.mSocketManager.broadCastOffline();
            app.stopService();
            app.mGoogleApiClient.disconnect();
            Intent intent1 = new Intent(JobViewActivity.this, LoginActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        sharedPrefs = new SharedPrefs(JobViewActivity.this);
        databaseHelper = new DatabaseHelper(JobViewActivity.this);
        backcheck = false;

        //CommonMethods.changeLang(sharedPrefs.getLocale(), HomeActivty.this);

        setContentView(R.layout.activity_job_view);
        fragmentManager = getSupportFragmentManager();

        ivLoctonBtn = (ImageView) findViewById(R.id.ivLoctonBtn);
        rellayout = (RelativeLayout) findViewById(R.id.mainContainer);
        btmBtnCurrent.setOnClickListener(this);
        btmBtnQueue.setOnClickListener(this);
        btmBtnOffer.setOnClickListener(this);
        btmBtnDispatch.setOnClickListener(this);
        btmBtnMessaging.setOnClickListener(this);
        btmBtnMap.setOnClickListener(this);
        ivLoctonBtn.setOnClickListener(this);
        BadgeDrawable drawable2 =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                        .badgeColor(Color.RED)
                        .textSize(sp2px(JobViewActivity.this, 14))
                        .text1(sharedPrefs.getJsonInt(Const.OFFER) + "")
                        .build();

        SpannableString spannableString =
                new SpannableString(TextUtils.concat(
                        drawable2.toSpannable()));

        //
        ivBadge.setText(spannableString);
        ivBadge.setText(spannableString);

        if (sharedPrefs.getJsonInt(Const.OFFER) <= 0) {
            ivBadge.setVisibility(View.GONE);
        } else {
            ivBadge.setVisibility(View.VISIBLE);
        }
        boolean flag = getIntent().getBooleanExtra(Const.FLAG, false);
        boolean isnotification = getIntent().getBooleanExtra(Const.ISNOTIFICTION, false);


        if (savedInstanceState == null) {


            if (flag) {
                Job job = (Job) getIntent().getSerializableExtra(Const.JOB_CODE);


                fragment = FragmentCurrentNew.newInstance(flag, isnotification, job);
                dashBordButtonEnabel(btmBtnCurrent);
                drawaselectedplaceorder = getResources().getDrawable(
                        R.drawable.current_nav_sel);

                btmBtnCurrent.setCompoundDrawablesWithIntrinsicBounds(null, drawaselectedplaceorder,
                        null, null);

                fragmentManager
                        .beginTransaction()
                        .add(R.id.fragment_container, fragment
                                , "FragmentDashBoard")
                        .commit();

                ivLoctonBtn.setImageResource(R.drawable.add_ic);
            } else {
                fragment = FragmentCurrentNew.newInstance(flag, false, new Job());
                dashBordButtonEnabel(btmBtnCurrent);
                drawaselectedplaceorder = getResources().getDrawable(
                        R.drawable.current_nav_sel);
                btmBtnCurrent.setCompoundDrawablesWithIntrinsicBounds(null, drawaselectedplaceorder,
                        null, null);
                fragmentManager
                        .beginTransaction()
                        .add(R.id.fragment_container, fragment
                                , "FragmentDashBoard")
                        .commit();

                ivLoctonBtn.setImageResource(R.drawable.add_ic);
            }

        }


        addBroadcastReceivers();

    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public void dashBordButtonEnabel(Button btn) {

        btmBtnCurrent.setTextColor(getResources().getColor(R.color.menutxt));
        btmBtnOffer.setTextColor(getResources().getColor(R.color.menutxt));
        btmBtnQueue.setTextColor(getResources().getColor(R.color.menutxt));
        btmBtnMap.setTextColor(getResources().getColor(R.color.menutxt));
        btmBtnMessaging.setTextColor(getResources().getColor(R.color.menutxt));
        btmBtnDispatch.setTextColor(getResources().getColor(R.color.menutxt));
        btn.setTextColor(getResources().getColor(R.color.white));
        Drawable offer = getResources().getDrawable(R.drawable.offers_nav_unsel);
        Drawable queue = getResources().getDrawable(R.drawable.queue_nav_unsel);
        Drawable current = getResources().getDrawable(R.drawable.current_nav_unsel);
        Drawable map = getResources().getDrawable(R.drawable.map_nav_unsel);
        Drawable dispatch = getResources().getDrawable(
                R.drawable.dispatch_nav_unsel);
        Drawable messaging = getResources().getDrawable(
                R.drawable.chat_nav_unsel);
        btmBtnOffer.setCompoundDrawablesWithIntrinsicBounds(null, offer,
                null, null);
        btmBtnCurrent.setCompoundDrawablesWithIntrinsicBounds(null, current, null, null);
        btmBtnMap.setCompoundDrawablesWithIntrinsicBounds(null, map, null,
                null);
        btmBtnQueue.setCompoundDrawablesWithIntrinsicBounds(null,
                queue, null, null);
        btmBtnDispatch.setCompoundDrawablesWithIntrinsicBounds(null,
                dispatch, null, null);
        btmBtnMessaging.setCompoundDrawablesWithIntrinsicBounds(null,
                messaging, null, null);


        btmBtnOffer.setEnabled(true);
        btmBtnCurrent.setEnabled(true);
        btmBtnQueue.setEnabled(true);
        btmBtnDispatch.setEnabled(true);
        btmBtnMap.setEnabled(true);
        btmBtnMessaging.setEnabled(true);
        btn.setEnabled(false);
        if (!app.myDriver.showDispatcher.equals("yes")) {
            btmBtnDispatch.setEnabled(false);
        }


    }


    public void moveToCurrentSection(Job job) {
        fragment = FragmentCurrentNew.newInstance(false, true, job);
        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment
                        , "FragmentDashBoard")
                .commit();
        drawaselectedplaceorder = getResources().getDrawable(
                R.drawable.current_nav_sel);
        btmBtnCurrent.setCompoundDrawablesWithIntrinsicBounds(null, drawaselectedplaceorder,
                null, null);
        dashBordButtonEnabel(btmBtnCurrent);
    }


    public void moveToQueueSection() {
        fragmentManager.popBackStack(null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        dashBordButtonEnabel(btmBtnQueue);
        fragment = new FragmentQueue();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container, fragment, "FragmentQueue")
                .commit();

        drawaselectedplaceorder = getResources().getDrawable(
                R.drawable.queue_nav_sel);

        btmBtnQueue.setCompoundDrawablesWithIntrinsicBounds(null, drawaselectedplaceorder,
                null, null);

    }

    @Override
    public void onClick(View view) {

        // TODO Auto-generated method stub
        fragmentManager.popBackStack(null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);

        switch (view.getId()) {
            case R.id.ivLoctonBtn:

                if (fragment instanceof FragmentCurrentNew) {
                    FragmentCurrentNew my = (FragmentCurrentNew) fragment;
                    my.jobAddDialog();

                }
                if (fragment instanceof FragmentChat) {
                    sosDialog();
                }

                break;
            case R.id.btmBtnCurrent:

                ivLoctonBtn.setImageResource(R.drawable.top_location_btn);
                fragmentManager.popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                dashBordButtonEnabel(btmBtnCurrent);
                if (MyApplication.getInstance().apiManager.mReservedBooking != null) {
                    fragment = FragmentCurrentNew.newInstance(true, false, MyApplication.getInstance().apiManager.mReservedBooking);
                } else {
                    fragment = FragmentCurrentNew.newInstance(false, false, new Job());
                }

                fragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.fragment_container, fragment
                                ,
                                "FragmentCurrentNew").commit();


                drawaselectedplaceorder = getResources().getDrawable(
                        R.drawable.current_nav_sel);

                btmBtnCurrent.setCompoundDrawablesWithIntrinsicBounds(null, drawaselectedplaceorder,
                        null, null);

                break;

            case R.id.btmBtnOffer:
                ivLoctonBtn.setImageResource(R.drawable.top_location_btn);

                fragmentManager.popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                dashBordButtonEnabel(btmBtnOffer);
                fragment = new FragmentOffers();
                fragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.fragment_container, fragment,
                                "FragmentOffers").commit();


                drawaselectedplaceorder = getResources().getDrawable(
                        R.drawable.offers_nav_sel);

                btmBtnOffer.setCompoundDrawablesWithIntrinsicBounds(null, drawaselectedplaceorder,
                        null, null);

                break;

            case R.id.btmBtnQueue:
                ivLoctonBtn.setImageResource(R.drawable.top_location_btn);

                fragmentManager.popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                dashBordButtonEnabel(btmBtnQueue);
                fragment = new FragmentQueue();
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.fragment_container, fragment, "FragmentQueue")
                        .commit();

                drawaselectedplaceorder = getResources().getDrawable(
                        R.drawable.queue_nav_sel);

                btmBtnQueue.setCompoundDrawablesWithIntrinsicBounds(null, drawaselectedplaceorder,
                        null, null);


                break;
            case R.id.btmBtnMap:
                //ib_map_option.setVisibility(View.GONE);

                dashBordButtonEnabel(btmBtnMap);


                drawaselectedplaceorder = getResources().getDrawable(
                        R.drawable.map_nav_sel);

                btmBtnMap.setCompoundDrawablesWithIntrinsicBounds(null, drawaselectedplaceorder,
                        null, null);

                break;

            case R.id.btmBtnDispatch:

                ivLoctonBtn.setImageResource(R.drawable.top_location_btn);
                fragmentManager.popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                dashBordButtonEnabel(btmBtnDispatch);

                fragment = new FragmentDispatch();
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.fragment_container, fragment, "FragmentDispatch").commit();


                drawaselectedplaceorder = getResources().getDrawable(
                        R.drawable.dispatch_nav_sel);

                btmBtnDispatch.setCompoundDrawablesWithIntrinsicBounds(null, drawaselectedplaceorder,
                        null, null);

                break;
            case R.id.btmBtnMessaging:
                ivLoctonBtn.setImageResource(R.drawable.top_sos_btn);

                fragmentManager.popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                dashBordButtonEnabel(btmBtnMessaging);
                fragment = new FragmentChat();

                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.fragment_container, fragment, "FragmentChat").commit();


                drawaselectedplaceorder = getResources().getDrawable(
                        R.drawable.chat_nav_sel);

                btmBtnMessaging.setCompoundDrawablesWithIntrinsicBounds(null, drawaselectedplaceorder,
                        null, null);

                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        fragment = null;
        if (mMessageReceivedReceiver != null)
            this.unregisterReceiver(mMessageReceivedReceiver);
        if (mDsipactSyncReceiver != null)
            this.unregisterReceiver(mDsipactSyncReceiver);
        this.unregisterReceiver(forcelogoutreciver);
        this.unregisterReceiver(offercount);
        this.unregisterReceiver(driverStatusSyncjobReciver);

           // this.unregisterReceiver(mReceivedReceiver);
       if(mReceivedReceiver!=null) {
            this.unregisterReceiver(mReceivedReceiver);
        }
        if (Constants.logoutcheck == false) {

            if (backcheck) {
                backcheck = false;
            } else {
                Intent i = new Intent(JobViewActivity.this,MainActivity.class);
                startActivity(i);
            }


        }
        sharedPrefs.setJson("latestjob", true);


    }


    @Override
    public void onPause() {
        super.onPause();


    }



    @Override
    public void onBackPressed() {
        backcheck = true;
        if (!sharedPrefs.isJson(Const.ISJOB)) {
            super.onBackPressed();
        } else {
            backcheck = false;
            Utilities.showToast(JobViewActivity.this, "Please complete active job before move to main screen");
        }


    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message = "";
        //int color;
        if (isConnected) {
            SnackBarDisplay.ShowSnackBarLong(JobViewActivity.this, message, false);
		/*	message = "Good! Connected to Internet";
			color = Color.WHITE;*/
        } else {
            message = "Searching Network.......";
            SnackBarDisplay.ShowSnackBarLong(JobViewActivity.this, message, true);

        }


    }

    public void sosDialog() {


        View outerView = LayoutInflater.from(JobViewActivity.this).inflate(R.layout.chat_sos_dialog, null);
        Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);
        Button btnCancel = (Button) outerView.findViewById(R.id.btnCancel);

        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(JobViewActivity.this);

        alBuilder.setView(outerView)
        ;
        final AlertDialog ad = alBuilder.show();

        ad.setCancelable(false);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();


            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();

                try {
                    app.apiManager.mSocketManager.sendSosMessage("dispatcher");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }


    @TargetApi(11)
    protected void moveToFront() {
        if (Build.VERSION.SDK_INT >= 11) { // honeycomb
            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

            for (int i = 0; i < recentTasks.size(); i++) {
                Log.d("Executed app", "Application executed : "
                        + recentTasks.get(i).baseActivity.toShortString()
                        + "\t\t ID: " + recentTasks.get(i).id + "");
                // bring to front
                if (recentTasks.get(i).baseActivity.toShortString().indexOf("mobileapps.technroid.io.cabigate") > -1) {
                    activityManager.moveTaskToFront(recentTasks.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
                }
            }
        }
    }


    class RecentTag {
        ActivityManager.RecentTaskInfo info;
        Intent intent;
    }

    private void switchTo(RecentTag tag) {
        if (tag.info.id >= 0) {
            // This is an active task; it should just go to the foreground.
            final ActivityManager am = (ActivityManager)
                    getSystemService(Context.ACTIVITY_SERVICE);
            am.moveTaskToFront(tag.info.id, ActivityManager.MOVE_TASK_WITH_HOME);
        } else if (tag.intent != null) {
            tag.intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
                    | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            try {
                startActivity(tag.intent);
            } catch (ActivityNotFoundException e) {
                Log.w("Recent", "Unable to launch recent task", e);
            }
        }
    }


    private int currentRequest = -1;
    Observer<Response> observer = new Observer<Response>() {
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
                if (obj.getInt(Const.STATUS) == 1) {
                    switch (currentRequest) {
                        case MainActivity.logoutRequest:
                            DatabaseHelper databaseHelper = new DatabaseHelper(JobViewActivity.this);
                            databaseHelper.deleteAllHIatorY();
                            sharedPrefs.clearSharedprefs();
                            app.apiManager.mSocketManager.broadCastOffline();
                            app.stopService();
                            app.mGoogleApiClient.disconnect();
                            Intent intent = new Intent(JobViewActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            break;
                        case MainActivity.zooneCheckOutRequest:
                            sharedPrefs.setJson("myqueue", false);
                            if (fragment instanceof FragmentQueue) {
                                FragmentQueue my = (FragmentQueue) fragment;
                                my.zoneCheckOut();

                            }

                            break;
                        case MainActivity.shiftRequest:
                            sharedPrefs.setJson(Const.SHIFTIN, false);
                            if (fragment instanceof FragmentCurrentNew) {
                                FragmentCurrentNew my = (FragmentCurrentNew) fragment;
                                my.isShifin();

                            }

                            break;

                    }

                } else {
                    SnackBarDisplay.ShowSnackBar(JobViewActivity.this, obj.getString(Const.ERROR_MESSAGE));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };


    private final BroadcastReceiver forcelogoutreciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("data")) {


                String type = intent.getStringExtra("data");
                sharedPrefs.setJson(Const.SHIFTIN, false);

                if (type.equals("shift")) {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Const.COMPANYID, app.myDriver.companyID);
                        jsonObject.put(Const.USERID, app.myDriver.userid);
                        jsonObject.put(Const.TOKEN, app.myDriver.token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    currentRequest = MainActivity.shiftRequest;
                    ApiManager.getInstance().mUrlManager.shiftOut(jsonObject.toString()).cache().
                            subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

                    sharedPrefs.setJson(Const.SHIFTIN, false);
               /*     tvTime.setText("");
                    btnJObShift.setText("Start Shift");*/
                    cancelAlarmManager();
                } else if (type.equals("logout")) {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Const.COMPANYID, app.myDriver.companyID);
                        jsonObject.put(Const.USERID, app.myDriver.userid);
                        jsonObject.put(Const.TOKEN, app.myDriver.token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    currentRequest = MainActivity.logoutRequest;
                    //sweetIndicator.show();
                    MyApplication.getInstance().apiManager.getInstance().mUrlManager.logout(jsonObject.toString()).cache().
                            subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

                } else if (type.equals("zone")) {

                    sharedPrefs.setJson("myqueue", false);
                    JSONObject jsonObject = new JSONObject();
                    try {

                        jsonObject.put(Const.USERID, MyApplication.getInstance().myDriver.userid);
                        jsonObject.put(Const.COMPANYID, MyApplication.getInstance().myDriver.companyID);
                        jsonObject.put(Const.TOKEN, MyApplication.getInstance().myDriver.token);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    currentRequest = MainActivity.zooneCheckOutRequest;
                    ApiManager.getInstance().mUrlManager.zoneCheckout(jsonObject.toString()).cache().subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(observer);

                }

            }
        }
    };


    private final BroadcastReceiver offercount = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BadgeDrawable drawable2 =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                            .badgeColor(Color.RED)
                            .textSize(sp2px(JobViewActivity.this, 14))
                            .text1(sharedPrefs.getJsonInt(Const.OFFER) + "")
                            .build();

            SpannableString spannableString =
                    new SpannableString(TextUtils.concat(
                            drawable2.toSpannable()));

            //
            ivBadge.setText(spannableString);
            if (sharedPrefs.getJsonInt(Const.OFFER) <= 0) {
                ivBadge.setVisibility(View.GONE);
            } else {
                ivBadge.setVisibility(View.VISIBLE);
            }

        }
    };


    private void cancelAlarmManager() {

        Context context = getBaseContext();
        Intent gpsTrackerIntent = new Intent(context, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, gpsTrackerIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}


