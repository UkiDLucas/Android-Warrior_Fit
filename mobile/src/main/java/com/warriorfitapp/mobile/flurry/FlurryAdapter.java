package com.warriorfitapp.mobile.flurry;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryEventRecordStatus;
import com.warriorfitapp.mobile.AppSettings;
import com.warriorfitapp.model.v2.Exercise;
import com.warriorfitapp.model.v2.ExerciseSession;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @author Andrii Kovalov, Uki D. Lucas
 */
public class FlurryAdapter {

    public static final String FLURRY_EVENT_APP_OPENED = "app_opened";
    public static final String FLURRY_EVENT_EXERCISE_COMPLETED = "exercise_completed";
    public static final String FLURRY_EVENT_SOCIAL_NETWORK_LOGIN = "social_network_login";
    public static final String FLURRY_EVENT_PROGRAM_OPENED = "program_opened";
    public static final String FLURRY_EVENT_ADD_BODY_MEASUREMENT = "add_body_measurement";
    public static final String FLURRY_EVENT_ADD_EXERCISE_HISTORY_RECORD = "add_exercise_history_record";
    public static final String FLURRY_EVENT_DELETE_EXERCISE_HISTORY_RECORD = "delete_exercise_history_record";
    public static final String FLURRY_EVENT_SHARE_EXERCISE_HISTORY = "share_exercise_history";
    public static final String FLURRY_EVENT_UPDATE_PROFILE = "update_profile";
    public static final String FLURRY_EVENT_UPDATE_PROFILE_IMAGE = "update_profile_image";
    public static final String FLURRY_EVENT_INFO_OPENED = "info_opened";
    public static final String FLURRY_EVENT_ADD_SCHEDULE_ENTRY = "add_schedule_entry";
    public static final String FLURRY_EVENT_DELETE_SCHEDULE_ENTRY = "delete_schedule_entry";
    public static final String FLURRY_EVENT_SHARE_SCHEDULE = "share_schedule";
    public static final String FLURRY_EVENT_SET_USER_GOALS = "set_user_goals";
    public static final String FLURRY_EVENT_EXERCISE_LOG_OPENED = "exercise_log_opened";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final FlurryAdapter INSTANCE = new FlurryAdapter();
    private static final String TAG = FlurryAdapter.class.getSimpleName();

    static {
        TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }

    private FlurryAdapter() {
        //TODO re-write according to newest SDK guideline
    }

    public static FlurryAdapter getInstance() {
        return INSTANCE;
    }


    /**
     * @param context This method is called in any Activity
     * @Override protected void onStart() {
     * super.onStart();
     * FlurryAdapter.getInstance().startSession(this);
     * }
     */
    public void startSession(Context context) {

        String flurryKey = "";
        flurryKey = fetchPropertyForFlurryKey(context, flurryKey);


        if (flurryKey != null && !flurryKey.equals("")) {
            Log.i(TAG, "Starting Flurry session for " + flurryKey);
            FlurryAgent.init(context, flurryKey); // TODO W/FlurryAgent: 'init' method is deprecated.
            FlurryAgent.onStartSession(context);
        } else {
            Log.e(TAG, "FLURRY KEY not provided: ");
        }
    }

