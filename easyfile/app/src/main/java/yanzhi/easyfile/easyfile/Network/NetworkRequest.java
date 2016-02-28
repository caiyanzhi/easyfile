package yanzhi.easyfile.easyfile.Network;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * @desc Created by yanzhi on 2016-02-28.
 */
public class NetworkRequest {
    public HttpMethod httpMethod;
    public String url = "http://192.168.1.105:5000/upload2";
    public String paramJson;
    public List<File> fileList;
    public NetworkRequest(){
        try {
            JSONObject object = new JSONObject();
            object.put("title", "cyz");
            paramJson = object.toString();
        } catch (Exception e){

        }
    }
    public String getParamJson(){
        return paramJson;
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
