package com.warriorfitapp.mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

public class AppSettings {

    private static final int TUTORIAL_VERSION = 3;

    public static final String KEY_FIRST_LAUNCH = "first_launch";
    public static final String KEY_TUTORIAL_SHOWN = "tutorial_shown_v" + TUTORIAL_VERSION;
    public static final String KEY_SHOW_GOALS_POPUP = "goals_popup";
    public static final String KEY_DRAWER_OPENED = "drawer_opened";
    public static final String KEY_SYSTEM_OF_MEASUREMENT = "system_of_measurement";
    public static final String KEY_DATE_FORMAT = "date_format";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_FILTER_FAVORITES_ONLY = "filter_favorites_only";

    private SharedPreferences mainPrefs;

    private Map<com.warriorfitapp.model.v2.AccountType, Integer> accountTypeStringResourceIds = new HashMap<>();

    {
        accountTypeStringResourceIds.put(com.warriorfitapp.model.v2.AccountType.LOCAL, com.warriorfitapp.mobile.R.string.user_profile_local);
        accountTypeStringResourceIds.put(com.warriorfitapp.model.v2.AccountType.GOOGLE, com.warriorfitapp.mobile.R.string.google_plus);
        accountTypeStringResourceIds.put(com.warriorfitapp.model.v2.AccountType.FACEBOOK, com.warriorfitapp.mobile.R.string.facebook);
    }

    public AppSettings(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context can't be null");
        }
        mainPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Integer getAccountTypeStringResourceId(com.warriorfitapp.model.v2.AccountType accountType) {
        return accountTypeStringResourceIds.get(accountType);
    }

    public boolean isTutorialShown() {
        return mainPrefs.getBoolean(KEY_TUTORIAL_SHOWN, false);
    }

    public void setTutorialShown(boolean tutorialShown) {
        mainPrefs.edit().putBoolean(KEY_TUTORIAL_SHOWN, tutorialShown).apply();
    }

    public boolean isFirstLaunch() {
        return mainPrefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void setFirstLaunch(boolean firstLaunch) {
        mainPrefs.edit().putBoolean(KEY_FIRST_LAUNCH, firstLaunch).apply();
    }

    public void saveUserToken(String token) {
        mainPrefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getUserToken() {
        return mainPrefs.getString(KEY_TOKEN, "");
    }

    public boolean hasUserToken() {
        return mainPrefs.contains(KEY_TOKEN);
    }

    public void setSystemOfMeasurement(SystemOfMeasurement som) {
        mainPrefs.edit().putString(KEY_SYSTEM_OF_MEASUREMENT, som.name()).apply();
    }

    public SystemOfMeasurement getSystemOfMeasurement() {
        return SystemOfMeasurement.valueOf(mainPrefs.getString(KEY_SYSTEM_OF_MEASUREMENT, SystemOfMeasurement.US.name()));
    }

    public void setDateFormatType(DateFormat df) {
        mainPrefs.edit().putString(KEY_DATE_FORMAT, df.name()).apply();
    }

    public DateFormat getDateFormat() {
        return DateFormat.valueOf(mainPrefs.getString(KEY_DATE_FORMAT, DateFormat.US.name()));
    }

    public void setDrawerOpened() {
        mainPrefs.edit().putBoolean(KEY_DRAWER_OPENED, true).apply();
    }

    public boolean isDrawerOpened() {
        return mainPrefs.getBoolean(KEY_DRAWER_OPENED, false);
    }

    public void setGoalsPopupShown() {
        mainPrefs.edit().putBoolean(KEY_SHOW_GOALS_POPUP, true).apply();
    }

    public void setFilterFavoritesOnly(boolean favoritesOnly) {
        mainPrefs.edit().putBoolean(KEY_FILTER_FAVORITES_ONLY, favoritesOnly).apply();
    }

    public boolean isFilterFavoritesOnly() {
        return mainPrefs.getBoolean(KEY_FILTER_FAVORITES_ONLY, false);
    }

    public void registerMainSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mainPrefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterMainSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mainPrefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public boolean isGoalsPopupShown() {
        return mainPrefs.getBoolean(KEY_SHOW_GOALS_POPUP, false);
    }

    public enum SystemOfMeasurement {
        METRIC(com.warriorfitapp.mobile.R.string.unit_kg, com.warriorfitapp.mobile.R.string.unit_km, com.warriorfitapp.mobile.R.string.unit_meters, com.warriorfitapp.mobile.R.string.unit_cm, com.warriorfitapp.mobile.R.string.txt_units_sample_metric, com.warriorfitapp.mobile.R.string.unit_speed_km_hr, com.warriorfitapp.mobile.R.string.unit_pace_minute_per_km), US(com.warriorfitapp.mobile.R.string.unit_pounds, com.warriorfitapp.mobile.R.string.unit_mile, com.warriorfitapp.mobile.R.string.unit_feets, com.warriorfitapp.mobile.R.string.unit_inches, com.warriorfitapp.mobile.R.string.txt_units_sample_us, com.warriorfitapp.mobile.R.string.unit_speed_miles_hr, com.warriorfitapp.mobile.R.string.unit_pace_minute_per_mile);

        private int weightUnitResource;
        private int distanceUnitResource;
        private int altitudeUnitResource;
        private int bodyHeightUnitResource;
        private int unitsSampleResource;
        private int speedUnitResource;
        private int paceUnitResource;


        SystemOfMeasurement(int weightUnitResource, int distanceUnitResource, int altitudeUnitResource, int bodyHeightUnitResource, int unitsSampleResource, int speedUnitResource, int paceUnitResource) {
            this.weightUnitResource = weightUnitResource;
            this.distanceUnitResource = distanceUnitResource;
            this.altitudeUnitResource = altitudeUnitResource;
            this.bodyHeightUnitResource = bodyHeightUnitResource;
            this.unitsSampleResource = unitsSampleResource;
            this.speedUnitResource = speedUnitResource;
            this.paceUnitResource = paceUnitResource;
        }

        public int getWeightUnitResource() {
            return weightUnitResource;
        }

        public int getDistanceUnitResource() {
            return distanceUnitResource;
        }

        public int getHeightPrimaryUnitResource() {
            return altitudeUnitResource;
        }

        public int getHeightSecondaryUnitResource() {
            return bodyHeightUnitResource;
        }

        public int getUnitsSampleResource() {
            return unitsSampleResource;
        }

        public int getSpeedUnitResource() {
            return speedUnitResource;
        }

        public int getPaceUnitResource() {
            return paceUnitResource;
        }
    }

    public enum DateFormat {

        EU(com.warriorfitapp.mobile.R.string.date_format_metric), US(com.warriorfitapp.mobile.R.string.date_format_us);

        private int formatResource;

        DateFormat(int formatResource) {
            this.formatResource = formatResource;
        }

        public int getFormatResource() {
            return formatResource;
        }

    }

}
