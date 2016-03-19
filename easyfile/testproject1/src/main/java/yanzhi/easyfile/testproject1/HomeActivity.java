package yanzhi.easyfile.testproject1;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class HomeActivity extends AppCompatActivity {
    private Button createFile1, testTime1;
    private Button createFile2, testTime2;
    private TextView tip_text;
    private final String NORMALFILEPATH = "NormalDir";
    private final String HASHFILEPATH = "HashDir";
    private final String normalTestTitle = "普通目录策略";
    private final String hashTestTitle = "散列目录策略";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_path);
        createFile1 = (Button) findViewById(R.id.createFile1);
        testTime1 = (Button) findViewById(R.id.testTime1);

        createFile2 = (Button) findViewById(R.id.createFile2);
        testTime2 = (Button) findViewById(R.id.testTime2);

        tip_text = (TextView) findViewById(R.id.tip_info);
        final String path1 = Environment.getExternalStorageDirectory().getPath() + "/" + NORMALFILEPATH;
        final String path2 = Environment.getExternalStorageDirectory().getPath() + "/" + HASHFILEPATH;

        createFile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTips(normalTestTitle);
                TestUtil.testCreateNormalDirFile(path1);
                showEndTips(normalTestTitle);
            }
        });

        testTime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //为了避免子线程优先级太低被阻塞影响实验结果，所以放在UI线程中访问文件，比较耗时
                showStartTips(normalTestTitle);
                TestUtil.testNormalDirConsume(path1);
                showEndTips(normalTestTitle);
            }
        });

        createFile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTips(hashTestTitle);
                TestUtil.testTwoRandomPathCreateFile(path2);
                showEndTips(hashTestTitle);
            }
        });

        testTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //为了避免子线程优先级太低被阻塞影响实验结果，所以放在UI线程中访问文件，比较耗时
                showStartTips(hashTestTitle);
                TestUtil.testTwoRandomPathConsume(path2);
                showEndTips(hashTestTitle);
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

    private void showStartTips(String title){
        tip_text.setText(title+"测试中，可连接adb查看log信息并等待...");
    }

    private void showEndTips(String title){
        tip_text.setText(title+"测试完毕");
    }
}
