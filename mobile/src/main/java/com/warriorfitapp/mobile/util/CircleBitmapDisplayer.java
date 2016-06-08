package com.warriorfitapp.mobile.util;

/**
 * @author Maria Dzyokh
 */
public class CircleBitmapDisplayer /*implements BitmapDisplayer*/ {

    /*private final int borderWidth;
    private int color = Color.BLACK;

    public CircleBitmapDisplayer(int color, int borderWidth) {
        this.color = color;
        this.borderWidth = borderWidth;
    }

    @Override
    public Bitmap display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        final int imageViewWidth = imageAware.getWidth();
        final int imageViewHeight = imageAware.getHeight();
        if (!imageAware.isCollected() && bitmap.getHeight() > 0 && bitmap.getWidth() > 0 && imageViewWidth > 0 && imageViewHeight > 0) {
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

            int sourceWidth = bitmap.getWidth();
            int sourceHeight = bitmap.getHeight();

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
            c.drawBitmap(bitmap, null, targetRect, null);

            bitmap = dest;

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            // draw border
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) borderSizePx);
            canvas.drawCircle(rect.centerX(), rect.centerY(), imageViewWidth / 2, paint);

            imageAware.setImageBitmap(output);
            return output;
        } else {
            return bitmap;
        }
    }*/
}
