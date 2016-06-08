package com.warriorfitapp.mobile.services;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by ukilucas on 6/8/16.
 */
public class WearExerciseService extends WearableListenerService {
    private static final String TAG = WearExerciseService.class.getSimpleName();
    public static String MESSAGE_EVENT_EXERCISE_STARTED = "WearMessageExerciseStarted";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        String event = messageEvent.getPath();
        Log.d(TAG, event);
        //    String[] message = event.split("--");
//        if (message[0].equals(SERVICE_CALLED_WEAR)) {
//            startActivity(new Intent((Intent) Listactivity.getInstance().tutorials.get(message[1]))
//                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//        }
    }
}
