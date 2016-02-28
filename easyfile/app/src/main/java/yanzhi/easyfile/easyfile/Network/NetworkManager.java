package yanzhi.easyfile.easyfile.Network;

import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @desc Created by yanzhi on 2016-02-27.
 */
public class NetworkManager {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("application/octet-stream");

    public static void httpSend(NetworkRequest networkRequest){
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

        //设置cookies
        setCookies(builder,networkRequest);

        OkHttpClient client = builder.build();

        Request request;
        String paramsString = networkRequest.getParamJson();

        if (networkRequest.getHttpMethod() == NetworkRequest.HttpMethod.HttpMethod_POST && paramsString != null) {
            RequestBody requestBody = RequestBody.create(JSON,paramsString);
            request = new Request.Builder()
                    .url(networkRequest.getUrl())
                    .post(requestBody)
                    .build();
        } else if (networkRequest.getHttpMethod() == NetworkRequest.HttpMethod.HttpMethod_MULTIPART) {
            RequestBody requestBody = getMultipartBuilderBody(networkRequest);
            request = new Request.Builder()
                    .url(networkRequest.getUrl())
                    .method("POST",requestBody)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(networkRequest.getUrl())
                    .build();
        }

        Log.v("cyz", "" + request.url());

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Headers responseHeaders = response.headers();
                long contentLen = response.body().contentLength();
                Log.v("cyz","contentLen " + contentLen);
                Log.v("cyz","ret " + response.body().string());
                InputStream inputStream =  response.body().byteStream();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    Log.v("cyz"," i " + i + " " + responseHeaders.name(i) + ":" + responseHeaders.value(i));
                }

                byte[] buffer = new byte[HttpClientConfig.RECEIVE_BUFF_LEN_INTEGER];

                FileOutputStream outputStream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/教务部关于选派我校优秀本科生2016学年秋季学期赴加拿大阿尔伯塔大学、新加坡国立大学交流学习的通知.doc");
                int readLen =-1;
                int byteLen = 0;
                while((readLen = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer,0, readLen);
                    byteLen += readLen;
                }
                Log.v("cyz","byteLen " + byteLen);
                outputStream.flush();
                outputStream.close();
                Log.v("cyz","download ok");
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MultipartBody getMultipartBuilderBody(NetworkRequest networkRequest) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(networkRequest.params != null) {
            for(HashMap.Entry<String,String> entry : networkRequest.params.entrySet()) {
                builder.addFormDataPart(entry.getKey(),entry.getValue());
            }
        }
        if(networkRequest.fileList != null) {
            for(FileEntity fileEntity : networkRequest.fileList) {
                builder.addFormDataPart(fileEntity.getFileNameString(), fileEntity.getFile().getName(),
                        RequestBody.create(MediaType.parse(fileEntity.getContentType()), fileEntity.getFile()));
            }
        }
        return builder.build();
    }

    private static void setCookies(OkHttpClient.Builder builder, final NetworkRequest networkRequest) {
        builder.cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                Log.v("cyz","save cookie from " + url + " cookies size " + cookies.size());
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                ArrayList<Cookie> cookies = new ArrayList<Cookie>();
                if(networkRequest.cookies == null) {
                    return cookies;
                }

                for(HashMap.Entry<String,String> entry : networkRequest.cookies.entrySet()) {
                    cookies.add(Cookie.parse(url,entry.getKey()+"="+entry.getValue()));
                }

                return cookies;
            }
        });
    }
}
