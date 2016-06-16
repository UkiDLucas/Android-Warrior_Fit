package technology.uki.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ukilucas on 6/5/16.
 */
public class MotionSensors {

    private static final String TAG = MotionSensors.class.getSimpleName();
    private SensorManager sensorManager;

    // TODO this should probably be replaced with proper rounding, not just cutting of
    DecimalFormat print = new DecimalFormat("#.#"); // 0.1 accuracy should be plenty sufficient

    float gyroAccelerationLast;
    float gyroAccelerationCurrent;
    float gyroAcceleration;
    long millisecondsStart;

    //StringBuffer exercisePerformedData = new StringBuffer();

    Map<String, String> gyroscopeSensorData = new HashMap<>();
    String gyroSingleReading;

    float rotationX; // x acceleration
    float rotationY; // y acceleration
    float rotationZ; // z acceleration

    public MotionSensors(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void startTracking() {
        gyroscopeSensorData = new HashMap<>();
        //exercisePerformedData.delete(0, exercisePerformedData.length()); // reset to empty

        // Adding "|" pipe separators as they make parsing later very easy
        // exercisePerformedData.append("|milliseconds,rotationX,rotationY,rotationZ,rotationCombined|");
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
    public Map<String, String> stopTracking() {
        sensorManager.unregisterListener(sensorEventListener);
        //return exercisePerformedData.toString();
        return gyroscopeSensorData;
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
                    // combined acceleration = squareRoot (x^2 + y^2 + z^2)
                    gyroAccelerationCurrent = (float) Math.sqrt((double) (rotationX * rotationX + rotationY * rotationY + rotationZ * rotationZ));
                    float delta = gyroAccelerationCurrent - gyroAccelerationLast;
                    gyroAcceleration = gyroAcceleration * 0.9806f + delta; // gravity
                    long timePassed = System.currentTimeMillis() - millisecondsStart;
                    gyroSingleReading = print.format(rotationX) + "," + print.format(rotationY) + "," + print.format(rotationZ);
                    //exercisePerformedData.append(gyroSingleReading);  //TODO add other types of data
                    gyroscopeSensorData.put("gyro_" + timePassed, gyroSingleReading);
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
