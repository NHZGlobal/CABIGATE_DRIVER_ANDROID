package mobileapps.technroid.io.cabigate.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imanoweb.calendarview.CalendarListener;
import com.imanoweb.calendarview.CustomCalendarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.api.ApiManager;
import mobileapps.technroid.io.cabigate.app.MyApplication;
import mobileapps.technroid.io.cabigate.helper.CommonMethods;
import mobileapps.technroid.io.cabigate.helper.Const;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;
import mobileapps.technroid.io.cabigate.helper.SnackBarDisplay;
import mobileapps.technroid.io.cabigate.models.Driver;
import mobileapps.technroid.io.cabigate.models.Job;
import mobileapps.technroid.io.cabigate.models.Passenger;
import mobileapps.technroid.io.cabigate.models.Vehicle;
import mobileapps.technroid.io.cabigate.ui.activity.JobViewActivity;
import mobileapps.technroid.io.cabigate.ui.adapter.DriverAdapter;
import mobileapps.technroid.io.cabigate.ui.adapter.PassengerAdapter;
import mobileapps.technroid.io.cabigate.ui.adapter.UnassignedJobAdapter;
import mobileapps.technroid.io.cabigate.ui.widgets.alertbox.SweetAlertDialog;
import mobileapps.technroid.io.cabigate.utilities.Utilities;
import retrofit.client.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public final class FragmentDispatch extends Fragment implements UnassignedJobAdapter.OnItemClickListener, DriverAdapter.OnItemClickListener, PassengerAdapter.OnItemClickListener {


	private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE_DROPOFF =1 ;
	private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKER =11 ;
	private SweetAlertDialog sweetIndicator;
	private SharedPrefs sharedPrefs;
	@Bind(R.id.btnShowUnassigned)
	Button btnShowUnassigned;
	@Bind(R.id.btnCreateJob)
	Button btnCreateJob;
	@Bind(R.id.ll_activate_dispatch)
	View ll_activate_dispatch;
	private int status;

	private ArrayList<Job> openJobList;


	private UnassignedJobAdapter unassignedJobAdapter;


	private MyApplication app;
	private RecyclerView rv;
	private View perviousview;
	private int perviospstion;
	private ArrayList<Driver> driverlist;
	 private android.app.AlertDialog driverdialog;
	 private android.app.AlertDialog passengerdialog;
	private Job currentJob;
	private ArrayList<Passenger> passengerlist;
	private PassengerAdapter passengerAdapter;
	private TextView tvName;
	private TextView tvPhone;
	private TextView tvDate;
	private Place placepickup=null;
	private Place placedropoff=null;
	private TextView tvPickUpLocation;
	private TextView tvNavDropOff;
	private String time="00:00";
	private String selecteddate;


	/***********************On Click******************************/

	@OnClick(R.id.btnCreateJob)
	void openDilaog(){

		requestAvailableVehicle();



	}
	@OnClick(R.id.btnShowUnassigned)
	void showUnassignedJOb(){
		status = 1;
		openJobList.clear();

		sweetIndicator.show();

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

			ApiManager.getInstance().mUrlManager.unAssignedJobs(jsonObject.toString()).cache().subscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(observerStaus);

	}




	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKER) {
			if (resultCode == getActivity().RESULT_OK) {
				 placepickup = PlaceAutocomplete.getPlace(getActivity(), data);
				 tvPickUpLocation.setText(placepickup.getName());
				Log.i(getClass().getName(), "Place: " + placepickup.getName());
			} else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
				Status status = PlaceAutocomplete.getStatus(getActivity(), data);
				// TODO: Handle the error.
				Log.i(getClass().getName(), status.getStatusMessage());

			} else if (resultCode == getActivity().RESULT_CANCELED) {
				// The user canceled the operation.
			}
		}if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DROPOFF) {
			if (resultCode == getActivity().RESULT_OK) {
				 placedropoff = PlaceAutocomplete.getPlace(getActivity(), data);
				tvNavDropOff.setText(placedropoff.getName());
				Log.i(getClass().getName(), "Place: " + placedropoff.getName());
			} else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
				Status status = PlaceAutocomplete.getStatus(getActivity(), data);
				// TODO: Handle the error.
				Log.i(getClass().getName(), status.getStatusMessage());

			} else if (resultCode == getActivity().RESULT_CANCELED) {
				// The user canceled the operation.
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dispatch, container,
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

		unassignedJobAdapter = new UnassignedJobAdapter(getActivity(),openJobList );
		rv.setAdapter(unassignedJobAdapter);
		unassignedJobAdapter.setOnItemClickListener(this);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		selecteddate=df.format(new Date());


		return rootView;
	}
