package mobileapps.technroid.io.cabigate.ui.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.joda.time.Interval;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.api.ApiManager;
import mobileapps.technroid.io.cabigate.app.MyApplication;
import mobileapps.technroid.io.cabigate.global.Constants;
import mobileapps.technroid.io.cabigate.gpstrackmodule.GpsTrackerAlarmReceiver;
import mobileapps.technroid.io.cabigate.helper.Const;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;
import mobileapps.technroid.io.cabigate.helper.SnackBarDisplay;
import mobileapps.technroid.io.cabigate.models.Job;
import mobileapps.technroid.io.cabigate.models.WayPoint;
import mobileapps.technroid.io.cabigate.ui.activity.JobViewActivity;
import mobileapps.technroid.io.cabigate.ui.widgets.alertbox.SweetAlertDialog;
import mobileapps.technroid.io.cabigate.utilities.Utilities;
import retrofit.client.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public final class FragmentCurrent extends Fragment {
	public static boolean check = false;
	@Bind(R.id.tvEmptyText)
	TextView tvEmpty;
	@Bind(R.id.ll_container_job1)
	View ll_container_job1;
	@Bind(R.id.bottomContainer)
	View bottomContainer;

	/*Assigned job*/
	@Bind(R.id.return_journey_container)
	View returnJourneyContainer;
	@Bind(R.id.tvDropOff)
	TextView tvDropOff;
	@Bind(R.id.tvPhone)
	TextView tvPhone;
	@Bind(R.id.tvDropOffTitle)
	TextView tvDropOffTitle;
	@Bind(R.id.tvNavDropOff)
	TextView tvNavDropOff;
	@Bind(R.id.iv_tick)
	ImageView iv_tick;

	@Bind(R.id.tvPickUp)
	TextView tvPickUp;
	@Bind(R.id.tvPickUpTitle)
	TextView tvPickUpTitle;
	@Bind(R.id.tvNavPickup)
	TextView tvNavPickup;
	@Bind(R.id.iv_tick1)
	ImageView iv_tick1;

	@Bind(R.id.tvPickUp1)
	TextView tvPickUp1;
	@Bind(R.id.tvPickUpTitle1)
	TextView tvPickUpTitle1;
	@Bind(R.id.tvNavPickup1)
	TextView tvNavPickup1;
	@Bind(R.id.iv_tick11)
	ImageView iv_tick11;

	@Bind(R.id.ratingBarNumberOfPerson)
	RatingBar ratingBarNumberOfPerson;
	@Bind(R.id.ratingBarbag)
	RatingBar ratingBarbag;
	@Bind(R.id.tvTime)
	TextView tvTime;
	@Bind(R.id.tvFair)
	TextView tvFair;
	@Bind(R.id.tvName)
	TextView tvName;
	@Bind(R.id.tvDispatechedid)
	TextView tvDispatechedid;


	@Bind(R.id.cb_avilable)
	CheckBox cb_avilable;
	@Bind(R.id.cb_busy)
	CheckBox cb_busy;
	@Bind(R.id.cb_away)
	CheckBox cb_away;
    @Bind(R.id.tv_availabel_time)
	TextView tv_availabel_time;

	@Bind(R.id.btnCallout)
	Button btnCallout;
	@Bind(R.id.btnConfirm)
	Button btnConfirm;
	@Bind(R.id.btnWait)
	Button btnWait;
	@Bind(R.id.btnPob)
	Button btnPob;
	@Bind(R.id.topPanel)
	View topPanel;
	@Bind(R.id.avilableContainer)
	View avilableContainer;
	@Bind(R.id.jobContainer)
	View jobContainer;
    @Bind(R.id.cb_away_container)
	View cb_away_container;
    @Bind(R.id.btn_finish)
	Button btn_finish;

	@Bind(R.id.way_point_container)
	LinearLayout way_point_container;
	private boolean flag;
	private String payvia="cash";
	private boolean jobflag;
	private SharedPrefs sharedPrefs;
	/*gps tracking*/
	private int intervalInMinutes = 1;
	private AlarmManager alarmManager;
	private Intent gpsTrackerIntent;
	private PendingIntent pendingIntent;
	private CountDownTimer mTimer;
	private SweetAlertDialog sweetIndicator;
    public static int status;
	private MediaPlayer mediaPlayer;
	private boolean isNewRequest=true;
	private AlertDialog jobdilog;
	private int REQUEST_CALL_PHONE=122;



	public void syncJob(Job job){



		if(MyApplication.getInstance().apiManager.mReservedBooking!=null){

			assignJob(MyApplication.getInstance().apiManager.mReservedBooking);
			updateDriverStatus("busy", "0");


		}else {
			jobflag = false;
			cancelAlarmManager();
			sharedPrefs.setJson(Const.JOURNYTYPE, false);
			sharedPrefs.setJson(Const.CURREMT_WAY_POINT, 0);
			jobstatus(cb_avilable);
			sharedPrefs.intatlizeGpsTracker();
			ll_container_job1.setVisibility(View.INVISIBLE);
			bottomContainer.setVisibility(View.INVISIBLE);
			status=6;
			updateDriverStatus("free", "0");
			sharedPrefs.setJson(Const.ISJOB, false);
			jobQueue(MyApplication.getInstance().apiManager.mReservedBooking, null);

		}


	}


	void displayLocationColor(boolean enable){
		if(enable){
			tvDropOff.setTextColor(getResources().getColor(R.color.white));
			tvDropOffTitle.setTextColor(getResources().getColor(R.color.white));
			tvNavDropOff.setVisibility(View.INVISIBLE);
			iv_tick.setVisibility(View.INVISIBLE);
			tvPickUp.setTextColor(getResources().getColor(R.color.colorAccent));
			tvPickUpTitle.setTextColor(getResources().getColor(R.color.colorAccent));
			tvNavPickup.setVisibility(View.VISIBLE);

		}else{

			Job job=MyApplication.getInstance().apiManager.mReservedBooking;
			int currentWayPoint=sharedPrefs.getJsonInt(Const.CURREMT_WAY_POINT);
			int stopCount=Integer.parseInt(job.getStop_count());
			if(stopCount>0&&currentWayPoint!=stopCount){
				if(sharedPrefs.getJsonInt(Const.CURREMT_WAY_POINT)==0){
					tvPickUp.setTextColor(getResources().getColor(R.color.white));
					tvPickUpTitle.setTextColor(getResources().getColor(R.color.white));
					tvNavPickup.setVisibility(View.INVISIBLE);
					iv_tick.setVisibility(View.VISIBLE);
					selectWayPoint(currentWayPoint,true);
				}else{

					selectWayPoint(currentWayPoint,true);
					unSelectWayPoint(currentWayPoint);
				}

				return;
			}else{
				tvPickUp.setTextColor(getResources().getColor(R.color.white));
				tvPickUpTitle.setTextColor(getResources().getColor(R.color.white));
				tvNavPickup.setVisibility(View.INVISIBLE);
				iv_tick.setVisibility(View.VISIBLE);
			}
		if(stopCount>0){
			unSelectWayPoint(currentWayPoint);
		}
		tvDropOff.setTextColor(getResources().getColor(R.color.colorAccent));
		tvDropOffTitle.setTextColor(getResources().getColor(R.color.colorAccent));
		tvNavDropOff.setVisibility(View.VISIBLE);
		iv_tick.setVisibility(View.VISIBLE);

			if(!job.journey_type.equals("single")){
				if (returnJourneyContainer.getVisibility() == View.VISIBLE){
					journeyReturn();

				}else{
					returnJourneyContainer.setVisibility(View.VISIBLE);
				}

			}

		}

	}




    void journeyReturn(){
		tvDropOff.setTextColor(getResources().getColor(R.color.white));
		tvDropOffTitle.setTextColor(getResources().getColor(R.color.white));
		tvNavDropOff.setVisibility(View.INVISIBLE);
		iv_tick1.setVisibility(View.VISIBLE);

		tvPickUp1.setTextColor(getResources().getColor(R.color.colorAccent));
		tvPickUpTitle1.setTextColor(getResources().getColor(R.color.colorAccent));
		tvNavPickup1.setVisibility(View.VISIBLE);

		sharedPrefs.setJson(Const.JOURNYTYPE, true);

	}

	void createWayPoints(WayPoint wayPoint,int waypoint){

		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.row_way_point, null);
		TextView tvWayPoint=(TextView) view.findViewById(R.id.tvWayPoint);
		TextView tvWayPointTitle=(TextView) view.findViewById(R.id.tvWayPointTitle);
		tvWayPoint.setText(wayPoint.getPoint());
		tvWayPointTitle.setText("WAYPOINT " + (waypoint + 1));

		way_point_container.addView(view);

	}


	void  unSelectWayPoint(int currentWayPoint){

				View view =way_point_container.getChildAt(currentWayPoint-1);
				TextView tvWayPoint=(TextView) view.findViewById(R.id.tvWayPoint);
				TextView tvWayPointTitle=(TextView) view.findViewById(R.id.tvWayPointTitle);
				TextView tvNavWayPoint=(TextView) view.findViewById(R.id.tvNavWayPoint);
				ImageView iv_tick1=(ImageView) view.findViewById(R.id.iv_tick1);

		        tvWayPoint.setTextColor(getResources().getColor(R.color.white));
				tvWayPointTitle.setTextColor(getResources().getColor(R.color.white));
				tvNavWayPoint.setVisibility(View.INVISIBLE);
				iv_tick1.setVisibility(View.VISIBLE);
	}
	void  selectWayPoint(int currentWayPoint,boolean isnew){

		if(isnew)
			sharedPrefs.setJson(Const.CURREMT_WAY_POINT,currentWayPoint+1);
				View view =way_point_container.getChildAt(currentWayPoint);

				TextView tvWayPoint=(TextView) view.findViewById(R.id.tvWayPoint);
				TextView tvWayPointTitle=(TextView) view.findViewById(R.id.tvWayPointTitle);
				TextView tvNavWayPoint=(TextView) view.findViewById(R.id.tvNavWayPoint);
		        tvNavWayPoint.setOnClickListener(wayPointNavClickListener);
		        tvNavWayPoint.setTag(currentWayPoint);
				ImageView iv_tick1=(ImageView) view.findViewById(R.id.iv_tick1);

		        tvWayPoint.setTextColor(getResources().getColor(R.color.colorAccent));
				tvWayPointTitle.setTextColor(getResources().getColor(R.color.colorAccent));
				tvNavWayPoint.setVisibility(View.VISIBLE);
				iv_tick1.setVisibility(View.INVISIBLE);

	}




	View.OnClickListener wayPointNavClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			int index=(int)v.getTag();
			Job job=MyApplication.getInstance().apiManager.mReservedBooking;
			WayPoint wayPoint=job.getWaypoints().get(index);

			Uri gmmIntentUri = Uri.parse("google.navigation:q="+wayPoint.getLat()+","+ wayPoint.getLng());
			Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
			mapIntent.setPackage("com.google.android.apps.maps");
			startActivity(mapIntent);

			/*String uri = "geo: "+wayPoint.getLat()+","+ wayPoint.getLng();
			startActivity(new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse(uri)));*/


		}
	};




	public void updateDriverStatus(String status,String time){

		JSONObject jsonObject=new JSONObject();
		try {
			jsonObject.put(Const.COMPANYID,MyApplication.getInstance().myDriver.companyID);
			jsonObject.put(Const.USERID,MyApplication.getInstance().myDriver.userid);
			jsonObject.put(Const.TOKEN,MyApplication.getInstance().myDriver.token);
			jsonObject.put("eta",time);
			jsonObject.put(Const.STATUS, status);
			} catch (JSONException e) {
			e.printStackTrace();
		}

		ApiManager.getInstance().mUrlManager.statusDriverUpdate(jsonObject.toString()).cache().
				subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observerStatus);
	}


	public void actualFare(Job job){
         status=7;
		JSONObject jsonObject=new JSONObject();
		sweetIndicator.show();
		try {

			/*{"duration":"600","distance":"325","waiting_time":"7200","jobid":"33","userid":"1"}*/
			Date enddate=new Date();
			Date startdate=new Date(Long.parseLong(sharedPrefs.getJson("jobstarttime")));

			long duration  = (enddate.getTime() - startdate.getTime())/1000;
			jsonObject.put(Const.JOBID, job.getJobid());
			jsonObject.put(Const.USERID, MyApplication.getInstance().myDriver.userid);
			jsonObject.put(Const.DURATION,duration+"");
			jsonObject.put(Const.DISTANCE, sharedPrefs.getTotalDistance());
			jsonObject.put(Const.WAITING_TIME, 7000+"");
			} catch (JSONException e) {
			e.printStackTrace();
		}


		ApiManager.getInstance().mUrlManager.actualFare(jsonObject.toString()).cache().
				subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observerStatus);

	}


   public  void updateStatus(Job job,boolean isnew){
	/*   14 - Update JOB Status (callout,wait,pob)
	   ---------------------------------------------
			   http://api.cabigate.com/index.php/updatestatus?companyid=2100&userid=2&jobid=18&status=callout&token=2100
*/
         if(job.issync){
			 job.issync=false;
         /*There is no need to call this app is case of job sync*/
			 switch (job.status1){
				 case 0:
					 jobButton(btnCallout);
					 break;
				 case 1:
					 jobButton(btnWait);
					 break;
				 case 2:
					 jobButton(btnPob);
					 break;
			 }




			 return;
		 }
	   isNewRequest=isnew;
	   sweetIndicator.show();
	   MyApplication app=MyApplication.getInstance();
	   ApiManager.getInstance().mUrlManager.updateStatus(
			   app.myDriver.companyID, app.myDriver.userid, job.getCurrentstatus(), app.myDriver.token, job.getJobid()
	   ).cache().
			   subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observerStatus);



   }
 public  void  jobDeliver(String jobfeeback,Job job){
   /* use  in case of observer*/
	 status=3;
	 String rating="12";
	 String comment=jobfeeback;
     sweetIndicator.show();

	   MyApplication app=MyApplication.getInstance();
	   ApiManager.getInstance().mUrlManager.deliverJob(
			   app.myDriver.companyID, app.myDriver.userid, app.myDriver.token, job.getJobid(), rating, comment, job.getFare(), payvia
	   ).cache().
			   subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observerStatus);
   }

	public  void  jobCancel(String jobfeeback,Job job){
   /* use  in case of observer*/
		status=4;
		String rating="12";
		String comment=jobfeeback;
		sweetIndicator.show();

		MyApplication app = MyApplication.getInstance();
		ApiManager.getInstance().mUrlManager.canceljob(
				app.myDriver.companyID, app.myDriver.userid, jobfeeback, app.myDriver.token, job.getJobid()
		).cache().
				subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observerStatus);



	}


	@OnClick(R.id.btn_finish)
	void finishAway(View view){
		status=6;
		sweetIndicator.show();
		sharedPrefs.setJson(Const.AWAYTIME, false);
		updateDriverStatus("free", "0");
	}

	@OnClick(R.id.btnCallout)
	void callOut(View view){
		status=0;
		MyApplication.getInstance().apiManager.mReservedBooking.setCurrentstatus("callout");
		MyApplication.getInstance().apiManager.mReservedBooking.status1 = status;
		updateStatus(MyApplication.getInstance().apiManager.mReservedBooking, true);

	}

	@OnClick(R.id.btnWait)
	void wait(View view){
		status=1;
		MyApplication.getInstance().apiManager.mReservedBooking.getBags();
		MyApplication.getInstance().apiManager.mReservedBooking.status1 = status;
		MyApplication.getInstance().apiManager.mReservedBooking.setCurrentstatus("wait");
		updateStatus(MyApplication.getInstance().apiManager.mReservedBooking, true);
	}

	@OnClick(R.id.btnPob)
	void pob(View view){
		status=2;
		if(check) {
			Constants.locationc = true;
		}
		MyApplication.getInstance().apiManager.mReservedBooking.status1 = status;
		MyApplication.getInstance().apiManager.mReservedBooking.setCurrentstatus("pob");
		updateStatus(MyApplication.getInstance().apiManager.mReservedBooking, true);
		if(check) {
			startTrackingLocation();
			check = false;
		}
	}
	@OnClick(R.id.btnConfirm)
	void deliver(View view){
	//	actualFare(MyApplication.getInstance().apiManager.mReservedBooking);
		Job job=MyApplication.getInstance().apiManager.mReservedBooking;
		if((!job.getPayment_type().equals("account")&&!job.getPayment_type().equals("prepaid"))) {

			/*if(job.getTariff().equals("0")||sharedPrefs.getTotalDistance()==0.0) {
				jobFairCompleteDilaog(MyApplication.getInstance().apiManager.mReservedBooking, "12");
			}else*/{

              actualFare(job);
			}

		} else {
			jobCompleteDilaog(MyApplication.getInstance().apiManager.mReservedBooking);
		}

	}

	@OnClick(R.id.btnCancel)
	void cannel(View view){

		sosDialog();



	}




	public void sosDialog() {


		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.job_cancel_confirm_dialog, null);
		Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);
		Button btnCancel = (Button) outerView.findViewById(R.id.btnCancel);

		final AlertDialog.Builder alBuilder = new AlertDialog.Builder(getActivity());

		alBuilder.setView(outerView)
		;
		final AlertDialog ad = alBuilder.show();

		ad.setCancelable(false);

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();


			}
		});

		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();

				jobCancelDilaog(MyApplication.getInstance().apiManager.mReservedBooking);


			}
		});

	}




	@OnClick(R.id.tvNavDropOff)
	void navDropOff(){
		Job job=MyApplication.getInstance().apiManager.mReservedBooking;
		Uri gmmIntentUri = Uri.parse("google.navigation:q="+job.getDrop_lat()+","+ job.getDrop_lng());
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
		mapIntent.setPackage("com.google.android.apps.maps");
	startActivity(mapIntent);
		/*String uri = "geo: "+job.getDrop_lat()+","+job.getDrop_lng();
		//String uri = "geo: 31.554606,74.357158";
		startActivity(new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse(uri)));*/

	}
	@OnClick(R.id.tvNavPickup1)
	void navPickUp1(){
		Job job=MyApplication.getInstance().apiManager.mReservedBooking;
		Uri gmmIntentUri = Uri.parse("google.navigation:q="+job.getPickup_lat()+","+ job.getPickup_lng());
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
		mapIntent.setPackage("com.google.android.apps.maps");
		startActivity(mapIntent);
		/*String uri = "geo: "+job.getPickup_lat()+","+ job.getPickup_lng();
		startActivity(new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse(uri)));*/

	}


	@OnClick(R.id.tvNavPickup)
	void navPickUp(){
		Job job=MyApplication.getInstance().apiManager.mReservedBooking;
		Uri gmmIntentUri = Uri.parse("google.navigation:q="+job.getPickup_lat()+","+ job.getPickup_lng());
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
		mapIntent.setPackage("com.google.android.apps.maps");
		//startActivity(mapIntent);
	/*	String uri = "geo: "+job.getPickup_lat()+","+ job.getPickup_lng();
		startActivity(new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse(uri)));*/

	}


	public static FragmentCurrent newInstance(boolean flag,boolean currentframnet,Job job) {

		Bundle args = new Bundle();
		args.putBoolean(Const.TIME, flag);
		args.putBoolean(Const.CURRENT_FRAGMENT
				, currentframnet);
		args.putSerializable(Const.JOB_CODE, job);

		FragmentCurrent fragment = new FragmentCurrent();
		fragment.setArguments(args);
		return fragment;
	}

   void jobstatus(CheckBox cb){

	   cb_away.setEnabled(false);
	   cb_avilable.setEnabled(false);
	   cb_busy.setEnabled(false);

	   cb_avilable.setChecked(false);
	   cb_busy.setChecked(false);
	   cb_avilable.setChecked(false);

	   cb.setChecked(true);
	   cb_away_container.setClickable(false);
	   cb_away_container.setEnabled(false);
	     if(cb.getId()==cb_avilable.getId()){
		     cb_away_container.setClickable(true);
			 cb_away_container.setEnabled(true);
			 cb_away.setChecked(false);
	   }

   }

	@Override
	public void onResume() {
		super.onResume();
		if(sharedPrefs.isJson(Const.AWAYTIME)&&sharedPrefs.isJson(Const.SHIFTIN)){
			Date date=new Date(	Long.parseLong(sharedPrefs.getJson(Const.AWAYSTARTTIME)));
			Date nowdate=new Date();
			long second = Long.parseLong(sharedPrefs.getJson(Const.AWAYTIME));
			long duration  = nowdate.getTime() - date.getTime();

			Interval interval = new Interval( date.getTime(),nowdate.getTime());


			int diffSeconds=(int)interval.toDurationMillis()/1000;

			if(diffSeconds<second){
				long remainingseconds=second-diffSeconds;
				startTimerAvailable(remainingseconds + "");
				jobstatus(cb_away);
			}else{

				avialbleDilog();
			/*	updateDriverStatus("free", "0");
				sharedPrefs.setJson(Const.AWAYTIME, false);*/
			}

		}
	}

	public void isShifin(){
		if(!sharedPrefs.isJson(Const.SHIFTIN)){
			topPanel.setVisibility(View.GONE);
			tvEmpty.setVisibility(View.VISIBLE);
		}else {
			topPanel.setVisibility(View.VISIBLE);
			tvEmpty.setVisibility(View.GONE);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_current, container,
				false);
		sharedPrefs=new SharedPrefs(getActivity());
		sweetIndicator = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
		sweetIndicator.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
		sweetIndicator.setTitleText("Please wait...");
		sweetIndicator.setCancelable(false);
		ButterKnife.bind(this, rootView);
        btnConfirm.setEnabled(false) ;
		cb_busy.setClickable(false);
		Bundle arg = getArguments();
		boolean flag=arg.getBoolean(Const.TIME);
		boolean flag_currentfragment=arg.getBoolean(Const.CURRENT_FRAGMENT);
		if(flag){
			if(!flag_currentfragment){

				Job job= (Job) arg.getSerializable(Const.JOB_CODE);
				if(job.getWhen().equals("ASAP")){
					assignJob(job);
					sharedPrefs.setJson(Const.ISJOB, true);}

			}else{

				Job job= (Job) arg.getSerializable(Const.JOB_CODE);

				jobDilaog(job);
			}

		}
		if(MyApplication.getInstance().apiManager.mReservedBooking!=null){
          if( MyApplication.getInstance().apiManager.mReservedBooking.status1==4){
			  MyApplication.getInstance().apiManager.mReservedBooking=null;
			  syncJob( MyApplication.getInstance().apiManager.mReservedBooking);
		  }else {
			  if(MyApplication.getInstance().apiManager.mReservedBooking.issync){
				  syncJob( MyApplication.getInstance().apiManager.mReservedBooking);
			  }else {
			  assignJob(MyApplication.getInstance().apiManager.mReservedBooking);
			  }
		  }
		  }
			isShifin();


		cb_away_container.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				driverAvailableDilog();
				//cb_away.setChecked(true);
			}
		});

		return rootView;
	}
