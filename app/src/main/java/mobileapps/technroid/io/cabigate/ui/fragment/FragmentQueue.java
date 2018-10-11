package mobileapps.technroid.io.cabigate.ui.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.api.ApiManager;
import mobileapps.technroid.io.cabigate.app.MyApplication;
import mobileapps.technroid.io.cabigate.helper.Const;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;
import mobileapps.technroid.io.cabigate.helper.SnackBarDisplay;
import mobileapps.technroid.io.cabigate.models.Job;
import mobileapps.technroid.io.cabigate.models.MyZoneQueue;
import mobileapps.technroid.io.cabigate.models.MyZones;
import mobileapps.technroid.io.cabigate.models.Zones;
import mobileapps.technroid.io.cabigate.ui.activity.JobViewActivity;
import mobileapps.technroid.io.cabigate.ui.adapter.CurrentJobAdapter;
import mobileapps.technroid.io.cabigate.ui.adapter.MyZoneAdapter;
import mobileapps.technroid.io.cabigate.ui.adapter.MyZoneQueueAdapter;
import mobileapps.technroid.io.cabigate.ui.adapter.ZonesAdapter;
import mobileapps.technroid.io.cabigate.ui.widgets.alertbox.SweetAlertDialog;
import mobileapps.technroid.io.cabigate.utilities.Utilities;
import retrofit.client.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public final class FragmentQueue extends Fragment implements ZonesAdapter.OnItemClickListener, CurrentJobAdapter.OnItemClickListener {


	private SweetAlertDialog sweetIndicator;
	private SharedPrefs sharedPrefs;
	@Bind(R.id.btnZoneCheckin)
	Button btnZoneCheckin;
	@Bind(R.id.btnOpenJob)
	Button btnOpenJob;
	private int status;
	private ArrayList<Zones> zoneslist;
	private ArrayList<Job> openJobList;
	private ArrayList<MyZoneQueue> myzonequeueList;
	private ArrayList<MyZones> myzoneList;
	private CurrentJobAdapter currentJobAdapter;
	private MyZoneQueueAdapter myZoneQueueAdapter;
	private MyZoneAdapter myZoneAdapter;
	private MyApplication app;
	private RecyclerView rv;
	private View perviousview;
	private int perviospstion;
	private String zoneId="";


	/***********************On Click******************************/

	@OnClick(R.id.btnOpenJob)
	void openDilaog(){

		//openJobDilog();
		if (sharedPrefs.isJson("myqueue")) {
			myzonequeueList.clear();
			status = 2;
			Log.e("msg", sharedPrefs.getJson("myqueue"));
			ApiManager.getInstance().mUrlManager.myZonequeue(app.myDriver.companyID, sharedPrefs.getJson("myqueue"), app.myDriver.token).cache().subscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(observerStaus);
			sweetIndicator.show();
			myZoneQueueAdapter = new MyZoneQueueAdapter(getActivity(), myzonequeueList);
			rv.setAdapter(myZoneQueueAdapter);
		} else {
			SnackBarDisplay.ShowSnackBar(getActivity(), "Please select zone");
		}


	}
	@OnClick(R.id.btnZoneCheckin)
	void zoneCheckIn(){
		if(sharedPrefs.isJson("myqueue")) {
			status=5;
			JSONObject jsonObject=new JSONObject();
			sweetIndicator.show();
			try {

				jsonObject.put(Const.USERID, MyApplication.getInstance().myDriver.userid);
				jsonObject.put(Const.COMPANYID,MyApplication.getInstance().myDriver.companyID);
				jsonObject.put(Const.TOKEN,MyApplication.getInstance().myDriver.token);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			ApiManager.getInstance().mUrlManager.zoneCheckout(jsonObject.toString()).cache().subscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(observerStaus);
		}else{
		showZones();
		}
	}

	/*********************************Zone Queue List*************************************/

	public void showZones(){



		status=0;
		JSONObject jsonObject=new JSONObject();
		sweetIndicator.show();
		try {

			jsonObject.put(Const.USERID, MyApplication.getInstance().myDriver.userid);
			jsonObject.put(Const.COMPANYID,MyApplication.getInstance().myDriver.companyID);
			jsonObject.put(Const.TOKEN,MyApplication.getInstance().myDriver.token);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		ApiManager.getInstance().mUrlManager.getZoneList(jsonObject.toString()).cache().subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observerStaus);




	}


	/********************************Select Job Queue option popup**********************************/

	public void openJobDilog(){


		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.queue_open_job_dilog, null);
		View ll_ten_minute = (View) outerView.findViewById(R.id.ll_ten_minute);
		View ll_fifteen_min = (View) outerView.findViewById(R.id.ll_fifteen_min);
		View ll_thirty_min = (View) outerView.findViewById(R.id.ll_thirty_min);
		View ll_one_hour = (View) outerView.findViewById(R.id.ll_one_hour);
		final AlertDialog.Builder alBuilder=new AlertDialog.Builder(getActivity());
		alBuilder.setView(outerView);
		final AlertDialog ad = alBuilder.show();
		ad.setCancelable(false);
		ll_ten_minute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();

				status = 1;
				openJobList.clear();
				ApiManager.getInstance().mUrlManager.openJobsque(app.myDriver.companyID, app.myDriver.userid, app.myDriver.token).cache().subscribeOn(Schedulers.newThread())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(observerStaus);
				sweetIndicator.show();
				currentJobAdapter = new CurrentJobAdapter(getActivity(), openJobList);
				rv.setAdapter(currentJobAdapter);
				currentJobAdapter.setOnItemClickListener(FragmentQueue.this);
				status = 1;
				//sweetIndicator.show();

			}
		});
		ll_fifteen_min.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();
				if (sharedPrefs.isJson("myqueue")) {
					myzonequeueList.clear();
					status = 2;
					Log.e("msg", sharedPrefs.getJson("myqueue"));
					ApiManager.getInstance().mUrlManager.myZonequeue(app.myDriver.companyID, sharedPrefs.getJson("myqueue"), app.myDriver.token).cache().subscribeOn(Schedulers.newThread())
							.observeOn(AndroidSchedulers.mainThread())
							.subscribe(observerStaus);
					sweetIndicator.show();
					myZoneQueueAdapter = new MyZoneQueueAdapter(getActivity(), myzonequeueList);
					rv.setAdapter(myZoneQueueAdapter);
				} else {
					SnackBarDisplay.ShowSnackBar(getActivity(), "Please select zone");
				}
			}
		});
		ll_thirty_min.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();
				myzoneList.clear();
				status = 3;
				ApiManager.getInstance().mUrlManager.zoneQueue(app.myDriver.companyID, app.myDriver.userid, app.myDriver.token).cache().subscribeOn(Schedulers.newThread())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(observerStaus);
				sweetIndicator.show();
				myZoneAdapter = new MyZoneAdapter(getActivity(), myzoneList);
				rv.setAdapter(myZoneAdapter);

			}
		});


	}


	/********************************Zone Check In popup**********************************/

	public void openZonesCheckInDilog(ArrayList<Zones> listzone){


		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_queue_zone_list, null);
		RecyclerView rv = (RecyclerView) outerView.findViewById(R.id.rv);
		View selctedContainer = (View) outerView.findViewById(R.id.tvSelectedVeihlhle);
		View tvCancel = (View) outerView.findViewById(R.id.tvCancel);


		rv.setHasFixedSize(true);
		rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


		zoneslist=  new ArrayList<Zones>();
		zoneslist.addAll(listzone);
		ZonesAdapter zonesAdapter = new ZonesAdapter(getActivity(),zoneslist );
		rv.setAdapter(zonesAdapter);
		zonesAdapter.setOnItemClickListener(this);


		/*final AlertDialog.Builder alBuilder=new AlertDialog.Builder(getActivity());
		alBuilder.setView(outerView);
		final AlertDialog ad = alBuilder.show();*/
		final Dialog ad = new Dialog(getActivity(),R.style.MyDialog);
		ad.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ad.setContentView(outerView);
		ad.show();

		ad.setCancelable(false);
		tvCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ad.dismiss();
			}
		});
		selctedContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {



				if(!zoneId.equals("")){
					ad.dismiss();
					status = 4;

					JSONObject jsonObject=new JSONObject();
					sweetIndicator.show();
					try {

						jsonObject.put(Const.USERID, MyApplication.getInstance().myDriver.userid);
						jsonObject.put(Const.COMPANYID,MyApplication.getInstance().myDriver.companyID);
						jsonObject.put(Const.TOKEN,MyApplication.getInstance().myDriver.token);
						jsonObject.put("zoneid",zoneId);

					} catch (JSONException e) {
						e.printStackTrace();
					}

					ApiManager.getInstance().mUrlManager.zoneCheckin(jsonObject.toString()).cache().subscribeOn(Schedulers.newThread())
							.observeOn(AndroidSchedulers.mainThread())
							.subscribe(observerStaus);




				}else{

					SnackBarDisplay.ShowSnackBar(getActivity(),"Please choose the zone");
				}
				//sweetIndicator.show();

			}
		});


	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_qeue, container,
				false);
		sharedPrefs=new SharedPrefs(getActivity());
		sweetIndicator = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
		sweetIndicator.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
		sweetIndicator.setTitleText("Please wait...");
		sweetIndicator.setCancelable(false);
		ButterKnife.bind(this, rootView);

		 app=MyApplication.getInstance();

		rv = (RecyclerView) rootView.findViewById(R.id.rv);
		rv.setHasFixedSize(true);
		rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
		openJobList=  new ArrayList<Job>();
		myzonequeueList=  new ArrayList<MyZoneQueue>();
		myzoneList=  new ArrayList<MyZones>();
		currentJobAdapter = new CurrentJobAdapter(getActivity(),openJobList );
		currentJobAdapter.setOnItemClickListener(this);
		rv.setAdapter(currentJobAdapter);

        status=1;

		ApiManager.getInstance().mUrlManager.openJobsque(app.myDriver.companyID, app.myDriver.userid,  app.myDriver.token).cache().subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observerStaus);

		sweetIndicator.show();

		if(sharedPrefs.isJson("myqueue")) {
			btnZoneCheckin.setText("Zone checkout");
		}

		return rootView;
	}


	public void zoneCheckOut(){

		if(!sharedPrefs.isJson("myqueue")) {
			btnZoneCheckin.setText("Zone CheckIn");
		}

	}


