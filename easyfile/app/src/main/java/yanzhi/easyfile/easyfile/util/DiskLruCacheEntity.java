package yanzhi.easyfile.easyfile.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import yanzhi.easyfile.easyfile.Network.FileUtil;
import yanzhi.easyfile.easyfile.Network.HttpClientConfig;

/**
 * @desc Created by yanzhi on 2016-03-06.
 */
public class DiskLruCacheEntity {
    private static final String TAG = "DiskLruCacheEntity";
    private Object diskCacheLock = new Object();//diskLruCache的锁，控制多线程访问
    private File diskCacheDir;
    private DiskLruCache diskLruCache;
    private String cacheDirName;


    public DiskLruCacheEntity(Context context, String cacheDirName){
        synchronized (diskCacheLock) {
            this.cacheDirName = cacheDirName;
            diskCacheDir = FileUtil.getDiskCacheDir(context, cacheDirName);
            initDiskCache();
        }
    }

    //若无指定存储目录，默认存储在Android制定的应用目录下面，根据是否有外置存储来自动选择
    public DiskLruCacheEntity(Context context, String cacheDirName, String aimDirPath){
        synchronized (diskCacheLock) {
            if(aimDirPath != null) {
                diskCacheDir = FileUtil.getDiskFromPath(cacheDirName);
            } else {
                diskCacheDir = FileUtil.getDiskCacheDir(context, cacheDirName);
            }
            this.cacheDirName = cacheDirName;
            initDiskCache();
        }
    }

    private void initDiskCache(){
        try {
            diskLruCache = DiskLruCache.open(
                    diskCacheDir,
                    1,
                    1,
                    HttpClientConfig.DEFAULT_DISK_CACHE_SIZE);
        } catch (IOException e) {
            diskLruCache = null;
            e.printStackTrace();
        }
    }

    public void flush() throws IOException{
        synchronized (diskCacheLock) {
            diskLruCache.flush();
        }
    }

    public void writeInputStreamToCache(){

    }

    public DiskLruCache.Editor getEditor(String key){
        synchronized (diskCacheLock) {
            try {
                return diskLruCache.edit(key);
            } catch (IOException e) {
                return null;
            }
        }
    }

    public File getDirFile(){
        return diskCacheDir;
    }
    public File getFile(String key) {
        synchronized (diskCacheLock) {
            return diskLruCache.getReadableFile(key);
        }
    }

    public File getDirtyFile(String key) {
        synchronized (diskCacheLock) {
            return diskLruCache.getDirtyFile(key);
        }
    }

    public boolean isFileExsits(String key) {
        synchronized (diskCacheLock) {
            return diskLruCache.isExist(key);
        }
    }

    public String getDiskCacheFilePath(String key) {
        synchronized (diskCacheLock) {
            return diskLruCache.getFilePath(key);
        }
    }
    
    public void deleteAllCache(){
        synchronized (diskCacheLock) {
            if (diskLruCache != null && !diskLruCache.isClosed()) {
                try {
                    diskLruCache.delete();
                } catch (IOException e) {
                    Log.e(TAG, "clearCache - " + e);
                }
                diskLruCache = null;
                initDiskCache();
            }
        }
    }
}
