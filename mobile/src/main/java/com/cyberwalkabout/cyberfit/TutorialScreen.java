package com.cyberwalkabout.cyberfit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.vlonjatg.android.apptourlibrary.AppTour;
import com.vlonjatg.android.apptourlibrary.MaterialSlide;

/**
 * @author Andrii Kovalov
 */
public class TutorialScreen extends AppTour {

    private AppSettings appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appSettings = new AppSettings(this);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        String[] tutorialDescriptions = getResources().getStringArray(R.array.tutorial_descriptions);
        String[] tutorialTitles = getResources().getStringArray(R.array.tutorial_titles);

        // colors from material design palette http://www.google.com.ua/design/spec/style/color.html#color-color-palette
        addSlide(MaterialSlide.newInstance(R.drawable.tutorial_1, tutorialTitles[0], tutorialDescriptions[0], Color.WHITE, Color.WHITE), Color.parseColor("#607D8B"));
        addSlide(MaterialSlide.newInstance(R.drawable.tutorial_2, tutorialTitles[1], tutorialDescriptions[1], Color.WHITE, Color.WHITE), Color.parseColor("#795548"));
        addSlide(MaterialSlide.newInstance(R.drawable.tutorial_3, tutorialTitles[2], tutorialDescriptions[2], Color.WHITE, Color.WHITE), Color.parseColor("#3F51B5"));
        addSlide(MaterialSlide.newInstance(R.drawable.tutorial_4, tutorialTitles[3], tutorialDescriptions[3], Color.WHITE, Color.WHITE), Color.parseColor("#673AB7"));
        addSlide(MaterialSlide.newInstance(R.drawable.tutorial_5, tutorialTitles[4], tutorialDescriptions[4], Color.WHITE, Color.WHITE), Color.parseColor("#AB47BC"));
        addSlide(MaterialSlide.newInstance(R.drawable.tutorial_6, tutorialTitles[5], tutorialDescriptions[5], Color.WHITE, Color.WHITE), Color.parseColor("#EC407A"));

        setSkipButtonTextColor(Color.WHITE);
        setNextButtonColorToWhite();
        setDoneButtonTextColor(Color.WHITE);
    }

    @Override
    public void onSkipPressed() {
        appSettings.setTutorialShown(true);
        startHomeScreen();
    }

    @Override
    public void onDonePressed() {
        appSettings.setTutorialShown(true);
        startHomeScreen();
    }

    private void startHomeScreen() {
        startActivity(new Intent(TutorialScreen.this, HomeScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }
}
