package mobileapps.technroid.io.cabigate.ui.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.pnikosis.materialishprogress.ProgressWheel;

import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;
import mobileapps.technroid.io.cabigate.ui.widgets.alertbox.SweetAlertDialog;
import mobileapps.technroid.io.cabigate.utilities.Utilities;

public class SplashActivity extends BaseActivity
{
    private long SPLASH_TIME = 1000;
    private String gcmRegId;
    private ProgressWheel progressBar;
    private SharedPrefs sharedPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPrefs=new SharedPrefs(SplashActivity.this);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        progressBar = (ProgressWheel) findViewById(R.id.progressBar);
        startSplashAnimation();
    }

    private Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            finish();
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.do_nothing);
        }
    };


    private void startSplashAnimation()
    {
        if (Utilities.checkPlayServices(SplashActivity.this))
        {
            progressBar.setVisibility(View.VISIBLE);

                if(sharedPrefs.isLogin())
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {

                            MainActivity.openActivity(SplashActivity.this);
                            finish();
                           // getDriver();
                        }
                    });
                }
                else{
                    new Handler().postDelayed(runnable, SPLASH_TIME);
                }

        }
        else
        {
            new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.app_name))
                    .setContentText("Please install or update Google play services")
                    .show();
        }
    }

}