@Override
public void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();


}

	@Override
	public void onItemClick(View view, int position) {

		Zones zone=zoneslist.get(position);
		 zoneId=zone.getZoneid();
		if(perviousview!=null&&perviospstion!=-1){

			perviousview.setBackgroundColor(Color.TRANSPARENT);
			view.setBackgroundColor(getResources().getColor(R.color.colorAccent));


		}else{
			view.setBackgroundColor(getResources().getColor(R.color.colorAccent));

		}

		perviousview=view;
		perviospstion=position;




	}



	/************************************Response of all apis******************************************/
	Observer<Response> observerStaus=new Observer<Response>() {
		@Override
		public void onCompleted() {

			sweetIndicator.dismiss();
		}

		@Override
		public void onError(Throwable e) {
			sweetIndicator.dismiss();
			((JobViewActivity)getActivity()).displayError(e);
		}

		@Override
		public void onNext(Response response) {
			sweetIndicator.dismiss();
			String jsonStr = Utilities.convertTypedInputToString(response.getBody());
			try {
				Log.e("error", jsonStr);
				JSONObject obj = new JSONObject(jsonStr);

				if(obj.getInt(Const.STATUS)==1){
					JSONObject response1 =obj.getJSONObject("response");
					Gson gson=new Gson();
					switch (status){
						case 0:
							JSONArray jsonArray=response1.getJSONArray("list");
							Type listType = new TypeToken<List<Zones>>(){}.getType();
							ArrayList<Zones> zones = (ArrayList<Zones>) gson.fromJson(jsonArray.toString(), listType);
							openZonesCheckInDilog(zones);

							break;
						case 1:
							JSONArray openjobs=response1.getJSONArray("list");
							openJobList.addAll((ArrayList<Job>) gson.fromJson(openjobs.toString(), new TypeToken<List<Job>>() {
							}.getType()));
							currentJobAdapter.notifyDataSetChanged();
							btnOpenJob.setText("Open Job");
							break;
						case 2:
							JSONArray myzonequeue=response1.getJSONArray("list");
							myzonequeueList.addAll((ArrayList<MyZoneQueue>) gson.fromJson(myzonequeue.toString(), new TypeToken<List<MyZoneQueue>>() {
							}.getType()));
							myZoneQueueAdapter.notifyDataSetChanged();
							btnOpenJob.setText("My Zone Queue");

							break;
						case 3:

							JSONArray zonesArray=response1.getJSONArray("list");
							myzoneList.addAll((ArrayList<MyZones>) gson.fromJson(zonesArray.toString(), new TypeToken<List<MyZones>>() {
							}.getType()));
							myZoneAdapter.notifyDataSetChanged();
							btnOpenJob.setText("Zones");
							break;
						case 4:
							sharedPrefs.setJson("myqueue",true);
                            sharedPrefs.setJson("myqueue", zoneId);
							btnZoneCheckin.setText("Zone CheckOut");
							zoneId="";
							break;
						case 5:
                            sharedPrefs.setJson("myqueue",false);
							btnZoneCheckin.setText("Zone CheckIn");
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
	public void onQueueItemClick(View view, int position) {

    jobOfferDilaog(openJobList.get(position));
	}


	/*********************it show the job diologs***************************/
	public void jobOfferDilaog(final Job job){
		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.job_offer_dialog, null);
		Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);
		Button btnCancel = (Button) outerView.findViewById(R.id.btnCancel);
		View queue_container = (View) outerView.findViewById(R.id.queue_container);
		;
		btnConfirm.setVisibility(View.GONE);
		TextView tvFair = (TextView) outerView.findViewById(R.id.tvFair);
		TextView tvTime = (TextView) outerView.findViewById(R.id.tvTime);
		TextView tvName = (TextView) outerView.findViewById(R.id.tvName);
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
		final AlertDialog.Builder alBuilder=new AlertDialog.Builder(getActivity());
		alBuilder.setView(outerView)
		;
		final AlertDialog ad = alBuilder.show();
		//ad.setCancelable(false);
		ad.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		ad. getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		ad.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

				sweetIndicator.show();
				status=1;
				JSONObject jsonObject=new JSONObject();
				sweetIndicator.show();
				try {

					jsonObject.put(Const.USERID, MyApplication.getInstance().myDriver.userid);
					jsonObject.put(Const.COMPANYID,MyApplication.getInstance().myDriver.companyID);
					jsonObject.put(Const.TOKEN,MyApplication.getInstance().myDriver.token);
					jsonObject.put(Const.JOBID,job.getJobid());

				} catch (JSONException e) {
					e.printStackTrace();
				}

				ApiManager.getInstance().mUrlManager.acceptOffer(jsonObject.toString()).cache().subscribeOn(Schedulers.newThread())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(observerStaus);

			}
		});



	}


}
