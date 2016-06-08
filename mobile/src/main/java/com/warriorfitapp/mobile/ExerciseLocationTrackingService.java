package com.warriorfitapp.mobile;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.warriorfitapp.db.sqlite.schema.table.ExerciseSessionTable;
import com.warriorfitapp.mobile.content.ContentProviderAdapter;
import com.warriorfitapp.mobile.content.UriHelper;
import com.warriorfitapp.mobile.flurry.FlurryAdapter;
import com.warriorfitapp.mobile.model.v2.factory.LocationInfoAndroidLocationFactory;
import com.warriorfitapp.mobile.util.Const;
import com.warriorfitapp.mobile.util.ConvertUtils;
import com.warriorfitapp.model.v2.Exercise;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Maria Dzyokh, Andrii Kovalov
 */
public class ExerciseLocationTrackingService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = ExerciseLocationTrackingService.class.getSimpleName();

    public static final String ACTION_START = ExerciseLocationTrackingService.class.getName() + ".ACTION_START";
    public static final String ACTION_PAUSE = ExerciseLocationTrackingService.class.getName() + ".ACTION_PAUSE";
    public static final String ACTION_RESUME = ExerciseLocationTrackingService.class.getName() + ".ACTION_RESUME";
    public static final String ACTION_FINISH = ExerciseLocationTrackingService.class.getName() + ".ACTION_FINISH";

    private static final int LOCATION_MAX_AGE = 60000; // ms
    private static final int LOCATION_GOOD_ACCURACY = 30; // meters
    public static final int NOTIFICATION_ID = 42;

    private GoogleApiClient mGoogleApiClient;

    private volatile Location lastLocation;

    private ContentProviderAdapter contentProviderAdapter = ContentProviderAdapter.getInstance();

    private LocationInfoAndroidLocationFactory locationFactory = LocationInfoAndroidLocationFactory.getInstance();

    private float[] distanceHolder = new float[1];

    private ExecutorService executor = Executors.newFixedThreadPool(2);

    private Handler handler = new Handler(Looper.getMainLooper());

    private Lock lock = new ReentrantLock();
    private Condition hasLocation = lock.newCondition();
    private Condition isConnected = lock.newCondition();

    private static final LocationRequest PERIODIC_LOCATION_REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(3000)    // 3 seconds
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // request most accurate locations available
            .setSmallestDisplacement(10); // 10 meters

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, getClass().getSimpleName() + " created");
        setUpLocationClientIfNeeded();
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        if (executor != null) {
            executor.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction() != null) {
                String action = intent.getAction();

                if (ACTION_START.equals(action)) {
                    startExercise(intent);
                } else if (ACTION_PAUSE.equals(action)) {
                    pauseExercise(intent);
                } else if (ACTION_RESUME.equals(action)) {
                    resumeExercise(intent);
                } else if (ACTION_FINISH.equals(action)) {
                    finishExercise(intent);
                }
            } else {
                // if no action provided, assuming that activity tries to recreate service because there
                restoreExercise(intent);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void restoreExercise(Intent intent) {
        final String exerciseId = intent.getStringExtra(Const.EXERCISE_ID);
        new AsyncTask<Void, Void, Exercise>() {

            @Override
            protected Exercise doInBackground(Void... params) {
                com.warriorfitapp.model.v2.ExerciseSession exerciseSession = contentProviderAdapter.getInProgressExerciseHistoryRecord(getApplicationContext(), exerciseId);
                if (exerciseSession != null) {
                    return contentProviderAdapter.getExerciseById(getApplicationContext(), exerciseId);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exercise exercise) {
                if (exercise != null) {
                    startAsForeground(exercise);
                }
            }
        }.executeOnExecutor(executor);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to location client");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, PERIODIC_LOCATION_REQUEST, this);

        signalConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended(" + i + ")");
    }

    private void signalConnected() {
        try {
            lock.tryLock(1, TimeUnit.SECONDS);
            isConnected.signalAll();
        } catch (InterruptedException ignore) {
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to location client");
        // TODO: any retries
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopIfNoMapExercisesInProgress() {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                return contentProviderAdapter.countMapExercisesInProgress(getApplicationContext());
            }

            @Override
            protected void onPostExecute(Integer count) {
                Log.d(TAG, "There " + count + " map exercises in progress");
                if (count == null || count == 0) {
                    stopForeground(true);
                    stopSelf();
                    Log.d(TAG, "Stopping service");
                }
            }
        }.executeOnExecutor(executor);
    }

    private void startAsForeground(Exercise exercise) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        notificationBuilder.setSmallIcon(R.drawable.ic_map_exercise_notification);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_app_launcher));
        notificationBuilder.setContentTitle(getString(R.string.app_name));
        notificationBuilder.setContentText(exercise.getName());

        Intent openMapIntent = new Intent(this, MapExerciseScreen.class);
        openMapIntent.putExtra(Const.EXERCISE, Parcels.wrap(exercise));
        notificationBuilder.setContentIntent(PendingIntent.getActivity(this, (int) System.currentTimeMillis(), openMapIntent, 0));

        // TODO: add control buttons like Finish/Pause/Resume etc...
        //Intent intent = new Intent(this, ExerciseLocationTrackingService.class);
        //PendingIntent pIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(), intent, 0);
        //notificationBuilder.addAction(R.drawable.ic_action_done, getString(R.string.finish), pIntent);

        startForeground(NOTIFICATION_ID, notificationBuilder.build());

        Log.d(TAG, "Display service notification");
    }

    private void setUpLocationClientIfNeeded() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
            Log.d(TAG, "Connecting to google api client...");
            mGoogleApiClient.connect();
        }
    }

    private LocationQuality detectLocationQuality(Location location) {
        if (location == null) return LocationQuality.BAD;
        if (!location.hasAccuracy()) return LocationQuality.BAD;
        long currentTime = System.currentTimeMillis();
        if (currentTime - location.getTime() < LOCATION_MAX_AGE && location.getAccuracy() <= LOCATION_GOOD_ACCURACY)
            return LocationQuality.GOOD;
        return LocationQuality.BAD;
    }

    private void startExercise(final Intent intent) {
        final long timestamp = System.currentTimeMillis();

        final String exerciseId = intent.getStringExtra(Const.EXERCISE_ID);

        Log.d(TAG, "Start exercise [exerciseId=" + exerciseId + "]");

        if (!TextUtils.isEmpty(exerciseId)) {
            new AsyncTask<Void, Void, com.warriorfitapp.model.v2.ExerciseSession>() {
                @Override
                protected com.warriorfitapp.model.v2.ExerciseSession doInBackground(Void... params) {
                    waitForFreshLocation();

                    com.warriorfitapp.model.v2.ExerciseSession exerciseSession = contentProviderAdapter.getInProgressExerciseHistoryRecord(getApplicationContext(), exerciseId);

                    if (lastLocation != null) {
                        if (exerciseSession == null) {
                            exerciseSession = new com.warriorfitapp.model.v2.ExerciseSession();
                            exerciseSession.setTime(0L);
                            exerciseSession.setDistance(0D);
                        }

                        exerciseSession.setExerciseId(exerciseId);
                        exerciseSession.setState(com.warriorfitapp.model.v2.ExerciseState.STARTED);

                        exerciseSession.setTimestampStarted(timestamp);
                        exerciseSession.setLastTimestampStarted(timestamp);

                        contentProviderAdapter.updateExerciseSession(getApplicationContext(), exerciseSession, false);

                        com.warriorfitapp.model.v2.LocationInfo locationInfo = locationFactory.create(lastLocation);
                        locationInfo.setExerciseHistoryRecordId(exerciseSession.getId());
                        locationInfo.setType(com.warriorfitapp.model.v2.LocationInfo.LocationType.START);

                        contentProviderAdapter.insertLocationInfo(getApplicationContext(), locationInfo, exerciseId);

                        return exerciseSession;
                    } else {
                        gpsLocationNotAvailable(exerciseSession);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(com.warriorfitapp.model.v2.ExerciseSession exerciseSession) {
                    if (exerciseSession != null) {
                        Exercise exercise = contentProviderAdapter.getExerciseById(getApplicationContext(), exerciseId);
                        startAsForeground(exercise);
                    }
                }

                private void waitForFreshLocation() {
                    try {
                        if (!mGoogleApiClient.isConnected() && mGoogleApiClient.isConnecting()) {
                            try {
                                lock.tryLock(1, TimeUnit.SECONDS);

                                // wait 6 times by 5 seconds = 30 seconds
                                for (int i = 0; i < 6; i++) {
                                    // wait for location client to connect to location providers
                                    isConnected.await(5, TimeUnit.SECONDS);
                                    if (mGoogleApiClient.isConnected()) {
                                        break;
                                    }
                                }

                            } finally {
                                lock.unlock();
                            }
                        }

                        if (mGoogleApiClient.isConnected()) {
                            if (lastLocation == null) {
                                Runnable displayToastRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        if (lastLocation == null) {
                                            Toast.makeText(getApplicationContext(), "Waiting for initial GPS location...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                };

                                try {
                                    lock.tryLock(1, TimeUnit.SECONDS);

                                    Log.d(TAG, "Request initial location to start tracking...");

                                    // display information toast in 3 seconds indicating that we are waiting for the initial location
                                    handler.postDelayed(displayToastRunnable, 3000);

                                    Log.d(TAG, "Wait for location...");

                                    // if no recent location available start to wait for location callback that provides the most recent location (we want fresh location to start exercise)
                                    if (detectLocationQuality(lastLocation) != LocationQuality.GOOD) {
                                        hasLocation.await(7, TimeUnit.SECONDS); // wait for location for 30 minute

                                        // if there still no recent location available wait for location callback little bit more
                                        if (detectLocationQuality(lastLocation) != LocationQuality.GOOD) {
                                            hasLocation.await(7, TimeUnit.SECONDS); // wait for location for 30 minute
                                        }

                                        // if there still no recent location try to use last known location
                                        if (detectLocationQuality(lastLocation) != LocationQuality.GOOD) {
                                            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                        }
                                    }
                                } finally {
                                    lock.unlock();
                                }

                                Log.d(TAG, "Cancel 'Waiting gps location...' toast");
                                handler.removeCallbacks(displayToastRunnable);
                            }
                        } else {
                            gpsLocationNotAvailable(null);
                        }

                    } catch (InterruptedException ignore) {
                    }
                }

                private void gpsLocationNotAvailable(com.warriorfitapp.model.v2.ExerciseSession exerciseSession) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "GPS Location isn't detected. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // notify to refresh UI
                    Uri uri = exerciseSession != null && exerciseSession.getId() != null ? UriHelper.getInstance().exerciseHistoryRecord(exerciseSession.getId()) : UriHelper.getInstance().allExerciseSessions();
                    getContentResolver().notifyChange(uri, null);
                }
            }.executeOnExecutor(executor);
        }
    }

    private void pauseExercise(Intent intent) {
        final long timestamp = System.currentTimeMillis();
        final String exerciseId = intent.getStringExtra(Const.EXERCISE_ID);

        Log.d(TAG, "Pause exercise [exerciseId=" + exerciseId + "]");

        if (!TextUtils.isEmpty(exerciseId)) {
            new AsyncTask<Long, Void, Void>() {
                @Override
                protected Void doInBackground(Long... params) {
                    com.warriorfitapp.model.v2.ExerciseSession exerciseSession = contentProviderAdapter.getInProgressExerciseHistoryRecord(getApplicationContext(), exerciseId);
                    exerciseSession.setState(com.warriorfitapp.model.v2.ExerciseState.PAUSED);

                    exerciseSession.setTime(exerciseSession.getTime() + timestamp - exerciseSession.getLastTimestampStarted());

                    contentProviderAdapter.updateExerciseSession(getApplicationContext(), exerciseSession, true);

                    if (lastLocation != null) {
                        com.warriorfitapp.model.v2.LocationInfo locationInfo = locationFactory.create(lastLocation);
                        locationInfo.setExerciseHistoryRecordId(exerciseSession.getId());
                        locationInfo.setType(com.warriorfitapp.model.v2.LocationInfo.LocationType.PAUSE);

                        contentProviderAdapter.insertLocationInfo(getApplicationContext(), locationInfo, exerciseId);
                    }
                    return null;
                }
            }.executeOnExecutor(executor);
        }
    }

    private void resumeExercise(Intent intent) {
        final long timestamp = System.currentTimeMillis();
        final String exerciseId = intent.getStringExtra(Const.EXERCISE_ID);

        Log.d(TAG, "Resume exercise [exerciseId=" + exerciseId + "]");

        if (!TextUtils.isEmpty(exerciseId)) {
            new AsyncTask<Long, Void, Void>() {
                @Override
                protected Void doInBackground(Long... params) {
                    com.warriorfitapp.model.v2.ExerciseSession exerciseSession = contentProviderAdapter.getInProgressExerciseHistoryRecord(getApplicationContext(), exerciseId);

                    exerciseSession.setState(com.warriorfitapp.model.v2.ExerciseState.STARTED);
                    exerciseSession.setLastTimestampStarted(timestamp);

                    contentProviderAdapter.updateExerciseSession(getApplicationContext(), exerciseSession, true);

                    if (lastLocation != null) {
                        com.warriorfitapp.model.v2.LocationInfo locationInfo = locationFactory.create(lastLocation);
                        locationInfo.setExerciseHistoryRecordId(exerciseSession.getId());
                        locationInfo.setType(com.warriorfitapp.model.v2.LocationInfo.LocationType.RESUME);

                        contentProviderAdapter.insertLocationInfo(getApplicationContext(), locationInfo, exerciseId);
                    }
                    return null;
                }
            }.executeOnExecutor(executor);
        }
    }

    private void finishExercise(Intent intent) {
        final long timestamp = System.currentTimeMillis();
        final String exerciseId = intent.getStringExtra(Const.EXERCISE_ID);

        Log.d(TAG, "Finish exercise [exerciseId=" + exerciseId + "]");

        if (!TextUtils.isEmpty(exerciseId)) {
            new AsyncTask<Long, Void, Void>() {
                @Override
                protected Void doInBackground(Long... params) {
                    com.warriorfitapp.model.v2.ExerciseSession exerciseSession = contentProviderAdapter.getInProgressExerciseHistoryRecord(getApplicationContext(), exerciseId);

                    if (exerciseSession.getState() != com.warriorfitapp.model.v2.ExerciseState.PAUSED) {
                        exerciseSession.setTime(exerciseSession.getTime() + timestamp - exerciseSession.getLastTimestampStarted());
                    }

                    exerciseSession.setState(com.warriorfitapp.model.v2.ExerciseState.DONE);

                    contentProviderAdapter.updateExerciseSession(getApplicationContext(), exerciseSession, true);

                    FlurryAdapter.getInstance().addExerciseSession(exerciseSession.getExerciseId(), "");

                    if (lastLocation != null) {
                        com.warriorfitapp.model.v2.LocationInfo locationInfo = locationFactory.create(lastLocation);
                        locationInfo.setExerciseHistoryRecordId(exerciseSession.getId());
                        locationInfo.setType(com.warriorfitapp.model.v2.LocationInfo.LocationType.FINISH);

                        contentProviderAdapter.insertLocationInfo(getApplicationContext(), locationInfo, exerciseId);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    stopIfNoMapExercisesInProgress();
                }
            }.executeOnExecutor(executor);
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        Log.d(TAG, "onLocationChanged " + location);
        // if (location != null) {
        if (detectLocationQuality(location) == LocationQuality.GOOD) {

            lastLocation = location;

            signalHasLocation();

            new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... params) {
                    int count = 0;
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(UriHelper.getInstance().allMapExerciseHistoryRecordsInProgressAndPaused(), ExerciseSessionTable.ALL_COLUMNS_QUALIFIED, null, null, null);

                        if (cursor != null) {
                            count = cursor.getCount();

                            while (cursor.moveToNext()) {
                                long id = cursor.getLong(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_ID));
                                String exerciseId = cursor.getString(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_EXERCISE_ID));

                                Log.d(TAG, "Processing location for exercise history record " + id);

                                double distance = cursor.getDouble(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_DISTANCE));
                                long time = cursor.getLong(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_TIME));

                                com.warriorfitapp.model.v2.ExerciseState state = com.warriorfitapp.model.v2.ExerciseState.valueOf(cursor.getString(cursor.getColumnIndex(ExerciseSessionTable.COLUMN_STATE)));

                                com.warriorfitapp.model.v2.LocationInfo locationInfo = locationFactory.create(lastLocation);
                                locationInfo.setExerciseHistoryRecordId(id);

                                long locationsCount = contentProviderAdapter.countLocationInfoByExerciseHistoryRecordId(getApplicationContext(), id);
                                Log.d(TAG, locationsCount + " locations associated with exercise history record " + id);

                                if (locationsCount == 0) {
                                    locationInfo.setType(com.warriorfitapp.model.v2.LocationInfo.LocationType.START);
                                    locationInfo.setCurrentDistance(0D);
                                } else {
                                    if (state == com.warriorfitapp.model.v2.ExerciseState.PAUSED) {
                                        locationInfo.setType(com.warriorfitapp.model.v2.LocationInfo.LocationType.PAUSED);
                                    } else {
                                        locationInfo.setType(com.warriorfitapp.model.v2.LocationInfo.LocationType.IN_PROGRESS);
                                    }

                                    com.warriorfitapp.model.v2.LocationInfo prevLocation = contentProviderAdapter.getMostRecentLocationInfo(getApplicationContext(), id);
                                    double speed = ConvertUtils.distanceToSpeed(prevLocation.getLatitude(), prevLocation.getLongitude(), prevLocation.getTimestamp(), locationInfo.getLatitude(), locationInfo.getLongitude(), locationInfo.getTimestamp(), distanceHolder);

                                    Log.d(TAG, "Prev location: " + prevLocation);
                                    Log.d(TAG, "Curr location: " + locationInfo);
                                    Log.d(TAG, "Calculated distance: " + Arrays.toString(distanceHolder));

                                    double distanceToPreviousLocation = distanceHolder[0]; //distance between current location and previous location in meters

                                    setSpeed(locationInfo, speed);
                                    setPace(locationInfo);

                                    if (locationInfo.getType() != com.warriorfitapp.model.v2.LocationInfo.LocationType.PAUSED) {
                                        if (speed <= Const.MAX_SPEED_LIMIT) {
                                            distance += ConvertUtils.metersToKm(distanceToPreviousLocation);
                                            time += (locationInfo.getTimestamp() - prevLocation.getTimestamp()) / 1000; // timestamp in seconds
                                        }

                                        locationInfo.setCurrentDistance(distance);

                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(ExerciseSessionTable.COLUMN_ID, id);
                                        contentValues.put(ExerciseSessionTable.COLUMN_DISTANCE, distance);
                                        contentValues.put(ExerciseSessionTable.COLUMN_TIME, time);

                                        contentProviderAdapter.updateExerciseSession(getApplicationContext(), contentValues, true);
                                    } else {
                                        Log.d(TAG, "Skipping calculation of time and distance passed because exercise paused");
                                    }
                                }

                                contentProviderAdapter.insertLocationInfo(getApplicationContext(), locationInfo, exerciseId);

                                Log.d(TAG, "Insert " + locationInfo);
                            }
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    return count;
                }

                private void setSpeed(com.warriorfitapp.model.v2.LocationInfo locationInfo, double speed) {
                    if (locationInfo.getSpeed() == null) {
                        if (speed > 0) {
                            if (speed <= Const.MAX_SPEED_LIMIT) {
                                locationInfo.setSpeed(speed); // current speed km/hr
                            }
                        } else {
                            locationInfo.setSpeed(0D);
                        }
                    }

                    Log.d(TAG, "Curr speed: " + locationInfo.getSpeed());
                }

                private void setPace(com.warriorfitapp.model.v2.LocationInfo locationInfo) {
                    if (locationInfo.getSpeed() != null) {
                        if (locationInfo.getSpeed() == 0) {
                            locationInfo.setPace(0D);
                        } else {
                            double pace = ConvertUtils.speedToPace(locationInfo.getSpeed());
                            if (pace > 0) {
                                locationInfo.setPace(pace); // current pace min/km
                            } else {
                                locationInfo.setPace(0D);
                            }
                        }
                    }

                    Log.d(TAG, "Curr pace: " + locationInfo.getPace());
                }
            }.execute();
        }
    }

    private void signalHasLocation() {
        try {
            lock.tryLock(1, TimeUnit.SECONDS);
            hasLocation.signalAll();
        } catch (InterruptedException ignore) {
        } finally {
            lock.unlock();
        }
    }

    private enum LocationQuality {
        BAD, GOOD
    }
}
