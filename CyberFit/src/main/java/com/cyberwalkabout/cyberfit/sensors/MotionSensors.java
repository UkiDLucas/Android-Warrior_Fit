package com.cyberwalkabout.cyberfit.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by ukilucas on 6/5/16.
 */
public class MotionSensors {

    private static final String TAG = MotionSensors.class.getSimpleName();
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gravity;
    private Sensor gyroscope;

    float gyroAccelerationLast;
    float gyroAccelerationCurrent;
    float gyroAcceleration;

    public MotionSensors(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
//        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void startTracking() {
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL); // TODO in the future consider SENSOR_DELAY_GAME
    }

    public void stopTracking() {
        sensorManager.unregisterListener(sensorEventListener);
    }


    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {

            switch (event.sensor.getType()) {
                case Sensor.TYPE_GYROSCOPE:
                    Log.d(TAG, "onSensorChanged  Sensor.TYPE_GYROSCOPE " + event);

                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];
                    gyroAccelerationLast = gyroAccelerationCurrent;
                    gyroAccelerationCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
                    float delta = gyroAccelerationCurrent - gyroAccelerationLast;
                    gyroAcceleration = gyroAcceleration * 0.9f + delta; // perform low-cut filter

                    Log.d(TAG, "onSensorChanged  Sensor.TYPE_GYROSCOPE gyroAcceleration " + gyroAcceleration);
                    updateOrientation(x, y, z);
                    break;
                case Sensor.TYPE_GRAVITY:
                    Log.d(TAG, "onSensorChanged  Sensor.TYPE_GRAVITY ");
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    Log.d(TAG, "onSensorChanged  Sensor.TYPE_ACCELEROMETER ");
                    break;
                default:
                    Log.d(TAG, "onSensorChanged  event.sensor.getType() ");
                    break;
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    private void updateOrientation(float heading, float pitch, float roll) {
        // Update the UI
    }


    public void onSensorChanged(SensorEvent event) {
    }

    // In this example, alpha is calculated as t / (t + dT),
    // where t is the low-pass filter's time-constant and
    // dT is the event delivery rate.

//        final float alpha = 0.8;
//
//        // Isolate the force of gravity with the low-pass filter.
//        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
//        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
//        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
//
//        // Remove the gravity contribution with the high-pass filter.
//        linear_acceleration[0] = event.values[0] - gravity[0];
//        linear_acceleration[1] = event.values[1] - gravity[1];
//        linear_acceleration[2] = event.values[2] - gravity[2];
//}


}
