package mobileapps.technroid.io.cabigate.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import mobileapps.technroid.io.cabigate.R;
import mobileapps.technroid.io.cabigate.global.Constants;

@SuppressLint("NewApi")
public class DirectoryUtils
{
    public static File getMainPath(Context mContext)
    {
        if (Constants.isSandBox)
        {
            return Environment.getExternalStorageDirectory();
        }
        else
        {
            //return Environment.getExternalStorageDirectory();
            return mContext.getFilesDir();
        }
    }

    public static void createAppDirectories(Context mContext)
    {
        File path = getMainPath(mContext);

        File directory = new File(path + "/" + mContext.getString(R.string.app_name) +"/");
        File mainFile;
        mainFile = new File(directory, Constants.TEMP_DIRECTORY);

        if(mainFile.exists())
        {
            final File tempFile = new File(directory, Constants.TEMP_DIRECTORY);
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (tempFile.isDirectory())
                        for (String child : tempFile.list())
                            new File(tempFile, child).delete();
                }
            }).start();
        }
        else
            mainFile.mkdirs();

        mainFile = new File(directory, Constants.VIDEO_DIRECTORY);
        mainFile.mkdirs();
    }

    public static File getMainDirectory(Context mContext)
    {
        File path = getMainPath(mContext);
        File directory = new File(path + "/" + mContext.getString(R.string.app_name) +"/");
        directory.mkdirs();
        return directory;
    }

    public static File getTempDirectory(Context mContext)
    {
        File path = getMainPath(mContext);
        File directory = new File(path + "/" + mContext.getString(R.string.app_name) +"/");
        directory.mkdirs();
        final File mainFile = new File(directory, Constants.TEMP_DIRECTORY);
        mainFile.mkdirs();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (mainFile.isDirectory())
                    for (String child : mainFile.list())
                        new File(mainFile, child).delete();

            }
        }).start();
        return mainFile;
    }

    public static File getDirectory(Context mContext, String name)
    {
        File path = getMainPath(mContext);
        File directory = new File(path + "/" + mContext.getString(R.string.app_name) +"/" + name + "/");
        directory.mkdirs();
        return directory;
    }

    public static File createVideoFile(Context mContext, InputStream in, String filename)
    {
        File destinationFile = new File(getDirectory(mContext, Constants.VIDEO_DIRECTORY), filename);
        try{
            destinationFile.createNewFile();
            //For Overwrite the file.
            OutputStream out = new FileOutputStream(destinationFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
        }
        catch(FileNotFoundException ex){
            System.out.println(ex.getMessage() + " in the specified directory.");
            return null;
        }
        catch(IOException e){
            return null;
        }
        return destinationFile;
    }

	public static File createFileFromBitmap(Context context, Bitmap bm) throws IOException
	{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + ".png";

        File path = getMainPath(context);
        File directory = new File(path + "/" + context.getString(R.string.app_name) +"/" + Constants.TEMP_DIRECTORY + "/");
        directory.mkdirs();
        File mainFile = new File(directory, imageFileName);
        try {
            if(!mainFile.exists())
            {
                mainFile.createNewFile();
            }
        } catch (Exception e) {
        }

		//Convert bitmap to byte array
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bm.compress(CompressFormat.PNG, 100, bos);
		byte[] bitmapdata = bos.toByteArray();

		//write the bytes in file
		FileOutputStream fos = new FileOutputStream(mainFile);
		fos.write(bitmapdata);
		fos.close();
		return mainFile;
	}

    public static File createTemporaryFile(String ext, Context mContext) throws Exception
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ext;

        File path = getMainPath(mContext);
        File directory = new File(path + "/" + mContext.getString(R.string.app_name) +"/" + Constants.TEMP_DIRECTORY + "/");
        directory.mkdirs();
        File mainFile = new File(directory, imageFileName);
        try {
            if(!mainFile.exists())
            {
                mainFile.createNewFile();
            }
        } catch (Exception e) {
        }

        return mainFile;
    }

    public static File ifVideoFileExists(Context mContext, String mediaName)
    {
        File path = getMainPath(mContext);
        File mainFile = new File(path + "/" + mContext.getString(R.string.app_name) +"/" + Constants.VIDEO_DIRECTORY + "/");
        final String fileName = mediaName.substring(mediaName.lastIndexOf("/")+1);

        mainFile = new File(mainFile, fileName);
        if(mainFile.exists())
            return mainFile;
        else
            return null;
    }
}