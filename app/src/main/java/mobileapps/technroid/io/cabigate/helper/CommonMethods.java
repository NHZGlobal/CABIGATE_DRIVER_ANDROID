package mobileapps.technroid.io.cabigate.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class CommonMethods {


    public static DecimalFormat df = new DecimalFormat(".##");


    public static String roundValue(double value) {


        return df.format(Math.round(value * 100.0) / 100.0) + "";


    }


    public static String getSeprtaedImageFromPath(String images) {


        String[] imagearray = images.split("<>");
        if (imagearray.length > 0)
            for (int i = 0; i < imagearray.length; i++) {

                return imagearray[0];

            }
        return "";
    }


    public static UUID getUUID(String uuid) {
        return UUID.fromString(uuid);
    }


    public static int toInteger(String numaricdate) {


        try {
            Integer.parseInt(numaricdate);
        } catch (NumberFormatException nfe) {
            return 0;
        }
        return Integer.parseInt(numaricdate);


    }

    public static String formatDecimal(float number) {
        float epsilon = 0.004f; // 4 tenths of a cent
        if (Math.abs(Math.round(number) - number) < epsilon) {
            return String.format("%10.2f", number); // sdb
        } else {
            return String.format("%10.2f", number); // dj_segfault
        }
    }   public static String formatDecimal(double number) {
        float epsilon = 0.004f; // 4 tenths of a cent
        if (Math.abs(Math.round(number) - number) < epsilon) {
            return String.format("%10.2f", number); // sdb
        } else {
            return String.format("%10.2f", number); // dj_segfault
        }
    }


    public static Bitmap screenShot(View view) {

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);


        return bitmap;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return false; //!org.apache.http.util.TextUtils.isEmpty(locationProviders);
        }


    }


    /*takeScreenshot*/
    public static Bitmap takeScreenshot(Activity activity) {
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decorChild.setDrawingCacheEnabled(true);
        decorChild.buildDrawingCache();
        Bitmap drawingCache = decorChild.getDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(drawingCache);
        decorChild.setDrawingCacheEnabled(false);
        return bitmap;
    }

