package mobileapps.technroid.io.cabigate.global;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

public class Constants
{
    public static final String TWITTER_KEY = "hIWj4IdsrcjBZAIXRgHMFvWTP";
    public static final String TWITTER_SECRET = "M1z35Rl3JMpCc1yaWBk2eH8GDRg7NSWmW4TrYEhVWQ2TpRS6mc";
    public static final String TWITTER_CALLBACK = "x-oauthflow-twitter://xouath.devicebee.com";

    public static final String ADOBE_CREATIVE_CLIENT_SECRET = "34673e41-b940-4d8d-8b72-a808d703568b";
    public static final String ADOBE_CREATIVE_CLIENT = "ab0a0f4eae5e4b9cb86a7cbbdcd26426";
    public   static boolean locationc = false;
    public  static boolean backcheck = false;
    public static boolean check = false;
    public static boolean syncheck = false;
    public static Timer timer;
    public static TimerTask timerTask;
    public static final Handler handler = new Handler();

    public static boolean permissioncheck;
    public static  boolean logoutcheck = false;
    public static final String ACTION_REQUEST_PUSH    = "pushPing";
    public static final String ACTION_REQUEST_REFRESH = "refreshPing";
    public static final String ACTION_CANCEL_BOOKING  = "cancelBooking";
    public static final String ACTION_RECIVE_MESSAGE  = "reciveMessage";
    public static final String ACTION_SEND_MESSAGE  = "sendMessage";
    public static final String ACTION_REQUEST_ORDER   = "newOrder";
    public static final String ACTION_FORCE  = "action_force";
    public static final String ACTION_OFFER_COUNT  = "action_offer_count";
    public static final String ACTION_SYNC_APP_STATUS  = "sync_app_status";
    public static final String ACTION_REQUEST_SYNC_JOB = "syncJob";
    public static final String ACTION_UPDATE_ORDER     = "updateOrder";
    public static final String ACTION_FINISH_ORDER     = "finishOrder";
    public static final String ACTION_PUSH_ORDER       = "pushOrder";

    public static final String ACTION_MY_SHIFT_FILTER      = "action_my_shift_filter";

    public static final String  DEVICE_TYPE_IPHONE  = "iPhone";
    public static final String  DEVICE_TYPE_ANDROID = "Android";


    public static final String SOCKET_STATUS_OFFLINE = "";
    public static final String SOCKET_STATUS_ONLINE = "";
    public static final String ACTION_SYNC_DRVIER_STATUS ="ACTION_SYNC_DRVIER_STATUS" ;

    public static int PUSH_ID = 800;

    public static String GCM_SENDER_ID = "754971593698";
    public static final String DIRECTION_API_KEY    = "AIzaSyDQZoyGwdyOKZxeQ3F0Mab6YNP9LRI3gW8";

    public static boolean isSandBox = false;
    public static boolean isDebug   = true;

    /**************************************************
     *
     *		 RESULT CODES
     *
     **************************************************/
    public static int RESULT_COUNTRY    = 9000;
    public static int RESULT_ACTIVATION = 9001;

    public static int RESULT_MAIN       = 9002;
    public static int RESULT_CHAT       = 9010;

    public static final int RESULT_TAG_CONTACTS = 1300;
    public static final int RESULT_FINISH_MAP   = 1400;
    public static final int RESULT_REFRESH_MAIN = 1500;


    public static final String TEMP_DIRECTORY = "temp";
    public static final String VIDEO_DIRECTORY = "video";

    /**************************************************
	 *
	 *		 USERDEFAULT USER KEYS
	 *
	 **************************************************/

    public static final String USERDEFAULT_ISLOGGEDIN    = "isLoggedIn";
    public static final String USERDEFAULT_USER_DATA     = "driverData";
    public static final String USERDEFAULT_GCM_STRING    = "gcmString";

    public static final String USERDEFAULT_SETTINGS_KM_VALUE = "kmValue";


    /**************************************************
     *
     *		 RESPONSE KEYS
     *
     **************************************************/
    public static final String RESPONSE_KEY_ERROR       = "error";
    public static final String RESPONSE_KEY_MESSAGE     = "message";
    public static final String RESPONSE_KEY_USER        = "user";
    public static final String RESPONSE_KEY_INVALID_KEY = "invalidKey";
    public static final String RESPONSE_KEY_DRIVER        = "driver";


    public static final String RESPONSE_KEY_BOOKINGS        = "bookings";



 //public static final String BASE_URL = "http://nextcar.club/cityTaxi";
    public static final String BASE_URL = "http://api.cabigate.com/index.php";

    //public static final String PROFILE_PATH = "http://nextcar.club/cityTaxi/profile_images/";
    public static final String PROFILE_PATH = "http://45.55.169.112/DTC/profile_images/";

    public static final int NOTIF_ID    = 9818;
}