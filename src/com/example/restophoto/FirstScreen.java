package com.example.restophoto;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class FirstScreen extends Activity {
	public static String DEBUG_TAG;
	
	GPSTracker gps;
	//List<ReviewProvider> reviewProviders = new LinkedList<ReviewProvider>();
	ReviewProvider qype = null;
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_pic);
		
		// Copy raw ressources on sdcard
		//copyRessourceOnSDCard();

		// Add review providers
        //reviewProviders.add(new Qype());
        qype = new Qype();
		
        // Check GPS
        
        //gps = new GPSTracker(getApplicationContext());
        //checkGPS(gps);
        //qype.getNearbyRestaurants(gps.getLatitude(), gps.getLongitude());
        qype.getNearbyRestaurants(48.828826, 2.35074);
		
        /*
        // create Intent to take a picture and return control to the calling application
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		//fileUri = getOutputPhotoFileUri(); // create a file to save the image TODO does not work with cyanogen mod
		//intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name TODO does not work with cyanogenmod
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
				Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "photoresto.jpg")));
		//Toast.makeText(this, "Saving pics to:\n" +
        //         "/sdcard/photoresto.jpg", //fileUri, 
        //         Toast.LENGTH_LONG).show();
		// start the image capture Intent
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		*/
        
        File f = new File(Environment.getExternalStorageDirectory().getPath(), "/photoresto/chez_gladines.jpg");
        String text = ocr(f.toString());
        Log.d(DEBUG_TAG, text);
	}

	private static Uri getOutputPhotoFileUri(){ // TODO unused with cyanogenmod
		return Uri.fromFile(getOutputPhotoFile());
	}

	private static File getOutputPhotoFile(){ // TODO unused with cyanogenmod
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "photoresto");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("photoresto", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");

		return mediaFile;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent
	            Toast.makeText(this, "Image saved to:\n" +
	                     data.getData(), Toast.LENGTH_LONG).show();	            
	            	
	            // Retrieve match
	            List<Resto> match = new LinkedList<Resto>();
	            /*for (ReviewProvider provider : reviewProviders) {
	            	List<Resto> candidates = provider.getNearbyRestaurants(lat, lon);
	            	match.add(findBestMatch(candidates));
	            }*/
	            
	            //TODO match.add(qype.findBestMatch(ocr(
	            //		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "photoresto.jpg")));            
	            
	            
	            // Go to review webpage
	            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(match.get(0).reviewURL));
	            startActivity(browserIntent);
	            
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        	Toast.makeText(this, "You cancelled the capture.", Toast.LENGTH_LONG).show();
	        } else {
	            // Image capture failed, advise user
	        	Toast.makeText(this, "Capture failed :(", Toast.LENGTH_LONG).show();
	        }
	    }
	}
	    
    void checkGPS(GPSTracker gps) {
    	if (!gps.canGetLocation()) {
    	    gps.showSettingsAlert();
    	    if (!gps.canGetLocation()) {    
    	      finish();
    	    }
    	}
    }
    
    String ocr(String imagePath) {
    	try {
	    	ExifInterface exif = new ExifInterface(imagePath);
	    	int exifOrientation = exif.getAttributeInt(
	    	        ExifInterface.TAG_ORIENTATION,
	    	        ExifInterface.ORIENTATION_NORMAL);
	
	    	int rotate = 0;
	
	    	switch (exifOrientation) {
	    	case ExifInterface.ORIENTATION_ROTATE_90:
	    	    rotate = 90;
	    	    break;
	    	case ExifInterface.ORIENTATION_ROTATE_180:
	    	    rotate = 180;
	    	    break;
	    	case ExifInterface.ORIENTATION_ROTATE_270:
	    	    rotate = 270;
	    	    break;
	    	}
	
	    	Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
	    	if (rotate != 0) {	    		
	    	    int w = bitmap.getWidth();
	    	    int h = bitmap.getHeight();
	
	    	    // Setting pre rotate
	    	    Matrix mtx = new Matrix();
	    	    mtx.preRotate(rotate);
	
	    	    // Rotating Bitmap & convert to ARGB_8888, required by tess
	    	    bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
	    	    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
	    	}
	    	
	    	File myDir = new File(Environment.getExternalStorageDirectory().getPath(), "/photoresto/tesseract");
	    	Log.d(DEBUG_TAG, myDir.toString());
	    	TessBaseAPI baseApi = new TessBaseAPI();
	    	baseApi.init(myDir.toString(), "fra"); 
	    	baseApi.setImage(bitmap);
	    	String recognizedText = baseApi.getUTF8Text();
	    	baseApi.end();
	    	
	    	return recognizedText;
    	}
    	catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
    
    void copyRessourceOnSDCard() {    	
    	File myDir = new File(Environment.getExternalStorageDirectory().getPath(), "/tesseract");    	
    	if(!myDir.isDirectory()) {
    		unpackZip(getResources().openRawResource(R.raw.data), 
    				  Environment.getExternalStorageDirectory().getPath());
    	}
    }
    
    private boolean unpackZip(InputStream is, String outpath)
    {       
         ZipInputStream zis;
         try 
         {
             String filename;
             zis = new ZipInputStream(new BufferedInputStream(is));          
             ZipEntry ze;
             byte[] buffer = new byte[1024];
             int count;

             while ((ze = zis.getNextEntry()) != null) 
             {
                 filename = ze.getName();

                 // Need to create directories if not exists, or
                 // it will generate an Exception...
                 if (ze.isDirectory()) {
                    File fmd = new File(outpath + filename);
                    fmd.mkdirs();
                    continue;
                 }

                 FileOutputStream fout = new FileOutputStream(outpath + filename);

                 while ((count = zis.read(buffer)) != -1) 
                 {
                     fout.write(buffer, 0, count);             
                 }

                 fout.close();               
                 zis.closeEntry();
             }

             zis.close();
         } 
         catch(Exception e)
         {
             throw new RuntimeException(e);
         }

        return true;
    }
}

