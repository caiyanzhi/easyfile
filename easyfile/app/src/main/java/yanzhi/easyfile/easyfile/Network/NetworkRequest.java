package yanzhi.easyfile.easyfile.Network;

import android.os.Environment;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @desc Created by yanzhi on 2016-02-28.
 */
public class NetworkRequest {
    public HttpMethod httpMethod;
    public String url = "http://192.168.1.105:5000/upload2?abc=cyz";
    public String paramJson;
    public List<FileEntity> fileList;
    public HashMap<String,String> cookies;
    public HashMap<String,String> params;

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

    public enum HttpMethod {
        HttpMethod_GET, HttpMethod_POST, HttpMethod_MULTIPART;
    }
}