/*open aler dailog  */

    public static void open(String title, String message, Context activty) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activty);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                /*Intent positveActivity = new Intent(getApplicationContext(),com.example.alertdialog.PositiveActivity.class);
                startActivity(positveActivity);*/


                    }
                });
        alertDialogBuilder.show();

    }

    public static void openActivtyCloss(String title, String message, final Activity activty) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activty);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        activty.finish();

                    }
                });
        alertDialogBuilder.show();

    }


    public static void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public static String getConvertedDateWithServerTime(String dateString) {
        Calendar systemCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = (Date) formatter.parse(dateString);
            int gmtOffset = 0;//TimeZone.getDefault().getOffset(date.getTime());

            CharSequence myDateString = DateUtils.getRelativeTimeSpanString(date.getTime() + gmtOffset, systemCal.getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
            return myDateString.toString().replace("minutes", "min");
        } catch (Exception e) {
            return "";
        }
    }


    public static String getConvertedDateWithServerTime(Date dateString) {
        Calendar systemCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            String datestring = formatter.format(dateString);
            int gmtOffset = 0;//TimeZone.getDefault().getOffset(date.getTime());

            ///CharSequence myDateString = DateUtils.getRelativeTimeSpanString(date.getTime() + gmtOffset, systemCal.getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
            return datestring;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getConvertedDate(Date dateString) {
        Calendar systemCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            //formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            String datestring = formatter.format(dateString);
            int gmtOffset = 0;//TimeZone.getDefault().getOffset(date.getTime());

            ///CharSequence myDateString = DateUtils.getRelativeTimeSpanString(date.getTime() + gmtOffset, systemCal.getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
            return datestring;
        } catch (Exception e) {
            return "";
        }
    }


    public static Bitmap compressPhotoIfNeeded(String galleryImagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(galleryImagePath, options);

        options.inJustDecodeBounds = false;

        int photoWidth = options.outWidth;

        int photoHeight = options.outHeight;

        if (photoWidth > 2000 || photoHeight > 2000) {
            System.out.println("inSampleSize = 8");

            options.inSampleSize = 8;

            Bitmap photo = BitmapFactory.decodeFile(galleryImagePath, options);

            storeImage(photo, galleryImagePath);


            //addPhotoToProfile(photo);
            //profileimage.setImageBitmap(photo);
            return photo;
        }

        if (photoWidth > 1024 || photoHeight > 1024) {
            System.out.println("inSampleSize = 4");

            options.inSampleSize = 4;

            Bitmap photo = BitmapFactory.decodeFile(galleryImagePath, options);
            storeImage(photo, galleryImagePath);
            //addPhotoToProfile(photo);
            //profileimage.setImageBitmap(photo);
            return photo;
        }

        if (photoWidth > 512 || photoHeight > 512) {
            System.out.println("inSampleSize = 2");

            options.inSampleSize = 2;

            Bitmap photo = BitmapFactory.decodeFile(galleryImagePath, options);
            storeImage(photo, galleryImagePath);
            //profileimage.setImageBitmap(photo);
            //	addPhotoToProfile(photo);

            return photo;
        }

        Bitmap photo = BitmapFactory.decodeFile(galleryImagePath, options);
        storeImage(photo, galleryImagePath);
        return photo;
        //profileimage.setImageBitmap(photo);
        //addPhotoToProfile(photo);
    }


    public static boolean storeImage(Bitmap imageData, String filePath) {


        try {
            //String filePath = sdIconStorageDir.toString() + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            imageData.compress(CompressFormat.PNG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        }

        return true;
    }


    public static boolean storeImageGallery(Bitmap imageData, String filePath) {

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);


            imageData.compress(CompressFormat.PNG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file:" + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file:" + e.getMessage());
            return false;
        }

        return true;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public static String getImagePath() {

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "EyeTravels");


        return mediaStorageDir.getPath() + File.separator;


    }


    public static boolean isSet(String string) {
        if (string != null && string.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isValidURL(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }


    public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
       /* if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }*/
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
        /*::  This function converts decimal degrees to radians             :*/
	    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	    /*::  This function converts radians to decimal degrees             :*/
	    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public static void showToast(Activity context, String text) {


        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();


    }


    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Activity _context) {
        int columnWidth;

        Display display = (_context).getWindowManager().getDefaultDisplay();
        Point size = new Point();

        columnWidth = display.getWidth();
        return columnWidth;
    }


    public static boolean checkPasw(String pass) {

        if (TextUtils.isEmpty(pass) || pass.length() < 5) {
            // passwordEditText.setError("You must have x characters in your password");
            return false;
        }
        return true;

        //continue processing

    }

    public static void changeActionbartext(ActionBar actionBar, String title) {


        actionBar.setTitle(title);
    }


    public static void performDial(String numberString, Activity activty) {
        // if (!numberString.equals("")) {
        Uri number = Uri.parse("tel:" + numberString);

        Intent dial = new Intent();
        dial.setAction("android.intent.action.DIAL");
        dial.setData(number);

        activty.startActivity(dial);
        // }
    }

    public static void performEmail(String email, String subject, Activity activty) {
        // if (!numberString.equals("")) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        //i.putExtra(Intent.EXTRA_TEXT   , "body of email");
        try {
            activty.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activty, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
        // }
    }

    public static void openPerformCall(final String title, final String message, final Activity activty, final String contact) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activty);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setPositiveButton("Call",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        performDial(contact, activty);

                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    public static void open(final Activity activty, final String contact) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activty);
        alertDialogBuilder.setMessage(contact);
        alertDialogBuilder.setPositiveButton("Call",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        performDial(contact, activty);

                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public static void changeActionbartext(ActionBar actionBar) {


        //actionBar.setTitle(title);
		/*BaseActivity.sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		actionBar.setIcon(R.drawable.ic_launcher);
		actionBar.setHomeButtonEnabled(false);*/


    }

    public static void changeActionbarHometext(ActionBar actionBar) {


        //actionBar.setTitle(title);
		/*BaseActivity.sm.setTouchModeAbove(SlidingMenu.LEFT);
		actionBar.setIcon(R.drawable.menu_top_icon);
		actionBar.setHomeButtonEnabled(true);*/


    }


    public static boolean isNetworkAvailable(Activity context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static Bitmap ScaleBitmap(Bitmap bm, float scalingFactor) {
        int scaleHeight = (int) (bm.getHeight() * scalingFactor);
        int scaleWidth = (int) (bm.getWidth() * scalingFactor);

        return Bitmap.createScaledBitmap(bm, scaleWidth, scaleHeight, true);
    }


    public static boolean isPassword(String string, String string1) {
        if (string.equals(string1)) {
            return true;
        } else {
            return false;
        }
    }


    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;

    }


    public static void showCustomAlert(Activity _activity) {/*

			Context context = _activity;

			LayoutInflater inflater = _activity.getLayoutInflater();

			// Call toast.xml file for toast layout
			View toastRoot = inflater.inflate(R.layout.cutom_toast, null);

			TextView message = (TextView) toastRoot.findViewById(R.id.tv_message);
			message.setText("Verify your device doesn\'t have internet connection!");
			
				ImageView iv_message = (ImageView) toastRoot
						.findViewById(R.id.message_image);
				iv_message.setImageResource(R.drawable.error);

			

			Toast toast = new Toast(context);

			// Set layout to toast
			toast.setView(toastRoot);
			toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
					0, 0);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.show();

		*/
    }


    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    public static String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public static String getDate() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        return sdf.format(new Date());

    }

    public static String getTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aa", Locale.US);
        return sdf.format(new Date());

    }

    public static void setTextSpanColor(TextView view, String fulltext, String subtext, int color) {
        view.setText(fulltext, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) view.getText();
        int i = fulltext.indexOf(subtext);
        str.setSpan(new RelativeSizeSpan(0.9f), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    public static void initiateAppUri(Context myContext, String Uri) {

        // Make sure the Skype for Android client is installed.
        if (!isAppInstalled(myContext, Uri)) {
            goToMarket(myContext, Uri);
            return;
        }

        Intent myIntent = myContext.getPackageManager()
                .getLaunchIntentForPackage(Uri);

        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        myContext.startActivity(myIntent);

        return;
    }


    public static void shareApAppUri(Context myContext, String Uri) {

        // Make sure the Skype for Android client is installed.
        if (!isAppInstalled(myContext, Uri)) {
            goToMarket(myContext, Uri);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage(Uri);

        //intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.devicebee.shopat&hl=en");

        myContext.startActivity(Intent.createChooser(intent, "choose one"));

        return;
    }


    public static void goToMarket(Context myContext, String uri) {
        Uri marketUri = Uri.parse("market://details?id=" + uri);
        Intent myIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myContext.startActivity(myIntent);

        return;
    }

    public static boolean isAppInstalled(Context myContext, String uri) {
        PackageManager myPackageMgr = myContext.getPackageManager();
        try {
            myPackageMgr.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return (false);
        }
        return (true);
    }


    public static String getMondayDate() {

        try {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

            Date monday = c.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Log.d("monday", sdf.format(monday));

            return sdf.format(monday);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String getTuesdayDate() {

        try {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);

            Date monday = c.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Log.d("tuesday", sdf.format(monday));

            return sdf.format(monday);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String getWednesdayDate() {

        try {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);

            Date monday = c.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Log.d("wednesday", sdf.format(monday));

            return sdf.format(monday);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


    public static String getThursdayDate() {

        try {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);

            Date monday = c.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Log.d("thursday", sdf.format(monday));

            return sdf.format(monday);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String getFridayDate() {

        try {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

            Date monday = c.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Log.d("friday", sdf.format(monday));

            return sdf.format(monday);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String getSaturdayDate() {

        try {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

            Date monday = c.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Log.d("saturday", sdf.format(monday));

            return sdf.format(monday);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String getSundayDate() {

        try {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

            Date monday = c.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Log.d("sunday", sdf.format(monday));

            return sdf.format(monday);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String[] getWeekDates() {
        return new String[]{getMondayDate(), getTuesdayDate(),
                getWednesdayDate(), getThursdayDate(), getFridayDate(),
                getSaturdayDate(), getSundayDate()};
    }

    public static String monthName(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }


}
