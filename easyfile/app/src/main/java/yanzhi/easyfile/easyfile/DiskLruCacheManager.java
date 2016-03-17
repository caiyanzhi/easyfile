package yanzhi.easyfile.easyfile;

import android.content.Context;

import yanzhi.easyfile.easyfile.util.DiskLruCacheEntity;

/**
 * @desc Created by yanzhi on 2016-03-06.
 */
public class DiskLruCacheManager {
    private DiskLruCacheEntity imageLruCacheEntity;
    private DiskLruCacheEntity downloadLruCacheEntity;
    private static DiskLruCacheManager instance;
    private static final String imageCacheDir = "imageCache";
    private static final String downloadCacheDir = "downloadCache";
    private DiskLruCacheManager(Context context){
        init(context, null);
    }

    private DiskLruCacheManager(Context context, String dirPath){
        init(context,dirPath);
    }

    public static DiskLruCacheManager getInstance(Context context, String dirPath){
        synchronized (DiskLruCacheManager.class) {
            if(instance == null) {
                instance = new DiskLruCacheManager(context,dirPath);
            }
            return instance;
        }
    }

    public static DiskLruCacheManager getInstance(Context context){
        synchronized (DiskLruCacheManager.class) {
            if(instance == null) {
                instance = new DiskLruCacheManager(context,null);
            }
            return instance;
        }
    }

    private void init(Context context, String dirPath){
        imageLruCacheEntity = new DiskLruCacheEntity(context, imageCacheDir, dirPath);
        downloadLruCacheEntity = new DiskLruCacheEntity(context, downloadCacheDir, dirPath);
    }

    public DiskLruCacheEntity getImageDiskCache(){
        return imageLruCacheEntity;
    }

    public DiskLruCacheEntity getDownloadDiskCache(){
        return downloadLruCacheEntity;
    }
}