@Override
public void onDestroy() {
	super.onDestroy();
	// TODO Auto-generated method stub

	flag=false;
	
	
}



	public void  assignJob(Job job) {
		check = true;
		ll_container_job1.setVisibility(View.VISIBLE);
		bottomContainer.setVisibility(View.VISIBLE);

		/* number of stop -way point */
		way_point_container.removeAllViews();

		int currentWayPoint=sharedPrefs.getJsonInt(Const.CURREMT_WAY_POINT);

		int stopCount=Integer.parseInt(job.getStop_count());
		for(int i=0;i<stopCount;i++){
			createWayPoints(job.getWaypoints().get(i),i);
		}

		if(currentWayPoint>0&&currentWayPoint!=stopCount&&currentWayPoint>0){

				tvPickUp.setTextColor(getResources().getColor(R.color.white));
				tvPickUpTitle.setTextColor(getResources().getColor(R.color.white));
				tvNavPickup.setVisibility(View.INVISIBLE);
				iv_tick.setVisibility(View.VISIBLE);
				selectWayPoint(currentWayPoint,false);
				for(int i=1;i<=currentWayPoint;i++){

                 unSelectWayPoint(i);
				}

				selectWayPoint(currentWayPoint,false);
			    updateStatus(MyApplication.getInstance().apiManager.mReservedBooking, false);

			}else if(currentWayPoint>0&&currentWayPoint==stopCount&&currentWayPoint>0){

				tvPickUp.setTextColor(getResources().getColor(R.color.white));
				tvPickUpTitle.setTextColor(getResources().getColor(R.color.white));
				tvNavPickup.setVisibility(View.INVISIBLE);
				iv_tick.setVisibility(View.VISIBLE);
				//selectWayPoint(currentWayPoint);
				for(int i=1;i<=currentWayPoint;i++){

					unSelectWayPoint(i);
				}
				tvDropOff.setTextColor(getResources().getColor(R.color.colorAccent));
				tvDropOffTitle.setTextColor(getResources().getColor(R.color.colorAccent));
				tvNavDropOff.setVisibility(View.VISIBLE);
			   	iv_tick.setVisibility(View.VISIBLE);

			updateStatus(MyApplication.getInstance().apiManager.mReservedBooking, false);
		}else{
			updateStatus(MyApplication.getInstance().apiManager.mReservedBooking,true);
		}

		if(!job.journey_type.equals("single")){
			tvPickUp1.setText(job.getPickup());
		}

		if(sharedPrefs.isJson(Const.JOURNYTYPE)){
			returnJourneyContainer.setVisibility(View.VISIBLE);
			journeyReturn();
		}

		tvDropOff.setText(job.getDropoff());
		tvPickUp.setText(job.getPickup());
		ratingBarbag.setRating(Float.parseFloat(job.getBags()));
		ratingBarNumberOfPerson.setRating(Float.parseFloat(job.getPassengers()));
		tvName.setText(job.getPaxname());
		tvPhone.setText(job.getPaxtel());
		tvDispatechedid.setText("#"+job.getRefrence() + "  Created by:" + job.getDispatcher());
		tvFair.setText(job.getFare());
		tvTime.setText(job.getWhen() + ":" + job.getDuration());

		status = MyApplication.getInstance().apiManager.mReservedBooking.status1;
		jobflag=true;

		tvPhone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				try {
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:" + ApiManager.getInstance().mReservedBooking.getPaxtel()));
					if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},
								REQUEST_CALL_PHONE);
					} else
						startActivity(callIntent);
				} catch (Exception e) {
				}


			}
		});
		jobstatus(cb_busy);

	}

	private void startTrackingLocation() {
		Context context =getActivity(). getBaseContext();
		alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        intervalInMinutes = 1;
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime(),
				intervalInMinutes * 60000, // 60000 = 1 minute
				pendingIntent);
		sharedPrefs.setJson("jobstarttime", new Date().getTime() + "");


	}

	private void cancelAlarmManager() {
		//Log.d(TAG, "cancelAlarmManager");
		Context context = getActivity().getBaseContext();
		Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}


	/*********************it show the job diologs***************************/
	public void jobDilaog(final Job job){
		/*sharedPrefs.setJson(Const.JOURNYTYPE, false);
		sharedPrefs.setJson(Const.CURREMT_WAY_POINT, 0);*/

		if(!MyApplication.getInstance().isAppOnline){

			//startActivity(intent1);
			moveToFront();
		}
		if (sharedPrefs.isJson(Const.SHIFTIN)){
			if( jobdilog!=null&&jobdilog.isShowing())
				return;



		}

		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.job_dialog, null);
		Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);
		Button btnCancel = (Button) outerView.findViewById(R.id.btnCancel);
		TextView tvFair = (TextView) outerView.findViewById(R.id.tvFair);
		TextView tvTime = (TextView) outerView.findViewById(R.id.tvTime);
		TextView tvName = (TextView) outerView.findViewById(R.id.tvName);
		final TextView tvTimer = (TextView) outerView.findViewById(R.id.tvTimer);
		TextView tvPickUpLocation = (TextView) outerView.findViewById(R.id.tvPickUpLocation);
		TextView tvPickUp = (TextView) outerView.findViewById(R.id.tvPickUp);
		tvPickUp.setText(tvPickUp.getText().toString()+""+job.getWhen());
		tvPickUpLocation.setText(job.getPickup());
		RatingBar ratingBarNumberOfPerson  = (RatingBar) outerView.findViewById(R.id.ratingBarNumberOfPerson);
		RatingBar ratingBarbag = (RatingBar) outerView.findViewById(R.id.ratingBarbag);
		ratingBarNumberOfPerson.setRating(Float.parseFloat(job.getPassengers()));
		ratingBarbag.setRating(Float.parseFloat(job.getBags()));
		tvFair.setText(job.getFare());
		tvName.setText(job.getPaxname());
		tvTime.setText(job.getDuration());

		if(!job.isprice){
			tvFair.setVisibility(View.INVISIBLE);
		}
		mediaPlayer = MediaPlayer.create(getActivity(), R.raw.tick_muscic);
		mediaPlayer.setLooping(true);
		mediaPlayer.start();

		final AlertDialog.Builder alBuilder=new AlertDialog.Builder(getActivity());

		alBuilder.setView(outerView)
		;
		jobdilog = alBuilder.show();
		jobdilog.setCancelable(false);
		jobdilog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		jobdilog. getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		jobdilog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				jobdilog.dismiss();
				mediaPlayer.stop();
				mediaPlayer.release();
				///	jobButton(btnPob);
				if(mTimer!=null)
					mTimer.cancel();
				if (MyApplication.getInstance().apiManager.mReservedBooking != null & jobflag) {
					MyApplication.getInstance().apiManager.mSocketManager.acceptRejectBooking(2, job);

				} else {

					ApiManager.getInstance().mReservedBooking = job;
					MyApplication.getInstance().apiManager.mReservedBooking.setCurrentstatus("callout");
					MyApplication.getInstance().apiManager.mReservedBooking.status1 = 0;

					if(job.getWhen().equals("ASAP")){
					assignJob(job);
					sharedPrefs.setJson(Const.ISJOB, true);
					MyApplication.getInstance().apiManager.mSocketManager.acceptRejectBooking(1, job);

					}else {

						MyApplication.getInstance().apiManager.mReservedBooking=null;
						MyApplication.getInstance().apiManager.mSocketManager.acceptRejectBooking(2, job);
					}
				}

			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mediaPlayer.stop();
				mediaPlayer.release();
				jobdilog.dismiss();
				///	jobButton(btnPob);
				if(mTimer!=null)
					mTimer.cancel();

				MyApplication.getInstance().apiManager.mSocketManager.acceptRejectBooking(0, job);


			}
		});

		mTimer=new CountDownTimer(1000 * Integer.parseInt(job.getTimer()),1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				tvTimer.setText("" + millisUntilFinished/1000);
			}

			@Override
			public void onFinish() {
				ApiManager.getInstance().mBooking = null;
				MyApplication.getInstance().apiManager.mSocketManager.acceptRejectBooking(0,job);
				jobdilog.dismiss();
				mediaPlayer.stop();
				mediaPlayer.release();
			}
		};
		///	jobButton(btnPob);
		if(mTimer!=null)
			mTimer.cancel();

		mTimer.start();






	}


	@TargetApi(11)
	protected void moveToFront() {
		if (Build.VERSION.SDK_INT >= 11) { // honeycomb
			final ActivityManager activityManager = (ActivityManager)getActivity(). getSystemService(Context.ACTIVITY_SERVICE);
			final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

			for (int i = 0; i < recentTasks.size(); i++)
			{
				Log.d("Executed app", "Application executed : "
						+recentTasks.get(i).baseActivity.toShortString()
						+ "\t\t ID: "+recentTasks.get(i).id+"");
				// bring to front
				if (recentTasks.get(i).baseActivity.toShortString().indexOf("mobileapps.technroid.io.cabigate") > -1) {
					activityManager.moveTaskToFront(recentTasks.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
				}
			}
		}
	}

/********************************job fair complete dialog********************************/

	public void jobFairCompleteDilaog(final Job job,String fair){
		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.job_deliver_dialog, null);
		Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);
		Button btnCancel = (Button) outerView.findViewById(R.id.btnCancel);

		final EditText tvFair = (EditText) outerView.findViewById(R.id.tvFair);
		TextView tvTime = (TextView) outerView.findViewById(R.id.tvTime);
		TextView tvName = (TextView) outerView.findViewById(R.id.tvName);
	    final TextView tvDistance = (TextView) outerView.findViewById(R.id.tvDistance);
		Date enddate=new Date();
		Date startdate=new Date(Long.parseLong(sharedPrefs.getJson("jobstarttime")));
		long duration  = enddate.getTime() - startdate.getTime();
		long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
		long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
		tvFair.setText(job.getFare());
		tvName.setText(job.traveltime);
		tvTime.setText(job.getDuration());
		//tvTime.setText(diffInHours + "h-" + diffInMinutes + "m");
		tvDistance.setText(String.format("%.1f", sharedPrefs.getTotalDistance()) + " km");
		final AlertDialog.Builder alBuilder=new AlertDialog.Builder(getActivity());
		if(job.getPayment_type() != null) {
			if(job.getPayment_type().equals("card")){
				btnCancel.setVisibility(View.INVISIBLE);
			}
		}

		alBuilder.setView(outerView);
		///alBuilder.setCancelable(false);
		;
		final AlertDialog ad = alBuilder.show();

		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();

				jobCompleteDilaog(MyApplication.getInstance().apiManager.mReservedBooking);
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//ad.dismiss();
				tvFair.setEnabled(true);
				tvFair.requestFocus();
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(tvFair, InputMethodManager.SHOW_IMPLICIT);
				//	jobCancelDilaog(MyApplication.getInstance().apiManager.mReservedBooking);

			}
		});
	}



	/********************************Start Timer for Available**********************************/


	void startTimerAvailable(String time){
		avilableContainer.setVisibility(View.VISIBLE);
		mTimer=new CountDownTimer(1000 * Integer.parseInt(time),1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				tv_availabel_time.setText(formatElapsedTime(millisUntilFinished));
			}

			@Override
			public void onFinish() {
				avialbleDilog();

			}
		};
		///	jobButton(btnPob);
		if(mTimer!=null)
			mTimer.cancel();

		mTimer.start();

	}

