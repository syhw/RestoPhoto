package com.example.restophoto;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
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
	List<ReviewProvider> reviewProviders = new LinkedList<ReviewProvider>();
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_pic);

		// Add review providers
        reviewProviders.add(new Qype());
              
        // Check GPS
        gps = new GPSTracker(getApplicationContext());
        checkGPS(gps);
		
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
	            // TODO now there is the image in Environment.getExternalStoragePublicDirectory(
				// Environment.DIRECTORY_PICTURES) + "photoresto.jpg"
	            // we can use location and photo (OCR + CV) to make API calls to various services 
	            // http://stackoverflow.com/questions/3505930/make-an-http-request-with-android
	            // and aggregate their results on the results.xml view/activity
	            // /TODO
	            //new RequestTask().execute("http://api.qype.com/v1/positions/48.842933,2.348576/places?consumer_key=EYGgS1vansn8b7DemMOw&radius=20");
	            
	            // Find location
	            checkGPS(gps);
	            double lat = gps.getLatitude(); 
	            double lon = gps.getLongitude();
	                  
	            // Retrieve match
	            List<Resto> match = new LinkedList<Resto>();
	            for (ReviewProvider provider : reviewProviders) {
	            	List<Resto> candidates = provider.getNearbyRestaurants(lat, lon);
	            	match.add(findBestMatch(candidates));
	            }
	            
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
	    	
    Resto findBestMatch(List<Resto> candidates) {
    	// TODO
    	return null;
	}	     	
}

