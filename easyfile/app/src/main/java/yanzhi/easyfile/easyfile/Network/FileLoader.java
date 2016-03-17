package yanzhi.easyfile.easyfile.Network;

/**
 * 文件加载器，设计成单例模式
 * @desc Created by yanzhi on 2016-03-18.
 */
public class FileLoader {
    private static FileLoader defaultFileLoader;
    public FileLoader(){}

    public synchronized static FileLoader getInstance(){
        if(defaultFileLoader == null){
            defaultFileLoader = new FileLoader();
        }
        return defaultFileLoader;
    }
}
