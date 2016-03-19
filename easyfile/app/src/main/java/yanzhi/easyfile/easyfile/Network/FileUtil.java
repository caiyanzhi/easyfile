package yanzhi.easyfile.easyfile.Network;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * @desc Created by yanzhi on 2016-03-06.
 */
public class FileUtil {

    /**
     * 判断SD卡是否存在
     */
    public static boolean hasSdcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @TargetApi(9)
    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    @TargetApi(8)
    public static File getExternalCacheDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO && context.getExternalCacheDir() != null) {
            return context.getExternalCacheDir();
        }
        // Before Froyo we need to construct the external cache dir ourselves
        String packagePath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + context.getPackageName();
        File packageDir = new File(packagePath);
        if (!tryMkdirs(packageDir)) {
            return null;
        }

        String cachePath = packagePath + "/cache/";
        File cacheDir = new File(cachePath);
        if (!tryMkdirs(cacheDir)) {
            return null;
        }

        return cacheDir;
    }


    public static synchronized boolean tryMkdirs(File dir) {
        if (!(dir.exists() && dir.isDirectory())) {
            Log.v("mkdirs", dir.getAbsolutePath());
            if (!dir.mkdirs()) {
                //创建不成功
                return false;
            }
        }
        return true;
    }

    public static File getDiskFromPath(String dirPath) {
        File path = new File(dirPath);
        FileUtil.tryMkdirs(path);
        return path;
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir
        // otherwise use internal cache dir
        // if sdcard has no space, getExternalCacheDir(context) return null
        final String cachePath = ((Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()) || !isExternalStorageRemovable()) && getExternalCacheDir(context) != null) ?
                getExternalCacheDir(context).getPath() :
                context.getCacheDir().getPath();
        File path = new File(cachePath + File.separator + uniqueName);
        FileUtil.tryMkdirs(path);
        return path;
    }

    public static void deleteDir(File dir) throws IOException {
        if (!dir.exists()) {
            return;
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("not a directory: " + dir);
        }
        File[] files = dir.listFiles();// Returns null if this abstract pathname does not denote a directory, or if an I/O error occurs.
        if (files == null) {
            //not a dir
            return;
        }
        if (files!=null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                }
                if (!file.delete()) {
                    throw new IOException("failed to delete file: " + file);
                }
            }
        }
    }

    public static File renameFile(File srcFile, File destFile) {
        if (srcFile == null || destFile == null) return null;

        try {
            srcFile.renameTo(destFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return destFile;
    }

}
