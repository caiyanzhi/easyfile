package yanzhi.easyfile.easyfile.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 本类用于生成文件和访问文件测试，有两种方案，一种是在所有文件放于一个目录，一种是所有文件都通过hash值来确定一二级目录，
 * 然后放在“散列文件夹”中，分为一级散列和二级散列
 * @desc Created by yanzhi on 2016-01-20.
 */
public class TestUtil {
    /***********************************************************************
     * 一级散列
     * @param dirPath
     */
    public static void testOneRandomPathCreateFile(String dirPath){
        for (int i = 0; i < 31366; i++) {
            String str = "" + i;
            String tmpDir = MathUtils.hashKeyForDisk(str);
            String firstDir = tmpDir.substring(0, 2);
            File file = new File(dirPath + "/" + firstDir);
            try {
                if (!file.exists()) {
                    file.mkdirs();
                }
                File file1 = new File(file.getPath() + "/" + str);
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("cyz", "e = " + e.getMessage() + " filename " + str + " i " + i);
            }
        }
        Log.v("cyz","create 31366 files");
    }

    public static void testOneRandomPathConsume(String dirPath){
        for(int i = 0; i < 31366; i++) {
            String str = "" + i;
            long start = System.currentTimeMillis();
            String tmpDir = MathUtils.hashKeyForDisk(str);
            String firstDir = tmpDir.substring(0, 2);
            File file = new File(dirPath + "/" + firstDir);
            if(file.exists()) {
                long spend = System.currentTimeMillis() - start;
                Log.v("spend", "find file i " + i + " spend " + spend);
            } else {
                long spend = System.currentTimeMillis() - start;
                Log.v("cyz","spend not " + spend+" " + i);
                break;
            }
        }

        Log.v("cyz","finish test OneRandomPathConsume");
    }


    /***********************************************************************
     * 二级散列
     * @param dirPath
     */
    public static void testTwoRandomPathCreateFile(String dirPath){
        Log.v("cyz","begin test TwoRandomPathConsume");
        for (int i = 0; i < 31366; i++) {
            String str = "" + i;
            String tmpDir = MathUtils.hashKeyForDisk(str);
            String firstDir = tmpDir.substring(0, 2);
            String secondDir = tmpDir.substring(2, 4);
            File file = new File(dirPath + "/" + firstDir + "/" + secondDir);
            try {
                if (!file.exists()) {
                    file.mkdirs();
                }

                File file1 = new File(file.getPath() + "/" + str);
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("cyz", "e = " + e.getMessage() + " filename " + str + " i " + i);
            }
        }
        Log.v("cyz","finish test TwoRandomPathConsume");
    }


    public static void testRandomPathConsume(String dirPath){
        Log.v("cyz","start test testRandomPathConsume");
        for(int i = 0; i < 31366; i+=30) {
            String str = "" + i;
            String tmpDir = MathUtils.hashKeyForDisk(str);
            long start = System.currentTimeMillis();
            String firstDir = tmpDir.substring(0,2);
            String secondDir = tmpDir.substring(2,4);

            File file = new File(dirPath+"/" + firstDir + "/" + secondDir,str);
            if(file.exists()) {
                long spend = System.currentTimeMillis() - start;
                Log.v("spend", "" + i + " " + spend);
            } else {
                long spend = System.currentTimeMillis() - start;
                Log.v("cyz","spend not " + spend+" " + i);
                break;
            }
        }
        Log.v("cyz","finish test testRandomPathConsume");
    }

    /***********************************************************************
     * 普通的方法，全部放在一个目录下面
     * @param dirPath
     */
    public static void testNormalDirConsume(String dirPath){
        //为了避免子线程优先级太低被阻塞影响实验结果，所以放在UI线程中访问文件，比较耗时
        for(int i = 0; i < 31366; i+= 1) {
            long start = System.currentTimeMillis();
            File file = new File(dirPath + "/" + i + ".txt");
            if(file.exists()) {
                long spend = System.currentTimeMillis() - start;
                Log.v("spend","find file i "+i +" spend " + spend);
            } else {
                long spend = System.currentTimeMillis() - start;
                Log.v("cyz","spend not " + spend+" " + i);
                break;
            }
        }

        Log.v("cyz","finish test NormalPathConsume");
    }

    public static void testCreateNormalDirFile(final String dirPath){
        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.v("cyz","start testCreateNormalDirFile");
                for(int i = 0; i < 31366; i+= 1) {
                    try {
                        File file = new File(dirPath + "/" + i + ".txt");
                        file.createNewFile();
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

                Log.v("cyz","finish testCreateNormalDirFile");
            }
        }).start();
    }

    public static String appVersionName = null;
    public static String  getAppVersion(Activity activity) {
        if (appVersionName == null) {
            PackageManager manager = activity.getPackageManager();
            try {
                android.content.pm.PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
                appVersionName = info.versionName;   //版本名
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (appVersionName == null) {
            return "";
        } else {
            return appVersionName;
        }
    }
}
