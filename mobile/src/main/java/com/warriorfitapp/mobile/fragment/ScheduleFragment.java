package com.warriorfitapp.mobile.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.warriorfitapp.mobile.R;
import com.warriorfitapp.mobile.widget.ScheduleList;

import java.text.SimpleDateFormat;

public class ScheduleFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ImageButton shareScheduleBtn;
    private View headerEmpty;
    private TextView headerEmptyDesc;
    private TextView subheaderEmptyDesc;
    private View enterDataContainer;
    private Button startTimeBtn;
    private Button endTimeBtn;
    private Button dayBtn;
    private CheckBox repeatCheckBox;
    private ScheduleList scheduleList;

    private CheckBox remind5minutesCb;
    private CheckBox remind10minutesCb;
    private CheckBox remind15minutesCb;
    private CheckBox remind30minutesCb;
    private CheckBox remind1hourCb;
    private CheckBox remind3hoursCb;

    private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm aa");

    private String[] daysOfWeekShort;

    // FIXME: 8/11/15
    /*private ScheduleEntry scheduleEntry;

    private User user;*/
    private boolean canEdit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schedule_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // FIXME: 8/11/15
        /*if (getActivity().getIntent().hasExtra(Const.EXTRA_USER)) {
            user = (User) getActivity().getIntent().getSerializableExtra(Const.EXTRA_USER);
            canEdit = getActivity().getIntent().getBooleanExtra(Const.CAN_EDIT, true);
        } else {
            user = User.get();
            canEdit = true;
        }*/
        setHasOptionsMenu(true);

        daysOfWeekShort = getActivity().getResources().getStringArray(R.array.day_of_week_short);

        headerEmpty = getView().findViewById(R.id.header_empty);
        headerEmptyDesc = (TextView) getView().findViewById(R.id.header_empty_description);
        subheaderEmptyDesc = (TextView) getView().findViewById(R.id.subheader_empty_description);
        enterDataContainer = getView().findViewById(R.id.new_schedule_entry);

        dayBtn = (Button) getView().findViewById(R.id.btn_day);
        startTimeBtn = (Button) getView().findViewById(R.id.btn_start_time);
        endTimeBtn = (Button) getView().findViewById(R.id.btn_end_time);
        repeatCheckBox = (CheckBox) getView().findViewById(R.id.repeat);

        scheduleList = (ScheduleList) getView().findViewById(R.id.schedule_list);

        remind5minutesCb = (CheckBox) getView().findViewById(R.id.remind_5_minutes);
        remind10minutesCb = (CheckBox) getView().findViewById(R.id.remind_10_minutes);
        remind15minutesCb = (CheckBox) getView().findViewById(R.id.remind_15_minutes);
        remind30minutesCb = (CheckBox) getView().findViewById(R.id.remind_30_minutes);
        remind1hourCb = (CheckBox) getView().findViewById(R.id.remind_1_hour);
        remind3hoursCb = (CheckBox) getView().findViewById(R.id.remind_3_hours);

        getView().findViewById(R.id.btn_done).setOnClickListener(this);

        shareScheduleBtn = (ImageButton) getView().findViewById(R.id.btn_share);
        shareScheduleBtn.setOnClickListener(this);

        SpannableStringBuilder ssb = new SpannableStringBuilder(getActivity().getString(R.string.schedule_subheader_empty));
        Bitmap plusIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_plus);
        ssb.setSpan(new ImageSpan(getActivity(), plusIcon, DynamicDrawableSpan.ALIGN_BASELINE), 13, 14, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        subheaderEmptyDesc.setText(ssb, TextView.BufferType.SPANNABLE);

        dayBtn.setOnClickListener(this);
        startTimeBtn.setOnClickListener(this);
        endTimeBtn.setOnClickListener(this);

        repeatCheckBox.setOnCheckedChangeListener(this);

        setupUserHeader();
        populateList();
        loadSchedule();
        loadNextSession();


    }

    private void setupUserHeader() {
        final ImageView profileImage = (ImageView) getView().findViewById(R.id.profile_image);
        final TextView userName = (TextView) getView().findViewById(R.id.display_name);

        // FIXME: 8/11/15
        /*userName.setText(user.getUsername());
        if (!User.isMe(user.getServerId())) {
            // FIXME: 8/11/15
            // ImageLoader.getInstance().displayImage(String.format(Settings.GET_ANOTHER_USER_PROFILE_PHOTO_URL, user.getServerId()), profileImage, ((CyberFitApp) getActivity().getApplication()).getDisplayProfileImageOptions(null));
        } else if (new AppSettings(getActivity()).getAccountType() != AppSettings.AccountType.LOCAL) {
            final DisplayImageOptions displayOptions = ((CyberFitApp) getActivity().getApplication()).getDisplayProfileImageOptions(ScheduleFragment.this.user);
            // FIXME: 8/11/15
            // ImageLoader.getInstance().displayImage(Settings.GET_USER_PROFILE_PHOTO_URL, profileImage, displayOptions);
        }*/
    }

    private void populateList() {
        // FIXME: 8/11/15
          /*ScheduleEntry.getAllForUserAsync(user.getServerId(), new BaseModel.AsyncCallback<List<ScheduleEntry>>() {
              @Override
              public void callback(List<ScheduleEntry> data) {
                  if (getActivity() != null) {
                      if (data.isEmpty()) {
                          headerEmpty.setVisibility(View.VISIBLE);
                          shareScheduleBtn.setVisibility(View.GONE);
                      } else {
                          shareScheduleBtn.setVisibility(View.VISIBLE);
                          scheduleList.setVisibility(View.VISIBLE);
                          scheduleList.setAdapter(new ScheduleList.ScheduleAdapter(getActivity(), data, new ScheduleList.OnDeleteListener() {
                              @Override
                              public void onDelete(ScheduleEntry entry, int position) {
                                  if (canEdit) {
                                      FlurryAdapter.getInstance().deleteScheduleEntry();
                                      if (entry.hasServerId()) {
                                          if (new AppSettings(getActivity()).getAccountType() != AppSettings.AccountType.LOCAL) {
                                              // FIXME: 8/11/15
                                              *//*DeleteScheduleEntriesAction.invoke(getActivity(), new ApiResultReceiver(new Handler(), new ApiResultReceiver.Receiver() {
                                                  @Override
                                                  public void onReceiveResult(int resultCode, Bundle resultData) {
                                                      switch (resultCode) {
                                                          case ApiResultReceiver.STATUS_SUCCESS:
                                                              break;
                                                          case ApiResultReceiver.STATUS_FAILURE:
                                                              break;
                                                      }
                                                  }
                                              }), entry, user.getServerId());*//*
                                          }
                                      }
                                      entry.delete();
                                      scheduleList.getAdapter().remove(position);
                                      scheduleList.getAdapter().notifyDataSetChanged();

                                      loadNextSession();
                                      if (scheduleList.getAdapter().getCount() == 0) {
                                          headerEmpty.setVisibility(View.VISIBLE);
                                          shareScheduleBtn.setVisibility(View.GONE);
                                          scheduleList.setVisibility(View.GONE);
                                      }
                                  } else {
                                      SimpleDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager()).setMessage(R.string.no_edit_permission_notice)
                                              .setPositiveButtonText(R.string.dismiss).show();
                                  }
                              }
                          }));
                      }
                  }
              }
          });*/
    }

    private void loadNextSession() {
        // FIXME: 8/11/15
        /*ScheduleEntry.getNextSessionStrAsync(user.getServerId(), new BaseModel.AsyncCallback<String>() {
            @Override
            public void callback(String data) {
                txtNextSesion.setText(data);
            }
        });*/
    }

    private void loadSchedule() {
        /*if (new AppSettings(getActivity()).getUserType() != AccountType.LOCAL) {
            ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);
            // FIXME: 8/11/15
            *//*GetScheduleAction.invoke(getActivity(), new ApiResultReceiver(new Handler(), new ApiResultReceiver.Receiver() {
                @Override
                public void onReceiveResult(int resultCode, Bundle resultData) {
                    switch (resultCode) {
                        case ApiResultReceiver.STATUS_SUCCESS:
                            populateList();
                            loadNextSession();
                            break;
                        case ApiResultReceiver.STATUS_FAILURE:
                            break;
                    }
                    ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
                }
            }), user.getServerId());*//*
        } else {
            populateList();
            loadNextSession();
        }*/
    }

    // FIXME: 8/11/15
    /*private void addScheduleEntry(ScheduleEntry entry) {
        if (new AppSettings(getActivity()).getAccountType() != AppSettings.AccountType.LOCAL) {
            // FIXME: 8/11/15
            *//*AddScheduleEntryAction.invoke(getActivity(), new ApiResultReceiver(new Handler(), new ApiResultReceiver.Receiver() {
                @Override
                public void onReceiveResult(int resultCode, Bundle resultData) {
                    switch (resultCode) {
                        case ApiResultReceiver.STATUS_SUCCESS:
                            break;
                        case ApiResultReceiver.STATUS_FAILURE:
                            break;
                    }
                }
            }), entry, entry.getId());*//*
        }
    }*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            if (canEdit) {
                if (enterDataContainer.getVisibility() == View.GONE) {
                    enterDataContainer.setVisibility(View.VISIBLE);
                    headerEmpty.setVisibility(View.GONE);
                    // FIXME: 8/11/15
                    // scheduleEntry = new ScheduleEntry();
                    clearEnterDataUI();
                }
            } else {
                SimpleDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager()).setMessage(R.string.no_edit_permission_notice)
                        .setPositiveButtonText(R.string.dismiss).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearEnterDataUI() {
        dayBtn.setText("");
        startTimeBtn.setText("");
        endTimeBtn.setText("");
        repeatCheckBox.setChecked(false);
        remind5minutesCb.setChecked(false);
        remind10minutesCb.setChecked(false);
        remind15minutesCb.setChecked(false);
        remind30minutesCb.setChecked(false);
        remind1hourCb.setChecked(false);
        remind3hoursCb.setChecked(false);
    }

    @Override
    public void onClick(View v) {
        int hourOfDay;
        int minute;
        // FIXME: 8/11/15
        /*switch (v.getId()) {
            case R.id.btn_day:
                DayOfWeekInputDialog dayOfWeekInputDialog = DayOfWeekInputDialog.create(getActivity(), scheduleEntry.getDayOfWeek());
                dayOfWeekInputDialog.setListener(new DayOfWeekInputDialog.OnDayOfWeekSetListener() {
                    @Override
                    public void onDayOfWeekSet(int dayOfWeek) {
                        scheduleEntry.setDayOfWeek(dayOfWeek);
                        dayBtn.setText(daysOfWeekShort[dayOfWeek]);
                    }
                });
                dayOfWeekInputDialog.show(getActivity().getSupportFragmentManager(), DayOfWeekInputDialog.TAG);
                break;
            case R.id.btn_start_time:
                hourOfDay = DateUtils.getHourOfDay(scheduleEntry.hasStartTime()?scheduleEntry.getStartTime():new Date());
                minute = DateUtils.getMinuteOfHour(scheduleEntry.hasStartTime()?scheduleEntry.getStartTime():new Date());
                CustomTimePicker startTimeDialog = new CustomTimePicker(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        final Date date = new DateTime().withHourOfDay(hourOfDay).withMinuteOfHour(minute).toDate();
                        startTimeBtn.setText(timeFormatter.format(date));
                        scheduleEntry.setStartTime(date);
                    }
                }, hourOfDay, minute, false, 15);
                startTimeDialog.show();
                break;
            case R.id.btn_end_time:
                if (scheduleEntry.hasEndTime()) {
                    hourOfDay = DateUtils.getHourOfDay(scheduleEntry.getEndTime());
                    minute = DateUtils.getMinuteOfHour(scheduleEntry.getEndTime());
                } else if (scheduleEntry.hasStartTime()) {
                    minute = DateUtils.getMinuteOfHour(scheduleEntry.getStartTime());
                    hourOfDay = DateUtils.getHourOfDay(scheduleEntry.getStartTime());
                    hourOfDay++;
                } else {
                    minute = DateUtils.getMinuteOfHour(new Date());
                    hourOfDay = DateUtils.getHourOfDay(new Date());
                    hourOfDay++;
                }
                CustomTimePicker endTimeDialog = new CustomTimePicker(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        final Date date = new DateTime().withHourOfDay(hourOfDay).withMinuteOfHour(minute).toDate();
                        endTimeBtn.setText(timeFormatter.format(date));
                        scheduleEntry.setEndTime(date);
                    }
                }, hourOfDay, minute, false, 15);
                endTimeDialog.show();
                break;
            case R.id.btn_done:
                saveNewScheduleEntry();
                break;
            case R.id.btn_share:
                FlurryAdapter.getInstance().shareScheduleEntry();
                shareSchedule();
                break;
        }*/
    }

    private void saveNewScheduleEntry() {
        // FIXME: 8/11/15
        /*final FragmentActivity activity = getActivity();
        if (scheduleEntry.hasDayOfWeek() && scheduleEntry.hasEndTime() && scheduleEntry.hasStartTime()) {
            if (scheduleEntry.getEndTime().getTime() >= scheduleEntry.getStartTime().getTime()) {
                scheduleEntry.setUserId(user.getServerId());
                scheduleEntry.save();

                FlurryAdapter.getInstance().addScheduleEntry();

                addScheduleEntry(scheduleEntry);

                if (User.isMe(user.getServerId())) {
                    if (remind5minutesCb.isChecked()) {
                        setupReminder(5);
                    }
                    if (remind10minutesCb.isChecked()) {
                        setupReminder(10);
                    }

                    if (remind15minutesCb.isChecked()) {
                        setupReminder(15);
                    }

                    if (remind30minutesCb.isChecked()) {
                        setupReminder(30);
                    }

                    if (remind1hourCb.isChecked()) {
                        setupReminder(60); // 1 hour
                    }

                    if (remind3hoursCb.isChecked()) {
                        setupReminder(3 * 60); // 3 hours
                    }
                }

                enterDataContainer.setVisibility(View.GONE);
                if (shareScheduleBtn.getVisibility() == View.GONE) {
                    shareScheduleBtn.setVisibility(View.VISIBLE);
                }
                populateList();
                loadNextSession();
            } else {
                Toast.makeText(activity, "Start time cannot be greater than end time", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Enter date and time", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void setupReminder(int minutes) {
        // FIXME: 8/11/15
        /*final FragmentActivity activity = getActivity();
        ScheduleEntryReminder reminder = new ScheduleEntryReminder();
        reminder.setScheduleEntryId(scheduleEntry.getId());
        reminder.setTimeToRemindBefore(minutes);

        LocalDateTime timeToRemind;
        if (scheduleEntry.isRepeat()) {
            LocalDate date = DateUtils.getNearestDateForDayOfWeekAndTime(scheduleEntry.getStartTime(), scheduleEntry.getDayOfWeek());
            timeToRemind = new LocalDateTime(date.toDate().getTime());
        } else {
            timeToRemind = new LocalDateTime(DateUtils.dateTimeWithDayOfWeek(scheduleEntry.getStartTime(), scheduleEntry.getDayOfWeek()).toDate().getTime());
        }

        timeToRemind = timeToRemind.withHourOfDay(DateUtils.getHourOfDay(scheduleEntry.getStartTime()))
                .withMinuteOfHour(DateUtils.getMinuteOfHour(scheduleEntry.getStartTime())).minusMinutes(minutes);

        final long timToRemindMillis = timeToRemind.toDate().getTime();
        reminder.setCalculatedTriggerTime(timToRemindMillis);
        reminder.save();


        Intent intent = new Intent(activity, ReminderNotificationBroadcastReceiver.class);
        intent.putExtra("reminder_id", reminder.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, (int) reminder.getId().longValue(), intent, 0);
        AlarmManager alarm = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        if (scheduleEntry.isRepeat()) {
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, timToRemindMillis, AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        } else {
            alarm.set(AlarmManager.RTC_WAKEUP, timToRemindMillis, pendingIntent);
        }*/
    }

    private void shareSchedule() {
        // FIXME: 8/11/15
        /*ScheduleEntry.getAllAsync(ScheduleEntry.class, new BaseModel.AsyncCallback<List<ScheduleEntry>>() {
            @Override
            public void callback(List<ScheduleEntry> data) {
                StringBuilder shareMessage = new StringBuilder();
                shareMessage.append(user.getUsername()).append("\n\n");
                if (!data.isEmpty()) {
                    String[] daysOfWeek = getResources().getStringArray(R.array.day_of_week);
                    SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm aa");
                    for (ScheduleEntry entry : data) {
                        shareMessage.append(daysOfWeek[entry.getDayOfWeek() - 1]).append(" ");
                        shareMessage.append("(").append(entry.isRepeat() ? "Every Week" : "One Time").append(") ");
                        shareMessage.append("at ").append(timeFormatter.format(entry.getStartTime()) + " - " + timeFormatter.format(entry.getEndTime()));
                        shareMessage.append("\n");
                    }
                }
                ShareUtils.sendMail(getActivity(), "", getString(R.string.schedules_email_subject), shareMessage.toString());
            }
        });*/
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // FIXME: 8/11/15
        /*if (scheduleEntry != null) {
            scheduleEntry.setRepeat(isChecked);
        }*/
    }
}
