package com.cyberwalkabout.cyberfit.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.cyberwalkabout.cyberfit.AppSettings;
import com.cyberwalkabout.cyberfit.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Maria Dzyokh
 */
public class PhotoComparisonFragment extends Fragment implements View.OnClickListener, ISimpleDialogListener {
    //RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener,

    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_SET_PHOTO_PRIVACY = 4;

    private AppSettings appSettings;
    private SimpleDateFormat dateFormat;

    private RadioGroup photoTypeSelector;
    private Button dateLeftBtn;
    private Button dateRightBtn;
    private ImageView imageLeft;
    private ImageView imageRight;
//    private SwitchCompat privacySwitchLeft;
//    private SwitchCompat privacySwitchRight;

    // FIXME: 8/11/15
    /*private List<UserPhoto> userPhotos;
    private UserPhoto photoLeft;
    private UserPhoto photoRight;

    private UserPhoto.PhotoType currentPhotoType = UserPhoto.PhotoType.FRONT;*/

//    private DisplayImageOptions displayOptions;

    private Uri currentPictureUri;

    private Dialog datesPopup;

    // private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photo_comparison_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // FIXME: 8/11/15
        /*if (getActivity().getIntent().hasExtra(Const.EXTRA_USER)) {
            user = (User) getActivity().getIntent().getSerializableExtra(Const.EXTRA_USER);
        } else {
            user = User.get();
        }
        setHasOptionsMenu(User.isMe(user.getServerId()));*/

//        displayOptions = ((CyberFitApp) getActivity().getApplication()).getDisplayUserPhotoOptions();

        appSettings = new AppSettings(getActivity());

        // date format is used to show when the photo was taken, user might take photo on weekly or monthly basis
        dateFormat = new SimpleDateFormat(getString(appSettings.getDateFormat().getFormatResource())); // no need for time + " hh:mm aa"

        //photoTypeSelector = (RadioGroup) getView().findViewById(R.id.photo_type_selector);
        //photoTypeSelector.setOnCheckedChangeListener(this);

        //privacySwitchLeft = (SwitchCompat) getView().findViewById(R.id.privacy_left);
        //privacySwitchLeft.setOnCheckedChangeListener(this);
        //privacySwitchRight = (SwitchCompat) getView().findViewById(R.id.privacy_right);
        //privacySwitchRight.setOnCheckedChangeListener(this);

        imageLeft = (ImageView) getView().findViewById(R.id.image_left);
        imageRight = (ImageView) getView().findViewById(R.id.image_right);

        dateLeftBtn = (Button) getView().findViewById(R.id.btn_date_left);
        dateLeftBtn.setOnClickListener(this);
        dateRightBtn = (Button) getView().findViewById(R.id.btn_date_right);
        dateRightBtn.setOnClickListener(this);

        //initUserHeader();

//        ScheduleEntry.getNextSessionStrAsync(User.get().getServerId(), new BaseModel.AsyncCallback<String>() {
//            @Override
//            public void callback(String data) {
//                if (getView() != null) {
//                    ((TextView) getView().findViewById(R.id.txt_next_session)).setText(data);
//                }
//            }
//        });

        loadData();
        loadUserPhotoList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.set_photo_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_set_photo) {
            Intent pickIntent = new Intent();
            pickIntent.setType("image/*");
            pickIntent.setAction(Intent.ACTION_GET_CONTENT);
            Intent takePictureIntent = createTakePictureIntent();
            Intent chooserIntent = Intent.createChooser(pickIntent, getString(R.string.photo_chooser_dialog_title));
            if (takePictureIntent != null) {
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePictureIntent});
            }
            startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_IMAGE_CAPTURE == requestCode && resultCode == Activity.RESULT_OK && data != null && (data.getData() != null || data.hasExtra("data"))) {
            if (data.getData() != null) {
                this.currentPictureUri = data.getData();
            } else if (data.getExtras().get("data") != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                this.currentPictureUri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), imageBitmap, generatePhotoFileName(), null));
            }
            SimpleDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager()).setMessage(getString(R.string.set_photo_privacy))
                    .setNegativeButtonText("Shareable").setPositiveButtonText("Private").setTargetFragment(this, REQUEST_SET_PHOTO_PRIVACY).show();
        }
    }

