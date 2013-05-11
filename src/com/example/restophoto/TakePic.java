package com.example.restophoto;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class TakePic extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pic);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.take_pic, menu);
        return true;
    }
    
    public void testButton(View view) {
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
    }
}
