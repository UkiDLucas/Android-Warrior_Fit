package com.cyberwalkabout.cyberfit;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.cyberwalkabout.cyberfit.content.ContentProviderAdapter;
import com.cyberwalkabout.cyberfit.content.UriHelper;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.ExerciseSessionTable;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.LocationInfoTable;
import com.cyberwalkabout.cyberfit.flurry.FlurryAdapter;
import com.cyberwalkabout.cyberfit.model.v2.Exercise;
import com.cyberwalkabout.cyberfit.model.v2.ExerciseSession;
import com.cyberwalkabout.cyberfit.model.v2.ExerciseState;
import com.cyberwalkabout.cyberfit.model.v2.LocationInfo;
import com.cyberwalkabout.cyberfit.model.v2.factory.ExerciseSessionCursorFactory;
import com.cyberwalkabout.cyberfit.util.Const;
import com.cyberwalkabout.cyberfit.widget.MapExerciseInfoSummaryViewAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import org.parceler.Parcels;

import java.util.List;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

/**
 * @author Maria Dzyokh
 */
public class MapExerciseScreen extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener, /*ISimpleDialogListener,*/ LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MapExerciseScreen.class.getSimpleName();
    private static final int LOCATION_SETTINGS_REQUEST_CODE = 0;
    private static final int ANIMATE_CAMERA_DELAY = 15000;
    private static final float DEFAULT_ZOOM = 16.5F;

    private ContentProviderAdapter contentProviderAdapter = ContentProviderAdapter.getInstance();
    private GoogleMap map;

    private MapExerciseInfoSummaryViewAdapter mapExerciseInfoSummaryViewAdapter;

    private LinearLayout mapButtonsContainer;

    private Button btnFinish;
    private Button btnResume;
    private Button btnPause;
    private Button btnStart;

    private int lineWidth = 5;
    private int activeLineColor;
    private int pausedLineColor;

    private Exercise exercise;
    private boolean isFavorite;

    private LocationInfo currentLocation;

    private float currentZoom = DEFAULT_ZOOM;

    private CountDownTimer countDownTimer = new CountDownTimer(ANIMATE_CAMERA_DELAY, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            animateCamera = true;
        }
    };

    private boolean animateCamera = true;

    private ExerciseSession exerciseSession;

    private GoogleApiClient mGoogleApiClient;

    private boolean moveToCurrentLocation = true;

    private Handler handler = new Handler();

    private ExerciseSessionCursorFactory exerciseSessionCursorFactory = ExerciseSessionCursorFactory.getInstance();

    private Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            if (validForTimer()) {
                long calculatedTime = exerciseSession.getTime();

                calculatedTime += System.currentTimeMillis() - exerciseSession.getLastTimestampStarted();

                mapExerciseInfoSummaryViewAdapter.setTime(calculatedTime);
            }

            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);
            handler.postAtTime(this, next);
        }

        private boolean validForTimer() {
            return exerciseSession != null && exerciseSession.getId() != null && exerciseSession.getLastTimestampStarted() != null && exerciseSession.getState() == ExerciseState.STARTED;
        }
    };

    private ContentObserver exerciseSessionObserver = new ContentObserver(handler) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(TAG, ExerciseSessionTable.TABLE_NAME + ".onChange(" + uri + ")");
            Bundle bundle = new Bundle();
            bundle.putString(Const.EXERCISE_ID, exercise.getId());

            try {
                Long exerciseSessionId = Long.valueOf(uri.getLastPathSegment());
                bundle.putLong(Const.EXERCISE_SESSION_ID, exerciseSessionId);
            } catch (NumberFormatException e) {
            }

            getSupportLoaderManager().restartLoader(ContentProviderAdapter.LOADER_EXERCISE_IN_PROGRESS, bundle, MapExerciseScreen.this);
            getSupportLoaderManager().restartLoader(ContentProviderAdapter.LOADER_EXERCISE_COMPLETED, bundle, MapExerciseScreen.this);
        }
    };

    private ContentObserver locationInfoObserver = new ContentObserver(handler) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(TAG, LocationInfoTable.TABLE_NAME + ".onChange(" + uri + ")");

            // verify that location update is relevant to current exercise session
            if (exerciseSession != null) {
                boolean match = matchLocationInfoUriToExerciseSession(uri);

                if (match) {
                    long id = Long.parseLong(uri.getLastPathSegment());

                    LocationInfo locationInfo = contentProviderAdapter.getLocationInfoById(getApplicationContext(), id);

                    Log.d(TAG, LocationInfoTable.TABLE_NAME + ".onChange(" + uri + ") = " + locationInfo);

                    if (locationInfo != null) {
                        mapExerciseInfoSummaryViewAdapter.refresh(locationInfo);

                        switch (locationInfo.getType()) {
                            case START:
                                map.addMarker(new MarkerOptions().position(new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude())).title(getString(R.string.start))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                appendLocationToPath(locationInfo);
                                break;
                            case PAUSE:
                                map.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude())).title(getString(R.string.pause))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pause)));
                                appendLocationToPath(locationInfo);
                                break;
                            case RESUME:
                                map.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude())).title(getString(R.string.resume))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_resume)));
                                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                                appendLocationToPath(locationInfo);
                                break;
                            default:
                                appendLocationToPath(locationInfo);
                                if (animateCamera) {
                                    CameraPosition currentLocation = new CameraPosition.Builder().target(new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude())).zoom(currentZoom).build();
                                    map.animateCamera(CameraUpdateFactory.newCameraPosition(currentLocation));
                                }
                                break;
                        }
                    }
                }
            } else {
                Log.w(TAG, "LocationInfo doesn't match current ExerciseSession.\n" + uri + "\nvs\n" + exerciseSession);
            }
        }

        private boolean matchLocationInfoUriToExerciseSession(Uri uri) {
            boolean match = false;

            Long exerciseId = getExerciseIdFromUri(uri);
            if (exerciseId != null) {
                match = exerciseId.equals(exerciseSession.getExerciseId());
            } else {
                Long exerciseSessionId = getExerciseSessionIdFromUri(uri);
                if (exerciseSessionId != null) {
                    match = exerciseSessionId.equals(exerciseSession.getId());
                }
            }
            return match;
        }

        private Long getExerciseSessionIdFromUri(Uri uri) {
            Long exerciseHistoryRecordId = null;
            if (!TextUtils.isEmpty(uri.getQueryParameter(LocationInfoTable.COLUMN_EXERCISE_SESSION_ID))) {
                try {
                    exerciseHistoryRecordId = Long.valueOf(uri.getQueryParameter(LocationInfoTable.COLUMN_EXERCISE_SESSION_ID));
                } catch (NumberFormatException ignore) {
                }
            }
            return exerciseHistoryRecordId;
        }

        private Long getExerciseIdFromUri(Uri uri) {
            Long exerciseId = null;
            if (!TextUtils.isEmpty(uri.getQueryParameter(ExerciseSessionTable.COLUMN_EXERCISE_ID))) {
                try {
                    exerciseId = Long.valueOf(uri.getQueryParameter(ExerciseSessionTable.COLUMN_EXERCISE_ID));
                } catch (NumberFormatException ignore) {
                }
            }
            return exerciseId;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_screen);

        mapExerciseInfoSummaryViewAdapter = new MapExerciseInfoSummaryViewAdapter(findViewById(R.id.map_current_statistics));

        exercise = Parcels.unwrap(getIntent().getParcelableExtra(Const.EXERCISE));
        isFavorite = getIntent().getBooleanExtra(Const.IS_FAVORITE, false);

        iniActionBar();

        activeLineColor = getResources().getColor(R.color.main_red_semitransparent);
        //activeLineColor = getResources().getColor(R.color.light_green);
        pausedLineColor = getResources().getColor(R.color.paused_line_color);

        btnFinish = (Button) findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(this);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnPause.setOnClickListener(this);
        btnResume = (Button) findViewById(R.id.btn_resume);
        btnResume.setOnClickListener(this);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);

        initMapMenu();

        setUpMap();

        Bundle bundle = new Bundle();
        bundle.putString(Const.EXERCISE_ID, exercise.getId());
        getSupportLoaderManager().initLoader(ContentProviderAdapter.LOADER_EXERCISE_IN_PROGRESS, bundle, this);

        initLocationClient();

        getContentResolver().registerContentObserver(UriHelper.getInstance().allExerciseSessions(), true, exerciseSessionObserver);
        getContentResolver().registerContentObserver(UriHelper.getInstance().allLocationInfo(), true, locationInfoObserver);
    }

    private synchronized void initLocationClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(exerciseSessionObserver);
        getContentResolver().unregisterContentObserver(locationInfoObserver);
    }

    private void iniActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            if (exercise != null) {
                supportActionBar.setTitle(exercise.getName());
            }
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAdapter.getInstance().startSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAdapter.getInstance().endSession(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
        handler.removeCallbacks(timerTask);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(timerTask);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start: {
                Intent intent = new Intent(this, ExerciseLocationTrackingService.class);
                intent.setAction(ExerciseLocationTrackingService.ACTION_START);
                intent.putExtra(Const.EXERCISE_ID, exercise.getId());
                intent.putExtra(Const.EXERCISE_SESSION_ID, exerciseSession.getId());
                startService(intent);
            }
            break;
            case R.id.btn_pause: {
                mapExerciseInfoSummaryViewAdapter.setCurrentSpeed(0);
                mapExerciseInfoSummaryViewAdapter.setCurrentPace(0);

                Intent intent = new Intent(this, ExerciseLocationTrackingService.class);
                intent.setAction(ExerciseLocationTrackingService.ACTION_PAUSE);
                intent.putExtra(Const.EXERCISE_ID, exercise.getId());
                intent.putExtra(Const.EXERCISE_SESSION_ID, exerciseSession.getId());

                startService(intent);
            }
            break;
            case R.id.btn_resume: {
                Intent intent = new Intent(this, ExerciseLocationTrackingService.class);
                intent.setAction(ExerciseLocationTrackingService.ACTION_RESUME);
                intent.putExtra(Const.EXERCISE_ID, exercise.getId());
                intent.putExtra(Const.EXERCISE_SESSION_ID, exerciseSession.getId());

                startService(intent);
            }
            break;
            case R.id.btn_finish: {
                Intent intent = new Intent(this, ExerciseLocationTrackingService.class);
                intent.setAction(ExerciseLocationTrackingService.ACTION_FINISH);
                intent.putExtra(Const.EXERCISE_ID, exercise.getId());
                intent.putExtra(Const.EXERCISE_SESSION_ID, exerciseSession.getId());

                startService(intent);
            }
            break;
            default:
                break;
        }
        v.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exercise_details_menu, menu);
        menu.findItem(R.id.action_favorite).setIcon(isFavorite ? R.drawable.ic_favorite_selected : R.drawable.ic_favorite_unselected);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_favorite) {
            toggleFavorite(item);

            isFavorite = !isFavorite;
            supportInvalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleFavorite(final MenuItem item) {
        if (isFavorite) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    contentProviderAdapter.unfavoriteExercise(getApplicationContext(), exercise.getId());
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    item.setIcon(R.drawable.ic_favorite_unselected);
                }
            }.execute();
        } else {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    contentProviderAdapter.favoriteExercise(getApplicationContext(), exercise.getId());
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    item.setIcon(R.drawable.ic_favorite_selected);
                }
            }.execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            startLocationTrackingService();
        }
    }

    private void updateUiState(final ExerciseSession exerciseSession, ExerciseState oldState) {
        if (exerciseSession.getState() != ExerciseState.READY_TO_START) {
            mapExerciseInfoSummaryViewAdapter.show(true);
        } else {
            mapExerciseInfoSummaryViewAdapter.hide(false);
        }
        mapExerciseInfoSummaryViewAdapter.refresh(exerciseSession);
        updateButtonsVisibility(exerciseSession.getState());

        /*if (oldState == null && exerciseSession.getState() == ExerciseState.STARTED) {
            restorePath(exerciseSession);
        }*/
    }

    private void restorePath(final ExerciseSession exerciseSession) {
        // TODO: consider Loader instead of AsyncTask
        new AsyncTask<Void, Void, List<LocationInfo>>() {

            @Override
            protected List<LocationInfo> doInBackground(Void... params) {
                return contentProviderAdapter.getLocationInfoList(getApplicationContext(), exerciseSession.getId());
            }

            @Override
            protected void onPostExecute(List<LocationInfo> locationInfoList) {
                drawRestoredPath(locationInfoList);
            }
        }.execute();
    }

    private void updateButtonsVisibility(ExerciseState state) {
        switch (state) {
            case PAUSED:
                btnPause.setVisibility(View.GONE);
                btnStart.setVisibility(View.GONE);
                btnResume.setVisibility(View.VISIBLE);
                btnFinish.setVisibility(View.VISIBLE);
                btnResume.setEnabled(true);
                btnFinish.setEnabled(true);
                break;
            case STARTED:
                btnResume.setVisibility(View.GONE);
                btnStart.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
                btnFinish.setVisibility(View.VISIBLE);
                btnPause.setEnabled(true);
                btnFinish.setEnabled(true);
                break;
            default:
                // case READY_TO_START:
                btnPause.setVisibility(View.GONE);
                btnStart.setVisibility(View.VISIBLE);
                btnResume.setVisibility(View.GONE);
                btnFinish.setVisibility(View.GONE);
                btnStart.setEnabled(true);
        }
    }

    private void drawRestoredPath(List<LocationInfo> locations) {
        if (locations != null) {
            if (locations.size() >= 2) {
                LocationInfo firstLocation = null;
                LocationInfo locationInfo = null;

                for (int i = 0; i < locations.size(); i++) {
                    locationInfo = locations.get(i);

                    if (firstLocation == null) {
                        firstLocation = locationInfo;
                    }

                    switch (locationInfo.getType()) {
                        case START:
                            map.addMarker(new MarkerOptions().position(new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude())).title(getString(R.string.start))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            appendLocationToPath(locationInfo);
                            break;
                        case PAUSE:
                            map.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude())).title(getString(R.string.pause))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pause)));
                            appendLocationToPath(locationInfo);
                            break;
                        case PAUSED:
                            appendLocationToPath(locationInfo);
                            break;
                        case IN_PROGRESS:
                            appendLocationToPath(locationInfo);
                            break;
                        case RESUME:
                            map.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude())).title(getString(R.string.resume))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_resume)));
                            appendLocationToPath(locationInfo);
                            break;
                        case FINISH:
                            break;
                    }
                    if (i == locations.size() - 1) {
                        currentLocation = locationInfo;
                    }
                }

                if (firstLocation != null) {
                    LatLngBounds.Builder bc = new LatLngBounds.Builder();

                    bc.include(new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude()));
                    bc.include(new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude()));

                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 80));
                    map.animateCamera(CameraUpdateFactory.zoomTo(currentZoom), 1000, null);
                }
            } else if (locations.size() == 1) {
                currentLocation = locations.get(0);
                if (currentLocation != null) {
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    map.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.start)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, currentZoom));
                }
            }
        }

    }

    private void setUpMap() {
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if (map != null) {
            map.getUiSettings().setZoomControlsEnabled(false);
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    initMyLocationListener();
                }
            });
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    currentZoom = cameraPosition.zoom;
                }
            });
        }
    }

    private void startLocationTrackingService() {
        if (isLocationProvidersEnabled()) {
            Intent initServiceIntent = new Intent(MapExerciseScreen.this, ExerciseLocationTrackingService.class);
            initServiceIntent.putExtra(Const.EXERCISE_ID, exercise.getId());
            startService(initServiceIntent);
        } else {
            SimpleDialogFragment.createBuilder(MapExerciseScreen.this, getSupportFragmentManager()).setMessage(getString(R.string.location_provider_required_notice))
                    .setPositiveButtonText(android.R.string.ok).setNegativeButtonText(android.R.string.cancel).show();
        }
    }

    private void initMapMenu() {

        final Button btnTerrain = (Button) findViewById(R.id.btn_terrain);
        final Button btnRoadMap = (Button) findViewById(R.id.btn_road_map);
        final Button btnSatellite = (Button) findViewById(R.id.btn_satellite);

        View.OnClickListener changeMapTypeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_terrain) {
                    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    v.setSelected(true);
                    btnRoadMap.setSelected(false);
                    btnSatellite.setSelected(false);
                } else if (v.getId() == R.id.btn_road_map) {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    v.setSelected(true);
                    btnTerrain.setSelected(false);
                    btnSatellite.setSelected(false);
                } else if (v.getId() == R.id.btn_satellite) {
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    v.setSelected(true);
                    btnTerrain.setSelected(false);
                    btnRoadMap.setSelected(false);
                }
            }
        };

        mapButtonsContainer = (LinearLayout) findViewById(R.id.map_menu_container);

        btnTerrain.setOnClickListener(changeMapTypeListener);
        btnRoadMap.setOnClickListener(changeMapTypeListener);
        btnRoadMap.setSelected(true);
        btnSatellite.setOnClickListener(changeMapTypeListener);

        findViewById(R.id.btn_switcher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animate(mapButtonsContainer).alpha(mapButtonsContainer.getVisibility() == View.VISIBLE ? 0.0f : 1.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mapButtonsContainer.setVisibility(mapButtonsContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    }
                });

            }
        });
    }

    private void appendLocationToPath(LocationInfo newLocation) {
        if (currentLocation != null) {
            int lineColor = activeLineColor;

            if (LocationInfo.LocationType.PAUSE.equals(currentLocation.getType()) || LocationInfo.LocationType.PAUSED.equals(currentLocation.getType())) {
                lineColor = pausedLineColor;
            }
            // TODO: implement grayed out lines for unreliable parts of the path
            /*else {
                double speed = ConvertUtils.distanceToSpeed(currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getTimestamp(), newLocation.getLatitude(), newLocation.getLongitude(), newLocation.getTimestamp(), null);
                if (speed > Const.MAX_SPEED_LIMIT) {
                    lineColor = pausedLineColor;
                }
            }*/

            PolylineOptions options = new PolylineOptions();
            options.add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            options.add(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
            map.addPolyline(options.color(lineColor).width(lineWidth));
        }

        currentLocation = newLocation;
    }

    private boolean isLocationProvidersEnabled() {
        return !TextUtils.isEmpty(android.provider.Settings.Secure.getString(getContentResolver(),
                android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED));
    }

    /*@Override
    public void onPositiveButtonClicked(int requestCode) {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_SETTINGS_REQUEST_CODE);
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {

    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ContentProviderAdapter.LOADER_EXERCISE_IN_PROGRESS: {
                return ContentProviderAdapter.getInstance().loaderInProgressExerciseHistoryRecord(this, args);
            }
            case ContentProviderAdapter.LOADER_EXERCISE_COMPLETED: {
                return ContentProviderAdapter.getInstance().loaderCompletedExerciseHistoryRecord(this, args);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case ContentProviderAdapter.LOADER_EXERCISE_IN_PROGRESS: {
                ExerciseState oldState = null;

                if (exerciseSession != null) {
                    oldState = exerciseSession.getState();
                }

                if (data.getCount() > 0 && data.moveToFirst()) {
                    ExerciseSession newExerciseSession = exerciseSessionCursorFactory.create(data);

                    // if re-opening map for ongoing exercise, start location tracking service to keep getting location updates
                    if (this.exerciseSession == null && newExerciseSession.getState() != ExerciseState.READY_TO_START) {
                        startLocationTrackingService();
                    }

                    if (exerciseSession == null) {
                        restorePath(newExerciseSession);
                    }

                    this.exerciseSession = newExerciseSession;
                } else {
                    // there is no 'in progress' exercises
                    this.exerciseSession = new ExerciseSession();
                    this.exerciseSession.setExerciseId(exercise.getId());
                    this.exerciseSession.setTime(0L);
                    this.exerciseSession.setDistance(0D);
                    this.exerciseSession.setState(ExerciseState.READY_TO_START);
                }

                updateUiState(exerciseSession, oldState);
            }
            break;
            case ContentProviderAdapter.LOADER_EXERCISE_COMPLETED: {
                if (data.getCount() > 0 && data.moveToFirst()) {
                    ExerciseSession completedExerciseSession = exerciseSessionCursorFactory.create(data);
                    Intent intent = new Intent(MapExerciseScreen.this, MapExerciseSummaryScreen.class);
                    intent.putExtra(Const.EXERCISE_SESSION_ID, completedExerciseSession.getId());
                    intent.putExtra(Const.EXERCISE_SESSION, Parcels.wrap(completedExerciseSession));
                    intent.putExtra(Const.EXERCISE, Parcels.wrap(exercise));
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (moveToCurrentLocation) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), currentZoom));
                moveToCurrentLocation = false;
            } else {
                initMyLocationListener();
            }
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void initMyLocationListener() {
        if (map != null) {
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    if (location != null) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), currentZoom));
                        map.setOnMyLocationChangeListener(null);
                        moveToCurrentLocation = false;
                    }
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (moveToCurrentLocation) {
            initMyLocationListener();
        }
    }
}
