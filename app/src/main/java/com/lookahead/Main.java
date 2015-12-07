package com.lookahead;

import static android.hardware.SensorManager.SENSOR_DELAY_UI;
import static android.hardware.SensorManager.SENSOR_ORIENTATION;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity implements LocationListener, SensorListener, OnClickListener, TextToSpeech.OnInitListener
{
	private LocationManager myManager;
	private TextView tv,  textview02, textview03, textview04;
	Button button03;
	Geocoder geocoder; // = new Geocoder(this);
    java.util.List<Address> addressList = null;
    Float Speed;
    String currentAddress = "";
    String final_sentence =" Signal not good ";
    private TextToSpeech mTts;
    String height = "";
    int county = 0;
    int count = 0;
    Timer timer = new Timer();  // in oncreate method
	private long lastUpdate = -1;
	String this_is_it;
	double Longitude, Latitude;	
	SharedPreferences preferences; 
	// compass
	private SensorManager sensorMgr;
	private long lastUpdate2 = -1;
	private float x;
	/************************************************************************** 
	 * View overrides below 
	 **************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainlocator);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
       
		mTts = new TextToSpeech(this,
                this  // TextToSpeech.OnInitListener
                );
		tv = (TextView) findViewById(R.id.TextView01);
		textview02 = (TextView) findViewById(R.id.TextView02);
		textview03 = (TextView) findViewById(R.id.TextView03);
		textview04 = (TextView) findViewById(R.id.TextView04);
		button03 = (Button)findViewById(R.id.Button03);
		button03.setOnClickListener(this);
		myManager = (LocationManager) getSystemService(LOCATION_SERVICE);	
		tv.setText("  ");
		
	    // geocoder stuff
	    geocoder = new Geocoder(this);	
	    
	    startListening(); // this was in on resume, trying it here

	}

    public void onInit(int status) {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will indicate this.
       //     int result = mTts.setLanguage(Locale.UK);
        	 mTts.setLanguage(Locale.UK);
        	 mTts.setSpeechRate (2.0f);	
         } else {
Toast.makeText(getApplicationContext(), "Please install or switch on text to speech", Toast.LENGTH_SHORT).show();
        }
      	mTts.speak("gps looking ahead in direction of you point phone " ,TextToSpeech.QUEUE_FLUSH,null);
    }  
	// compass
	public void onAccuracyChanged(int sensor, int accuracy) {
  //  have not found anything yet
	}
	// compass
	public void onSensorChanged(int sensor, float[] values) {
			long curTime = System.currentTimeMillis();
			// only allow one update every 100ms, otherwise updates
			// come way too fast and the phone gets blown away down
			// with garbage collection
	//        int j = (sensor == SensorManager.SENSOR_MAGNETIC_FIELD) ? 1 : 0;			
			if (lastUpdate2 == -1 || (curTime - lastUpdate2) > 7000) {
				lastUpdate2 = curTime;
				x = values[0]; // String.valueOf(x)
			//	mTts.speak(String.format("%.0f",x) ,TextToSpeech.QUEUE_FLUSH,null);
				textview02.setText(String.format("%.0f",x));
// stick n ne w here
				if(x>337.5 || x<22.5){
					mTts.speak(" North " + String.format("%.0f",x) ,TextToSpeech.QUEUE_ADD,null);
					textview02.setText( " North " +String.format("%.0f",x));
				}
					if(x>22.5 && x<67.5){
						mTts.speak(" North East " + String.format("%.0f",x)  ,TextToSpeech.QUEUE_ADD,null);
						textview02.setText(String.format(" North East " +String.format("%.0f",x)));
				}
				if(x>67.5 && x<112.5){
					mTts.speak(" East " + String.format("%.0f",x)  ,TextToSpeech.QUEUE_ADD,null);
					textview02.setText(String.format(" East " +String.format("%.0f",x)));
					}
				if(x>112.5 && x<157.5){
					mTts.speak(" South East " + String.format("%.0f",x)  ,TextToSpeech.QUEUE_ADD,null);
					textview02.setText(String.format(" South East " +String.format("%.0f",x)));
					} 		
				if(x>157.5 && x<202.5){
					mTts.speak(" South " + String.format("%.0f",x) ,TextToSpeech.QUEUE_ADD,null);
					textview02.setText(String.format(" South " +String.format("%.0f",x)));
					} 				
				if(x>202.5 && x<247.5){
					mTts.speak(" South West " + String.format("%.0f",x)  ,TextToSpeech.QUEUE_ADD,null);
					textview02.setText(String.format(" South West " +String.format("%.0f",x)));
					} 
				if(x>247.5 && x<292.5){
					mTts.speak(" West " + String.format("%.0f",x)  ,TextToSpeech.QUEUE_ADD,null);
					textview02.setText(String.format(" West " +String.format("%.0f",x)));
					}
				if(x>292.5 && x<337.5){
					mTts.speak(" North West " + String.format("%.0f",x)  ,TextToSpeech.QUEUE_ADD,null);
					textview02.setText(String.format(" North West " +String.format("%.0f",x)));
					} 
				}				
				
				
// end n e nw speak
				
				
				
				
				
				}	
	
     
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	protected void onStop() {
		timer.cancel();
		stopListening();
		finish();  // added this to try to stop repeat locators activities
		super.onStop();
	}	
	@Override
	protected void onPause() {
		stopListening();
		finish(); // added this to try to stop repeat locators activities
		super.onPause();
		sensorMgr.unregisterListener(this, SENSOR_ORIENTATION);
		sensorMgr = null;
	}
	@Override
	protected void onResume() {
	//	startListening();
//		tv.setText(" testing onResume");
		super.onResume();
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		boolean magneticSupported = sensorMgr.registerListener(this, 
				SENSOR_ORIENTATION,
				SENSOR_DELAY_UI);
		if (!magneticSupported) {
			// on compass on this device
			sensorMgr.unregisterListener(this, SENSOR_ORIENTATION);

		}
	}
    public void onClick(View v) {                 // method on its own
        if (v==button03){
	    //	textview03.setText("button 2 clicked");
	    	stopListening();
	    	startListening3();
	    }
}    
	/**************************************************************************
	 *  helper functions for starting/stopping monitoring of GPS changes below 
	 **************************************************************************/
	private void startListening() {
		myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
  
	}
	private void startListening3() {
        Intent myIntent = new Intent(this,Coarse.class);
        startActivityForResult(myIntent, 0);
        finish();
	}	  
	private void stopListening() {
		if (myManager != null)
			myManager.removeUpdates(this);
	}
	/**************************************************************************
	 * LocationListener overrides below 
	 **************************************************************************/
	@Override
	public void onLocationChanged(Location location)  {
try {
		long curTime = System.currentTimeMillis();
		// only allow one update every 100ms, otherwise updates
		// come way too fast and the phone gets blown away down
		// with garbage collection
//        int j = (sensor == SensorManager.SENSOR_MAGNETIC_FIELD) ? 1 : 0;			
		if (lastUpdate == -1 || (curTime - lastUpdate) > 12000) { // 
			lastUpdate = curTime  ;

		String s = " ";

		try {
		Speed = location.getSpeed(); // getAccuracy();  // float
		Longitude = location.getLongitude(); // double
		Latitude = location.getLatitude();   // double
		} catch(Exception e) {
			tv.setText("location");	
			// doSomeShit(51.52628660, -0.088346600 );
		    Intent intent = new android.content.Intent(); // think this may be starting another clone
		    intent.setClass(this, this.getClass()); 
		    this.startActivity(intent);
		    finish();
			}
		if(Longitude!=0.00000 & Latitude!=0.00000){
// 		TODO
//		Time = location.getTime();
//		s += "\tTime: "      + String.valueOf(Time)  + "\n";
		s += "\tLatitude:  " + String.valueOf(Latitude)  + "\n";
		s += "\tLongitude: " + String.valueOf(Longitude) + "\n";
//		s += "\tAccuracy:  " + String.valueOf(Accuracy) + "\n";
		tv.setText(s);
		x = roundBearing(x);
	//	mTts.speak(String.valueOf(x) ,TextToSpeech.QUEUE_ADD,null);
		Latitude =  targetLat(Latitude, x);
		Longitude = targetLong(Longitude, x);
   Toast.makeText(getApplicationContext(), "Latitude" + String.valueOf(Latitude) , Toast.LENGTH_SHORT).show();
   textview04.setText("lat is " + Latitude + "/n long is " + Longitude+" /n Bearing is"+ x );	
   doSomeShit(Latitude, Longitude );
		} // end of if lat and  long != 0
		else {
		// lat and long did = 0, go round again
			tv.setText("lat lon 0");	
			// doSomeShit(51.52628660, -0.088346600 );
		    Intent intent = new android.content.Intent(); // think this may be starting another clone
		    intent.setClass(this, this.getClass()); 
		    this.startActivity(intent);
		    finish();			
		}
		} 
} catch(Exception ex){
	this.onCreate(null);
	tv.setText("the lot");	
}
}

	
	
	private double round(double d) {
 	   DecimalFormat twoDForm = new DecimalFormat("#.#####");
	return Double.valueOf(twoDForm.format(d));
}
// "00.E00"
private float roundBearing(float b) {
	   DecimalFormat twoDForm = new DecimalFormat("000.E00");
return Float.valueOf(twoDForm.format(b));
} 

private double deg2rad(double deg) {
	  return (deg * Math.PI / 180.0);
	}
	private double rad2deg(double rad) {
	  return (rad * 180.0 / Math.PI);
	}
private double targetLat(double Lat, Float bearing){
	 double targLat = 0.0005*Math.cos(deg2rad(bearing)) + Lat ;
	 return targLat;
}
private double targetLong(double Long, Float bearing){
	 double targLat = 0.0005*Math.sin(deg2rad(bearing)) + Long ;
	 return targLat;
}		
	private  synchronized void doSomeShit(double Latitude, double Longitude){
   	try {
		addressList = geocoder.getFromLocation(Latitude, Longitude, 1); 
	} catch (Exception e) {
		tv.setText("geocoder");
	    Intent intent = new android.content.Intent();
	    intent.setClass(this, this.getClass()); 
	    this.startActivity(intent);
	    finish();
//		System.exit(0);
			} 
// the meat
        if(!addressList.isEmpty()) {
	    currentAddress = new String();
	    currentAddress = addressList.get(0).getAddressLine(0) + "\n "
          + addressList.get(0).getAddressLine(1) + "\n ";
	    final_sentence = currentAddress ;
	 //   textview03.setText(final_sentence);
	    final_sentence.replace("St", "Street");
    	this_is_it = final_sentence.replace("St", "Street,").replace("Ln", "Lane,").replace("Dr", "Drive,").replace("Rd", "Road,").replace("Ave", "Avenue,").replace("Pl", "Place,");
  //    	this_is_it.replace("St", "Street").replace("Rd", "Road").replace("Av", "Avenue").replace("Pl", "Place");
	    textview03.setText(this_is_it+" "+Speed);  
	    mTts.speak("gps looking ahead "+ this_is_it ,TextToSpeech.QUEUE_FLUSH,null);
	  //  sayTheTime();
 
        } else {    
    		tv.setText("al");  
    	    Intent intent = new android.content.Intent(); // think this may be starting another clone
    	    intent.setClass(this, this.getClass()); 
    	    this.startActivity(intent);
    	    finish();
        }
	}

	@Override
	public void onProviderDisabled(String provider) {
mTts.speak("Your GPS is switched off in settings, or you do not have it" ,TextToSpeech.QUEUE_ADD,null);
textview03.setText("Your GPS is switched off in settings, or you do not have it");	
	}

	@Override
	public void onProviderEnabled(String provider) {
		textview03.setText("onProviderEnabled fired");			
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		textview03.setText("onStatusChanged fired");	
	}
	
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        super.onCreateOptionsMenu(menu);
	       MenuInflater inflater = getMenuInflater();  
	       inflater.inflate(R.menu.menu, menu);
	        return true;
	    }
	    /**
	     * Called right before your activity's option menu is displayed.
	     */
	    @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {  
	  
	        return true;  
	    } 
	    /**
	     * Called when a menu item is selected.
	     */
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item){  
	        switch (item.getItemId()) {  
	        case R.id.preferences:  
	            Intent i = new Intent(Main.this, PreferencesFromXml.class);  
	            startActivity(i);  
	            break;  
	        case R.id.exit:
	    		finish();
	            break;
	        case R.id.email:
	        if(this_is_it!=null){
	 String emailColl = preferences.getString("edittext_preference"," ");
	Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+emailColl));
	intent.putExtra("subject", "My location is " + this_is_it);
	intent.putExtra("body", "My location is " + this_is_it+" My latitude is "+String.valueOf(Latitude)
	+" My longitude is "+String.valueOf(Longitude)+" \n "
	+" My google maps link is: http://maps.google.com/maps?q="+Latitude+",+"+Longitude+" ");
	startActivity(intent); 
	        } else {
 Toast.makeText(getApplicationContext(), "Did not get a fix ", Toast.LENGTH_SHORT).show();
 mTts.speak(" Did not get a fix yet ",TextToSpeech.QUEUE_ADD,null);// Drop all pending entries in the playback queue.
	        	
	        }
	        break;
	        case R.id.sms:
	        	if(this_is_it!=null){
	    String smsColl = preferences.getString("edittext_sms"," ");
	    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
	    sendIntent.putExtra("sms_body", "To "+smsColl+" My location is " + this_is_it+
	    " My latitude / longitude is "+String.valueOf(Latitude) + " / " + String.valueOf(Longitude));
	    sendIntent.setType("vnd.android-dir/mms-sms");
	    startActivity(sendIntent); 
	     } else {
	 Toast.makeText(getApplicationContext(), "Did not get a fix ", Toast.LENGTH_SHORT).show();
	 mTts.speak(" Did not get a fix yet ",TextToSpeech.QUEUE_ADD,null);// Drop all pending entries in the playback queue.
	        			        		        }
	             break;	        
	    		
	        case R.id.refresh:
	        	this.onCreate(null);
	    		break;
	        }  
	        return true; 
	       
	    }	   
}