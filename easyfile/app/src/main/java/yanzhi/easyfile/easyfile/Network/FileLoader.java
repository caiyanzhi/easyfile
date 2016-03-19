package yanzhi.easyfile.easyfile.Network;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Subscriber;
import yanzhi.easyfile.easyfile.DiskLruCacheManager;
import yanzhi.easyfile.easyfile.util.DiskLruCache;
import yanzhi.easyfile.easyfile.util.DiskLruCacheEntity;
import yanzhi.easyfile.easyfile.util.MathUtils;

/**
 * 文件加载器，设计成单例模式
 * @desc Created by yanzhi on 2016-03-18.
 */
public class FileLoader {
    private static String TAG = "FileLoader";
    private static FileLoader defaultFileLoader;
    public FileLoader(){};
    private NetworkManager networkManager;
    private DiskLruCacheManager diskLruCacheManager;
    public void init(Context applicationContext){
        networkManager = new NetworkManager(NetworkManager.MAX_DEFAULT_THREAD_CNT,NetworkManager.MAX_DEFAULT_THREAD_CNT);
        diskLruCacheManager  = DiskLruCacheManager.getInstance(applicationContext);
    }

    public synchronized static FileLoader getInstance(Context applicationContext){
        if(defaultFileLoader == null){
            defaultFileLoader = new FileLoader();
            defaultFileLoader.init(applicationContext);
        }
        return defaultFileLoader;
    }

    public void loadFile(String fileUrl, boolean reload, Subscriber<Long> processSubscriber){

        processSubscriber.onStart();
        //加载本地文件，不需要下载，如果有，直接通知上层下载完成。
        if (fileUrl.startsWith("file://")) {
            try {
                File localFile = new File(new URI(fileUrl));
                if (localFile.exists()) {
                    processSubscriber.onNext(100L);
                    processSubscriber.onCompleted();
                } else {
                    processSubscriber.onError(new Exception("Disk File not exists"));
                }
            } catch (URISyntaxException e) {
                processSubscriber.onError(e);
            }
            return;
        }

        File file = loadFileFromDiskCache(fileUrl);
        if(!reload && file != null) {
            processSubscriber.onNext(100L);
            processSubscriber.onCompleted();
            return;
        }

        loadFileFromWeb(fileUrl, reload, processSubscriber);
    }

