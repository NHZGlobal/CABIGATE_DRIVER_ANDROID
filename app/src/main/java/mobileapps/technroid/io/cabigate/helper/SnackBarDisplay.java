package mobileapps.technroid.io.cabigate.helper;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

import mobileapps.technroid.io.cabigate.R;

/**
 * Created by LAPTOP WORLD on 19/02/2016.
 */
public class SnackBarDisplay {
   public static TSnackbar snackbar;

    public static void ShowSnackBar(Activity mcontext,String text){
        TSnackbar snackbar = TSnackbar
                .make(mcontext.findViewById(android.R.id.content), "  "+text,600);
      // snackbar.setDuration(TSnackbar.LENGTH_SHORT);

        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorPrimary));

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        snackbar.show();


    }
    public static void ShowSnackBarLong(Activity mcontext,String text,boolean check){

        if(check)
        {
         snackbar = TSnackbar
                .make(mcontext.findViewById(android.R.id.content), "  "+text,600).setDuration((60*1000)*60);
        // snackbar.setDuration(TSnackbar.LENGTH_SHORT);

        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorPrimary));

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

            snackbar.show();
        }
        else
        {
          snackbar.dismiss();
        }


    }

    public static void ShowSnackBarTime(Activity mcontext,String text){
        TSnackbar snackbar = TSnackbar
                .make(mcontext.findViewById(android.R.id.content), "  "+text,1500);
        // snackbar.setDuration(TSnackbar.LENGTH_SHORT);

        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorAccent));

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        snackbar.show();


    }
 public static void ShowSnackBarTimeFragment(Activity mcontext,View view,String text){
        TSnackbar snackbar = TSnackbar
                .make(view, "  "+text,1500);
        // snackbar.setDuration(TSnackbar.LENGTH_SHORT);

        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorAccent));

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        snackbar.show();


    }



}
