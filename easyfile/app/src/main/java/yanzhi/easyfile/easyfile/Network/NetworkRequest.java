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
    public HttpMethod httpMethod;
    public String url = "http://sdcs.sysu.edu.cn/wp-content/uploads/2016/02/教务部关于选派我校优秀本科生2016学年秋季学期赴加拿大阿尔伯塔大学、新加坡国立大学交流学习的通知.doc";
    //                                ;//http://192.168.1.105:5000/upload2";
    public String paramJson;
    public List<FileEntity> fileList;
    public HashMap<String,String> cookies;
    public HashMap<String,String> params;
    private boolean isDownload;
    private DownloadEntity downloadEntity;

    public DownloadEntity getDownloadEntity() {
        return downloadEntity;
    }

    public void setDownloadEntity(DownloadEntity downloadEntity) {
        this.downloadEntity = downloadEntity;
    }

    public NetworkRequest(){
        cookies = new HashMap<>();
        fileList = new ArrayList<>();
        fileList.add(new FileEntity(new File(Environment.getExternalStorageDirectory().getPath()+"/stack.txt"),"application/octet-stream","file1"));

        fileList.add(new FileEntity(new File(Environment.getExternalStorageDirectory().getPath()+"/jfinal-1.8-manual.pdf"),"application/octet-stream","file2"));
        params = new HashMap<>();
        params.put("user","caiyanzhi");
        cookies.put("username2","cyz");
        try {
            JSONObject object = new JSONObject();
            object.put("title", "cyz");
            paramJson = object.toString();
        } catch (Exception e){
        }
    }

    public String getParamJson(){
        if(httpMethod == HttpMethod.HttpMethod_GET) {
            paramJson = "";
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
            return null;
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

    public void responseReceiveHeader(Map<String, List<String>> headers){

    }

    public void responseSuccess(){

    }

    public void responseComplete(){

    }

    public void responseError(){

    }

    public void responseReceiveData(byte[] dataRead, int readLen, long totalBytesRead){

    }
}
