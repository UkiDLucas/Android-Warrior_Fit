package technology.uki.sensors;

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

    float gyroAccelerationLast;
    float gyroAccelerationCurrent;
    float gyroAcceleration;
    long millisecondsStart;

    StringBuffer gyroData = new StringBuffer();
    String gyroSingleReading;

    float rotationX; // x acceleration
    float rotationY; // y acceleration
    float rotationZ; // z acceleration

    public MotionSensors(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void startTracking() {
        gyroData.delete(0, gyroData.length()); // reset to empty

        // Adding "|" pipe separators as they make parsing later very easy
        gyroData.append("|milliseconds,rotationX,rotationY,rotationZ,rotationCombined|");
        millisecondsStart = System.currentTimeMillis();

        /**
         *   SENSOR_DELAY_NORMAL about every 199 milliseconds (1/5s)
         *   this might do fine for counting repetitions,
         *   but probably not enough resolution to recognize correct exercise.
         *
         *   SENSOR_DELAY_UI about every 67 milliseconds (1/15s)
         *
         *   SENSOR_DELAY_GAME about every 20 milliseconds (1/50s)
         *   it might be slightly too fast for our needs
         */
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_UI);

    }

    /**
     * Returns the String of readings representing whole exercise session.
     *
     * @return
     */
    public String stopTracking() {
        sensorManager.unregisterListener(sensorEventListener);
        return gyroData.toString(); //TODO add other types of data
    }

    /**
     * read: https://developer.android.com/reference/android/hardware/SensorEvent.html#values
     */
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {

            switch (event.sensor.getType()) {

                case Sensor.TYPE_GYROSCOPE: // rotational acceleration around X,Y,Z

                    rotationX = event.values[0];
                    rotationY = event.values[1];
                    rotationZ = event.values[2];

                    gyroAccelerationLast = gyroAccelerationCurrent;
                    gyroAccelerationCurrent = (float) Math.sqrt((double) (rotationX * rotationX + rotationY * rotationY + rotationZ * rotationZ));
                    float delta = gyroAccelerationCurrent - gyroAccelerationLast;
                    gyroAcceleration = gyroAcceleration * 0.9f + delta; // perform low-cut filter
                    long timePassed = System.currentTimeMillis() - millisecondsStart;
                    gyroSingleReading = "|" + timePassed + "," + rotationX + "," + rotationY + "," + rotationZ + "," + gyroAcceleration + "|";
                    gyroData.append(gyroSingleReading);
                    Log.d(TAG, "onSensorChanged  Sensor.TYPE_GYROSCOPE gyroAcceleration " + gyroSingleReading);
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
}
