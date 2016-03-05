package yanzhi.easyfile.easyfile;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import yanzhi.easyfile.easyfile.Network.DownloadEntity;
import yanzhi.easyfile.easyfile.Network.HttpClientConfig;
import yanzhi.easyfile.easyfile.Network.NetworkManager;
import yanzhi.easyfile.easyfile.Network.NetworkRequest;
import yanzhi.easyfile.easyfile.util.DiskLruCache;
import yanzhi.easyfile.easyfile.util.MathUtils;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    @InjectView(R.id.text)
    TextView textView;
    @InjectView(R.id.download)
    Button downloadBt;
    @InjectView(R.id.upload)
    Button uploadBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);
        String dirPath = Environment.getExternalStorageDirectory().getPath() + "/TempTestOne";
        //TestUtil.testOneRandomPathConsume(dirPath);

        downloadBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DiskLruCache diskLruCache = DiskLruCache.open(
                            new File(Environment.getExternalStorageDirectory().getPath() + "/aMTest"),
                            1,
                            1,
                            HttpClientConfig.DEFAULT_DISK_CACHE_SIZE);
                    DiskLruCache.Editor editor = diskLruCache
                            .edit(MathUtils.hashKeyForDisk("hsfdfsfdas342424hDir"));//picforcnn.jpg
                   //jfinal-1.8-manual.pdf
                    OutputStream out = editor.newOutputStream(0);

                    FileInputStream inputStream = new FileInputStream(Environment.getExternalStorageDirectory().getPath()+"/"+"jfinal-1.8-manual.pdf");
                    int readLen = 0;
                    byte[] buffer = new byte[HttpClientConfig.RECEIVE_BUFF_LEN_INTEGER];

                    while((readLen = inputStream.read(buffer)) != -1) {
                        out.write(buffer,0,readLen);
                        Log.v("cyz","readlen " + readLen);
                    }

                    inputStream.close();

                    out.flush();
                    out.close();

                    editor.commit();
                    diskLruCache.flush();//写journal文件
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        uploadBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NetworkRequest request = new NetworkRequest();
                        request.setHttpMethod(NetworkRequest.HttpMethod.HttpMethod_GET);
//                        DownloadEntity entity = new DownloadEntity(true,0,10000);
                        DownloadEntity entity = new DownloadEntity(true,10000,35329);
                        request.setDownloadEntity(entity);
                        NetworkManager.httpSend(request);
//                        OkHttpClient client = new OkHttpClient();
//                        Request request = new Request.Builder()
//                                .url("http://sdcs.sysu.edu.cn/wp-content/uploads/2016/02/教务部关于选派我校优秀本科生2016学年秋季学期赴加拿大阿尔伯塔大学、新加坡国立大学交流学习的通知.doc")
//                                .build();
//
//                        Log.v("cyz",""+request.url());
//                        try {
//                            Response response = client.newCall(request).execute();
//                            if (response.isSuccessful()) {
//                                Headers responseHeaders = response.headers();
//                                long contentLen = response.body().contentLength();
//                                Log.v("cyz","contentLen " + contentLen);
//                                InputStream inputStream =  response.body().byteStream();
//                                for (int i = 0; i < responseHeaders.size(); i++) {
//                                    Log.v("cyz"," i " + i + " " + responseHeaders.name(i) + ":" + responseHeaders.value(i));
//                                }
//                                byte[] buffer = new byte[HttpClientConfig.RECEIVE_BUFF_LEN_INTEGER];
//
//                                FileOutputStream outputStream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/教务部关于选派我校优秀本科生2016学年秋季学期赴加拿大阿尔伯塔大学、新加坡国立大学交流学习的通知.doc");
//                                int readLen =-1;
//                                int byteLen = 0;
//                                while((readLen = inputStream.read(buffer)) != -1) {
//                                    outputStream.write(buffer,0, readLen);
//                                    byteLen += readLen;
//                                }
//                                Log.v("cyz","byteLen " + byteLen);
//                                outputStream.flush();
//                                outputStream.close();
//                                Log.v("cyz","download ok");
//                            } else {
//                                throw new IOException("Unexpected code " + response);
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//                        final PublishSubject<Integer> stringPublishSubject =
//                    PublishSubject.create();
//            stringPublishSubject.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
//                @Override
//                public void onCompleted() {
//
//                }
//
//                @Override
//                public void onError(Throwable e) {
//
//                }
//
//                @Override
//                public void onNext(Integer s) {
//                    Log.v("cyz", "onNext " + s);
//                    textView.setText("process: " + s);
//                }
//            });
            final Subscription stringPublishSubject = Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    for(int i = 0; i < 100;i ++){
                        subscriber.onNext(i);
                        if( i == 50) {
                            subscriber.unsubscribe();
                        }
                    }
                }
            }).subscribe(new Subscriber<Integer>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Integer s) {
                    Log.v("cyz", "onNext " + s);
                    textView.setText("process: " + s);
                }
            });
            Observable.just("hello world").map(new Func1<String, String>() {
                @Override
                public String call(String s) {
                    return s + " !";
                }
            }).subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(String s) {

                    Log.v("cyz", "onNext " + s);
                }
            });
            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    Log.i(TAG, "create 现在的线程:" + Thread.currentThread().getName());
                    subscriber.onNext("hello world!");
                    subscriber.onCompleted();
                }
            }).map(new Func1<String, String>() {
                @Override
                public String call(String s) {
                    Log.i(TAG, "map 现在的线程:" + Thread.currentThread().getName());
                    return s + "这是尾巴~";
                }
            }).map(new Func1<String, String>() {
                @Override
                public String call(String s) {
                    return s + "这是尾巴~";
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {
                            Log.i(TAG, "onCompleted 现在的线程:" + Thread.currentThread().getName());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(String s) {
                            Log.i(TAG, "onNext 现在的线程:" + Thread.currentThread().getName());
                            Log.i(TAG, "onNext " + s);
                        }
                    });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    String getUrl(){
        return "http://www.baidu.com";
    }
}