    /**
     * 支持断点续传
     * 仅支持get方式获取文件
     * @param fileUrl
     * @param processSubscriber
     */
    public void loadFileFromWeb(final String fileUrl, boolean reload, final Subscriber<Long> processSubscriber) {
        final String key = MathUtils.hashKeyForDisk(fileUrl);
        // 暂时加 "__" 区分下载中
        String loadingKey = key + "__";
        final DiskLruCacheEntity diskLruCache = diskLruCacheManager.getDownloadDiskCache();
        File dirFile = diskLruCache.getDirFile();

        File file = new File(dirFile.getAbsolutePath() + "/" + loadingKey);
        if(!file.exists()) {
            File diskTmpFile = loadDirtyFileFromDiskCache(loadingKey);
            if(diskTmpFile != null) {
                FileUtil.renameFile(diskTmpFile, file);
            }
        }

        final DiskLruCache.Editor editor =  diskLruCache.getEditor(loadingKey);
        if (editor == null) {
            // 多个线程编辑
            processSubscriber.onError(new Exception("multi thread edit the same key"));
            return;
        }

        File downloadingFile = editor.getFile(DiskLruCache.DISK_CACHE_INDEX, false);

        final long startOffset = !reload && downloadingFile != null ? downloadingFile
                .length() : 0;
        final OutputStream[] out = new OutputStream[1];
        out[0] = null;

        // 使用Range来续下载
        final boolean[] isServerSupportRange = new boolean[2]; //
        isServerSupportRange[0] = false; // 如果true，svr有输出Accept-Ranges，
        isServerSupportRange[1] = false; // 如果true，svr有输出Content-Range

        final long[] sizeRange = new long[3];
        sizeRange[0] = 0; // 开始下载字节
        sizeRange[1] = -1; // 还剩多少字节下载
        sizeRange[2] = -1; // 整个文件字节

        NetworkRequest request = new NetworkRequest(fileUrl);

        NetworkRequest.NetworkResponseHandler networkResponseHandler = new NetworkRequest.NetworkResponseHandler() {

            private long readLen = 0; // 本次读的字节数

            @Override
            public void responseReceiveHeader(Map<String, List<String>> headers) {
                // 本次下载的字节数
                long contentLength = -1;
                final String CONTENT_LENGTH = "Content-Length";
                if (headers.containsKey(CONTENT_LENGTH)
                        && headers.get(CONTENT_LENGTH).size() == 1) {
                    try {
                        contentLength = Long.parseLong(headers.get(CONTENT_LENGTH).get(0));
                    } catch (Exception e) {

                    }
                }

                final String ACCEPT_RANGES = "Accept-Ranges";
                if (headers.containsKey(ACCEPT_RANGES)
                        && "bytes".equals(headers.get(ACCEPT_RANGES).get(0))) {
                    isServerSupportRange[0] = true;
                }
                final String CONTENT_RANGE = "Content-Range";
                final Pattern pattern = Pattern.compile(
                        "(bytes )?(\\d+)-(\\d+)\\/(\\d+)", Pattern.DOTALL);

                try {
                    if (headers.containsKey(CONTENT_RANGE)
                            && headers.get(CONTENT_RANGE).size() == 1) {
                        Matcher matcher = pattern.matcher(headers.get(
                                CONTENT_RANGE).get(0));
                        if (matcher.find()) {
                            try {
                                sizeRange[0] = Long.parseLong(matcher.group(1 + 1));
                                sizeRange[1] = Long.parseLong(matcher.group(2 + 1));
                                sizeRange[2] = Long.parseLong(matcher.group(3 + 1));
                                if (sizeRange[0] != startOffset) {
                                    // 这个是SVR问题，app不处理了
                                }
                                out[0] = editor.newOutputStream(DiskLruCache.DISK_CACHE_INDEX, true);
                            } catch (NumberFormatException e) {
                                sizeRange[0] = 0;
                                sizeRange[2] = -1;
                            }
                        }
                    }
                    if (out[0] == null) {
                        // 非续传
                        out[0] = editor.newOutputStream(DiskLruCache.DISK_CACHE_INDEX, false);
                    }
                    processSubscriber.onNext(0L);
                } catch (Exception e) {
                    processSubscriber.onError(e);
//                    request.abort();
                }
            }

            @Override
            public void responseSuccess(String responseStr) {
                long fileSize = -1; //-1找不到文件
                try {
                    out[0].close();
                    out[0] = null;
                    // 重命名
                    File file = editor.getFile(DiskLruCache.DISK_CACHE_INDEX,
                            true);
                    if (!file.exists()) {
                        // error
                        processSubscriber.onError(new FileNotFoundException());
                    } else {
                        fileSize = file.length();
                        // 把loadingKey的命名改为key
                        Log.d(TAG, "diskLruCache.edit3 : " + key);
                        DiskLruCache.Editor completeEditor = diskLruCache
                                .getEditor(key);
                        File completeFile = completeEditor.getFile(
                                DiskLruCache.DISK_CACHE_INDEX, true);
                        file.renameTo(completeFile);
                        completeEditor.commit();
                    }
                    editor.abort(); // 删除文件名loadingKey
                    diskLruCache.flush();
                } catch (Exception e) {
                }

                Log.v(TAG, new StringBuilder().append("success ")
                        .append(fileUrl).append(",").append(key).append(",").append(fileSize).toString());
            }

            @Override
            public void responseComplete() {
                processSubscriber.onCompleted();
            }

            @Override
            public void responseError(String responseStr) {
                try {
                    synchronized (out) {
                        // 由于abort在别的线程回调onError，这里加锁保护
                        if (out[0] != null) {
                            // out[0]为null是还没接收数据前abort
                            out[0].close();
                            out[0] = null;
                        }
                    }
                    if (isServerSupportRange[0]) {
                        // server支持Range
                        editor.commit();
                    } else {
                        // 不支持，就丢弃已下载的
                        editor.abort();
                    }
                } catch (Exception e) {
                    // 这里出错，不管了
                }
                processSubscriber.onError(new Exception(responseStr));
                Log.v(TAG, new StringBuilder()
                        .append("error:").append(fileUrl).append(",").append(key)
                        .append(",").append(responseStr).toString());
            }

            @Override
            public void responseReceiveData(byte[] dataRead, int readLen, long totalBytesRead) {
                long total = sizeRange[2] > -1 ? sizeRange[2] : totalBytesRead;

                //Log.v(TAG, "file readLen:" + readLen + ", total:" + total
                //		+ ", totalBytesRead:" + totalBytesRead
                //		+ ", startOffset:" + sizeRange[0]);
                try {
                    synchronized (out) {
                        //由于abort在别的线程回调onError，这里加锁保护
                        if (out[0] == null) {
                            Log.i(TAG, "receive out null");
                            return;
                        }
                        out[0].write(dataRead, 0, readLen);
                    }
                    this.readLen += readLen;
                    long process = (this.readLen + sizeRange[0]) * 100/total;
                    processSubscriber.onNext(process);
                } catch (Exception e) {
                    Log.e(TAG, "receive data " + fileUrl + ", msg: "
                            + e.getMessage());
                }
            }
        };

        request.setNetworkResponse(networkResponseHandler);
        if (startOffset >= 0) {
            HashMap<String, String> requestHeaders = new HashMap<String, String>();
            requestHeaders.put("Range", "bytes=" + startOffset + "-");
            request.setRequestHeaders(requestHeaders);
        }

        Log.v(TAG, new StringBuilder().append("send:")
                .append(fileUrl).append(",").append(key)
                .append(",").append(startOffset).toString());

        networkManager.sendRequest(request);
    }
    
    public File loadDirtyFileFromDiskCache(String url) {
        String key = MathUtils.hashKeyForDisk(url);
        DiskLruCacheEntity diskLruCache = diskLruCacheManager.getDownloadDiskCache();
        File file = diskLruCache.getDirtyFile(key);
        return file;
    }

    public File loadFileFromDiskCache(String url) {
        String key = MathUtils.hashKeyForDisk(url);
        DiskLruCacheEntity diskLruCache =diskLruCacheManager.getDownloadDiskCache();
        File file = diskLruCache.getFile(key);
        return file;
    }
}
