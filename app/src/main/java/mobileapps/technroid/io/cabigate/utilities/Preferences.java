package mobileapps.technroid.io.cabigate.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Random;

import mobileapps.technroid.io.cabigate.app.MyApplication;
import mobileapps.technroid.io.cabigate.global.Constants;
import mobileapps.technroid.io.cabigate.models.DriverModel;

public class Preferences
{
    private static final String SHARED_PREFRENCES_KEY = "UserSharedPrefs";

    public static void saveSharedPrefValue(Context mContext, String key, String value)
    {
        SharedPreferences UserSharedPrefs = mContext.getSharedPreferences(SHARED_PREFRENCES_KEY, Activity.MODE_PRIVATE);
        Editor edit = UserSharedPrefs.edit();
        edit.putString(key, scrambleText(value));
        edit.commit();
    }

    public static void saveBoolSharedPrefValue(Context mContext,String key, boolean value)
    {
        SharedPreferences UserSharedPrefs = mContext.getSharedPreferences(SHARED_PREFRENCES_KEY, Activity.MODE_PRIVATE);
        Editor edit = UserSharedPrefs.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static void saveIntegerSharedPrefValue(Context mContext, String key, int value)
    {
        SharedPreferences UserSharedPrefs = mContext.getSharedPreferences(SHARED_PREFRENCES_KEY, Activity.MODE_PRIVATE);
        Editor edit = UserSharedPrefs.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    /**********************************************************************************************
     *
     * @param cxt
     * @param key
     * @return
     */
    public static boolean getBoolSharedPrefValue(Context cxt, String key, boolean defaultValue)
    {
        SharedPreferences UserSharedPrefs = cxt.getSharedPreferences(SHARED_PREFRENCES_KEY, Activity.MODE_PRIVATE);
        return UserSharedPrefs.getBoolean(key, defaultValue);
    }

	public static String getSharedPrefValue(Context mContext, String key)
	{
		SharedPreferences UserSharedPrefs = mContext.getSharedPreferences(SHARED_PREFRENCES_KEY, Activity.MODE_PRIVATE);
		String value = UserSharedPrefs.getString(key, null);
		return unScrambleText(value);
	}

    public static SharedPreferences getSharedPref(Context cxt)
    {
        return cxt.getSharedPreferences(SHARED_PREFRENCES_KEY, Activity.MODE_PRIVATE);
    }

    public static int getIntSharedPrefValue(Context cxt, String shared_pref_key, int defaultValue)
    {
        SharedPreferences UserSharedPrefs = cxt.getSharedPreferences("UserSharedPrefs", Activity.MODE_PRIVATE);
        return UserSharedPrefs.getInt(shared_pref_key, defaultValue);
    }

    /*******************************************************************************************
     *
     * @param mContext
     * @return
     */
    public static boolean isUserLoggedIn(Context mContext)
    {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFRENCES_KEY, Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constants.USERDEFAULT_ISLOGGEDIN, false);
    }

    public static DriverModel getDriverDetails(Context mContext)
    {
        String val = getSharedPrefValue(mContext, Constants.USERDEFAULT_USER_DATA);

        DriverModel driver = new Gson().fromJson(val, DriverModel.class);
        try {

            JSONObject obj  = new JSONObject(val);
            obj             = obj.getJSONObject("rate");
            driver.price    = obj.getDouble("price");
            driver.distance = obj.getDouble("distance");
            driver.currency ="AED"; //obj.getString("currency");

        }catch (Exception e)
        {}

        return driver;
    }

    public static void saveLoginDefaults(Context mContext, String jsonData)
    {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFRENCES_KEY, Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(Constants.USERDEFAULT_USER_DATA, scrambleText(jsonData));
        editor.putBoolean(Constants.USERDEFAULT_ISLOGGEDIN, true);
        editor.commit();
        MyApplication.getInstance().myDriver = DriverModel.initWithCurrentDeviceValues(mContext);
    }

    public static void logoutDefaults(Context mContext)
    {
        //app.mDbHelper.purgeEntireDatabase();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFRENCES_KEY, Activity.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        String gcmToken =  Preferences.getSharedPrefValue(mContext, Constants.USERDEFAULT_GCM_STRING);
        editor.clear();
        editor.putString(Constants.USERDEFAULT_GCM_STRING, scrambleText(gcmToken));
        editor.commit();
    }

    private static String scrambleText(String text)
    {
        try {
            Random r = new Random();
            String prefix = String.valueOf(r.nextInt(90000) + 10000);

            String suffix = String.valueOf(r.nextInt(90000) + 10000);

            text = prefix + text + suffix;

            byte[] bytes = text.getBytes("UTF-8");
            byte[] newBytes = new byte[bytes.length];

            for (int i = 0; i < bytes.length; i++)
            {
                newBytes[i] = (byte)(bytes[i] - 1);
            }

            return new String(newBytes, "UTF-8");
        }catch (Exception e)
        {
            return text;
        }
    }

    private static String unScrambleText(String text)
    {
        try {
            byte[] bytes = text.getBytes("UTF-8");
            byte[] newBytes = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++)
            {
                newBytes[i] = (byte)(bytes[i] + 1);
            }
            String textVal = new String(newBytes, "UTF-8");

            return textVal.substring(5,textVal.length() - 5);
        }catch (Exception e)
        {
            return text;
        }
    }
}
