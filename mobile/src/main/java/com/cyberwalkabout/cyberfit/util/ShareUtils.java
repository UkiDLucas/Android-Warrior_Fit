package com.cyberwalkabout.cyberfit.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cyberwalkabout.cyberfit.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Maria Dzyokh
 */
public class ShareUtils {
    private static final String TAG = ShareUtils.class.getSimpleName();

    public static final int SHARE_ACTIVITY_REQ = 1001;

    private static final String FACEBOOK_APP = "com.facebook.katana";

    private static final String[] ALLOWED_SHARE_APPS = new String[]{"com.google.android.gm", "twitter", FACEBOOK_APP, "com.google.android.apps.plus"};
    private static final boolean LIMIT_SHARE_APPS = false;

    private static final String KEY_IMAGE_URI = "image_uri";

    private OnShareListener listener;

    public ShareUtils() {
    }

    public ShareUtils(OnShareListener listener) {
        this.listener = listener;
    }

    public void sendMail(Context ctx, String to, String subject, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setType("text/html");
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        emailIntent.setData(Uri.parse("mailto:" + to));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(message));
        ctx.startActivity(Intent.createChooser(emailIntent, ctx.getString(R.string.email_chooser_popup_title)));
    }

    public void shareText(Context context, String text) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");

        showChooser(context, intent, text, null);
    }

    public void shareTextWithImage(final Context context, final String text, final File imageFile) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        if (imageFile != null) {
            intent.setType("image/jpeg");
            Uri uri = Uri.fromFile(imageFile);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_IMAGE_URI, uri.toString()).commit();
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        } else {
            intent.setType("text/plain");
        }

        showChooser(context, intent, text, imageFile != null ? imageFile.getAbsolutePath() : null);
    }

    public void shareTextWithImage(final Context context, final String text, final Bitmap image) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        if (image != null) {
            intent.setType("image/jpeg");
        } else {
            intent.setType("text/plain");
        }
        String pathOfBmp = null;
        if (image != null) {
            pathOfBmp = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "screen", null);
            Uri bmpUri = Uri.parse(pathOfBmp);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_IMAGE_URI, bmpUri.toString()).commit();
            intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        }
        showChooser(context, intent, text, pathOfBmp);
    }

    public void shareApp(final Context ctx) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, ctx.getString(R.string.share_this_app_message));
        intent.setType("text/plain");
        showChooser(ctx, intent, ctx.getString(R.string.share_this_app_message), null);
    }

    private void showChooser(final Context context, Intent intent, final String text, final String pathToImage) {
        final List<Intent> intents = new ArrayList<Intent>();
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resInfo = pm.queryIntentActivities(intent, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                String packageName = info.activityInfo.packageName.toLowerCase();
                String activityName = info.activityInfo.name.toLowerCase();

                boolean allowed = true;

                if (LIMIT_SHARE_APPS) {
                    for (String shareApp : ALLOWED_SHARE_APPS) {
                        if (!packageName.contains(shareApp) && !activityName.contains(shareApp)) {
                            allowed = false;
                            break;
                        }
                    }
                }

                if (allowed) {
                    Intent i = new Intent(intent);
                    i.setPackage(info.activityInfo.packageName);
                    /*if (FACEBOOK_APP.equals(packageName)) {
                        i.putExtra(NativeProtocol.EXTRA_LINK, text);
                    }*/
                    intents.add(i);
                }
            }
        }

        ArrayAdapter<Intent> adapter = new ChooserArrayAdapter(context, android.R.layout.select_dialog_item, android.R.id.text1, intents);
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.share_app_chooser_popup_title))
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        dialog.dismiss();

                        Intent intent = intents.get(item);

                        boolean handled = false;

                        if (listener != null) {
                            handled = listener.onShare(intent);
                        }

                        if (!handled) {
                            /*if (intent.getPackage().contains(FACEBOOK_APP)) {
                                Intent shareIntent = new Intent(context, ShareScreen.class);
                                shareIntent.putExtra("share_text", text);
                                if (!TextUtils.isEmpty(pathToImage)) {
                                    shareIntent.putExtra("share_bitmap", pathToImage);
                                }
                                context.startActivity(shareIntent);
                            } else {*/
                            ((Activity) context).startActivityForResult(intent, SHARE_ACTIVITY_REQ);
                            /*}*/
                        }
                    }
                })
                .show();
    }

    public void deleteTempImageIfExists(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(KEY_IMAGE_URI)) {
            Uri imageUri = Uri.parse(prefs.getString(KEY_IMAGE_URI, ""));
            try {
                context.getContentResolver().delete(imageUri, null, null);
            } catch (Exception e) {
                Log.e(TAG, "Failed to delete " + imageUri);
            }
        }
    }

    public Bitmap takeScreenshotAsBitmap(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        Bitmap screenshot = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return screenshot;
    }

    public File takeScreenshotAsFile(Activity activity) {
        try {
            // image naming and path  to include sd card  appending name you choose for file
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Screenshot");
            if (dir.mkdirs()) {
                Log.d(TAG, "Created " + dir.getAbsolutePath());
            }
            File imageFile = new File(dir, DateFormat.format("yyyy-MM-dd_hh:mm:ss", new Date()) + ".jpg");
            if (imageFile.createNewFile()) {
                Log.d(TAG, "Created " + imageFile.getAbsolutePath());
            }

            // create bitmap screen capture
            View v1 = activity.getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            v1.destroyDrawingCache();

            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            int width = activity.getWindowManager().getDefaultDisplay().getWidth();
            int height = activity.getWindowManager().getDefaultDisplay().getHeight();
            bitmap = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width, height - statusBarHeight);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            return imageFile;
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private static class ChooserArrayAdapter extends ArrayAdapter<Intent> {
        private PackageManager pm;
        private int textViewResourceId;

        public ChooserArrayAdapter(Context context, int resource, int textViewResourceId, List<Intent> intents) {
            super(context, resource, textViewResourceId, intents);
            this.pm = context.getPackageManager();
            this.textViewResourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            try {
                ApplicationInfo info = pm.getApplicationInfo(getItem(position).getPackage(), 0);
                Drawable appIcon = pm.getApplicationIcon(info);

                TextView textView = (TextView) view.findViewById(textViewResourceId);
                textView.setText(pm.getApplicationLabel(info));
                textView.setCompoundDrawablesWithIntrinsicBounds(appIcon, null, null, null);
                textView.setCompoundDrawablePadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(ShareUtils.class.getSimpleName(), e.getMessage(), e);
            }
            return view;
        }
    }

    public interface OnShareListener {
        boolean onShare(Intent intent);
    }

}