@Override
public void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();


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
				JSONObject response1 =obj.getJSONObject("response");
				if(obj.getInt(Const.STATUS)==1){
					Gson gson=new Gson();
					switch (status){
						case 0:
                            rv.setVisibility(View.VISIBLE);
							ll_activate_dispatch.setVisibility(View.INVISIBLE);
							JSONArray openjobs=response1.getJSONArray("list");
							openJobList.addAll((ArrayList<Job>) gson.fromJson(openjobs.toString(), new TypeToken<List<Job>>() {
							}.getType()));
							unassignedJobAdapter.notifyDataSetChanged();
						;
							break;
						case 1:
							JSONArray drivers=response1.getJSONArray("list");
							ArrayList<Driver> driverlist1=new ArrayList<Driver>();
							driverlist1.addAll((ArrayList<Driver>) gson.fromJson(drivers.toString(), new TypeToken<List<Driver>>() {
							}.getType()));
							openAvailableDriverDialog(driverlist1);

							break;
						case 2:
							driverdialog.dismiss();
							SnackBarDisplay.ShowSnackBarTime(getActivity(), response1.getString("msg"));


							break;
						case 3:

							JSONArray veichles=response1.getJSONArray("list");
							ArrayList<Vehicle> veichleslist=new ArrayList<Vehicle>();
							veichleslist.addAll((ArrayList<Vehicle>) gson.fromJson(veichles.toString(), new TypeToken<List<Vehicle>>() {
							}.getType()));

                              jobCreateDialaog(veichleslist);
							break;
						case 4:

							JSONArray passJsonArray=response1.getJSONArray("list");
							//veichleslist=new ArrayList<Vehicle>();
							passengerlist.clear();
							passengerlist.addAll((ArrayList<Passenger>) gson.fromJson(passJsonArray.toString(), new TypeToken<List<Passenger>>() {
							}.getType()));

                            passengerAdapter.notifyDataSetChanged();

							break;
						case 5:
							SnackBarDisplay.ShowSnackBarTime(getActivity(), response1.getString("msg"));
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
	public void onItemClick(View view, int position) {
		jobOfferDilaog(openJobList.get(position));
	}



	/*********************it is user to create the new job ***************************/
	public void jobCreateDialaog(final ArrayList<Vehicle> vehicleArrayList){
		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.job_create_new_dialog, null);
		Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);
		Button btnCancel = (Button) outerView.findViewById(R.id.btnCancel);
		RadioButton rb_asp = (RadioButton) outerView.findViewById(R.id.rb_asp);
		final RadioButton rb_later = (RadioButton) outerView.findViewById(R.id.rb_later);
		final RadioButton rb_self = (RadioButton) outerView.findViewById(R.id.rb_self);
		final RadioButton rb_other = (RadioButton) outerView.findViewById(R.id.rb_other);
		tvPickUpLocation = (TextView) outerView.findViewById(R.id.tvPickUpLocation);
		tvNavDropOff = (TextView) outerView.findViewById(R.id.tvNavDropOff);
		tvName = (TextView) outerView.findViewById(R.id.tvName);
		tvPhone = (TextView) outerView.findViewById(R.id.tvPhone);
		tvDate = (TextView) outerView.findViewById(R.id.tvDate);
		final Spinner spVehicle = (Spinner) outerView.findViewById(R.id.sp_vehicle);
		final Spinner spinner_no_bag = (Spinner) outerView.findViewById(R.id.spinner_no_bag);
		final Spinner spinner_no_passenger = (Spinner) outerView.findViewById(R.id.spinner_no_passenger);

		ArrayList<String> noOfBags=new ArrayList<String>();
		ArrayList<String> noOfPassenger=new ArrayList<String>();
		ArrayList<String> vehicles=new ArrayList<String>();

		for (int i=0;i<vehicleArrayList.size();i++){
			vehicles.add(vehicleArrayList.get(i).getName());
		}

		String[] bagsArray = {"1", "2", "3", "4"};
		String[] passengerArray = {"1", "2", "3", "4","5","6","7","8","9"};
		noOfBags.addAll(Arrays.asList(bagsArray));
		noOfPassenger.addAll(Arrays.asList(passengerArray));


		// Creating adapter for spinner
		ArrayAdapter<String> noPessengerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, noOfPassenger);
		ArrayAdapter<String> noBagAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, noOfBags);
		ArrayAdapter<String> veichleAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, vehicles);

		// Drop down layout style - list view with radio button
		noPessengerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		noBagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		veichleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinner_no_passenger.setAdapter(noPessengerAdapter);
		spinner_no_bag.setAdapter(noBagAdapter);
		spVehicle.setAdapter(veichleAdapter);

		tvDate.setText(selecteddate + " "+time);



		spinner_no_bag.getSelectedItemPosition();



		tvNavDropOff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					Intent intent =
							new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
									.build(getActivity());
					startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DROPOFF);
				} catch (GooglePlayServicesRepairableException e) {
					// TODO: Handle the error.
				} catch (GooglePlayServicesNotAvailableException e) {
					// TODO: Handle the error.
				}

			}
		});
		tvPickUpLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				try {
					Intent intent =
							new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
									.build(getActivity());
					startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKER);
				} catch (GooglePlayServicesRepairableException e) {
					// TODO: Handle the error.
				} catch (GooglePlayServicesNotAvailableException e) {
					// TODO: Handle the error.
				}

			}
		});
		tvName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

              openAvailablePassnegerDialog();


			}
		});
		tvPhone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openAvailablePassnegerDialog();
			}
		});
	    tvDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(rb_later.isChecked())
					calenderDialaog();
			}
		});





		final AlertDialog.Builder alBuilder=new AlertDialog.Builder(getActivity());
		alBuilder.setView(outerView);
		final AlertDialog ad = alBuilder.show();
		ad.setCancelable(false);
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

				if(placepickup==null){
					SnackBarDisplay.ShowSnackBarTime(getActivity(),"Please enter pick up location");
				return;
				} if(placedropoff==null){
					SnackBarDisplay.ShowSnackBarTime(getActivity(), "Please enter drop off location");
				return;
				}
				if(!CommonMethods.isSet(tvName.getText().toString())){
					SnackBarDisplay.ShowSnackBarTime(getActivity(), "Please enter name or phone");
				return;
				}

				ad.dismiss();
				JSONObject jsonObject=new JSONObject();
				Vehicle vehicle=vehicleArrayList.get(spVehicle.getSelectedItemPosition());

				try {

					jsonObject.put(Const.USERID, MyApplication.getInstance().myDriver.userid);
					jsonObject.put(Const.COMPANYID,MyApplication.getInstance().myDriver.companyID);
					jsonObject.put("firstname",tvName.getText().toString());
					jsonObject.put("passenger_id",tvName.getTag().toString());
					jsonObject.put("current_location",placepickup.getAddress());
					jsonObject.put("drop_location",placedropoff.getAddress());
					jsonObject.put("pax_count",spinner_no_passenger.getSelectedItem().toString());
					jsonObject.put("bags",spinner_no_bag.getSelectedItem().toString());
					jsonObject.put("pickup_date",tvDate.getText().toString());
					jsonObject.put("taxi_model",vehicle.getTaxi_model());
					jsonObject.put("pickup_lat",placepickup.getLatLng().latitude+"");
					jsonObject.put("pickup_lng",placepickup.getLatLng().longitude+"");
					jsonObject.put("drop_lng",placedropoff.getLatLng().longitude+"");
					jsonObject.put("drop_lat",placedropoff.getLatLng().latitude+"");
					jsonObject.put("notes","");
					jsonObject.put("when", "now");
					jsonObject.put("assign", "0");

					if(rb_other.isChecked()){
						jsonObject.put("assign", "1");
					}
					if(rb_later.isChecked()){
						jsonObject.put("when", "later");
					}else{
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						selecteddate=df.format(new Date());
						jsonObject.put("pickup_date",selecteddate+" 00:00");
					}

                   requestCreateJob(jsonObject);

				} catch (JSONException e) {
					e.printStackTrace();
				}





			}
		});





	}

	/*********************it is user to create the new job ***************************/
	public void calenderDialaog(){
		 time="00:00";

		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.caledener_dilaog, null);
		Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);


		Spinner spTime = (Spinner) outerView.findViewById(R.id.spTime);

		//Initialize CustomCalendarView from layout
		CustomCalendarView calendarView = (CustomCalendarView)outerView. findViewById(R.id.calendar_view);

