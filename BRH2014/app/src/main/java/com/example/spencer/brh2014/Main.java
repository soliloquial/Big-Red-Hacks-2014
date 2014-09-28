package com.example.spencer.brh2014;

import com.example.spencer.brh2014.ShakeDetector;
import com.example.spencer.brh2014.ShakeDetector.OnShakeListener;

import com.google.vrtoolkit.cardboard.sensors.MagnetSensor;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.spencer.brh2014.ShakeDetector.OnShakeListener;


public class Main extends Activity implements OnShakeListener,  {

    private Camera mCamera;
    private CameraPreview mPreview;
    private ShakeDetector shakeDetector;
    private MagnetSensor magnetometer;
    public static String lang = "de";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instance of Camera
        mCamera = getCameraInstance();

       // Create a shake detector
     	shakeDetector = new ShakeDetector(this);

	// Create a magnetometer sensor
	magnetometer = new MagnetSensor(this);
	magnetometer.setOnCardboardTriggerListener(
            new MagnetSensor.OnCardboardTriggerListener {
                @Override
                public void onCardboardTrigger() {
                    mPreview.handleClick(Main.this);
                }
            });
	magnetometer.start();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera, (TextView) findViewById(R.id.textview), (TextView) findViewById(R.id.textview2));
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreview.handleClick(Main.this);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
	public void onShake() {
		Toast.makeText(this, "shake detected", Toast.LENGTH_SHORT).show();
	}
}
