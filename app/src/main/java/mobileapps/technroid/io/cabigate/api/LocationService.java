package mobileapps.technroid.io.cabigate.api;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import mobileapps.technroid.io.cabigate.global.Constants;
import mobileapps.technroid.io.cabigate.helper.Const;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;
import mobileapps.technroid.io.cabigate.helper.SphericalUtil;

//import com.google.android.gms.common.GooglePlayServicesUtil;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, SensorEventListener {

    private static final String TAG = "LocationService";

    // use the websmithing defaultUploadWebsite for testing and then check your
    // location with your browser here: https://www.websmithing.com/gpstracker/displaymap.php


    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    // INTERNAL STATE
    private boolean movingState = false;

    // Accelerometer-related
    private int iAccelReadings, iAccelSignificantReadings;
    private long iAccelTimestamp;
    private SensorManager mSensorManager;

    @Override
    public void onCreate() {
        super.onCreate();


    }


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
    public int onStartCommand(Intent intent, int flags, int startId) {
        // if we are currently trying to get a location and the alarm manager has called this again,
        // no need to start processing a new location.
        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
            startAccelerometer();
        }

        return START_NOT_STICKY;
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }

    protected void sendLocationDataToWebsite(Location location) {
        // formatted for mysql datetime format
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date date = new Date(location.getTime());
        SharedPrefs sharedPrefs = new SharedPrefs(this);


        float totalDistanceInMeters = sharedPrefs.getTotalDistance();//sharedPreferences.getFloat("totalDistanceInMeters", 0f);
        if (Constants.locationc) {
            sharedPrefs.setTotalDistance(0);
            totalDistanceInMeters = 0;
            Constants.locationc = false;
        }
        boolean firstTimeGettingPosition = sharedPrefs.getfirstTimeGettingPosition();
        if (true) {
            if (firstTimeGettingPosition) {
                sharedPrefs.setfirstTimeGettingPosition(false);
            } else {
                Location previousLocation = new Location("");
                // previousLocation.setLatitude(sharedPreferences.getFloat("previousLatitude", 0f));
                // previousLocation.setLongitude(sharedPreferences.getFloat("previousLongitude", 0f));
                previousLocation.setLatitude(sharedPrefs.getPreviousLatitude());
                previousLocation.setLongitude(sharedPrefs.getPreviousLongitude());
                double distance = SphericalUtil.computeDistanceBetween(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(previousLocation.getLatitude(), previousLocation.getLongitude()));//CommonMethods.distance(location.getLatitude(),location.getLongitude(),previousLocation.getLatitude(),previousLocation.getLongitude(),'k');
                //float distance = location.distanceTo(previousLocation);
                if (location.getSpeed() != 0) {

                    totalDistanceInMeters += distance / 1000;
                    sharedPrefs.setTotalDistance(totalDistanceInMeters);
                }
                //   editor.putFloat("totalDistanceInMeters", totalDistanceInMeters);
            }


            sharedPrefs.setPreviousLatitude((float) location.getLatitude());
            sharedPrefs.setPreviousLongitude((float) location.getLongitude());


            Double speedInMilesPerHour = location.getSpeed() * 2.2369;
            //  requestParams.put("speed",  Integer.toString(speedInMilesPerHour.intValue()));

        /*try {
            requestParams.put("date", URLEncoder.encode(dateFormat.format(date), "UTF-8"));
        } catch (UnsupportedEncodingException e) {}
*/
            //  requestParams.put("locationmethod", location.getProvider());

            if (totalDistanceInMeters > 0) {
                //     requestParams.put("distance", String.format("%.1f", totalDistanceInMeters / 1609)); // in miles,
            } else {
                //      requestParams.put("distance", "0.0"); // in miles
            }

            //  sendBroadcast(String.format("%.1f", totalDistanceInMeters));


        }
        stopSelf();
        // requestParams.put("username", sharedPreferences.getString("userName", ""));
        //  requestParams.put("phonenumber", sharedPreferences.getString("appID", "")); // uuid
        //   requestParams.put("sessionid", sharedPreferences.getString("sessionID", "")); // uuid

        //  Double accuracyInFeet = location.getAccuracy()* 3.28;
        //   requestParams.put("accuracy",  Integer.toString(accuracyInFeet.intValue()));

        Double altitudeInFeet = location.getAltitude() * 3.28;
        // requestParams.put("extrainfo",  Integer.toString(altitudeInFeet.intValue()));

        // requestParams.put("eventtype", "android");

        Float direction = location.getBearing();
        //requestParams.put("direction",  Integer.toString(direction.intValue()));


       /* LoopjHttpClient.get(uploadWebsite, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                LoopjHttpClient.debugLoopJ(TAG, "sendLocationDataToWebsite - success", uploadWebsite, requestParams, responseBody, headers, statusCode, null);
                stopSelf();
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                LoopjHttpClient.debugLoopJ(TAG, "sendLocationDataToWebsite - failure", uploadWebsite, requestParams, errorResponse, headers, statusCode, e);
                stopSelf();
            }
        });*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.e(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy() + " speed: " + location.getSpeed());

            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
            if (location.getAccuracy() < 300.0f && location.getSpeed() < 6.95f) {
                //stopLocationUpdates();
                sendLocationDataToWebsite(location);
            }
        }
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    private void sendBroadcast(String distance) {
        Intent intent = new Intent("message"); //put the same message as in the filter you used in the activity when registering the receiver
        intent.putExtra("distance", distance);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");

        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient connection has been suspend");
    }






    // ACCELEROMETER METHODS
    public void startAccelerometer() {
        iAccelReadings = 0;
        iAccelSignificantReadings = 0;
        iAccelTimestamp = System.currentTimeMillis();
        // should probably store handles to these earlier, when service is created
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void stopAccelerometer() {
        mSensorManager.unregisterListener( this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double accel, x, y, z;
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        iAccelReadings++;
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        accel = Math.abs(
                Math.sqrt(
                        Math.pow(x,2)
                                +
                                Math.pow(y,2)
                                +
                                Math.pow(z,2)
                )
        );
        // Was 0.6. Lowered to 0.3 (plus gravity) to account for smooth motion from Portland Streetcar
        if (
                accel > (9.8 +0.30)
                        ||
                        accel < (9.8 - 0.30)
                ) {
            iAccelSignificantReadings++;
        }

        //Debug(String.format("event: %f %f %f %f %f", x, y, z, accel, 0.600));

        // Get readings for 1 second
        // Maybe we should sample for longer given that I've lowered the threshold
        if ( (System.currentTimeMillis() - iAccelTimestamp) < 2000) return;

        stopAccelerometer();
        if (((1.0*iAccelSignificantReadings) / iAccelReadings) > 0.30) {
            setMovingState(true);
            } else {
            setMovingState(false);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // can be safely ignored
    }



    protected void setMovingState(boolean newMovingState) {
       /* String s;
        NotificationManager nm;
        PendingIntent mPendingIntent;

        if (movingState != newMovingState) {
            // State has changed. Log the time.
            fSince = System.currentTimeMillis();
        }

        if (newMovingState == true) {
            if (currentBestLocation != null) {
                s = String.format("At " + currentBestLocation.getSpeed() + " m/s");
            } else {
                s = String.format("Awaiting speed reading");
            }
        } else {
            if (currentBestLocation != null) {
                s = String.format("Previous speed: " + currentBestLocation.getSpeed() + " m/s");
            } else {
                s = String.format(". . .");
            }
        }

        // Could detect long periods of motion here and give a badge or say something funny

        if (mNotificationBuilder == null) {
            // The PendingIntent to launch our activity if the user selects this notification
            mPendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    new Intent(this, MainActivity.class),
                    0
            );
            mNotificationBuilder = new Notification.Builder(this).setContentIntent(mPendingIntent);
        }
        mNotificationBuilder.setContentTitle( (newMovingState ? "Moving" : "Stationary") )
                .setContentText(s)
                .setSmallIcon( (newMovingState ? R.drawable.moving : R.drawable.stationary) );
        mNotificationBuilder.setWhen(fSince);
        mNotification = mNotificationBuilder.build();

        nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, mNotification);*/

        movingState = newMovingState;
    }













}