//    @Override
//    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        if (checkedId == R.id.front) {
//            currentPhotoType = UserPhoto.PhotoType.FRONT;
//        } else if (checkedId == R.id.side) {
//            currentPhotoType = UserPhoto.PhotoType.SIDE;
//        } else if (checkedId == R.id.back) {
//            currentPhotoType = UserPhoto.PhotoType.BACK;
//        }
//        loadData();
//    }

//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        switch (buttonView.getId()) {
//            case R.id.privacy_left:
//                if (photoLeft != null) {
//                    photoLeft.setPrivate(isChecked);
//                    photoLeft.save();
//                }
//                break;
//            case R.id.privacy_right:
//                if (photoRight != null) {
//                    photoRight.setPrivate(isChecked);
//                    photoRight.save();
//                }
//                break;
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_date_left:
                showDateSelectionPopup(Side.LEFT);
                break;
            case R.id.btn_date_right:
                showDateSelectionPopup(Side.RIGHT);
                break;
        }
    }

    private void loadUserPhotoList() {
        /*if (appSettings.getUserType() != AccountType.LOCAL) {
            ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);
            // FIXME: 8/11/15
            *//*GetUserPhotoListAction.invoke(getActivity(), new ApiResultReceiver(new Handler(), new ApiResultReceiver.Receiver() {
                @Override
                public void onReceiveResult(int resultCode, Bundle resultData) {
                    switch (resultCode) {
                        case ApiResultReceiver.STATUS_SUCCESS:
                            loadData();
                            break;
                        case ApiResultReceiver.STATUS_FAILURE:
                            break;
                    }
                    ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
                }
            }), user.getServerId());*//*
        }*/
    }

    private void loadData() {
        /*UserPhoto.getAllForTypeAsync(user.getServerId(), currentPhotoType.value(), new BaseModel.AsyncCallback<List<UserPhoto>>() {
            @Override
            public void callback(List<UserPhoto> data) {
                if (data != null) {
                    userPhotos = data;
                    initUI();
                }
            }
        });*/
    }

    private void initUI() {
        // FIXME: 8/11/15
        /*photoLeft = null;
        photoRight = null;
        imageLeft.setImageBitmap(null);
        imageRight.setImageBitmap(null);
        dateRightBtn.setText(getString(R.string.select_date));
        dateLeftBtn.setText(getString(R.string.select_date));
//        privacySwitchRight.setChecked(true);
//        privacySwitchLeft.setChecked(true);
        if (userPhotos.size() > 0) {
            populateUI(userPhotos.get(0), Side.RIGHT);
        }
        if (userPhotos.size() >= 2) {
            populateUI(userPhotos.get(1), Side.LEFT);
        }*/
    }

    // FIXME: 8/11/15
    /*private void populateUI(UserPhoto photo, Side side) {
        switch (side) {
            case RIGHT:
                photoRight = photo;
                dateRightBtn.setText(dateFormat.format(new Date(new Double(photo.getDateCreated()).longValue())));
                ImageLoader.getInstance().displayImage(photo.getLink(), imageRight, displayOptions);
                //privacySwitchRight.setChecked(photo.isPrivate());
                break;
            case LEFT:
                photoLeft = photo;
                dateLeftBtn.setText(dateFormat.format(new Date(new Double(photo.getDateCreated()).longValue())));
                ImageLoader.getInstance().displayImage(photo.getLink(), imageLeft, displayOptions);
                //privacySwitchLeft.setChecked(photo.isPrivate());
                break;
        }
    }*/