//Initialize calendar with date
		Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());

//Show Monday as first date of week
		calendarView.setFirstDayOfWeek(Calendar.MONDAY);



//Show/hide overflow days of a month
		calendarView.setShowOverflowDate(true);

//call refreshCalendar to update calendar the view
		calendarView.refreshCalendar(currentCalendar);


//Handling custom calendar events

		calendarView.setCalendarListener(new CalendarListener() {
			@Override
			public void onDateSelected(Date date) {

				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date date1=new Date();

				if(!date.before(date1)||date.equals(date1)){
					System.out.println("Date1 is before Date2");
					selecteddate=df.format(date);
				}else{
					CommonMethods.showToast(getActivity(),"Please select the latest date");
				}

				//Toast.makeText(getActivity(), df.format(date), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onMonthChanged(Date date) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				selecteddate=df.format(date);
				//Toast.makeText(getActivity(), df.format(date), Toast.LENGTH_SHORT).show();
			}
		});




		final ArrayList<String> noOfTime=new ArrayList<String>();



		String[] timeArray = {"00:00", "00:30","01:00","01:30","02:00","02:30","03:00","03:30",
				"04:00","04:30","05:00","05:30","06:00","06:30","07:00","07:30","08:00","08:30",
				"09:00","09:30","10:00","10:30","11:00","11:30","12:00","12:30","13:00","13:30",
				"14:00","14:30","15:00","15:30","16:00","16:30","17:00","17:30","18:00","18:30"
				,"19:00","19:30","20:00","20:30","21:00","21:30","22:00","22:30","23:00","23:30"
		};
		noOfTime.addAll(Arrays.asList(timeArray));



		// Creating adapter for spinner
		ArrayAdapter<String> noOfTimeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, noOfTime);


		// Drop down layout style - list view with radio button
		noOfTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner

		spTime.setAdapter(noOfTimeAdapter);


		spTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

				time=noOfTime.get(i);

			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});


		final AlertDialog.Builder alBuilder=new AlertDialog.Builder(getActivity());
		alBuilder.setView(outerView);
		final AlertDialog ad = alBuilder.show();
		ad.setCancelable(false);
		ad.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		ad. getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		ad.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				tvDate.setText(selecteddate+" "+time);
				ad.dismiss();

			}
		});





	}
	/*********************it show the job diologs***************************/
	public void jobOfferDilaog(final Job job){
		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.job_dispatch_dialog, null);
		Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);
		Button btnCancel = (Button) outerView.findViewById(R.id.btnCancel);
		;
		 currentJob=job;
		TextView tvFair = (TextView) outerView.findViewById(R.id.tvFair);
		TextView tvTime = (TextView) outerView.findViewById(R.id.tvTime);
		TextView tvName = (TextView) outerView.findViewById(R.id.tvName);
		TextView tvPickUpLocation = (TextView) outerView.findViewById(R.id.tvPickUpLocation);
		TextView tvNavDropOff = (TextView) outerView.findViewById(R.id.tvNavDropOff);
		TextView tvStatus = (TextView) outerView.findViewById(R.id.tvStatus);
		tvStatus.setText(tvStatus.getText().toString() + "" + job.getWhen());
		tvPickUpLocation.setText(job.getPickup());
		tvNavDropOff.setText(job.getDropoff());
		RatingBar ratingBarNumberOfPerson  = (RatingBar) outerView.findViewById(R.id.ratingBarNumberOfPerson);
		RatingBar ratingBarbag = (RatingBar) outerView.findViewById(R.id.ratingBarbag);
		ratingBarNumberOfPerson.setRating(Float.parseFloat(job.getPassengers()));
		ratingBarbag.setRating(Float.parseFloat(job.getBags()));
		tvFair.setText(job.getFare());
		tvName.setText(job.getPaxname());
		tvTime.setText(job.getDuration());
		final AlertDialog.Builder alBuilder=new AlertDialog.Builder(getActivity());
		alBuilder.setView(outerView);
		final AlertDialog ad = alBuilder.show();
		ad.setCancelable(false);
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



				requestAvailableDriver(job);

			}
		});



	}


	public void requestAvailableDriver(Job job){
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

		ApiManager.getInstance().mUrlManager.availabelDrivers(jsonObject.toString()).cache().subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observerStaus);
	}
	public void requestAvailablePassenger(String tokensearch){
		status=4;
		JSONObject jsonObject=new JSONObject();
		sweetIndicator.show();
		try {

			jsonObject.put(Const.USERID, MyApplication.getInstance().myDriver.userid);
			jsonObject.put(Const.COMPANYID,MyApplication.getInstance().myDriver.companyID);
			jsonObject.put(Const.TOKEN,MyApplication.getInstance().myDriver.token);
			jsonObject.put(Const.KEYWORD,tokensearch);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		ApiManager.getInstance().mUrlManager.searchPassengers(jsonObject.toString()).cache().subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observerStaus);
	}

	public void requestAvailableVehicle(){
		status=3;
		JSONObject jsonObject=new JSONObject();
		sweetIndicator.show();
		try {

			jsonObject.put(Const.USERID, MyApplication.getInstance().myDriver.userid);
			jsonObject.put(Const.COMPANYID,MyApplication.getInstance().myDriver.companyID);
			jsonObject.put(Const.TOKEN,MyApplication.getInstance().myDriver.token);


		} catch (JSONException e) {
			e.printStackTrace();
		}

		ApiManager.getInstance().mUrlManager.vehicleModels(jsonObject.toString()).cache().subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observerStaus);
	}

