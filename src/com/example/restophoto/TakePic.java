package com.example.restophoto;

import java.util.LinkedList;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class TakePic extends Activity {

	List<ReviewProvider> reviewProviders = new LinkedList<ReviewProvider>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pic);
        
        // Add review providers
        reviewProviders.add(new Qype());
        
        // Check GPS
        GPSTracker gps = new GPSTracker(getApplicationContext());
        checkGPS(gps);
        
        // Take photo
        // TODO
        
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
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.take_pic, menu);
        return true;
    }
    
    public void testButton(View view) {
    	/*
        GPSTracker gps = new GPSTracker(view.getContext());
        TextView text = (TextView) findViewById(R.id.textView1);
        if(gps.canGetLocation()){
        	double lat = gps.getLatitude(); 
        	double lon = gps.getLongitude();
        	text.setText(lat + " " + lon);
        }
        else {        	
        	text.setText("Pas de GPS motherfucker !");
        }
        */

    }
}
