package mobileapps.technroid.io.cabigate.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.app.MyApplication;
import mobileapps.technroid.io.cabigate.helper.DatabaseHelper;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;
import mobileapps.technroid.io.cabigate.helper.SnackBarDisplay;
import mobileapps.technroid.io.cabigate.ui.widgets.alertbox.SweetAlertDialog;
import retrofit.RetrofitError;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity {

    public SweetAlertDialog sweetIndicator;
    public MyApplication app;


    @Nullable
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @Bind(R.id.mainContainer)
    View mainContainer;

    private int REQUEST_LOCATION = 11;
    private LocationManager locationManager;
    String TAG = getClass().getName();


    private MenuItem inboxMenuItem;
    public SharedPrefs sharedPrefs;
    private PowerManager.WakeLock wl;
    public DatabaseHelper databaseHelper;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        sharedPrefs = new SharedPrefs(this);
        Binds();
    }

    protected void Binds() {
        ButterKnife.bind(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (MyApplication) getApplication();

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(
                Context.POWER_SERVICE);
        this.wl = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE,
                TAG);
        wl.acquire();


        databaseHelper = new DatabaseHelper(BaseActivity.this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sweetIndicator = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetIndicator.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        sweetIndicator.setTitleText("Please wait...");
        sweetIndicator.setCancelable(false);
    }

    @Override
    public void onPause() {
        //app.removeLocationUpdates();
        app.isAppOnline = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wl.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.isAppOnline = true;
        if (sharedPrefs.isLogin()) {
            locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    isLocation();
                } else {
                    requestPermission();
                }
            } else {
                if (checkPermission()) {
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        showSettingsAlert();
                    } else {
                        if (!isNetworkAvailable()) {
                           // Toast.makeText(app, "You are Not Connected To internet", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    requestPermission();
                }
               /* if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showSettingsAlert();
                }*/

            }

        }

    }

    private boolean checkPermission() {
        int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void logoutUserForWrongKey() {


        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.warning_oops))
                .setContentText(getString(R.string.error_relog))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent i = new Intent(BaseActivity.this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        sweetAlertDialog.cancel();
                    }
                })
                .show();
    }


    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void displayError(Throwable error) {

        if (error != null) {
            RetrofitError e = (RetrofitError) error;
            if (e.getKind() == RetrofitError.Kind.NETWORK) {
                SnackBarDisplay.ShowSnackBar(BaseActivity.this, getResources().getString(R.string.error_network_subtitle));
            } else if (e.getKind() == RetrofitError.Kind.HTTP) {
                SnackBarDisplay.ShowSnackBar(BaseActivity.this, getResources().getString(R.string.error_server_subtitle));
            } else {
                SnackBarDisplay.ShowSnackBar(BaseActivity.this, getResources().getString(R.string.error_uncommon_subtitle));
            }
        }
    }


    public void isLocation() {
        /*if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();

        }else{
            if (!locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showSettingsAlert();
            }
        }*/
        if (!locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            showSettingsAlert();
        } else {
            if (!isNetworkAvailable()) {

                //Toast.makeText(app, "You are Not Connected To internet", Toast.LENGTH_LONG).show();
            }
        }

    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BaseActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(BaseActivity.this);
            alertDialog.setCancelable(false);

            // Setting Dialog Title
            alertDialog.setTitle("Alert");

            // Setting Dialog Message
            alertDialog.setMessage("Location permission allows us to access location data. Please allow in App Settings for additional functionality.");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    openSettings();
                }
            });


            // Showing Alert Message
            alertDialog.show();


        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(app, "Permission Granted, Now you can access location data.", Toast.LENGTH_SHORT).show();
                } else {

                    requestPermission();

                }
                break;
        }
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    /*private void requestCameraPermission() {
              if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                  Snackbar.make(mainContainer, "Enable gps to get current loaction",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(BaseActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_LOCATION);
                        }
                    })
                    .show();
        } else {
             ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                     REQUEST_LOCATION);
        }
        // END_INCLUDE(camera_permission_request)
    }

    *//**
     * Callback received when a permissions request has been completed.
     *//*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_LOCATION) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "Lo permission has now been granted. Showing preview.");
                Snackbar.make(mainContainer, "permission has now been granted",
                        Snackbar.LENGTH_SHORT).show();
                app.addLocationUpdates();
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
                Snackbar.make(mainContainer, "permission has now been granted",
                        Snackbar.LENGTH_SHORT).show();

            }
            // END_INCLUDE(permission_result)

        }  else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }*/
/*}*/


}
