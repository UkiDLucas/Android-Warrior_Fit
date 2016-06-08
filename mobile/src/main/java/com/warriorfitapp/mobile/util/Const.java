package com.warriorfitapp.mobile.util;

import com.warriorfitapp.mobile.R;

public class Const {
    public static final int MAX_SPEED_LIMIT = 200; // km/hr

    public static final String ACTION_GOALS_UPDATE = "ACTION_GOALS_UPDATE";

    public static final String BUGSENSE_KEY = "826d0b50";

    public static final String BROADCAST_SIGN_IN_COMPLETED = "com.cyberwalkabout.cyberfit.BROADCAST_SIGN_IN_COMPLETED";
    public static final String BROADCAST_EDIT_PROFILE_COMPLETED = "com.cyberwalkabout.cyberfit.BROADCAST_EDIT_PROFILE_COMPLETED";
    public static final String BROADCAST_DATA_LOADED = "com.cyberwalkabout.cyberfit.BROADCAST_DATA_LOADED";
    public static final String BROADCAST_RATING_LOADED = "com.cyberwalkabout.cyberfit.BROADCAST_RATING_LOADED";
    public static final String BROADCAST_RELOAD_PEOPLE_LIST = "com.cyberwalkabout.cyberfit.BROADCAST_RELOAD_PEOPLE_LIST";

    public static final String USER = "user";
    public static final String CAN_EDIT = "can_edit";

    public static final String PROGRAM = "program";
    public static final String EXERCISE = "exercise";
    public static final String EXTRA_AUTHOR_NAME = "author_name";
    public static final String EXERCISE_SESSION = "exercise_history_record";
    public static final String EXERCISE_SESSION_ID = "exercise_session_id";
    public static final String IS_FAVORITE = "is_favorite";
    public static final String RECEIVER = "receiver";
    public static final String EXERCISE_ID = "exercise_id";
    public static final String STATE = "state";
    public static final String TYPE = "type";

    public static final String SEARCH_KEYWORD = "search";
    public static final String FAVORITES_ONLY = "favorites_only";
    public static final String PROGRAM_ID = "program_id";

    public static final String UPDATE_MAX_AND_AVG_VALUES = "update_max_and_avg_values";

    public static final String TODAY_ONLY = "today_only";

    public static enum Permissions {
        CAN_EDIT(R.string.can_edit_profile), CAN_SEE_AGE(R.string.can_see_age), CAN_SEE_WEIGHT(R.string.can_see_weight), CAN_SEE_HEIGHT(R.string.can_see_height), CAN_SEE_HISTORY(R.string.can_see_history), CAN_SEE_GOALS(R.string.can_see_goals), CAN_SEE_SCHEDULE(R.string.can_see_schedule), CAN_SEE_MEASUREMENTS(R.string.can_see_measurements), CAN_SEE_PHOTOS(R.string.can_see_photos), CAN_SEE_BODY_FAT(R.string.can_see_body_fat), CAN_SEE_DESIRED_BODY_FAT(R.string.can_see_desired_body_fat), CAN_BE_NOTIFIED(R.string.can_be_notified);

        private int displayName;

        private Permissions(int displayName) {
            this.displayName = displayName;
        }

        public int getDisplayName() {
            return displayName;
        }

        public void setDisplayName(int displayName) {
            this.displayName = displayName;
        }
    }
}
