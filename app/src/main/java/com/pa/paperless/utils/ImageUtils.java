package com.pa.paperless.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.path;

/**
 * Created by Administrator on 2017/12/11.
 */

public class ImageUtils {
    /**
     * 保存bitmap到系统相册并更新图库
     *
     * @param context 上下文
     * @param bitmap  需要保存的bitmap 对象
     */
    public static void saveToGallery(Context context, Bitmap bitmap) {
        //Paperless  自定义的文件夹名称
        File appDir = new File(Environment.getExternalStorageDirectory(), "Paperless");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //文件的名称设置为 系统时间.jpg
        String fileName = System.currentTimeMillis() + ".PNG";
        Log.e("MyLog", "ImageUtils.saveToGallery:  fileName --->>> " + fileName);
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        /** ************ ******  如果用以下两行代码则会出现两张图片  try catch ****** ************ **/
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    file.getAbsolutePath(), fileName, null);
        /** ************ ******  解决方案： 以下四行代码 ****** ************ **/
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }

}
