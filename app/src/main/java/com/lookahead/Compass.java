package com.lookahead;

import static android.hardware.SensorManager.SENSOR_ORIENTATION;
import static android.hardware.SensorManager.SENSOR_DELAY_UI;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_LOW;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM;
import static android.hardware.SensorManager.SENSOR_STATUS_UNRELIABLE;
import java.util.Locale;
import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;

public class Compass extends Activity implements SensorListener, OnClickListener,
TextToSpeech.OnInitListener {
	private SensorManager sensorMgr;
	private TextView  textview02;
	private Button calibrateButton;
	private float x;
    private TextToSpeech mTts;
	// deltas for calibration
	private long lastUpdate2 = -1;
	String tt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.mainlocator);

        textview02 = (TextView) findViewById(R.id.TextView02);
  //      zLabel = (TextView) findViewById(R.id.z_label);
   //     tv1    = (TextView) findViewById(R.id.TextView01);

        mTts = new TextToSpeech(this,
                this  // TextToSpeech.OnInitListener
                );
    }

	@Override
	protected void onPause() {
		super.onPause();

		sensorMgr.unregisterListener(this, SENSOR_ORIENTATION);
		sensorMgr = null;
	}

	@Override
	protected void onResume() {
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

	// from the android.hardware.SensorListener interface
	public void onAccuracyChanged(int sensor, int accuracy) {
  // TODO have not found anything yet
	}
	// from the android.hardware.SensorListener interface
	public void onSensorChanged(int sensor, float[] values) {
			long curTime = System.currentTimeMillis();
			// only allow one update every 100ms, otherwise updates
			// come way too fast and the phone gets blown away down
			// with garbage collection
	//        int j = (sensor == SensorManager.SENSOR_MAGNETIC_FIELD) ? 1 : 0;			
			if (lastUpdate2 == -1 || (curTime - lastUpdate2) > 4000) {
				lastUpdate2 = curTime;
				x = values[0]; // String.valueOf(x)
				textview02.setText(String.format("%.0f",x));
				}	}
	public void onClick(View v) {
		if (v == calibrateButton) {

		}
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
	      // Lanuage data is missing or the language is not supported.
	        }
	        } else {
	        	textview02.setText("Please switch-on or install text-to-speech");       	
	        }
	   }
	   
	   private void speakItFlush(String say_this, float rate, float pitcher){
			try {
				mTts.setPitch (pitcher); 
				mTts.setSpeechRate (rate);
				mTts.speak(say_this ,TextToSpeech.QUEUE_FLUSH,null);
				mTts.setPitch (1.0f); 
				mTts.setSpeechRate (1.0f);	     
			} catch(Exception e){
			//	mTts.speak("whoops. Something went wrong." ,TextToSpeech.QUEUE_ADD,null);
			}
		  }
	    @Override
	    public void onDestroy() {
	        // Don't forget to shutdown!
	        if (mTts != null) {
	   mTts.stop();
	   mTts.shutdown();
	        }
	        super.onDestroy();
	    }
	    
	    
}