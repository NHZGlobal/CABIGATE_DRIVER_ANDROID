package mobileapps.technroid.io.cabigate.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Point;
import android.location.Address;
import android.location.Location;
import android.os.Vibrator;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import mobileapps.technroid.io.cabigate.global.Constants;
import retrofit.mime.TypedInput;

@SuppressLint("NewApi")
public class Utilities
{
    /*****************
     *
     * @param email
     * @return
     */
    public final static boolean isEmailValid(String email)
    {
        if (email == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /****
     *
     * @param password
     * @return
     */
    public static boolean isPasswordValid(String password)
    {
        //return true if and only if password:
        //1. have at least eight characters.
        //2. consists of only letters and digits.
        //3. must contain at least two digits.
        if (password.length() < 8)
        {
            return false;
        }
        else
        {
            char c;
            int count = 1;
            for (int i = 0; i < password.length() - 1; i++)
            {
                c = password.charAt(i);
                if (!Character.isLetterOrDigit(c))
                {
                    return false;
                }
                else if (Character.isDigit(c))
                {
                    count++;
                    if (count < 2)
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     *
     * @param url
     * @return
     */
    public static String getMimeType(String url)
    {
        String extension = url.substring(url.lastIndexOf("."));
        String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
        return mimeType;
    }
















    public static void vibrate(Context mContext, int millis)
    {
        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(millis);
    }

    public static Point getScreenSize(Activity context)
    {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static int calculateTextSizeFromWidth(int width, int x)
    {
        int initialTextSize = 10;

        int count = x/width * 10;
        for(int i = 0; i < count ; i++)
        {
            initialTextSize = initialTextSize + 1;
        }

        return initialTextSize;
    }


    public static int dpToPx(int dp, Context mContext)
    {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /*********************************************************************************************
     *
     * @return
     */
    public static int getDistanceSeparationFromZoom(float zoomLevel)
    {
        double distance = 20000;
        distance = distance / zoomLevel + 2000;
        return (int) distance;
    }

        public static String getVisibleRegionGrids(final VisibleRegion region)
    {
        JSONArray mainArray = new JSONArray();
        int columns = 5;
        int rows    = 5;

        double mainTopLat  = region.latLngBounds.northeast.latitude;  // (c, d)
        double mainMaxLng  = region.latLngBounds.northeast.longitude;

        double mainBottomLat = region.latLngBounds.southwest.latitude;
        double mainMinLng = region.latLngBounds.southwest.longitude;

        double horizontalDiff = Math.abs ((mainMaxLng - mainMinLng ) / columns);  // 1
        double verticalDiff   = Math.abs ((mainTopLat - mainBottomLat) / rows);  // 1

        double topLat = mainTopLat;  // (c, d)
        double topLng = mainMinLng;
        int position = 0;

        for (int i = 0; i < rows; i++)
        {
            for (int x = 0; x < columns ; x++)
            {
                double currentTopLat  = (topLat - (i * verticalDiff));
                double currentLeftLng = (topLng + (x * horizontalDiff));

                if(position == i)
                {
                    currentTopLat  = (topLat - (i * verticalDiff));
                    currentLeftLng = (topLng + (x * horizontalDiff));
                }

                position = -1;

                if(x == columns)
                    position = i + 1;

                try
                {
                    double currentBottomLat = currentTopLat - verticalDiff;
                    double currentRightLng = currentLeftLng + horizontalDiff;

                    JSONObject obj = new JSONObject();
                    obj.put("topLat"    , currentTopLat);
                    obj.put("topLng"    , currentLeftLng);
                    obj.put("bottomLat" , currentBottomLat);
                    obj.put("bottomLng", currentRightLng);

                    double random = new Random().nextDouble();
                    double randomLat = currentTopLat + (random * (currentBottomLat - currentTopLat));
                    double randomLng = currentRightLng + (random * (currentLeftLng - currentRightLng));
                    obj.put("lat", randomLat);
                    obj.put("lng", randomLng);

                    mainArray.put(obj);
                }catch (Exception e)
                {}
            }
        }
        return mainArray.toString();
    }


    public static int convertKMToMiles(int km)
    {
        return (int) (km * 0.62137);
    }

    public static float calculateZoomLevel(Context mContext, int screenWidth)
    {
        double equatorLength = 40075004; // in meters
        double widthInPixels = screenWidth;
        double metersPerPixel = equatorLength / 256;
        int zoomLevel = 1;

        int currentRadius = Preferences.getIntSharedPrefValue(mContext, Constants.USERDEFAULT_SETTINGS_KM_VALUE, 5);
        currentRadius = currentRadius * 1000;

        while ((metersPerPixel * widthInPixels) > currentRadius)
        {
            metersPerPixel /= 2;
            ++zoomLevel;
        }

        return zoomLevel;
    }

    public static String getCompleteAddress(Address address)
    {
        String addText = "";
        for (int i = 0 ; i < address.getMaxAddressLineIndex(); i++)
        {
            if (i == 0)
                addText = address.getAddressLine(i);
            else
                addText = addText + "," + address.getAddressLine(i);
        }

        if (address.getAdminArea() != null)
        {
            if (!addText.contains(address.getAdminArea()) && !address.getAdminArea().equals(""))
            {
                addText = addText + ", " + address.getAdminArea();
            }
        }

        if (address.getCountryName() != null)
        {
            if (!addText.contains(address.getCountryName()) && !address.getCountryName().equals(""))
            {
                addText = addText + ", " + address.getCountryName();
            }
        }

        return  addText;
    }
    /*********************************************************************************************
     *
     * @return
     */
    public static boolean isApplicationSentToBackground(final Context context)
    {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty())
        {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static String getDisplayNameWithAsteriks(String phone)
    {
        return "******" + phone.substring(phone.length() - 4);
    }

    /* Get drawable ID from resorce folder */
    public static int getResourceByName(Context mContext, String name)
    {
        Resources resources = mContext.getResources();
        return resources.getIdentifier(name, "drawable",
                mContext.getPackageName());
    }

    public static void setViewStatus(ViewGroup layout, boolean status)
    {
        layout.setEnabled(status);
        for (int i = 0; i < layout.getChildCount(); i++)
        {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup)
            {
                setViewStatus((ViewGroup) child, status);
            }
            else
            {
                child.setEnabled(status);
            }
        }
    }

    public static void showToast(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    /**********************************************************************************************
     *
     *
     *      API UTILITIES
     *
     *********************************************************************************************/
    /* We check if play services available or not Otherwise we show error to update */
    public static boolean checkPlayServices(Context mContext)
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            return false;
        }
        return true;
    }

    public static String convertTypedInputToString(TypedInput result)
    {
        //Try to get response body
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {

            reader = new BufferedReader(new InputStreamReader(result.in()));

            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();

    }

    public static String getConvertedDate(String dateString)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date convertedDate = new Date();

        Calendar systemCal = Calendar.getInstance();

        /***********************************/
        Calendar cal = Calendar.getInstance();

        try {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            convertedDate = dateFormat.parse(dateString);
        } catch (Exception e) {
            return "";
        }
        cal.setTime(convertedDate);
        /***********************************/
        CharSequence myDateString = DateUtils.getRelativeTimeSpanString(cal.getTimeInMillis(), systemCal.getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
        return myDateString.toString().replace("minutes", "min");
    }

    public static String getConvertedDateWithTime(String dateString)
    {
        Calendar systemCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        /***********************************/
        CharSequence myDateString = DateUtils.getRelativeTimeSpanString(Long.parseLong(dateString) * 1000L, systemCal.getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
        return myDateString.toString().replace("minutes", "min");
    }


    public static String getHashKey(Activity mContext)
    {
        try
        {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                return Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (Exception e)
        {

        }
        return null;
    }
    public static Location convertLatLngToLocation(LatLng point)
    {
        Location loc = new Location("");
        loc.setLatitude(point.latitude);
        loc.setLongitude(point.longitude);
        return loc;
    }

    public static final String convertStreamToString(InputStream in)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
            {
                result.append(line);
            }

            return result.toString();
        }catch (Exception e)
        {}
        return null;
    }

    public static List<LatLng> decodePoly(String encoded)
    {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }



}