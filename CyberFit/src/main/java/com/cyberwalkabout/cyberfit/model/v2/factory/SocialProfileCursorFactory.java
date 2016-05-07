package com.cyberwalkabout.cyberfit.model.v2.factory;

import android.database.Cursor;
import android.text.TextUtils;

import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.SocialProfileTable;
import com.cyberwalkabout.cyberfit.model.v2.AccountType;
import com.cyberwalkabout.cyberfit.model.v2.ModelFactory;
import com.cyberwalkabout.cyberfit.model.v2.SocialProfile;

/**
 * @author Andrii Kovalov
 */
public class SocialProfileCursorFactory implements ModelFactory<SocialProfile, Cursor> {

    private static final SocialProfileCursorFactory INSTANCE = new SocialProfileCursorFactory();

    public static SocialProfileCursorFactory getInstance() {
        return INSTANCE;
    }

    private SocialProfileCursorFactory() {
    }

    @Override
    public SocialProfile create(Cursor cursor) {
        SocialProfile socialProfile = new SocialProfile();
        socialProfile.setId(cursor.getLong(cursor.getColumnIndex(SocialProfileTable.COLUMN_ID)));
        socialProfile.setEmail(cursor.getString(cursor.getColumnIndex(SocialProfileTable.COLUMN_EMAIL)));
        socialProfile.setToken(cursor.getString(cursor.getColumnIndex(SocialProfileTable.COLUMN_TOKEN)));
        socialProfile.setPrimary(cursor.getInt(cursor.getColumnIndex(SocialProfileTable.COLUMN_IS_PRIMARY)) == 1);
        socialProfile.setSocialId(cursor.getString(cursor.getColumnIndex(SocialProfileTable.COLUMN_SOCIAL_ID)));
        String type = cursor.getString(cursor.getColumnIndex(SocialProfileTable.COLUMN_TYPE));
        if (!TextUtils.isEmpty(type)) {
            socialProfile.setType(AccountType.valueOf(type));
        }
        socialProfile.setUrl(cursor.getString(cursor.getColumnIndex(SocialProfileTable.COLUMN_URL)));
        socialProfile.setUserId(cursor.getLong(cursor.getColumnIndex(SocialProfileTable.COLUMN_USER_ID)));
        return socialProfile;
    }
}
