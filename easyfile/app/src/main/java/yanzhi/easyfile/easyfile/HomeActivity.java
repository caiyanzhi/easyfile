package yanzhi.easyfile.easyfile;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import yanzhi.easyfile.easyfile.util.TestUtil;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    @InjectView(R.id.text)
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);
        String dirPath = Environment.getExternalStorageDirectory().getPath() + "/TempTestOne";
        TestUtil.testOneRandomPathConsume(dirPath);
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
