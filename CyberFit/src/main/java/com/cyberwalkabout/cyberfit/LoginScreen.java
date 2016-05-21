package com.cyberwalkabout.cyberfit;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.ProgressDialogFragment;
import com.bugsense.trace.BugSenseHandler;
import com.cyberwalkabout.cyberfit.content.ContentProviderAdapter;
import com.cyberwalkabout.cyberfit.flurry.FlurryAdapter;
import com.cyberwalkabout.cyberfit.model.v2.AccountType;
import com.cyberwalkabout.cyberfit.model.v2.SocialProfile;
import com.cyberwalkabout.cyberfit.model.v2.User;
import com.cyberwalkabout.cyberfit.util.Const;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Maria Dzyokh
 */
public class LoginScreen extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSIONS_REQUEST_GET_ACCOUNTS = 0x01;

    private static final String TAG = LoginScreen.class.getSimpleName();

    public static final DateFormat GOOGLE_PLUS_BIRTHDAY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat FACEBOOK_BIRTHDAY_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    static {
        TimeZone timeZone = TimeZone.getTimeZone("GMT+0");
        GOOGLE_PLUS_BIRTHDAY_DATE_FORMAT.setTimeZone(timeZone);
        FACEBOOK_BIRTHDAY_DATE_FORMAT.setTimeZone(timeZone);
    }

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    //    private boolean accountDropped = false;
    private AppSettings appSettings;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    private boolean resetDefaultAccount = true;

    private ContentProviderAdapter contentProviderAdapter = ContentProviderAdapter.getInstance();

    // Facebook
    private LoginButton facebookLoginButton;
    private CallbackManager callbackManager;
    private ProfileTracker facebookProfileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, Const.BUGSENSE_KEY);
        setContentView(R.layout.login_screen);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .addScope(new Scope(Scopes.PLUS_ME))
                .addScope(new Scope(Scopes.EMAIL))
                .build();

        appSettings = new AppSettings(this);

        /*mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");*/

        initFacebookLogin();

        findViewById(R.id.google_login_btn).setOnClickListener(this);
        findViewById(R.id.not_now).setOnClickListener(this);
    }

    private void initFacebookLogin() {
//        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_btn);
//        facebookLoginButton.setReadPermissions("email", "public_profile", "user_birthday");
//
//        callbackManager = CallbackManager.Factory.create();
//        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.e(TAG, "Facebook: onSuccess" + loginResult);
//            }
//
//            @Override
//            public void onCancel() {
//                Log.e(TAG, "Facebook: onCancel");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.e(TAG, "Facebook: onError" + error);
//            }
//        });
//
//        facebookProfileTracker = new ProfileTracker() {
//            @Override
//            protected void onCurrentProfileChanged(Profile oldProfile,
//                                                   Profile currentProfile) {
//                if (currentProfile != null) {
//                    signInFacebook(currentProfile);
//                }
//            }
//        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_login_btn:
                googleLoginButtonClicked();
                break;
            case R.id.not_now:
                FlurryAdapter.getInstance().socialNetworkLogin("Not Now"); // User chose not to log in
                onBackPressed();
                break;
        }
    }

    private void googleLoginButtonClicked() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(LoginScreen.this);
            if (available != ConnectionResult.SUCCESS) {
                if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
                    GooglePlayServicesUtil.getErrorDialog(available, LoginScreen.this, REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES).show();
                } else {
                    new AlertDialog.Builder(LoginScreen.this)
                            .setMessage("Sign in with Google is not available.")
                            .setCancelable(true)
                            .create().show();
                }
            } else {
                onGooglePlusSignInClicked();
            }
        } else {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(LoginScreen.this)
                        .setMessage("This permission is required to retrieve list of your google accounts and provide selection list.")
                        .setCancelable(true)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                ActivityCompat.requestPermissions(LoginScreen.this, new String[]{Manifest.permission.GET_ACCOUNTS}, PERMISSIONS_REQUEST_GET_ACCOUNTS);
                            }
                        })
                        .create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, PERMISSIONS_REQUEST_GET_ACCOUNTS);
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_GET_ACCOUNTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    googleLoginButtonClicked();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(this, "Sorry but google login doesn't work without granted permission.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult");

        if (requestCode == REQUEST_CODE_RESOLVE_ERR) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    /**
     * Connected to google plus client
     *
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");

        if (resetDefaultAccount) {
            Log.d(TAG, "resetDefaultAccount");
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.reconnect();

            // forceToLogoutGooglePlus();

            resetDefaultAccount = false;
        } else {
            // onConnected indicates that an account was selected on the device, that the selected
            // account has granted any requested permissions to our app and that we were able to
            // establish a service connection to Google Play services.
            Log.d(TAG, "onConnected:" + connectionHint);

            mShouldResolve = false;

            final String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);

            Log.d(TAG, "accountName=" + accountName);

            if (!TextUtils.isEmpty(accountName)) {
                signInGoogleAccount(accountName);
            } else {
                Log.w(TAG, "Account name not available");
            }
        }
    }

    private void forceToLogoutGooglePlus() {
        Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status s) {
                        Log.e(TAG, "User access revoked!");
                        mGoogleApiClient.reconnect();
                    }
                });
    }

    private void signInGoogleAccount(final String accountName) {
        // TODO: to speedup process we can request access token after set current account
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ProgressDialogFragment.createBuilder(LoginScreen.this, getSupportFragmentManager()).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String token = null;
                try {
                    token = GoogleAuthUtil.getToken(LoginScreen.this,
                            accountName,
                            "oauth2:" +
                                    Scopes.PLUS_LOGIN + " " +
                                    Scopes.PLUS_ME +
                                    " https://www.googleapis.com/auth/userinfo.email" +
                                    " https://www.googleapis.com/auth/userinfo.profile");
                } catch (IOException transientEx) {
                    // Network or server error, try later
                    Log.e(TAG, transientEx.toString());
                } catch (UserRecoverableAuthException e) {
                    // Recover (with e.getIntent())
                    Log.e(TAG, e.toString());
                    Intent recover = e.getIntent();
                    startActivityForResult(recover, REQUEST_CODE_SIGN_IN);
                } catch (GoogleAuthException authEx) {
                    // The call is not ever expected to succeed
                    // assuming you have already verified that
                    // Google Play services is installed.
                    Log.e(TAG, authEx.toString());
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                final AccountType accountType = AccountType.GOOGLE;

                final User user = new User();
                user.setUsername(accountName);
                user.setAccountType(accountType);

                final SocialProfile socialProfile = new SocialProfile();
                socialProfile.setEmail(accountName);
                socialProfile.setType(accountType);

                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                if (currentPerson != null) {
                    user.setDisplayName(currentPerson.getDisplayName());
                    if (currentPerson.hasImage()) {
                        user.setImageUri(getImageUrl(currentPerson));
                    }

                    if (currentPerson.hasBirthday()) {
                        try {
                            Date birthday = GOOGLE_PLUS_BIRTHDAY_DATE_FORMAT.parse(currentPerson.getBirthday());
                            user.setBirthday(birthday.getTime());
                            // set age
                        } catch (ParseException ignore) {
                            Log.w(TAG, "Couldn't parse " + currentPerson.getBirthday());
                        }
                    }

                    // age range isn't precise so skip it for now
                    /*if (currentPerson.hasAgeRange()) {
                        if (currentPerson.getAgeRange().hasMin()) {
                            user.setAge(currentPerson.getAgeRange().getMin());
                        } else if (currentPerson.getAgeRange().hasMax()) {
                            user.setAge(currentPerson.getAgeRange().getMax());
                        }
                    }*/

                    if (currentPerson.hasGender()) {
                        user.setIsMale(currentPerson.getGender() == 0);
                    }

                    socialProfile.setUrl(currentPerson.getUrl());
                    socialProfile.setSocialId(currentPerson.getId());
                    socialProfile.setToken(token);
                    socialProfile.setPrimary(true);

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Log.d(TAG, "setCurrentUser " + user + "\n" + socialProfile);

                            contentProviderAdapter.setCurrentUser(getApplicationContext(), user, socialProfile);
                            FlurryAdapter.getInstance().socialNetworkLogin(accountType.name());
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            finishOk();
                        }
                    }.execute();
                } else {
                    Toast.makeText(LoginScreen.this, "Failed to login as " + accountName, Toast.LENGTH_SHORT).show();
                }
            }

            @NonNull
            private String getImageUrl(Person currentPerson) {
                String url = currentPerson.getImage().getUrl();
                if (url.indexOf("?") > 0) {
                    url = url.substring(0, url.indexOf("?"));
                }
                return url;
            }
        }.execute();
    }

    private void signInFacebook(final Profile facebookProfile) {
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(final JSONObject object, final GraphResponse response) {
                Log.d(TAG, "newMeRequest.onCompleted(object: " + object + ", response: " + response + ")");

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        AccountType accountType = AccountType.FACEBOOK;

                        User user = new User();
                        user.setAccountType(accountType);
                        user.setDisplayName(facebookProfile.getName());
                        user.setImageUri(facebookProfile.getProfilePictureUri(400, 400).toString());

                        final SocialProfile socialProfile = new SocialProfile();
                        socialProfile.setType(accountType);
                        socialProfile.setUrl(facebookProfile.getLinkUri().toString());
                        socialProfile.setSocialId(facebookProfile.getId());
                        socialProfile.setPrimary(true);
                        if (accessToken != null) {
                            socialProfile.setToken(accessToken.getToken());
                        }

                        if (response != null && response.getError() == null && object != null) {
                            Log.d(TAG, object.toString());

                            String birthdayStr = object.optString("birthday");
                            if (!TextUtils.isEmpty(birthdayStr)) {
                                try {
                                    Date birthday = FACEBOOK_BIRTHDAY_DATE_FORMAT.parse(birthdayStr);
                                    Years age = Years.yearsBetween(new LocalDate(birthday), new LocalDate());

                                    user.setBirthday(birthday.getTime());
                                    user.setAge(age.getYears());
                                } catch (ParseException e) {
                                    Log.w(TAG, e.getMessage(), e);
                                }
                            }

                            user.setIsMale("male".equals(object.optString("gender")));

                            String email = object.optString("email");

                            if (!TextUtils.isEmpty(email)) {
                                user.setUsername(email);
                                socialProfile.setEmail(email);
                            }
                        }

                        Log.d(TAG, "setCurrentUser:\n" + user + "\n" + socialProfile);
                        contentProviderAdapter.setCurrentUser(getApplicationContext(), user, socialProfile);
                        FlurryAdapter.getInstance().socialNetworkLogin(accountType.name());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        LoginManager.getInstance().logOut();
                        finishOk();
                    }
                }.execute();
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,birthday,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void finishOk() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                //showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            //showSignedOutUI();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BugSenseHandler.closeSession(this);
        if (facebookProfileTracker != null) {
            facebookProfileTracker.stopTracking();
        }
    }

    private void onGooglePlusSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }
}
