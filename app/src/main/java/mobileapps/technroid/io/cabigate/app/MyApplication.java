package mobileapps.technroid.io.cabigate.app;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.LocationSource;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import mobileapps.technroid.io.cabigate.BuildConfig;
import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.api.ApiManager;
import mobileapps.technroid.io.cabigate.api.ConnectionService;
import mobileapps.technroid.io.cabigate.global.Constants;
import mobileapps.technroid.io.cabigate.gpstrackmodule.ConnectivityReceiver;
import mobileapps.technroid.io.cabigate.gpstrackmodule.NetworkStateReceiver;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;
import mobileapps.technroid.io.cabigate.models.DriverModelResponse;
import mobileapps.technroid.io.cabigate.utilities.DirectoryUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static android.location.LocationManager.GPS_PROVIDER;

@ReportsCrashes(
        mailTo = "munamdev@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        forceCloseDialogAfterToast = false, // optional, default false
        resToastText = R.string.app_name
)
public class MyApplication extends Application implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, LocationSource {
    public static DriverModelResponse myDriver = new DriverModelResponse();

    public GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public Location mLocation;
    private static MyApplication mInstance;
    private Context mContext;
    public static final String CHANNEL_ID = "exampleServiceChannel";
    private OnLocationChangedListener mListener;
    public boolean isAppOnline = false;
    public String versionName = BuildConfig.VERSION_NAME;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public ApiManager apiManager;
    public ConnectionService mService;
    public SharedPrefs sharedPrefs;

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        if (currentBestLocation.getLatitude() == 0 && currentBestLocation.getLongitude() == 0)
            return true;

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > 30000;
        boolean isSignificantlyOlder = timeDelta < -30000;
        boolean isNewer = timeDelta > 0;

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 50;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // If it's been more than 30 seconds since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer && !isSignificantlyLessAccurate) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/mainFont.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        mInstance = this;
        apiManager = new ApiManager(this);
        //createNotificationChannel();
       if (Build.VERSION.SDK_INT >= 24) {
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
           registerReceiver(new ConnectivityReceiver(), intentFilter);
        }
      /*  if (Build.VERSION.SDK_INT >= 24) {
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(new ConnectivityReceiver(), intentFilter);
        }*/

        if (Constants.isDebug)
            // ACRA.init(this);

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)

                    .build();

        mGoogleApiClient.connect();
        DirectoryUtils.createAppDirectories(this);
        sharedPrefs = new SharedPrefs(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //startService(new Intent(new Intent(this, ConnectionService.class)));
            //Intent serviceIntent = new Intent(this, ConnectionService.class);
         startForegroundService(new Intent(new Intent(this, ConnectionService.class)));
        }
        else {
            startService(new Intent(new Intent(this, ConnectionService.class)));
        }
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });

    }

    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Example Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
    }
    public void handleUncaughtException(Thread thread, Throwable e) {
        //e.printStackTrace(); // not all Android versions will print the stack trace automatically

        if (apiManager.mReservedBooking != null) {
            sharedPrefs.setJson("latestjob", true);
            Intent intent = new Intent ();
            intent.setAction ("mobileapps.technroid.io.cabigate.SEND_LOG"); // see step 5.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK); // required when starting from Application
            startActivity(intent);
        }
        ;

        System.exit(1);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (Constants.isSandBox) {
            mLocation =new Location("");
            mLocation.setLatitude(31.5228892);
            mLocation.setLongitude(74.2668035);
            // mLocation.setLatitude(25.0979065);
            // mLocation.setLongitude(55.1634082);
        }

//        mLocationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(2 * 1000)        // 10 seconds, in milliseconds
//                .setFastestInterval(1 * 1000)
                ; // 1 second, in milliseconds
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000).setSmallestDisplacement(2)
        ; // 1 second, in milliseconds

        addLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null)
            return;

        if (location.getLongitude() == 0 && location.getLatitude() == 0)
            return;

        if (!isBetterLocation(location, mLocation))
            return;

        if (mListener != null) {
            if (location != null)
                mListener.onLocationChanged(location);
        }

        location.getLongitude();
        Log.e("loaction" + location.getLongitude(), "loaction" + location.getLatitude());
        mLocation = location;

        if (getLatitude() != 0 && getLongitude() != 0) {

            long timeDelta = (location.getTime() - mLocation.getTime()) / 1000;
            float distance = mLocation.distanceTo(location);
            float speed = distance / timeDelta;

            if (speed < 0.2)
                speed = 0;

            if (speed > 45)
                speed = 0;

            if (location.getSpeed() != 0)
                speed = location.getSpeed();
            if (!location.getProvider().equals(GPS_PROVIDER)) {
                mLocation.setBearing(normalizeDegree(mLocation.bearingTo(location)));
            }
        }

        mService.updateLocationOfDriver();
    }
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    private float normalizeDegree(float value) {
        if (value >= 0.0f && value <= 180.0f) {
            return value;
        } else {

            return 180 + (180 + value);
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocation != null)
            onLocationChangedListener.onLocationChanged(mLocation);
    }

    @Override
    public void deactivate() {
    }

    public void disconnectGoogleApi() {
        try {
            removeLocationUpdates();
            if (mGoogleApiClient.isConnected())
                mGoogleApiClient.disconnect();
        } catch (Exception e) {
        }
    }

    public void removeLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
        }
    }

    public void addLocationUpdates() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (Exception e)
        {
            try
            {
                mGoogleApiClient.connect();
            } catch (Exception ex)
            {
            }
        }
    }


    public  void  setLocation(Location location){
        mLocation=location;

    }
    public double getLatitude()
    {
        if (mLocation == null)
            return 0;
        else
            return mLocation.getLatitude();
    }

    public double getLongitude()
    {
        if (mLocation == null)
            return 0;
        else
            return mLocation.getLongitude();
    }

    public float getBearing()
    {
        if (mLocation == null)
            return 0;
        else
            return mLocation.getBearing();
    }

    public void startService()
    {
        Intent service = new Intent(this, ConnectionService.class);
        startService(service);
    }

    public void stopService()
    {
        Intent service = new Intent(this, ConnectionService.class);
        stopService(service);
    }

}