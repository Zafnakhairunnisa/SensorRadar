package com.example.sensorradar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;

    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;

    private TextView mTextSensorAzimuth, mTextSensoPitch, mTextSensrRoll;

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];

    private ImageView mSpotTop, mSpotLeft, mSpotRight, mSpotBottom;

    private static final float VALUE_DRIFT = 0.05f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mTextSensorAzimuth = findViewById(R.id.value_azimuth);
        mTextSensrRoll = findViewById(R.id.value_roll);
        mTextSensoPitch = findViewById(R.id.value_pitch);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSpotTop = findViewById(R.id.spot_top);
        mSpotBottom = findViewById(R.id.spot_bottom);
        mSpotRight = findViewById(R.id.spot_right);
        mSpotLeft = findViewById(R.id.spot_left);
    }

    protected void onStart() {
        super.onStart();
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = sensorEvent.values.clone();
                break;
            default:
                return;
        }

        float [] rotationMatrix = new float[9];
        boolean rotationOk = SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerData, mMagnetometerData);
        float orientationValues [] = new float[3];
        if (rotationOk) {
            SensorManager.getOrientation(rotationMatrix, orientationValues);
        }

        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        mTextSensorAzimuth.setText(getResources().getString(R.string.value_format, azimuth));
        mTextSensoPitch.setText(getResources().getString(R.string.value_format, pitch));
        mTextSensrRoll.setText(getResources().getString(R.string.value_format, roll));

        if (Math.abs(pitch) < VALUE_DRIFT) {
            pitch = 0;
        }
        if (Math.abs(roll) < VALUE_DRIFT) {
            roll = 0;
        }

        mSpotRight.setAlpha(0.0f);
        mSpotTop.setAlpha(0.0f);
        mSpotRight.setAlpha(0.0f);
        mSpotBottom.setAlpha(0.0f);

        if (pitch > 0) {
            mSpotBottom.setAlpha(pitch);
        }
        else {
            mSpotTop.setAlpha(Math.abs(pitch));
        }
        if (roll > 0) {
            mSpotLeft.setAlpha(roll);
        }
        else {
            mSpotRight.setAlpha(Math.abs(roll));
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}