/*******new Timer*************/


public void avialbleDilog(){

	View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.available_time_dialog, null);
	Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);
	Button btnCancel = (Button) outerView.findViewById(R.id.btnCancel);

	final AlertDialog.Builder alBuilder=new AlertDialog.Builder(getActivity());

	alBuilder.setView(outerView)
	;
	final AlertDialog ad = alBuilder.show();

	ad.setCancelable(false);
	ad.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	ad. getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	ad.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	btnConfirm.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ad.dismiss();
			startTimerAvailable("30");
			sharedPrefs.setJson(Const.AWAYSTARTTIME, new Date().getTime() + "");
			sharedPrefs.setJson(Const.AWAYTIME,"30");
			sharedPrefs.setJson(Const.AWAYTIME, true);

		}
	});

	btnCancel.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			sweetIndicator.show();
			status = 6;
			updateDriverStatus("free", "0");
			sharedPrefs.setJson(Const.AWAYTIME, false);
			ad.dismiss();

		}
	});

}



	/********************************Driver available popup**********************************/

	public void driverAvailableDilog(){


		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.timer_dilog, null);
		View ll_ten_minute = (View) outerView.findViewById(R.id.ll_ten_minute);
		View ll_fifteen_min = (View) outerView.findViewById(R.id.ll_fifteen_min);
		View ll_thirty_min = (View) outerView.findViewById(R.id.ll_thirty_min);
		View ll_one_hour = (View) outerView.findViewById(R.id.ll_one_hour);
		final AlertDialog.Builder alBuilder=new AlertDialog.Builder(getActivity());
		alBuilder.setView(outerView);
		final AlertDialog ad = alBuilder.show();
		ll_ten_minute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();
				startTimerAvailable("600");
				status = 5;
				sweetIndicator.show();
				updateDriverStatus("away", "10");

				sharedPrefs.setJson(Const.AWAYSTARTTIME, new Date().getTime() + "");
				sharedPrefs.setJson(Const.AWAYTIME, "600");
				sharedPrefs.setJson(Const.AWAYTIME, true);

			}
		});
		ll_fifteen_min.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();

				status = 5;
				sweetIndicator.show();
				startTimerAvailable("900");
				updateDriverStatus("away", "15");
				sharedPrefs.setJson(Const.AWAYSTARTTIME, new Date().getTime() + "");
				sharedPrefs.setJson(Const.AWAYTIME, "900");
				sharedPrefs.setJson(Const.AWAYTIME, true);
			}
		});
		ll_thirty_min.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();
				status=5;
				startTimerAvailable("1800");
				updateDriverStatus("away", "30");
				sharedPrefs.setJson(Const.AWAYSTARTTIME, new Date().getTime() + "");
				sharedPrefs.setJson(Const.AWAYTIME, "1800");
				sharedPrefs.setJson(Const.AWAYTIME, true);

			}
		});
	    ll_one_hour.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();
				status=5;
				updateDriverStatus("away", "60");
				startTimerAvailable("3600");
				sharedPrefs.setJson(Const.AWAYSTARTTIME, new Date().getTime() + "");
				sharedPrefs.setJson(Const.AWAYTIME, "3600");
				sharedPrefs.setJson(Const.AWAYTIME, true);
			}
		});


	}


