package gg.base.library.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;


/**
 * Created by sss on 2020-02-11 11:24.
 * email jkjkjk.com
 */
public class FileManagerUtil {

    public static final String RECORD_FILE_PATH = "xiangqin_record";


    public static File getImageDir() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
    }

    public static File getVideoDir() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath());
    }

    public static File getDownloadDir() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
    }

    public static File getExternalCacheDir(Context context) {
        String filePath;
        if (Build.VERSION.SDK_INT >= 29) {//10以上不需要申请权限
            filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        } else {
            filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        }

        return createFileDir(new File(filePath, "XiangqinCacheTemp"));
    }

    public static File getCacheDir(Context context) {
        return createFileDir(new File(context.getCacheDir(), "XiangqinCacheTemp"));
    }

    public static File createFileDir(File file) {
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            LL.i("创建缓存目录结果LeoCacheTemp：" + mkdirs);
            if (mkdirs) {
                return file;
            } else {
                return null;
            }
        }
        return file;
    }


    public static File getRecordCacheDir(Context context) {
        File file = new File(context.getCacheDir(), RECORD_FILE_PATH);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            LL.i("获取缓存音频结果：" + mkdirs);
            if (mkdirs) {
                return file;
            } else {
                return null;
            }
        }
        return file;
    }

    /**
     * 删除用户可能存在的录音文件
     */
    public static void deleteRecordCache(Context context) {
        File dir = getRecordCacheDir(context);
        if (dir == null) {
            return;
        }
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((dir1, name) -> name.endsWith(".mp4"));
            for (File file : files) {
                LL.i("delete file:" + file.getName() + ",result:" + file.delete());
            }
        }
    }

    /**
     * 删除用户可能存在的图片缓存
     */
    public static void deleteImageCacheFiles(Context context) {
        File dir = getExternalCacheDir(context);
        if (dir == null) {
            return;
        }
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                LL.i("deleteCacheFile", String.format("delete file:%s,result:%s", file.getName(), file.delete()));
            }
        }
    }
}
