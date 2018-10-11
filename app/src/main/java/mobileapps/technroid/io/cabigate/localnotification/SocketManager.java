/*
package mobileapps.technroid.io.cabigate.localnotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Calendar;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import pgitc.kya.and.cabigate.driver.cell.ChatModel;
import pgitc.kya.and.cabigate.driver.common.CabigateGlobal;
import pgitc.kya.and.cabigate.driver.common.KyaUtility;
import pgitc.kya.and.cabigate.driver.job.ChatActivity;

*/
/********** Created by kasimmirza on 10/28/15.*************//*

public class SocketManager {

    final String SERVER_URL = "http://cabigate.com:3000";
    private Context context;

    public static boolean SOCKETS_CONNECTED = false;


    private JSONObject DATA;

    private Socket Socket;

    {
        try {
            Socket = IO.socket(SERVER_URL);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public SocketManager(Context c) {
        this.context = c;
        //this.activity = (Activity)this.context;
    }

//    public void register_Socket_Events() {
//        Socket.on("showjob", showjob);
//        Socket.on("showresponse", showresponse);
//        Socket.on("recivemessage", recivemessage);
//    }

    public void register_Client() {
        CabigateGlobal.loadLoginInfo(context);
        final JSONObject obj = new JSONObject();
        try {
            obj.put("user_id", CabigateGlobal.g_sUserID);
            obj.put("room_id", CabigateGlobal.g_sCompanyID);
            obj.put("username", CabigateGlobal.g_sNickName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Socket.emit("senddata", obj);
    }

    public void connect_Sockets() {

        Socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                SOCKETS_CONNECTED = true;
                SocketService.mClients.clear();

                register_Client();
//                register_Socket_Events();
            }

        }).on("showjob", showjob
        ).on("showresponse", showresponse
        ).on("recivemessage", recivemessage
        ).on("isTyping", istyping
        ).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                createNotification();
                SOCKETS_CONNECTED = false;
            }

        }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                createNotification();
                SOCKETS_CONNECTED = false;
            }
        }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                createNotification();
                SOCKETS_CONNECTED = false;
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                createNotification();
                SOCKETS_CONNECTED = false;
            }
        });
        Socket.connect();


    }

    public NotificationManager notificationManager;

    public void createNotification() {
        if (!CabigateGlobal.g_isShiftStarted)
            return;
*/
/*
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(context, LoginActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(context)
                .setContentTitle("Socket Error")
                .setContentText("Socket is disconnected.\nPlease sign in again for our services.")
                .setSmallIcon(R.drawable.notfication_icon)
                .setContentIntent(pIntent).build();
        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        //noti.flags |= Notification.FLAG_ONGOING_EVENT;

        notificationManager.notify(1224, noti);
*//*



        if (!KyaUtility.isNetworkConnectionAvailable(this.context)) {
            Intent intent2 = new Intent(context, ReSigninActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);

            CabigateGlobal.g_isShiftStarted = false;
            return;
        }

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        connect_Sockets();
    }


    public void sendIncomingJobStatus(int i, String _to, String _jobid) {

        CabigateGlobal.loadLoginInfo(context);

        JSONObject obj = new JSONObject();
        try {
            obj.put("to", _to);
            obj.put("status", "" + i);
            obj.put("room_id", CabigateGlobal.g_sCompanyID);
            obj.put("jobid", _jobid);

        } catch (Exception ex) {
        }

        Socket.emit("sendstatus", obj);

    }

    public void sendMessage(String _to, String _msg) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("to", _to);
            obj.put("message", _msg);
            Socket.emit("sendmessage", obj);
        } catch (Exception ex) {
        }
    }

    public void sendSOS() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "sos");
            obj.put("to", "dispatcher");
            obj.put("message", "abcde");
            Socket.emit("sendmessage", obj);
        } catch (Exception ex) {
        }
    }

    public void sendTyping(String _to, String _type) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("to", _to);
            obj.put("typing", _type);
            Socket.emit("typing", obj);
        } catch (Exception ex) {
        }
    }

    public void disconnect_Sockets() {
        SOCKETS_CONNECTED = false;
        Socket.disconnect();
        Socket.off("showjob");
        Socket.off("showresponse");
        Socket.off("recivemessage");

    }


    private Emitter.Listener showjob = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            DATA = data;

            Intent intent = new Intent(context, IncomingActivity.class);
            //ERROR/AndroidRuntime(): Calling startActivity()
            // from outside of an Activity context requires the FLAG_ACTIVITY_NEW_TASK flag.
            // Is this really what you want?
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("DATA", DATA.toString());
//            Log.i("CAB_SERVICE", "Starting Dialog Activity");
            context.startActivity(intent);
        }
    };


    private Emitter.Listener showresponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            Log.e("CAB", data.toString());
            //show_Dialog("Socket Resonse: " + data.toString() ) ;
        }
    };


    private Emitter.Listener recivemessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                String message = data.getString("message");
                String senderid = data.getString("senderid");
                String username = data.getString("username");

                long mm = System.currentTimeMillis();
                data.put("mm", mm);

                boolean isSent = false;

                for (int i = SocketService.mClients.size() - 1; i >= 0; i--) {
                    try {
                        // Send data as an Integer
//                        mClients.get(i).send(Message.obtain(null, MSG_SET_INT_VALUE, intvaluetosend, 0));

                        //Send data as a String
                        Bundle b = new Bundle();
                        b.putString("DATA", data.toString());
                        Message msg = Message.obtain(null, MSG_SET_STRING_VALUE);
                        msg.setData(b);
                        SocketService.mClients.get(i).send(msg);
                        isSent = true;
                    } catch (RemoteException e) {
                        // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                        SocketService.mClients.remove(i);
                    }
                }

                if (!isSent) {

                    ChatModel cm = new ChatModel();
                    cm.sID = senderid;
                    cm.sName = username;
                    cm.sMsg = message;
                    cm.type = ChatModel.TYPE_CHAT_FROM;

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(mm);
                    int nCurYear = calendar.get(Calendar.YEAR);
                    int nCurMonth = calendar.get(Calendar.MONTH) + 1;
                    int nCurDay = calendar.get(Calendar.DAY_OF_MONTH);
                    int nCurHour = calendar.get(Calendar.HOUR);
                    int nCurMinute = calendar.get(Calendar.MINUTE);
                    int nCurSec = calendar.get(Calendar.SECOND);

                    cm.sDate = String.format("%04d-%02d-%02d", nCurYear, nCurMonth, nCurDay);
                    cm.sTime = String.format("%02d:%02d:%02d", nCurHour, nCurMinute, nCurSec);

                    CabigateGlobal.g_arrayData.add(cm);
                    CabigateGlobal.g_sTo = senderid;

                    Intent intent = new Intent(context, ChatActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(context, (int) mm, intent, 0);

                    Notification noti = new Notification.Builder(context)
                            .setContentTitle("#" + senderid + "." + username)
                            .setContentText(message)
                            .setSmallIcon(R.drawable.notfication_icon)
                            .setTicker(username + ":" + message)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setContentIntent(pIntent).build();
                    notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;

                    notificationManager.notify(1985, noti);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };


    private Emitter.Listener istyping = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            for (int i = SocketService.mClients.size() - 1; i >= 0; i--) {
                try {
                    // Send data as an Integer
//                        mClients.get(i).send(Message.obtain(null, MSG_SET_INT_VALUE, intvaluetosend, 0));

                    //Send data as a String
                    Bundle b = new Bundle();
                    b.putString("DATA", data.toString());
                    Message msg = Message.obtain(null, MSG_SET_TYPING);
                    msg.setData(b);
                    SocketService.mClients.get(i).send(msg);
                } catch (RemoteException e) {
                    // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
                    SocketService.mClients.remove(i);
                }
            }


        }
    };

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SET_STRING_VALUE = 4;
    public static final int MSG_SET_TYPING = 3;

}
*/
