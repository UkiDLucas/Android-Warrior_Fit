package com.cyberwalkabout.cyberfit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.cyberwalkabout.cyberfit.AppSettings;
import com.cyberwalkabout.cyberfit.R;
import com.cyberwalkabout.cyberfit.widget.MeasurementHistory;
import com.cyberwalkabout.cyberfit.widget.dialog.DateInputDialog;
import com.cyberwalkabout.cyberfit.widget.dialog.MeasurementInputDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Maria Dzyokh
 */
public class MeasurementsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String MEASUREMENT_TYPE_HIP = "Hip";

    // FIXME: 8/11/15
    // private User user;
    private boolean canEdit;

    private Spinner measurementTypes;

    private ImageView measurementTypeImage;
    private TextView measurementTypeDescription;

    private View enterDataContainer;
    private Button btnDate;
    private Button btnMeasurement;
    private Button btnDone;

    private MeasurementHistory measurementHistory;
    // FIXME: 8/11/15 
    // private MeasurementType currentMeasurementType;

    private Date currentDate = null;
    private double currentValue = -1;

    private boolean dateSelected = false;
    private boolean measurementSelected = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.measurements_fragment, null);
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

        measurementTypes = (Spinner) getView().findViewById(R.id.measurement_types);
        measurementTypes.setOnItemSelectedListener(this);
        populateMeasurementTypes();

        measurementTypeImage = (ImageView) getView().findViewById(R.id.measurement_type_image);
        measurementTypeDescription = (TextView) getView().findViewById(R.id.measurement_type_description);

        enterDataContainer = getView().findViewById(R.id.enter_data_container);
        btnDate = (Button) getView().findViewById(R.id.enter_date_btn);
        btnMeasurement = (Button) getView().findViewById(R.id.enter_measurement_btn);
        btnDone = (Button) getView().findViewById(R.id.btn_done);
        initDataInputContainer();

        measurementHistory = (MeasurementHistory) getView().findViewById(R.id.measurements_history);

        loadBodyMeasurements();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.measurement_types_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_measurement) {
            if (canEdit) {
                if (enterDataContainer.getVisibility() == View.GONE) {
                    enterDataContainer.setVisibility(View.VISIBLE);
                    measurementHistory.hideEmptyHeader();
                }
            } else {
                SimpleDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager()).setMessage(R.string.no_edit_permission_notice)
                        .setPositiveButtonText(R.string.dismiss).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        enterDataContainer.setVisibility(View.GONE);
        currentDate = null;
        currentValue = 0;

        btnDate.setText(getString(R.string.date_btn_title));
        btnMeasurement.setText(getString(R.string.measurement_btn_title));

        // FIXME: 8/11/15 
        /*currentMeasurementType = (MeasurementType) measurementTypes.getSelectedItem();

        measurementTypeDescription.setText(currentMeasurementType.getDescription());
        ImageLoader.getInstance().displayImage(currentMeasurementType.getImageUrl(), measurementTypeImage);*/

        populateMeasurementsHistory();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadBodyMeasurements() {
        /*if (new AppSettings(getActivity()).getUserType() != AccountType.LOCAL) {
            ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);

            // FIXME: 8/11/15
            *//*GetMeasurementsAction.invoke(getActivity(), new ApiResultReceiver(new Handler(), new ApiResultReceiver.Receiver() {
                @Override
                public void onReceiveResult(int resultCode, Bundle resultData) {
                    switch (resultCode) {
                        case ApiResultReceiver.STATUS_SUCCESS:
                            populateMeasurementsHistory();
                            break;
                        case ApiResultReceiver.STATUS_FAILURE:
                            break;
                    }
                    ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
                }
            }), user.getServerId());*//*
        }*/
    }

    private void populateMeasurementTypes() {
        // FIXME: 8/11/15 
        /*MeasurementType.getAllSortedByNameAsync(new BaseModel.AsyncCallback<List<MeasurementType>>() {
            @Override
            public void callback(List<MeasurementType> data) {
                ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                        R.layout.custom_spinner_item, data.toArray(new MeasurementType[data.size()]));
                measurementTypes.setAdapter(adapter);
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getTitle().equalsIgnoreCase(MEASUREMENT_TYPE_HIP)) {
                        measurementTypes.setSelection(i);
                        break;
                    }
                }

            }
        });*/
    }

    private void populateMeasurementsHistory() {
        // FIXME: 8/11/15
        /*BodyMeasurement.getAllForMeasurementTypeAsync(user.getServerId(), currentMeasurementType.getServerId(), new BaseModel.AsyncCallback<List<BodyMeasurement>>() {
            @Override
            public void callback(List<BodyMeasurement> data) {
                measurementHistory.setAdapter(new MeasurementHistory.MeasurementHistoryAdapter(getActivity(), data));
            }
        });*/
    }

    private void initDataInputContainer() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AppSettings appSettings = new AppSettings(getActivity());
                final AppSettings.SystemOfMeasurement som = appSettings.getSystemOfMeasurement();
                if (v.getId() == R.id.enter_date_btn) {
                    DateInputDialog dateInputDialog = DateInputDialog.create(getActivity(), currentDate);
                    dateInputDialog.setListener(new DateInputDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(Date date) {
                            dateSelected = true;
                            currentDate = date;
                            btnDate.setText(new SimpleDateFormat(getString(appSettings.getDateFormat().getFormatResource())).format(date));
                            showDoneButton();
                        }
                    });
                    dateInputDialog.show(getActivity().getSupportFragmentManager(), DateInputDialog.TAG);
                } else if (v.getId() == R.id.enter_measurement_btn) {
                    MeasurementInputDialog measurementInputDialog = MeasurementInputDialog.create(getActivity(), currentValue);
                    measurementInputDialog.setListener(new MeasurementInputDialog.OnMeasurementSetListener() {
                        @Override
                        public void onMeasurementSet(View view, float value) {
                            measurementSelected = true;
                            currentValue = value;
                            btnMeasurement.setText(String.format("%.2f " + getString(appSettings.getSystemOfMeasurement().getHeightSecondaryUnitResource()), value));
                            showDoneButton();
                        }
                    });
                    measurementInputDialog.show(getActivity().getSupportFragmentManager(), MeasurementInputDialog.TAG);
                } else if (v.getId() == R.id.btn_done) {
                    if (currentDate != null && currentValue != 0) {
                        // FIXME: 8/11/15
                        /*BodyMeasurement bodyMeasurement = new BodyMeasurement();
                        bodyMeasurement.setDate(currentDate.getTime());
                        bodyMeasurement.setValue(som == AppSettings.SystemOfMeasurement.US ? currentValue : ConvertUtils.cmToInch(currentValue));
                        bodyMeasurement.setType(currentMeasurementType.getServerId());
                        bodyMeasurement.setUserId(user.getServerId());
                        bodyMeasurement.save();

                        FlurryAdapter.getInstance().addBodyMeasurement(bodyMeasurement.getUserId(), (int) bodyMeasurement.getType(), currentMeasurementType.getTitle());*/

                        /*if (appSettings.getUserType() != AccountType.LOCAL) {
                            // FIXME: 8/11/15
                            *//*AddBodyMeasurementAction.invoke(getActivity(), new ApiResultReceiver(new Handler(), new ApiResultReceiver.Receiver() {
                                @Override
                                public void onReceiveResult(int resultCode, Bundle resultData) {
                                    switch (resultCode) {
                                        case ApiResultReceiver.STATUS_SUCCESS:
                                            break;
                                        case ApiResultReceiver.STATUS_FAILURE:
                                            break;
                                    }
                                }
                            }), bodyMeasurement);*//*
                        }*/

                        currentDate = null;
                        currentValue = -1;
                        dateSelected = false;
                        measurementSelected = false;

                        populateMeasurementsHistory();
                    }

                    enterDataContainer.setVisibility(View.GONE);
                    btnDate.setText(getString(R.string.date_btn_title));
                    btnMeasurement.setText(getString(R.string.measurement_btn_title));
                    btnDone.setVisibility(View.GONE);
                    measurementHistory.showEmptyHeader();
                }

            }
        };
        btnDate.setOnClickListener(listener);
        btnMeasurement.setOnClickListener(listener);
        btnDone.setOnClickListener(listener);
    }

    private void showDoneButton() {
        if (dateSelected && measurementSelected) {
            btnDone.setVisibility(View.VISIBLE);
        }
    }
}
