package yanzhi.easyfile.radompathtest;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RandomPathActivity extends AppCompatActivity {
    private Button testFileCnt, testTime;
    private TextView textView;
    private final String filePath = "Test";
    private final String randomFilePath = "Test2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_path);
        testFileCnt = (Button) findViewById(R.id.testFileCnt);
        testTime = (Button) findViewById(R.id.testTime);
        textView = (TextView) findViewById(R.id.text);
        final String path = Environment.getExternalStorageDirectory().getPath() + "/" + filePath;


        testFileCnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean error = false;
                        int i= 0;
                        Log.v("cyz","start at " + i);
                        while(!error) {
                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(path + "/" + i++ + ".txt",true);
                                fileOutputStream.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Log.e("cyz", "FileNotFoundException" + e.getMessage());
                                Log.e("cyz", "FileNotFoundException" + i);
                                break;
                            } catch (IOException e) {
                                Log.e("cyz","ioException" + e.getMessage());
                                Log.e("cyz", "FileNotFoundException" + i);
                                break;
                            }
                        }
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText("fail after create " + finalI + " files");
                            }
                        });
                        Log.v("cyz","end at " + i);
                    }
                }).start();
            }
        });

        testTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //为了避免子线程优先级太低被阻塞影响实验结果，所以放在UI线程中访问文件，比较耗时
                for(int i = 0; i < 31366; i+= 1) {
                    long start = System.currentTimeMillis();
                    File file = new File(path + "/" + i + ".txt");
                    if(file.exists()) {
                        long spend = System.currentTimeMillis() - start;
                        Log.v("spend","find file i "+i +" spend " + spend);
                    } else {
                        long spend = System.currentTimeMillis() - start;
                        Log.v("cyz","spend not " + spend+" " + i);
                        break;
                    }
                }
            }
        });
    }


    private void createDir(String path){
        File dirFile = new File(path);
        if(!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File[] files = dirFile.listFiles();
        Log.v("cyz","path " + path + " has " + files.length);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_random_path, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
