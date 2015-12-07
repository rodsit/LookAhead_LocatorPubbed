package com.lookahead;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class Coarse extends Activity implements LocationListener, OnClickListener, TextToSpeech.OnInitListener
{
	private LocationManager myManager;
	private TextView tv,  textview02, textview03;
	Button button03;
	Geocoder geocoder; 
    java.util.List<Address> addressList = null;
    Float Accuracy;
    String currentAddress = "";
    String final_sentence =" Signal not good ";
    private TextToSpeech mTts;
    String height = "";
    int county = 0;
    int count;
    Timer timer = new Timer();  // in oncreate method
	private long lastUpdate = -1;
	String this_is_it;
	double Longitude, Latitude;
	SharedPreferences preferences; 

	/************************************************************************** 
	 * View overrides below 
	 **************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainlocatorcoarse);
       
		mTts = new TextToSpeech(this,
                this  // TextToSpeech.OnInitListener
                );
		tv = (TextView) findViewById(R.id.TextView01);
		textview02 = (TextView) findViewById(R.id.TextView02);
		textview03 = (TextView) findViewById(R.id.TextView03);
     	button03 = (Button)findViewById(R.id.Button03);
		button03.setOnClickListener(this);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);			

		myManager = (LocationManager) getSystemService(LOCATION_SERVICE);	
		tv.setText("  ");
		
	    // geocoder stuff
	    geocoder = new Geocoder(this);	
	    
	    startListening(); // this was in on resume, trying it here

	//	timer.schedule(new ScheduledTaskWithHandeler(), 40000); // IF IT TAKES TOO LONG do sthg!
	}

	public void onInit(int status) {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will indicate this.
            int result = mTts.setLanguage(Locale.UK);
            // Try this someday for some interesting results.
            // int result mTts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getApplicationContext(), "You do not support the language UK english)", Toast.LENGTH_SHORT).show();
                result = mTts.setLanguage(Locale.US);
        }
         } else {
Toast.makeText(getApplicationContext(), "Please install or switch on text to speech", Toast.LENGTH_SHORT).show();
        }
      	mTts.speak("Why-Fy , Mobile " ,TextToSpeech.QUEUE_FLUSH,null);
    }  

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
  // no message 
        }
     };
     class ScheduledTaskWithHandeler extends TimerTask {
    @Override
    public void run() {
            handler.sendEmptyMessage(0);
                  
                }

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
	}
	@Override
	protected void onResume() {
	//	startListening();
//		tv.setText(" testing onResume");
		super.onResume();
	}
    public void onClick(View v) {                 // method on its own
      if (v==button03){
	    	stopListening();
	    	startListening3();
	    }
}
	/**************************************************************************
	 *  helper functions for starting/stopping monitoring of GPS changes below 
	 **************************************************************************/
	private void startListening() {
		myManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	}
	private void startListening3() {
        Intent myIntent = new Intent(this,Main.class);
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
	public void onLocationChanged(Location location) { synchronized(this)  {
		long curTime = System.currentTimeMillis();
		// only allow one update every 100ms, otherwise updates
		// come way too fast and the phone gets blown away down
		// with garbage collection
//        int j = (sensor == SensorManager.SENSOR_MAGNETIC_FIELD) ? 1 : 0;			
		if (lastUpdate == -1 || (curTime - lastUpdate) > 12000) { // TODO
			lastUpdate = curTime;

		String s = " ";
		try {
//		Accuracy = location.getAccuracy();  // float
		 Longitude = location.getLongitude(); // double
		 Latitude = location.getLatitude();   // double
//		Time = location.getTime();
//		s += "\tTime: "      + String.valueOf(Time)  + "\n";
		s += "\tLatitude:  " + String.valueOf(Latitude)  + "\n";
		s += "\tLongitude: " + String.valueOf(Longitude) + "\n";
//		s += "\tAccuracy:  " + String.valueOf(Accuracy) + "\n";
		tv.setText(s);
		doSomeShit(Latitude, Longitude );
		} catch(Exception e) {
		tv.setText("location");	// at least it goes round and round until success
		// doSomeShit(51.52628660, -0.088346600 );
		
	    Intent intent = new android.content.Intent();
	    intent.setClass(this, this.getClass()); 
	    this.startActivity(intent);
	    finish();
		}
		} }}
	
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
	    mTts.speak("Your approximate location by Why Fi is "+ this_is_it ,TextToSpeech.QUEUE_ADD,null);
	    textview03.setText("Your approximate location by WiFi is "+ this_is_it);
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
mTts.speak("You do not have 'use Wireless networks' switched on in phone settings" ,TextToSpeech.QUEUE_ADD,null);
textview03.setText("You do not have 'use Wireless networks' switched on in phone settings");	
	}

	@Override
	public void onProviderEnabled(String provider) {
		textview03.setText("onProviderEnabled fired");			
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		textview03.setText("onStatusChanged fired");	
	}
	
	   private void sayTheTime(){
	     	Calendar calendar = new GregorianCalendar();
	        String am_pm;
	        int hour = calendar.get(Calendar.HOUR);
	        int minute = calendar.get(Calendar.MINUTE);
	        int second = calendar.get(Calendar.SECOND);
	        if(calendar.get(Calendar.AM_PM) == 0)
	          am_pm = "AM";
	        else
	          am_pm = "PM"; 
 mTts.speak(hour + "  "   + minute + " and " + second + " seconds " + am_pm,TextToSpeech.QUEUE_ADD,null);// Drop all pending entries in the playback queue.
 
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
	            Intent i = new Intent(Coarse.this, PreferencesFromXml.class);  
	            startActivity(i);  
	            break;  
	        case R.id.exit:
	    		finish();
	            break;
	        case R.id.email:
	        if(this_is_it!=null){
	       	String emailColl = preferences.getString("edittext_preference"," ");
	     	Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+emailColl));
	      	intent.putExtra("subject", "My approximate location is " + this_is_it);
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
	    sendIntent.putExtra("sms_body", "To "+smsColl+" My approximate location is " + this_is_it+
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
