package technology.uki.android_warriorfit_wear;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private Button exerciseStartButton;
    private Button exerciseStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        exerciseStartButton = (Button) findViewById(R.id.exercise_start);
        exerciseStopButton = (Button) findViewById(R.id.exercise_stop);
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);
        mClockView = (TextView) findViewById(R.id.clock);

        initExerciseStartButton();
        initExerciseStopButton();
    }

    private void initExerciseStartButton() {
        exerciseStartButton.setVisibility(View.VISIBLE);

        exerciseStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exerciseStartButton.setVisibility(View.GONE);
                exerciseStopButton.setVisibility(View.VISIBLE);
                Log.d(TAG, "Button exerciseStartButton.onCLick() ");
            }
        });
    }

    private void initExerciseStopButton() {
        exerciseStopButton.setVisibility(View.GONE);

        exerciseStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exerciseStartButton.setVisibility(View.VISIBLE);
                exerciseStopButton.setVisibility(View.GONE);
                Log.d(TAG, "Button exerciseStopButton.onCLick() ");
            }
        });
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        Log.d(TAG, "onUpdateAmbient() ");
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        Log.d(TAG, "onExitAmbient() - the app is ON");
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) { // the app is OFF

            // not need to show buttons as they do not respond when in ambient mode
            exerciseStartButton.setVisibility(View.GONE);
            exerciseStopButton.setVisibility(View.GONE);

            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else { // the app is ON
            // show the START button for the current exercise
            exerciseStartButton.setVisibility(View.VISIBLE);
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }
    }
}
