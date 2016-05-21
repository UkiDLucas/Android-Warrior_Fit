package com.cyberwalkabout.cyberfit.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cyberwalkabout.cyberfit.AppSettings;
import com.cyberwalkabout.cyberfit.LoginScreen;
import com.cyberwalkabout.cyberfit.R;
import com.cyberwalkabout.cyberfit.content.ContentProviderAdapter;
import com.cyberwalkabout.cyberfit.db.sqlite.schema.table.UserTable;
import com.cyberwalkabout.cyberfit.flurry.FlurryAdapter;
import com.cyberwalkabout.cyberfit.model.v2.AccountType;
import com.cyberwalkabout.cyberfit.model.v2.User;
import com.cyberwalkabout.cyberfit.model.v2.factory.UserCursorFactory;
import com.cyberwalkabout.cyberfit.util.ConvertUtils;
import com.cyberwalkabout.cyberfit.widget.dialog.AgePickerDialog;
import com.cyberwalkabout.cyberfit.widget.dialog.HeightInputDialog;
import com.cyberwalkabout.cyberfit.widget.dialog.UserWeightInputDialog;
import com.cyberwalkabout.cyberfit.widget.dialog.WaistInputDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Maria Dzyokh, Andrii Kovalov
 */
public class MyProfileFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MyProfileFragment.class.getSimpleName();

    private static final int REQUEST_ACTIVITY_LOGIN = 1;
    private static final int REQUEST_SELECT_PHOTO = 2;

    private static final String AGE_BUTTON_FORMAT = "Age: %d years";
    private static final String DESIRED_FAT_BUTTON_FORMAT = "Desired Fat: %.1f";
    private static final String CURRENT_FAT_BUTTON_FORMAT = "Current Body Fat: %.1f";
    private static final String WEIGHT_BUTTON_FORMAT = "Weight: %d %s";
    private static final String HEIGHT_BUTTON_FORMAT = "Height: %d %s %d %s";
    private static final String WAIST_BUTTON_FORMAT = "Waist: %d %s";

    private User currentUser;

    private AppSettings appSettings;

    private SwitchCompat genderSwitch;
    private SwitchCompat unitsOfMeasurementsSwitch;
    private SwitchCompat dateFormatSwitch;
    private Button btnAge;
    private Button btnWeight;
    private Button btnHeight;
    private Button btnWaist;
    private Button btnCurrentBodyFat;
    private Button btnDesiredBodyFat;
    private ImageView profileImage;
    private EditText displayName;
    private TextInputLayout displayNameLayout;

    private TextView dateFormatLabel;
    private TextView unitsLabel;
    private TextView genderLabel;

    private boolean suppressUserUpdates = false;
    private boolean displayLoginPopup = true;

    private boolean initialLoad = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        appSettings = new AppSettings(getActivity());

        btnAge = (Button) getView().findViewById(R.id.btn_age);
        btnAge.setOnClickListener(this);
        btnHeight = (Button) getView().findViewById(R.id.btn_height);
        btnHeight.setOnClickListener(this);
        btnWeight = (Button) getView().findViewById(R.id.btn_weight);
        btnWeight.setOnClickListener(this);
        btnWaist = (Button) getView().findViewById(R.id.btn_waist);
        btnWaist.setOnClickListener(this);
        btnCurrentBodyFat = (Button) getView().findViewById(R.id.btn_current_body_fat);
        btnCurrentBodyFat.setOnClickListener(this);
        btnDesiredBodyFat = (Button) getView().findViewById(R.id.btn_desired_body_fat);
        btnDesiredBodyFat.setOnClickListener(this);

        profileImage = (ImageView) getView().findViewById(R.id.profile_image);
        profileImage.setOnClickListener(this);

        displayName = (EditText) getView().findViewById(R.id.display_name);
        displayName.addTextChangedListener(this);
        displayNameLayout = (TextInputLayout) getView().findViewById(R.id.display_name_layout);

        genderSwitch = ((SwitchCompat) getView().findViewById(R.id.gender));
        genderSwitch.setOnCheckedChangeListener(MyProfileFragment.this);
        genderLabel = (TextView) getView().findViewById(R.id.gender_label);

        unitsOfMeasurementsSwitch = ((SwitchCompat) getView().findViewById(R.id.units_of_measurements));
        unitsOfMeasurementsSwitch.setChecked(appSettings.getSystemOfMeasurement() == AppSettings.SystemOfMeasurement.METRIC);
        unitsOfMeasurementsSwitch.setOnCheckedChangeListener(this);
        unitsLabel = (TextView) getView().findViewById(R.id.units_of_measurements_label);
        initUnitsLabel(appSettings.getSystemOfMeasurement());

        dateFormatSwitch = ((SwitchCompat) getView().findViewById(R.id.date_format));
        dateFormatSwitch.setChecked(appSettings.getDateFormat() == AppSettings.DateFormat.EU);
        dateFormatSwitch.setOnCheckedChangeListener(this);
        dateFormatLabel = (TextView) getView().findViewById(R.id.date_format_label);
        initDateFormatLabel(appSettings.getDateFormat());

        getLoaderManager().initLoader(ContentProviderAdapter.LOADER_USER, null, this);

        initialLoad = true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            Toast.makeText(getActivity(), "Share functionality isn't implemented in current version", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.action_accounts) {
            startActivityForResult(new Intent(getActivity(), LoginScreen.class), REQUEST_ACTIVITY_LOGIN);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (REQUEST_ACTIVITY_LOGIN == requestCode && resultCode == Activity.RESULT_OK) {
            getLoaderManager().restartLoader(ContentProviderAdapter.LOADER_USER, null, this);
        }

        if (REQUEST_SELECT_PHOTO == requestCode && resultCode == Activity.RESULT_OK) {
            if (data != null && (data.getData() != null || data.hasExtra("data"))) {
                new AsyncTask<Void, Void, Uri>() {
                    @Override
                    protected Uri doInBackground(Void... params) {
                        final Uri imageUri;
                        if (data.getData() != null) {
                            imageUri = data.getData();
                        } else {
                            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                            //TODO Uki: taking photo crashes the app
                            imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), imageBitmap, generatePhotoFileName(), null));
                        }

                        ContentValues values = new ContentValues(1);
                        values.put(UserTable.COLUMN_IMAGE_URI, imageUri.toString());
                        ContentProviderAdapter.getInstance().updateUser(getActivity(), currentUser.getId(), values);

                        FlurryAdapter.getInstance().updateProfileImage();

                        return imageUri;
                    }
                }.execute();
            } else {
                Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (currentUser != null) {
            final AppSettings.SystemOfMeasurement som = appSettings.getSystemOfMeasurement();
            switch (v.getId()) {
                case R.id.btn_age:
                    AgePickerDialog agePickerDialog = AgePickerDialog.create(getActivity(), currentUser.hasAge() ? currentUser.getAge() : 0);
                    agePickerDialog.setListener(new AgePickerDialog.OnAgeSetListener() {
                        @Override
                        public void onAgeSet(View view, int age) {
                            Log.d(TAG, "onWaistSet(" + age + ")");
                            currentUser.setAge(age);
                            btnAge.setText(String.format(AGE_BUTTON_FORMAT, age));

                            new AsyncTask<User, Void, Void>() {
                                @Override
                                protected Void doInBackground(User... params) {
                                    ContentValues values = new ContentValues(1);
                                    values.put(UserTable.COLUMN_AGE, params[0].getAge());
                                    ContentProviderAdapter.getInstance().updateUser(getActivity(), currentUser.getId(), values);
                                    return null;
                                }
                            }.execute(currentUser);
                        }
                    });
                    agePickerDialog.show(getActivity().getSupportFragmentManager(), AgePickerDialog.TAG);
                    break;
                case R.id.btn_height:
                    double height = 0;
                    if (currentUser.hasHeight()) {
                        height = som == AppSettings.SystemOfMeasurement.METRIC ? currentUser.getHeight() : ConvertUtils.cmToInch(currentUser.getHeight());
                    }
                    HeightInputDialog heightInputDialog = HeightInputDialog.create(getActivity(), height);
                    heightInputDialog.setListener(new HeightInputDialog.OnHeightSetListener() {
                        @Override
                        public void onHeightSet(View view, double height) {
                            if (som == AppSettings.SystemOfMeasurement.US) {
                                currentUser.setHeight(ConvertUtils.inchToCm(height));
                            } else {
                                currentUser.setHeight(height);
                            }
                            btnHeight.setText(getHeightFormatted(currentUser.getHeight()));

                            new AsyncTask<User, Void, Void>() {
                                @Override
                                protected Void doInBackground(User... params) {
                                    ContentValues values = new ContentValues(1);
                                    values.put(UserTable.COLUMN_HEIGHT, params[0].getHeight());
                                    ContentProviderAdapter.getInstance().updateUser(getActivity(), currentUser.getId(), values);
                                    return null;
                                }
                            }.execute(currentUser);
                        }
                    });
                    heightInputDialog.show(getActivity().getSupportFragmentManager(), HeightInputDialog.TAG);
                    break;
                case R.id.btn_weight:
                    int weightConverted = 0;
                    if (currentUser.hasWeight()) {
                        weightConverted = (int) Math.round(som == AppSettings.SystemOfMeasurement.METRIC ? currentUser.getWeight() : ConvertUtils.kgToLbs(currentUser.getWeight()));
                    }
                    UserWeightInputDialog userWeightInputDialog = UserWeightInputDialog.create(getActivity(), weightConverted);
                    userWeightInputDialog.setListener(new UserWeightInputDialog.OnWeightSetListener() {
                        @Override
                        public void onWeightSet(View v, float weight, String unit) {
                            if (som == AppSettings.SystemOfMeasurement.US) {
                                currentUser.setWeight(ConvertUtils.lbsToKg(weight));
                            } else {
                                currentUser.setWeight((double) weight);
                            }
                            btnWeight.setText(getWeightFormatted(currentUser.getWeight()));

                            new AsyncTask<User, Void, Void>() {
                                @Override
                                protected Void doInBackground(User... params) {
                                    ContentValues values = new ContentValues(1);
                                    values.put(UserTable.COLUMN_WEIGHT, params[0].getWeight());
                                    ContentProviderAdapter.getInstance().updateUser(getActivity(), currentUser.getId(), values);
                                    return null;
                                }
                            }.execute(currentUser);
                        }
                    });
                    userWeightInputDialog.show(getActivity().getSupportFragmentManager(), UserWeightInputDialog.TAG);
                    break;
                case R.id.btn_waist:
                    int waist = 0;
                    if (currentUser.hasWaist()) {
                        waist = (int) (som == AppSettings.SystemOfMeasurement.METRIC ? currentUser.getWaist() : ConvertUtils.cmToInch(currentUser.getWaist()));
                    }
                    WaistInputDialog waistInputDialog = WaistInputDialog.create(getActivity(), waist);
                    waistInputDialog.setListener(new WaistInputDialog.OnWaistSetListener() {
                        @Override
                        public void onWaistSet(View view, int waist) {
                            if (som == AppSettings.SystemOfMeasurement.US) {
                                currentUser.setWaist(ConvertUtils.inchToCm(waist));
                            } else {
                                currentUser.setWaist((double) waist);
                            }
                            btnWaist.setText(getWaistFormatted(currentUser.getWaist()));

                            new AsyncTask<User, Void, Void>() {
                                @Override
                                protected Void doInBackground(User... params) {
                                    ContentValues values = new ContentValues(1);
                                    values.put(UserTable.COLUMN_WAIST, params[0].getWaist());
                                    ContentProviderAdapter.getInstance().updateUser(getActivity(), currentUser.getId(), values);
                                    return null;
                                }
                            }.execute(currentUser);
                        }
                    });
                    waistInputDialog.show(getActivity().getSupportFragmentManager(), WaistInputDialog.TAG);
                    break;
                case R.id.profile_image:
                    Intent pickIntent = new Intent();
                    pickIntent.setType("image/*");
                    pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                    Intent[] intents = new Intent[]
                            {new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        /*,new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image")*/
                            };

                    Intent chooserIntent = Intent.createChooser(pickIntent, getString(R.string.photo_chooser_dialog_title));
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
                    startActivityForResult(chooserIntent, REQUEST_SELECT_PHOTO);
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (currentUser != null) {
            switch (buttonView.getId()) {
                case R.id.date_format:
                    AppSettings.DateFormat dateFormatType = isChecked ? AppSettings.DateFormat.EU : AppSettings.DateFormat.US;
                    appSettings.setDateFormatType(dateFormatType);
                    initDateFormatLabel(dateFormatType);
                    break;
                case R.id.gender:
                    currentUser.setIsMale(isChecked);

                    if (currentUser.getAccountType() == AccountType.LOCAL) {
                        int defaultDrawable = currentUser.isMale() ? R.drawable.profile_image_stub_male : R.drawable.profile_image_stub_female;
                        profileImage.setImageResource(defaultDrawable);
                    }

                    genderLabel.setText(currentUser.isMale() ? R.string.switch_text_male : R.string.switch_text_female);
                    break;
                case R.id.units_of_measurements:
                    AppSettings.SystemOfMeasurement units = isChecked ? AppSettings.SystemOfMeasurement.METRIC : AppSettings.SystemOfMeasurement.US;
                    appSettings.setSystemOfMeasurement(units);

                    initUnitsLabel(units);

                    btnWeight.setText(currentUser.hasWeight() ? getWeightFormatted(currentUser.getWeight()) : getString(R.string.btn_weight_default));
                    btnHeight.setText(currentUser.hasHeight() ? getHeightFormatted(currentUser.getHeight()) : getString(R.string.btn_height_default));
                    btnWaist.setText(currentUser.hasWaist() ? getWaistFormatted(currentUser.getWaist()) : getString(R.string.btn_waist_default));
                    break;
            }
        }
    }

    private void initUnitsLabel(AppSettings.SystemOfMeasurement units) {

        String unitsName = getString(units == AppSettings.SystemOfMeasurement.METRIC ? R.string.switch_text_metric : R.string.switch_text_us) + " " + getString(R.string.units_lower) + " ";
        String exampleUnits = "(" + getString(appSettings.getSystemOfMeasurement().getUnitsSampleResource()) + ")";

        SpannableString text = new SpannableString(unitsName + exampleUnits);
        text.setSpan(new RelativeSizeSpan(0.75f), unitsName.length(), text.length(), 0);
        text.setSpan(new StyleSpan(Typeface.ITALIC), unitsName.length(), text.length(), 0);
        text.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.tertiary_text_light)), unitsName.length(), text.length(), 0);

        unitsLabel.setText(text);
    }

    private void initDateFormatLabel(AppSettings.DateFormat dateFormatType) {
        String dateFormatPrefix = getString(R.string.txt_date_format) + " ";
        String dateFormat = new SimpleDateFormat(getString(dateFormatType.getFormatResource())).format(new Date());

        SpannableString text = new SpannableString(dateFormatPrefix + dateFormat);
        text.setSpan(new RelativeSizeSpan(0.75f), dateFormatPrefix.length(), text.length(), 0);
        text.setSpan(new StyleSpan(Typeface.ITALIC), dateFormatPrefix.length(), text.length(), 0);
        text.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.tertiary_text_light)), dateFormatPrefix.length(), text.length(), 0);

        dateFormatLabel.setText(text);
    }

    private String getWeightFormatted(double weight) {
        AppSettings.SystemOfMeasurement som = appSettings.getSystemOfMeasurement();
        long weightRounded = Math.round(som == AppSettings.SystemOfMeasurement.METRIC ? weight : ConvertUtils.kgToLbs(weight));
        return String.format(WEIGHT_BUTTON_FORMAT, weightRounded, getString(som.getWeightUnitResource()));
    }

    private String getHeightFormatted(double height) {
        AppSettings.SystemOfMeasurement som = appSettings.getSystemOfMeasurement();
        int h1 = 0;
        double h2 = 0;
        switch (som) {
            case METRIC:
                h1 = (int) ConvertUtils.cmToMeters(height);
                h2 = height - h1 * 100;
                break;
            case US:
                double inches = ConvertUtils.cmToInch(height);
                double feets = ConvertUtils.inchesToFeets(inches);
                h1 = (int) feets;
                h2 = inches - ConvertUtils.feetsToInches(h1);
                break;
        }
        return String.format(HEIGHT_BUTTON_FORMAT, h1, getString(som.getHeightPrimaryUnitResource()), Math.round(h2), getString(som.getHeightSecondaryUnitResource()));
    }

    private String getWaistFormatted(double waist) {
        AppSettings.SystemOfMeasurement som = appSettings.getSystemOfMeasurement();
        int value = (int) waist;
        if (som == AppSettings.SystemOfMeasurement.US) {
            value = (int) ConvertUtils.cmToInch(waist);
        }
        String units = som == AppSettings.SystemOfMeasurement.US ? getString(R.string.unit_inches) : getString(R.string.unit_cm);

        return String.format(WAIST_BUTTON_FORMAT, value, units);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
        if (!suppressUserUpdates && currentUser != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(UserTable.COLUMN_DISPLAY_NAME, text.toString());
            ContentProviderAdapter.getInstance().updateUser(getContext(), currentUser.getId(), contentValues);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private String generatePhotoFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "JPEG_" + timeStamp + "_";
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ContentProviderAdapter.LOADER_USER) {
            return ContentProviderAdapter.getInstance().loaderUser(getContext());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == ContentProviderAdapter.LOADER_USER) {
            if (data != null && data.moveToFirst()) {
                suppressUserUpdates = true;
                currentUser = UserCursorFactory.getInstance().create(data);
                Log.d(TAG, "Loaded user:\n" + currentUser);

                initUserProfileView();

                if (displayLoginPopup && currentUser.getAccountType() == AccountType.LOCAL) {
                    displayLoginPopup = false;
                    startActivityForResult(new Intent(getActivity(), LoginScreen.class), REQUEST_ACTIVITY_LOGIN);
                }
                suppressUserUpdates = false;

                if (!initialLoad) {
                    FlurryAdapter.getInstance().updateProfile(currentUser, appSettings.getSystemOfMeasurement(), appSettings.getDateFormat());
                }
            } else {
                if (displayLoginPopup) {
                    displayLoginPopup = false;
                    startActivityForResult(new Intent(getActivity(), LoginScreen.class), REQUEST_ACTIVITY_LOGIN);
                }
            }

            initialLoad = false;
        }
    }

    private void initUserProfileView() {
        int selection = displayName.getSelectionStart();
        displayName.setText(currentUser.getDisplayName());
        displayName.setSelection(selection);

        genderSwitch.setChecked(currentUser.isMale());

        btnAge.setText(currentUser.hasAge() ? String.format(AGE_BUTTON_FORMAT, currentUser.getAge()) : getString(R.string.btn_age_default));
        btnWeight.setText(currentUser.hasWeight() ? getWeightFormatted(currentUser.getWeight()) : getString(R.string.btn_weight_default));
        btnHeight.setText(currentUser.hasHeight() ? getHeightFormatted(currentUser.getHeight()) : getString(R.string.btn_height_default));
        btnWaist.setText(currentUser.hasWaist() ? getWaistFormatted(currentUser.getWaist()) : getString(R.string.btn_waist_default));

        btnCurrentBodyFat.setText(currentUser.hasCurrentBodyFat() ? String.format(CURRENT_FAT_BUTTON_FORMAT, currentUser.getCurrentBodyFat()) + "%" : getString(R.string.btn_current_body_fat_default));

        btnCurrentBodyFat.setVisibility(View.GONE);
        btnDesiredBodyFat.setVisibility(View.GONE);

        if (currentUser.getAccountType() != AccountType.LOCAL) {
            displayNameLayout.setHint(getString(R.string.login_view_text, getString(appSettings.getAccountTypeStringResourceId(currentUser.getAccountType()))));
        }

        displayUserProfileImage();
    }

    private void displayUserProfileImage() {
        int defaultDrawable = currentUser.isMale() ? R.drawable.profile_image_stub_male : R.drawable.profile_image_stub_female;
        String imageUri = currentUser.getImageUri();

        if (!TextUtils.isEmpty(imageUri)) {
            Glide.with(getActivity())
                    .load(imageUri)
                    .dontAnimate()
                    .placeholder(defaultDrawable)
                    .centerCrop()
                    .into(profileImage);
        } else {
            profileImage.setImageResource(defaultDrawable);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
