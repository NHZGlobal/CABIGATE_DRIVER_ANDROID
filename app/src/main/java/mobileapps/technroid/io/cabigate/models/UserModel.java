package mobileapps.technroid.io.cabigate.models;

import android.content.Context;
import android.os.Build;

import java.util.Locale;

import mobileapps.technroid.io.cabigate.global.Constants;

public class UserModel
{
    public static final String USER_INFO_DATASET = "userinfo";

    public String id;
    public String accessKey;
    public String email;

    public String imagePath;
    public String language;
    public String name = "******";
    public String endpointARN;
    public String oSversion;
    public String deviceModel;
    public String phone;

    public boolean notifStatus;
    public boolean isOnline;
    public boolean availability;
    public String deviceType;
    public long creationDate;

    public UserModel()
    {
    }

    /**
     * *********************************************************************
     *
     * @param mContext
     * @return
     */
    public static UserModel initWithCurrentDeviceValues(Context mContext)
    {
        UserModel user = new UserModel();

        user.oSversion   = Build.VERSION.RELEASE;
        user.deviceModel = Build.MODEL;
        user.deviceType  = Constants.DEVICE_TYPE_ANDROID;
        user.language    = Locale.getDefault().getLanguage();

        return user;
    }

    public String getProfileImagePath()
    {
        return Constants.PROFILE_PATH + imagePath;
    }
}