/*******************************JOb Complete daiog*************************************/

	public void jobCompleteDilaog(final Job job){


		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.job_deliver_payment_dialog, null);
		Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);
		final Button btnCash = (Button) outerView.findViewById(R.id.btnCash);
		final Button btnCard = (Button) outerView.findViewById(R.id.btnCard);
		Button btnCancel = (Button) outerView.findViewById(R.id.btnCancel);

		final  EditText edtFeadBack = (EditText) outerView.findViewById(R.id.edtFeadBack);
	;
		final TextView tvPickUpLocation = (TextView) outerView.findViewById(R.id.tvPickUpLocation);

		final AlertDialog.Builder alBuilder=new AlertDialog.Builder(getActivity());

		alBuilder.setView(outerView)
		;
		final AlertDialog ad = alBuilder.show();

	/*	btnCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {*/
		if(job.getPayment_type()!=null){
            if(job.getPayment_type().equals("account")||job.getPayment_type().equals("prepaid")){
				btnCash.setBackgroundColor(Color.TRANSPARENT);
				btnCard.setBackgroundColor(getResources().getColor(R.color.gray_btn_bg_color));
				btnCard.setEnabled(false);
				btnCash.setEnabled(true);
				payvia="card";
			}else if(job.getPayment_type().equals("cash")){
				btnCard.setBackgroundColor(Color.TRANSPARENT);
				btnCash.setBackgroundColor(getResources().getColor(R.color.gray_btn_bg_color));
				btnCard.setEnabled(true);
				btnCash.setEnabled(false);
				payvia = "cash";

			}
		}


		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();

                 jobDeliver(edtFeadBack.getText().toString(), job);



			}
		});btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();


			}
		});
	}

	/*************************************job cancel dilaog******************************************/
	public void jobCancelDilaog(final Job job){


		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.job_canncel_dialog, null);
		Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);
		final CheckBox cb_noshown = (CheckBox) outerView.findViewById(R.id.cb_noshown);
		final CheckBox cb_other = (CheckBox) outerView.findViewById(R.id.cb_other);
		Button btnCancel = (Button) outerView.findViewById(R.id.btnCancel);

		cb_noshown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(cb_noshown.isChecked()){
					cb_other.setChecked(false);

				}
			}
		});
		cb_other.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(cb_other.isChecked()){
					cb_noshown.setChecked(false);
				}
			}
		});

		final  EditText edtFeadBack = (EditText) outerView.findViewById(R.id.edtFeadBack);
	;
		//final TextView tvPickUpLocation = (TextView) outerView.findViewById(R.id.tvPickUpLocation);

		final AlertDialog.Builder alBuilder=new AlertDialog.Builder(getActivity());

		alBuilder.setView(outerView)
		;
		final AlertDialog ad = alBuilder.show();

		ad.setCancelable(false);

		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();

				jobCancel(edtFeadBack.getText().toString(), job);

			}
		});btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();

			}
		});

	}

