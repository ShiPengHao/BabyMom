package com.yimeng.babymom.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.graphics.BitmapFactory.decodeStream;

/**
 * bitmap工具类
 */
public class BitmapUtils {
    /**
     * 通过资源id获取bitmap对象，宽高适应手机屏幕
     *
     * @param activity mActivity
     * @param resId    资源id
     * @return bitmap对象
     */
    public static Bitmap getResImg(Context activity, int resId) {
        // 获得本地图片宽高
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置开关
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeResource(activity.getResources(), resId, opts);
        float imgWidth = opts.outWidth;
        float imgHeight = opts.outHeight;
        // 获得缩放比例
        opts.inSampleSize = Math.max(((int) (Math.min(imgWidth / DensityUtil.SCREEN_WIDTH, imgHeight / DensityUtil.SCREEN_HEIGHT) + 0.5f))
                , 1);
        // 设置开关
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(activity.getResources(), resId, opts);
    }

    /**
     * 通过content uri获取bitmap对象，宽高适应手机屏幕
     *
     * @param uri local uri
     * @return bitmap对象
     */
    public static Bitmap getImgFromUri(Context context, Uri uri) throws Exception {
        Bitmap bitmap;
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        // 获得本地图片宽高
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置开关
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inDither = true;
        BitmapFactory.decodeStream(inputStream, null, opts);
        inputStream.close();
        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;
        // 获得缩放比例
        opts.inSampleSize = Math.max(((int) (Math.min(imgWidth / DensityUtil.SCREEN_WIDTH, imgHeight / DensityUtil.SCREEN_HEIGHT) + 0.5f))
                , 1);
        // 设置开关
        opts.inJustDecodeBounds = false;
        inputStream = context.getContentResolver().openInputStream(uri);
        bitmap = BitmapFactory.decodeStream(inputStream, null, opts);
        inputStream.close();
        return bitmap;
    }

    /**
     * 保持宽高比缩放bitmap
     *
     * @param oldBitmap 输入bitmap
     * @param newWidth  指定的宽
     * @param newHeight 指定的高
     * @return bitmap对象
     */
    public static Bitmap zoomBitmap(Bitmap oldBitmap, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = oldBitmap.getWidth();
        int height = oldBitmap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        float scale = Math.min(scaleHeight, scaleWidth);
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        return Bitmap.createBitmap(oldBitmap, 0, 0, width, height, matrix, true);
    }

    /**
     * 缩放本地图片 依赖于zoomBitmap
     *
     * @param path      文件路径
     * @param newWidth  指定宽
     * @param newHeight 指定高
     * @return bitmap对象，或者null，如果指定路径文件不能被解析为bitmap
     */
    public static Bitmap zoomFileImg(String path, int newWidth, int newHeight) {
        // 图片源
        Bitmap bm = BitmapFactory.decodeFile(path);
        if (null != bm) {
            return zoomBitmap(bm, newWidth, newHeight);
        }
        return null;
    }

    /**
     * 缩放资产图片 依赖于zoomBitmap
     *
     * @param context   上下文
     * @param fileName  资产文件名
     * @param newWidth  指定宽
     * @param newHeight 指定高
     * @return bitmap对象，或者null，如果指定的资产文件不能被解析
     */
    public static Bitmap zoomAssertImg(Context context, String fileName, int newWidth,
                                       int newHeight) {
        // 图片源
        try {
            Bitmap bm = decodeStream(context.getAssets()
                    .open(fileName));
            if (null != bm) {
                return zoomBitmap(bm, newWidth, newHeight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断bitmap是否存在
     *
     * @param bitmap bitmap对象
     * @return 存在true，不存在false
     */
    public static boolean bitmapAvailable(Bitmap bitmap) {
        return bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0;
    }

    /**
     * drawable 转成bitmap
     *
     * @param drawable drawable
     * @return bitmap 对象
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap转换成Drawable
     *
     * @param context 上下文
     * @param bitmap  bitmap对象
     * @return drawable对象
     */
    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
        //因为BtimapDrawable是Drawable的子类，最终直接使用bd对象即可。
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    /**
     * 从资源中获取Bitmap
     *
     * @param context 上下文
     * @param resId   资源id R.drawable.IconUrl(eg.)
     * @return bitmap对象
     */
    public Bitmap getBitmapFromResources(Context context, int resId) {
        Resources res = context.getResources();
        return BitmapFactory.decodeResource(res, resId);
    }

    /**
     * Byte[] -> Bitmap的转换
     */
    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * Bitmap->Byte[]的转换
     *
     * @param bm bitmap对象
     * @return 数组
     */
    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        try {
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 将图片对象压缩为不大于2M的string
     *
     * @param bitmap  图片对象
     * @param maxSize 目标图片大小，单位byte
     * @return 压缩成功返回string，否则null
     */
    public static String compressBitmap2Base64String(Bitmap bitmap, int maxSize) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes;
        int quality = 100;
        while (quality > 0) {
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
                    && (bytes = baos.toByteArray()).length < maxSize) {
                try {
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Base64.encodeToString(bytes, Base64.DEFAULT);
            }
            try {
                baos.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }

            quality -= 9;
        }
        try {
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
