package com.warriorfitapp.mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.warriorfitapp.mobile.content.ContentProviderAdapter;
import com.warriorfitapp.mobile.flurry.FlurryAdapter;
import com.warriorfitapp.model.v2.Exercise;
import com.warriorfitapp.mobile.util.Const;
import com.warriorfitapp.mobile.util.ConvertUtils;
import com.warriorfitapp.mobile.util.ShareUtils;
import com.warriorfitapp.mobile.widget.MapExerciseInfoSummaryViewAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author Maria Dzyokh
 */
public class MapExerciseSummaryScreen extends AppCompatActivity {
    private static final String TAG = MapExerciseSummaryScreen.class.getSimpleName();
    public static final int DEFAULT_ZOOM = 18;

    private GoogleMap map;

    private int lineWidth = 5;
    private int activeLineColor;
    private int pausedLineColor;

    private com.warriorfitapp.model.v2.ExerciseSession exerciseSession;
    private List<com.warriorfitapp.model.v2.LocationInfo> locationInfoList = new ArrayList<com.warriorfitapp.model.v2.LocationInfo>();

    private ContentProviderAdapter contentProviderAdapter = ContentProviderAdapter.getInstance();

    private AppSettings appSettings;
    private ShareUtils shareUtils = new ShareUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_screen_results);
        appSettings = new AppSettings(this);

        MapExerciseInfoSummaryViewAdapter mapExerciseInfoSummaryViewAdapter = new MapExerciseInfoSummaryViewAdapter(findViewById(R.id.map_current_statistics));
        mapExerciseInfoSummaryViewAdapter.show(true);

        activeLineColor = getResources().getColor(R.color.main_red_semitransparent);
        pausedLineColor = getResources().getColor(R.color.paused_line_color);

        Intent intent = getIntent();

        exerciseSession = Parcels.unwrap(intent.getParcelableExtra(Const.EXERCISE_SESSION));
        Exercise exercise = Parcels.unwrap(intent.getParcelableExtra(Const.EXERCISE));

        initActionBar(exercise);

        mapExerciseInfoSummaryViewAdapter.refresh(exerciseSession);
        mapExerciseInfoSummaryViewAdapter.setCurrentAltitudeNotVisible();
        mapExerciseInfoSummaryViewAdapter.setCurrentPaceNotVisible();
        mapExerciseInfoSummaryViewAdapter.setCurrentSpeedNotVisible();

        loadPath();
    }

    private void initActionBar(Exercise exercise) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(exercise.getName());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            map.snapshot(new GoogleMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {
                    share(bitmap);
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void share(Bitmap bitmap) {
        double distance = appSettings.getSystemOfMeasurement() == AppSettings.SystemOfMeasurement.METRIC ? exerciseSession.getDistance() : ConvertUtils.kmToMiles(exerciseSession.getDistance());

        shareUtils.shareTextWithImage(MapExerciseSummaryScreen.this, getString(R.string.share_running_summary_message,
                String.format(MapExerciseInfoSummaryViewAdapter.DISTANCE_FORMAT, distance) + " " + getString(appSettings.getSystemOfMeasurement().getDistanceUnitResource()),
                MapExerciseInfoSummaryViewAdapter.TIME_FORMAT.format(new Date(exerciseSession.getTime()))), bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ShareUtils.SHARE_ACTIVITY_REQ == requestCode) {
            shareUtils.deleteTempImageIfExists(this);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loadPath() {
        // TODO: consider Loader instead of AsyncTask
        new AsyncTask<Long, Void, List<com.warriorfitapp.model.v2.LocationInfo>>() {
            @Override
            protected List<com.warriorfitapp.model.v2.LocationInfo> doInBackground(Long... params) {
                return contentProviderAdapter.getLocationInfoList(getApplicationContext(), params[0]);
            }

            @Override
            protected void onPostExecute(List<com.warriorfitapp.model.v2.LocationInfo> locationInfoList) {
                MapExerciseSummaryScreen.this.locationInfoList = locationInfoList;
                setUpMap();
            }
        }.execute(exerciseSession.getId());
    }

    private void setUpMap() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (map != null) {
                map.setMyLocationEnabled(false);
                map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        map.setOnCameraChangeListener(null);
                        drawPath();
                    }
                });
            }
        }
    }

    private void drawPath() {
        if (locationInfoList != null) {
            if (locationInfoList.size() >= 2) {

                List<LatLng> allPoints = new ArrayList<>();

                com.warriorfitapp.model.v2.LocationInfo firstLocation = null;
                com.warriorfitapp.model.v2.LocationInfo prevLocationInfo = null;
                com.warriorfitapp.model.v2.LocationInfo locationInfo = null;

                PolylineOptions polyline = new PolylineOptions();

                for (int i = 0; i < locationInfoList.size(); i++) {
                    prevLocationInfo = locationInfo;
                    locationInfo = locationInfoList.get(i);

                    if (firstLocation == null) {
                        firstLocation = locationInfo;
                    }

                    LatLng latLng = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());
                    allPoints.add(latLng);

                    if (prevLocationInfo != null) {
                        LatLng prevLatLng = new LatLng(prevLocationInfo.getLatitude(), prevLocationInfo.getLongitude());
                        double speed = ConvertUtils.distanceToSpeed(prevLocationInfo.getLatitude(), prevLocationInfo.getLongitude(), prevLocationInfo.getTimestamp(), locationInfo.getLatitude(), locationInfo.getLongitude(), locationInfo.getTimestamp(), null);

                        if (com.warriorfitapp.model.v2.LocationInfo.LocationType.PAUSED.equals(locationInfo.getType())) {
                            polyline.add(latLng);
                        } else if (com.warriorfitapp.model.v2.LocationInfo.LocationType.IN_PROGRESS.equals(locationInfo.getType())) {
                            // TODO: display grey lines for unreliable path
                            /*if (speed > Const.MAX_SPEED_LIMIT) {
                                polyline.add(latLng);
                                map.addPolyline(polyline.color(activeLineColor).width(lineWidth));

                                polyline.getPoints().clear();
                                polyline.add(prevLatLng);
                                polyline.add(latLng);

                                map.addPolyline(polyline.color(pausedLineColor).width(lineWidth));

                                polyline.getPoints().clear();
                                polyline.add(latLng);
                            } else {*/
                            polyline.add(latLng);
                            /*}*/
                        } else if (com.warriorfitapp.model.v2.LocationInfo.LocationType.PAUSE.equals(locationInfo.getType())) {
                            int lineColor = activeLineColor;
                            if (speed > Const.MAX_SPEED_LIMIT) {
                                lineColor = pausedLineColor;
                            }
                            polyline.add(latLng);
                            map.addPolyline(polyline.color(lineColor).width(lineWidth));
                            polyline.getPoints().clear();

                            map.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(latLng).title(getString(R.string.pause))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pause)));

                            polyline.add(latLng);
                            map.addPolyline(polyline.color(pausedLineColor).width(lineWidth));

                            polyline.getPoints().clear();
                            polyline.add(latLng);
                        } else if (com.warriorfitapp.model.v2.LocationInfo.LocationType.RESUME.equals(locationInfo.getType())) {
                            map.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(latLng).title(getString(R.string.resume))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_resume)));

                            polyline.add(latLng);

                            map.addPolyline(polyline.color(pausedLineColor).width(lineWidth));

                            polyline.getPoints().clear();
                            polyline.add(latLng);
                        } else if (com.warriorfitapp.model.v2.LocationInfo.LocationType.FINISH.equals(locationInfo.getType())) {
                            map.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.exercise_next))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                            polyline.add(latLng);

                            int lineColor = activeLineColor;
                            if (speed > Const.MAX_SPEED_LIMIT) {
                                lineColor = pausedLineColor;
                            }

                            map.addPolyline(polyline.color(prevLocationInfo.getType() == com.warriorfitapp.model.v2.LocationInfo.LocationType.PAUSE ? pausedLineColor : lineColor).width(lineWidth));
                        }
                    } else if (com.warriorfitapp.model.v2.LocationInfo.LocationType.START.equals(locationInfo.getType())) {
                        map.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.start)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        polyline.add(latLng);
                    }
                }

                fixZoom(allPoints);
            } else if (locationInfoList.size() == 1) {
                LatLng latLng = new LatLng(locationInfoList.get(0).getLatitude(), locationInfoList.get(0).getLongitude());
                map.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.start)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
            }
        }

    }

    private void fixZoom(List<LatLng> points) {
        if (points != null && !points.isEmpty()) {
            LatLngBounds.Builder bc = new LatLngBounds.Builder();
            for (LatLng item : points) {
                bc.include(item);
            }
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 160));
        }
    }
}
