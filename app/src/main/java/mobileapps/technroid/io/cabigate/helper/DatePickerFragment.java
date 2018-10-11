package mobileapps.technroid.io.cabigate.helper;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment {
	
	  private static Fragment mFragment;
	  
	  public static final DatePickerFragment newInstance()
	  {
		  DatePickerFragment fragment = new DatePickerFragment();
	    //  mFragment = ;
	    
	      return fragment ;
	  }
	
	  public DatePickerFragment () {
	       
	    }
	 

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), (OnDateSetListener) getActivity(), year, month, day);
	}
	
	

	
}