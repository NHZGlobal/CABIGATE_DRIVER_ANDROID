package mobileapps.technroid.io.cabigate.api;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.SocketIOException;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.app.MyApplication;
import mobileapps.technroid.io.cabigate.global.Constants;
import mobileapps.technroid.io.cabigate.helper.Const;
import mobileapps.technroid.io.cabigate.helper.NotificationUtils;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;
import mobileapps.technroid.io.cabigate.models.Job;
import mobileapps.technroid.io.cabigate.ui.activity.MainActivity;

/**
 * Created by Muhammad on 21/03/2015.
 */
public class SocketManager implements Emitter.Listener
{
 public static final int SOCKET_PORT = 3000;
 // public static final int SOCKET_PORT = 555;
    public static final String SOCKET_URL = "http://cabigate.com" + ":" + SOCKET_PORT;
    // public static final String SOCKET_URL = "http://nextcar.club" + ":" + SOCKET_PORT;
  //  public static final String SERVER_URL = "accessKey=%s&id=%s&accountType=1";
     public static final String SERVER_URL = "user_id=1&room_id=2100&username=arslan";
    private static Socket mSocket;
    private static SocketManager mInstance;
    private static final String EVENT_SHOWJOB = "showjob";
    private static final String CONNECTED                 = "senddata";
    private static final String EVENT_SEND_STATUS    = "sendstatus";
    private static final String EVENT_DRIVER_UPDATE_LOCATION    = "updatelocation";
    private static final String EVENT_DRIVER_NEW_ORDER          = "newOrder";
    private static final String EVENT_DRIVER_REJECT_BOOKING     = "rejectBooking";
    private static final String EVENT_SEND_MY_DETAILS     = "send_my_details";
    private static final String EVENT_DRIVER_ACCEPT_BOOKING     = "acceptBooking";
    private static final String EVENT_DRIVER_ACTIVATE_BOOKING   = "activateBooking";
    private static final String EVENT_DRIVER_END_BOOKING        = "endBooking";
    private static final String SOCKET_EVENT_CANCEL_BOOKED_BOOKING  = "cancelBookedBooking";
    private static final String SOCKET_EVENT_RECIVE_MESSAGE = "recivemessage";
    private static final String SOCKET_EVENT_SEND_MESSAGE = "sendmessage";
    private static final String SOCKET_EVENT_SYNC_DISPATCH_STATUS = "sync_dispatch_status";
    private static final String SOCKET_SYNC_APP_STATUS   = "sync_app_status";
    private static final String SOCKET_PUSHNOTIFICATION   = "pushnotification";
    private Context mContext;
    private NotificationManager mNotificationManager;
    public boolean shouldSendOffline = true;
    private static final String SOCKET_EVENT_OFFER_COUNT="offersnotifier";
    private void reconectSocket(){

/*

  try {
    Thread.sleep(10 * 1000);
} catch (InterruptedException e) {
    e.printStackTrace();
}

        connectSocketManager();
*/

}


    public static synchronized SocketManager getInstance()
    {
        return mInstance;
    }

    public static void clear()
    {
        if (mInstance != null)
        {
            mInstance = null;
        }
    }

    public boolean isSocketConnected()
    {
        if(mSocket != null)
        {
            if (mSocket.connected())
                return true;
        }
        return false;
    }

    public SocketManager(final Context mContext)
    {
        this.mContext = mContext;
        mInstance = this;
    }

    public void connectSocketManager()
    {
        if(mSocket != null)
        {
            if (mSocket.connected())
                return;
        }
        SharedPrefs sharedPrefs=new SharedPrefs(mContext);

        if(!sharedPrefs.isLogin())
        {
            return;
        }

        try {
            String query = String.format(Locale.getDefault(),
                    SERVER_URL,
                    MyApplication.getInstance().myDriver.token,
                    MyApplication.getInstance().myDriver.userid
            );

            // socket options
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = true;
            opts.secure = false;
            //opts.sslContext = SSLContext.getDefault();

            opts.query = query;
            opts.timeout=20000;
            opts.reconnection=true;
            opts.reconnectionDelay=1000;
            opts.reconnectionDelayMax=10000;
            opts.transports = new String[]{"polling"};

            mSocket = IO.socket(MyApplication.getInstance().myDriver.socketUrl, opts);
            JSONObject mObj = new JSONObject();
            mObj.put("company_id",MyApplication.getInstance().myDriver.companyID);
            mObj.put("user_id", MyApplication.getInstance().myDriver.userid);
            mObj.put("room_id",MyApplication.getInstance().myDriver.companyID);
            mObj.put("user_id", MyApplication.getInstance().myDriver.userid);
            mObj.put("username", MyApplication.getInstance().myDriver.username);

            mSocket.emit(CONNECTED, mObj);
            mSocket.on(CONNECTED, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    Log.d("TAG", "eventConnect");
                    Intent myIntent = new Intent();
                    myIntent.setAction(Constants.ACTION_PUSH_ORDER);
                    mContext.sendBroadcast(myIntent);
                }
            });

