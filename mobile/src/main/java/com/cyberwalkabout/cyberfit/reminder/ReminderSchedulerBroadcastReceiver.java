package com.cyberwalkabout.cyberfit.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// FIXME: 8/11/15
public class ReminderSchedulerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        /*new AsyncTask<Void, Void, List<ReminderInfo>>() {
            @Override
            protected List<ReminderInfo> doInBackground(Void... params) {
                List<ReminderInfo> reminderInfoList = new ArrayList<ReminderInfo>();

                final SQLiteDatabase db = Cache.openDatabase();
                Cursor cursor = null;
                try {
                    cursor = db.rawQuery("select r.Id, r.calculated_trigger_time, e.repeat from schedule_entry_reminder r join schedule_entry e on e.Id == r.schedule_entry_id", null);
                    while (cursor.moveToNext()) {
                        long reminderId = cursor.getLong(0);
                        long calculatedTriggerTime = cursor.getLong(1);
                        boolean repeat = cursor.getInt(2) != 0;

                        reminderInfoList.add(new ReminderInfo(reminderId, calculatedTriggerTime, repeat));
                    }
                } finally {
                    try {
                        cursor.close();
                    } catch (Exception e) {
                    }
                }
                return reminderInfoList;
            }

            @Override
            protected void onPostExecute(List<ReminderInfo> reminderInfoList) {
                super.onPostExecute(reminderInfoList);
                for (ReminderInfo reminderInfo : reminderInfoList) {
                    Intent i = new Intent(context, ReminderNotificationBroadcastReceiver.class);
                    i.putExtra("reminder_id", reminderInfo.reminderId);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) reminderInfo.reminderId, i, 0);
                    AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    if (reminderInfo.repeat) {
                        alarm.setRepeating(AlarmManager.RTC_WAKEUP, reminderInfo.calculatedTriggerTime, AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                    } else {
                        alarm.set(AlarmManager.RTC_WAKEUP, reminderInfo.calculatedTriggerTime, pendingIntent);
                    }
                }
            }
        }.execute();*/
    }

    class ReminderInfo {
        private long reminderId;
        private long calculatedTriggerTime;
        private boolean repeat;

        ReminderInfo(long reminderId, long calculatedTriggerTime, boolean repeat) {
            this.reminderId = reminderId;
            this.calculatedTriggerTime = calculatedTriggerTime;
            this.repeat = repeat;
        }
    }
}
