package mobileapps.technroid.io.cabigate.ui.fragment;


import android.app.Dialog;
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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.api.ApiManager;
import mobileapps.technroid.io.cabigate.app.MyApplication;
import mobileapps.technroid.io.cabigate.helper.Const;
import mobileapps.technroid.io.cabigate.helper.SharedPrefs;
import mobileapps.technroid.io.cabigate.helper.SnackBarDisplay;
import mobileapps.technroid.io.cabigate.models.Job;
import mobileapps.technroid.io.cabigate.models.Zones;
import mobileapps.technroid.io.cabigate.ui.activity.JobViewActivity;
import mobileapps.technroid.io.cabigate.ui.adapter.OffersAdapter;
import mobileapps.technroid.io.cabigate.ui.adapter.ZonesAdapter;
import mobileapps.technroid.io.cabigate.ui.widgets.alertbox.SweetAlertDialog;
import mobileapps.technroid.io.cabigate.utilities.Utilities;
import retrofit.client.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public final class FragmentOffers extends Fragment implements ZonesAdapter.OnItemClickListener, OffersAdapter.OnItemClickListener {


	private SweetAlertDialog sweetIndicator;
	private SharedPrefs sharedPrefs;

	private int status;
	private ArrayList<Zones> zoneslist;
	private ArrayList<Job> openJobList;
	private OffersAdapter offersAdapter;
	private MyApplication app;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_offer, container,
				false);
		sharedPrefs=new SharedPrefs(getActivity());
		sweetIndicator = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
		sweetIndicator.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
		sweetIndicator.setTitleText("Please wait...");
		sweetIndicator.setCancelable(false);
		ButterKnife.bind(this, rootView);

		 app=MyApplication.getInstance();



		RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv);
		rv.setHasFixedSize(true);
		rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
		openJobList=  new ArrayList<Job>();
		offersAdapter = new OffersAdapter(getActivity(),openJobList );
		rv.setAdapter(offersAdapter);
		offersAdapter.setOnItemClickListener(this);

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


		ApiManager.getInstance().mUrlManager.getOffers(jsonObject.toString()).cache().subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(observerStaus);

		return rootView;
	}
@Override
public void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();


}

	@Override
	public void onItemClick(View view, int position) {
		jobOfferDilaog(openJobList.get(position));
	}


	/*********************it show the job diologs***************************/
	public void jobOfferDilaog(final Job job){

		View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_job_offer, null);
		Button btnConfirm = (Button) outerView.findViewById(R.id.btnConfirm);
		Button btnCancel = (Button) outerView.findViewById(R.id.btnCancel);
		TextView tvFair = (TextView) outerView.findViewById(R.id.tvFair);
		TextView tvTime = (TextView) outerView.findViewById(R.id.tvTime);
		TextView tvName = (TextView) outerView.findViewById(R.id.tvName);
		final TextView tvTimer = (TextView) outerView.findViewById(R.id.tvTimer);
		final TextView tvBag = (TextView) outerView.findViewById(R.id.tvBag);
		final TextView tvPass = (TextView) outerView.findViewById(R.id.tvPass);
		TextView tvPickUpLocation = (TextView) outerView.findViewById(R.id.tvPickUpLocation);
		TextView tvDropOffTitle = (TextView) outerView.findViewById(R.id.tvDropOffTitle);
		TextView tvDropOff = (TextView) outerView.findViewById(R.id.tvDropOff);
		TextView tvPickUp = (TextView) outerView.findViewById(R.id.tvPickUp);
		tvPickUp.setText(job.getWhen());
		tvPickUpLocation.setText(job.getPickup());

		if(!job.getDropoff().equals("")) {
			tvDropOff.setText(job.getDropoff());
			tvDropOff.setVisibility(View.VISIBLE);
			tvDropOffTitle.setVisibility(View.VISIBLE);

		}
		/*RatingBar ratingBarNumberOfPerson  = (RatingBar) outerView.findViewById(R.id.ratingBarNumberOfPerson);
		RatingBar ratingBarbag = (RatingBar) outerView.findViewById(R.id.ratingBarbag);
		ratingBarNumberOfPerson.setRating(Float.parseFloat(job.getPassengers()));
		ratingBarbag.setRating(Float.parseFloat(job.getBags()));
		*/
		tvFair.setText(job.getFare());
		tvName.setText(job.getPaxname());
		tvTime.setText(job.getDuration());
		tvBag.setText(job.getBags());
		tvPass.setText(job.getPassengers());


		final Dialog ad= new Dialog(getActivity(),R.style.MyDialog);
		ad.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ad.setContentView(outerView);
		ad.show();

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
							JSONArray openjobs=response1.getJSONArray("list");
							openJobList.addAll((ArrayList<Job>) gson.fromJson(openjobs.toString(), new TypeToken<List<Job>>() {
							}.getType()));
							offersAdapter.notifyDataSetChanged();
							break;
						case 1:
							((JobViewActivity)getActivity()).moveToQueueSection();


							break;
						case 2:


							break;
						case 3:


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


}
