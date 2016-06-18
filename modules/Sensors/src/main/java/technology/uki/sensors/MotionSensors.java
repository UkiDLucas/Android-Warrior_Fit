package technology.uki.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by ukilucas on 6/5/16.
 */
public class MotionSensors {

    private static final String TAG = MotionSensors.class.getSimpleName();
    private SensorManager sensorManager;

    long millisecondsStart;

    Map<String, String> sensorData = null;
    String gyroSingleReading; // keeping as class member to avoid creation 10 times per second

    // Gyroscope rotational acceleration around axis
    // keeping as class members to avoid creation 10 times per second
    float gyroX;
    float gyroY;
    float gyroZ;

    public MotionSensors(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void startTracking() {
        sensorData = new TreeMap<String, String>();
        //exercisePerformedData.delete(0, exercisePerformedData.length()); // reset to empty

        // Adding "|" pipe separators as they make parsing later very easy
        // exercisePerformedData.append("|milliseconds,gyroX,gyroY,gyroZ,rotationCombined|");
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
                SensorManager.SENSOR_DELAY_GAME);

    }


    /**
     * Returns the String of readings representing whole exercise session.
     *
     * @return
     */
    public Map<String, String> stopTracking() {
        sensorManager.unregisterListener(sensorEventListener);
        //return exercisePerformedData.toString();
        return sensorData;
    }


    /**
     * read: https://developer.android.com/reference/android/hardware/SensorEvent.html#values
     */
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {

            switch (event.sensor.getType()) {

                case Sensor.TYPE_GYROSCOPE: // rotational acceleration around X,Y,Z

                    // Decided to round accelaration to whole integers,
                    // as more granularity is not needed,
                    // or even detrimental
                    gyroX = Math.round(event.values[0]);
                    gyroY = Math.round(event.values[1]);
                    gyroZ = Math.round(event.values[2]);

                    long timePassed = System.currentTimeMillis() - millisecondsStart; // loop optimize
                    gyroSingleReading = gyroX + "," + gyroY + "," + gyroZ + "," + absSum(gyroX, gyroY, gyroZ);
                    sensorData.put("gyro_" + interval(timePassed), gyroSingleReading);
                    Log.d(TAG, "onSensorChanged  Sensor.TYPE_GYROSCOPE gyroAcceleration " + gyroSingleReading);
                    break;
                case Sensor.TYPE_GRAVITY:
                    Log.d(TAG, "onSensorChanged  Sensor.TYPE_GRAVITY ");
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    Log.d(TAG, "onSensorChanged  Sensor.TYPE_ACCELEROMETER x=" + event.values[0] + " y=" + event.values[1] + " z=" + event.values[2]);
                    break;
                default:
                    Log.d(TAG, "onSensorChanged  event.sensor.getType() ");
                    break;
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    String absSum(float x, float y, float z) {
        float sum = Math.abs(x) + Math.abs(y) + Math.abs(z);
//        if (sum == 0)
//            return "split";
//        else
        return String.valueOf(sum);
    }


    int timeInterval = 100; // we will report vectors in 0.1 intervals, not the real time

    //DecimalFormat integerFormatter = new DecimalFormat("#");

    /**
     * We will measure exercise intervals in 1/10 of a second
     * e.g. 357 millisecons/100 = 3.57 rounding -> 4  or 4/10 second
     *
     * @param millisecounds
     * @return
     */
    private String interval(long millisecounds) {
        String output = String.valueOf(Math.round(millisecounds / 100)); // see JavaDoc

        while (output.length() < 3) { // 3 zeros allow us for 999.9 seconds, Flurry will limit this
            // prepend zeros to help with sorting e.g. 0045 before 0041
            output = "0" + output;
        }
        return output;
    }

    // DecimalFormat oneDecimalAfterFormatter = new DecimalFormat("#.#"); // 0.1 accuracy
    // combined acceleration not used at this time
    // gyroAccelerationLast = gyroAccelerationCurrent;
    // combined acceleration = squareRoot (x^2 + y^2 + z^2)
    // gyroAccelerationCurrent = (float) Math.sqrt((double) (gyroX * gyroX + gyroY * gyroY + gyroZ * gyroZ));
    // float delta = gyroAccelerationCurrent - gyroAccelerationLast;
    // gyroAcceleration = gyroAcceleration * 0.9806f + delta; // gravity

}
