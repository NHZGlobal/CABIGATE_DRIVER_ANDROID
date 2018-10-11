package mobileapps.technroid.io.cabigate.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.api.ApiManager;
import mobileapps.technroid.io.cabigate.global.Constants;
import mobileapps.technroid.io.cabigate.helper.CommonMethods;
import mobileapps.technroid.io.cabigate.helper.Const;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;
import mobileapps.technroid.io.cabigate.helper.SnackBarDisplay;
import mobileapps.technroid.io.cabigate.models.DriverModel;
import retrofit.RetrofitError;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LoginActivity extends BaseActivity
{
    private EditText edtusername;
    private EditText edtPassword;

    private ImageButton loginBtn;
    private ProgressWheel progressBar;
    private EditText edtCompanyName;
    private SharedPrefs sharedPrefs;
    private String companyname;
    private String name;
    private String username;
    private SharedPreferences prefs;
    private TextView tvVersionName;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         sharedPrefs=new SharedPrefs(LoginActivity.this);
         prefs = this.getSharedPreferences(
                "cabigateauto", Context.MODE_PRIVATE);




// use a default value using new Date()
        String company = prefs.getString("company","");
        String username1 = prefs.getString("username", "");


       // checkPermission();

        if (Build.VERSION.SDK_INT >= 23) {
            requestAppPermissions();
        }

        tvVersionName = (TextView) findViewById(R.id.tvVersionName);
        edtusername = (EditText) findViewById(R.id.edtusername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtCompanyName = (EditText) findViewById(R.id.edtCompanyName);

        tvVersionName.setText(app.versionName);
        edtCompanyName.setText(company);
        edtusername.setText(username1);



        progressBar = (ProgressWheel) findViewById(R.id.progressBar);

        loginBtn = (ImageButton) findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                companyname = edtCompanyName.getText().toString();
                 username = edtusername.getText().toString();
                String password = edtPassword.getText().toString();


                if (!CommonMethods.isSet(username)) {
                    SnackBarDisplay.ShowSnackBar(LoginActivity.this, "Username is missing");
                    Animation shake = AnimationUtils.loadAnimation(v.getContext(), R.anim.shake);
                    edtusername.startAnimation(shake);
                } else if (!CommonMethods.isSet(companyname)) {
                    SnackBarDisplay.ShowSnackBar(LoginActivity.this, "Company name is missing");
                    Animation shake = AnimationUtils.loadAnimation(v.getContext(), R.anim.shake);
                    edtCompanyName.startAnimation(shake);
                } else if (!CommonMethods.isSet(password)) {
                    SnackBarDisplay.ShowSnackBar(LoginActivity.this, "Password is missing");
                    Animation shake = AnimationUtils.loadAnimation(v.getContext(), R.anim.shake);
                    edtPassword.startAnimation(shake);
                } else {


                    JSONObject jsonparams=new JSONObject();
                    try {
                        jsonparams.put(Const.COMPANY,companyname);
                        jsonparams.put(Const.USERNAME,username);
                        jsonparams.put(Const.PASSWORD,password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sweetIndicator.show();
                    ApiManager.getInstance().mUrlManager.login(jsonparams.toString()).cache().subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(observer);


                }

            }
        });


    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                checkPermission();
            } else {
                // Do as per your logic
            }

        }

    }
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
    private void requestAppPermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )

                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                                      checkPermission();
                            //Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                               showSettingsDialog();

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                checkPermission();
               // openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private Observer<DriverModel> observer = new Observer<DriverModel>() {
        @Override
        public void onCompleted() {
            sweetIndicator.dismiss();
            Constants.logoutcheck = false;

        }

        @Override
        public void onError(Throwable error) {
            sweetIndicator.dismiss();
            RetrofitError e = (RetrofitError) error;
            if (e.getKind() == RetrofitError.Kind.NETWORK) {
                SnackBarDisplay.ShowSnackBar(LoginActivity.this, getResources().getString(R.string.error_network_subtitle));

            } else if (e.getKind() == RetrofitError.Kind.HTTP) {
                SnackBarDisplay.ShowSnackBar(LoginActivity.this, getResources().getString(R.string.error_server_subtitle));


            } else {
                SnackBarDisplay.ShowSnackBar(LoginActivity.this, getResources().getString(R.string.error_uncommon_subtitle));

            }


        }

        @Override
        public void onNext(DriverModel driverModel) {


            if (driverModel.status == 1) {


                prefs.edit().putString("company", companyname).apply();
                prefs.edit().putString("username",username).apply();
                sharedPrefs.setLogin(true);
                Gson gson = new Gson();
                String response = gson.toJson(driverModel.response);
                Log.e("Login", response);

                sharedPrefs.setJson(Const.USER_DETAIL, response);
                app.myDriver=driverModel.response;
                app.apiManager = new ApiManager(app.getApplicationContext());
                if(!app.mGoogleApiClient.isConnected()){
                    app.mGoogleApiClient.connect();
                }
                app.startService();
                MainActivity.openActivity(LoginActivity.this);
                Constants.permissioncheck = true;
                finish();


            } else {
                SnackBarDisplay.ShowSnackBar(LoginActivity.this, driverModel.error_msg);

            }

        }
    };
        }