package gg.base.library.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.security.MessageDigest;

import jp.wasabeef.glide.transformations.internal.FastBlur;

/**
 * Created by sss on 2020-01-07 11:05.
 * email jkjkjk.com
 */
public class ManhuaCoverCrop extends BitmapTransformation {
    private static final String ID = "com.sunshine.manhua.util.glide.ManhuaCoverCrop";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    @Override
    protected Bitmap transform(
           BitmapPool pool,  Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap = MyTransformationUtils.centerCrop2(pool, toTransform, outWidth, outHeight);

        int sampling = 1;

        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / (float) sampling, 1 / (float) sampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(toTransform, 0, 0, paint);

        bitmap = FastBlur.blur(bitmap, 150, true);
        return bitmap;
    }


    private static Bitmap roundCrop(Bitmap source) {
        Canvas canvas = new Canvas(source);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, 120, 120, paint);
        return source;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CenterCrop;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }


}
