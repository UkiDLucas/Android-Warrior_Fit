package com.warriorfitapp.mobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;

/*import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;*/

/**
 * @author Maria Dzyokh
 */
public class ShareScreen extends Activity /*implements View.OnClickListener */{

    private String shareText;
    private Bitmap shareBitmap;
    private Uri bitmapUri;

    private EditText shareTextView;

    private Bundle savedInstanceState;

    private ProgressDialog progressDialog;

    /*private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
                share(session);
            } else if (state == SessionState.CLOSED_LOGIN_FAILED) {
                Toast.makeText(ShareScreen.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Request.Callback statusUpdateRequestCallback = new Request.Callback() {
        @Override
        public void onCompleted(Response response) {
            if (response.getError() != null) {
                Toast.makeText(ShareScreen.this, response.getError().getErrorMessage(), Toast.LENGTH_SHORT).show();
            } else {
                String idRploadResponse = (String) response.getGraphObject().getProperty("id");
                if (idRploadResponse != null) {
                    String fbPhotoAddress = "https://www.facebook.com/photo.php?fbid=" + idRploadResponse;
                    Log.d(ShareScreen.class.getSimpleName(), fbPhotoAddress);
                } else {
                    Toast.makeText(ShareScreen.this, "Post failed", Toast.LENGTH_SHORT).show();
                }
            }
            progressDialog.dismiss();
            finish();
        }
    };

    private Request.Callback uploadPhotoRequestCallback = new Request.Callback() {
        @Override
        public void onCompleted(Response response) {
            getContentResolver().delete(bitmapUri, null, null);
            if (response.getError() != null) {
                Toast.makeText(ShareScreen.this, response.getError().getErrorMessage(), Toast.LENGTH_SHORT).show();
            } else {
                String idUploadResponse = (String) response.getGraphObject().getProperty("id");
                if (idUploadResponse != null) {
                    String fbPhotoAddress = "https://www.facebook.com/photo.php?fbid=" + idUploadResponse;
                    Log.d(ShareScreen.class.getSimpleName(), fbPhotoAddress);
                } else {
                    Toast.makeText(ShareScreen.this, "Post failed", Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, Const.BUGSENSE_KEY);
        setContentView(R.layout.share_screen);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.savedInstanceState = savedInstanceState;

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sharing...");

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        shareText = getIntent().getStringExtra("share_text");
        if (getIntent().hasExtra("share_bitmap")) {
            new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    Bitmap bitmap = null;
                    try {
                        bitmapUri = Uri.parse(params[0]);
                        bitmap = MediaStore.Images.Media.getBitmap(ShareScreen.this.getContentResolver(), bitmapUri);
                    } catch (IOException e) {
                        Log.e(ShareScreen.class.getSimpleName(), e.getMessage(), e);
                    }
                    return bitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if (bitmap != null) {
                        shareBitmap = bitmap;
                        ((ImageView) findViewById(R.id.share_image)).setImageBitmap(shareBitmap);
                    } else {
                        findViewById(R.id.share_image).setVisibility(View.GONE);
                    }
                }
            }.execute(getIntent().getStringExtra("share_bitmap"));
        } else {
            findViewById(R.id.share_image).setVisibility(View.GONE);
        }

        findViewById(R.id.btn_share).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);

        shareTextView = (EditText) findViewById(R.id.share_text);
        shareTextView.setText(shareText);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_share) {
            if (TextUtils.isEmpty(shareTextView.getText().toString().trim()) && shareBitmap == null) {
                Toast.makeText(ShareScreen.this, "Type message to share", Toast.LENGTH_SHORT).show();
            } else {
                Session session = new Session.Builder(ShareScreen.this).build();
                if (!SessionState.OPENED.equals(session.getState())) {
                    Session.setActiveSession(session);
                    session.openForPublish(new Session.OpenRequest(this).setPermissions("publish_actions").setCallback(statusCallback));
                } else {
                    share(session);
                }
            }
        } else if (v.getId() == R.id.btn_cancel) {
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BugSenseHandler.closeSession(this);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void share(Session session) {
        if (shareBitmap != null) {
            Request request = Request.newUploadPhotoRequest(session, shareBitmap, uploadPhotoRequestCallback);
            Bundle parameters = request.getParameters();
            parameters.putString("message", shareTextView.getText().toString().trim());
            request.setParameters(parameters);
            request.executeAsync();
        } else {
            Request.newStatusUpdateRequest(session, shareText, statusUpdateRequestCallback).executeAsync();
        }
        if (!this.isFinishing()) {
            progressDialog.show();
        }
    }*/
}