//    private void initUserHeader() {
//        final ImageView profileImage = (ImageView) getView().findViewById(R.id.profile_image);
//        final TextView userName = (TextView) getView().findViewById(R.id.name);
//        userName.setText(user.getUsername());
//        if (!User.isMe(user.getServerId())) {
//            getView().findViewById(R.id.me).setVisibility(View.GONE);
//            ImageLoader.getInstance().displayImage(String.format(Settings.GET_ANOTHER_USER_PROFILE_PHOTO_URL, user.getServerId()), profileImage, ((CyberFitApp) getActivity().getApplication()).getDisplayProfileImageOptions(null));
//        } else if (new AppSettings(getActivity()).getAccountType() != AppSettings.AccountType.LOCAL) {
//            final DisplayImageOptions displayOptions = ((CyberFitApp) getActivity().getApplication()).getDisplayProfileImageOptions(PhotoComparisonFragment.this.user);
//            ImageLoader.getInstance().displayImage(Settings.GET_USER_PROFILE_PHOTO_URL, profileImage, displayOptions);
//        }
//    }

    private void showDateSelectionPopup(final Side side) {
        // FIXME: 8/11/15
        /*if (userPhotos.size() == 0) {
            // no photos, do nothing
            SimpleDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager()).setMessage(R.string.no_photos_taken)
                    .setNegativeButtonText(R.string.dismiss).show();
        } else if (userPhotos.size() == 1) {
            // single photo found
            SimpleDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager()).setMessage(R.string.photo_single_photo)
                    .setNegativeButtonText(R.string.dismiss).show();
        } else {
            UserPhoto.getAllExceptGivenAsync(user.getServerId(), currentPhotoType.value(), photoLeft.getServerId(), photoRight.getServerId(), new BaseModel.AsyncCallback<List<UserPhoto>>() {
                @Override
                public void callback(List<UserPhoto> data) {
                    if (data == null || data.size() == 0) {
                        SimpleDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager()).setMessage(R.string.photo_single_photo)
                                .setPositiveButtonText(R.string.dismiss).show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Select Date:");
                        builder.setSingleChoiceItems(new DatesAdapter(data, side), -1, null);
                        datesPopup = builder.create();
                        datesPopup.show();
                    }
                }
            });
        }*/
    }

    @Override
    public void onPositiveButtonClicked(int requestCode) {
        if (requestCode == REQUEST_SET_PHOTO_PRIVACY) {
            uploadPhoto(1);
        }
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {
        if (requestCode == REQUEST_SET_PHOTO_PRIVACY) {
            uploadPhoto(0);
        }
    }

    private void uploadPhoto(int privacy) {
        /*if (appSettings.getUserType() != AccountType.LOCAL) {
            ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);
            // FIXME: 8/11/15
            *//*AddUserPhotoAction.invoke(getActivity(), new ApiResultReceiver(new Handler(), new ApiResultReceiver.Receiver() {
                @Override
                public void onReceiveResult(int resultCode, Bundle resultData) {
                    switch (resultCode) {
                        case ApiResultReceiver.STATUS_SUCCESS:
                            loadUserPhotoList();
                            break;
                        case ApiResultReceiver.STATUS_FAILURE:
                            Toast.makeText(getActivity(), resultData.getString(ApiIntent.EXTRA_ERROR_MESSAGE), Toast.LENGTH_LONG).show();
                            break;
                    }
                    ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
                }
            }), currentPictureUri, currentPictureUri.getLastPathSegment(), currentPhotoType.value(), privacy);*//*
        } else {

            // TODO: this should be displayed BEFORE you attempt to take photo
            Toast.makeText(getActivity(), "You need to login first!", Toast.LENGTH_SHORT).show();
        }*/
    }

    private Intent createTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            return takePictureIntent;
        }
        return null;
    }

    @Override
    public void onNeutralButtonClicked(int i) {

    }

    // FIXME: 8/11/15
    /*private class DatesAdapter extends BaseAdapter {

        private Side side;
        private List<UserPhoto> items;

        public DatesAdapter(List<UserPhoto> items, Side side) {
            this.items = items;
            this.side = side;
        }


        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public UserPhoto getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return items.get(position).getServerId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, null);
            }
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(dateFormat.format(new Date(new Double(getItem(position).getDateCreated()).longValue())));

            convertView.setTag(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = Integer.parseInt(v.getTag().toString());
                    populateUI(getItem(position), side);
                    datesPopup.dismiss();
                }
            });

            return convertView;
        }
    }*/

    private enum Side {
        RIGHT, LEFT;
    }

    private String generatePhotoFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return imageFileName;
    }
}
