package yanzhi.easyfile.easyfile.Network;

import android.os.Environment;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @desc Created by yanzhi on 2016-02-28.
 */
public class NetworkRequest {
    public HttpMethod httpMethod = HttpMethod.HttpMethod_GET;
    private String url = "http://sdcs.sysu.edu.cn/wp-content/uploads/2016/02/教务部关于选派我校优秀本科生2016学年秋季学期赴加拿大阿尔伯塔大学、新加坡国立大学交流学习的通知.doc";
    //                                ;//;
    public List<FileEntity> fileList;
    public HashMap<String,String> cookies;
    public HashMap<String,String> params;
    private boolean isDownload;
    private DownloadEntity downloadEntity;
    private HashMap<String, String> requestHeaders;
    public int priorityInteger = 0; // 优先级,不设置就FIFO

    public DownloadEntity getDownloadEntity() {
        return downloadEntity;
    }

    public void setDownloadEntity(DownloadEntity downloadEntity) {
        this.downloadEntity = downloadEntity;
    }

    public NetworkRequest(String url){
        this.url = url;
    }


    public void test(){
        httpMethod = HttpMethod.HttpMethod_MULTIPART;
        url = "http://192.168.1.105:5000/upload2";
        FileEntity fileEntity1 = new FileEntity(new File(Environment.getExternalStorageDirectory().getPath()+"/stack.txt"),"application/octet-stream","file1");
        FileEntity fileEntity2 = new FileEntity(new File(Environment.getExternalStorageDirectory().getPath()+"/jfinal-1.8-manual.pdf"),"application/octet-stream","file2");
        addUploadFile(fileEntity1);
        addUploadFile(fileEntity2);
        addParam("user","caiyanzhi");
        addParam("username2","cyz");
        addCookie("pass","word");
    }

    public void addParam(String key, String value){
        if(params == null) {
            params = new HashMap<>();
        }
        params.put(key,value);
    }

    public void addCookie(String key, String value){
        if(cookies == null) {
            cookies = new HashMap<>();
        }
        cookies.put(key,value);
    }

    public void addUploadFile(FileEntity fileEntity){
        if(fileList == null) {
            fileList = new ArrayList<>();
        }
        fileList.add(fileEntity);
    }
    /**
     * @return the requestHeaderDictionary
     */
    public HashMap<String, String> getRequestHeaders() {
        if (null == requestHeaders) {
            requestHeaders = new HashMap<String, String>();
        }
        return requestHeaders;
    }

    public void setRequestHeaders(HashMap<String, String> headers){
        this.requestHeaders = headers;
    }

    //获取格式化的参数，get的方式用&拼接，post的方式用json
    public String getParamJson(){
        String paramJson = "";
        if(params == null) {
            return paramJson;
        }

        if(httpMethod == HttpMethod.HttpMethod_GET) {
            for(HashMap.Entry<String,String> entry : params.entrySet()) {
                paramJson = paramJson + "&" + entry.getKey() + "=" + entry.getValue();
            }
            return paramJson;
        }

        try {
            JSONObject object = new JSONObject();
            for(HashMap.Entry<String,String> entry : params.entrySet()) {
                object.put(entry.getKey(), entry.getValue());
            }
            paramJson = object.toString();
            return paramJson;
        } catch (Exception e){
            return "";
        }
    }

    public String getUrl(){
        return url;
    }
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setIsDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }

    public enum HttpMethod {
        HttpMethod_GET, HttpMethod_POST, HttpMethod_MULTIPART;
    }

    public interface NetworkResponseHandler {
        //对于返回头部的处理
        void responseReceiveHeader(Map<String, List<String>> headers);
        void responseSuccess(String responseStr);
        void responseComplete();
        void responseError(String responseStr);
        void responseReceiveData(byte[] dataRead, int readLen, long totalBytesRead);
    }

    private NetworkResponseHandler networkResponse;

    public void setNetworkResponse(NetworkResponseHandler networkResponse) {
        this.networkResponse = networkResponse;
    }

    public NetworkResponseHandler getNetworkResponse(){
        return  networkResponse;
    }
}