            /************************************************************************************/


            mSocket.on(EVENT_SHOWJOB, new Emitter.Listener()
            {
                @Override
                public void call(Object... args)
                {

                    JSONObject mObj = new JSONObject();
                    JSONObject obj22 = (JSONObject) args[0];
                    Job job=(Job)new Gson().fromJson(obj22.toString(),Job.class);
                    try {
                        mObj.put("company_id",MyApplication.getInstance().myDriver.companyID);
                        mObj.put("user_id", MyApplication.getInstance().myDriver.userid);
                        mObj.put("jobid",job.getJobid());
                        mObj.put("sender",job.getSender());
                        mObj.put("user_id", MyApplication.getInstance().myDriver.userid);
                        mObj.put("refrence", job.getRefrence());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("dispatch",mObj.toString());
                   mSocket.emit("job_recived_on_app", mObj);



                    if (MyApplication.getInstance().isAppOnline) {
                        JSONObject obj = (JSONObject) args[0];

                        Intent myIntent = new Intent();
                        myIntent.setAction(Constants.ACTION_REQUEST_ORDER);
                        myIntent.putExtra("data", obj.toString());

                        myIntent.putExtra("setupBooking", true);
                        mContext.sendBroadcast(myIntent);
                    }else{


                        JSONObject obj = (JSONObject) args[0];

                        Intent myIntent = new Intent();
                        myIntent.setAction(Constants.ACTION_REQUEST_ORDER);

                        myIntent.putExtra("data", obj.toString());

                        myIntent.putExtra("setupBooking", true);
                        mContext.sendBroadcast(myIntent);

                    }


                }
            });
            /************************************************************************************/
            mSocket.on(EVENT_SEND_MY_DETAILS, new Emitter.Listener()
            {
                @Override
                public void call(Object... args)
                {
                    Log.e("dispatch2",args.toString());
                    JSONObject mObj = new JSONObject();
                    JSONObject obj22 = (JSONObject) args[0];
                    //Job job=(Job)new Gson().fromJson(obj22.toString(),Job.class);
                    try {
                        mObj.put("company_id",MyApplication.getInstance().myDriver.companyID);
                        mObj.put("user_id", MyApplication.getInstance().myDriver.userid);

                        mObj.put("user_id", MyApplication.getInstance().myDriver.userid);
                        mObj.put("sender","");

                        mObj.put("lat", MyApplication.getInstance()
                                .getLatitude());
                        mObj.put("lng", MyApplication.getInstance()
                                .getLongitude());
                        mObj.put("appversioninfo",MyApplication.getInstance().versionName);
                        mObj.put("isjob","false");

                        if(MyApplication.getInstance().apiManager.mReservedBooking!=null) {
                            mObj.put("isjob","true");
                            mObj.put("jobstatus", MyApplication.getInstance().apiManager.mReservedBooking.status);
                            mObj.put("jobrefrence", MyApplication.getInstance().apiManager.mReservedBooking.getRefrence());
                            mObj.put("jobid", MyApplication.getInstance().apiManager.mReservedBooking.getRefrence());
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("dispatch2",mObj.toString());
                    mSocket.emit(EVENT_SEND_MY_DETAILS, mObj);


                }
            });

            mSocket.on(EVENT_DRIVER_NEW_ORDER, new Emitter.Listener()
            {
                @Override
                public void call(Object... args)
                {
                    if (ApiManager.getInstance().mBooking == null) // If there is booking already present.
                    {
                        if (MyApplication.getInstance().isAppOnline)
                        {
                            Intent myIntent = new Intent();
                            myIntent.setAction(Constants.ACTION_REQUEST_ORDER);
                            myIntent.putExtra("data", args[0].toString());
                            mContext.sendBroadcast(myIntent);
                        }
                        else
                        {
                            Intent myIntent = new Intent(mContext, MainActivity.class);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            myIntent.putExtra("data", args[0].toString());
                           // ApiManager.getInstance().mReservedBooking = new Gson().fromJson(args[0].toString(), Job.class);
                            sendNotification("You have received a new booking", myIntent, mContext.getResources().getString(R.string.app_name));
                        }
                    }
                    else
                    {

                    }
                }
            });





            mSocket.on("forcelogout", new Emitter.Listener()
            {
                @Override
                public void call(Object... args)
                {
                    String type="";
                    JSONObject jsonObject = (JSONObject) args[0];
                    Log.e("msg",jsonObject.toString());
                    try {
                    type=jsonObject.getString("type");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                       Intent myIntent = new Intent();
                        myIntent.setAction(Constants.ACTION_FORCE);
                        myIntent.putExtra("data", type);
                        mContext.sendBroadcast(myIntent);




                }
            });
            mSocket.on(SOCKET_PUSHNOTIFICATION, new Emitter.Listener()
            {
                @Override
                public void call(Object... args)
                {

                    String type="";
                    JSONObject jsonObject = (JSONObject) args[0];
                    Log.e("msg", jsonObject.toString());
                    try {
                        String message=jsonObject.getString("message");
                        String title=jsonObject.getString("title");
                        Intent myIntent = new Intent();
                        NotificationUtils notificationUtils=new NotificationUtils(mContext);
                        notificationUtils.showNotificationMessageCustom(title, message, new Date() + "", myIntent, "notification1");
                            //Constants.syncheck = true;


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
                         mSocket.on(SOCKET_SYNC_APP_STATUS, new Emitter.Listener()
            {
                @Override
                public void call(Object... args)
                {
                    String type="";
                    JSONObject jsonObject = (JSONObject) args[0];
                    Log.e("msg",jsonObject.toString());
                    try {
                    type=jsonObject.getString("type");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                 //   "type":"logout";


                        Intent myIntent = new Intent();
                        myIntent.setAction(Constants.ACTION_SYNC_APP_STATUS);
                        myIntent.putExtra("data", type);
                        mContext.sendBroadcast(myIntent);




                }
            });


            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    connectSocketManager();
                    //reconectSocket();
                }
            });
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {


                    JSONObject mObj = new JSONObject();
                    try {
                        mObj.put("company_id",MyApplication.getInstance().myDriver.companyID);
                        mObj.put("user_id", MyApplication.getInstance().myDriver.userid);
                        mObj.put("room_id",MyApplication.getInstance().myDriver.companyID);
                        mObj.put("user_id", MyApplication.getInstance().myDriver.userid);
                        mObj.put("username", MyApplication.getInstance().myDriver.username);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    mSocket.emit(CONNECTED, mObj);

                }
            });

            /************************************************************************************/

            /************************************************************************************/

            mSocket.on(SOCKET_EVENT_CANCEL_BOOKED_BOOKING, new Emitter.Listener()
            {
                @Override
                public void call(Object... args)
                {
                    ApiManager.getInstance().mBooking = null;
                    Intent myIntent = new Intent();
                    myIntent.setAction(Constants.ACTION_CANCEL_BOOKING);
                    myIntent.putExtra("data", args[0].toString());
                    mContext.sendBroadcast(myIntent);
                }
            });

            /************************************************************************************/

            /************************************************************************************/

            mSocket.on(SOCKET_EVENT_RECIVE_MESSAGE, new Emitter.Listener()
            {
                @Override
                public void call(Object... args)
                {

                    Intent myIntent = new Intent();
                    myIntent.setAction(Constants.ACTION_RECIVE_MESSAGE);
                    myIntent.putExtra("data", args[0].toString());
                    mContext.sendBroadcast(myIntent);
                }
            });

   mSocket.on(SOCKET_EVENT_OFFER_COUNT, new Emitter.Listener()
            {
                @Override
                public void call(Object... args)
                {

                    try {

                        SharedPrefs sharedPrefs=new SharedPrefs(mContext);
                        JSONObject jsonObject=new JSONObject( args[0].toString());
                        sharedPrefs.setJson(Const.OFFER,jsonObject.getInt("offerscount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent myIntent = new Intent();
                    myIntent.setAction(Constants.ACTION_OFFER_COUNT);
                    myIntent.putExtra("data", args[0].toString());
                    mContext.sendBroadcast(myIntent);

                }
            });








            mSocket.on(SOCKET_EVENT_SYNC_DISPATCH_STATUS, new Emitter.Listener()
            {
                @Override
                public void call(Object... args)
                {


                  //  {"synctype":"driverstatus","company_id":2100,"user_id":1,"sender":12,"status":"busy","awaytime":0}

                    JSONObject obj = (JSONObject) args[0];
                    Log.e("disatch", obj.toString());
                    try {
                        if(obj.getString("synctype").equals("driverstatus")){

                            Intent myIntent = new Intent();
                            myIntent.setAction(Constants.ACTION_SYNC_DRVIER_STATUS);
                            myIntent.putExtra("data", obj.toString());
                            mContext.sendBroadcast(myIntent);
                        }else{

                            Job job=(Job)new Gson().fromJson(obj.toString(),Job.class);
                            JSONObject mObj = new JSONObject();
                            try {
                                mObj.put("company_id",MyApplication.getInstance().myDriver.companyID);
                                mObj.put("user_id", MyApplication.getInstance().myDriver.userid);
                                mObj.put("jobid",job.getJobid());
                                mObj.put("sender",job.getSender());
                                mObj.put("user_id", MyApplication.getInstance().myDriver.userid);
                                mObj.put("refrence", job.getRefrence());
                                mObj.put("type", "sync");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e("dispatch",mObj.toString());
                            mSocket.emit("job_recived_on_app", mObj);




                            Intent myIntent = new Intent();
                            myIntent.setAction(Constants.ACTION_REQUEST_SYNC_JOB);
                            myIntent.putExtra("data", obj.toString());

                            myIntent.putExtra("setupBooking", true);
                            mContext.sendBroadcast(myIntent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            /************************************************************************************/
            mSocket.on(Socket.EVENT_MESSAGE, new Emitter.Listener()
            {
                @Override
                public void call(Object... args)
                {
                    Log.d("TAG", "eventMessage");
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener()
            {
                @Override
                public void call(Object... args)
                {
                    Log.d("TAG", "eventDisconnect");
                    //connectSocketManager();
                    reconectSocket();
                }
            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener()
            {
                @Override
                public void call(Object... args)
                {

                    reconectSocket();
                    Log.d("TAG", "eventConnectError");
                    for (Object o : args)
                    {
                        Log.d("TAG", "object: " + o.toString());
                        if (o instanceof SocketIOException)
                            ((SocketIOException) o).printStackTrace();
                    }
                }
            }).on(Socket.EVENT_ERROR, new Emitter.Listener()
            {
                @Override
                public void call(Object... args)
                {
                    Log.d("TAG", "eventError");
                  //  connectSocketManager();
                }
            });

            if(!mSocket.connected())
                mSocket.connect();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadCastOnline()
    {
        try {
              if(!connectSocket1())
                  return;

            JSONObject mObj = new JSONObject();
            mObj.put("lng", MyApplication.getInstance().getLongitude());
            mObj.put("lat", MyApplication.getInstance().getLatitude());
            mObj.put("id", MyApplication.getInstance().myDriver.userid);
            mObj.put("bearing", MyApplication.getInstance().getBearing());
            mObj.put("company_id",MyApplication.getInstance().myDriver.companyID);
            mObj.put("user_id", MyApplication.getInstance().myDriver.userid);
            mObj.put("speed", MyApplication.getInstance().mLocation.getSpeed());

            mSocket.emit(EVENT_DRIVER_UPDATE_LOCATION, mObj, new Ack()
            {
                @Override
                public void call(Object... args)
                {
                    try
                    {
                        JSONObject obj = (JSONObject) args[0];

                        Intent myIntent = new Intent();
                        myIntent.setAction(Constants.ACTION_REQUEST_ORDER);
                        if (obj.has("booking"))
                        {
                            myIntent.putExtra("data", obj.getString("booking"));
                            myIntent.putExtra("setupBooking", true);
                        }
                        mContext.sendBroadcast(myIntent);
                    }catch (Exception e)
                    {}
                }
            });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void connectSocket()
    {
           if(mSocket==null){
               connectSocketManager();
           }
        if(!mSocket.connected())
            mSocket.connect();
           // connectSocketManager();
    }
    public boolean connectSocket1()
    {
           if(mSocket==null){
               connectSocketManager();
               return false;
           }
        if(!mSocket.connected()){
           mSocket.connect();
           // connectSocketManager();
            return false;
        }
        return  true;
    }

    public void broadCastOffline()
    {
        mSocket.disconnect();
    }

    public void rejectBooking(String bookingId, String userId)
    {
        try {

            JSONObject mObj = new JSONObject();
            mObj.put("bookingId", bookingId);
            mObj.put("userId", userId);

            mSocket.emit(EVENT_DRIVER_REJECT_BOOKING, mObj.toString(), new Ack()
            {
                @Override
                public void call(Object... args)
                {
                    try
                    {
                        Intent myIntent = new Intent();
                        myIntent.setAction(Constants.ACTION_UPDATE_ORDER);
                        myIntent.putExtra("data",  args[0].toString());
                        mContext.sendBroadcast(myIntent);

                    }catch (Exception e)
                    {}
                }
            });


        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void sendMessage(String senderidId, String message) throws JSONException {


            JSONObject mObj = new JSONObject();
            mObj.put("to", senderidId);
            mObj.put("message", message);
            mObj.put("company_id",MyApplication.getInstance().myDriver.companyID);
            mObj.put("user_id", MyApplication.getInstance().myDriver.userid);


            mSocket.emit(SOCKET_EVENT_SEND_MESSAGE, mObj);
        }  public void sendSosMessage(String senderidId) throws JSONException {


            JSONObject mObj = new JSONObject();
            mObj.put("to", senderidId);
            mObj.put("message", "");
            mObj.put("type", "sos");
          mObj.put("company_id",MyApplication.getInstance().myDriver.companyID);
           mObj.put("user_id", MyApplication.getInstance().myDriver.userid);

            mSocket.emit(SOCKET_EVENT_SEND_MESSAGE, mObj);
        }

    public void acceptRejectBooking(final int status,Job job)
    {
        try {



            JSONObject mObj = new JSONObject();
            mObj.put("status", status);
            mObj.put("jobid", Integer.parseInt(job.getJobid()));
            mObj.put("room_id", Integer.parseInt(MyApplication.getInstance().myDriver.companyID));
            mObj.put("to", job.getSender());
            mObj.put("company_id",MyApplication.getInstance().myDriver.companyID);
            mObj.put("user_id", MyApplication.getInstance().myDriver.userid);
          //  to:to,status:status,room_id:roomid,jobid:jobid
          //  mSocket.emit(EVENT_SEND_STATUS,mObj);
            mSocket.emit(EVENT_SEND_STATUS, mObj, new Ack()
            {
                @Override
                public void call(Object... args)
                {
                    try
                    {
                        if(status==0) {
                            Intent myIntent = new Intent();
                            myIntent.setAction(Constants.ACTION_REQUEST_ORDER);
                            myIntent.putExtra("data", new Gson().toJson(MyApplication.getInstance().apiManager.mBooking));
                            myIntent.putExtra("setupBooking", true);
                            mContext.sendBroadcast(myIntent);
                        }
                    }catch (Exception e)
                    {}
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void activateBooking(String bookingId, String userId)
    {
        try {

            JSONObject mObj = new JSONObject();
            mObj.put("bookingId", bookingId);
            mObj.put("userId", userId);

            mSocket.emit(EVENT_DRIVER_ACTIVATE_BOOKING, mObj.toString(), new Ack()
            {
                @Override
                public void call(Object... args)
                {
                    try
                    {
                        JSONObject obj = (JSONObject) args[0];
                        Intent myIntent = new Intent();
                        myIntent.setAction(Constants.ACTION_REQUEST_ORDER);
                        myIntent.putExtra("data", obj.getString("booking"));
                        myIntent.putExtra("setupBooking", true);
                        mContext.sendBroadcast(myIntent);

                    }catch (Exception e)
                    {}
                }
            });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }






    @Override
    public void call(Object... args)
    {
    }

    private void sendNotification(String msg, Intent i, String title)
    {
        mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                i, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.cancel(Constants.PUSH_ID);
        mNotificationManager.notify(Constants.PUSH_ID, mBuilder.build());
    }


    private void sendNotificationCustome(String msg, Intent i, String title,Uri uri)
    {
        mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                i, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.mipmap.ic_launcher)


                        .setContentTitle(title)
                        .setSound(uri)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.cancel(Constants.PUSH_ID);
        mNotificationManager.notify(Constants.PUSH_ID, mBuilder.build());
    }



    @TargetApi(11)
    protected void moveToFront() {
        if (Build.VERSION.SDK_INT >= 11) { // honeycomb
            final ActivityManager activityManager = (ActivityManager) MyApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
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


}