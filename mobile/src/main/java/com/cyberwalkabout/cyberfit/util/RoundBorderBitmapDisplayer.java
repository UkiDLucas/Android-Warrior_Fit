package com.cyberwalkabout.cyberfit.util;

public class RoundBorderBitmapDisplayer /*implements BitmapDisplayer */{

    /*private final int borderWidth;
    private float cornerRadiusPx = 16;
    private int color = Color.BLACK;

    private boolean fitToImageView = false;

    public RoundBorderBitmapDisplayer(float cornerRadiusPx, int color, int borderWidth) {
        this.cornerRadiusPx = cornerRadiusPx;
        this.color = color;
        this.borderWidth = borderWidth;
    }

    public RoundBorderBitmapDisplayer(float cornerRadiusPx, int color, int borderWidth, boolean fitToImageView) {
        this(cornerRadiusPx, color, borderWidth);
        this.fitToImageView = fitToImageView;
    }

    @Override
    public Bitmap display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        final int imageViewWidth = imageAware.getWidth();
        final int imageViewHeight = imageAware.getHeight();
        if (!imageAware.isCollected() && bitmap.getHeight() > 0 && bitmap.getWidth() > 0 && imageViewHeight > 0 && imageViewWidth > 0) {
            // TODO: check that width and height of the bitmap is greater then 0
            Bitmap output = Bitmap.createBitmap(fitToImageView ? imageViewWidth : bitmap.getWidth(), fitToImageView ? imageViewHeight : bitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int borderSizePx = borderWidth;
            final float cornerSizePx = cornerRadiusPx;

            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, fitToImageView ? imageViewWidth : bitmap.getWidth(), fitToImageView ? imageViewHeight : bitmap.getHeight());
            final RectF rectF = new RectF(rect);

            // prepare canvas for transfer
            paint.setAntiAlias(true);
            paint.setColor(0xFFFFFFFF);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

            // draw bitmap
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            if (fitToImageView) {
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
                Bitmap dest = Bitmap.createBitmap(imageViewWidth, imageViewHeight, bitmap.getConfig()!=null?bitmap.getConfig(): Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(dest);
                c.drawBitmap(bitmap, null, targetRect, null);

                bitmap = dest;
            }
            canvas.drawBitmap(bitmap, rect, rect, paint);

            // draw border
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) borderSizePx);
            canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

            imageAware.setImageBitmap(output);
            return output;
        } else {
            return bitmap;
        }
    }*/
}
