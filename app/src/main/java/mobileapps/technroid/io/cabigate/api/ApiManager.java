package mobileapps.technroid.io.cabigate.api;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import mobileapps.technroid.io.cabigate.app.MyApplication;
import mobileapps.technroid.io.cabigate.global.Constants;
import mobileapps.technroid.io.cabigate.models.DriverModel;
import mobileapps.technroid.io.cabigate.models.Job;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by Muhammad on 21/03/2015.
 */
public class ApiManager
{
    private static ApiManager mInstance;
    public static synchronized ApiManager getInstance()
    {
        return mInstance;
    }

    private static Context mContext;

    public static RestAdapter mApiAdapter;
    public static URLManager  mUrlManager;
    public static SocketManager mSocketManager;

    public ThreadPoolExecutor executor;

    public Job mBooking;
    public Job mReservedBooking = null;

    public static void clear()
    {
        if (mInstance != null)
        {
            mSocketManager.broadCastOffline();
            mContext.stopService(new Intent(mContext, ConnectionService.class));
            mInstance = null;
            mSocketManager.clear();
        }
    }
    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(); //2015-01-18 15:48:56


    public ApiManager(Context mContext)
    {
        this.mContext = mContext;
        mSocketManager = new SocketManager(mContext);
        mInstance = this;

        MyApplication.getInstance().myDriver = DriverModel.initWithCurrentDeviceValues(mContext);

        mApiAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Constants.BASE_URL)
                .build();
        System.setProperty("http.keepAlive", "false");
        mUrlManager = mApiAdapter.create(URLManager.class);

        Cache cache = null;
        OkHttpClient okHttpClient = null;

        try {
            File cacheDir = new File(MyApplication.getInstance().getCacheDir().getPath(), "cabigate.json");
            cache = new Cache(cacheDir, 10 * 1024 * 1024);
            okHttpClient = new OkHttpClient();
            okHttpClient.setCache(cache);
        } catch (Exception e) {
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .setClient(new OkClient(okHttpClient))
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestInterceptor.RequestFacade request) {
                        request.addHeader("Cache-Control", "public, max-age=" + 60 * 60 * 4);
                    }
                })
                .build();
        mUrlManager = restAdapter.create(URLManager.class);



        executor = new ThreadPoolExecutor(
                5,
                10,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );
        executor.allowCoreThreadTimeOut(true);

    }
}