/********************************************Job Queues******************************************/
	public void jobQueue(final Job job,final Job newJob){


		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.job_queue_dialog, null);
		Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);

		final AlertDialog.Builder alBuilder=new AlertDialog.Builder(getActivity());

		alBuilder.setView(outerView)
		;
		final AlertDialog ad = alBuilder.show();

        ad.setCancelable(false);

		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();

				MyApplication.getInstance().apiManager.mReservedBooking = null;
				if (newJob != null) {

					MyApplication.getInstance().apiManager.mReservedBooking = newJob;
					MyApplication.getInstance().apiManager.mReservedBooking.setCurrentstatus("callout");
					MyApplication.getInstance().apiManager.mReservedBooking.status1=0;
					assignJob(MyApplication.getInstance().apiManager.mReservedBooking);
				}


			}
		});
		final Handler handler  = new Handler();
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (ad.isShowing()) {
					ad.dismiss();
					MyApplication.getInstance().apiManager.mReservedBooking = null;
					if (newJob != null) {

						MyApplication.getInstance().apiManager.mReservedBooking = newJob;
						MyApplication.getInstance().apiManager.mReservedBooking.setCurrentstatus("callout");
						MyApplication.getInstance().apiManager.mReservedBooking.status1=0;
						assignJob(MyApplication.getInstance().apiManager.mReservedBooking);
					}
				}
			}
		};

	}

	/****************************Job button status change *************************************/
	void jobButton(Button btn){

		btnCallout.setTextColor(getResources().getColor(R.color.white));
		btnWait.setTextColor(getResources().getColor(R.color.white));
		btnPob.setTextColor(getResources().getColor(R.color.white));
		btnCallout.setEnabled(true);
		btnWait.setEnabled(true);
		btnPob.setEnabled(true);
		btn.setTextColor(getResources().getColor(R.color.colorAccent));
		btn.setEnabled(false);

	}



	private String formatElapsedTime(long now) {
		long hours=0, minutes=0, seconds=0, tenths=0;
		StringBuilder sb = new StringBuilder();

		if (now < 1000) {
			tenths = now / 100;
		} else if (now < 60000) {
			seconds = now / 1000;
			now -= seconds * 1000;
			tenths = (now / 100);
		} else if (now < 3600000) {
			hours = now / 3600000;
			now -= hours * 3600000;
			minutes = now / 60000;
			now -= minutes * 60000;
			seconds = now / 1000;
			now -= seconds * 1000;
			tenths = (now / 100);
		}

		if (hours > 0) {
			sb.append(hours).append(":")
					.append(formatDigits(minutes)).append(":")
					.append(formatDigits(seconds)).append(".");
			//.append(tenths);
		} else {
			sb.append(formatDigits(minutes)).append(":")
					.append(formatDigits(seconds));//.append(".");
					//.append(tenths);
		}

		return sb.toString();
	}

	private String formatDigits(long num) {
		return (num < 10) ? "0" + num : new Long(num).toString();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if(mTimer!=null)
		mTimer.cancel();
	}


	/************************************Response of all apis******************************************/
	Observer<Response> observerStatus=new Observer<Response>() {
		@Override
		public void onCompleted() {

			sweetIndicator.dismiss();
		}

		@Override
		public void onError(Throwable e) {
			sweetIndicator.dismiss();
			((JobViewActivity)getActivity()).displayError(e);
		}

			private String formatDigits(long num) {
			return (num < 10) ? "0" + num : new Long(num).toString();
		}

		public void onNext(Response response) {
			sweetIndicator.dismiss();
			String jsonStr = Utilities.convertTypedInputToString(response.getBody());
			try {

				Log.e("error", jsonStr);
				JSONObject obj = new JSONObject(jsonStr);
				JSONObject response1 =obj.getJSONObject("response");
				if(obj.getInt(Const.STATUS)==1){
					switch (status){
						case -1:
							jobstatus(cb_busy);
							cb_avilable.setChecked(false);
							cb_avilable.setClickable(false);
							if(mTimer!=null){
								mTimer.cancel();
							avilableContainer.setVisibility(View.GONE);
							}
							break;
						case 0:
							btnConfirm.setEnabled(false);
							jobButton(btnCallout);
							displayLocationColor(true);
							status=-1;
							sweetIndicator.show();
							updateDriverStatus("busy","0");
							break;
						case 1:
							btnConfirm.setEnabled(false);
							jobButton(btnWait);
							if(isNewRequest)
							displayLocationColor(false);
							break;
						case 2:

							btnConfirm.setEnabled(true);
							jobButton(btnPob);

							//displayLocationColor(false);
							break;
						case 3:

							jobflag=false;
							cancelAlarmManager();
							sharedPrefs.setJson(Const.JOURNYTYPE, false);
							sharedPrefs.setJson(Const.CURREMT_WAY_POINT, 0);
							jobstatus(cb_avilable);
							sharedPrefs.intatlizeGpsTracker();
							ll_container_job1.setVisibility(View.INVISIBLE);
							bottomContainer.setVisibility(View.INVISIBLE);
							if(response1.getString("next_active_job").equals("yes")){
								String newjob1=response1.getJSONObject("jobdetails").toString();
								Job newJob = (Job)new Gson().fromJson(newjob1, Job.class);
								jobQueue(MyApplication.getInstance().apiManager.mReservedBooking,newJob);
							}else {
								status=6;
								updateDriverStatus("free", "0");
								sharedPrefs.setJson(Const.ISJOB,false);
								jobQueue(MyApplication.getInstance().apiManager.mReservedBooking, null);
							}


							break;
						case 4:
							jobflag=false;
							sharedPrefs.intatlizeGpsTracker();
							sharedPrefs.setJson(Const.JOURNYTYPE, false);
							sharedPrefs.setJson(Const.CURREMT_WAY_POINT, 0);
							cancelAlarmManager();
							jobstatus(cb_avilable);

							ll_container_job1.setVisibility(View.INVISIBLE);
							bottomContainer.setVisibility(View.INVISIBLE);
							if(response1.getString("next_active_job").equals("yes")){
								String newjob1=response1.getJSONObject("jobdetails").toString();
								Job newJob = (Job)new Gson().fromJson(newjob1, Job.class);
								jobQueue(MyApplication.getInstance().apiManager.mReservedBooking,newJob);
							}else {
								status=6;
								updateDriverStatus("free", "0");
								sharedPrefs.setJson(Const.ISJOB,false);
								jobQueue(MyApplication.getInstance().apiManager.mReservedBooking, null);

							}
							break;
						case 5:
							///	jobButton(btnPob);
							jobstatus(cb_away);
							break;
						case 6:
							///	jobButton(btnPob);
							if(mTimer!=null)
							mTimer.cancel();
							avilableContainer.setVisibility(View.GONE);
							jobstatus(cb_avilable);
							break;
						case 7:
							///	jobButton(btnPob);
							double actualFare=response1.getDouble("actual_fate");
							String traveltime=response1.getString("traveltime");
							double estfare=Double.parseDouble(MyApplication.getInstance().apiManager.mReservedBooking.getFare());
                            if(actualFare>estfare)
								MyApplication.getInstance().apiManager.mReservedBooking.setFare(actualFare+"");
							MyApplication.getInstance().apiManager.mReservedBooking.traveltime=traveltime;
							jobFairCompleteDilaog(MyApplication.getInstance().apiManager.mReservedBooking, "fair");
							break;

					}
				}else{
					SnackBarDisplay.ShowSnackBar(getActivity(), obj.getString(Const.ERROR_MESSAGE));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};


	@Override
	public void onPause() {
		super.onPause();

	}

	public  void awayStatussync(int time){
		startTimerAvailable((60*time)+"");
		status = 5;
		sweetIndicator.show();
		updateDriverStatus("away", ""+time);

		sharedPrefs.setJson(Const.AWAYSTARTTIME, new Date().getTime() + "");
		sharedPrefs.setJson(Const.AWAYTIME, (60*time)+"");
		sharedPrefs.setJson(Const.AWAYTIME, true);

	}




}
