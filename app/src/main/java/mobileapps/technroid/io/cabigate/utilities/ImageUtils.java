package mobileapps.technroid.io.cabigate.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import mobileapps.technroid.io.cabigate.custom.CircleTransform;


@SuppressLint("NewApi")
public class ImageUtils
{
    public static void setRoundedBitmapFromFile(String url, ImageView imgView, Context mContext, int placeHolderRecource)
    {
        Picasso.with(mContext).
                load("file://" + url)
                .centerCrop().placeholder(placeHolderRecource)
                .transform(new CircleTransform()).resize(Utilities.dpToPx(100, mContext), Utilities.dpToPx(100, mContext)).into(imgView);
    }

    public static void setRoundedBitmap(String url, ImageView imgView, Context mContext, int placeHolderRecource)
    {
        Picasso.with(mContext).
                load(url)
                .centerCrop().placeholder(placeHolderRecource)
                .transform(new CircleTransform()).resize(Utilities.dpToPx(100, mContext), Utilities.dpToPx(100, mContext)).into(imgView);
    }

    public static void setRoundedBitmap(String url, ImageView imgView, Context mContext)
    {
        Picasso.with(mContext).
                load(url)
                .centerCrop()
                .transform(new CircleTransform()).resize(100, 100).into(imgView);
    }


    public static void setCenterInsideBitmap(String url, ImageView imgView, Context mContext)
    {
        Picasso.with(mContext).
                load(url).fit().centerInside()
                .into(imgView);
    }



    public static void setImageFromFile(String url, ImageView imgView)
    {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeFile(url, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, imgView.getWidth(), imgView.getHeight());
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(url, options);
            imgView.setImageBitmap(bitmap);
        }catch (Exception e)
        {}
    }

    public static Bitmap getBitmapFromFile(String url, int size)
    {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(url, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, size, size);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(url, options);
        if (bitmap == null || bitmap.isRecycled())
        {
            return null;
        }
        else
            return bitmap;
    }

    public static Bitmap getBitmapFromFile(String url,  int width, int height)
    {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(url, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(url, options);
    }

    public static Bitmap getBitmapFromReturnedImage(Context mContext, Uri selectedImage, int reqWidth, int reqHeight)
    {
        try {
            InputStream inputStream = mContext.getContentResolver().openInputStream(selectedImage);

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            BitmapFactory.decodeFile(selectedImage.getPath(), options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            inputStream.close();
            inputStream = mContext.getContentResolver().openInputStream(selectedImage);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(inputStream, null, options);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeSampledBitmap(byte[] image, int reqWidth, int reqHeight)
    {
        if (image != null)
        {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeByteArray(image, 0, image.length, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(image, 0, image.length, options);
        }
        else
            return null;
    }

    public static Bitmap getCircularBitmap(String url, int mainSize)
    {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(url, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, mainSize, mainSize);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inMutable = true;
        Bitmap source = BitmapFactory.decodeFile(url, options);
        if (source == null || source.isRecycled())
        {
            return null;
        }

        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size/2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    /**********************************************************************************************
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     *********************************************************************************************/
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
    public static Bitmap setOrientationOfBitmapExif(Bitmap bitmap, int exifOrientation)
    {
        try
        {
            final Matrix bitmapMatrix = new Matrix();
            switch(exifOrientation)
            {
                case 1:                                                                                     break;  // top left
                case 2:                                                 bitmapMatrix.postScale(-1, 1);      break;  // top right
                case 3:         bitmapMatrix.postRotate(180);                                               break;  // bottom right
                case 4:         bitmapMatrix.postRotate(180);           bitmapMatrix.postScale(-1, 1);      break;  // bottom left
                case 5:         bitmapMatrix.postRotate(90);            bitmapMatrix.postScale(-1, 1);      break;  // left top
                case 6:         bitmapMatrix.postRotate(90);                                                break;  // right top
                case 7:         bitmapMatrix.postRotate(270);           bitmapMatrix.postScale(-1, 1);      break;  // right bottom
                case 8:         bitmapMatrix.postRotate(270);                                               break;  // left bottom
                default:                                                                                    break;  // Unknown
            }

            // Create new bitmap.
            final Bitmap transformedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), bitmapMatrix, false);
            return transformedBitmap;
        }
        catch(Exception e)
        {
            return bitmap;
        }
    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(Utilities.dpToPx(36,context), Utilities.dpToPx(43,context)));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public static Bitmap getCircularBitmapWithBorder(String url, int color, int size, int borderWidth, int resId, Context mContext)
    {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(url, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, size, size);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inMutable = true;
        Bitmap bitmap = null;

        if(resId != 0)
        {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId, options);
            bitmap = bitmap.createScaledBitmap(bitmap , size, size, true);
        }
        else
             bitmap = BitmapFactory.decodeFile(url, options);

        if (bitmap == null || bitmap.isRecycled())
        {
            return null;
        }

        final int width  = size + borderWidth;
        final int height = size + borderWidth;

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);
        float radius = width > height ? ((float) height) / 2f : ((float) width) / 2f;
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2, paint);
        return canvasBitmap;
    }

    public static Uri saveBitmapToFileForUri(Bitmap bm, File fileToSend)
    {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileToSend);
            bm.compress(CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                    return Uri.fromFile(fileToSend);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  null;
    }

    public static File saveImageWithReducedSize(Context mContext, String url, int size)
    {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeFile(url, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, size, size);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(url, options);
            return DirectoryUtils.createFileFromBitmap(mContext, bitmap);


        }catch (Exception e)
        {}

        return null;
    }
}
