package com.cyberwalkabout.cyberfit.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class ReminderNotificationBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = ReminderNotificationBroadcastReceiver.class.getSimpleName();
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm aa");

    public ReminderNotificationBroadcastReceiver() {
        timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "onReceive");
        final long reminderId = intent.getLongExtra("reminder_id", -1);
        if (reminderId != -1) {
            // FIXME: 8/11/15
            /*new AsyncTask<Void, Void, ScheduleData>() {
                @Override
                protected ScheduleData doInBackground(Void... params) {
                    ScheduleEntryReminder reminder = ScheduleEntryReminder.load(ScheduleEntryReminder.class, reminderId);
                    ScheduleEntry scheduleEntry = null;
                    if (reminder != null) {
                        scheduleEntry = ScheduleEntry.load(ScheduleEntry.class, reminder.getScheduleEntryId());
                        if (scheduleEntry != null) {
                            if (!scheduleEntry.isRepeat()) {
                                reminder.setReminded(true);
                                reminder.save();
                            }
                        }
                    }
                    return new ScheduleData(reminder, scheduleEntry);
                }

                @Override
                protected void onPostExecute(ScheduleData scheduleData) {
                    super.onPostExecute(scheduleData);
                    if (scheduleData.scheduleEntry == null || scheduleData.reminder == null) {
                        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) reminderId, intent, PendingIntent.FLAG_NO_CREATE);
                        if (pendingIntent != null) {
                            alarm.cancel(pendingIntent);
                            pendingIntent.cancel();
                        }
                    } else {
                        notificationStatus(context, scheduleData);
                    }
                }
            }.execute();*/
        }
    }

    // FIXME: 8/11/15
    /*private class ScheduleData {
        private ScheduleEntryReminder reminder;
        private ScheduleEntry scheduleEntry;

        public ScheduleData(ScheduleEntryReminder reminder, ScheduleEntry scheduleEntry) {
            this.reminder = reminder;
            this.scheduleEntry = scheduleEntry;
        }
    }*/

    /**
     * To show notification for the alarm on time that is set as reminder
     *
     * @param context
     */
    // FIXME: 8/11/15
    /*private void notificationStatus(Context context, ScheduleData scheduleData) {
        String message = constructMessage(scheduleData);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(message);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) scheduleData.reminder.getId().longValue(), builder.build());
    }*/

    // FIXME: 8/11/15
    /*private String constructMessage(ScheduleData scheduleData) {
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append("Your training session at ")
                .append(timeFormatter.format(scheduleData.scheduleEntry.getStartTime()))
                .append(" is in ");

        if (scheduleData.reminder.getTimeToRemindBefore() < 60) {
            msgBuilder.append(scheduleData.reminder.getTimeToRemindBefore()).append(" minutes.");
        } else {
            int hours = scheduleData.reminder.getTimeToRemindBefore() / 60;

            msgBuilder.append(hours);

            if (hours > 1) {
                msgBuilder.append(" hours.");
            } else {
                msgBuilder.append(" hour.");
            }
        }
        return msgBuilder.toString();
    }*/
}
