package com.warriorfitapp.mobile.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * @author Andrii Kovalov
 */
public class CircleBitmapTransform extends BitmapTransformation {
    private final int borderWidth = 2;
    private int color = Color.BLACK;

    /*public CircleBitmapDisplayer(int color, int borderWidth) {
        this.color = color;
        this.borderWidth = borderWidth;
    }*/

    public CircleBitmapTransform(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        /*int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        // TODO this could be acquired from the pool too
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        return result;*/


        final int imageViewWidth = source.getWidth();
        final int imageViewHeight = source.getHeight();
        if (source.getHeight() > 0 && source.getWidth() > 0 && imageViewWidth > 0 && imageViewHeight > 0) {
            Bitmap output = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int borderSizePx = borderWidth;

            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, imageViewWidth, imageViewHeight);

            // prepare canvas for transfer
            paint.setAntiAlias(true);
            paint.setColor(0xFFFFFFFF);
            paint.setStyle(Paint.Style.FILL);

            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawCircle(rect.centerX(), rect.centerY(), imageViewWidth / 2, paint);

            int sourceWidth = source.getWidth();
            int sourceHeight = source.getHeight();

            // Compute the scaling factors to fit the new height and width, respectively.
            // To cover the final image, the final scaling will be the bigger
            // of these two.
            float xScale = (float) imageViewWidth / sourceWidth;
            float yScale = (float) imageViewHeight / sourceHeight;
            float scale = Math.max(xScale, yScale);

            // Now get the size of the source bitmap when scaled
            float scaledWidth = scale * sourceWidth;
            float scaledHeight = scale * sourceHeight;

            // Let's find out the upper left coordinates if the scaled bitmap
            // should be centered in the new size give by the parameters
            float left = (imageViewWidth - scaledWidth) / 2;
            float top = (imageViewHeight - scaledHeight) / 2;

            // The target rectangle for the new, scaled version of the source bitmap will now be
            RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

            // Finally, we create a new bitmap of the specified size and draw our new, scaled bitmap onto it.
            Bitmap dest = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(dest);
            c.drawBitmap(source, null, targetRect, null);

            source = dest;

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(source, rect, rect, paint);

            // draw border
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) borderSizePx);
            canvas.drawCircle(rect.centerX(), rect.centerY(), imageViewWidth / 2, paint);

            return output;
        } else {
            return source;
        }
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