public void requestCreateJob(JSONObject jsonObject){
		status=5;
	sweetIndicator.show();
		;


		ApiManager.getInstance().mUrlManager.createJob(jsonObject.toString()).cache().subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observerStaus);
	}


	/***************open passenger dialog***************/
	public void openAvailableDriverDialog(ArrayList<Driver> listdriver){


		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.dispatch_driver_list_dilaog, null);
		RecyclerView rv = (RecyclerView) outerView.findViewById(R.id.rv);
		;
		View tvCancel = (View) outerView.findViewById(R.id.tvCancel);


		rv.setHasFixedSize(true);
		rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


		driverlist=  new ArrayList<Driver>();
		driverlist.addAll(listdriver);
		DriverAdapter driverAdapter = new DriverAdapter(getActivity(),driverlist );
		rv.setAdapter(driverAdapter);
		driverAdapter.setOnItemClickListener(this);


		final android.app.AlertDialog.Builder alBuilder=new android.app.AlertDialog.Builder(getActivity());
		alBuilder.setView(outerView);
		driverdialog = alBuilder.show();
		driverdialog.setCancelable(false);
		tvCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				driverdialog.dismiss();
			}
		});



	}


	/********************************Driver In popup**********************************/

	public void openAvailablePassnegerDialog(){


		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.dispatch_passenger_list_dilaog, null);
		RecyclerView rv = (RecyclerView) outerView.findViewById(R.id.rv);
	;
		View tvCancel = (View) outerView.findViewById(R.id.tvCancel);
		EditText edtPassenger = (EditText) outerView.findViewById(R.id.edtPassenger);
		RecyclerView rv_passenger = (RecyclerView) outerView.findViewById(R.id.rv_passenger);

		rv_passenger.setHasFixedSize(true);
		rv_passenger.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
		passengerlist=  new ArrayList<Passenger>();
		passengerAdapter = new PassengerAdapter(getActivity(),passengerlist );
		rv_passenger.setAdapter(passengerAdapter);
		passengerAdapter.setOnItemClickListener(this);

		edtPassenger.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				requestAvailablePassenger(charSequence.toString());
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

		final android.app.AlertDialog.Builder alBuilder=new android.app.AlertDialog.Builder(getActivity());
		alBuilder.setView(outerView);
		passengerdialog = alBuilder.show();
		passengerdialog.setCancelable(false);

		tvCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				passengerdialog.dismiss();
			}
		});
	}


	@Override
	public void onDriverItemClick(View view, int position) {


       Driver driver= driverlist.get(position);


			status=2;
			JSONObject jsonObject=new JSONObject();
			sweetIndicator.show();
			try {

				jsonObject.put(Const.USERID, MyApplication.getInstance().myDriver.userid);
				jsonObject.put(Const.COMPANYID,MyApplication.getInstance().myDriver.companyID);
				jsonObject.put(Const.TOKEN,MyApplication.getInstance().myDriver.token);
				jsonObject.put(Const.DRIVER_ID,driver.getDriver_id());
				jsonObject.put(Const.JOBID,currentJob.getJobid());

			} catch (JSONException e) {
				e.printStackTrace();
			}

			ApiManager.getInstance().mUrlManager.assignJob(jsonObject.toString()).cache().subscribeOn(Schedulers.newThread())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(observerStaus);


	}

	@Override
	public void onPassengerItemClick(View view, int position) {

		Passenger passenger=passengerlist.get(position);
		tvName.setText(passenger.getFirstname());
		tvName.setTag(passenger.getPassenger_id());
		tvPhone.setText(passenger.getPhone());
		passengerdialog.dismiss();

	}
}
