package mobileapps.technroid.io.cabigate.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;

public class ReminderServices {
	
	
	
	
	private Context mContext;
	private SharedPreferences mPreferences;


	public ReminderServices(Context context) {
	    mContext = context;
	    mPreferences = context.getSharedPreferences(PREFS_NAME, 0);
	    
	    
	    long firstDate = mPreferences.getLong(KEY_FIRST_HIT_DATE, -1L);

	    if (firstDate == -1L) {
	      registerDate();
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	  }

		

		  private static final String PREFS_NAME = "eyetravel_data";
		  private static final String KEY_FIRST_HIT_DATE = "KEY_FIRST_HIT_DATE";
	
		  public boolean shouldShowServices() {
				 
			    long firstDate = mPreferences.getLong(KEY_FIRST_HIT_DATE, 0L);
			    long today = new Date().getTime();
			    int maxDaysAfter =1;
			    Log.e("daybetween", daysBetween(firstDate, today)+"");
			    if (daysBetween(firstDate, today) > maxDaysAfter) {
			    	registerDate();
			      return true;
			    }

			    return false;
			  }

		  public boolean shouldShow() {
		 
		    long firstDate = mPreferences.getLong(KEY_FIRST_HIT_DATE, 0L);
		    long today = new Date().getTime();
		    int maxDaysAfter =2;
		    Log.e("daybetween", minuteBetween(firstDate, today)+"");
		    if (minuteBetween(firstDate, today) > maxDaysAfter) {
		    	registerDate();
		      return true;
		    }

		    return false;
		  }
		  private void registerDate() {
		    Date today = new Date();
		    mPreferences
		        .edit()
		        .putLong(KEY_FIRST_HIT_DATE, today.getTime())
		        .commit();
		  }

		  private long minuteBetween(long firstDate, long lastDate) {
		    return (lastDate - firstDate) / (1000 * 60 );
		  }
		  
		  private long daysBetween(long firstDate, long lastDate) {
			    return (lastDate - firstDate) / (1000 *60*60 );
			  }

}
	