    private String fetchPropertyForFlurryKey(Context context, String flurryKey) {
        try {
            Resources resources = context.getResources();
            AssetManager assetManager = resources.getAssets();
            InputStream inputStream = assetManager.open("secret.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            Log.d(TAG, "properties: " + properties);

            flurryKey = properties.getProperty("FLURRY_KEY");
            Log.d(TAG, "Reading FLURRY_KEY: " + flurryKey); //TODO: turn off in PROD

        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return flurryKey;
    }

    public void endSession(Context context) {
        FlurryAgent.onEndSession(context);
    }


    public void logEventAppOpened() {
        Log.d(TAG, FLURRY_EVENT_APP_OPENED);
        FlurryAgent.logEvent(FLURRY_EVENT_APP_OPENED); //TODO add app version parameter
    }


    public void programOpened(com.warriorfitapp.model.v2.Program program, boolean isSubscribed, int numberOfExercises) {
        if (program != null) {
            Map<String, String> args = createProgramArgs(program);
            args.put("is_subscribed", String.valueOf(isSubscribed));
            args.put("number_of_exercises", String.valueOf(numberOfExercises));

            Log.d(TAG, FLURRY_EVENT_PROGRAM_OPENED + " " + args);
            FlurryAgent.logEvent(FLURRY_EVENT_PROGRAM_OPENED, args);
        }
    }

    public void exerciseCompleted(Exercise exercise, ExerciseSession exerciseSession) {
        Map<String, String> args = createExerciseArgs(exercise);

        if (exerciseSession != null) {
            if (exerciseSession.hasRepetitions() && exerciseSession.getRepetitions() > 0) {
                args.put("repetitions", String.valueOf(exerciseSession.getRepetitions()));
            }
            if (exerciseSession.hasDistance() && exerciseSession.getDistance() > 0) {
                args.put("distance", String.format("%.1f", exerciseSession.getDistance()));
            }
            if (exerciseSession.hasTime() && exerciseSession.getTime() > 0) {
                args.put("time", TIME_FORMAT.format(new Date(exerciseSession.getTime())));
                args.put("time_seconds", String.valueOf(exerciseSession.getTime() / 1000));
            }
            if (exerciseSession.hasWeight() && exerciseSession.getWeight() > 0) {
                args.put("weight", String.format("%.1f", exerciseSession.getWeight()));
            }
        }

        Log.d(TAG, FLURRY_EVENT_EXERCISE_COMPLETED + " " + args);
        FlurryAgent.logEvent(FLURRY_EVENT_EXERCISE_COMPLETED, args);
    }


    public void exerciseLogOpened(int totalCompleted, int todayCompleted) {
        Map<String, String> args = new HashMap<>();
        args.put("number_of_exercises_total", String.valueOf(totalCompleted));
        args.put("number_of_exercises_today", String.valueOf(todayCompleted));

        Log.d(TAG, FLURRY_EVENT_EXERCISE_LOG_OPENED + " " + args);
        FlurryAgent.logEvent(FLURRY_EVENT_EXERCISE_LOG_OPENED, args);
    }

    public void deleteExerciseSession(long exerciseSessionId, String exerciseName) {
        Map<String, String> args = new HashMap<String, String>();
        args.put("exercise_id", String.valueOf(exerciseSessionId));
        args.put("exercise_name", exerciseName);
        FlurryAgent.logEvent(FLURRY_EVENT_DELETE_EXERCISE_HISTORY_RECORD, args);
    }

    public void addExerciseSession(String exerciseId, String exerciseName) {
        Map<String, String> args = new HashMap<String, String>();
        args.put("exercise_id", String.valueOf(exerciseId));
        args.put("exercise_name", exerciseName);
        FlurryAgent.logEvent(FLURRY_EVENT_ADD_EXERCISE_HISTORY_RECORD, args);
    }

    public void updateProfileImage() {
        FlurryAgent.logEvent(FLURRY_EVENT_UPDATE_PROFILE_IMAGE);
    }

    public void shareExerciseHistory(String shareApp, int numberOfExercises) {


        FlurryAgent.logEvent(FLURRY_EVENT_SHARE_EXERCISE_HISTORY);
    }

    public void infoOpened() {
        Log.d(TAG, FLURRY_EVENT_INFO_OPENED);
        FlurryAgent.logEvent(FLURRY_EVENT_INFO_OPENED);
    }

    public void infoOpened(String type) {
        Map<String, String> args = new HashMap<>();
        args.put("type", type);

        Log.d(TAG, FLURRY_EVENT_INFO_OPENED + " " + args);
        FlurryAgent.logEvent(FLURRY_EVENT_INFO_OPENED, args);
    }

    public void socialNetworkLogin(String loginAction) {
        Log.d(TAG, "socialNetworkLogin: " + loginAction);
        Map<String, String> args = new HashMap<String, String>();
        args.put("action_taken", loginAction);
        FlurryAgent.logEvent(FLURRY_EVENT_SOCIAL_NETWORK_LOGIN, args);
    }

    public void addBodyMeasurement(long userId, int type, String title) {
        Map<String, String> args = new HashMap<String, String>();
        args.put("user_id", String.valueOf(userId));
        args.put("type_id", String.valueOf(type));
        args.put("type_name", title);
        FlurryAgent.logEvent(FLURRY_EVENT_ADD_BODY_MEASUREMENT, args);
    }

    public void setUserGoals() {
        FlurryAgent.logEvent(FLURRY_EVENT_SET_USER_GOALS);
    }

    public void updateProfile(com.warriorfitapp.model.v2.User user, AppSettings.SystemOfMeasurement units, AppSettings.DateFormat dateFormat) {
        Map<String, String> args = new HashMap<>();

        if (user.hasAge()) {
            args.put("age", String.valueOf(user.getAge()));
        }

        if (user.hasWeight()) {
            args.put("weight", String.format("%.1f", user.getWeight()));
        }

        if (user.hasHeight()) {
            args.put("height", String.valueOf(user.getHeight()));
        }

        if (user.hasWaist()) {
            args.put("waist", String.format("%.1f", user.getWeight()));
        }

        args.put("login", String.valueOf(user.getAccountType()));
        args.put("gender", user.isMale() ? "male" : "female");

        args.put("units", String.valueOf(units));
        args.put("date", String.valueOf(dateFormat));


        FlurryEventRecordStatus status = FlurryAgent.logEvent(FLURRY_EVENT_UPDATE_PROFILE + args);
        Log.d(TAG, status.toString() + ": " + FLURRY_EVENT_UPDATE_PROFILE + " " + args);
    }

    public void deleteScheduleEntry() {
        FlurryAgent.logEvent(FLURRY_EVENT_DELETE_SCHEDULE_ENTRY);
    }

    public void addScheduleEntry() {
        FlurryAgent.logEvent(FLURRY_EVENT_ADD_SCHEDULE_ENTRY);
    }

    public void shareScheduleEntry() {
        FlurryAgent.logEvent(FLURRY_EVENT_SHARE_SCHEDULE);
    }

    @NonNull
    private Map<String, String> createExerciseArgs(Exercise exercise) {
        Map<String, String> args = new HashMap<String, String>();
        args.put("youtube_id", String.valueOf(exercise.getYoutubeId()));
        args.put("exercise_name", exercise.getName());
        return args;
    }

    @NonNull
    private Map<String, String> createProgramArgs(com.warriorfitapp.model.v2.Program program) {
        Map<String, String> args = new HashMap<>();
        args.put("program_id", String.valueOf(program.getId()));
        args.put("program_name", program.getName());
        return args;
    }
}
