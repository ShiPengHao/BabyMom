package com.yimeng.babymom.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import java.io.InputStream;

/**
 * 从图库选择图片，上传图片相关的工具类
 */
public class PickImageUtils {
    private PickImageUtils(){

    }

    /**
     * 从手机图库中选择图片
     */
    public static void getGalleryImage(Activity activity, int requestCode) {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 照相
//        Intent intent=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 处理从相册返回的数据
     *
     * @param data 从图库返回的意图对象
     * @return 对应的bitmap
     */
    public static Bitmap onActivityResult(Intent data, Context context) {
        if (data == null) {
            return null;
        }
        Uri uri = data.getData();
        Bitmap bitmap = null;
        // 从相册返回的数据，得到图片的全路径
        if (uri != null) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                assert inputStream != null;
                inputStream.close();
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bundle extras = data.getExtras();
            if (extras != null) {
                //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                bitmap = extras.getParcelable("data");
            }
        }
       return bitmap;
    }


}
