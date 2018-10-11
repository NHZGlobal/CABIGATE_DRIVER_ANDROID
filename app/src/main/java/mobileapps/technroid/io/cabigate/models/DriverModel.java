package mobileapps.technroid.io.cabigate.models;

import android.content.Context;

import com.google.gson.Gson;

import mobileapps.technroid.io.cabigate.global.Constants;
import mobileapps.technroid.io.cabigate.helper.Const;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;

public class DriverModel
{
    public static final int DRIVER_AVAILABLE = 1;
    public static final int DRIVER_NOT_AVAILABLE = 0;

    public String id;
    public String accessKey;
    public String phone;
    public String email;
    public String imagePath;
    public String language;
    public String name = "******";
    public String oSversion;
    public String deviceModel;
    public String show_dispatcher;

    public boolean notifStatus;
    public int isAvailable;
    public String deviceType;
    public long creationDate;

    public double distance;
    public double price;

    public BookingModel booking;
    public String currency;
    public int status;
    public String error_msg;
    public DriverModelResponse response;

    public DriverModel()
    {
    }

    /**
     * *********************************************************************
     *
     * @param mContext
     * @return
     */
    public static DriverModelResponse initWithCurrentDeviceValues(Context mContext)
    {



        SharedPrefs sharedPrefs=new SharedPrefs(mContext);
        if(sharedPrefs.isLogin()){
        DriverModelResponse driver = new Gson().fromJson(sharedPrefs.getJson(Const.USER_DETAIL), DriverModelResponse.class);
        return driver;}
        else{
            return new DriverModelResponse();
        }
    }

    public String getProfileImagePath()
    {
        return Constants.PROFILE_PATH + imagePath;
    }
}
